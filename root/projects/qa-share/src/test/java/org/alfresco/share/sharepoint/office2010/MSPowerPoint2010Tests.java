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
import org.apache.camel.util.toolbox.FlexibleAggregationStrategy;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cobra.ldtp.Ldtp;

@Listeners(FailedTestListener.class)
public class MSPowerPoint2010Tests extends AbstractUtils
{
    private String testName;
    private String testUser;
    private String siteName;

    private static DocumentLibraryPage documentLibPage;
    private String pptxFileName_9677;
    private String pptxFileName_9678;
    private String pptxFileName_9679;
    private String pptxFileName_9680;
    private String pptxFileName_9681;

    private String pptxFileName_9683;
    private String pptxFileName_9682;
    private String pptxFileName_9684;
    private String pptxFileName_9685;
    private String pptxFileName_9686;
    private String pptxFileName_9687;
    private String pptxFileName_9688;
    private String pptxFileName_9689;

    MicorsoftOffice2010 powerpoint = new MicorsoftOffice2010(Application.POWERPOINT, "2010");
    public String officePath;
    public String sharepointPath;

    private static final String SHAREPOINT = "sharepoint";
    private String fileType = ".pptx";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName() + "01";
        testUser = getUserNameFreeDomain(testName);
        siteName = getSiteName(testName);

        pptxFileName_9677 = "SaveFileToShare";
        pptxFileName_9678 = "CheckInAfterSaving";
        pptxFileName_9679 = "PInputSavechanges";
        pptxFileName_9680 = "PInputOpen";
        pptxFileName_9681 = "PInputCheckout";
        pptxFileName_9683 = "PInputSave";
        pptxFileName_9682 = "PInputCheckin";
        pptxFileName_9684 = "PInputKeepcheckout";
        pptxFileName_9685 = "PInputDiscard";
        pptxFileName_9686 = "PInputRefresh";
        pptxFileName_9687 = "PInputWildcardscomm";
        pptxFileName_9688 = "PInputWildcardscomm";
        pptxFileName_9689 = "PInputCancel";

        Runtime.getRuntime().exec("taskkill /F /IM POWERPNT.EXE");

