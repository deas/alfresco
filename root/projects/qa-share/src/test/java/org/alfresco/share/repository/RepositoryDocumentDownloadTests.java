/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.share.repository;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.WebDroneType;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

/**
 * Test document download for repository.
 * 
 * @author jcule
 */
@Listeners(FailedTestListener.class)
public class RepositoryDocumentDownloadTests extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(RepositoryDocumentDownloadTests.class);

    private String customTestUser;
    private static final String USER_HOMES_FOLDER = "User Homes";

    @Override
    @BeforeClass(groups = { "NonGrid" }, alwaysRun = true)
    public void setup() throws Exception
    {
        // create a single user
        super.setup();
        testName = this.getClass().getSimpleName();
        customTestUser = testName + "Custom" + "@" + DOMAIN_FREE;
    }

    /**
     * User logs in before test is executed
     * 
     * @throws Exception
     */

    @BeforeMethod(groups = { "NonGrid" })
    public void customPrepare() throws Exception
    {
        // login as created user
        try
        {
            setupCustomDrone(WebDroneType.DownLoadDrone);
            String[] customTestUserInfo = new String[] { customTestUser };
            CreateUserAPI.createActivateUserAsTenantAdmin(customDrone, ADMIN_USERNAME, customTestUserInfo);

            ShareUser.login(customDrone, customTestUser, DEFAULT_PASSWORD);
            logger.info("Repository user logged in - custom drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    /**
     * User logs out before test is executed
     * 
     * @throws Exception
     */
    @AfterMethod(groups = { "NonGrid" })
    public void customQuit() throws Exception
    {
        // login as created user
        try
        {
            ShareUser.logout(customDrone);
            customDrone.quit();
            logger.info("Repository user logged out - custom drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    /**
     * Downloads the file
     * 1)Create plain text file in users home test folder
     * 2)Download content
     * 3)Check the file is downloaded successfully
     * 
     * @throws Exception
     */

    @Test(groups = { "NonGrid" })
    public void AONE_3590() throws Exception
    {
        // Create plain text file in users home test folder
        String testName = getTestName();
        String fileName = testName + System.currentTimeMillis();

        ShareUserRepositoryPage.openRepositorySimpleView(customDrone);
        String[] folderPath = { USER_HOMES_FOLDER, customTestUser };
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(customDrone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(customDrone, testName, testName, testName);
        }

        // create a plain text file in test folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(testName + " title");
        contentDetails.setDescription(testName + " description");
        contentDetails.setContent(testName + " content");
        ShareUserRepositoryPage.openRepositoryDetailedView(customDrone);
        String[] contentFolderPath = { USER_HOMES_FOLDER, customTestUser, testName };
        repositoryPage = ShareUserRepositoryPage.createContentInFolder(customDrone, contentDetails, ContentType.PLAINTEXT, contentFolderPath);
        Assert.assertTrue(repositoryPage.isFileVisible(fileName));

        // Download file
        FileDirectoryInfo fileDirectoryInfo = repositoryPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectDownload();

        // Check the file is downloaded successfully
        repositoryPage.waitForFile(downloadDirectory + fileName);

        List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        Assert.assertTrue(extractedChildFilesOrFolders.contains(fileName));

    }

    /**
     * Edit content offline
     * 1)Create plain text file in users home test folder
     * 2)Select edit offline
     * 3)Check the file is downloaded successfully
     * 4)Cancel edit offline so that the file/folder can be deleted
     * 
     * @throws Exception
     */

    @Test(groups = { "NonGrid" })
    public void AONE_3591() throws Exception
    {
        // Create plain text file in users home test folder
        String testName = getTestName();
        String fileName = testName + System.currentTimeMillis();

        ShareUserRepositoryPage.openRepositorySimpleView(customDrone);
        String[] folderPath = { USER_HOMES_FOLDER, customTestUser };
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(customDrone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(customDrone, testName, testName, testName);
        }

        // create a plain text file in test folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(testName + " title");
        contentDetails.setDescription(testName + " description");
        contentDetails.setContent(testName + " content");
        ShareUserRepositoryPage.openRepositoryDetailedView(customDrone);
        String[] contentFolderPath = { USER_HOMES_FOLDER, customTestUser, testName };
        repositoryPage = ShareUserRepositoryPage.createContentInFolder(customDrone, contentDetails, ContentType.PLAINTEXT, contentFolderPath);
        Assert.assertTrue(repositoryPage.isFileVisible(fileName));

        // Select edit offline
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(fileName).render();
        DocumentEditOfflinePage editOfflinePage = detailsPage.selectEditOffLine(null).render();
        String editedFileName = fileName + "%2520(Working%2520Copy)";

        // Check the file is downloaded successfully
        repositoryPage.waitForFile(downloadDirectory + editedFileName);
        Assert.assertTrue(editOfflinePage.isCheckedOut());
        List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        Assert.assertTrue(extractedChildFilesOrFolders.contains(editedFileName));

    }

    /**
     * Edit content off line - upload new version - minor changes
     * 1)Create plain text file in users home test folder
     * 2)Select edit offline
     * 3)Upload new version with minor changes
     * 4)Check the file is downloaded successfully
     * 5)Check the version has increased
     * 6)Cancel edit offline so that the file/folder can be deleted
     * 
     * @throws Exception
     */

    @Test(groups = { "NonGrid" })
    public void AONE_3592() throws Exception
    {
        // Create plain text file in users home test folder
        String testName = getTestName();
        String fileName = testName + System.currentTimeMillis();

        ShareUserRepositoryPage.openRepositorySimpleView(customDrone);

        String[] folderPath = { USER_HOMES_FOLDER, customTestUser };
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(customDrone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(customDrone, testName, testName, testName);
        }

        // create a plain text file in test folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(testName + " title");
        contentDetails.setDescription(testName + " description");
        contentDetails.setContent(testName + " content");
        ShareUserRepositoryPage.openRepositoryDetailedView(customDrone);
        String[] contentFolderPath = { USER_HOMES_FOLDER, customTestUser, testName };
        repositoryPage = ShareUserRepositoryPage.createContentInFolder(customDrone, contentDetails, ContentType.PLAINTEXT, contentFolderPath);
        Assert.assertTrue(repositoryPage.isFileVisible(fileName));

        // Select edit offline
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(fileName).render();
        DocumentEditOfflinePage editOfflinePage = detailsPage.selectEditOffLine(null).render();
        String editedFileName = fileName + "%2520(Working%2520Copy)";

        // Check the file is downloaded successfully
        repositoryPage.waitForFile(downloadDirectory + editedFileName);
        Assert.assertTrue(editOfflinePage.isCheckedOut());
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(editedFileName));

        // upload new version with minor changes
        UpdateFilePage updatePage = editOfflinePage.selectUploadNewVersion().render();
        updatePage.selectMinorVersionChange();
        updatePage.uploadFile(downloadDirectory + editedFileName);
        updatePage.setComment("Edit off line - upload new version - minor changes");
        detailsPage = updatePage.submit().render();

        // Check the file is downloaded successfully
        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
        Assert.assertFalse(detailsPage.isCheckedOut());
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(editedFileName));

        // Check the version has increased
        Assert.assertEquals(detailsPage.getDocumentVersion(), "1.1");

    }

    /**
     * Edit content off line - upload new version - major changes
     * 1)Create plain text file in users home test folder
     * 2)Select edit offline
     * 3)Upload new version with major changes
     * 4)Check the file is downloaded successfully
     * 5)Check the version has increased
     * 6)Cancel edit offline so that the file/folder can be deleted
     * 
     * @throws Exception
     */

    @Test(groups = { "NonGrid" })
    public void AONE_3593() throws Exception
    {
        // Create plain text file in users home test folder
        String testName = getTestName();
        String fileName = testName + System.currentTimeMillis();

        ShareUserRepositoryPage.openRepositorySimpleView(customDrone);
        String[] folderPath = { USER_HOMES_FOLDER, customTestUser };
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(customDrone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(customDrone, testName, testName, testName);
        }

        // create a plain text file in test folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(testName + " title");
        contentDetails.setDescription(testName + " description");
        contentDetails.setContent(testName + " content");
        ShareUserRepositoryPage.openRepositoryDetailedView(customDrone);
        String[] contentFolderPath = { USER_HOMES_FOLDER, customTestUser, testName };
        repositoryPage = ShareUserRepositoryPage.createContentInFolder(customDrone, contentDetails, ContentType.PLAINTEXT, contentFolderPath);
        Assert.assertTrue(repositoryPage.isFileVisible(fileName));

        // Select edit offline
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(fileName).render();
        DocumentEditOfflinePage editOfflinePage = detailsPage.selectEditOffLine(null).render();
        String editedFileName = fileName + "%2520(Working%2520Copy)";

        // Check the file is downloaded successfully
        repositoryPage.waitForFile(downloadDirectory + editedFileName);
        Assert.assertTrue(editOfflinePage.isCheckedOut());
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(editedFileName));

        // upload new version with major changes
        UpdateFilePage updatePage = editOfflinePage.selectUploadNewVersion().render();
        updatePage.selectMajorVersionChange();
        updatePage.uploadFile(downloadDirectory + editedFileName);
        updatePage.setComment("Edit off line - upload new version - minor changes");
        detailsPage = updatePage.submit().render();

        // Check the file is downloaded successfully
        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
        Assert.assertFalse(detailsPage.isCheckedOut());
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(editedFileName));

        // Check the version has increased
        Assert.assertEquals(detailsPage.getDocumentVersion(), "2.0");

    }

    /**
     * Edit content off line - cancel
     * 1) Create plain text file in users home test folder
     * 2) Select edit offline
     * 3) Check the file is downloaded successfully
     * 4) Cancel editing offline
     * 5) Check the file is downloaded successfully
     * 6) Check the version has increased
     * 
     * @throws Exception
     */

    @Test(groups = { "NonGrid" })
    public void AONE_3594() throws Exception
    {
        // Create plain text file in users home test folder
        String testName = getTestName();
        String fileName = testName + System.currentTimeMillis();

        ShareUserRepositoryPage.openRepositorySimpleView(customDrone);
        String[] folderPath = { USER_HOMES_FOLDER, customTestUser };
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(customDrone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(customDrone, testName, testName, testName);
        }

        // create a plain text file in test folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(testName + " title");
        contentDetails.setDescription(testName + " description");
        contentDetails.setContent(testName + " content");
        ShareUserRepositoryPage.openRepositoryDetailedView(customDrone);
        String[] contentFolderPath = { USER_HOMES_FOLDER, customTestUser, testName };
        repositoryPage = ShareUserRepositoryPage.createContentInFolder(customDrone, contentDetails, ContentType.PLAINTEXT, contentFolderPath);
        Assert.assertTrue(repositoryPage.isFileVisible(fileName));

        // Select edit offline
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(fileName).render();
        DocumentEditOfflinePage editOfflinePage = detailsPage.selectEditOffLine(null).render();
        String editedFileName = fileName + "%2520(Working%2520Copy)";

        // Check the file is downloaded successfully and the document is edited offline
        repositoryPage.waitForFile(downloadDirectory + editedFileName);
        Assert.assertTrue(editOfflinePage.isCheckedOut());
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(editedFileName));

        // Cancel editing offline
        detailsPage = editOfflinePage.selectCancelEditing().render();

        // Check the file is downloaded successfully
        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
        Assert.assertFalse(detailsPage.isCheckedOut());
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(editedFileName));

        // Check the version has increased
        Assert.assertEquals(detailsPage.getDocumentVersion(), "1.0");

    }

    /**
     * Edit content off line - view original document
     * 1) Create plain text file in users home test folder
     * 2) Select edit offline
     * 3) Check that file can be edited offline
     * 4) Select view original document
     * 5) Check the original document is viewed
     * 6) Cancel edit offline so that the file/folder can be deleted
     * 
     * @throws Exception
     */

    @Test(groups = { "NonGrid" })
    public void AONE_3595() throws Exception
    {

        // Create plain text file in users home test folder
        String testName = getTestName();
        String fileName = testName + System.currentTimeMillis();

        ShareUserRepositoryPage.openRepositorySimpleView(customDrone);
        String[] folderPath = { USER_HOMES_FOLDER, customTestUser };
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(customDrone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(customDrone, testName, testName, testName);
        }

        // create a plain text file in test folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(testName + " title");
        contentDetails.setDescription(testName + " description");
        contentDetails.setContent(testName + " content");
        ShareUserRepositoryPage.openRepositoryDetailedView(customDrone);
        String[] contentFolderPath = { USER_HOMES_FOLDER, customTestUser, testName };
        repositoryPage = ShareUserRepositoryPage.createContentInFolder(customDrone, contentDetails, ContentType.PLAINTEXT, contentFolderPath);
        Assert.assertTrue(repositoryPage.isFileVisible(fileName));

        // Select edit offline
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(fileName).render();
        DocumentEditOfflinePage editOfflinePage = detailsPage.selectEditOffLine(null).render();
        String editedFileName = fileName + "%2520(Working%2520Copy)";

        // Check the file is downloaded successfully
        repositoryPage.waitForFile(downloadDirectory + editedFileName);
        Assert.assertTrue(editOfflinePage.isCheckedOut());
        Assert.assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(editedFileName));

        // Check that file can be edited offline
        Assert.assertTrue(detailsPage.isEditOfflineDisplayed());

        // Select view original document
        editOfflinePage.selectViewOriginalDocument().render();

        // Check the original document is viewed
        Assert.assertTrue(detailsPage.isLockedByYou());

        // Cancel edit offline so that the file/folder can be deleted
        editOfflinePage.selectViewWorkingCopy().render();
        editOfflinePage.selectCancelEditing().render();
        Assert.assertNotNull(detailsPage);
        Assert.assertFalse(detailsPage.isCheckedOut());
        Assert.assertFalse(detailsPage.isEditOfflineDisplayed());

    }

}
