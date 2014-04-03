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


package org.alfresco.po.share.search;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
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
 * @author jcule
 *
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
            /**
             * File file = SiteUtil.prepareFile();
             * fileName = "livesearchdropdowntest";
             * file = SiteUtil.prepareFile("House-" + fileName);
             **/

            dashBoard = loginAs(username, password);
            siteName = "House-livesearchdropdowntest" + System.currentTimeMillis();
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

    @Test
    public void liveSearchDocumentResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(fileName).render();

        Assert.assertNotNull(liveSearchResultPage);
        List<LiveSearchDocumentResult> liveSearchDocumentResults = liveSearchResultPage.getSearchDocumentResults();
        for (LiveSearchDocumentResult liveSearchDocumentResult : liveSearchDocumentResults)
        {
            Assert.assertEquals(liveSearchDocumentResult.getTitle(), fileName);
            Assert.assertEquals(liveSearchDocumentResult.getSiteName(), siteName);
            Assert.assertEquals(liveSearchDocumentResult.getUserName(), username);
        }

    }

    @Test(dependsOnMethods="liveSearchDocumentResult")
    public void liveSearchSitesResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(siteName).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<String> liveSearchSitesResults = liveSearchResultPage.getSearchSitesResults();
        for (String liveSearchSiteResult : liveSearchSitesResults)
        {
            Assert.assertEquals(liveSearchSiteResult, siteName);
        }

    }

    @Test(dependsOnMethods="liveSearchSitesResult")
    public void liveSearchPeopleResult()
    {
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = search.liveSearch(username).render();
        Assert.assertNotNull(liveSearchResultPage);

        List<String> liveSearchPeopleResults = liveSearchResultPage.getSearchPeopleResults();
        for (String liveSearchPeopleResult : liveSearchPeopleResults)
        {
            Assert.assertTrue(liveSearchPeopleResult.indexOf(username) != -1);
        }

    }
    
    @Test(dependsOnMethods="liveSearchPeopleResult")
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
    
}
