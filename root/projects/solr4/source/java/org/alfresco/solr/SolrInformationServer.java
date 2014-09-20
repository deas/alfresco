/*
 * Copyright (C) 2014 Alfresco Software Limited.
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

package org.alfresco.solr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.model.ContentModel;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.repo.content.ContentContext;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoSolrDataModel.FieldInstance;
import org.alfresco.solr.AlfrescoSolrDataModel.IndexedField;
import org.alfresco.solr.AlfrescoSolrDataModel.TenantAclIdDbId;
import org.alfresco.solr.adapters.IOpenBitSet;
import org.alfresco.solr.adapters.ISimpleOrderedMap;
import org.alfresco.solr.adapters.SolrOpenBitSetAdapter;
import org.alfresco.solr.adapters.SolrSimpleOrderedMap;
import org.alfresco.solr.client.AclChangeSet;
import org.alfresco.solr.client.AclReaders;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.client.ContentPropertyValue;
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
import org.alfresco.solr.content.SolrContentStore;
import org.alfresco.solr.content.SolrContentUrlBuilder;
import org.alfresco.solr.tracker.IndexHealthReport;
import org.alfresco.solr.tracker.TrackerStats;
import org.alfresco.util.ISO9075;
import org.alfresco.util.Pair;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.JavaBinCodec;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.IndexDeletionPolicyWrapper;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrInfoMBean;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.ResultContext;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.DeleteUpdateCommand;
import org.apache.solr.update.RollbackUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.util.RefCounted;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.util.FileCopyUtils;

/**
 * This is the Solr4 implementation of the information server (index).
 * @author Ahmed Owian
 */
public class SolrInformationServer implements InformationServer, QueryConstants
{
    /**
     * 
     */
    private static final String NO_SITE = "_REPOSITORY_";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String REQUEST_HANDLER_ALFRESCO_FULL_TEXT_SEARCH = "/afts";
    public static final String REQUEST_HANDLER_SELECT = "/select";
    public static final String REQUEST_HANDLER_ALFRESCO = "/alfresco";
    public static final String REQUEST_HANDLER_GET = "/get";
    public static final String RESPONSE_DEFAULT = "response";
    
    public static final String PREFIX_ERROR = "ERROR-";
    
    public static final String DOC_TYPE_NODE = "Node";
    public static final String DOC_TYPE_UNINDEXED_NODE = "UnindexedNode";
    public static final String DOC_TYPE_ERROR_NODE = "ErrorNode";
    public static final String DOC_TYPE_ACL = "Acl";
    public static final String DOC_TYPE_TX = "Tx";
    public static final String DOC_TYPE_ACL_TX = "AclTx";
    public static final String DOC_TYPE_STATE = "State";
 
    private static final Pattern CAPTURE_SITE = Pattern.compile("^/\\{http\\://www\\.alfresco\\.org/model/application/1\\.0\\}company\\_home/\\{http\\://www\\.alfresco\\.org/model/site/1\\.0\\}sites/\\{http\\://www\\.alfresco\\.org/model/content/1\\.0}([^/]*)/.*" ); 
    private static final Pattern CAPTURE_TAG = Pattern.compile("^/\\{http\\://www\\.alfresco\\.org/model/content/1\\.0\\}taggable/\\{http\\://www\\.alfresco\\.org/model/content/1\\.0\\}([^/]*)/\\{\\}member");
    
    private AlfrescoCoreAdminHandler adminHandler;
    private SolrCore core;
    private SolrRequestHandler selectRequestHandler;
    private Cloud cloud;
    private TrackerStats trackerStats = new TrackerStats(this);
    private AlfrescoSolrDataModel dataModel;
    private SolrContentStore solrContentStore;
    private String alfrescoVersion;
    private boolean transformContent = true;
    private long lag;
    private long holeRetention;
    
    // Metadata pulling control
    private boolean skipDescendantDocsForSpecificTypes;
    private Set<QName> typesForSkippingDescendantDocs = new HashSet<QName>();
    private BooleanQuery skippingDocsQuery;
    private String skippingDocsQueryString;
    private SOLRAPIClient repositoryClient;
    
    protected final static Logger log = LoggerFactory.getLogger(SolrInformationServer.class);
    protected enum FTSStatus {New, Dirty, Clean}; //TODO store this somewhere common
    
    // write a BytesRef as a byte array
    JavaBinCodec.ObjectResolver resolver = new JavaBinCodec.ObjectResolver() {
      @Override
      public Object resolve(Object o, JavaBinCodec codec) throws IOException {
        if (o instanceof BytesRef) {
          BytesRef br = (BytesRef)o;
          codec.writeByteArray(br.bytes, br.offset, br.length);
          return null;
        }
        return o;
      }
    };
    

    public SolrInformationServer(AlfrescoCoreAdminHandler adminHandler, SolrCore core, SOLRAPIClient repositoryClient,
                SolrContentStore solrContentStore)
    {
        this.adminHandler = adminHandler;
        this.core = core;
        this.selectRequestHandler = core.getRequestHandler(REQUEST_HANDLER_SELECT);
        this.cloud = new Cloud();
        this.repositoryClient = repositoryClient;
        this.solrContentStore = solrContentStore;

        Properties p = core.getResourceLoader().getCoreProperties();
        alfrescoVersion = p.getProperty("alfresco.version", "4.2.2");
        transformContent = Boolean.parseBoolean(p.getProperty("alfresco.index.transformContent", "true"));
        lag = Integer.parseInt(p.getProperty("alfresco.lag", "1000"));
        holeRetention = Integer.parseInt(p.getProperty("alfresco.hole.retention", "3600000"));
        
        dataModel = AlfrescoSolrDataModel.getInstance();

        skipDescendantDocsForSpecificTypes = Boolean.parseBoolean(p.getProperty("alfresco.metadata.skipDescendantDocsForSpecificTypes", "false"));

        if (skipDescendantDocsForSpecificTypes)
        {
            int i = 0;
            for (String key = new StringBuilder(PROP_PREFIX_PARENT_TYPE).append(i).toString(); p.containsKey(key); 
                        key = new StringBuilder(PROP_PREFIX_PARENT_TYPE).append(++i).toString())
            {
                String qName = p.getProperty(key);
                if ((null != qName) && !qName.isEmpty())
                {
                    QName typeQName = QName.resolveToQName(dataModel.getNamespaceDAO(), qName);
                    TypeDefinition type = dataModel.getDictionaryService(CMISStrictDictionaryService.DEFAULT).getType(typeQName);
                    if (null != type)
                    {
                        typesForSkippingDescendantDocs.add(typeQName);
                    }
                }
            }
            
            skippingDocsQueryString = this.cloud.getQuery(FIELD_TYPE, OR, typesForSkippingDescendantDocs);
        }
    }

    public String getAlfrescoVersion()
    {
        return this.alfrescoVersion;
    }
    
    @Override
    public void afterInitModels()
    {
        this.dataModel.afterInitModels();
    }

    @Override
    public AclReport checkAclInIndex(Long aclid, AclReport aclReport)
    {
        String query = FIELD_ACLID + ":" + aclid + AND + FIELD_DOC_TYPE + ":" + DOC_TYPE_ACL;
        int count = this.getDocListSize(query);
        aclReport.setIndexedAclDocCount(new Long(count));
        
        // TODO Could add INACLTXID later
        return aclReport;
    }

    @Override
    public IndexHealthReport checkIndexTransactions(IndexHealthReport indexHealthReport, Long minTxId, Long minAclTxId,
                IOpenBitSet txIdsInDb, long maxTxId, IOpenBitSet aclTxIdsInDb, long maxAclTxId) throws IOException
    {
        // TODO This can be local
        
        return new IndexHealthReport(this);
    }
    
    @Override
    public List<TenantAclIdDbId> getDocsWithUncleanContent(int start, int rows) throws IOException
    {
        SolrQueryRequest request = null;
        try
        {
            request = getLocalSolrQueryRequest();
            ModifiableSolrParams params = new ModifiableSolrParams(request.getParams());
            String query = FIELD_FTSSTATUS + ":" + FTSStatus.Dirty + " OR " + FIELD_FTSSTATUS + ":" + FTSStatus.New;
            params.set("q", query)
                .set("fl", FIELD_SOLR4_ID)
                .set("rows", rows)
                .set("start", start);
            List<TenantAclIdDbId> docIds = new ArrayList<>();
            SolrDocumentList docList = cloud.getSolrDocumentList(selectRequestHandler, request, params);
            if (docList != null)
            {
                for (SolrDocument doc : docList)
                {
                    String id = getFieldValueString(doc, FIELD_SOLR4_ID);
                    TenantAclIdDbId tenantAndDbId = AlfrescoSolrDataModel.decodeSolr4id(id);
                    docIds.add(tenantAndDbId);
                }
            }
            
            return docIds;
        }
        finally
        {
            if(request != null){request.close();}
        }
    }

    private String getFieldValueString(SolrDocument doc, String fieldName)
    {
        IndexableField field = (IndexableField)doc.getFieldValue(fieldName);
        String value = null;
        if (field != null)
        {
            value = field.stringValue();
        }
        return value;
    }
    
    private long getFieldValueLong(SolrDocument doc, String fieldName)
    {
        return Long.parseLong(getFieldValueString(doc, fieldName));
    }

    @Override
    public void addCommonNodeReportInfo(NodeReport nodeReport)
    {
        long dbId = nodeReport.getDbid();
        String query = FIELD_DBID + ":" + dbId + AND + FIELD_DOC_TYPE + ":" + DOC_TYPE_NODE;
        int count = this.getDocListSize(query);
        nodeReport.setIndexedNodeDocCount(new Long(count));
    }
    
