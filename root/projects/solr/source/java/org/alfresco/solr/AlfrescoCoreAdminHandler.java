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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.MultiReader;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.MLText;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Period;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.repository.datatype.Duration;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.client.ContentPropertyValue;
import org.alfresco.solr.client.MLTextPropertyValue;
import org.alfresco.solr.client.MultiPropertyValue;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.PropertyValue;
import org.alfresco.solr.client.StringPropertyValue;
import org.alfresco.solr.query.SolrQueryParser;
import org.alfresco.solr.tracker.CoreTracker;
import org.alfresco.solr.tracker.CoreWatcherJob;
import org.alfresco.solr.tracker.IndexHealthReport;
import org.alfresco.util.CachingDateFormat;
import org.alfresco.util.GUID;
import org.alfresco.util.ISO9075;
import org.alfresco.util.CachingDateFormat.SimpleDateFormatAndResolution;
import org.apache.chemistry.opencmis.server.support.query.CmisQlStrictParser.root_return;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.FileUtils;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.admin.CoreAdminHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryResponse;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.apache.solr.util.RefCounted;
import org.json.JSONException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import org.xml.sax.SAXException;

/**
 * @author Andy
 */
public class AlfrescoCoreAdminHandler extends CoreAdminHandler
{
    /**
     * 
     */
    private static final String ARG_ACLTXID = "acltxid";

    /**
     * 
     */
    private static final String ARG_TXID = "txid";

    /**
     * 
     */
    private static final String ARG_ACLID = "aclid";

    /**
     * 
     */
    private static final String ARG_NODEID = "nodeid";

    private static final String TEST_NAMESPACE = "http://www.alfresco.org/test/solrtest";

    QName createdDate = QName.createQName(TEST_NAMESPACE, "createdDate");

    QName orderDouble = QName.createQName(TEST_NAMESPACE, "orderDouble");

    QName orderFloat = QName.createQName(TEST_NAMESPACE, "orderFloat");

    QName orderLong = QName.createQName(TEST_NAMESPACE, "orderLong");

    QName orderInt = QName.createQName(TEST_NAMESPACE, "orderInt");

    QName orderText = QName.createQName(TEST_NAMESPACE, "orderText");

    QName orderMLText = QName.createQName(TEST_NAMESPACE, "orderMLText");

    QName aspectWithChildren = QName.createQName(TEST_NAMESPACE, "aspectWithChildren");

    private QName testType = QName.createQName(TEST_NAMESPACE, "testType");

    private QName testSuperType = QName.createQName(TEST_NAMESPACE, "testSuperType");

    private QName testAspect = QName.createQName(TEST_NAMESPACE, "testAspect");

    private QName testSuperAspect = QName.createQName(TEST_NAMESPACE, "testSuperAspect");

    protected final static Logger log = LoggerFactory.getLogger(AlfrescoCoreAdminHandler.class);

    Scheduler scheduler = null;

    ConcurrentHashMap<String, CoreTracker> trackers = new ConcurrentHashMap<String, CoreTracker>();

    private double orderDoubleCount = -0.11d;

    private Date orderDate = new Date();

    private float orderFloatCount = -3.5556f;

    private long orderLongCount = -1999999999999999l;

    private int orderIntCount = -45764576;

    private int orderTextCount = 0;

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
            properties.setProperty("org.quartz.threadPool.makeThreadsDaemons", "true");
            properties.setProperty("org.quartz.scheduler.makeSchedulerThreadDaemon", "true");
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
    public ConcurrentHashMap<String, CoreTracker> getTrackers()
    {
        return trackers;
    }

