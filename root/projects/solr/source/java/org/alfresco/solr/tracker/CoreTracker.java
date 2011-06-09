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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.MultiReader;
import org.alfresco.repo.search.impl.lucene.analysis.NumericEncoder;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.client.ContentPropertyValue;
import org.alfresco.solr.client.GetNodesParameters;
import org.alfresco.solr.client.MLTextPropertyValue;
import org.alfresco.solr.client.MultiPropertyValue;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.NodeMetaData;
import org.alfresco.solr.client.NodeMetaDataParameters;
import org.alfresco.solr.client.PropertyValue;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.StringPropertyValue;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.client.Node.SolrApiNodeStatus;
import org.alfresco.solr.client.SOLRAPIClient.GetTextContentResponse;
import org.alfresco.util.Pair;
import org.alfresco.util.TempFileProvider;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.core.SolrCore;
import org.apache.solr.schema.BinaryField;
import org.apache.solr.schema.CopyField;
import org.apache.solr.schema.DateField;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
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
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.util.FileCopyUtils;

public class CoreTracker
{
    private SOLRAPIClient client;

    private volatile long lastIndexedCommitTime = 0;

    private volatile long lastIndexedIdBeforeHoles = -1;

    private volatile boolean running = false;

    volatile boolean check = false;

    SolrCore core;

