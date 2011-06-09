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
package org.alfresco.solr.tracker;

import org.apache.lucene.util.OpenBitSet;

public class IndexHealthReport
{
    long dbTransactionCount;

    OpenBitSet missingFromIndex = new OpenBitSet();

    OpenBitSet duplicatedInIndex = new OpenBitSet();

    OpenBitSet inIndexButNotInDb = new OpenBitSet();

    OpenBitSet duplicatedLeafInIndex = new OpenBitSet();

    long transactionDocsInIndex;

    long leafDocCountInIndex;

    long lastIndexedCommitTime;

    long lastIndexedIdBeforeHoles;

    /**
     * @return the transactionDocsInIndex
     */
    public long getTransactionDocsInIndex()
    {
        return transactionDocsInIndex;
    }

    /**
     * @param leafCount
     */
    public void setLeafDocCountInIndex(long leafDocCountInIndex)
    {
        this.leafDocCountInIndex = leafDocCountInIndex;
    }

    /**
     * @return the leafDocCountInIndex
     */
    public long getLeafDocCountInIndex()
    {
        return leafDocCountInIndex;
    }

    /**
     * @param txid
     */
    public void setDuplicatedLeafInIndex(long txid)
    {
        duplicatedLeafInIndex.set(txid);

    }

    /**
     * @return the duplicatedLeafInIndex
     */
    public OpenBitSet getDuplicatedLeafInIndex()
    {
        return duplicatedLeafInIndex;
    }

    /**
     * @param transactionDocsInIndex
     *            the transactionDocsInIndex to set
     */
    public void setTransactionDocsInIndex(long transactionDocsInIndex)
    {
        this.transactionDocsInIndex = transactionDocsInIndex;
    }

    /**
     * @return the missingFromIndex
     */
    public OpenBitSet getMissingFromIndex()
    {
        return missingFromIndex;
    }

    /**
     * @return the duplicatedInIndex
     */
    public OpenBitSet getDuplicatedInIndex()
    {
        return duplicatedInIndex;
    }

    /**
     * @return the inIndexButNotInDb
     */
    public OpenBitSet getInIndexButNotInDb()
    {
        return inIndexButNotInDb;
    }

    /**
     * @return the dbTransactionCount
     */
    public long getDbTransactionCount()
    {
        return dbTransactionCount;
    }

    /**
     * @param dbTransactionCount
     *            the dbTransactionCount to set
     */
    public void setDbTransactionCount(long dbTransactionCount)
    {
        this.dbTransactionCount = dbTransactionCount;
    }

    public void setMissingFromIndex(long txid)
    {
        missingFromIndex.set(txid);
    }

    public void setDuplicatedInIndex(long txid)
    {
        duplicatedInIndex.set(txid);
    }

    public void setInIndexButNotInDb(long txid)
    {
        inIndexButNotInDb.set(txid);
    }

    /**
     * @return the lastIndexCommitTime
     */
    public long getLastIndexCommitTime()
    {
        return lastIndexedCommitTime;
    }

    /**
     * @param lastIndexCommitTime
     *            the lastIndexCommitTime to set
     */
    public void setLastIndexedCommitTime(long lastIndexedCommitTime)
    {
        this.lastIndexedCommitTime = lastIndexedCommitTime;
    }

    /**
     * @return the lastIndexedIdBeforeHoles
     */
    public long getLastIndexedIdBeforeHoles()
    {
        return lastIndexedIdBeforeHoles;
    }

    /**
     * @param lastIndexedIdBeforeHoles
     *            the lastIndexedIdBeforeHoles to set
     */
    public void setLastIndexedIdBeforeHoles(long lastIndexedIdBeforeHoles)
    {
        this.lastIndexedIdBeforeHoles = lastIndexedIdBeforeHoles;
    }

}