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
package org.alfresco.solr.query;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Find the set of documents owned by the specified set of authorities,
 * for those authorities that are users (e.g. we're not interested in groups etc.)
 * 
 * @author Matt Ward
 */
public class SolrOwnerSetScorer extends AbstractSolrCachingScorer
{
    /**
     * Package private constructor.
     */
    SolrOwnerSetScorer(Weight weight, DocSet in, AtomicReaderContext context, SolrIndexSearcher searcher)
    {
        super(weight, in, context, searcher);
    }

    public static SolrOwnerSetScorer createOwnerSetScorer(Weight weight, AtomicReaderContext context, SolrIndexSearcher searcher, String authorities) throws IOException
    {
        // The set of docs owned by all of the authorities
        BitDocSet authorityOwnedDocs = new BitDocSet(new FixedBitSet(searcher.maxDoc()));

        // Split the authorities. The first character in the authorities String
        // specifies the separator, e.g. ",jbloggs,abeecher"
        String[] auths = authorities.substring(1).split(authorities.substring(0, 1));
        
        for (String current : auths)
        {
            DocSet currentAuthDocs = searcher.getDocSet(new SolrOwnerQuery(current));
            // Add to the doc set owned by the set of authorities.
            authorityOwnedDocs.union(currentAuthDocs);
        }

        // TODO: Cache the final set? e.g. searcher.cacheInsert(authorities, authorityOwnedDocs)
        return new SolrOwnerSetScorer(weight, authorityOwnedDocs, context, searcher);
    }
}
