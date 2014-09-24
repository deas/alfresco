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
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.solr.IndexTrackingShutdownException;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.TrackerState;
import org.alfresco.solr.client.SOLRAPIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base class that provides common {@link Tracker} behaviour.
 * 
 * @author Matt Ward
 */
public abstract class AbstractTracker implements Tracker
{
    public static final long TIME_STEP_32_DAYS_IN_MS = 1000 * 60 * 60 * 24 * 32L;
    public static final long TIME_STEP_1_HR_IN_MS = 60 * 60 * 1000L;
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
    protected TrackerStats trackerStats;
    protected boolean runPostModelLoadInit = true;
    private int maxLiveSearchers;
    private volatile boolean shutdown = false;
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private volatile TrackerState state;
    
    /*
     * A thread handler can be used by subclasses, but they have to intentionally instantiate it.
     */
    protected ThreadHandler threadHandler;

    /**
     * Default constructor, strictly for testing.
     */
    protected AbstractTracker()
    {
    }
    
    protected AbstractTracker(SolrTrackerScheduler scheduler, Properties p, SOLRAPIClient client, 
                String coreName, InformationServer informationServer)
    {
        this.props = p;
        this.client = client;
        this.coreName = coreName;
        this.infoSrv = informationServer;

        storeRef = new StoreRef(p.getProperty("alfresco.stores", "workspace://SpacesStore"));
        batchCount = Integer.parseInt(p.getProperty("alfresco.batch.count", "1000"));
        maxLiveSearchers =  Integer.parseInt(p.getProperty("alfresco.maxLiveSearchers", "2"));
        isSlave =  Boolean.parseBoolean(p.getProperty("enable.slave", "false"));
        isMaster =  Boolean.parseBoolean(p.getProperty("enable.master", "true"));

        this.trackerStats = new TrackerStats(this.infoSrv);
        
        scheduler.schedule(this, coreName, p);

        alfrescoVersion = p.getProperty("alfresco.version", "5.0.0");
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
        // This ensures that we will get a new initial state each run
       

        readWriteLock.readLock().lock();
        try
        {
            if((state != null) && state.isRunning())
            {
                log.info("... " + this.getClass().getSimpleName() + " for core [" + coreName + "] is already running");
                return;
            }
        }
        finally
        {
            readWriteLock.readLock().unlock();
        }
        
        readWriteLock.writeLock().lock();
        try
        {
            if((state != null) && state.isRunning())
            {
                log.info("... " + this.getClass().getSimpleName() + " for core [" + coreName + "] is already running.");
                return;
            }
            else
            {
                log.info("... Running " + this.getClass().getSimpleName() + " for core ["+ coreName + "].");
                this.invalidateTrackerState();
                getTrackerState();
                state.setRunning(true);
            }
        }
        finally
        {
            readWriteLock.writeLock().unlock();
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
            readWriteLock.writeLock().lock();
            try
            {
                state.setRunning(false);
                state.setCheck(false);
            }
            finally
            {
                readWriteLock.writeLock().unlock();
            }
        }
    }
    
    private void invalidateTrackerState()
    {
        readWriteLock.writeLock().lock();
        try
        {
            state = null;
        }
        finally
        {
            readWriteLock.writeLock().unlock();
        }
    }
    
    @Override
    public TrackerState getTrackerState()
    {
        readWriteLock.readLock().lock();
        try
        {
            if(state != null) 
            {
               return state;
            }
        }
        finally
        {
            readWriteLock.readLock().unlock();
        }
        
        
        readWriteLock.writeLock().lock();
        try
        {
            if(state != null) 
            {
               return state;
            }
            else
            {
                state = this.infoSrv.getTrackerInitialState();
                return state;
            }
        }
        finally
        {
            readWriteLock.writeLock().unlock();
        }
    }
    
    

    /**
     * Allows time for the scheduled asynchronous tasks to complete
     */
    protected synchronized void waitForAsynchronous()
    {
        AbstractWorkerRunnable currentRunnable = this.threadHandler.peekHeadReindexWorker();
        while (currentRunnable != null)
        {
            checkShutdown();
            synchronized (this)
            {
                try
                {
                    wait(100);
                }
                catch (InterruptedException e)
                {
                }
            }
            currentRunnable = this.threadHandler.peekHeadReindexWorker();
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
