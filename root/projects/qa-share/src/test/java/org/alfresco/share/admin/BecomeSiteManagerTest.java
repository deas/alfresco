package org.alfresco.share.admin;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.admin.ManagedSiteRow;
import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.SiteUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class BecomeSiteManagerTest.
 * This tests the 'Become Site Manager' actions feature of the Manage Sites page.
 * 
 * Test Case References tc-797-*
 * 
 * @link JIRA Story: ACE-788
 * @author Richard Smith
 */

public class BecomeSiteManagerTest extends AbstractTest
{

    /** The logger */
    private static Log logger = LogFactory.getLog(BecomeSiteManagerTest.class);

    /** Constants */
    private static final String PREFIX = "aaaa-tc797-";
    private static final String ESUFFIX = "@example.com";
    private static final String NEW_USER_PASSWORD = "password";
    private static final String BECOME_SITE_MANAGER_BUTTON = "Become Site Manager";

    private DashBoardPage dashBoardPage;
    private ManageSitesPage manageSitesPage;
    private List<String> sites;
    private List<String> users;
    private int userCounter = 1;
    private int siteCounter = 1;

    /**
     * Setup.
     * 
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        trace("Starting setup for BecomeSiteManagerTest");

        // Initialise sites and users
        users = new ArrayList<>(2);
        sites = new ArrayList<>(2);

        // Login as admin:admin
        dashBoardPage = loginAs(username, password);

        // Create two users - one site administrator and one not
        createUser(true);
        createUser(false);

        // Logout
        logout(drone);

        // Login as the site administrator
        dashBoardPage = loginAs(users.get(0), NEW_USER_PASSWORD);

        // Create a private site managed by the site administrator
        createSite(SiteVisibility.PRIVATE);

        // Logout
        logout(drone);

        // Login as the non site administrator
        dashBoardPage = loginAs(users.get(1), NEW_USER_PASSWORD);

        // Create a private site managed by the non site administrator
        createSite(SiteVisibility.PRIVATE);

        // Logout
        logout(drone);
    }

    /**
     * Test to click a 'Become Site Manager' button
     */
    @Test
    public void hasButtonTest() throws Exception
    {
        trace("Starting Test1");

        // Login as the site administrator and navigate to manage sites
        dashBoardPage = loginAs(users.get(0), NEW_USER_PASSWORD);
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
        logout(drone);
    }

    /**
     * Test to check a 'Become Site Manager' button is hidden
     */
    @Test
    public void hasNoButtonTest() throws Exception
    {
        trace("Starting Test2");

        // Login as the site administrator and navigate to manage sites
        dashBoardPage = loginAs(users.get(0), NEW_USER_PASSWORD);
        manageSitesPage = dashBoardPage.getNav().selectManageSitesPage().render();

        // Load manage sites page elements
        manageSitesPage.loadElements();

        // Find the test site of the administrator and make sure it does not have a 'become manager' button
        ManagedSiteRow row = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(sites.get(0));
        Assert.assertNotNull(row);
        Assert.assertFalse(row.getActions().hasActionByName(BECOME_SITE_MANAGER_BUTTON));

        // Logout
        logout(drone);
    }

    /**
     * Teardown.
     * 
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void teardown() throws Exception
    {
        trace("Starting teardown for BecomeSiteManagerTest");

        // Login as user 1
        dashBoardPage = loginAs(users.get(0), NEW_USER_PASSWORD);

        // Delete site 1
        SiteUtil.deleteSite(drone, sites.get(0));

        // Logout
        logout(drone);

        // Login as user 2
        dashBoardPage = loginAs(users.get(1), NEW_USER_PASSWORD);

        // Delete site 2
        SiteUtil.deleteSite(drone, sites.get(1));

        // Logout
        logout(drone);

        // Login as admin:admin
        dashBoardPage = loginAs(username, password);

        // Delete users
        for (String username : users)
        {
            ShareUtil.deleteUser(username);
        }

        manageSitesPage = null;
        dashBoardPage = null;
        sites = null;
        users = null;

        // Logout
        logout(drone);
    }

    /**
     * Creates a test user.
     * 
     * @param admin should this be a site administrator?
     */
    private void createUser(boolean admin)
    {
        UserSearchPage userPage = dashBoardPage.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();

        String username = PREFIX + "user-" + userCounter + "-" + System.currentTimeMillis() + (AbstractTests.isAlfrescoVersionCloud(drone) ? ESUFFIX : "");

        if (admin)
        {
            newPage.createEnterpriseUserWithGroup(username, username, username, PREFIX + System.currentTimeMillis() + ESUFFIX, NEW_USER_PASSWORD,
                    "SITE_ADMINISTRATORS");
        }
        else
        {
            newPage.createEnterpriseUser(username, username, username, PREFIX + System.currentTimeMillis() + ESUFFIX, NEW_USER_PASSWORD);
        }

        users.add(username);
        userCounter++;

        trace("Created User: " + username);
    }

    /**
     * Creates a test site.
     * 
     * @param visibility the SiteVisibility of the created site
     */
    private void createSite(SiteVisibility visibility)
    {
        String siteName = PREFIX + "site-" + siteCounter + "-" + System.currentTimeMillis();
        boolean created = SiteUtil.createSite(drone, siteName, visibility.getDisplayValue());
        assertTrue(created);
        sites.add(siteName);
        siteCounter++;
    }

    /**
     * Compact proxy for the logger.trace method.
     * 
     * @param string to log
     */
    private void trace(String string)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace(string);
        }
    }
}