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
import java.util.ArrayList;

import org.alfresco.repo.search.impl.lucene.query.CachingTermPositions;
import org.alfresco.repo.search.impl.lucene.query.SelfAxisStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.StructuredFieldPosition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;
import org.apache.solr.search.SolrIndexReader;

public class SolrPathScorer extends Scorer
{
    Scorer scorer;
  
    SolrPathScorer(Similarity similarity, Scorer scorer)
    {
        super(similarity);
        this.scorer = scorer;
    }
  

    public static SolrPathScorer createPathScorer(Similarity similarity, SolrPathQuery solrPathQuery, SolrIndexReader reader, Weight weight, DictionaryService dictionarySertvice, boolean repeat) throws IOException
    {
        
        StructuredFieldPosition last = null;
        if(solrPathQuery.getPathStructuredFieldPositions().size() > 0)
        {
           last = solrPathQuery.getPathStructuredFieldPositions().get(solrPathQuery.getPathStructuredFieldPositions().size() - 1);
        }
   
        
        if (solrPathQuery.getPathStructuredFieldPositions().size() == 0) 
        {
                ArrayList<StructuredFieldPosition> answer = new ArrayList<StructuredFieldPosition>(2);
                answer.add(new SelfAxisStructuredFieldPosition());
                answer.add(new SelfAxisStructuredFieldPosition());
                
                solrPathQuery.appendQuery(answer);
        }

        
        for (StructuredFieldPosition sfp : solrPathQuery.getPathStructuredFieldPositions())
        {
            if (sfp.getTermText() != null)
            {
                TermPositions p = reader.termPositions(new Term(solrPathQuery.getPathField(), sfp.getTermText()));
                if (p == null)
                    return null;
                CachingTermPositions ctp = new CachingTermPositions(p);
                sfp.setCachingTermPositions(ctp);
            }
        }

        SolrContainerScorer cs = null;

        TermPositions rootContainerPositions = null;
        if (solrPathQuery.getPathRootTerm() != null)
        {
            rootContainerPositions = reader.termPositions(solrPathQuery.getPathRootTerm());
        }
       
        if (solrPathQuery.getPathStructuredFieldPositions().size() > 0)
        {
            cs = new SolrContainerScorer(weight, rootContainerPositions, (StructuredFieldPosition[]) solrPathQuery.getPathStructuredFieldPositions().toArray(new StructuredFieldPosition[] {}),
                    similarity, reader.norms(solrPathQuery.getPathField()));
        }
       
       
        return new SolrPathScorer(similarity, cs);
    }

    @Override
    public boolean next() throws IOException
    {
        return scorer.next();
    }

    @Override
    public int doc()
    {
        return scorer.doc();
    }

    @Override
    public float score() throws IOException
    {
        return scorer.score();
    }

    @Override
    public boolean skipTo(int position) throws IOException
    {
        return scorer.skipTo(position);
    }

    @Override
    public Explanation explain(int position) throws IOException
    {
        return scorer.explain(position);
    }

}
