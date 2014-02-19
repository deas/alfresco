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

import java.util.List;
import java.util.Map;

import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;

/**
 * This class acts as a client to REST api for /tasks related requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class WorkflowAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(WorkflowAPI.class);

    /**
     * Gets {@link JSONObject} of task for a particular workflow id.
     * URL - /tasks/{taskId}/
     * 
     * @param authUser
     * @param domain
     * @param userRequestingSM
     * @param siteMembershipRequestID
     * @return boolean
     * @throws PublicApiException
     */
    public JSONObject getTaskFromId(String authUser, String domain, String taskID) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        JSONObject taskJson = taskClient.findTaskById(taskID);

        logger.info("Task JSON received: /n" + taskJson);
        return taskJson;
    }

    /**
     * Gets {@link JSONObject} task-form-model of task for a particular workflow id for form-model.
     * URL - /tasks/{taskId}/task-form-model
     * 
     * @param authUser
     * @param domain
     * @param taskID
     * @return
     * @throws PublicApiException
     */
    public JSONObject getTaskFromIdFormModel(String authUser, String domain, String taskID) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        JSONObject taskJson = taskClient.findTaskFormModel(taskID);

        logger.info("Task JSON received: /n" + taskJson);
        return taskJson;
    }

    /**
     * Gets {@link JSONObject} of task for a particular workflow id for
     * variables URL - /tasks/{taskId}/variables
     * 
     * @param authUser
     * @param domain
     * @param taskID
     * @param params 
     * @return
     * @throws PublicApiException
     */
    public JSONObject getTaskVariables(String authUser, String domain, String taskID, Map<String, String> params) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        JSONObject taskJson = taskClient.findTaskVariables(taskID, params);

        logger.info("Task JSON received: /n" + taskJson);
        return taskJson;
    }

    /**
     * Gets {@link JSONObject} of task for a particular workflow id for
     * variables. URL - /tasks/{taskId}/items
     * 
     * @param authUser
     * @param domain
     * @param taskID
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public JSONObject getTaskItems(String authUser, String domain, String taskID) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        JSONObject response = taskClient.findTaskItems(taskID);

        logger.info("Task JSON received: /n" + response);
        return response;
    }

    /**
     * Gets the JSONObject for item specified by item id and task id.
     * 
     * @param authUser
     * @param domain
     * @param taskID
     * @param itemId
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public JSONObject getTaskItem(String authUser, String domain, String taskID, String itemId) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        JSONObject response = taskClient.findTaskItem(taskID, itemId);

        logger.info("Task JSON received: /n" + response);
        return response;
    }

    /**
     * Creates {@link JSONObject} of task for a particular workflow id for
     * items. URL - tasks/{taskId} /items/ {itemId} (POST)
     * 
     * @param authUser
     * @param domain
     * @param taskID
     * @param items
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public JSONObject createItemsForTask(String authUser, String domain, String taskID, JSONObject items) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        HttpResponse response = taskClient.create("tasks", taskID, "items", null, items.toJSONString(), "Failed to create task items");

        logger.info("Task JSON received: /n" + response.getJsonResponse());
        return response.getJsonResponse();
    }

    /**
     * Gets {@link JSONObject} of tasks for a particular workflow id. URL -
     * /tasks/
     * 
     * @param authUser
     * @param domain
     * @param taskID
     * @param params
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    public JSONObject getTasks(String authUser, String domain, Map<String, String> params) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        JSONObject taskJson = taskClient.findTasks(params);
        logger.info("Task JSON received: /n" + taskJson);
        return taskJson;
    }

    /**
     * Updates a task of taskID with the new {@link JSONObject}.
     * 
     * @param authUser
     * @param domain
     * @param taskId
     * @param taskJson
     * @param selectedFields
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    protected JSONObject updateTask(String authUser, String domain, String taskId, JSONObject taskJson, List<String> selectedFields) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        JSONObject json = taskClient.updateTask(taskId, taskJson, selectedFields);
        logger.info("Task JSON updated: /n" + json);
        return json;
    }

    /**
     * Updates a task variable with {@link JSONObject} for a particular workflow
     * id.
     * 
     * @param authUser
     * @param domain
     * @param taskId
     * @param taskJson
     * @param selectedFields
     * @param variableName
     * @return {@link JSONObject}
     * @throws PublicApiException
     */
    protected JSONObject updateTaskVariable(String authUser, String domain, String taskId, JSONObject json, String variableName)
 throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        json = taskClient.updateTaskVariable(taskId, variableName, json);
        logger.info("Task JSON updated: /n" + json);
        return json;
    }

    /**
     * Deletes the task item for taskId and item
     * 
     * @param authUser
     * @param domain
     * @param taskId
     * @param json
     * @param item
     * @return {@link HttpResponse}
     * @throws PublicApiException
     */
    protected HttpResponse deleteTaskItems(String authUser, String domain, String taskId, String itemId)
 throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        HttpResponse resp = taskClient.remove("tasks", taskId, "items", itemId, "Failed to delete task items");
        logger.info("Task JSON updated: /n" + resp);
        return resp;
    }
}
