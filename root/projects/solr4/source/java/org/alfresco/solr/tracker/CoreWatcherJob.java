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

import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.alfresco.solr.AlfrescoSolrCloseHook;
import org.alfresco.solr.SolrInformationServer;
import org.alfresco.solr.SolrKeyResourceLoader;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.SOLRAPIClientFactory;
import org.apache.solr.core.CoreDescriptorDecorator;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This job when scheduled periodically goes through the Solr cores and sets up trackers, etc. for each core.
 */
public class CoreWatcherJob implements Job
{
    public static final String JOBDATA_ADMIN_HANDLER_KEY = "ADMIN_HANDLER";
    protected static final Logger log = LoggerFactory.getLogger(CoreWatcherJob.class);

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException
    {
        AlfrescoCoreAdminHandler adminHandler = (AlfrescoCoreAdminHandler) jec.getJobDetail().getJobDataMap()
                    .get(JOBDATA_ADMIN_HANDLER_KEY);

        for (SolrCore core : adminHandler.getCoreContainer().getCores())
        {
            String coreName = core.getName();
            TrackerRegistry trackerRegistry = adminHandler.getTrackerRegistry();
            if (!trackerRegistry.hasTrackersForCore(coreName))
            {
            	Properties props = new CoreDescriptorDecorator(core.getCoreDescriptor()).getCoreProperties();
                if (Boolean.parseBoolean(props.getProperty("enable.alfresco.tracking", "false")))
                {
                    log.info("Starting to track " + coreName);

                    core.addCloseHook(new AlfrescoSolrCloseHook(adminHandler));

                    // Registers the information server and the trackers.
                    SolrInformationServer srv = new SolrInformationServer(adminHandler, core);
                    adminHandler.getInformationServers().put(coreName, srv);

                    SolrTrackerScheduler scheduler = adminHandler.getScheduler();
                    SolrResourceLoader loader = core.getLatestSchema().getResourceLoader();
                    SolrKeyResourceLoader keyResourceLoader = new SolrKeyResourceLoader(loader);

                    SOLRAPIClientFactory clientFactory = new SOLRAPIClientFactory();
                    SOLRAPIClient repositoryClient = clientFactory.getSOLRAPIClient(props, keyResourceLoader,
                                srv.getDictionaryService(CMISStrictDictionaryService.DEFAULT), srv.getNamespaceDAO());

                    if (trackerRegistry.getModelTracker() == null)
                    {
                        ModelTracker mTracker = new ModelTracker(scheduler, adminHandler.getCoreContainer().getSolrHome(), props, repositoryClient, coreName, srv);
                        trackerRegistry.setModelTracker(mTracker);
                    }

                    AclTracker aclTracker = new AclTracker(scheduler, props, repositoryClient, coreName, srv);
                    trackerRegistry.register(coreName, aclTracker);

                    ContentTracker contentTracker = new ContentTracker();
                    trackerRegistry.register(coreName, contentTracker);

                    MetadataTracker metadataTracker = new MetadataTracker();
                    trackerRegistry.register(coreName, metadataTracker);
                }
            }
        }
    }
}
