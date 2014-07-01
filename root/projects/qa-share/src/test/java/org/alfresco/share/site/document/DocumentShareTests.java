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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.site.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.ShareLinkPage;
import org.alfresco.po.share.site.document.ViewPublicLinkPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Repository Tests
 * 
 * @author Jamie Allison
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class DocumentShareTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(DocumentShareTests.class);

    protected String testUser;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepAlfrescoOne", "AlfrescoOne" })
    public void dataPrep_ALF_5673() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUser.openDocumentLibrary(drone);
        
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        FolderDetailsPage detailsPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName).selectViewFolderDetails();

        detailsPage.selectLike().render();

        for (int x = 0; x < 11; x++)
        {
            detailsPage = detailsPage.addComment("Comment " + x).render();
        }

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_5673() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create folders and files for the test
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        FolderDetailsPage detailsPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName).selectViewFolderDetails();

        assertTrue(detailsPage.isCorrectPath(folderName), "Folder Path Not as expected");
        assertEquals(detailsPage.getContentTitle(), folderName, "Folder Title Not as expected");
        assertTrue(detailsPage.isModifiedByDetailsPresent(), "Modified By Not as expected");
        assertTrue(detailsPage.isCommentLinkPresent(), "Comment Link is not present");
        assertEquals(detailsPage.getCommentCount(), 11, "Incorrect comment Count: " + detailsPage.getCommentCount());
        assertTrue(detailsPage.isLikeLinkPresent(), "Like Link is not present");
        assertEquals(detailsPage.getLikeCount(), "1", "Like Count not as expected");
        assertTrue(detailsPage.isCommentLinkPresent(), "Comment Link is not present");

        assertFalse(detailsPage.getComments().isEmpty(), "Comments are empty");
        assertTrue(detailsPage.isAddCommentButtonPresent(), "Add Comment button is not present");
        assertTrue(detailsPage.getCommentsPagination().isDisplay(), "Comments paging is not present");
        assertTrue(detailsPage.isTagsPanelPresent(), "Tags Panel is not present");
        assertTrue(detailsPage.isPropertiesPanelPresent(), "Properties Panel is not present");
        assertTrue(detailsPage.isSynPanelPresent(), "Sync Panel is not present");

        if (!isAlfrescoVersionCloud(drone))
        {
            assertTrue(detailsPage.isSharePanePresent(), "Share Panel is not present");
            assertTrue(detailsPage.isPermissionsPanelPresent(), "Permissions Panel is not present");
        }
    }

    @Test(groups = { "DataPrepAlfrescoOne", "AlfrescoOne" })
    public void dataPrep_ALF_8654() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_8654() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + ".txt";
        File file1 = newFile(fileName1, fileName1);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create folders and files for the test
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);

        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file1);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1).clickShareLink().render();

        assertTrue(shareLinkPage.isEmailLinkPresent());
        assertTrue(shareLinkPage.isFaceBookLinkPresent());
        assertTrue(shareLinkPage.isTwitterLinkPresent());
        assertTrue(shareLinkPage.isGooglePlusLinkPresent());

        shareLinkPage.clickFaceBookLink();
        String mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Facebook"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        shareLinkPage.clickTwitterLink();
        mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Share a link on Twitter"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        shareLinkPage.clickGooglePlusLink();
        mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Google+"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        ViewPublicLinkPage viewPublicLinkPage = shareLinkPage.clickViewButton().render();
        assertEquals(viewPublicLinkPage.getContentTitle(), fileName1);
    }

    @Test(groups = { "DataPrepAlfrescoOne", "AlfrescoOne" })
    public void dataPrep_ALF_8655() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_8655() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + ".txt";
        File file1 = newFile(fileName1, fileName1);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create folders and files for the test
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file1);

        ShareUserSitePage.selectView(drone, ViewType.GALLERY_VIEW);

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1).clickShareLink().render();

        assertTrue(shareLinkPage.isEmailLinkPresent());
        assertTrue(shareLinkPage.isFaceBookLinkPresent());
        assertTrue(shareLinkPage.isTwitterLinkPresent());
        assertTrue(shareLinkPage.isGooglePlusLinkPresent());

        shareLinkPage.clickFaceBookLink();
        String mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Facebook"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        shareLinkPage.clickTwitterLink();
        mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Share a link on Twitter"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        shareLinkPage.clickGooglePlusLink();
        mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Google+"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        ViewPublicLinkPage viewPublicLinkPage = shareLinkPage.clickViewButton().render();
        assertEquals(viewPublicLinkPage.getContentTitle(), fileName1);
    }

    @Test(groups = { "DataPrepAlfrescoOne", "AlfrescoOne" })
    public void dataPrep_ALF_8656() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_8656() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + ".txt";
        File file1 = newFile(fileName1, fileName1);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create folders and files for the test
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file1);

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName1);

        ShareLinkPage shareLinkPage = detailsPage.clickShareLink().render();

        assertTrue(shareLinkPage.isEmailLinkPresent());
        assertTrue(shareLinkPage.isFaceBookLinkPresent());
        assertTrue(shareLinkPage.isTwitterLinkPresent());
        assertTrue(shareLinkPage.isGooglePlusLinkPresent());

        shareLinkPage.clickFaceBookLink();
        String mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Facebook"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        shareLinkPage.clickTwitterLink();
        mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Share a link on Twitter"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        shareLinkPage.clickGooglePlusLink();
        mainWindow = drone.getWindowHandle();
        assertTrue(isWindowOpened(drone, "Google+"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);

        ViewPublicLinkPage viewPublicLinkPage = shareLinkPage.clickViewButton().render();
        assertEquals(viewPublicLinkPage.getContentTitle(), fileName1);
    }

    @Test(groups = { "DataPrepAlfrescoOne", "AlfrescoOne" })
    public void dataPrep_ALF_8657() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_8657() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + "_1.txt";
        String fileName2 = getFileName(testName) + System.currentTimeMillis() + "_2.txt";
        String fileName3 = getFileName(testName) + System.currentTimeMillis() + "_3.txt";
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);
        File file3 = newFile(fileName3, fileName3);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create folders and files for the test
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);
        
        ShareUserSitePage.navigateToFolder(drone, folderName);
        
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.uploadFile(drone, file2);
        ShareUserSitePage.uploadFile(drone, file3);
        
        ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);
        
        ShareUserSitePage.getFileDirectoryInfo(drone, fileName1).clickShareLink();
        ShareUserSitePage.getFileDirectoryInfo(drone, fileName2).clickShareLink();
        ShareUserSitePage.getFileDirectoryInfo(drone, fileName3).clickShareLink();

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName1).clickShareLink().render();
        String sharedURL1 = shareLinkPage.getShareURL();

        shareLinkPage.clickOnUnShareButton().render();
        assertFalse(ShareUserSitePage.getFileDirectoryInfo(drone, fileName1).isFileShared());

        ShareUserSitePage.selectView(drone, ViewType.GALLERY_VIEW);

        shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName2).clickShareLink().render();
        String sharedURL2 = shareLinkPage.getShareURL();

        shareLinkPage.clickOnUnShareButton().render();
        assertFalse(ShareUserSitePage.getFileDirectoryInfo(drone, fileName2).isFileShared());

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName3);

        shareLinkPage = detailsPage.clickShareLink().render();
        String sharedURL3 = shareLinkPage.getShareURL();

        detailsPage = shareLinkPage.clickOnUnShareButton().render();
        assertFalse(detailsPage.isFileShared());

        drone.navigateTo(sharedURL1);
        assertTrue(drone.getTitle().contains(drone.getValue("page.not.found.title")), fileName1 + " is still shared from Detailed view.");

        drone.navigateTo(sharedURL2);
        assertTrue(drone.getTitle().contains(drone.getValue("page.not.found.title")), fileName2 + " is still shared from Gallery view.");

        drone.navigateTo(sharedURL3);
        assertTrue(drone.getTitle().contains(drone.getValue("page.not.found.title")), fileName3 + " is still shared from Document Details page.");
    }
}