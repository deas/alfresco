package org.alfresco.share;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.dashlet.SiteContentFilter;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SiteDashBoardTest extends AbstractTests
{
    private static Log logger = LogFactory.getLog(SiteDashBoardTest.class);

    protected String testUser;

    protected String siteName = "";

    /**
     * Class includes: Tests from TestLink in Area: Site DashBoard Tests
     * <ul>
     * <li>Perform an Activity on Site</li>
     * <li>Site DashBoard shows Activity Feed</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    // SiteDashBoard Tests
    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_575() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: Private</li>
     * <li>Upload File</li>
     * <li>Open User Dash-board</li>
     * <li>Check that the User Dash-board > My Documents Dashlet shows the new file</li>
     * </ul>
     */
    @Test
    public void cloud_575()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String testUser = getUserNameFreeDomain(testName);
            String siteName = getSiteName(testName);
            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";

            String[] fileInfo = { fileName, DOCLIB };

            String activityEntry = "";

            /** Test Steps */
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open Site DashBoard
            ShareUser.openSiteDashboard(drone, siteName);

            // uploadFile
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // openUserDashboard(drone);
            ShareUser.openUserDashboard(drone);

            // addDashlet(drone, myDocuments);

            // Check Activity Feed: Add Document
            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_ADDED + FEED_FOR_FILE + fileName + FEED_LOCATION + siteName;

            Boolean entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);

            Assert.assertTrue(entryFound, "Activity Entry not found: " + activityEntry);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_578() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        // User
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site</li>
     * <li>Upload Document</li>
     * <li>Like Document, Check Like Count = 1</li>
     * <li>Open User Dash-board</li>
     * <li>Check that the User Dash-board > My Documents Dashlet shows the new file</li>
     * </ul>
     */
    @Test
    public void cloud_578()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String testUser = getUserNameFreeDomain(testName);
            String siteName = getSiteName(testName) + System.currentTimeMillis();
            String fileName = getFileName(testName) + "-" + System.currentTimeMillis() + "." + "txt";

            String[] fileInfo = { fileName };

            String activityEntry = "";

            /** Test Steps */
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            // Open Site DashBoard
            // ShareUser.openSiteDashboard(drone, siteName);

            // uploadFile
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // TODO: Un-quarantine test once https://issues.alfresco.com/jira/browse/WEBDRONE-614 is fixed
            // Check Like count is zero
            String likeCount = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).getLikeCount();
            Assert.assertEquals(likeCount, "0", "Test: 711: Fail: Incorrect Like Count. Expected 0 when no likes");

            // Like Document
            ShareUserSitePage.getFileDirectoryInfo(drone, fileName).selectLike();

            // Check Like Count
            likeCount = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).getLikeCount();
            Assert.assertEquals(likeCount, "1", "Test: 711: Fail: Incorrect Like Count. Expected 1, on Like Document");

            // openUserDashboard(drone);
            ShareUser.openUserDashboard(drone);

            // Check Activity Feed: Liked Document
            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_LIKED + FEED_FOR_FILE + fileName + FEED_LOCATION + siteName;

            Boolean entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);

            Assert.assertTrue(entryFound, "Activity Entry not found: " + activityEntry);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_SiteDashBoard_580() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        String siteName = getSiteName(testName);

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site</li>
     * <li>Create Folder</li>
     * <li>Open User Dash-board</li>
     * <li>Check that the User Dash-board > My Documents Dashlet shows the activity feed</li>
     * </ul>
     */
    @Test(groups = { "nonEnterprise" })
    public void cloud_580()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String testUser = getUserNameFreeDomain(testName);
            String siteName = getSiteName(testName);
            String folderName = getFolderName(testName) + "-" + System.currentTimeMillis();

            String activityEntry = "";

            /** Test Steps */
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open Site DashBoard
            ShareUser.openSiteDashboard(drone, siteName);

            // uploadFile
            ShareUserSitePage.createFolder(drone, folderName, folderName + "-Description");

            // openUserDashboard(drone);
            ShareUser.openUserDashboard(drone);

            // addDashlet(drone, myDocuments);

            // Check Activity Feed: Add Folder
            activityEntry = testUser + " " + DEFAULT_LASTNAME + FEED_CONTENT_ADDED + FEED_FOR_FOLDER + folderName + FEED_LOCATION + siteName;

            Boolean entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);

            Assert.assertTrue(entryFound, "Activity Entry not found: " + activityEntry);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_Enterprise40x_7932() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site: Private</li>
     * <li>Check that the created site contents dashlet > Search filters</li>
     * </ul>
     */
    @Test
    public void enterprise40x_7932()
    {
        try
        {
            /** Start Test */
            testName = getTestName();
            String testUser = getUserNameFreeDomain(testName);
            String siteName = getSiteName(testName);

            List<String> expectedValues = new ArrayList<String>();

            expectedValues.add("I've Recently Modified");
            expectedValues.add("I'm Editing");
            expectedValues.add("My Favorites");

            // TODO: Chiran: List does not match in the test case. Amend test case here or in TestLink if it's not appropriate.
            /* Note : These two filter options verification will be done as part of cloud sync tests. */
            /*
             * if(hybridEnabled)
             * {
             * expectedValues.add("Synced content");
             * expectedValues.add("Synced with Error");
             * }
             */

            String actualHelpBalloonMessage = "This dashlet makes it easy to keep track of your recent changes to library content in this site. Clicking the item name or thumbnail takes you to the details page so you can preview or work with the item.There are two views for this dashlet. The detailed view lets you:Mark an item as a favorite so it appears in Favorites lists for easy accessLike (and unlike) an itemJump to the item details page to leave a comment";

            /** Test Steps */
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open Site DashBoard
            SiteDashboardPage siteDashBoardPage = ShareUser.openSiteDashboard(drone, siteName);

            SiteContentDashlet siteContentDashlet = siteDashBoardPage.getDashlet(SITE_CONTENT_DASHLET).render();

            Assert.assertTrue(siteContentDashlet.isSimpleButtonDisplayed());
            Assert.assertTrue(siteContentDashlet.isDetailButtonDisplayed());

            siteContentDashlet.clickFilterButtton();

            List<SiteContentFilter> list = siteContentDashlet.getFilters();
            Assert.assertNotNull(list);

            List<String> actualValues = new ArrayList<String>();

            for (SiteContentFilter link : list)
            {
                actualValues.add(link.getDescription());
            }

            for (String expectedValue : expectedValues)
            {
                Assert.assertTrue(actualValues.contains(expectedValue));
            }

            siteContentDashlet.clickHelpButton();

            String msg = siteContentDashlet.getHelpBalloonMessage();

            Assert.assertNotNull(msg);
            Assert.assertEquals(actualHelpBalloonMessage, msg);

            siteContentDashlet.closeHelpBallon();
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    /**
     * Test:
     * <ul>
     * <li>Login as userA</li>
     * <li>Create Site</li>
     * <li>select added first document as Favorite</li>
     * <li>Check that the favorite document as listed in contents dashlet > My Favorites</li>
     * <li>Logout userA</li>
     * <li>Login as userB</li>
     * <li>Accept invitation to become as collaborator for the site request given by userA</li>
     * <li>select added second document as Favorite</li>
     * <li>Check that the favorite document as listed in contents dashlet > My Favorites</li>
     * </ul>
     */
    @Test
    public void enterprise40x_7942()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String testName = getTestName();
            String fileLikedByUser1 = getFileName(testName + "1");
            String fileLikedByUser2 = getFileName(testName + "2");
            String siteName = getSiteName(testName);
            String testUser1 = getUserNameFreeDomain(testName + "1");
            String testUser2 = getUserNameFreeDomain(testName + "2");

            // Login
            ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

            // Open Site DashBoard
            SiteDashboardPage siteDashBoardPage = ShareUser.openSiteDashboard(drone, siteName);

            SiteContentDashlet siteContentDashlet = siteDashBoardPage.getDashlet(SITE_CONTENT_DASHLET).render();

            siteDashBoardPage = siteContentDashlet.selectFilter(SiteContentFilter.MY_FAVOURITES);

            Boolean entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileLikedByUser1, true, siteName);

            siteContentDashlet = siteDashBoardPage.getDashlet(SITE_CONTENT_DASHLET).render();

            Assert.assertTrue(entryFound, "User 1's favourite entry is not found in Site Content Dashlet");
            Assert.assertTrue(siteContentDashlet.getSiteContents().size() == 1);

            ShareUser.logout(drone);

            // Login As userB cloud specific code
            ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

            // Open Site DashBoard
            siteDashBoardPage = ShareUser.openSiteDashboard(drone, siteName);

            siteContentDashlet = siteDashBoardPage.getDashlet(SITE_CONTENT_DASHLET).render();

            siteDashBoardPage = siteContentDashlet.selectFilter(SiteContentFilter.MY_FAVOURITES);

            entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileLikedByUser2, true, siteName);

            Assert.assertTrue(entryFound, "User 2's favourite entry is not found in Site Content Dashlet" + fileLikedByUser2);

            entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileLikedByUser1, false, siteName);
            Assert.assertTrue(entryFound, "User 1's favourite entry should not be found for User 2" + fileLikedByUser1);

            // TODO: Chiran: Can this be removed if not required by the test?
            siteContentDashlet = siteDashBoardPage.getDashlet(SITE_CONTENT_DASHLET).render();
            Assert.assertTrue(siteContentDashlet.getSiteContents().size() == 1);

            ShareUser.logout(drone);

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepSiteDashboard" })
    public void dataPrep_Enterprise40x_7942() throws Exception
    {
        String testName = getTestName();
        String fileLikedByUser1 = getFileName(testName + "1");
        String fileLikedByUser2 = getFileName(testName + "2");

        String siteName = getSiteName(testName);

        String testUser1 = getUserNameFreeDomain(testName + "1");
        String[] testUserInfo1 = new String[] { testUser1 };

        String testUser2 = getUserNameFreeDomain(testName + "2");
        String[] testUserInfo2 = new String[] { testUser2 };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

        // Inviting another user to join on site.
        // Login User1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

        String[] fileInfo1 = { fileLikedByUser1 };
        ShareUser.uploadFileInFolder(drone, fileInfo1).render();

        String[] fileInfo2 = { fileLikedByUser2 };
        DocumentLibraryPage libraryPage = ShareUser.uploadFileInFolder(drone, fileInfo2).render();

        // Open Site DashBoard
        libraryPage.getFileDirectoryInfo(fileLikedByUser1).selectFavourite();

        // Invite user to Site as Collaborator and log-out the current user.
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, UserRole.COLLABORATOR);
        ShareUser.logout(drone);

        // Login As user2
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // Open Site DashBoard
        ShareUser.openSiteDashboard(drone, siteName);
        libraryPage = ShareUser.openDocumentLibrary(drone);
        libraryPage.getFileDirectoryInfo(fileLikedByUser2).selectFavourite();
        libraryPage.render();
    }
}