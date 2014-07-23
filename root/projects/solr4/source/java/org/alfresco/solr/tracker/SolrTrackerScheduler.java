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
package org.alfresco.solr.tracker;

import java.text.ParseException;
import java.util.Collection;
import java.util.Properties;

import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a decorator of the Quartz Scheduler object to add Solr-specific functionality.
 * @author Ahmed Owian
 */
public class SolrTrackerScheduler
{
    protected static final String SOLR_JOB_GROUP = "Solr";
    protected final static Logger log = LoggerFactory.getLogger(SolrTrackerScheduler.class);
    protected Scheduler scheduler;

    public SolrTrackerScheduler(AlfrescoCoreAdminHandler adminHandler)
    {
        // TODO: pick scheduler properties from SOLR config or file ...
        try
        {
            StdSchedulerFactory factory = new StdSchedulerFactory();
            Properties properties = new Properties();
            properties.setProperty("org.quartz.scheduler.instanceName", SolrTrackerScheduler.class.getSimpleName());
            properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
            properties.setProperty("org.quartz.threadPool.threadCount", "3");
            properties.setProperty("org.quartz.threadPool.makeThreadsDaemons", "true");
            properties.setProperty("org.quartz.scheduler.makeSchedulerThreadDaemon", "true");
            properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
            factory.initialize(properties);
            scheduler = factory.getScheduler();
            scheduler.start();

            // Start job to manage the tracker jobs
            JobDetail job = new JobDetail("CoreWatcher", SOLR_JOB_GROUP, CoreWatcherJob.class);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(CoreWatcherJob.JOBDATA_ADMIN_HANDLER_KEY, adminHandler);
            job.setJobDataMap(jobDataMap);
            Trigger trigger;
            try
            {
                trigger = new CronTrigger("CoreWatcherTrigger", SOLR_JOB_GROUP, "0/20 * * * * ? *");
                scheduler.scheduleJob(job, trigger);
            }
            catch (ParseException e)
            {
                logError("CoreWatcher", e);
            }
        }
        catch (SchedulerException e)
        {
            logError("CoreWatcher", e);
        }
    }

    private void logError(String jobType, Throwable e)
    {
        log.error("Failed to schedule " + jobType + " Job.", e);
    }
    
    public void schedule(Tracker tracker, String coreName, Properties props)
    {
        String jobName = this.getJobName(tracker, coreName);
        JobDetail job = new JobDetail(jobName, SOLR_JOB_GROUP, TrackerJob.class);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(TrackerJob.JOBDATA_TRACKER_KEY, tracker);
        job.setJobDataMap(jobDataMap);
        Trigger trigger;
        try
        {
            String cron =  props.getProperty("alfresco.cron", "0/15 * * * * ? *");
            trigger = new CronTrigger(jobName, SOLR_JOB_GROUP, cron);
            scheduler.scheduleJob(job, trigger);
        }
        catch (ParseException e)
        {
            logError("Tracker", e);
        }
        catch (SchedulerException e)
        {
            logError("Tracker", e);
        }
    }

    protected String getJobName(Tracker tracker, String coreName)
    {
        return tracker.getClass().getSimpleName() + "-" + coreName;
    }

    public void shutdown() throws SchedulerException
    {
        this.scheduler.shutdown();
    }

    public void deleteTrackerJobs(String coreName, Collection<Tracker> trackers) throws SchedulerException
    {
        for (Tracker tracker : trackers)
        {
            deleteTrackerJob(coreName, tracker);
        }
        
    }

    public void deleteTrackerJob(String coreName, Tracker tracker) throws SchedulerException
    {
        String jobName = this.getJobName(tracker, coreName);
        this.scheduler.deleteJob(jobName, SOLR_JOB_GROUP);
    }

    public boolean isShutdown() throws SchedulerException
    {
        return this.scheduler.isShutdown();
    }

    public void pauseAll() throws SchedulerException
    {
        this.scheduler.pauseAll();
    }
}
