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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.share.util.AbstractUtils;
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

import java.util.List;

import static org.testng.Assert.*;

/**
 * Test the FilmStrip view of Document Library. Share Refresh > FilmStrip View
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
public class FilmStripDocLibTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(FilmStripDocLibTest.class);

    protected String testUser;
    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        // super.setup();
        super.setupCustomDrone(WebDroneType.DownLoadDrone);
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    /**
     * DataPreparation method - AONE-14042
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14042() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName });
        ShareUser.logout(customDrone);
        //

    }

    /**
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14042() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName).render();

        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());
    }

    /**
     * DataPreparation method - AONE-14043
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
    public void dataPrep_FilmStrip_AONE_14043() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14043() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());

        // The first item is displayed in the foreground
        assertEquals(docLibPage.getFilmstripActions().getDisplyedFilmstripItem(), fileName1);

        // No Metadata displayed
        assertFalse(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).isInfoPopUpDisplayed());

        // There are elements of navigation
        assertTrue(docLibPage.getFilmstripActions().isNextFilmstripArrowPresent());

        // down arrow pointer in the middle
        docLibPage.getFilmstripActions().toggleNavHandleForFilmstrip();

        // It is possible browsing of documents
        docLibPage.getFilmstripActions().selectNextFilmstripItem().render();

        // down arrow pointer in the middle
        docLibPage.getFilmstripActions().toggleNavHandleForFilmstrip();

        assertEquals(docLibPage.getFilmstripActions().getDisplyedFilmstripItem(), fileName2);
    }

    /**
     * DataPreparation method - AONE-14044
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14044() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        // ShareUser.openSiteDashboard(customDrone, siteName);
        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName).render();
        for (int i = 3; i < 5; i++)
        {
            ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 + i });
        }

        for (int i = 0; i < 2; i++)
        {
            documentLibPage = documentLibPage.getNavigation().selectAll().render();

            // Select Copy To
            ShareUserSitePage.copyToActionFromNavigation(customDrone);
        }
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14044() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());

        String firstDiplayedItem = docLibPage.getFilmstripActions().getDisplyedFilmstripItem();

        // Click the right arrow pointer in the foreground;
        docLibPage = docLibPage.getFilmstripActions().selectNextFilmstripItem().render();
        assertTrue(docLibPage.getFilmstripActions().getDisplyedFilmstripItem().contains(testName));
        String secondDisplayedItem = docLibPage.getFilmstripActions().getDisplyedFilmstripItem();

        // Click the left arrow pointer in the foreground;
        docLibPage = docLibPage.getFilmstripActions().selectPreviousFilmstripItem().render();
        assertEquals(docLibPage.getFilmstripActions().getDisplyedFilmstripItem(), firstDiplayedItem);

        // Click on the arbitrary document or folder;
        FileDirectoryInfo fileInfo = docLibPage.getFileDirectoryInfo(secondDisplayedItem);
        docLibPage = fileInfo.selectThumbnail().render();

        assertEquals(docLibPage.getFilmstripActions().getDisplyedFilmstripItem(), secondDisplayedItem);

        // Click the down arrow pointer in the middle for any document or folder;
        docLibPage.getFilmstripActions().toggleNavHandleForFilmstrip();

        assertFalse(docLibPage.getFilmstripActions().isFilmstripTapeDisplpayed());
        docLibPage.getFilmstripActions().toggleNavHandleForFilmstrip();

        // Click the right arrow pointer on the tape;
        assertFalse(docLibPage.getFilmstripActions().isPreviousFilmstripTapeArrowPresent());
        docLibPage = docLibPage.getFilmstripActions().selectNextFilmstripTape().render();
        assertTrue(docLibPage.getFilmstripActions().isPreviousFilmstripTapeArrowPresent());

        // Click the left arrow pointer on the tape;
        assertFalse(docLibPage.getFilmstripActions().isNextFilmstripTapeArrowPresent());
        docLibPage = docLibPage.getFilmstripActions().selectPreviousFilmstripTape().render();
        assertTrue(docLibPage.getFilmstripActions().isNextFilmstripTapeArrowPresent());

        // Select the folder and click on it in the foreground;
        docLibPage = docLibPage.selectFolder(firstDiplayedItem).render();
        assertTrue(docLibPage.isDocumentLibrary());

        // Return to the Document Library page;
        docLibPage = ShareUser.openDocumentLibrary(customDrone);
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());
    }


    /**
     * DataPreparation method - AONE-14047
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14047() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        // ShareUser.openSiteDashboard(customDrone, siteName);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14047() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());
        

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        // Select any item and click the checkbox on the left top corner of the panel;
        fileInfo.selectThumbnail();
        fileInfo.selectCheckbox();
        
        // The checkbox is checked, a blue border is diplayed around the thumbnail;
        List<String> selectFiles = docLibPage.getFilmstripActions().getSelectedFIlesForFilmstrip();
        assertTrue(selectFiles.contains(fileName1), selectFiles.toString());
        assertFalse(selectFiles.contains(fileName2), selectFiles.toString());

        // Select the different item and click the checkbox;
        FileDirectoryInfo fileInfo2 = docLibPage.getFileDirectoryInfo(fileName2);
        // Select any item and click the checkbox on the left top corner of the panel;
        fileInfo2.selectThumbnail();
        fileInfo2.selectCheckbox();

        selectFiles = docLibPage.getFilmstripActions().getSelectedFIlesForFilmstrip();
        assertTrue(selectFiles.contains(fileName1), selectFiles.toString());
        assertTrue(selectFiles.contains(fileName2), selectFiles.toString());
    }
    
    /**
     * DataPreparation method - AONE-14054
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14054() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14054() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());

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
        assertTrue(fileInfo.isSaveLinkVisible());
        assertTrue(fileInfo.isCancelLinkVisible());
        assertTrue(fileInfo.removeTagButtonIsDisplayed(tagName0));

        // Step 4
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.clickOnTagRemoveButton(tagName0);
        List<String> tags = fileInfo.getTags();
        assertFalse(tags.contains(tagName0), tags + " should contain " + testName);

        // Tags added step 5
        String tagName2 = "tag2";
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.enterTagString(tagName2);

        // step 6
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        fileInfo.clickOnTagCancelButton();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        tags = fileInfo.getTags();
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
     * DataPreparation method - AONE-14055
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14055() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14055() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());

        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        String tagName0 = "tag0"+ folderName.substring(folderName.length() -5, folderName.length() -1);
        String tagName1 = "tag1"+ folderName.substring(folderName.length() -5, folderName.length() -2);
        folderInfo.addTag(tagName0);

        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        folderInfo.addTag(tagName1);

        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        // Click the "Edit" icon;
        // Steps 2 and 3
        folderInfo.clickOnAddTag();
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        assertTrue(folderInfo.isSaveLinkVisible());
        assertTrue(folderInfo.isCancelLinkVisible());
        assertTrue(folderInfo.removeTagButtonIsDisplayed(tagName0));
        folderInfo.clickOnTagCancelButton();

        // Step 4, 5 and 6
        String tagName2 = "tag2"+ folderName.substring(folderName.length() -5, folderName.length() -3);
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        folderInfo.clickOnAddTag();
        folderInfo.clickOnTagRemoveButton(tagName0);
        assertFalse(folderInfo.getTags().contains(tagName0), "Taglist - '" + folderInfo.getTags() + "' should contain - " + tagName0);
        folderInfo.enterTagString(tagName2);
     
        folderInfo.clickOnTagCancelButton();
        assertTrue(folderInfo.getTags().contains(tagName0), "Taglist - '" + folderInfo.getTags() + "' should contain - " + tagName0);
        assertTrue(folderInfo.getTags().contains(tagName1), "Taglist - '" + folderInfo.getTags() + "' should contain - " + tagName1);
        assertFalse(folderInfo.getTags().contains(tagName2), "Taglist - '" + folderInfo.getTags() + "' should not contain - " + tagName2);

        // step 7
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        folderInfo.clickOnAddTag();
        folderInfo.clickOnTagRemoveButton(tagName0);
        folderInfo.enterTagString(tagName2);
        folderInfo.clickOnTagSaveButton();
        assertTrue(folderInfo.getTags().contains(tagName2), "Taglist - '" + folderInfo.getTags() + "' should contain - " + tagName2);
        assertTrue(folderInfo.getTags().contains(tagName1), "Taglist - '" + folderInfo.getTags() + "' should contain - " + tagName1);
        assertFalse(folderInfo.getTags().contains(tagName0), "Taglist - '" + folderInfo.getTags() + "' should not contain - " + tagName0);
    }

    /**
     * DataPreparation method - AONE-14048
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14048() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = { "AlfrescoOne", "NonGrid" })
    public void AONE_14048() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());

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
        List<String> selectFiles = docLibPage.getFilmstripActions().getSelectedFIlesForFilmstrip();
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
     * DataPreparation method - AONE-14049
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14049() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

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
    public void AONE_14049() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);
        docLibPage = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).selectThumbnail().render();

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());

        docLibPage = docLibPage.getFilmstripActions().sendKeyRightArrowForFilmstrip().render();
        assertEquals(docLibPage.getFilmstripActions().getDisplyedFilmstripItem(), fileName2);

        docLibPage = docLibPage.getFilmstripActions().sendKeyLeftArrowForFilmstrip().render();
        assertEquals(docLibPage.getFilmstripActions().getDisplyedFilmstripItem(), fileName1);

        // Select document with several pages. Navigate up and down using keys.
        // Cannot be implemented
        docLibPage = docLibPage.getFileDirectoryInfo(fileName2).selectThumbnail().render();

    }

    /**
     * DataPreparation method - AONE-14049
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14051() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14051() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // Rename file name.
        FileDirectoryInfo fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // - Mouse-over on the document's name;
        // Click the "Edit" icon;
        fileInfo1.contentNameEnableEdit();
        assertTrue(fileInfo1.isSaveLinkVisible());
        assertTrue(fileInfo1.isCancelLinkVisible());

        // Type any new name of the document
        fileInfo1.contentNameEnter(fileName1 + " not updated");

        // and click "Cancel" link;
        fileInfo1.contentNameClickCancel();

        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);
        assertTrue(docLibPage.isFileVisible(fileName1));

        fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // - Mouse-over on the document's name, click 'Edit" icon, 
        // type any new name and click "Save" button;
        fileInfo1.renameContent(fileName1 + "-Updated");

        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);
        assertTrue(docLibPage.isFileVisible(fileName1 + "-Updated"));

    }

    /**
     * DataPreparation method - AONE-14049
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14052() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14052() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // Rename file name.
        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        // - Mouse-over on the document's name;
        // Click the "Edit" icon;
        folderInfo.contentNameEnableEdit();
        assertTrue(folderInfo.isSaveLinkVisible());
        assertTrue(folderInfo.isCancelLinkVisible());

        // Type any new name of the document
        folderInfo.contentNameEnter(folderName + " not updated");

        // and click "Cancel" link;
        folderInfo.contentNameClickCancel();

        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);
        assertTrue(docLibPage.isFileVisible(folderName));

        folderInfo = docLibPage.getFileDirectoryInfo(folderName);

        // - Mouse-over on the document's name, click 'Edit" icon,
        // type any new name and click "Save" button;
        folderInfo.renameContent(folderName + "-Updated");

        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);
        assertTrue(docLibPage.isFileVisible(folderName + "-Updated"));

    }

    /**
     * DataPreparation method - AONE-14049
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14056() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.logout(customDrone);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14056() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName + "1");

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

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

        ShareUser.openDocumentLibrary(customDrone);

        fileInfo1 = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        assertTrue(fileInfo1.getCommentsCount() > 0, "Got comments count: " + fileInfo1.getCommentsCount());
    }
    
    /**
     * DataPreparation method - AONE-14049
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14057() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);

        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUser);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        // ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14057() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

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
     * DataPreparation method - AONE-14053
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14053() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = { "AlfrescoOne", "NonGrid" })
    public void AONE_14053() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // Step - 3 "View in Browser"
        // This is covered by unit test. FileDirectoryInfoFilmstripViewTest.testSelectViewInBrowser()

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


    }

    /**
     * DataPreparation method - AONE-14053
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14058() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = { "AlfrescoOne", "NonGrid" })
    public void AONE_14058() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        Thread.sleep(1000);
        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);

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

        // Return to the document's Info panel and click the "Copy to..." action;
        CopyOrMoveContentPage copyToForm = docLibPage.getFileDirectoryInfo(folderName).selectCopyTo().render();
        //CopyOrMoveContentPage copyToForm = folderInfo.selectCopyTo().render();
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
        docLibPage = manageAspect.clickCancel().render();

        folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        FolderRulesPage page = folderInfo.selectManageRules().render();
        assertNotNull(page);
        // Rules page is opened;
        assertTrue(page.isPageCorrect(folderName));
        
        docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);
        folderInfo = docLibPage.getFileDirectoryInfo(folderName);

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
        // folder is downloaded checking here as folder gets time to download //Step 2 assert.
        assertTrue(ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory).contains(folderName + ".zip"));
    }

    /**
     * DataPreparation method - AONE-14059
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14059() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName2 });
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14059() throws Exception
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
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        FileDirectoryInfo folderInfo = docLibPage.getFileDirectoryInfo(folderName);
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        // Select any folder and click the down arrow pointer;
        docLibPage.getFilmstripActions().toggleNavHandleForFilmstrip();
        assertFalse(folderInfo.isCheckBoxVisible());
        assertFalse(folderInfo.isInfoIconVisible());
        assertFalse(docLibPage.getFilmstripActions().isFilmstripTapeDisplpayed());
        assertTrue(docLibPage.getFilmstripActions().isNextFilmstripArrowPresent());

        // Click up arrow pointer;
        docLibPage.getFilmstripActions().toggleNavHandleForFilmstrip();
        assertTrue(folderInfo.isCheckBoxVisible());
        assertTrue(folderInfo.isInfoIconVisible());
        assertTrue(docLibPage.getFilmstripActions().isFilmstripTapeDisplpayed());
        assertTrue(docLibPage.getFilmstripActions().isNextFilmstripArrowPresent());

        // Select any document and click the down arrow pointer;
        fileInfo.selectThumbnail();
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);
        docLibPage.getFilmstripActions().toggleNavHandleForFilmstrip();
        assertFalse(fileInfo.isCheckBoxVisible());
        assertFalse(fileInfo.isInfoIconVisible());
        assertFalse(docLibPage.getFilmstripActions().isFilmstripTapeDisplpayed());
        assertTrue(docLibPage.getFilmstripActions().isNextFilmstripArrowPresent());
        assertTrue(docLibPage.getFilmstripActions().isPreviousFilmstripArrowPresent());

        // Click right/left arrows pointers;
        docLibPage = docLibPage.getFilmstripActions().selectNextFilmstripItem().render();
        assertFalse(docLibPage.getFilmstripActions().isFilmstripTapeDisplpayed());
        assertFalse(docLibPage.getFilmstripActions().isNextFilmstripArrowPresent());
        assertTrue(docLibPage.getFilmstripActions().isPreviousFilmstripArrowPresent());

        // Click up arrow pointer;
        docLibPage.getFilmstripActions().toggleNavHandleForFilmstrip();
        assertTrue(docLibPage.getFilmstripActions().isFilmstripTapeDisplpayed());
        assertFalse(docLibPage.getFilmstripActions().isNextFilmstripArrowPresent());
        assertTrue(docLibPage.getFilmstripActions().isPreviousFilmstripArrowPresent());
    }

    /**
     * DataPreparation method - AONE-14059
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14060() throws Exception
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
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.createFolderInFolder(customDrone, folderName, folderName, DOCLIB_CONTAINER);

        ShareUser.logout(customDrone);

    }

    //TODO: User ShareUSerSitePage.selectView()
    @Test(groups = "AlfrescoOne")
    public void AONE_14060() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Expand the "Options" menu and click the "Filmstrip View" button;
        docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());

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

        // The view is changed to audio view;
        docLibPage = docLibPage.getNavigation().selectAudioView().render();
        assertEquals(docLibPage.getViewType(), ViewType.AUDIO_VIEW);

        // The view is changed to media view;
        docLibPage = docLibPage.getNavigation().selectMediaView().render();
        assertEquals(docLibPage.getViewType(), ViewType.MEDIA_VIEW);
    }


    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_FilmStrip_AONE_14045() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName);
        String[] testUserInfo = new String[] { testUser };
        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload FIles
        ShareUser.uploadFileInFolder(customDrone, new String[] { fileName1 });
        ShareUser.logout(customDrone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14045() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName1 = getFileName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(customDrone, siteName);

        // Navigate to filmstrip view
        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(customDrone, ViewType.FILMSTRIP_VIEW);

        // The view is changed to Filmstrip mode;
        assertTrue(docLibPage.getFilmstripActions().isFilmStripViewDisplayed());

        // The first item is displayed in the foreground
        assertEquals(docLibPage.getFilmstripActions().getDisplyedFilmstripItem(), fileName1);

        // No Metadata displayed
        assertFalse(ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1).isInfoPopUpDisplayed());

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(customDrone, fileName1);

        fileInfo.clickInfoIcon();
        assertTrue(fileInfo.isInfoPopUpDisplayed(), "Info panel isn't shown");

        assertTrue(fileInfo.isVersionVisible(), "An item version isn't present");

        assertTrue(fileInfo.isDownloadPresent(), "Download link isn't present");
        assertTrue(fileInfo.isViewInBrowserVisible(), "View in Browser link  isn't present");
        assertTrue(fileInfo.isEditPropertiesLinkPresent(), "Edit Properties link isn't present");
        assertTrue(fileInfo.isMoreMenuButtonPresent(), "More menu link isn't present");

        List<String> description = fileInfo.getDescriptionList();
        String desc = description.toString();

        assertTrue(desc.contains("ago"), "Created/modified date isn't present");
        assertTrue(desc.contains("bytes"), "Size information isnt't present");
        assertEquals(fileInfo.getDescriptionFromInfo(),"No Description", "Description information isn't present");

        assertTrue(description.contains("No Tags"), "Tags info isn't present");
        assertTrue(description.contains("Favorite"), "Favorite icon isn't present");
        assertTrue(description.contains("Like0"), "Like icon isn't present");

        assertTrue(desc.contains(testUser), "Modifier name isn't present");

        assertTrue(fileInfo.isCommentLinkPresent(), "Comment link isn't present");
        assertTrue(fileInfo.isShareLinkVisible(), "Share link isn't present");

    }

}