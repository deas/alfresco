package org.alfresco.share.cloud.documentlibrary;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.ShareLinkPage;
import org.alfresco.po.share.site.document.ViewPublicLinkPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
@Test(groups = { "CloudOnly" })
public class QuickShareTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(QuickShareTests.class);

    private String testUser;

    private String siteName = "";
    private String fileName_12437;
    private String fileName_12438;

    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName() + 10;
        testUser = getUserNameFreeDomain(testName);
        siteName = getSiteName(testName);
        fileName_12437 = getFileName(testName) + "12437.txt";
        fileName_12438 = getFileName(testName) + "12438.txt";

        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = { "DataPrepDocumentLibrary", "CloudOnly" })
    public void dataPrep_AONE() throws Exception
    {
        // Create 2 normal Users
        String[] testUserNew1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserNew1);

        // login with user
        ShareUser.login(drone, testUser);

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // create a file with user1
        ContentDetails contentDetails1 = new ContentDetails();
        contentDetails1.setName(fileName_12437);
        contentDetails1.setContent("file content for test 12437");
        ShareUser.createContent(drone, contentDetails1, ContentType.PLAINTEXT);

        // create a file with user1
        ContentDetails contentDetails2 = new ContentDetails();
        contentDetails2.setName(fileName_12438);
        contentDetails2.setContent("file content for test 12438");
        ShareUser.createContent(drone, contentDetails2, ContentType.PLAINTEXT);

        ShareUser.logout(drone);

    }

    @Test(groups = { "CloudOnly" })
    public void AONE_12437() throws Exception
    {
        // Login with user
        ShareUser.login(drone, testUser);
      
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();

        // Step 1. Share a document using quick share
        DocumentDetailsPage docDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName_12437).render();
        ShareLinkPage shareLinkPage = (ShareLinkPage) docDetailsPage.clickShareLink().render();
        // The document is shared
        String shareUrl = shareLinkPage.getShareURL();
        Assert.assertTrue(shareLinkPage.isUnShareLinkPresent(), "The document " + fileName_12437 + " is not shared.");

        // Step 2. Upload a new version for the document
        String newFileName = getFileName(testName) + "12437_new18.txt";

        docDetailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileName, "comment", false);
        // New version is uploaded
        docDetailsPage.render();
        Assert.assertEquals(docDetailsPage.getDocumentVersion(), "1.1");
        Assert.assertEquals(docDetailsPage.getDocumentBody(), "New File being created via newFile:" + newFileName);

        // Step 3. Follow the Quick Share URL again
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);
        viewPublicLinkPage.render();
        // The new version of the document should be shown
        Assert.assertEquals(viewPublicLinkPage.getDocumentBody(), "New File being created via newFile:" + newFileName);
        docDetailsPage = viewPublicLinkPage.clickOnDocumentDetailsButton().render();
        Assert.assertEquals(docDetailsPage.getDocumentVersion(), "1.1");

    }
    
    @Test(groups = { "DataPrepDocumentLibrary", "CloudOnly" })
    public void AONE_12438() throws Exception
    {
        // Login with user
        ShareUser.login(drone, testUser);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        // Step 1. Generate a QuickShare URL for a document
        DocumentDetailsPage docDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName_12438).render();
        ShareLinkPage shareLinkPage = (ShareLinkPage) docDetailsPage.clickShareLink().render();
        // The URL is generated successfully
        String shareUrl = shareLinkPage.getShareURL();
        Assert.assertTrue(shareLinkPage.isUnShareLinkPresent(), "The document " + fileName_12438 + " is not shared.");

        // Step 2. Follow the URL in a browser window
        drone.createNewTab();
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);
        // You should be brought to the document preview page.
        viewPublicLinkPage.render();
        // Step3. Check that the document displayed in the preview page is the one shared
        // The displayed document is the one that was initially shared
        Assert.assertEquals(viewPublicLinkPage.getDocumentBody(), "file content for test 12438");

    }

    @Test(groups = { "DataPrepDocumentLibrary", "CloudOnly" })
    public void dataPrep_12427() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();
        ShareUser.openSiteDashboard(drone, siteName).render();

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo).render();

    }

    @Test(groups = "CloudOnly")
    public void AONE_12427() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Get Share Link document
        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        ShareLinkPage shareLinkPage = ShareUserSitePage.getFileDirectoryInfo(drone, fileName).clickShareLink().render();

        String shareUrl = shareLinkPage.getShareURL();

        // create new tab
        drone.createNewTab();

        // navigate to the shared link
        drone.navigateTo(shareUrl);
        ViewPublicLinkPage viewPublicLinkPage = new ViewPublicLinkPage(drone);

        // ---- Step 1 -----
        // --- Step action ---
        // Check that the document is correctly displayed in the preview controller
        // ---- Expected results ----
        // The document is correctly displayed.
        assertEquals(viewPublicLinkPage.getContentTitle(), fileName);
        assertTrue(viewPublicLinkPage.isDocumentViewDisplayed());

        // get the zoom scale
        int standardZoom = viewPublicLinkPage.getIntegerZoomScale();

        // ---- Step 2 -----
        // --- Step action ---
        // Click on the zoom in/out ('+','-') buttons.
        // ---- Expected results ----
        // The document is zoomed in/out.
        viewPublicLinkPage.clickZoomIn();

        int firstZoom = viewPublicLinkPage.getIntegerZoomScale();
        Assert.assertTrue(firstZoom > standardZoom);

        // zoom in
        viewPublicLinkPage.clickZoomIn();
 
        int secondZoom = viewPublicLinkPage.getIntegerZoomScale();
        Assert.assertTrue(secondZoom > firstZoom);

        // zoom out
        viewPublicLinkPage.clickZoomOut();
        int zoomOut = viewPublicLinkPage.getIntegerZoomScale();
        Assert.assertTrue(zoomOut < secondZoom);
        
        // ---- Step 3 -----
        // --- Step action ---
        // Scroll the document (if applicable) using the scroll bar.
        // ---- Expected results ----
        //The document is scrolled
        
        // - Not applicable

    }
}
