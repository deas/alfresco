package org.alfresco.share.workflow;

import org.alfresco.share.util.AbstractWorkflow;

//9603 X similar to 9608
//9607 X
//9611 X
//9617 X
//9598 X
//9605 X
//9606 
//9610 X
//9608 --> 9603
//9612 X--> pending
//9613 X- Pending for workflow i've created object.
//9614
//9616 X- Pending for My Task object.
//9594 9605
//14192
//9618
  
public class HybridWorkFlowTest_1 extends AbstractWorkflow
{/*
    private static Log logger = LogFactory.getLog(HybridWorkflowTest1.class);

    protected String testUser;    
    DocumentLibraryPage documentLibraryPage;
    protected long maxPageLoadingTime = 20000;    

    
    *//**
     * Class includes: Tests from TestLink in Area: Workflow
     *//*
    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testUser + "@" + DOMAIN_FREE;
        logger.info("[Suite ] : Start Tests in: " + testName);
    }
    
    @DataSetup(testLinkId = "9611")
    public void dataPrep_9611(WebDrone drone, WebDrone hybridDrone) throws Exception
    {
        this.drone = drone;
        this.hybridDrone = hybridDrone;
        String user1 = String.format("user%s@%s", getTestName(),  "HWFfree" + DOMAIN_FREE);
       
        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] {user1});
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, new String[] {user1});
        
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);        
        ShareUser.logout(drone);
        
    }
    
    @Test
    public void testHybridWorkFlow_9611() throws Exception
    {
        //dataPrep_9611(drone, hybridDrone);

        String user1 = String.format("user%s@%s", getTestName(),  "HWFfree" + DOMAIN_FREE);
       
        try
        {
            // Login as User1 (On-Premise)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            MyTasksPage myTasksPage = openTask(drone);

            StartWorkFlowPage startWrkFlwPage = myTasksPage.selectStartWorkflowButton();
            CloudTaskOrReviewPage wrkFlwPage = (CloudTaskOrReviewPage) startWrkFlwPage.getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW);
            wrkFlwPage.selectDestinationAndAssigneePage();
            Assert.assertEquals(wrkFlwPage.getErrorMessage(), "No network is enabled for sync");
            ShareUser.logout(drone);
        }
        catch (InterruptedException e)
        {
            logger.error("Interupted while opening task");
        }
        catch(Exception e)
        {
            logger.error("Time out during retrievel of some element");
        }
    }
    
    @DataSetup(testLinkId = "9603")
    public void dataPrep_9603(WebDrone drone, WebDrone hybridDrone) throws Exception
    {
        this.drone = drone;
        this.hybridDrone = hybridDrone;
        siteName = getTestName()+"-001";
        String user1FileName = getFileName(testName) + "-UF.pdf";
        String user1 = getUserNamePremiumDomain(getTestName()+"-001");
        String user2 = getUserNamePremiumDomain(getTestName()+"-002") ;      
        
        String[] userFileInfo = { user1FileName, DOCLIB };
        String[] userInfo = { user1 };
        
        // create one on prem user1
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, userInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo);        
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[]{user2});
        
        // Create site by OP user1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
        
        // upload a file1 with on prem user1' site
        ShareUser.uploadFileInFolder(drone, userFileInfo).render();        
        // sync user 1 with the premier net cloud user       
        ShareUser.logout(drone);        
       
        // sync user 2 with premier net cloud user
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, user1, DEFAULT_PASSWORD);
        ShareUser.logout(drone);        
       
     // Create site by OP user1
        ShareUser.login(hybridDrone, user1, DEFAULT_PASSWORD);
        
        ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
            
        // sync user 1 with the premier net cloud user       
        ShareUser.logout(hybridDrone);     
        
    }
    *//**
     * Task of workflow should not appear in non work flow user. 
     * *//*
    @Test
    public void testSyncFilesByAdmin_9603() throws Exception
    {
        //dataPrep_9603(drone, hybridDrone);
        taskName="new task";
        siteName = getTestName()+"-001";
        String user1FileName = getFileName(testName) + "-UF.pdf";
        String user1 = getUserNamePremiumDomain(getTestName()+"-001");
        String user2 = getUserNamePremiumDomain(getTestName()+"-002") ;      
        
     
        // create a work flow with cloud review
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        
        initiateCloudReviewWorkflow(null, TaskType.CLOUD_REVIEW_TASK, user1FileName, user1);
    
        // login to user2
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);
        // check tasks dashlet of user2 
        Assert.assertFalse(ShareUser.searchMyDashBoardWithRetry(drone, "my-tasks", taskName, false));
        // Test:: while checking user'1 workflow should not be appearing        
    }
    

    
    @DataSetup(testLinkId = "9605")
    public void dataPrep_9605(WebDrone drone) throws Exception
    {
        logger.info("[Suite ] : Start Tests in: " + getTestName());

        this.drone = drone;
        siteName = getTestName();

        // This method creates site on Enterprise and sync OP user with cloud
        // user.
        createSiteAndSyncOPUser(siteName);

        // On cloud - create site. And invite reviewers on cloud site.
        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(1), siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(2), siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(3), siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(4), siteName, UserRole.COLLABORATOR);
        ShareUser.logout(hybridDrone);
    }
    
    *//**
     * User tries to remove the created workflow which is been reviewed by 4 reviewers wherein
     * 2 have approved it and 2 rejected.
     * *//*
    @Test
    public void testSyncFilesByAdmin_9605() throws Exception
    {
        onPremUser = getUserNamePremiumDomain("9605");
        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);

        // Creating the doc for review
        String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
        siteName = getTestName();
        //dataPrep_9883(drone);
        createAndUploadFile(fileName);

        taskName = getTestName();
        // login with an enterprise user and start a cloud task or review work flow.
        initiateCloudReviewWorkflow("25", TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(1), reviewers.get(2),reviewers.get(3), reviewers.get(4) );

        // reviewers complete the task on cloud.
        completeTaskOnCloud(reviewers.get(1));
        completeTaskOnCloud(reviewers.get(2)); 
        completeTaskOnCloud(reviewers.get(3));
        completeTaskOnCloud(reviewers.get(4));
        
        // logged in by on prem user and remove the work flow
        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
        
        // Cancel workflow code need to be added 
        
        
        
    }
    
    @DataSetup(testLinkId = "9617")
    public void dataPrep_9617(WebDrone drone) throws Exception
    {
        logger.info("[Suite ] : Start Tests in: " + getTestName());

        this.drone = drone;
        siteName = getTestName();

        // This method creates site on Enterprise and sync OP user with cloud
        // user.
        createSiteAndSyncOPUser(siteName);

        // On cloud - create site. And invite reviewers on cloud site.
        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(1), siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(2), siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(3), siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(4), siteName, UserRole.COLLABORATOR);
        ShareUser.logout(hybridDrone);
    }
    *//**
     * Delete file which is been used in workflow and synced with cloud user.
     * *//*
    @Test(enabled = false)
    public void defaultHybridWorkflow2Reviewers_9617() throws InterruptedException
    {
        try
        {
            onPremUser = getUserNamePremiumDomain("9617");
            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);

            // Creating the doc for review
            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
            siteName = getTestName();
            //dataPrep_9617(drone);
            createAndUploadFile(fileName);

            taskName = getTestName();
            // login with an enterprise user and start a cloud task or review workflow.
            initiateCloudReviewWorkflow("50", TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(1), reviewers.get(2));

           ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
           DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
           docLibPage.deleteItem(fileName);
           Assert.assertFalse(docLibPage.isFileVisible(fileName));
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }  
    
    
    @DataSetup(testLinkId = "9617")
    public void dataPrep_9598(WebDrone drone) throws Exception
    {
        logger.info("[Suite ] : Start Tests in: " + getTestName());

        this.drone = drone;
        siteName = getTestName();

        // This method creates site on Enterprise and sync OP user with cloud
        // user.
        createSiteAndSyncOPUser(siteName);

        // On cloud - create site. And invite reviewers on cloud site.
        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(1), siteName, UserRole.COLLABORATOR);
        ShareUser.logout(hybridDrone);
    }
    
    @Test(enabled = false)
    public void defaultHybridWorkflow2Reviewers_9598() throws InterruptedException
    {
        try
        {
            onPremUser = getUserNamePremiumDomain("9598");
            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);

            // Creating the doc for review
            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
            siteName = getTestName();
            //dataPrep_9617(drone);
            createAndUploadFile(fileName);

            taskName = getTestName();
            // login with an enterprise user and start a cloud task or review work flow.
            initiateCloudReviewWorkflow("100", TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(1));
            
            //Add "LOCK ON PREM" on the document of workflow.

            //CANCEL WORKFLOW  CREATED BY CALLING CANCEL METHOD WHICH IS IN PROGRESS
            
            initiateCloudReviewWorkflow("100", TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(1));
            
            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
            
            MyTasksPage myTaskPage = openTask(hybridDrone);
           
            //  //CANCEL WORKFLOW  CREATED BY CALLING CANCEL METHOD WHICH IS IN PROGRESS
            // Should not be able to delete the work flow from task list.
                        
            // Open workflow page and delete the document from there and it should be able to delete. 
        }
        catch(Throwable e)
        {
            reportError(drone, testName, e);
        }
    }
    
    
    @DataSetup(testLinkId = "9610")
    public void dataPrep_9610(WebDrone drone) throws Exception
    {
        logger.info("[Suite ] : Start Tests in: " + getTestName());

        this.drone = drone;
        siteName = getTestName();

        // This method creates site on Enterprise and sync OP user with cloud
        // user.
        createSiteAndSyncOPUser(siteName);

        // On cloud - create site. And invite reviewers on cloud site.
        ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
        ShareUserMembers.inviteUserToSiteWithRole(hybridDrone, reviewers.get(0), reviewers.get(1), siteName, UserRole.COLLABORATOR);
        ShareUser.logout(hybridDrone);
           
    }
    
    @Test(enabled = false)
    public void testHybridWorkflow_9610() throws InterruptedException
    {
        try
        {
            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
            // Creating the doc for review
            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
            siteName = getTestName();
            // dataPrep_9617(drone);
            createAndUploadFile(fileName);

            taskName = getTestName();
            // login with an enterprise user and start a cloud task or review
            // work flow.
            initiateCloudReviewWorkflow("100", TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(1));

            // reviewers complete the task on cloud.
            completeTaskOnCloud(reviewers.get(1));
            
            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
            
            //unsync the document
           
            DashBoardPage dashBoardPage = (DashBoardPage) drone.getCurrentPage();
            MyProfilePage myProfilePage = dashBoardPage.getNav().selectMyProfile().render();
            CloudSyncPage cloudSyncPage = myProfilePage.selectCloudSyncPage().render();
            cloudSyncPage.disconnectCloudAccount();
            // Edit the document
            DocumentLibraryPage docLibPage =ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
            docLibPage.getContentRow(fileName).selectMoreAction();
            //edit element click is need to be implemented.
            
            // and sync back to same cloud.
            
        }
        catch (Throwable e)
        {

        }
    }
        //9612 Add the same document in different flow, This document was already been used by
        // different workflow.
        @DataSetup(testLinkId = "9612")
        public void dataPrep_9612(WebDrone drone) throws Exception
        {
            logger.info("[Suite ] : Start Tests in: " + getTestName());

            this.drone = drone;
            siteName = getTestName();

            // This method creates site on Enterprise and sync OP user with cloud
            // user.
            createSiteAndSyncOPUser(siteName);

            // On cloud - create site. And invite reviewers on cloud site.
            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);           
            ShareUser.logout(hybridDrone);
        }
        
        @Test(enabled = false)
        public void testHybridWorkFlow_9612() throws Exception
        {
            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
            // Creating the doc for review
            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
            siteName = getTestName();
            // dataPrep_9617(drone);
            createAndUploadFile(fileName);

            taskName = getTestName();
            // login with an enterprise user and start a cloud task or review
            // work flow.
            initiateCloudReviewWorkflow("100", TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(0));

            // reviewers complete the task on cloud.
            completeTaskOnCloud(reviewers.get(1));
            
            //start another workflow with some other user capture error message and assert
            initiateCloudReviewWorkflow("100", TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(1));

            
        }
        
        *//**9613
         * Edit workflow details after its been already created 
         * and active.
         * 
         * *//*
        @DataSetup(testLinkId = "9613")
        public void dataPrep_9613(WebDrone drone) throws Exception
        {
            logger.info("[Suite ] : Start Tests in: " + getTestName());

            this.drone = drone;
            siteName = getTestName();

            // This method creates site on Enterprise and sync OP user with cloud
            // user.
            createSiteAndSyncOPUser(siteName);

            // On cloud - create site. And invite reviewers on cloud site.
            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);            
            ShareUser.logout(hybridDrone);
        }
        
        @Test(enabled = false)
        public void testHybridWorkFlow_9613() throws Exception
        {
            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
            // Creating the doc for review
            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
            siteName = getTestName();
            // dataPrep_9617(drone);
            createAndUploadFile(fileName);

            taskName = getTestName();
            // login with an enterprise user and start a cloud task or review
            // work flow.
            initiateCloudReviewWorkflow("100", TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(0));

            // reviewers complete the task on cloud.
            completeTaskOnCloud(reviewers.get(0));
            
            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
            //go to "I'hv started workflow"
            
            //retrieve list of tasks/workflow
            
            //select the one just created and check if its editable.
        }
        
        *//**9613
         * Edit workflow details after its been already created 
         * and active.
         * 
         * *//*
        @DataSetup(testLinkId = "9616")
        public void dataPrep_9616(WebDrone drone) throws Exception
        {
            logger.info("[Suite ] : Start Tests in: " + getTestName());

            this.drone = drone;
            siteName = getTestName();

            // This method creates site on Enterprise and sync OP user with cloud
            // user.
            createSiteAndSyncOPUser(siteName);

            // On cloud - create site. And invite reviewers on cloud site.
            ShareUser.login(hybridDrone, reviewers.get(0), DEFAULT_PASSWORD);
            ShareUser.createSite(hybridDrone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);            
            ShareUser.logout(hybridDrone);
        }
        
        @Test(enabled = false)
        public void testHybridWorkFlow_9616() throws Exception
        {
            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
            // Creating the doc for review
            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";
            siteName = getTestName();
            // dataPrep_9617(drone);
            createAndUploadFile(fileName);

            taskName = getTestName();
            // login with an enterprise user and start a cloud task or review
            // work flow.
            initiateCloudReviewWorkflow("100", TaskType.CLOUD_REVIEW_TASK, fileName, reviewers.get(0));

            // reviewers complete the task on cloud.
            completeTaskOnCloud(reviewers.get(0));
            
            ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
            
            //go to My Tasks
            
            //retrieve list of tasks/workflow
            
            //select the one just created.
            
            //cancel the workflow which is been created.
            
            // Assert cancelled workflow shouldnt appear as part of workflow list.
            
        }
        
        
        

*/}

