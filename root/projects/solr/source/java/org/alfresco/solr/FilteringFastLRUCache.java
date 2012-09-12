/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import org.apache.solr.search.FastLRUCache;

/**
 * @author Andy
 *
 */
public class FilteringFastLRUCache extends  FastLRUCache
{

   


    /**
     * @param key
     * @param value
     * @return
     * @see org.apache.solr.search.SolrCache#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value)
    {
        if(key instanceof ContextAwareQuery)
        {
             return super.put(key, value);
        }
        else
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.solr.search.FastLRUCache#get(java.lang.Object)
     */
    @Override
    public Object get(Object key)
    {
        if(key instanceof ContextAwareQuery)
        {
             return super.get(key);
        }
        else
        {
            return null;
        }
    }

    
}
