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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.model.ContentModel;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoSolrDataModel.FieldInstance;
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
import org.alfresco.solr.tracker.IndexHealthReport;
import org.alfresco.solr.tracker.TrackerStats;
import org.alfresco.util.ISO9075;
import org.alfresco.util.NumericEncoder;
import org.alfresco.util.Pair;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.NumericUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrInfoMBean;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.DeleteUpdateCommand;
import org.apache.solr.update.RollbackUpdateCommand;
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
public class SolrInformationServer implements InformationServer
{

    private AlfrescoCoreAdminHandler adminHandler;
    private SolrCore core;
    private TrackerState trackerState = new TrackerState();
    private TrackerStats trackerStats = new TrackerStats(this);
    private AlfrescoSolrDataModel dataModel;
    private String alfrescoVersion;
    private int authorityCacheSize;
    private int filterCacheSize;
    private int pathCacheSize;
    private boolean transformContent = true;
    private long lag;
    private long holeRetention;
    
    // Metadata pulling control
    private boolean skipDescendantAuxDocsForSpecificTypes;
    private Set<QName> typesForSkippingDescendantAuxDocs = new HashSet<QName>();
    private BooleanQuery skippingDocsQuery;
    private SOLRAPIClient repositoryClient;
    protected final static Logger log = LoggerFactory.getLogger(SolrInformationServer.class);

