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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.solr.client.AuthenticationException;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.tracker.CoreTracker;
import org.alfresco.solr.tracker.CoreWatcherJob;
import org.alfresco.solr.tracker.IndexHealthReport;
import org.alfresco.util.CachingDateFormat;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.handler.admin.CoreAdminHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryResponse;
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
            else if (a.equalsIgnoreCase("NODEREPORT"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    Long dbid = null;
                    if (params.get("dbid") != null)
                    {
                        dbid = Long.valueOf(params.get("dbid"));
                        NamedList<Object> report = new SimpleOrderedMap<Object>();
                        report.add(cname, buildNodeReport(tracker, dbid));
                        rsp.add("report", report);

                    }
                    else
                    {
                        throw new AlfrescoRuntimeException("No dbid parameter set");
                    }
                }
                else
                {
                    Long dbid = null;
                    if (params.get("dbid") != null)
                    {
                        dbid = Long.valueOf(params.get("dbid"));
                        NamedList<Object> report = new SimpleOrderedMap<Object>();
                        for (String trackerName : trackers.keySet())
                        {
                            CoreTracker tracker = trackers.get(trackerName);
                            report.add(trackerName, buildNodeReport(tracker, dbid));
                        }
                        rsp.add("report", report);
                    }
                    else
                    {
                        throw new AlfrescoRuntimeException("No dbid parameter set");
                    }

                }
                return false;
            }
            else if (a.equalsIgnoreCase("ACLREPORT"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    Long aclid = null;
                    if (params.get("aclid") != null)
                    {
                        aclid = Long.valueOf(params.get("aclid"));
                        NamedList<Object> report = new SimpleOrderedMap<Object>();
                        report.add(cname, buildAclReport(tracker, aclid));
                        rsp.add("report", report);

                    }
                    else
                    {
                        throw new AlfrescoRuntimeException("No aclid parameter set");
                    }
                }
                else
                {
                    Long aclid = null;
                    if (params.get("aclid") != null)
                    {
                        aclid = Long.valueOf(params.get("aclid"));
                        NamedList<Object> report = new SimpleOrderedMap<Object>();
                        for (String trackerName : trackers.keySet())
                        {
                            CoreTracker tracker = trackers.get(trackerName);
                            report.add(trackerName, buildAclReport(tracker, aclid));
                        }
                        rsp.add("report", report);
                    }
                    else
                    {
                        throw new AlfrescoRuntimeException("No dbid parameter set");
                    }

                }
                return false;
            }
            else if (a.equalsIgnoreCase("TXREPORT"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    Long txid = null;
                    if (params.get("txid") != null)
                    {
                        txid = Long.valueOf(params.get("txid"));
                        NamedList<Object> report = new SimpleOrderedMap<Object>();
                        report.add(cname, buildTxReport(tracker, txid));
                        rsp.add("report", report);

                    }
                    else
                    {
                        throw new AlfrescoRuntimeException("No txid parameter set");
                    }
                }
                else
                {
                    Long txid = null;
                    if (params.get("txid") != null)
                    {
                        txid = Long.valueOf(params.get("txid"));
                        NamedList<Object> report = new SimpleOrderedMap<Object>();
                        for (String trackerName : trackers.keySet())
                        {
                            CoreTracker tracker = trackers.get(trackerName);
                            report.add(trackerName, buildTxReport(tracker, txid));
                        }
                        rsp.add("report", report);
                    }
                    else
                    {
                        throw new AlfrescoRuntimeException("No txid parameter set");
                    }

                }
                return false;
            }
            else if (a.equalsIgnoreCase("ACLTXREPORT"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    Long acltxid = null;
                    if (params.get("acltxid") != null)
                    {
                        acltxid = Long.valueOf(params.get("acltxid"));
                        NamedList<Object> report = new SimpleOrderedMap<Object>();
                        report.add(cname, buildAclTxReport(tracker, acltxid));
                        rsp.add("report", report);

                    }
                    else
                    {
                        throw new AlfrescoRuntimeException("No acltxid parameter set");
                    }
                }
                else
                {
                    Long acltxid = null;
                    if (params.get("acltxid") != null)
                    {
                        acltxid = Long.valueOf(params.get("acltxid"));
                        NamedList<Object> report = new SimpleOrderedMap<Object>();
                        for (String trackerName : trackers.keySet())
                        {
                            CoreTracker tracker = trackers.get(trackerName);
                            report.add(trackerName, buildAclTxReport(tracker, acltxid));
                        }
                        rsp.add("report", report);
                    }
                    else
                    {
                        throw new AlfrescoRuntimeException("No acltxid parameter set");
                    }

                }
                return false;
            }
            else if (a.equalsIgnoreCase("REPORT"))
            {
                if (cname != null)
                {
                    Long fromTime = null;
                    if (params.get("fromTime") != null)
                    {
                        fromTime = Long.valueOf(params.get("fromTime"));
                    }
                    Long toTime = null;
                    if (params.get("toTime") != null)
                    {
                        toTime = Long.valueOf(params.get("toTime"));
                    }
                    Long fromTx = null;
                    if (params.get("fromTx") != null)
                    {
                        fromTx = Long.valueOf(params.get("fromTx"));
                    }
                    Long toTx = null;
                    if (params.get("toTx") != null)
                    {
                        toTx = Long.valueOf(params.get("toTx"));
                    }
                    Long fromAclTx = null;
                    if (params.get("fromAclTx") != null)
                    {
                        fromAclTx = Long.valueOf(params.get("fromAclTx"));
                    }
                    Long toAclTx = null;
                    if (params.get("toAclTx") != null)
                    {
                        toAclTx = Long.valueOf(params.get("toAclTx"));
                    }

                    CoreTracker tracker = trackers.get(cname);

                    NamedList<Object> report = new SimpleOrderedMap<Object>();
                    if (tracker != null)
                    {
                        report.add(cname, buildTrackerReport(tracker, fromTx, toTx, fromAclTx, toAclTx, fromTime, toTime));
                    }
                    else
                    {
                        report.add(cname, "Core unknown");
                    }
                    rsp.add("report", report);
                }
                else
                {
                    Long fromTime = null;
                    if (params.get("fromTime") != null)
                    {
                        fromTime = Long.valueOf(params.get("fromTime"));
                    }
                    Long toTime = null;
                    if (params.get("toTime") != null)
                    {
                        toTime = Long.valueOf(params.get("toTime"));
                    }
                    Long fromTx = null;
                    if (params.get("fromTx") != null)
                    {
                        fromTx = Long.valueOf(params.get("fromTx"));
                    }
                    Long toTx = null;
                    if (params.get("toTx") != null)
                    {
                        toTx = Long.valueOf(params.get("toTx"));
                    }
                    Long fromAclTx = null;
                    if (params.get("fromAclTx") != null)
                    {
                        fromAclTx = Long.valueOf(params.get("fromAclTx"));
                    }
                    Long toAclTx = null;
                    if (params.get("toAclTx") != null)
                    {
                        toAclTx = Long.valueOf(params.get("toAclTx"));
                    }

                    NamedList<Object> report = new SimpleOrderedMap<Object>();
                    for (String coreName : trackers.keySet())
                    {
                        CoreTracker tracker = trackers.get(coreName);
                        report.add(coreName, buildTrackerReport(tracker, fromTx, toTx, fromAclTx, toAclTx, fromTime, toTime));
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

    private NamedList<Object> buildAclTxReport(CoreTracker tracker, Long acltxid) throws AuthenticationException, IOException, JSONException
    {
        NamedList<Object> nr = new SimpleOrderedMap<Object>();
        nr.add("TXID", acltxid);
        nr.add("transaction", buildTrackerReport(tracker, 0l, 0l, acltxid, acltxid, null, null));
        NamedList<Object> nodes = new SimpleOrderedMap<Object>();
        // add node reports ....
        List<Long> dbAclIds = tracker.getAclsForDbAclTransaction(acltxid);
        for(Long aclid : dbAclIds)
        {
            nodes.add("ACLID "+aclid, buildAclReport(tracker, aclid));
        }
        nr.add("aclTxDbAclCount", dbAclIds.size());
        nr.add("nodes", nodes);
        return nr;
    }
    
    private NamedList<Object> buildAclReport(CoreTracker tracker, Long aclid) throws IOException, JSONException
    {
        AclReport aclReport = tracker.checkAcl(aclid);

        NamedList<Object> nr = new SimpleOrderedMap<Object>();
        nr.add("Acl Id", aclReport.getAclId());
        nr.add("Acl doc in index", aclReport.getIndexAclDoc());
        if (aclReport.getIndexAclDoc() != null)
        {
            nr.add("Acl tx in Index", aclReport.getIndexAclTx());
        }
      
        return nr;
    }

    private NamedList<Object> buildTxReport(CoreTracker tracker, Long txid) throws AuthenticationException, IOException, JSONException
    {
        NamedList<Object> nr = new SimpleOrderedMap<Object>();
        nr.add("TXID", txid);
        nr.add("transaction", buildTrackerReport(tracker, txid, txid, 0l, 0l, null, null));
        NamedList<Object> nodes = new SimpleOrderedMap<Object>();
        // add node reports ....
        List<Node> dbNodes = tracker.getFullNodesForDbTransaction(txid);
        for(Node node : dbNodes)
        {
            nodes.add("DBID "+node.getId(), buildNodeReport(tracker, node));
        }

        nr.add("txDbNodeCount", dbNodes.size());
        nr.add("nodes", nodes);
        return nr;
    }
    
    private NamedList<Object> buildNodeReport(CoreTracker tracker, Node node) throws IOException, JSONException
    {
        NodeReport nodeReport = tracker.checkNode(node);

        NamedList<Object> nr = new SimpleOrderedMap<Object>();
        nr.add("Node DBID", nodeReport.getDbid());
        nr.add("DB TX", nodeReport.getDbTx());
        nr.add("DB TX status", nodeReport.getDbNodeStatus().toString());
        nr.add("Leaf doc in Index", nodeReport.getIndexLeafDoc());
        nr.add("Aux doc in Index", nodeReport.getIndexAuxDoc());
        if (nodeReport.getIndexLeafDoc() != null)
        {
            nr.add("Leaf tx in Index", nodeReport.getIndexLeafTx());
        }
        if (nodeReport.getIndexAuxDoc() != null)
        {
            nr.add("Aux tx in Index", nodeReport.getIndexAuxTx());
        }
        return nr;
    }
    
    private NamedList<Object> buildNodeReport(CoreTracker tracker, Long dbid) throws IOException, JSONException
    {
        NodeReport nodeReport = tracker.checkNode(dbid);

        NamedList<Object> nr = new SimpleOrderedMap<Object>();
        nr.add("Node DBID", nodeReport.getDbid());
        nr.add("DB TX", nodeReport.getDbTx());
        nr.add("DB TX status", nodeReport.getDbNodeStatus().toString());
        nr.add("Leaf doc in Index", nodeReport.getIndexLeafDoc());
        nr.add("Aux doc in Index", nodeReport.getIndexAuxDoc());
        if (nodeReport.getIndexLeafDoc() != null)
        {
            nr.add("Leaf tx in Index", nodeReport.getIndexLeafTx());
        }
        if (nodeReport.getIndexAuxDoc() != null)
        {
            nr.add("Aux tx in Index", nodeReport.getIndexAuxTx());
        }
        return nr;
    }

    private NamedList<Object> buildTrackerReport(CoreTracker tracker, Long fromTx, Long toTx, Long fromAclTx, Long toAclTx, Long fromTime, Long toTime) throws IOException,
            JSONException, AuthenticationException
    {
        IndexHealthReport indexHealthReport = tracker.checkIndex(fromTx, toTx, fromAclTx, toAclTx, fromTime, toTime);

        NamedList<Object> ihr = new SimpleOrderedMap<Object>();
        ihr.add("DB transaction count", indexHealthReport.getDbTransactionCount());
        ihr.add("DB acl transaction count", indexHealthReport.getDbAclTransactionCount());
        ihr.add("Count of duplicated transactions in the index", indexHealthReport.getDuplicatedTxInIndex().cardinality());
        if (indexHealthReport.getDuplicatedTxInIndex().cardinality() > 0)
        {
            ihr.add("First duplicate", indexHealthReport.getDuplicatedTxInIndex().nextSetBit(0L));
        }
        ihr.add("Count of duplicated acl transactions in the index", indexHealthReport.getDuplicatedAclTxInIndex().cardinality());
        if (indexHealthReport.getDuplicatedAclTxInIndex().cardinality() > 0)
        {
            ihr.add("First duplicate acl tx", indexHealthReport.getDuplicatedAclTxInIndex().nextSetBit(0L));
        }
        ihr.add("Count of transactions in the index but not the DB", indexHealthReport.getTxInIndexButNotInDb().cardinality());
        if (indexHealthReport.getTxInIndexButNotInDb().cardinality() > 0)
        {
            ihr.add("First transaction in the index but not the DB", indexHealthReport.getTxInIndexButNotInDb().nextSetBit(0L));
        }
        ihr.add("Count of acl transactions in the index but not the DB", indexHealthReport.getAclTxInIndexButNotInDb().cardinality());
        if (indexHealthReport.getAclTxInIndexButNotInDb().cardinality() > 0)
        {
            ihr.add("First acl transaction in the index but not the DB", indexHealthReport.getAclTxInIndexButNotInDb().nextSetBit(0L));
        }
        ihr.add("Count of missing transactions from the Index", indexHealthReport.getMissingTxFromIndex().cardinality());
        if (indexHealthReport.getMissingTxFromIndex().cardinality() > 0)
        {
            ihr.add("First transaction missing from the Index", indexHealthReport.getMissingTxFromIndex().nextSetBit(0L));
        }
        ihr.add("Count of missing acl transactions from the Index", indexHealthReport.getMissingAclTxFromIndex().cardinality());
        if (indexHealthReport.getMissingAclTxFromIndex().cardinality() > 0)
        {
            ihr.add("First acl transaction missing from the Index", indexHealthReport.getMissingAclTxFromIndex().nextSetBit(0L));
        }
        ihr.add("Index transaction count", indexHealthReport.getTransactionDocsInIndex());
        ihr.add("Index acl transaction count", indexHealthReport.getAclTransactionDocsInIndex());
        ihr.add("Index unique transaction count", indexHealthReport.getTransactionDocsInIndex());
        ihr.add("Index unique acl transaction count", indexHealthReport.getAclTransactionDocsInIndex());
        ihr.add("Index leaf count", indexHealthReport.getLeafDocCountInIndex());
        ihr.add("Count of duplicate leaves in the index", indexHealthReport.getDuplicatedLeafInIndex().cardinality());
        if (indexHealthReport.getDuplicatedLeafInIndex().cardinality() > 0)
        {
            ihr.add("First duplicate leaf in the index", "LEAF-" + indexHealthReport.getDuplicatedLeafInIndex().nextSetBit(0L));
        }
        ihr.add("Last index commit time", indexHealthReport.getLastIndexedCommitTime());
        Date lastDate = new Date(indexHealthReport.getLastIndexedCommitTime());
        ihr.add("Last Index commit date", CachingDateFormat.getDateFormat().format(lastDate));
        ihr.add("Last TX id before holes", indexHealthReport.getLastIndexedIdBeforeHoles());
        return ihr;
    }

}
