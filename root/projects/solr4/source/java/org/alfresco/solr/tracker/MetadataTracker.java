/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.solr.tracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.solr.BoundedDeque;
import org.alfresco.solr.NodeReport;
import org.alfresco.solr.TrackerState;
import org.alfresco.solr.adapters.IOpenBitSet;
import org.alfresco.solr.client.GetNodesParameters;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.client.Transactions;
import org.alfresco.solr.client.Node.SolrApiNodeStatus;
import org.json.JSONException;

public class MetadataTracker extends AbstractTracker implements Tracker
{
    private ConcurrentLinkedQueue<Long> transactionsToReindex = new ConcurrentLinkedQueue<Long>();
    private ConcurrentLinkedQueue<Long> transactionsToIndex = new ConcurrentLinkedQueue<Long>();
    private ConcurrentLinkedQueue<Long> transactionsToPurge = new ConcurrentLinkedQueue<Long>();
    private ConcurrentLinkedQueue<Long> nodesToReindex = new ConcurrentLinkedQueue<Long>();
    private ConcurrentLinkedQueue<Long> nodesToIndex = new ConcurrentLinkedQueue<Long>();
    private ConcurrentLinkedQueue<Long> nodesToPurge = new ConcurrentLinkedQueue<Long>();

    @Override
    protected void doTrack() throws AuthenticationException, IOException, JSONException
    {

        checkShutdown();
        trackTransactions();
    }

    protected Long getTxFromCommitTime(BoundedDeque<Transaction> txnsFound, long lastGoodTxCommitTimeInIndex)
    {
        if (txnsFound.size() > 0)
        {
            return txnsFound.getLast().getCommitTimeMs();
        }
        else
        {
            return lastGoodTxCommitTimeInIndex;
        }
    }

    private boolean alreadyFoundTransactions(BoundedDeque<Transaction> txnsFound, Transactions transactions)
    {
        if (txnsFound.size() == 0) { return false; }

        if (transactions.getTransactions().size() == 1)
        {
            return transactions.getTransactions().get(0).getId() == txnsFound.getLast().getId();
        }
        else
        {
            HashSet<Transaction> alreadyFound = new HashSet<Transaction>(txnsFound.getDeque());
            for (Transaction txn : transactions.getTransactions())
            {
                if (!alreadyFound.contains(txn)) { return false; }
            }
            return true;
        }
    }

    protected Transactions getSomeTransactions(BoundedDeque<Transaction> txnsFound, Long fromCommitTime, long timeStep,
                int maxResults, long endTime) throws AuthenticationException, IOException, JSONException
    {
        long actualTimeStep = timeStep;

        Transactions transactions;
        // step forward in time until we find something or hit the time bound
        // max id unbounded
        Long startTime = fromCommitTime == null ? Long.valueOf(0L) : fromCommitTime;
        do
        {
            transactions = client.getTransactions(startTime, null, startTime + actualTimeStep, null, maxResults);
            startTime += actualTimeStep;
            actualTimeStep *= 2;
            if (actualTimeStep > 1000 * 60 * 60 * 24 * 32L)
            {
                actualTimeStep = 1000 * 60 * 60 * 24 * 32L;
            }
        } while (((transactions.getTransactions().size() == 0) && (startTime < endTime))
                    || ((transactions.getTransactions().size() > 0) && alreadyFoundTransactions(txnsFound, transactions)));

        return transactions;
    }

    protected void trackTransactions() throws AuthenticationException, IOException, JSONException
    {
        boolean indexed = false;
        boolean upToDate = false;
        Transactions transactions;
        BoundedDeque<Transaction> txnsFound = new BoundedDeque<Transaction>(100);
        TrackerState state = this.infoSrv.getTrackerState();

        do
        {
            int docCount = 0;

            Long fromCommitTime = getTxFromCommitTime(txnsFound, state.getLastGoodTxCommitTimeInIndex());
            transactions = getSomeTransactions(txnsFound, fromCommitTime, 60 * 60 * 1000L, 2000,
                        state.getTimeToStopIndexing());

            Long maxTxnCommitTime = transactions.getMaxTxnCommitTime();
            if (maxTxnCommitTime != null)

            {
                state.setLastTxCommitTimeOnServer(transactions.getMaxTxnCommitTime());
            }

            Long maxTxnId = transactions.getMaxTxnId();
            if (maxTxnId != null)
            {
                state.setLastTxIdOnServer(transactions.getMaxTxnId());
            }

            log.info("Scanning transactions ...");
            if (transactions.getTransactions().size() > 0)
            {
                log.info(".... from " + transactions.getTransactions().get(0));
                log.info(".... to " + transactions.getTransactions().get(transactions.getTransactions().size() - 1));
            }
            else
            {
                log.info(".... non found after lastTxCommitTime "
                            + ((txnsFound.size() > 0) ? txnsFound.getLast().getCommitTimeMs() : state
                                        .getLastIndexedTxCommitTime()));
            }
            for (Transaction info : transactions.getTransactions())
            {
                boolean isInIndex = this.infoSrv.isInIndex(QueryConstants.FIELD_TXID, info.getId());
                if (isInIndex)
                {
                    txnsFound.add(info);
                }
                else
                {
                    // Make sure we do not go ahead of where we started - we will check the holes here
                    // correctly next time
                    if (info.getCommitTimeMs() > state.getTimeToStopIndexing())
                    {
                        upToDate = true;
                    }
                    else
                    {
                        indexed = true;

                        GetNodesParameters gnp = new GetNodesParameters();
                        ArrayList<Long> txs = new ArrayList<Long>();
                        txs.add(info.getId());
                        gnp.setTransactionIds(txs);
                        gnp.setStoreProtocol(storeRef.getProtocol());
                        gnp.setStoreIdentifier(storeRef.getIdentifier());
                        List<Node> nodes = client.getNodes(gnp, Integer.MAX_VALUE);
                        for (Node node : nodes)
                        {
                            docCount++;
                            if (log.isDebugEnabled())
                            {
                                log.debug(node.toString());
                            }

                            this.infoSrv.indexNode(node, true);

                            checkShutdown();
                        }

                        // Index the transaction doc after the node - if this is not found then a reindex will be
                        // done.
                        this.infoSrv.indexTransaction(info, true);

                        trackerStats.addTxDocs(nodes.size());

                        if (info.getCommitTimeMs() > state.getLastIndexedTxCommitTime())
                        {
                            state.setLastIndexedTxCommitTime(info.getCommitTimeMs());
                            state.setLastIndexedTxId(info.getId());
                        }

                        txnsFound.add(info);
                    }
                }
                // could batch commit here
                if (docCount > batchCount)
                {
                    if (this.infoSrv.getRegisteredSearcherCount() < getMaxLiveSearchers())
                    {
                        checkShutdown();
                        this.infoSrv.commit();
                        docCount = 0;
                    }
                }
                checkShutdown();
            }
        } while ((transactions.getTransactions().size() > 0) && (upToDate == false));

        if (indexed)
        {
            checkShutdown();
            this.infoSrv.commit();
        }
    }

