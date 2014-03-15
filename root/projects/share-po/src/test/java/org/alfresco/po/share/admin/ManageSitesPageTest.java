package org.alfresco.po.share.admin;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.*;
import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.po.share.util.SiteUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * The Class ManageSitesPageTest.
 *
 * This tests the Manage Sites page behaves as one would expect.
 *
 * @link JIRA Story: ACE-87
 * @ Test Case Reference tc-497-01 to tc-497-28
 * @author David Webster
 */

public class ManageSitesPageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(ManageSitesPageTest.class);
    private static final int NUM_OF_SITES_PER_TYPE = 2; // TODO: this was reduced from 25 due to speed. Increase again once API Create Site call is functional.
    private static final int NUM_OF_USERS = 2;
    private static final int TOTAL_NUM_OF_SITES = NUM_OF_SITES_PER_TYPE * 3 * NUM_OF_USERS;
    private static final String PREFIX = "aaaa-tc497-";
    private static final String NEW_USER_PASSWORD = "password"; // Hard coded in createEnterpriseUser method.
    private DashBoardPage dashBoardPage;
    private ManageSitesPage manageSitesPage;
    private DocListPaginator docListPaginator;
    private List<String> sites;
    private List<String> users;

    /**
     * Setup.
     * 
     * @throws Exception the exception
     */
    @BeforeClass
    public void setup() throws Exception
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("Starting ManageSitesPageTest setup");
        }
        // Public, Private and Moderated sites are created for every user
        sites = new ArrayList<>(TOTAL_NUM_OF_SITES);
        users = new ArrayList<>(NUM_OF_USERS);

        dashBoardPage = loginAs(username, password);

        UserSearchPage userPage = dashBoardPage.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();

        // Create our test users
        for (int i = 0; i < NUM_OF_USERS; i++)
        {
            String username = PREFIX + "user-" + i + "-" + System.currentTimeMillis();

            // Create the first user as a site admin, and the others as regular users.
            if (i == 0)
            {
                newPage.createEnterpriseUserWithGroup(username, username, username, "tc497-" + System.currentTimeMillis() + "@example.com", NEW_USER_PASSWORD, "SITE_ADMINISTRATORS");

                if(logger.isTraceEnabled())
                {
                    logger.trace("Created Site Admin User: " + username);
                }
            }
            else
            {
                newPage = userPage.selectNewUser().render();
                newPage.createEnterpriseUser(username, username, username, "tc497-" + System.currentTimeMillis() + "@example.com", NEW_USER_PASSWORD);

                if(logger.isTraceEnabled())
                {
                    logger.trace("Created User: " + username);
                }
            }

            users.add(username);
        }

        logout(drone);

        for (String username : users)
        {
            // Create sites as multiple users & of multiple types in order to confirm list is able to display them
            dashBoardPage = loginAs(username, NEW_USER_PASSWORD);
            createTestSites(NUM_OF_SITES_PER_TYPE, SiteVisibility.PUBLIC);
            createTestSites(NUM_OF_SITES_PER_TYPE, SiteVisibility.PRIVATE);
            createTestSites(NUM_OF_SITES_PER_TYPE, SiteVisibility.MODERATED);
            logout(drone);

            if(logger.isTraceEnabled())
            {
                logger.trace("Created " + NUM_OF_SITES_PER_TYPE * 3 + " sites for user: " + username);
            }
        }

        dashBoardPage = loginAs(username, password);


        if(logger.isTraceEnabled())
        {
            logger.trace("Created " + NUM_OF_SITES_PER_TYPE * 3 + " sites for user: " + username);
        }

        manageSitesPage = dashBoardPage.getNav().selectManageSitesPage().render();
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
        if(logger.isTraceEnabled())
        {
            logger.trace("Starting teardown for ManageSitesPageTest");
        }
        for (String siteName : sites)
        {
            SiteUtil.deleteSite(drone, siteName);
        }
        for (String username : users)
        {
            ShareUtil.deleteUser(username);
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
     * Test to find a bootstrapped site by name.
     */
    @Test
    public void ManageSitesPageTests() throws Exception
    {

        if(logger.isTraceEnabled())
        {
            logger.trace("Starting ManageSitesPageTests");
        }
        manageSitesPage.loadElements();

        verifySitesExist();

        paginationTest();

        /* TODO: This doesn't work yet because selectManageSitesPage only works for an admin/network admin. See todo notes in ShareUtil.isUserAdmin.
        if(!alfrescoVersion.isCloud())
        {

            // * Log Out
            logout(drone);
            // * Log in as User 1
            dashBoardPage = loginAs(users.get(0), NEW_USER_PASSWORD);

            // * Navigate to Manage Sites page.
            manageSitesPage = dashBoardPage.getNav().selectManageSitesPage().render();

            // * verify one site of each visibility appears in list for each user (test 6 sites)  (TC-497-16)
            verifySitesExist();

            // * log out
            logout(drone);

            // * log in as user 2
            dashBoardPage = loginAs(users.get(1), NEW_USER_PASSWORD);

            // TODO: Abstract this out into the page object.
            // * verify manage sites link does not appear (TC-497-04)
            String sitesHeaderLinkSelector = "span[id='HEADER_SITES_CONSOLE_text']>a";
            assertEquals(drone.findAll(By.cssSelector(sitesHeaderLinkSelector)).size(), 0);
        }
        */
    }

    /**
     * Checks to see if sites exist.
     */
    private void verifySitesExist()
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("Checking Sites exist in list");
        }
        for (int i = 0; i < TOTAL_NUM_OF_SITES;)
        {
            String testSiteName = sites.get(i);
            ManagedSiteRow result = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
            assertNotNull(result);
            assertEquals(testSiteName.toLowerCase(), result.getSiteName().toLowerCase());

            // Increment the counter to jump to the next site type.
            i = i + NUM_OF_SITES_PER_TYPE;
        }
    }

    /**
     *
     * Verifies the tests run correctly
     */
    private void paginationTest()
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("Testing pagination performs as expected");
        }
        // Assumes sites are not being created, modified or deleted by parallel processes.
        docListPaginator = manageSitesPage.getPaginator();
        docListPaginator.gotoFirstResultsPage();
        // Check there isn't a previous page.
        assertFalse(docListPaginator.hasPrevPage());
        // Check there is a next page
        assertTrue(docListPaginator.hasNextPage());
        // Check that clicking on the pagination actually does something.
        List<ManagedSiteRow> ManagedSiteRowsOnCurrentPage = manageSitesPage.getManagedSiteRows();
        List<ManagedSiteRow> ManagedSiteRowsOnPreviousPage;
        do
        {
            ManagedSiteRowsOnPreviousPage = ManagedSiteRowsOnCurrentPage;
            docListPaginator.clickNextButton();
            manageSitesPage.loadElements();
            ManagedSiteRowsOnCurrentPage = manageSitesPage.getManagedSiteRows();
            assertNotEquals(ManagedSiteRowsOnCurrentPage, ManagedSiteRowsOnPreviousPage);
        }
        while (docListPaginator.hasNextPage());
        docListPaginator.clickPrevButton();
        manageSitesPage.loadElements();
        ManagedSiteRowsOnCurrentPage = manageSitesPage.getManagedSiteRows();
        // We've not modified the previousPage results, so these contain the results for the penultimate page.
        // Check that going back a page means we get the same results as we had before.
        assertEquals(ManagedSiteRowsOnCurrentPage, ManagedSiteRowsOnPreviousPage);
    }

    /**
     * Creates the test sites.
     *
     * @param numOfSites the number of sites required
     */
    private void createTestSites(int numOfSites, SiteVisibility siteVisibility)
    {
        for (int i = 0; i < numOfSites; i++)
        {
            String siteName = PREFIX + "site-" + i + "-" +  System.currentTimeMillis();
            boolean created = SiteUtil.createSite(drone, siteName, siteVisibility.getDisplayValue());
            assertTrue(created);
            sites.add(siteName);
        }
    }
}