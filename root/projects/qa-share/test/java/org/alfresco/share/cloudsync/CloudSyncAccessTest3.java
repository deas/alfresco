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
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractCloudSyncTest;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Ranjith Manyam
 * 
 */
public class CloudSyncAccessTest3 extends AbstractCloudSyncTest
{
    private static final Logger logger = Logger.getLogger(CloudSyncAccessTest3.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

    }

    /**
     * 7025 - ALF-2104:Sync and lock file with consumer role
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Login as User1, create a site and upload a document</li>
     * <li>3) Invite User2 to join the site as Consumer</li>
     * <li>4) Login as User2, Accept the invitation (User2 will be consumer)</li>
     * <li>5) Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2104() throws Exception
    {

        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String[] userInfo1 = new String[]
        { user1 };
        String user2 = getUserNamePremiumDomain(testName + "-2");
        String[] userInfo2 = new String[]
        { user2 };
        String opSiteName = testName + "-OP";
        String fileName = testName + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo2);
        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login as User1 (OP)
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        // Open Document library, Upload a file
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

        // Invite User2 to the site as Consumer and log-out the current user.
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.CONSUMER);
        ShareUser.logout(drone);

        // Login as User2 (OP User)
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // SetUp Cloud Sync with a Cloud user
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * <ul>
     * <li>1) Login as User2, open the site document library</li>
     * <li>2) Verify "Sync to Cloud" option is not displayed</li>
     * </ul>
     */
    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2104() throws Exception
    {
        String testName = getTestName();
        String user2 = getUserNamePremiumDomain(testName + "-2");
        String opSiteName = testName + "-OP";
        String fileName = testName + ".txt";

        try
        {
            // Login as User2 (OP)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);

            // Open Site Document Library from search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            // Verify "Sync To Cloud" option is not displayed
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * TODO - Need to look at again 7028 - ALF-2106:Sync history is available
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Login as User1, create a site and upload a document</li>
     * <li>3) Invite User2 to join the site as Consumer</li>
     * <li>4) Login as User2, Accept the invitation (User2 will be consumer)</li>
     * <li>5) Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2106() throws Exception
    {
        String testName = getTestName() + "bing";
        ;
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String[] userInfo1 = new String[]
        { user1 };
        String user2 = getUserNamePremiumDomain(testName + "-2");
        String[] userInfo2 = new String[]
        { user2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * 7028 - ALF-2106:Sync history is available
     * <ul>
     * <li>1) Login as Cloud User, Create a site</li>
     * <li>2) Login as User1 (OP), create a site, Invite User2 to join the site
     * as Collaborator</li>
     * <li>3) Login as User2 (OP), Accept the invitation</li>
     * <li>4) Upload a document into the site and Sync to Cloud</li>
     * <li>5) Verify the document is synced</li>
     * <li>6) Verify Sync info in Document details page</li>
     * <li>7) Login as User1, open the site document library</li>
     * <li>8) Verify the document exists in the site and it is synced</li>
     * <li>9) Verify Sync info in Document details page</li>
     * <li>10) Login as Admin, open the site document library</li>
     * <li>11) Verify the document exists in the site and it is synced</li>
     * <li>12) Verify Sync info in Document details page</li>
     * <li>13) Login as User2, open the site document library</li>
     * <li>14) Verify the document exists in the site and it is synced</li>
     * <li>15) Verify Sync info in Document details page</li>
     * </ul>
     */
    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2106() throws Exception
    {
        String testName = getTestName() + "bing";
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String user2 = getUserNamePremiumDomain(testName + "-2");
        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + System.currentTimeMillis() + "-CL";
        String fileName = testName + System.currentTimeMillis() + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Invite User2 to the site as Consumer and log-out the current
            // user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);

            // Login as User2 (OP User)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);

            // Open Site Document Library from search
            ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();
            // Verify "Sync To Cloud" option is displayed
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());
            // Select "Sync to cloud", select Cloud Site and sync the document

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(user1));
            desAndAssBean.setSiteName(cloudSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);
            // Verify the document is synced

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verify Document is synced");
            DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
            // Verify Sync Status
            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains("Sync"));
            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains(", by you"));
            Assert.assertEquals(documentDetailsPage.getLocationInCloud(), getUserDomain(user1) + ">" + cloudSiteName + ">Documents");

            ShareUser.logout(drone);

            // Login as User1 (OP User)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Verify File uploaded by User2 exists in Site Document Library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName, "Verifying the file uploaded by User2 exists in Site");

            // Verify the Document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verify document is synced");

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verify Document is synced");
            // Select the file to go to Document Details page
            documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

            // Verify Sync Status
            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains("Synced"));
            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains(", by " + user2));
            // Assert.assertEquals(syncInfoPage.getCloudSyncLocation(),
            // getUserDomain(user1)+">"+cloudSiteName+">Documents");

            ShareUser.logout(drone);

            // Login as User1 (OP User)
            ShareUser.login(drone, adminUserPrem, DEFAULT_PASSWORD);
            // Open Site Document Library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Verify File uploaded by User2 exists in Site Document Library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName, "Verifying the file uploaded by User2 exists in Site");

            // Verify the Document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verify document is synced");

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verify Document is synced");

            // Select the file to go to Document Details page
            documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
            // Verify Sync Status
            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains("Synced"));
            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains(", by " + user2));

            ShareUser.logout(drone);

            // Login as User2 (OP User)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);
            signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Verify File uploaded by User2 exists in Site Document Library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName, "Verifying the file uploaded by User2 exists in Site");

            // Verify the Document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verify document is synced");

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verify Document is synced");

            // Select the file to go to Document Details page
            documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
            // Verify Sync Status
            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains("Synced"));
            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains(", by you"));
            Assert.assertEquals(documentDetailsPage.getLocationInCloud(), getUserDomain(user1) + ">" + cloudSiteName + ">Documents");

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Login as User1, create a site and upload a document</li>
     * <li>3) Invite User2 to join the site as Consumer</li>
     * <li>4) Login as User2, Accept the invitation (User2 will be consumer)</li>
     * <li>5) Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2107() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName.toLowerCase() + "-1");
        String[] userInfo1 = new String[]
        { user1 };
        String user2 = getUserNamePremiumDomain(testName.toLowerCase() + "-2");
        String[] userInfo2 = new String[]
        { user2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    // TODO - Need latest cloud as the changes made in cloud doesn't reflect in
    // ENT
    /**
     * <ul>
     * <li>1) Login as User1 (Cloud), Create a site</li>
     * <li>2) Login as User1 (OP), Create a site and Upload a doc</li>
     * <li>3) Sync the document into the cloud</li>
     * <li>4) Verify the document is synced</li>
     * <li>5) Invite User2 to the site as Consumer</li>
     * <li>6) Login as User2 (OP), Accept the invitation</li>
     * <li>7) Open Site Document Library from search</li>
     * <li>8) Verify Document uploaded by User1 is displayed in Site Document
     * Library</li>
     * <li>9) Verify the document is synced</li>
     * <li>)</li>
     * </ul>
     */
    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2107() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName.toLowerCase() + "-1");
        String user2 = getUserNamePremiumDomain(testName.toLowerCase() + "-2");
        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + System.currentTimeMillis() + "-OP";
        String fileName = testName + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(testName + testName);

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload a file
            ShareUser.uploadFileInFolder(drone, fileInfo).render();
            // Sync the doc to cloud
            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(user1));
            desAndAssBean.setSiteName(cloudSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);
            // Verify the document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verify document is synced");

            // Invite User2 to the site as Consumer and log-out the current
            // user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.CONSUMER);
            ShareUser.logout(drone);

            // ************Cloud User logs in and change the document
            // properties**************//
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, opSiteName);
            // Verify Document uploaded by User1 is displayed in Site Document
            // Library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName, "Verifying the file uploaded by User1 exists in Site");

            DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(hybridDrone, fileName);
            // Select Inline Edit and change the content and save
            EditTextDocumentPage inlineEditPage = documentDetailsPage.selectInlineEdit().render();
            documentDetailsPage = inlineEditPage.save(contentDetails).render();

            Assert.assertEquals(documentDetailsPage.getDocumentVersion(), "1.1");
            ShareUser.logout(hybridDrone);

            // Login as User2 (OP User)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);

            // Open Site Document Library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            // Verify Document uploaded by User1 is displayed in Site Document
            // Library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName, "Verifying the file uploaded by User1 exists in Site");

            // Verify the document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verify document is synced");

            documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

            Assert.assertTrue(AbstractCloudSyncTest.checkForNewVersion(drone, "1.1"));

            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains("Synced"));
            Assert.assertTrue(documentDetailsPage.getSyncStatus().contains(user1));
       //     Assert.assertEquals(documentDetailsPage.getLocationInCloud(), getUserDomain(user1) + ">" + cloudSiteName + ">Documents");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * ALF:2108- Create a new folder in target selection window as admin
     * <ul>
     * <li>1) Create User1 in OP and Cloud</li>
     * <li>2) Login as User1, create a site and upload a document</li>
     * <li>5) Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2108() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName.toLowerCase() + "-1");
        String[] userInfo1 = new String[]
        { user1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login as User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * ALF:2108- Create a new folder in target selection window as admin
     * <ul>
     * <li>1) Login as Admin (OP), Setup cloud Sync with cloud User (User1)</li>
     * <li>2) Login as User1 (Cloud), Create a site</li>
     * <li>3) Login as User1 (OP), Create a site and Upload a doc</li>
     * <li>4) Sync the document into the cloud</li>
     * <li>5) Verify the document is synced</li>
     * <li>6) Login as Admin, open site document library from search (Site
     * created by User1)</li>
     * <li>7) Upload a new file, select Sync to cloud</li>
     * <li>8) Select network, cloud site and click on Create new Folder</li>
     * <li>9) Create new folder and Sync the document into the cloud folder</li>
     * <li>10) Verify file is synced</li>
     * <li>11) Login to Cloud as User1</li>
     * <li>12) Go to site document library</li>
     * <li>13) Verify Document uploaded by User1 is displayed in Site Document
     * Library</li>
     * <li>14) Verify Folder created by Admin is displayed in Site Document
     * Library</li>
     * <li>14) Select the folder and Verify Document uploaded by Admin is
     * displayed in Site Document Library</li>
     * </ul>
     */
    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2108() throws Exception
    {
        // dataPrep_2108(drone, hybridDrone);
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName.toLowerCase() + "-1");
        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + System.currentTimeMillis() + "-CL";
        String fileName1 = testName + "-1.txt";
        String[] fileInfo1 =
        { fileName1, DOCLIB };
        String fileName2 = testName + "-2.txt";
        String[] fileInfo2 =
        { fileName2, DOCLIB };
        String cloudFolder = getFolderName(testName + "-CL");

        try
        {
            // Login as Admin, set up the cloud sync
            ShareUser.login(drone, adminUserPrem, DEFAULT_PASSWORD);
            signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
            ShareUser.logout(drone);

            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload a file
            ShareUser.uploadFileInFolder(drone, fileInfo1).render();
            // Sync the doc to cloud
            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(user1));
            desAndAssBean.setSiteName(cloudSiteName);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
            AbstractCloudSyncTest.syncContentToCloud(drone, fileName1, desAndAssBean);

            // Verify the document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName1), "Verify document is synced");

            ShareUser.logout(drone);

            // Login as Admin (OP User)
            ShareUser.login(drone, adminUserPrem, DEFAULT_PASSWORD);

            // Open Site Document Library from search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            // Verify Document uploaded by User1 is displayed in Site Document
            // Library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName1, "Verifying the file uploaded by User1 exists in Site");

            // Verify the document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verify document is synced");

            // Upload a new file
            ShareUser.uploadFileInFolder(drone, fileInfo2).render();

            // Sync the doc to cloud, select network and select cloud site
            desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(user1));
            desAndAssBean.setSiteName(cloudSiteName);

            AbstractCloudSyncTest.createNewFolderAndSyncContent(drone, fileName2, desAndAssBean, cloudFolder);

            // Verify file is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName2), "Verify File is synced");

            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

            // Open Site Document Library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            // Verify Document uploaded by User1 is displayed in Site Document
            // Library
            Assert.assertEquals(documentLibraryPage.getFiles().get(1).getName(), fileName1, "Verifying the file uploaded by User1 exists in Site");
            // Verify Folder created by Admin is displayed in Site Document
            // Library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), cloudFolder, "Verifying the Folder created by Admin exists in Site");

            // Select the folder and Verify Document uploaded by Admin is
            // displayed in Site Document Library
            documentLibraryPage = documentLibraryPage.selectFolder(cloudFolder).render();
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName2, "Verifying the file uploaded by Admin exists in Site");
            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * 7030 - ALF-2109:Create a new folder in target selection window as site
     * manager
     * <ul>
     * <li>1) Create User1 in OP and Cloud</li>
     * <li>2) Login as User1, create a site and upload a document</li>
     * <li>5) Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2109() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName.toLowerCase() + "-1");
        String[] userInfo1 = new String[]
        { user1 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login as User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * 7030 - ALF-2109:Create a new folder in target selection window as site
     * manager
     * <ul>
     * <li>1) Login as User1 (Cloud), Create a site</li>
     * <li>2) Login as User1 (OP), Create a site and Upload a doc</li>
     * <li>3) Select "Sync to Cloud", select network and site and select Create
     * new folder</li>
     * <li>4) Create new folder and verify created folder is displayed in the
     * tree</li>
     * <li>5) Select submit Sync button and verify document is synced</li>
     * <li>6) Login to Cloud and open Cloud site document library</li>
     * <li>7) Verify create folder exists</li>
     * <li>8) Select the folder and verify Synced document exists</li>
     * </ul>
     */
    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2109() throws Exception
    {
        // dataPrep_2109(drone, hybridDrone);
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName.toLowerCase() + "-1");
        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + System.currentTimeMillis() + "-CL";
        String fileName1 = testName + "-1.txt";
        String[] fileInfo1 =
        { fileName1, DOCLIB };
        String cloudFolder = getFolderName(testName + "-CL");

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload a file
            ShareUser.uploadFileInFolder(drone, fileInfo1).render();
            // Sync the doc to cloud

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(user1));
            desAndAssBean.setSiteName(cloudSiteName);

            AbstractCloudSyncTest.createNewFolderAndSyncContent(drone, fileName1, desAndAssBean, cloudFolder);

            // Verify the document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName1), "Verify document is synced");

            ShareUser.logout(drone);

            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

            // Open Site Document Library from search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

            // Verify Folder created by OP user is displayed in Site Document
            // Library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), cloudFolder, "Verifying the Folder created by Admin exists in Site");

            // Select the folder and Verify Document uploaded by OP user is
            // displayed in Site Document Library
            documentLibraryPage = documentLibraryPage.selectFolder(cloudFolder).render();
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName1, "Verifying the file uploaded by Admin exists in Site");
            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Login as User1, create a site and upload a document</li>
     * <li>3) Login as User2, Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2110() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName.toLowerCase() + "-1");
        String[] userInfo1 = new String[]
        { user1 };
        String user2 = getUserNamePremiumDomain(testName.toLowerCase() + "-2");
        String[] userInfo2 = new String[]
        { user2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * <ul>
     * <li>1) Login as Cloud User, Create a site</li>
     * <li>2) Login as User1 (OP), create a site, Invite User2 to join the site
     * as Collaborator</li>
     * <li>3) Login as User2 (OP), Accept the invitation</li>
     * <li>4) Upload a document into the site and Sync to Cloud</li>
     * <li>5) Verify the document is synced</li>
     * <li>6) Verify Sync info in Document details page</li>
     * <li>7) Login as User1, open the site document library</li>
     * <li>8) Verify the document exists in the site and it is synced</li>
     * <li>9) Verify Sync info in Document details page</li>
     * <li>10) Login as Admin, open the site document library</li>
     * <li>11) Verify the document exists in the site and it is synced</li>
     * <li>12) Verify Sync info in Document details page</li>
     * <li>13) Login as User2, open the site document library</li>
     * <li>14) Verify the document exists in the site and it is synced</li>
     * <li>15) Verify Sync info in Document details page</li>
     * </ul>
     */

    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2110() throws Exception
    {
        // dataPrep_2110(drone, hybridDrone);
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName.toLowerCase() + "-1");
        String user2 = getUserNamePremiumDomain(testName.toLowerCase() + "-2");
        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + System.currentTimeMillis() + "-CL";
        String fileName = testName + System.currentTimeMillis() + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };
        String cloudFolder = getFolderName(testName + "-CL");

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            // Invite User2 to the site as Consumer and log-out the current
            // user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);

            // Login as User2 (OP User)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);

            // Open Site Document Library from search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            ShareUser.uploadFileInFolder(drone, fileInfo).render();
            // Select "Sync to cloud", select Cloud Site and sync the document

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(user1));
            desAndAssBean.setSiteName(cloudSiteName);

            AbstractCloudSyncTest.createNewFolderAndSyncContent(drone, fileName, desAndAssBean, cloudFolder);

            // Verify the document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verify document is synced");

            ShareUser.logout(drone);

            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            // Open Site Document Library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);
            // Verify File uploaded by User2 exists in Site Document Library
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), cloudFolder, "Verifying the folder created by User2 exists in Site");

            documentLibraryPage = documentLibraryPage.selectFolder(cloudFolder).render();
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName, "Verifying the file uploaded by User2 exists in Site");
            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Login as User1, create a site and upload a document</li>
     * <li>3) Login as User2, Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2111() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName.toLowerCase() + "-1");
        String[] userInfo1 = new String[]
        { user1 };
        String user2 = getUserNamePremiumDomain(testName.toLowerCase() + "-2");
        String[] userInfo2 = new String[]
        { user2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, userInfo1);

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * <ul>
     * <li>1) Login as Cloud User, Create a site</li>
     * <li>2) Login as User1 (OP), create a site, upload a document, Invite
     * User2 to join the site as Consumer</li>
     * <li>3) Login as User2 (OP), Accept the invitation</li>
     * <li>4) Verify "Sync to Cloud" option is not displayed</li>
     * </ul>
     */

    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2111() throws Exception
    {
        // dataPrep_2111(drone, hybridDrone);
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName.toLowerCase() + "-1");
        String user2 = getUserNamePremiumDomain(testName.toLowerCase() + "-2");
        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String fileName = testName + System.currentTimeMillis() + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };

        try
        {
            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site and upload a file
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            ShareUser.uploadFileInFolder(drone, fileInfo).render();

            // Invite User2 to the site as Consumer and log-out the current
            // user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.CONSUMER);
            ShareUser.logout(drone);

            // Login as User2 (OP User)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);

            // Open Site Document Library from search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            // Verify "Sync To Cloud" option is displayed
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isSyncToCloudLinkPresent());

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * ALF-2113:Create a new folder in target selection window. Write access at
     * target selection is removed for user
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Login as User1, create a site and upload a document</li>
     * <li>3) Login as User2, Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2113() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "-1", "network1.test");
        String[] userInfo1 = new String[]
        { user1 };
        String user2 = getUserNameForDomain(testName + "-2", "network2.test");
        String[] userInfo2 = new String[]
        { user2 };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, userInfo2);

        // Upgrade free domain user (Cloud)
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, getUserDomain(user1), "1000");
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, getUserDomain(user2), "1000");

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user2, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * ALF-2113:Create a new folder in target selection window. Write access at
     * target selection is removed for user
     * <ul>
     * <li>1) Login as Cloud User, Create a site</li>
     * <li>2) Login as User1 (OP), create a site, upload a document, Invite
     * User2 to join the site as Consumer</li>
     * <li>3) Login as User2 (OP), Accept the invitation</li>
     * <li>4) Verify "Sync to Cloud" option is not displayed</li>
     * </ul>
     */

    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2113() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "-1", "network1.test");
        String user2 = getUserNameForDomain(testName + "-2", "network2.test");
        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSite1 = testName + System.currentTimeMillis() + "-CL1";
        String cloudSite2 = testName + System.currentTimeMillis() + "-CL2";
        String fileName = testName + System.currentTimeMillis() + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };

        try
        {
            // Login as User1 (Cloud) and create a site
            ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSite1, SITE_VISIBILITY_PUBLIC);
            // Invite User2 to the site as Collaborator
            CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, user1, user2, getSiteShortname(cloudSite1), "SiteConsumer", "");

            ShareUser.logout(hybridDrone);

            // Login as User2 (Cloud) and create a site
            ShareUser.login(hybridDrone, user2, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSite2, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);
            // Create Site and upload a file
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

            DestinationAndAssigneePage destinationAndAssigneePage = documentLibraryPage.getFileDirectoryInfo(fileName).selectSyncToCloud().render();

            destinationAndAssigneePage.selectNetwork(getUserDomain(user1));

            Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSite1), "Verifying Site is not displayed");
            Assert.assertTrue(destinationAndAssigneePage.isSyncPermitted(DEFAULT_FOLDER_NAME), "Verifying Folder is not displayed");
            
            

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * 7034 - ALF-2114:Sync a non-empty folder with chosen sync sub folders
     * option. Some sub folders are inaccessible
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create a Cloud User (cloudUser)</li>
     * <li>3) Login as User2, Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2114() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String[] userInfo1 = new String[]
        { user1 };

        String user2 = getUserNamePremiumDomain(testName + "-2");
        String[] userInfo2 = new String[]
        { user2 };

        String cloudUser = getUserNamePremiumDomain(testName + "-1");
        String[] cloudUserInfo = new String[]
        { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, cloudUserInfo);

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * 7034 - ALF-2114:Sync a non-empty folder with chosen sync sub folders
     * option. Some sub folders are inaccessible
     * <ul>
     * <li>1) Login as Cloud User, Create a site</li>
     * <li>2) Login as User1 (OP), create a site, Create a folder and 3 Sub
     * folders within that folder</li>
     * <li>3) Invite User2 to join the site as Consumer</li>
     * <li>4) Login as User2, accept the invitation</li>
     * <li>5) Login as User1, open the site, remove permissions on SubFolder1
     * for User2 (Consumer)</li>
     * <li>6) Login as User2, Sync the parent folder on to cloud</li>
     * <li>7) Verify Folder is Synced</li>
     * <li>8) Login to Cloud, open the site, Verify Parent folder exists</li>
     * <li>9) Click on the parent folder, verify SubFolder2, SubFolder3 exists</li>
     * <li>9) Verify SubFolder1 doesn't exists</li>
     * </ul>
     */

    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2114() throws Exception
    {
        // dataPrep_2114(drone, hybridDrone);
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String user2 = getUserNamePremiumDomain(testName + "-2");
        String cloudUser = getUserNamePremiumDomain(testName + "-1");
        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSite = testName + System.currentTimeMillis() + "-CL1";
        String folderName = getFolderName(testName);
        String subFolderName1 = "Sub" + getFolderName(testName) + "-1";
        String subFolderName2 = "Sub" + getFolderName(testName) + "-2";
        String subFolderName3 = "Sub" + getFolderName(testName) + "-3";

        try
        {
            // Login as cloudUser (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            DocumentLibraryPage documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, folderName).render();

            // Create a folder in Document Library
            documentLibraryPage.selectFolder(folderName).render();
            // Create 3 Sub Folders
            ShareUserSitePage.createFolder(drone, subFolderName1, subFolderName1).render();
            ShareUserSitePage.createFolder(drone, subFolderName2, subFolderName2).render();
            ShareUserSitePage.createFolder(drone, subFolderName3, subFolderName3).render();

            // Invite User2 to the site as Consumer and log-out the current
            // user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);

            // Login as User1 (OP User)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open Site Document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Remove access to SubFolder-3 to User2
            documentLibraryPage.selectFolder(folderName).render();
            ShareUserMembers.managePermissionsOnContent(drone, user2, subFolderName1, UserRole.CONSUMER, false);
            ShareUser.logout(drone);

            // Login as User2 (OP)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);
            // Open Document Library from Search
            ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Select "Sync to Cloud" from more options of the Folder
            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(cloudUser));
            desAndAssBean.setSiteName(cloudSite);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);
            AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);
            // Verify Document is synced
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, folderName), "Verifying Folder is synced.");
            ShareUser.logout(drone);

            // Login as cloudUser (OP)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Open Site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite);
            // Verify parent folder exists
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), folderName, "Verifying Parent Folder name exists in Document Library");
            // Select the parent folder and verify SubFolder2 and SubFolder3
            // exists
            documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), subFolderName2, "Verifying SubFolder2 exists in Document Library");
            Assert.assertEquals(documentLibraryPage.getFiles().get(1).getName(), subFolderName3, "Verifying SubFolder3 exists in Document Library");
            // Verify SubFolder1 doesn't exists
            Assert.assertFalse(documentLibraryPage.isFileVisible(subFolderName1), "Verify SubFolder1 is not displayed");
            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * 7035 - ALF-2115:Sync details are available for all users from details
     * page
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create a Cloud User (cloudUser)</li>
     * <li>3) Login as User1, Set Up cloud sync with a cloud user</li>
     * <li>4) Login as User2, Set Up cloud sync with a cloud user</li>
     * <li>5) Login as User3, Set Up cloud sync with a cloud user</li>
     * <li>6) Login as User4, Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2115() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String[] userInfo1 = new String[]
        { user1 };

        String user2 = getUserNamePremiumDomain(testName + "-2");
        String[] userInfo2 = new String[]
        { user2 };

        String user3 = getUserNamePremiumDomain(testName + "-3");
        String[] userInfo3 = new String[]
        { user3 };

        String user4 = getUserNamePremiumDomain(testName + "-4");
        String[] userInfo4 = new String[]
        { user4 };

        String cloudUser = getUserNamePremiumDomain(testName + "-1");
        String[] cloudUserInfo = new String[]
        { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo2);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo3);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo4);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, cloudUserInfo);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login to User3, set up the cloud sync
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login to User4, set up the cloud sync
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * 7035 - ALF-2115:Sync details are available for all users from details
     * page
     * <ul>
     * <li>1) Login as Admin, SetUp Cloud Sync with cloud user</li>
     * <li>2) Login as Cloud User, Create a site</li>
     * <li>3) Login as User1 (OP), create a site, Upload a document</li>
     * <li>4) Invite User2 to the site as COLLABORATOR, User3 as CONTRIBUTOR,
     * User4 as CONSUMER</li>
     * <li>5) Login as User2, accept the invitation</li>
     * <li>6) Login as User3, accept the invitation</li>
     * <li>7) Login as User4, accept the invitation</li>
     * <li>8) Login as User1, sync the document into cloud</li>
     * <li>9) Verify document is synced and Sync info is displayed</li>
     * <li>10) Login as User2, verify document is synced and Sync info has been
     * displayed</li>
     * <li>11) Login as User3, verify document is synced and Sync info has been
     * displayed</li>
     * <li>12) Login as User4, verify document is synced and Sync info has been
     * displayed</li>
     * <li>13) Login as admin, verify document is synced and Sync info has been
     * displayed</li>
     * <li>14) Login to Cloud and verify synced document is displayed</li>
     * </ul>
     */

    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2115() throws Exception
    {
        // dataPrep_2115(drone, hybridDrone);
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String user2 = getUserNamePremiumDomain(testName + "-2");
        String user3 = getUserNamePremiumDomain(testName + "-3");
        String user4 = getUserNamePremiumDomain(testName + "-4");

        String cloudUser = getUserNamePremiumDomain(testName + "-1");

        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSite = testName + System.currentTimeMillis() + "-CL1";

        String fileName = testName + System.currentTimeMillis() + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };

        try
        {

            // Login to Admin, set up the cloud sync
            ShareUser.login(drone, adminUserPrem, DEFAULT_PASSWORD);
            signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.logout(drone);

            // Login as cloudUser (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, fileInfo).render();

            // Invite User2 to the site as COLLABORATOR, User3 as CONTRIBUTOR,
            // User4 as CONSUMER

            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);

            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user3, opSiteName, UserRole.CONTRIBUTOR);
            ShareUser.logout(drone);
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user4, opSiteName, UserRole.CONSUMER);
            ShareUser.logout(drone);

            // Login as User1 (OP User)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Open Site Document library from search
            ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Sync the file

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(cloudUser));
            desAndAssBean.setSiteName(cloudSite);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verifying if the document is Synced");
            
            DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

            logger.info("------User1------");
            Assert.assertEquals(detailsPage.getSyncStatus(), "Synced just now, by you", "Verifying Sync Status");
            Assert.assertEquals(detailsPage.getLocationInCloud(), getUserDomain(cloudUser) + ">" + cloudSite + ">" + DEFAULT_FOLDER_NAME,
                "Verifying Sync Location");

            ShareUser.logout(drone);

            // Login as User2 (OP)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);
            // Open Document Library from Search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Verify document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying Folder is synced.");
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verifying if the document is Synced");

            detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);


            logger.info("------User2------");
            Assert.assertTrue(detailsPage.getSyncStatus().contains("Synced "), "Verifying Sync Status contains \"Synced\"");
            Assert.assertTrue(detailsPage.getSyncStatus().contains(user1), "Verifying Sync Status contains" + user1);
            Assert.assertEquals(detailsPage.getLocationInCloud(), getUserDomain(cloudUser) + ">" + cloudSite + ">" + DEFAULT_FOLDER_NAME,
                "Verifying Sync Location");

            ShareUser.logout(drone);

            // Login as User3 (OP)
            ShareUser.login(drone, user3, DEFAULT_PASSWORD);
            // Open Document Library from Search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Verify document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying Folder is synced.");
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verifying if the document is Synced");

            detailsPage  = ShareUser.openDocumentDetailPage(drone, fileName);

            logger.info("------User3------");
            Assert.assertTrue(detailsPage.getSyncStatus().contains("Synced "), "Verifying Sync Status contains \"Synced\"");
            Assert.assertTrue(detailsPage.getSyncStatus().contains(user1), "Verifying Sync Status contains" + user1);
            Assert.assertEquals(detailsPage.getLocationInCloud(), getUserDomain(cloudUser) + ">" + cloudSite + ">" + DEFAULT_FOLDER_NAME,
                "Verifying Sync Location");

            ShareUser.logout(drone);

            // Login as User4 (OP)
            ShareUser.login(drone, user4, DEFAULT_PASSWORD);

            
            // Open Document Library from Search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            // Verify document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying Folder is synced.");
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verifying if the document is Synced");

            detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

            logger.info("------User4------");
            Assert.assertTrue(detailsPage.getSyncStatus().contains("Synced "), "Verifying Sync Status contains \"Synced\"");
            Assert.assertTrue(detailsPage.getSyncStatus().contains(user1), "Verifying Sync Status contains" + user1);
            Assert.assertEquals(detailsPage.getLocationInCloud(), getUserDomain(cloudUser) + ">" + cloudSite + ">" + DEFAULT_FOLDER_NAME,
                "Verifying Sync Location");

            ShareUser.logout(drone);

            // Login as Admin (OP)
            ShareUser.login(drone, adminUserPrem, DEFAULT_PASSWORD);

            // Open Document Library from Search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            // Verify document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying Folder is synced.");
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verifying if the document is Synced");

            detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

            logger.info("------Admin------");
            Assert.assertTrue(detailsPage.getSyncStatus().contains("Synced "), "Verifying Sync Status contains \"Synced\"");
            Assert.assertTrue(detailsPage.getSyncStatus().contains(user1), "Verifying Sync Status contains" + user1);
            Assert.assertEquals(detailsPage.getLocationInCloud(), getUserDomain(cloudUser) + ">" + cloudSite + ">" + DEFAULT_FOLDER_NAME,
                "Verifying Sync Location");

            ShareUser.logout(drone);

            // Login as cloudUser (OP)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Open Site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite);
            // Verify parent folder exists
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName, "Verifying Synced file exists in Cloud site's Document Library");
            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * 7036 - ALF-2116:UnSync and remove a file from Cloud as owner
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create a Cloud User (cloudUser)</li>
     * <li>3) Login as User2, Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2116() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String[] userInfo1 = new String[]
        { user1 };

        String user2 = getUserNamePremiumDomain(testName + "-2");
        String[] userInfo2 = new String[]
        { user2 };

        String cloudUser = getUserNamePremiumDomain(testName + "-1");
        String[] cloudUserInfo = new String[]
        { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, cloudUserInfo);

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * 7036 - ALF-2116:UnSync and remove a file from Cloud as owner
     * <ul>
     * <li>1) Login as Cloud User, Create a site</li>
     * <li>2) Login as User1 (OP), create a site</li>
     * <li>3) Invite User2 to join the site as Collaborator</li>
     * <li>4) Login as User2, accept the invitation</li>
     * <li>5) User2 uploads a document and select "Sync to Cloud" from more
     * options</li>
     * <li>6) Select destination and sync</li>
     * <li>7) Verify File is Synced</li>
     * <li>8) Verify Sync info</li>
     * <li>9) Login to Cloud and verify document is there in cloud location</li>
     * <li>10) Login as user2, unsync and remove content from cloud</li>
     * <li>11) Verify sync icon doesn't exists for the file</li>
     * <li>12) Login to Cloud and verify the document doesn't exists in cloud
     * location</li>
     * </ul>
     */

    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2116() throws Exception
    {
        // dataPrep_2116(drone, hybridDrone);
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String user2 = getUserNamePremiumDomain(testName + "-2");
        String cloudUser = getUserNamePremiumDomain(testName + "-1");
        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSite = testName + System.currentTimeMillis() + "-CL";
        String fileName = testName + System.currentTimeMillis() + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };

        try
        {
            // Login as cloudUser (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

            // Invite User2 to the site as Consumer and then log-out the current
            // user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);

            // Login as User2 (OP User)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);

            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            ShareUser.uploadFileInFolder(drone, fileInfo).render();

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(cloudUser));
            desAndAssBean.setSiteName(cloudSite);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

            // Verify Document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying Sync icon is displayed.");
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verifying Sync info is displayed");

            ShareUser.logout(drone);

            // Login as cloudUser (OP)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Open Site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite);
            // Verify parent folder exists
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName, "Verifying Parent Folder name exists in Document Library");
            ShareUser.logout(hybridDrone);

            // Login as User2 (OP User)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);
            signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
            // Open site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);
            // Select UnSync and select Remove content from Cloud checkbox
            documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectUnSyncAndRemoveContentFromCloud(true).render();
            // Verify CloudSync icon is not displayed
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying Cloud Sync icon is not be displayed");
            ShareUser.logout(drone);

            // Login as cloudUser (OP)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Open Site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite);
            // Verify parent folder exists
            Assert.assertFalse(documentLibraryPage.isFileVisible(fileName), "Verifying File doesn't exists anymore in Cloud Site");
            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * 7038 - ALF-2117:UnSync and remove the file from Cloud without access to
     * the file in Cloud
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create a Cloud User (cloudUser)</li>
     * <li>3) Login as User1, Set Up cloud sync with a cloud user</li>
     * </ul>
     */
    @Test(groups =
    { "DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync" })
    public void dataPrep_2117() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String[] userInfo1 = new String[]
        { user1 };

        String user2 = getUserNamePremiumDomain(testName + "-2");
        String[] userInfo2 = new String[]
        { user2 };

        String cloudUser = getUserNamePremiumDomain(testName + "-1");
        String[] cloudUserInfo = new String[]
        { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, adminUserPrem, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, adminUserPrem, cloudUserInfo);

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * 7038 - ALF-2117:Unsync and remove the file from Cloud without access to
     * the file in Cloud
     * <ul>
     * <li>1) Login as Cloud User, Create a site</li>
     * <li>2) Login as User1 (OP), create a site</li>
     * <li>3) Invite User2 to join the site as Collaborator</li>
     * <li>4) Login as User2, accept the invitation</li>
     * <li>5) User2 uploads a document and select "Sync to Cloud" from more
     * options</li>
     * <li>6) Select destination and sync</li>
     * <li>7) Verify File is Synced</li>
     * <li>8) Verify Sync info</li>
     * <li>9) Login to Cloud and verify document is there in cloud location</li>
     * <li>10) Login as user2, unsync and remove content from cloud</li>
     * <li>11) Verify sync icon doesn't exists for the file</li>
     * <li>12) Login to Cloud and verify the document doesn't exists in cloud
     * location</li>
     * </ul>
     */

    @Test(groups =
    { "EnterpriseOnly", "CloudSync" })
    public void testALF_2117() throws Exception
    {
        // dataPrep_2117(drone, hybridDrone);
        String testName = getTestName();
        String user1 = getUserNamePremiumDomain(testName + "-1");
        String user2 = getUserNamePremiumDomain(testName + "-2");
        String cloudUser = getUserNamePremiumDomain(testName + "-1");
        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSite = testName + System.currentTimeMillis() + "-CL";
        String fileName = testName + System.currentTimeMillis() + ".txt";
        String[] fileInfo =
        { fileName, DOCLIB };

        try
        {
            // Login as cloudUser (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, fileInfo).render();

            DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
            desAndAssBean.setNetwork(getUserDomain(cloudUser));
            desAndAssBean.setSiteName(cloudSite);
            desAndAssBean.setSyncToPath(DEFAULT_FOLDER_NAME);

            AbstractCloudSyncTest.syncContentToCloud(drone, fileName, desAndAssBean);

            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verifying Sync info is displayed");

            // Invite User2 to the site as Consumer and then log-out the current
            // user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.MANAGER);
            ShareUser.logout(drone);

            // Login as cloudUser (OP)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Open Site document library from search
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite);
            // Verify parent folder exists
            Assert.assertEquals(documentLibraryPage.getFiles().get(0).getName(), fileName, "Verifying Synced file exists in Document Library");
            ShareUser.logout(hybridDrone);

            // Login as User2 (OP User)
            ShareUser.login(drone, user2, DEFAULT_PASSWORD);
            signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

            // Verify Document is synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying Sync icon is displayed.");
            Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, fileName), "Verifying Sync info is displayed");

            documentLibraryPage.render();

            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isUnSyncFromCloudLinkPresent(),
                "Verifying \"UnSync from Cloud\" link is not present");

            documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectUnSyncAndRemoveContentFromCloud(true).render();

            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isCloudSynced(), "Verifying Sync icon is NOT displayed.");

            ShareUser.logout(drone);

            // Login as cloudUser (OP)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Open Site document library from search
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSite);
            // Verify the document doesn't exists in cloud location
            Assert.assertFalse(documentLibraryPage.isFileVisible(fileName), "Verifying File has NOT been deleted from Cloud");
            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }
}
