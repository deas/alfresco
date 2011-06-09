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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.analysis.MLTokenDuplicator;
import org.alfresco.repo.search.impl.lucene.analysis.VerbatimAnalyser;
import org.alfresco.solr.tracker.CoreTracker;
import org.alfresco.solr.tracker.CoreWatcherJob;
import org.alfresco.solr.tracker.IndexHealthReport;
import org.alfresco.util.CachingDateFormat;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.handler.admin.CoreAdminHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryResponse;
import org.apache.solr.schema.BinaryField;
import org.apache.solr.schema.CopyField;
import org.apache.solr.schema.DateField;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.json.JSONException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author Andy
 */
public class AlfrescoCoreAdminHandler extends CoreAdminHandler
{
    Scheduler scheduler = null;

    HashMap<String, CoreTracker> trackers = new HashMap<String, CoreTracker>();

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

    
    
    /**
     * @return the scheduler
     */
    public Scheduler getScheduler()
    {
        return scheduler;
    }
    
    

    /**
     * @return the trackers
     */
    public HashMap<String, CoreTracker> getTrackers()
    {
        return trackers;
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
                    tracker.setCheck(true);
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

    
}
