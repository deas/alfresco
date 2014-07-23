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



public class OwnerLookUp
{
    int owner;

    int start;

    int end;

    public OwnerLookUp(int owner)
    {
        this.owner = owner;
    }

    public OwnerLookUp(int owner, int start)
    {
        this.owner = owner;
        this.start = start;
    }

    public void setEnd(int end)
    {
        this.end = end;
    }

    /**
     * @return the owner
     */
    public int getOwner()
    {
        return owner;
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

}