    @Override
    public void commit() throws IOException
    {
        SolrQueryRequest request = null;
        UpdateRequestProcessor processor = null;
        try
        {
            request = getLocalSolrQueryRequest();
            processor = this.core.getUpdateProcessingChain(null).createProcessor(request, new SolrQueryResponse());
            processor.processCommit(new CommitUpdateCommand(request, false));
        }
        finally
        {
            if(processor != null) {processor.finish();}
            if(request != null) {request.close();}
        }
    }
    
    private void deleteById(String field, Long id) throws IOException
    {
        String query = field + ":" + id;
        deleteByQuery(query);
    }
    
    @Override
    public void deleteByAclChangeSetId(Long aclChangeSetId) throws IOException
    {
        deleteById(FIELD_INACLTXID, aclChangeSetId);
    }
    
    @Override
    public void deleteByAclId(Long aclId) throws IOException
    {
        deleteById(FIELD_ACLID, aclId);
    }
    
    @Override
    public void deleteByNodeId(Long nodeId) throws IOException
    {
        deleteById(FIELD_DBID, nodeId);
    }
    
    @Override
    public void deleteByTransactionId(Long transactionId) throws IOException
    {
        deleteById(FIELD_INTXID, transactionId);
    }
    
    private void deleteByQuery(String query) throws IOException
    {
        SolrQueryRequest request = null;
        UpdateRequestProcessor processor = null;
        try
        {
            request = getLocalSolrQueryRequest();
            processor = this.core.getUpdateProcessingChain(null).createProcessor(request, new SolrQueryResponse());
            DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(request);
            delDocCmd.setQuery(query);
            processor.processDelete(delDocCmd);
        }
        finally
        {
            if(processor != null) {processor.finish();}
            if(request != null) {request.close();}
        }
    }

    @Override
    public List<AlfrescoModel> getAlfrescoModels()
    {
        return this.dataModel.getAlfrescoModels();
    }

    private int getSafeCount(NamedList docTypeCounts, String docType)
    {
        Integer count = (Integer) docTypeCounts.get(docType);
        return (count == null ? 0 : count.intValue());
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Iterable<Entry<String, Object>> getCoreStats() throws IOException
    {
        // This is still local, not totally cloud-friendly
        // TODO Make this cloud-friendly by aggregating the stats across the cloud
        
        SolrQueryRequest request = null;
        NamedList<Object> coreSummary = new SimpleOrderedMap<Object>();
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            request = getLocalSolrQueryRequest();
            ModifiableSolrParams params = new ModifiableSolrParams(request.getParams());
            params.set("q", "*:*")
                .set("rows", 0)
                .set("facet", true)
                .set("facet.field", FIELD_DOC_TYPE);
            SolrQueryResponse response = cloud.getResponse(selectRequestHandler, request, params);
            NamedList facetCounts = (NamedList) response.getValues().get("facet_counts");
            NamedList facetFields = (NamedList) facetCounts.get("facet_fields");
            NamedList docTypeCounts = (NamedList) facetFields.get(FIELD_DOC_TYPE);
            int aclCount = getSafeCount(docTypeCounts, DOC_TYPE_ACL);
            coreSummary.add("Alfresco Acls in Index", aclCount);
            int nodeCount = getSafeCount(docTypeCounts, DOC_TYPE_NODE);
            coreSummary.add("Alfresco Nodes in Index", nodeCount);
            int txCount = getSafeCount(docTypeCounts, DOC_TYPE_TX);
            coreSummary.add("Alfresco Transactions in Index", txCount);
            int aclTxCount = getSafeCount(docTypeCounts, DOC_TYPE_ACL_TX);
            coreSummary.add("Alfresco Acl Transactions in Index", aclTxCount);
            int stateCount = getSafeCount(docTypeCounts, DOC_TYPE_STATE);
            coreSummary.add("Alfresco States in Index", stateCount);
            int unindexedNodeCount = getSafeCount(docTypeCounts, DOC_TYPE_UNINDEXED_NODE);
            coreSummary.add("Alfresco Unindexed Nodes", unindexedNodeCount);
            int errorNodeCount = getSafeCount(docTypeCounts, DOC_TYPE_ERROR_NODE);
            coreSummary.add("Alfresco Error Nodes in Index", errorNodeCount);
            
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            coreSummary.add("Searcher", solrIndexSearcher.getStatistics());
            Map<String, SolrInfoMBean> infoRegistry = core.getInfoRegistry();
            for (String key : infoRegistry.keySet())
            {
                SolrInfoMBean infoMBean = infoRegistry.get(key);
                if (key.equals("/alfresco"))
                {
// TODO Do we really need to fixStats in solr4?
                    coreSummary.add("/alfresco", fixStats(infoMBean.getStatistics()));
                }
                if (key.equals("/afts"))
                {
                    coreSummary.add("/afts", fixStats(infoMBean.getStatistics()));
                }
                if (key.equals("/cmis"))
                {
                    coreSummary.add("/cmis", fixStats(infoMBean.getStatistics()));
                }
                if (key.equals("filterCache"))
                {
                    coreSummary.add("/filterCache", infoMBean.getStatistics());
                }
                if (key.equals("queryResultCache"))
                {
                    coreSummary.add("/queryResultCache", infoMBean.getStatistics());
                }
                if (key.equals("alfrescoAuthorityCache"))
                {
                    coreSummary.add("/alfrescoAuthorityCache", infoMBean.getStatistics());
                }
                if (key.equals("alfrescoPathCache"))
                {
                    coreSummary.add("/alfrescoPathCache", infoMBean.getStatistics());
                }
            }

            // Adds detailed stats for each registered searcher
            int searcherIndex = 0;
            List<SolrIndexSearcher> searchers = getRegisteredSearchers();
            for (SolrIndexSearcher searcher : searchers)
            {
                NamedList<Object> details = new SimpleOrderedMap<Object>();
                details.add("Searcher", searcher.getStatistics());
                coreSummary.add("Searcher-" + searcherIndex, details);
                searcherIndex++;
            }

            coreSummary.add("Number of Searchers", searchers.size());
            // This is zero for Solr4, whereas we had some local caches before
            coreSummary.add("Total Searcher Cache (GB)", 0);

            IndexDeletionPolicyWrapper delPolicy = core.getDeletionPolicy();
            IndexCommit indexCommit = delPolicy.getLatestCommit();
            // race?
            if (indexCommit == null)
            {
                indexCommit = solrIndexSearcher.getIndexReader().getIndexCommit();
            }
            if (indexCommit != null)
            {
                // Tells Solr to stop deleting things for 20 seconds so we can get a snapshot of all the files on the index
                delPolicy.setReserveDuration(solrIndexSearcher.getIndexReader().getVersion(), 20000);
                Long fileSize = 0L;

                File dir = new File(solrIndexSearcher.getPath());
                for (String name : (Collection<String>) indexCommit.getFileNames())
                {
                    File file = new File(dir, name);
                    if (file.exists())
                    {
                        fileSize += file.length();
                    }
                }

                DecimalFormat df = new DecimalFormat("###,###.######");
                coreSummary.add("On disk (GB)", df.format(fileSize / 1024.0f / 1024.0f / 1024.0f));
                coreSummary.add("Per node B", nodeCount > 0 ? fileSize / nodeCount : 0);
            }
        }
        finally
        {
            if(request != null)
            {
                request.close();
            }
            
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }

        return coreSummary;
    }
    
    private NamedList<Object> fixStats(NamedList<Object> namedList)
    {
        int sz = namedList.size();

        for (int i = 0; i < sz; i++)
        {
            Object value = namedList.getVal(i);
            if (value instanceof Number)
            {
                Number number = (Number) value;
                if (Float.isInfinite(number.floatValue()) || Float.isNaN(number.floatValue())
                            || Double.isInfinite(number.doubleValue()) || Double.isNaN(number.doubleValue()))
                {
                    namedList.setVal(i, null);
                }
            }
        }
        return namedList;
    }

    private List<SolrIndexSearcher> getRegisteredSearchers()
    {
        List<SolrIndexSearcher> searchers = new ArrayList<SolrIndexSearcher>();
        for (Entry<String, SolrInfoMBean> entry : core.getInfoRegistry().entrySet())
        {
            if (entry.getValue() != null)
            {
                if (entry.getValue().getName().equals(SolrIndexSearcher.class.getName()))
                {
                    if (!entry.getKey().equals("searcher"))
                    {
                        searchers.add((SolrIndexSearcher) entry.getValue());
                    }
                }
            }
        }
        return searchers;
    }
    
    @Override
    public DictionaryComponent getDictionaryService(String alternativeDictionary)
    {
        return this.dataModel.getDictionaryService(alternativeDictionary);
    }

    @Override
    public int getDocSetSize(String targetTxId, String targetTxCommitTime) throws IOException
    {
        return getDocListSize(FIELD_TXID + ":" + targetTxId + AND + FIELD_TXCOMMITTIME + ":" + targetTxCommitTime);
    }
    
    private int getDocListSize(String query)
    {
        SolrQueryRequest request = null;
        try 
        {
            request = this.getLocalSolrQueryRequest();
            ModifiableSolrParams params = new ModifiableSolrParams(request.getParams());
            params.set("q", query);
            // Sets the rows to zero, because we actually just want the count
            params.set("rows", 0);
            ResultContext resultContext = cloud.getResultContext(selectRequestHandler, request, params);
            int matches = resultContext.docs.matches();
            return matches;
        }
        finally
        {
            if (request != null) { request.close(); }
        }
    }