    public NodeReport checkNode(Long dbid)
    {
        NodeReport nodeReport = new NodeReport();
        nodeReport.setDbid(dbid);

        // In DB

        GetNodesParameters parameters = new GetNodesParameters();
        parameters.setFromNodeId(dbid);
        parameters.setToNodeId(dbid);
        List<Node> dbnodes;
        try
        {
            dbnodes = client.getNodes(parameters, 1);
            if (dbnodes.size() == 1)
            {
                Node dbnode = dbnodes.get(0);
                nodeReport.setDbNodeStatus(dbnode.getStatus());
                nodeReport.setDbTx(dbnode.getTxnId());
            }
            else
            {
                nodeReport.setDbNodeStatus(SolrApiNodeStatus.UNKNOWN);
                nodeReport.setDbTx(-1l);
            }
        }
        catch (IOException e)
        {
            nodeReport.setDbNodeStatus(SolrApiNodeStatus.UNKNOWN);
            nodeReport.setDbTx(-2l);
        }
        catch (JSONException e)
        {
            nodeReport.setDbNodeStatus(SolrApiNodeStatus.UNKNOWN);
            nodeReport.setDbTx(-3l);
        }
        catch (AuthenticationException e1)
        {
            nodeReport.setDbNodeStatus(SolrApiNodeStatus.UNKNOWN);
            nodeReport.setDbTx(-4l);
        }

        this.infoSrv.checkNodeCommon(nodeReport);

        return nodeReport;
    }

    public NodeReport checkNode(Node node)
    {
        NodeReport nodeReport = new NodeReport();
        nodeReport.setDbid(node.getId());

        nodeReport.setDbNodeStatus(node.getStatus());
        nodeReport.setDbTx(node.getTxnId());

        this.infoSrv.checkNodeCommon(nodeReport);

        return nodeReport;
    }

    public List<Node> getFullNodesForDbTransaction(Long txid)
    {
        try
        {
            GetNodesParameters gnp = new GetNodesParameters();
            ArrayList<Long> txs = new ArrayList<Long>();
            txs.add(txid);
            gnp.setTransactionIds(txs);
            gnp.setStoreProtocol(storeRef.getProtocol());
            gnp.setStoreIdentifier(storeRef.getIdentifier());
            return client.getNodes(gnp, Integer.MAX_VALUE);
        }
        catch (IOException e)
        {
            throw new AlfrescoRuntimeException("Failed to get nodes", e);
        }
        catch (JSONException e)
        {
            throw new AlfrescoRuntimeException("Failed to get nodes", e);
        }
        catch (AuthenticationException e)
        {
            throw new AlfrescoRuntimeException("Failed to get nodes", e);
        }
    }

    @Override
    public IndexHealthReport checkIndex(Long fromTx, Long toTx, Long fromAclTx, Long toAclTx, Long fromTime, Long toTime)
                throws IOException, AuthenticationException, JSONException
    {
        IndexHealthReport indexHealthReport = new IndexHealthReport(infoSrv);
        Long minTxId = null;
        Long minAclTxId = null;

        // DB TX Count
        IOpenBitSet txIdsInDb = infoSrv.getOpenBitSetInstance();
        long maxTxId = 0;

        indexHealthReport.setDbTransactionCount(txIdsInDb.cardinality());

        IOpenBitSet aclTxIdsInDb = infoSrv.getOpenBitSetInstance();
        long maxAclTxId = 0;

        indexHealthReport.setDbAclTransactionCount(aclTxIdsInDb.cardinality());

        // Index TX Count
        return this.infoSrv.checkIndexTransactions(indexHealthReport, minTxId, minAclTxId, txIdsInDb, maxTxId,
                    aclTxIdsInDb, maxAclTxId);
    }

    public void addTransactionToPurge(Long txId)
    {
        this.transactionsToPurge.offer(txId);
    }

    public void addNodeToPurge(Long nodeId)
    {
        this.nodesToPurge.offer(nodeId);
    }

    public void addTransactionToReindex(Long txId)
    {
        this.transactionsToReindex.offer(txId);
    }

    public void addNodeToReindex(Long nodeId)
    {
        nodesToReindex.offer(nodeId);
    }

    public void addTransactionToIndex(Long txId)
    {
        transactionsToIndex.offer(txId);
    }

    public void addNodeToIndex(Long nodeId)
    {
        this.nodesToIndex.offer(nodeId);
    }

}
