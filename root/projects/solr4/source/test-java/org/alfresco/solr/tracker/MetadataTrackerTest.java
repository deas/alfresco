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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.TrackerState;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.client.Transactions;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetadataTrackerTest
{
    private MetadataTracker metadataTracker;

    @Mock
    private SolrTrackerScheduler scheduler;
    @Mock
    private SOLRAPIClient repositoryClient;
    private String coreName = "theCoreName";
    @Mock
    private InformationServer srv;
    @Mock
    private Properties props;
    
    @Before
    public void setUp() throws Exception
    {
        when(props.getProperty("alfresco.stores")).thenReturn("workspace://SpacesStore");
        when(props.getProperty("alfresco.batch.count", "1000")).thenReturn("1000");
        when(props.getProperty("alfresco.maxLiveSearchers", "2")).thenReturn("2");
        when(props.getProperty("enable.slave", "false")).thenReturn("false");
        when(props.getProperty("enable.master", "true")).thenReturn("true");

        this.metadataTracker = new MetadataTracker(scheduler, props, repositoryClient, coreName, srv);
    }
    
    @Test
    public void testDoTrack() throws AuthenticationException, IOException, JSONException
    {
        doReturn(new TrackerState()).when(srv).getTrackerState();
        Transactions txs = mock(Transactions.class);
        List<Transaction> txsList = new ArrayList<>();
        when(txs.getTransactions()).thenReturn(txsList);
        when(repositoryClient.getTransactions(anyLong(), anyLong(), anyLong(), anyLong(), anyInt())).thenReturn(txs);
        this.metadataTracker.doTrack();
        
        
    }

}
