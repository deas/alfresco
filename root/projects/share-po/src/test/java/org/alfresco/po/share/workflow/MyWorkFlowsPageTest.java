/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.webdrone.exception.PageException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify MyWorkFlowsPage.
 * 
 * @author Ranjith Manyam
 * @since 1.7.1
 */
@Listeners(FailedTestListener.class)
public class MyWorkFlowsPageTest extends AbstractTest
{
    DashBoardPage dashBoardPage;
    MyWorkFlowsPage myWorkFlowsPage;
    WorkFlowDetailsPage workFlowDetailsPage;
    MyTasksPage myTasksPage;
    EditTaskPage editTaskPage;

    String workFlow1;
    String workFlow2;
    String workFlow3;
    String dueDate;
    String workFlowComment;
    String uname;

    /**
     * Pre test to create a site and document content with properties set and navigate to StartWorkFlow  page.
     *
     * @throws Exception
     */
    @BeforeClass(groups = "Enterprise4.2")
    public void prepare() throws Exception
    {
        uname = "workflow" + System.currentTimeMillis();
        createEnterpriseUser(uname);
        dashBoardPage = loginAs(uname, UNAME_PASSWORD);
        workFlow1 = "MyWF-" + System.currentTimeMillis() + "-1";
        workFlow2 = "MyWF-" + System.currentTimeMillis() + "-2";
        workFlow3 = "MyWF-" + System.currentTimeMillis() + "-3";
        dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        workFlowComment = System.currentTimeMillis() + "-Comment";
    }

    @AfterClass
    public void afterClass()
    {
        SharePage sharePage = drone.getCurrentPage().render();

        List<String> workFlowList = Arrays.asList(workFlow1, workFlow3);

        for(String workFlow: workFlowList)
        {
            myWorkFlowsPage = sharePage.getNav().selectWorkFlowsIHaveStarted().render();
            if(myWorkFlowsPage.isWorkFlowPresent(workFlow))
            {
                myWorkFlowsPage.cancelWorkFlow(workFlow);
            }
            myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
            if(myWorkFlowsPage.isWorkFlowPresent(workFlow))
            {
                myWorkFlowsPage.deleteWorkFlow(workFlow);
            }
        }
    }

    private WorkFlowFormDetails getFormDetails(String workFlowName)
    {
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        List<String> userNames = new ArrayList<String>();
        userNames.add(uname);
        formDetails.setReviewers(userNames);
        return formDetails;
    }

