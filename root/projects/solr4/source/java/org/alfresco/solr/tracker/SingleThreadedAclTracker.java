package org.alfresco.solr.tracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.search.impl.lucene.analysis.NumericEncoder;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AclReport;
import org.alfresco.solr.BoundedDeque;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.TrackerState;
import org.alfresco.solr.adapters.IOpenBitSet;
import org.alfresco.solr.client.Acl;
import org.alfresco.solr.client.AclChangeSet;
import org.alfresco.solr.client.AclChangeSets;
import org.alfresco.solr.client.AclReaders;
import org.alfresco.solr.client.GetNodesParameters;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.client.Transactions;
import org.json.JSONException;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single threaded port of tracker code from SOLR 1.4. NOT to be used directly - use
 * the multithreading capable subclass.
 * 
 * The hierarchy maybe flattened out later, or the threadpool code may be moved to
 * a separate multithreaded executor.
 *  
 * @author Matt Ward
 */
public abstract class SingleThreadedAclTracker extends AbstractTracker
{
    protected final static Logger log = LoggerFactory.getLogger(SingleThreadedAclTracker.class);

    private ConcurrentLinkedQueue<Long> aclChangeSetsToReindex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclChangeSetsToIndex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclChangeSetsToPurge = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclsToReindex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclsToIndex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclsToPurge = new ConcurrentLinkedQueue<Long>();


    protected SingleThreadedAclTracker()
    {
        // Used for testing
    }
    
    public SingleThreadedAclTracker(SolrTrackerScheduler scheduler, String id, Properties p, SOLRAPIClient client, 
                String coreName, InformationServer informationServer)
    {
        super(scheduler, id, p, client, coreName, informationServer);
    }


    @Override
    void doTrack() throws Throwable
    {
        // Maintenance stuff
        purgeAclChangeSets();
        purgeAcls();

        reindexAclChangeSets();
        reindexAcls();

        indexAclChangeSets();
        indexAcls();

        trackRepository();
    }

    private void indexAclChangeSets() throws AuthenticationException, IOException, JSONException
    {
        boolean requiresCommit = false;
        while (aclChangeSetsToIndex.peek() != null)
        {
            Long aclChangeSetId = aclChangeSetsToIndex.poll();
            if (aclChangeSetId != null)
            {
                AclChangeSets aclChangeSets = client.getAclChangeSets(null, aclChangeSetId, null, aclChangeSetId+1, 1);
                if ((aclChangeSets.getAclChangeSets().size() > 0) && aclChangeSetId.equals(aclChangeSets.getAclChangeSets().get(0).getId()))
                {
                    AclChangeSet changeSet = aclChangeSets.getAclChangeSets().get(0);
                    List<Acl> acls = client.getAcls(Collections.singletonList(changeSet), null, Integer.MAX_VALUE);
                    for (Acl acl : acls)
                    {
                        List<AclReaders> readers = client.getAclReaders(Collections.singletonList(acl));
                        indexAcl(readers, false);
                    }
                    this.infoSrv.indexAclTransaction(changeSet, false);
                    requiresCommit = true;
                }
            }
            checkShutdown();
        }
        if(requiresCommit)
        {
            checkShutdown();
            this.infoSrv.commit();
        }
    }

    private void indexAcls() throws AuthenticationException, IOException, JSONException
    {
        boolean requiresCommit = false;
        while (aclsToIndex.peek() != null)
        {
            Long aclId = aclsToIndex.poll();
            if (aclId != null)
            {
                Acl acl = new Acl(0, aclId);
                List<AclReaders> readers = client.getAclReaders(Collections.singletonList(acl));
                indexAcl(readers, false);
                requiresCommit = true;
            }
            checkShutdown();
        }
        if(requiresCommit)
        {
            checkShutdown();
            this.infoSrv.commit();
        }
    }

