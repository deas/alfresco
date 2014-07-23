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

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.encryption.KeyStoreParameters;
import org.alfresco.encryption.ssl.SSLEncryptionParameters;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.httpclient.AlfrescoHttpClient;
import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.httpclient.HttpClientFactory.SecureCommsType;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.ModelDefinition.XMLBindingType;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AclReport;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.BoundedDeque;
import org.alfresco.solr.IndexTrackingShutdownException;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.NodeReport;
import org.alfresco.solr.SolrKeyResourceLoader;
import org.alfresco.solr.TrackerState;
import org.alfresco.solr.adapters.IOpenBitSet;
import org.alfresco.solr.client.Acl;
import org.alfresco.solr.client.AclChangeSet;
import org.alfresco.solr.client.AclChangeSets;
import org.alfresco.solr.client.AclReaders;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.client.AlfrescoModelDiff;
import org.alfresco.solr.client.GetNodesParameters;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.Node.SolrApiNodeStatus;
import org.alfresco.solr.client.NodeMetaData;
import org.alfresco.solr.client.NodeMetaDataParameters;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.SOLRAPIClient.GetTextContentResponse;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.client.Transactions;
import org.alfresco.util.NumericEncoder;
import org.json.JSONException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreTracker implements Tracker 
{
    protected final static Logger log = LoggerFactory.getLogger(CoreTracker.class);

    protected SOLRAPIClient client;
    protected Properties props;
    protected InformationServer infoSrv;

    private String coreName;

    private String alfrescoHost;

    private int alfrescoPort;

    private int alfrescoPortSSL;

    private String baseUrl;

    private String cron;

    protected StoreRef storeRef;
    protected long batchCount;

    // encryption related parameters
    private String secureCommsType; // "none", "https"

    private String keyStoreType;

    private String keyStoreProvider;

    private String passwordFileLocation;

    private String keyStoreLocation;

    // ssl
    private String sslKeyStoreType;

    private String sslKeyStoreProvider;

    private String sslKeyStoreLocation;

    private String sslKeyStorePasswordFileLocation;

    private String sslTrustStoreType;

    private String sslTrustStoreProvider;

    private String sslTrustStoreLocation;

    private String sslTrustStorePasswordFileLocation;

    private boolean isSlave = false;
    
    private boolean isMaster = true;
    
    private String alfrescoVersion;

    // index contrl

    // http client

    private int maxTotalConnections = 40;

    private int maxHostConnections = 40;

    private int socketTimeout = 120000;

    private String id;

    private ConcurrentLinkedQueue<Long> transactionsToReindex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> transactionsToIndex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> transactionsToPurge = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclChangeSetsToReindex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclChangeSetsToIndex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclChangeSetsToPurge = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> nodesToReindex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> nodesToIndex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> nodesToPurge = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclsToReindex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclsToIndex = new ConcurrentLinkedQueue<Long>();

    private ConcurrentLinkedQueue<Long> aclsToPurge = new ConcurrentLinkedQueue<Long>();

    protected TrackerStats trackerStats;

    //

    private boolean runPostModelLoadInit = true;

    private HashSet<StoreRef> indexedStores = new  HashSet<StoreRef>();

    private HashSet<String> indexedTenants = new  HashSet<String>();

    private HashSet<QName> indexedDataTypes = new  HashSet<QName>();

    private HashSet<QName> indexedTypes = new  HashSet<QName>();

    private HashSet<QName> indexedAspects = new  HashSet<QName>();

    private HashSet<String> indexedFields = new  HashSet<String>();

    //

    private HashSet<StoreRef> ignoredStores = new  HashSet<StoreRef>();

    private HashSet<String> ignoredTenants = new  HashSet<String>();

    private HashSet<QName> ignoredDataTypes = new  HashSet<QName>();

    private HashSet<QName> ignoredTypes = new  HashSet<QName>();

    private HashSet<QName> ignoredAspects = new  HashSet<QName>();

    private HashSet<String> ignoredFields = new  HashSet<String>();

    private int maxLiveSearchers;

    //

    private ReentrantReadWriteLock modelLock = new ReentrantReadWriteLock();
    
    boolean hasModels = false;
    
    //
    private volatile boolean shutdown = false;

    @Override
    public TrackerStats getTrackerStats()
    {
        return trackerStats;
    }

    @Override
    public int getMaxLiveSearchers()
    {
        return maxLiveSearchers;
    }

    public CoreTracker(Scheduler scheduler, String id, Properties p, SolrKeyResourceLoader keyResourceLoader, 
                String coreName, InformationServer informationServer)
    {
        this.id = id;
        this.props = p;
        this.coreName = coreName;
        this.infoSrv = informationServer;

        alfrescoHost = p.getProperty("alfresco.host", "localhost");
        alfrescoPort = Integer.parseInt(p.getProperty("alfresco.port", "8080"));
        alfrescoPortSSL = Integer.parseInt(p.getProperty("alfresco.port.ssl", "8443"));
        baseUrl = p.getProperty("alfresco.baseUrl", "/alfresco");
        cron =  p.getProperty("alfresco.cron", "0/15 * * * * ? *");
        storeRef = new StoreRef(p.getProperty("alfresco.stores"));
        batchCount = Integer.parseInt(p.getProperty("alfresco.batch.count", "1000"));
        keyStoreType = p.getProperty("alfresco.encryption.keystore.type", "JCEKS");
        keyStoreProvider = p.getProperty("alfresco.encryption.keystore.provider");
        passwordFileLocation = p.getProperty("alfresco.encryption.keystore.passwordFileLocation");
        keyStoreLocation = p.getProperty("alfresco.encryption.keystore.location");
        sslKeyStoreType = p.getProperty("alfresco.encryption.ssl.keystore.type");
        sslKeyStoreProvider = p.getProperty("alfresco.encryption.ssl.keystore.provider", "");
        sslKeyStoreLocation = p.getProperty("alfresco.encryption.ssl.keystore.location", "ssl.repo.client.keystore");
        sslKeyStorePasswordFileLocation = p.getProperty("alfresco.encryption.ssl.keystore.passwordFileLocation", "ssl-keystore-passwords.properties");
        sslTrustStoreType = p.getProperty("alfresco.encryption.ssl.truststore.type", "JCEKS");
        sslTrustStoreProvider = p.getProperty("alfresco.encryption.ssl.truststore.provider", "");
        sslTrustStoreLocation = p.getProperty("alfresco.encryption.ssl.truststore.location", "ssl.repo.client.truststore");
        sslTrustStorePasswordFileLocation = p.getProperty("alfresco.encryption.ssl.truststore.passwordFileLocation", "ssl-truststore-passwords.properties");
        secureCommsType = p.getProperty("alfresco.secureComms", "https");
        maxTotalConnections = Integer.parseInt(p.getProperty("alfresco.maxTotalConnections", "40"));
        maxHostConnections = Integer.parseInt(p.getProperty("alfresco.maxHostConnections", "40"));
        socketTimeout = Integer.parseInt(p.getProperty("alfresco.socketTimeout", "0"));
        maxLiveSearchers =  Integer.parseInt(p.getProperty("alfresco.maxLiveSearchers", "2"));
        isSlave =  Boolean.parseBoolean(p.getProperty("enable.slave", "false"));
        isMaster =  Boolean.parseBoolean(p.getProperty("enable.master", "true"));

        this.trackerStats = new TrackerStats(this.infoSrv);
        
        alfrescoVersion = p.getProperty("alfresco.version", "4.2.2");

        client = new SOLRAPIClient(getRepoClient(keyResourceLoader), 
                    this.infoSrv.getDictionaryService(CMISStrictDictionaryService.DEFAULT), 
                    this.infoSrv.getNamespaceDAO());
        initCoreTrackerJob(scheduler);
    }

    private void initCoreTrackerJob(Scheduler scheduler)
    {
        JobDetail job = new JobDetail("CoreTracker-" + coreName, "Solr", CoreTrackerJob.class);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("TRACKER", this);
        job.setJobDataMap(jobDataMap);
        Trigger trigger;
        try
        {
            trigger = new CronTrigger("CoreTrackerTrigger" + coreName, "Solr", cron);
            scheduler.scheduleJob(job, trigger);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (SchedulerException e)
        {
            e.printStackTrace();
        }
        log.info("Solr built for Alfresco version: " + alfrescoVersion);
    }

    protected AlfrescoHttpClient getRepoClient(SolrKeyResourceLoader keyResourceLoader)
    {
        // TODO i18n
        KeyStoreParameters keyStoreParameters = new KeyStoreParameters("SSL Key Store", sslKeyStoreType, sslKeyStoreProvider, sslKeyStorePasswordFileLocation, sslKeyStoreLocation);
        KeyStoreParameters trustStoreParameters = new KeyStoreParameters("SSL Trust Store", sslTrustStoreType, sslTrustStoreProvider, sslTrustStorePasswordFileLocation, sslTrustStoreLocation);
        SSLEncryptionParameters sslEncryptionParameters = new SSLEncryptionParameters(keyStoreParameters, trustStoreParameters);
        
        HttpClientFactory httpClientFactory = new HttpClientFactory(SecureCommsType.getType(secureCommsType),
                sslEncryptionParameters, keyResourceLoader, null, null, alfrescoHost, alfrescoPort, alfrescoPortSSL, maxTotalConnections, maxHostConnections, socketTimeout);
        // TODO need to make port configurable depending on secure comms, or just make redirects
        // work
        AlfrescoHttpClient repoClient = httpClientFactory.getRepoClient(alfrescoHost, alfrescoPortSSL);
        repoClient.setBaseUrl(baseUrl);
        return repoClient;
    }

    @Override
    public void updateIndex()
    {
        TrackerState state = this.infoSrv.getTrackerState();

        synchronized (this) 
        {
            if (state.isRunning())
            {
                log.info("... update for " + coreName + " is already running");
                return;
            }
            else
            {
                log.info("... updating " + coreName);
                state.setRunning(true);
            }
        }
        try
        {
            // Maintenance stuff
            purgeAclChangeSets();
            purgeAcls();
            purgeTransactions();
            purgeNodes();

            reindexAclChangeSets();
            reindexAcls();
            reindexTransactions();
            reindexNodes();

            indexAclChangeSets();
            indexAcls();
            indexTransactions();
            indexNodes();

            trackRepository();
        }
        catch(IndexTrackingShutdownException t)
        {
            try
            {
                this.infoSrv.rollback();
            }
            catch (IOException e)
            {
                log.error("Failed to roll back pending work on error", t);
            }
            log.info("Stopping index tracking for "+coreName);
        }
        catch(Throwable t)
        {
            try
            {
                this.infoSrv.rollback();
            }
            catch (IOException e)
            {
                log.error("Failed to roll back pending work on error", t);
            }
            if (t instanceof SocketTimeoutException)
            {
                if (log.isDebugEnabled())
                {
                    // DEBUG, so give the whole stack trace
                    log.warn("Tracking communication timed out.", t);
                }
                else
                {
                    // We don't need the stack trace.  It timed out.
                    log.warn("Tracking communication timed out.");
                }
            }
            else
            {
                log.error("Tracking failed", t);
            }
        }
        finally
        {
            state.setRunning(false);
            state.setCheck(false);
        }
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

    protected void checkShutdown()
    {
        if(shutdown)
        {
            throw new IndexTrackingShutdownException();
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

    // TX

    @Override
    public void addTransactionToReindex(Long transactionToReindex)
    {
        transactionsToReindex.offer(transactionToReindex);
    }

    @Override
    public void addTransactionToIndex(Long transactionToIndex)
    {
        transactionsToIndex.offer(transactionToIndex);
    }

    @Override
    public void addTransactionToPurge(Long transactionToPurge)
    {
        transactionsToPurge.offer(transactionToPurge);
    }

    // Node

    @Override
    public void addNodeToReindex(Long nodeToReindex)
    {
        nodesToReindex.offer(nodeToReindex);
    }

    @Override
    public void addNodeToIndex(Long nodeToIndex)
    {
        nodesToIndex.offer(nodeToIndex);
    }

    @Override
    public void addNodeToPurge(Long nodeToPurge)
    {
        nodesToPurge.offer(nodeToPurge);
    }

    // ACL change sets

    @Override
    public void addAclChangeSetToReindex(Long aclChangeSetToReindex)
    {
        aclChangeSetsToReindex.offer(aclChangeSetToReindex);
    }

    @Override
    public void addAclChangeSetToIndex(Long aclChangeSetToIndex)
    {
        aclChangeSetsToIndex.offer(aclChangeSetToIndex);
    }

    @Override
    public void addAclChangeSetToPurge(Long aclChangeSetToPurge)
    {
        aclChangeSetsToPurge.offer(aclChangeSetToPurge);
    }

    // ACLs

    @Override
    public void addAclToReindex(Long aclToReindex)
    {
        aclsToReindex.offer(aclToReindex);
    }

    @Override
    public void addAclToIndex(Long aclToIndex)
    {
        aclsToIndex.offer(aclToIndex);
    }

    @Override
    public void addAclToPurge(Long aclToPurge)
    {
        aclsToPurge.offer(aclToPurge);
    }

    private void reindexTransactions() throws IOException, AuthenticationException, JSONException
    {
        int docCount = 0;
        boolean requiresCommit = false;
        while (transactionsToReindex.peek() != null)
        {
            Long transactionId = transactionsToReindex.poll();
            if (transactionId != null)
            {
                // make sure it is cleaned out so we do not miss deletes
                this.infoSrv.deleteByTransactionId(transactionId);

                Transactions transactions = client.getTransactions(null, transactionId, null, transactionId+1, 1);
                if ((transactions.getTransactions().size() > 0) && (transactionId.equals(transactions.getTransactions().get(0).getId())))
                {
                    Transaction info = transactions.getTransactions().get(0);

                    GetNodesParameters gnp = new GetNodesParameters();
                    ArrayList<Long> txs = new ArrayList<Long>();
                    txs.add(info.getId());
                    gnp.setTransactionIds(txs);
                    gnp.setStoreProtocol(storeRef.getProtocol());
                    gnp.setStoreIdentifier(storeRef.getIdentifier());
                    List<Node> nodes = client.getNodes(gnp, (int) info.getUpdates());
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

                    // Index the transaction doc after the node - if this is not found then a reindex will be done.
                    this.infoSrv.indexTransaction(info, true);
                    requiresCommit = true;

                }
            }

            if (docCount > batchCount)
            {
                if(this.infoSrv.getRegisteredSearcherCount() < getMaxLiveSearchers())
                {
                    checkShutdown();
                    this.infoSrv.commit();
                    docCount = 0;
                    requiresCommit = false;
                }
            }
        }
        if (requiresCommit || ( docCount > 0))
        {
            checkShutdown();
            this.infoSrv.commit();
        }
    }

    
    private void reindexNodes() throws IOException, AuthenticationException, JSONException
    {
        boolean requiresCommit = false;
        while (nodesToReindex.peek() != null)
        {
            Long nodeId = nodesToReindex.poll();
            if (nodeId != null)
            {
                // make sure it is cleaned out so we do not miss deletes
                this.infoSrv.deleteByNodeId(nodeId);

                Node node = new Node();
                node.setId(nodeId);
                node.setStatus(SolrApiNodeStatus.UNKNOWN);
                node.setTxnId(Long.MAX_VALUE);

                this.infoSrv.indexNode(node, true);
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

    private void indexNodes() throws IOException, AuthenticationException, JSONException
    {
        boolean requiresCommit = false;
        while (nodesToIndex.peek() != null)
        {
            Long nodeId = nodesToIndex.poll();
            if (nodeId != null)
            {
                Node node = new Node();
                node.setId(nodeId);
                node.setStatus(SolrApiNodeStatus.UNKNOWN);
                node.setTxnId(Long.MAX_VALUE);

                this.infoSrv.indexNode(node, false);
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

    private void indexTransactions() throws IOException, AuthenticationException, JSONException
    {
        int docCount = 0;
        boolean requiresCommit = false;
        while (transactionsToIndex.peek() != null)
        {
            Long transactionId = transactionsToIndex.poll();
            if (transactionId != null)
            {
                Transactions transactions = client.getTransactions(null, transactionId, null, transactionId+1, 1);
                if ((transactions.getTransactions().size() > 0) && (transactionId.equals(transactions.getTransactions().get(0).getId())))
                {
                    Transaction info = transactions.getTransactions().get(0);

                    GetNodesParameters gnp = new GetNodesParameters();
                    ArrayList<Long> txs = new ArrayList<Long>();
                    txs.add(info.getId());
                    gnp.setTransactionIds(txs);
                    gnp.setStoreProtocol(storeRef.getProtocol());
                    gnp.setStoreIdentifier(storeRef.getIdentifier());
                    List<Node> nodes = client.getNodes(gnp, (int) info.getUpdates());
                    for (Node node : nodes)
                    {
                        docCount++;
                        if (log.isDebugEnabled())
                        {
                            log.debug(node.toString());
                        }
                        this.infoSrv.indexNode(node, false);
                        checkShutdown();
                    }

                    // Index the transaction doc after the node - if this is not found then a reindex will be done.
                    this.infoSrv.indexTransaction(info, false);
                    requiresCommit = true;

                }
            }

            if (docCount > batchCount)
            {
                if(this.infoSrv.getRegisteredSearcherCount() < getMaxLiveSearchers())
                {
                    checkShutdown();
                    this.infoSrv.commit();
                    docCount = 0;
                    requiresCommit = false;
                }
            }
        }
        if (requiresCommit || (docCount > 0))
        {
            checkShutdown();
            this.infoSrv.commit();
        }
    }

    private void purgeTransactions() throws IOException, AuthenticationException, JSONException
    {
        boolean requiresCommit = false;
        while (transactionsToPurge.peek() != null)
        {
            Long transactionId = transactionsToPurge.poll();
            if (transactionId != null)
            {
                // make sure it is cleaned out so we do not miss deletes
                this.infoSrv.deleteByTransactionId(transactionId);
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

    private void purgeNodes() throws IOException, AuthenticationException, JSONException
    {
        boolean requiresCommit = false;
        while (nodesToPurge.peek() != null)
        {
            Long nodeId = nodesToPurge.poll();
            if (nodeId != null)
            {
                // make sure it is cleaned out so we do not miss deletes
                this.infoSrv.deleteByNodeId(nodeId);
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
        trackModels(false);
        
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
        trackTransactions();

        // check index state
        if (state.isCheck())
        {
            this.infoSrv.checkCache();
        }
    }

    /**
     * @param solrIndexSearcher
     * @param reader
     * @throws AuthenticationException
     * @throws IOException
     * @throws JSONException
     */
    protected void trackTransactions() throws AuthenticationException, IOException, JSONException
    {
        boolean indexed = false;
        boolean upToDate = false;
// TODO: This variable is never used.  Please see if we even need it.
        ArrayList<Transaction> transactionsOrderedById = new ArrayList<Transaction>(10000);
        Transactions transactions;
        BoundedDeque<Transaction> txnsFound = new BoundedDeque<Transaction>(100);
        TrackerState state = this.infoSrv.getTrackerState();
        
        do
        {
            int docCount = 0;

            Long fromCommitTime =  getTxFromCommitTime(txnsFound, state.getLastGoodTxCommitTimeInIndex());
            transactions = getSomeTransactions(txnsFound, fromCommitTime, 60*60*1000L, 2000, state.getTimeToStopIndexing());

            Long maxTxnCommitTime = transactions.getMaxTxnCommitTime();
            if(maxTxnCommitTime != null)

            {
                state.setLastTxCommitTimeOnServer(transactions.getMaxTxnCommitTime());
            }

            Long maxTxnId = transactions.getMaxTxnId();
            if(maxTxnId != null)
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
                            + ((txnsFound.size() > 0) ? txnsFound.getLast().getCommitTimeMs() : state.getLastIndexedTxCommitTime()));
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
                    if(this.infoSrv.getRegisteredSearcherCount() < getMaxLiveSearchers())
                    {
                        checkShutdown();
                        this.infoSrv.commit();
                        docCount = 0;
                    }
                }
                checkShutdown();
            }
        }
        while ((transactions.getTransactions().size() > 0) && (upToDate == false));


        if (indexed)
        {
            checkShutdown();
            this.infoSrv.commit();
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


    /**
     * @throws AuthenticationException
     * @throws IOException
     * @throws JSONException
     */
    private void trackModelsImpl() throws AuthenticationException, IOException, JSONException
    {
        // track models
        // reflect changes changes and update on disk copy

        long start = System.nanoTime();

        List<AlfrescoModelDiff> modelDiffs = client.getModelsDiff(this.infoSrv.getAlfrescoModels());
        HashMap<String, M2Model> modelMap = new HashMap<String, M2Model>();

        for (AlfrescoModelDiff modelDiff : modelDiffs)
        {
            switch (modelDiff.getType())
            {
            case CHANGED:
                AlfrescoModel changedModel = client.getModel(modelDiff.getModelName());
                for (M2Namespace namespace : changedModel.getModel().getNamespaces())
                {
                    modelMap.put(namespace.getUri(), changedModel.getModel());
                }
                break;
            case NEW:
                AlfrescoModel newModel = client.getModel(modelDiff.getModelName());
                for (M2Namespace namespace : newModel.getModel().getNamespaces())
                {
                    modelMap.put(namespace.getUri(), newModel.getModel());
                }
                break;
            case REMOVED:
                // At the moment we do not unload models - I can see no side effects .... 
                // However search is used to check for references to indexed properties or types
                // This will be partially broken anyway due to eventual consistency
                // A model should only be unloaded if there are no data dependencies
                // Should have been on the de-lucene list.
                break;
            }
        }

        HashSet<String> loadedModels = new HashSet<String>();
        for (M2Model model : modelMap.values())
        {
            loadModel(modelMap, loadedModels, model);
        }
        if(loadedModels.size() > 0)
        {
            this.infoSrv.afterInitModels();
        }

        File alfrescoModelDir = new File(id, "alfrescoModels");
        if (!alfrescoModelDir.exists())
        {
            alfrescoModelDir.mkdir();
        }
        for (AlfrescoModelDiff modelDiff : modelDiffs)
        {
            switch (modelDiff.getType())
            {
            case CHANGED:
                removeMatchingModels(alfrescoModelDir, modelDiff.getModelName());
                M2Model changedModel = this.infoSrv.getM2Model(modelDiff.getModelName());
                File changedFile = new File(alfrescoModelDir, getModelFileName(changedModel));
                FileOutputStream cos = new FileOutputStream(changedFile);
                changedModel.toXML(cos);
                cos.flush();
                cos.close();
                break;
            case NEW:
                M2Model newModel = this.infoSrv.getM2Model(modelDiff.getModelName());
                // add on file
                File newFile = new File(alfrescoModelDir, getModelFileName(newModel));
                FileOutputStream nos = new FileOutputStream(newFile);
                newModel.toXML(nos);
                nos.flush();
                nos.close();
                break;
            case REMOVED:
                removeMatchingModels(alfrescoModelDir, modelDiff.getModelName());
                break;
            }
        }


        long end = System.nanoTime();

        trackerStats.addModelTime(end-start);

        if(true == runPostModelLoadInit)
        {
            for(Object key : props.keySet())
            {
                String stringKey = (String)key;
                if(stringKey.startsWith("alfresco.index.store"))
                {
                    StoreRef store = new StoreRef(props.getProperty(stringKey));
                    indexedStores.add(store);
                }
                if(stringKey.startsWith("alfresco.ignore.store"))
                {
                    StoreRef store = new StoreRef(props.getProperty(stringKey));
                    ignoredStores.add(store);
                }
                if(stringKey.startsWith("alfresco.index.tenant"))
                {
                    indexedTenants.add(props.getProperty(stringKey));
                }
                if(stringKey.startsWith("alfresco.ignore.tenant"))
                {
                    ignoredTenants.add(props.getProperty(stringKey));
                }
                if(stringKey.startsWith("alfresco.index.datatype"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    indexedDataTypes.add(qname);
                }
                if(stringKey.startsWith("alfresco.ignore.datatype"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    ignoredDataTypes.add(qname);
                }
                if(stringKey.startsWith("alfresco.index.type"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    indexedTypes.add(qname);
                }
                if(stringKey.startsWith("alfresco.ignore.type"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    ignoredTypes.add(qname);
                }
                if(stringKey.startsWith("alfresco.index.aspect"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    indexedAspects.add(qname);
                }
                if(stringKey.startsWith("alfresco.ignore.aspect"))
                {
                    QName qname = expandQName(props.getProperty(stringKey));
                    ignoredAspects.add(qname);
                }
                if(stringKey.startsWith("alfresco.index.field"))
                {
                    String name = expandName(props.getProperty(stringKey));
                    indexedFields.add(name);
                }
                if(stringKey.startsWith("alfresco.ignore.field"))
                {
                    String name = expandName(props.getProperty(stringKey));
                    ignoredFields.add(name);
                }
            }
            runPostModelLoadInit = false;
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
        else if (AlfrescoSolrDataModel.nonDictionaryFields.get(qName) == null)
        {
            expandedQName = expandQNameImpl(qName);
        }
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
        else if (AlfrescoSolrDataModel.nonDictionaryFields.get(qName) == null)
        {
            expandedQName = expandQNameImpl(qName);
        }
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
     * @param alfrescoModelDir
     * @param modelName
     */
    private void removeMatchingModels(File alfrescoModelDir, QName modelName)
    {

        final String prefix = modelName.toPrefixString(this.infoSrv.getNamespaceDAO()).replace(":", ".") + ".";
        final String postFix = ".xml";

        File[] toDelete = alfrescoModelDir.listFiles(new FileFilter()
        {

            @Override
            public boolean accept(File pathname)
            {
                if (pathname.isDirectory())
                {
                    return false;
                }
                String name = pathname.getName();
                if (false == name.endsWith(postFix))
                {
                    return false;
                }
                if (false == name.startsWith(prefix))
                {
                    return false;
                }
                // check is number between
                String checksum = name.substring(prefix.length(), name.length() - postFix.length());
                try
                {
                    Long.parseLong(checksum);
                    return true;
                }
                catch (NumberFormatException nfe)
                {
                    return false;
                }
            }
        });

        if (toDelete != null)
        {
            for (File file : toDelete)
            {
                file.delete();
            }
        }

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

    @Override
    public NodeReport checkNode(Node node)
    {
        NodeReport nodeReport = new NodeReport();
        nodeReport.setDbid(node.getId());

        nodeReport.setDbNodeStatus(node.getStatus());
        nodeReport.setDbTx(node.getTxnId());

        this.infoSrv.checkNodeCommon(nodeReport);

        return nodeReport;
    }

    /**
     * @param dbid
     * @return
     */
    @Override
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

    @Override
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
    @Override
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

    @Override
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

    private void loadModel(Map<String, M2Model> modelMap, HashSet<String> loadedModels, M2Model model)
    {
        String modelName = model.getName();
        if (loadedModels.contains(modelName) == false)
        {
            for (M2Namespace importNamespace : model.getImports())
            {
                M2Model importedModel = modelMap.get(importNamespace.getUri());
                if (importedModel != null)
                {

                    // Ensure that the imported model is loaded first
                    loadModel(modelMap, loadedModels, importedModel);
                }
            }

            if(this.infoSrv.putModel(model))
            {
                loadedModels.add(modelName);
            }
            log.info("Loading model " + model.getName());
        }
    }

    private String getModelFileName(M2Model model)
    {
        return model.getName().replace(":", ".") + "." + model.getChecksum(XMLBindingType.DEFAULT) + ".xml";
    }


    @Override
    public void close()
    {
        client.close();
    }

    @Override
    public void trackModels(boolean onlyFirstTime)  throws AuthenticationException, IOException, JSONException
    {
        boolean requiresWriteLock = false;
        modelLock.readLock().lock();
        try
        {
            if(hasModels)
            {
                if(onlyFirstTime)
                {
                    return;
                }
                else
                {
                    requiresWriteLock = false;
                }
            }
            else
            {
                requiresWriteLock = true;
            }
        }
        finally
        {
            modelLock.readLock().unlock();
        }

        if(requiresWriteLock)
        {
            modelLock.writeLock().lock();
            try
            {
                if(hasModels)
                {
                    if(onlyFirstTime)
                    {
                        return;
                    }
                }
                
                trackModelsImpl();
                hasModels = true;
            }
            finally
            {
                modelLock.writeLock().unlock();
            }
        }
        else
        {
            trackModelsImpl();
        }
    }

    @Override
    public void ensureFirstModelSync()
    {
        try
        {
            trackModels(true);
        }
        catch(Throwable t)
        {
            log.error("Model tracking failed", t);
        }
        
    }

    /**
     * @return Alfresco version Solr was built for
     */
    @Override
    public String getAlfrescoVersion()
    {
        return alfrescoVersion;
    }
    
    @Override
    public void setShutdown(boolean shutdown)
    {
        this.shutdown = shutdown;
    }
    
    @Override
    public List<NodeMetaData> getNodesMetaData(NodeMetaDataParameters params, int maxResults) throws AuthenticationException, IOException, JSONException 
    {
        return client.getNodesMetaData(params, maxResults);
    }
    
    @Override
    public GetTextContentResponse getTextContent(Long nodeId, QName propertyQName, Long modifiedSince) throws AuthenticationException, IOException
    {
        return client.getTextContent(nodeId, propertyQName, modifiedSince);
    }

    @Override
    public boolean canAddContentPropertyToDoc() 
    {
        return (indexedDataTypes.isEmpty() || indexedDataTypes.contains(DataTypeDefinition.CONTENT))
                    && !ignoredDataTypes.contains(DataTypeDefinition.CONTENT);
    }
    
}