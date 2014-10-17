package org.alfresco.share.workflow.cloudReviewTask;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.task.EditTaskPage.Button;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.CurrentTaskType;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.SendEMailNotifications;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDescription;
import org.alfresco.po.share.workflow.WorkFlowDetails;
import org.alfresco.po.share.workflow.WorkFlowDetailsCurrentTask;
import org.alfresco.po.share.workflow.WorkFlowDetailsHistory;
import org.alfresco.po.share.workflow.WorkFlowDetailsItem;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowHistoryOutCome;
import org.alfresco.po.share.workflow.WorkFlowHistoryType;
import org.alfresco.po.share.workflow.WorkFlowStatus;
import org.alfresco.po.share.workflow.WorkFlowTitle;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CompleteCloudReviewTaskTests extends AbstractWorkflow
{

    private String testDomain;
    private String opUser;
    private String cloudUser;
    private String cloudSite;
    private String opSite;
    private String fileName;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {

        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;

        opUser = getUserNameForDomain(testName + "opUser", testDomain);
        cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);

        cloudSite = getSiteName(testName + "CL4");
        opSite = getSiteName(testName + "OP4");

    }

//    @BeforeClass(groups = "DataPrepHybridWorkflow", dependsOnMethods = "setup")
    public void dataPrep_createUsers() throws Exception
    {

        String opUser = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] { opUser };
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

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
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15624() throws Exception
    {

        fileName = getFileName(testName) + "-15624" + ".txt";

        String[] fileInfo = { fileName, DOCLIB };

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.logout(drone);
    }

    /**
     * AONE-15624 : Approve action
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15624() throws Exception
    {

        String workFlowName = "Cloud Review Task test message" + testName + "-15624CL";
        fileName = getFileName(testName) + "-15624" + ".txt";
        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        try
        {

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // OP Specify 'Cloud Review Task' type.
            // --- Expected results ---
            // Performed correctly.

            // --- Step 2 ---
            // --- Step action ---
            // Specify any data in other required fields, e.g. Message: 'Cloud Review Task test message' Network: 'network.com' Site: 'user1 user1's Home'
            // Folder: 'Documents/' Assignee: 'user1@network.com' Required Approval Percentage: 50 After completion: any Lock on-premise content: any Items:
            // 'test1.txt'
            // --- Expected results ---
            // Performed correctly.

            ShareUser.openSitesDocumentLibrary(drone, opSite).render();

            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            formDetails.setMessage(workFlowName);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setApprovalPercentage(50);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

            // --- Step 3 ---
            // --- Step action ---
            // Click on Start Workflow button.
            // --- Expected results ---
            // Workflow is started successfully. The workflow is located under Active on Workflows I've Started page.

            cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render();
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

            // --- Step 4 ---
            // --- Step action ---
            // Cloud Login as user1@network.com and verify the workflow.
            // --- Expected results ---
            // The workflow is started in Cloud. It is Active on Workflows I've Started page. A new task is assigned to the specified user.

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            assertTrue(ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render().isTaskPresent(workFlowName));

            // --- Step 5 ---
            // --- Step action ---
            // OP Open created workflow details page.
            // --- Expected results ---
            // Details page is opened. The following title is displayed: "Details: Cloud Review Task test message (Start a task or review on Alfresco Cloud)"

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, workFlowName).render();
            String header = "Details: " + workFlowName + " (Start a task or review on Alfresco Cloud)";
            assertEquals(workFlowDetailsPage.getPageHeader(), header);

            // --- Step 6 ---
            // --- Step action ---
            // Verify 'General Info' section.
            // --- Expected results ---
            // The following data is displayed: Title: Cloud Task or Review Description: Create a task or start a review on Alfresco Cloud Started by:
            // Administrator Due: (None) Completed: in progress Started: Thu 12 Sep 2013 17:15:07 Priority: Medium Status: Workflow is in Progress Message:
            // Cloud Review Task test message

            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW);
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStartedBy(), getUserFullName(opUser));
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getDueDateString(), NONE);
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getCompleted(), "<in progress>");
            assertTrue(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStartDate().isBeforeNow());
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getPriority(), Priority.MEDIUM);
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.WORKFLOW_IN_PROGRESS);
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getMessage(), workFlowName);

            // --- Step 7 ---
            // --- Step action ---
            // Verify 'More Info' section.
            // --- Expected results ---
            // The following data is displayed: Type: Cloud Review Task Destination: network.com After completion: specified value Lock on-premise content:
            // specified value Assignment: user1 user1 (user1@network.com)

            assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getType(), TaskType.CLOUD_REVIEW_TASK);
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getDestination(), DOMAIN_HYBRID);
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getAfterCompletion(), KeepContentStrategy.DELETECONTENT);
            assertTrue(!workFlowDetailsPage.getWorkFlowDetailsMoreInfo().isLockOnPremise());
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getAssignmentList().size(), userNames.size());
            assertTrue(workFlowDetailsPage.getWorkFlowDetailsMoreInfo().getAssignmentList().contains(getUserFullNameWithEmail(cloudUser, cloudUser)));

            // --- Step 8 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following item is displayed: test1.txt

            List<WorkFlowDetailsItem> items = workFlowDetailsPage.getWorkFlowItems();
            assertEquals(items.size(), 1);
            assertEquals(items.get(0).getItemName(), fileName);

            // --- Step 9 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks

            assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed());

            // --- Step 10 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed: Start a task or review on Alfresco Cloud admin Thu 12 Sep 2013 17:15:07 Task Done

            List<WorkFlowDetailsHistory> historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 1);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(opUser));
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(0).getComment(), "");

            // --- Step 11 ---
            // --- Step action ---
            // OP Verify the document, which is the part of the workflow.
            // --- Expected results ---
            // The document is successfully synced to Cloud. The correct destination is displayed.

            // ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            DocumentLibraryPage documentLibraryPageOP = SiteUtil.openSiteDocumentLibraryURL(drone, opSite).render();

            assertTrue(documentLibraryPageOP.isFileVisible(fileName), "Verifying " + fileName + " exists");
            assertTrue(documentLibraryPageOP.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
            assertTrue(documentLibraryPageOP.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of workflow");
            ShareUser.logout(drone);

            // --- Step 12 ---
            // --- Step action ---
            // Cloud Verify the synced document.
            // --- Expected results ---
            // The document is successfully synced to Cloud. It is present in the correct destination.

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            DocumentLibraryPage documentLibraryPageCL = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            assertTrue(documentLibraryPageCL.isFileVisible(fileName), "Verifying " + fileName + " exists");
            assertTrue(documentLibraryPageCL.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
            assertTrue(documentLibraryPageCL.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of workflow");

            // --- Step 13 ---
            // --- Step action ---
            // Cloud Open Task History page.
            // --- Expected results ---
            // Details page is opened. The following title is displayed: "Details: Cloud Review Task test message (Start Review)"

            TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName).render();

            header = "Details: " + workFlowName + " (Review)";
            assertEquals(taskHistoryPage.getPageHeader(), header);

            // --- Step 14 ---
            // --- Step action ---
            // Verify 'General Info' section.
            // --- Expected results ---
            // The following data is displayed: Title: Hybrid Review And Approve Process Description: Hybrid Review And Approve Process Started by:
            // Administrator admin Due: (None) Completed: in progress Started: Thu 12 Sep 2013 17:17:42 Priority: Medium Status: Task is in Progress Message:
            // Cloud Review Task test message

            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getTitle(), WorkFlowTitle.HYBRID_REVIEW);
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getDescription(), WorkFlowDescription.REQUEST_DOCUMENT_APPROVAL);
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStartedBy(), getUserFullName(cloudUser));
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getDueDateString(), NONE);
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getCompleted(), "<in progress>");
            assertTrue(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStartDate().isBeforeNow());
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getPriority(), Priority.MEDIUM);
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.TASK_IN_PROGRESS);
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getMessage(), workFlowName);

            // --- Step 15 ---
            // --- Step action ---
            // Verify 'More Info' section.
            // --- Expected results ---
            // The following data is displayed: Send Email Notifications: No

            // TODO Send Email Notifications should be YES, Test case should be updated

            assertEquals(taskHistoryPage.getWorkFlowDetailsMoreInfo().getNotification(), SendEMailNotifications.YES);

            // --- Step 16 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following data is displayed: Send Email Notifications: No

            items = taskHistoryPage.getWorkFlowItems();
            assertEquals(items.size(), 1);
            assertEquals(items.get(0).getItemName(), fileName);

            // --- Step 17 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: Review Administrator admin (None) Not Yet Started

            List<WorkFlowDetailsCurrentTask> currentTaskList = taskHistoryPage.getCurrentTasksList();

            assertEquals(currentTaskList.size(), 1);
            assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.REVIEW);
            assertEquals(currentTaskList.get(0).getAssignedTo(), getUserFullName(cloudUser));
            assertEquals(currentTaskList.get(0).getDueDateString(), NONE);
            assertEquals(currentTaskList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // --- Step 18 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed: Start Review Administrator admin Thu 12 Sep 2013 17:17:42 Task Done

            historyList = taskHistoryPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 1);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_REVIEW);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser));
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(0).getComment(), "");

            // --- Step 19 ---
            // --- Step action ---
            // Cloud Verify My Tasks page.
            // --- Expected results ---
            // A new active task, e.g. "Cloud Review Task test message", is present.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // --- Step 20 ---
            // --- Step action ---
            // Open the task details.
            // --- Expected results ---
            // Performed correctly. Information details are displayed. The title is "Details: Cloud Review Task test message (Review)". Edit button is present
            // under the information details section.

            TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));

            header = "Details: " + workFlowName + " (Review)";
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), header);
            assertTrue(taskDetailsPage.isEditButtonPresent());

            // --- Step 21 ---
            // --- Step action ---
            // Verify 'Info' section.
            // --- Expected results ---
            // The following data is displayed: Message: Cloud Review Task test message Owner: Administrator admin Priority: Medium Due: (None) Identifier:
            // 101713

            TaskInfo taskInfo = taskDetailsPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(cloudUser));
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            assertEquals(taskInfo.getDueDateString(), NONE);
            assertNotNull(taskInfo.getIdentifier());

            // --- Step 22 ---
            // --- Step action ---
            // Verify 'Progress' section.
            // --- Expected results ---
            // The following data is displayed: Status: Not Yet Started

            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // --- Step 23 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following item is displayed: test1.txt

            List<TaskItem> taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);

            // --- Step 24 ---
            // --- Step action ---
            // Verify 'Response' section.
            // --- Expected results ---
            // The following data is displayed: Comment: (None)
            assertEquals(taskDetailsPage.getComment(), NONE);

            // --- Step 25 ---
            // --- Step action ---
            // Cloud Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.getTitle().contains("Edit Task"));

            // --- Step 26 ---
            // --- Step action ---
            // Verify the available controls on Edit Task page.
            // --- Expected results ---
            // The following additional controls are present: Status drop-down list View More Actions button for the document Comment field Approve button
            // Reject button Save and close button Cancel button

            List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();
            assertEquals(statusOptions.size(), TaskStatus.values().length);

            taskItems = editTaskPage.getTaskItem(fileName);
            assertTrue(taskItems.get(0).getViewMoreActionsLink().getDescription().contains("View More Actions"));
            assertTrue(editTaskPage.isCommentTextAreaDisplayed());
            assertTrue(editTaskPage.isButtonsDisplayed(Button.APPROVE));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.REJECT));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.SAVE_AND_CLOSE));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.CANCEL));

            // --- Step 27 ---
            // --- Step action ---
            // Verify the Status drop-down list.
            // --- Expected results ---
            // The following values are available: Not yet started (set by default) In Progress On Hold Canceled Completed

            assertTrue(statusOptions.containsAll(getTaskStatusList()));

            // --- Step 28 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'In Progress'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);

            // --- Step 29 ---
            // --- Step action ---
            // Add any data into the Comment field, e.g. "test comment".
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");

            // --- Step 30 ---
            // --- Step action ---
            // Click on Cancel button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. No data was changed. Comment: (None)

            taskDetailsPage = editTaskPage.selectCancelButton().render();
            assertEquals(taskDetailsPage.getComment(), NONE);

            // --- Step 31 ---
            // --- Step action ---
            // Repeat steps 25-29.
            // --- Expected results ---
            // Performed correctly.

            taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");

            // --- Step 32 ---
            // --- Step action ---
            // Click on Save and Close button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.

            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
            assertEquals(taskDetailsPage.getComment(), "test comment");

            // --- Step 33 ---
            // --- Step action ---
            // Cloud: Select Edit button, select Task Status as Completed, specify any comment, e.g. test comment and click on Approve button
            // --- Expected results ---
            // The task is completed. It moves to the Completed Tasks filter.

            editTaskPage = taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            editTaskPage.enterComment("test comment");
            taskDetailsPage = editTaskPage.selectApproveButton().render();
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).selectCompletedTasks().render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // --- Step 34 ---
            // --- Step action ---
            // Cloud Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Cloud Review Task test message", is present in the Completed Tasks filter.

            // same as previous step

            // --- Step 35 ---
            // --- Step action ---
            // Open Task Details page.
            // --- Expected results ---
            // Details page is opened.

            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));

            // --- Step 36 ---
            // --- Step action ---
            // Verify the changed data.
            // --- Expected results ---
            // The following changes are present: Edit button is absent under the information details section. Status: 'Completed' in the Progress section
            // Comment: 'test comment edited (Approved)' in the Response section

            assertTrue(!taskDetailsPage.isEditButtonPresent());
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
            // TODO modify in TestLink the step about the comment
            assertEquals(taskDetailsPage.getComment(), "test comment");

            // --- Step 37 ---
            // --- Step action ---
            // Cloud Open Task History page.
            // --- Expected results ---
            // Task History Page is opened

            taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName).render();
            assertTrue(taskHistoryPage.isBrowserTitle("Task History"));

            // --- Step 38 ---
            // --- Step action ---
            // Verify the changed data.
            // --- Expected results ---
            // The following changes are present: Completed: Thu 12 Sep 2013 20:26:03 in the General Info section Status: Task is Complete in the General Info
            // section

            assertTrue(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getCompletedDate().isBeforeNow());
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.TASK_COMPLETE);

            // --- Step 39 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks

            currentTaskList = taskHistoryPage.getCurrentTasksList();

            assertEquals(currentTaskList.size(), 0);
            // TODO verify id No Tasks means size == 0
            // assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.TASK);

            // --- Step 40 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed: Workflow Task Administrator admin Thu 12 Sep 2013 20:26:02 Task Done test comment edited Start Review
            // Administrator admin Thu 12 Sep 2013 17:17:42 Task Done

            // TODO Modify in TestLink the data displayed in History section

            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(0).getType(), WorkFlowHistoryType.REVIEW);
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(0).getCompletedBy(), getUserFullName(cloudUser));
            assertTrue(taskHistoryPage.getWorkFlowHistoryList().get(0).getCompletedDate().isBeforeNow());
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(0).getOutcome(), WorkFlowHistoryOutCome.APPROVED);
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(0).getComment(), "test comment");

            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(1).getType(), WorkFlowHistoryType.START_REVIEW);
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(1).getCompletedBy(), getUserFullName(cloudUser));
            assertTrue(taskHistoryPage.getWorkFlowHistoryList().get(1).getCompletedDate().isBeforeNow());
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(taskHistoryPage.getWorkFlowHistoryList().get(1).getComment(), "");
            ShareUser.logout(hybridDrone);

            // --- Step 41 ---
            // --- Step action ---
            // OP Verify My Tasks page.
            // --- Expected results ---
            // An active task, e.g. "Cloud Review Task test message", is present in the Active Tasks filter.

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD).render();
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render(4000);
            findTasks(drone);

            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // --- Step 42 ---
            // --- Step action ---
            // Open the task details.
            // --- Expected results ---
            // Performed correctly. Information details are displayed. The title is
            // "Details: Cloud Review Task test message (Document was approved on the cloud)". Edit button is present under the information details section.

            taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName);
            header = "Details: " + workFlowName + " (Document was approved on the cloud)";
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), header);
            assertTrue(taskDetailsPage.isEditButtonPresent());

            // --- Step 43 ---
            // --- Step action ---
            // Verify 'Info' section.
            // --- Expected results ---
            // The following data is displayed: Message: Cloud Review Task test message Owner: Administrator Priority: Medium Due: (None) Identifier: 416

            assertEquals(taskDetailsPage.getTaskDetailsInfo().getMessage(), workFlowName);
            assertEquals(taskDetailsPage.getTaskDetailsInfo().getOwner(), getUserFullName(opUser));
            assertEquals(taskDetailsPage.getTaskDetailsInfo().getPriority(), Priority.MEDIUM);
            assertEquals(taskDetailsPage.getTaskDetailsInfo().getDueDateString(), NONE);
            assertNotNull(taskDetailsPage.getTaskDetailsInfo().getIdentifier());

            // --- Step 44 ---
            // --- Step action ---
            // Verify 'Progress' section.
            // --- Expected results ---
            // The following data is displayed: Status: Not Yet Started

            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // --- Step 45 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following item is displayed: test1.txt

            taskItems = taskDetailsPage.getTaskItems();
            assertEquals(items.size(), 1);
            assertEquals(items.get(0).getItemName(), fileName);

            // --- Step 46 ---
            // --- Step action ---
            // Verify 'Response' section.
            // --- Expected results ---
            // The following data is displayed: Required approval percentage: 50 Actual approval percentage: 100 Comments: test comment (Approved)

            assertEquals(taskDetailsPage.getRequiredApprovalPercentage(), 50);
            assertEquals(taskDetailsPage.getActualApprovalPercentage(), 100);
            // TODO update the step from TestLink regarding comment --- add user name
            assertEquals(taskDetailsPage.getComment(), getUserFullName(cloudUser) + ": test comment  (Approved)");

            // --- Step 47 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            editTaskPage = taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"));

            // --- Step 48 ---
            // --- Step action ---
            // Verify the available controls on Edit Task page.
            // --- Expected results ---
            // The following additional controls are present: Status drop-down list View More Actions button for the document Task Done button Save and close
            // button Cancel button

            statusOptions = editTaskPage.getStatusOptions();
            assertEquals(statusOptions.size(), TaskStatus.values().length);

            taskItems = editTaskPage.getTaskItem(fileName);
            assertTrue(taskItems.get(0).getViewMoreActionsLink().getDescription().contains("View More Actions"));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.TASK_DONE));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.SAVE_AND_CLOSE));
            assertTrue(editTaskPage.isButtonsDisplayed(Button.CANCEL));

            // --- Step 49 ---
            // --- Step action ---
            // Verify the Status drop-down list.
            // --- Expected results ---
            // The following values are available: Not yet started (set by default) In Progress On Hold Canceled Completed

            assertTrue(statusOptions.containsAll(getTaskStatusList()));

            // --- Step 50 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'In Progress'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);

            // --- Step 51 ---
            // --- Step action ---
            // Click on Cancel button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. No data was changed. Comment: (None)

            // TODO update test in TestLink --- remove the comment part
            taskDetailsPage = editTaskPage.selectCancelButton().render();

            assertTrue(taskDetailsPage.isTitlePresent("Task Details"));

            // --- Step 52 ---
            // --- Step action ---
            // Repeat steps 47-50.
            // --- Expected results ---
            // Performed correctly.

            taskDetailsPage.selectEditButton().render();
            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);

            // --- Step 53 ---
            // --- Step action ---
            // Click on Save and Close button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.

            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);

            // --- Step 54 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            taskDetailsPage.selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"));

            // --- Step 55 ---
            // --- Step action ---
            // Specify any value in the Status drop-down list, e.g. 'Completed'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.COMPLETED);

            // --- Step 56 ---
            // --- Step action ---
            // Click on Task Done button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.

            taskDetailsPage = editTaskPage.selectTaskDoneButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            // /what data???

            // --- Step 57 ---
            // --- Step action ---
            // Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Cloud Review Task test message", is present in the Completed Tasks filter.

            MyTasksPage myTasksPage2 = ShareUserWorkFlow.navigateToMyTasksPage(drone).selectCompletedTasks().render();
            assertTrue(myTasksPage2.isTaskPresent(workFlowName));

            // --- Step 58 ---
            // --- Step action ---
            // Verify Workflows I've started page.
            // --- Expected results ---
            // A completed workflow, e.g. "Cloud Review Task test message", is present in the Completed Workflows filter.

            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).selectCompletedWorkFlows().render();
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

            // --- Step 59 ---
            // --- Step action ---
            // Verify Workflow's Details page.
            // --- Expected results ---
            // The following changes are present: Completed: Thu 12 Sep 2013 20:26:03 in the General Info section Status: Workflow is Complete in the General
            // Info section Delete Workflow button

            workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, workFlowName).render();

            assertTrue(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getCompletedDate().isBeforeNow());
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.WORKFLOW_COMPLETE);
            assertTrue(workFlowDetailsPage.isDeleteWorkFlowButtonDisplayed());

            // --- Step 60 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks

            assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed());

            // --- Step 61 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed: Document was approved on the cloud admin Thu 12 Sep 2013 21:02:27 Task Done Start a task or review on Alfresco
            // Cloud admin Thu 12 Sep 2013 17:15:07 Task Done

            historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 2);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.DOCUMENT_WAS_APPROVED_ON_CLOUD);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(opUser));
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(0).getComment(), "");

            assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
            assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(opUser));
            assertEquals(historyList.get(1).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(1).getComment(), "");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15625() throws Exception
    {

        fileName = getFileName(testName) + "-15625" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = "Cloud Review Task test message" + testName + "-15625CL";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.openSitesDocumentLibrary(drone, opSite).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(50);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        ShareUser.logout(drone);
    }

    /**
     * AONE-15625:Reject action
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15625() throws Exception
    {

    
        String workFlowName = "Cloud Review Task test message" + testName + "-15625CL";
        fileName = getFileName(testName) + "-15625" + ".txt";

        try
        {

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Specify any comment, e.g. test comment.
            // --- Expected results ---
            // Performed correctly.

            EditTaskPage editTaskPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).navigateToEditTaskPage(workFlowName).render();
            editTaskPage.enterComment("test comment");
            assertEquals(editTaskPage.readCommentFromCommentTextArea(), "test comment");

            // --- Step 2 ---
            // --- Step action ---
            // Click on Reject button.
            // --- Expected results ---
            // The task is completed. It moves to the Completed Tasks filter.

            MyTasksPage myTasksPage = editTaskPage.selectRejectButton().render();
            assertTrue(myTasksPage.selectCompletedTasks().render().isTaskPresent(workFlowName));

            ShareUser.logout(hybridDrone);

            // --- Step 3 ---
            // --- Step action ---
            // OP Open the received task details.
            // --- Expected results ---
            // The following data is present:
            // Title: 'Details: Cloud Review Task test message (Document was rejected on the cloud)'
            // Response
            // Required approval percentage: 50
            // Actual approval percentage: 100
            // Comments: test comment (Rejected)
            // TODO modify in TestLink the actual approval percentage to 0, since the task was rejected
            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            findTasks(drone);

            TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName);

            assertEquals(taskDetailsPage.getTaskDetailsHeader(), "Details: " + workFlowName + " (Document was rejected on the cloud)");
            assertEquals(taskDetailsPage.getRequiredApprovalPercentage(), 50);
            assertEquals(taskDetailsPage.getActualApprovalPercentage(), 0);
            assertEquals(taskDetailsPage.getComment(), getUserFullName(cloudUser) + ": " + "test comment  (Rejected)");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15626() throws Exception
    {

        fileName = getFileName(testName) + "-15626" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = "Cloud Review Task test message" + testName + "-15626CL";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.openSitesDocumentLibrary(drone, opSite).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(50);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        EditTaskPage editTaskPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).navigateToEditTaskPage(workFlowName).render();

        editTaskPage.enterComment("test comment");
        editTaskPage.selectSaveButton().render();

        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15626:Cloud Review Task - Task Done
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15626() throws Exception
    {

    
        String workFlowName = "Cloud Review Task test message" + testName + "-15626CL";
        fileName = getFileName(testName) + "-15626" + ".txt";

        try
        {

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            EditTaskPage editTaskPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).navigateToEditTaskPage(workFlowName).render();
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
            // Click on Approve (or Reject) button.
            // --- Expected results ---
            // Task is closed. It is disappeared from the Active Tasks.

            MyTasksPage myTasksPage = editTaskPage.selectApproveButton().render();

            assertTrue(!myTasksPage.selectActiveTasks().render().isTaskPresent(workFlowName));

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15627() throws Exception
    {

        fileName = getFileName(testName) + "-15627" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = "Cloud Review Task test message" + testName + "-15627CL";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.openSitesDocumentLibrary(drone, opSite).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(100);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
//        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        EditTaskPage editTaskPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).navigateToEditTaskPage(workFlowName).render();

        editTaskPage.enterComment("test comment");
        editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);

        MyTasksPage myTasksPage = editTaskPage.selectSaveButton().render();
        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
        assertEquals(taskDetailsPage.getComment(), "test comment");

        editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.enterComment("test comment edited");
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.selectApproveButton().render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
        assertEquals(taskDetailsPage.getComment(), "test comment edited");

        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15627:Cloud Review Task - Task Done - Details
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15627() throws Exception
    {

    
        String workFlowName = "Cloud Review Task test message" + testName + "-15627CL";
        fileName = getFileName(testName) + "-15627" + ".txt";

        try
        {

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // Cloud Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Cloud Review Task test message", is present in the Completed Tasks filter.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
            assertTrue(myTasksPage.selectCompletedTasks().isTaskPresent(workFlowName));

            // --- Step 2 ---
            // --- Step action ---
            // Open Task Details page.
            // --- Expected results ---
            // Details page is opened.

            TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();
            assertTrue(taskDetailsPage.isTitlePresent("Task Details"));

            // --- Step 3 ---
            // --- Step action ---
            // Verify the changed data.
            // --- Expected results ---
            // The following changes are present:
            // Edit button is absent under the information details section.
            // Status: 'Completed' in the Progress section
            // Comment: 'test comment edited (Approved)' in the Response section

            // TODO update TestLink with comment: "test comment edited"
            assertTrue(!taskDetailsPage.isEditButtonPresent());
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
            assertEquals(taskDetailsPage.getComment(), "test comment edited");

            // --- Step 4 ---
            // --- Step action ---
            // Cloud Open Task History page.
            // --- Expected results ---
            // Details page is opened."

            TaskHistoryPage taskHistoryPage = taskDetailsPage.selectTaskHistoryLink().render();
            assertTrue(taskHistoryPage.isBrowserTitle("Task History"));

            // --- Step 5 ---
            // --- Step action ---
            // Verify the changed data.
            // --- Expected results ---
            // The following changes are present:
            // Completed: Thu 12 Sep 2013 20:26:03 in the General Info section
            // Status: Task is Complete in the General Info section

            assertTrue(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getCompletedDate().isBeforeNow());
            assertEquals(taskHistoryPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.TASK_COMPLETE);

            // --- Step 6 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks

            assertTrue(taskHistoryPage.isNoTasksMessageDisplayed());

            // --- Step 7 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed:
            // Workflow Task Administrator admin Thu 12 Sep 2013 20:26:02 Task Done test comment edited
            // Start Review Administrator admin Thu 12 Sep 2013 17:17:42 Task Done

            // TODO update the TestLink with first history entry: the type should be Review, the outcome should be Approved

            List<WorkFlowDetailsHistory> historyList = taskHistoryPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 2);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.REVIEW);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser));
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.APPROVED);
            assertEquals(historyList.get(0).getComment(), "test comment edited");

            assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_REVIEW);
            assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(cloudUser));
            assertEquals(historyList.get(1).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(1).getComment(), "");

            ShareUser.logout(hybridDrone);

            // --- Step 8 ---
            // --- Step action ---
            // OPOpen workflow details page.
            // --- Expected results ---
            // Details page is opened."

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
            WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render().selectWorkFlow(workFlowName).render();
            assertTrue(workFlowDetailsPage.isTitlePresent("Workflow Details"));

            // --- Step 9 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed:
            // Verify task was completed on the cloud admin (None) Not Yet Started
            // TODO task was approved
            
            List<WorkFlowDetailsCurrentTask> workFlowDetailsCurrentTask = workFlowDetailsPage.getCurrentTasksList();
            assertEquals(workFlowDetailsCurrentTask.get(0).getTaskType(), CurrentTaskType.DOCUMENT_WAS_APPROVED_ON_CLOUD);
            assertEquals(workFlowDetailsCurrentTask.get(0).getAssignedTo(), getUserFullName(opUser));
            assertEquals(workFlowDetailsCurrentTask.get(0).getDueDateString(), NONE);
            assertEquals(workFlowDetailsCurrentTask.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED);


            // --- Step 10 ---
            // --- Step action ---
            // OP Verify My Tasks page.
            // --- Expected results ---
            // An active task, e.g. "Cloud Review Task test message", is present in the Active Tasks filter.

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).selectActiveTasks().render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName));
                        
            // --- Step 11 ---
            // --- Step action ---
            // Open the task details.
            // --- Expected results ---
            // Performed correctly. Information details are displayed. The title is
            // "Details: Cloud Review Task test message (Document was approved on the cloud)". Edit button is present under the information details section.

            taskDetailsPage =  myTasksPage.selectViewTasks(workFlowName).render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));

            String header = "Details: " + workFlowName + " (Document was approved on the cloud)";
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), header);
            assertTrue(taskDetailsPage.isEditButtonPresent());
            
            // --- Step 12 ---
            // --- Step action ---
            // Verify 'Info' section.
            // --- Expected results ---
            // The following data is displayed:
            // Message: Cloud Review Task test message
            // Owner: Administrator
            // Priority: Medium
            // Due: (None)
            // Identifier: 416

            TaskInfo taskInfo = taskDetailsPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(opUser));
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            assertEquals(taskInfo.getDueDateString(), NONE);
            assertNotNull(taskInfo.getIdentifier());
            
            // --- Step 13 ---
            // --- Step action ---
            // Verify 'Progress' section.
            // --- Expected results ---
            // The following data is displayed:
            // Status: Not Yet Started

            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);
     
            // --- Step 14 ---
            // --- Step action ---
            // Verify 'Items' section.
            // --- Expected results ---
            // The following item is displayed: test1.txt

            List<TaskItem> taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);
            
            // --- Step 15 ---
            // --- Step action ---
            // Verify 'Response' section.
            // --- Expected results ---
            // The following data is displayed:
            // Comment: test comment edited

            assertEquals(taskDetailsPage.getComment(), getUserFullName(cloudUser) + ": " + "test comment edited  (Approved)");
            
            
            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15628() throws Exception
    {

        fileName = getFileName(testName) + "-15628" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = "Cloud Review Task test message" + testName + "-15628CL";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.openSitesDocumentLibrary(drone, opSite).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(100);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // edit task with comment an
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
        findTasks(hybridDrone);
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();

        editTaskPage.enterComment("test comment");
        editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);

        myTasksPage = editTaskPage.selectSaveButton().render();
        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
        assertEquals(taskDetailsPage.getComment(), "test comment");

        editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.enterComment("test comment edited");
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.selectApproveButton().render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
        assertEquals(taskDetailsPage.getComment(), "test comment edited");

        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15628:Cloud Review Task - Task Done - Edit Task Details (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15628() throws Exception
    {

    
        String workFlowName = "Cloud Review Task test message" + testName + "-15628CL";
        fileName = getFileName(testName) + "-15628" + ".txt";

        try
        {

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            findTasks(drone);
            EditTaskPage editTaskPage = myTasksPage.selectViewTasks(workFlowName).selectEditButton().render();
            assertTrue(editTaskPage.isBrowserTitle("Edit Task"));

            // --- Step 2 ---
            // --- Step action ---
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
            assertTrue(editTaskPage.isButtonsDisplayed(Button.CANCEL));

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
            // Specify any value in the Status drop-down list, e.g. 'In Progress'.
            // --- Expected results ---
            // Performed correctly.

            editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);
            assertEquals(editTaskPage.getSelectedStatusFromDropDown(), TaskStatus.INPROGRESS);

            // --- Step 5 ---
            // --- Step action ---
            // Click on Cancel button.
            // --- Expected results ---
            // Edit Task page is closed. Task Details are displayed. No data was changed.
            // Comment: (None)

            TaskDetailsPage taskDetailsPage = editTaskPage.selectCancelButton().render();
            assertTrue(taskDetailsPage.isTitlePresent("Task Details"));

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
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.

            taskDetailsPage = editTaskPage.selectSaveButton().render();
            assertTrue(taskDetailsPage.isBrowserTitle("Task Details"));
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15629() throws Exception
    {

        fileName = getFileName(testName) + "-15629" + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String workFlowName = "Cloud Review Task test message" + testName + "-15629CL";

        List<String> userNames = new ArrayList<String>();
        userNames.add(cloudUser);

        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.openSitesDocumentLibrary(drone, opSite).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(100);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // edit task with comment an
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone).render();
        findTasks(hybridDrone);
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName).render();

        editTaskPage.enterComment("test comment");
        editTaskPage.selectStatusDropDown(TaskStatus.INPROGRESS);

        myTasksPage = editTaskPage.selectSaveButton().render();
        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
        assertEquals(taskDetailsPage.getComment(), "test comment");

        editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.enterComment("test comment edited");
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.selectApproveButton().render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
        assertEquals(taskDetailsPage.getComment(), "test comment edited");

        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15629:Cloud Review Task - Task Done - Complete (OP)
     */
    @Test(groups = "Hybrid", enabled = true)
    public void AONE_15629() throws Exception
    {

    
        String workFlowName = "Cloud Review Task test message" + testName + "-15629CL";
        fileName = getFileName(testName) + "-15629" + ".txt";

        try
        {

            ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

            // --- Step 1 ---
            // --- Step action ---
            // OP Click on Edit button.
            // --- Expected results ---
            // The button is pressed. Edit Task page is opened.

            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            findTasks(drone);
            EditTaskPage editTaskPage = myTasksPage.selectViewTasks(workFlowName).selectEditButton().render();
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
            // Edit Task page is closed. Task Details are displayed. The specified data was changed.

            TaskDetailsPage taskDetailsPage = editTaskPage.selectTaskDoneButton().render();
            taskDetailsPage.isBrowserTitle("Task Details");

            // --- Step 4 ---
            // --- Step action ---
            // Verify My Tasks page.
            // --- Expected results ---
            // A completed task, e.g. "Cloud Review Task test message", is present in the Completed Tasks filter.

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
            assertTrue(myTasksPage.selectCompletedTasks().render().isTaskPresent(workFlowName));

            // --- Step 5 ---
            // --- Step action ---
            // Verify Workflows I've started page.
            // --- Expected results ---
            // A completed workflow, e.g. "Cloud Review Task test message", is present in the Completed Workflows filter.

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone).render().selectCompletedWorkFlows().render();
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

            // --- Step 6 ---
            // --- Step action ---
            // Verify Workflow's Details page.
            // --- Expected results ---
            // The following changes are present:
            // Completed: Thu 12 Sep 2013 20:26:03 in the General Info section
            // Status: Workflow is Complete in the General Info section
            // Delete Workflow button

            WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, workFlowName).render();

            assertTrue(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getCompletedDate().isBeforeNow());
            assertEquals(workFlowDetailsPage.getWorkFlowDetailsGeneralInfo().getStatus(), WorkFlowStatus.WORKFLOW_COMPLETE);
            assertTrue(workFlowDetailsPage.isDeleteWorkFlowButtonDisplayed());

            // --- Step 7 ---
            // --- Step action ---
            // Verify 'Current Tasks' section.
            // --- Expected results ---
            // The following data is displayed: No tasks

            assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed());

            // --- Step 8 ---
            // --- Step action ---
            // Verify 'History' section.
            // --- Expected results ---
            // The following data is displayed:
            // Document was approved on the cloud admin Thu 12 Sep 2013 21:02:27 Task Done
            // Start a task or review on Alfresco Cloud admin Thu 12 Sep 2013 17:15:07 Task Done

            List<WorkFlowDetailsHistory> historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 2);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.DOCUMENT_WAS_APPROVED_ON_CLOUD);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(opUser));
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(0).getComment(), "");

            assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
            assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(opUser));
            assertEquals(historyList.get(1).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(1).getComment(), "");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(hybridDrone, testName + "-HY", t);
            reportError(drone, testName + "-HY", t);
        }

    }

    private void findTasks(WebDrone driver)
    {

        assertTrue(driver.findAndWaitWithRefresh(By.cssSelector("h3 a")).isDisplayed());
    }

}
