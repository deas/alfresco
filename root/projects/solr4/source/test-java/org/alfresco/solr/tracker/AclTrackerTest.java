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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import org.alfresco.solr.IndexTrackingShutdownException;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.TrackerState;
import org.alfresco.solr.client.SOLRAPIClient;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for the {@link AclTracker} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class AclTrackerTest
{
    private static final Object CUSTOM_ALFRESCO_VERSION = "99.9.9";
    // The class under test
    private AclTracker tracker;
    private @Mock SolrTrackerScheduler scheduler;
    private @Mock SOLRAPIClient client;
    private @Mock InformationServer informationServer;
    private TrackerState trackerState;
    
    @Before
    public void setUp() throws Exception
    {
        trackerState = new TrackerState();
        trackerState.setRunning(false); // Nothing would happen if it were already running.
        Properties props = createProperties();
        tracker = new AclTracker(scheduler, "tracker-id", props, client, "core-name", informationServer);
    }

    private Properties createProperties()
    {
        Properties props = new Properties();
        props.put("alfresco.stores", "workspace://SpacesStore");
        props.put("alfresco.version", CUSTOM_ALFRESCO_VERSION);
        return props;
    }

    protected void testTrackChangesRan()
    {
        when(informationServer.getTrackerState()).thenReturn(trackerState);
        
        tracker.track();

        // TODO: verify a mock instead?
        assertFalse(trackerState.isRunning());
        assertFalse(trackerState.isCheck());
    }
    
    
    // Tests the purgeAclChangeSets() call made in AclTracker.doTrack()
    // TODO: the other operations in doTrack().
    @Test
    public void checkTrackingWhenAclChangeSetsToPurge() throws IllegalAccessException, IOException
    {
        @SuppressWarnings("unchecked")
        ConcurrentLinkedQueue<Long> aclsToPurge = (ConcurrentLinkedQueue<Long>)
        FieldUtils.readField(tracker, "aclChangeSetsToPurge", true);
        
        aclsToPurge.add(101L);
        aclsToPurge.add(102L);
        aclsToPurge.add(103L);
        
        testTrackChangesRan();
        
        //verify infoSrv.deleteByAclChangeSetId(aclChangeSetId);
        verify(informationServer).deleteByAclChangeSetId(101L);
        verify(informationServer).deleteByAclChangeSetId(102L);
        verify(informationServer).deleteByAclChangeSetId(103L);
        
        // TODO: verify checkShutdown
        verify(informationServer).commit();
    }

    @Test
    public void trackingAbortsWhenAlreadyRunning() throws Throwable
    {
        // Want to be able to verify doTrack() was called.
        tracker = spy(tracker);
        
        trackerState.setRunning(true);
        // Prove running state, before attempt to track()
        assertTrue(trackerState.isRunning());
        
        when(informationServer.getTrackerState()).thenReturn(trackerState);
        tracker.track();

        // Still running - these values are unaffected.
        assertTrue(trackerState.isRunning());
        assertFalse(trackerState.isCheck());
        
        // Prove doTrack() was not called
        verify(tracker, never()).doTrack();
    }
    
    @Test
    public void willRollbackOnThrowableDuringTracking() throws Throwable
    {
        // Need to be able to mock doTrack() behaviour.
        tracker = spy(tracker);
        doThrow(new RuntimeException("Simulated problem during tracking")).when(tracker).doTrack();
        
        testTrackChangesRan();
        
        verify(informationServer).rollback();
    }
    
    @Test
    public void willRollbackOnIndexTrackingShutdownException() throws Throwable
    {
        // Need to be able to mock doTrack() behaviour.
        tracker = spy(tracker);
        doThrow(new IndexTrackingShutdownException()).when(tracker).doTrack();
        
        testTrackChangesRan();
        
        verify(informationServer).rollback();
    }
    
    @Ignore("Not yet implemented.")
    @Test
    public void canCheckIndex()
    {
        // TODO
//        tracker.checkIndex(fromTx, toTx, fromAclTx, toAclTx, fromTime, toTime);
    }
    
    @Test
    public void canGetAlfrescoVersion()
    {
        // Check we're testing something useful
        assertNotNull(CUSTOM_ALFRESCO_VERSION);
        // Check alfresco version retrieved from properties
        assertEquals(CUSTOM_ALFRESCO_VERSION, tracker.getAlfrescoVersion());
    }
    
    
    @Test
    public void canClose() throws IllegalAccessException
    {
        ExecutorService threadPool = (ExecutorService) FieldUtils.readField(tracker, "threadPool", true);
        threadPool = spy(threadPool);
        FieldUtils.writeField(tracker, "threadPool", threadPool, true);
        
        tracker.close();
        
        // AclTracker specific
        verify(threadPool).shutdownNow();
        
        // Applicable to all AbstractAclTracker
        verify(client).close();
    }
}
