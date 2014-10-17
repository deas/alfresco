package org.alfresco.share.sharepoint.office2010;

import static org.testng.Assert.assertEquals;

import java.io.File;

import java.util.Properties;

import org.alfresco.application.util.Application;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.WebDroneType;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.windows.application.MicorsoftOffice2010;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;

/**
 * @author bogdan.bocancea
 */

@Listeners(FailedTestListener.class)
public class MSWord2010Tests extends AbstractUtils
{
    private String testName;
    private String testUser;
    private String siteName;

    private String docFileName_9643;
    private String docFileName_9644;
    private String docFileName_9645;
    private String docFileName_9646;
    private String docFileName_9647;
    private String docFileName_9648;
    private String docFileName_9649;
    private String docFileName_9650;
    private String docFileName_9651;
    private String docFileName_9652;
    private String docFileName_9659;
    private String docFileName_9660;
    private String docFileName_9661;
    private String docFileName_9662;

    MicorsoftOffice2010 word = new MicorsoftOffice2010(Application.WORD, "2010");

    private static DocumentLibraryPage documentLibPage;
    public String officePath;
    public String sharepointPath;
    private static final String SHAREPOINT = "sharepoint";

    private String fileType = ".docx";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName() + 16;
        testUser = getUserNameFreeDomain(testName);
        siteName = getSiteName(testName);

        docFileName_9643 = "WSaveFileToShare";
        docFileName_9644 = "WInputSave";
        docFileName_9645 = "WInputSavechanges";
        docFileName_9646 = "WInputOpen";
        docFileName_9647 = "WCheckInAfterSaving";
        docFileName_9648 = "WInputCheckout";
        docFileName_9649 = "WInputCheckin";
        docFileName_9650 = "Winput_keepcheckout";
        docFileName_9651 = "WInputKeepcheckout";
        docFileName_9652 = "WInputDiscard";
        docFileName_9659 = "WInputRefresh";
        docFileName_9660 = "WInputEmptycomm";
        docFileName_9661 = "WInputWildcardscomm";
        docFileName_9662 = "WInputCancel";

        Runtime.getRuntime().exec("taskkill /F /IM WINWORD.EXE");

