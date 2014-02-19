package org.alfresco.share.workflow;

import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskDetailsType;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


/**
 * @author Ranjith Manyam
 * 
 */
public class WorkFlowActionsTest extends AbstractWorkflow
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
     * ALF-9528:Simple Cloud Task - Cancel Workflow (OP)
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_9528() throws Exception
    {
        dataPrep(getTestName());
        //TODO: TestLink- Update the TestLink testcase to move the related precondition steps into test steps. 
    }

    /**
     * ALF-9528:Simple Cloud Task - Cancel Workflow (OP)
     * <ul>
     * <li>1) Login as User1 (Cloud) and Create a site</li>
     * <li>2) Login as User1 (OP), create a site and upload 2 documents</li>
     * <li>3) TODO - COMPLETE AFTER REVIEW </li>
     * <li>4) </li>
     * <li>5) </li>
     * <li>6) </li>
     * <li>8) </li>
     * <li>9) </li>
     * <li>10) </li>
     * <li>11) </li>
     * <li>12) </li>
     * <li>13) </li>
     * <li>14) </li>
     * <li>15) </li>
     * </ul>
     */
     @Test(groups = "Hybrid", enabled = true)
    public void ALF_9528() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
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

            formDetails.setMessage(workFlowName1);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setAssignee(cloudUser);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setLockOnPremise(false);

            // Create Workflow using File1
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            //TODO: Update the TestLink testcase to add steps for the below assertions otherwise remove these steps from code.
            
            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            // Verify Workflows are created successfully
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

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

            assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP) and Cancel the workflow
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            myWorkFlowsPage = myWorkFlowsPage.cancelWorkFlow(workFlowName1).render();

            assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));
          
            //TODO: Amend the TestLink test 2nd step to verify the workflow only as task will not be present in mytasks.
            
            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Navigate to MyTasks page and verify both tasks are present
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertFalse(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-15167:Simple Cloud Task - Cancel Workflow (Cloud)
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15167() throws Exception
    {
        dataPrep(getTestName());
      //TODO: TestLink- Update the TestLink testcase to move the related precondition steps into test steps. 
    }

    /**
     * ALF-15167:Simple Cloud Task - Cancel Workflow (Cloud)
     * <ul>
     * <li>1) Login as User1 (Cloud) and Create a site</li>
     * <li>2) Login as User1 (OP), create a site and upload 2 documents</li>
     * <li>3) TODO - COMPLETE AFTER REVIEW </li>
     * <li>4) </li>
     * <li>5) </li>
     * <li>6) </li>
     * <li>8) </li>
     * <li>9) </li>
     * <li>10) </li>
     * <li>11) </li>
     * <li>12) </li>
     * <li>13) </li>
     * <li>14) </li>
     * <li>15) </li>
     * </ul>
     */
     @Test(groups = "Hybrid", enabled = true)
    public void ALF_15167() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
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

            formDetails.setMessage(workFlowName1);
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
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = ShareUserWorkFlow.cancelTaskFromMyTasksPage(hybridDrone, workFlowName1);

            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            myTasksPage =  ShareUserWorkFlow.navigateToMyTasksPage(drone);

            assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));

            TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName1);

            assertEquals(taskDetails.getType(), TaskDetailsType.WORKFLOW_CANCELLED_ON_THE_CLOUD);

            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-15168:Cloud Review Task - Cancel Workflow (OP)
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15168() throws Exception
    {
        dataPrep(getTestName());
        //TODO: TestLink- Update the TestLink testcase to move the related precondition steps into test steps. 
    }

    /**
     * ALF-15168:Cloud Review Task - Cancel Workflow (OP)
     * <ul>
     * <li>1) Login as User1 (Cloud) and Create a site</li>
     * <li>2) Login as User1 (OP), create a site and upload 2 documents</li>
     * <li>3) TODO - COMPLETE AFTER REVIEW </li>
     * <li>4) </li>
     * <li>5) </li>
     * <li>6) </li>
     * <li>8) </li>
     * <li>9) </li>
     * <li>10) </li>
     * <li>11) </li>
     * <li>12) </li>
     * <li>13) </li>
     * <li>14) </li>
     * <li>15) </li>
     * </ul>
     */
     @Test(groups = "Hybrid", enabled = true)
    public void ALF_15168() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
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
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo1).render();

            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            formDetails.setMessage(workFlowName1);
            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setLockOnPremise(false);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);

            // Create Workflow using File1
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            // Verify Workflows are created successfully
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

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

            assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            myWorkFlowsPage = myWorkFlowsPage.cancelWorkFlow(workFlowName1).render();

            assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));
          //TODO: Amend the TestLink test 2nd step to verify the workflow only as task will not be present in mytasks.
            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Navigate to MyTasks page and verify both tasks are present
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertFalse(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-15169:Cloud Review Task - Cancel Workflow (Cloud)
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15169() throws Exception
    {
        dataPrep(getTestName());
      //TODO: TestLink- Update the TestLink testcase to move the related precondition steps into test steps. 
    }

    /**
     * ALF-15169:Cloud Review Task - Cancel Workflow (Cloud)
     * <ul>
     * <li>1) Login as User1 (Cloud) and Create a site</li>
     * <li>2) Login as User1 (OP), create a site and upload 2 documents</li>
     * <li>3) TODO - COMPLETE AFTER REVIEW </li>
     * <li>4) </li>
     * <li>5) </li>
     * <li>6) </li>
     * <li>8) </li>
     * <li>9) </li>
     * <li>10) </li>
     * <li>11) </li>
     * <li>12) </li>
     * <li>13) </li>
     * <li>14) </li>
     * <li>15) </li>
     * </ul>
     */
     @Test(groups = "Hybrid", enabled = true)
    public void ALF_15169() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
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

            formDetails.setMessage(workFlowName1);
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
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = ShareUserWorkFlow.cancelTaskFromMyTasksPage(hybridDrone, workFlowName1);

            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

            assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));

            TaskDetails taskDetails = myTasksPage.getTaskDetails(workFlowName1);

            assertEquals(taskDetails.getType(), TaskDetailsType.WORKFLOW_CANCELLED_ON_THE_CLOUD);

            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");


            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-9570:Cancel Workflow if the document is locked in OP (OP)
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_9570() throws Exception
    {
        dataPrep(getTestName());
      //TODO: TestLink- Update the TestLink testcase to move the related precondition steps into test steps. 
    }

    /**
     * ALF-9570:Cancel Workflow if the document is locked in OP (OP)
     */
     @Test(groups = "Hybrid", enabled = true)
    public void ALF_9570() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName1 = testName + System.currentTimeMillis() + "-WF";
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
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload a file
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

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
            formDetails.setLockOnPremise(true);

            // Select Start WorkFlow from Document Library Page
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the File1 is synced");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "Verifying the File1 is Locked");
            assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getContentInfo(), "This document is locked by you.", "Verifying Locked message");
            assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            // Verify Workflows are created successfully
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying File1 exists");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of workflow");
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "Verifying the document is NOT locked");

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            myWorkFlowsPage = ShareUserWorkFlow.cancelWorkFlow(drone, workFlowName1);

            assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the File1 is synced");
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "Verifying the File1 is Locked");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertFalse(documentLibraryPage.isFileVisible(fileName), "Verifying File1 exists");

            // Navigate to MyTasks page and verify both tasks are present
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-9570:Cancel Workflow if the document is locked in OP (OP)
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15246() throws Exception
    {
        dataPrep(getTestName());
      //TODO: TestLink- Update the TestLink testcase to move the related precondition steps into test steps. 
    }

    /**
     * ALF-9570:Cancel Workflow if the document is locked in OP (OP)
     */
     @Test(groups = "Hybrid", enabled = true)
    public void ALF_15246() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String workFlowName1 = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();
        int requiredApprovalPercentage = 100;

        try
        {
            // CLOUD: User1 creates Site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            
            ShareUser.logout(hybridDrone);

            // OP: User 1 creates site, content
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            ShareUser.uploadFileInFolder(drone, fileInfo);

            // OP: User 1 creates a Cloud Review Task - Workflow
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
            formDetails.setLockOnPremise(false);

            // Select Start WorkFlow from Document Library Page
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

            DocumentLibraryPage documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the File1 is synced");
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "Verifying the File1 is Locked");
            assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName), "Verifying the Sync Status is \"Synced\"");

            // Verify Workflow is created successfully            
            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite);

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying File1 exists");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the document is synced");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the document is part of workflow");
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "Verifying the document is NOT locked");

            documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectEditOffline().render();

            assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getContentInfo(), "This document is locked by you for offline editing.");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isEdited(), "Verifying the document is being edited");

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            myWorkFlowsPage = ShareUserWorkFlow.cancelWorkFlow(drone, workFlowName1);

            assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File1 is Cloud Synced, part of workflow
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the File1 is synced");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying File1 exists");

            documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectCancelEditing().render();
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isEdited());

            // Navigate to MyTasks page and verify both tasks are present
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            myWorkFlowsPage = ShareUserWorkFlow.cancelWorkFlow(drone, workFlowName1);

            assertFalse(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying the File1 is synced");
            assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "Verifying the File1 is Locked");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            assertFalse(documentLibraryPage.isFileVisible(fileName), "Verifying File1 exists");

            // Navigate to MyTasks page and verify both tasks are present
            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }
}
