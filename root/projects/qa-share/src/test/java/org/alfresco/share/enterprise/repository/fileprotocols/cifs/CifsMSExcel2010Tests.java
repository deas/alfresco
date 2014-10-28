package org.alfresco.share.enterprise.repository.fileprotocols.cifs;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

@Listeners(FailedTestListener.class)
public class CifsMSExcel2010Tests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(CifsMSExcel2010Tests.class);

    private String testName;
    private String testUser;
    private String siteName;
    private String xlsxFileType = ".xlsx";
    String docFileName_6265;
    String docFileName_6266;
    String docxFileName_6269;
    String fileName_6271;
    String fileName_6272;
    String fileName_6273;
    String fileName_6274;
    String fileName_6275;
    String fileName_6276;
    String fileName_6277;
    String fileName_6278;

    String image_1 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic1.jpg";
    String image_2 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic2.jpg";
    String image_3 = DATA_FOLDER + CIFS_LOCATION + SLASH + "CifsPic3.jpg";

    private static DocumentLibraryPage documentLibPage;
    private static final String CIFS_LOCATION = "cifs";
    public String officePath;
    WindowsExplorer explorer = new WindowsExplorer();
    MicorsoftOffice2010 word = new MicorsoftOffice2010(Application.WORD, "2010");
    MicorsoftOffice2010 excel = new MicorsoftOffice2010(Application.EXCEL, "2010");
    MicorsoftOffice2010 power = new MicorsoftOffice2010(Application.POWERPOINT, "2010");
    String mapConnect;
    String networkDrive;
    String networkPath;
    String cifsPath;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName() + 20;
        testUser = getUserNameFreeDomain(testName);

        // word files
        docFileName_6265 = "AONE-6265";
        docFileName_6266 = "AONE-6266";

        // excel files
        fileName_6271 = "AONE-6271";
        fileName_6272 = "AONE-6272";
        fileName_6273 = "AONE-6273";
        fileName_6274 = "AONE-6274";
        fileName_6275 = "AONE-6275";
        fileName_6276 = "AONE-6276";

        // power point files
        fileName_6277 = "AONE-6277";
        fileName_6278 = "AONE-6278";

        cifsPath = excel.getCIFSPath();

        networkDrive = excel.getMapDriver();
        networkPath = excel.getMapPath();
        mapConnect = "net use" + " " + networkDrive + " " + networkPath + " " + "/user:" + testUser + " " + DEFAULT_PASSWORD;

        // create user
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Runtime.getRuntime().exec("net use * /d /y");;
        // logger.info("Unmapping succesfull " + testUser);

        Runtime.getRuntime().exec(mapConnect);
        logger.info("----------Mapping succesfull " + testUser);

    }

    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {
        Runtime.getRuntime().exec("taskkill /F /IM EXCEL.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM POWERPNT.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM WINWORD.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM CobraWinLDTP.EXE");
    }

    @AfterClass(alwaysRun = true)
    public void unmapDrive() throws Exception
    {
        Runtime.getRuntime().exec("net use * /d /y");
        ;
        logger.info("--------Unmapping succesfull " + testUser);
    }

    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6271() throws Exception
    {
        String testName = getTestName() + 21;
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + fileName_6271 + xlsxFileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6271 + xlsxFileType);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    /** AONE-6271:MS Excel 2010 - uploaded to Share */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6271() throws IOException
    {
        String testName = getTestName() + 21;
        String siteName = getSiteName(testName).toLowerCase();
        String first_modification = testName + "1";
        String second_modification = testName + "2";
        String last_modification = testName + "3";

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        // ---- Step 1 ----
        // ---- Step Action -----
        // Open .xlsx document for editing.
        // The document is opened in write mode.
        Ldtp ldtp = excel.openFileFromCMD(fullPath, fileName_6271 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);

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
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6271 + xlsxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
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
        ldtp = excel.openFileFromCMD(fullPath, fileName_6271 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);

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
        detailsPage = documentLibPage.selectFile(fileName_6271 + xlsxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
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
        ldtp = excel.openFileFromCMD(fullPath, fileName_6271 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);

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
        detailsPage = documentLibPage.selectFile(fileName_6271 + xlsxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
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

    @Test(groups = { "DataPrepWord" })
    public void dataPrep_6272() throws Exception
    {
        String testName = getTestName() + 21;
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + CIFS_LOCATION + SLASH + fileName_6272 + xlsxFileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6272 + xlsxFileType);

        // Click "Edit Properties" in Actions section;
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setDocumentTitle(testName);

        detailsPage = editPropertiesPage.selectSave().render();
        detailsPage.render();
    }

    /** AONE-6272:MS Excel 2010 - uploaded to Share (big) 
     * @throws AWTException */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6272() throws IOException, AWTException
    {
        String testName = getTestName() + 21;
        String siteName = getSiteName(testName).toLowerCase();
        String first_modification = testName + "1";
        String second_modification = testName + "2";
        String last_modification = testName + "3";

        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        // ---- Step 1 ----
        // ---- Step Action -----
        // Open .xlsx document for editing.
        // The document is opened in write mode.
        Ldtp ldtp = excel.openFileFromCMD(fullPath, fileName_6272 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);

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
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6272 + xlsxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
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
        ldtp = excel.openFileFromCMD(fullPath, fileName_6272 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);

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
        detailsPage = documentLibPage.selectFile(fileName_6272 + xlsxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
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
        ldtp = excel.openFileFromCMD(fullPath, fileName_6272 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);

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
        detailsPage = documentLibPage.selectFile(fileName_6272 + xlsxFileType);

        editPropertiesPage = detailsPage.selectEditProperties().render();
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

    @Test(groups = { "DataPrepExcel" })
    public void dataPrep_6273() throws Exception
    {

        testName = getTestName() + 21;
        siteName = getSiteName(testName);

        ShareUser.login(drone, testUser);

        // --- Step 2 ---
        // --- Step action ---
        // Any site is created;
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        // --- Step 3 ---
        // --- Step action ---
        // CIFS is mapped as a network drive as a site manager user; -- already
        // performed manually
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

    }

    @Test(groups = { "DataPrepExcel" })
    public void dataPrep_6274() throws Exception
    {

        testName = getTestName() + 21;
        siteName = getSiteName(testName);

        ShareUser.login(drone, testUser);

        // --- Step 2 ---
        // --- Step action ---
        // Any site is created;
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        // --- Step 3 ---
        // --- Step action ---
        // CIFS is mapped as a network drive as a site manager user; -- already
        // performed manually
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

    }

    @Test(groups = { "DataPrepExcel" })
    public void dataPrep_6275() throws Exception
    {
        String testName = getTestName() + 21;
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser);

        // --- Step 2 ---
        // --- Step action ---
        // Any site is created;
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        // --- Step 3 ---
        // --- Step action ---
        // CIFS is mapped as a network drive as a site manager user; -- already
        // performed manually
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

    }

    @Test(groups = { "DataPrepExcel" })
    public void dataPrep_6276() throws Exception
    {
        String testName = getTestName() + 21;
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser);

        // --- Step 2 ---
        // --- Step action ---
        // Any site is created;
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        // --- Step 3 ---
        // --- Step action ---
        // CIFS is mapped as a network drive as a site manager user; -- already
        // performed manually
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

    }

    /** AONE-6273:MS Excel 2010 - created via context menu */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6273() throws Exception
    {
        String testName = getTestName() + 21;
        String siteName = getSiteName(testName).toLowerCase();
        String addText = "First text";
        String edit1 = "New text1";
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        Ldtp security = new Ldtp("Windows Security");
        Ldtp l1;

        // --- Step 1 ---
        // --- Step action ---
        // RBC in the window -> New -> New Microsoft Office Word Document
        // --- Expected results --
        // The document .docx is created

        l1 = excel.openOfficeApplication();
        excel.editOffice(l1, addText);
        excel.saveAsOffice(l1, fullPath + fileName_6273);
        excel.operateOnSecurityAndWait(security, testUser, DEFAULT_PASSWORD);
        l1.waitTime(2);
        excel.getAbstractUtil().waitForWindow(fileName_6273);
        excel.exitOfficeApplication(l1);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = excel.openFileFromCMD(fullPath, fileName_6273 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);
        l1 = excel.getAbstractUtil().setOnWindow(fileName_6273);

        // ---- Step 3 ----
        // ---- Step Action -----
        // Add any data.
        // ---- Expected Result -----
        // The data is entered.
        excel.editOffice(l1, " " + edit1 + " ");

        // --- Step 4 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        excel.saveOffice(l1);
        l1.waitTime(2);
        excel.exitOfficeApplication(l1);
        int noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, 1, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + 1);

        // ---- Step 5 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6273 + xlsxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6273 + xlsxFileType));
        editPropertiesPage.clickCancel();
        String body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit1));

        // --- Step 6 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        EditDocumentPropertiesPage editDocPropPage = detailsPage.selectEditProperties();
        String documentTitle = "Title " + fileName_6273;
        String documentDescription = "Description for " + fileName_6273;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.xlsx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.xlsx";
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion2, "", true).render();
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion3, "", true).render();

        Map<String, Object> propertiesValues = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 7 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = excel.openFileFromCMD(fullPath, fileName_6273 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);

        // ---- Step 8 ----
        // ---- Step Action -----
        // Add any data.
        // ---- Expected Result -----
        // The data is entered.
        excel.editOffice(l1, " " + edit2 + " ");

        // --- Step 9 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        excel.saveOffice(l1);
        l1.waitTime(2);
        excel.exitOfficeApplication(l1);
        l1.waitTime(3);
        noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, 1, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + 1);

        // --- Step 10 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6273 + xlsxFileType).render();

        Map<String, Object> propertiesValues2 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues2.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues2.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 11 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit2));

        // ---- Step 12 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = excel.openFileFromCMD(fullPath, fileName_6273 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);

        // ---- Step 13 ----
        // ---- Step Action -----
        // Add any data.
        // ---- Expected Result -----
        // The data is entered.
        excel.editOffice(l1, " " + edit3 + " ");

        // --- Step 14 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        excel.saveOffice(l1);
        l1.waitTime(2);
        excel.exitOfficeApplication(l1);
        l1.waitTime(3);
        noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, 1, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + 1);

        // --- Step 15 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6273 + xlsxFileType).render();

        Map<String, Object> propertiesValues3 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues3.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues3.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 16 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit3));

    }

    /** AONE-6274:MS Excel 2010 - created via context menu (big) */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6274() throws Exception
    {
        String testName = getTestName() + 21;
        String siteName = getSiteName(testName).toLowerCase();
        String addText = "First text";
        String edit1 = "New text1";
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;

        Ldtp security = new Ldtp("Windows Security");
        Ldtp l1;

        // --- Step 1 ---
        // --- Step action ---
        // RBC in the window -> New -> New Microsoft Office Word Document
        // --- Expected results --
        // The document .docx is created

        l1 = excel.openOfficeApplication();
        excel.editOffice(l1, addText);
        excel.saveAsOffice(l1, fullPath + fileName_6274);
        excel.operateOnSecurityAndWait(security, testUser, DEFAULT_PASSWORD);
        l1.waitTime(3);
        excel.getAbstractUtil().waitForWindow(fileName_6274);
        excel.exitOfficeApplication(l1);

        // ---- Step 2 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = excel.openFileFromCMD(fullPath, fileName_6274 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);
        l1 = excel.getAbstractUtil().setOnWindow(fileName_6274);

        // ---- Step 3 ----
        // ---- Step Action -----
        // Add any data (5-10 mb).
        // ---- Expected Result -----
        // The data is entered.
        uploadImageInOffice(image_1);
        excel.editOffice(l1, " " + edit1 + " ");

        // --- Step 4 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        excel.saveOffice(l1);
        l1.waitTime(2);
        excel.exitOfficeApplication(l1);
        l1.waitTime(3);
        int noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, 1, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + 1);

        // ---- Step 5 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6274 + xlsxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6274 + xlsxFileType));
        editPropertiesPage.clickCancel();
        String body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit1));

        // --- Step 6 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        EditDocumentPropertiesPage editDocPropPage = detailsPage.selectEditProperties();
        String documentTitle = "Title " + fileName_6274;
        String documentDescription = "Description for " + fileName_6274;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.xlsx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.xlsx";
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion2, "", true).render();
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion3, "", true).render();

        Map<String, Object> propertiesValues = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 7 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = excel.openFileFromCMD(fullPath, fileName_6274 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);

        // ---- Step 8 ----
        // ---- Step Action -----
        // Add any data (5-10 mb).
        // ---- Expected Result -----
        // The data is entered.
        uploadImageInOffice(image_2);
        excel.editOffice(l1, " " + edit2 + " ");

        // --- Step 9 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        excel.saveOffice(l1);
        l1.waitTime(2);
        excel.exitOfficeApplication(l1);
        l1.waitTime(3);
        noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, 1, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + 1);

        // --- Step 10 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6274 + xlsxFileType).render();

        Map<String, Object> propertiesValues2 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues2.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues2.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 11 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit2));

        // ---- Step 12 ----
        // ---- Step Action -----
        // Open .docx document for editing.
        // ---- Expected Result -----
        // The document is opened in write mode.
        l1 = excel.openFileFromCMD(fullPath, fileName_6274 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);

        // ---- Step 13 ----
        // ---- Step Action -----
        // Add any data (5-10 mb).
        // ---- Expected Result -----
        // The data is entered.
        uploadImageInOffice(image_3);
        excel.editOffice(l1, " " + edit3 + " ");

        // --- Step 14 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        excel.saveOffice(l1);
        l1.waitTime(2);
        excel.exitOfficeApplication(l1);
        l1.waitTime(3);
        noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, 1, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + 1);

        // --- Step 15 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6274 + xlsxFileType).render();

        Map<String, Object> propertiesValues3 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues3.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues3.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // ---- Step 16 ----
        // ---- Step Action -----
        // Verify the document's content.
        // Expected Result
        // All changes are present and displayed correctly.
        body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit3));

    }

    /** AONE-6275:MS Excel 2010 - saved into CIFS */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6275() throws Exception
    {
        String testName = getTestName() + 21;
        String siteName = getSiteName(testName).toLowerCase();
        String edit2 = "new text2";
        String edit3 = "new text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;
        String localPath = DATA_FOLDER + CIFS_LOCATION + SLASH;
        Ldtp l1;

        // --- Step 1 ---
        // --- Step action ---
        // Save a document into the CIFS drive (into the Document Library space)
        // and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.

        int noOfFilesBeforeSave = getNumberOfFilesFromPath(fullPath);
        l1 = excel.openFileFromCMD(localPath, fileName_6275 + xlsxFileType, testUser, DEFAULT_PASSWORD, false);

        l1 = excel.getAbstractUtil().setOnWindow(fileName_6275);
        excel.goToFile(l1);
        l1.waitTime(1);
        excel.getAbstractUtil().clickOnObject(l1, "SaveAs");

        excel.operateOnSaveAsWithFullPath(l1, fullPath, fileName_6275, testUser, DEFAULT_PASSWORD);
        l1 = excel.getAbstractUtil().setOnWindow(fileName_6275);
        l1.waitTime(3);
        excel.exitOfficeApplication(l1);

        int noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        noOfFilesBeforeSave = noOfFilesBeforeSave + 1;
        Assert.assertEquals(noOfFilesAfterSave, noOfFilesBeforeSave, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + noOfFilesBeforeSave);

        // --- Step 2 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6275 + xlsxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6275 + xlsxFileType));
        editPropertiesPage.clickCancel();

        // --- Step 3 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        EditDocumentPropertiesPage editDocPropPage = detailsPage.selectEditProperties();
        String documentTitle = "Title " + fileName_6275;
        String documentDescription = "Description for " + fileName_6275;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.xlsx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.xlsx";
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion2, "", true).render();
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion3, "", true).render();

        Map<String, Object> propertiesValues = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 4 ---
        // --- Step action ---
        // Open the document for editing again.
        // --- Expected results --
        // The document is opened in write mode.
        l1 = excel.openFileFromCMD(fullPath, fileName_6275 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);
        l1 = excel.getAbstractUtil().setOnWindow(fileName_6275);
        String actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(fileName_6275), "Microsoft Excel - " + fileName_6275 + " window is not active.");

        // --- Step 5 ---
        // --- Step action ---
        // Add any data.
        // --- Expected results --
        // The data is entered.
        excel.editOffice(l1, " " + edit2 + " ");

        // --- Step 6 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        excel.saveOffice(l1);
        l1.waitTime(2);
        excel.exitOfficeApplication(l1);
        noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, noOfFilesBeforeSave, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + noOfFilesBeforeSave);

        // --- Step 7 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6275 + xlsxFileType).render();

        Map<String, Object> propertiesValues2 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues2.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues2.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 8 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        String body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit2));

        // --- Step 9 ---
        // --- Step action ---
        // Open the document for editing again.
        // --- Expected results --
        // The document is opened in write mode.
        l1 = excel.openFileFromCMD(fullPath, fileName_6275 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);
        l1 = excel.getAbstractUtil().setOnWindow(fileName_6275);
        actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(fileName_6275), "Microsoft Excel - " + fileName_6275 + " window is not active.");

        // --- Step 10 ---
        // --- Step action ---
        // Add any data.
        // --- Expected results --
        // The data is entered.
        excel.editOffice(l1, " " + edit3 + " ");

        // --- Step 11 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        l1 = excel.getAbstractUtil().setOnWindow(fileName_6275);
        excel.saveOffice(l1);
        l1.waitTime(2);
        excel.exitOfficeApplication(l1);
        noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, noOfFilesBeforeSave, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + noOfFilesBeforeSave);

        // --- Step 12 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6275 + xlsxFileType).render();

        Map<String, Object> propertiesValues3 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues3.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues3.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 13 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        String body3 = detailsPage.getDocumentBody();
        Assert.assertTrue(body3.contains(edit3));
    }

    /** AONE-6276:MS Excel 2010 - saved into CIFS (big) */
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_6276() throws Exception
    {
        String testName = getTestName() + 21;
        String siteName = getSiteName(testName).toLowerCase();
        String edit2 = "New text2";
        String edit3 = "New text3";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;
        String localPath = DATA_FOLDER + CIFS_LOCATION + SLASH;
        Ldtp l1;

        // --- Step 1 ---
        // --- Step action ---
        // Save a document into the CIFS drive (into the Document Library space)
        // and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.

        int noOfFilesBeforeSave = getNumberOfFilesFromPath(fullPath);
        l1 = excel.openFileFromCMD(localPath, fileName_6276 + xlsxFileType, testUser, DEFAULT_PASSWORD, false);

        l1 = excel.getAbstractUtil().setOnWindow(fileName_6276);
        excel.goToFile(l1);
        l1.waitTime(1);
        excel.getAbstractUtil().clickOnObject(l1, "SaveAs");

        excel.operateOnSaveAsWithFullPath(l1, fullPath, fileName_6276, testUser, DEFAULT_PASSWORD);
        l1 = excel.getAbstractUtil().setOnWindow(fileName_6276);
        l1.waitTime(3);
        excel.exitOfficeApplication(l1);

        int noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        noOfFilesBeforeSave = noOfFilesBeforeSave + 1;
        Assert.assertEquals(noOfFilesAfterSave, noOfFilesBeforeSave, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + noOfFilesBeforeSave
                + 1);

        // --- Step 2 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        ShareUser.login(drone, testUser);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName_6276 + xlsxFileType);

        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPropertiesPage.getName().equals(fileName_6276 + xlsxFileType));
        editPropertiesPage.clickCancel();

        // --- Step 3 ---
        // --- Step action ---
        // Specify the document's metadata, e.g. title, description and so on,
        // and create some version history (i.e. add several new versions (minor
        // and major))
        // via the Share.
        // --- Expected results --
        // The document's metadata and version history are specified.
        EditDocumentPropertiesPage editDocPropPage = detailsPage.selectEditProperties();
        String documentTitle = "Title " + fileName_6276;
        String documentDescription = "Description for " + fileName_6276;
        editDocPropPage.setDocumentTitle(documentTitle);
        editDocPropPage.setDescription(documentDescription);
        detailsPage = editDocPropPage.selectSave().render();

        String newFileVersion2 = CIFS_LOCATION + SLASH + "SaveCIFSv2.xlsx";
        String newFileVersion3 = CIFS_LOCATION + SLASH + "SaveCIFSv3.xlsx";
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion2, "", true).render();
        detailsPage = ShareUser.uploadNewVersionOfDocument(drone, newFileVersion3, "", true).render();

        Map<String, Object> propertiesValues = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 4 ---
        // --- Step action ---
        // Open the document for editing again.
        // --- Expected results --
        // The document is opened in write mode.
        l1 = excel.openFileFromCMD(fullPath, fileName_6276 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);
        l1 = excel.getAbstractUtil().setOnWindow(fileName_6276);
        String actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(fileName_6276), "Microsoft Excel - " + fileName_6276 + " window is active.");

        // --- Step 5 ---
        // --- Step action ---
        // Add any data (5-10 mb).
        // --- Expected results --
        // The data is entered.
        uploadImageInOffice(image_1);
        excel.editOffice(l1, " " + edit2 + " ");

        // --- Step 6 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        excel.saveOffice(l1);
        l1.waitTime(2);
        excel.exitOfficeApplication(l1);
        l1.waitTime(3);
        noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, noOfFilesBeforeSave, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + noOfFilesBeforeSave);

        // --- Step 7 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6276 + xlsxFileType).render();

        Map<String, Object> propertiesValues2 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues2.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues2.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 8 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        String body = detailsPage.getDocumentBody();
        Assert.assertTrue(body.contains(edit2));

        // --- Step 9 ---
        // --- Step action ---
        // Open the document for editing again.
        // --- Expected results --
        // The document is opened in write mode.
        l1 = excel.openFileFromCMD(fullPath, fileName_6276 + xlsxFileType, testUser, DEFAULT_PASSWORD, true);
        l1 = excel.getAbstractUtil().setOnWindow(fileName_6276);
        actualName = l1.getWindowName();
        Assert.assertTrue(actualName.contains(fileName_6276), "Microsoft Excel - " + fileName_6276 + " window is active.");

        // --- Step 10 ---
        // --- Step action ---
        // Add any data (5-10 mb).
        // --- Expected results --
        // The data is entered.
        uploadImageInOffice(image_2);
        excel.editOffice(l1, " " + edit3 + " ");

        // --- Step 11 ---
        // --- Step action ---
        // Save the document (Ctrl+S, for example) and close it.
        // --- Expected results --
        // The document is saved. No errors occur in UI and in the log. No tmp
        // files are left.
        l1 = excel.getAbstractUtil().setOnWindow(fileName_6276);
        excel.saveOffice(l1);
        l1.waitTime(2);
        excel.exitOfficeApplication(l1);
        l1.waitTime(3);
        noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, noOfFilesBeforeSave, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + noOfFilesBeforeSave);

        // --- Step 12 ---
        // --- Step action ---
        // Verify the document's metadata and version history in the Share.
        // --- Expected results --
        // The document's metadata and version history are not broken. They are
        // displayed correctly..
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName_6276 + xlsxFileType).render();

        Map<String, Object> propertiesValues3 = detailsPage.getProperties();
        Assert.assertEquals(propertiesValues3.get("Title").toString(), documentTitle, "Document title is not " + documentTitle);
        Assert.assertEquals(propertiesValues3.get("Description").toString(), documentDescription, "Document description is not " + documentDescription);
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent());
        Assert.assertTrue(detailsPage.isVersionPresentInVersionHistoryPanel("1.0") && detailsPage.isVersionPresentInVersionHistoryPanel("2.0")
                && detailsPage.isVersionPresentInVersionHistoryPanel("3.0"));

        // --- Step 13 ---
        // --- Step action ---
        // Verify the document's content.
        // --- Expected results --
        // All changes are present and displayed correctly.
        String body3 = detailsPage.getDocumentBody();
        Assert.assertTrue(body3.contains(edit3));
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
    
    @Test(groups = { "CIFSWindowsClient", "EnterpriseOnly" })
    public void AONE_62761111() throws Exception
    {
        String siteName ="SitemyasShare-627620";
        String fullPath = networkDrive + cifsPath + siteName.toLowerCase() + SLASH + "documentLibrary" + SLASH;
        int noOfFilesAfterSave = getNumberOfFilesFromPath(fullPath);
        Assert.assertEquals(noOfFilesAfterSave, 1, "Number of file after save: " + noOfFilesAfterSave + ". Expected: " + 1);

    }
}
