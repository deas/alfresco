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
package org.alfresco.repo.search.impl.lucene.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.alfresco.repo.search.impl.lucene.query.LeafScorer.Counter;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Weight;

public class PathScorer extends Scorer
{
    Scorer scorer;
  
    PathScorer(Similarity similarity, Scorer scorer)
    {
        super(similarity);
        this.scorer = scorer;
    }
  

    public static PathScorer createPathScorer(Similarity similarity, PathQuery pathQuery, IndexReader reader, Weight weight, DictionaryService dictionarySertvice, boolean repeat) throws IOException
    {
        Scorer selfScorer = null;
        HashMap<String, Counter> selfIds = null;
        boolean followParentInLevel0 = true;
        
        StructuredFieldPosition last = null;
        if(pathQuery.getQNameStructuredFieldPositions().size() > 0)
        {
           last = pathQuery.getQNameStructuredFieldPositions().get(pathQuery.getQNameStructuredFieldPositions().size() - 1);
        }
        if ((last != null) && last.linkSelf())
        {
            PathQuery selfQuery = new PathQuery(dictionarySertvice);
            selfQuery.setQuery(pathQuery.getPathStructuredFieldPositions(), pathQuery.getQNameStructuredFieldPositions());
            selfQuery.removeDescendantAndSelf();
            if (!selfQuery.isEmpty())
            {
               selfIds = new HashMap<String, Counter>();
               selfScorer = PathScorer.createPathScorer(similarity, selfQuery, reader, weight, dictionarySertvice, repeat);
               selfIds.clear();
               while (selfScorer.next())
               {
                   int doc = selfScorer.doc();
                   Document document = reader.document(doc);
                   Field id = document.getField("ID");
                   Counter counter = selfIds.get(id.stringValue());
                   if (counter == null)
                   {
                       counter = new Counter();
                       selfIds.put(id.stringValue(), counter);
                   }
                   counter.count++;
               }
            }
        }
        
        
        if ((pathQuery.getPathStructuredFieldPositions().size() + pathQuery.getQNameStructuredFieldPositions().size()) == 0) 
        {
                ArrayList<StructuredFieldPosition> answer = new ArrayList<StructuredFieldPosition>(2);
                answer.add(new SelfAxisStructuredFieldPosition());
                answer.add(new SelfAxisStructuredFieldPosition());
                
                pathQuery.appendQuery(answer);
        }

        
        for (StructuredFieldPosition sfp : pathQuery.getPathStructuredFieldPositions())
        {
            if (sfp.getTermText() != null)
            {
                TermPositions p = reader.termPositions(new Term(pathQuery.getPathField(), sfp.getTermText()));
                if (p == null)
                    return null;
                CachingTermPositions ctp = new CachingTermPositions(p);
                sfp.setCachingTermPositions(ctp);
            }
        }

        for (StructuredFieldPosition sfp : pathQuery.getQNameStructuredFieldPositions())
        {
            if (sfp.getTermText() != null)
            {
                TermPositions p = reader.termPositions(new Term(pathQuery.getQnameField(), sfp.getTermText()));
                if (p == null)
                    return null;
                CachingTermPositions ctp = new CachingTermPositions(p);
                sfp.setCachingTermPositions(ctp);
            }
        }

        TermPositions rootContainerPositions = null;
        if (pathQuery.getPathRootTerm() != null)
        {
            rootContainerPositions = reader.termPositions(pathQuery.getPathRootTerm());
        }
        
        TermPositions rootLeafPositions = null;
        if (pathQuery.getQNameRootTerm() != null)
        {
            rootLeafPositions = reader.termPositions(pathQuery.getQNameRootTerm());
        }

        ContainerScorer cs = null;

        TermPositions level0 = null;

        // StructuredFieldPosition[] test =
        // (StructuredFieldPosition[])structuredFieldPositions.toArray(new
        // StructuredFieldPosition[]{});
        if (pathQuery.getPathStructuredFieldPositions().size() > 0)
        {
            TermPositions containerPositions = reader.termPositions(new Term("ISCONTAINER", "T"));
            cs = new ContainerScorer(weight, rootContainerPositions, (StructuredFieldPosition[]) pathQuery.getPathStructuredFieldPositions().toArray(new StructuredFieldPosition[] {}),
                    containerPositions, similarity, reader.norms(pathQuery.getPathField()));
        }
        else
        {
            level0 = reader.termPositions(new Term("ISROOT", "T"));
        }
        
        if((cs == null) && 
                (pathQuery.getQNameStructuredFieldPositions().get(pathQuery.getQNameStructuredFieldPositions().size()-1)).linkSelf())
        {
            followParentInLevel0 = false;
        }
        

        LeafScorer ls = new LeafScorer(weight, rootLeafPositions, level0, cs, (StructuredFieldPosition[]) pathQuery.getQNameStructuredFieldPositions().toArray(new StructuredFieldPosition[] {}), followParentInLevel0,
                selfIds, reader, similarity, reader.norms(pathQuery.getQnameField()), dictionarySertvice, repeat);

        return new PathScorer(similarity, ls);
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
