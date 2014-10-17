/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.workflow;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.site.document.TableViewDocLibTest;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * @author Jamie Allison
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class MyTasksTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(TableViewDocLibTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepSiteNotification", "AlfrescoOne" })
    public void dataPrep_AONE_14198() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String[] testAdminInfo = new String[] { testAdmin };
        String[] testUserInfo1 = new String[] { testUser1 };

        // Create Users
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testAdminInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14198() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + "1");

        String taskName = testName + "-task-" + System.currentTimeMillis();

        // Admin login
        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);

        // Create a task and assign it for user
        NewWorkflowPage newWorkFlowPage = ShareUserWorkFlow.startNewWorkFlow(drone);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setSiteName(taskName);
        formDetails.setReviewers(Arrays.asList(testUser1));
        formDetails.setMessage(taskName);
        newWorkFlowPage.startWorkflow(formDetails).render();
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
        assertFalse(myTasksPage.isTaskPresent(taskName));
        ShareUser.logout(drone);

        // Login as assignee user
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_TASKS, taskName, true), "Could not find task: " + taskName);

        // Complete the task
        MyTasksDashlet myTasksDashlet = dashBoardPage.getDashlet("tasks").render();
        myTasksDashlet = myTasksDashlet.renderTask(maxWaitTime, taskName);
        myTasksDashlet.clickOnTask(taskName);
        ShareUserWorkFlow.completeTask(drone, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        myTasksPage = myTasksPage.selectActiveTasks().render();
        assertFalse(myTasksPage.isTaskPresent(taskName), "Task " + taskName + " should not appear in active tasks list.");

        // Verify Completed Tasks filter
        myTasksPage = myTasksPage.selectCompletedTasks();
        myTasksPage = myTasksPage.renderTask(maxWaitTime, taskName);
        assertTrue(myTasksPage.isTaskPresent(taskName), "Cannot find task: " + taskName);
        ShareUser.logout(drone);

        // Admin login
        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);

        // Verify Active Tasks filter
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        myTasksPage = myTasksPage.selectActiveTasks();
        myTasksPage = myTasksPage.renderTask(maxWaitTime, taskName);
        assertTrue(myTasksPage.isTaskPresent(taskName), "Cannot find task: " + taskName);

        // Complete the task
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, taskName, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Check that the task is disappeared from Active Tasks filter and displayed in Completed Tasks
        myTasksPage = myTasksPage.selectActiveTasks();
        assertFalse(myTasksPage.isTaskPresent(taskName), "Task " + taskName + " should not appear in active tasks list.");
        myTasksPage = myTasksPage.selectCompletedTasks();
        myTasksPage = myTasksPage.renderTask(maxWaitTime, taskName);
        assertTrue(myTasksPage.isTaskPresent(taskName), "Cannot find task: " + taskName);
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteNotification", "AlfrescoOne" })
    public void dataPrep_AONE_14199() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String[] testAdminInfo = new String[] { testAdmin };
        String[] testUserInfo1 = new String[] { testUser1 };

        // Create Users
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testAdminInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14199() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + "1");

        String taskName = testName + "-task-" + System.currentTimeMillis();

        // Admin login
        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);

        // Create a task and assign it for user
        NewWorkflowPage newWorkFlowPage = ShareUserWorkFlow.startNewWorkFlow(drone);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setSiteName(taskName);
        formDetails.setReviewers(Arrays.asList(testUser1));
        formDetails.setMessage(taskName);
        newWorkFlowPage.startWorkflow(formDetails).render();
        ShareUser.logout(drone);

        // Login as assignee user
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Go to My Tasks page and verify task's information
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        myTasksPage = myTasksPage.selectActiveTasks().render();
        myTasksPage = myTasksPage.renderTask(maxWaitTime, taskName);

        TaskDetails taskDetails = myTasksPage.getTaskDetails(taskName);
        assertTrue(myTasksPage.isTaskEditButtonEnabled(taskName), "Edit Task button not displayed for task: " + taskName);
        assertTrue(myTasksPage.isTaskViewButtonEnabled(taskName), "View Task button not displayed for task: " + taskName);
        assertTrue(myTasksPage.isTaskWorkflowButtonEnabled(taskName), "View Workflow button not displayed for task: " + taskName);

        assertNotNull(taskDetails.getTaskName(), "Task Name is null for task: " + taskName);
        assertNotNull(taskDetails.getDue(), "Task Due is null for task: " + taskName);
        assertNotNull(taskDetails.getStartDate(), "Task Start Date is null for task: " + taskName);
        assertNotNull(taskDetails.getStatus(), "Task Status is null for task: " + taskName);
        assertNotNull(taskDetails.getType(), "Task Type is null for task: " + taskName);
        assertNotNull(taskDetails.getDescription(), "Task Description is null for task: " + taskName);
        assertNotNull(taskDetails.getStartedBy(), "Task Started By is null for task: " + taskName);

        // Complete the task
        ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, taskName, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
        ShareUser.logout(drone);

        // log in as assigner
        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);

        // Check Active filter on My Tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        myTasksPage = myTasksPage.selectActiveTasks().render();
        myTasksPage = myTasksPage.renderTask(maxWaitTime, taskName);
        assertTrue(myTasksPage.isTaskEditButtonEnabled(taskName), "Edit Task button not displayed for task: " + taskName);
        myTasksPage = myTasksPage.renderTask(maxWaitTime, taskName);
        assertTrue(myTasksPage.isTaskViewButtonEnabled(taskName), "View Task button not displayed for task: " + taskName);
        myTasksPage = myTasksPage.renderTask(maxWaitTime, taskName);
        assertTrue(myTasksPage.isTaskWorkflowButtonEnabled(taskName), "View Workflow button not displayed for task: " + taskName);

        ShareUser.logout(drone);
    }
}