    @Override
    public Set<Long> getErrorDocIds() throws IOException
    {
        HashSet<Long> errorDocIds = new HashSet<Long>();
        
        SolrQueryRequest request = null;
        try 
        {
            request = this.getLocalSolrQueryRequest();
            String query = FIELD_DOC_TYPE + ":" + DOC_TYPE_ERROR_NODE;
            ModifiableSolrParams params = new ModifiableSolrParams(request.getParams());
            // Sets MAX_VALUE to get all the rows
            params.set("q", query)
                .set("fl", FIELD_SOLR4_ID)
                .set("rows", Integer.MAX_VALUE);
            SolrDocumentList docs = cloud.getSolrDocumentList(selectRequestHandler, request , params);
            for (SolrDocument doc : docs)
            {
                String id = getFieldValueString(doc, FIELD_SOLR4_ID);
                if (id.startsWith(PREFIX_ERROR))
                {
                    String nodeId = id.substring(PREFIX_ERROR.length());
                    errorDocIds.add(Long.valueOf(nodeId));
                }
            }
        }
        finally
        {
            if (request != null)
            {
                request.close();
            }
        }
        return errorDocIds;
    }

    @Override
    public long getHoleRetention()
    {
        return this.holeRetention;
    }

    @Override
    public M2Model getM2Model(QName modelQName)
    {
        return this.dataModel.getM2Model(modelQName);
    }

    @Override
    public Map<String, Set<String>> getModelErrors()
    {
        return AlfrescoSolrDataModel.getInstance().getModelErrors();
    }

    @Override
    public NamespaceDAO getNamespaceDAO()
    {
        return this.dataModel.getNamespaceDAO();
    }

    @Override
    public IOpenBitSet getOpenBitSetInstance()
    {
        return new SolrOpenBitSetAdapter();
    }

    @Override
    public int getRegisteredSearcherCount()
    {
        int count = getRegisteredSearchers().size();
        log.info(".... registered Searchers for " + core.getName() + " = " + count);
        return count;
    }

    @Override
    public <T> ISimpleOrderedMap<T> getSimpleOrderedMapInstance()
    {
        return new SolrSimpleOrderedMap<T>();
    }

    @Override
    public TrackerStats getTrackerStats()
    {
        return this.trackerStats;
    }

    @Override
    public TrackerState getTrackerInitialState()
    {
        SolrQueryRequest request = null;
        try
        {
            request = getLocalSolrQueryRequest();
            
            TrackerState state = new TrackerState();
            SolrRequestHandler handler = core.getRequestHandler(REQUEST_HANDLER_GET);
            SolrQueryResponse rsp = new SolrQueryResponse();
            
            ModifiableSolrParams newParams = new ModifiableSolrParams(request.getParams());
            newParams.set("ids", "TRACKER!STATE!ACLTX,TRACKER!STATE!TX");
            request.setParams(newParams);
            
            handler.handleRequest(request, rsp);
            
            @SuppressWarnings("rawtypes")
            NamedList values = rsp.getValues();
            SolrDocumentList response = (SolrDocumentList)values.get(RESPONSE_DEFAULT);
            SolrDocument acl = null;
            SolrDocument tx = null;
            if(response.getNumFound() > 0)
            {
                acl = response.get(0);
                if (state.getLastIndexedChangeSetCommitTime() == 0)
                {
                    state.setLastIndexedChangeSetCommitTime(getFieldValueLong(acl, FIELD_S_ACLTXCOMMITTIME));
                }

                if (state.getLastIndexedChangeSetId() == 0)
                {
                    state.setLastIndexedChangeSetId(getFieldValueLong(acl, FIELD_S_ACLTXID));
                }
            }
            if(response.getNumFound() > 1)
            {
                tx = response.get(1);
                if (state.getLastIndexedTxCommitTime() == 0)
                {
                    state.setLastIndexedTxCommitTime(getFieldValueLong(tx, FIELD_S_TXCOMMITTIME));
                }
                if (state.getLastIndexedTxId() == 0)
                {
                    state.setLastIndexedTxId(getFieldValueLong(tx, FIELD_S_TXID));
                }
            }
            
            long startTime = System.currentTimeMillis();
            state.setTimeToStopIndexing(startTime - lag);
            state.setTimeBeforeWhichThereCanBeNoHoles(startTime - holeRetention);

            long timeBeforeWhichThereCanBeNoTxHolesInIndex = state.getLastIndexedTxCommitTime() - holeRetention;
            state.setLastGoodTxCommitTimeInIndex(timeBeforeWhichThereCanBeNoTxHolesInIndex > 0 ? timeBeforeWhichThereCanBeNoTxHolesInIndex : 0);

            long timeBeforeWhichThereCanBeNoChangeSetHolesInIndex = state.getLastIndexedChangeSetCommitTime()
                        - holeRetention;
            state.setLastGoodChangeSetCommitTimeInIndex(timeBeforeWhichThereCanBeNoChangeSetHolesInIndex > 0 ? timeBeforeWhichThereCanBeNoChangeSetHolesInIndex : 0);
            
            return state;
           
        }
        finally
        {
            if(request != null) {request.close();}
        }
    }
    
    @Override
    public long indexAcl(List<AclReaders> aclReaderList, boolean overwrite) throws IOException
    {
        long start = System.nanoTime();
        
        SolrQueryRequest request = null;
        UpdateRequestProcessor processor = null;
        try
        {
            request = getLocalSolrQueryRequest();
            processor = this.core.getUpdateProcessingChain(null).createProcessor(request, new SolrQueryResponse());
            
            for (AclReaders aclReaders : aclReaderList)
            {
                AddUpdateCommand cmd = new AddUpdateCommand(request);
                cmd.overwrite = overwrite;
                SolrInputDocument input = new SolrInputDocument();
                String id = AlfrescoSolrDataModel.getAclDocumentId(aclReaders.getTenantDomain(), aclReaders.getId());
                input.addField(FIELD_SOLR4_ID, id);
                input.addField(FIELD_VERSION, "0");
                input.addField(FIELD_ACLID, aclReaders.getId());
                input.addField(FIELD_INACLTXID, aclReaders.getAclChangeSetId());
                String tenant = aclReaders.getTenantDomain();
                for (String reader : aclReaders.getReaders())
                {
                    reader = addTenantToAuthority(reader, tenant);
                    input.addField(FIELD_READER, reader);
                }
                for (String denied : aclReaders.getDenied())
                {
                    denied = addTenantToAuthority(denied, tenant);
                    input.addField(FIELD_DENIED, denied);
                }
                input.addField(FIELD_DOC_TYPE, DOC_TYPE_ACL);
                cmd.solrDoc = input;
                processor.processAdd(cmd);
            }
        }
        finally
        {
            if(processor != null) {processor.finish();}
            if(request != null) {request.close();}
        }

        long end = System.nanoTime();
        return (end - start);
    }
    
    /**
     * Adds tenant information to an authority, <strong>if required</strong>, such that jbloggs for tenant example.com
     * would become jbloggs@example.com
     * 
     * @param authority   The authority to mutate, if it matches certain types.
     * @param tenant      The tenant that will be added to the authority.
     * @return The new authority information
     */
    private String addTenantToAuthority(String authority, String tenant)
    {
        switch (AuthorityType.getAuthorityType(authority))
        {
            case GROUP:
            case EVERYONE:
            case GUEST:
                if (tenant.length() == 0)
                {
                    // Default tenant matches 4.0
                }
                else
                {
                    authority = authority + "@" + tenant;
                }
                break;
            default:
                break;
        }
        return authority;
    }
    
    private LocalSolrQueryRequest getLocalSolrQueryRequest()
    {
        LocalSolrQueryRequest req = new LocalSolrQueryRequest(core, new NamedList<>());
        return req;
    }

    @Override
    public void indexAclTransaction(AclChangeSet changeSet, boolean overwrite) throws IOException
    {
        SolrQueryRequest request = null;
        UpdateRequestProcessor processor = null;
        try
        {
            request = getLocalSolrQueryRequest();
            processor = this.core.getUpdateProcessingChain(null).createProcessor(request, new SolrQueryResponse());
            AddUpdateCommand cmd = new AddUpdateCommand(request);
            cmd.overwrite = overwrite;
            SolrInputDocument input = new SolrInputDocument();
            input.addField(FIELD_SOLR4_ID, AlfrescoSolrDataModel.getAclChangeSetDocumentId(changeSet.getId()));
            input.addField(FIELD_VERSION, "0");
            input.addField(FIELD_ACLTXID, changeSet.getId());
            input.addField(FIELD_INACLTXID, changeSet.getId());
            input.addField(FIELD_ACLTXCOMMITTIME, changeSet.getCommitTimeMs());
            input.addField(FIELD_DOC_TYPE, DOC_TYPE_ACL_TX);
            cmd.solrDoc = input;
            processor.processAdd(cmd);
            putAclTransactionState(processor, request, changeSet);
        }
        finally
        {
            if(processor != null) {processor.finish();}
            if(request != null) {request.close();}
        }
    }
    
