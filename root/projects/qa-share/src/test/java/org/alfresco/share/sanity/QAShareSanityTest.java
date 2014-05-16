package org.alfresco.share.sanity;

/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.WebDroneType;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.api.PublicAPIRestClient;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for shareuser util methods and internal rest apis implemented in Utils > api
 * 
 * @author Meenal Bhave
 */
@Listeners(FailedTestListener.class)
public class QAShareSanityTest extends AbstractWorkflow
{
    private static Log logger = LogFactory.getLog(QAShareSanityTest.class);

    // If default user is not yet created: Set to ADMIN_USERNAME
    protected String testUser;

    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        super.setupCustomDrone(WebDroneType.DownLoadDrone);
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    // Internal API SanityTests
    /**
     * Happy Path
     * @throws Exception 
     */
    @Test(groups = { "Sanity", "AlfrescoOne" })
    public void apiTests_1() throws Exception
    {
        
        Assert.assertNotNull(drone);
        Assert.assertTrue(drone.isReady());
        Assert.assertFalse(isAlfrescoVersionCloud(drone));
        
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis() + "1");
        String[] testUserInfo = new String[] { testUser };

        String testUser2 = getUserNameFreeDomain(testName + System.currentTimeMillis() + "2");
        String[] testUserInfo2 = new String[] { testUser2 };

        String testUser3 = getUserNamePremiumDomain(testName + System.currentTimeMillis() + "3");
        String[] testUserInfo3 = new String[] { testUser3 };

        String siteName = getSiteName(testName + System.currentTimeMillis());
        String siteNameModerated = siteName + "moderated";
        String siteNamePrivate = siteName + "private";

        String folderName = testName;
        String[] fileInfo = new String[] { testName };

        HttpResponse response;
        Boolean result;
        /** Test Steps */

