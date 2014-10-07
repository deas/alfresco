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

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.solr.cache.CacheConstants;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrDenySetScorer2 extends AbstractSolrCachingScorer
{

    SolrDenySetScorer2(Weight weight, DocSet in, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher)
    {
        super(weight, in, context, acceptDocs, searcher);
    }

    public static SolrDenySetScorer2 createDenySetScorer(Weight weight, AtomicReaderContext context, Bits acceptDocs, SolrIndexSearcher searcher, String authorities, AtomicReader reader) throws IOException
    {
        DocSet deniedDocSet = (DocSet) searcher.cacheLookup(CacheConstants.ALFRESCO_DENIED_CACHE, authorities);

        if (deniedDocSet == null)
        {

            String[] auths = authorities.substring(1).split(authorities.substring(0, 1));

            deniedDocSet = new BitDocSet(new FixedBitSet(searcher.maxDoc()));

            BooleanQuery bQuery = new BooleanQuery();
            for(String current : auths)
            {
                bQuery.add(new TermQuery(new Term(QueryConstants.FIELD_DENIED, current)), Occur.SHOULD);
            }

            DocSet aclDocs = searcher.getDocSet(bQuery);
            
            HashSet<Long> aclsFound = new HashSet<Long>(aclDocs.size());
            NumericDocValues aclDocValues = searcher.getAtomicReader().getNumericDocValues(QueryConstants.FIELD_ACLID);
            
            BooleanQuery aQuery = new BooleanQuery();
            for (DocIterator it = aclDocs.iterator(); it.hasNext(); /**/)
            {
                int docID = it.nextDoc();
                // Obtain the ACL ID for this ACL doc.
                long aclID = aclDocValues.get(docID);
                aclsFound.add(Long.valueOf(aclID));
            }
         
            for(int i = 0; i < searcher.maxDoc(); i ++)
            {
                long aclID = aclDocValues.get(i);
                Long key = Long.valueOf(aclID);
                if(aclsFound.contains(key))
                {
                    deniedDocSet.add(i);
                }
            }
            
            // Exclude the ACL docs from the results, we only want real docs that match.
            // Probably not very efficient, what we really want is remove(docID)
            deniedDocSet = deniedDocSet.andNot(aclDocs);
            searcher.cacheInsert(CacheConstants.ALFRESCO_DENIED_CACHE, authorities, deniedDocSet);
        }
        
        // TODO: cache the full set? e.g. searcher.cacheInsert(CacheConstants.ALFRESCO_READERSET_CACHE, authorities, readableDocSet)
        // plus check of course, for presence in cache at start of method.
        return new SolrDenySetScorer2(weight, deniedDocSet, context, acceptDocs, searcher);
        
        
        
    }

}
