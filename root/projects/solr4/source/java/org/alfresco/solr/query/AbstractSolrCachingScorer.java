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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.FixedBitSet;
import org.apache.solr.search.BitDocSet;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;


/**
 * @author Andy
 *
 */
public abstract class AbstractSolrCachingScorer extends Scorer
{
    BitDocSet matches;

    int doc = -1;

    FixedBitSet bitSet;

    IndexReader solrIndexReader;

    AbstractSolrCachingScorer(Weight weight, DocSet in, IndexReader solrIndexReader)
    {
        super(weight);
        if (in instanceof BitDocSet)
        {
            matches = (BitDocSet) in;
        }
        else
        {
            this.matches = new BitDocSet(new FixedBitSet(solrIndexReader.maxDoc()));
            for (DocIterator it = in.iterator(); it.hasNext(); /* */)
            {
                matches.addUnique(it.nextDoc());
            }
        }
        bitSet = matches.getBits();
        this.solrIndexReader = solrIndexReader;
        // TODO: original code uses -1 as initial value for doc and checks for this value
        // in next(), but unless getBase() returns 0, then this value will not be -1.
        // TODO: check the logic in next() - perhaps should be return (doc != initialDocValue) or similar?
        doc = getBase() - 1;
    }

    
    private boolean next()
    {        
        doc = bitSet.nextSetBit(doc+1);
        return (doc != -1)  && (doc < (getBase()  + solrIndexReader.maxDoc()));
    }
    
    /**
     * Not yet implemented. Previously this method was the inlined code:
     * <pre>solrIndexReader.getBase()</pre>
     * TODO: determine the equivalent for Solr 4, then inline this method once again.
     * 
     * From the original javadoc: "Returns the docid offset within the parent reader"
     */
    private int getBase()
    {
        return 0;
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
        throw new UnsupportedOperationException();
    }

    // TODO: implement
    @Override
    public long cost()
    {
        throw new UnsupportedOperationException();
    }
}