        Properties officeAppProperty = new Properties();
        officeAppProperty.load(this.getClass().getClassLoader().getResourceAsStream("qa-share.properties"));
        String officeVersion = "2013";
        sharepointPath = officeAppProperty.getProperty("sharepoint.path");
        officePath = officeAppProperty.getProperty("office" + officeVersion + ".path");

    }

    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {
        Runtime.getRuntime().exec("taskkill /F /IM WINWORD.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM CobraWinLDTP.EXE");
    }

    @Test(groups = { "DataPrepWord" })
    public void dataPrep_AONE() throws Exception
    {
        // Create normal User
        String[] testUser1 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

        // login with user
        ShareUser.login(drone, testUser);

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        File file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9644 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9645 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9646 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9648 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9649 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9650 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9651 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9652 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9659 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9660 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9661 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + docFileName_9662 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

    }

    @Test(groups = "alfresco-one")
    public void AONE_9643() throws Exception
    {

        // MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);

        // Enter some content to the document;
        word.editOffice(l, "new input data");

        // Save as window is opened
        word.goToFile(l);
        word.getAbstractUtil().clickOnObject(l, "SaveAs");

        // Save As window is opened;
        // 1. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 2. Enter the credentials;
        // 3. Select site Document Library where you would like to save the document;
        // 4. Enter a workbook name;
        // 5. Click Save button;
        String path = getPathSharepoint(drone);
        word.operateOnSaveAs(l, path, siteName, docFileName_9643, testUser, DEFAULT_PASSWORD);

        word.getAbstractUtil().setOnWindow(docFileName_9643);
        String actualName = word.getAbstractUtil().findWindowName(docFileName_9643);
        Assert.assertTrue(actualName.contains(docFileName_9643) && actualName.contains("Word"), "File not found");

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // verify that the document is visible in document library
        Assert.assertTrue(documentLibPage.isFileVisible(docFileName_9643 + fileType), "The saved document is not displayed.");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9644() throws Exception
    {

        // MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // 1. Enter the credentials;
        // 2. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 3. Select the workbook from site Document Library you would like to open;
        // 4. Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9644, testUser, DEFAULT_PASSWORD);

        String actualName = word.getAbstractUtil().findWindowName(docFileName_9644);
        Assert.assertTrue(actualName.contains(docFileName_9644) && actualName.contains("Word"), "File not found");

        Ldtp l2 = word.getAbstractUtil().setOnWindow(docFileName_9644);

        // edit document
        word.editOffice(l2, testName);
        word.saveOffice(l2);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, docFileName_9644 + fileType);

        // verify that the document is locked
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you.", "File " + docFileName_9644 + " isn't locked");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9645() throws Exception
    {
        // 1. MS Office Excel 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9645, testUser, DEFAULT_PASSWORD);

        String actualName = word.getAbstractUtil().findWindowName(docFileName_9645);
        Assert.assertTrue(actualName.contains(docFileName_9645) && actualName.contains("Word"), "File not found");

        Ldtp l1 = word.getAbstractUtil().setOnWindow(docFileName_9645);

        // 1. Make some changes to the excel document;
        // 2. Click Save button;
        word.editOffice(l1, testName);
        word.saveOffice(l1);

        // Click File ->Info -> Manage Versions;
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");

        // 3. Click Refresh Versions list;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Refresh Server Versions List");
        // 3. New minor version is created
        String fileVersion = "1.1";
        String fileVersionObject = "btn" + fileVersion.replace(".", "");

        Assert.assertTrue(word.getAbstractUtil().isObjectDisplayed(l1, fileVersionObject), "Object with version " + fileVersion + " is not displayed");

        // 4. Log into Share;
        ShareUser.login(drone, testUser);

        // 5. Go to site Document Library and verify changes;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // 5. Document version history is correctly dispalyed; Changes are applied;

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_9645 + fileType).render();
        // 8. Changes are applied to the original file; Version is increased to
        // new major one.
        Assert.assertTrue(detailsPage.isCheckedOut(), "The document is not checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), fileVersion);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9646() throws Exception
    {
        // MS Office word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // 1. Enter the credentials;
        // 2. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 3. Select the workbook from site Document Library you would like to open;
        // 4. Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9646, testUser, DEFAULT_PASSWORD);

        // get the title name
        String actualName = word.getAbstractUtil().findWindowName(docFileName_9646);

        // verify word title
        Assert.assertTrue(actualName.contains(docFileName_9646) && actualName.contains("Word"), "File not found");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9647() throws Exception
    {

        // MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        // Enter some content to the document;
        word.editOffice(l, "new input data for 9647");

        // Save as window is opened
        word.goToFile(l);
        word.getAbstractUtil().clickOnObject(l, "SaveAs");
        // Save As window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Enter the credentials;
        // Select site Document Library where you would like to save the document;
        // Enter a workbook name;
        // Click Save button;
        String path = getPathSharepoint(drone);
        word.operateOnSaveAs(l, path, siteName, docFileName_9647, testUser, DEFAULT_PASSWORD);

        // verify the word title
        String actualName = word.getAbstractUtil().findWindowName(docFileName_9647);
        Assert.assertTrue(actualName.contains(docFileName_9647) && actualName.contains("Word"), "File not found");

        Ldtp l1 = word.getAbstractUtil().setOnWindow(docFileName_9647);
        // 2. Click File->Info ;
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");

        // 3. Expand Version pane and click Check Out action;
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 3. User return to the document; (I) Checked Out This file has been checked out to you. Check In this file to allow other users to see your changes
        // and edit this file;
        Ldtp l2 = word.getAbstractUtil().setOnWindow(docFileName_9647);

        // 4. Click Check In;
        word.goToFile(l2);
        word.getAbstractUtil().clickOnObject(l2, "Info");
        word.getAbstractUtil().clickOnObject(l2, "CheckIn");

        // 5. Enter any comment and click OK button;
        String commentFromWord = "comment for 9647";
        word.operateOnCheckIn(l2, commentFromWord, false);

        String actualName2 = word.getAbstractUtil().findWindowName(docFileName_9647);
        Assert.assertTrue(actualName2.contains("[Read-Only]"), "Word is NOT opened in read only mode");

        // 6. Log into Share;
        ShareUser.login(drone, testUser);

        // 7. Go to site Document library;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 7. Locked document and working copy are present;
        Assert.assertTrue(documentLibPage.isFileVisible(docFileName_9647 + fileType), "The saved document is not displayed.");
    }

    @Test(groups = "alfresco-one")
    public void AONE_9648() throws Exception
    {
        // 1. MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Enter the credentials;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9648, testUser, DEFAULT_PASSWORD);

        String actualName = word.getAbstractUtil().findWindowName(docFileName_9648);
        Assert.assertTrue(actualName.contains(docFileName_9648) && actualName.contains("Word"), "File not found");

        Ldtp l1 = word.getAbstractUtil().setOnWindow(docFileName_9648);

        // Click File ->Info;
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");

        // 2. Expand the Manage versions section;
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // 3. Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        Ldtp l2 = word.getAbstractUtil().setOnWindow(docFileName_9648);
        // 4. Click File ->Info;
        word.goToFile(l2);
        word.getAbstractUtil().clickOnObject(l2, "Info");

        // 5. Verify Checked out information is added to Info;
        // 5. Check In and Discard check out actions are available; "No else can edit this document or view your changes until it is checked it" message
        // is displayed at the pane;
        Assert.assertTrue(word.getAbstractUtil().isObjectDisplayed(l1, "DiscardCheckOut"), "Discard Check Out action is not available");
        Assert.assertTrue(word.getAbstractUtil().isObjectDisplayed(l1, "CheckIn"), "Check In action is not available");

        // 6. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, docFileName_9648 + fileType);

        // verify that the document is locked
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + docFileName_9650 + " isn't locked");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9649() throws Exception
    {
        // 1. MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Enter the credentials;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9649, testUser, DEFAULT_PASSWORD);

        String actualName = word.getAbstractUtil().findWindowName(docFileName_9649);
        Assert.assertTrue(actualName.contains(docFileName_9649) && actualName.contains("Word"), "File not found");

        Ldtp l1 = word.getAbstractUtil().setOnWindow(docFileName_9649);

        // Click File ->Info;
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");

        // 2. Expand the Manage versions section;
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // 3. Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Make some changes to the workbook;
        Ldtp l2 = word.getAbstractUtil().setOnWindow(docFileName_9649);
        word.editOffice(l2, "new input data");

        // 2. Cick File->Info button;
        word.goToFile(l2);
        word.getAbstractUtil().clickOnObject(l2, "Info");
        // 3. Click Check In button;
        word.getAbstractUtil().clickOnObject(l2, "CheckIn");

        // 3. Versions comment window pops up;
        // 4. Enter a comment for this version;
        // 5. Click OK button;
        String commentFromWord = "comment from word file check in";
        word.operateOnCheckIn(l2, commentFromWord, false);

        // TODO: 5. Version window is closed; Information message (i) Server read-only; This file was opened from server in read-only mode and to buttons: Edit
        // Workbook and (X) close;

        // 6. Cick File->Info button;
        // word.goToFile(l);
        // word.getAbstractUtil().clickOnObject(l, "Info");

        // TODO: 7. Verify the comment is displayed near the version (Put cursor on comment icon to see it);

        // 8. Log into Share;
        ShareUser.login(drone, testUser);
        DocumentDetailsPage detailsPage;
        // 9. Navigate the workbook; Verify changes are applied; Version history contains the entered comment;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        detailsPage = documentLibPage.selectFile(docFileName_9649 + fileType).render();

        // 9. Document is checked in; Changes are applied; Comment is successfully displayed in Versions history;
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent(), "Version History section is not present");
        String commentFromShare = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(commentFromShare, commentFromWord);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9650() throws Exception
    {

        // 1. MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Enter the credentials;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9650, testUser, DEFAULT_PASSWORD);

        // Ldtp l1 = word.getAbstractUtil().setOnWindow("Winput_keepcheckout");
        //
        // // Checkout the document
        // word.goToFile(l1);
        // word.getAbstractUtil().clickOnObject(l1, "Info");
        // word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        // l1.waitTime(2);
        //
        // // Click Check Out action;
        // l1.keyPress("tab");
        // l1.keyPress("enter");
        // l1.click("Check Out");
        //
        // // 1. Make some changes to the workbook;
        // word.editOffice(l1, "new input data");
        //
        // // Save the document
        // word.goToFile(l1);
        // word.getAbstractUtil().clickOnObject(l1, "Info");
        // word.getAbstractUtil().clickOnObject(l1, "mnuSave");
        //
        // // 2. Cick File->Info button;
        // word.goToFile(l1);
        // word.getAbstractUtil().clickOnObject(l1, "Info");
        // // 3. Click Check In button;
        // word.getAbstractUtil().clickOnObject(l1, "CheckIn");
        //
        // // 4. Enter a comment for this version;
        // // 5. Select Keep the word document checked out after checking in this version;
        // // 6. Click OK button;
        // String commentFromWord = "comment from word file check in";
        // word.operateOnCheckIn(l1, commentFromWord, true);
        //
        // // 7. Log into Share;
        // ShareUser.login(drone, testUser);
        //
        // DocumentDetailsPage detailsPage;
        //
        // documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        //
        // FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, docFileName_9650);
        //
        // assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + docFileName_9650 + " isn't locked");
        //
        // documentLibPage.getFileDirectoryInfo(docFileName_9650).selectCancelEditing().render();
        // detailsPage = documentLibPage.selectFile(docFileName_9650).render();
        // String docVersion = detailsPage.getDocumentVersion();
        //
        // // 8. Document is checked out; Changes are applied to the original file; Version is increased to new major one.
        // Assert.assertEquals(docVersion, "2.0");

        // test AONE_9652

    }

    @Test(groups = "alfresco-one")
    public void AONE_9651() throws Exception
    {

        // 1. MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");

        // Open document window is opened;
        // Enter the credentials;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9651, testUser, DEFAULT_PASSWORD);

        String actualName = word.getAbstractUtil().findWindowName(docFileName_9651);
        Assert.assertTrue(actualName.contains(docFileName_9651) && actualName.contains("Word"), "File not found");

        Ldtp l1 = word.getAbstractUtil().setOnWindow(docFileName_9651);

        // Checkout the document
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Make some changes to the file;
        word.editOffice(l1, "new input data");

        // 2. Cick File->Info button;
        word.goToFile(l1);

        word.getAbstractUtil().clickOnObject(l1, "Info");

        // 3. Click Discard check out button;
        word.getAbstractUtil().clickOnObject(l1, "DiscardCheckOut");

        word.getAbstractUtil().clickOnObject(l1, "Yes");

        // 4. User returns to the document;
        word.goToFile(l1);

        // 5. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, docFileName_9651 + fileType);

        // verify that the document is locked
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + docFileName_9650 + " isn't locked");

        String version = fileInfo.getVersionInfo();
        if (!version.isEmpty())
        {
            assertEquals(version, "1.0");
        }
    }

    @Test(groups = "alfresco-one")
    public void AONE_9652() throws Exception
    {

        // 1. MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");

        // Open document window is opened;
        // Enter the credentials;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9652, testUser, DEFAULT_PASSWORD);

        String actualName = word.getAbstractUtil().findWindowName(docFileName_9652);
        Assert.assertTrue(actualName.contains(docFileName_9652) && actualName.contains("Word"), "File not found");

        Ldtp l1 = word.getAbstractUtil().setOnWindow(docFileName_9652);

        // Checkout the document
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Don't make some changes to the document;
        // 2. Cick File->Info button;
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        // Click Refresh Server Versions List
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Refresh Server Versions List");

        // New minor version is created
        String fileVersion = "1.1";
        String fileVersionObject = "btn" + fileVersion.replace(".", "");
        Assert.assertTrue(word.getAbstractUtil().isObjectDisplayed(l1, fileVersionObject), "Object with version " + fileVersion + " is not displayed");

        // 3. Expand Vresions Menu and select Compare with Major version;
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Compare with Major Version");
        // 3. New MS Word application with the major version is opened; Word notification "Word found no differences between the two documents"
        Ldtp l2 = word.getAbstractUtil().setOnWindow("Microsoft Word");
        String expectedMessage = "Word found no differences between the two documents.";
        String objectName = expectedMessage.replace(" ", "").replace(".", "");
        Assert.assertTrue(word.getAbstractUtil().isObjectDisplayed(l2, objectName),
                "Word found no differences between the two documents - message is not displayed");
        l2.click("OK");
        String actualName1 = word.getAbstractUtil().findWindowName("version 1.0");
        Assert.assertTrue(actualName.contains(docFileName_9652 + fileType) && actualName1.contains("Word") && actualName1.contains("version 1.0"),
                "File not found");

    }

    // @Test(groups = "alfresco-one")
    // public void AONE_9653() throws Exception
    // {
    //
    // // 1. MS Office Word 2010 is opened;
    // Ldtp l = word.openOfficeApplication(officePath);
    // word.goToFile(l);
    //
    // word.getAbstractUtil().clickOnObject(l, "Open");
    // // Open document window is opened;
    // // Enter the credentials;
    // // Type url into File name field (e.g. http://<host>:7070/alfresco);
    // // Select the workbook from site Document Library you would like to open;
    // // Click Open button;
    // String path = getPathSharepoint(drone);
    // word.operateOnOpen(path, siteName, docFileName_9652, testUser, DEFAULT_PASSWORD);
    //
    // Ldtp l1 = word.getAbstractUtil().setOnWindow("aa");
    //
    // // Checkout the document
    // word.goToFile(l1);
    // word.getAbstractUtil().clickOnObject(l1, "Info");
    // word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
    // l1.waitTime(2);
    //
    // // Click Check Out action;
    // l1.keyPress("tab");
    // l1.keyPress("enter");
    // l1.click("Check Out");
    //
    //
    //
    //
    //
    // // 1. Make some changes to the document;
    // word.editOffice(l1, "new input data");
    //
    // // 2. Cick File->Info button;
    // word.goToFile(l1);
    // word.getAbstractUtil().clickOnObject(l1, "Info");
    // word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
    // // Click Refresh Server Versions List
    // l1.keyPress("tab");
    // l1.keyPress("enter");
    // l1.click("Refresh Server Versions List");
    //
    // // New minor version is created
    // String fileVersion = "2.1";
    // String fileVersionObject = "btn" + fileVersion.replace(".", "");
    // Assert.assertTrue(word.getAbstractUtil().isObjectDisplayed(l1, fileVersionObject), "Object with version " + fileVersion + " is not displayed");
    //
    // // 3. Expand Versions Menu and select Compare with Major version;
    // word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
    // l1.keyPress("tab");
    // l1.keyPress("enter");
    // l1.click("Compare with Major Version");
    // // 3. New MS Word application with the major version is opened;
    // word.getAbstractUtil().setOnWindow("Maa,");
    // String actualName = word.getAbstractUtil().findWindowName("aa,");
    // Assert.assertTrue(actualName.contains("aa,") && actualName.contains("Word") && actualName.contains("version 2.0"), "File not found");
    //
    // // 4. Click Compare button at the top of just opened major version;
    // // 4. Differences are underlined and marked with another color in new openede Compare document;
    //
    // }

    @Test(groups = "alfresco-one")
    public void AONE_9659() throws Exception
    {
        // 1. MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Enter the credentials;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9659, testUser, DEFAULT_PASSWORD);

        String actualName = word.getAbstractUtil().findWindowName(docFileName_9659);
        Assert.assertTrue(actualName.contains(docFileName_9659) && actualName.contains("Word"), "File not found");

        Ldtp l1 = word.getAbstractUtil().setOnWindow(docFileName_9659);

        // Checkout and check In the document
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // Make some changes to the workbook;
        word.editOffice(l1, "new input data");

        // Save the document
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        // Click Check In button;
        word.getAbstractUtil().clickOnObject(l1, "CheckIn");
        String commentFromWord = "comment from word file check in";
        word.operateOnCheckIn(l1, commentFromWord, false);

        // Word Document is opened in read-only mode;
        String actualName1 = word.getAbstractUtil().findWindowName("Winput_refresh");
        Assert.assertTrue(actualName1.contains("[Read-Only]"), "Word is NOT opened in read only mode");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9660() throws Exception
    {
        // 1. MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");

        // Open document window is opened;
        // Enter the credentials;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the file from site Document Library you would like to open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9660, testUser, DEFAULT_PASSWORD);

        String actualName = word.getAbstractUtil().findWindowName(docFileName_9660);
        Assert.assertTrue(actualName.contains(docFileName_9660) && actualName.contains("Word"), "File not found");

        Ldtp l1 = word.getAbstractUtil().setOnWindow(docFileName_9660);

        // Checkout the document
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // Make some changes to the file;
        word.editOffice(l1, "new input data");

        // Save the document
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");

        // 1.Click Check In button;
        word.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 2. Don't enter a comment for this version;
        // 3. Click OK button;
        String commentFromExcel = "";
        word.operateOnCheckIn(l1, commentFromExcel, false);

        // 4. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 5. Navigate the document; Verify changes are applied; Version history contains is increased to major; no comment is displayed for the version;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_9660 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        String version = detailsPage.getDocumentVersion();
        Assert.assertEquals(version, "1.1");

        String emptyComment = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(emptyComment, "(No Comment)");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9661() throws Exception
    {
        // 1. MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");

        // Open document window is opened;
        // Enter the credentials;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the file from site Document Library you would like to open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9661, testUser, DEFAULT_PASSWORD);

        String actualName = word.getAbstractUtil().findWindowName(docFileName_9661);
        Assert.assertTrue(actualName.contains(docFileName_9661) && actualName.contains("Word"), "File not found");

        Ldtp l1 = word.getAbstractUtil().setOnWindow(docFileName_9661);

        // Checkout the document
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // Make some changes to the file;
        word.editOffice(l1, "new input data");

        // Save the document
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");

        // 1.Click Check In button;
        word.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 2. Don't enter a comment for this version;
        // 3. Click OK button;
        String commentFromExcel = "a(e.g. !@#$%^&*()_+|\\/?.,:;\"'`=-{}[]";

        word.operateOnCheckIn(l1, commentFromExcel, false);

        // 4. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 5. Navigate the document; Verify changes are applied; Version history contains is increased to major; no comment is displayed for the version;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(docFileName_9661 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        String version = detailsPage.getDocumentVersion();
        Assert.assertEquals(version, "1.1");

        String fileComment = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(fileComment, commentFromExcel);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9662() throws Exception
    {
        // 1. MS Office Word 2010 is opened;
        Ldtp l = word.openOfficeApplication(officePath);
        word.goToFile(l);

        word.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Enter the credentials;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        word.operateOnOpen(l, path, siteName, docFileName_9662, testUser, DEFAULT_PASSWORD);

        Ldtp l1 = word.getAbstractUtil().setOnWindow("Winput_cancel");

        // Checkout the document
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Make some changes to the workbook;
        word.editOffice(l1, "new input data");

        // Save the document
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");
        word.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // 2. Cick File->Info button;
        word.goToFile(l1);
        word.getAbstractUtil().clickOnObject(l1, "Info");

        // 3.Click Check In button;
        word.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 4. Enter a comment for this version;
        // 5. Click Cancel button;
        String commentFromExcel = "comment";
        l1.enterString("txtVersionComments", commentFromExcel);
        l1.mouseLeftClick("btnCancel");

        // 6. Log into Share;
        ShareUser.login(drone, testUser);

        DocumentDetailsPage detailsPage;

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, docFileName_9662);

        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + docFileName_9662 + " isn't locked");

        documentLibPage.getFileDirectoryInfo(docFileName_9662).selectCancelEditing().render();
        detailsPage = documentLibPage.selectFile(docFileName_9662).render();
        String docVersion = detailsPage.getDocumentVersion();

        Assert.assertEquals(docVersion, "1.0");
    }

    @Test(groups = "alfresco-one")
    public void AONE_9663() throws Exception
    {
        String testName = getTestName();
        DocumentLibraryPage customDocumentLibPage;
        WebDrone thisDrone;
        DocumentDetailsPage detailsPage;
        DocumentDetailsPage customDetailsPage;

        setupCustomDrone(WebDroneType.HybridDrone);
        thisDrone = customDrone;

        // Create User
        String testUser2 = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // 1. Connect to Share with user1 and browse to document library.
        ShareUser.login(drone, testUser);

        // User2 is invited to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser2, siteName, UserRole.MANAGER);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // 2. Upload a word document in the document library and open its details page.
        String fileName = getFileName(testName) + "3.txt";
        String[] fileInfo = { fileName, DOCLIB };
        documentLibPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        detailsPage = documentLibPage.selectFile(fileName).render();

        // 3. In another browser or client machine, login to Share as user2, and browse to the same document details page.
        ShareUser.login(thisDrone, testUser2, DEFAULT_PASSWORD);
        customDocumentLibPage = ShareUser.openSitesDocumentLibrary(thisDrone, siteName);
        customDetailsPage = customDocumentLibPage.selectFile(fileName).render();

        // 4. As user1, click "Edit Offline". Document should show up as locked by user1 in user1's Share UI.
        detailsPage = ShareUser.openDocumentDetailPage(drone, fileName).render();
        detailsPage.selectEditOffLine(null).render();
        Assert.assertTrue(detailsPage.isCheckedOut());

        // 5. As user2, without refreshing the page, click "Edit Online", then click OK.
        customDetailsPage = ShareUser.openDocumentDetailPage(thisDrone, fileName).render();

        String errorMessage = customDetailsPage.getErrorEditOfflineDocument();
        Assert.assertEquals(errorMessage, "You cannot edit '" + fileName + "'.");

    }

}