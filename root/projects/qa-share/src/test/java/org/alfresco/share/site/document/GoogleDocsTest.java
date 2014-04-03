/**
 * 
 */
package org.alfresco.share.site.document;

import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.DestinationAndAssigneeBean;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditInGoogleDocsPage;
import org.alfresco.po.share.site.document.GoogleDocsUpdateFilePage;
import org.alfresco.share.util.AbstractCloudSyncTest;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ActivityType;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserGoogleDocs;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author cbairaajoni
 */
@Listeners(FailedTestListener.class)
public class GoogleDocsTest extends ShareUserGoogleDocs
{
    private static Log logger = LogFactory.getLog(GoogleDocsTest.class);

    protected String testUser;
    protected String siteName = "";

    private static final String TEST_TXT_FILE = "Test2.txt";
    private static final String TEST_DOC_FILE = "Test3.doc";
    private static final String TEST_JPG_FILE = "Test4.JPG";
    private static final String TEST_PDF_FILE = "TestPDFImap.pdf";
    private static final String TEST_HTML_FILE = "Test5.html";
    private static final String TEST_DOCX_FILE = "WordDocument.docx";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    @BeforeMethod(groups={"DataPrepGoogleDocs","GoogleDocs"})
    public void prepare()
    {
        // Deleting cookies
        ShareUser.deleteSiteCookies(drone, googleURL);
        ShareUser.deleteSiteCookies(drone, googlePlusURL);
        	
        logger.info("Deleted google cookies successfully.");
    }

