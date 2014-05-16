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
    public void ALF_15136() throws Exception
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
     * ALF-15137:Form - Destination and Assignee (Simple Cloud Task)
     * ALF-15139:Form - Destination and Assignee (Simple Cloud Task) - Select Destination
     * ALF-15140:Form - Destination and Assignee (Simple Cloud Task) - Select Assignee
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
     * ALF-15137:Form - Destination and Assignee (Simple Cloud Task)
     * ALF-15139:Form - Destination and Assignee (Simple Cloud Task) - Select Destination
     * ALF-15140:Form - Destination and Assignee (Simple Cloud Task) - Select Assignee
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
    public void ALF_15137() throws Exception
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
     * ALF-15138:Form - Destination and Assignee (Cloud Review Task)
     * ALF-15141:Form - Destination and Assignee (Cloud Review Task) - Select Destination
     * ALF-15142:Form - Destination and Assignee (Cloud Review Task) - Select Reviewers
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
     * ALF-15138:Form - Destination and Assignee (Cloud Review Task)
     * ALF-15141:Form - Destination and Assignee (Cloud Review Task) - Select Destination
     * ALF-15142:Form - Destination and Assignee (Cloud Review Task) - Select Reviewers
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
    public void ALF_15138() throws Exception
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

    /**
     * ALF-15173:Simple Cloud Task - action execution after completion in Cloud and OP
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15173() throws Exception
    {
        dataPrep(getTestName());
    }

    /**
     * ALF-15173:Simple Cloud Task - action execution after completion in Cloud and OP
     */
    @Test (groups="Hybrid", enabled = true)
    public void ALF_15173() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String simpleTaskFile = getFileName(testName) + ".txt";
        String[] fileInfo = { simpleTaskFile, DOCLIB };

        String simpleTaskWF = testName + System.currentTimeMillis() + "-WF";

        String dueDate = getDueDateString();

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload a file
            siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

            // Select "Cloud Task or Review" from select a workflow dropdown
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, simpleTaskFile);

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setAssignee(cloudUser);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setMessage(simpleTaskWF);
            formDetails.setTaskType(TaskType.SIMPLE_CLOUD_TASK);

            // Create Workflow using the uploaded file
            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify the file is part of the workflow, and cloud synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the File1 is synced");
            Assert.assertTrue(checkIfContentIsSynced(drone, simpleTaskFile), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify Workflow has been created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(simpleTaskWF), "Verifying workflow exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Navigate to MyTasks page
            MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();

            // Verify tasks are displayed in Active Tasks list
            Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

            // Edit each task and mark them as completed
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

            // Verify tasks are NOT displayed in Active Tasks list any more
            Assert.assertFalse(myTasksPage.isTaskPresent(simpleTaskWF));

            // Verify tasks are displayed in Completed Tasks list
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

            // Open Site Document Library, verify all files are part of the workflow, and synced
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            Assert.assertTrue(documentLibraryPage.isFileVisible(simpleTaskFile), "Verifying File1 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the File1 is synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");

            ShareUser.logout(hybridDrone);

            // Login as OP user
            sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);


            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify all files are still synced and part of workflow
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the document is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the document is part of a workflow");

            // Open My Tasks page
            myTasksPage = sharePage.getNav().selectMyTasks().render();

            // Verify a new tasks are displayed for OP user in Active Tasks List
            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, simpleTaskWF));

            // Edit each task and mark them as completed
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, simpleTaskWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

            // Verify the tasks are disappeared from Active Tasks list
            Assert.assertFalse(myTasksPage.isTaskPresent(simpleTaskWF));

            // Select Completed tasks and verify the tasks are displayed
            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(simpleTaskWF));

            // Navigate to Workflows I've Started page and verify tasks are not displayed under Active Workflows page
            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(simpleTaskWF));

            // Select Completed Workflows and verify workflows are displayed
            myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(simpleTaskWF));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File1 is still Synced and not part of a workflow any more
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isCloudSynced(), "Verifying the document is NOT synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(simpleTaskFile).isPartOfWorkflow(), "Verifying the document is NOT part of a workflow");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify all documents are disappeared from Cloud site
            Assert.assertFalse(documentLibraryPage.isFileVisible(simpleTaskFile), "Verifying File1 exists");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-15174:Cloud Review Task - action execution after approval in Cloud and completion in OP
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15174() throws Exception
    {
        dataPrep(getTestName());
    }
    /**
     * ALF-15174:Cloud Review Task - action execution after approval in Cloud and completion in OP
     */
    @Test (groups="Hybrid", enabled = true)
    public void ALF_15174() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String cloudReviewApproveFile = getFileName(testName) + ".txt";
        String[] fileInfo = { cloudReviewApproveFile, DOCLIB };

        String cloudReviewApproveWF = testName + System.currentTimeMillis() + "-WF";

        String dueDate = getDueDateString();
        int requiredApprovalPercentage = 100;

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 3 files
            siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setMessage(cloudReviewApproveWF);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);

            // Select "Cloud Task or Review" from select a workflow dropdown
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, cloudReviewApproveFile);

            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify all files are part of the workflow, and cloud synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isCloudSynced(), "Verifying the File2 is synced");
            Assert.assertTrue(checkIfContentIsSynced(drone, cloudReviewApproveFile), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify Workflows are created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(cloudReviewApproveWF), "Verifying workflow2 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Navigate to MyTasks page
            MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();

            // Verify tasks are displayed in Active Tasks list
            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewApproveWF));

            // Edit each task and mark them as completed
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, cloudReviewApproveWF, TaskStatus.COMPLETED, EditTaskAction.APPROVE);

            // Verify tasks are NOT displayed in Active Tasks list any more
            Assert.assertFalse(myTasksPage.isTaskPresent(cloudReviewApproveWF));

            // Verify tasks are displayed in Completed Tasks list
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewApproveWF));

            // Open Site Document Library, verify all files are part of the workflow, and synced
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            Assert.assertTrue(documentLibraryPage.isFileVisible(cloudReviewApproveFile), "Verifying File2 exists");

            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isCloudSynced(), "Verifying the File2 is synced");

            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");

            ShareUser.logout(hybridDrone);

            // Login as OP user
            sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);


            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify all files are still synced and part of workflow
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isCloudSynced(), "Verifying the document is synced");

            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isPartOfWorkflow(), "Verifying the document is part of a workflow");


            // Open My Tasks page
            myTasksPage = sharePage.getNav().selectMyTasks().render();

            // Verify a new tasks are displayed for OP user in Active Tasks List
            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, cloudReviewApproveWF));

            // Edit each task and mark them as completed
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, cloudReviewApproveWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);


            // Verify the tasks are disappeared from Active Tasks list
            Assert.assertFalse(myTasksPage.isTaskPresent(cloudReviewApproveWF));

            // Select Completed tasks and verify the tasks are displayed
            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewApproveWF));

            // Navigate to Workflows I've Started page and verify tasks are not displayed under Active Workflows page
            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(cloudReviewApproveWF));

            // Select Completed Workflows and verify workflows are displayed
            myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(cloudReviewApproveWF));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File2 is NOT Synced and NOT part of a workflow
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isCloudSynced(), "Verifying the document is NOT synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewApproveFile).isPartOfWorkflow(), "Verifying the document is NOT part of a workflow");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify all documents are disappeared from Cloud site
            Assert.assertFalse(documentLibraryPage.isFileVisible(cloudReviewApproveFile), "Verifying File2 exists");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-15175:Cloud Review Task - action execution after rejection in Cloud and completion in OP
     */

    @Test(groups="DataPrepHybrid")
    public void dataPrep_15175() throws Exception
    {
        dataPrep(getTestName());
    }

    /**
     * ALF-15175:Cloud Review Task - action execution after rejection in Cloud and completion in OP
     */

    @Test (groups="Hybrid", enabled = true)
    public void ALF_15175() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String cloudReviewRejectFile = getFileName(testName) + "-3.txt";
        String[] fileInfo3 = { cloudReviewRejectFile, DOCLIB };

        String cloudReviewRejectWF = testName + System.currentTimeMillis() + "-3-WF";

        String dueDate = getDueDateString();
        int requiredApprovalPercentage = 100;

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 3 files
            siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo3).render();

            // Select "Cloud Task or Review" from select a workflow dropdown
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, cloudReviewRejectFile);


            // Create Workflow3 using File3 (After Completion: Delete content on cloud and remove sync)
            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setMessage(cloudReviewRejectWF);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);

            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify all files are part of the workflow, and cloud synced
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isPartOfWorkflow(), "Verifying the File3 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isCloudSynced(), "Verifying the File3 is synced");
            Assert.assertTrue(checkIfContentIsSynced(drone, cloudReviewRejectFile), "Verifying the Sync Status is \"Synced\"");


            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            // Verify Workflows are created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(cloudReviewRejectWF), "Verifying workflow3 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            SharePage sharePage = ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Navigate to MyTasks page
            MyTasksPage myTasksPage = sharePage.getNav().selectMyTasks().render();

            // Verify tasks are displayed in Active Tasks list
            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewRejectWF));

            // Edit each task and mark them as completed
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, cloudReviewRejectWF, TaskStatus.COMPLETED, EditTaskAction.REJECT);

            // Verify tasks are NOT displayed in Active Tasks list any more
            Assert.assertFalse(myTasksPage.isTaskPresent(cloudReviewRejectWF));

            // Verify tasks are displayed in Completed Tasks list
            myTasksPage = myTasksPage.selectCompletedTasks().render();
            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewRejectWF));

            // Open Site Document Library, verify all files are part of the workflow, and synced
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            Assert.assertTrue(documentLibraryPage.isFileVisible(cloudReviewRejectFile), "Verifying File3 exists");

            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isCloudSynced(), "Verifying the File3 is synced");

            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isPartOfWorkflow(), "Verifying the File3 is part of a workflow");

            ShareUser.logout(hybridDrone);

            // Login as OP user
            sharePage = ShareUser.login(drone, user1, DEFAULT_PASSWORD);


            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify all files are still synced and part of workflow
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isCloudSynced(), "Verifying the document is synced");

            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isPartOfWorkflow(), "Verifying the document is part of a workflow");


            // Open My Tasks page
            myTasksPage = sharePage.getNav().selectMyTasks().render();

            // Verify a new tasks are displayed for OP user in Active Tasks List
            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, cloudReviewRejectWF));

            // Edit each task and mark them as completed
            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, cloudReviewRejectWF, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);


            // Verify the tasks are disappeared from Active Tasks list
            Assert.assertFalse(myTasksPage.isTaskPresent(cloudReviewRejectWF));

            // Select Completed tasks and verify the tasks are displayed
            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(cloudReviewRejectWF));

            // Navigate to Workflows I've Started page and verify tasks are not displayed under Active Workflows page
            myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);

            Assert.assertFalse(myWorkFlowsPage.isWorkFlowPresent(cloudReviewRejectWF));

            // Select Completed Workflows and verify workflows are displayed
            myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();

            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(cloudReviewRejectWF));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File3 is NOT Synced and NOT part of a workflow
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isCloudSynced(), "Verifying the document is NOT synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(cloudReviewRejectFile).isPartOfWorkflow(), "Verifying the document is NOT part of a workflow");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify all documents are disappeared from Cloud site
            Assert.assertFalse(documentLibraryPage.isFileVisible(cloudReviewRejectFile), "Verifying File3 does NOT exist");

            ShareUser.logout(hybridDrone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-15176:Lock on-premise content - ON
     * ALF-15177:Lock on-premise content - OFF
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15176() throws Exception
    {
        dataPrep(getTestName());
    }

    /**
     * ALF-15176:Lock on-premise content - ON
     * ALF-15177:Lock on-premise content - OFF
     * <ul>
     * <li>1) Login as User1 (Cloud) and Create a site</li>
     * <li>2) Login as User1 (OP), create a site and upload 2 documents</li>
     * <li>3) TODO - COMPLETE AFTER REVIEW </li>
     * <li>4) </li>
     * <li>5) </li>
     * <li>6) </li>
     * <li>8) </li>
     * <li>9) </li>
     * <li>10) </li>
     * <li>11) </li>
     * <li>12) </li>
     * <li>13) </li>
     * <li>14) </li>
     * <li>15) </li>
     * </ul>
     */
    @Test (groups="Hybrid", enabled = true)
    public void ALF_15176() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName1 = getFileName(testName) + "-1.txt";
        String[] fileInfo1 = { fileName1, DOCLIB };

        String workFlowName1 = testName + System.currentTimeMillis() + "-1-WF";
        String dueDate = getDueDateString();
        int requiredApprovalPercentage = 100;

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files
            siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo1).render();

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);
            formDetails.setMessage(workFlowName1);
            formDetails.setLockOnPremise(true);

            // Select Start WorkFlow from Document Library Page
            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName1);

            documentLibraryPage = cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isLocked(), "Verifying the File1 is Locked");
            Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(fileName1).getContentInfo(), "This document is locked by you.", "Verifying Locked message");
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName1), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            // Verify Workflows are created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName1), "Verifying workflow1 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File1 exists in Site Document Library, it is Synced and part of workflow.
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName1), "Verifying File1 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the document is synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the document is part of workflow");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isLocked(), "Verifying the document is NOT locked");

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName1, TaskStatus.COMPLETED, EditTaskAction.APPROVE);

            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName1));

            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName1, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName1));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName1));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isPartOfWorkflow(), "Verifying the File1 is part of a workflow");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isCloudSynced(), "Verifying the File1 is synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName1).isLocked(), "Verifying the File1 is Locked");

            ShareUser.logout(drone);
        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }

    /**
     * ALF-15177:Lock on-premise content - OFF
     * <ul>
     * <li>1) Create a OP User (User1)</li>
     * <li>2) Create 2 Cloud Users (cloudUser, Reviewer1)</li>
     * <li>3) Login as OP User and set up Cloud Sync with cloudUser</li>
     * </ul>
     */
    @Test(groups="DataPrepHybrid")
    public void dataPrep_15177() throws Exception
    {
        dataPrep(getTestName());
    }

    @Test (groups="Hybrid", enabled = true)
    public void ALF_15177() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameForDomain(testName, testDomain);
        String cloudUser = getUserNameForDomain(testName, testDomain);
        String opSiteName = getSiteName(testName) + System.currentTimeMillis() + "-OP";
        String cloudSite = getSiteName(testName) + System.currentTimeMillis() + "-CL";

        String fileName2 = getFileName(testName) + "-2.txt";
        String[] fileInfo2 = { fileName2, DOCLIB };

        String workFlowName2 = testName + System.currentTimeMillis() + "-2-WF";
        String dueDate = getDueDateString();
        int requiredApprovalPercentage = 100;

        try
        {
            // Login as User1 (Cloud)
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);
            // Create Site
            ShareUser.createSite(hybridDrone, cloudSite, SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);
            // Create Site
            SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, opSiteName, SITE_VISIBILITY_PUBLIC);
            // Open Document library, Upload 2 files
            siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2).render();

            WorkFlowFormDetails formDetails = new WorkFlowFormDetails();

            formDetails.setMessage(workFlowName2);
            List<String> userNames = new ArrayList<String>();
            userNames.add(cloudUser);
            formDetails.setDueDate(dueDate);
            formDetails.setTaskType(TaskType.CLOUD_REVIEW_TASK);
            formDetails.setTaskPriority(Priority.MEDIUM);
            formDetails.setSiteName(cloudSite);
            formDetails.setReviewers(userNames);
            formDetails.setContentStrategy(KeepContentStrategy.DELETECONTENT);
            formDetails.setApprovalPercentage(requiredApprovalPercentage);
            formDetails.setLockOnPremise(false);

            CloudTaskOrReviewPage cloudTaskOrReviewPage = ShareUserWorkFlow.startWorkFlowFromDocumentLibraryPage(drone, fileName2);
            documentLibraryPage =  cloudTaskOrReviewPage.startWorkflow(formDetails).render();

            // Verify File2 is Cloud Synced, part of workflow and it is NOT Locked
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isLocked(), "Verifying the File2 is NOT Locked");
            Assert.assertTrue(checkIfContentIsSynced(drone, fileName2), "Verifying the Sync Status is \"Synced\"");

            MyWorkFlowsPage myWorkFlowsPage = ShareUserWorkFlow.navigateToMyWorkFlowsPage(drone);
            // Verify Workflows are created successfully
            Assert.assertTrue(myWorkFlowsPage.isWorkFlowPresent(workFlowName2), "Verifying workflow2 exists");

            ShareUser.logout(drone);

            // Login as CloudUser User
            ShareUser.login(hybridDrone, cloudUser, DEFAULT_PASSWORD);

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(hybridDrone, cloudSite).render();

            // Verify File2 exists in Site Document library, it is Synced and part of workflow.
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName2), "Verifying File2 exists");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the document is NOT synced");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the document is NOT synced");

            // Navigate to MyTasks page and verify both tasks are present
            MyTasksPage myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(hybridDrone);

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName2));

            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(hybridDrone, workFlowName2, TaskStatus.COMPLETED, EditTaskAction.APPROVE);

            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName2));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName2));

            ShareUser.logout(hybridDrone);

            // Login as User1 (OP)
            ShareUser.login(drone, user1, DEFAULT_PASSWORD);

            myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone);

            Assert.assertTrue(ShareUser.checkIfTaskIsPresent(drone, workFlowName2));

            myTasksPage = ShareUserWorkFlow.completeTaskFromMyTasksPage(drone, workFlowName2, TaskStatus.COMPLETED, EditTaskAction.TASK_DONE);

            Assert.assertFalse(myTasksPage.isTaskPresent(workFlowName2));

            myTasksPage = myTasksPage.selectCompletedTasks().render();

            Assert.assertTrue(myTasksPage.isTaskPresent(workFlowName2));

            // Open Site Document Library
            documentLibraryPage = SiteUtil.openSiteDocumentLibraryURL(drone, opSiteName).render();

            // Verify File1 is Cloud Synced, part of workflow and it is Locked
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isPartOfWorkflow(), "Verifying the File2 is part of a workflow");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isCloudSynced(), "Verifying the File2 is synced");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName2).isLocked(), "Verifying the File2 is Locked");

            ShareUser.logout(drone);

        }
        catch (Throwable t)
        {
            reportError(drone, testName + "-ENT", t);
            reportError(hybridDrone, testName + "-CL", t);
        }
    }
}