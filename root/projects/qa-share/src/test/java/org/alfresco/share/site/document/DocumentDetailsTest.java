package org.alfresco.share.site.document;

import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
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

                Assert.assertTrue(documentLibraryPage.isFileVisible(fileCSV), String.format("File %s didn't uploaded", fileCSV));

                DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(fileCSV);
                Assert.assertTrue(documentDetailsPage.isFlashPreviewDisplayed(), "Preview for file didn't displayed.");
                Assert.assertEquals(documentDetailsPage.getDocumentSize(), "45 bytes", "File has the wrong size");
        }

        /**
         * 1) Creates test user 
         * 2) Test user logs in
         * 3) Test user creates site
         * 4) Test user uploads file to site's document library
         * 5) Test user logs out
         * 
         * @throws Exception
         */
        @Test(groups = "DataPrepAlfrescoOne")
        public void dataPrep_ALF_3160() throws Exception
        {
            String testName = getTestName();
            String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
            String[] testUserInfo = new String[] { testUser };
            String siteName = getSiteName(testName);

            // Create test user
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

            // Login as created user
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Create site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
            
            //Upload a file in site's document library
            String fileName = getFileName(testName);
            String[] fileInfo = { fileName };
            ShareUser.uploadFileInFolder(drone, fileInfo);
           
        }
        
        /**
         * 1) Test user logs in, opens site document library
         * 2) Clicks on file's name
         * 3) Verfies Share pane is present on the right handside of document details page for enterprise
         * 4) Verfies Share pane is not present on the right handside of document details page for cloud  
         */
        @Test(groups = "AlfrescoOne")
        public void ALF_3160()
        {
            // test user (site creator) logs in
            String testName = getTestName();
            String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
            String siteName = getSiteName(testName);
            String fileName = getFileName(testName);
            
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName);
            
            if (!isAlfrescoVersionCloud(drone))
            {
                Assert.assertTrue(detailsPage.isSharePanePresent());
            }
            else
            {
                Assert.assertFalse(detailsPage.isSharePanePresent());
            }
  
        }    
        
        
        @AfterMethod(alwaysRun = true)
        public void logout()
        {
                ShareUser.logout(drone);
                logger.info("User logged out - drone.");
        }

}
