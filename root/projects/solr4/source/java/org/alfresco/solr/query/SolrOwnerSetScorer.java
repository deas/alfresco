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
import java.util.HashMap;

import org.alfresco.solr.ResizeableArrayList;
import org.alfresco.solr.cache.CacheConstants;
import org.alfresco.solr.cache.CacheEntry;
import org.alfresco.solr.cache.OwnerLookUp;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrOwnerSetScorer extends AbstractSolrCachingScorer
{

    /**
     * @param similarity
     * @param in
     * @param solrIndexReader
     */
    SolrOwnerSetScorer(Similarity similarity, DocSet in, AtomicReader reader)
    {
        super(null/*TODO*/, in, reader);
        // TODO Auto-generated constructor stub
    }

    public static SolrOwnerSetScorer createOwnerSetScorer(AtomicReaderContext context, SolrIndexSearcher searcher, String authorities) throws IOException
    {
        // Get hold of solr top level searcher
        // Execute query with caching
        // translate reults to leaf docs
        // build ordered doc list

        BitDocSet authorityOwnedDocs = new BitDocSet(new FixedBitSet(searcher.maxDoc()));

        HashMap<String, OwnerLookUp> ownerLookUp = (HashMap<String, OwnerLookUp>) searcher.cacheLookup(CacheConstants.ALFRESCO_CACHE,
                    CacheConstants.KEY_OWNER_LOOKUP);

        String[] auths = authorities.substring(1).split(authorities.substring(0, 1));
        
        for (String current : auths)
        {
            OwnerLookUp lookUp = ownerLookUp.get(current);
            if (lookUp != null)
            {
                ResizeableArrayList<CacheEntry> indexedOderedByOwnerIdThenDoc = (ResizeableArrayList<CacheEntry>) searcher.cacheLookup(CacheConstants.ALFRESCO_ARRAYLIST_CACHE,
                            CacheConstants.KEY_DBID_LEAF_PATH_BY_OWNER_ID_THEN_LEAF);
                for (int i = lookUp.getStart(); i < lookUp.getEnd(); i++)
                {
                    authorityOwnedDocs.addUnique(indexedOderedByOwnerIdThenDoc.get(i).getLeaf());
                }
            }
        }

        return new SolrOwnerSetScorer(searcher.getSimilarity(), authorityOwnedDocs, context.reader());

    }

}
