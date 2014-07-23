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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.BeforeClass;
import org.junit.Test;

public class TrackerRegistryTest
{
    private static TrackerRegistry reg = new TrackerRegistry();
    private static Tracker aclTracker = new AclTracker();
    private static Tracker contentTracker = new ContentTracker();
    private static Tracker metadataTracker = new MetadataTracker();
    private static Tracker modelTracker = new ModelTracker();
    private static final String CORE_NAME = "coreName";

    public static void registerTrackers(String coreName)
    {
        reg.register(coreName, aclTracker);
        reg.register(coreName, contentTracker );
        reg.register(coreName, metadataTracker);
        reg.register(coreName, modelTracker);
    }
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        registerTrackers(CORE_NAME);
    }

    
    @Test
    public void testGetTrackers()
    {
        Map<String, ConcurrentHashMap<Class<? extends Tracker>, Tracker>> coreTrackers = reg.getTrackers();
        assertNotNull(coreTrackers);
        ConcurrentHashMap<Class<? extends Tracker>, Tracker> trackers = coreTrackers.get(CORE_NAME);
        assertNotNull(trackers);
        assertTrue(trackers.containsKey(AclTracker.class));
        assertTrue(trackers.containsKey(ContentTracker.class));
        assertTrue(trackers.containsKey(MetadataTracker.class));
        assertTrue(trackers.containsKey(ModelTracker.class));
    }

    @Test
    public void testGetTrackersForCore()
    {
        Collection<Tracker> trackersForCore = reg.getTrackersForCore(CORE_NAME);
        assertNotNull(trackersForCore);
        assertFalse(trackersForCore.isEmpty());
        assertTrue(trackersForCore.contains(aclTracker));
        assertTrue(trackersForCore.contains(contentTracker));
        assertTrue(trackersForCore.contains(modelTracker));
        assertTrue(trackersForCore.contains(metadataTracker));
    }

    @Test
    public void testHasTrackersForCore()
    {
        assertTrue(reg.hasTrackersForCore(CORE_NAME));
        assertFalse(reg.hasTrackersForCore("abc"));
    }

    @Test
    public void testGetTrackerForCore()
    {
        assertEquals(aclTracker, reg.getTrackerForCore(CORE_NAME, AclTracker.class));
        assertEquals(contentTracker, reg.getTrackerForCore(CORE_NAME, ContentTracker.class));
        assertEquals(metadataTracker, reg.getTrackerForCore(CORE_NAME, MetadataTracker.class));
        assertEquals(modelTracker, reg.getTrackerForCore(CORE_NAME, ModelTracker.class));
    }
}
