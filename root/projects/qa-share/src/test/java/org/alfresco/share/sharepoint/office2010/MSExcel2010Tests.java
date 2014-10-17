package org.alfresco.share.sharepoint.office2010;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.Properties;

import org.alfresco.application.util.Application;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.windows.application.MicorsoftOffice2010;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;

@Listeners(FailedTestListener.class)
public class MSExcel2010Tests extends AbstractUtils
{

    private String testName;
    private String testUser;
    private String siteName;

    private static DocumentLibraryPage documentLibPage;
    private String xlsFileName_9664;
    private String xlsFileName_9665;
    private String xlsFileName_9666;
    private String xlsFileName_9667;
    private String xlsFileName_9668;
    private String xlsFileName_9669;
    private String xlsFileName_9670;
    private String xlsFileName_9671;
    private String xlsFileName_9672;
    private String xlsFileName_9673;
    private String xlsFileName_9674;
    private String xlsFileName_9675;
    private String xlsFileName_9676;
    MicorsoftOffice2010 excel = new MicorsoftOffice2010(Application.EXCEL, "2010");
    private String fileType = ".xlsx";
    public String officePath;
    public String sharepointPath;

    private static final String SHAREPOINT = "sharepoint";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName() + "01";
        testUser = getUserNameFreeDomain(testName);
        siteName = getSiteName(testName);
        xlsFileName_9664 = "AONE_9664.xlsx";
        xlsFileName_9665 = "AONE_9665.xlsx";
        xlsFileName_9666 = "InputSavechanges";
        xlsFileName_9667 = "InputOpen";
        xlsFileName_9668 = "InputCheckout";
        xlsFileName_9669 = "InputSave";
        xlsFileName_9670 = "InputCheckin";
        xlsFileName_9671 = "InputKeepcheckout";
        xlsFileName_9672 = "InputDiscard";
        xlsFileName_9673 = "InputRefresh";
        xlsFileName_9674 = "InputEmptycomm";
        xlsFileName_9675 = "InputWildcardscomm";
        xlsFileName_9676 = "InputCancel";

        Runtime.getRuntime().exec("taskkill /F /IM EXCEL.EXE");

        Properties officeAppProperty = new Properties();
        officeAppProperty.load(this.getClass().getClassLoader().getResourceAsStream("qa-share.properties"));
        String officeVersion = "2010";
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

