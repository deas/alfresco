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

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage.ButtonType;
import org.alfresco.po.share.site.document.ManagePermissionsPage.UserSearchPage;
import org.alfresco.po.share.site.document.SortField;
import org.alfresco.share.search.SearchKeys;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

/**
 * @author nshah
 */
@Listeners(FailedTestListener.class)
public class RepositoryManagePermissionTest extends AbstractUtils
{
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(RepositoryManagePermissionTest.class);

    private String testDomainFree = DOMAIN_FREE;

    private String adminUserFree = ADMIN_USERNAME;
    private FacetedSearchPage facetedSearchPage;   

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomainFree = DOMAIN_FREE;
        adminUserFree = ADMIN_USERNAME;
    }

    // 5380
    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5380() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5380() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName);

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String childfolderName = getFolderName(testName) + System.currentTimeMillis() + "child";

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        RepositoryPage repoPage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
        repoPage.selectFolder(folderName);
        ShareUserRepositoryPage.createFolderInRepository(drone, childfolderName, childfolderName);

        ShareUser.logout(drone);

        // Consumer
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repoPage.selectFolder(folderName);

        Assert.assertFalse(repoPage.getFileDirectoryInfo(childfolderName).isManagePermissionLinkPresent());

        ShareUser.logout(drone);

        // Contributor

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repoPage.selectFolder(folderName);

        ShareUserMembers.managePermissionsOnContent(drone, user1, childfolderName, UserRole.CONTRIBUTOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repoPage.selectFolder(folderName);

        Assert.assertFalse(repoPage.getFileDirectoryInfo(childfolderName).isManagePermissionLinkPresent());

        ShareUser.logout(drone);

        // Collaborator
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repoPage.selectFolder(folderName);
        ShareUserMembers.updateRoleOnContent(drone, user1, childfolderName, UserRole.COLLABORATOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repoPage.selectFolder(folderName);
        Assert.assertFalse(repoPage.getFileDirectoryInfo(childfolderName).isManagePermissionLinkPresent());

        ShareUser.logout(drone);

        // Editor
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repoPage.selectFolder(folderName);
        ShareUserMembers.updateRoleOnContent(drone, user1, childfolderName, UserRole.EDITOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repoPage.selectFolder(folderName);
        Assert.assertFalse(repoPage.getFileDirectoryInfo(childfolderName).isManagePermissionLinkPresent());

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5381() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user2 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5381() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String fileLocation = DATA_FOLDER + fileName;

        File file = newFile(fileLocation, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);

        ShareUserMembers.managePermissionsOnContent(drone, user1, folderName, UserRole.COORDINATOR, true);
        ShareUserMembers.managePermissionsOnContent(drone, user2, folderName, UserRole.COORDINATOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // repoPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        ShareUserMembers.updateRoleOnContent(drone, user2, folderName, UserRole.CONSUMER, true);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepository(drone);

        repoPage.selectFolder(folderName).render();
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        ShareUser.logout(drone);

        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepository(drone);

        DocumentLibraryPage docLibPage = repoPage.selectFolder(folderName).render();
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5382() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user2 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5382() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String fileLocation = DATA_FOLDER + fileName;

        File file = newFile(fileLocation, "New file");

        ShareUser.login(drone, adminUserFree, ADMIN_PASSWORD);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone).render();

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName).render();

        ShareUserMembers.managePermissionsOnContent(drone, user1, folderName, UserRole.COORDINATOR, true);
        ShareUserMembers.managePermissionsOnContent(drone, user2, folderName, UserRole.COORDINATOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Cancel manage permissions
        FolderDetailsPage folderDetailsPage = repoPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
        ManagePermissionsPage mangPermPage = folderDetailsPage.selectManagePermissions().render();
        mangPermPage.updateUserRole(user2, UserRole.CONSUMER);
        mangPermPage.toggleInheritPermission(false, ButtonType.Yes);
        folderDetailsPage = (FolderDetailsPage) mangPermPage.selectCancel().render();

        ShareUserRepositoryPage.openRepository(drone);

        repoPage.selectFolder(folderName).render();
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        ShareUser.logout(drone);

        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        DocumentLibraryPage docLibPage = repoPage.selectFolder(folderName).render();

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());
        docLibPage = docLibPage.deleteItem(fileName).render();

        Assert.assertFalse(docLibPage.isFileVisible(fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5383() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user2 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5383() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String subFolderName = getFolderName(testName) + System.currentTimeMillis() + "-2";
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String fileLocation = DATA_FOLDER + fileName;

        String editFileText = "just edit!!";

        File file = newFile(fileLocation, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        RepositoryPage repoPage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ShareUserRepositoryPage.openRepository(drone);
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, subFolderName, subFolderName, REPO + SLASH + folderName);

        repoPage = ShareUserRepositoryPage.openRepository(drone);
        repoPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        ShareUserMembers.managePermissionsOnContent(drone, user1, folderName, UserRole.COORDINATOR, true);
        ShareUserMembers.managePermissionsOnContent(drone, user2, folderName, UserRole.EDITOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ManagePermissionsPage mangPermPage = ShareUser.returnManagePermissionPage(drone, subFolderName);

        Assert.assertTrue(UserRole.EDITOR.getRoleName().equalsIgnoreCase(mangPermPage.getExistingPermissionForInheritPermission(user2).getRoleName()));
        mangPermPage.selectSave().render();

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName + SLASH + subFolderName);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        ShareUser.logout(drone);

        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repoPage = repoPage.selectFolder(folderName).render();
        repoPage = repoPage.selectFolder(subFolderName).render();

        DocumentDetailsPage documentDetailsPage = repoPage.selectFile(fileName).render();

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(editFileText);
        contentDetails.setName(fileName);

        // Select Inline Edit and change the content and save
        EditTextDocumentPage editTextDocumentPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = editTextDocumentPage.save(contentDetails).render();

        // TODO: Can this be checked by asserting modifier property?
        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName + SLASH + subFolderName);

        repoPage.getFileDirectoryInfo(fileName).selectViewInBrowser();
        String htmlSource = ((WebDroneImpl) drone).getDriver().getPageSource();
        Assert.assertTrue(htmlSource.contains(editFileText));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5384() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user2 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5384() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "_1");
        String user2 = getUserNameFreeDomain(testName + "_2");

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String subFolderName = getFolderName(testName) + System.currentTimeMillis() + "-2";
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String fileLocation = DATA_FOLDER + fileName;

        File file = newFile(fileLocation, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        RepositoryPage repoPage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ShareUserRepositoryPage.createFolderInRepository(drone, subFolderName, subFolderName, subFolderName);

        repoPage = ShareUserRepositoryPage.openRepository(drone);

        ShareUserMembers.managePermissionsOnContent(drone, user1, folderName, UserRole.COORDINATOR, true);
        ShareUserMembers.managePermissionsOnContent(drone, user2, folderName, UserRole.EDITOR, true);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserMembers.managePermissionsOnContent(drone, user1, subFolderName, UserRole.COORDINATOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepository(drone);

        repoPage = repoPage.selectFolder(folderName).render();

        ManagePermissionsPage mangPermPage = ShareUser.returnManagePermissionPage(drone, subFolderName);

        Assert.assertTrue(UserRole.EDITOR.getRoleName().equalsIgnoreCase(mangPermPage.getExistingPermissionForInheritPermission(user2).getRoleName()));
        mangPermPage.toggleInheritPermission(false, ButtonType.Yes);
        mangPermPage.selectSave().render();

        // Upload file
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName + SLASH + subFolderName);

        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        ShareUser.logout(drone);

        // Check that file is not visible for user2
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepository(drone);

        repoPage = repoPage.selectFolder(folderName).render();

        Assert.assertFalse(repoPage.isFileVisible(subFolderName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5385() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user2 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5385() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String subFolderName = getFolderName(testName) + System.currentTimeMillis();

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String fileLocation = DATA_FOLDER + fileName;
        File file = newFile(fileLocation, "New file");
        String modify = "Just Edit the content";

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        RepositoryPage repoPage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        repoPage.selectFolder(folderName).render();
        ShareUserRepositoryPage.createFolderInRepository(drone, subFolderName, subFolderName);

        ShareUserRepositoryPage.openRepository(drone);
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.EDITOR, true);
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.COORDINATOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ManagePermissionsPage mangPermPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Assert.assertTrue(UserRole.EDITOR.getRoleName().equalsIgnoreCase(mangPermPage.getExistingPermissionForInheritPermission(user2).getRoleName()));
        mangPermPage.toggleInheritPermission(false, ButtonType.No);
        mangPermPage.selectSave().render();

        // Upload File
        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName + SLASH + subFolderName);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        ShareUser.logout(drone);

        // Check if file can be edited by user2 as Editor
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName + SLASH + subFolderName);

        // repoPage = repoPage.selectFolder(folderName).render();
        // repoPage = repoPage.selectFolder(subFolderName).render();

        DocumentDetailsPage documentDetailsPage = repoPage.selectFile(fileName).render();

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(modify);
        contentDetails.setName(fileName);

        Assert.assertTrue(documentDetailsPage.isInlineEditLinkDisplayed());
        // Select Inline Edit and change the content and save
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = inlineEditPage.save(contentDetails).render();
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        Assert.assertEquals(modify, inlineEditPage.getDetails().getContent());
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5386() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String group1 = getGroupName(testName) + "-1";
        String group2 = getGroupName(testName) + "-2";

        ShareUser.createEnterpriseGroup(drone, group1);
        ShareUser.createEnterpriseGroup(drone, group2);

        ShareUser.createEnterpriseUserWithGroup(drone, adminUserFree, user1, user1, user1, getAuthDetails(user1)[1], group1);
        ShareUser.createEnterpriseUserWithGroup(drone, adminUserFree, user2, user2, user2, getAuthDetails(user2)[1], group2);
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5386() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();

        String user = getUserNameFreeDomain(testName).substring(0, 4);
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String group = getGroupName(testName);
        String group1 = group + "-1";
        String group2 = group + "-2";

        File file1 = newFile(fileName, "New File");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepository(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepository(drone);

        ManagePermissionsPage manPerPage = repoPage.getFileDirectoryInfo(folderName).selectManagePermission().render();

        UserSearchPage userSearchPage = manPerPage.selectAddUser().render();

        // added to debug fail in bamboo
        Assert.assertTrue(userSearchPage.isUserOrGroupPresentInSearchList(user1));
        Assert.assertTrue(userSearchPage.isUserOrGroupPresentInSearchList(user2));

        Assert.assertTrue(userSearchPage.usersExistInSearchResults(user, user1, user2));
        Assert.assertTrue(userSearchPage.usersExistInSearchResults(group, group1, group2));
        manPerPage.selectCancel();

        ShareUserMembers.managePermissionsOnContent(drone, group1, folderName, UserRole.COORDINATOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUserRepositoryPage.openRepository(drone);
        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ConfirmDeletePage deletePage = repoPage.getFileDirectoryInfo(fileName).selectDelete();
        deletePage.selectAction(Action.Delete);

        Assert.assertFalse(repoPage.isFileVisible(fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5387() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String group1 = getGroupName(testName) + "-1";

        ShareUser.createEnterpriseGroup(drone, group1);
        ShareUser.createEnterpriseUserWithGroup(drone, adminUserFree, user1, user1, user1, getAuthDetails(user1)[1], group1);

    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5387() throws Exception
    {
        String testName = getTestName();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String wildCardStringUser = "<>?:\"|}{+_)(*&^%$#@!~;";

        String longNameUser = AbstractUtils.getRandomString(1024);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        ManagePermissionsPage.UserSearchPage userSearchPage = managePermissionPage.selectAddUser().render();

        Assert.assertEquals(userSearchPage.getSearchErrorMessage(""), drone.getValue("message.enter.at.least.3.characters"), "Error message missing: " + drone.getValue("message.enter.at.least.3.characters"));
        managePermissionPage.selectCancel();

        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        userSearchPage = managePermissionPage.selectAddUser().render();
        Assert.assertTrue(userSearchPage.isEveryOneDisplayed(wildCardStringUser), "Everyone group is not displayed");
        managePermissionPage.selectCancel();

        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        userSearchPage = managePermissionPage.selectAddUser().render();
        Assert.assertTrue(userSearchPage.isEveryOneDisplayed(longNameUser), "Everyone group is not displayed");

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5388() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String group1 = getGroupName(testName) + "-1";

        ShareUser.createEnterpriseGroup(drone, group1);
        ShareUser.createEnterpriseUserWithGroup(drone, adminUserFree, user1, user1, user1, getAuthDetails(user1)[1], group1);

    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5388() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String group1 = getGroupName(testName) + "-1";

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepository(drone);

        ManagePermissionsPage managePermissionsPage = ShareUser.returnManagePermissionPage(drone, folderName);

        ManagePermissionsPage.UserSearchPage userSearchPage = managePermissionsPage.selectAddUser().render();
        managePermissionsPage = userSearchPage.searchAndSelectGroup(group1).render();

        // Add role to Group
        managePermissionsPage.updateUserRole(group1, UserRole.COORDINATOR);

        Assert.assertTrue(managePermissionsPage.isUserExistForPermission(group1), group1 + " has not been added for permission.");
        managePermissionsPage.selectCancel().render();

        managePermissionsPage = ShareUser.returnManagePermissionPage(drone, folderName);
        Assert.assertFalse(managePermissionsPage.isUserExistForPermission(group1), group1 + " should not have permission.");

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        repoPage = ShareUserRepositoryPage.openRepository(drone);
        repoPage = repoPage.selectFolder(folderName).render();

        Assert.assertTrue(repoPage.isFileVisible(fileName));

        ShareUser.selectContentCheckBox(drone, fileName);
        Assert.assertFalse(repoPage.getFileDirectoryInfo(fileName).isDeletePresent(), "Delete is present.");

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5389() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);

        String group1 = getGroupName(testName);
        ShareUser.createEnterpriseGroup(drone, group1);

        ShareUser.createEnterpriseUserWithGroup(drone, adminUserFree, user1, user1, user1, getAuthDetails(user1)[1], group1);

    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5389() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String group1 = getGroupName(testName);

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepository(drone);

        ShareUser.returnManagePermissionPage(drone, folderName);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, group1, false, UserRole.COORDINATOR, true);
        Assert.assertEquals(UserRole.COORDINATOR, ShareUserMembers.getContentPermission(drone, folderName, group1));

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Check folder is visible
        repoPage = repoPage.selectFolder(folderName).render();

        ShareUser.selectContentCheckBox(drone, fileName);
        ShareUser.deleteSelectedContent(drone);

        // Check file is deleted
        Assert.assertFalse(repoPage.isFileVisible(fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5390() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5390() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepository(drone);

        ShareUserMembers.managePermissionsOnContent(drone, user1, folderName, UserRole.COORDINATOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        // Folder can be opened
        repoPage = repoPage.selectFolder(folderName).render();

        // File is visible
        Assert.assertTrue(repoPage.isFileVisible(fileName));

        DocumentDetailsPage detailsPage = repoPage.selectFile(fileName).render();
        Assert.assertTrue(detailsPage.isDocumentDetailsPage(), "Unable to View Document details");

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5391() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5391() throws Exception
    {
        String testName = getTestName();
        
        String user1 = getUserNameFreeDomain(testName);

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        File file = newFile(fileName, "New file");
        
        String editFileText = "just edit!!";

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepository(drone);

        ShareUser.returnManagePermissionPage(drone, folderName);

        // ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1,
        // true, UserRole.EDITOR, true);
        // Assert.assertEquals(UserRole.EDITOR,
        // ShareUserMembers.getContentPermission(drone, folderName, user1));
        ShareUserMembers.managePermissionsOnContent(drone, user1, folderName, UserRole.EDITOR, true);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repoPage = repoPage.selectFolder(folderName).render();

        Assert.assertTrue(repoPage.isFileVisible(fileName));
        Assert.assertEquals(ShareUserRepositoryPage.getInLineEditContentDetails(drone, fileName).getName(), file.getName());

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setContent(editFileText);

        DocumentDetailsPage documentDetailsPage = repoPage.selectFile(fileName).render();

        // Select Inline Edit and change the content and save
        EditTextDocumentPage editTextDocumentPage = documentDetailsPage.selectInlineEdit().render();
        documentDetailsPage = editTextDocumentPage.saveWithValidation(contentDetails).render();

        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        repoPage.getFileDirectoryInfo(fileName).selectViewInBrowser();
        String htmlSource = ((WebDroneImpl) drone).getDriver().getPageSource();
        Assert.assertTrue(htmlSource.contains(editFileText));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5392() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user2 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5392() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String folderName = getFolderName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ManagePermissionsPage manPermPage = ShareUser.returnManagePermissionPage(drone, folderName);

        ManagePermissionsPage.UserSearchPage userSearchPage = manPermPage.selectAddUser().render();

        Assert.assertTrue(userSearchPage.usersExistInSearchResults("use", user1, user2));
        manPermPage.selectCancel();

        ShareUser.returnManagePermissionPage(drone, folderName);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, true);

        manPermPage = ShareUser.returnManagePermissionPage(drone, folderName);
        Assert.assertEquals(manPermPage.getExistingPermission(user1), UserRole.CONTRIBUTOR);

        manPermPage.deleteUserOrGroupFromPermission(user1, UserRole.CONTRIBUTOR);
        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        Assert.assertFalse(repoPage.getNavigation().isFileUploadEnabled());

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5393() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5393() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + System.currentTimeMillis() + "-2";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        File file = newFile(fileName, "New file");

        String modifyProperties = "modified" + testName;

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        RepositoryPage repoPage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);

        ShareUser.returnManagePermissionPage(drone, folderName);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.COORDINATOR, true);
        Assert.assertEquals(UserRole.COORDINATOR, ShareUserMembers.getContentPermission(drone, folderName, user1));

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderName);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName2, folderName2);
        ShareUserRepositoryPage.editContentProperties(drone, folderName2, modifyProperties, true);

        repoPage = ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        ShareUserRepositoryPage.editContentProperties(drone, fileName, modifyProperties, true);

        Assert.assertTrue(repoPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());
        Assert.assertTrue(repoPage.getFileDirectoryInfo(fileName).isInlineEditLinkPresent());
        Assert.assertEquals(ShareUserRepositoryPage.getInLineEditContentDetails(drone, fileName).getName(), file.getName());

        repoPage = repoPage.deleteItem(fileName).render();
        Assert.assertFalse(repoPage.isFileVisible(fileName));

        repoPage = repoPage.deleteItem(folderName2).render();
        Assert.assertFalse(repoPage.isFileVisible(folderName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5394() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5394() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + System.currentTimeMillis() + "-2";
        String folderCreatedByAdmin = getFolderName(testName) + System.currentTimeMillis() + "-1";

        String fileName = getFileName(testName) + System.currentTimeMillis() + "-1.txt";
        String fileCreatedByAdmin = getFileName(testName) + System.currentTimeMillis() + "-2.txt";

        File file = newFile(fileName, "New file");
        File fileByAdmin = newFile(fileCreatedByAdmin, "New file");

        String modifyProperties = "modified" + testName;

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        RepositoryPage repoPage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderCreatedByAdmin, folderCreatedByAdmin);

        repoPage = ShareUserRepositoryPage.uploadFileInRepository(drone, fileByAdmin);

        ShareUserRepositoryPage.openRepository(drone);

        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.COLLABORATOR, true);
        Assert.assertEquals(UserRole.COLLABORATOR, ShareUserMembers.getContentPermission(drone, folderName, user1));

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repoPage = repoPage.selectFolder(folderName).render();

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName2, folderName2);

        repoPage = ShareUserRepositoryPage.editContentProperties(drone, folderCreatedByAdmin, modifyProperties, true);

        repoPage = ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        Assert.assertTrue(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isEditOfflineLinkPresent());
        Assert.assertTrue(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isInlineEditLinkPresent());
        Assert.assertEquals(ShareUserRepositoryPage.getInLineEditContentDetails(drone, fileCreatedByAdmin).getName(), fileByAdmin.getName());

        repoPage = repoPage.deleteItem(folderName2).render();
        Assert.assertFalse(repoPage.isFileVisible(folderName2));
        Assert.assertFalse(repoPage.getFileDirectoryInfo(folderCreatedByAdmin).isDeletePresent());

        repoPage = repoPage.deleteItem(fileName).render();
        Assert.assertFalse(repoPage.isFileVisible(fileName));

        Assert.assertFalse(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isDeletePresent());

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5395() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5395() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String folderName2 = getFolderName(testName) + System.currentTimeMillis() + "-2";
        String folderCreatedByAdmin = getFolderName(testName) + System.currentTimeMillis() + "-1";

        String fileName = getFileName(testName) + System.currentTimeMillis() + "-1.txt";
        String fileCreatedByAdmin = getFileName(testName) + System.currentTimeMillis() + "-2.txt";

        File file = newFile(fileName, "New file");
        File fileByAdmin = newFile(fileCreatedByAdmin, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        RepositoryPage repoPage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);

        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderName);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderCreatedByAdmin, folderCreatedByAdmin);

        ShareUserRepositoryPage.uploadFileInRepository(drone, fileByAdmin);

        ShareUserRepositoryPage.openRepository(drone);

        ShareUser.returnManagePermissionPage(drone, folderName);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.CONTRIBUTOR, true);
        Assert.assertEquals(UserRole.CONTRIBUTOR, ShareUserMembers.getContentPermission(drone, folderName, user1));

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderName);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName2, folderName2);

        Assert.assertFalse(repoPage.getFileDirectoryInfo(folderCreatedByAdmin).isEditPropertiesLinkPresent());

        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        Assert.assertFalse(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isEditOfflineLinkPresent());
        Assert.assertFalse(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isInlineEditLinkPresent());
        Assert.assertFalse(repoPage.getFileDirectoryInfo(folderCreatedByAdmin).isDeletePresent());

        Assert.assertFalse(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isDeletePresent());

        // TODO: Testlink: Add steps to testlink?
        repoPage = repoPage.deleteItem(folderName2).render();
        Assert.assertFalse(repoPage.isFileVisible(folderName2));

        repoPage = repoPage.deleteItem(fileName).render();
        Assert.assertFalse(repoPage.isFileVisible(fileName));

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5396() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5396() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        String folderCreatedByAdmin = getFolderName(testName) + System.currentTimeMillis();

        String fileCreatedByAdmin = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String modifyProperties = "modified" + testName;

        File fileByAdmin = newFile(fileCreatedByAdmin, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepository(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderCreatedByAdmin, folderCreatedByAdmin);

        ShareUserRepositoryPage.uploadFileInRepository(drone, fileByAdmin);

        ShareUserRepositoryPage.openRepository(drone);
        ShareUserRepositoryPage.sortLibraryOn(drone, SortField.CREATED, false);

        ShareUser.returnManagePermissionPage(drone, folderName);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.EDITOR, true);

        Assert.assertEquals(UserRole.EDITOR, ShareUserMembers.getContentPermission(drone, folderName, user1));

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.sortLibraryOn(drone, SortField.CREATED, false);

        RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        Assert.assertFalse(repoPage.getNavigation().isFileUploadEnabled());
        Assert.assertFalse(repoPage.getNavigation().isCreateContentEnabled());

        Assert.assertTrue(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isEditOfflineLinkPresent());
        Assert.assertTrue(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isInlineEditLinkPresent());

        repoPage = ShareUserRepositoryPage.editContentProperties(drone, fileCreatedByAdmin, modifyProperties, true);
        Assert.assertEquals(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).getDescription(), modifyProperties);

        repoPage = ShareUserRepositoryPage.editContentProperties(drone, folderCreatedByAdmin, modifyProperties, true);
        Assert.assertEquals(repoPage.getFileDirectoryInfo(folderCreatedByAdmin).getDescription(), modifyProperties);

        Assert.assertFalse(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isDeletePresent());
        Assert.assertFalse(repoPage.getFileDirectoryInfo(folderCreatedByAdmin).isDeletePresent());

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5397() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5397() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        String folderCreatedByAdmin = getFolderName(testName) + System.currentTimeMillis();

        String fileCreatedByAdmin = getFileName(testName) + System.currentTimeMillis() + ".txt";

        File fileByAdmin = newFile(fileCreatedByAdmin, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepository(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderCreatedByAdmin, folderCreatedByAdmin);

        ShareUserRepositoryPage.uploadFileInRepository(drone, fileByAdmin);

        ShareUserRepositoryPage.openRepository(drone);
        ShareUserRepositoryPage.sortLibraryOn(drone, SortField.CREATED, false);

        ShareUser.returnManagePermissionPage(drone, folderName);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.CONSUMER, true);

        Assert.assertEquals(UserRole.CONSUMER, ShareUserMembers.getContentPermission(drone, folderName, user1));

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.sortLibraryOn(drone, SortField.CREATED, false);

        RepositoryPage repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        Assert.assertFalse(repoPage.getNavigation().isFileUploadEnabled());
        Assert.assertFalse(repoPage.getNavigation().isCreateContentEnabled());

        Assert.assertFalse(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isEditPropertiesLinkPresent());
        Assert.assertFalse(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isEditOfflineLinkPresent());
        Assert.assertFalse(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isInlineEditLinkPresent());

        Assert.assertFalse(repoPage.getFileDirectoryInfo(folderCreatedByAdmin).isEditPropertiesLinkPresent());

        Assert.assertFalse(repoPage.getFileDirectoryInfo(fileCreatedByAdmin).isDeletePresent());
        Assert.assertFalse(repoPage.getFileDirectoryInfo(folderCreatedByAdmin).isDeletePresent());

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5398() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String group1 = getGroupName(testName) + "-2";

        ShareUser.createEnterpriseGroup(drone, group1);
        ShareUser.createEnterpriseUserWithGroup(drone, adminUserFree, user1, user1, user1, getAuthDetails(user1)[1], group1);
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5398() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String group1 = getGroupName(testName) + "-2";

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepository(drone);

        ShareUser.returnManagePermissionPage(drone, folderName);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, group1, false, UserRole.EDITOR, true);
        Assert.assertEquals(UserRole.EDITOR, ShareUserMembers.getContentPermission(drone, folderName, group1));

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepository(drone);

        Assert.assertFalse(repoPage.getFileDirectoryInfo(folderName).isManagePermissionLinkPresent());

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5399() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5399() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");

        String folderName = getFolderName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ShareUser.returnManagePermissionPage(drone, folderName);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.COORDINATOR, true);
        Assert.assertEquals(UserRole.COORDINATOR, ShareUserMembers.getContentPermission(drone, folderName, user1));

        ShareUserMembers.updateRoleOnContent(drone, user1, folderName, UserRole.COLLABORATOR, true);
        Assert.assertEquals(UserRole.COLLABORATOR, ShareUserMembers.getContentPermission(drone, folderName, user1));

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        Assert.assertTrue(repoPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5400() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5400() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");

        String folderName = getFolderName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ShareUser.returnManagePermissionPage(drone, folderName);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.COORDINATOR, true);
        Assert.assertEquals(UserRole.COORDINATOR, ShareUserMembers.getContentPermission(drone, folderName, user1));

        ManagePermissionsPage managePermission = ShareUser.returnManagePermissionPage(drone, folderName);

        Assert.assertTrue(managePermission.deleteUserOrGroupFromPermission(user1, UserRole.COORDINATOR));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5401() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String group1 = getGroupName(testName) + "-1";
        ShareUser.createEnterpriseGroup(drone, group1);
        ShareUser.createEnterpriseUserWithGroup(drone, adminUserFree, user1, user1, user1, getAuthDetails(user1)[1], group1);
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5401() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String group1 = getGroupName(testName) + "-1";

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + "-1";
        File file1 = newFile(fileName1, "New file");

        String fileName2 = getFileName(testName) + System.currentTimeMillis() + "-2";
        File file2 = newFile(fileName2, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);

        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file2);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, group1, false, UserRole.COORDINATOR, true);

        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.CONSUMER, true);

        Assert.assertEquals(UserRole.CONSUMER, ShareUserMembers.getContentPermission(drone, folderName, user1));
        Assert.assertEquals(UserRole.COORDINATOR, ShareUserMembers.getContentPermission(drone, folderName, group1));

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repoPage = repoPage.selectFolder(folderName).render();
        repoPage = (RepositoryPage) repoPage.deleteItem(fileName1).render();

        Assert.assertFalse(repoPage.isFileVisible(fileName1));

        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepository(drone);

        ShareUserMembers.updateRoleOnContent(drone, user1, folderName, UserRole.COORDINATOR, true);
        // ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.updateRoleOnContent(drone, group1, folderName, UserRole.CONSUMER, true);

        Assert.assertEquals(UserRole.CONSUMER, ShareUserMembers.getContentPermission(drone, folderName, group1));
        Assert.assertEquals(UserRole.COORDINATOR, ShareUserMembers.getContentPermission(drone, folderName, user1));

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        repoPage = ShareUserRepositoryPage.openRepository(drone);
        repoPage = repoPage.selectFolder(folderName).render();

        repoPage = (RepositoryPage) repoPage.deleteItem(fileName2);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_8576() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomainFree);
        CreateUserAPI.CreateActivateUser(drone, adminUserFree, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_8576() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameForDomain(testName, testDomainFree);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_MODERATED, true);

        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, user1, siteName, UserRole.COLLABORATOR);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentLibraryPage docLibaPage = ShareUserSitePage.createFolder(drone, folderName, folderName);

        docLibaPage = docLibaPage.selectFolder(folderName).render();
        ShareUserSitePage.uploadFile(drone, file);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repoPage = repoPage.selectFolder("Sites").render();
        repoPage = ShareUserRepositoryPage.openSiteFromSitesFolderOfRepository(drone, siteName);

        repoPage = repoPage.selectFolder(DOCLIB_CONTAINER).render();

        ShareUserMembers.managePermissionsOnContent(drone, user1, folderName, UserRole.CONSUMER, false);
        ManagePermissionsPage manPermPage = ShareUser.returnManagePermissionPage(drone, folderName);

        Assert.assertFalse(manPermPage.isInheritPermissionEnabled());
        Assert.assertEquals(manPermPage.getExistingPermission(user1), UserRole.CONSUMER);

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // TODO: Testlink: To confirm: Steps can be implemented from doclib or Repo
        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        repoPage = repoPage.selectFolder("Sites").render();
        repoPage = ShareUserRepositoryPage.openSiteFromSitesFolderOfRepository(drone, siteName);
        repoPage = repoPage.selectFolder(DOCLIB_CONTAINER).render();
        repoPage = repoPage.selectFolder(folderName).render();

        Assert.assertTrue(repoPage.isFileVisible(fileName));

        Assert.assertTrue(repoPage.selectFile(fileName).render() instanceof DocumentDetailsPage);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_8403() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_8403() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName);

        String folderName1 = getFolderName(testName) + System.currentTimeMillis() + "-1";
        String folderName2 = getFolderName(testName) + System.currentTimeMillis() + "-2";

        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        File file = newFile(fileName, "New file");

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        RepositoryPage repoPage = ShareUserRepositoryPage.createFolderInRepository(drone, folderName1, folderName1);
        repoPage.selectFolder(folderName1).render();

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName2, folderName2, folderName2).render();
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);

        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName1);

        // Possible UI issue as <Manage Permissions> options is not available on DocLibView
        ShareUser.returnManagePermissionPage(drone, folderName2);
        ShareUserMembers.managePermissionsOnContent(drone, user1, folderName2, UserRole.COORDINATOR, true);

        repoPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName1);

        repoPage.getFileDirectoryInfo(fileName).clickOnTitle();
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.managePermissionsOnContent(drone, user1, fileName, UserRole.COORDINATOR, true);

        repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ManagePermissionsPage managePermPage = ShareUser.returnManagePermissionPage(drone, folderName1);

        managePermPage = managePermPage.toggleInheritPermission(false, ButtonType.Yes).render();
        managePermPage.selectSave().render();

        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), folderName2);
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);        
        facetedSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(facetedSearchPage.getResults().size()>0);
        
        facetedSearchPage.getResultByName(folderName2).clickLink().render();
        HtmlPage page = FactorySharePage.resolvePage(drone);
        Assert.assertTrue(page instanceof RepositoryPage);
        
      
        // TODO: Use ShareUserSearchPage.checkSearchResultsWithRetry to avoid inconsistent results
        /*boolean found = false;
        for (SearchResult item : results)
        {
            if (item.getTitle().equals(folderName2))
            {
                found = true;
                item.clickLink();
                HtmlPage page = FactorySharePage.resolvePage(drone);
                Assert.assertTrue(page instanceof RepositoryPage);
                break;
            }
        }
        Assert.assertTrue(found);*/

        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), fileName);
        searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, fileName), "Not Found " + fileName);        
        facetedSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(facetedSearchPage.getResults().size()>0);        
        facetedSearchPage.getResultByName(fileName).clickLink().render();
        HtmlPage page1 = FactorySharePage.resolvePage(drone);
        Assert.assertTrue(page1 instanceof DocumentDetailsPage);


        // TODO: Use ShareUserSearchPage.checkSearchResultsWithRetry to avoid inconsistent results in all places
        /*found = false;
        for (SearchResult item : results)
        {
            if (item.getTitle().equals(fileName))
            {
                found = true;
                item.clickLink();
                HtmlPage page = FactorySharePage.resolvePage(drone);
                Assert.assertTrue(page instanceof DocumentDetailsPage);
                break;
            }
        }
        Assert.assertTrue(found);
*/
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5379() throws Exception
    {
        String testName = getTestName();

        String user = getUserNameFreeDomain(testName);

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, new String[] { user });
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5379() throws Exception
    {
        String testName = getTestName();

        String user = getUserNameFreeDomain(testName);

        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        RepositoryPage repoPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repoPage.selectFolder(PAGE_TITLE_USERHOMES).render();

        ManagePermissionsPage managePermissionsPage = repoPage.getFileDirectoryInfo(user).selectManagePermission().render();

        Assert.assertTrue(managePermissionsPage.isUserExistForPermission(user));
        Assert.assertEquals(UserRole.ALL, managePermissionsPage.getExistingPermission(user));
        Assert.assertTrue(managePermissionsPage.isUserDeleteButtonPresent(user));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_5402() throws Exception
    {
        // N/A
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_5402() throws Exception
    {
        String testName = getTestName();
        String group = "EVERYONE";

        String folderName = getFolderName(testName) + System.currentTimeMillis();

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName, folderName);

        // Add EveryOne group to locally set permissions
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, group, false, UserRole.COORDINATOR, false);

        // Delete EveryOne group from locally set permissions
        ManagePermissionsPage managePermissionsPage = ShareUser.returnManagePermissionPage(drone, folderName);
        managePermissionsPage.deleteUserOrGroupFromPermission(group, UserRole.COORDINATOR);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepRepository" })
    public void dataPrepEnterprise40x_8501() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user2 });

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);

        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + drone.getValue("system.folder.user.homes"));

        ManagePermissionsPage managePermissionsPage = ShareUser.returnManagePermissionPage(drone, user1);

        ManagePermissionsPage.UserSearchPage userSearchPage = managePermissionsPage.selectAddUser().render();
        managePermissionsPage = userSearchPage.searchAndSelectGroup(user2).render();

        // Add role to Group
        managePermissionsPage.updateUserRole(user2, UserRole.COORDINATOR);

        managePermissionsPage.selectSave();

        ShareUser.logout(drone);
    }

    @Test(groups = { "Repository" })
    public void Enterprise40x_8501() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + drone.getValue("system.folder.user.homes"));

        ManagePermissionsPage managePermissionsPage = ShareUser.returnManagePermissionPage(drone, user1);

        managePermissionsPage = managePermissionsPage.deleteUserWithPermission(user2, UserRole.COORDINATOR);

        SharePopup popup = managePermissionsPage.selectSave().render();

        Assert.assertTrue(popup.getShareMessage().contains("Access Denied. You do not have the appropriate permissions to perform this operation."));
    }
}