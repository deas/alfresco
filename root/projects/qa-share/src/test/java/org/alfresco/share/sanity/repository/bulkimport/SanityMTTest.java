package org.alfresco.share.sanity.repository.bulkimport;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.systemsummary.TenantConsole;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Olga Antonik
 */
@Listeners(FailedTestListener.class)
public class SanityMTTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SanityMTTest.class);
    String tenantDomain;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);

    }

    @BeforeMethod
    public void set()
    {
        tenantDomain = getRandomString(5);
        ShareUser.deleteSiteCookies(drone, shareUrl);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_8082() throws Exception
    {

        // log into Alfresco Explorer as admin
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        TenantConsole tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
        assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

        // run create <tenant domain> <tenant admin password> command
        tenantConsole.createTenant(tenantDomain, ADMIN_PASSWORD);
        tenantConsole.render();
        assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain));

        // log in Alfresco Share as admin@tenantA
        SharePage page = ShareUser.login(drone, ADMIN_USERNAME + "@" + tenantDomain, ADMIN_PASSWORD);
        assertTrue(page.getTitle().contains("User Dashboard"));

       ShareUser.logout(drone);

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_8084() throws Exception
    {
        String tenantDomain2 = getRandomString(5);
        String tenantAdmin1 = ADMIN_USERNAME + "@" + tenantDomain;
        String tenantAdmin2 = ADMIN_USERNAME + "@" + tenantDomain2;
        String testUser = getRandomString(5);
        String tenantUser = testUser + "@" + tenantDomain;
        String siteName = getSiteName(testName);
        String testContent = getFileName(testName);

        // Preconditions:
        // at least 2 tenant users created: tenantA and tenantB
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        TenantConsole tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
        assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

        tenantConsole.createTenant(tenantDomain, DEFAULT_PASSWORD);
        tenantConsole.render();
        assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain));

        tenantConsole.createTenant(tenantDomain2, DEFAULT_PASSWORD);
        tenantConsole.render();
        assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain2));

        // log in Alfresco Share as admin@tenantA
        SharePage page = ShareUser.login(drone, tenantAdmin1, DEFAULT_PASSWORD);
        assertTrue(page.getTitle().contains("User Dashboard"));

        // go to Admin Console and create any user (TestUser)
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, tenantAdmin1, testUserInfo);
        page = ShareUser.login(drone, tenantUser, DEFAULT_PASSWORD);
        assertTrue(page.getTitle().contains("User Dashboard"));

        // create any TestSite and upload TestDoc to the site
        ShareUser.login(drone, tenantAdmin1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ContentDetails content = new ContentDetails();
        content.setName(testContent);
        DocumentLibraryPage documentLibraryPage = ShareUser.createContent(drone, content, ContentType.PLAINTEXT);
        assertTrue(documentLibraryPage.isFileVisible(testContent));

        // invite the user(TestUser) to the site (TestSite)
        ShareUserMembers.inviteUserToSiteWithRole(drone, tenantAdmin1, tenantUser, siteName, UserRole.MANAGER);

        // log in Alfresco Share as admin@tenantA
        page = ShareUser.login(drone, tenantAdmin1, DEFAULT_PASSWORD);
        assertTrue(page.getTitle().contains("User Dashboard"));

        // log in Alfresco Share as admin@tenantB
        DashBoardPage dashBoardPage = ShareUser.login(drone, tenantAdmin2, DEFAULT_PASSWORD).render();
        assertTrue(dashBoardPage.getTitle().contains("User Dashboard"));

        // go to Admin Console and search for created by tenantA user
        UserSearchPage userSearchPage = dashBoardPage.getNav().getUsersPage().render();
        userSearchPage.searchFor(testUser).render();
        assertFalse(userSearchPage.hasResults());

        // search for TestSite using site finder
        SiteFinderPage siteFinderPage = page.getNav().selectSearchForSites().render();
        siteFinderPage.searchForSite(siteName).render();
        assertFalse(siteFinderPage.hasResults());

        // search for TestDoc
        //FacetedSearchPage facetedSearchPage = ShareUser.openUserDashboard(drone).getNav().performSearch(testContent);
        ShareUserSearchPage.basicSearch(drone, testContent, false);
        Boolean searchOk = ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, BASIC_SEARCH, testContent, testContent, true);
        assertFalse(searchOk);

        ShareUser.logout(drone);

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_8085() throws Exception
    {
        String tenantAdmin = ADMIN_USERNAME + "@" + tenantDomain;
        String path = "alfresco/webdav/";
        String pathFTP = "Alfresco/";

        // at least one tenant user is created
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        TenantConsole tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
        assertTrue(tenantConsole.getTitle().contains("Tenant Admin Console"));

        tenantConsole.createTenant(tenantDomain, DEFAULT_PASSWORD);
        tenantConsole.render();
        assertTrue(tenantConsole.findText().contains("created tenant: " + tenantDomain));

        ArrayList<String> items = WebDavUtil.getObjects(shareUrl, tenantAdmin, DEFAULT_PASSWORD, path);

        // try to connect to Alfresco through WebDAV protocol by tenant user
        assertTrue(items.contains("/" + path + "Sites/"));
        assertTrue(items.contains("/" + path + "User%20Homes/"));
        assertTrue(items.contains("/" + path + "Guest%20Home/"));
        assertTrue(items.contains("/" + path + "Data%20Dictionary/"));

        // try to connect to Alfresco through FTP protocol by tenant user
        assertTrue(FtpUtil.isObjectExists(shareUrl, tenantAdmin, DEFAULT_PASSWORD, "Sites", pathFTP));
        assertTrue(FtpUtil.isObjectExists(shareUrl, tenantAdmin, DEFAULT_PASSWORD, "User Homes", pathFTP));
        assertTrue(FtpUtil.isObjectExists(shareUrl, tenantAdmin, DEFAULT_PASSWORD, "Guest Home", pathFTP));
        assertTrue(FtpUtil.isObjectExists(shareUrl, tenantAdmin, DEFAULT_PASSWORD, "Data Dictionary", pathFTP));

    }

}
