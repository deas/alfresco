package org.alfresco.share.util;

import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UtilsTest extends AbstractTests
{
    private static Log logger = LogFactory.getLog(UtilsTest.class);

    protected String testUser;
    protected String testUserPass = DEFAULT_PASSWORD;

    protected String testDomain = "utils";

    /**
     * Class includes: Tests for utilities written for qa-share: Not to be executed in a test run for Ent / Cloud
     * <ul>
     * </ul>
     */
    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = getDomainName(testDomain);
        testUser = testName + "@" + testDomain;
        logger.info("Starting Tests: " + testName);
    }

    @Test
    public void testDeleteSite_100()
    {
        try
        {
            String testName = getTestName();
            String testUser = getUserNameForDomain(testName, testDomain) + System.currentTimeMillis();
            String[] testUserInfo = new String[] { testUser };

            String siteName = getSiteName(testName) + System.currentTimeMillis();
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testUserPass);

            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.createSite(drone, siteName + "M", SITE_VISIBILITY_MODERATED);
            ShareUser.createSite(drone, siteName + "PR", SITE_VISIBILITY_PRIVATE);

            String siteurl = getSiteShortname(siteName);
            SiteDashboardPage sitedash = SiteUtil.openSiteURL(drone, siteurl);
            Assert.assertTrue(sitedash.isSite(siteName));

            DocumentLibraryPage doclib = SiteUtil.openSiteDocumentLibraryURL(drone, siteurl);
            Assert.assertTrue(doclib.isDocumentLibrary());

            SiteUtil.deleteSitesAsUser(drone, testUser, new String[] { siteName, siteName + "M", siteName + "PR" });

            ShareUser.createSite(drone, siteName + "PR", SITE_VISIBILITY_PRIVATE);
            ShareUser.createSite(drone, siteName + "M", SITE_VISIBILITY_MODERATED);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

            SiteUtil.deleteSites(drone, siteName);

        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    @Test()
    public void navigateFoldersTest_101() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        String folderName1 = getFolderName(testName) + "test1";
        String folderName2 = getFolderName(testName) + "test2";

        String fileName = getFileName(siteName) + "_file1";
        String fileInfo[];

        // User
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // Creating folder
        ShareUserSitePage.createFolder(drone, folderName1, null);

        ShareUserSitePage.navigateToFolder(drone, DOCLIB);
        ShareUserSitePage.navigateToFolder(drone, DOCLIB + SLASH + folderName1);

        ShareUserSitePage.createFolder(drone, folderName2, null);
        ShareUserSitePage.navigateToFolder(drone, folderName1 + SLASH + folderName2);

        // Uploading files in the folder tree.
        fileInfo = new String[] { fileName, DOCLIB + SLASH + folderName1 + SLASH + folderName2 };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        fileInfo = new String[] { fileName, DOCLIB + SLASH + folderName1 };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        fileInfo = new String[] { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Repository Test
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUserRepositoryPage.openRepositorySimpleView(drone);

            ShareUserSitePage.createFolder(drone, folderName1, null);

            ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO);
            ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName1);

            ShareUserSitePage.createFolder(drone, folderName2, null);
            ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName1 + SLASH + folderName2);

            // Uploading files in the folder tree.
            fileInfo = new String[] { fileName, REPO + SLASH + folderName1 + SLASH + folderName2 };
            ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo);

            fileInfo = new String[] { fileName, REPO + SLASH + folderName1 };
            ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo);

            fileInfo = new String[] { fileName, REPO };
            ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo);
        }
    }
}
