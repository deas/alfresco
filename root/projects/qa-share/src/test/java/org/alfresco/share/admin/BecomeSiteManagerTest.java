package org.alfresco.share.admin;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.admin.ManagedSiteRow;
import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.OpCloudTestContext;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserAdmin;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
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
    
    // TODO: Consider using uniqueTestDataString in qa-share.properties in place of PREFIX
    
    // TODO: Create groups enum in Share-po project, since many new test classes define and use it
    private static final String SITE_ADMIN_GROUP = "SITE_ADMINISTRATORS";

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
        super.setup();
        this.testContext = new OpCloudTestContext(this);
        
        String network = "acme-" + System.currentTimeMillis() + ".test";

        // Create two users - one site administrator and one not
        String siteAdminUser = getUserNameForDomain("AmandaAdmin", network);
        String testUser = getUserNameForDomain("JaneUser", network);
        
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, SITE_ADMIN_GROUP, siteAdminUser);
        testContext.addUser(siteAdminUser);
        
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, null, testUser);
        testContext.addUser(testUser);

        users = new ArrayList<>(testContext.getCreatedUsers());

        // Login as the site administrator
        ShareUser.login(drone, siteAdminUser);

        // Create a private site managed by the site administrator
        createTestSite(drone, testContext, getSiteName("admin"), SiteVisibility.PRIVATE, siteAdminUser);

        // Logout
        ShareUser.logout(drone);

        // Login as the non site administrator
        ShareUser.login(drone, testUser);

        // Create a private site managed by the non site administrator
        createTestSite(drone, testContext, getSiteName("testUser"), SiteVisibility.PRIVATE, testUser);

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
        ShareUser.login(drone, users.get(0));
        
        manageSitesPage = ShareUserAdmin.navigateToManageSites(drone);

        // TODO: Remove step, as its part of manageSitesPage render from all tests
        // Load manage sites page elements
        // manageSitesPage.loadElements();

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
        ShareUser.login(drone, users.get(0));

        manageSitesPage = ShareUserAdmin.navigateToManageSites(drone);

        // TODO: Remove this line as loadElements is part of render
        // Load manage sites page elements
        // manageSitesPage.loadElements();

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
        sites = null;
        users = null;
    }
    
    /**
     * Creates a test site.
     *
     * @param siteVisibility the SiteVisibility of the created site
     */
    // TODO: Move ShareUtil out of AbstractTest, this includes non share code
    public static void createTestSite(WebDrone drone, OpCloudTestContext testContext, String prefix, SiteVisibility siteVisibility, String createdUsername)
    {
        String siteName = getSiteName(prefix + "site-");
        boolean created = SiteUtil.createSite(drone, siteName, siteVisibility.getDisplayValue());
        

        assertTrue(created);
        testContext.addSite(createdUsername, siteName);
    }
}