    public SolrInformationServer(AlfrescoCoreAdminHandler adminHandler, SolrCore core, SOLRAPIClient repositoryClient)
    {
        this.adminHandler = adminHandler;
        this.core = core;
        this.repositoryClient = repositoryClient;

        Properties p = core.getResourceLoader().getCoreProperties();
        alfrescoVersion = p.getProperty("alfresco.version", "4.2.2");
        authorityCacheSize = Integer.parseInt(p.getProperty("solr.authorityCache.size", "64"));
        filterCacheSize = Integer.parseInt(p.getProperty("solr.filterCache.size", "64"));
        pathCacheSize = Integer.parseInt(p.getProperty("solr.pathCache.size", "64"));
        transformContent = Boolean.parseBoolean(p.getProperty("alfresco.index.transformContent", "true"));
        lag = Integer.parseInt(p.getProperty("alfresco.lag", "1000"));
        holeRetention = Integer.parseInt(p.getProperty("alfresco.hole.retention", "3600000"));
        
        dataModel = AlfrescoSolrDataModel.getInstance();
        

        skipDescendantAuxDocsForSpecificTypes = Boolean.parseBoolean(p.getProperty("alfresco.metadata.skipDescendantAuxDocsForSpecificTypes", "false"));

        if (skipDescendantAuxDocsForSpecificTypes)
        {
            int i = 0;
            skippingDocsQuery = new BooleanQuery();
            for (String key = new StringBuilder(PROP_PREFIX_PARENT_TYPE).append(i).toString(); p.containsKey(key); key = new StringBuilder(PROP_PREFIX_PARENT_TYPE).append(++i)
                    .toString())
            {
                String qName = p.getProperty(key);
                if ((null != qName) && !qName.isEmpty())
                {
                    QName typeQName = QName.resolveToQName(dataModel.getNamespaceDAO(), qName);
                    TypeDefinition type = dataModel.getDictionaryService(CMISStrictDictionaryService.DEFAULT).getType(typeQName);
                    if (null != type)
                    {
                        typesForSkippingDescendantAuxDocs.add(typeQName);
                        skippingDocsQuery.add(new TermQuery(new Term(QueryConstants.FIELD_TYPE, typeQName.toString())), Occur.SHOULD);
                    }
                }
            }
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

                String aclIdString = NumericEncoder.encode(aclid);
                DocSet docSet = solrIndexSearcher.getDocSet(new TermQuery(new Term("ACLID", aclIdString)));
                // should find leaf and aux
                for (DocIterator it = docSet.iterator(); it.hasNext(); /* */)
                {
                    int doc = it.nextDoc();

                    Document document = solrIndexSearcher.doc(doc);
                    IndexableField fieldable = document.getField("ID");
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
                    IndexableField fieldable = document.getField("ACLTXID");
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

    @Override
    public void checkCache() throws IOException
    {
       // There is no cache to check for SOLR 4
    }

    @Override
    public IndexHealthReport checkIndexTransactions(IndexHealthReport indexHealthReport, Long minTxId, Long minAclTxId,
                IOpenBitSet txIdsInDb, long maxTxId, IOpenBitSet aclTxIdsInDb, long maxAclTxId) throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NodeReport checkNodeCommon(NodeReport nodeReport)
    {
        long dbid = nodeReport.getDbid();
        RefCounted<SolrIndexSearcher> refCounted = null;

        try
        {
            refCounted = core.getSearcher(false, true, null);
            if (refCounted == null) { return nodeReport; }

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
                    IndexableField fieldable = document.getField("ID");
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
                    IndexableField fieldable = document.getField("TXID");
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
            // TODO: do what here?
        }

        return nodeReport;
    }

    @Override
    public void commit() throws IOException
    {
        this.core.getUpdateHandler().commit(new CommitUpdateCommand(getLocalSolrQueryRequest(), false));
    }

    @Override
    public void deleteByAclChangeSetId(Long aclChangeSetId) throws IOException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            Query query = new TermQuery(new Term(QueryConstants.FIELD_INACLTXID, NumericEncoder.encode(aclChangeSetId)));
            deleteByQuery(solrIndexSearcher, query);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }
    

    private void deleteByQuery(SolrIndexSearcher solrIndexSearcher, Query query) throws IOException
    {
        HashSet<String> idsToDelete = new HashSet<String>();

        DocSet docSet = solrIndexSearcher.getDocSet(query);
        if (docSet instanceof BitDocSet)
        {
            BitDocSet source = (BitDocSet) docSet;
            FixedBitSet openBitSet = source.getBits();
            int current = -1;
            while ((current = openBitSet.nextSetBit(current + 1)) != -1)
            {
                Document doc = solrIndexSearcher.doc(current, Collections.singleton(QueryConstants.FIELD_ID));
                IndexableField fieldable = doc.getField(QueryConstants.FIELD_ID);
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
                IndexableField fieldable = doc.getField(QueryConstants.FIELD_ID);
                if (fieldable != null)
                {
                    idsToDelete.add(fieldable.stringValue());
                }
            }
        }

        for (String idToDelete : idsToDelete)
        {
            DeleteUpdateCommand docCmd = new DeleteUpdateCommand(getLocalSolrQueryRequest());
            docCmd.setId(idToDelete);
            core.getUpdateHandler().delete(docCmd);
        }

    }

    @Override
    public void deleteByAclId(Long aclId) throws IOException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            Query query = new TermQuery(new Term(QueryConstants.FIELD_ACLID, NumericEncoder.encode(aclId)));
            deleteByQuery(solrIndexSearcher, query);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    @Override
    public void deleteByNodeId(Long nodeId) throws IOException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            Query query = new TermQuery(new Term(QueryConstants.FIELD_DBID, NumericEncoder.encode(nodeId)));
            deleteByQuery(solrIndexSearcher, query);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    @Override
    public void deleteByTransactionId(Long transactionId) throws IOException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            Query query = new TermQuery(new Term(QueryConstants.FIELD_INTXID, NumericEncoder.encode(transactionId)));
            deleteByQuery(solrIndexSearcher, query);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    @Override
    public List<AlfrescoModel> getAlfrescoModels()
    {
        return this.dataModel.getAlfrescoModels();
    }

    @Override
    public Iterable<Entry<String, Object>> getCoreStats() throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DictionaryComponent getDictionaryService(String alternativeDictionary)
    {
        return this.dataModel.getDictionaryService(alternativeDictionary);
    }

    @Override
    public int getDocSetSize(String targetTxId, String targetTxCommitTime) throws IOException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            BooleanQuery query = new BooleanQuery();
            query.add(new TermQuery(AlfrescoSolrDataModel.getLongTerm(QueryConstants.FIELD_TXID, targetTxId)), Occur.MUST);
            query.add(new TermQuery(AlfrescoSolrDataModel.getLongTerm(QueryConstants.FIELD_TXCOMMITTIME, targetTxCommitTime)), Occur.MUST);
            DocSet set = solrIndexSearcher.getDocSet(query);

            return set.size();
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    @Override
    public Set<Long> getErrorDocIds() throws IOException
    {
        // TODO Auto-generated method stub
        return null;
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
        HashSet<String> keys = new HashSet<String>();

        for (String key : core.getInfoRegistry().keySet())
        {
            SolrInfoMBean mBean = core.getInfoRegistry().get(key);
            if (mBean != null)
            {
                if (mBean.getName().equals(SolrIndexSearcher.class.getName()))
                {
                    if (!key.equals("searcher"))
                    {
                        keys.add(key);
                    }
                }
            }
        }

        log.info(".... registered Searchers for " + core.getName() + " = " + keys.size());
        return keys.size();

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
    public TrackerState getTrackerInitialState() throws IOException
    {
        TrackerState state = new TrackerState();

        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            IndexReader reader = solrIndexSearcher.getIndexReader();

            if (state.getLastIndexedTxCommitTime() == 0)
            {
                state.setLastIndexedTxCommitTime(getLargestTermValue(reader, QueryConstants.FIELD_TXCOMMITTIME, -1L));
            }

            if (state.getLastIndexedTxId() == 0)
            {
                state.setLastIndexedTxId(getLargestTermValue(reader, QueryConstants.FIELD_TXID, -1L));
            }

            if (state.getLastIndexedChangeSetCommitTime() == 0)
            {
                state.setLastIndexedChangeSetCommitTime(getLargestTermValue(reader, QueryConstants.FIELD_ACLTXCOMMITTIME, -1L));
            }

            if (state.getLastIndexedChangeSetId() == 0)
            {
                state.setLastIndexedChangeSetId(getLargestTermValue(reader, QueryConstants.FIELD_ACLTXID, -1L));
            }

            long startTime = System.currentTimeMillis();
            state.setTimeToStopIndexing(startTime - lag);
            state.setTimeBeforeWhichThereCanBeNoHoles(startTime - holeRetention);

            long timeBeforeWhichThereCanBeNoTxHolesInIndex = state.getLastIndexedTxCommitTime() - holeRetention;
            state.setLastGoodTxCommitTimeInIndex(getLargestTermValue(reader, QueryConstants.FIELD_TXCOMMITTIME, timeBeforeWhichThereCanBeNoTxHolesInIndex));

            long timeBeforeWhichThereCanBeNoChangeSetHolesInIndex = state.getLastIndexedChangeSetCommitTime()
                        - holeRetention;
            state.setLastGoodChangeSetCommitTimeInIndex(getLargestTermValue(reader, QueryConstants.FIELD_ACLTXCOMMITTIME, timeBeforeWhichThereCanBeNoChangeSetHolesInIndex));

            if (state.getLastGoodTxCommitTimeInIndex() > 0)
            {
                if (state.getLastIndexedTxIdBeforeHoles() == -1)
                {
                    state.setLastIndexedTxIdBeforeHoles(getStoredLongByLongTerm(reader, QueryConstants.FIELD_TXCOMMITTIME, QueryConstants.FIELD_TXID, state.getLastGoodTxCommitTimeInIndex()));
                            
                }
                else
                {
                    long currentBestFromIndex = getStoredLongByLongTerm(reader, QueryConstants.FIELD_TXCOMMITTIME, QueryConstants.FIELD_TXID, state.getLastGoodTxCommitTimeInIndex());
                    if (currentBestFromIndex > state.getLastIndexedTxIdBeforeHoles())
                    {
                        state.setLastIndexedTxIdBeforeHoles(currentBestFromIndex);
                    }
                }
            }

            if (state.getLastGoodChangeSetCommitTimeInIndex() > 0)
            {
                if (state.getLastIndexedChangeSetIdBeforeHoles() == -1)
                {
                    state.setLastIndexedChangeSetIdBeforeHoles(getStoredLongByLongTerm(reader, QueryConstants.FIELD_ACLTXCOMMITTIME, QueryConstants.FIELD_ACLTXID, state.getLastGoodTxCommitTimeInIndex()));
                }
                else
                {
                    long currentBestFromIndex = getStoredLongByLongTerm(reader, QueryConstants.FIELD_ACLTXCOMMITTIME, QueryConstants.FIELD_ACLTXID, state.getLastGoodTxCommitTimeInIndex());
                    if (currentBestFromIndex > state.getLastIndexedTxIdBeforeHoles())
                    {
                        state.setLastIndexedChangeSetIdBeforeHoles(currentBestFromIndex);
                    }
                }
            }
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }

        // Sets the trackerState only after it has been fully initialized
        this.trackerState = state;
        
        return this.trackerState;
    }

    /*
     * Find the largest numeric long term value in the given index field
     */
    private long getLargestTermValue(IndexReader reader, String fieldName, long limit)
    {
        long largestValue = 0;
        long value;
        
        for(AtomicReaderContext atomicReaderContext  : reader.leaves())
        {
            value = getLargestTermValue(atomicReaderContext.reader(), fieldName, limit);
            if(value > largestValue)
            {
                largestValue = value;
            }
        }
        return largestValue;
      
    }
    
    /*
     * Find the last numeric long term value
     * -1 indicates not found
     */
    private long getLargestTermValue(AtomicReader atomicReader, String fieldName, long limit)
    {
        long largestValue = -1;
        try
        {
            Terms terms = atomicReader.terms(fieldName);
            if(terms != null)
            {
                TermsEnum termEnum = terms.iterator(null);
                BytesRef bytesRef;
                while((bytesRef = termEnum.next()) != null)
                {
                    long value = NumericUtils.prefixCodedToLong(bytesRef);
                    if(limit > -1)
                    {
                        if(value < limit)
                        {
                            if(value > largestValue)
                            {
                                largestValue = value;
                            }
                        }
                        else
                        {
                            return largestValue;
                        }
                    }
                    else
                    {
                        if(value > largestValue)
                        {
                            largestValue = value;
                        }
                    }

                }
            }
        }
        catch (IOException e1)
        {
            // do nothing
        }
        return largestValue;
    }
    

    private long getStoredLongByLongTerm(IndexReader reader, String term, String fieldable, Long value) throws IOException
    {
        long storedValue = 0;
        
        for(AtomicReaderContext atomicReaderContext  : reader.leaves())
        {
            storedValue = getStoredLongByLongTerm(atomicReaderContext.reader(), term, fieldable, value);
            if(storedValue > 0L)
            {
                return storedValue;
            }
        }
        return storedValue;
    }
    
    private long getStoredLongByLongTerm(AtomicReader atomicReader, String term, String fieldable, Long value) throws IOException
    {
        long storedLong = -1L;
        if (value != -1L)
        {
            DocsEnum docsEnum = atomicReader.termDocsEnum(AlfrescoSolrDataModel.getLongTerm(term, value));
            if(docsEnum != null)
            {
                int docId = -1;
                while ((docId = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS)
                {
                    Document doc = atomicReader.document(docId);
                    IndexableField field = doc.getField(QueryConstants.FIELD_ACLTXID);
                    if (field != null)
                    {
                        long curentStoredLong = field.numericValue().longValue();
                        if (curentStoredLong > storedLong)
                        {
                            storedLong = curentStoredLong;
                        }
                    }
                }
            }
        }
        return storedLong;
    }
    
    
    @Override
    public TrackerState getTrackerState() 
    {
        return this.trackerState;
    }

    @Override
    public long indexAcl(List<AclReaders> aclReaderList, boolean overwrite) throws IOException
    {
        long start = System.nanoTime();
        for (AclReaders aclReaders : aclReaderList)
        {
            AddUpdateCommand cmd = new AddUpdateCommand(getLocalSolrQueryRequest());
            cmd.overwrite = overwrite;
            SolrInputDocument input = new SolrInputDocument();
            input.addField("id", AlfrescoSolrDataModel.getAclDocumentId(aclReaders.getTenantDomain(), aclReaders.getId()));
            // Do we need to get this right - the version stamp from the DB? 
            input.addField("_version_", "0");
            input.addField(QueryConstants.FIELD_ACLID, aclReaders.getId());
            input.addField(QueryConstants.FIELD_INACLTXID, aclReaders.getAclChangeSetId());
            String tenant = aclReaders.getTenantDomain();
            for (String reader : aclReaders.getReaders())
            {
                reader = addTenantToAuthority(reader, tenant);
                input.addField(QueryConstants.FIELD_READER, reader);
            }
            for (String denied : aclReaders.getDenied())
            {
                denied = addTenantToAuthority(denied, tenant);
                input.addField(QueryConstants.FIELD_DENIED, denied);
            }
            cmd.solrDoc = input;
            //cmd.doc = LegacySolrInformationServer.toDocument(cmd.getSolrInputDocument(), core.getSchema(),  dataModel);
            core.getUpdateHandler().addDoc(cmd);
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
        LocalSolrQueryRequest req = new LocalSolrQueryRequest(core, new NamedList<>());
        AddUpdateCommand cmd = new AddUpdateCommand(getLocalSolrQueryRequest());
        cmd.overwrite = overwrite;
        SolrInputDocument input = new SolrInputDocument();
        //input.addField(QueryConstants.FIELD_ID, "ACLTX-" + changeSet.getId());
        input.addField("id", AlfrescoSolrDataModel.getAclChangeSetDocumentId(TenantService.DEFAULT_DOMAIN, changeSet.getId()));
        input.addField("_version_", "0");
        input.addField(QueryConstants.FIELD_ACLTXID, changeSet.getId());
        input.addField(QueryConstants.FIELD_INACLTXID, changeSet.getId());
        input.addField(QueryConstants.FIELD_ACLTXCOMMITTIME, changeSet.getCommitTimeMs());
        cmd.solrDoc = input;
        //cmd.doc = toDocument(cmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(cmd);

    }

    @Override
    public void indexNode(Node node, boolean overwrite) throws IOException, AuthenticationException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            long start = System.nanoTime();

            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

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
                    nodeMetaDatas =  repositoryClient.getNodesMetaData(nmdp, Integer.MAX_VALUE);
                }
                for (NodeMetaData nodeMetaData : nodeMetaDatas)
                {
                    if (nodeMetaData.getTxnId() > node.getTxnId())
                    {
                        // the node has moved on to a later transaction
                        // it will be indexed later
                        continue;
                    }
                    LinkedHashSet<Long> visited = new LinkedHashSet<Long>();
                    //TODO: updateDescendantDocs(nodeMetaData, overwrite, solrIndexSearcher, visited, solrIndexSearcher.getDocSet(skippingDocsQuery));
                }

                log.debug(".. deleting");
                DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(getLocalSolrQueryRequest());
                delDocCmd.setQuery("DBID:"+node.getId());
                core.getUpdateHandler().deleteByQuery(delDocCmd);
            }

            if ((node.getStatus() == SolrApiNodeStatus.UPDATED) || (node.getStatus() == SolrApiNodeStatus.UNKNOWN))
            {

                log.info(".. updating");
                NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
                nmdp.setFromNodeId(node.getId());
                nmdp.setToNodeId(node.getId());

                List<NodeMetaData> nodeMetaDatas =  repositoryClient.getNodesMetaData(nmdp, Integer.MAX_VALUE);

                AddUpdateCommand addDocCmd = new AddUpdateCommand(getLocalSolrQueryRequest());
                addDocCmd.overwrite = overwrite;

                for (NodeMetaData nodeMetaData : nodeMetaDatas)
                {
                    if (nodeMetaData.getTxnId() > node.getTxnId())
                    {
                        // the node has moved on to a later transaction
                        // it will be indexed later
                        continue;
                    }

                    if (mayHaveChildren(nodeMetaData))
                    {
                        log.info(".. checking for path change");
                        BooleanQuery bQuery = new BooleanQuery();
                        bQuery.add(new TermQuery(AlfrescoSolrDataModel.getLongTerm(QueryConstants.FIELD_DBID, nodeMetaData.getId())), Occur.MUST);
                        bQuery.add(new TermQuery(AlfrescoSolrDataModel.getLongTerm(QueryConstants.FIELD_PARENT_ASSOC_CRC, nodeMetaData.getParentAssocsCrc())), Occur.MUST);
                        DocSet docSet = solrIndexSearcher.getDocSet(bQuery);
                        if (docSet.size() > 0)
                        {
                            log.debug("... found match");
                        }
                        else
                        {
                            docSet = solrIndexSearcher.getDocSet(new TermQuery(AlfrescoSolrDataModel.getLongTerm(QueryConstants.FIELD_DBID, nodeMetaData.getId())));
                            if (docSet.size() > 0)
                            {
                                log.debug("... cascade updating docs");
                                LinkedHashSet<Long> visited = new LinkedHashSet<Long>();
                                // TODO: updateDescendantAuxDocs(nodeMetaData, overwrite, solrIndexSearcher, visited, solrIndexSearcher.getDocSet(skippingDocsQuery));
                            }
                            else
                            {
                                log.debug("... no doc to update");
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
                                log.debug(".. clearing unindexed");
                                DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(getLocalSolrQueryRequest());
                                delDocCmd.setQuery("DBID:"+node.getId());
                                core.getUpdateHandler().deleteByQuery(delDocCmd);

                                SolrInputDocument doc = new SolrInputDocument();
                                //doc.addField(QueryConstants.FIELD_ID, "UNINDEXED-" + nodeMetaData.getId());
                                doc.addField("id", AlfrescoSolrDataModel.getNodeDocumentId(nodeMetaData.getTenantDomain(), nodeMetaData.getAclId(), nodeMetaData.getId()));
                                doc.addField("_version_", 0);
                                doc.addField(QueryConstants.FIELD_DBID, nodeMetaData.getId());
                                doc.addField(QueryConstants.FIELD_LID, nodeMetaData.getNodeRef());
                                doc.addField(QueryConstants.FIELD_INTXID, nodeMetaData.getTxnId());

                                addDocCmd.solrDoc = doc;
                                core.getUpdateHandler().addDoc(addDocCmd);

                                long end = System.nanoTime();
                                this.trackerStats.addNodeTime(end - start);
                                return;
                            }
                        }
                    }

                    boolean isContentIndexedForNode = true;
                    if (properties.containsKey(ContentModel.PROP_IS_CONTENT_INDEXED))
                    {
                        StringPropertyValue pValue = (StringPropertyValue) properties
                                    .get(ContentModel.PROP_IS_CONTENT_INDEXED);
                        if (pValue != null)
                        {
                            Boolean isIndexed = Boolean.valueOf(pValue.getValue());
                            if ((isIndexed != null) && (isIndexed.booleanValue() == false))
                            {
                                isContentIndexedForNode = false;
                            }
                        }
                    }

                    // Make sure any unindexed or error doc is removed.
                    log.debug(".. deleting");
                    DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(getLocalSolrQueryRequest());
                    delDocCmd.setQuery("DBID:"+node.getId());
                    core.getUpdateHandler().deleteByQuery(delDocCmd);

                    SolrInputDocument doc = new SolrInputDocument();
                    doc.addField("id", AlfrescoSolrDataModel.getNodeDocumentId(nodeMetaData.getTenantDomain(), nodeMetaData.getAclId(), nodeMetaData.getId()));
                    doc.addField("_version_", 0);
                    doc.addField(QueryConstants.FIELD_DBID, nodeMetaData.getId());
                    doc.addField(QueryConstants.FIELD_LID, nodeMetaData.getNodeRef());
                    doc.addField(QueryConstants.FIELD_INTXID, nodeMetaData.getTxnId());
    
                    for (QName propertyQName : properties.keySet())
                    {
                        PropertyValue value = properties.get(propertyQName);
                        if(value != null)
                        {
                            if (value instanceof StringPropertyValue)
                            {
                                for( FieldInstance  field : AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(propertyQName).getFields())
                                {
                                    addStringPropertyToDoc(doc, field, (StringPropertyValue) value, properties);
                                }
                            }
                            else if (value instanceof MLTextPropertyValue)
                            {
                                for( FieldInstance  field : AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(propertyQName).getFields())
                                {
                                    addMLTextPropertyToDoc(doc, field, (MLTextPropertyValue) value);
                                }

                            }
                            else if (value instanceof ContentPropertyValue)
                            {
                                if (isContentIndexedForNode)
                                {
                                    addContentPropertyToDoc(doc, propertyQName, nodeMetaData, (ContentPropertyValue) value);
                                }
                            }
                            else if (value instanceof MultiPropertyValue)
                            {
                                  MultiPropertyValue typedValue = (MultiPropertyValue) value;
                                  for (PropertyValue singleValue : typedValue.getValues())
                                  {
                                      if (singleValue instanceof StringPropertyValue)
                                      {
                                          for( FieldInstance  field : AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(propertyQName).getFields())
                                          {
                                              addStringPropertyToDoc(doc, field, (StringPropertyValue) singleValue, properties);
                                          }
                                      }
                                      else if (singleValue instanceof MLTextPropertyValue)
                                      {
                                          for( FieldInstance  field : AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(propertyQName).getFields())
                                          {
                                              addMLTextPropertyToDoc(doc, field, (MLTextPropertyValue) singleValue);
                                          }

                                      }
                                      else if (singleValue instanceof ContentPropertyValue)
                                      {
                                          if (isContentIndexedForNode)
                                          {
                                              addContentPropertyToDoc(doc, propertyQName, nodeMetaData, (ContentPropertyValue) singleValue);
                                          }
                                      }
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
                   
                    doc.addField(QueryConstants.FIELD_TENANT, AlfrescoSolrDataModel.getTenantId(nodeMetaData.getTenantDomain()));

                    for (Pair<String, QName> path : nodeMetaData.getPaths())
                    {
                        doc.addField(QueryConstants.FIELD_PATH, path.getFirst());
                    }

                    if (nodeMetaData.getOwner() != null)
                    {
                        doc.addField(QueryConstants.FIELD_OWNER, nodeMetaData.getOwner());
                    }
                    doc.addField(QueryConstants.FIELD_PARENT_ASSOC_CRC, nodeMetaData.getParentAssocsCrc());

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
                            doc.addField(QueryConstants.FIELD_PARENT, childAssocRef.getParentRef());

                            if (childAssocRef.isPrimary())
                            {
                                doc.addField(QueryConstants.FIELD_PRIMARYPARENT, childAssocRef.getParentRef());
                                doc.addField(QueryConstants.FIELD_PRIMARYASSOCTYPEQNAME,
                                            ISO9075.getXPathName(childAssocRef.getTypeQName()));
                                doc.addField(QueryConstants.FIELD_PRIMARYASSOCQNAME, ISO9075.getXPathName(childAssocRef.getQName()));

                            }
                        }
                        doc.addField(QueryConstants.FIELD_ASSOCTYPEQNAME, assocTypeQNameBuffer.toString());
                        doc.addField(QueryConstants.FIELD_QNAME, qNameBuffer.toString());
                    }
                    if (nodeMetaData.getAncestors() != null)
                    {
                        for (NodeRef ancestor : nodeMetaData.getAncestors())
                        {
                            doc.addField(QueryConstants.FIELD_ANCESTOR, ancestor.toString());
                        }
                    }                   
                    
                    addDocCmd.solrDoc = doc;

                }

                if (addDocCmd.solrDoc != null)
                {
                    core.getUpdateHandler().addDoc(addDocCmd);
                }
               

            }
            long end = System.nanoTime();
            this.trackerStats.addNodeTime(end - start);
        }
        catch (Exception e)
        {
            // generic recovery
            // Add failed node marker to try later
            // TODO: add to reporting
            // TODO: Store exception for display via query
            // TODO: retry failed

            log.debug(".. deleting on exception");
            DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(getLocalSolrQueryRequest());
            delDocCmd.setQuery("DBID:"+node.getId());
            core.getUpdateHandler().deleteByQuery(delDocCmd);

            AddUpdateCommand addDocCmd = new AddUpdateCommand(getLocalSolrQueryRequest());
            addDocCmd.overwrite = overwrite;

            SolrInputDocument doc = new SolrInputDocument();
            // TODO: Error doc
            doc.addField("id", "ERROR-" + node.getId());
            doc.addField("_version_", "0");
            doc.addField(QueryConstants.FIELD_DBID, node.getId());
            doc.addField(QueryConstants.FIELD_INTXID, node.getTxnId());
            doc.addField(QueryConstants.FIELD_EXCEPTION_MESSAGE, e.getMessage());

            StringWriter stringWriter = new StringWriter(4096);
            PrintWriter printWriter = new PrintWriter(stringWriter, true);
            try
            {
                e.printStackTrace(printWriter);
                String stack = stringWriter.toString();
                doc.addField(QueryConstants.FIELD_EXCEPTION_STACK, stack.length() < 32766 ? stack : stack.substring(0, 32765));
            }
            finally
            {
                printWriter.close();
            }

            addDocCmd.solrDoc = doc;
           
            if (addDocCmd.solrDoc != null)
            {
                core.getUpdateHandler().addDoc(addDocCmd);
            }

            log.warn("Node index failed and skipped for " + node.getId() + " in Tx " + node.getTxnId(), e);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
      

    }
    

    @Override
    public void indexNodes(List<Node> nodes, boolean overwrite) throws IOException, AuthenticationException, JSONException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            long start = System.nanoTime();

            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            
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
                    NodeMetaData nodeMetaData = new NodeMetaData();
                    nodeMetaData.setId(node.getId());
                    nodeMetaData.setType(ContentModel.TYPE_DELETED);
                    nodeMetaData.setNodeRef(new NodeRef(node.getNodeRef()));
                    nodeMetaData.setTxnId(node.getTxnId());
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
                    //TODO: updateDescendantDocs(nodeMetaData, overwrite, solrIndexSearcher, visited, solrIndexSearcher.getDocSet(skippingDocsQuery));
                }

                log.debug(".. deleting");
                DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(getLocalSolrQueryRequest());
                StringBuilder query = new StringBuilder();
                final String OR = " OR ";
                for (Long nodeId : deletedNodeIds)
                {
                    query.append("DBID:").append(nodeId).append(OR);
                }
                for (Long nodeId : unknownNodeIds)
                {
                    query.append("DBID:").append(nodeId).append(OR);
                }
                query.delete(query.length() - 1 - OR.length(), query.length());
                delDocCmd.setQuery(query.toString());
                core.getUpdateHandler().deleteByQuery(delDocCmd);
            }

            if (!updatedNodeIds.isEmpty() || !unknownNodeIds.isEmpty()) 
            {
                log.info(".. updating");
                NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
                List<Long> nodeIds = new LinkedList<>();
                nodeIds.addAll(updatedNodeIds);
                nodeIds.addAll(unknownNodeIds);
                nmdp.setNodeIds(nodeIds);

                List<NodeMetaData> nodeMetaDatas =  repositoryClient.getNodesMetaData(nmdp, Integer.MAX_VALUE);

                AddUpdateCommand addDocCmd = new AddUpdateCommand(getLocalSolrQueryRequest());
                addDocCmd.overwrite = overwrite;

                for (NodeMetaData nodeMetaData : nodeMetaDatas)
                {
                    Node node = nodeIdsToNodes.get(nodeMetaData.getId());
                    if (nodeMetaData.getTxnId() > node.getTxnId())
                    {
                        // the node has moved on to a later transaction
                        // it will be indexed later
                        continue;
                    }

                    if (mayHaveChildren(nodeMetaData))
                    {
                        log.info(".. checking for path change");
                        BooleanQuery bQuery = new BooleanQuery();
                        bQuery.add(new TermQuery(AlfrescoSolrDataModel.getLongTerm(QueryConstants.FIELD_DBID, nodeMetaData.getId())), Occur.MUST);
                        bQuery.add(new TermQuery(AlfrescoSolrDataModel.getLongTerm(QueryConstants.FIELD_PARENT_ASSOC_CRC, nodeMetaData.getParentAssocsCrc())), Occur.MUST);
                        DocSet docSet = solrIndexSearcher.getDocSet(bQuery);
                        if (docSet.size() > 0)
                        {
                            log.debug("... found match");
                        }
                        else
                        {
                            docSet = solrIndexSearcher.getDocSet(new TermQuery(AlfrescoSolrDataModel.getLongTerm(QueryConstants.FIELD_DBID, nodeMetaData.getId())));
                            if (docSet.size() > 0)
                            {
                                log.debug("... cascade updating docs");
                                LinkedHashSet<Long> visited = new LinkedHashSet<Long>();
                                // TODO: updateDescendantAuxDocs(nodeMetaData, overwrite, solrIndexSearcher, visited, solrIndexSearcher.getDocSet(skippingDocsQuery));
                            }
                            else
                            {
                                log.debug("... no doc to update");
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
                                log.debug(".. clearing unindexed");
                                DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(getLocalSolrQueryRequest());
                                delDocCmd.setQuery("DBID:"+node.getId());
                                core.getUpdateHandler().deleteByQuery(delDocCmd);

                                SolrInputDocument doc = new SolrInputDocument();
                                //doc.addField(QueryConstants.FIELD_ID, "UNINDEXED-" + nodeMetaData.getId());
                                doc.addField("id", AlfrescoSolrDataModel.getNodeDocumentId(nodeMetaData.getTenantDomain(), nodeMetaData.getAclId(), nodeMetaData.getId()));
                                doc.addField("_version_", 0);
                                doc.addField(QueryConstants.FIELD_DBID, nodeMetaData.getId());
                                doc.addField(QueryConstants.FIELD_LID, nodeMetaData.getNodeRef());
                                doc.addField(QueryConstants.FIELD_INTXID, nodeMetaData.getTxnId());

                                addDocCmd.solrDoc = doc;
                                core.getUpdateHandler().addDoc(addDocCmd);

//                                long end = System.nanoTime();
//                                this.trackerStats.addNodeTime(end - start);
//                                return;
                            }
                        }
                    }

                    boolean isContentIndexedForNode = true;
                    if (properties.containsKey(ContentModel.PROP_IS_CONTENT_INDEXED))
                    {
                        StringPropertyValue pValue = (StringPropertyValue) properties
                                    .get(ContentModel.PROP_IS_CONTENT_INDEXED);
                        if (pValue != null)
                        {
                            Boolean isIndexed = Boolean.valueOf(pValue.getValue());
                            if ((isIndexed != null) && (isIndexed.booleanValue() == false))
                            {
                                isContentIndexedForNode = false;
                            }
                        }
                    }

                    // Make sure any unindexed or error doc is removed.
                    log.debug(".. deleting");
                    DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(getLocalSolrQueryRequest());
                    delDocCmd.setQuery("DBID:"+node.getId());
                    core.getUpdateHandler().deleteByQuery(delDocCmd);

                    SolrInputDocument doc = new SolrInputDocument();
                    doc.addField("id", AlfrescoSolrDataModel.getNodeDocumentId(nodeMetaData.getTenantDomain(), nodeMetaData.getAclId(), nodeMetaData.getId()));
                    doc.addField("_version_", 0);
                    doc.addField(QueryConstants.FIELD_DBID, nodeMetaData.getId());
                    doc.addField(QueryConstants.FIELD_LID, nodeMetaData.getNodeRef());
                    doc.addField(QueryConstants.FIELD_INTXID, nodeMetaData.getTxnId());

                    for (QName propertyQName : properties.keySet())
                    {
                        PropertyValue value = properties.get(propertyQName);
                        if(value != null)
                        {
                            if (value instanceof StringPropertyValue)
                            {
                                for( FieldInstance  field : AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(propertyQName).getFields())
                                {
                                    addStringPropertyToDoc(doc, field, (StringPropertyValue) value, properties);
                                }
                            }
                            else if (value instanceof MLTextPropertyValue)
                            {
                                for( FieldInstance  field : AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(propertyQName).getFields())
                                {
                                    addMLTextPropertyToDoc(doc, field, (MLTextPropertyValue) value);
                                }

                            }
                            else if (value instanceof ContentPropertyValue)
                            {
                                if (isContentIndexedForNode)
                                {
                                    addContentPropertyToDoc(doc, propertyQName, nodeMetaData, (ContentPropertyValue) value);
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
                                            addStringPropertyToDoc(doc, field, (StringPropertyValue) singleValue,
                                                        properties);
                                        }
                                    }
                                    else if (singleValue instanceof MLTextPropertyValue)
                                    {
                                        for (FieldInstance field : AlfrescoSolrDataModel.getInstance()
                                                    .getIndexedFieldNamesForProperty(propertyQName).getFields())
                                        {
                                            addMLTextPropertyToDoc(doc, field, (MLTextPropertyValue) singleValue);
                                        }

                                    }
                                    else if (singleValue instanceof ContentPropertyValue)
                                    {
                                        if (isContentIndexedForNode)
                                        {
                                            addContentPropertyToDoc(doc, propertyQName, nodeMetaData,
                                                        (ContentPropertyValue) singleValue);
                                        }
                                    }
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
                   
                    doc.addField(QueryConstants.FIELD_TENANT, AlfrescoSolrDataModel.getTenantId(nodeMetaData.getTenantDomain()));

                    for (Pair<String, QName> path : nodeMetaData.getPaths())
                    {
                        doc.addField(QueryConstants.FIELD_PATH, path.getFirst());
                    }

                    if (nodeMetaData.getOwner() != null)
                    {
                        doc.addField(QueryConstants.FIELD_OWNER, nodeMetaData.getOwner());
                    }
                    doc.addField(QueryConstants.FIELD_PARENT_ASSOC_CRC, nodeMetaData.getParentAssocsCrc());

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
                            doc.addField(QueryConstants.FIELD_PARENT, childAssocRef.getParentRef());

                            if (childAssocRef.isPrimary())
                            {
                                doc.addField(QueryConstants.FIELD_PRIMARYPARENT, childAssocRef.getParentRef());
                                doc.addField(QueryConstants.FIELD_PRIMARYASSOCTYPEQNAME,
                                            ISO9075.getXPathName(childAssocRef.getTypeQName()));
                                doc.addField(QueryConstants.FIELD_PRIMARYASSOCQNAME, ISO9075.getXPathName(childAssocRef.getQName()));

                            }
                        }
                        doc.addField(QueryConstants.FIELD_ASSOCTYPEQNAME, assocTypeQNameBuffer.toString());
                        doc.addField(QueryConstants.FIELD_QNAME, qNameBuffer.toString());
                    }
                    if (nodeMetaData.getAncestors() != null)
                    {
                        for (NodeRef ancestor : nodeMetaData.getAncestors())
                        {
                            doc.addField(QueryConstants.FIELD_ANCESTOR, ancestor.toString());
                        }
                    }                   
                    
                    addDocCmd.solrDoc = doc;

                }

                if (addDocCmd.solrDoc != null)
                {
                    core.getUpdateHandler().addDoc(addDocCmd);
                }
               

            }
            long end = System.nanoTime();
            this.trackerStats.addNodeTime(end - start);
        }
        catch (Exception e)
        {
            // generic recovery
            // Add failed node marker to try later
            // TODO: add to reporting
            // TODO: Store exception for display via query
            // TODO: retry failed

//            log.debug(".. deleting on exception");
//            DeleteUpdateCommand delDocCmd = new DeleteUpdateCommand(getLocalSolrQueryRequest());
//            delDocCmd.setQuery("DBID:"+node.getId());
//            core.getUpdateHandler().deleteByQuery(delDocCmd);
//
//            AddUpdateCommand addDocCmd = new AddUpdateCommand(getLocalSolrQueryRequest());
//            addDocCmd.overwrite = overwrite;
//
//            SolrInputDocument doc = new SolrInputDocument();
//            // TODO: Error doc
//            doc.addField("id", "ERROR-" + node.getId());
//            doc.addField("_version_", "0");
//            doc.addField(QueryConstants.FIELD_DBID, node.getId());
//            doc.addField(QueryConstants.FIELD_INTXID, node.getTxnId());
//            doc.addField(QueryConstants.FIELD_EXCEPTION_MESSAGE, e.getMessage());
//
//            StringWriter stringWriter = new StringWriter(4096);
//            PrintWriter printWriter = new PrintWriter(stringWriter, true);
//            try
//            {
//                e.printStackTrace(printWriter);
//                String stack = stringWriter.toString();
//                doc.addField(QueryConstants.FIELD_EXCEPTION_STACK, stack.length() < 32766 ? stack : stack.substring(0, 32765));
//            }
//            finally
//            {
//                printWriter.close();
//            }
//
//            addDocCmd.solrDoc = doc;
//           
//            if (addDocCmd.solrDoc != null)
//            {
//                core.getUpdateHandler().addDoc(addDocCmd);
//            }
//
//            log.warn("Node index failed and skipped for " + node.getId() + " in Tx " + node.getTxnId(), e);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
      

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

    private void addContentPropertyToDoc(SolrInputDocument doc, QName propertyQName, NodeMetaData nodeMetaData, ContentPropertyValue contentPropertyValue)
                    throws AuthenticationException, IOException
    {
        
//        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + nodeMetaData.toString() + ".size",
//                contentPropertyValue.getLength());
//        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + nodeMetaData.toString() + ".locale",
//                contentPropertyValue.getLocale());
//        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + nodeMetaData.toString() + ".mimetype",
//                contentPropertyValue.getMimetype());
//        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + nodeMetaData.toString() + ".encoding",
//                contentPropertyValue.getEncoding());

        if (false == transformContent) { return; }

        long start = System.nanoTime();
        GetTextContentResponse response = repositoryClient.getTextContent(nodeMetaData.getId(), propertyQName, null);
//        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + nodeMetaData.toString() + ".transformationStatus",
//                response.getStatus());
//        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + nodeMetaData.toString() + ".transformationTime",
//                response.getTransformDuration());
//        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + nodeMetaData.toString() + ".transformationException",
//                response.getTransformException());

        InputStreamReader isr = null;
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
        //coreTracker.getTrackerStats().addDocTransformationTime(end - start);



        StringBuilder builder = new StringBuilder();
        builder.append("\u0000").append(contentPropertyValue.getLocale().toString()).append("\u0000");
        builder.append(textContent);

        for( FieldInstance  field : AlfrescoSolrDataModel.getInstance().getIndexedFieldNamesForProperty(propertyQName).getFields())
        {
            if(field.isLocalised())
            {
                doc.addField(field.getField(), builder.toString());
            }
            else
            {
                doc.addField(field.getField(), textContent);
            }
        }
        
    }
    

    private void addMLTextPropertyToDoc(SolrInputDocument doc, FieldInstance field, MLTextPropertyValue mlTextPropertyValue) throws IOException
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
    
    private void addStringPropertyToDoc(SolrInputDocument doc, FieldInstance field, StringPropertyValue stringPropertyValue, Map<QName, PropertyValue> properties) throws IOException
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
        AddUpdateCommand cmd = new AddUpdateCommand(getLocalSolrQueryRequest());
        cmd.overwrite = overwrite;
        SolrInputDocument input = new SolrInputDocument();
        input.addField("id", AlfrescoSolrDataModel.getTransactionDocumentId(TenantService.DEFAULT_DOMAIN, info.getId()));
        input.addField("_version_", 0);
        input.addField(QueryConstants.FIELD_TXID, info.getId());
        input.addField(QueryConstants.FIELD_INTXID, info.getId());
        input.addField(QueryConstants.FIELD_TXCOMMITTIME, info.getCommitTimeMs());
        cmd.solrDoc = input;
//        cmd.doc = toDocument(cmd.getSolrInputDocument(), core.getLatestSchema(), dataModel);
        core.getUpdateHandler().addDoc(cmd);
    }

    
    
    @Override
    public boolean isInIndex(String field, long id) throws IOException
    {
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            IndexReader reader = solrIndexSearcher.getIndexReader();
            
            return isInIndex(reader, field, id);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
            
    }
    
    private boolean isInIndex(IndexReader reader, String field, long value) throws IOException
    {
        for(AtomicReaderContext atomicReaderContext  : reader.leaves())
        {
            if(isInIndex(atomicReaderContext.reader(), field, value))
            {
                return true;
            }
        }
        return false;
    }
    
    private boolean isInIndex(AtomicReader atomicReader, String field, long value) throws IOException
    {
        if (value != -1L)
        {
            DocsEnum docsEnum = atomicReader.termDocsEnum(AlfrescoSolrDataModel.getLongTerm(field, value));
            if(docsEnum != null)
            {
                if(docsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean putModel(M2Model model)
    {
        return this.dataModel.putModel(model);
    }

    @Override
    public void rollback() throws IOException
    {
        this.core.getUpdateHandler().rollback(new RollbackUpdateCommand(getLocalSolrQueryRequest()));
    }

}
