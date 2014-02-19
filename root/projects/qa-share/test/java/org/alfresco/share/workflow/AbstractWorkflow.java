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

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareTestProperty;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Abhijeet Bharade
 * 
 */
public class AbstractWorkflow extends AbstractTests
{
    protected String onPremUser = "hybriduser";
    // protected String onPremUser = "onPremUser";
    protected String cloudUser;
    protected String siteName = "";
    protected String siteNameForSyncing = "hybrid4_site";
    // protected String siteNameForSyncing = "hybrid";
    protected DocumentLibraryPage libPage;
	// protected InviteMembersPage membersPage;
    protected String taskName;
    protected SharePage sharePage;
    protected String reviewer1 = "hybridflow3@test.com";
    protected String reviewer2 = "hybridflow3@test.com";

    private static Log logger = LogFactory.getLog(AbstractWorkflow.class);

    /**
     * 
     */
    public AbstractWorkflow()
    {
        super();
    }

	/**
	 * Class includes: Tests from TestLink in Area: Advanced Search Tests
	 */
	@BeforeClass
	@Override
	public void setup() throws Exception {
		super.setup();
		hybridDrone = (WebDrone) ctx.getBean("hybridWebDrone");
		hybridShareTestProperties = (ShareTestProperty) ctx.getBean("hybridShareTestProperties");
		dronePropertiesMap.put(hybridDrone, hybridShareTestProperties);
	}

	/*   *//**
	 * Use cloud URL to complete the task.
	 * 
	 * @param user
	 * @param password
	 */
	/*
	 * protected void completeTaskOnCloud(String user, String password) {
	 * WebDrone tempDrone = new WebDroneImpl(((WebDroneImpl) drone).getDriver(),
	 * AlfrescoVersion.Cloud); WebDroneUtil.loginAs(tempDrone,
	 * cloudUrlForHybrid, user, password);
	 * 
	 * try { sharePage = openTask(tempDrone); } catch (InterruptedException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); } sharePage =
	 * completeTask((MyTasksPage) sharePage, taskName); ShareUser.logout(drone);
	 * tempDrone = null; }
	 *//**
	 * Assumes user has just logged in and is on dashboard.
	 * 
	 * @param drone
	 * @throws InterruptedException
	 * 
	 *//*
    protected MyTasksPage openTask(WebDrone drone) throws InterruptedException
    {
        if (logger.isTraceEnabled())
            logger.trace("openTask - " + taskName);
        DashBoardPage dashBoard = drone.getCurrentPage().render();

        MyTasksPage myTasksPage = dashBoard.getNav().selectMyTasks().render(90000);
        if (!drone.getAlfrescoVersion().isCloud())
        {
            drone.find(By.cssSelector("a[rel='completed']")).click();
            drone.refresh();
            myTasksPage.render(90000);
            drone.find(By.cssSelector("a[rel='active']")).click();
            myTasksPage.render(90000);
        }
        assertTrue(myTasksPage.getTitle().contains(PAGE_TITLE_MYTASKS));
        return myTasksPage;
    }

    *//**
     * Assumes flow is on My task page. Opens the given task and completes it.
     * 
     * @param myTasksPage
     * @return
     *//*
    protected SharePage completeTask(MyTasksPage myTasksPage, String taskName)
    {
        EditTaskPage editTasksPage = null;
        logger.trace("completeTask - " + taskName);

        editTasksPage = myTasksPage.navigateToEditTaskPage(taskName).render(10000);
        editTasksPage.selectStatusDropDown(TaskStatus.COMPLETED);
        sharePage = editTasksPage.selectTaskDoneButton().render(30000);

        return sharePage;
    }

    *//**
     * Assumes flow is on My task page
     * 
     * @param myTasksPage
     * @return
     *//*
    protected SharePage reAssignTask(MyTasksPage myTasksPage, String reassignee) throws Exception
    {
        EditTaskPage editTasksPage = myTasksPage.navigateToEditTaskPage(taskName).render();

        SelectAssigneePage saPage = editTasksPage.selectReassignButton().render();
        List<AssigneeResultsRow> searchResultList = saPage.searchPeople(reassignee);
        assertTrue(searchResultList.size() > 0, "Users should be found.");

        for (AssigneeResultsRow user : searchResultList)
        {
            if (user.getUsername().contains(reassignee))
            {
                return saPage.selectUserAsAssignee(user, reassignee);
            }
        }
        throw new Exception("User not found");
    }*/

