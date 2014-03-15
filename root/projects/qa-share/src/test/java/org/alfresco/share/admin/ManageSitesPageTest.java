package org.alfresco.share.admin;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.*;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.admin.ManagedSiteRow;
import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.share.util.OpCloudTestContext;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.AbstractTests;
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

public class ManageSitesPageTest extends AbstractTests
{
    private static Log logger = LogFactory.getLog(ManageSitesPageTest.class);
    private static final int NUM_OF_SITES_PER_TYPE = 1; // TODO: this was reduced from 25 due to speed. Increase again once API Create Site call is functional.
    private static final int NUM_OF_USERS = 2;
    private static final int TOTAL_NUM_OF_SITES = NUM_OF_SITES_PER_TYPE * 3 * NUM_OF_USERS;
    private static final String PREFIX = "aaaa-tc497-";
    private static final String SITE_ADMIN_GROUP = "SITE_ADMINISTRATORS";
    private DashBoardPage dashBoardPage;
    private ManageSitesPage manageSitesPage;
    private OpCloudTestContext testContext;
    private List<String> users;


    /**
     * Setup.
     * 
     * @throws Exception the exception
     */
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        this.testContext = new OpCloudTestContext(this);
        String network = testContext.createNetworkName("acme");

        traceLog("Starting ManageSitesPageTest setup");

        // Create our test users
        for (int i = 0; i < NUM_OF_USERS; i++)
        {
            String username = testContext.createUserName("johnDoe-" + i, network);

            // Create the first user as a site admin, and the others as regular users.
            if (i == 0)
            {
                createTestUser(drone, username, DEFAULT_PASSWORD, SITE_ADMIN_GROUP);

                traceLog("Created Site Admin User: " + username);
            }
            else
            {
                createTestUserWithoutGroup(drone, username, DEFAULT_PASSWORD);

                traceLog("Created User: " + username);
            }

            testContext.addUser(username);
        }

        users = new ArrayList<> (testContext.getCreatedUsers());

        for (String username : users)
        {
            // Create sites as multiple users & of multiple types in order to confirm list is able to display them
            dashBoardPage = ShareUser.loginAs(drone, username, DEFAULT_PASSWORD);
            createTestSites(drone, testContext, PREFIX, SiteVisibility.PUBLIC, username, NUM_OF_SITES_PER_TYPE);
            createTestSites(drone, testContext, PREFIX, SiteVisibility.PRIVATE, username, NUM_OF_SITES_PER_TYPE);
            createTestSites(drone, testContext, PREFIX, SiteVisibility.MODERATED, username, NUM_OF_SITES_PER_TYPE);
            ShareUser.logout(drone);

            traceLog("Created " + NUM_OF_SITES_PER_TYPE * 3 + " sites for user: " + username);
        }

        dashBoardPage = ShareUser.loginAs(drone, users.get(0), DEFAULT_PASSWORD);

        traceLog("Created " + NUM_OF_SITES_PER_TYPE * 3 + " sites for user: " + username);

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
        traceLog("Starting teardown for ManageSitesPageTest");

        testContext.cleanupAllSites();
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
        traceLog("Starting ManageSitesPageTests");

        manageSitesPage.loadElements();

        verifySitesExist();

        paginationTest();

        if(!alfrescoVersion.isCloud())
        {

            // * Log Out
            ShareUser.logout(drone);

            // * Log in as Repo Admin
            dashBoardPage = ShareUser.loginAs(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // * Navigate to Manage Sites page.
            manageSitesPage = dashBoardPage.getNav().selectManageSitesPage().render();

            // * verify one site of each visibility appears in list for each user (test 6 sites)  (TC-497-16)
            verifySitesExist();

            // * log out
            ShareUser.logout(drone);

            // * log in as user 2
            dashBoardPage = ShareUser.loginAs(drone, users.get(1), DEFAULT_PASSWORD);

            // * verify manage sites link does not appear (TC-497-04)
            assertFalse(dashBoardPage.getNav().hasSelectManageSitesSiteAdminLink());
        }
    }

    /**
     * Checks to see if sites exist.
     */
    private void verifySitesExist()
    {
        traceLog("Checking Sites exist in list");

        for (int i = 0; i < TOTAL_NUM_OF_SITES;)
        {
            List<String> sites = new ArrayList<> (testContext.getCreatedSitesAsList());
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
        traceLog("Testing pagination performs as expected");

        // Assumes sites are not being created, modified or deleted by parallel processes.
        DocListPaginator docListPaginator = manageSitesPage.getPaginator();
        docListPaginator.gotoFirstResultsPage();
        // Check there isn't a previous page.
        assertFalse(docListPaginator.hasPrevPage());

        // TODO: This can't be tested until we're generating more than 100 sites.
        // Check there is a next page
        // assertTrue(docListPaginator.hasNextPage());
        // Check that clicking on the pagination actually does something.


        if (docListPaginator.hasNextPage())
        {
            List<ManagedSiteRow> managedSiteRowsOnCurrentPage = manageSitesPage.getManagedSiteRows();
            List<ManagedSiteRow> managedSiteRowsOnPreviousPage;


            do {
                managedSiteRowsOnPreviousPage = managedSiteRowsOnCurrentPage;
                docListPaginator.clickNextButton();
                manageSitesPage.loadElements();
                managedSiteRowsOnCurrentPage = manageSitesPage.getManagedSiteRows();
                assertNotEquals(managedSiteRowsOnCurrentPage, managedSiteRowsOnPreviousPage);
            }
            while (docListPaginator.hasNextPage());

            docListPaginator.clickPrevButton();
            manageSitesPage.loadElements();
            managedSiteRowsOnCurrentPage = manageSitesPage.getManagedSiteRows();
            // We've not modified the previousPage results, so these contain the results for the penultimate page.
            // Check that going back a page means we get the same results as we had before.
            assertEquals(managedSiteRowsOnCurrentPage, managedSiteRowsOnPreviousPage);
        }
    }
}