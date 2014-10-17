package org.alfresco.share.sanity;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;
import static org.alfresco.po.share.site.document.DocumentAspect.EMAILED;
import static org.alfresco.po.share.site.document.DocumentAspect.VERSIONABLE;
import static org.testng.Assert.*;

/**
 * @author Olga Antonik
 */
@Listeners(FailedTestListener.class)
public class MyFilesTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(MyFilesTest.class);
    protected String testUser;
    protected String folderName;
    protected String testName;
    protected String fileName;
    public static long downloadWaitTime = 3000;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_8267() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        folderName = getFolderName(testName);
        fileName = getFileName(testName) + ".txt";
        String folderPath = REPO + SLASH + "Data Dictionary" + SLASH + "Node Templates";

        String file1 = getFileName(testName) + getRandomString(3) + ".txt";
        String plainFile = getRandomString(5) + ".txt";
        String xmlFile = getRandomString(5) + ".xml";
        String htmlFile = getRandomString(5) + ".html";
        String newFolder = getRandomString(5);

        /*
         * Preconditions
         * 1. Any user is created
         * 3. Any file template is uploaded to Data Dictionary > Node Templates
         * 4. Any folder template is uploaded to Data Dictionary > SpaceTemplates
         * 5. The user is logged in
         * 6. My Files is opened
         */

        // create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // admin logs in
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // navigate to the Share Repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone).render();

        // open Data Dictionary > Node Templates folder and upload template file to the Node Templates
        String[] fileInfo = { fileName, folderPath };
        RepositoryPage repositoryPage = ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo).render();

        new File(DATA_FOLDER + SLASH + fileName).delete();

        assertTrue(repositoryPage.isFileVisible(fileName), "File wasn't be uploaded to the Data Dictionary > Node Templates");

        // navigate to the Dictionary > SpaceTemplates
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + "Data Dictionary" + SLASH + "Space Templates").render();

        // create template folder
        repositoryPage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName).render();

        assertTrue(repositoryPage.isItemVisble(folderName), "Failed to create new folder template into the Data Dictionary > Space Templates");

        // user logs in
        DashBoardPage dashBoardPage = ShareUser.login(drone, testUserInfo).render();

        // open my files page
        MyFilesPage myFilesPage = dashBoardPage.getNav().selectMyFilesPage().render();
        assertTrue(myFilesPage.getTitle().contains("My Files"), "Couldn't navigate to My Files page");

        // ////////// Test ////////////

        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW).render(maxWaitTime);

        // click Upload, select some files and upload
        File newFileName1 = newFile(DATA_FOLDER + (file1), file1);
        myFilesPage = ShareUserSitePage.uploadFile(drone, newFileName1).render();

        FileUtils.forceDelete(newFileName1);

        assertTrue(myFilesPage.isFileVisible(newFileName1.getName()), "Uploaded file is not visible on the My Files page");

        // verify that Thumbnail is generated. Preview is correctly displayed on details page
        FileDirectoryInfo fileDirectoryInfo = myFilesPage.getFileDirectoryInfo(file1);
        assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty());

        DocumentDetailsPage detailsPage = myFilesPage.selectFile(file1).render();
        assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page");
        assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page");

        Map<ContentType, String> types = new HashMap<>();
        types.put(ContentType.PLAINTEXT, plainFile);
        types.put(ContentType.XML, xmlFile);
        types.put(ContentType.HTML, htmlFile);

        // create Plain Text, XML and HTML contents
        for (Map.Entry<ContentType, String> type : types.entrySet())
        {
            myFilesPage = detailsPage.getNav().selectMyFilesPage().render();
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(type.getValue());
            contentDetails.setContent(type.getValue());
            CreatePlainTextContentPage contentPage = myFilesPage.getNavigation().selectCreateContent(type.getKey()).render();
            detailsPage = contentPage.create(contentDetails).render();

            myFilesPage = detailsPage.getNav().selectMyFilesPage().render();

            fileDirectoryInfo = myFilesPage.getFileDirectoryInfo(type.getValue());
            assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty());

            detailsPage = myFilesPage.selectFile(type.getValue()).render();
            assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page for plain text file");
            if (!type.getKey().equals(ContentType.HTML))
                assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"),
                        "Preview isn't correctly displayed on details page for plain text file");
        }

        // create document from template
        myFilesPage = detailsPage.getNav().selectMyFilesPage().render();
        myFilesPage = myFilesPage.createContentFromTemplate(fileName).render();
        fileDirectoryInfo = myFilesPage.getFileDirectoryInfo(fileName);
        assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty());

        detailsPage = myFilesPage.selectFile(fileName).render();
        assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page for file created from template");
        assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"),
                "Preview isn't correctly displayed on details page for file created from template");

        // create folder
        myFilesPage = detailsPage.getNav().selectMyFilesPage().render();
        NewFolderPage newFolderPage = myFilesPage.getNavigation().selectCreateNewFolder().render();
        myFilesPage = newFolderPage.createNewFolder(newFolder).render();

        assertTrue(myFilesPage.isItemVisble(newFolder), "Folder wasn't be created");

        // create folder from template
        myFilesPage.createFolderFromTemplate(folderName).render(maxWaitTime);
        assertTrue(myFilesPage.isItemVisble(folderName), "Failed to create new folder from template");

        ShareUser.logout(drone);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_8268() throws Exception
    {
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        fileName = getFileName(testName) + ".txt";
        String fileName1 = getFileName(testName) + getRandomString(3) + ".txt";
        String tag = getTagName(testName);
        String copyFolder = "4copy";
        String moveFolder = "4move";

        /*
         * Preconditions:
         * 1. Two users are created
         * 2. The user1 is logged in
         * 3. Any document is uploaded/created in My Files
         * 6. My Files is opened
         */

        setupCustomDrone(WebDroneType.DownLoadDrone);

        // create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        DashBoardPage dashBoardPage = ShareUser.login(customDrone, testUserInfo).render();

        // open my files page
        MyFilesPage myFilesPage = dashBoardPage.getNav().selectMyFilesPage().render();
        assertTrue(myFilesPage.getTitle().contains("My Files"), "Couldn't navigate to My Files page");

        // upload files
        File newFileName = newFile(DATA_FOLDER + (fileName), fileName);
        myFilesPage = ShareUserSitePage.uploadFile(customDrone, newFileName).render();
        FileUtils.forceDelete(newFileName);

        // create two folders (for steps 15 and 16: for verification of Copy and Move actions)
        NewFolderPage newFolderPage;

        for (String folder : new String[] { copyFolder, moveFolder })
        {
            newFolderPage = myFilesPage.getNavigation().selectCreateNewFolder().render();
            myFilesPage = newFolderPage.createNewFolder(folder).render();

        }

        // ///////////////////////////////////// Test

        // mark the document as favorite
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        boolean beforeStatus = fileInfo.isFavourite();
        fileInfo.selectFavourite();
        webDriverWait(customDrone, 2000);

        myFilesPage.getNav().selectMyFilesPage().render();
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).isFavourite() != beforeStatus, "File wasn't be marked as favorite");

        // like / Unlike the content
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        beforeStatus = fileInfo.isLiked();
        fileInfo.selectLike();
        webDriverWait(customDrone, 2000);
        assertTrue(fileInfo.isLiked() != beforeStatus);
        if (fileInfo.isLiked())
            assertTrue(fileInfo.getLikeCount().equals("1"));

        else
            assertTrue(fileInfo.getLikeCount().equals("0"));

        // comment on the document
        ShareUserSharedFilesPage.addCommentToFile(customDrone, fileName, "test");
        myFilesPage.getNav().selectMyFilesPage().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        assertTrue(fileInfo.getCommentsCount() > 0, "Got comments count: " + fileInfo.getCommentsCount());

        // navigate back to My Files. Click Share link
        myFilesPage.getNav().selectMyFilesPage().render();
        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).clickShareLink().render();
        assertTrue(shareLinkPage.isEmailLinkPresent());
        assertTrue(shareLinkPage.isFaceBookLinkPresent());
        assertTrue(shareLinkPage.isTwitterLinkPresent());
        assertTrue(shareLinkPage.isGooglePlusLinkPresent());
        assertTrue(shareLinkPage.isViewLinkPresent());
        assertTrue(shareLinkPage.isUnShareLinkPresent());

        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        myFilesPage.getNav().selectMyFilesPage().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        assertTrue(fileInfo.isFileShared());

        // edit content offline
        //fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
