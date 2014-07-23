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
 * Find the set of readable documents for a given authority.
 *   
 * @author Matt Ward
 */
public class SolrReaderScorer extends AbstractSolrCachingScorer
{
    SolrReaderScorer(Weight weight, DocSet in, AtomicReaderContext context, SolrIndexSearcher searcher)
    {
        super(weight, in, context, searcher);
    }

    public static SolrReaderScorer createReaderScorer(Weight weight, AtomicReaderContext context, SolrIndexSearcher searcher, String authority) throws IOException
    {     
        DocSet readableDocs = (DocSet) searcher.cacheLookup(CacheConstants.ALFRESCO_READER_CACHE, authority);

        if (readableDocs == null)
        {
            // Cache miss: query the index for docs where the reader matches the authority. 
            readableDocs = searcher.getDocSet(new TermQuery(new Term(QueryConstants.FIELD_READER, authority)));
            searcher.cacheInsert(CacheConstants.ALFRESCO_READER_CACHE, authority, readableDocs);
        }
        return new SolrReaderScorer(weight, readableDocs, context, searcher);
    }
}
