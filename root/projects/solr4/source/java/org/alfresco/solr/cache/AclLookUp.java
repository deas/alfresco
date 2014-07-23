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


public class AclLookUp
{
    long aclid;

    int start;

    int end;

    public AclLookUp(long aclid)
    {
        this.aclid = aclid;
    }

    public AclLookUp(long aclid, int start)
    {
        this.aclid = aclid;
        this.start = start;
    }

    public void setEnd(int end)
    {
        this.end = end;
    }

    /**
     * @return the aclid
     */
    public long getAclid()
    {
        return aclid;
    }

    /**
     * @return the start
     */
    public int getStart()
    {
        return start;
    }

    /**
     * @return the end
     */
    public int getEnd()
    {
        return end;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return (int)(aclid ^ (aclid >>> 32));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof AclLookUp))
            return false;
        AclLookUp other = (AclLookUp) obj;
        if (aclid != other.aclid)
            return false;
        return true;
    }    

    public void setAclid(long aclid)
    {
        this.aclid = aclid;
    }
}
