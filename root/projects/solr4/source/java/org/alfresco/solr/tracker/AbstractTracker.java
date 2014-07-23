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
package org.alfresco.solr.tracker;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.Properties;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.solr.IndexTrackingShutdownException;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.TrackerState;
import org.alfresco.solr.client.SOLRAPIClient;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base class that provides common {@link Tracker} behaviour.
 * 
 * @author Matt Ward
 */
public abstract class AbstractTracker implements Tracker
{
    protected final static Logger log = LoggerFactory.getLogger(AbstractTracker.class);
    
    protected Properties props;
    
    protected SOLRAPIClient client;
    protected InformationServer infoSrv;

    protected String coreName;

    protected StoreRef storeRef;
    protected long batchCount;

    protected boolean isSlave = false;
    
    protected boolean isMaster = true;
    
    protected String alfrescoVersion;

    protected String id;
    
    protected TrackerStats trackerStats;
    
    protected boolean runPostModelLoadInit = true;
    
    private int maxLiveSearchers;
    
    private volatile boolean shutdown = false;
    

    /**
     * Default constructor, strictly for testing.
     */
    protected AbstractTracker()
    {
    }
    
    protected AbstractTracker(SolrTrackerScheduler scheduler, String id, Properties p, SOLRAPIClient client, 
                String coreName, InformationServer informationServer)
    {
        this.props = p;
        this.client = client;
        this.id = id;
        this.coreName = coreName;
        this.infoSrv = informationServer;

        storeRef = new StoreRef(p.getProperty("alfresco.stores"));
        batchCount = Integer.parseInt(p.getProperty("alfresco.batch.count", "1000"));
        maxLiveSearchers =  Integer.parseInt(p.getProperty("alfresco.maxLiveSearchers", "2"));
        isSlave =  Boolean.parseBoolean(p.getProperty("enable.slave", "false"));
        isMaster =  Boolean.parseBoolean(p.getProperty("enable.master", "true"));

        this.trackerStats = new TrackerStats(this.infoSrv);
        
        scheduler.schedule(this, coreName, p);

        alfrescoVersion = p.getProperty("alfresco.version", "4.2.2");
        log.info("Solr built for Alfresco version: " + alfrescoVersion);
    }
    
    /**
     * Subclasses must implement behaviour that completes the following steps, in order:
     * <ol>
     *     <li>Purge</li>
     *     <li>Reindex</li>
     *     <li>Index</li>
     *     <li>Track repository</li>
     * </ol>
     * @throws Throwable
     */
    protected abstract void doTrack() throws Throwable;
    
    /**
     * Template method - subclasses must implement the {@link Tracker}-specific indexing
     * by implementing the abstract method {@link #doTrack()}.
     */
    @Override
    public void track()
    {
        TrackerState state = this.infoSrv.getTrackerState();

        synchronized (this) // TODO: Should we be synchronize on something else, such as state? 
        {
            if (state.isRunning())
            {
                log.info("... update for " + coreName + " is already running");
                return;
            }
            else
            {
                log.info("... updating " + coreName);
                state.setRunning(true);
            }
        }
        try
        {
            doTrack();
        }
        catch(IndexTrackingShutdownException t)
        {
            try
            {
                this.infoSrv.rollback();
            }
            catch (IOException e)
            {
                log.error("Failed to roll back pending work on error", t);
            }
            log.info("Stopping index tracking for "+coreName);
        }
        catch(Throwable t)
        {
            try
            {
                this.infoSrv.rollback();
            }
            catch (IOException e)
            {
                log.error("Failed to roll back pending work on error", t);
            }
            if (t instanceof SocketTimeoutException)
            {
                if (log.isDebugEnabled())
                {
                    // DEBUG, so give the whole stack trace
                    log.warn("Tracking communication timed out.", t);
                }
                else
                {
                    // We don't need the stack trace.  It timed out.
                    log.warn("Tracking communication timed out.");
                }
            }
            else
            {
                log.error("Tracking failed", t);
            }
        }
        finally
        {
            state.setRunning(false);
            state.setCheck(false);
        }
    }
    

    public int getMaxLiveSearchers()
    {
        return maxLiveSearchers;
    }

    protected void checkShutdown()
    {
        if(shutdown)
        {
            throw new IndexTrackingShutdownException();
        }
    }
    
    public void setShutdown(boolean shutdown)
    {
        this.shutdown = shutdown;
    }
    
    public void close()
    {
        client.close();
    }

    /**
     * @return Alfresco version Solr was built for
     */
    @Override
    public String getAlfrescoVersion()
    {
        return alfrescoVersion;
    }
}
