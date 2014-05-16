package org.alfresco.share.workflow;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.user.Language;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


/**
 * @author Ranjith Manyam
 * 
 */
@Listeners(FailedTestListener.class)
public class HWLanguageTest extends AbstractWorkflow
{

    /**
     * Class includes: Tests from TestLink in Area: Hybrid Workflow/Advanced Scenarios
     */
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setupHybridDrone();
        testName = this.getClass().getSimpleName();
    }

    @Override
    @AfterClass(alwaysRun=true)
    public void tearDown()
    {
        super.tearDown();
    }

    /**
     * ALF-1064:L10N for Cloud Review Task
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_1063() throws Exception
    {
//        setupCustomDrone(getCustomDroneWithLanguage(Language.SPANISH));
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(customDrone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(customDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(customDrone);
    }

    /**
     * ALF-1064:L10N for Cloud Review Task
     */
    @Test
    public void ALF_1063() throws Exception
    {
//        setupCustomDrone(getCustomDroneWithLanguage(Language.SPANISH));
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
            // Change language to Deutsche
            ShareUser.changeLanguage(hybridDrone, Language.ITALIAN);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(customDrone, user1, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(customDrone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(customDrone, fileInfo1).render();

            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(customDrone, fileName1);

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
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            Assert.assertTrue(checkIfContentIsSynced(customDrone, fileName1), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(customDrone);
            // Verify Workflows are created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

            ShareUser.logout(customDrone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName1, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(customDrone, user1, DEFAULT_PASSWORD);

            ShareUserWorkFlow.navigateToMyTasksPage(customDrone);

            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName1));

            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(customDrone, workFlowName1, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(customDrone, opSiteName).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");

            ShareUser.logout(customDrone);

        }
        catch (Throwable t)
        {
            reportError(customDrone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-1064:L10N for Cloud Review Task
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_1064() throws Exception
    {
//        setupCustomDrone(getCustomDroneWithLanguage(Language.FRENCH));
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(customDrone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(customDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(customDrone);
    }

    /**
     * ALF-1064:L10N for Cloud Review Task
     */
    @Test
    public void ALF_1064() throws Exception
    {
//        setupCustomDrone(getCustomDroneWithLanguage(Language.FRENCH));
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
            // Change language to Deutsche
            ShareUser.changeLanguage(hybridDrone, Language.DEUTSCHE);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(customDrone, user1, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(customDrone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(customDrone, fileInfo1).render();

            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(customDrone, fileName1);

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
            formDetails.setApprovalPercentage(requiredApprovalPercentage);
            formDetails.setLockOnPremise(false);

            // Create Workflow using File1
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            Assert.assertTrue(checkIfContentIsSynced(customDrone, fileName1), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(customDrone);
            // Verify Workflows are created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

            ShareUser.logout(customDrone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName1, TaskStatus.COMPLETED, EditTaskAction.APPROVE);

            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(customDrone, user1, DEFAULT_PASSWORD);

            ShareUserWorkFlow.navigateToMyTasksPage(customDrone);

            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(customDrone, workFlowName1));

            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(customDrone, workFlowName1, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(customDrone, opSiteName).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");

            ShareUser.logout(customDrone);

        }
        catch (Throwable t)
        {
            reportError(customDrone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }
}
