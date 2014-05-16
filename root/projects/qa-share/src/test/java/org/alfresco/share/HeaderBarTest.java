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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share;

import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.MyFilesPage;
import org.alfresco.po.share.site.document.SharedFilesPage;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUser.SiteOperation;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.collections.CollectionUtils;

/**
 * This Class covers the test for Header.
 * 
 * @author nshah Dated: 06/03/2014
 */
@Listeners(FailedTestListener.class)
public class HeaderBarTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(SiteDashBoardTest.class);

    protected String testUser;

    protected String siteName = "";

    /**
     * Class includes: Tests from TestLink in Area: Site DashBoard Tests
     * <ul>
     * <li>Perform an Activity on Site</li>
     * <li>Site DashBoard shows Activity Feed</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_ALF_9296() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create one user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });
    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9296() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String siteNameNew = getSiteName(testName + System.currentTimeMillis() + "new");

        // Login through created user.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Navigate to My sites- Dashlet page

        DashBoardPage dashBoard = ShareUser.openUserDashboard(drone);
        Assert.assertTrue(dashBoard.getNav().isCreateSitePresent());
        
        ShareUser.selectMyDashBoard(drone);

        // Create a site by clicking on create site link.
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Verify/Assert for new site is present in dashlets.
        SitePage sitePage = ShareUser.navigateToSiteThroughDashlet(drone, siteName, SiteOperation.Open).render();
        Assert.assertNotNull(sitePage);
        Assert.assertEquals(true, sitePage.isSite(siteName));
        
        // Navigate back to Site Dashlet page.
        dashBoard = ShareUser.navigateToSiteThroughDashlet(drone, siteName, SiteOperation.Delete).render();
        Assert.assertFalse(dashBoard.getNav().selectSearchForSites().render().getSiteList().contains(siteName));

        ShareUser.createSite(drone, siteNameNew, SITE_VISIBILITY_PUBLIC);
        
        // Verify create site link is still present.
        Assert.assertNotNull(sitePage);
        Assert.assertEquals(true, sitePage.isSite(siteNameNew));

    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_ALF_9297() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // Create one user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });
    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9297() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String publicSiteName = getSiteName(testName + System.currentTimeMillis() + "private");
        String privateSiteName = getSiteName(testName + System.currentTimeMillis() + "public");
        String moderateSiteName = getSiteName(testName + System.currentTimeMillis() + "moderate");

        // Login through created user.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Navigate to My sites- Dashlet page

        // Create 3 sites: private, public, Moderated
        ShareUser.createSite(drone, publicSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, moderateSiteName, SITE_VISIBILITY_MODERATED);
        ShareUser.createSite(drone, privateSiteName, SITE_VISIBILITY_PRIVATE);

        // search site and check available options are present.
        List<String> sitesVisited = SiteUtil.getSiteList(drone, siteName);
        Assert.assertTrue(sitesVisited.size() > 0);

        // Click on available site.
        Assert.assertTrue(SiteUtil.isSiteFound(drone, publicSiteName));
        Assert.assertTrue(SiteUtil.isSiteFound(drone, privateSiteName));
        Assert.assertTrue(SiteUtil.isSiteFound(drone, moderateSiteName));
        
        // Verify Site finder option is present in Sites menu
        SiteDashboardPage siteDashBoardPage = ShareUser.openSiteDashboard(drone, publicSiteName).render();
        Assert.assertTrue(siteDashBoardPage.getNav().selectSearchForSites().render() instanceof SiteFinderPage);

    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_ALF_9298() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String publicSiteName = getSiteName(testName);

        // Create one user every time since reusing if user will change the position of Site listed in Site Menu
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        // Login through created user.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, publicSiteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);

    }

    @Test(groups = { "SiteDashboard", "BambooBug" })
    public void ALF_9298() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openUserDashboard(drone);

        SharePage sharePage = ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.ARROW_RIGHT, Keys.RETURN).render();
        Assert.assertTrue(sharePage instanceof MyFilesPage, "Could not navigate to MyfilesPage using keys: QA-597");

        ShareUser.openUserDashboard(drone);

        sharePage = ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.RETURN).render();
        Assert.assertTrue(sharePage instanceof SharedFilesPage, "Could not navigate to SharedFilesPage");

        ShareUser.openUserDashboard(drone);

        sharePage = ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.ARROW_RIGHT, Keys.RETURN).render();
        Assert.assertTrue(sharePage instanceof DashBoardPage, "Expected page is DashBoardPage but found: " + getSharePage(drone).getTitle());

        ShareUser.navigateMenuUsingKeys(drone, Keys.RETURN);

        ShareUser.openUserDashboard(drone);
        Assert.assertTrue(
                ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.ARROW_RIGHT, Keys.RETURN, Keys.RETURN) instanceof SiteDashboardPage,
                "Expected page is SiteDashBoardPage but found: " + getSharePage(drone).getTitle());

        ShareUser.openUserDashboard(drone);
        Assert.assertTrue(
                ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.ARROW_RIGHT, Keys.RETURN, Keys.ARROW_DOWN, Keys.RETURN) instanceof SiteFinderPage,
                "Expected page is SiteFinderPage but found: " + getSharePage(drone).getTitle());

        ShareUser.openUserDashboard(drone);
        Assert.assertTrue(
                ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.ARROW_RIGHT, Keys.RETURN) instanceof DashBoardPage,
                "Expected page is DashBoardPage but found: " + getSharePage(drone).getTitle());

        ShareUser.navigateMenuUsingKeys(drone, Keys.RETURN);

        ShareUser.openUserDashboard(drone);
        Assert.assertTrue(ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.ARROW_RIGHT, Keys.RETURN, Keys.ARROW_DOWN,
                Keys.ARROW_DOWN, Keys.RETURN) instanceof CreateSitePage, "Expected page is CreateSitePAge but found: " + getSharePage(drone).getTitle());

        ((CreateSitePage) getSharePage(drone)).clickCancel();

        ShareUser.openUserDashboard(drone);
        Assert.assertTrue(ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.ARROW_RIGHT, Keys.RETURN, Keys.ARROW_DOWN,
                Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.RETURN, Keys.RETURN) instanceof SiteDashboardPage, "Expected page is SiteDashBoardPage but found: "
                + getSharePage(drone).getTitle());

        ShareUser.openUserDashboard(drone);
        Assert.assertTrue(
                ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.RETURN, Keys.RETURN) instanceof MyTasksPage,
                "Expected page is MyTaskPage but found: " + getSharePage(drone).getTitle());

        ShareUser.openUserDashboard(drone);
        Assert.assertTrue(ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.RETURN,
                Keys.ARROW_DOWN, Keys.RETURN) instanceof MyWorkFlowsPage, "Expected page is MyWorkFlowPage but found: " + getSharePage(drone).getTitle());

        ShareUser.openUserDashboard(drone);
        Assert.assertTrue(ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.RETURN) instanceof PeopleFinderPage,
                "Expected page is PeopleFinderPage but found: " + getSharePage(drone).getTitle());

        ShareUser.openUserDashboard(drone);
        Assert.assertTrue(
                ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.RETURN) instanceof RepositoryPage,
                "Expected page is RepositoryPage but found: " + getSharePage(drone).getTitle());

        ShareUser.openUserDashboard(drone);
        Assert.assertTrue(ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.RETURN,
                Keys.RETURN) instanceof AdvanceSearchPage, "Expected page is AdvanceSearchPage but found: " + getSharePage(drone).getTitle());

        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        sharePage = ShareUser.navigateMenuUsingKeys(drone, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.RETURN).render();
        Assert.assertTrue(sharePage instanceof AdminConsolePage, "Expected page is AdminConsolePage but found: " + getSharePage(drone).getTitle());
        ShareUser.logout(drone);

    }

    // TODO: for cloud as well
    @Test(groups = { "SiteDashboard" })
    public void ALF_14186() throws Exception
    {
        // Login through Admin user.
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Navigate to User DashBoard
        DashBoardPage dashBoard = ShareUser.openUserDashboard(drone);

        // TODO: Use util: ShareUserWorkFlow.navigateToMyTasksPage(drone)?
        MyTasksPage taskPage = dashBoard.getNav().selectMyTasks().render();

        Assert.assertNotNull(taskPage);

        dashBoard = ShareUser.openUserDashboard(drone);

        if (!isAlfrescoVersionCloud(drone))
        {
            MyWorkFlowsPage myWorkFlowsPage = dashBoard.getNav().selectWorkFlowsIHaveStarted().render();
            Assert.assertNotNull(myWorkFlowsPage);
        }
    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_ALF_9291() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");

        // Create one user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser2 });
    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9291() throws Exception
    {

        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");
        String siteName2 = getSiteName(testName + System.currentTimeMillis() + "2");
        String siteName1 = getSiteName(testName + System.currentTimeMillis() + "1");

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to User DashBoard
        ShareUser.openUserDashboard(drone);

        // Create a different new site.
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);

        // log out user
        ShareUser.logout(drone);

        // Create a any new user using admin
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        // login as a new user
        ShareUser.openUserDashboard(drone);

        MySitesDashlet dashlet = ShareUser.getDashlet(drone, "my-sites").render();

        // Verify that there is no site exists in Sites > recent site menu
        Assert.assertFalse(CollectionUtils.hasElements(dashlet.getSites()));

    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_ALF_9292() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");

        // Create one user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser2 });

    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9292() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");
        String siteName2 = getSiteName(testName + System.currentTimeMillis() + "2");
        String siteName1 = getSiteName(testName + System.currentTimeMillis() + "1");

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to User DashBoard
        ShareUser.openUserDashboard(drone);

        // Create a different new site.
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName2, UserRole.COLLABORATOR);

        // log out user
        ShareUser.logout(drone);

        // Create a any new user using admin
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // login as a new user
        ShareUser.openSiteDashboard(drone, siteName1);
        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName2);
        Assert.assertTrue(dashBoard.getNav().getRecentSitesPresent().contains(siteName1));
        Assert.assertTrue(dashBoard.getNav().getRecentSitesPresent().contains(siteName2));

    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_ALF_9293() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");

        // Create one user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser2 });

    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9293() throws Exception
    {

        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");
        String siteName1 = getSiteName(testName + System.currentTimeMillis() + "1");
        String siteName2 = getSiteName(testName + System.currentTimeMillis() + "2");
        String siteName3 = getSiteName(testName + System.currentTimeMillis() + "3");
        String siteName4 = getSiteName(testName + System.currentTimeMillis() + "4");
        String siteName5 = getSiteName(testName + System.currentTimeMillis() + "5");
        String siteName6 = getSiteName(testName + System.currentTimeMillis() + "6");

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to User DashBoard.
        // Verify My Dash let is present.
        // Create a 6 new site.
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName3, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName4, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName5, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName6, SITE_VISIBILITY_PUBLIC);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName2, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName3, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName4, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName5, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName6, UserRole.COLLABORATOR);

        // log out user
        ShareUser.logout(drone);

        // Create a any new user2 using admin
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName1);
        ShareUser.openSiteDashboard(drone, siteName2);
        ShareUser.openSiteDashboard(drone, siteName3);
        ShareUser.openSiteDashboard(drone, siteName4);
        ShareUser.openSiteDashboard(drone, siteName5);
        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName6);

        // VErify recent sites show maximum 5 sites shown.
        List<String> siteNames = dashBoard.getNav().getRecentSitesPresent();
        Assert.assertTrue(siteNames.size() == 5);
        Assert.assertFalse(siteNames.contains(siteName1));

    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_ALF_9294() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");

        // Create one user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser2 });

    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9294() throws Exception
    {

        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");
        String siteName1 = getSiteName(testName + System.currentTimeMillis() + "one");
        String siteName2 = getSiteName(testName + System.currentTimeMillis() + "two");
        String siteName3 = getSiteName(testName + System.currentTimeMillis() + "three");
        String siteName4 = getSiteName(testName + System.currentTimeMillis() + "four");
        String siteName5 = getSiteName(testName + System.currentTimeMillis() + "five");
        String siteName6 = getSiteName(testName + System.currentTimeMillis() + "six");

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to User DashBoard.
        // Verify My Dash let is present.
        // Create a 6 new site.
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC).render();
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC).render();
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName2, UserRole.COLLABORATOR);
        ShareUser.createSite(drone, siteName3, SITE_VISIBILITY_PUBLIC).render();
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName3, UserRole.COLLABORATOR);
        ShareUser.createSite(drone, siteName4, SITE_VISIBILITY_PUBLIC).render();
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName4, UserRole.COLLABORATOR);
        ShareUser.createSite(drone, siteName5, SITE_VISIBILITY_PUBLIC).render();
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName5, UserRole.COLLABORATOR);
        ShareUser.createSite(drone, siteName6, SITE_VISIBILITY_PUBLIC).render();
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName6, UserRole.COLLABORATOR);

        // log out user
        ShareUser.logout(drone);

        // Create a any new user2 using admin
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName1);
        dashBoard.getNav().isSiteFavourtie();
        dashBoard.getNav().setSiteAsFavourite();

        ShareUser.openSiteDashboard(drone, siteName2);
        dashBoard.getNav().isSiteFavourtie();
        dashBoard.getNav().setSiteAsFavourite();

        ShareUser.openSiteDashboard(drone, siteName3);
        ShareUser.openSiteDashboard(drone, siteName4);
        ShareUser.openSiteDashboard(drone, siteName5);
        dashBoard = ShareUser.openSiteDashboard(drone, siteName6);

        // VErify recent sites show maximum 5 sites shown.
        List<String> siteNames = dashBoard.getNav().getRecentSitesPresent();
        Assert.assertTrue(siteNames.size() == 5);
        Assert.assertTrue(siteName6.equals(siteNames.get(0)));

    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_ALF_9304() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");

        // Create one user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser2 });
    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9304() throws Exception
    {

        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");
        String siteName1 = getSiteName(testName + System.currentTimeMillis() + "1");
        String siteName2 = getSiteName(testName + System.currentTimeMillis() + "2");
        String siteName3 = getSiteName(testName + System.currentTimeMillis() + "3");
        String siteName4 = getSiteName(testName + System.currentTimeMillis() + "4");
        String siteName5 = getSiteName(testName + System.currentTimeMillis() + "5");
        String siteName6 = getSiteName(testName + System.currentTimeMillis() + "6");

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to User DashBoard.
        // Verify My Dash let is present.
        // Create a 6 new site.
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName3, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName4, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName5, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName6, SITE_VISIBILITY_PUBLIC);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName2, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName3, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName4, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName5, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName6, UserRole.COLLABORATOR);

        // log out user
        ShareUser.logout(drone);

        // Create a any new user2 using admin
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName1);
        dashBoard.getNav().isSiteFavourtie();
        dashBoard.getNav().setSiteAsFavourite();

        ShareUser.openSiteDashboard(drone, siteName2);
        dashBoard.getNav().isSiteFavourtie();
        dashBoard.getNav().setSiteAsFavourite();

        ShareUser.openSiteDashboard(drone, siteName3);
        ShareUser.openSiteDashboard(drone, siteName4);
        ShareUser.openSiteDashboard(drone, siteName5);
        dashBoard = ShareUser.openSiteDashboard(drone, siteName6);

        Assert.assertTrue(dashBoard.getNav().doesAnyFavouriteSiteExist());
        List<String> sites = dashBoard.getNav().getFavouriteSites();
        Assert.assertTrue(sites.contains(siteName1));
        Assert.assertTrue(sites.contains(siteName2));

    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_ALF_9305() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");

        // Create one user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser2 });

    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9305() throws Exception
    {

        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");
        String siteName1 = getSiteName(testName + System.currentTimeMillis() + "1");
        String siteName2 = getSiteName(testName + System.currentTimeMillis() + "2");
        String siteName3 = getSiteName(testName + System.currentTimeMillis() + "3");

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to User DashBoard.
        // Verify My Dash let is present.
        // Create a 3 new site.
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName3, SITE_VISIBILITY_PUBLIC);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName2, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName3, UserRole.COLLABORATOR);

        // log out user
        ShareUser.logout(drone);

        // Create a any new user2 using admin
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName1);
        ShareUser.openSiteDashboard(drone, siteName2);
        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName3);

        Assert.assertFalse(dashBoard.getNav().doesAnyFavouriteSiteExist());

    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_ALF_9306() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");

        // Create one user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser2 });
    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9306() throws Exception
    {

        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "-1");
        String testUser2 = getUserNameFreeDomain(testName + "-2");
        String siteName1 = getSiteName(testName + System.currentTimeMillis() + "1");
        String siteName2 = getSiteName(testName + System.currentTimeMillis() + "2");
        String siteName3 = getSiteName(testName + System.currentTimeMillis() + "3");
        String siteName4 = getSiteName(testName + System.currentTimeMillis() + "4");
        String siteName5 = getSiteName(testName + System.currentTimeMillis() + "5");
        String siteName6 = getSiteName(testName + System.currentTimeMillis() + "6");

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to User DashBoard.
        // Verify My Dash let is present.
        // Create a 6 new site.
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName3, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName4, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName5, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName6, SITE_VISIBILITY_PUBLIC);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName2, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName3, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName4, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName5, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName6, UserRole.COLLABORATOR);

        // log out user
        ShareUser.logout(drone);

        // Create a any new user2 using admin
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName1);
        dashBoard.getNav().isSiteFavourtie();
        dashBoard.getNav().setSiteAsFavourite();

        ShareUser.openSiteDashboard(drone, siteName2);
        dashBoard.getNav().isSiteFavourtie();
        dashBoard.getNav().setSiteAsFavourite();

        ShareUser.openSiteDashboard(drone, siteName3);
        dashBoard.getNav().isSiteFavourtie();
        dashBoard.getNav().setSiteAsFavourite();
        ShareUser.openSiteDashboard(drone, siteName4);
        dashBoard.getNav().isSiteFavourtie();
        dashBoard.getNav().setSiteAsFavourite();
        ShareUser.openSiteDashboard(drone, siteName5);
        dashBoard.getNav().isSiteFavourtie();
        dashBoard.getNav().setSiteAsFavourite();
        dashBoard = ShareUser.openSiteDashboard(drone, siteName6);
        dashBoard.getNav().isSiteFavourtie();
        dashBoard.getNav().setSiteAsFavourite();

        Assert.assertTrue(dashBoard.getNav().doesAnyFavouriteSiteExist());
        List<String> sites = dashBoard.getNav().getFavouriteSites();
        Assert.assertTrue(sites.contains(siteName1));
        Assert.assertTrue(sites.contains(siteName2));
        Assert.assertTrue(sites.contains(siteName3));
        Assert.assertTrue(sites.contains(siteName4));
        Assert.assertTrue(sites.contains(siteName5));
        Assert.assertTrue(sites.contains(siteName6));

    }

    // TODO: No data prep is needed for this because user has to be new.
    @Test(groups = { "SiteDashboard" })
    public void ALF_9307() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + System.currentTimeMillis() + "-1");
        String testUser2 = getUserNameFreeDomain(testName + System.currentTimeMillis() + "-2");
        String siteName1 = getSiteName(testName + System.currentTimeMillis() + "1");
      

        // Create one user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser2 });

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to User DashBoard.
        // Verify My Dash let is present.
        // Create a 6 new site.
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);
       
        // log out user
        ShareUser.logout(drone);

        // Create a any new user2 using admin
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName1);
        Assert.assertFalse(dashBoard.getNav().isSiteFavourtie());
        dashBoard.getNav().setSiteAsFavourite();
        Assert.assertTrue(dashBoard.getNav().isSiteFavourtie());
        
        Assert.assertTrue(dashBoard.getNav().doesAnyFavouriteSiteExist());
        List<String> sites = dashBoard.getNav().getFavouriteSites();
        Assert.assertTrue(sites.contains(siteName1));    

    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9308() throws Exception
    {

        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + System.currentTimeMillis() + "-1");
        String testUser2 = getUserNameFreeDomain(testName + System.currentTimeMillis() + "-2");
        String siteName1 = getSiteName(testName + System.currentTimeMillis() + "1");
        String siteName2 = getSiteName(testName + System.currentTimeMillis() + "2");
        String siteName3 = getSiteName(testName + System.currentTimeMillis() + "3");

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser2 });

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to User DashBoard.
        // Verify My Dash let is present.
        // Create a 6 new site.
        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName3, SITE_VISIBILITY_PUBLIC);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName2, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName3, UserRole.COLLABORATOR);

        // log out user
        ShareUser.logout(drone);

        // Create a any new user2 using admin
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName1);
        Assert.assertFalse(dashBoard.getNav().isSiteFavourtie());
        dashBoard.getNav().setSiteAsFavourite();
        Assert.assertTrue(dashBoard.getNav().isSiteFavourtie());
        dashBoard.getNav().removeFavourite();
        Assert.assertFalse(dashBoard.getNav().isSiteFavourtie());

        ShareUser.openSiteDashboard(drone, siteName2);
        Assert.assertFalse(dashBoard.getNav().isSiteFavourtie());
        dashBoard.getNav().setSiteAsFavourite();
        Assert.assertTrue(dashBoard.getNav().isSiteFavourtie());
        dashBoard.getNav().removeFavourite();
        Assert.assertFalse(dashBoard.getNav().isSiteFavourtie());

        ShareUser.openSiteDashboard(drone, siteName3);
        Assert.assertFalse(dashBoard.getNav().isSiteFavourtie());
        dashBoard.getNav().setSiteAsFavourite();
        Assert.assertTrue(dashBoard.getNav().isSiteFavourtie());
        dashBoard.getNav().removeFavourite();
        Assert.assertFalse(dashBoard.getNav().isSiteFavourtie());

        Assert.assertFalse(dashBoard.getNav().doesAnyFavouriteSiteExist());
        List<String> sites = dashBoard.getNav().getFavouriteSites();
        Assert.assertFalse(CollectionUtils.hasElements(sites));

    }

    @Test(groups = { "SiteDashboard" })
    public void ALF_9309() throws Exception
    {

        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + System.currentTimeMillis() + "-1");
        String testUser2 = getUserNameFreeDomain(testName + System.currentTimeMillis() + "-2");
        String siteName1 = getSiteName(testName + System.currentTimeMillis() + "1");

        // Create one user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser1 });
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser2 });

        // Login through created user.
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName1, SITE_VISIBILITY_PUBLIC);

        ShareUser.logout(drone);

        // Login as user2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        SiteDashboardPage dashBoard = SiteUtil.openSiteFromSearch(drone, siteName1);
        Assert.assertFalse(dashBoard.getNav().isSiteFavourtie());
        dashBoard.getNav().setSiteAsFavourite();
        Assert.assertTrue(dashBoard.getNav().isSiteFavourtie());
        dashBoard.getNav().removeFavourite();
        Assert.assertFalse(dashBoard.getNav().isSiteFavourtie());

        Assert.assertFalse(dashBoard.getNav().doesAnyFavouriteSiteExist());
        List<String> sites = dashBoard.getNav().getFavouriteSites();
        Assert.assertFalse(CollectionUtils.hasElements(sites));

    }
}
