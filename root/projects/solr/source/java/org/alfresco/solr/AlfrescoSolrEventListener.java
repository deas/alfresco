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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.alfresco.repo.search.impl.lucene.analysis.NumericEncoder;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrEventListener;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;

/**
 * @author Andy
 */
public class AlfrescoSolrEventListener implements SolrEventListener
{
    public static String ALFRESCO_CACHE = "alfrescoCache";

    public static String KEY_ORDERED_BY_DBID = "ordered_by_dbid";

    public static String KEY_INDEXED_BY_DOC_ID = "indexed_by_doc_id";

    private NamedList args;

    /*
     * (non-Javadoc)
     * @see org.apache.solr.core.SolrEventListener#init(org.apache.solr.common.util.NamedList)
     */
    @Override
    public void init(NamedList args)
    {
        this.args = args;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.core.SolrEventListener#newSearcher(org.apache.solr.search.SolrIndexSearcher,
     * org.apache.solr.search.SolrIndexSearcher)
     */
    @Override
    public void newSearcher(SolrIndexSearcher newSearcher, SolrIndexSearcher currentSearcher)
    {
        SolrIndexReader reader = newSearcher.getReader();
        System.out.println("Max "+reader.maxDoc());
        System.out.println("Docs "+reader.numDocs());
        System.out.println("Deleted "+reader.numDeletedDocs());
        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_INDEXED_BY_DOC_ID, new CacheEntry[0]);
        int max = reader.maxDoc();
        CacheEntry[] indexedByDocId = new CacheEntry[max];
        HashMap<Long, CacheEntry> unmatchedByDBID = new HashMap<Long, CacheEntry>(10000);
        try
        {
            for (int i = 0; i < max; i++)
            {
                if (reader.isDeleted(i))
                {
                    continue;
                }

                Document document = reader.document(i);
                Field field = document.getField("DBID");
                if (field != null)
                {
                    String string = field.stringValue();
                    long dbid = Long.parseLong(string);
                    
                    field = document.getField("ISNODE");
                    boolean isLeaf;
                    if(field == null)
                    {
                        isLeaf = false;
                    }
                    else
                    {
                        string = field.stringValue();
                        isLeaf = string.equals("T");
                    }
                 
                    CacheEntry entry = unmatchedByDBID.get(dbid);
                    if(entry == null)
                    {
                        entry = new CacheEntry(dbid);
                        unmatchedByDBID.put(dbid, entry);
                    }
                    else
                    {
                        unmatchedByDBID.remove(dbid);
                    }


                    if (isLeaf)
                    {
                        entry.setLeaf(i);
                    }
                    else
                    {
                        entry.setPath(i);
                    }
                    indexedByDocId[i] = entry;
                }
            }

        }
        catch (IOException e)
        {

        }
        newSearcher.cacheInsert(ALFRESCO_CACHE, KEY_INDEXED_BY_DOC_ID, indexedByDocId);

    }

    /*
     * (non-Javadoc)
     * @see org.apache.solr.core.SolrEventListener#postCommit()
     */
    @Override
    public void postCommit()
    {
        throw new UnsupportedOperationException();
    }

    public static class CacheEntry implements Comparator<CacheEntry>
    {
        int leaf;

        int path;

        long dbid;

        /**
         * @param doc
         * @param dbid
         */
        public CacheEntry(long dbid)
        {
            this.dbid = dbid;
        }

        public int getLeaf()
        {
            return leaf;
        }

        public void setLeaf(int leaf)
        {
            this.leaf = leaf;
        }

        public int getPath()
        {
            return path;
        }

        public void setPath(int path)
        {
            this.path = path;
        }

        public long getDbid()
        {
            return dbid;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (dbid ^ (dbid >>> 32));
            result = prime * result + leaf;
            result = prime * result + path;
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheEntry other = (CacheEntry) obj;
            if (dbid != other.dbid)
                return false;
            if (leaf != other.leaf)
                return false;
            if (path != other.path)
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "CacheEntry [dbid=" + dbid + ", leaf=" + leaf + ", path=" + path + "]";
        }

        /*
         * (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(CacheEntry o1, CacheEntry o2)
        {
            if (o1 == null && o2 == null)
                return 0;
            if (o1 == null)
                return 1;
            if (o2 == null)
                return -1;
            return (o1.getDbid() < o2.getDbid() ? -1 : ((o1.getDbid() == o2.getDbid()) ? 0 : 1));
        }
    }

}
