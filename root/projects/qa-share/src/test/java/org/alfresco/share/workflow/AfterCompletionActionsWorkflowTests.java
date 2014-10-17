package org.alfresco.share.workflow;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Bogdan.Bocancea
 */

@Listeners(FailedTestListener.class)
public class AfterCompletionActionsWorkflowTests extends AbstractWorkflow
{
    private String testDomain;

    /**
     * Class includes: Tests from TestLink in Area: Workflow
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;
    }

    public void dataPrep(String testName) throws Exception
    {

        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15630() throws Exception
    {
        dataPrep(getTestName());
    }

    /**
     * AONE-15630:Keep content synced on cloud
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15630() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "1-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "1-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";

        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Specify 'Keep content synced on cloud' value in the After Completion drop-down list.
        // ---- Expected results ----
        // Performed correctly.
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

        // ---- Step 2 ----
        // ---- Step action ----
        // Create Workflow.
        // ---- Expected results ----
        // The workflow is created successfully. A new task appeared to the assignee.
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the File1 is synced");
        Assert.assertTrue(checkIfContentIsSynced(drone, simpleTaskFile), "Verifying the Sync Status is \"Synced\"");

        // Login as CloudUser User
        SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Complete task (Approve/Reject for 'Cloud Review Task', Task Done for 'Simple Cloud Task').
        // ---- Expected results ----
        // Performed correctly. The task is moved to Completed filter. A new task appears in OP.
        // Navigate to MyTasks page
        MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
        // Verify tasks are displayed in Active Tasks list
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        // Edit each task and mark them as completed
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Verify tasks are NOT displayed in Active Tasks list any more
        Assert.assertFalse(myTasksPage.isTaskPresent(simpleTaskWF));
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // ---- Step 4 ----
        // ---- Step action ---
        // OP Complete task (Task Done action).
        // ---- Expected results ----
        // Performed correctly. The task is moved to Completed filter.
        myTasksPage = sharePage.getNav().selectMyTasks().render();
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, simpleTaskWF));
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Select Completed tasks and verify the tasks are displayed
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        // ---- Step 5 ----
        // ---- Step action ---
        // OP Verify the document.
        // ---- Expected results ----
        // The document is still synced to Cloud. The Synced label and no Workflow label is present for the document on Document Library page.
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow());

        ShareUser.logout(drone);

        // Login as CloudUser User
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // ---- Step 6 ----
        // ---- Step action ---
        // Cloud Verify the document.
        // ---- Expected results ----
        // The document is still synced to Cloud. The Synced label and no Workflow label is present for the document on Document Library page.
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow());

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15631() throws Exception
    {
        dataPrep(getTestName());
    }

    /**
     * AONE-15631:Keep content on cloud and remove sync
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15631() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";

        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Specify 'Keep content on cloud and remove sync' value in the After Completion drop-down list.
        // ---- Expected results ----
        // Performed correctly.
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

        // ---- Step 2 ----
        // ---- Step action ----
        // Create Workflow.
        // ---- Expected results ----
        // The workflow is created successfully. A new task appeared to the assignee.
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the File1 is synced");
        Assert.assertTrue(checkIfContentIsSynced(drone, simpleTaskFile), "Verifying the Sync Status is \"Synced\"");

        // Login as CloudUser User
        SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Complete task (Approve/Reject for 'Cloud Review Task', Task Done for 'Simple Cloud Task').
        // ---- Expected results ----
        // Performed correctly. The task is moved to Completed filter. A new task appears in OP.
        // Navigate to MyTasks page
        MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
        // Verify tasks are displayed in Active Tasks list
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        // Edit each task and mark them as completed
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Verify tasks are NOT displayed in Active Tasks list any more
        Assert.assertFalse(myTasksPage.isTaskPresent(simpleTaskWF));
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // ---- Step 4 ----
        // ---- Step action ---
        // OP Complete task (Task Done action).
        // ---- Expected results ----
        // Performed correctly. The task is moved to Completed filter.
        myTasksPage = sharePage.getNav().selectMyTasks().render();
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, simpleTaskWF));
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Select Completed tasks and verify the tasks are displayed
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        // ---- Step 5 ----
        // ---- Step action ---
        // OP Verify the document.
        // ---- Expected results ----
        // The document is still synced to Cloud. The Synced label and no Workflow label is present for the document on Document Library page.
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow());

        ShareUser.logout(drone);

        // Login as CloudUser User
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // ---- Step 6 ----
        // ---- Step action ---
        // Cloud Verify the document.
        // ---- Expected results ----
        // The document is still synced to Cloud. The Synced label and no Workflow label is present for the document on Document Library page.
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(simpleTaskFile));
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow());

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15632() throws Exception
    {
        dataPrep(getTestName());
    }

    /**
     * AONE-15632:Delete content on cloud and remove sync
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15632() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";

        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Specify 'Delete content on cloud and remove sync' value in the After Completion drop-down list.
        // ---- Expected results ----
        // Performed correctly.
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        formDetails.setMessage(simpleTaskWF);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

        // ---- Step 2 ----
        // ---- Step action ----
        // Create Workflow.
        // ---- Expected results ----
        // The workflow is created successfully. A new task appeared to the assignee.
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the File1 is synced");
        Assert.assertTrue(checkIfContentIsSynced(drone, simpleTaskFile), "Verifying the Sync Status is \"Synced\"");

        // Login as CloudUser User
        SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // ---- Step 3 ----
        // ---- Step action ---
        // Cloud Complete task (Approve/Reject for 'Cloud Review Task', Task Done for 'Simple Cloud Task').
        // ---- Expected results ----
        // Performed correctly. The task is moved to Completed filter. A new task appears in OP.
        // Navigate to MyTasks page
        MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
        // Verify tasks are displayed in Active Tasks list
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        // Edit each task and mark them as completed
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Verify tasks are NOT displayed in Active Tasks list any more
        Assert.assertFalse(myTasksPage.isTaskPresent(simpleTaskWF));
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // ---- Step 4 ----
        // ---- Step action ---
        // OP Complete task (Task Done action).
        // ---- Expected results ----
        // Performed correctly. The task is moved to Completed filter.
        myTasksPage = sharePage.getNav().selectMyTasks().render();
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, simpleTaskWF));
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Select Completed tasks and verify the tasks are displayed
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

        // ---- Step 5 ----
        // ---- Step action ---
        // OP Verify the document.
        // ---- Expected results ----
        // The document is still synced to Cloud. The Synced label and no Workflow label is present for the document on Document Library page.
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow());

        ShareUser.logout(drone);

        // Login as CloudUser User
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // ---- Step 6 ----
        // ---- Step action ---
        // Cloud Verify the document.
        // ---- Expected results ----
        // The document is still synced to Cloud. The Synced label and no Workflow label is present for the document on Document Library page.
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(simpleTaskFile));

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15633() throws Exception
    {
        dataPrep(getTestName());
    }

    /**
     * AONE-15633:Simple Cloud Task - action execution after completion in Cloud and OP
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15633() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";

        String dueDate = getDueDateString();

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload a file
            siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

            // Select "Cloud Task or Review" from select a workflow dropdown
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile).render();

            // ---- Step 1 ----
            // ---- Step action ---
            // Specify any value in the After Completion drop-down list, e.g. 'Delete content on cloud and remove sync'.
            // ---- Expected results ----
            // Performed correctly.
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setAssignee(cloudUser);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setMessage(simpleTaskWF);
            formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

            // ---- Step 2 ----
            // ---- Step action ---
            // Create Workflow.
            // ---- Expected results ----
            // The workflow is created successfully. A new task appeared to the assignee.
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // ---- Step 3 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The Synced label and Workflow label are present for the document on Document Library page.
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the File1 is synced");
            Assert.assertTrue(checkIfContentIsSynced(drone, simpleTaskFile), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify Workflow has been created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(simpleTaskWF), "Verifying workflow exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // ---- Step 4 ----
            // ---- Step action ---
            // Cloud Verify the document
            // ---- Expected results ----
            // The Synced label and Workflow label are present for the document
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
            Assert.assertTrue(documentLibraryPage.isFileVisible(simpleTaskFile), "Verifying File1 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the File1 is synced");

            // ---- Step 5 ----
            // ---- Step action ---
            // Cloud Complete task (Task Done for 'Simple Cloud Task').
            // ---- Expected results ----
            // Performed correctly. The task is moved to Completed filter. A new task appears in OP.
            MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
            Assert.assertFalse(myTasksPage.isTaskPresent(simpleTaskWF));
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

            // ---- Step 7 ----
            // ---- Step action ---
            // Cloud Verify the document
            // ---- Expected results ----
            // The document is still synced to Cloud. The Synced label are present for the document .
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            Assert.assertTrue(documentLibraryPage.isFileVisible(simpleTaskFile), "Verifying File1 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the File1 is synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");

            ShareUser.logout(hybridDrone);

            // Login as OP user
            sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // ---- Step 6 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The document is still synced to Cloud. The Synced label and Workflow label are present for the document on Document Library page.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the document is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the document is part of a workflow");

            // ---- Step 8 ----
            // ---- Step action ---
            // OP Complete task (Task Done action)..
            // ---- Expected results ----
            // Performed correctly. The task is moved to Completed filter.
            myTasksPage = sharePage.getNav().selectMyTasks().render();
            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, simpleTaskWF));
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
            Assert.assertFalse(myTasksPage.isTaskPresent(simpleTaskWF));

            // Select Completed tasks and verify the tasks are displayed
            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

            // Navigate to Workflows I've Started page and verify tasks are not displayed under Active Workflows page
            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(simpleTaskWF));

            // Select Completed Workflows and verify workflows are displayed
            myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(simpleTaskWF));

            // ---- Step 9 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The document is not synced to Cloud. The Synced label and Workflow label are not present for the document on Document Library page.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the document is NOT synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the document is NOT part of a workflow");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // ---- Step 10 ----
            // ---- Step action ---
            // Cloud Verify the document.
            // ---- Expected results ----
            // The document is absent in Cloud.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
            Assert.assertFalse(documentLibraryPage.isFileVisible(simpleTaskFile), "Verifying File1 exists");
            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15634() throws Exception
    {
        dataPrep(getTestName());
    }

    /**
     * AONE-15634:Cloud Review Task - action execution after approval in Cloud and completion in OP
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15634() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String cloudReviewApproveFile = getFileName(testName) + ".txt";
        String[] fileInfo = { cloudReviewApproveFile, DOCLIB };

        String cloudReviewApproveWF = testName + System.currentTimeMillis() + "-WF";

        String dueDate = getDueDateString();
        int requiredApprovalPercentage = 100;

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 3 files
            siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            // ---- Step 1 ----
            // ---- Step action ---
            // Specify any value in the After Completion drop-down list, e.g. 'Delete content on cloud and remove sync'.
            // ---- Expected results ----
            // Performed correctly.
            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setMessage(cloudReviewApproveWF);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);

            // ---- Step 2 ----
            // ---- Step action ---
            // Create Workflow.
            // ---- Expected results ----
            // The workflow is created successfully. A new task appeared to the assignee.
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, cloudReviewApproveFile).render();
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // ---- Step 3 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The Synced label and Workflow label are present for the document on Document Library page.
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isCloudSynced(), "Verifying the File2 is synced");
            Assert.assertTrue(checkIfContentIsSynced(drone, cloudReviewApproveFile), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify Workflows are created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(cloudReviewApproveWF), "Verifying workflow2 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // ---- Step 4 ----
            // ---- Step action ---
            // Cloud Verify the document
            // ---- Expected results ----
            // The Synced label and Workflow label are present for the document
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
            Assert.assertTrue(documentLibraryPage.isFileVisible(cloudReviewApproveFile), "Verifying File1 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isCloudSynced(), "Verifying the File1 is synced");

            // ---- Step 5 ----
            // ---- Step action ---
            // Cloud Approve task.
            // ---- Expected results ----
            // Performed correctly. The task is moved to Completed filter. A new task appears in OP.
            MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewApproveWF));
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, cloudReviewApproveWF, TaskStatus.COMPLETED, EditTaskAction.APPROVE);

            // Verify tasks are NOT displayed in Active Tasks list any more
            Assert.assertFalse(myTasksPage.isTaskPresent(cloudReviewApproveWF));

            // Verify tasks are displayed in Completed Tasks list
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewApproveWF));

            // ---- Step 7 ----
            // ---- Step action ---
            // Cloud Verify the document.
            // ---- Expected results ----
            // The document is still synced to Cloud. The Synced label are present for the document .
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
            Assert.assertTrue(documentLibraryPage.isFileVisible(cloudReviewApproveFile), "Verifying File2 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isCloudSynced(), "Verifying the File2 is synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
            ShareUser.logout(hybridDrone);

            // Login as OP user
            sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // ---- Step 6 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The document is still synced to Cloud. The Synced label and Workflow label are present for the document on Document Library page.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isCloudSynced(), "Verifying the document is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isPartOfWorkflow(),
                    "Verifying the document is part of a workflow");

            // ---- Step 8 ----
            // ---- Step action ---
            // OP Complete task (Task Done action).
            // ---- Expected results ----
            // Performed correctly. The task is moved to Completed filter.
            myTasksPage = sharePage.getNav().selectMyTasks().render();
            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, cloudReviewApproveWF));
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, cloudReviewApproveWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
            Assert.assertFalse(myTasksPage.isTaskPresent(cloudReviewApproveWF));
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewApproveWF));

            // Navigate to Workflows I've Started page and verify tasks are not displayed under Active Workflows page
            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(cloudReviewApproveWF));

            // Select Completed Workflows and verify workflows are displayed
            myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(cloudReviewApproveWF));

            // ---- Step 9 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The document is not synced to Cloud. The Synced label and Workflow label are not present for the document on Document Library page.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File2 is NOT Synced and NOT part of a workflow
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isCloudSynced(), "Verifying the document is NOT synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isPartOfWorkflow(),
                    "Verifying the document is NOT part of a workflow");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // ---- Step 10 ----
            // ---- Step action ---
            // Cloud Verify the document.
            // ---- Expected results ----
            // The document is absent in Cloud.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
            Assert.assertFalse(documentLibraryPage.isFileVisible(cloudReviewApproveFile), "Verifying File2 exists");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15635() throws Exception
    {
        dataPrep(getTestName() + "83");
    }

    /**
     * AONE-15635:Cloud Review Task - action execution after rejection in Cloud and completion in OP
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15635() throws Exception
    {
        String testName = getTestName() + "83";
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String cloudReviewRejectFile = getFileName(testName) + "-3.txt";
        String[] fileInfo3 = { cloudReviewRejectFile, DOCLIB };

        String cloudReviewRejectWF = testName + System.currentTimeMillis() + "-3-WF";

        String dueDate = getDueDateString();
        int requiredApprovalPercentage = 100;

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 3 files
            siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo3).render();

            // Select "Cloud Task or Review" from select a workflow dropdown
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, cloudReviewRejectFile).render();

            // ---- Step 1 ----
            // ---- Step action ---
            // Specify any value in the After Completion drop-down list, e.g. 'Delete content on cloud and remove sync'.
            // ---- Expected results ----
            // Performed correctly.
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setMessage(cloudReviewRejectWF);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);

            // ---- Step 2 ----
            // ---- Step action ---
            // Create Workflow.
            // ---- Expected results ----
            // The workflow is created successfully. A new task appeared to the assignee.
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // ---- Step 3 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The Synced label and Workflow label are present for the document on Document Library page.
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isPartOfWorkflow(), "Verifying the File3 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isCloudSynced(), "Verifying the File3 is synced");
            Assert.assertTrue(checkIfContentIsSynced(drone, cloudReviewRejectFile), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify Workflows are created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(cloudReviewRejectWF), "Verifying workflow3 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // ---- Step 4 ----
            // ---- Step action ---
            // Cloud Verify the document
            // ---- Expected results ----
            // The Synced label and Workflow label are present for the document
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
            Assert.assertTrue(documentLibraryPage.isFileVisible(cloudReviewRejectFile), "Verifying File1 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isCloudSynced(), "Verifying the File1 is synced");

            // ---- Step 5 ----
            // ---- Step action ---
            // Cloud Reject task.
            // ---- Expected results ----
            // Performed correctly. The task is moved to Completed filter. A new task appears in OP.
            MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewRejectWF));
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, cloudReviewRejectWF, TaskStatus.COMPLETED, EditTaskAction.REJECT);
            Assert.assertFalse(myTasksPage.isTaskPresent(cloudReviewRejectWF));

            // Verify tasks are displayed in Completed Tasks list
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewRejectWF));

            // ---- Step 7 ----
            // ---- Step action ---
            // Cloud Verify the document.
            // ---- Expected results ----
            // The document is still synced to Cloud. The Synced label are present for the document .
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
            Assert.assertTrue(documentLibraryPage.isFileVisible(cloudReviewRejectFile), "Verifying File3 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isCloudSynced(), "Verifying the File3 is synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isPartOfWorkflow(), "Verifying the File3 is part of a workflow");
            ShareUser.logout(hybridDrone);

            // Login as OP user
            sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // ---- Step 6 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The document is still synced to Cloud. The Synced label and Workflow label are present for the document on Document Library page.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isCloudSynced(), "Verifying the document is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isPartOfWorkflow(),
                    "Verifying the document is part of a workflow");

            // ---- Step 8 ----
            // ---- Step action ---
            // OP Complete task (Task Done action).
            // ---- Expected results ----
            // Performed correctly. The task is moved to Completed filter.
            myTasksPage = sharePage.getNav().selectMyTasks().render();
            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, cloudReviewRejectWF));
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, cloudReviewRejectWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
            Assert.assertFalse(myTasksPage.isTaskPresent(cloudReviewRejectWF));
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewRejectWF));

            // Navigate to Workflows I've Started page and verify tasks are not displayed under Active Workflows page
            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(cloudReviewRejectWF));

            // Select Completed Workflows and verify workflows are displayed
            myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(cloudReviewRejectWF));

            // ---- Step 9 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The document is not synced to Cloud. The Synced label and Workflow label are not present for the document on Document Library page.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isCloudSynced(), "Verifying the document is NOT synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isPartOfWorkflow(),
                    "Verifying the document is NOT part of a workflow");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // ---- Step 10 ----
            // ---- Step action ---
            // Cloud Verify the document.
            // ---- Expected results ----
            // The document is absent in Cloud.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
            Assert.assertFalse(documentLibraryPage.isFileVisible(cloudReviewRejectFile), "Verifying File3 does NOT exist");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    @Test(groups = "DataPrepHybrid", enabled = true)
    public void dataPrep_15636() throws Exception
    {
        dataPrep(getTestName() + "83");
    }

    /**
     * AONE-15636:Lock on-premise content - ON
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15636() throws Exception
    {
        String testName = getTestName() + "83";
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = getDueDateString();
        int requiredApprovalPercentage = 100;

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files
            siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo1).render();

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);
            formDetails.setMessage(workFlowName1);

            // ---- Step 1 ----
            // ---- Step action ---
            // Check the Lock on-premise content check-box.
            // ---- Expected results ----
            // Performed correctly.
            formDetails.setLockOnPremise(true);

            // ---- Step 2 ----
            // ---- Step action ---
            // Create Workflow.
            // ---- Expected results ----
            // The workflow is created successfully. A new task appeared to the assignee.
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1).render();
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // ---- Step 3 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // Document is part of workflow, is synced and locked
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isLocked(), "Verifying the File1 is Locked");
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getContentInfo(), "This document is locked by you.",
                    "Verifying Locked message");
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            // Verify Workflows are created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // ---- Step 4 ----
            // ---- Step action ---
            // Cloud Verify the document.
            // ---- Expected results ----
            // The document is not locked.
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isLocked(), "Verifying the document is NOT locked");

            // ---- Step 5 ----
            // ---- Step action ---
            // Cloud Complete task.
            // ---- Expected results ----
            // Performed correctly.
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName1, TaskStatus.COMPLETED, EditTaskAction.APPROVE);
            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName1));
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // ---- Step 6 ----
            // ---- Step action ---
            // OP Complete task.
            // ---- Expected results ----
            // Performed correctly.
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName1, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName1));
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            // ---- Step 7 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The document is now unlocked.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isLocked(), "Verifying the File1 is Locked");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15637() throws Exception
    {
        dataPrep(getTestName() + "83");
    }

    /**
     * AONE-15637:Lock on-premise content - OFF
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15637() throws Exception
    {
        String testName = getTestName() + "83";
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName2 = getFileName(testName) + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String workFlowName2 = testName + System.currentTimeMillis() + "-2-WF";
        String dueDate = getDueDateString();
        int requiredApprovalPercentage = 100;

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files
            siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2).render();

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            formDetails.setMessage(workFlowName2);
            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);

            // ---- Step 1 ----
            // ---- Step action ---
            // Uncheck the Lock on-premise content check-box.
            // ---- Expected results ----
            // Performed correctly.
            formDetails.setLockOnPremise(false);

            // ---- Step 2 ----
            // ---- Step action ---
            // Create Workflow.
            // ---- Expected results ----
            // The workflow is created successfully. A new task appeared to the assignee
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName2).render();
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // ---- Step 3 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // Document is part of workflow and not locked
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isLocked(), "Verifying the File2 is NOT Locked");
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName2), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify Workflows are created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName2), "Verifying workflow2 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // ---- Step 4 ----
            // ---- Step action ---
            // Cloud Verify the document.
            // ---- Expected results ----
            // The document is not locked.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName2), "Verifying File2 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the document is NOT synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the document is NOT synced");

            // ---- Step 5 ----
            // ---- Step action ---
            // Cloud Complete task.
            // ---- Expected results ----
            // Performed correctly.
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName2));
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName2, TaskStatus.COMPLETED, EditTaskAction.APPROVE);
            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName2));
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName2));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // ---- Step 6 ----
            // ---- Step action ---
            // OP Complete task.
            // ---- Expected results ----
            // Performed correctly.
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName2));
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName2, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName2));
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName2));

            // ---- Step 7 ----
            // ---- Step action ---
            // OP Verify the document.
            // ---- Expected results ----
            // The document is not locked.
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isLocked(), "Verifying the File2 is Locked");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

}
