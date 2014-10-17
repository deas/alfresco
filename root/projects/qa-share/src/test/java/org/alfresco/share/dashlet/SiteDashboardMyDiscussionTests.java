/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.dashlet;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet.LinkType;
import org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter;
import org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter;
import org.alfresco.po.share.dashlet.TopicStatusDetails;
import org.alfresco.po.share.dashlet.mydiscussions.CreateNewTopicPage;
import org.alfresco.po.share.dashlet.mydiscussions.DeleteTopicDialogPage;
import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter.*;
import static org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter.ALL_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter.MY_TOPICS;

/**
 * 1) AONE_14754() - My Discussions dashlet. No topics created
 * 2) AONE_14755() - My Discussions dashlet. Create topic
 * 3) AONE_14756_8901() - My Discussions dashlet. Update topic. Reply to topic
 * 4) AONE_14758() - My Discussions dashlet. Delete topic
 * 5) AONE_14760() - My Discussions dashlet. New topic
 * 6) AONE_14761() - My Discussions dashlet. User's filter
 * 7) AONE_14759() - My Discussions dashlet. Date filter
 * 8) AONE_14762() - My Discussions dashlet. Link to user's profile
 * 9) AONE_14763() - My Discussions dashlet. Create topics in different sites
 * 10)AONE_14764() - My Discussions dashlet. Expand/narrow My Discussions dashlet
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

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
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
    @Test(groups = "AlfrescoOne")
    public void AONE_14754() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Get My Discussions Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet, "My Discussions Dashlet isn't displayed");

        // verify dashlet is empty
        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertEquals(topicsTitles.size(),0, "My Discussions dashlet isn't empty");

        // verify empty dashlet message - "There are no topics matching your filters." is displayed
        String emptyDashletMessage = myDiscussionsDashlet.getEmptyDashletMessage();
        Assert.assertEquals(emptyDashletMessage, EMPTY_DASHLET_MESSAGE, "Message 'There are no topics matching your filters.' isn't displayed");

        // verify ? icon is present and click on the icon
        Assert.assertTrue(myDiscussionsDashlet.isHelpButtonDisplayed(), "Help icon isn't displayed");

        // verify baloon popup with Discussion Forum dashlet. View your latest posts on the Discussion Forum. is displayed
        myDiscussionsDashlet.clickHelpButton();
        Assert.assertTrue(myDiscussionsDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        String actualHelpBallonMsg = myDiscussionsDashlet.getHelpBalloonMessage();
        Assert.assertEquals(actualHelpBallonMsg, EXPECTED_HELP_BALOON_MESSAGES, "Expected baloon message isn't displayed");

        // click X icon on baloon popup and check popup is hidden
        myDiscussionsDashlet.closeHelpBallon();
        Assert.assertFalse(myDiscussionsDashlet.isBalloonDisplayed(), "Baloon popup is displayed");

        // verify drop-down menu: My Topics, All Topics
        MyDiscussionsTopicsFilter currentTopicFilter = myDiscussionsDashlet.getCurrentTopicFilter();
        Assert.assertEquals(currentTopicFilter, MY_TOPICS, "Current filter isn't 'My Topics'");
        myDiscussionsDashlet.clickTopicsButtton();
        List<MyDiscussionsTopicsFilter> allTopicFilters = myDiscussionsDashlet.getTopicFilters();
        Assert.assertTrue(allTopicFilters.contains(ALL_TOPICS), "Filter isn't contain 'All Topics'");
        Assert.assertTrue(allTopicFilters.contains(MY_TOPICS), "Filter isn't contain 'My Topics'");

        // Drop-down menu: Topics updated in the last day; Topics updated in the last 7 days; Topics updated in the last 14 days, Topics updated in the last 28
        // days;
        MyDiscussionsHistoryFilter currentHistoryFilter = myDiscussionsDashlet.getCurrentHistoryFilter();
        Assert.assertEquals(currentHistoryFilter, LAST_DAY_TOPICS, "Current history filter isn't '" + LAST_DAY_TOPICS + "'");
        myDiscussionsDashlet.clickHistoryButtton();
        List<MyDiscussionsHistoryFilter> allHistoryFilters = myDiscussionsDashlet.getHistoryFilters();
        Assert.assertTrue(allHistoryFilters.contains(LAST_DAY_TOPICS), "History filter isn't contain '" + LAST_DAY_TOPICS + "'");
        Assert.assertTrue(allHistoryFilters.contains(SEVEN_DAYS_TOPICS), "History filter isn't contain '" + SEVEN_DAYS_TOPICS + "'");
        Assert.assertTrue(allHistoryFilters.contains(FOURTEEN_DAYS_TOPICS), "History filter isn't contain '" + FOURTEEN_DAYS_TOPICS + "'");
        Assert.assertTrue(allHistoryFilters.contains(TWENTY_EIGHT_DAYS_TOPICS), "History filter isn't contain '" + TWENTY_EIGHT_DAYS_TOPICS + "'");

        // verify New Topic button and click on the button
        Assert.assertTrue(myDiscussionsDashlet.isNewTopicLinkDisplayed(), "New Topic button isn't displayed");
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // verify Create New Topic Page is displayed

        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");

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
    @Test(groups = "AlfrescoOne")
    public void AONE_14755() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Get My Discussions Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);

        // add a topic for the site
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

        // check why LName appears??????
        Assert.assertEquals(topicDetailsPage.getTopicAuthor(), testUser + " LName", "Author isn't displayed");
        Assert.assertFalse("".equalsIgnoreCase(topicDetailsPage.getTopicCreationDate()), "Topic creation date isn't diplayed");

        // Verify topic's name link is displayed on the My Discussion dashlet
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();

        // Repeat search until the element is found or Timeout is hit
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        // click topic's name link
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_ONE).click().render();

        // verify topic details page is displayed

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON, "Expected info " + TOPIC_DETAILS_CREATED_ON + " isn't presented");

        // click user's name link
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);

        myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);

        MyProfilePage myProfilePage = myDiscussionsDashlet.selectTopicUser(testUser + " LName").click().render();

        // verify User Profile page is displayed
        Assert.assertTrue(myProfilePage.titlePresent(), "Expected profile title isn't presented");

        ShareUser.logout(drone);
    }

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
    @Test(groups = "AlfrescoOne")
    public void AONE_14756() throws Exception
    {
        String newTopicName = TOPIC_DETAILS_TITLE_ONE + " Updated";

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Get My Discussions Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);

        // add a topic for the site
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");

        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

        // Update created topic
        createNewTopicPage = topicDetailsPage.clickOnEditLink().render();
        Assert.assertEquals(createNewTopicPage.getPageTitle(), EDIT_TOPIC_TITLE, "Expected page title isn't presented");

        createNewTopicPage.enterTopicTitle(newTopicName);
        topicDetailsPage = createNewTopicPage.saveTopic().render();

        // Post a reply to created topic
        topicDetailsPage.clickOnReplyLink().render();
        topicDetailsPage.clickOnCreateReply().render();

        ShareUser.openSiteDashboard(drone, siteName);
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone);
        myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();

        // Repeat search until the element is found or Timeout is hit
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { newTopicName }, true);

        // Verify the following notification is displayed: [topic name's link] Created by [user name's link] [time ago]. There is/are [quantity] replies.
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone);
        List<TopicStatusDetails> topicStatusDetails = myDiscussionsDashlet.getUpdatedTopics();
        String createdBy = String.format("Created by %s ", testUser + " LName");

        for (TopicStatusDetails topicStatusDetail : topicStatusDetails)
        {
            Assert.assertTrue(topicStatusDetail.getCreationTime().contains(createdBy), "Expected info 'Created by [user name's link]' isn't presented");
            Assert.assertTrue(topicStatusDetail.getUpdateTime().contains("Updated"), "Expected info 'Updated' isn't presented");
        }

        // click topic's name link
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_ONE + " Updated").click().render();

        // verify topic details page is displayed
        Assert.assertNotNull(topicDetailsPage, "Topic details page isn't displayed");
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE + " Updated", "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON, "Expected info " + TOPIC_DETAILS_CREATED_ON + " isn't presented");

        // click user's name link
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        MyProfilePage myProfilePage = myDiscussionsDashlet.selectTopicUser(testUser + " LName").click().render();

        // verify User Profile page is displayed
        Assert.assertTrue(myProfilePage.titlePresent(), "Expected profile title isn't presented");

        // logout
        ShareUser.logout(drone);
    }

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
    @Test(groups = "AlfrescoOne")
    public void AONE_14757() throws Exception
    {
        String newTopicName = TOPIC_DETAILS_TITLE_ONE + " Updated";

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Get My Discussions Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);

        // add a topic for the site
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");

        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

        // Update created topic
        createNewTopicPage = topicDetailsPage.clickOnEditLink().render();
        Assert.assertEquals(createNewTopicPage.getPageTitle(), EDIT_TOPIC_TITLE, "Expected page title isn't presented");

        createNewTopicPage.enterTopicTitle(newTopicName);
        topicDetailsPage = createNewTopicPage.saveTopic().render();

        // Post a reply to created topic
        topicDetailsPage.clickOnReplyLink().render();
        topicDetailsPage.clickOnCreateReply().render();

        ShareUser.openSiteDashboard(drone, siteName);
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone);
        myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();

        // Repeat search until the element is found or Timeout is hit
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { newTopicName }, true);

        // Verify the following notification is displayed: [topic name's link] Created by [user name's link] [time ago]. There is/are [quantity] replies.
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone);
        List<TopicStatusDetails> topicStatusDetails = myDiscussionsDashlet.getUpdatedTopics();
        String createdBy = String.format("Created by %s ", testUser + " LName");
        String replyDetails = String.format("The last reply was posted by %s ", testUser + " LName");

        for (TopicStatusDetails topicStatusDetail : topicStatusDetails)
        {
            Assert.assertTrue(topicStatusDetail.getCreationTime().contains(createdBy), "Expected info 'Created by [user name's link]' isn't presented");
            Assert.assertTrue(topicStatusDetail.getUpdateTime().contains("Updated"), "Expected info 'Updated' isn't presented");
            Assert.assertEquals(topicStatusDetail.getNumberOfReplies(), NUMBER_OF_REPLIES, "Number of replies does not match");
            Assert.assertTrue(topicStatusDetail.getReplyDetails().contains(replyDetails), "Reply details isn't presented");
        }

        // click user's name link
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        MyProfilePage myProfilePage = myDiscussionsDashlet.selectTopicUser(testUser + " LName").click().render();

        // verify User Profile page is displayed
        Assert.assertTrue(myProfilePage.titlePresent(), "Expected profile title isn't presented");

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
    @Test(groups = "AlfrescoOne")
    public void AONE_14758() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Get My Discussions Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet, "My Discussions Dashlet isn't displayed");

        // add a topic for the site
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");

        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

        // Delete the topic
        DeleteTopicDialogPage deleteTopicDialogPage = topicDetailsPage.clickOnDeleteLink().render();

        Assert.assertNotNull(deleteTopicDialogPage, "Delete topic page is displayed");
        DiscussionsPage discussionsPage = deleteTopicDialogPage.clickOnDeleteButton().render();
        Assert.assertNotNull(discussionsPage, "Discussions page isn't displayed");

        // verify the notification about deleted topic is not displayed in My Discussions dashlet.??????????

        // verify topic is not displayed in dashlet anymore
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        myDiscussionsDashlet = siteDashboardPage.getDashlet(MY_DISCUSSIONS).render();

        // verify empty dashlet message - "There are no topics matching your filters." is displayed
        String emptyDashletMessage = myDiscussionsDashlet.getEmptyDashletMessage();
        Assert.assertEquals(emptyDashletMessage, EMPTY_DASHLET_MESSAGE, "Message 'There are no topics matching your filters.' isn't displayed");

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
    @Test(groups = "AlfrescoOne")
    public void AONE_14760() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Open My Discussions dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet, "My Discussions Dashlet isn't displayed");

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage
        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE, "Expected topic text isn't presented");

        // Go back to My Discussions dashlet and verify information about the created topic
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        siteDashboardPage.getDashlet(MY_DISCUSSIONS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        myDiscussionsDashlet = siteDashboardPage.getDashlet(MY_DISCUSSIONS).render();
        List<ShareLink> topicsUsers = myDiscussionsDashlet.getTopics(LinkType.User);
        Assert.assertNotNull(topicsUsers, "Topic users isn't presented");

        Assert.assertEquals(topicsUsers.size(), 1, "Number of topic users does not match");
        Assert.assertEquals(topicsUsers.get(0).getDescription(), testUser + " LName", "Expected user name isn't presented");

        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(topicsTitles, "Expected topic title isn't presented");
        Assert.assertEquals(topicsTitles.size(), 1, "Number of topics does not match");

        Assert.assertEquals(topicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

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
    @Test(groups = "AlfrescoOne")
    public void AONE_14761() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        String testUser1 = getUserNameFreeDomain(testName + "InvitedUser1" + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Open My Discussions dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE, "Expected topic text isn't presented");

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

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_TWO);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO, "Expected topic text isn't presented");

        // invited user logs out
        ShareUser.logout(drone);

        // site creator logs in and opens My Discussion dashlet on site dashboard
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();

        // Selected "My Topics" and verify only topics created by site creator are displayed
        // Repeat search until the element is found or Timeout is hit
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        List<ShareLink> myTopicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(myTopicsTitles, "Expected topic title isn't presented");
        Assert.assertEquals(myTopicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_TWO }, true);

        List<ShareLink> allTopicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(allTopicsTitles, "Expected topic title isn't presented");
        Assert.assertEquals(allTopicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_TWO, "Expected topic title isn't presented");
        Assert.assertEquals(allTopicsTitles.get(1).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

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
    @Test(groups = "AlfrescoOne")
    public void AONE_14759() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Open My Discussions dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE, "Expected topic text isn't presented");

        // create a topic2 for a site
        createNewTopicPage = topicDetailsPage.clickOnNewTopicLink().render();
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_TWO);
        topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO, "Expected topic text isn't presented");

        // create a topic3 for a site
        createNewTopicPage = topicDetailsPage.clickOnNewTopicLink().render();
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_THREE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_THREE);
        topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_THREE, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_THREE, "Expected topic text isn't presented");

        SiteDashboardPage siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        // Select Topics updated in the last day value from drop-down menu;
        myDiscussionsDashlet.clickHistoryButtton();
        myDiscussionsDashlet.selectTopicsHistoryFilter(LAST_DAY_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_THREE }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_TWO }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        List<ShareLink> lastDayTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);

        // Verify Topics updated in the last day are displayed
        Assert.assertNotNull(lastDayTopics, "Expected topic isn't displayed");
        Assert.assertEquals(lastDayTopics.size(), 3, "Expected topics isn't displayed");
        Assert.assertEquals(lastDayTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE, "Expected topic isn't displayed");
        Assert.assertEquals(lastDayTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO, "Expected topic isn't displayed");
        Assert.assertEquals(lastDayTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic isn't displayed");

        // Select Topics updated in the last 7 days value from drop-down menu
        myDiscussionsDashlet.clickHistoryButtton();
        myDiscussionsDashlet.selectTopicsHistoryFilter(SEVEN_DAYS_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_THREE }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_TWO }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        List<ShareLink> sevenDaysTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);

        // Verify Topics updated in the last 7 days are displayed
        Assert.assertNotNull(sevenDaysTopics, "Expected topics isn't displayed");
        Assert.assertEquals(sevenDaysTopics.size(), 3, "Expected topics isn't displayed");
        Assert.assertEquals(sevenDaysTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE, "Expected topic isn't displayed");
        Assert.assertEquals(sevenDaysTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO, "Expected topic isn't displayed");
        Assert.assertEquals(sevenDaysTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic isn't displayed");

        // Select Topics updated in the last 14 days value from drop-down menu
        myDiscussionsDashlet.clickHistoryButtton();
        myDiscussionsDashlet.selectTopicsHistoryFilter(FOURTEEN_DAYS_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_THREE }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_TWO }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        List<ShareLink> fourteenDaysTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);

        // Verify Topics updated in the last 14 days are displayed
        Assert.assertNotNull(fourteenDaysTopics, "Expected topics isn't displayed");
        Assert.assertEquals(fourteenDaysTopics.size(), 3, "Expected topics isn't displayed");
        Assert.assertEquals(fourteenDaysTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE, "Expected topic isn't displayed");
        Assert.assertEquals(fourteenDaysTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO, "Expected topic isn't displayed");
        Assert.assertEquals(fourteenDaysTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic isn't displayed");

        // Select Topics updated in the last 28 days value from drop-down menu
        myDiscussionsDashlet.clickHistoryButtton();
        myDiscussionsDashlet.selectTopicsHistoryFilter(TWENTY_EIGHT_DAYS_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_THREE }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_TWO }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        List<ShareLink> twentyEightDaysTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);

        // Verify Topics updated in the last 28 days are displayed
        Assert.assertNotNull(twentyEightDaysTopics, "Expected topics isn't displayed");
        Assert.assertEquals(twentyEightDaysTopics.size(), 3, "Expected topics isn't displayed");
        Assert.assertEquals(twentyEightDaysTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE, "Expected topic isn't displayed");
        Assert.assertEquals(twentyEightDaysTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO, "Expected topic isn't displayed");
        Assert.assertEquals(twentyEightDaysTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic isn't displayed");

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
    @Test(groups = "AlfrescoOne")
    public void AONE_14762() throws Exception
    {

        String testName = getTestName();

        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUser);

        String testUser1 = getUserNameFreeDomain(testName + "InvitedUser1" + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        String testUser2;
        if (!isAlfrescoVersionCloud(drone))
        {
            testUser2 = getUserNameFreeDomain(testName + "InvitedUser2" + System.currentTimeMillis());
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);
        }
        else
        {
            testUser2 = getUserNameForDomain(testName + "InvitedUser2" + System.currentTimeMillis(), DOMAIN_HYBRID);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);
        }

        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create a site
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        Assert.assertNotNull(myDiscussionsDashlet, "My Discussions Dashlet isn't displayed");

        // invite user1 as e.g. contributor (any role except consumer)
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser1, siteName, UserRole.CONTRIBUTOR);
        // invite user2 as e.g. contributor (any role except consumer)
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.CONTRIBUTOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User1 logs in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Open My Discussions - dashlet create a topic1 for a site
        SiteDashboardPage siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE, "Expected topic text isn't presented");

        // Invited User1 logs out
        ShareUser.logout(drone);

        // Invited User2 logs in
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // Open My Discussions - dashlet create a topic2 for a site
        if (!isAlfrescoVersionCloud(drone))
        {
            siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        }
        else
        {

            ShareUser.selectHomeNetwork(drone, testUser1);
            siteDashPage = ShareUser.openSiteDashboard(drone, siteName);
        }
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        // Click on New Topic button
        createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_TWO);

        // Click "Save" button on CreateNewTopicPage - create a topic2 for a site
        topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO, "Expected topic text isn't presented");

        // Invited User2 logs out
        ShareUser.logout(drone);

        // admin logs in, removes User1 and deletes User2
        // ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUserMembers.removeSiteMember(drone, testUser1, siteName);
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUserAdmin.deleteUser(drone, testUser2);
        }
        else
        {
            ShareUserMembers.removeSiteMember(drone, testUser2, siteName);

        }
        ShareUser.logout(drone);

        // admin logs in, removes User1 and deletes User2
        // ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open My Discussions dashlet
        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_TWO }, true);

        List<ShareLink> allTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(allTopics, "Expected topic title isn't presented");
        Assert.assertEquals(allTopics.size(), 2, "Expected topic title isn't presented");
        Assert.assertEquals(allTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_TWO, "Expected topic title isn't presented");
        Assert.assertEquals(allTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

        // Click on the topic1 name link
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_ONE).click().render();

        // verify topic details page is displayed

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON, "Expected info " + TOPIC_DETAILS_CREATED_ON + " isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE, "Expected topic text isn't presented");

        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        // Verify that invited user's name link is not active
        List<ShareLink> allUsers = myDiscussionsDashlet.getTopics(LinkType.User);
        Assert.assertEquals(allUsers.size(), 1, "Expected user name isn't presented");

        Assert.assertEquals(allUsers.get(0).getDescription(), testUser1 + " LName", "Expected user name isn't presented");
        MyProfilePage myProfilePage = myDiscussionsDashlet.selectTopicUser(testUser1 + " LName").click().render();

        // verify User Profile page is displayed
        Assert.assertTrue(myProfilePage.titlePresent(), "Expected profile title isn't presented");

        siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_TWO }, true);

        // Click on the topic2 name link
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_TWO).click().render();

        // Verify topic2 details page is displayed correctly

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON, "Expected info " + TOPIC_DETAILS_CREATED_ON + " isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO, "Expected topic text isn't presented");

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
    @Test(groups = "AlfrescoOne")
    public void AONE_14763() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create site1
        String siteName1 = testName + System.currentTimeMillis() + "-Site1";
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

        // create topic1 for site1 - Open My Discussions dashlet for site1
        ShareUserDashboard.addDashlet(drone, siteName1, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName1);

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for site1
        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE, "Expected topic text isn't presented");

        // create site2
        String siteName2 = testName + System.currentTimeMillis() + "-Site2";
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);

        // Open My Discussions dashlet for site2
        ShareUserDashboard.addDashlet(drone, siteName2, Dashlets.MY_DISCUSSIONS);
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName2);

        // Click on New Topic button
        createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_TWO);

        // Click "Save" button on CreateNewTopicPage - create a topic2 for site2
        topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO, "Expected topic text isn't presented");

        SiteDashboardPage siteDashPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName1));
        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();
        myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        // Selected "My Topics" value from drop-down menu by default
        // Verify only topics created by site creator are displayed
        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(topicsTitles, "Expected topic title isn't presented");
        Assert.assertEquals(topicsTitles.size(), 1, "Number of topics does not match");
        Assert.assertEquals(topicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

        // Select "All Topics" value from drop-down menu
        myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        List<ShareLink> allTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);

        // Verify all topics created by users of the site are displayed (no topic2)
        Assert.assertNotNull(allTopics, "Expected topic title isn't presented");
        Assert.assertEquals(allTopics.size(), 1, "Expected topic title isn't presented");
        Assert.assertEquals(allTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

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
    @Test(groups = "AlfrescoOne")
    public void AONE_14764() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        // login as created user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // create site
        String siteName = testName + System.currentTimeMillis();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Open My Discussions dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.MY_DISCUSSIONS);
        MyDiscussionsDashlet myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);

        // Click on New Topic button
        CreateNewTopicPage createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_ONE);

        TopicDetailsPage topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_ONE, "Expected topic text isn't presented");

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        myDiscussionsDashlet = siteDashPage.getDashlet(MY_DISCUSSIONS).render();

        // invited user creates a topic2 for the site
        // Click on New Topic button
        createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();

        // Fill in the required fields (Title and Text) on CreateNewTopicPage
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_TOPIC_TITLE, "Expected page title isn't presented");
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        createNewTopicPage.getTinyMCEEditor().setText(TOPIC_DETAILS_TEXT_TWO);

        // Click "Save" button on CreateNewTopicPage - create a topic1 for a site
        topicDetailsPage = createNewTopicPage.saveTopic().render();

        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO, "Expected topic title isn't presented");
        Assert.assertEquals(topicDetailsPage.getTopicText(), TOPIC_DETAILS_TEXT_TWO, "Expected topic text isn't presented");

        // Try to expand dashlet to possible size
        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        myDiscussionsDashlet.resizeDashlet(+50, +50);

        // Verify that all items is correctly displayed
        myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_TWO }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        myDiscussionsDashlet = ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName);
        List<ShareLink> expandedTopicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(expandedTopicsTitles, "Topic title isn't presented (expanded)");

        Assert.assertEquals(expandedTopicsTitles.size(), 2, "Number of topics does not match");
        Assert.assertEquals(expandedTopicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_TWO, "Expected topic title isn't presented");
        Assert.assertEquals(expandedTopicsTitles.get(1).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

        List<ShareLink> expandedTopicsUsers = myDiscussionsDashlet.getTopics(LinkType.User);
        Assert.assertNotNull(expandedTopicsUsers, "Topic users isn't presented");
        Assert.assertEquals(expandedTopicsUsers.size(), 2, "Number of topic users does not match");
        Assert.assertEquals(expandedTopicsUsers.get(0).getDescription(), testUser + " LName", "Expected user name isn't presented");
        Assert.assertEquals(expandedTopicsUsers.get(1).getDescription(), testUser + " LName", "Expected user name isn't presented");

        // Try to expand dashlet to possible size
        myDiscussionsDashlet.resizeDashlet(0, -400);

        // Verify that all items is correctly displayed
        myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();

        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_TWO }, true);
        ShareUserDashboard.searchMyDiscussionDashletWithRetry(drone, new String[] { TOPIC_DETAILS_TITLE_ONE }, true);

        List<ShareLink> narrowedTopicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(narrowedTopicsTitles, "Topics titles isn't presented (narrowed)");

        Assert.assertEquals(narrowedTopicsTitles.size(), 2, "Number of topics does not match (narrowed)");
        Assert.assertEquals(narrowedTopicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_TWO, "Expected topic title isn't presented");
        Assert.assertEquals(narrowedTopicsTitles.get(1).getDescription(), TOPIC_DETAILS_TITLE_ONE, "Expected topic title isn't presented");

        List<ShareLink> narrowedTopicsUsers = myDiscussionsDashlet.getTopics(LinkType.User);
        Assert.assertNotNull(narrowedTopicsUsers, "Topic title isn't presented (narrowed)");

        Assert.assertEquals(narrowedTopicsUsers.size(), 2, "Topic title isn't presented (narrowed)");
        Assert.assertEquals(narrowedTopicsUsers.get(0).getDescription(), testUser + " LName", "Expected user name isn't presented");
        Assert.assertEquals(narrowedTopicsUsers.get(1).getDescription(), testUser + " LName", "Expected user name isn't presented");

        ShareUser.logout(drone);
    }

}
