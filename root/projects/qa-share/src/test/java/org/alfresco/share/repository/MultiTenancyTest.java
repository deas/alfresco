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

import org.alfresco.po.alfresco.*;
import org.alfresco.po.share.*;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.InviteMembersPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.DocumentAction;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.ModelAndMessagesConsole;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.systemsummary.TenantConsole;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.share.workflow.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.AlfrescoHttpClient;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.alfresco.po.share.enums.UserRole.COLLABORATOR;
import static org.alfresco.po.share.enums.UserRole.CONTRIBUTOR;
import static org.testng.Assert.*;

/**
 * Created by Olga Lokhach
 */
@Listeners(FailedTestListener.class)

public class MultiTenancyTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(MultiTenancyTest.class);
    protected String errorNotification = "Your authentication details have not been recognized";
    private String userName = "admin@";
    private static String adminTenantDomain1;
    private static String adminTenantDomain2;
    private static String userTenantDomain1;
    private static String userTenantDomain2;
    private TenantAdminConsolePage tenantConsolePage;
    private SharePage resultPage;
    private UserSearchPage userSearchPage;
    private NewUserPage newUserPage;
    private FolderRulesPage folderRulesPage;
    private RepositoryPage repositoryPage;
    private FolderDetailsPage folderDetailsPage;
    private CreateRulePage createRulePage;
    private FolderRulesPageWithRules folderRulesPageWithRules;
    private ManagePermissionsPage PermPage;
    private GroupsPage page;
    private NewGroupPage newGroupPage;
    private MyTasksDashlet myTasksDashlet;
    private MyTasksPage myTasksPage;
    private InviteMembersPage inviteMembersPage;
    private SiteDashboardPage siteDashboardPage;
    private DashBoardPage dashBoardPage;
    private String DATA_DICTIONARY_FOLDER = "Data Dictionary";
    private static final String INVITATION_URL_IN_EMAIL = "//DIV/P[3]/A";
    private String invitationUrlInEmail;
    private static String tenantDomain1;
    private static String tenantDomain2;
    private static TenantConsole tenantConsole;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Starting Tests: " + testName);
    }

    @BeforeMethod(alwaysRun = true)
    public void getNewTenantDomainName()
    {
        tenantDomain1 = RandomUtil.getRandomString(3) + ".local";
        tenantDomain2 = RandomUtil.getRandomString(3) + ".local";
    }

    /**
     * Test: AONE-15244: Enable multi-tenancy
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_15244() throws Exception
    {

        if (alfrescoVersion.getVersion() >= 5.0)
        {

            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));
        }
        else
        {
            //Login to Alfresco
            LoginAlfrescoPage loginPage = new LoginAlfrescoPage(drone);
            drone.navigateTo(LoginAlfrescoPage.getAlfrescoURL(shareUrl));
            ShareUser.deleteSiteCookies(drone, shareUrl);
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
    }

    /**
     * Test: AONE-15245: Create tenant
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_15245() throws Exception
    {

        if (alfrescoVersion.getVersion() >= 5.0)
        {

            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create two tenants
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD + " " + downloadDirectory);
            tenantConsole.render();
            tenantConsole.createTenant(tenantDomain2, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain2));

            //Show a list of all tenants
            tenantConsole.sendCommand("show tenants");
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("Enabled  - Tenant: " + tenantDomain1) && tenantConsole.findText().contains(downloadDirectory));
            assertTrue(tenantConsole.findText().contains("Enabled  - Tenant: " + tenantDomain2) && tenantConsole.findText().contains("alf_data/contentstore"));
        }

        else

        {
            //Create two tenants
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
            tenantConsolePage.createTenant(tenantDomain1, DEFAULT_PASSWORD + " " + downloadDirectory);
            AlfrescoUtil.createTenant(drone, tenantDomain2, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertTrue(
                    tenantConsolePage.findText().contains("created tenant: " + tenantDomain1));
            assertTrue(
                    tenantConsolePage.findText().contains("created tenant: " + tenantDomain2));

            //Show a list of all tenants
            tenantConsolePage.sendCommands("show tenants");
            tenantConsolePage.render();
            assertTrue(
                    tenantConsolePage.findText().contains("Enabled  - Tenant: " + tenantDomain1) && tenantConsolePage.findText().contains(downloadDirectory));
            assertTrue(
                    tenantConsolePage.findText().contains("Enabled  - Tenant: " + tenantDomain2) && tenantConsolePage.findText()
                            .contains("alf_data/contentstore"));
        }

        //Check Login to Share
        adminTenantDomain2 = getUserNameForDomain("admin", tenantDomain2).replace("user", "");
        resultPage = ShareUser.login(drone, adminTenantDomain2, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

    }

    /**
     * Test: AONE-15246: Show Tenant(s) and Help
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_15246() throws Exception
    {

        if (alfrescoVersion.getVersion() >= 5.0)
        {

            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create any tenant
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            //Show a list of all tenants
            tenantConsole.sendCommand("show tenants");
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("Enabled  - Tenant: " + tenantDomain1) && tenantConsole.findText().contains("alf_data/contentstore"));

            //Show Help
            tenantConsole.sendCommand("help");
            tenantConsole.render();
            assertEquals(tenantConsole.findText().contains("List this help"), true);
        }

        else

        {

            //Creating any Tenant
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
            tenantConsolePage.render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render(maxWaitTime);

            //Show a list of all tenants
            tenantConsolePage.sendCommands("show tenants");
            tenantConsolePage.render();
            assertTrue(
                    tenantConsolePage.findText().contains("Enabled  - Tenant: " + tenantDomain1) && tenantConsolePage.findText()
                            .contains("alf_data/contentstore"));

            //Show Help
            tenantConsolePage.sendCommands("help");
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("List this help"), true);

        }

    }

    /**
     * Test: AONE-15247: Disable/ Enable tenant
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_15247() throws Exception
    {

        if (alfrescoVersion.getVersion() >= 5.0)
        {

            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create any tenant
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            //Disable tenant
            tenantConsole.sendCommand("disable " + tenantDomain1);
            tenantConsole.render();
            assertEquals(tenantConsole.findText().contains("Disabled tenant: " + tenantDomain1), true);

        }

        else

        {
            // Disable tenant
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render(maxWaitTime);
            tenantConsolePage.sendCommands("disable " + tenantDomain1);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("Disabled tenant: " + tenantDomain1), true);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");

        // Login Fails: When tenant is disabled
        resultPage = login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
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

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Enable tenant
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            tenantConsole.sendCommand("enable " + tenantDomain1);
            tenantConsole.render();
            assertEquals(tenantConsole.findText().contains("Enabled tenant: " + tenantDomain1), true);
        }
        else
        {
            // Enable tenant
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsolePage.sendCommands("enable " + tenantDomain1);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("Enabled tenant: " + tenantDomain1), true);
        }

        //Login Succeeds: When tenant is enable
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

    }

    /**
     * Test: AONE-15248: Close tenant admin console
     */

    @Test(groups = "Enterprise42Only", timeOut = 400000)
    public void AONE_15248() throws Exception
    {

        //Press Close
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
        tenantConsolePage.render();
        tenantConsolePage.clickClose();
        assertTrue(drone.getTitle().contains("Alfresco Explorer"));

    }

    /**
     * Test: AONE-15249: Create user by tenant
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_15249() throws Exception
    {

        if (alfrescoVersion.getVersion() >= 5.0)
        {

            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create any tenant
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));
        }
        else
        {
            // Create any tenant
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");
        userTenantDomain1 = getUserNameForDomain("", tenantDomain1);

        // Login to Share as Admin tenant.
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // Creating new user.
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUser(userTenantDomain1, userTenantDomain1, userTenantDomain1, userTenantDomain1, DEFAULT_PASSWORD);
        userSearchPage.searchFor(userTenantDomain1).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        // Check login to Share as created user tenant.
        resultPage = ShareUser.login(drone, userTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

    }

    /**
     * Test: AONE-15250: Log in. Incorrect password
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_15250() throws Exception
    {

        if (alfrescoVersion.getVersion() >= 5.0)
        {

            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create any tenant
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));
        }
        else
        {
            // Create any tenant
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");

        // Login Fails: When password is incorrect
        resultPage = login(drone, adminTenantDomain1, "uuu").render();
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
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

    }

    /**
     * Test: AONE-15251: Impossibility to see users created by other tenant
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_15251() throws Exception
    {

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Creating two tenants.
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            tenantConsole.createTenant(tenantDomain2, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain2));
        }

        else
        {
            // Creating two tenants.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);

            AlfrescoUtil.createTenant(drone, tenantDomain2, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain2), true, "Tenant already exists: " + tenantDomain2);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");
        adminTenantDomain2 = getUserNameForDomain("admin", tenantDomain2).replace("user", "");
        userTenantDomain1 = getUserNameForDomain("", tenantDomain1);

        //Login to Share as first admin tenant
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // Creating two users.
        for (int i = 1; i <= 2; i++)
        {
            userSearchPage = resultPage.getNav().getUsersPage().render();
            newUserPage = userSearchPage.selectNewUser().render();
            newUserPage.createEnterpriseUser("user" + i, userTenantDomain1, userTenantDomain1, userTenantDomain1, DEFAULT_PASSWORD);
            userSearchPage.searchFor(userTenantDomain1).render();
            assertTrue(userSearchPage.hasResults());
        }
        ShareUser.logout(drone);

        //Login to Share as second admin tenant
        resultPage = ShareUser.login(drone, adminTenantDomain2, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //Check impossibility to see users created by other tenant
        userSearchPage = resultPage.getNav().getUsersPage().render();
        userSearchPage.searchFor(userTenantDomain1).render();
        String message = userSearchPage.getResultsStatus();
        assertTrue(message.endsWith("found 0 results.") || message.equals("No Results."));

    }

    /**
     * Test: AONE-15252: Verify case sensitivity in tenant creation and tenant login.
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_15252() throws Exception
    {

        if (alfrescoVersion.getVersion() >= 5.0)
        {

            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create any tenant
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            //Create fails : When tenant has the same name, but with capital letters.
            tenantConsole.createTenant(tenantDomain1.toUpperCase(), DEFAULT_PASSWORD);
            tenantConsole.render();
            assertEquals(tenantConsole.findText().contains("Tenant already exists: " + tenantDomain1.toLowerCase()), true);
        }

        else
        {
            //Creating new tenant.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);

            //Create fails : When tenant has the same name, but with capital letters.
            AlfrescoUtil.createTenant(drone, tenantDomain1.toUpperCase(), DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("Tenant already exists: " + tenantDomain1.toLowerCase()), true);
        }

        //Login to Share as created tenant.
        resultPage = ShareUser.login(drone, userName + tenantDomain1.toLowerCase(), DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        resultPage.render();
        assertTrue(drone.getCurrentUrl().contains("admin%40" + tenantDomain1.toLowerCase()));
        ShareUser.logout(drone);

        //Check login to Share as another tenant with the same name but with capital letters
        resultPage = ShareUser.login(drone, userName + tenantDomain1.toUpperCase(), DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        resultPage.render();
        assertTrue(drone.getCurrentUrl().contains("admin%40" + tenantDomain1.toLowerCase()));

    }

    /**
     * Test: AONE-15253: Add tenant to alfresco_administrators group
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_15253() throws Exception
    {

        String groupToAdd = "ALFRESCO_ADMINISTRATORS";

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Creating two tenants.
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            tenantConsole.createTenant(tenantDomain2, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain2));
        }

        else
        {
            // Creating two tenants.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);

            AlfrescoUtil.createTenant(drone, tenantDomain2, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain2), true, "Tenant already exists: " + tenantDomain2);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");
        adminTenantDomain2 = getUserNameForDomain("admin", tenantDomain2).replace("user", "");
        userTenantDomain1 = getUserNameForDomain("", tenantDomain1);
        userTenantDomain2 = getUserNameForDomain("", tenantDomain2);

        //Login to Share as first tenant admin
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //Creating new user with alfresco_administrators group.
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUserWithGroup(userTenantDomain1, userTenantDomain1, userTenantDomain1, userTenantDomain1, DEFAULT_PASSWORD, groupToAdd);
        userSearchPage.searchFor(userTenantDomain1).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        //Login to Share as second tenant admin
        resultPage = ShareUser.login(drone, adminTenantDomain2, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //Creating new user with alfresco_administrators group.
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUserWithGroup(userTenantDomain2, userTenantDomain2, userTenantDomain2, userTenantDomain2, DEFAULT_PASSWORD, groupToAdd);
        userSearchPage.searchFor(userTenantDomain2).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        //Login to Share as admin (not tenant)
        resultPage = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        ShareUser.logout(drone);

        //Login to Share as first tenant user
        resultPage = ShareUser.login(drone, userTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        ShareUser.logout(drone);

        //Login to Share as second tenant user
        resultPage = ShareUser.login(drone, userTenantDomain2, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
    }

    /**
     * Test: AONE-6590 Export tenant
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6590() throws Exception
    {
        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create any tenant
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            //Export tenant
            tenantConsole.sendCommand("export " + tenantDomain1 + " " + downloadDirectory);
            tenantConsole.waitForFile(downloadDirectory + tenantDomain1 + "_models.acp");
            tenantConsole.waitForFile(downloadDirectory + tenantDomain1 + "_spaces.acp");
            tenantConsole.waitForFile(downloadDirectory + tenantDomain1 + "_spaces_archive.acp");
            tenantConsole.waitForFile(downloadDirectory + tenantDomain1 + "_system.acp");
            tenantConsole.waitForFile(downloadDirectory + tenantDomain1 + "_users.acp");
            tenantConsole.waitForFile(downloadDirectory + tenantDomain1 + "_versions2.acp");
        }

        else
        {
            //Creating new tenant.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);

            //Export tenant
            tenantConsolePage.sendCommands("export " + tenantDomain1 + " " + downloadDirectory);
            tenantConsolePage.waitForFile(downloadDirectory + tenantDomain1 + "_models.acp");
            tenantConsolePage.waitForFile(downloadDirectory + tenantDomain1 + "_spaces.acp");
            tenantConsolePage.waitForFile(downloadDirectory + tenantDomain1 + "_spaces_archive.acp");
            tenantConsolePage.waitForFile(downloadDirectory + tenantDomain1 + "_system.acp");
            tenantConsolePage.waitForFile(downloadDirectory + tenantDomain1 + "_users.acp");
            tenantConsolePage.waitForFile(downloadDirectory + tenantDomain1 + "_versions2.acp");
        }

        assertTrue(ShareUser.getContentsOfDownloadedArchieve(drone, downloadDirectory).contains(tenantDomain1 + "_models.acp"),
                tenantDomain1 + "_models.acp is not found");

        assertTrue(ShareUser.getContentsOfDownloadedArchieve(drone, downloadDirectory).contains(tenantDomain1 + "_spaces.acp"),
                tenantDomain1 + "_spaces.acp is not found");

        assertTrue(ShareUser.getContentsOfDownloadedArchieve(drone, downloadDirectory).contains(tenantDomain1 + "_spaces_archive.acp"),
                tenantDomain1 + "_spaces_archive.acp is not found");

        assertTrue(ShareUser.getContentsOfDownloadedArchieve(drone, downloadDirectory).contains(tenantDomain1 + "_system.acp"),
                tenantDomain1 + "_system.acp is not found");

        assertTrue(ShareUser.getContentsOfDownloadedArchieve(drone, downloadDirectory).contains(tenantDomain1 + "_users.acp"),
                tenantDomain1 + "_users.acp is not found");

        assertTrue(ShareUser.getContentsOfDownloadedArchieve(drone, downloadDirectory).contains(tenantDomain1 + "_versions2.acp"),
                tenantDomain1 + "_versions2.acp is not found");
    }

    /**
     * Test: AONE-6599 Impossibility to execute a rule created by other tenant
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6599() throws Exception
    {
        String folder = "Guest Home";
        String testName = getTestName();
        String fileName = getFileName(testName) + "_1";
        File file = newFile(fileName, fileName);

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Creating two tenants.
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            tenantConsole.createTenant(tenantDomain2, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain2));
        }

        else
        {
            // Creating two tenants.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);

            AlfrescoUtil.createTenant(drone, tenantDomain2, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain2), true, "Tenant already exists: " + tenantDomain2);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");
        adminTenantDomain2 = getUserNameForDomain("admin", tenantDomain2).replace("user", "");

        //Login to Share as first admin tenant
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());

        // Navigate to repository page
        repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        folderDetailsPage = repositoryPage.getNavigation().selectFolderInNavBar(REPO).render();
        assertTrue(folderDetailsPage.isDocumentActionPresent(DocumentAction.MANAGE_RULES), "Manage Rules is not present");

        // Create the rule for Company Home
        folderDetailsPage.selectManageRules().render();
        folderRulesPage = drone.getCurrentPage().render();

        // Fill "Name" field with correct data
        createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("Move to Guest folder");

        // Select "Inbound" value from "When" drop-down select control
        WhenSelectorImpl whenSelectorIml = createRulePage.getWhenOptionObj();
        whenSelectorIml.selectInbound();

        // Select 'All items' from "If" drop-down select control
        AbstractIfSelector ifSelector = createRulePage.getIfOptionObj();
        ifSelector.selectIFOption(0);

        // Select 'Classifiable' from drop-down select control
        ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();
        actionSelectorEnterpImpl.selectMoveToDestination(REPO, folder);

        // Click "Create" button
        folderRulesPageWithRules = createRulePage.clickCreate().render();
        assertTrue(folderRulesPageWithRules.isRuleNameDisplayed("Move to Guest folder"), "Rule isn't present");
        ShareUser.logout(drone);

        //Login to Share as second admin tenant
        resultPage = ShareUser.login(drone, adminTenantDomain2, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());

        // Navigate to repository page
        repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        repositoryPage.getNavigation().selectFolderInNavBar(REPO);
        folderDetailsPage = getCurrentPage(drone).render(maxWaitTime);
        assertTrue(folderDetailsPage.isDocumentActionPresent(DocumentAction.MANAGE_RULES), "Manage Rules is not present");

        // Check that impossibility to execute a rule created by other tenant
        folderDetailsPage.selectManageRules().render();
        folderRulesPage = drone.getCurrentPage().render();
        assertFalse(folderRulesPage.isPageCorrect(REPO), "Rule is present, but should not");
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);
        assertTrue(ShareUserSitePage.isFileVisible(drone, fileName));
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folder);
        assertFalse(ShareUserSitePage.isFileVisible(drone, fileName));
    }

    /**
     * Test: AONE-6596 Deploy dynamic workflow
     */

    @Test(groups = "DataPrepMultiTenancy")
    public void dataPrep_AONE_6596() throws Exception
    {

        JmxUtils.setAlfrescoServerProperty("Alfresco:Name=WorkflowInformation", "JBPMEngineEnabled", true);
        JmxUtils.setAlfrescoServerProperty("Alfresco:Name=WorkflowInformation", "JBPMWorkflowDefinitionsVisible", true);
    }

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6596() throws Exception
    {

        String testName = getTestName();
        String file1 = "lifecycleModel.xml";
        String file2 = "lifecycle_processdefinition.xml";
        String file3 = "lifecycle-messages.properties";
        String fileName = getFileName(testName);
        File fileName1 = new File(DATA_FOLDER + SLASH + "deploy-workflow", file1);
        File fileName2 = new File(DATA_FOLDER + SLASH + "deploy-workflow", file2);
        File fileName3 = new File(DATA_FOLDER + SLASH + "deploy-workflow", file3);
        File file = newFile(fileName, fileName);
        EditDocumentPropertiesPage editProps;
        RepositoryAdminConsolePage repositoryAdminConsolePage;

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create any tenant
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));
        }
        else
        {
            // Creating any tenant.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");

        // Login to Share as Admin tenant.
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());

        // Navigate to repository page
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Models");

        // Activate model
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, fileName1);
        repositoryPage.renderItem(maxWaitTime, file1);
        assertEquals(repositoryPage.getFiles().size(), 1);
        editProps = repositoryPage.getFileDirectoryInfo(file1).selectEditProperties().render();
        editProps.setModelActive();
        editProps.selectSave().render();

        // Workflow deployed
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Workflow Definitions");
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, fileName2);
        repositoryPage.renderItem(maxWaitTime, file2);
        assertEquals(repositoryPage.getFiles().size(), 1);
        editProps = repositoryPage.getFileDirectoryInfo(file2).selectEditProperties().render();
        editProps.setWorkflowDeployed();
        editProps.selectSave().render();

        // Upload workflow messages
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Messages");
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, fileName3);
        repositoryPage.renderItem(maxWaitTime, file3);
        assertEquals(repositoryPage.getFiles().size(), 1);

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Model and Messages Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, adminTenantDomain1, DEFAULT_PASSWORD).render();
            ModelAndMessagesConsole repoConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.RepoConsole).render();
            assertTrue(repoConsole.getTitle().contains("Model and Messages Console"));

            //Send "reload messages lifecycle-messages" command
            repoConsole.sendCommand("reload messages lifecycle-messages");
            assertTrue(repoConsole.findText().contains("Message resource bundle reloaded: lifecycle-messages"));
        }
        else
        {
            // Open the Admin Console and send "reload messages lifecycle-messages" command
            repositoryAdminConsolePage = AlfrescoUtil.repoAdminConsoleLogin(drone, shareUrl, adminTenantDomain1, DEFAULT_PASSWORD);
            repositoryAdminConsolePage.sendCommands("reload messages lifecycle-messages");
            repositoryAdminConsolePage.render();
            assertTrue(repositoryAdminConsolePage.findText().contains("Message resource bundle reloaded: lifecycle-messages"));
        }

        // Login to Share as Admin tenant and create a content.
        ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);
        assertTrue(ShareUserSitePage.isFileVisible(drone, fileName));

        // Check that Lifecycle Review & Approve workflow is present on Start Workflow page;
        StartWorkFlowPage startWorkFlowPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).selectStartWorkFlow().render();
        List<WorkFlowType> userWorkFlow = startWorkFlowPage.getWorkflowTypes();
        assertTrue(userWorkFlow.contains(WorkFlowType.LIFECYCLE_REVIEW_AND_APPROVE));

    }

    /**
     * Test: AONE-6597 Inherit parent space permissions
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6597() throws Exception
    {

        String testName = getTestName();
        String folderName = getFolderName(testName);
        String testFolder1 = folderName + "_1";
        String testFolder2 = folderName + "_2";
        String fileName1 = getFileName(testName) + "_1";
        File file1 = newFile(fileName1, fileName1);

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create any tenant
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));
        }

        else
        {
            // Creating new tenant.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render(maxWaitTime);
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");
        userTenantDomain1 = getUserNameForDomain("", tenantDomain1);

        // Login to Share as Admin tenant.
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());

        // Creating new user.
        CreateUserAPI.CreateActivateUser(drone, adminTenantDomain1, userTenantDomain1);
        ShareUser.logout(drone);

        // Creating folder, content and sub-folders.
        ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file1);
        ShareUserRepositoryPage.createFolderInRepository(drone, testFolder1, testFolder1);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName + SLASH + testFolder1);
        ShareUserRepositoryPage.createFolderInRepository(drone, testFolder2, testFolder2);

        //Add user to locally set permissions, uncheck "Inherit Permissions", save and return to repository page
        ShareUserMembers.managePermissionsOnContent(drone, userTenantDomain1, testFolder2, UserRole.COLLABORATOR, true);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName + SLASH + testFolder1);
        PermPage = ShareUser.returnManagePermissionPage(drone, testFolder2);
        assertTrue(UserRole.COLLABORATOR.getRoleName().equalsIgnoreCase(PermPage.getExistingPermission(userTenantDomain1).getRoleName()));
        PermPage.toggleInheritPermission(false, ManagePermissionsPage.ButtonType.Yes);
        PermPage.selectSave().render();

        //Verify Inherit permissions options in Manage Permissions page
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName + SLASH + testFolder1);
        PermPage = ShareUser.returnManagePermissionPage(drone, testFolder2);
        assertFalse(PermPage.isInheritPermissionEnabled(), "Inherit permissions options in Manage Permissions page is Enabled");

    }

    /**
     * Test: AONE-6604 Creating pooled task by tenant-user
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6604() throws Exception
    {

        String testName = getTestName();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, fileName);
        String group = getGroupName(testName);
        String workFlowName = testName;
        String dueDate = new DateTime().plusDays(2).toString("dd/MM/yyyy");

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Creating two tenants.
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            tenantConsole.createTenant(tenantDomain2, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain2));
        }

        else
        {
            // Creating two tenants.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);

            AlfrescoUtil.createTenant(drone, tenantDomain2, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain2), true, "Tenant already exists: " + tenantDomain2);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");
        adminTenantDomain2 = getUserNameForDomain("admin", tenantDomain2).replace("user", "");
        userTenantDomain1 = getUserNameForDomain("", tenantDomain1);
        userTenantDomain2 = getUserNameForDomain("", tenantDomain2);

        // Login to Share as first admin tenant
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());

        // Creating new group and user.
        page = resultPage.getNav().getGroupsPage();
        page = page.clickBrowse().render();
        newGroupPage = page.navigateToNewGroupPage().render();
        page = newGroupPage.createGroup(group, group, NewGroupPage.ActionButton.CREATE_GROUP).render();
        assertTrue(page.getGroupList().contains(group), String.format("Group: %s can not be found", group));
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUserWithGroup(userTenantDomain1, userTenantDomain1, userTenantDomain1, userTenantDomain1, DEFAULT_PASSWORD, group);
        userSearchPage.searchFor(userTenantDomain1).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        // Login to Share as second tenant admin
        resultPage = ShareUser.login(drone, adminTenantDomain2, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());

        // Creating new group and user.
        page = resultPage.getNav().getGroupsPage();
        page = page.clickBrowse().render();
        newGroupPage = page.navigateToNewGroupPage().render();
        page = newGroupPage.createGroup(group, group, NewGroupPage.ActionButton.CREATE_GROUP).render();
        assertTrue(page.getGroupList().contains(group), String.format("Group: %s can not be found", group));
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUserWithGroup(userTenantDomain2, userTenantDomain2, userTenantDomain2, userTenantDomain2, DEFAULT_PASSWORD, group);
        userSearchPage.searchFor(userTenantDomain2).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        // Login to Share as first admin tenant.
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());

        // Creating folder and content.
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        ShareUserRepositoryPage.uploadFileInRepository(drone, file);
        assertTrue(ShareUserSitePage.isFileVisible(drone, fileName));

        // Creating "Pooled Review & Approve" workflow for content and assign it to group.
        StartWorkFlowPage startWorkFlowPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).selectStartWorkFlow().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.POOLED_REVIEW_AND_APPROVE)).render();
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails();
        formDetails.setMessage(workFlowName);
        formDetails.setDueDate(dueDate);
        formDetails.setReviewers(Arrays.asList(group));
        formDetails.setTaskPriority(Priority.MEDIUM);
        newWorkflowPage.startWorkflow(formDetails).render();

        // Check the document is marked with icon
        ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName);
        assertTrue(ShareUserSitePage.getFileDirectoryInfo(drone, fileName).isPartOfWorkflow(), "Verifying the file is part of a workflow");
        ShareUser.logout(drone);

        // Login to Share as assignee user (first tenant user);
        dashBoardPage = ShareUser.login(drone, userTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(dashBoardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // Check the task is present in MyTasks for first tenant user
        myTasksDashlet = dashBoardPage.getDashlet(DASHLET_TASKS).render();
        List<ShareLink> userTasks1 = myTasksDashlet.getTasks();
        assertTrue(userTasks1.toString().contains(workFlowName));
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
        assertTrue(myTasksPage.isTaskPresent(workFlowName));
        ShareUser.logout(drone);

        // Login to Share as second tenant user;
        dashBoardPage = ShareUser.login(drone, userTenantDomain2, DEFAULT_PASSWORD).render();
        assertTrue(dashBoardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // Check the task is not present in MyTasks for second tenant user
        myTasksDashlet = dashBoardPage.getDashlet(DASHLET_TASKS).render();
        List<ShareLink> userTasks2 = myTasksDashlet.getTasks();
        assertFalse(userTasks2.toString().contains(workFlowName));
        myTasksPage = ShareUserWorkFlow.navigateToMyTasksPage(drone).render();
        assertFalse(myTasksPage.isTaskPresent(workFlowName));
    }

    /**
     * Test: AONE-6609 Sending invitation by tenants
     */

    @Test(groups = "DataPrepMultiTenancy")
    public void dataPrep_AONE_6609() throws Exception
    {

        MailUtil.configOutBoundEmail();
    }

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6609() throws Exception
    {

        String testName = getTestName();
        String testUser1 = MailUtil.BOT_MAIL_1;
        String siteName = getSiteName(testName);

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Creating two tenants.
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            tenantConsole.createTenant(tenantDomain2, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain2));
        }

        else
        {
            // Creating two tenants.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);

            AlfrescoUtil.createTenant(drone, tenantDomain2, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain2), true, "Tenant already exists: " + tenantDomain2);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");
        adminTenantDomain2 = getUserNameForDomain("admin", tenantDomain2).replace("user", "");
        userTenantDomain1 = getUserNameForDomain("", tenantDomain1);
        userTenantDomain2 = getUserNameForDomain("", tenantDomain2);

        // Login to Share as first admin tenant
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());

        // Creating new user.
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUser(userTenantDomain1, userTenantDomain1, userTenantDomain1, testUser1, DEFAULT_PASSWORD);
        userSearchPage.searchFor(userTenantDomain1).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        // Creating a site and invite the user to it.
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        inviteMembersPage = siteDashboardPage.getSiteNav().selectInvite().render();
        List<String> foundUsers = inviteMembersPage.searchUser(userTenantDomain1);
        inviteMembersPage.clickAddUser(userTenantDomain1).render();
        inviteMembersPage.selectRoleForAll(COLLABORATOR);
        assertTrue(inviteMembersPage.isInviteButtonEnabled(), "Invite button is disabled.");
        inviteMembersPage.clickInviteButton();
        assertTrue(MailUtil.isMailPresent(testUser1, String.format("You have been invited to join the %s site", siteName)), "Email about invite don't send.");
        ShareUser.logout(drone);

        //Login to Share as first tenant user, check that task "Invitation to join a site" is present.
        dashBoardPage = ShareUser.login(drone, userTenantDomain1, DEFAULT_PASSWORD).render();
        myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        List<ShareLink> tasks = myTasksDashlet.getTasks();
        assertEquals(tasks.get(0).getDescription(),
                String.format("Invitation to join %s site", siteName),
                String.format("Information about invitation task don't display for user[%s]", testUser1));
        ShareUser.logout(drone);

        // Login to Share as second admin tenant
        resultPage = ShareUser.login(drone, adminTenantDomain2, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());

        // Creating new user.
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUser(userTenantDomain2, userTenantDomain2, userTenantDomain2, testUser1, DEFAULT_PASSWORD);
        userSearchPage.searchFor(userTenantDomain2).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        // Creating a site and invite the user to it.
        resultPage = ShareUser.login(drone, adminTenantDomain2, DEFAULT_PASSWORD).render();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        inviteMembersPage = siteDashboardPage.getSiteNav().selectInvite().render();
        foundUsers = inviteMembersPage.searchUser(userTenantDomain2);
        inviteMembersPage.clickAddUser(userTenantDomain2).render();
        inviteMembersPage.selectRoleForAll(CONTRIBUTOR);
        assertTrue(inviteMembersPage.isInviteButtonEnabled(), "Invite button is disabled.");
        inviteMembersPage.clickInviteButton();
        assertTrue(MailUtil.isMailPresent(testUser1, String.format("You have been invited to join the %s site", siteName)), "Email about invite don't send.");
        ShareUser.logout(drone);

        //Login to Share as second tenant user, check that task "Invitation to join a site" is present.
        dashBoardPage = ShareUser.login(drone, userTenantDomain2, DEFAULT_PASSWORD).render();
        myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        tasks = myTasksDashlet.getTasks();
        assertEquals(tasks.get(0).getDescription(),
                String.format("Invitation to join %s site", siteName),
                String.format("Information about invitation task don't display for user[%s]", testUser1));
    }

    /**
     * Test: AONE-6606 Form-data in webscripts
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6606() throws Exception
    {

        String file1 = "formdata.get.desc.xml";
        String file2 = "formdata.get.html.ftl";
        String file3 = "formdata.post.js";
        String file4 = "formdata.post.desc.xml";
        String file5 = "formdata.post.html.ftl";
        File fileName1 = new File(DATA_FOLDER + SLASH + "formdata", file1);
        File fileName2 = new File(DATA_FOLDER + SLASH + "formdata", file2);
        File fileName3 = new File(DATA_FOLDER + SLASH + "formdata", file3);
        File fileName4 = new File(DATA_FOLDER + SLASH + "formdata", file4);
        File fileName5 = new File(DATA_FOLDER + SLASH + "formdata", file5);

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create any tenant
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));
        }

        else
        {
            // Creating new tenant.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render(maxWaitTime);
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");

        // Login to Share as Admin tenant.
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());

        // Add files to Company Home > Data Dictionary > Web Scripts > org > alfresco > sample.
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.navigateToFolderInRepository(drone,
                REPO + SLASH + DATA_DICTIONARY_FOLDER + SLASH + "Web Scripts" + SLASH + "org" + SLASH + "alfresco" + SLASH + "sample");
        ShareUserRepositoryPage.uploadFileInRepository(drone, fileName1);
        ShareUserRepositoryPage.uploadFileInRepository(drone, fileName2);
        ShareUserRepositoryPage.uploadFileInRepository(drone, fileName3);
        ShareUserRepositoryPage.uploadFileInRepository(drone, fileName4);
        ShareUserRepositoryPage.uploadFileInRepository(drone, fileName5);

        // Reload scripts.
        WebScriptsPage webScriptsPage = ShareUtil.navigateToWebScriptsHome(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        WebScriptsMaintenancePage webScriptsMaintenancePage = webScriptsPage.clickRefresh();
        assertTrue(webScriptsMaintenancePage.isOpened());

        // Check work of 'formdata' webscript.
        drone.navigateTo(PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + "/alfresco/s/test/formdata");
        drone.findAndWait(By.xpath("*//input[1]")).sendKeys(DATA_FOLDER + "formdata" + SLASH + file1);
        drone.findAndWait(By.xpath("*//input[2]")).click();
        assertTrue(drone.findAndWait(By.xpath("//body"), maxWaitTime).getText().contains("Your file '" + file1 + "'") && drone
                .findAndWait(By.xpath("//body"), maxWaitTime).getText().contains("InputStreamContent@"), "Webscripts are broken in multi tenancy environment");
    }

    /**
     * Test: AONE-6593 Guest tenant. Log in
     */

    @Test(groups = "Enterprise42Only", timeOut = 400000)
    public void AONE_6593() throws Exception
    {

        String userName = "guest@";
        String password = "guest";

        // Creating new tenant.
        tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
        tenantConsolePage.render();
        assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);

        //Login to Alfresco as guest
        LoginAlfrescoPage loginPage = new LoginAlfrescoPage(drone);
        drone.navigateTo(loginPage.getAlfrescoURL(shareUrl));
        loginPage.render();
        MyAlfrescoPage alfrescoPage = loginPage.login(userName + tenantDomain1, password);
        alfrescoPage.render();
        assertTrue(drone.getTitle().contains("My Alfresco"));
    }


    /**
     * Test: AONE-6610: Webscript call api/invite/{inviteId}/{InvitTicket} obtained using tenant user
     */

    @Test(groups = "DataPrepMultiTenancy")
    public void dataPrep_AONE_6610() throws Exception
    {

        MailUtil.configOutBoundEmail();
    }

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6610() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser1 = MailUtil.BOT_MAIL_1;
        String regex = "[=&]+";

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Create any tenant
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));
        }
        else
        {
            // Creating new tenant.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD);
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");
        userTenantDomain1 = getUserNameForDomain("", tenantDomain1);

        // Login to Share as Admin tenant.
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        assertTrue(resultPage.isLoggedIn());
        assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // Creating new user.
        userSearchPage = resultPage.getNav().getUsersPage().render();
        newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.createEnterpriseUser(userTenantDomain1, userTenantDomain1, userTenantDomain1, testUser1, DEFAULT_PASSWORD);
        userSearchPage.searchFor(userTenantDomain1).render();
        assertTrue(userSearchPage.hasResults());
        ShareUser.logout(drone);

        // Creating a site and invite the user to it.
        resultPage = ShareUser.login(drone, adminTenantDomain1, DEFAULT_PASSWORD).render();
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        inviteMembersPage = siteDashboardPage.getSiteNav().selectInvite();
        List<String> foundUsers = inviteMembersPage.searchUser(userTenantDomain1);
        inviteMembersPage.clickAddUser(userTenantDomain1).render();
        inviteMembersPage.selectRoleForAll(CONTRIBUTOR);
        assertTrue(inviteMembersPage.isInviteButtonEnabled(), "Invite button is disabled.");
        inviteMembersPage.clickInviteButton();
        String email = MailUtil.getMailAsString(testUser1, String.format("Alfresco Share: You have been invited to join the %s site", siteName));
        parseInvitationMail(email);

        //Get {inviteId} and {InviteTicket}from invitation mail and perform the call anonymously on the site
        String[] parts = invitationUrlInEmail.split(regex);
        String reqURL =
                PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + "/alfresco/s/api/invite/" + parts[1] + "/" + parts[7].substring(0, 36)
                        + "?inviteeUserName=" + parts[3];
        String[] headers = AlfrescoHttpClient.getRequestHeaders(null);
        HttpGet request = AlfrescoHttpClient.generateGetRequest(reqURL, headers);
        HttpClient client = AlfrescoHttpClient.getHttpClientWithBasicAuth(reqURL, "", "");
        HttpResponse response = AlfrescoHttpClient.executeRequestHttpResp(client, request);
        assertEquals(response.getStatusLine().getStatusCode(), 200, "Failed to call an anonymous request");
    }

    /**
     * Test: AONE-6603:FTP tenant-clients
     */

    @Test(groups = "DataPrepMultiTenancy")
    public void dataPrep_AONE_6603() throws Exception
    {

        FtpUtil.configFtpPort();
    }

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6603() throws Exception
    {

        String testName = getTestName();
        String path = "Alfresco" + "/";
        String fileName = getFileName(testName) + "_1";
        File file = newFile(fileName, fileName);
        String folderName = getFolderName(testName) + "_1";

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Creating two tenants.
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            tenantConsole.createTenant(tenantDomain2, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain2));
        }

        else
        {
            // Creating two tenants.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);

            AlfrescoUtil.createTenant(drone, tenantDomain2, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain2), true, "Tenant already exists: " + tenantDomain2);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");
        adminTenantDomain2 = getUserNameForDomain("admin", tenantDomain2).replace("user", "");

        // Create new content by first tenant user using FTP;
        assertTrue(FtpUtil.uploadContent(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, file, path), "Cann't create " + fileName + " content");
        assertTrue(FtpUtil.isObjectExists(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, fileName, path), fileName + " content is not exist.");

        // Second tenant user isn't able to see created content using FTP;
        assertFalse(FtpUtil.isObjectExists(shareUrl, adminTenantDomain2, DEFAULT_PASSWORD, fileName, path),
                fileName + " content is exist, but should be not.");

        // Create new folder by tenant user using FTP;
        assertTrue(FtpUtil.createSpace(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, folderName, path), "Cann't create " + folderName + " folder");
        assertTrue(FtpUtil.isObjectExists(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, folderName, path), folderName + " folder is not exist.");

        // Second tenant user isn't able to see created folder using FTP;
        assertFalse(FtpUtil.isObjectExists(shareUrl, adminTenantDomain2, DEFAULT_PASSWORD, folderName, path),
                folderName + " folder is exist, but should be not.");

        // Check possible editing of any content by tenant user using FTP;
        assertTrue(FtpUtil.editContent(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, fileName, path));
        assertTrue(FtpUtil.getContent(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, fileName, path).equals(adminTenantDomain1));

        // Check possible deleting any content by tenant user using FTP;
        assertTrue(FtpUtil.deleteContentItem(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, fileName, path));

        // Check possible deleting any folder by tenant user using FTP;
        assertTrue(FtpUtil.deleteFolder(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, folderName, path));

    }

    /**
     * Test: AONE-6601:WebDav tenant-clients
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6601() throws Exception
    {
        String testName = getTestName();
        String path = "alfresco/webdav/";
        String fileName = getFileName(testName) + "_1";
        File file = newFile(fileName, fileName);
        String folderName = getFolderName(testName) + "_1";

        if (alfrescoVersion.getVersion() >= 5.0)
        {
            // Open Tenant Administration Console
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

            // Creating two tenants.
            tenantConsole.createTenant(tenantDomain1, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain1));

            tenantConsole.createTenant(tenantDomain2, DEFAULT_PASSWORD);
            tenantConsole.render();
            assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain2));
        }

        else
        {
            // Creating two tenants.
            tenantConsolePage = AlfrescoUtil.tenantAdminLogin(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            AlfrescoUtil.createTenant(drone, tenantDomain1, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain1), true, "Tenant already exists: " + tenantDomain1);

            AlfrescoUtil.createTenant(drone, tenantDomain2, DEFAULT_PASSWORD);
            tenantConsolePage.render();
            assertEquals(tenantConsolePage.findText().contains("created tenant: " + tenantDomain2), true, "Tenant already exists: " + tenantDomain2);
        }

        adminTenantDomain1 = getUserNameForDomain("admin", tenantDomain1).replace("user", "");
        adminTenantDomain2 = getUserNameForDomain("admin", tenantDomain2).replace("user", "");

        // Create new content by first tenant user using WebDav;
        assertTrue(WebDavUtil.uploadContent(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, file, path),
                "Cann't create " + fileName + " content");
        assertTrue(WebDavUtil.isObjectExists(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, fileName, path),
                fileName + " content is not exist.");

        // Second tenant user isn't able to see created content using WebDav;
        assertFalse(WebDavUtil.isObjectExists(shareUrl, adminTenantDomain2, DEFAULT_PASSWORD, fileName, path),
                fileName + " content is exist, but should be not.");

        // Create new folder by tenant user using WebDav;
        assertTrue(WebDavUtil.createFolder(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, folderName, path),
                "Cann't create " + folderName + " folder");
        assertTrue(WebDavUtil.isObjectExists(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, fileName, path),
                folderName + " folder is not exist.");

        // Second tenant user isn't able to see created folder using WebDav;
        assertFalse(WebDavUtil.isObjectExists(shareUrl, adminTenantDomain2, DEFAULT_PASSWORD, folderName, path),
                folderName + " folder is exist, but should be not.");

        // Check possible editing of any content by tenant user using WebDav;
        assertTrue(WebDavUtil.editContent(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, fileName, path));
        assertTrue(WebDavUtil.getContent(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, fileName, path).equals(adminTenantDomain1));

        // Check possible deleting any content by tenant user using WebDav;
        assertTrue(WebDavUtil.deleteItem(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, fileName, path));

        // Check possible deleting any folder by tenant user using WebDav;
        assertTrue(WebDavUtil.deleteItem(shareUrl, adminTenantDomain1, DEFAULT_PASSWORD, folderName, path));

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

    private void parseInvitationMail(String email) throws IOException, SAXException
    {
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(email)));
        DOMReader reader = new DOMReader();
        Document document = reader.read(parser.getDocument());
        try
        {
            invitationUrlInEmail = document.selectSingleNode(INVITATION_URL_IN_EMAIL).getText();
        }
        catch (NullPointerException e)
        {
            System.out.println("Bad email: ");
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(System.out, format);
            writer.write(document);
            fail("Can.t parse email about inviting.");
        }
    }

}