    private void reindexAclChangeSets() throws AuthenticationException, IOException, JSONException
    {
        boolean requiresCommit = false;
        while (aclChangeSetsToReindex.peek() != null)
        {
            Long aclChangeSetId = aclChangeSetsToReindex.poll();
            if (aclChangeSetId != null)
            {
                this.infoSrv.deleteByAclChangeSetId(aclChangeSetId);

                AclChangeSets aclChangeSets = client.getAclChangeSets(null, aclChangeSetId, null, aclChangeSetId+1, 1);
                if ((aclChangeSets.getAclChangeSets().size() > 0) && aclChangeSetId.equals(aclChangeSets.getAclChangeSets().get(0).getId()))
                {
                    AclChangeSet changeSet = aclChangeSets.getAclChangeSets().get(0);
                    List<Acl> acls = client.getAcls(Collections.singletonList(changeSet), null, Integer.MAX_VALUE);
                    for (Acl acl : acls)
                    {
                        List<AclReaders> readers = client.getAclReaders(Collections.singletonList(acl));
                        indexAcl(readers, true);
                    }

                    this.infoSrv.indexAclTransaction(changeSet, true);
                    requiresCommit = true;
                }
            }
            checkShutdown();
        }
        if(requiresCommit)
        {
            checkShutdown();
            this.infoSrv.commit();
        }
    }

    private void reindexAcls() throws AuthenticationException, IOException, JSONException
    {
        boolean requiresCommit = false;
        while (aclsToReindex.peek() != null)
        {
            Long aclId = aclsToReindex.poll();
            if (aclId != null)
            {
                this.infoSrv.deleteByAclId(aclId);

                Acl acl = new Acl(0, aclId);
                List<AclReaders> readers = client.getAclReaders(Collections.singletonList(acl));
                indexAcl(readers, true);
                requiresCommit = true;
            }
            checkShutdown();
        }
        if(requiresCommit)
        {
            checkShutdown();
            this.infoSrv.commit();
        }
    }

    private void purgeAclChangeSets() throws AuthenticationException, IOException, JSONException
    {       
        boolean requiresCommit = false;
        while (aclChangeSetsToPurge.peek() != null)
        {
            Long aclChangeSetId = aclChangeSetsToPurge.poll();
            if (aclChangeSetId != null)
            {
                this.infoSrv.deleteByAclChangeSetId(aclChangeSetId);
                requiresCommit = true;
            }
            checkShutdown();
        }
        if(requiresCommit)
        {
            checkShutdown();
            this.infoSrv.commit();
        }
    }
    
    private void purgeAcls() throws AuthenticationException, IOException, JSONException
    {
        boolean requiresCommit = false;
        while (aclsToPurge.peek() != null)
        {
            Long aclId = aclsToPurge.poll();
            if (aclId != null)
            {
                this.infoSrv.deleteByAclId(aclId);
                requiresCommit = true;
            }
            checkShutdown();
        }
        if(requiresCommit)
        {
            checkShutdown();
            this.infoSrv.commit();
        }
    }


    // ACL change sets

//    @Override
    public void addAclChangeSetToReindex(Long aclChangeSetToReindex)
    {
        aclChangeSetsToReindex.offer(aclChangeSetToReindex);
    }

//    @Override
    public void addAclChangeSetToIndex(Long aclChangeSetToIndex)
    {
        aclChangeSetsToIndex.offer(aclChangeSetToIndex);
    }

//    @Override
    public void addAclChangeSetToPurge(Long aclChangeSetToPurge)
    {
        aclChangeSetsToPurge.offer(aclChangeSetToPurge);
    }

    // ACLs

//    @Override
    public void addAclToReindex(Long aclToReindex)
    {
        aclsToReindex.offer(aclToReindex);
    }

//    @Override
    public void addAclToIndex(Long aclToIndex)
    {
        aclsToIndex.offer(aclToIndex);
    }

//    @Override
    public void addAclToPurge(Long aclToPurge)
    {
        aclsToPurge.offer(aclToPurge);
    }

    
    private void trackRepository() throws IOException, AuthenticationException, JSONException
    {
        // Is the InformationServer ready to update
        int registeredSearcherCount = this.infoSrv.getRegisteredSearcherCount();
        if(registeredSearcherCount >= getMaxLiveSearchers())
        {
            log.info(".... skipping tracking registered searcher count = " + registeredSearcherCount);
            return;
        }

        checkShutdown();
//        trackModels(false);
        
        if(!isMaster && isSlave)
        {
            return;
        }

        TrackerState state = this.infoSrv.getTrackerInitialState();

        // Check we are tracking the correct repository
        // Check the first TX time
        checkRepoAndIndexConsistency(state);

        checkShutdown();
        trackAclChangeSets();

        checkShutdown();
//        trackTransactions();

        // check index state
        if (state.isCheck())
        {
            this.infoSrv.checkCache();
        }
    }


