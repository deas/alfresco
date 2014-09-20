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
package org.alfresco.solr.query;

import java.io.IOException;

import org.alfresco.solr.cache.CacheConstants;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Caching wrapper for {@link SolrPathQuery}.
 *  
 * @author Matt Ward
 */
public class SolrCachingPathScorer extends AbstractSolrCachingScorer
{
    SolrCachingPathScorer(Weight weight, DocSet in, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher)
    {
        super(weight, in, context, acceptDocs, searcher);
    }


    /**
     * Factory method used to create {@link SolrCachingPathScorer} instances.
     * @param acceptDocs 
     */
    public static SolrCachingPathScorer create(SolrCachingPathWeight weight,
                                               AtomicReaderContext context,
                                               Bits acceptDocs, SolrIndexSearcher searcher,
                                               SolrPathQuery wrappedPathQuery) throws IOException
    {
        DocSet results = (DocSet) searcher.cacheLookup(CacheConstants.ALFRESCO_PATH_CACHE, wrappedPathQuery);
        if (results == null)
        {
            // Cache miss: get path query results and cache them
            results = searcher.getDocSet(wrappedPathQuery);
            searcher.cacheInsert(CacheConstants.ALFRESCO_PATH_CACHE, wrappedPathQuery, results);
        }
        
        return new SolrCachingPathScorer(weight, results, context, acceptDocs, searcher);
    }
}
