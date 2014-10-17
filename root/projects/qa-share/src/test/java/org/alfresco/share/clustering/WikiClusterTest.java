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
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.WikiUtils;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class WikiClusterTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(WikiClusterTest.class);
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
     * Test - AONE_9133: Adding new wiki page
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Wiki page for created site is opened</li>
     * <li>Create new page with tag</li>
     * <li>Go to server B to the site created previously</li>
     * <li>Go to Wiki page and click 'Wiki page List' link</li>
     * <li>Created new Wiki page with added tag appears on both nodes (nodeA and nodeB)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9133() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String wikiPageTitle = "title" + getRandomString(3);
        String wikiPageText = getRandomString(10);
        String tagName = "tag_" + getRandomString(3);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Wiki page for created site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.WIKI);

        // Create new page with tag
        WikiPage wikiPage = WikiUtils.createWikiPage(drone, siteName, wikiPageTitle, wikiPageText, tagName);

        // Open Wiki Page List
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Verify that created new Wiki page with added tag appears on Server A
        Assert.assertTrue(wikiPageList.isWikiPagePresent(wikiPageTitle), "New Wiki page " + wikiPageTitle + " with added tag isn't appears on Server A");

        wikiPage = wikiPageList.clickWikiPageDetails(wikiPageTitle);

        Assert.assertEquals(wikiPage.getTagName(), tagName, "Tag " + tagName + " isn't appears on Server A");

        ShareUser.logout(drone);

        // verify that created Data list is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Wiki Page List
        wikiPage = WikiUtils.openWikiPage(drone, siteName);

        wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Verify that created new Wiki page with added tag appears on Server B
        Assert.assertTrue(wikiPageList.isWikiPagePresent(wikiPageTitle), "New Wiki page " + wikiPageTitle + " with added tag isn't appears on Server B");

        wikiPage = wikiPageList.clickWikiPageDetails(wikiPageTitle);

        Assert.assertEquals(wikiPage.getTagName(), tagName, "Tag " + tagName + " isn't appears on Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9134:Editing a wiki page
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Wiki page for created site is opened</li>
     * <li>Create new page with tag</li>
     * <li>Click Edit action for created wiki page</li>
     * <li>Do some changes</li>
     * <li>Save changes</li>
     * <li>Information is saved and changed correctly on both servers (servers A and B )</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9134() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String wikiPageTitle = "title" + getRandomString(3);
        String wikiPageText = getRandomString(10);
        String newWikiPageText = getRandomString(10);
        String tagName = "tag_" + getRandomString(3);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Wiki page for created site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.WIKI);

        // Create new page with tag
        WikiPage wikiPage = WikiUtils.createWikiPage(drone, siteName, wikiPageTitle, wikiPageText, tagName);

        // Open Wiki Page List
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Edit text for wiki page
        wikiPage = wikiPageList.editWikiPage(wikiPageTitle, newWikiPageText);

        // Check wiki page text. Information is saved and changed correctly on Server A
        assertEquals(wikiPage.getWikiText(), newWikiPageText, "Text for wiki page isn't changed. Server A");

        ShareUser.logout(drone);

        // verify that created Data list is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Wiki Page List
        wikiPage = WikiUtils.openWikiPage(drone, siteName);

        wikiPageList = wikiPage.clickWikiPageListBtn().render();

        wikiPage = wikiPageList.clickWikiPage(wikiPageTitle);

        // Check wiki page text. Information is saved and changed correctly on Server B
        assertEquals(wikiPage.getWikiText(), newWikiPageText, "Text for wiki page isn't changed. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9135:Renaming a wiki page
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Wiki page for created site is opened</li>
     * <li>Create new page with tag</li>
     * <li>Rename a wiki page</li>
     * <li>Both the updated wiki pages displaying in the top of list on servers A and B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9135() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String wikiPageTitle = "title" + getRandomString(3);
        String newWikiPageTitle = "newTitle" + getRandomString(3);
        String wikiPageText = getRandomString(10);
        String tagName = "tag_" + getRandomString(3);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Wiki page for created site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.WIKI);

        // Create new page with tag
        WikiPage wikiPage = WikiUtils.createWikiPage(drone, siteName, wikiPageTitle, wikiPageText, tagName);

        // Rename own wiki page
        wikiPage.renameWikiPage(newWikiPageTitle);

        // Open Wiki Page List
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Check wiki page titles. Both the updated wiki pages displaying in the top of list on servers A
        Assert.assertTrue(wikiPageList.isWikiPagePresent(newWikiPageTitle), "Wiki page with new title " + newWikiPageTitle + " isn't changed on Server A");
        Assert.assertTrue(wikiPageList.isWikiPagePresent(wikiPageTitle), "Wiki page with old title " + wikiPageTitle + " isn't changed on Server A");

        ShareUser.logout(drone);

        // verify that Both the updated wiki pages displaying in the top of list on servers B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Wiki Page List
        wikiPage = WikiUtils.openWikiPage(drone, siteName);

        wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Check wiki page titles. Both the updated wiki pages displaying in the top of list on servers B
        Assert.assertTrue(wikiPageList.isWikiPagePresent(newWikiPageTitle), "Wiki page with new title " + newWikiPageTitle + " isn't changed on Server B");
        Assert.assertTrue(wikiPageList.isWikiPagePresent(wikiPageTitle), "Wiki page with old title " + wikiPageTitle + " isn't changed on Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9136:Deleting tag from wiki page
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Wiki page for created site is opened</li>
     * <li>Create new page with tag</li>
     * <li>Click Edit action for created wiki page</li>
     * <li>Delete added tag</li>
     * <li>Save changes</li>
     * <li>Tag is removed for wiki page on both servers (servers A and B)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9136() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String wikiPageTitle = "title" + getRandomString(3);
        String wikiPageText = getRandomString(10);
        String tag1 = "tag1_" + getRandomString(3);
        String[] removeTags = { tag1 };

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Wiki page for created site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.WIKI);

        // Create new page with tag
        WikiPage wikiPage = WikiUtils.createWikiPage(drone, siteName, wikiPageTitle, wikiPageText, tag1);

        // Open Wiki Page List
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Check that tag added
        Assert.assertTrue(wikiPageList.checkTags(wikiPageTitle, tag1), "Tag '" + tag1 + "' isn't displayed for wiki page " + wikiPageTitle);

        // Click Edit action for created wiki page. Delete added tag
        wikiPage = wikiPageList.removeTag(wikiPageTitle, removeTags);

        wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Check that no tag displayed for wiki page at Server A
        Assert.assertTrue(wikiPageList.checkTags(wikiPageTitle, null), "Server A. Tag '" + tag1 + "' isn't removed for wiki page " + wikiPageTitle);

        ShareUser.logout(drone);

        // verify that tag is removed for Data list at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Wiki Page List
        wikiPage = WikiUtils.openWikiPage(drone, siteName);

        wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Check that no tag displayed for wiki page at Server B
        Assert.assertTrue(wikiPageList.checkTags(wikiPageTitle, null), "Server B. Tag '" + tag1 + "' isn't removed for wiki page " + wikiPageTitle);

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9137:Deleting a wiki page
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Wiki page for created site is opened</li>
     * <li>Click Delete action for created wiki page</li>
     * <li>Click Delete button</li>
     * <li>WIki page is deleted on both servers(A and B)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9137() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String wikiPageTitle = "title" + getRandomString(3);
        String wikiPageText = getRandomString(10);
        String tagName = "tag_" + getRandomString(3);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Wiki page for created site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.WIKI);

        // Create new page with tag
        WikiPage wikiPage = WikiUtils.createWikiPage(drone, siteName, wikiPageTitle, wikiPageText, tagName);

        // Open Wiki Page List
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();

        Assert.assertTrue(wikiPageList.isWikiPagePresent(wikiPageTitle), "New Wiki page " + wikiPageTitle + " with added tag isn't appears on Server A");

        // Click Delete action for created wiki page
        wikiPageList.deleteWikiWithConfirm(wikiPageTitle).render();

        wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Verify that Wiki page was removed for Server A
        Assert.assertTrue(wikiPageList.isNoWikiPagesDisplayed(), "Text 'There are currently no pages to display' isn't displayed on Server A");

        ShareUser.logout(drone);

        // Verify that Wiki page was removed for Server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Wiki Page List
        wikiPage = WikiUtils.openWikiPage(drone, siteName);

        wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Verify that Wiki page was removed for Server B
        Assert.assertTrue(wikiPageList.isNoWikiPagesDisplayed(), "Text 'There are currently no pages to display' isn't displayed on Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9138:Filling the wiki main page
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>User is logged in as Site Manager at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Wiki component page opened at Main Wiki page</li>
     * <li>Click 'Edit Page' link</li>
     * <li>Type the content for the main page in the Text box</li>
     * <li>Type a tag in the box provided and click "Add</li>
     * <li>Click 'Save' button</li>
     * <li>The page view displays the main page</li>
     * <li>Go to server B to the current site and Click "Wiki Page List" link</li>
     * <li>The newly created content for Main Page is displayed on server B</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9138() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String wikiPageTitle = "Main Page";
        String wikiPageText = getRandomString(10);
        String tagName = "tag_" + getRandomString(3);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Wiki page for created site is opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.WIKI);

        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectSiteWikiPage().render();

        // Click 'Edit Page' link
        wikiPage = wikiPage.editWikiPage();

        // Type the content for the main page in the Text box
        List<String> wikiPageTextLine = new ArrayList<String>();
        wikiPageTextLine.add(wikiPageText);
        wikiPage.editWikiText(wikiPageText);

        // Type a tag in the box provided and click "Add
        List<String> wikiTag = new ArrayList<>();
        wikiTag.add(tagName);
        wikiPage.addTag(wikiTag);

        // Click 'Save' button
        wikiPage = wikiPage.clickSaveButton();

        // The page view displays the main page
        assertEquals(wikiPage.getWikiTitle(), wikiPageTitle, "The page " + wikiPageTitle + " isn't displayed. Server A");
        assertEquals(wikiPage.getWikiText(), wikiPageText, "The page content isn't displayed. Server A");

        // Open Wiki Page List
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Verify that created new Wiki page with added tag appears on Server A
        Assert.assertTrue(wikiPageList.isWikiPagePresent(wikiPageTitle), "New Wiki page " + wikiPageTitle + " with added tag isn't appears on Server A");
        Assert.assertTrue(wikiPageList.getWikiPageTextFromPageList(wikiPageTitle).contains(wikiPageText), "New Wiki page content isn't appears on Server A");
        Assert.assertTrue(wikiPageList.checkTags(wikiPageTitle, tagName), "Tag isn't appears on Server A");

        ShareUser.logout(drone);

        // verify that created Data list is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Wiki Page List
        wikiPage = WikiUtils.openWikiPage(drone, siteName);

        // The page view displays the main page
        assertEquals(wikiPage.getWikiTitle(), wikiPageTitle, "The page " + wikiPageTitle + " isn't displayed. Server B");
        assertEquals(wikiPage.getWikiText(), wikiPageText, "The page content isn't displayed. Server B");

        wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Verify that created new Wiki page with added tag appears on Server B
        Assert.assertTrue(wikiPageList.isWikiPagePresent(wikiPageTitle), "New Wiki page " + wikiPageTitle + " with added tag isn't appears on Server B");
        Assert.assertTrue(wikiPageList.getWikiPageTextFromPageList(wikiPageTitle).contains(wikiPageText), "New Wiki page content isn't appears on Server B");
        Assert.assertTrue(wikiPageList.checkTags(wikiPageTitle, tagName), "Tag isn't appears on Server B");

        ShareUser.logout(drone);
    }
}