package org.alfresco.po.share.admin;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.po.share.util.SiteUtil;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class ManageSitesPageTest.
 * 
 * @author Richard Smith
 */
public class ManageSitesPageTest extends AbstractTest
{
    private static final int MAX_NUM_OF_SITES = 2;
    private DashBoardPage dashBoardPage;
    private ManageSitesPage manageSitesPage;
    private List<String> sites;

    /**
     * Setup.
     * 
     * @throws Exception the exception
     */
    @BeforeClass
    public void setup() throws Exception
    {
        dashBoardPage = loginAs(username, password);

        sites = new ArrayList<String>(MAX_NUM_OF_SITES);
        createTestSites(MAX_NUM_OF_SITES);

        manageSitesPage = dashBoardPage.getNav().selectManageSites().render();
    }

    /**
     * This method will NOT delete the sites permanently.
     * <p>
     * All the deleted sites will be archived which can be accessed through the Trashcan
     * 
     * @throws Exception
     */
    @AfterClass
    public void teardown() throws Exception
    {
        for (String siteName : sites)
        {
            SiteUtil.deleteSite(drone, siteName);
        }
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void constructor()
    {
        ManageSitesPage manageSitesPageNull = new ManageSitesPage(null);
    }

    /**
     * Test the manage sites page has some sites.
     */
    @Test
    public void testHasSites()
    {
        List<ManagedSiteRow> rows = manageSitesPage.getManagedSiteRows();
        assertTrue(rows.size() > 0);
        assertTrue(StringUtils.isNotEmpty(rows.get(0).getSiteName()));
    }

    /**
     * Test the manage sites page has pagination.
     */
    @Test
    public void testHasPagination()
    {
        assertNotNull(manageSitesPage.getPaginator());
    }

    /**
     * Test to find a bootstrapped site by name.
     */
    @Test
    public void testFindBootstrappedSite()
    {
        manageSitesPage.loadElements();

        String testSiteName = sites.get(0);
        ManagedSiteRow result = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
        assertNotNull(result);
        assertEquals(testSiteName.toLowerCase(), result.getSiteName().toLowerCase());
    }

    /**
     * Test to modify the visibility of a bootstrapped site.
     */
    @Test
    public void testModifyVisibilityOfBootstrappedSite()
    {
        manageSitesPage.loadElements();

        String testSiteName = sites.get(0);
        ManagedSiteRow result = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
        assertNotNull(result);
        result.getVisibility().selectValue(SiteVisibility.PRIVATE);

        manageSitesPage.loadElements();

        result = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
        assertEquals(result.getVisibility().getValue(), SiteVisibility.PRIVATE);
    }

    /**
     * Test to delete a bootstrapped site.
     */
    @Test
    public void testDeleteOfBootstrappedSite()
    {
        manageSitesPage.loadElements();

        String testSiteName = sites.get(1);
        ManagedSiteRow result = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
        assertNotNull(result);
        result.getActions().clickActionByNameAndDialogByButtonName("Delete Site", "OK");

        manageSitesPage.loadElements();

        result = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
        assertNull(result);
        sites.remove(1);

    }

    /**
     * Creates the test sites.
     * 
     * @param numOfSites the number of sites required
     */
    private void createTestSites(int numOfSites)
    {
        for (int i = 0; i < numOfSites; i++)
        {
            String siteName = "test-site-" + System.currentTimeMillis();
            boolean created = SiteUtil.createSite(drone, siteName, SiteVisibility.PUBLIC.getDisplayValue());
            assertTrue(created);
            sites.add(siteName);
        }
    }
}