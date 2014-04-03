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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.WebDroneType;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test the FilmStrip view of Document Library. Share Refresh > FilmStrip View
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
public class FilmStripDocLibTest extends AbstractTests
{
    private static Log logger = LogFactory.getLog(FilmStripDocLibTest.class);

    protected String testUser;
    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        super.setupCustomDrone(WebDroneType.DownLoadDrone);
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    /**
     * DataPreparation method - ALF-14193
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14193() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName });
        ShareUser.logout(customDrone);
        //

    }

    /**
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_14193() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName).render();

        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());
    }

    /**
     * DataPreparation method - ALF-14195
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Upload Files</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14195() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");
        String[] testUserInfo = new String[] { testUser };
        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14195() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Navigate to filmstrip view
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());

        // The first item is displayed in the foreground
        assertEquals(docLibPage.getDisplyedFilmstripItem(), fileName1);

        // No Metadata displayed
        assertFalse(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).isInfoPopUpDisplayed());

        // There are elements of navigation
        assertTrue(docLibPage.isNextFilmstripArrowPresent());

        // down arrow pointer in the middle
        docLibPage.toggleNavHandleForFilmstrip();

        // It is possible browsing of documents
        docLibPage.selectNextFilmstripItem().render();

        // down arrow pointer in the middle
        docLibPage.toggleNavHandleForFilmstrip();

        assertEquals(docLibPage.getDisplyedFilmstripItem(), fileName2);
    }

    /**
     * DataPreparation method - ALF-14196
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14196() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1");
        String folderName = getFolderName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName).render();
        for (int i = 3; i < 5; i++)
        {
            File file = SiteUtil.prepareFile(siteName + i);
            UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
            documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        }

        for (int i = 0; i < 2; i++)
        {
            documentLibPage = documentLibPage.getNavigation().selectAll().render();

            // Select Copy To
            // TODO: write or use util to cpy / move
            CopyOrMoveContentPage copyContent = documentLibPage.getNavigation().selectCopyTo().render();

            // Keep the selected Destination: Current Site > DocumentLibrary Folder
            documentLibPage = copyContent.selectOkButton().render();
        }
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14196() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());

        String firstDiplayedItem = docLibPage.getDisplyedFilmstripItem();

        // Click the right arrow pointer in the foreground;
        docLibPage = docLibPage.selectNextFilmstripItem().render();
        assertTrue(docLibPage.getDisplyedFilmstripItem().contains(testName));
        String secondDisplayedItem = docLibPage.getDisplyedFilmstripItem();

        // Click the left arrow pointer in the foreground;
        docLibPage = docLibPage.selectPreviousFilmstripItem().render();
        assertEquals(docLibPage.getDisplyedFilmstripItem(), firstDiplayedItem);

        // Click on the arbitrary document or folder;
        FileDirectoryInfo fileInfo = docLibPage.getFileDirectoryInfo(secondDisplayedItem);
        docLibPage = fileInfo.selectThumbnail().render();

        assertEquals(docLibPage.getDisplyedFilmstripItem(), secondDisplayedItem);

        // Click the down arrow pointer in the middle for any document or folder;
        docLibPage.toggleNavHandleForFilmstrip();

        assertFalse(docLibPage.isFilmstripTapeDisplpayed());
        docLibPage.toggleNavHandleForFilmstrip();

        // Click the right arrow pointer on the tape;
        docLibPage = docLibPage.selectNextFilmstripTape().render();
        assertTrue(docLibPage.isPreviousFilmstripArrowPresent());

        // Click the left arrow pointer on the tape;
        docLibPage = docLibPage.selectPreviousFilmstripTape().render();
        assertTrue(docLibPage.isNextFilmstripArrowPresent());

        // Select the folder and click on it in the foreground;
        docLibPage = docLibPage.selectFolder(firstDiplayedItem).render();
        assertTrue(docLibPage.isDocumentLibrary());

        // Return to the Document Library page;
        docLibPage = ShareUser.openDocumentLibrary(customDrone);
        assertTrue(docLibPage.isFilmStripViewDisplayed());
    }

    /**
     * DataPreparation method - ALF-14197
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14197() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName + "1");
        String folderName = getFolderName(testName);

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, new String[] { testUser });

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14197() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String folderName = getFolderName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // Verify that only thumbnails of items are displayed without metadata;
        // Gets the list of visible files from thumbnails.
        assertFalse(docLibPage.getFiles().isEmpty(), docLibPage.getFiles().toString());

        // No metadata is displayed, only thumbnails of items are displayed;
        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        assertFalse(folderInfo.isVersionVisible());
        assertFalse(folderInfo.isInfoPopUpDisplayed());

        // Click the "Info" icon on the top right corner of the panel;
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // Item Name with version
        assertTrue(fileInfo.getContentEditInfo().contains(testUser), fileInfo.getContentEditInfo() + " should contain " + testUser);

        // Download icon
        fileInfo.selectDownload();

        // View in browser icon
        fileInfo.clickInfoIcon();
        fileInfo.selectMoreLink();
        assertTrue(fileInfo.isViewInBrowserVisible());

        // Edit Properties icon
        // + More... menu
        fileInfo.clickInfoIcon();
        fileInfo.selectMoreLink();
        assertTrue(fileInfo.isEditPropertiesLinkPresent());

        // Date created/modified and author
        // Size
        // Description
        assertTrue(fileInfo.getDescription().contains(testUser), fileInfo.getDescription() + " should contain " + testUser);

        // Tags
        fileInfo.addTag(testName.toLowerCase());
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        assertTrue(fileInfo.getTags().contains(testName.toLowerCase()), fileInfo.getTags() + " should contain " + testName);

        // Favorite icon and label
        fileInfo.selectFavourite();

        // Like icon and label
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.selectLike();

        // Comment icon and label
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.clickInfoIcon();
        fileInfo.selectMoreLink();
        assertTrue(fileInfo.isCommentLinkPresent(), "comment link should be visible");

        // QuickShare icon
        fileInfo.clickInfoIcon();
        fileInfo.selectMoreLink();
        assertTrue(fileInfo.isShareLinkVisible(), "share link should be visible");
    }

    /**
     * DataPreparation method - ALF-14198
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14198() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileName1 = getFileName(testName);
        String folderName = getFolderName(testName);

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);
    }

    /**
     * TODO - Need to implement video player.
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_14198() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());

        // Verify the ability to play the video file and control the player;

        assertEquals(true, true);
    }

    /**
     * DataPreparation method - ALF-14199
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14199() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");
        String folderName = getFolderName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        // ShareUser.openSiteDashboard(customDrone, siteName);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14199() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());
        

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        // Select any item and click the checkbox on the left top corner of the panel;
        fileInfo.selectThumbnail();
        fileInfo.selectCheckbox();
        
        // The checkbox is checked, a blue border is diplayed around the thumbnail;
        List<String> selectFiles = docLibPage.getSelectedFIlesForFilmstrip();
        assertTrue(selectFiles.contains(fileName1), selectFiles.toString());
        assertFalse(selectFiles.contains(fileName2), selectFiles.toString());

        // Select the different item and click the checkbox;
        FileDirectoryInfo fileInfo2 = docLibPage.getFileDirectoryInfo(fileName2);
        // Select any item and click the checkbox on the left top corner of the panel;
        fileInfo2.selectThumbnail();
        fileInfo2.selectCheckbox();

        selectFiles = docLibPage.getSelectedFIlesForFilmstrip();
        assertTrue(selectFiles.contains(fileName1), selectFiles.toString());
        assertTrue(selectFiles.contains(fileName2), selectFiles.toString());
    }
    
    /**
     * DataPreparation method - ALF-14205
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14205() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName + "1");

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14205() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + System.currentTimeMillis());

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        String tagName0 = "tag0";
        String tagName1 = "tag1";
        fileInfo.addTag(tagName0);

        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.addTag(tagName1);

        // Click the "Edit" icon;
        // Steps 2 and 3
        fileInfo.clickOnAddTag();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        assertTrue(fileInfo.removeTagButtonIsDisplayed(tagName0));

        // Step 4
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.clickOnTagRemoveButton(tagName0);

        // Tags added step 5
        String tagName2 = "tag2";
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.enterTagString(tagName2);

        // step 6
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.clickOnTagCancelButton();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        List<String> tags = fileInfo.getTags();
        assertFalse(tags.contains(tagName2), tags + " should contain " + testName);
        assertTrue(tags.contains(tagName0), tags + " should contain " + testName);
        assertTrue(tags.contains(tagName1), tags + " should contain " + testName);

        // step 7
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.clickOnAddTag();
        fileInfo.clickOnTagRemoveButton(tagName0);
        fileInfo.enterTagString(tagName2);
        fileInfo.clickOnTagSaveButton();
        docLibPage = customDrone.getCurrentPage().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        tags = fileInfo.getTags();
        assertFalse(tags.contains(tagName0), tags + " should contain " + testName);
        assertTrue(tags.contains(tagName2), tags + " should contain " + testName);
        assertTrue(tags.contains(tagName1), tags + " should contain " + testName);
        

    }

    /**
     * DataPreparation method - ALF-14206
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14206() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14206() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName + System.currentTimeMillis());

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());

        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        String tagName0 = "tag0";
        String tagName1 = "tag1";
        folderInfo.addTag(tagName0);

        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        folderInfo.addTag(tagName1);

        // Click the "Edit" icon;
        // Steps 2 and 3
        folderInfo.clickOnAddTag();
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        assertTrue(folderInfo.removeTagButtonIsDisplayed(tagName0));

        // Step 4
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        folderInfo.clickOnTagRemoveButton(tagName0);

        // Tags added step 5
        String tagName2 = "tag2";
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        folderInfo.enterTagString(tagName2);

        // step 6
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        folderInfo.clickOnTagCancelButton();
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        List<String> tags = folderInfo.getTags();
        assertFalse(tags.contains(tagName2), tags + " should contain " + testName);
        assertTrue(tags.contains(tagName0), tags + " should contain " + testName);
        assertTrue(tags.contains(tagName1), tags + " should contain " + testName);

        // step 7
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        folderInfo.clickOnAddTag();
        folderInfo.clickOnTagRemoveButton(tagName0);
        folderInfo.enterTagString(tagName2);
        folderInfo.clickOnTagSaveButton();
        docLibPage = customDrone.getCurrentPage().render();
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        tags = folderInfo.getTags();
        assertFalse(tags.contains(tagName0), tags + " should contain " + testName);
        assertTrue(tags.contains(tagName2), tags + " should contain " + testName);
        assertTrue(tags.contains(tagName1), tags + " should contain " + testName);
    }

    /**
     * DataPreparation method - ALF-14200
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14200() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14200() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");
        String deleteFile = getFileName("delete" + System.currentTimeMillis());

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        ShareUser.uploadFileInFolder(customDrone, new String[] { deleteFile });

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());

        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        FileDirectoryInfo fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        FileDirectoryInfo fileInfo2 = docLibPage.getFileDirectoryInfo(fileName2);

        // Select any item and click the checkbox on the left top corner of the panel;
        // folderInfo.selectThumbnail();
        // folderInfo.selectCheckbox();
        fileInfo1.selectThumbnail();
        fileInfo1.selectCheckbox();
        fileInfo2.selectThumbnail();
        fileInfo2.selectCheckbox();
        //fileInfo2.selectCheckbox();

        if (!isAlfrescoVersionCloud(customDrone))
        {
            // Perform following actions from top nav.
            // Download as ZIP (only for Ent.)
            // docLibPage.getNavigation().clickSelectedItems();
            docLibPage.getNavigation().selectDownloadAsZip();
        }
        // Copy to
        CopyOrMoveContentPage copyOrMoveContentPage = docLibPage.getNavigation().selectCopyTo().render();
        assertEquals(copyOrMoveContentPage.getDialogTitle(), "Copy 2 items to...");
        docLibPage = ((CopyOrMoveContentPage) copyOrMoveContentPage).selectCancelButton().render();

        // Move to
        copyOrMoveContentPage = docLibPage.getNavigation().selectMoveTo().render();
        assertEquals(copyOrMoveContentPage.getDialogTitle(), "Move 2 items to...");
        docLibPage = ((CopyOrMoveContentPage) copyOrMoveContentPage).selectCancelButton().render();

        // Create Task (for Cloud)
        // Start Workflow... (for Ent.)
        StartWorkFlowPage workFlowPage = docLibPage.getNavigation().selectStartWorkFlow();
        if (!isAlfrescoVersionCloud(customDrone))
        {
            assertTrue(workFlowPage.getTitle().contains("Start Workflow"));
        }
        else
        {
            assertTrue(workFlowPage.getTitle().contains("Create Task"));
        }
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName).render();
        
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        folderInfo.selectThumbnail();
        folderInfo.selectCheckbox();
        fileInfo1.selectThumbnail();
        fileInfo1.selectCheckbox();

        // Deselect All
        docLibPage.getNavigation().selectDesellectAll();
        List<String> selectFiles = docLibPage.getSelectedFIlesForFilmstrip();
        assertFalse(selectFiles.contains(fileName1), selectFiles.toString());
        
        // Delete
        FileDirectoryInfo deleteFileInfo = docLibPage.getFileDirectoryInfo(deleteFile);
        deleteFileInfo.selectThumbnail();
        deleteFileInfo.selectCheckbox();
        ConfirmDeletePage deleteConf = docLibPage.getNavigation().selectDelete().render();
        docLibPage = deleteConf.selectAction(Action.Delete).render();
        assertFalse(docLibPage.isFileVisible(deleteFile));
    }

    /**
     * DataPreparation method - ALF-14201
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14201() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.logout(customDrone);

    }

    /**
     * Arrows not being recognised yet.
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne", enabled = true)
    public void ALF_14201() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();
        docLibPage = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).selectThumbnail().render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());

        docLibPage = docLibPage.sendKeyRightArrowForFilmstrip().render();
        assertEquals(docLibPage.getDisplyedFilmstripItem(), fileName2);

        docLibPage = docLibPage.sendKeyLeftArrowForFilmstrip().render();
        assertEquals(docLibPage.getDisplyedFilmstripItem(), fileName1);

        // Select document with several pages. Navigate up and down using keys.
        // Cannot be implemented
        docLibPage = docLibPage.getFileDirectoryInfo(fileName2).selectThumbnail().render();

    }

    /**
     * DataPreparation method - ALF-14201
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14203() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14203() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + System.currentTimeMillis());

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        docLibPage = ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // Rename file name.
        FileDirectoryInfo fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // - Mouse-over on the document's name;
        // Click the "Edit" icon;
        fileInfo1.contentNameEnableEdit();

        // Type any new name of the document
        fileInfo1.contentNameEnter(fileName1 + " not updated");

        // and click "Cancel" link;
        fileInfo1.contentNameClickCancel();

        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();
        assertTrue(docLibPage.isFileVisible(fileName1));

        fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // - Mouse-over on the document's name, click 'Edit" icon, 
        // type any new name and click "Save" button;
        fileInfo1.renameContent(fileName1 + "-Updated");

        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();
        assertTrue(docLibPage.isFileVisible(fileName1 + "-Updated"));

    }

    /**
     * DataPreparation method - ALF-14201
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14204() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName + "1");
        String folderName = getFolderName(testName);

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14204() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName + System.currentTimeMillis());

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // Rename file name.
        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        // - Mouse-over on the document's name;
        // Click the "Edit" icon;
        folderInfo.contentNameEnableEdit();

        // Type any new name of the document
        folderInfo.contentNameEnter(folderName + " not updated");

        // and click "Cancel" link;
        folderInfo.contentNameClickCancel();

        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();
        assertTrue(docLibPage.isFileVisible(folderName));

        folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        // - Mouse-over on the document's name, click 'Edit" icon,
        // type any new name and click "Save" button;
        folderInfo.renameContent(folderName + "-Updated");

        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();
        assertTrue(docLibPage.isFileVisible(folderName + "-Updated"));

    }

    /**
     * DataPreparation method - ALF-14201
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14207() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName + "1");

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.logout(customDrone);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14207() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        FileDirectoryInfo fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // Favorite icon and label
        boolean beforeStatus = fileInfo1.isFavourite();
        fileInfo1.selectFavourite();
        assertTrue(fileInfo1.isFavourite() != beforeStatus);

        // Like icon and label also checks unlinking if doc is liked
        fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        beforeStatus = fileInfo1.isLiked();
        fileInfo1.selectLike();
        assertTrue(fileInfo1.isLiked() != beforeStatus);
        if (fileInfo1.isLiked())
        {
            assertTrue(fileInfo1.getLikeCount().equals("1"));
        }
        else
        {
            assertTrue(fileInfo1.getLikeCount().equals("0"));
        }

        // Type any comment and click the "Add Comment" button;
        HtmlPage docDetailPage = fileInfo1.clickCommentsLink().render();
        docDetailPage = ((DocumentDetailsPage) docDetailPage).addComment("test").render();

        docLibPage = ShareUser.openDocumentLibrary(customDrone);

        fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        assertTrue(fileInfo1.getCommentsCount() > 0, "Got comments count: " + fileInfo1.getCommentsCount());
    }
    
    /**
     * DataPreparation method - ALF-14201
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14208() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        // ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14208() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        // Favorite icon and label
        boolean beforeSelect = folderInfo.isFavourite();
        folderInfo.selectFavourite();
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        assertTrue(folderInfo.isFavourite() != beforeSelect);

        // Like icon and label
        beforeSelect = folderInfo.isLiked();
        folderInfo.selectLike();
        docLibPage = FactorySharePage.resolvePage(customDrone).render();
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        assertTrue(folderInfo.isLiked() != beforeSelect);
        if (folderInfo.isLiked())
        {
            assertTrue(folderInfo.getLikeCount().equals("1"));
        }
        else
        {
            assertTrue(folderInfo.getLikeCount().equals("0"));
        }

        FolderDetailsPage folDetailPage = folderInfo.clickCommentsLink().render();
        folDetailPage = folDetailPage.addComment("test").render();

        docLibPage = ((FolderDetailsPage) folDetailPage).getSiteNav().selectSiteDocumentLibrary().render();

        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        assertTrue(folderInfo.getCommentsCount() == 1);

        docLibPage.getNavigation().selectAll();
        ConfirmDeletePage deleteConf = docLibPage.getNavigation().selectDelete().render();
        deleteConf.selectAction(Action.Delete).render();
        
    }

    /**
     * DataPreparation method - ALF-14210
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14210() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14210() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + System.currentTimeMillis());

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        docLibPage = ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // Click on the document's Info panel icon;
        fileInfo.clickInfoIcon();
        // Info Panel is displayed for the document;
        assertTrue(fileInfo.isInfoPopUpDisplayed());

        // Return to the document's Info panel and click the "Edit Properties" action;
        EditDocumentPropertiesPage propDialog = fileInfo.selectEditProperties().render();
        // "Edit Properties" form is displayed;
        assertTrue(propDialog.isEditPropertiesPopupVisible());
        docLibPage = propDialog.selectCancel().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
         
        // Close the "Edit Properties" form and return to the document's Info panel and click the 'Upload new version' action;
        // fileInfo.clickInfoIcon();
        UpdateFilePage uploadForm = fileInfo.selectUploadNewVersion().render();
        // "Update File" form is displayed;
        uploadForm.selectCancel();
         
        docLibPage = ShareUser.openDocumentLibrary(customDrone);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        // click the "Edit Offline" action;
        docLibPage = fileInfo.selectEditOffline().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // "This document is locked by you for offline editing" message displays above document's name on Info panel;
        assertEquals(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).getContentInfo(), "This document is locked by you for offline editing.",
                "Got content info: " + ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).getContentInfo());
         
        // Return to the document's Info Panel and click the "Cancel Editing" button;
        docLibPage = fileInfo.selectCancelEditing().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        // "Editing has been cancelled" message is displayed;
        assertFalse(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).isEdited());

        // Return to the document's Info panel and click the "Copy to..." action;
        CopyOrMoveContentPage copyToForm = fileInfo.selectCopyTo().render();
        // "Copy %itemname% to..." form is opened;
        assertEquals(copyToForm.getDialogTitle(), "Copy " + fileName1 + " to...");
        docLibPage = copyToForm.selectCancelButton().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
         
        // Close the "Copy %itemname% to..." form, return to the document's Info panel and click the "Move to..." action.
        CopyOrMoveContentPage moveToForm = fileInfo.selectMoveTo().render();
        // "Move %itemname% to..." form is opened;
        assertEquals(moveToForm.getDialogTitle(), "Move " + fileName1 + " to...");
        docLibPage = moveToForm.selectCancelButton().render();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // Close "Move %itemname% to..." form, return to the document's Info panel and click the "Delete Document" action.
        ConfirmDeletePage deleteConf = fileInfo.selectDelete().render();
        // Confirmation about deleting document is displayed;
        docLibPage = deleteConf.selectAction(Action.Cancel).render();
         
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        // "Start workflow" action;
        StartWorkFlowPage workFlowPage = fileInfo.selectStartWorkFlow().render();
        if (!isAlfrescoVersionCloud(customDrone))
        {
            assertTrue(workFlowPage.getTitle().contains("Start Workflow"));
        }
        else
        {
            assertTrue(workFlowPage.getTitle().contains("Create Task"));
        }

        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
         
        // Click the "Cancel", return to the document's Info panel and click 'Manage Permissions' action from the info panel for the document;
        ManagePermissionsPage managePerm = fileInfo.selectManagePermission().render();
        assertTrue(managePerm.getTitle().contains("Manage Permissions"));

        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // Step - 1
        // Click the "Download" action;#
        fileInfo.selectDownload();
        // Download dialog is displayed;
        // Check the file is downloaded successfully
        docLibPage.waitForFile(downloadDirectory + fileName1);
        List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
        assertTrue(extractedChildFilesOrFolders.contains(fileName1));

        // Step - 2
        // Return to the document's Info panel and click the "View in Browser" action;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage1 = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docLibPage1 = docLibPage1.getNavigation().selectFilmstripView().render();
        docLibPage1.getFileDirectoryInfo(fileName1).selectViewInBrowser();
        // The document is opened in browser. If the document cannot be previewed in the browser, download dialog should be displayed;
        // assertTrue(drone.getTitle().contains(fileName1));
        // drone.closeTab();
    }

    /**
     * DataPreparation method - ALF-14210
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14214() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");
        String folderName = getFolderName(testName);

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14214() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName1 = getFileName(testName + "1");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        // Click on the document's Info panel icon;
        folderInfo.clickInfoIcon();
        // Info Panel is displayed for the document;
        assertTrue(folderInfo.isInfoPopUpDisplayed());

        if (!isAlfrescoVersionCloud(customDrone))
        {
            folderInfo = docLibPage.getFileDirectoryInfo(folderName);

            // Click the "Download as Zip" action (only for Ent.); //Step 2
            folderInfo.selectDownloadFolderAsZip();
            docLibPage.waitForFile(downloadDirectory + folderName + ".zip");
        }

        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        // Return to the folder's Info panel and click the "View Details" action;
        FolderDetailsPage folderDetailsPage = folderInfo.selectViewFolderDetails().render();
        assertTrue(folderDetailsPage.isDetailsPage("folder"));

        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        // Return to the document's Info panel and click the "Edit Properties" action;
        EditDocumentPropertiesPage propDialog = folderInfo.selectEditProperties().render();
        // "Edit Properties" form is displayed;
        assertTrue(propDialog.isEditPropertiesPopupVisible());
        docLibPage = propDialog.selectCancel().render();
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        // folder is downloaded checking here as folder gets time to download //Step 2 assert.
        assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(folderName + ".zip"));

        // Return to the document's Info panel and click the "Copy to..." action;
        CopyOrMoveContentPage copyToForm = folderInfo.selectCopyTo().render();
        // "Copy %itemname% to..." form is opened;
        assertEquals(copyToForm.getDialogTitle(), "Copy " + folderName + " to...");
        docLibPage = copyToForm.selectCancelButton().render();
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        // Close the "Copy %itemname% to..." form, return to the document's Info panel and click the "Move to..." action.
        CopyOrMoveContentPage moveToForm = folderInfo.selectMoveTo().render();
        // "Move %itemname% to..." form is opened;
        assertEquals(moveToForm.getDialogTitle(), "Move " + folderName + " to...");
        docLibPage = moveToForm.selectCancelButton().render();
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        // Close "Move %itemname% to..." form, return to the document's Info panel and click the "Delete Document" action.
        ConfirmDeletePage deleteConf = folderInfo.selectDelete().render();
        // Confirmation about deleting document is displayed;
        deleteConf.selectAction(Action.Cancel);

        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        // Click the "Cancel", return to the document's Info panel and click 'Manage Permissions' action from the info panel for the document;
        ManagePermissionsPage managePerm = folderInfo.selectManagePermission().render();
        assertTrue(managePerm.getTitle().contains("Manage Permissions"));

        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        // Click the "Cancel", return to the document's Info panel and click 'Manage Permissions' action from the info panel for the document;
        SelectAspectsPage manageAspect = folderInfo.selectManageAspects().render();
        assertTrue(manageAspect.getTitle().contains(folderName), manageAspect.getTitle());
    }

    /**
     * DataPreparation method - ALF-14228
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14228() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");
        String folderName = getFolderName(testName);

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14228() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName1 = getFileName(testName + "1");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // Select any folder and click the down arrow pointer;
        docLibPage.toggleNavHandleForFilmstrip();
        assertFalse(folderInfo.isCheckBoxVisible());
        assertFalse(folderInfo.isInfoIconVisible());
        assertFalse(docLibPage.isFilmstripTapeDisplpayed());
        assertTrue(docLibPage.isNextFilmstripArrowPresent());

        // Click up arrow pointer;
        docLibPage.toggleNavHandleForFilmstrip();
        assertTrue(folderInfo.isCheckBoxVisible());
        assertTrue(folderInfo.isInfoIconVisible());
        assertTrue(docLibPage.isFilmstripTapeDisplpayed());
        assertTrue(docLibPage.isNextFilmstripArrowPresent());

        // Select any document and click the down arrow pointer;
        fileInfo.selectThumbnail();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        docLibPage.toggleNavHandleForFilmstrip();
        assertFalse(fileInfo.isCheckBoxVisible());
        assertFalse(fileInfo.isInfoIconVisible());
        assertFalse(docLibPage.isFilmstripTapeDisplpayed());
        assertTrue(docLibPage.isNextFilmstripArrowPresent());
        assertTrue(docLibPage.isPreviousFilmstripArrowPresent());

        // Click right/left arrows pointers;
        docLibPage = docLibPage.selectNextFilmstripItem().render();
        assertFalse(docLibPage.isFilmstripTapeDisplpayed());
        assertFalse(docLibPage.isNextFilmstripArrowPresent());
        assertTrue(docLibPage.isPreviousFilmstripArrowPresent());

        // Click up arrow pointer;
        docLibPage.toggleNavHandleForFilmstrip();
        assertTrue(docLibPage.isFilmstripTapeDisplpayed());
        assertFalse(docLibPage.isNextFilmstripArrowPresent());
        assertTrue(docLibPage.isPreviousFilmstripArrowPresent());
    }

    /**
     * DataPreparation method - ALF-14228
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14233() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);

        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void ALF_14233() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());

        // The view is changed to simple view;
        docLibPage = docLibPage.getNavigation().selectSimpleView().render();
        assertEquals(docLibPage.getViewType(), ViewType.SIMPLE_VIEW);

        // The view is changed to gallery view;
        docLibPage = docLibPage.getNavigation().selectGalleryView().render();
        assertEquals(docLibPage.getViewType(), ViewType.GALLERY_VIEW);

        // The view is changed to table view;
        docLibPage = docLibPage.getNavigation().selectTableView().render();
        assertEquals(docLibPage.getViewType(), ViewType.TABLE_VIEW);

        // The view is changed to detailed view;
        docLibPage = docLibPage.getNavigation().selectDetailedView().render();
        assertEquals(docLibPage.getViewType(), ViewType.DETAILED_VIEW);
    }

    /**
     * DataPreparation method - ALF-14234
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_ALF_14234() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(customDrone);

    }

    // Waiting for webdrone implementation of checking icons.
    @Test(groups = "AlfrescoOne", enabled = false)
    public void ALF_14234() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        // String fileName1 = getFileName(testName + "1");
        // String fileName2 = getFileName(testName + "2");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW).render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.isFilmStripViewDisplayed());

        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        folderInfo.selectThumbnail();

        // We are checking this using the class folder which creates the folder icon.
        assertTrue(folderInfo.isFolder());

    }

}