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

import java.util.Collection;

import org.alfresco.solr.tracker.Tracker;
import org.alfresco.solr.tracker.TrackerRegistry;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.SolrCore;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlfrescoSolrCloseHook extends CloseHook
{
    protected final static Logger log = LoggerFactory.getLogger(AlfrescoSolrCloseHook.class);
    
    private TrackerRegistry trackerRegistry;
    private Scheduler scheduler;
    
    public AlfrescoSolrCloseHook(AlfrescoCoreAdminHandler adminHandler)
    {
        this.trackerRegistry = adminHandler.getTrackerRegistry();
        this.scheduler = adminHandler.getScheduler();
    }
    
    @Override
    public void postClose(SolrCore core)
    {
        setTrackersShutdown(core);
        
        try
        {
            Collection<Tracker> coreTrackers = trackerRegistry.getTrackersForCore(core.getName());
            
            scheduler.deleteJob("CoreTracker-" + core.getName(), "Solr");
            trackerRegistry.removeTrackersForCore(core.getName());
            
            // Shuts down the scheduler if all cores have been closed
            if (trackerRegistry.getCoreNames().size() == 0)
            {
                if (!scheduler.isShutdown())
                {
                    scheduler.pauseAll();
                    scheduler.shutdown();
                }
            }

            for (Tracker tracker : coreTrackers)
            {
                tracker.close();
            }
        }
        catch (SchedulerException e)
        {
            log.error("Failed to shutdown scheduler", e);
        }
    }

    @Override
    public void preClose(SolrCore core)
    {
        setTrackersShutdown(core);
    }

    /**
     * Sets the shutdown flag on the trackers to stop them from doing any more work
     * @param core the core that is closing
     */
    private void setTrackersShutdown(SolrCore core)
    {
        trackerRegistry.getModelTracker().setShutdown(true);
        Collection<Tracker> coreTrackers = trackerRegistry.getTrackersForCore(core.getName());
        
        for(Tracker tracker : coreTrackers)
        {
            tracker.setShutdown(true);
        }
    }

}