    CoreTracker(AlfrescoCoreAdminHandler adminHandler, SolrCore core)
    {
        super();
        this.core = core;

        String id = core.getSchema().getResourceLoader().getInstanceDir();

        AlfrescoSolrDataModel dataModel = AlfrescoSolrDataModel.getInstance(id);
        client = new SOLRAPIClient(dataModel.getDictionaryService(), dataModel.getNamespaceDAO(), "http://localhost:8080/alfresco/service", "admin", "admin");

        JobDetail job = new JobDetail("CoreTracker-" + core.getName(), "Solr", CoreTrackerJob.class);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("TRACKER", this);
        job.setJobDataMap(jobDataMap);
        Trigger trigger;
        try
        {
            trigger = new CronTrigger("CoreTrackerTrigger" + core.getName(), "Solr", "0/15 * * * * ? *");
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

    public void track()
    {
        System.out.println("... tracking " + core.getName());

        String id = core.getSchema().getResourceLoader().getInstanceDir();

        AlfrescoSolrDataModel dataModel = AlfrescoSolrDataModel.getInstance(id);

        synchronized (this)
        {
            if (running)
            {
                System.out.println("... tracker for " + core.getName() + " is already running");
                return;
            }
            else
            {
                running = true;
            }
        }

        // track
        try
        {
            RefCounted<SolrIndexSearcher> refCounted = core.getSearcher(false, true, null);

            boolean indexing = false;
            try
            {
                SolrIndexSearcher solrIndexSearcher = refCounted.get();
                SolrIndexReader reader = solrIndexSearcher.getReader();

                if (lastIndexedCommitTime == 0)
                {
                    lastIndexedCommitTime = getLastTransactionCommitTime(reader);
                }

                long startTime = System.currentTimeMillis();
                long timeToStopIndexing = startTime - 1000L;
                long timeBeforeWhichThereCanBeNoHoles = startTime - (60 * 60 * 1000);
                long timeBeforeWhichThereCanBeNoHolesInIndex = lastIndexedCommitTime - (60 * 60 * 1000);
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

                boolean upToDate = false;
                ArrayList<Transaction> transactionsOrderedById = new ArrayList<Transaction>(10000);
                List<Transaction> transactions;
                long loopStartingCommitTime;
                do
                {
                    int docCount = 0;

                    loopStartingCommitTime = lastTxCommitTime;
                    transactions = client.getTransactions(lastTxCommitTime, null, 2000);
                    System.out.println("Scanning transactions ...");
                    if (transactions.size() > 0)
                    {
                        System.out.println(".... from " + transactions.get(0));
                        System.out.println(".... to " + transactions.get(transactions.size() - 1));
                    }
                    else
                    {
                        System.out.println(".... non found after lastTxCommitTime " + lastTxCommitTime);
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
                                indexTransaction(dataModel, info);

                                GetNodesParameters gnp = new GetNodesParameters();
                                ArrayList<Long> txs = new ArrayList<Long>();
                                txs.add(info.getId());
                                gnp.setTransactionIds(txs);
                                gnp.setStoreProtocol("workspace");
                                gnp.setStoreIdentifier("SpacesStore");
                                List<Node> nodes = client.getNodes(gnp, (int) info.getUpdates());
                                for (Node node : nodes)
                                {
                                    docCount++;
                                    System.out.println(node);
                                    indexNode(dataModel, node);

                                }

                                if (info.getCommitTimeMs() > lastIndexedCommitTime)
                                {
                                    lastIndexedCommitTime = info.getCommitTimeMs();
                                }

                                lastTxCommitTime = info.getCommitTimeMs();
                            }
                        }
                        // could batch commit here
                        if (docCount > 1000)
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
                refCounted.decref();
            }
            if (indexing)
            {
                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }

            // check index state

            if (check)
            {
                checkIndex();
            }
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
        finally
        {
            running = false;
            check = false;
        }

    }

    /**
     * @param dataModel
     * @param info
     * @throws IOException
     */
    private void indexTransaction(AlfrescoSolrDataModel dataModel, Transaction info) throws IOException
    {
        AddUpdateCommand cmd = new AddUpdateCommand();
        cmd.overwriteCommitted = true;
        cmd.overwritePending = true;
        SolrInputDocument input = new SolrInputDocument();
        input.addField(AbstractLuceneQueryParser.FIELD_ID, "TX-" + info.getId());
        input.addField(AbstractLuceneQueryParser.FIELD_TXID, info.getId());
        input.addField(AbstractLuceneQueryParser.FIELD_TXCOMMITTIME, info.getCommitTimeMs());
        cmd.solrDoc = input;
        cmd.doc = CoreTracker.toDocument(cmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(cmd);
    }

    /**
     * @param dataModel
     * @param node
     * @throws IOException
     */
    private void indexNode(AlfrescoSolrDataModel dataModel, Node node) throws IOException
    {
        if (node.getStatus() == SolrApiNodeStatus.UPDATED)
        {
            System.out.println(".. updating");
            NodeMetaDataParameters nmdp = new NodeMetaDataParameters();
            nmdp.setFromNodeId(node.getId());
            nmdp.setToNodeId(node.getId());
            try
            {
                List<NodeMetaData> nodeMetaDatas = client.getNodesMetaData(nmdp, 1);

                AddUpdateCommand leafDocCmd = new AddUpdateCommand();
                leafDocCmd.overwriteCommitted = true;
                leafDocCmd.overwritePending = true;
                AddUpdateCommand auxDocCmd = new AddUpdateCommand();
                auxDocCmd.overwriteCommitted = true;
                auxDocCmd.overwritePending = true;

                ArrayList<Reader> toClose = new ArrayList<Reader>();
                ArrayList<File> toDelete = new ArrayList<File>();

                for (NodeMetaData nodeMetaData : nodeMetaDatas)
                {
                    SolrInputDocument doc = new SolrInputDocument();
                    doc.addField(AbstractLuceneQueryParser.FIELD_ID, "LEAF-" + node.getId());
                    doc.addField(AbstractLuceneQueryParser.FIELD_DBID, node.getId());

                    Map<QName, PropertyValue> properties = nodeMetaData.getProperties();
                    for (QName propertyQname : properties.keySet())
                    {
                        PropertyValue value = properties.get(propertyQname);
                        if (value != null)
                        {
                            if (value instanceof ContentPropertyValue)
                            {
                                addContentPropertyToDoc(doc, toClose, toDelete, node, propertyQname, (ContentPropertyValue) value);
                            }
                            else if (value instanceof MLTextPropertyValue)
                            {
                                addMLTextPropertyToDoc(doc, propertyQname, (MLTextPropertyValue) value, dataModel);
                            }
                            else if (value instanceof MultiPropertyValue)
                            {
                                MultiPropertyValue typedValue = (MultiPropertyValue) value;
                                for (PropertyValue singleValue : typedValue.getValues())
                                {
                                    if (singleValue instanceof ContentPropertyValue)
                                    {
                                        addContentPropertyToDoc(doc, toClose, toDelete, node, propertyQname, (ContentPropertyValue) singleValue);
                                    }
                                    else if (singleValue instanceof MLTextPropertyValue)
                                    {
                                        addMLTextPropertyToDoc(doc, propertyQname, (MLTextPropertyValue) singleValue, dataModel);

                                    }
                                    else if (singleValue instanceof StringPropertyValue)
                                    {
                                        addStringPropertyToDoc(doc, propertyQname, (StringPropertyValue) singleValue, dataModel, properties);
                                    }
                                }
                            }
                            else if (value instanceof StringPropertyValue)
                            {
                                addStringPropertyToDoc(doc, propertyQname, (StringPropertyValue) value, dataModel, properties);
                            }

                        }
                    }
                    doc.addField(AbstractLuceneQueryParser.FIELD_TYPE, nodeMetaData.getType().toString());
                    for (QName aspect : nodeMetaData.getAspects())
                    {
                        doc.addField(AbstractLuceneQueryParser.FIELD_ASPECT, aspect.toString());
                    }
                    doc.addField(AbstractLuceneQueryParser.FIELD_ISNODE, "T");

                    leafDocCmd.solrDoc = doc;
                    leafDocCmd.doc = CoreTracker.toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

                    SolrInputDocument aux = new SolrInputDocument();
                    aux.addField(AbstractLuceneQueryParser.FIELD_ID, "AUX-" + node.getId());
                    aux.addField(AbstractLuceneQueryParser.FIELD_DBID, node.getId());
                    aux.addField(AbstractLuceneQueryParser.FIELD_ACLID, nodeMetaData.getAclId());

                    for (Pair<String, QName> path : nodeMetaData.getPaths())
                    {
                        aux.addField(AbstractLuceneQueryParser.FIELD_PATH, path.getFirst());
                    }

                    StringPropertyValue owner = (StringPropertyValue) properties.get(ContentModel.PROP_OWNER);
                    if (owner == null)
                    {
                        owner = (StringPropertyValue) properties.get(ContentModel.PROP_CREATOR);
                    }
                    if (owner != null)
                    {
                        aux.addField(AbstractLuceneQueryParser.FIELD_OWNER, owner.getValue());
                    }

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
                System.out.println(e.getStackTrace());
            }

        }
        else if (node.getStatus() == SolrApiNodeStatus.DELETED)
        {
            System.out.println(".. deleting");
            DeleteUpdateCommand docCmd = new DeleteUpdateCommand();
            docCmd.id = "LEAF-" + node.getId();
            docCmd.fromPending = true;
            docCmd.fromCommitted = true;
            core.getUpdateHandler().delete(docCmd);
        }
    }

    private void addContentPropertyToDoc(SolrInputDocument doc, ArrayList<Reader> toClose, ArrayList<File> toDelete, Node node, QName propertyQName,
            ContentPropertyValue contentPropertyValue) throws IOException
    {
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".size", contentPropertyValue.getLength());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".locale", contentPropertyValue.getLocale());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".mimetype", contentPropertyValue.getMimetype());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".encoding", contentPropertyValue.getEncoding());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".contentDataId", contentPropertyValue.getId());
        GetTextContentResponse response = client.getTextContent(node.getId(), propertyQName, null);
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationStatus", response.getStatus());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationTime", response.getRequestDuration());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".transformationException", response.getTransformException());

        InputStreamReader isr = null;
        InputStream ris = response.getContent();

        if (ris != null)
        {
            // Get and copy content
            File temp = TempFileProvider.createTempFile("solr", "content");
            toDelete.add(temp);
            OutputStream os = new BufferedOutputStream(new FileOutputStream(temp));
            FileCopyUtils.copy(ris, os);

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

    private void addMLTextPropertyToDoc(SolrInputDocument doc, QName propertyQName, MLTextPropertyValue mlTextPropertyValue, AlfrescoSolrDataModel dataModel) throws IOException
    {
        PropertyDefinition propertyDefinition = dataModel.getDictionaryService().getProperty(propertyQName);
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

    private void addStringPropertyToDoc(SolrInputDocument doc, QName propertyQName, StringPropertyValue stringPropertyValue, AlfrescoSolrDataModel dataModel,
            Map<QName, PropertyValue> properties) throws IOException
    {
        PropertyDefinition propertyDefinition = dataModel.getDictionaryService().getProperty(propertyQName);
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
                    locale = DefaultTypeConverter.INSTANCE.convert(Locale.class, localePropertyValue.toString());
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
    public IndexHealthReport checkIndex() throws IOException, JSONException
    {

        IndexHealthReport indexHealthReport = new IndexHealthReport();

        OpenBitSet txIdsInDb = new OpenBitSet();
        long lastTxCommitTime = 0;
        long maxTxId = 0;

        long loopStartingCommitTime;
        List<Transaction> transactions;
        do
        {
            loopStartingCommitTime = lastTxCommitTime;
            transactions = client.getTransactions(lastTxCommitTime, null, 2000);
            for (Transaction info : transactions)
            {
                lastTxCommitTime = info.getCommitTimeMs();
                txIdsInDb.set(info.getId());
            }
        }
        while ((transactions.size() > 0) && (loopStartingCommitTime < lastTxCommitTime));

        indexHealthReport.setDbTransactionCount(txIdsInDb.cardinality());

        OpenBitSet txIdsInIndex = new OpenBitSet();

        RefCounted<SolrIndexSearcher> refCounted = core.getSearcher(false, true, null);

        if (refCounted == null)
        {
            return indexHealthReport;
        }

        try
        {
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            SolrIndexReader reader = solrIndexSearcher.getReader();

            for (long i = 0; i <= maxTxId + 10; i++)
            {
                String target = NumericEncoder.encode(i);
                TermEnum termEnum = reader.terms(new Term(AbstractLuceneQueryParser.FIELD_TXID, target));
                Term term = termEnum.term();
                termEnum.close();
                if (term == null)
                {
                    if (txIdsInDb.get(i))
                    {
                        indexHealthReport.setMissingFromIndex(i);
                    }
                }
                else
                {
                    if (term.field().equals(AbstractLuceneQueryParser.FIELD_TXID))
                    {
                        if (target.equals(term.text()))
                        {
                            if (txIdsInIndex.get(i))
                            {
                                indexHealthReport.setDuplicatedInIndex(i);
                            }
                            else
                            {
                                txIdsInIndex.set(i);
                            }

                            if (!txIdsInDb.get(i))
                            {
                                indexHealthReport.setInIndexButNotInDb(i);
                            }
                        }
                        else
                        {
                            if (txIdsInDb.get(i))
                            {
                                indexHealthReport.setMissingFromIndex(i);
                            }
                        }
                    }
                    else
                    {
                        if (txIdsInDb.get(i))
                        {
                            indexHealthReport.setMissingFromIndex(i);
                        }
                    }
                }

            }
            // count terms and check for duplicates
            int count = 0;
            TermEnum termEnum = reader.terms(new Term(AbstractLuceneQueryParser.FIELD_TXID, ""));
            do
            {
                Term term = termEnum.term();
                if (term.field().equals(AbstractLuceneQueryParser.FIELD_TXID))
                {
                    int docCount = 0;
                    TermDocs termDocs = reader.termDocs(new Term(AbstractLuceneQueryParser.FIELD_TXID, term.text()));
                    while (termDocs.next())
                    {
                        if (!reader.isDeleted(termDocs.doc()))
                        {
                            docCount++;
                        }
                    }
                    if (docCount > 1)
                    {
                        long txid = NumericEncoder.decodeLong(term.text());
                        indexHealthReport.setDuplicatedInIndex(txid);
                    }

                    count++;
                }
                else
                {
                    break;
                }
            }
            while (termEnum.next());
            termEnum.close();

            indexHealthReport.setTransactionDocsInIndex(count);

            int leafCount = 0;
            termEnum = reader.terms(new Term(AbstractLuceneQueryParser.FIELD_ID, "LEAF-"));
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
}