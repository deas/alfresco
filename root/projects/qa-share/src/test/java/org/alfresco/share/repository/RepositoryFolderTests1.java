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

import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;

import java.util.Map;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.document.Categories;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentAction;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage.ButtonType;
import org.alfresco.po.share.site.document.UserProfile;
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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

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

    // TODO -Remove if unused
    protected String baseFolderTitle = "Base folder for FolderTests";
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

    @Test
    public void Enterprise40x_5405()
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
        FolderDetailsPage folderDetailsPage = repositorypage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        // TODO - Name, Title and Description assertions missing

        // Verify details page of selected folder is displayed
        // Assert.assertTrue(folderDetailsPage.isDetailsPage(folderName), "Verifying folder details page is displayed");

        // Verify section Add comment is present
        Assert.assertTrue(folderDetailsPage.isCommentLinkPresent(), "Verifying comment link is present");

        // Verify share panel is displayed
        Assert.assertTrue(folderDetailsPage.isSharePanePresent(), "Verifying Share pane is present");

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

        // Check another tab
        String url = drone.getCurrentUrl();
        drone.createNewTab();
        drone.navigateTo(url);

        // TODO - Doesn't match Steps 6, 7 and 8 from test link
        folderDetailsPage = ShareUser.getSharePage(drone).render();
        ManagePermissionsPage managePermissionPage = folderDetailsPage.selectManagePermissions().render();
        Assert.assertTrue(managePermissionPage.isInheritPermissionEnabled());
        folderDetailsPage = managePermissionPage.selectCancel().render();
        drone.closeTab();

        // Check another browser.
        WebDrone anotherDrone = getSecondDrone();
        anotherDrone.navigateTo(url);

        ShareUser.login(anotherDrone, testUser, testUserPass);

        folderDetailsPage = ShareUser.getSharePage(drone).render();

        managePermissionPage = folderDetailsPage.selectManagePermissions().render();
        Assert.assertTrue(managePermissionPage.isInheritPermissionEnabled());
        folderDetailsPage = managePermissionPage.selectCancel().render();
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
    @Test
    public void Enterprise40x_5407() throws Exception
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

        repositorypage = ShareUserRepositoryPage.addTag(drone, folderName, tagName);

        // Select folder and click on edit properties
        EditDocumentPropertiesPage editDocumentPropertiesPopup = repositorypage.getFileDirectoryInfo(folderName).selectEditProperties().render();

        // Edit the folder name
        editDocumentPropertiesPopup.setName(newFolderName);

        // Remove tag value by clicking on remove tag button

        ShareUserRepositoryPage.operationOnTag(drone, Operation.REMOVE, tagName);

        // Adding tag value and click on cancel button

        ShareUserRepositoryPage.operationOnTag(drone, Operation.ADD_AND_CANCEL, tagName + "1");

        editDocumentPropertiesPopup.selectSave().render();

        // Adding tag value and click on OK button
        ShareUserRepositoryPage.openRepository(drone);

        repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);

        editDocumentPropertiesPopup = repositorypage.getFileDirectoryInfo(newFolderName).selectEditProperties().render();

        ShareUserRepositoryPage.operationOnTag(drone, Operation.SAVE, tagName + "1");

        editDocumentPropertiesPopup.selectSave().render();

        // Verify Name field changed successfully
        Assert.assertEquals(repositorypage.getFileDirectoryInfo(newFolderName).getName(), newFolderName, "verifying folder name is changed successfully");

        // Verify Tag Value changed successfully
        ShareUserSitePage.clickOnTagNameInTreeMenu(drone, tagName + "1").render();

        Assert.assertTrue(ShareUserSitePage.getDocLibInfoWithRetry(drone, newFolderName, "tags", tagName + "1", true));
        Assert.assertEquals(ShareUserSitePage.getContentCount(drone), 1);

    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_Enterprise40x_5419() throws Exception
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
    }

    /**
     * Test:
     * <ul>
     * <li>Select More options for any folder in repository</li>
     * <li>Click on Manage Permissions from More options</li>
     * <li>Verify options Inherit permissions, Inherit Permissions list, Locally set permissions panels are displayed</li>
     * <li>Change privileges for any group and cancel</li>
     * <li>Verify group privileges are discarded</li>
     * </ul>
     */
    @Test
    public void Enterprise40x_5419() throws Exception
    {
        String testName = getTestName();

        String testUser1 = getUserNameFreeDomain(testName);
        String testUser2 = getUserNameFreeDomain(testName + "2");

        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String description = testName + System.currentTimeMillis();
        String Title = "Manage Permissions";

        String GroupName = "EVERYONE";

        // Login
        ShareUser.login(drone, testUser1, testUserPass);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);

        // verify created folders are present in the main repository
        Assert.assertTrue(repositorypage.isFileVisible(folder1), "verifying folder present in repository");

        // Select more options in folder1 and click on Manage permissions
        ManagePermissionsPage managePermissionsPage = repositorypage.getFileDirectoryInfo(folder1).selectManagePermission().render();

        // Verify Manage Permissions page is displayed
        Assert.assertTrue(managePermissionsPage.isTitlePresent(Title), "Verify manage permissions page is displayed");

        // Verify Inherit permissions options in Manage Permissions page
        Assert.assertTrue(managePermissionsPage.isInheritPermissionEnabled());

        UserProfile profile = new UserProfile();
        profile.setfName("xyz");
        profile.setUsername("xyz");
        Assert.assertFalse(managePermissionsPage.isDirectPermissionForUser(profile));

        // Logout as user1
        ShareUser.logout(drone);

        // Login as testuser2
        ShareUser.login(drone, testUser2, testUserPass);

        // Navigate to repository page
        repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);

        ManagePermissionsPage managepermissionsPage = repositorypage.getFileDirectoryInfo(folder1).selectManagePermission().render();

        Assert.assertFalse(managepermissionsPage.toggleInheritPermission(false, ButtonType.Yes).isUserExistForPermission(GroupName),
                "Verify user exists or not");

    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_Enterprise40x_5422() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // Create Admin user.
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
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
    @Test
    public void Enterprise40x_5422()
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
    @Test
    public void Enterprise40x_5423()
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
        repositorypage = editDocumentPropertiesPopup.selectCancel().render();

        // View folder details page and verify category is not displayed under properties panel
        Assert.assertEquals(ShareUserRepositoryPage.getProperties(drone, folder1).get("Categories"), "(None)");

        ShareUser.logout(drone);
    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_Enterprise40x_5425() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // Create Admin user.
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
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
    @Test
    public void Enterprise40x_5425()
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
        EditDocumentPropertiesPage editDocumentPropertiesPopup = repositorypage.getFileDirectoryInfo(folder1).selectEditProperties().render();

        // Edit folder details
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
    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_Enterprise40x_5426() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // Create Admin user.
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
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
     * @throws Exception 
     */
    @Test
    public void Enterprise40x_5426()
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
        editDocumentPropertiesPopup.selectSave().render();

        // Verify Added category is displayed correctly in folder details page under properties panel
        Assert.assertFalse((ShareUserRepositoryPage.getProperties(drone, folder1 + "1").get("Categories").toString()).contains(Categories.TAGS.getValue().toUpperCase()));
                
    }

    @Test(groups = "DataPrepRepository")
    public void dataPrep_Enterprise40x_5431() throws Exception
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
    }

    /**
     * Test:
     * <ul>
     * <li>Verify documents/categories/tags sections when copy/delete folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    public void Enterprise40x_5431() throws Exception
    {
        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName);

        String opSiteName = testName + System.currentTimeMillis();

        String parentFolder = "1" + "-" + System.currentTimeMillis();
        String[] destFolder = new String[] { "Repository", PAGE_TITLE_USERHOMES, parentFolder };
        String destFolderPath = REPO + SLASH + PAGE_TITLE_USERHOMES + SLASH + parentFolder;

        String folderFav = "a" + System.currentTimeMillis();
        String folderCat = "aa" + System.currentTimeMillis();
        String folderTag = "aaa" + System.currentTimeMillis();

        String tagName = testName + System.currentTimeMillis();

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

        // TODO - Put this is share-po language property files for localization
        repositorypage.selectFolder(PAGE_TITLE_USERHOMES).render();

        ShareUserRepositoryPage.createFolderInRepository(drone, parentFolder, parentFolder);

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

        ShareUserSitePage.createFolder(drone, folderTag, folderTag, folderTag);

        editDocumentPropertiesPopup = ShareUserRepositoryPage.returnEditDocumentProperties(drone, folderTag);

        ShareUserRepositoryPage.operationOnTag(drone, Operation.SAVE, tagName + "1");

        editDocumentPropertiesPopup.selectSave().render();

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // verify created 3 folders are present in the main repository
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        repositorypage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, folderFav, destFolder, true);

        repositorypage = repositorypage.clickOnMyFavourites().render();

        Assert.assertTrue(repositorypage.isFileVisible(folderFav));

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, folderTag, destFolder, true);

        ShareUserSitePage.getFileDirectoryInfo(drone, folderTag).clickOnTagNameLink(tagName + "1").render();

        ShareUserSitePage.getDocLibInfoWithRetry(drone, "Copy of " + folderTag, "isContentVisible", "Copy of " + folderTag, true);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, folderCat, destFolder, true);

        ShareUserSitePage.getFileDirectoryInfo(drone, folderCat).clickOnCategoryNameLink(Categories.LANGUAGES.name()).render();
        ShareUserSitePage.getDocLibInfoWithRetry(drone, "Copy of " + folderCat, "isContentVisible", "Copy of " + folderCat, true);

        // copy: site
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        repositorypage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, folderFav, destFolder, true);

        Assert.assertTrue(repositorypage.isFileVisible(folderFav));
        Assert.assertTrue(repositorypage.isFileVisible("Copy of " + folderFav));

        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, folderTag, destFolder, true);
        ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, folderCat, destFolder, true);

        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        docLibPage.isFileVisible(folderFav);
        docLibPage.isFileVisible(folderTag);
        docLibPage.isFileVisible(folderCat);

        repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        ShareUserSitePage.getFileDirectoryInfo(drone, folderFav).selectCheckbox();
        ShareUserSitePage.getFileDirectoryInfo(drone, folderCat).selectCheckbox();
        ShareUserSitePage.getFileDirectoryInfo(drone, folderTag).selectCheckbox();

        // TODO: Add util to take action on selected content from DocLib Navigation menu
        ConfirmDeletePage deletePage = repositorypage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();

        repositorypage = ShareUserSitePage.getFileDirectoryInfo(drone, "Copy of " + folderTag).clickOnTagNameLink(tagName + "1").render();

        Assert.assertTrue(repositorypage.isFileVisible("Copy of " + folderTag));
        Assert.assertFalse(repositorypage.isFileVisible(folderTag));

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        repositorypage = ShareUserSitePage.getFileDirectoryInfo(drone, "Copy of " + folderCat).clickOnCategoryNameLink(Categories.LANGUAGES.name()).render();

        Assert.assertTrue(repositorypage.isFileVisible("Copy of " + folderCat));

        Assert.assertFalse(repositorypage.isFileVisible(folderCat));

        repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, destFolderPath);

        ShareUserSitePage.getFileDirectoryInfo(drone, "Copy of " + folderTag).selectCheckbox();
        ShareUserSitePage.getFileDirectoryInfo(drone, "Copy of " + folderCat).selectCheckbox();
        ShareUserSitePage.getFileDirectoryInfo(drone, "Copy of " + folderFav).selectCheckbox();

        deletePage = repositorypage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();

        // TODO - May need extra setps to check these aren't displayed when browsing by tags/categories or in My Favorites

        ShareUser.logout(drone);
    }

    /**
     * Test:
     * <ul>
     * <li>Verify category of actions in the drop-down "selected items" menu</li>
     * </ul>
     */
    @Test
    public void Enterprise40x_5337() throws Exception
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

        String subFolderPath = baseFolderPath + SLASH + folder1;

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
        // repospage.getNavigation().clickSelectedItems().render();
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

    }

    /**
     * Test:
     * <ul>
     * <li>Browsing the library using Simple/Detailed view</li>
     * </ul>
     */
    @Test
    public void Enterprise40x_5345() throws Exception
    {
        String testName = getTestName();

        String folder1 = getFolderName(testName + System.currentTimeMillis());
        String folder2 = getFolderName(testName + System.currentTimeMillis() + "1");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

        // Create new folder
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folder1, description, baseFolderPath);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);

        repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folder1);

        // Create sub folder2 in folder1
        repositorypage = (RepositoryPage) ShareUserSitePage.createFolder(drone, folder2, folder2);

        Assert.assertTrue(folder2.equals(repositorypage.getFileDirectoryInfo(folder2).getName()));
        Assert.assertTrue(repositorypage.getFileDirectoryInfo(folder2).getContentEditInfo().contains("Administrator"));

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);

        repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);

        repositorypage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folder1);

        Assert.assertTrue(folder2.equals(repositorypage.getFileDirectoryInfo(folder2).getName()));
        Assert.assertTrue(repositorypage.getFileDirectoryInfo(folder2).getContentEditInfo().contains("Administrator"));

    }

    /**
     * Test:
     * <ul>
     * <li>Hide/Show folders action</li>
     * </ul>
     */
    @Test
    public void Enterprise40x_5335() throws Exception
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
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

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
        repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);

        ShareUserRepositoryPage.createContentInFolder(drone, contentdetails2, ContentType.PLAINTEXT, folder1);

        ShareUserSitePage.createFolder(drone, folder4, folder4);

        // Click on hide folders under options
        repositorypage = repositorypage.getNavigation().selectHideFolders().render();
        Assert.assertFalse(repositorypage.isFileVisible(folder4));
        repositorypage = repositorypage.getNavigation().selectShowFolders().render();
        Assert.assertTrue(repositorypage.isFileVisible(folder4));

    }

    /**
     * Test:
     * <ul>
     * <li>Verify pagination button navigates to selected page</li>
     * </ul>
     */
    @Test
    public void Enterprise40x_5336() throws Exception
    {
        String testName = getTestName();

        String subfolder1 = getFolderName(testName + System.currentTimeMillis() + "3");
        String subfolder2 = getFolderName(testName + System.currentTimeMillis() + "4");

        String Description1 = getTestName() + System.currentTimeMillis();
        String Description2 = getTestName() + System.currentTimeMillis() + "1";

        String guestHomePath = REPO + SLASH + "Guest Home";

        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Navigate to folder
        repositorypage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, baseFolderPath);

        // Create sub folder1 in folder
        ShareUserSitePage.createFolder(drone, subfolder1, Description1);

        // Create sub folder2 in folder
        ShareUserSitePage.createFolder(drone, subfolder2, Description2);

        // Select more options in folder3 and copy to site
        RepositoryPage repopag = repositorypage.getNavigation().selectAll().render();
        CopyOrMoveContentPage copyOrMoveContentPage = repopag.getNavigation().selectCopyTo().render();
        copyOrMoveContentPage.selectPath("Repository", "Guest Home").render().selectOkButton().render();

        // verify folder1 is copied successfully
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        RepositoryPage repage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, guestHomePath);
        Assert.assertTrue(repage.isFileVisible(subfolder1), "Verifying copied folder is present in site doclib");
        Assert.assertTrue(repage.isFileVisible(subfolder2), "Verifying copied folder is present in site doclib");

    }

    @Test
    public void Enterprise40x_5412()
    {
        String testName = getTestName();

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String folderTitle = (testUser) + System.currentTimeMillis();
        String description = (testName) + System.currentTimeMillis();

        // Login
        ShareUser.login(drone, testUser, testUserPass);

        // Navigate to repository page
        RepositoryPage repositorypage = ShareUserRepositoryPage.openRepository(drone);

        // Create new folder
        repositorypage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderTitle, description);

        ShareUserRepositoryPage.copyToFolderInDestination(drone, folderName, "My Files");

        repositorypage = ShareUserRepositoryPage.openUserFromUserHomesFolderOfRepository(drone, testUser);

        Assert.assertTrue(repositorypage.isFileVisible(folderName));

        ShareUserRepositoryPage.copyToFolderInDestination(drone, folderName, "My Files");

        Assert.assertTrue(repositorypage.isFileVisible("Copy of " + folderName));

        ShareUser.logout(drone);
    }

}
