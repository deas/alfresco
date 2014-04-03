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

// TODO: Add FailedTest listener enabling error reporting + screenshots
public class BecomeSiteManagerTest extends AbstractTests
{
    /** Constants */
    private static final String BECOME_SITE_MANAGER_BUTTON = "Become Site Manager";
    private static final String PREFIX = "aaaa-tc797-";
    
    // TODO: Create groups enum in Share-po project, since many new test classes define and use it
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
        traceLog("Starting setup for BecomeSiteManagerTest");

        super.setup();
        this.testContext = new OpCloudTestContext(this);
        
        // TODO: Redundant createNetworkName, createUserName. Replace with getUserNameForDoamin(username, domain)
        String network = testContext.createNetworkName("acme");

        // Create two users - one site administrator and one not
        String user1 = testContext.createUserName("AmandaAdmin", network);
        createTestUser(drone, user1, DEFAULT_PASSWORD, SITE_ADMIN_GROUP);
        testContext.addUser(user1);

        String user2 = testContext.createUserName("JaneUser", network);
        
        createTestUserWithoutGroup(drone, user2, DEFAULT_PASSWORD);
        testContext.addUser(user2);

        users = new ArrayList<>(testContext.getCreatedUsers());

        // Login as the site administrator
        dashBoardPage = ShareUser.loginAs(drone, users.get(0), DEFAULT_PASSWORD);

        // Create a private site managed by the site administrator
        createTestSite(drone, testContext, PREFIX, SiteVisibility.PRIVATE, users.get(0));

        // Logout
        ShareUser.logout(drone);

        // Login as the non site administrator
        dashBoardPage = ShareUser.loginAs(drone, users.get(1), DEFAULT_PASSWORD);

        // Create a private site managed by the non site administrator
        createTestSite(drone, testContext, PREFIX, SiteVisibility.PRIVATE, users.get(1));

        // Logout
        ShareUser.logout(drone);

        sites = new ArrayList<>(testContext.getCreatedSitesAsList());
    }

    /**
     * Test to click a 'Become Site Manager' button
     */
    @Test
    public void hasButtonTest() throws Exception
    {
        traceLog("Starting Test1");

        // Login as the site administrator and navigate to manage sites
        dashBoardPage = ShareUser.loginAs(drone, users.get(0), DEFAULT_PASSWORD);
        
        manageSitesPage = dashBoardPage.getNav().selectManageSitesPage().render();

        // TODO: Remove step, as its part of manageSitesPage render from all tests
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
        traceLog("Starting Test2");

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
    public void teardown() throws Exception
    {
        traceLog("Starting teardown for BecomeSiteManagerTest");

        //TODO: Remove, since its advised to leave test data as is after execution for results analysis
        testContext.cleanupAllSites();

        manageSitesPage = null;
        dashBoardPage = null;
        sites = null;
        users = null;
    }
}