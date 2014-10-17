/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.sanity;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.alfresco.po.share.site.document.DocumentAspect.*;
import static org.testng.Assert.*;

/**
 * This class contains the sanity tests for shared files.
 * 
 * @author Antonik Olga
 */
@Listeners(FailedTestListener.class)
public class SharedFilesTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SharedFilesTest.class);
    protected String testUser;
    protected String folderName;
    protected String testName;
    protected String fileName;
    protected String file = "testFile";
    protected String folder = "testFolder";
    protected String user = "AONE_8275";
    public static long downloadWaitTime = 3000;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    /**
     * 1) Any user is created
     * 2) Any file template is uploaded to Data Dictionary > Node Templates
     * 3) Any folder template is uploaded to Data Dictionary > Space Templates
     * 4) The user is logged in
     * 5) Shared Files is opened
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepSanity" })
    public void dataPrep_AONE_15228() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        folderName = getFolderName(testName);
        fileName = getFileName(testName) + ".txt";
        String folderPath = REPO + SLASH + "Data Dictionary" + SLASH + "Node Templates";

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

        // TODO: Discuss: Remove asserts from dataprep methods. For sanity no separate dataprep is necessary
        assertTrue(repositoryPage.isFileVisible(fileName), "File wasn't be uploaded to the Data Dictionary > Node Templates");

        // navigate to the Dictionary > SpaceTemplates
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + "Data Dictionary" + SLASH + "Space Templates").render();

        // create template folder
        repositoryPage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName).render();

        assertTrue(repositoryPage.isItemVisble(folderName), "Failed to create new folder template into the Data Dictionary > Space Templates");

        // user logs in
        ShareUser.login(drone, testUserInfo);

        // open shared page
        SharedFilesPage sharedFilesTest = ShareUserSharedFilesPage.openSharedFiles(drone).render();

        assertTrue(sharedFilesTest.getTitle().contains("Shared Files"), "Couldn't navigate to Shared Files Page");

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_15228() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        folderName = getFolderName(testName);
        fileName = getFileName(testName) + ".txt";

        String file1 = getFileName(testName) + getRandomString(3) + ".txt";
        String plainFile = getRandomString(5) + ".txt";
        String xmlFile = getRandomString(5) + ".xml";
        String htmlFile = getRandomString(5) + ".html";
        String newFolder = getRandomString(5);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserSharedFilesPage.openSharedFiles(drone);
        SharedFilesPage sharedFilesPage = (SharedFilesPage) ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        assertTrue(sharedFilesPage.getTitle().contains("Shared Files"), "Failed to navigate to the Shared Files page");

        // click Upload, select some files and upload
        File newFileName1 = newFile(DATA_FOLDER + (file1), file1);
        sharedFilesPage = ShareUserSharedFilesPage.uploadFileInSharedFiles(drone, newFileName1);

        FileUtils.forceDelete(newFileName1);

        assertTrue(sharedFilesPage.isFileVisible(newFileName1.getName()), "Uploaded file is not visible on the Shared Files page");

        // verify that Thumbnail is generated. Preview is correctly displayed on details page
        FileDirectoryInfo fileDirectoryInfo = sharedFilesPage.getFileDirectoryInfo(file1);
        assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty());

        DocumentDetailsPage detailsPage = sharedFilesPage.selectFile(file1);
        assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page");
        assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page");

        // create Plain Text content
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(plainFile);
        contentDetails.setContent(plainFile);
        sharedFilesPage = ShareUserSharedFilesPage.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        fileDirectoryInfo = sharedFilesPage.getFileDirectoryInfo(plainFile);
        assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty());

        detailsPage = sharedFilesPage.selectFile(plainFile).render();
        assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page for plain text file");
        assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page for plain text file");

        // create XML content
        contentDetails = new ContentDetails();
        contentDetails.setName(xmlFile);
        contentDetails.setContent(xmlFile);
        sharedFilesPage = ShareUserSharedFilesPage.createContent(drone, contentDetails, ContentType.XML);

        fileDirectoryInfo = sharedFilesPage.getFileDirectoryInfo(xmlFile);
        assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty());

        detailsPage = sharedFilesPage.selectFile(xmlFile).render();
        assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page for XML file");
        assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page for XML file");

        // create HTML content
        contentDetails = new ContentDetails();
        contentDetails.setName(htmlFile);
        contentDetails.setContent(htmlFile);
        sharedFilesPage = ShareUserSharedFilesPage.createContent(drone, contentDetails, ContentType.HTML);

        fileDirectoryInfo = sharedFilesPage.getFileDirectoryInfo(htmlFile);
        assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty());

        detailsPage = sharedFilesPage.selectFile(htmlFile).render();
        assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page for HTML file");
        // assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"), "Preview isn't correctly displayed on details page for HTML file");

        // create document from template
        sharedFilesPage = ShareUserSharedFilesPage.createContentFromTemplate(drone, fileName);
        fileDirectoryInfo = sharedFilesPage.getFileDirectoryInfo(fileName);
        assertTrue(!fileDirectoryInfo.getPreViewUrl().isEmpty());

        detailsPage = sharedFilesPage.selectFile(fileName).render();
        assertTrue(detailsPage.isDocumentDetailsPage(), "Failed to open Document Details page for file created from template");
        assertTrue(detailsPage.getPreviewerClassName().equals("previewer PdfJs"),
                "Preview isn't correctly displayed on details page for file created from template");

        // create folder
        ShareUserSharedFilesPage.openSharedFiles(drone);
        sharedFilesPage = ShareUserSharedFilesPage.createNewFolder(drone, newFolder);
        assertTrue(sharedFilesPage.isItemVisble(newFolder), "Folder wasn't be created");

        // create folder from template
        sharedFilesPage = ShareUserSharedFilesPage.createFolderFromTemplate(drone, folderName);
        assertTrue(sharedFilesPage.isItemVisble(folderName), "Failed to create new folder from template");

        ShareUser.logout(drone);

    }

    /**
     * 1. Two users are created
     * 2. The user1 is logged in
     * 3. Any document is uploaded/created in Shared Files
     * 4. Shared Files is opened
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepSanity" })
    public void dataPrep_AONE_15229() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        fileName = getFileName(testName) + ".txt";

        // create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_15229() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        fileName = getFileName(testName) + System.currentTimeMillis();
        String tag = getRandomString(5);
        String folderForCopy = "4copy" + System.currentTimeMillis();
        String folderForMove = "4move" + System.currentTimeMillis();

        try
        {
            setupCustomDrone(WebDroneType.DownLoadDrone);

            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

            // navigate to Shared Files
            SharedFilesPage sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone);
            assertTrue(sharedFilesPage.getTitle().contains("Shared Files"), "Failed to navigate to the Shared Files page");

            // click Upload, select some files and upload
            File newFileName1 = newFile(DATA_FOLDER + (fileName + ".txt"), fileName);
            sharedFilesPage = ShareUserSharedFilesPage.uploadFileInSharedFiles(customDrone, newFileName1);

            FileUtils.forceDelete(newFileName1);

            assertTrue(sharedFilesPage.isFileVisible(newFileName1.getName()), "Uploaded file is not visible on the Shared Files page");

            // navigate to Shared Files
            sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
            assertTrue(sharedFilesPage.getTitle().contains("Shared Files"), "Failed to navigate to the Shared Files page");

            // mark the document as favorite
            FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName + ".txt");

            // Favorite icon and label
            boolean beforeStatus = fileInfo.isFavourite();
            fileInfo.selectFavourite();
            ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
            assertTrue(fileInfo.isFavourite() != beforeStatus, "File wasn't be marked as favorite");

            // like / Unlike the content
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName + ".txt");
            beforeStatus = fileInfo.isLiked();
            fileInfo.selectLike();
            assertTrue(fileInfo.isLiked() != beforeStatus);
            if (fileInfo.isLiked())
                assertTrue(fileInfo.getLikeCount().equals("1"));

            else
                assertTrue(fileInfo.getLikeCount().equals("0"));

            // comment on the document
            ShareUserSharedFilesPage.addCommentToFile(customDrone, fileName + ".txt", "test");
            ShareUserSharedFilesPage.openSharedFiles(customDrone).render();
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName + ".txt");
            assertTrue(fileInfo.getCommentsCount() > 0, "Got comments count: " + fileInfo.getCommentsCount());

            // click Share link
            ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName + ".txt").clickShareLink().render();
            assertTrue(shareLinkPage.isEmailLinkPresent());
            assertTrue(shareLinkPage.isFaceBookLinkPresent());
            assertTrue(shareLinkPage.isTwitterLinkPresent());
            assertTrue(shareLinkPage.isGooglePlusLinkPresent());
            assertTrue(shareLinkPage.isViewLinkPresent());

            sharedFilesPage = shareLinkPage.clickOnUnShareButton().render(maxWaitTime);

            assertFalse(sharedFilesPage.getFileDirectoryInfo(fileName + ".txt").isFileShared());

            sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);

            // click View in Browser
            String mainWindow = customDrone.getWindowHandle();
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName + ".txt");
            fileInfo.selectViewInBrowser();
            String htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
            assertTrue(htmlSource.contains(fileName), "Document isn't opened in a browser");
            customDrone.closeWindow();
            customDrone.switchToWindow(mainWindow);

            // click Download from the document actions
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName + ".txt");
            fileInfo.selectDownload();

            // Check the file is downloaded successfully
            sharedFilesPage.waitForFile(downloadDirectory + fileName + ".txt");
            List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
            assertTrue(extractedChildFilesOrFolders.contains(fileName + ".txt"));
            ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);

            ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);

            // click Edit Properties for content
            sharedFilesPage = (SharedFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName + ".txt");
            EditDocumentPropertiesPage editDocumentPropertiesPage = fileInfo.selectEditProperties().render();
            assertTrue(editDocumentPropertiesPage.isEditPropertiesPopupVisible());

            // click All Properties
            editDocumentPropertiesPage.selectAllProperties().render();

            // edit document
            fileName += "edited";
            editDocumentPropertiesPage.setName(fileName + ".txt");

            // add tag
            TagPage tagPage = editDocumentPropertiesPage.getTag().render();
            tagPage = tagPage.enterTagValue(tag).render();
            tagPage.clickOkButton();

            // save changes
            editDocumentPropertiesPage.selectSaveWithValidation().render();
            sharedFilesPage = (SharedFilesPage) customDrone.getCurrentPage();

            assertTrue(sharedFilesPage.isFileVisible(fileName + ".txt"));
            assertTrue(sharedFilesPage.getFileDirectoryInfo(fileName + ".txt").getTags().contains(tag.toLowerCase()));

            // upload New Version, e.g. minor
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName + ".txt");
            String actualVersion = fileInfo.getVersionInfo();

            UpdateFilePage updatePage = sharedFilesPage.getFileDirectoryInfo(fileName + ".txt").selectUploadNewVersion().render(maxWaitTime);
            updatePage.selectMinorVersionChange();
            File newFileName = newFile(DATA_FOLDER + (fileName + getRandomString(3) + ".txt"), fileName);
            updatePage.uploadFile(newFileName.getCanonicalPath());
            SitePage sitePage = updatePage.submit().render();
            sitePage.render();
            FileUtils.forceDelete(newFileName);

            // verify version
            sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone).render();
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName + ".txt");
            String currentVersion = fileInfo.getVersionInfo();
            assertNotEquals(actualVersion, currentVersion);

            // edit content Inline (for txt, html,xml)
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName + ".txt");
            InlineEditPage inlineEditPage = fileInfo.selectInlineEdit().render();
            EditTextDocumentPage editTextDocumentPage = ((EditTextDocumentPage) inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT)).render(maxWaitTime);

            fileName += "inline.txt";
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName);

            sharedFilesPage = editTextDocumentPage.save(contentDetails).render();

            assertTrue(sharedFilesPage.isFileVisible(fileName));
            sharedFilesPage = (SharedFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);

            // edit content offline
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName);
            fileInfo.selectEditOffline().render();

            sharedFilesPage.render();

            sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
            assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.");

            // click View Original Document action for the document
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName);
            DocumentDetailsPage documentDetailsPage = fileInfo.selectViewOriginalDocument().render(maxWaitTime);
            assertTrue(documentDetailsPage.isLockedByYou());
            assertTrue(documentDetailsPage.isViewWorkingCopyDisplayed());

            // navigate to Shared Files and click Cancel Editing
            sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName);
            fileInfo.selectCancelEditing();

            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName);
            assertFalse(fileInfo.isEdited());

            // copy the document to any place
            sharedFilesPage = ShareUserSharedFilesPage.createNewFolder(customDrone, folderForCopy);
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName);

            CopyOrMoveContentPage copyOrMoveContentPage = fileInfo.selectCopyTo().render();
            copyOrMoveContentPage.selectPath(REPO, folderForCopy).render().selectOkButton().render();
            ShareUserSharedFilesPage.openSharedFiles(customDrone).render();
            sharedFilesPage = ShareUserSharedFilesPage.navigateToFolderInSharedFiles(customDrone, REPO + SLASH + folderForCopy);
            assertTrue(sharedFilesPage.isFileVisible(fileName));

            // move the document to any place
            ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);

            ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
            sharedFilesPage = ShareUserSharedFilesPage.createNewFolder(customDrone, folderForMove).render();
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName);
            copyOrMoveContentPage = fileInfo.selectMoveTo().render();
            copyOrMoveContentPage.selectPath(REPO, folderForMove).render().selectOkButton().render();

            assertFalse(sharedFilesPage.isFileVisible(fileName));

            // navigate to space
            sharedFilesPage = ShareUserSharedFilesPage.navigateToFolderInSharedFiles(customDrone, REPO + SLASH + folderForMove).render(maxWaitTime);
            assertTrue(sharedFilesPage.isFileVisible(fileName));

            // Start Workflow for the document
            StartWorkFlowPage startWorkFlowPage = sharedFilesPage.getFileDirectoryInfo(fileName).selectStartWorkFlow().render(maxWaitTime);
            NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render(maxWaitTime);
            String workFlowName1 = testName + System.currentTimeMillis() + "WF";
            String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");
            List<String> reviewers = new ArrayList<>();
            reviewers.add(testUser);
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails(folderForMove, folderForMove, reviewers);
            formDetails.setMessage(workFlowName1);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            newWorkflowPage.startWorkflow(formDetails).render();

            // check the document is marked with icon
            sharedFilesPage = ShareUserSharedFilesPage.navigateToFolderInSharedFiles(customDrone, REPO + SLASH + folderForMove).render(maxWaitTime);
            assertTrue(sharedFilesPage.getFileDirectoryInfo(fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow");

            // delete document
            fileInfo = sharedFilesPage.getFileDirectoryInfo(fileName);
            ConfirmDeletePage confirmDeletePage = fileInfo.selectDelete().render(maxWaitTime);
            sharedFilesPage = confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render(maxWaitTime);
            assertFalse(sharedFilesPage.isFileVisible(fileName));
            ShareUser.logout(customDrone);
            customDrone.quit();

        }
        catch (Exception e)
        {
            ShareUser.logout(customDrone);
            customDrone.quit();
        }
    }

    /**
     * 1. Two users are created
     * 2. Any folder is created in Shared Files
     * 3. The user1 is logged in
     * 4. Shared Files is opened
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepSanity" })
    public void dataPrep_AONE_15230() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        folderName = testName + "_folder";

        // create user
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // created user is logged in
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // navigate to Shared Files
        SharedFilesPage sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(drone);
        assertTrue(sharedFilesPage.getTitle().contains("Shared Files"), "Failed to navigate to the Shared Files page");

        // create folder
        ShareUserSharedFilesPage.createNewFolder(drone, folderName);
        sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(drone);
        assertTrue(sharedFilesPage.isItemVisble(folderName), "Failed to create new folder in Shared Files page");
        ShareUser.logout(drone);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_15230() throws Exception
    {
        try
        {
            testName = getTestName();
            testUser = getUserNameFreeDomain(testName);
            folderName = testName + "_folder";
            fileName = getTestName() + ".txt";
            String tag = getRandomString(5);
            String folderForCopy = "4copyFolder";
            String folderForMove = "4moveFolder";

            setupCustomDrone(WebDroneType.DownLoadDrone);

            // created user logs in
            ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

            // navigate to Shared Files
            SharedFilesPage sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
            assertTrue(sharedFilesPage.getTitle().contains("Shared Files"), "Failed to navigate to the Shared Files page");

            // mark the folder as favorite
            FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);

            // Favorite icon and label
            boolean beforeStatus = fileInfo.isFavourite();
            fileInfo.selectFavourite();
            ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
            assertTrue(fileInfo.isFavourite() != beforeStatus, "Folder wasn't be marked as favorite");

            // like / Unlike the folder
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
            beforeStatus = fileInfo.isLiked();
            fileInfo.selectLike();
            assertTrue(fileInfo.isLiked() != beforeStatus);
            if (fileInfo.isLiked())
                assertTrue(fileInfo.getLikeCount().equals("1"));

            else
                assertTrue(fileInfo.getLikeCount().equals("0"));

            // comment on the folder
            ShareUserSharedFilesPage.addCommentToFolder(customDrone, folderName, "test");
            sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone);
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
            assertTrue(fileInfo.getCommentsCount() > 0, "Got comments count: " + fileInfo.getCommentsCount());

            // click Download as Zip from the actions
            fileInfo.selectDownloadFolderAsZip();
            sharedFilesPage.waitForFile(downloadDirectory + folderName + ".zip");

            // Getting the refreshed FileDirectoryInfo object.
            ShareUser.webDriverWait(customDrone, downloadWaitTime);
            ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);

            // click View Details
            FolderDetailsPage folderDetailsPage = fileInfo.selectViewFolderDetails().render(maxWaitTime);
            assertTrue(folderDetailsPage.getTitle().contains("Folder Details"), "Failed to open Folder Details page");

            // navigate back to Shared Files. Edit Properties for folder and add any
            // tag to it
            ShareUserSharedFilesPage.openSharedFiles(customDrone).render();
            ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
            EditDocumentPropertiesPage editDocumentPropertiesPage = fileInfo.selectEditProperties().render();
            assertTrue(editDocumentPropertiesPage.isEditPropertiesPopupVisible());

            // edit folder
            folderName += "edited";
            editDocumentPropertiesPage.setName(folderName);

            // add tag
            TagPage tagPage = editDocumentPropertiesPage.getTag().render(maxWaitTime);
            tagPage = tagPage.enterTagValue(tag).render();
            tagPage.clickOkButton();

            // save changes
            editDocumentPropertiesPage.selectSave().render(maxWaitTime);

            sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);

            assertTrue(sharedFilesPage.isItemVisble(folderName));
            assertTrue(sharedFilesPage.getFileDirectoryInfo(folderName).getTags().contains(tag.toLowerCase()));

            // copy the folder to any place
            ShareUserSharedFilesPage.createNewFolder(customDrone, folderForCopy).render(maxWaitTime);
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
            CopyOrMoveContentPage copyOrMoveContentPage = fileInfo.selectCopyTo().render();
            copyOrMoveContentPage.selectPath(REPO, folderForCopy).selectOkButton().render();
            sharedFilesPage = ShareUserSharedFilesPage.navigateToFolderInSharedFiles(customDrone, REPO + SLASH + folderForCopy);
            assertTrue(sharedFilesPage.isItemVisble(folderName));

            // move the folder to any place
            ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
            sharedFilesPage = ShareUserSharedFilesPage.createNewFolder(customDrone, folderForMove).render(maxWaitTime);
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
            copyOrMoveContentPage = fileInfo.selectMoveTo().render(maxWaitTime);
            copyOrMoveContentPage.selectPath(REPO, folderForMove).selectOkButton().render(maxWaitTime);

            assertFalse(sharedFilesPage.isItemVisble(folderName));

            sharedFilesPage = ShareUserSharedFilesPage.navigateToFolderInSharedFiles(customDrone, REPO + SLASH + folderForMove).render(maxWaitTime);
            assertTrue(sharedFilesPage.isItemVisble(folderName));

            // manage Rules for the folder, create any rule.
            FolderRulesPage folderRulesPage = sharedFilesPage.getFileDirectoryInfo(folderName).selectManageRules().render(maxWaitTime);
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

            ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
            ShareUserSharedFilesPage.navigateToFolderInSharedFiles(customDrone, REPO + SLASH + folderForMove + SLASH + folderName).render(
                    maxWaitTime);

            File newFileName = newFile(DATA_FOLDER + (fileName), fileName);
            sharedFilesPage = ShareUserSharedFilesPage.uploadFileInSharedFiles(customDrone, newFileName);

            FileUtils.forceDelete(newFileName);
            assertTrue(sharedFilesPage.isFileVisible(newFileName.getName()), "Uploaded file is not visible on the Shared Files page");

            DocumentDetailsPage documentDetailsPage = sharedFilesPage.selectFile(newFileName.getName()).render();
            Map<String, Object> properties = documentDetailsPage.getProperties();
            assertEquals(properties.get("Summary"), "(None)", "Failed to add aspect");

            // Manage Permissions, make some changes and click Cancel
            ShareUserSharedFilesPage.navigateToFolderInSharedFiles(customDrone, REPO + SLASH + folderForMove).render(maxWaitTime);
            ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
            ManagePermissionsPage managePermissionsPage = fileInfo.selectManagePermission().render(maxWaitTime);
            webDriverWait(customDrone, 7000);
            assertTrue(managePermissionsPage.isInheritPermissionEnabled());

            managePermissionsPage = managePermissionsPage.toggleInheritPermission(false, ManagePermissionsPage.ButtonType.Yes).render();
            assertFalse(managePermissionsPage.isInheritPermissionEnabled());
            managePermissionsPage.selectCancel().render();

            // navigate to Manage Permissions page and verify that changes wasn't be
            // saved
            managePermissionsPage = fileInfo.selectManagePermission().render(maxWaitTime);
            assertTrue(managePermissionsPage.isInheritPermissionEnabled());

            managePermissionsPage.selectCancel().render();

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

            aspectsPage.clickApplyChanges().render(maxWaitTime);

            // delete the folder. Confirm deletion
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName);
            ConfirmDeletePage confirmDeletePage = fileInfo.selectDelete().render();
            sharedFilesPage = confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();
            assertFalse(sharedFilesPage.isItemVisble(folderName));
            ShareUser.logout(customDrone);
            customDrone.quit();
        }
        catch (Exception e)
        {
            ShareUser.logout(customDrone);
            customDrone.quit();

        }

    }

    /**
     * 1) Two users are created
     * 2) Several documents and folders are created in Shared Files - at least:
     * - one doc and folder with tag1,one doc and folder with tag2, one doc and folder without tags
     * - one doc is being edited offline, one doc is being edited online, one doc is being edited offline by other user
     * - one doc recently modified, one doc recently added
     * - one doc and folder is marked as favourite by current user, one doc and folder is favorite for another user
     * - one doc and folder with category1, one doc and folder with category2,one doc and folder without any categories
     * 3) The user is logged in
     * 4) Shared Files page is opened
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepSanity" })
    public void dataPrep_AONE_15231() throws Exception
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

        setupCustomDrone(WebDroneType.DownLoadDrone);

        // create user
        String[] testUserInfo = new String[] { testUser1 };
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);
        testUserInfo = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // created user is logged in
        ShareUser.login(customDrone, testUser1, DEFAULT_PASSWORD);

        // navigate to Shared Files
        SharedFilesPage sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone);
        assertTrue(sharedFilesPage.getTitle().contains("Shared Files"), "Failed to navigate to the Shared Files page");

        // upload files
        File newFileName1 = newFile(DATA_FOLDER + (fileName1), fileName1);
        File newFileName2 = newFile(DATA_FOLDER + (fileName2), fileName2);
        File newFileName3 = newFile(DATA_FOLDER + (fileName3), fileName3);
        File newFileName4 = newFile(DATA_FOLDER + (fileName4), fileName4);

        ShareUserSharedFilesPage.uploadFileInSharedFiles(customDrone, newFileName1);
        ShareUserSharedFilesPage.uploadFileInSharedFiles(customDrone, newFileName2);
        ShareUserSharedFilesPage.uploadFileInSharedFiles(customDrone, newFileName3);
        sharedFilesPage = ShareUserSharedFilesPage.uploadFileInSharedFiles(customDrone, newFileName4);

        FileUtils.forceDelete(newFileName1);
        FileUtils.forceDelete(newFileName2);
        FileUtils.forceDelete(newFileName3);
        FileUtils.forceDelete(newFileName4);

        assertTrue(sharedFilesPage.isFileVisible(newFileName1.getName()), "Uploaded " + newFileName1.getName()
                + " file is not visible on the Shared Files page");
        assertTrue(sharedFilesPage.isFileVisible(newFileName2.getName()), "Uploaded " + newFileName2.getName()
                + " file is not visible on the Shared Files page");
        assertTrue(sharedFilesPage.isFileVisible(newFileName3.getName()), "Uploaded " + newFileName3.getName()
                + " file is not visible on the Shared Files page");
        assertTrue(sharedFilesPage.isFileVisible(newFileName4.getName()), "Uploaded " + newFileName4.getName()
                + " file is not visible on the Shared Files page");

        // create folders
        ShareUserSharedFilesPage.createNewFolder(customDrone, folderName1);
        ShareUserSharedFilesPage.createNewFolder(customDrone, folderName2);
        ShareUserSharedFilesPage.createNewFolder(customDrone, folderName3);

        sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
        assertTrue(sharedFilesPage.isItemVisble(folderName1), "Failed to create " + folderName1 + " folder in Shared Files page");
        assertTrue(sharedFilesPage.isItemVisble(folderName2), "Failed to create " + folderName2 + " folder in Shared Files page");
        assertTrue(sharedFilesPage.isItemVisble(folderName3), "Failed to create " + folderName3 + " folder in Shared Files page");

        // add permissions to user2
        sharedFilesPage.selectFile(fileName3).render(maxWaitTime);
        ShareUserMembers.managePermissionsOnContent(customDrone, testUser2, fileName3, UserRole.EDITOR, true);

        // one doc and folder with tag1
        ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
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

        ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.");

        // one doc is being edited online
        // TODO edit online

        // one doc is being edited offline by other user
        ShareUser.login(customDrone, testUser2, DEFAULT_PASSWORD);
        ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName3);

        fileInfo.selectEditOffline().render(maxWaitTime);
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName3).isEdited(), "The file is blocked for editing");

        ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName3);
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.");

        // one doc recently modified, one doc recently added
        ShareUser.login(customDrone, testUser1, DEFAULT_PASSWORD);
        ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
        sharedFilesPage = (SharedFilesPage) ShareUserSitePage.selectView(customDrone, ViewType.DETAILED_VIEW).render(maxWaitTime);
        DocumentDetailsPage documentDetailsPage = sharedFilesPage.selectFile(fileName2).render();
        EditDocumentPropertiesPage editDocumentPropertiesPage = documentDetailsPage.selectEditProperties().render(maxWaitTime);

        editDocumentPropertiesPage.setDescription(fileName2);
        editDocumentPropertiesPage.selectSaveWithValidation().render();

        ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);

        // one doc and folder is marked as favourite by current user
        for (FileDirectoryInfo file : new FileDirectoryInfo[] { ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName2),
                ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName2) })
        {
            boolean beforeStatus = file.isFavourite();
            if (!beforeStatus)
                file.selectFavourite();
            ShareUserSharedFilesPage.openSharedFiles(customDrone);
            assertTrue(file.isFavourite(), "File wasn't be marked as favorite");

        }

        // one doc and folder is favorite for another user
        ShareUser.login(customDrone, testUser2, DEFAULT_PASSWORD);
        ShareUserSharedFilesPage.openSharedFiles(customDrone);

        for (FileDirectoryInfo file : new FileDirectoryInfo[] { ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName4),
                ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName3) })
        {
            boolean beforeStatus = file.isFavourite();
            if (!beforeStatus)
                file.selectFavourite();
            ShareUserSharedFilesPage.openSharedFiles(customDrone).render();
            assertTrue(file.isFavourite(), "File wasn't be marked as favorite");

        }

        // one doc and folder with category1, one doc and folder with category2
        ShareUser.login(customDrone, testUser1, DEFAULT_PASSWORD);
        sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone).render();

        List<DocumentAspect> aspects = new ArrayList<>();
        aspects.add(CLASSIFIABLE);

        List<Categories> category1 = new ArrayList<>();
        category1.add(Categories.LANGUAGES);

        List<Categories> category2 = new ArrayList<>();
        category2.add(Categories.REGIONS);

        documentDetailsPage = sharedFilesPage.selectFile(fileName2).render();
        for (int i = 0; i < 2; i++)
        {
            if (i == 1)
                documentDetailsPage = sharedFilesPage.selectFile(fileName4).render();

            SelectAspectsPage aspectsPage = documentDetailsPage.selectManageAspects().render();

            aspectsPage.add(aspects);
            aspectsPage.clickApplyChanges().render();

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
                sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone);
        }

        ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);

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
            editDocumentPropertiesPage.selectSave().render();
            ShareUserSharedFilesPage.openSharedFiles(customDrone);
        }

        ShareUser.logout(customDrone);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_15231() throws Exception
    {
        testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + 1);
        String fileName1 = getFileName(testName) + "1.txt";
        String fileName2 = getFileName(testName) + "2.txt";
        String fileName3 = getFileName(testName) + "3.txt";
        String fileName4 = getFileName(testName) + "4.txt";
        String folderName1 = getFolderName(testName + 1);
        String folderName2 = getFolderName(testName + 2);
        String folderName3 = getFolderName(testName + 3);
        String tag1 = testName + "tag1";

        setupCustomDrone(WebDroneType.DownLoadDrone);

        // click All Documents
        ShareUser.login(customDrone, testUser1, DEFAULT_PASSWORD);
        SharedFilesPage sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(customDrone).render(maxWaitTime);
        assertTrue(sharedFilesPage.getTitle().contains("Shared Files"));

        sharedFilesPage.getLeftMenus().render(maxWaitTime);
        webDriverWait(customDrone, 10000);

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
        TreeMenuNavigation treeMenuNavigation = sharedFilesPage.getLeftMenus();
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
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.RECENTLY_MODIFIED).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(customDrone, fileName2, TreeMenuNavigation.DocumentsMenu.RECENTLY_MODIFIED, true), fileName2
                + " cannot be found.");

        // click Recently Added
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.RECENTLY_ADDED).render();
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

        // click on any folder under Shared Files section
        treeMenuNavigation = sharedFilesPage.getLeftMenus();
        List<String> children = treeMenuNavigation.getNodeChildren(TreeMenuNavigation.TreeMenu.LIBRARY,
                customDrone.getValue(TreeMenuNavigation.SHARED_FILES_ROOT_PROPERTY));
        assertTrue(children.contains(folderName1));
        treeMenuNavigation.selectNode(TreeMenuNavigation.TreeMenu.LIBRARY, customDrone.getValue(TreeMenuNavigation.SHARED_FILES_ROOT_PROPERTY), folderName1)
                .render();
        assertFalse(sharedFilesPage.isFileVisible(fileName1), fileName1 + " should not be visible.");
        assertFalse(sharedFilesPage.isFileVisible(fileName2), fileName2 + " should not be visible.");
        assertFalse(sharedFilesPage.isFileVisible(fileName3), fileName3 + " should not be visible.");
        assertFalse(sharedFilesPage.isFileVisible(fileName4), fileName4 + " should not be visible.");
        assertFalse(sharedFilesPage.isItemVisble(folderName2), folderName2 + " should not be visible.");
        assertFalse(sharedFilesPage.isItemVisble(folderName3), folderName3 + " should not be visible.");

        // click on <category1> under Categories section
        treeMenuNavigation = sharedFilesPage.getLeftMenus();
        sharedFilesPage = treeMenuNavigation.selectNode(TreeMenuNavigation.TreeMenu.CATEGORIES,
                customDrone.getValue(TreeMenuNavigation.CATEGORY_ROOT_PROPERTY), customDrone.getValue("category.languages")).render(maxWaitTime);
        webDriverWait(customDrone, 7000);
        assertTrue(sharedFilesPage.isFileVisible(fileName2), fileName2 + " cannot be found.");
        assertTrue(sharedFilesPage.isItemVisble(folderName2), folderName2 + " cannot be found.");
        assertFalse(sharedFilesPage.isFileVisible(fileName4), fileName4 + " should not be visible.");
        assertFalse(sharedFilesPage.isItemVisble(folderName3), folderName3 + " should not be visible.");

        // click on tag1 under Tags section
        sharedFilesPage = treeMenuNavigation.selectTagNode(tag1.toLowerCase()).render();
        assertTrue(sharedFilesPage.isFileVisible(fileName1), fileName1 + " cannot be found.");
        assertTrue(sharedFilesPage.isItemVisble(folderName1), folderName1 + " cannot be found.");
        assertFalse(sharedFilesPage.isFileVisible(fileName2), fileName2 + " should not be visible.");
        assertFalse(sharedFilesPage.isItemVisble(folderName2), folderName2 + " should not be visible.");

    }

    /**
     * 1) Admin user has created Folder1 > Document1, Folder2, Document2 in Shared Files
     * 2) At least 5 users are created
     * 3) Manage Permissions for Folder1:
     * - User1 - coordinator
     * - User2 - collaborator
     * - User3 - contributor
     * - User4 - consumer
     * - User5 - editor
     * 4) User1 is logged in
     * 5) Shared Files is opened
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepSanity" })
    public void dataPrep_AONE_15232() throws Exception
    {
        testName = getTestName();
        String testUser1 = getUserNameFreeDomain(user + 1);
        String testUser2 = getUserNameFreeDomain(user + 2);
        String testUser3 = getUserNameFreeDomain(user + 3);
        String testUser4 = getUserNameFreeDomain(user + 4);
        String testUser5 = getUserNameFreeDomain(user + 5);

        String fileName1 = file + "1.txt";
        String fileName2 = file + "2.txt";
        String folderName1 = folder + 1;
        String folderName2 = folder + 2;

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserSharedFilesPage.openSharedFiles(drone);

        // create folder1
        SharedFilesPage sharedFilesPage = ShareUserSharedFilesPage.createNewFolder(drone, folderName1).render(maxWaitTime);
        assertTrue(sharedFilesPage.isItemVisble(folderName1));

        // navigate to the folder1
        ShareUserSharedFilesPage.navigateToFolderInSharedFiles(drone, REPO + SLASH + folderName1);

        // create document1
        File newFileName1 = newFile(DATA_FOLDER + (fileName1), fileName1);
        sharedFilesPage = ShareUserSharedFilesPage.uploadFileInSharedFiles(drone, newFileName1).render();
        assertTrue(sharedFilesPage.isItemVisble(fileName1));

        // create folder2
        ShareUserSharedFilesPage.openSharedFiles(drone);
        sharedFilesPage = ShareUserSharedFilesPage.createNewFolder(drone, folderName2);
        assertTrue(sharedFilesPage.isItemVisble(folderName2));

        File newFileName2 = newFile(DATA_FOLDER + (fileName2), fileName2);
        sharedFilesPage = ShareUserSharedFilesPage.uploadFileInSharedFiles(drone, newFileName2).render();
        assertTrue(sharedFilesPage.isFileVisible(fileName2));

        // delete files
        FileUtils.forceDelete(newFileName1);
        FileUtils.forceDelete(newFileName2);

        // create 5 users
        String[] users = new String[] { testUser1, testUser2, testUser3, testUser4, testUser5 };

        for (String user : users)
        {
            String[] testUserInfo = new String[] { user };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        }

        // open Manage Permissions for Folder1
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(drone);

        assertTrue(sharedFilesPage.isItemVisble(folderName1));

        // User1 - coordinator; User2 - collaborator; User3 - contributor; User4 - consumer; User5 - editor
        UserRole[] userRoles = new UserRole[] { UserRole.COORDINATOR, UserRole.COLLABORATOR, UserRole.CONTRIBUTOR, UserRole.CONSUMER, UserRole.EDITOR };

        for (int i = 0; i < 5; i++)
            ShareUserMembers.managePermissionsOnContent(drone, users[i], folderName1, userRoles[i], true).render(maxWaitTime);

        // User1 is logged in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Shared Files is opened
        ShareUserSharedFilesPage.openSharedFiles(drone);

        ShareUser.logout(drone);

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_15232() throws Exception
    {
        testName = getTestName();
        String testUser1 = getUserNameFreeDomain(user + 1);
        String testUser2 = getUserNameFreeDomain(user + 2);
        String testUser3 = getUserNameFreeDomain(user + 3);
        String testUser4 = getUserNameFreeDomain(user + 4);
        String testUser5 = getUserNameFreeDomain(user + 5);

        String fileName1 = file + "1.txt";
        String fileName2 = file + "2.txt";
        String folderName1 = folder + 1;
        String folderName2 = folder + 2;

        String folderPath = REPO + SLASH + folderName1;
        String comment = UserRole.COORDINATOR.toString().toLowerCase();

        // User1 is logged in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Shared Files is opened
        SharedFilesPage sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(drone).render(maxWaitTime);

        // verify the actions available for Folder2
        assertTrue(sharedFilesPage.isItemVisble(folderName2));
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName2);

        FolderDetailsPage folderDetailsPage = fileInfo.selectViewFolderDetails().render();

        List<String> folderActionsList = folderDetailsPage.getFolderActionList();
        HashSet<String> actualActionSet = new HashSet<>(folderActionsList);

        HashSet<String> expectedActionSet = new HashSet<>();

        expectedActionSet.add("Download as Zip");
        expectedActionSet.add("Copy to...");
        expectedActionSet.add("View in Alfresco Explorer");

        assertTrue(expectedActionSet.containsAll(actualActionSet));

        // verify the actions available for Document2
        sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(drone);

        DocumentDetailsPage documentDetailsPage = sharedFilesPage.selectFile(fileName2);
        List<String> documentActionsList = documentDetailsPage.getDocumentActionList();
        actualActionSet.clear();
        actualActionSet = new HashSet<>(documentActionsList);
        expectedActionSet.clear();
        expectedActionSet = new HashSet<>();

        expectedActionSet.add("Download");
        expectedActionSet.add("View In Browser");
        expectedActionSet.add("Copy to...");
        expectedActionSet.add("Start Workflow");
        expectedActionSet.add("Publish");

        assertTrue(expectedActionSet.containsAll(actualActionSet));

        // verify the actions available for Folder1
        ShareUserSharedFilesPage.openSharedFiles(drone);

        fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1);
        folderDetailsPage = fileInfo.selectViewFolderDetails().render();
        folderActionsList.clear();
        folderActionsList = folderDetailsPage.getFolderActionList();

        actualActionSet.clear();
        actualActionSet = new HashSet<>(folderActionsList);
        expectedActionSet.clear();
        expectedActionSet = new HashSet<>();

        expectedActionSet.add("Download as Zip");
        expectedActionSet.add("Edit Properties");
        expectedActionSet.add("Copy to...");
        expectedActionSet.add("Move to...");
        expectedActionSet.add("Manage Rules");
        expectedActionSet.add("Delete Folder");
        expectedActionSet.add("Manage Permissions");
        expectedActionSet.add("Manage Aspects");
        expectedActionSet.add("Change Type");
        expectedActionSet.add("View in Alfresco Explorer");

        assertTrue(expectedActionSet.containsAll(actualActionSet));

        // verify the actions available for Document1
        sharedFilesPage = ShareUserSharedFilesPage.navigateToFolderInSharedFiles(drone, folderPath).render();

        documentDetailsPage = sharedFilesPage.selectFile(fileName1);
        documentActionsList.clear();
        documentActionsList = documentDetailsPage.getDocumentActionList();

        actualActionSet.clear();
        actualActionSet = new HashSet<>(documentActionsList);
        expectedActionSet.clear();
        expectedActionSet = new HashSet<>();

        expectedActionSet.add("Download");
        expectedActionSet.add("View In Browser");
        expectedActionSet.add("Edit Properties");
        expectedActionSet.add("Upload New Version");
        expectedActionSet.add("Inline Edit");
        expectedActionSet.add("Edit Offline");
        expectedActionSet.add("Copy to...");
        expectedActionSet.add("Move to...");
        expectedActionSet.add("Edit in Google Docs™");
        expectedActionSet.add("Delete Document");
        expectedActionSet.add("Start Workflow");
        expectedActionSet.add("Manage Permissions");
        expectedActionSet.add("Manage Aspects");
        expectedActionSet.add("Change Type");
        expectedActionSet.add("Publish");

        assertTrue(expectedActionSet.containsAll(actualActionSet));

        // create any folder and any content, e.g. CoordFolder, CoordContent.txt
        String newFolder = "CoordFolder";
        String newFile = "CoordContent.txt";

        ShareUserSharedFilesPage.createNewFolderInPath(drone, newFolder, folderPath);
        File newFileName1 = newFile(DATA_FOLDER + (newFile), newFile);
        sharedFilesPage = ShareUserSharedFilesPage.uploadFileInSharedFiles(drone, newFileName1);

        assertTrue(sharedFilesPage.isItemVisble(newFolder));
        assertTrue(sharedFilesPage.isFileVisible(newFile));

        FileUtils.forceDelete(newFileName1);

        // add a comment to the Folder1 and Document1
        ShareUserSharedFilesPage.openSharedFiles(drone);
        ShareUserSharedFilesPage.addCommentToFolder(drone, folderName1, comment);

        ShareUserSharedFilesPage.openSharedFiles(drone).render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1);
        assertTrue(fileInfo.getCommentsCount() > 0, "Got comments count: " + fileInfo.getCommentsCount());

        // navigate to folder1 and add comment to document1
        ShareUserSharedFilesPage.navigateToFolderInSharedFiles(drone, folderPath);
        ShareUserSharedFilesPage.addCommentToFile(drone, fileName1, comment);

        ShareUserSharedFilesPage.navigateToFolderInSharedFiles(drone, folderPath);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);
        assertTrue(fileInfo.getCommentsCount() > 0, "Got comments count: " + fileInfo.getCommentsCount());

        // log in as User2. Navigate to Shared Files
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        for (int i = 0; i < 4; i++)
        {
            if (i == 0)
            {
                // create any folder and content in Folder1, e.g. CollabFolder, CollabContent.txt
                comment = UserRole.COLLABORATOR.toString().toLowerCase();
                newFolder = "CollabFolder";
                newFile = "CollabContent.txt";
            }
            if (i == 1)
            {
                // log in as User3. Navigate to Shared Files
                ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);
                // create any folder and content in Folder1, e.g. ContribFolder, ContribContent.txt
                comment = UserRole.CONTRIBUTOR.toString().toLowerCase();
                newFolder = "ContribFolder";
                newFile = "ContribContent.txt";

            }
            if (i == 2)
            {
                // log in as User4. Navigate to Shared Files
                // create any folder and content in Folder1, e.g. ConsumerFolder, ConsumerContent.txt
                ShareUser.login(drone, testUser4, DEFAULT_PASSWORD);
                comment = UserRole.CONSUMER.toString().toLowerCase();
                newFolder = "ConsumerFolder";
                newFile = "ConsumerContent.txt";
            }
            if (i == 3)
            {
                // log in as User5. Navigate to Shared Files
                ShareUser.login(drone, testUser5, DEFAULT_PASSWORD);
                // create any folder and content in Folder1, e.g. EditorFolder, EditorContent.txt
                comment = UserRole.EDITOR.toString().toLowerCase();
                newFolder = "EditorFolder";
                newFile = "EditorContent.txt";

            }

            // Shared Files is opened
            ShareUserSharedFilesPage.openSharedFiles(drone).render(maxWaitTime);

            // verify the actions available for Folder1
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1);

            folderDetailsPage = fileInfo.selectViewFolderDetails().render();

            folderActionsList.clear();
            folderActionsList = folderDetailsPage.getFolderActionList();
            actualActionSet.clear();
            actualActionSet = new HashSet<>(folderActionsList);

            expectedActionSet.clear();
            expectedActionSet = new HashSet<>();

            // if logged in Contributor or Consumer
            if (i == 1 || i == 2)
            {
                expectedActionSet.add("Download as Zip");
                expectedActionSet.add("Copy to...");
                expectedActionSet.add("View in Alfresco Explorer");

            }
            else
            {
                expectedActionSet.add("Download as Zip");
                expectedActionSet.add("Edit Properties");
                expectedActionSet.add("Copy to...");
                expectedActionSet.add("Manage Aspects");
                expectedActionSet.add("Change Type");
                expectedActionSet.add("View in Alfresco Explorer");

            }

            assertTrue(expectedActionSet.containsAll(actualActionSet));

            // verify the actions available for Document1
            sharedFilesPage = ShareUserSharedFilesPage.navigateToFolderInSharedFiles(drone, folderPath).render(maxWaitTime);

            documentDetailsPage = sharedFilesPage.selectFile(fileName1).render();
            documentActionsList.clear();
            documentActionsList = documentDetailsPage.getDocumentActionList();

            actualActionSet.clear();
            actualActionSet = new HashSet<>(documentActionsList);
            expectedActionSet.clear();
            expectedActionSet = new HashSet<>();

            // if logged in Contributor or Consumer
            if (i == 1 || i == 2)
            {
                expectedActionSet.add("Download");
                expectedActionSet.add("View In Browser");
                expectedActionSet.add("Copy to...");
                expectedActionSet.add("Start Workflow");
                expectedActionSet.add("Publish");
            }
            else
            {
                expectedActionSet.add("Download");
                expectedActionSet.add("View In Browser");
                expectedActionSet.add("Edit Properties");
                expectedActionSet.add("Upload New Version");
                expectedActionSet.add("Inline Edit");
                expectedActionSet.add("Edit Offline");
                expectedActionSet.add("Copy to...");
                expectedActionSet.add("Edit in Google Docs™");
                expectedActionSet.add("Start Workflow");
                expectedActionSet.add("Manage Aspects");
                expectedActionSet.add("Change Type");
                expectedActionSet.add("Publish");

            }

            assertTrue(expectedActionSet.containsAll(actualActionSet));

            // create new folder and new file
            ShareUserSharedFilesPage.createNewFolderInPath(drone, newFolder, folderPath);
            newFileName1 = newFile(DATA_FOLDER + (newFile), newFile);
            sharedFilesPage = ShareUserSharedFilesPage.uploadFileInSharedFiles(drone, newFileName1).render();
            assertTrue(sharedFilesPage.isItemVisble(newFolder));
            assertTrue(sharedFilesPage.isFileVisible(newFile));

            FileUtils.forceDelete(newFileName1);

            // add a comment to the Folder1 and Document1
            ShareUserSharedFilesPage.openSharedFiles(drone).render(maxWaitTime);
            ShareUserSharedFilesPage.addCommentToFolder(drone, folderName1, comment);
            ShareUserSharedFilesPage.openSharedFiles(drone);
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1);
            assertTrue(fileInfo.getCommentsCount() > 0, "Got comments count: " + fileInfo.getCommentsCount());

            // navigate to folder1 and add comment to document1
            ShareUserSharedFilesPage.navigateToFolderInSharedFiles(drone, folderPath);

            ShareUserSharedFilesPage.addCommentToFile(drone, fileName1, comment);
            ShareUserSharedFilesPage.navigateToFolderInSharedFiles(drone, folderPath);
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);
            assertTrue(fileInfo.getCommentsCount() > 0, "Got comments count: " + fileInfo.getCommentsCount());

        }

        ShareUser.logout(drone);

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" }, alwaysRun = true, dependsOnMethods = { "AONE_15232" })
    public void AONE_15233() throws Exception
    {
        testName = getTestName();
        String testUser1 = getUserNameFreeDomain(user + 1);
        String testUser2 = getUserNameFreeDomain(user + 2);
        String testUser3 = getUserNameFreeDomain(user + 3);
        String testUser4 = getUserNameFreeDomain(user + 4);
        String testUser5 = getUserNameFreeDomain(user + 5);
        String[] users = new String[] { testUser1, testUser2, testUser3, testUser4, testUser5 };

        String fileName1 = file + "1.txt";
        String folderName1 = folder + 1;

        String folderPath = REPO + SLASH + folderName1;
        String commentColl = UserRole.COLLABORATOR.toString().toLowerCase();
        String commentCoord = UserRole.COORDINATOR.toString().toLowerCase();
        String commentCont = UserRole.CONTRIBUTOR.toString().toLowerCase();

        for (int i = 0; i < 5; i++)
        {
            // user1 is logged in
            ShareUser.login(drone, users[i], DEFAULT_PASSWORD);

            // navigate to the Shared Files
            ShareUserSharedFilesPage.openSharedFiles(drone).render(maxWaitTime);

            // select Edit Properties of Folder1
            FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1);
            FolderDetailsPage folderDetailsPage = fileInfo.selectViewFolderDetails().render(maxWaitTime);
            List<String> folderActions = folderDetailsPage.getFolderActionList();

            // if user3 (contributor) or user4 (consumer) logged in
            if (i == 2 || i == 3)
            {
                assertFalse(folderActions.contains("Edit Properties"));
            }
            else
            {
                assertTrue(folderActions.contains("Edit Properties"));
                EditDocumentPropertiesPage editDocumentPropertiesPage = folderDetailsPage.selectEditProperties().render(maxWaitTime);

                // edit folder
                String folderDesc = folderName1 + users[i];
                editDocumentPropertiesPage.setDescription(folderDesc);

                // save changes
                editDocumentPropertiesPage.selectSave().render();

                // verify that folder1's properties was changed
                ShareUserSharedFilesPage.openSharedFiles(drone);
                fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1);

                assertTrue(fileInfo.getDescription().equals(folderDesc));

            }

            // edit Document1
            SharedFilesPage sharedFilesPage = ShareUserSharedFilesPage.navigateToFolderInSharedFiles(drone, folderPath).render(maxWaitTime);
            DocumentDetailsPage documentDetailsPage = sharedFilesPage.selectFile(fileName1).render(maxWaitTime);
            List<String> actionsList = documentDetailsPage.getDocumentActionList();

            // if user3 (contributor) or user4 (consumer) logged in
            if (i == 2 || i == 3)
            {
                assertFalse(actionsList.contains("Edit Properties"));
            }
            else
            {
                assertTrue(actionsList.contains("Edit Properties"));
                EditDocumentPropertiesPage editDocumentPropertiesPage = documentDetailsPage.selectEditProperties().render(maxWaitTime);

                // edit document1
                String documentDesc = fileName1 + users[i];
                editDocumentPropertiesPage.setDescription(documentDesc);

                // save changes
                documentDetailsPage = editDocumentPropertiesPage.selectSave().render(maxWaitTime);

                // verify that document1's properties was changed
                sharedFilesPage = ShareUserSharedFilesPage.navigateToFolderInSharedFiles(drone, folderPath).render(maxWaitTime);
                fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);
                assertTrue(fileInfo.getDescription().equals(documentDesc));
            }

            // edit collaborator's comment to the Folder1 and Document1
            if (i != 2 && i != 3)
                documentDetailsPage = sharedFilesPage.selectFile(fileName1).render(maxWaitTime);

            // if logs user2 (collaborator) or user3 (contributor) or user4 (consumer)
            if (i == 1 || i == 2 || i == 3)
            {
                // try to edit comment of coordinator
                assertFalse(documentDetailsPage.isEditCommentButtonPresent(commentCoord));
            }
            else if (i == 0)
            {
                documentDetailsPage.editComment(commentColl, commentColl + 1);
                documentDetailsPage.saveEditComments().render(maxWaitTime);

                List<String> comments = documentDetailsPage.getComments();
                assertTrue(comments.contains(commentColl + 1));
            }
            // if editor logged in (user5)
            else if (i == 4)
                // try to edit comment of contributor
                assertFalse(documentDetailsPage.isEditCommentButtonPresent(commentCont));

            ShareUserSharedFilesPage.openSharedFiles(drone).render(maxWaitTime);
            fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1);
            folderDetailsPage = fileInfo.selectViewFolderDetails().render(maxWaitTime);

            // if logs user2 (collaborator) or user3 (contributor) or user4 (consumer)
            if (i == 1 || i == 2 || i == 3)
                // try to edit comment of coordinator
                assertFalse(folderDetailsPage.isEditCommentButtonPresent(commentCoord));
            else if (i == 0)
            {
                folderDetailsPage.editComment(commentColl, commentColl + 1);
                folderDetailsPage.saveEditComments().render(maxWaitTime);
                List<String> commentsFolder = folderDetailsPage.getComments();

                assertTrue(commentsFolder.contains(commentColl + 1));

                commentColl += 1;
            }
            // if editor logged in (user5)
            else if (i == 4)
                // try to edit comment of contributor
                assertFalse(folderDetailsPage.isEditCommentButtonPresent(commentCont));

        }

        ShareUser.logout(drone);

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" }, alwaysRun = true, dependsOnMethods = { "AONE_15233" })
    public void AONE_15234() throws Exception
    {
        testName = getTestName();
        String testUser1 = getUserNameFreeDomain(user + 1);
        String testUser2 = getUserNameFreeDomain(user + 2);
        String testUser3 = getUserNameFreeDomain(user + 3);
        String testUser4 = getUserNameFreeDomain(user + 4);
        String testUser5 = getUserNameFreeDomain(user + 5);
        String[] users = new String[] { testUser5, testUser4, testUser3, testUser2, testUser1 };

        String fileName1 = file + "1.txt";
        String folderName1 = folder + 1;

        String commentColl = UserRole.COLLABORATOR.toString().toLowerCase();
        String commentCoord = UserRole.COORDINATOR.toString().toLowerCase();
        String commentCont = UserRole.CONTRIBUTOR.toString().toLowerCase();

        for (int i = 0; i < 5; i++)
        {
            // Shared Files is opened
            ShareUser.login(drone, users[i], DEFAULT_PASSWORD);

            // navigate to the Shared Files
            SharedFilesPage sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(drone).render(maxWaitTime);

            // try to delete manager's comment to the Folder1 and to the Document1
            sharedFilesPage.selectFolder(folderName1).render();
            DocumentDetailsPage documentDetailsPage = sharedFilesPage.selectFile(fileName1).render();

            // try to delete the Document1
            List<String> documentActions = documentDetailsPage.getDocumentActionList();

            // if editor of collaborator logged in
            if (i == 0 || i == 3)
            {
                assertFalse(documentDetailsPage.isDeleteCommentButtonPresent(commentCoord));
            }
            // if consumer logged in
            if (i == 1)
                assertFalse(documentDetailsPage.isDeleteCommentButtonPresent(commentCont));

            // if contributor logged in
            if (i == 2)
            {
                List<String> comments = documentDetailsPage.getComments();
                if (comments.contains(commentColl + 1))
                    commentColl += 1;
                assertFalse(documentDetailsPage.isDeleteCommentButtonPresent(commentColl));
            }
            // if coordinator logged in
            if (i == 4)
            {
                assertEquals(5, documentDetailsPage.getCommentCount());
                documentDetailsPage.deleteComment(commentColl);
                assertFalse(documentDetailsPage.isCommentCorrect(commentColl));
            }

            if (i != 4)
                assertFalse(documentActions.contains("Delete Document"));
            else
            {
                sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(drone).render(maxWaitTime);
                sharedFilesPage.selectFolder(folderName1).render(maxWaitTime);
                ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW).render(maxWaitTime);
                FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1);
                assertTrue(fileInfo.isDeletePresent());
                webDriverWait(drone, 3000);
                ConfirmDeletePage deletePage = fileInfo.selectDelete().render();
                deletePage.selectAction(ConfirmDeletePage.Action.Delete).render(maxWaitTime);

                sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(drone).render(maxWaitTime);
                assertFalse(sharedFilesPage.isFileVisible(fileName1));
            }

            // try to delete the Folder1
            ShareUserSharedFilesPage.openSharedFiles(drone).render(maxWaitTime);
            ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW).render(maxWaitTime);
            webDriverWait(drone, 2000);
            FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1);
            FolderDetailsPage folderDetailsPage = fileInfo.selectViewFolderDetails().render(maxWaitTime);
            List<String> folderActions = folderDetailsPage.getFolderActionList();

            // if editor of collaborator logged in
            if (i == 0 || i == 3)
            {
                assertFalse(folderDetailsPage.isDeleteCommentButtonPresent(commentCoord));
            }
            // if consumer logged in
            if (i == 1)
                assertFalse(folderDetailsPage.isDeleteCommentButtonPresent(commentCont));

            // if contributor logged in
            if (i == 2)
            {
                assertFalse(folderDetailsPage.isDeleteCommentButtonPresent(commentColl));
            }
            // if coordinator logged in
            if (i == 4)
            {
                assertEquals(5, folderDetailsPage.getCommentCount());

                // Remove comment
                folderDetailsPage.deleteComment(commentColl + 1);
                assertFalse(folderDetailsPage.isCommentCorrect(commentColl));
            }

            // if coordinator logged in
            if (i != 4)
                assertFalse(folderActions.contains("Delete Folder"));
            else
            {
                ShareUserSharedFilesPage.openSharedFiles(drone).render();
                fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName1);
                ConfirmDeletePage deletePage = fileInfo.selectDelete().render();
                deletePage.selectAction(ConfirmDeletePage.Action.Delete).render(maxWaitTime);

                sharedFilesPage = ShareUserSharedFilesPage.openSharedFiles(drone).render();
                assertFalse(sharedFilesPage.isItemVisble(folderName1));
            }

            ShareUser.logout(drone);

        }

    }
}
