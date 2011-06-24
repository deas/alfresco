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
 * Parameters defining the {@link CannedQuery named query} to execute.
 * <p/>
 * The implementations of the underlying queries may be vastly different
 * depending on seemingly-minor variations in the parameters; only set the
 * parameters that are required.
 * 
 * @author Derek Hulley
 * @since 4.0
 */
public class CannedQueryParameters
{
    public static final int DEFAULT_REQUEST_TOTAL_COUNT_MAX = 0; // default 0 => don't request total count
    
    private final Object parameterBean;
    private final CannedQueryPageDetails pageDetails;
    private final CannedQuerySortDetails sortDetails;
    private final String authenticationToken;
    private final int requestTotalResultCountMax;
    private final String queryExecutionId;

    /**
     * <ul>
     *    <li><b>pageDetails</b>: <tt>null</tt></li>
     *    <li><b>sortDetails</b>: <tt>null</tt></li>
     *    <li><b>authenticationToken</b>: <tt>null</tt></li>
     *    <li><b>requestTotalResultCountMax</b>: <tt>0</tt></li>
     *    <li><b>queryExecutionId</b>: <tt>null</tt></li>
     * </ul>
     *  
     * @see #NamedQueryParameters(Object, CannedQueryPageDetails, CannedQuerySortDetails, String, int, String)
     */
    public CannedQueryParameters(Object parameterBean)
    {
        this (parameterBean, null, null, null, DEFAULT_REQUEST_TOTAL_COUNT_MAX, null);
    }

    /**
     * Defaults:
     * <ul>
     *    <li><b>pageDetails.pageNumber</b>: <tt>1</tt></li>
     *    <li><b>pageDetails.pageCount</b>: <tt>1</tt></li>
     *    <li><b>authenticationToken</b>: <tt>null</tt></li>
     *    <li><b>requestTotalResultCountMax</b>: <tt>0</tt></li>
     * </ul>
     *  
     * @see #NamedQueryParameters(Object, CannedQueryPageDetails, CannedQuerySortDetails, String, int, String)
     */
    public CannedQueryParameters(
            Object parameterBean,
            int skipResults,
            int pageSize,
            String queryExecutionId)
    {
        this (parameterBean, new CannedQueryPageDetails(skipResults, pageSize, CannedQueryPageDetails.DEFAULT_PAGE_NUMBER, CannedQueryPageDetails.DEFAULT_PAGE_COUNT), null, "", DEFAULT_REQUEST_TOTAL_COUNT_MAX, queryExecutionId);
    }

    /**
     * Defaults:
     * <ul>
     *    <li><b>authenticationToken</b>: <tt>null</tt></li>
     *    <li><b>requestTotalResultCountMax</b>: <tt>0</tt></li>
     *    <li><b>queryExecutionId</b>: <tt>null</tt></li>
     * </ul>
     *  
     * @see #NamedQueryParameters(Object, CannedQueryPageDetails, CannedQuerySortDetails, String, int, String)
     */
    public CannedQueryParameters(
            Object parameterBean,
            CannedQueryPageDetails pageDetails,
            CannedQuerySortDetails sortDetails)
    {
        this (parameterBean, pageDetails, sortDetails, null, DEFAULT_REQUEST_TOTAL_COUNT_MAX, null);
    }

    /**
     * Construct all the parameters for executing a named query.  Note that the allowable values
     * for the arguments depend on the specific query being executed.
     * 
     * @param parameterBean         the values that the query will be based on or <tt>null</tt>
     *                              if not relevant to the query
     * @param pageDetails           the type of paging to be applied or <tt>null</tt> for none
     * @param sortDetails           the type of sorting to be applied or <tt>null</tt> for none
     * @param authenticationToken   an authentication token (application-dependent) to be supplied
     *                              if permission-based filtering should be applied, otherwise <tt>null</tt>
     * @param requestTotalResultCountMax <tt>true</tt> if the query should not only return the required rows
     *                              but should also return the total number of possible rows
     * @param queryExecutionId      ID of a previously-executed query to be used during follow-up
     *                              page requests - <tt>null</tt> if not available
     */
    @SuppressWarnings("unchecked")
    public CannedQueryParameters(
            Object parameterBean,
            CannedQueryPageDetails pageDetails,
            CannedQuerySortDetails sortDetails,
            String authenticationToken,
            int requestTotalResultCountMax,
            String queryExecutionId)
    {
        this.parameterBean = parameterBean;
        this.pageDetails = pageDetails == null ? new CannedQueryPageDetails() : pageDetails;
        this.sortDetails = sortDetails == null ? new CannedQuerySortDetails() : sortDetails;
        this.authenticationToken = authenticationToken;
        this.requestTotalResultCountMax = requestTotalResultCountMax;
        this.queryExecutionId = queryExecutionId;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("NamedQueryParameters ")
          .append("[parameterBean=").append(parameterBean)
          .append(", pageDetails=").append(pageDetails)
          .append(", sortDetails=").append(sortDetails)
          .append(", authenticationToken=").append(authenticationToken)
          .append(", requestTotalResultCountMax=").append(requestTotalResultCountMax)
          .append(", queryExecutionId=").append(queryExecutionId)
          .append("]");
        return sb.toString();
    }

    public String getQueryExecutionId()
    {
        return queryExecutionId;
    }

    /**
     * @return              the sort details (never <tt>null</tt>)
     */
    public CannedQuerySortDetails getSortDetails()
    {
        return sortDetails;
    }

    /**
     * @return              query execution authentication token (may be <tt>null</tt> for no authentication)
     */
    public String getAuthenticationToken()
    {
        return authenticationToken;
    }

    /**
     * @return              the query paging details (never <tt>null</tt>)
     */
    public CannedQueryPageDetails getPageDetails()
    {
        return pageDetails;
    }

    /**
     * @return                      if > 0 then the query should not only return the required rows but should
     *                              also return the total count (number of possible rows) up to the given max
     *                              if 0 then query does not need to return the total count
     */
    public int requestTotalResultCountMax()
    {
        return requestTotalResultCountMax;
    }

    /**
     * @return parameterBean        the values that the query will be based on or <tt>null</tt>
     *                              if not relevant to the query
     */
    public Object getParameterBean()
    {
        return parameterBean;
    }

}
