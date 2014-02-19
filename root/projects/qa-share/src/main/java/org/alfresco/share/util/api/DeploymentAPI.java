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
import org.alfresco.rest.workflow.api.model.Deployment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * REST-API for handling /deployment requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class DeploymentAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(DeploymentAPI.class);

    /**
     * Gets a list {@link Deployment} object for a particular person Id.
     * 
     * @param authUser
     * @param domain
     * @param params
     * @return {@link ListResponse} of {@link Deployment}
     * @throws PublicApiException
     */
    public ListResponse<Deployment> getDeployments(String authUser, String domain, Map<String, String> params) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        ListResponse<Deployment> response = deploymentsClient.getDeployments(params);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Gets a {@link Deployment} object for a particular deploymentId 
     * @param authUser
     * @param domain
     * @param deploymentId
     * @return {@link Deployment}
     * @throws PublicApiException
     */
    public Deployment getDeploymentById(String authUser, String domain, String deploymentId) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        Deployment response = deploymentsClient.findDeploymentById(deploymentId);
        logger.info("Received response: " + response);
        return response;
    }

}
