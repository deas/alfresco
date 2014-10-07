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

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.solr.cache.CacheConstants;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
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
     * @param acceptDocs 
     */
    SolrOwnerSetScorer(Weight weight, DocSet in, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher)
    {
        super(weight, in, context, acceptDocs, searcher);
    }

    public static SolrOwnerSetScorer createOwnerSetScorer(Weight weight, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher, String authorities) throws IOException
    {
        
        DocSet authorityOwnedDocs = (DocSet) searcher.cacheLookup(CacheConstants.ALFRESCO_OWNERLOOKUP_CACHE, authorities);
        
        if(authorityOwnedDocs == null)
        {
            // Split the authorities. The first character in the authorities String
            // specifies the separator, e.g. ",jbloggs,abeecher"
            String[] auths = authorities.substring(1).split(authorities.substring(0, 1));

            BooleanQuery bQuery = new BooleanQuery();
            for(String current : auths)
            {
                if (AuthorityType.getAuthorityType(current) == AuthorityType.USER)
                {
                    bQuery.add(new TermQuery(new Term(QueryConstants.FIELD_OWNER, current)), Occur.SHOULD);
                }
            }

            authorityOwnedDocs = searcher.getDocSet(bQuery);
        
            searcher.cacheInsert(CacheConstants.ALFRESCO_OWNERLOOKUP_CACHE, authorities, authorityOwnedDocs);
        }
        
        // TODO: Cache the final set? e.g. searcher.cacheInsert(authorities, authorityOwnedDocs)
        return new SolrOwnerSetScorer(weight, authorityOwnedDocs, context, acceptDocs, searcher);
       
    }
}
