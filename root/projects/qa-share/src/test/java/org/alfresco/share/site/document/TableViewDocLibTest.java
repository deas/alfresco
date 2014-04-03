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
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.SortField;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Jamie Allison
 * @since 4.3.0
 */

@Listeners(FailedTestListener.class)
public class TableViewDocLibTest extends AbstractTests
{
    private static Log logger = LogFactory.getLog(TableViewDocLibTest.class);

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

    /**
     * DataPreparation method - Alf_14378
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_Alf_14378() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // TODO: Testlink: Remove Precondition : Several folders and documents were created/uploaded is not relevant
    }

    @Test(groups = "AlfrescoOne")
    public void Alf_14378() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(drone, ViewType.TABLE_VIEW);

        assertEquals(ViewType.TABLE_VIEW, docLibPage.getNavigation().getViewType());
    }

    /**
     * DataPreparation method - Alf_14379
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create 2 Files</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_Alf_14379() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName1 = getFileName(testName) + "-1";
        String fileName2 = getFileName(testName) + "-2";
        String[] testUserInfo = new String[] { testUser };

        File file1 = newFile(fileName1, "New file 1");
        File file2 = newFile(fileName2, "New file 2");

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Upload 2 files
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.uploadFile(drone, file2);
    }

    @Test(groups = "AlfrescoOne")
    public void Alf_14379() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(drone, ViewType.TABLE_VIEW);

        int fileCount = docLibPage.getFiles().size();

        assertEquals(fileCount, 2);
    }

    /**
     * DataPreparation method - Alf_14392
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_Alf_14392() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "AlfrescoOne")
    public void Alf_14392() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName1 = "aaaaaaa";
        String fileName2 = "bbbbbbb";

        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName1);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create folders and files for the test
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.uploadFile(drone, file2);

        ShareUserSitePage.editContentProperties(drone, fileName1, fileName1, true);
        ShareUserSitePage.editContentProperties(drone, fileName2, fileName2, true);

        // Start Test
        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(drone, ViewType.TABLE_VIEW);

        // TODO: Testlink steps missing. Steps say Click on column names to sort

        // TODO: Testlink: Consider checking sorting works via sort options in TableView in the same test
        docLibPage = ShareUserSitePage.sortLibraryOn(drone, SortField.NAME, true);
        assertEquals(docLibPage.getNavigation().getCurrentSortField(), SortField.NAME);
        assertTrue(docLibPage.getNavigation().isSortAscending());

        List<FileDirectoryInfo> files = docLibPage.getFiles();
        assertEquals(files.get(0).getName(), fileName1);
        assertEquals(files.get(1).getName(), fileName2);

        docLibPage = ShareUserSitePage.sortLibraryOn(drone, SortField.NAME, false);
        assertFalse(docLibPage.getNavigation().isSortAscending());

        files = docLibPage.getFiles();
        assertEquals(files.get(0).getName(), fileName2);
        assertEquals(files.get(1).getName(), fileName1);

        docLibPage = ShareUserSitePage.sortLibraryOn(drone, SortField.DESCRIPTION, true);
        assertEquals(docLibPage.getNavigation().getCurrentSortField(), SortField.DESCRIPTION);
        assertTrue(docLibPage.getNavigation().isSortAscending());

        files = docLibPage.getFiles();
        assertEquals(files.get(0).getDescription(), fileName1);
        assertEquals(files.get(1).getDescription(), fileName2);

        docLibPage = ShareUserSitePage.sortLibraryOn(drone, SortField.DESCRIPTION, false);
        assertFalse(docLibPage.getNavigation().isSortAscending());

        files = docLibPage.getFiles();
        assertEquals(files.get(0).getDescription(), fileName2);
        assertEquals(files.get(1).getDescription(), fileName1);
    }

    /**
     * DataPreparation method - Alf_14544
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_Alf_14544() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "AlfrescoOne")
    public void Alf_14544() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String newFileName = fileName + "_updated";

        File file1 = newFile(fileName, "New file");

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create folders and files for the test
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.uploadFile(drone, file1);

        // Start test
        ShareUserSitePage.selectView(drone, ViewType.TABLE_VIEW);

        ShareUserSitePage.editContentNameInline(drone, fileName, newFileName, false);
        FileDirectoryInfo fileDirInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);
        assertEquals(fileDirInfo.getName(), fileName);

        ShareUserSitePage.editContentNameInline(drone, fileName, newFileName, true);
        fileDirInfo = ShareUserSitePage.getFileDirectoryInfo(drone, newFileName);
        assertEquals(fileDirInfo.getName(), newFileName);
    }

    /**
     * DataPreparation method - Alf_14378
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_Alf_14548() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "AlfrescoOne")
    public void Alf_14548() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String subFolderName1 = "folder 1";
        String subFolderName2 = "folder 2";
        String fileName = getFileName(testName) + System.currentTimeMillis();

        File file1 = newFile(fileName, "New file");

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create folders and files for the test
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);
        ShareUserSitePage.createFolder(drone, folderName, folderName).render();
        ShareUserSitePage.navigateToFolder(drone, folderName).render();
        ShareUserSitePage.createFolder(drone, subFolderName1, subFolderName1);
        ShareUserSitePage.createFolder(drone, subFolderName2, subFolderName2);

        ShareUserSitePage.navigateToFolder(drone, folderName + SLASH + subFolderName1);
        ShareUserSitePage.uploadFile(drone, file1);

        ShareUserSitePage.navigateToFolder(drone, folderName + SLASH + subFolderName2);
        ShareUserSitePage.uploadFile(drone, file1);

        // Start test
        ShareUserSitePage.navigateToFolder(drone, folderName + SLASH + subFolderName1);
        DocumentLibraryPage docLibPage = ShareUserSitePage.selectView(drone, ViewType.TABLE_VIEW);

        assertTrue(docLibPage.getNavigation().isSetDefaultViewVisible());

        docLibPage = docLibPage.getNavigation().selectSetCurrentViewToDefault().render();

        assertTrue(docLibPage.getNavigation().isRemoveDefaultViewVisible());
        assertFalse(docLibPage.getNavigation().isSetDefaultViewVisible());

        docLibPage = ShareUserSitePage.navigateToFolder(drone, folderName + SLASH + subFolderName2);
        assertTrue(docLibPage.getNavigation().isSetDefaultViewVisible());

        docLibPage = ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);

        docLibPage = ShareUserSitePage.navigateToFolder(drone, folderName + SLASH + subFolderName1);
        assertEquals(docLibPage.getNavigation().getViewType(), ViewType.TABLE_VIEW);

        assertTrue(docLibPage.getNavigation().isRemoveDefaultViewVisible());
        assertFalse(docLibPage.getNavigation().isSetDefaultViewVisible());

        docLibPage = docLibPage.getNavigation().selectRemoveCurrentViewFromDefault().render();
        assertTrue(docLibPage.getNavigation().isSetDefaultViewVisible());
        assertFalse(docLibPage.getNavigation().isRemoveDefaultViewVisible());
    }

    /**
     * DataPreparation method - Alf_14378
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_Alf_14549() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "AlfrescoOne")
    public void Alf_14549() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // Create max length file name.
        StringBuffer longString = new StringBuffer("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        longString.append("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        String fileName = getFileName(testName) + System.currentTimeMillis();

        String maxLenghtFileName = longString.replace(0, fileName.length(), fileName).toString();

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.selectView(drone, ViewType.TABLE_VIEW);

        // TODO: Consider getting column width / supported no of chars for the column for current display / aspect ratio
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(maxLenghtFileName);
        contentDetails.setTitle(fileName);
        contentDetails.setDescription(maxLenghtFileName);
        contentDetails.setContent("New File!");

        DocumentLibraryPage docLibPage = ShareUser.createContentWithSpecificProps(drone, contentDetails);

        FileDirectoryInfo fileInfo = docLibPage.getFileDirectoryInfo(maxLenghtFileName);

        assertEquals(fileInfo.getName(), maxLenghtFileName);
        assertEquals(fileInfo.getDescription(), maxLenghtFileName);
    }

    /**
     * DataPreparation method - Alf_14552
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_Alf_14552() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "AlfrescoOne")
    public void Alf_14552() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String subFolderName1 = "folder 1";
        String subFolderName2 = "folder 2";
        String fileName = getFileName(testName);

        File file = newFile(fileName, "New file");

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);

        ShareUserSitePage.createFolder(drone, folderName, folderName);

        ShareUserSitePage.navigateToFolder(drone, folderName);
        ShareUserSitePage.createFolder(drone, subFolderName1, subFolderName1);
        ShareUserSitePage.createFolder(drone, subFolderName2, subFolderName2);

        ShareUserSitePage.navigateToFolder(drone, folderName + SLASH + subFolderName1);
        ShareUserSitePage.uploadFile(drone, file);

        ShareUserSitePage.navigateToFolder(drone, folderName + SLASH + subFolderName2);
        ShareUserSitePage.uploadFile(drone, file);

        ShareUserSitePage.selectView(drone, ViewType.TABLE_VIEW);
        DocumentLibraryPage docLibPage = ShareUserSitePage.navigateToFolder(drone, folderName + SLASH + subFolderName1);

        assertEquals(docLibPage.getNavigation().getViewType(), ViewType.TABLE_VIEW);
    }
}
