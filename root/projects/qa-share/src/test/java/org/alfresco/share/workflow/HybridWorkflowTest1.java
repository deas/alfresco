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

package org.alfresco.share.workflow;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.TaskStatus;
import org.alfresco.po.share.workflow.AssignmentPage;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.share.util.AbstractWorkflow;
import org.alfresco.share.util.EditTaskAction;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Ranjith Manyam
 * 
 */
@Listeners(FailedTestListener.class)
public class HybridWorkflowTest1 extends AbstractWorkflow
{
    private String testDomain;
    
    /**
     * Class includes: Tests from TestLink in Area: Workflow
     */
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = DOMAIN_HYBRID;
    }

    public void dataPrep(String testName) throws Exception
    {
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] { user1 };

        String cloudUser = getUserNameForDomain(testName, testDomain);
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

    @Test (groups="Hybrid", enabled = true)
    public void AONE_15593() throws Exception
    {
        // Login as OP user
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Start Simple Cloud Task Workflow
        CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

        Assert.assertTrue(cloudTaskOrReviewPage.isCloudReviewTaskElementsPresent(), "Verifying Cloud Task or Review fields displayed");

        cloudTaskOrReviewPage.clickHelpIcon();
        Assert.assertEquals(cloudTaskOrReviewPage.getHelpText(), "This field must have between 0 and 250 characters.");
        cloudTaskOrReviewPage.clickHelpIcon();

        cloudTaskOrReviewPage.selectTask(TaskType.SIMPLE_CLOUD_TASK);
        Assert.assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK));

        cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK);
        Assert.assertTrue(cloudTaskOrReviewPage.isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK));

        String manualEntryDueDate = new DateTime().plusDays(5).toString("dd/MM/yyyy");

        cloudTaskOrReviewPage.enterDueDateText(manualEntryDueDate);
        Assert.assertEquals(cloudTaskOrReviewPage.getDueDate(), manualEntryDueDate);

        cloudTaskOrReviewPage.selectDateFromCalendar(getDueDateString());
        Assert.assertEquals(cloudTaskOrReviewPage.getDueDate(), getDueDateString());

        List<String> options = cloudTaskOrReviewPage.getPriorityOptions();
        Assert.assertEquals(options.size(), Priority.values().length);
        Assert.assertTrue(options.contains(Priority.HIGH.getPriority()));
        Assert.assertTrue(options.contains(Priority.LOW.getPriority()));
        Assert.assertTrue(options.contains(Priority.MEDIUM.getPriority()));

        cloudTaskOrReviewPage.selectPriorityDropDown(Priority.LOW);
        Assert.assertEquals(cloudTaskOrReviewPage.getSelectedPriorityOption(), Priority.LOW);

        ShareUser.logout(drone);
    }

    /**
     * AONE-15594:Form - Destination and Assignee (Simple Cloud Task)
     * AONE-15596:Form - Destination and Assignee (Simple Cloud Task) - Select Destination
     * AONE-15597:Form - Destination and Assignee (Simple Cloud Task) - Select Assignee
     * <ul>
     *     <li>1) Create OP user</li>
     *     <li>2) Create Cloud user</li>
     *     <li>3) Login to OP, set up Cloud Sync with Cloud user</li>
     *     <li>4) Login to Cloud, Create a site, create a folder within the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15137() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "opUser", testDomain);
        String[] userInfo1 = new String[] {user1};

        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);
        String[] cloudUserInfo1 = new String[] {cloudUser};

        String cloudSite = getSiteName(testName) + "-CL";
        String folderName = getFolderName(testName);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as cloudUser (Cloud) and create a site, a folder within the site
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName);
        ShareUser.logout(hybridDrone);
    }

    /**
     * AONE-15594:Form - Destination and Assignee (Simple Cloud Task)
     * AONE-15596:Form - Destination and Assignee (Simple Cloud Task) - Select Destination
     * AONE-15597:Form - Destination and Assignee (Simple Cloud Task) - Select Assignee
     * <ul>
     *     <li>1) Login to OP, Start Simple Cloud Task Workflow</li>
     *     <li>2) Verify Destination Network, Site and Folder default values are "(None)"</li>
     *     <li>3) Verify the Select Assignee button is disabled when the destination is not chosen and no assignee is displayed</li>
     *     <li>4) Select Destination And Verify Destination and Assignee title</li>
     *     <li>5) Verify Destination Network, Site, Folder are displayed</li>
     *     <li>6) Select Close and verify the Destination is not updated</li>
     *     <li>7) Select Destination And Assignee and choose destination</li>
     *     <li>8) Verify the Destination details are updated</li>
     *     <li>9) Select Assignee, Search with Empty string and verify warning message</li>
     *     <li>10) Search for a user and Verify Format of the user displayed in search results</li>
     *     <li>11) Verify Add Icon is present</li>
     *     <li>12) Select the user and verify the user is added and Add icon is disappeared</li>
     *     <li>13) Select Remove user and verify the user is removed and add icon is present</li>
     *     <li>14) Select user, select close and Verify Assignee is not present in workflow form</li>
     *     <li>15) Select select Assignee, verify the user is still selected (Displayed in selected users list)</li>
     *     <li>16) Click on Cancel button and verify Assignee is not present</li>
     *     <li>17) Select select Assignee, Verify the user is still in selected users list</li>
     *     <li>18) Select OK Button</li>
     *     <li>19) Verify Assignee is present and name matches</li>
     * </ul>
     */
    @Test (groups="Hybrid", enabled = true)
    public void AONE_15594() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName + "opUser", testDomain);
        String cloudUser = getUserNameForDomain(testName + "cloudUser", testDomain);

        String cloudSite = getSiteName(testName) + "-CL";
        String folderName = getFolderName(testName);

        try
        {
            // Login as OP user
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Start Simple Cloud Task Workflow
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startSimpleCloudTaskWorkFlow(drone);

            Assert.assertTrue(cloudTaskOrReviewPage.isSimpleCloudTaskElementsPresent(), "Verifying Simple Cloud Task fields");

            // Verify Destination Network, Site and Folder default values are "(None)"
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), NONE, "Verifying Destination Network default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), NONE, "Verifying Destination Site default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), NONE, "Verifying Destination Folder default value is set to None");

            // Verify the Select Assignee button is disabled when the destination is not chosen and no assignee is displayed
            Assert.assertFalse(cloudTaskOrReviewPage.isSelectAssigneeButtonEnabled(), "Verifying the Select Assignee button is disabled when the destination is not chosen");
            Assert.assertFalse(cloudTaskOrReviewPage.isAssigneePresent(), "Verifying the Assignee is not present");
            
            // Select Destination And Assignee but DO NOT choose destination (15139)
            DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

            // Verify Destination and Assignee title
            Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), "Select destination for documents on Cloud");

            // Verify Destination Network, Site, Folder are displayed
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Verifying Cloud Network is displayed");
            destinationAndAssigneePage.selectNetwork(testDomain);

            Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSite), "Verifying Cloud Site is displayed");
            destinationAndAssigneePage.selectSite(cloudSite);
            Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(DEFAULT_FOLDER_NAME), "Verifying Default Folder (Documents) is displayed");
            Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(folderName), "Verifying Folder created under Documents is displayed");
            destinationAndAssigneePage.selectFolder(folderName);

            // Select Close and verify the Destination is not updated
            destinationAndAssigneePage.selectCloseButton();
            cloudTaskOrReviewPage.render();

            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), NONE, "Verifying Destination Network default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), NONE, "Verifying Destination Site default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), NONE, "Verifying Destination Folder default value is set to None");

            Assert.assertFalse(cloudTaskOrReviewPage.isSelectAssigneeButtonEnabled(), "Verifying the Select Assignee button is disabled when the destination is not chosen");

            // Select Destination And Assignee and choose destination
            destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
            destinationAndAssigneePage.selectNetwork(testDomain);
            destinationAndAssigneePage.selectSite(cloudSite);
            destinationAndAssigneePage.selectFolder(folderName);
            destinationAndAssigneePage.selectSubmitButtonToSync();
            cloudTaskOrReviewPage.render();

            // Verify the Destination details are updated
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), testDomain, "Verifying Destination Network default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), cloudSite, "Verifying Destination Site default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), DEFAULT_FOLDER_NAME+"/"+folderName, "Verifying Destination Folder default value is set to None");

            Assert.assertTrue(cloudTaskOrReviewPage.isSelectAssigneeButtonEnabled(), "Verifying the Select Assignee button is enabled when the destination is not chosen");

            // Select Assignee (15140)

            AssignmentPage assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
            Assert.assertTrue(assignmentPage.isEnterASearchTermMessageDisplayed());

            // Search with Empty string and verify warning message
            assignmentPage.clearSearchField();
            assignmentPage.selectSearchButton();
            Assert.assertEquals(assignmentPage.getWarningMessage(), "Enter at least 1 character(s) to search");

            // Search for a user and Verify Format of the user displayed in search results
            List<String> userList = assignmentPage.getUserList(cloudUser);
            Assert.assertEquals(userList.size(), 1, "Verifying search returned one result");
            Assert.assertEquals(userList.get(0).split(" ")[0], cloudUser, "Verifying First Name");
            Assert.assertEquals(userList.get(0).split(" ")[1], DEFAULT_LASTNAME, "Verifying Last Name");
            Assert.assertEquals(userList.get(0).split(" ")[2].trim(), "("+cloudUser+")", "Verifying Last Name");

            // Verify Add Icon is present
            Assert.assertTrue(assignmentPage.isAddIconPresent(cloudUser), "Verifying Add icon is present");

            // Select the user and verify the user is added and Add icon is disappeared
            assignmentPage.selectUser(cloudUser);
            Assert.assertTrue(assignmentPage.isUserSelected(cloudUser), "Verifying User is Selected");
            Assert.assertFalse(assignmentPage.isAddIconPresent(cloudUser), "Verifying Add icon is NOT present");

            // Select Remove user and verify the user is removed and add icon is present
            assignmentPage.removeUser(cloudUser);
            Assert.assertFalse(assignmentPage.isUserSelected(cloudUser), "Verifying User is Selected");
            Assert.assertTrue(assignmentPage.isAddIconPresent(cloudUser), "Verifying Add icon is NOT present");

            // Select user and select close
            assignmentPage.selectUser(cloudUser);
            Assert.assertTrue(assignmentPage.isUserSelected(cloudUser), "Verifying User is Selected");
            assignmentPage.selectCloseButton();
            cloudTaskOrReviewPage.render();
            // Verify Assignee is not present
            Assert.assertFalse(cloudTaskOrReviewPage.isAssigneePresent());

            // Select select Assignee, verify the user is still selected (Displayed in selected users list)
            assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
            Assert.assertTrue(assignmentPage.isUserSelected(cloudUser), "Verifying User is Selected");
            // Click on Cancel button and verify Assignee is not present
            assignmentPage.selectCancelButton();
            cloudTaskOrReviewPage.render();
            Assert.assertFalse(cloudTaskOrReviewPage.isAssigneePresent());

            // Select select Assignee, Verify the user is still in selected users list
            assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
            Assert.assertTrue(assignmentPage.isUserSelected(cloudUser), "Verifying User is Selected");
            // Select OK Button
            assignmentPage.selectOKButton();
            cloudTaskOrReviewPage.render();

            // Verify Assignee is present and name matches
            Assert.assertTrue(cloudTaskOrReviewPage.isAssigneePresent());
            Assert.assertTrue(cloudTaskOrReviewPage.getAssignee().contains("("+cloudUser+")"));


            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }
    }

    /**
     * AONE-15595:Form - Destination and Assignee (Cloud Review Task)
     * AONE-15598:Form - Destination and Assignee (Cloud Review Task) - Select Destination
     * AONE-15599:Form - Destination and Assignee (Cloud Review Task) - Select Reviewers
     * <ul>
     *     <li>1) Create OP user</li>
     *     <li>2) Create Cloud user</li>
     *     <li>3) Create 2 Cloud reviewers</li>
     *     <li>4) Login to OP, set up Cloud Sync with Cloud user</li>
     *     <li>5) Login to Cloud, Create a site, create a folder within the site</li>
     *     <li>6) Invite Reviewer1 and Reviewer2 to the site</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15138() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String[] userInfo1 = new String[] {user1};

        String cloudUser = getUserNameForDomain(testName, testDomain);
        String[] cloudUserInfo1 = new String[] {cloudUser};

        String reviewer1 = getUserNameForDomain(testName + "-1", testDomain).replace("user", "reviewer");
        String[] reviewerInfo1 = new String[] {reviewer1};

        String reviewer2 = getUserNameForDomain(testName + "-2", testDomain).replace("user", "reviewer");
        String[] reviewerInfo2 = new String[] {reviewer2};

        String cloudSite = getSiteName(testName) + "-CL";
        String folderName = getFolderName(testName);

        // Create User1 (On-premise)
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, userInfo1);

        // Create User1 (Cloud)
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, cloudUserInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo1);
        CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, reviewerInfo2);
        CreateUserAPI.upgradeCloudAccount(hybridDrone, ADMIN_USERNAME, testDomain, "1000");

        // Login to User1, set up the cloud sync
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        signInToAlfrescoInTheCloud(drone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.logout(drone);

        // Login as cloudUser (Cloud) and create a site, a folder within the site
        ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
        ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(hybridDrone, folderName, folderName).render();
        ShareUser.logout(hybridDrone);

        // Invite Cloud reviewers to join the site
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer1, getSiteShortname(cloudSite), "SiteContributor", "");
        CreateUserAPI.inviteUserToSiteWithRoleAndAccept(hybridDrone, cloudUser, reviewer2, getSiteShortname(cloudSite), "SiteContributor", "");
    }

    /**
     * AONE-15595:Form - Destination and Assignee (Cloud Review Task)
     * AONE-15598:Form - Destination and Assignee (Cloud Review Task) - Select Destination
     * AONE-15599:Form - Destination and Assignee (Cloud Review Task) - Select Reviewers
     * <ul>
     *     <li>1) Login to OP, Start Cloud Review Task Workflow</li>
     *     <li>2) Verify Destination Network, Site and Folder default values are "(None)"</li>
     *     <li>3) Verify the Select Reviewers button is disabled when the destination is not chosen and no Reviewers displayed</li>
     *     <li>4) Select Destination And Verify Destination and Assignee title</li>
     *     <li>5) Verify Destination Network, Site, Folder are displayed</li>
     *     <li>6) Select Close and verify the Destination is not updated</li>
     *     <li>7) Select Destination And Assignee and choose destination</li>
     *     <li>8) Verify the Destination details are updated</li>
     *     <li>9) Select Reviewers, Search with Empty string and verify warning message</li>
     *     <li>10) Search for a user and Verify Format of the user displayed in search results</li>
     *     <li>11) Verify Add Icon is present</li>
     *     <li>12) Select the Reviewers and verify the reviewers are added and Add icon is disappeared</li>
     *     <li>13) Remove both users and verify the users are removed and add icon is present</li>
     *     <li>14) Select both reviewers, select close and Verify Reviewers not present in workflow form</li>
     *     <li>15) Select select Reviewers, verify the both reviewers are still selected (Displayed in selected users list)</li>
     *     <li>16) Click on Cancel button and verify Reviewers not present</li>
     *     <li>17) Select select Reviewers, Verify the reviewers are still in selected users list</li>
     *     <li>18) Select OK Button</li>
     *     <li>19) Verify Reviewers are present and names matches</li>
     * </ul>
     */
    @Test (groups="Hybrid", enabled = true)
    public void AONE_15595() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String reviewer1 = getUserNameForDomain(testName + "-1", testDomain).replace("user", "reviewer");
        String reviewer2 = getUserNameForDomain(testName + "-2", testDomain).replace("user", "reviewer");

        String cloudSite = getSiteName(testName) + "-CL";
        String folderName = getFolderName(testName);

        try
        {
            // Login as OP user
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            // Start Simple Cloud Task Workflow
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startCloudReviewTaskWorkFlow(drone);

            Assert.assertTrue(cloudTaskOrReviewPage.isCloudReviewTaskElementsPresent(), "Verifying Cloud Review Task fields");

            // Verify Destination Network, Site and Folder default values are "(None)"
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), NONE, "Verifying Destination Network default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), NONE, "Verifying Destination Site default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), NONE, "Verifying Destination Folder default value is set to None");

            // Verify the Select Assignee button is disabled when the destination is not chosen and no assignee is displayed
            Assert.assertFalse(cloudTaskOrReviewPage.isSelectReviewersButtonEnabled(), "Verifying the Select Reviewers button is disabled when the destination is not chosen");
            Assert.assertFalse(cloudTaskOrReviewPage.isReviewersPresent(), "Verifying the Reviewers not present");

            Assert.assertEquals(cloudTaskOrReviewPage.getRequiredApprovalPercentageHelpText(), APPROVAL_PERCENTAGE_HELP_TEXT, "Verify Approval Percentage Help Text");

            // Select Destination And Assignee but DO NOT choose destination (15141)
            DestinationAndAssigneePage destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();

            // Verify Destination and Assignee title
            Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), "Select destination for documents on Cloud");

            // Verify Destination Network, Site, Folder are displayed
            Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(testDomain), "Verifying Cloud Network is displayed");
            destinationAndAssigneePage.selectNetwork(testDomain);

            Assert.assertTrue(destinationAndAssigneePage.isSiteDisplayed(cloudSite), "Verifying Cloud Site is displayed");
            destinationAndAssigneePage.selectSite(cloudSite);
            Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(DEFAULT_FOLDER_NAME), "Verifying Default Folder (Documents) is displayed");
            Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed(folderName), "Verifying Folder created under Documents is displayed");
            destinationAndAssigneePage.selectFolder(folderName);

            // Select Close and verify the Destination is not updated
            destinationAndAssigneePage.selectCloseButton();
            cloudTaskOrReviewPage.render();

            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), NONE, "Verifying Destination Network default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), NONE, "Verifying Destination Site default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), NONE, "Verifying Destination Folder default value is set to None");

            Assert.assertFalse(cloudTaskOrReviewPage.isSelectReviewersButtonEnabled(), "Verifying the Select Assignee button is disabled when the destination is not chosen");

            // Select Destination And Assignee and choose destination
            destinationAndAssigneePage = cloudTaskOrReviewPage.selectDestinationAndAssigneePage().render();
            destinationAndAssigneePage.selectNetwork(testDomain);
            destinationAndAssigneePage.selectSite(cloudSite);
            destinationAndAssigneePage.selectFolder(folderName);
            destinationAndAssigneePage.selectSubmitButtonToSync();
            cloudTaskOrReviewPage.render();

            // Verify the Destination details are updated
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationNetwork(), testDomain, "Verifying Destination Network default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationSite(), cloudSite, "Verifying Destination Site default value is set to None");
            Assert.assertEquals(cloudTaskOrReviewPage.getDestinationFolder(), DEFAULT_FOLDER_NAME+"/"+folderName, "Verifying Destination Folder default value is set to None");

            Assert.assertTrue(cloudTaskOrReviewPage.isSelectReviewersButtonEnabled(), "Verifying the Select Assignee button is enabled when the destination is not chosen");

            // Select Assignee (15142)
            AssignmentPage assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
            Assert.assertTrue(assignmentPage.isEnterASearchTermMessageDisplayed());

            // Search with Empty string and verify warning message
            assignmentPage.clearSearchField();
            assignmentPage.selectSearchButton();
            Assert.assertEquals(assignmentPage.getWarningMessage(), "Enter at least 1 character(s) to search");

            // Search for a user and Verify Format of the user displayed in search results
            List<String> userList = assignmentPage.getUserList(reviewer1);
            Assert.assertEquals(userList.size(), 1, "Verifying search returned one result");
            Assert.assertEquals(userList.get(0).split(" ")[0], reviewer1, "Verifying First Name");
            Assert.assertEquals(userList.get(0).split(" ")[1], DEFAULT_LASTNAME, "Verifying Last Name");
            Assert.assertEquals(userList.get(0).split(" ")[2].trim(), "("+reviewer1+")", "Verifying Last Name");

            // Verify Add Icon is present
            Assert.assertTrue(assignmentPage.isAddIconPresent(reviewer1), "Verifying Add icon is present");

            // Select the user and verify the user is added and Add icon is disappeared
            List<String> reviewers = new ArrayList<String>();
            reviewers.add(reviewer1);
            reviewers.add(reviewer2);

            assignmentPage.selectUsers(reviewers);
            Assert.assertTrue(assignmentPage.isUserSelected(reviewer1), "Verifying reviewer1 is Selected");
            Assert.assertTrue(assignmentPage.isUserSelected(reviewer2), "Verifying reviewer2 is Selected");
            Assert.assertFalse(assignmentPage.isAddIconPresent(reviewer1), "Verifying Add icon is NOT present");
            Assert.assertFalse(assignmentPage.isAddIconPresent(reviewer2), "Verifying Add icon is NOT present");

            // Select Remove user and verify the user is removed and add icon is present
            assignmentPage.removeUsers(reviewers);
            Assert.assertFalse(assignmentPage.isUserSelected(reviewer1), "Verifying User is NOT Selected");
            Assert.assertFalse(assignmentPage.isUserSelected(reviewer2), "Verifying User is NOT Selected");
            Assert.assertTrue(assignmentPage.isAddIconPresent(reviewer1), "Verifying Add icon is present");
            Assert.assertTrue(assignmentPage.isAddIconPresent(reviewer2), "Verifying Add icon is present");

            // Select user and select close
            assignmentPage.selectUsers(reviewers);
            Assert.assertTrue(assignmentPage.isUserSelected(reviewer1), "Verifying reviewer1 is Selected");
            Assert.assertTrue(assignmentPage.isUserSelected(reviewer2), "Verifying reviewer2 is Selected");
            assignmentPage.selectCloseButton();
            cloudTaskOrReviewPage.render();
            // Verify Assignee is not present
            Assert.assertFalse(cloudTaskOrReviewPage.isReviewersPresent());

            // Select select Assignee, verify the user is still selected (Displayed in selected users list)
            assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
            Assert.assertTrue(assignmentPage.isUserSelected(reviewer1), "Verifying reviewer1 is Selected");
            Assert.assertTrue(assignmentPage.isUserSelected(reviewer2), "Verifying reviewer2 is Selected");
            // Click on Cancel button and verify Assignee is not present
            assignmentPage.selectCancelButton();
            cloudTaskOrReviewPage.render();
            Assert.assertFalse(cloudTaskOrReviewPage.isReviewersPresent());

            // Select select Assignee, Verify the user is still in selected users list
            assignmentPage = cloudTaskOrReviewPage.selectAssignmentPage().render();
            Assert.assertTrue(assignmentPage.isUserSelected(reviewer1), "Verifying reviewer1 is Selected");
            Assert.assertTrue(assignmentPage.isUserSelected(reviewer2), "Verifying reviewer2 is Selected");
            // Select OK Button
            assignmentPage.selectOKButton();
            cloudTaskOrReviewPage.render();

            // Verify Assignee is present and name matches
            Assert.assertTrue(cloudTaskOrReviewPage.isReviewersPresent());
            Assert.assertEquals(cloudTaskOrReviewPage.getReviewers().size(), reviewers.size(), "Verify the size of reviewers added");
            Assert.assertTrue(cloudTaskOrReviewPage.getReviewers().get(0).contains("("+reviewer1+")"), "Verify reviewer1 is present");
            Assert.assertTrue(cloudTaskOrReviewPage.getReviewers().get(1).contains("("+reviewer2+")"), "Verify reviewer1 is present");


            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
        }
    }

}