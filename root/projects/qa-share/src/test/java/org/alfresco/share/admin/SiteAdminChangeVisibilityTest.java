/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.admin;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.admin.ManagedSiteRow;
import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.OpCloudTestContext;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Jamal Kaabi-Mofrad
 */
@Listeners(FailedTestListener.class)
public class SiteAdminChangeVisibilityTest extends AbstractTests
{

    private static final Logger logger = Logger.getLogger(SiteAdminChangeVisibilityTest.class);

    private static final String SITE_ADMIN_GROUP = "SITE_ADMINISTRATORS";
    private OpCloudTestContext testContext;
    private String user1;
    private String user2;
    private String user1PublicSite;
    private String user1ModeratedSite;
    private String user1PrivateSite;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        prepare();
    }

    /**
     * Prepares the required data.
     * 
     * @throws Exception
     */
    private void prepare() throws Exception
    {
        this.testContext = new OpCloudTestContext(this);
        String domain = this.testContext.createNetworkName("acme");

        // TODO: Remove redundant util createUserName: getUserNameForDomain("johndoe", domain); does the same
        this.user1 = this.testContext.createUserName("johndoe", domain);
        this.user2 = this.testContext.createUserName("joebloggs", domain);

        // Create user1
        // TODO: Remove redundant util: CreateUserAPI.CreateActivateUserAsTenantAdmin does the same
        createTestUser(ADMIN_USERNAME, user1, "John", "Doe", DEFAULT_PASSWORD);
        // Create user2
        createTestUser(ADMIN_USERNAME, user2, "Joe", "Bloggs", DEFAULT_PASSWORD);
        // Add the created users, so they can be cleaned up
        this.testContext.addUser(user1, user2);

        if (logger.isTraceEnabled())
        {
            logger.trace("Users created " + user1 + ", " + user2 + "]");
        }

        this.user1PublicSite = this.testContext.createSiteName("u1PubSite");
        this.user1ModeratedSite = this.testContext.createSiteName("u1ModSite");
        this.user1PrivateSite = this.testContext.createSiteName("u1PriSite");

        // login as user1 to create sites
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        logger.info(user1 + " logged in - drone.");

        // TODO: Remove redundant util: ShareUser.createSite(driver, siteName, siteVisibility), plus, this will not work with other types of drone, incl hybrid
        createTestSite(user1PublicSite, SiteVisibility.PUBLIC.getDisplayValue());
        createTestSite(user1ModeratedSite, SiteVisibility.MODERATED.getDisplayValue());
        createTestSite(user1PrivateSite, SiteVisibility.PRIVATE.getDisplayValue());

        // Add the created sites, so they can be cleaned up
        this.testContext.addSite(user1, user1PublicSite, user1ModeratedSite, user1PrivateSite);

        if (logger.isTraceEnabled())
        {
            logger.trace("Sites created by " + user1 + " [" + user1PublicSite + ", " + user1ModeratedSite + ", " + user1PrivateSite + "]");
        }
        ShareUser.logout(drone);

    }

    /**
     * logs out the user after each test method.
     * 
     * @throws Exception
     */
    @AfterMethod
    public void logout()
    {
        try
        {
            ShareUser.logout(drone);
            logger.info("user logged out from - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Cleanup
     * 
     * @throws Exception
     */
    @AfterClass
    public void teardown() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Starting teardown for " + testName);
        }
        // TODO: Remove cleanup as it was advised to keep the test data as is after test run for easy test analysis
        this.testContext.cleanupSites(user1, DEFAULT_PASSWORD);
    }

    /**
     * Test changing site visibility as a Site Administrator (Enterprise) or a
     * Network Administrator (Cloud), based on the underlying Alfresco version
     * currently running.
     * <p>
     * The tests are described in the following test cases:
     * <ul>
     * <li>ACE_508_02 change site visibility from Public to Private</li>
     * <li>ACE_508_04 change site visibility from Private to Public</li>
     * <li>ACE_508_06 change site visibility from Moderated to Public</li>
     * <li>ACE_508_08 change site visibility from Public to Moderated</li>
     * <li>ACE_508_10 change site visibility from Private to Moderated</li>
     * <li>ACE_508_12 change site visibility from Moderated to Private</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    public void ACE_508_AsSiteAdminOrNetworkAdmin() throws Exception
    {
        // login as user2
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Open user2 dash board page
        ShareUser.openUserDashboard(drone);

        // TODO: Move the navigation inside the util, passing drone as param and reduce PO dependencies.
        // ManageSitesPage manageSitesPage = dashBoard.getNav().selectManageSitesPage().render();

        // ACE_508_02 Public to Private
        testSiteVisibility(drone, user1PublicSite, SiteVisibility.PUBLIC, SiteVisibility.PRIVATE);
        // ACE_508_04 Private to Public
        testSiteVisibility(drone, user1PublicSite, SiteVisibility.PRIVATE, SiteVisibility.PUBLIC);

        // ACE_508_06 Moderated to Public
        testSiteVisibility(drone, user1ModeratedSite, SiteVisibility.MODERATED, SiteVisibility.PUBLIC);
        // ACE_508_08 Public to Moderated
        testSiteVisibility(drone, user1ModeratedSite, SiteVisibility.PUBLIC, SiteVisibility.MODERATED);

        // ACE_508_10 Private to Moderated
        testSiteVisibility(drone, user1PrivateSite, SiteVisibility.PRIVATE, SiteVisibility.MODERATED);
        // ACE_508_12 Moderated to Private
        testSiteVisibility(drone, user1PrivateSite, SiteVisibility.MODERATED, SiteVisibility.PRIVATE);
    }

    /**
     * Test changing site visibility as the Supper Administrator (repo Admin).
     * The test is only relevant to Alfresco Enterprise.
     * <p>
     * The tests are described in the following test cases:
     * <ul>
     * <li>ACE_508_02 change site visibility from Public to Private</li>
     * <li>ACE_508_04 change site visibility from Private to Public</li>
     * <li>ACE_508_06 change site visibility from Moderated to Public</li>
     * <li>ACE_508_08 change site visibility from Public to Moderated</li>
     * <li>ACE_508_10 change site visibility from Private to Moderated</li>
     * <li>ACE_508_12 change site visibility from Moderated to Private</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly")
    public void ACE_508_AsSuperAdmin() throws Exception
    {
        // We don't really need this check. however, it's added in case you run
        // the tests within your IDE and not excluding the relevant groups.
        if (isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("This operation is not supported for Cloud.");
        }

        // login as user2
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Open user2 dash board page
        ShareUser.openUserDashboard(drone);
        
        // ACE_508_02 Public to PRivate
        testSiteVisibility(drone, user1PublicSite, SiteVisibility.PUBLIC, SiteVisibility.PRIVATE);
        // ACE_508_04 Private to Public
        testSiteVisibility(drone, user1PublicSite, SiteVisibility.PRIVATE, SiteVisibility.PUBLIC);

        // ACE_508_06 Moderated to Public
        testSiteVisibility(drone, user1ModeratedSite, SiteVisibility.MODERATED, SiteVisibility.PUBLIC);
        // ACE_508_08 Public to Moderated
        testSiteVisibility(drone, user1ModeratedSite, SiteVisibility.PUBLIC, SiteVisibility.MODERATED);

        // ACE_508_10 Private to Moderated
        testSiteVisibility(drone, user1PrivateSite, SiteVisibility.PRIVATE, SiteVisibility.MODERATED);
        // ACE_508_12 Moderated to Private
        testSiteVisibility(drone, user1PrivateSite, SiteVisibility.MODERATED, SiteVisibility.PRIVATE);
    }

    /**
     * A helper method to find a site by name and change its visibility
     * Expects the user is logged in and on dashboardPage
     * @param drone
     * @param siteName the name of the site
     * @param from the old visibility of the site
     * @param to the new visibility of the site
     */
    private void testSiteVisibility(WebDrone drone, String siteName, SiteVisibility from, SiteVisibility to)
    {
        // TODO: Define this as a util in main/share/util, implementing only assert in testClass
        DashBoardPage dashBoard = getSharePage(drone).render();
        ManageSitesPage manageSitesPage = dashBoard.getNav().selectManageSitesPage().render();
        
        ManagedSiteRow managedSiteRow = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(siteName);
        assertNotNull(managedSiteRow);
        assertEquals(from, managedSiteRow.getVisibility().getValue());
        managedSiteRow.getVisibility().selectValue(to);
        
        managedSiteRow = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(siteName);
        assertEquals(to, managedSiteRow.getVisibility().getValue());
    }

    /**
     * Creates user as a network Admin in the Cloud or as a member of the
     * SITE_ADMINISTRATORS group in the Enterprise (on-premise)
     * 
     * @param inviterUsername user name of the inviter
     * @param userName the invitee's user name
     * @param firstName the invitee's first name
     * @param lastName the invitee's last name
     * @param password the invitee's password
     * @throws Exception
     */
    private void createTestUser(String inviterUsername, String userName, String firstName, String lastName, String password) throws Exception
    {
        boolean created = false;
        if (isAlfrescoVersionCloud(drone))
        {
            created = CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, new String[] { userName, firstName, lastName, password });
        }
        else
        {
            created = ShareUser.createEnterpriseUserWithGroup(drone, inviterUsername, userName, firstName, lastName, password, SITE_ADMIN_GROUP);
        }
        assertTrue(created);
    }

    /**
     * Creates the test site
     * 
     * @param siteName the name of the site
     * @param siteVisibility the site visibility (Public | Moderated | Private)
     */
    private void createTestSite(String siteName, String siteVisibility)
    {
        boolean created = SiteUtil.createSite(drone, siteName, siteVisibility);
        assertTrue(created);
    }
}
