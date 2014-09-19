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

    @Before
    public void setUp() throws Exception
    {
        doReturn("workspace://SpacesStore").when(props).getProperty("alfresco.stores");
        this.contentTracker = new ContentTracker(scheduler, props, repositoryClient, coreName, srv);
    }

    @Test
    public void doTrackWithNoContentDoesNothing() throws Exception
    {
        this.contentTracker.doTrack();
        
        verify(srv, never()).commit();
    }

    @Test
    public void doTrackWithContentUpdatesContent() throws Exception
    {
        List<TenantAclIdDbId> buckets = new ArrayList<>();
        List<TenantAclIdDbId> buckets2 = new ArrayList<>();
        TenantAclIdDbId bucket = new TenantAclIdDbId();
        bucket.dbId = 0l;
        bucket.tenant = "";
        buckets.add(bucket);
        when(this.srv.getDocsWithUncleanContent(anyInt(), anyInt())).thenReturn(buckets).thenReturn(buckets2);
        this.contentTracker.doTrack();
        
        verify(srv).updateContentToIndexAndCache(anyLong(), anyString());
        verify(srv).commit();
    }
    
    @Test
    public void testCheckIndex()
    {
        // TODO
    }

}
