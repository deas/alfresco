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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.repository;

import org.alfresco.po.alfresco.LoginAlfrescoPage;
import org.alfresco.po.alfresco.MyAlfrescoPage;
import org.alfresco.po.alfresco.TenantAdminConsolePage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.share.sanity.FolderSanityTest;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.AlfrescoUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by Olga Lokhach
 */
@Listeners(FailedTestListener.class)
@Test(groups = "EnterpriseOnly", timeOut = 400000)
public class MultiTenancyTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(FolderSanityTest.class);
    protected String errorNotification = "Your authentication details have not been recognized";
    private String[] tenantUser = new String[2];
    private String userName = "admin@";
    protected String userNameTest = "user" + "@test.com";
    private TenantAdminConsolePage tenantConsolePage;
    private SharePage resultPage;
    private UserSearchPage userSearchPage;
    private NewUserPage newUserPage;

    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        drone.deleteCookies();
        logger.info("Starting Tests: " + testName);
    }

    /**
     * Test: ALF-3138: Enable multi-tenancy
     */

    @Test
    public void ALF_3138() throws Exception
    {

        //Login to Alfresco
        LoginAlfrescoPage loginPage = new LoginAlfrescoPage(drone);
        drone.navigateTo(loginPage.getAlfrescoURL(shareUrl));
        loginPage.render();
        assertTrue(loginPage.isOpened(), String.format("Page %s does not opened", loginPage));
        MyAlfrescoPage alfrescoPage = loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        alfrescoPage.render();
        assertTrue(alfrescoPage.userIsLoggedIn("admin"));

        //Open Tenant Administration Console
        TenantAdminConsolePage tenantConsolePage = new TenantAdminConsolePage(drone);
        drone.navigateTo(tenantConsolePage.getTenantURL(shareUrl));
        tenantConsolePage.render();
        assertTrue(tenantConsolePage.isOpened(), String.format("Page %s does not opened", tenantConsolePage));

    }

    /**
     * Test: ALF-3139: Create tenant
     */

    @Test
    public void ALF_3139() throws Exception
    {

        //Create Tenant
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantConsolePage.createTenant("tenantUser", "password" + " " + downloadDirectory);
        tenantUser = AlfrescoUtil.createTenant(drone, "", "");

        //Show a list of all tenants
        tenantConsolePage.sendCommands("show tenants");
        tenantConsolePage.render();
        assertTrue(tenantConsolePage.findText().contains("Enabled  - Tenant: tenantuser") && tenantConsolePage.findText().contains(downloadDirectory));
        assertTrue(
            tenantConsolePage.findText().contains("Enabled  - Tenant: " + tenantUser[0]) && tenantConsolePage.findText().contains("alf_data/contentstore"));

        //Check Login to Share
        SharePage resultPage = login(drone, userName + tenantUser[0], tenantUser[1]).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

    }

    /**
     * Test: ALF-3140: Show Tenant(s) and Help
     */

    @Test
    public void ALF_3140() throws Exception
    {

        //Creating new Tenant
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantConsolePage.render();
        tenantUser = AlfrescoUtil.createTenant(drone, "", "");

        //Show a list of all tenants
        tenantConsolePage.sendCommands("show tenants");
        tenantConsolePage.render();
        assertTrue(
            tenantConsolePage.findText().contains("Enabled  - Tenant: " + tenantUser[0]) && tenantConsolePage.findText().contains("alf_data/contentstore"));

        //Show Help
        tenantConsolePage.sendCommands("help");
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("List this help"), true);

    }

    /**
     * Test: ALF-3141: Disable/ Enable tenant
     */

    @Test
    public void ALF_3141() throws Exception
    {

        // Disable tenant
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantUser = AlfrescoUtil.createTenant(drone, "", "");
        tenantConsolePage.sendCommands("disable " + tenantUser[0]);
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("Disabled tenant: " + tenantUser[0]), true);

        // Login Fails: When tenant is disabled
        resultPage = login(drone, userName + tenantUser[0], tenantUser[1]).render();
        assertFalse(resultPage.isLoggedIn());

        //  Check Page titles
        resultPage = drone.getCurrentPage().render();
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_LOGIN));
        assertFalse(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // Check Error Message
        LoginPage loginPage = (LoginPage) resultPage.render();
        assertTrue(loginPage.hasErrorMessage());
        logger.info(loginPage.getErrorMessage());
        assertTrue(loginPage.getErrorMessage().contains(errorNotification));

        // Enable tenant
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantConsolePage.sendCommands("enable " + tenantUser[0]);
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("Enabled tenant: " + tenantUser[0]), true);

        //Login Succeeds: When tenant is enable
        resultPage = login(drone, userName + tenantUser[0], tenantUser[1]).render(maxWaitTime);
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

    }

    /**
     * Test: ALF-3142: Close tenant admin console
     */

    @Test
    public void ALF_3142() throws Exception
    {

        //Press Close
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantConsolePage.render();
        tenantConsolePage.clickClose();
        assertTrue(drone.getTitle().contains("Alfresco Explorer"));

    }

    /**
     * Test: ALF-3143: Create user by tenant
     */

    @Test
    public void ALF_3143() throws Exception
    {

        // Creating new tenant.
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantUser = AlfrescoUtil.createTenant(drone, "", "");
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantUser[0]), true, "Tenant already exists: " + tenantUser[0]);

        // Login to Share as Admin tenant.
        resultPage = login(drone, userName + tenantUser[0], tenantUser[1]).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // Creating new user.
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUser("user1", userNameTest, userNameTest, userNameTest, userNameTest);
        userSearchPage.searchFor(userNameTest).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        // Check login to Share as created user tenant.
        resultPage = loginAs(drone, "user1@" + tenantUser[0], userNameTest).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

    }

    /**
     * Test: ALF-3144: Log in. Incorrect password
     */

    @Test
    public void ALF_3144() throws Exception
    {

        // Creating new tenant.
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantUser = AlfrescoUtil.createTenant(drone, "", "");
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantUser[0]), true, "Tenant already exists: " + tenantUser[0]);

        // Login Fails: When password is incorrect
        resultPage = login(drone, userName + tenantUser[0], "uuu").render();
        assertFalse(resultPage.isLoggedIn());

        //  Check Page titles
        resultPage = drone.getCurrentPage().render();
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_LOGIN));
        assertFalse(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // Check Error Message
        LoginPage loginPage = (LoginPage) resultPage.render();
        assertTrue(loginPage.hasErrorMessage());
        logger.info(loginPage.getErrorMessage());
        assertTrue(loginPage.getErrorMessage().contains(errorNotification));

        //Login Succeeds: When password is correct
        resultPage = login(drone, userName + tenantUser[0], tenantUser[1]).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

    }

    /**
     * Test: ALF-3145: Impossibility to see users created by other tenant
     */

    @Test
    public void ALF_3145() throws Exception
    {

        String[] tenantUser1;
        String[] tenantUser2;
        String message;

        // Creating two tenants.
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantUser1 = AlfrescoUtil.createTenant(drone, "", "");
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantUser1[0]), true, "Tenant already exists: " + tenantUser1[0]);

        tenantUser2 = AlfrescoUtil.createTenant(drone, "", "");
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantUser2[0]), true, "Tenant already exists: " + tenantUser2[0]);

        //Login to Share as first admin tenant
        resultPage = login(drone, userName + tenantUser1[0], tenantUser1[1]).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // Creating two users.
        for (int i = 1; i <= 2; i++)
        {
            userSearchPage = resultPage.getNav().getUsersPage().render();
            newUserPage = userSearchPage.selectNewUser().render();
            newUserPage.createEnterpriseUser("user1" + i, userNameTest, userNameTest, userNameTest, userNameTest);
            userSearchPage.searchFor(userNameTest).render();
            assertTrue(userSearchPage.hasResults());
        }
        ShareUser.logout(drone);

        //Login to Share as second admin tenant
        resultPage = login(drone, userName + tenantUser2[0], tenantUser2[1]).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //Check impossibility to see users created by other tenant
        userSearchPage = resultPage.getNav().getUsersPage().render();
        userSearchPage.searchFor(userNameTest).render();
        message = userSearchPage.getResultsStatus();
        assertTrue(message.endsWith("found 0 results.") || message.equals("No Results."));

    }

    /**
     * Test: ALF-3146: Verify case sensitivity in tenant creation and tenant login.
     */

    @Test
    public void ALF_3146() throws Exception
    {

        //Creating new tenant.
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantUser = AlfrescoUtil.createTenant(drone, "", "");
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantUser[0]), true, "Tenant already exists: " + tenantUser[0]);

        //Create fails : When tenant has the same name, but with capital letters.
        tenantUser = AlfrescoUtil.createTenant(drone, tenantUser[0].toUpperCase(), tenantUser[1]);
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("Tenant already exists: " + tenantUser[0].toLowerCase()), true);

        //Login to Share as created tenant.
        resultPage = login(drone, userName + tenantUser[0].toLowerCase(), tenantUser[1]).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        resultPage.render();
        assertTrue(drone.getCurrentUrl().contains("admin%40" + tenantUser[0].toLowerCase()));
        ShareUser.logout(drone);

        //Check login to Share as another tenant with the same name but with capital letters
        resultPage = login(drone, userName + tenantUser[0].toUpperCase(), tenantUser[1]).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        resultPage.render();
        assertTrue(drone.getCurrentUrl().contains("admin%40" + tenantUser[0].toLowerCase()));

    }

    /**
     * Test: ALF-3147: Add tenant to alfresco_administrators group
     */

    @Test
    public void ALF_3147() throws Exception
    {

        String groupToAdd = "ALFRESCO_ADMINISTRATORS";
        String[] tenantUser1;
        String[] tenantUser2;

        //Creating two tenants
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantUser1 = AlfrescoUtil.createTenant(drone, "", "");
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantUser1[0]), true, "Tenant already exists: " + tenantUser1[0]);

        tenantUser2 = AlfrescoUtil.createTenant(drone, "", "");
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantUser2[0]), true, "Tenant already exists: " + tenantUser2[0]);

        //Login to Share as first tenant admin
        resultPage = login(drone, userName + tenantUser1[0], tenantUser1[1]).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //Creating new user with alfresco_administrators group.
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUserWithGroup("user1", userNameTest, userNameTest, userNameTest, userNameTest, groupToAdd);
        userSearchPage.searchFor(userNameTest).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        //Login to Share as second tenant admin
        resultPage = login(drone, userName + tenantUser2[0], tenantUser2[1]).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //Creating new user with alfresco_administrators group.
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUserWithGroup("user1", userNameTest, userNameTest, userNameTest, userNameTest, groupToAdd);
        userSearchPage.searchFor(userNameTest).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        //Login to Share as admin (not tenant)
        resultPage = login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        ShareUser.logout(drone);

        //Login to Share as first tenant user
        resultPage = login(drone, "user1@" + tenantUser1[0], userNameTest).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        ShareUser.logout(drone);

        //Login to Share as second tenant user
        resultPage = login(drone, "user1@" + tenantUser2[0], userNameTest).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
    }

    private SharePage login(WebDrone drone, String userName, String userPassword)
    {
        SharePage resultPage = null;

        try
        {
            resultPage = ShareUser.login(drone, userName, userPassword);
        }
        catch (SkipException se)
        {
            resultPage = ShareUser.getSharePage(drone);
        }
        return resultPage;
    }

}
