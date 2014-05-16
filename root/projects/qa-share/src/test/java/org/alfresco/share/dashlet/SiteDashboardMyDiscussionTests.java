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

package org.alfresco.share.dashlet;

import static org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter.FOURTEEN_DAYS_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter.LAST_DAY_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter.SEVEN_DAYS_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter.TWENTY_EIGHT_DAYS_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter.ALL_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter.MY_TOPICS;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet;
import org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter;
import org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter;
import org.alfresco.po.share.dashlet.TopicStatusDetails;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet.LinkType;
import org.alfresco.po.share.dashlet.mydiscussions.CreateNewTopicPage;
import org.alfresco.po.share.dashlet.mydiscussions.DeleteTopicDialogPage;
import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;
import org.alfresco.po.share.dashlet.mydiscussions.TopicsListPage;
import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * 1) ALF_8894() - My Discussions dashlet. No topics created
 * 2) ALF_8896() - My Discussions dashlet. Create topic
 * 3) ALF_8899_8901() - My Discussions dashlet. Update topic. Reply to topic
 * 4) ALF_8903() - My Discussions dashlet. Delete topic
 * 5) ALF_8905() - My Discussions dashlet. New topic
 * 6) ALF_8910() - My Discussions dashlet. User's filter
 * 7) ALF_8913() - My Discussions dashlet. Date filter
 * 8) ALF_8944() - My Discussions dashlet. Link to user's profile
 * 9) ALF_8945() - My Discussions dashlet. Create topics in different sites
 * 10)ALF_8947() - My Discussions dashlet. Expand/narrow My Discussions dashlet
 *
 * @author jcule
 */
