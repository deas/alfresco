package org.alfresco.share.enterprise.repository.fileprotocols.cifs;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.alfresco.application.util.Application;
import org.alfresco.explorer.WindowsExplorer;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.CifsUtil;
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
public class CifsMSOffice2010Tests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(CifsMSOffice2010Tests.class);

    private String testName;
    private String testUser;
    private String siteName;
    
    private String fileTypeDoc = ".docx";
    private String fileTypeExcel = ".xlsx";
    private String fileTypePowerPoint = ".pptx";


    private static DocumentLibraryPage documentLibPage;
    private static final String CIFS_LOCATION = "cifs";
    public String officePath;
    WindowsExplorer explorer = new WindowsExplorer();
    
    MicorsoftOffice2010 word = new MicorsoftOffice2010(Application.WORD, "2013");
    MicorsoftOffice2010 excel = new MicorsoftOffice2010(Application.EXCEL, "2010");
    MicorsoftOffice2010 power = new MicorsoftOffice2010(Application.POWERPOINT, "2010");
    
    String mapConnect;
    String networkDrive;
    String networkPath;
    String cifsPath;
    String docFileName_6265;
    String docFileName_6266;
    String docxFileName_6269;
    String fileName_6271;
    String fileName_6272;
    String fileName_6277;
    String fileName_6278;
    
    String image_1 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic1.jpg";
    String image_2 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic2.jpg";
    String image_3 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic3.jpg";

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        siteName = getSiteName(testName);

        // word files
        docxFileName_6269 = "SaveCIFS";
        docFileName_6265 = "AONE-6265";
        docFileName_6266 = "AONE-6266";
        
        // excel files
        fileName_6271 = "AONE-6271";
        fileName_6272 = "AONE-6272";
        
        // power point files
        fileName_6277 = "AONE-6277";
        fileName_6278 = "AONE-6278";

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
        String testName = getTestName() + "11";
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + docFileName_6265 + fileTypeDoc);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_6265 + fileTypeDoc);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6265() throws IOException
    {
        String testName = getTestName() + "10";
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
            Ldtp ldtp = word.openFileFromCMD(fullPath, docFileName_6265 + fileTypeDoc, testUser, DEFAULT_PASSWORD);

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
            DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_6265 + fileTypeDoc);

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
            ldtp = word.openFileFromCMD(fullPath, docFileName_6265 + fileTypeDoc, testUser, DEFAULT_PASSWORD);

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
            detailsPage = documentLibPage.selectFile(docFileName_6265 + fileTypeDoc);

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
            ldtp = word.openFileFromCMD(fullPath, docFileName_6265 + fileTypeDoc, testUser, DEFAULT_PASSWORD);

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
            detailsPage = documentLibPage.selectFile(docFileName_6265 + fileTypeDoc);

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
      
    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6266() throws Exception
    {
        String testName = getTestName() + "5";
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + docFileName_6266 + fileTypeDoc);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_6266 + fileTypeDoc);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6266() throws IOException
    {
        String testName = getTestName() + "5";
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
            Ldtp ldtp = word.openFileFromCMD(fullPath, docFileName_6266 + fileTypeDoc, testUser, DEFAULT_PASSWORD);

            // ---- Step 2 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.                  
            uploadImageInOffice(image_1);
            word.editOffice(ldtp, " " + first_modification + " ");

            // ---- Step 3 ----
            // ---- Step Action -----
            // Save the document (Ctrl+S, for example) and close it
            // Expected Result
            // The document is saved. No errors occur in UI and in the log. No tmp files are left.
            word.saveOffice(ldtp);
            ldtp.waitTime(5);
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
            DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_6266 + fileTypeDoc);

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
            ldtp = word.openFileFromCMD(fullPath, docFileName_6266 + fileTypeDoc, testUser, DEFAULT_PASSWORD);

            // ---- Step 7 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            uploadImageInOffice(image_2);          
            word.editOffice(ldtp, " " + second_modification + " ");

            // ---- Step 8 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            word.saveOffice(ldtp);
            ldtp.waitTime(5);
            word.exitOfficeApplication(ldtp);

            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 9 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(docFileName_6266 + fileTypeDoc);

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
            ldtp = word.openFileFromCMD(fullPath, docFileName_6266 + fileTypeDoc, testUser, DEFAULT_PASSWORD);

            // ---- Step 12 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            uploadImageInOffice(image_3);
            word.editOffice(ldtp, " " + last_modification + " ");

            // ---- Step 13 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            word.saveOffice(ldtp);
            ldtp.waitTime(5);
            word.exitOfficeApplication(ldtp);

            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 14 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(docFileName_6266 + fileTypeDoc);

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
    
    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6271() throws Exception
    {
        String testName = getTestName() + "3";
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + fileName_6271 + fileTypeExcel);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6271 + fileTypeExcel);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6271() throws IOException
    {
        String testName = getTestName() + "3";
        String siteName = getSiteName(testName).toLowerCase();
        String first_modification = testName + "1";
        String second_modification = testName + "2";
        String last_modification = testName + "3";

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        try
        {
            // ---- Step 1 ----
            // ---- Step Action -----
            // Open .xlsx document for editing.
            // The document is opened in write mode.
            Ldtp ldtp = excel.openFileFromCMD(fullPath, fileName_6271 + fileTypeExcel, testUser, DEFAULT_PASSWORD);

            // ---- Step 2 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.        
            excel.editOffice(ldtp, " " + first_modification + " ");

            // ---- Step 3 ----
            // ---- Step Action -----
            // Save the document (Ctrl+S, for example) and close it
            // Expected Result
            // The document is saved. No errors occur in UI and in the log. No tmp files are left.
            excel.saveOffice(ldtp);
            ldtp.waitTime(3);
            excel.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);

            int nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 4 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6271 + fileTypeExcel);

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
            ldtp = excel.openFileFromCMD(fullPath, fileName_6271 + fileTypeExcel, testUser, DEFAULT_PASSWORD);

            // ---- Step 7 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            excel.editOffice(ldtp, " " + second_modification + " ");

            // ---- Step 8 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            excel.saveOffice(ldtp);
            ldtp.waitTime(2);
            excel.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);
            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 9 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(fileName_6271 + fileTypeExcel);

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
            ldtp = excel.openFileFromCMD(fullPath, fileName_6271 + fileTypeExcel, testUser, DEFAULT_PASSWORD);

            // ---- Step 12 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            excel.editOffice(ldtp, " " + last_modification + " ");

            // ---- Step 13 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            excel.saveOffice(ldtp);
            ldtp.waitTime(2);
            excel.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);
            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 14 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(fileName_6271 + fileTypeExcel);

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
      
    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6272() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + fileName_6272 + fileTypeExcel);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6272 + fileTypeExcel);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6272() throws IOException
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
            // Open .xlsx document for editing.
            // The document is opened in write mode.
            Ldtp ldtp = excel.openFileFromCMD(fullPath, fileName_6272 + fileTypeExcel, testUser, DEFAULT_PASSWORD);

            // ---- Step 2 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.   
            uploadImageInOffice(image_1);
            excel.editOffice(ldtp, " " + first_modification + " ");

            // ---- Step 3 ----
            // ---- Step Action -----
            // Save the document (Ctrl+S, for example) and close it
            // Expected Result
            // The document is saved. No errors occur in UI and in the log. No tmp files are left.
            excel.saveOffice(ldtp);
            ldtp.waitTime(3);
            excel.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);

            int nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 4 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6272 + fileTypeExcel);

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
            ldtp = excel.openFileFromCMD(fullPath, fileName_6272 + fileTypeExcel, testUser, DEFAULT_PASSWORD);

            // ---- Step 7 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            uploadImageInOffice(image_2);
            excel.editOffice(ldtp, " " + second_modification + " ");

            // ---- Step 8 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            excel.saveOffice(ldtp);
            ldtp.waitTime(2);
            excel.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);
            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 9 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(fileName_6272 + fileTypeExcel);

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
            ldtp = excel.openFileFromCMD(fullPath, fileName_6272 + fileTypeExcel, testUser, DEFAULT_PASSWORD);

            // ---- Step 12 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            uploadImageInOffice(image_3);
            excel.editOffice(ldtp, " " + last_modification + " ");

            // ---- Step 13 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            excel.saveOffice(ldtp);
            ldtp.waitTime(2);
            excel.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);
            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 14 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(fileName_6272 + fileTypeExcel);

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
    
    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6277() throws Exception
    {
        String testName = getTestName() + "6";
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + fileName_6277 + fileTypePowerPoint);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6277 + fileTypePowerPoint);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6277() throws IOException
    {
        String testName = getTestName() + "6";
        String siteName = getSiteName(testName).toLowerCase();
        String first_modification = testName + "1";
        String second_modification = testName + "2";
        String last_modification = testName + "3";

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        try
        {
            // ---- Step 1 ----
            // ---- Step Action -----
            // Open .pptx document for editing.
            // The document is opened in write mode.
            Ldtp ldtp = power.openFileFromCMD(fullPath, fileName_6277 + fileTypePowerPoint, testUser, DEFAULT_PASSWORD);

            // ---- Step 2 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.   
            ldtp = power.getAbstractUtil().setOnWindow(fileName_6277); 
            ldtp.click("paneSlide");
            power.editOffice(ldtp, " " + first_modification + " ");

            // ---- Step 3 ----
            // ---- Step Action -----
            // Save the document (Ctrl+S, for example) and close it
            // Expected Result
            // The document is saved. No errors occur in UI and in the log. No tmp files are left.
            power.saveOffice(ldtp);
            ldtp.waitTime(3);
            power.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);

            int nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 4 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6277 + fileTypePowerPoint);

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
            ldtp = power.openFileFromCMD(fullPath, fileName_6277 + fileTypePowerPoint, testUser, DEFAULT_PASSWORD);

            // ---- Step 7 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            ldtp = power.getAbstractUtil().setOnWindow(fileName_6277);
            power.editOffice(ldtp, " " + second_modification + " ");

            // ---- Step 8 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            power.saveOffice(ldtp);
            ldtp.waitTime(2);
            power.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);
            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 9 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(fileName_6277 + fileTypePowerPoint);

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
            ldtp = power.openFileFromCMD(fullPath, fileName_6277 + fileTypePowerPoint, testUser, DEFAULT_PASSWORD);

            // ---- Step 12 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            ldtp = power.getAbstractUtil().setOnWindow(fileName_6277);
            ldtp.click("paneSlide");
            power.editOffice(ldtp, " " + last_modification + " ");

            // ---- Step 13 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            power.saveOffice(ldtp);
            ldtp.waitTime(2);
            power.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);
            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 14 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(fileName_6277 + fileTypePowerPoint);

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
    
    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6278() throws Exception
    {
        String testName = getTestName() + "9";
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + fileName_6278 + fileTypePowerPoint);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6278 + fileTypePowerPoint);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6278() throws IOException
    {
        String testName = getTestName() + "9";
        String siteName = getSiteName(testName).toLowerCase();
        String first_modification = testName + "1";
        String second_modification = testName + "2";
        String last_modification = testName + "3";

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        try
        {
            // ---- Step 1 ----
            // ---- Step Action -----
            // Open .pptx document for editing.
            // The document is opened in write mode.
            Ldtp ldtp = power.openFileFromCMD(fullPath, fileName_6278 + fileTypePowerPoint, testUser, DEFAULT_PASSWORD);

            // ---- Step 2 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.   
            ldtp = power.getAbstractUtil().setOnWindow(fileName_6278); 
            ldtp.click("paneSlide");
            uploadImageInOffice(image_1);
            ldtp.waitTime(2);
            ldtp.click("paneSlide");
            power.editOffice(ldtp, " " + first_modification + " ");

            // ---- Step 3 ----
            // ---- Step Action -----
            // Save the document (Ctrl+S, for example) and close it
            // Expected Result
            // The document is saved. No errors occur in UI and in the log. No tmp files are left.
            power.saveOffice(ldtp);
            ldtp.waitTime(3);
            power.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);

            int nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 4 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6278 + fileTypePowerPoint);

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
            ldtp = power.openFileFromCMD(fullPath, fileName_6278 + fileTypePowerPoint, testUser, DEFAULT_PASSWORD);

            // ---- Step 7 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            ldtp = power.getAbstractUtil().setOnWindow(fileName_6278);
            ldtp.click("btnNextSlide");
            ldtp.waitTime(1);
            ldtp.click("paneSlide");
            uploadImageInOffice(image_2);
            ldtp.waitTime(2);
            ldtp.click("paneSlide");
            power.editOffice(ldtp, " " + second_modification + " ");

            // ---- Step 8 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            power.saveOffice(ldtp);
            ldtp.waitTime(2);
            power.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);
            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 9 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(fileName_6278 + fileTypePowerPoint);

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
            ldtp = power.openFileFromCMD(fullPath, fileName_6278 + fileTypePowerPoint, testUser, DEFAULT_PASSWORD);

            // ---- Step 12 ----
            // ---- Step Action -----
            // Add any data.
            // Expected Result
            // The data is entered.
            ldtp = power.getAbstractUtil().setOnWindow(fileName_6278);
            ldtp.click("btnNextSlide");
            ldtp.waitTime(1);
            ldtp.click("btnNextSlide");
            ldtp.waitTime(1);
            ldtp.click("paneSlide");
            uploadImageInOffice(image_3);
            ldtp.waitTime(2);
            ldtp.click("paneSlide");
            power.editOffice(ldtp, " " + last_modification + " ");

            // ---- Step 13 ----
            // ---- Step Action -----
            // Verify the document's content.
            // Expected Result
            // All changes are present and displayed correctly
            power.saveOffice(ldtp);
            ldtp.waitTime(2);
            power.exitOfficeApplication(ldtp);
            ldtp.waitTime(3);
            nrFiles = getNumberOfFilesFromPath(fullPath);
            Assert.assertEquals(nrFiles, 1);

            // ---- Step 14 ----
            // ---- Step Action -----
            // Verify the document's metadata and version history in the Share.
            // Expected Result
            // The document's metadata and version history are not broken. They are displayed correctly.
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
            detailsPage = documentLibPage.selectFile(fileName_6278 + fileTypePowerPoint);

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
        Ldtp l1 = word.openFileFromCMD(localPath, docxFileName_6269 + fileTypeDoc, testUser, DEFAULT_PASSWORD);

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
    
    private void uploadImageInOffice(String image) throws AWTException
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();            
        ImageIcon icon = new ImageIcon(image);   
        CifsUtil clipboardImage = new CifsUtil(icon.getImage());
        clipboard.setContents(clipboardImage, clipboardImage);
        
        Robot r = new Robot();
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_V);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_V); 
    }
    
}
