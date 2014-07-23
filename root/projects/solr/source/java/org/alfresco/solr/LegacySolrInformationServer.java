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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.model.ContentModel;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.search.impl.lucene.MultiReader;
import org.alfresco.repo.search.impl.lucene.analysis.NumericEncoder;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry;
import org.alfresco.solr.adapters.IOpenBitSet;
import org.alfresco.solr.adapters.ISimpleOrderedMap;
import org.alfresco.solr.adapters.LegacySolrOpenBitSetAdapter;
import org.alfresco.solr.adapters.LegacySolrSimpleOrderedMap;
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
import org.alfresco.solr.client.SOLRAPIClient.GetTextContentResponse;
import org.alfresco.solr.client.StringPropertyValue;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.tracker.CoreTracker;
import org.alfresco.solr.tracker.MultiThreadedCoreTracker;
import org.alfresco.solr.tracker.Tracker;
import org.alfresco.util.ISO9075;
import org.alfresco.util.Pair;
import org.alfresco.util.TempFileProvider;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexCommit;
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
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.IndexDeletionPolicyWrapper;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrInfoMBean;
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
import org.apache.solr.update.RollbackUpdateCommand;
import org.apache.solr.util.RefCounted;
import org.json.JSONException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.util.FileCopyUtils;

/**
 * This class interfaces with the old Solr Information Search Server.
 * 
 * @author Ahmed Owian
 */
public class LegacySolrInformationServer implements CloseHook, InformationServer
{
    private static final String PREFIX_ERROR = "ERROR-";

    protected final static Logger log = LoggerFactory.getLogger(LegacySolrInformationServer.class);

    private final static int MAX_RESULTS_NODES_META_DATA = 1;

    private AlfrescoCoreAdminHandler adminHandler;
    private SolrCore core;
    private CoreTracker coreTracker;
    private TrackerState trackerState = new TrackerState();
    private AlfrescoSolrDataModel dataModel;

    private int authorityCacheSize;
    private int filterCacheSize;
    private int pathCacheSize;
    private boolean transformContent = true;
    private long lag;
    private long holeRetention;

