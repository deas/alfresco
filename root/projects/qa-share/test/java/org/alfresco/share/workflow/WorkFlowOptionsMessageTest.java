package org.alfresco.share.workflow;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskInfo;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDetailsGeneralInfo;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowTitle;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


/**
 * @author Ranjith Manyam
 * 
 */
@Listeners(FailedTestListener.class)
public class WorkFlowOptionsMessageTest extends AbstractWorkflow
{

    private String testDomain;
    
    /**
     * Class includes: Tests from TestLink in Area: Hybrid Workflow/WorkFlow Actions
     */
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
    }

    public void dataPrep(String testName) throws Exception
    {
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
     * ALF-15186:Message - Empty
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15186() throws Exception
    {
        dataPrep(getTestName());
      //TODO: TestLink- Update the TestLink testcase to move the related precondition steps into test steps. 
    }

    /**
     * ALF-15186:Message - Empty
     */
     @Test(groups = "Hybrid", enabled = true)
    public void ALF_15186() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String defaultWorkFlowName = "(No Message)";
        String defaultTaskName = "Review Task";
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
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo1).render();

            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            formDetails.setDueDate(dueDate);
            formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setAssignee(cloudUser);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setLockOnPremise(false);

            // Create Workflow using File1
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            // Verify Workflows are created successfully
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(defaultWorkFlowName), "Verifying workflow1 exists");

            WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, defaultWorkFlowName);

            WorkFlowDetailsGeneralInfo generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();
            assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
            assertEquals(generalInfo.getMessage(), defaultWorkFlowName);

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertTrue(myTasksPage.isTaskPresent(defaultWorkFlowName));

            /*      TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, defaultTaskName);

            generalInfo = taskHistoryPage.getWorkFlowDetailsGeneralInfo();
            assertEquals(generalInfo.getTitle(), WorkFlowTitle.HYBRID_ADHOC_TASK_PROCESS);
            assertEquals(generalInfo.getMessage(), defaultWorkFlowName);*/

            TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(hybridDrone, defaultTaskName);
            TaskInfo taskDetailsInfo = taskDetailsPage.getTaskDetailsInfo();
            assertEquals(taskDetailsInfo.getMessage(), defaultTaskName);

            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
        finally
        {
            // Login as User1 (OP) and Cancel the workflow
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            myWorkFlowsPage = myWorkFlowsPage.cancelWorkFlow(defaultWorkFlowName).render();

            assertFalse(myWorkFlowsPage.isWorkFlowPresent(defaultWorkFlowName));

            ShareUser.logout(drone);
        }
    }

    /**
     * ALF-15187:Message - Single line
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15187() throws Exception
    {
        dataPrep(getTestName());
      //TODO: TestLink- Update the TestLink testcase to move the related precondition steps into test steps. 
    }

    /**
     * ALF-15187:Message - Single line
     */
     @Test(groups = "Hybrid", enabled = true)
    public void ALF_15187() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String workFlowName = testName + System.currentTimeMillis() + "-1-WF";
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
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo1).render();

            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            formDetails.setMessage(workFlowName);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setAssignee(cloudUser);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setLockOnPremise(false);

            // Create Workflow using File1
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            // Verify Workflows are created successfully
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying workflow1 exists");

            WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, workFlowName);

            WorkFlowDetailsGeneralInfo generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();
            assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
            assertEquals(generalInfo.getMessage(), workFlowName);

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName);

   /*         generalInfo = taskHistoryPage.getWorkFlowDetailsGeneralInfo();
            assertEquals(generalInfo.getTitle(), WorkFlowTitle.HYBRID_ADHOC_TASK_PROCESS);
            assertEquals(generalInfo.getMessage(), workFlowName);*/

            TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(hybridDrone, workFlowName);
            TaskInfo taskDetailsInfo = taskDetailsPage.getTaskDetailsInfo();
            assertEquals(taskDetailsInfo.getMessage(), workFlowName);

            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
        finally
        {
            // Login as User1 (OP) and Cancel the workflow
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            myWorkFlowsPage = myWorkFlowsPage.cancelWorkFlow(workFlowName).render();

            assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

            ShareUser.logout(drone);
        }
    }

    /**
     * ALF-15188:Message - Multiple lines
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15188() throws Exception
    {
        dataPrep(getTestName() + "1");
      //TODO: TestLink- Update the TestLink testcase to move the related precondition steps into test steps. 
    }

    // TODO - Extra spaces in Message if the workflow is created with message contains multiple lines (ALF-20523)
    /**
     * ALF-15188:Message - Multiple lines
     */
     @Test(groups = "Hybrid", enabled = true)
    public void ALF_15188() throws Exception
    {
        String testName = getTestName()+ "1";
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String workFlowName = testName + System.currentTimeMillis() + "-1-WF\n" + testName + System.currentTimeMillis() + "-2-WF";
//        String workFlowName = testName + System.currentTimeMillis() + "-1-WF\n" + "   " + testName + System.currentTimeMillis() + "-2-WF";
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
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo1).render();

            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

