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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
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

/**
 * This Class contains the tests of Gallery View functionality for files and folders.
 * 
 * @author cbairaajoni
 */
@Listeners(FailedTestListener.class)
public class GalleryViewTest extends AbstractTests
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

    // TODO: Add to group AlfrescoOne
    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
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
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8751() throws Exception
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

        // TODO: Replace PO code with ShareUserSitePage util, for easy maintenance after changes to PO impl
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        Assert.assertTrue(thisRow.isInfoIconVisible());

        // Click on Gallery Info Icon
        thisRow.clickInfoIcon();
        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        String version = thisRow.getVersionInfo();

        // TODO: Use / Amend the existing util: ShareUserGoogleDocs.signIntoEditGoogleDocFromDetailsPage, saveGoogleDoc
        // Select EditInGoogleDocs
        GoogleDocsAuthorisation googleAuthorisationPage = thisRow.selectEditInGoogleDocs().render();
        googleAuthorisationPage.render();

        GoogleSignUpPage signUpPage = googleAuthorisationPage.submitAuth().render();
        EditInGoogleDocsPage googleDocsPage = signUpPage.signUp(googleUserName, googlePassword).render();

        // Verify the Document is opened in google docs or not.
        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());

        GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
        googleUpdatefile.render();
        googleUpdatefile.selectMinorVersionChange();
        googleUpdatefile.setComment(comment);
        docLibPage = googleUpdatefile.submit().render();

        Assert.assertNotNull(docLibPage);
        thisRow = docLibPage.getFileDirectoryInfo(fileName1 + ".txt");
        Assert.assertNotEquals(thisRow.getVersionInfo(), version);
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Open Site Library
        ShareUser.openDocumentLibrary(customDrone);
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
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8748() throws Exception
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

        // TODO: Use or Amend the ShareUserSitePage util to edit various props
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // Select InlineEdit for the file
        InlineEditPage inlineEditPage = thisRow.selectInlineEdit().render();
        EditTextDocumentPage docPage = inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT).render();
        ContentDetails contentDetails = docPage.getDetails();
        contentDetails.setName(fileName1);
        contentDetails.setDescription(fileName1);

        // Save the modified changes
        docLibPage = docPage.save(contentDetails).render();
        detailsPage = ShareUser.openDocumentDetailPage(customDrone, fileName1);
        String newDocVersion = detailsPage.getDocumentVersion();
        // Verify the both versions are not same.
        Assert.assertNotEquals(docVersion, newDocVersion);
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

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
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8747() throws Exception
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

        // TODO: Use: ShareUser.openDocumentLibrary
        // Open document library Documents view.
        docLibPage.getSiteNav().selectSiteDocumentLibrary().render();

        // move the testFolder to moveFolder
        ShareUserSitePage.copyOrMoveToFolder(customDrone, siteName, folderName, moveFolderPath, false);

        // Navigating to movefolder
        docLibPage = ShareUserSitePage.navigateToFolder(customDrone, moveFolderName);

        // Verifying that file is moved successfully.
        Assert.assertTrue(docLibPage.isFileVisible(folderName));
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
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
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8712() throws Exception
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

        // TODO: Use ShareUserSitePage util to uploadNewVersion
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        String version = thisRow.getVersionInfo();

        // Upload New version of the document.
        UpdateFilePage updatePage = thisRow.selectUploadNewVersion().render();
        updatePage.selectMinorVersionChange();
        updatePage.uploadFile(newFileName.getCanonicalPath());
        updatePage.setComment(comment);
        SitePage sitePage = updatePage.submit().render();
        docLibPage = (DocumentLibraryPage) sitePage;
        Assert.assertNotNull(docLibPage);
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        Assert.assertNotEquals(thisRow.getVersionInfo(), version);
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
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
    @Test(groups = "Download", timeOut = 400000)
    public void alf_8745() throws Exception
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

        // TODO: Consider Adding this to SharePage method / ShareUser util, instead of adding to all tests with hardcoded waitime
        // Thread needs to be stopped for some time to make sure the file download stream has been closed properly
        // otherwise download part will not be closed and will throws an exception.
        webDriverWait(customDrone, 3000);

        // Verify the file is downloaded or not.
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(fileName1));
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    /**
     * Test:
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
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8749() throws Exception
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
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // TODO: Missing steps - Uncomment
        if (isAlfrescoVersionCloud(customDrone))
        {
            // Select more , select start workflow
            /*
             * StartWorkFlowPage startWorkFlow = thisRow.selectStartWorkFlow().render();
             * //select New Task
             * NewWorkflowPage newWorkflowPage = startWorkFlow.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();
             * WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
             * formDetails.setDueDate(dueDate);
             * formDetails.setTaskPriority(Priority.MEDIUM);
             * formDetails.setAssignee(testUser);
             * formDetails.setMessage(simpleTaskWF);
             * // Start workflow
             * docLibPage = newWorkflowPage.startWorkflow(formDetails).render();
             * Assert.assertTrue(docLibPage.isFileVisible(fileName1));
             * thisRow = docLibPage.getFileDirectoryInfo(fileName1);
             * DocumentDetailsPage detailsPage = thisRow.selectThumbnail().render();
             * //Verify the document is part of work flow.
             * Assert.assertTrue(detailsPage.isPartOfWorkflow());
             */
        }
        else
        {
            // Select more , select start workflow
            StartWorkFlowPage startWorkFlow = thisRow.selectStartWorkFlow().render();

            // select Adhoc Workflow
            NewWorkflowPage newWorkflowPage = startWorkFlow.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setAssignee(testUser);
            List<String> reviewers = new ArrayList<String>();
            reviewers.add(testUser);
            formDetails.setReviewers(reviewers);
            formDetails.setMessage(simpleTaskWF);

            // Start workflow
            docLibPage = newWorkflowPage.startWorkflow(formDetails).render();
            Assert.assertTrue(docLibPage.isFileVisible(fileName1));

            thisRow = docLibPage.getFileDirectoryInfo(fileName1);
            DocumentDetailsPage detailsPage = thisRow.selectThumbnail().render();

            // Verify the document is part of work flow.
            Assert.assertTrue(detailsPage.isPartOfWorkflow());
        }
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    /**
     * Test: This test includes the 8739/8742
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
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8742() throws Exception
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
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // Click on documetn name link
        DocumentDetailsPage detailsPage = thisRow.clickContentNameFromInfoMenu().render();

        // verify the document details page is opened
        Assert.assertTrue(detailsPage.isDocumentDetailsPage());

        ShareUser.openDocumentLibrary(customDrone);
        // For Folder:
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // verify info button
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // Click on folder name link
        docLibPage = thisRow.clickContentNameFromInfoMenu().render();

        // verify the document details page is opened
        Assert.assertTrue(docLibPage.isDocumentLibrary());
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
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
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8741() throws Exception
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
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // Click on user link
        MyProfilePage profilePage = thisRow.selectModifier().render();

        // verify the user profile page is opened
        Assert.assertTrue(profilePage.getTitle().contains("User Profile Page"));

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // For Folder:
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // verify info button
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // Click on user link
        profilePage = thisRow.selectModifier().render();

        // TODO: Do not hard code. Fix to work with language.property files for localization
        // verify the user profile page is opened
        Assert.assertTrue(profilePage.getTitle().contains("User Profile Page"));
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
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
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8719() throws Exception
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
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // mouse over on documetn link and clicks on edit link
        // Enter the modified name, Click save
        thisRow.renameContent(updatedFileName1);

        thisRow = docLibPage.getFileDirectoryInfo(updatedFileName1);

        // verify the rename is successful
        Assert.assertTrue(thisRow.getContentNameFromInfoMenu().equals(updatedFileName1));

        // mouse over on documetn link and clicks on edit link
        thisRow.contentNameEnableEdit();

        // Enter the modified name
        thisRow.contentNameEnter(notUpdatedfileName1);

        // Click Cancel
        thisRow.contentNameClickCancel();

        // Verify the name is not changed.
        Assert.assertTrue(docLibPage.isFileVisible(updatedFileName1));
        Assert.assertFalse(docLibPage.isFileVisible(notUpdatedfileName1));

        // For Folder:
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // verify info button
        Assert.assertTrue(thisRow.isInfoIconVisible());

        // click on info button
        thisRow.clickInfoIcon();

        Assert.assertTrue(thisRow.isInfoPopUpDisplayed());

        // mouse over on documetn link and clicks on edit link
        // Enter the modified name, Click save
        thisRow.renameContent(updatedFolderName);

        thisRow = docLibPage.getFileDirectoryInfo(updatedFolderName);

        // verify the rename is successful
        Assert.assertTrue(thisRow.getContentNameFromInfoMenu().equals(updatedFolderName));

        // mouse over on documetn link and clicks on edit link
        thisRow.contentNameEnableEdit();

        // Enter the modified name
        thisRow.contentNameEnter(notUpdatedFolderName);

        // Click Cancel
        thisRow.contentNameClickCancel();

        // Verify the file is not found with cancelled name changes.
        Assert.assertTrue(docLibPage.isFileVisible(updatedFolderName));
        Assert.assertFalse(docLibPage.isFileVisible(notUpdatedFolderName));
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    // This test includes 8716/8723
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8723() throws Exception
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

        // TODO: Fix to work with localisation
        String COMMENT_TOOLTIP = "Comment on this Document";

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
        Assert.assertTrue(thisRow.isCommentLinkPresent());
        Assert.assertTrue(thisRow.getCommentsToolTip().equalsIgnoreCase(COMMENT_TOOLTIP));

        // Add the comment
        DocumentDetailsPage detailsPage = thisRow.clickCommentsLink().render();
        detailsPage = detailsPage.addComment(comment).render();

        ShareUser.openDocumentLibrary(customDrone);

        // Verify the comments count is 1 on doclib page.
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        Assert.assertTrue(thisRow.getCommentsCount() == 1);

        // Edit the previous comment and verify the changes
        detailsPage = thisRow.clickCommentsLink().render();
        detailsPage = detailsPage.editComment(comment, newComment).render();
        detailsPage = detailsPage.saveEditComments().render();

        Assert.assertTrue(detailsPage.getComments().contains(newComment));
        Assert.assertFalse(detailsPage.getComments().contains(comment));

        // Edit the previous comment anc cancel the changes,and verify the changes
        detailsPage = detailsPage.editComment(newComment, noNewComment).render();
        detailsPage.cancelEditComments();

        Assert.assertTrue(detailsPage.getComments().contains(newComment));
        Assert.assertFalse(detailsPage.getComments().contains(noNewComment));

        // Remove the comment and verify it is removed successfully.
        detailsPage = detailsPage.removeComment(newComment).render();

        docLibPage = ShareUser.openDocumentLibrary(customDrone);
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        Assert.assertTrue(thisRow.getCommentsCount() == 0);

        // For Folder:
        docLibPage = ShareUser.openDocumentLibrary(customDrone);
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Verify the comments link and tooltip
        Assert.assertTrue(thisRow.isCommentLinkPresent());
        Assert.assertTrue(thisRow.getCommentsToolTip().equalsIgnoreCase(COMMENT_TOOLTIP));

        // Add the comment
        FolderDetailsPage folderDetailsPage = thisRow.clickCommentsLink().render();
        folderDetailsPage = folderDetailsPage.addComment(comment).render();

        ShareUser.openDocumentLibrary(customDrone);

        // Verify the comments count is 1 on doclib page.
        thisRow = docLibPage.getFileDirectoryInfo(folderName);
        Assert.assertTrue(thisRow.getCommentsCount() == 1);

        // Edit the previous comment and verify the changes
        folderDetailsPage = thisRow.clickCommentsLink().render();
        folderDetailsPage = folderDetailsPage.editComment(comment, newComment).render();
        folderDetailsPage = folderDetailsPage.saveEditComments().render();

        Assert.assertTrue(folderDetailsPage.getComments().contains(newComment));
        Assert.assertFalse(folderDetailsPage.getComments().contains(comment));

        // Edit the previous comment anc cancel the changes,and verify the changes
        folderDetailsPage = folderDetailsPage.editComment(newComment, noNewComment).render();
        folderDetailsPage.cancelEditComments();
        folderDetailsPage = folderDetailsPage.render();
        Assert.assertTrue(folderDetailsPage.getComments().contains(newComment));
        Assert.assertFalse(folderDetailsPage.getComments().contains(noNewComment));

        // Remove the comment and verify it is removed successfully.
        folderDetailsPage = folderDetailsPage.removeComment(newComment).render();

        docLibPage = ShareUser.openDocumentLibrary(customDrone);
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);
        Assert.assertTrue(thisRow.getCommentsCount() == 0);
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8728() throws Exception
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
        Assert.assertTrue(htmlSource.contains(content));
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    // This test includes 8666/8729
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8729() throws Exception
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

        // TODO: Use shareUserSite Util
        FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(updatedFileName);

        // Verify the changes are successful.
        EditDocumentPropertiesPage editDocPropertiesPage = thisRow.selectEditProperties().render();

        Assert.assertTrue(editDocPropertiesPage.getName().equals(updatedFileName));
        Assert.assertTrue(editDocPropertiesPage.getDocumentTitle().equals(newTitle));
        Assert.assertTrue(editDocPropertiesPage.getDescription().equals(description));
        editDocPropertiesPage.clickOnCancel();
        // Edit the name,title,description and cancel the changes and verify the changes are not reflected.
        docLibPage = ShareUserSitePage.editProperties(customDrone, updatedFileName, cancelledname, cancelledTitle, cancelledDescription, false);

        Assert.assertFalse(docLibPage.isFileVisible(cancelledname));
        thisRow = docLibPage.getFileDirectoryInfo(updatedFileName);

        // Verify the changes are not successful.
        editDocPropertiesPage = thisRow.selectEditProperties().render();

        Assert.assertFalse(editDocPropertiesPage.getName().equals(cancelledname));
        Assert.assertFalse(editDocPropertiesPage.getDocumentTitle().equals(cancelledTitle));
        Assert.assertFalse(editDocPropertiesPage.getDescription().equals(cancelledDescription));
        editDocPropertiesPage.clickOnCancel();

        // Folder: Edit the name,title,description and save
        docLibPage = ShareUserSitePage.editProperties(customDrone, folderName, updatedFolderName, newTitle, description, true);

        thisRow = docLibPage.getFileDirectoryInfo(updatedFolderName);

        // verify the changes are successful..
        editDocPropertiesPage = thisRow.selectEditProperties().render();

        Assert.assertTrue(editDocPropertiesPage.getName().equals(updatedFolderName));
        Assert.assertTrue(editDocPropertiesPage.getDocumentTitle().equals(newTitle));
        Assert.assertTrue(editDocPropertiesPage.getDescription().equals(description));
        editDocPropertiesPage.clickOnCancel();
        // Edit the name,title,description and cancel the changes and verify the changes are not reflected.
        docLibPage = ShareUserSitePage.editProperties(customDrone, updatedFolderName, cancelledname, cancelledTitle, cancelledDescription, false);

        Assert.assertFalse(docLibPage.isFileVisible(cancelledname));
        thisRow = docLibPage.getFileDirectoryInfo(updatedFolderName);

        // verify the changes are not successful.
        editDocPropertiesPage = thisRow.selectEditProperties().render();

        Assert.assertFalse(editDocPropertiesPage.getName().equals(cancelledname));
        Assert.assertFalse(editDocPropertiesPage.getDocumentTitle().equals(cancelledTitle));
        Assert.assertFalse(editDocPropertiesPage.getDescription().equals(cancelledDescription));
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Open Site Library
        ShareUser.openDocumentLibrary(customDrone);
        ShareUserSitePage.createFolder(customDrone, copyFolderName, "");
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
    @Test(groups = "GalleryView", timeOut = 400000)
    public void alf_8722() throws Exception
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
        Assert.assertTrue(docLibPage.isFileVisible(fileName1));

        docLibPage = ShareUser.openDocumentLibrary(customDrone);

        // Copy the testFolder to copyFolder
        ShareUserSitePage.copyOrMoveToFolder(customDrone, siteName, folder, copyFolderPath, true);

        // Navigating to copied folder
        docLibPage = ShareUserSitePage.navigateToFolder(customDrone, copyFolderName);

        // Verifying that folder is copied successfully.
        Assert.assertTrue(docLibPage.isFileVisible(folder));

        ShareUserSitePage.navigateToFolder(customDrone, folder);

        // Verifying that sub folder is copied successfully.
        Assert.assertTrue(docLibPage.isFileVisible(subFolder));
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
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
    @Test(groups = "DocLibToolBar", timeOut = 400000)
    public void alf_8743() throws Exception
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
        ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).addTag(testFileTagName);


        // Verify the Tags size
        Assert.assertEquals(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).getTags().size(), 1);

        // Clicking the tagName link present under file1 name
        FileDirectoryInfo thisRow = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        thisRow.clickInfoIcon();
        thisRow.clickOnTagNameLink(testFileTagName).render();

        // Check that the file1 is listed
        Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(customDrone, fileName1, true));

        // Clicking the tagName present under Tags menu tree on Document Library page.
        DocumentLibraryPage docLibPage = getSharePage(customDrone).render();
        docLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(testFileTagName).render();

        // Check that the file1 is listed
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
        Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(customDrone, folderName, true));

        // Clicking the tagName present under Tags menu tree on Document Library page.
        docLibPage = docLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(testFolderTagName).render();

        // Check that the file1 is listed
        Assert.assertTrue(docLibPage.isFileVisible(folderName));
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
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
    @Test(groups = "DocLibToolBar", timeOut = 400000)
    public void alf_8721() throws Exception
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
        Assert.assertFalse(docLibPage.isFileVisible(fileName1));

        // Folder:
        thisRow = docLibPage.getFileDirectoryInfo(folderName);

        // Deleting the folder
        docLibPage = thisRow.delete().render();

        // Verify the folder is deleted successfully.
        Assert.assertFalse(docLibPage.isFileVisible(folderName));
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
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
    @Test(groups = "Download", timeOut = 400000)
    public void alf_8664() throws Exception
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

        // TODO: Move to waitForFile or util method as above
        // Thread needs to be stopped for some time to make sure the file download stream has been closed properly
        // otherwise download part will not be closed and will throws an exception.
        webDriverWait(customDrone, 3000);

        // Verify the file is downloaded or not.
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(folderName + ZIP_EXTENSION));
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
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
    @Test(groups = "GalleryView", timeOut = 400000, enabled = false)
    public void alf_8679() throws Exception
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
        // TODO: Replace this statement if already in Selected DocLib. Use ShareUserSitePage.selectView instead - in all methods
        ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);

        // Turn off the "Inherit permissions" option, add any users/groups to "Locally Set Permissions", set any role to users/groups and click "Save" button;
        DocumentLibraryPage docLibPage = (DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(customDrone, accessUser, folderName,
                UserRole.COLLABORATOR, false);

        DetailsPage detailsPage = ShareUserSitePage.getContentDetailsPage(customDrone, folderName);

        Assert.assertTrue(detailsPage.getPermissionsOfDetailsPage().containsKey(accessUser));

        // TODO: Uncomment steps
        // Verify the for Enterprise:
        // The dialog box closes . New permissions are set and are displayed for this folder in Details page
        // for Cloud:
        // The dialog box closes. New permissions are set;
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
    @Test(groups = "DocLibToolBar", timeOut = 400000, enabled = false)
    public void alf_8667() throws Exception
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
        // TODO: Replace this statement if already in Selected DocLib. Use ShareUserSitePage.selectView instead - in all methods
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibraryInGalleryView(customDrone, siteName);
        GalleryViewFileDirectoryInfo thisRow = (GalleryViewFileDirectoryInfo) docLibPage.getFileDirectoryInfo(folderName);

        // click on info button on folder
        // click on More and select manage rules link
        // Click Create Rules
        // Verify The form New rule is present.
        // Fill "New rule" form :
        // enter a name (required) and description (optional) for the rule;
        // select "Copy" in the field "Perform Action";
        // click "Select" to the right of"Copy" and select Site and Path, click "Ok";
        // other fields fill by default.
        // Click "Creat" button in the bottom of a form;
        // Verify that page is return to the Rules page and new rule appears in the left side of the page

    }

    // ALF-8678:Action "Manage Aspects " for folder
    @Test(groups = "DocLibToolBar", timeOut = 400000, enabled = false)
    public void alf_8678() throws Exception
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
        GalleryViewFileDirectoryInfo thisRow = (GalleryViewFileDirectoryInfo) docLibPage.getFileDirectoryInfo(folderName);

        // click on info button on folder
        // click on More and select manage aspects link
        // Select in the "Available to Add" list select the "Classifiable" aspect and click the " +" button .
        // Verify that the "Available to Add" list select the "Classifiable" aspect and click the " +" button .
        // Click "Apply changes"
        // Verify that the "Classifiable" aspects have now been applied to your folder and this is displayed in the Document Details page.
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
    @Test(groups = "DocLibToolBar", timeOut = 400000, enabled = false)
    public void alf_8734() throws Exception
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
        GalleryViewFileDirectoryInfo thisRow = (GalleryViewFileDirectoryInfo) docLibPage.getFileDirectoryInfo(folderName);

        // click on info button folder
        // click on view Details icon
        // verify the below things according to environment specific.
        /*
         * for Enterprise:
         * •Location: path;
         * •Comments part with Add Comment enabled button;
         * •Folder actions: 1.Download as ZIP;
         * 2.Edit properties;
         * 3.Copy to;
         * 4.Move to;
         * 5.Manage Rules;
         * 6.Delete folder;
         * 7.Manage Permissions;
         * 8.Manage Aspects;
         * 9.Change type;
         * 10.View in Alfresco Explorer;
         * •Tags
         * •Share (link to share the current page
         * Properties part:
         * Name:
         * Title:
         * Description:
         * •Sync setting
         * •Permissions setting
         * for cloud:
         * Location: path;
         * Comments part with Add Comment enabled button;
         * Folder actions:
         * Edit properties;
         * Copy to;
         * Move to;
         * Manage Rules;
         * Delete folder;
         * Manage Permissions;
         * Tags
         * Share (link to share the current page
         * Properties part:
         * Name:
         * Title:
         * Description:
         * Sync setting
         */
    }

    // @Test(groups="DataPrepDocLibToolBar", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Creating files.
        ShareUser.uploadFileInFolder(customDrone, testFile1Info);
    }

    // @Test(groups="DocLibToolBar", timeOut = 400000, enabled=false)
    public void alf_8755() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        // String fileName1 = getFileName(testName + "file1");
        // String fileName2 = getFileName(testName + "file2");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // TODO: Method to click on on info icon
        // TODO: Method to click on on share link icon
        // TODO: Method to verify the displayed information
        // TODO: Method to Open the public link
        // TODO: Method to verify the public link item is displayed
        // TODO: Method to share by Email.
        // TODO: Method to verify the message is opened
        // TODO: Method to click on send message
        // TODO: Method to verify the message is sent
        // NOTE: To verify that "The Reciepient recieved the email" step is not possible to automate from alfresco.
        // TODO: Method to verify the email link is provided and provides the correct details to the shared document.
        // TODO: Method to click on share using facebook
        // TODO: Method to verify facebook authorization form
        // TODO: Method to click on share using twitter
        // TODO: Method to verify twitter authorization form
        // NOTE: Need to think about twitter automation step as "Share a link with your followers page is opened. You can click Tweet to post the link"
        // TODO: Method to click on share using google+
        // TODO: Method to verify google+ authorization form
        // NOTE: Need to think about twitter automation step as "Google+ share form is opened. The link is proviced. You can share a link by clicking Share"

    }

    // This test includes 8717 / 8715
    @Test(groups = "GalleryView", timeOut = 400000, enabled = false)
    public void alf_8717() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Select gallery view from options menu.
        // verify gallery view preivew.
        // verify info button on file
        // click on info button
        // verify favorite icon is appeared and message ""Add document to favorites" hint is displayed.
        // select favourite.
        // TODO : Method to select my favourites in doclib page tree menu.
        // verify the favourite doc is displayed.
        // click on info button
        // verify favorite icon color is changed and ""Remove document from favorites " icon is displayed and unselect the favourite
        // Verify that my favourites page should not display the doc.

        // verify info button on folder
        // click on info button
        // verify favorite icon is appeared and message ""Add document to favorites" hint is displayed.
        // select favourite.
        // Method to select my favourites in doclib page tree menu.
        // verify the favourite folder is displayed.
        // click on info button
        // verify favorite icon color is changed and ""Remove document from favorites " icon is displayed and unselect the favourite
        // Verify that my favourites page should not display the folder.
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
    public void dataprep_ALF_8663() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "GalleryView", timeOut = 400000, enabled = false)
    public void alf_8663() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "file1");
        String fileInfo1[] = { fileName1 };

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Select gallery view from options menu.
        // verify gallery view preivew.
        // TODO: Method to verify the zoom control
        // TODO: Method to access the zoom control
        // TODO: Method to minimize/maximize the zoom control
        // TODO: Method to verify the gallery items and maximized and minimized.
    }

    @Test(groups = "DataPrepGalleryView", timeOut = 400000)
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
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    /**
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
    @Test(groups = "GalleryView", timeOut = 400000, enabled = false)
    public void alf_8718() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1") + getRandomStringWithNumders(4);
        String folderName = getFolderName(testName + "2") + getRandomStringWithNumders(3);
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
        Assert.assertTrue(thisRow.getLikeOrUnlikeTip().equals("Like this document"));

        // Select like for the first file
        thisRow.selectLike();
        docLibPage = docLibPage.render();
        thisRow = docLibPage.getFileDirectoryInfo(fileName1);

        // Verify the file is liked and like count as well.
        Assert.assertTrue(thisRow.isLiked());
        Assert.assertTrue(thisRow.getLikeCount().equals("1"));

        // TODO: verify like icon color is changed and unlike icon is displayed and select it.

        // Verify the unlike tool tip.
        Assert.assertTrue(thisRow.getLikeOrUnlikeTip().equals("Unlike"));

        // Verify the like tool tip before liking the folder1
        // Select like on folder1
        // Verify the folder1 is liked and like count.
        // Verify the Unlike tool tip after liking the folder1
    }
}