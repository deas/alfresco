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
import java.util.HashSet;

import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.solr.AlfrescoSolrEventListener;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrCachingAuthorityScorer extends AbstractSolrCachingScorer
{
    

    SolrCachingAuthorityScorer(Similarity similarity, DocSet in, SolrIndexReader solrIndexReader)
    {
        super(similarity, in, solrIndexReader);
     
    }

    public static SolrCachingAuthorityScorer createAuthorityScorer(SolrIndexSearcher searcher, Similarity similarity, String authority, SolrIndexReader reader) throws IOException
    {
        // Get hold of solr top level searcher
        // Execute query with caching
        // translate reults to leaf docs
        // build ordered doc list

        HashSet<String> globalReaders = (HashSet<String>) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_GLOBAL_READERS);

        if (globalReaders.contains(authority))
        {
            // can read all
            OpenBitSet allLeafDocs = (OpenBitSet) searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_ALL_LEAF_DOCS);
            return new SolrCachingAuthorityScorer(similarity, new BitDocSet(allLeafDocs), reader);
        }

        DocSet readableDocSet = searcher.getDocSet(new SolrCachingReaderQuery(authority));

        if (globalReaders.contains(PermissionService.OWNER_AUTHORITY))
        {
            DocSet authorityOwnedDocs = searcher.getDocSet(new SolrCachingOwnerQuery(authority));
            return new SolrCachingAuthorityScorer(similarity, readableDocSet.union(authorityOwnedDocs), reader);
        }
        else
        {
            // for that docs I own that have owner Read rights
            DocSet ownerReadableDocSet = searcher.getDocSet(new SolrCachingReaderQuery(PermissionService.OWNER_AUTHORITY));
            DocSet authorityOwnedDocs = searcher.getDocSet(new SolrCachingOwnerQuery(authority));
           
            DocSet docsAuthorityOwnsAndCanRead = ownerReadableDocSet.intersection(authorityOwnedDocs);
            
            return new SolrCachingAuthorityScorer(similarity, readableDocSet.union(docsAuthorityOwnsAndCanRead), reader);
        }
    }
}
