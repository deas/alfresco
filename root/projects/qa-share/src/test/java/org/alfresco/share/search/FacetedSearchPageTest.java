package org.alfresco.share.search;

import static org.alfresco.po.share.site.document.ContentType.PLAINTEXT;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.search.FacetedSearchFacetGroup;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.OpCloudTestContext;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
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
    private static final List<Class> resultLinkToClassType = new ArrayList<Class>() {{
        add(DocumentDetailsPage.class);
        add(DocumentLibraryPage.class);
    }};
    private static final Class dateLinkToClassType = MyProfilePage.class;
    private static final Class siteLinkToClassType = SiteDashboardPage.class;
    private static final String fileDir = "faceted-search-files\\";
    private static final String fileStem = "-fs-test.txt";
    private static final String obscureSearchWord = "antidisestablishmentarianism";

    private OpCloudTestContext testContext;
    private DashBoardPage dashBoardPage;
    private FacetedSearchPage facetedSearchPage;    
    private SiteDashboardPage siteDashboardPage;
    private String siteName;
    private String testUser;

    /* (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#setup()
     * 
     * Should not be cloud only.
     * 
     */
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        trace("Starting setup");

        super.setup();
        this.testContext = new OpCloudTestContext(this);

        // Compose user and site names
        String testName = "FacetedSearch" + testContext.getRunId();
        this.testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] {this.testUser};
        
        this.siteName = getSiteName(testName);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create site
        ShareUser.createSite(drone, this.siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload Files - there are 26 starting with the letters of the alphabet
        for (int i=0; i < 26; i++)
        {
        	String fileInfo =  (char)(i+97) + fileStem;
            ContentDetails contentDetails = new ContentDetails(fileInfo, fileInfo, fileInfo, fileInfo);
            ShareUser.createContent(drone, contentDetails, PLAINTEXT);
        }

        // Navigate to the faceted search page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
        
        // Logout
        ShareUser.logout(drone);

        trace("Setup complete");
    }

    /**
     * First render test.
     *
     * Should not be cloud only.
     *
     * @throws Exception
     */
    @Test(groups= "alfresco-one")
    public void ALF_3112() throws Exception
    {
        trace("Starting firstRenderTest");

        // Login as Test user
        userLogin();
        
        // Page should have a title
        Assert.assertTrue(StringUtils.isNotEmpty(facetedSearchPage.getPageTitle()), "The faceted search page should have a title");

        // The url should not contain a hash value
        Assert.assertTrue(StringUtils.isEmpty(facetedSearchPage.getUrlHash()), "Before searching the url # should be empty");

        // The search box should be empty
        Assert.assertTrue(StringUtils.isEmpty(facetedSearchPage.getSearchForm().getSearchTerm()), "The search box on the faceted search page should be empty");

        // To begin with the page should have no results
        Assert.assertTrue(facetedSearchPage.getResults().isEmpty(), "There should be no results shown on the faceted search page when first loaded");

        // Set the search term (but no search submission) and re-test
        facetedSearchPage.getSearchForm().setSearchTerm("test");
        Assert.assertTrue(StringUtils.isNotEmpty(facetedSearchPage.getSearchForm().getSearchTerm()), "After setting the search box should not be empty");

        // Clear the search
        facetedSearchPage.getSearchForm().clearSearchTerm();

        // Logout
        ShareUser.logout(drone);
        
        trace("firstRenderTest complete");
    }

    /**
     * Search test.
     *
     * Should not be cloud only.
     *
     * @throws Exception
     */
    @Test(groups= "alfresco-one")
    public void ALF_3113() throws Exception
    {
        trace("Starting searchTest");

        // Login as Test user
        userLogin();

        // Do a search for the letter 'a'
        doretrySearch("a");

        // There should now be some results, facet groups and facets
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' there should be some search results");
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for the letter 'a' there should be some facet groups");
        Assert.assertTrue(((FacetedSearchFacetGroup)facetedSearchPage.getFacetGroups().get(0)).getFacets().size() > 0, "After searching the first facet group should contain some facets");

        // Clear the search
        facetedSearchPage.getSearchForm().clearSearchTerm();

        // Logout
        ShareUser.logout(drone);

        trace("searchTest complete");
    }

    /**
     * Search and facet test.
     * 
     * Should not be cloud only.
     *
     * @throws Exception
     */
    @Test(groups= "alfresco-one")
    public void ALF_3114() throws Exception
    {
        trace("Starting searchAndFacetTest");

        // Login as test user
        userLogin();

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

        // Logout
        ShareUser.logout(drone);

        trace("searchAndFacetTest complete");
    }

    /**
     * Search and sort test.
     * 
     * Should not be cloud only.
     *
     * @throws Exception
     */
    @Test(groups= "alfresco-one")
    public void ALF_3115() throws Exception
    {
        trace("Starting searchAndSortTest");

        // Login as Test user
        userLogin();
        
        // Do a search for the letter 'e'
        doretrySearch("e");

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

        // Logout
        ShareUser.logout(drone);

        trace("searchAndSortTest complete");
    }

    /**
     * Search and paginate test.
     * 
     * Should not be cloud only.
     *
     * @throws Exception
     */
    @Test(groups = "alfresco-one")
    public void ALF_3121() throws Exception
    {
        trace("Starting searchAndSortTest");

        // Login as Test user
        userLogin();

        // Do a search for the letter 'a'
        doretrySearch("a");

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

        // Logout
        ShareUser.logout(drone);

        trace("searchAndSortTest complete");
    }

    /**
     * Search and link test.
     *
     * Should not be cloud only.
     *
     * @throws Exception
     */
    @Test(groups = "alfresco-one")
    public void ALF_3122() throws Exception
    {
        trace("Starting searchAndLinkTest");

        // Login as test user
        userLogin();

        // Do a search for the letter 'a'
        doretrySearch("a");

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
        Assert.assertTrue(resultLinkToClassType.contains(FactorySharePage.resolvePage(drone).getClass()), "After searching for the letter 'a' and clicking result 1 we should be on an expected page type");

        // Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for the letter 'a'
        doretrySearch("a");

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' there should be some search results");

        // Click the first result dateLink
        facetedSearchPage.getResults().get(0).clickDateLink();

        // Get the url again
        newUrl = drone.getCurrentUrl();

        // We should no longer be on the faceted search page
        Assert.assertNotEquals(url, newUrl, "After searching for the letter 'a' and clicking the date link of result 1, the url should have changed");

        // Resolve the new page - we should have linked to one of the types defined in linkToClassType list
        Assert.assertTrue(dateLinkToClassType.equals(FactorySharePage.resolvePage(drone).getClass()), "After searching for the letter 'a' and clicking result 1 we should be on a user profile page");

        // Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for the letter 'a'
        doretrySearch("a");

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' there should be some search results");

        // Click the first result siteLink
        facetedSearchPage.getResults().get(0).clickSiteLink();

        // Get the url again
        newUrl = drone.getCurrentUrl();

        // We should no longer be on the faceted search page
        Assert.assertNotEquals(url, newUrl, "After searching for the letter 'a' and clicking the site link of result 1, the url should have changed");

        // Resolve the new page - we should have linked to one of the types defined in linkToClassType list
        Assert.assertTrue(siteLinkToClassType.equals(FactorySharePage.resolvePage(drone).getClass()), "After searching for the letter 'a' and clicking result 1 we should be on a site dashboard page");

        // Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Logout
        ShareUser.logout(drone);

        trace("searchAndLinkTest complete");
    }

    /**
     * Search and scope test (enterprise).
     *
     * Really is enterprise only.
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise-only")
    public void ALF_3125() throws Exception
    {
        trace("Starting searchAndScopeTest");

        // Login as test user
        userLogin();

        // Do a search for the letter 'a'
        doretrySearch("a");

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

        // Logout
        ShareUser.logout(drone);

        trace("searchAndScopeTest complete");
    }

    /**
     * Search and scope test (cloud).
     *
     * Really is cloud only.
     *
     * @throws Exception
     */
    @Test(groups = "CloudOnly")
    public void ALF_3124() throws Exception
    {
        trace("Starting searchAndScopeTest");

        // Login as test user
        userLogin();

        // Do a search for the letter 'a'
        doretrySearch("a");

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' there should be some search results");

        // Check scope menu options
        Assert.assertFalse(facetedSearchPage.getScopeMenu().hasScopeLabel("Repository"), "The scope menu should not have a 'Repository' option");
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel("All Sites"), "The scope menu should have an 'All Sites' option");
        
        // Current scope selection
        Assert.assertTrue("All Sites".equalsIgnoreCase(facetedSearchPage.getScopeMenu().getCurrentSelection()), "The initial value of the scope menu should be 'All Sites'");

        // Navigate to the test site
        dashBoardPage = ShareUser.openUserDashboard(drone).render();
        siteDashboardPage = SiteUtil.openSiteURL(drone, getSiteShortname(this.siteName));

        // Do a header search for the letter 'a'
        facetedSearchPage = (FacetedSearchPage)siteDashboardPage.getSearch().search("a").render();

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for the letter 'a' there should be some search results");

        // Check scope menu options - now with a siteName
        Assert.assertFalse(facetedSearchPage.getScopeMenu().hasScopeLabel("Repository"), "The scope menu should have a 'Repository' option");
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel("All Sites"), "The scope menu should have an 'All Sites' option");
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel(this.siteName), "The scope menu should have a '" + this.siteName + "' option");
        
        // Select siteName
        facetedSearchPage.getScopeMenu().scopeByLabel(this.siteName);

        // Reload the page objects
        facetedSearchPage.render();

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After choosing the test site and searching for the letter 'a' there should be some search results");

        // Logout
        ShareUser.logout(drone);

        trace("searchAndScopeTest complete");
    }

    /**
     * Precision search and sort test.
     *
     * Should not be cloud only.
     *
     * @throws Exception
     */
    @Test(groups = "Alfresco-One")
    public void ALF_3123() throws Exception
    {
        trace("Starting precisionSearchAndSortTest");

        // Login as test user
        userLogin();
        
        // Do a search for the obscureSearchWord
        doretrySearch("fs-test.txt");

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

        // Logout
        ShareUser.logout(drone);

        trace("precisionSearchAndSortTest complete");
    }
    
    //This test is to select the view option and verify the results are displayed as per the selected view option 
    /**
    * selectViewOptionAndVerifyResults
    *
    * Should not be cloud only.
    *
    * @throws Exception
    */

    
    @Test(groups = "Alfresco-One")
    public void AONE_16063() throws Exception
    {
        trace("Starting selectViewOptionAndVerifyResults");

        // Login as test user
        userLogin();
        
        // Do a search for the obscureSearchWord
        doretrySearch("test");

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for '" + obscureSearchWord + "' there should be some search results");

        // Verify the results are in Simple View
        Assert.assertTrue(facetedSearchPage.getView().isSimpleViewResultsDisplayed(),"Results not dispalyed in SimpleView");
        
        //Select the Gallery View option
        facetedSearchPage.getView().selectViewByLabel("Gallery View");

        // Reload the page objects
        facetedSearchPage.render();
        
        //Verify the results are displayed as Gallery View
        Assert.assertTrue(facetedSearchPage.getView().isGalleryViewResultsDisplayed(), "gallery view not displayed");
        
        //Select the Gallery View optionS
        facetedSearchPage.getView().selectViewByLabel("Simple View");
       
        // Logout
        ShareUser.logout(drone);

        trace("selectViewOptionAndVerifyResults complete");
    }


    /* (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#tearDown()
     * 
     * Should not be cloud only.
     * 
     */
    @AfterClass(alwaysRun = true, groups = "alfresco-one")
    public void tearDown()
    {
        trace("Starting tearDown");

        // Login as test user        
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Navigate to the document library page and delete all content
        SiteUtil.openSiteURL(drone, getSiteShortname(this.siteName));
        ShareUser.openDocumentLibrary(drone);
        ShareUser.deleteAllContentFromDocumentLibrary(drone);

        // Logout
        ShareUser.logout(drone);

        super.tearDown();

        trace("TearDown complete");
    }

    /**
     * Do retry search.
     *
     * @param searchTerm the search term
     */
        
    private void doretrySearch(String searchTerm)
	{
		facetedSearchPage.getSearchForm().search(searchTerm);
		facetedSearchPage.render();
		if (!(facetedSearchPage.getResults().size() > 0)) 
		{
			webDriverWait(drone, refreshDuration);
			facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
			facetedSearchPage.getSearchForm().search(searchTerm);
			facetedSearchPage.render();
		}
	}       

//    /**
//     * Do header search.
//     *
//     * @param searchTerm the search term
//     */
//    private void doHeaderSearch(String searchTerm)
//    {
//        // Do a search for the searchTerm
//        facetedSearchPage.getHeaderSearchForm().search(searchTerm);
//
//        // Reload the page objects
//        facetedSearchPage.render();
//    }

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

    /**
     * Login as user.
     */
    private void userLogin()
    {
        // Login as test user        
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Navigate to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
    }
}