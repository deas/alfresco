/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.encryption.KeyStoreParameters;
import org.alfresco.encryption.ssl.SSLEncryptionParameters;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.httpclient.AlfrescoHttpClient;
import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.httpclient.HttpClientFactory.SecureCommsType;
import org.alfresco.model.ContentModel;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.search.impl.lucene.MultiReader;
import org.alfresco.repo.search.impl.lucene.analysis.NumericEncoder;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.ModelDefinition.XMLBindingType;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AclReport;
import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.AlfrescoSolrEventListener;
import org.alfresco.solr.SolrKeyResourceLoader;
import org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry;
import org.alfresco.solr.AlfrescoSolrEventListener.OwnerIdManager;
import org.alfresco.solr.NodeReport;
import org.alfresco.solr.ResizeableArrayList;
import org.alfresco.solr.client.Acl;
import org.alfresco.solr.client.AclChangeSet;
import org.alfresco.solr.client.AclChangeSets;
import org.alfresco.solr.client.AclReaders;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.client.AlfrescoModelDiff;
import org.alfresco.solr.client.ContentPropertyValue;
import org.alfresco.solr.client.GetNodesParameters;
import org.alfresco.solr.client.MLTextPropertyValue;
import org.alfresco.solr.client.MultiPropertyValue;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.Node.SolrApiNodeStatus;
import org.alfresco.solr.client.NodeMetaData;
import org.alfresco.solr.client.NodeMetaDataParameters;
import org.alfresco.solr.client.PropertyValue;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.SOLRAPIClient.GetTextContentResponse;
import org.alfresco.solr.client.StringPropertyValue;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.client.Transactions;
import org.alfresco.util.ISO9075;
import org.alfresco.util.Pair;
import org.alfresco.util.TempFileProvider;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.IndexDeletionPolicyWrapper;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrInfoMBean;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.SnapShooter;
import org.apache.solr.schema.BinaryField;
import org.apache.solr.schema.CopyField;
import org.apache.solr.schema.DateField;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.FastLRUCache;
import org.apache.solr.search.LRUCache;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.DeleteUpdateCommand;
import org.apache.solr.update.RollbackUpdateCommand;
import org.apache.solr.util.RefCounted;
import org.json.JSONException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.util.FileCopyUtils;

public class CoreTracker implements CloseHook
{
    protected final static Logger log = LoggerFactory.getLogger(CoreTracker.class);

    private AlfrescoCoreAdminHandler adminHandler;

    protected SOLRAPIClient client;

    protected TrackerState state = new TrackerState();

    SolrCore core;

    private String alfrescoHost;

    private int alfrescoPort;

    private int alfrescoPortSSL;

    private String baseUrl;

    private String cron;

    protected StoreRef storeRef;

    private long lag;

    private long holeRetention;

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
    
    // index contrl

    // http client

    private int maxTotalConnections = 40;

    private int maxHostConnections = 40;

    private int socketTimeout = 120000;

    private String id;

    private AlfrescoSolrDataModel dataModel;

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

    protected TrackerStats trackerStats = new TrackerStats();

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

    // 

    private boolean transformContent = true;

    private int maxLiveSearchers;

    //

    private ReentrantReadWriteLock modelLock = new ReentrantReadWriteLock();
    
    boolean hasModels = false;
    
    //
    private volatile boolean shutdown = false;

    private int filterCacheSize;

    private int authorityCacheSize;

    private int pathCacheSize;


    public long getLastIndexedTxCommitTime()
    {
        return state.lastIndexedTxCommitTime;
    }

    public long getLastIndexedChangeSetCommitTime()
    {
        return state.lastIndexedChangeSetCommitTime;
    }

    public long getLastIndexedTxId()
    {
        return state.lastIndexedTxId;
    }

    public long getLastIndexedChangeSetId()
    {
        return state.lastIndexedChangeSetId;
    }


    public boolean isRunning()
    {
        return state.running;
    }

    public long getLastTxCommitTimeOnServer()
    {
        return state.lastTxCommitTimeOnServer;
    }

    public long getLastChangeSetCommitTimeOnServer()
    {
        return state.lastChangeSetCommitTimeOnServer;
    }

    public long getLastTxIdOnServer()
    {
        return state.lastTxIdOnServer;
    }

    public long getLastChangeSetIdOnServer()
    {
        return state.lastChangeSetIdOnServer;
    }

    public TrackerStats getTrackerStats()
    {
        return trackerStats;
    }

    public Map<String, Set<String>> getModelErrors()
    {
        return dataModel.getModelErrors();
    }

    public int getMaxLiveSearchers()
    {
        return maxLiveSearchers;
    }

