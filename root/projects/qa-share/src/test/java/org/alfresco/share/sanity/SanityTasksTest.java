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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.sanity;

import org.alfresco.po.share.*;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.task.*;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserAdmin;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.alfresco.po.share.NewGroupPage.ActionButton.CREATE_GROUP;
import static org.alfresco.po.share.task.AssignFilter.ME;
import static org.alfresco.po.share.task.AssignFilter.UNASSIGNED;
import static org.alfresco.po.share.task.EditTaskPage.Button.*;
import static org.alfresco.po.share.task.TaskStatus.CANCELLED;
import static org.alfresco.po.share.task.TaskStatus.COMPLETED;
import static org.alfresco.po.share.workflow.DueFilters.*;
import static org.alfresco.po.share.workflow.Priority.*;
import static org.alfresco.po.share.workflow.StartedFilter.*;
import static org.alfresco.po.share.workflow.WorkFlowType.*;
import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class SanityTasksTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SanityTasksTest.class);

    private final static String DATE_FORMAT = "dd/MM/yyyy";
    private static String baseId;

    private List<WorkFlowFormDetails> createdWorkFlows = new ArrayList<>();
    private WorkFlowFormDetails newWorkFlow;
    private WorkFlowFormDetails reviewAndApproveWorkFlow;
    private WorkFlowFormDetails sendDocsForReviewWorkFlow;
    private WorkFlowFormDetails groupReviewAndApproveWorkFlow;
    private WorkFlowFormDetails pooledReviewAndApproveWorkFlow;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        setupWorkFlowsData();
    }

    private void setupWorkFlowsData()
    {
        baseId = getTestName("TEST_T2014");
        String user = getUserNameFreeDomain(baseId);
        String user1 = user + 1;
        String user2 = user + 2;
        String user3 = user + 3;
        String user4 = user + 4;

        String group = getGroupName(baseId);
        String group1 = group + 1;
        String group2 = group + 2;

        newWorkFlow = new WorkFlowFormDetails();
        DateTime date = new DateTime();
        String dateToday = date.toString(DATE_FORMAT);
        newWorkFlow.setDueDate(dateToday);
        newWorkFlow.setTaskPriority(HIGH);
        newWorkFlow.setReviewers(Arrays.asList(user1));
        newWorkFlow.setMessage(NEW_WORKFLOW.getTitle());
        createdWorkFlows.add(newWorkFlow);

        reviewAndApproveWorkFlow = new WorkFlowFormDetails();
        String dateTomorrow = date.plusDays(1).toString(DATE_FORMAT);
        reviewAndApproveWorkFlow.setDueDate(dateTomorrow);
        reviewAndApproveWorkFlow.setTaskPriority(MEDIUM);
        reviewAndApproveWorkFlow.setReviewers(Arrays.asList(user2));
        reviewAndApproveWorkFlow.setMessage(REVIEW_AND_APPROVE.getTitle());
        createdWorkFlows.add(reviewAndApproveWorkFlow);

        sendDocsForReviewWorkFlow = new WorkFlowFormDetails();
        String dateAfter7 = date.plusDays(7).toString(DATE_FORMAT);
        sendDocsForReviewWorkFlow.setDueDate(dateAfter7);
        sendDocsForReviewWorkFlow.setTaskPriority(LOW);
        sendDocsForReviewWorkFlow.setReviewers(Arrays.asList(user3, user4));
        sendDocsForReviewWorkFlow.setMessage(SEND_DOCS_FOR_REVIEW.getTitle());
        sendDocsForReviewWorkFlow.setApprovalPercentage(80);
        createdWorkFlows.add(sendDocsForReviewWorkFlow);

        groupReviewAndApproveWorkFlow = new WorkFlowFormDetails();
        String yesterday = date.minusDays(1).toString(DATE_FORMAT);
        groupReviewAndApproveWorkFlow.setDueDate(yesterday);
        groupReviewAndApproveWorkFlow.setTaskPriority(HIGH);
        groupReviewAndApproveWorkFlow.setReviewers(Arrays.asList(group1));
        groupReviewAndApproveWorkFlow.setMessage(GROUP_REVIEW_AND_APPROVE.getTitle());
        groupReviewAndApproveWorkFlow.setApprovalPercentage(50);
        createdWorkFlows.add(groupReviewAndApproveWorkFlow);

        pooledReviewAndApproveWorkFlow = new WorkFlowFormDetails();
        pooledReviewAndApproveWorkFlow.setTaskPriority(LOW);
        pooledReviewAndApproveWorkFlow.setReviewers(Arrays.asList(group2));
        pooledReviewAndApproveWorkFlow.setMessage(POOLED_REVIEW_AND_APPROVE.getTitle());
        createdWorkFlows.add(pooledReviewAndApproveWorkFlow);
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8282() throws Exception
    {
        String siteName = getSiteName(baseId);

        String user = getUserNameFreeDomain(baseId);
        String user1 = user + 1;
        String user2 = user + 2;
        String user3 = user + 3;
        String user4 = user + 4;
        String user5 = user + 5;

        String group = getGroupName(baseId);
        String group1 = group + 1;
        String group2 = group + 2;

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserAdmin.navigateToGroup(drone);
        GroupsPage groupsPage = ShareUserAdmin.browseGroups(drone);
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage();
        groupsPage = (GroupsPage) newGroupPage.createGroup(group1, group1, CREATE_GROUP);
        newGroupPage = groupsPage.navigateToNewGroupPage();
        newGroupPage.createGroup(group2, group2, CREATE_GROUP);
        Thread.sleep(60000);

        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, group1, user1);
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, group1, user2);
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, group1, user3);

        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, group2, user4);
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, group2, user5);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        for (int i = 0; i < 2; i++)
        {
            String[] fileInfo = { baseId + i };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
    }

    @Test(groups = "Sanity")
    public void AONE_8282() throws Exception
    {
        String user1 = getUserNameFreeDomain(baseId) + 1;

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertTrue(drone.getCurrentPage().render() instanceof MyTasksPage, "MyTaskPage don't open.");
        StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton();

        NewWorkflowPage workFlow = (NewWorkflowPage) startWorkFlowPage.getWorkflowPage(NEW_WORKFLOW);
        workFlow.startWorkflow(newWorkFlow).render();

        MyWorkFlowsPage myWorkFlowsPage = myTasksPage.getNav().selectWorkFlowsIHaveStarted();
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(newWorkFlow.getMessage()), "Task[New Task] don't create.");
        assertTrue(drone.getCurrentPage().render() instanceof MyWorkFlowsPage, "MyWorkFlowsPage don't open.");

        startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        workFlow = (NewWorkflowPage) startWorkFlowPage.getWorkflowPage(REVIEW_AND_APPROVE);
        myWorkFlowsPage = workFlow.startWorkflow(reviewAndApproveWorkFlow).render();
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(reviewAndApproveWorkFlow.getMessage()), "Task[Review and Approve] don't create.");

        startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();
        workFlow = (NewWorkflowPage) startWorkFlowPage.getWorkflowPage(SEND_DOCS_FOR_REVIEW);
        myWorkFlowsPage = workFlow.startWorkflow(sendDocsForReviewWorkFlow).render();
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(sendDocsForReviewWorkFlow.getMessage()), "Task[Send documents for review] don't create.");

        startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();
        workFlow = (NewWorkflowPage) startWorkFlowPage.getWorkflowPage(GROUP_REVIEW_AND_APPROVE);
        myWorkFlowsPage = workFlow.startWorkflow(groupReviewAndApproveWorkFlow).render();
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(groupReviewAndApproveWorkFlow.getMessage()), "Task[Group review and approve] don't create.");

        startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();
        workFlow = (NewWorkflowPage) startWorkFlowPage.getWorkflowPage(POOLED_REVIEW_AND_APPROVE);
        myWorkFlowsPage = workFlow.startWorkflow(pooledReviewAndApproveWorkFlow).render();
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(pooledReviewAndApproveWorkFlow.getMessage()), "Task[Pooled review and approve] don't create.");

        myWorkFlowsPage = myWorkFlowsPage.getNav().selectWorkFlowsIHaveStarted();
        assertTrue(drone.getCurrentPage().render() instanceof MyWorkFlowsPage, "MyWorkFlowsPage don't open.");

        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();
        for (WorkFlowFormDetails createdWorkFlow : createdWorkFlows)
        {
            List<WorkFlowDetails> workFlowDetailsList = myWorkFlowsPage.getWorkFlowDetails(createdWorkFlow.getMessage());
            WorkFlowDetails workFlowDetails = workFlowDetailsList.get(0);
            assertTrue(workFlowDetails.isCancelWorkFlowDisplayed(), String.format("CancelWorkFlow button for task[%s] don't displayed.", createdWorkFlow.getMessage()));
            assertTrue(workFlowDetails.isViewHistoryDisplayed(), String.format("HistoryView button for task[%s] don't displayed.", createdWorkFlow.getMessage()));
            assertEquals(createdWorkFlow.getTaskPriority(), workFlowDetails.getPriority(), String.format("Priority for task[%s] wrong!", createdWorkFlow.getMessage()));
            assertNotNull(workFlowDetails.getDescription(), String.format("Description for task[%s] don't displayed.", createdWorkFlow.getMessage()));
            assertNotNull(workFlowDetails.getStartDate(), String.format("StartDate for task[%s] don't displayed.", createdWorkFlow.getMessage()));
            assertNotNull(workFlowDetails.getDueDateString(), String.format("DueDate for task[%s] don't displayed.", createdWorkFlow.getMessage()));
        }

        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 0, "Wrong completed workflow displayed.");

        WorkFlowFilters workFlowFilters = myWorkFlowsPage.getWorkFlowsFilter();

        workFlowFilters.select(TODAY);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1, "Wrong today workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(NEW_WORKFLOW.getTitle()), "Expected workflow[New Task] don't displayed.");

        workFlowFilters.select(TOMORROW);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1, "Wrong tomorrow workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(REVIEW_AND_APPROVE.getTitle()), "Expected workflow[Review and Approve] don't displayed.");

        workFlowFilters.select(NEXT_7_DAYS);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 2, "Wrong next 7 days workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(REVIEW_AND_APPROVE.getTitle()), "Expected workflow[Review and Approve] don't displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(SEND_DOCS_FOR_REVIEW.getTitle()), "Expected workflow[Send documents for review] don't displayed.");

        workFlowFilters.select(OVERDUE);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1, "Wrong overdue workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(GROUP_REVIEW_AND_APPROVE.getTitle()), "Expected workflow[Group review and approve] don't displayed.");

        workFlowFilters.select(NO_DATE);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1, "Wrong no date workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(POOLED_REVIEW_AND_APPROVE.getTitle()), "Expected workflow[Pooled review and approve] don't displayed.");

        workFlowFilters.select(LAST_14_DAYS);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 5, "Wrong last 14 days workflow displayed.");
        for (WorkFlowFormDetails createdWorkFlow : createdWorkFlows)
        {
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(createdWorkFlow.getMessage()), String.format("Expected workflow[%s] don't displayed.", createdWorkFlow.getMessage()));
        }

        workFlowFilters.select(LAST_7_DAYS);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 5, "Wrong last 7 days workflow displayed.");
        for (WorkFlowFormDetails createdWorkFlow : createdWorkFlows)
        {
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(createdWorkFlow.getMessage()), String.format("Expected workflow[%s] don't displayed.", createdWorkFlow.getMessage()));
        }

        workFlowFilters.select(LAST_28_DAYS);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 5, "Wrong last 14 days workflow displayed.");
        for (WorkFlowFormDetails createdWorkFlow : createdWorkFlows)
        {
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(createdWorkFlow.getMessage()), String.format("Expected workflow[%s] don't displayed.", createdWorkFlow.getMessage()));
        }

        workFlowFilters.select(HIGH);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 2, "Wrong HIGH workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(NEW_WORKFLOW.getTitle()), "Expected workflow[New Task] don't displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(GROUP_REVIEW_AND_APPROVE.getTitle()), "Expected workflow[Group review and approve] don't displayed.");

        workFlowFilters.select(MEDIUM);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1, "Wrong MEDIUM workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(REVIEW_AND_APPROVE.getTitle()), "Expected workflow[Review and Approve] don't displayed.");

        workFlowFilters.select(LOW);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 2, "Wrong LOW workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(SEND_DOCS_FOR_REVIEW.getTitle()), "Expected workflow[Send documents for review] don't displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(POOLED_REVIEW_AND_APPROVE.getTitle()), "Expected workflow[Pooled review and approve] don't displayed.");

        workFlowFilters.select(GROUP_REVIEW_AND_APPROVE);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1, "Wrong Group review and approve workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(GROUP_REVIEW_AND_APPROVE.getTitle()), "Expected workflow[Group review and approve] don't displayed.");

        workFlowFilters.select(NEW_WORKFLOW);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1, "Wrong New Task workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(NEW_WORKFLOW.getTitle()), "Expected workflow[New Task] don't displayed.");

        workFlowFilters.select(POOLED_REVIEW_AND_APPROVE);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1, "Wrong Pooled review and approve workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(POOLED_REVIEW_AND_APPROVE.getTitle()), "Expected workflow[Pooled review and approve] don't displayed.");

        workFlowFilters.select(REVIEW_AND_APPROVE);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1, "Wrong Review and Approve workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(REVIEW_AND_APPROVE.getTitle()), "Expected workflow[Review and Approve] don't displayed.");

        workFlowFilters.select(SEND_DOCS_FOR_REVIEW);
        assertEquals(myWorkFlowsPage.getDisplayedWorkFlowCount(), 1, "Wrong Send documents for review workflow displayed.");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(SEND_DOCS_FOR_REVIEW.getTitle()), "Expected workflow[Send documents for review] don't displayed.");
    }

    @Test(groups = "Sanity", dependsOnMethods = "AONE_8282")
    public void AONE_8283()
    {
        String user = getUserNameFreeDomain(baseId);
        String user1 = user + 1;
        String user2 = user + 2;
        String user3 = user + 3;
        String user4 = user + 4;

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        myTasksPage = myTasksPage.selectActiveTasks().render();
        List<WorkFlowFormDetails> workFlowFormDetails = Arrays.asList(groupReviewAndApproveWorkFlow, newWorkFlow);
        for (WorkFlowFormDetails details : workFlowFormDetails)
        {
            TaskDetails taskDetails = myTasksPage.getTaskDetails(details.getMessage());
            assertNotNull(taskDetails.getDue(), "Wrong due date don't display.");
            assertNotNull(taskDetails.getStartDate(), "Start Date don't display");
            assertNotNull(taskDetails.getStatus(), "Status don't display.");
            assertNotNull(taskDetails.getType(), "TaskType don't display.");

            assertTrue(taskDetails.isEditTaskDisplayed(), "EditTask button don't display.");
            assertTrue(taskDetails.isViewTaskDisplayed(), "ViewTask button don't display.");
            assertTrue(taskDetails.isViewWorkFlowDisplayed(), "WorkFlowTask button don't display.");
        }

        myTasksPage = myTasksPage.selectCompletedTasks();
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        TaskFilters taskFilters = myTasksPage.getTaskFilters();

        taskFilters.select(TODAY);
        assertEquals(myTasksPage.getTasksCount(), 1, "Wrong task count displayed on page.");
        TaskDetails taskDetails = myTasksPage.getTaskDetails(newWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", newWorkFlow.getMessage()));

        taskFilters.select(TOMORROW);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(NEXT_7_DAYS);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(NO_DATE);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(OVERDUE);
        assertEquals(myTasksPage.getTasksCount(), 1, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(groupReviewAndApproveWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", groupReviewAndApproveWorkFlow.getMessage()));

        taskFilters.select(HIGH);
        assertEquals(myTasksPage.getTasksCount(), 2, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(groupReviewAndApproveWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", groupReviewAndApproveWorkFlow.getMessage()));
        taskDetails = myTasksPage.getTaskDetails(newWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", newWorkFlow.getMessage()));

        taskFilters.select(MEDIUM);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(LOW);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(ME);
        assertEquals(myTasksPage.getTasksCount(), 2, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(groupReviewAndApproveWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", groupReviewAndApproveWorkFlow.getMessage()));
        taskDetails = myTasksPage.getTaskDetails(newWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", newWorkFlow.getMessage()));

        taskFilters.select(UNASSIGNED);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        myTasksPage = myTasksPage.selectActiveTasks();
        assertTrue(myTasksPage.isTaskEditButtonEnabled(newWorkFlow.getMessage()), String.format("Missing edit button for %s workflow", newWorkFlow.getMessage()));
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(newWorkFlow.getMessage());
        TaskInfo taskInfo = editTaskPage.getTaskDetailsInfo();
        assertEquals(taskInfo.getMessage(), newWorkFlow.getMessage(), "Wrong message on editTaskPage.");
        assertEquals(taskInfo.getDueDate().toString(DATE_FORMAT), newWorkFlow.getDueDate(), "Wrong dueDate on editTaskPage.");
        assertTrue(taskInfo.getOwner().contains(user1), "Wrong owner displayed on editTaskPage.");
        assertEquals(taskInfo.getPriority(), newWorkFlow.getTaskPriority(), "Wrong priority displayed on editTaskPage.");
        assertNotNull(taskInfo.getIdentifier(), "Wrong identifier on editTaskPage.");

        assertTrue(editTaskPage.isButtonsDisplayed(ADD), "Button ADD don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(TASK_DONE), "Button TASK DONE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(SAVE_AND_CLOSE), "Button SAVE AND CLOSE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(CANCEL), "Button CANCEL don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(REASSIGN), "Button REASSIGN don't display on editTaskPage.");
        assertNotNull(editTaskPage.getSelectedStatusFromDropDown(), "Can't get selected status from dropDown.");

        myTasksPage = editTaskPage.selectCancelButton().render();
        assertTrue(myTasksPage.isFilterTitle("Active Tasks"), "Active tasks page don't open.");

        editTaskPage = myTasksPage.navigateToEditTaskPage(groupReviewAndApproveWorkFlow.getMessage());
        taskInfo = editTaskPage.getTaskDetailsInfo();
        assertEquals(taskInfo.getMessage(), groupReviewAndApproveWorkFlow.getMessage(), "Wrong message on editTaskPage.");
        assertEquals(taskInfo.getDueDate().toString(DATE_FORMAT), groupReviewAndApproveWorkFlow.getDueDate(), "Wrong dueDate on editTaskPage.");
        assertTrue(taskInfo.getOwner().contains(user1), "Wrong owner displayed on editTaskPage.");
        assertEquals(taskInfo.getPriority(), groupReviewAndApproveWorkFlow.getTaskPriority(), "Wrong priority displayed on editTaskPage.");
        assertNotNull(taskInfo.getIdentifier(), "Wrong identifier on editTaskPage.");

        assertTrue(editTaskPage.isButtonsDisplayed(REJECT), "Button REJECT don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(APPROVE), "Button APPROVE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(SAVE_AND_CLOSE), "Button SAVE AND CLOSE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(CANCEL), "Button CANCEL don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(REASSIGN), "Button REASSIGN don't display on editTaskPage.");
        assertNotNull(editTaskPage.getSelectedStatusFromDropDown(), "Can't get selected status from dropDown.");

        myTasksPage = editTaskPage.selectCancelButton().render();
        assertTrue(myTasksPage.isFilterTitle("Active Tasks"), "Active tasks page don't open.");

        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        myTasksPage = myTasksPage.selectActiveTasks().render();
        workFlowFormDetails = Arrays.asList(groupReviewAndApproveWorkFlow, reviewAndApproveWorkFlow);
        for (WorkFlowFormDetails details : workFlowFormDetails)
        {
            taskDetails = myTasksPage.getTaskDetails(details.getMessage());
            assertNotNull(taskDetails.getDue(), "Wrong due date don't display.");
            assertNotNull(taskDetails.getStartDate(), "Start Date don't display");
            assertNotNull(taskDetails.getStatus(), "Status don't display.");
            assertNotNull(taskDetails.getType(), "TaskType don't display.");

            assertTrue(taskDetails.isEditTaskDisplayed(), "EditTask button don't display.");
            assertTrue(taskDetails.isViewTaskDisplayed(), "ViewTask button don't display.");
            assertTrue(taskDetails.isViewWorkFlowDisplayed(), "WorkFlowTask button don't display.");
        }

        myTasksPage = myTasksPage.selectCompletedTasks().render();
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters = myTasksPage.getTaskFilters();

        taskFilters.select(HIGH);
        assertEquals(myTasksPage.getTasksCount(), 1, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(groupReviewAndApproveWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", groupReviewAndApproveWorkFlow.getMessage()));

        taskFilters.select(TODAY);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(ME);
        assertEquals(myTasksPage.getTasksCount(), 2, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(groupReviewAndApproveWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", groupReviewAndApproveWorkFlow.getMessage()));
        taskDetails = myTasksPage.getTaskDetails(reviewAndApproveWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", reviewAndApproveWorkFlow.getMessage()));

        taskFilters.select(UNASSIGNED);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(OVERDUE);
        assertEquals(myTasksPage.getTasksCount(), 1, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(groupReviewAndApproveWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", groupReviewAndApproveWorkFlow.getMessage()));

        myTasksPage = myTasksPage.selectActiveTasks();
        assertTrue(myTasksPage.isTaskEditButtonEnabled(reviewAndApproveWorkFlow.getMessage()), String.format("Missing edit button for %s workflow", newWorkFlow.getMessage()));
        editTaskPage = myTasksPage.navigateToEditTaskPage(reviewAndApproveWorkFlow.getMessage());
        taskInfo = editTaskPage.getTaskDetailsInfo();
        assertEquals(taskInfo.getMessage(), reviewAndApproveWorkFlow.getMessage(), "Wrong message on editTaskPage.");
        assertEquals(taskInfo.getDueDate().toString(DATE_FORMAT), reviewAndApproveWorkFlow.getDueDate(), "Wrong dueDate on editTaskPage.");
        assertTrue(taskInfo.getOwner().contains(user2), "Wrong owner displayed on editTaskPage.");
        assertEquals(taskInfo.getPriority(), reviewAndApproveWorkFlow.getTaskPriority(), "Wrong priority displayed on editTaskPage.");
        assertNotNull(taskInfo.getIdentifier(), "Wrong identifier on editTaskPage.");

        assertTrue(editTaskPage.isButtonsDisplayed(REJECT), "Button REJECT don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(APPROVE), "Button APPROVE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(SAVE_AND_CLOSE), "Button SAVE AND CLOSE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(CANCEL), "Button CANCEL don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(REASSIGN), "Button REASSIGN don't display on editTaskPage.");
        assertNotNull(editTaskPage.getSelectedStatusFromDropDown(), "Can't get selected status from dropDown.");

        myTasksPage = editTaskPage.selectCancelButton().render();
        assertTrue(myTasksPage.isFilterTitle("Active Tasks"), "Active tasks page don't open.");
        String[] arg = new String[3];
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        myTasksPage = myTasksPage.selectActiveTasks().render();
        workFlowFormDetails = Arrays.asList(groupReviewAndApproveWorkFlow, sendDocsForReviewWorkFlow);
        for (WorkFlowFormDetails details : workFlowFormDetails)
        {
            taskDetails = myTasksPage.getTaskDetails(details.getMessage());
            assertNotNull(taskDetails.getDue(), "Wrong due date don't display.");
            assertNotNull(taskDetails.getStartDate(), "Start Date don't display");
            assertNotNull(taskDetails.getStatus(), "Status don't display.");
            assertNotNull(taskDetails.getType(), "TaskType don't display.");

            assertTrue(taskDetails.isEditTaskDisplayed(), "EditTask button don't display.");
            assertTrue(taskDetails.isViewTaskDisplayed(), "ViewTask button don't display.");
            assertTrue(taskDetails.isViewWorkFlowDisplayed(), "WorkFlowTask button don't display.");
        }

        DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone);
        MyTasksDashlet myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();

        myTasksPage = myTasksDashlet.selectComplete();
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters = myTasksPage.getTaskFilters();
        taskFilters.select(OVERDUE);
        assertEquals(myTasksPage.getTasksCount(), 1, "Wrong task count displayed on page.");

        taskFilters.select(LOW);
        taskDetails = myTasksPage.getTaskDetails(sendDocsForReviewWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", sendDocsForReviewWorkFlow.getMessage()));
        editTaskPage = myTasksPage.navigateToEditTaskPage(sendDocsForReviewWorkFlow.getMessage());
        taskInfo = editTaskPage.getTaskDetailsInfo();
        assertEquals(taskInfo.getMessage(), sendDocsForReviewWorkFlow.getMessage(), "Wrong message on editTaskPage.");
        assertEquals(taskInfo.getDueDate().toString(DATE_FORMAT), sendDocsForReviewWorkFlow.getDueDate(), "Wrong dueDate on editTaskPage.");
        assertTrue(taskInfo.getOwner().contains(user3), "Wrong owner displayed on editTaskPage.");
        assertEquals(taskInfo.getPriority(), sendDocsForReviewWorkFlow.getTaskPriority(), "Wrong priority displayed on editTaskPage.");
        assertNotNull(taskInfo.getIdentifier(), "Wrong identifier on editTaskPage.");

        assertTrue(editTaskPage.isButtonsDisplayed(REJECT), "Button REJECT don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(APPROVE), "Button APPROVE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(SAVE_AND_CLOSE), "Button SAVE AND CLOSE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(CANCEL), "Button CANCEL don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(REASSIGN), "Button REASSIGN don't display on editTaskPage.");
        assertNotNull(editTaskPage.getSelectedStatusFromDropDown(), "Can't get selected status from dropDown.");

        myTasksPage = editTaskPage.selectCancelButton().render();
        assertTrue(myTasksPage.isFilterTitle("Active Tasks"), "Active tasks page don't open.");

        ShareUser.login(drone, user4, DEFAULT_PASSWORD);
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        myTasksPage = myTasksPage.selectActiveTasks().render();
        workFlowFormDetails = Arrays.asList(sendDocsForReviewWorkFlow, pooledReviewAndApproveWorkFlow);
        for (WorkFlowFormDetails details : workFlowFormDetails)
        {
            taskDetails = myTasksPage.getTaskDetails(details.getMessage());
            assertNotNull(taskDetails.getDue(), "Wrong due date don't display.");
            assertNotNull(taskDetails.getStartDate(), "Start Date don't display");
            assertNotNull(taskDetails.getStatus(), "Status don't display.");
            assertNotNull(taskDetails.getType(), "TaskType don't display.");

            assertTrue(taskDetails.isEditTaskDisplayed(), "EditTask button don't display.");
            assertTrue(taskDetails.isViewTaskDisplayed(), "ViewTask button don't display.");
            assertTrue(taskDetails.isViewWorkFlowDisplayed(), "WorkFlowTask button don't display.");
        }

        myTasksPage = myTasksPage.selectCompletedTasks();
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters = myTasksPage.getTaskFilters();

        taskFilters.select(TODAY);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(TOMORROW);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(NEXT_7_DAYS);
        assertEquals(myTasksPage.getTasksCount(), 1, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(sendDocsForReviewWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", sendDocsForReviewWorkFlow.getMessage()));

        taskFilters.select(NO_DATE);
        assertEquals(myTasksPage.getTasksCount(), 1, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(pooledReviewAndApproveWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", pooledReviewAndApproveWorkFlow.getMessage()));

        taskFilters.select(HIGH);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(MEDIUM);
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        taskFilters.select(LOW);
        assertEquals(myTasksPage.getTasksCount(), 2, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(sendDocsForReviewWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", sendDocsForReviewWorkFlow.getMessage()));
        taskDetails = myTasksPage.getTaskDetails(pooledReviewAndApproveWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", pooledReviewAndApproveWorkFlow.getMessage()));

        taskFilters.select(ME);
        assertEquals(myTasksPage.getTasksCount(), 1, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(sendDocsForReviewWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", sendDocsForReviewWorkFlow.getMessage()));

        taskFilters.select(UNASSIGNED);
        assertEquals(myTasksPage.getTasksCount(), 1, "Wrong task count displayed on page.");
        taskDetails = myTasksPage.getTaskDetails(pooledReviewAndApproveWorkFlow.getMessage());
        assertNotNull(taskDetails, String.format("Expected task[%s] don't displayed.", pooledReviewAndApproveWorkFlow.getMessage()));

        assertTrue(myTasksPage.isTaskEditButtonEnabled(pooledReviewAndApproveWorkFlow.getMessage()), String.format("Missing edit button for %s workflow", pooledReviewAndApproveWorkFlow.getMessage()));
        editTaskPage = myTasksPage.navigateToEditTaskPage(pooledReviewAndApproveWorkFlow.getMessage());
        taskInfo = editTaskPage.getTaskDetailsInfo();
        assertEquals(taskInfo.getMessage(), pooledReviewAndApproveWorkFlow.getMessage(), "Wrong message on editTaskPage.");
        assertNull(taskInfo.getDueDate(), "Wrong dueDate on editTaskPage.");
        assertEquals(taskInfo.getOwner(), "(None)", "Wrong owner displayed on editTaskPage.");
        assertEquals(taskInfo.getPriority(), pooledReviewAndApproveWorkFlow.getTaskPriority(), "Wrong priority displayed on editTaskPage.");
        assertNotNull(taskInfo.getIdentifier(), "Wrong identifier on editTaskPage.");

        assertTrue(editTaskPage.isButtonsDisplayed(REJECT), "Button REJECT don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(APPROVE), "Button APPROVE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(SAVE_AND_CLOSE), "Button SAVE AND CLOSE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(CANCEL), "Button CANCEL don't display on editTaskPage.");
        assertFalse(editTaskPage.isButtonsDisplayed(REASSIGN), "Button REASSIGN display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(CLAIM), "Button CLAIM don't display on editTaskPage.");
        assertNotNull(editTaskPage.getSelectedStatusFromDropDown(), "Can't get selected status from dropDown.");
    }

    @Test(groups = "Sanity", dependsOnMethods = "AONE_8283")
    public void AONE_8284()
    {
        String user = getUserNameFreeDomain(baseId);
        String user1 = user + 1;
        String user2 = user + 2;
        String user3 = user + 3;
        String user4 = user + 4;
        String user5 = user + 5;

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(newWorkFlow.getMessage());
        myTasksPage = editTaskPage.selectTaskDoneButton().render();
        assertTrue(myTasksPage.isFilterTitle("Active Tasks"), "Active tasks page don't open.");
        editTaskPage = myTasksPage.navigateToEditTaskPage(newWorkFlow.getMessage());
        TaskInfo taskInfo = editTaskPage.getTaskDetailsInfo();
        assertEquals(taskInfo.getMessage(), newWorkFlow.getMessage(), "Wrong message on editTaskPage.");
        assertEquals(taskInfo.getDueDate().toString(DATE_FORMAT), newWorkFlow.getDueDate(), "Wrong dueDate on editTaskPage.");
        assertTrue(taskInfo.getOwner().contains(user1), "Wrong owner displayed on editTaskPage.");
        assertEquals(taskInfo.getPriority(), newWorkFlow.getTaskPriority(), "Wrong priority displayed on editTaskPage.");
        assertNotNull(taskInfo.getIdentifier(), "Wrong identifier on editTaskPage.");

        assertTrue(editTaskPage.isButtonsDisplayed(TASK_DONE), "Button TASK DONE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(SAVE_AND_CLOSE), "Button SAVE AND CLOSE don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(CANCEL), "Button CANCEL don't display on editTaskPage.");
        assertTrue(editTaskPage.isButtonsDisplayed(REASSIGN), "Button REASSIGN don't display on editTaskPage.");
        assertNotNull(editTaskPage.getSelectedStatusFromDropDown(), "Can't get selected status from dropDown.");

        myTasksPage = editTaskPage.selectTaskDoneButton().render();
        assertTrue(myTasksPage.isFilterTitle("Active Tasks"), "Active tasks page don't open.");
        assertEquals(myTasksPage.getTasksCount(), 1, "Wrong task count displayed on page.");

        MyWorkFlowsPage myWorkFlowsPage = myTasksPage.getNav().selectWorkFlowsIHaveStarted();
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
        List<WorkFlowDetails> workFlowDetailsList = myWorkFlowsPage.getWorkFlowDetails(newWorkFlow.getMessage());
        WorkFlowDetails workFlowDetails = workFlowDetailsList.get(0);
        assertTrue(workFlowDetails.isDeleteWorkFlowDisplayed(), String.format("DeleteWorkFlow button for task[%s] don't displayed.", newWorkFlow.getMessage()));
        assertTrue(workFlowDetails.isViewHistoryDisplayed(), String.format("HistoryView button for task[%s] don't displayed.", newWorkFlow.getMessage()));
        assertEquals(newWorkFlow.getTaskPriority(), workFlowDetails.getPriority(), String.format("Priority for task[%s] wrong!", newWorkFlow.getMessage()));
        assertNotNull(workFlowDetails.getDescription(), String.format("Description for task[%s] don't displayed.", newWorkFlow.getMessage()));
        assertNotNull(workFlowDetails.getStartDate(), String.format("StartDate for task[%s] don't displayed.", newWorkFlow.getMessage()));
        assertNotNull(workFlowDetails.getDueDateString(), String.format("DueDate for task[%s] don't displayed.", newWorkFlow.getMessage()));

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        editTaskPage = myTasksPage.navigateToEditTaskPage(groupReviewAndApproveWorkFlow.getMessage());
        editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
        myTasksPage = editTaskPage.selectApproveButton().render();
        assertEquals(myTasksPage.getTasksCount(), 0, "Wrong task count displayed on page.");

        DashBoardPage dashBoardPage = ShareUser.login(drone, user2, DEFAULT_PASSWORD).render();
        MyTasksDashlet myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        ShareLink shareLink = myTasksDashlet.selectTask(reviewAndApproveWorkFlow.getMessage());
        editTaskPage = shareLink.click().render();
        editTaskPage.selectStatusDropDown(COMPLETED);
        dashBoardPage = editTaskPage.selectRejectButton().render();

        myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        List<ShareLink> shareLinks = myTasksDashlet.getTasks();
        assertEquals(shareLinks.size(), 1, "Wrong task count displayed on dashlet");
        assertEquals(shareLinks.get(0).getDescription(), groupReviewAndApproveWorkFlow.getMessage(), String.format("Expected task[%s] don't displayed.", groupReviewAndApproveWorkFlow.getMessage()));

        shareLink = myTasksDashlet.selectTask(groupReviewAndApproveWorkFlow.getMessage());
        editTaskPage = shareLink.click().render();
        editTaskPage.selectStatusDropDown(TaskStatus.ONHOLD);
        dashBoardPage = editTaskPage.selectApproveButton().render();
        myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        shareLinks = myTasksDashlet.getTasks();
        assertEquals(shareLinks.size(), 0, "Wrong task count displayed in dashlet");

        dashBoardPage = ShareUser.login(drone, user3, DEFAULT_PASSWORD).render();
        myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        shareLink = myTasksDashlet.selectTask(sendDocsForReviewWorkFlow.getMessage());
        editTaskPage = shareLink.click().render();
        editTaskPage.selectStatusDropDown(CANCELLED);
        editTaskPage.selectRejectButton().render();

        dashBoardPage = ShareUser.login(drone, user2, DEFAULT_PASSWORD).render();
        myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        assertEquals(myTasksDashlet.getTasks().size(), 0, "Canceled workFlow still display.");

        dashBoardPage = ShareUser.login(drone, user4, DEFAULT_PASSWORD).render();
        myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        shareLink = myTasksDashlet.selectTask(pooledReviewAndApproveWorkFlow.getMessage());
        editTaskPage = shareLink.click().render();
        editTaskPage = editTaskPage.selectClaim();
        assertTrue(editTaskPage.isButtonsDisplayed(RELEASE_TO_POOL));
        editTaskPage.selectSaveButton().render();
        assertTrue(drone.getCurrentPage().render() instanceof DashBoardPage, "User Dashboard page don't opened.");

        dashBoardPage = ShareUser.login(drone, user5, DEFAULT_PASSWORD).render();
        myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        assertEquals(myTasksDashlet.getTasks().size(), 0, "WorkFlows still display for user5.");

        dashBoardPage = ShareUser.login(drone, user4, DEFAULT_PASSWORD).render();
        myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        shareLink = myTasksDashlet.selectTask(pooledReviewAndApproveWorkFlow.getMessage());
        editTaskPage = shareLink.click().render();
        editTaskPage.selectStatusDropDown(COMPLETED);
        dashBoardPage = editTaskPage.selectApproveButton().render();
        myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        assertEquals(myTasksDashlet.getTasks().size(), 0, "Pooled Review And Approve WorkFlow still display for user4.");

        ShareUser.login(drone, user1, DEFAULT_PASSWORD).render();
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertEquals(myTasksPage.getTasksCount(), 4, "Wrong task count displayed on page.");
        assertEquals(myTasksPage.getTaskCount("The document was reviewed and approved."), 2, "Review and approved task count wrong!");
        assertEquals(myTasksPage.getTaskCount("The document was reviewed and rejected."), 2, "Review and rejected task count wrong!");
    }


}
