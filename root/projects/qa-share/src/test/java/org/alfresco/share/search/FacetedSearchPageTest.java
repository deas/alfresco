package org.alfresco.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.search.FacetedSearchFacetGroup;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.OpCloudTestContext;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.share.util.SiteUtil;
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
@SuppressWarnings({"rawtypes","serial"})
public class FacetedSearchPageTest extends AbstractUtils
{

    /** The logger. */
    private static Log logger = LogFactory.getLog(FacetedSearchPageTest.class);

    /** Constants */
    private static final int expectedResultLength = 25;
    private static final List<Class> linkToClassType = new ArrayList<Class>() {{
        add(DocumentDetailsPage.class);
        add(DocumentLibraryPage.class);
    }};
    private static final String fileDir = "faceted-search-files\\";
    private static final String fileStem = "-fs-test.docx";
    private static final String obscureSearchWord = "antidisestablishmentarianism";

    private OpCloudTestContext testContext;
    private DashBoardPage dashBoardPage;
    private FacetedSearchPage facetedSearchPage;
    private SiteDashboardPage siteDashboardPage;
    private String siteName;

    /* (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#setup()
     */
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        trace("Starting setup");

        super.setup();
        this.testContext = new OpCloudTestContext(this);

        // Upload test documents
        uploadTestDocs();

        // Login as admin
        dashBoardPage = ShareUser.loginAs(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Navigate to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        trace("Setup complete");
    }

    /**
     * First render test.
     *
     * @throws Exception
     */
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

        // Set the search term (but no search submission) and re-test
        facetedSearchPage.getSearchForm().setSearchTerm("test");
        Assert.assertTrue(StringUtils.isNotEmpty(facetedSearchPage.getSearchForm().getSearchTerm()), "After setting the search box should not be empty");

        // Clear the search
        facetedSearchPage.getSearchForm().clearSearchTerm();