    /**
     * DataPreparation method - ALF-1515
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1515() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Site creation
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
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
     * Note: Test Link Steps are changed in implementation, 4,5,6 steps come first and 1,2,3 steps later. Test - ALF-1515:Changes can be discarded.
     * <ul>
     * <li>Login</li>
     * <li>Open Site</li>
     * <li>Click on createcontent for google docs</li>
     * <li>Verify Google docs is opened</li>
     * <li>Rename the document</li>
     * <li>Verify Document is renamed</li>
     * <li>Click Discard Changes</li>
     * <li>Verify Google Docs editor is closed, user redirected to the document library, document created vid 'Create Document' menu is not created in document
     * library because changes were discarded</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Edit the content</li>
     * <li>Click on Discard Changes</li>
     * <li>Open docx in Alfresco share</li>
     * <li>Verify the discarded changes should not be present</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1515() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName + System.currentTimeMillis());
        String testUser = getUserNameFreeDomain(testName);
        String newFileName = getFileName(testName + "1");
        String siteName = getSiteName(testName);

        fileName = fileName + ".docx";
        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        docLibPage = createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);

        Assert.assertTrue(docLibPage.isDocumentLibrary());

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        String docVersion = detailsPage.getDocumentVersion();

        // Select editInGoogleDocs and sign into googleDocs.
        EditInGoogleDocsPage googleDocsPage = openEditGoogleDocFromDetailsPage(drone);
        
        renameGoogleDocName(newFileName, googleDocsPage);

        // Discard the google doc modifications.
        detailsPage = (DocumentDetailsPage) discardGoogleDocsChanges(drone);
        detailsPage.render();

        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
        Assert.assertTrue(fileName.equalsIgnoreCase(detailsPage.getDocumentTitle()));
        Assert.assertEquals(detailsPage.getDocumentVersion(), docVersion);
    }

    /**
     * DataPreparation method - ALF-1521
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create a content docx type</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1521() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
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
     * Test - ALF-1521:File name can be changed.
     * <ul>
     * <li>Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Rename the document</li>
     * <li>Verify Document is renamed</li>
     * <li>Click Save Changes</li>
     * <li>Verify Google Docs name should be renamed</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1521() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String newFileName = "Edited in Google Docs" + System.currentTimeMillis();

        fileName = fileName + ".docx";
        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);

        ShareUser.openDocumentDetailPage(drone, fileName);

        // open editInGoogleDocs.
        EditInGoogleDocsPage googleDocsPage = openEditGoogleDocFromDetailsPage(drone);

        // Rename the google doc name
        renameGoogleDocName(newFileName, googleDocsPage);

        // Save the google doc modifications.
        DocumentDetailsPage docDetailsPage = (DocumentDetailsPage) saveGoogleDoc(drone, false);
        docDetailsPage.render();

        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone);

        newFileName = newFileName + ".docx";
        docDetailsPage = docLibPage.selectFile(newFileName).render();

        Assert.assertEquals("1.1", docDetailsPage.getDocumentVersion());

        docLibPage = ShareUser.openDocumentLibrary(drone);

        docLibPage.isFileVisible(newFileName);
    }

    /**
     * DataPreparation method - ALF-1522
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Upload encrypted content docx type</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1522() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String encryptedFileName = "Password protected.docx";
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Uploading encrypted file.
            String[] fileInfo = { encryptedFileName };
            ShareUser.uploadFileInFolder(drone, fileInfo);
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
     * Note: This test is partially implemented and once Webdrone-133 is resolved, the remaining coding will be comopleted. Test - ALF-1522:Password protected
     * documents.
     * <ul>
     * <li>Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select encrypted docx document</li>
     * <li>Verify error message is displayed and Document is present in doclib.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1522() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String encryptedFileName = "Password protected.docx";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.openDocumentDetailPage(drone, encryptedFileName);

        // Sign into googleDocs.
        try
        {
            signIntoEditGoogleDocFromDetailsPage(drone);
        }
        catch(PageException e)
        {
            Assert.assertTrue(e.getMessage().contains("There was an error opening the document in Google Docs™. If the errors occurs again please contact your System Administrator."));
        }
        
        Assert.assertTrue(drone.getCurrentPage().getTitle().contains("Document Details"));
    }

    /**
     * DataPreparation method - ALF-1927
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create content with docx type</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1927() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
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
     * Test - ALF-1927:Save editing document. User logged out from Alfresco.
     * <ul>
     * <li>Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Capture the url</li>
     * <li>Sign Out of google docs</li>
     * <li>Delete cookies</li>
     * <li>Navigate to url</li>
     * <li>Verify the login page should be displayed.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1927() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        fileName = fileName +ShareUser.getRandomStringWithNumders(4)+ ".docx";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);
        
        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
        detailsPage.render();

        // open editInGoogleDocs.
        openEditGoogleDocFromDetailsPage(drone);

        // Capture the URL
        String url = drone.getCurrentUrl();

        // Discard changes and Navigate to alfresco
        discardGoogleDocsChanges(drone);

        // Logout
        ShareUser.logout(drone);

        // Deleting cookies
        ShareUser.deleteSiteCookies(drone, googleURL);
        drone.deleteCookies();

        // Navigate to the captured URL
        drone.navigateTo(url);

        HtmlPage page = drone.getCurrentPage().render();

        Assert.assertTrue(page.getTitle().equals("Alfresco » Login"));

        ((LoginPage) page).loginAs(testUser, DEFAULT_PASSWORD);

        Assert.assertTrue(drone.getCurrentPage().getTitle().equals("Alfresco » Google Docs Editor"));
    }

    /**
     * DataPreparation method - ALF-1929
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create contents with docx,xls,ppt type</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1929() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] testUserInfo = new String[] { testUser };
        String docFileName = fileName + "_doc";
        String xlsFileName = fileName + "_xls";
        String pptFileName = fileName + "_ppt";

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.openDocumentLibrary(drone);

            // Creating and saving google doc through sign in with the google doc authentication details
            createAndSavegoogleDocBySignIn(drone, docFileName, ContentType.GOOGLEDOCS);

            // Navigating to google account and Deleting cookies
            ShareUser.deleteSiteCookies(drone, googleURL);
            ShareUser.deleteSiteCookies(drone, googlePlusURL);
            
            createAndSavegoogleDocBySignIn(drone, xlsFileName, ContentType.GOOGLESPREADSHEET);

            // Deleting cookies
            ShareUser.deleteSiteCookies(drone, googleURL);
            ShareUser.deleteSiteCookies(drone, googlePlusURL);

            createAndSavegoogleDocBySignIn(drone, pptFileName, ContentType.GOOGLEPRESENTATION);
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
     * Test - ALF-1929::Check that document opened for editing in appropriate Google Docs editor.
     * <ul>
     * <li>Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>open 3 google doc files</li>
     * <li>verify the name</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1929() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String docFileName = fileName + "_doc.docx";
        String xlsFileName = fileName + "_xls.xlsx";
        String pptFileName = fileName + "_ppt.pptx";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, docFileName);

        // open editInGoogleDocs.
        EditInGoogleDocsPage googleDocsPage = signIntoEditGoogleDocFromDetailsPage(drone);
        String docTitle = googleDocsPage.getDocumentTitle();

        Assert.assertTrue(docTitle.contains(fileName + "_doc"));

        // Discard changes and Navigate to alfresco
        detailsPage = (DocumentDetailsPage) discardGoogleDocsChanges(drone);
        detailsPage.render();

        // Opening .xls file in googledocs
        ShareUser.openDocumentLibrary(drone);
        detailsPage = ShareUser.openDocumentDetailPage(drone, xlsFileName);
        detailsPage.render();

        // open editInGoogleDocs.
        googleDocsPage = openEditGoogleDocFromDetailsPage(drone);
        docTitle = googleDocsPage.getDocumentTitle();

        Assert.assertTrue(docTitle.contains(fileName + "_xls"));

        // Discard changes and Navigate to alfresco
        detailsPage = (DocumentDetailsPage) discardGoogleDocsChanges(drone);
        detailsPage.render();

        // Opening .ppt file in googledocs
        ShareUser.openDocumentLibrary(drone);

        detailsPage = ShareUser.openDocumentDetailPage(drone, pptFileName);
        detailsPage.render();

        // open editInGoogleDocs.
        googleDocsPage = openEditGoogleDocFromDetailsPage(drone);
        docTitle = googleDocsPage.getDocumentTitle();

        Assert.assertTrue(docTitle.contains(fileName + "_ppt"));

        // Discard changes and Navigate to alfresco
        detailsPage = (DocumentDetailsPage) discardGoogleDocsChanges(drone);
        detailsPage.render();
    }

    /**
     * DataPreparation method - ALF-1931
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create content with docx type</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1931() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
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
     * Test - ALF-1931:Check versioning for edited document.
     * <ul>
     * <li>Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Edit doc name and version</li>
     * <li>Cancel the changes</li>
     * <li>Modify the version and comment again</li>
     * <li>Save the changes</li>
     * <li>Modify the version as major and comment again</li>
     * <li>Save the changes</li>
     * <li>Verify the doc name, version and comments</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1931() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName) + ShareUser.getRandomStringWithNumders(4);
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        fileName = fileName + ".docx";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        // open editInGoogleDocs.
        EditInGoogleDocsPage googleDocsPage = openEditGoogleDocFromDetailsPage(drone);

        // Verifying the editInGoogleDocs page or Iframe
        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());

        // Renaming the filename;
        fileName = fileName.replace(".docx", "Modified.docx");

        googleDocsPage = renameGoogleDocName(fileName, googleDocsPage);

        // Click Save to Alfresco and modify the minor version and comment , click cancel
        GoogleDocsUpdateFilePage googleUpdatefile = saveGoogleDocWithVersionAndComment(drone, "First Comments", true);
        googleUpdatefile.selectCancel();

        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());

        // Click Save to Alfresco and modify the minor version and comment , click save
        googleUpdatefile = saveGoogleDocWithVersionAndComment(drone, "Second Comments", true);
        detailsPage = googleUpdatefile.submit().render();

        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
        Assert.assertEquals("1.1", detailsPage.getDocumentVersion());
        Assert.assertEquals(fileName, detailsPage.getDocumentTitle());

        // Open doc lib.
        ShareUser.openDocumentLibrary(drone);
        detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        // Open the editgoogle docs for the same file again
        googleDocsPage = openEditGoogleDocFromDetailsPage(drone);

        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());

        // Renaming the file name
        fileName = fileName.replace(".docx", "Renamed.docx");

        googleDocsPage = renameGoogleDocName(fileName, googleDocsPage);

        // Click Save to Alfresco and modify the major version and comment , click cancel
        googleUpdatefile = saveGoogleDocWithVersionAndComment(drone, "Third Comments", false);
        detailsPage = googleUpdatefile.submit().render();

        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
        Assert.assertEquals("2.0", detailsPage.getDocumentVersion());
        Assert.assertEquals(fileName, detailsPage.getDocumentTitle());
        Assert.assertEquals(detailsPage.getCommentsOfLastCommit(), "Third Comments");
    }

    /**
     * DataPreparation method - ALF-2452
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create folder and sync with cloud</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_2452() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };
        String folderName = getFolderName(testName);

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
            CreateUserAPI.CreateActivateUser(hybridDrone, ADMIN_USERNAME, testUserInfo);

            // Cloud User login
            ShareUser.login(hybridDrone, testUser, DEFAULT_PASSWORD);

            // Creating Site
            ShareUser.createSite(hybridDrone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Cloud logout.
            ShareUser.logout(hybridDrone);

            // Login as User (On-Premise) and configure Cloud Sync
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            signInToAlfrescoInTheCloud(drone, testUser, DEFAULT_PASSWORD);

            // Create Site
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

            // Creating folder.
            DocumentLibraryPage docsPage = ShareUserSitePage.createFolder(drone, folderName, null);
          
            // Select network and site and click on sync
            DestinationAndAssigneeBean destinationBean = new DestinationAndAssigneeBean();
            destinationBean.setNetwork(getUserDomain(testUser));
            destinationBean.setSiteName(siteName);
            
            docsPage = (DocumentLibraryPage) AbstractCloudSyncTest.syncContentToCloud(drone, folderName, destinationBean);
            docsPage.render();
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
     * Test - ALF-2452:Creating Google Doc in Synced folder.
     * <ul>
     * <li>Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Rename the document</li>
     * <li>Verify Document is renamed</li>
     * <li>Click Save Changes</li>
     * <li>Verify Google Docs name should be renamed</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "CloudSync", timeOut = 400000)
    public void ALF_2452() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        ShareUser.deleteSiteCookies(drone, googleURL);
        ShareUser.deleteSiteCookies(drone, googlePlusURL);
        
        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);

        ShareUser.deleteSiteCookies(drone, googleURL);
        ShareUser.deleteSiteCookies(drone, googlePlusURL);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEPRESENTATION);

        ShareUser.deleteSiteCookies(drone, googleURL);
        ShareUser.deleteSiteCookies(drone, googlePlusURL);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLESPREADSHEET);

        Assert.assertTrue(docLibPage.isDocumentLibrary());

        Assert.assertTrue(docLibPage.isFileVisible(fileName + ".docx"));

        Assert.assertTrue(docLibPage.isFileVisible(fileName + ".xlsx"));

        Assert.assertTrue(docLibPage.isFileVisible(fileName + ".pptx"));
    }

    /**
     * Note: This data setup valid for enterprise only.
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Upload documents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1502() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            String[] fileInfo1 = { TEST_DOC_FILE };
            ShareUser.uploadFileInFolder(drone, fileInfo1);

            String[] fileInfo2 = { TEST_TXT_FILE };
            ShareUser.uploadFileInFolder(drone, fileInfo2);

            String[] fileInfo3 = { TEST_JPG_FILE };
            ShareUser.uploadFileInFolder(drone, fileInfo3);

            String[] fileInfo4 = { TEST_PDF_FILE };
            ShareUser.uploadFileInFolder(drone, fileInfo4);
            
            String[] fileInfo5 = { TEST_HTML_FILE };
            ShareUser.uploadFileInFolder(drone, fileInfo5);
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
     * Note: This test will be valid for Enterprise only. Test - ALF-1502:No option to edit in Google Docs documents of un-supported MIME type.
     * <ul>
     * <li>Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added documents</li>
     * <li>Verify edit in Google Docs option should not be present</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1502() throws Exception
    { 
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verifying the EditInGoogleDocs link is present for the supported formats.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_DOC_FILE).isEditInGoogleDocsPresent());
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_TXT_FILE).isEditInGoogleDocsPresent());

        // Verifying the EditInGoogleDocs link is not present for the below documents
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(TEST_JPG_FILE).isEditInGoogleDocsPresent());
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(TEST_PDF_FILE).isEditInGoogleDocsPresent());
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(TEST_HTML_FILE).isEditInGoogleDocsPresent());
    }

    /**
     * DataPreparation method - ALF-1516
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1516() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
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
     * Test - ALF-1516:Unsupported characters in file name.
     * <ul>
     * <li>Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Edit doc name with spl chars and version</li>
     * <li>Save the changes</li>
     * <li>Verify the error message is displayed.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1516() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        String expectedMessage = "Document cannot be saved to Alfresco. The filename contains illegal characters.";

        fileName = fileName + ".docx";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);

        ShareUser.openDocumentDetailPage(drone, fileName);

        // open editInGoogleDocs.
        EditInGoogleDocsPage googleDocsPage = openEditGoogleDocFromDetailsPage(drone);

        // Renaming the filename;
        fileName = fileName.replace(".docx", "> :*<>?.docx");

        googleDocsPage = renameGoogleDocName(fileName, googleDocsPage);

        // Click Save to Alfresco and modify the minor version and comment , click save
        GoogleDocsUpdateFilePage googleUpdatefile = saveGoogleDocWithVersionAndComment(drone, "First Comments", true);
        
        SharePopup shareErrorPopup = googleUpdatefile.submit().render();

        String actualMessage = shareErrorPopup.getShareMessage();

        Assert.assertEquals(actualMessage, expectedMessage);
    }

    /**
     * DataPreparation method - ALF-1934
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1934() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
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
     * Test - ALF-1934:Save to Alfresco. Delete document.
     * <ul>
     * <li>Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Navigate to doclib from another browser</li>
     * <li>Verify the delete button is not displayed.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1934() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + ShareUser.getRandomStringWithNumders(4);

        fileName = fileName + ".docx";

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        String url = drone.getCurrentUrl();

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Open Site Library
        ShareUser.openDocumentLibrary(drone);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);

        ShareUser.openDocumentDetailPage(drone, fileName);

        // open editInGoogleDocs.
        openEditGoogleDocFromDetailsPage(drone);

        // Open Site Library
        drone.navigateTo(url);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());
    }

    /**
     * DataPreparation method - ALF-1935
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site with site admin</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1935() throws Exception
    {
        String testName = getTestName();
        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String siteName = getSiteName(testName);
        String[] siteAdminInfo = new String[] { siteAdmin };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, siteAdminInfo);

            // User login
            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
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
     * Note : WEBDRONE-219:Incorrect CSS used in UserRole enum .
     * Test - ALF-1935:Save to Alfresco. Decrease user's rights.
     * <ul>
     * <li>Create InvitedUser</li>
     * <li>SiteAdmin login</li>
     * <li>Upload document</li>
     * <li>Invite second user onto the site as collaborator</li>
     * <li>InvitedUser Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Save the URL and Delete Cookies</li>
     * <li>Login as Site Admin</li>
     * <li>Change the InvitedUser role as consumer</li>
     * <li>Delete Cookies</li>
     * <li>Load the saved URL</li>
     * <li>InviteUser Navigated to Opening doc in google docs</li>
     * <li>Click save to alfresco</li>
     * <li>Verify the error message.</li>
     * </ul>
     * 
     * @throws Exception
     */
    //@Test(groups={"GoogleDocs","Enterprise42Bug"}, timeOut = 400000)
    public void ALF_1935() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        String siteName = getSiteName(testName);
        String expectedMessage = "You no longer have access to this site.";
        String[] testUserInfo = new String[] { testUser };

