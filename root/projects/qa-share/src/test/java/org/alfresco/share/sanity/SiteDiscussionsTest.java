/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.share.sanity;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.ActivityShareLink;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.dashlet.mydiscussions.DeleteTopicDialogPage;
import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteNavigation;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.discussions.*;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.alfresco.po.share.dashlet.SiteActivitiesUserFilter.MY_ACTIVITIES;
import static org.alfresco.po.share.enums.UserRole.CONTRIBUTOR;
import static org.alfresco.po.share.site.discussions.TopicsListFilter.FilterOption.*;
import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class SiteDiscussionsTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SiteDiscussionsTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8230() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        ShareUser.login(drone, testUser);
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(new SitePageType[] { SitePageType.DISCUSSIONS }));
    }

    @Test(groups = "Sanity")
    public void AONE_8230()
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String topicName = RandomUtil.getRandomString(5);
        String topicName2 = RandomUtil.getRandomString(5);
        String topicName2Edited = RandomUtil.getRandomString(5);
        String replyText = RandomUtil.getRandomString(5);
        String replyEditText = RandomUtil.getRandomString(5);
        String subReply = RandomUtil.getRandomString(5);
        String tagName = getTagName(testName);

        ShareUser.login(drone, testUser);
        SiteDashboardPage siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        DiscussionsPage discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage();
        TopicViewPage topicViewPage = discussionsPage.createTopic(topicName, topicName);
        assertTrue(topicViewPage.isReplyLinkDisplayed(), "Reply link don't displayed.");
        assertEquals(topicViewPage.getTopicTitle(), topicName, "Wrong title displayed.");
        assertEquals(topicViewPage.getTopicText(), topicName, "Wrong topic text displayed.");

        topicViewPage.createTopic(topicName2, topicName2);
        assertTrue(topicViewPage.isReplyLinkDisplayed(), "Reply link don't displayed.");
        assertEquals(topicViewPage.getTopicTitle(), topicName2, "Wrong title displayed.");
        assertEquals(topicViewPage.getTopicText(), topicName2, "Wrong topic text displayed.");

        topicViewPage.editTopic(topicName2, topicName2Edited, topicName2Edited, tagName);
        assertEquals(topicViewPage.getTagName(), tagName, "Wrong tag name.");
        assertTrue(topicViewPage.isReplyLinkDisplayed(), "Reply link don't displayed.");
        assertEquals(topicViewPage.getTopicTitle(), topicName2Edited, "Wrong title displayed.");
        assertEquals(topicViewPage.getTopicText(), topicName2Edited, "Wrong topic text displayed.");
        topicViewPage.clickOnTag(tagName);
        for (int i = 0; i < 1000; i++)
        {
            drone.refresh();
            if (discussionsPage.getTopicsCount() != 0)
            {
                break;
            }
        }
        assertEquals(discussionsPage.getTopicsCount(), 1, "Wrong topic count with tags.");
        assertNotNull(discussionsPage.getTopicDirectoryInfo(topicName2Edited), "Our topic with tag don't display.");
        discussionsPage.viewTopic(topicName2Edited);
        assertEquals(topicViewPage.getReplyCount(), 0, "Topic has replay.");
        topicViewPage.createReply(replyText);
        assertEquals(topicViewPage.getReplyCount(), 1, "Replay don't create.");
        assertTrue(topicViewPage.isReplyDisplay(replyText), format("Reply[%s] wrong or don't found on page", replyText));
        topicViewPage.editReply(replyText, replyEditText);
        assertEquals(topicViewPage.getReplyCount(), 1, "Replay don't create.");
        assertTrue(topicViewPage.isReplyDisplay(replyEditText), format("Reply[%s] wrong or don't found on page", replyText));
        ReplyDirectoryInfo replyDirectoryInfo = topicViewPage.getReplyDirectoryInfo(replyEditText);
        replyDirectoryInfo.createSubReply(subReply);
        assertEquals(replyDirectoryInfo.getSubRepliesCount(), 1, "Sub reply don't create.");
        assertTrue(replyDirectoryInfo.isSubReply(subReply), format("SubReply[%s] wrong or don't found on page", subReply));
        topicViewPage.createReply(replyText);
        assertEquals(topicViewPage.getReplyCount(), 3, "Wrong reply count after added new.");
        replyDirectoryInfo = topicViewPage.getReplyDirectoryInfo(replyEditText);
        assertFalse(replyDirectoryInfo.isSubReply(replyText), "New topic reply added as sub-reply");
        assertEquals(replyDirectoryInfo.getSubRepliesCount(), 1, "Wrong sub-replies count after new post fot topic add.");
        RssFeedPage rssFeedPage = topicViewPage.selectRssFeed(testUser, DEFAULT_PASSWORD);
        TopicDetailsPage topicDetailsPage = rssFeedPage.clickOnFeedContent(topicName2Edited).render();
        assertEquals(topicDetailsPage.getTopicTitle(), topicName2Edited, "Page with correct topic don't open.");
        assertEquals(topicDetailsPage.getTopicText(), topicName2Edited, "Page with correct text don't open.");
        DeleteTopicDialogPage deleteTopicDialogPage = topicDetailsPage.clickOnDeleteLink();
        discussionsPage = deleteTopicDialogPage.clickOnDeleteButton();
        assertEquals(discussionsPage.getTopicsCount(), 1, "Wrong topics count after one topic deleted.");
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        SiteActivitiesDashlet siteActivitiesDashlet = siteDashboardPage.getDashlet("site-activities").render();
        siteActivitiesDashlet.selectUserFilter(MY_ACTIVITIES);
        List<String> descriptions = siteActivitiesDashlet.getSiteActivityDescriptions();
        assertTrue(descriptions.size() != 0, "Information about activities with topics don't reflect in site dashlet.");
        DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone);
        MyActivitiesDashlet myActivitiesDashlet = dashBoardPage.getDashlet("activities").render();
        List<ActivityShareLink> activityShareLinks = myActivitiesDashlet.getActivities();
        assertTrue(activityShareLinks.size() != 0, "Information about activities with topics don't reflect in dashlet.");
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8231() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String testUser1 = testUser + 1;
        String testUser2 = testUser + 2;
        String siteName = getSiteName(testName);
        String topic = testName;
        String topicUser1 = testUser1 + "topic";
        String topicUser2 = testUser2 + "topic";
        String tag1 = getTagName(testName) + 1;
        String tag2 = getTagName(testName) + 2;
        String reply1 = testName + "reply1";
        String reply2 = testName + "reply2";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        ShareUser.login(drone, testUser1);
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(new SitePageType[] { SitePageType.DISCUSSIONS }));
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, CONTRIBUTOR);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        DiscussionsPage discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage();
        TopicViewPage topicViewPage = discussionsPage.createTopic(topic, topic);
        topicViewPage.createTopic(topicUser1, topicUser1, tag1);
        topicViewPage.createReply(reply1);
        ShareUser.login(drone, testUser2);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        discussionsPage = siteDashboardPage.getSiteNav().selectDiscussionsPage();
        topicViewPage = discussionsPage.createTopic(topicUser2, topicUser2, tag2);
        topicViewPage.createReply(reply2);
    }

    @Test(groups = "Sanity")
    public void AONE_8231()
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String testUser1 = testUser + 1;
        String testUser2 = testUser + 2;
        String siteName = getSiteName(testName);
        String topic = testName;
        String topicUser1 = testUser1 + "topic";
        String topicUser2 = testUser2 + "topic";
        String tag1 = getTagName(testName) + 1;
        String tag2 = getTagName(testName) + 2;

        ShareUser.login(drone, testUser1);
        SiteDashboardPage siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        SiteNavigation siteNavigation = siteDashboardPage.getSiteNav();
        DiscussionsPage discussionsPage = siteNavigation.selectDiscussionsPage();
        TopicsListFilter topicsListFilter = discussionsPage.getTopicsListFilter();

        topicsListFilter.select(NEW);
        assertEquals(discussionsPage.getTopicsCount(), 3, "Wrong topics count displayed in filter");
        List<String> topicTitles = discussionsPage.getTopicTitles();
        assertEquals(topicTitles.get(0), topicUser2, "Wrong first element in filter");

        topicsListFilter.select(MOST_ACTIVE);
        assertEquals(discussionsPage.getTopicsCount(), 2, "Wrong topics count displayed in filter");
        topicTitles = discussionsPage.getTopicTitles();
        assertEquals(topicTitles.get(0), topicUser2, "Wrong first element in filter");
        assertEquals(topicTitles.get(1), topicUser1, "Wrong first element in filter");

        topicsListFilter.select(ALL);
        assertEquals(discussionsPage.getTopicsCount(), 3, "Wrong topics count displayed in filter");
        topicTitles = discussionsPage.getTopicTitles();
        assertEquals(topicTitles.get(0), topicUser2, "Wrong first element in filter");

        topicsListFilter.select(MY_TOPICS);
        assertEquals(discussionsPage.getTopicsCount(), 2, "Wrong topics count displayed in filter");
        topicTitles = discussionsPage.getTopicTitles();
        assertEquals(topicTitles.get(0), topic, "Wrong first element in filter");
        assertEquals(topicTitles.get(1), topicUser1, "Wrong first element in filter");

        topicsListFilter.clickOnTag(tag1);
        assertEquals(discussionsPage.getTopicsCount(), 1, "Wrong topics count displayed in filter");
        topicTitles = discussionsPage.getTopicTitles();
        assertEquals(topicTitles.get(0), topicUser1, "Wrong first element in filter");

        topicsListFilter.clickOnTag(tag2);
        assertEquals(discussionsPage.getTopicsCount(), 1, "Wrong topics count displayed in filter");
        topicTitles = discussionsPage.getTopicTitles();
        assertEquals(topicTitles.get(0), topicUser2, "Wrong first element in filter");

        topicsListFilter.select(NEW);
        TopicDirectoryInfo topicDirectoryInfo = discussionsPage.getTopicDirectoryInfo(topicUser1);
        assertEquals(topicDirectoryInfo.getRepliesCount(), 1, "Wrong replies count.");
        topicDirectoryInfo = discussionsPage.getTopicDirectoryInfo(topicUser2);
        assertEquals(topicDirectoryInfo.getRepliesCount(), 1, "Wrong replies count.");
        topicDirectoryInfo = discussionsPage.getTopicDirectoryInfo(topic);
        assertEquals(topicDirectoryInfo.getRepliesCount(), 0, "Wrong replies count.");

        TopicDetailsPage topicDetailsPage = topicDirectoryInfo.clickRead();
        assertEquals(topicDetailsPage.getTopicTitle(), topic, "Wrong page open after read clicking.");

        discussionsPage = siteNavigation.selectDiscussionsPage();
        topicDirectoryInfo = discussionsPage.getTopicDirectoryInfo(topicUser1);
        discussionsPage = topicDirectoryInfo.clickOnTag(tag1);
        assertEquals(discussionsPage.getTopicsCount(), 1, "Wrong topics count displayed in filter");
        topicTitles = discussionsPage.getTopicTitles();
        assertEquals(topicTitles.get(0), topicUser1, "Wrong first element in filter");

        discussionsPage = siteNavigation.selectDiscussionsPage();
        topicDirectoryInfo = discussionsPage.getTopicDirectoryInfo(topic);
        TopicViewPage topicViewPage = topicDirectoryInfo.viewTopic();
        assertEquals(topicViewPage.getTopicTitle(), topic, "Wrong page open after read clicking.");

        discussionsPage = siteNavigation.selectDiscussionsPage();
        topicDirectoryInfo = discussionsPage.getTopicDirectoryInfo(topic);
        NewTopicForm newTopicForm = topicDirectoryInfo.editTopic();
        String editedTopic = topic + "edited";
        newTopicForm.setTitleField(editedTopic);
        newTopicForm.clickSave();
        discussionsPage = siteNavigation.selectDiscussionsPage();
        topicDirectoryInfo = discussionsPage.getTopicDirectoryInfo(editedTopic);
        assertNotNull(topicDirectoryInfo, "Topic don't edited.");

        discussionsPage = siteNavigation.selectDiscussionsPage();
        topicDirectoryInfo = discussionsPage.getTopicDirectoryInfo(editedTopic);
        topicDirectoryInfo.deleteTopic();
        try
        {
            discussionsPage.getTopicDirectoryInfo(editedTopic);
            fail("Topic don't delete.");
        }
        catch (PageException e)
        {
            logger.info("Can't found topic on page. It's ok.");
        }
    }

}
