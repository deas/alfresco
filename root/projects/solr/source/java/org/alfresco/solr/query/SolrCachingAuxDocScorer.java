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
import java.util.HashSet;

import org.alfresco.solr.AlfrescoSolrEventListener;
import org.alfresco.solr.AlfrescoSolrEventListener.AclLookUp;
import org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrCachingAuxDocScorer extends AbstractSolrCachingScorer
{
   
    SolrCachingAuxDocScorer(Similarity similarity, DocSet in, SolrIndexReader solrIndexReader)
    {
        super(similarity, in, solrIndexReader);
       
    }

    public static SolrCachingAuxDocScorer createAuxDocScorer(SolrIndexSearcher searcher, Similarity similarity, Query query, SolrIndexReader reader) throws IOException
    {
        // Get hold of solr top level searcher
        // Execute query with caching
        // translate reults to leaf docs
        // build ordered doc list

        DocSet auxDocSet = searcher.getDocSet(query);

        CacheEntry[] indexedByDocId = (CacheEntry[]) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_DOC_ID);

        // List<ScoreDoc> auxDocs = pathCollector.getDocs();
        OpenBitSet translated = new OpenBitSet();

        if (auxDocSet instanceof BitDocSet)
        {
            BitDocSet source = (BitDocSet) auxDocSet;
            OpenBitSet openBitSet = source.getBits();
            int current = -1;
            while ((current = openBitSet.nextSetBit(current + 1)) != -1)
            {
                CacheEntry entry = indexedByDocId[current];
                translated.set(entry.getLeaf());
            }
        }
        else
        {
            for (DocIterator it = auxDocSet.iterator(); it.hasNext(); /* */)
            {
                CacheEntry entry = indexedByDocId[it.nextDoc()];
                translated.set(entry.getLeaf());
            }
        }

        return new SolrCachingAuxDocScorer(similarity, new BitDocSet(translated), reader);
        
    }
}
