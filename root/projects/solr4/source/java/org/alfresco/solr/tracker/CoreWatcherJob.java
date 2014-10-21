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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;

import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.solr.AlfrescoCoreAdminHandler;
import org.alfresco.solr.AlfrescoSolrCloseHook;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.alfresco.solr.SolrInformationServer;
import org.alfresco.solr.SolrKeyResourceLoader;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.SOLRAPIClientFactory;
import org.alfresco.solr.content.SolrContentStore;
import org.apache.solr.core.CoreContainer;
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
        CoreContainer coreContainer = adminHandler.getCoreContainer();
        for (SolrCore core : coreContainer.getCores())
        {
            logIfDebugEnabled("About to enter synchronized block for core " + core.getName());
            // Prevents other threads from creating trackers for this core before its trackers are done registering
            synchronized (core)
            {
                logIfDebugEnabled("Entered synchronized block for core " + core.getName());

                String coreName = core.getName();
                TrackerRegistry trackerRegistry = adminHandler.getTrackerRegistry();
                if (!trackerRegistry.hasTrackersForCore(coreName))
                {
                    registerForCore(adminHandler, coreContainer, core, coreName, trackerRegistry);
                }

                logIfDebugEnabled("Exiting synchronized block for core " + core.getName());
            }
        }
    }

    private void logIfDebugEnabled(String debugString)
    {
        if (log.isDebugEnabled())
        {
            log.info(debugString);
        }
    }

    /**
     * Registers with the admin handler the information server and the trackers.
     */
    private void registerForCore(AlfrescoCoreAdminHandler adminHandler, CoreContainer coreContainer, SolrCore core,
                String coreName, TrackerRegistry trackerRegistry) throws JobExecutionException
    {
        Properties props = new CoreDescriptorDecorator(core.getCoreDescriptor()).getCoreProperties();
        if (Boolean.parseBoolean(props.getProperty("enable.alfresco.tracking", "false")))
        {
            core.addCloseHook(new AlfrescoSolrCloseHook(adminHandler));

            SolrTrackerScheduler scheduler = adminHandler.getScheduler();
            SolrResourceLoader loader = core.getLatestSchema().getResourceLoader();
            SolrKeyResourceLoader keyResourceLoader = new SolrKeyResourceLoader(loader);
            SOLRAPIClientFactory clientFactory = new SOLRAPIClientFactory();
            SOLRAPIClient repositoryClient = clientFactory.getSOLRAPIClient(props, keyResourceLoader,
                        AlfrescoSolrDataModel.getInstance().getDictionaryService(CMISStrictDictionaryService.DEFAULT),
                        AlfrescoSolrDataModel.getInstance().getNamespaceDAO());
            SolrContentStore solrContentStore = this.getSolrContentStore(coreContainer);
            SolrInformationServer srv = new SolrInformationServer(adminHandler, core, repositoryClient,
                        solrContentStore);
            adminHandler.getInformationServers().put(coreName, srv);

            log.info("Starting to track " + coreName);
            if (trackerRegistry.getModelTracker() == null)
            {
                logIfDebugEnabled("Creating ModelTracker when registering trackers for core " + coreName);
                ModelTracker mTracker = new ModelTracker(coreContainer.getSolrHome(), props, repositoryClient,
                        coreName, srv);
                trackerRegistry.setModelTracker(mTracker);
                scheduler.schedule(mTracker, coreName, props);
            }

            AclTracker aclTracker = new AclTracker(props, repositoryClient, coreName, srv);
            trackerRegistry.register(coreName, aclTracker);
            scheduler.schedule(aclTracker, coreName, props);

            ContentTracker contentTrkr = new ContentTracker(props, repositoryClient, coreName, srv);
            trackerRegistry.register(coreName, contentTrkr);
            scheduler.schedule(contentTrkr, coreName, props);

            MetadataTracker metaTrkr = new MetadataTracker(props, repositoryClient, coreName, srv);
            trackerRegistry.register(coreName, metaTrkr);
            scheduler.schedule(metaTrkr, coreName, props);
        }
    }

    private SolrContentStore getSolrContentStore(CoreContainer coreContainer) throws JobExecutionException
    {
        // TODO: Could specify the rootStr from a properties file.
        return new SolrContentStore(locateContentHome(coreContainer.getSolrHome()));
    }
    
    public static String locateContentHome(String solrHome)
    {
        String contentDir = null;
        // Try JNDI
        try
        {
            Context c = new InitialContext();
            contentDir = (String) c.lookup("java:comp/env/solr/content/dir");
            log.info("Using JNDI solr.content.dir: " + contentDir);
        }
        catch (NoInitialContextException e)
        {
            log.info("JNDI not configured for solr (NoInitialContextEx)");
        }
        catch (NamingException e)
        {
            log.info("No solr/content/dir in JNDI");
        }
        catch (RuntimeException ex)
        {
            log.warn("Odd RuntimeException while testing for JNDI: " + ex.getMessage());
        }

        // Now try system property
        if (contentDir == null)
        {
            String prop = "solr.solr.content.dir";
            contentDir = System.getProperty(prop);
            if (contentDir != null)
            {
                log.info("using system property " + prop + ": " + contentDir);
            }
        }

        // if all else fails, try
        if (contentDir == null)
        {
            return solrHome + "ContentStore";

        }
        else
        {
            return contentDir;
        }
    }
}
