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
import org.alfresco.rest.api.tests.client.data.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Client for the REST API that deals with "/tags" requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class TagsAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(TagsAPI.class);

    /**
     * Gets the {@link ListResponse} of {@link Tag} for params.
     * 
     * @param authUser
     * @param domain
     * @param nodeId
     * @param params
     * @return a {@link ListResponse} of {@link Tag}
     * @throws PublicApiException
     */
    public ListResponse<Tag> getTags(String authUser, String domain, Map<String, String> params) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<Tag> response = tagsClient.getTags(params);
        logger.info("Response received: /n" + response);
        return response;
    }

    /**
     * Gets the {@link Tag} for tag id.
     * 
     * @param authUser
     * @param domain
     * @param tagId
     * @return {@link Tag}
     * @throws PublicApiException
     */
    public Tag getTag(String authUser, String domain, String tagId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        Tag response = tagsClient.getSingle(tagId);
        logger.info("Response received: /n" + response);
        return response;
    }

    /**
     * Updates a {@link Tag} .
     * 
     * @param authUser
     * @param domain
     * @param tag
     * @return
     * @throws PublicApiException
     */
    public Tag updateTag(String authUser, String domain, Tag tag) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        Tag response = tagsClient.update(tag);
        logger.info("Site found received: /n" + response);
        return response;
    }
}