    /**
     * Creates a file and uploads it.
     * 
     * @param fileName
     * @throws Exception
     */
    protected void createAndUploadFile(String fileName) throws Exception
    {
        String[] fileInfo = { fileName, DOCLIB };

        // Open Site DashBoard
        ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
        HtmlPage htmlPage = drone.getCurrentPage();

        if (!(htmlPage instanceof SiteDashboardPage))
        {
            ShareUser.openSiteDashboard(drone, siteName);
        }

        // uploadFile
        libPage = ShareUser.uploadFileInFolder(drone, fileInfo);
    }

	@Test
	public void dualDroneTest() throws Exception {

		// Creating 2 users
		String user1 = "CoreUser" + System.currentTimeMillis(), user2 = "CloudUser" + System.currentTimeMillis();

		CreateUserAPI.CreateActivateUser(hybridDrone, user2 + "@example.com", new String[] { user2 + "@example.com" });
		CreateUserAPI.CreateActivateUser(drone, user1 + "@example.com",
				new String[] { user1 });

		ShareUser.login(drone, username, password);
		ShareUser.login(hybridDrone, user2 + "@example.com", DEFAULT_PASSWORD);

		siteNameForSyncing += System.currentTimeMillis();
		ShareUser.createSite(drone, siteNameForSyncing, SITE_VISIBILITY_PUBLIC);
		ShareUser.createSite(hybridDrone, siteNameForSyncing, SITE_VISIBILITY_PUBLIC);

		ShareUser.logout(drone);
		ShareUser.logout(hybridDrone);

	}

	@AfterClass
	@Override
	public void tearDown()
    {
		super.tearDown();
        if (logger.isTraceEnabled())
        {
			logger.trace("shutting hybrid web drone");
        }
        // Close the browser
		if (hybridDrone != null)
        {
			ShareUtil.logout(hybridDrone);
			hybridDrone.quit();
        }
    }

