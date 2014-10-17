package org.alfresco.share.enterprise.repository.fileprotocols.cifs;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.alfresco.application.util.Application;
import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.alfresco.windows.application.MicorsoftOffice2010;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

@Listeners(FailedTestListener.class)
public class CifsMSWord2010Tests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(CifsMSWord2010Tests.class);

    private String testName;
    private String testUser;
    private String siteName;
    private String docxFileName_6269;
    private String fileType = ".docx";

    private static DocumentLibraryPage documentLibPage;
    private static final String CIFS_LOCATION = "cifs";
    public String officePath;
    WindowsExplorer explorer = new WindowsExplorer();
    MicorsoftOffice2010 word = new MicorsoftOffice2010(Application.WORD, "2013");
    String mapConnect;
    String networkDrive;
    String networkPath;
    String cifsPath;
    String docFileName_6265;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        siteName = getSiteName(testName);

        docxFileName_6269 = "SaveCIFS";

        docFileName_6265 = "AONE-6265.docx";

        Properties officeAppProperty = new Properties();
        officeAppProperty.load(this.getClass().getClassLoader().getResourceAsStream("office-application.properties"));
        cifsPath = officeAppProperty.getProperty("cifs.path");

        networkDrive = officeAppProperty.getProperty("network.map.driver");
        networkPath = officeAppProperty.getProperty("network.map.path");
        mapConnect = "net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + testUser + " " + DEFAULT_PASSWORD;

        // create user
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        Runtime.getRuntime().exec(mapConnect);
        logger.info("Mapping succesfull " + testUser);

    }

    @AfterClass
    public void unmapDrive() throws Exception
    {
        Runtime.getRuntime().exec("net use " + networkDrive + " /d");
        logger.info("Unmapping succesfull " + testUser);
    }

    @Test(groups = { "DataPrepExcel" })
    public void dataPrep_AONE() throws Exception
    {

        ShareUser.login(drone, testUser);

        // --- Step 2 ---
        // --- Step action ---
        // Any site is created;
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        // --- Step 3 ---
        // --- Step action ---
        // CIFS is mapped as a network drive as a site manager user; -- already performed manually
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

    }

    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6265() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + docFileName_6265);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_6265);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6265() throws IOException
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).toLowerCase();
        String first_modification = testName + "1";
        String second_modification = testName + "2";
        String last_modification = testName + "3";

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        try
        {
            // ---- Step 1 ----
            // ---- Step Action -----
            // Open .docx document for editing.
            // The document is opened in write mode.
            Ldtp ldtp = word.openFileFromCMD(fullPath, docFileName_6265, testUser, DEFAULT_PASSWORD);

            // ---- Step 2 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            word.editOffice(ldtp, " " + first_modification + " ");

            // ---- Step 3 ----
            // ---- Step Action -----
            // Save the document (Ctrl+S, for example) and close it
            // Expected Result
            // The document is saved. No errors occur in UI and in the log. No tmp files are left.
            word.saveOffice(ldtp);
            ldtp.waitTime(2);
            word.exitOfficeApplication(ldtp);

            int nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 4 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_6265);

            EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
            editPropertiesPage.getDocumentTitle();
            Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
            editPropertiesPage.clickCancel();

            // ---- Step 5 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly.
            String body = detailsPage.getDocumentBody();
            Assert.assertTrue(body.contains(first_modification));

            // ---- Step 6 ----
            // ---- Step Action -----
            // 6. Open the document for editing again.
            // Expected Result
            // 6. The document is opened in write mode.
            ldtp = word.openFileFromCMD(fullPath, docFileName_6265, testUser, DEFAULT_PASSWORD);

            // ---- Step 7 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            word.editOffice(ldtp, " " + second_modification + " ");

            // ---- Step 8 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            word.saveOffice(ldtp);
            ldtp.waitTime(2);
            word.exitOfficeApplication(ldtp);

            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 9 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(docFileName_6265);

            editPropertiesPage = detailsPage.selectEditProperties().render();
            editPropertiesPage.getDocumentTitle();
            Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
            editPropertiesPage.clickCancel();

            // ---- Step 10 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly.
            body = detailsPage.getDocumentBody();
            Assert.assertTrue(body.contains(second_modification));

            // ---- Step 11 ----
            // ---- Step Action -----
            // 6. Open the document for editing again.
            // Expected Result
            // 6. The document is opened in write mode.
            ldtp = word.openFileFromCMD(fullPath, docFileName_6265, testUser, DEFAULT_PASSWORD);

            // ---- Step 12 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            word.editOffice(ldtp, " " + last_modification + " ");

            // ---- Step 13 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            word.saveOffice(ldtp);
            ldtp.waitTime(2);
            word.exitOfficeApplication(ldtp);

            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 14 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(docFileName_6265);

            editPropertiesPage = detailsPage.selectEditProperties().render();
            editPropertiesPage.getDocumentTitle();
            Assert.assertTrue(editPropertiesPage.getDocumentTitle().equals(testName));
            editPropertiesPage.clickCancel();

            // ---- Step 15 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly.
            body = detailsPage.getDocumentBody();
            Assert.assertTrue(body.contains(last_modification));

        }
        catch (Exception e)
        {
            throw new LdtpExecutionError("Not working");
        }

    }

    /** AONE-6269:MS Word 2010 - saved into CIFS */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6269() throws Exception
    {

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;
        String localPath = DATA_FOLDER + CIFS_LOCATION + SLASH;

        // --- Step 1 ---
        // --- Step action ---
        // Save a document into the CIFS drive (into the Document Library space) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp files are left.

        int noOfFilesBeforeSave = getNumberOfFilesFromPath(fullPath);
        Ldtp l1 = word.openFileFromCMD(localPath, docxFileName_6269 + fileType, testUser, DEFAULT_PASSWORD);

        // Ldtp l2 = word.openFileFromCMD(fullPath, docxFileName_6269+fileType, testUser, DEFAULT_PASSWORD);
        l1 = word.getAbstractUtil().setOnWindow(docxFileName_6269);
        word.goToFile(l1);
        l1.waitTime(1);
        word.getAbstractUtil().clickOnObject(l1, "SaveAs");

        word.operateOnSaveAsWithFullPath(l1, fullPath, docxFileName_6269, testUser, DEFAULT_PASSWORD);

        int noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, noOfFilesBeforeSave, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + noOfFilesBeforeSave);

    }

    private int getNumberOfFilesFromPath(String path)
    {
        int noOfFiles = 0;
        File folder = new File(path);
        noOfFiles = folder.listFiles().length;

        return noOfFiles;
    }

}