    /**
     * @param reader
     * @throws AuthenticationException
     * @throws IOException
     * @throws JSONException
     */
    protected void trackAclChangeSets() throws AuthenticationException, IOException, JSONException
    {
        boolean aclIndexing = false;
        AclChangeSets aclChangeSets;
        boolean aclsUpToDate = false;
        BoundedDeque<AclChangeSet> changeSetsFound = new  BoundedDeque<AclChangeSet>(100);
        TrackerState state = this.infoSrv.getTrackerState();
        
        do
        {
            Long fromCommitTime =  getChangeSetFromCommitTime(changeSetsFound, state.getLastGoodChangeSetCommitTimeInIndex());
            aclChangeSets = getSomeAclChangeSets(changeSetsFound, fromCommitTime, 60*60*1000L, 2000, state.getTimeToStopIndexing());

            Long maxChangeSetCommitTime = aclChangeSets.getMaxChangeSetCommitTime();
            if(maxChangeSetCommitTime != null)
            {
                state.setLastChangeSetCommitTimeOnServer(aclChangeSets.getMaxChangeSetCommitTime());
            }

            Long maxChangeSetId = aclChangeSets.getMaxChangeSetId();
            if(maxChangeSetId != null)
            {
                state.setLastChangeSetIdOnServer(aclChangeSets.getMaxChangeSetId());
            }

            log.info("Scanning Acl change sets ...");
            if (aclChangeSets.getAclChangeSets().size() > 0)
            {
                log.info(".... from " + aclChangeSets.getAclChangeSets().get(0));
                log.info(".... to " + aclChangeSets.getAclChangeSets().get(aclChangeSets.getAclChangeSets().size() - 1));
            }
            else
            {
                log.info(".... non found after lastTxCommitTime " + fromCommitTime);
            }

            for (AclChangeSet changeSet : aclChangeSets.getAclChangeSets())
            {
                if (!aclIndexing)
                {
                    boolean isInIndex = this.infoSrv.isInIndex(QueryConstants.FIELD_ACLTXID, changeSet.getId());
                    if (isInIndex) 
                    {
                        changeSetsFound.add(changeSet);
                    }
                    else 
                    {
                        aclIndexing = true;
                    }
                }

                if (aclIndexing)
                {
                    // Make sure we do not go ahead of where we started - we will check the holes here
                    // correctly next time
                    if (changeSet.getCommitTimeMs() > state.getTimeToStopIndexing())
                    {
                        aclsUpToDate = true;
                    }
                    else
                    {
                        List<Acl> acls = client.getAcls(Collections.singletonList(changeSet), null, Integer.MAX_VALUE);
                        for (Acl acl : acls)
                        {
                            List<AclReaders> readers = client.getAclReaders(Collections.singletonList(acl));
                            indexAcl(readers, true);
                        }

                        this.infoSrv.indexAclTransaction(changeSet, true);
                        changeSetsFound.add(changeSet);

                        trackerStats.addChangeSetAcls(acls.size());

                        if (changeSet.getCommitTimeMs() > state.getLastIndexedChangeSetCommitTime())
                        {
                            state.setLastIndexedChangeSetCommitTime(changeSet.getCommitTimeMs());
                            state.setLastIndexedChangeSetId(changeSet.getId());
                        }

                    }
                }
                checkShutdown();
            }
        }
        while ((aclChangeSets.getAclChangeSets().size() > 0) && (aclsUpToDate == false));

        if (aclIndexing)
        {
            checkShutdown();
            this.infoSrv.commit();
        }
    }

