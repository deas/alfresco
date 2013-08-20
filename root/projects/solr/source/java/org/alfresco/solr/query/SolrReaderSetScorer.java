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
import org.alfresco.solr.ResizeableArrayList;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
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

        long[] aclIdByDocId = (long[]) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ACL_ID_BY_DOC_ID);
        HashMap<AlfrescoSolrEventListener.AclLookUp, AlfrescoSolrEventListener.AclLookUp> lookups = (HashMap<AlfrescoSolrEventListener.AclLookUp, AlfrescoSolrEventListener.AclLookUp>) searcher
                .cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ACL_LOOKUP);
        ResizeableArrayList<AlfrescoSolrEventListener.CacheEntry> aclThenLeafOrderedEntries = (ResizeableArrayList<AlfrescoSolrEventListener.CacheEntry>) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_ARRAYLIST_CACHE,
                AlfrescoSolrEventListener.KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF);
        BitDocSet publicDocSet = (BitDocSet) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_PUBLIC_DOC_SET);

        DocSet readableDocSet = new BitDocSet(new OpenBitSet(searcher.getReader().maxDoc()));
        HashSet<Long> ignoredAlcs = null;
        for (String auth : auths)
        {
            if (auth.equals("GROUP_EVERYONE"))
            {
                HashSet<Long> aclIds =  (HashSet<Long>)searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_READER_TO_ACL_IDS_CACHE, auth);
                if(aclIds == null)
                {
                    aclIds = buildReaderAclIds(searcher, auth, aclIdByDocId);
                    searcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_READER_TO_ACL_IDS_CACHE, auth, aclIds);
                }
                ignoredAlcs = aclIds;
                break;
            }
        }

        HashSet<Long> acls = new HashSet<Long>();
        for (String auth : auths)
        {
            if (auth.equals("GROUP_EVERYONE"))
            {
                readableDocSet = readableDocSet.union(publicDocSet);
            }
            else
            {
                HashSet<Long> aclIds =  (HashSet<Long>)searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_READER_TO_ACL_IDS_CACHE, auth);
                if(aclIds == null)
                {
                    aclIds = buildReaderAclIds(searcher, auth, aclIdByDocId);
                    searcher.cacheInsert(AlfrescoSolrEventListener.ALFRESCO_READER_TO_ACL_IDS_CACHE, auth, aclIds);
                }
                acls.addAll(aclIds);
               
            }
        }
        if(ignoredAlcs != null)
        {
            acls.removeAll(ignoredAlcs);
        }

        AlfrescoSolrEventListener.AclLookUp key = new AlfrescoSolrEventListener.AclLookUp(0);

        for (Long acl : acls)
        {
            key.setAclid(acl);
            AlfrescoSolrEventListener.AclLookUp value = lookups.get(key);
            if (value != null)
            {
                for (int i = value.getStart(); i < value.getEnd(); i++)
                {
                    readableDocSet.add(aclThenLeafOrderedEntries.get(i).getLeaf());
                }
            }
        }

        return new SolrReaderSetScorer(similarity, readableDocSet, reader);

    }

    public static HashSet<Long> buildReaderAclIds(SolrIndexSearcher searcher, String authority, long[] aclIdByDocId) throws IOException
    {
        HashSet<Long> aclsAsSet = new HashSet<Long>();

        IndexReader reader = searcher.getReader();
        TermEnum termEnum = reader.terms(new Term("READER", authority));
        try
        {
            Term term = termEnum.term();
            if (term == null)
            {
                return aclsAsSet;
            }
            if (term.field().equals("READER") && term.text().equals(authority))
            {
                TermDocs termDocs = reader.termDocs(term);
                try
                {
                    while (termDocs.next())
                    {
                        int currentDoc = termDocs.doc();
                        long acl = aclIdByDocId[currentDoc];
                        aclsAsSet.add(acl);
                    }
                }
                finally
                {

                    termDocs.close();
                }
                return aclsAsSet;
            }
            else
            {
                return aclsAsSet;
            }
        }
        finally
        {
            termEnum.close();
        }

    }

}
