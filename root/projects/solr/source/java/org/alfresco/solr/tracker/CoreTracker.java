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
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alfresco.encryption.KeyStoreParameters;
import org.alfresco.encryption.ssl.SSLEncryptionParameters;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.httpclient.AlfrescoHttpClient;
import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.httpclient.HttpClientFactory;
import org.alfresco.httpclient.HttpClientFactory.SecureCommsType;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.MultiReader;
import org.alfresco.repo.search.impl.lucene.analysis.NumericEncoder;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.ModelDefinition.XMLBindingType;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AclReport;
import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.NodeReport;
import org.alfresco.solr.client.Acl;
import org.alfresco.solr.client.AclChangeSet;
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
import org.alfresco.solr.client.SolrKeyResourceLoader;
import org.alfresco.solr.client.StringPropertyValue;
import org.alfresco.solr.client.Transaction;
import org.alfresco.util.ISO9075;
import org.alfresco.util.Pair;
import org.alfresco.util.TempFileProvider;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.schema.BinaryField;
import org.apache.solr.schema.CopyField;
import org.apache.solr.schema.DateField;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.DeleteUpdateCommand;
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

    private SOLRAPIClient client;

    private volatile long lastIndexedCommitTime = 0;

    private volatile long lastIndexedIdBeforeHoles = -1;

    private volatile boolean running = false;

    volatile boolean check = false;

    SolrCore core;

    private String alfrescoHost;

    private int alfrescoPort;

    private int alfrescoPortSSL;

    private String cron;

    private StoreRef storeRef;

    private long lag;

    private long holeRetention;

    private long batchCount;

    // encryption related parameters
    private String secureCommsType; // "none", "https"

    private String cipherAlgorithm;

	private String keyStoreType;

	private String keyStoreProvider;

	private String passwordFileLocation;

	private String keyStoreLocation;

	private Long messageTimeout;

	private String macAlgorithm;

	// ssl
	private String sslKeyStoreType;

	private String sslKeyStoreProvider;

	private String sslKeyStoreLocation;

	private String sslKeyStorePasswordFileLocation;

	private String sslTrustStoreType;

	private String sslTrustStoreProvider;

	private String sslTrustStoreLocation;

	private String sslTrustStorePasswordFileLocation;
	
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

    CoreTracker(AlfrescoCoreAdminHandler adminHandler, SolrCore core)
    {
        super();
        this.adminHandler = adminHandler;
        this.core = core;

        try
        {
            List<String> lines = core.getResourceLoader().getLines("solrcore.properties");
            for (String line : lines)
            {
                if ((line.length() == 0) || (line.startsWith("#")))
                {
                    continue;
                }
                String[] split = line.split("=", 2);
                if (split.length != 2)
                {
                    return;
                }
                if (split[0].equals("alfresco.host"))
                {
                	alfrescoHost = split[1];
                }
                else  if (split[0].equals("alfresco.port"))
                {
                	alfrescoPort = Integer.parseInt(split[1]);
                }
                else  if (split[0].equals("alfresco.port.ssl"))
                {
                	alfrescoPortSSL = Integer.parseInt(split[1]);
                }
                else if (split[0].equals("alfresco.cron"))
                {
                    cron = split[1];
                }
                else if (split[0].equals("alfresco.stores"))
                {
                    storeRef = new StoreRef(split[1]);
                }
                else if (split[0].equals("alfresco.lag"))
                {
                    lag = Long.parseLong(split[1]);
                }
                else if (split[0].equals("alfresco.hole.retention"))
                {
                    holeRetention = Long.parseLong(split[1]);
                }
                else if (split[0].equals("alfresco.batch.count"))
                {
                    batchCount = Long.parseLong(split[1]);
                }
                else if (split[0].equals("alfresco.encryption.cipherAlgorithm"))
                {
                    cipherAlgorithm = split[1];
                }
                else if (split[0].equals("alfresco.encryption.keystore.type"))
                {
                	keyStoreType = split[1];
                }
                else if (split[0].equals("alfresco.encryption.keystore.provider"))
                {
                	keyStoreProvider = split[1];
                }
                else if (split[0].equals("alfresco.encryption.keystore.passwordFileLocation"))
                {
                    passwordFileLocation = split[1];
                }
                else if (split[0].equals("alfresco.encryption.keystore.location"))
                {
                    keyStoreLocation = split[1];
                }
                else if (split[0].equals("alfresco.encryption.ssl.keystore.type"))
                {
                	sslKeyStoreType = split[1];
                }
                else if (split[0].equals("alfresco.encryption.ssl.keystore.provider"))
                {
                	sslKeyStoreProvider = split[1];
                }
                else if (split[0].equals("alfresco.encryption.ssl.keystore.location"))
                {
                	sslKeyStoreLocation = split[1];
                }
                else if (split[0].equals("alfresco.encryption.ssl.keystore.passwordFileLocation"))
                {
                    sslKeyStorePasswordFileLocation = split[1];
                }
                else if (split[0].equals("alfresco.encryption.ssl.truststore.type"))
                {
                	sslTrustStoreType = split[1];
                }
                else if (split[0].equals("alfresco.encryption.ssl.truststore.provider"))
                {
                	sslTrustStoreProvider = split[1];
                }
                else if (split[0].equals("alfresco.encryption.ssl.truststore.location"))
                {
                	sslTrustStoreLocation = split[1];
                }
                else if (split[0].equals("alfresco.encryption.ssl.truststore.passwordFileLocation"))
                {
                    sslTrustStorePasswordFileLocation = split[1];
                }
                else if (split[0].equals("alfresco.encryption.messageTimeout"))
                {
                    messageTimeout = Long.parseLong(split[1]);
                }
                else if (split[0].equals("alfresco.encryption.macAlgorithm"))
                {
                    macAlgorithm = split[1];
                }
                else if (split[0].equals("alfresco.secureComms"))
                {
                	secureCommsType = split[1];
                }
            }
        }
        catch (IOException e1)
        {
            throw new AlfrescoRuntimeException("Error reading alfrecso core config for " + core.getName());
        }

        SolrResourceLoader loader = core.getSchema().getResourceLoader();
        id = loader.getInstanceDir();
        dataModel = AlfrescoSolrDataModel.getInstance(id);

        client = new SOLRAPIClient(getRepoClient(loader), dataModel.getDictionaryService(), dataModel.getNamespaceDAO());

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SchedulerException e)
        {
            // TODO Auto-generated catch block
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
    		sslEncryptionParameters, keyResourceLoader, null, null, alfrescoHost, alfrescoPort, alfrescoPortSSL);
        // TODO need to make port configurable depending on secure comms, or just make redirects
        // work
        AlfrescoHttpClient repoClient = httpClientFactory.getRepoClient(alfrescoHost, alfrescoPortSSL);
        return repoClient;
    }

    /**
     * @return the check
     */
    public boolean isCheck()
    {
        return check;
    }

    /**
     * @param check
     *            the check to set
     */
    public void setCheck(boolean check)
    {
        this.check = check;
    }

    public void updateIndex()
    {

        synchronized (this)
        {
            if (running)
            {
                log.info("... update for " + core.getName() + " is already running");
                return;
            }
            else
            {
                log.info("... updating " + core.getName());
                running = true;
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
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (JSONException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (AuthenticationException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        finally
        {
            running = false;
            check = false;
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
                List<AclChangeSet> aclChangeSets = client.getAclChangeSets(0L, aclChangeSetId, 1);
                if ((aclChangeSets.size() > 0) && aclChangeSetId.equals(aclChangeSets.get(0).getId()))
                {
                    AclChangeSet changeSet = aclChangeSets.get(0);
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
        }
        if(requiresCommit)
        {
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
        }
        if(requiresCommit)
        {
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

                    List<AclChangeSet> aclChangeSets = client.getAclChangeSets(0L, aclChangeSetId, 1);
                    if ((aclChangeSets.size() > 0) && aclChangeSetId.equals(aclChangeSets.get(0).getId()))
                    {
                        AclChangeSet changeSet = aclChangeSets.get(0);
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
            }
            if(requiresCommit)
            {
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
            }
            if(requiresCommit)
            {
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
            }
            if(requiresCommit)
            {
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
            }
            if(requiresCommit)
            {
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

        Query query = new TermQuery(new Term(AbstractLuceneQueryParser.FIELD_INACLTXID, NumericEncoder.encode(aclChangeSetId)));
        deleteByQuery(solrIndexSearcher, query);
    }

    public void deleteByAclId(SolrIndexSearcher solrIndexSearcher, Long aclId) throws IOException
    {

        Query query = new TermQuery(new Term(AbstractLuceneQueryParser.FIELD_ACLID, NumericEncoder.encode(aclId)));
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
                Document doc = solrIndexSearcher.doc(current, Collections.singleton(AbstractLuceneQueryParser.FIELD_ID));
                Fieldable fieldable = doc.getFieldable(AbstractLuceneQueryParser.FIELD_ID);
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
                Document doc = solrIndexSearcher.doc(it.nextDoc(), Collections.singleton(AbstractLuceneQueryParser.FIELD_ID));
                Fieldable fieldable = doc.getFieldable(AbstractLuceneQueryParser.FIELD_ID);
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

                    List<Transaction> transactions = client.getTransactions(0L, transactionId, 0);
                    if ((transactions.size() > 0) && (transactionId.equals(transactions.get(0).getId())))
                    {
                        Transaction info = transactions.get(0);

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

                        }

                        // Index the transaction doc after the node - if this is not found then a reindex will be
                        // done.
                        indexTransaction(info, true);
                        requiresCommit = true;

                    }
                }

                if (docCount > batchCount)
                {
                    core.getUpdateHandler().commit(new CommitUpdateCommand(false));
                    docCount = 0;
                    requiresCommit = false;
                }
            }
            if (requiresCommit || ( docCount > 0))
            {
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

                    indexNode(node, solrIndexSearcher, true);
                    requiresCommit = true;
                }

            }

            if(requiresCommit)
            {
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

                    indexNode(node, solrIndexSearcher, false);
                    requiresCommit = true;
                }

            }

            if(requiresCommit)
            {
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
                    List<Transaction> transactions = client.getTransactions(0L, transactionId, 0);
                    if ((transactions.size() > 0) && (transactionId.equals(transactions.get(0).getId())))
                    {
                        Transaction info = transactions.get(0);

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

                        }

                        // Index the transaction doc after the node - if this is not found then a reindex will be
                        // done.
                        indexTransaction(info, false);
                        requiresCommit = true;

                    }
                }

                if (docCount > batchCount)
                {
                    core.getUpdateHandler().commit(new CommitUpdateCommand(false));
                    docCount = 0;
                    requiresCommit = false;
                }
            }
            if (requiresCommit || (docCount > 0))
            {
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
            }
            if(requiresCommit)
            {
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
            }
            if(requiresCommit)
            {
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
        Query query = new TermQuery(new Term(AbstractLuceneQueryParser.FIELD_INTXID, NumericEncoder.encode(transactionId)));
        deleteByQuery(solrIndexSearcher, query);
    }

    private void deleteByNodeId(SolrIndexSearcher solrIndexSearcher, Long nodeId) throws IOException
    {
        Query query = new TermQuery(new Term(AbstractLuceneQueryParser.FIELD_DBID, NumericEncoder.encode(nodeId)));
        deleteByQuery(solrIndexSearcher, query);
    }

    public void trackRepository() throws IOException, AuthenticationException, JSONException
    {

        boolean indexing = false;
        boolean aclIndexing = false;

        trackModels();

        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);

            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            SolrIndexReader reader = solrIndexSearcher.getReader();

            if (lastIndexedCommitTime == 0)
            {
                lastIndexedCommitTime = getLastTransactionCommitTime(reader);
            }

            long startTime = System.currentTimeMillis();
            long timeToStopIndexing = startTime - lag;
            long timeBeforeWhichThereCanBeNoHoles = startTime - holeRetention;
            long timeBeforeWhichThereCanBeNoHolesInIndex = lastIndexedCommitTime - holeRetention;
            long lastGoodTxCommitTimeInIndex = getLastTxCommitTimeBeforeHoles(reader, timeBeforeWhichThereCanBeNoHolesInIndex);

            if (lastIndexedIdBeforeHoles == -1)
            {
                lastIndexedIdBeforeHoles = getLargestTxIdByCommitTime(reader, lastGoodTxCommitTimeInIndex);
            }
            else
            {
                long currentBestFromIndex = getLargestTxIdByCommitTime(reader, lastGoodTxCommitTimeInIndex);
                if (currentBestFromIndex > lastIndexedIdBeforeHoles)
                {
                    lastIndexedIdBeforeHoles = currentBestFromIndex;
                }
            }

            long lastTxCommitTime = lastGoodTxCommitTimeInIndex;
            long aclLastCommitTime = lastGoodTxCommitTimeInIndex;

            // Track Acls up to end point
            long aclLoopStartingCommitTime;
            List<AclChangeSet> aclChangeSets;
            boolean aclsUpToDate = false;
            do
            {
                aclLoopStartingCommitTime = aclLastCommitTime;
                aclChangeSets = client.getAclChangeSets(aclLastCommitTime, null, 2000);

                log.info("Scanning Acl change sets ...");
                if (aclChangeSets.size() > 0)
                {
                    log.info(".... from " + aclChangeSets.get(0));
                    log.info(".... to " + aclChangeSets.get(aclChangeSets.size() - 1));
                }
                else
                {
                    log.info(".... non found after lastTxCommitTime " + aclLastCommitTime);
                }

                for (AclChangeSet changeSet : aclChangeSets)
                {
                    if (!aclIndexing)
                    {
                        String target = NumericEncoder.encode(changeSet.getId());
                        TermEnum termEnum = reader.terms(new Term(AbstractLuceneQueryParser.FIELD_ACLTXID, target));
                        Term term = termEnum.term();
                        termEnum.close();
                        if (term == null)
                        {
                            aclIndexing = true;
                        }
                        else
                        {
                            if (target.equals(term.text()))
                            {
                                aclLastCommitTime = changeSet.getCommitTimeMs();
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
                        if (changeSet.getCommitTimeMs() > timeToStopIndexing)
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
                            aclLastCommitTime = changeSet.getCommitTimeMs();
                        }
                    }
                }
            }
            while ((aclChangeSets.size() > 0) && (aclsUpToDate == false) && (aclLoopStartingCommitTime < aclLastCommitTime));

            if (aclIndexing)
            {
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }

            boolean upToDate = false;
            ArrayList<Transaction> transactionsOrderedById = new ArrayList<Transaction>(10000);
            List<Transaction> transactions;
            long loopStartingCommitTime;
            do
            {
                int docCount = 0;

                loopStartingCommitTime = lastTxCommitTime;
                transactions = client.getTransactions(lastTxCommitTime, null, 2000);
                log.info("Scanning transactions ...");
                if (transactions.size() > 0)
                {
                    log.info(".... from " + transactions.get(0));
                    log.info(".... to " + transactions.get(transactions.size() - 1));
                }
                else
                {
                    log.info(".... non found after lastTxCommitTime " + lastTxCommitTime);
                }
                for (Transaction info : transactions)
                {

                    if (!indexing)
                    {
                        String target = NumericEncoder.encode(info.getId());
                        TermEnum termEnum = reader.terms(new Term(AbstractLuceneQueryParser.FIELD_TXID, target));
                        Term term = termEnum.term();
                        termEnum.close();
                        if (term == null)
                        {
                            indexing = true;
                        }
                        else
                        {
                            if (target.equals(term.text()))
                            {
                                lastTxCommitTime = info.getCommitTimeMs();
                            }
                            else
                            {
                                indexing = true;
                            }
                        }
                    }

                    if (indexing)
                    {
                        // Make sure we do not go ahead of where we started - we will check the holes here
                        // correctly next time
                        if (info.getCommitTimeMs() > timeToStopIndexing)
                        {
                            upToDate = true;
                        }
                        else
                        {
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

                            }

                            // Index the transaction doc after the node - if this is not found then a reindex will be
                            // done.
                            indexTransaction(info, true);

                            if (info.getCommitTimeMs() > lastIndexedCommitTime)
                            {
                                lastIndexedCommitTime = info.getCommitTimeMs();
                            }

                            lastTxCommitTime = info.getCommitTimeMs();
                        }
                    }
                    // could batch commit here
                    if (docCount > batchCount)
                    {
                        core.getUpdateHandler().commit(new CommitUpdateCommand(false));
                        docCount = 0;
                    }
                }
                // reorder and find first last before hole

                if (transactionsOrderedById.size() < 10000)
                {
                    transactionsOrderedById.addAll(transactions);
                    Collections.sort(transactionsOrderedById, new Comparator<Transaction>()
                    {

                        @Override
                        public int compare(Transaction o1, Transaction o2)
                        {
                            return (int) (o1.getId() - o2.getId());
                        }
                    });

                    ArrayList<Transaction> newTransactionsOrderedById = new ArrayList<Transaction>(10000);
                    for (Transaction info : transactions)
                    {
                        if (info.getCommitTimeMs() < timeBeforeWhichThereCanBeNoHoles)
                        {
                            lastIndexedIdBeforeHoles = info.getId();
                        }
                        else
                        {
                            if (info.getId() == (lastIndexedIdBeforeHoles + 1))
                            {
                                lastIndexedIdBeforeHoles = info.getId();
                            }
                            else
                            {
                                newTransactionsOrderedById.add(info);
                            }
                        }
                    }
                    transactionsOrderedById = newTransactionsOrderedById;
                }
            }
            while ((transactions.size() > 0) && (upToDate == false) && (loopStartingCommitTime < lastTxCommitTime));
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }

        if (indexing)
        {
            core.getUpdateHandler().commit(new CommitUpdateCommand(false));
        }

        // check index state

        if (check)
        {
            checkIndex(null, null, null, null, null, null);
        }

    }

    private void trackModels() throws AuthenticationException, IOException, JSONException
    {
        // track models
        // reflect changes changes and update on disk copy

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
                break;
            }
        }

        HashSet<String> loadedModels = new HashSet<String>();
        for (M2Model model : modelMap.values())
        {
            loadModel(modelMap, loadedModels, model, dataModel);
        }
        dataModel.afterInitModels();

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
    }

    /**
     * @param alfrescoModelDir
     * @param modelName
     */
    private void removeMatchingModels(File alfrescoModelDir, QName modelName)
    {

        final String prefix = modelName.toString().replace(":", ".") + ".";
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
    private void indexAcl(List<AclReaders> aclReaderList, boolean overwrite) throws IOException
    {

        for (AclReaders aclReaders : aclReaderList)
        {
            AddUpdateCommand cmd = new AddUpdateCommand();
            cmd.overwriteCommitted = overwrite;
            cmd.overwritePending = overwrite;
            SolrInputDocument input = new SolrInputDocument();
            input.addField(AbstractLuceneQueryParser.FIELD_ID, "ACL-" + aclReaders.getId());
            input.addField(AbstractLuceneQueryParser.FIELD_ACLID, aclReaders.getId());
            input.addField(AbstractLuceneQueryParser.FIELD_INACLTXID, aclReaders.getAclChangeSetId());
            for (String reader : aclReaders.getReaders())
            {
                input.addField(AbstractLuceneQueryParser.FIELD_READER, reader);
            }
            cmd.solrDoc = input;
            cmd.doc = CoreTracker.toDocument(cmd.getSolrInputDocument(), core.getSchema(), dataModel);
            core.getUpdateHandler().addDoc(cmd);
        }
    }

    /**
     * @param dataModel
     * @param info
     * @throws IOException
     */
    private void indexTransaction(Transaction info, boolean overwrite) throws IOException
    {
        AddUpdateCommand cmd = new AddUpdateCommand();
        cmd.overwriteCommitted = overwrite;
        cmd.overwritePending = overwrite;
        SolrInputDocument input = new SolrInputDocument();
        input.addField(AbstractLuceneQueryParser.FIELD_ID, "TX-" + info.getId());
        input.addField(AbstractLuceneQueryParser.FIELD_TXID, info.getId());
        input.addField(AbstractLuceneQueryParser.FIELD_INTXID, info.getId());
        input.addField(AbstractLuceneQueryParser.FIELD_TXCOMMITTIME, info.getCommitTimeMs());
        cmd.solrDoc = input;
        cmd.doc = CoreTracker.toDocument(cmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(cmd);
    }

    /**
     * @param dataModel
     * @param info
     * @throws IOException
     */
    private void indexAclTransaction(AclChangeSet changeSet, boolean overwrite) throws IOException
    {
        AddUpdateCommand cmd = new AddUpdateCommand();
        cmd.overwriteCommitted = overwrite;
        cmd.overwritePending = overwrite;
        SolrInputDocument input = new SolrInputDocument();
        input.addField(AbstractLuceneQueryParser.FIELD_ID, "ACLTX-" + changeSet.getId());
        input.addField(AbstractLuceneQueryParser.FIELD_ACLTXID, changeSet.getId());
        input.addField(AbstractLuceneQueryParser.FIELD_INACLTXID, changeSet.getId());
        input.addField(AbstractLuceneQueryParser.FIELD_ACLTXCOMMITTIME, changeSet.getCommitTimeMs());
        cmd.solrDoc = input;
        cmd.doc = CoreTracker.toDocument(cmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(cmd);
    }

    /**
     * @param dataModel
     * @param node
     * @throws IOException
     */
    private void indexNode(Node node, SolrIndexSearcher solrIndexSearcher, boolean overwrite) throws IOException
    {
        if ((node.getStatus() == SolrApiNodeStatus.DELETED) || (node.getStatus() == SolrApiNodeStatus.UNKNOWN))
        {
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
        }
        
        if ((node.getStatus() == SolrApiNodeStatus.UPDATED)  || (node.getStatus() == SolrApiNodeStatus.UNKNOWN))
        {

            log.info(".. updating");
            NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
            nmdp.setFromNodeId(node.getId());
            nmdp.setToNodeId(node.getId());
            try
            {
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
                    if (mayHaveChildren(nodeMetaData))
                    {
                        log.info(".. checking for path change");
                        BooleanQuery bQuery = new BooleanQuery();
                        bQuery.add(new TermQuery(new Term(AbstractLuceneQueryParser.FIELD_DBID, NumericEncoder.encode(nodeMetaData.getId()))), Occur.MUST);
                        bQuery.add(new TermQuery(new Term(AbstractLuceneQueryParser.FIELD_PARENT_ASSOC_CRC, NumericEncoder.encode(nodeMetaData.getParentAssocsCrc()))), Occur.MUST);
                        DocSet docSet = solrIndexSearcher.getDocSet(bQuery);
                        if (docSet.size() > 0)
                        {
                            log.debug("... found aux match");
                        }
                        else
                        {
                            docSet = solrIndexSearcher.getDocSet(new TermQuery(new Term(AbstractLuceneQueryParser.FIELD_DBID, NumericEncoder.encode(nodeMetaData.getId()))));
                            if (docSet.size() > 0)
                            {
                                log.debug("... cascade updating aux doc");
                                updateDescendantAuxDocs(nodeMetaData, overwrite);
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

                    SolrInputDocument doc = new SolrInputDocument();
                    doc.addField(AbstractLuceneQueryParser.FIELD_ID, "LEAF-" + nodeMetaData.getId());
                    doc.addField(AbstractLuceneQueryParser.FIELD_DBID, nodeMetaData.getId());
                    doc.addField(AbstractLuceneQueryParser.FIELD_LID, nodeMetaData.getNodeRef());
                    doc.addField(AbstractLuceneQueryParser.FIELD_INTXID, nodeMetaData.getTxnId());

                    for (QName propertyQname : properties.keySet())
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
                    doc.addField(AbstractLuceneQueryParser.FIELD_TYPE, nodeMetaData.getType().toString());
                    for (QName aspect : nodeMetaData.getAspects())
                    {
                        doc.addField(AbstractLuceneQueryParser.FIELD_ASPECT, aspect.toString());
                    }
                    doc.addField(AbstractLuceneQueryParser.FIELD_ISNODE, "T");
                    doc.addField(AbstractLuceneQueryParser.FIELD_FTSSTATUS, "Clean");

                    leafDocCmd.solrDoc = doc;
                    leafDocCmd.doc = CoreTracker.toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

                    SolrInputDocument aux = createAuxDoc(nodeMetaData);
                    auxDocCmd.solrDoc = aux;
                    auxDocCmd.doc = CoreTracker.toDocument(auxDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

                }

                core.getUpdateHandler().addDoc(leafDocCmd);
                core.getUpdateHandler().addDoc(auxDocCmd);

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
            catch (JSONException e)
            {
                log.warn(e.getStackTrace().toString());
            }
            catch (AuthenticationException e)
            {
                log.warn(e.getStackTrace().toString());
            }
        }
        
    }

    private void updateDescendantAuxDocs(NodeMetaData parentNodeMetaData, boolean overwrite) throws AuthenticationException, IOException, JSONException
    {
        if (parentNodeMetaData.getChildIds() != null)
        {
            for (Long childId : parentNodeMetaData.getChildIds())
            {
                NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
                nmdp.setFromNodeId(childId);
                nmdp.setToNodeId(childId);
                nmdp.setIncludeAclId(true);
                nmdp.setIncludeAspects(true);
                nmdp.setIncludeChildAssociations(false);
                nmdp.setIncludeChildIds(true);
                nmdp.setIncludeNodeRef(false);
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
                        updateDescendantAuxDocs(nodeMetaData, overwrite);
                    }

                    SolrInputDocument aux = createAuxDoc(nodeMetaData);
                    AddUpdateCommand auxDocCmd = new AddUpdateCommand();
                    auxDocCmd.overwriteCommitted = overwrite;
                    auxDocCmd.overwritePending = overwrite;
                    auxDocCmd.solrDoc = aux;
                    auxDocCmd.doc = CoreTracker.toDocument(auxDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

                    core.getUpdateHandler().addDoc(auxDocCmd);
                }

            }
        }

    }

    private SolrInputDocument createAuxDoc(NodeMetaData nodeMetaData)
    {
        SolrInputDocument aux = new SolrInputDocument();
        aux.addField(AbstractLuceneQueryParser.FIELD_ID, "AUX-" + nodeMetaData.getId());
        aux.addField(AbstractLuceneQueryParser.FIELD_DBID, nodeMetaData.getId());
        aux.addField(AbstractLuceneQueryParser.FIELD_ACLID, nodeMetaData.getAclId());
        aux.addField(AbstractLuceneQueryParser.FIELD_INTXID, nodeMetaData.getTxnId());

        for (Pair<String, QName> path : nodeMetaData.getPaths())
        {
            aux.addField(AbstractLuceneQueryParser.FIELD_PATH, path.getFirst());
        }

        if (nodeMetaData.getOwner() != null)
        {
            aux.addField(AbstractLuceneQueryParser.FIELD_OWNER, nodeMetaData.getOwner());
        }
        aux.addField(AbstractLuceneQueryParser.FIELD_PARENT_ASSOC_CRC, nodeMetaData.getParentAssocsCrc());

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
                aux.addField(AbstractLuceneQueryParser.FIELD_PARENT, childAssocRef.getParentRef());

                if (childAssocRef.isPrimary())
                {
                    aux.addField(AbstractLuceneQueryParser.FIELD_PRIMARYPARENT, childAssocRef.getParentRef());
                    aux.addField(AbstractLuceneQueryParser.FIELD_PRIMARYASSOCTYPEQNAME, ISO9075.getXPathName(childAssocRef.getTypeQName()));
                    aux.addField(AbstractLuceneQueryParser.FIELD_PRIMARYASSOCQNAME, ISO9075.getXPathName(childAssocRef.getQName()));
                }
            }
            aux.addField(AbstractLuceneQueryParser.FIELD_ASSOCTYPEQNAME, assocTypeQNameBuffer.toString());
            aux.addField(AbstractLuceneQueryParser.FIELD_QNAME, qNameBuffer.toString());
        }
        return aux;
    }

    private boolean mayHaveChildren(NodeMetaData nodeMetaData)
    {
        // 1) Does the type support children?
        TypeDefinition nodeTypeDef = dataModel.getDictionaryService().getType(nodeMetaData.getType());
        if ((nodeTypeDef != null) && (nodeTypeDef.getChildAssociations().size() > 0))
        {
            return true;
        }
        // 2) Do any of the applied aspects support children?
        for (QName aspect : nodeMetaData.getAspects())
        {
            AspectDefinition aspectDef = dataModel.getDictionaryService().getAspect(aspect);
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
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".size", contentPropertyValue.getLength());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".locale", contentPropertyValue.getLocale());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".mimetype", contentPropertyValue.getMimetype());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".encoding", contentPropertyValue.getEncoding());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".contentDataId", contentPropertyValue.getId());
        GetTextContentResponse response = client.getTextContent(nodeMetaData.getId(), propertyQName, null);
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationStatus", response.getStatus());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationTime", response.getTransformDuration());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationException", response.getTransformException());

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
            doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString(), multiReader);

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
            doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__", multiReader);
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
                    doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString(), builder.toString());
                    doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__", builder.toString());
                }
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".u", builder.toString());
                    doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__.u", builder.toString());
                }

                if (sort.length() > 0)
                {
                    sort.append("\u0000");
                }
                sort.append(builder.toString());
            }

            if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
            {
                doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort", sort.toString());
            }
        }
        else
        {
            for (Locale locale : mlTextPropertyValue.getLocales())
            {
                doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString(), mlTextPropertyValue.getValue(locale));
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
                doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString(), stringPropertyValue.getValue());
                doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort", stringPropertyValue.getValue());
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
                    doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString(), builder.toString());
                    doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__", builder.toString());
                }
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".u", builder.toString());
                    doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__.u", builder.toString());
                }

                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort", builder.toString());
                }

            }
            else
            {
                doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString(), stringPropertyValue.getValue());
            }

        }
        else
        {
            doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString(), stringPropertyValue.getValue());
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

        // DB TX Count
        OpenBitSet txIdsInDb = new OpenBitSet();
        Long lastTxCommitTime = Long.valueOf(0);
        if (fromTime != null)
        {
            lastTxCommitTime = fromTime;
        }
        long maxTxId = 0;

        long loopStartingCommitTime;
        List<Transaction> transactions;
        DO: do
        {
            loopStartingCommitTime = lastTxCommitTime;
            transactions = client.getTransactions(lastTxCommitTime, fromTx, 2000);
            for (Transaction info : transactions)
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
            }
        }
        while ((transactions.size() > 0) && (loopStartingCommitTime < lastTxCommitTime));

        indexHealthReport.setDbTransactionCount(txIdsInDb.cardinality());

        // DB ACL TX Count

        OpenBitSet aclTxIdsInDb = new OpenBitSet();
        Long lastAclTxCommitTime = Long.valueOf(0);
        if (fromTime != null)
        {
            lastAclTxCommitTime = fromTime;
        }
        long maxAclTxId = 0;

        List<AclChangeSet> aclTransactions;
        DO: do
        {
            loopStartingCommitTime = lastAclTxCommitTime;
            aclTransactions = client.getAclChangeSets(lastAclTxCommitTime, fromAclTx, 2000);
            for (AclChangeSet set : aclTransactions)
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
            }
        }
        while ((aclTransactions.size() > 0) && (loopStartingCommitTime < lastAclTxCommitTime));

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
                    Term term = new Term(AbstractLuceneQueryParser.FIELD_TXID, target);
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
                for (long i = minAclTxId; i <= maxTxId; i++)
                {
                    int docCount = 0;
                    String target = NumericEncoder.encode(i);
                    Term term = new Term(AbstractLuceneQueryParser.FIELD_ACLTXID, target);
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
            TermEnum termEnum = reader.terms(new Term(AbstractLuceneQueryParser.FIELD_ID, "LEAF-"));
            do
            {
                Term term = termEnum.term();
                if (term.field().equals(AbstractLuceneQueryParser.FIELD_ID) && term.text().startsWith("LEAF-"))
                {
                    int docCount = 0;
                    TermDocs termDocs = reader.termDocs(new Term(AbstractLuceneQueryParser.FIELD_ID, term.text()));
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

                    leafCount++;
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
            termEnum = reader.terms(new Term(AbstractLuceneQueryParser.FIELD_ID, "AUX-"));
            do
            {
                Term term = termEnum.term();
                if (term.field().equals(AbstractLuceneQueryParser.FIELD_ID) && term.text().startsWith("AUX-"))
                {
                    int docCount = 0;
                    TermDocs termDocs = reader.termDocs(new Term(AbstractLuceneQueryParser.FIELD_ID, term.text()));
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
                        indexHealthReport.setDuplicatedAuxInIndex(txid);
                    }

                    auxCount++;
                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();

            indexHealthReport.setAuxDocCountInIndex(auxCount);

            // Other

            indexHealthReport.setLastIndexedCommitTime(lastIndexedCommitTime);
            indexHealthReport.setLastIndexedIdBeforeHoles(lastIndexedIdBeforeHoles);

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

        TermEnum termEnum = reader.terms(new Term(AbstractLuceneQueryParser.FIELD_TXCOMMITTIME, ""));
        do
        {
            Term term = termEnum.term();
            if (term == null)
            {
                break;
            }
            if (term.field().equals(AbstractLuceneQueryParser.FIELD_TXCOMMITTIME))
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
            TermDocs docs = reader.termDocs(new Term(AbstractLuceneQueryParser.FIELD_TXCOMMITTIME, NumericEncoder.encode(lastTxCommitTimeBeforeHoles)));
            while (docs.next())
            {
                Document doc = reader.document(docs.doc());
                Fieldable field = doc.getFieldable(AbstractLuceneQueryParser.FIELD_TXID);
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
            TermEnum termEnum = reader.terms(new Term(AbstractLuceneQueryParser.FIELD_TXCOMMITTIME, ""));
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals(AbstractLuceneQueryParser.FIELD_TXCOMMITTIME))
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

    public List<Long> getNodesForDbTransaction(Long txid)
    {

        try
        {
            ArrayList<Long> answer = new ArrayList<Long>();
            GetNodesParameters gnp = new GetNodesParameters();
            ArrayList<Long> txs = new ArrayList<Long>();
            txs.add(txid);
            gnp.setTransactionIds(txs);
            gnp.setStoreProtocol(storeRef.getProtocol());
            gnp.setStoreIdentifier(storeRef.getIdentifier());
            List<Node> nodes;
            nodes = client.getNodes(gnp, Integer.MAX_VALUE);
            for (Node node : nodes)
            {
                answer.add(node.getId());
            }
            return answer;
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
    public List<Long> getAclsForDbAclTransaction(Long acltxid)
    {
        try
        {
            ArrayList<Long> answer = new ArrayList<Long>();
            List<AclChangeSet> changeSet = client.getAclChangeSets(null, acltxid, 1);
            List<Acl> acls = client.getAcls(changeSet, null, Integer.MAX_VALUE);
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

            dataModel.putModel(model);
            loadedModels.add(modelName);
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
            adminHandler.getScheduler().deleteJob("CoreTracker-" + core.getName(), "Solr");
            adminHandler.getTrackers().remove(core.getName());
            if (adminHandler.getTrackers().size() == 0)
            {
                if (!adminHandler.getScheduler().isShutdown())
                {
                    adminHandler.getScheduler().shutdown();
                }
            }
        }
        catch (SchedulerException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}