        fileName = fileName + ".docx";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);

        // Invite user to Site as Collaborator.
        ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, testUser, siteName, UserRole.COLLABORATOR);
        ShareUser.logout(drone);

        ShareUser.deleteSiteCookies(drone, googleURL);
        ShareUser.deleteSiteCookies(drone, googlePlusURL);

        // Login As testUser
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.openDocumentDetailPage(drone, fileName);

        // Opening google doc
        signIntoEditGoogleDocFromDetailsPage(drone);

        // Getting second drone
        WebDrone secondDrone = getSecondDrone();
        
        try
        {
            // SiteAdmin login.
            ShareUser.login(secondDrone, siteAdmin, DEFAULT_PASSWORD);
            
            SiteUtil.openSiteDocumentLibraryURL(secondDrone, getSiteShortname(siteName)); 

            // Assign Consumer Role to InvitedUser
            DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(secondDrone, fileName);
            
            detailsPage = (DocumentDetailsPage) ShareUserMembers.managePermissionsOnContent(secondDrone, testUser, fileName, UserRole.CONSUMER, false);
            detailsPage.render();
        }
        catch (Throwable e)
        {
            reportError(secondDrone, testName, e);
        }
        finally
        {
            testCleanup(secondDrone, testName);
            secondDrone.quit();
        }
        
        SharePopup shareErrorPopup = (SharePopup) saveGoogleDoc(drone, false);
        shareErrorPopup.render();

        String actualMessage = shareErrorPopup.getShareMessage();
        Assert.assertEquals(actualMessage, expectedMessage," WEBDRONE-219:Incorrect CSS used in UserRole enum ");
    }

    /**
     * DataPreparation method - ALF-1936
     * <ul>
     * <li>Login</li>
     * <li>Create admin Users</li>
     * <li>Create Site with site admin</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1936() throws Exception
    {
        String testName = getTestName();
        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String siteName = getSiteName(testName);
        String[] siteAdminInfo = new String[] { siteAdmin };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, siteAdminInfo);

            // User login
            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
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
     * Test - ALF-1936:Save to Alfresco. Remove User from site members.
     * <ul>
     * <li>Create Sitemember</li>
     * <li>SiteAdmin login</li>
     * <li>Upload document</li>
     * <li>Invite second user onto the site as collaborator</li>
     * <li>InvitedUser Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Save the URL and Delete Cookies</li>
     * <li>Login as Site Admin</li>
     * <li>Remove the InvitedUser from sitemembers list</li>
     * <li>Delete Cookies</li>
     * <li>Load the saved URL</li>
     * <li>Opened document in google docs</li>
     * <li>Try to save the google doc.</li>
     * <li>Verify the expected error message is displayed</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1936() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        String siteName = getSiteName(testName);
        String expectedMessage = "You no longer have access to this site.";
        String[] testUserInfo = new String[] { testUser };

        fileName = fileName + ".docx";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // SiteAdmin login
        ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);

        // Invite user to Site as Collaborator.
        ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, testUser, siteName, UserRole.COLLABORATOR);
        
        ShareUser.deleteSiteCookies(drone, googleURL);
        ShareUser.deleteSiteCookies(drone, googlePlusURL);
        
        ShareUser.logout(drone);

        // Login As testUser
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

       // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.openDocumentDetailPage(drone, fileName);

        // Opening google doc through sign in with the google doc authentication details
        signIntoEditGoogleDocFromDetailsPage(drone);

        WebDrone secondDrone = getSecondDrone();

        try
        {
            // SiteAdmin login.
            ShareUser.login(secondDrone, siteAdmin, DEFAULT_PASSWORD);
            
            // Remove sitemember
            ShareUserMembers.removeSiteMember(secondDrone, testUser, siteName);
        }
        catch (Throwable e)
        {
            reportError(secondDrone, testName, e);
        }
        finally
        {
            testCleanup(secondDrone, testName);
            secondDrone.quit();
        }

        SharePopup shareErrorPopup =(SharePopup) saveGoogleDoc(drone, false);
        shareErrorPopup.render();

        String actualMessage = shareErrorPopup.getShareMessage();
        Assert.assertEquals(actualMessage, expectedMessage);
    }

    /**
     * DataPreparation method - ALF-1937
     * <ul>
     * <li>Login</li>
     * <li>Create admin Users</li>
     * <li>Create Site with site admin</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1937() throws Exception
    {
        String testName = getTestName();
        String siteAdmin = getUserNamePremiumDomain(testName + "Admin");
        String siteName = getSiteName(testName);
        String[] siteAdminInfo = new String[] { siteAdmin };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, siteAdminInfo);
            CreateUserAPI.upgradeCloudAccount(drone, ADMIN_USERNAME, getUserDomain(siteAdmin), "1000");
            CreateUserAPI.promoteUserAsAdmin(drone, ADMIN_USERNAME, siteAdmin, getUserDomain(siteAdmin));

            // User login
            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
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
     * Test - ALF-1937:Save to Alfresco. User deleted.
     * <ul>
     * <li>Create Sitemember</li>
     * <li>SiteAdmin login</li>
     * <li>Upload document</li>
     * <li>Invite second user onto the site as collaborator</li>
     * <li>InvitedUser Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Save the URL and Delete Cookies</li>
     * <li>Login as Site Admin in another window</li>
     * <li>Delete the invitedUser from users list</li>
     * <li>Delete Cookies and close the second window</li>
     * <li>Load the saved URL</li>
     * <li>Try to save google docs</li>
     * <li>Verify the expected error message is displayed.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "Enterprise42Bug", "EnterpriseOnly" }, timeOut = 400000)
    public void ALF_1937() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String siteAdmin = getUserNamePremiumDomain(testName + "Admin");
        String testUser = getUserNamePremiumDomain(testName + System.currentTimeMillis());
        String siteName = getSiteName(testName);
        // String expectedMessage = "Incorrect User.";
        String[] testUserInfo = new String[] { testUser };

        fileName = fileName + ".docx";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        CreateUserAPI.upgradeCloudAccount(drone, ADMIN_USERNAME, getUserDomain(testUser), "1000");

        // SiteAdmin login
        ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);

        // Invite user to Site as Collaborator.
        ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, testUser, siteName, UserRole.COLLABORATOR);
        ShareUser.deleteSiteCookies(drone, googleURL);
        ShareUser.deleteSiteCookies(drone, googlePlusURL);
        ShareUser.logout(drone);

        // Login As testUser
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.openDocumentDetailPage(drone, fileName);

        // Opening google doc through sign in with the google doc authentication details
        EditInGoogleDocsPage googleDocsPage = signIntoEditGoogleDocFromDetailsPage(drone);

        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());

        // Getting second drone
        WebDrone secondDrone = getSecondDrone();

        try
        {
            // login with second drone as SiteAdmin.
            ShareUser.login(secondDrone, siteAdmin, DEFAULT_PASSWORD);
            
            // Removing user from users list
            deleteUser(secondDrone, testUser);
        }
        catch (Throwable e)
        {
            reportError(secondDrone, testName, e);
        }
        finally
        {
            testCleanup(secondDrone, testName);
            secondDrone.quit();
        }

        // Second user tries to save the document.
        SharePopup shareErrorPopup = (SharePopup)saveGoogleDoc(drone, false);
        shareErrorPopup.render();
        // Note: Expected error popup is not displaying and not redirecting to login page, Need to have analysis on it.
        /*
         * actualMessage = shareErrorPopup.getShareErrorMessage(); Assert.assertEquals(actualMessage, expectedMessage);
         */
    }

    /**
     * DataPreparation method - ALF-1938
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Upload document</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1938() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };
        String docFileName = getFileName(testName);

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.openDocumentLibrary(drone);

            // Creating and saving google doc through sign in with the google doc authentication details
            createAndSavegoogleDocBySignIn(drone, docFileName, ContentType.GOOGLEDOCS);

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
     * Test - ALF-1938:Save to Alfresco. Delete Site.
     * <ul>
     * <li>User Login</li>
     * <li>Save the dashboard URL</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Save the googleDoc URL</li>
     * <li>Load the dashboard URL</li>
     * <li>delete the site</li>
     * <li>load the googleDoc URL</li>
     * <li>Open doc in google docs and click save</li>
     * <li>Verify the detailsPage is opened and version will be changed.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1938() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        fileName = fileName + ".docx";

        // InvitedUser login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Saving the url.
        String dashBoardUrl = drone.getCurrentUrl();

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        String docVersion = detailsPage.getDocumentVersion();

        // Opening google doc through sign in with the google doc authentication details
        signIntoEditGoogleDocFromDetailsPage(drone);

        // Saving the url.
        String googleDocsUrl = drone.getCurrentUrl();

        // Navigate to saved url
        drone.navigateTo(dashBoardUrl);

        // Deleting site, but site should not be deleted and will return the false.
        Assert.assertFalse(SiteUtil.deleteSite(drone, siteName));

        // Navigate to saved url
        drone.navigateTo(googleDocsUrl);

        // Saving the google doc.
        detailsPage = (DocumentDetailsPage) saveGoogleDoc(drone, false);
        detailsPage.render();

        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
        Assert.assertNotEquals(detailsPage.getDocumentVersion(), docVersion);
    }

    /**
     * DataPreparation method - ALF-1513
     * <ul>
     * <li>Login</li>
     * <li>Create 2 Users</li>
     * <li>Create Site with site admin</li>
     * <li>Invite another user onto site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1513() throws Exception
    {
        String testName = getTestName();
        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] siteAdminInfo = new String[] { siteAdmin };
        String[] testUsernInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, siteAdminInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUsernInfo);

            // User login
            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Invite user to Site as Collaborator.
            ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, testUser, siteName, UserRole.COLLABORATOR);
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

    /**
     * Test - ALF-1513:Lock Mechanism.
     * <ul>
     * <li>SiteAdmin login</li>
     * <li>Add document to the site</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>Save the URL</li>
     * <li>InvitedUser Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>verify the added docx does not show the editIngoogledocs option</li>
     * <li>Login with siteAdmin</li>
     * <li>Load the saved URL</li>
     * <li>Navigated to googledocs</li>
     * <li>Save doc in google docs</li>
     * <li>Verify the save is successful.</li>
     * <li>InvitedUser Login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>verify the added docx should show the editIngoogledocs option</li>
     * <li>select editIngoogledocs option and discard changes</li>
     * <li>verify the upload new version is available</li>
     * <li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1513() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String docVersion = "";
        String modifiedDocVersion = "";
        
        fileName = fileName + ".docx";

        // SiteAdmin login
        ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);
        
        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        docVersion = detailsPage.getDocumentVersion();

        // Opening google doc through sign in with the google doc authentication details
        EditInGoogleDocsPage googleDocsPage = openEditGoogleDocFromDetailsPage(drone);
        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());

        // Saving the url.
        String url = drone.getCurrentUrl();

        // Select BackToAlfresco
        detailsPage = googleDocsPage.selectBackToShare().render();       

        ShareUser.logout(drone);

        // Login As testUser
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Document is locked for editing, EditingoogleDocs option isn't available
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditInGoogleDocsPresent());

        detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        Assert.assertFalse(detailsPage.isUploadNewVersionDisplayed());

        ShareUser.logout(drone);

        // SiteAdmin login.
        ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

        // Navigate to saved url
        drone.navigateTo(url);

        // Saving google doc
        detailsPage = (DocumentDetailsPage) saveGoogleDoc(drone, false);
        detailsPage.render();

        modifiedDocVersion = detailsPage.getDocumentVersion();

        Assert.assertTrue(detailsPage.isDocumentDetailsPage());

        Assert.assertNotEquals(docVersion, modifiedDocVersion);

        ShareUser.logout(drone);

        // Deleting cookies
        ShareUser.deleteSiteCookies(drone, googleURL);
        ShareUser.deleteSiteCookies(drone, googlePlusURL);

        // Login As testUser
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        googleDocsPage = signIntoEditGoogleDocFromDetailsPage(drone);

        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());

        // Discarding google doc changes.
        detailsPage = (DocumentDetailsPage) discardGoogleDocsChanges(drone);
        detailsPage.render();

        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
        Assert.assertTrue(detailsPage.isUploadNewVersionDisplayed());
        
        String latestDocVersion = detailsPage.getDocumentVersion();
        
        // Uploading new version of the document.
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, fileName, "New version uploaded.");

        Assert.assertNotEquals(latestDocVersion, detailsPage.getDocumentVersion());
        Assert.assertEquals("New version uploaded.", detailsPage.getCommentsOfLastCommit());
    }

    /**
     * DataPreparation method - ALF-1943
     * <ul>
     * <li>Login</li>
     * <li>Create 4 Users</li>
     * <li>Create Site with site admin</li>
     * <li>Add document to the site</li>
     * <li>Invite another user onto site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1943() throws Exception
    {
        String testName = getTestName();
        String siteAdmin = getUserNameFreeDomain(testName + "Admin").toLowerCase();
        String collaborator = getUserNameFreeDomain(testName + "Colloaborator").toLowerCase();
        String contributor = getUserNameFreeDomain(testName + "Contributor").toLowerCase();
        String consumer = getUserNameFreeDomain(testName + "Consumer").toLowerCase();
        String siteName = getSiteName(testName + "test2");
        String[] siteAdminInfo = new String[] { siteAdmin };
        String[] collaboratorInfo = new String[] { collaborator };
        String[] contributorInfo = new String[] { contributor };
        String[] consumerInfo = new String[] { consumer };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, siteAdminInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, collaboratorInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, contributorInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, consumerInfo);

            // User login
            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Invite users to Site as Collaborator,contributor,consumer.
            ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, collaborator, siteName, UserRole.COLLABORATOR);
            ShareUser.logout(drone);
            
            // User login
            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);
            ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, contributor, siteName, UserRole.CONTRIBUTOR);
            ShareUser.logout(drone);
            
            // User login
            ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);
            ShareUserMembers.inviteUserToSiteWithRole(drone, siteAdmin, consumer, siteName, UserRole.CONSUMER);
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

    /**
     * This test is combination of ALF-1943/1944/1945 Test - ALF-1943:Lock Mechanism.
     * <ul>
     * <li>Collaborator login</li>
     * <li>Open Document library page</li>
     * <li>select added docx document</li>
     * <li>Open in google docs</li>
     * <li>rename the file and save the changes</li>
     * <li>Site Admin login</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>verify the changes done by collaborator</li>
     * <li>Login with contributor</li>
     * <li>verify the EditInGoogleDocs option should not be available</li>
     * <li>Login with consumer</li>
     * <li>verify the EditInGoogleDocs option should not be available</li>
     * <li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1943() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName+ ShareUser.getRandomStringWithNumders(3));
        String siteAdmin = getUserNameFreeDomain(testName + "Admin");
        String collaborator = getUserNameFreeDomain(testName + "Colloaborator");
        String contributor = getUserNameFreeDomain(testName + "Contributor");
        String consumer = getUserNameFreeDomain(testName + "Consumer");
        String siteName = getSiteName(testName + "test2");

        fileName = fileName + ".docx";

        // User login
        ShareUser.login(drone, siteAdmin, DEFAULT_PASSWORD);
        
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);
        
        ShareUser.logout(drone);
        
        ShareUser.deleteSiteCookies(drone, googleURL);
        ShareUser.deleteSiteCookies(drone, googlePlusURL);
        // Collaborator login
        ShareUser.login(drone, collaborator, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
        detailsPage.render();

        String docVersion = detailsPage.getDocumentVersion();

        // Opening google doc through sign in with the google doc authentication details
        EditInGoogleDocsPage googleDocsPage = signIntoEditGoogleDocFromDetailsPage(drone);

        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());

        // Saving google doc
        detailsPage = (DocumentDetailsPage) saveGoogleDoc(drone, false);
        detailsPage.render();

        String modifiedDocVersion = detailsPage.getDocumentVersion();
        
        Assert.assertNotEquals(docVersion, modifiedDocVersion);
        
        ShareUser.logout(drone);

        // Contributor login
        ShareUser.login(drone, contributor, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditInGoogleDocsPresent());

        ShareUser.logout(drone);

        // Consumer login
        ShareUser.login(drone, consumer, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditInGoogleDocsPresent());

        ShareUser.logout(drone);
    }

    /**
     * DataPreparation method - ALF-1946
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Add 2 documents to the site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1946() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUsernInfo = new String[] { testUser };
        String firstFileName = getFileName(testName + "1");
        String secondFileName = getFileName(testName + "2");

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUsernInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Creating and saving google doc through sign in with the google doc authentication details
            createAndSavegoogleDocBySignIn(drone, firstFileName, ContentType.GOOGLEDOCS);

            // Deleting cookies
            ShareUser.deleteSiteCookies(drone, googleURL);
            ShareUser.deleteSiteCookies(drone, googlePlusURL);

            // Creating and saving google doc through sign in with the google doc authentication details
            createAndSavegoogleDocBySignIn(drone, secondFileName, ContentType.GOOGLEDOCS);
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
     * Note: Alfreso Enterprise42 issue (ALF-20775) is raised for this task.  
     * Test - ALF-1946:Check editing two documents within two different browser sessions.
     * <ul>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select first document</li>
     * <li>Open in google docs</li>
     * <li>Login as Site Admin in another browser</li>
     * <li>Navigate to doclib</li>
     * <li>Open in doc in google docs</li>
     * <li>Open Second doc</li>
     * <li>Edit and save the two documents alternatively in two browsers</li>
     * <li>verify save is successful</li>
     * </ul>
     * 
     * @throws Exception
     */
    //@Test(groups = { "GoogleDocs", "Enterprise42Bug" }, timeOut = 400000)
    public void ALF_1946() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String firstFileName = getFileName(testName + "1");
        String secondFileName = getFileName(testName + "2");
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String firstDocVersion = null;
        String secondDocVersion = null;

        firstFileName = firstFileName + ".docx";
        secondFileName = secondFileName + ".docx";

        // login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, firstFileName);

        // Retrieving first document version before changing it.
        firstDocVersion = detailsPage.getDocumentVersion();

        // Opening google doc through sign in with the google doc authentication details
        signIntoEditGoogleDocFromDetailsPage(drone);

        // Getting second drone
        WebDrone secondDrone = getSecondDrone();
        String actualComments = "";
        String actualDocVersion = "";

        try
        {
            // login with second drone.
            ShareUser.login(secondDrone, testUser, DEFAULT_PASSWORD);
            
            // Open Site Library
            ShareUser.openSitesDocumentLibrary(secondDrone, siteName);
            
            DocumentDetailsPage secondDetailsPage = ShareUser.openDocumentDetailPage(secondDrone, secondFileName);
            
            // Retrieving second document version before changing it.
            secondDocVersion = secondDetailsPage.getDocumentVersion();
            
            // Opening google doc through sign in with the google doc authentication details
            signIntoEditGoogleDocFromDetailsPage(secondDrone);
            
            // Saving the second document changes in second browser
            GoogleDocsUpdateFilePage googleUpdatefile2 = saveGoogleDocWithVersionAndComment(secondDrone, "Second document renamed", true);
            secondDetailsPage = googleUpdatefile2.submit().render();
            
            actualComments = secondDetailsPage.getCommentsOfLastCommit();
            actualDocVersion = secondDetailsPage.getDocumentVersion();
        }
        catch (Throwable e)
        {
            reportError(secondDrone, testName, e);
        }
        finally
        {
            testCleanup(secondDrone, testName);
            secondDrone.quit();
        }
        
        // Saving the first document changes in first browser
        GoogleDocsUpdateFilePage googleUpdatefile1 = saveGoogleDocWithVersionAndComment(drone, "First document renamed", true);
        detailsPage = googleUpdatefile1.submit().render();

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        detailsPage = ShareUser.openDocumentDetailPage(drone, firstFileName);

        Assert.assertEquals("First document renamed", detailsPage.getCommentsOfLastCommit());
        Assert.assertNotSame(firstDocVersion, detailsPage.getDocumentVersion());

        ShareUser.openDocumentLibrary(drone);

        detailsPage = ShareUser.openDocumentDetailPage(drone, secondFileName);

        Assert.assertNotSame(secondDocVersion, actualDocVersion);
        Assert.assertEquals("Second document renamed", actualComments);
        
        ShareUser.logout(drone);
    }

    /**
     * DataPreparation method - ALF-1947
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Add 2 documents to the site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1947() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUsernInfo = new String[] { testUser };
        String firstFileName = getFileName(testName + "1");
        String secondFileName = getFileName(testName + "2");

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUsernInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Creating and saving google doc through sign in with the google doc authentication details
            createAndSavegoogleDocBySignIn(drone, firstFileName, ContentType.GOOGLEDOCS);

            // Deleting cookies
            ShareUser.deleteSiteCookies(drone, googleURL);
            ShareUser.deleteSiteCookies(drone, googlePlusURL);

            // Creating and saving google doc through sign in with the google doc authentication details
            createAndSavegoogleDocBySignIn(drone, secondFileName, ContentType.GOOGLEDOCS);
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
     * Test - ALF-1947:Check editing two different documents in the same browser
     * <ul>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select first document</li>
     * <li>Open in google docs</li>
     * <li>Back To Share</li>
     * <li>Navigate to doclib</li>
     * <li>Open Second doc</li>
     * <li>Back To Share</li>
     * <li>Open Document library page</li>
     * <li>select first document</li>
     * <li>verify Resume in Google Docs link is present</li>
     * <li>Select Resume in Google Docs link</li>
     * <li>Save Changes.</li>
     * <li>Navigate to doclib</li>
     * <li>Open Second doc</li>
     * <li>verify Resume in Google Docs link is present</li>
     * <li>Select Resume in Google Docs link</li>
     * <li>Save Changes.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1947() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String firstFileName = getFileName(testName + "1");
        String secondFileName = getFileName(testName + "2");
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        firstFileName = firstFileName + ".docx";
        secondFileName = secondFileName + ".docx";

        // login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, firstFileName);

        // Retrieving first document version before changing it.
        String firstDocVersion = detailsPage.getDocumentVersion();

        // Opening google doc through sign in with the google doc authentication details
        EditInGoogleDocsPage googleDocsPage = signIntoEditGoogleDocFromDetailsPage(drone);

        // Select BackToAlfresco
        detailsPage = (DocumentDetailsPage) googleDocsPage.selectBackToShare();
        detailsPage.render();

        Assert.assertFalse(detailsPage.isEditInGoogleDocsLinkVisible());

        // Back to Document Library Page
        ShareUser.openDocumentLibrary(drone);

        detailsPage = ShareUser.openDocumentDetailPage(drone, secondFileName);

        // Retrieving first document version before changing it.
        String secondDocVersion = detailsPage.getDocumentVersion();

        // Opening google doc
        googleDocsPage = openEditGoogleDocFromDetailsPage(drone);

        // Select BackToAlfresco
        detailsPage = (DocumentDetailsPage) googleDocsPage.selectBackToShare();
        detailsPage.render();

        Assert.assertFalse(detailsPage.isEditInGoogleDocsLinkVisible());
        
        // Back to Document Library Page
        ShareUser.openDocumentLibrary(drone);

        detailsPage = ShareUser.openDocumentDetailPage(drone, firstFileName);
        detailsPage.render();

        Assert.assertFalse(detailsPage.isEditInGoogleDocsLinkVisible());

        Assert.assertTrue(detailsPage.isResumeEditingInGoogleDocsLinkVisible());

        // Select Resume Edint In Google Docs Link
        googleDocsPage = detailsPage.resumeEditInGoogleDocs().render();
        googleDocsPage.render();

        // Save the first document
        detailsPage = (DocumentDetailsPage) saveGoogleDoc(drone, false);
        detailsPage.render();

        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
        Assert.assertNotEquals(detailsPage.getDocumentVersion(), firstDocVersion);

        // Back to Document Library Page
        ShareUser.openDocumentLibrary(drone);

        detailsPage = ShareUser.openDocumentDetailPage(drone, secondFileName);

        Assert.assertFalse(detailsPage.isEditInGoogleDocsLinkVisible());

        Assert.assertTrue(detailsPage.isResumeEditingInGoogleDocsLinkVisible());

        // Select Resume Edint In Google Docs Link
        googleDocsPage = detailsPage.resumeEditInGoogleDocs().render();
        googleDocsPage.render();

        // Save the first document
        detailsPage = (DocumentDetailsPage) saveGoogleDoc(drone, false);
        detailsPage.render();

        Assert.assertTrue(detailsPage.isDocumentDetailsPage());

        Assert.assertNotEquals(detailsPage.getDocumentVersion(), secondDocVersion);
    }

    /**
     * DataPreparation method - ALF-1948
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Add 2 documents to the site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_1948() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUsernInfo = new String[] { testUser };
        String firstFileName = getFileName(testName + "3");
        String secondFileName = getFileName(testName + "2");

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUsernInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Creating and saving google doc through sign in with the google doc authentication details
            createAndSavegoogleDocBySignIn(drone, firstFileName, ContentType.GOOGLEDOCS);

            // Deleting cookies
            ShareUser.deleteSiteCookies(drone, googleURL);
            ShareUser.deleteSiteCookies(drone, googlePlusURL);

            // Creating and saving google doc through sign in with the google doc authentication details
            createAndSavegoogleDocBySignIn(drone, secondFileName, ContentType.GOOGLEDOCS);
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
     * Test - ALF-1948:Check editing two different documents in one browser in different tabs
     * <ul>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select first document</li>
     * <li>Open in google docs</li>
     * <li>Login as Site Admin in another tab</li>
     * <li>Navigate to doclib</li>
     * <li>Open in doc in google docs</li>
     * <li>Open Second doc</li>
     * <li>Edit and save the two documents alternatively in two browsers</li>
     * <li>verify save is successful</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_1948() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String firstFileName = getFileName(testName + "3");
        String secondFileName = getFileName(testName + "2");
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String firstDocVersion = null;
        String secondDocVersion = null;

        firstFileName = firstFileName + ".docx";
        secondFileName = secondFileName + ".docx";

        // login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, firstFileName);

        // Retrieving first document version before changing it.
        firstDocVersion = detailsPage.getDocumentVersion();

        // Opening google doc through sign in with the google doc authentication details
        signIntoEditGoogleDocFromDetailsPage(drone);

        String mainWindow = drone.getWindowHandle();

        // Getting second drone: Another tab in the same browser
        drone.createNewTab();

        // login with.
        drone.navigateTo(dronePropertiesMap.get(drone).getShareUrl());

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage secondDetailsPage = ShareUser.openDocumentDetailPage(drone, secondFileName);

        // Retrieving second document version before changing it.
        secondDocVersion = secondDetailsPage.getDocumentVersion();

        // Opening google doc through sign in with the google doc authentication details
        openEditGoogleDocFromDetailsPage(drone);

        // Saving the second document changes in second browser
        GoogleDocsUpdateFilePage googleUpdatefile2 = saveGoogleDocWithVersionAndComment(drone, "Second Comments", true);
        googleUpdatefile2.render();
        detailsPage = googleUpdatefile2.submit().render();

        // Back to the main tab.
        drone.closeTab();
        drone.switchToWindow(mainWindow);

        // Saving the first document changes in first browser
        GoogleDocsUpdateFilePage googleUpdatefile1 = saveGoogleDocWithVersionAndComment(drone, "First Comments", true);
        detailsPage = googleUpdatefile1.submit().render();

        // Open Site Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        detailsPage = ShareUser.openDocumentDetailPage(drone, firstFileName);
        
        Assert.assertNotSame("First Comments", detailsPage.getCommentsOfLastCommit());
        Assert.assertNotSame(firstDocVersion, detailsPage.getDocumentVersion());

        ShareUser.openDocumentLibrary(drone);

        detailsPage = ShareUser.openDocumentDetailPage(drone, secondFileName);
        Assert.assertNotSame("Second Comments", detailsPage.getCommentsOfLastCommit());
        Assert.assertNotSame(secondDocVersion, detailsPage.getDocumentVersion());

        ShareUser.logout(drone);
    }
    
    /**
     * DataPreparation method - ALF-15112
     * <ul>
     * <li>Create 1 User</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_15112() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUsernInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUsernInfo);
    }

    /**
     * Test - Enterprise40x-15112:Site Dashboard - Create Google Docs™ document.
     * This test combination of below tests:
     * Enterprise40x-15112:Site Dashboard - Create Google Docs™ document
     * Enterprise40x-15113:Site Dashboard - Update Google Docs™ document
     * Enterprise40x-15115:User Dashboard - Create Google Docs™ document
     * Enterprise40x-15116:User Dashboard - Update Google Docs™ document
     * <ul>
     * <li>User login</li>
     * <li>Create Site</li>
     * <li>Open Document library page</li>
     * <li>Create google docs</li>
     * <li>Edit the google docs</li>
     * <li>Verify the activities in Site and User dashboard</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_15112() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String fileName = getFileName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String docVersion = "";
        String modifiedDocVersion = "";
        
        fileName = fileName + ".docx";

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        createAndSavegoogleDocBySignIn(drone, fileName, ContentType.GOOGLEDOCS);
        
        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        docVersion = detailsPage.getDocumentVersion();

        // Opening google doc through sign in with the google doc authentication details
        EditInGoogleDocsPage googleDocsPage = openEditGoogleDocFromDetailsPage(drone);
        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());
        
        // Saving the second document changes in second browser
        GoogleDocsUpdateFilePage googleUpdatefile = saveGoogleDocWithVersionAndComment(drone, "Comments", true);
        detailsPage = googleUpdatefile.submit().render();
        
        modifiedDocVersion = detailsPage.getDocumentVersion();

        Assert.assertNotEquals(docVersion, modifiedDocVersion);
 
        // User DashBoard Activities
        ShareUser.openUserDashboard(drone);

        // Check activity feed on: User DashBoard: Content Added
        String activityEntry = testUser + " LName" + FEED_CONTENT_ADDED + FEED_FOR_FILE + fileName + FEED_LOCATION + siteName;

        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true));
        
        // Check activity feed on: User DashBoard: Content Updated        
        activityEntry = testUser + " LName" + FEED_CONTENT_UPDATED + FEED_FOR_FILE + fileName + FEED_LOCATION + siteName;

        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true));
        
        // Site DashBoard Activities
        ShareUser.openSiteDashboard(drone, siteName);

        // Check activity feed on: Site DashBoard: Content Added
        activityEntry = testUser + " LName" + FEED_CONTENT_ADDED + FEED_FOR_FILE + fileName;
        
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION));

        // Check activity feed on: Site DashBoard: Content Updated
        activityEntry = testUser + " LName" + FEED_CONTENT_UPDATED + FEED_FOR_FILE + fileName;
        
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION));
        
        // Deleting the test data.
        SiteUtil.deleteSite(drone, siteName);
    }
    
    /**
     * DataPreparation method - Enterprise40x-15114:Site Dashboard - Update non-Google Docs™ document
     * <ul>
     * <li>Create 1 User</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepGoogleDocs"}, timeOut = 400000)
    public void dataPrep_GoogleDocs_ALF_15114() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUsernInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUsernInfo);
    }

    /**
     * Test - Enterprise40x-15114:Site Dashboard - Update non-Google Docs™ document.
     * This test combination of below tests:
     * Enterprise40x-15114:Site Dashboard - Update non-Google Docs™ document
     * Enterprise40x-15117:User Dashboard - Update non-Google Docs™ document
     * <ul>
     * <li>User login</li>
     * <li>Create Site</li>
     * <li>Open Document library page</li>
     * <li>Add document to the site</li>
     * <li>Edit document in the google docs successfully</li>
     * <li>Verify the activities in Site and User dashboard</li>
     * </ul>
     * 
     * @throws Exception
     */
    @SuppressWarnings("unused")
    @Test(groups="GoogleDocs", timeOut = 400000)
    public void ALF_15114() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = TEST_DOCX_FILE;
        String docVersion = "";
        String modifiedDocVersion = "";
        
        String[] fileInfo = {fileName};

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, ShareUser.SITE_VISIBILITY_PUBLIC);
        
        ShareUser.uploadFileInFolder(drone, fileInfo);

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        docVersion = detailsPage.getDocumentVersion();

        // Opening google doc through sign in with the google doc authentication details
        EditInGoogleDocsPage googleDocsPage = signIntoEditGoogleDocFromDetailsPage(drone);
        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible());
        
        // Saving the second document changes in second browser
        GoogleDocsUpdateFilePage googleUpdatefile = saveGoogleDocWithVersionAndComment(drone, "Comments", true);
        detailsPage = googleUpdatefile.submit().render();
        
        // User DashBoard Activities
        ShareUser.openUserDashboard(drone);

        // Check activity feed on: User DashBoard: Content Updated  
        String activityEntry = testUser + " LName" + FEED_CONTENT_UPDATED + FEED_FOR_FILE + fileName + FEED_LOCATION + siteName;

        Assert.assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true));
        
        // Site DashBoard Activities
        ShareUser.openSiteDashboard(drone, siteName);
        
        // Check activity feed on: User DashBoard: Content Updated
        activityEntry = testUser + " LName" + FEED_CONTENT_UPDATED + FEED_FOR_FILE + fileName;
        
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION));
        
        // Deleting the test data.
        SiteUtil.deleteSite(drone, siteName);
    }
}