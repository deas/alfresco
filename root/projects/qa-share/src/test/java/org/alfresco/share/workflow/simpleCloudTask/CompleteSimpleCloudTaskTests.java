package org.alfresco.share.workflow.simpleCloudTask;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.EditTaskPage.Button;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowHistoryOutCome;
import org.alfresco.po.share.workflow.WorkFlowHistoryType;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CompleteSimpleCloudTaskTests extends AbstractWorkflow
{

    private String testDomain;
    private String opUser;
    private String cloudUser;
    private String cloudSite;
    private String opSite;
    private String fileName;
    private String folderName;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;

        opUser = getUserNameForDomain(testName + "opUser", testDomain);
        cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);

        folderName = getFolderName(testName);
        cloudSite = getSiteName(testName + "CL03");
        opSite = getSiteName(testName + "OP03");

    }

    @BeforeClass(groups = "DataPrepHybridWorkflow", dependsOnMethods = "setup")
    public void dataPrep_createUsers() throws Exception
    {

        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        folderName = getFolderName(testName);
        // fileName = getFileName(testName) + ".txt";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15613() throws Exception
    {

        String workFlowName = "Simple Cloud Task " + testName + "-15613CL";

        folderName = getFolderName(testName);
        fileName = getFileName(testName) + "-15613" + "2.txt";

        String[] fileInfo = { fileName, DOCLIB };

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.openSitesDocumentLibrary(drone, opSite).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        formDetails.setLockOnPremise(false);

        // Create Workflow using File1
        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

        @SuppressWarnings("unused")
        boolean synced = checkIfContentIsSynced(drone, fileName);
        if (synced = false)
        {
            checkIfContentIsSynced(drone, fileName);
        }
        ShareUser.logout(drone);

        SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName));
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.INPROGRESS, EditTaskAction.SAVE);

        ShareUser.logout(hybridDrone);

    }

    /** AONE-15613:Simple Cloud Task - Task Done */

    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15613() throws Exception
    {

        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String workFlowName = "Simple Cloud Task " + testName + "-15613CL";
        EditTaskPage editTaskPage;

        try
        {
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName);
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"));

            // --- Step 2 ---
            // --- Step action ---
            // Add any data into the Comment field, e.g. "test comment edited".
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.enterComment("test comment edited");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment edited");

            // --- Step 3 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'Completed'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.COMPLETED);

            // --- Step 4 ---
            // --- Step action ---
            // Click on Task Done button.
            // --- Expected results ---
            // Task is closed. It is disappeared from the Active Tasks.

            myTasksPage = editTaskPage.selectTaskDoneButton().render();
            assertTrue(myTasksPage.isBrowserTitle("My Tasks"));
            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName));

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15615() throws Exception
    {

        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String workFlowName = "Simple Cloud Task " + testName + "-15615CL";

        folderName = getFolderName(testName);
        fileName = getFileName(testName) + "-15615" + ".txt";

        String[] fileInfo = { fileName, DOCLIB };

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.openSitesDocumentLibrary(drone, opSite).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        formDetails.setLockOnPremise(false);

        // Create Workflow using File1
        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

        @SuppressWarnings("unused")
        boolean synced = checkIfContentIsSynced(drone, fileName);
        if (synced = false)
        {
            checkIfContentIsSynced(drone, fileName);
        }
        ShareUser.logout(drone);

        SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName));
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.INPROGRESS, EditTaskAction.TASK_DONE);

        ShareUser.logout(hybridDrone);

    }

    /**
     * AONE-15615:Simple Cloud Task - Task Done - Edit Task Details (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15615() throws Exception
    {

        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String workFlowName = "Simple Cloud Task " + testName + "-15615CL";
        String fileName = getFileName(testName) + "-15615" + ".txt";

        TaskDetailsPage taskDetailsPage;
        EditTaskPage editTaskPage;

        try
        {
            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();

            // --- Step 1 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render(maxWaitTime);
            editTaskPage = taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"));

            // --- Step 2 ---
            // --- Step action ---
            // Verify the available controls on Edit Task page.
            // --- Expected results ---
            // The following additional controls are present:
            // Status drop-down list
            // View More Actions button for the document
            // Task Done button
            // Save and close button
            // Cancel button

            List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();
            assertEquals(statusOptions.size(), TaskStatus.values().length);

            List<TaskItem> taskItems = editTaskPage.getTaskItem(fileName);
            assertTrue(taskItems.get(0).getViewMoreActionsLink().getDescription().contains("View More Actions"));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.TASK_DONE));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.SAVE_AND_CLOSE));

            // --- Step 3 ---
            // --- Step action ---
            // Verify the Status drop-down list.
            // --- Expected results ---
            // The following values are available:
            // Not yet started (set by default)
            // In Progress
            // On Hold
            // Canceled
            // Completed

            assertTrue(statusOptions.containsAll(getTaskStatusList()));

            // --- Step 4 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'In
            // Progress'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);

            // --- Step 5 ---
            // --- Step action ---
            // Click on Cancel button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. No data was
            // changed.

            taskDetailsPage = editTaskPage.selectCancelButton().render();
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // --- Step 6 ---
            // --- Step action ---
            // Repeat steps 1-4.
            // --- Expected results ---
            // Performed correctly.

            taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);

            // --- Step 7 ---
            // --- Step action ---
            // Click on Save and Close button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The
            // specified data was changed.

            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }

        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15616() throws Exception
    {

        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String workFlowName = "Simple Cloud Task " + testName + "-15616CL";

        folderName = getFolderName(testName);
        fileName = getFileName(testName) + "-15616" + ".txt";

        String[] fileInfo = { fileName, DOCLIB };

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.openSitesDocumentLibrary(drone, opSite).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        formDetails.setLockOnPremise(false);

        // Create Workflow using File1
        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

        @SuppressWarnings("unused")
        boolean synced = checkIfContentIsSynced(drone, fileName);
        if (synced = false)
        {
            checkIfContentIsSynced(drone, fileName);
        }
        ShareUser.logout(drone);

        SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName));
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.INPROGRESS, EditTaskAction.TASK_DONE);

        ShareUser.logout(hybridDrone);

    }

    /**
     * AONE-15616:Simple Cloud Task - Task Done - Complete (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15616() throws Exception
    {

        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String workFlowName = "Simple Cloud Task " + testName + "-15616CL";

        TaskDetailsPage taskDetailsPage;
        WorkFlowDetailsPage workflowDetailsPage;
        EditTaskPage editTaskPage;
        MyWorkFlowsPage myWorkfFlowsPage;

        try
        {
            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();

            // --- Step 1 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render(maxWaitTime);
            editTaskPage = taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"));

            // --- Step 2 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'Completed'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.COMPLETED);

            // --- Step 3 ---
            // --- Step action ---
            // Click on Task Done button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The
            // specified data was changed.

            taskDetailsPage = editTaskPage.selectTaskDoneButton().render();
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);

            // --- Step 4 ---
            // --- Step action ---
            // Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Simple Cloud Task test message", is
            // present in the Completed Tasks filter.

            ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            myTasksPage.selectCompletedTasks().render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // --- Step 5 ---
            // --- Step action ---
            // Verify Workflows I've started page.
            // --- Expected results ---
            // A completed workflow, e.g. "Simple Cloud Task test message", is
            // present in the Completed Workflows filter.

            myWorkfFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render();
            myWorkfFlowsPage.selectCompletedWorkFlows().render();
            assertTrue(myWorkfFlowsPage.isWorkFlowPresent(workFlowName));

            // --- Step 6 ---
            // --- Step action ---
            // Verify Workflow's Details page.
            // --- Expected results ---
            // The following changes are present:
            // Completed: Thu 12 Sep 2013 20:26:03 in the General Info section
            // Status: Workflow is Complete in the General Info section
            // Delete Workflow button

            workflowDetailsPage = myWorkfFlowsPage.selectWorkFlow(workFlowName).render();
            assertTrue(workflowDetailsPage.getWorkFlowDetailsGeneralInfo().getCompletedDate().isBeforeNow());
            assertEquals(workflowDetailsPage.getWorkFlowStatus(), "Workflow is Complete");
            assertTrue(workflowDetailsPage.isDeleteWorkFlowButtonDisplayed());

            // --- Step 7 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed:
            // No tasks

            assertTrue(workflowDetailsPage.isNoTasksMessageDisplayed());

            // --- Step 8 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed:
            // Verify task was completed on the cloud admin Thu 12 Sep 2013
            // 21:02:27 Task Done
            // Start a task or review on Alfresco Cloud admin Thu 12 Sep 2013
            // 17:15:07 Task Done

            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getType(), WorkFlowHistoryType.VERIFY_TASK_COMPLETED_ON_CLOUD);
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getCompletedBy(), getUserFullName(opUser));
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);

            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(1).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getCompletedBy(), getUserFullName(opUser));
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(workflowDetailsPage.getWorkFlowHistoryList().get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }

        ShareUser.logout(drone);
    }

}
