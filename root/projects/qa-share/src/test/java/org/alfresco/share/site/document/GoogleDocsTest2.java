/**
 *
 */
package org.alfresco.share.site.document;

import com.google.api.services.drive.Drive;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.util.Map;
import static org.alfresco.share.util.ShareUserGoogleDocs.createAndSavegoogleDocBySignIn;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne" }, timeOut = 60000)
public class GoogleDocsTest2 extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(GoogleDocsTest2.class);

    protected String testUser;
    protected String siteName = "";

    private static final String TEST_FILE_XLSX = "xlsx.xlsx";
    private static final String TEST_FILE_XLS = "xls.xls";
    private static final String TEST_FILE_TXT = "tab.txt";
    private static final String TEST_FILE_TSV = "tsv.tsv";
    private static final String TEST_FILE_RTF = "rtf.rtf";
    private static final String TEST_FILE_PPTX = "pptx.pptx";
    private static final String TEST_FILE_PPT = "ppt.ppt";
    private static final String TEST_FILE_PPS = "pps.pps";
    private static final String TEST_FILE_ODS = "ods_file.ods";
    private static final String TEST_FILE_DOCX = "docx.docx";
    private static final String TEST_FILE_DOC = "doc.doc";
    private static final String TEST_FILE_CSV = "csv.csv";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    private void prepare()
    {
        // Deleting cookies
        ShareUser.deleteSiteCookies(drone, ShareUserGoogleDocs.googleURL);
        ShareUser.deleteSiteCookies(drone, ShareUserGoogleDocs.googlePlusURL);

        logger.info("Deleted google cookies successfully.");
    }

    /**
     * Test - AONE-14615:Content that cannot be round tripped
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Upload documents(.doc, .xls or .ppt for example)</li>
     * <li>Open a .doc, .xls or .ppt document in Google Docs</li>
     * <li>Disagree with the document upgrade</li>
     * <li>You are returned to the Document Library</li>
     * <li>Agree with the document Upgrade</li>
     * <li>User continue to Google Docs editor and the document is upgraded</li>
     * <li>Rename document without file extension</li>
     * <li>Agree with the document Upgrade</li>
     * <li>File extension added automatically for the upgraded document.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "NonGrid", timeOut = 600000, alwaysRun = true)
    public void AONE_14615() throws Exception
    {
        prepare();

        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String[] testUserInfo = new String[] { testUser };
        String filename1 = "doc.doc";
        String newFilename1 = "doc.docx";
        String filename2 = "doc_new.doc";
        String tempFilename2 = "doc_new";
        String newFilename2 = "doc_new.docx";

        String fileName = "WordDocument.docx";

        String[] fileInfo1 = { filename1 };
        String[] fileInfo2 = { filename2 };
        String[] fileInfo3 = { fileName };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(drone, fileInfo1);
        ShareUser.uploadFileInFolder(drone, fileInfo2);
        ShareUser.uploadFileInFolder(drone, fileInfo3);

        // Open Site Library
        DocumentLibraryPage docLibPage;
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.openDocumentDetailPage(drone, fileName);

        // click 'Edit in Google Docs' for doc
        // Specify correct credentials and complete authentication
        ShareUserGoogleDocs.signIntoEditGoogleDocFromDetailsPage(drone);
        Thread.sleep(15000);

        // User authenticated successfully. Document is opened for editing.
        Assert.assertTrue(drone.getCurrentPage().render() instanceof EditInGoogleDocsPage,
                "After agree with the document upgrade user don't redirect to EditInGoogleDocsPage");

        ShareUserGoogleDocs.discardGoogleDocsChanges(drone).render();

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(filename1).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for " + filename1);

        DocumentDetailsPage detailsPage;
        detailsPage = ShareUser.openDocumentDetailPage(drone, filename1);

        detailsPage = detailsPage.editInGoogleDocsOldFormat(false).render();
        Assert.assertTrue(drone.getCurrentPage().render() instanceof DocumentDetailsPage,
                "After disagree with the document upgrade user don't redirect to DocumentDetailsPage");

        detailsPage.editInGoogleDocsOldFormat(true).render();

        ShareUserGoogleDocs.saveGoogleDoc(drone, false);
        detailsPage.render();

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertTrue(docLibPage.isFileVisible(newFilename1), "File " + newFilename1 + " isn't visible (format docx.). File isn't upgraded.");
        Assert.assertFalse(docLibPage.isFileVisible(filename1), "File " + filename1 + " is visible (format doc.). File isn't upgraded.");

        Assert.assertTrue(docLibPage.isFileVisible(filename2), "File " + filename2 + " isn't visible (format doc.).");
        detailsPage = docLibPage.selectFile(filename2);

        // Click on the Edit Properties icon in the Properties section
        EditDocumentPropertiesPage editDocumentPropertiesPage = detailsPage.selectEditProperties();

        // Try to rename file and click Save
        editDocumentPropertiesPage.setName(tempFilename2);
        editDocumentPropertiesPage.selectSaveWithValidation().render();
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertEquals(properties.get("Name"), tempFilename2, "File isn't renamed to " + tempFilename2);

        // Open Library
        ShareUser.openDocumentLibrary(drone);
        Assert.assertFalse(docLibPage.isFileVisible(filename2), "File " + filename2 + " is visible (format doc.). File wasn't renamed");
        Assert.assertTrue(docLibPage.isFileVisible(tempFilename2), "File " + tempFilename2 + " isn't visible (doc without extension). File wasn't renamed");

        detailsPage = ShareUser.openDocumentDetailPage(drone, tempFilename2);

        detailsPage.editInGoogleDocsOldFormat(true).render();

        ShareUserGoogleDocs.saveGoogleDoc(drone, false);
        detailsPage.render();

        ShareUser.openDocumentLibrary(drone);

        Assert.assertFalse(docLibPage.isFileVisible(tempFilename2), "File " + tempFilename2 + " is visible (xls without extension). File isn't upgraded.");
        Assert.assertTrue(docLibPage.isFileVisible(newFilename2), "File " + newFilename2 + " isn't visible (format docx.). It wasn't upgraded");

    }

    /**
     * Test - AONE-14611:Option to edit in Google Docs documents of supported MIME type
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Upload documents</li>
     * <li>For documents: .doc, .docx, plain text (.txt), .rtf
     * <li>For spreadsheets: .xls, .xlsx, .ods, .csv, .tsv</li>
     * <li>For presentations: .ppt, .pps, .pptx.</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>select added documents</li>
     * <li>Verify edit in Google Docs option should be present</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "NonGrid", timeOut = 600000, alwaysRun = true, dependsOnMethods = "AONE_14615")
    public void AONE_14611() throws Exception
    {
        prepare();

        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        String[] fileInfo1 = { TEST_FILE_XLSX };
        ShareUser.uploadFileInFolder(drone, fileInfo1);

        String[] fileInfo2 = { TEST_FILE_XLS };
        ShareUser.uploadFileInFolder(drone, fileInfo2);

        String[] fileInfo3 = { TEST_FILE_TXT };
        ShareUser.uploadFileInFolder(drone, fileInfo3);

        String[] fileInfo4 = { TEST_FILE_TSV };
        ShareUser.uploadFileInFolder(drone, fileInfo4);

        String[] fileInfo5 = { TEST_FILE_RTF };
        ShareUser.uploadFileInFolder(drone, fileInfo5);

        String[] fileInfo6 = { TEST_FILE_PPTX };
        ShareUser.uploadFileInFolder(drone, fileInfo6);

        String[] fileInfo7 = { TEST_FILE_PPT };
        ShareUser.uploadFileInFolder(drone, fileInfo7);

        String[] fileInfo8 = { TEST_FILE_PPS };
        ShareUser.uploadFileInFolder(drone, fileInfo8);

        String[] fileInfo9 = { TEST_FILE_ODS };
        ShareUser.uploadFileInFolder(drone, fileInfo9);

        String[] fileInfo10 = { TEST_FILE_DOCX };
        ShareUser.uploadFileInFolder(drone, fileInfo10);

        String[] fileInfo11 = { TEST_FILE_DOC };
        ShareUser.uploadFileInFolder(drone, fileInfo11);

        String[] fileInfo12 = { TEST_FILE_CSV };
        ShareUser.uploadFileInFolder(drone, fileInfo12);

        // Open Site Library
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verifying the EditInGoogleDocs link is present for the supported formats.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_XLSX).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_XLSX);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_XLS).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_XLS);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_TXT).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_TXT);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_TSV).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_TSV);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_RTF).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_RTF);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_PPTX).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_PPTX);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_PPT).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_PPT);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_PPS).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_PPS);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_ODS).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_ODS);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_DOCX).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_DOCX);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_DOC).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_DOC);
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(TEST_FILE_CSV).isEditInGoogleDocsPresent(), "The EditInGoogleDocs link is present for "
                + TEST_FILE_CSV);
    }

    /**
     * Test - AONE-14616:Changes are saved back to Alfresco
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Upload documents</li>
     * <li>For documents: plain text (.txt)</li>
     * <li>Open Site</li>
     * <li>Open Document library page</li>
     * <li>click on edit in google docs</li>
     * <li>document is successfully loaded into the Google Docs editor</li>
     * <li>Add line to the beginning of the document</li>
     * <li>Click on discard changes</li>
     * <li>The document should not contain any new line added in the library</li>
     * <li>Add line to the beginning of the document</li>
     * <li>Click the 'Save to Alfresco' button</li>
     * <li>Select "Minor changes" option and click "Confirm"</li>
     * <li>User are returned to the Document Library.</li>
     * <li>Open the document in Alfresco</li>
     * <li>Document contains the added line at the beginning.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "NonGrid", dependsOnMethods = "AONE_14611", timeOut = 600000, alwaysRun = true)
    public void AONE_14616() throws Exception
    {
        prepare();

        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String[] testUserInfo = new String[] { testUser };
        String filename1 = "tab.txt";
        String[] fileInfo1 = { filename1 };
        String text = "Edited in Google Docs and saved back to Alfresco";
        String newFileName = getRandomString(15) + ".txt";
        try
        {
            newFile(newFileName, text);

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.uploadFileInFolder(drone, fileInfo1);

            // Open Site Library
            ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage;
            ShareUser.openDocumentDetailPage(drone, filename1);

            // open editInGoogleDocs
            EditInGoogleDocsPage googleDocsPage = ShareUserGoogleDocs.signIntoEditGoogleDocFromDetailsPage(drone);

            // Check document is successfully loaded into the Google Docs editor
            Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible(), "The Google Docs editor isn't opened");

            // Add line to the beginning of the document
            googleDocsPage.render();

            webDriverWait(drone, 5000);
            Drive service;
            service = GoogleDriveUtil.getDriveService(serviceAccountEmail, googleUserName, serviceAccountPKCS12FilePath);

            String fileId = GoogleDriveUtil.getFileID(service, filename1);

            GoogleDriveUtil.updateFile(service, fileId, null, null, "text/plain", newFileName);

            webDriverWait(drone, 15000);
            // Discard changes and Navigate to alfresco
            detailsPage = ShareUserGoogleDocs.discardGoogleDocsChanges(drone).render();

            // The document should not contain any new line added in the library
            EditTextDocumentPage editPage = detailsPage.selectInlineEdit().render();
            ContentDetails contentDetails;
            contentDetails = editPage.getDetails();
            Assert.assertFalse(contentDetails.getContent().contains(text), "Document contain any new line added in the library");

            detailsPage = editPage.selectCancel().render();

            // open editInGoogleDocs
            googleDocsPage = detailsPage.editInGoogleDocs().render();

            // document is successfully loaded into the Google Docs editor
            Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible(), "The Google Docs editor isn't opened");

            webDriverWait(drone, 5000);

            // Add line to the beginning of the document
            service = GoogleDriveUtil.getDriveService(serviceAccountEmail, googleUserName, serviceAccountPKCS12FilePath);

            fileId = GoogleDriveUtil.getFileID(service, filename1);

            GoogleDriveUtil.updateFile(service, fileId, null, null, "text/plain", newFileName);

            webDriverWait(drone, 15000);

            // Save changes and Navigate to alfresco
            detailsPage = ShareUserGoogleDocs.saveGoogleDoc(drone, false).render();

            editPage = detailsPage.selectInlineEdit().render();
            contentDetails = editPage.getDetails();

            // Document contains the added line at the beginning.
            Assert.assertTrue(contentDetails.getContent().contains(text), "Document isn't contain any new line added in the library");
        }
        finally
        {
            FtpUtil.RemoveLocalFile(newFileName);
        }

    }

    /**
     * Test - AONE-14622:Editing document. Log out from Google Docs
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Upload documents</li>
     * <li>Click 'Edit in Google Docs' for the uploaded doc</li>
     * <li>Document is opened for editing</li>
     * <li>While editing document click on the username and click 'Sign out'</li>
     * <li>Return to Alfresco and verify that the uploaded doc will be locked</li>
     * <li>Click 'Edit in Google Docs' for the uploaded doc</li>
     * <li>Specify correct credentials and complete authentication</li>
     * <li>User authenticated successfully. Document is opened for editing.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "NonGrid", dependsOnMethods = "AONE_14616", timeOut = 600000, alwaysRun = true)
    public void AONE_14622() throws Exception
    {
        prepare();

        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String[] testUserInfo = new String[] { testUser };
        String filename1 = "tab.txt";
        String filename2 = "temp_file.docx";
        String[] fileInfo1 = { filename1 };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // User login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Creating and saving google doc through sign in with the google doc authentication details
        DocumentLibraryPage docLibPage;
        createAndSavegoogleDocBySignIn(drone, filename2, ContentType.GOOGLEDOCS);

        ShareUser.uploadFileInFolder(drone, fileInfo1);

        // Open Site Library

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        String url = getShareUrl();

        ShareUser.openDocumentDetailPage(drone, filename1);

        // open editInGoogleDocs.
        EditInGoogleDocsPage googleDocsPage = ShareUserGoogleDocs.openEditGoogleDocFromDetailsPage(drone);

        // document is successfully loaded into the Google Docs editor
        Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible(), "The Google Docs editor isn't opened");

        googleDocsPage.render();

        // click 'Sign out'
        prepare();

        // Return to Alfresco
        drone.navigateTo(url);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        docLibPage = drone.getCurrentPage().render();

        // Verify that the uploaded doc will be locked
        Assert.assertEquals(docLibPage.getFileDirectoryInfo(filename1).getContentInfo(), "This document is locked by you.", "File " + filename1
                + " isn't locked");

        ShareUser.openDocumentDetailPage(drone, filename1);

        // click 'Edit in Google Docs' for doc
        // Specify correct credentials and complete authentication
        ShareUserGoogleDocs.signIntoResumeEditGoogleDocFromDetailsPage(drone);

        // User authenticated successfully. Document is opened for editing.
        Assert.assertTrue(drone.getCurrentPage().render() instanceof EditInGoogleDocsPage,
                "After agree with the document upgrade user don't redirect to EditInGoogleDocsPage");

        ShareUserGoogleDocs.discardGoogleDocsChanges(drone).render();

    }

    /**
     * Test - AONE-14637:Check possibility to share document
     * <ul>
     * <li>Any site is created</li>
     * <li>Any supported document is uploaded to the site</li>
     * <li>Document is opened for editing in Google Docs editor</li>
     * <li>To the 'Add people' field enter valid email and click 'Done' button</li>
     * <li>Check inbox for the user who was invited.</li>
     * <li>Rename document and save changes</li>
     * <li>As a document creator save changes made by the invited user and check that all changes are present</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "NonGrid", timeOut = 600000, alwaysRun = true)
    public void AONE_14637() throws Exception
    {
        prepare();

        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String[] testUserInfo = new String[] { testUser };
        String filename1 = getRandomString(15) + ".txt";
        String newFileName = getRandomString(15) + ".txt";
        String[] fileInfo1 = { filename1 };

        try
        {
            newFile(newFileName, newFileName);

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Any site is created
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Any supported document is uploaded to the site
            ShareUser.uploadFileInFolder(drone, fileInfo1);

            // Open Site Library
            ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage;
            ShareUser.openDocumentDetailPage(drone, filename1);

            // open editInGoogleDocs
            EditInGoogleDocsPage googleDocsPage = ShareUserGoogleDocs.signIntoEditGoogleDocFromDetailsPage(drone);

            // Check document is successfully loaded into the Google Docs editor
            Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible(), "The Google Docs editor isn't opened");

            // Document is opened for editing in Google Docs editor
            googleDocsPage.render();

            webDriverWait(drone, 5000);
            // Browse to your mail box and open the document through Drive tab
            Drive service;
            service = GoogleDriveUtil.getDriveService(serviceAccountEmail, googleUserName, serviceAccountPKCS12FilePath);

            String fileId = GoogleDriveUtil.getFileID(service, filename1);

            // To the 'Add people' field enter valid email and click 'Done' button.
            GoogleDriveUtil.insertPermission(service, fileId, googleDriveUserName, "user", "writer");

            webDriverWait(drone, 5000);

            // Rename document and save changes.
            Drive service2;
            service2 = GoogleDriveUtil.getDriveService(serviceDriveAccountEmail, googleDriveUserName, serviceDriveAccountPKCS12FilePath);

            String fileId2 = GoogleDriveUtil.getFileID(service, filename1);

            GoogleDriveUtil.updateFile(service2, fileId2, newFileName, null, "text/plain", newFileName);

            webDriverWait(drone, 15000);
            // Save changes and Navigate to alfresco
            ShareUserGoogleDocs.saveGoogleDocOtherEditor(drone, false).render();

            // Open Site Library
            ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Document is renamed and all changes are present
            detailsPage = ShareUser.openDocumentDetailPage(drone, newFileName);

            EditTextDocumentPage editPage = detailsPage.selectInlineEdit().render();
            ContentDetails contentDetails;
            contentDetails = editPage.getDetails();

            // Document is renamed and all changes are present
            Assert.assertTrue(contentDetails.getContent().contains(newFileName), "Document isn't renamed and all changes aren't present");
        }
        finally
        {
            FtpUtil.RemoveLocalFile(filename1);
            FtpUtil.RemoveLocalFile(newFileName);
        }

    }

    /**
     * Test - AONE-14639:Discarding other person's changes
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Any supported document is uploaded to the site</li>
     * <li>Document is opened for editing in Google Docs editor</li>
     * <li>To the 'Add people' field enter valid email and click 'Done' button</li>
     * <li>Check inbox for the user who was invited.</li>
     * <li>Rename document and save changes</li>
     * <li>As document creator click 'Discard Changes' button</li>
     * <li>Changes made by invited person are NOT present</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = "NonGrid", timeOut = 600000, alwaysRun = true)
    public void AONE_14639() throws Exception
    {
        prepare();

        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String[] testUserInfo = new String[] { testUser };
        String filename1 = getRandomString(15) + ".txt";
        String newFileName = getRandomString(15) + ".txt";
        String[] fileInfo1 = { filename1 };

        try
        {
            newFile(newFileName, newFileName);

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // User login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.uploadFileInFolder(drone, fileInfo1);

            // Open Site Library
            ShareUser.openSitesDocumentLibrary(drone, siteName);

            DocumentDetailsPage detailsPage;
            ShareUser.openDocumentDetailPage(drone, filename1);

            // open editInGoogleDocs
            EditInGoogleDocsPage googleDocsPage = ShareUserGoogleDocs.signIntoEditGoogleDocFromDetailsPage(drone);

            // Check document is successfully loaded into the Google Docs editor
            Assert.assertTrue(googleDocsPage.isGoogleDocsIframeVisible(), "The Google Docs editor isn't opened");

            webDriverWait(drone, 5000);

            // Document is opened for editing in Google Docs editor
            googleDocsPage.render();

            Drive service;
            service = GoogleDriveUtil.getDriveService(serviceAccountEmail, googleUserName, serviceAccountPKCS12FilePath);

            String fileId = GoogleDriveUtil.getFileID(service, filename1);

            GoogleDriveUtil.insertPermission(service, fileId, googleDriveUserName, "user", "writer");

            Drive service2;
            service2 = GoogleDriveUtil.getDriveService(serviceDriveAccountEmail, googleDriveUserName, serviceDriveAccountPKCS12FilePath);

            String fileId2 = GoogleDriveUtil.getFileID(service, filename1);

            // Rename document by invited person
            GoogleDriveUtil.updateFile(service2, fileId2, newFileName, null, "text/plain", newFileName);

            webDriverWait(drone, 15000);
            // As document creator click 'Discard Changes' button
            logger.info("discard Google Docs Changes for other editor");
            ShareUserGoogleDocs.discardGoogleDocsChangesOtherEditor(drone).render();

            drone.getCurrentPage().render();
            // // Open Site Library
            // ShareUser.openSitesDocumentLibrary(drone, siteName);
            // detailsPage = ShareUser.openDocumentDetailPage(drone, filename1);
            detailsPage = drone.getCurrentPage().render();

            EditTextDocumentPage editPage = detailsPage.selectInlineEdit().render();
            ContentDetails contentDetails;
            contentDetails = editPage.getDetails();

            // Changes made by invited person are NOT present
            Assert.assertFalse(contentDetails.getContent().contains(newFileName), "Changes made by invited person are present. File " + newFileName
                    + "is presented");
            Assert.assertTrue(contentDetails.getContent().contains(filename1), "Changes made by invited person are present. File " + filename1
                    + "isn't presented");
        }
        finally
        {
            FtpUtil.RemoveLocalFile(filename1);
            FtpUtil.RemoveLocalFile(newFileName);
        }

    }

}