    /**
     * This test is to select WorkFlows I've Started from
     * Navigation bar and verify the title.
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2")
    public void selectWorkFlowsIHaveStarted() throws Exception
    {
        myWorkFlowsPage = dashBoardPage.getNav().selectWorkFlowsIHaveStarted().render();

        Assert.assertTrue(myWorkFlowsPage.isTitlePresent());
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectWorkFlowsIHaveStarted")
    public void selectActiveWorkFlows()
    {
        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();
        Assert.assertEquals(myWorkFlowsPage.getSubTitle(), "Active Workflows");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectActiveWorkFlows")
    public void selectCompletedWorkFlows()
    {
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
        Assert.assertEquals(myWorkFlowsPage.getSubTitle(), "Completed Workflows");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectCompletedWorkFlows")
    public void selectStartWorkflowButton() throws InterruptedException 
    {
        StartWorkFlowPage startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        NewWorkflowPage workFlow = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();
        WorkFlowFormDetails formDetails = getFormDetails(workFlow1);
        myWorkFlowsPage = workFlow.startWorkflow(formDetails).render();
        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();
        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlow1));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectStartWorkflowButton")
    public void getWorkFlowDetails()
    {
        List<WorkFlowDetails> workFlowDetailsList = myWorkFlowsPage.getWorkFlowDetails(workFlow1);
        Assert.assertEquals(workFlowDetailsList.size(), 1, "Verifying there is only one workflow that matches the workflow name");
        Assert.assertEquals(workFlowDetailsList.get(0).getWorkFlowName(), workFlow1);
        Assert.assertEquals(workFlowDetailsList.get(0).getDue(), DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(dueDate));
        Assert.assertEquals(workFlowDetailsList.get(0).getStartDate().toLocalDate(), new DateTime().toLocalDate());
        Assert.assertNull(workFlowDetailsList.get(0).getEndDate());
        Assert.assertEquals(workFlowDetailsList.get(0).getType(), WorkFlowType.NEW_WORKFLOW);
        Assert.assertEquals(workFlowDetailsList.get(0).getDescription(), WorkFlowDescription.ASSIGN_NEW_TASK_TO_YOUR_SELF_OR_COLLEAGUE);
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getWorkFlowDetails", expectedExceptions = IllegalArgumentException.class)
    public void selectWorkFlowWithNullData()
    {
        myWorkFlowsPage.selectWorkFlow(null);
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectWorkFlowWithNullData", expectedExceptions = PageException.class)
    public void selectWorkFlowWithIncorrectData()
    {
        myWorkFlowsPage.selectWorkFlow(String.valueOf(System.currentTimeMillis()));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectWorkFlowWithIncorrectData")
    public void selectWorkFlowWithValidData()
    {
        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlow1).render();
        Assert.assertTrue(workFlowDetailsPage.isTitlePresent());
        Assert.assertEquals(workFlowDetailsPage.getWorkFlowStatus(), "Workflow is in Progress");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectWorkFlowWithValidData")
    public void getWorkFlowDetailsHeader()
    {
        String workFlowDetailsHeader = "Details: " + workFlow1 + " (Task)";
        Assert.assertEquals(workFlowDetailsPage.getPageHeader(), workFlowDetailsHeader);
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getWorkFlowDetailsHeader")
    public void verifyTaskDetails()
    {
        myTasksPage = workFlowDetailsPage.getNav().selectMyTasks().render();
        Assert.assertEquals(myTasksPage.getSubTitle(), "Active Tasks");
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlow1);

        Assert.assertEquals(taskDetails.getTaskName(), workFlow1);
        Assert.assertEquals(taskDetails.getDue(), getDueDateOnMyTaskPage(dueDate));
        Assert.assertEquals(taskDetails.getStartDate().toLocalDate(), new DateTime().toLocalDate());
        Assert.assertNull(taskDetails.getEndDate());
        Assert.assertEquals(taskDetails.getStatus(), "Not Yet Started");
        Assert.assertEquals(taskDetails.getType(), TaskDetailsType.TASK);
        Assert.assertEquals(taskDetails.getDescription(), "Task allocated by colleague");
        Assert.assertEquals(taskDetails.getStartedBy(), uname + "@test.com");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "verifyTaskDetails")
    public void selectEditTask()
    {
        myWorkFlowsPage = myWorkFlowsPage.getNav().selectWorkFlowsIHaveStarted().render();
        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlow1).render();
        editTaskPage = workFlowDetailsPage.getCurrentTasksList().get(0).getEditTaskLink().click().render();
        Assert.assertTrue(editTaskPage.isBrowserTitle("Edit Task"));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectEditTask")
    public void completeWorkFlow()
    {
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.enterComment(workFlowComment);
        workFlowDetailsPage = editTaskPage.selectTaskDoneButton().render();
        Assert.assertTrue(workFlowDetailsPage.isTitlePresent());
        Assert.assertEquals(workFlowDetailsPage.getWorkFlowStatus(), "Workflow is in Progress");

        editTaskPage = workFlowDetailsPage.getCurrentTasksList().get(0).getEditTaskLink().click().render();
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.enterComment(workFlowComment);
        workFlowDetailsPage = editTaskPage.selectTaskDoneButton().render();
        Assert.assertTrue(workFlowDetailsPage.isTitlePresent());
        Assert.assertEquals(workFlowDetailsPage.getWorkFlowStatus(), "Workflow is Complete");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "completeWorkFlow")
    public void verifyCompletedWorkFlowDetails()
    {
        myWorkFlowsPage = workFlowDetailsPage.getNav().selectWorkFlowsIHaveStarted().render();
        myWorkFlowsPage.selectActiveWorkFlows().render();
        Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlow1));

        myWorkFlowsPage.selectCompletedWorkFlows().render();
        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlow1));

        List<WorkFlowDetails> workFlowDetailsList = myWorkFlowsPage.getWorkFlowDetails(workFlow1);

        Assert.assertEquals(workFlowDetailsList.get(0).getWorkFlowName(), workFlow1);
        Assert.assertEquals(workFlowDetailsList.get(0).getDue(), DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(dueDate));
        Assert.assertEquals(workFlowDetailsList.get(0).getStartDate().toLocalDate(), new DateTime().toLocalDate());
        Assert.assertEquals(workFlowDetailsList.get(0).getEndDate().toLocalDate(), new DateTime().toLocalDate());
        Assert.assertEquals(workFlowDetailsList.get(0).getType(), WorkFlowType.NEW_WORKFLOW);
        Assert.assertEquals(workFlowDetailsList.get(0).getDescription(), WorkFlowDescription.ASSIGN_NEW_TASK_TO_YOUR_SELF_OR_COLLEAGUE);
    }
    @Test(groups = "Enterprise4.2", dependsOnMethods = "verifyCompletedWorkFlowDetails")
    public void selectCancelWorkFlow() throws Exception
    {
        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();
        StartWorkFlowPage startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        NewWorkflowPage workFlow = (NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW);
        workFlow.render();

        WorkFlowFormDetails formDetails = getFormDetails(workFlow2);
        myWorkFlowsPage = workFlow.startWorkflow(formDetails).render();

        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();
        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlow2));

        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlow2).render();
        
        Assert.assertTrue(workFlowDetailsPage.isCancelTaskOrWorkFlowButtonDisplayed());
        myTasksPage = workFlowDetailsPage.selectCancelWorkFlow().render();
        myWorkFlowsPage = myTasksPage.getNav().selectWorkFlowsIHaveStarted().render();
        Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlow2));
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
        Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlow2));
    }

    private String getDueDateOnMyTaskPage(String dueDateString)
    {
        DateTime date = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(dueDateString);
        return date.toString(DateTimeFormat.forPattern("dd MMMM, yyyy"));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectCancelWorkFlow")
    public void selectStartWorkflowButtonWithDueDateNone() throws InterruptedException
    {
        dueDate = "";
        StartWorkFlowPage startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        NewWorkflowPage workFlow = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();
        WorkFlowFormDetails formDetails = getFormDetails(workFlow3);
        myWorkFlowsPage = workFlow.startWorkflow(formDetails).render();
        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();
        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlow3));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectStartWorkflowButtonWithDueDateNone")
    public void verifyTaskDetailsWithDueDateNone()
    {
        myTasksPage = workFlowDetailsPage.getNav().selectMyTasks().render();
        Assert.assertEquals(myTasksPage.getSubTitle(), "Active Tasks");
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlow3);

        Assert.assertEquals(taskDetails.getTaskName(), workFlow3);
        Assert.assertEquals(taskDetails.getDue(), "(None)");
        Assert.assertEquals(taskDetails.getStartDate().toLocalDate(), new DateTime().toLocalDate());
        Assert.assertNull(taskDetails.getEndDate());
        Assert.assertEquals(taskDetails.getStatus(), "Not Yet Started");
        Assert.assertEquals(taskDetails.getType(), TaskDetailsType.TASK);
        Assert.assertEquals(taskDetails.getDescription(), "Task allocated by colleague");
        Assert.assertEquals(taskDetails.getStartedBy(), uname + "@test.com");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "verifyTaskDetailsWithDueDateNone")
    public void verifyTaskButtonsPresent()
    {
        myTasksPage = workFlowDetailsPage.getNav().selectMyTasks().render();
        Assert.assertEquals(myTasksPage.getSubTitle(), "Active Tasks");
        myTasksPage.renderTask(3000, workFlow3);

        Assert.assertTrue(myTasksPage.isTaskEditButtonEnabled(workFlow3), "Task Edit button is not present.");
        Assert.assertTrue(myTasksPage.isTaskViewButtonEnabled(workFlow3), "Task View button is not present.");
        Assert.assertTrue(myTasksPage.isTaskWorkflowButtonEnabled(workFlow3), "Task Workflow View button is not present.");
    }
}
