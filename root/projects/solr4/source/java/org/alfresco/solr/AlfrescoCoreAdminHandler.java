/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.alfresco.solr.tracker.CoreWatcherJob;
import org.alfresco.solr.tracker.Tracker;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.handler.admin.CoreAdminHandler;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class AlfrescoCoreAdminHandler extends CoreAdminHandler
{
    private Scheduler scheduler = null;
    private ConcurrentHashMap<String, Tracker> trackers = new ConcurrentHashMap<String, Tracker>();
    private ConcurrentHashMap<String, InformationServer> informationServers = new ConcurrentHashMap<String, InformationServer>();

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
        catch (Exception e)
        {
            log.info("Failed to load " + resource, e);
        }
    }

    private InputStream openResource(CoreContainer coreContainer, String resource)
    {
        InputStream is = null;
        try
        {
            File f0 = new File(resource);
            File f = f0;
            if (!f.isAbsolute())
            {
                // try $CWD/$configDir/$resource
                f = new File(coreContainer.getSolrHome() + resource);
            }
            if (f.isFile() && f.canRead())
            {
                return new FileInputStream(f);
            }
            else if (f != f0)
            { // no success with $CWD/$configDir/$resource
                if (f0.isFile() && f0.canRead()) return new FileInputStream(f0);
            }
            // delegate to the class loader (looking into $INSTANCE_DIR/lib jars)
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error opening " + resource, e);
        }
        if (is == null) { throw new RuntimeException("Can't find resource '" + resource + "' in classpath or '"
                    + coreContainer.getSolrHome() + "', cwd=" + System.getProperty("user.dir")); }
        return is;
    }

    /**
     * @return the trackers
     */
    public ConcurrentHashMap<String, Tracker> getTrackers()
    {
        return trackers;
    }

    public ConcurrentHashMap<String, InformationServer> getInformationServers()
    {
        return this.informationServers;
    }
}
