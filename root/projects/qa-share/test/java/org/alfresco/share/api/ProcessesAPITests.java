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

import java.util.Arrays;

import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.workflow.api.model.ProcessInfo;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.ProcessesAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class ProcessesAPITests extends ProcessesAPI
{

    public String testUser = "";
    public String testUserInvalid = "";
    private String processId = "0";
    private String siteName;
    private String fileName;
    private String docGuid1;
    private String docGuid2;
    private String fileName2;
    private static Log logger = LogFactory.getLog(ProcessesAPITests.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        super.beforeClass();

        testName = this.getClass().getSimpleName() + System.currentTimeMillis();
        testUser = getUserNameFreeDomain(testName).toLowerCase();
        testUserInvalid = "invalid" + testUser;
        siteName = getSiteName(testName);

        fileName = getFileName(testName);
        fileName2 = getFileName(testName + "_2");

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD).render();

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2 });

        docGuid1 = ShareUser.getGuid(drone, fileName);
        docGuid2 = ShareUser.getGuid(drone, fileName2);

        ShareUserWorkFlow.startWorkflow(drone, WorkFlowType.NEW_WORKFLOW, createWorkFlowDetails(testName, testUser));

        JSONObject processJson = getProcesses(testUser, DOMAIN, null);
        logger.info("Process JSON received - " + processJson);

        JSONObject jsonList = (JSONObject) processJson.get("list");
        JSONArray entries = (JSONArray) jsonList.get("entries");
        for (int i = 0; i < entries.size(); i++)
        {
            JSONObject entry = (JSONObject) ((JSONObject) entries.get(i)).get("entry");

            if (entry.get("startUserId").equals(testUser))
            {
                processId = (String) entry.get("id");
            }
        }
    }

    // -- -- Get Methods -- -- //

    @Test
    public void ALF_1104() throws Exception
    {
        JSONObject processJson = getProcesses(testUser, DOMAIN, null);
        
        assertNotNull(processJson);
        
        JSONObject jsonList = (JSONObject) processJson.get("list");
        
        JSONArray jsonEntries = (JSONArray) jsonList.get("entries");
        
        assertNotNull(jsonEntries);
        assertTrue(jsonEntries.size() > 0);
    }

    @Test
    public void ALF_1102() throws Exception
    {
        try
        {
            getProcesses(testUserInvalid, DOMAIN, null);

            Assert.fail("Process items should not be received as invalid user - " + testUserInvalid);
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

    @Test
    public void ALF_1106() throws Exception
    {
        ProcessInfo processInfo = getProcessById(testUser, DOMAIN, processId);
        
        assertNotNull(processInfo);
        
        assertTrue(processInfo.getId().equals(processId), "Incorrect process ID: The id should be - " + processId);
        
        assertTrue(processInfo.getStartUserId().equals(testUser), "Incorrect StartUserId");
    }

    @Test
    public void ALF_1105() throws Exception
    {
        try
        {
            getProcessById(testUser, DOMAIN, processId + "123");

            Assert.fail("Expected PublicAPIException with Response 404: For invalid process id");

        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test(dependsOnMethods = "ALF_1117")
    public void ALF_1110() throws Exception
    {
        JSONObject itemsJson = getProcessItems(testUser, DOMAIN, processId);

        assertNotNull(itemsJson);

        JSONObject response = getProcessItem(testUser, DOMAIN, processId, docGuid1);
        
        assertNotNull(response);
        
        assertEquals(docGuid1, response.get("id"));
        assertEquals(fileName, response.get("name"));

    }

    @Test
    public void ALF_1109() throws Exception
    {
        try
        {
            getProcessItems(testUser, DOMAIN, processId + "123");

            Assert.fail("Process items should not be received as invalid id.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ALF_1115() throws Exception
    {
        String varName = "newVariable" + System.currentTimeMillis();
        
        JSONObject variableBody = new JSONObject();
        variableBody.put("name", varName);
        variableBody.put("value", 1234);
        
        JSONArray variableArray = new JSONArray();
        variableArray.add(variableBody);
        
        createProcessVariables(testUser, DOMAIN, processId, variableArray);
        
        // Check using get request
        JSONObject variablesJson = getProcessVariables(testUser, DOMAIN, processId);
        assertNotNull(variablesJson);

        // Check value matches share, ie initiator
        JSONArray entries = (JSONArray) variablesJson.get("entries");
        assertNotNull(entries);

        for (int i = 0; i < entries.size(); i++)
        {
            JSONObject entry = (JSONObject) ((JSONObject) entries.get(i)).get("entry");
            if (entry.get("name").equals("workflowinstanceid"))
            {
                assertEquals(entry.get("value"), ("activiti$" + processId));
            }
            if (entry.get("name").equals("bpm_assignee"))
            {
                assertEquals(entry.get("value"), testUser);
            }
            if (entry.get("name").equals(varName))
            {
                assertEquals(entry.get("value").toString(), "1234");
            }
        }
    }

    @Test
    public void ALF_1116() throws Exception
    {
        try
        {
            getProcessVariables(testUser, DOMAIN, processId + "123");

            Assert.fail("Expected Exception with Response: 404. Process variables should not be received as invalid id.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void ALF_1108() throws Exception
    {
        JSONObject activitiesJson = getProcessActivities(testUser, DOMAIN, processId, null);
        assertNotNull(activitiesJson);

        // Check the values are appropriate
        // TODO: Implement / uncomment list response on resolution of issue: ALF-20612 
        // JSONObject jsonList = (JSONObject) activitiesJson.get("list");
        JSONArray entries = (JSONArray) activitiesJson.get("entries");
        for (int i = 0; i < entries.size(); i++)
        {
            JSONObject entry = (JSONObject) ((JSONObject) entries.get(i)).get("entry");

            assertNotNull(entry.get("id"));
            assertNotNull(entry.get("startedAt"));
            assertNotNull(entry.get("activityDefinitionId"));
        }
    }

    @Test
    public void ALF_1107() throws Exception
    {
        try
        {
            getProcessActivities(testUser, DOMAIN, processId + "1234", null);

            Assert.fail("Process activities should not be received as invalid id.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void ALF_1111() throws Exception
    {
        JSONObject tasksJson = getProcessTasks(testUser, DOMAIN, processId, null);
        assertNotNull(tasksJson);

        // Check Process id, assignee, priority consistent with Share UI
        JSONArray entries = (JSONArray) tasksJson.get("entries");
        assertNotNull(entries);

        for (int i = 0; i < entries.size(); i++)
        {
            JSONObject entry = (JSONObject) ((JSONObject) entries.get(0)).get("entry");

            if (entry.get("processId").equals(processId))
            {
                assertEquals(entry.get("assignee"), testUser);
                assertEquals(entry.get("description"), testName);
            }
        }
    }

    @Test
    public void ALF_1112() throws Exception
    {
        try
        {
            getProcessTasks(testUser, DOMAIN, processId + "1234", null);

            Assert.fail("Expected Exception with response 404: Process tasks should not be received as invalid id.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void ALF_1114() throws Exception
    {
        HttpResponse response = getProcessImage(testUser, DOMAIN, processId, null);
        assertNotNull(response);
        assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void ALF_1113() throws Exception
    {
        try
        {
            getProcessImage(testUser, DOMAIN, processId + "1234", null);

            Assert.fail("Expected Exception with response 404: Process Image should not be received as invalid id.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test(dependsOnMethods = "ALF_1360")
    public void ALF_1362() throws Exception
    {
        HttpResponse resp = deleteProcess(testUser, DOMAIN, processId);
        assertNotNull(resp);
        assertEquals(resp.getStatusCode(), 204);

        // Check using Get request: getProcessById: returns appropriate process
        // info. not 404
        // When Process is deleted its marked as Completed, not completely
        // deleted from history
        ProcessInfo pinfo = getProcessById(testUser, DOMAIN, processId);
        assertTrue(pinfo.isCompleted());
        assertNotNull(pinfo.getEndedAt());
        assertEquals(pinfo.getDeleteReason(), "deleted through REST API call");
    }

    @Test(enabled = true)
    public void ALF_1363() throws Exception
    {
        try
        {
            deleteProcess(testUser, DOMAIN, processId + "1234");

            Assert.fail("Process should not be removed as invalid id.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test(dependsOnMethods = "ALF_1110")
    public void ALF_1360() throws Exception
    {
        HttpResponse resp = deleteProcessItem(testUser, DOMAIN, processId, docGuid1);
        assertNotNull(resp);

        try
        {
            JSONObject item = getProcessItem(testUser, DOMAIN, processId, docGuid1);
            Assert.fail("Expected response 404: for a deleted process item: Process id: " + processId + " Item Id: " + docGuid1);
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
        // Confirm on Share UI?
        ShareUser.login(drone, testUser);
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, testName);
        assertTrue(taskDetailsPage.getTaskItem(fileName).size() == 0);
    }

    @Test
    public void ALF_1361() throws Exception
    {
        try
        {
            deleteProcessItem(testUser, DOMAIN, processId + "123", docGuid1);

            Assert.fail("Process item should not be removed as invalid process id.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ALF_1359() throws Exception
    {
        try
        {
            JSONObject variableBody = new JSONObject();
            variableBody.put("name", "newVariable");
            variableBody.put("value", 1234);
            updateProcessVariables(testUser, DOMAIN, processId + "123", "newVariable", variableBody);

            Assert.fail("Expected Response 404. Process variable should not be updated as invalid process id.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ALF_1120() throws Exception
    {
        JSONObject variableBody = new JSONObject();
        variableBody.put("name", "newVariable");
        variableBody.put("value", 1234);
        JSONArray variableArray = new JSONArray();
        variableArray.add(variableBody);
        createProcessVariables(testUser, DOMAIN, processId, variableArray);

        variableBody.put("value", 4321);
        JSONObject response = updateProcessVariables(testUser, DOMAIN, processId, "newVariable", variableBody);
        assertNotNull(response);

        // Check new Value using Get
        response = getProcessVariables(testUser, DOMAIN, processId);
        JSONArray entries = (JSONArray) response.get("entries");
        for (int i = 0; i < entries.size(); i++)
        {
            JSONObject entry = (JSONObject) ((JSONObject) entries.get(i)).get("entry");

            if (entry.get("name").equals("newVariable"))
            {
                entry.get("name").toString().equals("1234");
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ALF_1358() throws Exception
    {
        try
        {
            JSONObject processJson = new JSONObject();
            processJson.put("id", processId);
            processesClient.update("processes", processId, null, null, processJson.toJSONString(), "Failed to update variable");

            Assert.fail("Process update is not available. PUT method is invalid for this resource");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 405);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ALF_1355() throws Exception
    {
        JSONObject variableBody = new JSONObject();
        variableBody.put("name", "newVariable");
        variableBody.put("value", 1234);
        JSONArray variableArray = new JSONArray();
        variableArray.add(variableBody);
        JSONObject response = createProcessVariables(testUser, DOMAIN, processId, variableArray);
        assertNotNull(response);

        // Check variable using Get
        response = getProcessVariables(testUser, DOMAIN, processId);
        JSONArray entries = (JSONArray) response.get("entries");

        for (int i = 0; i < entries.size(); i++)
        {
            JSONObject entry = (JSONObject) ((JSONObject) entries.get(i)).get("entry");

            if (entry.get("name").equals("newVariable"))
            {
                entry.get("name").toString().equals("1234");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ALF_1357() throws Exception
    {
        try
        {
            JSONObject variableBody = new JSONObject();
            variableBody.put("name", "newVariable");
            variableBody.put("value", 1234);
            JSONArray variableArray = new JSONArray();
            variableArray.add(variableBody);
            createProcessVariables(testUser, DOMAIN, processId + "123", variableArray);
            Assert.fail("Expected Exception with Response 404. Variable should not be added as invalid process id.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ALF_1117() throws Exception
    {
        JSONObject item = new JSONObject();
        item.put("id", docGuid1);
        HttpResponse response = createProcessItems(testUser, DOMAIN, processId, item);
        assertNotNull(response);

        ShareUser.login(drone, testUser);
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, testName);
        assertTrue(taskDetailsPage.getTaskItem(fileName).size() > 0);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ALF_1118() throws Exception
    {
        try
        {
            JSONObject item = new JSONObject();
            item.put("id", docGuid1);
            createProcessItems(testUser, DOMAIN, processId, item);
            createProcessItems(testUser, DOMAIN, processId + "123", item);
            Assert.fail("Excepted Exception with Response: 404. Process items should not be added as invalid process id.");
        }
        catch (PublicApiException e)
        {
            assertEquals(e.getHttpResponse().getStatusCode(), 404);
        }
    }

    @Test
    public void getProcessByIdInvalidDomain() throws Exception
    {
        try
        {
            getProcessById(testUser, DOMAIN + "yyy", processId);

            Assert.fail("Process items should not be received as invalid domain - " + DOMAIN + "yyy");
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

    /**
     * @param siteName
     * @param cloudUser
     * @return {@link WorkFlowFormDetails}
     */
    public WorkFlowFormDetails createWorkFlowDetails(String siteName, String... assignee)
    {
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setSiteName(siteName);
        formDetails.setReviewers(Arrays.asList(assignee));
        formDetails.setMessage(siteName);
        return formDetails;
    }

}
