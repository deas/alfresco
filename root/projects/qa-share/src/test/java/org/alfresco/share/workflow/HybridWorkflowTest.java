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
package org.alfresco.share.workflow;

import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners(FailedTestListener.class)
public class HybridWorkflowTest extends AbstractWorkflow
{

    protected String testUser;
    protected String siteName = "";
    DocumentLibraryPage documentLibraryPage;
    protected long maxPageLoadingTime = 20000;
    private static int i;

    /**
     * Class includes: Tests from TestLink in Area: Site DashBoard Tests
     * <ul>
     * <li>Perform an Activity on Site</li>
     * <li>Site DashBoard shows Activity Feed</li>
     * </ul>
     */
    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        initialUsersCreation();
        testName = this.getClass().getSimpleName();
    }

//
//    // TODO: Abhijit: Unable to map this test in Ent_Cloud_Mapping
//    // @DataSetup(testLinkId = "9460", groups = DataGroup.HYBRID)
//    public void dataPrep_9460(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        logger.info("[Suite ] : Start Tests in: " + getTestName());
//
//        this.drone = drone;
//        this.hybridDrone = hybridDrone;
//        siteName = getSiteName("9460");
//
//        // This method creates site on Enterprise and sync OP user with cloud user.
//        createSiteAndSyncOPUser(onPremUser, siteName);
//
//        // On cloud - create site. And invite reviewers on cloud site.
//        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//        ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(1), siteName, UserRole.COLLABORATOR);
//        ShareUser.logout(hybridDrone);
//
//    }
//
//    // TODO: Abhijit: Unable to map this test in Ent_Cloud_Mapping
//    // @DataSetup(testLinkId = "9883", groups = DataGroup.HYBRID)
//    public void dataPrep_9883(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        logger.info("[Suite ] : Start Tests in: " + getTestName());
//
//        this.drone = drone;
//        this.hybridDrone = hybridDrone;
//        siteName = getSiteName("9883");
//
//        // This method creates site on Enterprise and sync OP user with cloud user.
//        createSiteAndSyncOPUser(onPremUser, siteName);
//
//        // On cloud - create site. And invite reviewers on cloud site.
//        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//
//        ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//
//        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(1), siteName, UserRole.COLLABORATOR);
//        ShareUser.logout(hybridDrone);
//
//        // On cloud - create site. And invite reviewers on cloud site.
//        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(2), siteName, UserRole.COLLABORATOR);
//        ShareUser.logout(hybridDrone);
//    }
//
//    // @DataSetup(testLinkId = "9880", groups = DataGroup.HYBRID)
//    public void dataPrep_9880(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        // initialUsersCreation(drone);
//        logger.info("[Suite ] : Start Tests in: " + getTestName());
//
//        this.drone = drone;
//        this.hybridDrone = hybridDrone;
//        siteName = getSiteName("9880");
//
//        // This method creates site on Enterprise and sync OP user with cloud user.
//        createSiteAndSyncOPUser(onPremUser, siteName);
//
//        // On cloud - create site. And invite reviewers on cloud site.
//        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//
//        ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//
//        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(1), siteName, UserRole.COLLABORATOR);
//        ShareUser.logout(hybridDrone);
//    }
//
//    // @DataSetup(testLinkId = "9580", groups = DataGroup.HYBRID)
//    public void dataPrep_9580(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        // initialUsersCreation(drone);
//        logger.info("[Suite ] : Start Tests in: " + getTestName());
//
//        this.drone = drone;
//        siteName = getSiteName("9580");
//
//        // Create OP user
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//        ShareUser.createSite(drone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//
//        ShareUser.logout(drone);
//    }
//
//    // @DataSetup(testLinkId = "9579", groups = DataGroup.HYBRID)
//    public void dataPrep_9579(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        siteName = getSiteName("9579");// + System.currentTimeMillis();
//
//        SiteUtil.createSite(drone, siteName, "Public");
//    }
//
//    // @DataSetup(testLinkId = "9455", groups = DataGroup.HYBRID)
//    public void dataPrep_9455(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//
//        siteName = getSiteName("9455");// + System.currentTimeMillis();
//
//        SiteUtil.createSite(drone, siteName, "Public");
//    }
//
//    // @DataSetup(testLinkId = "9462", groups = DataGroup.HYBRID)
//    public void dataPrep_9462(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//
//        siteName = getSiteName("9462");// + System.currentTimeMillis();
//
//        SiteUtil.createSite(drone, siteName, "Public");
//    }
//
//    @Test(enabled = false)
//    public void defaultHybridWorkflow_9460()
//    {
//        onPremUser = getTestName();
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        try
//        {
//            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
//
//            // Creating the doc for review
//            siteName = getSiteName("9460");
//
//            createAndUploadFile(fileName);
//            taskName = "Task name - " + System.currentTimeMillis();
//
//            // login with an enterprise user and start a cloud task or review
//            // workflow.
//            initiateCloudReviewWorkflow(null, TaskType.SIMPLE_CLOUD_TASK, fileName, reviewers.get(1));
//            ShareUser.logout(drone);
//            // logon to cloud url and complete the task
//            completeTaskOnCloud(reviewers.get(1));
//
//            // OP - Logs in and completes task on Enterprise
//            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//            sharePage = openMyTaskPage(drone);
//
//            sharePage = completeTask("The document was reviewed and approved on Alfresco Cloud");
//            assertTrue(sharePage.getTitle().contains(PAGE_TITLE_MYTASKS));
//            ShareUser.logout(drone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//
//    }
//
//    @Test(enabled = false)
//    public void defaultHybridWorkflow2Reviewers_9883() throws InterruptedException
//    {
//        try
//        {
//            onPremUser = getUserNamePremiumDomain("9883");
//            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//            // Creating the doc for review
//            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
//            siteName = getTestName();
//            // dataPrep_9883(drone);
//            createAndUploadFile(fileName);
//
//            taskName = getTestName();
//            // login with an enterprise user and start a cloud task or review workflow.
//            initiateCloudReviewWorkflow("50", TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(1), reviewers.get(2));
//            ShareUser.logout(drone);
//
//            // reviewers complete the task on cloud.
//            completeTaskOnCloud(reviewers.get(1));
//            completeTaskOnCloud(reviewers.get(2));
//
//            // OP - Login -> open task and complete it on enterprise
//            sharePage = openMyTaskPage(drone);
//            sharePage = completeTask("The task was completed on Alfresco Cloud");
//            assertTrue(sharePage.isBrowserTitle(PAGE_TITLE_MYTASKS));
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * @throws Exception
//     */
//    @Test(enabled = false)
//    public void defaultHybridWorkflowReassign_9880() throws Exception
//    {
//        onPremUser = getUserNamePremiumDomain("9880");
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        // Creating the doc for review
//        String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
//        siteName = getTestName();
//        createAndUploadFile(fileName);
//
//        taskName = getTestName();
//
//        // login with an enterprise user and start a cloud task or review workflow.
//        initiateCloudReviewWorkflow(null, TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(1));
//        ShareUser.logout(drone);
//
//        completeTaskOnCloud(reviewers.get(1));
//
//        // OP - Login and reassign the task to another OP user - this case ADMIN
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        sharePage = openMyTaskPage(drone);
//        String reassignee = ADMIN_USERNAME;
//        sharePage = reAssignTask(reassignee);
//        ShareUser.logoutCleanCookies(drone);
//
//        // Second OP user open and complete the task.
//        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
//        sharePage = openMyTaskPage(drone);
//        sharePage = completeTask("The task was completed on Alfresco Cloud");
//        assertTrue(sharePage.isBrowserTitle(PAGE_TITLE_EDITTASK));
//    }
//
//    @Test(enabled = false)
//    public void defaultHybridWorkflow_9579() throws Exception
//
//    {
//        try
//        {
//            File file = SiteUtil.prepareFile();
//            SitePage site = drone.getCurrentPage().render();
//            documentLibraryPage = site.getSiteNav().selectSiteDocumentLibrary().render();
//            UploadFilePage upLoadPage = documentLibraryPage.getNavigation().selectFileUpload();
//            documentLibraryPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
//            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(file.getName()).render();
//            StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
//            CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW))
//                    .render();
//            cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
//            Assert.assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK));
//            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
//            formDetails.setSiteName(siteName);
//            List<String> userNames = new ArrayList<String>();
//            userNames.add(reviewers.get(1));
//            formDetails.setReviewers(userNames);
//            formDetails.setMessage("test1");
//            documentDetailsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxPageLoadingTime);
//            Assert.assertTrue(documentDetailsPage.isDocumentDetailsPage());
//            ShareUser.logout(drone);
//            // logon to cloud url and complete the task
//            // completeTaskOnCloud(reviewer1, password1);
//            ShareUser.login(hybridDrone, reviewers.get(1), DEFAULT_PASSWORD);
//            DashBoardPage dashBoard = hybridDrone.getCurrentPage().render();
//            MyTasksPage MyTasksPage = dashBoard.getNav().selectMyTasks();
//            Assert.assertEquals(MyTasksPage.getTitle(), String.format("Alfresco %s My Tasks", StringEscapeUtils.unescapeHtml4("&raquo;")));
//        }
//
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//
//    }
//
//    @Test(enabled = false)
//    public void defaultHybridWorkflow_9455() throws Exception
//
//    {
//        try
//        {
//            File file = SiteUtil.prepareFile();
//            SitePage site = drone.getCurrentPage().render();
//            documentLibraryPage = site.getSiteNav().selectSiteDocumentLibrary().render();
//            UploadFilePage upLoadPage = documentLibraryPage.getNavigation().selectFileUpload();
//            documentLibraryPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
//            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(file.getName()).render();
//            StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
//            CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW))
//                    .render();
//            cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
//            Assert.assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK));
//
//            ShareUser.logout(drone);
//        }
//
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//
//    }
//
//    @Test(enabled = false)
//    public void defaultHybridWorkflow_9580()
//    {
//        try
//        {
//            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
//
//            // Creating the doc for review
//            siteName = getTestName();
//            // dataPrep_9580(drone);
//            createAndUploadFile(fileName);
//            taskName = "Task name - " + System.currentTimeMillis();
//
//            // login with an enterprise user and start a cloud task or review workflow.
//            initiateCloudReviewWorkflow(null, TaskType.SIMPLE_CLOUD_TASK, fileName, reviewers.get(1));
//            ShareUser.logout(drone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//
//    }
//
//    @Test(enabled = false)
//    public void defaultHybridWorkflow_9462() throws Exception
//
//    {
//        try
//        {
//            File file = SiteUtil.prepareFile();
//            SitePage site = drone.getCurrentPage().render();
//            documentLibraryPage = site.getSiteNav().selectSiteDocumentLibrary().render();
//            UploadFilePage upLoadPage = documentLibraryPage.getNavigation().selectFileUpload();
//            documentLibraryPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
//            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(file.getName()).render();
//            StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
//            CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW))
//                    .render();
//            cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
//            Assert.assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK));
//            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
//            formDetails.setSiteName(siteName);
//            List<String> userNames = new ArrayList<String>();
//            userNames.add(reviewers.get(1));
//            formDetails.setReviewers(userNames);
//            formDetails.setMessage("test1");
//            documentDetailsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxPageLoadingTime);
//            Assert.assertTrue(documentDetailsPage.isDocumentDetailsPage());
//            drone.findAndWait(By.cssSelector("div.document-delete>a")).click();
//            drone.findAndWait(By.cssSelector("button[id$='yui-gen39-button']")).click();
//            ShareUser.logout(drone);
//        }
//
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//
//    }
//
//    @Test(enabled = false)
//    public void defaultHybridWorkflow_9611() throws Exception
//
//    {
//        try
//        {
//            File file = SiteUtil.prepareFile();
//            SitePage site = drone.getCurrentPage().render();
//            documentLibraryPage = site.getSiteNav().selectSiteDocumentLibrary().render();
//            UploadFilePage upLoadPage = documentLibraryPage.getNavigation().selectFileUpload();
//            documentLibraryPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
//            DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(file.getName()).render();
//            StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
//            CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW))
//                    .render();
//            cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
//            Assert.assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK));
//            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
//            formDetails.setSiteName(siteName);
//            List<String> userNames = new ArrayList<String>();
//            userNames.add(reviewers.get(1));
//            formDetails.setReviewers(userNames);
//            // documentDetailsPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxPageLoadingTime);
//            Assert.assertFalse(documentDetailsPage.isDocumentDetailsPage());
//            ShareUser.logout(drone);
//
//        }
//
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    // @DataSetup(testLinkId = "9634", groups = DataGroup.HYBRID)
//    public void dataPrep_9634(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        this.drone = drone;
//        this.hybridDrone = hybridDrone;
//        String testName = getTestName();
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//
//        // User
//        String[] testUserInfo = new String[] { onPremUser };
//        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//        // This method creates site on Enterprise and sync OP user with cloud
//        // user.
//        createSiteAndSyncOPUser(onPremUser, siteName);
//
//        // On cloud - create site. And invite reviewers on cloud site.
//        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//        ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//
//        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(1), siteName, UserRole.COLLABORATOR);
//        ShareUser.logout(hybridDrone);
//
//        // On cloud - create site. And invite reviewers on cloud site.
//        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(2), siteName, UserRole.COLLABORATOR);
//        ShareUser.logout(hybridDrone);
//
//        // On cloud - create site. And invite reviewers on cloud site.
//        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(3), siteName, UserRole.COLLABORATOR);
//        ShareUser.logout(hybridDrone);
//
//        // On cloud - create site. And invite reviewers on cloud site.
//        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(4), siteName, UserRole.COLLABORATOR);
//        ShareUser.logout(hybridDrone);
//    }
//
//    /**
//     * Test - Enterprise40x-9634: Users required approval percent.
//     * <ul>
//     * <li>Login</li>
//     * <li>From My Site Document Library access the file view details page</li>
//     * <li>Start work flow</li>
//     * <li>Review will be done by Reviewers</li>
//     * <li>OP User will complete the task done process.</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9634() throws Exception
//    {
//        DocumentLibraryPage documentLibPage = null;
//        /** Start Test */
//        testName = getTestName();
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//        String fileName = getFileName(testName);
//
//        // Login
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//
//        String[] fileInfo = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo);
//        documentLibPage.selectFile(fileName).render();
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        initiateCloudReviewWorkflow("70", TaskType.SIMPLE_CLOUD_TASK, fileName, reviewers.get(1), reviewers.get(2), reviewers.get(3), reviewers.get(4));
//        ShareUser.logout(drone);
//
//        // logon to cloud url and complete the task
//        // TODO Two reviewers will accept and another two users will reject it.
//        completeTaskOnCloud(reviewers.get(1));
//        completeTaskOnCloud(reviewers.get(2));
//        completeTaskOnCloud(reviewers.get(3));
//        completeTaskOnCloud(reviewers.get(4));
//
//        // OP - Logs in and completes task on Enterprise
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//        sharePage = openMyTaskPage(drone);
//        sharePage = completeTask("The document was reviewed and approved on Alfresco Cloud");
//        Assert.assertTrue(sharePage.getTitle().contains(PAGE_TITLE_MYTASKS));
//
//        // TODO Cancel workflow is not yet implemented.
//        ShareUser.logout(drone);
//    }
//
//    // @DataSetup(testLinkId = "9638", groups = DataGroup.HYBRID)
//    public void dataPrep_9638(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // This method creates site on Enterprise and sync OP user with cloud
//            // user.
//            createSiteAndSyncOPUser(onPremUser, siteName);
//
//            // On cloud - create site. And invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//
//            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(1), siteName, UserRole.COLLABORATOR);
//            ShareUser.logout(hybridDrone);
//
//            // On cloud - invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(2), siteName, UserRole.COLLABORATOR);
//            ShareUser.logout(hybridDrone);
//
//            // On cloud - invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(3), siteName, UserRole.COLLABORATOR);
//            ShareUser.logout(hybridDrone);
//
//            // On cloud - invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(4), siteName, UserRole.COLLABORATOR);
//            ShareUser.logout(hybridDrone);
//
//            // On cloud - invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(5), siteName, UserRole.COLLABORATOR);
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * Test - Enterprise40x-9638: : Check more reviewers can review.
//     * <ul>
//     * <li>Login</li>
//     * <li>From My Site Document Library access the file view details page</li>
//     * <li>Start work flow</li>
//     * <li>Review will be done by Reviewers</li>
//     * <li>OP User will complete the task done process.</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9638(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        this.drone = drone;
//        this.hybridDrone = hybridDrone;
//        DocumentLibraryPage documentLibPage = null;
//        /** Start Test */
//        testName = getTestName();
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//        String fileName = getFileName(testName);
//
//        // Login
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//
//        String[] fileInfo = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo);
//        documentLibPage.selectFile(fileName).render();
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        initiateCloudReviewWorkflow(null, TaskType.SIMPLE_CLOUD_TASK, fileName, reviewers.get(1), reviewers.get(2), reviewers.get(3), reviewers.get(4),
//                reviewers.get(5));
//        ShareUser.logout(drone);
//
//        // logon to cloud url and complete the task
//        // TODO Addeding comments on approve step is not yet implmented.
//        completeTaskOnCloud(reviewers.get(1));
//        completeTaskOnCloud(reviewers.get(2));
//        completeTaskOnCloud(reviewers.get(3));
//        completeTaskOnCloud(reviewers.get(4));
//        completeTaskOnCloud(reviewers.get(5));
//
//        // OP - Logs in and completes task on Enterprise
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//        sharePage = openMyTaskPage(drone);
//        sharePage = completeTask("The document was reviewed and approved on Alfresco Cloud");
//
//        Assert.assertNotNull(sharePage);
//        Assert.assertTrue(sharePage.getTitle().contains(PAGE_TITLE_MYTASKS));
//
//        ShareUser.logout(drone);
//    }
//
//    // @DataSetup(testLinkId = "9650", groups = DataGroup.HYBRID)
//    public void dataPrep_9650(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // This method creates site on Enterprise and sync OP user with cloud
//            // user.
//            createSiteAndSyncOPUser(onPremUser, siteName);
//
//            // On cloud - create site. And invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//
//            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(1), siteName, UserRole.COLLABORATOR);
//            ShareUser.logout(hybridDrone);
//
//            // On cloud -invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(2), siteName, UserRole.COLLABORATOR);
//            ShareUser.logout(hybridDrone);
//
//            // On cloud -invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(3), siteName, UserRole.COLLABORATOR);
//            ShareUser.logout(hybridDrone);
//
//            // On cloud -invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(4), siteName, UserRole.COLLABORATOR);
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * Test - Enterprise40x-9650: Check more reviewers can review.
//     * <ul>
//     * <li>Login</li>
//     * <li>From My Site Document Library access the file view details page</li>
//     * <li>Start work flow</li>
//     * <li>Review will be done by Reviewers</li>
//     * <li>OP User will complete the task done process.</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9650() throws Exception
//    {
//        DocumentLibraryPage documentLibPage = null;
//        /** Start Test */
//        testName = getTestName();
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//        String fileName = getFileName(testName);
//
//        // Login
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//
//        String[] fileInfo = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo);
//        documentLibPage.selectFile(fileName).render();
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        // TODO Adding dueDate,task, priority are missing in webdrone.
//        initiateCloudReviewWorkflow(null, TaskType.SIMPLE_CLOUD_TASK, fileName, reviewers.get(1), reviewers.get(2), reviewers.get(3), reviewers.get(4));
//        ShareUser.logout(drone);
//
//        // logon to cloud url and complete the task
//        // TODO Rejecting task is not yet implemented.
//        completeTaskOnCloud(reviewers.get(1));
//        completeTaskOnCloud(reviewers.get(2));
//        completeTaskOnCloud(reviewers.get(3));
//        completeTaskOnCloud(reviewers.get(4));
//
//        // OP - Logs in and completes task on Enterprise
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//        sharePage = openMyTaskPage(drone);
//
//        // TODO Remove workflow is not yet implemented in webdrone.
//
//        ShareUser.logout(drone);
//    }
//
//    // @DataSetup(testLinkId = "9623", groups = DataGroup.HYBRID)
//    public void dataPrep_9623(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // This method creates site on Enterprise and sync OP user with cloud user.
//            createSiteAndSyncOPUser(onPremUser, siteName);
//
//            // On cloud - create site. And invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * Test - Enterprise40x-9623: Remove Ownership.
//     * <ul>
//     * <li>Login</li>
//     * <li>From My Site Document Library access the file view details page</li>
//     * <li>Start work flow</li>
//     * <li>Assigned task to Reviewer1</li>
//     * <li>Site Manager tries to unsync the started task workflow</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9623() throws Exception
//    {
//        DocumentLibraryPage documentLibPage = null;
//        /** Start Test */
//        testName = getTestName();
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//        String fileName = getFileName(testName);
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//
//        String[] fileInfo = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo);
//        documentLibPage.selectFile(fileName).render();
//
//        // start a cloud task or review workflow.
//        initiateCloudReviewWorkflow(null, TaskType.SIMPLE_CLOUD_TASK, fileName, reviewers.get(1));
//        ShareUser.logout(drone);
//
//        // Cloud Sitemanager logs in
//        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//        documentLibPage = ShareUser.openSitesDocumentLibrary(hybridDrone, siteName);
//        DocumentDetailsPage docPage = documentLibPage.selectFile(fileName).render();
//
//        // TODO: Verifying Unsync the task is not yet implemented.
//    }
//
//    // @DataSetup(testLinkId = "9624", groups = DataGroup.HYBRID)
//    public void dataPrep_9624(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // This method creates site on Enterprise and sync OP user with cloud user.
//            createSiteAndSyncOPUser(onPremUser, siteName);
//
//            // On cloud - create site. And invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * Test - Enterprise40x-9624: Choosing different cloud network.
//     * <ul>
//     * <li>Login</li>
//     * <li>From My Site Document Library access the file view details page</li>
//     * <li>Start work flow</li>
//     * <li>verify the other network is accessible in Destination and assignee page</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9624() throws Exception
//    {
//        DocumentLibraryPage documentLibPage = null;
//        /** Start Test */
//        testName = getTestName();
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//        String fileName = getFileName(testName);
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//
//        String[] fileInfo = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo);
//        documentLibPage.selectFile(fileName).render();
//
//        // Starting workflow
//        StartWorkFlowPage workFlowPage = startWorkFLow(fileName).render();
//
//        // Create cloud/review workflow
//        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) workFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
//        DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
//
//        Assert.assertFalse(destinationAndAssigneePage.isNetworkDisplayed("freenet.test"));
//    }
//
//    // @DataSetup(testLinkId = "9625", groups = DataGroup.HYBRID)
//    public void dataPrep_9625(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // This method creates site on Enterprise and sync OP user with cloud user.
//            createSiteAndSyncOPUser(onPremUser, siteName);
//
//            // On cloud - create site. And invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * Test - Enterprise40x-9625: Add comments by non assignee.
//     * <ul>
//     * <li>Login</li>
//     * <li>From My Site Document Library access the file view details page</li>
//     * <li>Start work flow</li>
//     * <li>verify the other than assignee can add comment or not</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9625() throws Exception
//    {
//        DocumentLibraryPage documentLibPage = null;
//        /** Start Test */
//        testName = getTestName();
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//        String fileName = getFileName(testName);
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//
//        String[] fileInfo = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo);
//        documentLibPage.selectFile(fileName).render();
//
//        // start a cloud task or review workflow.
//        initiateCloudReviewWorkflow(null, TaskType.SIMPLE_CLOUD_TASK, fileName, reviewers.get(1), reviewers.get(2));
//        ShareUser.logout(drone);
//
//        // Reviewer1 Accepting and creating comment.
//        // TODO : Add Comments is not yet implemented.
//        completeTaskOnCloud(reviewers.get(1));
//
//        // TODO : For Reviewer 2, Rejecting task is not yet implemented
//
//        // OP - Logs in and Verifies the given comments
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//        // TODO: Openinig existing workflows needs to be implemented.
//        // TODO: Retrieving the comments is not yet implemented.
//
//    }
//
//    // @DataSetup(testLinkId = "9626", groups = DataGroup.HYBRID)
//    public void dataPrep_9626(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // This method creates site on Enterprise and sync OP user with cloud user.
//            createSiteAndSyncOPUser(onPremUser, siteName);
//
//            // On cloud - create site. And invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * Test - Enterprise40x-9626: Adding multiple files .
//     * <ul>
//     * <li>Login</li>
//     * <li>From My Site Document Library access the file view details page</li>
//     * <li>Start work flow</li>
//     * <li>Add more files to workflow</li>
//     * <li>verify the files added successfully.</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9626() throws Exception
//    {
//        /** Start Test */
//        testName = getTestName();
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//        String fileName = getFileName(testName);
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
//
//        String[] fileInfo1 = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo1);
//        documentLibPage.selectFile(fileName).render();
//
//        String[] fileInfo2 = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo2);
//        documentLibPage.selectFile(fileName).render();
//
//        String[] fileInfo3 = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo3);
//        documentLibPage.selectFile(fileName).render();
//
//        CloudTaskOrReviewPage cloudTaskOrReviewPage = getCloudReviewPageFromMyTasks();
//
//        DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
//        drone.waitFor(4000);
//        destinationAndAssigneePage.selectSite(siteName);
//        AssignmentPage assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage();
//        List<String> userNames = new ArrayList<String>();
//        userNames.add(reviewers.get(1));
//        userNames.add(reviewers.get(2));
//        assignmentPage.selectReviewers(userNames);
//
//        // TODO : Add multiple files.
//
//        // TODO : Verify the files added successfully.
//    }
//
//    // @DataSetup(testLinkId = "9669", groups = DataGroup.HYBRID)
//    public void dataPrep_9669(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//            String fileName = getFileName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // This method creates site on Enterprise and sync OP user with cloud user.
//            createSiteAndSyncOPUser(onPremUser, siteName);
//
//            // On cloud - create site. And invite reviewers on cloud site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * Test - Enterprise40x-9669: More folders test.
//     * <ul>
//     * <li>Login</li>
//     * <li>From My Site Document Library access the file view details page</li>
//     * <li>Start work flow</li>
//     * <li>Add more files to workflow</li>
//     * <li>Try to remove child files from workflow</li>
//     * <li>Stopping workflow from OP</li>
//     * <li>Delete parent folder and verify is deletion is successful.</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9669() throws Exception
//    {
//        /** Start Test */
//        testName = getTestName();
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//        String fileName = getFileName(testName);
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//        String[] fileInfo1 = { fileName + "1" };
//        ShareUser.uploadFileInFolder(drone, fileInfo1);
//
//        String[] fileInfo2 = { fileName + "2" };
//        ShareUser.uploadFileInFolder(drone, fileInfo2);
//
//        String[] fileInfo3 = { fileName + "3" };
//        ShareUser.uploadFileInFolder(drone, fileInfo3);
//
//        String[] fileInfo4 = { fileName + "4" };
//        ShareUser.uploadFileInFolder(drone, fileInfo4);
//
//        CloudTaskOrReviewPage cloudTaskOrReviewPage = getCloudReviewPageFromMyTasks();
//
//        DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
//        drone.waitFor(4000);
//        destinationAndAssigneePage.selectSite(siteName);
//        AssignmentPage assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage();
//        List<String> userNames = new ArrayList<String>();
//        userNames.add(reviewers.get(1));
//        assignmentPage.selectReviewers(userNames);
//
//        // TODO: Adding more files to workflow is not yet implemented.
//
//        // TODO: Removing files from workflow in cloud is not yet implemented.
//
//        // TODO: Stopping the workflow from OP is not yet implmented
//
//        // Cloud initiator tries to remove parent folder from document details page.
//    }
//
//    // @DataSetup(testLinkId = "9670", groups = DataGroup.HYBRID)
//    public void dataPrep_9670(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // This method creates site on Enterprise and sync OP user with cloud user.
//            createSiteAndSyncOPUser(onPremUser, siteName);
//
//            // On cloud - create site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * Test - Enterprise40x-9670: Removing the max file in workflow task.
//     * <ul>
//     * <li>Login</li>
//     * <li>From My Site Document Library access the file view details page</li>
//     * <li>Add more than 1 gb size file to workflow</li>
//     * <li>Start work flow</li>
//     * <li>Initiator tries to remove more than 1 gb size file to workflow</li>
//     * <li>Verify the exception should display</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9670() throws Exception
//    {
//        DocumentLibraryPage documentLibPage = null;
//        /** Start Test */
//        testName = getTestName();
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//        String fileName = "ALF-5024-2MB-FILE.txt";
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//
//        // TODO : As per the test link file size should be 1 gb but unable to create it so using the existing 2mb file.
//        String[] fileInfo = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo);
//        documentLibPage.selectFile(fileName).render();
//
//        // start a cloud task or review workflow.
//        initiateCloudReviewWorkflow(null, TaskType.SIMPLE_CLOUD_TASK, fileName, reviewers.get(1));
//        ShareUser.logout(drone);
//
//        // Initiator in cloud- Logs in and tries to remove the file
//        ShareUser.login(drone, reviewers.get(0), DEFAULT_PASSWORD);
//
//        // TODO: Removing the file from workflow needs to be implemented.
//    }
//
//    // @DataSetup(testLinkId = "9628", groups = DataGroup.HYBRID)
//    public void dataPrep_9628(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // This method creates site on Enterprise and sync OP user with cloud user.
//            createSiteAndSyncOPUser(onPremUser, siteName);
//
//            // On cloud - create site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * Test - Enterprise40x-9628: Cloud standard network user trying to start workflow.
//     * <ul>
//     * <li>Login</li>
//     * <li>Start work flow using cloud trial user</li>
//     * <li>Workflow should get started.</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9628() throws Exception
//    {
//        /** Start Test */
//        testName = getTestName();
//        String fileName = getFileName(testName);
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//        ShareUser.logout(drone);
//
//        // Initiator in cloud- Logs in and tries to remove the file
//        ShareUser.login(drone, reviewers.get(0), DEFAULT_PASSWORD);
//        ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//        String[] fileInfo = { fileName };
//        ShareUser.uploadFileInFolder(drone, fileInfo);
//
//        MyTasksPage myTasksPage = createNewWorkFlowFromMyTasks(siteName, onPremUser);
//
//        Assert.assertNotNull(myTasksPage);
//        Assert.assertNotNull(myTasksPage.navigateToEditTaskPage(siteName));
//    }
//
//    // @DataSetup(testLinkId = "9629", groups = DataGroup.HYBRID)
//    public void dataPrep_9629(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//            String fileName = getFileName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // Creating cloud user to be used for downgrading.
//            String downgradedUser = "downgradedUser" + "@" + "downgrade.test";
//            CreateUserAPI.CreateActivateUser(hybridDrone, DEFAULT_PREMIUMNET_USER, downgradedUser);
//            CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, "downgrade.test", "1000");
//
//            // This method creates site on Enterprise and sync OP user with cloud user.
//            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//            ShareUser.createSite(drone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//
//            ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//            String[] fileInfo = { fileName };
//            ShareUser.uploadFileInFolder(drone, fileInfo);
//
//            signInToAlfrescoInTheCloud(drone, downgradedUser, DEFAULT_PASSWORD);
//
//            ShareUser.logout(drone);
//
//            // On cloud - create site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    /**
//     * Test - Enterprise40x-9629: Demote cloud account during flow.
//     * <ul>
//     * <li>Login</li>
//     * <li>Start work flow using OP user</li>
//     * <li>Workflow should get started.</li>
//     * <li>Downgrade the cloud user and tries to edit the doc and sync to cloud</li>
//     * <li>Verify Account downgraded successfully and sync cannot be successful</li>
//     * </ul>
//     */
//    @Test(enabled = false)
//    public void hybridWorkflow_9629() throws Exception
//    {
//        /** Start Test */
//        testName = getTestName();
//        String fileName = getFileName(testName);
//        String downgradedUser = "downgradedUser" + "@" + "downgrade.test";
//
//        /** Test Data Setup */
//        siteName = getSiteName(testName);
//        String onPremUser = getUserNameFreeDomain(testName);
//
//        // Downgrading cloud user
//        HttpResponse response = CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, DOMAIN_FREE, "1001");
//        Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);
//
//        // login with an enterprise user and start a cloud task or review
//        // workflow.
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//        // start a cloud task or review workflow.
//        StartWorkFlowPage workFlowPage = startWorkFLow(fileName).render();
//
//        // Create cloud/review workflow
//        CloudTaskOrReviewPage cloudTaskOrReviewPage = ((CloudTaskOrReviewPage) workFlowPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
//
//        String[] cloudUsers = { downgradedUser };
//        SharePage sharePage = submitFormDetails(siteName, cloudUsers, null, siteName);
//
//        // TODO verify the excetpion in submitFormDetails for sync to cloud.
//
//        ShareUser.logout(drone);
//    }
//
//    // @DataSetup(testLinkId = "9630", groups = DataGroup.HYBRID)
//    public void dataPrep_9630(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        try
//        {
//            this.drone = drone;
//            this.hybridDrone = hybridDrone;
//            String testName = getTestName();
//            siteName = getSiteName(testName);
//            String fileName = getFileName(testName);
//
//            // User
//            String[] testUserInfo = new String[] { testUser };
//            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
//
//            // This method creates site on Enterprise and sync OP user with cloud user.
//            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//
//            ShareUser.createSite(drone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//
//            signInToAlfrescoInTheCloud(drone, reviewers.get(0), DEFAULT_PASSWORD);
//
//            ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);
//            String[] fileInfo = { fileName };
//            ShareUser.uploadFileInFolder(drone, fileInfo);
//
//            ShareUser.logout(drone);
//
//            // On cloud - create site.
//            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
//            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//            ShareUser.logout(hybridDrone);
//        }
//        catch (Throwable e)
//        {
//            reportError(drone, testName, e);
//        }
//    }
//
//    @DataSetup(testLinkId = "9607")
//    public void dataPrep_9607(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//
//        this.drone = drone;
//        this.hybridDrone = hybridDrone;
//
//        String user1FileName = getFileName(testName) + "-UF.pdf";
//        String[] userFileInfo = { user1FileName, DOCLIB };
//        siteName = getTestName() + "-01";
//        onPremUser = getUserNamePremiumDomain(getTestName() + "-01");
//
//        // create one on prem user1
//        String[] userInfo = new String[] { onPremUser };
//        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);
//
//        // Create User (Cloud)
//        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, userInfo);
//
//        // Create OP user
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        // sync user 1 with the premier net cloud user
//        signInToAlfrescoInTheCloud(drone, onPremUser, DEFAULT_PASSWORD);
//        ShareUser.createSite(drone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
//        // upload a file1 with on prem user1' site
//        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
//        ShareUser.uploadFileInFolder(drone, userFileInfo).render();
//        ShareUser.logout(drone);
//
//    }
//
//    /**
//     * Users/Reviewers from different network should not appear as workflow participant.
//     */
//    @Test
//    public void testSyncFilesByAdmin_9607() throws Exception
//    {
//        siteName = getTestName() + "-01";
//        onPremUser = getUserNamePremiumDomain(getTestName() + "-01");
//
//        // create a work flow with cloud review
//        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
//        MyTasksPage myTasksPage = openMyTaskPage(drone);
//
//        StartWorkFlowPage startWrkFlwPage = myTasksPage.selectStartWorkflowButton();
//        CloudTaskOrReviewPage wrkFlwPage = (CloudTaskOrReviewPage) startWrkFlwPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW);
//        DestinationAndAssigneePage destAndAssPage = wrkFlwPage.selectDestinationAndAssigneePage().render();
//
//        // check for synced and some other network
//        Assert.assertTrue(destAndAssPage.isNetworkDisplayed(DOMAIN_PREMIUM));
//        Assert.assertFalse(destAndAssPage.isNetworkDisplayed(DOMAIN_FREE));
//        destAndAssPage.selectCancelButton();
//        ShareUser.logout(drone);
//
//    }
//
//    @DataSetup(testLinkId = "9611")
//    public void dataPrep_9611(WebDrone drone, WebDrone hybridDrone) throws Exception
//    {
//        this.drone = drone;
//        this.hybridDrone = hybridDrone;
//        String user1 = String.format("user%s@%s", getTestName() + "+01", "HWFfree" + DOMAIN_FREE);
//
//        // Create User1 (On-premise)
//        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { user1 });
//        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, new String[] { user1 });
//
//        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
//        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
//        ShareUser.logout(drone);
//
//    }
//
//    @Test
//    public void testHybridWorkFlow_9611() throws Exception
//    {
//        String user1 = String.format("user%s@%s", getTestName() + "-01", "HWFfree" + DOMAIN_FREE);
//
//        try
//        {
//            // Login as User1 (On-Premise)
//            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
//            MyTasksPage myTasksPage = openMyTaskPage(drone);
//
//            StartWorkFlowPage startWrkFlwPage = myTasksPage.selectStartWorkflowButton();
//            CloudTaskOrReviewPage wrkFlwPage = (CloudTaskOrReviewPage) startWrkFlwPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW);
//            wrkFlwPage.selectDestinationAndAssigneePage();
//            Assert.assertEquals(wrkFlwPage.getErrorMessage(), "No network is enabled for sync");
//            ShareUser.logout(drone);
//        }
//        catch (InterruptedException e)
//        {
//            logger.error("Interupted while opening task");
//        }
//        catch (Exception e)
//        {
//            logger.error("Time out during retrievel of some element");
//        }
//    }
}