//            formDetails.setMessage(workFlowName.replace("   ", ""));
            formDetails.setMessage(workFlowName);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setAssignee(cloudUser);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setLockOnPremise(false);

            // Create Workflow using File1
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify Workflows are created successfully
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying workflow1 exists");

            WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, workFlowName);

            WorkFlowDetailsGeneralInfo generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();
            assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
            assertEquals(StringUtils.deleteWhitespace(generalInfo.getMessage()), StringUtils.deleteWhitespace(workFlowName));

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertTrue(myTasksPage.isTaskPresent(workFlowName));

            TaskHistoryPage taskHistoryPage = ShareUserWorkFlow.navigateToTaskHistoryPage(hybridDrone, workFlowName);

//TODO: Added issue since Hybrid Task is newly added workflow type, And code is Commented 
            /*generalInfo = taskHistoryPage.getWorkFlowDetailsGeneralInfo();
            assertEquals(generalInfo.getTitle(), WorkFlowTitle.HYBRID_ADHOC_TASK_PROCESS);
            assertEquals(generalInfo.getMessage(), workFlowName);*/

            TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(hybridDrone, workFlowName);
            TaskInfo taskDetailsInfo = taskDetailsPage.getTaskDetailsInfo();
//            assertEquals(taskDetailsInfo.getMessage(), workFlowName.replace("   ", "").replace("\n", " "));
            assertEquals(taskDetailsInfo.getMessage(), workFlowName.replace("\n", " "));

            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
        finally
        {
            // Login as User1 (OP) and Cancel the workflow
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            myWorkFlowsPage = myWorkFlowsPage.cancelWorkFlow(workFlowName).render();

            assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName));

            ShareUser.logout(drone);
        }
    }

    /**
     * ALF-15189:Message - Negative case
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15189() throws Exception
    {
        dataPrep(getTestName() + "0");
      //TODO: TestLink- Update the TestLink testcase to move the related precondition steps into test steps. 
    }

    /**
     * ALF-15189:Message - Negative case
     */
     @Test(groups = "Hybrid", enabled = true)
    public void ALF_15189() throws Exception
    {
        String testName = getTestName()+ "0";
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = getFileName(testName) + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String workFlowName1 = "!@#$%^&*()_+|/?.,<>:;''=-{}[]" + System.currentTimeMillis() + "-1-WF";
        String workFlowName2 = " ";
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
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo1).render();
            documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2).render();

            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            formDetails.setMessage(workFlowName1);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
            formDetails.setTaskPriority(Priority.HIGH);
            formDetails.setSiteName(cloudSite);
            formDetails.setAssignee(cloudUser);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setLockOnPremise(false);

            // Create Workflow using File1
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify Workflows are created successfully
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

            WorkFlowDetailsPage workFlowDetailsPage = ShareUserWorkFlow.navigateToWorkFlowDetailsPage(drone, workFlowName1);

            WorkFlowDetailsGeneralInfo generalInfo = workFlowDetailsPage.getWorkFlowDetailsGeneralInfo();
            assertEquals(generalInfo.getTitle(), WorkFlowTitle.CLOUD_TASK_OR_REVIEW);
            assertEquals(generalInfo.getMessage(), workFlowName1);

            SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);

            cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName2);

            formDetails.setMessage(workFlowName2);

            // Create Workflow using File1
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File1 is synced");

            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify Workflows are created successfully
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName2.replace(" ", "")), "Verifying workflow2 exists");
         
            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
        finally
        {
            // Login as User1 (OP) and Cancel the workflow
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            myWorkFlowsPage = myWorkFlowsPage.cancelWorkFlow(workFlowName1).render();
            myWorkFlowsPage = myWorkFlowsPage.cancelWorkFlow(workFlowName2.replace(" ", "")).render();

            assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));

            ShareUser.logout(drone);
        }
    }
}
