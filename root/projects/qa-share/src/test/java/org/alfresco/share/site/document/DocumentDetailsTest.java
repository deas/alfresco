package org.alfresco.share.site.document;

import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class DocumentDetailsTest extends AbstractUtils
{

        private static Log logger = LogFactory.getLog(DocumentDetailsTest.class);

        @Override
        @BeforeClass(alwaysRun = true)
        public void setup() throws Exception
        {
                super.setup();
                logger.info("Start Tests in: " + testName);
        }

        @Test(groups = "DataPrepDocumentLibrary")
        public void dataPrep_Enterprise40x_8573() throws Exception
        {
                String testName = getTestName();
                String siteName = getSiteName(testName);

                // User
                String testUser = getUserNameFreeDomain(testName);
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

                // Login
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);
        }

        /**
         * Uploading .csv file into docLib.
         *
         * @throws Exception
         */
        @Test(groups = "Enterprise4.2")
        public void Enterprise40x_8573() throws Exception
        {
                testName = getTestName();

                /** Test Data Setup */
                String siteName = getSiteName(testName);
                String testUser = getUserNameFreeDomain(testName);
                String fileCSV = "test-csv.csv";
                // Login
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(drone, siteName);
                // Uploading .csv file.
                String[] fileInfo = { fileCSV };
                DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

                assertTrue(documentLibraryPage.isFileVisible(fileCSV), String.format("File %s didn't uploaded", fileCSV));

                DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileCSV);
                assertTrue(documentDetailsPage.isFlashPreviewDisplayed(), "Preview for file didn't displayed.");
                assertEquals(documentDetailsPage.getDocumentSize(), "45 bytes", "File has the wrong size");
        }

        @AfterMethod(alwaysRun = true)
        public void logout()
        {
                ShareUser.logout(drone);
                logger.info("User logged out - drone.");
        }

}
