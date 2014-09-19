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

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * {@link Weight} implementation for the {@link SolrCachingPathQuery}.
 * 
 * @author Matt Ward
 */
public class SolrCachingPathWeight extends Weight
{
    private SolrCachingPathQuery cachingPathQuery;
    private SolrIndexSearcher searcher;
    
    public SolrCachingPathWeight(SolrCachingPathQuery cachingPathQuery, SolrIndexSearcher searcher) throws IOException 
    {
        this.searcher = searcher;
        this.cachingPathQuery = cachingPathQuery;
    }

    @Override
    public Explanation explain(AtomicReaderContext context, int doc) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Query getQuery()
    {
        return cachingPathQuery;
    }

    @Override
    public float getValueForNormalization() throws IOException
    {
        return 1.0f;
    }

    @Override
    public void normalize(float norm, float topLevelBoost)
    {
    }

    @Override
    public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException
    {
        return SolrCachingPathScorer.create(this, context, searcher, cachingPathQuery.pathQuery);
    }
}
