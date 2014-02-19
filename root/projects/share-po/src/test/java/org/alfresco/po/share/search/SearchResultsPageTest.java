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
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify search page elements are in place.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
public class SearchResultsPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    
    @BeforeClass(groups={"Enterprise-only", "alfresco-one"})
    public void prepare() throws Exception
    {
        dashBoard = loginAs(username, password);
    }
    
    @BeforeMethod(groups={"Enterprise-only", "alfresco-one"})
    public void reset()
    {
        SharePage page = drone.getCurrentPage().render();
        page.getNav().selectMyDashBoard();
    }
    
    @Test(groups="alfresco-one")
    public void searchEmptyResult()
    {
        SearchBox search = dashBoard.getSearch();
        SearchResultsPage resultPage = search.search("y@z").render();
        Assert.assertNotNull(resultPage);
        Assert.assertFalse(resultPage.hasResults());
    }
    
    @Test(groups = {"Enterprise-only"},dependsOnMethods="searchEmptyResult")
    public void selectNthSearchResult() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        SearchResultsPage resultPage = search.search("ipsum").render();
        Assert.assertNotNull(resultPage);

        DocumentDetailsPage itemPage = resultPage.selectItem(1).render();
        Assert.assertTrue(itemPage.getTitle().contains("Document Details"));
    }
    
    @Test(groups = {"Enterprise-only"}, dependsOnMethods="selectNthSearchResult")
    public void selectSearchResultOfTypeFolder() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        SearchResultsPage resultPage = search.search("Images").render();
        Assert.assertNotNull(resultPage);

        DocumentLibraryPage itemPage = resultPage.selectItem("Images").render();
        Assert.assertTrue(itemPage.getTitle().contains("Library"));
    }
    
    @Test(groups = {"Enterprise-only"}, dependsOnMethods="selectSearchResultOfTypeFolder")
    public void selectSearchResultOfTypeWiki() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        SearchResultsPage resultPage = search.search("Project").render();
        Assert.assertNotNull(resultPage);
        
        WikiPage itemPage = resultPage.selectItem("Main Page").render();
        Assert.assertTrue(itemPage.getTitle().contains("Wiki"));
    }
    
    @Test(groups = {"Enterprise-only"}, dependsOnMethods="selectSearchResultOfTypeWiki")
    public void selectSearchResultOfTypeDataList() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        SearchResultsPage resultPage = search.search("Issue Log").render();
        Assert.assertNotNull(resultPage);
        
        DataListPage itemPage = resultPage.selectItem("Issue Log").render();
        Assert.assertTrue(itemPage.getTitle().contains("Project Lists"));
    }
    
    @Test(groups = {"Enterprise-only"}, dependsOnMethods="selectSearchResultOfTypeDataList")
    public void pagination()
    {
        SearchBox search = dashBoard.getSearch();
        SearchResultsPage result = search.search("a").render();
        RepositoryResultsPage repoResults = result.selectRepository().render();
        repoResults = repoResults.search("email").render();
        
        boolean hasPagination = repoResults.paginationDisplayed();
        Assert.assertTrue(hasPagination);
        int count = repoResults.count();
        Assert.assertTrue(count > 76);
        
        int maxPaginationPagePosition = 2;
        Assert.assertEquals(repoResults.paginationCount(), maxPaginationPagePosition);
        
        int paginationPosition = repoResults.getPaginationPosition();
        Assert.assertEquals(paginationPosition, 1);
        
        Assert.assertTrue(repoResults.hasNextPage());
        
        //Select next button
        repoResults.selectNextPage().render();
        paginationPosition = repoResults.getPaginationPosition();
        Assert.assertEquals(paginationPosition,2);
        
        Assert.assertFalse(repoResults.hasNextPage());
        Assert.assertTrue(repoResults.hasPrevioudPage());
        //Select previous button
        repoResults.selectPreviousPage().render();
        Assert.assertTrue(repoResults.hasNextPage());
        Assert.assertFalse(repoResults.hasPrevioudPage());
        //Select by number
        repoResults.paginationSelect(maxPaginationPagePosition).render();
        paginationPosition = repoResults.getPaginationPosition();
        Assert.assertEquals(2, paginationPosition);
        
        Assert.assertFalse(repoResults.hasNextPage());
        Assert.assertTrue(repoResults.hasPrevioudPage());
    }
    
    /**
     * This test is validate the list of sort descriptions are same as what is
     * displayed.
     * 
     * @author sprasanna
     * @throws Exception
     */

    @Test(groups = { "Enterprise-only" }, dependsOnMethods="pagination")
    public void searchSortDescTest() throws Exception
    {
        boolean sortDescription = false;
        SearchResultsPage resultPage;
        SearchBox search = dashBoard.getSearch();
        resultPage = search.search("ipsum").render();
        Assert.assertNotNull(resultPage);
        List<String> sortDescripion = resultPage.sortListItemsDescription();
        for (String sortDescElement : sortDescripion)
        {
            switch (SortType.getSortType(sortDescElement))
            {
                case RELEVANCE:
                case NAME:
                case TITLE:
                case DESCRIPTION:
                case AUTHOR:
                case MODIFIER:
                case MODIFIED:
                case CREATOR:
                case CREATED:
                case SIZE:
                case MIMETYPE:
                case TYPE:
                    Assert.assertTrue(true, sortDescElement + "sort is present");
                    sortDescription = true;
                    break;
            }
        }
        Assert.assertTrue(sortDescription, "sort description is not matching");
    }

    /**
     * This test is to validate whether when we pass the sort type we get
     * correct results back.
     * 
     * @author sprasanna
     * @throws Exception
     */

    @Test(groups = { "Enterprise-only" }, dependsOnMethods="searchSortDescTest")
    public void searchSortTest() throws Exception
    {
        SearchResultsPage resultPage;
        SearchBox search = dashBoard.getSearch();
        resultPage = search.search("ipsum").render();
        Assert.assertNotNull(resultPage);
        resultPage = (SearchResultsPage) resultPage.sortPage(SortType.NAME).render();
        List<SearchResultItem> searchResultsItem = resultPage.getResults();
        if (searchResultsItem.isEmpty() || searchResultsItem == null)
        {
            Assert.fail("serach results is empty");
        }
        for (SearchResultItem results : searchResultsItem)
        {
            if (results.getTitle().contains("Meeting"))
            {
                Assert.assertTrue(true, "Test passed");
            }
        }
    }

    /**
     * This test is validate the negative condition for such element exception
     * in sort filter list
     * 
     * @author sprasanna
     * @throws UnsupportedOperationException
     */
    @Test(expectedExceptions = IllegalArgumentException.class, groups = "Enterprise-only", dependsOnMethods="searchSortTest")
    public void searchSortExceptionTest() throws Exception
    {
        SearchResultsPage resultPage = new SiteResultsPage(drone);
        resultPage.sortPage(null);
    }
    public void selectInvalidRange0SearchResult() throws Exception
    {
        SearchResultsPage r = new AllSitesResultsPage(drone);
        r.selectItem(-1);
    }
}
