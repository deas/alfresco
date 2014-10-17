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
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.ShareUserRepositoryPage.Operation;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;
import static org.testng.Assert.*;

/**
 * @author jcule
 */
@Listeners(FailedTestListener.class)
public class RepositoryFolderTests1 extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryFolderTests1.class);

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
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        logger.info("[Suite ] : Start Tests in: " + testName);
        dataPrepRepositoryFolderTests(testName);
    }

    private void dataPrepRepositoryFolderTests(String testName) throws Exception
    {
        String testUser = getUserNameFreeDomain(testName);
        baseFolderName = getFolderName(testName);
        String baseFolderTitle = "Base folder for FolderTests";
        String description = "Base folder for FolderTests";
        String[] testUserInfo = new String[] { testUser };
        baseFolderPath = REPO + SLASH + baseFolderName;

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        if (!repositorypage.isFileVisible(baseFolderName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, baseFolderName, baseFolderTitle, description);
        }

        // Create Admin User.
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>View folder details of selected folder in main page of repository</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void AONE_3552() throws Exception
    {
        String testName = getTestName();

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String folderTitle = (testName) + System.currentTimeMillis();
        String description = (testName) + System.currentTimeMillis();

        // Login
        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepository(drone);

        // Create new folder
        RepositoryPage repositorypage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderTitle, description);

        // verify created folder is present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folderName), "verifying folder present in repository");

        // Select folder and view folder details
        FolderDetailsPage folderDetailsPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName).selectViewFolderDetails().render();

        Assert.assertTrue(folderDetailsPage.getTitle().contains("Folder Details"), "Failed to open 'Folder Details' page");

        // 'Details' page includes three sections: Add Comment , Properties, Folder Actions and Share.
        // verify that Comment section present
        Assert.assertTrue(folderDetailsPage.isCommentSectionPresent(), "Comment section is not present on the folder's details page");
        Assert.assertTrue(folderDetailsPage.isAddCommentButtonPresent(), "'Add Comment' button is not dispalyed in the Comment section");

        // verify that Properties section is displayed
        Assert.assertTrue(folderDetailsPage.isPropertiesPanelPresent(), "Properties section is not displayed on the folder's details page");

        // verify Folder Action section
        Assert.assertTrue(folderDetailsPage.isFolderActionsPresent(), "'Folder Actions' section is not present on the details page");

        // Verify share panel is displayed
        Assert.assertTrue(folderDetailsPage.isSharePanePresent(), "Verifying Share pane is present");

        // Properties includes: Name, Title, Description
        Assert.assertTrue(folderDetailsPage.isPropertiesLabelsPresent(), "Labels of Properties section display incorrectly");

        // verify Actions sections:
        // Verify folder action is displayed
        Assert.assertTrue(folderDetailsPage.isDownloadAsZipAtTopRight(), "Verifying download as zip action is present");

        // Verify Copy To link is present
        Assert.assertTrue(folderDetailsPage.isDocumentActionPresent(DocumentAction.COPY_TO), "Verifying copy to  is present");

        // Verify Move To link is present
        Assert.assertTrue(folderDetailsPage.isDocumentActionPresent(DocumentAction.MOVE_TO), "Verifying move to is present");

        // Verify Delete folder link is present
        Assert.assertTrue(folderDetailsPage.isDocumentActionPresent(DocumentAction.DELETE_CONTENT), "Verifying delete folder is present");

        // Verify Manage Permission folder link is present
        Assert.assertTrue(folderDetailsPage.isDocumentActionPresent(DocumentAction.MANAGE_PERMISSION_REPO), "Verifying manage permission is present");

        // Verify Manage Aspect folder link is present
        Assert.assertTrue(folderDetailsPage.isDocumentActionPresent(DocumentAction.MANAGE_RULES), "Verifying manage rules is present");

        // Verify Change type folder link is present
        Assert.assertTrue(folderDetailsPage.isDocumentActionPresent(DocumentAction.CHNAGE_TYPE), "Verifying change type is present");

        Assert.assertTrue(folderDetailsPage.isDocumentActionPresent(DocumentAction.EDIT_PROPERTIES), "Verifying change type is present");

        Assert.assertTrue(folderDetailsPage.isDocumentActionPresent(DocumentAction.MANAGE_ASPECTS), "Verifying change type is present");

        // Verify section Add comment is present
        Assert.assertTrue(folderDetailsPage.isCommentLinkPresent(), "Verifying comment link is present");

        // Check another tab
        String url = drone.getCurrentUrl();
        drone.createNewTab();
        drone.navigateTo(url);

        folderDetailsPage = ShareUser.getSharePage(drone).render();
        ManagePermissionsPage managePermissionPage = folderDetailsPage.selectManagePermissions().render();
        Assert.assertTrue(managePermissionPage.isInheritPermissionEnabled());
        managePermissionPage.selectCancel().render();
        drone.closeTab();

        // Check another browser.
        WebDrone anotherDrone = getSecondDrone();
        anotherDrone.navigateTo(url);

        ShareUser.login(anotherDrone, testUser, testUserPass);

        folderDetailsPage = ShareUser.getSharePage(drone).render();

        managePermissionPage = folderDetailsPage.selectManagePermissions().render();
        Assert.assertTrue(managePermissionPage.isInheritPermissionEnabled());
        managePermissionPage.selectCancel().render();
        ShareUser.logout(anotherDrone);

        anotherDrone.closeWindow();

        ShareUser.logout(drone);

    }

    /**
     * Test:
     * <ul>
     * <li>Edit Meta data</li>
     * <li>Remove tag value</li>
     * <li>Add new tag value and click cancel</li>
     * <li>Add new tag value and click OK</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups = { "Repository" })
    public void AONE_3554() throws Exception
    {
        String testName = getTestName();

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String newFolderName = folderName + "1";

        String description = testName + System.currentTimeMillis();
        String tagName = testName + System.currentTimeMillis();

        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folderName, description, baseFolderPath);

        // verify created folder is present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folderName), "verifying folder present in repository");

        // Add tag to the folder in repository
        ShareUserRepositoryPage.selectView(drone, ViewType.DETAILED_VIEW).render();

        ShareUserRepositoryPage.addTag(drone, folderName, tagName);

        // Select folder and click on edit properties
        EditDocumentPropertiesPage editDocumentPropertiesPopup = ShareUserSitePage.getFileDirectoryInfo(drone, folderName).selectEditProperties().render();

        // Edit the folder name
        editDocumentPropertiesPopup.setName(newFolderName);

        // Remove tag value by clicking on remove tag button

        ShareUserRepositoryPage.operationOnTag(drone, Operation.REMOVE, tagName);

        // Adding tag value and click on cancel button

        ShareUserRepositoryPage.operationOnTag(drone, Operation.ADD_AND_CANCEL, tagName + "1");

        editDocumentPropertiesPopup.selectSave().render();

        // Adding tag value and click on OK button
        ShareUserRepositoryPage.openRepository(drone);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);

        editDocumentPropertiesPopup = ShareUserSitePage.getFileDirectoryInfo(drone, newFolderName).selectEditProperties().render();

        ShareUserRepositoryPage.operationOnTag(drone, Operation.SAVE, tagName + "1");

        editDocumentPropertiesPopup.selectSave().render();

        // Verify Name field changed successfully
        Assert.assertEquals(ShareUserSitePage.getFileDirectoryInfo(drone, newFolderName).getName(), newFolderName, "verifying folder name is changed successfully");

        // Verify Tag Value changed successfully
        ShareUserSitePage.clickOnTagNameInTreeMenu(drone, tagName + "1").render();

        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, newFolderName, "isContentVisible", "", true));
        Assert.assertEquals(ShareUserSitePage.getContentCount(drone), 1);

        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_AONE_3566() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName);
        String testUser2 = getUserNameFreeDomain(testName + "2");

        // User
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };

        // Create User by adding to Alfresco_Administrators group
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo2);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Repository" })
    public void AONE_3566() throws Exception
    {
        String testName = getTestName();

        String testUser1 = getUserNameFreeDomain(testName);
        String testUser2 = getUserNameFreeDomain(testName + "2");

        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String Title = "Manage Permissions";

        // Login
        ShareUser.login(drone, testUser1, testUserPass);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);

        // verify created folders are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");

        // Select more options in folder1 and click on Manage permissions (this verify Add user / group button, Inherit permissions button, Save and Cancel
        // buttons)
        ManagePermissionsPage managePermissionsPage = ShareUserSitePage.getFileDirectoryInfo(drone, folder1).selectManagePermission().render();

        // Verify Manage Permissions page is displayed
        Assert.assertTrue(managePermissionsPage.isTitlePresent(Title), "Verify manage permissions page is displayed");

        // Verify Inherit permissions options in Manage Permissions page
        Assert.assertTrue(managePermissionsPage.isInheritPermissionEnabled());

        // Verify Locally Set Permissions list
        Assert.assertTrue(managePermissionsPage.isLocallyPermissionEnabled());

        // Make any changes i.e click add user/group -> search for any existing user and click add button);
        UserProfile profile = new UserProfile();
        profile.setfName(testUser2);
        profile.setUsername(testUser2);

        ManagePermissionsPage.UserSearchPage userSearchPage = managePermissionsPage.selectAddUser().render();
        managePermissionsPage = userSearchPage.searchAndSelectUser(profile).render();

        Assert.assertNotNull(managePermissionsPage.getExistingPermission(testUser2),
                "Failed to add user to the Locally Set Permissions section of Manage Permissions page");

        repositorypage = managePermissionsPage.selectCancel().render();

        Assert.assertTrue(repositorypage.titlePresent(), "Failed to return to the Repository page");

        // verify that The changes made are discarded.
        managePermissionsPage = ShareUserSitePage.getFileDirectoryInfo(drone, folder1).selectManagePermission().render();
        Assert.assertFalse(managePermissionsPage.isUserExistForPermission(testUser2), "Failed to cancel changes");

        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_AONE_3569() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // Create Admin user.
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Select More options for any folder in repository</li>
     * <li>Click on Edit meta data from More options</li>
     * <li>Verify edit meta data form is displayed correctly</li>
     * <li>Click on select button beneath category in edit document properties page</li>
     * <li>Move any category from left hand side to right hand side</li>
     * <li>Click ok button</li>
     * <li>verify changes are applied successfully</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void AONE_3569() throws Exception
    {
        String testName = getTestName();
        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String testUser = getUserNameFreeDomain(testName);

        // Login
        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        RepositoryPage repositorypage = ShareUserRepositoryPage.createFolderInRepository(drone, folder1, folder1);

        // verify created folders are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");

        ShareUserRepositoryPage.addAspect(drone, folder1, CLASSIFIABLE);

        // Select more options in folder1 and click on Edit properties
        EditDocumentPropertiesPage editDocumentPropertiesPopup = ShareUserRepositoryPage.returnEditDocumentProperties(drone, folder1);

        ShareUserRepositoryPage.addCategories(drone, folder1, Categories.TAGS, true);
        editDocumentPropertiesPopup.selectSave().render();

        // View folder details page and verify category is displayed under properties panel
        FolderDetailsPage folderDetailsPage = ShareUserSitePage.openDetailsPage(drone, folder1).render();
        Map<String, Object> props = folderDetailsPage.getProperties();

        Assert.assertEquals(props.get("Categories").toString(), "[" + Categories.TAGS + "]");

        ShareUser.logout(drone);

    }

    /**
     * Test:
     * <ul>
     * <li>Select More options for any folder in repository</li>
     * <li>Click on Edit meta data from More options</li>
     * <li>Verify edit meta data form is displayed correctly</li>
     * <li>Click on select button beneath category in edit document properties page</li>
     * <li>Move any category from left hand side to right hand side</li>
     * <li>Click Cancel button</li>
     * <li>Verify changes are not applied successfully</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void AONE_3570() throws Exception
    {
        String folder1 = getFolderName(testName + System.currentTimeMillis());

        ShareUser.login(drone, testUser, testUserPass);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        RepositoryPage repositorypage = ShareUserRepositoryPage.createFolderInRepository(drone, folder1, folder1);

        // verify created folders are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");

        ShareUserRepositoryPage.addAspect(drone, folder1, CLASSIFIABLE);

        // Select more options in folder1 and click on Edit properties
        EditDocumentPropertiesPage editDocumentPropertiesPopup = ShareUserRepositoryPage.returnEditDocumentProperties(drone, folder1);

        ShareUserRepositoryPage.addCategories(drone, folder1, Categories.TAGS, true);
        editDocumentPropertiesPopup.selectCancel().render();

        // View folder details page and verify category is not displayed under properties panel
        Assert.assertEquals(ShareUserRepositoryPage.getProperties(drone, folder1).get("Categories"), "(None)");

        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_AONE_3572() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // Create Admin user.
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Create any folder in User home folder in repository</li>
     * <li>Select More options for any folder in repository</li>
     * <li>Click on Edit meta data from More options</li>
     * <li>Verify edit meta data form is displayed correctly</li>
     * <li>Edit the title of created folder</li>
     * <li>Verify the title of created folder is updated</li>
     * <li>Edit the description of created folder</li>
     * <li>Verify the description of created folder is updated</li>
     * <li>Add the tag of created folder</li>
     * <li>Verify the tag of created folder is updated successfully</li>
     * <li>Edit the category of created folder</li>
     * <li>Click ok button</li>
     * <li>verify changes are applied successfully</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void AONE_3572() throws Exception
    {
        String testUser = getUserNameFreeDomain(testName);

        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String tagName = getTestName();

        // Login
        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInRepository(drone, folder1, folder1);

        // verify created folders are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");

        // Select more options in folder1 and click on Manage Aspects
        ShareUserRepositoryPage.addAspect(drone, folder1, CLASSIFIABLE);

        // Select more options in folder1 and click on Edit properties
        // repositorypage =
        // ShareUserRepositoryPage.openUserFromUserHomesFolderOfRepository(drone,
        // testUser);
        EditDocumentPropertiesPage editDocumentPropertiesPopup = ShareUserSitePage.getFileDirectoryInfo(drone, folder1).selectEditProperties().render();

        // Edit folder details
        editDocumentPropertiesPopup.selectAllProperties().render();
        editDocumentPropertiesPopup.setName(folder1 + "1");
        editDocumentPropertiesPopup.setDocumentTitle(folder1 + "1");
        editDocumentPropertiesPopup.setDescription(description + "1");

        // Add tag value
        ShareUserRepositoryPage.operationOnTag(drone, Operation.SAVE, tagName + "1");
        editDocumentPropertiesPopup.selectSave();

        // Add category
        editDocumentPropertiesPopup = ShareUserRepositoryPage.returnEditDocumentProperties(drone, folder1 + "1");

        ShareUserRepositoryPage.addCategories(drone, folder1 + "1", Categories.TAGS, true);
        editDocumentPropertiesPopup.selectSave().render();

        // Verify Added category is displayed correctly in folder details page under properties panel
        Assert.assertTrue((ShareUserRepositoryPage.getProperties(drone, folder1 + "1").get("Categories").toString()).contains(Categories.TAGS.getValue().toUpperCase()));

        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_AONE_3573() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // Create Admin user.
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Create any folder in User home folder in repository</li>
     * <li>Select More options for any folder in repository</li>
     * <li>Click on Edit meta data from More options</li>
     * <li>Verify edit meta data form is displayed correctly</li>
     * <li>Edit the title of created folder</li>
     * <li>Verify the title of created folder is updated</li>
     * <li>Edit the description of created folder</li>
     * <li>Verify the description of created folder is updated</li>
     * <li>Add the tag of created folder</li>
     * <li>Verify the tag of created folder is updated successfully</li>
     * <li>Edit the category of created folder</li>
     * <li>Click cancel button</li>
     * <li>verify changes are not applied successfully</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups = { "Repository" })
    public void AONE_3573() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);

        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String tagName = getTestName();

        // Login
        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInRepository(drone, folder1, folder1);

        // verify created folders are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");

        ShareUserRepositoryPage.addAspect(drone, folder1, CLASSIFIABLE);

        // Select more options in folder1 and click on Edit properties
        EditDocumentPropertiesPage editDocumentPropertiesPopup = ShareUserRepositoryPage.returnEditDocumentProperties(drone, folder1);

        editDocumentPropertiesPopup.selectAllProperties().render();
        // Edit folder details
        editDocumentPropertiesPopup.setName(folder1 + "1");
        editDocumentPropertiesPopup.setDocumentTitle(folder1 + "1");
        editDocumentPropertiesPopup.setDescription(description + "1");

        // Add tag value
        ShareUserRepositoryPage.operationOnTag(drone, Operation.SAVE, tagName + "1");
        editDocumentPropertiesPopup.selectSave().render();

        // Add category

        // editdocumentPropertiesPage.selectcategory
        editDocumentPropertiesPopup = ShareUserRepositoryPage.returnEditDocumentProperties(drone, folder1 + "1");

        ShareUserRepositoryPage.addCategories(drone, folder1, Categories.TAGS, false);

        // Click Cancel button
        editDocumentPropertiesPopup.selectCancel().render();

        // Verify Added category is displayed correctly in folder details page under properties panel
        Assert.assertFalse((ShareUserRepositoryPage.getProperties(drone, folder1 + "1").get("Categories").toString()).contains(Categories.TAGS.getValue().toUpperCase()));

        ShareUser.logout(drone);

    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_AONE_3578() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        String baseFolderName = getFolderName(testName);
        String baseFolderTitle = "Base folder for FolderTests";
        String description = "Base folder for FolderTests";
        baseFolderPath = REPO + SLASH + baseFolderName;

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

        // Create new folder
        if (!repositorypage.isFileVisible(baseFolderName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, baseFolderName, baseFolderTitle, description);
        }

        // Create OP user.
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Verify documents/categories/tags sections when copy/delete folders</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test(groups = { "Repository" })
    public void AONE_3578() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);

        String opSiteName = testName + System.currentTimeMillis();

        String parentFolder = "1" + "-" + System.currentTimeMillis();
        String[] destFolder = new String[] { "Repository", PAGE_TITLE_USERHOMES, parentFolder };
        String[] siteDestFolder = new String[] { "Repository", "Sites", opSiteName, "documentLibrary" };
        String destFolderPath = REPO + SLASH + PAGE_TITLE_USERHOMES + SLASH + parentFolder;

        String folderFav = "a" + System.currentTimeMillis();
        String folderCat = "aa" + System.currentTimeMillis();
        String folderTag = "aaa" + System.currentTimeMillis();

        String tagName = testName + System.currentTimeMillis();

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

        repositorypage.selectFolder(PAGE_TITLE_USERHOMES).render();

        ShareUserRepositoryPage.createFolderInRepository(drone, parentFolder, parentFolder).render();

        ShareUserSitePage.selectContent(drone, parentFolder).render();

        // Create new folder: Make it Favourite
        ShareUserSitePage.createFolder(drone, folderFav, folderFav, folderFav);
        ShareUserSitePage.getFileDirectoryInfo(drone, folderFav).selectFavourite();

        // Create another folder: Add aspect
        ShareUserSitePage.createFolder(drone, folderCat, folderCat, folderCat).render();
        ShareUserRepositoryPage.addAspect(drone, folderCat, CLASSIFIABLE);

        ShareUserRepositoryPage.returnEditDocumentProperties(drone, folderCat);

        EditDocumentPropertiesPage editDocumentPropertiesPopup = ShareUserRepositoryPage.addCategories(drone, folderCat, Categories.LANGUAGES, true);

        editDocumentPropertiesPopup.selectSave().render();

        ShareUserSitePage.createFolder(drone, folderTag, folderTag, folderTag).render();

        editDocumentPropertiesPopup = ShareUserRepositoryPage.returnEditDocumentProperties(drone, folderTag).render();

        ShareUserRepositoryPage.operationOnTag(drone, Operation.SAVE, tagName + "1");

        editDocumentPropertiesPopup.selectSave().render();

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // verify created 3 folders are present in the main repository
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);
        webDriverWait(drone, 2000);
        CopyOrMoveContentPage copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderFav).selectCopyTo().render();
        copyOrMoveContentPage.selectPath(destFolder).render();
        repositorypage = copyOrMoveContentPage.selectOkButton().render();
        //repositorypage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, folderFav, destFolder, true);

        repositorypage.clickOnMyFavourites().render();

        Assert.assertTrue(repositorypage.isFileVisible(folderFav));
        Assert.assertFalse(repositorypage.isFileVisible("Copy of " + folderFav));
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderTag).selectCopyTo().render();
        copyOrMoveContentPage.selectPath(destFolder).render();
        repositorypage = copyOrMoveContentPage.selectOkButton().render();

        ShareUserSitePage.getFileDirectoryInfo(drone, folderTag).clickOnTagNameLink(tagName + "1").render();

        ShareUserSitePage.getDocLibInfoWithRetry(drone, "Copy of " + folderTag, "isContentVisible", "Copy of " + folderTag, true);
        Assert.assertTrue(repositorypage.isFileVisible(folderTag));
        Assert.assertTrue(repositorypage.isFileVisible("Copy of " + folderTag));

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderCat).selectCopyTo().render();
        copyOrMoveContentPage.selectPath(destFolder).render();
        repositorypage = copyOrMoveContentPage.selectOkButton().render();

        ShareUserSitePage.getFileDirectoryInfo(drone, folderCat).clickOnCategoryNameLink(Categories.LANGUAGES.name()).render();
        ShareUserSitePage.getDocLibInfoWithRetry(drone, "Copy of " + folderCat, "isContentVisible", "Copy of " + folderCat, true);
        Assert.assertTrue(repositorypage.isFileVisible(folderCat));
        Assert.assertTrue(repositorypage.isFileVisible("Copy of " + folderCat));

        // copy: site
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderFav).selectCopyTo().render();
        copyOrMoveContentPage.selectPath(siteDestFolder).render();
        copyOrMoveContentPage.selectOkButton().render();

        copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderTag).selectCopyTo().render();
        copyOrMoveContentPage.selectPath(siteDestFolder).render();
        copyOrMoveContentPage.selectOkButton().render();

        copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderCat).selectCopyTo().render();
        copyOrMoveContentPage.selectPath(siteDestFolder).render();
        repositorypage = copyOrMoveContentPage.selectOkButton().render();

        // verify  folders are displayed when browsing Repository using tags/categories/documents sections; tag scope increased
        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        Assert.assertTrue(docLibPage.isFileVisible(folderFav));
        Assert.assertTrue(docLibPage.isFileVisible(folderTag));
        Assert.assertTrue(docLibPage.isFileVisible(folderCat));

        FileDirectoryInfo fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderFav);
        Assert.assertFalse(fileDirectoryInfo.isFavourite());

        ShareUserSitePage.getFileDirectoryInfo(drone, folderTag).clickOnTagNameLink(tagName + "1").render();
        Assert.assertTrue(docLibPage.isFileVisible(folderTag));

        docLibPage = ShareUser.openDocumentLibrary(drone).render();
        ShareUserSitePage.getFileDirectoryInfo(drone, folderCat).clickOnCategoryNameLink(Categories.LANGUAGES.name()).render();
        Assert.assertTrue(docLibPage.isFileVisible(folderCat));

        // delete this folders from site's document library
        ShareUser.openDocumentLibrary(drone).render();
        ShareUserSitePage.getFileDirectoryInfo(drone, folderFav).selectCheckbox();
        ShareUserSitePage.getFileDirectoryInfo(drone, folderCat).selectCheckbox();
        ShareUserSitePage.getFileDirectoryInfo(drone, folderTag).selectCheckbox();

        ConfirmDeletePage deletePage = repositorypage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();

        repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        ShareUserSitePage.getFileDirectoryInfo(drone, folderFav).selectCheckbox();
        ShareUserSitePage.getFileDirectoryInfo(drone, folderCat).selectCheckbox();
        ShareUserSitePage.getFileDirectoryInfo(drone, folderTag).selectCheckbox();

        deletePage = repositorypage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();

        repositorypage = ShareUserSitePage.getFileDirectoryInfo(drone, "Copy of " + folderTag).clickOnTagNameLink(tagName + "1").render();

        Assert.assertTrue(repositorypage.isFileVisible("Copy of " + folderTag));
        Assert.assertFalse(repositorypage.isFileVisible(folderTag));

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        repositorypage = ShareUserSitePage.getFileDirectoryInfo(drone, "Copy of " + folderCat).clickOnCategoryNameLink(Categories.LANGUAGES.name()).render();

        Assert.assertTrue(repositorypage.isFileVisible("Copy of " + folderCat));

        Assert.assertFalse(repositorypage.isFileVisible(folderCat));

        repositorypage = repositorypage.clickOnMyFavourites().render();
        Assert.assertFalse(repositorypage.isFileVisible("Copy of " + folderCat));
        Assert.assertFalse(repositorypage.isFileVisible(folderCat));

        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Verify category of actions in the drop-down "selected items" menu</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void AONE_3475() throws Exception
    {
        String testName = getTestName();

        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String folder2 = getFolderName(testName + System.currentTimeMillis() + "1");
        String folder3 = getFolderName(testName + System.currentTimeMillis() + "2");

        String file1 = getFolderName(testName + System.currentTimeMillis() + "3");
        String file2 = getFolderName(testName + System.currentTimeMillis() + "4");

        String Title1 = getTestName() + System.currentTimeMillis();
        String Description1 = getTestName() + System.currentTimeMillis();
        String Content1 = getTestName() + System.currentTimeMillis() + "1";

        String Title2 = testName + System.currentTimeMillis() + "1";
        String Description2 = getTestName() + System.currentTimeMillis() + "1";
        String Content2 = getTestName() + System.currentTimeMillis() + "2";

        String description1 = testName + System.currentTimeMillis();
        String description2 = testName + System.currentTimeMillis() + "1";
        String description3 = testName + System.currentTimeMillis() + "2";

        String subFolderPath;

        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description1, baseFolderPath);

        // verify created 3 folders are present in the main repository
        // ShareUserRepositoryPage.navigateToFolderInRepository(drone,
        // baseFolderPath);
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in baseFolderPath");

        // Navigate to folder1

        // Create folder2, folder3 in folder1
        ShareUserSitePage.createFolder(drone, folder2, description2);
        ShareUserSitePage.createFolder(drone, folder3, description3);

        // Create content1 in folder1
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(file1);
        contentDetails.setTitle(Title1);
        contentDetails.setDescription(Description1);
        contentDetails.setContent(Content1);

        // Create content 1 in folder1
        // ShareUserRepositoryPage.navigateToFolderInRepository(drone, folder1);
        subFolderPath = folder1;
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, subFolderPath);

        // Create content2 in folder1
        ContentDetails contentdetails = new ContentDetails();
        contentdetails.setName(file2);
        contentdetails.setTitle(Title2);
        contentdetails.setDescription(Description2);
        contentdetails.setContent(Content2);

        // Create content 2 in folder1
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);
        subFolderPath = folder1;
        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails, ContentType.PLAINTEXT, subFolderPath);

        // Navigate to folder1
        subFolderPath = baseFolderPath + SLASH + folder1;
        RepositoryPage repopage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, subFolderPath);
        RepositoryPage repopag = repopage.getNavigation().selectAll().render();

        // Select copy to from top menu selected items
        CopyOrMoveContentPage copyOrMoveContentPage = repopag.getNavigation().selectCopyTo();
        copyOrMoveContentPage.selectCancelButton();

        // Select Move to from top menu selected items
        CopyOrMoveContentPage copyorMoveContentPage = repopag.getNavigation().selectMoveTo();
        copyorMoveContentPage.selectCancelButton();

        // Click on selected items from top menu and 'Deselect' all
        // RepositoryPage reposPage =
        repopag.getNavigation().selectDesellectAll();

        // To do -Verify check box is unchecked for all folders
        // Verify Selected items menu is not visible
        // RepositoryPage reppage =
        // ShareUserRepositoryPage.navigateToFolderInRepository(drone,
        // subFolderPath);
        Assert.assertFalse(repopag.getNavigation().isSelectedItemMenuVisible());

        // Click on select menu and set all
        RepositoryPage rePage = repopage.getNavigation().selectAll().render();

        // Confirm delete
        ConfirmDeletePage confirmDeletePage = rePage.getNavigation().selectDelete();
        confirmDeletePage.selectAction(Action.Delete);

        // Verify folders are deleted
        RepositoryPage repage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, subFolderPath);
        Assert.assertFalse(repage.isFileVisible(folder1));
        Assert.assertFalse(repage.isFileVisible(folder2));
        Assert.assertFalse(repage.isFileVisible(folder3));

        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Browsing the library using Simple/Detailed view</li>
     * </ul>
     */
    @Test(groups = { "Repository" }, priority = 10 /*for change order. */)
    public void AONE_3483() throws Exception
    {
        String testName = getTestName();

        baseFolderName = getFolderName(getRandomString(5));
        baseFolderPath = REPO + SLASH + baseFolderName;
        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String folder2 = getFolderName(testName + System.currentTimeMillis() + "1");
        String content1 = getFileName(testName + System.currentTimeMillis());
        String content2 = getFileName(testName + System.currentTimeMillis() + "1");
        String title = getRandomString(5);
        String tag = getRandomString(5);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

        // Create two new folders
        if (!repositorypage.isFileVisible(baseFolderName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, baseFolderName, baseFolderName).render();
        }

        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);
        ShareUserRepositoryPage.createFolderInRepository(drone, folder2, description);

        // create two files
        ContentDetails contentDetails = new ContentDetails();
        int i = 1;
        for (String content : new String[] { content1, content2 })
        {
            ShareUserRepositoryPage.openRepository(drone).render();
            contentDetails.setName(content);
            contentDetails.setTitle(title + i);
            contentDetails.setDescription(content + i);
            contentDetails.setContent(content);
            ShareUserRepositoryPage.createContentInFolderWithValidation(drone, contentDetails, ContentType.PLAINTEXT, baseFolderName).render();
            i++;
        }
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath).render();
        String[] desc = new String[] { description, description, content1 + 1, content2 + 2 };
        webDriverWait(drone, 3000);
        for (String content : new String[] { folder1, folder2, content1, content2 })
        {
            ShareUserSitePage.getFileDirectoryInfo(drone, content).addTag(tag);
        }

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath).render();

        i = 0;
        for (String content : new String[] { folder1, folder2, content1, content2 })
        {
            // verify folder1, folder2, doc1 and doc2
            FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, content);

            // verify that only basic item details (title, modification date and time, user responsible for the modifications) are displayed;
            Assert.assertTrue(content.equals(fileInfo.getName()));

            Assert.assertFalse(drone.isElementDisplayed(By.xpath(String.format("//span[@class='item' and text()='%s']", desc[i]))));
            Assert.assertNotNull(fileInfo.getContentEditInfo());
            Assert.assertTrue(fileInfo.isEditPropertiesLinkPresent());
            if (content.equals(content1))
                Assert.assertTrue(drone.isElementDisplayed(By.xpath(String.format("//span[@class='title' and text()='(%s)']", title + 1))));
            if (content.equals(content2))
                Assert.assertTrue(drone.isElementDisplayed(By.xpath(String.format("//span[@class='title' and text()='(%s)']", title + 2))));
            i++;
        }

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);

        i = 0;
        for (String content : new String[] { folder1, folder2, content1, content2 })
        {
            // verify folder1, folder2, doc1 and doc2
            FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, content);

            Assert.assertTrue(content.equals(fileInfo.getName()));
            Assert.assertTrue(desc[i].equals(fileInfo.getDescription()));
            Assert.assertTrue(fileInfo.hasTags());
            Assert.assertNotNull(fileInfo.getContentEditInfo());
            Assert.assertTrue(fileInfo.isEditPropertiesLinkPresent());
            if (content.equals(content1))
                Assert.assertTrue(drone.isElementDisplayed(By.xpath(String.format("//span[@class='title' and text()='(%s)']", title + 1))));
            if (content.equals(content2))
                Assert.assertTrue(drone.isElementDisplayed(By.xpath(String.format("//span[@class='title' and text()='(%s)']", title + 2))));
            Assert.assertTrue(fileInfo.getContentEditInfo().toLowerCase().contains("admin"));
            i++;
        }

        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Hide/Show folders action</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void AONE_3473() throws Exception
    {
        String testName = getTestName();

        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String folder4 = getFolderName(testName + System.currentTimeMillis() + "-3");

        String file2 = getFileName(testName + System.currentTimeMillis() + "4.txt");

        String Title2 = testName + System.currentTimeMillis() + "1";
        String Description2 = getTestName() + System.currentTimeMillis() + "1";
        String Content2 = getTestName() + System.currentTimeMillis() + "2";

        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepository(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);

        // Create content2 in folder1
        ContentDetails contentdetails2 = new ContentDetails();
        contentdetails2.setName(file2);
        contentdetails2.setTitle(Title2);
        contentdetails2.setDescription(Description2);
        contentdetails2.setContent(Content2);

        // Create content 2 in folder1
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        // String[] subFolderPath = new String[] { baseFolderName, folder1 };
        RepositoryPage repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);

        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails2, ContentType.PLAINTEXT, folder1);

        ShareUserSitePage.createFolder(drone, folder4, folder4);

        // Click on hide folders under options
        repositorypage = repositorypage.getNavigation().selectHideFolders().render();
        Assert.assertFalse(repositorypage.isFileVisible(folder4));
        repositorypage = repositorypage.getNavigation().selectShowFolders().render();
        Assert.assertTrue(repositorypage.isFileVisible(folder4));

        ShareUser.logout(drone);

    }

    /**
     * Test:
     * <ul>
     * <li>Verify pagination button navigates to selected page</li>
     * </ul>
     */
    @Test(groups = { "Repository" })
    public void AONE_3474() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        baseFolderPath = REPO + SLASH + folderName;

        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Navigate to folder
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName).render();
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);

        for (int i = 1; i < 150; i++)
        {
            File file = newFile(testName + i, testName);
            ShareUserRepositoryPage.uploadFileInRepository(drone, file).render();
        }

        RepositoryPage repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath).render();

        PaginationForm paginationForm = repositorypage.getBottomPaginationForm();
        webDriverWait(drone, 5000);
        assertFalse(paginationForm.isPreviousButtonEnable(), "Can move to previous page.");
        assertTrue(paginationForm.isNextButtonEnable(), "Can't move next page.");
        assertEquals(paginationForm.getPaginationInfo(), "1 - 50 of 149", "Wrong info about pagination items.");
        assertEquals(paginationForm.getPaginationLinks().size(), 3, "Wrong pages links count.");
        assertEquals(repositorypage.getFiles().size(), 50, "50 items didn't displayed.");

        // click "Next" navigation button
        paginationForm.clickNext();
        assertTrue(paginationForm.isPreviousButtonEnable(), "Can't move to previous page.");
        assertTrue(paginationForm.isNextButtonEnable(), "Can move next page.");
        assertEquals(paginationForm.getPaginationInfo(), "51 - 100 of 149", "Wrong info about pagination items.");
        assertEquals(paginationForm.getPaginationLinks().size(), 3, "Wrong pages links count.");
        assertEquals(repositorypage.getFiles().size(), 50, "50 items didn't displayed.");

        // click "Previous" navigation button
        paginationForm.clickPrevious();
        assertFalse(paginationForm.isPreviousButtonEnable(), "Can move to previous page.");
        assertTrue(paginationForm.isNextButtonEnable(), "Can't move next page.");
        assertEquals(paginationForm.getPaginationInfo(), "1 - 50 of 149", "Wrong info about pagination items.");
        assertEquals(paginationForm.getPaginationLinks().size(), 3, "Wrong pages links count.");
        assertEquals(repositorypage.getFiles().size(), 50, "50 items didn't displayed.");

        // go to the any page by clicking page number;
        paginationForm.clickOnPaginationPage(2);
        Assert.assertEquals(paginationForm.getCurrentPageNumber(), 2, "Page number isn't displayed");

        ShareUser.logout(drone);
    }

    @Test(groups = { "Repository" })
    public void AONE_3559() throws Exception
    {
        String testName = getTestName();

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String folderTitle = (testUser) + System.currentTimeMillis();
        String description = (testName) + System.currentTimeMillis();

        // Login
        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        ShareUserRepositoryPage.openRepository(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderTitle, description);

        // click Copy To action from More actions menu for the folder created in pre-conditions
        CopyOrMoveContentPage copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName).selectCopyTo().render();

        // There are 6 buttons for select destination:
        List<String> destinations = copyOrMoveContentPage.getDestinations();

        for (String destination : new String[] { "Recent Sites", "Favorite Sites", "All Sites", "Repository", "Shared Files", "My Files" })
            Assert.assertTrue(destinations.contains(destination));

        Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().equals("Copy " + folderName + " to..."));

        // two enabled buttons Copy and Cancel
        Assert.assertTrue(copyOrMoveContentPage.isOkButtonPresent());
        Assert.assertTrue(copyOrMoveContentPage.isCancelButtonPresent());

        // click My Files button and click Copy button
        copyOrMoveContentPage.selectDestination("My Files").render().selectOkButton().render();

        // open user's home folder (Repository->User Homes-><user_name>);
        RepositoryPage repositorypage = ShareUserRepositoryPage.openUserFromUserHomesFolderOfRepository(drone, testUser);

        // verify copied item is present in the folder;
        Assert.assertTrue(repositorypage.isFileVisible(folderName));

        // click Copy To action from More actions menu for the folder that was previously pasted
        copyOrMoveContentPage = ShareUserSitePage.getFileDirectoryInfo(drone, folderName).selectCopyTo().render();

        // There are 6 buttons for select destination:
        destinations = copyOrMoveContentPage.getDestinations();

        for (String destination : new String[] { "Recent Sites", "Favorite Sites", "All Sites", "Repository", "Shared Files", "My Files" })
            Assert.assertTrue(destinations.contains(destination));

        Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().equals("Copy " + folderName + " to..."));

        // two enabled buttons Copy and Cancel
        Assert.assertTrue(copyOrMoveContentPage.isOkButtonPresent());
        Assert.assertTrue(copyOrMoveContentPage.isCancelButtonPresent());

        // click My Files button and click Copy button
        repositorypage = copyOrMoveContentPage.selectDestination("My Files").render().selectOkButton().render();

        // open the folder and verify item and Copy of item's name are present there
        Assert.assertTrue(repositorypage.isFileVisible("Copy of " + folderName));

        ShareUser.logout(drone);
    }

}
