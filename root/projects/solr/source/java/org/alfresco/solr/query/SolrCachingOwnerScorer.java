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
package org.alfresco.solr.query;

import java.io.IOException;
import java.util.HashMap;

import org.alfresco.solr.AlfrescoSolrEventListener;
import org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry;
import org.alfresco.solr.AlfrescoSolrEventListener.OwnerLookUp;
import org.apache.lucene.search.Similarity;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrCachingOwnerScorer extends AbstractSolrCachingScorer
{
  

    /**
     * @param similarity
     * @param in
     * @param solrIndexReader
     */
    SolrCachingOwnerScorer(Similarity similarity, DocSet in, SolrIndexReader solrIndexReader)
    {
        super(similarity, in, solrIndexReader);
        // TODO Auto-generated constructor stub
    }

    public static SolrCachingOwnerScorer createOwnerScorer(SolrIndexSearcher searcher, Similarity similarity, String authority, SolrIndexReader reader) throws IOException
    {
        // Get hold of solr top level searcher
        // Execute query with caching
        // translate reults to leaf docs
        // build ordered doc list

        // CacheEntry[] indexedByDocId = (CacheEntry[]) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE,
        // AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_DOC_ID);
        //
        //       
        // DocSet authorityOwnedAuxDocs = searcher.getDocSet(new TermQuery(new Term("OWNER", authority)));
        // BitDocSet authorityOwnedDocs = new BitDocSet();
        //
        // if(authorityOwnedAuxDocs instanceof BitDocSet)
        // {
        // BitDocSet source = (BitDocSet)authorityOwnedAuxDocs;
        // OpenBitSet openBitSet = source.getBits();
        // int current = -1;
        // while((current = openBitSet.nextSetBit(current+1)) != -1)
        // {
        // CacheEntry entry = indexedByDocId[current];
        // authorityOwnedDocs.addUnique(entry.getLeaf());
        // }
        // }
        // else
        // {
        // for (DocIterator it = authorityOwnedAuxDocs.iterator(); it.hasNext(); /* */)
        // {
        // CacheEntry entry = indexedByDocId[it.next()];
        // authorityOwnedDocs.addUnique(entry.getLeaf());
        // }
        // }

        BitDocSet authorityOwnedDocs = new BitDocSet();

        HashMap<String, OwnerLookUp> ownerLookUp = (HashMap<String, OwnerLookUp>) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE,
                AlfrescoSolrEventListener.KEY_OWNER_LOOKUP);
        OwnerLookUp lookUp = ownerLookUp.get(authority);
        if (lookUp != null)
        {
            CacheEntry[] indexedOderedByOwnerIdThenDoc = (CacheEntry[]) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE,
                    AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_OWNER_ID_THEN_LEAF);
            for (int i = lookUp.getStart(); i < lookUp.getEnd(); i++)
            {
                authorityOwnedDocs.addUnique(indexedOderedByOwnerIdThenDoc[i].getLeaf());
            }
        }

        return new SolrCachingOwnerScorer(similarity, authorityOwnedDocs, reader);

    }

}
