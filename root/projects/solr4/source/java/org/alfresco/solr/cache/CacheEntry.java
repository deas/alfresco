/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.solr.cache;

import java.util.Comparator;


public class CacheEntry implements Comparator<CacheEntry>
{
    int leaf;

    int path;

    long dbid;

    long aclid;

    int owner;

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

    /**
     * @return the alcid
     */
    public long getAclid()
    {
        return aclid;
    }

    /**
     * @param alcid
     *            the alcid to set
     */
    public void setAclid(long aclid)
    {
        this.aclid = aclid;
    }

    /**
     * @return the owner
     */
    public int getOwner()
    {
        return owner;
    }

    /**
     * @param owner
     *            the owner to set
     */
    public void setOwner(int owner)
    {
        this.owner = owner;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (aclid ^ (aclid >>> 32));
        result = prime * result + (int) (dbid ^ (dbid >>> 32));
        result = prime * result + leaf;
        result = prime * result + owner;
        result = prime * result + path;
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        if (aclid != other.aclid)
            return false;
        if (dbid != other.dbid)
            return false;
        if (leaf != other.leaf)
            return false;
        if (owner != other.owner)
            return false;
        if (path != other.path)
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "CacheEntry [dbid=" + dbid + ", leaf=" + leaf + ", path=" + path + ", aclid=" + aclid + ", owner=" + owner + "]";
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