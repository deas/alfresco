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
import java.util.HashSet;

import org.alfresco.solr.AlfrescoSolrEventListener;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrReaderSetScorer extends AbstractSolrCachingScorer
{

    SolrReaderSetScorer(Similarity similarity, DocSet in, SolrIndexReader solrIndexReader)
    {
        super(similarity, in, solrIndexReader);

    }

    public static SolrReaderSetScorer createReaderSetScorer(SolrIndexSearcher searcher, Similarity similarity, String authorities, SolrIndexReader reader) throws IOException
    {
        // Get hold of solr top level searcher
        // Execute query with caching
        // translate reults to leaf docs
        // build ordered doc list

        String[] auths = authorities.substring(1).split(authorities.substring(0, 1));

        HashMap<String, HashSet<Long>> readerToAclIds = (HashMap<String, HashSet<Long>>) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_READER_TO_ACL_IDS_LOOKUP);
        long[] aclByDocId = (long[]) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ACL_ID_BY_DOC_ID);
        HashMap<AlfrescoSolrEventListener.AclLookUp, AlfrescoSolrEventListener.AclLookUp> lookups = (HashMap<AlfrescoSolrEventListener.AclLookUp, AlfrescoSolrEventListener.AclLookUp>) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ACL_LOOKUP);
        AlfrescoSolrEventListener.CacheEntry[] aclThenLeafOrderedEntries = ( AlfrescoSolrEventListener.CacheEntry[]) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF);
        BitDocSet publicDocSet = (BitDocSet) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_PUBLIC_DOC_SET);

        //        DocSet aclDocSet;
        //        if(auths.length > 1)
        //        {
        //           BooleanQuery bQuery = new BooleanQuery();
        //           for(String current : auths)
        //           {
        //               bQuery.add(new TermQuery(new Term("READER", current)), Occur.SHOULD);
        //           }
        //           aclDocSet = searcher.getDocSet(bQuery);
        //        }
        //        else
        //        {   
        //            aclDocSet = searcher.getDocSet(new TermQuery(new Term("READER", auths[0])));
        //        }

        DocSet readableDocSet = new BitDocSet(new OpenBitSet(searcher.getReader().maxDoc()));
        
        HashSet<Long> acls = new HashSet<Long>();
        for(String auth : auths)
        {
            if(auth.equals("GROUP_EVERYONE"))
            {
                readableDocSet = readableDocSet.union(publicDocSet);
            }
            else
            {
                HashSet<Long> aclIds = readerToAclIds.get(auth);
                if(aclIds != null)
                {
                    acls.addAll(aclIds);
                }
            }
        }

        AlfrescoSolrEventListener.AclLookUp key = new AlfrescoSolrEventListener.AclLookUp(0);

        for(Long acl : acls)
        {
            key.setAclid(acl);
            AlfrescoSolrEventListener.AclLookUp value = lookups.get(key);
            if(value != null)
            {
                for(int i = value.getStart(); i < value.getEnd(); i++)
                {
                    readableDocSet.add(aclThenLeafOrderedEntries[i].getLeaf());
                }
            }
        }



        //        if(aclDocSet instanceof BitDocSet)
        //        {
        //            BitDocSet source = (BitDocSet)aclDocSet;
        //            OpenBitSet openBitSet = source.getBits();
        //            int current = -1;
        //            while((current = openBitSet.nextSetBit(current+1)) != -1)
        //            {
        //                long acl = aclByDocId[current];
        //                key.setAclid(acl);
        //                AlfrescoSolrEventListener.AclLookUp value = lookups.get(key);
        //                if(value != null)
        //                {
        //                    for(int i = value.getStart(); i < value.getEnd(); i++)
        //                    {
        //                        readableDocSet.add(aclThenLeafOrderedEntries[i].getLeaf());
        //                    }
        //                }
        //            }
        //        }
        //        else
        //        {
        //
        //            for (DocIterator it = aclDocSet.iterator(); it.hasNext(); /* */)
        //            {
        //                int doc = it.nextDoc();
        //                long acl = aclByDocId[doc];
        //                key.setAclid(acl);
        //                AlfrescoSolrEventListener.AclLookUp value = lookups.get(key);
        //                if(value != null)
        //                {
        //                    for(int i = value.getStart(); i < value.getEnd(); i++)
        //                    {
        //                        readableDocSet.add(aclThenLeafOrderedEntries[i].getLeaf());
        //                    }
        //                }
        //            }
        //        }
        return new SolrReaderSetScorer(similarity, readableDocSet, reader);

    }
}
