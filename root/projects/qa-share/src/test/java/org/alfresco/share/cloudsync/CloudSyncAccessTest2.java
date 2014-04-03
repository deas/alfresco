/*
 * Copyright (C) 2005-2013 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.cloudsync;

import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.site.document.ManagePermissionsPage.ButtonType;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractCloudSyncTest;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
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
 * Class includes: Tests from TestLink in Area: Cloud Sync
 * <ul>
 * <li>The tests that check the access level/authentication for cloud sync.</li>
 * </ul>
 * 
 * @author nshah
 */

@Listeners(FailedTestListener.class)
public class CloudSyncAccessTest2 extends AbstractCloudSyncTest
{

    private static Log logger = LogFactory.getLog(CloudSyncAccessTest2.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    /**
     * Alf-1885 1) Create On-Prem user 2) Create a Cloud User 3) Create site on cloud user. 4) Login to On-Premise and set up Cloud Sync
     */
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7009() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName);

        // Enterprise: create user1, user2 (1 network)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: create user1, user2 (as above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[] { testUser2 });

        // Cloud: User1 Creates Site
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // OP: User1 Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // User1 is Sets up CloudSync
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // User1 invites User2 as consumer
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.CONSUMER);

        ShareUser.logout(drone);
    }

    @Test(groups = { "CloudSync" })
    public void ALF_7009() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
            String siteName = getSiteName(testName);
            String fileName = getTestName() + System.currentTimeMillis() + ".txt";

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(siteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            // OP: Login to Alfresco share as user1, Upload a file and sync to Cloud
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB }).render();

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

            // Assert to test content is synced.
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName));

            ShareUser.logout(drone);

            // OP: Log in Alfresco Share as user2
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Synced file is visible in DocLib
            Assert.assertTrue(docLibPage.isFileVisible(fileName));

            Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isViewCloudSyncInfoLinkPresent());

            // 5. Click cloud icon against the file which is synced
            SyncInfoPage syncInfoPage = docLibPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();

            // 6. Assert Pop up dialogue with sync info appears. Unsync button not available
            Assert.assertTrue(syncInfoPage.isSyncStatusPresent());
            Assert.assertFalse(syncInfoPage.isUnsyncButtonPresent());
        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
            logger.error("Failed  : " + t.getMessage());
        }
    }

    /**
     * Test Case 1892- Unsync with previous edit options
     */
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7010() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName);

        // Enterprise: create user1, user2 (1 network)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: create user1, user2 (same as above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[] { testUser2 });

        // OP: User1 Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Sets up sync
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

        // CLOUD: User1 Creates Site
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // OP: User2 joins site created by user1.
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);
        ShareUser.logout(drone);
    }

    @Test(groups = { "CloudSync" })
    public void ALF_7010() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
            String siteName = getSiteName(testName);
            String fileName = getTestName() + System.currentTimeMillis() + ".txt";

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(siteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            // OP: User 1: create file and sync to Cloud
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

            // Assert to test content is synced or not.
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName));

            // User 1 assigns role to User 2 as Collaborator
            ShareUserMembers.setUserRoleWithSite(drone, testUser2, UserRole.COLLABORATOR, siteName);

            ShareUser.logout(drone);

            // OP: User2
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            Assert.assertTrue(docLibPage.isFileVisible(fileName));

            // Assert Edit option is available.
            Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isInlineEditLinkPresent());

            ShareUser.logout(drone);

            // OP: Set user1 as Role: consumer for site of User2
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

            ShareUserMembers.setUserRoleWithSite(drone, testUser2, UserRole.CONSUMER, siteName);

            ShareUser.logout(drone);

            // OP: User 2: Sees Consumer permissions
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Assert Document Library page is opened,
            Assert.assertTrue(docLibPage.isFileVisible(fileName));

            // Assert Edit option is available.
            Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isInlineEditLinkPresent());

            SyncInfoPage syncInfoPage = docLibPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();

            Assert.assertFalse(syncInfoPage.isUnsyncButtonPresent());

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * Test Case 2038- Sync a folder & file without edit options to it
     */
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7012() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        // OP: Create User 1, User 2: (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: Create User 1 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // OP. User1 Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // User2 joins the site created by user1: As Consumer: So no write access by default
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);
        ShareUser.logout(drone);
    }

    @Test(groups = { "CloudSync" })
    public void ALF_7012() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
            String siteName = getSiteName(testName + "-1");
            String folderName = getTestName() + System.currentTimeMillis();
            String fileName = getTestName() + System.currentTimeMillis() + ".txt";

            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
            ShareUserMembers.setUserRoleWithSite(drone, testUser2, UserRole.CONSUMER, siteName);
            ShareUser.logout(drone);

            // OP: User 1
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            ShareUserSitePage.createFolder(drone, folderName, folderName);

            // Step added for 2041
            ShareUser.openDocumentLibrary(drone);

            // Step added for 2041:- Upload a file in DocumentLibrary.
            ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

            ShareUser.logout(drone);

            // OP. User2
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

            // Assert no 'Sync to cloud' option available for created folder
            Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isSyncToCloudLinkPresent());

            // Step added for 2041: Check for file has sync to cloud link is present.
            Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());
            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
        }

    }

    // Test Case 2039 - Sync a non empty with some inaccessible files/folders
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7013() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        // OP: Create User 1, User 2 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: Create User 1, User 2 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[] { testUser2 });

        // OP. User1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // OP: User2 joins the site created by User1
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);
        ShareUser.logout(drone);

        // Cloud: User1: Creates Site
        ShareUser.login(hybridDrone, testUser2, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = { "CloudSync" })
    public void ALF_7013() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

            String siteName = getSiteName(testName + "-1");

            String folderName = getTestName() + System.currentTimeMillis();
            String subFolderName = getTestName() + System.currentTimeMillis() + "sub";
            String subFileName = getTestName() + System.currentTimeMillis() + ".txt";

            DestinationAndAssigneeBean destAndAssBean = new DestinationAndAssigneeBean();
            destAndAssBean.setNetwork(hybridDomainPremium);
            destAndAssBean.setSiteName(siteName);

            // OP: User1: Creates a folder and a folder and file, inside this folder
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folderName, folderName);

            docLibPage.selectFolder(folderName);
            ShareUserSitePage.createFolder(drone, subFolderName, subFolderName);
            ShareUser.uploadFileInFolder(drone, new String[] { subFileName, folderName });

            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            docLibPage = (DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(drone, testUser2, folderName, UserRole.COLLABORATOR, false);

            docLibPage = docLibPage.selectFolder(folderName).render();

            docLibPage = docLibPage.renderItem(maxWaitTime, subFolderName).render();

            docLibPage = (DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(drone, testUser2, subFolderName, UserRole.CONSUMER, false);

            docLibPage = (DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(drone, testUser2, subFileName, UserRole.CONSUMER, false);

            ShareUser.logout(drone);

            // OP: User 2
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            docLibPage = AbstractCloudSyncTest.syncContentToCloud(drone, folderName, destAndAssBean);

            Assert.assertTrue(checkIfContentIsSynced(drone, folderName));

            ShareUser.logout(drone);

            // Cloud: User 2: Check synced content in Cloud
            ShareUser.login(hybridDrone, testUser2, DEFAULT_PASSWORD);
            docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);

            Assert.assertTrue(docLibPage.isFileVisible(folderName));

            docLibPage = docLibPage.selectFolder(folderName).render();

            Assert.assertFalse(docLibPage.isFileVisible(subFolderName));
            Assert.assertFalse(docLibPage.isFileVisible(subFileName));

            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /*
     * ALF-2040:Update the root folder after write access to some files/sub folders removed 1. Create user1, user2 and user2 on cloud. 2. Create Site using
     * user1. 3. User2 joins the User1's site as default role of CONSUMER. 4. User2 on Cloud create a site. 5. User2 sync with the User2 on Cloud.
     */
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7014() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-02", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        // OP: Create User 1, User 2 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: Create User 1, User 2 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[] { testUser2 });

        // OP: User1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // OP: User2 joins the site created by user1 and sets up cloudSync
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);

        signInToAlfrescoInTheCloud(drone, testUser2, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

        // Cloud: User1: Creates Site
        ShareUser.login(hybridDrone, testUser2, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    /*
     * 1. User1 creates Folder(root), sub-folder, and sub-file in root folder. 2. User1 set permission of Root Folder - Collaborator, Sub-folder -Collaborator,
     * Sub-File - Collaborator. 3. User2 sync content created by User1 with cloud user -User2. 4. User1 logs in from another browser and change sub-folder &
     * subfile permissions to CONSUMER. 5. User2 continues from step2 to modify edit (root)folder properties and save it. 6. User2 logins to cloud account and
     * assert edited properties present for root folder. 7. User2 assert to verify that sub-folder and sub-file are present.
     */

    @Test(groups = { "CloudSync" })
    public void ALF_7014() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String testUser2 = getUserNameForDomain(testName + "-02", hybridDomainPremium);

            String siteName = getSiteName(testName + "-1");
            String folderName = getTestName() + System.currentTimeMillis();
            String subFolderName = getTestName() + System.currentTimeMillis() + "sub";
            String subFileName = getTestName() + System.currentTimeMillis() + ".txt";

            String propDescription = " assert to test";

            DestinationAndAssigneeBean destAndAssBean = new DestinationAndAssigneeBean();
            destAndAssBean.setNetwork(hybridDomainPremium);
            destAndAssBean.setSiteName(siteName);

            // OP: User 1
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName);
            DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folderName, folderName);
            docLibPage.selectFolder(folderName);

            ShareUserSitePage.createFolder(drone, subFolderName, subFolderName);
            ShareUser.uploadFileInFolder(drone, new String[] { subFileName, folderName });

            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            docLibPage = (DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(drone, testUser2, folderName, UserRole.COLLABORATOR, false);

            docLibPage = docLibPage.selectFolder(folderName).render();

            ShareUserMembers.managePermissionsOnContent(drone, testUser2, subFolderName, UserRole.COLLABORATOR, false);
            ShareUserMembers.managePermissionsOnContent(drone, testUser2, subFileName, UserRole.COLLABORATOR, false);

            ShareUser.logout(drone);

            // OP: User 2
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            // OP: User 2: Syncs to Cloud
            docLibPage = ((DocumentLibraryPage) AbstractCloudSyncTest.syncContentToCloud(drone, folderName, destAndAssBean)).render();

            Assert.assertTrue(checkIfContentIsSynced(drone, folderName));

            // OP: User 1
            WebDrone anotherDrone = getSecondDrone();
            anotherDrone.navigateTo(getShareUrl());
            LoginPage loginPage = (LoginPage) anotherDrone.getCurrentPage();

            loginPage.loginAs(testUser1, DEFAULT_PASSWORD);

            docLibPage = ShareUser.openSitesDocumentLibrary(anotherDrone, siteName);

            docLibPage = docLibPage.selectFolder(folderName).render();

            docLibPage = ShareUserMembers.managePermissionsOnContent(anotherDrone, testUser2, subFolderName, UserRole.CONSUMER, false).render();

            docLibPage = ShareUserMembers.managePermissionsOnContent(anotherDrone, testUser2, subFileName, UserRole.CONSUMER, false).render();

            ShareUser.logout(anotherDrone);
            anotherDrone.quit();

            // OP: User 2
            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
            docLibPage = docLibPage.selectFolder(folderName).render();

            // Confirm consumer Access on SubFolder and SubFiles
            Assert.assertFalse(docLibPage.getFileDirectoryInfo(subFolderName).isInlineEditLinkPresent());
            Assert.assertFalse(docLibPage.getFileDirectoryInfo(subFileName).isInlineEditLinkPresent());

            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Confirm Write access on folder
            EditDocumentPropertiesPage editDocumentProp = docLibPage.getFileDirectoryInfo(folderName).selectEditProperties().render();
            editDocumentProp.setDescription(propDescription);
            docLibPage = editDocumentProp.selectSave().render();

            Assert.assertTrue(checkIfContentIsSynced(drone, folderName));
            ShareUser.logout(drone);

            // Cloud: User 2
            ShareUser.login(hybridDrone, testUser2, DEFAULT_PASSWORD);
            docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName).render();

            Assert.assertTrue(docLibPage.isFileVisible(folderName));

            editDocumentProp = docLibPage.getFileDirectoryInfo(folderName).selectEditProperties().render();
            Assert.assertEquals(editDocumentProp.getDescription(), propDescription);
            docLibPage = editDocumentProp.selectSave().render();
            docLibPage = docLibPage.selectFolder(folderName).render();

            docLibPage = docLibPage.renderItem(maxWaitTime, subFolderName);
            Assert.assertTrue(docLibPage.isFileVisible(subFolderName));
            Assert.assertTrue(docLibPage.isFileVisible(subFileName));

            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * Test Case 2041- Sync a file without edit options to it
     */
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7015() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        // OP: Create User 1, User 2 (Same Network 1),
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: Create User 1 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // OP: User1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // OP: User2 joins the site created by user1
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);
        ShareUser.logout(drone);

        // OP: User 1 makes User 2 Site Consumer
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUserMembers.setUserRoleWithSite(drone, testUser2, UserRole.CONSUMER, siteName);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = { "CloudSync" })
    public void ALF_7015() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
            String siteName = getSiteName(testName + "-1");
            String fileName = getTestName() + System.currentTimeMillis() + ".txt";

            // OP: User1: Uploads a file
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName);
            ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

            DestinationAndAssigneeBean destAndAssBean = new DestinationAndAssigneeBean();
            destAndAssBean.setNetwork(hybridDomainPremium);
            destAndAssBean.setSiteName(siteName);

            syncContentToCloud(drone, fileName, destAndAssBean);

            Assert.assertTrue(checkIfContentIsSynced(drone, fileName));

            ShareUser.logout(drone);

            // OP: User2
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

            // OP: User 2: Can't Sync To Cloud but can request to Sync
            Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());
            Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isRequestToSyncLinkPresent());
        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
        }

    }

    /*
     * <ul> <li>Create User1 and User2 (OP)</li> <li>Login as User1, create a site and upload 4 document</li> <li>Invited user2 as collaborator on user1
     * site</li> <li>Restrict access on 2 documents for user2</li> <li>Login as cloud user and create site</li> </ul>
     */
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7003() throws Exception
    {
        String testName = getTestName();
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");
        String fileName3 = getFileName(testName + "3");
        String fileName4 = getFileName(testName + "4");
        String opSiteName = getSiteName(testName);
        String cloudSiteName = getSiteName(testName + "cloud");

        String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String cloudUser1 = getUserNamePremiumDomain(testName + "2");

        String[] userInfo1 = new String[] { user1 };
        String[] userInfo2 = new String[] { user2 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] fileInfo1 = { fileName1 };
        String[] fileInfo2 = { fileName2 };
        String[] fileInfo3 = { fileName3 };
        String[] fileInfo4 = { fileName4 };

        try
        {
            // Create Users (On-premise)
            CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
            CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo2);

            // Create User (Cloud)
            CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, cloudUserInfo1);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            // Invite user to Site as Collaborator and log-out the current user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);

            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Upload a file1
            ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            ShareUser.uploadFileInFolder(drone, fileInfo1);

            // Assign consumer role to User 2 on the content
            DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName1);
            detailsPage.selectManagePermissions().render();
            ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.COLLABORATOR, true);

            // Upload a file2
            ShareUser.openDocumentLibrary(drone);
            ShareUser.uploadFileInFolder(drone, fileInfo2);

            // Assign consumer role to User 2 on the content
            ShareUser.openDocumentDetailPage(drone, fileName2);
            detailsPage.selectManagePermissions().render();
            ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.COLLABORATOR, true);

            // Upload a file3
            ShareUser.openDocumentLibrary(drone);
            ShareUser.uploadFileInFolder(drone, fileInfo3);

            // Assign consumer role to User 2 on the content
            ShareUser.openDocumentDetailPage(drone, fileName3);
            detailsPage.selectManagePermissions().render();
            ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.CONSUMER, false);

            // Upload a file4
            ShareUser.openDocumentLibrary(drone);
            ShareUser.uploadFileInFolder(drone, fileInfo4);

            // Assign consumer role to User 2 on the content
            ShareUser.openDocumentDetailPage(drone, fileName4);
            detailsPage.selectManagePermissions().render();
            ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.CONSUMER, false);

            ShareUser.logout(drone);

            // Login as Cloud User
            ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

            ShareUser.logout(hybridDrone);

            ShareUser.login(drone, user2, DEFAULT_PASSWORD);

            signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

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

    /**
     * <ul>
     * <li>Login as user 2</li>
     * <li>User2 sets the cloud sync - using cloud user’s credentials</li>
     * <li>Access the invited site</li>
     * <li>Validate file1, file2, file3,file4 is present in doc library</li>
     * <li>Select All files</li>
     * <li>Choose 'Sync on Cloud' option from selected items</li>
     * <li>Choose target location in the Cloud and click sync button</li>
     * <li>Verify that successful files sync appears for file1 and file2</li>
     * <li>Verify that sync should not be appeared for file3 and file4</li>
     * <li>Login cloud</li>
     * <li>Verify that synced files should appear</li>
     * </ul>
     */
    @Test(groups = { "CloudSync" })
    public void ALF_7003() throws Exception
    {
        String testName = getTestName();
        String fileName1 = getFileName(testName + "1");
        String fileName2 = getFileName(testName + "2");
        String fileName3 = getFileName(testName + "3");
        String fileName4 = getFileName(testName + "4");
        String opSiteName = getSiteName(testName);
        String cloudSiteName = getSiteName(testName + "cloud");

        String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String cloudUser1 = getUserNamePremiumDomain(testName + "2");

        DestinationAndAssigneeBean destinationBean = new DestinationAndAssigneeBean();
        destinationBean.setNetwork(getUserDomain(cloudUser1));
        destinationBean.setSiteName(cloudSiteName);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Site Document Library from search
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, fileName1);
        if (documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced())
        {
            selectUnSyncAndRemoveContentFromCloud(drone, fileName1);
        }
        documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, fileName2);
        if (documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced())
        {
            selectUnSyncAndRemoveContentFromCloud(drone, fileName2);
        }
        documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, fileName3);
        if (documentLibraryPage.getFileDirectoryInfo(fileName3).isCloudSynced())
        {
            selectUnSyncAndRemoveContentFromCloud(drone, fileName3);
        }
        documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, fileName4);
        if (documentLibraryPage.getFileDirectoryInfo(fileName4).isCloudSynced())
        {
            selectUnSyncAndRemoveContentFromCloud(drone, fileName4);
        }

        ShareUser.logout(drone);

        // OP: User 2: Sets up cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Open Site Document Library from search
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1));
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName2));
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName3));
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName4));

        documentLibraryPage = documentLibraryPage.getNavigation().selectAll().render();

        AbstractCloudSyncTest.syncAllContentToCloud(drone, destinationBean);

        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced());
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName3).isCloudSynced());
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName4).isCloudSynced());

        ShareUser.logout(drone);

        // Cloud: Login as CloudUser
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1));
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName2));

        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName3));
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName4));

        ShareUser.logout(hybridDrone);
    }

    /*
     * Test Case 2042- Request sync to file/folder as owner Given (preconditions) 1. Enterprise: create user1 2. Cloud: create user1 OP: 3. User1 is logged in
     * on-premise 4. Create any site in on-premise by user1
     */
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7016() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        // OP: Create User 1 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Cloud: Create User 1 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // OP: User 1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // OP: User 1: Sets up Cloud Sync
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

        // Cloud: User1: Creates Site
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    /*
     * 6. Open Document Library page of the created site (e.g. Test) 7. Create any file/folder in the document library and synced to cloud by user1 Test steps
     * 1. Go to the site->document library-> created file/folder 2. Change the content of synced file/folder in on -premise as owner 3. Assert content changed
     * on-premise 4. Choose ‘Request to sync’ option from More+ menu 5. Go the the cloud target location where file/folder is synced 6. Assert content changed
     * in cloud
     */
    @Test(groups = { "CloudSync" })
    public void ALF_7016() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        String fileName = getTestName() + System.currentTimeMillis() + ".txt";
        String folderName = getTestName() + System.currentTimeMillis();

        String fileDesc = fileName + " testing !!";
        String folderDesc = folderName + " testing !!";
        DestinationAndAssigneeBean destAndAssBean = new DestinationAndAssigneeBean();
        destAndAssBean.setNetwork(hybridDomainPremium);
        destAndAssBean.setSiteName(siteName);

        // OP: User 1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folderName, folderName);

        EditDocumentPropertiesPage documentProp = docLibPage.getFileDirectoryInfo(fileName).selectEditProperties().render();

        documentProp.setDescription(fileDesc);
        docLibPage = documentProp.selectSave().render();
        documentProp = docLibPage.getFileDirectoryInfo(folderName).selectEditProperties().render();
        documentProp.setDescription(folderDesc);
        docLibPage = documentProp.selectSave().render();

        docLibPage = syncContentToCloud(drone, folderName, destAndAssBean);
        docLibPage = docLibPage.renderItem(maxWaitTime, fileName);
        docLibPage = syncContentToCloud(drone, fileName, destAndAssBean);
        docLibPage = docLibPage.renderItem(maxWaitTime, folderName);

        Assert.assertTrue(checkIfContentIsSynced(drone, folderName));
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName));
        ShareUser.logout(drone);

        // Cloud: User 1: Confirm the changes are reflected after sync to cloud

        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName).render();

        documentProp = docLibPage.getFileDirectoryInfo(fileName).selectEditProperties().render();
        Assert.assertEquals(documentProp.getDescription(), fileDesc);
        docLibPage = documentProp.selectSave().render();

        documentProp = docLibPage.getFileDirectoryInfo(folderName).selectEditProperties().render();
        Assert.assertEquals(documentProp.getDescription(), folderDesc);
        documentProp.selectSave().render();

        ShareUser.logout(hybridDrone);
    }

    /*
     * Test Case 2043- Request sync multiple files without edit options to some files
     */
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7018() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        String[] userInfo = { testUser1 };

        // OP: User 1, User 2
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: User 1, User 2
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[] { testUser2 });

        // // OP: User 1: Creates Site
        // ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        // ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // ShareUser.logout(drone);
        //
        // // OP: User 2: Joins Site above
        // ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        // ShareUserMembers.userRequestToJoinSite(drone, siteName);
        // ShareUser.logout(drone);
        //
        // // Cloud: User 1: Creates Site
        // ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        // ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        // ShareUser.logout(hybridDrone);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /*
     * Try to request sync for multiple synced files, when user does not have write access to some of the files
     */
    @Test(groups = { "CloudSync" })
    public void ALF_7018() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName) + System.currentTimeMillis();

        String fileName1 = getTestName() + System.currentTimeMillis() + "-1.txt";
        String fileName2 = getTestName() + System.currentTimeMillis() + "-2.txt";
        String fileName3 = getTestName() + System.currentTimeMillis() + "-3.txt";

        String file1Desc = "InlineEdit Description of file1";
        String file2Desc = "InlineEdit Description of file2";
        String file3Desc = "InlineEdit Description of file3";
        String file1Content = getTestName() + " InlineEdit Content of file1";
        String file2Content = getTestName() + " InlineEdit Content of file2";
        String file3Content = getTestName() + " InlineEdit Content of file3";

        DestinationAndAssigneeBean destAndAssBean = new DestinationAndAssigneeBean();
        destAndAssBean.setNetwork(hybridDomainPremium);
        destAndAssBean.setSiteName(siteName);

        // OP: User 1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // OP: User 2: Joins Site above
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);
        ShareUser.logout(drone);

        // Cloud: User 1: Creates Site
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // OP: User 1: Uploads files
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1 });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2 });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName3 });

        // OP: User 1: Sets content level permission for User 2
        ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName1, UserRole.CONSUMER, false);
        ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName2, UserRole.COLLABORATOR, false);
        DocumentLibraryPage documentLibraryPage = ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName3, UserRole.COLLABORATOR, false).render();

        documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, fileName1);
        // OP: User 1: Syncs files to Cloud
        AbstractCloudSyncTest.syncContentToCloud(drone, fileName1, destAndAssBean);
        AbstractCloudSyncTest.syncContentToCloud(drone, fileName2, destAndAssBean);
        DocumentLibraryPage docLibPage = AbstractCloudSyncTest.syncContentToCloud(drone, fileName3, destAndAssBean);

        Assert.assertTrue(checkIfContentIsSynced(drone, fileName1));
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName2));
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName3));

        docLibPage = drone.getCurrentPage().render();
        docLibPage = docLibPage.renderItem(maxWaitTime, fileName1);
        // OP: User 1: Edits content
        DocumentDetailsPage documentDetailsPage = docLibPage.selectFile(fileName1).render();

        // Select "In line Edit", modify details
        ShareUser.editTextDocument(drone, fileName1, file1Desc, file1Content);

        docLibPage = ShareUser.openDocumentLibrary(drone);
        documentDetailsPage = docLibPage.selectFile(fileName2).render();

        // Select "In line Edit", modify details
        ShareUser.editTextDocument(drone, fileName2, file2Desc, file2Content);

        docLibPage = ShareUser.openDocumentLibrary(drone);

        documentDetailsPage = docLibPage.selectFile(fileName3).render();

        // Select "In line Edit", modify details
        ShareUser.editTextDocument(drone, fileName3, file3Desc, file3Content);
        ShareUser.logout(drone);

        // OP: User 2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isViewCloudSyncInfoLinkPresent());
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName2).isViewCloudSyncInfoLinkPresent());
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName3).isViewCloudSyncInfoLinkPresent());

        docLibPage.getNavigation().selectAll();
        docLibPage.getNavigation().selectRequestSync();

        Assert.assertTrue(checkIfContentIsSynced(drone, fileName1));
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName2));
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName3));

        ShareUser.logout(drone);

        // Cloud: User 1:
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);

        documentDetailsPage = docLibPage.selectFile(fileName1).render();

        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        ContentDetails contentDetails = inlineEditPage.getDetails();
        Assert.assertEquals(contentDetails.getDescription(), file1Desc);
        // Assert.assertEquals(contentDetails.getContent(), file1Content); TODO - A known issue with reading Content filed (WEBDRONE-361)
        inlineEditPage.selectCancel().render();

        ShareUser.openDocumentLibrary(hybridDrone);
        documentDetailsPage = ShareUser.openDocumentDetailPage(hybridDrone, fileName2);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        contentDetails = inlineEditPage.getDetails();
        Assert.assertEquals(contentDetails.getDescription(), file2Desc);
        // Assert.assertEquals(contentDetails.getContent(), file2Content); TODO - A known issue with reading Content filed (WEBDRONE-361)
        inlineEditPage.selectCancel().render();

        ShareUser.openDocumentLibrary(hybridDrone);
        documentDetailsPage = ShareUser.openDocumentDetailPage(hybridDrone, fileName3);
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        contentDetails = inlineEditPage.getDetails();
        Assert.assertEquals(contentDetails.getDescription(), file3Desc);
        // Assert.assertEquals(contentDetails.getContent(), file3Content); TODO - A known issue with reading Content filed (WEBDRONE-361)
        inlineEditPage.selectCancel().render();

        ShareUser.logout(hybridDrone);
    }

    /*
     * <ul> <li>Create User1, User2, User3, User4 (OP)</li> <li>Login as User1, create a site and upload a document</li> <li>Sync added document and unsync it
     * as well.</li> <li>User2, user3, user4 are members of the site respectively with site collaborator, site contributor and site consumer roles</li>
     * <li>Login as cloud user and create site</li> </ul>
     */
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_2118() throws Exception
    {
        String testName = getTestName();
        String fileName1 = getFileName(testName);
        String opSiteName = getSiteName(testName);
        String cloudSiteName = getSiteName(testName + "cloud");

        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String collaborator = getUserNameFreeDomain(testName + "Colloaborator");
        String contributor = getUserNameFreeDomain(testName + "Contributor");
        String consumer = getUserNameFreeDomain(testName + "Consumer");
        String cloudUser = getUserNamePremiumDomain(testName + "Cloud");

        String[] siteAdminInfo = new String[] { siteAdmin };
        String[] collaboratorInfo = new String[] { collaborator };
        String[] contributorInfo = new String[] { contributor };
        String[] consumerInfo = new String[] { consumer };

        String[] cloudUserInfo = new String[] { cloudUser };

        String[] fileInfo1 = { fileName1 };

        // Select network and site and click on sync
        DestinationAndAssigneeBean destinationBean = new DestinationAndAssigneeBean();
        destinationBean.setNetwork(getUserDomain(cloudUser));
        destinationBean.setSiteName(cloudSiteName);

        try
        {
            // OP: Create Users:
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, siteAdminInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, collaboratorInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, contributorInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, consumerInfo);

            // Cloud: Create User 1: for Cloud sync
            CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);

            // Cloud: User 1: Creates Site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // OP: SiteAdmin: Sets up cloudSync
            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

            signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

            // OP: SiteAdmin: Creates content and syncs content to cloud
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, fileInfo1).render();
            AbstractCloudSyncTest.syncContentToCloud(drone, fileName1, destinationBean);

            // OP: SiteAdmin: Unsync the document
            AbstractCloudSyncTest.unSyncFromCloud(drone, fileName1);

            // OP: SiteAdmin: Invites user <collaborator> to Site as Collaborator
            ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, collaborator, opSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);

            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);
            // OP: SiteAdmin: Invites user <contributor> to Site as Contributor
            ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, contributor, opSiteName, UserRole.CONTRIBUTOR);
            ShareUser.logout(drone);

            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);
            // OP: SiteAdmin: Invites user <Consumer> to Site as Consumer
            ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, consumer, opSiteName, UserRole.CONSUMER);
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

    /**
     * <ul>
     * <li>Login as siteAdmin</li>
     * <li>open site document library page</li>
     * <li>verify that sync should not be present</li>
     * <li>Login as Contributor</li>
     * <li>open site document library page</li>
     * <li>verify that sync should not be present</li>
     * <li>Login as Collaborator</li>
     * <li>open site document library page</li>
     * <li>verify that sync should not be present</li>
     * <li>Login as Consumer</li>
     * <li>open site document library page</li>
     * <li>verify that sync should not be present</li>
     * </ul>
     */
    @Test(groups = { "CloudSync" })
    public void ALF_2118() throws Exception
    {
        String testName = getTestName();
        String fileName1 = getFileName(testName);
        String opSiteName = getSiteName(testName);

        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String collaborator = getUserNameFreeDomain(testName + "Colloaborator");
        String contributor = getUserNameFreeDomain(testName + "Contributor");
        String consumer = getUserNameFreeDomain(testName + "Consumer");
        String cloudUser = getUserNamePremiumDomain(testName + "Cloud");

        // OP: siteAdmin
        ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1));
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced());

        ShareUser.logout(drone);

        // OP: Collaborator
        ShareUser.login(drone, collaborator, DEFAULT_PASSWORD);

        // Open Site Document Library from search
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Confirm file is not synced
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1));
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced());

        ShareUser.logout(drone);

        // OP: Contributor
        ShareUser.login(drone, contributor, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Confirm file is not synced
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1));
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced());

        ShareUser.logout(drone);

        // OP: Consumer
        ShareUser.login(drone, consumer, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        // Confirm file is not synced
        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1));
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced());

        ShareUser.logout(drone);
    }

    /*
     * UnSync Content from Cloud as SiteManager
     */
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7037() throws Exception
    {
        String testName = getTestName();
        String opSiteName = getSiteName(testName);
        String cloudSiteName = getSiteName(testName + "cloud");

        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String collaborator = getUserNameFreeDomain(testName + "Colloaborator");
        String cloudUser = getUserNamePremiumDomain(testName + "Cloud");

        // OP: Users siteAdmin, collaborator
        String[] siteAdminInfo = new String[] { siteAdmin };
        String[] collaboratorInfo = new String[] { collaborator };

        // Cloud: cloudUser
        String[] cloudUserInfo = new String[] { cloudUser };

        try
        {
            // OP: Create User siteAdmin, collaborator
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, siteAdminInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, collaboratorInfo);

            // Cloud: Create cloudUser
            CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo);

            // Cloud: cloudUser Creates Site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // OP: siteAdmin: Creates Site
            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            // OP: siteAdmin: Invite user <collaborator> as Site collaborator.
            ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, collaborator, opSiteName, UserRole.COLLABORATOR);

            ShareUser.logout(drone);

            ShareUser.login(drone, collaborator, DEFAULT_PASSWORD);

            signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

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

    /**
     * UnSync Content from Cloud as SiteManager
     */
    @Test(groups = { "CloudSync" })
    public void ALF_7037() throws Exception
    {
        String testName = getTestName();
        String fileName1 = getFileName(testName) + System.currentTimeMillis();
        String opSiteName = getSiteName(testName);
        String cloudSiteName = getSiteName(testName + "cloud");

        String collaborator = getUserNameFreeDomain(testName + "Colloaborator");
        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String cloudUser = getUserNamePremiumDomain(testName + "Cloud");

        // Select network and site and click on sync
        DestinationAndAssigneeBean destinationBean = new DestinationAndAssigneeBean();
        destinationBean.setNetwork(getUserDomain(cloudUser));
        destinationBean.setSiteName(cloudSiteName);

        String[] fileInfo1 = { fileName1 };

        // OP: collaborator: sync file to Cloud
        ShareUser.login(drone, collaborator, DEFAULT_PASSWORD);

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUser.uploadFileInFolder(drone, fileInfo1);

        AbstractCloudSyncTest.syncContentToCloud(drone, fileName1, destinationBean);

        Assert.assertTrue(checkIfContentIsSynced(drone, fileName1), "Sync is not successful");

        ShareUser.logout(drone);

        // OP: Unsync file as siteAdmin
        ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        Assert.assertTrue(checkIfContentIsSynced(drone, fileName1));
        documentLibraryPage = selectUnSyncAndRemoveContentFromCloud(drone, fileName1);
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isSyncToCloudLinkPresent());

        ShareUser.logout(drone);

        // Cloud: cloudUser
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

        // Confirm that un-synced file is removed from cloud
        Assert.assertFalse(documentLibraryPage.isFileVisible(fileName1));

        ShareUser.logout(hybridDrone);
    }

    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7017() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        // OP: Create User 1, User 2 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: Create User 1 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // OP: User 1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // OP: User 1: Invites User 2 to site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.CONSUMER);
        ShareUser.logout(drone);

        // Cloud: User 1: Creates Site
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // OP: User 2: Sets up CloudSync
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    @Test(groups = { "CloudSync" })
    public void ALF_7017() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");
        DestinationAndAssigneeBean destAndAssBean = new DestinationAndAssigneeBean();
        destAndAssBean.setNetwork(hybridDomainPremium);
        destAndAssBean.setSiteName(siteName);

        String fileDesc = "Assert Test file desc!!";
        String fileContent = "Assert Content in test file";

        String fileName = getTestName() + System.currentTimeMillis() + ".txt";
        try
        {
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(drone, siteName);
            ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, destAndAssBean);

            Assert.assertTrue(checkIfContentIsSynced(drone, fileName));

            ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName, UserRole.COLLABORATOR, false);

            ShareUser.logout(drone);

            // OP: User 2
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage docDetailPage = docLibPage.selectFile(fileName).render();
            docDetailPage = ShareUser.editTextDocument(drone, fileName, fileDesc, fileContent);

            // Open another browser window
            WebDrone anotherDrone = getSecondDrone();
            anotherDrone.navigateTo(getShareUrl());

            // OP: User 1
            ShareUser.login(anotherDrone, testUser1, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(anotherDrone, siteName);

            ShareUserMembers.managePermissionsOnContent(anotherDrone, testUser2, fileName, UserRole.CONSUMER, false);

            ShareUser.logout(anotherDrone);

            anotherDrone.quit();

            // Back to OP: User 2: Request Sync
            docDetailPage = (DocumentDetailsPage) drone.getCurrentPage();
            docLibPage = docDetailPage.getSiteNav().selectSiteDocumentLibrary().render();
            docLibPage = docLibPage.getFileDirectoryInfo(fileName).selectRequestSync().render();
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName));
            ShareUser.logout(drone);

            // Cloud: User 1:
            ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);

            docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);

            docDetailPage = docLibPage.selectFile(fileName).render();
            EditTextDocumentPage inlineEditPage = docDetailPage.selectInlineEdit().render();

            ContentDetails contentDetails = inlineEditPage.getDetails();
            Assert.assertEquals(contentDetails.getName(), fileName);
            Assert.assertEquals(contentDetails.getDescription(), fileDesc);
            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * Request Sync to multiple content without write access to sme folders
     */

    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7019() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        // 1. Enterprise: Create User 1 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        // 2. Cloud: Create User 1 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);
        // 3. Enterprise: Create User 2 (Network 1)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // 3. User1 is logged in Alfresco Share (On-premise)
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * Multiple folders are created and synced to Cloud by user1
     * User2 has no write access to some of folders created/uploaded by user1
     */
    @Test(groups = { "CloudSync" })
    public void ALF_7019() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName) + System.currentTimeMillis();

        String folder1 = testName + System.currentTimeMillis() + "01";
        String folder2 = testName + System.currentTimeMillis() + "02";
        String folder3 = testName + System.currentTimeMillis() + "03";
        String folder4 = testName + System.currentTimeMillis() + "04";

        DestinationAndAssigneeBean destAndAssBean = new DestinationAndAssigneeBean();
        destAndAssBean.setNetwork(hybridDomainPremium);
        destAndAssBean.setSiteName(siteName);

        String folderDesc1 = "I am Folder1!!";
        String folderDesc2 = "I am Folder2!!";
        String folderDesc3 = "I am Folder3!!";
        String folderDesc4 = "I am Folder4!!";

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        // 4. Create any site in Alfresco Share (On-premise) by user1
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.CONSUMER);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // OP: User 1:
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.createFolder(drone, folder1, folder1);
        ShareUserSitePage.createFolder(drone, folder2, folder2);
        ShareUserSitePage.createFolder(drone, folder3, folder3);
        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folder4, folder4);

        AbstractCloudSyncTest.syncContentToCloud(drone, folder1, destAndAssBean);
        ShareUserMembers.managePermissionsOnContent(drone, testUser2, folder1, UserRole.CONSUMER, false).render();
        Assert.assertTrue(checkIfContentIsSynced(drone, folder1));
        ShareUser.editProperties(drone, folder1, folderDesc1);

        AbstractCloudSyncTest.syncContentToCloud(drone, folder2, destAndAssBean);
        ShareUserMembers.managePermissionsOnContent(drone, testUser2, folder2, UserRole.COLLABORATOR, false);
        Assert.assertTrue(checkIfContentIsSynced(drone, folder2));
        ShareUser.editProperties(drone, folder2, folderDesc2);

        AbstractCloudSyncTest.syncContentToCloud(drone, folder3, destAndAssBean);
        ShareUserMembers.managePermissionsOnContent(drone, testUser2, folder3, UserRole.CONSUMER, false);
        Assert.assertTrue(checkIfContentIsSynced(drone, folder3));
        ShareUser.editProperties(drone, folder3, folderDesc3);

        AbstractCloudSyncTest.syncContentToCloud(drone, folder4, destAndAssBean);
        ShareUserMembers.managePermissionsOnContent(drone, testUser2, folder4, UserRole.COLLABORATOR, false);
        Assert.assertTrue(checkIfContentIsSynced(drone, folder4));
        ShareUser.editProperties(drone, folder4, folderDesc4);

        ShareUser.logout(drone);

        // OP: User 2: check content is cloud synced
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentLibraryNavigation docLibNavigation = docLibPage.getNavigation().render();
        docLibNavigation.selectAll().render();
        docLibPage = docLibNavigation.selectRequestSync().render();

        Assert.assertTrue(checkIfContentIsSynced(drone, folder1));
        Assert.assertTrue(checkIfContentIsSynced(drone, folder2));
        Assert.assertTrue(checkIfContentIsSynced(drone, folder3));
        Assert.assertTrue(checkIfContentIsSynced(drone, folder4));

        ShareUser.logout(drone);

        // Cloud: User 1
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folder1).isCloudSynced());
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(folder1).getDescription(), folderDesc1);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folder2).isCloudSynced());
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(folder2).getDescription(), folderDesc2);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folder3).isCloudSynced());
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(folder3).getDescription(), folderDesc3);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folder4).isCloudSynced());
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(folder4).getDescription(), folderDesc4);

        ShareUser.logout(hybridDrone);
    }

    // 1. At least two users are created (user1,user2) in Alfresco Share
    // (On-primise)
    // 2. At least one network and one site are setup in Cloud
    // 3. Both users (user1,user2) have access to the Cloud account
    // 4. Any site (e.g. Test) is created in Alfresco Share (On-premise) by
    // user1
    // 5. Document Library page of the created site (e.g. Test) is opened
    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7023() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String testUser3 = getUserNameForDomain(testName + "-3", hybridDomainPremium);

        String siteName = getSiteName(testName + "-1");

        // 1. Enterprise: Create User 1 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        // 2. Cloud: Create User 1 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);
        // 3. Enterprise: Create User 2 (Network 1)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser3 });

        // 3. User1 is logged in Alfresco Share (On-premise)
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        // 4. Create any site (e.g. Test) in Alfresco Share (On-premise) by
        // user1
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // User2 joins the site created by user1 and log-out the current user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.CONSUMER);
        ShareUser.logout(drone);

        // User3 joins the site created by user1 and log-out the current user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser3, siteName, UserRole.CONSUMER);
        ShareUser.logout(drone);

        // admin user joins the site.
        ShareUser.login(drone, adminUserPrem, DEFAULT_PASSWORD);

        ShareUserMembers.userRequestToJoinSite(drone, siteName);

        ShareUser.logout(drone);

        // 3. User1 is logged in Alfresco Share (On-premise)
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);

        // 4. Create any site (e.g. Test) in Alfresco Share (On-premise) by user1
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    @Test(groups = { "CloudSync" })
    public void ALF_7023() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String testUser3 = getUserNameForDomain(testName + "-3", hybridDomainPremium);

        String siteName = getSiteName(testName + "-1");
        String fileName = testName + System.currentTimeMillis() + ".txt";

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setLockOnPrem(true);

        // Admin user setup cloud sync with Cloud User
        ShareUser.login(drone, adminUserPrem, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // OP: User1 creates a file and assigns permission to user2 and user3.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });
        ((DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName, UserRole.COLLABORATOR, false)).render();
        ((DocumentLibraryPage) ShareUserMembers.managePermissionsOnContent(drone, testUser3, fileName, UserRole.SITEMANAGER, false)).render();
        ShareUser.logout(drone);

        // OP: User 2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName));
        ShareUser.logout(drone);

        // Cloud: User 1
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        Assert.assertTrue(checkIfContentIsSynced(hybridDrone, fileName));
        ShareUser.logout(hybridDrone);

        // OP: User 3
        ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        AbstractCloudSyncTest.requestSyncToCloud(drone, siteName, fileName);
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName));
        ShareUser.logout(drone);

        // Cloud: User 1
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        Assert.assertTrue(checkIfContentIsSynced(hybridDrone, fileName));
        ShareUser.logout(hybridDrone);

        // OP: User 1: Request to sync
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        AbstractCloudSyncTest.requestSyncToCloud(drone, siteName, fileName);
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName));
        ShareUser.logout(drone);

        // Cloud: User 1
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        Assert.assertTrue(checkIfContentIsSynced(hybridDrone, fileName));
        ShareUser.logout(hybridDrone);

        // OP: admin: Request sync
        ShareUser.login(drone, adminUserPrem, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        AbstractCloudSyncTest.requestSyncToCloud(drone, siteName, fileName);
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName));
        ShareUser.logout(drone);

        // Cloud: User 1
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        Assert.assertTrue(checkIfContentIsSynced(hybridDrone, fileName));
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7024() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        String cloudSiteName = getSiteName(testName + "-1");

        // OP: Create User 1, User 2 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: Create User 1 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // OP: User 1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        // OP: User 1: Sets up cloudSync
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // OP: User 2: Sets up CloudSync
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Cloud: User 1: Creates Site
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

    }

    @Test(groups = { "CloudSync" })
    public void ALF_7024() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String cloudSiteName = getSiteName(testName + "-1");
        String fileName1 = testName + System.currentTimeMillis() + "-1.txt";
        String fileName2 = testName + System.currentTimeMillis() + "-2.txt";
        String fileName3 = testName + System.currentTimeMillis() + "-3.txt";

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setSiteName(cloudSiteName);
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setLockOnPrem(true);

        // OP: User 1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        // OP: User 1: Sets up cloudSync
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // OP: User 1: Invites User 2 to site as Site Manager
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.MANAGER);
        ShareUser.logout(drone);

        // OP: Admin user joins the site.
        ShareUser.login(drone, adminUserPrem, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);
        // OP: Admin user: sets up CloudSync
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // OP: User 1: Creates 3 files
        // And assign permission to user2
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });
        docLibPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName3, DOCLIB });

        docLibPage.getNavigation().selectAll().render();
        // OP: User 1: Syncs 3 files (With Lock On Prem)
        // AbstractCloudSyncTest.syncContentToCloud(drone, fileName1, desAndAssBean);
        // AbstractCloudSyncTest.syncContentToCloud(drone, fileName2, desAndAssBean);
        // docLibPage = AbstractCloudSyncTest.syncContentToCloud(drone, fileName3, desAndAssBean);
        docLibPage = syncAllContentToCloud(drone, desAndAssBean).render();

        docLibPage = docLibPage.renderItem(maxWaitTime, fileName1);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isLocked());
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(fileName1).getContentInfo(), "This document is locked by you.");

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName2).isLocked());
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(fileName2).getContentInfo(), "This document is locked by you.");

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName3).isLocked());
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(fileName3).getContentInfo(), "This document is locked by you.");

        ShareUser.logout(drone);

        // OP: User2 unsync file1.
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docLibPage = docLibPage.renderItem(maxWaitTime, fileName1);
        docLibPage.getFileDirectoryInfo(fileName1).selectUnSyncAndRemoveContentFromCloud(false);
        docLibPage = docLibPage.renderItem(maxWaitTime, fileName1);
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName1).isCloudSynced());
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName1).isViewCloudSyncInfoLinkPresent());

        ShareUser.logout(drone);

        // OP: User 1: Unsync file1.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docLibPage = docLibPage.renderItem(maxWaitTime, fileName2);
        docLibPage = docLibPage.getFileDirectoryInfo(fileName2).selectUnSyncAndRemoveContentFromCloud(false);
        docLibPage = docLibPage.renderItem(maxWaitTime, fileName2);
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName2).isCloudSynced());
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName2).isViewCloudSyncInfoLinkPresent());
        ShareUser.logout(drone);

        // OP: Admin User: unsync file1.
        ShareUser.login(drone, adminUserPrem, DEFAULT_PASSWORD);
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docLibPage = docLibPage.renderItem(maxWaitTime, fileName3);
        docLibPage = docLibPage.getFileDirectoryInfo(fileName3).selectUnSyncAndRemoveContentFromCloud(false);
        docLibPage = docLibPage.renderItem(maxWaitTime, fileName3);
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName3).isCloudSynced());
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName3).isViewCloudSyncInfoLinkPresent());
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7004() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        String siteName = getSiteName(testName + "-1");

        // OP: Create User 1, User 2 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // 2. Cloud: Create User 1 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, adminUserPrem, hybridDomainPremium, "1000");

        // OP: User 1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        // OP: User 2: Sets up CloudSync
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

        // OP: User 2 joins the site created by user1
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        // OP: User 2: Sets up CloudSync
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    @Test(groups = { "CloudSync" })
    public void ALF_7004() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName1 = testName + System.currentTimeMillis() + "-1.txt";
        String fileName2 = testName + System.currentTimeMillis() + "-2.txt";
        String fileName3 = testName + System.currentTimeMillis() + "-3.txt";
        String fileName4 = testName + System.currentTimeMillis() + "-4.txt";

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setLockOnPrem(true);

        // OP: User 1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // OP: User 2 joins the site created by user1
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);
        ShareUser.logout(drone);

        // Cloud: User 1: Creates Site
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        try
        {
            // OP: User 1: Uploads files
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
            ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });
            ShareUser.uploadFileInFolder(drone, new String[] { fileName3, DOCLIB });
            ShareUser.uploadFileInFolder(drone, new String[] { fileName4, DOCLIB });

            // OP: User 1 assigns user 2 COLLABORATOR role on files added
            ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName1, UserRole.COLLABORATOR, false);
            ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName2, UserRole.COLLABORATOR, false);
            ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName3, UserRole.COLLABORATOR, false);
            ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName4, UserRole.COLLABORATOR, false);

            ShareUser.logout(drone);

            // OP: User 2: Syncs content to Cloud
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
            docLibPage.getNavigation().selectAll().render();
            ((DocumentLibraryPage) AbstractCloudSyncTest.syncAllContentToCloud(drone, desAndAssBean)).render();

            Assert.assertTrue(checkIfContentIsSynced(drone, fileName1));
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName2));
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName3));
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName4));

            ShareUser.logout(drone);

            // Cloud: User 1:
            ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);

            docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);

            Assert.assertTrue(docLibPage.isFileVisible(fileName1));
            Assert.assertTrue(docLibPage.isFileVisible(fileName2));
            Assert.assertTrue(docLibPage.isFileVisible(fileName3));
            Assert.assertTrue(docLibPage.isFileVisible(fileName4));

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7005() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        String siteName = getSiteName(testName + "-1");

        // OP: Create User 1, User 2 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: Create User 1 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        CreateUserAPI.upgradeCloudAccount(hybridDrone, adminUserPrem, hybridDomainPremium, "1000");

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    @Test(groups = { "CloudSync" })
    public void ALF_7005() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName1 = testName + System.currentTimeMillis() + "-1.txt";
        String fileName2 = testName + System.currentTimeMillis() + "-2.txt";
        String fileName3 = testName + System.currentTimeMillis() + "-3.txt";
        String fileName4 = testName + System.currentTimeMillis() + "-4.txt";

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setLockOnPrem(true);

        // 3. User1 is logged in Alfresco Share (On-premise)
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        // 4. Create any site (e.g. Test) in Alfresco Share (On-premise) by
        // user1
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // 3. User1 is logged in Alfresco Share (On-premise)
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        // 4. Create any site (e.g. Test) in Alfresco Share (On-premise) by
        // user1
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.COLLABORATOR);
        ShareUser.logout(drone);

        // OP: User 1: Uploads files and makes user 2 Collaborator
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        SiteUtil.openSiteDocumentLibraryURL(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName3, DOCLIB });
        ShareUser.uploadFileInFolder(drone, new String[] { fileName4, DOCLIB });

        ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName1, UserRole.COLLABORATOR, false);
        ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName2, UserRole.COLLABORATOR, false);
        ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName3, UserRole.COLLABORATOR, false);
        ShareUserMembers.managePermissionsOnContent(drone, testUser2, fileName4, UserRole.COLLABORATOR, false);

        ShareUser.logout(drone);

        // OP: User 2: Syncs all content to cloud
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docLibPage.getNavigation().selectAll().render();

        // Open another browser window
        WebDrone anotherDrone = getSecondDrone();
        anotherDrone.navigateTo(getShareUrl());

        // OP: User 1: Another browser window
        ShareUser.login(anotherDrone, testUser1, DEFAULT_PASSWORD);

        SiteUtil.openSiteDocumentLibraryURL(anotherDrone, siteName);

        ShareUserMembers.managePermissionsOnContent(anotherDrone, testUser2, fileName1, UserRole.CONSUMER, false);
        ShareUserMembers.managePermissionsOnContent(anotherDrone, testUser2, fileName3, UserRole.CONSUMER, false);

        ShareUser.logout(anotherDrone);
        anotherDrone.quit();

        docLibPage = AbstractCloudSyncTest.syncAllContentToCloud(drone, desAndAssBean).render();

        docLibPage = docLibPage.renderItem(maxWaitTime, fileName1);
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName1).isCloudSynced());
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName2));
        docLibPage = docLibPage.renderItem(maxWaitTime, fileName3);
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName3).isCloudSynced());
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName4));
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);

        docLibPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, siteName);

        Assert.assertFalse(docLibPage.isFileVisible(fileName1));// false
        Assert.assertTrue(docLibPage.isFileVisible(fileName2));// true
        Assert.assertFalse(docLibPage.isFileVisible(fileName3));// false
        Assert.assertTrue(docLibPage.isFileVisible(fileName4));// true

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7007() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        // OP: Create User 1 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: Create User 1, User 2 (Network 1)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[] { testUser2 });
        CreateUserAPI.upgradeCloudAccount(hybridDrone, adminUserPrem, hybridDomainPremium, "1000");

        // OP: User2 creates a site
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        // OP: User 2: sets up cloud Sync
        signInToAlfrescoInTheCloud(drone, testUser2, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // OP: User 1: Sets up Cloud Sync
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    @Test(groups = { "CloudSync" })
    public void ALF_7007() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = testName + System.currentTimeMillis();
        String fileName1 = testName + System.currentTimeMillis() + "-1.txt";
        String fileName2 = testName + System.currentTimeMillis() + "-2.txt";
        String fileName3 = testName + System.currentTimeMillis() + "-3.txt";

        // Cloud: User 1: Create Site
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, testUser1, testUser2, siteName, UserRole.COLLABORATOR);
        ShareUser.logout(hybridDrone);

        // OP: User2 creates a site
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

        // Cloud: User 1: Creates Folder
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(hybridDrone, siteName);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName, folderName);

        // Cloud: User 1: Assigns Collaborator permissions to user 2 on Folder
        ShareUserMembers.managePermissionsOnContent(hybridDrone, testUser2, folderName, UserRole.COLLABORATOR, false);

        ShareUser.logout(hybridDrone);

        // OP: User2 logs in
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        SiteUtil.openSiteDocumentLibraryURL(drone, siteName);

        ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB }).render();
        ShareUser.uploadFileInFolder(drone, new String[] { fileName2, DOCLIB }).render();
        DocumentLibraryPage docLibPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName3, DOCLIB });

        docLibPage = docLibPage.getNavigation().selectAll().render();

        DestinationAndAssigneePage desAndAssPage = ((DestinationAndAssigneePage) docLibPage.getNavigation().render().selectSyncToCloud()).render();

        desAndAssPage.selectNetwork(hybridDomainPremium);

        desAndAssPage.selectSite(siteName);
        desAndAssPage.selectFolder(folderName);

        // Cloud: User 1 sets user2 as consumer for this folder
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);

        SiteUtil.openSiteDocumentLibraryURL(hybridDrone, siteName);

        ShareUserMembers.managePermissionsOnContent(hybridDrone, testUser2, folderName, UserRole.CONSUMER, false);

        ShareUser.logout(hybridDrone);

        // OP: User 2: Continue from where left

        docLibPage = ((DocumentLibraryPage) desAndAssPage.selectSubmitButtonToSync()).render();

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName1).isSyncFailedIconPresent(maxWaitTime));
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName2).isSyncFailedIconPresent(maxWaitTime));
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName3).isSyncFailedIconPresent(maxWaitTime));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepCloudSync2", "DataPrepCloudSync" })
    public void dataPrep_ALF_7006() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        String siteName = getSiteName(testName);

        // OP: Create User 1, User 2 (Network 1)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { testUser2 });

        // Cloud: Create User 1 (same user above)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, adminUserPrem, hybridDomainPremium, "1000");

        // OP: User1: Creates Site
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // OP: User1 invites User 2 to the site, as Collaborator
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.COLLABORATOR);

        // OP: User 1: sets up cloud Sync
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

        // Cloud: User1: Creates site
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // OP: User 2: sets up cloud Sync
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    @Test(groups = { "CloudSync" })
    public void ALF_7006() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName1 = testName + "_1.txt";
        String user1FileName = "test_file_5MB" + ".txt";
        String user2FileName = "test_file_10MB" + ".html";
        String user3FileName = "test_file_7MB" + ".html";

        // OP: User 1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        docLibPage = ShareUserSitePage.createFolder(drone, folderName, folderName);

        docLibPage.selectFolder(folderName);
        ShareUser.uploadFileInFolder(drone, new String[] { user1FileName, DOCLIB + SLASH + folderName });
        ShareUser.uploadFileInFolder(drone, new String[] { user2FileName, DOCLIB + SLASH + folderName });
        ShareUser.uploadFileInFolder(drone, new String[] { user3FileName, DOCLIB + SLASH + folderName });

        ShareUser.logout(drone);

        // OP: User 2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        docLibPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName1, DOCLIB + SLASH + folderName }).render();

        docLibPage.getFileDirectoryInfo(user1FileName).selectCheckbox();
        docLibPage.getFileDirectoryInfo(user2FileName).selectCheckbox();
        docLibPage.getFileDirectoryInfo(user3FileName).selectCheckbox();
        docLibPage.getFileDirectoryInfo(fileName1).selectCheckbox();

        DocumentLibraryNavigation docLibNav = docLibPage.getNavigation().render();

        DestinationAndAssigneePage desAndAssPage = ((DestinationAndAssigneePage) docLibNav.selectSyncToCloud()).render();
        desAndAssPage.selectNetwork(hybridDomainPremium);
        desAndAssPage.selectSite(siteName);

        WebDrone anotherDrone = getSecondDrone();
        anotherDrone.navigateTo(getShareUrl());

        LoginPage loginPage = (LoginPage) anotherDrone.getCurrentPage();
        loginPage.loginAs(testUser1, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage1 = ShareUser.openSitesDocumentLibrary(anotherDrone, siteName);

        ManagePermissionsPage mangPermPage = docLibPage1.getFileDirectoryInfo(user1FileName).selectManagePermission().render();
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(testUser2);
        mangPermPage = mangPermPage.selectAddUser().searchAndSelectUser(userProfile);
        mangPermPage.setAccessType(UserRole.CONSUMER);
        mangPermPage = mangPermPage.toggleInheritPermission(false, ButtonType.Yes);

        docLibPage = ((DocumentLibraryPage) desAndAssPage.selectSubmitButtonToSync()).render();

        mangPermPage.selectSave();

        drone.refresh();

        docLibPage = docLibPage.renderItem(maxWaitTime, user1FileName);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(user1FileName).isSyncFailedIconPresent(maxWaitTime));
        ShareUser.logout(drone);

        ShareUser.logout(anotherDrone);
        anotherDrone.quit();

    }
}