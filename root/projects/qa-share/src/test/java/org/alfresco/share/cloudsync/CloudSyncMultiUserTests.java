/*
 * C opyright (C) 2005-2013 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.cloudsync;

import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.share.util.AbstractCloudSyncTest;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

/**
 * @author Ranjith Manyam
 * 
 */
@Listeners(FailedTestListener.class)
public class CloudSyncMultiUserTests extends AbstractCloudSyncTest
{
    private static final Logger logger = Logger.getLogger(CloudSyncMultiUserTests.class);
    WebDrone secondDrone;
    
    
    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        secondDrone = getSecondDrone();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        super.tearDown();
        if(secondDrone != null)
        {
            secondDrone.quit();
        }
    }

    /**
     * ALF-7076:Sync one file to same location by two users
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create User1 in Cloud</li>
     * <li>3) Set Up cloud sync with the cloud user for both User1 and User2</li>
     * </ul>
     */
    @Test(groups={"DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync"})
    public void dataPrep_7076() throws Exception
    {
        testName = getTestName();

        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String user2 = getUserNameForDomain(testName + "op-2", DOMAIN_HYBRID);
        String[] userInfo2 = new String[] { user2 };

        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * ALF-7076:Sync one file to same location by two users
     * <ul>
     *     <li>1) Login as User2, open the site document library</li>
     *     <li>2) Verify "Sync to Cloud" option is not displayed</li>
     * </ul>
     */
    // TODO - Is it better to split this test into two (one from Doc Lib and one from Doc Details??)
    @Test(groups = {"EnterpriseOnly", "CloudSync"})
    public void testALF_7076() throws Exception
    {
    	testName = getTestName();

        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String user2 = getUserNameForDomain(testName + "op-2", DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);

        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + System.currentTimeMillis() +"-CL";

        String fileName1 = testName + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = testName + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        try
        {
            // Login as cloudUser (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, fileInfo1).render();
            ShareUser.uploadFileInFolder(drone, fileInfo2).render();
            // Invite User2 to the site as Consumer and log-out the current user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);


            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.login(secondDrone, user2, DEFAULT_PASSWORD);

            SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);
            DestinationAndAssigneePage destinationPage1 = selectSyncToCloudDocLib(drone, fileName1);
            destinationPage1.selectSite(cloudSiteName);


            SiteUtil.openSiteDocumentLibraryURL(secondDrone, opSiteName);
            DestinationAndAssigneePage destinationPage2 = selectSyncToCloudDocLib(secondDrone, fileName1).render();
            destinationPage2.selectSite(cloudSiteName);

            destinationPage1.selectSubmitButtonToSync().render();
            destinationPage2.selectSubmitButtonToSync();
            // TODO - There is some delay in displaying ShareError popup so we needed to wait a bit - Done(need to test)
            SharePopup errorPopup = getShareErrorPopupPage(secondDrone);

            assertTrue(errorPopup.isShareMessageDisplayed(), "Error should be displayed");
            assertEquals(errorPopup.getShareMessage(), "Could not create sync");
            errorPopup.clickOK();
            secondDrone.refresh();

            assertTrue(checkIfContentIsSynced(drone, fileName1));
            assertTrue(checkIfContentIsSynced(secondDrone, fileName1));

            DocumentDetailsPage detailsPage1 = ShareUser.openDocumentDetailPage(drone, fileName2);
            destinationPage1 = selectSyncToCloud(drone).render();
            destinationPage1.selectSite(cloudSiteName);


            DocumentDetailsPage detailsPage2 = ShareUser.openDocumentDetailPage(secondDrone, fileName2);
            destinationPage2 = selectSyncToCloud(secondDrone).render();
            destinationPage2.selectSite(cloudSiteName);

            detailsPage1 = destinationPage1.selectSubmitButtonToSync().render();

            destinationPage2.selectSubmitButtonToSync();

            errorPopup = getShareErrorPopupPage(secondDrone);
            assertEquals(errorPopup.getShareMessage(), "Could not create sync");
            errorPopup.clickOK();

            detailsPage2 = getSharePage(secondDrone).render();

            detailsPage1.getSiteNav().selectSiteDocumentLibrary().render();
            detailsPage2.getSiteNav().selectSiteDocumentLibrary().render();

            assertTrue(checkIfContentIsSynced(drone, fileName1));
            assertTrue(checkIfContentIsSynced(secondDrone, fileName1));

            ShareUser.logout(drone);
            ShareUser.logout(secondDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * ALF-7077:Sync two files to the same folder at the same time
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create User1 in Cloud</li>
     * <li>3) Set Up cloud sync with the cloud user for both User1 and User2</li>
     * </ul>
     */
    @Test(groups={"DataPrepCloudSync3", "DataPrepEnterpriseOnly", "DataPrepCloudSync"})
    public void dataPrep_7077() throws Exception
    {
        testName = getTestName();

        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String user2 = getUserNameForDomain(testName + "op-2", DOMAIN_HYBRID);
        String[] userInfo2 = new String[] { user2 };

        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login to User2, set up the cloud sync
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

    }

    /**
     * ALF-7077:Sync two files to the same folder at the same time
     * <ul>
     *     <li>1) Login as User2, open the site document library</li>
     *     <li>2) Verify "Sync to Cloud" option is not displayed</li>
     * </ul>
     */
    // TODO - Is it better to split this test into two (one from Doc Lib and one from Doc Details??)
    @Test(groups = {"EnterpriseOnly", "CloudSync"})
    public void testALF_7077() throws Exception
    {
        testName = getTestName();

        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String user2 = getUserNameForDomain(testName + "op-2", DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);

        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + System.currentTimeMillis() +"-CL";

        String fileName1 = testName + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = testName + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String fileName3 = testName + "-3.txt";
        String[] fileInfo3 = { fileName3, DOCLIB };

        String fileName4 = testName + "-4.txt";
        String[] fileInfo4 = { fileName4, DOCLIB };

        try
        {
            // Login as cloudUser (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, fileInfo1).render();
            ShareUser.uploadFileInFolder(drone, fileInfo2).render();
            ShareUser.uploadFileInFolder(drone, fileInfo3).render();
            ShareUser.uploadFileInFolder(drone, fileInfo4).render();
            // Invite User2 to the site as Consumer and log-out the current user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);


            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);
            DestinationAndAssigneePage destinationPage1 = selectSyncToCloudDocLib(drone, fileName1);
            destinationPage1.selectSite(cloudSiteName);

            ShareUser.login(secondDrone, user2, DEFAULT_PASSWORD);

            SiteUtil.openSiteDocumentLibraryURL(secondDrone, opSiteName);
            DestinationAndAssigneePage destinationPage2 = selectSyncToCloudDocLib(secondDrone, fileName2).render();
            destinationPage2.selectSite(cloudSiteName);

            destinationPage1.selectSubmitButtonToSync().render();
            destinationPage2.selectSubmitButtonToSync().render();

            assertTrue(checkIfContentIsSynced(drone, fileName1));
            assertTrue(checkIfContentIsSynced(secondDrone, fileName2));

//            SyncInfoPage syncInfoPage1 = navigateToSyncInfoPage(drone, fileName1);
//            SyncInfoPage syncInfoPage2 = navigateToSyncInfoPage(secondDrone, fileName2);
//
//            assertEquals(syncInfoPage1.getCloudSyncLocation(), DOMAIN_HYBRID + ">" + cloudSiteName + ">" + DEFAULT_FOLDER_NAME);
//            assertEquals(syncInfoPage2.getCloudSyncLocation(), DOMAIN_HYBRID + ">" + cloudSiteName + ">" + DEFAULT_FOLDER_NAME);

            DocumentDetailsPage detailsPage1 = ShareUser.openDocumentDetailPage(drone, fileName3);
            destinationPage1 = selectSyncToCloud(drone).render();
            destinationPage1.selectSite(cloudSiteName);


            DocumentDetailsPage detailsPage2 = ShareUser.openDocumentDetailPage(secondDrone, fileName4);
            destinationPage2 = selectSyncToCloud(secondDrone).render();
            destinationPage2.selectSite(cloudSiteName);

            detailsPage1 = destinationPage1.selectSubmitButtonToSync().render();
            detailsPage2 = destinationPage2.selectSubmitButtonToSync().render();

            detailsPage1.getSiteNav().selectSiteDocumentLibrary().render();
            detailsPage2.getSiteNav().selectSiteDocumentLibrary().render();

            assertTrue(checkIfContentIsSynced(drone, fileName3));
            assertTrue(checkIfContentIsSynced(secondDrone, fileName4));

//            syncInfoPage1 = navigateToSyncInfoPage(drone, fileName3);
//            syncInfoPage2 = navigateToSyncInfoPage(secondDrone, fileName4);
//
//            assertEquals(syncInfoPage1.getCloudSyncLocation(), DOMAIN_HYBRID + ">" + cloudSiteName + ">" + DEFAULT_FOLDER_NAME);
//            assertEquals(syncInfoPage2.getCloudSyncLocation(), DOMAIN_HYBRID + ">" + cloudSiteName + ">" + DEFAULT_FOLDER_NAME);

            ShareUser.logout(drone);
            ShareUser.logout(secondDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * // TODO - PreCondition steps in test script are confusing
     * ALF-7078:Sync to Cloud. Target folder is deleted
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create User1 in Cloud</li>
     * <li>3) Set Up cloud sync with the cloud user for both User1 and User2</li>
     * </ul>
     */
    @Test(groups={"DataPrepCloudSyncMultiUser", "DataPrepEnterpriseOnly", "DataPrepCloudSync"})
    public void dataPrep_7078() throws Exception
    {
        testName = getTestName();

        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = testName + "-OP-Site";
        String cloudSiteName = testName +"-CL-Site";

        String fileName1 = testName + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = testName + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo1).render();
        ShareUser.uploadFileInFolder(drone, fileInfo2).render();
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as cloudUser (Cloud) and create a site
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-7078:Sync to Cloud. Target folder is deleted
     * <ul>
     *     <li>1) Login as User2, open the site document library</li>
     *     <li>2) Verify "Sync to Cloud" option is not displayed</li>
     * </ul>
     */
    // TODO - Is it better to split this test into two (one from Doc Lib and one from Doc Details??)
    @Test(groups = {"EnterpriseOnly", "CloudSync"})
    public void testALF_7078() throws Exception
    {
        testName = getTestName();

        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);

        String opSiteName = testName + "-OP-Site";
        String cloudSiteName = testName +"-CL-Site";

        String fileName1 = testName + "-1.txt";
        String fileName2 = testName + "-2.txt";

        String folderName1 = getFolderName(testName + "-1");
        String folderName2 = getFolderName(testName + "-2");

        try
        {
            // Login as cloudUser (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            DocumentLibraryPage cloudDocLibPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSiteName);
            if(!cloudDocLibPage.isFileVisible(folderName1))
            {
                ShareUserSitePage.createFolder(hybridDrone, folderName1, "Folder-1");
            }
            if(!cloudDocLibPage.isFileVisible(folderName2))
            {
                cloudDocLibPage = ShareUserSitePage.createFolder(hybridDrone, folderName2, "Folder-2");
            }


            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);

            DestinationAndAssigneePage destinationPage = selectSyncToCloudDocLib(drone, fileName1);
            destinationPage.selectSite(cloudSiteName);
            destinationPage.selectFolder(folderName1);

            ConfirmDeletePage confirmDeletePage = cloudDocLibPage.getFileDirectoryInfo(folderName1).selectDelete().render();

            cloudDocLibPage = confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();
            // TODO - Why Share Error popup is not working??