    public LegacySolrInformationServer(AlfrescoCoreAdminHandler adminHandler, SolrCore core)
    {
        this.adminHandler = adminHandler;
        this.core = core;

        Properties p = core.getResourceLoader().getCoreProperties();
        authorityCacheSize = Integer.parseInt(p.getProperty("solr.authorityCache.size", "64"));
        filterCacheSize = Integer.parseInt(p.getProperty("solr.filterCache.size", "64"));
        pathCacheSize = Integer.parseInt(p.getProperty("solr.pathCache.size", "64"));
        transformContent = Boolean.parseBoolean(p.getProperty("alfresco.index.transformContent", "true"));
        lag = Integer.parseInt(p.getProperty("alfresco.lag", "1000"));
        holeRetention = Integer.parseInt(p.getProperty("alfresco.hole.retention", "3600000"));

        SolrResourceLoader loader = core.getSchema().getResourceLoader();
        String id = loader.getInstanceDir();
        SolrKeyResourceLoader keyResourceLoader = new SolrKeyResourceLoader(loader);

        String coreName = core.getName();
        core.addCloseHook(this);

        boolean storeAll = Boolean.parseBoolean(p.getProperty("alfresco.storeAll", "false"));
        dataModel = AlfrescoSolrDataModel.getInstance(id);
        dataModel.setStoreAll(storeAll);
        
        this.coreTracker = new MultiThreadedCoreTracker(adminHandler.getScheduler(), id, p, keyResourceLoader,
                    coreName, this);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.core.CloseHook#close(org.apache.solr.core.SolrCore)
     */
    @Override
    public void close(SolrCore core)
    {
        this.coreTracker.setShutdown(true);

        try
        {
            adminHandler.getScheduler().deleteJob("CoreTracker-" + this.core.getName(), "Solr");
            adminHandler.getTrackers().remove(this.core.getName());
            if (adminHandler.getTrackers().size() == 0)
            {
                if (!adminHandler.getScheduler().isShutdown())
                {
                    adminHandler.getScheduler().pauseAll();
                    adminHandler.getScheduler().shutdown();
                }
            }

            this.coreTracker.close();
        }
        catch (SchedulerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void rollback() throws IOException
    {
        this.core.getUpdateHandler().rollback(new RollbackUpdateCommand());
    }

    @Override
    public void commit() throws IOException
    {
        this.core.getUpdateHandler().commit(new CommitUpdateCommand(false));
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
                throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "ERROR: " + id
                            + "multiple values encountered for non multiValued field " + sfield.getName() + ": "
                            + field.getValue());
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
                    if (f != null) out.add(f);
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
                    if (!destinationField.multiValued() && out.get(destinationField.getName()) != null) { throw new SolrException(
                                SolrException.ErrorCode.BAD_REQUEST,
                                "ERROR: multiple values encountered for non multiValued copy field "
                                            + destinationField.getName() + ": " + val); }

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
            if (!used && hasField) { throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
                        "ERROR:unknown field '" + name + "'"); }
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

    protected List<SolrIndexSearcher> getRegisteredSearchers()
    {
        List<SolrIndexSearcher> searchers = new ArrayList<SolrIndexSearcher>();
        for (String key : core.getInfoRegistry().keySet())
        {
            SolrInfoMBean mBean = core.getInfoRegistry().get(key);
            if (mBean != null)
            {
                if (mBean.getName().equals(SolrIndexSearcher.class.getName()))
                {
                    if (!key.equals("searcher"))
                    {
                        searchers.add((SolrIndexSearcher) mBean);
                    }

                }
            }
        }

        return searchers;
    }

    @Override
    public Iterable<Map.Entry<String, Object>> getCoreStats() throws IOException
    {
        DecimalFormat df = new DecimalFormat("###,###.######");

        NamedList<Object> coreSummary = new SimpleOrderedMap<Object>();
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            OpenBitSet allLeafDocs = (OpenBitSet) solrIndexSearcher.cacheLookup(
                        AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ALL_LEAF_DOCS);
            long count = allLeafDocs.cardinality();
            coreSummary.add("Alfresco Nodes in Index", count);
            coreSummary.add("Searcher", solrIndexSearcher.getStatistics());
            Map<String, SolrInfoMBean> infoRegistry = core.getInfoRegistry();
            for (String key : infoRegistry.keySet())
            {
                SolrInfoMBean infoMBean = infoRegistry.get(key);
                if (key.equals("/alfresco"))
                {
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

            // Find searchers and do memory use for each .. and add them all up7

            long memory = 0L;
            int searcherCount = 0;
            List<SolrIndexSearcher> searchers = getRegisteredSearchers();
            for (SolrIndexSearcher searcher : searchers)
            {
                memory += addSearcherStats(coreSummary, searcher, searcherCount);
                searcherCount++;
            }

            coreSummary.add("Number of Searchers", searchers.size());
            coreSummary.add("Total Searcher Cache (GB)", df.format(memory / 1024.0f / 1024.0f / 1024.0f));

            IndexDeletionPolicyWrapper delPolicy = core.getDeletionPolicy();
            IndexCommit indexCommit = delPolicy.getLatestCommit();
            // race?
            if (indexCommit == null)
            {
                indexCommit = solrIndexSearcher.getReader().getIndexCommit();
            }
            if (indexCommit != null)
            {
                delPolicy.setReserveDuration(indexCommit.getVersion(), 20000);
                Long fileSize = 0L;

                File dir = new File(solrIndexSearcher.getIndexDir());
                for (Iterator it = indexCommit.getFileNames().iterator(); it.hasNext(); /**/)
                {
                    String name = (String) it.next();
                    File file = new File(dir, name);
                    if (file.exists())
                    {
                        fileSize += file.length();
                    }
                }

                coreSummary.add("On disk (GB)", df.format(fileSize / 1024.0f / 1024.0f / 1024.0f));
                coreSummary.add("Per node B", count > 0 ? fileSize / count : 0);
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
            // TODO: do what here?
        }

        return nodeReport;
    }

    @Override
    public void indexTransaction(Transaction info, boolean overwrite) throws IOException
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
        cmd.doc = toDocument(cmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(cmd);
    }

    @Override
    public void indexAclTransaction(AclChangeSet changeSet, boolean overwrite) throws IOException
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
        cmd.doc = toDocument(cmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(cmd);
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
    public void indexNode(Node node, boolean overwrite) throws IOException, AuthenticationException, JSONException
    {
        try
        {
            long start = System.nanoTime();

            RefCounted<SolrIndexSearcher> refCounted = core.getSearcher(false, true, null);
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
                    nodeMetaDatas = coreTracker.getNodesMetaData(nmdp, MAX_RESULTS_NODES_META_DATA);
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
                docCmd.id = PREFIX_ERROR + node.getId();
                docCmd.fromPending = true;
                docCmd.fromCommitted = true;
                core.getUpdateHandler().delete(docCmd);

            }

            if ((node.getStatus() == SolrApiNodeStatus.UPDATED) || (node.getStatus() == SolrApiNodeStatus.UNKNOWN))
            {

                log.info(".. updating");
                NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
                nmdp.setFromNodeId(node.getId());
                nmdp.setToNodeId(node.getId());

                List<NodeMetaData> nodeMetaDatas = coreTracker.getNodesMetaData(nmdp, MAX_RESULTS_NODES_META_DATA);

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
                        bQuery.add(new TermQuery(new Term(QueryConstants.FIELD_DBID, NumericEncoder.encode(nodeMetaData
                                    .getId()))), Occur.MUST);
                        bQuery.add(new TermQuery(new Term(QueryConstants.FIELD_PARENT_ASSOC_CRC, NumericEncoder
                                    .encode(nodeMetaData.getParentAssocsCrc()))), Occur.MUST);
                        DocSet docSet = solrIndexSearcher.getDocSet(bQuery);
                        if (docSet.size() > 0)
                        {
                            log.debug("... found aux match");
                        }
                        else
                        {
                            docSet = solrIndexSearcher.getDocSet(new TermQuery(new Term(QueryConstants.FIELD_DBID,
                                        NumericEncoder.encode(nodeMetaData.getId()))));
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
                                docCmd.id = PREFIX_ERROR + node.getId();
                                docCmd.fromPending = true;
                                docCmd.fromCommitted = true;
                                core.getUpdateHandler().delete(docCmd);

                                SolrInputDocument doc = new SolrInputDocument();
                                doc.addField(QueryConstants.FIELD_ID, "UNINDEXED-" + nodeMetaData.getId());
                                doc.addField(QueryConstants.FIELD_DBID, nodeMetaData.getId());
                                doc.addField(QueryConstants.FIELD_LID, nodeMetaData.getNodeRef());
                                doc.addField(QueryConstants.FIELD_INTXID, nodeMetaData.getTxnId());

                                leafDocCmd.solrDoc = doc;
                                leafDocCmd.doc = toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema(),
                                            dataModel);

                                if (leafDocCmd.doc != null)
                                {
                                    core.getUpdateHandler().addDoc(leafDocCmd);
                                }

                                long end = System.nanoTime();
                                coreTracker.getTrackerStats().addNodeTime(end - start);
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

                    // Make sure any unindexed doc is removed.
                    DeleteUpdateCommand docCmd = new DeleteUpdateCommand();
                    docCmd.id = "UNINDEXED-" + node.getId();
                    docCmd.fromPending = true;
                    docCmd.fromCommitted = true;
                    core.getUpdateHandler().delete(docCmd);

                    docCmd = new DeleteUpdateCommand();
                    docCmd.id = PREFIX_ERROR + node.getId();
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
                        if (dataModel.isIndexedOrStored(propertyQname))
                        {
                            PropertyValue value = properties.get(propertyQname);
                            if (value != null)
                            {
                                if (value instanceof ContentPropertyValue)
                                {
                                    if (isContentIndexedForNode)
                                    {
                                        addContentPropertyToDoc(doc, toClose, toDelete, nodeMetaData, propertyQname,
                                                    (ContentPropertyValue) value);
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
                                                addContentPropertyToDoc(doc, toClose, toDelete, nodeMetaData,
                                                            propertyQname, (ContentPropertyValue) singleValue);
                                            }
                                        }
                                        else if (singleValue instanceof MLTextPropertyValue)
                                        {
                                            addMLTextPropertyToDoc(doc, propertyQname,
                                                        (MLTextPropertyValue) singleValue);

                                        }
                                        else if (singleValue instanceof StringPropertyValue)
                                        {
                                            addStringPropertyToDoc(doc, propertyQname,
                                                        (StringPropertyValue) singleValue, properties);
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
                    String tenant = nodeMetaData.getTenantDomain();
                    if (tenant.length() > 0)
                    {
                        doc.addField(QueryConstants.FIELD_TENANT, nodeMetaData.getTenantDomain());
                    }
                    else
                    {
                        doc.addField(QueryConstants.FIELD_TENANT, "_DEFAULT_");
                    }

                    leafDocCmd.solrDoc = doc;
                    leafDocCmd.doc = toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema(),
                                dataModel);

                    SolrInputDocument aux = createAuxDoc(nodeMetaData);
                    auxDocCmd.solrDoc = aux;
                    auxDocCmd.doc = toDocument(auxDocCmd.getSolrInputDocument(), core.getSchema(),
                                dataModel);

                }

                if (leafDocCmd.doc != null)
                {
                    core.getUpdateHandler().addDoc(leafDocCmd);
                }
                if (auxDocCmd.doc != null)
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
            coreTracker.getTrackerStats().addNodeTime(end - start);
        }
        catch (Exception e)
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
            doc.addField(QueryConstants.FIELD_ID, PREFIX_ERROR + node.getId());
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
            leafDocCmd.doc = toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

            if (leafDocCmd.doc != null)
            {
                core.getUpdateHandler().addDoc(leafDocCmd);
            }

            log.warn("Node index failed and skipped for " + node.getId() + " in Tx " + node.getTxnId(), e);
        }

    }

    // move
    private void updateDescendantAuxDocs(NodeMetaData parentNodeMetaData, boolean overwrite,
                SolrIndexSearcher solrIndexSearcher, LinkedHashSet<Long> stack) throws AuthenticationException,
                IOException, JSONException
    {
        if (stack.contains(parentNodeMetaData.getId()))
        {
            log.warn("Found aux data loop for node id " + parentNodeMetaData.getId());
            log.warn("... stack to node =" + stack);
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

    // move
    private void doUpdateDescendantAuxDocs(NodeMetaData parentNodeMetaData, boolean overwrite,
                SolrIndexSearcher solrIndexSearcher, LinkedHashSet<Long> stack) throws AuthenticationException,
                IOException, JSONException
    {
        HashSet<Long> childIds = new HashSet<Long>();

        if (parentNodeMetaData.getChildIds() != null)
        {
            childIds.addAll(parentNodeMetaData.getChildIds());
        }

        BooleanQuery bQuery = new BooleanQuery();
        bQuery.add(new TermQuery(new Term(QueryConstants.FIELD_PARENT, parentNodeMetaData.getNodeRef().toString())),
                    Occur.MUST);
        DocSet docSet = solrIndexSearcher.getDocSet(bQuery);
        ResizeableArrayList<CacheEntry> indexedByDocId = (ResizeableArrayList<CacheEntry>) solrIndexSearcher
                    .cacheLookup(AlfrescoSolrEventListener.ALFRESCO_ARRAYLIST_CACHE,
                                AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_DOC_ID);
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
            // call back to core tracker to talk to client
            List<NodeMetaData> nodeMetaDatas = coreTracker.getNodesMetaData(nmdp, MAX_RESULTS_NODES_META_DATA);

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
                    log.debug("... cascade update aux doc " + childId);

                    SolrInputDocument aux = createAuxDoc(nodeMetaData);
                    AddUpdateCommand auxDocCmd = new AddUpdateCommand();
                    auxDocCmd.overwriteCommitted = overwrite;
                    auxDocCmd.overwritePending = overwrite;
                    auxDocCmd.solrDoc = aux;
                    auxDocCmd.doc = toDocument(auxDocCmd.getSolrInputDocument(), core.getSchema(),
                                dataModel);

                    core.getUpdateHandler().addDoc(auxDocCmd);
                }
                else
                {
                    log.debug("... no aux doc found to update " + childId);
                }
            }
        }
    }

    // move
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
                    aux.addField(QueryConstants.FIELD_PRIMARYASSOCTYPEQNAME,
                                ISO9075.getXPathName(childAssocRef.getTypeQName()));
                    aux.addField(QueryConstants.FIELD_PRIMARYASSOCQNAME, ISO9075.getXPathName(childAssocRef.getQName()));

                }
            }
            aux.addField(QueryConstants.FIELD_ASSOCTYPEQNAME, assocTypeQNameBuffer.toString());
            aux.addField(QueryConstants.FIELD_QNAME, qNameBuffer.toString());
        }
        if (nodeMetaData.getAncestors() != null)
        {
            for (NodeRef ancestor : nodeMetaData.getAncestors())
            {
                aux.addField(QueryConstants.FIELD_ANCESTOR, ancestor.toString());
            }
        }
        return aux;
    }

