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

import org.alfresco.solr.ResizeableArrayList;
import org.alfresco.solr.cache.AclLookUp;
import org.alfresco.solr.cache.CacheConstants;
import org.alfresco.solr.cache.CacheEntry;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrDenySetScorer extends AbstractSolrCachingScorer
{

    SolrDenySetScorer(Weight weight, DocSet in, IndexReader solrIndexReader)
    {
        super(weight, in, solrIndexReader);
    }

    public static SolrDenySetScorer createDenySetScorer(Weight weight, SolrIndexSearcher searcher, String authorities, AtomicReader reader) throws IOException
    {
        // Get hold of solr top level searcher
        // Execute query with caching
        // translate reults to leaf docs
        // build ordered doc list

        String[] auths = authorities.substring(1).split(authorities.substring(0, 1));

        long[] aclIdByDocId = (long[]) searcher.cacheLookup(CacheConstants.ALFRESCO_CACHE, CacheConstants.KEY_ACL_ID_BY_DOC_ID);
        HashMap<AclLookUp, AclLookUp> lookups = (HashMap<AclLookUp, AclLookUp>) searcher
                .cacheLookup(CacheConstants.ALFRESCO_CACHE, CacheConstants.KEY_ACL_LOOKUP);
        ResizeableArrayList<CacheEntry> aclThenLeafOrderedEntries = (ResizeableArrayList<CacheEntry>) searcher.cacheLookup(CacheConstants.ALFRESCO_ARRAYLIST_CACHE,
                CacheConstants.KEY_DBID_LEAF_PATH_BY_ACL_ID_THEN_LEAF);
        BitDocSet publicDocSet = (BitDocSet) searcher.cacheLookup(CacheConstants.ALFRESCO_CACHE, CacheConstants.KEY_PUBLIC_DOC_SET);

        DocSet deniedDocSet = new BitDocSet(new FixedBitSet(searcher.maxDoc()));
        HashSet<Long> ignoredAlcs = null;
        for (String auth : auths)
        {
            if (auth.equals("GROUP_EVERYONE"))
            {
                HashSet<Long> aclIds =  (HashSet<Long>)searcher.cacheLookup(CacheConstants.ALFRESCO_DENY_TO_ACL_IDS_CACHE, auth);
                if(aclIds == null)
                {
                    aclIds = buildDenyAclIds(searcher, auth, aclIdByDocId);
                    searcher.cacheInsert(CacheConstants.ALFRESCO_DENY_TO_ACL_IDS_CACHE, auth, aclIds);
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
                deniedDocSet = deniedDocSet.union(publicDocSet);
            }
            else
            {
                HashSet<Long> aclIds =  (HashSet<Long>)searcher.cacheLookup(CacheConstants.ALFRESCO_DENY_TO_ACL_IDS_CACHE, auth);
                if(aclIds == null)
                {
                    aclIds = buildDenyAclIds(searcher, auth, aclIdByDocId);
                    searcher.cacheInsert(CacheConstants.ALFRESCO_DENY_TO_ACL_IDS_CACHE, auth, aclIds);
                }
                acls.addAll(aclIds);
               
            }
        }
        if(ignoredAlcs != null)
        {
            acls.removeAll(ignoredAlcs);
        }

        AclLookUp key = new AclLookUp(0);

        for (Long acl : acls)
        {
            key.setAclid(acl);
            AclLookUp value = lookups.get(key);
            if (value != null)
            {
                for (int i = value.getStart(); i < value.getEnd(); i++)
                {
                    deniedDocSet.add(aclThenLeafOrderedEntries.get(i).getLeaf());
                }
            }
        }

        return new SolrDenySetScorer(weight, deniedDocSet, reader);

    }

    public static HashSet<Long> buildDenyAclIds(SolrIndexSearcher searcher, String authority, long[] aclIdByDocId) throws IOException
    {
        HashSet<Long> aclsAsSet = new HashSet<Long>();

        AtomicReader reader = searcher.getAtomicReader();
        DocsEnum docsEnum = reader.termDocsEnum(new Term("DENIED", authority));
        if (docsEnum == null)
        {
            // field or term does not exist
            return aclsAsSet;
        }

        int currentDoc;
        while ((currentDoc = docsEnum.nextDoc()) != DocsEnum.NO_MORE_DOCS)
        {
            long acl = aclIdByDocId[currentDoc];
            aclsAsSet.add(acl);
        }
        return aclsAsSet;
    }

}
