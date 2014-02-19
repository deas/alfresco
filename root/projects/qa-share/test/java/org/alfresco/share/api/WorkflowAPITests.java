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

package org.alfresco.share.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.WorkflowAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class WorkflowAPITests extends WorkflowAPI
{

    private String testUser = "";
    private String testUserInvalid = "";
    private String taskId = "0";
    private String siteName;
    private String fileName;
    private String docGuid;
    private String invalidDomain;
    private String updatedTaskName;
    private static Log logger = LogFactory.getLog(WorkflowAPITests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();
        logger.info("Starting Tests: " + testName);

        testName = this.getClass().getSimpleName();

        invalidDomain = DOMAIN + System.currentTimeMillis();
        testUser = getUserNameFreeDomain(testName);
        testUserInvalid = "invalid" + testUser;

        siteName = getSiteName(testName) + System.currentTimeMillis();
        fileName = getFileName(testName) + System.currentTimeMillis();

        updatedTaskName = siteName;

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName });

        docGuid = ShareUser.getGuid(drone, fileName);

        NewWorkflowPage newWorkflowPage = ShareUserWorkFlow.startNewWorkFlow(drone);

        WorkFlowFormDetails formDetails = createWorkFlowDetails(siteName, testUser);
        newWorkflowPage.startWorkflow(formDetails).render();

        JSONObject taskJson = getTasks(testUser, DOMAIN, null);
        assertNotNull(taskJson);

        JSONArray entries = (JSONArray) taskJson.get("entries");
        for (int i = 0; i < entries.size(); i++)
        {
            JSONObject entry = (JSONObject) ((JSONObject) entries.get(i)).get("entry");

            if (entry.get("description").equals(siteName))
            {
                taskId = (String) entry.get("id");
            }
        }
    }

    @Test()
    public void ALF_1084() throws Exception
    {
        JSONObject taskJson = getTasks(testUser, DOMAIN, null);
        assertNotNull(taskJson);

        JSONArray jsonEntries = (JSONArray) taskJson.get("entries");
        assertNotNull(jsonEntries);
        assertTrue(jsonEntries.size() > 0);
    }

    @Test()
    public void ALF_1085() throws Exception
    {
        try
        {
            getTasks(testUser, invalidDomain, null);

            fail("ALF_1085: Invalid domain - " + invalidDomain);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

    @Test(dependsOnMethods = "ALF_1096")
    public void ALF_1086() throws Exception
    {
        JSONObject taskJson = getTaskFromId(testUser, DOMAIN.toUpperCase(), taskId);
        assertNotNull(taskJson);
        assertTrue(taskJson.get("id").equals(taskId), "The id should be - " + taskId);
        if (ShareUser.isAlfrescoVersionCloud(drone))
        {
            assertTrue(taskJson.get("assignee").equals(testUser.toLowerCase()), "The assignee should be - " + testUser);
        }
        else
        {
            assertTrue(taskJson.get("assignee").equals(testUser), "The assignee should be - " + testUser);
        }
        assertTrue(taskJson.get("description").toString().equalsIgnoreCase(updatedTaskName), "The description should be - " + siteName + " but was found to be - "
                + taskJson.get("description").toString());
    }

    @Test()
    public void ALF_1087() throws Exception
    {
        try
        {
            getTaskFromId(testUser, invalidDomain, taskId);

            fail("ALF_1087: Invalid domain - " + invalidDomain);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

    @Test()
    public void ALF_1088() throws Exception
    {
        try
        {
            getTaskFromIdFormModel(testUserInvalid, DOMAIN, taskId);

            fail("ALF_1088: Invalid User - " + testUserInvalid);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
        catch (Exception ex)
        {
            assertTrue(ex.toString().contains("401"));

        }
    }

    @Test()
    public void ALF_1091() throws Exception
    {
        JSONObject resp = getTaskVariables(testUser, DOMAIN, taskId, null);
        assertNotNull(resp);
        JSONObject entries = (JSONObject) resp.get("list");
        JSONArray varArray = (JSONArray) entries.get("entries");
        for (int i = 0; i < varArray.size(); i++)
        {
            JSONObject entry = (JSONObject) ((JSONObject) varArray.get(i)).get("entry");
            if (entry.containsValue("bpm_workflowPriority"))
            {
                assertEquals(entry.get("value").toString(), "2", "ALF_1091: priority should be 2");
                break;
            }
        }
    }

    @Test()
    public void ALF_1090() throws Exception
    {
        try
        {
            getTaskVariables(testUser, DOMAIN + "aaa", taskId, null);

            fail("ALF_1090: Invalid domain - " + DOMAIN + "aaa");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
        catch (Exception ex)
        {
            assertTrue(ex.toString().contains("401"));

        }
    }

    @Test()
    public void ALF_1089() throws Exception
    {
        JSONObject taskJson = getTaskFromIdFormModel(testUser, DOMAIN, taskId);
        assertNotNull(taskJson);
        JSONArray jsonEntries = (JSONArray) taskJson.get("entries");
        assertNotNull(jsonEntries);
        if (jsonEntries.size() > 0)
        {
            JSONObject entry = (JSONObject) ((JSONObject) jsonEntries.get(0)).get("entry");

            assertTrue(entry.containsKey("dataType"), "ALF_1089: name should be a key in variable json");
            String value = (String) entry.get("dataType");
            assertTrue(StringUtils.isNotEmpty(value), "ALF_1089: value of dataType should not be empty");
        }
        else
        {
            fail("ALF_1089: more than one entry for task-form-model must be present.");
        }
    }

    @Test()
    public void ALF_1092() throws Exception
    {
        try
        {
            getTaskItems(testUser, invalidDomain, taskId);

            fail("ALF_1092: Invalid domain - " + invalidDomain);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

    @Test(dependsOnMethods = "ALF_1095")
    public void ALF_1093() throws Exception
    {
        JSONObject itemJson = getTaskItem(testUser, DOMAIN, taskId, docGuid);
        assertNotNull(itemJson);
        assertTrue(itemJson.get("id").equals(docGuid), "The id should be - " + docGuid);
        if (ShareUser.isAlfrescoVersionCloud(drone))
        {
            assertTrue(itemJson.get("createdBy").equals(testUser.toLowerCase()), "The createdBy should be - " + testUser);
        }
        else
        {
            assertTrue(itemJson.get("createdBy").equals(testUser), "The createdBy should be - " + testUser);
        }
    }

    // -- -- End of Get Methods -- -- //

    @Test()
    public void ALF_1097() throws Exception
    {
        try
        {
            JSONObject taskJson = new JSONObject();

            List<String> selectedFields = new ArrayList<String>();
            selectedFields.addAll(Arrays.asList(new String[] { "description" }));

            taskJson.put("description", "This is the updated description");
            taskJson.put("id", taskId);
            updateTask(testUser, invalidDomain, taskId, taskJson, selectedFields);
            fail("ALF_1097: Invalid domain - " + invalidDomain);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

    @Test()
    public void ALF_1096() throws Exception
    {
        JSONObject taskJson = new JSONObject();

        List<String> selectedFields = new ArrayList<String>();
        selectedFields.addAll(Arrays.asList(new String[] { "description" }));

        updatedTaskName = siteName + "Updated";
        taskJson.put("description", updatedTaskName);
        taskJson.put("id", taskId);
        JSONObject response = updateTask(testUser, DOMAIN, taskId, taskJson, selectedFields);
        assertNotNull(response);
        assertEquals(response.get("id").toString(), taskJson.get("id").toString());

        JSONObject returnTaskObject = getTaskFromId(testUser, DOMAIN, taskId);
        assertNotNull(returnTaskObject);
        assertEquals(returnTaskObject.get("id").toString(), taskJson.get("id").toString());
        assertTrue(taskJson.get("description").equals(updatedTaskName), "The description should be - " + updatedTaskName);

        // Confirm the details on Share UI

        ShareUser.login(drone, testUser);
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, updatedTaskName), "Task with name '" + updatedTaskName + "' should exist");
    }

    @Test()
    public void ALF_1098() throws Exception
    {
        try
        {
            JSONObject variableBody = new JSONObject();
            variableBody.put("name", "newVariable");
            variableBody.put("value", 1234);
            variableBody.put("scope", "global");
            updateTaskVariable(testUser, invalidDomain, taskId, variableBody, "newVariable");
            fail("ALF_1098: Invalid domain - " + invalidDomain);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

    // TODO Refer to JIRA for failures - https://issues.alfresco.com/jira/browse/MNT-10535
    @Test(dependsOnMethods = "ALF_1096")
    public void ALF_1099() throws Exception
    {
        // Update Workflow Priority to 1: High
        String varName = "bpm_workflowPriority";
        JSONObject variableBody = new JSONObject();
        variableBody.put("name", varName);
        variableBody.put("value", 1);
        variableBody.put("scope", "global");
        JSONObject response = updateTaskVariable(testUser, DOMAIN, taskId, variableBody, "bpm_workflowPriority");
        assertNotNull(response);

        // Check Priority is updated using Get request
        JSONObject resp = getTaskVariables(testUser, DOMAIN, taskId, null);
        assertNotNull(resp);
        JSONObject entries = (JSONObject) resp.get("list");
        JSONArray varArray = (JSONArray) entries.get("entries");
        boolean gotIt = false;
        for (int i = 0; i < varArray.size(); i++)
        {
            JSONObject entry = (JSONObject) ((JSONObject) varArray.get(i)).get("entry");
            if (entry.get("name").toString().equalsIgnoreCase(varName) && entry.containsKey("value"))
            {
                assertEquals(entry.get("value").toString(), "1", "Target entry did not get updated - " + entry.toJSONString());
                gotIt = true;
                break;
            }
        }

        assertTrue(gotIt, "ALF_1099: variable not correctly updated: " + varName);

        // Confirm the details on Share UI
        ShareUser.login(drone, testUser);
        if (ShareUser.isAlfrescoVersionCloud(drone))
        {
            TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(drone, updatedTaskName);
            String taskPriority = taskHistoryPage.getWorkFlowDetailsGeneralInfo().getPriority().getValue();
            assertEquals(taskPriority, "1");
        }
        else
        {
            WorkFlowDetailsPage detailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, siteName);
            String priority = detailsPage.getWorkFlowDetailsGeneralInfo().getPriority().getValue();
            assertEquals(detailsPage.getWorkFlowDetailsGeneralInfo().getPriority().getValue(), "1", "ALF_1099: Priority not updated correctly: found - "
                    + priority);
        }
    }

    @SuppressWarnings("unchecked")
    @Test()
    public void ALF_1094() throws Exception
    {
        JSONObject item = new JSONObject();
        item.put("id", docGuid);
        try
        {
            createItemsForTask(testUser, invalidDomain, taskId, item);

            fail("ALF_10964: Invalid domain - " + invalidDomain);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

    @SuppressWarnings("unchecked")
    @Test()
    public void ALF_1095() throws Exception
    {
        JSONObject item = new JSONObject();
        item.put("id", docGuid);
        JSONObject taskJson = createItemsForTask(testUser, DOMAIN, taskId, item);
        assertNotNull(taskJson);
        logger.info("Received JSON ALF_1095 - " + taskJson);
        assertEquals(((JSONObject) taskJson.get("entry")).get("id"), docGuid, "Received JSON - " + taskJson);
    }

    @Test()
    public void ALF_1100() throws Exception
    {
        try
        {
            deleteTaskItems(testUserInvalid, DOMAIN, taskId, docGuid);

            fail("ALF_1100: Invalid User - " + testUserInvalid);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 401);
        }
    }

    @Test(dependsOnMethods = "ALF_1093")
    public void ALF_1101() throws Exception
    {
        HttpResponse taskJson = deleteTaskItems(testUser, DOMAIN, taskId, docGuid);
        assertNotNull(taskJson);
        assertEquals(taskJson.getStatusCode(), 204);

        try
        {
            getTaskItem(testUser, DOMAIN, taskId, docGuid);
            fail("ALF_1101: The item should be deleted.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }

        // Confirm the details on Share UI
        ShareUser.login(drone, testUser);
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, updatedTaskName);
        assertTrue(taskDetailsPage.getTaskItem(fileName).size() == 0);
    }

    /**
     * @param siteName
     * @param cloudUser
     * @return {@link WorkFlowFormDetails}
     */
    protected WorkFlowFormDetails createWorkFlowDetails(String siteName, String... assignee)
    {
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setSiteName(siteName);
        formDetails.setReviewers(Arrays.asList(assignee));
        formDetails.setMessage(siteName);
        return formDetails;
    }

}
