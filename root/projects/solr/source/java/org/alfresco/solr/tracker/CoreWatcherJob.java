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

import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.alfresco.solr.LegacySolrInformationServer;
import org.apache.solr.core.SolrCore;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreWatcherJob implements Job
{
    protected final static Logger log = LoggerFactory.getLogger(CoreWatcherJob.class);
    
    
    public CoreWatcherJob()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException
    {
        AlfrescoCoreAdminHandler adminHandler = (AlfrescoCoreAdminHandler) jec.getJobDetail().getJobDataMap().get("ADMIN_HANDLER");

        for (SolrCore core : adminHandler.getCoreContainer().getCores())
        {

            if (!adminHandler.getTrackers().containsKey(core.getName()))
            {
                if (core.getSolrConfig().getBool("alfresco/track", false))
                {
                    log.info("Starting to track " + core.getName());
                    
                    // Create information server and wire it up.  This will be done by a registry
                    LegacySolrInformationServer srv = new LegacySolrInformationServer(adminHandler, core);
                    adminHandler.getInformationServers().put(core.getName(), srv);
                    adminHandler.getTrackers().put(core.getName(), srv.getTracker());
                }
            }
        }
    }
    

}