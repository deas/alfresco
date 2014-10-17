/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.share.repository;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author cganesh
 *
 */
@Listeners(FailedTestListener.class)
public class RepositoryFolderTests3 extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryFolderTests3.class);

    protected String testUser;
    protected String testUserPass = DEFAULT_PASSWORD;
    protected String baseFolderName;
    protected String baseFolderPath;
    protected String description = "Base folder for FolderTests";

    /**
     * Class includes: Tests from TestLink in Area: Repository Tests
     * <ul>
     * <li>Test User logged in Navigates to repository</li>
     * <li>Test Logged user can create new folder in main page of repository</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        logger.info("[Suite ] : Start Tests in: " + testName);
        
        // login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        
        dataPrepRepositoryFolderTests(testName);
        
        ShareUtil.logout(drone);

    }

    private void dataPrepRepositoryFolderTests(String testName) throws Exception
    {
        baseFolderName = getFolderName(testName);

        String baseFolderTitle = "Base folder for FolderTests";
        String description = "Base folder for FolderTests";

        baseFolderPath = REPO + SLASH + baseFolderName;

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        if (!repositorypage.isFileVisible(baseFolderName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, baseFolderName, baseFolderTitle, description);
        }
    }

    /**
     * Test:
     * <ul>
     * <li>Select any folder with tag value</li>
     * <li>Select delete from list of actions</li>
     * <li>Click delete on confirmation pop up</li>
     * <li>Verify tag scope is decreased after delete</li>
     * </ul>
     */
    @Test(groups = { "Repository" }, enabled = false)
    public void AONE_3563()
    {
        String testName = getTestName();
        
        String randomTestName = getFolderName(testName) + System.currentTimeMillis();

        String folderName = randomTestName + "1";

        try
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // Navigate to repository page
            RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

            // Create new folder
            String[] folderPath = { baseFolderName };
            ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);            
            ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

            // verify created folder is present in the main repository
            Assert.assertTrue(repositorypage.isFileVisible(folderName), "verifying folder present in repository");

            // Add tag to the folder in repository
            ShareUserRepositoryPage.addTagsInRepo(drone, folderName, Arrays.asList(randomTestName));

            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            ShareUserRepositoryPage.openRepositorySimpleView(drone).render();
            repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath).render();
            List<String> tags =  repositorypage.getAllTagNames();
            Assert.assertTrue(tags.contains(randomTestName.toLowerCase()));

            // Select folder and and click on delete folder from More actions
            ConfirmDeletePage conformDeletePage = repositorypage.getFileDirectoryInfo(folderName).selectDelete().render();

            // Select delete on confirm delete page
            repositorypage = conformDeletePage.selectAction(Action.Delete).render();

            // Verify deleted folder is not present in repository
            Assert.assertFalse(repositorypage.isFileVisible(folderName), "verifying folder not present in repository");

            // Verify tag scope is decreased in repository page after folder is deleted
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            ShareUserRepositoryPage.openRepositorySimpleView(drone).render();
            repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath).render();
            tags.clear();
            tags =  repositorypage.getAllTagNames();
            Assert.assertFalse(tags.contains(randomTestName.toLowerCase()));

            ShareUser.logout(drone);
        }
        catch (Exception e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test:
     * <ul>
     * <li>Select any folder with tag value</li>
     * <li>Select delete from list of actions</li>
     * <li>Click cancel on confirmation pop up</li>
     * <li>Verify folder is not deleted</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void AONE_3564()
    {
        String testName = getTestName();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String description = testName + System.currentTimeMillis();

        try
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // Navigate to repository page
            RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

            // Create new folder
            String[] folderPath = { baseFolderName };
            ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);            
            ShareUserRepositoryPage.createFolderInRepository(drone, folderName, description);

            // verify created folder is present in the main repository
            Assert.assertTrue(repositorypage.isFileVisible(folderName), "verifying folder present in repository");

            // Select folder and and click on delete folder from More actions
            ConfirmDeletePage conformDeletePage = repositorypage.getFileDirectoryInfo(folderName).selectDelete().render();

            // Select delete on confirm delete page
            conformDeletePage.selectAction(Action.Cancel);

            // Verify deleted folder is present in repository
            Assert.assertTrue(repositorypage.isFileVisible(folderName), "verifying folder present in repository");

            ShareUser.logout(drone);

        }
        catch (Throwable e)
        {

            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_AONE_3565() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName);
        String testUser2 = getUserNameFreeDomain(testName + "_2");

        // Create User by adding to Alfresco_Administrators group
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUser2);

        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Select More options for any folder in repository</li>
     * <li>Click on Manage Permissions from More options</li>
     * <li>Verify options Inherit permissions, Inherit Permissions list, Locally set permissions panels are displayed</li>
     * <li>Change privileges for any group and save</li>
     * <li>Verify group privileges are saved</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void AONE_3565()
    {
        String testName = getTestName();

        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();

        String Title = "Manage Permissions";
        String GroupName = "EVERYONE";

        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser1 = getUserNameFreeDomain(testName);
        String testUser2 = getUserNameFreeDomain(testName + "_2");

        try
        {

            /** Test Steps */
            ShareUser.login(drone, testUser1, testUserPass);

            // Navigate to repository page
            RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

            // Create new folder
            String[] folderPath = { baseFolderName };
            ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);            
            ShareUserRepositoryPage.createFolderInRepository(drone, folder1, description);

            // verify created folders are present in the main repository
            Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");

            // Select more options in folder1 and click on Manage permissions (this verify Add user / group button, Inherit permissions button, Save and Cancel
            // buttons)
            ManagePermissionsPage managePermissionsPage = repositorypage.getFileDirectoryInfo(folder1).selectManagePermission().render();

            // Verify Manage Permissions page is displayed
            Assert.assertTrue(managePermissionsPage.render().isTitlePresent(Title), "Verify manage permissions page is displayed");

            // Verify Inherit permissions options in Manage Permissions page
            Assert.assertTrue(managePermissionsPage.isInheritPermissionEnabled());

            // Verify Inherit permissions options in Manage Permissions page
            Assert.assertTrue(managePermissionsPage.isInheritPermissionEnabled());

            // Verify Locally Set Permissions list
            Assert.assertTrue(managePermissionsPage.isLocallyPermissionEnabled());

            // Add group, save and return to repository page
            ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, GroupName, false, UserRole.COLLABORATOR, false);

            // Logout as user1
            ShareUtil.logout(drone);

            // Login as testuser2
            ShareUser.login(drone, testUser2, testUserPass);

            // Navigate to repository page
            ShareUserRepositoryPage.openRepositorySimpleView(drone);

            // Verify privileges are changed successfully
            repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, baseFolderName);
            ManagePermissionsPage managepermissionsPage = repositorypage.getFileDirectoryInfo(folder1).selectManagePermission().render();
            Assert.assertEquals(managepermissionsPage.getExistingPermission(GroupName), UserRole.COLLABORATOR, "Verify user role is changed");

            ShareUser.logout(drone);
        }
        catch (Exception e)
        {
            reportError(drone, testName, e);
        }
    }
}