        trace("firstRenderTest complete");
    }

    /**
     * Search test.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods={"firstRenderTest"})
    public void searchTest() throws Exception
    {
        trace("Starting searchTest");

        // Do a search for the letter 'a'
        doSearch("a");

        // There should now be some results, facet groups and facets
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' there should be some search results");
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for the letter 'a' there should be some facet groups");
        Assert.assertTrue(((FacetedSearchFacetGroup)facetedSearchPage.getFacetGroups().get(0)).getFacets().size() > 0, "After searching the first facet group should contain some facets");

        // Clear the search
        facetedSearchPage.getSearchForm().clearSearchTerm();

        trace("searchTest complete");
    }

    /**
     * Search and facet test.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods={"searchTest"})
    public void searchAndFacetTest() throws Exception
    {
        trace("Starting searchAndFacetTest");

        // Do a search for the letter 'e'
        facetedSearchPage.getSearchForm().search("e");
        
        // After searching the search term should be on the url
        Assert.assertTrue(StringUtils.contains(facetedSearchPage.getUrlHash(), "searchTerm=e"), "After searching for the letter 'e' the phrase 'searchTerm=e' should appear on the url");

        // Reload the page objects
        facetedSearchPage.render();

        // There should now be some results, facet groups and facets
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'e' there should be some search results");
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for the letter 'e' there should be some facet groups");

        // Click the first facet in the first facet group
        facetedSearchPage.getFacetGroups().get(0).getFacets().get(0).clickLink();

        // Reload the page objects
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
     * Search and sort test.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods={"searchAndFacetTest"})
    public void searchAndSortTest() throws Exception
    {
        trace("Starting searchAndSortTest");

        // Do a search for the letter 'e'
        doSearch("e");

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'e' there should be some search results");

        // Sort by the 3rd item in the sort menu (probably Title)
        facetedSearchPage.getSort().sortByIndex(2);

        // Reload the page objects
        facetedSearchPage.render();

        // Check the results again
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'e' and sorting by Title there should be some search results");

        // Toggle the sorting of the results
        facetedSearchPage.getSort().toggleSortOrder();

        // Reload the page objects
        facetedSearchPage.render();

        // Check the results again
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'e' and toggling the sort order there should be some search results");

        // Sort by the 20th item in the sort menu (does not exist)
        facetedSearchPage.getSort().sortByIndex(20);

        // Reload the page objects
        facetedSearchPage.render();

        // Check the results again
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'e' and sorting by an item (outside of the dropdown list size) there should still be some search results");

        // Sort by 'Creator'
        facetedSearchPage.getSort().sortByLabel("Creator");

        // Reload the page objects
        facetedSearchPage.render();

        // Check the results again
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'e' and sorting by 'Creator' there should still be some search results");

        // Sort by 'Time of day' (does not exist)
        facetedSearchPage.getSort().sortByLabel("Time of day");

        // Reload the page objects
        facetedSearchPage.render();

        // Check the results again
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'e' and sorting by a non-existant option there should still be some search results");

        // Clear the search
        facetedSearchPage.getSearchForm().clearSearchTerm();

        trace("searchAndSortTest complete");
    }

    /**
     * Search and paginate test.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods={"searchAndSortTest"})
    public void searchAndPaginateTest() throws Exception
    {
        trace("Starting searchAndSortTest");

        // Do a search for the letter 'a'
        doSearch("a");

        // Check the results
        int resultsCount = facetedSearchPage.getResults().size();
        Assert.assertTrue(resultsCount > 0, "After searching for the letter 'a' there should be some search results");

        // If the number of results equals the expectedResultCount - pagination is probably available
        if(resultsCount == expectedResultLength)
        {
            // Force a pagination
            // We do a short scroll first to get past the exclusion of the first scroll event (required for some browsers)
            facetedSearchPage.scrollSome(50);
            facetedSearchPage.scrollToPageBottom();
    
            // Wait 2 seconds to allow the extra results to render
            webDriverWait(drone, 2000);
    
            // Reload the page objects
            facetedSearchPage.render();
    
            // Check the results
            int paginatedResultsCount = facetedSearchPage.getResults().size();
            Assert.assertTrue(paginatedResultsCount > 0, "After searching for the letter 'a' and paginating there should be some search results");
            Assert.assertTrue(paginatedResultsCount >= resultsCount, "After searching for the letter 'a' and paginating there should be the same or more search results");
        }

        // Clear the search
        facetedSearchPage.getSearchForm().clearSearchTerm();

        trace("searchAndSortTest complete");
    }

    /**
     * Search and link test.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods={"searchAndPaginateTest"})
    public void searchAndLinkTest() throws Exception
    {
        trace("Starting searchAndLinkTest");

        // Do a search for the letter 'a'
        doSearch("a");

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' there should be some search results");

        // Get the current url
        String url = drone.getCurrentUrl();

        // Click the first result
        facetedSearchPage.getResults().get(0).clickLink();

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // We should no longer be on the faceted search page
        Assert.assertNotEquals(url, newUrl, "After searching for the letter 'a' and clicking result 1, the url should have changed");

        // Resolve the new page - we should have linked to one of the types defined in linkToClassType list
        Assert.assertTrue(linkToClassType.contains(FactorySharePage.resolvePage(drone).getClass()), "After searching for the letter 'a' and clicking result 1 we should be on an expected page type");

        // Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        trace("searchAndLinkTest complete");
    }

    /**
     * Scope test.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods={"searchAndLinkTest"})
    public void searchAndScopeTest() throws Exception
    {
        trace("Starting searchAndScopeTest");

        // Do a search for the letter 'a'
        doSearch("a");

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' there should be some search results");

        // Check scope menu options
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel("Repository"), "The scope menu should have a 'Repository' option");
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel("All Sites"), "The scope menu should have an 'All Sites' option");
        
        // Current scope selection
        Assert.assertTrue("Repository".equalsIgnoreCase(facetedSearchPage.getScopeMenu().getCurrentSelection()), "The initial value of the scope menu should be 'Repository'");

        // Select 'All Sites'
        facetedSearchPage.getScopeMenu().scopeByLabel("All Sites");

        // Reload the page objects
        facetedSearchPage.render();

        // Re-check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' in 'All Sites' there should be some search results");

        // Navigate to the test site
        dashBoardPage = ShareUser.openUserDashboard(drone).render();
        siteDashboardPage = SiteUtil.openSiteURL(drone, getSiteShortname(this.siteName));

        // Do a header search for the letter 'a'
        facetedSearchPage = (FacetedSearchPage)siteDashboardPage.getSearch().search("a").render();

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' there should be some search results");

        // Check scope menu options - now with a siteName
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel("Repository"), "The scope menu should have a 'Repository' option");
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel("All Sites"), "The scope menu should have an 'All Sites' option");
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel(this.siteName), "The scope menu should have a '" + this.siteName + "' option");
        
        // Select siteName
        facetedSearchPage.getScopeMenu().scopeByLabel(this.siteName);

        // Reload the page objects
        facetedSearchPage.render();

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After choosing the test site and searching for the letter 'a' there should be some search results");

        trace("searchAndScopeTest complete");
    }

    /**
     * Precision search and sort test.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods={"searchAndScopeTest"})
    public void precisionSearchAndSortTest() throws Exception
    {
        trace("Starting precisionSearchAndSortTest");

        // Do a search for the obscureSearchWord
        doSearch(obscureSearchWord);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for '" + obscureSearchWord + "' there should be some search results");

        // Sort by Name
        facetedSearchPage.getSort().sortByLabel("Name");

        // Reload the page objects
        facetedSearchPage.render();
        
        // First result should begin with 'a'
        Assert.assertTrue(facetedSearchPage.getResults().get(0).getName().charAt(0) == 'a', "After searching for '" + obscureSearchWord + "' and sorting by 'Name' the first letter of the Name of result one should be 'a'");
        
        // Invert sort
        facetedSearchPage.getSort().getSortOrderButton().click();
        
        // Reload the page objects
        facetedSearchPage.render();
        
        // First result should begin with 'z'
        Assert.assertTrue(facetedSearchPage.getResults().get(0).getName().charAt(0) == 'z', "After searching for '" + obscureSearchWord + "' and sorting by 'Name' and inverting the sort, the first letter of the Name of result one should be 'z'");
        
        trace("precisionSearchAndSortTest complete");
    }

    /* (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#tearDown()
     */
