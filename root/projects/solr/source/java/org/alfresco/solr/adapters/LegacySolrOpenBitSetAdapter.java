package org.alfresco.solr.adapters;

import org.apache.lucene.util.OpenBitSet;

public class LegacySolrOpenBitSetAdapter extends OpenBitSet implements IOpenBitSet
{

    @Override
    public void or(IOpenBitSet duplicatedTxInIndex)
    {
        super.or((OpenBitSet) duplicatedTxInIndex);
    }

}
