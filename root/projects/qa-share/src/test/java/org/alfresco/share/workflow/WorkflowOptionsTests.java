package org.alfresco.share.workflow;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.AssignmentPage;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Bogdan.Bocancea
 *         Class includes: Tests from TestLink in Area: Workflow Options
 *         - After Completion Actions
 *         - Lock on-premise content
 *         - Priority
 *         - Select Destination
 */

@Listeners(FailedTestListener.class)
public class WorkflowOptionsTests extends AbstractWorkflow
{
    private String testDomain;
    private String invitedDomain1 = "invited1.test";

    String cloudCollaboratorSite = null;
    String cloudContributorSite = null;
    String cloudConsumerSite = null;
    String testSelectDestFolders;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;
        testSelectDestFolders = "testDifferenFolders3";
    }

    public void dataPrep(String testName) throws Exception
    {

        String user1 = getUserNameForDomain(testName + "OP", testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "CL", testDomain);
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
        dataPrep(getTestName() + "101");
    }

    /**
     * AONE-15630:Keep content synced on cloud
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15630() throws Exception
    {
        String testName = getTestName() + "101";
        String user1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser = getUserNameForDomain(testName + "CL", testDomain);
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
        dataPrep(getTestName()+ "101");
    }

    /**
     * AONE-15631:Keep content on cloud and remove sync
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15631() throws Exception
    {
        String testName = getTestName()+ "101";
        String user1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser = getUserNameForDomain(testName + "CL", testDomain);
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
        dataPrep(getTestName()+ "101");
    }

    /**
     * AONE-15632:Delete content on cloud and remove sync
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15632() throws Exception
    {
        String testName = getTestName()+ "101";
        String user1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser = getUserNameForDomain(testName + "CL", testDomain);
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
        dataPrep(getTestName()+ "101");
    }

    /**
     * AONE-15633:Simple Cloud Task - action execution after completion in Cloud and OP
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15633() throws Exception
    {
        String testName = getTestName()+ "101";
        String user1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser = getUserNameForDomain(testName + "CL", testDomain);
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
        dataPrep(getTestName()+ "101");
    }

    /**
     * AONE-15634:Cloud Review Task - action execution after approval in Cloud and completion in OP
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15634() throws Exception
    {
        String testName = getTestName()+ "101";
        String user1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser = getUserNameForDomain(testName + "CL", testDomain);
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
        dataPrep(getTestName() + "101");
    }

    /**
     * AONE-15635:Cloud Review Task - action execution after rejection in Cloud and completion in OP
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15635() throws Exception
    {
        String testName = getTestName() + "101";
        String user1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser = getUserNameForDomain(testName + "CL", testDomain);
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
        dataPrep(getTestName() + "101");
    }

    /**
     * AONE-15636:Lock on-premise content - ON
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15636() throws Exception
    {
        String testName = getTestName() + "101";
        String user1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser = getUserNameForDomain(testName + "CL", testDomain);
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
        dataPrep(getTestName() + "101");
    }

    /**
     * AONE-15637:Lock on-premise content - OFF
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15637() throws Exception
    {
        String testName = getTestName() + "101";
        String user1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser = getUserNameForDomain(testName + "CL", testDomain);
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

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15643() throws Exception
    {
        dataPrep(getTestName()+ "101");
    }

    /**
     * AONE-15643: Priority - High
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15643() throws Exception
    {
        String testName = getTestName()+ "101";
        String opUser1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser1 = getUserNameForDomain(testName + "CL", testDomain);
        String cloudSiteName = getSiteName(testName + "cloud" + System.currentTimeMillis());
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName1 = getFileName(testName) + System.currentTimeMillis() + "high";
        String[] opFileInfo1 = new String[] { opFileName1 };
        String workFlowName1 = testName + System.currentTimeMillis() + "High";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site in cloud
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName));

        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();

        // Start Simple Cloud Task workflow with High Priority
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName1).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        // ---- Step 1 ----
        // ---- Step action ---
        // Choose High value in the Priority drop-down list
        // ---- Expected results ----
        // Performed correctly.
        formDetails.setMessage(workFlowName1);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setTaskPriority(Priority.HIGH);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // ---- Step 2 ----
        // ---- Step action ---
        // Create Workflow.
        // ---- Expected results ----
        // The workflow is created successfully. A new task appeared to the assignee.
        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the document is part of the workflow
        assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), "Verifying the document is synced");
        assertTrue(checkIfContentIsSynced(drone, opFileName1), "Verifying the Sync Status is \"Synced\"");

        // ---- Step 3 ----
        // ---- Step action ---
        // OP Verify the workflow details.
        // ---- Expected results ----
        // High Priority is set.
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        WorkFlowDetailsPage detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName1).render();
        Assert.assertEquals(detailsPage.getWorkFlowDetailsGeneralInfo().getPriority(), Priority.HIGH);

        ShareUser.logout(drone);

        // ---- Step 4, 5 ----
        // ---- Step action ---
        // 4. Cloud Verify the workflow details.
        // 5. Cloud Verify the received task details.
        // ---- Expected results ----
        // 4/5. High Priority is set.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        Assert.assertEquals(myTasks.selectViewTasks(workFlowName1).render().getTaskDetailsInfo().getPriority(), Priority.HIGH);

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15644() throws Exception
    {
        dataPrep(getTestName()+ "101");
    }

    /**
     * AONE-15644:Priority - Medium
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15644() throws Exception
    {
        String testName = getTestName()+ "101";
  
        String opUser1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser1 = getUserNameForDomain(testName + "CL", testDomain);
        
        String cloudSiteName = getSiteName(testName + "cloud" + System.currentTimeMillis());
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName1 = getFileName(testName) + System.currentTimeMillis() + "Medium";
        String[] opFileInfo1 = new String[] { opFileName1 };
        String workFlowName1 = testName + System.currentTimeMillis() + "Medium";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site in cloud
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName));

        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();

        // Start Simple Cloud Task workflow with medium Priority
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName1).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        // ---- Step 1 ----
        // ---- Step action ---
        // Choose Medium value in the Priority drop-down list
        // ---- Expected results ----
        // Performed correctly.
        formDetails.setMessage(workFlowName1);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // ---- Step 2 ----
        // ---- Step action ---
        // Create Workflow.
        // ---- Expected results ----
        // The workflow is created successfully. A new task appeared to the assignee.
        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the document is part of the workflow
        assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), "Verifying the document is synced");
        assertTrue(checkIfContentIsSynced(drone, opFileName1), "Verifying the Sync Status is \"Synced\"");

        // ---- Step 3 ----
        // ---- Step action ---
        // OP Verify the workflow details.
        // ---- Expected results ----
        // Medium Priority is set.
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        WorkFlowDetailsPage detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName1).render();
        Assert.assertEquals(detailsPage.getWorkFlowDetailsGeneralInfo().getPriority(), Priority.MEDIUM);

        ShareUser.logout(drone);

        // ---- Step 4, 5 ----
        // ---- Step action ---
        // 4. Cloud Verify the workflow details.
        // 5. Cloud Verify the received task details.
        // ---- Expected results ----
        // 4/5. Medium priority is set.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        Assert.assertEquals(myTasks.selectViewTasks(workFlowName1).render().getTaskDetailsInfo().getPriority(), Priority.MEDIUM);

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15645() throws Exception
    {
        dataPrep(getTestName()+ "101");
    }

    /**
     * AONE-15645:Priority - Low
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15645() throws Exception
    {
        String testName = getTestName()+ "101";
        String opUser1 = getUserNameForDomain(testName+ "OP", testDomain);
        String cloudUser1 = getUserNameForDomain(testName + "CL", testDomain);
        String cloudSiteName = getSiteName(testName + "cloud" + System.currentTimeMillis());
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName1 = getFileName(testName) + System.currentTimeMillis() + "Low";
        String[] opFileInfo1 = new String[] { opFileName1 };
        String workFlowName1 = testName + System.currentTimeMillis() + "Low";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site in cloud
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName));

        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();

        // Start Simple Cloud Task workflow with low priority
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName1).render();
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        // ---- Step 1 ----
        // ---- Step action ---
        // Choose Low value in the Priority drop-down list
        // ---- Expected results ----
        // Performed correctly.
        formDetails.setMessage(workFlowName1);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setTaskPriority(Priority.LOW);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // ---- Step 2 ----
        // ---- Step action ---
        // Create Workflow.
        // ---- Expected results ----
        // The workflow is created successfully. A new task appeared to the assignee.
        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the document is part of the workflow
        assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(opFileName1).isCloudSynced(), "Verifying the document is synced");
        assertTrue(checkIfContentIsSynced(drone, opFileName1), "Verifying the Sync Status is \"Synced\"");

        // ---- Step 3 ----
        // ---- Step action ---
        // OP Verify the workflow details.
        // ---- Expected results ----
        // Low Priority is set.
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        WorkFlowDetailsPage detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName1).render();
        Assert.assertEquals(detailsPage.getWorkFlowDetailsGeneralInfo().getPriority(), Priority.LOW);

        ShareUser.logout(drone);

        // ---- Step 4, 5 ----
        // ---- Step action ---
        // 4. Cloud Verify the workflow details.
        // 5. Cloud Verify the received task details.
        // ---- Expected results ----
        // 4/5. Low priority is set.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        Assert.assertEquals(myTasks.selectViewTasks(workFlowName1).render().getTaskDetailsInfo().getPriority(), Priority.LOW);

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15652() throws Exception
    {
        String testName = getTestName()+ "101";
        String opUser1 = getUserNameForDomain(testName + "OP", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { opUser1 };

        String cloudUser1 = getUserNameForDomain(testName + "1", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String cloudUser2 = getUserNameForDomain(testName + "2", invitedDomain1);
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        String cloudSiteName1 = getSiteName(testName + "1");
        String opSiteName = getSiteName(testName + "OP");
        String fileName = getFileName(testName) + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, invitedDomain1, "1000");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser1, cloudUser2, getSiteShortname(cloudSiteName1), "SiteCollaborator", "");
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        signInToAlfrescoInTheCloud(drone, cloudUser2, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
    }

    /**
     * AONE-15652:Select Destination - different networks
     */
    @Test(groups = "Hybrid")
    public void AONE_15652() throws Exception
    {
        String testName = getTestName()+ "101";
        String opUser1 = getUserNameForDomain(testName + "OP", DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName + "OP");
        String fileName = getFileName(testName) + ".doc";

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Open Document library, Upload a file
        DocumentLibraryPage docLibPage = SiteUtil.openSiteDocumentLibraryURL(drone, AbstractUtils.getSiteShortname(opSiteName));

        // Select StartWorkflow
        StartWorkFlowPage startWorkFlowPage = docLibPage.getFileDirectoryInfo(fileName).selectStartWorkFlow().render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        DestinationAndAssigneePage destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Verify the list of available networks.
        // ---- Expected results ----
        // network1 and network2 are available.
        Assert.assertTrue(destinationPage.isNetworkDisplayed(DOMAIN_HYBRID));
        Assert.assertTrue(destinationPage.isNetworkDisplayed(invitedDomain1));
    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15653() throws Exception
    {
        String testName = getTestName()+ "101";
        String opUser1 = getUserNameForDomain(testName + "OP", DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudUser2 = getUserNameForDomain(testName + "user2", invitedDomain1);
        String cloudUser3 = getUserNameForDomain(testName + "user3", invitedDomain1);
        String cloudSiteName1 = getSiteName(testName + "4");
        String cloudSiteName2 = getSiteName(testName + "5");
        String cloudSiteName3 = getSiteName(testName + "6");

        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };
        String[] cloudUserInfo3 = new String[] { cloudUser3 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Create User3 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo3);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User2 (Cloud)
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName2, SITE_VISIBILITY_PUBLIC);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser2, cloudUser1, cloudSiteName2, UserRole.COLLABORATOR);
        ShareUser.logout(hybridDrone);

        // Login as User3 (Cloud)
        ShareUser.login(hybridDrone, cloudUser3, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName3, SITE_VISIBILITY_PUBLIC);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser3, cloudUser1, cloudSiteName3, UserRole.CONSUMER);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * AONE-15653:Select Destination - different sites
     */
    @Test(groups = "Hybrid")
    public void AONE_15653() throws Exception
    {
        String testName = getTestName()+ "101";
        String opUser1 = getUserNameForDomain(testName + "OP", DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudSiteName1 = getSiteName(testName + "4");
        String cloudSiteName2 = getSiteName(testName + "5");
        String cloudSiteName3 = getSiteName(testName + "6");
        String opSiteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + "1.doc";
        String fileName2 = getFileName(testName) + System.currentTimeMillis() + "2.doc";
        String fileName3 = getFileName(testName) + System.currentTimeMillis() + "3.doc";
        String[] fileInfo1 = { fileName1, DOCLIB };
        String[] fileInfo2 = { fileName2, DOCLIB };
        String[] fileInfo3 = { fileName3, DOCLIB };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo1).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo2).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo3).render();

        // Select StartWorkflow for cloud user1 file
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);

        DestinationAndAssigneePage destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        // ---- Step 1 ----
        // ---- Step action ---
        // Verify the list of available sites
        // ---- Expected results ----
        // user1site, user2site, user3site are available.
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName1));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName2));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName3));

        // ---- Step 2 ----
        // ---- Step action ---
        // Choose user1site and click Sync
        // ---- Expected results ----
        // Performed correctly.
        selectDestinationAndSync(destinationPage, invitedDomain1, cloudSiteName1, DEFAULT_FOLDER_NAME);

        // ---- Step 3 ----
        // ---- Step action ---
        // Select user1 as assignee.
        // ---- Expected results ----
        // Performed correctly. No notifications are displayed.
        AssignmentPage assigneePage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assigneePage.selectAssignee(cloudUser1);

        // ---- Step 4 ----
        // ---- Step action ---
        // Create Workflow.
        // ---- Expected results ----
        // Performed correctly. No notifications are displayed. Workflow is created. The document is synced to the chosen site.
        cloudTaskOrReviewPage.selectStartWorkflow().render();
        docLibPage = docLibPage.render();
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow());

        // ---- Step 5 ----
        // ---- Step action ---
        // Repeat steps 2-4 for user2site
        // ---- Expected results ----
        // Performed correctly. No notifications are displayed. Workflow is created. The document is synced to the chosen site.
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName2);
        destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName1));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName2));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName3));
        selectDestinationAndSync(destinationPage, invitedDomain1, cloudSiteName2, DEFAULT_FOLDER_NAME);

        assigneePage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assigneePage.selectAssignee(cloudUser1);

        cloudTaskOrReviewPage.selectStartWorkflow().render();
        docLibPage = docLibPage.render();

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow());

        // ---- Step 6 ----
        // ---- Step action ---
        // Repeat steps 2-4 for user3site
        // ---- Expected results ----
        // Performed correctly. No notifications are displayed. Workflow is created. The document is synced to the chosen site.
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName3);
        destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName1));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName2));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName3));

        destinationPage.selectNetwork(invitedDomain1);
        destinationPage.selectSite(cloudSiteName3);
        try
        {
            destinationPage.selectSubmitButtonToSync();
        }
        catch (PageOperationException e)
        {
            Assert.assertEquals(e.getMessage(), "Sync Button is disabled");
        }
    }

    /**
     * Data preparation for tests: AONE-15654, AONE-15655, AONE-15656
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_selectDifferentFolders() throws Exception
    {
        String opUser1 = getUserNameForDomain(testSelectDestFolders, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testSelectDestFolders + "user1", invitedDomain1);
        String cloudUser2 = getUserNameForDomain(testSelectDestFolders + "user2", invitedDomain1);
        String cloudUser3 = getUserNameForDomain(testSelectDestFolders + "user3", invitedDomain1);
        String cloudUser4 = getUserNameForDomain(testSelectDestFolders + "user4", invitedDomain1);
        cloudCollaboratorSite = getSiteName(testSelectDestFolders + "Collaborator");
        cloudContributorSite = getSiteName(testSelectDestFolders + "Contributor");
        cloudConsumerSite = getSiteName(testSelectDestFolders + "Consumer");

        String folder1 = getFolderName(testSelectDestFolders + "Collaborator");
        String folder2 = getFolderName(testSelectDestFolders + "Contributor");
        String folder3 = getFolderName(testSelectDestFolders + "Consumer");

        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };
        String[] cloudUserInfo3 = new String[] { cloudUser3 };
        String[] cloudUserInfo4 = new String[] { cloudUser4 };

        try
        {
            // Create User1 (On-premise)
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

            // Create User1 (Cloud)
            CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

            // Create User2 (Cloud)
            CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

            // Create User3 (Cloud)
            CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo3);

            // Create User4 (Cloud)
            CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo4);

            // Login as User2 (Cloud)
            ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(hybridDrone, cloudCollaboratorSite, SITE_VISIBILITY_PUBLIC);

            // Inviting user1 as colloborator to the site.
            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser2, cloudUser1, cloudCollaboratorSite, UserRole.COLLABORATOR);

            // Creating 3 folders with Collobarator,Contributor and Consumer roles for each folder.
            ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudCollaboratorSite);
            createThreeFoldersWithContentRoles(hybridDrone, cloudUser1, folder1, folder2, folder3);

            ShareUser.logout(hybridDrone);

            // Login as User3 (Cloud)
            ShareUser.login(hybridDrone, cloudUser3, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(hybridDrone, cloudContributorSite, SITE_VISIBILITY_PUBLIC);

            // Inviting user1 as contributor to the site.
            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser3, cloudUser1, cloudContributorSite, UserRole.CONTRIBUTOR);

            // Creating 3 folders with Collobarator,Contrinbutor and Consumer
            // roles for each folder.
            createThreeFoldersWithContentRoles(hybridDrone, cloudUser1, folder1, folder2, folder3);

            ShareUser.logout(hybridDrone);

            // Login as User4 (Cloud)
            ShareUser.login(hybridDrone, cloudUser4, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(hybridDrone, cloudConsumerSite, SITE_VISIBILITY_PUBLIC);

            // Inviting user1 as consumer to the site.
            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser4, cloudUser1, cloudConsumerSite, UserRole.CONSUMER);

            // Creating 3 folders with Collobarator,Contrinbutor and Consumer
            // roles for each folder.
            createThreeFoldersWithContentRoles(hybridDrone, cloudUser1, folder1, folder2, folder3);

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

            // Set up the cloud sync
            signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    /**
     * AONE-15654:Select Destination - different folders - Collaborator
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15654() throws Exception
    {
        String opUser1 = getUserNameForDomain(testSelectDestFolders, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testSelectDestFolders + "user1", invitedDomain1);
        cloudCollaboratorSite = getSiteName(testSelectDestFolders + "Collaborator");
        cloudContributorSite = getSiteName(testSelectDestFolders + "Contributor");
        cloudConsumerSite = getSiteName(testSelectDestFolders + "Consumer");
        String opSiteName1 = getSiteName(testSelectDestFolders + System.currentTimeMillis() + "op1");

        String folder1 = getFolderName(testSelectDestFolders + "Collaborator");

        String fileName1 = getFileName(testSelectDestFolders) + System.currentTimeMillis() + "1.doc";
        String fileName2 = getFileName(testSelectDestFolders) + System.currentTimeMillis() + "2.doc";
        String fileName3 = getFileName(testSelectDestFolders) + System.currentTimeMillis() + "3.doc";
        String[] fileInfo1 = { fileName1, DOCLIB };
        String[] fileInfo2 = { fileName2, DOCLIB };
        String[] fileInfo3 = { fileName3, DOCLIB };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName1, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo1).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo2).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo3).render();

        // ---- Step 1, 2, 3, 4 ----
        // ---- Step action ---
        // 1. Verify the list of available sites.
        // 2. Choose user2siteCollaborator > folder1.
        // 3. Select user1 as assignee.
        // 4. Create Workflow.
        // ---- Expected results ----
        // 1. user2siteCollaborator, user3siteContributor, user4siteConsumer are available.
        // 2. Performed correctly.
        // 3. Performed correctly. No notifications are displayed.
        // 4.Performed correctly. No notifications are displayed. Workflow is created. The document is synced to the chosen site. The user has Collaborator
        // permissions.
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder1, fileName1, cloudCollaboratorSite, docLibPage);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow());

        // ---- Step 5 ----
        // ---- Step action ---
        // Repeat steps 2-4 for user3siteContributor
        // ---- Expected results ----
        // Performed correctly. No notifications are displayed. Workflow is created. The document is synced to the chosen site. The user has Collaborator
        // permissions.
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder1, fileName2, cloudContributorSite, docLibPage);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow());

        // ---- Step 6 ----
        // ---- Step action ---
        // Repeat steps 2-4 for user4siteConsumer
        // ---- Expected results ----
        // Performed correctly. No notifications are displayed. Workflow is created. The document is synced to the chosen site. The user has Collaborator
        // permissions.
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder1, fileName3, cloudConsumerSite, docLibPage);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName3).isPartOfWorkflow());
    }

    /**
     * AONE-15655:Select Destination - different folders - Contributor
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15655() throws Exception
    {
        String opUser1 = getUserNameForDomain(testSelectDestFolders, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testSelectDestFolders + "user1", invitedDomain1);
        cloudCollaboratorSite = getSiteName(testSelectDestFolders + "Collaborator");
        cloudContributorSite = getSiteName(testSelectDestFolders + "Contributor");
        cloudConsumerSite = getSiteName(testSelectDestFolders + "Consumer");
        String opSiteName2 = getSiteName(testSelectDestFolders + System.currentTimeMillis() + "15655");

        String folder2 = getFolderName(testSelectDestFolders + "Contributor");

        String fileName4 = getFileName(testSelectDestFolders) + System.currentTimeMillis() + "1.doc";
        String fileName5 = getFileName(testSelectDestFolders) + System.currentTimeMillis() + "2.doc";
        String fileName6 = getFileName(testSelectDestFolders) + System.currentTimeMillis() + "3.doc";

        String[] fileInfo4 = { fileName4, DOCLIB };
        String[] fileInfo5 = { fileName5, DOCLIB };
        String[] fileInfo6 = { fileName6, DOCLIB };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        // Creating 2nd site to have sync with contributor site.
        ShareUser.createSite(drone, opSiteName2, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo4).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo5).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo6).render();

        // ---- Step 1, 2, 3, 4 ----
        // ---- Step action ---
        // 1. Verify the list of available sites.
        // 2. Choose user2siteCollaborator > folder2.
        // 3. Select user1 as assignee.
        // 4. Create Workflow.
        // ---- Expected results ----
        // 1. user2siteCollaborator, user3siteContributor, user4siteConsumer are available.
        // 2. Performed correctly.
        // 3. Performed correctly. No notifications are displayed.
        // 4.Performed correctly. No notifications are displayed. Workflow is created. The document is synced to the chosen site. The user has Contributor 
        // permissions.
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder2, fileName4, cloudCollaboratorSite, docLibPage);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName4).isPartOfWorkflow());

        // ---- Step 5 ----
        // ---- Step action ---
        // Repeat steps 2-4 for user3siteContributor
        // ---- Expected results ----
        // Performed correctly. No notifications are displayed. Workflow is created. The document is synced to the chosen site. The user has Contributor
        // permissions.
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder2, fileName5, cloudContributorSite, docLibPage);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName5).isPartOfWorkflow());

        // ---- Step 6 ----
        // ---- Step action ---
        // Repeat steps 2-4 for user4siteConsumer
        // ---- Expected results ----
        // Performed correctly. No notifications are displayed. Workflow is created. The document is synced to the chosen site. The user has Contributor
        // permissions.
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder2, fileName6, cloudConsumerSite, docLibPage);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName6).isPartOfWorkflow());
    }

    /**
     * AONE-15656:Select Destination - different folders - Consumer
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15656() throws Exception
    {
        String opUser1 = getUserNameForDomain(testSelectDestFolders, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testSelectDestFolders + "user1", invitedDomain1);
        cloudCollaboratorSite = getSiteName(testSelectDestFolders + "Collaborator");
        cloudContributorSite = getSiteName(testSelectDestFolders + "Contributor");
        cloudConsumerSite = getSiteName(testSelectDestFolders + "Consumer");
        String opSiteName3 = getSiteName(testSelectDestFolders + System.currentTimeMillis() + "consumer");

        String folder3 = getFolderName(testSelectDestFolders + "Consumer");

        String fileName7 = getFileName(testSelectDestFolders) + System.currentTimeMillis() + "1.doc";
        String fileName8 = getFileName(testSelectDestFolders) + System.currentTimeMillis() + "2.doc";
        String fileName9 = getFileName(testSelectDestFolders) + System.currentTimeMillis() + "3.doc";

        String[] fileInfo7 = { fileName7, DOCLIB };
        String[] fileInfo8 = { fileName8, DOCLIB };
        String[] fileInfo9 = { fileName9, DOCLIB };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        // Creating 3rd site to have sync with consumer site.
        ShareUser.createSite(drone, opSiteName3, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);

        ShareUser.uploadFileInFolder(drone, fileInfo7).render();
        ShareUser.uploadFileInFolder(drone, fileInfo8).render();
        ShareUser.uploadFileInFolder(drone, fileInfo9).render();

        // Select StartWorkflow for cloud user1 on contributor site for folder3
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName7);

        DestinationAndAssigneePage destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudCollaboratorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudContributorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudConsumerSite));

        // ---- Step 1 ----
        // ---- Step action ---
        // OP Open Create Workflow page again and click on Select Destination.
        // ---- Expected results ----
        // Select Destination window is opened.
        destinationPage.selectNetwork(invitedDomain1);
        
        // ---- Step 2 ----
        // ---- Step action ---
        // Choose user2siteCollaborator > folder3.
        // ---- Expected results ----
        // Its not possible to create workflow. 'Sync' button is disabled for folder3.
        destinationPage.selectSite(cloudCollaboratorSite);
        try
        {
            destinationPage.selectFolder(folder3);
        }
        catch (PageOperationException e)
        {
            Assert.assertEquals(e.getMessage(), "Sync Folder is disabled");
        }

        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName3));

        // Select StartWorkflow for cloud user1 on contributor site for folder3
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName8);
        destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudCollaboratorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudContributorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudConsumerSite));

        // ---- Step 3 ----
        // ---- Step action ---
        // Repeat steps 2-4 for user3siteContributor
        // ---- Expected results ----
        // Its not possible to create a workflow. 'Sync' button is disabled for folder3.
        destinationPage.selectNetwork(invitedDomain1);
        destinationPage.selectSite(cloudContributorSite);
        try
        {
            destinationPage.selectFolder(folder3);
        }
        catch (PageOperationException e)
        {
            Assert.assertEquals(e.getMessage(), "Sync Folder is disabled");
        }

        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName3));

        // Select StartWorkflow for cloud user1 on contributor site for folder3
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName9);

        destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudCollaboratorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudContributorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudConsumerSite));

        // ---- Step 4 ----
        // ---- Step action ---
        // Repeat steps 2-4 for user3siteConsumer
        // ---- Expected results ----
        // Its not possible to create a workflow. 'Sync' button is disabled for folder3.
        destinationPage.selectNetwork(invitedDomain1);
        destinationPage.selectSite(cloudConsumerSite);
        destinationPage.selectFolder(folder3);
        try
        {
            destinationPage.selectFolder(folder3);
        }
        catch (PageOperationException e)
        {
            Assert.assertEquals(e.getMessage(), "Sync Folder is disabled");
        }
    }

    /**
     * @param hybridDrone
     * @param cloudUser1
     * @param folder1
     * @param folder2
     * @param folder3
     */
    private void createThreeFoldersWithContentRoles(WebDrone hybridDrone, String cloudUser1, String folder1, String folder2, String folder3)
    {
        DocumentLibraryPage docLibPage;

        // Creating 3 folders with Collobarator,Contrinbutor and Consumer roles for each folder.
        ShareUserSitePage.createFolder(hybridDrone, folder1, "").render();
        docLibPage = (DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(hybridDrone, cloudUser1, folder1, UserRole.COLLABORATOR, false);
        docLibPage.render();

        ShareUserSitePage.createFolder(hybridDrone, folder2, "").render();
        docLibPage = (DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(hybridDrone, cloudUser1, folder2, UserRole.CONTRIBUTOR, false);
        docLibPage.render();

        ShareUserSitePage.createFolder(hybridDrone, folder3, "").render();
        docLibPage = (DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(hybridDrone, cloudUser1, folder3, UserRole.CONSUMER, false);
        docLibPage.render();
    }

    /**
     * @param cloudUser
     * @param folder
     * @param fileName
     * @param docLibPage
     * @return DocumentLibraryPage
     */
    private DocumentLibraryPage startWorFlowOnContentWithRole(String cloudUser, String folder, String fileName, String syncSite, DocumentLibraryPage docLibPage)
    {
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        DestinationAndAssigneePage destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudCollaboratorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudContributorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudConsumerSite));

        // Selection clouduser1 site and sync successfull.
        selectDestinationAndSync(destinationPage, invitedDomain1, syncSite, folder);

        AssignmentPage assigneePage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assigneePage.selectAssignee(cloudUser);

        cloudTaskOrReviewPage.selectStartWorkflow().render();
        docLibPage = docLibPage.render();

        return docLibPage;
    }

    /**
     * Method to select the sitename of destination and clicks on sync button
     * 
     * @param drone
     * @param network
     * @param siteName
     * @param folderName
     * @return {@link DocumentDetailsPage}
     */
    private HtmlPage selectDestinationAndSync(DestinationAndAssigneePage assigneePage, String network, String siteName, String folderName)
    {
        assigneePage.selectNetwork(network);
        assigneePage.selectSite(siteName);
        assigneePage.selectFolder(folderName);

        return assigneePage.selectSubmitButtonToSync().render();
    }

}