    public void putAclTransactionState(UpdateRequestProcessor processor, SolrQueryRequest request, AclChangeSet changeSet) throws IOException
    {
        AddUpdateCommand cmd = new AddUpdateCommand(request);
        cmd.overwrite = true;
        SolrInputDocument input = new SolrInputDocument();
        input.addField(FIELD_SOLR4_ID, "TRACKER!STATE!ACLTX");
        input.addField(FIELD_VERSION, "0");
        input.addField(FIELD_S_ACLTXID, changeSet.getId());
        input.addField(FIELD_S_INACLTXID, changeSet.getId());
        input.addField(FIELD_S_ACLTXCOMMITTIME, changeSet.getCommitTimeMs());
        input.addField(FIELD_DOC_TYPE, DOC_TYPE_STATE);
        cmd.solrDoc = input;
        processor.processAdd(cmd);
    }

    @Override
    public void indexNode(Node node, boolean overwrite) throws IOException, AuthenticationException, JSONException
    {
        SolrQueryRequest request = null;
        UpdateRequestProcessor processor = null;
        try
        {
            request = getLocalSolrQueryRequest();
            processor = this.core.getUpdateProcessingChain(null).createProcessor(request, new SolrQueryResponse());
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
                    NodeMetaData nodeMetaData = createDeletedNodeMetaData(node);
                    nodeMetaDatas = Collections.singletonList(nodeMetaData);
                }
                else
                {
                    nodeMetaDatas = repositoryClient.getNodesMetaData(nmdp, Integer.MAX_VALUE);
                }
                
                NodeMetaData nodeMetaData = null;
                if (!nodeMetaDatas.isEmpty())
                {
                    nodeMetaData = nodeMetaDatas.get(0);
                    if (!(nodeMetaData.getTxnId() > node.getTxnId()))
                    {
                        LinkedHashSet<Long> visited = new LinkedHashSet<Long>();
                        updateDescendantDocs(nodeMetaData, overwrite, request, processor, visited);
                    }
                    // else, the node has moved on to a later transaction, and it will be indexed later
                }

                log.debug(".. deleting");
                if (nodeMetaData != null)
                {
                    this.removeDocFromContentStore(nodeMetaData);
                }
                deleteNode(processor, request, node);
            }

