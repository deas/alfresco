package org.alfresco.share.admin;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.admin.ManagedSiteRow;
import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.OpCloudTestContext;
import org.alfresco.share.util.ShareUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class BecomeSiteManagerTest.
 * This tests the 'Become Site Manager' actions feature of the Manage Sites page.
 * Test Case References tc-797-*
 * 
 * @link JIRA Story: ACE-788
 * @author Richard Smith
 */

public class BecomeSiteManagerTest extends AbstractTests
{
    /** The logger */
    private static Log logger = LogFactory.getLog(BecomeSiteManagerTest.class);

    /** Constants */
    private static final String BECOME_SITE_MANAGER_BUTTON = "Become Site Manager";
    private static final String PREFIX = "aaaa-tc797-";
    private static final String SITE_ADMIN_GROUP = "SITE_ADMINISTRATORS";

    private DashBoardPage dashBoardPage;
    private ManageSitesPage manageSitesPage;
    private OpCloudTestContext testContext;
    private List<String> sites;
    private List<String> users;

    /**
     * Setup.
     * 
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("Starting BecomeSiteManagerTest setup");

        super.setup();
        this.testContext = new OpCloudTestContext(this);
        String network = testContext.createNetworkName("acme");

        // Initialise arrays
        sites = new ArrayList<>(2);
        users = new ArrayList<>(2);

        // Create two users - one site administrator and one not
        String user1 = testContext.createUserName("AmandaAdmin", network);
        createTestUser(drone, user1, DEFAULT_PASSWORD, SITE_ADMIN_GROUP);
        users.add(user1);

        String user2 = testContext.createUserName("JaneUser", network);
        createTestUserWithoutGroup(drone, user2, DEFAULT_PASSWORD);
        users.add(user2);

        // Add the new users to the testContext for cleanup later
        testContext.addUser(users.toArray(new String[users.size()]));

        // Login as the site administrator
        dashBoardPage = ShareUser.loginAs(drone, users.get(0), DEFAULT_PASSWORD);

        // Create a private site managed by the site administrator
        sites.add(createTestSite(drone, testContext, PREFIX, SiteVisibility.PRIVATE, users.get(0)));

        // Logout
        ShareUser.logout(drone);

        // Login as the non site administrator
        dashBoardPage = ShareUser.loginAs(drone, users.get(1), DEFAULT_PASSWORD);

        // Create a private site managed by the non site administrator
        sites.add(createTestSite(drone, testContext, PREFIX, SiteVisibility.PRIVATE, users.get(1)));

        // Logout
        ShareUser.logout(drone);
    }

    /**
     * Test to click a 'Become Site Manager' button
     */
    @Test
    public void hasButtonTest() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("Starting BecomeSiteManagerTest hasButtonTest");

        // Login as the site administrator and navigate to manage sites
        dashBoardPage = ShareUser.loginAs(drone, users.get(0), DEFAULT_PASSWORD);
        manageSitesPage = dashBoardPage.getNav().selectManageSitesPage().render();

        // Load manage sites page elements
        manageSitesPage.loadElements();

        // Find the test site of the other test user and make sure it has a 'become manager' button
        ManagedSiteRow row = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(sites.get(1));
        Assert.assertNotNull(row);
        Assert.assertTrue(row.getActions().hasActionByName(BECOME_SITE_MANAGER_BUTTON));

        // Click the become manager button
        row.getActions().clickActionByName(BECOME_SITE_MANAGER_BUTTON);

        // Reload page elements
        manageSitesPage.loadElements();

        // Re-find site and make sure it no longer has a 'become manager' button
        row = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(sites.get(1));
        Assert.assertNotNull(row);
        Assert.assertFalse(row.getActions().hasActionByName(BECOME_SITE_MANAGER_BUTTON));

        // Logout
        ShareUser.logout(drone);
    }

    /**
     * Test to check a 'Become Site Manager' button is hidden
     */
    @Test
    public void hasNoButtonTest() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("Starting BecomeSiteManagerTest hasNoButtonTest");

        // Login as the site administrator and navigate to manage sites
        dashBoardPage = ShareUser.loginAs(drone, users.get(0), DEFAULT_PASSWORD);
        manageSitesPage = dashBoardPage.getNav().selectManageSitesPage().render();

        // Load manage sites page elements
        manageSitesPage.loadElements();

        // Find the test site of the administrator and make sure it does not have a 'become manager' button
        ManagedSiteRow row = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(sites.get(0));
        Assert.assertNotNull(row);
        Assert.assertFalse(row.getActions().hasActionByName(BECOME_SITE_MANAGER_BUTTON));

        // Logout
        ShareUser.logout(drone);
    }

    /**
     * Teardown.
     * 
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void teardown()
    {
        if (logger.isTraceEnabled())
            logger.trace("Starting BecomeSiteManagerTest teardown");

        try
        {
            testContext.cleanupAllSites();
        }
        catch (Exception e)
        {
            if (logger.isTraceEnabled())
                logger.trace("Teardown of BecomeSiteManagerTest failed", e);
        }

        manageSitesPage = null;
        dashBoardPage = null;
        sites = null;
        users = null;
    }
}