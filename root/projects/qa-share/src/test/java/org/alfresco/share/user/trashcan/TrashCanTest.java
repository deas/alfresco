/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.user.trashcan;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractCloudSyncTest;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserProfile;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


/**
 * Class includes: Tests from TrashCan.
 * 
 * @author nshah
 */
@Listeners(FailedTestListener.class)
public class TrashCanTest extends AbstractCloudSyncTest
{

    protected String testUser;
    private String testDomainFree = DOMAIN_FREE;
    private String adminUserFree = ADMIN_USERNAME;
    private String testDomain = DOMAIN_HYBRID;
    private String format = "EEE d MMM YYYY";
    
    /**
     * Class includes: Tests from TestLink in Area: Advanced Search Tests
     * <ul>
     * <li>Test searches using various Properties, content, Folder</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testDomain = DOMAIN_HYBRID;
        testDomainFree = DOMAIN_FREE;
        adminUserFree = ADMIN_USERNAME;
        testName = this.getClass().getSimpleName();
    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_AONE_15084() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(hybridDrone);

        // Login as Enterprise user, create site
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

    }

    @Test(groups = { "HybridSync", "Enterprise42" })
    public void AONE_15084() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String file = testName + System.currentTimeMillis() + ".txt";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);

        AbstractCloudSyncTest.syncContentToCloud(drone, file, desAndAssBean);

        ShareUser.selectContentCheckBox(drone, file);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, file));

        ShareUserProfile.recoverTrashCanItem(drone, file);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, file));

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        docLibPage.isFileVisible(file);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(file).isViewCloudSyncInfoLinkPresent());
              
        Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, file), "ALF-20445: sync is not happening!!");               

        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, cloudSiteName);        

        Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(hybridDrone, file));
        
        Assert.assertTrue(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_AONE_15085() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);

        // Login as Enterprise user, create site
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

    }

    @Test(groups = { "HybridSync", "Enterprise42" })
    public void AONE_15085() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        
        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        
        String folderName = testName + System.currentTimeMillis();
        String file = getFileName(testName)+ System.currentTimeMillis()+".text";
        
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);

        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);
        AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);

        ShareUser.selectContentCheckBox(drone, folderName);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        // Check the folder is removed on cloud
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        DocumentLibraryPage doclib = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertFalse(doclib.isFileVisible(folderName));

        ShareUser.logout(hybridDrone);

        // Check On-Premise
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        ShareUserProfile.recoverTrashCanItem(drone, folderName);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        doclib = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        doclib.isFileVisible(folderName);

        Assert.assertTrue(doclib.getFileDirectoryInfo(folderName).isViewCloudSyncInfoLinkPresent());

        Assert.assertTrue(AbstractCloudSyncTest.checkIfContentIsSynced(drone, file),"ALF-20445: sync is not happening!!");     
        
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        doclib = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertTrue(doclib.isFileVisible(folderName));

        Assert.assertTrue(doclib.getFileDirectoryInfo(folderName).isCloudSynced());
        
        // Open Folder
        doclib = doclib.selectFolder(folderName).render();

        Assert.assertTrue(doclib.isFileVisible(file));
        ShareUser.logout(hybridDrone);

    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_AONE_15086() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, create site, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
       
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = {"HybridSync", "Enterprise42" })
    public void AONE_15086() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        String file = testName + System.currentTimeMillis() + ".txt";

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);
        AbstractCloudSyncTest.syncContentToCloud(drone, file, desAndAssBean);

        ShareUser.selectContentCheckBox(drone, file);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        // Check on Cloud
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

        // Check On Premise
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, file));

        ShareUserProfile.deleteTrashCanItem(drone, file);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, file));

        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(drone);

        // Check On Cloud

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName);

        Assert.assertFalse(docLibPage.isFileVisible(file));

        ShareUser.logout(hybridDrone);

    }

     @Test(groups = { "DataPrepTrashCan", "HybridSync", "Enterprise42" })
    public void dataPrep_TrashCan_AONE_15087() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] { cloudUser };

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, create site, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(hybridDrone);
    }

    @Test(groups = { "HybridSync", "Enterprise42" })
    public void AONE_15087() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);

        String opSiteName = getSiteName(testName) + "-OP";
        String cloudSiteName = getSiteName(testName) + "-CL";
        String file = getFileName(testName)+ System.currentTimeMillis()+".text";
        String folderName = testName + System.currentTimeMillis();

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, opSiteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);  
        ShareUser.uploadFileInFolder(drone, new String[] { file });

        DestinationAndAssigneeBean desAndAssBean = new DestinationAndAssigneeBean();
        desAndAssBean.setNetwork(testDomain);
        desAndAssBean.setSiteName(cloudSiteName);
        AbstractCloudSyncTest.syncContentToCloud(drone, folderName, desAndAssBean);
       
        ShareUser.selectContentCheckBox(drone, folderName);

        ShareUser.deleteSelectedContent(drone);

        ShareUser.logout(drone);

        // Check On Cloud
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        Assert.assertFalse(ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName).isFileVisible(folderName));

        ShareUser.logout(hybridDrone);

        // Check On Premise
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToTrashCan(drone);

        Assert.assertTrue(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        ShareUserProfile.deleteTrashCanItem(drone, folderName);

        Assert.assertFalse(ShareUserProfile.isTrashCanItemPresent(drone, folderName));

        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, opSiteName);

        docLibPage.isFileVisible(folderName);

        ShareUser.logout(drone);

        // Check On Cloud

        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);

        Assert.assertFalse(ShareUser.openSiteDocumentLibraryFromSearch(hybridDrone, cloudSiteName).isFileVisible(folderName));

        ShareUser.logout(hybridDrone);

    }
}
