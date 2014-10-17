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
import org.alfresco.share.util.AbstractUtils;
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
public class TableViewDocLibTest extends AbstractUtils
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
     * DataPreparation method - AONE_14090
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_AONE_14090() throws Exception
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

        // TODO: Testlink: Remove Precondition : Several folders and documents were created/uploaded is not relevant
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14090() throws Exception
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
     * DataPreparation method - AONE_14091
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
    public void dataPrep_AONE_14091() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload 2 files
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.uploadFile(drone, file2);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14091() throws Exception
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
     * DataPreparation method - AONE_14092
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_AONE_14092() throws Exception
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
    public void AONE_14092() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName1 = "aaaaaaa";
        String fileName2 = "bbbbbbb";
        String fileName1Desc = "aaaa" + System.currentTimeMillis();
        String fileName2Desc = "bbbb" + System.currentTimeMillis();

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

        ShareUserSitePage.editContentProperties(drone, fileName1, fileName1Desc, true);
        ShareUserSitePage.editContentProperties(drone, fileName2, fileName2Desc, true);

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
        // WebDriver sendKeys issue. Complete Path is sent rather than just the filename. Hence the assert had to be amended
        assertTrue(files.get(0).getDescription().endsWith(fileName1Desc));
        assertTrue(files.get(1).getDescription().endsWith(fileName2Desc));

        docLibPage = ShareUserSitePage.sortLibraryOn(drone, SortField.DESCRIPTION, false);
        assertFalse(docLibPage.getNavigation().isSortAscending());

        files = docLibPage.getFiles();
        assertTrue(files.get(0).getDescription().endsWith(fileName2Desc));
        assertTrue(files.get(1).getDescription().endsWith(fileName1Desc));
    }

    /**
     * DataPreparation method - AONE_14093
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_AONE_14093() throws Exception
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
    public void AONE_14093() throws Exception
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
     * DataPreparation method - AONE_14090
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_AONE_14095() throws Exception
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
    public void AONE_14095() throws Exception
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
     * DataPreparation method - AONE_14090
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_AONE_14096() throws Exception
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
    public void AONE_14096() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        String fileName = getFileName(testName) + System.currentTimeMillis();

        // Create max length description
        String tooLongString = getNaturalString(1025);
        String maxLengthString = getResizedString(tooLongString, 1024);

        // Create max length file name.
        String tooLongFileName = getResizedString(fileName, 256);
        String maxLengthFileName = getResizedString(tooLongFileName, 255);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.selectView(drone, ViewType.TABLE_VIEW);

        // TODO: Consider getting column width / supported no of chars for the column for current display / aspect ratio
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(fileName);
        contentDetails.setDescription(fileName);
        contentDetails.setContent("New File!");

        DocumentLibraryPage docLibPage = ShareUser.createContentWithSpecificProps(drone, contentDetails);
        docLibPage = ShareUserSitePage.editProperties(drone, fileName, tooLongFileName, tooLongString, tooLongString, true);

        FileDirectoryInfo fileInfo = docLibPage.getFileDirectoryInfo(maxLengthFileName);

        assertEquals(fileInfo.getName(), maxLengthFileName);
        assertEquals(fileInfo.getDescription(), maxLengthString);
        assertEquals(fileInfo.getTitle(), maxLengthString);
    }

    /**
     * DataPreparation method - AONE_14098
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepTableView", "AlfrescoOne" })
    public void dataPrep_AONE_14098() throws Exception
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
    public void AONE_14098() throws Exception
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
