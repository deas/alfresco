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
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
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
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrInfoMBean;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.DeleteUpdateCommand;
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
     // TODO: Add the SolrQueryRequest instead of null
        AddUpdateCommand checkDocCmd = new AddUpdateCommand(null);
        
        
        BytesRef indexedId = new BytesRef("CHECK_CACHE");
        checkDocCmd.setIndexedId(indexedId );
        core.getUpdateHandler().addDoc(checkDocCmd);
        this.commit();
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
// TODO Add SolrQueryRequest
        this.core.getUpdateHandler().commit(new CommitUpdateCommand(null, false));
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
// TODO Add SolrQueryRequest
            DeleteUpdateCommand docCmd = new DeleteUpdateCommand(null);
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TrackerState getTrackerState()
    {
        return this.trackerState;
    }

    @Override
    public long indexAcl(List<AclReaders> arg0, boolean arg1) throws IOException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void indexAclTransaction(AclChangeSet arg0, boolean arg1) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void indexNode(Node node, boolean overwrite) throws IOException, AuthenticationException, JSONException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void indexTransaction(Transaction info, boolean overwrite) throws IOException
    {
        AddUpdateCommand cmd = new AddUpdateCommand(null); // TODO: Add SolrQueryRequest req
        cmd.overwrite = overwrite;
        SolrInputDocument input = new SolrInputDocument();
        input.addField(QueryConstants.FIELD_ID, "TX-" + info.getId());
        input.addField(QueryConstants.FIELD_TXID, info.getId());
        input.addField(QueryConstants.FIELD_INTXID, info.getId());
        input.addField(QueryConstants.FIELD_TXCOMMITTIME, info.getCommitTimeMs());
        cmd.solrDoc = input;
//        cmd.doc = toDocument(cmd.getSolrInputDocument(), core.getLatestSchema(), dataModel);
        core.getUpdateHandler().addDoc(cmd);
    }

    
    
    @Override
    public boolean isInIndex(String arg0, long arg1) throws IOException
    {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
    }

}
