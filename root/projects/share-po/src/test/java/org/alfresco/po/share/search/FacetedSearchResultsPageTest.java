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
public class FacetedSearchResultsPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    
    @BeforeClass(groups={"alfresco-one"})
    public void prepare() throws Exception
    {
        dashBoard = loginAs(username, password);
    }
    
    @BeforeMethod(groups={"alfresco-one"})
    public void reset()
    {
        SharePage page = drone.getCurrentPage().render();
        page.getNav().selectMyDashBoard();
    }
    
    @Test(groups="alfresco-one")
    public void searchEmptyResult()
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("y@z").render();
        Assert.assertNotNull(resultPage);
        Assert.assertFalse(resultPage.getResults().size()>0);
    }   
        
    @Test(groups = {"Enterprise-only"},dependsOnMethods="searchEmptyResult")
    public void selectNthSearchResult() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("ipsum").render();
        Assert.assertNotNull(resultPage);

        DocumentDetailsPage itemPage = resultPage.getResults().get(0).clickLink().render();
        Assert.assertTrue(itemPage.getTitle().contains("Document Details"));
    }
    
    @Test(groups = {"Enterprise-only"}, dependsOnMethods="selectNthSearchResult")
    public void selectSearchResultOfTypeFolder() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("Images").render();
        Assert.assertNotNull(resultPage);
        
        DocumentLibraryPage itemPage = resultPage.getResultByName("Images").clickLink().render();
        Assert.assertTrue(itemPage.getTitle().contains("Library"));
    }
    
    @Test(groups = {"Enterprise-only"}, dependsOnMethods="selectSearchResultOfTypeFolder")
    public void selectSearchResultOfTypeWiki() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("Project").render();
        Assert.assertNotNull(resultPage);
        
        WikiPage itemPage = resultPage.getResultByName("Main Page").clickLink().render();
        Assert.assertTrue(itemPage.getTitle().contains("Wiki"));
    }
    
    @Test(groups = {"Enterprise-only"}, dependsOnMethods="selectSearchResultOfTypeWiki")
    public void selectSearchResultOfTypeDataList() throws Exception
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search("Issue Log").render();
        Assert.assertNotNull(resultPage);
        
        DataListPage itemPage = resultPage.getResultByName("Issue Log").clickLink().render();
        Assert.assertTrue(itemPage.getTitle().contains("Project Lists"));
    }
    
    @Test(groups = {"Enterprise-only"}, dependsOnMethods="selectSearchResultOfTypeWiki")
    public void pagination() throws Exception
    {
        int expectedResultLength = 10;
        
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search("a").render();        

        // Check the results
        int resultsCount = facetedSearchPage.getResults().size();
        Assert.assertTrue(resultsCount > 0, "After searching for the letter 'a' there should be some search results");

        // If the number of results equals the expectedResultCount - pagination is probably available
        if(resultsCount > expectedResultLength)
        {
            // Force a pagination
            // We do a short scroll first to get past the exclusion of the first scroll event (required for some browsers)
            facetedSearchPage.scrollSome(50);
            facetedSearchPage.scrollToPageBottom();          
              
            // Check the results
            int paginatedResultsCount = facetedSearchPage.getResults().size();
            Assert.assertTrue(paginatedResultsCount > 0, "After searching for the letter 'a' and paginating there should be some search results");
            Assert.assertTrue(paginatedResultsCount >= resultsCount, "After searching for the letter 'a' and paginating there should be the same or more search results");
        }  
    }
    
    /**
     * This test is validate the list of sort descriptions are same as what is
     * displayed.
     * 
     * @author Charu
     * @throws Exception
     */

    @Test(groups = { "Enterprise-only" }, dependsOnMethods="pagination")
    public void searchSortDescTest() throws Exception
    {
        String selectedSort;
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search("ipsum").render();
        Assert.assertNotNull(facetedSearchPage);
        facetedSearchPage.getSort().sortByLabel("Name");
        selectedSort = facetedSearchPage.getSort().getCurrentSelection();       
        Assert.assertEquals(selectedSort,"Name", "sort description is not matching");        
        facetedSearchPage.getSort().toggleSortOrder().render();
        Assert.assertNotNull(facetedSearchPage);
    }

    /**
     * This test is to validate whether when we pass the sort type we get
     * correct results back.
     * 
     * @author Charu
     * @throws Exception
     */

    @Test(groups = { "Enterprise-only" }, dependsOnMethods="searchSortDescTest")
    public void searchSortTest() throws Exception
    {
        FacetedSearchPage resultPage;
        SearchBox search = dashBoard.getSearch();
        resultPage = search.search("ipsum").render();
        Assert.assertNotNull(resultPage);
        resultPage = resultPage.getSort().sortByLabel("NAME").render();
        List<FacetedSearchResult> facetedSearchResult = resultPage.getResults();
        if (facetedSearchResult.isEmpty() || facetedSearchResult == null)
        {
            Assert.fail("serach results is empty");
        }
        for (FacetedSearchResult results : facetedSearchResult)
        {
            if (results.getTitle().contains("Meeting"))
            {
                Assert.assertTrue(true, "Test passed");
            }
        }
    }

    /**
     * This test is validate the sort with invalid data and to verify the sort order is set to default.
     * To toggle the sort order and verify there are some search results 
     * in sort filter list     * 
     * @author Charu
     * 
     */
    @Test(groups = { "Enterprise-only" }, dependsOnMethods="searchSortTest")
    public void searchSortExceptionTest() throws Exception
    {
        String selectedSort;
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search("ipsum").render();
        Assert.assertNotNull(facetedSearchPage);
        facetedSearchPage.getSort().sortByLabel("N");
        selectedSort = facetedSearchPage.getSort().getCurrentSelection();       
        Assert.assertEquals(selectedSort,"Relevance", "sort description is not matching");        
        facetedSearchPage.getSort().sortByLabel("Title");
        selectedSort = facetedSearchPage.getSort().getCurrentSelection();       
        Assert.assertEquals(selectedSort,"Title", "sort description is not matching");
        facetedSearchPage.getSort().toggleSortOrder().render();
        Assert.assertNotNull(facetedSearchPage);
     }
    
    @Test(groups = { "Enterprise-only"})
    public void getResultCount()
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search("ipsum").render();
        Assert.assertEquals(facetedSearchPage.getResultCount(),6);
        facetedSearchPage = facetedSearchPage.getSearch().search("yyyxxxxz").render();
        Assert.assertEquals(facetedSearchPage.getResultCount(),0);
    }
    public void selectFacet()
    {
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage facetedSearchPage = search.search("ipsum").render();
        FacetedSearchPage filteredResults = facetedSearchPage.selectFacet("Microsoft Word").render();
        Assert.assertEquals(filteredResults.getResultCount(), 3);
    }
    
}
