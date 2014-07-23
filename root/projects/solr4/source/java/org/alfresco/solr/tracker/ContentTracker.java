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

import org.alfresco.solr.InformationServer;
import org.alfresco.solr.SolrInformationServer.TenantAndDbId;
import org.alfresco.solr.client.SOLRAPIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The tracker is triggered and then queries for docs with FTSSTATUS (Full Text Search Status) set to something other than clean.
 * States may be NONE - first time indexed; STALE - using old content for the node; OK - current; Clean - TODO
 * Then it fixes up the docs.
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
        List<TenantAndDbId> buckets = this.infoSrv.getDocsWithUncleanContent();
        
        for (TenantAndDbId bucket : buckets)
        {
            // update the content
            this.infoSrv.updateContentToIndexAndCache(bucket.dbId, bucket.tenant);
        }
        super.infoSrv.commit();
        
    }

    @Override
    public IndexHealthReport checkIndex(Long fromTx, Long toTx, Long fromAclTx, Long toAclTx, Long fromTime, Long toTime)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
