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

    OpenBitSet missingTxFromIndex = new OpenBitSet();

    OpenBitSet duplicatedTxInIndex = new OpenBitSet();

    OpenBitSet txInIndexButNotInDb = new OpenBitSet();

    OpenBitSet duplicatedLeafInIndex = new OpenBitSet();
    
    OpenBitSet duplicatedAuxInIndex = new OpenBitSet();

    long transactionDocsInIndex;
    
    long uniqueTransactionDocsInIndex;
    
    long uniqueAclTransactionDocsInIndex;

    long aclTransactionDocsInIndex;

    long leafDocCountInIndex;
    
    long auxDocCountInIndex;

    long lastIndexedCommitTime;

    long lastIndexedIdBeforeHoles;

    long dbAclTransactionCount;

    OpenBitSet missingAclTxFromIndex = new OpenBitSet();

    OpenBitSet duplicatedAclTxInIndex = new OpenBitSet();

    OpenBitSet aclTxInIndexButNotInDb = new OpenBitSet();

    /**
     * @return the transactionDocsInIndex
     */
    public long getTransactionDocsInIndex()
    {
        return transactionDocsInIndex;
    }

    public long getAclTransactionDocsInIndex()
    {
        return aclTransactionDocsInIndex;
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
     * @param auxCount
     */
    public void setAuxDocCountInIndex(long auxDocCountInIndex)
    {
        this.auxDocCountInIndex = auxDocCountInIndex;
    }

    /**
     * @return the leafDocCountInIndex
     */
    public long getAuxDocCountInIndex()
    {
        return auxDocCountInIndex;
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
     * @param txid
     */
    public void setDuplicatedAuxInIndex(long txid)
    {
        duplicatedAuxInIndex.set(txid);

    }

    /**
     * @return the duplicatedLeafInIndex
     */
    public OpenBitSet getDuplicatedAuxInIndex()
    {
        return duplicatedAuxInIndex;
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
     * @param transactionDocsInIndex
     *            the transactionDocsInIndex to set
     */
    public void setAclTransactionDocsInIndex(long aclTransactionDocsInIndex)
    {
        this.aclTransactionDocsInIndex = aclTransactionDocsInIndex;
    }

    /**
     * @return the missingFromIndex
     */
    public OpenBitSet getMissingTxFromIndex()
    {
        return missingTxFromIndex;
    }

    /**
     * @return the missingFromIndex
     */
    public OpenBitSet getMissingAclTxFromIndex()
    {
        return missingAclTxFromIndex;
    }

    /**
     * @return the duplicatedInIndex
     */
    public OpenBitSet getDuplicatedTxInIndex()
    {
        return duplicatedTxInIndex;
    }

    /**
     * @return the duplicatedInIndex
     */
    public OpenBitSet getDuplicatedAclTxInIndex()
    {
        return duplicatedAclTxInIndex;
    }

    /**
     * @return the inIndexButNotInDb
     */
    public OpenBitSet getTxInIndexButNotInDb()
    {
        return txInIndexButNotInDb;
    }

    /**
     * @return the inIndexButNotInDb
     */
    public OpenBitSet getAclTxInIndexButNotInDb()
    {
        return aclTxInIndexButNotInDb;
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

    public void setMissingTxFromIndex(long txid)
    {
        missingTxFromIndex.set(txid);
    }
    
    public void setMissingAclTxFromIndex(long txid)
    {
        missingAclTxFromIndex.set(txid);
    }

    public void setDuplicatedTxInIndex(long txid)
    {
        duplicatedTxInIndex.set(txid);
    }
    
    public void setDuplicatedAclTxInIndex(long txid)
    {
        duplicatedAclTxInIndex.set(txid);
    }

    public void setTxInIndexButNotInDb(long txid)
    {
        txInIndexButNotInDb.set(txid);
    }
    
    public void setAclTxInIndexButNotInDb(long txid)
    {
        aclTxInIndexButNotInDb.set(txid);
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

    /**
     * @param cardinality
     */
    public void setDbAclTransactionCount(long dbAclTransactionCount)
    {
        this.dbAclTransactionCount = dbAclTransactionCount;
    }

    /**
     * @return the dbAclTransactionCount
     */
    public long getDbAclTransactionCount()
    {
        return dbAclTransactionCount;
    }

    /**
     * @return the uniqueTransactionDocsInIndex
     */
    public long getUniqueTransactionDocsInIndex()
    {
        return uniqueTransactionDocsInIndex;
    }

    /**
     * @param uniqueTransactionDocsInIndex the uniqueTransactionDocsInIndex to set
     */
    public void setUniqueTransactionDocsInIndex(long uniqueTransactionDocsInIndex)
    {
        this.uniqueTransactionDocsInIndex = uniqueTransactionDocsInIndex;
    }

    /**
     * @return the uniqueAclTransactionDocsInIndex
     */
    public long getUniqueAclTransactionDocsInIndex()
    {
        return uniqueAclTransactionDocsInIndex;
    }

    /**
     * @param uniqueAclTransactionDocsInIndex the uniqueAclTransactionDocsInIndex to set
     */
    public void setUniqueAclTransactionDocsInIndex(long uniqueAclTransactionDocsInIndex)
    {
        this.uniqueAclTransactionDocsInIndex = uniqueAclTransactionDocsInIndex;
    }

    /**
     * @return the lastIndexedCommitTime
     */
    public long getLastIndexedCommitTime()
    {
        return lastIndexedCommitTime;
    }
    
    

}