//            ShareErrorPopup errorPopup = (ShareErrorPopup) destinationPage.selectSubmitButtonToSync();
//            errorPopup.render();
            destinationPage.selectSubmitButtonToSync();

            SharePopup errorPopup = (SharePopup)getSharePage(drone);


            assertEquals(errorPopup.getShareMessage(), "Could not create sync");
            assertFalse(cloudDocLibPage.isFileVisible(folderName1));

            errorPopup.close();

            DocumentLibraryPage documentLibraryPage = getSharePage(drone).render();

            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileName2).render();

            destinationPage = documentDetailsPage.selectSyncToCloud().render();
            destinationPage.selectSite(cloudSiteName);
            destinationPage.selectFolder(folderName2);

            confirmDeletePage = cloudDocLibPage.getFileDirectoryInfo(folderName2).selectDelete().render();
            cloudDocLibPage = confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();

            // TODO - Refactor this code
            destinationPage.selectSubmitButtonToSync();

            errorPopup.render();

            assertEquals(errorPopup.getShareMessage(), "Could not create sync");
            assertFalse(cloudDocLibPage.isFileVisible(folderName1));

            ShareUser.logout(drone);
            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * // TODO - PreCondition steps in test script are confusing (No need of User2 in OP)
     * ALF-7079:Sync to Cloud. Target folder is moved
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create User1 in Cloud</li>
     * <li>3) Set Up cloud sync with the cloud user for both User1 and User2</li>
     * </ul>
     */
    @Test(groups={"DataPrepCloudSyncMultiUser", "DataPrepEnterpriseOnly", "DataPrepCloudSync"})
    public void dataPrep_7079() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * ALF-7079:Sync to Cloud. Target folder is moved
     * <ul>
     *     <li>1) Login as User2, open the site document library</li>
     *     <li>2) Verify "Sync to Cloud" option is not displayed</li>
     * </ul>
     */
    // TODO - Is it better to split this test into two (one from Doc Lib and one from Doc Details??)
    @Test(groups = {"EnterpriseOnly", "CloudSync"})
    public void testALF_7079() throws Exception
    {
        testName = getTestName();

        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);

        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + System.currentTimeMillis() +"-CL";

        String fileName1 = testName + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = testName + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String folderName1 = getFolderName(testName + "-1");
        String folderName2 = getFolderName(testName + "-2");
        String folderName3 = getFolderName(testName + "-3");

        try
        {
            // Login as cloudUser (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUserSitePage.createFolder(hybridDrone, folderName1, "Folder-1");
            ShareUserSitePage.createFolder(hybridDrone, folderName2, "Folder-2");
            DocumentLibraryPage cloudDocLibPage = ShareUserSitePage.createFolder(hybridDrone, folderName3, "Folder-3");


            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, fileInfo1).render();
            ShareUser.uploadFileInFolder(drone, fileInfo2).render();

            DestinationAndAssigneePage destinationPage = selectSyncToCloudDocLib(drone, fileName1);
            destinationPage.selectSite(cloudSiteName);
            destinationPage.selectFolder(folderName1);

            ShareUserSitePage.copyOrMoveToFolder(hybridDrone, cloudSiteName, folderName1, new String[] {folderName3}, false);
            DocumentLibraryPage opDocLibPage = destinationPage.selectSubmitButtonToSync().render();

            assertTrue(checkIfContentIsSynced(drone, fileName1));
            assertFalse(cloudDocLibPage.isFileVisible(folderName1));

            // Document Details test
            DocumentDetailsPage opDocDetailsPage = opDocLibPage.selectFile(fileName2).render();

            destinationPage = opDocDetailsPage.selectSyncToCloud().render();
            destinationPage.selectSite(cloudSiteName);
            destinationPage.selectFolder(folderName2);

            ShareUserSitePage.copyOrMoveToFolder(hybridDrone, cloudSiteName, folderName2, new String[] {folderName3}, false);
            opDocDetailsPage = destinationPage.selectSubmitButtonToSync().render();

            opDocDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();

            assertTrue(checkIfContentIsSynced(drone, fileName2));
            assertFalse(cloudDocLibPage.isFileVisible(folderName2));

            ShareUser.logout(drone);
            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }


    /**
     * ALF-7080:Sync to Cloud. Target folder is edited
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create User1 in Cloud</li>
     * <li>3) Set Up cloud sync with the cloud user for both User1 and User2</li>
     * </ul>
     */
    @Test(groups={"DataPrepCloudSyncMultiUser", "DataPrepEnterpriseOnly", "DataPrepCloudSync"})
    public void dataPrep_7080() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * ALF-7080:Sync to Cloud. Target folder is edited
     * <ul>
     *     <li>1) Login as User2, open the site document library</li>
     *     <li>2) Verify "Sync to Cloud" option is not displayed</li>
     * </ul>
     */
    // TODO - Is it better to split this test into two (one from Doc Lib and one from Doc Details??)
    @Test(groups = {"EnterpriseOnly", "CloudSync"})
    public void testALF_7080() throws Exception
    {
        testName = getTestName();

        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);

        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + System.currentTimeMillis() +"-CL";

        String fileName1 = testName + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = testName + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String folderName1 = getFolderName(testName + "-1");
        String folderName2 = getFolderName(testName + "-2");

        String newFolderName1 = testName + System.currentTimeMillis() + "-1";
        String newFolderName2 = testName + System.currentTimeMillis() + "-2";

        try
        {
            // Login as cloudUser (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUserSitePage.createFolder(hybridDrone, folderName1, "Folder-1");
            DocumentLibraryPage cloudDocLibPage = ShareUserSitePage.createFolder(hybridDrone, folderName2, "Folder-2");


            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, fileInfo1).render();
            ShareUser.uploadFileInFolder(drone, fileInfo2).render();

            DestinationAndAssigneePage destinationPage = selectSyncToCloudDocLib(drone, fileName1);
            destinationPage.selectSite(cloudSiteName);
            destinationPage.selectFolder(folderName1);

            EditDocumentPropertiesPage editDocumentPropertiesPopup = cloudDocLibPage.getFileDirectoryInfo(folderName1).selectEditProperties().render();
            editDocumentPropertiesPopup.setName(newFolderName1);
            cloudDocLibPage = editDocumentPropertiesPopup.selectSave().render();
            DocumentLibraryPage opDocLibPage = destinationPage.selectSubmitButtonToSync().render();

            assertTrue(checkIfContentIsSynced(drone, fileName1));

            drone.refresh();
            opDocLibPage = getSharePage(drone).render();
            // Document Details test
            DocumentDetailsPage opDocDetailsPage = opDocLibPage.selectFile(fileName2).render();

            destinationPage = opDocDetailsPage.selectSyncToCloud().render();
            destinationPage.selectSite(cloudSiteName);
            destinationPage.selectFolder(folderName2);

            editDocumentPropertiesPopup = cloudDocLibPage.getFileDirectoryInfo(folderName2).selectEditProperties().render();
            editDocumentPropertiesPopup.setName(newFolderName2);

            cloudDocLibPage = editDocumentPropertiesPopup.selectSave().render();

            opDocDetailsPage = destinationPage.selectSubmitButtonToSync().render();

            opDocDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();

            assertTrue(checkIfContentIsSynced(drone, fileName2));

            ShareUser.logout(drone);
            ShareUser.logout(hybridDrone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * ALF-7081:Sync to Cloud. Some files are locked
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create User1 in Cloud</li>
     * <li>3) Set Up cloud sync with the cloud user for both User1 and User2</li>
     * </ul>
     */
    @Test(groups={"DataPrepCloudSyncMultiUser", "DataPrepEnterpriseOnly", "DataPrepCloudSync"})
    public void dataPrep_7081() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String user2 = getUserNameForDomain(testName + "op-2", DOMAIN_HYBRID);
        String[] userInfo2 = new String[] { user2 };

        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);
    }

    /**
     * ALF-7081:Sync to Cloud. Some files are locked
     * <ul>
     *     <li>1) Login as User2, open the site document library</li>
     *     <li>2) Verify "Sync to Cloud" option is not displayed</li>
     * </ul>
     */
    @Test(groups = {"EnterpriseOnly", "CloudSync"})
    public void testALF_7081() throws Exception
    {
        testName = getTestName();

        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String user2 = getUserNameForDomain(testName + "op-2", DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);

        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + System.currentTimeMillis() +"-CL";

        String fileName1 = testName + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = testName + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String fileName3 = testName + "-3.txt";
        String[] fileInfo3 = { fileName3, DOCLIB };

        String fileName4 = testName + "-4.txt";
        String[] fileInfo4 = { fileName4, DOCLIB };

        try
        {
            // Login as cloudUser (Cloud) and create a site
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, fileInfo1).render();
            ShareUser.uploadFileInFolder(drone, fileInfo2).render();
            ShareUser.uploadFileInFolder(drone, fileInfo3).render();
            ShareUser.uploadFileInFolder(drone, fileInfo4).render();
            // Invite User2 to the site as Collaborator and log-out the current user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);


            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.login(secondDrone, user2, DEFAULT_PASSWORD);

            DocumentLibraryPage docLibPage1 = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);
            DocumentLibraryPage docLibPage2 = SiteUtil.openSiteDocumentLibraryURL(secondDrone, opSiteName);

            //DestinationAndAssigneePage destinationPage = docLibPage1.getNavigation().selectAll().getNavigation().selectSyncToCloud().render();
            DocumentLibraryPage documentLibraryPage = docLibPage1.getNavigation().selectAll().render();
            DestinationAndAssigneePage destinationPage = documentLibraryPage.getNavigation().selectSyncToCloud().render();
 
            destinationPage.selectSite(cloudSiteName);
            destinationPage.selectFolder(DEFAULT_FOLDER_NAME);

            docLibPage2 = docLibPage2.getFileDirectoryInfo(fileName2).selectEditOffline().render();
            docLibPage2 = docLibPage2.getFileDirectoryInfo(fileName4).selectEditOffline().render();

            assertEquals(docLibPage2.getFileDirectoryInfo(fileName2).getContentInfo(), "This document is locked by you for offline editing.");
            assertEquals(docLibPage2.getFileDirectoryInfo(fileName4).getContentInfo(), "This document is locked by you for offline editing.");

            docLibPage1 = destinationPage.selectSubmitButtonToSync().render();

            assertTrue(docLibPage1.getFileDirectoryInfo(fileName1).isCloudSynced());

            assertFalse(docLibPage1.getFileDirectoryInfo(fileName2).isCloudSynced());
            assertEquals(docLibPage1.getFileDirectoryInfo(fileName2).getContentInfo(), "This document is locked by " + getUserFullName(user2) + ".");

            assertTrue(docLibPage1.getFileDirectoryInfo(fileName3).isCloudSynced());

            assertFalse(docLibPage1.getFileDirectoryInfo(fileName4).isCloudSynced());
            assertEquals(docLibPage1.getFileDirectoryInfo(fileName4).getContentInfo(), "This document is locked by "+getUserFullName(user2)+".");

            ShareUser.logout(drone);
            ShareUser.logout(secondDrone);

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            DocumentLibraryPage cloudDocLibPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSiteName);
            assertTrue(cloudDocLibPage.isFileVisible(fileName1));
            assertFalse(cloudDocLibPage.isFileVisible(fileName2));
            assertTrue(cloudDocLibPage.isFileVisible(fileName3));
            assertFalse(cloudDocLibPage.isFileVisible(fileName4));

            // TODO - QA-365 : Request Clarification - Test Link : ALF-7081
            // Step 3 of ALF-7081 test case says the documents locked by other user should not be synced but the locked documents synced (Indirectly synced) and can be seen on the cloud.

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }

    /**
     * ALF-7082:Sync to Cloud. Some files are deleted
     * <ul>
     * <li>1) Create User1 and User2 (OP)</li>
     * <li>2) Create User1 in Cloud</li>
     * <li>3) Set Up cloud sync with the cloud user for both User1 and User2</li>
     * </ul>
     */
    @Test(groups={"DataPrepCloudSyncMultiUser", "DataPrepEnterpriseOnly", "DataPrepCloudSync"})
    public void dataPrep_7082() throws Exception
    {
        testName = getTestName();
        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String[] userInfo1 = new String[] { user1 };

        String user2 = getUserNameForDomain(testName + "op-2", DOMAIN_HYBRID);
        String[] userInfo2 = new String[] { user2 };

        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String cloudSiteName = testName + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo2);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_HYBRID, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as cloudUser (Cloud) and create a site
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    /**
     * ALF-7082:Sync to Cloud. Some files are deleted
     * <ul>
     *     <li>1) Login as User2, open the site document library</li>
     *     <li>2) Verify "Sync to Cloud" option is not displayed</li>
     * </ul>
     */
    @Test(groups = {"EnterpriseOnly", "CloudSync"})
    public void testALF_7082() throws Exception
    {
        testName = getTestName();

        String user1 = getUserNameForDomain(testName + "op-1", DOMAIN_HYBRID);
        String user2 = getUserNameForDomain(testName + "op-2", DOMAIN_HYBRID);
        String cloudUser = getUserNameForDomain(testName + "cl", DOMAIN_HYBRID);

        String opSiteName = testName + System.currentTimeMillis() + "-OP";
        String cloudSiteName = testName + "-CL";

        String fileName1 = testName + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String fileName2 = testName + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String fileName3 = testName + "-3.txt";
        String[] fileInfo3 = { fileName3, DOCLIB };

        String fileName4 = testName + "-4.txt";
        String[] fileInfo4 = { fileName4, DOCLIB };

        try
        {
            // Login as User1 (OP) and create a site
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.uploadFileInFolder(drone, fileInfo1).render();
            ShareUser.uploadFileInFolder(drone, fileInfo2).render();
            ShareUser.uploadFileInFolder(drone, fileInfo3).render();
            ShareUser.uploadFileInFolder(drone, fileInfo4).render();
            // Invite User2 to the site as Collaborator and log-out the current user.
            ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, opSiteName, UserRole.MANAGER);
            ShareUser.logout(drone);


            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            ShareUser.login(secondDrone, user2, DEFAULT_PASSWORD);

            DocumentLibraryPage docLibPage1 = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName);
            DocumentLibraryPage docLibPage2 = SiteUtil.openSiteDocumentLibraryURL(secondDrone, opSiteName);

            //DestinationAndAssigneePage destinationPage = docLibPage1.getNavigation().selectAll().getNavigation().selectSyncToCloud().render();
            DocumentLibraryPage documentLibraryPage = docLibPage1.getNavigation().selectAll().render();
            DestinationAndAssigneePage destinationPage = documentLibraryPage.getNavigation().selectSyncToCloud().render();
            destinationPage.selectSite(cloudSiteName);
            destinationPage.selectFolder(DEFAULT_FOLDER_NAME);

            docLibPage2 = docLibPage2.getFileDirectoryInfo(fileName2).selectDelete().render().selectAction(ConfirmDeletePage.Action.Delete).render();
            docLibPage2 = docLibPage2.getFileDirectoryInfo(fileName4).selectDelete().render().selectAction(ConfirmDeletePage.Action.Delete).render();

            assertFalse(docLibPage2.isFileVisible(fileName2));
            assertFalse(docLibPage2.isFileVisible(fileName4));

            destinationPage.selectSubmitButtonToSync();

            SharePopup errorPopup = getShareErrorPopupPage(drone);

            assertTrue(errorPopup.isShareMessageDisplayed(), "Error should be displayed");
            assertEquals(errorPopup.getShareMessage(), "Could not create sync");
            errorPopup.clickOK();

            ShareUser.logout(drone);
            ShareUser.logout(secondDrone);

            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            DocumentLibraryPage cloudDocLibPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSiteName);
            assertFalse(cloudDocLibPage.isFileVisible(fileName1));
            assertFalse(cloudDocLibPage.isFileVisible(fileName2));
            assertFalse(cloudDocLibPage.isFileVisible(fileName3));
            assertFalse(cloudDocLibPage.isFileVisible(fileName4));

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-Cloud", t);
        }
    }
}