    private void checkRepoAndIndexConsistency(TrackerState state) throws AuthenticationException, IOException, JSONException
    {
        if(state.getLastGoodTxCommitTimeInIndex() == 0) 
        {

            state.setCheckedFirstTransactionTime(true);
            log.info("No transactions found - no verification required");


            // Fix up inital state
            Transactions firstTransactions = client.getTransactions(null, 0L, null, 2000L, 1);
            if(firstTransactions.getTransactions().size() > 0)
            {
                Transaction firstTransaction = firstTransactions.getTransactions().get(0);
                long firstTransactionCommitTime = firstTransaction.getCommitTimeMs();
                state.setLastGoodTxCommitTimeInIndex(firstTransactionCommitTime);
            }
        }

        if (state.getLastGoodChangeSetCommitTimeInIndex() == 0)
        {
            AclChangeSets firstChangeSets = client.getAclChangeSets(null, 0L, null, 2000L, 1);
            if(firstChangeSets.getAclChangeSets().size() > 0)
            {
                AclChangeSet firstChangeSet = firstChangeSets.getAclChangeSets().get(0);
                long firstChangeSetCommitTimex = firstChangeSet.getCommitTimeMs();
                state.setLastGoodChangeSetCommitTimeInIndex(firstChangeSetCommitTimex);
            }

        }

        if (!state.isCheckedFirstTransactionTime())
        {

            // TODO: getFirstTransaction
            Transactions firstTransactions = client.getTransactions(null, 0L, null, 2000L, 1);
            if (firstTransactions.getTransactions().size() > 0)
            {
                Transaction firstTransaction = firstTransactions.getTransactions().get(0);
                long firstTxId = firstTransaction.getId();
                String targetTxId = NumericEncoder.encode(firstTxId);
                long firstTransactionCommitTime = firstTransaction.getCommitTimeMs();
                String targetTxCommitTime = NumericEncoder.encode(firstTransactionCommitTime);
                int setSize = this.infoSrv.getDocSetSize(targetTxId, targetTxCommitTime);
                
                if (setSize == 0)
                {
                    log.error("First transaction was not found with the correct timestamp.");
                    log.error("SOLR has successfully connected to your repository  however the SOLR indexes and repository database do not match."); 
                    log.error("If this is a new or rebuilt database your SOLR indexes also need to be re-built to match the database."); 
                    log.error("You can also check your SOLR connection details in solrcore.properties.");
                    throw new AlfrescoRuntimeException("Initial transaction not found with correct timestamp");
                }
                else if (setSize == 1)
                {
                    state.setCheckedFirstTransactionTime(true);
                    log.info("Verified first transaction and timetsamp in index");
                }
                else
                {
                    log.warn("Duplicate initial transaction found with correct timestamp");
                }
            }

        }
    }



    /**
     * @param txnsFound
     * @param lastGoodTxCommitTimeInIndex
     * @return
     */
    protected Long getTxFromCommitTime(BoundedDeque<Transaction> txnsFound, long lastGoodTxCommitTimeInIndex)
    {
        if(txnsFound.size() > 0)
        {
            return txnsFound.getLast().getCommitTimeMs();
        }
        else
        {
            return lastGoodTxCommitTimeInIndex;
        }
    }


    /**
     * @param changeSetsFound
     * @param lastGoodChangeSetCommitTimeInIndex
     * @return
     */
    protected Long getChangeSetFromCommitTime(BoundedDeque<AclChangeSet> changeSetsFound, long lastGoodChangeSetCommitTimeInIndex)
    {
        if(changeSetsFound.size() > 0)
        {
            return changeSetsFound.getLast().getCommitTimeMs();
        }
        else
        {
            return lastGoodChangeSetCommitTimeInIndex;
        }
    }

