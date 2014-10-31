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

import java.util.List;
import java.util.Properties;

import org.alfresco.solr.AlfrescoSolrDataModel.TenantAclIdDbId;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.client.SOLRAPIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This tracker queries for docs with unclean content, and then updates them.
 * Similar to org.alfresco.repo.search.impl.lucene.ADMLuceneIndexerImpl
 * 
 * @author Ahmed Owian
 */
public class ContentTracker extends AbstractTracker implements Tracker
{
    protected final static Logger log = LoggerFactory.getLogger(ContentTracker.class);
    private int contentReadBatchSize;
    private int contentUpdateBatchSize;
    

    public ContentTracker(Properties p, SOLRAPIClient client, String coreName,
                InformationServer informationServer)
    {
        super(p, client, coreName, informationServer);
        contentReadBatchSize = Integer.parseInt(p.getProperty("alfresco.contentReadBatchSize", "4000"));
        contentUpdateBatchSize = Integer.parseInt(p.getProperty("alfresco.contentUpdateBatchSize", "1000"));
        threadHandler = new ThreadHandler(p, coreName, "ContentTracker");
    }
    
    ContentTracker()
    {
        // Testing purposes only
    }
    
    @Override
    protected void doTrack() throws Exception
    {
        long startElapsed = System.nanoTime();
        
        checkShutdown();
        int registeredSearcherCount = this.infoSrv.getRegisteredSearcherCount();
        if(registeredSearcherCount >= getMaxLiveSearchers())
        {
            log.info(".... skipping tracking registered searcher count = " + registeredSearcherCount);
            return;
        }
        
        final int ROWS = contentReadBatchSize;
        int start = 0;
        long totalDocs = 0l;
        checkShutdown();
        List<TenantAclIdDbId> docs = this.infoSrv.getDocsWithUncleanContent(start, ROWS);
        while (!docs.isEmpty())
        {
            int docsUpdatedSinceLastCommit = 0;
            for (TenantAclIdDbId doc : docs)
            {
                ContentIndexWorkerRunnable ciwr = new ContentIndexWorkerRunnable(super.threadHandler, doc, infoSrv);
                super.threadHandler.scheduleTask(ciwr);
                docsUpdatedSinceLastCommit ++;
                
                if (docsUpdatedSinceLastCommit >= contentUpdateBatchSize)
                {
                    registeredSearcherCount = super.infoSrv.getRegisteredSearcherCount();
                    if (registeredSearcherCount < getMaxLiveSearchers())
                    {
                        super.waitForAsynchronous();
                        checkShutdown();
                        this.infoSrv.commit();
                        long endElapsed = System.nanoTime();
                        trackerStats.addElapsedContentTime(docsUpdatedSinceLastCommit, endElapsed-startElapsed);
                        startElapsed = endElapsed;
                        docsUpdatedSinceLastCommit = 0;
                    }
                }
            }
            
            if (docsUpdatedSinceLastCommit > 0)
            {
                super.waitForAsynchronous();
                checkShutdown();
                this.infoSrv.commit();
                long endElapsed = System.nanoTime();
                trackerStats.addElapsedContentTime(docsUpdatedSinceLastCommit, endElapsed-startElapsed);
            }
            totalDocs += docs.size();
            start += ROWS;
            checkShutdown();
            docs = this.infoSrv.getDocsWithUncleanContent(start, ROWS);
        }
        
        log.info("total number of docs with content updated: " + totalDocs);
    }
    
    class ContentIndexWorkerRunnable extends AbstractWorkerRunnable
    {
        InformationServer infoServer;
        TenantAclIdDbId doc;

        ContentIndexWorkerRunnable(QueueHandler queueHandler, TenantAclIdDbId doc, InformationServer infoServer)
        {
            super(queueHandler);
            this.doc = doc;
            this.infoServer = infoServer;
        }

        @Override
        protected void doWork() throws Exception
        {
            checkShutdown();
            this.infoServer.updateContentToIndexAndCache(doc.dbId, doc.tenant);
        }
    }
}
