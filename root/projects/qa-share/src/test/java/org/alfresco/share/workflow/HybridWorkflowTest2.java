package org.alfresco.share.workflow;

import java.util.List;

import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDetails;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Naved Shah
 * 
 */
@Listeners(FailedTestListener.class)
public class HybridWorkflowTest2 extends AbstractWorkflow
{
    private static final Logger logger = Logger.getLogger(HybridWorkflowTest2.class);
    protected String testUser;
    protected String siteName = "";
    DocumentLibraryPage documentLibraryPage;
    protected long maxPageLoadingTime = 20000;
    protected String testName = "";
    protected String prefixIncomplete = "WFInComplete";
    protected String prefixComplete = "WFComplete";
    protected String prefixCompleteWithRemoveSync = "WFCompleteRemoveSync";
    protected String prefixCompleteWithRemoveFile = "WFCompltetRemoveFile";
    private String testDomain = "hybrid.test";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName() ;
        testDomain = "hybrid.test";
    }

    protected void dataprep_Incomplete(WebDrone drone, WebDrone hybridDrone, String prefix) throws Exception
    {
        testName = this.getClass().getSimpleName() ;
        String user1 = getUserNameForDomain(prefix + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefix + testName, testDomain);

        String opSiteName = getSiteName(prefix + testName) + "-OP";
        String cloudSiteName = getSiteName(prefix + testName) + "-CL";

        String fileName = getFileName(testName) + ".txt";
        String folderName = getFolderName(testName);

        String workFlowName = prefix + testName + "-WF";
        String dueDate = "12/05/2015";

        String[] userInfo1 = new String[] { user1 };
        String[] cloudUserInfo1 = new String[] { cloudUser };
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Cloud user logins and create site.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
        ShareUser.logout(hybridDrone);

        // User1 starts Workflow
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });
        
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        if (prefixCompleteWithRemoveSync.equals(prefix))
        {
            formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);
        }
        else if (prefixCompleteWithRemoveFile.equals(prefix))
        {
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
        }
        else
        {
            formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);
        }
        
        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);
        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);
        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        
        ShareUser.logout(drone);

    }

    protected void dataprep_Complete(WebDrone drone, WebDrone hybridDrone, String prefix) throws Exception
    {
        dataprep_Incomplete(drone, hybridDrone, prefix);
        String cloudUser = getUserNameForDomain(prefix + testName, testDomain);
        String user1 = getUserNameForDomain(prefix + testName, testDomain);
        String workFlowName = prefix + testName + "-WF";

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        // Login as OP user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15201() throws Exception
    {
        // TODO: Naveed- Workflow Tasktype should be Cloud Task or Review
        // TODO: Naveed - Please use startCloudReviewTaskWorkFlow instead of
        // startSimpleCloudTaskWorkFlow.

        dataprep_Incomplete(drone, hybridDrone, prefixIncomplete);
    }

    /**
     * ALF-15201 & ALF-15202:Create Simple Cloud Task and update when its
     * incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file
     * with cloud.</li>
     * <li>3) Go back to file uploaded and edit properties by modified title and
     * modifed description.</li>
     * <li>4) Login as CL-User, Open CL site and verfiy changes of updated
     * files.</li>
     * </ul>
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15201() throws Exception
    {
        String user1 = getUserNameForDomain(prefixIncomplete + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefixIncomplete + testName, testDomain);

        String opSiteName = getSiteName(prefixIncomplete + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixIncomplete + testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedFileTitle = testName + "modifiedBy " + System.currentTimeMillis();
        String descOfFile = fileName + " modified by " + System.currentTimeMillis();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + user1);
        editDocumentProperties.setDescription(descOfFile + user1);
        editDocumentProperties.selectSave().render();

        ShareUser.logout(drone);

        // TODO : TestLink: Please update the 2nd step accordingly.

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSiteName, fileName);

        Assert.assertTrue((modifiedFileTitle + user1).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by OP User is not present for Cloud.");
        Assert.assertTrue((descOfFile + user1).equals(editDocumentProperties.getDescription()),
                "Document Description modified by OP User is not present for Cloud.");

        // TODO: TestLink need to be updated. so changed property can reflect.
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + cloudUser);
        editDocumentProperties.setDescription(descOfFile + cloudUser);

        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);

        Assert.assertTrue((modifiedFileTitle + cloudUser).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by Cloud User is not present for OP.");
        Assert.assertTrue((descOfFile + cloudUser).equals(editDocumentProperties.getDescription()),
                "Document Description modified by CLoud User is not present for OP.");

        ShareUser.logout(drone);

    }

    /**
     * ALF-15203 & ALF-15204:Create Simple Cloud Task and update when its
     * incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file
     * with cloud.</li>
     * <li>3) Go back to file uploaded and edit properties by modified title and
     * modifed description.</li>
     * <li>4) Login as CL-User, Open CL site and verfiy changes of updated
     * files.</li>
     * </ul>
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15203() throws Exception
    {

        String user1 = getUserNameForDomain(prefixIncomplete + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixIncomplete + testName, testDomain);

        String opSiteName = getSiteName(prefixIncomplete + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixIncomplete + testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedContentByOnPrem = testName + " modifiedBy " + user1;
        String modifiedContentByCloud = testName + " modifiedBy " + cloudUser;

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentByOnPrem);
        contentDetails.setName(fileName);
        // Select Inline Edit and change the content and save
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
        documentDetailsPage = documentLibraryPage.selectFile(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertTrue(inlineEditPage.getDetails().getContent().contains(modifiedContentByOnPrem));

        contentDetails.setContent(modifiedContentByCloud);
        documentDetailsPage = inlineEditPage.save(contentDetails).render();
        ShareUser.logout(hybridDrone);

        // TODO : TestLink: Please update the 2nd step accordingly.

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        documentDetailsPage = documentLibraryPage.selectFile(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertTrue(inlineEditPage.getDetails().getContent().contains(modifiedContentByCloud));

        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15207() throws Exception
    {
        // TODO: Naveed- Workflow Tasktype should be Cloud Task or Reivew
        // TODO: Naveed - Please use startCloudReviewTaskWorkFlow instead of
        // startSimpleCloudTaskWorkFlow.
        dataprep_Incomplete(drone, hybridDrone, getTestName());
    }

    /**
     * ALF-15207 & ALF-208:Create Simple Cloud Task and update when its
     * incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file
     * with cloud.</li>
     * <li>3) Go back to file uploaded and try to delete the file.
     * <li>4) Login as CL-User, Open CL site and verfiy no chane happened.
     * </ul>
     */
    @Test(groups = "Hybrid-Bug", enabled = true)
    public void ALF_15207() throws Exception
    {
        prefixIncomplete = getTestName() ;
        String user1 = getUserNameForDomain(prefixIncomplete + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefixIncomplete + testName, testDomain);

        String opSiteName = getSiteName(prefixIncomplete + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixIncomplete + testName) + "-CL";

        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(drone).render();
        // TODO: Naveed - Update the testlink step1 according to the assertion.
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "File part of workflow is getting delete due to bug alf-20133");
        ShareUser.logout(drone);
        // TODO : TestLink: Please update the 2nd step accordingly.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(hybridDrone).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "File part of workflow is getting delete due to bug alf-20133");
        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-15209 & ALF-210:Create Simple Cloud Task and update when its
     * incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file
     * with cloud.</li>
     * <li>3) Go back to file uploaded and try to unsync the file.
     * <li>4) Login as CL-User, Open CL site and verfiy no chane happened.
     * </ul>
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15209() throws Exception
    {
        String user1 = getUserNameForDomain(prefixIncomplete + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefixIncomplete + testName, testDomain);

        String opSiteName = getSiteName(prefixIncomplete + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixIncomplete + testName) + "-CL";

        String fileName = getFileName(testName) + ".txt";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();

        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());

        ShareUser.logout(drone);
        // TODO : TestLink: Please update the 2nd step accordingly.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();

        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15205() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15205 :Create Simple Cloud Task and update when its incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file
     * with cloud.</li>
     * <li>3) Go back to file uploaded and try to move the file to different
     * folder.
     * <li>4) Login as CL-User, Open CL site and verfiy location of sync is
     * changed.
     * </ul>
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15205() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // TODO: Naveed - Please use startCloudReviewTaskWorkFlow instead of
        // startSimpleCloudTaskWorkFlow.
        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);

        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        MyWorkFlowsPage myWorkFlowsPage = (MyWorkFlowsPage) ShareUser.getSharePage(drone);

        // Verify workflow details
        List<WorkFlowDetails> workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);
        Assert.assertEquals(workFlowDetails.size(), 1);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton();
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15206() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);

        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15206 :Create Simple Cloud Task and update when its incomplete.
     * <ul>
     * <li>1) Login to CL user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file
     * with cloud.</li>
     * <li>3) Go back to file uploaded and try to move the file to different
     * folder.
     * <li>4) Login as OP-User, Open OP site and verfiy location of sync is
     * changed.
     * </ul>
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15206() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // TODO: Naveed - Please use startCloudReviewTaskWorkFlow instead of
        // startSimpleCloudTaskWorkFlow.
        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        // TODO: Naveed - TaskType should be Cloud_Task_Review
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        MyWorkFlowsPage myWorkFlowsPage = (MyWorkFlowsPage) ShareUser.getSharePage(drone);

        // Verify workflow details
        List<WorkFlowDetails> workFlowDetails = myWorkFlowsPage.getWorkFlowDetails(workFlowName);
        Assert.assertEquals(workFlowDetails.size(), 1);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton();
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render().getCloudSyncLocation().contains(folderName));
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15211() throws Exception
    {
        dataprep_Complete(drone, hybridDrone, prefixComplete);
    }

    /**
     * ALF-15211 & ALF-212 : With complete workflow check for modified
     * properties.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15211() throws Exception
    {

        // dataPrep_15211(drone, hybridDrone);
        String user1 = getUserNameForDomain(prefixComplete + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixComplete + testName, testDomain);

        String opSiteName = getSiteName(prefixComplete + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixComplete + testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedFileTitle = testName + "modifiedBy " + System.currentTimeMillis();
        String descOfFile = fileName + " modified by " + System.currentTimeMillis();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + user1);
        editDocumentProperties.setDescription(descOfFile + user1);
        editDocumentProperties.selectSave().render();

        ShareUser.logout(drone);

        // TODO : TestLink: Please update the 2nd step accordingly.

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSiteName, fileName).render();

        Assert.assertTrue((modifiedFileTitle + user1).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by OP User is not present for Cloud.");
        Assert.assertTrue((descOfFile + user1).equals(editDocumentProperties.getDescription()),
                "Document Description modified by OP User is not present for Cloud.");

        editDocumentProperties.setDocumentTitle(modifiedFileTitle + cloudUser);
        editDocumentProperties.setDescription(descOfFile + cloudUser);

        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);

        Assert.assertTrue((modifiedFileTitle + cloudUser).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by Cloud User is not present for OP.");
        Assert.assertTrue((descOfFile + cloudUser).equals(editDocumentProperties.getDescription()),
                "Document Description modified by CLoud User is not present for OP.");

        ShareUser.logout(drone);
    }

    /**
     * ALF-15213 & ALF-214 : With complete workflow check for modified contents.
     * 
     * //TODO : TestLink tests needs to updated according to the combining
     * steps.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15213() throws Exception
    {

        String user1 = getUserNameForDomain(prefixComplete + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefixComplete + testName, testDomain);

        String opSiteName = getSiteName(prefixComplete + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixComplete + testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedContentByOnPrem = testName + " modifiedBy " + user1;
        String modifiedContentByCloud = testName + " modifiedBy " + cloudUser;

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentByOnPrem);
        contentDetails.setName(fileName);
        // Select Inline Edit and change the content and save
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        ShareUser.logout(drone);

        // TODO : TestLink: Please update the 2nd step accordingly.

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
        documentDetailsPage = documentLibraryPage.selectFile(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertTrue(((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).getDetails().getContent()
                .contains(modifiedContentByOnPrem));

        contentDetails.setContent(modifiedContentByCloud);
        documentDetailsPage = inlineEditPage.save(contentDetails).render();
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        documentDetailsPage = documentLibraryPage.selectFile(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertTrue(((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).getDetails().getContent()
                .contains(modifiedContentByCloud));

        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15215() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15215 : With complete workflow check for moved content location in
     * cloud site.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15215() throws Exception
    {
        // dataPrep_15215(drone, hybridDrone);
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton();
        ShareUser.logout(drone);

        // TODO : TestLink: Please update the 2nd step accordingly.

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15216() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15216 : With complete workflow check for moved content location in
     * On-Prem site.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15216() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton();
        ShareUser.logout(hybridDrone);
        // TODO : TestLink: Please update the 2nd step accordingly.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render().getCloudSyncLocation().contains(folderName));
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15217() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15217 : With complete workflow check for delete content location in
     * On-Prem site.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15217() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        ShareUser.deleteSelectedContent(drone).render();
        Assert.assertTrue(ShareUser.checkIfContentIsPresent(drone, fileName, false));
        ShareUser.logout(drone);
        // TODO : TestLink: Please update the 2nd step accordingly.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertTrue(ShareUser.checkIfContentIsPresent(hybridDrone, fileName, false));
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15218() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15218 : With complete workflow check for delete content location in
     * On-Prem site.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15218() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(hybridDrone).render();
        Assert.assertTrue(ShareUser.checkIfContentIsPresent(hybridDrone, fileName, false));
        ShareUser.logout(hybridDrone);
        
        // TODO : TestLink: Please update the 2nd step accordingly.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName));
        
        // TODO : Once document deleted it will not be in sync. - QA-588
        Assert.assertTrue(ShareUser.checkIfContentIsSynced(drone, fileName));
        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15219() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15219 : With complete workflow check for unsync content in Cloud
     * site.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15219() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectUnSyncAndRemoveContentFromCloud(false).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);
        // TODO : TestLink: Please update the 2nd step accordingly.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15220() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.createActivateUserAsTenantAdmin(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15220 : With complete workflow check for unsync content in on prem
     * site.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15220() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = getDueDateString();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
        documentLibraryPage.getFileDirectoryInfo(fileName).selectForceUnSyncInCloud();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);
        // TODO : TestLink: Please update the 2nd step accordingly.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15221() throws Exception
    {
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveSync);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15221() throws Exception
    {

        String user1 = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedFileTitle = testName + "modifiedBy " + System.currentTimeMillis();
        String descOfFile = fileName + " modified by " + System.currentTimeMillis();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + user1 + "OP");
        editDocumentProperties.setDescription(descOfFile + user1 + "OP");
        editDocumentProperties.selectSave().render();

        ShareUser.logout(drone);
        // TODO : TestLink: Please update the 2nd step accordingly.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(hybridDrone, cloudSiteName, fileName);

        Assert.assertFalse((modifiedFileTitle + user1).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by OP User is not present for Cloud.");
        Assert.assertFalse((descOfFile + user1).equals(editDocumentProperties.getDescription()),
                "Document Description modified by OP User is not present for Cloud.");

        // TODO : Please update TestLink according to below steps.
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + cloudUser + "CL");
        editDocumentProperties.setDescription(descOfFile + cloudUser + "CL");

        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);

        Assert.assertFalse((modifiedFileTitle + cloudUser).equals(editDocumentProperties.getDocumentTitle()),
                "Document Title modified by Cloud User is not present for OP.");
        Assert.assertFalse((descOfFile + cloudUser).equals(editDocumentProperties.getDescription()),
                "Document Description modified by CLoud User is not present for OP.");

        ShareUser.logout(drone);

    }

    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15223() throws Exception
    {
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveSync);
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveSync + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveSync + testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedContentByOnPrem = testName + " modifiedBy: OP User" + System.currentTimeMillis();
        String modifiedContentByCloud = testName + " modifiedBy: Cloud User" + System.currentTimeMillis();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentByOnPrem);
        contentDetails.setName(fileName);
        
        // Select Inline Edit and change the content and save
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        ShareUser.logout(drone);
        
        // TODO : TestLink: Please update the 2nd step accordingly.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
        
        documentDetailsPage = documentLibraryPage.selectFile(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertFalse(inlineEditPage.getDetails().getContent().contains(modifiedContentByOnPrem));
        // TODO : Please update TestLink according to below steps.
        contentDetails.setContent(modifiedContentByCloud);
        documentDetailsPage = inlineEditPage.save(contentDetails).render();
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
        documentDetailsPage = documentLibraryPage.selectFile(fileName);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertFalse(inlineEditPage.getDetails().getContent().contains(modifiedContentByCloud));
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15225() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15225 :Create Simple Cloud Task and update when its incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file
     * with cloud.</li>
     * <li>3) Go back to file uploaded and try to move the file to different
     * folder.
     * <li>4) Login as CL-User, Open CL site and verfiy location of sync is
     * changed.
     * </ul>
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15225() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton();
        ShareUser.logout(drone);
        // TODO : Please update 2nd step in TestLink as verifying in logs is not
        // present.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15226() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15226 :Create Simple Cloud Task and update when its incomplete.
     * <ul>
     * <li>1) Login to CL user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file
     * with cloud.</li>
     * <li>3) Go back to file uploaded and try to move the file to different
     * folder.
     * <li>4) Login as OP-User, Open OP site and verfiy location of sync is
     * changed.
     * </ul>
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15226() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton();
        ShareUser.logout(hybridDrone);
        // TODO : Please update 2nd step in TestLink as verifying in logs is not
        // present.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15227() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15227 : With complete workflow check for delete content location in
     * On-Prem site.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15227() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(drone).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(drone);
        // TODO : Please update 2nd step in TestLink as verifying in logs is not
        // present.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15228() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15228 : With complete workflow check for delete content location in
     * On-Prem site.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15228() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(hybridDrone).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
        // TODO : Please update 2nd step in TestLink as verifying in logs is not
        // present.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15229() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15229 : With complete workflow check for unsync content in Cloud
     * site.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15229() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15230() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15230 : With complete workflow check for unsync content in on prem
     * site.
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15230() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15231() throws Exception
    {
        dataprep_Complete(drone, hybridDrone, prefixCompleteWithRemoveFile);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15231() throws Exception
    {

        String user1 = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedFileTitle = testName + "modifiedBy " + System.currentTimeMillis();
        String descOfFile = fileName + " modified by " + System.currentTimeMillis();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        EditDocumentPropertiesPage editDocumentProperties = ShareUserSitePage.getEditPropertiesFromDocLibPage(drone, opSiteName, fileName);
        editDocumentProperties.setDocumentTitle(modifiedFileTitle + user1);
        editDocumentProperties.setDescription(descOfFile + user1);
        editDocumentProperties.selectSave().render();

        ShareUser.logout(drone);
        // TODO : Please update 2nd step in TestLink as verifying in logs is not
        // present.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15232() throws Exception
    {

        String user1 = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);

        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String modifiedContentByOnPrem = testName + " modifiedBy " + user1;

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modifiedContentByOnPrem);
        contentDetails.setName(fileName);
        // Select Inline Edit and change the content and save
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();

        ShareUser.logout(drone);
        // TODO : Please update 2nd step in TestLink as verifying in logs is not
        // present.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15235() throws Exception
    {
        String user1 = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);
        String cloudUser = getUserNameForDomain(prefixCompleteWithRemoveFile + testName, testDomain);

        String opSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + "-OP";
        String cloudSiteName = getSiteName(prefixCompleteWithRemoveFile + testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced());
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15233() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.

    }

    /**
     * ALF-15225 :Create Simple Cloud Task and update when its incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file
     * with cloud.</li>
     * <li>3) Go back to file uploaded and try to move the file to different
     * folder.
     * <li>4) Login as CL-User, Open CL site and verfiy location of sync is
     * changed.
     * </ul>
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15233() throws Exception
    {
        // dataprep_Incomplete(drone, hybridDrone, prefixIncomplete);
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String folderName = getFolderName(testName);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        moveToPage.selectPath(folderName).render().selectOkButton();
        ShareUser.logout(drone);
        // TODO : Please update 2nd step in TestLink as verifying in logs is not
        // present.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15234() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String folderName = getFolderName(testName);
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
        // TODO : Update the TestLink to move the 7 and 8 steps into step
        // Actions.
    }

    /**
     * ALF-15225 :Create Simple Cloud Task and update when its incomplete.
     * <ul>
     * <li>1) Login to OP user and upload a file.</li>
     * <li>2) Start work flow with the file created on 1. which will sync file
     * with cloud.</li>
     * <li>3) Go back to file uploaded and try to move the file to different
     * folder.
     * <li>4) Login as CL-User, Open CL site and verfiy location of sync is
     * changed.
     * </ul>
     */
    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15234() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";

        String workFlowName = testName + System.currentTimeMillis() + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        documentLibraryPage.getFileDirectoryInfo(fileName).selectCheckbox();
        documentLibraryPage = ShareUser.deleteSelectedContent(drone).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(drone);
        // TODO : Please update 2nd step in TestLink as verifying in logs is not
        // present.
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_9612() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String workFlowName = testName + "-WF";
        String dueDate = "12/05/2015";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName).render();
        ShareUser.logout(drone);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void ALF_9612() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        SharePopup errorPopup = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertTrue(errorPopup
                .getShareMessage()
                .contains(
                        "One of the selected documents is already syncronized with the Cloud. You can only use content that is not yet syncronized with the Cloud to start a new Hybrid Workflow."));

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        docLibPage.isFileVisible(fileName);
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isPartOfWorkflow());
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15236() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String workFlowName = testName + "-WF";
        String dueDate = "12/05/2015";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15236() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String workFlowName = testName + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENTREMOVESYNC);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        SharePopup errorPopup = cloudTaskOrReviewPage.startWorkflow(formDetails).render();
        Assert.assertTrue(errorPopup.getShareMessage().contains(
                "One of the selected documents is already syncronized with the Cloud. "
                        + "You can only use content that is not yet syncronized with the Cloud " + "to start a new Hybrid Workflow."));

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        docLibPage.isFileVisible(fileName);
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isPartOfWorkflow());
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "DataPrepHybridWorkflow2")
    public void dataPrep_15237() throws Exception
    {
        String testName = getTestName() ;
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as Enterprise user, create site and upload a file.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = "Hybrid", enabled = true)
    public void ALF_15237() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String workFlowName = testName + "-WF";
        String dueDate = "12/05/2015";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, opSiteName).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser);
        formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(hybridDrone, user1, workFlowName);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUserWorkFlow.navigateToMyTasksPage(drone);
        ShareUser.checkIfTaskIsPresent(drone, workFlowName);
        ShareUserWorkFlow.completeWorkFlow(drone, user1, workFlowName);

        // Start Simple Cloud Task workflow
        cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

        // Select uploaded file
        cloudTaskOrReviewPage.selectItem(fileName, opSiteName);

        // Fill the form details and start workflow
        MyWorkFlowsPage myWorkFlowsPage = (MyWorkFlowsPage) cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        myWorkFlowsPage.render();
        Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName), "Verifying workflow exists");
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName).render();
        docLibPage.isFileVisible(fileName);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isPartOfWorkflow());
        ShareUser.logout(hybridDrone);

    }
}
