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

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;


/**
 * @author Andy
 *
 */
public abstract class AbstractSolrCachingScorer extends Scorer
{
    BitDocSet matches;

    int doc = -1;

    FixedBitSet bitSet;

    AtomicReaderContext context;
    
    AbstractSolrCachingScorer(Weight weight, DocSet in, AtomicReaderContext context, SolrIndexSearcher searcher)
    {
        super(weight);
        // TODO: 'in' is often too small for the logic in next() to work successfully (ArrayIndexOutOfBoundsException)
        if (false /*in instanceof BitDocSet*/)
        {
            matches = (BitDocSet) in;
        }
        else
        {
            this.matches = new BitDocSet(new FixedBitSet(searcher.maxDoc()));
            for (DocIterator it = in.iterator(); it.hasNext(); /* */)
            {
                matches.addUnique(it.nextDoc());
            }
        }
        bitSet = matches.getBits();
        this.context = context;
        doc = getBase() - 1;
    }

    
    private boolean next()
    {        
        // TODO: this is breaking because sometimes a BitDocSet is passed in to the constructor
        // that is smaller than searcher.maxDoc()
        doc = bitSet.nextSetBit(doc+1);
        return (doc != -1)  && (doc < (getBase()  + context.reader().maxDoc()));
    }
    
    private int getBase()
    {
        return context.docBase;
    }
    
    @Override
    public int nextDoc() throws IOException
    {
        if (!next())
        {
            return NO_MORE_DOCS;
        }
        return docID();
    }

    
    @Override
    public int docID()
    {
        // TODO: check this expression as for next()
        if (doc > -1)
        {
            return doc - getBase();
        }
        return doc;
    }

    @Override
    public float score() throws IOException
    {
        return 1.0f;
    }

    @Override
    public int advance(int target) throws IOException
    {
        while (next())
        {
            final int doc = docID();
            if (doc >= target)
            {
                return doc;
            }
        }
        return NO_MORE_DOCS;
    }

    // TODO: implement
    @Override
    public int freq() throws IOException
    {
        return 1;
    }

    // TODO: implement
    @Override
    public long cost()
    {
       return matches.size();
    }
}