    protected AclChangeSets getSomeAclChangeSets(BoundedDeque<AclChangeSet> changeSetsFound, Long fromCommitTime, long timeStep, int maxResults, long endTime) throws AuthenticationException, IOException, JSONException
    {
        long actualTimeStep  = timeStep;

        AclChangeSets aclChangeSets;
        // step forward in time until we find something or hit the time bound
        // max id unbounded
        Long startTime = fromCommitTime == null ? Long.valueOf(0L) :fromCommitTime;
        do
        {
            aclChangeSets = client.getAclChangeSets(startTime, null, startTime + actualTimeStep, null, maxResults);
            startTime += actualTimeStep;
            actualTimeStep *= 2;
            if(actualTimeStep > 1000*60*60*24*32L)
            {
                actualTimeStep = 1000*60*60*24*32L;
            }
        }
        while( ((aclChangeSets.getAclChangeSets().size() == 0)  && (startTime < endTime)) || ((aclChangeSets.getAclChangeSets().size() > 0) && alreadyFoundChangeSets(changeSetsFound, aclChangeSets)));

        return aclChangeSets;

    }

    private boolean alreadyFoundChangeSets(BoundedDeque<AclChangeSet> changeSetsFound, AclChangeSets aclChangeSets)
    {
        if(changeSetsFound.size() == 0)
        {
            return false;
        }

        if(aclChangeSets.getAclChangeSets().size() == 1)
        {
            return aclChangeSets.getAclChangeSets().get(0).getId() == changeSetsFound.getLast().getId();
        }
        else
        {
            HashSet<AclChangeSet> alreadyFound = new HashSet<AclChangeSet>(changeSetsFound.getDeque());
            for(AclChangeSet aclChangeSet : aclChangeSets.getAclChangeSets())
            {
                if(!alreadyFound.contains(aclChangeSet))
                {
                    return false;
                }
            }
            return true;
        }

    }


    protected Transactions getSomeTransactions(BoundedDeque<Transaction> txnsFound, Long fromCommitTime, long timeStep, int maxResults, long endTime) throws AuthenticationException, IOException, JSONException
    {
        long actualTimeStep  = timeStep;

        Transactions transactions;
        // step forward in time until we find something or hit the time bound
        // max id unbounded
        Long startTime = fromCommitTime == null ? Long.valueOf(0L) :fromCommitTime;
        do
        {
            transactions = client.getTransactions(startTime, null, startTime + actualTimeStep, null, maxResults); 
            startTime += actualTimeStep;
            actualTimeStep *= 2;
            if(actualTimeStep > 1000*60*60*24*32L)
            {
                actualTimeStep = 1000*60*60*24*32L;
            }
        }
        while( ((transactions.getTransactions().size() == 0)  && (startTime < endTime)) || ((transactions.getTransactions().size() > 0)  && alreadyFoundTransactions(txnsFound, transactions)));

        return transactions;
    }

    private boolean alreadyFoundTransactions(BoundedDeque<Transaction> txnsFound, Transactions transactions)
    {
        if(txnsFound.size() == 0)
        {
            return false;
        }

        if(transactions.getTransactions().size() == 1)
        {
            return transactions.getTransactions().get(0).getId() == txnsFound.getLast().getId();
        }
        else
        {
            HashSet<Transaction> alreadyFound = new HashSet<Transaction>(txnsFound.getDeque());
            for(Transaction txn : transactions.getTransactions())
            {
                if(!alreadyFound.contains(txn))
                {
                    return false;
                }
            }
            return true;
        }

    }



    QName expandQName(String qName)
    {
        String expandedQName = qName;
        if (qName.startsWith("@"))
        {
            return expandQName(qName.substring(1));
        }
        else if (qName.startsWith("{"))
        {
            expandedQName = expandQNameImpl(qName);
        }
        else if (qName.contains(":"))
        {
            expandedQName = expandQNameImpl(qName);
        }
        // TODO: field has gone, check correct course of action
//        else if (AlfrescoSolrDataModel.nonDictionaryFields.get(qName) == null)
//        {
//            expandedQName = expandQNameImpl(qName);
//        }
        return QName.createQName(expandedQName);

    }

