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

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.alfresco.solr.AlfrescoSolrDataModel.TenantAclIdDbId;
import org.alfresco.solr.SolrInformationServer;
import org.alfresco.solr.client.SOLRAPIClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContentTrackerTest
{
    private ContentTracker contentTracker;
    
    @Mock
    private SolrTrackerScheduler scheduler;
    @Mock
    private SOLRAPIClient repositoryClient;
    private String coreName = "theCoreName";
    @Mock
    private SolrInformationServer srv;
    @Spy
    private Properties props;

    private int UPDATE_BATCH = 2;
    private int READ_BATCH = 400;

    @Before
    public void setUp() throws Exception
    {
        doReturn("workspace://SpacesStore").when(props).getProperty(eq("alfresco.stores"), anyString());
        doReturn("" + UPDATE_BATCH).when(props).getProperty(eq("alfresco.contentUpdateBatchSize"), anyString());
        doReturn("" + READ_BATCH).when(props).getProperty(eq("alfresco.contentReadBatchSize"), anyString());
        this.contentTracker = new ContentTracker(scheduler, props, repositoryClient, coreName, srv);
    }

    @Test
    public void doTrackWithNoContentDoesNothing() throws Exception
    {
        this.contentTracker.doTrack();
        verify(srv, never()).updateContentToIndexAndCache(anyLong(), anyString());
        verify(srv, never()).commit();
    }

    @Test
    public void doTrackWithContentUpdatesContent() throws Exception
    {
        List<TenantAclIdDbId> buckets1 = new ArrayList<>();
        List<TenantAclIdDbId> buckets2 = new ArrayList<>();
        List<TenantAclIdDbId> emptyList = new ArrayList<>();
        TenantAclIdDbId bucket = new TenantAclIdDbId();
        bucket.dbId = 0l;
        bucket.tenant = "";
        // Adds one more than the UPDATE_BATCH
        for (int i = 0; i <= UPDATE_BATCH; i++)
        {
            buckets1.add(bucket);
            buckets2.add(bucket);
        }
        // Keeps only UPDATE_BATCH buckets
        buckets2.remove(UPDATE_BATCH);
        when(this.srv.getDocsWithUncleanContent(anyInt(), anyInt()))
            .thenReturn(buckets1)
            .thenReturn(buckets2)
            .thenReturn(emptyList);
        this.contentTracker.doTrack();
        
        InOrder order = inOrder(srv);
        order.verify(srv).getDocsWithUncleanContent(0, READ_BATCH);
        
        // From buckets1
        order.verify(srv, times(UPDATE_BATCH)).updateContentToIndexAndCache(bucket.dbId, bucket.tenant);
        order.verify(srv).commit();
        // The one extra bucket should be processed and then committed
        order.verify(srv).updateContentToIndexAndCache(bucket.dbId, bucket.tenant);
        order.verify(srv).commit();
        
        order.verify(srv).getDocsWithUncleanContent(0 + READ_BATCH, READ_BATCH);
        
        // From buckets2
        order.verify(srv, times(UPDATE_BATCH)).updateContentToIndexAndCache(bucket.dbId, bucket.tenant);
        order.verify(srv).commit();
        
        order.verify(srv).getDocsWithUncleanContent(0 + READ_BATCH + READ_BATCH, READ_BATCH);
    }
}