//        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
//        fileInfo.selectEditOffline().render();
//
//        myFilesPage.getNav().selectMyFilesPage().render();
//        fileInfo =ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
//        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.");
//
//        // click View Original Document action for the document
//        DocumentDetailsPage documentDetailsPage = fileInfo.selectViewOriginalDocument().render(maxWaitTime);
//        assertTrue(documentDetailsPage.isLockedByYou());
//        assertTrue(documentDetailsPage.isViewWorkingCopyDisplayed());
//
//        // navigate to My Files and click Cancel Editing
//        myFilesPage.getNav().selectMyFilesPage().render();
//        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
//        webDriverWait(customDrone, 1000);
//        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
//        webDriverWait(customDrone, 1000);
//        fileInfo.selectCancelEditing();
//
//        myFilesPage.getNav().selectMyFilesPage().render();
//        webDriverWait(customDrone, 3000);
//        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
//        assertFalse(fileInfo.isEdited());

        // click Download from the document actions
        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        fileInfo.selectDownload();

        // Check the file is downloaded successfully
        myFilesPage.waitForFile(downloadDirectory + fileName);
        List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        assertTrue(extractedChildFilesOrFolders.contains(fileName));
        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);

        // click View in Browser
        webDriverWait(customDrone, 2000);
        String mainWindow = customDrone.getWindowHandle();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        webDriverWait(customDrone, 5000);
        fileInfo.selectViewInBrowser();
        String htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
        assertTrue(htmlSource.contains(fileName), "Document isn't opened in a browser");
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // click Edit Properties for content
        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        webDriverWait(customDrone, 5000);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        EditDocumentPropertiesPage editDocumentPropertiesPage = fileInfo.selectEditProperties().render();
        assertTrue(editDocumentPropertiesPage.isEditPropertiesPopupVisible());

        // click All Properties
        editDocumentPropertiesPage.selectAllProperties().render();

        // edit document
        String[] splitString = fileName.split("\\.");
        fileName = splitString[0] + "edit." + splitString[1];
        editDocumentPropertiesPage.setName(fileName);

        // add tag
        TagPage tagPage = editDocumentPropertiesPage.getTag().render();
        tagPage = tagPage.enterTagValue(tag).render();
        tagPage.clickOkButton().render();

        // save changes
        myFilesPage = editDocumentPropertiesPage.selectSaveWithValidation().render();

        assertTrue(myFilesPage.isFileVisible(fileName));
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).getTags().contains(tag.toLowerCase()));

        // upload New Version, e.g. minor
        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);

        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        String actualVersion = fileInfo.getVersionInfo();

        // go to the document details page
        DocumentDetailsPage documentDetailsPage = myFilesPage.selectFile(fileName).render();
        UpdateFilePage updatePage = documentDetailsPage.selectUploadNewVersion().render();
        updatePage.selectMinorVersionChange();
        newFileName = newFile(DATA_FOLDER + fileName1, fileName);
        updatePage.uploadFile(newFileName.getCanonicalPath());
        SitePage sitePage = updatePage.submit().render();
        documentDetailsPage = sitePage.render();
        FileUtils.forceDelete(newFileName);

        // verify version
        myFilesPage.getNav().selectMyFilesPage().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        String currentVersion = fileInfo.getVersionInfo();
        assertNotEquals(actualVersion, currentVersion);

        // edit content Inline (for txt, html,xml)
        myFilesPage.getNav().selectMyFilesPage().render();
        documentDetailsPage = myFilesPage.selectFile(fileName).render();
        InlineEditPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        EditTextDocumentPage editTextDocumentPage = ((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).render(maxWaitTime);

        splitString = fileName.split("\\.");
        fileName = splitString[0] + "inline." + splitString[1];
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);

        documentDetailsPage = editTextDocumentPage.save(contentDetails).render();

        myFilesPage.getNav().selectMyFilesPage().render(maxWaitTime);
        assertTrue(myFilesPage.isFileVisible(fileName));

        // copy the document to any place
        documentDetailsPage = myFilesPage.selectFile(fileName).render();
        CopyOrMoveContentPage copyOrMoveContentPage = documentDetailsPage.selectCopyTo().render();
        documentDetailsPage = copyOrMoveContentPage.selectPath(testUser, copyFolder).render().selectOkButton().render();
