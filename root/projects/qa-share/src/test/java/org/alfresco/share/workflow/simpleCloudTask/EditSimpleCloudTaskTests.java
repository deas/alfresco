package org.alfresco.share.workflow.simpleCloudTask;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.EditTaskPage.Button;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class EditSimpleCloudTaskTests extends AbstractWorkflow
{

    private String testDomain;
    private String opUser;
    private String cloudUser;
    private String cloudSite;
    private String siteName;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;

        opUser = getUserNameForDomain(testName + "op5", testDomain);
        cloudUser = getUserNameForDomain(testName + "cl5", testDomain);
        cloudSite = getSiteName(testName + "cl5");
        siteName = getSiteName(testName + "op5");
    }

    @BeforeClass(groups = "DataPrepHybridWorkflow", dependsOnMethods = "setup")
    public void dataPrep_createUsers() throws Exception
    {

        // --- Step 1 ---
        // --- Step action ---
        // A Cloud user with standard/enterprise/partner network is created and activated, e.g. user1@network.com
        String[] opUserInfo1 = new String[] { opUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, opUserInfo1);

        // --- Step 2 ---
        // --- Step action ---
        // Cloud Sync and Hybrid Workflow functionality is enabled and correctly configured as per Enterprise40x-9342
        String[] cloudUserInfo1 = new String[] { cloudUser };

        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        // --- Step 3 ---
        // --- Step action ---
        // Any user is logged into the Share
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // --- Step 4 ---
        // --- Step action ---
        // The created user is authorised in Cloud, e.g. by user1@network.com
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        // --- Step 5 ---
        // --- Step action ---
        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();
        ShareUser.logout(drone);
    }

    private void dataPrep(String testName) throws Exception
    {

        String workFlowName = "Simple Cloud Task " + testName;

        String fileName = getFileName(testName) + "-1.txt";
        String[] fileInfo = { fileName, DOCLIB };

        // Login to User1, create the simple task
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // open site dashboard and upload a file
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
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

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15611() throws Exception
    {

        String testName = getTestName();
        dataPrep(testName);

    }

    /**
     * AONE-15611:Simple Cloud Task - Edit Task Details (Cloud)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15611() throws Exception
    {

        String testName = getTestName();
        String workFlowName = "Simple Cloud Task " + testName;
        String fileName = getFileName(testName) + "-1.txt";

        TaskDetailsPage taskDetailsPage;
        EditTaskPage editTaskPage;

        try
        {
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.
            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
            editTaskPage = taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"));

            // --- Step 2 ---
            // --- Step action ---
            // Verify the available controls on Edit Task page.
            // --- Expected results ---
            // The following additional controls are present:
            // Status drop-down list
            // View More Actions button for the document
            // Comment field
            // Task Done button
            // Save and close button
            // Cancel button
            List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();
            assertEquals(statusOptions.size(), TaskStatus.values().length);

            List<TaskItem> taskItems = editTaskPage.getTaskItem(fileName);
            assertTrue(taskItems.get(0).getViewMoreActionsLink().getDescription().contains("View More Actions"));
            assertTrue(editTaskPage.isCommentTextAreaDisplayed());
            assertTrue(editTaskPage.isButtonsDisplayed(Button.TASK_DONE));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.SAVE_AND_CLOSE));

            // --- Step 3 ---
            // --- Step action ---
            // Verify the Status drop-down list.
            // --- Expected results ---
            // The following values are available:
            // - Not yet started (set by default)
            // - In Progress
            // - On Hold
            // - Canceled
            // - Completed
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
            // Add any data into the Comment field, e.g. "test comment".
            // --- Expected results ---
            // Performed correctly.
            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");

            // --- Step 6 ---
            // --- Step action ---
            // Click on Cancel button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. No data was
            // changed.
            // Comment: (None)
            taskDetailsPage = editTaskPage.selectCancelButton().render();
            assertEquals(taskDetailsPage.getComment(), NONE);

            // --- Step 7 ---
            // --- Step action ---
            // Repeat steps 1-5.
            // --- Expected results ---
            // Performed correctly.
            taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");

            // --- Step 8 ---
            // --- Step action ---
            // Click on Save and Close button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The
            // specified data was changed.
            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
            assertEquals(taskDetailsPage.getComment(), "test comment");

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybridWorkflow")
    public void dataPrep_15612() throws Exception
    {

        String testName = getTestName();
        String workFlowName = "Simple Cloud Task " + testName;

        dataPrep(testName);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();

        ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.INPROGRESS, EditTaskAction.SAVE).render();
        ShareUser.logout(hybridDrone);

    }

    /**
     * AONE-15612:Simple Cloud Task - Edit Task Details - Items (Cloud)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15612() throws Exception
    {

        String testName = getTestName();
        String workFlowName = "Simple Cloud Task " + testName;
        // Login as Cloud user
        // ShareUser.login(hybridDrone, cloudUser);
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // --- Step 1 ---
        // --- Step action ---
        // Cloud Click on Edit button.
        // --- Expected results ---
        // The button is pressed. Edit Task page is opened.
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();
        Assert.assertTrue(editTaskPage.getTitle().contains("Edit Task"));

        // --- Step 2 ---
        // --- Step action ---
        // Try to add any item to the existing set.
        // --- Expected results ---
        // It is not possible to add any item to the task.
        Assert.assertFalse(editTaskPage.isButtonsDisplayed(Button.ADD));

        // --- Step 3 ---
        // --- Step action ---
        // Try to remove the existing items from the task.
        // --- Expected results ---
        // It is not possible to remove the item from the task.
        Assert.assertFalse(editTaskPage.isButtonsDisplayed(Button.REMOVE_ALL));

        ShareUser.logout(hybridDrone);
    }

}
