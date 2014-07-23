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

import java.util.Properties;

import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.SolrInformationServer;
import org.alfresco.solr.SolrKeyResourceLoader;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreWatcherJob implements Job
{
    protected final static Logger log = LoggerFactory.getLogger(CoreWatcherJob.class);

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException
    {
        AlfrescoCoreAdminHandler adminHandler = (AlfrescoCoreAdminHandler) jec.getJobDetail().getJobDataMap().get("ADMIN_HANDLER");

        for (SolrCore core : adminHandler.getCoreContainer().getCores())
        {
            String coreName = core.getName();
            if (!adminHandler.getTrackerRegistry().hasTrackersForCore(coreName))
            {
                if (core.getSolrConfig().getBool("alfresco/track", false))
                {
                    log.info("Starting to track " + coreName);
                    
                    // Registers the information server and the trackers.
                    SolrInformationServer srv = new SolrInformationServer(adminHandler, core);
                    adminHandler.getInformationServers().put(coreName, srv);
                    
                    Scheduler scheduler = adminHandler.getScheduler();
                    SolrResourceLoader loader = core.getLatestSchema().getResourceLoader();
                    String id = loader.getInstanceDir();
                    Properties props = core.getResourceLoader().getCoreProperties();
                    SolrKeyResourceLoader keyResourceLoader = new SolrKeyResourceLoader(loader);
                    
                    AclTracker aclTracker = new AclTracker(scheduler, id, props, keyResourceLoader, coreName, srv);
                    adminHandler.getTrackerRegistry().register(coreName, aclTracker);
                    
                    ContentTracker contentTracker = new ContentTracker();
                    adminHandler.getTrackerRegistry().register(coreName, contentTracker);
                    
                    MetadataTracker metadataTracker = new MetadataTracker();
                    adminHandler.getTrackerRegistry().register(coreName, metadataTracker);
                    
                    // ModelTracker is created per adminHandler, not per core, and therefore is not registered.
                }
            }
        }

        
        
    }

}
