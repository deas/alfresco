/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
import java.util.Set;

import org.alfresco.service.cmr.search.SearchParameters;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andy
 *
 */
public class ContextAwareQuery extends Query
{
    protected final static Logger log = LoggerFactory.getLogger(ContextAwareQuery.class);

    private Query luceneQuery;
    
    private SearchParameters searchParameters;
    
    /**
     * @param luceneQuery
     */
    public ContextAwareQuery(Query luceneQuery, SearchParameters searchParameters)
    {
        this.luceneQuery = luceneQuery;
        this.searchParameters = searchParameters;
    }

    /**
     * @param b
     * @see org.apache.lucene.search.Query#setBoost(float)
     */
    public void setBoost(float b)
    {
        luceneQuery.setBoost(b);
    }

    /**
     * @return
     * @see org.apache.lucene.search.Query#getBoost()
     */
    public float getBoost()
    {
        return luceneQuery.getBoost();
    }

    /**
     * @param field
     * @return
     * @see org.apache.lucene.search.Query#toString(java.lang.String)
     */
    public String toString(String field)
    {
        return luceneQuery.toString(field);
    }

    /**
     * @return
     * @see org.apache.lucene.search.Query#toString()
     */
    public String toString()
    {
        return luceneQuery.toString();
    }

    /**
     * @param searcher
     * @return
     * @throws IOException
     * @see org.apache.lucene.search.Query#createWeight(org.apache.lucene.search.Searcher)
     */
    public Weight createWeight(Searcher searcher) throws IOException
    {
        return luceneQuery.createWeight(searcher);
    }

    /**
     * @param searcher
     * @return
     * @throws IOException
     * @see org.apache.lucene.search.Query#weight(org.apache.lucene.search.Searcher)
     */
    public Weight weight(Searcher searcher) throws IOException
    {
        return luceneQuery.weight(searcher);
    }

    /**
     * @param reader
     * @return
     * @throws IOException
     * @see org.apache.lucene.search.Query#rewrite(org.apache.lucene.index.IndexReader)
     */
    public Query rewrite(IndexReader reader) throws IOException
    {
        return luceneQuery.rewrite(reader);
    }

    /**
     * @param queries
     * @return
     * @see org.apache.lucene.search.Query#combine(org.apache.lucene.search.Query[])
     */
    public Query combine(Query[] queries)
    {
        return luceneQuery.combine(queries);
    }

    /**
     * @param terms
     * @see org.apache.lucene.search.Query#extractTerms(java.util.Set)
     */
    public void extractTerms(Set terms)
    {
        luceneQuery.extractTerms(terms);
    }

    /**
     * @param searcher
     * @return
     * @see org.apache.lucene.search.Query#getSimilarity(org.apache.lucene.search.Searcher)
     */
    public Similarity getSimilarity(Searcher searcher)
    {
        return luceneQuery.getSimilarity(searcher);
    }

    /**
     * @return
     * @see org.apache.lucene.search.Query#clone()
     */
    public Object clone()
    {
        return luceneQuery.clone();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((luceneQuery == null) ? 0 : luceneQuery.hashCode());
        result = prime * result + ((searchParameters == null) ? 0 : searchParameters.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContextAwareQuery other = (ContextAwareQuery) obj;
        if (luceneQuery == null)
        {
            if (other.luceneQuery != null)
                return false;
        }
        else if (!luceneQuery.equals(other.luceneQuery))
            return false;
        if (searchParameters == null)
        {
            if (other.searchParameters != null)
                return false;
        }
        else if (!searchParameters.equals(other.searchParameters))
            return false;
        return true;
    }

 

    
}
