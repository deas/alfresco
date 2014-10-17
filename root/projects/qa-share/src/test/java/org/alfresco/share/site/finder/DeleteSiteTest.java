package org.alfresco.share.site.finder;

import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.share.admin.SiteAdminGroupTests;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Verify possibility to Delete a site from Site Finder Page
 * 
 * @author Bogdan.Bocancea
 */

@Listeners(FailedTestListener.class)
public class DeleteSiteTest extends AbstractUtils
{

    private static final Logger logger = Logger.getLogger(SiteAdminGroupTests.class);
    public DashBoardPage dashBoard;
    private static SiteFinderPage siteFinder;
    private String user1, user2;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_5156() throws Exception
    {
        String testName = getTestName();
        user1 = getUserNameFreeDomain(testName + "1");

        // Create User1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        user2 = getUserNameFreeDomain(testName + "2");

        // Create User2
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);
    }

    @Test(groups = "DeleteSiteGroup")
    public void Enterprise40x_5156() throws Exception
    {
        testName = getTestName();
        String siteName1 = getSiteName(testName) + System.currentTimeMillis() + "1";
        String siteName2 = getSiteName(testName) + System.currentTimeMillis() + "2";
        
        user1 = getUserNameFreeDomain(testName + "1");
        user2 = getUserNameFreeDomain(testName + "2");
        
        // login with user1 and create a site
        ShareUser.login(drone, user1);
        ShareUser.createSite(drone, siteName1, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName1, UserRole.CONSUMER);
        ShareUser.logout(drone);

        // login user2 and create site
        ShareUser.login(drone, user2);
        ShareUser.createSite(drone, siteName2, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();
        ShareUserMembers.inviteUserToSiteWithRole(drone, user2, user1, siteName2, UserRole.MANAGER);
        ShareUser.logout(drone);

        // login with user1
        ShareUser.login(drone, user1);
        ShareUser.openSiteDashboard(drone, siteName1);

        // Navigate to Search For Site
        // Perform a search for Site1;
        SharePage page = drone.getCurrentPage().render();
        siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteName1);

        List<String> sitesFound = siteFinder.getSiteList();

        // Click "Delete" button for Site1 -> Cancel Button
        org.alfresco.share.util.SiteUtil.deleteSiteWithConfirm(drone, siteName1, false, false);

        Assert.assertTrue(sitesFound.contains(siteName1));

        // click delete -> delete -> No
        org.alfresco.share.util.SiteUtil.deleteSiteWithConfirm(drone, siteName1, true, false);
        Assert.assertTrue(sitesFound.contains(siteName1));

        // click delete-> delete -> Yes
        org.alfresco.share.util.SiteUtil.deleteSiteWithConfirm(drone, siteName1, true, true);

        // search for the site
        page = drone.getCurrentPage().render();
        siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName1).render();
        sitesFound = siteFinder.getSiteList();
        Assert.assertFalse(sitesFound.contains(siteName1));

        // Navigate to Search For Site
        // Perform a search for Site2;
        page = drone.getCurrentPage().render();
        siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = SiteUtil.siteSearchRetry(drone, siteFinder, siteName2);
        sitesFound = siteFinder.getSiteList();

        // Click "Delete" button for Site2; -> Cancel Button
        org.alfresco.share.util.SiteUtil.deleteSiteWithConfirm(drone, siteName2, false, false);
        Assert.assertTrue(sitesFound.contains(siteName2));

        org.alfresco.share.util.SiteUtil.deleteSiteWithConfirm(drone, siteName2, true, false);
        Assert.assertTrue(sitesFound.contains(siteName2));

        org.alfresco.share.util.SiteUtil.deleteSiteWithConfirm(drone, siteName2, true, true);

        page = drone.getCurrentPage().render();

        siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName2).render();
        sitesFound = siteFinder.getSiteList();
        Assert.assertFalse(sitesFound.contains(siteName2));

    }
}
