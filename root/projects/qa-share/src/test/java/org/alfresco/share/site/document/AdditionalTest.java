package org.alfresco.share.site.document;

import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;


/**
 * @author Roman.Chul
 */

@Listeners(FailedTestListener.class)
public class AdditionalTest extends AbstractUtils {

    private static Log logger = LogFactory.getLog(DocumentDetailsActionsTest.class);
    protected String testUser;
    protected String siteName = "";
    private static String DOC_FILE_LARGE_SIZE = "Test_8545.txt";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_4037() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_4037() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = getFileName(testName) + ".txt";

        DocumentLibraryPage documentLibraryPage;

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            //Open document details page;
            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
            double currentVersion = Double.parseDouble(detailsPage.getDocumentVersion());
            //Upload new version for the document;
            File file = new File(DATA_FOLDER + fileName);
            UpdateFilePage updatePage = detailsPage.selectUploadNewVersion().render();
            updatePage.render();
            updatePage.selectMinorVersionChange();
            updatePage.uploadFile(file.getCanonicalPath());
            updatePage.setComment("upload new version");
            detailsPage = updatePage.submit().render();
            // verify that version of document is increased
            detailsPage = drone.getCurrentPage().render();
            double actualVersion = Double.parseDouble(detailsPage.getDocumentVersion());
            Assert.assertNotEquals(currentVersion, actualVersion);
            // Click "Edit Offline";
            DocumentEditOfflinePage editOfflinePage = detailsPage.selectEditOffLine(null).render();
            Assert.assertEquals(editOfflinePage.getContentInfo(), "This document is locked by you for offline editing.");
            Assert.assertTrue(editOfflinePage.isCheckedOut());
            drone.refresh();
            //Upload new version for the document;
            currentVersion = Double.parseDouble(detailsPage.getDocumentVersion());
            file = new File(DATA_FOLDER + fileName);
            updatePage = detailsPage.selectUploadNewVersion().render();
            updatePage.render();
            updatePage.selectMajorVersionChange();
            updatePage.uploadFile(file.getCanonicalPath());
            updatePage.setComment("upload new version");
            detailsPage = updatePage.submit().render();

            // verify that version of document is increased
            Assert.assertFalse(detailsPage.isCheckedOut());
            actualVersion = Double.parseDouble(detailsPage.getDocumentVersion());
            Assert.assertNotEquals(currentVersion, actualVersion);

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_8505() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

    }

    @Test(groups = "EnterpriseOnly")
    public void Enterprise40x_8505() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileName = DOC_FILE_LARGE_SIZE;
        String randomString = getRandomString(5);

        DocumentLibraryPage documentLibraryPage;

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            //Uploading a large test document that can be previewed (e.g. 7MB word doc)
            String[] fileInfo = {fileName};
            documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo).render();
            FileDirectoryInfo info = documentLibraryPage.getFileDirectoryInfo(fileName);
            //Before the preview/thumbnails are generated, attempt to edit metadata of the uploaded item. Change the title and add a tag. Click "Submit".
            EditDocumentPropertiesPage editDocumentPropertiesPage = info.selectEditProperties().render();
            editDocumentPropertiesPage.setDocumentTitle(randomString);
            TagPage tagPage = editDocumentPropertiesPage.getTag().render();
            tagPage = tagPage.enterTagValue(randomString).render();
            tagPage.clickOkButton();
            documentLibraryPage = editDocumentPropertiesPage.selectSave().render();
            //Verify changes
            documentLibraryPage = documentLibraryPage.getSiteNav().selectSiteDocumentLibrary().render();
            info = documentLibraryPage.getFileDirectoryInfo(fileName);
            Assert.assertEquals(info.getTitle(), String.format("(%s)", randomString));
            Assert.assertTrue(info.getTags().contains(randomString));

    }
    @AfterMethod(alwaysRun = true)
    public void logout()
    {
        ShareUser.logout(drone);
        logger.info("User logged out - drone.");
    }
}