//    @AfterClass(alwaysRun = true)
//    public void tearDown()
//    {
//        trace("Starting tearDown");
//
//        // Navigate to the document library page and delete all content
//        ShareUser.openSiteDashboard(drone, siteName);
//        ShareUser.openDocumentLibrary(drone);
//        ShareUser.deleteAllContentFromDocumentLibrary(drone);
//
//        super.tearDown();
//
//        trace("TearDown complete");
//    }

    /**
     * Upload test docs.
     * @throws Exception 
     */
    private void uploadTestDocs() throws Exception
    {
        String testName = "FacetedSearch" + testContext.getRunId();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] {testUser};
        
        this.siteName = getSiteName(testName);

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, this.siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload Files - there are 26 starting with the letters of the alphabet
        for (int i=0; i < 26; i++)
        {
            String[] fileInfo = { fileDir + (char)(i+97) + fileStem };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }

        // Logout
        ShareUtil.logout(drone);
    }

    /**
     * Do search.
     *
     * @param searchTerm the search term
     */
    private void doSearch(String searchTerm)
    {
        // Do a search for the searchTerm
        facetedSearchPage.getSearchForm().search(searchTerm);

        // Reload the page objects
        facetedSearchPage.render();
    }

    /**
     * Do header search.
     *
     * @param searchTerm the search term
     */
    private void doHeaderSearch(String searchTerm)
    {
        // Do a search for the searchTerm
        facetedSearchPage.getHeaderSearchForm().search(searchTerm);

        // Reload the page objects
        facetedSearchPage.render();
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