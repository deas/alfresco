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
package org.alfresco.query;

import java.util.List;

/**
 * Interface for results returned by {@link CannedQuery canned queries}.
 * 
 * @author Derek Hulley
 * @since 4.0
 */
public interface CannedQueryResults<R>
{
    /**
     * Get the instance of the query that generated these results.
     * 
     * @return              the query that generated these results.
     */
    CannedQuery<R> getOriginatingQuery();
    
    /**
     * Get a unique ID associated with these query results.  This must be available before and
     * after execution i.e. it must depend on the type of query and the query parameters
     * rather than the execution results.
     * 
     * @return                      a unique ID associated with the query execution results
     */
    String getQueryExecutionId();
    
    /**
     * Get the total number of results available within the pages of this result.
     * The count excludes results chopped out by the paging process i.e. it is only
     * the count of results physically obtainable through this instance.
     * 
     * @return                  number of results available in the pages
     */
    int getPagedResultCount();
    
    /**
     * Get the number of pages available
     * 
     * @return                  the number of pages available
     */
    int getPageCount();
    
    /**
     * Get the total result count assuming no paging applied.  This value will only be available if
     * the query supports it and the client requested it.
     * 
     * @return                  Returns the total results (all results, including the paged results returned)
     * @throws IllegalStateException if the total number of available results were not requested
     */
    int getTotalResultCount();
    
    /**
     * Get a single result if there is only one result expected.
     * 
     * @return                  a single result
     * @throws IllegalStateException if the query returned more than one result
     */
    R getSingleResult();

    /**
     * Get the paged results
     * 
     * @return                  a list of paged results
     */
    List<List<R>> getPages();
}