//        copyOrMoveContentPage.selectOkButton().render();

//        CopyOrMoveContentPage copyOrMoveContentPage = fileInfo.selectCopyTo().render();
//        copyOrMoveContentPage.selectPath(REPO, copyFolder).render().selectOkButton().render();
        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.navigateToFolder(customDrone, REPO + SLASH + copyFolder).render(maxWaitTime);
        webDriverWait(customDrone, 5000);
        assertTrue(myFilesPage.isFileVisible(fileName));

        // move the document to any place
        myFilesPage.getNav().selectMyFilesPage().render();

        documentDetailsPage = myFilesPage.selectFile(fileName).render();
        copyOrMoveContentPage = documentDetailsPage.selectMoveTo().render();
        documentDetailsPage = copyOrMoveContentPage.selectPath(testUser, moveFolder).render().selectOkButton().render();

//        fileInfo = myFilesPage.getFileDirectoryInfo(fileName);
//        copyOrMoveContentPage = fileInfo.selectMoveTo().render();
//        myFilesPage = copyOrMoveContentPage.selectPath(REPO, moveFolder).render().selectOkButton().render();
//        assertFalse(myFilesPage.isFileVisible(fileName));

        // Start Workflow for the document
        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.navigateToFolder(customDrone, REPO + SLASH + moveFolder).render(maxWaitTime);
        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        StartWorkFlowPage startWorkFlowPage = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).selectStartWorkFlow().render(maxWaitTime);
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render(maxWaitTime);
        String workFlowName1 = testName + System.currentTimeMillis() + "WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
        List<String> reviewers = new ArrayList<>();
        reviewers.add(testUser);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(moveFolder, moveFolder, reviewers);
        formDetails.setMessage(workFlowName1);
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        myFilesPage = newWorkflowPage.startWorkflow(formDetails).render();

        // check the document is marked with icon
        myFilesPage = (MyFilesPage) ShareUserSitePage.navigateToFolder(customDrone, REPO + SLASH + moveFolder).render(maxWaitTime);
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow");

        // delete document
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
        ConfirmDeletePage confirmDeletePage = fileInfo.selectDelete().render(maxWaitTime);
        myFilesPage = confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render(maxWaitTime);
        assertFalse(myFilesPage.isFileVisible(fileName));

        ShareUser.logout(customDrone);
        customDrone.quit();

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_8269() throws Exception
    {
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        folderName = getFolderName(testName);
        fileName = getFileName(testName) + ".txt";
        String copyFolder = "4copy";
        String moveFolder = "4move";
        String tag = getTagName(testName);

        /*
         * Preconditions:
         * 1. Two users are created
         * 2. Any folder is created in My Files
         * 3. The user1 is logged in
         * 4. My Files is opened
         */

        setupCustomDrone(WebDroneType.DownLoadDrone);

        // create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        DashBoardPage dashBoardPage = ShareUser.login(customDrone, testUserInfo).render();

        // open my files page
        MyFilesPage myFilesPage = dashBoardPage.getNav().selectMyFilesPage().render();
        assertTrue(myFilesPage.getTitle().contains("My Files"), "Couldn't navigate to My Files page");

        // create folders
        NewFolderPage newFolderPage;
        for (String folder : new String[] { folderName, copyFolder, moveFolder })
        {
            newFolderPage = myFilesPage.getNavigation().selectCreateNewFolder().render();
            myFilesPage = newFolderPage.createNewFolder(folder).render();
        }

        assertTrue(myFilesPage.isItemVisble(folderName), "Failed to create " + folderName + " folder in My Files page");

        // /////////////////////// Test

        // mark the folder as favorite
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
        boolean beforeStatus = fileInfo.isFavourite();
        fileInfo.selectFavourite();
        webDriverWait(customDrone, 2000);

        myFilesPage.getNav().selectMyFilesPage().render();
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName).isFavourite() != beforeStatus, "Folder wasn't be marked as favorite");

        // like / Unlike the folder
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
        beforeStatus = fileInfo.isLiked();
        fileInfo.selectLike();
        webDriverWait(customDrone, 2000);
        assertTrue(fileInfo.isLiked() != beforeStatus);
        if (fileInfo.isLiked())
            assertTrue(fileInfo.getLikeCount().equals("1"));

        else
            assertTrue(fileInfo.getLikeCount().equals("0"));

        // comment on the folder
        ShareUserSharedFilesPage.addCommentToFolder(customDrone, folderName, "test");
        myFilesPage.getNav().selectMyFilesPage().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
        assertTrue(fileInfo.getCommentsCount() > 0, "Got comments count: " + fileInfo.getCommentsCount());

        // click Download as Zip from the actions
        fileInfo.selectDownloadFolderAsZip();
        myFilesPage.waitForFile(downloadDirectory + folderName + ".zip");
        ShareUser.webDriverWait(customDrone, downloadWaitTime);
        // click View Details
        FolderDetailsPage folderDetailsPage = fileInfo.selectViewFolderDetails().render();
        assertTrue(folderDetailsPage.getTitle().contains("Folder Details"), "Failed to open Folder Details page");

        // navigate back to My Files. Edit Properties for folder and add any tag to it
        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        fileInfo = myFilesPage.getFileDirectoryInfo(folderName);
        EditDocumentPropertiesPage editDocumentPropertiesPage = fileInfo.selectEditProperties().render();
        assertTrue(editDocumentPropertiesPage.isEditPropertiesPopupVisible());

        folderName += "edit";
        editDocumentPropertiesPage.setName(folderName);

        // add tag
        TagPage tagPage = editDocumentPropertiesPage.getTag().render();
        tagPage = tagPage.enterTagValue(tag).render();
        tagPage.clickOkButton().render();

        // save changes
        myFilesPage = editDocumentPropertiesPage.selectSaveWithValidation().render();

        assertTrue(myFilesPage.isFileVisible(folderName));
        assertTrue(myFilesPage.getFileDirectoryInfo(folderName).getTags().contains(tag.toLowerCase()));

        // copy the folder to any place
        fileInfo = myFilesPage.getFileDirectoryInfo(folderName);
        folderDetailsPage = fileInfo.selectViewFolderDetails().render();
        CopyOrMoveContentPage copyOrMoveContentPage = folderDetailsPage.selectCopyTo().render();
        folderDetailsPage = copyOrMoveContentPage.selectPath(testUser, copyFolder).render().selectOkButton().render();