@Listeners(FailedTestListener.class)
public class SiteDashboardMyDiscussionTests extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SiteDashboardMyDiscussionTests.class);

    private static final String EMPTY_DASHLET_MESSAGE = "There are no topics matching your filters.";
    private static final String EXPECTED_HELP_BALOON_MESSAGES = "Discussion Forum dashlet.View your latest posts on the Discussion Forum.";
    private static final String CREATE_NEW_TOPIC_TITLE = "Create New Topic";
    private static final String EDIT_TOPIC_TITLE = "Edit Topic";
    private static final String TOPIC_DETAILS_TITLE_ONE = "topic title 1";
    private static final String TOPIC_DETAILS_TEXT_ONE = "topic text 1";
    private static final String TOPIC_DETAILS_TITLE_TWO = "topic title 2";
    private static final String TOPIC_DETAILS_TEXT_TWO = "topic text 2";
    private static final String TOPIC_DETAILS_TITLE_THREE = "topic title 3";
    private static final String TOPIC_DETAILS_TEXT_THREE = "topic text 3";
    private static final String TOPIC_DETAILS_CREATED_ON = "Created on:";
    private static final String NUMBER_OF_REPLIES = "There is 1 reply.";


    private String testUser;
    private String testUser1;
    private String testUser2;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        // create any user
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        testUser1 = testName + "InvitedUser1" + "@" + DOMAIN_FREE;
        String[] testUser1Info = new String[] { testUser1 };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1Info);

        testUser2 = testName + "InvitedUser2" + "@" + DOMAIN_FREE;
        String[] testUser2Info = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2Info);

        logger.info("Starting Tests: " + testName);
    }

    /**
     * My Discussions dashlet. No topics created
     * 1) Login as created user
     * 2) Create a site
     * 3) Open My Discussions dashlet
     * 4) Verify "There are no topics matching your filters." message is displayed
     * 5) Verify ? icon is present and click on the icon
     * 6) Verify baloon popup with Discussion Forum dashlet. View your latest posts on the Discussion Forum. is displayed
     * 7) Click X icon on baloon popup and check popup is hidden
     * 8) Verify drop-down menu with options My Topics, All Topics
     * 9) Verify drop-down menu: Topics updated in the last day; Topics updated in the last 7 days; Topics updated in the last 14 days, Topics updated in the
     * last 28 days;
     * 10) Verify New Topic button and click on the button
     * 11) Verify Create New Topic Page is displayed
     *
     * @throws Exception
     */
    @Test(groups = "SiteDashboardMyDiscussionTests")
    public void ALF_8894() throws Exception
    {
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Get My Discussions Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet);

        //verify dashlet is empty
        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertEquals(topicsTitles.size(), 0);

        //verify empty dashlet message - "There are no topics matching your filters." is displayed
        String emptyDashletMessage = myDiscussionsDashlet.getEmptyDashletMessage();
        Assert.assertEquals(emptyDashletMessage, EMPTY_DASHLET_MESSAGE);

        //verify ? icon is present and click on the icon
        Assert.assertTrue(myDiscussionsDashlet.isHelpButtonDisplayed());

        //verify baloon popup with Discussion Forum dashlet. View your latest posts on the Discussion Forum. is displayed
        myDiscussionsDashlet.clickHelpButton();
        Assert.assertTrue(myDiscussionsDashlet.isBalloonDisplayed());
        String actualHelpBallonMsg = myDiscussionsDashlet.getHelpBalloonMessage();
        Assert.assertEquals(actualHelpBallonMsg, EXPECTED_HELP_BALOON_MESSAGES);

        // click X icon on baloon popup and check popup is hidden
        myDiscussionsDashlet.closeHelpBallon();
        Assert.assertFalse(myDiscussionsDashlet.isBalloonDisplayed());

        // verify drop-down menu: My Topics, All Topics
        MyDiscussionsTopicsFilter currentTopicFilter = myDiscussionsDashlet.getCurrentTopicFilter();
        Assert.assertEquals(currentTopicFilter, MY_TOPICS);
        myDiscussionsDashlet.clickTopicsButtton();
        List<MyDiscussionsTopicsFilter> allTopicFilters = myDiscussionsDashlet.getTopicFilters();
        Assert.assertTrue(allTopicFilters.contains(ALL_TOPICS));
        Assert.assertTrue(allTopicFilters.contains(MY_TOPICS));

        // Drop-down menu: Topics updated in the last day; Topics updated in the last 7 days; Topics updated in the last 14 days, Topics updated in the last 28
        // days;
        MyDiscussionsHistoryFilter currentHistoryFilter = myDiscussionsDashlet.getCurrentHistoryFilter();
        Assert.assertEquals(currentHistoryFilter, LAST_DAY_TOPICS);
        myDiscussionsDashlet.clickHistoryButtton();
        List<MyDiscussionsHistoryFilter> allHistoryFilters = myDiscussionsDashlet.getHistoryFilters();
        Assert.assertTrue(allHistoryFilters.contains(LAST_DAY_TOPICS));
        Assert.assertTrue(allHistoryFilters.contains(SEVEN_DAYS_TOPICS));
        Assert.assertTrue(allHistoryFilters.contains(FOURTEEN_DAYS_TOPICS));
        Assert.assertTrue(allHistoryFilters.contains(TWENTY_EIGHT_DAYS_TOPICS));

        // verify New Topic button and click on the button
        Assert.assertTrue(myDiscussionsDashlet.isNewTopicLinkDisplayed());
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // verify Create New Topic Page is displayed
        Assert.assertNotNull(createNewTopicPage);
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);

        // logout
        ShareUser.logout(drone);
    }

    /**
     * My Discussions dashlet. Create topic
     * 1) Login as created user
     * 2) Create a site
     * 3) Add a topic for the site
     * 4) Verify topic's name link is displayed on My Discussion dashlet
     * 5) Click topic's name link
     * 6) Verify topic details page is displayed
     * 7) Click user's name link
     * 8) Verify User Profile page is displayed
     *
     * @throws Exception
     */
    @Test(groups = "SiteDashboardMyDiscussionTests")
    public void ALF_8896() throws Exception
    {
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Get My Discussions Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet);

        // add a topic for the site
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        TopicDetailsPage topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);

        //check why LName appears??????
        Assert.assertEquals(topicDetailsPage.getTopicAuthor(), testUser + " LName");
        Assert.assertFalse("".equalsIgnoreCase(topicDetailsPage.getTopicCreationDate()));

        // Verify topic's name link is displayed on the My Discussion dashlet
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        myDiscussionsDashlet = siteDashboardPage.getDashlet(MY_DISCUSSIONS).render();
        siteDashboardPage = myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();

        // Repeat search until the element is found or Timeout is hit
        //TODO: move somewhere to share po
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (title.isDisplayed())
            {
                break;
            }
        }

        // click topic's name link
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_ONE).click().render();

        // verify topic details page is displayed
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON);

        // click user's name link
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        myDiscussionsDashlet = siteDashboardPage.getDashlet(MY_DISCUSSIONS).render();
        siteDashboardPage = myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();
        MyProfilePage myProfilePage = myDiscussionsDashlet.selectTopicUser(testUser + " LName").click().render();

        // verify User Profile page is displayed
        Assert.assertNotNull(myProfilePage);
        Assert.assertTrue(myProfilePage.titlePresent());

        // logout
        ShareUser.logout(drone);

    }

    /**
     * My Discussions dashlet. Update topic
     * 1) Login as created user
     * 2) Create a site
     * 3) Add a topic for the site
     * 4) Update created topic
     * 5) Verify the following notification is displayed on My Discussion dashlet: [topic name's link] (Updated [time ago]) Created by [user name's link] about
     * [time ago].
     * 6) Click topic's name link
     * 7) Verify topic details page is displayed
     * 8) Click user's name link
     * 9) Verify User Profile page is displayed
     *
     * @throws Exception
     */
    /**
    @Test(groups = "SiteDashboardMyDiscussionTests")
    public void ALF_8899() throws Exception
    {
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Get My Discussions Dashlet
        ShareUserSitePage.addDashlet(drone, siteName, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserSitePage.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet);

        // add a topic for the site
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        TopicDetailsPage topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);

        // Update created topic
        createNewTopicPage = topicDetailsPage.clickOnEditLink().render();
        Assert.assertEquals(createNewTopicPage.getPageTitle(), EDIT_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE + " Updated");
        topicDetailsPage =  createNewTopicPage.saveTopic().render();

        // Post a reply to created topic
        topicDetailsPage.clickOnReplyLink().render();
        topicDetailsPage.clickOnCreateReply().render();

        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        drone.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE + " Updated")));
        myDiscussionsDashlet = siteDashboardPage.getDashlet(MY_DISCUSSIONS).render();

        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(topicsTitles);
        Assert.assertEquals(topicsTitles.size(), 1);
        Assert.assertEquals(topicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE + " Updated");


        // Verify the following notification is displayed: [topic name's link] (Updated [time ago]) Created by [user name's link] about [time ago].
        List<TopicStatusDetails> topicStatusDetails = myDiscussionsDashlet.getUpdatedTopics();
        String createdByJustNow = String.format("Created by %s just now.", this.getClass().getSimpleName() + "@" + DOMAIN_FREE + " LName");
        for (TopicStatusDetails topicStatusDetail : topicStatusDetails)
        {
            Assert.assertEquals(topicStatusDetail.getCreationTime(), createdByJustNow);
            Assert.assertEquals(topicStatusDetail.getUpdateTime(), UPDATED_JUST_NOW);
        }

        // click topic's name link
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_ONE + " Updated").click().render();

        // verify topic details page is displayed
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE + " Updated");
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON);

        // click user's name link
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        drone.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
        myDiscussionsDashlet = siteDashboardPage.getDashlet(MY_DISCUSSIONS).render();
        MyProfilePage myProfilePage = myDiscussionsDashlet.selectTopicUser(this.getClass().getSimpleName() + "@" + DOMAIN_FREE + " LName").click().render();

        // verify User Profile page is displayed
        Assert.assertNotNull(myProfilePage);
        Assert.assertTrue(myProfilePage.titlePresent());

        // logout
        ShareUser.logout(drone);
    }
    **/
    /**
     * My Discussions dashlet. Update topic. Reply to topic
     * 1) Login as created user
     * 2) Create a site
     * 3) Add a topic for the site
     * 4) Update topic
     * 5) Post a reply to created topic
     * 6) Verify the following notification is displayed on My Discussions dashlet: [topic name's link] Created by [user name's link] [time ago]. There is/are
     * [quantity] replies. The last reply was posted by [user name's link] [time ago].
     * 7) Click user's name link
     * 8) Verify User Profile page is displayed
     *
     * @throws Exception
     */
    @Test(groups = "SiteDashboardMyDiscussionTests")
    public void ALF_8899_8901() throws Exception
    {
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName,  SITE_VISIBILITY_PUBLIC);

        // Get My Discussions Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet);

        // add a topic for the site
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        TopicDetailsPage topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);

        // Update created topic
        createNewTopicPage = topicDetailsPage.clickOnEditLink().render();
        Assert.assertEquals(createNewTopicPage.getPageTitle(), EDIT_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE + " Updated");
        topicDetailsPage =  createNewTopicPage.saveTopic().render();

        // Post a reply to created topic
        topicDetailsPage.clickOnReplyLink().render();
        topicDetailsPage.clickOnCreateReply().render();

        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        myDiscussionsDashlet = siteDashboardPage.getDashlet(MY_DISCUSSIONS).render();
        siteDashboardPage = myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();

        // Repeat search until the element is found or Timeout is hit
        //TODO: move somewhere to share po ?????
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE + " Updated")));
            if (title.isDisplayed())
            {
                break;
            }
        }

        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(topicsTitles);
        Assert.assertEquals(topicsTitles.size(), 1);
        Assert.assertEquals(topicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE + " Updated");

        // Verify the following notification is displayed: [topic name's link] Created by [user name's link] [time ago]. There is/are [quantity] replies.
        // The last reply was posted by [user name's link] [time ago].

        List<TopicStatusDetails> topicStatusDetails = myDiscussionsDashlet.getUpdatedTopics();
        String createdBy = String.format("Created by %s ", this.getClass().getSimpleName() + "@" + DOMAIN_FREE + " LName");
        String replyDetails = String.format("The last reply was posted by %s ", this.getClass().getSimpleName() + "@" + DOMAIN_FREE + " LName");
        for (TopicStatusDetails topicStatusDetail : topicStatusDetails)
        {
            Assert.assertTrue(topicStatusDetail.getCreationTime().indexOf(createdBy) != -1);
            Assert.assertTrue(topicStatusDetail.getUpdateTime().indexOf("Updated") != -1);
            Assert.assertEquals(topicStatusDetail.getNumberOfReplies(), NUMBER_OF_REPLIES);
            Assert.assertTrue(topicStatusDetail.getReplyDetails().indexOf(replyDetails) != -1);
        }

        // click user's name link
        MyProfilePage myProfilePage = myDiscussionsDashlet.selectTopicUser(testUser + " LName").click().render();

        // verify User Profile page is displayed
        Assert.assertNotNull(myProfilePage);
        Assert.assertTrue(myProfilePage.titlePresent());

        // logout
        ShareUser.logout(drone);
    }

    /**
     * My Discussions dashlet. Delete topic
     * 1) Login as created user
     * 2) Create a site
     * 3) Add a topic for the site
     * 4) Delete the topic
     * 5) Verify the notification about deleted topic is not displayed on My Discussions dashlet.
     *
     * @throws Exception
     */
    @Test(groups = "SiteDashboardMyDiscussionTests")
    public void ALF_8903() throws Exception
    {
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Get My Discussions Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet);

        // add a topic for the site
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        TopicDetailsPage topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);

        // Delete the topic
        DeleteTopicDialogPage deleteTopicDialogPage = topicDetailsPage.clickOnDeleteLink().render();

        Assert.assertNotNull(deleteTopicDialogPage);
        TopicsListPage topicsListPage = deleteTopicDialogPage.clickOnDeleteButton().render();
        Assert.assertNotNull(topicsListPage);

        //verify the notification about deleted topic is not displayed in My Discussions dashlet.??????????

        //verify topic is not displayed in dashlet anymore
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        myDiscussionsDashlet = siteDashboardPage.getDashlet(MY_DISCUSSIONS).render();

        //verify empty dashlet message - "There are no topics matching your filters." is displayed
        String emptyDashletMessage = myDiscussionsDashlet.getEmptyDashletMessage();
        Assert.assertEquals(emptyDashletMessage, EMPTY_DASHLET_MESSAGE);

        // logout
        ShareUser.logout(drone);

    }

    /**
     * My Discussions dashlet. New topic
     * 1) Login as created user
     * 2) Create a site
     * 3) Open My Discussions dashlet
     * 4) Click on New Topic button
     * 5) Fill the required fields (Title and Text)
     * 6) Click "Save" button
     * 7) Go back to My Discussions dashlet and verify information about the created topic
     *
     * @throws Exception
     */
    @Test(groups = "SiteDashboardMyDiscussionTests")
    public void ALF_8905() throws Exception
    {
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Open My Discussions dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet);

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage
        TopicDetailsPage topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE);

        // Go back to My Discussions dashlet and verify information about the created topic

        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        myDiscussionsDashlet = siteDashboardPage.getDashlet(MY_DISCUSSIONS).render();

        //TODO: move somewhere to share po
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                siteDashboardPage = myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (title.isDisplayed())
            {
                break;
            }
        }

        List<ShareLink> topicsUsers = myDiscussionsDashlet.getTopics(LinkType.User);
        Assert.assertNotNull(topicsUsers);

        Assert.assertEquals(topicsUsers.size(), 1);
        Assert.assertEquals(topicsUsers.get(0).getDescription(), testUser + " LName");

        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(topicsTitles);
        Assert.assertEquals(topicsTitles.size(), 1);

        Assert.assertEquals(topicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        // logout
        ShareUser.logout(drone);

    }

    /**
     * My Discussions dashlet. User's filter
     * 1) Login as created user
     * 2) Create a site
     * 3) Create a topic1 for the site
     * 4) Invite user as e.g. contributor (any role except consumer)
     * 5) Invited user creates a topic2 for the site
     * 6) Site creator logs in and opens My Discussion dashlet on site dashboard
     * 7) Select "My Topics" value from drop-down menu
     * 8) Verify only topics created by site creator are displayed
     * 9) Select "All Topics" value from drop-down menu
     * 10) Verify all topics created by users of the site are displayed
     *
     * @throws Exception
     */
    @Test(groups = "SiteDashboardMyDiscussionTests")
    public void ALF_8910() throws Exception
    {
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Open My Discussions dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet);

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        TopicDetailsPage topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE);

        // invite user as e.g. contributor (any role except consumer)
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser1, siteName, UserRole.CONTRIBUTOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User logs in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        SiteDashboardPage siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        // invited user creates a topic2 for the site
        // Click on New Topic button
        createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_TWO);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO);

        // invited user logs out
        ShareUser.logout(drone);

        // site creator logs in and opens My Discussion dashlet on site dashboard
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        siteDashPage = myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();

        //Selected "My Topics" and verify only topics created by site creator are displayed
        // Repeat search until the element is found or Timeout is hit
        //TODO: move somewhere to share po
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (title.isDisplayed())
            {
                break;
            }
        }

        List<ShareLink> myTopicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(myTopicsTitles);
        Assert.assertEquals(myTopicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        siteDashPage = myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();

        //Selected "All Topics" and verify only topics created by site creator are displayed
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_TWO)));
            if (title.isDisplayed())
            {
                break;
            }
        }


        List<ShareLink> allTopicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(allTopicsTitles);
        Assert.assertEquals(allTopicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(allTopicsTitles.get(1).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        // logout
        ShareUser.logout(drone);

    }

    /**
     * My Discussions dashlet. Date filter
     * 1) Login as created user
     * 2) Create a site
     * 3) Create topics for the site
     * 4) Modify topics at different dates/times
     * 6) Select Topics updated in the last day value from drop-down menu
     * 7) Verify Topics updated in the last day are displayed
     * 8) Select Topics updated in the last 7 days value from drop-down menu
     * 9) Verify Topics updated in the last 7 days are displayed
     * 10) Select Topics updated in the last 14 days value from drop-down menu
     * 11) Verify Topics updated in the last 14 days are displayed
     * 12) Select Topics updated in the last 28 days value from drop-down menu
     * 13) Verify Topics updated in the last 28 days are displayed
     *
     * @throws Exception
     */
    @Test(groups = "SiteDashboardMyDiscussionTests")
    public void ALF_8913() throws Exception
    {
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Open My Discussions dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet);

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        TopicDetailsPage topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE);

        //create a topic2 for a site
        createNewTopicPage = topicDetailsPage.clickOnNewTopicLink().render();
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_TWO);
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO);

        //create a topic3 for a site
        createNewTopicPage = topicDetailsPage.clickOnNewTopicLink().render();
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_THREE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_THREE);
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_THREE);

        SiteDashboardPage siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        // Select Topics updated in the last day value from drop-down menu;
        myDiscussionsDashlet.clickHistoryButtton();
        myDiscussionsDashlet.selectTopicsHistoryFilter(LAST_DAY_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement titleThree = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_THREE)));
            WebElement titleTwo = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_TWO)));
            WebElement titleOne = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (titleThree.isDisplayed() && titleTwo.isEnabled() && titleOne.isDisplayed())
            {
                break;
            }
        }
        List<ShareLink> lastDayTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);

        // Verify Topics updated in the last day are displayed
        Assert.assertNotNull(lastDayTopics);
        Assert.assertEquals(lastDayTopics.size(), 3);
        Assert.assertEquals(lastDayTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(lastDayTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(lastDayTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        // Select Topics updated in the last 7 days value from drop-down menu
        myDiscussionsDashlet.clickHistoryButtton();
        myDiscussionsDashlet.selectTopicsHistoryFilter(SEVEN_DAYS_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement titleThree = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_THREE)));
            WebElement titleTwo = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_TWO)));
            WebElement titleOne = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (titleThree.isDisplayed() && titleTwo.isEnabled() && titleOne.isDisplayed())
            {
                break;
            }
        }
        List<ShareLink> sevenDaysTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);

        // Verify Topics updated in the last 7 days are displayed
        Assert.assertNotNull(sevenDaysTopics);
        Assert.assertEquals(sevenDaysTopics.size(), 3);
        Assert.assertEquals(sevenDaysTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(sevenDaysTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(sevenDaysTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        // Select Topics updated in the last 14 days value from drop-down menu
        myDiscussionsDashlet.clickHistoryButtton();
        myDiscussionsDashlet.selectTopicsHistoryFilter(FOURTEEN_DAYS_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement titleThree = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_THREE)));
            WebElement titleTwo = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_TWO)));
            WebElement titleOne = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (titleThree.isDisplayed() && titleTwo.isEnabled() && titleOne.isDisplayed())
            {
                break;
            }
        }
        List<ShareLink> fourteenDaysTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);

        // Verify Topics updated in the last 14 days are displayed
        Assert.assertNotNull(fourteenDaysTopics);
        Assert.assertEquals(fourteenDaysTopics.size(), 3);
        Assert.assertEquals(fourteenDaysTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(fourteenDaysTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(fourteenDaysTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        // Select Topics updated in the last 28 days value from drop-down menu
        myDiscussionsDashlet.clickHistoryButtton();
        myDiscussionsDashlet.selectTopicsHistoryFilter(TWENTY_EIGHT_DAYS_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement titleThree = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_THREE)));
            WebElement titleTwo = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_TWO)));
            WebElement titleOne = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (titleThree.isDisplayed() && titleTwo.isEnabled() && titleOne.isDisplayed())
            {
                break;
            }
        }
        List<ShareLink> twentyEightDaysTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);

        // Verify Topics updated in the last 28 days are displayed
        Assert.assertNotNull(twentyEightDaysTopics);
        Assert.assertEquals(twentyEightDaysTopics.size(), 3);
        Assert.assertEquals(twentyEightDaysTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(twentyEightDaysTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(twentyEightDaysTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        // logout
        ShareUser.logout(drone);

    }

    /**
     * My Discussions dashlet. Link to user's profile
     * 1) Login as admin
     * 2) Create a site
     * 3) Invite user1 as e.g. contributor (any role except consumer)
     * 4) Invite user2 as e.g. contributor (any role except consumer)
     * 5) User1 creates a topic1 for the site
     * 6) User2 creates a topic2 for the site
     * 7) Invited user creates a topic2 for the site
     * 8) Admin logs in, removes user1 from site and deletes user2 from share
     * 9) Open My Discussions dashlet
     * 9) Click on the user1 name link
     * 10) Verify user1 profile page is displayed correctly
     * 11) Click on the topic1 name link
     * 12) Verify that UserB name link is not active
     * 13) Click on the topic2 name link
     * 14) Verify topic2 details page is displayed correctly
     *
     * @throws Exception
     */
    @Test(groups = "SiteDashboardMyDiscussionTests")
    public void ALF_8944() throws Exception
    {
        // login as created user
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // create a site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet);

        // invite user1 as e.g. contributor (any role except consumer)
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser1, siteName, UserRole.CONTRIBUTOR);
        // invite user2 as e.g. contributor (any role except consumer)
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser2, siteName, UserRole.CONTRIBUTOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User1 logs in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Open My Discussions - dashlet create a topic1 for a site
        SiteDashboardPage siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        TopicDetailsPage topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE);

        //Invited User1 logs out
        ShareUser.logout(drone);

        // Invited User2 logs in
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // Open My Discussions - dashlet create a topic2 for a site
        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        // Click on New Topic button
        createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_TWO);

        // Click "Save" button on CreateNewTopicPage - create a topic2 for a site
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO);

        //Invited User2 logs out
        ShareUser.logout(drone);

        //admin logs in, removes User1 and deletes User2
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUserMembers.removeSiteMember(drone, testUser1, siteName);
        DashBoardPage dashboardPage = ShareUser.openUserDashboard(drone);
        UserSearchPage page = dashboardPage.getNav().getUsersPage().render();
        page = page.searchFor(testUser2).render();
        Assert.assertTrue(page.hasResults());
        UserProfilePage userProfile = page.clickOnUser(testUser2).render();
        page = userProfile.deleteUser().render();

        // Open My Discussions dashlet
        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        siteDashPage = myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_TWO)));
            if (title.isDisplayed())
            {
                break;
            }
        }

        List<ShareLink> allTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(allTopics);
        Assert.assertEquals(allTopics.size(), 2);
        Assert.assertEquals(allTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(allTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        // Click on the topic1 name link
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_ONE).click().render();

        // verify topic details page is displayed
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE);

        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        //Verify that invited user's name link is not active
        List<ShareLink> allUsers = myDiscussionsDashlet.getTopics(LinkType.User);
        Assert.assertEquals(allUsers.size(), 1);

        Assert.assertEquals(allUsers.get(0).getDescription(), testUser1 + " LName");
        MyProfilePage myProfilePage = myDiscussionsDashlet.selectTopicUser(testUser1 + " LName").click().render();

        // verify User Profile page is displayed
        Assert.assertNotNull(myProfilePage);
        Assert.assertTrue(myProfilePage.titlePresent());

        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        siteDashPage = myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_TWO)));
            if (title.isDisplayed())
            {
                break;
            }
        }

        // Click on the topic2 name link
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_TWO).click().render();

        // Verify topic2 details page is displayed correctly
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO);

        // logout
        ShareUser.logout(drone);

    }

    /**
     * My Discussions dashlet. Create topics in different sites
     * 1) Login as created user
     * 2) Create site1 and site2
     * 3) Add several topics for each site
     * 4) Open My Discussions dashlet ('All Topics' view and 'My Topics' view) for site1
     * 5) Verify only created topics in site1 are displayed in My Discussions dashlet
     *
     * @throws Exception
     */
    @Test(groups = "SiteDashboardMyDiscussionTests")
    public void ALF_8945() throws Exception
    {
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create site1
        String testName = getTestName();
        String siteName1 = testName + System.currentTimeMillis() + "-Site1";
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

        //create topic1 for site1 - Open My Discussions dashlet for site1
        ShareUserDashboard.addDashlet(drone, siteName1, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName1);
        Assert.assertNotNull(myDiscussionsDashlet);

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for site1
        TopicDetailsPage topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE);

        // create site2
        String siteName2 = testName + System.currentTimeMillis() + "-Site2";
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);

        // Open My Discussions dashlet for site2
        ShareUserDashboard.addDashlet(drone, siteName2, Dashlet.MY_DISCUSSIONS);
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName2);
        Assert.assertNotNull(myDiscussionsDashlet);

        // Click on New Topic button
        createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_TWO);

        // Click "Save" button on CreateNewTopicPage - create a topic2 for site2
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO);

        SiteDashboardPage siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName1));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        siteDashPage = myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (title.isDisplayed())
            {
                break;
            }
        }

        // Selected "My Topics" value from drop-down menu by default
        // Verify only topics created by site creator are displayed
        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(topicsTitles);
        Assert.assertEquals(topicsTitles.size(), 1);
        Assert.assertEquals(topicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        // Select "All Topics" value from drop-down menu
        siteDashPage = myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (title.isDisplayed())
            {
                break;
            }
        }
        List<ShareLink> allTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);

        // Verify all topics created by users of the site are displayed (no topic2)
        Assert.assertNotNull(allTopics);
        Assert.assertEquals(allTopics.size(), 1);
        Assert.assertEquals(allTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        // logout
        ShareUser.logout(drone);

    }

    /**
     * Expand/narrow My Discussions dashlet
     * 1) Login as created user
     * 2) Create site
     * 3) Add several topics for the site
     * 4) Open My Discussions dashlet and try to expand and narrow dashlet to possible size
     * 5) Verify that dashlet is expanded and narrowed, all items is correctly displayed
     *
     * @throws Exception
     */
    @Test(groups = "AlfrescoBug")
    public void ALF_8947() throws Exception
    {
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create site
        String testName = getTestName();
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create several topics for site
        // Open My Discussions dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet);

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        TopicDetailsPage topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE);

        SiteDashboardPage siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        drone.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        // invited user creates a topic2 for the site
        // Click on New Topic button
        createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_TWO);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO);

        // Try to expand dashlet to possible size
        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        siteDashPage = myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (title.isDisplayed())
            {
                break;
            }
        }

        myDiscussionsDashlet.resizeDashlet(0, +400);

        // Verify that all items is correctly displayed
        siteDashPage = myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement titleTwo = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_TWO)));
            WebElement titleOne = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (titleOne.isDisplayed() && titleTwo.isDisplayed())
            {
                break;
            }
        }
        List<ShareLink> expandedTopicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(expandedTopicsTitles);

        Assert.assertEquals(expandedTopicsTitles.size(), 2);
        Assert.assertEquals(expandedTopicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(expandedTopicsTitles.get(1).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        List<ShareLink> expandedTopicsUsers = myDiscussionsDashlet.getTopics(LinkType.User);
        Assert.assertNotNull(expandedTopicsUsers);
        Assert.assertEquals(expandedTopicsUsers.size(), 2);
        Assert.assertEquals(expandedTopicsUsers.get(0).getDescription(), testUser + " LName");
        Assert.assertEquals(expandedTopicsUsers.get(1).getDescription(), testUser + " LName");

        // Try to expand dashlet to possible size
        myDiscussionsDashlet.resizeDashlet(0, -400);

        // Verify that all items is correctly displayed
        siteDashPage = myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                webDriverWait(drone, refreshDuration);
                drone.refresh();
            }
            WebElement title = drone.findAndWaitWithRefresh(By.xpath(String.format("//span[@class='nodeTitle']//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE)));
            if (title.isDisplayed())
            {
                break;
            }
        }        List<ShareLink> narrowedTopicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(narrowedTopicsTitles);

        Assert.assertEquals(narrowedTopicsTitles.size(), 2);
        Assert.assertEquals(narrowedTopicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(narrowedTopicsTitles.get(1).getDescription(), TOPIC_DETAILS_TITLE_ONE);

        List<ShareLink>narrowedTopicsUsers = myDiscussionsDashlet.getTopics(LinkType.User);
        Assert.assertNotNull(narrowedTopicsUsers);
        Assert.assertEquals(narrowedTopicsUsers.size(), 2);
        Assert.assertEquals(narrowedTopicsUsers.get(0).getDescription(), testUser + " LName");
        Assert.assertEquals(narrowedTopicsUsers.get(1).getDescription(), testUser + " LName");

        // logout
        ShareUser.logout(drone);

    }

}
