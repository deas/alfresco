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

import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.workflow.api.model.ProcessInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * REST-API for handling /processes requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class ProcessesAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(ProcessesAPI.class);

    /**
     * Gets a list Favourite object for a particular person Id.
     * 
     * @param authUser
     * @param domain
     * @param favMap
     * @return ListResponse<Favourite>
     * @throws PublicApiException 
     */
    public JSONObject getProcesses(String authUser, String domain, Map<String, String> params) throws PublicApiException 
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        HttpResponse response = processesClient.getAll("processes", null, null, null, params, "Failed to get process instances");
        logger.info("Received response: " + response.getJsonResponse());
        return response.getJsonResponse();
    }

    /**
     * Gets a list Favourite object for a particular person Id.
     * 
     * @param authUser
     * @param domain
     * @param favMap
     * @return ListResponse<Favourite>
     * @throws PublicApiException
     */
    public ProcessInfo getProcessById(String authUser, String domain, String processInstanceId) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        ProcessInfo response = processesClient.findProcessById(processInstanceId);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Gets a list Favourite object for a particular person Id.
     * 
     * @param authUser
     * @param domain
     * @param itemId
     * @param favMap
     * @return ListResponse<Favourite>
     * @throws PublicApiException
     */
    public JSONObject getProcessItems(String authUser, String domain, String processInstanceId) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        JSONObject response = processesClient.findProcessItems(processInstanceId);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Gets a list JSONObject object for a particular processInstanceId, String
     * itemId
     * 
     * @param authUser
     * @param domain
     * @param itemId
     * @param itemId
     * @param favMap
     * @return ListResponse<Favourite>
     * @throws PublicApiException
     */
    public JSONObject getProcessItem(String authUser, String domain, String processInstanceId, String itemId) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        JSONObject response = processesClient.findProcessItem(processInstanceId, itemId);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Get the process tasks for a particular processInstanceId filtered by
     * params
     * 
     * @param authUser
     * @param domain
     * @param processInstanceId
     * @param params
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public JSONObject getProcessTasks(String authUser, String domain, String processInstanceId, Map<String, String> params) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        JSONObject response = processesClient.getTasks(processInstanceId, params);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Get the process variables for a particular processInstanceId.
     * 
     * @param authUser
     * @param domain
     * @param processInstanceId
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public JSONObject getProcessVariables(String authUser, String domain, String processInstanceId) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        JSONObject response = processesClient.getProcessvariables(processInstanceId);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Get the process image for a particular processInstanceId filtered by
     * params
     * 
     * @param authUser
     * @param domain
     * @param processInstanceId
     * @param params
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public HttpResponse getProcessImage(String authUser, String domain, String processInstanceId, Map<String, String> params) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        HttpResponse response = processesClient.getAll("processes", processInstanceId, "image", null, params,
                "Failed to get activity instances of processInstanceId " + processInstanceId);
        return response;
    }

    /**
     * Get the process activities for a particular processInstanceId filtered by
     * params
     * 
     * @param authUser
     * @param domain
     * @param processInstanceId
     * @param params
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public JSONObject getProcessActivities(String authUser, String domain, String processInstanceId, Map<String, String> params) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        JSONObject response = processesClient.getActivities(processInstanceId, params);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Create process with a process JSON.
     * 
     * @param authUser
     * @param domain
     * @param processInstanceId
     * @param params
     * @return {@link ProcessInfo}
     * @throws PublicApiException
     */
    public ProcessInfo createProcess(String authUser, String domain, JSONObject processJson) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        ProcessInfo response = processesClient.createProcess(processJson.toJSONString());
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Create process item for a particular processId.
     * 
     * @param authUser
     * @param domain
     * @param processJson
     * @param processId
     * @param items
     * @return {@link HttpResponse}
     * @throws PublicApiException
     */
    public HttpResponse createProcessItems(String authUser, String domain, String processId, JSONObject items) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        HttpResponse response = processesClient.create("processes", processId, "items", null, items.toJSONString(), "Failed to create items.");
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Create process variable for a particular processId.
     * 
     * @param authUser
     * @param domain
     * @param processJson
     * @param processId
     * @param items
     * @param variables
     * @return {@link HttpResponse}
     * @throws PublicApiException
     */
    public JSONObject createProcessVariables(String authUser, String domain, String processId, JSONArray variables) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        JSONObject response = processesClient.createVariables(processId, variables);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Update process variable for a particular processId and variableName.
     * 
     * @param authUser
     * @param domain
     * @param processId
     * @param variableName
     * @param variable
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public JSONObject updateProcessVariableName(String authUser, String domain, String processId, String variableName, JSONObject variable) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        JSONObject response = processesClient.updateVariable(processId, variableName, variable);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Update process variable for a particular processId
     * 
     * @param authUser
     * @param domain
     * @param processId
     * @param variableName
     * @param variableArray
     * @param variable
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public JSONObject updateProcessVariables(String authUser, String domain, String processId, String variableName, JSONObject variable) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        JSONObject response = processesClient.updateVariable(processId, variableName, variable);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Update process for a particular processId filtered by params.
     * 
     * @param authUser
     * @param domain
     * @param processId
     * @param variableName
     * @param processJson
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    // public JSONObject updateProcess(String authUser, String domain, String
    // processId, JSONObject processJson) throws PublicApiException
    // {
    // workflowApiClient.setRequestContext(new RequestContext(domain,
    // getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
    // HttpResponse response = processesClient.update("processes", processId,
    // null, null, processJson.toJSONString(), "Failed to update variable");
    // logger.info("Received response: " + response);
    // return response.getJsonResponse();
    // }

    /**
     * Delete process for a particular processId.
     * 
     * @param authUser
     * @param domain
     * @param processId
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public HttpResponse deleteProcess(String authUser, String domain, String processId) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        HttpResponse response = processesClient.remove("processes", processId, null, null, "Failed to delete process");
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Delete process item for a particular processId.
     * 
     * @param authUser
     * @param domain
     * @param processId
     * @param itemId
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public HttpResponse deleteProcessItem(String authUser, String domain, String processId, String itemId) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        HttpResponse response = processesClient.remove("processes", processId, "items", itemId, "Failed to delete item");
        logger.info("Received response: " + response);
        return response;
    }

}