    String expandQNameImpl(String q)
    {
        String eq = q;
        // Check for any prefixes and expand to the full uri
        if (q.charAt(0) != '{')
        {
            int colonPosition = q.indexOf(':');
            if (colonPosition == -1)
            {
                // use the default namespace
                eq = "{" + this.infoSrv.getNamespaceDAO().getNamespaceURI("") + "}" + q;
            }
            else
            {
                // find the prefix
                eq = "{" + this.infoSrv.getNamespaceDAO().getNamespaceURI(q.substring(0, colonPosition)) + "}" + q.substring(colonPosition + 1);
            }
        }
        return eq;
    }

    String expandName(String qName)
    {
        String expandedQName = qName;
        if (qName.startsWith("@"))
        {
            return expandName(qName.substring(1));
        }
        else if (qName.startsWith("{"))
        {
            expandedQName = expandQNameImpl(qName);
        }
        else if (qName.contains(":"))
        {
            expandedQName = expandQNameImpl(qName);
        }
     // TODO: field has gone, check correct course of action
//        else if (AlfrescoSolrDataModel.nonDictionaryFields.get(qName) == null)
//        {
//            expandedQName = expandQNameImpl(qName);
//        }
        return expandedQName;

    }

    String expandNameImpl(String q)
    {
        String eq = q;
        // Check for any prefixes and expand to the full uri
        if (q.charAt(0) != '{')
        {
            int colonPosition = q.indexOf(':');
            if (colonPosition == -1)
            {
                // use the default namespace
                eq = "{" + this.infoSrv.getNamespaceDAO().getNamespaceURI("") + "}" + q;
            }
            else
            {
                // find the prefix
                eq = "{" + this.infoSrv.getNamespaceDAO().getNamespaceURI(q.substring(0, colonPosition)) + "}" + q.substring(colonPosition + 1);
            }
        }
        return eq;
    }

    /**
     * @param acl
     * @param readers
     */
    protected void indexAcl(List<AclReaders> aclReaderList, boolean overwrite) throws IOException
    {
        long time = this.infoSrv.indexAcl(aclReaderList, overwrite);
        trackerStats.addAclTime(time);
    }

