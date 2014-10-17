package org.alfresco.share.sharepoint.office2013;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.alfresco.application.util.Application;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.TreeMenuNavigation;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.windows.application.MicrosoftOffice2013;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;

@Listeners(FailedTestListener.class)
public class MSExcelTest extends AbstractUtils
{

    private String testName;
    private String testUser;
    private String siteName;

    private static DocumentLibraryPage documentLibPage;
    private String xlsFileName_9829;
    private String xlsFileName_9830;
    private String xlsFileName_9831;
    private String xlsFileName_9832;
    private String xlsFileName_9833;
    private String xlsFileName_9834;
    private String xlsFileName_9835;
    private String xlsFileName_9836;
    private String xlsFileName_9837;
    private String xlsFileName_9838;
    private String xlsFileName_9839;
    private String xlsFileName_9840;
    private String xlsFileName_9841;
    private String fileType = ".xlsx";

    MicrosoftOffice2013 excel = new MicrosoftOffice2013(Application.EXCEL, "2013");
    private static final String SHAREPOINT = "sharepoint";

    public String officePath;
    public String sharepointPath;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        testUser = getUserNameFreeDomain(testName);
        siteName = getSiteName(testName);
        xlsFileName_9829 = "SaveFileToShare";
        xlsFileName_9830 = "CheckInAfterSaving";
        xlsFileName_9831 = "InputSavechanges";
        xlsFileName_9832 = "InputOpen";
        xlsFileName_9833 = "InputCheckout";
        xlsFileName_9834 = "InputSave";
        xlsFileName_9835 = "InputCheckin";
        xlsFileName_9836 = "InputKeepcheckout";
        xlsFileName_9837 = "InputDiscard";
        xlsFileName_9838 = "InputRefresh";
        xlsFileName_9839 = "InputEmptycomm";
        xlsFileName_9840 = "InputWildcardscomm";
        xlsFileName_9841 = "InputCancel";

        Runtime.getRuntime().exec("taskkill /F /IM EXCEL.EXE");

