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
package org.alfresco.query;


/**
 * Marker interface for single page request (unsorted)
 * 
 * @author janv
 * @since 4.0
 */
public interface PagingRequest
{
    /**
     * Results to skip before retrieving the page.  Usually a multiple of page size (ie. page size * num pages to skip).
     * Default is 0.
     * 
     * @return
     */
    public int getSkipCount();
    
    /**
     * Size of the page - if skip count is 0 then return up to max items.
     * 
     * @return
     */
    public int getMaxItems();
    
    /**
     * Request total count up to a given maximum.  Default is 0 => do not request total count (which allows possible query optimisation).
     */
    public int getRequestTotalCountMax();
    
    /**
     * Get a unique ID associated with these query results.  This must be available before and
     * after execution i.e. it must depend on the type of query and the query parameters
     * rather than the execution results.  Client has the option to pass this back as a hint when
     * paging.
     * 
     * @return                      a unique ID associated with the query execution results
     */
    public String getQueryExecutionId();
}
