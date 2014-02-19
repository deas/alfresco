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

import java.text.ParseException;
import java.util.Map;

import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.SiteMembershipRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * REST api for handling {@link SiteMembershipRequest} requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class SiteMembershipAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(SiteMembershipAPI.class);

    /**
     * GETs a list of {@link SiteMembershipRequest} object for a particular
     * user.
     * 
     * @param authUser
     * @param domain
     * @param userRequestingSM
     * @param params
     * @return
     * @throws ParseException
     * @throws PublicApiException
     */
    public ListResponse<SiteMembershipRequest> getSiteMembershipRequest(String authUser, String domain, String userRequestingSM, Map<String, String> params)
            throws PublicApiException, ParseException
    {

        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<SiteMembershipRequest> resp = siteMembershipRequestsProxy.getSiteMembershipRequests(userRequestingSM, params);
        logger.info("Received response: " + resp);
        return resp;
    }

    /**
     * GETs a {@link SiteMembershipRequest} object for a particular user and
     * site.
     * 
     * @param authUser
     * @param domain
     * @param userRequestingSM
     * @param params
     * @return
     * @throws ParseException
     * @throws PublicApiException
     */
    public SiteMembershipRequest getSiteMembershipRequestForSite(String authUser, String domain, String userRequestingSM, String siteId)
            throws PublicApiException, ParseException
    {

        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        SiteMembershipRequest resp = siteMembershipRequestsProxy.getSiteMembershipRequest(userRequestingSM, siteId);
        logger.info("Received response: " + resp);
        return resp;
    }

    /**
     * POSTs a {@link SiteMembershipRequest} object to create a new SMR request.
     * 
     * @param authUser
     * @param domain
     * @param userRequestingSM
     * @param siteID
     * @param message
     * @return {@link SiteMembershipRequest}
     * @throws ParseException
     * @throws PublicApiException
     */
    public SiteMembershipRequest createSiteMembershipRequest(String authUser, String domain, String userRequestingSM, String siteID, String message)
            throws PublicApiException, ParseException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        SiteMembershipRequest siteMembershipRequest = new SiteMembershipRequest();
        siteMembershipRequest.setId(siteID);
        if (StringUtils.isNotEmpty(message))
        {
            siteMembershipRequest.setMessage(message);
        }
        SiteMembershipRequest resp = siteMembershipRequestsProxy.createSiteMembershipRequest(userRequestingSM, siteMembershipRequest);
        logger.info("Received response: " + resp);
        return resp;
    }

    /**
     * Sends a PUT request to update any existing SMR identified by an id.
     * 
     * @param authUser
     * @param domain
     * @param userRequestingSM
     * @param siteID
     * @param message
     * @return {@link SiteMembershipRequest}
     * @throws ParseException
     * @throws PublicApiException
     */
    public SiteMembershipRequest updateSiteMembershipRequest(String authUser, String domain, String userRequestingSM, String siteID, String message)
            throws PublicApiException, ParseException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        SiteMembershipRequest siteMembershipRequest = new SiteMembershipRequest();

        siteMembershipRequest.setId(siteID);
        siteMembershipRequest.setMessage(message);
        SiteMembershipRequest resp = siteMembershipRequestsProxy.updateSiteMembershipRequest(userRequestingSM, siteMembershipRequest);
        logger.info("Received response: " + resp);
        return resp;
    }

    /**
     * Sends a DELETE request to cancel any existing SMR identified by an id.
     * 
     * @param authUser
     * @param domain
     * @param userRequestingSM
     * @param siteMembershipRequestID
     * @return boolean
     * @throws PublicApiException
     */
    public HttpResponse cancelSiteMembershipRequest(String authUser, String domain, String userRequestingSM, String siteMembershipRequestID)
            throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        HttpResponse response = siteMembershipRequestsProxy.remove("people", userRequestingSM, "site-membership-requests", siteMembershipRequestID,
                "Failed to cancel siteMembershipRequest");
        logger.info("Received response: " + response);
        return response;
    }
}