    CoreTracker(AlfrescoCoreAdminHandler adminHandler, SolrCore core)
    {
        super();
        this.adminHandler = adminHandler;
        this.core = core;

        boolean storeAll = false;

        SolrResourceLoader loader = core.getSchema().getResourceLoader();
        id = loader.getInstanceDir();
        dataModel = AlfrescoSolrDataModel.getInstance(id);
        dataModel.setStoreAll(storeAll);

        Properties p = core.getResourceLoader().getCoreProperties();
        alfrescoHost = p.getProperty("alfresco.host", "localhost");
        alfrescoPort = Integer.parseInt(p.getProperty("alfresco.port", "8080"));
        alfrescoPortSSL = Integer.parseInt(p.getProperty("alfresco.port.ssl", "8443"));
        baseUrl = p.getProperty("alfresco.baseUrl", "/alfresco");
        cron =  p.getProperty("alfresco.cron", "0/15 * * * * ? *");
        storeRef = new StoreRef(p.getProperty("alfresco.stores"));
        lag = Integer.parseInt(p.getProperty("alfresco.lag", "1000"));
        holeRetention = Integer.parseInt(p.getProperty("alfresco.hole.retention", "3600000"));
        batchCount = Integer.parseInt(p.getProperty("alfresco.batch.count", "1000"));
        storeAll = Boolean.parseBoolean(p.getProperty("alfresco.storeAll", "false"));
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
        transformContent = Boolean.parseBoolean(p.getProperty("alfresco.index.transformContent", "true"));
        socketTimeout = Integer.parseInt(p.getProperty("alfresco.socketTimeout", "0"));
        maxLiveSearchers =  Integer.parseInt(p.getProperty("alfresco.maxLiveSearchers", "2"));
        isSlave =  Boolean.parseBoolean(p.getProperty("enable.slave", "false"));
        isMaster =  Boolean.parseBoolean(p.getProperty("enable.master", "true"));

        filterCacheSize =  Integer.parseInt(p.getProperty("solr.filterCache.size", "64"));
        authorityCacheSize =  Integer.parseInt(p.getProperty("solr.authorityCache.size", "64"));
        pathCacheSize =  Integer.parseInt(p.getProperty("solr.pathCache.size", "64"));
        
        
        client = new SOLRAPIClient(getRepoClient(loader), dataModel.getDictionaryService(CMISStrictDictionaryService.DEFAULT), dataModel.getNamespaceDAO());

        JobDetail job = new JobDetail("CoreTracker-" + core.getName(), "Solr", CoreTrackerJob.class);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("TRACKER", this);
        job.setJobDataMap(jobDataMap);
        Trigger trigger;
        try
        {
            trigger = new CronTrigger("CoreTrackerTrigger" + core.getName(), "Solr", cron);
            adminHandler.getScheduler().scheduleJob(job, trigger);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (SchedulerException e)
        {
            e.printStackTrace();
        }

        core.addCloseHook(this);
    }

    protected AlfrescoHttpClient getRepoClient(SolrResourceLoader loader)
    {
        // TODO i18n
        KeyStoreParameters keyStoreParameters = new KeyStoreParameters("SSL Key Store", sslKeyStoreType, sslKeyStoreProvider, sslKeyStorePasswordFileLocation, sslKeyStoreLocation);
        KeyStoreParameters trustStoreParameters = new KeyStoreParameters("SSL Trust Store", sslTrustStoreType, sslTrustStoreProvider, sslTrustStorePasswordFileLocation, sslTrustStoreLocation);
        SSLEncryptionParameters sslEncryptionParameters = new SSLEncryptionParameters(keyStoreParameters, trustStoreParameters);
        SolrKeyResourceLoader keyResourceLoader = new SolrKeyResourceLoader(loader);

        HttpClientFactory httpClientFactory = new HttpClientFactory(SecureCommsType.getType(secureCommsType),
                sslEncryptionParameters, keyResourceLoader, null, null, alfrescoHost, alfrescoPort, alfrescoPortSSL, maxTotalConnections, maxHostConnections, socketTimeout);
        // TODO need to make port configurable depending on secure comms, or just make redirects
        // work
        AlfrescoHttpClient repoClient = httpClientFactory.getRepoClient(alfrescoHost, alfrescoPortSSL);
        repoClient.setBaseUrl(baseUrl);
        return repoClient;
    }

    /**
     * @return the check
     */
    public boolean isCheck()
    {
        return state.check;
    }

    /**
     * @param check
     *            the check to set
     */
    public void setCheck(boolean check)
    {
        this.state.check = check;
    }

    public void updateIndex()
    {

        synchronized (this)
        {
            if (state.running)
            {
                log.info("... update for " + core.getName() + " is already running");
                return;
            }
            else
            {
                log.info("... updating " + core.getName());
                state.running = true;
            }
        }
        try
        {
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
                core.getUpdateHandler().rollback(new RollbackUpdateCommand());
            }
            catch (IOException e)
            {
                log.error("Failed to roll back pending work on error", t);
            }
            log.info("Stopping index tracking for "+core.getName());
        }
        catch(Throwable t)
        {
            try
            {
                core.getUpdateHandler().rollback(new RollbackUpdateCommand());
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
            state.running = false;
            state.check = false;
        }
    }

    public void indexAclChangeSets() throws AuthenticationException, IOException, JSONException
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
                    indexAclTransaction(changeSet, false);
                    requiresCommit = true;
                }
            }
            checkShutdown();
        }
        if(requiresCommit)
        {
            checkShutdown();
            core.getUpdateHandler().commit(new CommitUpdateCommand(false));
        }
    }

    public void indexAcls() throws AuthenticationException, IOException, JSONException
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
            core.getUpdateHandler().commit(new CommitUpdateCommand(false));
        }
    }

    public void reindexAclChangeSets() throws AuthenticationException, IOException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            boolean requiresCommit = false;
            while (aclChangeSetsToReindex.peek() != null)
            {
                Long aclChangeSetId = aclChangeSetsToReindex.poll();
                if (aclChangeSetId != null)
                {
                    deleteByAclChangeSetId(solrIndexSearcher, aclChangeSetId);

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
                        indexAclTransaction(changeSet, true);
                        requiresCommit = true;
                    }
                }
                checkShutdown();
            }
            if(requiresCommit)
            {
                checkShutdown();
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    public void reindexAcls() throws AuthenticationException, IOException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);

            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            boolean requiresCommit = false;
            while (aclsToReindex.peek() != null)
            {
                Long aclId = aclsToReindex.poll();
                if (aclId != null)
                {
                    deleteByAclId(solrIndexSearcher, aclId);

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
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    public void purgeAclChangeSets() throws AuthenticationException, IOException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            boolean requiresCommit = false;
            while (aclChangeSetsToPurge.peek() != null)
            {
                Long aclChangeSetId = aclChangeSetsToPurge.poll();
                if (aclChangeSetId != null)
                {
                    deleteByAclChangeSetId(solrIndexSearcher, aclChangeSetId);
                    requiresCommit = true;
                }
                checkShutdown();
            }
            if(requiresCommit)
            {
                checkShutdown();
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    protected void checkShutdown()
    {
        if(shutdown)
        {
            throw new IndexTrackingShutdownException();
        }
    }
    
    public void purgeAcls() throws AuthenticationException, IOException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            boolean requiresCommit = false;
            while (aclsToPurge.peek() != null)
            {
                Long aclId = aclsToPurge.poll();
                if (aclId != null)
                {
                    deleteByAclId(solrIndexSearcher, aclId);
                    requiresCommit = true;
                }
                checkShutdown();
            }
            if(requiresCommit)
            {
                checkShutdown();
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    public void deleteByAclChangeSetId(SolrIndexSearcher solrIndexSearcher, Long aclChangeSetId) throws IOException
    {

        Query query = new TermQuery(new Term(QueryConstants.FIELD_INACLTXID, NumericEncoder.encode(aclChangeSetId)));
        deleteByQuery(solrIndexSearcher, query);
    }

    public void deleteByAclId(SolrIndexSearcher solrIndexSearcher, Long aclId) throws IOException
    {

        Query query = new TermQuery(new Term(QueryConstants.FIELD_ACLID, NumericEncoder.encode(aclId)));
        deleteByQuery(solrIndexSearcher, query);
    }

    private void deleteByQuery(SolrIndexSearcher solrIndexSearcher, Query query) throws IOException
    {
        HashSet<String> idsToDelete = new HashSet<String>();

        DocSet docSet = solrIndexSearcher.getDocSet(query);
        if (docSet instanceof BitDocSet)
        {
            BitDocSet source = (BitDocSet) docSet;
            OpenBitSet openBitSet = source.getBits();
            int current = -1;
            while ((current = openBitSet.nextSetBit(current + 1)) != -1)
            {
                Document doc = solrIndexSearcher.doc(current, Collections.singleton(QueryConstants.FIELD_ID));
                Fieldable fieldable = doc.getFieldable(QueryConstants.FIELD_ID);
                if (fieldable != null)
                {
                    idsToDelete.add(fieldable.stringValue());
                }

            }
        }
        else
        {
            for (DocIterator it = docSet.iterator(); it.hasNext(); /* */)
            {
                Document doc = solrIndexSearcher.doc(it.nextDoc(), Collections.singleton(QueryConstants.FIELD_ID));
                Fieldable fieldable = doc.getFieldable(QueryConstants.FIELD_ID);
                if (fieldable != null)
                {
                    idsToDelete.add(fieldable.stringValue());
                }
            }
        }

        for (String idToDelete : idsToDelete)
        {
            DeleteUpdateCommand docCmd = new DeleteUpdateCommand();
            docCmd.id = idToDelete;
            docCmd.fromPending = true;
            docCmd.fromCommitted = true;
            core.getUpdateHandler().delete(docCmd);
        }

    }

    // TX

    public void addTransactionToReindex(Long transactionToReindex)
    {
        transactionsToReindex.offer(transactionToReindex);
    }

    public void addTransactionToIndex(Long transactionToIndex)
    {
        transactionsToIndex.offer(transactionToIndex);
    }

    public void addTransactionToPurge(Long transactionToPurge)
    {
        transactionsToPurge.offer(transactionToPurge);
    }

    // Node

    public void addNodeToReindex(Long nodeToReindex)
    {
        nodesToReindex.offer(nodeToReindex);
    }

    public void addNodeToIndex(Long nodeToIndex)
    {
        nodesToIndex.offer(nodeToIndex);
    }

    public void addNodeToPurge(Long nodeToPurge)
    {
        nodesToPurge.offer(nodeToPurge);
    }

    // ACL change sets

    public void addAclChangeSetToReindex(Long aclChangeSetToReindex)
    {
        aclChangeSetsToReindex.offer(aclChangeSetToReindex);
    }

    public void addAclChangeSetToIndex(Long aclChangeSetToIndex)
    {
        aclChangeSetsToIndex.offer(aclChangeSetToIndex);
    }

    public void addAclChangeSetToPurge(Long aclChangeSetToPurge)
    {
        aclChangeSetsToPurge.offer(aclChangeSetToPurge);
    }

    // ACLs

    public void addAclToReindex(Long aclToReindex)
    {
        aclsToReindex.offer(aclToReindex);
    }

    public void addAclToIndex(Long aclToIndex)
    {
        aclsToIndex.offer(aclToIndex);
    }

    public void addAclToPurge(Long aclToPurge)
    {
        aclsToPurge.offer(aclToPurge);
    }

    private void reindexTransactions() throws IOException, AuthenticationException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);

            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            int docCount = 0;
            boolean requiresCommit = false;
            while (transactionsToReindex.peek() != null)
            {
                Long transactionId = transactionsToReindex.poll();
                if (transactionId != null)
                {
                    // make sure it is cleaned out so we do not miss deletes
                    deleteByTransactionId(solrIndexSearcher, transactionId);

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
                            indexNode(node, solrIndexSearcher, true);
                            checkShutdown();
                        }

                        // Index the transaction doc after the node - if this is not found then a reindex will be
                        // done.
                        indexTransaction(info, true);
                        requiresCommit = true;

                    }
                }

                if (docCount > batchCount)
                {
                    if(getRegisteredSearcherCount() < getMaxLiveSearchers())
                    {
                        checkShutdown();
                        core.getUpdateHandler().commit(new CommitUpdateCommand(false));
                        docCount = 0;
                        requiresCommit = false;
                    }
                }
            }
            if (requiresCommit || ( docCount > 0))
            {
                checkShutdown();
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }

    }

    private void reindexNodes() throws IOException, AuthenticationException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            boolean requiresCommit = false;
            while (nodesToReindex.peek() != null)
            {
                Long nodeId = nodesToReindex.poll();
                if (nodeId != null)
                {
                    // make sure it is cleaned out so we do not miss deletes
                    deleteByNodeId(solrIndexSearcher, nodeId);

                    Node node = new Node();
                    node.setId(nodeId);
                    node.setStatus(SolrApiNodeStatus.UNKNOWN);
                    node.setTxnId(Long.MAX_VALUE);

                    indexNode(node, solrIndexSearcher, true);
                    requiresCommit = true;
                }
                checkShutdown();

            }

            if(requiresCommit)
            {
                checkShutdown();
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }

        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }

    }

    private void indexNodes() throws IOException, AuthenticationException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
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

                    indexNode(node, solrIndexSearcher, false);
                    requiresCommit = true;
                }
                checkShutdown();
            }

            if(requiresCommit)
            {
                checkShutdown();
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }

        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }

    }

    private void indexTransactions() throws IOException, AuthenticationException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);

            SolrIndexSearcher solrIndexSearcher = refCounted.get();
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
                            indexNode(node, solrIndexSearcher, false);
                            checkShutdown();
                        }

                        // Index the transaction doc after the node - if this is not found then a reindex will be
                        // done.
                        indexTransaction(info, false);
                        requiresCommit = true;

                    }
                }

                if (docCount > batchCount)
                {
                    if(getRegisteredSearcherCount() < getMaxLiveSearchers())
                    {
                        checkShutdown();
                        core.getUpdateHandler().commit(new CommitUpdateCommand(false));
                        docCount = 0;
                        requiresCommit = false;
                    }
                }
            }
            if (requiresCommit || (docCount > 0))
            {
                checkShutdown();
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }

    }

    private void purgeTransactions() throws IOException, AuthenticationException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            boolean requiresCommit = false;
            while (transactionsToPurge.peek() != null)
            {
                Long transactionId = transactionsToPurge.poll();
                if (transactionId != null)
                {
                    // make sure it is cleaned out so we do not miss deletes
                    deleteByTransactionId(solrIndexSearcher, transactionId);
                    requiresCommit = true;
                }
                checkShutdown();
            }
            if(requiresCommit)
            {
                checkShutdown();
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }

    }

    private void purgeNodes() throws IOException, AuthenticationException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            boolean requiresCommit = false;
            while (nodesToPurge.peek() != null)
            {
                Long nodeId = nodesToPurge.poll();
                if (nodeId != null)
                {
                    // make sure it is cleaned out so we do not miss deletes
                    deleteByNodeId(solrIndexSearcher, nodeId);
                    requiresCommit = true;
                }
                checkShutdown();
            }
            if(requiresCommit)
            {
                checkShutdown();
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }

    }

    private void deleteByTransactionId(SolrIndexSearcher solrIndexSearcher, Long transactionId) throws IOException
    {
        Query query = new TermQuery(new Term(QueryConstants.FIELD_INTXID, NumericEncoder.encode(transactionId)));
        deleteByQuery(solrIndexSearcher, query);
    }

    private void deleteByNodeId(SolrIndexSearcher solrIndexSearcher, Long nodeId) throws IOException
    {
        Query query = new TermQuery(new Term(QueryConstants.FIELD_DBID, NumericEncoder.encode(nodeId)));
        deleteByQuery(solrIndexSearcher, query);
    }

    public void trackRepository() throws IOException, AuthenticationException, JSONException
    {
        int registeredSearcherCount = getRegisteredSearcherCount();
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

        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            //SolrIndexReader reader = solrIndexSearcher.getReader();

            setTrackerInitialState(solrIndexSearcher);

            // Check we are tracking the correct repository
            // Check the first TX time

            checkRepoAndIndexConsistency(solrIndexSearcher);


        }

        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }

        checkShutdown();
        trackAclChangeSets();

        checkShutdown();
        trackTransactions();


        // check index state

        if (state.check)
        {
            AddUpdateCommand checkDocCmd = new AddUpdateCommand();
            checkDocCmd.indexedId = "CHECK_CACHE";
            core.getUpdateHandler().addDoc(checkDocCmd);
            core.getUpdateHandler().commit(new CommitUpdateCommand(false));
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
        ArrayList<Transaction> transactionsOrderedById = new ArrayList<Transaction>(10000);
        Transactions transactions;
        BoundedDeque<Transaction> txnsFound = new  BoundedDeque<Transaction>(100);
        do
        {
            int docCount = 0;

            Long fromCommitTime =  getTxFromCommitTime(txnsFound, state.lastGoodTxCommitTimeInIndex);
            transactions = getSomeTransactions(txnsFound, fromCommitTime, 60*60*1000L, 2000, state.timeToStopIndexing);

            Long maxTxnCommitTime = transactions.getMaxTxnCommitTime();
            if(maxTxnCommitTime != null)

            {
                state.lastTxCommitTimeOnServer = transactions.getMaxTxnCommitTime();
            }

            Long maxTxnId = transactions.getMaxTxnId();
            if(maxTxnId != null)
            {
                state.lastTxIdOnServer = transactions.getMaxTxnId();
            }

            log.info("Scanning transactions ...");
            if (transactions.getTransactions().size() > 0)
            {
                log.info(".... from " + transactions.getTransactions().get(0));
                log.info(".... to " + transactions.getTransactions().get(transactions.getTransactions().size() - 1));
            }
            else
            {
                log.info(".... non found after lastTxCommitTime " + ((txnsFound.size() > 0) ? txnsFound.getLast().getCommitTimeMs() : state.lastIndexedTxCommitTime));
            }
            for (Transaction info : transactions.getTransactions())
            {
                boolean index = false;

                String target = NumericEncoder.encode(info.getId());
                RefCounted<SolrIndexSearcher> refCounted = null;
                Term term = null;
                try
                {
                    refCounted = core.getSearcher(false, true, null);
                    TermEnum termEnum = refCounted.get().getReader().terms(new Term(QueryConstants.FIELD_TXID, target));
                    term = termEnum.term();
                    termEnum.close();
                }
                finally
                {
                    if(refCounted != null)
                    {
                        refCounted.decref();
                    }
                    refCounted = null;
                }
                if (term == null)
                {
                    index = true;
                }
                else
                {
                    if (target.equals(term.text()))
                    {
                        txnsFound.add(info);
                    }
                    else
                    {
                        index = true;
                    }
                }


                if (index)
                {
                    // Make sure we do not go ahead of where we started - we will check the holes here
                    // correctly next time
                    if (info.getCommitTimeMs() > state.timeToStopIndexing)
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
                            refCounted = null;
                            try
                            {
                                refCounted = core.getSearcher(false, true, null);
                                indexNode(node, refCounted.get(), true);
                            }
                            finally
                            {
                                if(refCounted != null)
                                {
                                    refCounted.decref();
                                }
                                refCounted = null;
                            }
                            checkShutdown();
                        }


                        // Index the transaction doc after the node - if this is not found then a reindex will be
                        // done.
                        indexTransaction(info, true);

                        trackerStats.addTxDocs(nodes.size());

                        if (info.getCommitTimeMs() > state.lastIndexedTxCommitTime)
                        {
                            state.lastIndexedTxCommitTime = info.getCommitTimeMs();
                            state.lastIndexedTxId = info.getId();
                        }

                        txnsFound.add(info);
                    }
                }
                // could batch commit here
                if (docCount > batchCount)
                {
                    if(getRegisteredSearcherCount() < getMaxLiveSearchers())
                    {
                        checkShutdown();
                        core.getUpdateHandler().commit(new CommitUpdateCommand(false));
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
            core.getUpdateHandler().commit(new CommitUpdateCommand(false));
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
        do
        {
            Long fromCommitTime =  getChangeSetFromCommitTime(changeSetsFound, state.lastGoodChangeSetCommitTimeInIndex);
            aclChangeSets = getSomeAclChangeSets(changeSetsFound, fromCommitTime, 60*60*1000L, 2000, state.timeToStopIndexing);

            Long maxChangeSetCommitTime = aclChangeSets.getMaxChangeSetCommitTime();
            if(maxChangeSetCommitTime != null)
            {
                state.lastChangeSetCommitTimeOnServer = aclChangeSets.getMaxChangeSetCommitTime();
            }

            Long maxChangeSetId = aclChangeSets.getMaxChangeSetId();
            if(maxChangeSetId != null)
            {
                state.lastChangeSetIdOnServer = aclChangeSets.getMaxChangeSetId();
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
                    String target = NumericEncoder.encode(changeSet.getId());
                    RefCounted<SolrIndexSearcher> refCounted = null;
                    Term term = null;
                    try
                    {
                        refCounted = core.getSearcher(false, true, null);

                        TermEnum termEnum = refCounted.get().getReader().terms(new Term(QueryConstants.FIELD_ACLTXID, target));
                        term = termEnum.term();
                        termEnum.close();
                    }
                    finally
                    {
                        if(refCounted != null)
                        {
                            refCounted.decref();
                        }
                        refCounted = null;
                    }



                    if (term == null)
                    {
                        aclIndexing = true;
                    }
                    else
                    {
                        if (target.equals(term.text()))
                        {
                            changeSetsFound.add(changeSet);
                        }
                        else
                        {
                            aclIndexing = true;
                        }
                    }
                }

                if (aclIndexing)
                {
                    // Make sure we do not go ahead of where we started - we will check the holes here
                    // correctly next time
                    if (changeSet.getCommitTimeMs() > state.timeToStopIndexing)
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

                        indexAclTransaction(changeSet, true);
                        changeSetsFound.add(changeSet);

                        trackerStats.addChangeSetAcls(acls.size());

                        if (changeSet.getCommitTimeMs() > state.lastIndexedChangeSetCommitTime)
                        {
                            state.lastIndexedChangeSetCommitTime = changeSet.getCommitTimeMs();
                            state.lastIndexedChangeSetId = changeSet.getId();
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
            core.getUpdateHandler().commit(new CommitUpdateCommand(false));
        }
    }

    /**
     * @param solrIndexSearcher
     * @param reader
     * @throws AuthenticationException
     * @throws IOException
     * @throws JSONException
     */
    private void checkRepoAndIndexConsistency(SolrIndexSearcher solrIndexSearcher) throws AuthenticationException, IOException, JSONException
    {
        SolrIndexReader reader = solrIndexSearcher.getReader();

        if(state.lastGoodTxCommitTimeInIndex == 0) 
        {

            state.checkedFirstTransactionTime = true;
            log.info("No transactions found - no verification required");


            // Fix up inital state
            Transactions firstTransactions = client.getTransactions(null, 0L, null, 2000L, 1);
            if(firstTransactions.getTransactions().size() > 0)
            {
                Transaction firstTransaction = firstTransactions.getTransactions().get(0);
                long firstTransactionCommitTime = firstTransaction.getCommitTimeMs();
                state.lastGoodTxCommitTimeInIndex = firstTransactionCommitTime;
            }
        }

        if(state.lastGoodChangeSetCommitTimeInIndex == 0)
        {
            AclChangeSets firstChangeSets = client.getAclChangeSets(null, 0L, null, 2000L, 1);
            if(firstChangeSets.getAclChangeSets().size() > 0)
            {
                AclChangeSet firstChangeSet = firstChangeSets.getAclChangeSets().get(0);
                long firstChangeSetCommitTimex = firstChangeSet.getCommitTimeMs();
                state.lastGoodChangeSetCommitTimeInIndex =  firstChangeSetCommitTimex;
            }


        }

        if(!state.checkedFirstTransactionTime)
        {

            // TODO: getFirstTransaction
            Transactions firstTransactions = client.getTransactions(null, 0L, null, 2000L, 1);
            if(firstTransactions.getTransactions().size() > 0)
            {
                Transaction firstTransaction = firstTransactions.getTransactions().get(0);
                long firstTxId = firstTransaction.getId();
                String targetTxId = NumericEncoder.encode(firstTxId);
                long firstTransactionCommitTime = firstTransaction.getCommitTimeMs();
                String targetTxCommitTime = NumericEncoder.encode(firstTransactionCommitTime);

                BooleanQuery query = new BooleanQuery();


                query.add(new TermQuery(new Term(QueryConstants.FIELD_TXID, targetTxId)), Occur.MUST);
                query.add(new TermQuery(new Term(QueryConstants.FIELD_TXCOMMITTIME, targetTxCommitTime)), Occur.MUST);

                DocSet set = solrIndexSearcher.getDocSet(query);
                if(set.size() == 0)
                {
                    log.error("First transaction was not found with the correct timestamp.");
                    log.error("SOLR has successfully connected to your repository  however the SOLR indexes and repository database do not match."); 
                    log.error("If this is a new or rebuilt database you SOLR indexes also need to be re-built to match the database."); 
                    log.error("You can also check your SOLR connection details in solrcore.properties.");
                    throw new AlfrescoRuntimeException("Initial transaction not found with correct timestamp");
                }
                else if(set.size() == 1)
                {
                    state.checkedFirstTransactionTime = true;
                    log.info("Verified first transaction and timetsamp in index");
                }
                else
                {
                    log.warn("Duplicate initial transaction found with correct timestamp");
                }
            }

        }
    }

    static class TrackerState
    {
        volatile long lastChangeSetIdOnServer;

        volatile long lastChangeSetCommitTimeOnServer;

        volatile long lastIndexedChangeSetId;

        volatile long lastIndexedTxCommitTime = 0;

        volatile long lastIndexedTxId = 0;

        volatile long lastIndexedChangeSetCommitTime = 0;

        volatile long lastTxCommitTimeOnServer = 0;

        volatile long lastTxIdOnServer = 0;

        volatile long lastIndexedTxIdBeforeHoles = -1;

        volatile long lastIndexedChangeSetIdBeforeHoles = -1;

        volatile boolean running = false;

        volatile boolean checkedFirstTransactionTime = false;

        volatile boolean check = false;

        long timeToStopIndexing;

        long lastGoodChangeSetCommitTimeInIndex;

        long lastGoodTxCommitTimeInIndex ;

        long timeBeforeWhichThereCanBeNoHoles;
    }

    private void setTrackerInitialState(SolrIndexSearcher solrIndexSearcher) throws IOException
    {
        SolrIndexReader reader = solrIndexSearcher.getReader();

        if (state.lastIndexedTxCommitTime == 0)
        {
            state.lastIndexedTxCommitTime = getLastTransactionCommitTime(reader);
        }

        if (state.lastIndexedTxId == 0)
        {
            state.lastIndexedTxId = getLastTransactionId(reader);
        }

        if (state.lastIndexedChangeSetCommitTime == 0)
        {
            state.lastIndexedChangeSetCommitTime = getLastChangeSetCommitTime(reader);
        }

        if (state.lastIndexedChangeSetId == 0)
        {
            state.lastIndexedChangeSetId = getLastChangeSetId(reader);
        }

        long startTime = System.currentTimeMillis();
        state.timeToStopIndexing = startTime - lag;
        state.timeBeforeWhichThereCanBeNoHoles = startTime - holeRetention;

        long timeBeforeWhichThereCanBeNoTxHolesInIndex = state.lastIndexedTxCommitTime - holeRetention;
        state.lastGoodTxCommitTimeInIndex = getLastTxCommitTimeBeforeHoles(reader, timeBeforeWhichThereCanBeNoTxHolesInIndex);


        long timeBeforeWhichThereCanBeNoChangeSetHolesInIndex = state.lastIndexedChangeSetCommitTime - holeRetention;
        state.lastGoodChangeSetCommitTimeInIndex = getLastChangeSetCommitTimeBeforeHoles(reader, timeBeforeWhichThereCanBeNoChangeSetHolesInIndex);


        if(state.lastGoodTxCommitTimeInIndex > 0)
        {
            if (state.lastIndexedTxIdBeforeHoles == -1)
            {
                state.lastIndexedTxIdBeforeHoles = getLargestTxIdByCommitTime(reader, state.lastGoodTxCommitTimeInIndex);
            }
            else
            {
                long currentBestFromIndex = getLargestTxIdByCommitTime(reader, state.lastGoodTxCommitTimeInIndex);
                if (currentBestFromIndex > state.lastIndexedTxIdBeforeHoles)
                {
                    state.lastIndexedTxIdBeforeHoles = currentBestFromIndex;
                }
            }
        }

        if(state.lastGoodChangeSetCommitTimeInIndex > 0)
        {
            if (state.lastIndexedChangeSetIdBeforeHoles == -1)
            {
                state.lastIndexedChangeSetIdBeforeHoles = getLargestChangeSetIdByCommitTime(reader, state.lastGoodChangeSetCommitTimeInIndex);
            }
            else
            {
                long currentBestFromIndex = getLargestChangeSetIdByCommitTime(reader, state.lastGoodChangeSetCommitTimeInIndex);
                if (currentBestFromIndex > state.lastIndexedTxIdBeforeHoles)
                {
                    state.lastIndexedChangeSetIdBeforeHoles = currentBestFromIndex;
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
            HashSet<AclChangeSet> alreadyFound = new HashSet<AclChangeSet>(changeSetsFound.deque);
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
            HashSet<Transaction> alreadyFound = new HashSet<Transaction>(txnsFound.deque);
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


    private void trackModelsImpl() throws AuthenticationException, IOException, JSONException
    {
        // track models
        // reflect changes changes and update on disk copy

        long start = System.nanoTime();

        List<AlfrescoModelDiff> modelDiffs = client.getModelsDiff(dataModel.getAlfrescoModels());
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
            loadModel(modelMap, loadedModels, model, dataModel);
        }
        if(loadedModels.size() > 0)
        {
            dataModel.afterInitModels();
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
                M2Model changedModel = dataModel.getM2Model(modelDiff.getModelName());
                File changedFile = new File(alfrescoModelDir, getModelFileName(changedModel));
                FileOutputStream cos = new FileOutputStream(changedFile);
                changedModel.toXML(cos);
                cos.flush();
                cos.close();
                break;
            case NEW:
                M2Model newModel = dataModel.getM2Model(modelDiff.getModelName());
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
            Properties p = core.getResourceLoader().getCoreProperties();
            for(Object key : p.keySet())
            {
                String stringKey = (String)key;
                if(stringKey.startsWith("alfresco.index.store"))
                {
                    StoreRef store = new StoreRef(p.getProperty(stringKey));
                    indexedStores.add(store);
                }
                if(stringKey.startsWith("alfresco.ignore.store"))
                {
                    StoreRef store = new StoreRef(p.getProperty(stringKey));
                    ignoredStores.add(store);
                }
                if(stringKey.startsWith("alfresco.index.tenant"))
                {
                    indexedTenants.add(p.getProperty(stringKey));
                }
                if(stringKey.startsWith("alfresco.ignore.tenant"))
                {
                    ignoredTenants.add(p.getProperty(stringKey));
                }
                if(stringKey.startsWith("alfresco.index.datatype"))
                {
                    QName qname = expandQName(p.getProperty(stringKey));
                    indexedDataTypes.add(qname);
                }
                if(stringKey.startsWith("alfresco.ignore.datatype"))
                {
                    QName qname = expandQName(p.getProperty(stringKey));
                    ignoredDataTypes.add(qname);
                }
                if(stringKey.startsWith("alfresco.index.type"))
                {
                    QName qname = expandQName(p.getProperty(stringKey));
                    indexedTypes.add(qname);
                }
                if(stringKey.startsWith("alfresco.ignore.type"))
                {
                    QName qname = expandQName(p.getProperty(stringKey));
                    ignoredTypes.add(qname);
                }
                if(stringKey.startsWith("alfresco.index.aspect"))
                {
                    QName qname = expandQName(p.getProperty(stringKey));
                    indexedAspects.add(qname);
                }
                if(stringKey.startsWith("alfresco.ignore.aspect"))
                {
                    QName qname = expandQName(p.getProperty(stringKey));
                    ignoredAspects.add(qname);
                }
                if(stringKey.startsWith("alfresco.index.field"))
                {
                    String name = expandName(p.getProperty(stringKey));
                    indexedFields.add(name);
                }
                if(stringKey.startsWith("alfresco.ignore.field"))
                {
                    String name = expandName(p.getProperty(stringKey));
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
                eq = "{" + dataModel.getNamespaceDAO().getNamespaceURI("") + "}" + q;
            }
            else
            {
                // find the prefix
                eq = "{" + dataModel.getNamespaceDAO().getNamespaceURI(q.substring(0, colonPosition)) + "}" + q.substring(colonPosition + 1);
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
                eq = "{" + dataModel.getNamespaceDAO().getNamespaceURI("") + "}" + q;
            }
            else
            {
                // find the prefix
                eq = "{" + dataModel.getNamespaceDAO().getNamespaceURI(q.substring(0, colonPosition)) + "}" + q.substring(colonPosition + 1);
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

        final String prefix = modelName.toPrefixString(dataModel.getNamespaceDAO()).replace(":", ".") + ".";
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
        long start = System.nanoTime();
        for (AclReaders aclReaders : aclReaderList)
        {
            AddUpdateCommand cmd = new AddUpdateCommand();
            cmd.overwriteCommitted = overwrite;
            cmd.overwritePending = overwrite;
            SolrInputDocument input = new SolrInputDocument();
            input.addField(QueryConstants.FIELD_ID, "ACL-" + aclReaders.getId());
            input.addField(QueryConstants.FIELD_ACLID, aclReaders.getId());
            input.addField(QueryConstants.FIELD_INACLTXID, aclReaders.getAclChangeSetId());
            String tenant = aclReaders.getTenantDomain();
            for (String reader : aclReaders.getReaders())
            {
                switch(AuthorityType.getAuthorityType(reader))
                {
                case USER:
                    input.addField(QueryConstants.FIELD_READER, reader);
                    break;
                case GROUP:
                case EVERYONE:
                case GUEST:
                    if(tenant.length() == 0)
                    {
                        // Default tenant matches 4.0 
                        input.addField(QueryConstants.FIELD_READER, reader);
                    }
                    else
                    {
                        input.addField(QueryConstants.FIELD_READER, reader+"@"+tenant);
                    }
                    break;
                default:
                    input.addField(QueryConstants.FIELD_READER, reader);
                    break;
                }
            }
            cmd.solrDoc = input;
            cmd.doc = CoreTracker.toDocument(cmd.getSolrInputDocument(), core.getSchema(), dataModel);
            core.getUpdateHandler().addDoc(cmd);
        }

        long end = System.nanoTime();
        trackerStats.addAclTime(end-start);
    }

    /**
     * @param dataModel
     * @param info
     * @throws IOException
     */
    protected void indexTransaction(Transaction info, boolean overwrite) throws IOException
    {
        AddUpdateCommand cmd = new AddUpdateCommand();
        cmd.overwriteCommitted = overwrite;
        cmd.overwritePending = overwrite;
        SolrInputDocument input = new SolrInputDocument();
        input.addField(QueryConstants.FIELD_ID, "TX-" + info.getId());
        input.addField(QueryConstants.FIELD_TXID, info.getId());
        input.addField(QueryConstants.FIELD_INTXID, info.getId());
        input.addField(QueryConstants.FIELD_TXCOMMITTIME, info.getCommitTimeMs());
        cmd.solrDoc = input;
        cmd.doc = CoreTracker.toDocument(cmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(cmd);
    }

    /**
     * @param dataModel
     * @param info
     * @throws IOException
     */
    protected void indexAclTransaction(AclChangeSet changeSet, boolean overwrite) throws IOException
    {
        AddUpdateCommand cmd = new AddUpdateCommand();
        cmd.overwriteCommitted = overwrite;
        cmd.overwritePending = overwrite;
        SolrInputDocument input = new SolrInputDocument();
        input.addField(QueryConstants.FIELD_ID, "ACLTX-" + changeSet.getId());
        input.addField(QueryConstants.FIELD_ACLTXID, changeSet.getId());
        input.addField(QueryConstants.FIELD_INACLTXID, changeSet.getId());
        input.addField(QueryConstants.FIELD_ACLTXCOMMITTIME, changeSet.getCommitTimeMs());
        cmd.solrDoc = input;
        cmd.doc = CoreTracker.toDocument(cmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(cmd);
    }

    /**
     * @param dataModel
     * @param node
     * @throws IOException
     * @throws JSONException 
     * @throws AuthenticationException 
     */
    protected void indexNode(Node node, SolrIndexSearcher solrIndexSearcher, boolean overwrite) throws IOException, AuthenticationException, JSONException
    {
        try
        {
            long start = System.nanoTime();

            if ((node.getStatus() == SolrApiNodeStatus.DELETED) || (node.getStatus() == SolrApiNodeStatus.UNKNOWN))
            {
                // fix up any secondary paths
                NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
                nmdp.setFromNodeId(node.getId());
                nmdp.setToNodeId(node.getId());
                List<NodeMetaData> nodeMetaDatas;
                if (node.getStatus() == SolrApiNodeStatus.DELETED)
                {
                    // Fake the empty node metadata for this parent deleted node
                    NodeMetaData nodeMetaData = new NodeMetaData();
                    nodeMetaData.setId(node.getId());
                    nodeMetaData.setType(ContentModel.TYPE_DELETED);
                    nodeMetaData.setNodeRef(new NodeRef(node.getNodeRef()));
                    nodeMetaData.setTxnId(node.getTxnId());
                    nodeMetaDatas = Collections.singletonList(nodeMetaData);                    
                }
                else
                {
                    nodeMetaDatas = client.getNodesMetaData(nmdp, 1);
                }
                for (NodeMetaData nodeMetaData : nodeMetaDatas)
                {
                    if(nodeMetaData.getTxnId() > node.getTxnId())
                    {
                        // the node has moved on to a later transaction
                        // it will be indexed later
                        continue;
                    }
                    LinkedHashSet<Long> visited = new LinkedHashSet<Long>();
                    updateDescendantAuxDocs(nodeMetaData, overwrite, solrIndexSearcher, visited);
                }


                log.debug(".. deleting");
                DeleteUpdateCommand docCmd = new DeleteUpdateCommand();
                docCmd.id = "LEAF-" + node.getId();
                docCmd.fromPending = true;
                docCmd.fromCommitted = true;
                core.getUpdateHandler().delete(docCmd);

                docCmd = new DeleteUpdateCommand();
                docCmd.id = "AUX-" + node.getId();
                docCmd.fromPending = true;
                docCmd.fromCommitted = true;
                core.getUpdateHandler().delete(docCmd);

                docCmd = new DeleteUpdateCommand();
                docCmd.id = "UNINDEXED-" + node.getId();
                docCmd.fromPending = true;
                docCmd.fromCommitted = true;
                core.getUpdateHandler().delete(docCmd);

                docCmd = new DeleteUpdateCommand();
                docCmd.id = "ERROR-" + node.getId();
                docCmd.fromPending = true;
                docCmd.fromCommitted = true;
                core.getUpdateHandler().delete(docCmd);


            }

            if ((node.getStatus() == SolrApiNodeStatus.UPDATED)  || (node.getStatus() == SolrApiNodeStatus.UNKNOWN))
            {

                log.info(".. updating");
                NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
                nmdp.setFromNodeId(node.getId());
                nmdp.setToNodeId(node.getId());

                List<NodeMetaData> nodeMetaDatas = client.getNodesMetaData(nmdp, 1);

                AddUpdateCommand leafDocCmd = new AddUpdateCommand();
                leafDocCmd.overwriteCommitted = overwrite;
                leafDocCmd.overwritePending = overwrite;
                AddUpdateCommand auxDocCmd = new AddUpdateCommand();
                auxDocCmd.overwriteCommitted = overwrite;
                auxDocCmd.overwritePending = overwrite;

                ArrayList<Reader> toClose = new ArrayList<Reader>();
                ArrayList<File> toDelete = new ArrayList<File>();

                for (NodeMetaData nodeMetaData : nodeMetaDatas)
                {
                    if(nodeMetaData.getTxnId() > node.getTxnId())
                    {
                        // the node has moved on to a later transaction
                        // it will be indexed later
                        continue;
                    }

                    if (mayHaveChildren(nodeMetaData))
                    {
                        log.info(".. checking for path change");
                        BooleanQuery bQuery = new BooleanQuery();
                        bQuery.add(new TermQuery(new Term(QueryConstants.FIELD_DBID, NumericEncoder.encode(nodeMetaData.getId()))), Occur.MUST);
                        bQuery.add(new TermQuery(new Term(QueryConstants.FIELD_PARENT_ASSOC_CRC, NumericEncoder.encode(nodeMetaData.getParentAssocsCrc()))), Occur.MUST);
                        DocSet docSet = solrIndexSearcher.getDocSet(bQuery);
                        if (docSet.size() > 0)
                        {
                            log.debug("... found aux match");
                        }
                        else
                        {
                            docSet = solrIndexSearcher.getDocSet(new TermQuery(new Term(QueryConstants.FIELD_DBID, NumericEncoder.encode(nodeMetaData.getId()))));
                            if (docSet.size() > 0)
                            {
                                log.debug("... cascade updating aux doc");
                                LinkedHashSet<Long> visited = new LinkedHashSet<Long>();
                                updateDescendantAuxDocs(nodeMetaData, overwrite, solrIndexSearcher, visited);
                            }
                            else
                            {
                                log.debug("... no aux doc");
                            }
                        }
                    }

                    Map<QName, PropertyValue> properties = nodeMetaData.getProperties();

                    // check index control

                    if (properties.containsKey(ContentModel.PROP_IS_INDEXED))
                    {
                        StringPropertyValue pValue = (StringPropertyValue) properties.get(ContentModel.PROP_IS_INDEXED);
                        if (pValue != null)
                        {
                            Boolean isIndexed = Boolean.valueOf(pValue.getValue());
                            if ((isIndexed != null) && (isIndexed.booleanValue() == false))
                            {
                                DeleteUpdateCommand docCmd = new DeleteUpdateCommand();
                                docCmd.id = "LEAF-" + node.getId();
                                docCmd.fromPending = true;
                                docCmd.fromCommitted = true;
                                core.getUpdateHandler().delete(docCmd);

                                docCmd = new DeleteUpdateCommand();
                                docCmd.id = "AUX-" + node.getId();
                                docCmd.fromPending = true;
                                docCmd.fromCommitted = true;
                                core.getUpdateHandler().delete(docCmd);

                                docCmd = new DeleteUpdateCommand();
                                docCmd.id = "ERROR-" + node.getId();
                                docCmd.fromPending = true;
                                docCmd.fromCommitted = true;
                                core.getUpdateHandler().delete(docCmd);

                                SolrInputDocument doc = new SolrInputDocument();
                                doc.addField(QueryConstants.FIELD_ID, "UNINDEXED-" + nodeMetaData.getId());
                                doc.addField(QueryConstants.FIELD_DBID, nodeMetaData.getId());
                                doc.addField(QueryConstants.FIELD_LID, nodeMetaData.getNodeRef());
                                doc.addField(QueryConstants.FIELD_INTXID, nodeMetaData.getTxnId());

                                leafDocCmd.solrDoc = doc;
                                leafDocCmd.doc = CoreTracker.toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

                                if(leafDocCmd.doc != null)
                                {
                                    core.getUpdateHandler().addDoc(leafDocCmd);
                                }

                                long end = System.nanoTime();
                                trackerStats.addNodeTime(end-start);
                                return;
                            }
                        }
                    }

                    boolean isContentIndexedForNode = true;
                    if (properties.containsKey(ContentModel.PROP_IS_CONTENT_INDEXED))
                    {
                        StringPropertyValue pValue = (StringPropertyValue) properties.get(ContentModel.PROP_IS_CONTENT_INDEXED);
                        if (pValue != null)
                        {
                            Boolean isIndexed = Boolean.valueOf(pValue.getValue());
                            if ((isIndexed != null) && (isIndexed.booleanValue() == false))
                            {
                                isContentIndexedForNode = false;
                            }
                        }
                    }

                    // Make sure any unindexed doc is removed.
                    DeleteUpdateCommand docCmd = new DeleteUpdateCommand();
                    docCmd.id = "UNINDEXED-" + node.getId();
                    docCmd.fromPending = true;
                    docCmd.fromCommitted = true;
                    core.getUpdateHandler().delete(docCmd);

                    docCmd = new DeleteUpdateCommand();
                    docCmd.id = "ERROR-" + node.getId();
                    docCmd.fromPending = true;
                    docCmd.fromCommitted = true;
                    core.getUpdateHandler().delete(docCmd);

                    SolrInputDocument doc = new SolrInputDocument();
                    doc.addField(QueryConstants.FIELD_ID, "LEAF-" + nodeMetaData.getId());
                    doc.addField(QueryConstants.FIELD_DBID, nodeMetaData.getId());
                    doc.addField(QueryConstants.FIELD_LID, nodeMetaData.getNodeRef());
                    doc.addField(QueryConstants.FIELD_INTXID, nodeMetaData.getTxnId());

                    for (QName propertyQname : properties.keySet())
                    {
                        if(dataModel.isIndexedOrStored(propertyQname))
                        {
                            PropertyValue value = properties.get(propertyQname);
                            if (value != null)
                            {
                                if (value instanceof ContentPropertyValue)
                                {
                                    if (isContentIndexedForNode)
                                    {
                                        addContentPropertyToDoc(doc, toClose, toDelete, nodeMetaData, propertyQname, (ContentPropertyValue) value);
                                    }
                                }
                                else if (value instanceof MLTextPropertyValue)
                                {
                                    addMLTextPropertyToDoc(doc, propertyQname, (MLTextPropertyValue) value);
                                }
                                else if (value instanceof MultiPropertyValue)
                                {
                                    MultiPropertyValue typedValue = (MultiPropertyValue) value;
                                    for (PropertyValue singleValue : typedValue.getValues())
                                    {
                                        if (singleValue instanceof ContentPropertyValue)
                                        {
                                            if (isContentIndexedForNode)
                                            {
                                                addContentPropertyToDoc(doc, toClose, toDelete, nodeMetaData, propertyQname, (ContentPropertyValue) singleValue);
                                            }
                                        }
                                        else if (singleValue instanceof MLTextPropertyValue)
                                        {
                                            addMLTextPropertyToDoc(doc, propertyQname, (MLTextPropertyValue) singleValue);

                                        }
                                        else if (singleValue instanceof StringPropertyValue)
                                        {
                                            addStringPropertyToDoc(doc, propertyQname, (StringPropertyValue) singleValue, properties);
                                        }
                                    }
                                }
                                else if (value instanceof StringPropertyValue)
                                {
                                    addStringPropertyToDoc(doc, propertyQname, (StringPropertyValue) value, properties);
                                }

                            }
                        }
                    }
                    doc.addField(QueryConstants.FIELD_TYPE, nodeMetaData.getType().toString());
                    for (QName aspect : nodeMetaData.getAspects())
                    {
                        doc.addField(QueryConstants.FIELD_ASPECT, aspect.toString());
                    }
                    doc.addField(QueryConstants.FIELD_ISNODE, "T");
                    doc.addField(QueryConstants.FIELD_FTSSTATUS, "Clean");
                    // TODO: Place holder to test tenant queries
                    String tenant =  nodeMetaData.getTenantDomain();
                    if(tenant.length() > 0)
                    {
                        doc.addField(QueryConstants.FIELD_TENANT, nodeMetaData.getTenantDomain());
                    }
                    else
                    {
                        doc.addField(QueryConstants.FIELD_TENANT, "_DEFAULT_");
                    }

                    leafDocCmd.solrDoc = doc;
                    leafDocCmd.doc = CoreTracker.toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

                    SolrInputDocument aux = createAuxDoc(nodeMetaData);
                    auxDocCmd.solrDoc = aux;
                    auxDocCmd.doc = CoreTracker.toDocument(auxDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

                }

                if(leafDocCmd.doc != null)
                {
                    core.getUpdateHandler().addDoc(leafDocCmd);
                }
                if(auxDocCmd.doc != null)
                {    
                    core.getUpdateHandler().addDoc(auxDocCmd);
                }

                for (Reader forClose : toClose)
                {
                    try
                    {
                        forClose.close();
                    }
                    catch (IOException ioe)
                    {
                    }

                }

                for (File file : toDelete)
                {
                    file.delete();
                }

            }
            long end = System.nanoTime();
            trackerStats.addNodeTime(end-start);
        }
        catch(Exception e)
        {
            // generic recovery 
            // Add failed node marker to try later
            // TODO: add to reporting
            // TODO: Store exception for display via query
            // TODO: retry failed

            DeleteUpdateCommand docCmd = new DeleteUpdateCommand();
            docCmd.id = "LEAF-" + node.getId();
            docCmd.fromPending = true;
            docCmd.fromCommitted = true;
            core.getUpdateHandler().delete(docCmd);

            docCmd = new DeleteUpdateCommand();
            docCmd.id = "AUX-" + node.getId();
            docCmd.fromPending = true;
            docCmd.fromCommitted = true;
            core.getUpdateHandler().delete(docCmd);

            docCmd = new DeleteUpdateCommand();
            docCmd.id = "UNINDEXED-" + node.getId();
            docCmd.fromPending = true;
            docCmd.fromCommitted = true;
            core.getUpdateHandler().delete(docCmd);

            AddUpdateCommand leafDocCmd = new AddUpdateCommand();
            leafDocCmd.overwriteCommitted = overwrite;
            leafDocCmd.overwritePending = overwrite;

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField(QueryConstants.FIELD_ID, "ERROR-" + node.getId());
            doc.addField(QueryConstants.FIELD_DBID, node.getId());
            doc.addField(QueryConstants.FIELD_INTXID, node.getTxnId());
            doc.addField(QueryConstants.FIELD_EXCEPTION_MESSAGE, e.getMessage());

            StringWriter stringWriter = new StringWriter(4096);
            PrintWriter printWriter = new PrintWriter(stringWriter, true);
            try
            {
                e.printStackTrace(printWriter);
                doc.addField(QueryConstants.FIELD_EXCEPTION_STACK, stringWriter.toString());
            }
            finally
            {
                printWriter.close();
            }



            leafDocCmd.solrDoc = doc;
            leafDocCmd.doc = CoreTracker.toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

            if(leafDocCmd.doc != null)
            {
                core.getUpdateHandler().addDoc(leafDocCmd);
            }

            log.warn("Node index failed and skipped for " + node.getId() + " in Tx "+node.getTxnId(), e);
        }

    }

    private void updateDescendantAuxDocs(NodeMetaData parentNodeMetaData, boolean overwrite, SolrIndexSearcher solrIndexSearcher, LinkedHashSet<Long> stack) throws AuthenticationException, IOException, JSONException
    {
        if(stack.contains(parentNodeMetaData.getId()))
        {
            log.warn("Found aux data loop for node id "+parentNodeMetaData.getId());
            log.warn("... stack to node ="+stack);
            return;
        }
        else
        {
            try
            {
                stack.add(parentNodeMetaData.getId());
                doUpdateDescendantAuxDocs(parentNodeMetaData, overwrite, solrIndexSearcher, stack);
            }
            finally
            {
                stack.remove(parentNodeMetaData.getId());
            }
            
        }
        
    }
        
    private void doUpdateDescendantAuxDocs(NodeMetaData parentNodeMetaData, boolean overwrite, SolrIndexSearcher solrIndexSearcher, LinkedHashSet<Long> stack) throws AuthenticationException, IOException, JSONException
    {
        HashSet<Long>childIds = new HashSet<Long>();

        if (parentNodeMetaData.getChildIds() != null)
        {
            childIds.addAll(parentNodeMetaData.getChildIds());
        }

        BooleanQuery bQuery = new BooleanQuery();
        bQuery.add(new TermQuery(new Term(QueryConstants.FIELD_PARENT, parentNodeMetaData.getNodeRef().toString())), Occur.MUST);
        DocSet docSet = solrIndexSearcher.getDocSet(bQuery);
        ResizeableArrayList<CacheEntry> indexedByDocId = (ResizeableArrayList<CacheEntry>) solrIndexSearcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_ARRAYLIST_CACHE, AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_DOC_ID);
        if (docSet instanceof BitDocSet)
        {
            BitDocSet source = (BitDocSet) docSet;
            OpenBitSet openBitSet = source.getBits();
            int current = -1;
            while ((current = openBitSet.nextSetBit(current + 1)) != -1)
            {
                CacheEntry entry = indexedByDocId.get(current);
                childIds.add(entry.getDbid());
            }
        }
        else
        {
            for (DocIterator it = docSet.iterator(); it.hasNext(); /* */)
            {
                CacheEntry entry = indexedByDocId.get(it.nextDoc());
                childIds.add(entry.getDbid());
            }
        }

        for (Long childId : childIds)
        { 
            NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
            nmdp.setFromNodeId(childId);
            nmdp.setToNodeId(childId);
            nmdp.setIncludeAclId(true);
            nmdp.setIncludeAspects(true);
            nmdp.setIncludeChildAssociations(false);
            nmdp.setIncludeChildIds(true);
            nmdp.setIncludeNodeRef(true);
            nmdp.setIncludeOwner(true);
            nmdp.setIncludeParentAssociations(true);
            nmdp.setIncludePaths(true);
            nmdp.setIncludeProperties(false);
            nmdp.setIncludeType(true);
            nmdp.setIncludeTxnId(true);
            List<NodeMetaData> nodeMetaDatas = client.getNodesMetaData(nmdp, 1);

            for (NodeMetaData nodeMetaData : nodeMetaDatas)
            {
                if (mayHaveChildren(nodeMetaData))
                {
                    updateDescendantAuxDocs(nodeMetaData, overwrite, solrIndexSearcher, stack);
                }

                // Avoid adding aux docs for stuff yet to be indexed or unindexed (via the index control aspect)
                log.info(".. checking aux doc exists in index before we update it");
                Query query = new TermQuery(new Term(QueryConstants.FIELD_ID, "AUX-" + childId));
                DocSet auxSet = solrIndexSearcher.getDocSet(query);
                if (auxSet.size() > 0)
                {
                    log.debug("... cascade update aux doc "+childId);

                    SolrInputDocument aux = createAuxDoc(nodeMetaData);
                    AddUpdateCommand auxDocCmd = new AddUpdateCommand();
                    auxDocCmd.overwriteCommitted = overwrite;
                    auxDocCmd.overwritePending = overwrite;
                    auxDocCmd.solrDoc = aux;
                    auxDocCmd.doc = CoreTracker.toDocument(auxDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

                    core.getUpdateHandler().addDoc(auxDocCmd);
                }
                else
                {
                    log.debug("... no aux doc found to update "+childId);
                }   
            }
        }
    }



    private SolrInputDocument createAuxDoc(NodeMetaData nodeMetaData)
    {
        SolrInputDocument aux = new SolrInputDocument();
        aux.addField(QueryConstants.FIELD_ID, "AUX-" + nodeMetaData.getId());
        aux.addField(QueryConstants.FIELD_DBID, nodeMetaData.getId());
        aux.addField(QueryConstants.FIELD_ACLID, nodeMetaData.getAclId());
        aux.addField(QueryConstants.FIELD_INTXID, nodeMetaData.getTxnId());

        for (Pair<String, QName> path : nodeMetaData.getPaths())
        {
            aux.addField(QueryConstants.FIELD_PATH, path.getFirst());
        }

        if (nodeMetaData.getOwner() != null)
        {
            aux.addField(QueryConstants.FIELD_OWNER, nodeMetaData.getOwner());
        }
        aux.addField(QueryConstants.FIELD_PARENT_ASSOC_CRC, nodeMetaData.getParentAssocsCrc());

        StringBuilder qNameBuffer = new StringBuilder(64);
        StringBuilder assocTypeQNameBuffer = new StringBuilder(64);
        if (nodeMetaData.getParentAssocs() != null)
        {
            for (ChildAssociationRef childAssocRef : nodeMetaData.getParentAssocs())
            {
                if (qNameBuffer.length() > 0)
                {
                    qNameBuffer.append(";/");
                    assocTypeQNameBuffer.append(";/");
                }
                qNameBuffer.append(ISO9075.getXPathName(childAssocRef.getQName()));
                assocTypeQNameBuffer.append(ISO9075.getXPathName(childAssocRef.getTypeQName()));
                aux.addField(QueryConstants.FIELD_PARENT, childAssocRef.getParentRef());

                if (childAssocRef.isPrimary())
                {
                    aux.addField(QueryConstants.FIELD_PRIMARYPARENT, childAssocRef.getParentRef());
                    aux.addField(QueryConstants.FIELD_PRIMARYASSOCTYPEQNAME, ISO9075.getXPathName(childAssocRef.getTypeQName()));
                    aux.addField(QueryConstants.FIELD_PRIMARYASSOCQNAME, ISO9075.getXPathName(childAssocRef.getQName()));

                }
            }
            aux.addField(QueryConstants.FIELD_ASSOCTYPEQNAME, assocTypeQNameBuffer.toString());
            aux.addField(QueryConstants.FIELD_QNAME, qNameBuffer.toString());
        }
        if(nodeMetaData.getAncestors() != null)
        {
            for(NodeRef ancestor : nodeMetaData.getAncestors())
            {
                aux.addField(QueryConstants.FIELD_ANCESTOR, ancestor.toString());
            }
        }
        return aux;
    }

    private boolean mayHaveChildren(NodeMetaData nodeMetaData)
    {
        // 1) Does the type support children?
        TypeDefinition nodeTypeDef = dataModel.getDictionaryService(CMISStrictDictionaryService.DEFAULT).getType(nodeMetaData.getType());
        if ((nodeTypeDef != null) && (nodeTypeDef.getChildAssociations().size() > 0))
        {
            return true;
        }
        // 2) Do any of the applied aspects support children?
        for (QName aspect : nodeMetaData.getAspects())
        {
            AspectDefinition aspectDef = dataModel.getDictionaryService(CMISStrictDictionaryService.DEFAULT).getAspect(aspect);
            if ((aspectDef != null) && (aspectDef.getChildAssociations().size() > 0))
            {
                return true;
            }
        }
        return false;
    }

    private void addContentPropertyToDoc(SolrInputDocument doc, ArrayList<Reader> toClose, ArrayList<File> toDelete, NodeMetaData nodeMetaData, QName propertyQName,
            ContentPropertyValue contentPropertyValue) throws AuthenticationException, IOException
            {

        if(indexedDataTypes.size() > 0 && !indexedDataTypes.contains(DataTypeDefinition.CONTENT))
        {
            return;
        }
        if(ignoredDataTypes.contains(DataTypeDefinition.CONTENT))
        {
            return;
        }


        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".size", contentPropertyValue.getLength());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".locale", contentPropertyValue.getLocale());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".mimetype", contentPropertyValue.getMimetype());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".encoding", contentPropertyValue.getEncoding());

        if(false == transformContent)
        {
            return;
        }

        long start = System.nanoTime();
        GetTextContentResponse response = client.getTextContent(nodeMetaData.getId(), propertyQName, null);
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationStatus", response.getStatus());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationTime", response.getTransformDuration());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationException", response.getTransformException());

        InputStreamReader isr = null;
        InputStream ris = response.getContent();
        File temp = null;
        try
        {
            if (ris != null)
            {
                // Get and copy content
                temp = TempFileProvider.createTempFile("solr", "content");
                toDelete.add(temp);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(temp));
                FileCopyUtils.copy(ris, os);
            }
        }
        finally
        {
            // release the response only when the content has been read
            response.release();
        }

        long end = System.nanoTime();
        trackerStats.addDocTransformationTime(end-start);

        if (ris != null)
        {
            // Localised
            ris = new BufferedInputStream(new FileInputStream(temp));

            try
            {
                isr = new InputStreamReader(ris, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                isr = new InputStreamReader(ris);
            }
            toClose.add(isr);

            StringBuilder builder = new StringBuilder();
            builder.append("\u0000").append(contentPropertyValue.getLocale().toString()).append("\u0000");
            StringReader prefix = new StringReader(builder.toString());
            Reader multiReader = new MultiReader(prefix, isr);
            doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), multiReader);

            // Cross language search support
            ris = new BufferedInputStream(new FileInputStream(temp));

            try
            {
                isr = new InputStreamReader(ris, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                isr = new InputStreamReader(ris);
            }
            toClose.add(isr);

            builder = new StringBuilder();
            builder.append("\u0000").append(contentPropertyValue.getLocale().toString()).append("\u0000");
            prefix = new StringReader(builder.toString());
            multiReader = new MultiReader(prefix, isr);
            doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__", multiReader);
        }
            }

    private void addMLTextPropertyToDoc(SolrInputDocument doc, QName propertyQName, MLTextPropertyValue mlTextPropertyValue) throws IOException
    {
        PropertyDefinition propertyDefinition = dataModel.getPropertyDefinition(propertyQName);
        if (propertyDefinition != null)
        {
            StringBuilder sort = new StringBuilder();
            for (Locale locale : mlTextPropertyValue.getLocales())
            {
                StringBuilder builder = new StringBuilder();
                builder.append("\u0000").append(locale.toString()).append("\u0000").append(mlTextPropertyValue.getValue(locale));

                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.TRUE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__", builder.toString());
                }
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".u", builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__.u", builder.toString());
                }

                if (sort.length() > 0)
                {
                    sort.append("\u0000");
                }
                sort.append(builder.toString());
            }

            if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort", sort.toString());
            }
        }
        else
        {
            for (Locale locale : mlTextPropertyValue.getLocales())
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), mlTextPropertyValue.getValue(locale));
            }
        }

    }

    private void addStringPropertyToDoc(SolrInputDocument doc, QName propertyQName, StringPropertyValue stringPropertyValue, Map<QName, PropertyValue> properties)
            throws IOException
            {
        PropertyDefinition propertyDefinition = dataModel.getPropertyDefinition(propertyQName);
        if (propertyDefinition != null)
        {
            if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), stringPropertyValue.getValue());
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort", stringPropertyValue.getValue());
            }
            else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
            {
                Locale locale = null;

                PropertyValue localePropertyValue = properties.get(ContentModel.PROP_LOCALE);
                if (localePropertyValue != null)
                {
                    locale = DefaultTypeConverter.INSTANCE.convert(Locale.class, ((StringPropertyValue) localePropertyValue).getValue());
                }

                if (locale == null)
                {
                    locale = I18NUtil.getLocale();
                }

                StringBuilder builder;
                builder = new StringBuilder();
                builder.append("\u0000").append(locale.toString()).append("\u0000").append(stringPropertyValue.getValue());
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.TRUE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__", builder.toString());
                }
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".u", builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__.u", builder.toString());
                }

                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort", builder.toString());
                }

            }
            else
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), stringPropertyValue.getValue());
            }

        }
        else
        {
            doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), stringPropertyValue.getValue());
        }
            }

    /**
     * @param refCounted
     * @throws IOException
     * @throws JSONException
     */
    public IndexHealthReport checkIndex(Long fromTx, Long toTx, Long fromAclTx, Long toAclTx, Long fromTime, Long toTime) throws AuthenticationException, IOException,
    JSONException
    {

        IndexHealthReport indexHealthReport = new IndexHealthReport();
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
        OpenBitSet txIdsInDb = new OpenBitSet();
        Long lastTxCommitTime = Long.valueOf(firstTransactionCommitTime);
        if (fromTime != null)
        {
            lastTxCommitTime = fromTime;
        }
        long maxTxId = 0;

        long loopStartingCommitTime;
        Transactions transactions;
        BoundedDeque<Transaction> txnsFound = new  BoundedDeque<Transaction>(100);
        long endTime = System.currentTimeMillis() + holeRetention;
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

        OpenBitSet aclTxIdsInDb = new OpenBitSet();
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

        OpenBitSet txIdsInIndex = new OpenBitSet();

        OpenBitSet aclTxIdsInIndex = new OpenBitSet();

        RefCounted<SolrIndexSearcher> refCounted = core.getSearcher(false, true, null);

        if (refCounted == null)
        {
            return indexHealthReport;
        }

        try
        {
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            SolrIndexReader reader = solrIndexSearcher.getReader();

            // Index TX Count
            if (minTxId != null)
            {
                TermDocs termDocs = null;
                int count = 0;
                for (long i = minTxId; i <= maxTxId; i++)
                {
                    int docCount = 0;
                    String target = NumericEncoder.encode(i);
                    Term term = new Term(QueryConstants.FIELD_TXID, target);
                    if (termDocs == null)
                    {
                        termDocs = reader.termDocs(term);
                    }
                    else
                    {
                        termDocs.seek(term);
                    }
                    while (termDocs.next())
                    {
                        int doc = termDocs.doc();
                        if (!reader.isDeleted(doc))
                        {
                            docCount++;
                        }
                    }

                    if (docCount == 0)
                    {
                        if (txIdsInDb.get(i))
                        {
                            indexHealthReport.setMissingTxFromIndex(i);
                        }
                    }
                    else if (docCount == 1)
                    {
                        txIdsInIndex.set(i);
                        if (!txIdsInDb.get(i))
                        {
                            indexHealthReport.setTxInIndexButNotInDb(i);
                        }
                        count++;
                    }
                    else if (docCount > 1)
                    {
                        indexHealthReport.setDuplicatedTxInIndex(i);
                        if (!txIdsInDb.get(i))
                        {
                            indexHealthReport.setTxInIndexButNotInDb(i);
                        }
                        count++;
                    }

                }
                if (termDocs != null)
                {
                    termDocs.close();
                }

                indexHealthReport.setUniqueTransactionDocsInIndex(txIdsInIndex.cardinality());
                indexHealthReport.setTransactionDocsInIndex(count);
            }

            // ACL TX

            if (minAclTxId != null)
            {
                TermDocs termDocs = null;
                int count = 0;
                for (long i = minAclTxId; i <= maxAclTxId; i++)
                {
                    int docCount = 0;
                    String target = NumericEncoder.encode(i);
                    Term term = new Term(QueryConstants.FIELD_ACLTXID, target);
                    if (termDocs == null)
                    {
                        termDocs = reader.termDocs(term);
                    }
                    else
                    {
                        termDocs.seek(term);
                    }
                    while (termDocs.next())
                    {
                        int doc = termDocs.doc();
                        if (!reader.isDeleted(doc))
                        {
                            docCount++;
                        }
                    }

                    if (docCount == 0)
                    {
                        if (aclTxIdsInDb.get(i))
                        {
                            indexHealthReport.setMissingAclTxFromIndex(i);
                        }
                    }
                    else if (docCount == 1)
                    {
                        aclTxIdsInIndex.set(i);
                        if (!aclTxIdsInDb.get(i))
                        {
                            indexHealthReport.setAclTxInIndexButNotInDb(i);
                        }
                        count++;
                    }
                    else if (docCount > 1)
                    {
                        indexHealthReport.setDuplicatedAclTxInIndex(i);
                        if (!aclTxIdsInDb.get(i))
                        {
                            indexHealthReport.setAclTxInIndexButNotInDb(i);
                        }
                        count++;
                    }

                }
                if (termDocs != null)
                {
                    termDocs.close();
                }

                indexHealthReport.setUniqueAclTransactionDocsInIndex(aclTxIdsInIndex.cardinality());
                indexHealthReport.setAclTransactionDocsInIndex(count);
            }

            // LEAF

            int leafCount = 0;
            TermEnum termEnum = reader.terms(new Term(QueryConstants.FIELD_ID, "LEAF-"));
            do
            {
                Term term = termEnum.term();
                if (term.field().equals(QueryConstants.FIELD_ID) && term.text().startsWith("LEAF-"))
                {
                    int docCount = 0;
                    TermDocs termDocs = reader.termDocs(new Term(QueryConstants.FIELD_ID, term.text()));
                    while (termDocs.next())
                    {
                        if (!reader.isDeleted(termDocs.doc()))
                        {
                            docCount++;
                        }
                    }
                    if (docCount > 1)
                    {
                        long txid = Long.parseLong(term.text().substring(5));
                        indexHealthReport.setDuplicatedLeafInIndex(txid);
                    }

                    leafCount += docCount;
                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();

            indexHealthReport.setLeafDocCountInIndex(leafCount);

            // AUX

            int auxCount = 0;
            termEnum = reader.terms(new Term(QueryConstants.FIELD_ID, "AUX-"));
            do
            {
                Term term = termEnum.term();
                if (term.field().equals(QueryConstants.FIELD_ID) && term.text().startsWith("AUX-"))
                {
                    int docCount = 0;
                    TermDocs termDocs = reader.termDocs(new Term(QueryConstants.FIELD_ID, term.text()));
                    while (termDocs.next())
                    {
                        if (!reader.isDeleted(termDocs.doc()))
                        {
                            docCount++;
                        }
                    }
                    if (docCount > 1)
                    {
                        long txid = Long.parseLong(term.text().substring(4));
                        indexHealthReport.setDuplicatedAuxInIndex(txid);
                    }
    
                    auxCount += docCount;
                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();

            indexHealthReport.setAuxDocCountInIndex(auxCount);

            // ERROR

            int errorCount = 0;
            termEnum = reader.terms(new Term(QueryConstants.FIELD_ID, "ERROR-"));
            do
            {
                Term term = termEnum.term();
                if (term.field().equals(QueryConstants.FIELD_ID) && term.text().startsWith("ERROR-"))
                {
                    int docCount = 0;
                    TermDocs termDocs = reader.termDocs(new Term(QueryConstants.FIELD_ID, term.text()));
                    while (termDocs.next())
                    {
                        if (!reader.isDeleted(termDocs.doc()))
                        {
                            docCount++;
                        }
                    }
                    if (docCount > 1)
                    {
                        long txid = Long.parseLong(term.text().substring(6));
                        indexHealthReport.setDuplicatedErrorInIndex(txid);
                    }

                    errorCount += docCount;         
                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();

            indexHealthReport.setErrorDocCountInIndex(errorCount);

            // UNINDEXED

            int unindexedCount = 0;
            termEnum = reader.terms(new Term(QueryConstants.FIELD_ID, "UNINDEXED-"));
            do
            {
                Term term = termEnum.term();
                if (term.field().equals(QueryConstants.FIELD_ID) && term.text().startsWith("UNINDEXED-"))
                {
                    int docCount = 0;
                    TermDocs termDocs = reader.termDocs(new Term(QueryConstants.FIELD_ID, term.text()));
                    while (termDocs.next())
                    {
                        if (!reader.isDeleted(termDocs.doc()))
                        {
                            docCount++;
                        }
                    }
                    if (docCount > 1)
                    {
                        long txid = Long.parseLong(term.text().substring(10));
                        indexHealthReport.setDuplicatedUnindexedInIndex(txid);
                    }

                    unindexedCount += docCount;
                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();

            indexHealthReport.setUnindexedDocCountInIndex(unindexedCount);

            // Other

            indexHealthReport.setLastIndexedCommitTime(state.lastIndexedTxCommitTime);
            indexHealthReport.setLastIndexedIdBeforeHoles(state.lastIndexedTxIdBeforeHoles);

            return indexHealthReport;
        }
        finally
        {
            refCounted.decref();
        }

    }

    private long getLastTxCommitTimeBeforeHoles(SolrIndexReader reader, Long cutOffTime) throws IOException
    {
        long lastTxCommitTimeBeforeHoles = 0;

        TermEnum termEnum = reader.terms(new Term(QueryConstants.FIELD_TXCOMMITTIME, ""));
        do
        {
            Term term = termEnum.term();
            if (term == null)
            {
                break;
            }
            if (term.field().equals(QueryConstants.FIELD_TXCOMMITTIME))
            {
                Long txCommitTime = NumericEncoder.decodeLong(term.text());
                if (txCommitTime < cutOffTime)
                {
                    lastTxCommitTimeBeforeHoles = txCommitTime;
                }
                else
                {
                    break;
                }

            }
            else
            {
                break;
            }
        }
        while (termEnum.next());
        termEnum.close();
        return lastTxCommitTimeBeforeHoles;
    }

    public Set<Long> getErrorDocIds() throws IOException
    {
        HashSet<Long> errorDocIds = new HashSet<Long>();
        RefCounted<SolrIndexSearcher> refCounted = core.getSearcher(false, true, null);

        if (refCounted == null)
        {
            return errorDocIds;
        }

        try
        {
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            SolrIndexReader reader = solrIndexSearcher.getReader();
            TermEnum termEnum = reader.terms(new Term(QueryConstants.FIELD_ID, "ERROR-"));
            do
            {
                Term term = termEnum.term();
                if (term.field().equals(QueryConstants.FIELD_ID) && term.text().startsWith("ERROR-"))
                {
                    int docCount = 0;
                    TermDocs termDocs = reader.termDocs(new Term(QueryConstants.FIELD_ID, term.text()));
                    while (termDocs.next())
                    {
                        if (!reader.isDeleted(termDocs.doc()))
                        {
                            docCount++;
                        }
                    }

                    long txid = Long.parseLong(term.text().substring(6));
                    errorDocIds.add(txid);
                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();
        }
        finally
        {
            refCounted.decref();
        }
        return errorDocIds;
    }

    private long getLastChangeSetCommitTimeBeforeHoles(SolrIndexReader reader, Long cutOffTime) throws IOException
    {
        long lastTxCommitTimeBeforeHoles = 0;

        TermEnum termEnum = reader.terms(new Term(QueryConstants.FIELD_ACLTXCOMMITTIME, ""));
        do
        {
            Term term = termEnum.term();
            if (term == null)
            {
                break;
            }
            if (term.field().equals(QueryConstants.FIELD_ACLTXCOMMITTIME))
            {
                Long txCommitTime = NumericEncoder.decodeLong(term.text());
                if (txCommitTime < cutOffTime)
                {
                    lastTxCommitTimeBeforeHoles = txCommitTime;
                }
                else
                {
                    break;
                }

            }
            else
            {
                break;
            }
        }
        while (termEnum.next());
        termEnum.close();
        return lastTxCommitTimeBeforeHoles;
    }

    private long getLargestTxIdByCommitTime(SolrIndexReader reader, Long lastTxCommitTimeBeforeHoles) throws IOException
    {
        long txid = -1;
        if (lastTxCommitTimeBeforeHoles != -1)
        {
            TermDocs docs = reader.termDocs(new Term(QueryConstants.FIELD_TXCOMMITTIME, NumericEncoder.encode(lastTxCommitTimeBeforeHoles)));
            while (docs.next())
            {
                Document doc = reader.document(docs.doc());
                Fieldable field = doc.getFieldable(QueryConstants.FIELD_TXID);
                if (field != null)
                {
                    long currentTxId = Long.valueOf(field.stringValue());
                    if (currentTxId > txid)
                    {
                        txid = currentTxId;
                    }
                }
            }
        }
        return txid;
    }

    private long getLargestChangeSetIdByCommitTime(SolrIndexReader reader, Long lastChangeSetCommitTimeBeforeHoles) throws IOException
    {
        long txid = -1;
        if (lastChangeSetCommitTimeBeforeHoles != -1)
        {
            TermDocs docs = reader.termDocs(new Term(QueryConstants.FIELD_ACLTXCOMMITTIME, NumericEncoder.encode(lastChangeSetCommitTimeBeforeHoles)));
            while (docs.next())
            {
                Document doc = reader.document(docs.doc());
                Fieldable field = doc.getFieldable(QueryConstants.FIELD_ACLTXID);
                if (field != null)
                {
                    long currentTxId = Long.valueOf(field.stringValue());
                    if (currentTxId > txid)
                    {
                        txid = currentTxId;
                    }
                }
            }
        }
        return txid;
    }

    private long getLastTransactionCommitTime(SolrIndexReader reader) throws IOException
    {
        long lastTxCommitTime = 0;

        try
        {
            TermEnum termEnum = reader.terms(new Term(QueryConstants.FIELD_TXCOMMITTIME, ""));
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals(QueryConstants.FIELD_TXCOMMITTIME))
                {
                    Long txCommitTime = NumericEncoder.decodeLong(term.text());
                    lastTxCommitTime = txCommitTime;

                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();
        }
        catch (IOException e1)
        {

        }

        return lastTxCommitTime;
    }

    private long getLastTransactionId(SolrIndexReader reader) throws IOException
    {
        long lastTxCommitTime = 0;

        try
        {
            TermEnum termEnum = reader.terms(new Term(QueryConstants.FIELD_TXID, ""));
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals(QueryConstants.FIELD_TXID))
                {
                    Long txCommitTime = NumericEncoder.decodeLong(term.text());
                    lastTxCommitTime = txCommitTime;

                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();
        }
        catch (IOException e1)
        {

        }

        return lastTxCommitTime;
    }

    private long getLastChangeSetId(SolrIndexReader reader) throws IOException
    {
        long lastTxCommitTime = 0;

        try
        {
            TermEnum termEnum = reader.terms(new Term(QueryConstants.FIELD_ACLTXID, ""));
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals(QueryConstants.FIELD_ACLTXID))
                {
                    Long txCommitTime = NumericEncoder.decodeLong(term.text());
                    lastTxCommitTime = txCommitTime;

                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();
        }
        catch (IOException e1)
        {

        }

        return lastTxCommitTime;
    }

    private long getLastChangeSetCommitTime(SolrIndexReader reader) throws IOException
    {
        long lastTxCommitTime = 0;

        try
        {
            TermEnum termEnum = reader.terms(new Term(QueryConstants.FIELD_ACLTXCOMMITTIME, ""));
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals(QueryConstants.FIELD_ACLTXCOMMITTIME))
                {
                    Long txCommitTime = NumericEncoder.decodeLong(term.text());
                    lastTxCommitTime = txCommitTime;

                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();
        }
        catch (IOException e1)
        {

        }

        return lastTxCommitTime;
    }

    public static Document toDocument(SolrInputDocument doc, IndexSchema schema, AlfrescoSolrDataModel model)
    {
        Document out = new Document();
        out.setBoost(doc.getDocumentBoost());

        // Load fields from SolrDocument to Document
        for (SolrInputField field : doc)
        {
            String name = field.getName();
            SchemaField sfield = schema.getFieldOrNull(name);
            boolean used = false;
            float boost = field.getBoost();

            // Make sure it has the correct number
            if (sfield != null && !sfield.multiValued() && field.getValueCount() > 1)
            {
                String id = "";
                SchemaField sf = schema.getUniqueKeyField();
                if (sf != null)
                {
                    id = "[" + doc.getFieldValue(sf.getName()) + "] ";
                }
                throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "ERROR: "
                        + id + "multiple values encountered for non multiValued field " + sfield.getName() + ": " + field.getValue());
            }

            // load each field value
            boolean hasField = false;
            for (Object v : field)
            {
                // TODO: Sort out null
                if (v == null)
                {
                    continue;
                }
                String val = null;
                hasField = true;
                boolean isBinaryField = false;
                if (sfield != null && sfield.getType() instanceof BinaryField)
                {
                    isBinaryField = true;
                    BinaryField binaryField = (BinaryField) sfield.getType();
                    Field f = binaryField.createField(sfield, v, boost);
                    if (f != null)
                        out.add(f);
                    used = true;
                }
                else
                {
                    // TODO!!! HACK -- date conversion
                    if (sfield != null && v instanceof Date && sfield.getType() instanceof DateField)
                    {
                        DateField df = (DateField) sfield.getType();
                        val = df.toInternal((Date) v) + 'Z';
                    }
                    else if (v != null)
                    {
                        val = v.toString();
                    }

                    if (sfield != null)
                    {
                        if (v instanceof Reader)
                        {
                            used = true;
                            Field f = new Field(field.getName(), (Reader) v, model.getFieldTermVec(sfield));
                            f.setOmitNorms(model.getOmitNorms(sfield));
                            f.setOmitTermFreqAndPositions(sfield.omitTf());

                            if (f != null)
                            { // null fields are not added
                                out.add(f);
                            }
                        }
                        else
                        {
                            used = true;
                            Field f = sfield.createField(val, boost);
                            if (f != null)
                            { // null fields are not added
                                out.add(f);
                            }
                        }
                    }
                }

                // Check if we should copy this field to any other fields.
                // This could happen whether it is explicit or not.
                List<CopyField> copyFields = schema.getCopyFieldsList(name);
                for (CopyField cf : copyFields)
                {
                    SchemaField destinationField = cf.getDestination();
                    // check if the copy field is a multivalued or not
                    if (!destinationField.multiValued() && out.get(destinationField.getName()) != null)
                    {
                        throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "ERROR: multiple values encountered for non multiValued copy field "
                                + destinationField.getName() + ": " + val);
                    }

                    used = true;
                    Field f = null;
                    if (isBinaryField)
                    {
                        if (destinationField.getType() instanceof BinaryField)
                        {
                            BinaryField binaryField = (BinaryField) destinationField.getType();
                            f = binaryField.createField(destinationField, v, boost);
                        }
                    }
                    else
                    {
                        f = destinationField.createField(cf.getLimitedValue(val), boost);
                    }
                    if (f != null)
                    { // null fields are not added
                        out.add(f);
                    }
                }

                // In lucene, the boost for a given field is the product of the
                // document boost and *all* boosts on values of that field.
                // For multi-valued fields, we only want to set the boost on the
                // first field.
                boost = 1.0f;
            }

            // make sure the field was used somehow...
            if (!used && hasField)
            {
                throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "ERROR:unknown field '" + name + "'");
            }
        }

        // Now validate required fields or add default values
        // fields with default values are defacto 'required'
        for (SchemaField field : schema.getRequiredFields())
        {
            if (out.getField(field.getName()) == null)
            {
                if (field.getDefaultValue() != null)
                {
                    out.add(field.createField(field.getDefaultValue(), 1.0f));
                }
                else
                {
                    String id = schema.printableUniqueKey(out);
                    String msg = "Document [" + id + "] missing required field: " + field.getName();
                    throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, msg);
                }
            }
        }
        return out;
    }

    protected int getRegisteredSearcherCount()
    {
        HashSet<String> keys = new HashSet<String>();
        for(String  key : core.getInfoRegistry().keySet())
        {
            SolrInfoMBean mBean = core.getInfoRegistry().get(key);
            if(mBean != null)
            {
                if(mBean.getName().equals(SolrIndexSearcher.class.getName()))
                {
                    if(!key.equals("searcher"))
                    {
                        keys.add(key);
                    }
                }
            }
        }

        log.info(".... registered Searchers for "+core.getName()+" = "+keys.size());
        return keys.size();
    }
    
    protected List<SolrIndexSearcher> getRegisteredSearchers()
    {
        List<SolrIndexSearcher> searchers = new ArrayList<SolrIndexSearcher>();
        for(String  key : core.getInfoRegistry().keySet())
        {
            SolrInfoMBean mBean = core.getInfoRegistry().get(key);
            if(mBean != null)
            {
                if(mBean.getName().equals(SolrIndexSearcher.class.getName()))
                {
                    if(!key.equals("searcher"))
                    {
                        searchers.add((SolrIndexSearcher)mBean);
                    }
                   
                }
            }
        }

        return searchers; 
    }

    public NodeReport checkNodeCommon(NodeReport nodeReport)
    {
        // In Index

        long dbid = nodeReport.getDbid();

        try
        {
            RefCounted<SolrIndexSearcher> refCounted = core.getSearcher(false, true, null);

            refCounted = core.getSearcher(false, true, null);
            if (refCounted == null)
            {
                return nodeReport;
            }

            try
            {
                SolrIndexSearcher solrIndexSearcher = refCounted.get();

                String dbidString = NumericEncoder.encode(dbid);
                DocSet docSet = solrIndexSearcher.getDocSet(new TermQuery(new Term("DBID", dbidString)));
                // should find leaf and aux
                for (DocIterator it = docSet.iterator(); it.hasNext(); /* */)
                {
                    int doc = it.nextDoc();
                    Document document = solrIndexSearcher.doc(doc);
                    Fieldable fieldable = document.getFieldable("ID");
                    if (fieldable != null)
                    {
                        String value = fieldable.stringValue();
                        if (value != null)
                        {
                            if (value.startsWith("LEAF-"))
                            {
                                nodeReport.setIndexLeafDoc(Long.valueOf(doc));
                            }
                            else if (value.startsWith("AUX-"))
                            {
                                nodeReport.setIndexAuxDoc(Long.valueOf(doc));
                            }
                        }
                    }

                }
                DocSet txDocSet = solrIndexSearcher.getDocSet(new WildcardQuery(new Term("TXID", "*")));
                for (DocIterator it = txDocSet.iterator(); it.hasNext(); /* */)
                {
                    int doc = it.nextDoc();
                    Document document = solrIndexSearcher.doc(doc);
                    Fieldable fieldable = document.getFieldable("TXID");
                    if (fieldable != null)
                    {

                        if ((nodeReport.getIndexLeafDoc() == null) || (doc < nodeReport.getIndexLeafDoc().longValue()))
                        {
                            String value = fieldable.stringValue();
                            long txid = Long.parseLong(value);
                            nodeReport.setIndexLeafTx(txid);
                        }
                        if ((nodeReport.getIndexAuxDoc() == null) || (doc < nodeReport.getIndexAuxDoc().longValue()))
                        {
                            String value = fieldable.stringValue();
                            long txid = Long.parseLong(value);
                            nodeReport.setIndexAuxTx(txid);
                        }
                    }
                }

            }
            finally
            {
                refCounted.decref();
            }

        }
        catch (IOException e)
        {

        }

        return nodeReport;
    }

    public NodeReport checkNode(Node node)
    {
        NodeReport nodeReport = new NodeReport();
        nodeReport.setDbid(node.getId());

        nodeReport.setDbNodeStatus(node.getStatus());
        nodeReport.setDbTx(node.getTxnId());

        checkNodeCommon(nodeReport);

        return nodeReport;
    }

    /**
     * @param dbid
     * @return
     */
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

        checkNodeCommon(nodeReport);

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

    //    public List<Long> getNodesForDbTransaction(Long txid)
    //    {
    //
    //        try
    //        {
    //            ArrayList<Long> answer = new ArrayList<Long>();
    //            GetNodesParameters gnp = new GetNodesParameters();
    //            ArrayList<Long> txs = new ArrayList<Long>();
    //            txs.add(txid);
    //            gnp.setTransactionIds(txs);
    //            gnp.setStoreProtocol(storeRef.getProtocol());
    //            gnp.setStoreIdentifier(storeRef.getIdentifier());
    //            List<Node> nodes;
    //            nodes = client.getNodes(gnp, Integer.MAX_VALUE);
    //            for (Node node : nodes)
    //            {
    //                answer.add(node.getId());
    //            }
    //            return answer;
    //        }
    //        catch (IOException e)
    //        {
    //            throw new AlfrescoRuntimeException("Failed to get nodes", e);
    //        }
    //        catch (JSONException e)
    //        {
    //            throw new AlfrescoRuntimeException("Failed to get nodes", e);
    //        }
    //        catch (AuthenticationException e)
    //        {
    //            throw new AlfrescoRuntimeException("Failed to get nodes", e);
    //        }
    //    }

    /**
     * @param acltxid
     * @return
     */
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

    /**
     * @param aclid
     * @return
     */
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

        try
        {
            RefCounted<SolrIndexSearcher> refCounted = core.getSearcher(false, true, null);

            refCounted = core.getSearcher(false, true, null);
            if (refCounted == null)
            {
                return aclReport;
            }

            try
            {
                SolrIndexSearcher solrIndexSearcher = refCounted.get();
                SolrIndexReader reader = solrIndexSearcher.getReader();

                String aclIdString = NumericEncoder.encode(aclid);
                DocSet docSet = solrIndexSearcher.getDocSet(new TermQuery(new Term("ACLID", aclIdString)));
                // should find leaf and aux
                for (DocIterator it = docSet.iterator(); it.hasNext(); /* */)
                {
                    int doc = it.nextDoc();

                    Document document = solrIndexSearcher.doc(doc);
                    Fieldable fieldable = document.getFieldable("ID");
                    if (fieldable != null)
                    {
                        String value = fieldable.stringValue();
                        if (value != null)
                        {
                            if (value.startsWith("ACL-"))
                            {
                                aclReport.setIndexAclDoc(Long.valueOf(doc));
                            }
                        }
                    }

                }
                DocSet txDocSet = solrIndexSearcher.getDocSet(new WildcardQuery(new Term("ACLTXID", "*")));
                for (DocIterator it = txDocSet.iterator(); it.hasNext(); /* */)
                {
                    int doc = it.nextDoc();
                    Document document = solrIndexSearcher.doc(doc);
                    Fieldable fieldable = document.getFieldable("ACLTXID");
                    if (fieldable != null)
                    {

                        if ((aclReport.getIndexAclDoc() == null) || (doc < aclReport.getIndexAclDoc().longValue()))
                        {
                            String value = fieldable.stringValue();
                            long acltxid = Long.parseLong(value);
                            aclReport.setIndexAclTx(acltxid);
                        }

                    }
                }

            }
            finally
            {
                refCounted.decref();
            }

        }
        catch (IOException e)
        {

        }

        return aclReport;
    }

    private void loadModel(Map<String, M2Model> modelMap, HashSet<String> loadedModels, M2Model model, AlfrescoSolrDataModel dataModel)
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
                    loadModel(modelMap, loadedModels, importedModel, dataModel);
                }
            }

            if(dataModel.putModel(model))
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

    /*
     * (non-Javadoc)
     * @see org.apache.solr.core.CloseHook#close(org.apache.solr.core.SolrCore)
     */
    @Override
    public void close(SolrCore core)
    {
        try
        {
            shutdown = true;
            
            adminHandler.getScheduler().deleteJob("CoreTracker-" + core.getName(), "Solr");
            adminHandler.getTrackers().remove(core.getName());
            if (adminHandler.getTrackers().size() == 0)
            {
                if (!adminHandler.getScheduler().isShutdown())
                {
                    adminHandler.getScheduler().pauseAll();
                    adminHandler.getScheduler().shutdown();
                }
            }

            client.close();
        }
        catch (SchedulerException e)
        {
            e.printStackTrace();
        }

    }

    static class BoundedDeque<T> implements Iterable<T>
    {
        private LinkedBlockingDeque<T> deque;

        private int max = 10;

        BoundedDeque (int max)
        {
            this.max = max;
            deque = new LinkedBlockingDeque<T>();
        }

        /**
         * @return
         */
        public int size()
        {
            return deque.size();
        }

        void add(T add)
        {
            while(deque.size() > (max - 1))
            {
                deque.removeLast();
            }
            deque.addFirst(add);
        }

        T getLast()
        {
            return deque.getFirst();
        }

        /* (non-Javadoc)
         * @see java.lang.Iterable#iterator()
         */
        @Override
        public Iterator<T> iterator()
        {
            return deque.iterator();
        }

    }

    /**
     * @return
     * @throws IOException 
     */
    public NamedList<Object> getCoreStats() throws IOException
    {
        DecimalFormat df = new DecimalFormat("###,###.######");
        
        NamedList<Object> coreSummary = new SimpleOrderedMap<Object>();
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            OpenBitSet allLeafDocs = (OpenBitSet) solrIndexSearcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ALL_LEAF_DOCS);
            long count = allLeafDocs.cardinality();
            coreSummary.add("Alfresco Nodes in Index", count);
            coreSummary.add("Searcher", solrIndexSearcher.getStatistics());
            Map<String, SolrInfoMBean> infoRegistry = core.getInfoRegistry();
            for(String key : infoRegistry.keySet())
            {
                SolrInfoMBean infoMBean = infoRegistry.get(key);
                if(key.equals("/alfresco"))
                {
                    coreSummary.add("/alfresco", fixStats(infoMBean.getStatistics()));
                }
                if(key.equals("/afts"))
                {
                    coreSummary.add("/afts",  fixStats(infoMBean.getStatistics()));
                }
                if(key.equals("/cmis"))
                {
                    coreSummary.add("/cmis",  fixStats(infoMBean.getStatistics()));
                }
                if(key.equals("filterCache"))
                {
                    coreSummary.add("/filterCache", infoMBean.getStatistics());
                }
                if(key.equals("queryResultCache"))
                {
                    coreSummary.add("/queryResultCache", infoMBean.getStatistics());
                }
                if(key.equals("alfrescoAuthorityCache"))
                {
                    coreSummary.add("/alfrescoAuthorityCache", infoMBean.getStatistics());
                }
                if(key.equals("alfrescoPathCache"))
                {
                    coreSummary.add("/alfrescoPathCache", infoMBean.getStatistics());
                }
            }
            
            // Find searchers and do memory use for each .. and add them all up7
            
            long memory = 0L;
            int searcherCount = 0;
            List<SolrIndexSearcher> searchers = getRegisteredSearchers();
            for(SolrIndexSearcher searcher : searchers)
            {
                memory +=  addSearcherStats(coreSummary, searcher, searcherCount);
                searcherCount++;
            }
            
            coreSummary.add("Number of Searchers", searchers.size());
            coreSummary.add("Total Searcher Cache (GB)", df.format(memory/1024.0f/1024.0f/1024.0f));
            
            
            IndexDeletionPolicyWrapper delPolicy = core.getDeletionPolicy();
            IndexCommit indexCommit = delPolicy.getLatestCommit();
            // race?
            if(indexCommit == null) {
                indexCommit = solrIndexSearcher.getReader().getIndexCommit();
            }
            if (indexCommit != null)  
            {    
                delPolicy.setReserveDuration(indexCommit.getVersion(), 20000);
                Long fileSize = 0L;
                
                File dir = new File(solrIndexSearcher.getIndexDir());
                for(Iterator it = indexCommit.getFileNames().iterator(); it.hasNext(); /**/)
                {
                    String name = (String)it.next();
                    File file = new File(dir, name);
                    if(file.exists())
                    {
                        fileSize += file.length();
                    }
                }
                
                coreSummary.add("On disk (GB)", df.format(fileSize/1024.0f/1024.0f/1024.0f));
                coreSummary.add("Per node B", count > 0 ? fileSize/count : 0);
            }
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }



        return coreSummary;
    }

    /**
     * @param coreSummary
     * @param solrIndexSearcher
     * @param count
     * @return
     */
    private  long addSearcherStats(NamedList<Object> coreSummary, SolrIndexSearcher solrIndexSearcher, int index)
    {
        DecimalFormat df = new DecimalFormat("###,###.######");
        
        OpenBitSet allLeafDocs = (OpenBitSet) solrIndexSearcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ALL_LEAF_DOCS);
        long count = allLeafDocs.cardinality();
        
        ResizeableArrayList<CacheEntry> indexedByDocId = (ResizeableArrayList<CacheEntry>) solrIndexSearcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_ARRAYLIST_CACHE, AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_DOC_ID);
        ResizeableArrayList<CacheEntry> indexedOderedByAclIdThenDoc = (ResizeableArrayList<CacheEntry>)solrIndexSearcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_ARRAYLIST_CACHE, AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF);
        
        long memory = (count * 40) + (indexedByDocId.size() * 8 * 4) + (indexedOderedByAclIdThenDoc.size() * 8 * 2);
        memory += (filterCacheSize + pathCacheSize + authorityCacheSize) * indexedByDocId.size() / 8;
        NamedList<Object> details = new SimpleOrderedMap<Object>();
        details.add("Searcher", solrIndexSearcher.getStatistics());
        details.add("Size", indexedByDocId.size());
        details.add("Node Count", count);
        details.add("Memory (GB)", df.format(memory/1024.0f/1024.0f/1024.0f));
        coreSummary.add("Searcher-"+index, details);     
        return memory;
    }

    /**
     * @param statistics
     * @return
     */
    private NamedList<Object> fixStats(NamedList<Object> namedList)
    {
        int sz = namedList.size();

        for (int i=0; i<sz; i++) 
        {
            String key = namedList.getName(i);
            Object value = namedList.getVal(i);
            if(value instanceof Number)
            {
                Number number = (Number)value;
                if(Float.isInfinite(number.floatValue()) || Float.isNaN(number.floatValue()) || Double.isInfinite(number.doubleValue()) || Double.isNaN(number.doubleValue()))
                {
                    namedList.setVal(i, null);
                }
            }
        }
        return namedList;
    }

    
    public static class IndexTrackingShutdownException extends RuntimeException
    {

        /**
         * 
         */
        private static final long serialVersionUID = -1294455847013444397L;
        
    }

    /**
     * 
     */
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

    /**
     * 
     */
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
    
    
}