    protected boolean handleCustomAction(SolrQueryRequest req, SolrQueryResponse rsp)
    {
        SolrParams params = req.getParams();
        String cname = params.get(CoreAdminParams.CORE);
        String a = params.get(CoreAdminParams.ACTION);
        try
        {
            if (a.equalsIgnoreCase("TEST"))
            {
                runTests(req, rsp);
                return false;
            }
            else if (a.equalsIgnoreCase("CHECK"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    if (tracker != null)
                    {
                        tracker.setCheck(true);
                    }
                }
                else
                {
                    for (String trackerName : trackers.keySet())
                    {
                        CoreTracker tracker = trackers.get(trackerName);
                        tracker.setCheck(true);
                    }
                }
                return false;
            }
            else if (a.equalsIgnoreCase("NODEREPORT"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    Long dbid = null;
                    if (params.get(ARG_NODEID) != null)
                    {
                        dbid = Long.valueOf(params.get(ARG_NODEID));
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
                    if (params.get(ARG_NODEID) != null)
                    {
                        dbid = Long.valueOf(params.get(ARG_NODEID));
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
                    if (params.get(ARG_ACLID) != null)
                    {
                        aclid = Long.valueOf(params.get(ARG_ACLID));
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
                    if (params.get(ARG_ACLID) != null)
                    {
                        aclid = Long.valueOf(params.get(ARG_ACLID));
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
                    if (params.get(ARG_TXID) != null)
                    {
                        txid = Long.valueOf(params.get(ARG_TXID));
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
                    if (params.get(ARG_TXID) != null)
                    {
                        txid = Long.valueOf(params.get(ARG_TXID));
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
                    if (params.get(ARG_ACLTXID) != null)
                    {
                        acltxid = Long.valueOf(params.get(ARG_ACLTXID));
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
                    if (params.get(ARG_ACLTXID) != null)
                    {
                        acltxid = Long.valueOf(params.get(ARG_ACLTXID));
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
            else if (a.equalsIgnoreCase("PURGE"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    if (params.get(ARG_TXID) != null)
                    {
                        Long txid = Long.valueOf(params.get(ARG_TXID));
                        tracker.addTransactionToPurge(txid);
                    }
                    if (params.get(ARG_ACLTXID) != null)
                    {
                        Long acltxid = Long.valueOf(params.get(ARG_ACLTXID));
                        tracker.addAclChangeSetToPurge(acltxid);
                    }
                    if (params.get(ARG_NODEID) != null)
                    {
                        Long nodeid = Long.valueOf(params.get(ARG_NODEID));
                        tracker.addNodeToPurge(nodeid);
                    }
                    if (params.get(ARG_ACLID) != null)
                    {
                        Long aclid = Long.valueOf(params.get(ARG_ACLID));
                        tracker.addAclToPurge(aclid);
                    }

                }
                else
                {
                    for (String coreName : trackers.keySet())
                    {
                        CoreTracker tracker = trackers.get(coreName);
                        if (params.get(ARG_TXID) != null)
                        {
                            Long txid = Long.valueOf(params.get(ARG_TXID));
                            tracker.addTransactionToPurge(txid);
                        }
                        if (params.get(ARG_ACLTXID) != null)
                        {
                            Long acltxid = Long.valueOf(params.get(ARG_ACLTXID));
                            tracker.addAclChangeSetToPurge(acltxid);
                        }
                        if (params.get(ARG_NODEID) != null)
                        {
                            Long nodeid = Long.valueOf(params.get(ARG_NODEID));
                            tracker.addNodeToPurge(nodeid);
                        }
                        if (params.get(ARG_ACLID) != null)
                        {
                            Long aclid = Long.valueOf(params.get(ARG_ACLID));
                            tracker.addAclToPurge(aclid);
                        }
                    }
                }
                return false;
            }
            else if (a.equalsIgnoreCase("REINDEX"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    if (params.get(ARG_TXID) != null)
                    {
                        Long txid = Long.valueOf(params.get(ARG_TXID));
                        tracker.addTransactionToReindex(txid);
                    }
                    if (params.get(ARG_ACLTXID) != null)
                    {
                        Long acltxid = Long.valueOf(params.get(ARG_ACLTXID));
                        tracker.addAclChangeSetToReindex(acltxid);
                    }
                    if (params.get(ARG_NODEID) != null)
                    {
                        Long nodeid = Long.valueOf(params.get(ARG_NODEID));
                        tracker.addNodeToReindex(nodeid);
                    }
                    if (params.get(ARG_ACLID) != null)
                    {
                        Long aclid = Long.valueOf(params.get(ARG_ACLID));
                        tracker.addAclToReindex(aclid);
                    }

                }
                else
                {
                    for (String coreName : trackers.keySet())
                    {
                        CoreTracker tracker = trackers.get(coreName);
                        if (params.get(ARG_TXID) != null)
                        {
                            Long txid = Long.valueOf(params.get(ARG_TXID));
                            tracker.addTransactionToReindex(txid);
                        }
                        if (params.get(ARG_ACLTXID) != null)
                        {
                            Long acltxid = Long.valueOf(params.get(ARG_ACLTXID));
                            tracker.addAclChangeSetToReindex(acltxid);
                        }
                        if (params.get(ARG_NODEID) != null)
                        {
                            Long nodeid = Long.valueOf(params.get(ARG_NODEID));
                            tracker.addNodeToReindex(nodeid);
                        }
                        if (params.get(ARG_ACLID) != null)
                        {
                            Long aclid = Long.valueOf(params.get(ARG_ACLID));
                            tracker.addAclToReindex(aclid);
                        }
                    }
                }
                return false;
            }
            else if (a.equalsIgnoreCase("INDEX"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    if (params.get(ARG_TXID) != null)
                    {
                        Long txid = Long.valueOf(params.get(ARG_TXID));
                        tracker.addTransactionToIndex(txid);
                    }
                    if (params.get(ARG_ACLTXID) != null)
                    {
                        Long acltxid = Long.valueOf(params.get(ARG_ACLTXID));
                        tracker.addAclChangeSetToIndex(acltxid);
                    }
                    if (params.get(ARG_NODEID) != null)
                    {
                        Long nodeid = Long.valueOf(params.get(ARG_NODEID));
                        tracker.addNodeToIndex(nodeid);
                    }
                    if (params.get(ARG_ACLID) != null)
                    {
                        Long aclid = Long.valueOf(params.get(ARG_ACLID));
                        tracker.addAclToIndex(aclid);
                    }

                }
                else
                {
                    for (String coreName : trackers.keySet())
                    {
                        CoreTracker tracker = trackers.get(coreName);
                        if (params.get(ARG_TXID) != null)
                        {
                            Long txid = Long.valueOf(params.get(ARG_TXID));
                            tracker.addTransactionToIndex(txid);
                        }
                        if (params.get(ARG_ACLTXID) != null)
                        {
                            Long acltxid = Long.valueOf(params.get(ARG_ACLTXID));
                            tracker.addAclChangeSetToIndex(acltxid);
                        }
                        if (params.get(ARG_NODEID) != null)
                        {
                            Long nodeid = Long.valueOf(params.get(ARG_NODEID));
                            tracker.addNodeToIndex(nodeid);
                        }
                        if (params.get(ARG_ACLID) != null)
                        {
                            Long aclid = Long.valueOf(params.get(ARG_ACLID));
                            tracker.addAclToIndex(aclid);
                        }
                    }
                }
                return false;
            }
            else if (a.equalsIgnoreCase("FIX"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    IndexHealthReport indexHealthReport = tracker.checkIndex(null, null, null, null, null, null);
                    OpenBitSet toReindex = indexHealthReport.getTxInIndexButNotInDb();
                    toReindex.or(indexHealthReport.getDuplicatedTxInIndex());
                    toReindex.or(indexHealthReport.getMissingTxFromIndex());
                    long current = -1;
                    while ((current = toReindex.nextSetBit(current + 1)) != -1)
                    {
                        tracker.addTransactionToReindex(current);
                    }
                    toReindex = indexHealthReport.getAclTxInIndexButNotInDb();
                    toReindex.or(indexHealthReport.getDuplicatedAclTxInIndex());
                    toReindex.or(indexHealthReport.getMissingAclTxFromIndex());
                    current = -1;
                    while ((current = toReindex.nextSetBit(current + 1)) != -1)
                    {
                        tracker.addAclChangeSetToReindex(current);
                    }

                }
                else
                {
                    for (String coreName : trackers.keySet())
                    {
                        CoreTracker tracker = trackers.get(coreName);
                        IndexHealthReport indexHealthReport = tracker.checkIndex(null, null, null, null, null, null);
                        OpenBitSet toReindex = indexHealthReport.getTxInIndexButNotInDb();
                        toReindex.or(indexHealthReport.getDuplicatedTxInIndex());
                        toReindex.or(indexHealthReport.getMissingTxFromIndex());
                        long current = -1;
                        while ((current = toReindex.nextSetBit(current + 1)) != -1)
                        {
                            tracker.addTransactionToReindex(current);
                        }
                        toReindex = indexHealthReport.getAclTxInIndexButNotInDb();
                        toReindex.or(indexHealthReport.getDuplicatedAclTxInIndex());
                        toReindex.or(indexHealthReport.getMissingAclTxFromIndex());
                        current = -1;
                        while ((current = toReindex.nextSetBit(current + 1)) != -1)
                        {
                            tracker.addAclChangeSetToReindex(current);
                        }
                    }
                }
                return false;
            }
            else if (a.equalsIgnoreCase("SUMMARY"))
            {
                boolean reset = false;
                boolean detail = false;
                boolean hist = false;
                boolean values = false;
                if (params.get("reset") != null)
                {
                    reset = Boolean.valueOf(params.get("reset"));
                }
                if (params.get("detail") != null)
                {
                    detail = Boolean.valueOf(params.get("detail"));
                }
                if (params.get("hist") != null)
                {
                    hist = Boolean.valueOf(params.get("hist"));
                }
                if (params.get("values") != null)
                {
                    values = Boolean.valueOf(params.get("values"));
                }

                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);

                    NamedList<Object> report = new SimpleOrderedMap<Object>();
                    if (tracker != null)
                    {
                        addCoreSummary(cname, detail, hist, values, tracker, report);

                        if (reset)
                        {
                            tracker.getTrackerStats().reset();
                        }
                    }
                    else
                    {
                        report.add(cname, "Core unknown");
                    }
                    rsp.add("Summary", report);

                }
                else
                {
                    NamedList<Object> report = new SimpleOrderedMap<Object>();
                    for (String coreName : trackers.keySet())
                    {
                        CoreTracker tracker = trackers.get(coreName);
                        if (tracker != null)
                        {
                            addCoreSummary(cname, detail, hist, values, tracker, report);

                            if (reset)
                            {
                                tracker.getTrackerStats().reset();
                            }
                        }
                        else
                        {
                            report.add(cname, "Core unknown");
                        }
                    }
                    rsp.add("Summary", report);
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
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Error executing implementation of admin request " + a, ex);
        }
    }

    /**
     * @param req
     * @param rsp
     */
    private void runTests(SolrQueryRequest req, SolrQueryResponse rsp)
    {

        try
        {
            boolean remove = true;
            SolrParams params = req.getParams();
            if (params.get("remove") != null)
            {
                remove = Boolean.valueOf(params.get("remove"));
            }

            String name = "test-" + System.nanoTime();

            // copy core from template

            File solrHome = new File(getCoreContainer().getSolrHome());
            File templates = new File(solrHome, "templates");
            File template = new File(templates, "test");

            File newCore = new File(solrHome, name);

            copyDirectory(template, newCore, false);

            // add core

            CoreDescriptor dcore = new CoreDescriptor(coreContainer, name, newCore.toString());
            dcore.setCoreProperties(null);
            SolrCore core = coreContainer.create(dcore);
            coreContainer.register(name, core, false);
            rsp.add("core", core.getName());

            SolrResourceLoader loader = core.getSchema().getResourceLoader();
            String id = loader.getInstanceDir();
            AlfrescoSolrDataModel dataModel = AlfrescoSolrDataModel.getInstance(id);
            // add data

            // Root

            NodeRef rootNodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            addStoreRoot(core, dataModel, rootNodeRef, 1, 1, 1, 1);
            rsp.add("RootNode", 1);

            // 1

            NodeRef n01NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n01QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "one");
            ChildAssociationRef n01CAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, n01QName, n01NodeRef, true, 0);
            addNode(core, dataModel, 1, 2, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n01CAR }, new NodeRef[] { rootNodeRef },
                    new String[] { "/" + n01QName.toString() });

            // 2

            NodeRef n02NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n02QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "two");
            ChildAssociationRef n02CAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, n02QName, n02NodeRef, true, 0);
            addNode(core, dataModel, 1, 3, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n02CAR }, new NodeRef[] { rootNodeRef },
                    new String[] { "/" + n02QName.toString() });

            // 3

            NodeRef n03NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n03QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "three");
            ChildAssociationRef n03CAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, n03QName, n03NodeRef, true, 0);
            addNode(core, dataModel, 1, 4, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n03CAR }, new NodeRef[] { rootNodeRef },
                    new String[] { "/" + n03QName.toString() });

            // 4

            HashMap<QName, PropertyValue> properties04 = new HashMap<QName, PropertyValue>();
            HashMap<QName, String> content04 = new HashMap<QName, String>();
            properties04.put(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic"), new StringPropertyValue(
                    "TEXT THAT IS INDEXED STORED AND TOKENISED ATOMICALLY KEYONE"));
            properties04.put(QName.createQName(TEST_NAMESPACE, "text-indexed-unstored-tokenised-atomic"), new StringPropertyValue(
                    "TEXT THAT IS INDEXED STORED AND TOKENISED ATOMICALLY KEYUNSTORED"));
            properties04.put(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-nonatomic"), new StringPropertyValue(
                    "TEXT THAT IS INDEXED STORED AND TOKENISED BUT NOT ATOMICALLY KEYTWO"));
            properties04.put(QName.createQName(TEST_NAMESPACE, "int-ista"), new StringPropertyValue("1"));
            properties04.put(QName.createQName(TEST_NAMESPACE, "long-ista"), new StringPropertyValue("2"));
            properties04.put(QName.createQName(TEST_NAMESPACE, "float-ista"), new StringPropertyValue("3.4"));
            properties04.put(QName.createQName(TEST_NAMESPACE, "double-ista"), new StringPropertyValue("5.6"));

            Calendar c = new GregorianCalendar();
            c.setTime(new Date(((new Date().getTime() - 10000))));
            Date testDate = c.getTime();
            properties04.put(QName.createQName(TEST_NAMESPACE, "date-ista"), new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, testDate)));
            properties04.put(QName.createQName(TEST_NAMESPACE, "datetime-ista"), new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, testDate)));
            properties04
                    .put(QName.createQName(TEST_NAMESPACE, "boolean-ista"), new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, Boolean.valueOf(true))));
            properties04.put(QName.createQName(TEST_NAMESPACE, "qname-ista"),
                    new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, QName.createQName("{wibble}wobble"))));
            properties04.put(QName.createQName(TEST_NAMESPACE, "category-ista"),
                    new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, new NodeRef(new StoreRef("proto", "id"), "CategoryId"))));
            properties04.put(QName.createQName(TEST_NAMESPACE, "noderef-ista"), new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, n01NodeRef)));
            properties04.put(QName.createQName(TEST_NAMESPACE, "path-ista"), new StringPropertyValue("/" + n03QName.toString()));
            properties04.put(QName.createQName(TEST_NAMESPACE, "locale-ista"), new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, Locale.UK)));
            properties04.put(QName.createQName(TEST_NAMESPACE, "period-ista"),
                    new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, new Period("period|12"))));
            properties04.put(QName.createQName(TEST_NAMESPACE, "null"), null);
            MultiPropertyValue list_0 = new MultiPropertyValue();
            list_0.addValue(new StringPropertyValue("one"));
            list_0.addValue(new StringPropertyValue("two"));
            properties04.put(QName.createQName(TEST_NAMESPACE, "list"), list_0);
            MLTextPropertyValue mlText = new MLTextPropertyValue();
            mlText.addValue(Locale.ENGLISH, "banana");
            mlText.addValue(Locale.FRENCH, "banane");
            mlText.addValue(Locale.CHINESE, "香蕉");
            mlText.addValue(new Locale("nl"), "banaan");
            mlText.addValue(Locale.GERMAN, "banane");
            mlText.addValue(new Locale("el"), "μπανάνα");
            mlText.addValue(Locale.ITALIAN, "banana");
            mlText.addValue(new Locale("ja"), "バナナ");
            mlText.addValue(new Locale("ko"), "바나나");
            mlText.addValue(new Locale("pt"), "banana");
            mlText.addValue(new Locale("ru"), "банан");
            mlText.addValue(new Locale("es"), "plátano");
            properties04.put(QName.createQName(TEST_NAMESPACE, "ml"), mlText);
            MultiPropertyValue list_1 = new MultiPropertyValue();
            list_1.addValue(new StringPropertyValue("100"));
            list_1.addValue(new StringPropertyValue("anyValueAsString"));
            properties04.put(QName.createQName(TEST_NAMESPACE, "any-many-ista"), list_1);
            MultiPropertyValue list_2 = new MultiPropertyValue();
            list_2.addValue(new ContentPropertyValue(Locale.UK, 0L, "UTF-16", "text/plain"));
            properties04.put(QName.createQName(TEST_NAMESPACE, "content-many-ista"), list_2);
            content04.put(QName.createQName(TEST_NAMESPACE, "content-many-ista"), "multicontent");

            MLTextPropertyValue mlText1 = new MLTextPropertyValue();
            mlText1.addValue(Locale.ENGLISH, "cabbage");
            mlText1.addValue(Locale.FRENCH, "chou");

            MLTextPropertyValue mlText2 = new MLTextPropertyValue();
            mlText2.addValue(Locale.ENGLISH, "lemur");
            mlText2.addValue(new Locale("ru"), "лемур");

            MultiPropertyValue list_3 = new MultiPropertyValue();
            list_3.addValue(mlText1);
            list_3.addValue(mlText2);

            properties04.put(QName.createQName(TEST_NAMESPACE, "mltext-many-ista"), list_3);

            MultiPropertyValue list_4 = new MultiPropertyValue();
            list_4.addValue(null);
            properties04.put(QName.createQName(TEST_NAMESPACE, "nullist"), list_4);

            NodeRef n04NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n04QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "four");
            ChildAssociationRef n04CAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, n04QName, n04NodeRef, true, 0);
            addNode(core, dataModel, 1, 5, 1, testType, null, properties04, null, "system", new ChildAssociationRef[] { n04CAR }, new NodeRef[] { rootNodeRef }, new String[] { "/"
                    + n04QName.toString() });

            // 5

            NodeRef n05NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n05QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "five");
            ChildAssociationRef n05CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n01NodeRef, n05QName, n05NodeRef, true, 0);
            addNode(core, dataModel, 1, 6, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n05CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() });

            // 6

            NodeRef n06NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n06QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "six");
            ChildAssociationRef n06CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n01NodeRef, n06QName, n06NodeRef, true, 0);
            addNode(core, dataModel, 1, 7, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n06CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n06QName.toString() });

            // 7

            NodeRef n07NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n07QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "seven");
            ChildAssociationRef n07CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n02NodeRef, n07QName, n07NodeRef, true, 0);
            addNode(core, dataModel, 1, 8, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n07CAR }, new NodeRef[] { rootNodeRef,
                    n02NodeRef }, new String[] { "/" + n02QName.toString() + "/" + n07QName.toString() });

            // 8

            NodeRef n08NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n08QName_0 = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "eight-0");
            QName n08QName_1 = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "eight-1");
            QName n08QName_2 = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "eight-2");
            ChildAssociationRef n08CAR_0 = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, n08QName_0, n08NodeRef, false, 2);
            ChildAssociationRef n08CAR_1 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n01NodeRef, n08QName_1, n08NodeRef, false, 1);
            ChildAssociationRef n08CAR_2 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n02NodeRef, n08QName_2, n08NodeRef, true, 0);

            addNode(core, dataModel, 1, 9, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n08CAR_0, n08CAR_1, n08CAR_2 }, new NodeRef[] {
                    rootNodeRef, rootNodeRef, n01NodeRef, rootNodeRef, n02NodeRef }, new String[] { "/" + n08QName_0, "/" + n01QName.toString() + "/" + n08QName_1.toString(),
                    "/" + n02QName.toString() + "/" + n08QName_2.toString() });

            // 9

            NodeRef n09NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n09QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "nine");
            ChildAssociationRef n09CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n05NodeRef, n09QName, n09NodeRef, true, 0);
            addNode(core, dataModel, 1, 10, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n09CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n09QName });

            // 10

            NodeRef n10NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n10QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "ten");
            ChildAssociationRef n10CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n05NodeRef, n10QName, n10NodeRef, true, 0);
            addNode(core, dataModel, 1, 11, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n10CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n10QName });

            // 11

            NodeRef n11NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n11QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "eleven");
            ChildAssociationRef n11CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n05NodeRef, n11QName, n11NodeRef, true, 0);
            addNode(core, dataModel, 1, 12, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n11CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n11QName });

            // 12

            NodeRef n12NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n12QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "twelve");
            ChildAssociationRef n12CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n05NodeRef, n12QName, n12NodeRef, true, 0);
            addNode(core, dataModel, 1, 13, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n12CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName });

            // 13

            NodeRef n13NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n13QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "thirteen");
            QName n13QNameLink = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "link");
            ChildAssociationRef n13CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n12NodeRef, n13QName, n13NodeRef, true, 0);
            ChildAssociationRef n13CARLink = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n02NodeRef, n13QName, n13NodeRef, false, 0);
            addNode(core, dataModel, 1, 14, 1, testSuperType, null, getOrderProperties(), null, "system", new ChildAssociationRef[] { n13CAR, n13CARLink }, new NodeRef[] {
                    rootNodeRef, n01NodeRef, n05NodeRef, n12NodeRef, rootNodeRef, n02NodeRef }, new String[] {
                    "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName + "/" + n13QName, "/" + n02QName.toString() + "/" + n13QNameLink });

            // 14

            HashMap<QName, PropertyValue> properties14 = new HashMap<QName, PropertyValue>();
            HashMap<QName, String> content14 = new HashMap<QName, String>();
            MLTextPropertyValue desc1 = new MLTextPropertyValue();
            desc1.addValue(Locale.ENGLISH, "Alfresco tutorial");
            desc1.addValue(Locale.US, "Alfresco tutorial");

            Date explicitCreatedDate = new Date();
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            properties14.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0L, "UTF-8", "text/plain"));
            content14.put(ContentModel.PROP_CONTENT,
                    "The quick brown fox jumped over the lazy dog and ate the Alfresco Tutorial, in pdf format, along with the following stop words;  a an and are"
                            + " as at be but by for if in into is it no not of on or such that the their then there these they this to was will with: "
                            + " and random charcters \u00E0\u00EA\u00EE\u00F0\u00F1\u00F6\u00FB\u00FF");
            properties14.put(ContentModel.PROP_DESCRIPTION, desc1);
            properties14.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, explicitCreatedDate)));

            NodeRef n14NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n14QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "fourteen");
            QName n14QNameCommon = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "common");
            ChildAssociationRef n14CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n13NodeRef, n14QName, n14NodeRef, true, 0);
            ChildAssociationRef n14CAR_1 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n01NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            ChildAssociationRef n14CAR_2 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n02NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            ChildAssociationRef n14CAR_5 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n05NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            ChildAssociationRef n14CAR_6 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n06NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            ChildAssociationRef n14CAR_12 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n12NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            ChildAssociationRef n14CAR_13 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n13NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            addNode(core, dataModel, 1, 15, 1, ContentModel.TYPE_CONTENT, null, properties14, content14, "system", new ChildAssociationRef[] { n14CAR, n14CAR_1, n14CAR_2,
                    n14CAR_5, n14CAR_5, n14CAR_12, n14CAR_13 }, new NodeRef[] { rootNodeRef, n01NodeRef, n05NodeRef, n12NodeRef, n13NodeRef },
                    new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName + "/" + n13QName + "/" + n14QName,
                            "/" + n02QName.toString() + "/" + n13QNameLink + "/" + n14QName, "/" + n01QName + "/" + n14QNameCommon, "/" + n02QName + "/" + n14QNameCommon,
                            "/" + n01QName + "/" + n05QName + "/" + n14QNameCommon, "/" + n01QName + "/" + n06QName + "/" + n14QNameCommon,
                            "/" + n01QName + "/" + n05QName + "/" + n12QName + "/" + n14QNameCommon,
                            "/" + n01QName + "/" + n05QName + "/" + n12QName + "/" + n13QName + "/" + n14QNameCommon });

            // 15

            HashMap<QName, PropertyValue> properties15 = new HashMap<QName, PropertyValue>();
            properties15.putAll(getOrderProperties());
            HashMap<QName, String> content15 = new HashMap<QName, String>();
            content15.put(ContentModel.PROP_CONTENT, "          ");
            NodeRef n15NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());
            QName n15QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "fifteen");
            ChildAssociationRef n15CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n13NodeRef, n15QName, n15NodeRef, true, 0);
            addNode(core, dataModel, 1, 16, 1, ContentModel.TYPE_THUMBNAIL, null, properties15, content15, "system", new ChildAssociationRef[] { n15CAR }, new NodeRef[] {
                    rootNodeRef, n01NodeRef, n05NodeRef, n12NodeRef, n13NodeRef }, new String[] {
                    "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName + "/" + n13QName + "/" + n15QName,
                    "/" + n02QName.toString() + "/" + n13QNameLink + "/" + n14QName });

            // run tests

            checkRootNode(rsp, core, dataModel);
            checkPaths(rsp, core, dataModel);
            checkQNames(rsp, core, dataModel);
            checkPropertyTypes(rsp, core, dataModel, testDate, n01NodeRef.toString());
            checkType(rsp, core, dataModel);

            // remove core

            if (remove)
            {
                SolrCore done = coreContainer.remove(name);
                if (done != null)
                {
                    done.close();
                }

                deleteDirectory(newCore);
            }

        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (org.apache.lucene.queryParser.ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Map<QName, PropertyValue> getOrderProperties()
    {
        Map<QName, PropertyValue> testProperties = new HashMap<QName, PropertyValue>();
        testProperties.put(createdDate, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderDate)));
        testProperties.put(orderDouble, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderDoubleCount)));
        testProperties.put(orderFloat, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderFloatCount)));
        testProperties.put(orderLong, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderLongCount)));
        testProperties.put(orderInt, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderIntCount)));
        testProperties.put(orderText,
                new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, new String(new char[] { (char) ('a' + orderTextCount) }) + " cabbage")));

        MLTextPropertyValue mlTextPropVal = new MLTextPropertyValue();
        mlTextPropVal.addValue(Locale.ENGLISH, new String(new char[] { (char) ('a' + orderTextCount) }) + " banana");
        mlTextPropVal.addValue(Locale.FRENCH, new String(new char[] { (char) ('Z' - orderTextCount) }) + " banane");
        mlTextPropVal.addValue(Locale.CHINESE, new String(new char[] { (char) ('香' + orderTextCount) }) + " 香蕉");
        testProperties.put(orderMLText, mlTextPropVal);

        orderDate = Duration.subtract(orderDate, new Duration("P1D"));
        orderDoubleCount += 0.1d;
        orderFloatCount += 0.82f;
        orderLongCount += 299999999999999l;
        orderIntCount += 8576457;
        orderTextCount++;
        return testProperties;
    }

    /**
     * @param rsp
     * @return
     * @throws IOException
     * @throws org.apache.lucene.queryParser.ParseException
     */
    private void checkRootNode(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("RootNode", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/.\"", 1);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private void checkQNames(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("QNames", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "QNAME:\"nine\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PRIMARYASSOCTYPEQNAME:\"cm:contains\"", 11);
            testQuery(dataModel, report, solrIndexSearcher, "PRIMARYASSOCTYPEQNAME:\"sys:children\"", 4);
            testQuery(dataModel, report, solrIndexSearcher, "ASSOCTYPEQNAME:\"cm:contains\"", 11);
            testQuery(dataModel, report, solrIndexSearcher, "ASSOCTYPEQNAME:\"sys:children\"", 5);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private void checkType(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Type", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"" + testType.toString() + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"" + testType.toPrefixString(dataModel.getNamespaceDAO()) + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "EXACTTYPE:\"" + testType.toString() + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "EXACTTYPE:\"" + testType.toPrefixString(dataModel.getNamespaceDAO()) + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"" + testSuperType.toString() + "\"", 13);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"" + testSuperType.toPrefixString(dataModel.getNamespaceDAO()) + "\"", 13);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"" + ContentModel.TYPE_CONTENT.toString() + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"cm:content\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"cm:CONTENT\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"CM:CONTENT\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"CONTENT\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"content\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"" + ContentModel.TYPE_THUMBNAIL.toString() + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"" + ContentModel.TYPE_THUMBNAIL.toString() + "\" TYPE:\"" + ContentModel.TYPE_CONTENT.toString() + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "EXACTTYPE:\"" + testSuperType.toString() + "\"", 12);
            testQuery(dataModel, report, solrIndexSearcher, "EXACTTYPE:\"" + testSuperType.toPrefixString(dataModel.getNamespaceDAO()) + "\"", 12);
            testQuery(dataModel, report, solrIndexSearcher, "ASPECT:\"" + testAspect.toString() + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "ASPECT:\"" + testAspect.toPrefixString(dataModel.getNamespaceDAO()) + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "EXACTASPECT:\"" + testAspect.toString() + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "EXACTASPECT:\"" + testAspect.toPrefixString(dataModel.getNamespaceDAO()) + "\"", 1);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private void checkPropertyTypes(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, Date testDate, String n01NodeRef) throws IOException,
            org.apache.lucene.queryParser.ParseException
    {
        StoreRef storeRef = new StoreRef("workspace", "SpacesStore");

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("PropertyTypes", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            QName qname = QName.createQName(TEST_NAMESPACE, "int-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"1\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":1", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"01\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":01", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"001\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"0001\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[A TO 2]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[0 TO 2]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[0 TO A]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{A TO 1}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{0 TO 1}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{0 TO A}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{A TO 2}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{1 TO 2}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{1 TO A}", 0);

            qname = QName.createQName(TEST_NAMESPACE, "long-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"2\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"02\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"002\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"0002\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[A TO 2]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[0 TO 2]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[0 TO A]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{A TO 2}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{0 TO 2}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{0 TO A}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{A TO 3}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{2 TO 3}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{2 TO A}", 0);

            qname = QName.createQName(TEST_NAMESPACE, "float-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"3.4\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[A TO 4]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[3 TO 4]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[3 TO A]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[A TO 3.4]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[3.3 TO 3.4]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[3.3 TO A]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{A TO 3.4}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{3.3 TO 3.4}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{3.3 TO A}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"3.40\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"03.4\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"03.40\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "double-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"5.6\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"05.6\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"5.60\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"05.60\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[A TO 5.7]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[5.5 TO 5.7]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":[5.5 TO A]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{A TO 5.6}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{5.5 TO 5.6}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{5.5 TO A}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{A TO 5.7}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{5.6 TO 5.7}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":{5.6 TO A}", 0);

            Date date = new Date();
            for (SimpleDateFormatAndResolution df : CachingDateFormat.getLenientFormatters())
            {
                if (df.getResolution() < Calendar.DAY_OF_MONTH)
                {
                    continue;
                }

                String sDate = df.getSimpleDateFormat().format(testDate);

                if (sDate.length() >= 9)
                {
                    testQuery(dataModel, report, solrIndexSearcher, "\\@"
                            + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "date-ista").toString()) + ":\"" + sDate + "\"", 1);
                }
                testQuery(dataModel, report, solrIndexSearcher, "\\@"
                        + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":\"" + sDate + "\"", 1);

                sDate = df.getSimpleDateFormat().format(date);
                testQuery(dataModel, report, solrIndexSearcher, "\\@cm\\:CrEaTeD:[MIN TO " + sDate + "]", 1);
                testQuery(dataModel, report, solrIndexSearcher, "\\@cm\\:created:[MIN TO NOW]", 1);
                testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(ContentModel.PROP_CREATED.toString()) + ":[MIN TO " + sDate + "]", 1);

                if (sDate.length() >= 9)
                {
                    sDate = df.getSimpleDateFormat().format(testDate);
                    testQuery(dataModel, report, solrIndexSearcher, "\\@"
                            + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "date-ista").toString()) + ":[" + sDate + " TO " + sDate + "]", 1);
                    testQuery(dataModel, report, solrIndexSearcher, "\\@"
                            + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "date-ista").toString()) + ":[MIN  TO " + sDate + "]", 1);
                    testQuery(dataModel, report, solrIndexSearcher, "\\@"
                            + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "date-ista").toString()) + ":[" + sDate + " TO MAX]", 1);
                }

                sDate = CachingDateFormat.getDateFormat().format(testDate);
                testQuery(dataModel, report, solrIndexSearcher, "\\@"
                        + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":[MIN TO " + sDate + "]", 1);

                sDate = df.getSimpleDateFormat().format(testDate);
                for (long i : new long[] { 333, 20000, 20 * 60 * 1000, 8 * 60 * 60 * 1000, 10 * 24 * 60 * 60 * 1000, 4 * 30 * 24 * 60 * 60 * 1000,
                        10 * 12 * 30 * 24 * 60 * 60 * 1000 })
                {
                    String startDate = df.getSimpleDateFormat().format(new Date(testDate.getTime() - i));
                    String endDate = df.getSimpleDateFormat().format(new Date(testDate.getTime() + i));

                    testQuery(dataModel, report, solrIndexSearcher, "\\@"
                            + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":[" + startDate + " TO " + endDate + "]", 1);
                    testQuery(dataModel, report, solrIndexSearcher, "\\@"
                            + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":[" + sDate + " TO " + endDate + "]", 1);
                    testQuery(dataModel, report, solrIndexSearcher, "\\@"
                            + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":[" + startDate + " TO " + sDate + "]", 1);
                    testQuery(dataModel, report, solrIndexSearcher, "\\@"
                            + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":{" + sDate + " TO " + endDate + "}", 0);
                    testQuery(dataModel, report, solrIndexSearcher, "\\@"
                            + SolrQueryParser.escape(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":{" + startDate + " TO " + sDate + "}", 0);

                }
            }

            qname = QName.createQName(TEST_NAMESPACE, "boolean-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"true\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "qname-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"{wibble}wobble\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "category-ista");
            testQuery(
                    dataModel,
                    report,
                    solrIndexSearcher,
                    "\\@"
                            + SolrQueryParser.escape(qname.toString()) + ":\""
                            + DefaultTypeConverter.INSTANCE.convert(String.class, new NodeRef(new StoreRef("proto", "id"), "CategoryId")) + "\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "noderef-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"" + n01NodeRef + "\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "path-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"/{" + NamespaceService.CONTENT_MODEL_1_0_URI + "}three\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "any-many-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"100\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SolrQueryParser.escape(qname.toString()) + ":\"anyValueAsString\"", 1);

            //

            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"Tutorial Alfresco\"~0", 0);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"Tutorial Alfresco\"~1", 0);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"Tutorial Alfresco\"~2", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"Tutorial Alfresco\"~3", 1);

            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Alfresco Tutorial\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Tutorial Alfresco\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Tutorial Alfresco\"~0", 0);
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Tutorial Alfresco\"~1", 0);
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Tutorial Alfresco\"~2", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Tutorial Alfresco\"~3", 1);

            qname = QName.createQName(TEST_NAMESPACE, "mltext-many-ista");
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":лемур", 1, (new Locale("ru")));
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":lemur", 1, (new Locale("en")));
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":chou", 1, (new Locale("fr")));
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":cabbage", 1, (new Locale("en")));
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":cabba*", 1, (new Locale("en")));
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":ca*ge", 1, (new Locale("en")));
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":*bage", 1, (new Locale("en")));
            // testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) +
            // ":cabage~", 1, (new Locale("en")));
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":*b?ag?", 1, (new Locale("en")));
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":cho*", 1, (new Locale("fr")));

            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(QName.createQName(TEST_NAMESPACE, "content-many-ista").toString()) + ":multicontent", 1,
                    (new Locale("fr")));

            qname = QName.createQName(TEST_NAMESPACE, "locale-ista");
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":\"en_GB_\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":en_GB_", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":en_*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":*_GB_*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":*_gb_*", 1);

            qname = QName.createQName(TEST_NAMESPACE, "period-ista");
            testQuery(dataModel, report, solrIndexSearcher, "@" + LuceneQueryParser.escape(qname.toString()) + ":\"period|12\"", 1);

        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private String checkPaths(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Paths", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:two\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:three\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:four\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:eight-0\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:five\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:one\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:two\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:two/cm:one\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:two/cm:two\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:five\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:six\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:two/cm:seven\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:eight-1\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:two/cm:eight-2\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:eight-2\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:two/cm:eight-1\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:two/cm:eight-0\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:eight-0\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:five/cm:nine\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:five/cm:ten\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:five/cm:eleven\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:five/cm:twelve\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:five/cm:twelve/cm:thirteen\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:five/cm:twelve/cm:thirteen/cm:fourteen\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:five/cm:twelve/cm:thirteen/cm:common\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:five/cm:twelve/cm:common\"", 1);

            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:*\"", 5);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:*/cm:*\"", 6);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:*/cm:five\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:*/cm:*/cm:*\"", 6);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:*\"", 4);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:*/cm:five/cm:*\"", 5);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/cm:*/cm:nine\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/*\"", 5);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/*/*\"", 6);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/*/cm:five\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/*/*/*\"", 6);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/*\"", 4);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/*/cm:five/*\"", 5);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:one/*/cm:nine\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"//.\"", 16);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"//*\"", 15);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"//*/.\"", 15);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"//*/./.\"", 15);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"//./*\"", 15);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"//././*/././.\"", 15);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"//cm:common\"", 1);

            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/one//common\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/one/five//*\"", 7);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/one/five//.\"", 8);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/one//five/nine\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/one//thirteen/fourteen\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/one//thirteen/fourteen/.\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/one//thirteen/fourteen//.\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/one//thirteen/fourteen//.//.\"", 1);

            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/one\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/two\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/three\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/four\"", 1);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
        return "PASSED";
    }

    /**
     * @param dataModel
     * @param report
     * @param solrIndexSearcher
     * @throws ParseException
     * @throws IOException
     */
    private void testQuery(AlfrescoSolrDataModel dataModel, NamedList<Object> report, SolrIndexSearcher solrIndexSearcher, String queryString, int count, Locale locale)
            throws org.apache.lucene.queryParser.ParseException, IOException
    {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setQuery(queryString);
        if (locale != null)
        {
            searchParameters.addLocale(locale);
        }
        // Query query = dataModel.getFTSQuery(searchParameters, solrIndexSearcher.getIndexReader());
        Query query = dataModel.getLuceneQueryParser(searchParameters, solrIndexSearcher.getIndexReader()).parse(queryString);
        TopDocs docs = solrIndexSearcher.search(query, count * 2 + 10);
        if (docs.totalHits != count)
        {
            report.add("Failed: " + queryString, docs.totalHits);
        }
        else
        {
            report.add("Passed: " + queryString, docs.totalHits);
        }
    }

    private void testQuery(AlfrescoSolrDataModel dataModel, NamedList<Object> report, SolrIndexSearcher solrIndexSearcher, String queryString, int count)
            throws org.apache.lucene.queryParser.ParseException, IOException
    {
        testQuery(dataModel, report, solrIndexSearcher, queryString, count, null);
    }

    private NodeRef addNode(SolrCore core, AlfrescoSolrDataModel dataModel, int txid, int dbid, int aclid, QName type, QName[] aspects, Map<QName, PropertyValue> properties,
            Map<QName, String> content, String owner, ChildAssociationRef[] parentAssocs, NodeRef[] ancestors, String[] paths) throws IOException
    {
        NodeRef nodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), GUID.generate());

        AddUpdateCommand leafDocCmd = new AddUpdateCommand();
        leafDocCmd.overwriteCommitted = true;
        leafDocCmd.overwritePending = true;
        leafDocCmd.solrDoc = createLeafDocument(dataModel, txid, dbid, nodeRef, type, aspects,
                properties, content);
        leafDocCmd.doc = CoreTracker.toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

        AddUpdateCommand auxDocCmd = new AddUpdateCommand();
        auxDocCmd.overwriteCommitted = true;
        auxDocCmd.overwritePending = true;
        auxDocCmd.solrDoc = createAuxDocument(txid, dbid, aclid, paths, owner, parentAssocs, ancestors);
        auxDocCmd.doc = CoreTracker.toDocument(auxDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

        if (leafDocCmd.doc != null)
        {
            core.getUpdateHandler().addDoc(leafDocCmd);
        }
        if (auxDocCmd.doc != null)
        {
            core.getUpdateHandler().addDoc(auxDocCmd);
        }

        core.getUpdateHandler().commit(new CommitUpdateCommand(false));

        return nodeRef;
    }

    /**
     * @param i
     * @param j
     * @throws IOException
     */
    private void addStoreRoot(SolrCore core, AlfrescoSolrDataModel dataModel, NodeRef rootNodeRef, int txid, int dbid, int acltxid, int aclid) throws IOException
    {
        AddUpdateCommand leafDocCmd = new AddUpdateCommand();
        leafDocCmd.overwriteCommitted = true;
        leafDocCmd.overwritePending = true;
        leafDocCmd.solrDoc = createLeafDocument(dataModel, txid, dbid, rootNodeRef, ContentModel.TYPE_STOREROOT, new QName[]{ContentModel.ASPECT_ROOT}, null,
                null);
        leafDocCmd.doc = CoreTracker.toDocument(leafDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

        AddUpdateCommand auxDocCmd = new AddUpdateCommand();
        auxDocCmd.overwriteCommitted = true;
        auxDocCmd.overwritePending = true;
        auxDocCmd.solrDoc = createAuxDocument(txid, dbid, aclid, new String[] { "/" }, "system", null, null);
        auxDocCmd.doc = CoreTracker.toDocument(auxDocCmd.getSolrInputDocument(), core.getSchema(), dataModel);

        if (leafDocCmd.doc != null)
        {
            core.getUpdateHandler().addDoc(leafDocCmd);
        }
        if (auxDocCmd.doc != null)
        {
            core.getUpdateHandler().addDoc(auxDocCmd);
        }

        AddUpdateCommand aclTxCmd = new AddUpdateCommand();
        aclTxCmd.overwriteCommitted = true;
        aclTxCmd.overwritePending = true;
        SolrInputDocument aclTxSol = new SolrInputDocument();
        aclTxSol.addField(AbstractLuceneQueryParser.FIELD_ID, "ACLTX-" + acltxid);
        aclTxSol.addField(AbstractLuceneQueryParser.FIELD_ACLTXID, acltxid);
        aclTxSol.addField(AbstractLuceneQueryParser.FIELD_INACLTXID, acltxid);
        aclTxSol.addField(AbstractLuceneQueryParser.FIELD_ACLTXCOMMITTIME, (new Date()).getTime());
        aclTxCmd.solrDoc = aclTxSol;
        aclTxCmd.doc = CoreTracker.toDocument(aclTxCmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(aclTxCmd);

        AddUpdateCommand aclCmd = new AddUpdateCommand();
        aclCmd.overwriteCommitted = true;
        aclCmd.overwritePending = true;
        SolrInputDocument aclSol = new SolrInputDocument();
        aclSol.addField(AbstractLuceneQueryParser.FIELD_ID, "ACL-" + aclid);
        aclSol.addField(AbstractLuceneQueryParser.FIELD_ACLID, aclid);
        aclSol.addField(AbstractLuceneQueryParser.FIELD_INACLTXID, "" + acltxid);
        aclSol.addField(AbstractLuceneQueryParser.FIELD_READER, "GROUP_EVERYONE");

        aclCmd.solrDoc = aclSol;
        aclCmd.doc = CoreTracker.toDocument(aclCmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(aclCmd);

        core.getUpdateHandler().commit(new CommitUpdateCommand(false));
    }

    public SolrInputDocument createLeafDocument(AlfrescoSolrDataModel dataModel, int txid, int dbid, NodeRef nodeRef, QName type, QName[] aspects,
            Map<QName, PropertyValue> properties, Map<QName, String> content) throws IOException
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(AbstractLuceneQueryParser.FIELD_ID, "LEAF-" + dbid);
        doc.addField(AbstractLuceneQueryParser.FIELD_DBID, "" + dbid);
        doc.addField(AbstractLuceneQueryParser.FIELD_LID, nodeRef);
        doc.addField(AbstractLuceneQueryParser.FIELD_INTXID, "" + txid);

        if (properties != null)
        {
            for (QName propertyQname : properties.keySet())
            {
                if (dataModel.isIndexedOrStored(propertyQname))
                {
                    PropertyValue value = properties.get(propertyQname);
                    if (value != null)
                    {
                        if (value instanceof ContentPropertyValue)
                        {
                            addContentPropertyToDoc(doc, propertyQname, (ContentPropertyValue) value, content);

                        }
                        else if (value instanceof MLTextPropertyValue)
                        {
                            addMLTextPropertyToDoc(dataModel, doc, propertyQname, (MLTextPropertyValue) value);
                        }
                        else if (value instanceof MultiPropertyValue)
                        {
                            MultiPropertyValue typedValue = (MultiPropertyValue) value;
                            for (PropertyValue singleValue : typedValue.getValues())
                            {
                                if (singleValue instanceof ContentPropertyValue)
                                {
                                    addContentPropertyToDoc(doc, propertyQname, (ContentPropertyValue) singleValue, content);
                                }
                                else if (singleValue instanceof MLTextPropertyValue)
                                {
                                    addMLTextPropertyToDoc(dataModel, doc, propertyQname, (MLTextPropertyValue) singleValue);

                                }
                                else if (singleValue instanceof StringPropertyValue)
                                {
                                    addStringPropertyToDoc(dataModel, doc, propertyQname, (StringPropertyValue) singleValue, properties);
                                }
                            }
                        }
                        else if (value instanceof StringPropertyValue)
                        {
                            addStringPropertyToDoc(dataModel, doc, propertyQname, (StringPropertyValue) value, properties);
                        }

                    }
                }
            }
        }

        doc.addField(AbstractLuceneQueryParser.FIELD_TYPE, type);
        if (aspects != null)
        {
            for (QName aspect : aspects)
            {
                doc.addField(AbstractLuceneQueryParser.FIELD_ASPECT, aspect);
            }
        }
        doc.addField(AbstractLuceneQueryParser.FIELD_ISNODE, "T");
        doc.addField(AbstractLuceneQueryParser.FIELD_FTSSTATUS, "Clean");
        doc.addField(AbstractLuceneQueryParser.FIELD_TENANT, "_DEFAULT_");

        return doc;
    }

    private void addStringPropertyToDoc(AlfrescoSolrDataModel dataModel, SolrInputDocument doc, QName propertyQName, StringPropertyValue stringPropertyValue,
            Map<QName, PropertyValue> properties) throws IOException
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

    private void addContentPropertyToDoc(SolrInputDocument doc, QName propertyQName, ContentPropertyValue contentPropertyValue, Map<QName, String> content) throws IOException
    {
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".size", contentPropertyValue.getLength());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".locale", contentPropertyValue.getLocale());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".mimetype", contentPropertyValue.getMimetype());
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".encoding", contentPropertyValue.getEncoding());

        // doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() +
        // ".transformationStatus", response.getStatus());
        // doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() +
        // ".transformationTime", response.getTransformDuration());
        // doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() +
        // ".transformationException", response.getTransformException());

        String value = "";
        if (content != null)
        {
            value = content.get(propertyQName);
            if (value == null)
            {
                value = "";
            }
        }
        StringReader isr = new StringReader(value);
        StringBuilder builder = new StringBuilder();
        builder.append("\u0000").append(contentPropertyValue.getLocale().toString()).append("\u0000");
        StringReader prefix = new StringReader(builder.toString());
        Reader multiReader = new MultiReader(prefix, isr);
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString(), multiReader);

        isr = new StringReader(value);
        builder = new StringBuilder();
        builder.append("\u0000").append(contentPropertyValue.getLocale().toString()).append("\u0000");
        prefix = new StringReader(builder.toString());
        multiReader = new MultiReader(prefix, isr);
        doc.addField(AbstractLuceneQueryParser.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__", multiReader);

    }

    private void addMLTextPropertyToDoc(AlfrescoSolrDataModel dataModel, SolrInputDocument doc, QName propertyQName, MLTextPropertyValue mlTextPropertyValue) throws IOException
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

    private SolrInputDocument createAuxDocument(int txid, int dbid, int aclid, String[] paths, String owner, ChildAssociationRef[] parentAssocs, NodeRef[] ancestors)
    {
        SolrInputDocument aux = new SolrInputDocument();
        aux.addField(AbstractLuceneQueryParser.FIELD_ID, "AUX-" + dbid);
        aux.addField(AbstractLuceneQueryParser.FIELD_DBID, "" + dbid);
        aux.addField(AbstractLuceneQueryParser.FIELD_ACLID, "" + aclid);
        aux.addField(AbstractLuceneQueryParser.FIELD_INTXID, "" + txid);

        if (paths != null)
        {
            for (String path : paths)
            {
                aux.addField(AbstractLuceneQueryParser.FIELD_PATH, path);
            }
        }

        if (owner != null)
        {
            aux.addField(AbstractLuceneQueryParser.FIELD_OWNER, owner);
        }
        aux.addField(AbstractLuceneQueryParser.FIELD_PARENT_ASSOC_CRC, "0");

        StringBuilder qNameBuffer = new StringBuilder(64);
        StringBuilder assocTypeQNameBuffer = new StringBuilder(64);
        if (parentAssocs != null)
        {
            for (ChildAssociationRef childAssocRef : parentAssocs)
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
        if (ancestors != null)
        {
            for (NodeRef ancestor : ancestors)
            {
                aux.addField(AbstractLuceneQueryParser.FIELD_ANCESTOR, ancestor.toString());
            }
        }
        return aux;
    }

    public static SolrInputDocument createRootAclDocument()
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("ACLID", "1");
        doc.addField("READER", "ROLE_ALL");
        doc.addField("READER", "ROLE_JUST_ROOT");
        doc.addField("ID", "ACL-1");
        return doc;
    }

    /**
     * @param cname
     * @param detail
     * @param hist
     * @param values
     * @param tracker
     * @param report
     */
    private void addCoreSummary(String cname, boolean detail, boolean hist, boolean values, CoreTracker tracker, NamedList<Object> report)
    {
        NamedList<Object> coreSummary = new SimpleOrderedMap<Object>();
        long lastIndexTxCommitTime = tracker.getLastIndexedTxCommitTime();
        long lastIndexedTxId = tracker.getLastIndexedTxId();
        long lastTxCommitTimeOnServer = tracker.getLastTxCommitTimeOnServer();
        long lastTxIdOnServer = tracker.getLastTxIdOnServer();
        Date lastIndexTxCommitDate = new Date(lastIndexTxCommitTime);
        Date lastTxOnServerDate = new Date(lastTxCommitTimeOnServer);

        long lastIndexChangeSetCommitTime = tracker.getLastIndexedChangeSetCommitTime();
        long lastIndexedChangeSetId = tracker.getLastIndexedChangeSetId();
        long lastChangeSetCommitTimeOnServer = tracker.getLastChangeSetCommitTimeOnServer();
        long lastChangeSetIdOnServer = tracker.getLastChangeSetIdOnServer();
        Date lastIndexChangeSetCommitDate = new Date(lastIndexChangeSetCommitTime);
        Date lastChangeSetOnServerDate = new Date(lastChangeSetCommitTimeOnServer);

        long remainingTxTimeMillis = (long) ((lastTxIdOnServer - lastIndexedTxId) * tracker.getTrackerStats().getMeanDocsPerTx() * tracker.getTrackerStats().getMeanNodeIndexTime() / tracker
                .getTrackerStats().getNodeIndexingThreadCount());
        Date now = new Date();
        Date end = new Date(now.getTime() + remainingTxTimeMillis);
        Duration remainingTx = new Duration(now, end);

        long remainingChangeSetTimeMillis = (long) ((lastChangeSetIdOnServer - lastIndexedChangeSetId)
                * tracker.getTrackerStats().getMeanAclsPerChangeSet() * tracker.getTrackerStats().getMeanAclIndexTime() / tracker.getTrackerStats().getNodeIndexingThreadCount());
        now = new Date();
        end = new Date(now.getTime() + remainingChangeSetTimeMillis);
        Duration remainingChangeSet = new Duration(now, end);

        Duration txLag = new Duration(lastIndexTxCommitDate, lastTxOnServerDate);
        Duration changeSetLag = new Duration(lastIndexChangeSetCommitDate, lastChangeSetOnServerDate);

        coreSummary.add("Active", tracker.isRunning());

        // TX

        coreSummary.add("Last Index TX Commit Time", lastIndexTxCommitTime);
        coreSummary.add("Last Index TX Commit Date", lastIndexTxCommitDate);
        coreSummary.add("TX Lag", (lastTxCommitTimeOnServer - lastIndexTxCommitTime) / 1000 + " s");
        coreSummary.add("TX Duration", txLag.toString());
        coreSummary.add("Timestamp for last TX on server", lastTxCommitTimeOnServer);
        coreSummary.add("Date for last TX on server", lastTxOnServerDate);
        coreSummary.add("Id for last TX on server", lastTxIdOnServer);
        coreSummary.add("Id for last TX in index", lastIndexedTxId);
        coreSummary.add("Approx transactions remaining", lastTxIdOnServer - lastIndexedTxId);
        coreSummary.add("Approx transaction indexing time remaining", remainingTx.largestComponentformattedString());

        // Change set

        coreSummary.add("Last Index Change Set Commit Time", lastIndexChangeSetCommitTime);
        coreSummary.add("Last Index Change Set Commit Date", lastIndexChangeSetCommitDate);
        coreSummary.add("Change Set Lag", (lastChangeSetCommitTimeOnServer - lastIndexChangeSetCommitTime) / 1000 + " s");
        coreSummary.add("Change Set Duration", changeSetLag.toString());
        coreSummary.add("Timestamp for last Change Set on server", lastChangeSetCommitTimeOnServer);
        coreSummary.add("Date for last Change Set on server", lastChangeSetOnServerDate);
        coreSummary.add("Id for last Change Set on server", lastChangeSetIdOnServer);
        coreSummary.add("Id for last Change Set in index", lastIndexedChangeSetId);
        coreSummary.add("Approx change sets remaining", lastChangeSetIdOnServer - lastIndexedChangeSetId);
        coreSummary.add("Approx change set indexing time remaining", remainingChangeSet.largestComponentformattedString());

        // Stats

        coreSummary.add("Model sync times (ms)", tracker.getTrackerStats().getModelTimes().getNamedList(detail, hist, values));
        coreSummary.add("Acl index time (ms)", tracker.getTrackerStats().getAclTimes().getNamedList(detail, hist, values));
        coreSummary.add("Node index time (ms)", tracker.getTrackerStats().getNodeTimes().getNamedList(detail, hist, values));
        coreSummary.add("Docs/Tx", tracker.getTrackerStats().getTxDocs().getNamedList(detail, hist, values));
        coreSummary.add("Doc Transformation time (ms)", tracker.getTrackerStats().getDocTransformationTimes().getNamedList(detail, hist, values));

        report.add(cname, coreSummary);
    }

    private NamedList<Object> buildAclTxReport(CoreTracker tracker, Long acltxid) throws AuthenticationException, IOException, JSONException
    {
        NamedList<Object> nr = new SimpleOrderedMap<Object>();
        nr.add("TXID", acltxid);
        nr.add("transaction", buildTrackerReport(tracker, 0l, 0l, acltxid, acltxid, null, null));
        NamedList<Object> nodes = new SimpleOrderedMap<Object>();
        // add node reports ....
        List<Long> dbAclIds = tracker.getAclsForDbAclTransaction(acltxid);
        for (Long aclid : dbAclIds)
        {
            nodes.add("ACLID " + aclid, buildAclReport(tracker, aclid));
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
        for (Node node : dbNodes)
        {
            nodes.add("DBID " + node.getId(), buildNodeReport(tracker, node));
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

    /**
     * Note files can alter due to background processes so file not found is Ok
     * 
     * @param srcDir
     * @param destDir
     * @param preserveFileDate
     * @throws IOException
     */
    private void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException
    {
        if (destDir.exists())
        {
            throw new IOException("Destination should be created from clean");
        }
        else
        {
            if (!destDir.mkdirs())
            {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
            if (preserveFileDate)
            {
                // OL if file not found so does not need to check
                destDir.setLastModified(srcDir.lastModified());
            }
        }
        if (!destDir.canWrite())
        {
            throw new IOException("No acces to destination directory" + destDir);
        }

        File[] files = srcDir.listFiles();
        if (files != null)
        {
            for (int i = 0; i < files.length; i++)
            {
                File currentCopyTarget = new File(destDir, files[i].getName());
                if (files[i].isDirectory())
                {
                    copyDirectory(files[i], currentCopyTarget, preserveFileDate);
                }
                else
                {
                    copyFile(files[i], currentCopyTarget, preserveFileDate);
                }
            }
        }
    }

    private void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException
    {
        try
        {
            if (destFile.exists())
            {
                throw new IOException("File shoud not exist " + destFile);
            }

            FileInputStream input = new FileInputStream(srcFile);
            try
            {
                FileOutputStream output = new FileOutputStream(destFile);
                try
                {
                    copy(input, output);
                }
                finally
                {
                    try
                    {
                        output.close();
                    }
                    catch (IOException io)
                    {

                    }
                }
            }
            finally
            {
                try
                {
                    input.close();
                }
                catch (IOException io)
                {

                }
            }

            // check copy
            if (srcFile.length() != destFile.length())
            {
                throw new IOException("Failed to copy full from '" + srcFile + "' to '" + destFile + "'");
            }
            if (preserveFileDate)
            {
                destFile.setLastModified(srcFile.lastModified());
            }
        }
        catch (FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
    }

    public int copy(InputStream input, OutputStream output) throws IOException
    {
        byte[] buffer = new byte[2048 * 4];
        int count = 0;
        int n = 0;
        while ((n = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public void deleteDirectory(File directory) throws IOException
    {
        if (!directory.exists())
        {
            return;
        }
        if (!directory.isDirectory())
        {
            throw new IllegalArgumentException("Not a directory " + directory);
        }

        File[] files = directory.listFiles();
        if (files == null)
        {
            throw new IOException("Failed to delete director - no access" + directory);
        }

        for (int i = 0; i < files.length; i++)
        {
            File file = files[i];

            if (file.isDirectory())
            {
                deleteDirectory(file);
            }
            else
            {
                if (!file.delete())
                {
                    throw new IOException("Unable to delete file: " + file);
                }
            }
        }

        if (!directory.delete())
        {
            throw new IOException("Unable to delete directory " + directory);
        }
    }

}
