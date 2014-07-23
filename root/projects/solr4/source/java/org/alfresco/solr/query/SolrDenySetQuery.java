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

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Query for a set of denied authorities.
 * 
 * @author Matt Ward
 */
public class SolrDenySetQuery extends AbstractAuthoritySetQuery
{
    public SolrDenySetQuery(String authorities)
    {
        super(authorities);
    }
    
    /*
     * @see org.apache.lucene.search.Query#createWeight(org.apache.lucene.search.Searcher)
     */
    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException
    {
        if(!(searcher instanceof SolrIndexSearcher))
        {
            throw new IllegalStateException("Must have a SolrIndexSearcher");
        }
        return new SolrDenySetQueryWeight((SolrIndexSearcher)searcher, this);
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DENYSET:");
        stringBuilder.append(authorities);
        return stringBuilder.toString();
    }

    
    private class SolrDenySetQueryWeight extends AbstractAuthoritySetQueryWeight
    {
        public SolrDenySetQueryWeight(SolrIndexSearcher searcher, SolrDenySetQuery query) throws IOException 
        {
            super(searcher, query, "DENYSET");
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, Bits acceptDocs) throws IOException
        {
            AtomicReader reader = context.reader();
            return SolrDenySetScorer.createDenySetScorer(this, searcher, query.authorities, reader);
        }
    }
}
