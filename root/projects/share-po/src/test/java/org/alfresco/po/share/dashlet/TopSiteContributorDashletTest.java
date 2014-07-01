/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.share.dashlet;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.InviteMembersPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.exception.PageException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * TopSiteContributorDashlet test class for top site contributor report dashlet page object
 * 
 * @author jcule
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class TopSiteContributorDashletTest extends AbstractSiteDashletTest
{

    private static final String TOP_SITE_CONTRIBUTOR_REPORT = "top-site-contributor-report";
    private TopSiteContributorDashlet topSiteContributorDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;

    private String random1 = UUID.randomUUID().toString();
    private String random2 = UUID.randomUUID().toString();
    private String random3 = UUID.randomUUID().toString();
    private String random4 = UUID.randomUUID().toString();
    private String random5 = UUID.randomUUID().toString();
    
    private static int firstNumberOfFiles = 7;
    private static int secondNumberOfFiles = 4;
    private static int thirdNumberOfFiles = 1;
    private static int fourthNumberOfFiles = 6;
    private static int fifthNumberOfFiles = 10;

    private DashBoardPage dashBoard;
    private InviteMembersPage membersPage;
    private SiteDashboardPage siteDashBoard;

    @BeforeTest
    public void prepare()
    {
        siteName = "topsitecontributordashlettest" + System.currentTimeMillis();
    }

    @BeforeClass
    public void loadFiles() throws Exception
    {
        // uploadDocument();
        dashBoard = loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "description", "moderated");

        logout(drone);

        createUsersAndFiles(firstNumberOfFiles, random1);
        createUsersAndFiles(secondNumberOfFiles, random2);
        createUsersAndFiles(thirdNumberOfFiles, random3);
        createUsersAndFiles(fourthNumberOfFiles, random4);
        createUsersAndFiles(fifthNumberOfFiles, random5);

    }

    @AfterClass
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    /**
     * Creates a user, invites it to the site as a collaborator and logs out
     * Collaborator logs in and uploads files to site's document library
     * 
     * @param numberOfFiles
     * @throws Exception
     */
    
    private void createUsersAndFiles(int numberOfFiles, String random1) throws Exception
    {
 
        createEnterpriseUser(random1);
        loginAs(username, password).render();

        navigateToSiteDashboard();
        SitePage site = drone.getCurrentPage().render();
        membersPage = site.getSiteNav().selectInvite().render();
        List<String> searchUsers = userSearchRetry(random1);
        membersPage.clickAddUser(random1);
        membersPage.selectInviteeAndAssignRole("(" + random1 + ")", UserRole.COLLABORATOR);
        membersPage.clickInviteButton();
        logout(drone);
        loginAs(random1, UNAME_PASSWORD).render();
        MyTasksDashlet task = dashBoard.getDashlet("tasks").render();
        task = task.clickOnTask(siteName);
        task.acceptInvitaton();

        navigateToSiteDashboard();
        site = drone.getCurrentPage().render();

        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();

        for (int i = 0; i < numberOfFiles; i++)
        {
            String random = UUID.randomUUID().toString();
            File file = SiteUtil.prepareFile(random, random, ".txt");
            docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
            docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();

        }
        logout(drone);
    }

    /**
     * Drags and drops Top site contributor report dashlet to site's dashboard
     */
    @Test
    public void instantiateDashlet() throws Exception
    {
        DashBoardPage boardPage = loginAs(username, password).render();
        SiteFinderPage siteFinder = boardPage.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        SiteDashboardPage siteDashBoard = siteFinder.selectSite(siteName).render();
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlet.TOP_SITE_CONTRIBUTOR_REPORT, 2).render();
        topSiteContributorDashlet = siteDashBoard.getDashlet(TOP_SITE_CONTRIBUTOR_REPORT).render();
        Assert.assertNotNull(topSiteContributorDashlet);
    }

    /**
     * Checks Top Site Contributor report counts
     * @throws Exception
     */
    @Test(dependsOnMethods = "instantiateDashlet")
    public void testTopSiteContributorCounts() throws Exception
    {
        List<String> topSiteContributorCounts = topSiteContributorDashlet.getTopSiteContributorCounts();
        //assert the counts
        Assert.assertTrue(topSiteContributorCounts.size() == 5);
          
        Assert.assertTrue(topSiteContributorCounts.contains(Integer.toString(firstNumberOfFiles)));
        Assert.assertTrue(topSiteContributorCounts.contains(Integer.toString(secondNumberOfFiles)));
        Assert.assertTrue(topSiteContributorCounts.contains(Integer.toString(thirdNumberOfFiles)));
        Assert.assertTrue(topSiteContributorCounts.contains(Integer.toString(fourthNumberOfFiles)));
        Assert.assertTrue(topSiteContributorCounts.contains(Integer.toString(fifthNumberOfFiles)));

    }

    
    /**
     * Checks Top Site Contributor report users
     * @throws Exception
     */
    @Test(dependsOnMethods = "testTopSiteContributorCounts")
    public void testTopSiteContributorUsers() throws Exception
    {
        List<String> topSiteContributorUsers = topSiteContributorDashlet.getTopSiteContributorUsers();
        //assert the users
        Assert.assertTrue(topSiteContributorUsers.size() == 5);
          
        Assert.assertTrue(topSiteContributorUsers.contains(random1));
        Assert.assertTrue(topSiteContributorUsers.contains(random2));
        Assert.assertTrue(topSiteContributorUsers.contains(random3));
        Assert.assertTrue(topSiteContributorUsers.contains(random4));
        Assert.assertTrue(topSiteContributorUsers.contains(random5));

    }
    
    /**
     * Site members user search retry
     * 
     * @param user
     * @return
     */
    private List<String> userSearchRetry(String user)
    {
        int counter = 0;
        int waitInMilliSeconds = 2000;
        while (counter < retrySearchCount)
        {
            List<String> searchUsers = membersPage.searchUser(user);
            if (searchUsers.size() > 0)
            {
                return searchUsers;
            }
            else
            {
                counter++;
                drone.getCurrentPage().render();
            }
            // double wait time to not over do solr search
            waitInMilliSeconds = (waitInMilliSeconds * 2);
            synchronized (this)
            {
                try
                {
                    this.wait(waitInMilliSeconds);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        throw new PageException("user search failed");
    }
}
