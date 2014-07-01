/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.site.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.enums.ZoomStyle;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentAction;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditInGoogleDocsPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.GalleryViewFileDirectoryInfo;
import org.alfresco.po.share.site.document.GoogleDocsAuthorisation;
import org.alfresco.po.share.site.document.GoogleDocsUpdateFilePage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.po.share.site.document.ShareLinkPage;
import org.alfresco.po.share.site.document.ViewPublicLinkPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserGoogleDocs;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.WebDroneType;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.Target;

/**
 * This Class contains the tests of Gallery View functionality for files and folders.
 * 
 * @author cbairaajoni
 */
@Listeners(FailedTestListener.class)
public class GalleryViewTest extends AbstractUtils
{
    private String testUser;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setupCustomDrone(WebDroneType.DownLoadDrone);

        // create a single user
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
    }

    @Test(groups = {"DataPrepAlfrescoOne", "NonGrid"})
    public void dataprep_ALF_8751() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add the file to doclib</li>
     * <li>Verify Gallery info icon is visible for that.</li>
     * <li>Click on Gallery info icon.</li>
     * <li>Verify that Gallery popup is opened.</li>
     * <li>Open the document in EditInGoogleDocs.</li>
     * <li>Verify that google docs is opened successfully.</li>
     * </ul>
     */
    @Test(groups = {"AlfrescoOne", "NonGrid"}, timeOut = 400000)
    public void ALF_8751() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + getRandomStringWithNumders(4) + ".doc";
        String comment = "comment" + getRandomStringWithNumders(4);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        ContentDetails content = new ContentDetails();
        content.setName(fileName1);

        // Creating files.
        DocumentLibraryPage docLibPage = ShareUser.createContent(customDrone, content, ContentType.PLAINTEXT);

        // Select file
        // Open Gallery View
        docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        FileDirectoryInfo thisRow = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        Assert.assertTrue(thisRow.isInfoIconVisible());

        // Click on Gallery Info Icon
        thisRow.clickInfoIcon();
        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        String version = thisRow.getVersionInfo();

        // Select EditInGoogleDocs
        GoogleDocsAuthorisation googleAuthorisationPage = thisRow.selectEditInGoogleDocs().render();
        googleAuthorisationPage.render();

        EditInGoogleDocsPage googleDocsPage = ShareUserGoogleDocs.signInGoogleDocs(googleAuthorisationPage);

        // Verify the Document is opened in google docs or not.
        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());

        GoogleDocsUpdateFilePage googleUpdatefile = ShareUserGoogleDocs.saveGoogleDocWithVersionAndComment(customDrone, comment, true);
        googleUpdatefile.render();
        docLibPage = googleUpdatefile.submit().render();

        Assert.assertNotNull(docLibPage);
        thisRow = docLibPage.getFileDirectoryInfo(fileName1 + ".txt");
        Assert.assertNotEquals(thisRow.getVersionInfo(), version);
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8748() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Open Site Library
        ShareUser.openDocumentLibrary(customDrone);

        //TODO - Add step 3. Аt least 1 folders and 1 .txt/.xml/.html document are created according to TestLink
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add the file to doclib</li>
     * <li>Get the document version</li>
     * <li>Select InlineView</li>
     * <li>Save the modified details.</li>
     * <li>Get the modified doc version.</li>
     * <li>Verify both versions should not be same.</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8748() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + getRandomStringWithNumders(4) + ".txt";
        String fileInfo[] = { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo);

        // Open Gallery View
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        DocumentDetailsPage detailsPage = docLibPage.selectFile(fileName1).render();

        String docVersion = detailsPage.getDocumentVersion();

        docLibPage = ShareUser.openDocumentLibrary(customDrone);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setDescription(fileName1);

        // Save the modified changes
        ShareUserRepositoryPage.editTextDocumentInLine(customDrone, fileName1, contentDetails).render();
        
        detailsPage = ShareUser.openDocumentDetailPage(customDrone, fileName1);
        String newDocVersion = detailsPage.getDocumentVersion();
        
        // Verify the both versions are not same.
        //TODO Add explanation assert
        Assert.assertNotEquals(docVersion, newDocVersion);
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8747() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String moveFolderName = getFolderName("move" + testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Open Site Library
        ShareUser.openDocumentLibrary(customDrone);
        ShareUserSitePage.createFolder(customDrone, moveFolderName, "");
    }

    /**
     * Note : Jira task for merging QA-459
     * Test: This includes ALF_8747 / ALF_8746
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Add the folder and file to doclib</li>
     * <li>Move the file into moveFolder.</li>
     * <li>Verify that file is moved into moveFolder.</li>
     * <li>Move the folder into moveFolder.</li>
     * <li>Verify that folder is moved into moveFolder.</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8747() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "file1") + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String moveFolderName = getFolderName("move" + testName);
        String fileInfo[] = { fileName1 };
        String[] moveFolderPath = { moveFolderName };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating file and folder.
        ShareUser.createFolderInFolder(customDrone, folderName, "", DOCLIB);
        ShareUser.uploadFileInFolder(customDrone, fileInfo);

        // Open document library in GalleryView.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        // move the testFolder to moveFolder
        ShareUserSitePage.copyOrMoveToFolder(customDrone, siteName, fileName1, moveFolderPath, false);

        // Navigating to movefolder
        docLibPage = ShareUserSitePage.navigateToFolder(customDrone, moveFolderName);

        // Verifying that file is moved successfully.
        Assert.assertTrue(docLibPage.isFileVisible(fileName1));

        // Open document library Documents view.
        ShareUser.openDocumentLibrary(customDrone);

        // move the testFolder to moveFolder
        ShareUserSitePage.copyOrMoveToFolder(customDrone, siteName, folderName, moveFolderPath, false);

        // Navigating to movefolder
        docLibPage = ShareUserSitePage.navigateToFolder(customDrone, moveFolderName);

        // Verifying that file is moved successfully.
        Assert.assertTrue(docLibPage.isFileVisible(folderName));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8712() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO - Add step 3. Аt least 1 folders and 1 document  are created. according to TestLink
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add the file to doclib</li>
     * <li>Upload new version of document</li>
     * <li>verify upload is successfull.</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8712() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "file1") + getRandomStringWithNumders(4);
        String fileName2 = getFileName(testName + "file2") + getRandomStringWithNumders(4);
        String fileContents = "New File being created via newFile:" + fileName2;
        File newFileName = newFile(DATA_FOLDER + (fileName2), fileContents);
        String comment = "new doc uploaded";
        String fileInfo[] = { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo);

        // Open document library in GalleryView.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        String version = thisRow.getVersionInfo();

        // Upload New version of the document.
        UpdateFilePage updatePage = thisRow.selectUploadNewVersion().render();

        docLibPage = (DocumentLibraryPage) ShareUserSitePage.UploadNewVersion(customDrone, updatePage, false, newFileName.getName(), comment);
        
        Assert.assertNotNull(docLibPage);
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        Assert.assertNotEquals(thisRow.getVersionInfo(), version);

        //TODO Add explanation assert
    }

    @Test(groups = {"DataPrepAlfrescoOne", "NonGrid"})
    public void dataprep_ALF_8745() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO - Add step 3. At least 1 folder and 1 document are created in the Document Library according to TestLink
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add 1 file to doclib</li>
     * <li>Select download</li>
     * <li>Verify the file is downloaded successfully in given folder.</li>
     * </ul>
     */
    @Test(groups = {"AlfrescoOne", "NonGrid", "download"})
    public void ALF_8745() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1") + getRandomStringWithNumders(4);
        String fileInfo1[] = { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);

        // Open the documentlibrary in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // download the file
        thisRow.selectDownload();
        docLibPage.waitForFile(downloadDirectory + fileName1);

        // Thread needs to be stopped for some time to make sure the file download stream has been closed properly
        // otherwise download part will not be closed and will throws an exception.
        webDriverWait(customDrone, maxDownloadWaitTime);

        // Verify the file is downloaded or not.
        //TODO Add explanation assert
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(fileName1));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8749() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        //TODO - Add step 3. Аt least 1 folders and 1 document  are created according to TestLink
    }

    /**
     * Test: Test case steps needs to be amended in TestLink.
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Add 1 file to doclib</li>
     * <li>Verify info icon</li>
     * <li>Click the Info Icon</li>
     * <li>Info Popup is displayed.</li>
     * <li>ENT : Click More and select startworkflow / CLOUD: Click more and select "Create Task"</li>
     * <li>ENT : Verify Start workflow page appears / CLOUD: Create Task page appears</li>
     * <li>ENT : Click "Please select a workflow' button and select  "Adhoc Workflow" / CLOUD: Click "Please select a task' button and select "New Task";</li>
     * <li>ENT : Verify Adhoc Workflow form appears / CLOUD: New Task form appears</li>
     * <li>ENT : Enter the details of the workflow task and click "Start workflow" button / CLOUD: Enter the details of the task and click "Create Task" button</li>
     * <li>ENT : Workflow is created, user returns to Doclib page / CLOUD: Task is created, user returns to Doclib page</li>
     * <li>ENT : Open this document in View Details and check Workflows pane / CLOUD: Open this document in View Details and check Tasks pane</li>
     * <li>ENT : This document is part of the following workflow(s): <workflow_name> is displayed / CLOUD: This document is part of the following task(s):
     * <task_name> is displayed</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8749() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String fileInfo1[] = { fileName1 };
        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);

        // Open the documentlibrary in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // verify info button
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // Select more , select start workflow/task
        StartWorkFlowPage startWorkFlow = thisRow.selectStartWorkFlow().render();

        //TODO Add explanation assert
        Assert.assertTrue(startWorkFlow instanceof StartWorkFlowPage);
        
        // select Adhoc Workflow/task
        NewWorkflowPage newWorkflowPage = startWorkFlow.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setDueDate(dueDate);
        formDetails.setTaskPriority(Priority.MEDIUM);
        formDetails.setAssignee(testUser);
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(testUser);
        formDetails.setReviewers(reviewers);
        formDetails.setMessage(simpleTaskWF);

        // Start workflow / task 
        docLibPage = newWorkflowPage.startWorkflow(formDetails).render();
        Assert.assertTrue(docLibPage.isFileVisible(fileName1));

        thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        DocumentDetailsPage detailsPage = thisRow.selectThumbnail().render();

        // Verify the document is part of work flow/task.
        //TODO Add explanation assert
        Assert.assertTrue(detailsPage.isPartOfWorkflow());
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8742() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ////TODO - Add step 3. At least 1 folder and 1 document are created in the Document Library according to TestLink
    }

    /**
     * Test: This test includes the 8739/8742
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add 1 file to doclib</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8742() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileInfo1[] = { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);
        ShareUser.createFolderInFolder(customDrone, folderName, "", DOCLIB);

        // Open the documentlibrary in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // For File:
        // verify info button
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // Click on documetn name link
        DocumentDetailsPage detailsPage = thisRow.clickContentNameFromInfoMenu().render();

        // verify the document details page is opened
        //TODO Add explanation assert
        Assert.assertTrue(detailsPage.isDocumentDetailsPage());

        docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        // For Folder:
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // verify info button
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // Click on folder name link
        docLibPage = thisRow.clickContentNameFromInfoMenu().render();

        // verify the document details page is opened
        //TODO Add explanation assert
        Assert.assertTrue(docLibPage.isDocumentLibrary());
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8741() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO - Add step 3. At least 1 folder and 1 document are created in the Document Library according to TestLink
    }

    /**
     * Test: This test includes the 8740/8741
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add 1 file and folder to doclib</li>
     * <li>Select modifier link</li>
     * <li>Verify the User profile page is opened.</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8741() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileInfo1[] = { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);
        ShareUser.createFolderInFolder(customDrone, folderName, "", DOCLIB);

        // Open the documentlibrary in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // For File:
        // verify info button
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // Click on user link
        MyProfilePage profilePage = thisRow.selectModifier().render();

        // verify the user profile page is opened
        //TODO Add explanation assert
        Assert.assertTrue(profilePage.getTitle().contains(customDrone.getValue("user.profile.page.text")));

        docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        // For Folder:
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // verify info button
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // Click on user link
        profilePage = thisRow.selectModifier().render();

        // verify the user profile page is opened
        //TODO Add explanation assert
        Assert.assertTrue(profilePage.getTitle().contains(customDrone.getValue("user.profile.page.text")));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8719() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO - Add step 3. At least 1 document and 1 folder are created in the Document Library according to TestLink
    }

    /**
     * Test: This test includes 8736/8719
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add 1 file and folder to doclib</li>
     * <li>Select name edit link and rename, click save, verify name is changed successfully for file/folder</li>
     * <li>Select name edit link and rename, click cancel, verify name is not changed successfully for file/folder</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8719() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String updatedFileName1 = getFileName(testName + "U") + System.currentTimeMillis();
        String notUpdatedfileName1 = getFileName(testName + "NU") + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String updatedFolderName = getFolderName(testName + "U") + System.currentTimeMillis();
        String notUpdatedFolderName = getFolderName(testName + "NU") + System.currentTimeMillis();
        String fileInfo1[] = { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);
        ShareUser.createFolderInFolder(customDrone, folderName, "", DOCLIB);

        // Open the documentlibrary in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // For File:
        // verify info button
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // mouse over on documetn link and clicks on edit link
        // Enter the modified name, Click save
        thisRow.renameContent(updatedFileName1);

        thisRow = docLibPage.getFileDirectoryInfo(updatedFileName1);

        // verify the rename is successful
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getContentNameFromInfoMenu().equals(updatedFileName1));

        // mouse over on documetn link and clicks on edit link
        thisRow.contentNameEnableEdit();

        // Enter the modified name
        thisRow.contentNameEnter(notUpdatedfileName1);

        // Click Cancel
        thisRow.contentNameClickCancel();

        // Verify the name is not changed.
        //TODO Add explanation assert
        Assert.assertTrue(docLibPage.isFileVisible(updatedFileName1));
        Assert.assertFalse(docLibPage.isFileVisible(notUpdatedfileName1));

        // For Folder:
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // verify info button
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        // mouse over on documetn link and clicks on edit link
        // Enter the modified name, Click save
        thisRow.renameContent(updatedFolderName);

        thisRow = docLibPage.getFileDirectoryInfo(updatedFolderName);

        // verify the rename is successful
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getContentNameFromInfoMenu().equals(updatedFolderName));

        // mouse over on documetn link and clicks on edit link
        thisRow.contentNameEnableEdit();

        // Enter the modified name
        thisRow.contentNameEnter(notUpdatedFolderName);

        // Click Cancel
        thisRow.contentNameClickCancel();

        // Verify the file is not found with cancelled name changes.
        //TODO Add explanation assert
        Assert.assertTrue(docLibPage.isFileVisible(updatedFolderName));
        Assert.assertFalse(docLibPage.isFileVisible(notUpdatedFolderName));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8723() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO - Add step 3. At least 1 document and 1 folder are created in the Document Library according to TestLink
    }

    // This test includes 8716/8723
    @Test(groups = "AlfrescoOne")
    public void ALF_8723() throws Exception
    {
        /** Start Test */
        String testName = getTestName();

        testUser = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);

        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileInfo1[] = { fileName1 };

        String comment = "test";
        String newComment = "test Updated";
        String noNewComment = "test Updated cancelled";

        String COMMENT_TOOLTIP = customDrone.getValue("comment.on.document.text");
        String FOLDER_COMMENT_TOOLTIP = customDrone.getValue("comment.on.folder.text");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);
        ShareUser.createFolderInFolder(customDrone, folderName, "", DOCLIB);

        // Open the documentlibrary in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // For File:
        // Verify the comments link and tooltip
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isCommentLinkPresent());
        Assert.assertTrue(thisRow.getCommentsToolTip().equalsIgnoreCase(COMMENT_TOOLTIP));

        // Add the comment
        DocumentDetailsPage detailsPage = thisRow.clickCommentsLink().render();
        detailsPage = detailsPage.addComment(comment).render();

        ShareUser.openDocumentLibrary(customDrone);

        // Verify the comments count is 1 on doclib page.
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getCommentsCount() == 1);

        // Edit the previous comment and verify the changes
        detailsPage = thisRow.clickCommentsLink().render();
        detailsPage = detailsPage.editComment(comment, newComment).render();
        detailsPage = detailsPage.saveEditComments().render();

        //TODO Add explanation assert
        Assert.assertTrue(detailsPage.getComments().contains(newComment));
        Assert.assertFalse(detailsPage.getComments().contains(comment));

        // Edit the previous comment anc cancel the changes,and verify the changes
        detailsPage = detailsPage.editComment(newComment, noNewComment).render();
        detailsPage.cancelEditComments();

        //TODO Add explanation assert
        Assert.assertTrue(detailsPage.getComments().contains(newComment));
        Assert.assertFalse(detailsPage.getComments().contains(noNewComment));

        // Remove the comment and verify it is removed successfully.
        detailsPage = detailsPage.removeComment(newComment).render();

        docLibPage = ShareUser.openDocumentLibrary(customDrone);
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getCommentsCount() == 0);

        // For Folder:
        docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Verify the comments link and tooltip
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isCommentLinkPresent());
        Assert.assertTrue(thisRow.getCommentsToolTip().equalsIgnoreCase(FOLDER_COMMENT_TOOLTIP));

        // Add the comment
        FolderDetailsPage folderDetailsPage = thisRow.clickCommentsLink().render();
        folderDetailsPage = folderDetailsPage.addComment(comment).render();

        ShareUser.openDocumentLibrary(customDrone);

        // Verify the comments count is 1 on doclib page.
        thisRow = docLibPage.getFileDirectoryInfo(folderName);
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getCommentsCount() == 1);

        // Edit the previous comment and verify the changes
        folderDetailsPage = thisRow.clickCommentsLink().render();
        folderDetailsPage = folderDetailsPage.editComment(comment, newComment).render();
        folderDetailsPage = folderDetailsPage.saveEditComments().render();

        //TODO Add explanation assert
        Assert.assertTrue(folderDetailsPage.getComments().contains(newComment));
        Assert.assertFalse(folderDetailsPage.getComments().contains(comment));

        // Edit the previous comment anc cancel the changes,and verify the changes
        folderDetailsPage = folderDetailsPage.editComment(newComment, noNewComment).render();
        folderDetailsPage.cancelEditComments();
        folderDetailsPage = folderDetailsPage.render();
        //TODO Add explanation assert
        Assert.assertTrue(folderDetailsPage.getComments().contains(newComment));
        Assert.assertFalse(folderDetailsPage.getComments().contains(noNewComment));

        // Remove the comment and verify it is removed successfully.
        folderDetailsPage = folderDetailsPage.removeComment(newComment).render();

        docLibPage = ShareUser.openDocumentLibrary(customDrone);
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getCommentsCount() == 0);
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8728() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO - Move step 3. At least 1 document is created in the Document Library from test to dataprep
    }

    @Test(groups = {"AlfrescoOne", "Enterprise42Bug"})
    public void ALF_8728() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String fileInfo1[] = { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);

        // Open the documentlibrary in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // Click on view in browser from gallery view.
        thisRow.selectViewInBrowser();

        // Check the document is correctly displayed
        String content = "this is a sample test upload file";
        String htmlSource = ((WebDroneImpl) customDrone).getDriver().getPageSource();
        //TODO Add explanation assert
        Assert.assertTrue(htmlSource.contains(content));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8729() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        //TODO Move step 3. At least 1 document and 1 folder are created in the Document Library from test to dataprep
    }

    // This test includes 8666/8729
    @Test(groups = "AlfrescoOne")
    public void ALF_8729() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileInfo1[] = { fileName1 };
        String updatedFileName = getFileName(testName + "U") + System.currentTimeMillis();
        String updatedFolderName = getFolderName(testName + "U") + System.currentTimeMillis();
        String newTitle = testName + "title";
        String description = testName + "desc";
        String cancelledname = testName + "cancelled name";
        String cancelledTitle = testName + "cancelled title";
        String cancelledDescription = testName + "cancelled desc";

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);
        ShareUser.createFolderInFolder(customDrone, folderName, "", DOCLIB);

        // Open the document library in Gallery view.
        ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        // File: Edit the name,title,description and save
        DocumentLibraryPage docLibPage = ShareUserSitePage.editProperties(customDrone, fileName1, updatedFileName, newTitle, description, true);

        FileDirectoryInfo thisRow = ShareUserSitePage.getFileDirectoryInfo(customDrone, updatedFileName);

        // Verify the changes are successful.
        EditDocumentPropertiesPage editDocPropertiesPage = thisRow.selectEditProperties().render();

        //TODO Add explanation assert
        Assert.assertTrue(editDocPropertiesPage.getName().equals(updatedFileName));
        Assert.assertTrue(editDocPropertiesPage.getDocumentTitle().equals(newTitle));
        Assert.assertTrue(editDocPropertiesPage.getDescription().equals(description));
        editDocPropertiesPage.clickOnCancel();
        // Edit the name,title,description and cancel the changes and verify the changes are not reflected.
        docLibPage = ShareUserSitePage.editProperties(customDrone, updatedFileName, cancelledname, cancelledTitle, cancelledDescription, false);

        //TODO Add explanation assert
        Assert.assertFalse(docLibPage.isFileVisible(cancelledname));
        thisRow = docLibPage.getFileDirectoryInfo(updatedFileName);

        // Verify the changes are not successful.
        editDocPropertiesPage = thisRow.selectEditProperties().render();

        //TODO Add explanation assert
        Assert.assertFalse(editDocPropertiesPage.getName().equals(cancelledname));
        Assert.assertFalse(editDocPropertiesPage.getDocumentTitle().equals(cancelledTitle));
        Assert.assertFalse(editDocPropertiesPage.getDescription().equals(cancelledDescription));
        editDocPropertiesPage.clickOnCancel();

        // Folder: Edit the name,title,description and save
        docLibPage = ShareUserSitePage.editProperties(customDrone, folderName, updatedFolderName, newTitle, description, true);

        thisRow = docLibPage.getFileDirectoryInfo(updatedFolderName);

        // verify the changes are successful..
        editDocPropertiesPage = thisRow.selectEditProperties().render();

        //TODO Add explanation assert
        Assert.assertTrue(editDocPropertiesPage.getName().equals(updatedFolderName));
        Assert.assertTrue(editDocPropertiesPage.getDocumentTitle().equals(newTitle));
        Assert.assertTrue(editDocPropertiesPage.getDescription().equals(description));
        editDocPropertiesPage.clickOnCancel();
        // Edit the name,title,description and cancel the changes and verify the changes are not reflected.
        docLibPage = ShareUserSitePage.editProperties(customDrone, updatedFolderName, cancelledname, cancelledTitle, cancelledDescription, false);

        //TODO Add explanation assert
        Assert.assertFalse(docLibPage.isFileVisible(cancelledname));
        thisRow = docLibPage.getFileDirectoryInfo(updatedFolderName);

        // verify the changes are not successful.
        editDocPropertiesPage = thisRow.selectEditProperties().render();

        //TODO Add explanation assert
        Assert.assertFalse(editDocPropertiesPage.getName().equals(cancelledname));
        Assert.assertFalse(editDocPropertiesPage.getDocumentTitle().equals(cancelledTitle));
        Assert.assertFalse(editDocPropertiesPage.getDescription().equals(cancelledDescription));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8722() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String copyFolderName = getFolderName("copy" + testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Open Site Library
        ShareUser.openDocumentLibrary(customDrone);
        ShareUserSitePage.createFolder(customDrone, copyFolderName, "");

        //TODO Move step 3. At least 3 folders (folder1 containing subfolder folder1_1and folder2) and 1 document are created in the Document Library; from test to dataprep
    }

    /**
     * Test: This includes ALF_8722 / ALF_8680
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add folder and file to doclib</li>
     * <li>Copy the file into copyFolder</li>
     * <li>Verify that file is copied succesfully</li>
     * <li>Copy the folder into copyFolder</li>
     * <li>Verify that folder is copied succesfully</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8722() throws Exception
    {
        /** Start Test */
        String testName = getTestName();

        testUser = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);

        String copyFolderName = getFolderName("copy" + testName);
        String folder = getFolderName(testName) + System.currentTimeMillis();
        String subFolder = getFolderName(testName + "s") + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String fileInfo[] = { fileName1 };

        String[] copyFolderPath = { copyFolderName };
        String[] copyFilePath = { folder };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, fileInfo);

        // Creating folders
        ShareUserSitePage.createFolder(customDrone, folder, "");

        ShareUserSitePage.navigateToFolder(customDrone, folder);

        ShareUserSitePage.createFolder(customDrone, subFolder, "");

        ShareUser.openDocumentLibrary(customDrone);

        // Open document library in GalleryView.
        ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        // Copy the testFolder to copyFolder
        ShareUserSitePage.copyOrMoveToFolder(customDrone, siteName, fileName1, copyFilePath, true);

        // Navigating to copied folder
        DocumentLibraryPage docLibPage = ShareUserSitePage.navigateToFolder(customDrone, folder);

        // Verifying that file is copied successfully.
        //TODO Add explanation assert
        Assert.assertTrue(docLibPage.isFileVisible(fileName1));

        docLibPage = ShareUser.openDocumentLibrary(customDrone);

        // Copy the testFolder to copyFolder
        ShareUserSitePage.copyOrMoveToFolder(customDrone, siteName, folder, copyFolderPath, true);

        // Navigating to copied folder
        docLibPage = ShareUserSitePage.navigateToFolder(customDrone, copyFolderName);

        // Verifying that folder is copied successfully.
        //TODO Add explanation assert
        Assert.assertTrue(docLibPage.isFileVisible(folder));

        ShareUserSitePage.navigateToFolder(customDrone, folder);

        // Verifying that sub folder is copied successfully.
        //TODO Add explanation assert
        Assert.assertTrue(docLibPage.isFileVisible(subFolder));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8743() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ////TODO Move step 3. At least 1 folder and 1 document are created in the Document Library; from test to dataprep

    }

    /**
     * Test: This test includes ALF_8743 / ALF_8737
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add folder and file to doclib</li>
     * <li>Add tag on file1</li>
     * <li>Verify the tag is created and tagsize also</li>
     * <li>Click on tag under file1</li>
     * <li>Verify the file1 is displayed</li>
     * <li>Click on tag under Documents tree</li>
     * <li>Verify the file1 is displayed</li>
     * <li>Add tag on folder</li>
     * <li>Verify the tag is created and tagsize also</li>
     * <li>Click on tag under folder</li>
     * <li>Verify the folder is displayed</li>
     * <li>Click on tag under Documents tree</li>
     * <li>Verify the folder is displayed</li>
     * </ul>
     */
    @Test(groups = "DocLibToolBar")
    public void ALF_8743() throws Exception
    {
        /** Start Test */
        String testName = getTestName();

        testUser = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);

        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileInfo1[] = { fileName1 };
        String testFileTagName = "file" + getRandomStringWithNumders(5);
        String testFolderTagName = "folder" + getRandomStringWithNumders(5);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating file.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);

        // Creating folder.
        ShareUserSitePage.createFolder(customDrone, folderName, "");

        // Open document library in Gallery view.
        ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        // Add a tag to the first file
        ShareUserSitePage.addTag(customDrone, fileName1,testFileTagName);


        // Verify the Tags size

        //TODO Add explanation assert
        Assert.assertEquals(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).getTags().size(), 1, "Tag added above isn't displayed");

        // Clicking the tagName link present under file1 name
        FileDirectoryInfo thisRow = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        thisRow.clickInfoIcon();
        thisRow.clickOnTagNameLink(testFileTagName).render();

        // Check that the file1 is listed

        //TODO Add explanation assert
        Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(customDrone, fileName1, true));

        // Clicking the tagName present under Tags menu tree on Document Library page.
        DocumentLibraryPage docLibPage = getSharePage(customDrone).render();
        docLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(testFileTagName).render();

        // Check that the file1 is listed

        //TODO Add explanation assert
        Assert.assertTrue(docLibPage.isFileVisible(fileName1));

        // Folder:
        docLibPage = ShareUser.openDocumentLibrary(customDrone);

        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Add a tag to the first file
        ShareUserSitePage.getFileDirectoryInfo(customDrone, folderName).addTag(testFolderTagName);
        docLibPage = docLibPage.getSiteNav().selectSiteDocumentLibrary().render();
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Verify the Tags size
        Assert.assertEquals(thisRow.getTags().size(), 1);

        // Clicking the tagName link present under file1 name
        docLibPage = docLibPage.getSiteNav().selectSiteDocumentLibrary().render();
        thisRow = docLibPage.getFileDirectoryInfo(folderName);
        thisRow.clickInfoIcon();
        docLibPage = thisRow.clickOnTagNameLink(testFolderTagName).render();

        // Check that the file1 is listed

        //TODO Add explanation assert
        Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(customDrone, folderName, true));

        // Clicking the tagName present under Tags menu tree on Document Library page.
        docLibPage = docLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(testFolderTagName).render();

        // Check that the file1 is listed

        //TODO Add explanation assert
        Assert.assertTrue(docLibPage.isFileVisible(folderName));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8721() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO Move step 3. At least 1 document and 1 folder containing a subfolder are created in the Document Library from test to dataprep
    }

    /**
     * Test: This test includes ALF_8721 / ALF_8711
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add folder and file to doclib</li>
     * <li>Delete the file1</li>
     * <li>File1 is deleted successfully.</li>
     * <li>Delete the folder</li>
     * <li>folder is deleted successfully.</li>
     * </ul>
     */
    @Test(groups = "DocLibToolBar")
    public void ALF_8721() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + getRandomStringWithNumders(4);
        String folderName = getFolderName(testName) + getRandomStringWithNumders(3);
        String fileInfo1[] = { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating file.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);

        // Creating folder.
        ShareUserSitePage.createFolder(customDrone, folderName, "");

        // Open document library in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // Deleting the first file
        docLibPage = thisRow.delete().render();

        // Verify the first file is deleted successfully.
        //TODO Add explanation assert
        Assert.assertFalse(docLibPage.isFileVisible(fileName1));

        // Folder:
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Deleting the folder
        docLibPage = thisRow.delete().render();

        // Verify the folder is deleted successfully.
        //TODO Add explanation assert
        Assert.assertFalse(docLibPage.isFileVisible(folderName));
    }

    @Test(groups = {"DataPrepAlfrescoOne", "NonGrid"})
    public void dataprep_ALF_8664() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO Move step 3. At least 1 folder and 1 document are created in the Document Library from test to dataprep
    }

    /**
     * Test: ALF-8664:Action "Downloads" for folder
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add 1 folder to doclib</li>
     * <li>Select download</li>
     * <li>Verify the folder is downloaded successfully in given folder.</li>
     * </ul>
     */
    @Test(groups = {"EnterpriseOnly", "NonGrid", "Download"})
    public void ALF_8664() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String ZIP_EXTENSION = ".zip";

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating folder.
        ShareUserSitePage.createFolder(customDrone, folderName, "");

        // Open the documentlibrary in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        GalleryViewFileDirectoryInfo thisRow = (GalleryViewFileDirectoryInfo) docLibPage.getFileDirectoryInfo(folderName);

        // download the file
        thisRow.selectDownloadFolderAsZip();
        docLibPage.waitForFile(downloadDirectory + folderName + ZIP_EXTENSION);

        // Thread needs to be stopped for some time to make sure the file download stream has been closed properly
        // otherwise download part will not be closed and will throws an exception.
        webDriverWait(customDrone, maxDownloadWaitTime);

        // Verify the file is downloaded or not.
        //TODO Add explanation assert
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(folderName + ZIP_EXTENSION));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8679() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO Move step 3. At least 1 folder is created in the Document Library from test to dataprep
    }

    /**
     * Test: ALF-8679:Action "Manage Permissions " for folder
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Add folder and file to doclib</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8679() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String accessUser = getUserNameFreeDomain(testName + "accessUser") + getRandomStringWithNumders(5);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + getRandomStringWithNumders(3);
        // User
        String[] testUserInfo = new String[] { accessUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating folder.
        ShareUserSitePage.createFolder(customDrone, folderName, "");

        // Open document library in Gallery view.
        ShareUserSitePage.selectView(customDrone, ViewType.GALLERY_VIEW);

        // Turn off the "Inherit permissions" option, add any users/groups to "Locally Set Permissions", set any role to users/groups and click "Save" button;
        DocumentLibraryPage docLibPage = (DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(customDrone, accessUser, folderName,
                UserRole.COLLABORATOR, false);
        docLibPage.render();
        DetailsPage detailsPage = ShareUserSitePage.getContentDetailsPage(customDrone, folderName);
        
        ManagePermissionsPage managerPermissions = detailsPage.selectManagePermissions().render();

        //TODO Add explanation assert
        Assert.assertNotNull(managerPermissions.getExistingPermission(accessUser));

        //TODO Add explanation assert
        Assert.assertTrue(managerPermissions.getExistingPermission(accessUser).equals(UserRole.COLLABORATOR));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8667() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, SITE_VISIBILITY_PUBLIC);

        //TODO Move step 3. At least 1 folder is created in the Document Library from test to dataprep
    }
    
    /**
     * Test: ALF-8667:"Manage Rules" action for folder
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add folder and file to doclib</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8667() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName1 = getFolderName(testName + 1) + System.currentTimeMillis();
        String folderName2 = getFolderName(testName + 2) + System.currentTimeMillis();

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating folder.
        ShareUserSitePage.createFolder(customDrone, folderName1, "");
        ShareUserSitePage.createFolder(customDrone, folderName2, "");

        // Open document library in Gallery view.
        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.GALLERY_VIEW);
        GalleryViewFileDirectoryInfo thisRow = (GalleryViewFileDirectoryInfo) docLibPage.getFileDirectoryInfo(folderName1);
        
        FolderRulesPage rulesPage = thisRow.selectManageRules().render();
        //TODO Add explanation assert
        Assert.assertNotNull(rulesPage);

        CreateRulePage createRulePage = rulesPage.openCreateRulePage().render();
        //TODO Add explanation assert
        Assert.assertNotNull(createRulePage);
        
        createRulePage.fillNameField(testName);
        createRulePage.fillDescriptionField(testName);

        //Select 'Copy' value in the 'Perform Action' section.
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectCopy(siteName, folderName2);

        // Click "Create" button
        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
        Assert.assertTrue(folderRulesPageWithRules.isRuleNameDisplayed(testName), "Created Rule is not displayed.");
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8678() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, SITE_VISIBILITY_PUBLIC);

        //TODO Move step 3. At least 1 folder and 1 document are created in the Document Library from test to dataprep
    }
    
    // ALF-8678:Action "Manage Aspects " for folder
    @Test(groups = "EnterpriseOnly")
    public void ALF_8678() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + getRandomStringWithNumders(3);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating folder.
        ShareUserSitePage.createFolder(customDrone, folderName, "");

        // Open document library in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        SelectAspectsPage selectAspectsPage = docLibPage.getFileDirectoryInfo(folderName).selectManageAspects().render();

        // Get several aspects in left hand side
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(DocumentAspect.RESTRICTABLE); 
        aspects.add(DocumentAspect.CLASSIFIABLE);

        // Add several aspects to right hand side
        selectAspectsPage = selectAspectsPage.add(aspects).render();

        // Verify assert added to currently selected right hand side
        //TODO Add explanation assert
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.RESTRICTABLE));
        Assert.assertTrue(selectAspectsPage.getSelectedAspects().contains(DocumentAspect.CLASSIFIABLE));
        
        docLibPage = selectAspectsPage.clickApplyChanges().render();
        DetailsPage detailsPage = ShareUserSitePage.getContentDetailsPage(customDrone, folderName);
        
        Map<String, Object> props = detailsPage.getProperties();

        // Verify that the aspects have now been applied to your folder and this is displayed in the Document Details page.
        //TODO Add explanation assert
        Assert.assertNotNull(props.get("Categories"));
        Assert.assertNotNull(props.get("OfflineExpiresAfter(hours)"));
    }

    
    @Test(groups = { "DataPrepAlfrescoOne", "NonGrid" })
    public void dataPrep_ALF_8734() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);

        // Create 2 users
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login with the first user
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(customDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(customDrone);

        // Create Folder
        ShareUserSitePage.createFolder(customDrone, folderName, folderName);
    }

    /**
     * Test: ALF-8734:Action"View Details" for folder
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add folder and file to doclib</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne", "NonGrid" })
    public void ALF_8734()
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        
        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);
        
        // Open doclib in Gallery view
        ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        // Navigate to Folder Details Page
        FolderDetailsPage detailsPage = ShareUser.openFolderDetailPage(customDrone, folderName);
        
        Assert.assertTrue(detailsPage.isCorrectPath(folderName), "Location Path is not present.");
        Assert.assertTrue(detailsPage.isCommentsPanelPresent(), "Comment Panel is not displayed.");
        Assert.assertTrue(detailsPage.isAddCommentsButtonEnbaled(), "AddComment button is not enabled.");
        Assert.assertTrue(detailsPage.isDocumentActionPresent(DocumentAction.EDIT_PROPERTIES), "Edit Properties is not present");
        Assert.assertTrue(detailsPage.isDocumentActionPresent(DocumentAction.COPY_TO), "Copy to is not present");
        Assert.assertTrue(detailsPage.isDocumentActionPresent(DocumentAction.MOVE_TO), "Move to is not present");
        Assert.assertTrue(detailsPage.isDocumentActionPresent(DocumentAction.DELETE_CONTENT), "Delete is not present");
        Assert.assertTrue(detailsPage.isDocumentActionPresent(DocumentAction.MANAGE_RULES), "Manage Rules is not present");
        Assert.assertTrue(detailsPage.isDocumentActionPresent(DocumentAction.MANAGE_PERMISSION_FOL), "Manage Permission not present");
        Assert.assertTrue(detailsPage.isTagPanelPresent(), "Tags Panel is not present.");
        Assert.assertTrue(detailsPage.isCopyShareLinkPresent(), "Copy Share LInk is not present.");
        
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), folderName, "Name Property is not equals with foldername.");
        Assert.assertEquals(properties.get("Title"), "(None)", "Title Property is not present");
        Assert.assertEquals(properties.get("Description"), folderName, "Description Property is not present");
        Assert.assertTrue(detailsPage.isSynPanelPresent(), "Sync Panel is not present.");
        
        if(!isAlfrescoVersionCloud(customDrone))
        {
            Assert.assertTrue(detailsPage.isSharePanePresent(), "Share Panel is not present.");
            Assert.assertTrue(detailsPage.isDownloadAsZipAtTopRight(), "Download as zip is not present.");
            Assert.assertTrue(detailsPage.isDocumentActionPresent(DocumentAction.MANAGE_ASPECTS), "Manager Aspect not present");
            Assert.assertTrue(detailsPage.isDocumentActionPresent(DocumentAction.CHNAGE_TYPE), "Change Type is not present");
            Assert.assertTrue(detailsPage.isDocumentActionPresent(DocumentAction.VIEW_IN_EXPLORER), "View in exlporer is not present");
            Assert.assertTrue(detailsPage.isPermissionsPanelPresent(), "Permission Settings are not present.");
        }
    }
    
    @Test(groups="DataPrepAlfrescoOne")
    public void dataprep_ALF_8755() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "file1");

        // User
        String[] testUserInfo = new String[] { testUser };
        String[] testFile1Info = new String[] { fileName1 };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, testFile1Info);
    }

    //Note: As discussed with Ravi,in TestLink alf-8755 : 3,4,6,7th expected step and 9th steps cannot be automated so will be done by manual testing 
    @Test(groups="AlfrescoOne")
    public void ALF_8755() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "file1");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isShareLinkVisible());

        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();

        // Verify the sharelink, view, unshare and public links
        //TODO Add explanation assert
        Assert.assertNotNull(shareLinkPage);
        Assert.assertTrue(shareLinkPage.isViewLinkPresent());
        Assert.assertTrue(shareLinkPage.isUnShareLinkPresent());

        // vieryfing share by Email link.
        //TODO Add explanation assert
        Assert.assertTrue(shareLinkPage.isEmailLinkPresent());
        Assert.assertTrue(shareLinkPage.isFaceBookLinkPresent());
        Assert.assertTrue(shareLinkPage.isTwitterLinkPresent());
        Assert.assertTrue(shareLinkPage.isGooglePlusLinkPresent());

        // Verify the public link (view)
        ViewPublicLinkPage viewPage = shareLinkPage.clickViewButton().render();
        //TODO Add explanation assert
        Assert.assertTrue(viewPage.isDocumentViewDisplayed());

        DocumentDetailsPage detailsPage = viewPage.clickOnDocumentDetailsButton().render();
        docLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        shareLinkPage = thisRow.clickShareLink().render();

        // Verifying the facoobook link and page
        //TODO Add explanation assert
        Assert.assertTrue(shareLinkPage.isFaceBookLinkPresent());
        shareLinkPage.clickFaceBookLink();
        String mainWindow = customDrone.getWindowHandle();
        //TODO Add explanation assert
        Assert.assertTrue(isWindowOpened(customDrone, customDrone.getValue("page.facebook.title")));
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // Verifying the Twitter link and page
        //TODO Add explanation assert
        Assert.assertTrue(shareLinkPage.isTwitterLinkPresent());
        shareLinkPage.clickTwitterLink();
        mainWindow = customDrone.getWindowHandle();
        //TODO Add explanation assert
        Assert.assertTrue(isWindowOpened(customDrone, customDrone.getValue("page.twitter.title")));
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);

        // Verifying the GooglePlus link and page
        //TODO Add explanation assert
        Assert.assertTrue(shareLinkPage.isGooglePlusLinkPresent());
        shareLinkPage.clickGooglePlusLink();
        mainWindow = customDrone.getWindowHandle();
        //TODO Add explanation assert
        Assert.assertTrue(isWindowOpened(customDrone, customDrone.getValue("page.googleplus.title")));
        customDrone.closeWindow();
        customDrone.switchToWindow(mainWindow);
    }

    @Test(groups="DataPrepAlfrescoOne")
    public void dataprep_ALF_8717() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
       

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO Move step 3. At least 1 folder and 1 document are created in the Document Library from test to dataprep
    }
    
    //Note: As discussed with Ravi,in TestLink alf-8717 : 4, 5, 8, 12, 15 steps cannot be automated so will be done by manual testing
    // This test includes 8717 / 8715
    @Test(groups = "AlfrescoOne")
    public void ALF_8717() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String[] testFile1Info = new String[] { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating file and folder.
        ShareUser.uploadFileInFolder(customDrone, testFile1Info);
        ShareUserSitePage.createFolder(customDrone, folderName, "");

        // / Open Site Library
        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.GALLERY_VIEW);

        // Content
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // Get the favourite tool tip and verify it.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getFavouriteOrUnFavouriteTip().equals(customDrone.getValue(CONTENT_FAVOURITE_TOOLTIP)));

        // Select favourite for the file
        thisRow.selectFavourite();
        docLibPage = docLibPage.render();
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // Verify the file has favourite.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isFavourite());

        // Verify the myfavourite for favourite content
        docLibPage.clickOnMyFavourites().render();
        //TODO Add explanation assert
        Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(customDrone, fileName1, true));
        
        ShareUser.openSiteDashboard(customDrone, siteName);
        docLibPage = ShareUser.openDocumentLibrary(customDrone);
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        
        // Verify the unfavourite tool tip.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getFavouriteOrUnFavouriteTip().equals(customDrone.getValue(CONTENT_UNFAVOURITE_TOOLTIP)));

        thisRow.selectFavourite();
        docLibPage = docLibPage.render();
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // Verify the file doesnt has favourite.
        //TODO Add explanation assert
        Assert.assertFalse(thisRow.isFavourite());

        // Verify the myfavourite for favourite content
        docLibPage.clickOnMyFavourites().render();
        //TODO Add explanation assert
        Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(customDrone, fileName1, false));
        
        ShareUser.openSiteDashboard(customDrone, siteName);
        docLibPage = ShareUser.openDocumentLibrary(customDrone);

        // Folder
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Get the favourite tool tip and verify it.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getFavouriteOrUnFavouriteTip().equals(customDrone.getValue(FOLDER_FAVOURITE_TOOLTIP)));

        // Select favourite for the folder
        thisRow.selectFavourite();
        docLibPage = docLibPage.render();
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Verify the file has favourite.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isFavourite());

        // Verify the myfavourite for favourite folder
        docLibPage.clickOnMyFavourites().render();
        //TODO Add explanation assert
        Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(customDrone, folderName, true));
        
        ShareUser.openSiteDashboard(customDrone, siteName);
        docLibPage = ShareUser.openDocumentLibrary(customDrone);
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Verify the unfavourite tool tip.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getFavouriteOrUnFavouriteTip().equals(customDrone.getValue(FOLDER_UNFAVOURITE_TOOLTIP)));

        thisRow.selectFavourite();
        docLibPage = docLibPage.render();
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Verify the file doesnt has favourite.
        //TODO Add explanation assert
        Assert.assertFalse(thisRow.isFavourite());

        // Verify the myfavourite for unfavourite folder
        docLibPage.clickOnMyFavourites().render();
        //TODO Add explanation assert
        Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(customDrone, folderName, false));
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8663() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String fileInfo1[] = { fileName };
        String folderName = getFolderName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        // Creating file.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);

        // Creating folder.
        ShareUserSitePage.createFolder(customDrone, folderName, "");

        //TODO Move step 3. At least 1 folder and 1 document are created in the Document Library from test to dataprep

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_8663() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage documentLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        DocumentLibraryNavigation navigation = documentLibPage.getNavigation();
        documentLibPage = ((DocumentLibraryPage) navigation.selectGalleryView()).render();

        //TODO Add explanation assert
        assertTrue(navigation.isZoomControlVisible());

        documentLibPage = navigation.selectZoom(ZoomStyle.SMALLER).render();

        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(fileName);
        FileDirectoryInfo thisfolderRow = documentLibPage.getFileDirectoryInfo(folderName);

        double fileHeightSize = thisRow.getFileOrFolderHeight();
        double folderHeightSize = thisfolderRow.getFileOrFolderHeight();

        navigation.selectZoom(ZoomStyle.BIGGER);
        ZoomStyle actualZoomStyle = navigation.getZoomStyle();
        assertEquals(actualZoomStyle, ZoomStyle.BIGGER);

        double actualFileHeight = documentLibPage.getFileDirectoryInfo(fileName).getFileOrFolderHeight();
        double actualFolderHeight = documentLibPage.getFileDirectoryInfo(folderName).getFileOrFolderHeight();

        //TODO Add explanation assert
        assertTrue(fileHeightSize < actualFileHeight);
        assertTrue(folderHeightSize < actualFolderHeight);

        navigation.selectZoom(ZoomStyle.BIGGEST);
        actualZoomStyle = navigation.getZoomStyle();
        //TODO Add explanation assert
        assertEquals(actualZoomStyle, ZoomStyle.BIGGEST);

        fileHeightSize = documentLibPage.getFileDirectoryInfo(fileName).getFileOrFolderHeight();
        folderHeightSize = documentLibPage.getFileDirectoryInfo(folderName).getFileOrFolderHeight();

        //TODO Add explanation assert
        assertTrue(fileHeightSize > actualFileHeight);
        assertTrue(folderHeightSize > actualFolderHeight);

        navigation.selectZoom(ZoomStyle.SMALLER);
        actualZoomStyle = navigation.getZoomStyle();
        //TODO Add explanation assert
        assertEquals(actualZoomStyle, ZoomStyle.SMALLER);

        actualFileHeight = documentLibPage.getFileDirectoryInfo(fileName).getFileOrFolderHeight();
        actualFolderHeight = documentLibPage.getFileDirectoryInfo(folderName).getFileOrFolderHeight();

        //TODO Add explanation assert
        assertTrue(fileHeightSize > actualFileHeight);
        assertTrue(folderHeightSize > actualFolderHeight);

        navigation.selectZoom(ZoomStyle.SMALLEST);
        actualZoomStyle = navigation.getZoomStyle();
        assertEquals(actualZoomStyle, ZoomStyle.SMALLEST);

        fileHeightSize = documentLibPage.getFileDirectoryInfo(fileName).getFileOrFolderHeight();
        folderHeightSize = documentLibPage.getFileDirectoryInfo(folderName).getFileOrFolderHeight();

        //TODO Add explanation assert
        assertTrue(actualFileHeight > fileHeightSize);
        assertTrue(actualFolderHeight > folderHeightSize);
        
        // Resetting style to normal.
        navigation.selectZoom(ZoomStyle.SMALLER);
        actualZoomStyle = navigation.getZoomStyle();
    }

    @Test(groups = "DataPrepAlfrescoOne")
    public void dataprep_ALF_8718() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        //TODO Move step 3. At least 1 document and 1 folder are created in the Document Library from test to dataprep
    }

    /**
     *Note: As discussed with Ravi,in TestLink alf-8718 : 6, 8, 11, 12 steps cannot be automated so will be done by manual testing
     * Test: This test includes ALF_8718 / ALF_8714
     * <ul>
     * <li>Login</li>
     * <li>Open Site Dashboard</li>
     * <li>Open Document Library in Gallery View</li>
     * <li>Verify that Customize Dashboard page is opened</li>
     * <li>Add folder and file to doclib</li>
     * <li>Verify the like tool tip before liking the file1</li>
     * <li>Select like on file1</li>
     * <li>Verify the file is liked and like count.</li>
     * <li>Verify the Unlike tool tip after liking the file1</li>
     * <li>Verify the like tool tip before liking the folder</li>
     * <li>Select like on folder</li>
     * <li>Verify the folder is liked and like count.</li>
     * <li>Verify the Unlike tool tip after liking the folder</li>
     * </ul>
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_8718() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1") + System.currentTimeMillis();
        String folderName = getFolderName(testName + "2") + System.currentTimeMillis();
        String fileInfo1[] = { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Creating file.
        ShareUser.uploadFileInFolder(customDrone, fileInfo1);

        // Creating folder.
        ShareUserSitePage.createFolder(customDrone, folderName, "");

        // Open document library in Gallery view.
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // Get the like tool tip and verify it.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getLikeOrUnlikeTip().equals(customDrone.getValue("content.like.tooltip")));

        // Select like for the first file
        thisRow.selectLike();
        docLibPage = docLibPage.render();
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // Verify the file is liked and like count as well.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isLiked());
        Assert.assertTrue(thisRow.getLikeCount().equals("1"));

        // Verify the unlike tool tip.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getLikeOrUnlikeTip().equals(customDrone.getValue("content.unlike.tooltip")));

        // Select unlike
        thisRow.selectLike();
        docLibPage = docLibPage.render();
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        
        // Verify the file is unliked
        //TODO Add explanation assert
        Assert.assertFalse(thisRow.isLiked());

        // Folder
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Get the like tool tip and verify it.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getLikeOrUnlikeTip().equals(customDrone.getValue("folder.like.tooltip")));

        // Select like for the first file
        thisRow.selectLike();
        docLibPage = docLibPage.render();
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Verify the file is liked and like count as well.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.isLiked());
        Assert.assertTrue(thisRow.getLikeCount().equals("1"));

        // Verify the unlike tool tip.
        //TODO Add explanation assert
        Assert.assertTrue(thisRow.getLikeOrUnlikeTip().equals(customDrone.getValue("content.unlike.tooltip")));

        // Select unlike
        thisRow.selectLike();
        docLibPage = docLibPage.render();
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        
        // Verify the folder is unliked
        //TODO Add explanation assert
        Assert.assertFalse(thisRow.isLiked());
    }    

    @Test(groups = {"DataPrepEnterpriseOnly", "NonGrid"})
    public void dataprep_ALF_14399() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);
    }
    
    @Test(groups = {"EnterpriseOnly", "NonGrid"} )
    public void ALF_14399() throws Exception
    {
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        
        String[] fileTypes = { "aep", "ai", "aiff", "asf", "asnd", "asx", "au", "avi", "avx", "bmp", "css", "doc", "docm", "docx", "dotm", "eml", "eps", "fla", "flac", "flv", "fxp", "gif", "html", "indd", "jpeg", "jpg", "key", "m4v", "mov", "movie", "mp2", "mp3", "mp4", "mpeg", "mpeg2", "mpv2", "numbers", "odg", "odp", "ods", "odt", "oga", "ogg", "ogv", "pages", "pdf", "png", "potm", "potx", "ppam", "ppj", "ppsm", "ppsx", "ppt", "pptm", "pptx", "psd", "qt", "rtf", "sldm", "sldx", "snd", "spx", "svg", "swf", "tiff", "txt", "wav", "webm", "wma", "wmv", "xbm", "xlam", "xls", "xlsb", "xlsm", "xlsx", "xltm", "xltx", "xml", "zip" };

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(customDrone);
        ShareUserSitePage.selectView(customDrone, ViewType.GALLERY_VIEW);
        
        String currentURL = customDrone.getCurrentUrl();
        
        // Start Test
        for(String fileType : fileTypes)
        {
            String fileName = "file-type" + "-" + fileType + "." + fileType;
            File file = newFile(fileName, fileName);
            
            ShareUserSitePage.uploadFile(customDrone, file);
            
            FileDirectoryInfo fileDirInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName);
            String fileUrl = fileDirInfo.getThumbnailURL();
            String imgFileName = String.format("thumbnail_placeholder_256_%s.png", fileType);
            
            File imgFile = new File(DATA_FOLDER + SLASH + "placeholder-thumbnails", imgFileName);
            Target target = new ImageTarget(imgFile);
            
            customDrone.navigateTo(fileUrl);
            
            customDrone.waitForPageLoad(maxWaitTime);
            Thread.sleep(10000);
            
            Assert.assertTrue(customDrone.isImageVisible(target), "File Type: " + fileType + " Image does not match test image.");
            
            customDrone.navigateTo(currentURL);
            
            ShareUser.getSharePage(customDrone).render();
        }
        ShareUser.logout(customDrone);
   }    
}