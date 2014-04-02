/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
import java.io.StringReader;
import java.lang.reflect.Method;
import java.text.Collator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.MultiReader;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
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
import org.alfresco.util.CachingDateFormat.SimpleDateFormatAndResolution;
import org.alfresco.util.GUID;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.ISO9075;
import org.alfresco.util.Pair;
import org.alfresco.util.SearchLanguageConversion;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.handler.admin.CoreAdminHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryRequestBase;
import org.apache.solr.request.SolrQueryResponse;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSlice;
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
    private static final String CMIS_TEST_NAMESPACE = "http://www.alfresco.org/test/cmis-query-test";

    QName typeThatRequiresEncoding = QName.createQName(CMIS_TEST_NAMESPACE, "type-that-requires-encoding");

    QName aspectThatRequiresEncoding = QName.createQName(CMIS_TEST_NAMESPACE, "aspect-that-requires-encoding");

    QName propertyThatRequiresEncoding = QName.createQName(CMIS_TEST_NAMESPACE, "property-that-requires-encoding");

    QName extendedContent = QName.createQName(CMIS_TEST_NAMESPACE, "extendedContent");

    QName singleTextBoth = QName.createQName(CMIS_TEST_NAMESPACE, "singleTextBoth");

    QName singleTextUntokenised = QName.createQName(CMIS_TEST_NAMESPACE, "singleTextUntokenised");

    QName singleTextTokenised = QName.createQName(CMIS_TEST_NAMESPACE, "singleTextTokenised");

    QName multipleTextBoth = QName.createQName(CMIS_TEST_NAMESPACE, "multipleTextBoth");

    QName multipleTextUntokenised = QName.createQName(CMIS_TEST_NAMESPACE, "multipleTextUntokenised");

    QName multipleTextTokenised = QName.createQName(CMIS_TEST_NAMESPACE, "multipleTextTokenised");

    QName singleMLTextBoth = QName.createQName(CMIS_TEST_NAMESPACE, "singleMLTextBoth");

    QName singleMLTextUntokenised = QName.createQName(CMIS_TEST_NAMESPACE, "singleMLTextUntokenised");

    QName singleMLTextTokenised = QName.createQName(CMIS_TEST_NAMESPACE, "singleMLTextTokenised");

    QName multipleMLTextBoth = QName.createQName(CMIS_TEST_NAMESPACE, "multipleMLTextBoth");

    QName multipleMLTextUntokenised = QName.createQName(CMIS_TEST_NAMESPACE, "multipleMLTextUntokenised");

    QName multipleMLTextTokenised = QName.createQName(CMIS_TEST_NAMESPACE, "multipleMLTextTokenised");

    QName singleFloat = QName.createQName(CMIS_TEST_NAMESPACE, "singleFloat");

    QName multipleFloat = QName.createQName(CMIS_TEST_NAMESPACE, "multipleFloat");

    QName singleDouble = QName.createQName(CMIS_TEST_NAMESPACE, "singleDouble");

    QName multipleDouble = QName.createQName(CMIS_TEST_NAMESPACE, "multipleDouble");

    QName singleInteger = QName.createQName(CMIS_TEST_NAMESPACE, "singleInteger");

    QName multipleInteger = QName.createQName(CMIS_TEST_NAMESPACE, "multipleInteger");

    QName singleLong = QName.createQName(CMIS_TEST_NAMESPACE, "singleLong");

    QName multipleLong = QName.createQName(CMIS_TEST_NAMESPACE, "multipleLong");

    QName singleBoolean = QName.createQName(CMIS_TEST_NAMESPACE, "singleBoolean");

    QName multipleBoolean = QName.createQName(CMIS_TEST_NAMESPACE, "multipleBoolean");

    QName singleDate = QName.createQName(CMIS_TEST_NAMESPACE, "singleDate");

    QName multipleDate = QName.createQName(CMIS_TEST_NAMESPACE, "multipleDate");

    QName singleDatetime = QName.createQName(CMIS_TEST_NAMESPACE, "singleDatetime");

    QName multipleDatetime = QName.createQName(CMIS_TEST_NAMESPACE, "multipleDatetime");

    ReentrantReadWriteLock guidLock = new ReentrantReadWriteLock();

    long guid = System.nanoTime();

    private String createGUID()
    {
        long time;
        guidLock.writeLock().lock();
        try
        {
            time = guid++;
        }
        finally
        {
            guidLock.writeLock().unlock();
        }

        return "00000000-0000-" + ((time / 1000000000000L) % 10000L) + "-" + ((time / 100000000L) % 10000L) + "-" + (time % 100000000L);
    }

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

    QName createdTime = QName.createQName(TEST_NAMESPACE, "createdTime");

    QName orderDouble = QName.createQName(TEST_NAMESPACE, "orderDouble");

    QName orderFloat = QName.createQName(TEST_NAMESPACE, "orderFloat");

    QName orderLong = QName.createQName(TEST_NAMESPACE, "orderLong");

    QName orderInt = QName.createQName(TEST_NAMESPACE, "orderInt");

    QName orderText = QName.createQName(TEST_NAMESPACE, "orderText");

    QName orderLocalisedText = QName.createQName(TEST_NAMESPACE, "orderLocalisedText");

    QName orderMLText = QName.createQName(TEST_NAMESPACE, "orderMLText");

    QName orderLocalisedMLText = QName.createQName(TEST_NAMESPACE, "orderLocalisedMLText");

    QName aspectWithChildren = QName.createQName(TEST_NAMESPACE, "aspectWithChildren");

    private QName testType = QName.createQName(TEST_NAMESPACE, "testType");

    private QName testSuperType = QName.createQName(TEST_NAMESPACE, "testSuperType");

    private QName testAspect = QName.createQName(TEST_NAMESPACE, "testAspect");

    // private QName testSuperAspect = QName.createQName(TEST_NAMESPACE, "testSuperAspect");

    protected final static Logger log = LoggerFactory.getLogger(AlfrescoCoreAdminHandler.class);

    Scheduler scheduler = null;

    ConcurrentHashMap<String, CoreTracker> trackers = new ConcurrentHashMap<String, CoreTracker>();

    private Date orderDate = new Date();

    private int orderTextCount = 0;

    private String[] orderNames = new String[] { "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen",
            "fifteen", "sixteen" };

    // Spanish- Eng, French-English, Swedish German, English
    private String[] orderLocalisedNames = new String[] { "chalina", "curioso", "llama", "luz", "peach", "péché", "pêche", "sin", "\u00e4pple", "banan", "p\u00e4ron", "orange",
            "rock", "rôle", "rose", "filler" };

    private String[] orderLocaliseMLText_de = new String[] { "Arg", "Ärgerlich", "Arm", "Assistent", "Aßlar", "Assoziation", "Udet", "Übelacker", "Uell", "Ülle", "Ueve", "Üxküll",
            "Uffenbach", "apple", "and", "aardvark" };

    private String[] orderLocaliseMLText_fr = new String[] { "cote", "côte", "coté", "côté", "rock", "lemur", "lemonade", "lemon", "kale", "guava", "cheese", "beans",
            "bananana", "apple", "and", "aardvark" };

    private String[] orderLocaliseMLText_en = new String[] { "zebra", "tiger", "rose", "rôle", "rock", "lemur", "lemonade", "lemon", "kale", "guava", "cheese", "beans",
            "bananana", "apple", "and", "aardvark" };

    private String[] orderLocaliseMLText_es = new String[] { "radio", "ráfaga", "rana", "rápido", "rastrillo", "arroz", "campo", "chihuahua", "ciudad", "limonada", "llaves",
            "luna", "bananana", "apple", "and", "aardvark" };

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
            e.printStackTrace();
        }
        
        initLogging(coreContainer);

    }


    private void initLogging(CoreContainer coreContainer) 
    {
        initResourceBasedLogging(coreContainer, "log4j.properties");   
        initResourceBasedLogging(coreContainer, "log4j-solr.properties");   
    }

    /**
     * @param solrResourceLoader
     */
    private void initResourceBasedLogging(CoreContainer coreContainer, String resource)
    {
        try
        {
            Class<?> clazz = Class.forName("org.apache.log4j.PropertyConfigurator");
            Method method = clazz.getMethod("configure", Properties.class);
            InputStream is = openResource(coreContainer, resource);
            Properties p = new Properties();
            p.load(is);
            method.invoke(null, p);
        }
        catch (ClassNotFoundException e)
        {
           return;
        }
        catch (Throwable e)
        {
            log.info("Failed to load "+resource, e);
        }
    }
    
    private InputStream openResource(CoreContainer coreContainer, String resource) {
        InputStream is=null;
        try {
            File f0 = new File(resource);
            File f = f0;
            if (!f.isAbsolute()) {
                // try $CWD/$configDir/$resource
                f = new File(coreContainer.getSolrHome() + resource);
            }
            if (f.isFile() && f.canRead()) {
                return new FileInputStream(f);
            } else if (f != f0) { // no success with $CWD/$configDir/$resource
                if (f0.isFile() && f0.canRead())
                    return new FileInputStream(f0);
            }
            // delegate to the class loader (looking into $INSTANCE_DIR/lib jars)
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        } catch (Exception e) {
            throw new RuntimeException("Error opening " + resource, e);
        }
        if (is==null) {
            throw new RuntimeException("Can't find resource '" + resource + "' in classpath or '" + coreContainer.getSolrHome() + "', cwd="+System.getProperty("user.dir"));
        }
        return is;
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
            else if (a.equalsIgnoreCase("AUTHTEST"))
            {
                runAuthTest(req, rsp);
                return false;
            }
            else if (a.equalsIgnoreCase("CMISTEST"))
            {
                runCmisTests(req, rsp);
                return false;
            }
            else if (a.equalsIgnoreCase("newCore"))
            {
                return newCore(req, rsp);
            }
            else if (a.equalsIgnoreCase("updateCore"))
            {
                return updateCore(req, rsp);
            }
            else if (a.equalsIgnoreCase("removeCore"))
            {
                return removeCore(req, rsp);
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
            else if (a.equalsIgnoreCase("RETRY"))
            {
                if (cname != null)
                {
                    CoreTracker tracker = trackers.get(cname);
                    Set<Long> errorDocIds = tracker.getErrorDocIds();
                    for(Long nodeid : errorDocIds)
                    {
                        tracker.addNodeToReindex(nodeid);
                    }
                    rsp.add(cname, errorDocIds);

                }
                else
                {
                    for (String coreName : trackers.keySet())
                    {
                        CoreTracker tracker = trackers.get(coreName);
                        Set<Long> errorDocIds = tracker.getErrorDocIds();
                        for(Long nodeid : errorDocIds)
                        {
                            tracker.addNodeToReindex(nodeid);
                        }
                        rsp.add(coreName, errorDocIds);
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
                            addCoreSummary(coreName, detail, hist, values, tracker, report);

                            if (reset)
                            {
                                tracker.getTrackerStats().reset();
                            }
                        }
                        else
                        {
                            report.add(coreName, "Core unknown");
                        }
                    }
                    rsp.add("Summary", report);
                }
                return false;
            }
            else if (a.equalsIgnoreCase("LOG4J"))
            {
                String resource = "log4j-solr.properties";
                if (params.get("resource") != null)
                {
                    resource = params.get("resource"); 
                }
                initResourceBasedLogging(coreContainer, resource);
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
        orderDate = new Date();

        orderTextCount = 0;

       
        
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

            // fix configuration properties

            File config = new File(newCore, "conf/solrcore.properties");
            Properties properties = new Properties();
            properties.load(new FileInputStream(config));
            properties.setProperty("data.dir.root", newCore.getCanonicalPath());
            properties.store(new FileOutputStream(config), null);

            // add core
            
            NamedList<Object> before = new SimpleOrderedMap<Object>();
            rsp.add("Before", before);

            CoreDescriptor dcore = new CoreDescriptor(coreContainer, name, newCore.toString());
            dcore.setCoreProperties(null);
            SolrCore core = coreContainer.create(dcore);
            coreContainer.register(name, core, false);
            before.add("core", core.getName());

            SolrResourceLoader loader = core.getSchema().getResourceLoader();
            String id = loader.getInstanceDir();
            AlfrescoSolrDataModel dataModel = AlfrescoSolrDataModel.getInstance(id);
            dataModel.setCMDefaultUri();
            // add data

            // Root

            NodeRef rootNodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            addStoreRoot(core, dataModel, rootNodeRef, 1, 1, 1, 1);
            before.add("StoreRoot", 1);

            // 1

            NodeRef n01NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n01QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "one");
            ChildAssociationRef n01CAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, n01QName, n01NodeRef, true, 0);
            addNode(core, dataModel, 1, 2, 1, testSuperType, null, getOrderProperties(), null, "andy", new ChildAssociationRef[] { n01CAR }, new NodeRef[] { rootNodeRef },
                    new String[] { "/" + n01QName.toString() }, n01NodeRef, true);

            // 2

            NodeRef n02NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n02QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "two");
            ChildAssociationRef n02CAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, n02QName, n02NodeRef, true, 0);
            addNode(core, dataModel, 1, 3, 1, testSuperType, null, getOrderProperties(), null, "bob", new ChildAssociationRef[] { n02CAR }, new NodeRef[] { rootNodeRef },
                    new String[] { "/" + n02QName.toString() }, n02NodeRef, true);

            // 3

            NodeRef n03NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n03QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "three");
            ChildAssociationRef n03CAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, n03QName, n03NodeRef, true, 0);
            addNode(core, dataModel, 1, 4, 1, testSuperType, null, getOrderProperties(), null, "cid", new ChildAssociationRef[] { n03CAR }, new NodeRef[] { rootNodeRef },
                    new String[] { "/" + n03QName.toString() }, n03NodeRef, true);

            // 4

            HashMap<QName, PropertyValue> properties04 = new HashMap<QName, PropertyValue>();
            HashMap<QName, String> content04 = new HashMap<QName, String>();
            properties04.putAll(getOrderProperties());
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
            list_2.addValue(new ContentPropertyValue(Locale.ENGLISH, 12L, "UTF-16", "text/plain"));
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

            NodeRef n04NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n04QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "four");
            ChildAssociationRef n04CAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, n04QName, n04NodeRef, true, 0);

            properties04.put(QName.createQName(TEST_NAMESPACE, "aspectProperty"), new StringPropertyValue(""));
            addNode(core, dataModel, 1, 5, 1, testType, new QName[] { testAspect }, properties04, content04, "dave", new ChildAssociationRef[] { n04CAR },
                    new NodeRef[] { rootNodeRef }, new String[] { "/" + n04QName.toString() }, n04NodeRef, true);

            // 5

            NodeRef n05NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n05QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "five");
            ChildAssociationRef n05CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n01NodeRef, n05QName, n05NodeRef, true, 0);
            addNode(core, dataModel, 1, 6, 1, testSuperType, null, getOrderProperties(), null, "eoin", new ChildAssociationRef[] { n05CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() }, n05NodeRef, true);

            // 6

            NodeRef n06NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n06QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "six");
            ChildAssociationRef n06CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n01NodeRef, n06QName, n06NodeRef, true, 0);
            addNode(core, dataModel, 1, 7, 1, testSuperType, null, getOrderProperties(), null, "fred", new ChildAssociationRef[] { n06CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n06QName.toString() }, n06NodeRef, true);

            // 7

            NodeRef n07NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n07QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "seven");
            ChildAssociationRef n07CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n02NodeRef, n07QName, n07NodeRef, true, 0);
            addNode(core, dataModel, 1, 8, 1, testSuperType, null, getOrderProperties(), null, "gail", new ChildAssociationRef[] { n07CAR }, new NodeRef[] { rootNodeRef,
                    n02NodeRef }, new String[] { "/" + n02QName.toString() + "/" + n07QName.toString() }, n07NodeRef, true);

            // 8

            NodeRef n08NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n08QName_0 = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "eight-0");
            QName n08QName_1 = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "eight-1");
            QName n08QName_2 = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "eight-2");
            ChildAssociationRef n08CAR_0 = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, n08QName_0, n08NodeRef, false, 2);
            ChildAssociationRef n08CAR_1 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n01NodeRef, n08QName_1, n08NodeRef, false, 1);
            ChildAssociationRef n08CAR_2 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n02NodeRef, n08QName_2, n08NodeRef, true, 0);

            addNode(core, dataModel, 1, 9, 1, testSuperType, null, getOrderProperties(), null, "hal", new ChildAssociationRef[] { n08CAR_0, n08CAR_1, n08CAR_2 }, new NodeRef[] {
                    rootNodeRef, rootNodeRef, n01NodeRef, rootNodeRef, n02NodeRef }, new String[] { "/" + n08QName_0, "/" + n01QName.toString() + "/" + n08QName_1.toString(),
                    "/" + n02QName.toString() + "/" + n08QName_2.toString() }, n08NodeRef, true);

            // 9

            NodeRef n09NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n09QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "nine");
            ChildAssociationRef n09CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n05NodeRef, n09QName, n09NodeRef, true, 0);
            addNode(core, dataModel, 1, 10, 1, testSuperType, null, getOrderProperties(), null, "ian", new ChildAssociationRef[] { n09CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n09QName }, n09NodeRef, true);

            // 10

            NodeRef n10NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n10QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "ten");
            ChildAssociationRef n10CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n05NodeRef, n10QName, n10NodeRef, true, 0);
            addNode(core, dataModel, 1, 11, 1, testSuperType, null, getOrderProperties(), null, "jake", new ChildAssociationRef[] { n10CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n10QName }, n10NodeRef, true);

            // 11

            NodeRef n11NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n11QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "eleven");
            ChildAssociationRef n11CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n05NodeRef, n11QName, n11NodeRef, true, 0);
            addNode(core, dataModel, 1, 12, 1, testSuperType, null, getOrderProperties(), null, "kara", new ChildAssociationRef[] { n11CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n11QName }, n11NodeRef, true);

            // 12

            NodeRef n12NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n12QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "twelve");
            ChildAssociationRef n12CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n05NodeRef, n12QName, n12NodeRef, true, 0);
            addNode(core, dataModel, 1, 13, 1, testSuperType, null, getOrderProperties(), null, "loon", new ChildAssociationRef[] { n12CAR }, new NodeRef[] { rootNodeRef,
                    n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName }, n12NodeRef, true);

            // 13

            NodeRef n13NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n13QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "thirteen");
            QName n13QNameLink = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "link");
            ChildAssociationRef n13CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n12NodeRef, n13QName, n13NodeRef, true, 0);
            ChildAssociationRef n13CARLink = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n02NodeRef, n13QName, n13NodeRef, false, 0);
            addNode(core, dataModel, 1, 14, 1, testSuperType, null, getOrderProperties(), null, "mike", new ChildAssociationRef[] { n13CAR, n13CARLink }, new NodeRef[] {
                    rootNodeRef, n01NodeRef, n05NodeRef, n12NodeRef, rootNodeRef, n02NodeRef }, new String[] {
                    "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName + "/" + n13QName, "/" + n02QName.toString() + "/" + n13QNameLink }, n13NodeRef, true);

            // 14

            HashMap<QName, PropertyValue> properties14 = new HashMap<QName, PropertyValue>();
            properties14.putAll(getOrderProperties());
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
                e.printStackTrace();
            }

            properties14.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 298L, "UTF-8", "text/plain"));
            content14.put(ContentModel.PROP_CONTENT,
                    "The quick brown fox jumped over the lazy dog and ate the Alfresco Tutorial, in pdf format, along with the following stop words;  a an and are"
                            + " as at be but by for if in into is it no not of on or such that the their then there these they this to was will with: "
                            + " and random charcters \u00E0\u00EA\u00EE\u00F0\u00F1\u00F6\u00FB\u00FF");
            properties14.put(ContentModel.PROP_DESCRIPTION, desc1);
            properties14.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, explicitCreatedDate)));
            properties14.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, explicitCreatedDate)));

            NodeRef n14NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n14QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "fourteen");
            QName n14QNameCommon = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "common");
            ChildAssociationRef n14CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n13NodeRef, n14QName, n14NodeRef, true, 0);
            ChildAssociationRef n14CAR_1 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n01NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            ChildAssociationRef n14CAR_2 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n02NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            ChildAssociationRef n14CAR_5 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n05NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            ChildAssociationRef n14CAR_6 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n06NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            ChildAssociationRef n14CAR_12 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n12NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            ChildAssociationRef n14CAR_13 = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n13NodeRef, n14QNameCommon, n14NodeRef, false, 0);
            addNode(core, dataModel, 1, 15, 1, ContentModel.TYPE_CONTENT, null, properties14, content14, "noodle", new ChildAssociationRef[] { n14CAR, n14CAR_1, n14CAR_2,
                    n14CAR_5, n14CAR_6, n14CAR_12, n14CAR_13 }, new NodeRef[] { rootNodeRef, n01NodeRef, n05NodeRef, n12NodeRef, n13NodeRef },
                    new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName + "/" + n13QName + "/" + n14QName,
                            "/" + n02QName.toString() + "/" + n13QNameLink + "/" + n14QName, "/" + n01QName + "/" + n14QNameCommon, "/" + n02QName + "/" + n14QNameCommon,
                            "/" + n01QName + "/" + n05QName + "/" + n14QNameCommon, "/" + n01QName + "/" + n06QName + "/" + n14QNameCommon,
                            "/" + n01QName + "/" + n05QName + "/" + n12QName + "/" + n14QNameCommon,
                            "/" + n01QName + "/" + n05QName + "/" + n12QName + "/" + n13QName + "/" + n14QNameCommon }, n14NodeRef, true);

            // 15

            HashMap<QName, PropertyValue> properties15 = new HashMap<QName, PropertyValue>();
            properties15.putAll(getOrderProperties());
            properties15.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, explicitCreatedDate)));
            HashMap<QName, String> content15 = new HashMap<QName, String>();
            content15.put(ContentModel.PROP_CONTENT, "          ");
            NodeRef n15NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName n15QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "fifteen");
            ChildAssociationRef n15CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, n13NodeRef, n15QName, n15NodeRef, true, 0);
            addNode(core, dataModel, 1, 16, 1, ContentModel.TYPE_THUMBNAIL, null, properties15, content15, "ood", new ChildAssociationRef[] { n15CAR }, new NodeRef[] {
                    rootNodeRef, n01NodeRef, n05NodeRef, n12NodeRef, n13NodeRef }, new String[] {
                    "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName + "/" + n13QName + "/" + n15QName,
                    "/" + n02QName.toString() + "/" + n13QNameLink + "/" + n14QName }, n15NodeRef, true);

            // run tests

            checkRootNode(before, core, dataModel);
            checkPaths(before, core, dataModel);
            checkQNames(before, core, dataModel);
            checkPropertyTypes(before, core, dataModel, testDate, n01NodeRef.toString());
            checkType(before, core, dataModel);
            checkText(before, core, dataModel);
            checkMLText(before, core, dataModel);
            checkAll(before, core, dataModel);
            checkDataType(before, core, dataModel);
            checkNullAndUnset(before, core, dataModel);
            checkNonField(before, core, dataModel);
            checkRanges(before, core, dataModel);
            checkInternalFields(before, core, dataModel, n01NodeRef.toString());
            checkAuthorityFilter(before, core, dataModel);
            checkPaging(before, core, dataModel);

            testSort(before, core, dataModel);

            //

            testAFTS(before, core, dataModel);
            testAFTSandSort(before, core, dataModel);
            testCMIS(before, core, dataModel);

            long start = System.nanoTime();
            for (int i = 0; i < 100; i++)
            {
                orderDate = new Date();
                orderTextCount = 0;
                addNode(core, dataModel, 1, 2, 1, testSuperType, null, getOrderProperties(), null, "andy", new ChildAssociationRef[] { n01CAR }, new NodeRef[] { rootNodeRef },
                        new String[] { "/" + n01QName.toString() }, n01NodeRef, true);
            }
            long end = System.nanoTime();
            rsp.add("Index rate (docs/s)", 100.0f / (end - start) * 1e9);

            for (int i = 0; i < 10; i++)
            {
                orderDate = new Date();
                orderTextCount = 0;

                addNode(core, dataModel, 1, 2, 1, testSuperType, null, getOrderProperties(), null, "andy", new ChildAssociationRef[] { n01CAR }, new NodeRef[] { rootNodeRef },
                        new String[] { "/" + n01QName.toString() }, n01NodeRef, true);
                addNode(core, dataModel, 1, 3, 1, testSuperType, null, getOrderProperties(), null, "bob", new ChildAssociationRef[] { n02CAR }, new NodeRef[] { rootNodeRef },
                        new String[] { "/" + n02QName.toString() }, n02NodeRef, true);
                addNode(core, dataModel, 1, 4, 1, testSuperType, null, getOrderProperties(), null, "cid", new ChildAssociationRef[] { n03CAR }, new NodeRef[] { rootNodeRef },
                        new String[] { "/" + n03QName.toString() }, n03NodeRef, true);
                properties04.putAll(getOrderProperties());
                addNode(core, dataModel, 1, 5, 1, testType, new QName[] { testAspect }, properties04, content04, "dave", new ChildAssociationRef[] { n04CAR },
                        new NodeRef[] { rootNodeRef }, new String[] { "/" + n04QName.toString() }, n04NodeRef, true);
                addNode(core, dataModel, 1, 6, 1, testSuperType, null, getOrderProperties(), null, "eoin", new ChildAssociationRef[] { n05CAR }, new NodeRef[] { rootNodeRef,
                        n01NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() }, n05NodeRef, true);
                addNode(core, dataModel, 1, 7, 1, testSuperType, null, getOrderProperties(), null, "fred", new ChildAssociationRef[] { n06CAR }, new NodeRef[] { rootNodeRef,
                        n01NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n06QName.toString() }, n06NodeRef, true);
                addNode(core, dataModel, 1, 8, 1, testSuperType, null, getOrderProperties(), null, "gail", new ChildAssociationRef[] { n07CAR }, new NodeRef[] { rootNodeRef,
                        n02NodeRef }, new String[] { "/" + n02QName.toString() + "/" + n07QName.toString() }, n07NodeRef, true);
                addNode(core, dataModel, 1, 9, 1, testSuperType, null, getOrderProperties(), null, "hal", new ChildAssociationRef[] { n08CAR_0, n08CAR_1, n08CAR_2 },
                        new NodeRef[] { rootNodeRef, rootNodeRef, n01NodeRef, rootNodeRef, n02NodeRef }, new String[] { "/" + n08QName_0,
                                "/" + n01QName.toString() + "/" + n08QName_1.toString(), "/" + n02QName.toString() + "/" + n08QName_2.toString() }, n08NodeRef, true);
                addNode(core, dataModel, 1, 10, 1, testSuperType, null, getOrderProperties(), null, "ian", new ChildAssociationRef[] { n09CAR }, new NodeRef[] { rootNodeRef,
                        n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n09QName }, n09NodeRef, true);
                addNode(core, dataModel, 1, 11, 1, testSuperType, null, getOrderProperties(), null, "jake", new ChildAssociationRef[] { n10CAR }, new NodeRef[] { rootNodeRef,
                        n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n10QName }, n10NodeRef, true);
                addNode(core, dataModel, 1, 12, 1, testSuperType, null, getOrderProperties(), null, "kara", new ChildAssociationRef[] { n11CAR }, new NodeRef[] { rootNodeRef,
                        n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n11QName }, n11NodeRef, true);
                addNode(core, dataModel, 1, 13, 1, testSuperType, null, getOrderProperties(), null, "loon", new ChildAssociationRef[] { n12CAR }, new NodeRef[] { rootNodeRef,
                        n01NodeRef, n05NodeRef }, new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName }, n12NodeRef, true);
                addNode(core, dataModel, 1, 14, 1, testSuperType, null, getOrderProperties(), null, "mike", new ChildAssociationRef[] { n13CAR, n13CARLink }, new NodeRef[] {
                        rootNodeRef, n01NodeRef, n05NodeRef, n12NodeRef, rootNodeRef, n02NodeRef }, new String[] {
                        "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName + "/" + n13QName, "/" + n02QName.toString() + "/" + n13QNameLink }, n13NodeRef, true);
                properties14.putAll(getOrderProperties());
                addNode(core, dataModel, 1, 15, 1, ContentModel.TYPE_CONTENT, null, properties14, content14, "noodle", new ChildAssociationRef[] { n14CAR, n14CAR_1, n14CAR_2,
                        n14CAR_5, n14CAR_6, n14CAR_12, n14CAR_13 }, new NodeRef[] { rootNodeRef, n01NodeRef, n05NodeRef, n12NodeRef, n13NodeRef },
                        new String[] { "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName + "/" + n13QName + "/" + n14QName,
                                "/" + n02QName.toString() + "/" + n13QNameLink + "/" + n14QName, "/" + n01QName + "/" + n14QNameCommon, "/" + n02QName + "/" + n14QNameCommon,
                                "/" + n01QName + "/" + n05QName + "/" + n14QNameCommon, "/" + n01QName + "/" + n06QName + "/" + n14QNameCommon,
                                "/" + n01QName + "/" + n05QName + "/" + n12QName + "/" + n14QNameCommon,
                                "/" + n01QName + "/" + n05QName + "/" + n12QName + "/" + n13QName + "/" + n14QNameCommon }, n14NodeRef, true);
                properties14.putAll(getOrderProperties());
                addNode(core, dataModel, 1, 16, 1, ContentModel.TYPE_THUMBNAIL, null, properties15, content15, "ood", new ChildAssociationRef[] { n15CAR }, new NodeRef[] {
                        rootNodeRef, n01NodeRef, n05NodeRef, n12NodeRef, n13NodeRef }, new String[] {
                        "/" + n01QName.toString() + "/" + n05QName.toString() + "/" + n12QName + "/" + n13QName + "/" + n15QName,
                        "/" + n02QName.toString() + "/" + n13QNameLink + "/" + n14QName }, n15NodeRef, true);
            }

            NamedList<Object> after = new SimpleOrderedMap<Object>();
            rsp.add("After", after);
            
            checkRootNode(after, core, dataModel);
            checkPaths(after, core, dataModel);
            checkQNames(after, core, dataModel);
            checkPropertyTypes(after, core, dataModel, testDate, n01NodeRef.toString());
            checkType(after, core, dataModel);
            checkText(after, core, dataModel);
            checkMLText(after, core, dataModel);
            checkAll(after, core, dataModel);
            checkDataType(after, core, dataModel);
            checkNullAndUnset(after, core, dataModel);
            checkNonField(after, core, dataModel);
            checkRanges(after, core, dataModel);

            testSort(after, core, dataModel);

            //

            testAFTS(after, core, dataModel);
            testAFTSandSort(after, core, dataModel);
            testCMIS(after, core, dataModel);

            //

            testChildNameEscaping(after, core, dataModel, rootNodeRef);

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
            e.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (org.apache.lucene.queryParser.ParseException e)
        {
            e.printStackTrace();
        }

    }

    private boolean newCore(SolrQueryRequest req, SolrQueryResponse rsp)
    {
        try
        {
            String store = "";
            SolrParams params = req.getParams();
            if (params.get("storeRef") != null)
            {
                store = params.get("storeRef");
            }

            if ((store == null) || (store.length() == 0))
            {
                return false;
            }

            String templateName = "store";
            if (params.get("template") != null)
            {
                templateName = params.get("template");
            }

            StoreRef storeRef = new StoreRef(store);
            String coreName = storeRef.getProtocol() + "-" + storeRef.getIdentifier();
            if (params.get("coreName") != null)
            {
                coreName = params.get("coreName");
            }

            // copy core from template

            File solrHome = new File(getCoreContainer().getSolrHome());
            File templates = new File(solrHome, "templates");
            File template = new File(templates, templateName);

            File newCore = new File(solrHome, coreName);

            copyDirectory(template, newCore, false);

            // fix configuration properties

            File config = new File(newCore, "conf/solrcore.properties");
            Properties properties = new Properties();
            properties.load(new FileInputStream(config));
            properties.setProperty("data.dir.root", newCore.getCanonicalPath());
            properties.setProperty("data.dir.store", coreName);
            properties.setProperty("alfresco.stores", store);

            for (Iterator<String> it = params.getParameterNamesIterator(); it.hasNext(); /**/)
            {
                String paramName = it.next();
                if (paramName.startsWith("property."))
                {
                    properties.setProperty(paramName.substring("property.".length()), params.get(paramName));
                }
            }

            properties.store(new FileOutputStream(config), null);

            // add core

            CoreDescriptor dcore = new CoreDescriptor(coreContainer, coreName, newCore.toString());
            dcore.setCoreProperties(null);
            SolrCore core = coreContainer.create(dcore);
            coreContainer.register(coreName, core, false);
            rsp.add("core", core.getName());

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (ParserConfigurationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        catch (SAXException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateCore(SolrQueryRequest req, SolrQueryResponse rsp)
    {
        try
        {
            String store = "";
            SolrParams params = req.getParams();
            if (params.get("storeRef") != null)
            {
                store = params.get("storeRef");
            }

            if ((store == null) || (store.length() == 0))
            {
                return false;
            }

            StoreRef storeRef = new StoreRef(store);
            String coreName = storeRef.getProtocol() + "-" + storeRef.getIdentifier();
            if (params.get("coreName") != null)
            {
                coreName = params.get("coreName");
            }

            File solrHome = new File(getCoreContainer().getSolrHome());

            File newCore = new File(solrHome, coreName);

            // fix configuration properties

            File config = new File(newCore, "conf/solrcore.properties");
            Properties properties = new Properties();
            properties.load(new FileInputStream(config));

            for (Iterator<String> it = params.getParameterNamesIterator(); it.hasNext(); /**/)
            {
                String paramName = it.next();
                if (paramName.startsWith("property."))
                {
                    properties.setProperty(paramName.substring("property.".length()), params.get(paramName));
                }
            }

            properties.store(new FileOutputStream(config), null);

            coreContainer.reload(coreName);

            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (ParserConfigurationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        catch (SAXException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    private boolean removeCore(SolrQueryRequest req, SolrQueryResponse rsp)
    {
        try
        {
            String store = "";
            SolrParams params = req.getParams();
            if (params.get("storeRef") != null)
            {
                store = params.get("storeRef");
            }

            if ((store == null) || (store.length() == 0))
            {
                return false;
            }

            StoreRef storeRef = new StoreRef(store);
            String coreName = storeRef.getProtocol() + "-" + storeRef.getIdentifier();
            if (params.get("coreName") != null)
            {
                coreName = params.get("coreName");
            }

            File solrHome = new File(getCoreContainer().getSolrHome());
            File newCore = new File(solrHome, coreName);

            // remove core

            SolrCore done = coreContainer.remove(coreName);
            if (done != null)
            {
                done.close();
            }

            deleteDirectory(newCore);

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param req
     * @param rsp
     */
    @SuppressWarnings("unused")
    private void runAuthTest(SolrQueryRequest req, SolrQueryResponse rsp)
    {

        try
        {
            boolean remove = true;
            boolean reuse = true;
            long count = 100L;
            long maxReader = 1000;
            SolrParams params = req.getParams();
            if (params.get("remove") != null)
            {
                remove = Boolean.valueOf(params.get("remove"));
            }
            if (params.get("count") != null)
            {
                count = Long.valueOf(params.get("count"));
            }
            if (params.get("maxReader") != null)
            {
                maxReader = Long.valueOf(params.get("maxReader"));
            }
            if (params.get("reuse") != null)
            {
                reuse = Boolean.valueOf(params.get("reuse"));
            }

            AlfrescoSolrDataModel dataModel = null;
            String name = "test-auth-" + "" + System.nanoTime();
            SolrCore core = null;
            if (reuse)
            {
                for (String coreName : coreContainer.getCoreNames())
                {
                    if (coreName.startsWith("test-auth-"))
                    {
                        core = coreContainer.getCore(coreName);
                        name = coreName;
                        
                        SolrResourceLoader loader = core.getSchema().getResourceLoader();
                        String id = loader.getInstanceDir();
                        dataModel = AlfrescoSolrDataModel.getInstance(id);
                        break;
                    }
                }
            }
            
            if (core == null)
            {

                // copy core from template

                File solrHome = new File(getCoreContainer().getSolrHome());
                File templates = new File(solrHome, "templates");
                File template = new File(templates, "test");

                File newCore = new File(solrHome, name);

                copyDirectory(template, newCore, false);

                // fix configuration properties

                File config = new File(newCore, "conf/solrcore.properties");
                Properties properties = new Properties();
                properties.load(new FileInputStream(config));
                properties.setProperty("data.dir.root", newCore.getCanonicalPath());
                properties.store(new FileOutputStream(config), null);

                // add core

                CoreDescriptor dcore = new CoreDescriptor(coreContainer, name, newCore.toString());
                dcore.setCoreProperties(null);
                core = coreContainer.create(dcore);
                coreContainer.register(name, core, false);
                rsp.add("core", core.getName());

                SolrResourceLoader loader = core.getSchema().getResourceLoader();
                String id = loader.getInstanceDir();
                dataModel = AlfrescoSolrDataModel.getInstance(id);
                dataModel.setCMDefaultUri();
                // add data

                // Root

                NodeRef rootNodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
                addStoreRoot(core, dataModel, rootNodeRef, 1, 1, 1, 1);
                rsp.add("StoreRootNode", 1);

                // Base

                HashMap<QName, PropertyValue> baseFolderProperties = new HashMap<QName, PropertyValue>();
                baseFolderProperties.put(ContentModel.PROP_NAME, new StringPropertyValue("Base Folder"));
                HashMap<QName, String> baseFolderContent = new HashMap<QName, String>();
                NodeRef baseFolderNodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
                QName baseFolderQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "baseFolder");
                ChildAssociationRef n01CAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, baseFolderQName, baseFolderNodeRef, true, 0);
                addNode(core, dataModel, 1, 2, 1, ContentModel.TYPE_FOLDER, null, baseFolderProperties, null, "andy", new ChildAssociationRef[] { n01CAR },
                        new NodeRef[] { rootNodeRef }, new String[] { "/" + baseFolderQName.toString() }, baseFolderNodeRef, true);

                // Folders

                HashMap<QName, PropertyValue> folder00Properties = new HashMap<QName, PropertyValue>();
                folder00Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 0"));
                HashMap<QName, String> folder00Content = new HashMap<QName, String>();
                NodeRef folder00NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
                QName folder00QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 0");
                ChildAssociationRef folder00CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, baseFolderNodeRef, folder00QName, folder00NodeRef, true, 0);
                addNode(core, dataModel, 1, 3, 1, ContentModel.TYPE_FOLDER, null, folder00Properties, null, "andy", new ChildAssociationRef[] { folder00CAR }, new NodeRef[] {
                        baseFolderNodeRef, rootNodeRef }, new String[] { "/" + baseFolderQName.toString() + "/" + folder00QName.toString() }, folder00NodeRef, true);

                for (long i = 0; i < count; i++)
                {
                    addAcl(core, dataModel, 10 + (int) i, 10 + (int) i, (int) (i % maxReader), (int) maxReader);

                    HashMap<QName, PropertyValue> content00Properties = new HashMap<QName, PropertyValue>();
                    MLTextPropertyValue desc00 = new MLTextPropertyValue();
                    desc00.addValue(Locale.ENGLISH, "Doc " + i);
                    desc00.addValue(Locale.US, "Doc " + i);
                    content00Properties.put(ContentModel.PROP_DESCRIPTION, desc00);
                    content00Properties.put(ContentModel.PROP_TITLE, desc00);
                    content00Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-8", "text/plain"));
                    content00Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Doc " + i));
                    content00Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("Test"));
                    content00Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("Test"));
                    content00Properties.put(ContentModel.PROP_VERSION_LABEL, new StringPropertyValue("1.0"));
                    content00Properties.put(ContentModel.PROP_OWNER, new StringPropertyValue("Test"));
                    Date date00 = new Date();
                    content00Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date00)));
                    content00Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date00)));
                    HashMap<QName, String> content00Content = new HashMap<QName, String>();
                    content00Content.put(ContentModel.PROP_CONTENT, "Test doc number " + i);
                    NodeRef content00NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
                    QName content00QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Doc-" + i);
                    ChildAssociationRef content00CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder00NodeRef, content00QName, content00NodeRef, true, 0);
                    addNode(core, dataModel, 1, 10 + (int) i, 10 + (int) i, ContentModel.TYPE_CONTENT, new QName[] { ContentModel.ASPECT_OWNABLE, ContentModel.ASPECT_TITLED },
                            content00Properties, content00Content, "andy", new ChildAssociationRef[] { content00CAR }, new NodeRef[] { baseFolderNodeRef, rootNodeRef,
                                    folder00NodeRef }, new String[] { "/" + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + content00QName.toString() },
                            content00NodeRef, false);

                }

                core.getUpdateHandler().commit(new CommitUpdateCommand(false));
            }

            checkAuth(rsp, core, dataModel, count);

            // remove core

            if (remove)
            {
                SolrCore done = coreContainer.remove(name);
                if (done != null)
                {
                    done.close();
                }

                deleteDirectory(new File(core.getCoreDescriptor().getInstanceDir()));
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (org.apache.lucene.queryParser.ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void checkAuth(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, long count) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Auth", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testFTSQuery(dataModel, report, solrIndexSearcher, "TEXT:\"Test\"", (int) count, null, null, null);
            testFTSQuery(dataModel, report, solrIndexSearcher, "TEXT:\"doc\"", (int) count, null, null, null);
            testFTSQuery(dataModel, report, solrIndexSearcher, "TEXT:\"number\"", (int) count, null, null, null);
            testFTSQuery(dataModel, report, solrIndexSearcher, "TEXT:\"1\"", (int) count, null, null, null);

            testFTSQuery(dataModel, report, solrIndexSearcher, "AUTHORITY:\"READER-0\"", (int) count, null, null, null);

            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 8);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 9);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 10);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 98);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 99);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 100);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 998);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 999);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 1000);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 9998);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 9999);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 10000);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 10000);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 10000);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 20000);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 20000);
            buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 20000);
            // buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 100000);
            // buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 1000000);
            // buildAndRunAuthQuery(dataModel, count, report, solrIndexSearcher, 10000000);
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
     * @param dataModel
     * @param count
     * @param report
     * @param solrIndexSearcher
     * @throws ParseException
     * @throws IOException
     */
    private void buildAndRunAuthQuery(AlfrescoSolrDataModel dataModel, long count, NamedList<Object> report, SolrIndexSearcher solrIndexSearcher, int loop)
            throws org.apache.lucene.queryParser.ParseException, IOException
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= loop; i++)
        {
            if (i % 100 == 1)
            {
                if (i == 1)
                {
                    builder.append("|AUTHORITY:\"");
                }
                else
                {
                    builder.append("\" |AUTHORITY:\"");
                }
            }
            builder.append("|READER-" + i);
        }
        builder.append("\"");
        testFTSQuery(dataModel, report, solrIndexSearcher, builder.toString(), 0, null, null, null, "Auth-" + loop);
    }

    /**
     * @param req
     * @param rsp
     */
    @SuppressWarnings("unused")
    private void runCmisTests(SolrQueryRequest req, SolrQueryResponse rsp)
    {

        TimeZone.setDefault(null);
        try
        {
            boolean remove = true;
            SolrParams params = req.getParams();
            if (params.get("remove") != null)
            {
                remove = Boolean.valueOf(params.get("remove"));
            }

            String name = "test-cmis-" + "" + System.nanoTime();

            // copy core from template

            File solrHome = new File(getCoreContainer().getSolrHome());
            File templates = new File(solrHome, "templates");
            File template = new File(templates, "test");

            File newCore = new File(solrHome, name);

            copyDirectory(template, newCore, false);

            // fix configuration properties

            File config = new File(newCore, "conf/solrcore.properties");
            Properties properties = new Properties();
            properties.load(new FileInputStream(config));
            properties.setProperty("data.dir.root", newCore.getCanonicalPath());
            properties.store(new FileOutputStream(config), null);

            // add core

            CoreDescriptor dcore = new CoreDescriptor(coreContainer, name, newCore.toString());
            dcore.setCoreProperties(null);
            SolrCore core = coreContainer.create(dcore);
            coreContainer.register(name, core, false);
            rsp.add("core", core.getName());

            SolrResourceLoader loader = core.getSchema().getResourceLoader();
            String id = loader.getInstanceDir();
            AlfrescoSolrDataModel dataModel = AlfrescoSolrDataModel.getInstance(id);
            dataModel.setCMDefaultUri();
            // add data

            // Root

            NodeRef rootNodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            addStoreRoot(core, dataModel, rootNodeRef, 1, 1, 1, 1);
            rsp.add("StoreRootNode", 1);

            // Base

            HashMap<QName, PropertyValue> baseFolderProperties = new HashMap<QName, PropertyValue>();
            baseFolderProperties.put(ContentModel.PROP_NAME, new StringPropertyValue("Base Folder"));
            HashMap<QName, String> baseFolderContent = new HashMap<QName, String>();
            NodeRef baseFolderNodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName baseFolderQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "baseFolder");
            ChildAssociationRef n01CAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, baseFolderQName, baseFolderNodeRef, true, 0);
            addNode(core, dataModel, 1, 2, 1, ContentModel.TYPE_FOLDER, null, baseFolderProperties, null, "andy", new ChildAssociationRef[] { n01CAR },
                    new NodeRef[] { rootNodeRef }, new String[] { "/" + baseFolderQName.toString() }, baseFolderNodeRef, true);

            // Folders

            HashMap<QName, PropertyValue> folder00Properties = new HashMap<QName, PropertyValue>();
            folder00Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 0"));
            HashMap<QName, String> folder00Content = new HashMap<QName, String>();
            NodeRef folder00NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName folder00QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 0");
            ChildAssociationRef folder00CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, baseFolderNodeRef, folder00QName, folder00NodeRef, true, 0);
            addNode(core, dataModel, 1, 3, 1, ContentModel.TYPE_FOLDER, null, folder00Properties, null, "andy", new ChildAssociationRef[] { folder00CAR }, new NodeRef[] {
                    baseFolderNodeRef, rootNodeRef }, new String[] { "/" + baseFolderQName.toString() + "/" + folder00QName.toString() }, folder00NodeRef, true);

            HashMap<QName, PropertyValue> folder01Properties = new HashMap<QName, PropertyValue>();
            folder01Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 1"));
            HashMap<QName, String> folder01Content = new HashMap<QName, String>();
            NodeRef folder01NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName folder01QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 1");
            ChildAssociationRef folder01CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, baseFolderNodeRef, folder01QName, folder01NodeRef, true, 0);
            addNode(core, dataModel, 1, 4, 1, ContentModel.TYPE_FOLDER, null, folder01Properties, null, "bob", new ChildAssociationRef[] { folder01CAR }, new NodeRef[] {
                    baseFolderNodeRef, rootNodeRef }, new String[] { "/" + baseFolderQName.toString() + "/" + folder01QName.toString() }, folder01NodeRef, true);

            HashMap<QName, PropertyValue> folder02Properties = new HashMap<QName, PropertyValue>();
            folder02Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 2"));
            HashMap<QName, String> folder02Content = new HashMap<QName, String>();
            NodeRef folder02NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName folder02QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 2");
            ChildAssociationRef folder02CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, baseFolderNodeRef, folder02QName, folder02NodeRef, true, 0);
            addNode(core, dataModel, 1, 5, 1, ContentModel.TYPE_FOLDER, null, folder02Properties, null, "cid", new ChildAssociationRef[] { folder02CAR }, new NodeRef[] {
                    baseFolderNodeRef, rootNodeRef }, new String[] { "/" + baseFolderQName.toString() + "/" + folder02QName.toString() }, folder02NodeRef, true);

            HashMap<QName, PropertyValue> folder03Properties = new HashMap<QName, PropertyValue>();
            folder03Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 3"));
            HashMap<QName, String> folder03Content = new HashMap<QName, String>();
            NodeRef folder03NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName folder03QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 3");
            ChildAssociationRef folder03CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, baseFolderNodeRef, folder03QName, folder03NodeRef, true, 0);
            addNode(core, dataModel, 1, 6, 1, ContentModel.TYPE_FOLDER, null, folder03Properties, null, "dave", new ChildAssociationRef[] { folder03CAR }, new NodeRef[] {
                    baseFolderNodeRef, rootNodeRef }, new String[] { "/" + baseFolderQName.toString() + "/" + folder03QName.toString() }, folder03NodeRef, true);

            HashMap<QName, PropertyValue> folder04Properties = new HashMap<QName, PropertyValue>();
            folder04Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 4"));
            HashMap<QName, String> folder04Content = new HashMap<QName, String>();
            NodeRef folder04NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName folder04QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 4");
            ChildAssociationRef folder04CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder00NodeRef, folder04QName, folder04NodeRef, true, 0);
            addNode(core, dataModel, 1, 7, 1, ContentModel.TYPE_FOLDER, null, folder04Properties, null, "eoin", new ChildAssociationRef[] { folder04CAR }, new NodeRef[] {
                    baseFolderNodeRef, rootNodeRef, folder00NodeRef },
                    new String[] { "/" + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder04QName.toString() }, folder04NodeRef, true);

            HashMap<QName, PropertyValue> folder05Properties = new HashMap<QName, PropertyValue>();
            folder05Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 5"));
            HashMap<QName, String> folder05Content = new HashMap<QName, String>();
            NodeRef folder05NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName folder05QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 5");
            ChildAssociationRef folder05CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder00NodeRef, folder05QName, folder05NodeRef, true, 0);
            addNode(core, dataModel, 1, 8, 1, ContentModel.TYPE_FOLDER, null, folder05Properties, null, "fred", new ChildAssociationRef[] { folder05CAR }, new NodeRef[] {
                    baseFolderNodeRef, rootNodeRef, folder00NodeRef },
                    new String[] { "/" + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder05QName.toString() }, folder05NodeRef, true);

            HashMap<QName, PropertyValue> folder06Properties = new HashMap<QName, PropertyValue>();
            folder06Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 6"));
            HashMap<QName, String> folder06Content = new HashMap<QName, String>();
            NodeRef folder06NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName folder06QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 6");
            ChildAssociationRef folder06CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder05NodeRef, folder06QName, folder06NodeRef, true, 0);
            addNode(core, dataModel, 1, 9, 1, ContentModel.TYPE_FOLDER, null, folder06Properties, null, "gail", new ChildAssociationRef[] { folder06CAR }, new NodeRef[] {
                    baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder05NodeRef }, new String[] { "/"
                    + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder05QName.toString() + "/" + folder06QName.toString() }, folder06NodeRef, true);

            HashMap<QName, PropertyValue> folder07Properties = new HashMap<QName, PropertyValue>();
            folder07Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 7"));
            HashMap<QName, String> folder07Content = new HashMap<QName, String>();
            NodeRef folder07NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName folder07QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 7");
            ChildAssociationRef folder07CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder06NodeRef, folder07QName, folder07NodeRef, true, 0);
            addNode(core,
                    dataModel,
                    1,
                    10,
                    1,
                    ContentModel.TYPE_FOLDER,
                    null,
                    folder07Properties,
                    null,
                    "hal",
                    new ChildAssociationRef[] { folder07CAR },
                    new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder05NodeRef, folder06NodeRef },
                    new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder05QName.toString() + "/" + folder06QName.toString() + "/"
                            + folder07QName.toString() }, folder07NodeRef, true);

            HashMap<QName, PropertyValue> folder08Properties = new HashMap<QName, PropertyValue>();
            folder08Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 8"));
            HashMap<QName, String> folder08Content = new HashMap<QName, String>();
            NodeRef folder08NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName folder08QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 8");
            ChildAssociationRef folder08CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder07NodeRef, folder08QName, folder08NodeRef, true, 0);
            addNode(core,
                    dataModel,
                    1,
                    11,
                    1,
                    ContentModel.TYPE_FOLDER,
                    null,
                    folder08Properties,
                    null,
                    "ian",
                    new ChildAssociationRef[] { folder08CAR },
                    new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder05NodeRef, folder06NodeRef, folder07NodeRef },
                    new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder05QName.toString() + "/" + folder06QName.toString() + "/"
                            + folder07QName.toString() + "/" + folder08QName.toString() }, folder08NodeRef, true);

            HashMap<QName, PropertyValue> folder09Properties = new HashMap<QName, PropertyValue>();
            folder09Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Folder 9'"));
            HashMap<QName, String> folder09Content = new HashMap<QName, String>();
            NodeRef folder09NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName folder09QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Folder 9'");
            ChildAssociationRef folder09CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder08NodeRef, folder09QName, folder09NodeRef, true, 0);
            addNode(core,
                    dataModel,
                    1,
                    12,
                    1,
                    ContentModel.TYPE_FOLDER,
                    null,
                    folder09Properties,
                    null,
                    "jake",
                    new ChildAssociationRef[] { folder09CAR },
                    new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder05NodeRef, folder06NodeRef, folder07NodeRef, folder08NodeRef },
                    new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder05QName.toString() + "/" + folder06QName.toString() + "/"
                            + folder07QName.toString() + "/" + folder08QName.toString() + "/" + folder09QName.toString() }, folder09NodeRef, true);

            // content

            HashMap<QName, PropertyValue> content00Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc00 = new MLTextPropertyValue();
            desc00.addValue(Locale.ENGLISH, "Alfresco tutorial");
            desc00.addValue(Locale.US, "Alfresco tutorial");
            content00Properties.put(ContentModel.PROP_DESCRIPTION, desc00);
            content00Properties.put(ContentModel.PROP_TITLE, desc00);
            content00Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-8", "text/plain"));
            content00Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Alfresco Tutorial"));
            content00Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content00Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            content00Properties.put(ContentModel.PROP_VERSION_LABEL, new StringPropertyValue("1.0"));
            content00Properties.put(ContentModel.PROP_OWNER, new StringPropertyValue("andy"));
            Date date00 = new Date();
            content00Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date00)));
            content00Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date00)));
            HashMap<QName, String> content00Content = new HashMap<QName, String>();
            content00Content.put(ContentModel.PROP_CONTENT,
                    "The quick brown fox jumped over the lazy dog and ate the Alfresco Tutorial, in pdf format, along with the following stop words;  a an and are"
                            + " as at be but by for if in into is it no not of on or such that the their then there these they this to was will with: "
                            + " and random charcters \u00E0\u00EA\u00EE\u00F0\u00F1\u00F6\u00FB\u00FF");
            NodeRef content00NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content00QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Alfresco Tutorial");
            ChildAssociationRef content00CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder00NodeRef, content00QName, content00NodeRef, true, 0);
            addNode(core, dataModel, 1, 13, 1, ContentModel.TYPE_CONTENT, new QName[] { ContentModel.ASPECT_OWNABLE, ContentModel.ASPECT_TITLED }, content00Properties,
                    content00Content, "andy", new ChildAssociationRef[] { content00CAR }, new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef }, new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + content00QName.toString() }, content00NodeRef, true);

            HashMap<QName, PropertyValue> content01Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc01 = new MLTextPropertyValue();
            desc01.addValue(Locale.ENGLISH, "One");
            desc01.addValue(Locale.US, "One");
            content01Properties.put(ContentModel.PROP_DESCRIPTION, desc01);
            content01Properties.put(ContentModel.PROP_TITLE, desc01);
            content01Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-8", "text/plain"));
            content01Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("AA%"));
            content01Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content01Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            Date date01 = new Date(date00.getTime() + 1000);
            content01Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date01)));
            content01Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date01)));
            HashMap<QName, String> content01Content = new HashMap<QName, String>();
            content01Content.put(ContentModel.PROP_CONTENT, "One Zebra Apple");
            NodeRef content01NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content01QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "AA%");
            ChildAssociationRef content01CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder01NodeRef, content01QName, content01NodeRef, true, 0);
            addNode(core, dataModel, 1, 14, 1, ContentModel.TYPE_CONTENT, new QName[] { ContentModel.ASPECT_TITLED }, content01Properties, content01Content, "cmis",
                    new ChildAssociationRef[] { content01CAR }, new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder01NodeRef }, new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder01QName.toString() + "/" + content01QName.toString() }, content01NodeRef, true);

            HashMap<QName, PropertyValue> content02Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc02 = new MLTextPropertyValue();
            desc02.addValue(Locale.ENGLISH, "Two");
            desc02.addValue(Locale.US, "Two");
            content02Properties.put(ContentModel.PROP_DESCRIPTION, desc02);
            content02Properties.put(ContentModel.PROP_TITLE, desc02);
            content02Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-8", "text/plain"));
            content02Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("BB_"));
            content02Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content02Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            Date date02 = new Date(date01.getTime() + 1000);
            content02Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date02)));
            content02Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date02)));
            HashMap<QName, String> content02Content = new HashMap<QName, String>();
            content02Content.put(ContentModel.PROP_CONTENT, "Two Zebra Banana");
            NodeRef content02NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content02QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "BB_");
            ChildAssociationRef content02CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder02NodeRef, content02QName, content02NodeRef, true, 0);
            addNode(core, dataModel, 1, 15, 1, ContentModel.TYPE_CONTENT, new QName[] { ContentModel.ASPECT_TITLED }, content02Properties, content02Content, "cmis",
                    new ChildAssociationRef[] { content02CAR }, new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder02NodeRef }, new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder02QName.toString() + "/" + content02QName.toString() }, content02NodeRef, true);

            HashMap<QName, PropertyValue> content03Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc03 = new MLTextPropertyValue();
            desc03.addValue(Locale.ENGLISH, "Three");
            desc03.addValue(Locale.US, "Three");
            content03Properties.put(ContentModel.PROP_DESCRIPTION, desc03);
            content03Properties.put(ContentModel.PROP_TITLE, desc03);
            content03Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-8", "text/plain"));
            content03Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("CC\\"));
            content03Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content03Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            Date date03 = new Date(date02.getTime() + 1000);
            content03Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date03)));
            content03Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date03)));
            HashMap<QName, String> content03Content = new HashMap<QName, String>();
            content03Content.put(ContentModel.PROP_CONTENT, "Three Zebra Clementine");
            NodeRef content03NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content03QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "CC\\");
            ChildAssociationRef content03CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder03NodeRef, content03QName, content03NodeRef, true, 0);
            addNode(core, dataModel, 1, 16, 1, ContentModel.TYPE_CONTENT, new QName[] { ContentModel.ASPECT_TITLED }, content03Properties, content03Content, "cmis",
                    new ChildAssociationRef[] { content03CAR }, new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder03NodeRef }, new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder03QName.toString() + "/" + content03QName.toString() }, content03NodeRef, true);

            HashMap<QName, PropertyValue> content04Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc04 = new MLTextPropertyValue();
            desc04.addValue(Locale.ENGLISH, "Four");
            desc04.addValue(Locale.US, "Four");
            content04Properties.put(ContentModel.PROP_DESCRIPTION, desc04);
            content04Properties.put(ContentModel.PROP_TITLE, desc04);
            content04Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-8", "text/plain"));
            content04Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("DD\'"));
            content04Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content04Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            Date date04 = new Date(date03.getTime() + 1000);
            content04Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date04)));
            content04Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date04)));
            HashMap<QName, String> content04Content = new HashMap<QName, String>();
            content04Content.put(ContentModel.PROP_CONTENT, "Four zebra durian");
            NodeRef content04NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content04QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "DD\'");
            ChildAssociationRef content04CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder04NodeRef, content04QName, content04NodeRef, true, 0);
            addNode(core, dataModel, 1, 17, 1, ContentModel.TYPE_CONTENT, new QName[] { ContentModel.ASPECT_TITLED }, content04Properties, content04Content, null,
                    new ChildAssociationRef[] { content04CAR }, new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder04NodeRef }, new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder04QName.toString() + "/" + content04QName.toString() }, content04NodeRef,
                    true);

            HashMap<QName, PropertyValue> content05Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc05 = new MLTextPropertyValue();
            desc05.addValue(Locale.ENGLISH, "Five");
            desc05.addValue(Locale.US, "Five");
            content05Properties.put(ContentModel.PROP_DESCRIPTION, desc05);
            content05Properties.put(ContentModel.PROP_TITLE, desc05);
            content05Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-8", "text/plain"));
            content05Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("EE.aa"));
            content05Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content05Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            Date date05 = new Date(date04.getTime() + 1000);
            content05Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date05)));
            content05Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date05)));
            content05Properties.put(ContentModel.PROP_EXPIRY_DATE,
                    new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, DefaultTypeConverter.INSTANCE.convert(Date.class, "2012-12-12T12:12:12.012Z"))));
            content05Properties.put(ContentModel.PROP_LOCK_OWNER, new StringPropertyValue("andy"));
            content05Properties.put(ContentModel.PROP_LOCK_TYPE, new StringPropertyValue("WRITE_LOCK"));
            HashMap<QName, String> content05Content = new HashMap<QName, String>();
            content05Content.put(ContentModel.PROP_CONTENT, "Five zebra Ebury");
            NodeRef content05NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content05QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "EE.aa");
            ChildAssociationRef content05CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder05NodeRef, content05QName, content05NodeRef, true, 0);
            addNode(core, dataModel, 1, 18, 1, ContentModel.TYPE_CONTENT, new QName[] { ContentModel.ASPECT_TITLED, ContentModel.ASPECT_LOCKABLE }, content05Properties,
                    content05Content, null, new ChildAssociationRef[] { content05CAR }, new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder05NodeRef },
                    new String[] { "/" + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + content05QName.toString() }, content05NodeRef, true);

            HashMap<QName, PropertyValue> content06Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc06 = new MLTextPropertyValue();
            desc06.addValue(Locale.ENGLISH, "Six");
            desc06.addValue(Locale.US, "Six");
            content06Properties.put(ContentModel.PROP_DESCRIPTION, desc06);
            content06Properties.put(ContentModel.PROP_TITLE, desc06);
            content06Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-8", "text/plain"));
            content06Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("FF.EE"));
            content06Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content06Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            Date date06 = new Date(date05.getTime() + 1000);
            content06Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date06)));
            content06Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date06)));
            HashMap<QName, String> content06Content = new HashMap<QName, String>();
            content06Content.put(ContentModel.PROP_CONTENT, "Six zebra fig");
            NodeRef content06NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content06QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "FF.EE");
            ChildAssociationRef content06CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder06NodeRef, content06QName, content06NodeRef, true, 0);
            addNode(core,
                    dataModel,
                    1,
                    19,
                    1,
                    ContentModel.TYPE_CONTENT,
                    new QName[] { ContentModel.ASPECT_TITLED },
                    content06Properties,
                    content06Content,
                    null,
                    new ChildAssociationRef[] { content06CAR },
                    new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder05NodeRef, folder06NodeRef },
                    new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder05QName.toString() + "/" + folder06QName.toString() + "/"
                            + content06QName.toString() }, content06NodeRef, true);

            HashMap<QName, PropertyValue> content07Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc07 = new MLTextPropertyValue();
            desc07.addValue(Locale.ENGLISH, "Seven");
            desc07.addValue(Locale.US, "Seven");
            content07Properties.put(ContentModel.PROP_DESCRIPTION, desc07);
            content07Properties.put(ContentModel.PROP_TITLE, desc07);
            content07Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-8", "text/plain"));
            content07Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("GG*GG"));
            content07Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content07Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            Date date07 = new Date(date06.getTime() + 1000);
            content07Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date07)));
            content07Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date07)));
            HashMap<QName, String> content07Content = new HashMap<QName, String>();
            content07Content.put(ContentModel.PROP_CONTENT, "Seven zebra grapefruit");
            NodeRef content07NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content07QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "GG*GG");
            ChildAssociationRef content07CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder07NodeRef, content07QName, content07NodeRef, true, 0);
            addNode(core,
                    dataModel,
                    1,
                    20,
                    1,
                    ContentModel.TYPE_CONTENT,
                    new QName[] { ContentModel.ASPECT_TITLED },
                    content07Properties,
                    content07Content,
                    null,
                    new ChildAssociationRef[] { content07CAR },
                    new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder05NodeRef, folder06NodeRef, folder07NodeRef },
                    new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder05QName.toString() + "/" + folder06QName.toString() + "/"
                            + folder07QName.toString() + "/" + content07QName.toString() }, content07NodeRef, true);

            HashMap<QName, PropertyValue> content08Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc08 = new MLTextPropertyValue();
            desc08.addValue(Locale.ENGLISH, "Eight");
            desc08.addValue(Locale.US, "Eight");
            content08Properties.put(ContentModel.PROP_DESCRIPTION, desc08);
            content08Properties.put(ContentModel.PROP_TITLE, desc08);
            content08Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-8", "text/plain"));
            content08Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("HH?HH"));
            content08Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content08Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            Date date08 = new Date(date07.getTime() + 1000);
            content08Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date08)));
            content08Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date08)));
            HashMap<QName, String> content08Content = new HashMap<QName, String>();
            content08Content.put(ContentModel.PROP_CONTENT, "Eight zebra jackfruit");
            NodeRef content08NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content08QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "HH?HH");
            ChildAssociationRef content08CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder08NodeRef, content08QName, content08NodeRef, true, 0);
            addNode(core,
                    dataModel,
                    1,
                    21,
                    1,
                    ContentModel.TYPE_CONTENT,
                    new QName[] { ContentModel.ASPECT_TITLED },
                    content08Properties,
                    content08Content,
                    null,
                    new ChildAssociationRef[] { content08CAR },
                    new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder05NodeRef, folder06NodeRef, folder07NodeRef, folder08NodeRef },
                    new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder05QName.toString() + "/" + folder06QName.toString() + "/"
                            + folder07QName.toString() + "/" + folder08QName.toString() + "/" + content08QName.toString() }, content08NodeRef, true);

            HashMap<QName, PropertyValue> content09Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc09 = new MLTextPropertyValue();
            desc09.addValue(Locale.ENGLISH, "Nine");
            desc09.addValue(Locale.US, "Nine");
            content09Properties.put(ContentModel.PROP_DESCRIPTION, desc09);
            content09Properties.put(ContentModel.PROP_TITLE, desc09);
            content09Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-9", "text/plain"));
            content09Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("aa"));
            content09Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content09Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            Date date09 = new Date(date08.getTime() + 1000);
            content09Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date09)));
            content09Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date09)));
            content09Properties.put(ContentModel.PROP_VERSION_LABEL, new StringPropertyValue("label"));
            HashMap<QName, String> content09Content = new HashMap<QName, String>();
            content09Content.put(ContentModel.PROP_CONTENT, "Nine zebra kiwi");
            NodeRef content09NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content09QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "aa");
            ChildAssociationRef content09CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder09NodeRef, content09QName, content09NodeRef, true, 0);
            addNode(core,
                    dataModel,
                    1,
                    22,
                    1,
                    ContentModel.TYPE_CONTENT,
                    new QName[] { ContentModel.ASPECT_TITLED },
                    content09Properties,
                    content09Content,
                    null,
                    new ChildAssociationRef[] { content09CAR },
                    new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder05NodeRef, folder06NodeRef, folder07NodeRef, folder08NodeRef, folder09NodeRef },
                    new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder05QName.toString() + "/" + folder06QName.toString() + "/"
                            + folder07QName.toString() + "/" + folder08QName.toString() + "/" + folder09QName.toString() + "/" + content09QName.toString() }, content09NodeRef,
                    true);

            HashMap<QName, PropertyValue> content10Properties = new HashMap<QName, PropertyValue>();
            MLTextPropertyValue desc10 = new MLTextPropertyValue();
            desc10.addValue(Locale.ENGLISH, "Ten");
            desc10.addValue(Locale.US, "Ten");
            content10Properties.put(ContentModel.PROP_DESCRIPTION, desc10);
            content10Properties.put(ContentModel.PROP_TITLE, desc10);
            content10Properties.put(ContentModel.PROP_CONTENT, new ContentPropertyValue(Locale.UK, 0l, "UTF-9", "text/plain"));
            content10Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("aa-thumb"));
            content10Properties.put(ContentModel.PROP_CREATOR, new StringPropertyValue("System"));
            content10Properties.put(ContentModel.PROP_MODIFIER, new StringPropertyValue("System"));
            Date date10 = new Date(date09.getTime() + 1000);
            content10Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date10)));
            content10Properties.put(ContentModel.PROP_MODIFIED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date10)));
            content10Properties.put(ContentModel.PROP_VERSION_LABEL, new StringPropertyValue("label"));
            HashMap<QName, String> content10Content = new HashMap<QName, String>();
            content10Content.put(ContentModel.PROP_CONTENT, "Ten zebra kiwi thumb");
            NodeRef content10NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
            QName content10QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "aa-thumb");
            ChildAssociationRef content10CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder09NodeRef, content10QName, content10NodeRef, true, 0);
            addNode(core,
                    dataModel,
                    1,
                    23,
                    1,
                    ContentModel.TYPE_DICTIONARY_MODEL,
                    new QName[] { ContentModel.ASPECT_TITLED },
                    content10Properties,
                    content10Content,
                    null,
                    new ChildAssociationRef[] { content10CAR },
                    new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef, folder05NodeRef, folder06NodeRef, folder07NodeRef, folder08NodeRef, folder09NodeRef },
                    new String[] { "/"
                            + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + folder05QName.toString() + "/" + folder06QName.toString() + "/"
                            + folder07QName.toString() + "/" + folder08QName.toString() + "/" + folder09QName.toString() + "/" + content10QName.toString() }, content10NodeRef,
                    true);

            checkCmisBasic(rsp, core, dataModel);
            // cmis:allowedChildObjectTypeIds
            checkCmisParentId(rsp, core, dataModel, baseFolderNodeRef.toString());
            // cmis:path
            // cmis:contentStreamId
            checkCmisContentStreamFileName(rsp, core, dataModel);
            checkCmisContentStreamMimeType(rsp, core, dataModel);
            checkCmisContentStreamLength(rsp, core, dataModel);
            // cmis:checkinComment
            // cmis:versionSeriesCheckedOutId
            // cmis:versionSeriesCheckedOutBy
            // cmis:isVersionSeriesCheckedOut
            // cmis:versionSeriesId
            // cmis:versionLabel
            // cmis:isLatestMajorVersion
            // cmis:isMajorVersion
            // cmis:isLatestVersion
            // cmis:isImmutable
            checkCmisName(rsp, core, dataModel);
            // cmis:changeToken
            checkCmisLastModificationDate(rsp, core, dataModel, date10);
            checkCmisLastModifiedBy(rsp, core, dataModel);
            checkCmisCreationDate(rsp, core, dataModel, date10);
            checkCmisCreatedBy(rsp, core, dataModel);
            checkCmisObjectTypeId(rsp, core, dataModel);
            checkCmisObjecId(rsp, core, dataModel, folder00NodeRef.toString(), content00NodeRef.toString());

            checkCmisOrderby(rsp, core, dataModel);

            checkCmisUpperAndLower(rsp, core, dataModel);
            checkCmisTextPredicates(rsp, core, dataModel);
            checkCmisSimpleConjunction(rsp, core, dataModel);
            checkCmisSimpleDisjunction(rsp, core, dataModel);
            checkCmisExists(rsp, core, dataModel);
            checkInTree(rsp, core, dataModel, folder00NodeRef.toString());
            checkInFolder(rsp, core, dataModel, folder00NodeRef.toString());
            checkFTS(rsp, core, dataModel);
            checkAccessAs(rsp, core, dataModel);
            checkDateFormatting(rsp, core, dataModel);
            checkAspectJoin(rsp, core, dataModel);
            checkFTSConnectives(rsp, core, dataModel);
            checkLikeEscaping(rsp, core, dataModel);

            addTypeTestData(core, dataModel, folder00NodeRef, rootNodeRef, baseFolderNodeRef, baseFolderQName, folder00QName, date00);
            check_D_text(rsp, core, dataModel);
            check_locale(rsp, core, dataModel);
            check_D_mltext(rsp, core, dataModel);
            check_D_float(rsp, core, dataModel);
            check_D_double(rsp, core, dataModel);
            check_D_int(rsp, core, dataModel);
            check_D_long(rsp, core, dataModel);
            check_D_date(rsp, core, dataModel, date00);
            check_D_datetime(rsp, core, dataModel, date00);
            check_D_boolean(rsp, core, dataModel);
            check_contains_syntax(rsp, core, dataModel);

            addTypeSortTestData(core, dataModel, folder00NodeRef, rootNodeRef, baseFolderNodeRef, baseFolderQName, folder00QName, date00);
            check_order(rsp, core, dataModel);

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
            e.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        // catch (org.apache.lucene.queryParser.ParseException e)
        // {
        // e.printStackTrace();
        // }

    }

    /**
     * @param rsp
     * @param core
     * @param dataModel
     * @throws IOException
     */
    private void checkCmisBasic(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("CMIS Basic", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * from cmis:folder", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * from cmis:document", 11, null, null, null, null, null, (String) null);

    }

    private void checkCmisParentId(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, String base) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:parentId", report);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:parentId FROM cmis:folder WHERE cmis:parentId =  '" + base.toString() + "'", 4, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:parentId FROM cmis:folder WHERE cmis:parentId <> '" + base.toString() + "'", 7, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:parentId FROM cmis:folder WHERE cmis:parentId IN     ('" + base.toString() + "')", 4, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:parentId FROM cmis:folder WHERE cmis:parentId NOT IN ('" + base.toString() + "')", 7, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:parentId FROM cmis:folder WHERE cmis:parentId IS NOT NULL", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:parentId FROM cmis:folder WHERE cmis:parentId IS     NULL", 0, null, null, null, null, null, (String) null);

    }

    private void checkCmisContentStreamFileName(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:contentStreamFileName", report);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'Alfresco Tutorial'", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'AA%'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'BB_'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'CC\\\\'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'DD\\''", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'EE.aa'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'FF.EE'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'GG*GG'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'HH?HH'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'aa'", 1, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName =  'Alfresco Tutorial'", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName <> 'Alfresco Tutorial'", 10, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName <  'Alfresco Tutorial'", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName <= 'Alfresco Tutorial'", 2, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName >  'Alfresco Tutorial'", 9, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName >= 'Alfresco Tutorial'", 10, null, null,
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName IN     ('Alfresco Tutorial')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName NOT IN ('Alfresco Tutorial')", 10, null,
                null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName     LIKE 'Alfresco Tutorial'", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName NOT LIKE 'Alfresco Tutorial'", 10, null,
                null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName IS NOT NULL", 11, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamFileName FROM cmis:document WHERE cmis:contentStreamFileName IS     NULL", 0, null, null, null, null,
                null, (String) null);
    }

    private void checkCmisContentStreamMimeType(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:contentStreamMimeType", report);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType =  'text/plain'", 11, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType <> 'text/plain'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType <  'text/plain'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType <= 'text/plain'", 11, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType >  'text/plain'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType >= 'text/plain'", 11, null, null, null,
                null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType IN     ('text/plain')", 11, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType NOT IN ('text/plain')", 0, null, null,
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType     LIKE 'text/plain'", 11, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType NOT LIKE 'text/plain'", 0, null, null,
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType IS NOT NULL", 11, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamMimeType FROM cmis:document WHERE cmis:contentStreamMimeType IS     NULL", 0, null, null, null, null,
                null, (String) null);
    }

    private void checkCmisContentStreamLength(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:contentStreamLength", report);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength =  750", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength <> 750", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength <  750", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength <= 750", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength >  750", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength >= 750", 0, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength IN     (750)", 0, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength NOT IN (750)", 11, null, null, null, null,
                null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength     LIKE '750'", 0, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength NOT LIKE '750'", 11, null, null, null, null,
                null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength IS NOT NULL", 11, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:contentStreamLength FROM cmis:document WHERE cmis:contentStreamLength IS     NULL", 0, null, null, null, null, null,
                (String) null);
    }

    private void checkCmisName(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:name", report);

        // FOLDER

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name =  'Folder 1'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name <> 'Folder 1'", 10, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name <  'Folder 1'", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name <= 'Folder 1'", 3, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name >  'Folder 1'", 8, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name >= 'Folder 1'", 9, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name IN     ('Folder 1')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name NOT IN ('Folder 1')", 10, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name     LIKE 'Folder 1'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name NOT LIKE 'Folder 1'", 10, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name IS NOT NULL", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder WHERE cmis:name IS     NULL", 0, null, null, null, null, null, (String) null);

        // DOCUMENT

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name =  'Alfresco Tutorial'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name <> 'Alfresco Tutorial'", 10, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name <  'Alfresco Tutorial'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name <= 'Alfresco Tutorial'", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name >  'Alfresco Tutorial'", 9, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name >= 'Alfresco Tutorial'", 10, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name IN     ('Alfresco Tutorial')", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name NOT IN ('Alfresco Tutorial')", 10, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'Alfresco Tutorial'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name NOT LIKE 'Alfresco Tutorial'", 10, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name IS NOT NULL", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name IS     NULL", 0, null, null, null, null, null, (String) null);

    }

    private void checkCmisLastModificationDate(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, Date lastModificationDate) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:lastModificationDate", report);

        String sDate = ISO8601DateFormat.format(lastModificationDate);
        TimeZone.setDefault(TimeZone.getTimeZone("PST"));
        String sDate2 = ISO8601DateFormat.format(lastModificationDate);
        TimeZone.setDefault(null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate = TIMESTAMP '" + sDate + "'", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate = TIMESTAMP '" + sDate2 + "'", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate =  '" + sDate + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate <> '" + sDate + "'", 10, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate <  '" + sDate + "'", 10, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate <= '" + sDate + "'", 11, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate >  '" + sDate + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate >= '" + sDate + "'", 1, null, null, null,
                null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate IN     ('" + sDate + "')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate NOT IN ('" + sDate + "')", 10, null, null,
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate IS NOT NULL", 11, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate IS     NULL", 0, null, null, null, null,
                null, (String) null);

        Date date = Duration.subtract(lastModificationDate, new Duration("P1D"));
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(date);
        yesterday.set(Calendar.MILLISECOND, yesterday.getMinimum(Calendar.MILLISECOND));
        sDate = ISO8601DateFormat.format(yesterday.getTime());

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate =  '" + sDate + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate <> '" + sDate + "'", 11, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate <  '" + sDate + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate <= '" + sDate + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate >  '" + sDate + "'", 11, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate >= '" + sDate + "'", 11, null, null, null,
                null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate IN     ('" + sDate + "')", 0, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate NOT IN ('" + sDate + "')", 11, null, null,
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate IS NOT NULL", 11, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate IS     NULL", 0, null, null, null, null,
                null, (String) null);
        ;

        date = Duration.add(date, new Duration("P2D"));
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(date);
        tomorrow.set(Calendar.MILLISECOND, tomorrow.getMinimum(Calendar.MILLISECOND));
        sDate = ISO8601DateFormat.format(tomorrow.getTime());

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate =  '" + sDate + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate <> '" + sDate + "'", 11, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate <  '" + sDate + "'", 11, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate <= '" + sDate + "'", 11, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate >  '" + sDate + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate >= '" + sDate + "'", 0, null, null, null,
                null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate IN     ('" + sDate + "')", 0, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate NOT IN ('" + sDate + "')", 11, null, null,
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate IS NOT NULL", 11, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:document WHERE cmis:lastModificationDate IS     NULL", 0, null, null, null, null,
                null, (String) null);
        ;

    }

    private void checkCmisLastModifiedBy(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:lastModifiedBy", report);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy =  'System'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy <> 'System'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy <  'System'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy <= 'System'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy >  'System'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy >= 'System'", 11, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy IN     ('System')", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy NOT IN ('System')", 0, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy     LIKE 'System'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy NOT LIKE 'System'", 0, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy IS NOT NULL", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:document WHERE cmis:lastModifiedBy IS     NULL", 0, null, null, null, null, null,
                (String) null);

    }

    private void checkCmisCreationDate(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, Date lastModificationDate) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:creationDate", report);

        String sDate = ISO8601DateFormat.format(lastModificationDate);
        TimeZone.setDefault(TimeZone.getTimeZone("PST"));
        String sDate2 = ISO8601DateFormat.format(lastModificationDate);
        TimeZone.setDefault(null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate = TIMESTAMP '" + sDate + "'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate = TIMESTAMP '" + sDate2 + "'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate =  '" + sDate + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate <> '" + sDate + "'", 10, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate <  '" + sDate + "'", 10, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate <= '" + sDate + "'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate >  '" + sDate + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate >= '" + sDate + "'", 1, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate IN     ('" + sDate + "')", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate NOT IN ('" + sDate + "')", 10, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate IS NOT NULL", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate IS     NULL", 0, null, null, null, null, null, (String) null);

        Date date = Duration.subtract(lastModificationDate, new Duration("P1D"));
        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(date);
        yesterday.set(Calendar.MILLISECOND, yesterday.getMinimum(Calendar.MILLISECOND));
        sDate = ISO8601DateFormat.format(yesterday.getTime());

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate =  '" + sDate + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate <> '" + sDate + "'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate <  '" + sDate + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate <= '" + sDate + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate >  '" + sDate + "'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate >= '" + sDate + "'", 11, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate IN     ('" + sDate + "')", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate NOT IN ('" + sDate + "')", 11, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate IS NOT NULL", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate IS     NULL", 0, null, null, null, null, null, (String) null);
        ;

        date = Duration.add(date, new Duration("P2D"));
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(date);
        tomorrow.set(Calendar.MILLISECOND, tomorrow.getMinimum(Calendar.MILLISECOND));
        sDate = ISO8601DateFormat.format(tomorrow.getTime());

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate =  '" + sDate + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate <> '" + sDate + "'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate <  '" + sDate + "'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate <= '" + sDate + "'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate >  '" + sDate + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate >= '" + sDate + "'", 0, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate IN     ('" + sDate + "')", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate NOT IN ('" + sDate + "')", 11, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate IS NOT NULL", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:document WHERE cmis:creationDate IS     NULL", 0, null, null, null, null, null, (String) null);
        ;

    }

    private void checkCmisCreatedBy(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:createdBy", report);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy =  'System'", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy <> 'System'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy <  'System'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy <= 'System'", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy >  'System'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy >= 'System'", 11, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy IN     ('System')", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy NOT IN ('System')", 0, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy     LIKE 'System'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy NOT LIKE 'System'", 0, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy IS NOT NULL", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:document WHERE cmis:createdBy IS     NULL", 0, null, null, null, null, null, (String) null);

    }

    private void checkCmisObjectTypeId(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:objectTypeId", report);

        // Doc

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:document WHERE cmis:objectTypeId =  'cmis:document'", 10, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:document WHERE cmis:objectTypeId <> 'cmis:document'", 1, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:document WHERE cmis:objectTypeId IN     ('cmis:document')", 10, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:document WHERE cmis:objectTypeId NOT IN ('cmis:document')", 1, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:document WHERE cmis:objectTypeId IS NOT NULL", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:document WHERE cmis:objectTypeId IS     NULL", 0, null, null, null, null, null, (String) null);

        // Folder

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:folder WHERE cmis:objectTypeId =  'cmis:folder'", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:folder WHERE cmis:objectTypeId <> 'cmis:folder'", 0, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:folder WHERE cmis:objectTypeId IN     ('cmis:folder')", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:folder WHERE cmis:objectTypeId NOT IN ('cmis:folder')", 0, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:folder WHERE cmis:objectTypeId IS NOT NULL", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:folder WHERE cmis:objectTypeId IS     NULL", 0, null, null, null, null, null, (String) null);

    }

    private void checkCmisObjecId(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, String folderId, String docId) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("cmis:objectId", report);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE cmis:objectId =  '" + folderId + "'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE cmis:objectId <> '" + folderId + "'", 10, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE cmis:objectId IN     ('" + folderId + "')", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE cmis:objectId  NOT IN('" + folderId + "')", 10, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE IN_FOLDER('" + folderId + "')", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE IN_TREE  ('" + folderId + "')", 6, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE cmis:objectId IS NOT NULL", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE cmis:objectId IS     NULL", 0, null, null, null, null, null, (String) null);

        // ignore folder versions

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE cmis:objectId =  '" + folderId + ";1.0'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE cmis:objectId <> '" + folderId + ";1.0'", 10, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE cmis:objectId IN     ('" + folderId + ";1.0')", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE cmis:objectId  NOT IN('" + folderId + ";1.0')", 10, null, null, null, null, null,
                (String) null);

        // testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE IN_FOLDER('" +
        // folderId + ";1.0')", 2, null, null, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:folder WHERE IN_TREE  ('" +
        // folderId + ";1.0')", 6, null, null, null, null, null, (String) null);

        // Docs

        String id = docId;

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId =  '" + id + "'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId <> '" + id + "'", 10, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId IN     ('" + id + "')", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId  NOT IN('" + id + "')", 10, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId IS NOT NULL", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId IS     NULL", 0, null, null, null, null, null, (String) null);

        id = docId + ";1.0";

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId =  '" + id + "'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId <> '" + id + "'", 10, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId IN     ('" + id + "')", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId  NOT IN('" + id + "')", 10, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId IS NOT NULL", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectId FROM cmis:document WHERE cmis:objectId IS     NULL", 0, null, null, null, null, null, (String) null);
    }

    private void checkCmisOrderby(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("CMIS Order by", report);

        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId FROM cmis:folder ORDER BY cmis:objectId", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId FROM cmis:folder ORDER BY cmis:objectTypeId", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId FROM cmis:folder ORDER BY cmis:objectId ASC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId FROM cmis:folder ORDER BY cmis:objectId DESC", 11, null, new int[] { 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 },
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId Meep FROM cmis:folder ORDER BY Meep", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId Meep FROM cmis:folder ORDER BY cmis:objectId", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId Meep FROM cmis:folder ORDER BY Meep ASC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId Meep FROM cmis:folder ORDER BY Meep DESC", 11, null, new int[] { 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 },
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId FROM cmis:folder F ORDER BY F.cmis:objectId", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId FROM cmis:folder F ORDER BY cmis:objectId", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId FROM cmis:folder F ORDER BY F.cmis:objectId ASC", 11, null,
                new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  cmis:objectId FROM cmis:folder F ORDER BY F.cmis:objectId DESC", 11, null, new int[] { 12, 11, 10, 9, 8, 7, 6, 5, 4, 3,
                2 }, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT  F.cmis:objectId Meep FROM cmis:folder F ORDER BY Meep", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  F.cmis:objectId Meep FROM cmis:folder F ORDER BY F.cmis:objectId", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                12 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  F.cmis:objectId Meep FROM cmis:folder F ORDER BY cmis:objectId", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                12 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  F.cmis:objectId Meep FROM cmis:folder F ORDER BY Meep ASC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT  F.cmis:objectId Meep FROM cmis:folder F ORDER BY Meep DESC", 11, null, new int[] { 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 },
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:document where CONTAINS('*') ORDER BY MEEP", 11, null, new int[] { 13, 14, 15,
                16, 17, 18, 19, 20, 21, 22, 23 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT SCORE(), cmis:objectId FROM cmis:document where CONTAINS('*') ORDER BY SEARCH_SCORE", 11, null, new int[] { 13, 14, 15,
                16, 17, 18, 19, 20, 21, 22, 23 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:document where CONTAINS('*') ORDER BY MEEP ASC", 11, null, new int[] { 13, 14,
                15, 16, 17, 18, 19, 20, 21, 22, 23 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:document where CONTAINS('*') ORDER BY MEEP DESC", 11, null, new int[] { 13, 14,
                15, 16, 17, 18, 19, 20, 21, 22, 23 }, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:folder where CONTAINS('cmis:name:*') ORDER BY MEEP", 11, null, new int[] { 2, 3,
                4, 5, 6, 7, 8, 9, 10, 11, 12 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:folder where CONTAINS('cmis:name:*') ORDER BY MEEP ASC", 11, null, new int[] {
                2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:folder where CONTAINS('cmis:name:*') ORDER BY MEEP DESC", 11, null, new int[] {
                2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }, null, null, null, (String) null);

        // other

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:folder ORDER BY cmis:objectTypeId ASC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                12 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:objectTypeId FROM cmis:folder ORDER BY cmis:objectTypeId DESC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11, 12 }, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:folder ORDER BY cmis:createdBy ASC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:createdBy FROM cmis:folder ORDER BY cmis:createdBy DESC", 11, null,
                new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:folder ORDER BY cmis:creationDate ASC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                12 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:creationDate FROM cmis:folder ORDER BY cmis:creationDate DESC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11, 12 }, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:folder ORDER BY cmis:lastModifiedBy ASC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11, 12 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModifiedBy FROM cmis:folder ORDER BY cmis:lastModifiedBy DESC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9,
                10, 11, 12 }, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:folder ORDER BY cmis:lastModificationDate ASC", 11, null, new int[] { 2, 3, 4, 5, 6,
                7, 8, 9, 10, 11, 12 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:lastModificationDate FROM cmis:folder ORDER BY cmis:lastModificationDate DESC", 11, null, new int[] { 2, 3, 4, 5, 6,
                7, 8, 9, 10, 11, 12 }, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder ORDER BY cmis:name ASC", 11, null, new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:folder ORDER BY cmis:name DESC", 11, null, new int[] { 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 }, null,
                null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document ORDER BY cmis:name ASC", 11, null, new int[] { 22, 14, 23, 13, 15, 16, 17, 18, 19, 20, 21 },
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document ORDER BY cmis:name DESC", 11, null,
                new int[] { 21, 20, 19, 18, 17, 16, 15, 13, 23, 14, 22 }, null, null, null, (String) null);

        // testQueryByHandler(report, core, "/cmis",
        // "SELECT cmis:versionLabel FROM cmis:document ORDER BY cmis:versionLabel ASC", 11, null, new int[]{2, 3, 4, 5,
        // 6, 7, 8, 9, 10, 11, 12}, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/cmis",
        // "SELECT cmis:versionLabel FROM cmis:document ORDER BY cmis:versionLabel DESC", 11, null, new int[]{2, 3, 4,
        // 5, 6, 7, 8, 9, 10, 11, 12}, null, null, null, (String) null);

        // testQueryByHandler(report, core, "/cmis",
        // "SELECT cmis:contentStreamFileName FROM cmis:document ORDER BY cmis:contentStreamFileName ASC", 11, null, new
        // int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/cmis",
        // "SELECT cmis:contentStreamFileName FROM cmis:document ORDER BY cmis:contentStreamFileName DESC", 11, null,
        // new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, null, null, null, (String) null);

        // testQueryByHandler(report, core, "/cmis",
        // "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:folder WHERE CONTAINS('cmis:name:*') AND cmis:name = 'compan home' ORDER BY SCORE() DESC",
        // 11, null, new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/cmis",
        // "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:folder WHERE CONTAINS('cmis:name:*') AND cmis:name IN ('company', 'home') ORDER BY MEEEP DESC",
        // 11, null, new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/cmis",
        // "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:folder WHERE CONTAINS('cmis:name:*') AND cmis:name IN ('company', 'home') ORDER BY cmis:parentId DESC",
        // 11, null, new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis",
                "SELECT SCORE() AS MEEP, cmis:objectId, cmis:parentId FROM cmis:folder WHERE CONTAINS('cmis:name:*') ORDER BY cmis:parentId DESC", 11, null, new int[] { 2, 3, 4,
                        5, 6, 7, 8, 9, 10, 11, 12 }, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/cmis",
        // "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:folder WHERE CONTAINS('cmis:name:*') AND cmis:name IN ('company', 'home') ORDER BY cmis:notThere DESC",
        // 11, null, new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/cmis",
        // "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:folder as F WHERE CONTAINS('cmis:name:*') AND cmis:name IN ('company', 'home') ORDER BY F.cmis:parentId DESC",
        // 11, null, new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/cmis",
        // "SELECT SCORE() AS MEEP, cmis:objectId FROM cmis:folder F WHERE CONTAINS('cmis:name:*') AND cmis:name IN ('company', 'home') ORDER BY F.cmis:notThere DESC",
        // 11, null, new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, null, null, null, (String) null);
    }

    private void checkCmisUpperAndLower(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Upper and Lower", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name = 'Folder 1'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name = 'FOLDER 1'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name = 'folder 1'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Upper(cmis:name) = 'FOLDER 1'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Lower(cmis:name) = 'folder 1'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Upper(cmis:name) = 'folder 1'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Lower(cmis:name) = 'FOLDER 1'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Upper(cmis:name) = 'Folder 1'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Lower(cmis:name) = 'Folder 1'", 0, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Upper(cmis:name) <> 'FOLDER 1'", 10, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Upper(cmis:name) <= 'FOLDER 1'", 3, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Upper(cmis:name) <  'FOLDER 1'", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Upper(cmis:name) >= 'FOLDER 1'", 9, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE Upper(cmis:name) >  'FOLDER 1'", 8, null, null, null, null, null, (String) null);

    }

    private void checkCmisTextPredicates(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Text predicates", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name = 'Folder 1'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name = 'Folder 9'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name = 'Folder 9\\''", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND NOT cmis:name = 'Folder 1'", 10, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND 'Folder 1' = ANY cmis:name", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND NOT cmis:name <> 'Folder 1'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name <> 'Folder 1'", 10, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name <  'Folder 1'", 2, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name <= 'Folder 1'", 3, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name >  'Folder 1'", 8, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name >= 'Folder 1'", 9, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name IN ('Folder 1', '1')", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name NOT IN ('Folder 1', 'Folder 9\\'')", 9, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND ANY cmis:name IN ('Folder 1', 'Folder 9\\'')", 2, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND ANY cmis:name NOT IN ('2', '3')", 11, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name LIKE 'Folder 1'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name LIKE 'Fol%'", 10, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name LIKE 'F_l_e_ 1'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name NOT LIKE 'F_l_e_ 1'", 10, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name LIKE 'F_l_e_ %'", 10, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name NOT LIKE 'F_l_e_ %'", 1, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name LIKE 'F_l_e_ _'", 9, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name NOT LIKE 'F_l_e_ _'", 2, null, null, null, null, null,
                (String) null);
    }

    private void checkCmisSimpleConjunction(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Simple conjunction", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name = 'Folder 1'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL AND cmis:name = 'Folder'", 0, null, null, null, null, null, (String) null);
    }

    private void checkCmisSimpleDisjunction(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Simple disjunction", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name = 'Folder 1'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name = 'Folder 2'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name = 'Folder 1' OR cmis:name = 'Folder 2'", 2, null, null, null, null, null,
                (String) null);
    }

    private void checkCmisExists(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Exists", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NOT NULL", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE cmis:name IS NULL", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE cmis:name IS NOT NULL", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE cmis:name IS NULL", 0, null, null, null, null, null, (String) null);
    }

    private void checkInTree(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, String id) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("In Tree", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE IN_TREE('" + id + "')", 6, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder F WHERE IN_TREE(F, '" + id + "')", 6, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT D.*, O.* FROM cmis:document AS D JOIN cm:ownable AS O ON D.cmis:objectId = O.cmis:objectId WHERE IN_TREE(D, '"
                + id + "')", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE IN_TREE('woof://woof/woof')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE IN_TREE('woof://woof/woof;woof')", 0, null, null, null, null, null, (String) null);

    }

    private void checkInFolder(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, String id) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("In Folder", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE IN_FOLDER('" + id + "')", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder F WHERE IN_FOLDER(F, '" + id + "')", 2, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT D.*, O.* FROM cmis:document AS D JOIN cm:ownable AS O ON D.cmis:objectId = O.cmis:objectId WHERE IN_FOLDER(D, '"
                + id + "')", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE IN_FOLDER('woof://woof/woof')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE IN_FOLDER('woof://woof/woof;woof')", 0, null, null, null, null, null, (String) null);

    }

    private void checkFTS(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("FTS", report);

        testQueryByHandler(report, core, "/cmis", "SELECT SCORE()as ONE, SCORE()as TWO, D.* FROM cmis:document D WHERE CONTAINS('\\'zebra\\'')", 10, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('\\'zebra\\'')", 10, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('\\'quick\\'')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('\\'quick\\'')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document D WHERE CONTAINS(D, 'cmis:name:\\'Tutorial\\'')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name as BOO FROM cmis:document D WHERE CONTAINS('BOO:\\'Tutorial\\'')", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document D WHERE CONTAINS('TEXT:\\'zebra\\'')", 10, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document D WHERE CONTAINS('ALL:\\'zebra\\'')", 10, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document D WHERE CONTAINS('d:content:\\'zebra\\'')", 10, null, null, null, null, null, (String) null);

    }

    private void checkAccessAs(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Access", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document", 0, null, null, null, null, null, "{!afts}|AUTHORITY:guest");
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document", 0, null, null, null, null, null, "{!afts}|AUTHSET:\":guest\"");
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document", 3, null, null, null, null, null, "{!afts}|AUTHORITY:cmis");
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document", 3, null, null, null, null, null, "{!afts}|AUTHSET:\":cmis\"");
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document", 1, null, null, null, null, null, "{!afts}|OWNER:andy");
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document", 1, null, null, null, null, null, "{!afts}|OWNERSET:\":andy\"");
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document", 1, null, null, null, null, null, "{!afts}|AUTHORITY:andy");
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":andy\"");

    }

    private void checkDateFormatting(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Date formatting", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cm:lockable L WHERE L.cm:expiryDate =  TIMESTAMP '2012-12-12T12:12:12.012Z'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cm:lockable L WHERE L.cm:expiryDate =  TIMESTAMP '2012-012-12T12:12:12.012Z'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cm:lockable L WHERE L.cm:expiryDate =  TIMESTAMP '2012-2-12T12:12:12.012Z'", 0, null, null, null, null, null,
                (String) null);
    }

    private void checkAspectJoin(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Aspect Join", report);

        testQueryByHandler(
                report,
                core,
                "/cmis",
                "select o.*, t.* from ( cm:ownable o join cm:titled t on o.cmis:objectId = t.cmis:objectId JOIN cmis:document AS D ON D.cmis:objectId = o.cmis:objectId  ) where o.cm:owner = 'andy' and t.cm:title = 'Alfresco tutorial' and CONTAINS(D, '\\'jumped\\'') and D.cmis:contentStreamLength <> 2",
                1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cm:ownable", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cm:ownable where cm:owner = 'andy'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cm:ownable where cm:owner = 'bob'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT D.*, O.* FROM cmis:document AS D JOIN cm:ownable AS O ON D.cmis:objectId = O.cmis:objectId", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT D.*, O.* FROM cmis:document AS D JOIN cm:ownable AS O ON D.cmis:objectId = O.cmis:objectId", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis",
                "SELECT D.*, O.*, T.* FROM cmis:document AS D JOIN cm:ownable AS O ON D.cmis:objectId = O.cmis:objectId JOIN cm:titled AS T ON T.cmis:objectId = D.cmis:objectId",
                1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT D.*, O.* FROM cm:ownable O JOIN cmis:document D ON D.cmis:objectId = O.cmis:objectId", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT D.*, F.* FROM cmis:folder F JOIN cmis:document D ON D.cmis:objectId = F.cmis:objectId", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT O.*, T.* FROM cm:ownable O JOIN cm:titled T ON O.cmis:objectId = T.cmis:objectId", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "select o.*, t.* from cm:ownable o join cm:titled t on o.cmis:objectId = t.cmis:objectId", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "sElEcT o.*, T.* fRoM cm:ownable o JoIn cm:titled T oN o.cmis:objectId = T.cmis:objectId", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "select o.*, t.* from ( cm:ownable o join cm:titled t on o.cmis:objectId = t.cmis:objectId )", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis",
                "select o.*, t.* from ( cm:ownable o join cm:titled t on o.cmis:objectId = t.cmis:objectId  JOIN cmis:document AS D ON D.cmis:objectId = o.cmis:objectId  )", 1,
                null, null, null, null, null, (String) null);
        testQueryByHandler(
                report,
                core,
                "/cmis",
                "select o.*, t.* from ( cm:ownable o join cm:titled t on o.cmis:objectId = t.cmis:objectId JOIN cmis:document AS D ON D.cmis:objectId = o.cmis:objectId ) where o.cm:owner = 'andy' and t.cm:title = 'Alfresco tutorial' and CONTAINS(D, '\\'jumped\\'') and D.cmis:contentStreamLength <> 2",
                1, null, null, null, null, null, (String) null);
        testQueryByHandler(
                report,
                core,
                "/cmis",
                "select o.*, t.* from ( cm:ownable o join cm:titled t on o.cmis:objectId = t.cmis:objectId JOIN cmis:document AS D ON D.cmis:objectId = o.cmis:objectId ) where o.cm:owner = 'andy' and t.cm:title = 'Alfresco tutorial' and CONTAINS(D, 'jumped') and D.cmis:contentStreamLength <> 2",
                1, null, null, null, null, null, (String) null);

    }

    private void checkFTSConnectives(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("FTS Connectives", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document where contains('\\'one\\' OR \\'zebra\\'')", 10, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document where contains('\\'one\\' or \\'zebra\\'')", 10, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document where contains('\\'one\\' \\'zebra\\'')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document where contains('\\'one\\' and \\'zebra\\'')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document where contains('\\'one\\' or \\'zebra\\'')", 10, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document where contains('\\'one\\'  \\'zebra\\'')", 1, null, null, null, null, null, (String) null);

        // TODO: set default OR
    }

    private void checkLikeEscaping(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("Like Escaping", report);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'Alfresco Tutorial'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'Alfresco Tutoria_'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'Alfresco T_______'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'Alfresco T______\\_'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'Alfresco T%'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'Alfresco'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'Alfresco%'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'Alfresco T\\%'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'GG*GG'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE '__*__'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE '%*%'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'HH?HH'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE '__?__'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE '%?%'", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'AA%'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'AA\\%'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'A%'", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'a%'", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'A\\%'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'BB_'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'BB\\_'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'B__'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'B_\\_'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'B\\_\\_'", 0, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'CC\\\\'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmis:name FROM cmis:document WHERE cmis:name     LIKE 'DD\\''", 1, null, null, null, null, null, (String) null);
    }

    @SuppressWarnings("unused")
    private void addTypeTestData(SolrCore core, AlfrescoSolrDataModel dataModel, NodeRef folder00NodeRef, NodeRef rootNodeRef, NodeRef baseFolderNodeRef, Object baseFolderQName,
            Object folder00QName, Date date1) throws IOException
    {
        HashMap<QName, PropertyValue> content00Properties = new HashMap<QName, PropertyValue>();
        MLTextPropertyValue desc00 = new MLTextPropertyValue();
        desc00.addValue(Locale.ENGLISH, "Test One");
        desc00.addValue(Locale.US, "Test 1");
        content00Properties.put(ContentModel.PROP_DESCRIPTION, desc00);
        content00Properties.put(ContentModel.PROP_TITLE, desc00);
        content00Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Test One"));
        content00Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date1)));

        StringPropertyValue single = new StringPropertyValue("Un tokenised");
        content00Properties.put(singleTextUntokenised, single);
        content00Properties.put(singleTextTokenised, single);
        content00Properties.put(singleTextBoth, single);
        MultiPropertyValue multi = new MultiPropertyValue();
        multi.addValue(single);
        multi.addValue(new StringPropertyValue("two parts"));
        content00Properties.put(multipleTextUntokenised, multi);
        content00Properties.put(multipleTextTokenised, multi);
        content00Properties.put(multipleTextBoth, multi);
        content00Properties.put(singleMLTextUntokenised, makeMLText());
        content00Properties.put(singleMLTextTokenised, makeMLText());
        content00Properties.put(singleMLTextBoth, makeMLText());
        content00Properties.put(multipleMLTextUntokenised, makeMLTextMVP());
        content00Properties.put(multipleMLTextTokenised, makeMLTextMVP());
        content00Properties.put(multipleMLTextBoth, makeMLTextMVP());
        StringPropertyValue one = new StringPropertyValue("1");
        StringPropertyValue two = new StringPropertyValue("2");
        MultiPropertyValue multiDec = new MultiPropertyValue();
        multiDec.addValue(one);
        multiDec.addValue(new StringPropertyValue("1.1"));
        content00Properties.put(singleFloat, one);
        content00Properties.put(multipleFloat, multiDec);
        content00Properties.put(singleDouble, one);
        content00Properties.put(multipleDouble, multiDec);
        MultiPropertyValue multiInt = new MultiPropertyValue();
        multiInt.addValue(one);
        multiInt.addValue(two);
        content00Properties.put(singleInteger, one);
        content00Properties.put(multipleInteger, multiInt);
        content00Properties.put(singleLong, one);
        content00Properties.put(multipleLong, multiInt);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date date0 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date date2 = cal.getTime();
        StringPropertyValue d0 = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date0));
        StringPropertyValue d1 = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date1));
        StringPropertyValue d2 = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date2));
        MultiPropertyValue multiDate = new MultiPropertyValue();
        multiDate.addValue(d1);
        multiDate.addValue(d2);
        content00Properties.put(singleDate, d1);
        content00Properties.put(multipleDate, multiDate);
        content00Properties.put(singleDatetime, d1);
        content00Properties.put(multipleDatetime, multiDate);

        StringPropertyValue bTrue = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, true));
        StringPropertyValue bFalse = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, false));
        MultiPropertyValue multiBool = new MultiPropertyValue();
        multiBool.addValue(bTrue);
        multiBool.addValue(bFalse);

        content00Properties.put(singleBoolean, bTrue);
        content00Properties.put(multipleBoolean, multiBool);

        NodeRef content00NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
        QName content00QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Test One");
        ChildAssociationRef content00CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder00NodeRef, content00QName, content00NodeRef, true, 0);
        addNode(core, dataModel, 1, 100, 1, extendedContent, new QName[] { ContentModel.ASPECT_OWNABLE, ContentModel.ASPECT_TITLED }, content00Properties, null, "andy",
                new ChildAssociationRef[] { content00CAR }, new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef }, new String[] { "/"
                        + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + content00QName.toString() }, content00NodeRef, true);
    }

    private void addTypeSortTestData(SolrCore core, AlfrescoSolrDataModel dataModel, NodeRef folder00NodeRef, NodeRef rootNodeRef, NodeRef baseFolderNodeRef,
            Object baseFolderQName, Object folder00QName, Date date1) throws IOException
    {
        addSortableNull(core, dataModel, folder00NodeRef, rootNodeRef, baseFolderNodeRef, baseFolderQName, folder00QName, date1, "start", 0);
        for (int i = 0; i < 10; i++)
        {
            addSortableNode(core, dataModel, folder00NodeRef, rootNodeRef, baseFolderNodeRef, baseFolderQName, folder00QName, date1, i);
            if (i == 5)
            {
                addSortableNull(core, dataModel, folder00NodeRef, rootNodeRef, baseFolderNodeRef, baseFolderQName, folder00QName, date1, "mid", 1);
            }
        }

        addSortableNull(core, dataModel, folder00NodeRef, rootNodeRef, baseFolderNodeRef, baseFolderQName, folder00QName, date1, "end", 2);
    }

    private void addSortableNull(SolrCore core, AlfrescoSolrDataModel dataModel, NodeRef folder00NodeRef, NodeRef rootNodeRef, NodeRef baseFolderNodeRef, Object baseFolderQName,
            Object folder00QName, Date date1, String id, int offset) throws IOException
    {
        HashMap<QName, PropertyValue> content00Properties = new HashMap<QName, PropertyValue>();
        MLTextPropertyValue desc00 = new MLTextPropertyValue();
        desc00.addValue(Locale.ENGLISH, "Test null");
        content00Properties.put(ContentModel.PROP_DESCRIPTION, desc00);
        content00Properties.put(ContentModel.PROP_TITLE, desc00);
        content00Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Test null"));
        content00Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date1)));

        NodeRef content00NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
        QName content00QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Test null");
        ChildAssociationRef content00CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder00NodeRef, content00QName, content00NodeRef, true, 0);
        addNode(core, dataModel, 1, 200 + offset, 1, extendedContent, new QName[] { ContentModel.ASPECT_OWNABLE, ContentModel.ASPECT_TITLED }, content00Properties, null, "andy",
                new ChildAssociationRef[] { content00CAR }, new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef }, new String[] { "/"
                        + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + content00QName.toString() }, content00NodeRef, true);
    }

    private static String[] orderable = new String[] { "zero loons", "one banana", "two apples", "three fruit", "four lemurs", "five rats", "six badgers", "seven cards",
            "eight cabbages", "nine zebras", "ten lemons" };

    @SuppressWarnings("unused")
    private void addSortableNode(SolrCore core, AlfrescoSolrDataModel dataModel, NodeRef folder00NodeRef, NodeRef rootNodeRef, NodeRef baseFolderNodeRef, Object baseFolderQName,
            Object folder00QName, Date date1, int position) throws IOException
    {
        HashMap<QName, PropertyValue> content00Properties = new HashMap<QName, PropertyValue>();
        MLTextPropertyValue desc00 = new MLTextPropertyValue();
        desc00.addValue(Locale.ENGLISH, "Test " + position);
        content00Properties.put(ContentModel.PROP_DESCRIPTION, desc00);
        content00Properties.put(ContentModel.PROP_TITLE, desc00);
        content00Properties.put(ContentModel.PROP_NAME, new StringPropertyValue("Test " + position));
        content00Properties.put(ContentModel.PROP_CREATED, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date1)));

        StringPropertyValue single = new StringPropertyValue(orderable[position]);
        content00Properties.put(singleTextUntokenised, single);
        content00Properties.put(singleTextTokenised, single);
        content00Properties.put(singleTextBoth, single);
        MultiPropertyValue multi = new MultiPropertyValue();
        multi.addValue(single);
        multi.addValue(new StringPropertyValue(orderable[position + 1]));
        content00Properties.put(multipleTextUntokenised, multi);
        content00Properties.put(multipleTextTokenised, multi);
        content00Properties.put(multipleTextBoth, multi);
        content00Properties.put(singleMLTextUntokenised, makeMLText(position));
        content00Properties.put(singleMLTextTokenised, makeMLText(position));
        content00Properties.put(singleMLTextBoth, makeMLText(position));
        content00Properties.put(multipleMLTextUntokenised, makeMLTextMVP(position));
        content00Properties.put(multipleMLTextTokenised, makeMLTextMVP(position));
        content00Properties.put(multipleMLTextBoth, makeMLTextMVP());
        StringPropertyValue one = new StringPropertyValue("" + (1.1 * position));
        StringPropertyValue two = new StringPropertyValue("" + (2.2 * position));
        MultiPropertyValue multiDec = new MultiPropertyValue();
        multiDec.addValue(one);
        multiDec.addValue(two);
        content00Properties.put(singleFloat, one);
        content00Properties.put(multipleFloat, multiDec);
        content00Properties.put(singleDouble, one);
        content00Properties.put(multipleDouble, multiDec);
        one = new StringPropertyValue("" + (1 * position));
        two = new StringPropertyValue("" + (2 * position));
        MultiPropertyValue multiInt = new MultiPropertyValue();
        multiInt.addValue(one);
        multiInt.addValue(two);
        content00Properties.put(singleInteger, one);
        content00Properties.put(multipleInteger, multiInt);
        content00Properties.put(singleLong, one);
        content00Properties.put(multipleLong, multiInt);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date1);
        cal.add(Calendar.DAY_OF_MONTH, position);

        Date newdate1 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date date0 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date date2 = cal.getTime();
        StringPropertyValue d0 = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date0));
        StringPropertyValue d1 = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, newdate1));
        StringPropertyValue d2 = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, date2));
        MultiPropertyValue multiDate = new MultiPropertyValue();
        multiDate.addValue(d1);
        multiDate.addValue(d2);
        content00Properties.put(singleDate, d1);
        content00Properties.put(multipleDate, multiDate);
        content00Properties.put(singleDatetime, d1);
        content00Properties.put(multipleDatetime, multiDate);

        StringPropertyValue b = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, position % 2 == 0 ? true : false));
        StringPropertyValue bTrue = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, true));
        StringPropertyValue bFalse = new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, false));
        MultiPropertyValue multiBool = new MultiPropertyValue();
        multiBool.addValue(bTrue);
        multiBool.addValue(bFalse);

        content00Properties.put(singleBoolean, b);
        content00Properties.put(multipleBoolean, multiBool);

        NodeRef content00NodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
        QName content00QName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Test " + position);
        ChildAssociationRef content00CAR = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, folder00NodeRef, content00QName, content00NodeRef, true, 0);
        addNode(core, dataModel, 1, 1000 + position, 1, extendedContent, new QName[] { ContentModel.ASPECT_OWNABLE, ContentModel.ASPECT_TITLED }, content00Properties, null,
                "andy", new ChildAssociationRef[] { content00CAR }, new NodeRef[] { baseFolderNodeRef, rootNodeRef, folder00NodeRef },
                new String[] { "/" + baseFolderQName.toString() + "/" + folder00QName.toString() + "/" + content00QName.toString() }, content00NodeRef, true);
    }

    private void check_D_text(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("d:text", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document", 12, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth = 'Un tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth <> 'tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth LIKE 'U_ to%sed'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth NOT LIKE 't__eni%'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth IN ('Un tokenised', 'Monkey')", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth NOT IN ('Un tokenized')", 1, null, null, null, null, null,
                (String) null);
        //
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth < 'tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth < 'Un tokenised'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth < 'V'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth < 'U'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth <= 'tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth <= 'Un tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth <= 'V'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth <= 'U'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth > 'tokenised'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth > 'Un tokenised'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth > 'V'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth > 'U'", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth >= 'tokenised'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth >= 'Un tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth >= 'V'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextBoth >= 'U'", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised = 'Un tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised <> 'tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised LIKE 'U_ to%sed'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised NOT LIKE 't__eni%'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised IN ('Un tokenised', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised NOT IN ('Un tokenized')", 1, null, null, null, null,
                null, (String) null);
        //
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised < 'tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised < 'Un tokenised'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised < 'V'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised < 'U'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised <= 'tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised <= 'Un tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised <= 'V'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised <= 'U'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised > 'tokenised'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised > 'Un tokenised'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised > 'V'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised > 'U'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised >= 'tokenised'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised >= 'Un tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised >= 'V'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextUntokenised >= 'U'", 1, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextTokenised = 'tokenised'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextTokenised <> 'tokenized'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextTokenised LIKE 'to%sed'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextTokenised NOT LIKE 'Ut__eniz%'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextTokenised IN ('tokenised', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleTextTokenised NOT IN ('tokenized')", 1, null, null, null, null,
                null, (String) null);

        // d:text single by alias

        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextBoth as alias FROM cmistest:extendedContent as T WHERE alias = 'Un tokenised'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextBoth as alias FROM cmistest:extendedContent as T WHERE alias <> 'tokenised'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextBoth as alias FROM cmistest:extendedContent as T WHERE alias LIKE 'U_ to%sed'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextBoth as alias FROM cmistest:extendedContent as T WHERE alias NOT LIKE 't__eni%'", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextBoth as alias FROM cmistest:extendedContent as T WHERE alias IN ('Un tokenised', 'Monkey')", 1,
                null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextBoth as alias FROM cmistest:extendedContent as T WHERE alias NOT IN ('Un tokenized')", 1, null,
                null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextUntokenised as alias FROM cmistest:extendedContent as T WHERE alias = 'Un tokenised'", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextUntokenised as alias FROM cmistest:extendedContent as T WHERE alias <> 'tokenised'", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextUntokenised as alias FROM cmistest:extendedContent as T WHERE alias LIKE 'U_ to%sed'", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextUntokenised as alias FROM cmistest:extendedContent as T WHERE alias NOT LIKE 't__eni%'", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextUntokenised as alias FROM cmistest:extendedContent as T WHERE alias IN ('Un tokenised', 'Monkey')",
                1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:singleTextUntokenised as alias FROM cmistest:extendedContent as T WHERE alias NOT IN ('Un tokenized')", 1,
                null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleTextTokenised as alias FROM cmistest:extendedContent WHERE alias = 'tokenised'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleTextTokenised as alias FROM cmistest:extendedContent WHERE alias <> 'tokenized'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleTextTokenised as alias FROM cmistest:extendedContent WHERE alias LIKE 'to%sed'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleTextTokenised as alias FROM cmistest:extendedContent WHERE alias NOT LIKE 'Ut__eniz%'", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleTextTokenised as alias FROM cmistest:extendedContent WHERE alias IN ('tokenised', 'Monkey')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleTextTokenised as alias FROM cmistest:extendedContent WHERE alias NOT IN ('tokenized')", 1, null, null,
                null, null, null, (String) null);

        // d:text multiple

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'Un tokenised' =  ANY cmistest:multipleTextBoth ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleTextBoth IN ('Un tokenised', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleTextBoth NOT IN ('Un tokenized')", 1, null, null, null, null,
                null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'Un tokenised' =  ANY cmistest:multipleTextUntokenised ", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleTextUntokenised IN ('Un tokenised', 'Monkey')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleTextUntokenised NOT IN ('Un tokenized')", 1, null, null, null,
                null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'tokenised' =  ANY cmistest:multipleTextTokenised ", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleTextTokenised IN ('tokenised', 'Monkey')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleTextTokenised NOT IN ('tokenized')", 1, null, null, null,
                null, null, (String) null);

        // d:text multiple by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleTextBoth as alias FROM cmistest:extendedContent WHERE 'Un tokenised' =  ANY alias ", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleTextBoth as alias FROM cmistest:extendedContent WHERE ANY alias IN ('Un tokenised', 'Monkey')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleTextBoth as alias FROM cmistest:extendedContent WHERE ANY alias NOT IN ('Un tokenized')", 1, null, null,
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleTextUntokenised alias FROM cmistest:extendedContent WHERE 'Un tokenised' =  ANY alias ", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleTextUntokenised alias FROM cmistest:extendedContent WHERE ANY alias IN ('Un tokenised', 'Monkey')", 1,
                null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleTextUntokenised alias FROM cmistest:extendedContent WHERE ANY alias NOT IN ('Un tokenized')", 1, null,
                null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:multipleTextTokenised alias FROM cmistest:extendedContent T WHERE 'tokenised' =  ANY alias ", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:multipleTextTokenised alias FROM cmistest:extendedContent T WHERE ANY alias IN ('tokenised', 'Monkey')", 1,
                null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT T.cmistest:multipleTextTokenised alias FROM cmistest:extendedContent T WHERE ANY alias NOT IN ('tokenized')", 1, null,
                null, null, null, null, (String) null);
    }

    private void check_locale(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("CMIS locale", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth = 'AAAA BBBB'", 1, null, null, Locale.ENGLISH, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth = 'AAAA BBBB'", 1, null, null, Locale.FRENCH, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth = 'CCCC DDDD'", 1, null, null, Locale.ENGLISH, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth = 'CCCC DDDD'", 1, null, null, Locale.FRENCH, null, null,
                (String) null);

    }

    private void check_D_mltext(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("d:mltext", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);
        // d:mltext single

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth = 'AAAA BBBB'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth = 'AAAA'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth = '%AAAA'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth = '%AAA'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth = 'BBBB'", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth = 'CCCC DDDD'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth <> 'EEEE FFFF'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth LIKE 'AAA_ B%'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth LIKE 'CCC_ D%'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth NOT LIKE 'B%'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth NOT LIKE 'D%'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth IN ('AAAA BBBB', 'Monkey')", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth IN ('CCCC DDDD', 'Monkey')", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextBoth NOT IN ('EEEE FFFF')", 1, null, null, null, null, null,
                (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextUntokenised = 'AAAA BBBB'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextUntokenised = 'CCCC DDDD'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextUntokenised <> 'EEEE FFFF'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextUntokenised LIKE 'AAA_ B%'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextUntokenised LIKE 'CCC_ D%'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextUntokenised NOT LIKE 'B%'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextUntokenised NOT LIKE 'D%'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextUntokenised IN ('AAAA BBBB', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextUntokenised IN ('CCCC DDDD', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextUntokenised NOT IN ('EEEE FFFF')", 1, null, null, null, null,
                null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised = 'AAAA'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised = 'BBBB'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised = 'CCCC'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised = 'DDDD'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised <> 'EEEE'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised LIKE 'A%'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised LIKE '_B__'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised LIKE '%C'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised LIKE 'D%D'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised NOT LIKE 'CCCC_'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised IN ('AAAA', 'Monkey')", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised IN ('BBBB', 'Monkey')", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised IN ('CCCC', 'Monkey')", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised IN ('DDDD', 'Monkey')", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleMLTextTokenised NOT IN ('EEEE')", 1, null, null, null, null, null,
                (String) null);

        // d:mltext single by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias = 'AAAA BBBB'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias = 'AAAA'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias = 'BBBB'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias = 'CCCC DDDD'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias <> 'EEEE FFFF'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias LIKE 'AAA_ B%'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias LIKE 'CCC_ D%'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias NOT LIKE 'B%'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias NOT LIKE 'D%'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias IN ('AAAA BBBB', 'Monkey')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias IN ('CCCC DDDD', 'Monkey')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextBoth as alias FROM cmistest:extendedContent WHERE alias NOT IN ('EEEE FFFF')", 1, null, null, null,
                null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextUntokenised as alias FROM cmistest:extendedContent WHERE alias = 'AAAA BBBB'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextUntokenised as alias FROM cmistest:extendedContent WHERE alias = 'CCCC DDDD'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextUntokenised as alias FROM cmistest:extendedContent WHERE alias <> 'EEEE FFFF'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextUntokenised as alias FROM cmistest:extendedContent WHERE alias LIKE 'AAA_ B%'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextUntokenised as alias FROM cmistest:extendedContent WHERE alias LIKE 'CCC_ D%'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextUntokenised as alias FROM cmistest:extendedContent WHERE alias NOT LIKE 'B%'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextUntokenised as alias FROM cmistest:extendedContent WHERE alias NOT LIKE 'D%'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextUntokenised as alias FROM cmistest:extendedContent WHERE alias IN ('AAAA BBBB', 'Monkey')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextUntokenised as alias FROM cmistest:extendedContent WHERE alias IN ('CCCC DDDD', 'Monkey')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextUntokenised as alias FROM cmistest:extendedContent WHERE alias NOT IN ('EEEE FFFF')", 1, null, null,
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias = 'AAAA'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias = 'BBBB'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias = 'CCCC'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias = 'DDDD'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias <> 'EEEE'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias LIKE 'A%'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias LIKE '_B__'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias LIKE '%C'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias LIKE 'D%D'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias NOT LIKE 'CCCC_'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias IN ('AAAA', 'Monkey')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias IN ('BBBB', 'Monkey')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias IN ('CCCC', 'Monkey')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias IN ('DDDD', 'Monkey')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleMLTextTokenised as alias FROM cmistest:extendedContent WHERE alias NOT IN ('EEEE')", 1, null, null, null,
                null, null, (String) null);

        // d:mltext multiple

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'AAAA BBBB' =  ANY cmistest:multipleMLTextBoth ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'CCCC DDDD' =  ANY cmistest:multipleMLTextBoth ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextBoth IN ('AAAA BBBB', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextBoth IN ('CCCC DDDD', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextBoth NOT IN ('EEEE FFFF')", 1, null, null, null, null,
                null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'AAAA BBBB' =  ANY cmistest:multipleMLTextUntokenised ", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'CCCC DDDD' =  ANY cmistest:multipleMLTextUntokenised ", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextUntokenised IN ('AAAA BBBB', 'Monkey')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextUntokenised IN ('CCCC DDDD', 'Monkey')", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextUntokenised NOT IN ('EEEE FFFF')", 1, null, null, null,
                null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'AAAA' =  ANY cmistest:multipleMLTextTokenised ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'BBBB' =  ANY cmistest:multipleMLTextTokenised ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'CCCC' =  ANY cmistest:multipleMLTextTokenised ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE 'DDDD' =  ANY cmistest:multipleMLTextTokenised ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextTokenised IN ('AAAA', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextTokenised IN ('BBBB', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextTokenised IN ('CCCC', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextTokenised IN ('DDDD', 'Monkey')", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleMLTextTokenised NOT IN ('EEEE')", 1, null, null, null, null,
                null, (String) null);

        // d:mltext multiple by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextBoth alias FROM cmistest:extendedContent WHERE 'AAAA BBBB' =  ANY alias ", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextBoth alias FROM cmistest:extendedContent WHERE 'CCCC DDDD' =  ANY alias ", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextBoth alias FROM cmistest:extendedContent WHERE ANY alias IN ('AAAA BBBB', 'Monkey')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextBoth alias FROM cmistest:extendedContent WHERE ANY alias IN ('CCCC DDDD', 'Monkey')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextBoth alias FROM cmistest:extendedContent WHERE ANY alias NOT IN ('EEEE FFFF')", 1, null, null,
                null, null, null, (String) null);
        ;
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextUntokenised alias FROM cmistest:extendedContent WHERE 'AAAA BBBB' =  ANY alias ", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextUntokenised alias FROM cmistest:extendedContent WHERE 'CCCC DDDD' =  ANY alias ", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextUntokenised alias FROM cmistest:extendedContent WHERE ANY alias IN ('AAAA BBBB', 'Monkey')", 1,
                null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextUntokenised alias FROM cmistest:extendedContent WHERE ANY alias IN ('CCCC DDDD', 'Monkey')", 1,
                null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextUntokenised alias FROM cmistest:extendedContent WHERE ANY alias NOT IN ('EEEE FFFF')", 1, null,
                null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextTokenised alias FROM cmistest:extendedContent WHERE 'AAAA' =  ANY alias ", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextTokenised alias FROM cmistest:extendedContent WHERE 'BBBB' =  ANY alias ", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextTokenised alias FROM cmistest:extendedContent WHERE 'CCCC' =  ANY alias ", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextTokenised alias FROM cmistest:extendedContent WHERE 'DDDD' =  ANY alias ", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextTokenised alias FROM cmistest:extendedContent WHERE ANY alias IN ('AAAA', 'Monkey')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextTokenised alias FROM cmistest:extendedContent WHERE ANY alias IN ('BBBB', 'Monkey')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextTokenised alias FROM cmistest:extendedContent WHERE ANY alias IN ('CCCC', 'Monkey')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextTokenised alias FROM cmistest:extendedContent WHERE ANY alias IN ('DDDD', 'Monkey')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleMLTextTokenised alias FROM cmistest:extendedContent WHERE ANY alias NOT IN ('EEEE')", 1, null, null,
                null, null, null, (String) null);
    }

    private void check_D_float(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("d:float", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);

        // d:float single

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat = 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat = 1.1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat <> 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat <> 1.1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat < 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat < 1.1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat <= 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat <= 1.1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat > 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat > 0.9", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat >= 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat >= 0.9", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat IN (1, 2)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleFloat NOT IN (1.1)", 1, null, null, null, null, null, (String) null);

        // d:float single by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias = 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias = 1.1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias <> 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias <> 1.1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias < 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias < 1.1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias <= 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias <= 1.1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias > 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias > 0.9", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias >= 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias >= 0.9", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias IN (1, 2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleFloat as alias FROM cmistest:extendedContent WHERE alias NOT IN (1.1)", 1, null, null, null, null, null,
                (String) null);

        // d:float multiple

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE '1' =  ANY cmistest:multipleFloat ", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE '1.1' =  ANY cmistest:multipleFloat ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleFloat IN (1, 2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleFloat IN (1.1, 2.2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleFloat NOT IN (1.1, 2.2)", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleFloat NOT IN (1.3, 2.3)", 1, null, null, null, null, null,
                (String) null);

        // d:float multiple by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleFloat as alias FROM cmistest:extendedContent WHERE '1' =  ANY alias ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleFloat as alias FROM cmistest:extendedContent WHERE '1.1' =  ANY alias ", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleFloat as alias FROM cmistest:extendedContent WHERE ANY alias IN (1, 2)", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleFloat as alias FROM cmistest:extendedContent WHERE ANY alias IN (1.1, 2.2)", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleFloat as alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (1.1, 2.2)", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleFloat as alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (1.3, 2.3)", 1, null, null, null,
                null, null, (String) null);
    }

    private void check_D_double(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("d:double", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);

        // d:double single

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble = 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble = 1.1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble <> 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble <> 1.1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble < 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble < 1.1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble <= 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble <= 1.1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble > 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble > 0.9", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble >= 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble >= 0.9", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble IN (1, 2)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDouble NOT IN (1.1)", 1, null, null, null, null, null, (String) null);

        // d:double single by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias = 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias = 1.1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias <> 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias <> 1.1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias < 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias < 1.1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias <= 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias <= 1.1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias > 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias > 0.9", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias >= 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias >= 0.9", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias IN (1, 2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDouble alias FROM cmistest:extendedContent WHERE alias NOT IN (1.1)", 1, null, null, null, null, null,
                (String) null);

        // d:double multiple

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE '1' =  ANY cmistest:multipleDouble ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE '1.1' =  ANY cmistest:multipleDouble ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDouble IN (1, 2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDouble IN (1.1, 2.2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDouble NOT IN (1.1, 2.2)", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDouble NOT IN (1.3, 2.3)", 1, null, null, null, null, null,
                (String) null);

        // d:double multiple by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDouble alias FROM cmistest:extendedContent WHERE '1' =  ANY alias ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDouble alias FROM cmistest:extendedContent WHERE '1.1' =  ANY alias ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDouble alias FROM cmistest:extendedContent WHERE ANY alias IN (1, 2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDouble alias FROM cmistest:extendedContent WHERE ANY alias IN (1.1, 2.2)", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDouble alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (1.1, 2.2)", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDouble alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (1.3, 2.3)", 1, null, null, null,
                null, null, (String) null);
    }

    private void check_D_int(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("d:int", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);

        // d:int single

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger = 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger = 2", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger <> 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger <> 2", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger < 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger < 2", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger <= 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger <= 2", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger > 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger > 0", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger >= 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger >= 0", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger IN (1, 2)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleInteger NOT IN (2)", 1, null, null, null, null, null, (String) null);

        // d:int single by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias = 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias = 2", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias <> 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias <> 2", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias < 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias < 2", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias <= 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias <= 2", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias > 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias > 0", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias >= 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias >= 0", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias IN (1, 2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleInteger alias FROM cmistest:extendedContent WHERE alias NOT IN (2)", 1, null, null, null, null, null,
                (String) null);

        // d:int multiple

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE '1' =  ANY cmistest:multipleInteger ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE '2' =  ANY cmistest:multipleInteger ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleInteger IN (1, 2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleInteger IN (2, 3)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleInteger NOT IN (1, 2)", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleInteger NOT IN (2, 3)", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleInteger NOT IN (3, 4)", 1, null, null, null, null, null,
                (String) null);

        // d:int multiple by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleInteger as alias FROM cmistest:extendedContent WHERE '1' =  ANY alias ", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleInteger as alias FROM cmistest:extendedContent WHERE '2' =  ANY alias ", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleInteger as alias FROM cmistest:extendedContent WHERE ANY alias IN (1, 2)", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleInteger as alias FROM cmistest:extendedContent WHERE ANY alias IN (2, 3)", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleInteger as alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (1, 2)", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleInteger as alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (2, 3)", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleInteger as alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (3, 4)", 1, null, null, null,
                null, null, (String) null);
    }

    private void check_D_long(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("d:long", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);

        // d:long single

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong = 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong = 2", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong <> 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong <> 2", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong < 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong < 2", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong <= 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong <= 2", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong > 1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong > 0", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong >= 1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong >= 0", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong IN (1, 2)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleLong NOT IN (2)", 1, null, null, null, null, null, (String) null);

        // d:long single by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias = 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias = 2", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias <> 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias <> 2", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias < 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias < 2", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias <= 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias <= 2", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias > 1", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias > 0", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias >= 1", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias >= 0", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias IN (1, 2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleLong as alias FROM cmistest:extendedContent WHERE alias NOT IN (2)", 1, null, null, null, null, null,
                (String) null);

        // d:long multiple

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE '1' =  ANY cmistest:multipleLong ", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE '2' =  ANY cmistest:multipleLong ", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleLong IN (1, 2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleLong IN (2, 3)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleLong NOT IN (1, 2)", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleLong NOT IN (2, 3)", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleLong NOT IN (3, 4)", 1, null, null, null, null, null,
                (String) null);

        // d:long multiple by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleLong alias FROM cmistest:extendedContent WHERE '1' =  ANY alias ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleLong alias FROM cmistest:extendedContent WHERE '2' =  ANY alias ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleLong alias FROM cmistest:extendedContent WHERE ANY alias IN (1, 2)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleLong alias FROM cmistest:extendedContent WHERE ANY alias IN (2, 3)", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleLong alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (1, 2)", 0, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleLong alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (2, 3)", 0, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleLong alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (3, 4)", 1, null, null, null, null,
                null, (String) null);
    }

    private void check_D_date(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, Date date1) throws IOException
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date date0 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date date2 = cal.getTime();

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("d:date", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);

        // d:date single

        String d0 = ISO8601DateFormat.format(date0);
        String d1 = ISO8601DateFormat.format(date1);
        String d2 = ISO8601DateFormat.format(date2);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate = TIMESTAMP '" + d1 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate = TIMESTAMP '" + d2 + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate <> TIMESTAMP '" + d1 + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate <> TIMESTAMP '" + d2 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate < TIMESTAMP '" + d1 + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate < TIMESTAMP '" + d2 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate <= TIMESTAMP '" + d1 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate <= TIMESTAMP '" + d2 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate > TIMESTAMP '" + d1 + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate > TIMESTAMP '" + d0 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate >= TIMESTAMP '" + d1 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate >= TIMESTAMP '" + d0 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate IN (TIMESTAMP '" + d0 + "' ,TIMESTAMP '" + d1 + "')", 1, null,
                null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDate NOT IN (TIMESTAMP '" + d2 + "')", 1, null, null, null, null,
                null, (String) null);

        // d:date single by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias = TIMESTAMP '" + d1 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias = TIMESTAMP '" + d2 + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias <> TIMESTAMP '" + d1 + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias <> TIMESTAMP '" + d2 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias < TIMESTAMP '" + d1 + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias < TIMESTAMP '" + d2 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias <= TIMESTAMP '" + d1 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias <= TIMESTAMP '" + d2 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias > TIMESTAMP '" + d1 + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias > TIMESTAMP '" + d0 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias >= TIMESTAMP '" + d1 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias >= TIMESTAMP '" + d0 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias IN (TIMESTAMP '"
                + d0 + "' ,TIMESTAMP '" + d1 + "')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDate as alias FROM cmistest:extendedContent WHERE alias NOT IN (TIMESTAMP '" + d2 + "')", 1, null, null,
                null, null, null, (String) null);

        // d:date multiple

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE TIMESTAMP '" + d1 + "' =  ANY cmistest:multipleDate ", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE TIMESTAMP '" + d2 + "' =  ANY cmistest:multipleDate ", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDate IN (TIMESTAMP '" + d1 + "', TIMESTAMP '" + d2 + "')", 1,
                null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDate IN (TIMESTAMP '" + d2 + "', TIMESTAMP '" + d0 + "')", 1,
                null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDate NOT IN (TIMESTAMP '" + d0 + "', TIMESTAMP '" + d1 + "')",
                0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDate NOT IN (TIMESTAMP '" + d1 + "', TIMESTAMP '" + d2 + "')",
                0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDate NOT IN (TIMESTAMP '" + d0 + "')", 1, null, null, null,
                null, null, (String) null);

        // d:date multiple by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDate alias FROM cmistest:extendedContent WHERE TIMESTAMP '" + d1 + "' =  ANY alias ", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDate alias FROM cmistest:extendedContent WHERE TIMESTAMP '" + d2 + "' =  ANY alias ", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDate alias FROM cmistest:extendedContent WHERE ANY alias IN (TIMESTAMP '"
                + d1 + "', TIMESTAMP '" + d2 + "')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDate alias FROM cmistest:extendedContent WHERE ANY alias IN (TIMESTAMP '"
                + d2 + "', TIMESTAMP '" + d0 + "')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDate alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (TIMESTAMP '"
                + d0 + "', TIMESTAMP '" + d1 + "')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDate alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (TIMESTAMP '"
                + d1 + "', TIMESTAMP '" + d2 + "')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDate alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (TIMESTAMP '" + d0 + "')", 1, null,
                null, null, null, null, (String) null);

    }

    private void check_D_datetime(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, Date date1) throws IOException
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date date0 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date date2 = cal.getTime();

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("d:datetime", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);

        String d0 = ISO8601DateFormat.format(date0);
        String d1 = ISO8601DateFormat.format(date1);
        String d2 = ISO8601DateFormat.format(date2);

        // d:datetime single

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime = TIMESTAMP '" + d1 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime = TIMESTAMP '" + d2 + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime <> TIMESTAMP '" + d1 + "'", 0, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime <> TIMESTAMP '" + d2 + "'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime < TIMESTAMP '" + d1 + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime < TIMESTAMP '" + d2 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime <= TIMESTAMP '" + d1 + "'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime <= TIMESTAMP '" + d2 + "'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime > TIMESTAMP '" + d1 + "'", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime > TIMESTAMP '" + d0 + "'", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime >= TIMESTAMP '" + d1 + "'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime >= TIMESTAMP '" + d0 + "'", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime IN (TIMESTAMP '" + d0 + "' ,TIMESTAMP '" + d1 + "')", 1,
                null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleDatetime NOT IN (TIMESTAMP '" + d2 + "')", 1, null, null, null,
                null, null, (String) null);

        // d:datetime single by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias = TIMESTAMP '" + d1 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias = TIMESTAMP '" + d2 + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias <> TIMESTAMP '" + d1 + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias <> TIMESTAMP '" + d2 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias < TIMESTAMP '" + d1 + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias < TIMESTAMP '" + d2 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias <= TIMESTAMP '" + d1 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias <= TIMESTAMP '" + d2 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias > TIMESTAMP '" + d1 + "'", 0, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias > TIMESTAMP '" + d0 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias >= TIMESTAMP '" + d1 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias >= TIMESTAMP '" + d0 + "'", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias IN (TIMESTAMP '"
                + d0 + "' ,TIMESTAMP '" + d1 + "')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleDatetime alias FROM cmistest:extendedContent WHERE alias NOT IN (TIMESTAMP '" + d2 + "')", 1, null, null,
                null, null, null, (String) null);

        // d:date multiple

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE TIMESTAMP '" + d1 + "' =  ANY cmistest:multipleDatetime ", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE TIMESTAMP '" + d2 + "' =  ANY cmistest:multipleDatetime ", 1, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDatetime IN (TIMESTAMP '" + d1 + "', TIMESTAMP '" + d2 + "')",
                1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDatetime IN (TIMESTAMP '" + d2 + "', TIMESTAMP '" + d0 + "')",
                1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDatetime NOT IN (TIMESTAMP '"
                + d0 + "', TIMESTAMP '" + d1 + "')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDatetime NOT IN (TIMESTAMP '"
                + d1 + "', TIMESTAMP '" + d2 + "')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE ANY cmistest:multipleDatetime NOT IN (TIMESTAMP '" + d0 + "')", 1, null, null,
                null, null, null, (String) null);

        // d:date multiple by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDatetime alias FROM cmistest:extendedContent WHERE TIMESTAMP '" + d1 + "' =  ANY alias ", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDatetime alias FROM cmistest:extendedContent WHERE TIMESTAMP '" + d2 + "' =  ANY alias ", 1, null, null,
                null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDatetime alias FROM cmistest:extendedContent WHERE ANY alias IN (TIMESTAMP '"
                + d1 + "', TIMESTAMP '" + d2 + "')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDatetime alias FROM cmistest:extendedContent WHERE ANY alias IN (TIMESTAMP '"
                + d2 + "', TIMESTAMP '" + d0 + "')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDatetime alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (TIMESTAMP '"
                + d0 + "', TIMESTAMP '" + d1 + "')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDatetime alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (TIMESTAMP '"
                + d1 + "', TIMESTAMP '" + d2 + "')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleDatetime alias FROM cmistest:extendedContent WHERE ANY alias NOT IN (TIMESTAMP '" + d0 + "')", 1, null,
                null, null, null, null, (String) null);

    }

    private void check_D_boolean(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("d:boolean", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);

        // d:boolean single

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleBoolean = TRUE", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleBoolean = true", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleBoolean = FALSE", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleBoolean = false", 0, null, null, null, null, null, (String) null);
        // not strictly compliant...
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE cmistest:singleBoolean = TRue", 1, null, null, null, null, null, (String) null);
        // d:boolean single by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleBoolean alias FROM cmistest:extendedContent WHERE alias = TRUE", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleBoolean alias FROM cmistest:extendedContent WHERE alias = true", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleBoolean alias FROM cmistest:extendedContent WHERE alias = FALSE", 0, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleBoolean alias FROM cmistest:extendedContent WHERE alias = false", 0, null, null, null, null, null,
                (String) null);
        // not strictly compliant...
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:singleBoolean alias FROM cmistest:extendedContent WHERE alias = TRue", 1, null, null, null, null, null,
                (String) null);

        // d:boolean multiple

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE '1' =  ANY cmistest:multipleBoolean ", 1, null, null, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent WHERE '2' =  ANY cmistest:multipleBoolean ", 1, null, null, null, null, null,
                (String) null);

        // d:boolean multiple by alias

        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleBoolean as alias FROM cmistest:extendedContent WHERE '1' =  ANY alias ", 1, null, null, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT cmistest:multipleBoolean as alias FROM cmistest:extendedContent WHERE '2' =  ANY alias ", 1, null, null, null, null,
                null, (String) null);
    }

    private void check_contains_syntax(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("CMIS contains syntax", report);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmistest:extendedContent", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('one')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('-quick')", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick brown fox')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick one')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick -one')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('-quick one')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('-quick -one')", 10, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('fox brown quick')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick OR one')", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick OR -one')", 11, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('-quick OR -one')", 12, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('\\'quick brown fox\\'')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('\\'fox brown quick\\'')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('\\'quick brown fox\\' one')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('\\'quick brown fox\\' -one')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('-\\'quick brown fox\\' one')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('-\\'quick brown fox\\' -one')", 10, null, null, null, null, null, (String) null);

        // escaping
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:folder WHERE CONTAINS('cmis:name:\\'Folder 9\\\\\\'\\'')", 1, null, null, null, null, null, (String) null);

        // precedence
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick OR brown one')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick OR brown AND one')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick OR (brown AND one)')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('(quick OR brown) AND one')", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick OR brown OR one')", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document WHERE CONTAINS('quick OR brown one')", 1, null, null, null, null, null, (String) null);
    }

    private void check_order(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        rsp.add("CMIS order", report);

        int[] asc = new int[] { 200, 201, 202, 1008, 1005, 1004, 1009, 1001, 1007, 1006, 1003, 1002, 100, 1000 };
        int[] desc = new int[] { 1000, 100, 1002, 1003, 1006, 1007, 1001, 1009, 1004, 1005, 1008, 200, 201, 202 };

        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleTextUntokenised", asc, desc);
        // checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleTextTokenised");
        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleTextBoth", asc, desc);

        // testOrderablePropertyFail("test:multipleTextUntokenised");
        // testOrderablePropertyFail("test:multipleTextTokenised");
        // testOrderablePropertyFail("test:multipleTextBoth");

        asc = new int[] { 200, 201, 202, 1009, 100, 1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008 };
        desc = new int[] { 1008, 1007, 1006, 1005, 1004, 1003, 1002, 1001, 100, 1000, 1009, 200, 201, 202 };

        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleMLTextUntokenised", asc, desc);
        // testOrderablePropertyFail("cmistest:singleMLTextTokenised");
        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleMLTextBoth", asc, desc);

        // testOrderablePropertyFail("cmistest:multipleMLTextUntokenised");
        // testOrderablePropertyFail("cmistest:multipleMLTextTokenised");
        // testOrderablePropertyFail("cmistest:multipleMLTextBoth");

        asc = new int[] { 200, 201, 202, 1000, 100, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009 };
        desc = new int[] { 1009, 1008, 1007, 1006, 1005, 1004, 1003, 1002, 1001, 100, 1000, 200, 201, 202 };

        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleFloat", asc, desc);
        // testOrderablePropertyFail("cmistest:multipleFloat");

        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleDouble", asc, desc);
        // testOrderablePropertyFail("cmistest:multipleDouble");

        asc = new int[] { 200, 201, 202, 1000, 100, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009 };
        desc = new int[] { 1009, 1008, 1007, 1006, 1005, 1004, 1003, 1002, 100, 1001, 1000, 200, 201, 202 };

        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleInteger", asc, desc);
        // testOrderablePropertyFail("cmistest:multipleInteger");

        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleLong", asc, desc);
        // testOrderablePropertyFail("cmistest:multipleLong");

        asc = new int[] { 200, 201, 202, 100, 1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009 };
        desc = new int[] { 1009, 1008, 1007, 1006, 1005, 1004, 1003, 1002, 1001, 100, 1000, 200, 201, 202 };

        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleDate", asc, desc);
        // testOrderablePropertyFail("cmistest:multipleDate");

        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleDatetime", asc, desc);
        // testOrderablePropertyFail("cmistest:multipleDatetime");

        asc = new int[] { 200, 201, 202, 1001, 1003, 1005, 1007, 1009, 100, 1000, 1002, 1004, 1006, 1008 };
        desc = new int[] { 100, 1000, 1002, 1004, 1006, 1008, 1001, 1003, 1005, 1007, 1009, 200, 201, 202 };

        checkOrderableProperty(rsp, core, dataModel, report, "cmistest:singleBoolean", asc, desc);
        // testOrderablePropertyFail("cmistest:multipleBoolean");

    }

    private void checkOrderableProperty(SolrQueryResponse rsp, SolrCore core, AlfrescoSolrDataModel dataModel, NamedList<Object> report, String propertyQueryName, int[] asc,
            int[] desc) throws IOException
    {
        testQueryByHandler(report, core, "/cmis", "SELECT " + propertyQueryName + " FROM cmistest:extendedContent ORDER BY " + propertyQueryName + " ASC", 14, null, asc, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT " + propertyQueryName + " FROM cmistest:extendedContent ORDER BY " + propertyQueryName + " DESC", 14, null, desc, null,
                null, null, (String) null);
    }

    private static String[] mlOrderable_en = new String[] { "AAAA BBBB", "EEEE FFFF", "II", "KK", "MM", "OO", "QQ", "SS", "UU", "AA", "CC" };

    private static String[] mlOrderable_fr = new String[] { "CCCC DDDD", "GGGG HHHH", "JJ", "LL", "NN", "PP", "RR", "TT", "VV", "BB", "DD" };

    private MLTextPropertyValue makeMLText()
    {
        return makeMLText(0);
    }

    private MLTextPropertyValue makeMLText(int position)
    {
        MLTextPropertyValue ml = new MLTextPropertyValue();
        ml.addValue(Locale.ENGLISH, mlOrderable_en[position]);
        ml.addValue(Locale.FRENCH, mlOrderable_fr[position]);
        return ml;
    }

    private MultiPropertyValue makeMLTextMVP()
    {
        return makeMLTextMVP(0);
    }

    private MultiPropertyValue makeMLTextMVP(int position)
    {
        MLTextPropertyValue m1 = new MLTextPropertyValue();
        m1.addValue(Locale.ENGLISH, mlOrderable_en[position]);
        MLTextPropertyValue m2 = new MLTextPropertyValue();
        m2.addValue(Locale.FRENCH, mlOrderable_fr[position]);
        MultiPropertyValue answer = new MultiPropertyValue();
        answer.addValue(m1);
        answer.addValue(m2);
        return answer;
    }

    /**
     * @param before
     * @param core
     * @param dataModel
     * @throws IOException
     */
    private void testAFTS(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("AFS", report);

        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "DBID desc", new int[] { 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "\"lazy\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy and dog", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "-lazy and -dog", 15, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy and -dog", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "|lazy and |dog", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "|eager and |dog", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "|lazy and |wolf", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "|eager and |wolf", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "-lazy or -dog", 15, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "-lazy or -wolf", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -wolf", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy dog", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy and not dog", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy not dog", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy and !dog", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy !dog", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy and -dog", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy -dog", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:\"lazy\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm_content:\"lazy\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "=cm_content:\"lazy\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "~cm_content:\"lazy\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:big OR cm:content:lazy", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:big AND cm:content:lazy", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "{http://www.alfresco.org/model/content/1.0}content:\"lazy\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "=lazy", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "@cm:content:big OR @cm:content:lazy", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "@cm:content:big AND @cm:content:lazy", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "@{http://www.alfresco.org/model/content/1.0}content:\"lazy\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "~@cm:content:big OR ~@cm:content:lazy", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "brown * quick", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "brown * dog", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "brown * dog", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "brown *(0) dog", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "brown *(1) dog", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "brown *(2) dog", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "brown *(3) dog", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "brown *(4) dog", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "brown *(5) dog", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "brown *(6) dog", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(\"lazy\")", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(lazy and dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(-lazy and -dog)", 15, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(-lazy and dog)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(lazy and -dog)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(|lazy and |dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(|eager and |dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(|lazy and |wolf)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(|eager and |wolf)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(-lazy or -dog)", 15, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(-eager or -dog)", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(-lazy or -wolf)", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(-eager or -wolf)", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(lazy dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(lazy and not dog)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(lazy not dog)", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(lazy and !dog)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(lazy !dog)", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(lazy and -dog)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(lazy -dog)", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm_content:(\"lazy\")", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(big OR lazy)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(big AND lazy)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "{http://www.alfresco.org/model/content/1.0}content:(\"lazy\")", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(=lazy)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "@cm:content:(big) OR @cm:content:(lazy)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "@cm:content:(big) AND @cm:content:(lazy)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "@{http://www.alfresco.org/model/content/1.0}content:(\"lazy\")", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "@cm:content:(~big OR ~lazy)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown * quick)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown * dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown * dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown *(0) dog)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown *(1) dog)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown *(2) dog)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown *(3) dog)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown *(4) dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown *(5) dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown *(6) dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content.mimetype:\"text/plain\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm_content.mimetype:\"text/plain\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "@cm:content.mimetype:\"text/plain\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "@cm_content.mimetype:\"text/plain\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "content.mimetype:\"text/plain\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "@{http://www.alfresco.org/model/content/1.0}content.mimetype:\"text/plain\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "{http://www.alfresco.org/model/content/1.0}content.mimetype:\"text/plain\"", 1, null, null, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/afts", "brown..dog", 1, null, (String) null);
        // testQueryByHandler(report, core, "/afts", "TEXT:brown..dog", 1, null, (String) null);
        // testQueryByHandler(report, core, "/afts", "cm:content:brown..dog", 1, null, (String) null);
        // testQueryByHandler(report, core, "/afts", "", 1, null, (String) null);

        QName qname = QName.createQName(TEST_NAMESPACE, "float\\-ista");
        testQueryByHandler(report, core, "/afts", qname + ":3.40", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":3..4", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":3..3.39", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":3..3.40", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":3.41..3.9", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":3.40..3.9", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", qname + ":[3 TO 4]", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":[3 TO 3.39]", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":[3 TO 3.4]", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":[3.41 TO 4]", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":[3.4 TO 4]", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":[3 TO 3.4>", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":<3.4 TO 4]", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":<3.4 TO 3.4>", 0, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", qname + ":(3.40)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":(3..4)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":(3..3.39)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":(3..3.40)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":(3.41..3.9)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":(3.40..3.9)", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", qname + ":([3 TO 4])", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":([3 TO 3.39])", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":([3 TO 3.4])", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":([3.41 TO 4])", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":([3.4 TO 4])", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":([3 TO 3.4>)", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":(<3.4 TO 4])", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", qname + ":(<3.4 TO 3.4>)", 0, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "test:float_x002D_ista:3.40", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "lazy", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "laz*", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "l*y", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "l??y", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "?az?", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "*zy", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "\"lazy\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"laz*\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"l*y\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"l??y\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"?az?\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"*zy\"", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "cm:content:lazy", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:laz*", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:l*y", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:l??y", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:?az?", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:*zy", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "cm:content:\"lazy\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:\"laz*\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:\"l*y\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:\"l??y\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:\"?az?\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:\"*zy\"", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "cm:content:(lazy)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(laz*)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(l*y)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(l??y)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(?az?)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(*zy)", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "cm:content:(\"lazy\")", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(\"laz*\")", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(\"l*y\")", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(\"l??y\")", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(\"?az?\")", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:(\"*zy\")", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "lazy^2 dog^4.2", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "lazy~0.7", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:laxy~0.7", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "laxy~0.7", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "=laxy~0.7", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "~laxy~0.7", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "\"quick fox\"~0", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"quick fox\"~1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"quick fox\"~2", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"quick fox\"~3", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "\"fox quick\"~0", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"fox quick\"~1", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"fox quick\"~2", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"fox quick\"~3", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "lazy", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "-lazy", 15, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy -lazy", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy^20 -lazy", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "lazy^20 -lazy^20", 16, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "cm:content:lazy", 1, null, null, null, null, null, (String) null);

        // testQueryByHandler(report, core, "/afts", "ANDY:lazy", 1, null, (String) null);

        testQueryByHandler(report, core, "/afts", "content:lazy", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "+PATH:\"/app:company_home/st:sites/cm:rmtestnew1/cm:documentLibrary//*\"", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts",
                "+PATH:\"/app:company_home/st:sites/cm:rmtestnew1/cm:documentLibrary//*\" -TYPE:\"{http://www.alfresco.org/model/content/1.0}thumbnail\"", 15, null, null, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/afts",
                "+PATH:\"/app:company_home/st:sites/cm:rmtestnew1/cm:documentLibrary//*\" AND -TYPE:\"{http://www.alfresco.org/model/content/1.0}thumbnail\"", 0, null, null, null,
                null, null, (String) null);

        testQueryByHandler(report, core, "/afts", "(brown *(6) dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "TEXT:(brown *(6) dog)", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "\"//.\"", 0, null, null, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/afts", "PATH", "\"//.\"", 16, null, (String) null);
        testQueryByHandler(report, core, "/afts", "cm:content:brown", 1, null, null, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/afts", "ANDY:brown", 1, null, (String) null);
        // testQueryByHandler(report, core, "/afts", "andy:brown", 1, null, (String) null);
        // testQueryByHandler(report, core, "/afts", "ANDY", "brown", 1, null, (String) null);
        // testQueryByHandler(report, core, "/afts", "andy", "brown", 1, null, (String) null);

        testQueryByHandler(report, core, "/afts", "modified:*", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "modified:[MIN TO NOW]", 2, null, null, null, null, null, (String) null);

    }

    private void testSort(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("Sort", report);

        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "ID asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "ID desc", new int[] { 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "_docid_ asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "_docid_ desc", new int[] { 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "score asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "score desc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + createdDate + " asc", new int[] { 1, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + createdDate + " desc", new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 1 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + createdTime + " asc", new int[] { 1, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + createdTime + " desc", new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 1 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + ContentModel.PROP_MODIFIED + " asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
                16 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + ContentModel.PROP_MODIFIED + " desc", new int[] { 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                13, 14 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderDouble + " asc", new int[] { 1, 15, 13, 11, 9, 7, 5, 3, 2, 4, 6, 8, 10, 12, 14, 16 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderDouble + " desc", new int[] { 16, 14, 12, 10, 8, 6, 4, 2, 3, 5, 7, 9, 11, 13, 15, 1 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderFloat + " asc", new int[] { 1, 15, 13, 11, 9, 7, 5, 3, 2, 4, 6, 8, 10, 12, 14, 16 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderFloat + " desc", new int[] { 16, 14, 12, 10, 8, 6, 4, 2, 3, 5, 7, 9, 11, 13, 15, 1 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLong + " asc", new int[] { 1, 15, 13, 11, 9, 7, 5, 3, 2, 4, 6, 8, 10, 12, 14, 16 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLong + " desc", new int[] { 16, 14, 12, 10, 8, 6, 4, 2, 3, 5, 7, 9, 11, 13, 15, 1 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderInt + " asc", new int[] { 1, 15, 13, 11, 9, 7, 5, 3, 2, 4, 6, 8, 10, 12, 14, 16 }, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderInt + " desc", new int[] { 16, 14, 12, 10, 8, 6, 4, 2, 3, 5, 7, 9, 11, 13, 15, 1 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderText + " asc", new int[] { 1, 15, 13, 11, 9, 7, 5, 3, 2, 4, 6, 8, 10, 12, 14, 16 }, null,
                null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderText + " desc", new int[] { 16, 14, 12, 10, 8, 6, 4, 2, 3, 5, 7, 9, 11, 13, 15, 1 }, null,
                null, null, (String) null);

        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedText + " asc", new int[] { 1, 10, 11, 2, 3, 4, 5, 13, 12, 6, 7, 8, 14, 15, 16, 9 },
                Locale.ENGLISH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedText + " desc", new int[] { 9, 16, 15, 14, 8, 7, 6, 12, 13, 5, 4, 3, 2, 11, 10, 1 },
                Locale.ENGLISH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedText + " asc", new int[] { 1, 10, 11, 2, 3, 4, 5, 13, 12, 6, 8, 7, 14, 15, 16, 9 },
                Locale.FRENCH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedText + " desc", new int[] { 9, 16, 15, 14, 7, 8, 6, 12, 13, 5, 4, 3, 2, 11, 10, 1 },
                Locale.FRENCH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedText + " asc", new int[] { 1, 10, 11, 2, 3, 4, 5, 13, 12, 6, 7, 8, 14, 15, 16, 9 },
                Locale.GERMAN, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedText + " desc", new int[] { 9, 16, 15, 14, 8, 7, 6, 12, 13, 5, 4, 3, 2, 11, 10, 1 },
                Locale.GERMAN, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedText + " asc", new int[] { 1, 11, 2, 3, 4, 5, 13, 6, 7, 8, 12, 14, 15, 16, 9, 10 },
                new Locale("sv"), null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedText + " desc", new int[] { 10, 9, 16, 15, 14, 12, 8, 7, 6, 13, 5, 4, 3, 2, 11, 1 },
                new Locale("sv"), null, null, (String) null);

        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderMLText + " asc", new int[] { 1, 15, 13, 11, 9, 7, 5, 3, 2, 4, 6, 8, 10, 12, 14, 16 },
                Locale.ENGLISH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderMLText + " desc", new int[] { 16, 14, 12, 10, 8, 6, 4, 2, 3, 5, 7, 9, 11, 13, 15, 1 },
                Locale.ENGLISH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderMLText + " asc", new int[] { 1, 14, 16, 12, 10, 8, 6, 4, 2, 3, 5, 7, 9, 11, 13, 15 },
                Locale.FRENCH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderMLText + " desc", new int[] { 15, 13, 11, 9, 7, 5, 3, 2, 4, 6, 8, 10, 12, 16, 14, 1 },
                Locale.FRENCH, null, null, (String) null);

        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedMLText + " asc", new int[] { 1, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 },
                Locale.ENGLISH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedMLText + " desc",
                new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 1 }, Locale.ENGLISH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedMLText + " asc", new int[] { 1, 16, 15, 14, 13, 12, 2, 3, 4, 5, 11, 10, 9, 8, 7, 6 },
                Locale.FRENCH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedMLText + " desc",
                new int[] { 6, 7, 8, 9, 10, 11, 5, 4, 3, 2, 12, 13, 14, 15, 16, 1 }, Locale.FRENCH, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedMLText + " asc", new int[] { 1, 16, 15, 2, 3, 4, 5, 6, 7, 9, 8, 10, 12, 14, 11, 13 },
                Locale.GERMAN, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedMLText + " desc",
                new int[] { 13, 11, 14, 12, 10, 8, 9, 7, 6, 5, 4, 3, 2, 15, 16, 1 }, Locale.GERMAN, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedMLText + " asc", new int[] { 1, 16, 15, 7, 14, 8, 9, 10, 11, 12, 13, 2, 3, 4, 5, 6 },
                new Locale("es"), null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + orderLocalisedMLText + " desc",
                new int[] { 6, 5, 4, 3, 2, 13, 12, 11, 10, 9, 8, 14, 7, 15, 16, 1 }, new Locale("es"), null, null, (String) null);

        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "cabbage desc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "PARENT desc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@PARENT:PARENT desc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, null, null,
                null, (String) null);

        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + ContentModel.PROP_CONTENT + ".size asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
                16, 15 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + ContentModel.PROP_CONTENT + ".size desc", new int[] { 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                13, 14, 16 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + ContentModel.PROP_CONTENT + ".mimetype asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                14, 16, 15 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/alfresco", "PATH:\"//.\"", 16, "@" + ContentModel.PROP_CONTENT + ".mimetype desc", new int[] { 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                12, 13, 14, 16 }, null, null, null, (String) null);
    }

    private void testCMIS(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("CMIS", report);
        testQueryByHandler(report, core, "/cmis", "select * from cmis:document", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "select * from cmis:document D WHERE CONTAINS(D,'lazy')", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/cmis", "SELECT * FROM cmis:document D JOIN cm:ownable O ON D.cmis:objectId = O.cmis:objectId", 0, null, null, null, null, null,
                (String) null);
    }

    private void testAFTSandSort(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("AFS and Sort", report);

        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "@" + ContentModel.PROP_CONTENT.toString() + ".size asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                13, 14, 16, 15 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "@" + ContentModel.PROP_CONTENT.toString() + ".size desc", new int[] { 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                12, 13, 14, 16 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, ContentModel.PROP_CONTENT.toString() + ".size asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                14, 16, 15 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, ContentModel.PROP_CONTENT.toString() + ".size desc", new int[] { 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                13, 14, 16 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "@cm:content.size asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 15 }, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "@cm:content.size desc", new int[] { 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16 }, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "cm:content.size asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 15 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "cm:content.size desc", new int[] { 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16 }, null, null,
                null, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "@content.size asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 15 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "@content.size desc", new int[] { 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "content.size asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 15 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "content.size desc", new int[] { 15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16 }, null, null, null,
                (String) null);
        // testQueryByHandler(report, core, "/afts", "-eager or -dog", 16,
        // "@"+ContentModel.PROP_NODE_UUID.toString()+" asc", new int[] { 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4,
        // 3, 2, 1 });
        // testQueryByHandler(report, core, "/afts", "-eager or -dog", 16,
        // "@"+ContentModel.PROP_NODE_UUID.toString()+" desc", new int[] { 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4,
        // 3, 2, 1 });
        // testQueryByHandler(report, core, "/afts", "-eager or -dog", 16,
        // ContentModel.PROP_NODE_UUID.toString()+" asc", new int[] { 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3,
        // 2, 1 });
        // testQueryByHandler(report, core, "/afts", "-eager or -dog", 16,
        // ContentModel.PROP_NODE_UUID.toString()+" desc", new int[] { 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3,
        // 2, 1 });
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, "@" + ContentModel.PROP_NAME.toString() + " asc", new int[] { 1, 9, 12, 16, 6, 5, 15, 10, 2, 8, 7, 11, 14,
                4, 13, 3 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, "@" + ContentModel.PROP_NAME.toString() + " desc", new int[] { 3, 13, 4, 14, 11, 7, 8, 2, 10, 15, 5, 6, 16,
                12, 9, 1 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, ContentModel.PROP_NAME.toString() + " asc", new int[] { 1, 9, 12, 16, 6, 5, 15, 10, 2, 8, 7, 11, 14, 4, 13,
                3 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, ContentModel.PROP_NAME.toString() + " desc", new int[] { 3, 13, 4, 14, 11, 7, 8, 2, 10, 15, 5, 6, 16, 12,
                9, 1 }, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, "@cm:name asc", new int[] { 1, 9, 12, 16, 6, 5, 15, 10, 2, 8, 7, 11, 14, 4, 13, 3 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, "@cm:name desc", new int[] { 3, 13, 4, 14, 11, 7, 8, 2, 10, 15, 5, 6, 16, 12, 9, 1 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, "cm:name asc", new int[] { 1, 9, 12, 16, 6, 5, 15, 10, 2, 8, 7, 11, 14, 4, 13, 3 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, "cm:name desc", new int[] { 3, 13, 4, 14, 11, 7, 8, 2, 10, 15, 5, 6, 16, 12, 9, 1 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, "@name asc", new int[] { 1, 9, 12, 16, 6, 5, 15, 10, 2, 8, 7, 11, 14, 4, 13, 3 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, "@name desc", new int[] { 3, 13, 4, 14, 11, 7, 8, 2, 10, 15, 5, 6, 16, 12, 9, 1 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, "name asc", new int[] { 1, 9, 12, 16, 6, 5, 15, 10, 2, 8, 7, 11, 14, 4, 13, 3 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "-eager or -dog", 16, "name desc", new int[] { 3, 13, 4, 14, 11, 7, 8, 2, 10, 15, 5, 6, 16, 12, 9, 1 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "DBID asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "DBID desc", new int[] { 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 }, null, null, null,
                (String) null);
    }

    /**
     * @param core
     * @param locale
     *            TODO
     * @param rows
     *            TODO
     * @param start
     *            TODO
     * @param filter
     *            TODO
     * @param req
     * @param rsp
     * @param dataModel
     * @throws IOException
     */
    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
    private void testQueryByHandler(NamedList report, SolrCore core, String handler, String query, int count, String sort, int[] sorted, Locale locale, Integer rows,
            Integer start, String... filters) throws IOException
    {
        // TODO: report to rsp

        NamedList<Object> queryReport = new SimpleOrderedMap<Object>();
        report.add(GUID.generate(), queryReport);
        queryReport.add("Query", query);

        boolean passed = true;
        boolean ordered = true;

        SolrServletRequest solrReq = new SolrServletRequest(core, null);
        SolrQueryResponse solrRsp = new SolrQueryResponse();
        SolrRequestHandler afts = core.getRequestHandler(handler);

        ModifiableSolrParams newParams = new ModifiableSolrParams(solrReq.getParams());
        newParams.set("q", query);
        if (rows != null)
        {
            newParams.set("rows", "" + rows);
            queryReport.add("Rows", rows);
        }
        else
        {
            newParams.set("rows", "" + Integer.MAX_VALUE);
        }
        if (start != null)
        {
            newParams.set("start", "" + start);
            queryReport.add("Start", start);
        }
        if (sort != null)
        {
            newParams.set("sort", sort);
            queryReport.add("Sort", sort);
        }
        if (locale != null)
        {
            newParams.set("locale", locale.toString());
            queryReport.add("Locale", locale.toString());
        }
        if (filters != null)
        {
            newParams.set("fq", filters);
            queryReport.add("Filters", filters);
        }
        // newParams.set("fq", "AUTHORITY_FILTER_FROM_JSON");
        solrReq.setParams(newParams);
        ArrayList<ContentStream> streams = new ArrayList<ContentStream>();
        // streams.add(new ContentStreamBase.StringStream("json"));
        // solrReq.setContentStreams(streams);

        afts.handleRequest(solrReq, solrRsp);

        DocSlice ds = (DocSlice) solrRsp.getValues().get("response");
        if (ds != null)
        {
            if (ds.matches() != count)
            {
                passed = false;
                ordered = false;
                queryReport.add("Expected", count);
                queryReport.add("Found", ds.matches());
            }
            else
            {
                queryReport.add("Found", ds.matches());
            }
            int sz = ds.size();

            if (sorted != null)
            {
                int[] dbids = new int[sz];
                SolrIndexSearcher searcher = solrReq.getSearcher();
                DocIterator iterator = ds.iterator();
                for (int i = 0; i < sz; i++)
                {
                    int id = iterator.nextDoc();
                    Document doc = searcher.doc(id);
                    Fieldable dbidField = doc.getFieldable("DBID");
                    dbids[i] = Integer.valueOf(dbidField.stringValue());

                    if (ordered)
                    {
                        if (dbids[i] != sorted[i])
                        {

                            ordered = false;
                            queryReport.add("Sort at " + i + " expected", sorted[i]);
                            queryReport.add("Sort at " + i + " found", dbids[i]);
                        }
                    }
                }
                if (ordered)
                {
                    queryReport.add("Order", "Passed");
                }
                else
                {
                    queryReport.add("Order", "FAILED");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < dbids.length; i++)
                    {
                        if (builder.length() > 0)
                        {
                            builder.append(", ");
                        }
                        builder.append(dbids[i]);
                    }
                    queryReport.add("Sorted as ", builder.toString());
                }
            }

            if (passed)
            {
                queryReport.add("Count", "Passed");
            }
            else
            {
                queryReport.add("Count", "FAILED");
            }
        }
        else
        {
            queryReport.add("Test", "ERROR");
        }

        solrReq.close();
    }

    /**
     * @param after
     * @param core
     * @param dataModel
     * @throws IOException
     * @throws org.apache.lucene.queryParser.ParseException
     */
    private void testChildNameEscaping(NamedList<Object> after, SolrCore core, AlfrescoSolrDataModel dataModel, NodeRef rootNodeRef) throws IOException,
            org.apache.lucene.queryParser.ParseException
    {
        String COMPLEX_LOCAL_NAME = "\u0020\u0060\u00ac\u00a6\u0021\"\u00a3\u0024\u0025\u005e\u0026\u002a\u0028\u0029\u002d\u005f\u003d\u002b\t\n\\\u0000\u005b\u005d\u007b\u007d\u003b\u0027\u0023\u003a\u0040\u007e\u002c\u002e\u002f\u003c\u003e\u003f\\u007c\u005f\u0078\u0054\u0036\u0035\u0041\u005f";
        String NUMERIC_LOCAL_NAME = "12Woof12";

        NodeRef childNameEscapingNodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
        QName childNameEscapingQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, COMPLEX_LOCAL_NAME);
        QName pathChildNameEscapingQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ISO9075.encode(COMPLEX_LOCAL_NAME));
        ChildAssociationRef complexCAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, childNameEscapingQName, childNameEscapingNodeRef, true, 0);
        addNode(core, dataModel, 1, 17, 1, testSuperType, null, null, null, "system", new ChildAssociationRef[] { complexCAR }, new NodeRef[] { rootNodeRef }, new String[] { "/"
                + pathChildNameEscapingQName.toString() }, childNameEscapingNodeRef, true);

        NodeRef numericNameEscapingNodeRef = new NodeRef(new StoreRef("workspace", "SpacesStore"), createGUID());
        QName numericNameEscapingQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, NUMERIC_LOCAL_NAME);
        QName pathNumericNameEscapingQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, ISO9075.encode(NUMERIC_LOCAL_NAME));
        ChildAssociationRef numericCAR = new ChildAssociationRef(ContentModel.ASSOC_CHILDREN, rootNodeRef, numericNameEscapingQName, numericNameEscapingNodeRef, true, 0);
        addNode(core, dataModel, 1, 18, 1, testSuperType, null, null, null, "system", new ChildAssociationRef[] { numericCAR }, new NodeRef[] { rootNodeRef }, new String[] { "/"
                + pathNumericNameEscapingQName.toString() }, numericNameEscapingNodeRef, true);

        NamedList<Object> report = new SimpleOrderedMap<Object>();
        after.add("TestChildNameEscaping", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:" + ISO9075.encode(COMPLEX_LOCAL_NAME) + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "PATH:\"/cm:" + ISO9075.encode(NUMERIC_LOCAL_NAME) + "\"", 1);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    public Map<QName, PropertyValue> getOrderProperties()
    {
        double orderDoubleCount = -0.11d + orderTextCount * ((orderTextCount % 2 == 0) ? 0.1d : -0.1d);
        float orderFloatCount = -3.5556f + orderTextCount * ((orderTextCount % 2 == 0) ? 0.82f : -0.82f);
        long orderLongCount = -1999999999999999l + orderTextCount * ((orderTextCount % 2 == 0) ? 299999999999999l : -299999999999999l);
        int orderIntCount = -45764576 + orderTextCount * ((orderTextCount % 2 == 0) ? 8576457 : -8576457);

        Map<QName, PropertyValue> testProperties = new HashMap<QName, PropertyValue>();
        testProperties.put(createdDate, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderDate)));
        testProperties.put(createdTime, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderDate)));
        testProperties.put(orderDouble, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderDoubleCount)));
        testProperties.put(orderFloat, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderFloatCount)));
        testProperties.put(orderLong, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderLongCount)));
        testProperties.put(orderInt, new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, orderIntCount)));
        testProperties.put(
                orderText,
                new StringPropertyValue(DefaultTypeConverter.INSTANCE.convert(String.class, new String(new char[] { (char) ('l' + ((orderTextCount % 2 == 0) ? orderTextCount
                        : -orderTextCount)) }) + " cabbage")));

        testProperties.put(ContentModel.PROP_NAME, new StringPropertyValue(orderNames[orderTextCount]));
        testProperties.put(orderLocalisedText, new StringPropertyValue(orderLocalisedNames[orderTextCount]));

        MLTextPropertyValue mlTextPropLocalisedOrder = new MLTextPropertyValue();
        if (orderLocaliseMLText_en[orderTextCount].length() > 0)
        {
            mlTextPropLocalisedOrder.addValue(Locale.ENGLISH, orderLocaliseMLText_en[orderTextCount]);
        }
        if (orderLocaliseMLText_fr[orderTextCount].length() > 0)
        {
            mlTextPropLocalisedOrder.addValue(Locale.FRENCH, orderLocaliseMLText_fr[orderTextCount]);
        }
        if (orderLocaliseMLText_es[orderTextCount].length() > 0)
        {
            mlTextPropLocalisedOrder.addValue(new Locale("es"), orderLocaliseMLText_es[orderTextCount]);
        }
        if (orderLocaliseMLText_de[orderTextCount].length() > 0)
        {
            mlTextPropLocalisedOrder.addValue(Locale.GERMAN, orderLocaliseMLText_de[orderTextCount]);
        }
        testProperties.put(orderLocalisedMLText, mlTextPropLocalisedOrder);

        MLTextPropertyValue mlTextPropVal = new MLTextPropertyValue();
        mlTextPropVal.addValue(Locale.ENGLISH, new String(new char[] { (char) ('l' + ((orderTextCount % 2 == 0) ? orderTextCount : -orderTextCount)) }) + " banana");
        mlTextPropVal.addValue(Locale.FRENCH, new String(new char[] { (char) ('L' + ((orderTextCount % 2 == 0) ? -orderTextCount : orderTextCount)) }) + " banane");
        mlTextPropVal.addValue(Locale.CHINESE, new String(new char[] { (char) ('香' + ((orderTextCount % 2 == 0) ? orderTextCount : -orderTextCount)) }) + " 香蕉");
        testProperties.put(orderMLText, mlTextPropVal);

        orderDate = Duration.subtract(orderDate, new Duration("P1D"));
        orderTextCount++;
        return testProperties;
    }

    /**
     * @param before
     * @return
     * @throws IOException
     * @throws org.apache.lucene.queryParser.ParseException
     */
    private void checkRootNode(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("RootNode", report);
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

    private void checkQNames(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("QNames", report);
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

    private void checkType(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("Type", report);
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
            testQuery(dataModel, report, solrIndexSearcher, "TYPE:\"" + ContentModel.TYPE_THUMBNAIL.toString() + "\" TYPE:\"" + ContentModel.TYPE_CONTENT.toString() + "\"", 2);
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

    private void checkText(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("Text", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "TEXT:fox AND TYPE:\"" + ContentModel.PROP_CONTENT.toString() + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:fox cm\\:name:fox", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:fo AND TYPE:\"" + ContentModel.PROP_CONTENT.toString() + "\"", 0);

            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"the\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"and\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"over the lazy\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"over a lazy\"", 1);

            testQuery(dataModel, report, solrIndexSearcher, "\\@"
                    + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic").toString()) + ":*a*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@"
                    + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic").toString()) + ":*A*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@"
                    + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic").toString()) + ":\"*a*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@"
                    + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic").toString()) + ":\"*A*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@"
                    + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic").toString()) + ":*s*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@"
                    + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic").toString()) + ":*S*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@"
                    + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic").toString()) + ":\"*s*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@"
                    + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "text-indexed-stored-tokenised-atomic").toString()) + ":\"*S*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:*A*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*a*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*A*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:*a*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:*Z*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*z*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*Z*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:*z*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:laz*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:laz~", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:la?y", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:?a?y", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:*azy", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:*az*", 1);

            // Accents

            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"\u00E0\u00EA\u00EE\u00F0\u00F1\u00F6\u00FB\u00FF\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"aeidnouy\"", 1);

            // FTS

            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"fox\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toString()) + ":\"fox\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toString()) + ".mimetype:\"text/plain\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toString()) + ".locale:\"en_GB\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toString()) + ".locale:en_*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toString()) + ".locale:e*_GB", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toString()) + ".size:\"298\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"fox\"", 0, null, new String[] { "@" + ContentModel.PROP_NAME.toString() }, null);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"fox\"", 1, null,
                    new String[] { "@" + ContentModel.PROP_NAME.toString(), "@" + ContentModel.PROP_CONTENT.toString() }, null);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"cabbage\"", 15, null, new String[] { "@" + orderText.toString() }, null);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"cab*\"", 15, null, new String[] { "@" + orderText.toString() }, null);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*bage\"", 15, null, new String[] { "@" + orderText.toString() }, null);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*ba*\"", 15, null, new String[] { "@" + orderText.toString() }, null);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:cabbage", 15, null, new String[] { "@" + orderText.toString() }, null);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:*cab*", 15, Locale.ENGLISH, new String[] { "@" + orderText.toString() }, null);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:*bage", 15, null, new String[] { "@" + orderText.toString() }, null);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:dabbage~0.7", 15, null, new String[] { "@" + orderText.toString() }, null);

            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alfresco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alfresc?\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alfres??\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alfre???\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alfr????\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alf?????\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"al??????\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"a???????\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"????????\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"a??re???\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"?lfresco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"??fresco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"???resco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"????esco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"?????sco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"??????co\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"???????o\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"???res?o\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"????e?co\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"????e?c?\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"???re???\"", 1);

            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alfresc*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alfres*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alfre*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alfr*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alf*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"al*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"a*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"a****\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*lfresco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*fresco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*resco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*esco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*sco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*co\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*o\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"****lf**sc***\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*??*lf**sc***\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alfresc*tutorial\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"alf* tut*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"*co *al\"", 1);

        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private void checkAll(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("ALL", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "ALL:\"fox\"", 1, null, null, null);
            testQuery(dataModel, report, solrIndexSearcher, "ALL:\"fox\"", 0, null, null, new String[] { "@" + ContentModel.PROP_NAME.toString() });
            testQuery(dataModel, report, solrIndexSearcher, "ALL:\"fox\"", 1, null, null,
                    new String[] { "@" + ContentModel.PROP_NAME.toString(), "@" + ContentModel.PROP_CONTENT.toString() });
            testQuery(dataModel, report, solrIndexSearcher, "ALL:\"5.6\"", 1, null, null, null);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private void checkDataType(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("DataType", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "d\\:double:\"5.6\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "d\\:content:\"fox\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "d\\:content:\"fox\"", 1, Locale.US, null, null);

        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private void checkNullAndUnset(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("ISNULL/ISUNSET/ISNOTNULL", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "ISUNSET:\"" + QName.createQName(TEST_NAMESPACE, "null").toString() + "\"", 1);
            // testQuery(dataModel, report, solrIndexSearcher, "ISNULL:\"" + QName.createQName(TEST_NAMESPACE,
            // "null").toString() + "\"", 34);
            testQuery(dataModel, report, solrIndexSearcher, "ISUNSET:\"" + QName.createQName(TEST_NAMESPACE, "path-ista").toString() + "\"", 0);
            // testQuery(dataModel, report, solrIndexSearcher, "ISNULL:\"" + QName.createQName(TEST_NAMESPACE,
            // "path-ista").toString() + "\"", 33);
            testQuery(dataModel, report, solrIndexSearcher, "ISNOTNULL:\"" + QName.createQName(TEST_NAMESPACE, "null").toString() + "\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "ISNOTNULL:\"" + QName.createQName(TEST_NAMESPACE, "path-ista").toString() + "\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "ISUNSET:\"" + QName.createQName(TEST_NAMESPACE, "aspectProperty").toString() + "\"", 1);
            // testQuery(dataModel, report, solrIndexSearcher, "ISNULL:\"" + QName.createQName(TEST_NAMESPACE,
            // "aspectProperty").toString() + "\"", 34);
            testQuery(dataModel, report, solrIndexSearcher, "ISNOTNULL:\"" + QName.createQName(TEST_NAMESPACE, "aspectProperty").toString() + "\"", 0);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private void checkNonField(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("NonField", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "TEXT:fox", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:fo*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:f*x", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:*ox", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toString()) + ":fox", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toString()) + ":fo*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toString()) + ":f*x", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toString()) + ":*ox", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toPrefixString(dataModel.getNamespaceDAO())) + ":fox", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toPrefixString(dataModel.getNamespaceDAO())) + ":fo*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toPrefixString(dataModel.getNamespaceDAO())) + ":f*x", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CONTENT.toPrefixString(dataModel.getNamespaceDAO())) + ":*ox", 1);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private void checkRanges(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("Ranges", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(orderText.toString()) + ":[a TO b]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(orderText.toString()) + ":[a TO \uFFFF]", 15);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(orderText.toString()) + ":[\u0000 TO b]", 2);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(orderText.toString()) + ":[d TO \uFFFF]", 12);

        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private void checkInternalFields(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel, String nodeRef) throws IOException,
            org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("Internal", report);

        for (int i = 1; i < 16; i++)
        {
            testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ID + ":LEAF-" + i, 1, null, null, null, null, null, (String) null);
            testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ID + ":AUX-" + i, 1, null, null, null, null, null, (String) null);
        }
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ID + ":LEAF-*", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ID + ":AUX-*", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ID + ":ACL-*", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ID + ":ACLTX-*", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ID + ":TX-*", 1, null, null, null, null, null, (String) null);

        // LID is used internally via ID if a node ref is provided
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ID + ":\"" + nodeRef + "\"", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PARENT + ":\"" + nodeRef + "\"", 4, null, null, null, null, null, (String) null);

        // AbstractLuceneQueryParser.FIELD_LINKASPECT is not used for SOLR

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ANCESTOR + ":\"" + nodeRef + "\"", 10, null, null, null, null, null, (String) null);

        // AbstractLuceneQueryParser.FIELD_ISCONTAINER is not used for SOLR
        // AbstractLuceneQueryParser.FIELD_ISCATEGORY is not used for SOLR

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:one\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:two\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:three\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:four\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:five\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:six\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:seven\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:eight-0\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:eight-1\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:eight-2\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:nine\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:ten\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:eleven\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:twelve\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:thirteen\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:fourteen\"", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:fifteen\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:common\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_QNAME + ":\"cm:link\"", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:one\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:two\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:three\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:four\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:five\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:six\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:seven\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:eight-0\"", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:eight-1\"", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:eight-2\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:nine\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:ten\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:eleven\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:twelve\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:thirteen\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:fourteen\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:fifteen\"", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:common\"", 0, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYASSOCQNAME + ":\"cm:link\"", 0, null, null, null, null, null, (String) null);

        // AbstractLuceneQueryParser.FIELD_ISROOT is not used in SOLR

        testQueryByHandler(report, core, "/afts",
                QueryConstants.FIELD_PRIMARYASSOCTYPEQNAME + ":\"" + ContentModel.ASSOC_CHILDREN.toPrefixString(dataModel.getNamespaceDAO()) + "\"", 4, null, null,
                null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ISNODE + ":T", 16, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ASSOCTYPEQNAME
                + ":\"" + ContentModel.ASSOC_CHILDREN.toPrefixString(dataModel.getNamespaceDAO()) + "\"", 5, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PRIMARYPARENT + ":\"" + nodeRef + "\"", 2, null, null, null, null, null, (String) null);

        // TYPE and ASPECT is covered in other tests

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_FTSSTATUS + ":\"Clean\"", 16, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":1", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":2", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":3", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":4", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":5", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":6", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":7", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":8", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":9", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":10", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":11", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":12", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":13", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":14", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":15", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":16", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_DBID + ":17", 0, null, null, null, null, null, (String) null);
        // testQueryByHandler(report, core, "/afts", AbstractLuceneQueryParser.FIELD_DBID+":*", 16, null, null, (String)
        // null);
        // testQueryByHandler(report, core, "/afts", AbstractLuceneQueryParser.FIELD_DBID+":[3 TO 4]", 2, null, null,
        // null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_TXID + ":1", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_INTXID + ":1", 33, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ACLTXID + ":1", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_INACLTXID + ":1", 2, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_INACLTXID + ":2", 0, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_TXCOMMITTIME + ":*", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ACLTXCOMMITTIME + ":*", 1, null, null, null, null, null, (String) null);

        // AbstractLuceneQueryParser.FIELD_EXCEPTION_MESSAGE
        // addNonDictionaryField(AbstractLuceneQueryParser.FIELD_EXCEPTION_STACK

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_ACLID + ":1", 17, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_READER + ":\"GROUP_EVERYONE\"", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":andy", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":bob", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":cid", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":dave", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":eoin", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":fred", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":gail", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":hal", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":ian", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":jake", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":kara", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":loon", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":mike", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":noodle", 1, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_OWNER + ":ood", 1, null, null, null, null, null, (String) null);

        testQueryByHandler(report, core, "/afts", QueryConstants.FIELD_PARENT_ASSOC_CRC + ":0", 16, null, null, null, null, null, (String) null);
    }

    private void checkAuthorityFilter(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("Read Access", report);

        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:andy");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:bob");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:cid");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:dave");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:eoin");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:fred");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:gail");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:hal");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:ian");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:jake");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:kara");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:loon");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:mike");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:noodle");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHORITY:ood");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, null, null, null, null, null, "{!afts}|AUTHORITY:GROUP_EVERYONE");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 3, null, null, null, null, null, "{!afts}|AUTHORITY:andy |AUTHORITY:bob |AUTHORITY:cid");
        
        
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, null, null, null, null, null, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":andy\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":bob\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":cid\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":dave\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":eoin\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":fred\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":gail\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":hal\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":ian\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":jake\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":kara\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":loon\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":mike\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":noodle\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 1, null, null, null, null, null, "{!afts}|AUTHSET:\":ood\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, null, null, null, null, null, "{!afts}|AUTHSET:\":GROUP_EVERYONE\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 3, null, null, null, null, null, "{!afts}|AUTHSET:\":andy\" |AUTHSET:\":bob\" |AUTHSET:\":cid\"");
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 3, null, null, null, null, null, "{!afts}|AUTHSET:\":andy:bob:cid\"");
    }

    private void checkPaging(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("Paging", report);

        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "DBID asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, null, null, null,
                (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "DBID asc", new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, null, 20, 0, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "DBID asc", new int[] { 1, 2, 3, 4, 5, 6 }, null, 6, 0, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "DBID asc", new int[] { 7, 8, 9, 10, 11, 12 }, null, 6, 6, (String) null);
        testQueryByHandler(report, core, "/afts", "PATH:\"//.\"", 16, "DBID asc", new int[] { 13, 14, 15, 16 }, null, 6, 12, (String) null);
    }

    private void checkMLText(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("MLText", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alfresco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alfresc?\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alfres??\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alfre???\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alfr????\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alf?????\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"al??????\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"a???????\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"????????\"", 1);

            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"a??re???\"", 1);

            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"?lfresco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"??fresco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"???resco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"????esco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"?????sco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"??????co\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"???????o\"", 1);

            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"???resco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"???res?o\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"????e?co\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"????e?c?\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"???re???\"", 1);

            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alfresc*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alfres*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alfre*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alfr*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"alf*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"al*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"a*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"a*****\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"*lfresco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"*fresco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"*resco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"*esco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"*sco\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"*co\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"*o\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"****lf**sc***\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"*??*lf**sc***\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Alfresc*tutorial\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Alf* tut*\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"*co *al\"", 1);

            QName mlQName = QName.createQName(TEST_NAMESPACE, "ml");
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":and", 0);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":\"and\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":banana", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":banana", 1, Locale.UK, null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":banana", 1, Locale.ENGLISH, null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":banane", 1, Locale.FRENCH, null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":香蕉", 1, Locale.CHINESE, null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":banaan", 1, new Locale("nl"), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":banane", 1, Locale.GERMAN, null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":μπανάνα", 1, new Locale("el"), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":banana", 1, Locale.ITALIAN, null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":バナナ", 1, new Locale("ja"), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":바나나", 1, new Locale("ko"), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":banana", 1, new Locale("pt"), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":банан", 1, new Locale("ru"), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(mlQName.toString()) + ":plátano", 1, new Locale("es"), null, null);
        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private void checkPropertyTypes(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel, Date testDate, String n01NodeRef) throws IOException,
            org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("PropertyTypes", report);
        RefCounted<SolrIndexSearcher> refCounted = null;
        try
        {
            refCounted = core.getSearcher(false, true, null);
            SolrIndexSearcher solrIndexSearcher = refCounted.get();

            QName qname = QName.createQName(TEST_NAMESPACE, "int-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"1\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":1", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"01\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":01", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"001\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"0001\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[A TO 2]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[0 TO 2]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[0 TO A]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{A TO 1}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{0 TO 1}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{0 TO A}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{A TO 2}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{1 TO 2}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{1 TO A}", 0);

            qname = QName.createQName(TEST_NAMESPACE, "long-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"2\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"02\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"002\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"0002\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[A TO 2]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[0 TO 2]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[0 TO A]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{A TO 2}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{0 TO 2}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{0 TO A}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{A TO 3}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{2 TO 3}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{2 TO A}", 0);

            qname = QName.createQName(TEST_NAMESPACE, "float-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"3.4\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[A TO 4]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[3 TO 4]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[3 TO A]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[A TO 3.4]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[3.3 TO 3.4]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[3.3 TO A]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{A TO 3.4}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{3.3 TO 3.4}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{3.3 TO A}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"3.40\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"03.4\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"03.40\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "double-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"5.6\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"05.6\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"5.60\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"05.60\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[A TO 5.7]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[5.5 TO 5.7]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":[5.5 TO A]", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{A TO 5.6}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{5.5 TO 5.6}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{5.5 TO A}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{A TO 5.7}", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{5.6 TO 5.7}", 0);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":{5.6 TO A}", 0);

            Date date = new Date();
            for (SimpleDateFormatAndResolution df : CachingDateFormat.getLenientFormatters())
            {
                if (df.getResolution() < Calendar.DAY_OF_MONTH)
                {
                    continue;
                }

                String sDate = df.getSimpleDateFormat().format(testDate);
                
                NamedList<Object> subReport = new SimpleOrderedMap<Object>();
                report.add(sDate, subReport);
                

                if (sDate.length() >= 9)
                {
                    testQuery(dataModel, subReport, solrIndexSearcher, "\\@"
                            + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "date-ista").toString()) + ":\"" + sDate + "\"", 1);
                }
                testQuery(dataModel, subReport, solrIndexSearcher, "\\@"
                        + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":\"" + sDate + "\"", 1);

                sDate = df.getSimpleDateFormat().format(date);
                testQuery(dataModel, subReport, solrIndexSearcher, "\\@cm\\:CrEaTeD:[MIN TO " + sDate + "]", 1);
                testQuery(dataModel, subReport, solrIndexSearcher, "\\@cm\\:created:[MIN TO NOW]", 1);
                testQuery(dataModel, subReport, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_CREATED.toString()) + ":[MIN TO " + sDate + "]", 1);

                if (sDate.length() >= 9)
                {
                    sDate = df.getSimpleDateFormat().format(testDate);
                    testQuery(dataModel, subReport, solrIndexSearcher, "\\@"
                            + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "date-ista").toString()) + ":[" + sDate + " TO " + sDate + "]", 1);
                    testQuery(dataModel, subReport, solrIndexSearcher, "\\@"
                            + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "date-ista").toString()) + ":[MIN  TO " + sDate + "]", 1);
                    testQuery(dataModel, subReport, solrIndexSearcher, "\\@"
                            + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "date-ista").toString()) + ":[" + sDate + " TO MAX]", 1);
                }

                sDate = CachingDateFormat.getDateFormat().format(testDate);
                testQuery(dataModel, subReport, solrIndexSearcher, "\\@"
                        + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":[MIN TO " + sDate + "]", 1);

                sDate = df.getSimpleDateFormat().format(testDate);
                for (long i : new long[] { 333, 20000, 20 * 60 * 1000, 8 * 60 * 60 * 1000, 10 * 24 * 60 * 60 * 1000, 4 * 30 * 24 * 60 * 60 * 1000,
                        10 * 12 * 30 * 24 * 60 * 60 * 1000 })
                {
                    NamedList<Object> subSubReport1 = new SimpleOrderedMap<Object>();
                    NamedList<Object> subSubReport2 = new SimpleOrderedMap<Object>();
                    NamedList<Object> subSubReport3 = new SimpleOrderedMap<Object>();
                    NamedList<Object> subSubReport4 = new SimpleOrderedMap<Object>();
                    NamedList<Object> subSubReport5 = new SimpleOrderedMap<Object>();
                    
                    subReport.add("1_"+i, subSubReport1);
                    subReport.add("2_"+i, subSubReport2);
                    subReport.add("3_"+i, subSubReport3);
                    subReport.add("4_"+i, subSubReport4);
                    subReport.add("5_"+i, subSubReport5);
                    
                    String startDate = df.getSimpleDateFormat().format(new Date(testDate.getTime() - i));
                    String endDate = df.getSimpleDateFormat().format(new Date(testDate.getTime() + i));

                    testQuery(dataModel, subSubReport1, solrIndexSearcher, "\\@"
                            + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":[" + startDate + " TO " + endDate + "]", 1);
                    testQuery(dataModel, subSubReport2, solrIndexSearcher, "\\@"
                            + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":[" + sDate + " TO " + endDate + "]", 1);
                    testQuery(dataModel, subSubReport3, solrIndexSearcher, "\\@"
                            + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":[" + startDate + " TO " + sDate + "]", 1);
                    testQuery(dataModel, subSubReport4, solrIndexSearcher, "\\@"
                            + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":{" + sDate + " TO " + endDate + "}", 0);
                    testQuery(dataModel, subSubReport5, solrIndexSearcher, "\\@"
                            + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "datetime-ista").toString()) + ":{" + startDate + " TO " + sDate + "}", 0);

                }
            }

            qname = QName.createQName(TEST_NAMESPACE, "boolean-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"true\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "qname-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"{wibble}wobble\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "category-ista");
            testQuery(
                    dataModel,
                    report,
                    solrIndexSearcher,
                    "\\@"
                            + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\""
                            + DefaultTypeConverter.INSTANCE.convert(String.class, new NodeRef(new StoreRef("proto", "id"), "CategoryId")) + "\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "noderef-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"" + n01NodeRef + "\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "path-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"/{" + NamespaceService.CONTENT_MODEL_1_0_URI + "}three\"", 1);

            qname = QName.createQName(TEST_NAMESPACE, "any-many-ista");
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"100\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "\\@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"anyValueAsString\"", 1);

            //

            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"Tutorial Alfresco\"~0", 0);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"Tutorial Alfresco\"~1", 0);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"Tutorial Alfresco\"~2", 1);
            testQuery(dataModel, report, solrIndexSearcher, "TEXT:\"Tutorial Alfresco\"~3", 1);

            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Alfresco Tutorial\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Tutorial Alfresco\"", 0);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Tutorial Alfresco\"~0", 0);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Tutorial Alfresco\"~1", 0);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Tutorial Alfresco\"~2", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(ContentModel.PROP_DESCRIPTION.toString()) + ":\"Tutorial Alfresco\"~3", 1);

            qname = QName.createQName(TEST_NAMESPACE, "mltext-many-ista");
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":лемур", 1, (new Locale("ru")), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":lemur", 1, (new Locale("en")), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":chou", 1, (new Locale("fr")), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":cabbage", 1, (new Locale("en")), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":cabba*", 1, (new Locale("en")), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":ca*ge", 1, (new Locale("en")), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":*bage", 1, (new Locale("en")), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":cabage~", 1, (new Locale("en")), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":*b?ag?", 1, (new Locale("en")), null, null);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":cho*", 1, (new Locale("fr")), null, null);

            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(QName.createQName(TEST_NAMESPACE, "content-many-ista").toString()) + ":multicontent", 1);

            qname = QName.createQName(TEST_NAMESPACE, "locale-ista");
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"en_GB_\"", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":en_GB_", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":en_*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":*_GB_*", 1);
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":*_gb_*", 1);

            qname = QName.createQName(TEST_NAMESPACE, "period-ista");
            testQuery(dataModel, report, solrIndexSearcher, "@" + SearchLanguageConversion.escapeLuceneQuery(qname.toString()) + ":\"period|12\"", 1);

        }
        finally
        {
            if (refCounted != null)
            {
                refCounted.decref();
            }
        }
    }

    private String checkPaths(NamedList<Object> before, SolrCore core, AlfrescoSolrDataModel dataModel) throws IOException, org.apache.lucene.queryParser.ParseException
    {
        NamedList<Object> report = new SimpleOrderedMap<Object>();
        before.add("Paths", report);
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
    private void testQuery(AlfrescoSolrDataModel dataModel, NamedList<Object> report, SolrIndexSearcher solrIndexSearcher, String queryString, Integer count, Locale locale,
            String[] textAttributes, String[] allAttributes, String... name) throws org.apache.lucene.queryParser.ParseException, IOException
    {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setQuery(queryString);
        if (locale != null)
        {
            searchParameters.addLocale(locale);
        }
        if (textAttributes != null)
        {
            for (String textAttribute : textAttributes)
            {
                searchParameters.addTextAttribute(textAttribute);
            }
        }
        if (allAttributes != null)
        {
            for (String allAttribute : allAttributes)
            {
                searchParameters.addAllAttribute(allAttribute);
            }
        }
        // Query query = dataModel.getFTSQuery(searchParameters, solrIndexSearcher.getIndexReader());
        long start = System.nanoTime();
        Query query = dataModel.getLuceneQueryParser(searchParameters, solrIndexSearcher.getIndexReader()).parse(queryString);
        TopDocs docs = solrIndexSearcher.search(query, count * 2 + 10);
        
        NamedList<Object> subReport = new SimpleOrderedMap<Object>();
        report.add(GUID.generate(), subReport);
        
        long end = System.nanoTime();
        if (count != null)
        {
            if (docs.totalHits != count)
            {
                subReport.add("FAILED: " + fixQueryString(queryString, name), docs.totalHits);
            }
            else
            {
                subReport.add("Passed: " + fixQueryString(queryString, name), docs.totalHits);
            }
        }
        subReport.add("Time (s): " + fixQueryString(queryString, name), ((end - start) / 1000000000.0f));
    }

    private void testFTSQuery(AlfrescoSolrDataModel dataModel, NamedList<Object> report, SolrIndexSearcher solrIndexSearcher, String queryString, Integer count, Locale locale,
            String[] textAttributes, String[] allAttributes, String... name) throws org.apache.lucene.queryParser.ParseException, IOException
    {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setQuery(queryString);
        if (locale != null)
        {
            searchParameters.addLocale(locale);
        }
        if (textAttributes != null)
        {
            for (String textAttribute : textAttributes)
            {
                searchParameters.addTextAttribute(textAttribute);
            }
        }
        if (allAttributes != null)
        {
            for (String allAttribute : allAttributes)
            {
                searchParameters.addAllAttribute(allAttribute);
            }
        }
        // Query query = dataModel.getFTSQuery(searchParameters, solrIndexSearcher.getIndexReader());
        long start = System.nanoTime();
        Query query = dataModel.getFTSQuery(new Pair<SearchParameters, Boolean>(searchParameters, Boolean.FALSE), solrIndexSearcher.getIndexReader());
        TopDocs docs = solrIndexSearcher.search(query, count * 2 + 10);
        
        NamedList<Object> subReport = new SimpleOrderedMap<Object>();
        report.add(GUID.generate(), subReport);
        
        long end = System.nanoTime();
        if (count != null)
        {
            if (docs.totalHits != count)
            {
                subReport.add("FAILED: " + fixQueryString(queryString, name), docs.totalHits);
            }
            else
            {
                subReport.add("Passed: " + fixQueryString(queryString, name), docs.totalHits);
            }
        }
        subReport.add("Time (s): " + fixQueryString(queryString, name), ((end - start) / 1000000000.0f));
    }

    private String fixQueryString(String queryString, String... name)
    {
        if (name.length > 0)
        {
            return name[0].replace("\uFFFF", "<Unicode FFFF>");
        }
        else
        {
            return queryString.replace("\uFFFF", "<Unicode FFFF>");
        }
    }

    private void testQuery(AlfrescoSolrDataModel dataModel, NamedList<Object> report, SolrIndexSearcher solrIndexSearcher, String queryString, int count)
            throws org.apache.lucene.queryParser.ParseException, IOException
    {
        testQuery(dataModel, report, solrIndexSearcher, queryString, count, null, null, null);
    }

    private NodeRef addNode(SolrCore core, AlfrescoSolrDataModel dataModel, int txid, int dbid, int aclid, QName type, QName[] aspects, Map<QName, PropertyValue> properties,
            Map<QName, String> content, String owner, ChildAssociationRef[] parentAssocs, NodeRef[] ancestors, String[] paths, NodeRef nodeRef, boolean commit) throws IOException
    {
        AddUpdateCommand leafDocCmd = new AddUpdateCommand();
        leafDocCmd.overwriteCommitted = true;
        leafDocCmd.overwritePending = true;
        leafDocCmd.solrDoc = createLeafDocument(dataModel, txid, dbid, nodeRef, type, aspects, properties, content);
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

        if (commit)
        {
            core.getUpdateHandler().commit(new CommitUpdateCommand(false));
        }

        return nodeRef;
    }

    private void addAcl(SolrCore core, AlfrescoSolrDataModel dataModel, int acltxid, int aclid, int maxReader, int totalReader) throws IOException
    {
        AddUpdateCommand aclTxCmd = new AddUpdateCommand();
        aclTxCmd.overwriteCommitted = true;
        aclTxCmd.overwritePending = true;
        SolrInputDocument aclTxSol = new SolrInputDocument();
        aclTxSol.addField(QueryConstants.FIELD_ID, "ACLTX-" + acltxid);
        aclTxSol.addField(QueryConstants.FIELD_ACLTXID, acltxid);
        aclTxSol.addField(QueryConstants.FIELD_INACLTXID, acltxid);
        aclTxSol.addField(QueryConstants.FIELD_ACLTXCOMMITTIME, (new Date()).getTime());
        aclTxCmd.solrDoc = aclTxSol;
        aclTxCmd.doc = CoreTracker.toDocument(aclTxCmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(aclTxCmd);

        AddUpdateCommand aclCmd = new AddUpdateCommand();
        aclCmd.overwriteCommitted = true;
        aclCmd.overwritePending = true;
        SolrInputDocument aclSol = new SolrInputDocument();
        aclSol.addField(QueryConstants.FIELD_ID, "ACL-" + aclid);
        aclSol.addField(QueryConstants.FIELD_ACLID, aclid);
        aclSol.addField(QueryConstants.FIELD_INACLTXID, "" + acltxid);
        aclSol.addField(QueryConstants.FIELD_READER, "GROUP_EVERYONE");
        aclSol.addField(QueryConstants.FIELD_READER, "pig");
        for (int i = 0; i <= maxReader; i++)
        {
            aclSol.addField(QueryConstants.FIELD_READER, "READER-" + (totalReader - i));
        }
        aclCmd.solrDoc = aclSol;
        aclCmd.doc = CoreTracker.toDocument(aclCmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(aclCmd);

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
        leafDocCmd.solrDoc = createLeafDocument(dataModel, txid, dbid, rootNodeRef, ContentModel.TYPE_STOREROOT, new QName[] { ContentModel.ASPECT_ROOT }, null, null);
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

        addAcl(core, dataModel, acltxid, aclid, 0, 0);

        AddUpdateCommand txCmd = new AddUpdateCommand();
        txCmd.overwriteCommitted = true;
        txCmd.overwritePending = true;
        SolrInputDocument input = new SolrInputDocument();
        input.addField(QueryConstants.FIELD_ID, "TX-" + txid);
        input.addField(QueryConstants.FIELD_TXID, txid);
        input.addField(QueryConstants.FIELD_INTXID, txid);
        input.addField(QueryConstants.FIELD_TXCOMMITTIME, (new Date()).getTime());
        txCmd.solrDoc = input;
        txCmd.doc = CoreTracker.toDocument(txCmd.getSolrInputDocument(), core.getSchema(), dataModel);
        core.getUpdateHandler().addDoc(txCmd);

        core.getUpdateHandler().commit(new CommitUpdateCommand(false));
    }

    public SolrInputDocument createLeafDocument(AlfrescoSolrDataModel dataModel, int txid, int dbid, NodeRef nodeRef, QName type, QName[] aspects,
            Map<QName, PropertyValue> properties, Map<QName, String> content) throws IOException
    {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(QueryConstants.FIELD_ID, "LEAF-" + dbid);
        doc.addField(QueryConstants.FIELD_DBID, "" + dbid);
        doc.addField(QueryConstants.FIELD_LID, nodeRef);
        doc.addField(QueryConstants.FIELD_INTXID, "" + txid);

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

        doc.addField(QueryConstants.FIELD_TYPE, type);
        if (aspects != null)
        {
            for (QName aspect : aspects)
            {
                doc.addField(QueryConstants.FIELD_ASPECT, aspect);
            }
        }
        doc.addField(QueryConstants.FIELD_ISNODE, "T");
        doc.addField(QueryConstants.FIELD_FTSSTATUS, "Clean");
        doc.addField(QueryConstants.FIELD_TENANT, "_DEFAULT_");

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
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), stringPropertyValue.getValue());
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort", stringPropertyValue.getValue());
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
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__", builder.toString());
                }
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".u", builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__.u", builder.toString());
                }

                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort", builder.toString());
                }

            }
            else
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), stringPropertyValue.getValue());
            }

        }
        else
        {
            doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), stringPropertyValue.getValue());
        }
    }

    private void addContentPropertyToDoc(SolrInputDocument doc, QName propertyQName, ContentPropertyValue contentPropertyValue, Map<QName, String> content) throws IOException
    {
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".size", contentPropertyValue.getLength());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".locale", contentPropertyValue.getLocale());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".mimetype", contentPropertyValue.getMimetype());
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".encoding", contentPropertyValue.getEncoding());

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
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), multiReader);

        isr = new StringReader(value);
        builder = new StringBuilder();
        builder.append("\u0000").append(contentPropertyValue.getLocale().toString()).append("\u0000");
        prefix = new StringReader(builder.toString());
        multiReader = new MultiReader(prefix, isr);
        doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__", multiReader);

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
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__", builder.toString());
                }
                if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
                {
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".u", builder.toString());
                    doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".__.u", builder.toString());
                }

                if (sort.length() > 0)
                {
                    sort.append("\u0000");
                }
                sort.append(builder.toString());
            }

            if ((propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.FALSE) || (propertyDefinition.getIndexTokenisationMode() == IndexTokenisationMode.BOTH))
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString() + ".sort", sort.toString());
            }
        }
        else
        {
            for (Locale locale : mlTextPropertyValue.getLocales())
            {
                doc.addField(QueryConstants.PROPERTY_FIELD_PREFIX + propertyQName.toString(), mlTextPropertyValue.getValue(locale));
            }
        }

    }

    private SolrInputDocument createAuxDocument(int txid, int dbid, int aclid, String[] paths, String owner, ChildAssociationRef[] parentAssocs, NodeRef[] ancestors)
    {
        SolrInputDocument aux = new SolrInputDocument();
        aux.addField(QueryConstants.FIELD_ID, "AUX-" + dbid);
        aux.addField(QueryConstants.FIELD_DBID, "" + dbid);
        aux.addField(QueryConstants.FIELD_ACLID, "" + aclid);
        aux.addField(QueryConstants.FIELD_INTXID, "" + txid);

        if (paths != null)
        {
            for (String path : paths)
            {
                aux.addField(QueryConstants.FIELD_PATH, path);
            }
        }

        if (owner != null)
        {
            aux.addField(QueryConstants.FIELD_OWNER, owner);
        }
        aux.addField(QueryConstants.FIELD_PARENT_ASSOC_CRC, "0");

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
                aux.addField(QueryConstants.FIELD_PARENT, childAssocRef.getParentRef());

                if (childAssocRef.isPrimary())
                {
                    aux.addField(QueryConstants.FIELD_PRIMARYPARENT, childAssocRef.getParentRef());
                    aux.addField(QueryConstants.FIELD_PRIMARYASSOCTYPEQNAME, ISO9075.getXPathName(childAssocRef.getTypeQName()));
                    aux.addField(QueryConstants.FIELD_PRIMARYASSOCQNAME, ISO9075.getXPathName(childAssocRef.getQName()));

                }
            }
            aux.addField(QueryConstants.FIELD_ASSOCTYPEQNAME, assocTypeQNameBuffer.toString());
            aux.addField(QueryConstants.FIELD_QNAME, qNameBuffer.toString());
        }
        if (ancestors != null)
        {
            for (NodeRef ancestor : ancestors)
            {
                aux.addField(QueryConstants.FIELD_ANCESTOR, ancestor.toString());
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
     * @throws IOException 
     */
    private void addCoreSummary(String cname, boolean detail, boolean hist, boolean values, CoreTracker tracker, NamedList<Object> report) throws IOException
    {
        NamedList<Object> coreSummary = new SimpleOrderedMap<Object>();
        coreSummary.addAll(tracker.getCoreStats());
        long lastIndexTxCommitTime = tracker.getLastIndexedTxCommitTime();
        long lastIndexedTxId = tracker.getLastIndexedTxId();
        long lastTxCommitTimeOnServer = tracker.getLastTxCommitTimeOnServer();
        long lastTxIdOnServer = tracker.getLastTxIdOnServer();
        Date lastIndexTxCommitDate = new Date(lastIndexTxCommitTime);
        Date lastTxOnServerDate = new Date(lastTxCommitTimeOnServer);
        long transactionsToDo = lastTxIdOnServer - lastIndexedTxId;
        if(transactionsToDo < 0)
        {
            transactionsToDo = 0;
        }
        
        long lastIndexChangeSetCommitTime = tracker.getLastIndexedChangeSetCommitTime();
        long lastIndexedChangeSetId = tracker.getLastIndexedChangeSetId();
        long lastChangeSetCommitTimeOnServer = tracker.getLastChangeSetCommitTimeOnServer();
        long lastChangeSetIdOnServer = tracker.getLastChangeSetIdOnServer();
        Date lastIndexChangeSetCommitDate = new Date(lastIndexChangeSetCommitTime);
        Date lastChangeSetOnServerDate = new Date(lastChangeSetCommitTimeOnServer);
        long changeSetsToDo = lastChangeSetIdOnServer - lastIndexedChangeSetId;
        if(changeSetsToDo < 0)
        {
            changeSetsToDo = 0;
        }

        long remainingTxTimeMillis = (long) (transactionsToDo * tracker.getTrackerStats().getMeanDocsPerTx() * tracker.getTrackerStats().getMeanNodeIndexTime() / tracker
                .getTrackerStats().getNodeIndexingThreadCount());
        Date now = new Date();
        Date end = new Date(now.getTime() + remainingTxTimeMillis);
        Duration remainingTx = new Duration(now, end);

        long remainingChangeSetTimeMillis = (long) (changeSetsToDo
                * tracker.getTrackerStats().getMeanAclsPerChangeSet() * tracker.getTrackerStats().getMeanAclIndexTime() / tracker.getTrackerStats().getNodeIndexingThreadCount());
        now = new Date();
        end = new Date(now.getTime() + remainingChangeSetTimeMillis);
        Duration remainingChangeSet = new Duration(now, end);

        Duration txLag = new Duration(lastIndexTxCommitDate, lastTxOnServerDate);
        if(lastIndexTxCommitDate.compareTo(lastTxOnServerDate) > 0)
        {
            txLag = new Duration();
        }
        long txLagSeconds = (lastTxCommitTimeOnServer - lastIndexTxCommitTime) / 1000;
        if(txLagSeconds < 0)
        {
            txLagSeconds = 0;
        }
        
        Duration changeSetLag = new Duration(lastIndexChangeSetCommitDate, lastChangeSetOnServerDate);
        if(lastIndexChangeSetCommitDate.compareTo(lastChangeSetOnServerDate) > 0)
        {
            changeSetLag = new Duration();
        }
        long changeSetLagSeconds =  (lastChangeSetCommitTimeOnServer - lastIndexChangeSetCommitTime) / 1000;
        if(txLagSeconds < 0)
        {
            txLagSeconds = 0;
        }

        coreSummary.add("Active", tracker.isRunning());

        // TX

        coreSummary.add("Last Index TX Commit Time", lastIndexTxCommitTime);
        coreSummary.add("Last Index TX Commit Date", lastIndexTxCommitDate);
        coreSummary.add("TX Lag",  txLagSeconds+ " s");
        coreSummary.add("TX Duration", txLag.toString());
        coreSummary.add("Timestamp for last TX on server", lastTxCommitTimeOnServer);
        coreSummary.add("Date for last TX on server", lastTxOnServerDate);
        coreSummary.add("Id for last TX on server", lastTxIdOnServer);
        coreSummary.add("Id for last TX in index", lastIndexedTxId);
        coreSummary.add("Approx transactions remaining", transactionsToDo);
        coreSummary.add("Approx transaction indexing time remaining", remainingTx.largestComponentformattedString());

        // Change set

        coreSummary.add("Last Index Change Set Commit Time", lastIndexChangeSetCommitTime);
        coreSummary.add("Last Index Change Set Commit Date", lastIndexChangeSetCommitDate);
        coreSummary.add("Change Set Lag", changeSetLagSeconds + " s");
        coreSummary.add("Change Set Duration", changeSetLag.toString());
        coreSummary.add("Timestamp for last Change Set on server", lastChangeSetCommitTimeOnServer);
        coreSummary.add("Date for last Change Set on server", lastChangeSetOnServerDate);
        coreSummary.add("Id for last Change Set on server", lastChangeSetIdOnServer);
        coreSummary.add("Id for last Change Set in index", lastIndexedChangeSetId);
        coreSummary.add("Approx change sets remaining", changeSetsToDo);
        coreSummary.add("Approx change set indexing time remaining", remainingChangeSet.largestComponentformattedString());

        // Stats

        coreSummary.add("Model sync times (ms)", tracker.getTrackerStats().getModelTimes().getNamedList(detail, hist, values));
        coreSummary.add("Acl index time (ms)", tracker.getTrackerStats().getAclTimes().getNamedList(detail, hist, values));
        coreSummary.add("Node index time (ms)", tracker.getTrackerStats().getNodeTimes().getNamedList(detail, hist, values));
        coreSummary.add("Docs/Tx", tracker.getTrackerStats().getTxDocs().getNamedList(detail, hist, values));
        coreSummary.add("Doc Transformation time (ms)", tracker.getTrackerStats().getDocTransformationTimes().getNamedList(detail, hist, values));

        // Modela

        Map<String, Set<String>> modelErrors = tracker.getModelErrors();
        if (modelErrors.size() > 0)
        {
            NamedList<Object> errorList = new SimpleOrderedMap<Object>();
            for (String modelName : modelErrors.keySet())
            {
                Set<String> errors = modelErrors.get(modelName);
                errorList.add(modelName, errors);
            }
            coreSummary.add("Model changes are not compatible with the existing data model and have not been applied", errorList);
        }

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
        ihr.add("Index aux count", indexHealthReport.getAuxDocCountInIndex());
        ihr.add("Count of duplicate aux docs in the index", indexHealthReport.getDuplicatedAuxInIndex().cardinality());
        if (indexHealthReport.getDuplicatedAuxInIndex().cardinality() > 0)
        {
            ihr.add("First duplicate aux in the index", "AUX-" + indexHealthReport.getDuplicatedAuxInIndex().nextSetBit(0L));
        }
        ihr.add("Index error count", indexHealthReport.getErrorDocCountInIndex());
        ihr.add("Count of duplicate error docs in the index", indexHealthReport.getDuplicatedErrorInIndex().cardinality());
        if (indexHealthReport.getDuplicatedErrorInIndex().cardinality() > 0)
        {
            ihr.add("First duplicate error in the index", "ERROR-" + indexHealthReport.getDuplicatedErrorInIndex().nextSetBit(0L));
        }
        ihr.add("Index unindexed count", indexHealthReport.getUnindexedDocCountInIndex());
        ihr.add("Count of duplicate unindexed docs in the index", indexHealthReport.getDuplicatedUnindexedInIndex().cardinality());
        if (indexHealthReport.getDuplicatedUnindexedInIndex().cardinality() > 0)
        {
            ihr.add("First duplicate unindexed in the index", "UNINDEXED-" + indexHealthReport.getDuplicatedErrorInIndex().nextSetBit(0L));
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

    class SolrServletRequest extends SolrQueryRequestBase
    {
        public SolrServletRequest(SolrCore core, HttpServletRequest req)
        {
            super(core, new MultiMapSolrParams(Collections.<String, String[]> emptyMap()));
        }
    }

    public static void main(String[] args)
    {
        AlfrescoCoreAdminHandler handler = new AlfrescoCoreAdminHandler();
        String[] toSort = handler.orderLocalisedNames;
        Collator collator = Collator.getInstance(Locale.ENGLISH);
        Arrays.sort(toSort, collator);
        System.out.println(Locale.ENGLISH);
        for (int i = 0; i < toSort.length; i++)
        {
            System.out.println(toSort[i]);
        }

        collator = Collator.getInstance(Locale.FRENCH);
        Arrays.sort(toSort, collator);
        System.out.println(Locale.FRENCH);
        for (int i = 0; i < toSort.length; i++)
        {
            System.out.println(toSort[i]);
        }

        collator = Collator.getInstance(Locale.GERMAN);
        Arrays.sort(toSort, collator);
        System.out.println(Locale.GERMAN);
        for (int i = 0; i < toSort.length; i++)
        {
            System.out.println(toSort[i]);
        }

        collator = Collator.getInstance(new Locale("sv"));
        Arrays.sort(toSort, collator);
        System.out.println(new Locale("sv"));
        for (int i = 0; i < toSort.length; i++)
        {
            System.out.println(toSort[i]);
        }

    }
}
