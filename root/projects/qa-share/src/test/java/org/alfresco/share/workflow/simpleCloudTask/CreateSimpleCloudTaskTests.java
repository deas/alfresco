package org.alfresco.share.workflow.simpleCloudTask;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.AssignmentPage;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.SendEMailNotifications;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDescription;
import org.alfresco.po.share.workflow.WorkFlowDetailsCurrentTask;
import org.alfresco.po.share.workflow.WorkFlowDetailsGeneralInfo;
import org.alfresco.po.share.workflow.WorkFlowDetailsHistory;
import org.alfresco.po.share.workflow.WorkFlowDetailsItem;
import org.alfresco.po.share.workflow.WorkFlowDetailsMoreInfo;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowHistoryOutCome;
import org.alfresco.po.share.workflow.WorkFlowHistoryType;
import org.alfresco.po.share.workflow.WorkFlowStatus;
import org.alfresco.po.share.workflow.WorkFlowTitle;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class CreateSimpleCloudTaskTests extends AbstractWorkflow
{

    private static final Logger logger = Logger.getLogger(CreateSimpleCloudTaskTests.class);
    private String testDomain;
    private String opUser;
    private String cloudUser;
    private String cloudSite;
    private String fileName_15606;
    private String fileName_15607;
    private String fileName_15608;
    private String siteName;
    private String workflowName_15607;
    private String workflowName_15608;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Setup started...");
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;

        opUser = getUserNameForDomain(testName + "op", testDomain);
        cloudUser = getUserNameForDomain(testName + "cl", testDomain);

        fileName_15606 = getFileName(testName) + "15606" + ".txt";
        fileName_15607 = getFileName(testName) + "15607" + ".txt";
        fileName_15608 = getFileName(testName) + "15608" + ".txt";
        cloudSite = getSiteName(testName + "cl");

        siteName = getSiteName(testName + "op");
        workflowName_15607 = "Message for AONE_15607";
        workflowName_15608 = "Message for AONE_15608";

    }

    @BeforeClass(groups = "DataPrepHybridWorkflow", dependsOnMethods = "setup")
    public void dataPrep_createUsers() throws Exception
    {

        logger.info("DataPrep_createUsers started...");
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
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD).render();
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC).render();

        // --- Step 3 ---
        // --- Step action ---
        // Any user is logged into the Share
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD).render();

        // --- Step 4 ---
        // --- Step action ---
        // The created user is authorised in Cloud, e.g. by user1@network.com
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // --- Step 5 ---
        // --- Step action ---
        // Any site is created
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD).render();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();
        // ShareUserSitePage.createFolder(drone, folderName, folderName).render(maxWaitTime);
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybridWorkflow")
    public void dataPrep_15606() throws Exception
    {

        // --- Step 6 ---
        // --- Step action ---
        // Any document is created/uploaded, e.g. test1.txt
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName_15606);
        contentDetails.setContent("file content of enterprise user");
        DocumentLibraryPage docLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, docLibraryPage).render();
        ShareUser.logout(drone);

    }

    /**
     * AONE-15606:Simple Cloud Task - Create
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15606() throws Exception
    {
        String workflowName = "Simple Cloud Task message for AONE_15606" + 1;

        // Login as OP user
        ShareUser.login(drone, opUser);

        // Create 'Cloud Task or Review' workflow page is opened
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // --- Step 1 ---
        // --- Step action ---
        // OPSpecify 'Simple Cloud Task' type.
        // --- Expected results ---
        // Performed correctly.
        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        Assert.assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK));

        // --- Step 2 ---
        // --- Step action ---
        // Specify any data in other required fields, e.g.
        // Message: 'Simple Cloud Task test message'
        // Network: 'network.com'
        // Site: 'user1 user1's Home'
        // Folder: 'Documents/'
        // Assignee: 'user1@network.com'
        // After completion: any
        // Lock on-premise content: any
        // Items: 'test1.txt'
        // --- Expected results ---
        // Performed correctly.
        cloudTaskOrReviewPage.enterMessageText(workflowName);

        DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectNetwork(testDomain);
        destinationAndAssigneePage.selectSite(cloudSite);
        destinationAndAssigneePage.selectSubmitButtonToSync();
        cloudTaskOrReviewPage.render();

        AssignmentPage assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assignmentPage.selectAssignee(cloudUser);

        cloudTaskOrReviewPage.selectItem(fileName_15606, siteName);

        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), testDomain, "Verify Destination Network Failed");
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), cloudSite, "Verify Destination Site Failed");
        Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), DEFAULT_FOLDER_NAME + "/", "Verify Destination Folder Name Failed");
        Assert.assertTrue(cloudTaskOrReviewPage.getAssignee().contains("(" + cloudUser + ")"), "Verify Assignment Account Failed");
        Assert.assertTrue(cloudTaskOrReviewPage.isItemAdded(fileName_15606));

        // --- Step 3 ---
        // --- Step action ---
        // Click on Start Workflow button.
        // --- Expected results ---
        // Workflow is started successfully. The workflow is located under Active on Workflows I've Started page.
        MyWorkFlowsPage myWorkFlowsPage = cloudTaskOrReviewPage.selectStartWorkflow().render();

        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workflowName), "Workflow -" + workflowName + "- is not present.");

        // --- Step 4 ---
        // --- Step action ---
        // Cloud Login as user1@network.com and verify the workflow.
        // --- Expected results ---
        // The workflow is started in Cloud. It is Active on Workflows I've Started page. A new task is assigned to the specified user.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        MyTasksPage myTaskPageC = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(myTaskPageC.isTaskPresent(workflowName), "Workflow -" + workflowName + "- is not present.");

        TaskDetails taskDetails = myTaskPageC.getTaskDetails(workflowName);

        // TODO: Fails with ACE-2875
        Assert.assertEquals(taskDetails.getStartedBy(), opUser);

        ShareUser.logout(drone);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybridWorkflow")
    public void dataPrep_15607() throws Exception
    {
        // --- Step 1 ---
        // --- Step action ---
        // Test case Enterprise40x-9584 is executed:
        // A Cloud user with standard/enterprise/partner network is created and activated, e.g. user1@network.com
        // The user should have valid cloud and on-prem account.
        // OP- The user log into the on-prem account using credentials.
        // OP- The user fills the start form for simple task and chooses the single assignee for the cloud task
        String[] fileInfo = { fileName_15607, DOCLIB };

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName_15607);
        contentDetails.setContent("file content of enterprise user");

        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName_15607);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workflowName_15607);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybridWorkflow")
    public void dataPrep_15608() throws Exception
    {

        // --- Step 1 ---
        // --- Step action ---
        // Test case Enterprise40x-9584 is executed.
        // A Cloud user with standard/enterprise/partner network is created and activated, e.g. user1@network.com
        // The user should have valid cloud and on-prem account.
        // OP- The user log into the on-prem account using credentials.
        // OP- The user fills the start form for simple task and chooses the single assignee for the cloud task
        String[] fileInfo = { fileName_15608, DOCLIB };

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName_15608);
        contentDetails.setContent("file content of enterprise user");

        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName_15608);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workflowName_15608);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        cloudTaskOrReviewPage.startWorkflow(formDetails);

        // --- Step 2 ---
        // --- Step action ---
        // Test case Enterprise40x-15146 is executed.
        MyWorkFlowsPage myForkflows = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        WorkFlowDetailsPage workflowDetails = myForkflows.selectWorkFlow(workflowName_15608).render();
        Assert.assertEquals(workflowDetails.getPageHeader(), "Details: " + workflowName_15608 + " (Start a task or review on Alfresco Cloud)");

        ShareUser.logout(drone);
    }

    /**
     * AONE-15607:Simple Cloud Task - Workflow Details (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15607() throws Exception
    {
        // Login as OP user
        ShareUser.login(drone, opUser);

        // --- Step 1 ---
        // --- Step action ---
        // OP Open created workflow details page.
        // --- Expected results ---
        // Details page is opened. The following title is displayed:
        // "Details: Simple Cloud Task test message (Start a task or review on Alfresco Cloud)"
        MyWorkFlowsPage myForkflows = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        WorkFlowDetailsPage workflowDetails = myForkflows.selectWorkFlow(workflowName_15607).render();
        Assert.assertEquals(workflowDetails.getPageHeader(), "Details: " + workflowName_15607 + " (Start a task or review on Alfresco Cloud)");

        // --- Step 2 ---
        // --- Step action ---
        // Verify 'General Info' section.
        // --- Expected results ---
        // The following data is displayed:
        // - Title: Cloud Task or Review
        // - Description: Create a task or start a review on Alfresco Cloud
        // - Started by: Administrator
        // - Due: (None)
        // - Completed: in progress
        // - Started: Thu 12 Sep 2013 17:15:07
        // - Priority: Medium
        // - Status: Workflow is in Progress
        // - Message: Simple Cloud Task test message
        WorkFlowDetailsGeneralInfo workflowGeneralInfo = workflowDetails.getWorkFlowDetailsGeneralInfo();
        Assert.assertEquals(workflowGeneralInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
        Assert.assertEquals(workflowGeneralInfo.getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW);
        Assert.assertTrue(workflowGeneralInfo.getStartedBy().contains(opUser));
        Assert.assertEquals(workflowGeneralInfo.getDueDateString(), NONE);
        Assert.assertEquals(workflowGeneralInfo.getCompleted(), "<in progress>");
        Assert.assertEquals(workflowGeneralInfo.getPriority(), Priority.MEDIUM);
        Assert.assertEquals(workflowGeneralInfo.getStatus(), WorkFlowStatus.WORKFLOW_IN_PROGRESS);
        Assert.assertEquals(workflowGeneralInfo.getMessage(), workflowName_15607);

        // --- Step 3 ---
        // --- Step action ---
        // Verify 'More Info' section.
        // --- Expected results ---
        // The following data is displayed:
        // - Type: Simple Cloud Task
        // - Destination: network.com
        // - After completion: specified value
        // - Lock on-premise content: specified value
        // - Assignment: user1 user1 (user1@network.com)
        WorkFlowDetailsMoreInfo workflowMoreInfo = workflowDetails.getWorkFlowDetailsMoreInfo();
        Assert.assertEquals(workflowMoreInfo.getType(), TaskType.SIMPLE_CLOUD_TASK);
        Assert.assertEquals(workflowMoreInfo.getDestination(), testDomain);
        Assert.assertEquals(workflowMoreInfo.getAfterCompletion(), KeepContentStrategy.DELETECONTENT);
        Assert.assertEquals(workflowMoreInfo.getAssignmentList().size(), 1);
        Assert.assertEquals(workflowMoreInfo.getAssignmentList().get(0), getUserFullNameWithEmail(cloudUser, cloudUser));

        // --- Step 4 ---
        // --- Step action ---
        // Verify 'Items' section.
        // --- Expected results ---
        // The following item is displayed: test1.txt
        List<WorkFlowDetailsItem> workflowItems = workflowDetails.getWorkFlowItems();
        Assert.assertEquals(workflowItems.get(0).getItemName(), fileName_15607);

        // --- Step 5 ---
        // --- Step action ---
        // Verify 'Current Tasks' section.
        // --- Expected results ---
        // The following data is displayed:
        // No tasks
        List<WorkFlowDetailsCurrentTask> workflowTasksList = workflowDetails.getCurrentTasksList();
        Assert.assertTrue(workflowTasksList.isEmpty(), "Tasks are present for the workflow " + workflowName_15607);

        // --- Step 6 ---
        // --- Step action ---
        // Verify 'History' section..
        // --- Expected results ---
        // The following data is displayed:
        // Start a task or review on Alfresco Cloud admin Thu 12 Sep 2013 17:15:07 Task Done
        List<WorkFlowDetailsHistory> workflowHistoryList = workflowDetails.getWorkFlowHistoryList();
        Assert.assertEquals(workflowHistoryList.size(), 1);
        Assert.assertEquals(workflowHistoryList.get(0).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
        Assert.assertEquals(workflowHistoryList.get(0).getCompletedBy(), getUserFullName(opUser));
        Assert.assertEquals(workflowHistoryList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
        Assert.assertTrue(workflowHistoryList.get(0).getComment().isEmpty(), "Comments are present in the History section");

        ShareUser.logout(drone);
    }

    /**
     * AONE-15608:Simple Cloud Task - Workflow Details (Cloud)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15608() throws Exception
    {
        // Login as Cloud user
        ShareUser.login(hybridDrone, cloudUser);

        // --- Step 1 ---
        // --- Step action ---
        // Cloud Open created workflow details page.
        // --- Expected results ---
        // Details page is opened. The following title is displayed: "Details: Simple Cloud Task test message (Task)"
        TaskHistoryPage workflowDetails = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workflowName_15608);
        Assert.assertEquals(workflowDetails.getPageHeader(), "Details: " + workflowName_15608 + " (Task)");

        // --- Step 2 ---
        // --- Step action ---
        // Verify 'General Info' section.
        // --- Expected results ---
        // The following data is displayed:
        // Title: Hybrid Adhoc Task Process
        // Description: Hybrid Adhoc Task Process
        // Started by: Administrator admin
        // Due: (None)
        // Completed: in progress
        // Started: Thu 12 Sep 2013 17:17:42
        // Priority: Medium
        // Status: Task is in Progress
        // Message: Simple Cloud Task test message

        WorkFlowDetailsGeneralInfo workflowGeneralInfo = workflowDetails.getWorkFlowDetailsGeneralInfo();
        Assert.assertEquals(workflowGeneralInfo.getTitle(), WorkFlowTitle.HYBRID_TASK);
        Assert.assertEquals(workflowGeneralInfo.getDescription(), WorkFlowDescription.ASSIGN_NEW_TASK_TO_SOMEONE_ON_THE_CLOUD);
        Assert.assertTrue(workflowGeneralInfo.getStartedBy().contains(cloudUser));
        Assert.assertEquals(workflowGeneralInfo.getDueDateString(), NONE);
        Assert.assertEquals(workflowGeneralInfo.getCompleted(), "<in progress>");
        Assert.assertEquals(workflowGeneralInfo.getPriority(), Priority.MEDIUM);
        Assert.assertEquals(workflowGeneralInfo.getStatus(), WorkFlowStatus.TASK_IN_PROGRESS);
        Assert.assertEquals(workflowGeneralInfo.getMessage(), workflowName_15608);

        // --- Step 3 ---
        // --- Step action ---
        // Verify 'More Info' section.
        // --- Expected results ---
        // The following data is displayed:
        // Send Email Notifications: No

        WorkFlowDetailsMoreInfo workflowMoreInfo = workflowDetails.getWorkFlowDetailsMoreInfo();
        // Fails with defect #ACE-2913
        Assert.assertEquals(workflowMoreInfo.getNotification(), SendEMailNotifications.YES);

        // --- Step 4 ---
        // --- Step action ---
        // Verify 'Items' section.
        // --- Expected results ---
        // The following item is displayed: test1.txt
        List<WorkFlowDetailsItem> workflowItems = workflowDetails.getWorkFlowItems();
        Assert.assertEquals(workflowItems.get(0).getItemName(), fileName_15608);

        // --- Step 5 ---
        // --- Step action ---
        // Verify 'Current Tasks' section..
        // --- Expected results ---
        // The following data is displayed:
        // Task Administrator admin (None) Not Yet Started
        List<WorkFlowDetailsCurrentTask> workflowTasksList = workflowDetails.getCurrentTasksList();
        Assert.assertEquals(workflowTasksList.size(), 1);
        Assert.assertEquals(workflowTasksList.get(0).getAssignedTo(), getUserFullName(cloudUser));
        Assert.assertEquals(workflowTasksList.get(0).getDueDateString(), NONE);
        Assert.assertEquals(workflowTasksList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED);

        // --- Step 6 ---
        // --- Step action ---
        // Verify 'History' section.
        // --- Expected results ---
        // 6. The following data is displayed:
        // Task Administrator admin Thu 12 Sep 2013 17:17:42 Task Done
        List<WorkFlowDetailsHistory> workflowHistoryList = workflowDetails.getWorkFlowHistoryList();
        Assert.assertEquals(workflowHistoryList.size(), 1);
        Assert.assertEquals(workflowHistoryList.get(0).getType(), WorkFlowHistoryType.TASK);
        Assert.assertEquals(workflowHistoryList.get(0).getCompletedBy(), getUserFullName(cloudUser));
        Assert.assertEquals(workflowHistoryList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
        Assert.assertTrue(workflowHistoryList.get(0).getComment().isEmpty(), "Comments are present in the History section");

        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybridWorkflow")
    public void dataPrep_15609() throws Exception
    {
        String testName = getTestName();
        dataPrep(testName);
    }

    /**
     * AONE-15609:Simple Cloud Task - Task Details (Cloud)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15609() throws Exception
    {

        String testName = getTestName();
        String workFlowName = "Simple Cloud Task " + testName;
        String fileName = getFileName(testName) + "-1.txt";

        TaskDetailsPage taskDetailsPage;
        TaskInfo taskInfo;

        try
        {
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Verify My Tasks page.
            // --- Expected results ---
            // A new active task, e.g. "Simple Cloud Task test message", is
            // present.
            assertEquals(myTasksPage.getSubTitle(), "Active Tasks", "Verify active tasks");
            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // --- Step 2 ---
            // --- Step action ---
            // Open the task details.
            // --- Expected results ---
            // Performed correctly. Information details are displayed.
            // The title is "Details: Simple Cloud Task test message (Task)".
            // Edit button is present under the information details section.
            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), "Details: " + workFlowName + " (Task)", "Verify task title");
            assertTrue(taskDetailsPage.isEditButtonPresent());

            // --- Step 3 ---
            // --- Step action ---
            // Verify 'Info' section.
            // --- Expected results ---
            // The following data is displayed:
            // Message: Simple Cloud Task test message
            // Owner: Administrator admin
            // Priority: Medium
            // Due: (None)
            // Identifier: 101713
            taskInfo = taskDetailsPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName, "Verify task message");
            assertEquals(taskInfo.getOwner(), getUserFullName(cloudUser), "Verify owner");
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM, "Verify priority");
            assertEquals(taskInfo.getDueDateString(), NONE, "Verify due date");
            assertNotNull(taskInfo.getIdentifier());

            // --- Step 4 ---
            // --- Step action ---
            // Verify 'Progress' section.
            // --- Expected results ---
            // The following data is displayed:
            // Status: Not Yet Started
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // --- Step 5 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following item is displayed: test1.txt
            List<TaskItem> taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);

            // --- Step 6 ---
            // --- Step action ---
            // Verify 'Response' section.
            // --- Expected results ---
            // The following data is displayed:
            // Comment: (None)

            assertEquals(taskDetailsPage.getComment(), NONE);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }

        ShareUser.logout(hybridDrone);
    }

    private void dataPrep(String testName) throws Exception
    {

        String workFlowName = "Simple Cloud Task " + testName;
        String fileName = getFileName(testName) + "-1.txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(hybridDrone);

        // Login to User1, create the simple task
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

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
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepHybridWorkflow")
    public void dataPrep_15610() throws Exception
    {

        String testName = getTestName();
        dataPrep(testName);
    }

    /**
     * AONE-15610:Simple Cloud Task - Sync
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15610() throws Exception
    {

        String testName = getTestName();
        String fileName = getFileName(testName) + "-1.txt";

        DocumentLibraryPage documentLibraryPage;

        try
        {
            // --- Step 1 ---
            // --- Step action ---
            // OP Verify the document, which is the part of the workflow.
            // --- Expected results ---
            // The document is successfully synced to Cloud. The correct destination is displayed.

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, siteName).render();
            // documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, DOCLIB + File.separator + folderName).render();

            assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying " + fileName + " exists");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of workflow");

            ShareUser.logout(drone);

            // --- Step 2 ---
            // --- Step action ---
            // Cloud Verify the synced document.
            // --- Expected results ---
            // The document is successfully synced to Cloud. It is present in the correct destination.

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and
            assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying File1 exists");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of workflow");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

}
