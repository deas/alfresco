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
import org.alfresco.solr.ResizeableArrayList;
import org.alfresco.solr.AlfrescoSolrEventListener.AclLookUp;
import org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrCachingReaderScorer extends AbstractSolrCachingScorer
{
   
    SolrCachingReaderScorer(Similarity similarity, DocSet in, SolrIndexReader solrIndexReader)
    {
        super(similarity, in, solrIndexReader);
       
    }

    public static SolrCachingReaderScorer createReaderScorer(SolrIndexSearcher searcher, Similarity similarity, String authority, SolrIndexReader reader) throws IOException
    {
        // Get hold of solr top level searcher
        // Execute query with caching
        // translate reults to leaf docs
        // build ordered doc list

     
        
     
        long[] aclByDocId = (long[]) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ACL_ID_BY_DOC_ID);
        HashMap<AlfrescoSolrEventListener.AclLookUp, AlfrescoSolrEventListener.AclLookUp> lookups = (HashMap<AlfrescoSolrEventListener.AclLookUp, AlfrescoSolrEventListener.AclLookUp>) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ACL_LOOKUP);
        ResizeableArrayList<AlfrescoSolrEventListener.CacheEntry> aclThenLeafOrderedEntries = ( ResizeableArrayList<AlfrescoSolrEventListener.CacheEntry>) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_ARRAYLIST_CACHE, AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF);
      
        
        DocSet aclDocSet;
        if(authority.contains("|"))
        {
           BooleanQuery bQuery = new BooleanQuery();
           for(String current : authority.split("\\|"))
           {
               bQuery.add(new TermQuery(new Term("READER", current)), Occur.SHOULD);
           }
           aclDocSet = searcher.getDocSet(bQuery);
        }
        else
        {   
            aclDocSet = searcher.getDocSet(new TermQuery(new Term("READER", authority)));
        }
        
        BitDocSet readableDocSet = new BitDocSet(new OpenBitSet(searcher.getReader().maxDoc()));

        AlfrescoSolrEventListener.AclLookUp key = new AlfrescoSolrEventListener.AclLookUp(0);
        
        if(aclDocSet instanceof BitDocSet)
        {
            BitDocSet source = (BitDocSet)aclDocSet;
            OpenBitSet openBitSet = source.getBits();
            int current = -1;
            while((current = openBitSet.nextSetBit(current+1)) != -1)
            {
                long acl = aclByDocId[current];
                key.setAclid(acl);
                AlfrescoSolrEventListener.AclLookUp value = lookups.get(key);
                if(value != null)
                {
                    for(int i = value.getStart(); i < value.getEnd(); i++)
                    {
                        readableDocSet.add(aclThenLeafOrderedEntries.get(i).getLeaf());
                    }
                }
            }
        }
        else
        {

            for (DocIterator it = aclDocSet.iterator(); it.hasNext(); /* */)
            {
                int doc = it.nextDoc();
                long acl = aclByDocId[doc];
                key.setAclid(acl);
                AlfrescoSolrEventListener.AclLookUp value = lookups.get(key);
                if(value != null)
                {
                    for(int i = value.getStart(); i < value.getEnd(); i++)
                    {
                        readableDocSet.add(aclThenLeafOrderedEntries.get(i).getLeaf());
                    }
                }
            }
        }
        return new SolrCachingReaderScorer(similarity, readableDocSet, reader);
        
    }
}
