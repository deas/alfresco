/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.MLPropertyInterceptor;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.analysis.NumericEncoder;
import org.alfresco.service.namespace.QName;
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
import org.alfresco.solr.client.Node.STATUS;
import org.alfresco.util.CachingDateFormat;
import org.alfresco.util.Pair;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.FieldComparator.DocComparator;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.admin.CoreAdminHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryResponse;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.update.DeleteUpdateCommand;
import org.apache.solr.update.DocumentBuilder;
import org.apache.solr.util.RefCounted;
import org.json.JSONException;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author Andy
 */
public class AlfrescoCoreAdminHandler extends CoreAdminHandler
{
    private Scheduler scheduler = null;

    private HashMap<String, CoreTracker> trackers = new HashMap<String, CoreTracker>();

    /**
     * 
     */
    public AlfrescoCoreAdminHandler()
    {
        super();
    }

    /**
     * @param coreContainer
     */
    public AlfrescoCoreAdminHandler(CoreContainer coreContainer)
    {
        super(coreContainer);

        // TODO: pick scheduler properties from SOLR config or file ...
        try
        {
            StdSchedulerFactory factory = new StdSchedulerFactory();
            Properties properties = new Properties();
            properties.setProperty("org.quartz.scheduler.instanceName", "SolrTrackerScheduler");
            properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
            properties.setProperty("org.quartz.threadPool.threadCount", "3");
            properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
            factory.initialize(properties);
            scheduler = factory.getScheduler();
            scheduler.start();

            // Start job to manage the tracker jobs
            // Currently just add

            JobDetail job = new JobDetail("CoreWatcher", "Solr", CoreWatcherJob.class);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("ADMIN_HANDLER", this);
            job.setJobDataMap(jobDataMap);
            Trigger trigger;
            try
            {
                trigger = new CronTrigger("CoreWatcherTrigger", "Solr", "0/20 * * * * ? *");
                scheduler.scheduleJob(job, trigger);
            }
            catch (ParseException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        catch (SchedulerException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    protected boolean handleCustomAction(SolrQueryRequest req, SolrQueryResponse rsp)
    {
        try
        {
            SolrParams params = req.getParams();
            String cname = params.get(CoreAdminParams.CORE);
            String a = params.get(CoreAdminParams.ACTION);
            if (a.equalsIgnoreCase("CHECK"))
            {
                CoreTracker tracker = trackers.get(cname);
                if (tracker != null)
                {
                    tracker.check = true;
                }
                return false;
            }
            else if (a.equalsIgnoreCase("REPORT"))
            {
                if (cname != null)
                {

                    CoreTracker tracker = trackers.get(cname);
                    IndexHealthReport indexHealthReport = tracker.checkIndex();

                    NamedList<Object> report = new SimpleOrderedMap<Object>();

                    report.add(cname, buildTrackerReport(tracker));
                    rsp.add("report", report);
                }
                else
                {
                    NamedList<Object> report = new SimpleOrderedMap<Object>();
                    for (CoreTracker tracker : trackers.values())
                    {
                        report.add(cname, buildTrackerReport(tracker));
                    }
                    rsp.add("report", report);
                }

                return false;
            }
            else
            {
                return super.handleCustomAction(req, rsp);
            }

        }
        catch (Exception ex)
        {
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Error executing default implementation of CREATE", ex);
        }
    }

    private NamedList<Object> buildTrackerReport(CoreTracker tracker) throws IOException, JSONException
    {
        IndexHealthReport indexHealthReport = tracker.checkIndex();

        NamedList<Object> ihr = new SimpleOrderedMap<Object>();
        ihr.add("dbTransactionCount", indexHealthReport.getDbTransactionCount());
        ihr.add("duplicatedInIndexCount", indexHealthReport.getDuplicatedInIndex().cardinality());
        if (indexHealthReport.getDuplicatedInIndex().cardinality() > 0)
        {
            ihr.add("firstDuplicate", indexHealthReport.getDuplicatedInIndex().nextSetBit(0L));
        }
        ihr.add("inIndexButNotInDbCount", indexHealthReport.getInIndexButNotInDb().cardinality());
        if (indexHealthReport.getInIndexButNotInDb().cardinality() > 0)
        {
            ihr.add("firstInIndexButNotInDb", indexHealthReport.getInIndexButNotInDb().nextSetBit(0L));
        }
        ihr.add("missingFromIndexCount", indexHealthReport.getMissingFromIndex().cardinality());
        if (indexHealthReport.getMissingFromIndex().cardinality() > 0)
        {
            ihr.add("firstMissingFromIndex", indexHealthReport.getMissingFromIndex().nextSetBit(0L));
        }
        ihr.add("transactionDocsInIndexCount", indexHealthReport.getTransactionDocsInIndex());
        ihr.add("leafDocCountInIndex", indexHealthReport.getLeafDocCountInIndex());
        ihr.add("duplicatedLeafInIndexCount", indexHealthReport.getDuplicatedLeafInIndex().cardinality());
        if (indexHealthReport.getDuplicatedLeafInIndex().cardinality() > 0)
        {
            ihr.add("firstDuplicateLeafInIndex", "LEAF-" + indexHealthReport.getDuplicatedLeafInIndex().nextSetBit(0L));
        }
        ihr.add("lastIndexCommitTime", indexHealthReport.getLastIndexCommitTime());
        Date lastDate = new Date(indexHealthReport.getLastIndexCommitTime());
        ihr.add("lastIndexCommitDate", CachingDateFormat.getDateFormat().format(lastDate));
        ihr.add("lastIndexIdBeforeHoles", indexHealthReport.getLastIndexedIdBeforeHoles());
        return ihr;
    }

    public static class CoreWatcherJob implements Job
    {
        public CoreWatcherJob()
        {
            super();
        }

        /*
         * (non-Javadoc)
         * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
         */
        @Override
        public void execute(JobExecutionContext jec) throws JobExecutionException
        {
            AlfrescoCoreAdminHandler adminHandler = (AlfrescoCoreAdminHandler) jec.getJobDetail().getJobDataMap().get("ADMIN_HANDLER");

            for (SolrCore core : adminHandler.coreContainer.getCores())
            {

                if (!adminHandler.trackers.containsKey(core.getName()))
                {
                    if (core.getSolrConfig().getBool("alfresco/track", false))
                    {
                        System.out.println("Starting to track " + core.getName());
                        adminHandler.trackers.put(core.getName(), new CoreTracker(adminHandler, core));
                    }
                }
            }
        }

    }

    public static class CoreTracker
    {
        private SOLRAPIClient client;

        private volatile long lastIndexedCommitTime = 0;

        private volatile long lastIndexedIdBeforeHoles = -1;

        private volatile boolean running = false;

        private volatile boolean check = false;

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
                adminHandler.scheduler.scheduleJob(job, trigger);
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

        public void track()
        {
            System.out.println("... tracking " + core.getName());

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
                        loopStartingCommitTime = lastTxCommitTime;
                        transactions = client.getTransactions(lastTxCommitTime, null, 2000);
                        for (Transaction info : transactions)
                        {
                            System.out.println(info);
                            if (!indexing)
                            {
                                String target = NumericEncoder.encode(info.getId());
                                TermEnum termEnum = reader.terms(new Term(LuceneQueryParser.FIELD_TXID, target));
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
                                    AddUpdateCommand cmd = new AddUpdateCommand();
                                    cmd.overwriteCommitted = true;
                                    cmd.overwritePending = true;
                                    SolrInputDocument input = new SolrInputDocument();
                                    input.addField(LuceneQueryParser.FIELD_ID, "TX-" + info.getId());
                                    input.addField(LuceneQueryParser.FIELD_TXID, info.getId());
                                    input.addField(LuceneQueryParser.FIELD_TXCOMMITTIME, info.getCommitTimeMs());
                                    cmd.solrDoc = input;
                                    cmd.doc = DocumentBuilder.toDocument(cmd.getSolrInputDocument(), core.getSchema());
                                    core.getUpdateHandler().addDoc(cmd);

                                    GetNodesParameters gnp = new GetNodesParameters();
                                    ArrayList<Long> txs = new ArrayList<Long>();
                                    txs.add(info.getId());
                                    gnp.setTransactionIds(txs);
                                    gnp.setStoreProtocol("workspace");
                                    gnp.setStoreIdentifier("SpacesStore");
                                    List<Node> nodes = client.getNodes(gnp, (int) info.getUpdates());
                                    for (Node node : nodes)
                                    {
                                        System.out.println(node);
                                        if (node.getStatus() == STATUS.UPDATED)
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

                                                for (NodeMetaData nodeMetaData : nodeMetaDatas)
                                                {
                                                    SolrInputDocument doc = new SolrInputDocument();
                                                    doc.addField(LuceneQueryParser.FIELD_ID, "LEAF-" + node.getId());
                                                    doc.addField(LuceneQueryParser.FIELD_DBID, node.getId());

                                                    Map<QName, PropertyValue> properties = nodeMetaData.getProperties();
                                                    for (QName propertyQname : properties.keySet())
                                                    {
                                                        PropertyValue value = properties.get(propertyQname);
                                                        if (value != null)
                                                        {
                                                            if (value instanceof ContentPropertyValue)
                                                            {
                                                                ContentPropertyValue typedValue = (ContentPropertyValue) value;
                                                                doc.addField(LuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQname.toString(), typedValue.getId());
                                                            }
                                                            else if (value instanceof MLTextPropertyValue)
                                                            {
                                                                MLTextPropertyValue typedValue = (MLTextPropertyValue) value;
                                                                for (Locale locale : typedValue.getLocales())
                                                                {
                                                                    doc.addField(LuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQname.toString(), locale);
                                                                }
                                                            }
                                                            else if (value instanceof MultiPropertyValue)
                                                            {
                                                                MultiPropertyValue typedValue = (MultiPropertyValue) value;
                                                                for (PropertyValue singleValue : typedValue.getValues())
                                                                {
                                                                    if (singleValue instanceof ContentPropertyValue)
                                                                    {
                                                                        ContentPropertyValue current = (ContentPropertyValue) singleValue;
                                                                        doc.addField(LuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQname.toString(), current.getId());
                                                                    }
                                                                    else if (singleValue instanceof MLTextPropertyValue)
                                                                    {
                                                                        MLTextPropertyValue current = (MLTextPropertyValue) singleValue;
                                                                        for (Locale locale : current.getLocales())
                                                                        {
                                                                            doc.addField(LuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQname.toString(), locale);
                                                                        }
                                                                    }
                                                                    else if (singleValue instanceof StringPropertyValue)
                                                                    {
                                                                        StringPropertyValue current = (StringPropertyValue) singleValue;
                                                                        doc.addField(LuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQname.toString(), current.getValue());
                                                                    }
                                                                }
                                                            }
                                                            else if (value instanceof StringPropertyValue)
                                                            {
                                                                StringPropertyValue typedValue = (StringPropertyValue) value;
                                                                doc.addField(LuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQname.toString(), typedValue.getValue());
                                                            }

                                                        }
                                                    }
                                                    doc.addField(LuceneQueryParser.FIELD_TYPE, nodeMetaData.getType().toString());
                                                    for (QName aspect : nodeMetaData.getAspects())
                                                    {
                                                        doc.addField(LuceneQueryParser.FIELD_ASPECT, aspect.toString());
                                                    }
                                                    doc.addField(LuceneQueryParser.FIELD_ISNODE, "T");
                                                    
                                                    leafDocCmd.solrDoc = doc;
                                                    leafDocCmd.doc = DocumentBuilder.toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema());

                                                    SolrInputDocument aux = new SolrInputDocument();
                                                    aux.addField(LuceneQueryParser.FIELD_ID, "AUX-" + node.getId());
                                                    aux.addField(LuceneQueryParser.FIELD_DBID, node.getId());
                                                    aux.addField(LuceneQueryParser.FIELD_ACLID, nodeMetaData.getAclId());

                                                    for (Pair<String, QName> path : nodeMetaData.getPaths())
                                                    {
                                                        aux.addField(LuceneQueryParser.FIELD_PATH, path.getFirst());
                                                    }

                                                    StringPropertyValue owner = (StringPropertyValue) properties.get(ContentModel.PROP_OWNER);
                                                    if (owner == null)
                                                    {
                                                        owner = (StringPropertyValue) properties.get(ContentModel.PROP_CREATOR);
                                                    }
                                                    if (owner != null)
                                                    {
                                                        aux.addField(LuceneQueryParser.FIELD_OWNER, owner.getValue());
                                                    }

                                                    auxDocCmd.solrDoc = aux;
                                                    auxDocCmd.doc = DocumentBuilder.toDocument(auxDocCmd.getSolrInputDocument(), core.getSchema());

                                                }

                                                core.getUpdateHandler().addDoc(leafDocCmd);
                                                core.getUpdateHandler().addDoc(auxDocCmd);

                                            }
                                            catch (JSONException e)
                                            {
                                                System.out.println(e.getStackTrace());
                                            }

                                        }
                                        else if (node.getStatus() == STATUS.DELETED)
                                        {
                                            System.out.println(".. deleting");
                                            DeleteUpdateCommand docCmd = new DeleteUpdateCommand();
                                            docCmd.id = "LEAF-" + node.getId();
                                            docCmd.fromPending = true;
                                            docCmd.fromCommitted = true;
                                            core.getUpdateHandler().delete(docCmd);
                                        }

                                    }

                                    if (info.getCommitTimeMs() > lastIndexedCommitTime)
                                    {
                                        lastIndexedCommitTime = info.getCommitTimeMs();
                                    }

                                    lastTxCommitTime = info.getCommitTimeMs();
                                }
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
         * @param refCounted
         * @throws IOException
         * @throws JSONException
         */
        private IndexHealthReport checkIndex() throws IOException, JSONException
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
                    TermEnum termEnum = reader.terms(new Term(LuceneQueryParser.FIELD_TXID, target));
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
                        if (term.field().equals(LuceneQueryParser.FIELD_TXID))
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
                TermEnum termEnum = reader.terms(new Term(LuceneQueryParser.FIELD_TXID, ""));
                do
                {
                    Term term = termEnum.term();
                    if (term.field().equals(LuceneQueryParser.FIELD_TXID))
                    {
                        int docCount = 0;
                        TermDocs termDocs = reader.termDocs(new Term(LuceneQueryParser.FIELD_TXID, term.text()));
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
                termEnum = reader.terms(new Term(LuceneQueryParser.FIELD_ID, "LEAF-"));
                do
                {
                    Term term = termEnum.term();
                    if (term.field().equals(LuceneQueryParser.FIELD_ID) && term.text().startsWith("LEAF-"))
                    {
                        int docCount = 0;
                        TermDocs termDocs = reader.termDocs(new Term(LuceneQueryParser.FIELD_ID, term.text()));
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

            TermEnum termEnum = reader.terms(new Term(LuceneQueryParser.FIELD_TXCOMMITTIME, ""));
            do
            {
                Term term = termEnum.term();
                if (term == null)
                {
                    break;
                }
                if (term.field().equals(LuceneQueryParser.FIELD_TXCOMMITTIME))
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
                TermDocs docs = reader.termDocs(new Term(LuceneQueryParser.FIELD_TXCOMMITTIME, NumericEncoder.encode(lastTxCommitTimeBeforeHoles)));
                while (docs.next())
                {
                    Document doc = reader.document(docs.doc());
                    Fieldable field = doc.getFieldable(LuceneQueryParser.FIELD_TXID);
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
                TermEnum termEnum = reader.terms(new Term(LuceneQueryParser.FIELD_TXCOMMITTIME, ""));
                do
                {
                    Term term = termEnum.term();
                    if (term == null)
                    {
                        break;
                    }
                    if (term.field().equals(LuceneQueryParser.FIELD_TXCOMMITTIME))
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
    }

    public static class CoreTrackerJob implements Job
    {

        public CoreTrackerJob()
        {
            super();

        }

        /*
         * (non-Javadoc)
         * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
         */
        @Override
        public void execute(JobExecutionContext jec) throws JobExecutionException
        {
            CoreTracker coreTracker = (CoreTracker) jec.getJobDetail().getJobDataMap().get("TRACKER");
            coreTracker.track();
        }
    }

    public static class IndexHealthReport
    {
        long dbTransactionCount;

        OpenBitSet missingFromIndex = new OpenBitSet();

        OpenBitSet duplicatedInIndex = new OpenBitSet();

        OpenBitSet inIndexButNotInDb = new OpenBitSet();

        OpenBitSet duplicatedLeafInIndex = new OpenBitSet();

        long transactionDocsInIndex;

        long leafDocCountInIndex;

        long lastIndexedCommitTime;

        long lastIndexedIdBeforeHoles;

        /**
         * @return the transactionDocsInIndex
         */
        public long getTransactionDocsInIndex()
        {
            return transactionDocsInIndex;
        }

        /**
         * @param leafCount
         */
        public void setLeafDocCountInIndex(long leafDocCountInIndex)
        {
            this.leafDocCountInIndex = leafDocCountInIndex;
        }

        /**
         * @return the leafDocCountInIndex
         */
        public long getLeafDocCountInIndex()
        {
            return leafDocCountInIndex;
        }

        /**
         * @param txid
         */
        public void setDuplicatedLeafInIndex(long txid)
        {
            duplicatedLeafInIndex.set(txid);

        }

        /**
         * @return the duplicatedLeafInIndex
         */
        public OpenBitSet getDuplicatedLeafInIndex()
        {
            return duplicatedLeafInIndex;
        }

        /**
         * @param transactionDocsInIndex
         *            the transactionDocsInIndex to set
         */
        public void setTransactionDocsInIndex(long transactionDocsInIndex)
        {
            this.transactionDocsInIndex = transactionDocsInIndex;
        }

        /**
         * @return the missingFromIndex
         */
        public OpenBitSet getMissingFromIndex()
        {
            return missingFromIndex;
        }

        /**
         * @return the duplicatedInIndex
         */
        public OpenBitSet getDuplicatedInIndex()
        {
            return duplicatedInIndex;
        }

        /**
         * @return the inIndexButNotInDb
         */
        public OpenBitSet getInIndexButNotInDb()
        {
            return inIndexButNotInDb;
        }

        /**
         * @return the dbTransactionCount
         */
        public long getDbTransactionCount()
        {
            return dbTransactionCount;
        }

        /**
         * @param dbTransactionCount
         *            the dbTransactionCount to set
         */
        public void setDbTransactionCount(long dbTransactionCount)
        {
            this.dbTransactionCount = dbTransactionCount;
        }

        public void setMissingFromIndex(long txid)
        {
            missingFromIndex.set(txid);
        }

        public void setDuplicatedInIndex(long txid)
        {
            duplicatedInIndex.set(txid);
        }

        public void setInIndexButNotInDb(long txid)
        {
            inIndexButNotInDb.set(txid);
        }

        /**
         * @return the lastIndexCommitTime
         */
        public long getLastIndexCommitTime()
        {
            return lastIndexedCommitTime;
        }

        /**
         * @param lastIndexCommitTime
         *            the lastIndexCommitTime to set
         */
        public void setLastIndexedCommitTime(long lastIndexedCommitTime)
        {
            this.lastIndexedCommitTime = lastIndexedCommitTime;
        }

        /**
         * @return the lastIndexedIdBeforeHoles
         */
        public long getLastIndexedIdBeforeHoles()
        {
            return lastIndexedIdBeforeHoles;
        }

        /**
         * @param lastIndexedIdBeforeHoles
         *            the lastIndexedIdBeforeHoles to set
         */
        public void setLastIndexedIdBeforeHoles(long lastIndexedIdBeforeHoles)
        {
            this.lastIndexedIdBeforeHoles = lastIndexedIdBeforeHoles;
        }

    }
}