	/*  *//**
	 * Purpose of this method is to login with an enterprise user and start
	 * a cloud task or review workflow.
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	/*
	 * protected void initiateCloudReviewWorkflow(String[] reviewers, String
	 * approvalPercentage, TaskType taskType, String fileName) throws
	 * InterruptedException { sharePage = drone.getCurrentPage().render();
	 * 
	 * // OP - Login // ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
	 * sharePage.getNav().selectMyDashBoard();
	 * assertTrue(sharePage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
	 * 
	 * // Start workflow from library StartWorkFlowPage workFlowPage =
	 * startWorkFLow(fileName).render();
	 * assertTrue(workFlowPage.isBrowserTitle("Start workflow"));
	 * 
	 * // Create cloud/review workflow CloudTaskOrReviewPage
	 * cloudTaskOrReviewPage = (CloudTaskOrReviewPage) ((CloudTaskOrReviewPage)
	 * workFlowPage
	 * .getWorkflowPage(WorkFlowType.CLOUD_TASK_OR_REVIEW)).render();
	 * assertTrue(cloudTaskOrReviewPage.isBrowserTitle("Start workflow"));
	 * 
	 * 
	 * libPage = submitFormDetails(cloudTaskOrReviewPage, siteNameForSyncing,
	 * reviewers, StringUtils.isEmpty(approvalPercentage) ? "50" :
	 * approvalPercentage, taskName);
	 * 
	 * assertTrue(libPage.isBrowserTitle(PAGE_TITLE_DOCLIB));
	 * 
	 * // logout ShareUser.logout(drone); }
	 *//**
	 * This method goes into doc lib and creates
	 * 
	 */
	/*
	 * private StartWorkFlowPage startWorkFLow(String fileName) { // Visit
	 * document library DocumentLibraryPage libPage =
	 * ShareUser.openSitesDocumentLibrary(drone, siteName);
	 * assertTrue(libPage.isBrowserTitle(PAGE_TITLE_DOCLIB));
	 * 
	 * DocumentDetailsPage documentDetailsPage = libPage.selectFile(fileName);
	 * return documentDetailsPage.selectStartWorkFlowPage().render(); }
	 *//**
	 * Method to sign in Cloud page and return Cloud Sync page
	 * 
	 * @return boolean
	 */
	/*
	 * protected CloudSyncPage signInToCloud(final String username, final String
	 * password) { final By SIGN_IN_BUTTON = By.cssSelector(
	 * "button#template_x002e_user-cloud-auth_x002e_user-cloud-auth_x0023_default-button-signIn-button"
	 * ); drone.findAndWait(SIGN_IN_BUTTON).click(); CloudSignInPage
	 * cloudSignInPage = new CloudSignInPage(drone);
	 * cloudSignInPage.loginAs(username, password); return (CloudSyncPage)
	 * drone.getCurrentPage(); }
	 *//**
	 * Creates the bean for {@link WorkFlowFormDetails} and creates a new
	 * workflow.
	 * 
	 * @param cloudTaskOrReviewPage
	 * @param siteName
	 * @param cloudUser
	 * @param approvalPercentage
	 * @param message
	 * @return
	 * @throws InterruptedException
	 */
	/*
	 * public DocumentLibraryPage submitFormDetails(CloudTaskOrReviewPage
	 * cloudTaskOrReviewPage, String siteName, String[] cloudUser, String
	 * approvalPercentage, String message) throws InterruptedException {
	 * WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
	 * formDetails.setSiteName(siteName); formDetails.setReviewers(cloudUser);
	 * formDetails.setMessage(taskName);
	 * cloudTaskOrReviewPage.selectTask(TaskType.CLOUD_REVIEW_TASK); return
	 * cloudTaskOrReviewPage.startWorkflow(formDetails).render(maxWaitTime); }
	 *//**
	 * @param drone
	 * @throws Exception
	 */
	/*
	 * protected void initialUsersCreation(WebDrone drone) throws Exception { //
	 * TODO create new domain. onPremUser = "onPremUser" +
	 * System.currentTimeMillis() + "@" + DOMAIN_FREE; reviewer1 = "reviewer1" +
	 * System.currentTimeMillis() + "@" + DOMAIN_FREE; reviewer2 = "reviewer2" +
	 * System.currentTimeMillis() + "@" + DOMAIN_FREE;
	 * 
	 * // Create OP user cloud and enterprise account
	 * CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] {
	 * onPremUser }); if (CreateUserAPI.createCloudUser(onPremUser, onPremUser,
	 * onPremUser, onPremUser, DEFAULT_PASSWORD, true))
	 * CreateUserAPI.upgradeCloudAccount(ADMIN_USERNAME, DOMAIN_FREE, "1000");
	 * 
	 * // creating 2 reviewers. CreateUserAPI.createCloudUser(reviewer1,
	 * reviewer1, reviewer1, reviewer1, DEFAULT_PASSWORD, true);
	 * CreateUserAPI.createCloudUser(reviewer2, reviewer2, reviewer2, reviewer2,
	 * DEFAULT_PASSWORD, true);
	 * 
	 * cloudSyncOPUser(drone);
	 * 
	 * // Creating site on cloud for WebDrone tempDrone = new
	 * WebDroneImpl(((WebDroneImpl) drone).getDriver(), AlfrescoVersion.Cloud);
	 * WebDroneUtil.loginAs(tempDrone, cloudUrlForHybrid, onPremUser,
	 * DEFAULT_PASSWORD); ShareUser.createSite(tempDrone, siteNameForSyncing,
	 * SITE_VISIBILITY_PUBLIC);
	 * 
	 * // reviewer1 and reviewer2 joining siteNameForSyncing
	 * ShareUserMembers.userJoinsSite(tempDrone, reviewer1, DOMAIN_FREE,
	 * siteNameForSyncing, UserRole.CONTRIBUTOR, true);
	 * ShareUserMembers.userJoinsSite(tempDrone, reviewer2, DOMAIN_FREE,
	 * siteNameForSyncing, UserRole.CONTRIBUTOR, true);
	 * 
	 * ShareUser.logout(tempDrone); }
	 *//**
	 * Syncing the Enterprise account and cloud account of OP user.
	 * 
	 * @param drone
	 */
	/*
	 * private void cloudSyncOPUser(WebDrone drone) { // Clould sync OP user to
	 * cloud user. ShareUser.login(drone, onPremUser, DEFAULT_PASSWORD);
	 * DashBoardPage dashBoard = (DashBoardPage) drone.getCurrentPage();
	 * MyProfilePage myProfilePage =
	 * dashBoard.getNav().selectMyProfile().render();
	 * myProfilePage.getNav().selectCloudSyncPage().render();
	 * signInToCloud(onPremUser, DEFAULT_PASSWORD).render();
	 * ShareUser.logout(drone); // Syncing complete }
	 */

}