            if ((node.getStatus() == SolrApiNodeStatus.UPDATED) || (node.getStatus() == SolrApiNodeStatus.UNKNOWN))
            {
                log.info(".. updating");
                NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
                nmdp.setFromNodeId(node.getId());
                nmdp.setToNodeId(node.getId());

                List<NodeMetaData> nodeMetaDatas =  repositoryClient.getNodesMetaData(nmdp, Integer.MAX_VALUE);

                AddUpdateCommand addDocCmd = new AddUpdateCommand(request);
                addDocCmd.overwrite = overwrite;

                if (!nodeMetaDatas.isEmpty())
                {
                    NodeMetaData nodeMetaData = nodeMetaDatas.get(0);
                    if (!(nodeMetaData.getTxnId() > node.getTxnId()))
                    {
                        if (mayHaveChildren(nodeMetaData))
                        {
                            cascadeUpdate(nodeMetaData, overwrite, request, processor);
                        }
                    }
                    // else, the node has moved on to a later transaction, and it will be indexed later
                    
                    // check index control
                    Map<QName, PropertyValue> properties = nodeMetaData.getProperties();
                    StringPropertyValue pValue = (StringPropertyValue) properties.get(ContentModel.PROP_IS_INDEXED);
                    if (pValue != null)
                    {
                        Boolean isIndexed = Boolean.valueOf(pValue.getValue());
                        if (!isIndexed.booleanValue())
                        {
                            log.debug(".. clearing unindexed");
                            deleteNode(processor, request, node);

                            SolrInputDocument doc = createNewDoc(nodeMetaData, DOC_TYPE_UNINDEXED_NODE);
                            storeDocOnSolrContentStore(nodeMetaData, doc);
                            addDocCmd.solrDoc = doc;
                            processor.processAdd(addDocCmd);

                            long end = System.nanoTime();
                            this.trackerStats.addNodeTime(end - start);
                            return;
                        }
                    }
                    
                    // Make sure any unindexed or error doc is removed.
                    log.debug(".. deleting node " + node.getId());
                    deleteNode(processor, request, node);
                    
                    SolrInputDocument doc = createNewDoc(nodeMetaData, DOC_TYPE_NODE);
                    addToNewDocAndCache(nodeMetaData, doc);
                    addDocCmd.solrDoc = doc;
                    processor.processAdd(addDocCmd);
                } // Ends checking for a nodeMetaData
            } // Ends checking for updated or unknown node status
            long end = System.nanoTime();
            this.trackerStats.addNodeTime(end - start);
        }
        catch (Exception e)
        {
            // TODO: retry failed

            log.debug(".. deleting on exception");
            deleteNode(processor, request, node);

            AddUpdateCommand addDocCmd = new AddUpdateCommand(request);
            addDocCmd.overwrite = overwrite;

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField(FIELD_SOLR4_ID, PREFIX_ERROR + node.getId());
            doc.addField(FIELD_VERSION, "0");
            doc.addField(FIELD_DBID, node.getId());
            doc.addField(FIELD_INTXID, node.getTxnId());
            doc.addField(FIELD_EXCEPTION_MESSAGE, e.getMessage());
            doc.addField(FIELD_DOC_TYPE, DOC_TYPE_ERROR_NODE);

            StringWriter stringWriter = new StringWriter(4096);
            PrintWriter printWriter = new PrintWriter(stringWriter, true);
            try
            {
                e.printStackTrace(printWriter);
                String stack = stringWriter.toString();
                doc.addField(FIELD_EXCEPTION_STACK, stack.length() < 32766 ? stack : stack.substring(0, 32765));
            }
            finally
            {
                printWriter.close();
            }

            addDocCmd.solrDoc = doc;
            processor.processAdd(addDocCmd);
           
            log.warn("Node index failed and skipped for " + node.getId() + " in Tx " + node.getTxnId(), e);
        }
        finally
        {
            if(processor != null) {processor.finish();}
            if(request != null) {request.close();}
        }
    }
    

    private void updateDescendantDocs(NodeMetaData parentNodeMetaData, boolean overwrite,
                SolrQueryRequest request, UpdateRequestProcessor processor, LinkedHashSet<Long> stack) 
                            throws AuthenticationException, IOException, JSONException
    {
        if (stack.contains(parentNodeMetaData.getId()))
        {
            log.warn("Found descendant data loop for node id " + parentNodeMetaData.getId());
            log.warn("... stack to node =" + stack);
            return;
        }
        else
        {
            try
            {
                stack.add(parentNodeMetaData.getId());
                doUpdateDescendantDocs(parentNodeMetaData, overwrite, request, processor, stack);
            }
            finally
            {
                stack.remove(parentNodeMetaData.getId());
            }
        }
    }

    private void doUpdateDescendantDocs(NodeMetaData parentNodeMetaData, boolean overwrite,
                SolrQueryRequest request, UpdateRequestProcessor processor, LinkedHashSet<Long> stack) 
                            throws AuthenticationException, IOException, JSONException
    {
        if (skipDescendantDocsForSpecificTypes 
                    && typesForSkippingDescendantDocs.contains(parentNodeMetaData.getType()))
        {
            return;
        }
        
        HashSet<Long> childIds = new HashSet<Long>();

        if (parentNodeMetaData.getChildIds() != null)
        {
            childIds.addAll(parentNodeMetaData.getChildIds());
        }

        String query = FIELD_PARENT + ":\"" + parentNodeMetaData.getNodeRef() + "\"";
        ModifiableSolrParams params = new ModifiableSolrParams(request.getParams());
        params.set("q", query).set("fl", FIELD_SOLR4_ID).set("fq", "NOT ( " + skippingDocsQueryString + " )");
        SolrDocumentList docs = cloud.getSolrDocumentList(selectRequestHandler, request, params);
        for (SolrDocument doc : docs)
        {
            String id = getFieldValueString(doc, FIELD_SOLR4_ID);
            TenantAclIdDbId ids = AlfrescoSolrDataModel.decodeSolr4id(id);
            childIds.add(ids.dbId);
        }
        
        for (Long childId : childIds)
        {
            NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
            nmdp.setFromNodeId(childId);
            nmdp.setToNodeId(childId);
            nmdp.setIncludeAclId(false);
            nmdp.setIncludeAspects(false);
            nmdp.setIncludeChildAssociations(false);
            nmdp.setIncludeChildIds(false);
            nmdp.setIncludeNodeRef(false);
            nmdp.setIncludeOwner(false);
            nmdp.setIncludeParentAssociations(false);
            // We only care about the path and ancestors (which is included) for this case
            nmdp.setIncludePaths(true);
            nmdp.setIncludeProperties(false);
            nmdp.setIncludeType(false);
            nmdp.setIncludeTxnId(false);
            // Gets only one 
            List<NodeMetaData> nodeMetaDatas = repositoryClient.getNodesMetaData(nmdp, 1);
            
            if (!nodeMetaDatas.isEmpty())
            {
                NodeMetaData nodeMetaData = nodeMetaDatas.get(0);
                if (mayHaveChildren(nodeMetaData))
                {
                    updateDescendantDocs(nodeMetaData, overwrite, request, processor, stack);
                }
                
                log.debug("... cascade update child doc " + childId);
                // Gets the document that we have from the content store and updates it 
                String fixedTenantDomain = AlfrescoSolrDataModel.getTenantId(nodeMetaData.getTenantDomain());
                SolrInputDocument cachedDoc = retrieveDocFromSolrContentStore(fixedTenantDomain, nodeMetaData.getId());
                
                if (cachedDoc != null)
                {
                    for (Pair<String, QName> path : nodeMetaData.getPaths())
                    {
                        cachedDoc.removeField(FIELD_PATH);
                        cachedDoc.addField(FIELD_PATH, path.getFirst());
                    }
                    
                    if (nodeMetaData.getAncestors() != null)
                    {
// TODO: How many ancestors does one node have?  Should this be a for loop?
                        for (NodeRef ancestor : nodeMetaData.getAncestors())
                        {
                            cachedDoc.removeField(FIELD_ANCESTOR);
                            cachedDoc.addField(FIELD_ANCESTOR, ancestor.toString());
                        }
                    }
                    
                    AddUpdateCommand addDocCmd = new AddUpdateCommand(request);
                    addDocCmd.overwrite = overwrite;
                    addDocCmd.solrDoc = cachedDoc;
                    
                    processor.processAdd(addDocCmd);
                    storeDocOnSolrContentStore(fixedTenantDomain, nodeMetaData.getId(), cachedDoc);
                }
                else
                {
                    log.debug("... no child doc found to update " + childId);
                }
            }
        }
    }

    /**
     * Checks if a cascade update is necessary, and then updates descendants
     */
    private void cascadeUpdate(NodeMetaData nodeMetaData, boolean overwrite, SolrQueryRequest request, 
                UpdateRequestProcessor processor) throws AuthenticationException, IOException, JSONException
    {
        log.info(".. checking for path change");
        String query = FIELD_DBID + ":" + nodeMetaData.getId() + AND + FIELD_PARENT_ASSOC_CRC + ":"
                    + nodeMetaData.getParentAssocsCrc();
        boolean nodeHasSamePathAsBefore = cloud.exists(selectRequestHandler, request, query);
        if (nodeHasSamePathAsBefore)
        {
            log.debug("... found match");
        }
        else
        {
            query = FIELD_DBID + ":" + nodeMetaData.getId();
            boolean nodeHasBeenIndexed = cloud.exists(selectRequestHandler, request, query);
            if (nodeHasBeenIndexed)
            {
                log.debug("... cascade updating docs");
                LinkedHashSet<Long> visited = new LinkedHashSet<Long>();
                updateDescendantDocs(nodeMetaData, overwrite, request, processor, visited);
            }
            else
            {
                log.debug("... no doc to update");
            }
        }
    }

    private NodeMetaData createDeletedNodeMetaData(Node node)
    {
        NodeMetaData nodeMetaData = new NodeMetaData();
        nodeMetaData.setId(node.getId());
        nodeMetaData.setType(ContentModel.TYPE_DELETED);
        nodeMetaData.setNodeRef(new NodeRef(node.getNodeRef()));
        nodeMetaData.setTxnId(node.getTxnId());
        return nodeMetaData;
    }

    private SolrInputDocument createNewDoc(NodeMetaData nodeMetaData, String docType)
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(FIELD_SOLR4_ID, AlfrescoSolrDataModel.getNodeDocumentId(nodeMetaData.getTenantDomain(), 
                    nodeMetaData.getAclId(), nodeMetaData.getId()));
        doc.addField(FIELD_VERSION, 0);
        doc.addField(FIELD_DBID, nodeMetaData.getId());
        doc.addField(FIELD_LID, nodeMetaData.getNodeRef().toString());
        doc.addField(FIELD_INTXID, nodeMetaData.getTxnId());
        doc.addField(FIELD_DOC_TYPE, docType);
        doc.addField(FIELD_ACLID, nodeMetaData.getAclId());
        return doc;
    }
    

    @Override
    public void indexNodes(List<Node> nodes, boolean overwrite) throws IOException, AuthenticationException, JSONException
    {
        SolrQueryRequest request = null;
        UpdateRequestProcessor processor = null;
        try
        {
            request = getLocalSolrQueryRequest();
            processor = this.core.getUpdateProcessingChain(null).createProcessor(request, new SolrQueryResponse());
            long start = System.nanoTime();
            
            Map<Long, Node> nodeIdsToNodes = new HashMap<>();
            EnumMap<SolrApiNodeStatus, List<Long>> nodeStatusToNodeIds = new EnumMap<SolrApiNodeStatus, List<Long>>(SolrApiNodeStatus.class);
            categorizeNodes(nodes, nodeIdsToNodes, nodeStatusToNodeIds);
            List<Long> deletedNodeIds = mapNullToEmptyList(nodeStatusToNodeIds.get(SolrApiNodeStatus.DELETED));
            List<Long> unknownNodeIds = mapNullToEmptyList(nodeStatusToNodeIds.get(SolrApiNodeStatus.UNKNOWN));
            List<Long> updatedNodeIds = mapNullToEmptyList(nodeStatusToNodeIds.get(SolrApiNodeStatus.UPDATED));
            
            if (!deletedNodeIds.isEmpty() || !unknownNodeIds.isEmpty()) 
            {
                // fix up any secondary paths
                List<NodeMetaData> nodeMetaDatas = new ArrayList<>();
                
                // For all deleted nodes, fake the node metadata
                for (Long deletedNodeId : deletedNodeIds)
                {
                    Node node = nodeIdsToNodes.get(deletedNodeId);
                    NodeMetaData nodeMetaData = createDeletedNodeMetaData(node);
                    nodeMetaDatas.add(nodeMetaData);
                }
                
                if (!unknownNodeIds.isEmpty())
                {
                    NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
                    nmdp.setNodeIds(unknownNodeIds);
                    nodeMetaDatas.addAll(repositoryClient.getNodesMetaData(nmdp, Integer.MAX_VALUE));
                }
                
                for (NodeMetaData nodeMetaData : nodeMetaDatas)
                {
                    Node node = nodeIdsToNodes.get(nodeMetaData.getId());
                    if (nodeMetaData.getTxnId() > node.getTxnId())
                    {
                        // the node has moved on to a later transaction
                        // it will be indexed later
                        continue;
                    }
                    LinkedHashSet<Long> visited = new LinkedHashSet<Long>();
                    updateDescendantDocs(nodeMetaData, overwrite, request, processor, visited);
                }

                log.debug(".. deleting");
                DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(request);
                String query = this.cloud.getQuery(FIELD_DBID, OR, deletedNodeIds, unknownNodeIds);
                delDocCmd.setQuery(query);
                processor.processDelete(delDocCmd);
            }

            if (!updatedNodeIds.isEmpty() || !unknownNodeIds.isEmpty()) 
            {
                log.info(".. updating");
                NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
                List<Long> nodeIds = new LinkedList<>();
                nodeIds.addAll(updatedNodeIds);
                nodeIds.addAll(unknownNodeIds);
                nmdp.setNodeIds(nodeIds);

                // Fetches bulk metadata
                List<NodeMetaData> nodeMetaDatas =  repositoryClient.getNodesMetaData(nmdp, Integer.MAX_VALUE);

                for (NodeMetaData nodeMetaData : nodeMetaDatas)
                {
                    AddUpdateCommand addDocCmd = new AddUpdateCommand(request);
                    addDocCmd.overwrite = overwrite;
                    
                    Node node = nodeIdsToNodes.get(nodeMetaData.getId());
                    if (nodeMetaData.getTxnId() > node.getTxnId())
                    {
                        // the node has moved on to a later transaction
                        // it will be indexed later
                        continue;
                    }

                    if (mayHaveChildren(nodeMetaData))
                    {
                        cascadeUpdate(nodeMetaData, overwrite, request, processor);
                    }
                    
                    // check index control
                    Map<QName, PropertyValue> properties = nodeMetaData.getProperties();
                    StringPropertyValue pValue = (StringPropertyValue) properties.get(ContentModel.PROP_IS_INDEXED);
                    if (pValue != null)
                    {
                        Boolean isIndexed = Boolean.valueOf(pValue.getValue());
                        if (!isIndexed.booleanValue())
                        {
                            log.debug(".. clearing unindexed");
                            deleteNode(processor, request, node);

                            SolrInputDocument doc = createNewDoc(nodeMetaData, DOC_TYPE_UNINDEXED_NODE);
                            storeDocOnSolrContentStore(nodeMetaData, doc);
                            addDocCmd.solrDoc = doc;
                            processor.processAdd(addDocCmd);
                        }
                    }
                    
                    // Make sure any unindexed or error doc is removed.
                    log.debug(".. deleting node " + node.getId());
                    deleteNode(processor, request, node);
                    
                    SolrInputDocument doc = createNewDoc(nodeMetaData, DOC_TYPE_NODE);
                    addToNewDocAndCache(nodeMetaData, doc);
                    addDocCmd.solrDoc = doc;
                    processor.processAdd(addDocCmd);
                } // Ends iteration over nodeMetadatas
            } // Ends checking for the existence of updated or unknown node ids 
            long end = System.nanoTime();
            this.trackerStats.addNodeTime(end - start);
        }
        catch (Exception e)
        {
            // Bulk version failed, so do one at a time.
            for (Node node : nodes)
            {
                this.indexNode(node, true);
            }
        }
        finally
        {
            if(processor != null) {processor.finish();}
            if(request != null) {request.close();}
        }
      
    }

    private void addToNewDocAndCache(NodeMetaData nodeMetaData, SolrInputDocument newDoc) throws IOException,
                AuthenticationException
    {
        addFieldsToDoc(nodeMetaData, newDoc);
        SolrInputDocument cachedDoc = null;
        boolean isContentIndexedForNode = isContentIndexedForNode(nodeMetaData.getProperties());
        String fixedTenantDomain = AlfrescoSolrDataModel.getTenantId(nodeMetaData.getTenantDomain());
        if (isContentIndexedForNode)
        {
            cachedDoc = retrieveDocFromSolrContentStore(fixedTenantDomain, nodeMetaData.getId());
        }
        Map<QName, PropertyValue> properties = nodeMetaData.getProperties();
        addPropertiesToDoc(properties, isContentIndexedForNode, newDoc, cachedDoc, transformContent);
        if (isContentIndexedForNode)
        {
            // Now that the new doc is fully updated and is about to go to the Solr index, cache it.
            storeDocOnSolrContentStore(fixedTenantDomain, nodeMetaData.getId(), newDoc);
        }
    }

    private void addFieldsToDoc(NodeMetaData nodeMetaData, SolrInputDocument doc)
    {
        doc.addField(FIELD_TYPE, nodeMetaData.getType().toString());
        for (QName aspect : nodeMetaData.getAspects())
        {
            doc.addField(FIELD_ASPECT, aspect.toString());
            if(aspect.equals(ContentModel.ASPECT_GEOGRAPHIC))
            {
                String lat = ((StringPropertyValue)nodeMetaData.getProperties().get(ContentModel.PROP_LATITUDE)).getValue();
                String lon = ((StringPropertyValue)nodeMetaData.getProperties().get(ContentModel.PROP_LONGITUDE)).getValue();
                doc.addField(FIELD_GEO, lat + ", " + lon);
            }
        }
        doc.addField(FIELD_ISNODE, "T");
        // FIELD_FTSSTATUS is set when adding content properties to indicate whether or not the cache is clean.
               
        doc.addField(FIELD_TENANT, AlfrescoSolrDataModel.getTenantId(nodeMetaData.getTenantDomain()));

        boolean addedRepo = false;
        for (Pair<String, QName> path : nodeMetaData.getPaths())
        {
            boolean wasSiteOrTag = false;
            
            doc.addField(FIELD_PATH, path.getFirst());
            Matcher matcher = CAPTURE_SITE.matcher(path.getFirst());
            if(matcher.find())
            {
                wasSiteOrTag = true;
                doc.addField(FIELD_SITE, matcher.group(1));
            }
            
            matcher = CAPTURE_TAG.matcher(path.getFirst());
            if(matcher.find())
            {
                wasSiteOrTag = true;
                doc.addField(FIELD_TAG, ISO9075.decode(matcher.group(1)));
            }
            
            if(!addedRepo && !wasSiteOrTag)
            {
                addedRepo = true;
                doc.addField(FIELD_SITE, NO_SITE);
            }
        }

        for(List<String> namePath : nodeMetaData.getNamePaths())
        {
            StringBuilder builder = new StringBuilder();
            int i = 0;
            for(String element : namePath)
            {
                builder.append('/').append(element);
                doc.addField(FIELD_NPATH, "" + i++ + builder.toString());
            }
            
            builder = new StringBuilder();
            for(int j = 0;  j < namePath.size() - 1; j++)
            {
                String element = namePath.get(namePath.size() - 2 - j);
                builder.insert(0, element);
                builder.insert(0, '/');
                doc.addField(FIELD_PNAME, "" + j +  builder.toString());
            }
//            
//            if(namePath.size() > 1)
//            {
//                doc.addField(FIELD_PNAME, namePath.get(namePath.size() - 2));
//            }
        }
        
        if (nodeMetaData.getOwner() != null)
        {
            doc.addField(FIELD_OWNER, nodeMetaData.getOwner());
        }
        doc.addField(FIELD_PARENT_ASSOC_CRC, nodeMetaData.getParentAssocsCrc());

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
                doc.addField(FIELD_PARENT, childAssocRef.getParentRef());

                if (childAssocRef.isPrimary())
                {
                    doc.addField(FIELD_PRIMARYPARENT, childAssocRef.getParentRef());
                    doc.addField(FIELD_PRIMARYASSOCTYPEQNAME,
                                ISO9075.getXPathName(childAssocRef.getTypeQName()));
                    doc.addField(FIELD_PRIMARYASSOCQNAME, ISO9075.getXPathName(childAssocRef.getQName()));
                }
            }
            doc.addField(FIELD_ASSOCTYPEQNAME, assocTypeQNameBuffer.toString());
            doc.addField(FIELD_QNAME, qNameBuffer.toString());
        }
        if (nodeMetaData.getAncestors() != null)
        {
            for (NodeRef ancestor : nodeMetaData.getAncestors())
            {
                doc.addField(FIELD_ANCESTOR, ancestor.toString());
            }
        }
    }

    static void addPropertiesToDoc(Map<QName, PropertyValue> properties, boolean isContentIndexedForNode, 
                SolrInputDocument newDoc, SolrInputDocument cachedDoc, boolean transformContentFlag) 
                throws IOException
    {
        for (QName propertyQName : properties.keySet())
        {
            PropertyValue value = properties.get(propertyQName);
            if(value != null)
            {
                if (value instanceof StringPropertyValue)
                {
                    for( FieldInstance  field : AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(propertyQName).getFields())
                    {
                        addStringPropertyToDoc(newDoc, field, (StringPropertyValue) value, properties);
                    }
                }
                else if (value instanceof MLTextPropertyValue)
                {
                    for( FieldInstance  field : AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(propertyQName).getFields())
                    {
                        addMLTextPropertyToDoc(newDoc, field, (MLTextPropertyValue) value);
                    }

                }
                else if (value instanceof ContentPropertyValue)
                {
                    if (isContentIndexedForNode)
                    {
                        addContentPropertyToDocUsingCache(newDoc, cachedDoc, propertyQName, 
                                    (ContentPropertyValue) value, transformContentFlag);
                    }
                }
                else if (value instanceof MultiPropertyValue)
                {
                    MultiPropertyValue typedValue = (MultiPropertyValue) value;
                    for (PropertyValue singleValue : typedValue.getValues())
                    {
                        if (singleValue instanceof StringPropertyValue)
                        {
                            for (FieldInstance field : AlfrescoSolrDataModel.getInstance()
                                        .getIndexedFieldNamesForProperty(propertyQName).getFields())
                            {
                                addStringPropertyToDoc(newDoc, field, (StringPropertyValue) singleValue, properties);
                            }
                        }
                        else if (singleValue instanceof MLTextPropertyValue)
                        {
                            for (FieldInstance field : AlfrescoSolrDataModel.getInstance()
                                        .getIndexedFieldNamesForProperty(propertyQName).getFields())
                            {
                                addMLTextPropertyToDoc(newDoc, field, (MLTextPropertyValue) singleValue);
                            }

                        }
                        else if (singleValue instanceof ContentPropertyValue)
                        {
                            if (isContentIndexedForNode)
                            {
                                addContentPropertyToDocUsingCache(newDoc, cachedDoc, propertyQName,
                                            (ContentPropertyValue) singleValue, transformContentFlag);
                            }
                        }
                    }
                }
            }
        }
    }

    private void deleteNode(UpdateRequestProcessor processor, SolrQueryRequest request, Node node) throws IOException
    {
        DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(request);
        delDocCmd.setQuery(FIELD_DBID + ":" + node.getId());
        processor.processDelete(delDocCmd);
    }

    private boolean isContentIndexedForNode(Map<QName, PropertyValue> properties)
    {
        boolean isContentIndexed = true;
        if (properties.containsKey(ContentModel.PROP_IS_CONTENT_INDEXED))
        {
            StringPropertyValue pValue = (StringPropertyValue) properties
                        .get(ContentModel.PROP_IS_CONTENT_INDEXED);
            if (pValue != null)
            {
                Boolean isIndexed = Boolean.valueOf(pValue.getValue());
                if ((isIndexed != null) && (isIndexed.booleanValue() == false))
                {
                    isContentIndexed = false;
                }
            }
        }
        return isContentIndexed;
    }
    
    /**
     * @param list
     * @return
     */
    private List<Long> mapNullToEmptyList(List<Long> list)
    {
        return list == null ? Collections.<Long>emptyList() : list;
    }

    private void categorizeNodes(List<Node> nodes, Map<Long, Node> nodeIdsToNodes,
                EnumMap<SolrApiNodeStatus, List<Long>> nodeStatusToNodeIds)
    {
        for (Node node : nodes)
        {
            nodeIdsToNodes.put(node.getId(), node);
            
            List<Long> nodeIds = nodeStatusToNodeIds.get(node.getStatus());
            if (nodeIds == null)
            {
                nodeIds = new LinkedList<>();
                nodeStatusToNodeIds.put(node.getStatus(), nodeIds);
            }
            nodeIds.add(node.getId());
        }
    }

    /**
     * Gets the field name used in Solr for the specified content property.
     * Assumes that the first defined field in Solr is the "right one".
     * @param propertyQName the content property qualified name
     * @param type the content property field type, i.e. DOCID
     * @return a String representing the name of the field in Solr or null if not found
     */
    private static String getSolrFieldNameForContentPropertyMetadata(QName propertyQName, AlfrescoSolrDataModel.ContentFieldType type)
    {
        IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getIndexedFieldForContentPropertyMetadata(propertyQName, type);
        List<FieldInstance> fields = indexedField.getFields();
        String fieldName = null;
        if (fields != null && !fields.isEmpty())
        {
            FieldInstance instance = fields.get(0);
            if (instance != null)
            {
                fieldName = instance.getField();
            }
        }
        return fieldName;
    }

    private void addContentPropertyMetadata(SolrInputDocument doc, QName propertyQName, 
                AlfrescoSolrDataModel.ContentFieldType type, GetTextContentResponse textContentResponse)
    {
        IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getIndexedFieldForContentPropertyMetadata(
                    propertyQName, type);
        for (FieldInstance fieldInstance : indexedField.getFields())
        {
            doc.removeField(fieldInstance.getField());
            switch(type)
            {
            case TRANSFORMATION_EXCEPTION:
                doc.addField(fieldInstance.getField(), textContentResponse.getTransformException());
                break;
            case TRANSFORMATION_STATUS:
                doc.addField(fieldInstance.getField(), textContentResponse.getStatus());
                break;
            case TRANSFORMATION_TIME:
                doc.addField(fieldInstance.getField(), textContentResponse.getTransformDuration());
                break;
                // Skips the ones that require the ContentPropertyValue
                default:
                break;
            }
        }
    }

    private static void addContentPropertyMetadata(SolrInputDocument doc, QName propertyQName, 
                ContentPropertyValue contentPropertyValue, AlfrescoSolrDataModel.ContentFieldType type)
    {
        IndexedField indexedField = AlfrescoSolrDataModel.getInstance().getIndexedFieldForContentPropertyMetadata(
                    propertyQName, type);
        for (FieldInstance fieldInstance : indexedField.getFields())
        {
            switch(type)
            {
            case DOCID:
                doc.addField(fieldInstance.getField(), contentPropertyValue.getId());
                break;
            case ENCODING:
                doc.addField(fieldInstance.getField(), contentPropertyValue.getEncoding());
                break;
            case LOCALE:
                doc.addField(fieldInstance.getField(), contentPropertyValue.getLocale());
                break;
            case MIMETYPE:
                doc.addField(fieldInstance.getField(), contentPropertyValue.getMimetype());
                break;
            case SIZE:
                doc.addField(fieldInstance.getField(), contentPropertyValue.getLength());
                break;
                // Skips the ones that require the text content response
                default:
                break;
            }
        }
    }
    
    private static void addContentPropertyToDocUsingCache(SolrInputDocument newDoc, SolrInputDocument cachedDoc, 
                QName propertyQName, ContentPropertyValue contentPropertyValue, boolean transformContentFlag)
                    throws IOException
    {
        addContentPropertyMetadata(newDoc, propertyQName, contentPropertyValue, AlfrescoSolrDataModel.ContentFieldType.DOCID);
        addContentPropertyMetadata(newDoc, propertyQName, contentPropertyValue, AlfrescoSolrDataModel.ContentFieldType.SIZE);
        addContentPropertyMetadata(newDoc, propertyQName, contentPropertyValue, AlfrescoSolrDataModel.ContentFieldType.LOCALE);
        addContentPropertyMetadata(newDoc, propertyQName, contentPropertyValue, AlfrescoSolrDataModel.ContentFieldType.MIMETYPE);
        addContentPropertyMetadata(newDoc, propertyQName, contentPropertyValue, AlfrescoSolrDataModel.ContentFieldType.ENCODING);
        
        if (false == transformContentFlag) 
        {
            // Marks it as Clean so we do not get the actual content
            markFTSStatus(newDoc,  FTSStatus.Clean);
            return;
        }
        
        if (cachedDoc != null)
        {
            // Builds up the new solr doc from the cached content regardless of whether or not it is current
            List<FieldInstance> fields = AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(
                        propertyQName).getFields();
            for (FieldInstance  field : fields)
            {
                String fieldName = field.getField();
                Object cachedFieldValue = cachedDoc.getFieldValue(fieldName);
                newDoc.addField(fieldName, cachedFieldValue);
            }

            String transformationStatusFieldName = getSolrFieldNameForContentPropertyMetadata(propertyQName, 
                        AlfrescoSolrDataModel.ContentFieldType.TRANSFORMATION_STATUS);
            newDoc.addField(transformationStatusFieldName, cachedDoc.getFieldValue(transformationStatusFieldName));
            String transformationExceptionFieldName = getSolrFieldNameForContentPropertyMetadata(propertyQName, 
                        AlfrescoSolrDataModel.ContentFieldType.TRANSFORMATION_EXCEPTION);
            newDoc.addField(transformationExceptionFieldName, cachedDoc.getFieldValue(transformationExceptionFieldName));
            String transformationTimeFieldName = getSolrFieldNameForContentPropertyMetadata(propertyQName, 
                        AlfrescoSolrDataModel.ContentFieldType.TRANSFORMATION_TIME);
            newDoc.addField(transformationTimeFieldName, cachedDoc.getFieldValue(transformationTimeFieldName));

            // Gets the new content docid and compares to that of the cachedDoc to mark the content as clean/dirty
            String fldName = getSolrFieldNameForContentPropertyMetadata(propertyQName, 
                        AlfrescoSolrDataModel.ContentFieldType.DOCID);
            
            if(newDoc.getFieldValue(FIELD_FTSSTATUS) == null)
            {
                newDoc.addField(FIELD_FTSSTATUS, cachedDoc.getFieldValue(FIELD_FTSSTATUS));
            }
            
            if(cachedDoc.getFieldValue(fldName) != null)
            {
                long cachedDocContentDocid = Long.valueOf(String.valueOf(cachedDoc.getFieldValue(fldName)));
                long currentContentDocid = contentPropertyValue.getId();
                // If we have used out of date content we mark it as dirty
                // Otherwise we leave it alone - it could already be marked as dirty/New and require an update
                
                if (cachedDocContentDocid != currentContentDocid)
                {
                    // The cached content is out of date
                    markFTSStatus(newDoc, FTSStatus.Dirty);
                }
            }
            else
            {
                markFTSStatus(newDoc, FTSStatus.Dirty);
            }
        }
        else 
        {
            // There is not a SolrInputDocument in the solrContentStore, so no content is added now to the new solr doc
            markFTSStatus(newDoc, FTSStatus.New);
        }
    }
    
    @Override
    public void updateContentToIndexAndCache(long dbId, String tenant) throws Exception
    {
        SolrQueryRequest request = null;
        UpdateRequestProcessor processor = null;
        try
        {
            request = getLocalSolrQueryRequest();
            processor = this.core.getUpdateProcessingChain(null).createProcessor(request, new SolrQueryResponse()); 

            SolrInputDocument cachedDoc = retrieveDocFromSolrContentStore(tenant, dbId);
            if (cachedDoc != null)
            {
                addContentToCachedDoc(cachedDoc, dbId);
                // Marks as clean since the doc's content is now up to date
                markFTSStatus(cachedDoc, FTSStatus.Clean);
                storeDocOnSolrContentStore(tenant, dbId, cachedDoc);

                // Add to index
                AddUpdateCommand addDocCmd = new AddUpdateCommand(request);
                addDocCmd.overwrite = true;
                addDocCmd.solrDoc = cachedDoc;
                processor.processAdd(addDocCmd);
            }
            else
            {
                throw new Exception("This method should not be called unless there is a cached doc in the content store.");
            }
        }
        finally
        {
            if(processor != null) {processor.finish();}
            if(request != null) {request.close();}
        }
    }

    private static void markFTSStatus(SolrInputDocument doc, FTSStatus status)
    {
        doc.removeField(FIELD_FTSSTATUS);
        doc.addField(FIELD_FTSSTATUS, status.toString());
    }
    
    private void addContentToCachedDoc(SolrInputDocument cachedDoc, long dbId) throws UnsupportedEncodingException, AuthenticationException, IOException
    {
        Collection<String> fieldNames = cachedDoc.deepCopy().getFieldNames(); 
        for (String fieldName : fieldNames)
        {
            if (fieldName.startsWith(AlfrescoSolrDataModel.CONTENT_S_LOCALE_PREFIX))
            {
                String locale = String.valueOf(cachedDoc.getFieldValue(fieldName));
                String qNamePart = fieldName.substring(AlfrescoSolrDataModel.CONTENT_S_LOCALE_PREFIX.length());
                QName propertyQName = QName.createQName(qNamePart);
                addContentPropertyToDocUsingAlfrescoRepository(cachedDoc, propertyQName, dbId, locale);
            }
            // Could update multi content but it is broken ....
        }
    }
    
    private void addContentPropertyToDocUsingAlfrescoRepository(SolrInputDocument cachedDoc, 
                QName propertyQName, long dbId, String locale) 
                            throws AuthenticationException, IOException, UnsupportedEncodingException
    {
        long start = System.nanoTime();
        
        // Expensive call to be done with ContentTrakcer
        GetTextContentResponse response = repositoryClient.getTextContent(dbId, propertyQName, null);
        
        addContentPropertyMetadata(cachedDoc, propertyQName, AlfrescoSolrDataModel.ContentFieldType.TRANSFORMATION_STATUS, response);
        addContentPropertyMetadata(cachedDoc, propertyQName, AlfrescoSolrDataModel.ContentFieldType.TRANSFORMATION_EXCEPTION, response);
        addContentPropertyMetadata(cachedDoc, propertyQName, AlfrescoSolrDataModel.ContentFieldType.TRANSFORMATION_TIME, response);

        InputStream ris = response.getContent();
        String textContent = "";
        try
        {
            if (ris != null)
            {
                // Get and copy content
                byte[] bytes = FileCopyUtils.copyToByteArray(ris);
                textContent = new String( bytes, "UTF8");
            }
        }
        finally
        {
            // release the response only when the content has been read
            response.release();
        }
        
        long end = System.nanoTime();
        this.getTrackerStats().addDocTransformationTime(end - start);
        
        StringBuilder builder = new StringBuilder();
        builder.append("\u0000").append(locale).append("\u0000");
        builder.append(textContent);

        for (FieldInstance  field : AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(propertyQName).getFields())
        {
            cachedDoc.removeField(field.getField());
            if(field.isLocalised())
            {
                cachedDoc.addField(field.getField(), builder.toString());
            }
            else
            {
                cachedDoc.addField(field.getField(), textContent);
            }
        }
    }

    private void removeDocFromContentStore(NodeMetaData nodeMetaData)
    {
        String fixedTenantDomain = AlfrescoSolrDataModel.getTenantId(nodeMetaData.getTenantDomain());
        ContentContext contentContext = SolrContentUrlBuilder
                    .start()
                    .add(SolrContentUrlBuilder.KEY_TENANT, fixedTenantDomain)
                    .add(SolrContentUrlBuilder.KEY_DB_ID, String.valueOf(nodeMetaData.getId()))
                    .getContentContext();
        if (this.solrContentStore.exists(contentContext.getContentUrl()))
        {
            this.solrContentStore.delete(contentContext.getContentUrl());
        }
    }

    private void storeDocOnSolrContentStore(NodeMetaData nodeMetaData, SolrInputDocument doc) throws IOException
    {
        String fixedTenantDomain = AlfrescoSolrDataModel.getTenantId(nodeMetaData.getTenantDomain());
        storeDocOnSolrContentStore(fixedTenantDomain, nodeMetaData.getId(), doc);
    }
    
    private void storeDocOnSolrContentStore(String tenant, long dbId, SolrInputDocument doc) throws IOException
    {
        ContentContext contentContext = SolrContentUrlBuilder
                    .start()
                    .add(SolrContentUrlBuilder.KEY_TENANT, tenant)
                    .add(SolrContentUrlBuilder.KEY_DB_ID, String.valueOf(dbId))
                    .getContentContext();
        if (this.solrContentStore.exists(contentContext.getContentUrl()))
        {
            this.solrContentStore.delete(contentContext.getContentUrl());
        }
      
        ContentWriter writer = this.solrContentStore.getWriter(contentContext);
        try (
                    OutputStream contentOutputStream = writer.getContentOutputStream();
                    // Compresses the document
                    GZIPOutputStream gzip = new GZIPOutputStream(contentOutputStream);
            )
        {
            JavaBinCodec codec = new JavaBinCodec(resolver);
            codec.marshal(doc, gzip);
        }
    }

    private SolrInputDocument retrieveDocFromSolrContentStore(String tenant, long dbId) throws IOException
    {
        String contentUrl = SolrContentUrlBuilder
                    .start()
                    .add(SolrContentUrlBuilder.KEY_TENANT, tenant)
                    .add(SolrContentUrlBuilder.KEY_DB_ID, String.valueOf(dbId))
                    .get();
        if (!this.solrContentStore.exists(contentUrl))
        {
            return null;
        }
        ContentReader reader = this.solrContentStore.getReader(contentUrl);
        SolrInputDocument cachedDoc = null;
        if(reader.exists())
        {
            // try-with-resources statement closes all these InputStreams
            try (
                    InputStream contentInputStream = reader.getContentInputStream();
                    // Uncompresses the document
                    GZIPInputStream gzip = new GZIPInputStream(contentInputStream);
                    )
                    { 
                cachedDoc = (SolrInputDocument) new JavaBinCodec(resolver).unmarshal(gzip);
                    }
        }
        return cachedDoc;
    }
    
    private static void addMLTextPropertyToDoc(SolrInputDocument doc, FieldInstance field, MLTextPropertyValue mlTextPropertyValue) throws IOException
    {   
        if(field.isLocalised())
        {
            StringBuilder sort = new StringBuilder();
            for (Locale locale : mlTextPropertyValue.getLocales())
            {
                if(log.isDebugEnabled())
                {
                    log.debug("ML "+field.getField() + " in "+ locale+ " of "+mlTextPropertyValue.getValue(locale));
                }
                
                StringBuilder builder = new StringBuilder();
                builder.append("\u0000").append(locale.toString()).append("\u0000")
                .append(mlTextPropertyValue.getValue(locale));
       
                if(!field.isSort())
                {
                    doc.addField(field.getField(), builder.toString());
                }
                
                if (sort.length() > 0)
                {
                    sort.append("\u0000");
                }
                sort.append(builder.toString());
            }
            if(field.isSort())
            {
                doc.addField(field.getField(), sort.toString());
            }
        }
        else
        {
            for (Locale locale : mlTextPropertyValue.getLocales())
            {
                doc.addField(field.getField(), mlTextPropertyValue.getValue(locale));
            }
        }
    }
    
    private static void addStringPropertyToDoc(SolrInputDocument doc, FieldInstance field, StringPropertyValue stringPropertyValue, Map<QName, PropertyValue> properties) throws IOException
    {
        if(field.isLocalised())
        {
            Locale locale = null;

            PropertyValue localePropertyValue = properties.get(ContentModel.PROP_LOCALE);
            if (localePropertyValue != null)
            {
                locale = DefaultTypeConverter.INSTANCE.convert(Locale.class,
                        ((StringPropertyValue) localePropertyValue).getValue());
            }

            if (locale == null)
            {
                locale = I18NUtil.getLocale();
            }

            StringBuilder builder;
            builder = new StringBuilder();
            builder.append("\u0000").append(locale.toString()).append("\u0000")
            .append(stringPropertyValue.getValue());

            doc.addField(field.getField(), builder.toString());
        }
        else
        { 
            doc.addField(field.getField(), stringPropertyValue.getValue());
        }
    }
    
    private boolean mayHaveChildren(NodeMetaData nodeMetaData)
    {
        // 1) Does the type support children?
        TypeDefinition nodeTypeDef = dataModel
                    .getDictionaryService(CMISStrictDictionaryService.DEFAULT).getType(nodeMetaData.getType());
        if ((nodeTypeDef != null) && (nodeTypeDef.getChildAssociations().size() > 0)) { return true; }
        // 2) Do any of the applied aspects support children?
        for (QName aspect : nodeMetaData.getAspects())
        {
            AspectDefinition aspectDef = dataModel
                        .getDictionaryService(CMISStrictDictionaryService.DEFAULT).getAspect(aspect);
            if ((aspectDef != null) && (aspectDef.getChildAssociations().size() > 0)) { return true; }
        }
        return false;
    }
    
    @Override
    public void indexTransaction(Transaction info, boolean overwrite) throws IOException
    {
        SolrQueryRequest request = null;
        UpdateRequestProcessor processor = null;
        try
        {
            request = getLocalSolrQueryRequest();
            processor = this.core.getUpdateProcessingChain(null).createProcessor(request, new SolrQueryResponse());
    
            AddUpdateCommand cmd = new AddUpdateCommand(request);
            cmd.overwrite = overwrite;
            SolrInputDocument input = new SolrInputDocument();
            input.addField(FIELD_SOLR4_ID, AlfrescoSolrDataModel.getTransactionDocumentId(info.getId()));
            input.addField(FIELD_VERSION, 0);
            input.addField(FIELD_TXID, info.getId());
            input.addField(FIELD_INTXID, info.getId());
            input.addField(FIELD_TXCOMMITTIME, info.getCommitTimeMs());
            input.addField(FIELD_DOC_TYPE, DOC_TYPE_TX);
            cmd.solrDoc = input;
            processor.processAdd(cmd);

            putTransactionState(processor, request, info);
        }
        finally
        {
            if(processor != null) {processor.finish();}
            if(request != null) {request.close();}
        }
    }
    
    /**
     * 
     * @param processor
     * @param request
     * @param info
     * @throws IOException
     */
    public void putTransactionState(UpdateRequestProcessor processor, SolrQueryRequest request, Transaction info) throws IOException
    {
        AddUpdateCommand cmd = new AddUpdateCommand(request);
        cmd.overwrite = true;
        SolrInputDocument input = new SolrInputDocument();
        input.addField(FIELD_SOLR4_ID, "TRACKER!STATE!TX");
        input.addField(FIELD_VERSION, 0);
        input.addField(FIELD_S_TXID, info.getId());
        input.addField(FIELD_S_INTXID, info.getId());
        input.addField(FIELD_S_TXCOMMITTIME, info.getCommitTimeMs());
        input.addField(FIELD_DOC_TYPE, DOC_TYPE_STATE);
        cmd.solrDoc = input;
        processor.processAdd(cmd);
    }

    /**
     * @param 
     */
    @Override
    public boolean isInIndex(String ids) throws IOException
    {
        SolrQueryRequest request = null;
        try
        {
            request = getLocalSolrQueryRequest();

            SolrRequestHandler handler = core.getRequestHandler(REQUEST_HANDLER_GET);
            SolrQueryResponse rsp = new SolrQueryResponse();

            ModifiableSolrParams newParams = new ModifiableSolrParams(request.getParams());
            newParams.set("ids", ids);
            request.setParams(newParams);

            handler.handleRequest(request, rsp);

            @SuppressWarnings("rawtypes")
            NamedList values = rsp.getValues();
            SolrDocumentList response = (SolrDocumentList)values.get(RESPONSE_DEFAULT);
            return response.getNumFound() > 0;
        }
        finally
        {
            if(request != null) {request.close();}
        }
    }
    

    @Override
    public boolean putModel(M2Model model)
    {
        return this.dataModel.putModel(model);
    }

    @Override
    public void rollback() throws IOException
    {
        SolrQueryRequest request = null;
        UpdateRequestProcessor processor = null;
        try
        {
            request = getLocalSolrQueryRequest();
            processor = this.core.getUpdateProcessingChain(null).createProcessor(request, new SolrQueryResponse());
            processor.processRollback(new RollbackUpdateCommand(request));
        }
        finally
        {
            if(processor != null) {processor.finish();}
            if(request != null) {request.close();}
        }
        
    }
}
