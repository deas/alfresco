/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
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

package org.alfresco.share.clustering;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.discussions.TopicViewPage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static java.lang.String.format;
import static org.testng.Assert.*;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class DiscussionsClusterTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(DiscussionsClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    private String testUser;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);
        logger.info("Starting Tests: " + testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        /*
         * String shareJmxPort = getAlfrescoServerProperty("Alfresco:Type=Configuration,Category=sysAdmin,id1=default", "share.port").toString();
         * boolean clustering_enabled_jmx = (boolean) getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusteringEnabled");
         * if (clustering_enabled_jmx)
         * {
         * Object clustering_url = getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusterMembers");
         * try
         * {
         * CompositeDataSupport compData = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[0];
         * String clusterIP = compData.values().toArray()[0] + ":" + shareJmxPort;
         * CompositeDataSupport compData2 = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[1];
         * String clusterIP2 = compData2.values().toArray()[0] + ":" + shareJmxPort;
         * node1Url = shareUrl.replace(shareIP, clusterIP);
         * node2Url = shareUrl.replace(shareIP, clusterIP2);
         * }
         * catch (Throwable ex)
         * {
         * throw new SkipException("Skipping as pre-condition step(s) fail");
         * }
         * }
         */
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2)
        {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
        }
        else
        {
            throw new PageOperationException("Number of cluster members is less than two");
        }
    }

    /**
     * Test - AONE_15856:Creating new topic
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Click on Discussions link on nodeA</li>
     * <li>Create New topic button</li>
     * <li>Click Submit button</li>
     * <li>Check that topic is created on server A</li>
     * <li>Verify that topic displayed on "Discussions" page on both nodes (nodeA and nodeB)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9167() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String topicName = RandomUtil.getRandomString(5);
        String topicText = RandomUtil.getRandomString(5);
        String tag = getTagName(testName) + 1;

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Discussions page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DISCUSSIONS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Click on Discussions link on nodeA
        DiscussionsPage discussionsPage = siteDashPage.getSiteNav().selectDiscussionsPage();

        // Create New topic
        TopicViewPage topicViewPage = discussionsPage.createTopic(topicName, topicText, tag);

        // Check that topic is created on server A
        assertTrue(topicViewPage.isReplyLinkDisplayed(), "Reply link don't displayed. Server A");
        assertEquals(topicViewPage.getTopicTitle(), topicName, "Wrong  title displayed. Server A");
        assertEquals(topicViewPage.getTopicText(), topicText, "Wrong topic text displayed.  Server A");

        // Verify that topic displayed on "Discussions" page
        discussionsPage = topicViewPage.clickBack();
        assertEquals(discussionsPage.getTopicsCount(), 1, "Wrong topics count displayed in filter. Server A");
        List<String> topicTitles;
        topicTitles = discussionsPage.getTopicTitles();
        assertEquals(topicTitles.get(0), topicName, "Wrong first element in filter");
        // Verify that topic displayed on "Discussions" page on server A
        assertTrue(discussionsPage.isTopicPresented(topicName), "Topic " + topicName + " isn't displayed on 'Discussions' page. Server A");

        ShareUser.logout(drone);

        // verify that created topic is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Click on Discussions link on nodeB
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);

        discussionsPage = siteDashPage.getSiteNav().selectDiscussionsPage();

        assertEquals(discussionsPage.getTopicsCount(), 1, "Wrong topics count displayed in filter. Server B");
        topicTitles = discussionsPage.getTopicTitles();
        assertEquals(topicTitles.get(0), topicName, "Wrong first element in filter. Server B");

        // Verify that topic displayed on "Discussions" page on server B
        assertTrue(discussionsPage.isTopicPresented(topicName), "Topic " + topicName + " isn't displayed on 'Discussions' page. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15857:Edit topic details
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Discussions page is opened on nodeA</li>
     * <li>Any discussion is created</li>
     * <li>Edit topic on server A</li>
     * <li>Verify that topic changed correctly on both nodes (nodeA and nodeB)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9168() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String topicName = RandomUtil.getRandomString(5);
        String topicText = RandomUtil.getRandomString(5);
        String topicNameEdited = RandomUtil.getRandomString(5);
        String topicTextEdited = RandomUtil.getRandomString(5);
        String tag = getTagName(testName) + 1;

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Discussions page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DISCUSSIONS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Click on Discussions link on nodeA
        DiscussionsPage discussionsPage = siteDashPage.getSiteNav().selectDiscussionsPage();

        // Create New topic
        TopicViewPage topicViewPage = discussionsPage.createTopic(topicName, topicText);

        // Check that topic is created on server A
        assertTrue(topicViewPage.isReplyLinkDisplayed(), "Reply link don't displayed. Server A");
        assertEquals(topicViewPage.getTopicTitle(), topicName, "Wrong  title displayed. Server A");
        assertEquals(topicViewPage.getTopicText(), topicText, "Wrong topic text displayed.  Server A");

        // Edit topic on server A
        topicViewPage.editTopic(topicName, topicNameEdited, topicTextEdited, tag);

        // Verify that topic changed correctly on server A
        assertEquals(topicViewPage.getTagName(), tag, "Wrong tag name. Server A");
        assertTrue(topicViewPage.isReplyLinkDisplayed(), "Reply link don't displayed. Server A");
        assertEquals(topicViewPage.getTopicTitle(), topicNameEdited, "Wrong title displayed after editing. Server A");
        assertEquals(topicViewPage.getTopicText(), topicTextEdited, "Wrong topic text displayed after editing. Server A");

        ShareUser.logout(drone);

        // verify that created topic is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Click on Discussions link on nodeB
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);

        discussionsPage = siteDashPage.getSiteNav().selectDiscussionsPage();

        // Verify that edited topic displayed on "Discussions" page on server B
        assertTrue(discussionsPage.isTopicPresented(topicNameEdited), "Topic " + topicNameEdited + " isn't displayed on 'Discussions' page. Server B");

        discussionsPage.viewTopic(topicNameEdited);

        // Verify that topic changed correctly on server B
        assertEquals(topicViewPage.getTagName(), tag, "Wrong tag name. Server B");
        assertTrue(topicViewPage.isReplyLinkDisplayed(), "Reply link don't displayed. Server B");
        assertEquals(topicViewPage.getTopicTitle(), topicNameEdited, "Wrong title displayed after editing. Server B");
        assertEquals(topicViewPage.getTopicText(), topicTextEdited, "Wrong topic text displayed after editing. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15858:Replying a topic
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Discussions page is opened on nodeA</li>
     * <li>Any discussion is created</li>
     * <li>Reply topic on server A</li>
     * <li>Verify that Information is saved and displayed correctly on both nodes (nodeA and nodeB)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9169() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String topicName = RandomUtil.getRandomString(5);
        String topicText = RandomUtil.getRandomString(5);
        String replyText = RandomUtil.getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Discussions page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DISCUSSIONS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Click on Discussions link on nodeA
        DiscussionsPage discussionsPage = siteDashPage.getSiteNav().selectDiscussionsPage();

        // Create New topic
        TopicViewPage topicViewPage = discussionsPage.createTopic(topicName, topicText);

        // Check that topic is created on server A
        assertTrue(topicViewPage.isReplyLinkDisplayed(), "Reply link don't displayed. Server A");
        assertEquals(topicViewPage.getTopicTitle(), topicName, "Wrong  title displayed. Server A");
        assertEquals(topicViewPage.getTopicText(), topicText, "Wrong topic text displayed.  Server A");

        // Reply the topic at server A
        topicViewPage.createReply(replyText);

        // Verify that topic Replied on server A
        assertEquals(topicViewPage.getReplyCount(), 1, "Replay don't create. Server A");
        assertTrue(topicViewPage.isReplyDisplay(replyText), format("Reply[%s] wrong or don't found on page. Server A", replyText));

        ShareUser.logout(drone);

        // verify that created topic is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Click on Discussions link on nodeB
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);

        discussionsPage = siteDashPage.getSiteNav().selectDiscussionsPage();

        // Verify that topic displayed on "Discussions" page on server B
        assertTrue(discussionsPage.isTopicPresented(topicName), "Topic " + topicName + " isn't displayed on 'Discussions' page. Server B");

        discussionsPage.viewTopic(topicName);

        // Verify that topic displayed correctly on server B
        assertTrue(topicViewPage.isReplyLinkDisplayed(), "Reply link don't displayed. Server B");
        assertEquals(topicViewPage.getTopicTitle(), topicName, "Wrong title displayed after editing. Server B");

        // Verify that topic Replied on server B
        assertEquals(topicViewPage.getReplyCount(), 1, "Replay don't create. Server B");
        assertTrue(topicViewPage.isReplyDisplay(replyText), format("Reply[%s] wrong or don't found on page. Server B", replyText));

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15859:Deleting tag from topic
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>At least one topic with tag created for current site</li>
     * <li>Click 'Edit' button for created topic</li>
     * <li>Delete added tag and click 'Save' button</li>
     * <li>Tag is deleted, changes are saved</li>
     * <li>Verify that tag isn't displayed on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9170() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String topicName = RandomUtil.getRandomString(5);
        String topicText = RandomUtil.getRandomString(5);
        String tagName = getTagName(testName) + 1;

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Discussions page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DISCUSSIONS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Click on Discussions link on nodeA
        DiscussionsPage discussionsPage = siteDashPage.getSiteNav().selectDiscussionsPage();

        // Create New topic
        TopicViewPage topicViewPage = discussionsPage.createTopic(topicName, topicText, tagName);

        // Check that topic is created on server A
        assertEquals(topicViewPage.getTopicTitle(), topicName, "Wrong  title displayed. Server A");
        assertEquals(topicViewPage.getTagName(), tagName, "Wrong tag name. Server A");

        // Verify that topic displayed on "Discussions" page
        discussionsPage = topicViewPage.clickBack();

        assertTrue(discussionsPage.checkTags(topicName, tagName), "Wrong tag name on 'Discussions' page. Server A");

        // Remove tag
        discussionsPage.viewTopic(topicName);

        topicViewPage.editTopic(topicName, topicName, topicText, tagName, true);

        // Verify Tag is deleted server A
        assertEquals(topicViewPage.getTopicTitle(), topicName, "Wrong  title displayed. Server A");
        assertEquals(topicViewPage.getTagName(), "(None)", "Tag isn't deleted. Server A");

        discussionsPage = topicViewPage.clickBack();
        assertTrue(discussionsPage.isTopicPresented(topicName), "Topic " + topicName + " isn't displayed on 'Discussions' page. Server A");

        // Verify Tag is deleted on server A
        assertTrue(discussionsPage.checkTags(topicName, null), "Tag isn't deleted. Server A");

        ShareUser.logout(drone);

        // verify that created topic is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Click on Discussions link on nodeB
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);

        discussionsPage = siteDashPage.getSiteNav().selectDiscussionsPage();

        // Verify that topic displayed on "Discussions" page on server B
        assertTrue(discussionsPage.isTopicPresented(topicName), "Topic " + topicName + " isn't displayed on 'Discussions' page. Server B");

        assertTrue(discussionsPage.checkTags(topicName, null), "Tag isn't deleted. Server B");

        discussionsPage.viewTopic(topicName);

        // Verify Tag is deleted on server B
        assertEquals(topicViewPage.getTopicTitle(), topicName, "Wrong  title displayed. Server B");
        assertEquals(topicViewPage.getTagName(), "(None)", "Tag isn't deleted. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15860:Deleting a topic
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Click on Discussions link on nodeA</li>
     * <li>Any discussion is created</li>
     * <li>Delete topic</li>
     * <li>Verify that topic is deleted and isn't displayed on both nodes (nodeA and nodeB)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9171() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String topicName = RandomUtil.getRandomString(5);
        String topicText = RandomUtil.getRandomString(5);
        String tag = getTagName(testName) + 1;

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Discussions page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DISCUSSIONS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Click on Discussions link on nodeA
        DiscussionsPage discussionsPage = siteDashPage.getSiteNav().selectDiscussionsPage();

        // Create New topic
        TopicViewPage topicViewPage = discussionsPage.createTopic(topicName, topicText, tag);

        // Check that topic is created on server A
        assertTrue(topicViewPage.isReplyLinkDisplayed(), "Reply link don't displayed. Server A");
        assertEquals(topicViewPage.getTopicTitle(), topicName, "Wrong  title displayed. Server A");
        assertEquals(topicViewPage.getTopicText(), topicText, "Wrong topic text displayed.  Server A");

        // Verify that topic displayed on "Discussions" page
        discussionsPage = topicViewPage.clickBack();

        // Verify that topic displayed on "Discussions" page on server A
        assertTrue(discussionsPage.isTopicPresented(topicName), "Topic " + topicName + " isn't displayed on 'Discussions' page. Server A");

        // Delete topic
        discussionsPage.deleteTopicWithConfirm(topicName);

        // Verify that topic deleted on "Discussions" page on server A
        assertFalse(discussionsPage.isTopicPresented(topicName), "Topic " + topicName + " is displayed on 'Discussions' page. Server A");
        assertTrue(discussionsPage.isNoTopicsDisplayed(), "Topic isn't deleted displayed on 'Discussions' page. Server A");

        ShareUser.logout(drone);

        // verify that topic is deleted at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Click on Discussions link on nodeB
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);

        discussionsPage = siteDashPage.getSiteNav().selectDiscussionsPage();

        // Verify that topic deleted on "Discussions" page on server B
        assertFalse(discussionsPage.isTopicPresented(topicName), "Topic " + topicName + " is displayed on 'Discussions' page. Server B");
        assertTrue(discussionsPage.isNoTopicsDisplayed(), "Topic isn't deleted displayed on 'Discussions' page. Server B");

        ShareUser.logout(drone);
    }

}