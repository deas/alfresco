/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
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
package org.alfresco.share.util.api;

import java.util.Map;

import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.Comment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Client for the REST API that deals with "/comments" requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class CommentsAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(CommentsAPI.class);

    /**
     * Gets the {@link ListResponse} of {@link Comment} for params.
     * 
     * @param authUser
     * @param domain
     * @param nodeId
     * @param params
     * @param nodeId
     * @return a {@link ListResponse} of {@link Comment}
     * @throws PublicApiException
     */
    public ListResponse<Comment> getNodeComments(String authUser, String domain, Map<String, String> params, String nodeId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<Comment> response = commentsClient.getNodeComments(nodeId, params);
        logger.info("Response received: /n" + response);
        return response;
    }

    /**
     * Gets the {@link Comment} for node id.
     * 
     * @param authUser
     * @param domain
     * @param nodeId
     * @return {@link Comment}
     * @throws PublicApiException
     */
    public Comment getNodeComment(String authUser, String domain, String nodeId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        Comment response = commentsClient.getNodeComment(nodeId);
        logger.info("Response received: /n" + response);
        return response;
    }

    /**
     * Updates a {@link Comment} .
     * 
     * @param authUser
     * @param domain
     * @param nodeId
     * @param commentId
     * @param comment
     * @return {@link Comment}
     * @throws PublicApiException
     */
    public Comment updateNodeComment(String authUser, String domain, String nodeId, String commentId, Comment comment) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        Comment response = commentsClient.updateNodeComment(nodeId, commentId, comment);
        logger.info("Site found received: /n" + response);
        return response;
    }

    /**
     * Creates a {@link Comment} .
     * 
     * @param authUser
     * @param domain
     * @param nodeId
     * @param commentId
     * @param comment
     * @return {@link Comment}
     * @throws PublicApiException
     */
    public Comment createNodeComment(String authUser, String domain, String nodeId, Comment comment) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        Comment response = commentsClient.createNodeComment(nodeId, comment);
        logger.info("Site found received: /n" + response);
        return response;
    }

}
