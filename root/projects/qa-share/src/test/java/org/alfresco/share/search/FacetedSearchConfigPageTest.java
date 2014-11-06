package org.alfresco.share.search;

import static org.alfresco.po.share.site.document.ContentType.PLAINTEXT;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.search.CreateNewFilterPopUpPage;
import org.alfresco.po.share.search.FacetedSearchConfigFilter;
import org.alfresco.po.share.search.FacetedSearchConfigPage;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The Class FacetedSearchConfigPageTest.
 * This tests the Faceted Search Config page behaves as one would expect.
 *
 * @author Richard Smith
 */
@Listeners(FailedTestListener.class)
public class FacetedSearchConfigPageTest extends AbstractUtils
{

    /** The logger. */
    private static Log logger = LogFactory.getLog(FacetedSearchConfigPageTest.class);

    /** Constants */
    private DashBoardPage dashBoardPage;
    private FacetedSearchPage facetedSearchPage;
    private FacetedSearchConfigPage facetedSearchConfigPage;
    private SiteDashboardPage siteDashboardPage;

    /*
     * (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#setup()
     */
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        trace("Starting setup");

        super.setup();

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        String Admin = "ALFRESCO_ADMINISTRATORS";

        String testUser = "User" + System.currentTimeMillis();

        // Create User1 and add to SiteAdmin group
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, Admin, testUser);

        // Login as user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Navigate to the faceted search page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        trace("Setup complete");
    }

    /**
     * renderFacetedSearchConfigTest.
     *
     * @throws Exception
     */
    @Test
    public void ALF_3267() throws Exception
    {
        trace("Starting renderFacetedSearchConfigTest");

        // Page should have a title
        Assert.assertTrue(StringUtils.isNotEmpty(facetedSearchConfigPage.getPageTitle()), "The faceted search config page should have a title");

        // Page should have some filters on it
        Assert.assertTrue(facetedSearchConfigPage.getFilters().size() > 0, "The faceted search config page should have some facets");

        trace("renderFacetedSearchConfigTest complete");
    }

    /**
     * disableAndEnableFacetTest
     *
     * @throws Exception
     */
    @Test
    public void ALF_3268() throws Exception
    {
        trace("Starting disableAndEnableFacetTest");

        // Navigate to search
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to search config
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Set all filters to enabled
        for (FacetedSearchConfigFilter filter : facetedSearchConfigPage.getFilters())
        {
            filter.editFilterShow("yes");
        }

        // Navigate to search
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for the letter 'a'
        facetedSearchPage = (facetedSearchPage.getSearchForm().search("a")).render();

        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for the letter 'a' there should be some facet groups");
        int groupsFound = facetedSearchPage.getFacetGroups().size();

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Select the first filter and set it to not be shown
        facetedSearchConfigPage.getFilters().get(0).editFilterShow("no");

        // Navigate back to the search and search for a again
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();
        facetedSearchPage = (facetedSearchPage.getSearchForm().search("a")).render();

        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for the letter 'a' there should be some facet groups");
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() < groupsFound, "There should be fewer facet groups now shown");

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Select the first filter and set it to be shown
        facetedSearchConfigPage.getFilters().get(0).editFilterShow("yes");

        // Navigate back to the search and search for a again
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();
        facetedSearchPage = (facetedSearchPage.getSearchForm().search("a")).render();

        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for the letter 'a' there should be some facet groups");
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() == groupsFound, "There should be all facet groups now shown");

        trace("disableAndEnableFacetTest complete");
    }

    /**
     * Create new filter with validating 'Increment Minimum filter length and verify the results are displayed
     * Delete created filter and verify results are updated correctly
     * 
     * @throws Exception
     */
    @Test
    public void Createfilter_1() throws Exception
    {
        String filterId = "NewFilter" + System.currentTimeMillis();
        String displayName = "displayName" + System.currentTimeMillis();
        String property = "cm:name (Name)";
        String siteName = "site" + System.currentTimeMillis();
        String fileInfo1 = "apple" + System.currentTimeMillis() + "1";
        String fileInfo2 = "apple" + System.currentTimeMillis() + "2";

        trace("Starting Createfilter_1");

        // Navigate to User DashBoard
        ShareUser.openUserDashboard(drone);

        // Create site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Create content1
        ContentDetails contentDetails1 = new ContentDetails(fileInfo1, fileInfo1, fileInfo1, fileInfo1);
        ShareUser.createContent(drone, contentDetails1, PLAINTEXT);

        // Create content2
        ContentDetails contentDetails2 = new ContentDetails(fileInfo2, fileInfo2, fileInfo2, fileInfo2);
        ShareUser.createContent(drone, contentDetails2, PLAINTEXT);

        // Navigate to my DashBoard
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to search config page
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Navigate to faceted search page
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for text
        doretrySearch("apple");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for the text there should be some facet groups");
        int groupsFound = facetedSearchPage.getFacetGroups().size();

        // Navigate to search config page
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Click on Create new Filter page
        CreateNewFilterPopUpPage createNewFilterPopUpPage = facetedSearchConfigPage.clickAddNewFilter().render();
        createNewFilterPopUpPage.sendFilterID(filterId);
        Assert.assertTrue(createNewFilterPopUpPage.getFilterID().contains(filterId));
        createNewFilterPopUpPage.selectFilterProperty(property);
        Assert.assertTrue(createNewFilterPopUpPage.getSelectedProperty().contains(property));
        createNewFilterPopUpPage.selectSortBy("(high to low)");
        Assert.assertTrue(createNewFilterPopUpPage.isSortByDisplayed("(high to low)"));
        createNewFilterPopUpPage.incrementMinimumFilterLength(17);
        Assert.assertEquals(createNewFilterPopUpPage.getMinFilterLength(), 18);
        createNewFilterPopUpPage.sendFilterName(displayName).render();
        Assert.assertTrue(createNewFilterPopUpPage.getFilterName().contains(displayName));
        createNewFilterPopUpPage.selectSaveOrCancel("Save");
        Assert.assertTrue(facetedSearchConfigPage.isTitlePresent("Search Manager"));

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Verify created filterId is displayed in the Search config page
        Assert.assertEquals(facetedSearchConfigPage.getFilter(filterId).getFilterId_text(), filterId);
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Do search for text
        doretrySearch("apple");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is greater than groupsFound
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > groupsFound, "There should be more facet groups now shown");

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Delete filterId
        facetedSearchConfigPage.getFilter(filterId).deleteFilter(true);

        // Navigate back to the search and search for a again
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Do search for text
        doretrySearch("apple");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is equal to groupsFound
        Assert.assertEquals(facetedSearchPage.getFacetGroups().size(), groupsFound, "There should be all facet groups now shown");

        trace("Createfilter_1 complete");
    }

    /**
     * Create new filter with validating 'Increment Minimum filter length
     * verify filter not displayed when requirement not satisfied
     * Delete created filter and verify results are updated correctly
     * 
     * @throws Exception
     */
    @Test
    public void Createfilter_2() throws Exception
    {
        String filterId = "NewFilter" + System.currentTimeMillis();
        String displayName = "displayName" + System.currentTimeMillis();
        String property = "cm:name (Name)";
        String siteName = "site" + System.currentTimeMillis();
        String fileInfo1 = "apple" + System.currentTimeMillis() + "1";
        String fileInfo2 = "apple" + System.currentTimeMillis() + "2";

        trace("Starting Createfilter_2");

        // Navigate to User DashBoard
        ShareUser.openUserDashboard(drone);

        // Create site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Create content
        ContentDetails contentDetails1 = new ContentDetails(fileInfo1, fileInfo1, fileInfo1, fileInfo1);
        ShareUser.createContent(drone, contentDetails1, PLAINTEXT);

        // Create content
        ContentDetails contentDetails2 = new ContentDetails(fileInfo2, fileInfo2, fileInfo2, fileInfo2);
        ShareUser.createContent(drone, contentDetails2, PLAINTEXT);

        // Navigate to search
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to search config
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Navigate to search
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for text
        doretrySearch("apple");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");
        int groupsFound = facetedSearchPage.getFacetGroups().size();

        // Navigate to search config
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Click on Create new Filter page
        CreateNewFilterPopUpPage createNewFilterPopUpPage = facetedSearchConfigPage.clickAddNewFilter().render();
        createNewFilterPopUpPage.sendFilterID(filterId);
        Assert.assertTrue(createNewFilterPopUpPage.getFilterID().contains(filterId));
        createNewFilterPopUpPage.selectFilterProperty(property);
        Assert.assertTrue(createNewFilterPopUpPage.getSelectedProperty().contains(property));
        createNewFilterPopUpPage.selectSortBy("(high to low)");
        Assert.assertTrue(createNewFilterPopUpPage.isSortByDisplayed("(high to low)"));
        createNewFilterPopUpPage.incrementMinimumFilterLength(19);
        Assert.assertEquals(createNewFilterPopUpPage.getMinFilterLength(), 20);
        createNewFilterPopUpPage.sendFilterName(displayName).render();
        Assert.assertTrue(createNewFilterPopUpPage.getFilterName().contains(displayName));
        createNewFilterPopUpPage.selectSaveOrCancel("Save");
        Assert.assertTrue(facetedSearchConfigPage.isTitlePresent("Search Manager"));

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Verify created filterId is displayed in the Search config page
        Assert.assertEquals(facetedSearchConfigPage.getFilter(filterId).getFilterId_text(), filterId);
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for text
        doretrySearch("apple");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is greater than groupsFound
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() == groupsFound, "There should be fewer facet groups now shown");

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Delete filterId
        facetedSearchConfigPage.getFilter(filterId).deleteFilter(true);

        // Navigate back to the search and search for a again
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for text
        doretrySearch("apple");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is equal to groupsFound
        Assert.assertEquals(facetedSearchPage.getFacetGroups().size(), groupsFound, "There should be all facet groups now shown");

        trace("Createfilter_2 complete");
    }

    /**
     * Create new filter with validating 'Increment Minimum Required Results'.
     * Verify the results and filter are displayed when the requirement is satisfied
     * Delete created filter and verify results are updated correctly
     * 
     * @throws Exception
     */
    @Test
    public void Createfilter_3() throws Exception
    {
        String filterId = "NewFilter" + System.currentTimeMillis();
        String displayName = "displayName" + System.currentTimeMillis();
        String property = "cm:name (Name)";
        String siteName = "site" + System.currentTimeMillis();
        String fileInfo1 = getTestName() + System.currentTimeMillis();

        trace("Starting Createfilter_1");

        // Navigate to User DashBoard
        ShareUser.openUserDashboard(drone);

        // Create site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Create content
        ContentDetails contentDetails1 = new ContentDetails(fileInfo1, fileInfo1, fileInfo1, fileInfo1);
        ShareUser.createContent(drone, contentDetails1, PLAINTEXT);

        // Navigate to search
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to search config
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Navigate to search
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for fileInfo1
        doretrySearch(fileInfo1);

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");
        int groupsFound = facetedSearchPage.getFacetGroups().size();

        // Navigate to search config
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Click on Create new Filter page
        CreateNewFilterPopUpPage createNewFilterPopUpPage = facetedSearchConfigPage.clickAddNewFilter().render();
        createNewFilterPopUpPage.sendFilterID(filterId);
        Assert.assertTrue(createNewFilterPopUpPage.getFilterID().contains(filterId));
        createNewFilterPopUpPage.selectFilterProperty(property);
        Assert.assertTrue(createNewFilterPopUpPage.getSelectedProperty().contains(property));
        createNewFilterPopUpPage.selectSortBy("(high to low)");
        Assert.assertTrue(createNewFilterPopUpPage.isSortByDisplayed("(high to low)"));
        createNewFilterPopUpPage.sendFilterName(displayName).render();
        Assert.assertTrue(createNewFilterPopUpPage.getFilterName().contains(displayName));
        createNewFilterPopUpPage.selectSaveOrCancel("Save");
        Assert.assertTrue(facetedSearchConfigPage.isTitlePresent("Search Manager"));

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Verify created filterId is displayed in the Search config page
        Assert.assertEquals(facetedSearchConfigPage.getFilter(filterId).getFilterId_text(), filterId);
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for text
        doretrySearch(fileInfo1);

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is greater than groupsFound
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > groupsFound, "There should be more facet groups now shown");

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Delete filterId
        facetedSearchConfigPage.getFilter(filterId).deleteFilter(true);

        // Navigate back to the search and search for a again
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for text
        doretrySearch(fileInfo1);

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is equal to groupsFound
        Assert.assertEquals(facetedSearchPage.getFacetGroups().size(), groupsFound, "There should be all facet groups now shown");

        trace("Createfilter_3 complete");
    }

    /**
     * Create new filter with validating 'Increment Minimum Required Results'.
     * Verify the results and filter are not displayed when the requirement is not satisfied
     * Delete created filter and verify results are updated correctly
     * 
     * @throws Exception
     */
    @Test
    public void Createfilter_4() throws Exception
    {
        String filterId = "NewFilter" + System.currentTimeMillis();
        String displayName = "displayName" + System.currentTimeMillis();
        String property = "cm:name (Name)";
        String siteName = "site" + System.currentTimeMillis();
        String fileInfo1 = getTestName() + System.currentTimeMillis();

        trace("Starting Createfilter_1");

        // Navigate to User DashBoard
        ShareUser.openUserDashboard(drone);

        // Create site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Create content
        ContentDetails contentDetails1 = new ContentDetails(fileInfo1, fileInfo1, fileInfo1, fileInfo1);
        ShareUser.createContent(drone, contentDetails1, PLAINTEXT);

        // Navigate to search
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to search config
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Navigate to search
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for fileInfo1
        doretrySearch(fileInfo1);

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");
        int groupsFound = facetedSearchPage.getFacetGroups().size();

        // Navigate to search config
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Click on Create new Filter page
        CreateNewFilterPopUpPage createNewFilterPopUpPage = facetedSearchConfigPage.clickAddNewFilter().render();
        createNewFilterPopUpPage.sendFilterID(filterId);
        Assert.assertTrue(createNewFilterPopUpPage.getFilterID().contains(filterId));
        createNewFilterPopUpPage.selectFilterProperty(property);
        Assert.assertTrue(createNewFilterPopUpPage.getSelectedProperty().contains(property));
        createNewFilterPopUpPage.selectSortBy("(high to low)");
        Assert.assertTrue(createNewFilterPopUpPage.isSortByDisplayed("(high to low)"));
        createNewFilterPopUpPage.incrementMinimumRequiredResults(1);
        Assert.assertEquals(createNewFilterPopUpPage.getMinReqResults(), 2, "Min Required Results is not displayed correctly");
        createNewFilterPopUpPage.sendFilterName(displayName).render();
        Assert.assertTrue(createNewFilterPopUpPage.getFilterName().contains(displayName));
        createNewFilterPopUpPage.selectSaveOrCancel("Save");
        Assert.assertTrue(facetedSearchConfigPage.isTitlePresent("Search Manager"));

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Verify created filterId is displayed in the Search config page
        Assert.assertEquals(facetedSearchConfigPage.getFilter(filterId).getFilterId_text(), filterId);
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for fileInfo1
        doretrySearch(fileInfo1);

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is equal to groupsFound
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() == groupsFound, "There should be all facet groups now shown");

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Delete filterId
        facetedSearchConfigPage.getFilter(filterId).deleteFilter(true);

        // Navigate back to the search and search for a again
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for fileInfo1
        doretrySearch(fileInfo1);

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is equal to groupsFound
        Assert.assertEquals(facetedSearchPage.getFacetGroups().size(), groupsFound, "There should be all facet groups now shown");

        trace("Createfilter_4 complete");
    }

    /**
     * Create new filter
     * /**
     * Create new filter with validating 'Increment Max Number of Filters' and Filter availability as selected sites.
     * Verify the results and filter are displayed in the respective site scope when the requirement is satisfied.
     * Also validating all other scopes Repository, All Sites
     * Delete Filter and verify the results are updated correctly after delete
     * 
     * @throws Exception
     */
    @Test
    public void Createfilter_5() throws Exception
    {
        String filterId = "NewFilter" + System.currentTimeMillis();
        String displayName = "displayName" + System.currentTimeMillis();
        String property = "cm:content.size (Size)";
        String siteName = "site" + System.currentTimeMillis();
        String fileInfo = "apple" + System.currentTimeMillis();

        // Navigate to User DashBoard
        ShareUser.openUserDashboard(drone);

        // Create site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Create content
        ContentDetails contentDetails = new ContentDetails(fileInfo, fileInfo, fileInfo, fileInfo);
        ShareUser.createContent(drone, contentDetails, PLAINTEXT);

        // Navigate to search
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to search config
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Navigate to search
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for fileInfo
        doretrySearch(fileInfo);

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");
        int groupsFound = facetedSearchPage.getFacetGroups().size();

        // Navigate to search config
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        // Click on Create new Filter page
        CreateNewFilterPopUpPage createNewFilterPopUpPage = facetedSearchConfigPage.clickAddNewFilter().render();
        createNewFilterPopUpPage.sendFilterID(filterId);
        Assert.assertTrue(createNewFilterPopUpPage.getFilterID().contains(filterId));
        createNewFilterPopUpPage.selectFilterProperty(property);
        Assert.assertTrue(createNewFilterPopUpPage.getSelectedProperty().contains(property));
        createNewFilterPopUpPage.selectSortBy("(high to low)");
        Assert.assertTrue(createNewFilterPopUpPage.isSortByDisplayed("(high to low)"));
        createNewFilterPopUpPage.sendFilterName(displayName).render();
        Assert.assertTrue(createNewFilterPopUpPage.getFilterName().contains(displayName));
        createNewFilterPopUpPage.incrementMaxNumberOfFilters(2);
        Assert.assertEquals(createNewFilterPopUpPage.getMaxNoOfFilters(), 12);
        createNewFilterPopUpPage.selectFilterAvailability("Selected sites");
        Assert.assertTrue(createNewFilterPopUpPage.isFilterAvailabiltyDisplayed("Selected sites"));
        createNewFilterPopUpPage.clickAddNewEntry().render();
        createNewFilterPopUpPage.selectSiteNameAndSave(siteName).render();
        Assert.assertTrue(createNewFilterPopUpPage.isSavedSiteDisplayed(siteName));
        createNewFilterPopUpPage.selectSaveOrCancel("Save");
        Assert.assertTrue(facetedSearchConfigPage.isTitlePresent("Search Manager"));

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Verify created filterId is displayed in the Search config page
        Assert.assertEquals(facetedSearchConfigPage.getFilter(filterId).getFilterId_text(), filterId);
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for text
        doretrySearch("apple");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is equal to groupsFound
        Assert.assertEquals(facetedSearchPage.getFacetGroups().size(), groupsFound, "There should be all facet groups now shown");

        // Select siteName
        facetedSearchPage.getScopeMenu().scopeByLabel("All Sites");

        // Check scope menu options
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel("All Sites"), "The scope menu should have a siteName All Sites option");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is equal to groupsFound
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() == groupsFound, "There should be all facet groups now shown");

        // Navigate to the test site
        dashBoardPage = ShareUser.openUserDashboard(drone).render();
        siteDashboardPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));

        // Do a header search for the letter fileInfo
        facetedSearchPage = (FacetedSearchPage) siteDashboardPage.getSearch().search("apple").render();

        // Select siteName
        facetedSearchPage.getScopeMenu().scopeByLabel(siteName);

        // Check scope menu options
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel(siteName), "The scope menu should have a siteName All Sites option");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Navigate to search config
        facetedSearchConfigPage = facetedSearchPage.getNav().getFacetedSearchConfigPage().render();

        // Delete filter
        facetedSearchConfigPage.getFilter(filterId).deleteFilter(true);

        // Navigate back to the search and search for a again
        facetedSearchPage = facetedSearchConfigPage.getNav().getFacetedSearchPage().render();

        // Search for text
        doretrySearch("apple");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is equal to groupsFound
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() == groupsFound, "There should be all facet groups now shown");

        // Select siteName
        facetedSearchPage.getScopeMenu().scopeByLabel("All Sites");

        // Check scope menu options
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel("All Sites"), "The scope menu should have a siteName All Sites option");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is equal to groupsFound
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() == groupsFound, "There should be all facet groups now shown");

        // Navigate to the test site
        dashBoardPage = ShareUser.openUserDashboard(drone).render();
        siteDashboardPage = SiteUtil.openSiteURL(drone, getSiteShortname(siteName));

        // Do a header search for text
        facetedSearchPage = (FacetedSearchPage) siteDashboardPage.getSearch().search("apple").render();

        // Select siteName
        facetedSearchPage.getScopeMenu().scopeByLabel(siteName);

        // Check scope menu options
        Assert.assertTrue(facetedSearchPage.getScopeMenu().hasScopeLabel(siteName), "The scope menu should have a siteName" + siteName + "option");

        // Verify facet groups size is greater than zero
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() > 0, "After searching for text there should be some facet groups");

        // Verify facet groups size is equal to groupsFound
        Assert.assertTrue(facetedSearchPage.getFacetGroups().size() == groupsFound, "There should be all facet groups now shown");

        trace("Createfilter_5 complete");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#tearDown()
     */
    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        trace("Starting tearDown");

        // Logout
        ShareUser.logout(drone);

        super.tearDown();

        trace("TearDown complete");
    }

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

    /**
     * Trace.
     *
     * @param msg the msg
     */
    private void trace(String msg)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace(msg);
        }
    }
}