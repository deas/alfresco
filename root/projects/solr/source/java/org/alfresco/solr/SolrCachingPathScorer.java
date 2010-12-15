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
package org.alfresco.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.alfresco.solr.AlfrescoSolrEventListener.CacheEntry;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;

public class SolrCachingPathScorer extends Scorer
{
    List<ScoreDoc> matches;
    
    int position = -1;
  
    SolrCachingPathScorer(Similarity similarity,  List<ScoreDoc> matches)
    {
        super(similarity);
        this.matches = matches;
    }
  

    public static SolrCachingPathScorer createPathScorer( SolrIndexSearcher searcher, Similarity similarity, SolrPathQuery solrPathQuery, SolrIndexReader reader) throws IOException
    {
        // Get hold of solr top level searcher
        // Execute query with caching
        // translate reults to leaf docs 
        // build ordered doc list
        
     
        //PathCollector pathCollector = new PathCollector();
        //searcher.search(solrPathQuery, pathCollector);
        
        DocSet docSet = searcher.getDocSet(solrPathQuery);
        
        CacheEntry[] indexedByDocId = (CacheEntry[])searcher.cacheLookup(AlfrescoSolrEventListener.ALFRESCO_CACHE, AlfrescoSolrEventListener.KEY_INDEXED_BY_DOC_ID);
        
        //List<ScoreDoc> auxDocs = pathCollector.getDocs();
        ArrayList<ScoreDoc> translated = new ArrayList<ScoreDoc>(docSet.size());
        
        for(DocIterator it = docSet.iterator(); it.hasNext(); /* */)
        {
            CacheEntry entry = indexedByDocId[it.nextDoc()];
            translated.add(new ScoreDoc(entry.getLeaf(), it.score()));
        }
        
        Collections.sort(translated, new ScoreDocComparator() );
        
        return new SolrCachingPathScorer(similarity, translated);
    }

    @Override
    public boolean next() throws IOException
    {
        position++;
        return position < matches.size();
    }

    @Override
    public int doc()
    {
        return matches.get(position).doc;
    }

    @Override
    public float score() throws IOException
    {
        return 1.0f;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean skipTo(int target) throws IOException
    {
        while(next())
        {
            if(doc() >= target)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Explanation explain(int position) throws IOException
    {
       throw new UnsupportedOperationException();
    }

    static class PathCollector extends Collector
    {
        private List<ScoreDoc> docs = new ArrayList<ScoreDoc>();
 
        private Scorer scorer;
        
        private int docBase;
        
        /* (non-Javadoc)
         * @see org.apache.lucene.search.Collector#acceptsDocsOutOfOrder()
         */
        @Override
        public boolean acceptsDocsOutOfOrder()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.apache.lucene.search.Collector#collect(int)
         */
        @Override
        public void collect(int doc) throws IOException
        {
            docs.add(new ScoreDoc(doc+docBase, scorer.score()));
            
        }

        /* (non-Javadoc)
         * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.IndexReader, int)
         */
        @Override
        public void setNextReader(IndexReader reader, int docBase) throws IOException
        {
            this.docBase = docBase;
        }

        /* (non-Javadoc)
         * @see org.apache.lucene.search.Collector#setScorer(org.apache.lucene.search.Scorer)
         */
        @Override
        public void setScorer(Scorer scorer) throws IOException
        {
            this.scorer = scorer;
        }
        
        
        public List<ScoreDoc> getDocs()
        {
            return docs;
        }
    }
    
    public static class ScoreDocComparator implements Comparator<ScoreDoc>
    {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(ScoreDoc o1, ScoreDoc o2)
        {
            return o1.doc - o2.doc;
        }
        
    }
}