    // move into InformationSever and make that an abstract class
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

    private long addSearcherStats(NamedList<Object> coreSummary, SolrIndexSearcher solrIndexSearcher, int index)
    {
        DecimalFormat df = new DecimalFormat("###,###.######");

        OpenBitSet allLeafDocs = (OpenBitSet) solrIndexSearcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE,
                    AlfrescoSolrEventListener.KEY_ALL_LEAF_DOCS);
        long count = allLeafDocs.cardinality();

        ResizeableArrayList<CacheEntry> indexedByDocId = (ResizeableArrayList<CacheEntry>) solrIndexSearcher
                    .cacheLookup(AlfrescoSolrEventListener.ALFRESCO_ARRAYLIST_CACHE,
                                AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_DOC_ID);
        ResizeableArrayList<CacheEntry> indexedOderedByAclIdThenDoc = (ResizeableArrayList<CacheEntry>) solrIndexSearcher
                    .cacheLookup(AlfrescoSolrEventListener.ALFRESCO_ARRAYLIST_CACHE,
                                AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF);

        long memory = (count * 40) + (indexedByDocId.size() * 8 * 4) + (indexedOderedByAclIdThenDoc.size() * 8 * 2);
        memory += (filterCacheSize + pathCacheSize + authorityCacheSize) * indexedByDocId.size() / 8;
        NamedList<Object> details = new SimpleOrderedMap<Object>();
        details.add("Searcher", solrIndexSearcher.getStatistics());
        details.add("Size", indexedByDocId.size());
        details.add("Node Count", count);
        details.add("Memory (GB)", df.format(memory / 1024.0f / 1024.0f / 1024.0f));
        coreSummary.add("Searcher-" + index, details);
        return memory;
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
        } while (termEnum.next());
        termEnum.close();
        return lastTxCommitTimeBeforeHoles;
    }

    @Override
    public Set<Long> getErrorDocIds() throws IOException
    {
        HashSet<Long> errorDocIds = new HashSet<Long>();
        RefCounted<SolrIndexSearcher> refCounted = core.getSearcher(false, true, null);

        if (refCounted == null) { return errorDocIds; }

        try
        {
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            SolrIndexReader reader = solrIndexSearcher.getReader();
            TermEnum termEnum = reader.terms(new Term(QueryConstants.FIELD_ID, PREFIX_ERROR));
            do
            {
                Term term = termEnum.term();
                if (term.field().equals(QueryConstants.FIELD_ID) && term.text().startsWith(PREFIX_ERROR))
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

                    long txid = Long.parseLong(term.text().substring(PREFIX_ERROR.length()));
                    errorDocIds.add(txid);
                }
                else
                {
                    break;
                }
            } while (termEnum.next());
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
        } while (termEnum.next());
        termEnum.close();
        return lastTxCommitTimeBeforeHoles;
    }

    // move
    private long getLargestTxIdByCommitTime(SolrIndexReader reader, Long lastTxCommitTimeBeforeHoles)
                throws IOException
    {
        long txid = -1;
        if (lastTxCommitTimeBeforeHoles != -1)
        {
            TermDocs docs = reader.termDocs(new Term(QueryConstants.FIELD_TXCOMMITTIME, NumericEncoder
                        .encode(lastTxCommitTimeBeforeHoles)));
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

    // move
    private long getLargestChangeSetIdByCommitTime(SolrIndexReader reader, Long lastChangeSetCommitTimeBeforeHoles)
                throws IOException
    {
        long txid = -1;
        if (lastChangeSetCommitTimeBeforeHoles != -1)
        {
            TermDocs docs = reader.termDocs(new Term(QueryConstants.FIELD_ACLTXCOMMITTIME, NumericEncoder
                        .encode(lastChangeSetCommitTimeBeforeHoles)));
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

    private long getLastTransactionCommitTime(SolrIndexReader reader)
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
            } while (termEnum.next());
            termEnum.close();
        }
        catch (IOException e1)
        {
            // do nothing
        }

        return lastTxCommitTime;
    }

    // move
    private long getLastTransactionId(SolrIndexReader reader) throws IOException
    {
        long lastTxId = 0;

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
                    Long txId = NumericEncoder.decodeLong(term.text());
                    lastTxId = txId;

                }
                else
                {
                    break;
                }
            } while (termEnum.next());
            termEnum.close();
        }
        catch (IOException e1)
        {

        }

        return lastTxId;
    }

    // move
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
            } while (termEnum.next());
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
            } while (termEnum.next());
            termEnum.close();
        }
        catch (IOException e1)
        {

        }

        return lastTxCommitTime;
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

    private void addStringPropertyToDoc(SolrInputDocument doc, QName propertyQName,
                StringPropertyValue stringPropertyValue, Map<QName, PropertyValue> properties) throws IOException
    {
        PropertyDefinition propertyDefinition = dataModel.getPropertyDefinition(propertyQName);
        if (propertyDefinition != null)
        {
            if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(),
                            stringPropertyValue.getValue());
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort",
                            stringPropertyValue.getValue());
            }
            else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
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
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.TRUE)
                            || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__",
                                builder.toString());
                }
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE)
                            || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".u",
                                builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__.u",
                                builder.toString());
                }

                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE)
                            || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort",
                                builder.toString());
                }

            }
            else
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(),
                            stringPropertyValue.getValue());
            }

        }
        else
        {
            doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(),
                        stringPropertyValue.getValue());
        }
    }

    private void addMLTextPropertyToDoc(SolrInputDocument doc, QName propertyQName,
                MLTextPropertyValue mlTextPropertyValue) throws IOException
    {
        PropertyDefinition propertyDefinition = dataModel.getPropertyDefinition(propertyQName);
        if (propertyDefinition != null)
        {
            StringBuilder sort = new StringBuilder();
            for (Locale locale : mlTextPropertyValue.getLocales())
            {
                StringBuilder builder = new StringBuilder();
                builder.append("\u0000").append(locale.toString()).append("\u0000")
                            .append(mlTextPropertyValue.getValue(locale));

                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.TRUE)
                            || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__",
                                builder.toString());
                }
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE)
                            || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".u",
                                builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__.u",
                                builder.toString());
                }

                if (sort.length() > 0)
                {
                    sort.append("\u0000");
                }
                sort.append(builder.toString());
            }

            if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE)
                        || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort", sort.toString());
            }
        }
        else
        {
            for (Locale locale : mlTextPropertyValue.getLocales())
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(),
                            mlTextPropertyValue.getValue(locale));
            }
        }

    }

    private void addContentPropertyToDoc(SolrInputDocument doc, ArrayList<Reader> toClose, ArrayList<File> toDelete,
                NodeMetaData nodeMetaData, QName propertyQName, ContentPropertyValue contentPropertyValue)
                throws AuthenticationException, IOException
    {
        if (!coreTracker.canAddContentPropertyToDoc()) { return; }

        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".size",
                    contentPropertyValue.getLength());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".locale",
                    contentPropertyValue.getLocale());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".mimetype",
                    contentPropertyValue.getMimetype());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".encoding",
                    contentPropertyValue.getEncoding());

        if (false == transformContent) { return; }

        long start = System.nanoTime();
        GetTextContentResponse response = coreTracker.getTextContent(nodeMetaData.getId(), propertyQName, null);
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationStatus",
                    response.getStatus());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationTime",
                    response.getTransformDuration());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationException",
                    response.getTransformException());

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
        coreTracker.getTrackerStats().addDocTransformationTime(end - start);

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

    @Override
    public long indexAcl(List<AclReaders> aclReaderList, boolean overwrite) throws IOException
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
                switch (AuthorityType.getAuthorityType(reader))
                {
                    case USER:
                        input.addField(QueryConstants.FIELD_READER, reader);
                        break;
                    case GROUP:
                    case EVERYONE:
                    case GUEST:
                        if (tenant.length() == 0)
                        {
                            // Default tenant matches 4.0
                            input.addField(QueryConstants.FIELD_READER, reader);
                        }
                        else
                        {
                            input.addField(QueryConstants.FIELD_READER, reader + "@" + tenant);
                        }
                        break;
                    default:
                        input.addField(QueryConstants.FIELD_READER, reader);
                        break;
                }
            }
            cmd.solrDoc = input;
            cmd.doc = LegacySolrInformationServer.toDocument(cmd.getSolrInputDocument(), core.getSchema(),
                        dataModel);
            core.getUpdateHandler().addDoc(cmd);
        }

        long end = System.nanoTime();
        return (end - start);
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
            SolrIndexReader reader = solrIndexSearcher.getReader();

            if (state.getLastIndexedTxCommitTime() == 0)
            {
                state.setLastIndexedTxCommitTime(getLastTransactionCommitTime(reader));
            }

            if (state.getLastIndexedTxId() == 0)
            {
                state.setLastIndexedTxId(getLastTransactionId(reader));
            }

            if (state.getLastIndexedChangeSetCommitTime() == 0)
            {
                state.setLastIndexedChangeSetCommitTime(getLastChangeSetCommitTime(reader));
            }

            if (state.getLastIndexedChangeSetId() == 0)
            {
                state.setLastIndexedChangeSetId(getLastChangeSetId(reader));
            }

            long startTime = System.currentTimeMillis();
            state.setTimeToStopIndexing(startTime - lag);
            state.setTimeBeforeWhichThereCanBeNoHoles(startTime - holeRetention);

            long timeBeforeWhichThereCanBeNoTxHolesInIndex = state.getLastIndexedTxCommitTime() - holeRetention;
            state.setLastGoodTxCommitTimeInIndex(getLastTxCommitTimeBeforeHoles(reader,
                        timeBeforeWhichThereCanBeNoTxHolesInIndex));

            long timeBeforeWhichThereCanBeNoChangeSetHolesInIndex = state.getLastIndexedChangeSetCommitTime()
                        - holeRetention;
            state.setLastGoodChangeSetCommitTimeInIndex(getLastChangeSetCommitTimeBeforeHoles(reader,
                        timeBeforeWhichThereCanBeNoChangeSetHolesInIndex));

            if (state.getLastGoodTxCommitTimeInIndex() > 0)
            {
                if (state.getLastIndexedTxIdBeforeHoles() == -1)
                {
                    state.setLastIndexedTxIdBeforeHoles(getLargestTxIdByCommitTime(reader,
                                state.getLastGoodTxCommitTimeInIndex()));
                }
                else
                {
                    long currentBestFromIndex = getLargestTxIdByCommitTime(reader,
                                state.getLastGoodTxCommitTimeInIndex());
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
                    state.setLastIndexedChangeSetIdBeforeHoles(getLargestChangeSetIdByCommitTime(reader,
                                state.getLastGoodChangeSetCommitTimeInIndex()));
                }
                else
                {
                    long currentBestFromIndex = getLargestChangeSetIdByCommitTime(reader,
                                state.getLastGoodChangeSetCommitTimeInIndex());
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

        return state;
    }

    @Override
    public TrackerState getTrackerState()
    {
        return this.trackerState;
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
    public void checkCache() throws IOException
    {
        AddUpdateCommand checkDocCmd = new AddUpdateCommand();
        checkDocCmd.indexedId = "CHECK_CACHE";
        core.getUpdateHandler().addDoc(checkDocCmd);
        this.commit();
    }

    @Override
    public boolean isInIndex(String fieldType, long id) throws IOException
    {
        String target = NumericEncoder.encode(id);
        RefCounted<SolrIndexSearcher> refCounted = null;
        Term term = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);

            TermEnum termEnum = refCounted.get().getReader().terms(new Term(fieldType, target));
            term = termEnum.term();
            termEnum.close();
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
            refCounted = null;
        }

        return term != null && target.equals(term.text());
    }

    @Override
    public Tracker getTracker()
    {
        return this.coreTracker;
    }

    @Override
    public Map<String, Set<String>> getModelErrors()
    {
        return dataModel.getModelErrors();
    }

    @Override
    public IOpenBitSet getOpenBitSetInstance()
    {
        return new LegacySolrOpenBitSetAdapter();
    }
    
    @Override
    public <T> ISimpleOrderedMap<T> getSimpleOrderedMapInstance() 
    {
        return new LegacySolrSimpleOrderedMap<T>();
    }

    @Override
    public DictionaryComponent getDictionaryService(String alternativeDictionary)
    {
        return this.dataModel.getDictionaryService(alternativeDictionary);
    }

    @Override
    public NamespaceDAO getNamespaceDAO()
    {
        return this.dataModel.getNamespaceDAO();
    }
    
    @Override
    public List<AlfrescoModel> getAlfrescoModels()
    {
        return this.dataModel.getAlfrescoModels();
    }

    @Override
    public void afterInitModels()
    {
        this.dataModel.afterInitModels();
    }

    @Override
    public boolean putModel(M2Model model)
    {
        return this.dataModel.putModel(model);
    }
    
    @Override
    public M2Model getM2Model(QName modelQName)
    {
        return this.dataModel.getM2Model(modelQName);
    }
}
