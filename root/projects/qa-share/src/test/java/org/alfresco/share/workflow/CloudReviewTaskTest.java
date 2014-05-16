package org.alfresco.share.workflow;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.task.TaskItem;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.CurrentTaskType;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
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
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class CloudReviewTaskTest extends AbstractWorkflow
{

    /**
     * Class includes: Tests from TestLink in Area: Hybrid Workflow/Cloud Review
     * Task
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
    }

    /**
     * ALF-15151:Create Cloud Review Task
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create A Cloud User</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_15151() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "-op", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "-cl", DOMAIN_HYBRID);
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
     * ALF-15151:Cloud Review Task - Create ALF-15152:Cloud Review Task -
     * Workflow Details (OP) ALF-15153:Cloud Review Task - Workflow Details
     * (Cloud) - NOT A VALID SCENARIO ALF-15154:Cloud Review Task - Task Details
     * (Cloud) ALF-15166:Cloud Review Task - Sync ALF-15155:Cloud Review Task -
     * Edit Task Details (Cloud) ALF-15156:Cloud Review Task - Edit Task Details
     * - Items (Cloud) - CAN'T AUTOMATE ALF-9460:Approve action ALF-15161:Cloud
     * Review Task - Task Done ALF-15162:Cloud Review Task - Task Done - Details
     * ALF-15163:Cloud Review Task - Task Done - Edit Task Details (OP)
     * ALF-15164:Cloud Review Task - Task Done - Complete (OP)
     * <ul>
     * <li>1) Login as Cloud User (Cloud) and create a site</li>
     * <li>2) Login as User1 (OP), create a site and upload a document</li>
     * <li>3) Start a "Cloud Task or Review" workflow</li>
     * <li>4) Select uploaded file, Fill the form details and start workflow (Task Type : Cloud Review Task, Priority: Medium, Keep Content Strategy: Delete
     * Content, Destination: CloudSite, Reviewer: CloudUser)</li>
     * <li>5) Select the workflow to open WorkFlow Details Page</li>
     * <li>6) Verify WorkFlow Details General Info section</li>
     * <li>7) Verify WorkFlow Details More Info section</li>
     * <li>8) Verify WorkFlow Item Details</li>
     * <li>9) Verify "No Tasks" message is displayed in "Current Tasks" table</li>
     * <li>10) Verify WorkFlow Details History</li>
     * <li>11) Open Site Document Library, verify the document is prat of the workflow, document is synced and verify Sync Status</li>
     * <li>12) Login as Cloud User User, Verify the Document is Synced and it is part of WorkFlow</li>
     * <li>13) Navigate to MyTasks page and Verify Task is displayed</li>
     * <li>14) Verify Task Details are displayed correctly on MyTasks page</li>
     * <li>15) Select the Task to go to Task Details page</li>
     * <li>16) Verify Task Info section on Task Details page</li>
     * <li>17) Verify Task Status is "Not yet Started"</li>
     * <li>18) Verify Item details in Task Details Page</li>
     * <li>19) Verify Comment is "(None)"</li>
     * <li>20) Select "Edit" button to go to "Edit Task Page"</li>
     * <li>21) Verify Task Info in Edit Task Page</li>
     * <li>22) Verify Task item details in Edit Task Page</li>
     * <li>23) Verify Status Drop down options</li>
     * <li>24) Select "In Progress" Status, Comment (Eg: InProgress Comment) and Click on Save button</li>
     * <li>25) Verify Status now changed to "In Progress" and Cloud Comment updated in Task Details Page</li>
     * <li>26) Select Edit button, select "Completed" option from Status drop down, update Comment and click on "Approve" button</li>
     * <li>28) Verify Status now changed to "Completed" and Comment is updated in Task Details Page</li>
     * <li>29) Navigate to My Tasks page and verify Task is disappeared from Active Tasks list</li>
     * <li>30) Select Completed Tasks and verify the task is present</li>
     * <li>31) Select the Task to goto Task Details Page and Verify Edit button is not present, Task Status is "Completed" and Correct value is displayed in
     * Comments</li>
     * <li>32) Login as OP user and navigate to My Tasks page</li>
     * <li>33) Verify a new task is displayed for OP user</li>
     * <li>34) Navigate to My WorkFlows page and select the WorkFlow to open WorkFlow Details page</li>
     * <li>35) Verify WorkFlow General Info, More Info sections</li>
     * <li>36) Verify "No Tasks" message is not displayed in Current Tasks table</li>
     * <li>37) Verify Current Tasks table</li>
     * <li>38) Verify History table</li>
     * <li>39) Select "Task Details" link from Current Tasks table</li>
     * <li>40) Verify Task Details page header and Edit button is present in Task Details page</li>
     * <li>41) Verify Task Info section</li>
     * <li>42) Verify Task Status is "Not yet started"</li>
     * <li>43) Verify Item details</li>
     * <li>44) Verify "Required Approval Percentage" and "Actual Approval Percentage"</li>
     * <li>45) Verify Comment given by Cloud is displayed correctly</li>
     * <li>46) Select "Edit" button to goto Edit Task Page</li>
     * <li>47) Verify Task Info, Item details, Status drop down options on Edit Task page</li>
     * <li>48) Select "On Hold" status option and select Cancel button</li>
     * <li>49) Verify Task Status has not been changed to "On Hold". It is Still "Not yet Started" in Task Details Page</li>
     * <li>50) Select Edit button, select Status as "In Progress" and click on Save button</li>
     * <li>51) Verify the status is now changed to "In Progress" in Task Details page</li>
     * <li>52) Select Edit button, select Task Status as "Completed" and Select "Task Done" button</li>
     * <li>53) Verify Task status is now changed to "Completed" on Task Details page</li>
     * <li>54) Navigate to My Tasks page and verify task is disappeared from Active Tasks list</li>
     * <li>55) Select "Completed" tasks and verify task is present</li>
     * <li>56) Navigate to Workflows I've started</li>
     * <li>57) Verify the workflow is not displayed anymore in Active WorkFlows</li>
     * <li>58) Select Completed workflows and verify workflow is displayed</li>
     * <li>59) Verify the completed workflow details are displayed correctly.</li>
     * <li>60) Select WorkFlow to goto WorkFlow Details page</li>
     * <li>61) Verify WorkFlow Details General Info and More Info sections</li>
     * <li>62) Verify "No Tasks" displayed in Current Tasks table</li>
     * <li>63) Verify History Table</li>
     * </ul>
     */
    @Test(groups = "Hybrid")
    public void ALF_15151() throws Exception
    {
        String testName = getTestName();
        String uniqueString = String.valueOf(System.currentTimeMillis()).substring(7, 12);
        String user1 = getUserNameForDomain(testName + "-op", DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName + "-cl", DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName).replace(testName, "") + uniqueString + "-OP";
        String cloudSite = getSiteName(testName).replace(testName, "") + uniqueString + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + System.currentTimeMillis() + "-WorkFlow";

        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);
        int requiredApprovalPercentage = 50;

        String cloudCommentInProgress = testName + System.currentTimeMillis() + "-Comment : In Progress";
        String cloudComment = testName + System.currentTimeMillis() + "-Cloud Comment";

        try
        {
            // Login as Cloud User (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP), create a site and upload a document
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();
            // Start a "Cloud Task or Review" workflow
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setMessage(workFlowName);
            formDetails.setDueDate(due);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

            // Fill the form details and start workflow (Task Type : Cloud
            // Review Task, Priority: Medium, Keep Content Strategy: Delete
            // Content, Destination: CloudSite, Reviewer: CloudUser)
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Open Site Document Library, verify the document is prat of the
            // workflow, document is synced and verify Sync Status
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
            assertTrue(checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

            drone.refresh();
            documentLibraryPage.render();
            SyncInfoPage syncInfoPage = documentLibraryPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();
            Assert.assertEquals(syncInfoPage.getCloudSyncLocation(), DOMAIN_HYBRID + ">" + cloudSite + ">" + DEFAULT_FOLDER_NAME);
            syncInfoPage.clickOnCloseButton();

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Select the workflow to open WorkFlow Details Page
            WorkFlowDetailsPage workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

            // Verify WorkFlow Details General Info section
            WorkFlowDetailsGeneralInfo generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();

            assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
            assertEquals(generalInfo.getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW);
            assertEquals(generalInfo.getStartedBy(), getUserFullName(user1));
            assertEquals(generalInfo.getDueDate().toLocalDate(), dueDate.toLocalDate());
            assertEquals(generalInfo.getCompleted(), "<in progress>");
            assertEquals(generalInfo.getStartDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(generalInfo.getPriority(), Priority.MEDIUM);
            assertEquals(generalInfo.getStatus(), WorkFlowStatus.WORKFLOW_IN_PROGRESS);
            assertEquals(generalInfo.getMessage(), workFlowName);

            // Verify WorkFlow Details More Info section
            WorkFlowDetailsMoreInfo moreInfo = workFlowDetailsPage.getWorkFlowDetailsMoreInfo();

            assertEquals(moreInfo.getType(), TaskType.CLOUD_REVIEW_TASK);
            assertEquals(moreInfo.getDestination(), DOMAIN_HYBRID);
            assertEquals(moreInfo.getAfterCompletion(), KeepContentStrategy.DELETECONTENT);
            assertFalse(moreInfo.isLockOnPremise());
            assertEquals(moreInfo.getAssignmentList().size(), userNames.size());
            assertTrue(moreInfo.getAssignmentList().contains(getUserFullNameWithEmail(cloudUser, cloudUser)));

            // Verify WorkFlow Item Details
            List<WorkFlowDetailsItem> items = workFlowDetailsPage.getWorkFlowItems();
            assertEquals(items.size(), 1);
            assertEquals(items.get(0).getItemName(), fileName);
            assertEquals(items.get(0).getDescription(), NONE);
            assertEquals(items.get(0).getDateModified().toLocalDate(), getToDaysLocalDate());

            // Verify "No Tasks" message is displayed in "Current Tasks" table
            assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed());

            // Verify WorkFlow Details History
            List<WorkFlowDetailsHistory> historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 1);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(user1));
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(0).getComment(), "");

            ShareUser.logout(drone);

            // Login as Cloud User User, Verify the Document is Synced and it is
            // part of WorkFlow
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite);
            assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the document is displayed");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of Workflow");

            // Navigate to MyTasks page and Verify Task is displayed
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // Verify Task Details are displayed correctly
            TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName);

            assertEquals(taskDetails.getTaskName(), workFlowName, "Verifying workflow name");
            assertEquals(taskDetails.getDue(), ShareUserWorkFlow.getDueDateOnMyTaskPage(due), "Verifying workflow due date");
            assertEquals(taskDetails.getStartDate().toLocalDate(), getToDaysLocalDate(), "Verify Workflow Start date");
            assertEquals(taskDetails.getStatus(), TaskStatus.NOTYETSTARTED.getTaskName(), "Verifying status");
            assertEquals(taskDetails.getType(), TaskDetailsType.REVIEW, "Verifying Task type");
            System.out.println("1..." + taskDetails.getDescription());
            // assertEquals(taskDetails.getDescription(), workFlowName,
            // "Verifying Workflow Description");
            assertEquals(taskDetails.getStartedBy(), cloudUser, "Verifying Started by user");

            // Task History Verifications
            TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName);

            // Verify Task History Page Header
            assertEquals(taskHistoryPage.getPageHeader(), getCloudReviewTaskDetailsHeader(workFlowName));

            // Verify Task History Page General Info Section
            generalInfo = taskHistoryPage.getWorkFlowDetailsGeneralInfo();

            // assertEquals(generalInfo.getTitle(),
            // WorkFlowTitle.HYBRID_REVIEW_AND_APPROVE_PROCESS);
            assertEquals(generalInfo.getTitle(), WorkFlowTitle.HYBRID_REVIEW);
            System.out.println("1..." + generalInfo.getDescription());

            // assertEquals(generalInfo.getDescription(),
            // WorkFlowDescription.HYBRID_REVIEW_AND_APPROVE_PROCESS);
            assertEquals(generalInfo.getStartedBy(), getUserFullName(cloudUser));
            assertEquals(getLocalDate(generalInfo.getDueDate()), getLocalDate(dueDate));
            assertEquals(generalInfo.getCompleted(), "<in progress>");
            assertEquals(getLocalDate(generalInfo.getStartDate()), getToDaysLocalDate());
            assertEquals(generalInfo.getPriority(), formDetails.getTaskPriority());
            assertEquals(generalInfo.getStatus(), WorkFlowStatus.TASK_IN_PROGRESS);
            assertEquals(generalInfo.getMessage(), workFlowName);

            // Verify Task History Page More Info Section
            moreInfo = taskHistoryPage.getWorkFlowDetailsMoreInfo();
            // assertEquals(moreInfo.getNotification(),
            // SendEMailNotifications.NO);

            // Verify Task History Page Item Details
            items = taskHistoryPage.getWorkFlowItems();
            assertEquals(items.size(), 1);
            assertEquals(items.get(0).getItemName(), fileName);
            assertEquals(items.get(0).getDescription(), NONE);
            assertEquals(getLocalDate(items.get(0).getDateModified()), getToDaysLocalDate());

            // Verify Task History Page Current Tasks List
            List<WorkFlowDetailsCurrentTask> currentTaskList = taskHistoryPage.getCurrentTasksList();

            assertEquals(currentTaskList.size(), 1);
            assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.REVIEW);
            assertEquals(currentTaskList.get(0).getAssignedTo(), getUserFullName(cloudUser));
            assertEquals(currentTaskList.get(0).getDueDate().toLocalDate(), dueDate.toLocalDate());
            assertEquals(currentTaskList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // Verify Task History Page History List
            historyList = taskHistoryPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 1);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_REVIEW);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser));
            assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(0).getComment(), "");

            // Select the Task to go to Task Details page
            TaskDetailsPage taskDetailsPage = currentTaskList.get(0).getTaskDetailsLink().click().render();

            // Verify Task Details Page Header
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), getCloudReviewTaskDetailsHeader(workFlowName));

            // Verify Task Info section on Task Details page
            TaskInfo taskInfo = taskDetailsPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(cloudUser));
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            assertEquals(taskInfo.getDueDate().toLocalDate(), dueDate.toLocalDate());
            System.out.println(taskInfo.getDueDateString());
            assertNotNull(taskInfo.getIdentifier());
            assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

            // Verify Task Status is "Not yet Started"
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // Verify Item details in Task Details Page
            List<TaskItem> taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);
            assertEquals(taskItems.get(0).getDescription(), NONE);
            assertEquals(taskItems.get(0).getDateModified().toLocalDate(), getToDaysLocalDate());

            // Verify Comment is "(None)"
            assertEquals(taskDetailsPage.getComment(), NONE);

            // Select "Edit" button to go to "Edit Task Page"
            EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();

            // Verify Task Info in Edit Task Page
            taskInfo = editTaskPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(cloudUser));
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            // assertEquals(taskInfo.getDueDate().toLocalDate(),
            // dueDate.toLocalDate());
            assertNotNull(taskInfo.getIdentifier());
            assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

            // Verify Task item details in Edit Task Page
            taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);
            assertEquals(taskItems.get(0).getDescription(), NONE);
            assertEquals(taskItems.get(0).getDateModified().toLocalDate(), getToDaysLocalDate());

            // Verify Status Drop down options
            List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();

            assertEquals(statusOptions.size(), TaskStatus.values().length);
            assertTrue(statusOptions.containsAll(getTaskStatusList()));
            // Select "In Progress" Status, Comment (Eg: InProgress Comment) and
            // Click on Save button
            taskDetailsPage = ShareUserWorkFlow.completeTask(hybridDrone, TaskStatus.ONHOLD, cloudCommentInProgress, EditTaskAction.CANCEL).render();

            // Verify Status now changed to hasn't been changed after upon
            // cancel
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);
            assertEquals(taskDetailsPage.getComment(), NONE);

            taskDetailsPage = ShareUserWorkFlow
                    .completeTaskFromTaskDetailsPage(hybridDrone, TaskStatus.INPROGRESS, cloudCommentInProgress, EditTaskAction.SAVE);
            // Verify Status now changed to "In Progress" and Cloud Comment
            // updated in Task Details Page
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);
            assertEquals(taskDetailsPage.getComment(), cloudCommentInProgress);

            // Select Edit button, select "Completed" option from Status drop
            // down, update Comment and click on "Approve" button
            taskDetailsPage = ShareUserWorkFlow.completeTaskFromTaskDetailsPage(hybridDrone, TaskStatus.COMPLETED, cloudComment, EditTaskAction.APPROVE);

            // Verify Status now changed to "Completed" and Comment is updated
            // in Task Details Page
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
            assertEquals(taskDetailsPage.getComment(), cloudComment);

            // Navigate to My Tasks page and verify Task is disappeared from
            // Active Tasks list
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertFalse(myTasksPage.isTaskPresent(workFlowName));

            // Select Completed Tasks and verify the task is present
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // Select the Task to goto Task Details Page and Verify Edit button
            // is not present, Task Status is "Completed" and Correct value is
            // displayed in Comments
            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

            assertFalse(taskDetailsPage.isEditButtonPresent());
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
            assertEquals(taskDetailsPage.getComment(), cloudComment);

            // Task History Page Verifications

            taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName);

            // General Info verifications
            generalInfo = taskHistoryPage.getWorkFlowDetailsGeneralInfo();

            assertEquals(getLocalDate(generalInfo.getCompletedDate()), getToDaysLocalDate());
            assertEquals(generalInfo.getStatus(), WorkFlowStatus.TASK_COMPLETE);

            assertTrue(taskHistoryPage.isNoTasksMessageDisplayed(), "Verify \"No Tasks\" message is displayed in Current Tasks table");

            // Verify Task History Page History List
            historyList = taskHistoryPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 2);

            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.REVIEW);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser));
            assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.APPROVED);
            assertEquals(historyList.get(0).getComment(), cloudComment);

            assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_REVIEW);
            assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(cloudUser));
            assertEquals(getLocalDate(historyList.get(1).getCompletedDate()), getToDaysLocalDate());
            assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(1).getComment(), "");

            ShareUser.logout(hybridDrone);

            // Login as OP user and navigate to My Tasks page
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            ShareUserWorkFlow.navigateToMyTasksPage(drone);

            // Verify a new task is displayed for OP user
            assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

            // Navigate to My WorkFlows page and select the WorkFlow to open
            // WorkFlow Details page
            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

            assertTrue(workFlowDetailsPage.isTitlePresent());

            // Verify WorkFlow General Info section
            generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();

            assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
            assertEquals(generalInfo.getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW);
            assertEquals(generalInfo.getStartedBy(), getUserFullName(user1));
            assertEquals(generalInfo.getDueDate().toLocalDate(), dueDate.toLocalDate());
            assertEquals(generalInfo.getCompleted(), "<in progress>");
            assertEquals(generalInfo.getStartDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(generalInfo.getPriority(), Priority.MEDIUM);
            assertEquals(generalInfo.getStatus(), WorkFlowStatus.WORKFLOW_IN_PROGRESS);
            assertEquals(generalInfo.getMessage(), workFlowName);

            // Verify WorkFlow More Info section
            moreInfo = workFlowDetailsPage.getWorkFlowDetailsMoreInfo();

            assertEquals(moreInfo.getType(), TaskType.CLOUD_REVIEW_TASK);
            assertEquals(moreInfo.getDestination(), DOMAIN_HYBRID);
            assertEquals(moreInfo.getAfterCompletion(), KeepContentStrategy.DELETECONTENT);
            assertFalse(moreInfo.isLockOnPremise());
            assertEquals(moreInfo.getAssignmentList().size(), userNames.size());
            assertTrue(moreInfo.getAssignmentList().contains(getUserFullNameWithEmail(cloudUser, cloudUser)));

            // Verify "No Tasks" message is not displayed in Current Tasks table
            assertFalse(workFlowDetailsPage.isNoTasksMessageDisplayed());

            // Verify Current Tasks table
            currentTaskList = workFlowDetailsPage.getCurrentTasksList();

            assertEquals(currentTaskList.size(), 1);
            assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.DOCUMENT_WAS_APPROVED_ON_CLOUD);
            assertEquals(currentTaskList.get(0).getAssignedTo(), getUserFullName(user1));
            assertEquals(currentTaskList.get(0).getDueDate().toLocalDate(), dueDate.toLocalDate());
            assertEquals(currentTaskList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // Verify History table
            historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 1);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(user1));
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(0).getComment(), "");

            // Select "Task Details" link from Current Tasks table
            taskDetailsPage = currentTaskList.get(0).getTaskDetailsLink().click().render();

            // Verify Task Details page header and Edit button is present in
            // Task Details page
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), "Details: " + workFlowName + " (Document was approved on the cloud)");
            assertTrue(taskDetailsPage.isEditButtonPresent());

            // Verify Task Info section
            taskInfo = taskDetailsPage.getTaskDetailsInfo();
            assertEquals(taskInfo.getMessage(), workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(user1));
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            // assertEquals(taskInfo.getDueDate().toLocalDate(),
            // dueDate.toLocalDate());
            assertNotNull(taskInfo.getIdentifier());
            assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

            // Verify Task Status is "Not yet started"
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // Verify Item details
            taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);
            assertEquals(taskItems.get(0).getDescription(), NONE);
            assertEquals(taskItems.get(0).getDateModified().toLocalDate(), getToDaysLocalDate());

            // Verify "Required Approval Percentage" and
            // "Actual Approval Percentage"
            assertEquals(taskDetailsPage.getRequiredApprovalPercentage(), requiredApprovalPercentage);
            assertEquals(taskDetailsPage.getActualApprovalPercentage(), 100);

            // Verify Comment given by Cloud is displayed correctly
            assertEquals(taskDetailsPage.getComment(), getUserFullName(cloudUser) + ": " + cloudComment + "  (Approved)");

            // Select "Edit" button to goto Edit Task Page
            editTaskPage = taskDetailsPage.selectEditButton().render();

            // Verify Task Info in Edit Task page
            taskInfo = editTaskPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(user1));
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            // assertEquals(taskInfo.getDueDate().toLocalDate(),
            // dueDate.toLocalDate());
            assertNotNull(taskInfo.getIdentifier());
            assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

            // Verify Item Details
            taskItems = editTaskPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);
            assertEquals(taskItems.get(0).getDescription(), NONE);
            assertEquals(taskItems.get(0).getDateModified().toLocalDate(), getToDaysLocalDate());

            // Verify Status Drop down options
            statusOptions = editTaskPage.getStatusOptions();

            assertEquals(statusOptions.size(), TaskStatus.values().length);
            assertTrue(statusOptions.containsAll(getTaskStatusList()));

            // Select "On Hold" status option and select Cancel button
            editTaskPage.selectStatusDropDown(TaskStatus.ONHOLD);
            taskDetailsPage = editTaskPage.selectCancelButton().render();

            // Verify Task Status has not been changed to "On Hold". It is Still
            // "Not yet Started" in Task Details Page
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // Select Edit button, select Status as "In Progress" and click on
            // Save button
            taskDetailsPage = ShareUserWorkFlow.completeTaskFromTaskDetailsPage(drone, TaskStatus.INPROGRESS, EditTaskAction.SAVE);

            // Verify the status is now changed to "In Progress" in Task Details
            // page
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.INPROGRESS);

            // Select Edit button, select Task Status as "Completed" and Select
            // "Task Done" button
            taskDetailsPage = ShareUserWorkFlow.completeTaskFromTaskDetailsPage(drone, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

            // Verify Task status is now changed to "Completed" on Task Details
            // page
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);

            // Navigate to My Tasks page and verify task is disappeared from
            // Active Tasks list
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            assertFalse(myTasksPage.isTaskPresent(workFlowName));

            // Select "Completed" tasks and verify task is present
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // Navigate to Workflows I've started
            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify the workflow is not displayed anymore in Active WorkFlows
            assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

            // Select Completed workflows and verify workflow is displayed
            myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

            assertEquals(myWorkFlowsPage.getSubTitle(), "Completed Workflows");
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

            // Verify the completed workflow details are displayed correctly.
            List<WorkFlowDetails> workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

            assertEquals(workFlowDetails.size(), 1);
            assertEquals(workFlowDetails.get(0).getWorkFlowName(), workFlowName, "Verifying workflow name");
            assertEquals(workFlowDetails.get(0).getDue(), DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(due), "Verifying workflow due date");
            assertEquals(workFlowDetails.get(0).getStartDate().toLocalDate(), getToDaysLocalDate(), "Verify Workflow Start date");
            assertEquals(workFlowDetails.get(0).getEndDate().toLocalDate(), getToDaysLocalDate(), "Verify Workflow End date");
            assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Verifying Workflow type");
            assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Verifying Workflow Description");

            // Select WorkFlow to goto WorkFlow Details page
            workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

            assertTrue(workFlowDetailsPage.isTitlePresent());

            // Verify WorkFlow Details General Info section
            generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();

            assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
            assertEquals(generalInfo.getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW);
            assertEquals(generalInfo.getStartedBy(), getUserFullName(user1));
            assertEquals(generalInfo.getDueDate().toLocalDate(), dueDate.toLocalDate());
            assertEquals(generalInfo.getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(generalInfo.getStartDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(generalInfo.getPriority(), Priority.MEDIUM);
            assertEquals(generalInfo.getStatus(), WorkFlowStatus.WORKFLOW_COMPLETE);
            assertEquals(generalInfo.getMessage(), workFlowName);

            // Verify WorkFlow Details More Info section
            moreInfo = workFlowDetailsPage.getWorkFlowDetailsMoreInfo();

            assertEquals(moreInfo.getType(), TaskType.CLOUD_REVIEW_TASK);
            assertEquals(moreInfo.getDestination(), DOMAIN_HYBRID);
            assertEquals(moreInfo.getAfterCompletion(), KeepContentStrategy.DELETECONTENT);
            assertFalse(moreInfo.isLockOnPremise());
            assertEquals(moreInfo.getAssignmentList().size(), userNames.size());
            assertTrue(moreInfo.getAssignmentList().contains(getUserFullNameWithEmail(cloudUser, cloudUser)));

            // Verify "No Tasks" displayed in Current Tasks table
            assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed());
            currentTaskList = workFlowDetailsPage.getCurrentTasksList();
            assertEquals(currentTaskList.size(), 0);

            // Verify History Table
            historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 2);

            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.DOCUMENT_WAS_APPROVED_ON_CLOUD);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(user1));
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(0).getComment(), "");

            assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
            assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(user1));
            assertEquals(historyList.get(1).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(1).getComment(), "");

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-9724:Reject action
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create A Cloud User</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups = "DataPrepHybrid")
    public void dataPrep_9724() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameForDomain(testName + "-op", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "-cl", DOMAIN_HYBRID);
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
     * ALF-9724:Reject action ALF-15161:Cloud Review Task - Task Done
     * ALF-15162:Cloud Review Task - Task Done - Details ALF-15163:Cloud Review
     * Task - Task Done - Edit Task Details (OP) ALF-15164:Cloud Review Task -
     * Task Done - Complete (OP)
     * <ul>
     * <li>1) Login as Cloud User (Cloud) and create a site</li>
     * <li>2) Login as User1 (OP), create a site and upload a document</li>
     * <li>3) Start a "Cloud Task or Review" workflow</li>
     * <li>4) Select uploaded file, Fill the form details and start workflow (Task Type : Cloud Review Task, Priority: Medium, Keep Content Strategy: Delete
     * Content, Destination: CloudSite, Reviewer: CloudUser)</li>
     * <li>5) Open Site Document Library, verify the document is prat of the workflow, document is synced and verify Sync Status</li>
     * <li>6) Login as Cloud User User, Verify the Document is Synced and it is part of WorkFlow</li>
     * <li>7) Navigate to MyTasks page and Verify Task is displayed</li>
     * <li>8) Select the Task to go to Task Details page</li>
     * <li>9) Verify Task Info section on Task Details page</li>
     * <li>10) Verify Task Status is "Not yet Started"</li>
     * <li>11) Verify Item details in Task Details Page</li>
     * <li>12) Verify Comment is "(None)"</li>
     * <li>13) Select "Edit" button to go to "Edit Task Page"</li>
     * <li>14) Verify Task Info in Edit Task Page</li>
     * <li>15) Verify Task item details in Edit Task Page</li>
     * <li>16) Verify Status Drop down options</li>
     * <li>17) Select Edit button, select "Completed" option from Status drop down, update Comment and click on "Reject" button</li>
     * <li>18) Verify Status now changed to "Completed" and Comment is updated in Task Details Page</li>
     * <li>19) Navigate to My Tasks page and verify Task is disappeared from Active Tasks list</li>
     * <li>20) Select Completed Tasks and verify the task is present</li>
     * <li>21) Select the Task to goto Task Details Page and Verify Edit button is not present, Task Status is "Completed" and Correct value is displayed in
     * Comments</li>
     * <li>22) Login as OP user and navigate to My Tasks page</li>
     * <li>23) Verify a new task is displayed for OP user</li>
     * <li>24) Navigate to My WorkFlows page and select the WorkFlow to open WorkFlow Details page</li>
     * <li>25) Verify WorkFlow General Info, More Info sections</li>
     * <li>26) Verify "No Tasks" message is not displayed in Current Tasks table</li>
     * <li>27) Verify Current Tasks table</li>
     * <li>28) Verify History table</li>
     * <li>29) Select "Task Details" link from Current Tasks table</li>
     * <li>30) Verify Task Details page header and Edit button is present in Task Details page</li>
     * <li>31) Verify Task Info section</li>
     * <li>32) Verify Task Status is "Not yet started"</li>
     * <li>33) Verify Item details</li>
     * <li>34) Verify "Required Approval Percentage" and "Actual Approval Percentage"</li>
     * <li>35) Verify Comment given by Cloud is displayed correctly</li>
     * <li>36) Select "Edit" button to goto Edit Task Page</li>
     * <li>37) Verify Task Info, Item details, Status drop down options on Edit Task page</li>
     * <li>38) Select Edit button, select Task Status as "Completed" and Select "Task Done" button</li>
     * <li>39) Verify Task status is now changed to "Completed" on Task Details page</li>
     * <li>40) Navigate to My Tasks page and verify task is disappeared from Active Tasks list</li>
     * <li>41) Select "Completed" tasks and verify task is present</li>
     * <li>42) Navigate to Workflows I've started</li>
     * <li>43) Verify the workflow is not displayed anymore in Active WorkFlows</li>
     * <li>44) Select Completed workflows and verify workflow is displayed</li>
     * <li>45) Verify the completed workflow details are displayed correctly.</li>
     * <li>46) Select WorkFlow to goto WorkFlow Details page</li>
     * <li>47) Verify WorkFlow Details General Info and More Info sections</li>
     * <li>48) Verify "No Tasks" displayed in Current Tasks table</li>
     * <li>49) Verify History Table</li>
     * </ul>
     */
    @Test(groups = "Hybrid")
    public void ALF_9724() throws Exception
    {
        String testName = getTestName();

        String uniqueString = String.valueOf(System.currentTimeMillis()).substring(7, 12);

        String user1 = getUserNameForDomain(testName + "-op", DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName + "-cl", DOMAIN_HYBRID);

        String opSiteName = getSiteName(testName).replace(testName, "") + uniqueString + "-OP";
        String cloudSite = getSiteName(testName).replace(testName, "") + uniqueString + "-CL";

        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName = testName + System.currentTimeMillis() + "-WorkFlow";

        String due = getDueDateString();
        DateTime dueDate = getDueDate(due);
        int requiredApprovalPercentage = 50;

        String cloudComment = testName + System.currentTimeMillis() + "-Cloud Comment";

        try
        {
            // Login as Cloud User (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP), create a site and upload a document
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.openDocumentLibrary(drone);
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

            // Start a "Cloud Task or Review" workflow
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setMessage(workFlowName);
            formDetails.setDueDate(due);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

            // Create Task Type : Cloud Review Task, Keep Content Strategy: Delete Content
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
            assertTrue(checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

            ShareUser.logout(drone);

            // Login as Cloud User User, Verify the Document is Synced and it is part of WorkFlow
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Navigate to MyTasks page and Verify Task is displayed
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // Select the Task to go to Task Details page
            TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

            // Verify Task Details Page Header
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), getCloudReviewTaskDetailsHeader(workFlowName));

            // Verify Task Info section on Task Details page
            TaskInfo taskInfo = taskDetailsPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(cloudUser));
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            assertEquals(taskInfo.getDueDate().toLocalDate(), dueDate.toLocalDate());
            // TODO - ALF-20755
            // assertTrue(taskInfo.getDueDateString().equals(dueDate.toString("E dd MMM yyy")),"Incorrect Date Format: ALF-20755");
            assertNotNull(taskInfo.getIdentifier());
            assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

            // Verify Task Status is "Not yet Started"
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // Verify Item details in Task Details Page
            List<TaskItem> taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);
            assertEquals(taskItems.get(0).getDescription(), NONE);
            assertEquals(taskItems.get(0).getDateModified().toLocalDate(), getToDaysLocalDate());

            // Verify Comment is "(None)"
            assertEquals(taskDetailsPage.getComment(), NONE);

            // Select "Edit" button to go to "Edit Task Page"
            EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();

            // Verify Task Info in Edit Task Page
            taskInfo = editTaskPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(cloudUser));
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            // TODO - UnComment when ALF-20756 is fixed 
            // assertTrue(taskInfo.getDueDateString().contains(dueDate.toString("dd MMM, YYYY")), "Expected: " + taskInfo.getDueDateString() + " Actual: " + dueDate.toString("dd MMM, YYYY") + "Incorrect Date Format: ALF-20756");
            assertNotNull(taskInfo.getIdentifier());
            assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

            // Verify Task item details in Edit Task Page
            taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);
            assertEquals(taskItems.get(0).getDescription(), NONE);
            assertEquals(taskItems.get(0).getDateModified().toLocalDate(), getToDaysLocalDate());

            // Verify Status Drop down options
            List<TaskStatus> statusOptions = editTaskPage.getStatusOptions();

            assertEquals(statusOptions.size(), TaskStatus.values().length);
            assertTrue(statusOptions.containsAll(getTaskStatusList()));

            // Select "Completed" Status, Comment (Eg: Completed Comment) and
            // Click on Reject button
            taskDetailsPage = ShareUserWorkFlow.completeTask(hybridDrone, TaskStatus.COMPLETED, cloudComment, EditTaskAction.REJECT).render();

            // Verify Status now changed to "Completed" and Comment is updated
            // in Task Details Page
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
            assertEquals(taskDetailsPage.getComment(), cloudComment);

            // Navigate to My Tasks page and verify Task is disappeared from
            // Active Tasks list
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertFalse(myTasksPage.isTaskPresent(workFlowName));

            // Select Completed Tasks and verify the task is present
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // Select the Task to goto Task Details Page and Verify Edit button
            // is not present, Task Status is "Completed" and Correct value is
            // displayed in Comments
            taskDetailsPage = myTasksPage.selectViewTasks(workFlowName).render();

            assertFalse(taskDetailsPage.isEditButtonPresent());
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);
            assertEquals(taskDetailsPage.getComment(), cloudComment);

            // Verify Task History Details

            TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName);

            // General Info verifications
            WorkFlowDetailsGeneralInfo generalInfo = taskHistoryPage.getWorkFlowDetailsGeneralInfo();

            assertEquals(getLocalDate(generalInfo.getCompletedDate()), getToDaysLocalDate());
            assertEquals(generalInfo.getStatus(), WorkFlowStatus.TASK_COMPLETE);

            assertTrue(taskHistoryPage.isNoTasksMessageDisplayed(), "Verify \"No Tasks\" message is displayed in Current Tasks table");

            // Verify Task History Page History List
            List<WorkFlowDetailsHistory> historyList = taskHistoryPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 2);

            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.REVIEW);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(cloudUser));
            assertEquals(getLocalDate(historyList.get(0).getCompletedDate()), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.REJECTED);
            assertEquals(historyList.get(0).getComment(), cloudComment);

            assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_REVIEW);
            assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(cloudUser));
            assertEquals(getLocalDate(historyList.get(1).getCompletedDate()), getToDaysLocalDate());
            assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(1).getComment(), "");

            ShareUser.logout(hybridDrone);

            // Login as OP user and navigate to My Tasks page
            sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            ShareUserWorkFlow.navigateToMyTasksPage(drone);

            // Verify a new task is displayed for OP user
            assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName));

            // Navigate to My WorkFlows page and select the WorkFlow to open
            // WorkFlow Details page
            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            WorkFlowDetailsPage workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

            assertTrue(workFlowDetailsPage.isTitlePresent());

            // Verify WorkFlow General Info section
            generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();

            assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
            assertEquals(generalInfo.getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW);
            assertEquals(generalInfo.getStartedBy(), getUserFullName(user1));
            assertEquals(generalInfo.getDueDate().toLocalDate(), dueDate.toLocalDate());
            assertEquals(generalInfo.getCompleted(), "<in progress>");
            assertEquals(generalInfo.getStartDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(generalInfo.getPriority(), Priority.MEDIUM);
            assertEquals(generalInfo.getStatus(), WorkFlowStatus.WORKFLOW_IN_PROGRESS);
            assertEquals(generalInfo.getMessage(), workFlowName);

            // Verify WorkFlow More Info section
            WorkFlowDetailsMoreInfo moreInfo = workFlowDetailsPage.getWorkFlowDetailsMoreInfo();

            assertEquals(moreInfo.getType(), TaskType.CLOUD_REVIEW_TASK);
            assertEquals(moreInfo.getDestination(), DOMAIN_HYBRID);
            assertEquals(moreInfo.getAfterCompletion(), KeepContentStrategy.DELETECONTENT);
            assertFalse(moreInfo.isLockOnPremise());
            assertEquals(moreInfo.getAssignmentList().size(), userNames.size());
            assertTrue(moreInfo.getAssignmentList().contains(getUserFullNameWithEmail(cloudUser, cloudUser)));

            // Verify "No Tasks" message is not displayed in Current Tasks table
            assertFalse(workFlowDetailsPage.isNoTasksMessageDisplayed());

            // Verify Current Tasks table
            List<WorkFlowDetailsCurrentTask> currentTaskList = workFlowDetailsPage.getCurrentTasksList();

            assertEquals(currentTaskList.size(), 1);
            assertEquals(currentTaskList.get(0).getTaskType(), CurrentTaskType.DOCUMENT_WAS_REJECTED_ON_CLOUD);
            assertEquals(currentTaskList.get(0).getAssignedTo(), getUserFullName(user1));
            assertEquals(currentTaskList.get(0).getDueDate().toLocalDate(), dueDate.toLocalDate());
            assertEquals(currentTaskList.get(0).getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // Verify History table
            historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 1);
            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(user1));
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(0).getComment(), "");

            // Select "Task Details" link from Current Tasks table
            taskDetailsPage = currentTaskList.get(0).getTaskDetailsLink().click().render();

            // Verify Task Details page header and Edit button is present in
            // Task Details page
            assertEquals(taskDetailsPage.getTaskDetailsHeader(), "Details: " + workFlowName + " (Document was rejected on the cloud)");
            assertTrue(taskDetailsPage.isEditButtonPresent());

            // Verify Task Info section
            taskInfo = taskDetailsPage.getTaskDetailsInfo();
            assertEquals(taskInfo.getMessage(), workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(user1));
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            // TODO - Known Issue: ALF-20756
            // assertTrue(taskInfo.getDueDateString().contains(dueDate.toString("E dd MMM YYYY")), "Actual: " + taskInfo.getDueDateString() + " Expected: " + dueDate.toString("E dd MMM YYYY") + "Incorrect Date Format: ALF-20756");
            assertNotNull(taskInfo.getIdentifier());
            assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

            // Verify Task Status is "Not yet started"
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.NOTYETSTARTED);

            // Verify Item details
            taskItems = taskDetailsPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);
            assertEquals(taskItems.get(0).getDescription(), NONE);
            assertEquals(taskItems.get(0).getDateModified().toLocalDate(), getToDaysLocalDate());

            // Verify "Required Approval Percentage" and
            // "Actual Approval Percentage"
            assertEquals(taskDetailsPage.getRequiredApprovalPercentage(), requiredApprovalPercentage);
            assertEquals(taskDetailsPage.getActualApprovalPercentage(), 0);

            // Verify Comment given by Cloud is displayed correctly
            assertEquals(taskDetailsPage.getComment(), getUserFullName(cloudUser) + ": " + cloudComment + "  (Rejected)");

            // Select "Edit" button to goto Edit Task Page
            editTaskPage = taskDetailsPage.selectEditButton().render();

            // Verify Task Info in Edit Task page
            taskInfo = editTaskPage.getTaskDetailsInfo();

            assertEquals(taskInfo.getMessage(), workFlowName);
            assertEquals(taskInfo.getOwner(), getUserFullName(user1));
            assertEquals(taskInfo.getPriority(), Priority.MEDIUM);
            // TODO - ALF-20756
            // assertTrue(taskInfo.getDueDateString().contains(dueDate.toString("E dd MMM YYYY")), "Actual: " + taskInfo.getDueDateString() + " Expected: " + dueDate.toString("E dd MMM YYYY") + "Incorrect Date Format: ALF-20756");
            assertNotNull(taskInfo.getIdentifier());
            assertFalse(StringUtils.isEmpty(taskInfo.getIdentifier()));

            // Verify Item Details
            taskItems = editTaskPage.getTaskItems();
            assertEquals(taskItems.size(), 1);
            assertEquals(taskItems.get(0).getItemName(), fileName);
            assertEquals(taskItems.get(0).getDescription(), NONE);
            assertEquals(taskItems.get(0).getDateModified().toLocalDate(), getToDaysLocalDate());

            // Verify Status Drop down options
            statusOptions = editTaskPage.getStatusOptions();

            assertEquals(statusOptions.size(), TaskStatus.values().length);
            assertTrue(statusOptions.containsAll(getTaskStatusList()));

            // Select "Completed" status option and select Cancel button
            editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
            taskDetailsPage = editTaskPage.selectTaskDoneButton().render();

            // Verify Task status is now changed to "Completed" on Task Details
            // page
            assertEquals(taskDetailsPage.getTaskStatus(), TaskStatus.COMPLETED);

            // Navigate to My Tasks page and verify task is disappeared from
            // Active Tasks list
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
            assertFalse(myTasksPage.isTaskPresent(workFlowName));

            // Select "Completed" tasks and verify task is present
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            // Navigate to Workflows I've started
            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify the workflow is not displayed anymore in Active WorkFlows
            assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

            // Select Completed workflows and verify workflow is displayed
            myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

            assertEquals(myWorkFlowsPage.getSubTitle(), "Completed Workflows");
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

            // Verify the completed workflow details are displayed correctly.
            List<WorkFlowDetails> workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);

            assertEquals(workFlowDetails.size(), 1);
            assertEquals(workFlowDetails.get(0).getWorkFlowName(), workFlowName, "Verifying workflow name");
            assertEquals(workFlowDetails.get(0).getDue(), DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(due), "Verifying workflow due date");
            assertEquals(workFlowDetails.get(0).getStartDate().toLocalDate(), getToDaysLocalDate(), "Verify Workflow Start date");
            assertEquals(workFlowDetails.get(0).getEndDate().toLocalDate(), getToDaysLocalDate(), "Verify Workflow End date");
            assertEquals(workFlowDetails.get(0).getType(), WorkFlowType.CLOUD_TASK_OR_REVIEW, "Verifying Workflow type");
            assertEquals(workFlowDetails.get(0).getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW, "Verifying Workflow Description");

            // Select WorkFlow to goto WorkFlow Details page
            workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();

            assertTrue(workFlowDetailsPage.isTitlePresent());

            // Verify WorkFlow Details General Info section
            generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();

            assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
            assertEquals(generalInfo.getDescription(), WorkFlowDescription.CREATE_A_TASK_OR_START_A_REVIEW);
            assertEquals(generalInfo.getStartedBy(), getUserFullName(user1));

            // TODO - KNOWN ISSUE: ALF-20756
            // assertTrue(generalInfo.getDueDateString().contains(dueDate.toString("E dd MMM YYYY")), "Actual: " + taskInfo.getDueDateString() + " Expected: " + dueDate.toString("E dd MMM YYYY") + "Incorrect Date Format: ALF-20756");
            assertEquals(generalInfo.getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(generalInfo.getStartDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(generalInfo.getPriority(), Priority.MEDIUM);
            assertEquals(generalInfo.getStatus(), WorkFlowStatus.WORKFLOW_COMPLETE);
            assertEquals(generalInfo.getMessage(), workFlowName);

            // Verify WorkFlow Details More Info section
            moreInfo = workFlowDetailsPage.getWorkFlowDetailsMoreInfo();

            assertEquals(moreInfo.getType(), TaskType.CLOUD_REVIEW_TASK);
            assertEquals(moreInfo.getDestination(), DOMAIN_HYBRID);
            assertEquals(moreInfo.getAfterCompletion(), KeepContentStrategy.DELETECONTENT);
            assertFalse(moreInfo.isLockOnPremise());
            assertEquals(moreInfo.getAssignmentList().size(), userNames.size());
            assertTrue(moreInfo.getAssignmentList().contains(getUserFullNameWithEmail(cloudUser, cloudUser)));

            // Verify "No Tasks" displayed in Current Tasks table
            assertTrue(workFlowDetailsPage.isNoTasksMessageDisplayed());
            currentTaskList = workFlowDetailsPage.getCurrentTasksList();
            assertEquals(currentTaskList.size(), 0);

            // Verify History Table
            historyList = workFlowDetailsPage.getWorkFlowHistoryList();

            assertEquals(historyList.size(), 2);

            assertEquals(historyList.get(0).getType(), WorkFlowHistoryType.DOCUMENT_WAS_REJECTED_ON_CLOUD);
            assertEquals(historyList.get(0).getCompletedBy(), getUserFullName(user1));
            assertEquals(historyList.get(0).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(0).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(0).getComment(), "");

            assertEquals(historyList.get(1).getType(), WorkFlowHistoryType.START_TASK_OR_REVIEW_ON_CLOUD);
            assertEquals(historyList.get(1).getCompletedBy(), getUserFullName(user1));
            assertEquals(historyList.get(1).getCompletedDate().toLocalDate(), getToDaysLocalDate());
            assertEquals(historyList.get(1).getOutcome(), WorkFlowHistoryOutCome.TASK_DONE);
            assertEquals(historyList.get(1).getComment(), "");

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }
}