        Assert.assertTrue(CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo));
        Assert.assertTrue(CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2));
        Assert.assertTrue(CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo3));

        if (isAlfrescoVersionCloud(drone))
        {
            response = CreateUserAPI.upgradeCloudAccount(drone, ADMIN_USERNAME, getUserDomain(testUser), "1000");

            checkResult(response, 200);
        }

        result = CreateUserAPI.promoteUserAsAdmin(drone, ADMIN_USERNAME, testUser, getUserDomain(testUser));

        Assert.assertTrue(result);

        result = CreateUserAPI.promoteUserAsAdmin(drone, ADMIN_USERNAME, testUser3, getUserDomain(testUser3));

        Assert.assertTrue(result);

        // Login 1
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteNamePrivate, SITE_VISIBILITY_PRIVATE);
        ShareUser.createSite(drone, siteNameModerated, SITE_VISIBILITY_MODERATED);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Content
        ShareUser.openDocumentLibrary(drone);
        ShareUser.createFolderInFolder(drone, folderName, "", "");

        ShareUser.uploadFileInFolder(drone, fileInfo);

        // User 1 sends the invite to User 3 to join Site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser3, siteName, UserRole.COLLABORATOR);

        // Logout
        ShareUser.logout(drone);

        // User 2 sends Site Membership Request
        Assert.assertEquals(PublicAPIRestClient.requestSiteMembership(drone, testUser2, getUserDomain(testUser2), testUser2, siteNamePrivate).getStatusLine()
                .getStatusCode(), 404, "Check if the environment is setup with no layer 7.");
        Assert.assertEquals(PublicAPIRestClient.requestSiteMembership(drone, testUser2, getUserDomain(testUser2), testUser2, siteNameModerated).getStatusLine()
                .getStatusCode(), 201);
        Assert.assertEquals(PublicAPIRestClient.requestSiteMembership(drone, testUser2, getUserDomain(testUser2), testUser2, siteName).getStatusLine()
                .getStatusCode(), 201);

        // User 1 Invites User 2 to join the private site

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteNamePrivate, UserRole.COLLABORATOR);

        ShareUser.logout(drone);

        // Login 2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        ShareUser.openDocumentLibrary(drone);

        // Logout
        ShareUser.logout(drone);

        // Login 3
        ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);

        if (alfrescoVersion.isCloud())
        {
            ShareUser.selectTenant(drone, getUserDomain(testUser));
        }

        ShareUser.openSiteDashboard(drone, siteName);
    }
    
    @Test(groups = { "Sanity", "NonGrid", "EnterpriseOnly", "Download" })
    public void downloadDroneTest() throws Exception
    {
        /** Start Test */
        testName = "downloadTest";
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String FILE_ZIP_EXT = ".zip";

        Assert.assertNotNull(customDrone);
        Assert.assertTrue(customDrone.isReady());
        Assert.assertNotNull(customDrone.getDefaultWaitTime());
        Assert.assertNotNull(customDrone.getProperties().getVersion());
        Assert.assertFalse(isAlfrescoVersionCloud(customDrone));
        Assert.assertNotNull(downloadDirectory);
        Assert.assertNotNull(mimeTypes);
        Assert.assertNotNull(googleUserName);

        if (alfrescoVersion.equals(AlfrescoVersion.Enterprise41) || isAlfrescoVersionCloud(customDrone))
        {
            throw new UnsupportedOperationException("Download as zip functionality is not available in Product Version");
        }
        
        // User
        CreateUserAPI.CreateActivateUser(customDrone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(customDrone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(customDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        // Creating folder.
        ShareUserSitePage.createFolder(customDrone, folderName, null);

        // Open Folder Details Page
        FolderDetailsPage folderDetailsPage = ShareUserSitePage.getContentDetailsPage(customDrone, folderName).render();

        // Select DownloadFolder as Zip
        folderDetailsPage.selectDownloadFolderAsZip("folder");
        folderDetailsPage.waitForFile(downloadDirectory + folderName + FILE_ZIP_EXT);

        webDriverWait(customDrone, 3000);
        
        Assert.assertTrue(ShareUser.extractDownloadedArchieve(customDrone, folderName + FILE_ZIP_EXT));
    }
    
    @Test(groups = { "Sanity", "Hybrid" })
    public void hybridDroneTest() throws Exception
    {
        if(!hybridEnabled)
        {
            throw new SkipException("Skipping Test as hybrid.enabled property is set to false.");
        }
        Assert.assertNotNull(hybridDrone);
        Assert.assertTrue(hybridDrone.isReady());
        Assert.assertNotNull(hybridDrone.getDefaultWaitTime());
        Assert.assertNotNull(hybridDrone.getProperties().getVersion());
        Assert.assertTrue(isAlfrescoVersionCloud(hybridDrone));
        Assert.assertNotNull(cloudUrlForHybrid);
        Assert.assertNotNull(hybridDomainFree);
        Assert.assertNotNull(hybridDomainPremium);
        Assert.assertNotNull(hybridShareTestProperties);
        Assert.assertNotNull(hybridShareTestProperties.getadminPassword());
        Assert.assertNotNull(hybridShareTestProperties.getadminUsername());
        Assert.assertNotNull(hybridShareTestProperties.getShareUrl());

        // Starting Test
        String testName = "hdTest";
        String invitedDomain1 = hybridDomainPremium;
        String opUser1 = getUserNameFreeDomain(testName + System.currentTimeMillis());
        String cloudUser1 = getUserNameForDomain(testName + System.currentTimeMillis(), invitedDomain1);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis();
        String opFileName = getFileName(testName) + System.currentTimeMillis();
        String cloudSiteName = getSiteName(testName + "cloud") + System.currentTimeMillis();
        String cloudFileName = getFileName(testName + "cloud") + System.currentTimeMillis();
        String workFlowName = testName + System.currentTimeMillis();
        String[] userInfo1 = new String[] { opUser1 };
        String[] cloudUserInfo1 = new String[] { cloudUser1 };
        String[] cloudFileInfo = new String[] { cloudFileName };
        String[] opFileInfo = new String[] { opFileName };

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);

        // Login as User1 (Cloud)
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(hybridDrone, cloudSiteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(hybridDrone, cloudFileInfo);

        ShareUser.logout(hybridDrone);

        // Login as User1 (OP)
        ShareUser.login(drone, opUser1, DEFAULT_PASSWORD);

        // Set up the cloud sync
        signInToAlfrescoInTheCloud(drone, cloudUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);

        // Open Document library, Upload a file
        ShareUser.uploadFileInFolder(drone, opFileInfo).render();

        // Start Simple Cloud Task workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, opFileName);

        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

        formDetails.setMessage(workFlowName);
        formDetails.setSiteName(cloudSiteName);
        formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);
        formDetails.setAssignee(cloudUser1);
        formDetails.setContentStrategy(KeepContentStrategy.KEEPCONTENT);

        // Fill the form details and start workflow
        cloudTaskOrReviewPage.startWorkflow(formDetails).render();

        MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

        WorkFlowDetailsPage detailsPage = myWorkFlowsPage.selectWorkFlow(workFlowName).render();
        Assert.assertEquals(detailsPage.getWorkFlowDetailsGeneralInfo().getDueDateString(), NONE);

        ShareUser.logout(drone);

        // Cloud user login
        ShareUser.login(hybridDrone, cloudUser1, DEFAULT_PASSWORD);

        MyTasksPage myTasks = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);
        Assert.assertTrue(ShareUser.checkIfTaskIsPresent(hybridDrone, workFlowName));
        TaskDetailsPage taskDetailsPage = myTasks.selectViewTasks(workFlowName).render();
        Assert.assertEquals(taskDetailsPage.getTaskDetailsInfo().getDueDateString(), NONE);

        ShareUser.logout(hybridDrone);
    }
}