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

import java.io.IOException;

import org.apache.lucene.search.Query;
import org.apache.solr.search.CacheRegenerator;
import org.apache.solr.search.SolrCache;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * @author Andy
 *
 */
public class FilterCacheRegenerator implements CacheRegenerator
{

    /* (non-Javadoc)
     * @see org.apache.solr.search.CacheRegenerator#regenerateItem(org.apache.solr.search.SolrIndexSearcher, org.apache.solr.search.SolrCache, org.apache.solr.search.SolrCache, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean regenerateItem(SolrIndexSearcher newSearcher, SolrCache newCache, SolrCache oldCache, Object oldKey, Object oldVal) throws IOException
    {
        if(oldKey instanceof Query)
        {
            Object cache = newSearcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ALL_LEAF_DOCS);
            if(cache != null)
            {
                newSearcher.cacheDocSet((Query)oldKey, null, false);
            }
        }
        return true;
            
    }

}
