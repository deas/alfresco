/*
 * C opyright (C) 2005-2013 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.cloudsync;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractCloudSyncTest;
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
 * Class includes: Tests from TestLink in Area: Cloud Sync
 * <ul>
 * <li>The tests that check the access level/authentication for cloud sync.</li>
 * </ul>
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
public class CloudSyncAccessTest extends AbstractCloudSyncTest
{

    private static Log logger = LogFactory.getLog(CloudSyncAccessTest.class);

    private String fileName;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
    }

    /**
     * 1. At least one site is setup in Cloud 2. User have Cloud account but not
     * authorised 3. User is logged in Alfresco Share (On-premise) 4. Any site
     * is created in Alfresco Share (On-premise)
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1828() throws Exception
    {
        String testName = getTestName();
        onPremUser = getUserNameForDomain(testName, hybridDomainPremium);

        siteName = getSiteName(testName)+"1";

        // Create OP user
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[] { onPremUser });

        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);
    }

    /**
     * 1. Any file is created/uploaded into the Document Library 2. Click on
     * created/uploaded file to open its details page 3. Choose 'Sync to Cloud'
     * option from Document Actions list on Document Details page
     * 
     * @throws Exception
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_1828() throws Exception
    {
        try
        {
            String testName = getTestName();

            onPremUser = getUserNameForDomain(testName, hybridDomainPremium);
            siteName = getSiteName(testName)+"1";
            fileName = getFileName(testName) + System.currentTimeMillis() + "." + "txt";

            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);

            SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(siteName));

            DocumentLibraryPage libPage = ShareUser.uploadFileInFolder(drone, new String[]
            { fileName, DOCLIB });

            // Cloud Sync Dialog Checks on Doc lib Page
            libPage.getFileDirectoryInfo(fileName).selectSyncToCloud();
            Assert.assertTrue(libPage.isSignUpDialogVisible(), "Sign up dialog should appear");
            // Back to Doclib
            SiteUtil.openSiteDocumentLibraryURL(drone, getSiteShortname(siteName));

            // Cloud Sync Dialog Checks on Doc Details Page
            DocumentDetailsPage documentDetailsPage = libPage.selectFile(fileName).render();
            documentDetailsPage.selectSyncToCloud();
            Assert.assertTrue(documentDetailsPage.isSignUpDialogVisible(), "Sign up dialog should appear. User might be already connected to cloud account");
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 1. At least two users are created (user1,user2) in Alfresco Share
     * (On-premise) 2. At least one network and one site are setup in Cloud 3.
     * Both users (user1,user2) have access to the Cloud account 4. User1 is
     * logged in Alfresco Share (On-premise) 5. Any site (e.g. Test) is created
     * in Alfresco Share (On-premise) by user1 6. Document Library page of the
     * created site (e.g. Test) is opened 7. Any file is created/uploaded into
     * the Document Library 8. User2 have no any access to content of created
     * file
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1831() throws Exception
    {
        String testName = getTestName();
        onPremUser = getUserNameForDomain(testName, hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "2", hybridDomainPremium);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { onPremUser });
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user2 });
        fileName = getFileName(testName);
        siteName = getSiteName(testName);

        // Sync OP user1
        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Creating the doc for sync to cloud

        String[] fileInfo =
        { fileName, DOCLIB };

        ShareUser.uploadFileInFolder(drone, fileInfo);

        ShareUser.openDocumentDetailPage(drone, fileName);

        // Remove the permission so that only site manager has access for
        // content.

        ShareUser.returnManagePermissionPage(drone, fileName);

        ShareUserMembers.toggleInheritPermission(drone, false);

        ShareUser.logout(drone);

        // Login in as user 2 and request access for site as consumer.

        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        ShareUserMembers.userRequestToJoinSite(drone, siteName);

        ShareUser.logout(drone);
    }

    @Test(groups =
    {"CloudSync"})
    public void ALF_1831() throws Exception
    {

        String testName = getTestName();
        onPremUser = getUserNameForDomain(testName, hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "2", hybridDomainPremium);
        siteName = getSiteName(testName);
        fileName = getFileName(testName);

        try
        {
            // Log in Alfresco Share (On-premise) as user2
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);

            // Go to the site Test->Document Library
            DocumentLibraryPage libPage = SiteUtil.openSiteDocumentLibraryURL(drone, siteName);

            Assert.assertFalse(libPage.isFileVisible(fileName));
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1829() throws Exception
    {
        String testName = getTestName();
        onPremUser = getUserNameForDomain(testName, hybridDomainPremium);
        String ClUser = getUserNameForDomain(testName, hybridDomainPremium);
        siteName = getSiteName(testName);
        fileName = getFileName(testName);
        String[] fileInfo =
        { fileName, DOCLIB };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[]
        { onPremUser });
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, new String[]
        { ClUser });

        // Create OP user
        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        signInToAlfrescoInTheCloud(drone, ClUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    /**
     * - Any file is created/uploaded into the Document Library - Click on
     * created/uploaded file to open its details page - Choose 'Sync to Cloud'
     * option from Document Actions list on Document Details page - Sync file to
     * Cloud page should be displayed.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unused")
    @Test(groups =
    {"CloudSync"})
    public void ALF_1829() throws Exception
    {

        String testName = getTestName();
        siteName = getSiteName(testName);
        fileName = getFileName(testName);
        onPremUser = getUserNameForDomain(testName, hybridDomainPremium);
        try
        {
            // Creating the doc for review
            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailPage = ShareUser.openDocumentDetailPage(drone, fileName);
            Assert.assertTrue(detailPage.isSyncToCloudOptionDisplayed());

            DestinationAndAssigneePage desAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloud(drone);

            Assert.assertEquals(desAndAssigneePage.getSyncToCloudTitle(), "Sync " + fileName + " to The Cloud");

        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Enterprise40x-7008:Sync a file as admin 1. Create an on-premise user 2.
     * Login with above user, create a site and upload a document 3. Login into
     * cloud and create a site
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_7008() throws Exception
    {
        String testName = getTestName();
        String userName = getUserNameForDomain(testName, hybridDomainPremium);
        siteName = getSiteName(testName) + "-OP";
        cloudUserSiteName = getSiteName(testName) + "CL";

        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { userName });
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { userName });

        // Login as cloud user and create a site
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        // Set up Cloud Sync
        signInToAlfrescoInTheCloud(drone, userName, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    /**
     * Enterprise40x-7008:Sync a file as admin 1. Login to Alfresco Share
     * (On-Premise) as admin 2. Set up Cloud Sync 3. Share user's (created in
     * pre-reqs) document into Cloud user's site 4. Login to cloud and verify
     * the synced file appeared 5. UnSync the document from Cloud
     * 
     * @throws Exception
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_7008() throws Exception
    {
        try
        {
            String testName = getTestName();
            String userName = getUserNameForDomain(testName, hybridDomainPremium);
            fileName = getFileName(testName) + System.currentTimeMillis() + "." + "txt";
            String[] fileInfo = { fileName, DOCLIB };
            siteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
            cloudUserSiteName = getSiteName(testName) + System.currentTimeMillis() + "CL";

            DestinationAndAssigneeBean destAndAssBean = new DestinationAndAssigneeBean();
            destAndAssBean.setNetwork(hybridDomainPremium);
            destAndAssBean.setSiteName(cloudUserSiteName);

            // Login to Alfresco Share (On-Premise) as admin
            ShareUser.login(hybridDrone, userName, DEFAULT_PASSWORD);
            // Create On-Premise site
            ShareUser.createSite(hybridDrone, cloudUserSiteName, SITE_VISIBILITY_PUBLIC);

            ShareUser.logout(hybridDrone);

            // Login to Alfresco Share (On-Premise) as admin
            ShareUser.login(drone, userName, DEFAULT_PASSWORD);
            // Create On-Premise site
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

            // Upload a document
            ShareUser.uploadFileInFolder(drone, fileInfo);

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, destAndAssBean);

            Assert.assertTrue(checkIfContentIsSynced(drone, fileName));

            ShareUser.logout(drone);

            // Login to cloud and verify the synced file appeared
            ShareUser.login(hybridDrone, userName, DEFAULT_PASSWORD);

            DocumentLibraryPage documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudUserSiteName);

            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "Verifying the synced file displayed in cloud user's site document library");

            ShareUser.logout(hybridDrone);

            // Login to Alfresco Share (On-Premise) as admin
            ShareUser.login(drone, userName, DEFAULT_PASSWORD);
            SiteUtil.openSiteDocumentLibraryURL(drone, siteName);

            selectUnSyncAndRemoveContentFromCloud(drone, fileName);
            Assert.assertFalse(isCloudSynced(drone, fileName));

        } catch (Throwable e)
        {
            reportError(drone, testName, e);
            reportError(hybridDrone, testName, e);
        }
    }

    /**
     * 6996 - ALF-1839:Sync file(s). User have no write access to folder in
     * Cloud 1. Create User1 and User2 (On-premise) 2. Create User1 and User2
     * (Cloud) 3. Login to Cloud as User1, create a site and a folder within the
     * site 4. Cloud User2, Join the site created by Cloud User1 as "Consumer"
     * 7. Login as User1 (On-Premise), configure Cloud Sync(With Cloud User2),
     * Create a site and upload a document
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1839() throws Exception
    {
        String testName = getTestName();
        // Create User1 and User2 (On-premise)
        String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        fileName = getFileName(testName) + "." + "txt";
        siteName = getSiteName(testName) + "-OP";
        cloudUserSiteName = getSiteName(testName) + "-CS";
        String folderName1 = getFolderName(testName + "-1");
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(user2);

        // Create User: Enterprise
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1 });
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user2 });

        // Create User1 and User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user1 });
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user2 });

        // Login to Cloud as User1, create a site and a folder within the site
        // and Invite User2
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudUserSiteName, SITE_VISIBILITY_PUBLIC);

        // Create two folders
        ShareUserSitePage.createFolder(hybridDrone, folderName1, "Folder-1");

        // Invite User2 to the site as Consumer and log-out the current user.
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, user1, user2, cloudUserSiteName, UserRole.CONSUMER);
        ShareUser.logout(hybridDrone);

        // Login as User1 (On-Premise) and configure Cloud Sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        signInToAlfrescoInTheCloud(drone, user2, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload a file
        String[] fileInfo =
        { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUser.logout(drone);
    }

    /**
     * 6996 - ALF-1839:Sync file(s). User have no write access to folder in
     * Cloud 1. Login as User1 (On-Premise) 2. Search the site (created in
     * pre-reqs), go to Site Document library , select the file and click on
     * "Sync to Cloud" 3. Select the site joined by Cloud User2 4. Verify Cloud
     * User2 doesn't have permissions on Folder1
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_1839()
    {

        try
        {
            String testName = getTestName();

            String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            fileName = getFileName(testName) + "." + "txt";
            siteName = getSiteName(testName) + "-OP";
            cloudUserSiteName = getSiteName(testName) + "-CS";
            String folderName1 = getFolderName(testName + "-1");

            // Login as User1 (On-Premise)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Go to Site Document library
            ShareUser.openSitesDocumentLibrary(drone, siteName);

            // select the file and "Sync to Cloud"
            ShareUser.openDocumentDetailPage(drone, fileName);

            DestinationAndAssigneePage destinationAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloud(drone);

            destinationAndAssigneePage.selectSite(cloudUserSiteName);

            // Verify that the user doesn't have permissions on Folder 1
            Assert.assertFalse(destinationAndAssigneePage.isSyncPermitted(folderName1), "Verifying the user doesn't have permissions on the folder 1");

            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
            reportError(hybridDrone, testName, e);
        }
    }

    /**
     * 6999 - ALF-1852:Sync file(s). Write access removed for user in Cloud 1.
     * Create User1 and User2 (On-premise) 2. Create User1 and User2 (Cloud) 3.
     * Login as User1 (On-Premise), configure Cloud Sync(With Cloud User2)
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1852() throws Exception
    {
        testName = getTestName();
        // Create User1 and User2 (On-premise)
        String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        // Create User: Enterprise
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1 });
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user2 });

        // Create User1 and User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user1 });
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user2 });

        // Login as User1 (On-Premise) and configure Cloud Sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        signInToAlfrescoInTheCloud(drone, user2, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

    }

    /**
     * 1. Login as User1 (Cloud), create a site and cretae a folder 2. Invite
     * Cloud User2 as Collaborator 3. Login as User1, open site document
     * library, select file and select Sync to cloud 4. Verify that the user
     * have Sync permissions on Folder 5. Login to Cloud as User1, open the site
     * document library 6. Change the user2's permission on folder to Consumer
     * 7. Login as User1, open site document library, select file and select
     * Sync to cloud 8. Verify that the user doesn't have Sync permissions on
     * Folder
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_1852()
    {
        try
        {
            testName = getTestName();

            String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String user2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
            fileName = getFileName(testName) + System.currentTimeMillis() + "." + "txt";

            String[] fileInfo =
            { fileName, DOCLIB };
            siteName = getSiteName(testName) + "-OP";
            cloudUserSiteName = getSiteName(testName) + "-CS";
            String folderName1 = getFolderName(testName + System.currentTimeMillis() + "-1");

            // Login to Cloud as User1, create a site and a folder within the
            // site and Invite User2
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudUserSiteName, SITE_VISIBILITY_PUBLIC);

            // Create a folder
            ShareUserSitePage.createFolder(hybridDrone, folderName1, "Folder-1");

            // Invite User2 to the site as Collaborator and log-out the current
            // user.
            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, user1, user2, cloudUserSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(hybridDrone);

            // Login as User1 (On-Premise)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Create Site and upload a file
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, fileInfo);

            ShareUser.openDocumentDetailPage(drone, fileName);
            // select the file and "Sync to Cloud"

            DestinationAndAssigneePage destinationAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloud(drone);

            // Select the site joined by User2 (Cloud)
            destinationAndAssigneePage.selectSite(cloudUserSiteName);

            // Verify that the user have Sync permissions on Folder
            Assert.assertTrue(destinationAndAssigneePage.isSyncPermitted(folderName1), "Verifying the user have permissions on the folder 1");

            ShareUser.logout(drone);

            // Login to Cloud as User1, open the site document library
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.openSitesDocumentLibrary(hybridDrone, cloudUserSiteName);

            // Change the folder permissions to Consumer
            ShareUserMembers.managePermissionsOnContent(hybridDrone, user2, folderName1, UserRole.CONSUMER, false);

            ShareUser.logout(hybridDrone);

            // Login as User1 (On-Premise)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            // select the file and "Sync to Cloud"
            ShareUser.openDocumentDetailPage(drone, fileName);

            destinationAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloud(drone);

            // Select the site joined by User2 (Cloud)
            destinationAndAssigneePage.selectSite(cloudUserSiteName);

            // Verify that the user doesn't have sync permissions on Folder
            Assert.assertFalse(destinationAndAssigneePage.isSyncPermitted(folderName1), "Verifying the user Doesn't have permissions on the folder 1");

            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
            reportError(hybridDrone, testName, e);
        }
    }

    /**
     * 1. Create User1 (On-premise) 2. Create User1 and User2 (Cloud) 3. Login
     * to Cloud as User1, create a site 4. Login as User1 (On-Premise),
     * configure Cloud Sync (with Cloud User2), Create a site and upload a
     * document
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1841() throws Exception
    {
        testName = getTestName();
        String user1OP = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user1CL = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user2CL = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        cloudUserSiteName = getSiteName(testName) + "CS";
        siteName = getSiteName(testName) + "OP";
        fileName = getFileName(testName) + ".txt";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1OP });

        // Create User1 and User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user1CL });
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user2CL });

        // Login to Cloud as User1, create a site
        ShareUser.login(hybridDrone, user1CL, DEFAULT_PASSWORD);

        ShareUser.createSite(hybridDrone, cloudUserSiteName, SITE_VISIBILITY_PUBLIC).render();

        // Add a step to Logout from Cloud
        ShareUser.logout(hybridDrone);

        // Login as User1 (On-Premise) and configure Cloud Sync
        ShareUser.login(drone, user1OP, DEFAULT_PASSWORD);

        signInToAlfrescoInTheCloud(drone, user2CL, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload a file
        String[] fileInfo =
        { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        ShareUser.logout(drone);
    }

    /**
     * 1. Login as User1 (On-Premise) 2. Search the site (created in pre-reqs),
     * go to Site Document library , select the file and click on
     * "Sync to Cloud" 3. Verify Cloud User2 shouldn't see the site created by
     * Cloud User1
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_1841()
    {
        try
        {
            testName = getTestName();
            String user1OP = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            cloudUserSiteName = getSiteName(testName) + "CS";
            siteName = getSiteName(testName) + "OP";
            fileName = getFileName(testName) + ".txt";

            // Login as User1 (On-Premise)
            ShareUser.login(drone, user1OP, DEFAULT_PASSWORD);

            // Go to Site Document library, select the file and click on
            // "Sync to Cloud"
            ShareUser.openSitesDocumentLibrary(drone, siteName);

            ShareUser.openDocumentDetailPage(drone, fileName);

            DestinationAndAssigneePage destinationAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloud(drone);

            // Verify that the Sync to Cloud dialog shouldn't display the site
            // created by User1
            Assert.assertFalse(destinationAndAssigneePage.isSiteDisplayed(cloudUserSiteName), "Verifying the user won't see Cloud User1's Site");

            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 1. Create User1 (On-premise) 2. Create Cloud User1 with PremierNet domain
     * and Cloud User2 wih FreeNet domain 3. Login to Cloud as User1, create a
     * site 4. Login as User1 (On-Premise), configure Cloud Sync (with Cloud
     * User2), Create a site and upload a document
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1846() throws Exception
    {
        testName = getTestName();
        String user1OP = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user1CL = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user2CL = getUserNameForDomain(testName + "-2", hybridDomainFree);
        cloudUserSiteName = getSiteName(testName) + "CS";
        siteName = getSiteName(testName) + "OP-1";
        fileName = getFileName(testName) + ".txt";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1OP });

        // Create User1 and User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user1CL });
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserFree, new String[]
        { user2CL });

//        // Login to Cloud as User1, create a site
        ShareUser.login(hybridDrone, user1CL, DEFAULT_PASSWORD);

        ShareUser.createSite(hybridDrone, cloudUserSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as User1 (On-Premise) and configure Cloud Sync
        ShareUser.login(drone, user1OP, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload a file
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);
        signInToAlfrescoInTheCloud(drone, user2CL, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * 1. Login as User1 (On-Premise) 2. Search the site (created in pre-reqs),
     * go to Site Document library , select the file and click on
     * "Sync to Cloud" 3. Verify that Sync to cloud dialog shouldn't display
     * User1's network 4. Verify that Sync to cloud dialog should display
     * User2's network 5. Verify Cloud User2 shouldn't see the site created by
     * Cloud User1
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_1846()
    {
        try
        {
            testName = getTestName();
            String user1OP = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String user1CL = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String user2CL = getUserNameForDomain(testName + "-2", hybridDomainFree);
            cloudUserSiteName = getSiteName(testName) + "CS";
            siteName = getSiteName(testName) + "OP-1";
            fileName = getFileName(testName) + ".txt";

            logger.info("siteName: " + siteName);

            // Login as User1 (On-Premise)
            ShareUser.login(drone, user1OP, DEFAULT_PASSWORD);

            // Go to Site Document library, select the file and click on
            // "Sync to Cloud"
            ShareUser.openSitesDocumentLibrary(drone, siteName);

            ShareUser.openDocumentDetailPage(drone, fileName);

            DestinationAndAssigneePage destinationAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloud(drone);

            // Verify that the Sync to Cloud dialog shouldn't display User1's
            // network
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(user2CL.split("@")[1]), "Verifying the user can see Cloud User2's Network");

            Assert.assertFalse(destinationAndAssigneePage.isNetworkDisplayed(getUserDomain(user1CL)), "Verifying the user won't see Cloud User1's Network");

            Assert.assertFalse(destinationAndAssigneePage.isSiteDisplayed(cloudUserSiteName), "Verifying the user won't see Cloud User1's Site");

            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 1. Create User1 (On-premise) 2. Create Cloud User1 with PremierNet domain
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1832() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1 });
        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user1 });
    }

    /**
     * 1. Login to cloud as User1 and create a site 2. Login as User1
     * (On-Premise), Create a site (UserSite) and upload a document
     * (UserFile.txt) 3. Login as Admin (On-Premise), Create a site (AdminSite)
     * and upload a document (AdminFile.txt) 4. Setup the cloud sync with cloud
     * user1 and sync AdminFile.txt to cloud 5. Search for UserSite, open
     * DocumentLibrary and sync UserFile.txt to cloud 6. Login to Cloud as
     * User1, open Site Document Library and verify both files are displayed
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_1832()
    {
        try
        {
            String testName = getTestName();
            String user1OP = getUserNameForDomain(testName, hybridDomainPremium);
            String user1CL = getUserNameForDomain(testName, hybridDomainPremium);
            cloudUserSiteName = testName + System.currentTimeMillis() + "-CS";
            String adminSiteName = testName + System.currentTimeMillis() + "-AS";
            String userSiteName = testName + System.currentTimeMillis() + "-US";
            String adminFileName = getFileName(testName) + "-AF.txt";
            String userFileName = getFileName(testName) + "-UF.txt";
            String[] adminFileInfo =
            { adminFileName, DOCLIB };
            String[] userFileInfo =
            { userFileName, DOCLIB };
            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(cloudUserSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            // Login to Cloud as User1, create a site
            ShareUser.login(hybridDrone, user1CL, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudUserSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (On-Premise)
            ShareUser.login(drone, user1OP, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, userSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library and Upload a file
            ShareUser.uploadFileInFolder(drone, userFileInfo);
            ShareUser.logout(drone);

            // Login as Admin (On-Premise)
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            // Sign into Alfresco In the cloud
            signInToAlfrescoInTheCloud(drone, user1CL, DEFAULT_PASSWORD);
            // Create site, open document library and upload a file
            ShareUser.createSite(drone, adminSiteName, SITE_VISIBILITY_PUBLIC);
            // siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
            ShareUser.uploadFileInFolder(drone, adminFileInfo);

            // Go to Site Document library, select the adminFileName file and
            // click on "Sync to Cloud"
            AbstractCloudSyncTest.syncContentToCloud(drone, adminFileName, desAndAssBean);
            Assert.assertTrue(checkIfContentIsSynced(drone, adminFileName));

            SiteUtil.openSiteDocumentLibraryURL(drone, userSiteName);

            AbstractCloudSyncTest.syncContentToCloud(drone, userFileName, desAndAssBean);
            Assert.assertTrue(checkIfContentIsSynced(drone, userFileName));

            ShareUser.logout(drone);

            // Login as Cloud user
            ShareUser.login(hybridDrone, user1CL, DEFAULT_PASSWORD);

            // Verify both documents displayed in Cloud Destination site
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudUserSiteName);
            Assert.assertTrue(documentLibraryPage.isFileVisible(adminFileName), "Verifying the file uploaded by Admin is synced");
            Assert.assertTrue(documentLibraryPage.isFileVisible(userFileName), "Verifying the file uploaded by User is synced");
            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
            reportError(hybridDrone, testName, e);

        }
    }

    /**
     * 1. Create User1 and User2 (On-premise) 2. Create Cloud User1 with
     * PremierNet domain 3. Login as User1 (On-Premise), create a site and
     * upload a document 4. Login as User2, join the site created by User1 and
     * configure Cloud Sync using User1 (Cloud)
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1835() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String cloudUser = getUserNameForDomain(testName, hybridDomainPremium);
        String user1SiteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1 });
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user2 });

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { cloudUser });
        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, user1SiteName, SITE_VISIBILITY_PUBLIC);
        // Open Document library and Upload a file
        ShareUser.uploadFileInFolder(drone, fileInfo).render();
        ShareUser.logout(drone);

        // Login as User2 and setup cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        // Join the site created by User1 as consumer
        ShareUserMembers.userRequestToJoinSite(drone, user1SiteName);
        ShareUser.logout(drone);
    }

    /**
     * Login as User2 (On-Premise), search for the site created by User1 Open
     * Site Document Library, click on document and verify "Sync to Cloud"
     * option is not displayed
     * 
     * @throws Exception
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_1835() throws Exception
    {
        try
        {
            testName = getTestName();
            String user2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
            String user1SiteName = getSiteName(testName);
            String fileName = getFileName(testName) + ".txt";

            // Login as User2
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);
            // Search for the site (Created by user1), open Site Document
            // Library and click on file
            ShareUser.openSitesDocumentLibrary(drone, user1SiteName);

            DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
            // Verify "Sync to Cloud" option is not displayed as user doesn't
            // have rights
            Assert.assertFalse(documentDetailsPage.isSyncToCloudOptionDisplayed(), "Verifying \"Sync to Cloud\" option is not displayed");
            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 1. Create user1 & user2 (On-premise). 2. Create site from user1. 3.
     * Upload a file on site. 4. Join user2 with site.
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1837() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1 });
        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user2 });

    }

    /**
     * 1. Login from user2 and check uploaded file through user1 is present and
     * has permission. 2. Login from user1 and revoke all permission on the
     * file. 3. Login from user2 and check file(from user1) is present and has
     * permission.(Should not be). 4. Login from user1 and invoke permissions on
     * file. (For recursive using of the test case.)
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_1837()
    {
        try
        {
            testName = getTestName();
            String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String user2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
            String user1FileName = getFileName(testName) + "-UF.txt";

            String userSiteName = getSiteName(testName) + System.currentTimeMillis();

            String[] userFileInfo = { user1FileName, DOCLIB };

            // Login as User1 (On-Premise)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, userSiteName, ShareUser.SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, userFileInfo);
            ShareUser.logout(drone);
            // Login as user 2 and join the site.
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);
            ShareUserMembers.userRequestToJoinSite(drone, userSiteName);
            ShareUser.logout(drone);

            // login to the site and set the user role for user2.

            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            ShareUser.openSiteDashboard(drone, userSiteName);

            ShareUserMembers.setUserRoleWithSite(drone, user2, UserRole.COLLABORATOR, userSiteName);

            ShareUser.logout(drone);

            // Login as user2, check file is present or not, and permission to
            // change is there or not.
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(drone, userSiteName);

            DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, user1FileName);

            Assert.assertTrue(documentDetailsPage.isSyncToCloudOptionDisplayed());

            ShareUser.logout(drone);

            // Login as User1 (On-Premise), and revoke the permission to write.
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            ShareUser.openSiteDashboard(drone, userSiteName);

            ShareUserMembers.setUserRoleWithSite(drone, user2, UserRole.CONSUMER, userSiteName);

            ShareUser.logout(drone);

            // Login as user2 check the permission is there or not, it shoudn't
            // be ?
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(drone, userSiteName);

            documentDetailsPage = ShareUser.openDocumentDetailPage(drone, user1FileName);

            Assert.assertFalse(documentDetailsPage.isSyncToCloudOptionDisplayed());
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 1. Create User1 (On-premise) 2. Create two Cloud Users (User1 - Free
     * Domain, User2 - Premier Domain) 3. Login as User1 (Cloud), create a site
     * 4. Login as User2 (Cloud), create a site 5. Login as User1 (OP),
     * configure Cloud Sync with cloud User2 6. Create a site, upload a document
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1862() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "-2", hybridDomainFree);
        String opSiteName = getSiteName(testName) + "-OPSite";
        String user1SiteName = getSiteName(testName) + "-U1";
        String user2SiteName = getSiteName(testName) + "-U2";

        String fileName = getFileName(testName) + ".txt";
        String[] fileInfoOP = { fileName, DOCLIB };

        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1 });
        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user1 });
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserFree, new String[]
        { user2 });

        // Login as User1 (Cloud) and create a site
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, user1SiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User2 (Cloud) and create a site
        ShareUser.login(hybridDrone, user2, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, user2SiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1, set up cloud sync with Cloud user "User2"
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user2, DEFAULT_PASSWORD);
        // Create a site and upload a file
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfoOP);
        ShareUser.logout(drone);
    }

    /*
     * 1) Using CreateUser API, User2 joins User1's site (Cloud) 2) Login as
     * User1 (OP), open the site document library from search 3) Select
     * "Sync to Cloud" option from Document Library (more options) 4) Verify
     * both networks are displayed in Destination And Assignee page 5) Login
     * into Cloud as User1, remove User2 from site's Site Members page 6) Login
     * as User1 (OP), open the site document library, Select "Sync to Cloud"
     * option from Document Library (more options) 7) Verify only one network
     * (Premiernet.test) is displayed in Destination And Assignee page
     * 
     * @throws Exception
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_1862() throws Exception
    {
        try
        {

            String testName = getTestName();
            String user1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
            String user2 = getUserNameForDomain(testName + "-2", hybridDomainFree);
            String opSiteName = getSiteName(testName) + "-OPSite";
            String user1SiteName = getSiteName(testName) + "-U1";
            String fileName = getFileName(testName) + ".txt";

            CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, user1, user2, getSiteShortname(user1SiteName), "SiteCollaborator", "");

            // Login into OP as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from Search
            ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Select "Sync to Cloud" option from Document Library (more
            // options)
            DestinationAndAssigneePage destinationAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloudDocLib(drone, fileName);

            // Verify both networks are displayed in Destination And Assignee
            // page
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(getUserDomain(user1)));
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(getUserDomain(user2)));
            DocumentLibraryPage documentLibraryPage = destinationAndAssigneePage.selectCancelButton().render();
            documentLibraryPage.render();

            ShareUser.logout(drone);

            // Login into Cloud as User1
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from Search
            SiteDashboardPage siteDashboardPage = SiteUtil.openSiteFromSearch(hybridDrone, user1SiteName).render();
            SiteMembersPage siteMembersPage = siteDashboardPage.getSiteNav().selectMembers().render();
            // Remove User2 from site members page
            siteMembersPage.removeUser(user2);
            ShareUser.logout(hybridDrone);

            // Login into OP as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from Search
            ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Select "Sync to Cloud" option from Document Library (more
            // options)
            destinationAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloudDocLib(drone, fileName);
            // Verify user1 network (network 1) is not displayed as user2 had
            // been removed from the site.
            Assert.assertFalse(destinationAndAssigneePage.isNetworkDisplayed(getUserDomain(user1)));
            // Verify User2 network is displayed
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(getUserDomain(user2)));
            documentLibraryPage = destinationAndAssigneePage.selectCancelButton().render();
            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
            reportError(hybridDrone, testName, e);
        }
    }

    /**
     * 1. Create User1 & User2 (On-premise) 2. Create two Cloud Users (User1 -
     * Premier Domain, User2 - free Domain) 3. Upgrade Free domain user 4. Login
     * as User1 (OP), configure Cloud Sync with cloud User1
     * 
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_1867() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName + "1", hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "2", hybridDomainPremium);

        String cloudUser1 = getUserNameForDomain(testName + "1", hybridDomainPremium);
        String cloudUser2 = getUserNameForDomain(testName + "2", hybridDomainFree);

        // Create User1 and User2 (OP)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1 });
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user2 });

        // Create User1 and User2 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { cloudUser1 });
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserFree, new String[]
        { cloudUser2 });

        // Login as User1, set up cloud sync with Cloud user "User2"
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /*
     * 1) Login as User1 in Cloud, create a site 2) Login as User2 in cloud,
     * create a site 3) Using CreateUser API, User1 joins User2's site (Cloud)
     * 4) Login as User1 (OP), create a site, upload a document 5) Select
     * "Sync to Cloud" option from Document Library (more options) 6) Verify
     * both networks are displayed in Destination And Assignee page 7) Select
     * User2's network, select the site and select Sync button 8) Verify Sync
     * icon is displayed in Document Library for the synced document 5) Login
     * into Cloud as User2, Verify the synced document is present in site
     * document library 10) Remove User1 from site's Site Members page 11) Login
     * as User1 (OP), open the site document library, select the file 12) From
     * Document Details page, select Inline Edit, modify details and save 13)
     * Goto Document Library page and verify Sync failed icon is displayed 14)
     * Veriy "Last Sync failed" message displayed
     * 
     * @throws Exception
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_1867() throws Exception
    {
        try
        {
            testName = getTestName();
            String user1 = getUserNameForDomain(testName + "1", hybridDomainPremium);
            String user2 = getUserNameForDomain(testName + "2", hybridDomainPremium);

            String cloudUser1 = getUserNameForDomain(testName + "1", hybridDomainPremium);
            String cloudUser2 = getUserNameForDomain(testName + "2", hybridDomainFree);

            String opSiteName = getSiteName(testName);
            String user2SiteName = getSiteName(testName) + "-U2";
            String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
            String[] fileInfoOP =
            { fileName, DOCLIB };

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainFree);
            desAndAssBean.setSiteName(user2SiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            // Login as User2 (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, user2SiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Invite User1 to join User2's site
            CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser2, cloudUser1, getSiteShortname(user2SiteName), "SiteCollaborator", "");
            // Login into OP as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Create a site and upload a file
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC).render();
            ShareUser.uploadFileInFolder(drone, fileInfoOP);

            DocumentLibraryPage documentLibraryPage = AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

            Assert.assertTrue(checkIfContentIsSynced(drone, fileName));
            ShareUser.logout(drone);

            // Login into Cloud as User2
            ShareUser.login(hybridDrone, cloudUser2, DEFAULT_PASSWORD);
            // Open Site Document Library from Search
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, user2SiteName);
            // Verify Synced file displayed in the document library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName);
            // Select Members
            SiteMembersPage siteMembersPage = documentLibraryPage.getSiteNav().selectMembers().render();
            // Remove User2 from site members page
            siteMembersPage.removeUser(user1);
            ShareUser.logout(hybridDrone);

            // Login into OP as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from Search
            SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);
            // Select File to open Document Library page
            DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
            // DocumentDetailsPage documentDetailsPage =
            // documentLibraryPage.selectFile(fileName).render();
            // Select "Inline Edit", modify details
            EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName);
            contentDetails.setDescription("InlineEdit Description");
            contentDetails.setContent(testName + " InlineEdit Content");
            documentDetailsPage = inlineEditPage.save(contentDetails).render();
            documentDetailsPage.render();

            // Click on Document Library page and verify Sync failed icon is
            // present and Last Sync failed message on Info banner.
            documentLibraryPage = documentDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();
            // May fail if there is a delay in sync (need the maximum wait time)
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncFailedIconPresent(maxWaitTimeCloudSync),
                    "Verifying Sync failed icon is displayed");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).getContentInfo().contains(LAST_SYNC_FAILED_MESSAGE),
                    "Verifying \"Last sync failed.\" message is displayed");
            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 7120 - Sync to cloud option. More+ menu 7121 - Sync files to cloud.
     * Single network and single site 7122 - Sync files to cloud. Single network
     * and single site 1) Create OP User (User1) 2) Create Cloud User (User1) 3)
     * Login to OP, Create 3 sites and upload document in site1's Document
     * Library 4) SetUp Cloud Sync with the cloud user 5) Login to Cloud and
     * create a site.
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_2046() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain("1-" + testName, hybridDomainPremium);
        String opSiteName1 = getSiteName(testName + "-1") + "-OP";
        String opSiteName2 = getSiteName(testName + "-2") + "-OP";
        String opSiteName3 = getSiteName(testName + "-3") + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1 });
        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user1 });

        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create 3 Sites
        ShareUser.createSite(drone, opSiteName3, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, opSiteName2, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, opSiteName1, SITE_VISIBILITY_PUBLIC);
        // Open Document library and Upload a file
        ShareUser.uploadFileInFolder(drone, fileInfo);
        // SetUp CloudSync
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        // User1 logs out from OP
        ShareUser.logout(drone);

        // Login into cloud as User1
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        // Create a site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    /**
     * 7120 - Sync to cloud option. More+ menu 7121 - Sync files to cloud.
     * Single network and single site 7122 - Sync files to cloud. Single network
     * and single site 1) Login into OP as User1 2) Open Site Document Library
     * from Search 3) Select "Sync to Cloud" option from Document Library (more
     * options) 4) In Destination and assignee page, verify Network is
     * displayed, Site is displayed and Folder is displayed
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_2046() throws Exception
    {
        try
        {
            testName = getTestName();
            String user1 = getUserNameForDomain("1-" + testName, hybridDomainPremium);
            String opSiteName1 = getSiteName(testName + "-1") + "-OP";
            String cloudSiteName = getSiteName(testName) + "-CL";
            String fileName = getFileName(testName) + ".txt";

            // Login into OP as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from Search
            DocumentLibraryPage documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName1);
            // Select "Sync to Cloud" option from Document Library (more
            // options)
            FileDirectoryInfo contentRow = documentLibraryPage.getFileDirectoryInfo(fileName);
            DestinationAndAssigneePage destinationAndAssigneePage = (DestinationAndAssigneePage) contentRow.selectSyncToCloud();
            // Verify Network is displayed
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(hybridDomainPremium));
            // Verify Site is displayed
            Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName));
            // Select the site
            destinationAndAssigneePage.selectSite(cloudSiteName);
            // Verify Folder is displayed
            Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(DEFAULT_FOLDER_NAME));
            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 7124 - Sync to cloud option. Details page 7125 - Sync files to cloud from
     * Details page 1) Create OP User (User1) 2) Create Cloud User (User1) 3)
     * Login to OP, Create a site and upload document in Document Library 4)
     * SetUp Cloud Sync with the cloud user 5) Login to Cloud and create a site.
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_7125() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain("1-" + testName, hybridDomainPremium);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1 });
        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user1 });

        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create 3 Sites
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        // Open Document library and Upload a file
        ShareUser.uploadFileInFolder(drone, fileInfo);
        // SetUp CloudSync
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        // User1 logs out from OP
        ShareUser.logout(drone);

        // Login into cloud as User1
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        // Create a site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    /**
     * 7124 - Sync to cloud option. Details page 7125 - Sync files to cloud from
     * Details page 1) Login into OP as User1 2) Open Site Document Library from
     * Search 3) Select "Sync to Cloud" option from Document Library (more
     * options) 4) In Destination and assignee page, verify Network is
     * displayed, Site is displayed and Folder is displayed
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_7125() throws Exception
    {
        try
        {

            String testName = getTestName();
            String user1 = getUserNameForDomain("1-" + testName, hybridDomainPremium);
            String opSiteName = getSiteName(testName) + "-OP";
            String cloudSiteName = getSiteName(testName) + "-CL";
            String fileName = getFileName(testName) + ".txt";

            // Login into OP as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from Search
            ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Select file to open Document Details page
            ShareUser.openDocumentDetailPage(drone, fileName);
            // Verify "Sync to Cloud" option is displayed in Document Details
            // page
            DestinationAndAssigneePage destinationAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloud(drone);
            // Verify Network is displayed
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(hybridDomainPremium), "Verifying Network is displayed");
            // Verify Site is displayed
            Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName), "Verifying Site Created in Cloud is displayed");
            // Select the site
            destinationAndAssigneePage.selectSite(cloudSiteName);
            // Verify Folder is displayed
            Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(DEFAULT_FOLDER_NAME),
                    "Verifying directory is displayed (Default directory: Documents)");
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 7127 - Sync multiple files to accessible cloud account 7129 - Sync
     * File(s) after Cloud account authorised 7134 - Cloud icon for a synced
     * file 1) Create On-Prem user 2) Create a Cloud User 3) Login to On-Premise
     * and set up Cloud Sync
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_7129() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain("1-" + testName, hybridDomainPremium);
        String[] userInfo1 = new String[]
        { user1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // SetUp CloudSync
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        // User1 logs out from OP
        ShareUser.logout(drone);
    }

    /**
     * 7127 - Sync multiple files to accessible cloud account 7129 - Sync
     * File(s) after Cloud account authorised 7134 - Cloud icon for a synced
     * file 1) Login to Cloud, create a site 2) Login to OP, create a site,
     * upload 2 documents 3) Select "All" from "Select" drop down 4) Select
     * "Sync to Cloud" from "Selected Items" dropdown 5) Verify Network is
     * displayed, Site is displayed, Folder is displayed in
     * DestinationAndAssigneePage (Implicitly checking it's not the Login page)
     * 6) Select the Site and click Sync 7) Verify CloudSync Info Link is
     * displayed on each document. 8) Login to cloud, open the site document
     * library from search results and verify the synced documents are
     * displayed.
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_7129() throws Exception
    {
        try
        {

            testName = getTestName();
            String user1 = getUserNameForDomain("1-" + testName, hybridDomainPremium);

            String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
            String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
            String fileName1 = getFileName(testName) + "-1.txt";
            String fileName2 = getFileName(testName) + "-2.txt";
            String[] fileInfo1 = { fileName1, DOCLIB };
            String[] fileInfo2 = { fileName2, DOCLIB };

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(cloudSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            // Login into cloud as User1
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Create a site
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login into OP as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create a Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library and Upload a file
            ShareUser.uploadFileInFolder(drone, fileInfo1);
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2);
            // Select all documents from select drop down
            documentLibraryPage = documentLibraryPage.getNavigation().selectAll().render();
            // Select Sync to cloud from Selected Items drop down
            documentLibraryPage = (DocumentLibraryPage) AbstractCloudSyncTest.syncAllContentToCloud(drone, desAndAssBean);

            // Verify CloudSync Info Link is displayed on each document.
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isViewCloudSyncInfoLinkPresent(), "Verify File1 is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isViewCloudSyncInfoLinkPresent(), "Verify File2 is synced");
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName1), "Verify File1 is synced");
            drone.refresh();
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName2), "Verify File2 is synced");

            ShareUser.logout(drone);

            // Login to cloud and verify the synced file appeared
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying the synced file displayed in cloud user's site document library");
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName2), "Verifying the synced file displayed in cloud user's site document library");
            ShareUser.logout(hybridDrone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 7128:Sync File(s) to Cloud without cloud account authorised 1) Create OP
     * User (User1) 2) Create Cloud User (User1) 3) Login to OP, Create a site
     * and upload document in Document Library 5) Login to Cloud and create a
     * site.
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_7128() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain("1-" + testName, hybridDomainPremium);
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName1 = getFileName(testName) + ".txt";
        String fileName2 = getFileName(testName) + ".txt";
        String[] fileInfo1 =
        { fileName1, DOCLIB };
        String[] fileInfo2 =
        { fileName2, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
        { user1 });
        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, new String[]
        { user1 });

        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create a Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        // Open Document library and Upload a file
        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);
        // User1 logs out from OP
        ShareUser.logout(drone);

        // Login into cloud as User1
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        // Create a site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    /**
     * 7128:Sync File(s) to Cloud without cloud account authorised 1) Login into
     * OP as User1 2) Open Site Document Library from Search 3) Select
     * "Sync to Cloud" option from Document Library (more options) 4) Verify
     * Cloud Sign dialog page opens
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_7128() throws Exception
    {
        try
        {
            testName = getTestName();
            String user1 = getUserNameForDomain("1-" + testName, hybridDomainPremium);
            String opSiteName = getSiteName(testName) + "-OP";

            // Login into OP as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from Search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Select All files, SyncToCloud from DocumentLibrary Page (Selected
            // Items Menu)
            documentLibraryPage = documentLibraryPage.getNavigation().selectAll().render();
            CloudSignInPage cloudSignInPage = documentLibraryPage.getNavigation().selectSyncToCloud().render();
            // Verify CloudSignIn dialog header
            Assert.assertEquals(cloudSignInPage.getPageTitle(), "Sign in to Alfresco in the cloud",
                    "Verifying if the CloudSync is not configured, SignUp dialog will display by asserting the header");
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_7136() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, hybridDomainPremium);
        String siteName = getSiteName(testName);

        // Create User (On Premise)
        String[] userInfo =
        { testUser };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Create User (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // Login as User (Cloud)
        ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User (OP User)
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    /**
     * **Sync icon for Files and Folders, and cloud location for files and
     * folders (7136, 7140, 7141, 7142, 7148)** 1) Upload Folders and Files. 2)
     * Sync all the artifacts with cloud. 3) Test sync icon is present for all
     * synced artifiacts. 4) Test Sync cloud location is present.
     * 
     * @throws Exception
     */

    @Test(groups =
    {"CloudSync"})
    public void ALF_7136() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser = getUserNameForDomain(testName, hybridDomainPremium);
            String opSiteName = getSiteName(testName) + System.currentTimeMillis();
            String siteName = getSiteName(testName);
            String fileName = getFileName(testName + System.currentTimeMillis());
            String folderName = getFolderName(testName + System.currentTimeMillis());

            DocumentLibraryPage documentLibraryPage;

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(siteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            // Login to user(on-prem) and upload file\s and folder\s.
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            FileDirectoryInfo fileDirInfo;
            // Sync every uploaded file\s and folder\s in this site.
            for (int i = 0; i < 2; i++)
            {
                ShareUserSitePage.createFolder(drone, folderName + i, getFolderName(testName + i)).render();
                AbstractCloudSyncTest.syncContentToCloud(drone, folderName + i, desAndAssBean);
                Assert.assertTrue(checkIfContentIsSynced(drone, folderName + i));
                drone.refresh();
                documentLibraryPage = getSharePage(drone).render();
                fileDirInfo = documentLibraryPage.getFileDirectoryInfo(folderName + i);
                SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncLocation().contains(hybridDomainPremium + ">" + siteName + ">Documents"));
                syncInfoPage.clickOnCloseButton();
                documentLibraryPage = ShareUser.uploadFileInFolder(drone, new String[]
                { fileName + i, DOCLIB });
                AbstractCloudSyncTest.syncContentToCloud(drone, fileName + i, desAndAssBean);
                Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName + i));
                drone.refresh();
                documentLibraryPage = getSharePage(drone).render();
                fileDirInfo = documentLibraryPage.getFileDirectoryInfo(fileName + i);
                syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo().render();
                Assert.assertTrue(syncInfoPage.getCloudSyncLocation().contains(hybridDomainPremium + ">" + siteName + ">Documents"));
                syncInfoPage.clickOnCloseButton();
                drone.refresh();
                getSharePage(drone).render();
            }
            ShareUser.logout(drone);
            // Login as User (Cloud)
            ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, siteName).render();

            fileDirInfo = documentLibraryPage.getFileDirectoryInfo(folderName + 0);

            // check for 7148 (non empty folder)
            Assert.assertTrue(fileDirInfo.isViewCloudSyncInfoLinkPresent());

            SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo();

            Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));

            ShareUser.logout(hybridDrone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_7137() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, hybridDomainPremium);
        String siteName = getSiteName(testName);

        // Create User (On Premise)
        String[] userInfo =
        { testUser };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Create User (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // Login as User (Cloud)
        ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User (OP User)
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    /**
     * ****************Sync Status Pending (7137, 7179)*************************
     * 1) Upload file on -prem site. 2) sync with cloud user and site. 3) check
     * sync status by clicking on syncToCloud icon immidiately. 4) sync should
     * be pending. 1) Upload file on -prem site. 2) sync with cloud user and
     * site. 3) check sync status by clicking on syncToCloud icon immidiately.
     * 4) sync should be pending.
     * 
     * @throws Exception
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_7137() throws Exception
    {
        try
        {

            String testName = getTestName();
            String testUser = getUserNameForDomain(testName, hybridDomainPremium);
            String siteName = getSiteName(testName);
            String fileName = getFileName(testName + System.currentTimeMillis());
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(siteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            ShareUser.openSitesDocumentLibrary(drone, siteName);
            // Uploading multiple files and folders.
            ShareUser.uploadFileInFolder(drone, new String[]
            { fileName, DOCLIB });

            // Click on Sync of every file in the library.
            DocumentLibraryPage documentLibraryPage = AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

            // Iterate every content and which are synced should have the Cloud
            // Sync icon.
            FileDirectoryInfo fileDirInfo = documentLibraryPage.getFileDirectoryInfo(fileName);
            SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo().render();

            Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Sync Pending") ? true : false);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_7138() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, hybridDomainPremium);
        String siteName = getSiteName(testName);

        // Create User (On Premise)
        String[] userInfo =
        { testUser };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Create User (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // Login as User (Cloud)
        ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User (OP User)
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    /**
     ***************** Sync Status 'Synced' (7138, 7180, 7181)************************* 1)
     * Upload file on -prem site. 2) sync with cloud user and site. 3) check
     * sync status by clicking on syncToCloud icon after a wait. 4) sync should
     * be completed.
     * 
     * @throws Exception
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_7138() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser = getUserNameForDomain(testName, hybridDomainPremium);
            String siteName = getSiteName(testName);
            String fileName = getFileName(testName + System.currentTimeMillis());

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(siteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            SiteUtil.openSiteDocumentLibraryURL(drone, siteName);
            // Uploading multiple files and folders in Document Library
            ShareUser.uploadFileInFolder(drone, new String[]
            { fileName, DOCLIB });

            // Click on Sync of every file in the library.
            DocumentLibraryPage docLibPage = AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

            // Iterate every content and which are synced should have the Cloud
            // Sync icon.

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName));

            drone.refresh();
            docLibPage = drone.getCurrentPage().render();

            FileDirectoryInfo fileDirectoryInfo = docLibPage.getFileDirectoryInfo(fileName);

            SyncInfoPage syncInfoPage = fileDirectoryInfo.clickOnViewCloudSyncInfo().render();

            // Check for cloud sync location i.e.
            // premiernet.test>SitemEnterprise42-713644>Documents
            Assert.assertTrue(syncInfoPage.getCloudSyncLocation().contains(hybridDomainPremium + ">" + siteName + ">Documents"));

            Assert.assertNotNull(syncInfoPage.getSyncPeriodDetails());

            Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));

            Assert.assertTrue(syncInfoPage.getCloudSyncLocation().contains(hybridDomainPremium + ">" + siteName + ">Documents"));

        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * @throws Exception
     */
    @Test(groups =
    {"CloudSync"})
    public void dataPrep_ALF_7178() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "2", hybridDomainPremium);
        String siteName = getSiteName(testName + "01");

        // Create User (On Premise)
        String[] userInfo =
        { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);
        userInfo = new String[]
        { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Create User (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // Login as User (OP User)
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // User2 joins the site created.
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);
        ShareUser.logout(drone);

        // Login as User (Cloud)
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    /**
     * *****************Sync Status By CONSUMER (7178)*************************
     * 1) Upload file on -prem site. 2) sync with cloud user and site 3) User1
     * logs out. 4) User2 logs into on-prem and drive to site(which is shared
     * with User1 and USer2 is a consumer). 3) check sync status by clicking on
     * syncToCloud icon after a wait. 4) sync should be completed. 5) In sync to
     * cloud pop up it should show cloud user details.
     * 
     * @throws Exception
     */
    @Test(groups = {"CloudSync"})
    public void ALF_7178() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser1 = getUserNameForDomain(testName + "1", hybridDomainPremium);
            String testUser2 = getUserNameForDomain(testName + "2", hybridDomainPremium);
            String siteName = getSiteName(testName + "01");
            String fileName = getFileName(testName + System.currentTimeMillis());

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(siteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

            SiteUtil.openSiteDocumentLibraryURL(drone, siteName);
            // Uploading multiple files and folders.
            ShareUser.uploadFileInFolder(drone, new String[]
            { fileName, DOCLIB });

            // Sync the file to Cloud
            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

            ShareUser.logout(drone);

            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            DocumentLibraryPage docLibPage = SiteUtil.openSiteDocumentLibraryURL(drone, siteName);

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName));

            drone.refresh();
            docLibPage = drone.getCurrentPage().render();
            SyncInfoPage syncInfoPage = docLibPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();

            // Synced just now, by userEnterprise42-71781@premiernet.test
            Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));

            Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains(testUser1));

            ShareUser.logout(drone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * @throws Exception
     */
    @Test(groups = { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_7149() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "1", hybridDomainPremium);
        String siteName = getSiteName(testName);

        // Create User (On Premise)
        String[] userInfo =
        { testUser };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Create User (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // Login as User (Cloud)
        ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User (OP User)
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    /**
     ****** Sync status check from CLOUD LOCATION (7147,7149) 1) Upload a folder on
     * site. 2) Sync folder with the cloud user. 3) Open folder and upload
     * multiple files. 4) Uploaded files should be synced. 5) Login to cloud
     * user and drive to site. 6) Test uploaded folder (from on -pre user
     * step-1) appears. 7) Test uploaded files (from on -pre user step-4)
     * appear.
     * 
     * @throws Exception
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_7149() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "1", hybridDomainPremium);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName + System.currentTimeMillis());
        String folderName = getFolderName(testName + System.currentTimeMillis());

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
        try
        {
            // Login to user(on-prem) and upload file\s and folder\s.
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            SiteUtil.openSiteDocumentLibraryURL(drone, siteName);
            // Folder upload/sync
            ShareUserSitePage.createFolder(drone, folderName, folderName);

            AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);

            // Assert to check Sync to cloud icon is present
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, folderName));

            DocumentLibraryPage docPage = drone.getCurrentPage().render();

            FileDirectoryInfo fileDirInfo = docPage.getFileDirectoryInfo(folderName);

            Assert.assertTrue(fileDirInfo.isViewCloudSyncInfoLinkPresent());

            SyncInfoPage syncInfoPage = docPage.getFileDirectoryInfo(folderName).clickOnViewCloudSyncInfo().render();

            /*
             * Check for cloud sync location i.e.
             * premiernet.test>SitemEnterprise42-713644>Documents
             */

            Assert.assertTrue(syncInfoPage.getCloudSyncLocation().contains(hybridDomainPremium + ">" + siteName + ">Documents"));
            docPage = syncInfoPage.clickOnCloseButton().render();
            // File upload under folder
            for (int i = 0; i < 3; i++)
            {
                ShareUser.uploadFileInFolder(drone, new String[] { fileName + i, folderName }).render();
                Assert.assertTrue(checkIfContentIsSynced(drone, fileName+i));
            }



            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);

            docPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);

            fileDirInfo = docPage.getFileDirectoryInfo(folderName);

            // check for 7147
            Assert.assertTrue(fileDirInfo.isViewCloudSyncInfoLinkPresent());

            syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo();

            Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));
            docPage = syncInfoPage.clickOnCloseButton().render();

            // check for 7149
            docPage = docPage.selectFolder(folderName).render();
            for(int i=0; i<3; i++)
            {
                docPage = docPage.renderItem(maxWaitTime, fileName+i);
                Assert.assertTrue(docPage.isFileVisible(fileName+i));
            }

            ShareUser.logout(hybridDrone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * @throws Exception
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_7151() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "1", hybridDomainPremium);
        String siteName = getSiteName(testName);

        // Create User (On Premise)
        String[] userInfo =
        { testUser };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Create User (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // Login as User (Cloud)
        ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User (OP User)
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    /**
     ****** Sync status check from CLOUD LOCATION (7151, 7150) 1) Create folder on
     * cloud user. 2) Create folder/sub/supersub folderon cloud user. 3) Create
     * folder on prem user. 4) Create another folder on prem user. 5) Sync
     * folder on prem user with cloud user folder/sub/supersub. 6) Sync folder
     * on prem user with cloud user folder (another). 7) Login to cloud user and
     * test step 5 folder is synced and displayed cloud sync icon on cloud
     * user's folder/sub/super sub/folder(on-prem). 8) Test step 6 folder is
     * been synced under cloud user's folder(cloud)/folder(on-prem).
     * 
     * @throws Exception
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_7151() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "1", hybridDomainPremium);
        String siteName = getSiteName(testName);
        String opFolderName = getFolderName(testName + System.currentTimeMillis());
        String cldFolderName = getFolderName(testName + System.currentTimeMillis());
        String cldSubFolderName = getFolderName("sub" + testName + System.currentTimeMillis());
        String cldSuperSubFolderName = getFolderName("superSub" + testName + System.currentTimeMillis());
        String opFolderName2 = getFolderName(testName + "2" + System.currentTimeMillis());
        String cldFolderName2 = getFolderName(testName + "2" + System.currentTimeMillis());

        DocumentLibraryPage docLibPage;
        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        try
        {
            // Login as User (Cloud)
            ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);

            SiteUtil.openSiteDocumentLibraryURL(hybridDrone, siteName);
            // create folder 2 on cloud user.
            ShareUserSitePage.createFolder(hybridDrone, cldFolderName2, cldFolderName);

            // Create folder and sub folders under it.
            docLibPage = ShareUserSitePage.createFolder(hybridDrone, cldFolderName, cldFolderName);
            docLibPage.selectFolder(cldFolderName);
            docLibPage = ShareUserSitePage.createFolder(hybridDrone, cldSubFolderName, cldSubFolderName);
            docLibPage.selectFolder(cldSubFolderName);
            ShareUserSitePage.createFolder(hybridDrone, cldSuperSubFolderName, cldSuperSubFolderName);
            ShareUser.logout(hybridDrone);

            // Login to user(on-prem)
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            SiteUtil.openSiteDocumentLibraryURL(drone, siteName);
            // Create op user Folder
            docLibPage = ShareUserSitePage.createFolder(drone, opFolderName, opFolderName);

            // Create Folder2 on op-user.
            ShareUserSitePage.createFolder(drone, opFolderName2, opFolderName);

            // syncing opuser folder with cloud user sub folder.
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(siteName);
            desAndAssBean.setSyncToPath(cldFolderName, cldSubFolderName, cldSuperSubFolderName);

            AbstractCloudSyncTest.syncContentToCloud(drone, opFolderName, desAndAssBean);

            Assert.assertTrue(checkIfContentIsSynced(drone, opFolderName));

            drone.refresh();
            docLibPage = drone.getCurrentPage().render();
            // syncing opuser folder2 with cloud user folder2.
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(siteName);
            desAndAssBean.setSyncToPath(cldFolderName2);

            syncContentToCloud(drone, opFolderName2, desAndAssBean);
            Assert.assertTrue(checkIfContentIsSynced(drone, opFolderName2));

            ShareUser.logout(drone);

            // Login as User (Cloud) and test sync of opuser folder with cloud
            // user sub folder.
            ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
            docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
            docLibPage = docLibPage.selectFolder(cldFolderName).render();
            docLibPage = docLibPage.renderItem(maxWaitTime, cldSubFolderName);
            docLibPage = docLibPage.selectFolder(cldSubFolderName).render();
            docLibPage = docLibPage.renderItem(maxWaitTime, cldSuperSubFolderName);
            docLibPage = docLibPage.selectFolder(cldSuperSubFolderName).render();
            docLibPage = docLibPage.renderItem(maxWaitTime, opFolderName);
            Assert.assertTrue(docLibPage.getFileDirectoryInfo(opFolderName).isViewCloudSyncInfoLinkPresent());

            // test to check cloud user folder 2 has op user folder 2 synced
            // with it.
            docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
            docLibPage = docLibPage.selectFolder(cldFolderName2).render();
            docLibPage = docLibPage.renderItem(maxWaitTime, opFolderName2);
            Assert.assertTrue(docLibPage.getFileDirectoryInfo(opFolderName2).isViewCloudSyncInfoLinkPresent());

        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7135 - Enterprise40x-7135:Cloud icon for a synced folder 1) Create
     * On-Prem user 2) Create a Cloud User 3) Login to On-Premise and set up
     * Cloud Sync
     */
    @Test(groups =
    { "DataPrepCloudSync1", "DataPrepCloudSync" })
    public void dataPrep_ALF_7135() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);
        String[] userInfo1 = new String[]
        { user1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // SetUp CloudSync
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);

        // User1 logs out from OP
        ShareUser.logout(drone);
    }

    /**
     * 7135 - Enterprise40x-7135:Cloud icon for a synced folder 1) Login to
     * Cloud, create a site 2) Login to OP, create a site, Create a folder and
     * upload a file within that folder 3) Select the folder created, select
     * "Sync to Cloud" from more options 6) Select the Site and click Sync 7)
     * Verify CloudSync Info Link is displayed for the folder 8) Login to cloud,
     * open the site document library from search results and verify the synced
     * Folder is displayed.
     */
    @Test(groups =
    {"CloudSync"})
    public void ALF_7135() throws Exception
    {
        try
        {

            testName = getTestName();
            String user1 = getUserNameForDomain(testName, hybridDomainPremium);

            String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
            String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
            String fileName = getFileName(testName) + "-1.txt";
            String folderName = String.valueOf(System.currentTimeMillis());
            String[] fileInfo =
            { fileName, folderName };

            // Login into cloud as User1
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Create a site
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login into OP as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Create a Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            // Open Document library and Upload a file
            ShareUserSitePage.createFolder(drone, folderName, folderName);

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
            documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();
            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(cloudSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);

            // Verify CloudSync Info Link is displayed on each document.
            Assert.assertTrue(checkIfContentIsSynced(drone, folderName));

            ShareUser.logout(drone);

            // Login to cloud and verify the synced file appeared
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            Assert.assertTrue(documentLibraryPage.isFileVisible(folderName), "Verifying the synced file displayed in cloud user's site document library");

            ShareUser.logout(hybridDrone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

}
