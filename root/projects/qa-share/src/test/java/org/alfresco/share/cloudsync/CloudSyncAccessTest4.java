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
import org.alfresco.po.share.site.document.*;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Class includes: Tests from TestLink in Area: Cloud Sync
 * <ul>
 * <li>The tests that check the access level/authentication for cloud sync.</li>
 * </ul>
 * 
 * @author Abhijeet Bharade
 */
@Listeners(FailedTestListener.class)
public class CloudSyncAccessTest4 extends AbstractCloudSyncTest
{

    private static Log logger = LogFactory.getLog(CloudSyncAccessTest4.class);

    private String fileName;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
    }

    /**
     * 7123 - Enterprise40x-7123:Sync File(s) to the Cloud. More than one
     * network 1) Create On-Prem user 2) Create a Cloud User 3) Login to
     * On-Premise and set up Cloud Sync
     */
    @Test(groups = { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7123() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName + "1", hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "2", hybridDomainFree);
        String[] userInfo1 = new String[]
                { user1 };
        String[] userInfo2 = new String[]
                { user2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo2);

        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // SetUp CloudSync
        signInToAlfrescoInTheCloud(drone, user2, DEFAULT_PASSWORD);

        // User1 logs out from OP
        ShareUser.logout(drone);
    }

    /**
     * 7123 - Enterprise40x-7123:Sync File(s) to the Cloud. More than one
     * network 1) Login to Cloud as User1, create a site 2) User1 Invites User2
     * to join the site 3) Login to OP, setup CloudSync with Cloud User2 4)
     * Create a site and upload a file 5) Select the file, select
     * "Sync to Cloud" from more options 6) Verify both networks are displayed
     * in Destination And Assignee Page 7) Select Network, Site and select Sync
     * 8) Select the Site and click Sync 9) Verify CloudSync Info Link is
     * displayed for the file
     */
    @Test(groups = { "CloudSync" })
    public void ALF_7123() throws Exception
    {

        try
        {
            testName = getTestName();
            String user1 = getUserNameForDomain(testName + "1", hybridDomainPremium);
            String user2 = getUserNameForDomain(testName + "2", hybridDomainFree);
            String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
            String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
            String userFileName = getFileName(testName) + "-UF.txt";
            String[] userFileInfo =
                    { userFileName, DOCLIB };

            // Login into cloud as User1
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

            // Create a site
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

            ShareUser.logout(hybridDrone);

            // User 1 sends the invite to User 2 to join Site
            CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, user1, user2, getSiteShortname(cloudSiteName), "SiteCollaborator", "");

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Create a Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            // Open Document library and Upload a file
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, userFileInfo);
            // Select "Sync to Cloud" from More Options of the file
            DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(userFileName).selectSyncToCloud().render();

            // Verify both networks are displayed in Destination And Assignee
            // Page
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(hybridDomainPremium));
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(hybridDomainFree));

            // Select Network, Site and select Sync
            destinationAndAssigneePage.selectNetwork(hybridDomainPremium);
            destinationAndAssigneePage.selectSite(cloudSiteName);
            destinationAndAssigneePage.selectSubmitButtonToSync();

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, userFileName));

            // User1 logs out from OP
            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 7126 - Enterprise40x-7126:Sync File(s) to the Cloud from Details page.
     * More than one network 1) Create On-Prem user 2) Create two Cloud Users 3)
     * Login to Cloud as User1, create a site 4) User1 Invites User2 to join the
     * site 5) Login to OP, setup CloudSync with Cloud User2 6) Create a site
     * and upload a file
     */
    @Test(groups = { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7126() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName + "1", hybridDomainPremium);
        String user2 = getUserNameForDomain(testName + "2", hybridDomainFree);
        String[] userInfo1 = new String[]
                { user1 };
        String[] userInfo2 = new String[]
                { user2 };
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String userFileName = getFileName(testName) + "-UF.txt";
        String[] userFileInfo =
                { userFileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserFree, userInfo2);

        // Login into cloud as User1
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        // Create a site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Cloud User 1 sends the invite to Cloud User 2 to join Site
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, user1, user2, getSiteShortname(cloudSiteName), "SiteCollaborator", "");

        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // SetUp CloudSync
        signInToAlfrescoInTheCloud(drone, user2, DEFAULT_PASSWORD);

        // Create a Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library and Upload a file
        ShareUser.uploadFileInFolder(drone, userFileInfo);

        // User1 logs out from OP
        ShareUser.logout(drone);
    }

    /**
     * 7126 - Enterprise40x-7126:Sync File(s) to the Cloud from Details page.
     * More than one network 1) Login to OP as User1 2) Open Site Document
     * Library of the site created in pre-reqs, Select the file, select
     * "Sync to Cloud" from DocumentDetailsPage 3) Verify both networks are
     * displayed in Destination And Assignee Page 4) Select Network, and verify
     * Site is displayed (Create by Cloud User1 in pre-reqs)
     */
    @Test(groups = { "CloudSync" })
    public void ALF_7126() throws Exception
    {

        try
        {
            testName = getTestName();
            String user1 = getUserNameForDomain(testName + "1", hybridDomainPremium);
            // String user2 = getUserNameForDomain(testName+"2",
            // hybridDomainFree);
            String opSiteName = getSiteName(testName) + "-OP";
            String cloudSiteName = getSiteName(testName) + "-CL";
            String userFileName = getFileName(testName) + "-UF.txt";

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            ShareUser.openDocumentDetailPage(drone, userFileName);

            // Select "Sync to Cloud" from More Options of the file
            DestinationAndAssigneePage destinationAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloud(drone);

            // Verify both networks are displayed in Destination And Assignee
            // Page
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(hybridDomainPremium));
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(hybridDomainFree));

            // Select Network, Site and select Sync
            destinationAndAssigneePage.selectNetwork(hybridDomainPremium);
            Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSiteName));

            // User1 logs out from OP
            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * 7132 - Enterprise40x-7132:Sync File(s) to Cloud. Forgot password option
     * 8270 - Enterprise40x-8270:Sync File(s) to the Cloud and set new password
     * - TODO - Include 8270 steps after Reset Password method implementation.
     * 1) Create On-Prem user 2) Login to On-Premise Create Site and Upload a
     * document
     */
    @Test(groups = { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7132() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);
        String[] userInfo1 = new String[]
                { user1 };
        String opSiteName = getSiteName(testName) + "-OP";
        String userFileName = getFileName(testName) + "-UF.txt";
        String[] userFileInfo =
                { userFileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, userInfo1);

        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create a Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library and Upload a file
        ShareUser.uploadFileInFolder(drone, userFileInfo);
        ShareUser.logout(drone);

    }

    /**
     * 7123 - Enterprise40x-7123:Sync File(s) to the Cloud. More than one
     * network 8270 - Enterprise40x-8270:Sync File(s) to the Cloud and set new
     * password - TODO - Include 8270 steps after Reset Password method
     * implementation. 1) Login to OP as User1, open Site Document Library page
     * from search. 2) Select "Sync to Cloud" from more options 3) Verify
     * "Forgot Password" link is displayed 4) Verify "Forgot Password" link URL
     */
    @Test(groups = { "CloudSync" })
    public void ALF_7132() throws Exception
    {

        try
        {
            testName = getTestName();
            String user1 = getUserNameForDomain(testName, hybridDomainPremium);
            String opSiteName = getSiteName(testName) + "-OP";
            String userFileName = getFileName(testName) + "-UF.txt";

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Select "Sync to Cloud" from More Options of the file
            CloudSignInPage cloudSignInPage = documentLibraryPage.getFileDirectoryInfo(userFileName).selectSyncToCloud().render();

            // Verify "Forgot Password" link is displayed
            Assert.assertTrue(cloudSignInPage.isForgotPasswordLinkDisplayed(), "Verify \"Forgot Password\" link is displayed on CloudSignIn dialog");

            // Verify "Forgot Password" link URL
            Assert.assertEquals(cloudSignInPage.getForgotPasswordURL(), FORGOT_PASSWORD_LINK_URL, "Verify \"Forgot Password\" link URL");

            // User1 logs out from OP
            ShareUser.logout(drone);
        } catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Data preperation for Enterprise40x-7176.
     *
     * @throws Exception
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7176() throws Exception
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
     * 1) Create a folder in cloud user. 2) Create folder in on prem user. 3)
     * Creat sub folders and files under folder. 4) Sync Folder of on prem user
     * along with its sybfolder. 5) Login to cloud user and go to site. 6) Check
     * subfolders of on prem user which are synced are appearing.
     *
     * @throws Exception
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7176() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, hybridDomainPremium);
        String siteName = getSiteName(testName);

        String opFolderToBeSynced = getFolderName(testName + System.currentTimeMillis());
        String opSubFolderName1 = getFolderName("sub" + testName + System.currentTimeMillis() + "-1");
        String opSubFolderName2 = getFolderName("sub" + testName + System.currentTimeMillis() + "-2");
        String opFileName1 = getFileName("sub" + testName + System.currentTimeMillis() + "-1");
        String opFileName2 = getFileName("sub" + testName + System.currentTimeMillis() + "-2");

        // Cloud folder under which op prem folder will be synced.
        String cldFolderName = getFolderName(testName + System.currentTimeMillis());

        DocumentLibraryPage docLibPage;

        try
        {
            // Login as User (Cloud)
            ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
            SiteUtil.openSiteDocumentLibraryURL(hybridDrone, siteName);
            // create folder on cloud user.
            ShareUserSitePage.createFolder(hybridDrone, cldFolderName, cldFolderName);
            ShareUser.logout(hybridDrone);

            // Login to user(on-prem)
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            SiteUtil.openSiteDocumentLibraryURL(drone, siteName);
            // Create op user Folder
            docLibPage = ShareUserSitePage.createFolder(drone, opFolderToBeSynced, opFolderToBeSynced);
            docLibPage.selectFolder(opFolderToBeSynced).render();
            // Create sub folder-1
            ShareUserSitePage.createFolder(drone, opSubFolderName1, opSubFolderName1);
            // Create sub folder-2
            ShareUserSitePage.createFolder(drone, opSubFolderName2, opSubFolderName2);
            // Upload file-1
            ShareUser.uploadFileInFolder(drone, new String[]
                    { opFileName1, opFolderToBeSynced });
            // Upload file-2
            docLibPage = ShareUser.uploadFileInFolder(drone, new String[]
                    { opFileName2, opFolderToBeSynced });

            docLibPage.getSiteNav().selectSiteDocumentLibrary().render();
            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(siteName);
            desAndAssBean.setSyncToPath(cldFolderName);

            DestinationAndAssigneePage destinationAssignee = selectSyncToCloudDocLib(drone, opFolderToBeSynced);

//            AbstractCloudSyncTest.syncContentToCloud(drone, opFolderToBeSynced, desAndAssBean);

            // Selecting site, lock on prem as false, sub folder to sync as
            // true, and target cloud folder.
            destinationAssignee.selectSite(siteName);
            destinationAssignee.selectFolder(cldFolderName);
            // No change to select SubFolders since its default selected.
            Assert.assertTrue(destinationAssignee.isIncludeSubFoldersSelected());
            destinationAssignee.selectSubmitButtonToSync().render();

            Assert.assertTrue(checkIfContentIsSynced(drone, opFolderToBeSynced));
            ShareUserSitePage.navigateToFolder(drone, opFolderToBeSynced);

            Assert.assertTrue(checkIfContentIsSynced(drone, opSubFolderName1));
            Assert.assertTrue(checkIfContentIsSynced(drone, opSubFolderName2));
            Assert.assertTrue(checkIfContentIsSynced(drone, opFileName1));
            Assert.assertTrue(checkIfContentIsSynced(drone, opFileName2));

            ShareUser.logout(drone);

            ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
            docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
            // create folder on cloud user.
            docLibPage.selectFolder(cldFolderName).render();
            // Assert to test synced sub Folders and Files from onPrem user is
            // present in cloud folder or not.
            docLibPage.selectFolder(opFolderToBeSynced).render();
            Assert.assertEquals(opSubFolderName1, docLibPage.getFileDirectoryInfo(opSubFolderName1).getName());
            Assert.assertEquals(opSubFolderName2, docLibPage.getFileDirectoryInfo(opSubFolderName2).getName());
            Assert.assertEquals(opFileName1, docLibPage.getFileDirectoryInfo(opFileName1).getName());
            Assert.assertEquals(opFileName2, docLibPage.getFileDirectoryInfo(opFileName2).getName());
            ShareUser.logout(hybridDrone);

        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * Data preperation for Enterprise40x-7177.
     *
     * @throws Exception
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7177() throws Exception
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
     * 1) Create a folder in cloud user. 2) Create folder in on prem user. 3)
     * Creat sub folders and files under folder. 4) Sync Folder of on prem user
     * without its sybfolder. 5) Login to cloud user and go to site. 6) Check
     * subfolders of on prem user which are synced must not appear.
     *
     * @throws Exception
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7177() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, hybridDomainPremium);
        String siteName = getSiteName(testName);

        String opFolderToBeSynced = getFolderName(testName + System.currentTimeMillis());
        String opSubFolderName1 = getFolderName("sub" + testName + System.currentTimeMillis() + "-1");
        String opSubFolderName2 = getFolderName("sub" + testName + System.currentTimeMillis() + "-2");
        String opFileName1 = getFileName("sub" + testName + System.currentTimeMillis() + "-1");
        String opFileName2 = getFileName("sub" + testName + System.currentTimeMillis() + "-2");

        // Cloud folder under which op prem folder will be synced.
        String cldFolderName = getFolderName(testName + System.currentTimeMillis());

        DestinationAndAssigneePage desAndAssigneePage;

        try
        {
            // Login as User (Cloud)
            ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
            DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
            // create folder on cloud user.
            ShareUserSitePage.createFolder(hybridDrone, cldFolderName, cldFolderName);
            ShareUser.logout(hybridDrone);

            // Login to user(on-prem)
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Create op user Folder
            docLibPage = ShareUserSitePage.createFolder(drone, opFolderToBeSynced, opFolderToBeSynced);
            docLibPage.selectFolder(opFolderToBeSynced).render();
            // Create sub folder-1
            ShareUserSitePage.createFolder(drone, opSubFolderName1, opSubFolderName1);
            // Create sub folder-2
            ShareUserSitePage.createFolder(drone, opSubFolderName2, opSubFolderName2);
            // Upload file-1
            ShareUser.uploadFileInFolder(drone, new String[]
                    { opFileName1, opFolderToBeSynced });
            // Upload file-2
            docLibPage = ShareUser.uploadFileInFolder(drone, new String[]
                    { opFileName2, opFolderToBeSynced });

            docLibPage.getSiteNav().selectSiteDocumentLibrary().render();
            desAndAssigneePage = AbstractCloudSyncTest.selectSyncToCloudDocLib(drone, opFolderToBeSynced);

//            desAndAssigneePage = (DestinationAndAssigneePage) docLibPage.getFileDirectoryInfo(opFolderToBeSynced).selectSyncToCloud();
            // Selecting site, lock on prem as false, sub folder to sync as
            // true, and target cloud folder.

            desAndAssigneePage.selectSite(siteName);
            desAndAssigneePage.selectFolder(cldFolderName);
            desAndAssigneePage.unSelectIncludeSubFolders();
            Assert.assertFalse(desAndAssigneePage.isIncludeSubFoldersSelected());
            desAndAssigneePage.selectSubmitButtonToSync().render();

            Assert.assertTrue(checkIfContentIsSynced(drone, opFolderToBeSynced));

            ShareUser.logout(drone);

            ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
            docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
            // create folder on cloud user.
            docLibPage.selectFolder(cldFolderName).render();
            // Assert to test synced sub Folders and Files from onPrem user is
            // present in cloud folder or not.
            docLibPage.selectFolder(opFolderToBeSynced).render();
            List<String> fileNames = new ArrayList<String>();
            for (FileDirectoryInfo fileDirectoryInfo : docLibPage.getFiles())
            {
                fileNames.add(fileDirectoryInfo.getName());
            }
            Assert.assertFalse(fileNames.contains(opSubFolderName1));
            Assert.assertFalse(fileNames.contains(opSubFolderName2));
            Assert.assertTrue(fileNames.contains(opFileName1));
            Assert.assertTrue(fileNames.contains(opFileName2));

            ShareUser.logout(hybridDrone);

        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * Data preperation for Enterprise40x-7157 & 7158.
     *
     * @throws Exception
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7157() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        // Create User (On Premise)
        String[] userInfo = { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);

        // Create User (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // Creat another user in On prem.
        userInfo = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Login as User (Cloud)
        ShareUser.login(hybridDrone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User (OP User)
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // login as user 2 and join the site.
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);
        ShareUser.logout(drone);

    }

    @Test(groups =
            { "CloudSync" })
    public void ALF_7157() throws Exception
    {

        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        String opFolderToBeSynced = getFolderName(testName + System.currentTimeMillis());
        String opFileNameToBeSynced = getFileName("sub" + testName + System.currentTimeMillis() + "-1");
        DocumentLibraryPage docLibPage;

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

        try
        {

            // Login to user(on-prem)
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Create op user Folder
            ShareUserSitePage.createFolder(drone, opFolderToBeSynced, opFolderToBeSynced);

            // sync folder
            AbstractCloudSyncTest.syncContentToCloud(drone, opFolderToBeSynced, desAndAssBean);

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, opFolderToBeSynced));

            // sync file
            ShareUser.uploadFileInFolder(drone, new String[] { opFileNameToBeSynced, DOCLIB });

            AbstractCloudSyncTest.syncContentToCloud(drone, opFileNameToBeSynced, desAndAssBean);

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, opFileNameToBeSynced));

            ShareUser.logout(drone);

            // Login to user2(on-prem)
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
            // Check for sync icon details of document and folder from document
            // library.
            // they should be displaying synced.
            SyncInfoPage syncInfoPage = docLibPage.getFileDirectoryInfo(opFolderToBeSynced).clickOnViewCloudSyncInfo();
            Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));
            docLibPage = syncInfoPage.clickOnCloseButton().render();

            syncInfoPage = docLibPage.getFileDirectoryInfo(opFileNameToBeSynced).clickOnViewCloudSyncInfo();
            Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));
            syncInfoPage.clickOnCloseButton().render();

            ShareUser.logout(drone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * Data preperation for Enterprise40x-7159 & 7160.
     *
     * @throws Exception
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7159() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        // Create User (On Premise)
        String[] userInfo =
                { testUser };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Create User (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        // Creat another user in On prem.
        userInfo = new String[]
                { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Login as User (Cloud)
        ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User (OP User)
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // login as user 2 and join the site.
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);
        ShareUser.logout(drone);

    }

    /**
     * Covers 7159 & 7160.
     *
     * @throws Exception
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7159() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        String opFolderToBeSynced = getFolderName(testName + System.currentTimeMillis());
        String opSubFileNameToBeSynced = getFileName("sub" + testName + System.currentTimeMillis() + "-1");
        String opSubFolderToBeSynced = getFolderName(testName + System.currentTimeMillis());
        DocumentLibraryPage docLibPage;
        DestinationAndAssigneePage desAndAssigneePage;
        try
        {

            // Login to user(on-prem)
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            SiteUtil.openSiteDocumentLibraryURL(drone, siteName);
            // Create op user Folder
            docLibPage = ShareUserSitePage.createFolder(drone, opFolderToBeSynced, opFolderToBeSynced);
            docLibPage.selectFolder(opFolderToBeSynced);
            ShareUserSitePage.createFolder(drone, opSubFolderToBeSynced, opSubFolderToBeSynced);

            docLibPage = ShareUser.uploadFileInFolder(drone, new String[]
                    { opSubFileNameToBeSynced, opFolderToBeSynced });
            docLibPage= docLibPage.getSiteNav().selectSiteDocumentLibrary().render();
            // sync folder
            desAndAssigneePage = (DestinationAndAssigneePage) docLibPage.getFileDirectoryInfo(opFolderToBeSynced).selectSyncToCloud();
            desAndAssigneePage.selectSite(siteName);
            Assert.assertTrue(desAndAssigneePage.isIncludeSubFoldersSelected());
            desAndAssigneePage.selectSubmitButtonToSync();

            Assert.assertTrue(checkIfContentIsSynced(drone, opFolderToBeSynced));

            ShareUser.logout(drone);

            // Login to user2(on-prem)
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
            // /////////////////////////////////////////////////////////////
            //
            // Check for sync icon details of document and subfolder from top
            // folder.
            // they should be displaying sync in-directly.
            // ////////////////////////////////////////////////////////////
            docLibPage = docLibPage.selectFolder(opFolderToBeSynced).render();
            FileDirectoryInfo fileDirInfo = docLibPage.getFileDirectoryInfo(opSubFolderToBeSynced);
            Assert.assertTrue(fileDirInfo.getCloudSyncType().contains("Indirectly "));
            SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo().render();
            Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Synced"));
            syncInfoPage.clickOnCloseButton();

            docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
            docLibPage = docLibPage.selectFolder(opFolderToBeSynced).render();
            fileDirInfo = docLibPage.getFileDirectoryInfo(opSubFileNameToBeSynced);
            Assert.assertTrue(fileDirInfo.getCloudSyncType().contains("Indirectly "));
            SyncInfoPage fileSyncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo().render();
            Assert.assertTrue(fileSyncInfoPage.getCloudSyncStatus().contains("Synced"));
            syncInfoPage.clickOnCloseButton();

            ShareUser.logout(drone);

        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * Data preperation for Enterprise40x-7159 & 7160.
     *
     * @throws Exception
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7165() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

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
     * Covers 7165.
     *
     * @throws Exception
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7165() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String testMsgString = "This document is locked by";
        String opSubFileNameToBeSynced = getFileName("sub" + testName + System.currentTimeMillis() + "-1");
        DocumentLibraryPage docLibPage;
        DestinationAndAssigneePage desAndAssigneePage;
        try
        {

            // Login to user(on-prem)
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            SiteDashboardPage dashBoardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            docLibPage = dashBoardPage.getSiteNav().selectSiteDocumentLibrary().render();

            for (int i = 0; i < 2; i++)
            {
                docLibPage = ShareUser.uploadFileInFolder(drone, new String[]
                        { opSubFileNameToBeSynced + i, DOCLIB });
            }
            docLibPage = docLibPage.getNavigation().selectAll().render();

            DocumentLibraryNavigation docLibNav = docLibPage.getNavigation().render();

            desAndAssigneePage = docLibNav.selectSyncToCloud().render();
            desAndAssigneePage.selectSite(siteName);
            Assert.assertFalse(desAndAssigneePage.isLockOnPremCopy());
            desAndAssigneePage.selectLockOnPremCopy();
            docLibPage = (DocumentLibraryPage) desAndAssigneePage.selectSubmitButtonToSync();
            // iterate all the files and check lock icon is present or not.
            for (int i = 0; i < 2; i++)
            {
                FileDirectoryInfo fileDirInfo = docLibPage.getFileDirectoryInfo(opSubFileNameToBeSynced + i);
                Assert.assertTrue(fileDirInfo.getContentInfo().contains(testMsgString));
            }
            ShareUser.logout(drone);

        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7166 - Enterprise40x-7166:Sync a file to 'on-premise' version after
     * change the property 1) Create On-Prem user 2) Create a Cloud User 3)
     * Login to On-Premise and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7166() throws Exception
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
     * 7166 - Enterprise40x-7166:Sync a file to 'on-premise' version after
     * change the property 1) Login as User (Cloud), create a site 2) Login as
     * OP user, create a site, upload a document 3) Select "Sync to Cloud" from
     * more options 4) Select Cloud Site, Lock on-premise copy check box and
     * select Sync button 5) Verify the document is synced 6) Select the
     * document and Verify the document's version is "1.0" 7) Login as User
     * (Cloud), open site document library, Select Edit Properties, enter
     * description and save 8) Select the file to open Document Details page and
     * verify the Description property is saved and the version is still "1.0"
     * 9) Login as User (OP), verify Document description is updated and version
     * is changed to "1.1" from DocumentDetailsPage
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7166() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String docDescription = testName + " - " + getFileName(testName);
        String[] fileInfo =
                { fileName, DOCLIB };

        try
        {
            // Login as User (Cloud), create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library and Upload a file
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();
            // Select "Sync to cloud" from more options
            DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
            // Select Site Name, Lock on-premise copy check box and select Sync
            // button
            destinationAndAssigneePage.selectSite(cloudSiteName);
            destinationAndAssigneePage.selectLockOnPremCopy();
            documentLibraryPage = destinationAndAssigneePage.selectSubmitButtonToSync().render();

            // Verify the document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName));
            drone.refresh();
            documentLibraryPage = drone.getCurrentPage().render();
            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
            // Verify the document's version is "1.0"
            Assert.assertEquals("1.0", documentDetailsPage.getDocumentVersion());
            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
            // Select Edit Properties, enter description and save
            EditDocumentPropertiesPage editDocumentPropertiesPopup = documentLibraryPage.getFileDirectoryInfo(fileName).selectEditProperties().render();
            editDocumentPropertiesPopup.setDescription(docDescription);
            documentLibraryPage = editDocumentPropertiesPopup.selectSave().render();

            // Select the file to open Document Details page and verify the
            // Description property is saved and the version is still "1.0"
            documentDetailsPage = documentLibraryPage.selectFile(fileName).render();

            Assert.assertEquals(documentDetailsPage.getProperties().get("Description"), docDescription);
            Assert.assertEquals(documentDetailsPage.getDocumentVersion(), "1.0");
            ShareUser.logout(hybridDrone);

            // Login as User (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open the site Document Library from search, select the file to
            // open Document Library page
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            documentDetailsPage = documentLibraryPage.selectFile(fileName).render();

            // Verify Description property is updated and version changed from
            // "1.0" to "1.1"
            Assert.assertTrue(checkForNewVersion(drone, "1.1"));
            Assert.assertEquals(documentDetailsPage.getProperties().get("Description"), docDescription);

            // User1 logs out from OP
            ShareUser.logout(drone);

        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7167 - Enterprise40x-7167:Sync a file to 'on-premise' version after
     * change the content 1) Create On-Prem user 2) Create a Cloud User 3) Login
     * to On-Premise and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7167() throws Exception
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
     * 7167 - Enterprise40x-7167:Sync a file to 'on-premise' version after
     * change the content 1) Login as User (Cloud), create a site 2) Login as OP
     * user, create a site, upload a document 3) Select "Sync to Cloud" from
     * more options 4) Select Cloud Site, Lock on-premise copy check box and
     * select Sync button 5) Verify the document is synced 6) Select the
     * document and Verify the document's version is "1.0" 7) Login as User
     * (Cloud), open site document library, Select Document and select
     * InlineEdit, enter Content and save 8) Verify the version is changed to
     * "1.1" 9) Login as User (OP), verify version is updated to "1.1" from
     * DocumentDetailsPage
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7167() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);

        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo =
                { fileName, DOCLIB };
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(testName + getSiteName(testName));

        try
        {

            // Login as User (Cloud), create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library and Upload a file
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();
            // Select "Sync to cloud" from more options
            DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
            // Select Site Name, Lock on-premise copy check box and select Sync
            // button
            destinationAndAssigneePage.selectSite(cloudSiteName);
            destinationAndAssigneePage.selectLockOnPremCopy();
            documentLibraryPage = destinationAndAssigneePage.selectSubmitButtonToSync().render();

            // Verify the document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verify if the document is synced");
            drone.refresh();
            documentLibraryPage = drone.getCurrentPage().render();
            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
            // Verify the document's version is "1.0"
            Assert.assertEquals("1.0", documentDetailsPage.getDocumentVersion());
            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            documentDetailsPage = ShareUser.openDocumentDetailPage(hybridDrone, fileName);
            // Select Inline Edit and change the content and save
            EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
            documentDetailsPage = inlineEditPage.save(contentDetails).render();

            // Verify the version is changed to "1.1"
            Assert.assertEquals("1.1", documentDetailsPage.getDocumentVersion());
            ShareUser.logout(hybridDrone);

            // Login as User (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open the site Document Library from search, select the file to
            // open Document Library page
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            documentDetailsPage = documentLibraryPage.selectFile(fileName).render();
            // Verify version changed from "1.0" to "1.1"
            Assert.assertTrue(checkForNewVersion(drone, "1.1"));

            // User1 logs out from OP
            ShareUser.logout(drone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7168 - Enterprise40x-7168:Editing of the locked file 1) Create On-Prem
     * user 2) Create a Cloud User 3) Login to On-Premise and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7168() throws Exception
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
     * 7168 - Enterprise40x-7168:Editing of the locked file 1) Login as User
     * (Cloud), create a site 2) Login as OP user, create a site, upload a
     * document 3) Select "Sync to Cloud" from more options 4) Select Cloud
     * Site, Lock on-premise copy check box and select Sync button 5) Verify the
     * document is synced 6) Verify Message on locked content 7) Verify
     * "Inline Edit" and "Edit Offline" links are not displayed
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7168() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);

        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo =
                { fileName, DOCLIB };
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(testName + getSiteName(testName));

        try
        {
            // Login as User (Cloud), create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library and Upload a file
            ShareUser.uploadFileInFolder(drone, fileInfo).render();

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(cloudSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
            desAndAssBean.setLockOnPrem(true);

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);
            // Select "Sync to cloud" from more options
            // destinationAndAssigneePage.selectLockOnPremCopy();
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName));

            DocumentLibraryPage documentLibraryPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);

            // Verify the document is synced
            // Verify Message on locked content
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getContentInfo(), DOCUMENT_LOCKED_BY_YOU_MESSAGE);

            DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
            // Verify "Inline Edit" and "Edit Offline" links are not displayed
            Assert.assertFalse(documentDetailsPage.isInlineEditLinkDisplayed());
            Assert.assertFalse(documentDetailsPage.isEditOfflineLinkDisplayed());
            ShareUser.logout(drone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7169 - Enterprise40x-7169:Removing the lock by unsyncing the file 1)
     * Create On-Prem user 2) Create a Cloud User 3) Login to On-Premise and set
     * up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7169() throws Exception
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
     * 7169 - Enterprise40x-7169:Removing the lock by unsyncing the file 1)
     * Login as User (Cloud), create a site 2) Login as OP user, create a site,
     * upload a document 3) Select "Sync to Cloud" from more options 4) Select
     * Cloud Site, Lock on-premise copy check box and select Sync button 5)
     * Verify the Sync, Lock icons are displayed and Locked message is displayed
     * 6) Verify "Inline Edit" and "Edit Offline" options are NOT displayed in
     * More options menu 7) Select "UnSync from Cloud" option from More options
     * 8) Verify the Sync, Lock icons are NOT displayed 9) Verify "Inline Edit"
     * and "Edit Offline" options are displayed in More options menu
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7169() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);

        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo =
                { fileName, DOCLIB };
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(testName + getSiteName(testName));

        try
        {
            // Login as User (Cloud), create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library and Upload a file
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
            // Select "Sync to cloud" from more options
            DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();
            // Select Site Name, Lock on-premise copy check box and select Sync
            // button
            destinationAndAssigneePage.selectSite(cloudSiteName);
            destinationAndAssigneePage.selectLockOnPremCopy();
            destinationAndAssigneePage.selectSubmitButtonToSync().render();

            // verify the content is Synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verify if the document is synced");
            // Verify Sync and Lock icons are displayed
            documentLibraryPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "Verify if the document is Locked");
            // Verify Message on locked content
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getContentInfo(), DOCUMENT_LOCKED_BY_YOU_MESSAGE);
            // Verify "Inline Edit" and "Edit Offline" are not displayed in More
            // options
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isInlineEditLinkPresent());
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

            // Select "UnSync from cloud option

            documentLibraryPage.getFileDirectoryInfo(fileName).selectUnSyncAndRemoveContentFromCloud(true);
            documentLibraryPage.render();
            // Verify the Sync and Lock icons are not displayed
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verify if the document is synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isLocked(), "Verify if the document is Locked");
            // Verify "Inline Edit" and "Edit Offline" links are available
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isInlineEditLinkPresent());
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

            ShareUser.logout(drone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7171 - Enterprise40x-7171:Creating a new folder in a cloud target
     * selection window 7172 - Enterprise40x-7172:Creating a new folder in a
     * cloud target selection window under documents folder 7174 -
     * Enterprise40x-7174:Creating a new folder in cloud and sync a file to it
     * 1) Create On-Prem user 2) Create a Cloud User 3) Login to On-Premise and
     * set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7171() throws Exception
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
     * 7171 - Enterprise40x-7171:Creating a new folder in a cloud target
     * selection window 7172 - Enterprise40x-7172:Creating a new folder in a
     * cloud target selection window under documents folder 7174 -
     * Enterprise40x-7174:Creating a new folder in cloud and sync a file to it
     * 1) Login as User (Cloud), create a site 2) Login as OP user, create a
     * site, upload a document 3) Select "Sync to Cloud" from more options 4)
     * Select Cloud Site 5) Create a new folder by selecting "New Folder" icon
     * and Select Sync button 6) Verify the document is synced 7) Login as User
     * (Cloud), open site document library 8) Verify the folder created from ENT
     * exists 9) Select the folder and Verify the Synced Doc exists
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7171() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);

        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        String cloudFolderName = testName + "-Folder";

        try
        {

            // Login as User (Cloud), create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library and Upload a file
            ShareUser.uploadFileInFolder(drone, fileInfo).render();
            // Select "Sync to cloud" from more options
            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();

            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(cloudSiteName);

            createNewFolderAndSyncContent(drone, fileName, desAndAssBean, cloudFolderName);

            drone.refresh();
            drone.getCurrentPage().render();
            Assert.assertTrue(isCloudSynced(drone, fileName));
            // Verify the document is synced
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName));

            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            // Verify the Folder created in ENT exists in the site document
            // library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), cloudFolderName);
            // Select the folder and verify the synced file exist.
            documentLibraryPage = documentLibraryPage.selectFolder(cloudFolderName).render();

            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName);
            ShareUser.logout(hybridDrone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7170 - Enterprise40x-7170:Checking the history of the synced file 1)
     * Create On-Prem user 2) Create a Cloud User 3) Login to On-Premise and set
     * up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7170() throws Exception
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
     * 7170 - Enterprise40x-7170:Checking the history of the synced file 1)
     * Login as User (Cloud), create a site 2) Login as OP user, create a site,
     * upload a document 3) Select "Sync to Cloud" from more options select the
     * site in cloud and sync 4) Verify the file is synced from document library
     * page 5) Click on Document (DocumentDetailsPage) 6) Verify Sync Status,
     * Sync location and request sync icon is displayed.
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7170() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);

        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo =
                { fileName, DOCLIB };

        try
        {
            // Login as User (Cloud), create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library and Upload a file
            ShareUser.uploadFileInFolder(drone, fileInfo);
            // Select "Sync to cloud" from more options

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(cloudSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

            drone.refresh();
            drone.getCurrentPage().render();
            // Verify the document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName));

            // Click on Document (DocumentDetailsPage)
            DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

            // Verify Sync Status, Sync location and request sync icon is
            // displayed.
            String expectedLocation = getUserDomain(user1) + " > " + cloudSiteName + " > " + DEFAULT_FOLDER_NAME;
            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains("Sync"));
            Assert.assertTrue(documentDetailsPage.isRequestSyncIconDisplayed());
            Assert.assertEquals(documentDetailsPage.getLocationInCloud(), expectedLocation);
            ShareUser.logout(drone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7173 - Enterprise40x-7173:Creating a new folder in a cloud target
     * selection window under one of the subfolders 1) Create On-Prem user 2)
     * Create a Cloud User 3) Login to On-Premise and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7173() throws Exception
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
     * 7173 - Enterprise40x-7173:Creating a new folder in a cloud target
     * selection window under one of the subfolders 1) Login as User (Cloud),
     * create a site 2) Login as OP user, create a site, upload a document 3)
     * Select "Sync to Cloud" from more options 4) Select Cloud Site 5) Create a
     * new folder by selecting "New Folder" icon and Select Sync button 6)
     * Verify the document is synced 7) Login as User (Cloud), open site
     * document library 8) Verify the folder created from ENT exists 9) Select
     * the folder and Verify the Synced Doc exists
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7173() throws Exception
    {

        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);

        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo =
                { fileName, DOCLIB };
        String cloudFolderName = testName + "-Folder";
        String folderName1 = getFolderName(testName + "-1");

        try
        {
            // Login as User (Cloud), create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUserSitePage.createFolder(hybridDrone, folderName1, "Folder1 Description");
            ShareUser.logout(hybridDrone);

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library and Upload a file
            ShareUser.uploadFileInFolder(drone, fileInfo).render();
            // Select "Sync to cloud" from more options

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(cloudSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME, folderName1);

            AbstractCloudSyncTest.createNewFolderAndSyncContent(drone, fileName, desAndAssBean, cloudFolderName);
            // Verify the document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName));

            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            documentLibraryPage = documentLibraryPage.selectFolder(folderName1).render();
            documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, cloudFolderName);
            // Verify the Folder created in ENT exists in the site document
            // library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), cloudFolderName);
            // Select the folder and verify the synced file exist.
            documentLibraryPage = documentLibraryPage.selectFolder(cloudFolderName).render();
            documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, fileName);

            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName);
            ShareUser.logout(hybridDrone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7175 - Enterprise40x-7171:Creating a new folder in a cloud target
     * selection window 1) Create On-Prem user 2) Create a Cloud User 3) Login
     * to On-Premise and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7175() throws Exception
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
     * 7175 - Enterprise40x-7171:Creating a new folder in a cloud target
     * selection window 1) Login as User (Cloud), create a site 2) Login as OP
     * user, create a site, create a folder and upload a document within the
     * folder 3) Select the folder and select "Sync to Cloud" from more options
     * 4) Select Cloud Site 5) Create a new folder by selecting "New Folder"
     * icon and Select Sync button 6) Verify the folder is synced 7) Login as
     * User (Cloud), open site document library 8) Verify the folder created
     * from ENT exists 9) Select the folder and Verify the Synced Folder and
     * document within the folder exists
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7175() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);

        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String opFolderName = testName + "-OP_Folder";
        String[] fileInfo =
                { fileName, opFolderName };
        String cloudFolderName = testName + "-Folder";

        try
        {

            // Login as User (Cloud), create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, create a folder and Upload a file in that
            // folder
            ShareUserSitePage.createFolder(drone, opFolderName, "Folder-1");
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

            documentLibraryPage = documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();
            // Select the folder and select "Sync to cloud" from more options

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(user1));
            desAndAssBean.setSiteName(cloudSiteName);

            AbstractCloudSyncTest.createNewFolderAndSyncContent(drone, opFolderName, desAndAssBean, cloudFolderName);

            // Verify the document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, opFolderName));

            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            // Verify the Folder created in ENT exists in the site document
            // library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), cloudFolderName);
            // Select the folder and verify the synced Folder and the file
            // within the folder exist.
            documentLibraryPage = documentLibraryPage.selectFolder(cloudFolderName).render();

            documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, opFolderName);
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), opFolderName);

            documentLibraryPage = documentLibraryPage.selectFolder(opFolderName).render();
            documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, fileName).render();
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName);
            ShareUser.logout(hybridDrone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7175 - Enterprise40x-7152:Request sync. Changes to file 1) Create On-Prem
     * user 2) Create a Cloud User 3) Login to Cloud, Crate a site 4) Login to
     * On-Premise and set up Cloud Sync 5) Create a site, upload a file and sync
     * the file
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7152() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);
        String[] userInfo1 = new String[]
                { user1 };
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo =
                { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login as User (Cloud), create a site
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // SetUp CloudSync
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        // Open Document library, Upload a file

        ShareUser.uploadFileInFolder(drone, fileInfo);

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(cloudSiteName);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
        // sync to cloud
        AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);
        ShareUser.logout(drone);
    }

    /**
     * 7152 - Enterprise40x-7152:Request sync. Changes to file 1) Login as OP
     * user, open site document library from search 2) Select Edit Properties of
     * the file, change description and save 3) Verify the description has been
     * changed 4) Select "Request Sync" link from more options 5) Login to
     * Cloud, open site document library from search 6) Verify the description
     * has been updated
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7152() throws Exception
    {

        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String fileName = getFileName(testName) + ".txt";

        String propertyDescription = String.valueOf(System.currentTimeMillis());

        try
        {
            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            // Verify the document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verify if the document is synced");

            EditDocumentPropertiesPage editDocumentPropertiesPopup = documentLibraryPage.getFileDirectoryInfo(fileName).selectEditProperties().render();

            editDocumentPropertiesPopup.setDescription(propertyDescription);
            documentLibraryPage = editDocumentPropertiesPopup.selectSave().render();
            // Verify document description is changed
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getDescription(), propertyDescription);
            documentLibraryPage.getFileDirectoryInfo(fileName).selectRequestSync().render();

            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            // Verify the Description has been changed in cloud
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName).getDescription(), propertyDescription);
            ShareUser.logout(hybridDrone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7153 - Enterprise40x-7153:Request sync. changes to folder properties 1)
     * Create On-Prem user 2) Create a Cloud User 3) Login to Cloud, Crate a
     * site 4) Login to On-Premise and set up Cloud Sync 5) Create a site,
     * create a folder and sync the folder
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7153() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);
        String[] userInfo1 = new String[]
                { user1 };
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String folderName = getFolderName(testName) + "-OP";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login as User (Cloud), create a site
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // SetUp CloudSync
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        // Open Document library, create a folder

        ShareUserSitePage.createFolder(drone, folderName, "Folder-1");

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(cloudSiteName);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
        // Select the folder and select "Sync to cloud" from more options
        AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);
        // User1 logs out from OP
        ShareUser.logout(drone);
    }

    /**
     * 7153 - Enterprise40x-7153:Request sync. changes to folder properties 1)
     * Login as OP user, open site document library from search 2) Select Edit
     * Properties of the folder, change description and save 3) Verify the
     * description has been changed 4) Select "Request Sync" link from more
     * options 5) Login to Cloud, open site document library from search 6)
     * Verify the description has been updated
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7153() throws Exception
    {

        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String folderName = getFolderName(testName) + "-OP";
        String propertyDescription = String.valueOf(System.currentTimeMillis());

        try
        {

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            // Verify the document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, folderName));

            EditDocumentPropertiesPage editDocumentPropertiesPopup = documentLibraryPage.getFileDirectoryInfo(folderName).selectEditProperties().render();

            editDocumentPropertiesPopup.setDescription(propertyDescription);
            documentLibraryPage = editDocumentPropertiesPopup.selectSave().render();
            // Verify the folder description is changed
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(folderName).getDescription(), propertyDescription);
            documentLibraryPage.getFileDirectoryInfo(folderName).selectRequestSync().render();

            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            // Verify the Description has been changed in cloud
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(folderName).getDescription(), propertyDescription);
            ShareUser.logout(hybridDrone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7154 - Enterprise40x-7154:Request sync. Changes to some files in synced
     * folder 1) Create On-Prem user 2) Create a Cloud User 3) Login to Cloud,
     * set up Cloud Sync with the cloud user
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7154() throws Exception
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
     * 7154 - Enterprise40x-7154:Request sync. Changes to some files in synced
     * folder 1) Login to Cloud, create a site, logout 2) Login as OP user,
     * create a site, create a folder, upload 2 files 3) Select "Sync to cloud"
     * option from more options of the folder 4) Select Cloud Site and submit
     * sync. 5) Verify the folder is synced. 6) Login to Cloud, load site
     * document library from search 7) Verify Folder is synced as well as the
     * files within the folder 8) Verify there is no Description for both of the
     * files 9) Open the site document library from search 10) Select the
     * folder, Select Edit Properties for File1, set description and save. 11)
     * Verify the description of File1 has been changed and No Description for
     * File2 12) Go back to folder, select "Request to Sync" from more options
     * 13) Login to cloud, open site document library 14) Verify File1's
     * Description has been changed in cloud but not File2's description 15)
     * Verify there is no description for file1 after reverting back to previous
     * version 16) Login to OP, Open the site and verify there is no description
     * for File1 and File2
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7154() throws Exception
    {

        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String folderName = getFolderName(testName) + "-OP";
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";
        String[] fileInfo1 =
                { fileName1, folderName };
        String[] fileInfo2 =
                { fileName2, folderName };

        String propertyDescription = String.valueOf(System.currentTimeMillis());

        try
        {

            // Login as User (Cloud), create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, create a folder

            DocumentLibraryPage documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, "Folder-1");
            // Upload two files within the folder
            documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo1);
            documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2);

            documentLibraryPage = documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(cloudSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
            // Select the folder and select "Sync to cloud" from more options
            AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);

            // Verify the folder is synced.
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, folderName));
            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
            // Verify folder is synced
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), folderName);
            // Select the folder
            documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
            documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, fileName1).render();
            // Verify files within the folder are synced.
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName1, "Verify File1 is displayed");
            Assert.assertEquals(documentLibraryPage.getFiles().get(1).getName(), fileName2, "Verify File2 is displayed");

            // Verify there is no Description for both of the files
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getDescription(), "No Description");
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName2).getDescription(), "No Description");

            ShareUser.logout(hybridDrone);

            // Login to OP
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open the site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
            // Select Edit Properties for File1, set description and save.
            EditDocumentPropertiesPage editDocumentPropertiesPopup = documentLibraryPage.getFileDirectoryInfo(fileName1).selectEditProperties().render();
            editDocumentPropertiesPopup.setDescription(propertyDescription);
            documentLibraryPage = editDocumentPropertiesPopup.selectSave().render();

            // Verify the file description is changed
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getDescription(), propertyDescription);
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName2).getDescription(), "No Description");

            documentLibraryPage = documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();
            documentLibraryPage.getFileDirectoryInfo(folderName).selectRequestSync().render();

            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();

            // Verify File1's Description has been changed in cloud but not
            // File2's description
            Assert.assertTrue(checkIfDescriptionIsUpdated(hybridDrone, fileName1, propertyDescription));
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName2).getDescription(), "No Description");

            // Select File1, revert back to previous version
            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName1).render();
            RevertToVersionPage revertToVersionPage = (RevertToVersionPage) documentDetailsPage.selectRevertToVersion("1.0").render();
            revertToVersionPage.selectMajorVersionChange();
            documentDetailsPage = revertToVersionPage.submit().render();
            documentLibraryPage = documentDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();
            documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();

            documentLibraryPage = documentLibraryPage.renderItem(maxWaitTime, fileName1).render();
            // Verify there is no description for file1 after reverting back to
            // previous version
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getDescription(), "No Description");
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName2).getDescription(), "No Description");

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();

            // Verify the folder description is changed
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getDescription(), "No Description");
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName2).getDescription(), "No Description");

            ShareUser.logout(drone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7155 - Enterprise40x-7155:Request sync. Change some multiple files 1)
     * Create On-Prem user 2) Create a Cloud User 3) Login to Cloud and set up
     * Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7155() throws Exception
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
        ShareUser.logout(drone);
    }

    /**
     * 7155 - Enterprise40x-7155:Request sync. Change some multiple files 1)
     * Login as OP user, Create site and upload two documents 2) Select both
     * files (Select All) and select Sync to cloud from "Selected Items"
     * dropdown 3) Verify both file have been synced (by checking Sync icon) 4)
     * Login to Cloud and verify there is no description for both the files 5)
     * Login to OP, change the Description for both the files 6) Verify the
     * description has been updated for both files 7) Select both files, select
     * "Request Sync" from Selected Items drop down 8) Login to Cloud and verify
     * the description has been updated for both files
     */
    @Test(groups =
            { "CloudSync" })
    public void ALF_7155() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName, hybridDomainPremium);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSiteName = getSiteName(testName) + System.currentTimeMillis() + "-CL";
        String fileName1 = getFileName(testName) + "-1.txt";
        String fileName2 = getFileName(testName) + "-2.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };
        String[] fileInfo2 = { fileName2, DOCLIB };

        String file1Description = "File1 - " + String.valueOf(System.currentTimeMillis());
        String file2Description = "File2 - " + String.valueOf(System.currentTimeMillis());

        try
        {
            // Login as User (Cloud), create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            // Open Document library and upload two files
            ShareUser.uploadFileInFolder(drone, fileInfo1);
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2);

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(hybridDomainPremium);
            desAndAssBean.setSiteName(cloudSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            // Select all files
            documentLibraryPage = documentLibraryPage.getNavigation().selectAll().render();

            // Select "Sync to cloud" from "Selected items" drop down
            AbstractCloudSyncTest.syncAllContentToCloud(drone, desAndAssBean);

            // Verify both documents are synced (By checking Sync icon)
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName1), "Verifying File1 is synced");
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName2), "Verifying File2 is synced");

            // User1 logs out from OP
            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            // Verify the Description of both files are "No Description"
            // (Default)
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getDescription(), "No Description");
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName2).getDescription(), "No Description");
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open Site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            // Edit File1's properties and set Description
            EditDocumentPropertiesPage editDocumentPropertiesPopup = documentLibraryPage.getFileDirectoryInfo(fileName1).selectEditProperties().render();
            editDocumentPropertiesPopup.setDescription(file1Description);
            documentLibraryPage = editDocumentPropertiesPopup.selectSave().render();
            drone.refresh();
            // Edit File2's properties and set Description
            editDocumentPropertiesPopup = documentLibraryPage.getFileDirectoryInfo(fileName2).selectEditProperties().render();
            editDocumentPropertiesPopup.setDescription(file2Description);
            documentLibraryPage = editDocumentPropertiesPopup.selectSave().render();

            // Verify the folder description is changed
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getDescription(), file1Description);
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName2).getDescription(), file2Description);

            // Select both files and select "Request Sync" from "Selected Items"
            // drop down.
            documentLibraryPage = documentLibraryPage.getNavigation().selectAll().render();
            documentLibraryPage = documentLibraryPage.getNavigation().selectRequestSync().render();

            ShareUser.logout(drone);

            // Login as User (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Load site document library from search
            ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            // Verify the Description has been changed in cloud
            Assert.assertTrue(checkIfDescriptionIsUpdated(hybridDrone, fileName1, file1Description));
            Assert.assertTrue(checkIfDescriptionIsUpdated(hybridDrone, fileName2, file2Description));
            ShareUser.logout(hybridDrone);
        } catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    /**
     * 7144 - Enterprise40x-7144:Unsync file using Unsync button from sync info
     * pop up. 1) Create On-Prem user 2) Create a Cloud User 3) Create site on
     * cloud user. 3) Login to On-Premise and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7144() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

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

    @Test(groups =
            { "CloudSync" })
    public void ALF_7144() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");
        String fileName = testName + System.currentTimeMillis() + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.uploadFileInFolder(drone, new String[]
                { fileName, DOCLIB });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

        // Select sync network details from destination and assign page.
        AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

        Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName));

        DocumentLibraryPage docPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);

        FileDirectoryInfo fileDirInfo = docPage.getFileDirectoryInfo(fileName);
        SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo();
        Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());

        syncInfoPage.selectUnsyncRemoveContentFromCloud(true);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        Assert.assertFalse(docLibPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

    /**
     * 7145 - Enterprise40x-7145:Unsync folder using Unsync button from sync
     * info pop up. 1) Create On-Prem user 2) Create a Cloud User 3) Create site
     * on cloud user. 3) Login to On-Premise and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7145() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

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

    @Test(groups =
            { "CloudSync" })
    public void ALF_7145() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");
        String folder = testName + System.currentTimeMillis();

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.createFolder(drone, folder, folder);

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

        DocumentLibraryPage docLibPage = AbstractCloudSyncTest.syncContentToCloud(drone, folder, desAndAssBean);

        FileDirectoryInfo fileDirInfo = docLibPage.getFileDirectoryInfo(folder);

        SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo();
        Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());

        syncInfoPage.selectUnsyncRemoveContentFromCloud(true);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
        docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        Assert.assertFalse(docLibPage.isFileVisible(folder));
        ShareUser.logout(hybridDrone);

    }

    /**
     * 7163 - Enterprise40x-7163:Unsync a child file/folder of parent synced
     * folder. 1) Create On-Prem user 2) Create a Cloud User 3) Create site on
     * cloud user. 3) Login to On-Premise and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7163() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        // Create User (On Premise)
        String[] userInfo = { testUser };
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

    @Test(groups =
            { "CloudSync" })
    public void ALF_7163() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");
        String folder = testName + System.currentTimeMillis();
        String subFolder = testName + System.currentTimeMillis() + "-sub";
        String file = testName + System.currentTimeMillis() + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.createFolder(drone, folder, folder);
        ShareUser.uploadFileInFolder(drone, new String[] { file, folder });

        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, subFolder, subFolder);
        docLibPage = docLibPage.getSiteNav().selectSiteDocumentLibrary().render();

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

        docLibPage = AbstractCloudSyncTest.syncContentToCloud(drone, folder, desAndAssBean);

        DocumentLibraryPage folderPage = docLibPage.selectFolder(folder).render();

        Assert.assertFalse(folderPage.getFileDirectoryInfo(subFolder).isUnSyncFromCloudLinkPresent());
        drone.refresh();
        Assert.assertFalse(folderPage.getFileDirectoryInfo(file).isUnSyncFromCloudLinkPresent());

        FolderDetailsPage folderDetailsPAge = folderPage.getFileDirectoryInfo(subFolder).selectViewFolderDetails();
        SyncInfoPage syncInfoPage = folderDetailsPAge.getSyncInfoPage();

        Assert.assertFalse(syncInfoPage.isUnSyncIconPresentInDetailsPage());

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docLibPage.selectFolder(folder);
        syncInfoPage = ShareUser.openDocumentDetailPage(drone, file).getSyncInfoPage();

        Assert.assertFalse(syncInfoPage.isUnSyncIconPresentInDetailsPage());

        ShareUser.logout(drone);

    }

    /**
     * 7164 - Enterprise40x-7164:Unsync a child file/folder of parent synced
     * folder. 1) Create On-Prem user 2) Create a Cloud User 3) Create site on
     * cloud user. 3) Login to On-Premise and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7164() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");

        // Create User (On Premise)
        String[] userInfo =
                { testUser };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Create User (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo);

        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, new String[]
                { testUser2 });

        // Login as User (Cloud)
        ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User (OP User), create site and sync with cloud.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, testUser, DEFAULT_PASSWORD);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.MANAGER);
        ShareUser.logout(drone);
    }

    @Test(groups =
            { "CloudSync" })
    public void ALF_7164() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName + "-1");
        String folder = testName + System.currentTimeMillis();
        String subFolder = testName + System.currentTimeMillis() + "-sub";
        String file = testName + System.currentTimeMillis() + ".txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.createFolder(drone, folder, folder);
        ShareUser.uploadFileInFolder(drone, new String[]
                { file, folder });
        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, subFolder, subFolder);
        docLibPage = docLibPage.getSiteNav().selectSiteDocumentLibrary().render();
        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(hybridDomainPremium);
        desAndAssBean.setSiteName(siteName);
        desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

        AbstractCloudSyncTest.syncContentToCloud(drone, folder, desAndAssBean);

        ShareUser.logout(drone);
        /** login and test coditions as TestUser2 **/
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentLibraryPage folderPage = docLibPage.selectFolder(folder).render();

        Assert.assertFalse(folderPage.getFileDirectoryInfo(subFolder).isUnSyncFromCloudLinkPresent());
        drone.refresh();
        Assert.assertFalse(folderPage.getFileDirectoryInfo(file).isUnSyncFromCloudLinkPresent());

        FolderDetailsPage folderDetailsPAge = folderPage.getFileDirectoryInfo(subFolder).selectViewFolderDetails();
        SyncInfoPage syncInfoPage = folderDetailsPAge.getSyncInfoPage();

        Assert.assertFalse(syncInfoPage.isUnSyncIconPresentInDetailsPage());

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        docLibPage.selectFolder(folder);
        syncInfoPage = ShareUser.openDocumentDetailPage(drone, file).getSyncInfoPage();

        Assert.assertFalse(syncInfoPage.isUnSyncIconPresentInDetailsPage());

        ShareUser.logout(drone);
    }

    /**
     * 7182 - Enterprise40x-7182:Unsync file by User-2(as Collabrator) to site.
     * 1) Create On-Prem user 2) Create a Cloud User 3) Create site on cloud
     * user. 3) Login to On-Premise and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7182() throws Exception
    {
        String testName = getTestName().toLowerCase();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName);

        // Create User1 (On Premise)
        String[] userInfo =
                { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo);

        // Create User1 (On Premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, testUser2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, testUser2);

        // Login as User (Cloud)
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, testUser2, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as User (OP User)
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, testUser2, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, testUser2, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as User2 (OP User) and join site created by user1.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.COLLABORATOR);
        ShareUser.logout(drone);
    }

    @Test(groups =
            { "CloudSync" })
    public void ALF_7182() throws Exception
    {

        String testName = getTestName().toLowerCase();
        String testUser1 = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String testUser2 = getUserNameForDomain(testName + "-2", hybridDomainPremium);
        String siteName = getSiteName(testName);
        String fileName = testName + System.currentTimeMillis() + ".txt";
        DestinationAndAssigneeBean destAndAssBean = new DestinationAndAssigneeBean();
        destAndAssBean.setNetwork(getUserDomain(testUser2));
        destAndAssBean.setSiteName(siteName);
        // User2 who is a Collabrator to site created by (user1) uploads a file.
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });
        ShareUser.logout(drone);

        // User1 who owns the site sync the file with cloud.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        AbstractCloudSyncTest.syncContentToCloud(drone, fileName, destAndAssBean);
        Assert.assertTrue(checkIfContentIsSynced(drone, fileName));
        ShareUser.logout(drone);

        // User2 logins and check the sync status of (Its own file).
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FileDirectoryInfo fileDirInfo = docLibPage.getFileDirectoryInfo(fileName);
        SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo();
        Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());
        // Do remove content from cloud.
        syncInfoPage.selectUnsyncRemoveContentFromCloud(true);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, testUser2, DEFAULT_PASSWORD);
        docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        Assert.assertFalse(docLibPage.isFileVisible(fileName));
        ShareUser.logout(hybridDrone);
    }

    /**
     * 7184 - Enterprise40x-7184:Unsync file using Unsync button from sync info
     * pop up without deleting the file from cloud. 1) Create On-Prem user 2)
     * Create a Cloud User 3) Create site on cloud user. 3) Login to On-Premise
     * and set up Cloud Sync
     */
    @Test(groups =
            { "DataPrepCloudSync4", "DataPrepCloudSync" })
    public void dataPrep_ALF_7184() throws Exception
    {
        String testName = getTestName().toLowerCase();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
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

    @Test(groups = { "CloudSync" })
    public void ALF_7184() throws Exception
    {
        String testName = getTestName().toLowerCase();
        String testUser = getUserNameForDomain(testName + "-1", hybridDomainPremium);
        String siteName = getSiteName(testName);
        String fileName = testName + System.currentTimeMillis() + ".txt";
        String fileDesc = "This is an assert desc";
        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(getUserDomain(testUser));
        desAndAssBean.setSiteName(siteName);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.uploadFileInFolder(drone, new String[] { fileName, DOCLIB });

        AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

        SyncInfoPage syncInfoPage = docLibPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo();

        Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());

        syncInfoPage.selectUnsyncRemoveContentFromCloud(false);
        drone.refresh();
        DocumentDetailsPage documentDetailsPage = docLibPage.selectFile(fileName).render();
        // Select "In line Edit", modify details
        EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setDescription(fileDesc);
        documentDetailsPage = inlineEditPage.save(contentDetails).render();
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);
        docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
        Assert.assertTrue(docLibPage.isFileVisible(fileName));
        documentDetailsPage = docLibPage.selectFile(fileName).render();
        inlineEditPage = documentDetailsPage.selectInlineEdit().render();
        contentDetails = inlineEditPage.getDetails();
        Assert.assertFalse(fileDesc.equals(contentDetails.getDescription()));
        documentDetailsPage = inlineEditPage.save(contentDetails).render();
        ShareUser.logout(hybridDrone);
    }

}
