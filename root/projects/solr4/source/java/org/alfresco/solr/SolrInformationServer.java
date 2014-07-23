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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.adapters.IOpenBitSet;
import org.alfresco.solr.adapters.ISimpleOrderedMap;
import org.alfresco.solr.adapters.SolrOpenBitSetAdapter;
import org.alfresco.solr.adapters.SolrSimpleOrderedMap;
import org.alfresco.solr.client.AclChangeSet;
import org.alfresco.solr.client.AclReaders;
import org.alfresco.solr.client.AlfrescoModel;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.tracker.IndexHealthReport;
import org.alfresco.solr.tracker.TrackerStats;
import org.alfresco.util.NumericEncoder;
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
    protected final static Logger log = LoggerFactory.getLogger(SolrInformationServer.class);

    public SolrInformationServer(AlfrescoCoreAdminHandler adminHandler, SolrCore core)
    {
        this.adminHandler = adminHandler;
        this.core = core;

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
            query.add(new TermQuery(new Term(QueryConstants.FIELD_TXID, targetTxId)), Occur.MUST);
            query.add(new TermQuery(new Term(QueryConstants.FIELD_TXCOMMITTIME, targetTxCommitTime)), Occur.MUST);
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
//        return dataModel.getModelErrors();
        return null; // TODO
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
        BytesRef bytes = new BytesRef();
        if (value != -1L)
        {
            NumericUtils.longToPrefixCoded(value, 0, bytes);
            DocsEnum docsEnum = atomicReader.termDocsEnum(new Term(term, bytes));
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
            //input.addField(QueryConstants.FIELD_ID, "ACL-" + aclReaders.getId());
            input.addField("id", "ACL-" + aclReaders.getId());
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
        input.addField("id", "ACLTX-" + changeSet.getId());
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
        // TODO Auto-generated method stub

    }

    @Override
    public void indexTransaction(Transaction info, boolean overwrite) throws IOException
    {
        AddUpdateCommand cmd = new AddUpdateCommand(getLocalSolrQueryRequest());
        cmd.overwrite = overwrite;
        SolrInputDocument input = new SolrInputDocument();
        //input.addField(QueryConstants.FIELD_ID, "TX-" + info.getId());
        input.addField("id", "TX-" + info.getId());
        input.addField("_version_", "0");
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
        BytesRef bytes = new BytesRef();
        if (value != -1L)
        {
            NumericUtils.longToPrefixCoded(value, 0, bytes);
            DocsEnum docsEnum = atomicReader.termDocsEnum(new Term(field, bytes));
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
