/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.solr.client;

public class Transaction
{
    private long id;
    private long commitTimeMs;
    private long updates;
    private long deletes;

    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public long getCommitTimeMs()
    {
        return commitTimeMs;
    }
    public void setCommitTimeMs(long commitTimeMs)
    {
        this.commitTimeMs = commitTimeMs;
    }
    public long getUpdates()
    {
        return updates;
    }
    public void setUpdates(long updates)
    {
        this.updates = updates;
    }
    public long getDeletes()
    {
        return deletes;
    }
    public void setDeletes(long deletes)
    {
        this.deletes = deletes;
    }
    @Override
    public String toString()
    {
        return "TransactionInfo [id=" + id + ", commitTimeMs=" + commitTimeMs + ", updates=" + updates + ", deletes="
                + deletes + "]";
    }
}
