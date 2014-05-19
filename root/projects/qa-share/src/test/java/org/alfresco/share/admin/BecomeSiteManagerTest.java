package org.alfresco.share.admin;

import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.admin.ManagedSiteRow;
import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.OpCloudTestContext;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserAdmin;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
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
@Listeners(FailedTestListener.class)
public class BecomeSiteManagerTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(BecomeSiteManagerTest.class);
    
    /** Constants */
    // TODO: Do not hardcode, fix necessary for localisation
    private static final String BECOME_SITE_MANAGER_BUTTON = "Become Site Manager";
    
    // TODO: Consider using uniqueTestDataString in qa-share.properties in place of PREFIX
    
    // TODO: Create groups enum in Share-po project, since many new test classes define and use it
    private static final String SITE_ADMIN_GROUP = "SITE_ADMINISTRATORS";

    private ManageSitesPage manageSitesPage;
    private OpCloudTestContext testContext;

    private String testName;
    
    private String adminUser;
    private String testUser;
    
    private String adminUsersSite;
    private String testUsersSite;

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
        
        String network = "acme.test";
        testName = "BecomeSM" + testContext.getRunId();

        // Create two users - one site administrator and one not
        adminUser = getUserNameForDomain(testName + "Admin", network);
        testUser = getUserNameForDomain(testName + "JaneUser", network);
        
        adminUsersSite = getSiteName(testName + "admin");
        testUsersSite = getSiteName(testName + "testUser");
        
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, SITE_ADMIN_GROUP, adminUser);
        
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, null, testUser);

        // Login as the site administrator
        ShareUser.login(drone, adminUser);

        // Create a private site managed by the site administrator
        SiteUtil.createSite(drone, adminUsersSite, SiteVisibility.PRIVATE.getDisplayValue());

        // Logout
        ShareUser.logout(drone);

        // Login as the non site administrator
        ShareUser.login(drone, testUser);

        // Create a private site managed by the non site administrator
        SiteUtil.createSite(drone, testUsersSite, SiteVisibility.PRIVATE.getDisplayValue());

        // Logout
        ShareUser.logout(drone);
    }

    /**
     * Test to click a 'Become Site Manager' button
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_2947() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Starting Test1");
        }

        // Login as the site administrator and navigate to manage sites
        ShareUser.login(drone, adminUser);
        
        manageSitesPage = ShareUserAdmin.navigateToManageSites(drone);

        // Find the test site of the other test user and make sure it has a 'become manager' button
        ManagedSiteRow row = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testUsersSite);
        Assert.assertNotNull(row);
        Assert.assertTrue(row.getActions().hasActionByName(BECOME_SITE_MANAGER_BUTTON));

        // Click the become manager button
        row.getActions().clickActionByName(BECOME_SITE_MANAGER_BUTTON);

        // Reload page elements
        manageSitesPage.loadElements();

        // Re-find site and make sure it no longer has a 'become manager' button
        row = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testUsersSite);
        Assert.assertNotNull(row);
        Assert.assertFalse(row.getActions().hasActionByName(BECOME_SITE_MANAGER_BUTTON));

        // Logout
        ShareUser.logout(drone);
    }

    /**
     * Test to check a 'Become Site Manager' button is hidden
     */
    @Test(groups = "AlfrescoOne")
    public void ALF_2948() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Starting Test2");
        }

        // Login as the site administrator and navigate to manage sites
        ShareUser.login(drone, adminUser);

        manageSitesPage = ShareUserAdmin.navigateToManageSites(drone);

        // TODO: Remove this line as loadElements is part of render
        // Load manage sites page elements
        // manageSitesPage.loadElements();

        // Find the test site of the administrator and make sure it does not have a 'become manager' button
        ManagedSiteRow row = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(adminUsersSite);
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
        if (logger.isTraceEnabled())
        {
            logger.trace("Starting Test2");
        }
        

        //TODO: Remove, since its advised to leave test data as is after execution for results analysis
        testContext.cleanupAllSites();

        manageSitesPage = null;
        //super.tearDown();
    }   
}