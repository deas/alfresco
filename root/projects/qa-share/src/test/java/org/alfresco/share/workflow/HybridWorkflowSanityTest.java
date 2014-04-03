package org.alfresco.share.workflow;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.AssignmentPage;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.CurrentTaskType;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.SendEMailNotifications;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.TaskDetailsType;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDescription;
import org.alfresco.po.share.workflow.WorkFlowDetails;
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
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class HybridWorkflowSanityTest extends AbstractWorkflow
{
    private static final Logger logger = Logger.getLogger(HybridWorkflowSanityTest.class);

    private String testDomain1;
    private String testDomain2;

    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        testDomain1 = "hwsanity1.test";
        testDomain2 = "hwsanity2.test";
    }

    /**
     * ALF-15099:Enable Hybrid Workflow functionality
     * <ul>
     * <li>1) Login as Admin, Select MyTasks from Tasks</li>
     * <li>2) Select Start Workflow button and verify "Cloud Task or Review" workflow displayed in the drop down</li>
     * </ul>
     */
    @Test(groups="Hybrid", enabled = true)
    public void ALF_15099() throws Exception
    {
        try
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            StartWorkFlowPage startWorkFlowPage = ShareUserWorkFlow.selectStartWorkFlowFromMyTasksPage(drone);
            assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.CLOUD_TASK_OR_REVIEW),
                    "Verifying the \"Cloud Task or Review\" workflow is displayed in the dropdown");

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }
    }

    /**
     * ALF-15100:Create Simple Cloud Task ALF-15101:Complete Simple Cloud Task
     * <ul>
     * <li>1) Create User1 (OP)</li>
     * <li>3) Create a cloud user and upgrade the account</li>
     * <li>5) Login as User1, Set Up cloud sync with the cloud user</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15100() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
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

    /**
     * ALF-15100:Create Simple Cloud Task ALF-15101:Complete Simple Cloud Task
     * <ul>
     * <li>1) Login as Cloud User, Create a site and Logout</li>
     * <li>2) Login as User1 (OP), Create a site and Upload a document</li>
     * <li>3) Navigate to WorkFlows I've Started page and select StartWorkflow button</li>
     * <li>4) Select "Cloud Task or Review" from select a workflow dropdown</li>
     * <li>5) Select Simple Cloud Task and fill in the form with Message, Due Date, Task Priority etc.</li>
     * <li>6) Select Cloud Destination, Select Assignee, select content and select "Start Workflow" button</li>
     * <li>7) Verify a task is displayed in Active Workflows list</li>
     * <li>7) Load Site Document library and verify Document is part of workflow</li>
     * <li>8) Verify Document is cloud synced</li>
     * <li>9) Login to Cloud, open site document library and verify the synced document is displayed</li>
     * <li>10) Navigate to MyTasks page and verify a task is displayed in Active Tasks list</li>
     * <li>11) Verify the task details are accurate</li>
     * <li>12) Edit task and mark it as complete</li>
     * <li>13) Verify the task is disappeared from Active Tasks list</li>
     * <li>14) Select Completed and verify the task is displayed and the task details re correct</li>
     * <li>15) Login as OP user and verify there is a task displayed under Active task with the workflow name</li>
     * <li>16) Verify the task details are correct</li>
     * <li>17) Edit the task and mark it as completed</li>
     * <li>18) Verify the task is disappeared from Active Tasks list</li>
     * <li>19) Select Completed and Verify the task is displayed and the task details are correct</li>
     * <li>20) Navigate to Workflows I've Started page and verify the workflow doesn't exists in Active WorkFlows</li>
     * <li>21) Select Completed and verify the workflow is displayed and the details are correct</li>
     * </ul>
     */
    @Test(groups="Hybrid", enabled = true)
    public void ALF_15100() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String uniqueString = String.valueOf(System.currentTimeMillis()).substring(7,12);
        String opSiteName = getSiteName(testName) + uniqueString + "-OP";
        String cloudSiteName = getSiteName(testName) + uniqueString + "-CL";
        String fileName = getFileName(testName) + uniqueString + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + uniqueString + "-WF";
        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);

        String cloudCommentInProgress = testName + "-Cloud Comment InProgress";
        String cloudComment = testName + "-Cloud Comment";

        // Login as User1 (Cloud) and Create a Site
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site and Upload a document
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Start Simple Cloud Task workflow
//        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        // Verify "Simple Cloud Task" is selected
        assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK));

        // Fill up Task Details
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Fill the form details and start workflow
        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the document is part of the workflow
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");

        // Verify the document is synced
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");

        // Verify the Sync Status
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

        drone.refresh();
        documentLibraryPage.render();

        // Verify Sync location is displayed correctly.
        SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();
        assertEquals(syncInfoPage.getCloudSyncLocation(), DOMAIN_HYBRID + ">" + cloudSiteName + ">" + DEFAULT_FOLDER_NAME);

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        // Verify workflow details
        List<WorkFlowDetails> workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

        assertEquals(workFlowDetails.size(), 1);
        assertEquals(workFlowDetails.get(0).getWorkFlowName(), formDetails.getMessage(), "Verifying workflow name");
        assertEquals(workFlowDetails.get(0).getDue(), getDueDate(formDetails.getDueDate()), "Verifying workflow due date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Verifying Workflow type");
        assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Verifying Workflow Description");

        // Select the workflow to view WorkFlow Details
        WorkFlowDetailsPage workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

        // Verify WorkFlow Details page header
        assertEquals(workFlowDetailsPage.getPageHeader(), getWorkFlowDetailsHeader(workFlowName));

        // Verify WorkFlow Details General Info section
        WorkFlowDetailsGeneralInfo generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();

        assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
        assertEquals(generalInfo.getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW);
        assertEquals(generalInfo.getStartedBy(), getUserFullName(user1));
        assertEquals(getLocalDate(generalInfo.getDueDate()), getLocalDate(dueDate));
        assertEquals(generalInfo.getCompleted(), "<in progress>");
        assertEquals(getLocalDate(generalInfo.getStartDate()), getToDaysLocalDate());
        assertEquals(generalInfo.getPriority(), formDetails.getTaskPriority());
        assertEquals(generalInfo.getStatus(), WorkFlowStatus.WORKFLOW_IN_PROGRESS);
        assertEquals(generalInfo.getMessage(), workFlowName);

        // Verify WorkFlow Details More Info section
        WorkFlowDetailsMoreInfo moreInfo = workFlowDetailsPage.getWorkFlowDetailsMoreInfo();

        assertEquals(moreInfo.getType(), formDetails.getTaskType());
        assertEquals(moreInfo.getDestination(), DOMAIN_HYBRID);
        assertEquals(moreInfo.getAfterCompletion(), formDetails.getContentStrategy());
        assertFalse(moreInfo.isLockOnPremise());
        assertEquals(moreInfo.getAssignmentList().size(), 1);
        assertEquals(moreInfo.getAssignmentList().get(0), getUserFullNameWithEmail(cloudUser, cloudUser));

        // Verify WorkFlow Details Item section
        List<WorkFlowDetailsItem> items = workFlowDetailsPage.getWorkFlowItems();
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getItemName(), fileName);
        assertEquals(items.get(0).getDescription(), NONE);
        assertEquals(getLocalDate(items.get(0).getDateModified()), getToDaysLocalDate());

        // Verify WorkFlow Details Current Tasks table displays "No Tasks"
        assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed());

        // Verify WorkFlow Details History List
        List<WorkFlowDetailsHistory> historyList = workFlowDetailsPage.getWorkFlowHistoryList();

        assertEquals(historyList.size(), 1);
        assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
        assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(user1));
        assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate());
        assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
        assertEquals(historyList.get(0).getComment(), "");



        ShareUser.logout(drone);

        // Login as Cloud User,
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Open Site document library and verify the file is a part of workflow
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSiteName);

        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task Details are displayed correctly
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");

        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.TASK, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Task", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");

        //Task History Verifications
        TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName);

        // Verify Task History Page Header
        assertEquals(taskHistoryPage.getPageHeader(), getSimpleCloudTaskDetailsHeader(workFlowName));

        // Verify Task History Page General Info Section
        generalInfo = taskHistoryPage.getWorkFlowDetailsGeneralInfo();

        assertEquals(generalInfo.getTitle(), WorkFlowTitle.HYBRID_TASK);
        assertEquals(generalInfo.getDescription(), WorkFlowDescription.ASSIGN_NEW_TASK_TO_SOMEONE_ON_THE_CLOUD);
        assertEquals(generalInfo.getStartedBy(), getUserFullName(cloudUser));
        assertEquals(getLocalDate(generalInfo.getDueDate()), getLocalDate(dueDate));
        assertEquals(generalInfo.getCompleted(), "<in progress>");
        assertEquals(getLocalDate(generalInfo.getStartDate()), getToDaysLocalDate());
        assertEquals(generalInfo.getPriority(), formDetails.getTaskPriority());
        assertEquals(generalInfo.getStatus(), WorkFlowStatus.TASK_IN_PROGRESS);
        assertEquals(generalInfo.getMessage(), workFlowName);

        // Verify Task History Page More Info Section
        moreInfo = taskHistoryPage.getWorkFlowDetailsMoreInfo();
        assertEquals(moreInfo.getNotification(), SendEMailNotifications.YES);

        // Verify Task History Page Item Details
        items = taskHistoryPage.getWorkFlowItems();
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getItemName(), fileName);
        assertEquals(items.get(0).getDescription(), NONE);
        assertEquals(getLocalDate(items.get(0).getDateModified()), getToDaysLocalDate());

        // Verify Task History Page Current Tasks List
        List<WorkFlowDetailsCurrentTask> currentTaskList = taskHistoryPage.getCurrentTasksList();

        assertEquals(currentTaskList.size(), 1);
        assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.TASK);
        assertEquals(currentTaskList.get(0).getAssignedTo(), getUserFullName(cloudUser));
        assertEquals(currentTaskList.get(0).getDueDate().toLocalDate(), dueDate.toLocalDate());
        assertEquals(currentTaskList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED);

        // Verify Task History Page History List
        historyList = taskHistoryPage.getWorkFlowHistoryList();

        assertEquals(historyList.size(), 1);
        assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.TASK);
        assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser));
        assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate());
        assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
        assertEquals(historyList.get(0).getComment(), "");

        // Navigate to Task Details Page
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(hybridDrone, workFlowName);

        // Verify Task Details Page Header
        assertEquals(taskDetailsPage.getTaskDetailsHeader(), getSimpleCloudTaskDetailsHeader(workFlowName));

        // Verify Task Info Task Details Page
        TaskInfo taskInfo = taskDetailsPage.getTaskDetailsInfo();

        assertEquals(taskInfo.getMessage(), workFlowName);
        assertEquals(taskInfo.getOwner(), getUserFullName(user1));
        assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
        assertEquals(getLocalDate(taskInfo.getDueDate()), getLocalDate(dueDate));
        // TODO - Due Date format is incorrect -ALF-20755
        // assertEquals(taskInfo.getDueDateString(), dueDate.toString("E dd MMM yyy"));
        assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

        // Verify 'Progress' section (Status: Not Yet Started)
        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

        // Verify Item Details
        List<TaskItem> taskItems = taskDetailsPage.getTaskItems();
        assertEquals(taskItems.size(), 1);
        assertEquals(taskItems.get(0).getItemName(), fileName);
        assertEquals(taskItems.get(0).getDescription(), NONE);
        assertEquals(getLocalDate(taskItems.get(0).getDateModified()), getToDaysLocalDate());

        assertEquals(taskDetailsPage.getComment(), NONE);

        // Navigate to Edit Task Page
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();

        // Edit Task Page - Verify Task Info section
        taskInfo = editTaskPage.getTaskDetailsInfo();

        assertEquals(taskInfo.getMessage(), workFlowName);
        assertEquals(taskInfo.getOwner(), getUserFullName(user1));
        assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
        assertEquals(getLocalDate(taskInfo.getDueDate()), getLocalDate(dueDate));
        assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

        // Edit Task Page - Verify Task Item
        taskItems = taskDetailsPage.getTaskItems();

        assertEquals(taskItems.size(), 1);
        assertEquals(taskItems.get(0).getItemName(), fileName);
        assertEquals(taskItems.get(0).getDescription(), NONE);
        assertEquals(getLocalDate(taskItems.get(0).getDateModified()), getToDaysLocalDate());

        // Verify Status Drop down options
        List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();

        assertEquals(statusOptions.size(), TaskStatus.values().length);
        assertTrue(statusOptions.containsAll(getTaskStatusList()));

        assertFalse(editTaskPage.isReAssignButtonDisplayed(), "Verifying ReAssign button is not displayed (ALF-15238)");

        // Select Task Status as "In-Progress", enter a comment, select Save and verify the Task Status and comment are saved in Task Details Page
        taskDetailsPage = ShareUserWorkFlow.completeTask(hybridDrone, TaskStatus.INPROGRESS, cloudCommentInProgress, EditTaskAction.SAVE).render();
        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
        assertEquals(taskDetailsPage.getComment(), cloudCommentInProgress);

        // Go to Edit Task page, select Task Status as "On-Hold", change the comment and click on Cancel
        taskDetailsPage = ShareUserWorkFlow.completeTaskFromTaskDetailsPage(hybridDrone, TaskStatus.ONHOLD, cloudComment, EditTaskAction.CANCEL).render();
        // Verify the changes are not reflected in Task Details Page
        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
        assertEquals(taskDetailsPage.getComment(), cloudCommentInProgress);

        // Complete the task and verify task is completed (with the comment)
        taskDetailsPage = ShareUserWorkFlow.completeTaskFromTaskDetailsPage(hybridDrone, TaskStatus.COMPLETED, cloudComment, EditTaskAction.TASK_DONE).render();

        assertFalse(taskDetailsPage.isEditButtonPresent());
        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
        assertEquals(taskDetailsPage.getComment(), cloudComment);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed Tasks and verify Task is displayed and the task details are correct.
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        // Verify Task Details in MyTasks Page (Completed Tasks)
        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.TASK, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Task", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        // Navigate to Task History Page
        taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName);

        // Verify General Info section
        generalInfo = taskHistoryPage.getWorkFlowDetailsGeneralInfo();

        assertEquals(generalInfo.getTitle(), WorkFlowTitle.HYBRID_TASK);
        assertEquals(generalInfo.getDescription(), WorkFlowDescription.ASSIGN_NEW_TASK_TO_SOMEONE_ON_THE_CLOUD);
        assertEquals(generalInfo.getStartedBy(), getUserFullName(user1));
        assertEquals(getLocalDate(generalInfo.getDueDate()), getLocalDate(dueDate));
        assertEquals(getLocalDate(generalInfo.getCompletedDate()), getToDaysLocalDate());
        assertEquals(getLocalDate(generalInfo.getStartDate()), getToDaysLocalDate());
        assertEquals(generalInfo.getPriority(), formDetails.getTaskPriority());
        assertEquals(generalInfo.getStatus(), WorkFlowStatus.TASK_COMPLETE);
        assertEquals(generalInfo.getMessage(), workFlowName);

        // Verify More Info section
        moreInfo = taskHistoryPage.getWorkFlowDetailsMoreInfo();

        assertEquals(moreInfo.getNotification(), SendEMailNotifications.YES);

        // Verify Item details
        items = taskHistoryPage.getWorkFlowItems();
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getItemName(), fileName);
        assertEquals(items.get(0).getDescription(), NONE);
        assertEquals(getLocalDate(items.get(0).getDateModified()), getToDaysLocalDate());

        // Verify "Current Tasks" section (Displays "No Tasks")
        assertTrue(taskHistoryPage.isNoTasksMessageDisplayed());

        // Verify "History" section
        historyList = taskHistoryPage.getWorkFlowHistoryList();

        assertEquals(historyList.size(), 2);
        assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.TASK);
        assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser));
        assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate());
        assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
        assertEquals(historyList.get(0).getComment(), cloudComment);

        assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.TASK);
        assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(cloudUser));
        assertEquals(getLocalDate(historyList.get(1).getCompletedDate()), getToDaysLocalDate());
        assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
        assertEquals(historyList.get(1).getComment(), "");

        ShareUser.logout(hybridDrone);

        // Login as OP user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open My Tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify a new task is displayed for OP user
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

        // Verify the task details
        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertNull(taskDetails.getEndDate(), "Verify Workflow End date is NULL as the task is still active");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        // Navigate to Workflows I've started
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

        currentTaskList = workFlowDetailsPage.getCurrentTasksList();
        assertEquals(currentTaskList.size(), 1);
        assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD);
        assertEquals(currentTaskList.get(0).getAssignedTo(), getUserFullName(cloudUser));
        assertEquals(currentTaskList.get(0).getDueDate().toLocalDate(), dueDate.toLocalDate());
        assertEquals(currentTaskList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED);

        // Navigate to Task Details
        taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName);

        taskInfo = taskDetailsPage.getTaskDetailsInfo();

        assertEquals(taskInfo.getMessage(), workFlowName);
        assertEquals(taskInfo.getOwner(), getUserFullName(user1));
        assertEquals(taskInfo.getPriority(), formDetails.getTaskPriority());
        assertEquals(getLocalDate(taskInfo.getDueDate()), getLocalDate(dueDate));
        assertTrue(taskInfo.getDueDateString().equals(dueDate.toString("E dd MMM yyy")));
        assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

        taskItems = taskDetailsPage.getTaskItems();
        assertEquals(taskItems.size(), 1);
        assertEquals(taskItems.get(0).getItemName(), fileName);
        assertEquals(taskItems.get(0).getDescription(), NONE);
        assertEquals(getLocalDate(taskItems.get(0).getDateModified()), getToDaysLocalDate());

        assertEquals(taskDetailsPage.getComment(), cloudComment);

        // Edit task and complete the task
        editTaskPage = taskDetailsPage.selectEditButton().render();

        taskInfo = editTaskPage.getTaskDetailsInfo();

        assertEquals(taskInfo.getMessage(), workFlowName);
        assertEquals(taskInfo.getOwner(), getUserFullName(user1));
        assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
        assertEquals(getLocalDate(taskInfo.getDueDate()), getLocalDate(dueDate));
        assertTrue(taskInfo.getDueDateString().equals(dueDate.toString("E dd MMM yyy")));
        assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

        taskItems = taskDetailsPage.getTaskItems();
        assertEquals(taskItems.size(), 1);
        assertEquals(taskItems.get(0).getItemName(), fileName);
        assertEquals(taskItems.get(0).getDescription(), NONE);
        assertEquals(getLocalDate(taskItems.get(0).getDateModified()), getToDaysLocalDate());

        // Verify Status Drop down options
        statusOptions = editTaskPage.getStatusOptions();

        assertEquals(statusOptions.size(), TaskStatus.values().length);
        assertTrue(statusOptions.containsAll(getTaskStatusList()));

        assertFalse(editTaskPage.isReAssignButtonDisplayed(), "Verifying ReAssign button is not displayed (ALF-9880)");

        taskDetailsPage = ShareUserWorkFlow.completeTask(drone, TaskStatus.ONHOLD, EditTaskAction.CANCEL).render();

        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

        taskDetailsPage = ShareUserWorkFlow.completeTaskFromTaskDetailsPage(drone, TaskStatus.INPROGRESS, EditTaskAction.SAVE);
        assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);

        ShareUserWorkFlow.completeTaskFromTaskDetailsPage(drone, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify the task is disappeared from Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed tasks and verify the task is displayed and the details are accurate
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.VERIFY_TASK_WAS_COMPLETED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        // Navigate to Workflows I've started
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        // Verify the workflow is not displayed anymore in Active WorkFlows
        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        // Select Completed workflows and verify workflow is displayed
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

        assertEquals(myWorkFlowsPage.getSubTitle(), "Completed Workflows");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        // Verify the completed workflow details are displayed correctly.
        workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

        assertEquals(workFlowDetails.size(), 1);
        assertEquals(workFlowDetails.get(0).getWorkFlowName(), workFlowName, "Verifying workflow name");
        assertEquals(workFlowDetails.get(0).getDue(), dueDate, "Verifying workflow due date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Verifying Workflow type");
        assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Verifying Workflow Description");

        workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

        generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();

        assertEquals(getLocalDate(generalInfo.getCompletedDate()), getToDaysLocalDate());
        assertEquals(generalInfo.getStatus(), WorkFlowStatus.WORKFLOW_COMPLETE);

        assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed());

        historyList = workFlowDetailsPage.getWorkFlowHistoryList();

        assertEquals(historyList.size(), 2);
        assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.VERIFY_TASK_COMPLETED_ON_CLOUD);
        assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(user1));
        assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate());
        assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
        assertEquals(historyList.get(0).getComment(), "");

        assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
        assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(cloudUser));
        assertEquals(getLocalDate(historyList.get(1).getCompletedDate()), getToDaysLocalDate());
        assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
        assertEquals(historyList.get(1).getComment(), "");

        ShareUser.logout(drone);
    }

    /**
     * ALF-15103:Create Cloud Review Task ALF-15104:Approve Cloud Review Task
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 3 Cloud Users (cloudUser, Reviewer1, Reviewer2)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15103() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "-op", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "-cl", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String reviewer1 = getUserNameForDomain(testName + "-1", DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo1 = new String[] { reviewer1 };

        String reviewer2 = getUserNameForDomain(testName + "-2", DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo2 = new String[] { reviewer2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo2);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * ALF-15103:Create Cloud Review Task ALF-15104:Approve Cloud Review Task
     * <ul>
     * <li>1) Login as Cloud User, Create a site and Logout</li>
     * <li>2) Invite Reviewer1 and Reviewer2 to the site as collaborators</li>
     * <li>3) Login as User1 (OP), Create a site and Upload a document</li>
     * <li>4) Navigate to WorkFlows I've Started page and select StartWorkflow button</li>
     * <li>5) Select "Cloud Task or Review" from select a workflow dropdown</li>
     * <li>6) Select "Cloud Review Task" and fill in the form with Message, Due Date, Task Priority, approval % (50) etc.</li>
     * <li>7) Select Cloud Destination, Select Reviewers, select content and select "Start Workflow" button</li>
     * <li>8) Verify a task is displayed in Active Workflows list</li>
     * <li>9) Load Site Document library and verify Document is part of workflow</li>
     * <li>10) Verify Document is cloud synced</li>
     * <li>11) Login to Cloud as Reviewer1, open site document library and verify the synced document is displayed</li>
     * <li>12) Navigate to MyTasks page and verify a task is displayed in Active Tasks list</li>
     * <li>13) Verify the task details are accurate</li>
     * <li>14) Login to Cloud as Reviewer2, open site document library and verify the synced document is displayed</li>
     * <li>15) Navigate to MyTasks page and verify a task is displayed in Active Tasks list</li>
     * <li>16) Verify the task details are accurate</li>
     * <li>17) Edit task and mark it as Approved</li>
     * <li>18) Verify the task is disappeared from Active Tasks list</li>
     * <li>19) Select Completed and verify the task is displayed and the task details re correct</li>
     * <li>20) Login as Reviewer1, verify task is disappeared from the Active tasks list</li>
     * <li>21) Login as OP user and verify there is a task displayed under Active task with the workflow name</li>
     * <li>22) Verify the task details are correct</li>
     * <li>23) Edit the task and mark it as completed</li>
     * <li>24) Verify the task is disappeared from Active Tasks list</li>
     * <li>25) Select Completed and Verify the task is displayed and the task details are correct</li>
     * <li>26) Navigate to Workflows I've Started page and verify the workflow doesn't exists in Active WorkFlows</li>
     * <li>27) Select Completed and verify the workflow is displayed and the details are correct</li>
     * </ul>
     */
    @Test (groups="Hybrid", enabled = true)
    public void ALF_15103() throws Exception
    {
        String testName = getTestName();
        String opUser = getUserNameForDomain(testName + "-op", DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName + "-cl", DOMAIN_HYBRID);
        String reviewer1 = getUserNameForDomain(testName + "-1", DOMAIN_HYBRID).replace("user", "reviewer");
        String reviewer2 = getUserNameForDomain(testName + "-2", DOMAIN_HYBRID).replace("user", "reviewer");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + System.currentTimeMillis() + "-WorkFlow";
        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);
        int requiredApprovalPercentage = 50;

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer1, getSiteShortname(cloudSite), "SiteContributor", "");
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer2, getSiteShortname(cloudSite), "SiteContributor", "");

        // Login as User1 (OP)
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        List<String> userNames = new ArrayList<String>();
        userNames.add(reviewer1);
        userNames.add(reviewer2);
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(requiredApprovalPercentage);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = ((MyWorkFlowsPage) cloudTaskOrReviewPage.startWorkflow(formDetails)).render();

        List<WorkFlowDetails> workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

        assertEquals(workFlowDetails.size(), 1);
        assertEquals(workFlowDetails.get(0).getWorkFlowName(), formDetails.getMessage(), "Verifying workflow name");
        assertEquals(workFlowDetails.get(0).getDue(), dueDate, "Verifying workflow due date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertNull(workFlowDetails.get(0).getEndDate(), "Verify Workflow End date is NULL as the workflow is still active");
        assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Verifying Workflow type");
        assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Verifying Workflow Description");

        // Open Site Document Library, verify the document is part of the workflow, document is synced and verify Sync Status
        DocumentLibraryPage documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);

        // Verify Content is part of workflow and shows CloudSync icon
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");

        // Verify the sync status for the Content
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);

        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task Details are displayed correctly
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Review", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");

        ShareUser.logout(hybridDrone);

        // Login as reviewer2 User,
        ShareUser.login(hybridDrone, reviewer2, DEFAULT_PASSWORD);

        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);

        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task Details are displayed correctly
        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Review", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");

        // Edit task and mark it as complete
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.COMPLETED, EditTaskAction.APPROVE);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed Tasks and verify Task is displayed and the task details are correct.
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Review", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");
        ShareUser.logout(hybridDrone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify the task is disappeared from tasks list as it met 50% approval rate
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        myTasksPage = myTasksPage.selectCompletedTasks().render();
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        ShareUser.login(drone, opUser, DEFAULT_PASSWORD);

        // Open My Tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify a new task is displayed for OP user
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

        // Verify the task details
        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertNull(taskDetails.getEndDate(), "Verify Workflow End date is NULL as the task is still active");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_APPROVED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), opUser, "Verifying Started by user");

        // Edit the task and complete the task
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Verify the task is disappeared from Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed tasks and verify the task is displayed and the details are accurate
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_APPROVED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), opUser, "Verifying Started by user");

        // Navigate to Workflows I've started
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        // Verify the workflow is not displayed anymore in Active WorkFlows
        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        // Select Completed workflows and verify workflow is displayed
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

        assertEquals(myWorkFlowsPage.getSubTitle(), "Completed Workflows");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        // Verify the completed workflow details are displayed correctly.
        workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

        assertEquals(workFlowDetails.size(), 1);
        assertEquals(workFlowDetails.get(0).getWorkFlowName(), workFlowName, "Verifying workflow name");
        assertEquals(workFlowDetails.get(0).getDue(), dueDate, "Verifying workflow due date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Verifying Workflow type");
        assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Verifying Workflow Description");

        ShareUser.logout(drone);
    }

    /**
     * ALF-15105: Reject Cloud Review Task
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15105() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String reviewer1 = getUserNameForDomain(testName, DOMAIN_HYBRID).replace("user", "reviewer-1");
        String[] reviewerInfo1 = new String[] { reviewer1 };

        String reviewer2 = getUserNameForDomain(testName, DOMAIN_HYBRID).replace("user", "reviewer-2");
        String[] reviewerInfo2 = new String[] { reviewer2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo2);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * ALF-15105: Reject Cloud Review Task
     * <ul>
     * <li>1) Login as Cloud User, Create a site and Logout</li>
     * <li>2) Invite Reviewer1 to the site as collaborator</li>
     * <li>3) Login as User1 (OP), Create a site and Upload a document</li>
     * <li>4) Navigate to WorkFlows I've Started page and select StartWorkflow button</li>
     * <li>5) Select "Cloud Task or Review" from select a workflow dropdown</li>
     * <li>6) Select "Cloud Review Task" and fill in the form with Message, Due Date, Task Priority, approval % (100) etc.</li>
     * <li>7) Select Cloud Destination, Select Reviewers, select content and select "Start Workflow" button</li>
     * <li>8) Verify a task is displayed in Active Workflows list</li>
     * <li>9) Load Site Document library and verify Document is part of workflow</li>
     * <li>10) Verify Document is cloud synced</li>
     * <li>11) Login to Cloud as Reviewer1, open site document library and verify the synced document is displayed</li>
     * <li>12) Navigate to MyTasks page and verify a task is displayed in Active Tasks list</li>
     * <li>13) Verify the task details are accurate</li>
     * <li>14) Edit task and mark it as Rejected</li>
     * <li>15) Verify the task is disappeared from Active Tasks list</li>
     * <li>16) Select Completed and verify the task is displayed and the task details re correct</li>
     * <li>17) Login as OP user and verify there is a task displayed under Active task with the workflow name</li>
     * <li>18) Verify the task details are correct</li>
     * <li>19) Edit the task and mark it as completed</li>
     * <li>20) Verify the task is disappeared from Active Tasks list</li>
     * <li>21) Select Completed and Verify the task is displayed and the task details are correct</li>
     * <li>22) Navigate to Workflows I've Started page and verify the workflow doesn't exists in Active WorkFlows</li>
     * <li>23) Select Completed and verify the workflow is displayed and the details are correct</li>
     * </ul>
     */
    @Test(groups="Hybrid", enabled = true)
    public void ALF_15105() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String reviewer1 = getUserNameForDomain(testName, DOMAIN_HYBRID).replace("user", "reviewer-1");
        String reviewer2 = getUserNameForDomain(testName, DOMAIN_HYBRID).replace("user", "reviewer-2");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + System.currentTimeMillis() + "-WorkFlow";
        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);
        int requiredApprovalPercentage = 50;
        String cloudComment = testName + System.currentTimeMillis() + "-Cloud Comment";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer1, getSiteShortname(cloudSite), "SiteContributor", "");
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer2, getSiteShortname(cloudSite), "SiteContributor", "");

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        List<String> userNames = new ArrayList<String>();
        userNames.add(reviewer1);
        userNames.add(reviewer2);
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(requiredApprovalPercentage);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Fill the form details and start workflow
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the content is part of Workflow and cloudSync icon appears
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");

        // Verify the cloudsync status for the content
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);

        // Verify Synced doc is displayed in Site Document Library
        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task is displayed
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        // Edit task and Reject
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.COMPLETED, cloudComment, EditTaskAction.REJECT);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed Tasks and verify Task is displayed and the task details are correct.
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open My Tasks page
        ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify a new task is displayed for OP user
        assertFalse(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

        ShareUser.logout(drone);

        // Login as reviewer2 User,
        ShareUser.login(hybridDrone, reviewer2, DEFAULT_PASSWORD);

        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);

        // Verify Synced doc is displayed in Site Document Library
        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task is displayed
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        // Edit task and Reject
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.COMPLETED, cloudComment, EditTaskAction.REJECT);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed Tasks and verify Task is displayed and the task details are correct.
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);


        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open My Tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Verify a new task is displayed for OP user
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

        // Verify the task details
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertNull(taskDetails.getEndDate(), "Verify Workflow End date is NULL as the task is still active");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_REJECTED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        // Edit tha task and complete the task
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

        // Verify the task is disappeared from Active Tasks list
        myTasksPage = myTasksPage.selectActiveTasks().render();

        assertFalse(myTasksPage.isTaskPresent(workFlowName));

        // Select Completed tasks and verify the task is displayed and the details are accurate
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(taskDetails.getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(taskDetails.getStatus(), TaskStatus.COMPLETED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_REJECTED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        // Navigate to Workflows I've started
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        // Verify the workflow is not displayed anymore in Active WorkFlows
        myWorkFlowsPage = myWorkFlowsPage.selectActiveWorkFlows().render();

        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        // Select Completed workflows and verify workflow is displayed
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

        assertEquals(myWorkFlowsPage.getSubTitle(), "Completed Workflows");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        // Verify the completed workflow details are displayed correctly.
        List<WorkFlowDetails> workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

        assertEquals(workFlowDetails.size(), 1);
        assertEquals(workFlowDetails.get(0).getWorkFlowName(), workFlowName, "Verifying workflow name");
        assertEquals(workFlowDetails.get(0).getDue(), dueDate, "Verifying workflow due date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(getLocalDate(workFlowDetails.get(0).getEndDate()), getToDaysLocalDate(), "Verify Workflow End date");
        assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Verifying Workflow type");
        assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Verifying Workflow Description");

        ShareUser.logout(drone);
    }

    /**
     * ALF-15106: Cancel Workflow
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15106() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String reviewer1 = getUserNameForDomain(testName, DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo1 = new String[] { reviewer1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }
    /**
     * ALF-15106: Cancel Workflow
     * <ul>
     * <li>1) Login as Cloud User, Create a site and Logout</li>
     * <li>2) Invite Reviewer1 to the site as collaborator</li>
     * <li>3) Login as User1 (OP), Create a site and Upload a document</li>
     * <li>4) Navigate to WorkFlows I've Started page and select StartWorkflow button</li>
     * <li>5) Select "Cloud Task or Review" from select a workflow dropdown</li>
     * <li>6) Select "Cloud Review Task" and fill in the form with Message, Due Date, Task Priority, approval % (100) etc.</li>
     * <li>7) Select Cloud Destination, Select Reviewers, select content and select "Start Workflow" button</li>
     * <li>8) Verify a task is displayed in Active Workflows list</li>
     * <li>9) Load Site Document library and verify Document is part of workflow</li>
     * <li>10) Verify Document is cloud synced</li>
     * <li>11) Login to Cloud as Reviewer1, open site document library and verify the synced document is displayed</li>
     * <li>12) Navigate to MyTasks page and verify a task is displayed in Active Tasks list</li>
     * <li>13) Verify the task details are accurate</li>
     * <li>14) Edit task and mark it as Rejected</li>
     * <li>15) Verify the task is disappeared from Active Tasks list</li>
     * <li>16) Select Completed and verify the task is displayed and the task details re correct</li>
     * <li>17) Login as OP user and verify there is a task displayed under Active task with the workflow name</li>
     * <li>18) Verify the task details are correct</li>
     * <li>19) Edit the task and mark it as completed</li>
     * <li>20) Verify the task is disappeared from Active Tasks list</li>
     * <li>21) Select Completed and Verify the task is displayed and the task details are correct</li>
     * <li>22) Navigate to Workflows I've Started page and verify the workflow doesn't exists in Active WorkFlows</li>
     * <li>23) Select Completed and verify the workflow is displayed and the details are correct</li>
     * </ul>
     */
    @Test(groups="Hybrid", enabled = true)
    public void ALF_15106() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String reviewer1 = getUserNameForDomain(testName, DOMAIN_HYBRID).replace("user", "reviewer");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + System.currentTimeMillis() + "-WorkFlow";
        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);
        int requiredApprovalPercentage = 50;

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer1, getSiteShortname(cloudSite), "SiteContributor", "");

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        // Select Simple Cloud Task
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        List<String> userNames = new ArrayList<String>();
        userNames.add(reviewer1);
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(requiredApprovalPercentage);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow

        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the document is prat of the workflow, document is synced and verify Sync Status

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");

        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);

        // Verify Synced doc is displayed in Site Document Library
        assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task Details are displayed correctly
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), "Review", "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        ShareUser.logout(hybridDrone);

        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying the workflow is present");

        WorkFlowDetailsPage workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

        workFlowDetailsPage.selectCancelWorkFlow().render();

        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying the Workflow is not listed in the Active WorkFlows");

        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying the Workflow is not listed in the Completed WorkFlows");

        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify Task Details are displayed correctly
        assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName, false), "Verifying the task is Removed from tasks list");

        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-15107: Required approval percentage
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 3 Cloud Users (cloudUser, Reviewer1, Reviewer2)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15107() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String reviewer1 = getUserNameForDomain(testName + "-1", DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo1 = new String[] { reviewer1 };

        String reviewer2 = getUserNameForDomain(testName + "-2", DOMAIN_HYBRID).replace("user", "reviewer");
        String[] reviewerInfo2 = new String[] { reviewer2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo2);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * ALF-15107: Required approval percentage
     * <ul>
     * <li>1) Login as Cloud User, Create a site and Logout</li>
     * <li>2) Invite Reviewer1 and Reviewer2 to the site as collaborators</li>
     * <li>3) Login as User1 (OP), Create a site and Upload a document</li>
     * <li>4) Navigate to WorkFlows I've Started page and select StartWorkflow button</li>
     * <li>5) Select "Cloud Task or Review" from select a workflow dropdown</li>
     * <li>6) Select "Cloud Review Task" and fill in the form with Message, Due Date, Task Priority, approval % (50) etc.</li>
     * <li>7) Select Cloud Destination, Select Reviewers, select content and select "Start Workflow" button</li>
     * <li>8) Verify a task is displayed in Active Workflows list</li>
     * <li>9) Load Site Document library and verify Document is part of workflow</li>
     * <li>10) Verify Document is cloud synced</li>
     * <li>11) Login to Cloud as Reviewer1, Navigate to MyTasks page and verify a task is displayed in Active Tasks list</li>
     * <li>12) Login to Cloud as Reviewer2, Navigate to MyTasks page and verify a task is displayed in Active Tasks list</li>
     * <li>13) Login to Cloud as Reviewer1, Navigate to MyTasks page and Approve the task</li>
     * <li>14) Verify the task is disappeared from Active Tasks list</li>
     * <li>15) Select Completed and verify the task is displayed</li>
     * <li>16) Login to Cloud as Reviewer2, Navigate to MyTasks page and verify a task is NOT displayed in Active Tasks and Completed Tasks lists</li>
     * <li>17) Login as OP user and verify there is a task displayed under Active task with the workflow name</li>
     * <li>18) Verify the task details are correct</li>
     * </ul>
     */
    @Test(groups="Hybrid", enabled = true)
    public void ALF_15107() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String reviewer1 = getUserNameForDomain(testName + "-1", DOMAIN_HYBRID).replace("user", "reviewer");
        String reviewer2 = getUserNameForDomain(testName + "-2", DOMAIN_HYBRID).replace("user", "reviewer");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + System.currentTimeMillis() + "-WorkFlow";
        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);
        int requiredApprovalPercentage = 50;

        String cloudComment = testName + System.currentTimeMillis() + "-Cloud Comment";

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Invite Reviewer1 and Reviewer 2 to the site as Contributors
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer1, getSiteShortname(cloudSite), "SiteContributor", "");
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer2, getSiteShortname(cloudSite), "SiteContributor", "");

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        // Select Cloud Review Task
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        List<String> userNames = new ArrayList<String>();
        userNames.add(reviewer1);
        userNames.add(reviewer2);
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(due);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(requiredApprovalPercentage);
        formDetails.setSiteName(cloudSite);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Fill the form details and start workflow
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify the document is prat of the workflow, document is synced and verify Sync Status
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");

        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying workflow exists");

        ShareUser.logout(drone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify task is not displayed in Active Tasks list
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer2, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify task is displayed in Active Tasks list
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as reviewer1 User,
        ShareUser.login(hybridDrone, reviewer1, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName, TaskStatus.COMPLETED, cloudComment,EditTaskAction.APPROVE);

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));
        // Select Completed Tasks and verify the task is present
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as Reviewer2
        sharePage = ShareUser.login(hybridDrone, reviewer2, DEFAULT_PASSWORD);

        // Navigate to MyTasks page
        myTasksPage = sharePage.getNav().selectMyTasks().render();

        // Verify task is not displayed in Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName));
        // Select Completed Tasks and verify the task is NOT present
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open My Tasks page
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        myTasksPage = myTasksPage.selectActiveTasks().render();

        // Verify a new task is displayed for OP user
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

        // Verify the task details
        TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

        assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
        assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
        assertEquals(getLocalDate(taskDetails.getStartDate()), getToDaysLocalDate(), "Verify Workflow Start date");
        assertNull(taskDetails.getEndDate(), "Verify Workflow End date is NULL as the task is still active");
        assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
        assertEquals(taskDetails.getType(), TaskDetailsType.DOCUMENT_WAS_APPROVED_ON_THE_CLOUD, "Verifying Task type");
        assertEquals(taskDetails.getDescription(), workFlowName, "Verifying Workflow Description");
        assertEquals(taskDetails.getStartedBy(), user1, "Verifying Started by user");

        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

        assertTrue(taskDetailsPage.getComment().contains(cloudComment));

        ShareUser.logout(drone);
    }

    /**
     * ALF-15108: After completion
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15108() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
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

    /**
     * ALF-15108: After completion
     * <ul>
     * <li>1) Login as User1 (Cloud) and Create a site</li>
     * <li>2) Login as User1 (OP), create a site and upload 3 documents</li>
     * <li>3) Navigate to MyTasks page and select Workflows I've Started link</li>
     * <li>4) Select "Cloud Task or Review" from select a workflow dropdown</li>
     * <li>5) Create Workflow1 using File1 (After Completion: Keep content synced on cloud)</li>
     * <li>6) Create Workflow2 using File2 (After Completion: Keep content on cloud and remove sync)</li>
     * <li>7) Create Workflow3 using File3 (After Completion: Delete content on cloud and remove sync)</li>
     * <li>8) Verify Workflows are created successfully</li>
     * <li>9) Open Site Document Library, verify all files are part of the workflow, and cloud synced</li>
     * <li>10) Login as CloudUser User (Cloud)</li>
     * <li>11) Open Site Document Library, verify all files are part of the workflow, and synced</li>
     * <li>12) Navigate to MyTasks page and Verify tasks are displayed in Active Tasks list</li>
     * <li>13) Edit each task and mark them as completed</li>
     * <li>14) Verify tasks are NOT displayed in Active Tasks list any more</li>
     * <li>15) Verify tasks are displayed in Completed Tasks list</li>
     * <li>16) Login as User1 (OP), Navigate to MyTasks page</li>
     * <li>17) Verify a new tasks are displayed for OP user in Active Tasks List</li>
     * <li>18) Edit each task and mark them as completed</li>
     * <li>19) Verify the tasks are disappeared from Active Tasks list</li>
     * <li>20) Select Completed tasks and verify the tasks are displayed</li>
     * <li>21) Navigate to Workflows I've Started page and verify tasks are not displayed under Active Workflows page</li>
     * <li>22) Select Completed Workflows and verify workflows are displayed</li>
     * <li>23) Open Site Document Library</li>
     * <li>24) Verify File1 is still Synced and NOT part of a workflow any more</li>
     * <li>25) Verify File2 is NOT Synced and NOT part of a workflow</li>
     * <li>26) Verify File3 is NOT Synced and NOT part of a workflow</li>
     * <li>27) Login as CloudUser (Cloud) and Open Site Document Library</li>
     * <li>28) Verify File1 exists in Site Document Library and still Synced</li>
     * <li>29) Verify File2 exists in Site Document library and it is not Synced</li>
     * <li>30) Verify File3 doesn't exist</li>
     * </ul>
     */
    @Test(groups="Hybrid", enabled = true)
    public void ALF_15108() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = getFileName(testName) + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String fileName3 = getFileName(testName) + "-3.txt";
        String[] fileInfo3 = { fileName3, DOCLIB };

        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String workFlowName2 = testName + System.currentTimeMillis() + "-2-WF";
        String workFlowName3 = testName + System.currentTimeMillis() + "-3-WF";
        String dueDate = getDueDateString();

        String cloudComment1 = testName + System.currentTimeMillis() + "-1-Cloud Comment";
        String cloudComment2 = testName + System.currentTimeMillis() + "-2-Cloud Comment";
        String cloudComment3 = testName + System.currentTimeMillis() + "-3-Cloud Comment";

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

        ShareUser.uploadFileInFolder(drone, fileInfo1).render();
        ShareUser.uploadFileInFolder(drone, fileInfo2).render();
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo3).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);


        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setDueDate(dueDate);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);

        // Create Workflow1 using File1 (After Completion: Keep content synced on cloud)
        formDetails.setMessage(workFlowName1);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Create Workflow2 using File2 (After Completion: Keep content on cloud and remove sync)
        formDetails.setMessage(workFlowName2);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        // Select "Cloud Task or Review" from select a workflow dropdown
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName2);

        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Create Workflow3 using File3 (After Completion: Delete content on cloud and remove sync)
        formDetails.setMessage(workFlowName3);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName3);
        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify all files are part of the workflow, and cloud synced
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

        drone.refresh();
        documentLibraryPage.render();

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName2), "Verifying the Sync Status is \"Synced\"");

        drone.refresh();
        documentLibraryPage.render();

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName3).isPartOfWorkflow(), "Verifying the File3 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName3).isCloudSynced(), "Verifying the File3 is synced");
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName3), "Verifying the Sync Status is \"Synced\"");


        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        // Verify Workflows are created successfully
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName2), "Verifying workflow2 exists");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName3), "Verifying workflow3 exists");
        ShareUser.logout(drone);

        // Login as CloudUser User
        SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Open Site Document Library, verify all files are part of the workflow, and synced
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

        assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
        assertTrue(documentLibraryPage.isFileVisible(fileName2), "Verifying File2 exists");
        assertTrue(documentLibraryPage.isFileVisible(fileName3), "Verifying File3 exists");

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");

        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName3).isPartOfWorkflow(), "Verifying the File3 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName3).isCloudSynced(), "Verifying the File3 is synced");

        // Navigate to MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        // Verify tasks are displayed in Active Tasks list
        assertTrue(myTasksPage.isTaskPresent(workFlowName1));
        assertTrue(myTasksPage.isTaskPresent(workFlowName2));
        assertTrue(myTasksPage.isTaskPresent(workFlowName3));

        // Edit each task and mark them as completed
        ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName1, TaskStatus.COMPLETED, cloudComment1, EditTaskAction.TASK_DONE);
        ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName2, TaskStatus.COMPLETED, cloudComment2, EditTaskAction.TASK_DONE);
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName3, TaskStatus.COMPLETED, cloudComment3, EditTaskAction.TASK_DONE);


        // Verify tasks are NOT displayed in Active Tasks list any more
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));
        assertFalse(myTasksPage.isTaskPresent(workFlowName2));
        assertFalse(myTasksPage.isTaskPresent(workFlowName3));

        // Verify tasks are displayed in Completed Tasks list
        myTasksPage = myTasksPage.selectCompletedTasks().render();
        assertTrue(myTasksPage.isTaskPresent(workFlowName1));
        assertTrue(myTasksPage.isTaskPresent(workFlowName2));
        assertTrue(myTasksPage.isTaskPresent(workFlowName3));

        ShareUser.logout(hybridDrone);

        // Login as OP user
        sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open My Tasks page
        myTasksPage = sharePage.getNav().selectMyTasks().render();

        // Verify a new tasks are displayed for OP user in Active Tasks List
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName2));
        assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName3));

        // Edit each task and mark them as completed
        ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName1, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
        ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName2, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);
        myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName3, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);


        // Verify the tasks are disappeared from Active Tasks list
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));
        assertFalse(myTasksPage.isTaskPresent(workFlowName2));
        assertFalse(myTasksPage.isTaskPresent(workFlowName3));

        // Select Completed tasks and verify the tasks are displayed
        myTasksPage = myTasksPage.selectCompletedTasks().render();

        assertTrue(myTasksPage.isTaskPresent(workFlowName1));
        assertTrue(myTasksPage.isTaskPresent(workFlowName2));
        assertTrue(myTasksPage.isTaskPresent(workFlowName3));

        // Navigate to Workflows I've Started page and verify tasks are not displayed under Active Workflows page
        myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));
        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName2));
        assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName3));

        // Select Completed Workflows and verify workflows are displayed
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName2));
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName3));

        // Open Site Document Library
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

        // Verify File1 is still Synced and not part of a workflow any more
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of a workflow");

        // Verify File2 is NOT Synced and NOT part of a workflow
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the document is synced");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the document is part of a workflow");

        // Verify File3 is NOT Synced and NOT part of a workflow
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName3).isCloudSynced(), "Verifying the document is synced");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName3).isPartOfWorkflow(), "Verifying the document is part of a workflow");

        ShareUser.logout(drone);

        // Login as CloudUser User
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Open Site Document Library
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

        // Verify File1 exists in Site Document Library and still Synced
        assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");

        // Verify File2 exists in Site Document library and it is not Synced
        assertTrue(documentLibraryPage.isFileVisible(fileName2), "Verifying File2 exists");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the document is NOT synced");

        // Verify File3 doesn't exist
        assertFalse(documentLibraryPage.isFileVisible(fileName3), "Verifying File3 does NOT exist");

        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-15109:Lock on-premise content
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15109() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
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

    /**
     * ALF-15109:Lock on-premise content
     * <ul>
     * <li>1) Login as User1 (Cloud) and Create a site</li>
     * <li>2) Login as User1 (OP), create a site and upload 3 documents</li>
     * <li>3) Navigate to MyTasks page and select Workflows I've Started link</li>
     * <li>4) Select "Cloud Task or Review" from select a workflow dropdown</li>
     * <li>5) Create Workflow1 using File1 (Lock On Premise : True)</li>
     * <li>6) Create Workflow2 using File2 (Lock On Premise : False)</li>
     * <li>8) Verify Workflows are created successfully</li>
     * <li>9) Open Site Document Library</li>
     * <li>10) Verify File1 is Cloud Synced, part of workflow and it is Locked</li>
     * <li>11) Verify File2 is Cloud Synced, part of workflow and it is NOT Locked</li>
     * <li>12) Login as CloudUser User (Cloud)</li>
     * <li>13) Open Site Document Library</li>
     * <li>14) Verify File1 exists in Site Document Library, it is Synced and part of workflow</li>
     * <li>15) Verify File2 exists in Site Document Library, it is Synced and part of workflow</li>
     * </ul>
     */
    @Test(groups="Hybrid", enabled = true)
    public void ALF_15109() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = getFileName(testName) + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String workFlowName2 = testName + System.currentTimeMillis() + "-2-WF";
        String dueDate = getDueDateString();

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload 2 files
        ShareUser.uploadFileInFolder(drone, fileInfo1).render();
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setDueDate(dueDate);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);


        // Create Workflow1 using File1 (Lock On Premise : True)
        formDetails.setMessage(workFlowName1);
        formDetails.setLockOnPremise(true);

        documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Create Workflow2 using File2 (Lock On Premise : False)
        formDetails.setMessage(workFlowName2);
        formDetails.setLockOnPremise(false);

        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName2);
        documentLibraryPage =  cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        // Verify File1 is Cloud Synced, part of workflow and it is Locked
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isLocked(), "Verifying the File1 is Locked");
        assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getContentInfo(), "This document is locked by you.", "Verifying Locked message");
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

        drone.refresh();
        documentLibraryPage.render();
        // Verify File2 is Cloud Synced, part of workflow and it is NOT Locked
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isLocked(), "Verifying the File2 is NOT Locked");
        assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName2), "Verifying the Sync Status is \"Synced\"");

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        // Verify Workflows are created successfully
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName2), "Verifying workflow2 exists");

        ShareUser.logout(drone);

        // Login as CloudUser User
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        // Open Site Document Library
        documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

        // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
        assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isLocked(), "Verifying the File1 is NOT Locked");

        // Verify File2 exists in Site Document library, it is Synced and part of workflow.
        assertTrue(documentLibraryPage.isFileVisible(fileName2), "Verifying File2 exists");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the document is NOT synced");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the document is NOT synced");
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isLocked(), "Verifying the File2 is NOT Locked");

        // Navigate to MyTasks page and verify both tasks are present
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        assertTrue(myTasksPage.isTaskPresent(workFlowName1));
        assertTrue(myTasksPage.isTaskPresent(workFlowName2));

        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-15110:Select Destination ALF-15111:Select Assignment
     * <ul>
     * <li>1) Create OP Users (User1, testUser1, testUser2)</li>
     * <li>2) Create Cloud users (cloudUser1, cloudUser2, cloudUser3 & cloudUser4)</li>
     * <li>3) Upgrade both Cloud networks</li>
     * <li>4) Login to User1, set up the cloud sync, Create a site and upload a document</li>
     * <li>5) Login as CloudUser1 and create a site</li>
     * <li>6) Login as CloudUser2 and create a site</li>
     * <li>7) CloudUser2 invites CloudUser1 to join the site as Consumer</li>
     * <li>8) Login as CloudUser3 and create a site</li>
     * <li>9) Login as CloudUser4 and create a site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15110() throws Exception
    {
        String testName = getTestName();
        String opSite = getSiteName(testName) + "-OP";
        String cloudSite1 = getSiteName(testName) + "-CL-1";
        String cloudSite2 = getSiteName(testName) + "-CL-2";
        String cloudSite3 = getSiteName(testName) + "-CL-3";
        String cloudSite4 = getSiteName(testName) + "-CL-4";
        String file = getFileName(testName) + ".txt";
        String[] fileInfo = { file, DOCLIB };

        String user1 = getUserNameForDomain(testName + "-1", testDomain1);
        String[] userInfo1 = new String[] { user1 };

        String opTestUser1 = getUserNameForDomain(testName + "-op-1", testDomain1);
        String[] opTestUserInfo1 = new String[] { opTestUser1 };

        String opTestUser2 = getUserNameForDomain(testName + "-op-2", testDomain1);
        String[] opTestUserInfo2 = new String[] { opTestUser2 };

        String cloudUser1 = getUserNameForDomain(testName + "-1", testDomain1);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        String cloudUser2 = getUserNameForDomain(testName + "-2", testDomain1);
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        String cloudUser3 = getUserNameForDomain(testName + "-1", testDomain2);
        String[] cloudUserInfo3 = new String[] { cloudUser3 };

        String cloudUser4 = getUserNameForDomain(testName + "-2", testDomain2);
        String[] cloudUserInfo4 = new String[] { cloudUser4 };

        // Create OP Users (User1, testUser1, testUser2)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, opTestUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, opTestUserInfo2);

        // Create Cloud users (cloudUser1, cloudUser2, cloudUser3 & cloudUser4)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo3);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo4);
        // Upgrade both Cloud networks
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain1, "1000");
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain2, "1000");

        // Login to User1, set up the cloud sync, Create a site and upload a document
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSite, SITE_VISIBILITY_PUBLIC);
//        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        ShareUser.logout(drone);

        // Login as CloudUser1 and create a site
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite1, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as CloudUser2 and create a site
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite2, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // CloudUser2 invites CloudUser1 to join the site as Consumer
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser2, cloudUser1, getSiteShortname(cloudSite2), "SiteConsumer", "");

        // Login as CloudUser3 and create a site
        ShareUser.login(hybridDrone, cloudUser3, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite3, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as CloudUser4 and create a site
        ShareUser.login(hybridDrone, cloudUser4, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite4, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-15110:Select Destination ALF-15111:Select Assignment
     * <ul>
     * <li>1) Login to OP as User1, Navigate to WorkFlows I've started page and select Start Workflow</li>
     * <li>2) Select WorkFlow Type as "Cloud Task Or Review" and task type as "Simple Cloud Task"</li>
     * <li>3) Fill in Message (Workflow Name), due date, After Completion</li>
     * <li>4) Select Destination And Assignee and Verify User can only see Domain1</li>
     * <li>5) Select network, select CloudSite2 (CloudUser1 is consumer for that site) and Verify User1 have no permission</li> ALF-20094
     * <li>6) Select CloudSite1, verify user has permission to the folder</li>
     * <li>7) Select Default folder and select Sync button</li>
     * <li>8) Verify Destination details (Network, Site and Folder) are displayed correctly</li>
     * <li>9) Select Assignee button and verify "No items found" for OP TestUser1, OP TestUser2, CloudUser3, CloudUser4</li>
     * <li>10) Verify CloudUser1 and CloudUser2 can be found from search</li>
     * <li>11) Select CloudUser1</li>
     * <li>12) Select the file from the site and start workflow</li>
     * <li>13) Verify workflow is created</li>
     * <li>14) Select workflow and verify the Assignee is displayed correctly from the Workflow Details page</li>
     * <li>15) Login to Cloud as CloudUser1 and verify the task is present</li>
     * </ul>
     */
    @Test(groups="Hybrid", enabled = true)
    public void ALF_15110() throws Exception
    {
        String testName = getTestName();
        String opSite = getSiteName(testName) + "-OP";
        String cloudSite1 = getSiteName(testName) + "-CL-1";
        String cloudSite2 = getSiteName(testName) + "-CL-2";
        String cloudSite3 = getSiteName(testName) + "-CL-3";
        String cloudSite4 = getSiteName(testName) + "-CL-4";

        String cloudUser1 = getUserNameForDomain(testName + "-1", testDomain1);
        String cloudUser2 = getUserNameForDomain(testName + "-2", testDomain1);
        String cloudUser3 = getUserNameForDomain(testName + "-1", testDomain2);
        String cloudUser4 = getUserNameForDomain(testName + "-2", testDomain2);

        String user1 = getUserNameForDomain(testName + "-1", testDomain1);
        String opTestUser1 = getUserNameForDomain(testName + "-op-1", testDomain1);
        String opTestUser2 = getUserNameForDomain(testName + "-op-2", testDomain1);

        String file = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String[] fileInfo = { file, DOCLIB };

        String dueDate = getDueDateString();
        String workFlowName = testName + System.currentTimeMillis() + "-WF";

        // Login to OP as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        SiteUtil.openSiteDocumentLibraryURL(drone, opSite);
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        // Start Simple Cloud Task Workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, file);

        // Fill in Message (Workflow Name), due date, After Completion
        cloudTaskOrReviewPage.enterMessageText(workFlowName);
        cloudTaskOrReviewPage.enterDueDateText(dueDate);
        cloudTaskOrReviewPage.selectAfterCompleteDropDown(KeepContentStrategy.DELETECONTENT);

        // Verify Assignee button is not enabled
        assertFalse(cloudTaskOrReviewPage.isSelectAssigneeButtonEnabled());

        // Select Destination And Assignee
        DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        // Verify User can only see Domain1
        assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain1));
        assertFalse(destinationAndAssigneePage.isNetworkDisplayed(testDomain2));

        // Select network, select CloudSite2 (CloudUser1 is consumer for that site)
        destinationAndAssigneePage.selectNetwork(testDomain1);
        assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSite1));
        assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSite2));
        assertFalse(destinationAndAssigneePage.isSiteDisplayed(cloudSite3));
        assertFalse(destinationAndAssigneePage.isSiteDisplayed(cloudSite4));
        // i.e. testsite1 and testsite2.
        destinationAndAssigneePage.selectSite(cloudSite2);
        // ALF-20094 is fixed in such a way that sync button is not enabled .. so might be we need to do some more checks - Done
        // Verify User1 have no permission
        assertFalse(destinationAndAssigneePage.isSyncButtonEnabled(), "Verifying the Sync button is disabled");
        assertFalse(destinationAndAssigneePage.isSyncPermitted(DEFAULT_FOLDER_NAME), "Verifying User doesn't have permissions to the folder");

        // Select CloudSite1, verify user has permission to the folder
        destinationAndAssigneePage.selectSite(cloudSite1);
        assertTrue(destinationAndAssigneePage.isSyncPermitted(DEFAULT_FOLDER_NAME), "Verifying User has permissions to the folder");
        // Select Default folder and select Sync button
        destinationAndAssigneePage.selectFolder(DEFAULT_FOLDER_NAME);
        destinationAndAssigneePage.selectSubmitButtonToSync();
        cloudTaskOrReviewPage.render();

        // Verify Destination details (Network, Site and Folder)
        assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), testDomain1, "Verify Destination Network");
        assertEquals(cloudTaskOrReviewPage.getDestinationSite(), cloudSite1, "Verify Destination Site");
        assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), DEFAULT_FOLDER_NAME + "/", "Verify Destination Folder Name");

        // Select Assignee button and verify "No items found" for OP TestUser1, OP TestUser2, CloudUser3, CloudUser4
        AssignmentPage assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assertTrue(assignmentPage.isNoItemsFoundMessageDisplayed(opTestUser1));
        assertTrue(assignmentPage.isNoItemsFoundMessageDisplayed(opTestUser2));
        assertTrue(assignmentPage.isNoItemsFoundMessageDisplayed(cloudUser3));
        assertTrue(assignmentPage.isNoItemsFoundMessageDisplayed(cloudUser4));

        // Verify CloudUser1 and CloudUser2 can be found from search
        assertTrue(assignmentPage.isUserFound(cloudUser1));
        assertTrue(assignmentPage.isUserFound(cloudUser2));

        // Select CloudUser1
        assignmentPage.selectAssignee(cloudUser1);

        cloudTaskOrReviewPage.render();

        assertTrue(cloudTaskOrReviewPage.isAssigneePresent());
        assertTrue(cloudTaskOrReviewPage.getAssignee().contains(cloudUser1));

        // Select the file from the site and start workflow
//            cloudTaskOrReviewPage.selectItem(file, opSite);

        DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.selectStartWorkflow().render();

        assertTrue(checkIfContentIsSynced(drone, file));
        assertTrue(documentLibraryPage.getFileDirectoryInfo(file).isPartOfWorkflow());

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        // Verify workflow is created
        assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying workflow is created successfully");

        // Select workflow and verify the Assignee is displayed correctly
        WorkFlowDetailsPage workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

        assertTrue(workFlowDetailsPage.getAssignee().contains(cloudUser1), "Veifying Assignee");

        ShareUser.logout(drone);

        // Login to Cloud as CloudUser1 and verify the task is present
        sharePage = ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        assertTrue(myTasksPage.isTaskPresent(workFlowName));

        ShareUser.logout(hybridDrone);
    }
}
