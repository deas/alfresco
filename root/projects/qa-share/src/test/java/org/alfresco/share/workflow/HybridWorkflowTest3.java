package org.alfresco.share.workflow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPopup;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.workflow.AssignmentPage;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDetailsCurrentTask;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.share.util.AbstractCloudSyncTest;
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
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Chiran
 *
 */

@Listeners(FailedTestListener.class)
public class HybridWorkflowTest3 extends AbstractWorkflow
{
    private static Log logger = LogFactory.getLog(HybridWorkflowTest3.class);
    private String invitedDomain1 = "invited1.test";
    private String invitedDomain2 = "invited2.test";
    private String trialDomain1 = "trial1.test";
    private String partnerDomain1 = "partner1.test";
    private static final String START_WORKFLOW = "Start Workflow";
    private static final String DDMMYYYY = "dd/MM/yyyy";

    String cloudCollaboratorSite = null;
    String cloudContributorSite = null;
    String cloudConsumerSite = null;

    String workFlowName ="";
    String dueDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    String cloudComment = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        
        // The below method is mandatory call to intitiate the required domains for the first time.
        createSampleUsersAndUpgradeDomains();
        
        logger.info("HybridWorkFlowTest3 tests started at " + dueDate);
    }

    /**
     * This method is required to intitiate the required domains for the first time, it will be invoked before class instead of doing it every test level .
     * 
     * @throws Exception
     */
    private void createSampleUsersAndUpgradeDomains() throws Exception
    {
        String user1 = getUserNameForDomain("hybridDomainUser" + System.currentTimeMillis(), DOMAIN_HYBRID);
        String user2 = getUserNameForDomain("invitedDomain1User" + System.currentTimeMillis(), invitedDomain1);
        String user3 = getUserNameForDomain("invitedDomain2User" + System.currentTimeMillis(), invitedDomain2);

        String[] cloudUserInfo1 = new String[] { user1 };
        String[] cloudUserInfo2 = new String[] { user2 };
        String[] cloudUserInfo3 = new String[] { user3 };

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo3);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, invitedDomain1, "1000");
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, invitedDomain2, "1000");
    }

    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_9455() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "cloud", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "OP-Site";
        String fileName = getFileName(testName) + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site and Upload document
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    /**
     * <ul>
     * <li>ALF-9455 / ALF-15127</li>
     * <li>1) Login as Cloud User, Create a site and Logout</li>
     * <li>2) Login as User1 (OP), Create a site and Upload a document</li>
     * <li>3) Navigate to WorkFlows I've Started page and select StartWorkflow
     * button</li>
     * <li>4) Select "Cloud Task or Review" from select a workflow dropdown</li>
     * <li>5) Verify the Cloud Task or Review page has opened.</li>
     * </ul>
     */
    @Test(groups="Hybrid")
    public void alf_9455() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + "OP-Site";
        String fileName = getFileName(testName) + ".doc";

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);

        // Open Document Details page
        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        // Select StartWorkflow
        StartWorkFlowPage startWorkFlowPage = detailsPage.selectStartWorkFlowPage().render();

        Assert.assertTrue(startWorkFlowPage.getTitle().contains(START_WORKFLOW));

        Assert.assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.CLOUD_TASK_OR_REVIEW));

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        // Verify "Simple Cloud Task" is selected
        Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent());
    }

    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15127() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "cloud", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "OP-Site";
        String fileName = getFileName(testName) + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    /**
     * <ul>
     * <li>ALF-15127</li>
     * <li>1) Login as Cloud User, Create a site and Logout</li>
     * <li>2) Login as User1 (OP), Create a site and Upload a document</li>
     * <li>3) Navigate to WorkFlows I've Started page and select StartWorkflow
     * button</li>
     * <li>4) Select "Cloud Task or Review" from select a workflow dropdown</li>
     * <li>5) Verify the Cloud Task or Review page has opened.</li>
     * </ul>
     */
    @Test(groups="Hybrid")
    public void ALF_15127() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + "OP-Site";
        String fileName = getFileName(testName) + ".doc";

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);
        // Open Document Details page
        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        // Select StartWorkflow
        StartWorkFlowPage startWorkFlowPage = detailsPage.selectStartWorkFlowIcon().render();

        Assert.assertTrue(startWorkFlowPage.getTitle().contains(START_WORKFLOW));

        Assert.assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.CLOUD_TASK_OR_REVIEW));

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        // Verify "Simple Cloud Task" is selected
        Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent());
    }

    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_9457() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "cloud", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "OP-Site";
        String fileName = getFileName(testName) + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    /**
     * <ul>
     * <li>ALF-9457</li>
     * <li>1) Login as Cloud User, Create a site and Logout</li>
     * <li>2) Login as User1 (OP), Create a site and Upload a document</li>
     * <li>3) Navigate to WorkFlows I've Started page and select StartWorkflow
     * button</li>
     * <li>4) Select "Cloud Task or Review" from select a workflow dropdown</li>
     * <li>5) Verify the Cloud Task or Review page has opened.</li>
     * </ul>
     */
    @Test(groups="Hybrid")
    public void alf_9457() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + "OP-Site";
        String fileName = getFileName(testName) + ".doc";

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);

        // Sselect StartWorkflow
        StartWorkFlowPage startWorkFlowPage = docLibPage.getFileDirectoryInfo(fileName).selectStartWorkFlow().render();

        Assert.assertTrue(startWorkFlowPage.getTitle().contains(START_WORKFLOW));

        Assert.assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.CLOUD_TASK_OR_REVIEW));

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        // Verify "Simple Cloud Task" is selected
        Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent());
    }

    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15129() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "cloud", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "OP-Site";
        String fileName = getFileName(testName) + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    @Test(groups="Hybrid")
    public void alf_15129() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + "OP-Site";
        String fileName = getFileName(testName) + ".doc";

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        
        // Open Document library, Upload a file
        DocumentLibraryPage docLibPage = ShareUser.selectContentCheckBox(drone, fileName);

        // Select StartWorkflow from Document Library Navigation
        StartWorkFlowPage startWorkFlowPage = docLibPage.getNavigation().render().selectStartWorkFlow().render();

        Assert.assertTrue(startWorkFlowPage.getTitle().contains(START_WORKFLOW));

        Assert.assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.CLOUD_TASK_OR_REVIEW));

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        // Verify "Simple Cloud Task" is selected
        Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent());
    }

    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15265() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "cloud", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    @Test(groups="Hybrid")
    public void alf_15265() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Select Workflows I have started page
        MyWorkFlowsPage workFlowPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        // Select StartWorkflow from Workflows I have started page
        StartWorkFlowPage startWorkFlowPage = workFlowPage.selectStartWorkflowButton().render();

        Assert.assertTrue(startWorkFlowPage.getTitle().contains(START_WORKFLOW));

        Assert.assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.CLOUD_TASK_OR_REVIEW));

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        // Verify "Simple Cloud Task" is selected
        Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent());
    }

    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15266() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "cloud", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    @Test(groups="Hybrid")
    public void alf_15266() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Select MyTasks page
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

        // Select StartWorkflow from MyTasks page
        StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();

        Assert.assertTrue(startWorkFlowPage.getTitle().contains(START_WORKFLOW));

        Assert.assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.CLOUD_TASK_OR_REVIEW));

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        // Verify "Simple Cloud Task" is selected
        Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent());
    }

    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15267() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "cloud", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    @Test(groups="Hybrid")
    public void alf_15267() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, DOMAIN_HYBRID);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Select My Tasks dashlet from dashboard page.
        DashBoardPage dashboardPage = ShareUser.openUserDashboard(drone);
        MyTasksDashlet myTasksDashlet = dashboardPage.getDashlet("tasks").render();

        // Select StartWorkflow from My Tasks dashlet
        StartWorkFlowPage startWorkFlowPage = myTasksDashlet.selectStartWorkFlow().render();

        Assert.assertTrue(startWorkFlowPage.getTitle().contains(START_WORKFLOW));

        Assert.assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.CLOUD_TASK_OR_REVIEW));

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        // Verify "Simple Cloud Task" is selected
        Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent());
    }
    
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15192() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
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

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
 
        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName1, SITE_VISIBILITY_PUBLIC);
        
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1, cloudUser2, cloudSiteName1, UserRole.COLLABORATOR);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        signInToAlfrescoInTheCloud(drone, cloudUser2, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);

        // TODO: Naved: remove render for all utils which return a specific rendered page object 
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        ShareUser.logout(drone);
    }

    @Test(groups="Hybrid")
    public void alf_15192() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName + "OP");
        String fileName = getFileName(testName) + ".doc";

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Open Document library, Upload a file
        DocumentLibraryPage docLibPage = SiteUtil.openSiteDocumentLibraryURL(drone, AbstractUtils.getSiteShortname(opSiteName));

        // Sselect StartWorkflow
        StartWorkFlowPage startWorkFlowPage = docLibPage.getFileDirectoryInfo(fileName).selectStartWorkFlow().render();

        Assert.assertTrue(startWorkFlowPage.getTitle().contains(START_WORKFLOW));

        Assert.assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.CLOUD_TASK_OR_REVIEW));

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();

        // Verify "Simple Cloud Task" is selected
        Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent());

        DestinationAndAssigneePage destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isNetworkDisplayed(DOMAIN_HYBRID));
        Assert.assertTrue(destinationPage.isNetworkDisplayed(invitedDomain1));
    }

    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15193() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
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

    @Test(groups="Hybrid")
    public void alf_15193() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
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

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName1));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName2));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName3));

        // Selection clouduser1 site and sync successfull.
        selectDestinationAndSync(destinationPage, invitedDomain1, cloudSiteName1, DEFAULT_FOLDER_NAME);

        AssignmentPage assigneePage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assigneePage.selectAssignee(cloudUser1);
        cloudTaskOrReviewPage.selectStartWorkflow().render();

        docLibPage = docLibPage.render();

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow());


        // Select StartWorkflow for cloud user2 (user1 is Collaborator) file
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName2);

        destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName1));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName2));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName3));

        // Selection clouduser1 site and sync successfull.
        selectDestinationAndSync(destinationPage, invitedDomain1, cloudSiteName2, DEFAULT_FOLDER_NAME);
        
        assigneePage = cloudTaskOrReviewPage.selectAssignmentPage().render();
        assigneePage.selectAssignee(cloudUser1);

        cloudTaskOrReviewPage.selectStartWorkflow().render();
        docLibPage = docLibPage.render();

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow());

        // Select StartWorkflow for cloud user3 (user1 is Consumer) file
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName3);
        destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName1));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName2));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName3));

        // Selection clouduser1 site and sync unsuccessful.
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

    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15194() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudUser2 = getUserNameForDomain(testName + "user2", invitedDomain1);
        String cloudUser3 = getUserNameForDomain(testName + "user3", invitedDomain1);
        String cloudUser4 = getUserNameForDomain(testName + "user4", invitedDomain1);
        cloudCollaboratorSite = getSiteName(testName + "Collaborator");
        cloudContributorSite = getSiteName(testName + "Contributor");
        cloudConsumerSite = getSiteName(testName + "Consumer");

        String folder1 = getFolderName(testName + "Collaborator");
        String folder2 = getFolderName(testName + "Contributor");
        String folder3 = getFolderName(testName + "Consumer");

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

            // Creating 3 folders with Collobarator,Contrinbutor and Consumer
            // roles for each folder.
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
     * Note: This test case includes the alf_15194/15195/15196
     */
    @Test(groups="Hybrid")
    public void alf_15194() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        cloudCollaboratorSite = getSiteName(testName + "Collaborator");
        cloudContributorSite = getSiteName(testName + "Contributor");
        cloudConsumerSite = getSiteName(testName + "Consumer");
        String opSiteName1 = getSiteName(testName + System.currentTimeMillis() + "op1");
        String opSiteName2 = getSiteName(testName + System.currentTimeMillis() + "op2");
        String opSiteName3 = getSiteName(testName + System.currentTimeMillis() + "op3");
        String folder1 = getFolderName(testName + "Collaborator");
        String folder2 = getFolderName(testName + "Contributor");
        String folder3 = getFolderName(testName + "Consumer");

        String fileName1 = getFileName(testName) + System.currentTimeMillis() + "1.doc";
        String fileName2 = getFileName(testName) + System.currentTimeMillis() + "2.doc";
        String fileName3 = getFileName(testName) + System.currentTimeMillis() + "3.doc";
        String fileName4 = getFileName(testName) + System.currentTimeMillis() + "4.doc";
        String fileName5 = getFileName(testName) + System.currentTimeMillis() + "5.doc";
        String fileName6 = getFileName(testName) + System.currentTimeMillis() + "6.doc";
        String fileName7 = getFileName(testName) + System.currentTimeMillis() + "7.doc";
        String fileName8 = getFileName(testName) + System.currentTimeMillis() + "8.doc";
        String fileName9 = getFileName(testName) + System.currentTimeMillis() + "9.doc";
        String[] fileInfo1 = { fileName1, DOCLIB };
        String[] fileInfo2 = { fileName2, DOCLIB };
        String[] fileInfo3 = { fileName3, DOCLIB };
        String[] fileInfo4 = { fileName4, DOCLIB };
        String[] fileInfo5 = { fileName5, DOCLIB };
        String[] fileInfo6 = { fileName6, DOCLIB };
        String[] fileInfo7 = { fileName7, DOCLIB };
        String[] fileInfo8 = { fileName8, DOCLIB };
        String[] fileInfo9 = { fileName9, DOCLIB };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName1, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo1).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo2).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo3).render();

        // Select StartWorkflow for cloud user1 on collaborator site for folder1
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder1, fileName1, cloudCollaboratorSite, docLibPage);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow());

        // Select StartWorkflow for cloud user1 on contributor site for folder1
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder1, fileName2, cloudContributorSite, docLibPage);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow());

        // Select StartWorkflow for cloud user1 on contributor site for folder1
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder1, fileName3, cloudConsumerSite, docLibPage);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName3).isPartOfWorkflow());

        // Creating 2nd site to have sync with contributor site.
        ShareUser.createSite(drone, opSiteName2, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo4).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo5).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo6).render();

        // Select StartWorkflow for cloud user1 on contributor site for folder2
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder2, fileName4, cloudCollaboratorSite, docLibPage);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName4).isPartOfWorkflow());

        // Select StartWorkflow for cloud user1 on contributor site for folder2
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder2, fileName5, cloudContributorSite, docLibPage);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName5).isPartOfWorkflow());

        // Select StartWorkflow for cloud user1 on contributor site for folder2
        docLibPage = startWorFlowOnContentWithRole(cloudUser1, folder2, fileName6, cloudConsumerSite, docLibPage);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName6).isPartOfWorkflow());

        // Creating 3rd site to have sync with consumer site.
        ShareUser.createSite(drone, opSiteName3, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo7).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo8).render();
        docLibPage = ShareUser.uploadFileInFolder(drone, fileInfo9).render();

        // Select StartWorkflow for cloud user1 on contributor site for folder3
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName7);

        DestinationAndAssigneePage destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudCollaboratorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudContributorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudConsumerSite));

        // Selection clouduser1 site and sync successfull.
        destinationPage.selectNetwork(invitedDomain1);
        destinationPage.selectSite(cloudCollaboratorSite);
        try
        {
            destinationPage.selectFolder(folder3);
        }
        catch (PageOperationException e)
        {
            Assert.assertEquals(e.getMessage(), "Sync Folder is disabled");
        }

        docLibPage = SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName3));

        // Select StartWorkflow for cloud user1 on contributor site for folder3
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName8);

        destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudCollaboratorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudContributorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudConsumerSite));

        // Selection clouduser1 site and sync successfull.
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

        docLibPage = SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName3));

        // Select StartWorkflow for cloud user1 on contributor site for folder3
        cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName9);

        destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudCollaboratorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudContributorSite));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudConsumerSite));

        // Selection clouduser1 site and sync successfull.
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
     * ALF-15197:Select Assignee/Reviewers - search for Share users
     * <ul>
     * <li>1) Create OP user</li>
     * <li>2) Create Cloud user</li>
     * <li>3) Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>4) Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15197() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudSiteName = getSiteName(testName  + "cloud");
        String cloudFileName = getFileName(testName + "cloud");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudFileInfo = new String[] { cloudFileName };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(hybridDrone, cloudFileInfo);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * ALF-15197:Select Assignee/Reviewers - search for Share users
     * <ul>
     * <li>1) Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>2) Any workflow type is chosen</li>
     * <li>3) All fields except assignee field, are filled with correct data</li>
     * <li>4) Select Assignee/Reviewers window is opened.</li>
     * <li>5) Perform search for the Cloud user</li>
     * <li>6) Verify the Cloud user is found.</li>
     * <li>7) Perform search for the OP user</li>
     * <li>8) Verify the Cloud user is not found.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15197() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName);
        String cloudSiteName = getSiteName(testName  + "cloud");
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName);
        DestinationAndAssigneePage destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        // Selection clouduser1 site and sync successfull.
        selectDestinationAndSync(destinationPage, invitedDomain1, cloudSiteName, DEFAULT_FOLDER_NAME);

        AssignmentPage assigneePage = cloudTaskOrReviewPage.selectAssignmentPage().render();

        Assert.assertTrue(assigneePage.isUserFound(cloudUser1));

        Assert.assertFalse(assigneePage.isUserFound(opUser1));
    }

    /**
     * ALF-15198:Select Assignee/Reviewers - search for users from different networks
     * <ul>
     * <li>Create OP user</li>
     * <li>Create Cloud 2 users with same network and 1 user from diff network (invited1,invited2)</li>
     * <li>Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * <li>invite user@invited2.com to user@invited1.com site as collaborator</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15198() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1Network1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudUser2Network1 = getUserNameForDomain(testName + "user2", invitedDomain1);
        String cloudUserNetwork2 = getUserNameForDomain(testName + "user1", invitedDomain2);
        String cloudSiteName = getSiteName(testName  + "cloud");
        String cloudFileName = getFileName(testName + "cloud");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1Network1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2Network1 };
        String[] cloudUserInfo3 = new String[] { cloudUserNetwork2 };
        String[] cloudFileInfo = new String[] { cloudFileName };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo3);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1Network1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(hybridDrone, cloudFileInfo);

        // Inviting user1 as contributor to the site.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1Network1, cloudUserNetwork2, cloudSiteName, UserRole.COLLABORATOR);
        ShareUser.logout(hybridDrone);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1Network1, DEFAULT_PASSWORD);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1Network1, cloudUser2Network1, cloudSiteName, UserRole.COLLABORATOR);
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1Network1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * ALF-15198: Select Assignee/Reviewers - search for users from different
     * networks
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>Any workflow type is chosen</li>
     * <li>All fields except assignee field, are filled with correct data</li>
     * <li>Perform search for the Cloud user from network1</li>
     * <li>Verify the Cloud user is found.</li>
     * <li>Perform search for the Cloud user from network1</li>
     * <li>Verify the Cloud user is found.</li>
     * <li>Perform search for the Cloud user from network1</li>
     * <li>Verify the Cloud user is found.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15198() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1Network1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudUser2Network1 = getUserNameForDomain(testName + "user2", invitedDomain1);
        String cloudUserNetwork2 = getUserNameForDomain(testName + "user1", invitedDomain2);
        String cloudSiteName = getSiteName(testName  + "cloud");
        String opSiteName = getSiteName(testName + System.currentTimeMillis());
        String opFileName = getFileName(testName + System.currentTimeMillis());
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName);
        DestinationAndAssigneePage destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        // Selection clouduser1 site and sync successfull.
        selectDestinationAndSync(destinationPage, invitedDomain1, cloudSiteName, DEFAULT_FOLDER_NAME);

        AssignmentPage assigneePage = cloudTaskOrReviewPage.selectAssignmentPage().render();

        Assert.assertTrue(assigneePage.isUserFound(cloudUser1Network1));

        Assert.assertTrue(assigneePage.isUserFound(cloudUser2Network1));

        Assert.assertTrue(assigneePage.isUserFound(cloudUserNetwork2));
        
    }

    /**
     * ALF-15199:Select Assignee - select more than one user
     * <ul>
     * <li>Create OP user</li>
     * <li>Create Cloud 2 users with same network(invited1)</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15199() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudUser2 = getUserNameForDomain(testName + "user2", invitedDomain1);
        String cloudSiteName = getSiteName(testName + "cloud");
        String cloudFileName = getFileName(testName + "cloud");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };
        String[] cloudFileInfo = new String[] { cloudFileName };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create Users (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(hybridDrone, cloudFileInfo);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     *  ALF-15199:Select Assignee - select more than one user
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>Any workflow type is chosen</li>
     * <li>All fields except assignee field, are filled with correct data</li>
     * <li>Perform search for the Cloud user from network1</li>
     * <li>Verify the Cloud user is found.</li>
     * <li>Perform search for the Cloud user from network1</li>
     * <li>Verify the Cloud user is found.</li>
     * <li>Add cloud user1 as assignee and verify that add buttons are disabled for the remaining users in list</li>
     * <li>Remove the cloud user1 as assignee and Verify that Cloud user is removed from the added list.</li>
     * <li>Add cloud user2 as assignee and verify that add buttons are disabled for the remaining users in list</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups = "Hybrid")
    public void alf_15199() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudUser2 = getUserNameForDomain(testName + "user2", invitedDomain1);
        String cloudSiteName = getSiteName(testName + "cloud");
        String opSiteName = getSiteName(testName + System.currentTimeMillis());
        String opFileName = getFileName(testName + System.currentTimeMillis());
        String[] opFileInfo = new String[] { opFileName };
        boolean found;

        List<String> expectedUsers = new ArrayList<String>();
        expectedUsers.add(cloudUser1);
        expectedUsers.add(cloudUser2);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Select "Cloud Task or Review" from select a workflow dropdown
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName);

        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);

        DestinationAndAssigneePage destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        // Selection clouduser1 site and sync successfull.
        selectDestinationAndSync(destinationPage, invitedDomain1, cloudSiteName, DEFAULT_FOLDER_NAME);

        AssignmentPage assigneePage = cloudTaskOrReviewPage.selectAssignmentPage().render();

        List<String> actualUsers = assigneePage.getUserList("user" + testName + "user");

        // Verifying the two cloud users are listed in search list.
        for (String expectedUser : expectedUsers)
        {
            found = false;

            for (String actualUser : actualUsers)
            {
                if (actualUser.contains(expectedUser))
                {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        }

        // Select the user1 as assignee
        assigneePage.selectUser(cloudUser1);

        // verifying user1 is selected
        Assert.assertTrue(assigneePage.isUserSelected(cloudUser1));

        // Verifying the user2 doest have access to add button
        Assert.assertFalse(assigneePage.isAddIconPresent(cloudUser2));

        // Removing the selected user1
        assigneePage.removeUser(cloudUser1);

        // Verifying the user1 is still selected in added list
        Assert.assertFalse(assigneePage.isUserSelected(cloudUser1));

        // Adding the user2 as assignee
        assigneePage.selectUser(cloudUser2);

        // Verifying user2 is selected
        Assert.assertTrue(assigneePage.isUserSelected(cloudUser2));

        // Verifying the user1 doest have access to add button
        Assert.assertFalse(assigneePage.isAddIconPresent(cloudUser1));
    }

    /**
     * ALF-15199:Select Assignee - select more than one user
     * <ul>
     * <li>Create OP user</li>
     * <li>Create Cloud 5 users with same network(invited1)</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15200() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudUser2 = getUserNameForDomain(testName + "user2", invitedDomain1);
        String cloudUser3 = getUserNameForDomain(testName + "user3", invitedDomain1);
        String cloudUser4 = getUserNameForDomain(testName + "user4", invitedDomain1);
        String cloudUser5 = getUserNameForDomain(testName + "user5", invitedDomain1);
        String cloudSiteName = getSiteName(testName + "cloud");
        String cloudFileName = getFileName(testName + "cloud");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };
        String[] cloudUserInfo3 = new String[] { cloudUser3 };
        String[] cloudUserInfo4 = new String[] { cloudUser4 };
        String[] cloudUserInfo5 = new String[] { cloudUser5 };
        String[] cloudFileInfo = new String[] { cloudFileName };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create Users (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo3);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo4);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo5);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(hybridDrone, cloudFileInfo);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     *  ALF-15200:Select Reviewers - select more than one user
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>Any workflow type is chosen</li>
     * <li>All fields except assignee field, are filled with correct data</li>
     * <li>Perform search for the user</li>
     * <li>Verify the all 5 Cloud users are found.</li>
     * <li>Add cloud user1 as assignee and verify that user added successfully in list</li>
     * <li>Add cloud user2 as assignee and verify that user added successfully in list</li>
     * <li>Add cloud user3 as assignee and verify that user added successfully in list</li>
     * <li>Add cloud user4 as assignee and verify that user added successfully in list</li>
     * <li>Add cloud user5 as assignee and verify that user added successfully in list</li>
     * <li>Remove the all user2 and Verify that Cloud users are removed from the added list.</li>
     * <li>Add cloud user1 as reviewer and verify that user added successfully in list</li>
     * </ul>
     * @throws Exception
     */
    @Test(groups = "Hybrid")
    public void alf_15200() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudUser2 = getUserNameForDomain(testName + "user2", invitedDomain1);
        String cloudUser3 = getUserNameForDomain(testName + "user3", invitedDomain1);
        String cloudUser4 = getUserNameForDomain(testName + "user4", invitedDomain1);
        String cloudUser5 = getUserNameForDomain(testName + "user5", invitedDomain1);
        String cloudSiteName = getSiteName(testName + "cloud");
        String opSiteName = getSiteName(testName + System.currentTimeMillis());
        String opFileName = getFileName(testName + System.currentTimeMillis());
        String[] opFileInfo = new String[] { opFileName };
        boolean found;

        List<String> expectedUsers = new ArrayList<String>();
        expectedUsers.add(cloudUser1);
        expectedUsers.add(cloudUser2);
        expectedUsers.add(cloudUser3);
        expectedUsers.add(cloudUser4);
        expectedUsers.add(cloudUser5);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName);

        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);

        DestinationAndAssigneePage destinationPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        // Selection clouduser1 site and sync successfull.
        destinationPage.selectNetwork(invitedDomain1);
        destinationPage.selectSite(cloudSiteName);
        destinationPage.selectSubmitButtonToSync().render();

        AssignmentPage assigneePage = cloudTaskOrReviewPage.selectAssignmentPage().render();

        List<String> actualUsers = assigneePage.getUserList("user" + testName + "user");

        // Verifying the two cloud users are listed in search list.
        for (String expectedUser : expectedUsers)
        {
            found = false;

            for (String actualUser : actualUsers)
            {
                if (actualUser.contains(expectedUser))
                {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        }

        // Select the user1 as reviewer
        assigneePage.selectUser(cloudUser1);

        // verifying user1 is selected
        Assert.assertTrue(assigneePage.isUserSelected(cloudUser1));

        // Select the user2 as reviewer
        assigneePage.selectUser(cloudUser2);

        // verifying user1 is selected
        Assert.assertTrue(assigneePage.isUserSelected(cloudUser2));

        List<String> moreReviewers = new ArrayList<String>();
        moreReviewers.add(cloudUser3);
        moreReviewers.add(cloudUser4);
        moreReviewers.add(cloudUser5);

        // Adding 3 more reviewers
        assigneePage.selectUsers(moreReviewers);

        // Verifying the 3 more users addedd successfully
        Assert.assertTrue(assigneePage.isUserSelected(cloudUser3));
        Assert.assertTrue(assigneePage.isUserSelected(cloudUser4));
        Assert.assertTrue(assigneePage.isUserSelected(cloudUser5));

        // Removing all selected users.
        assigneePage.removeUsers(expectedUsers);

        // Verifying all users removed successfully
        Assert.assertFalse(assigneePage.isUserSelected(cloudUser1));
        Assert.assertFalse(assigneePage.isUserSelected(cloudUser2));
        Assert.assertFalse(assigneePage.isUserSelected(cloudUser3));
        Assert.assertFalse(assigneePage.isUserSelected(cloudUser4));
        Assert.assertFalse(assigneePage.isUserSelected(cloudUser5));

        // Select the user1 as reviewer
        assigneePage.selectUser(cloudUser1);

        // verifying user1 is selected
        Assert.assertTrue(assigneePage.isUserSelected(cloudUser1));
    }

    /**
     * ALF-15190:Simple Cloud Task - Select Destination - User is not authorized
     * <ul>
     * <li>Create OP user</li>
     * <li>Create Cloud user with network(invited1)</li>
     * <li>Login to OnPremise, Create a site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15190() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String cloudSiteName = getSiteName(testName + "cloud");
        String opSiteName = getSiteName(testName);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create Users (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login as User1 (cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OpUser)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);
    }

    @Test(groups="Hybrid")
    public void alf_15190() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String opSiteName = getSiteName(testName);
        String cloudSiteName = getSiteName(testName + "cloud");
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        //Verifying the cloud account is disconnected or not.
        AbstractCloudSyncTest.disconnectCloudSync(drone);

        // Create Site
        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName));

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select StartWorkflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);

        CloudSignInPage cloudSignPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        DestinationAndAssigneePage destinationPage = cloudSignPage.loginAs(cloudUser1, DEFAULT_PASSWORD).render();

        Assert.assertTrue(destinationPage.isNetworkDisplayed(invitedDomain1));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName));
    }

    /**
     * ALF-15191:Simple Cloud Task - Select Destination - User is not authorized
     * <ul>
     * <li>Create OP user</li>
     * <li>Create Cloud user with network(invited1)</li>
     * <li>Login to OnPremise, Create a site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15191() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String cloudSiteName = getSiteName(testName + "cloud");
        String opSiteName = getSiteName(testName);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create Users (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login as User1 (cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OpUser)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);
    }

    @Test(groups="Hybrid")
    public void alf_15191() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String opSiteName = getSiteName(testName);
        String cloudSiteName = getSiteName(testName + "cloud");
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".doc";
        String[] fileInfo = { fileName, DOCLIB };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        //Verifying the cloud account is disconnected or not.
        AbstractCloudSyncTest.disconnectCloudSync(drone);

        // Create Site
        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName));

        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Select StartWorkflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);

        CloudSignInPage cloudSignPage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

        DestinationAndAssigneePage destinationPage = cloudSignPage.loginAs(cloudUser1, DEFAULT_PASSWORD).render();

        Assert.assertTrue(destinationPage.isNetworkDisplayed(invitedDomain1));
        Assert.assertTrue(destinationPage.isSiteDisplayed(cloudSiteName));
    }

    /** ALF-15178:Due - value is empty
     * <ul>
     *     <li>1) Create OP user</li>
     *     <li>2) Create Cloud user</li>
     *     <li>3) Login to OP, set up Cloud Sync with Cloud user</li>
     *     <li>4) Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15178() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudSiteName = getSiteName(testName  + "cloud");
        String cloudFileName = getFileName(testName + "cloud");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudFileInfo = new String[] { cloudFileName };
        String opSiteName = getSiteName(testName);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(hybridDrone, cloudFileInfo);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);
    }

    /**
     * ALF-15178:Due - value is empty
     * <ul>
     * <li>1) Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>2) Any workflow type is chosen</li>
     * <li>3) All fields except Due are filled with correct data</li>
     * <li>4) Leave Due field empty, Create Workflow.</li>
     * <li>5) Verify The workflow is created successfully. A new task appeared
     * to the assignee.</li>
     * <li>6) OP Verify the workflow details.</li>
     * <li>7) Due:(None) is set.</li>
     * <li>8) Cloud Verify the workflow details.</li>
     * <li>9) Due:(None) is set.</li>
     * <li>10) Cloud Verify the received task details.</li>
     * <li>11) Due:(None) is set.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15178() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String opSiteName = getSiteName(testName);
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String cloudSiteName = getSiteName(testName  + "cloud");
        String[] opFileInfo = new String[] { opFileName };
        workFlowName = testName + System.currentTimeMillis();

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Open Document library, Upload a file
        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName));

        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        WorkFlowDetailsPage detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();
        Assert.assertEquals(detailsPage.getWorkFlowDetailsGeneralInfo().getDueDateString(),NONE);

        ShareUser.logout(drone);

        //Cloud user login
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        MyTasksPage myTasks= ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));
        TaskDetailsPage taskDetailsPage = myTasks.selectViewTasks(workFlowName).render();
        Assert.assertEquals(taskDetailsPage.getTaskDetailsInfo().getDueDateString(),NONE);

        ShareUser.logout(hybridDrone);
    }

    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15179() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudSiteName = getSiteName(testName  + "cloud");
        String cloudFileName = getFileName(testName + "cloud");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudFileInfo = new String[] { cloudFileName };
        String opSiteName = getSiteName(testName);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(hybridDrone, cloudFileInfo);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);
    }

    /**
     * ALF-15179:Due - to the same day
     * <ul>
     * <li>1) Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>2) Any workflow type is chosen</li>
     * <li>3) All fields except Due are filled with correct data</li>
     * <li>4) Set Due field to the same day date , Create Workflow.</li>
     * <li>5) Verify The workflow is created successfully. A new task appeared
     * to the assignee.</li>
     * <li>6) OP Verify the workflow details.</li>
     * <li>7) Due:is set to the same daydate.</li>
     * <li>8) Cloud Verify the workflow details.</li>
     * <li>9) Due:is set to the same day date.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15179() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String opSiteName = getSiteName(testName);
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String cloudSiteName = getSiteName(testName  + "cloud");
        String[] opFileInfo = new String[] { opFileName };
        DateTime dueDateTime;
        workFlowName = testName + System.currentTimeMillis();
        Calendar today = Calendar.getInstance();
        String strTodayDueDate = ShareUser.convertDateToString(today.getTime(), DDMMYYYY);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Open Document library, Upload a file
        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName));

        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        logger.info("workFlow name : " + workFlowName);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setDueDate(strTodayDueDate);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = (MyWorkFlowsPage) cloudTaskOrReviewPage.startWorkflow(formDetails);
        myWorkFlowsPage.render();

        WorkFlowDetailsPage detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();
        dueDateTime = detailsPage.getWorkFlowDetailsGeneralInfo().getDueDate();
        String expectedDueDate = ShareUser.convertDateToString(dueDateTime.toDate(), DDMMYYYY);
        Assert.assertEquals(expectedDueDate, strTodayDueDate);

        ShareUser.logout(drone);

        //Cloud user login
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        MyTasksPage myTasks= ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        dueDateTime = myTasks.selectViewTasks(workFlowName).render().getTaskDetailsInfo().getDueDate();
        expectedDueDate = ShareUser.convertDateToString(dueDateTime.toDate(), DDMMYYYY);

        Assert.assertEquals(expectedDueDate, strTodayDueDate);

        ShareUser.logout(hybridDrone);
    }
    
    /**
     * ALF-15180:Due - to the day backward
     * <ul>
     * <li>Create OP user</li>
     * <li>Create Cloud user</li>
     * <li>Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15180() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudSiteName = getSiteName(testName  + "cloud");
        String cloudFileName = getFileName(testName + "cloud");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudFileInfo = new String[] { cloudFileName };
        String opSiteName = getSiteName(testName);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(hybridDrone, cloudFileInfo);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);
    }

    /**
     * ALF-15180:Due - to the day backward
     * <ul>
     * <li>1) Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>2) Any workflow type is chosen</li>
     * <li>3) All fields except Due are filled with correct data</li>
     * <li>4) Set Due field to the day backward date , Create Workflow.</li>
     * <li>5) Verify The workflow is created successfully. A new task appeared
     * to the assignee.</li>
     * <li>6) OP Verify the workflow details.</li>
     * <li>7) Due:is set to the day backward date.</li>
     * <li>8) Cloud Verify the workflow details.</li>
     * <li>9) Due:is set to the day backward date.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15180() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String opSiteName = getSiteName(testName);
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String cloudSiteName = getSiteName(testName  + "cloud");
        String[] opFileInfo = new String[] { opFileName };

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);
        yesterday.set(Calendar.MILLISECOND, 0);

        String strYesterdayDueDate = ShareUser.convertDateToString(yesterday.getTime(), DDMMYYYY);

        DateTime dueDateTime;
        workFlowName = testName + System.currentTimeMillis();

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Open Document library, Upload a file
        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName));

        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setDueDate(strYesterdayDueDate);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        WorkFlowDetailsPage detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();
        dueDateTime = detailsPage.getWorkFlowDetailsGeneralInfo().getDueDate();

        String expectedDueDate = ShareUser.convertDateToString(dueDateTime.toDate(), DDMMYYYY);
        Assert.assertEquals(expectedDueDate, strYesterdayDueDate);

        ShareUser.logout(drone);

        //Cloud user login
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        MyTasksPage myTasks= ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));
        dueDateTime = myTasks.selectViewTasks(workFlowName).render().getTaskDetailsInfo().getDueDate();
        expectedDueDate = ShareUser.convertDateToString(dueDateTime.toDate(), DDMMYYYY);
        Assert.assertEquals(expectedDueDate, strYesterdayDueDate);
        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-15181:Due - to the day ahead.
     * <ul>
     * <li>Create OP user</li>
     * <li>Create Cloud user</li>
     * <li>Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15181() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudSiteName = getSiteName(testName  + "cloud");
        String cloudFileName = getFileName(testName + "cloud");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudFileInfo = new String[] { cloudFileName };
        String opSiteName = getSiteName(testName);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(hybridDrone, cloudFileInfo);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);
    }

    /**
     * ALF-15181:Due - to the day ahead
     * <ul>
     * <li>1) Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>2) Any workflow type is chosen</li>
     * <li>3) All fields except Due are filled with correct data</li>
     * <li>4) Set Due field to the day ahead date , Create Workflow.</li>
     * <li>5) Verify The workflow is created successfully. A new task appeared
     * to the assignee.</li>
     * <li>6) OP Verify the workflow details.</li>
     * <li>7) Due:is set to the day ahead date.</li>
     * <li>8) Cloud Verify the workflow details.</li>
     * <li>9) Due:is set to the day ahead date.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15181() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String opSiteName = getSiteName(testName);
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String cloudSiteName = getSiteName(testName  + "cloud");
        String[] opFileInfo = new String[] { opFileName };
        DateTime dueDateTime;
        workFlowName = testName + System.currentTimeMillis();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, +1);
        String strTomorrowDueDate = ShareUser.convertDateToString(tomorrow.getTime(), DDMMYYYY);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Open Document library, Upload a file
        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName));

        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        logger.info("workFlow name : " + workFlowName);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setDueDate(strTomorrowDueDate);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = (MyWorkFlowsPage) cloudTaskOrReviewPage.startWorkflow(formDetails);
        myWorkFlowsPage.render();

        WorkFlowDetailsPage detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();
        dueDateTime = detailsPage.getWorkFlowDetailsGeneralInfo().getDueDate();
        String expectedDueDate = ShareUser.convertDateToString(dueDateTime.toDate(), DDMMYYYY);
        Assert.assertEquals(expectedDueDate, strTomorrowDueDate);

        ShareUser.logout(drone);

        //Cloud user login
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        MyTasksPage myTasks= ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));

        dueDateTime = myTasks.selectViewTasks(workFlowName).render().getTaskDetailsInfo().getDueDate();
        expectedDueDate = ShareUser.convertDateToString(dueDateTime.toDate(), DDMMYYYY);

        Assert.assertEquals(expectedDueDate, strTomorrowDueDate);

        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-15185:Due - Negative case.
     * <ul>
     * <li>Create OP user</li>
     * <li>Create Cloud user</li>
     * <li>Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15185() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
    }
  
    /**
     * ALF-15185:Due - Negative case.
     * <ul>
     * <li>1) Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>2) CloudTaskOrReview workflow type is chosen</li>
     * <li>3) All fields except Due are filled with correct data</li>
     * <li>4) Enter illegal data into the Due field.</li>
     * <li>5) Verify Field contains an error' message is displayed.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15185() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Start Simple Cloud Task workflow
        StartWorkFlowPage startWorkFlowPage = ShareUserWorkFlow.selectStartWorkFlowFromMyWorkFlowsPage(drone);
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);

        cloudTaskOrReviewPage.enterDueDateText("text");
        Assert.assertTrue(cloudTaskOrReviewPage.isErrorBalloonPresent());
        Assert.assertEquals(cloudTaskOrReviewPage.getErrorBalloonMessage(), "Field contains an error.");

        cloudTaskOrReviewPage.enterDueDateText("!@#$%^&*()_+|?.,<>:;''=-{}[]");
        Assert.assertTrue(cloudTaskOrReviewPage.isErrorBalloonPresent());
        Assert.assertEquals(cloudTaskOrReviewPage.getErrorBalloonMessage(), "Field contains an error.");

        cloudTaskOrReviewPage.enterDueDateText("10/15/2013");
        Assert.assertTrue(cloudTaskOrReviewPage.isErrorBalloonPresent());
        Assert.assertEquals(cloudTaskOrReviewPage.getErrorBalloonMessage(), "Field contains an error.");

        cloudTaskOrReviewPage.enterDueDateText("32/10/2013");
        Assert.assertTrue(cloudTaskOrReviewPage.isErrorBalloonPresent());
        Assert.assertEquals(cloudTaskOrReviewPage.getErrorBalloonMessage(), "Field contains an error.");

        cloudTaskOrReviewPage.enterDueDateText("     ");
        Assert.assertTrue(cloudTaskOrReviewPage.isErrorBalloonPresent());
        Assert.assertEquals(cloudTaskOrReviewPage.getErrorBalloonMessage(), "Field contains an error.");
    }

    /**
     * ALF-15182 includes ALF-15182\ALF-15183\ALF-15184   
     * ALF-15182:Priority - High\Medium\Low.
     * <ul>
     * <li>Create OP user</li>
     * <li>Create Cloud user</li>
     * <li>Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15182() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudSiteName = getSiteName(testName + "cloud");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * ALF-15182 includes ALF-15182\ALF-15183\ALF-15184
     * ALF-15182:Priority - High\Medium\Low.
     * <ul>
     * <li>1) Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>2) Any workflow type is chosen</li>
     * <li>3) All fields are filled with correct data and Priority is High</li>
     * <li>4) Start work flow</li>
     * <li>5) Verify the work flow details and make sure the priority is high.</li>
     * <li>6) Start the another workflow, Any workflow type is chosen</li>
     * <li>7) All fields are filled with correct data and Priority is Medium</li>
     * <li>8) Start work flow</li>
     * <li>9) Verify the work flow details and make sure the priority is Medium.</li>
     * <li>10) Start the another workflow, Any workflow type is chosen</li>
     * <li>11) All fields are filled with correct data and Priority is Medium</li>
     * <li>12) Start work flow</li>
     * <li>13) Verify the work flow details and make sure the priority is Medium.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15182() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName + "user1", invitedDomain1);
        String cloudSiteName = getSiteName(testName + "cloud");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName1 = getFileName(testName) + System.currentTimeMillis() + "high";
        String opFileName2 = getFileName(testName) + System.currentTimeMillis() + "medium";
        String opFileName3 = getFileName(testName) + System.currentTimeMillis() + "low";
        String[] opFileInfo1 = new String[] { opFileName1 };
        String[] opFileInfo2 = new String[] { opFileName2 };
        String[] opFileInfo3 = new String[] { opFileName3 };
        String workFlowName1 = testName + System.currentTimeMillis() + "1";
        String workFlowName2 = testName + System.currentTimeMillis() + "2";
        String workFlowName3 = testName + System.currentTimeMillis() + "3";

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(opSiteName));

        ShareUser.uploadFileInFolder(drone, opFileInfo1).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo2).render();
        ShareUser.uploadFileInFolder(drone, opFileInfo3).render();

        // Start Simple Cloud Task workflow with High Priority
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setTaskPriority(Priority.HIGH);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName1, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        WorkFlowDetailsPage detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName1).render();
        Assert.assertEquals(detailsPage.getWorkFlowDetailsGeneralInfo().getPriority(),Priority.HIGH);

        // Start Simple Cloud Task workflow with Medium Priority
        cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName2);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName2, opSiteName);

        // Fill the form details and start workflow
        myWorkFlowsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName2).render();

        Assert.assertEquals(detailsPage.getWorkFlowDetailsGeneralInfo().getPriority(),Priority.MEDIUM);

        // Start Simple Cloud Task workflow with Low Priority
        cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName3);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setTaskPriority(Priority.LOW);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName3, opSiteName);

        // Fill the form details and start workflow
        myWorkFlowsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName3).render();

        Assert.assertEquals(detailsPage.getWorkFlowDetailsGeneralInfo().getPriority(),Priority.LOW);

        //Cloud user login
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        MyTasksPage myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        Assert.assertEquals(myTasks.selectViewTasks(workFlowName1).render().getTaskDetailsInfo().getPriority(), Priority.HIGH);

        myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName2));
        Assert.assertEquals(myTasks.selectViewTasks(workFlowName2).render().getTaskDetailsInfo().getPriority(), Priority.MEDIUM);

        myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName3));
        Assert.assertEquals(myTasks.selectViewTasks(workFlowName3).render().getTaskDetailsInfo().getPriority(), Priority.LOW);
    }

    /**
     * ALF-9559 50 - Approved by the first user
     * <ul>
     * <li>Create OP user</li>
     * <li>Create 2 Cloud users</li>
     * <li>Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_9559() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "rev1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "rev2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String cloudSite2Name = getSiteName(testName + "cl2");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        // Inviting user2 as contributor to the site.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1, cloudUser2, cloudSite1Name, UserRole.CONTRIBUTOR);

        ShareUser.logout(hybridDrone);

        // Login as User2 (Cloud)
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite2Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * ALF-9559:50 - Approved by the first user.
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields except approval percentage filled with correct data</li>
     * <li>Choose 2 cloud users as reviewers.</li>
     * <li>Enter 50% into the approval percentage field.</li>
     * <li>Start the work flow.</li>
     * <li>Verify the workflow is created successfully.</li>
     * <li>Verify the task is created successfully and available for review to 2
     * cloud users.</li>
     * <li>First cloud user approves the workflow task</li>
     * <li>Verify the workflow task is disappeared for both cloud users</li>
     * <li>OP-User : Verify the MyTasks list for A new task is received. The
     * following details are present: Title: 'Details: Cloud Review Task test
     * message (Document was approved on the cloud)' Response Required approval
     * percentage: 50 Actual approval percentage: 50 Comments: user1 user1: test
     * comment1 (Approved) .</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_9559() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "rev1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "rev2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();
        cloudComment = "test comment1 : " + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(50);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        ShareUser.logout(drone);

        // Cloud user2 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUser.logout(hybridDrone);

        // Cloud user1 login and verifies the task is present and approves the task.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUserWorkFlow.approveOrRejectTask(hybridDrone, cloudUser1, workFlowName1, cloudComment, EditTaskAction.APPROVE);

        // Verify task is not displayed in Active Tasks list
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Cloud user2 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));
        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName1).render();

        Assert.assertTrue(taskDetailsPage.getTaskDetailsHeader().contains(workFlowName1 + " (Document was approved on the cloud)"));
        Assert.assertTrue(taskDetailsPage.getRequiredApprovalPercentage() == 50);
        Assert.assertTrue(taskDetailsPage.getActualApprovalPercentage() == 50);
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser1 + " LName: " + cloudComment));
        Assert.assertTrue(taskDetailsPage.getComment().contains("(Approved)"));
    }

    /**
     * ALF-9594:50 - Rejected by the first user
     * <ul>
     * <li>Create OP user</li>
     * <li>Create 2 Cloud users</li>
     * <li>Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_9594() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        // Inviting user2 as contributor to the site.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1, cloudUser2, cloudSite1Name, UserRole.CONTRIBUTOR);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * ALF-9594:50 - Rejected by the first user.
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields except approval percentage filled with correct data</li>
     * <li>Choose 2 cloud users as reviewers.</li>
     * <li>Enter 50% into the approval percentage field.</li>
     * <li>Start the work flow.</li>
     * <li>Verify the workflow is created successfully.</li>
     * <li>Verify the task is created successfully and available for review to 2
     * cloud users.</li>
     * <li>First cloud user rejects the workflow task</li>
     * <li>Second cloud user approves the workflow task</li>
     * <li>Verify the workflow task is disappeared for both cloud users</li>
     * <li>OP-User : Verify the MyTasks list for A new task is received. The
     * following details are present: Title: 'Details: Cloud Review Task test
     * message (Document was approved on the cloud)' 
     * Response Required approval percentage: 50 Actual approval percentage: 50 
     * Comments: user1 user1: test comment1 (Rejected) user2 user2: test comment2 (Approved)</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_9594() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();
        String cloudComment1 = "test approved : " + System.currentTimeMillis();
        String cloudComment2 = "test rejected : " + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(50);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        ShareUser.logout(drone);

        // Cloud user1 login and verifies the task is present and rejects the task.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUserWorkFlow.approveOrRejectTask(hybridDrone, cloudUser1, workFlowName1, cloudComment2, EditTaskAction.REJECT);

        // Verify task is not displayed in Active Tasks list
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Cloud user2 login and verifies the task is present and approves the task.
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUserWorkFlow.approveOrRejectTask(hybridDrone, cloudUser1, workFlowName1, cloudComment1, EditTaskAction.APPROVE);

        // Verify task is not displayed in Active Tasks list
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));
        TaskDetailsPage taskDetailsPage =myTasksPage.selectViewTasks(workFlowName1).render();

        Assert.assertTrue(taskDetailsPage.getTaskDetailsHeader().contains(workFlowName1 + " (Document was approved on the cloud)"));
        Assert.assertTrue(taskDetailsPage.getRequiredApprovalPercentage() == 50);
        Assert.assertTrue(taskDetailsPage.getActualApprovalPercentage() == 50);
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser1 + " LName: " + cloudComment2));
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser2 + " LName: " + cloudComment1));
    }

    /**
     * ALF-15170:100 - Rejected by the first user
     * <ul>
     * <li>Create OP user</li>
     * <li>Create 2 Cloud users</li>
     * <li>Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15170() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        // Inviting user2 as contributor to the site.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1, cloudUser2, cloudSite1Name, UserRole.CONTRIBUTOR);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * ALF-15170:100 - Rejected by the first user.
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields except approval percentage filled with correct data</li>
     * <li>Choose 2 cloud users as reviewers.</li>
     * <li>Enter 100% into the approval percentage field.</li>
     * <li>Start the work flow.</li>
     * <li>Verify the workflow is created successfully.</li>
     * <li>Verify the task is created successfully and available for review to 2
     * cloud users.</li>
     * <li>First cloud user rejects the workflow task</li>
     * <li>Verify the workflow task is disappeared for both cloud users</li>
     * <li>OP-User : Verify the MyTasks list for A new task is received. The
     * following details are present: Title: 'Details: Cloud Review Task test
     * message (Document was rejected on the cloud)' 
     * Response Required approval percentage: 100 Actual approval percentage: 0 
     * Comments: user1 user1: test comment1 (Rejected)</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15170() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();
        cloudComment = "test comment1 Rejected : " + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(100);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        ShareUser.logout(drone);

        // Cloud user2 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUser.logout(hybridDrone);

        // Cloud user1 login and verifies the task is present and rejects the task.
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUserWorkFlow.approveOrRejectTask(hybridDrone, cloudUser1, workFlowName1, cloudComment, EditTaskAction.REJECT);

        // Verify task is not displayed in Active Tasks list
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Cloud user2 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));
        TaskDetailsPage taskDetailsPage =myTasksPage.selectViewTasks(workFlowName1).render();

        Assert.assertTrue(taskDetailsPage.getTaskDetailsHeader().contains(workFlowName1 + " (Document was rejected on the cloud)"));
        Assert.assertTrue(taskDetailsPage.getRequiredApprovalPercentage() == 100);
        Assert.assertTrue(taskDetailsPage.getActualApprovalPercentage() == 0);
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser1 + " LName: " + cloudComment));
        Assert.assertTrue(taskDetailsPage.getComment().contains("(Rejected)"));
    }

    /**
     * ALF-15171:Task Details - Required Approval Percentage after competion (less than 50).
     * <ul>
     * <li>Create OP user</li>
     * <li>Create 2 Cloud users</li>
     * <li>Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15171() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        // Inviting user2 as contributor to the site.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1, cloudUser2, cloudSite1Name, UserRole.CONTRIBUTOR);

        ShareUser.logout(hybridDrone);
       
        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * ALF-15171:Task Details - Required Approval Percentage after competion (less than 50).
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields except approval percentage filled with correct data</li>
     * <li>Choose 2 cloud users as reviewers.</li>
     * <li>Enter 34% into the approval percentage field.</li>
     * <li>Start the work flow.</li>
     * <li>Verify the workflow is created successfully.</li>
     * <li>Verify the task is created successfully and available for review to 2
     * cloud users.</li>
     * <li>First cloud user approves the workflow task</li>
     * <li>Verify the workflow task is disappeared for both cloud users</li>
     * <li>OP-User : Verify the MyTasks list for A new task is received. The
     * following details are present: Title: 'Details: Cloud Review Task test
     * message (Document was approved on the cloud)' 
     * Response Required approval percentage: 34 Actual approval percentage: 50 
     * Comments: user1 user1: test comment1 (Approved)</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15171() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();
        cloudComment = "test approved : " + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(34);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        ShareUser.logout(drone);

        // Cloud user2 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUser.logout(hybridDrone);

        // Cloud user1 login and verifies the task is present and approves the task
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUserWorkFlow.approveOrRejectTask(hybridDrone, cloudUser1, workFlowName1, cloudComment, EditTaskAction.APPROVE);

        // Verify task is not displayed in Active Tasks list
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Cloud user2 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));
        TaskDetailsPage taskDetailsPage =myTasksPage.selectViewTasks(workFlowName1).render();

        Assert.assertTrue(taskDetailsPage.getTaskDetailsHeader().contains(workFlowName1 + " (Document was approved on the cloud)"));
        Assert.assertTrue(taskDetailsPage.getRequiredApprovalPercentage() == 34 );
        Assert.assertTrue(taskDetailsPage.getActualApprovalPercentage() == 50 );
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser1 + " LName: " + cloudComment));
        Assert.assertTrue(taskDetailsPage.getComment().contains("(Approved)"));
    }

    /**
     * ALF-15172 50 - Approved by the first user
     * <ul>
     * <li>Create OP user</li>
     * <li>Create 2 Cloud users</li>
     * <li>Login to OP, set up Cloud Sync with Cloud user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15172() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        // Inviting user2 as contributor to the site.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1, cloudUser2, cloudSite1Name, UserRole.CONTRIBUTOR);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

    }

    /**
     * ALF-15171:Task Details - Required Approval Percentage after competion (less than 50).
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields except approval percentage filled with correct data</li>
     * <li>Choose 2 cloud users as reviewers.</li>
     * <li>Enter 67% into the approval percentage field.</li>
     * <li>Start the work flow.</li>
     * <li>Verify the workflow is created successfully.</li>
     * <li>Verify the task is created successfully and available for review to 2
     * cloud users.</li>
     * <li>First cloud user approves the workflow task</li>
     * <li>Second cloud user rejects the workflow task</li>
     * <li>Verify the workflow task is disappeared for both cloud users</li>
     * <li>OP-User : Verify the MyTasks list for A new task is received. The
     * following details are present: Title: 'Details: Cloud Review Task test
     * message (Document was approved on the cloud)' 
     * Response Required approval percentage: 67 Actual approval percentage: 50 
     * Comments: user1 user1: test comment1 (Approved) user2 user2: test comment2 (Rejected)</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15172() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();
        String cloudComment1 = "test approved : " + System.currentTimeMillis();
        String cloudComment2 = "test rejected : " + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setApprovalPercentage(67);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        ShareUser.logout(drone);

        // Cloud user2 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUser.logout(hybridDrone);

        // Cloud user1 login and verifies the task is present and approves the task
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUserWorkFlow.approveOrRejectTask(hybridDrone, cloudUser1, workFlowName1, cloudComment1, EditTaskAction.APPROVE);

        // Verify task is not displayed in Active Tasks list
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Cloud user2 login and verifies the task is present and rejects the task
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUserWorkFlow.approveOrRejectTask(hybridDrone, cloudUser1, workFlowName1, cloudComment2, EditTaskAction.REJECT);

        // Verify task is not displayed in Active Tasks list
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));
        TaskDetailsPage taskDetailsPage =myTasksPage.selectViewTasks(workFlowName1).render();

        Assert.assertTrue(taskDetailsPage.getTaskDetailsHeader().contains(workFlowName1 + " (Document was rejected on the cloud)"));
        Assert.assertTrue(taskDetailsPage.getRequiredApprovalPercentage() == 67);
        Assert.assertTrue(taskDetailsPage.getActualApprovalPercentage() == 50);
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser1 + " LName: " + cloudComment1));
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser2 + " LName: " + cloudComment2));
    }

    /**
     * ALF-9576:Reviewer/Assignee has no write permissions to the folder.
     * <ul>
     * <li>Create OP user</li>
     * <li>Create 2 Cloud users</li>
     * <li>Invite cloud user2 onto user1 site as consumer</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_9576() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudUserInfo2 = new String[] { cloudUser2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Create User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo2);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        // Inviting user2 as contributor to the site.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1, cloudUser2, cloudSite1Name, UserRole.CONSUMER);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * ALF-9576:Reviewer/Assignee has no write permissions to the folder.
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields filled with correct data</li>
     * <li>Choose 2 cloud users as reviewers.</li>
     * <li>Start the work flow.</li>
     * <li>Verify Workflow cannot be created. A friendly message, that user2 has
     * no write permissions to the specified destination, is thrown.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_9576() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setApprovalPercentage(50);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        ShareUser.logout(drone);

        // Cloud user2 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite1Name);
        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(hybridDrone, opFileName);

        // To verify the cloud user is not having the edit options on sync document as he is the consumer on this content.
        Assert.assertFalse(detailsPage.isEditOfflineLinkDisplayed());
        Assert.assertFalse(detailsPage.isUploadNewVersionDisplayed());

        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-9627:Cloud Free Network - Start Workflow.
     * <ul>
     * <li>Create OP user</li>
     * <li>Create 1 Cloud freenet user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_9627() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, DOMAIN_FREE).replace("userenterprise42", "reviewer1cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * ALF-9627:Cloud Free Network - Start Workflow.
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields filled with correct data</li>
     * <li>Choose cloud user as reviewers.</li>
     * <li>Start the work flow.</li>
     * <li>Verify Workflow cannot be created. A friendly behavior should occur.
     * Hybrid Workflow functionality is not avaiable for free accounts.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_9627() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();

        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        try
        {
            cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
        }
        catch(PageException e)
        {
            Assert.assertTrue(e.getMessage().equalsIgnoreCase("No network is enabled for sync"));
        }

        ShareUser.logout(drone);
    }

    /**
     * ALF-9628:Cloud Trial Standard Network - Start Workflow.
     * <ul>
     * <li>Create OP user</li>
     * <li>Create 1 Cloud trialnet user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_9628() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, trialDomain1).replace("userenterprise42", "cloud2user");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, trialDomain1, "1001");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * ALF-9628:Cloud Trial Standard Network - Start Workflow.
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields filled with correct data</li>
     * <li>Choose cloud user as reviewers.</li>
     * <li>Start the work flow.</li>
     * <li>Verify Workflow is created successfully.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_9628() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, trialDomain1).replace("userenterprise42", "cloud2user");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(50);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertNotNull(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));

        ShareUser.logout(drone);
    }


    /**
     * ALF-9630:Partner Cloud account - Start Workflow.
     * <ul>
     * <li>Create OP user</li>
     * <li>Create 1 Cloud partner network user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_9630() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, partnerDomain1).replace("userenterprise42", "cloud2user");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, partnerDomain1, "101");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

    }

    /**
     * ALF-9630:Partner Cloud account - Start Workflow.
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields filled with correct data</li>
     * <li>Choose cloud user as reviewers.</li>
     * <li>Start the work flow.</li>
     * <li>Verify Workflow is created successfully.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_9630() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, partnerDomain1).replace("userenterprise42", "cloud2user");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(50);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertNotNull(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));

        ShareUser.logout(drone);
    }

    /**
     * ALF-15243:Check workflow availability for non-initiator.
     * <ul>
     * <li>Create OP user</li>
     * <li>Create 3 Cloud users</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_15243() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudUser3 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer3cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
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
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        // Inviting user2 as contributor to the site.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, cloudUser1, cloudUser2, cloudSite1Name, UserRole.COLLABORATOR);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    /**
     * ALF-15243 - Combination of ALF-15243/ALF-15244/ALF-15245
     * ALF-15243:Check workflow availability for non-initiator.
     * <ul>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields filled with correct data</li>
     * <li>Choose 2 cloud users as reviewers.</li>
     * <li>Start the work flow.</li>
     * <li>Verify that cloud user2 has no write permissions on content.</li>
     * <li>Verify that cloud user3 has no read&write permissions on content.</li>
     * <li>Verify that user1 can see the both current user tasks in task history page.</li>
     * <li>cloud user1 writes the comments for both user tasks which appears on task history</li>
     * <li>Verify that OP user can see the completed task with two reviews from cloud user1.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_15243() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String cloudUser1 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer1cloud2");
        String cloudUser2 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer2cloud2");
        String cloudUser3 = getUserNameForDomain(testName, invitedDomain1).replace("userenterprise42", "reviewer3cloud2");
        String cloudSite1Name = getSiteName(testName + "cl1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();
        String user1Comments = "firstUserComments";
        String user2Comments = "secondUserComments";

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser2);
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setApprovalPercentage(100);
        formDetails.setReviewers(userNames);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        ShareUser.logout(drone);

        // Cloud user2 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
        MyTasksPage myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        TaskHistoryPage taskHistoryPage = myTasks.selectTaskHistory(workFlowName1).render();

        //Verifying that cloud user2 has no write permissions on content
        Assert.assertFalse(taskHistoryPage.isCancelTaskOrWorkFlowButtonDisplayed());

        // Cloud user1 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser3, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

        //Verifying that cloud user3 has no read&write permissions on content.
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone, workFlowName1, false));
        ShareUser.logout(hybridDrone);

        // Cloud user1 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName1));
        taskHistoryPage = myTasks.selectTaskHistory(workFlowName1).render();

        // Cloud1 user edits the own task and approves it
        EditTaskPage editTaskPage = selectEditLinkOnUserTask(cloudUser1, taskHistoryPage);
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.enterComment(user1Comments);
        editTaskPage.selectApproveButton().render();
        taskHistoryPage.render();

        // Cloud1 user edits the user2's task and approves it        
        editTaskPage = selectEditLinkOnUserTask(cloudUser2, taskHistoryPage);
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        editTaskPage.enterComment(user2Comments);
        editTaskPage.selectApproveButton().render();
        taskHistoryPage.render();

        ShareUser.logout(hybridDrone);

        // OP login and verifies the task is present 
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));

        TaskDetailsPage taskDetailsPage = myTasksPage.selectViewTasks(workFlowName1).render();

        //OP user verifying the both comments given by cloud user1.
        Assert.assertTrue(taskDetailsPage.getTaskDetailsHeader().contains(workFlowName1 + " (Document was approved on the cloud)"));
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser1 + " LName: " + user1Comments));
        Assert.assertTrue(taskDetailsPage.getComment().contains(cloudUser1 + " LName: " + user2Comments));
        Assert.assertFalse(taskDetailsPage.getComment().contains(cloudUser2));
        Assert.assertTrue(taskDetailsPage.getComment().contains("(Approved)"));

        ShareUser.logout(drone);
    }

    /**
     * ALF-9631:Upgrade Cloud account - Incomplete Workflow.
     * <ul>
     * <li>Create OP user</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_9631() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { opUser1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
    }

    /**
     * ALF-9631:Upgrade Cloud account - Incomplete Workflow.
     * <ul>
     * <li>Create 1 Cloud trialnet user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields filled with correct data</li>
     * <li>Choose cloud user as reviewers.</li>
     * <li>Start the work flow.</li>
     * <li>Verify Workflow is created successfully.</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups="Hybrid")
    public void alf_9631() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String trailDomainName = "trial" + System.currentTimeMillis() + ".test";
        String cloudUser1 = getUserNameForDomain(testName, trailDomainName).replace("userenterprise42", "cloud2user");
        String cloudSite1Name = getSiteName(testName + "cl1")  + System.currentTimeMillis();
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();
        String titleAndDescription = testName + ShareUser.getRandomStringWithNumders(4);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };

        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Creating trial domain.
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, trailDomainName, "1001");

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(50);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertNotNull(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));

        // Upgrading Trial Network account to Enterprise user account.
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, trailDomainName, "1000");

        // Modifying the synced document name in OP.
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, opFileName);
        editDocumentProperties.setDescription(titleAndDescription);
        editDocumentProperties.selectSave().render();

        ShareUser.logout(drone);

        // Cloud user1 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Modifying the synced document name to its actual name in Cloud.
        EditDocumentPropertiesPage editDocumentPropertiesCloud = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSite1Name, opFileName);
        editDocumentPropertiesCloud.setDocumentTitle(titleAndDescription);
        editDocumentPropertiesCloud.selectSave().render();

        // Approve the review task.
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone,workFlowName1));
        ShareUserWorkFlow.approveOrRejectTask(hybridDrone, cloudUser1, workFlowName1, "Approved by clouduser1", EditTaskAction.APPROVE);
        Assert.assertTrue(AbstractWorkflow.checkIfTaskIsPresent(hybridDrone,workFlowName1,false));

        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-9629:Downgrade Cloud account - Incomplete Workflow.
     * <ul>
     * <li>Create OP user</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_ALF_9629() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { opUser1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
    }

    /**
     * Note: This test case will be updated and uncommented once ALF-20442 is fixed.
     *
     * Downgrade Cloud account - Incomplete Workflow.
     * <ul>
     * <li>Create 1 Cloud trialnet user</li>
     * <li>Login to Cloud, Create a site, create a document within the site</li>
     * <li>Login to OP, Create Site and document, Start Cloud Review Task
     * Workflow</li>
     * <li>CloudTaskOrReview workflow type is chosen</li>
     * <li>All fields filled with correct data</li>
     * <li>Choose cloud user as reviewers.</li>
     * <li>Start the work flow.</li>
     * <li>Verify Workflow is created successfully.</li>
     * </ul>
     *
     * @throws Exception
     */
/*    @Test(groups="Hybrid")
    public void alf_9629() throws Exception
    {
        String testName = getTestName();
        String opUser1 = getUserNameForDomain(testName, DOMAIN_HYBRID);
        String trailDomainName = "trial" + System.currentTimeMillis() + ".test";
        String cloudUser1 = getUserNameForDomain(testName, trailDomainName).replace("userenterprise42", "cloud2user");
        String cloudSite1Name = getSiteName(testName + "cl1")  + System.currentTimeMillis();
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String workFlowName1 = testName + System.currentTimeMillis();
        String titleAndDescription = testName + ShareUser.getRandomStringWithNumders(4);
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        
        List<String> userNames = new ArrayList<String>();
        String[] opFileInfo = new String[] { opFileName };

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Creating trial domain.
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, trailDomainName, "1001");
        
        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSite1Name, SITE_VISIBILITY_PUBLIC);
        
        ShareUser.logout(hybridDrone);
        
        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.openDocumentLibrary(drone);
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Cloud Task or Review workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        // Adding reviewers
        userNames.add(cloudUser1);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName1);
        formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSite1Name);
        formDetails.setReviewers(userNames);
        formDetails.setApprovalPercentage(50);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(opFileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertNotNull(myWorkFlowsPage.isWorkFlowPresent(workFlowName1));
        
        
        // Modifying the synced document name in OP.
        EditDocumentPropertiesPopup editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, opFileName);
        editDocumentProperties.setDescription(titleAndDescription);
        editDocumentProperties.selectSave().render();
        
        // Cloud user1 login and verifies the task is present or not
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);
        
        // Modifying the synced document name to its actual name in Cloud.
        EditDocumentPropertiesPopup editDocumentPropertiesCloud = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSite1Name, opFileName);
        editDocumentPropertiesCloud.setDocumentTitle(titleAndDescription);
        editDocumentPropertiesCloud.selectSave().render();
        
        // Approve the review task.
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        
        // Add a comment to workflow from Cloud.
        String comment = "Cloud_comment" + ShareUser.getRandomStringWithNumders(4);
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        EditTaskPage editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName, cloudUser1).render();
        editTaskPage.enterComment(comment);
        editTaskPage.selectSaveButton().render();
        
        // Downgrading Trial Network account to free user account.
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, trailDomainName, "0");
        
        // Modifying the synced document name in OP.
        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, opFileName);
        String modifiedDesc = titleAndDescription + "modified";
        editDocumentProperties.setDescription(modifiedDesc);
        editDocumentProperties.selectSave().render();
        //Assert.assertFalse(ShareUser.checkIfContentIsSynced(drone, opFileName));
        
        // Modifying the synced document name in Cloud.
        editDocumentPropertiesCloud = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSite1Name, opFileName);
        String modifiedTitle = titleAndDescription + "updated";
        editDocumentPropertiesCloud.setDocumentTitle(modifiedTitle);
        editDocumentPropertiesCloud.selectSave().render();
        
        // Verifying the task details in Cloud.
        MyTasksPage myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        TaskDetailsPage taskDetails = myTasks.selectViewTasks(workFlowName1);
        TaskInfo taskDetailsInfo = taskDetails.getTaskDetailsInfo();

        Assert.assertTrue(taskDetails.getComment().equalsIgnoreCase(comment));
        Assert.assertTrue(taskDetailsInfo.getDueDateString().equals("(None)"));
        Assert.assertTrue(taskDetailsInfo.getPriority().equals(Priority.MEDIUM));
        
        // Verifying the task in My tasks in Cloud User dashboard page.
        ShareUser.openUserDashboard(hybridDrone);
        ShareUser.searchMyDashBoardWithRetry(hybridDrone, "tasks", workFlowName1, true);
        
        // Approve the review task in Cloud.
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        editTaskPage = myTasksPage.navigateToEditTaskPage(workFlowName, cloudUser1).render();
        editTaskPage.selectStatusDropDown(TaskStatus.COMPLETED);
        ShareErrorPopup errorPopUp = editTaskPage.selectApproveButton().render();
        
        Assert.assertNotNull(errorPopUp);
        Assert.assertNotNull(errorPopUp.handleErrorMessage());
    }*/

    /**
     * @param user
     * @param taskHistoryPage
     */
    private EditTaskPage selectEditLinkOnUserTask(String user, TaskHistoryPage taskHistoryPage)
    {
        for(WorkFlowDetailsCurrentTask currentTask : taskHistoryPage.getCurrentTasksList())
        {
            if(currentTask.getAssignedTo().contains(user))
            {
                return currentTask.getEditTaskLink().click().render();
            }
        }
        throw new PageOperationException("Unable for find the user current task.");
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