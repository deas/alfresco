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
import org.alfresco.solr.NodeReport;
import org.alfresco.solr.TrackerState;
import org.alfresco.solr.client.GetNodesParameters;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.Node.SolrApiNodeStatus;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.client.Transaction;
import org.alfresco.solr.client.Transactions;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    public void doTrackWithOneTransactionUpdatesOnce() throws AuthenticationException, IOException, JSONException
    {
        TrackerState state = new TrackerState();
        state.setTimeToStopIndexing(2L);
        when(srv.getTrackerState()).thenReturn(state);

        Transactions txs = mock(Transactions.class);
        List<Transaction> txsList = new ArrayList<>();
        Transaction tx = new Transaction();
        tx.setCommitTimeMs(1L);
        txsList.add(tx);
        when(txs.getTransactions()).thenReturn(txsList);

        // Subsequent calls to getTransactions must return a different set of transactions to avoid an infinite loop
        when(repositoryClient.getTransactions(anyLong(), anyLong(), anyLong(), anyLong(), anyInt())).thenReturn(txs)
                    .thenReturn(mock(Transactions.class));

        List<Node> nodes = new ArrayList<>();
        Node node = new Node();
        nodes.add(node );
        when(repositoryClient.getNodes(any(GetNodesParameters.class), anyInt())).thenReturn(nodes);
        
        this.metadataTracker.doTrack();

        InOrder inOrder = inOrder(srv);
        inOrder.verify(srv).indexNode(node, true);
        inOrder.verify(srv).indexTransaction(tx, true);
        inOrder.verify(srv).commit();
    }

    @Test
    public void doTrackWithNoTransactionsDoesNothing() throws AuthenticationException, IOException, JSONException
    {
        TrackerState state = new TrackerState();
        when(srv.getTrackerState()).thenReturn(state);

        Transactions txs = mock(Transactions.class);
        List<Transaction> txsList = new ArrayList<>();
        when(txs.getTransactions()).thenReturn(txsList);

        when(repositoryClient.getTransactions(anyLong(), anyLong(), anyLong(), anyLong(), anyInt())).thenReturn(txs);

        this.metadataTracker.doTrack();

        verify(srv, never()).commit();
    }

    @Test
    public void testCheckNode() throws AuthenticationException, IOException, JSONException
    {
        List<Node> nodes = new ArrayList<>();
        Node node = new Node();
        Long txnId = 10000000L;
        node.setTxnId(txnId);
        nodes.add(node);
        when(repositoryClient.getNodes(any(GetNodesParameters.class), eq(1))).thenReturn(nodes);
        
        Long dbId = 999L;
        NodeReport nodeReport = this.metadataTracker.checkNode(dbId);
        
        assertNotNull(nodeReport);
        assertEquals(dbId, nodeReport.getDbid());
        assertEquals(txnId, nodeReport.getDbTx());
        verify(srv).checkNodeCommon(nodeReport);
    }
    
}
