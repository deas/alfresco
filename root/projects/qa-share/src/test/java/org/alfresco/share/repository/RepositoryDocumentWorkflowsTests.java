package org.alfresco.share.repository;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPopup;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.site.document.TagPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowDetailsCurrentTask;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class RepositoryDocumentWorkflowsTests extends AbstractTests
{
    private static final Logger logger = Logger.getLogger(RepositoryDocumentWorkflowsTests.class);

    private String testUser;
    private static final String USER_HOMES_FOLDER = "User Homes";
    private static final String DOCUMENT_LIBRARY = "documentLibrary";
    private String testUserFolderHome = REPO + SLASH + USER_HOMES_FOLDER + SLASH;

    /**
     * A single user for the class is created and assigned admin rights
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        // create a single user
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUser);
    }

    /**
     * User logs in before test is executed
     * 
     * @throws Exception
     */
    @BeforeMethod(groups = { "RepositoryDocumentWorkflows" })
    public void prepare() throws Exception
    {
        // login as created user
        try
        {
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            logger.info("RepositoryDocumentWorkflowsTests user logged in - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    /**
     * User logs out after executing test
     * 
     * @throws Exception
     */
    @AfterMethod(groups = { "RepositoryDocumentWorkflows" })
    public void quit() throws Exception
    {
        // login as created user
        try
        {
            ShareUser.logout(drone);
            logger.info("RepositoryDocumentWorkflowsTests user logged out - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    /**
     * Copy to repository folder
     * 1) Upload file to test folder
     * 2) Add a tag to the file
     * 3) Create folder in users home folder
     * 4) Copy file to the created folder
     * 5) Check the file is copied to the folder
     * 6) Copy copied file in copy folder
     * 7) Check there is a copy of the file in the copy folder
     * 8) Check the tag scope increased
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5449() throws Exception
    {
        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        String fileName = sampleFile.getName();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // add a tag to the file
        String tag = "tag" + System.currentTimeMillis();
        EditDocumentPropertiesPopup editDocumentPropertiesPopup = repositoryPage.getFileDirectoryInfo(fileName).selectEditProperties().render();
        TagPage tagPage = editDocumentPropertiesPopup.getTag().render();
        tagPage = tagPage.enterTagValue(tag).render();
        tagPage.clickOkButton();
        editDocumentPropertiesPopup.selectSave().render();

        // create folder in users home folder
        String copyFolderName = "Copy Folder" + System.currentTimeMillis();
        String copyFolderDescription = "Copy Folder Description";
        ShareUserRepositoryPage.createFolderInRepository(drone, copyFolderName, copyFolderName, copyFolderDescription);

        // copy file to the created folder
        repositoryPage.getFileDirectoryInfo(copyFolderName).selectCheckbox();
        String[] copyDestinationFolders = { testUser, copyFolderName };
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, fileName, copyDestinationFolders, true);

        // check the file is copied to the folder - go to created folder first
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] testFolderPath = { USER_HOMES_FOLDER, testUser, testName, copyFolderName };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);
        FileDirectoryInfo copyFolderFileDirectoryInfo = repositoryPage.getFileDirectoryInfo(fileName);
        Assert.assertNotNull(copyFolderFileDirectoryInfo);

        // copy copied file in copy folder
        String[] destinationFolders = { copyFolderName };
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, fileName, destinationFolders, true);
        String copiedFileName = "Copy of " + fileName;

        // check the file is copied
        Assert.assertNotNull(repositoryPage.getFileDirectoryInfo(copiedFileName));

        // check the tag scope increased
        DocumentLibraryPage documentLibraryPage = repositoryPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(tag).render();
        Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(drone, copiedFileName, true));
        List<FileDirectoryInfo> files = documentLibraryPage.getFiles();
        Assert.assertTrue(files.size() == 3);

    }

    /**
     * Copy to site
     * 1) Create a site
     * 2) Upload file to test folder
     * 3) Copy uploaded file to the site
     * 4) Check the file is present in the sites document library
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5450() throws Exception
    {
        // create a site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, "Public");

        // Upload file to test folder
        File sampleFile = SiteUtil.prepareFile();
        String fileName = sampleFile.getName();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // copy uploaded file to the site
        String[] copySitesFolders = { "Sites", siteName, DOCUMENT_LIBRARY };
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, fileName, copySitesFolders, true);

        // check the file is present in the sites document library
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertNotNull(documentLibraryPage.getFileDirectoryInfo(fileName));

    }

    /**
     * Copy to site - cancel
     * Copy to folder - cancel
     * 1) Create site
     * 2) Upload file to test folder
     * 3) Cancel copying to the site's document library
     * 4) Check the file is not copied to the site's document library
     * 5) Create folder in users home folder
     * 6) Cancel copying of the file to the created folder
     * 7) Check the file is not copied
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5451() throws Exception
    {
        // create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, "Public");

        // Upload file to test folder
        File sampleFile = SiteUtil.prepareFile();
        String fileName = sampleFile.getName();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // cancel copying to the site's document library
        String[] copySitesFolders = { "Sites", siteName, DOCUMENT_LIBRARY };
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryCancel(drone, fileName, copySitesFolders, true);

        // check the file is not copied to the site's document library
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        List<FileDirectoryInfo> files = documentLibraryPage.getFiles();
        Assert.assertTrue(files.size() == 0);

        // create folder in users home folder
        String copyFolderName = "Copy Folder" + System.currentTimeMillis();
        String copyFolderDescription = "Copy Folder Description";
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] testFolderPath = { USER_HOMES_FOLDER, testUser, testName };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);
        ShareUserRepositoryPage.createFolderInRepository(drone, copyFolderName, copyFolderName, copyFolderDescription);

        // cancel copying of the file to the created folder
        String[] copyDestinationFolders = { testUser, copyFolderName };
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryCancel(drone, fileName, copyDestinationFolders, true);

        // check the file is not copied
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(copyFolderName);
        Assert.assertTrue(repositoryPage.getFiles().size() == 0);

    }

    /**
     * Move to repository folder
     * 1) Upload file to test folder
     * 2) Create folder in users home folder
     * 3) Move the file to the created folder
     * 4) Check the file is not present in users home directory anymore
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5452() throws Exception
    {

        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        String fileName = sampleFile.getName();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);

        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // create folder in users home folder
        String moveFolderName = "Move Folder" + System.currentTimeMillis();
        String moveFolderDescription = "Move Folder Description";
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
	  ShareUserRepositoryPage.createFolderInFolderInRepository(drone, moveFolderName, moveFolderDescription, testUserFolderHome + testUser + SLASH + testName);

        // move the file to the created folder
        String[] moveDestinationFolders = { testUser, moveFolderName };
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, fileName, moveDestinationFolders, false);

        // check there is no file in users home directory
        List<FileDirectoryInfo> files = repositoryPage.getFiles();
        for (FileDirectoryInfo file : files)
        {
            Assert.assertFalse(fileName.equalsIgnoreCase(file.getName()));
        }

        // check the file is moved to the folder - first navigate to created folder
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] testFolderPath = { USER_HOMES_FOLDER, testUser, testName, moveFolderName };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);
        Assert.assertNotNull(repositoryPage.getFileDirectoryInfo(fileName));

    }

    /**
     * Move to site
     * 1)Create site
     * 2)Upload file to test folder
     * 3)Move the file to the site
     * 4)Check there is no file in users home folder
     * 5)Check the file is in the sites document library
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5453() throws Exception
    {
        // create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, "Public");

        // Upload file to test folder
        File sampleFile = SiteUtil.prepareFile();
        String fileName = sampleFile.getName();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);

        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // move the file to the site
        String[] moveSitesFolders = { "Sites", siteName, DOCUMENT_LIBRARY };
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, fileName, moveSitesFolders, false);

        // check there is no file in users home folder
        List<FileDirectoryInfo> files = repositoryPage.getFiles();
        for (FileDirectoryInfo file : files)
        {
            Assert.assertFalse(fileName.equalsIgnoreCase(file.getName()));
        }

        // check the file is in the sites document library
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertNotNull(documentLibraryPage.getFileDirectoryInfo(fileName));

    }

    /**
     * Move to site - cancel
     * Move to folder - cancel
     * 1) Create site
     * 2) Upload file to the test folder
     * 3) Cancel moving to the site's document library
     * 4) Check the file is not moved to sites document library
     * 5) Check the file is still present in users home folder
     * 6) Create folder in users home folder
     * 7) Cancel moving of file to the created folder
     * 8) Check the file is not moved to created folder
     * 9) check the file is still present in users home folder
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5454() throws Exception
    {

        // create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, "Public");

        // Upload file to test folder
        File sampleFile = SiteUtil.prepareFile();
        String fileName = sampleFile.getName();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);

        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // move to sites document library - cancel
        String[] moveSitesFolders = { "Sites", siteName, DOCUMENT_LIBRARY };
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryCancel(drone, fileName, moveSitesFolders, false);

        // check the file is not moved to sites document library
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        List<FileDirectoryInfo> files = documentLibraryPage.getFiles();
        Assert.assertTrue(files.size() == 0);
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] testFolderPath = { USER_HOMES_FOLDER, testUser, testName };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);

        // check the file is still present in users home folder
        Assert.assertNotNull(repositoryPage.getFileDirectoryInfo(fileName));

        // create folder in users home folder
        String moveFolderName = "Move Folder" + System.currentTimeMillis();
        String moveFolderDescription = "Move Folder Description";
        repositoryPage = ShareUserRepositoryPage.createFolderInRepository(drone, moveFolderName, moveFolderName, moveFolderDescription);
        repositoryPage.getFileDirectoryInfo(moveFolderName).selectCheckbox();

        // cancel moving of file to the created folder
        String[] moveDestinationFolders = { testUser, moveFolderName };
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryCancel(drone, fileName, moveDestinationFolders, false);

        // check the file is not moved to folder -- navigate to created folder
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(moveFolderName);
        Assert.assertTrue(repositoryPage.getFiles().size() == 0);

        // check the file is still present in users home folder
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);

        // repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, testUserFolder + SLASH + testName);
        Assert.assertNotNull(repositoryPage.getFileDirectoryInfo(fileName));

    }

    /**
     * Document delete
     * 1) Upload file to the test folder
     * 2) Delete file
     * 3) Check the file is deleted
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5455() throws Exception
    {

        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        String fileName = sampleFile.getName();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);

        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // delete file
        ShareUser.selectContentCheckBox(drone, fileName);
        ShareUser.deleteSelectedContent(drone);

        // check the file is deleted
        Assert.assertFalse(repositoryPage.isFileVisible(fileName));

    }

    /**
     * Cancel deleting of document
     * 1) Upload file to the test folder
     * 2) Cancel deleting of the file
     * 3) Check the file is not deleted
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5456() throws Exception
    {

        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        String fileName = sampleFile.getName();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);

        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // cancel deleting of the file
        ShareUser.selectContentCheckBox(drone, fileName);
        DocumentLibraryNavigation docLibNavOption = repositoryPage.getNavigation().render();
        ConfirmDeletePage deletePage = docLibNavOption.selectDelete().render();
        repositoryPage = deletePage.selectAction(Action.Cancel).render();

        // check the file is not deleted
        Assert.assertTrue(repositoryPage.isFileVisible(fileName));

    }

    /**
     * Start new workflow
     * 1) Create a site
     * 2) Upload file to the test folder
     * 3) Site creator starts the new workflow
     * 4) Check the document is marked with icon
     * 5) Check the workflow is not present in MyTasks - active tasks
     * 6) Site creator logs out and assignee user logs in
     * 7) Check the task is present in MyTasks for assignee user
     * 8) Assignee user clicks press Task Done
     * 9) Check that task is not present in MyTasks for assignee user
     * 10) Assignee user logs out and task creator logs in
     * 11) Check the task is present in MyTasks for site creator
     * 12) Site creator clicks press Task Done
     * 13) Check that task is not in MyTasks for site creator
     * 14) Check the document is not marked with icon
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5457() throws Exception
    {
        // create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, "Public");

        // Upload file to test folder
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);

        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // start the new workflow
        StartWorkFlowPage startWorkFlowPage = repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectStartWorkFlow().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
        formDetails.setMessage(workFlowName1);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        repositoryPage = newWorkflowPage.startWorkflow(formDetails).render();

        // check the document is marked with icon
        assertTrue(repositoryPage.getFileDirectoryInfo(sampleFile.getName()).isPartOfWorkflow(), "Verifying the file is part of a workflow");

        // check the workflow is not present in MyTasks - active tasks
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));

        // site creator logs out and assignee user logs in
        ShareUser.logout(drone);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // check the task is in MyTasks for site creator
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertTrue(myTasksPage.isTaskPresent(workFlowName1));

        // press Task Done
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName1);
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.selectTaskDoneButton().render();

        // check that task is not in MyTasks for site creator
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));

        // assignee user logs out and task creator logs in
        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // task is in MyTasks for site creator
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertTrue(myTasksPage.isTaskPresent(workFlowName1));

        // press Task Done
        taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName1);
        editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.selectTaskDoneButton().render();

        // check the task not present in MyTasks for site creator
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));

        // check the document is not marked with icon
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] testFolderPath = { USER_HOMES_FOLDER, testUser, testName };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);
        Assert.assertFalse(repositoryPage.getFileDirectoryInfo(sampleFile.getName()).isPartOfWorkflow(), "Verifying the file is not part of a workflow");

    }

    /**
     * Review and Approve workflow
     * 1) Create a site
     * 2) Upload file to the test folder
     * 3) Start Review and Approve workflow
     * 4) Check the document is marked with icon
     * 5) Check that task is not present in MyTasks for site creator
     * 6) Log out as site creator and log in as assignee user
     * 7) Check the task is present in MyTasks for assignee user
     * 8) Approve the task
     * 9) Log out as assignee user and login as task creator
     * 10) Check the task is present in MyTasks for assignee user
     * 11) Approve the task
     * 12) Log out as assignee user and login as task creator
     * 13) Go to Workflows I've started
     * 14) Click on Actions - edit task
     * 15) Click on Task Done link
     * 16) Check the document is not marked with icon
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5458() throws Exception
    {
        // create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, "Public");

        // Upload file to test folder
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);

        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // start Review and Approve workflow
        StartWorkFlowPage startWorkFlowPage = repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectStartWorkFlow().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.REVIEW_AND_APPROVE)).render();
        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
        formDetails.setMessage(workFlowName1);
        formDetails.setDueDate(dueDate);
        formDetails.setAssignee(username);
        formDetails.setTaskPriority(Priority.MEDIUM);
        repositoryPage = newWorkflowPage.startWorkflow(formDetails).render();

        // check the document is marked with icon
        assertTrue(repositoryPage.getFileDirectoryInfo(sampleFile.getName()).isPartOfWorkflow(), "Verifying the file is part of a workflow");

        // check the workflow is not present in MyTasks - active tasks
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));

        // log out as site creator and log in as assignee user
        ShareUser.logout(drone);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // check the task is present in MyTasks for assignee user
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertTrue(myTasksPage.isTaskPresent(workFlowName1));

        // approve the task
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName1);
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.selectApproveButton().render();

        // TODO: task disappeared from MyTasks list - not possible for the time being because of task name change after approval (ALF-20546)

        // log out as assignee user and login as task creator
        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // task appeared in MyTasks list - not possible for the time being because of task name change after approval

        // go to Workflows I've started
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        WorkFlowDetailsPage workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName1).render();
        List<WorkFlowDetailsCurrentTask> currentTaskList = workFlowDetailsPage.getCurrentTasksList();

        // click on Actions - edit task
        editTaskPage = currentTaskList.get(0).getEditTaskLink().click().render();

        // click on Task Done link
        editTaskPage.selectTaskDoneButton().render();

        // TODO: task disappeared from MyTasks list - not possible for the time being because of task name change after approval (ALF-20546)

        // check the document is not marked with icon
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] testFolderPath = { USER_HOMES_FOLDER, testUser, testName };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);
        assertFalse(repositoryPage.getFileDirectoryInfo(sampleFile.getName()).isPartOfWorkflow(), "Verifying the file is not part of a workflow");

    }

    /**
     * Reject Review and Approve workflow
     * 1) Create a site
     * 2) Upload file to the test folder
     * 3) Start Review and Approve workflow
     * 4) Check the document is marked with icon
     * 5) Check the workflow is not present in MyTasks for site creator
     * 6) Log out as assignee user and login as task creator
     * 7) Check that the task is in MyTasks for assignee user
     * 8) Click reject
     * 9) Check that the task disappeared from MyTasks list for for assignee user
     * 10) Log out as assignee user and login as task creator
     * 11) Go to Workflows I've started
     * 12) Click on Task Done link
     * 13) Check the document is not marked with icon
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5459() throws Exception
    {
        // create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, "Public");

        // Upload file to test folder
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);

        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // start Review and Approve workflow
        StartWorkFlowPage startWorkFlowPage = repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectStartWorkFlow().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.REVIEW_AND_APPROVE)).render();
        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
        formDetails.setMessage(workFlowName1);
        formDetails.setDueDate(dueDate);
        formDetails.setAssignee(username);
        formDetails.setTaskPriority(Priority.MEDIUM);
        repositoryPage = newWorkflowPage.startWorkflow(formDetails).render();

        // check the document is marked with icon
        assertTrue(repositoryPage.getFileDirectoryInfo(sampleFile.getName()).isPartOfWorkflow(), "Verifying the file is part of a workflow");

        // check the workflow is not present in MyTasks - active tasks
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));

        // log out as site creator and log in as assignee user
        ShareUser.logout(drone);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // check that the task is in MyTasks for assignee user
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertTrue(myTasksPage.isTaskPresent(workFlowName1));

        // click reject
        TaskDetailsPage taskDetailsPage = ShareUserWorkFlow.navigateToTaskDetailsPage(drone, workFlowName1);
        EditTaskPage editTaskPage = taskDetailsPage.selectEditButton().render();
        editTaskPage.selectRejectButton().render();

        // check that the task disappeared from MyTasks list for for assignee user
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));

        // log out as assignee user and login as task creator
        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // TODO: task appeared in MyTasks list - not possible for the time being because of task name change after rejection (ALF-20546)

        // go to Workflows I've started
        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
        WorkFlowDetailsPage workFlowDetailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName1).render();
        List<WorkFlowDetailsCurrentTask> currentTaskList = workFlowDetailsPage.getCurrentTasksList();

        // click on Actions - edit task
        editTaskPage = currentTaskList.get(0).getEditTaskLink().click().render();

        // click on Task Done link
        editTaskPage.selectTaskDoneButton().render();

        // TODO: task disappeared from MyTasks list - not possible for the time being because of task name change after rejection (ALF-20546)

        // check the document is not marked with icon
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] testFolderPath = { USER_HOMES_FOLDER, testUser, testName };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);
        Assert.assertFalse(repositoryPage.getFileDirectoryInfo(sampleFile.getName()).isPartOfWorkflow(), "Verifying the file is not part of a workflow");

    }

    /**
     * Start any workflow - Cancel
     * 1) Create a site
     * 2) Upload file to the test folder
     * 3) Site creator starts the new workflow
     * 4) Cancel the new workflow
     * 5) Check the document is not marked with icon
     * 6) Check the workflow is not present in MyTasks - active tasks
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentWorkflows" })
    public void enterprise40x_5460() throws Exception
    {
        // create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, "Public");

        // Upload file to test folder
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);

        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // cancel the new workflow
        StartWorkFlowPage startWorkFlowPage = repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectStartWorkFlow().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
        formDetails.setMessage(workFlowName1);
        formDetails.setDueDate(dueDate);
        formDetails.setAssignee(username);
        formDetails.setTaskPriority(Priority.MEDIUM);
        repositoryPage = newWorkflowPage.cancelCreateWorkflow(formDetails).render();

        // check the document is not marked with icon
        assertFalse(repositoryPage.getFileDirectoryInfo(sampleFile.getName()).isPartOfWorkflow(), "Verifying the file is part of a workflow");

        // check the workflow is not present in MyTasks - active tasks
        MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);
        assertFalse(myTasksPage.isTaskPresent(workFlowName1));

    }

}
