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
import org.alfresco.po.share.site.links.LinkComment;
import org.alfresco.po.share.site.links.LinksDetailsPage;
import org.alfresco.po.share.site.links.LinksPage;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class LinksClusterTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(LinksClusterTest.class);
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
     * Test - AONE_15850:Create link
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Links page for site is opened</li>
     * <li>Press 'New Link' button</li>
     * <li>Fill in the fields with correct data</li>
     * <li>Press 'Save' button</li>
     * <li>The link is created and displayed in alone on 'Link's Details' page</li>
     * <li>Go to server B for current site and verify presence of created link</li>
     * <li>Created link is displayed correctly on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9172() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String linkName = "link_" + getRandomString(5);
        String linkUrl = "www.alfresco.com";
        String tagName = "tag_" + getRandomString(3);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Links page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.LINKS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        LinksPage linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Create new link
        LinksDetailsPage linksDetailsPage = linksPage.createLink(linkName, linkUrl, tagName).render();

        // Check created link
        assertEquals(linksDetailsPage.getUrl(), linkUrl, "Wrong url for created link. Server A");
        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title. Server A");

        ShareUser.logout(drone);

        // verify that created link at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Links page for site is opened
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);
        linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Check created link is displayed correctly on server B
        assertEquals(linksPage.getLinksCount(), 1, "Wrong links count. Server B");
        assertTrue(linksPage.isLinkPresented(linkName), "Link with title " + linkName + " don't displayed. Server B");
        assertTrue(linksPage.checkTags(linkName, tagName), "Tag " + tagName + " don't displayed. Server B");

        linksDetailsPage = linksPage.clickLink(linkName).render();

        assertEquals(linksDetailsPage.getUrl(), linkUrl, "Wrong url for created link.  Server B");
        assertEquals(linksDetailsPage.getTagName(), tagName, "Tag " + tagName + " don't displayed. Server B");
        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title. Server B");
        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15851:Edit link
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Links page for site is opened</li>
     * <li>Put pointer on any link with tag</li>
     * <li>Press 'Edit'</li>
     * <li>Make any changes in 'Title', 'Description' and 'URL' fields</li>
     * <li>Remove an existing tag</li>
     * <li>Press 'Update' button</li>
     * <li>'Link's Details' page is opened. Changes are applied</li>
     * <li>Go to server B and verify created link</li>
     * <li>All changes for current link are updated correctly on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9173() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String linkName = "link_" + getRandomString(5);
        String linkUrl = "www.alfresco.com";
        String tagName = "tag_" + getRandomString(3);

        String newLinkName = "Electric tower";
        String newLinkUrl = "http://wiki.alfresco.com/";
        String newLinkDescription = testName;

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Links page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.LINKS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        LinksPage linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Create new link
        LinksDetailsPage linksDetailsPage = linksPage.createLink(linkName, linkUrl, tagName).render();

        // Check created link
        assertEquals(linksDetailsPage.getUrl(), linkUrl, "Wrong url for created link. Server A");
        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title. Server A");
        assertEquals(linksDetailsPage.getTagName(), tagName, "Wrong link title. Server A");

        // Edit link and remove tag
        linksDetailsPage.editLink(newLinkUrl, newLinkName, newLinkDescription, tagName, true);

        // Check edited link on server A
        assertEquals(linksDetailsPage.getUrl(), newLinkUrl, "Wrong url for edited link. Server A");
        assertTrue(linksDetailsPage.getTagName().contains("None"), "Tag don't removed. Server A");
        assertEquals(linksDetailsPage.getDescription(), newLinkDescription, "Link description don't changed.  Server A");

        ShareUser.logout(drone);

        // verify that edited link at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Links page for site is opened
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);
        linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Check edited link is displayed correctly on server B
        assertEquals(linksPage.getLinksCount(), 1, "Wrong links count. Server B");
        assertTrue(linksPage.isLinkPresented(newLinkName), "Link with title " + linkName + " don't displayed. Server B");
        assertTrue(linksPage.checkTags(newLinkName, null), "Tag " + tagName + " don't displayed. Server B");

        linksDetailsPage = linksPage.clickLink(newLinkName).render();

        // Check edited link on server B (at link details page)
        assertEquals(linksDetailsPage.getUrl(), newLinkUrl, "Wrong url for edited link. Server B");
        assertTrue(linksDetailsPage.getTagName().contains("None"), "Tag wasn't removed");
        assertEquals(linksDetailsPage.getDescription(), newLinkDescription, "Link description don't changed.  Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15852:Delete link
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>"Links" page with at least one link is opened for the Site</li>
     * <li>Put cursor on created link;</li>
     * <li>Press 'Delete'</li>
     * <li>Press 'Delete' button</li>
     * <li>Go to server B and verify deletion of link</li>
     * <li>Link is missing on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9174() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String linkName = "link_" + getRandomString(5);
        String linkUrl = "www.alfresco.com";
        String tagName = "tag_" + getRandomString(3);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Links page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.LINKS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        LinksPage linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Create new link
        LinksDetailsPage linksDetailsPage = linksPage.createLink(linkName, linkUrl, tagName).render();

        // Check created link
        assertEquals(linksDetailsPage.getUrl(), linkUrl, "Wrong url for created link. Server A");
        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title. Server A");

        // "Links" page with at least one link is opened
        linksPage = linksDetailsPage.browseToLinksList();

        // Delete link
        linksPage.deleteLinkWithConfirm(linkName);

        // Check Link was deleted at the server A
        assertTrue(linksPage.isNoLinksDisplayed(), "Link wasn't deleted. Server A");

        ShareUser.logout(drone);

        // verify that created link at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Links page for site is opened
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);

        linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Check Link was deleted at the server B
        assertTrue(linksPage.isNoLinksDisplayed(), "Link wasn't deleted. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15853:Add comment
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>At least one link is created for current site</li>
     * <li>'Link's Details' page for link is opened</li>
     * <li>Type in any text into 'Add comment' text field</li>
     * <li>Hit 'Create comment' button</li>
     * <li>'Link's Details' page is opened. The comment is successfully added to the link</li>
     * <li>Go to server B and verify Link's details page for created link</li>
     * <li>Added comment is present for current link on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9175() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String linkName = "link_" + getRandomString(5);
        String linkUrl = "www.alfresco.com";
        String tagName = "tag_" + getRandomString(3);
        String commentText = RandomUtil.getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Links page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.LINKS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        LinksPage linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Create new link
        LinksDetailsPage linksDetailsPage = linksPage.createLink(linkName, linkUrl, tagName).render();

        // Check created link
        assertEquals(linksDetailsPage.getUrl(), linkUrl, "Wrong url for created link. Server A");
        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title. Server A");

        linksDetailsPage.addComment(commentText);

        // Add comment
        LinkComment addedComment = linksDetailsPage.getLinkComment(commentText);
        assertEquals(addedComment.getText(), commentText, "Comment link don't added. Server A.");
        assertTrue(addedComment.isCorrect(), "Added comment is broken. Server A.");

        ShareUser.logout(drone);

        // verify that comment was added to the link at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Links page for site is opened
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);
        linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Check created link is displayed correctly on server B
        assertTrue(linksPage.isLinkPresented(linkName), "Link with title " + linkName + " don't displayed. Server B");

        // verify Link's details page for created link
        linksDetailsPage = linksPage.clickLink(linkName).render();

        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title. Server B");

        // Verify Added comment is present for current link on server B
        addedComment = linksDetailsPage.getLinkComment(commentText);
        assertEquals(addedComment.getText(), commentText, "Comment link don't added. Server B.");
        assertTrue(addedComment.isCorrect(), "Added comment is broken. Server B.");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15854:Edit comment
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>At least one link is created for current site</li>
     * <li>'Link's Details' page for link is opened</li>
     * <li>Type in any text into 'Add comment' text field</li>
     * <li>Hit 'Create comment' button</li>
     * <li>'Link's Details' page is opened. The comment is successfully added to the link</li>
     * <li>Go to server B and verify Link's details page for created link</li>
     * <li>Added comment is present for current link on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9176() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String linkName = "link_" + getRandomString(5);
        String linkUrl = "www.alfresco.com";
        String tagName = "tag_" + getRandomString(3);
        String commentText = RandomUtil.getRandomString(5);
        String newCommentText = RandomUtil.getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Links page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.LINKS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        LinksPage linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Create new link
        LinksDetailsPage linksDetailsPage = linksPage.createLink(linkName, linkUrl, tagName).render();

        // Check created link
        assertEquals(linksDetailsPage.getUrl(), linkUrl, "Wrong url for created link. Server A");
        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title. Server A");

        // Add comment
        linksDetailsPage.addComment(commentText);

        // In the list of comments select the comment to edit and click on 'Edit'
        LinkComment addedComment = linksDetailsPage.getLinkComment(commentText);
        assertEquals(addedComment.getText(), commentText, "Comment link don't added. Server A.");
        assertTrue(addedComment.isCorrect(), "Added comment is broken. Server A.");

        // Type any new comment in the "Edit Comment" box and click "Update" button
        linksDetailsPage = addedComment.editComment(newCommentText);
        LinkComment editedComment = linksDetailsPage.getLinkComment(newCommentText);

        // The updated comment is displayed correctly on 'Link's Details' page
        assertEquals(editedComment.getText(), newCommentText, "Comment don't edited. Server A.");
        assertTrue(editedComment.isCorrect(), "Edited comment is broken. Server A.");

        ShareUser.logout(drone);

        // verify that comment was edited for the link at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Links page for site is opened
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);
        linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Check created link is displayed correctly on server B
        assertTrue(linksPage.isLinkPresented(linkName), "Link with title " + linkName + " don't displayed. Server B");

        // verify Link's details page for created link
        linksDetailsPage = linksPage.clickLink(linkName).render();

        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title. Server B");

        // Verify Added comment is present for current link on server B
        addedComment = linksDetailsPage.getLinkComment(newCommentText);

        // The updated comment is displayed correctly on 'Link's Details' page
        assertEquals(addedComment.getText(), newCommentText, "Comment don't edited. Server B.");
        assertTrue(addedComment.isCorrect(), "Edited comment is broken. Server B.");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_15855:Delete comment
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>At least one link with any  comments  is created for current site</li>
     * <li>'Link's Details' page for link is opened</li>
     * <li>In the list of comments select the comment  and click on 'Delete'</li>
     * <li>Press 'Delete' button</li>
     * <li>The comment is successfully deleted. It is not displayed on 'Link's Details' page</li>
     * <li>Go to server B and verify Link's details page for created link</li>
     * <li>Comment's section is updated on LInk's details page on server B :  comment is missing</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9177() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String linkName = "link_" + getRandomString(5);
        String linkUrl = "www.alfresco.com";
        String tagName = "tag_" + getRandomString(3);
        String commentText = RandomUtil.getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Links page for site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.LINKS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        LinksPage linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Create new link
        LinksDetailsPage linksDetailsPage = linksPage.createLink(linkName, linkUrl, tagName).render();

        // Check created link
        assertEquals(linksDetailsPage.getUrl(), linkUrl, "Wrong url for created link. Server A");
        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title. Server A");

        linksDetailsPage.addComment(commentText);

        // Add comment
        LinkComment addedComment = linksDetailsPage.getLinkComment(commentText);
        assertEquals(addedComment.getText(), commentText, "Comment link don't added. Server A.");
        assertTrue(addedComment.isCorrect(), "Added comment is broken. Server A.");

        linksDetailsPage = addedComment.deleteComment();

        try
        {
            linksDetailsPage.getLinkComment(commentText);
            fail(String.format("Comment[%s] don't deleted.  Server A", commentText));
        }
        catch (PageOperationException e)
        {
            logger.info("Can't found deleted comment. It's OK! Server A");
        }

        ShareUser.logout(drone);

        // verify that comment was added to the link at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Links page for site is opened
        siteDashPage = SiteUtil.openSiteDashboard(drone, siteName);
        linksPage = siteDashPage.getSiteNav().selectLinksPage();

        // Check created link is displayed correctly on server B
        assertTrue(linksPage.isLinkPresented(linkName), "Link with title " + linkName + " don't displayed. Server B");

        // verify Link's details page for created link
        linksDetailsPage = linksPage.clickLink(linkName).render();

        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title. Server B");

        // Verify that comment was deleted for current link on server B
        try
        {
            linksDetailsPage.getLinkComment(commentText);
            fail(String.format("Comment[%s] don't delete.  Server B", commentText));
        }
        catch (PageOperationException e)
        {
            logger.info("Can't found deleted comment. It's OK! Server B");
        }

        ShareUser.logout(drone);
    }

}