        File file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9666 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9667 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9668 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9669 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9670 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9671 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9672 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9673 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9674 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9675 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + xlsFileName_9676 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
    }

    @Test(groups = "alfresco-one")
    public void AONE_9664() throws Exception
    {
        // 1. MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        // Enter some content to the document;
        excel.editOffice(l, "new input data");

        // Save as window is opened
        excel.goToFile(l);

        excel.getAbstractUtil().clickOnObject(l, "SaveAs");
        // Save As window is opened;
        // 1. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 2. Enter the credentials;
        // 3. Select site Document Library where you would like to save the
        // document;
        // 4. Enter a workbook name;
        // 5. Click Save button;
        String path = getPathSharepoint(drone);
        excel.operateOnSaveAs(l, path, siteName, xlsFileName_9664, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;
        // the window name contains "frm" string at the begining
        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9664);
        Assert.assertTrue(actualName.contains(xlsFileName_9664), "Microsoft Excel - AONE_9839 window is active.");

        // 6. Log into Share;

        ShareUser.login(drone, testUser);

        // 7. Go to site Document library where workbook was saved;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 7. The saved workbook is present;
        Assert.assertTrue(documentLibPage.isFileVisible(xlsFileName_9664 + fileType), "The saved document is not displayed.");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9665() throws Exception
    {
        // MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        // Enter some content to the document;
        excel.editOffice(l, "new input data for 9665");

        // Save as window is opened
        excel.goToFile(l);
        excel.getAbstractUtil().clickOnObject(l, "SaveAs");
        // Save As window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Enter the credentials;
        // Select site Document Library where you would like to save the
        // document;
        // Enter a workbook name;
        // Click Save button;
        String path = getPathSharepoint(drone);
        excel.operateOnSaveAs(l, path, siteName, xlsFileName_9665, testUser, DEFAULT_PASSWORD);

        // 1. Workbook is saved in MS Office Excel 2010;

        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9665);
        Assert.assertTrue(actualName.contains(xlsFileName_9665), "Microsoft Excel - AONE_9665 window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9665);
        
        // 2. Click File->Info ;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");

        // 3. Expand Version pane and click Check Out action;
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");
        // 3. User return to the document; (I) Checked Out This file has been
        // checked out to you. Check In this file to allow other users to see
        // your changes
        // and editn this file;
        Ldtp l2 = excel.getAbstractUtil().setOnWindow(xlsFileName_9665);

        // 4. Click Check In;
        excel.goToFile(l2);
        excel.getAbstractUtil().clickOnObject(l2, "Info");
        excel.getAbstractUtil().clickOnObject(l2, "CheckIn");

        // 5. Enter any comment and click OK button;
        String commentFromExcel = "comment for 9665";
        excel.operateOnCheckIn(l2, commentFromExcel, false);
        // 5. Comment is entered; Version window is closed; Information message
        // (i) Server read-only;

        actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9665);
        Assert.assertTrue(actualName.contains("[Read-Only]"), "Excel " + actualName + " is NOT opened in read only mode");

        // 6. Log into Share;
        ShareUser.login(drone, testUser);

        // 7. Go to site Document library;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 7. Unlocked document is present. A new minor version was created. It contains the specified comment;
        Assert.assertTrue(documentLibPage.isFileVisible(xlsFileName_9665 + fileType), "The saved document is not displayed.");
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, xlsFileName_9665 + fileType);

        Assert.assertFalse(fileInfo.isLocked(), "File " + xlsFileName_9665 + " is locked");

        // Navigate the document;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9665 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        // Assert.assertEquals(detailsPage.getDocumentVersion(), "1.1");
        String emptyComment = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(emptyComment, commentFromExcel);
    }

    @Test(groups = "alfresco-one")
    public void AONE_9666() throws Exception
    {
        // 1. MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);

        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9666, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;

        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9666);
        Assert.assertTrue(actualName.contains(xlsFileName_9666), "Microsoft Excel window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9666);
        
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

        // 5. Document version history is correctly dispalyed; Changes are applied;

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9666 + fileVersion).render();
        // 8. Changes are applied to the original file; Version is increased to
        // new major one.
        Assert.assertTrue(detailsPage.isCheckedOut(), "The document is not checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), fileVersion);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9667() throws Exception
    {

        // MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);
        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;

        // 1. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 2. Enter the credentials;
        // 3. Select the workbook from site Document Library you would like to
        // open;
        // 4. Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9667, testUser, DEFAULT_PASSWORD);

        // 4. Workbook is opened in MS Office Excel 2010;

        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9667);
        Assert.assertTrue(actualName.contains(xlsFileName_9667), "Microsoft Excel window is active.");
        
    }

    @Test(groups = "alfresco-one")
    public void AONE_9668() throws Exception
    {
        // 1. MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);

        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9668, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;
        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9668);
        Assert.assertTrue(actualName.contains(xlsFileName_9668), "Microsoft Excel window is active.");
        
        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9668);
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

        Ldtp l2 = excel.getAbstractUtil().setOnWindow(xlsFileName_9668);
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
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, xlsFileName_9668 + fileType);
        // 7. Excel Document is present in I'm Editing section;
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + xlsFileName_9668 + " isn't locked");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9669() throws Exception
    {

        // MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);

        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // 1. Enter the credentials;
        // 2. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 3. Select the workbook from site Document Library you would like to
        // open;
        // 4. Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9669, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;
        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9669);
        Assert.assertTrue(actualName.contains(xlsFileName_9669), "Microsoft Excel - Input_save window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9669);
        // Check Out the Excel Document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Make some changes to the excel document;
        // 2. Click Save button;
        Ldtp l2 = new Ldtp(excel.getAbstractUtil().findWindowName(xlsFileName_9669));
        excel.editOffice(l2, testName);
        excel.saveOffice(l2);

        // 3. Log into Share;
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // 4. Go to site Document Library and verify changes are applied to the
        // working copy;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, xlsFileName_9669 + fileType);

        // Excel Document is present in I'm Editing section;
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + xlsFileName_9669 + " isn't locked");
    }

    @Test(groups = "alfresco-one")
    public void AONE_9670() throws Exception
    {
        // 1. MS Office Excel 2013 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);

        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9670, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;
        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9670);
        Assert.assertTrue(actualName.contains(xlsFileName_9670), "Microsoft Excel - Input_checkin window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9670);
        // Excel Document is opened and checked out;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");
        l1.waitTime(2);
        l1.verifySetText("pane8", "");

        // 1. Make some changes to the workbook;
        Ldtp l2 = excel.getAbstractUtil().setOnWindow(xlsFileName_9670);
        excel.editOffice(l2, "new input data");
        l2.verifySetText("pane8", "new input data");

        // 2. Cick File->Info button;
        excel.goToFile(l2);
        excel.getAbstractUtil().clickOnObject(l2, "Info");
        // 3. Click Check In button;
        excel.getAbstractUtil().clickOnObject(l2, "CheckIn");

        // 3. Versions comment window pops up;
        // 4. Enter a comment for this version;
        // 5. Click OK button;
        String commentFromExcel = "comment from excel file check in";
        excel.operateOnCheckIn(l2, commentFromExcel, false);

        // 5. Version wimdow is closed; Information message (i) Server
        // read-only; This file was opened from server in read-only mode and to
        // buttons: Edit
        // Workbook and (X) close;
        excel.getAbstractUtil().setOnWindow("Input_checkin");
        actualName = excel.getAbstractUtil().findWindowName("Input_checkin");
        Assert.assertTrue(actualName.contains("[Read-Only]"), "Excel is NOT opened in read only mode");

        // 6. Cick File->Info button;
        // 7. Verify the comment is dispalyed near the version (Put cursor on
        // comment icon to see it); - can not be implemented

        // 8. Log into Share;
        ShareUser.login(drone, testUser);

        // 9. Navigate the workbook; Verify changes are applied; Version history
        // contains the entered comment;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9670 + fileType).render();

        // 9. Document is checked in; Changes are applied; Comment is
        // successfullly displayed in Versions history;
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent(), "Version History section is not present");
        String commentFromShare = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(commentFromShare, commentFromExcel);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9671() throws Exception
    {

        // 1. MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);

        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9671, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;

        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9671);
        Assert.assertTrue(actualName.contains(xlsFileName_9671), "Microsoft Excel - Input_keepcheckout window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9671);
        
        // Checkout the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Make some changes to the workbook;
        excel.editOffice(l1, "new input data");
        l1.verifySetText("pane8", "new input data");
        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // 2. Cick File->Info button;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        // 3. Click Check In button;
        excel.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 4. Enter a comment for this version;
        // 5. Select Keep the excel document checked out after checking in this
        // version;
        // 6. Click OK button;
        String commentFromExcel = "comment from excel file check in";
        excel.operateOnCheckIn(l1, commentFromExcel, true);

        // 7. Log into Share;
        ShareUser.login(drone, testUser);

        // 8. Navigate the document;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, xlsFileName_9671 + fileType);

        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + xlsFileName_9671 + " isn't locked");

        documentLibPage.getFileDirectoryInfo(xlsFileName_9671 + fileType).selectCancelEditing().render();

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9671 + fileType).render();
        // 8. Changes are applied to the original file; Version is increased to
        // new major one.
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), "2.0");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9672() throws Exception
    {

        // 1. MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);
        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9672, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;

        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9672);
        Assert.assertTrue(actualName.contains(xlsFileName_9672), "Microsoft Excel - Input_keepcheckout window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9672);
        // Checkout the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Make some changes to the workbook;
        excel.editOffice(l1, "new input data");

        // 2. Cick File->Info button;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        // 3. Click Discard check out button;
        excel.getAbstractUtil().clickOnObject(l1, "DiscardCheckOut");
        // 4. Click Yes button;
        l1.waitTime(2);
        excel.getAbstractUtil().clickOnObject(l1, "Yes");

        // 4. User returns to the document;
        excel.goToFile(l1);

        // 5. Log into Share;

        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // 6. Navigate the document;
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, xlsFileName_9672 + fileType);
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + xlsFileName_9672 + " isn't locked");
        // 6. Changes are not applied to the original file; Version is not
        // increased to new major one.
        String version = fileInfo.getVersionInfo();
        if (!version.isEmpty())
        {
            assertEquals(version, "1.0");
        }

    }

    @Test(groups = "alfresco-one")
    public void AONE_9673() throws Exception
    {

        // 1. MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);
        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9673, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;

        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9673);
        Assert.assertTrue(actualName.contains(xlsFileName_9673), "Microsoft Excel - Input_keepcheckout window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9673);
        // Checkout and check In the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // Make some changes to the workbook;
        excel.editOffice(l1, "input data");
        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        // Click Check In button;
        excel.getAbstractUtil().clickOnObject(l1, "CheckIn");
        String commentFromExcel = "comment from excel file check in";
        excel.operateOnCheckIn(l1, commentFromExcel, false);

        // Excel Document is opened in read-only mode;

        actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9673);
        Assert.assertTrue(actualName.contains("[Read-Only]"), "Excel " + actualName + " is NOT opened in read only mode");

        Ldtp l2 = excel.getAbstractUtil().setOnWindow(xlsFileName_9673);
        
        // 1. Update the document;
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
    public void AONE_9674() throws Exception
    {
        // 1. MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);

        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9674, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;

        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9674);
        Assert.assertTrue(actualName.contains(xlsFileName_9674), "Microsoft Excel - Input_emptycomm window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9674);
        // Checkout the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // Make some changes to the workbook;
        excel.editOffice(l1, "new input data");
        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");
        // Cick File->Info button;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");

        // 1.Click Check In button;
        excel.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 2. Don't enter a comment for this version;
        // 3. Click OK button;
        String commentFromExcel = "";
        excel.operateOnCheckIn(l1, commentFromExcel, false);

        // 4. Log into Share;

        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 5. Navigate the document; Verify changes are applied; Version is increased to new minor version; No comment is displayed in Comments part of the
        // version;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9674 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), "1.1");
        String emptyComment = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(emptyComment, "(No Comment)");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9675() throws Exception
    {
        // 1. MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);

        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9675, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;

        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9675);
        Assert.assertTrue(actualName.contains(xlsFileName_9675), "Microsoft Excel - Input_wildcardscomm window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9675);

        
        // Checkout the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // Make some changes to the workbook;
        excel.editOffice(l1, "new input data");
        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");
        // Cick File->Info button;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");

        // 1.Click Check In button;
        excel.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 2. Enter a comment contains wildcards (e.g.
        // !@#$%^&*()_+|\/?.,<>:;"'`=-{}[]);
        // 3. Click OK button;
        String commentFromExcel = "a(e.g. !@#$%^&*()_+|\\/?.,<<>>:;\"'`=-{}[]";
        excel.operateOnCheckIn(l1, commentFromExcel, false);

        // 4. Log into Share;

        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 5. Navigate the document; Verify changes are applied; Version history
        // contains is increased to minor;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9675 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), "1.1");
        String commentExpected = commentFromExcel.replace("<<", "<").replace(">>", ">");
        Assert.assertEquals(detailsPage.getCommentsOfLastCommit(), commentExpected);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9676() throws Exception
    {
        // 1. MS Office Excel 2010 is opened;
        Ldtp l = excel.openOfficeApplication(officePath);
        excel.goToFile(l);

        excel.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        excel.operateOnOpen(l, path, siteName, xlsFileName_9676, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Excel 2010;

        String actualName = excel.getAbstractUtil().findWindowName(xlsFileName_9676);
        Assert.assertTrue(actualName.contains(xlsFileName_9676), "Microsoft Excel - Input_cancel window is active.");

        Ldtp l1 = excel.getAbstractUtil().setOnWindow(xlsFileName_9676);
        // Checkout the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");
        excel.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Make some changes to the workbook;
        excel.editOffice(l1, "new input data");
        // Save the document
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "mnuSave");
        // 2. Cick File->Info button;
        excel.goToFile(l1);
        excel.getAbstractUtil().clickOnObject(l1, "Info");

        // 3.Click Check In button;
        excel.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 4. Enter a comment for this version;
        // 5. Click Cancel button;
        String commentFromExcel = "a comment for 9676";
        l1.enterString("txtVersionComments", commentFromExcel);
        l1.mouseLeftClick("btnCancel");

        // 6. Log into Share;

        ShareUser.login(drone, testUser);

        DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

        // 7. Excel Document is still checked out; Changes are not applied;
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, xlsFileName_9676 + fileType);
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + xlsFileName_9676 + " isn't locked");
        documentLibPage.getFileDirectoryInfo(xlsFileName_9676 + fileType).selectCancelEditing().render();
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(xlsFileName_9676 + fileType).render();
        String docVersion = detailsPage.getDocumentVersion();
        Assert.assertEquals(docVersion, "1.0");
        Assert.assertEquals(detailsPage.getCommentsOfLastCommit(), "(No Comment)");

    }
}
