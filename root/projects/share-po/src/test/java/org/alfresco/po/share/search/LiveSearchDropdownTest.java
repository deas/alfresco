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

package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit tests for live search dropdown
 * 
 * @author jcule
 */
@Listeners(FailedTestListener.class)
public class LiveSearchDropdownTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(LiveSearchDropdownTest.class);
    protected String siteName;
    protected String fileName;

    private DashBoardPage dashBoard;

    @BeforeClass
    public void prepare() throws Exception
    {
        try
        {

            dashBoard = loginAs(username, password);
            siteName = "House-" + "livesearchdropdowntest" + System.currentTimeMillis();
            SiteUtil.createSite(drone, siteName, "description", "Public");
            SitePage site = drone.getCurrentPage().render();
            DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();

            CreatePlainTextContentPage contentPage = docPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
            ContentDetails contentDetails = new ContentDetails();
            fileName = "House-" + "livesearchdropdowntest" + System.currentTimeMillis();
            contentDetails.setName(fileName);
            contentDetails.setTitle("House");
            contentDetails.setDescription("House");
            contentDetails.setContent("House");
            contentPage.create(contentDetails).render();

        }
        catch (Throwable pe)
        {
            saveScreenShot("liveSearchFileUpload");
            logger.error("Cannot upload file to site ", pe);
        }
    }

    @BeforeMethod
    public void reset()
    {
        SharePage page = drone.getCurrentPage().render();
        page.getNav().selectMyDashBoard();
    }

    @AfterClass
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    /**
     * Checks that the document search result contains document name,
     * site name and user name
     */
    @Test
    public void liveSearchDocumentResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage;
        List<LiveSearchDocumentResult> liveSearchDocumentResults = new ArrayList<LiveSearchDocumentResult>();

        int counter = 0;
        int waitInMilliSeconds = 2000;
        while (counter < 3)
        {
            liveSearchResultPage = search.liveSearch(fileName).render();
            if (liveSearchResultPage.getSearchDocumentResults().size() > 0)
            {
                liveSearchDocumentResults = liveSearchResultPage.getSearchDocumentResults();
                break;
            }
            else
            {
                counter++;
                dashBoard = dashBoard.getNav().selectMyDashBoard().render();
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
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            Assert.assertEquals(liveSearchDocumentResult.getTitle().getDescription(), fileName);
            Assert.assertEquals(liveSearchDocumentResult.getSiteName().getDescription(), siteName);
            Assert.assertEquals(liveSearchDocumentResult.getUserName().getDescription(), username);
        }
    }

    /**
     * Expands document search results
     */
    @Test(dependsOnMethods = "liveSearchDocumentResult")
    public void expandLiveSearchDocumentResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(".ftl").render();
        Assert.assertNotNull(liveSearchResultPage);

        liveSearchResultPage.clickToSeeMoreDocumentResults();
        List<LiveSearchDocumentResult> liveSearchResultsPage = liveSearchResultPage.getSearchDocumentResults();

        Assert.assertTrue(liveSearchResultsPage.size() > 0);
        Assert.assertTrue(liveSearchResultPage.isDocumentsTitleVisible());
        Assert.assertTrue(liveSearchResultPage.isSitesTitleVisible());
        Assert.assertTrue(liveSearchResultPage.isPeopleTitleVisible());

    }

    /**
     * Searches for site and checks that site name is displayed in site results
     */
    @Test(dependsOnMethods = "expandLiveSearchDocumentResult")
    public void liveSearchSitesResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(siteName).render();
        Assert.assertNotNull(liveSearchResultPage);
        List<LiveSearchSiteResult> liveSearchSitesResults = liveSearchResultPage.getSearchSitesResults();
        for (LiveSearchSiteResult liveSearchSiteResult : liveSearchSitesResults)
        {
            Assert.assertEquals(liveSearchSiteResult.getSiteName().getDescription(), siteName);
        }

    }

    /**
     * Searches for username and checks that it is displayed in people search results
     */
    @Test(dependsOnMethods = "liveSearchSitesResult")
    public void liveSearchPeopleResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(username).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<LiveSearchPeopleResult> liveSearchPeopleResults = liveSearchResultPage.getSearchPeopleResults();
        for (LiveSearchPeopleResult liveSearchPeopleResult : liveSearchPeopleResults)
        {
            Assert.assertTrue(liveSearchPeopleResult.getUserName().getDescription().indexOf(username) != -1);
        }

    }

    /**
     * Clicks on close live search dropdown icon and checks that
     * the dropdown is not displayed
     */
    @Test(dependsOnMethods = "liveSearchPeopleResult")
    public void closeLiveSearchDropdown()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch("x@z").render();
        Assert.assertNotNull(liveSearchResultPage);

        liveSearchResultPage.closeLiveSearchDropdown();

        Assert.assertFalse(liveSearchResultPage.isDocumentsTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isSitesTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isPeopleTitleVisible());

    }

    /**
     * Clicks on the document name in the document search result and checks that
     * the documents details page is displayed
     */
    @Test(dependsOnMethods = "closeLiveSearchDropdown")
    public void clickOnDocumentTitle()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchResultPage.getSearchDocumentResults();
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            DocumentDetailsPage documentDetailsPage = liveSearchDocumentResult.clickOnDocumentTitle().render();
            Assert.assertEquals(documentDetailsPage.getDocumentTitle(), fileName);
        }

    }

    /**
     * Clicks on the site name in the document search result and checks
     * that document site library page is displayed
     */
    @Test(dependsOnMethods = "clickOnDocumentTitle")
    public void clickOnDocumentSiteName()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchResultPage.getSearchDocumentResults();
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            DocumentLibraryPage documentLibraryPage = liveSearchDocumentResult.clickOnDocumentSiteTitle().render();
            Assert.assertTrue(documentLibraryPage.isFileVisible(fileName));
        }

    }

    /**
     * Clicks on document user name in document search result and checks
     * that user profile page is displayed
     */
    @Test(dependsOnMethods = "clickOnDocumentSiteName")
    public void clickOnDocumentUserName()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchResultPage.getSearchDocumentResults();
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            MyProfilePage myProfilePage = liveSearchDocumentResult.clickOnDocumentUserName().render();
            Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
        }

    }

    /**
     * Clicks on the site name in sites search results and checks
     * that the site dashboard page is displayed
     */
    @Test(dependsOnMethods = "clickOnDocumentUserName")
    public void clickOnSiteResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(siteName).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<LiveSearchSiteResult> liveSearchSiteResults = liveSearchResultPage.getSearchSitesResults();
        for (LiveSearchSiteResult liveSearchSitesResult : liveSearchSiteResults)
        {
            SiteDashboardPage siteDashboardPage = liveSearchSitesResult.clickOnSiteTitle().render();
            Assert.assertTrue(siteDashboardPage.isSiteTitle(siteName));
        }
    }

    /**
     * Clicks on username in people search result and checks that
     * user profile page is displayed
     */
    @Test(dependsOnMethods = "clickOnSiteResult")
    public void clickOnPeopleResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(username).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<LiveSearchPeopleResult> liveSearchPeopleResults = liveSearchResultPage.getSearchPeopleResults();
        for (LiveSearchPeopleResult liveSearchPeopleResult : liveSearchPeopleResults)
        {
            MyProfilePage myProfilePage = liveSearchPeopleResult.clickOnUserName().render();
            Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
        }
    }

}