//        myFilesPage.getNav().selectMyFilesPage().render();
//        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW).render(maxWaitTime);
//        webDriverWait(drone, 5000);
//        fileInfo = myFilesPage.getFileDirectoryInfo(folderName);
//        CopyOrMoveContentPage copyOrMoveContentPage = fileInfo.selectCopyTo().render();
//        copyOrMoveContentPage.selectPath(REPO, copyFolder).render().selectOkButton().render();
        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.navigateToFolder(customDrone, REPO + SLASH + copyFolder).render(maxWaitTime);
        assertTrue(myFilesPage.isItemVisble(folderName));

        // move the document to any place
        myFilesPage.getNav().selectMyFilesPage().render(maxWaitTime);
        fileInfo = myFilesPage.getFileDirectoryInfo(folderName);

        folderDetailsPage = fileInfo.selectViewFolderDetails().render();
        copyOrMoveContentPage = folderDetailsPage.selectMoveTo().render();
        folderDetailsPage = copyOrMoveContentPage.selectPath(testUser, moveFolder).render().selectOkButton().render();

//        copyOrMoveContentPage = fileInfo.selectMoveTo().render(maxWaitTime);
//        myFilesPage = copyOrMoveContentPage.selectPath(REPO, moveFolder).render().selectOkButton().render();
//
//        assertFalse(myFilesPage.isItemVisble(folderName));

        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.navigateToFolder(customDrone, REPO + SLASH + moveFolder).render(maxWaitTime);
        assertTrue(myFilesPage.isItemVisble(folderName));

        // Manage Rules for the folder, create any rule. Execute the rule
        FolderRulesPage folderRulesPage = myFilesPage.getFileDirectoryInfo(folderName).selectManageRules().render(maxWaitTime);
        assertTrue(folderRulesPage.isPageCorrect(folderName), "Rule page isn't correct");

        // fill "Name" field with correct data
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("Rule Name");
        createRulePage.fillDescriptionField("Rule Description");

        // select "Inbound" value from "When" drop-down select control
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // select 'Specialize Type' action
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectAddAspect(DocumentAspect.SUMMARIZABLE.getValue());

        createRulePage.selectRunRuleInBackgroundCheckbox();

        // click "Create" button
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render(maxWaitTime);
        assertTrue(folderRulesPageWithRules.isPageCorrect(folderName), "Rule page with rule isn't correct");

        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.navigateToFolder(customDrone, REPO + SLASH + moveFolder + SLASH + folderName).render(maxWaitTime);

        File newFileName = newFile(DATA_FOLDER + (fileName), fileName);
        myFilesPage = ShareUserSitePage.uploadFile(customDrone, newFileName).render();

        FileUtils.forceDelete(newFileName);
        assertTrue(myFilesPage.isFileVisible(newFileName.getName()), "Uploaded file is not visible on the My Files page");

        DocumentDetailsPage documentDetailsPage = myFilesPage.selectFile(fileName).render();
        Map<String, Object> properties = documentDetailsPage.getProperties();
        assertEquals(properties.get("Summary"), "(None)", "Failed to add aspect");

        // Manage Permissions, make some changes and click Cancel
        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.navigateToFolder(customDrone, REPO + SLASH + moveFolder).render(maxWaitTime);
        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
        ManagePermissionsPage managePermissionsPage = fileInfo.selectManagePermission().render(maxWaitTime);
        webDriverWait(customDrone, 7000);
        assertTrue(managePermissionsPage.isInheritPermissionEnabled());

        managePermissionsPage = managePermissionsPage.toggleInheritPermission(false, ManagePermissionsPage.ButtonType.Yes).render();
        assertFalse(managePermissionsPage.isInheritPermissionEnabled());
        myFilesPage = managePermissionsPage.selectCancel().render();

        // navigate to Manage Permissions page and verify that changes wasn't be
        // saved
        managePermissionsPage = fileInfo.selectManagePermission().render(maxWaitTime);
        assertTrue(managePermissionsPage.isInheritPermissionEnabled());

        myFilesPage = managePermissionsPage.selectCancel().render();

        // Manage Aspects, add some aspect, remove some aspect
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
        SelectAspectsPage aspectsPage = fileInfo.selectManageAspects();
        List<DocumentAspect> aspects = new ArrayList<>();
        aspects.add(VERSIONABLE);
        aspects.add(EMAILED);
        aspectsPage = aspectsPage.add(aspects).render();

        assertTrue(aspectsPage.getSelectedAspects().contains(VERSIONABLE));
        assertTrue(aspectsPage.getSelectedAspects().contains(EMAILED));

        aspects.remove(VERSIONABLE);
        aspectsPage.remove(aspects);

        assertTrue(aspectsPage.getSelectedAspects().contains(VERSIONABLE));
        assertFalse(aspectsPage.getSelectedAspects().contains(EMAILED));

        myFilesPage = aspectsPage.clickApplyChanges().render(maxWaitTime);

        // delete the folder. Confirm deletion
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
        ConfirmDeletePage confirmDeletePage = fileInfo.selectDelete().render();
        myFilesPage = confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();
        assertFalse(myFilesPage.isItemVisble(folderName));

        ShareUser.logout(customDrone);
        customDrone.quit();
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_8270() throws Exception
    {
        testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + 1);
        String testUser2 = getUserNameFreeDomain(testName + 2);
        String fileName1 = getFileName(testName) + "1.txt";
        String fileName2 = getFileName(testName) + "2.txt";
        String fileName3 = getFileName(testName) + "3.txt";
        String fileName4 = getFileName(testName) + "4.txt";
        String folderName1 = getFolderName(testName + 1);
        String folderName2 = getFolderName(testName + 2);
        String folderName3 = getFolderName(testName + 3);
        String tag1 = testName + "tag1";
        String tag2 = testName + "tag2";
        String folderPath = REPO + SLASH + "User Homes";

        setupCustomDrone(WebDroneType.DownLoadDrone);

        // //////////////////////// Preconditions

        // create users
        String[] testUserInfo = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);
        testUserInfo = new String[] { testUser1 };
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // created user is logged in and navigate to My Files
        DashBoardPage dashBoardPage = ShareUser.login(customDrone, testUserInfo).render();

        // open my files page
        MyFilesPage myFilesPage = dashBoardPage.getNav().selectMyFilesPage().render();
        assertTrue(myFilesPage.getTitle().contains("My Files"), "Couldn't navigate to My Files page");

        // upload files
        File newFileName1 = newFile(DATA_FOLDER + (fileName1), fileName1);
        File newFileName2 = newFile(DATA_FOLDER + (fileName2), fileName2);
        File newFileName3 = newFile(DATA_FOLDER + (fileName3), fileName3);
        File newFileName4 = newFile(DATA_FOLDER + (fileName4), fileName4);

        File[] files = new File[] { newFileName1, newFileName2, newFileName3, newFileName4 };
        for (File file : files)
        {
            myFilesPage = ShareUserSitePage.uploadFile(customDrone, file).render();
            FileUtils.forceDelete(file);
        }

        for (File file : files)
            assertTrue(myFilesPage.isFileVisible(newFileName1.getName()), "Uploaded " + file.getName() + " file is not visible on the My Files page");

        String[] folders = new String[] { folderName1, folderName2, folderName3 };
        myFilesPage = myFilesPage.getNav().selectMyFilesPage().render();
        NewFolderPage newFolderPage;

        // create folders
        for (String folder : folders)
        {
            newFolderPage = myFilesPage.getNavigation().selectCreateNewFolder().render();
            myFilesPage = newFolderPage.createNewFolder(folder).render();
        }

        myFilesPage.getNav().selectMyFilesPage().render();

        for (String folder : folders)
            assertTrue(myFilesPage.isItemVisble(folderName1), "Failed to create " + folder + " folder in My Files page");

        // add permissions to user2
        // open Repository -> User Homes
        ShareUserRepositoryPage.navigateToFolderInRepository(customDrone, folderPath).render();
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, testUser1);
        fileInfo.selectViewFolderDetails().render();
        ShareUserMembers.managePermissionsOnContent(customDrone, testUser2, folderName3, UserRole.EDITOR, true);

        // one doc and folder with tag1
        myFilesPage.getNav().selectMyFilesPage().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.addTag(tag1);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName1);
        fileInfo.addTag(tag1);

        // one doc and folder with tag2
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName2);
        fileInfo.addTag(tag2);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName2);
        fileInfo.addTag(tag2);

        // one doc is being edited offline
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.selectEditOffline().render();

        myFilesPage.getNav().selectMyFilesPage().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.");

        // one doc is being edited offline by other user
        ShareUser.login(customDrone, testUser2, DEFAULT_PASSWORD);
        ShareUserRepositoryPage.navigateToFolderInRepository(customDrone, folderPath + SLASH + testUser1).render();

        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName3);

        fileInfo.selectEditOffline().render();
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName3).isEdited(), "The file is blocked for editing");

        ShareUserRepositoryPage.navigateToFolderInRepository(customDrone, folderPath + SLASH + testUser1).render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName3);
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.");

        // one doc recently modified, one doc recently added
        ShareUser.login(customDrone, testUser1, DEFAULT_PASSWORD);
        myFilesPage.getNav().selectMyFilesPage().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        DocumentDetailsPage documentDetailsPage = myFilesPage.selectFile(fileName2).render();
        EditDocumentPropertiesPage editDocumentPropertiesPage = documentDetailsPage.selectEditProperties().render(maxWaitTime);

        editDocumentPropertiesPage.setDescription(fileName2);
        documentDetailsPage = editDocumentPropertiesPage.selectSaveWithValidation().render();

        myFilesPage.getNav().selectMyFilesPage().render();

        // one doc and folder is marked as favourite by current user
        for (FileDirectoryInfo file : new FileDirectoryInfo[] { ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName2),
                ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName2) })
        {
            webDriverWait(customDrone, 1000);
            boolean beforeStatus = file.isFavourite();
            if (!beforeStatus)
                file.selectFavourite();
            webDriverWait(customDrone, 3000);
        }

        myFilesPage.getNav().selectMyFilesPage().render();
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName2).isFavourite(), "File wasn't be marked as favorite");
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName2).isFavourite(), "Folder wasn't be marked as favorite");

        // one doc and folder is favorite for another user
        ShareUser.login(customDrone, testUser2, DEFAULT_PASSWORD);
        ShareUserRepositoryPage.navigateToFolderInRepository(customDrone, folderPath + SLASH + testUser1).render();

        for (FileDirectoryInfo file : new FileDirectoryInfo[] { ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName4),
                ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName3) })
        {
            webDriverWait(customDrone, 1000);
            boolean beforeStatus = file.isFavourite();
            if (!beforeStatus)
                file.selectFavourite();
            webDriverWait(customDrone, 3000);

        }
        ShareUserRepositoryPage.navigateToFolderInRepository(customDrone, folderPath + SLASH + testUser1).render();
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName4).isFavourite(), "File wasn't be marked as favorite");
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName3).isFavourite(), "Folder wasn't be marked as favorite");

        // one doc and folder with category1, one doc and folder with category2
        ShareUser.login(customDrone, testUser1, DEFAULT_PASSWORD);
        myFilesPage.getNav().selectMyFilesPage().render();

        List<DocumentAspect> aspects = new ArrayList<>();
        aspects.add(CLASSIFIABLE);

        List<Categories> category1 = new ArrayList<>();
        category1.add(Categories.LANGUAGES);

        List<Categories> category2 = new ArrayList<>();
        category2.add(Categories.REGIONS);

        documentDetailsPage = myFilesPage.selectFile(fileName2).render();
        for (int i = 0; i < 2; i++)
        {
            if (i == 1)
                documentDetailsPage = myFilesPage.selectFile(fileName4).render();

            SelectAspectsPage aspectsPage = documentDetailsPage.selectManageAspects().render();

            aspectsPage.add(aspects);
            documentDetailsPage = aspectsPage.clickApplyChanges().render();

            documentDetailsPage = refreshSharePage(customDrone).render(maxWaitTime);
            webDriverWait(customDrone, 7000);
            editDocumentPropertiesPage = documentDetailsPage.selectEditProperties().render();
            CategoryPage categoryPage = editDocumentPropertiesPage.getCategory();

            if (i == 0)
                categoryPage.add(category1).render();
            else
                categoryPage.add(category2).render();

            categoryPage.clickOk();
            documentDetailsPage = editDocumentPropertiesPage.selectSave().render();

            if (i == 0)
                myFilesPage.getNav().selectMyFilesPage().render();
        }

        myFilesPage.getNav().selectMyFilesPage().render();

        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName2);
        for (int i = 0; i < 2; i++)
        {
            if (i == 1)
                fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName2);
            FolderDetailsPage folderDetailsPage = fileInfo.selectViewFolderDetails().render();
            SelectAspectsPage aspectsPage = folderDetailsPage.selectManageAspects().render();
            aspectsPage.add(aspects);
            folderDetailsPage = aspectsPage.clickApplyChanges().render();

            editDocumentPropertiesPage = folderDetailsPage.selectEditProperties().render();
            CategoryPage categoryPage = editDocumentPropertiesPage.getCategory();
            if (i == 0)
                categoryPage.add(category1).render();
            else
                categoryPage.add(category2).render();

            categoryPage.clickOk();
            folderDetailsPage = editDocumentPropertiesPage.selectSave().render();
            myFilesPage.getNav().selectMyFilesPage().render();
        }

        // ///////////////////////// Test
        myFilesPage.getNav().selectMyFilesPage().render();
        TreeMenuNavigation treeMenuNavigation = myFilesPage.getLeftMenus().render();
        myFilesPage = (MyFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        webDriverWait(customDrone, 7000);

        // and individual asserts for the remaining files.
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName1, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName2, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, true), fileName2
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName3, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, true), fileName3
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName4, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, true), fileName4
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, folderName1, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, false), folderName1
                + " should not be visible.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, folderName2, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, false), folderName2
                + " should not be visible.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, folderName3, TreeMenuNavigation.DocumentsMenu.ALL_DOCUMENTS, false), folderName3
                + " should not be visible.");

        // click I'm editing
        treeMenuNavigation = myFilesPage.getLeftMenus().render();
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.IM_EDITING).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName1, TreeMenuNavigation.DocumentsMenu.IM_EDITING, true), fileName1
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName3, TreeMenuNavigation.DocumentsMenu.IM_EDITING, false), fileName3
                + " should not be visible.");

        // click Others are Editing
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.OTHERS_EDITING).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName3, TreeMenuNavigation.DocumentsMenu.OTHERS_EDITING, true), fileName3
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName1, TreeMenuNavigation.DocumentsMenu.OTHERS_EDITING, false), fileName1
                + " should not be visible.");

        // click Recently Modified
        ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        webDriverWait(customDrone, 3000);
        treeMenuNavigation = myFilesPage.getLeftMenus().render();
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.RECENTLY_MODIFIED).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName2, TreeMenuNavigation.DocumentsMenu.RECENTLY_MODIFIED, true), fileName2
                + " cannot be found.");

        // click Recently Added
        ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        webDriverWait(customDrone, 3000);
        treeMenuNavigation = myFilesPage.getLeftMenus().render();
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED).render(maxWaitTime);
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName4, TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED, true), fileName4
                + " cannot be found.");

        // click My Favourites
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.MY_FAVORITES).render();
        webDriverWait(customDrone, 5000);
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName2, TreeMenuNavigation.DocumentsMenu.MY_FAVORITES, true), fileName2
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, folderName2, TreeMenuNavigation.DocumentsMenu.MY_FAVORITES, true), folderName2
                + " cannot be found.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName4, TreeMenuNavigation.DocumentsMenu.MY_FAVORITES, false), fileName4
                + " should not be visible.");
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, folderName3, TreeMenuNavigation.DocumentsMenu.MY_FAVORITES, false), folderName3
                + " should not be visible.");

        // click on any folder under My Files section
        treeMenuNavigation = myFilesPage.getLeftMenus().render();
        List<String> children = treeMenuNavigation.getNodeChildren(TreeMenuNavigation.TreeMenu.LIBRARY, "My Files");
        assertTrue(children.contains(folderName1));
        treeMenuNavigation.selectNode(TreeMenuNavigation.TreeMenu.LIBRARY, "My Files", folderName1).render();
        assertFalse(myFilesPage.isFileVisible(fileName1), fileName1 + " should not be visible.");
        assertFalse(myFilesPage.isFileVisible(fileName2), fileName2 + " should not be visible.");
        assertFalse(myFilesPage.isFileVisible(fileName3), fileName3 + " should not be visible.");
        assertFalse(myFilesPage.isFileVisible(fileName4), fileName4 + " should not be visible.");
        assertFalse(myFilesPage.isItemVisble(folderName2), folderName2 + " should not be visible.");
        assertFalse(myFilesPage.isItemVisble(folderName3), folderName3 + " should not be visible.");

        // click on <category1> under Categories section
        treeMenuNavigation = myFilesPage.getLeftMenus().render();
        myFilesPage = treeMenuNavigation.selectNode(TreeMenuNavigation.TreeMenu.CATEGORIES, customDrone.getValue(TreeMenuNavigation.CATEGORY_ROOT_PROPERTY),
                customDrone.getValue("category.languages")).render(maxWaitTime);
        webDriverWait(customDrone, 7000);
        assertTrue(myFilesPage.isFileVisible(fileName2), fileName2 + " cannot be found.");
        assertTrue(myFilesPage.isItemVisble(folderName2), folderName2 + " cannot be found.");
        assertFalse(myFilesPage.isFileVisible(fileName4), fileName4 + " should not be visible.");
        assertFalse(myFilesPage.isItemVisble(folderName3), folderName3 + " should not be visible.");

        // click on tag1 under Tags section
        myFilesPage = treeMenuNavigation.selectTagNode(tag2.toLowerCase()).render();
        assertTrue(myFilesPage.isFileVisible(fileName2), fileName2 + " cannot be found.");
        assertTrue(myFilesPage.isItemVisble(folderName2), folderName2 + " cannot be found.");
        assertFalse(myFilesPage.isFileVisible(fileName1), fileName1 + " should not be visible.");
        assertFalse(myFilesPage.isItemVisble(folderName1), folderName1 + " should not be visible.");

        ShareUser.logout(customDrone);
        customDrone.quit();
    }
}