    @Override
    public IndexHealthReport checkIndex(Long fromTx, Long toTx, Long fromAclTx, Long toAclTx, Long fromTime, Long toTime) 
                throws AuthenticationException, IOException, JSONException
    {

        IndexHealthReport indexHealthReport = new IndexHealthReport(infoSrv);
        Long minTxId = null;
        Long minAclTxId = null;

        long firstTransactionCommitTime = 0;
        Transactions firstTransactions = client.getTransactions(null, 0L, null, 2000L, 1);
        if(firstTransactions.getTransactions().size() > 0)
        {
            Transaction firstTransaction = firstTransactions.getTransactions().get(0);
            firstTransactionCommitTime = firstTransaction.getCommitTimeMs();
        }

        // DB TX Count
        IOpenBitSet txIdsInDb = infoSrv.getOpenBitSetInstance();
        Long lastTxCommitTime = Long.valueOf(firstTransactionCommitTime);
        if (fromTime != null)
        {
            lastTxCommitTime = fromTime;
        }
        long maxTxId = 0;

        Transactions transactions;
        BoundedDeque<Transaction> txnsFound = new  BoundedDeque<Transaction>(100);
        long endTime = System.currentTimeMillis() + infoSrv.getHoleRetention();
        DO: do
        {
            transactions = getSomeTransactions(txnsFound, lastTxCommitTime, 1000*60*60L, 2000, endTime);
            for (Transaction info : transactions.getTransactions())
            {
                // include
                if (toTime != null)
                {
                    if (info.getCommitTimeMs() > toTime.longValue())
                    {
                        break DO;
                    }
                }
                if (toTx != null)
                {
                    if (info.getId() > toTx.longValue())
                    {
                        break DO;
                    }
                }

                // bounds for later loops
                if (minTxId == null)
                {
                    minTxId = info.getId();
                }
                if (maxTxId < info.getId())
                {
                    maxTxId = info.getId();
                }

                lastTxCommitTime = info.getCommitTimeMs();
                txIdsInDb.set(info.getId());
                txnsFound.add(info);
            }
        }
        while (transactions.getTransactions().size() > 0);

        indexHealthReport.setDbTransactionCount(txIdsInDb.cardinality());

        // DB ACL TX Count

        long firstChangeSetCommitTimex = 0;
        AclChangeSets firstChangeSets = client.getAclChangeSets(null, 0L, null, 2000L, 1);
        if(firstChangeSets.getAclChangeSets().size() > 0)
        {
            AclChangeSet firstChangeSet = firstChangeSets.getAclChangeSets().get(0);
            firstChangeSetCommitTimex = firstChangeSet.getCommitTimeMs();
        }

        IOpenBitSet aclTxIdsInDb = infoSrv.getOpenBitSetInstance();
        Long lastAclTxCommitTime = Long.valueOf(firstChangeSetCommitTimex);
        if (fromTime != null)
        {
            lastAclTxCommitTime = fromTime;
        }
        long maxAclTxId = 0;

        AclChangeSets aclTransactions;
        BoundedDeque<AclChangeSet> changeSetsFound = new  BoundedDeque<AclChangeSet>(100);
        DO: do
        {
            aclTransactions = getSomeAclChangeSets(changeSetsFound, lastAclTxCommitTime, 1000*60*60L, 2000, endTime);
            for (AclChangeSet set : aclTransactions.getAclChangeSets())
            {
                // include
                if (toTime != null)
                {
                    if (set.getCommitTimeMs() > toTime.longValue())
                    {
                        break DO;
                    }
                }
                if (toAclTx != null)
                {
                    if (set.getId() > toAclTx.longValue())
                    {
                        break DO;
                    }
                }

                // bounds for later loops
                if (minAclTxId == null)
                {
                    minAclTxId = set.getId();
                }
                if (maxAclTxId < set.getId())
                {
                    maxAclTxId = set.getId();
                }

                lastAclTxCommitTime = set.getCommitTimeMs();
                aclTxIdsInDb.set(set.getId());
                changeSetsFound.add(set);
            }
        }
        while (aclTransactions.getAclChangeSets().size() > 0);

        indexHealthReport.setDbAclTransactionCount(aclTxIdsInDb.cardinality());

        // Index TX Count
        return this.infoSrv.checkIndexTransactions(indexHealthReport, minTxId, minAclTxId, txIdsInDb, maxTxId, 
                    aclTxIdsInDb, maxAclTxId);
    }


//    @Override
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



    /**
     * @param acltxid
     * @return
     */
//    @Override
    public List<Long> getAclsForDbAclTransaction(Long acltxid)
    {
        try
        {
            ArrayList<Long> answer = new ArrayList<Long>();
            AclChangeSets changeSet = client.getAclChangeSets(null, acltxid, null, acltxid+1, 1);
            List<Acl> acls = client.getAcls(changeSet.getAclChangeSets(), null, Integer.MAX_VALUE);
            for (Acl acl : acls)
            {
                answer.add(acl.getId());
            }
            return answer;
        }
        catch (IOException e)
        {
            throw new AlfrescoRuntimeException("Failed to get acls", e);
        }
        catch (JSONException e)
        {
            throw new AlfrescoRuntimeException("Failed to get acls", e);
        }
        catch (AuthenticationException e)
        {
            throw new AlfrescoRuntimeException("Failed to get acls", e);
        }
    }

//    @Override
    public AclReport checkAcl(Long aclid)
    {
        AclReport aclReport = new AclReport();
        aclReport.setAclId(aclid);

        // In DB

        try
        {
            List<AclReaders> readers = client.getAclReaders(Collections.singletonList(new Acl(0, aclid)));
            aclReport.setExistsInDb(readers.size() == 1);
        }
        catch (IOException e)
        {
            aclReport.setExistsInDb(false);
        }
        catch (JSONException e)
        {
            aclReport.setExistsInDb(false);
        }
        catch (AuthenticationException e)
        {
            aclReport.setExistsInDb(false);
        }

        // In Index
        return this.infoSrv.checkAclInIndex(aclid, aclReport);
    }
}
