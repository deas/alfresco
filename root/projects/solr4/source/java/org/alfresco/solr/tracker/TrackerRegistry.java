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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrackerRegistry
{
    // Keyed on core name and Tracker class
    private Map<String, ConcurrentHashMap<Class<? extends Tracker>, Tracker>> trackers = new ConcurrentHashMap<>();

    /**
     * @return the trackers
     */
    public Map<String, ConcurrentHashMap<Class<? extends Tracker>, Tracker>> getTrackers()
    {
        return trackers;
    }
    
    public synchronized void register(String coreName, Tracker tracker)
    {
        Map<Class<? extends Tracker>, Tracker> coreTrackers = this.trackers.get(coreName);
        if (coreTrackers == null) 
        {
            coreTrackers = new ConcurrentHashMap<>();
        }
        
        coreTrackers.put(tracker.getClass(), tracker);
    }
}
