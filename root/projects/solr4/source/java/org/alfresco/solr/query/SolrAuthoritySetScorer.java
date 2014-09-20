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
import java.util.HashSet;
import java.util.Properties;

import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.solr.cache.CacheConstants;
import org.alfresco.solr.data.GlobalReaders;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * Find the set of docs that the specified authority set can read.
 * <p>
 * Note that anyDenyDenies is not directly supported by this class, see {@link AbstractQParser}.
 * 
 * @see AbstractQParser
 * @author Matt Ward
 */
public class SolrAuthoritySetScorer extends AbstractSolrCachingScorer
{
    SolrAuthoritySetScorer(Weight weight, DocSet in, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher)
    {
        super(weight, in, context, acceptDocs, searcher);
    }
    
    public static SolrAuthoritySetScorer createAuthoritySetScorer(Weight weight, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher, String authorities) throws IOException
    {
        Properties p = searcher.getSchema().getResourceLoader().getCoreProperties();
        boolean doPermissionChecks = Boolean.parseBoolean(p.getProperty("alfresco.doPermissionChecks", "true"));
        
        Query key = new SolrAuthoritySetQuery(authorities);
        
        DocSet answer = (DocSet)searcher.cacheLookup(CacheConstants.ALFRESCO_AUTHORITY_CACHE, key);
        if(answer != null)
        {
            // Answer was in the cache, so return it.
            return new SolrAuthoritySetScorer(weight, answer, context, acceptDocs, searcher);
        }
        
        // Answer was not in cache, so build the results, cache and return.
        String[] auths = authorities.substring(1).split(authorities.substring(0, 1));
        
        boolean hasGlobalRead = false;
        
        final HashSet<String> globalReaders = GlobalReaders.getReaders();
        
        for(String auth : auths)
        {
            if(globalReaders.contains(auth))
            {
                hasGlobalRead = true;
                break;
            }
                    
        }
        
        if (hasGlobalRead || (doPermissionChecks == false))
        {
            // can read all
            DocSet allDocs = searcher.getDocSet(new MatchAllDocsQuery());
            return new SolrAuthoritySetScorer(weight, allDocs, context, acceptDocs, searcher);
        }

        // Docs for which the authorities have explicit read access.
        DocSet readableDocSet = searcher.getDocSet(new SolrReaderSetQuery(authorities));

        // Are all doc owners granted read permissions at a global level?
        if (globalReaders.contains(PermissionService.OWNER_AUTHORITY))
        {
            // Get the set of docs owned by the authorities (which they can therefore read).
            DocSet authorityOwnedDocs = searcher.getDocSet(new SolrOwnerSetQuery(authorities));
            // Final set of docs that the authorities can read.
            DocSet toCache = readableDocSet.union(authorityOwnedDocs);
            searcher.cacheInsert(CacheConstants.ALFRESCO_AUTHORITY_CACHE, key, toCache);
            return new SolrAuthoritySetScorer(weight, toCache, context, acceptDocs, searcher);
        }
        else
        {
            // for that docs I own that have owner Read rights
            DocSet ownerReadableDocSet = searcher.getDocSet(new SolrReaderSetQuery("|"+PermissionService.OWNER_AUTHORITY));
            DocSet authorityOwnedDocs = searcher.getDocSet(new SolrOwnerSetQuery(authorities));
           
            // Docs where the authority is an owner and where owners have read rights.
            DocSet docsAuthorityOwnsAndCanRead = ownerReadableDocSet.intersection(authorityOwnedDocs);
            // Final set of docs that the authorities can read.
            DocSet toCache = readableDocSet.union(docsAuthorityOwnsAndCanRead);
            searcher.cacheInsert(CacheConstants.ALFRESCO_AUTHORITY_CACHE, key, toCache);
            return new SolrAuthoritySetScorer(weight, toCache, context, acceptDocs, searcher);
        }
    }
}