        Properties officeAppProperty = new Properties();
        officeAppProperty.load(this.getClass().getClassLoader().getResourceAsStream("qa-share.properties"));
        String officeVersion = "2010";
        sharepointPath = officeAppProperty.getProperty("sharepoint.path");
        officePath = officeAppProperty.getProperty("office" + officeVersion + ".path");

    }

    @AfterMethod(alwaysRun = true)
    public void teardownMethod() throws Exception
    {
        Runtime.getRuntime().exec("taskkill /F /IM POWERPNT.EXE");
        Runtime.getRuntime().exec("taskkill /F /IM CobraWinLDTP.EXE");
    }

    @Test(groups = { "DataPrepPowerPoint" })
    public void dataPrep_AONE() throws Exception
    {

        // Create normal User
        String[] testUser2 = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        // login with user
        ShareUser.login(drone, testUser);

        // // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Any office document was saved to the site document library;

        File file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9679 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9680 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9681 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9683 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9682 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9684 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9685 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9686 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9687 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9688 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9689 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
    }

    @Test(groups = "alfresco-one")
    public void AONE_9677() throws Exception
    {

        // MS Office PowerPoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);

        // Enter some content to the document;
        powerpoint.editOffice(l, "data for 9677");

        // Save as window is opened
        powerpoint.goToFile(l);
        powerpoint.getAbstractUtil().clickOnObject(l, "SaveAs");

        // Save As window is opened;

        String path = getPathSharepoint(drone);
        powerpoint.operateOnSaveAs(l, path, siteName, pptxFileName_9677, testUser, DEFAULT_PASSWORD);

        // powerpoint.operateOnSecurity(testUser, DEFAULT_PASSWORD);
        powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9677);
        String actualName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9677);
        Assert.assertTrue(actualName.contains(pptxFileName_9677) && actualName.contains("PowerPoint"), "File not found");

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(documentLibPage.isFileVisible(pptxFileName_9677 + fileType), "The saved document is not displayed.");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9678() throws Exception
    {

        // MS Office PowerPoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        // Enter some content to the document;
        powerpoint.editOffice(l, "new input data for 9843");

        // Save as window is opened
        powerpoint.goToFile(l);
        powerpoint.getAbstractUtil().clickOnObject(l, "SaveAs");
        // Save As window is opened;
        // powerpoint.operateOnSecurity(testUser, DEFAULT_PASSWORD);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Enter the credentials;
        // Select site Document Library where you would like to save the document;
        // Enter a name;
        // Click Save button;
        String path = getPathSharepoint(drone);
        powerpoint.operateOnSaveAs(l, path, siteName, pptxFileName_9678, testUser, DEFAULT_PASSWORD);

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9678);
        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9678);
        Assert.assertTrue(winName.contains(pptxFileName_9678) && winName.contains("PowerPoint"), "File not found");

        // 2. Click File->Info ;
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");

        // 3. Expand Version pane and click Check Out action;
        powerpoint.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 3. User return to the document; (I) Checked Out This file has been checked out to you. Check In this file to allow other users to see your changes
        // and edit this file;
        Ldtp l2 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9678);

        // 4. Click Check In;
        powerpoint.goToFile(l2);
        powerpoint.getAbstractUtil().clickOnObject(l2, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l2, "CheckIn");

        // 5. Enter any comment and click OK button;
        String commentFromPP = "comment for 9843";
        powerpoint.operateOnCheckIn(l2, commentFromPP, false);

        String actualName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9678);
        Assert.assertTrue(actualName.contains("[Read-Only]"), "PowerPoint is NOT opened in read only mode");

        Runtime.getRuntime().exec("taskkill /F /IM POWERPNT.EXE");

        // 6. Log into Share;
        ShareUser.login(drone, testUser);

        // 7. Go to site Document library;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 7. Locked document and working copy are present;
        Assert.assertTrue(documentLibPage.isFileVisible(pptxFileName_9678 + fileType), "The saved document is not displayed.");
    }
    
    
    @Test(groups = "alfresco-one")
    public void AONE_9679() throws Exception
    {
        // 1. MS Office Powerpoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9679, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Powerpoint 2010;

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9679);
        Assert.assertTrue(winName.contains(pptxFileName_9679) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9679);

        // 1. Make some changes to the Powerpoint document;
        // 2. Click Save button;
        powerpoint.editOffice(l1, testName);
        powerpoint.saveOffice(l1);

        // Click File ->Info -> Manage Versions;
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // 3. Click Refresh Versions list;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Refresh Server Versions List");
        // 3. New minor version is created
        String fileVersion = "1.1";
        String fileVersionObject = "btn" + fileVersion.replace(".", "");

        Assert.assertTrue(powerpoint.getAbstractUtil().isObjectDisplayed(l1, fileVersionObject), "Object with version " + fileVersion + " is not displayed");

        // 4. Log into Share;
        ShareUser.login(drone, testUser);

        // 5. Go to site Document Library and verify changes;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // 5. Document version history is correctly dispalyed; Changes are applied;

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9679 + fileVersion).render();
        // 8. Changes are applied to the original file; Version is increased to
        // new major one.
        Assert.assertTrue(detailsPage.isCheckedOut(), "The document is not checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), fileVersion);

    }

    

    @Test(groups = "alfresco-one")
    public void AONE_9680() throws Exception
    {
        // MS Office PowerPoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        //
        // // 1. Enter the credentials;
        // powerpoint.operateOnSecurity(testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // 2. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 3. Select the workbook from site Document Library you would like to open;
        // 4. Click Open button;
        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9680, testUser, DEFAULT_PASSWORD);

        powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9680);
        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9680);
        Assert.assertTrue(winName.contains(pptxFileName_9680) && winName.contains("PowerPoint"), "File not found");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9681() throws Exception
    {
        // 1. MS PowerPoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;

        // Enter the credentials;
        // powerpoint.operateOnSecurity(testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;
        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9681, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9681);
        Assert.assertTrue(winName.contains(pptxFileName_9681) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(winName);

        // Click File ->Info;
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");

        // 2. Expand the Manage versions section;
        powerpoint.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // 3. Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        Ldtp l2 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9681);
        // 4. Click File ->Info;
        powerpoint.goToFile(l2);
        powerpoint.getAbstractUtil().clickOnObject(l2, "Info");

        // 5. Verify Checked out information is added to Info;
        // TODO: 5. Check In and Discard check out actions are available; "No else can edit this document or view your changes until it is checked it" message
        // is displayed at the pane;

        Runtime.getRuntime().exec("taskkill /F /IM POWERPNT.EXE");

        // 6. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // 7. Go to site Document Library and verify workbook is in locked state;
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, pptxFileName_9681 + fileType);

        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + pptxFileName_9681 + " isn't locked");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9682() throws Exception
    {
        // MS Office PowerPont 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;
        //
        // // 1. Enter the credentials;
        // powerpoint.operateOnSecurity(testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // 2. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 3. Select the workbook from site Document Library you would like to open;
        // 4. Click Open button;
        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9682, testUser, DEFAULT_PASSWORD);
        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9682);
        Assert.assertTrue(winName.contains(pptxFileName_9682) && winName.contains("PowerPoint"), "File not found");

        // edit document
        Ldtp l3 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9682);

        powerpoint.editOffice(l3, testName);
        powerpoint.saveOffice(l3);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9682 + fileType).render();
        // Document is present in I'm Editing section;
        Assert.assertTrue(detailsPage.isCheckedOut(), "The document is not checkout");
        Assert.assertEquals(detailsPage.getContentInfo(), "This document is locked by you.");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9683() throws Exception
    {
        // 1. MS Office PowerPoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;

        // Enter the credentials;
        powerpoint.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;
        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9683, testUser, DEFAULT_PASSWORD);
        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9683);
        Assert.assertTrue(winName.contains(pptxFileName_9683) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9683);

        // Click File ->Info;
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");

        // 2. Expand the Manage versions section;
        powerpoint.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // 3. Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Make some changes to the workbook;
        Ldtp l2 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9683);
        powerpoint.editOffice(l2, "new input data");

        // 2. Cick File->Info button;
        powerpoint.goToFile(l2);
        powerpoint.getAbstractUtil().clickOnObject(l2, "Info");
        // 3. Click Check In button;
        powerpoint.getAbstractUtil().clickOnObject(l2, "CheckIn");

        // 3. Versions comment window pops up;
        // 4. Enter a comment for this version;
        // 5. Click OK button;
        String commentFromPPT = "comment file check in";
        powerpoint.operateOnCheckIn(l2, commentFromPPT, false);

        // 8. Log into Share;
        ShareUser.login(drone, testUser);
        DocumentDetailsPage detailsPage;
        // 9. Navigate the workbook; Verify changes are applied; Version history contains the entered comment;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        detailsPage = documentLibPage.selectFile(pptxFileName_9683 + fileType).render();

        // 9. Document is checked in; Changes are applied; Comment is successfully displayed in Versions history;
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent(), "Version History section is not present");
        String commentFromShare = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(commentFromShare, commentFromPPT);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9684() throws Exception
    {

        // 1. MS Office PowerPoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;

        // Enter the credentials;
        powerpoint.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9684, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9684);
        Assert.assertTrue(winName.contains(pptxFileName_9684) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9684);

        // Checkout the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Make some changes to the workbook;
        powerpoint.editOffice(l1, "new input data");
        // l1.verifySetText("pane8", "new input data");
        // Save the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // 2. Cick File->Info button;
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        // 3. Click Check In button;
        powerpoint.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 4. Enter a comment for this version;
        // 5. Select Keep the document checked out after checking in this version;
        // 6. Click OK button;
        String commentFromWord = "comment from file check in";
        powerpoint.operateOnCheckIn(l1, commentFromWord, true);

        // 7. Log into Share;
        ShareUser.login(drone, testUser);

        DocumentDetailsPage detailsPage;

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, pptxFileName_9684 + fileType);

        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + pptxFileName_9684 + " isn't locked");

        documentLibPage.getFileDirectoryInfo(pptxFileName_9684 + fileType).selectCancelEditing().render();
        detailsPage = documentLibPage.selectFile(pptxFileName_9684 + fileType).render();
        String docVersion = detailsPage.getDocumentVersion();
        // TODO: Version is not increased to major one
        // 8. Document is checked out; Changes are applied to the original file; Version is increased to new major one.
        Assert.assertEquals(docVersion, "1.1");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9685() throws Exception
    {

        // 1. MS Office word 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");

        // Open document window is opened;

        // Enter the credentials;
        powerpoint.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9685, testUser, DEFAULT_PASSWORD);
        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9685);
        Assert.assertTrue(winName.contains(pptxFileName_9685) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9685);

        // Checkout the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // Click Check Out action;
        l1.keyPress("tab");
        System.out.println("tab");
        l1.keyPress("enter");
        System.out.println("enter");
        l1.click("Check Out");
        System.out.println("cehckout");
        // 1. Make some changes to the file;
        powerpoint.editOffice(l1, "new input data");

        // Save the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // 2. Cick File->Info button;
        powerpoint.goToFile(l1);

        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        // 3. Click Discard check out button;

        powerpoint.getAbstractUtil().clickOnObject(l1, "DiscardCheckOut");

        // 4. Click Yes button;
        powerpoint.getAbstractUtil().clickOnObject(l1, "Yes");

        // TODO: Verify ERROR CHECK OUT
        powerpoint.goToFile(l1);

        // 5. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, pptxFileName_9685 + fileType);

        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + pptxFileName_9685 + " isn't locked");

        String version = fileInfo.getVersionInfo();
        if (!version.isEmpty())
        {
            assertEquals(version, "1.0");
        }
    }

    @Test(groups = "alfresco-one")
    public void AONE_9686() throws Exception
    {

        // 1. MS Office PowerPoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;

        // Enter the credentials;
        powerpoint.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9686, testUser, DEFAULT_PASSWORD);
        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9686);
        Assert.assertTrue(winName.contains(pptxFileName_9686) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9686);
        // Checkout and check In the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // Make some changes to the workbook;
        powerpoint.editOffice(l1, "new input data");

        // Save the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        // Click Check In button;
        powerpoint.getAbstractUtil().clickOnObject(l1, "CheckIn");

        String comment = "comment file check for 9686";
        powerpoint.operateOnCheckIn(l1, comment, false);

        // Document is opened in read-only mode;
        // TODO: READ-ONLY is displayed in the header of the document

    }

    @Test(groups = "alfresco-one")
    public void AONE_9687() throws Exception
    {
        // 1. MS Office PowerPoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");

        // Open document window is opened;

        l.waitTime(4);

        // Enter the credentials;
        powerpoint.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the file from site Document Library you would like to open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9687, testUser, DEFAULT_PASSWORD);
        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9687);
        Assert.assertTrue(winName.contains(pptxFileName_9687) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9687);

        // Checkout the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // Make some changes to the file;
        powerpoint.editOffice(l1, "new input data");

        // Save the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");

        // 1.Click Check In button;
        powerpoint.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 2. Don't enter a comment for this version;
        // 3. Click OK button;
        String commentFromExcel = "";
        powerpoint.operateOnCheckIn(l1, commentFromExcel, false);

        // 4. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 5. Navigate the document; Verify changes are applied; Version history contains is increased to major; no comment is displayed for the version;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9687 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        String version = detailsPage.getDocumentVersion();
        Assert.assertEquals(version, "1.1");

        String emptyComment = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(emptyComment, "(No Comment)");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9688() throws Exception
    {
        // 1. MS Office PowerPoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");

        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the file from site Document Library you would like to open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9688, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9688);
        Assert.assertTrue(winName.contains(pptxFileName_9688) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9688);

        // Checkout the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);
        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // Make some changes to the file;
        powerpoint.editOffice(l1, "new input data");

        // Save the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");

        // 1.Click Check In button;
        powerpoint.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 2. Don't enter a comment for this version;
        // 3. Click OK button;
        String commentFromExcel = "a(e.g. !@#$%^&*()_+|\\/?.,:;\"'`=-{}[]";

        powerpoint.operateOnCheckIn(l1, commentFromExcel, false);

        // 4. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 5. Navigate the document; Verify changes are applied; Version history contains is increased to major; no comment is displayed for the version;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9688 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        String version = detailsPage.getDocumentVersion();
        Assert.assertEquals(version, "1.1");

        String fileComment = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(fileComment, commentFromExcel);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9689() throws Exception
    {
        // 1. MS Office PowerPoint 2010 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.goToFile(l);

        powerpoint.getAbstractUtil().clickOnObject(l, "Open");
        // Open document window is opened;

        // Enter the credentials;
        powerpoint.operateOnSecurity(l, testUser, DEFAULT_PASSWORD);
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9689, testUser, DEFAULT_PASSWORD);
        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9689);
        Assert.assertTrue(winName.contains(pptxFileName_9689) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9689);

        // Checkout the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "ManageVersions");
        l1.waitTime(2);

        // Click Check Out action;
        l1.keyPress("tab");
        l1.keyPress("enter");
        l1.click("Check Out");

        // 1. Make some changes to the workbook;
        powerpoint.editOffice(l1, "new input data");

        // Save the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // 2. Cick File->Info button;
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");

        // 3.Click Check In button;
        powerpoint.getAbstractUtil().clickOnObject(l1, "CheckIn");

        // 4. Enter a comment for this version;
        // 5. Click Cancel button;
        String commentFromExcel = "comment";
        l1.enterString("txtVersionComments", commentFromExcel);
        l1.mouseLeftClick("btnCancel");

        // 6. Log into Share;
        ShareUser.login(drone, testUser);

        DocumentDetailsPage detailsPage;

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, pptxFileName_9689 + fileType);

        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + pptxFileName_9689 + " isn't locked");

        documentLibPage.getFileDirectoryInfo(pptxFileName_9689 + fileType).selectCancelEditing().render();
        detailsPage = documentLibPage.selectFile(pptxFileName_9689 + fileType).render();
        String docVersion = detailsPage.getDocumentVersion();

        Assert.assertEquals(docVersion, "1.0");
    }


}
