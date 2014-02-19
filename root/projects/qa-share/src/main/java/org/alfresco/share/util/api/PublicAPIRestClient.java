/*
 * Copyright (C) 2005-2013 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.util.api;

import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

public class PublicAPIRestClient extends AlfrescoHttpClient
{
    public PublicAPIRestClient() throws Exception
    {
        super();
        // TODO Auto-generated constructor stub
    }

    private static Log logger = LogFactory.getLog(PublicAPIRestClient.class);
    
    @Override
    public void setup() throws Exception
    {
        //super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    /**
     * Method to send a Site-Membership-Request for a user using Public API.
     * 
     * @param drone WebDrone Instance
     * @param authUser String User details to be used for basic auth
     * @param domain String domain or network
     * @param userRequestingSM String User-name of the user who wishes to join the site
     * @param siteID String Site ID for site, the user wishes to join
     * @return Boolean <tt>true</tt> when the request is successful and returns Http Response 201
     * @throws Exception
     */
    public static HttpResponse requestSiteMembership(WebDrone drone, String authUser, String domain, String userRequestingSM, String siteID) throws Exception
    {        
        String reqURL = getAPIURL(drone) + getDomainForAPI(drone,domain) + apiContextPublicAPI + "people/" + userRequestingSM + "/site-membership-requests";

        logger.info("Using Url - " + reqURL + " for siteMembershipRequest");

        String[] authDetails = getAuthDetails(authUser);
        String[] headers = { "Content-Type", "application/json;charset=utf-8", "key", getHeaderKey() };
        String[] body = { "id", getSiteShortname(siteID) };

        HttpPost request = generatePostRequest(reqURL, headers, body);
        HttpClient client = getHttpClientWithBasicAuth(reqURL, authDetails[0], authDetails[1]);
        HttpResponse response = executeRequestHttpResp(client, request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
        {
            logger.info("SMR: " + authUser + " : Site: " + siteID);
        }
        else
        {
            logger.error("SMR Failed for user: " + authUser + " Response Received: " + response.getStatusLine());
        }
        return response;
    }

}
