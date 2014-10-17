/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.share.site.document;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage.ButtonType;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class ManagePermissionTest2 extends AbstractAspectTests
{
    private static Log logger = LogFactory.getLog(ManagePermissionTest2.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14147() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(getRandomString(5) + "1");
        String user2 = getUserNameFreeDomain(getRandomString(5) + "2");
        String user3 = getUserNameFreeDomain(getRandomString(5) + "3");
        String user4 = getUserNameFreeDomain(getRandomString(5) + "4");
        String user5 = getUserNameFreeDomain(getRandomString(5) + "5");

        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String[] fileInfo = { fileName, DOCLIB };

        // Create a user1.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        // Create a user2.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);
        // Create a user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);
        // Create a user4.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user4);
        // Create a user5.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user5);

        // User1 login.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user2 to the site with Role "Manager"
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.MANAGER);
        }
        else
        {
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);
            ShareUserMembers.assignRoleToSiteMember(drone, user2, siteName, UserRole.MANAGER);
        }

        // Add user3 to the site with Role "Collaborator"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user3, siteName, UserRole.COLLABORATOR);

        // Add user4 to the site with Role "Contributor"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user4, siteName, UserRole.CONTRIBUTOR);

        // Add user5 to the site with Role "Consumer"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user5, siteName, UserRole.CONSUMER);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // User1 logs outs.
        ShareUser.logout(drone);

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to the site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Create a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user2 in permission with "Consumer".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.CONSUMER, true);

        // Add user3 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.CONSUMER, true);

        // Add user4 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.CONSUMER, true);

        // Add user5 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.CONSUMER, true);

        // Verify the inherit permission is turned off.
        ManagePermissionsPage managePermisisonPage;
        ShareUser.returnManagePermissionPage(drone, fileName);
        managePermisisonPage = getCurrentPage(drone).render();
        managePermisisonPage.toggleInheritPermission(false, ButtonType.Yes);
        managePermisisonPage.selectSave();
        ShareUser.returnManagePermissionPage(drone, fileName);
        Assert.assertFalse(managePermisisonPage.isInheritPermissionEnabled(), "The inherit permission isn't turned off");

        // Save.
        managePermisisonPage.selectSave();

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user2 in permission with "Consumer".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.CONSUMER, true);

        // Add user3 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.CONSUMER, true);

        // Add user4 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.CONSUMER, true);

        // Add user5 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.CONSUMER, true);

        // Verify the inherit permission is turned off.
        ShareUser.returnManagePermissionPage(drone, folderName);
        managePermisisonPage = getCurrentPage(drone).render();
        managePermisisonPage.toggleInheritPermission(false, ButtonType.Yes);
        managePermisisonPage.selectSave();
        ShareUser.returnManagePermissionPage(drone, fileName);
        Assert.assertFalse(managePermisisonPage.isInheritPermissionEnabled(), "The inherit permission isn't turned off");

        // Save.
        managePermisisonPage.selectSave();

        // User1 logs outs.
        ShareUser.logout(drone);

        // User2 logs in.
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Navigate to the site
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent(),
                "The Manager user hasn't Manager permissions to the document (i.e 'Delete' button isn't available)");

        // Verify folder has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent(),
                "The Manager user hasn't Manager permissions to the folder (i.e 'Delete' button isn't available)");

        // Log out user2.
        ShareUser.logout(drone);

        // User3 logs in.
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has comment button isn't visible
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent(),
                "The Collaborator user hasn't Consumer permissions to the document (i.e. 'Add comment' button is visible).");

        // Verify folder has comment button isn't visible
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent(),
                "The Collaborator user hasn't Consumer permissions to the folder (i.e. 'Add comment' button is visible).");

        // Log out user3.
        ShareUser.logout(drone);

        // User4 logs in.
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has comment button isn't visible
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent(),
                "The Contributor user hasn't Consumer permissions to the document (i.e. 'Add comment' button is visible).");

        // Verify folder has comment button isn't visible
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent(),
                "The Contributor user hasn't Consumer permissions to the folder (i.e. 'Add comment' button is visible).");

        // Log out user4.
        ShareUser.logout(drone);

        // User5 logs in.
        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has comment button isn't visible
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent(),
                "The Consumer user hasn't Consumer permissions to the document (i.e. 'Add comment' button is visible).");

        // Verify folder has comment button isn't visible
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent(),
                "The Consumer user hasn't Consumer permissions to the folder (i.e. 'Add comment' button is visible).");
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14148() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(getRandomString(5) + "1");
        String user2 = getUserNameFreeDomain(getRandomString(5) + "2");
        String user3 = getUserNameFreeDomain(getRandomString(5) + "3");
        String user4 = getUserNameFreeDomain(getRandomString(5) + "4");
        String user5 = getUserNameFreeDomain(getRandomString(5) + "5");

        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        // Create a user1.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        // Create a user2.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);
        // Create a user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);
        // Create a user4.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user4);
        // Create a user5.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user5);

        // User1 login.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user2 to the site with Role "Manager"
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.MANAGER);
        }
        else
        {
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);
            ShareUserMembers.assignRoleToSiteMember(drone, user2, siteName, UserRole.MANAGER);

        }

        // Add user3 to the site with Role "Collaborator"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user3, siteName, UserRole.COLLABORATOR);

        // Add user4 to the site with Role "Contributor"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user4, siteName, UserRole.CONTRIBUTOR);

        // Add user5 to the site with Role "Consumer"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user5, siteName, UserRole.CONSUMER);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // User1 logs outs.
        ShareUser.logout(drone);

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to the site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Create a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user2 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.CONTRIBUTOR, true);

        // Add user3 in permission with "Contributor".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.CONTRIBUTOR, true);

        // Add user4 in permission with "Contributor".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.CONTRIBUTOR, true);

        // Add user5 in permission with "Contributor".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.CONTRIBUTOR, true);

        // Verify the inherit permission is turned off.
        ManagePermissionsPage managePermisisonPage;
        ShareUser.returnManagePermissionPage(drone, fileName);
        managePermisisonPage = getCurrentPage(drone).render();
        managePermisisonPage.toggleInheritPermission(false, ButtonType.Yes);
        managePermisisonPage.selectSave();
        ShareUser.returnManagePermissionPage(drone, fileName);
        Assert.assertFalse(managePermisisonPage.isInheritPermissionEnabled(), "The inherit permission isn't turned off");

        // Save.
        managePermisisonPage.selectSave();

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user2 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.CONTRIBUTOR, true);

        // Add user3 in permission with "Contributor".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.CONTRIBUTOR, true);

        // Add user4 in permission with "Contributor".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.CONTRIBUTOR, true);

        // Add user5 in permission with "Contributor".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.CONTRIBUTOR, true);

        // Verify the inherit permission is turned off.
        ShareUser.returnManagePermissionPage(drone, folderName);
        managePermisisonPage = getCurrentPage(drone).render();
        managePermisisonPage.toggleInheritPermission(false, ButtonType.Yes);
        managePermisisonPage.selectSave();
        ShareUser.returnManagePermissionPage(drone, folderName);
        Assert.assertFalse(managePermisisonPage.isInheritPermissionEnabled(), "The inherit permission isn't turned off");

        // Save.
        managePermisisonPage.selectSave();

        // User1 logs outs.
        ShareUser.logout(drone);

        // User2 logs in.
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Navigate to the site
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent(),
                "The Manager user hasn't Manager permissions to the document (i.e 'Delete' button isn't available)");

        // Verify folder has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent(),
                "The Manager user hasn't Manager permissions to the folder (i.e 'Delete' button isn't available)");

        // Log out user2.
        ShareUser.logout(drone);

        // User3 logs in.
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has comment button is visible
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent(),
                "The Collaborator user hasn't Contributor permissions to the document. 'Add comment' button isn't visible");

        // Verify folder has comment button is visible
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent(),
                "The Collaborator user has Contributor permissions to the folder. 'Add comment' button isn't visible");

        // Verify document has no 'Edit Offline' button
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent(),
                "The Collaborator user hasn't Contributor permissions to the document. 'Edit Offline' button is available");

        // Verify folder has no 'Edit Properties' button
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent(),
                "The Collaborator user has Contributor permissions to the folder. 'Edit Properties' button is visible");

        // Log out user3.
        ShareUser.logout(drone);

        // User4 logs in.
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has comment button is visible
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent(),
                "The Contributor user hasn't Contributor permissions to the document. 'Add comment' button isn't visible");

        // Verify folder has comment button is visible
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent(),
                "The Contributor user has Contributor permissions to the folder. 'Add comment' button isn't visible");

        // Verify document has no 'Edit Offline' button
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent(),
                "The Contributor user hasn't Contributor permissions to the document. 'Edit Offline' button is available");

        // Verify folder has no 'Edit Properties' button
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent(),
                "The Contributor user has Contributor permissions to the folder. 'Edit Properties' button is visible");

        // Log out user4.
        ShareUser.logout(drone);

        // User5 logs in.
        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has comment button is visible
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent(),
                "The Consumer user hasn't Contributor permissions to the document. 'Add comment' button isn't visible");

        // Verify folder has comment button is visible
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent(),
                "The Consumer user has Contributor permissions to the folder. 'Add comment' button isn't visible");

        // Verify document has no 'Edit Offline' button
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent(),
                "The Consumer user hasn't Contributor permissions to the document. 'Edit Offline' button is available");

        // Verify folder has no 'Edit Properties' button
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent(),
                "The Consumer  user has Contributor permissions to the folder. 'Edit Properties' button is visible");

        // Log out user5.
        ShareUser.logout(drone);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14149() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(getRandomString(5) + "1");
        String user2 = getUserNameFreeDomain(getRandomString(5) + "2");
        String user3 = getUserNameFreeDomain(getRandomString(5) + "3");
        String user4 = getUserNameFreeDomain(getRandomString(5) + "4");
        String user5 = getUserNameFreeDomain(getRandomString(5) + "5");

        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        // Create a user1.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        // Create a user2.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);
        // Create a user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);
        // Create a user4.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user4);
        // Create a user5.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user5);

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user2 to the site with Role "Manager"
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.MANAGER);
        }
        else
        {
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);
            ShareUserMembers.assignRoleToSiteMember(drone, user2, siteName, UserRole.MANAGER);
        }

        // Add user3 to the site with Role "Collaborator"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user3, siteName, UserRole.COLLABORATOR);

        // Add user4 to the site with Role "Contributor"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user4, siteName, UserRole.CONTRIBUTOR);

        // Add user5 to the site with Role "Consumer"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user5, siteName, UserRole.CONSUMER);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // User1 logs outs.
        ShareUser.logout(drone);

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to the site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Create a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user2 in permission with "Collaborator".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.COLLABORATOR, true);

        // Add user3 in permission with "Collaborator".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.COLLABORATOR, true);

        // Add user4 in permission with "Collaborator".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.COLLABORATOR, true);

        // Add user5 in permission with "Collaborator".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.COLLABORATOR, true);

        // Verify the inherit permission is turned off.
        ManagePermissionsPage managePermisisonPage;
        ShareUser.returnManagePermissionPage(drone, fileName);
        managePermisisonPage = getCurrentPage(drone).render();
        managePermisisonPage.toggleInheritPermission(false, ButtonType.Yes);
        managePermisisonPage.selectSave();
        ShareUser.returnManagePermissionPage(drone, fileName);
        Assert.assertFalse(managePermisisonPage.isInheritPermissionEnabled(), "The inherit permission isn't turned off");

        // Save.
        managePermisisonPage.selectSave();

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user2 in permission with "Collaborator".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.COLLABORATOR, true);

        // Add user3 in permission with "Collaborator".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.COLLABORATOR, true);

        // Add user4 in permission with "Collaborator".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.COLLABORATOR, true);

        // Add user5 in permission with "Collaborator".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.COLLABORATOR, true);

        // Verify the inherit permission is turned off.
        ShareUser.returnManagePermissionPage(drone, folderName);
        managePermisisonPage = getCurrentPage(drone).render();
        managePermisisonPage.toggleInheritPermission(false, ButtonType.Yes);
        managePermisisonPage.selectSave();
        ShareUser.returnManagePermissionPage(drone, fileName);
        Assert.assertFalse(managePermisisonPage.isInheritPermissionEnabled(), "The inherit permission isn't turned off");

        // Save.
        managePermisisonPage.selectSave();

        // User1 logs outs.
        ShareUser.logout(drone);

        // User2 logs in.
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Navigate to the site
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent(),
                "The Manager user hasn't Manager permissions to the document (i.e 'Delete' button isn't available)");

        // Verify folder has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent(),
                "The Manager user hasn't Manager permissions to the folder (i.e 'Delete' button isn't available)");

        // Log out user2.
        ShareUser.logout(drone);

        // User3 logs in.
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Edit Off-line button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent(),
                "The Collaborator user hasn't Collaborator  permissions to the document. 'Edit Offline' button isn't available");

        // Verify folder has Edit Properties button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent(),
                "The Collaborator user hasn't Collaborator permissions to the folder. 'Edit Properties' button isn't available");

        // Verify document has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent(),
                "The Collaborator user hasn't Collaborator  permissions to the document. 'Delete' button is available");

        // Verify folder has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent(),
                "The Collaborator user hasn't Collaborator permissions to the folder. 'Delete' button is available");

        // Log out user3.
        ShareUser.logout(drone);

        // User4 logs in.
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Edit Off-line button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent(),
                "The Contributor user hasn't Collaborator  permissions to the document. 'Edit Offline' button isn't available");

        // Verify folder has Edit Properties button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent(),
                "The Contributor user hasn't Collaborator permissions to the folder. 'Edit Properties' button isn't available");

        // Verify document has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent(),
                "The Contributor user hasn't Collaborator  permissions to the document. 'Delete' button is available");

        // Verify folder has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent(),
                "The Contributor user hasn't Collaborator permissions to the folder. 'Delete' button is available");

        // Log out user4.
        ShareUser.logout(drone);

        // User5 logs in.
        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Edit Off-line button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent(),
                "The Consumer user hasn't Collaborator  permissions to the document. 'Edit Offline' button isn't available");

        // Verify folder has Edit Properties button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent(),
                "The Consumer user hasn't Collaborator permissions to the folder. 'Edit Properties' button isn't available");

        // Verify document has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent(),
                "The Consumer user hasn't Collaborator  permissions to the document. 'Delete' button is available");

        // Verify folder has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent(),
                "The Consumer user hasn't Collaborator permissions to the folder. 'Delete' button is available");

        // Log out user5.
        ShareUser.logout(drone);
    }

}