        Properties officeAppProperty = new Properties();
        officeAppProperty.load(this.getClass().getClassLoader().getResourceAsStream("qa-share.properties"));
        String officeVersion = "2013";
        sharepointPath = officeAppProperty.getProperty("sharepoint.path");
        officePath = officeAppProperty.getProperty("office" + officeVersion + ".path");

    }

    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {
        Runtime.getRuntime().exec("taskkill /F /IM EXCEL.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM CobraWinLDTP.EXE");
    }

    @Test(groups = { "DataPrepExcel" })
    public void dataPrep_AONE() throws Exception
    {

        // Create normal User
        String[] testUser2 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        // login with user
        ShareUser.login(drone, testUser);

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Any office excel document was saved to the site document library;
        File file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9832 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9831 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9833 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9834 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9835 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9836 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9837 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9838 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9839 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9840 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9841 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
    }

    @Test(groups = "alfresco-one")
    public void AONE_9829() throws Exception
    {

        // MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        // Enter some content to the document;
        excel.editOffice(l, "new input data");

        // File->SaveAs ->SharePoint -> Browse
        excel.navigateToSaveAsSharePointBrowse(l);

        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);

        // 1. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 2. Enter the credentials;
        // 3. Select site Document Library where you would like to save the
        // document;
        // 4. Enter a workbook name;
        // 5. Click Save button;

        String path = getPathSharepoint(drone);
        excel.operateOnSaveAsWithSharepoint(l, path, siteName, xlsFileName_9829, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9829);

        // Workbook is saved in MS Office Excel 2013;
        Assert.assertTrue(excelName.contains(xlsFileName_9829 + " - Excel"));

        // 6. Log into Share;

        ShareUser.login(drone, testUser);

        // 7. Go to site Document library where workbook was saved;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 7. The saved workbook is present;
        Assert.assertTrue(documentLibPage.isFileVisible(xlsFileName_9829 + fileType), "The saved document is not displayed.");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9830() throws Exception
    {
        // MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        // Enter some content to the document;
        String newContent = "new input data for 9830";
        excel.editOffice(l, newContent);

        // Save as window is opened
        excel.navigateToSaveAsSharePointBrowse(l);

        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Enter the credentials;
        // Select site Document Library where you would like to save the
        // document;
        // Enter a workbook name;
        // Click Save button;
        String path = getPathSharepoint(drone);
        excel.operateOnSaveAsWithSharepoint(l, path, siteName, xlsFileName_9830, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9830);
        // 1. Workbook is saved in MS Office Excel 2013;
        Assert.assertTrue(excelName.contains(xlsFileName_9830 + " - Excel"));

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9830);
        // Checkout the document
        excel.checkOutOffice(l1);

        // 3. User return to the document; (I) Checked Out This file has been
        // checked out to you. Check In this file to allow other users to see
        // your changes
        // and editn this file;
        Ldtp l2 = excel.getAbstractUtil().setOnWindow(xlsFileName_9830);

        // 4. Click Check In;
        String commentFromExcel = "comment for 9830";
        excel.checkInOffice(l2, commentFromExcel, false);

        // 5. Comment is entered; Version window is closed; Information message
        // (i) Server read-only;
        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9830);
        Assert.assertTrue(actualName.contains("[Read-Only]"), "Excel is NOT opened in read only mode");

        // 6. Log into Share;
        ShareUser.login(drone, testUser);

        // 7. Go to site Document library;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 7. Locked document and working copy are present;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9830 + fileType);
        Assert.assertEquals(detailsPage.getContentInfo(), "This document is locked by you for offline editing.");
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertEquals(properties.get("Name"), xlsFileName_9830 + " (Working Copy)" + fileType);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9831() throws Exception
    {
        // 1. MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9831, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2013;

        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9831);
        Assert.assertTrue(actualName.contains(xlsFileName_9831), "Microsoft Excel - Input_savechanges window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9831);
        // 1. Make some changes to the excel document;
        // 2. Click Save button;
        excel.editOffice(l1, testName);
        excel.saveOffice(l1);

        // Click File ->Info -> Manage Versions;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // 3. Click Refresh Versions list;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Refresh Server Versions List");
        // 3. New minor version is created
        String fileVersion = "1.1";
        String fileVersionObject = "btn" + fileVersion.replace(".", "");

        Assert.assertTrue(excel.getAbstractUtil().isObjectDisplayed(l1, fileVersionObject), "Object with version " + fileVersion + " is not displayed");

        // 4. Log into Share;
        ShareUser.login(drone, testUser);

        // 5. Go to site Document Library and verify changes;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // 5. Document version history is correctly dispalyed; Changes are
        // applied;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9831 + fileType);
        Assert.assertTrue(detailsPage.isCheckedOut(), "The document is not checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), fileVersion);
        String documentContent = detailsPage.getDocumentBody();
        Assert.assertTrue(documentContent.contains(testName), "Changes are present in the document.");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9832() throws Exception
    {

        // MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // 1. Enter the credentials;
        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // 2. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 3. Select the workbook from site Document Library you would like to
        // open;
        // 4. Click Open button;
        excel.operateOnOpen(l, path, siteName, xlsFileName_9832, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9832);
        // the window name contains "frm" string at the begining
        // excelName = excelName.substring(3);
        // 4. Workbook is opened in MS Office Excel 2013;
        Assert.assertTrue(excelName.contains(xlsFileName_9832 + " - Excel"));

    }

    @Test(groups = "alfresco-one")
    public void AONE_9833() throws Exception
    {
        // 1. MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // Enter the credentials;
        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        excel.operateOnOpen(l, path, siteName, xlsFileName_9833, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9833);
        // the window name contains "frm" string at the begining(3);
        // Workbook is opened in MS Office Excel 2013;
        Assert.assertTrue(excelName.contains(xlsFileName_9833 + " - Excel"));

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9833);
        // Click File ->Info;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        Assert.assertFalse(excel.getAbstractUtil().isObjectDisplayed(l1, "DiscardCheckOut"));
        Assert.assertFalse(excel.getAbstractUtil().isObjectDisplayed(l1, "CheckIn"));
        // 2. Expand the Manage versions section;
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // 3. Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        Ldtp l2 = excel.getAbstractUtil().setOnWindow(xlsFileName_9833);
        // 4. Click File ->Info;
        excel.goToFile(l2);
        excel.getAbstractUtil().clickOnObject(l2, "Info");

        // 5. Verify Checked out information is added to Info;
        // 5. Check In and Discard check out actions are available;
        // "No else can edit this document or view your changes until it is checked it"
        // message is displayed at the pane; Check the excel is in Read-Only
        // mode
        Assert.assertTrue(excel.getAbstractUtil().isObjectDisplayed(l1, "DiscardCheckOut"), "Discard Check Out action is not available");
        Assert.assertTrue(excel.getAbstractUtil().isObjectDisplayed(l1, "CheckIn"), "Check In action is not available");

        // 6. Log into Share;

        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // 7. Go to site Document Library and verify workbook is in locked
        // state;
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, xlsFileName_9833 + fileType);
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + xlsFileName_9833 + " isn't locked");
        // 7. Excel Document is present in I'm Editing section;
        TreeMenuNavigation treeMenuNavigation = documentLibPage.getLeftMenus().render();
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.IM_EDITING).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, xlsFileName_9833 + fileType, TreeMenuNavigation.DocumentsMenu.IM_EDITING, true),
                xlsFileName_9833 + fileType + " cannot be found.");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9834() throws Exception
    {

        // MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // 1. Enter the credentials;
        excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // 2. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 3. Select the workbook from site Document Library you would like to
        // open;
        // 4. Click Open button;
        excel.operateOnOpen(l, path, siteName, xlsFileName_9834, testUser, DEFAULT_PASSWORD);
        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9834);
        Assert.assertTrue(actualName.contains(xlsFileName_9834), "Microsoft Excel window is active.");

        // 1. Make some changes to the excel document;
        // 2. Click Save button;
        String newContent = testName;
        excel.editOffice(l, newContent);
        // Ldtp l2 = new
        // Ldtp(excel.getAbstractUtil().findWindowName("AONE_9834"));
        excel.saveOffice(l);

        // 3. Log into Share;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // 4. Go to site Document Library and verify changes are applied to the
        // working copy;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9834 + fileType);
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertEquals(properties.get("Name"), xlsFileName_9834 + " (Working Copy)" + fileType);
        String documentContent = detailsPage.getDocumentBody();
        Assert.assertTrue(documentContent.contains(newContent), "Changes are present in the document.");

        // Excel Document is present in I'm Editing section;
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, xlsFileName_9834 + fileType);
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you.", "File " + xlsFileName_9834 + " isn't locked");
        TreeMenuNavigation treeMenuNavigation = documentLibPage.getLeftMenus().render();
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.IM_EDITING).render();
        assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, xlsFileName_9834 + fileType, TreeMenuNavigation.DocumentsMenu.IM_EDITING, true),
                xlsFileName_9834 + fileType + " cannot be found.");
    }

    @Test(groups = "alfresco-one")
    public void AONE_9835() throws Exception
    {
        // 1. MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // Enter the credentials;
        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        excel.operateOnOpen(l, path, siteName, xlsFileName_9835, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9835);
        // the window name contains "frm" string at the begining
        // Workbook is opened in MS Office Excel 2013;
        Assert.assertTrue(excelName.contains(xlsFileName_9835 + " - Excel"));

        // Excel Document is opened and checked out;
        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9835);

        // Checkout the document
        excel.checkOutOffice(l1);

        l1.waitTime(2);
        l1.verifySetText("pane8", "");

        // 1. Make some changes to the workbook;
        // Ldtp l2 = excel.getAbstractUtil().setOnWindow("AONE_9835");
        excel.editOffice(l1, "new input data");
        l1.verifySetText("pane8", "new input data");
        // Save the document
        // excel.goToFile(l2);
        // excel.clickOnObject(l2, "Info");
        // excel.clickOnObject(l2, "mnuSave");

        // check in with comments
        String commentFromExcel = "comment from excel file check in";
        excel.checkInOffice(l1, commentFromExcel, false);

        // 5. Version wimdow is closed; Information message (i) Server
        // read-only; This file was opened from server in read-only mode and to
        // buttons: Edit
        // Workbook and (X) close;
        // TODO: String actualName = excel.findWindowName("AONE_9835");
        // Assert.assertTrue(actualName.contains("[Read-Only]"),
        // "Excel is NOT opened in read only mode");

        // 6. Cick File->Info button;
        // 7. Verify the comment is dispalyed near the version (Put cursor on
        // comment icon to see it); - can not be implemented

        // 8. Log into Share;
        ShareUser.login(drone, testUser);

        // 9. Navigate the workbook; Verify changes are applied; Version history
        // contains the entered comment;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9835 + fileType).render();

        // 9. Document is checked in; Changes are applied; Comment is
        // successfullly displayed in Versions history;
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent(), "Version History section is not present");
        String commentFromShare = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(commentFromShare, commentFromExcel);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9836() throws Exception
    {

        // 1. MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // Enter the credentials;
        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;

        excel.operateOnOpen(l, path, siteName, xlsFileName_9836, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9836);
        // the window name contains "frm" string at the begining
        // Workbook is opened in MS Office Excel 2013;
        Assert.assertTrue(excelName.contains(xlsFileName_9836 + " - Excel"));

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9836);

        // Checkout the document
        excel.checkOutOffice(l1);

        // 1. Make some changes to the workbook;
        String newContent = "new input data";
        excel.editOffice(l1, newContent);
        l1.verifySetText("pane8", newContent);
        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // check in with comments
        String commentFromExcel = "comment from excel file check in";
        excel.checkInOffice(l1, commentFromExcel, true);

        // 7. Log into Share;

        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, xlsFileName_9836 + fileType);

        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + xlsFileName_9836 + " isn't locked");

        documentLibPage.getFileDirectoryInfo(xlsFileName_9836 + fileType).selectCancelEditing().render();

        // 8. Navigate the document;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9836 + fileType).render();

        String documentContent = detailsPage.getDocumentBody();
        Assert.assertTrue(documentContent.contains(newContent), "Changes are present in the document.");

        // 8. Changes are applied to the original file; Version is increased to
        // new major one.
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), "2.0");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9837() throws Exception
    {

        // 1. MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // Enter the credentials;
        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;

        excel.operateOnOpen(l, path, siteName, xlsFileName_9837, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9837);
        Assert.assertTrue(excelName.contains(xlsFileName_9837 + " - Excel"));
        ;

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9837);
        // Checkout the documents
        excel.checkOutOffice(l1);

        // 1. Make some changes to the workbook;
        String newContent = "new input data";
        excel.editOffice(l1, newContent);
        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // 2. Cick File->Info button;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        // 3. Click Discard check out button;
        excel.getAbstractUtil().clickOnObject(l1, "DiscardCheckOut");
        // 4. Click Yes button;
        excel.getAbstractUtil().clickOnObject(l1, "Yes");

        // 4. User returns to the document;
        excel.goToFile(l1);

        // 5. Log into Share;

        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // 6. Navigate the document;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9837 + fileType);

        // 6. Changes are not applied to the original file; Version is not
        // increased to new major one.
        String documentContent = detailsPage.getDocumentBody();
        Assert.assertFalse(documentContent.contains(newContent), "Changes are not applied to the document.");
        String version = detailsPage.getDocumentVersion();
        if (!version.isEmpty())
        {
            assertEquals(version, "1.0");
        }

    }

    @Test(groups = "alfresco-one")
    public void AONE_9838() throws Exception
    {

        // 1. MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // Enter the credentials;
        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        excel.operateOnOpen(l, path, siteName, xlsFileName_9838, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9838);
        // Workbook is opened in MS Office Excel 2013;
        Assert.assertTrue(excelName.contains(xlsFileName_9838 + " - Excel"));

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9838);
        // Checkout and check In the document
        excel.checkOutOffice(l1);

        excel.editOffice(l1, "new input data");
        l1.verifySetText("pane8", "new input data");

        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");
        
        String commentFromExcel = "comment from excel file check in";
        excel.checkInOffice(l1, commentFromExcel, false);

        // Excel Document is opened in read-only mode;
        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9838);
        Assert.assertTrue(actualName.contains("[Read-Only]"), "Excel is NOT opened in read only mode");

        // 1. Update the document;
        Ldtp l2 = excel.getAbstractUtil().setOnWindow(xlsFileName_9838);
        excel.editOffice(l2, "new input data added");

        // 2. In MS Excel click File->Info;
        excel.goToFile(l2);
        excel.getAbstractUtil().clickOnObject(l2, "Info");
        // 3. Expand Versions menu and select Refresh server versions list;
        excel.getAbstractUtil().clickOnObject(l2, "ManageVersions");
        l2.waitTime(2);
        l2.keyPress("tab");
        l2.keyPress("enter");
        l2.click("Refresh Server Versions List");
        // 3. New version created via Share is added to the list;
        String fileVersion = "1.1";
        String fileVersionObject = "btn" + fileVersion.replace(".", "");
        Assert.assertTrue(excel.getAbstractUtil().isObjectDisplayed(l1, fileVersionObject), "Object with version " + fileVersion + " is not displayed");

        // This step cannot be automated. The LDTP tool cannot perform actions on Read Only window
        // 4. Click Edit Workbook button;
        // 4. The uploaded version is opened;

    }

    @Test(groups = "alfresco-one")
    public void AONE_9839() throws Exception
    {
        // 1. MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // Enter the credentials;
        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;

        excel.operateOnOpen(l, path, siteName, xlsFileName_9839, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9839);

        // Workbook is opened in MS Office Excel 2013;
        Assert.assertTrue(excelName.contains(xlsFileName_9839 + " - Excel"));

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9839);
        // Checkout the document
        excel.checkOutOffice(l1);

        // Make some changes to the workbook;
        excel.editOffice(l1, "new input data");
        l1.verifySetText("pane8", "new input data9839");
        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // 2. Don't enter a comment for this version;
        // 3. Click OK button;
        String commentFromExcel = "";
        excel.checkInOffice(l1, commentFromExcel, false);

        // 4. Log into Share;

        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 5. Navigate the document; Verify changes are applied; Version history
        // contains is increased to major; no comment is displayed for the
        // version;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9839 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), "2.0");
        String emptyComment = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(emptyComment, "(No Comment)");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9840() throws Exception
    {
        // 1. MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // Enter the credentials;
        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;

        excel.operateOnOpen(l, path, siteName, xlsFileName_9840, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9840);

        // Workbook is opened in MS Office Excel 2013;
        Assert.assertTrue(excelName.contains(xlsFileName_9840 + " - Excel"));

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9840);
        // Checkout the document
        excel.checkOutOffice(l1);

        // Make some changes to the workbook;
        excel.editOffice(l1, "new input data");
        l1.verifySetText("pane8", "new input data9840");
        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        // 2. Enter a comment contains wildcards (e.g.
        // !@#$%^&*()_+|\/?.,<>:;"'`=-{}[]);
        // 3. Click OK button;
        String commentFromExcel = "a(e.g. !@#$%^&*()_+|\\/?.,<>:;\"'`=-{}[]";
        excel.checkInOffice(l1, commentFromExcel, false);

        // 4. Log into Share;

        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 5. Navigate the document; Verify changes are applied; Version history
        // contains is increased to major; no comment is displayed for the
        // version;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9840 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), "2.0");
        Assert.assertEquals(detailsPage.getCommentsOfLastCommit(), commentFromExcel);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9841() throws Exception
    {
        // 1. MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.navigateToOpenSharePointBrowse(l);

        // Enter the credentials;
        // excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);

        // Enter the credentials;
        excel.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;

        excel.operateOnOpen(l, path, siteName, xlsFileName_9841, testUser, DEFAULT_PASSWORD);

        String excelName = excel.getAbstractUtil().findWindowName(xlsFileName_9841);

        // Workbook is opened in MS Office Excel 2013;
        Assert.assertTrue(excelName.contains(xlsFileName_9841 + " - Excel"));

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9841);
        // Checkout the document
        excel.checkOutOffice(l1);

        // 1. Make some changes to the workbook;
        String newContent = "new input data";
        excel.editOffice(l1, newContent);
        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");
        // 2. Cick File->Info button;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");

        // 3.Click Check In button;
        excel.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 4. Enter a comment for this version;
        // 5. Click Cancel button;
        String commentFromExcel = "a comment for 9841";
        l1.enterString("txtVersionComments", commentFromExcel);
        l1.mouseLeftClick("btnCancel");

        // 6. Log into Share;

        ShareUser.login(drone, testUser);

        // 7. Navigate the document;
        DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

        // 7. Excel Document is still checked out; Changes are not applied;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9841 + fileType);
        assertEquals(detailsPage.getContentInfo(), "This document is locked by you for offline editing.", "File " + xlsFileName_9841 + " isn't locked");

        String documentContent = detailsPage.getDocumentBody();
        Assert.assertFalse(documentContent.contains(newContent), "Changes are applied to the document.");

    }

}
