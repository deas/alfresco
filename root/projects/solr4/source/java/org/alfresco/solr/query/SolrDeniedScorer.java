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

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.solr.cache.CacheConstants;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Find the set of docs denied to an authority.
 * 
 * @author Matt Ward
 */
public class SolrDeniedScorer extends AbstractSolrCachingScorer
{
    SolrDeniedScorer(Weight weight, DocSet in, AtomicReaderContext context, SolrIndexSearcher searcher)
    {
        super(weight, in, context, searcher);
    }

    public static SolrDeniedScorer createDenyScorer(Weight weight, AtomicReaderContext context, SolrIndexSearcher searcher, String authority) throws IOException
    {     
        DocSet deniedDocs = (DocSet) searcher.cacheLookup(CacheConstants.ALFRESCO_DENIED_CACHE, authority);

        if (deniedDocs == null)
        {
            // Cache miss: query the index for docs where the denial matches the authority. 
            deniedDocs = searcher.getDocSet(new TermQuery(new Term(QueryConstants.FIELD_DENIED, authority)));
            searcher.cacheInsert(CacheConstants.ALFRESCO_DENIED_CACHE, authority, deniedDocs);
        }
        return new SolrDeniedScorer(weight, deniedDocs, context, searcher);
    }
}
