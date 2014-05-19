package org.alfresco.share.search;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.search.FacetedSearchFacetGroup;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class FacetedSearchPageTest.
 *
 * This tests the Faceted Search page behaves as one would expect.
 *
 * @author Richard Smith
 */

public class FacetedSearchPageTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(FacetedSearchPageTest.class);

    private DashBoardPage dashBoardPage;
    private FacetedSearchPage facetedSearchPage;

    /**
     * Setup.
     * 
     * @throws Exception the exception
     */
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        trace("Starting setup");
        
        super.setup();
        
        // Login as admin
        dashBoardPage = ShareUser.loginAs(drone, username, "admin");

        // Navigate to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        trace("Setup complete");
    }

    @Test
    public void firstRenderTest() throws Exception
    {
        trace("Starting firstRenderTest");

        // Page should have a title
        Assert.assertTrue(StringUtils.isNotEmpty(facetedSearchPage.getPageTitle()), "The faceted search page should have a title");

        // The url should not contain a hash value
        Assert.assertNull(facetedSearchPage.getUrlHash(), "Before searching the url # should be empty");

        // The search box should be empty
        Assert.assertTrue(StringUtils.isEmpty(facetedSearchPage.getSearchForm().getSearchTerm()), "The search box on the faceted search page should be empty");

        // To begin with the page should have no results
        Assert.assertTrue(facetedSearchPage.getResults().isEmpty(), "There should be no results shown on the faceted search page when first loaded");

        // Set the search term (but no search) and re-test
        facetedSearchPage.getSearchForm().setSearchTerm("test");
        Assert.assertTrue(StringUtils.isNotEmpty(facetedSearchPage.getSearchForm().getSearchTerm()), "After setting the search box should not be empty");

        // Clear the search
        facetedSearchPage.getSearchForm().clearSearchTerm();

        trace("firstRenderTest complete");
    }

    @Test(dependsOnMethods={"firstRenderTest"})
    public void searchTest() throws Exception
    {
        trace("Starting searchTest");

        // Do a search for the letter 'a'
        facetedSearchPage.getSearchForm().search("a");

        // Reload the page
        facetedSearchPage.render();

        // There should now be some results, facet groups and facets
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' there should be some search results");
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for the letter 'a' there should be some facet groups");
        Assert.assertTrue(((FacetedSearchFacetGroup)facetedSearchPage.getFacetGroups().get(0)).getFacets().size() > 0, "After searching the first facet group should contain some facets");

        // Clear the search
        facetedSearchPage.getSearchForm().clearSearchTerm();

        trace("searchTest complete");
    }

    @Test(dependsOnMethods={"searchTest"})
    public void searchAndFacetTest() throws Exception
    {
        trace("Starting searchAndFacetTest");

        // Do a search for the letter 'e'
        facetedSearchPage.getSearchForm().search("e");
        
        // After searching the search term should be on the url
        Assert.assertTrue(StringUtils.contains(facetedSearchPage.getUrlHash(), "searchTerm=e"), "After searching for the letter 'e' the phrase 'searchTerm=e' should appear on the url");

        // Reload the page
        facetedSearchPage.render();

        // There should now be some results, facet groups and facets
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'e' there should be some search results");
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for the letter 'e' there should be some facet groups");

        // Click the first facet in the first facet group
        facetedSearchPage.getFacetGroups().get(0).getFacets().get(0).clickLink();

        // Reload the page
        facetedSearchPage.render();

        // There should still be some results, facet groups and facets
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'e' and facetting there should be some search results");
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for the letter 'e' and facetting there should be some facet groups");
        Assert.assertTrue(((FacetedSearchFacetGroup)facetedSearchPage.getFacetGroups().get(0)).getFacets().size() > 0, "After searching for the letter 'e' and facetting the first facet group should contain some facets");

        // The url should now contain a hash value of the search term and a facet
        Assert.assertTrue(StringUtils.isNotEmpty(facetedSearchPage.getUrlHash()), "After searching for the letter 'e' and facetting there should be a url # value present");
        Assert.assertTrue(StringUtils.contains(facetedSearchPage.getUrlHash(), "searchTerm=e"), "After searching for the letter 'e' and facetting the phrase 'searchTerm=e' should still appear on the url");
        Assert.assertTrue(StringUtils.contains(facetedSearchPage.getUrlHash(), "facetFilters="), "After searching for the letter 'e' and facetting the phrase 'facetFilters=' should appear on the url");

        // Clear the search
        facetedSearchPage.getSearchForm().clearSearchTerm();

        trace("searchAndFacetTest complete");
    }

    /**
     * Teardown.
     * 
     * @throws Exception the exception
     */
    @AfterClass(alwaysRun = true)
    public void teardown() throws Exception
    {
        ShareUser.logout(drone);
    }

    /**
     * Trace.
     *
     * @param msg the msg
     */
    private void trace(String msg)
    {
        if(logger.isTraceEnabled())
        {
            logger.trace(msg);
        }
    }
}