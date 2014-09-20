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
    

    public ContentTracker(SolrTrackerScheduler scheduler, Properties p, SOLRAPIClient client, String coreName,
                InformationServer informationServer)
    {
        super(scheduler, p, client, coreName, informationServer);
    }
    
    ContentTracker()
    {
        // Testing purposes only
    }
    
    @Override
    protected void doTrack() throws Exception
    {
        int registeredSearcherCount = this.infoSrv.getRegisteredSearcherCount();
        if(registeredSearcherCount >= getMaxLiveSearchers())
        {
            log.info(".... skipping tracking registered searcher count = " + registeredSearcherCount);
            return;
        }
        
        checkShutdown();
        final int ROWS = 300;
        int start = 0;
        long totalDocs = 0l;
        List<TenantAclIdDbId> buckets = this.infoSrv.getDocsWithUncleanContent(start, ROWS);
        while (!buckets.isEmpty())
        {
            for (TenantAclIdDbId bucket : buckets)
            {
                // update the content
                this.infoSrv.updateContentToIndexAndCache(bucket.dbId, bucket.tenant);
            }
            
            this.infoSrv.commit();
            totalDocs += buckets.size();
            start += ROWS;
            buckets = this.infoSrv.getDocsWithUncleanContent(start, ROWS);
        }
        
        log.info("total number of docs with content updated: " + totalDocs);
    }

    @Override
    public IndexHealthReport checkIndex(Long fromTx, Long toTx, Long fromAclTx, Long toAclTx, Long fromTime, Long toTime)
    {
        // TODO Auto-generated method stub
        return new IndexHealthReport(infoSrv);
    }

}
