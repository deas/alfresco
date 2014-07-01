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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit tests for live search dropdown
 * 
 * @author jcule
 */
@Test(groups={"alfresco-one"})
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
            String random = UUID.randomUUID().toString();
            siteName = random;
            fileName = random;
            SiteUtil.createSite(drone, siteName, "description", "Public");
            SitePage site = drone.getCurrentPage().render();
            DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();

            AlfrescoVersion version = drone.getProperties().getVersion();
            if (version.isCloud())
            {
                UploadFilePage filePage = docPage.getNavigation().selectFileUpload().render();
                File file = SiteUtil.prepareFile(fileName, fileName, "");
                docPage = filePage.uploadFile(file.getCanonicalPath()).render();
                DocumentDetailsPage detailsPage = docPage.selectFile(file.getName()).render();
                EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
                propertiesPage.setName(fileName);
                detailsPage = propertiesPage.selectSave().render();
            }
            else
            {
                CreatePlainTextContentPage contentPage = docPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
                ContentDetails contentDetails = new ContentDetails();
                contentDetails.setName(fileName);
                contentDetails.setTitle("House");
                contentDetails.setDescription("House");
                contentDetails.setContent("House");
                contentPage.create(contentDetails).render();
            }

        }
        catch (Throwable pe)
        {
            saveScreenShot("liveSearchFileUpload");
            logger.error("Cannot upload file to site ", pe);
        }
    }

    @AfterClass
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void checkNoLiveSearchResults()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch("x@z").render();
        Assert.assertFalse(liveSearchResultPage.isDocumentsTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isSitesTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isPeopleTitleVisible());

    }
    
    
    /**
     * Checks that the document search result contains document name,
     * site name and user name
     * 
     * @throws InterruptedException
     */
    @Test(dependsOnMethods = "checkNoLiveSearchResults")
    public void liveSearchDocumentResult() throws Exception
    {
        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchRetry();
        Assert.assertTrue(liveSearchDocumentResults.size() > 0);
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            Assert.assertEquals(liveSearchDocumentResult.getTitle().getDescription(), fileName);
            Assert.assertEquals(liveSearchDocumentResult.getSiteName().getDescription(), siteName);
            Assert.assertEquals(liveSearchDocumentResult.getUserName().getDescription(), username.toLowerCase());
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
               
        liveSearchResultPage.closeLiveSearchDropdown();
        Assert.assertFalse(liveSearchResultPage.isDocumentsTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isSitesTitleVisible());
        Assert.assertFalse(liveSearchResultPage.isPeopleTitleVisible());
    }
   
    
    /**
     * Clicks on the document name in the document search result and checks that
     * the documents details page is displayed
     */
    @Test(dependsOnMethods = "expandLiveSearchDocumentResult")
    public void clickOnDocumentTitle() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();
        Assert.assertNotNull(liveSearchResultPage);
        
        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchResultPage.getSearchDocumentResults();
        Assert.assertTrue(liveSearchDocumentResults.size() > 0);
        
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
        Assert.assertTrue(liveSearchDocumentResults.size() > 0);
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
        Assert.assertTrue(liveSearchDocumentResults.size() > 0);
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            MyProfilePage myProfilePage = liveSearchDocumentResult.clickOnDocumentUserName().render();
            Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
        }

    }


    /**
     * Searches for site and checks that site name is displayed in site results
     */
    @Test(dependsOnMethods = "clickOnDocumentUserName")
    public void liveSearchSitesResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(siteName).render();
        Assert.assertNotNull(liveSearchResultPage);
        List<LiveSearchSiteResult> liveSearchSitesResults = liveSearchResultPage.getSearchSitesResults();
        Assert.assertTrue(liveSearchSitesResults.size() > 0);
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
        Assert.assertTrue(liveSearchPeopleResults.size() > 0);

        for (LiveSearchPeopleResult liveSearchPeopleResult : liveSearchPeopleResults)
        {
            Assert.assertTrue(liveSearchPeopleResult.getUserName().getDescription().indexOf(username) != -1);
        }

    }


    /**
     * Clicks on the site name in sites search results and checks
     * that the site dashboard page is displayed
     */
    @Test(dependsOnMethods = "liveSearchPeopleResult")
    public void clickOnSiteResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(siteName).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<LiveSearchSiteResult> liveSearchSiteResults = liveSearchResultPage.getSearchSitesResults();
        Assert.assertTrue(liveSearchSiteResults.size() > 0);
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
        Assert.assertTrue(liveSearchPeopleResults.size() > 0);
        for (LiveSearchPeopleResult liveSearchPeopleResult : liveSearchPeopleResults)
        {
            MyProfilePage myProfilePage = liveSearchPeopleResult.clickOnUserName().render();
            Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
        }
    }

    
    public List<LiveSearchDocumentResult> liveSearchRetry() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = null;
        List<LiveSearchDocumentResult> liveSearchDocumentResults = new ArrayList<LiveSearchDocumentResult>();
        int counter = 0;
        int waitInMilliSeconds = 2000;
        while (counter < 3)
        {
            liveSearchResultPage = search.liveSearch(fileName).render();
            if (liveSearchResultPage.getSearchDocumentResults().size() > 0)
            {
                liveSearchDocumentResults = liveSearchResultPage.getSearchDocumentResults();
                return liveSearchDocumentResults;
            }
            else
            {
                counter++;
                dashBoard = dashBoard.getNav().selectMyDashBoard().render();

            }
            // double wait time to not overdo solr search
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
        throw new Exception("livesearch failed");
    }
}
