/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.solr;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.search.CacheRegenerator;
import org.apache.solr.search.SolrCache;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Solr cache supporting the reuse of {@link ResizeableArrayList} objects across cache instances.
 * 
 * Entries are stored in a persistence object when the cache is closed, and can then be reused when a new cache is created.   
 *
 * @author Alex Miller
 */
public class ArrayListCache implements SolrCache
{
    /**
     * Solr cache persistence object supporting the reuse of {@link ResizeableArrayList} instances across cache instances  
     */
    private static class Persistence {
        private ConcurrentHashMap<Object, ResizeableArrayList<Object>> pooledLists = new ConcurrentHashMap<Object, ResizeableArrayList<Object>>();

        /**
         * Borrow a {@link ResizeableArrayList} instance, for the given cache entry key.
         */
        public ResizeableArrayList<Object> borrow(Object key)
        {
            return pooledLists.remove(key);
        }

        /**
         * Store the {@link ResizeableArrayList} instance for the given cache entry key, for reuse in another cache instance.
         */
        public void returnObject(Object key, ResizeableArrayList<Object> value)
        {
            pooledLists.put(key, value);
        }
        
    }
    private Persistence persistence;
    
    private String description;
    private String name;
    private State state;

    private ConcurrentHashMap<Object, ResizeableArrayList<Object>> currentLists = new ConcurrentHashMap<Object, ResizeableArrayList<Object>>();

    @Override
    public String getName()
    {
        return this.getClass().getName();
    }

    @Override
    public String getVersion()
    {
        return SolrCore.version;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public Category getCategory()
    {
        return Category.CACHE;
    }

    @Override
    public String getSourceId()
    {
        return "ArrayListCache.java";
    }

    @Override
    public String getSource()
    {
        return null;
    }

    @Override
    public URL[] getDocs()
    {
        return null;
    }

    @Override
    public NamedList<?> getStatistics()
    {
        return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object init(Map args, Object existingPersistence, CacheRegenerator regenerator)
    {
        state = State.CREATED;
        name = (String) args.get("name");
        persistence = (Persistence) existingPersistence;
        if (persistence == null) {
          persistence = new Persistence();
        }
        return persistence;
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public int size()
    {
        return currentLists.size();
    }

    @Override
    public Object put(Object key, Object value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(Object key)
    {
        ResizeableArrayList<Object> result = currentLists.get(key);
        if (result == null)
        {
            // Attempt to borrow the instance for the given key from the persistence object.
            // If one doesn't exist, create a new one.
            result = persistence.borrow(key);
            if (result == null)
            {
                result = new ResizeableArrayList<Object>();
            }
            result.activate();
            currentLists.put(key, result);
        }
        return result;
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setState(State state)
    {
        this.state = state;
    }

    @Override
    public State getState()
    {
        return state;
    }

    @Override
    public void warm(SolrIndexSearcher searcher, SolrCache old) throws IOException
    {
    }

    @Override
    public void close()
    {
        // Store entries in the persistence object for reuse by future cache instances.
        for (Map.Entry<Object, ResizeableArrayList<Object>> entry : currentLists.entrySet())
        {
            ResizeableArrayList<Object> value = entry.getValue();
            value.deactivate();
            persistence.returnObject(entry.getKey(), value);
        }
    }

}
