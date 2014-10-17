package org.alfresco.share.sharepoint.office2013;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.alfresco.application.util.Application;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
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
public class MSPowerPointTests extends AbstractUtils
{
    private String testName;
    private String testUser;
    private String siteName;

    private static DocumentLibraryPage documentLibPage;
    private String pptxFileName_9842;
    private String pptxFileName_9843;
    private String pptxFileName_9844;
    private String pptxFileName_9845;
    private String pptxFileName_9846;
    private String pptxFileName_9848;
    private String pptxFileName_9847;
    private String pptxFileName_9849;
    private String pptxFileName_9850;
    private String pptxFileName_9851;
    private String pptxFileName_9852;
    private String pptxFileName_9853;
    private String pptxFileName_9854;
    private String fileType = ".pptx";

    public String officePath;
    public String sharepointPath;

    MicrosoftOffice2013 powerpoint = new MicrosoftOffice2013(Application.POWERPOINT, "2013");

    private static final String SHAREPOINT = "sharepoint";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName() + 113;
        testUser = getUserNameFreeDomain(testName);
        siteName = getSiteName(testName);

        pptxFileName_9842 = "SaveFileToShare";
        pptxFileName_9843 = "CheckInAfterSaving";
        pptxFileName_9844 = "PInputSavechanges";
        pptxFileName_9845 = "PInputOpen";
        pptxFileName_9846 = "PInputCheckout";
        pptxFileName_9848 = "PInputSave";
        pptxFileName_9847 = "PInputCheckin";
        pptxFileName_9849 = "PInputKeepcheckout";
        pptxFileName_9850 = "PInputDiscard";
        pptxFileName_9851 = "PInputRefresh";
        pptxFileName_9852 = "PInputEmptycomm";
        pptxFileName_9853 = "PInputWildcardscomm";
        pptxFileName_9854 = "PInputCancel";

        Runtime.getRuntime().exec("taskkill /F /IM POWERPNT.EXE");

        Properties officeAppProperty = new Properties();
        officeAppProperty.load(this.getClass().getClassLoader().getResourceAsStream("qa-share.properties"));
        String officeVersion = "2013";
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
        File file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9844 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9845 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9846 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9848 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9847 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9849 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9850 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9851 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9852 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9853 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();

        file = new File(DATA_FOLDER + SLASH + SHAREPOINT + SLASH + pptxFileName_9854 + fileType);
        ShareUserSitePage.uploadFile(drone, file).render();
    }

    @Test(groups = "alfresco-one")
    public void AONE_9842() throws Exception
    {

        // MS Office PowerPoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);

        // Enter some content to the document;
        powerpoint.editOffice(l, "new input data for 9842");

        // Save as window is opened
        powerpoint.navigateToSaveAsSharePointBrowse(l);

        String path = getPathSharepoint(drone);
        powerpoint.operateOnSaveAsWithSharepoint(l, path, siteName, pptxFileName_9842, testUser, DEFAULT_PASSWORD);

        String actualName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9842);
        Assert.assertTrue(actualName.contains(pptxFileName_9842) && actualName.contains("PowerPoint"), "File not found");

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(documentLibPage.isFileVisible(pptxFileName_9842 + fileType), "The saved document is not displayed.");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9843() throws Exception
    {

        // MS Office PowerPoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        // Enter some content to the document;
        powerpoint.editOffice(l, "new input data for 9843");

        // Save as window is opened
        powerpoint.navigateToSaveAsSharePointBrowse(l);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Enter the credentials;
        // Select site Document Library where you would like to save the
        // document;
        // Enter a name;
        // Click Save button;
        String path = getPathSharepoint(drone);
        powerpoint.operateOnSaveAsWithSharepoint(l, path, siteName, pptxFileName_9843, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9843);
        Assert.assertTrue(winName.contains(pptxFileName_9843) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9843);
        // Checkout the document
        powerpoint.checkOutOffice(l1);

        // 3. User return to the document; (I) Checked Out This file has been
        // checked out to you. Check In this file to allow other users to see
        // your changes
        // and edit this file;
        Ldtp l2 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9843);

        // 4. Click Check In;
        // 5. Enter any comment and click OK button;
        String commentFromPP = "comment for 9843";
        powerpoint.checkInOffice(l2, commentFromPP, false);

        String actualName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9843);
        Assert.assertTrue(actualName.contains("[Read-Only]"), "PowerPoint is NOT opened in read only mode");

        // 6. Log into Share;
        ShareUser.login(drone, testUser);

        // 7. Go to site Document library;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 7. Locked document and working copy are present;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9843 + fileType);
        Assert.assertEquals(detailsPage.getContentInfo(), "This document is locked by you for offline editing.");
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertEquals(properties.get("Name"), pptxFileName_9843 + " (Working Copy)" + fileType);
    }

    @Test(groups = "alfresco-one")
    public void AONE_9844() throws Exception
    {
        // 1. MS Office Powerpoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);

        powerpoint.navigateToOpenSharePointBrowse(l);

        // Open document window is opened;
        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        String path = getPathSharepoint(drone);
        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9844, testUser, DEFAULT_PASSWORD);

        // Workbook is opened in MS Office Powerpoint 2013;

        String actualName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9844);
        Assert.assertTrue(actualName.contains(pptxFileName_9844) && actualName.contains("PowerPoint"), "File was not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9844);

        // 1. Make some changes to the Powerpoint document;
        // 2. Click Save button;
        String newContent = testName;
        powerpoint.editOffice(l1, newContent);
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

        // 5. Document version history is correctly dispalyed; Changes are
        // applied;

        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9844 + fileType).render();
        // 8. Changes are applied to the original file; Version is increased to
        // new major one.
        Assert.assertTrue(detailsPage.isCheckedOut(), "The document is not checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), fileVersion);
        String documentContent = detailsPage.getDocumentBody();
        Assert.assertTrue(documentContent.contains(newContent), "Changes are present in the document.");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9845() throws Exception
    {
        // MS Office PowerPoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.navigateToOpenSharePointBrowse(l);

        // 1. Enter the credentials
        String path = getPathSharepoint(drone);

        // 2. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 3. Select the workbook from site Document Library you would like to
        // open;
        // 4. Click Open button;
        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9845, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9845);
        Assert.assertTrue(winName.contains(pptxFileName_9845) && winName.contains("PowerPoint"), "File not found");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9846() throws Exception
    {
        // 1. MS PowerPoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.navigateToOpenSharePointBrowse(l);

        // Enter the credentials;;
        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9846, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9846);
        Assert.assertTrue(winName.contains(pptxFileName_9846) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9846);

        // Checkout the document
        powerpoint.checkOutOffice(l1);

        Ldtp l2 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9846);
        // 4. Click File ->Info;
        powerpoint.goToFile(l2);
        powerpoint.getAbstractUtil().clickOnObject(l2, "Info");

        // 5. Verify Checked out information is added to Info;
        // TODO: 5. Check In and Discard check out actions are available;
        // "No else can edit this document or view your changes until it is checked it"
        // message
        // is displayed at the pane;

        Assert.assertTrue(powerpoint.getAbstractUtil().isObjectDisplayed(l1, "DiscardCheckOut"), "Discard Check Out action is not available");
        Assert.assertTrue(powerpoint.getAbstractUtil().isObjectDisplayed(l1, "CheckIn"), "Check In action is not available");

        
        // 6. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // 7. Go to site Document Library and verify workbook is in locked
        // state;
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, pptxFileName_9846 + fileType);

        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + pptxFileName_9846 + " isn't locked");

        TreeMenuNavigation treeMenuNavigation = documentLibPage.getLeftMenus().render();
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.IM_EDITING).render();
        Assert.assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, pptxFileName_9846 + fileType, TreeMenuNavigation.DocumentsMenu.IM_EDITING, true),
                pptxFileName_9846 + fileType + " cannot be found.");
        
    }

    @Test(groups = "alfresco-one")
    public void AONE_9847() throws Exception
    {

        // MS Office PowerPont 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.navigateToOpenSharePointBrowse(l);

        // 1. Enter the credentials;
        String path = getPathSharepoint(drone);

        // 2. Type url into File name field (e.g. http://<host>:7070/alfresco);
        // 3. Select the workbook from site Document Library you would like to
        // open;
        // 4. Click Open button;
        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9847, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9847);
        Assert.assertTrue(winName.contains(pptxFileName_9847) && winName.contains("PowerPoint"), "File not found");

        // edit document
        Ldtp l3 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9847);

        String newContent = testName;
        powerpoint.editOffice(l3, newContent);
        powerpoint.saveOffice(l3);

        // User login.
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9847 + fileType);
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertEquals(properties.get("Name"), pptxFileName_9847 + " (Working Copy)" + fileType);
        String documentContent = detailsPage.getDocumentBody();
        Assert.assertTrue(documentContent.contains(newContent), "Changes are present in the document.");
        
        // Excel Document is present in I'm Editing section;
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, pptxFileName_9847 + fileType);
        assertEquals(fileInfo.getContentInfo(), "This document is locked by you.", "File " + pptxFileName_9847 + " isn't locked");
        TreeMenuNavigation treeMenuNavigation = documentLibPage.getLeftMenus().render();
        treeMenuNavigation.selectDocumentNode(TreeMenuNavigation.DocumentsMenu.IM_EDITING).render();
        Assert.assertTrue(ShareUserSitePage.getDocTreeMenuWithRetry(drone, pptxFileName_9847 + fileType, TreeMenuNavigation.DocumentsMenu.IM_EDITING, true),
                pptxFileName_9847 + fileType + " cannot be found.");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9848() throws Exception
    {
        // 1. MS Office PowerPoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.navigateToOpenSharePointBrowse(l);

        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;
        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9848, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9848);
        Assert.assertTrue(winName.contains(pptxFileName_9848) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9848);

        // Checkout the document
        powerpoint.checkOutOffice(l1);

        // 1. Make some changes to the workbook;
        Ldtp l2 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9848);
        powerpoint.editOffice(l2, "new input data");

        // 2. Cick File->Info button;
        // 3. Click Check In button;
        // 3. Versions comment window pops up;
        // 4. Enter a comment for this version;
        // 5. Click OK button;
        String commentFromPPT = "comment file check in";
        powerpoint.checkInOffice(l2, commentFromPPT, false);

        // 8. Log into Share;
        ShareUser.login(drone, testUser);
        DocumentDetailsPage detailsPage;
        // 9. Navigate the workbook; Verify changes are applied; Version history
        // contains the entered comment;
        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        detailsPage = documentLibPage.selectFile(pptxFileName_9848 + fileType).render();

        // 9. Document is checked in; Changes are applied; Comment is
        // successfully displayed in Versions history;
        Assert.assertTrue(detailsPage.isVersionHistoryPanelPresent(), "Version History section is not present");
        String commentFromShare = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(commentFromShare, commentFromPPT);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9849() throws Exception
    {

        // 1. MS Office PowerPoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.navigateToOpenSharePointBrowse(l);

        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9849, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9849);
        Assert.assertTrue(winName.contains(pptxFileName_9849) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9849);

        // Checkout the document
        powerpoint.checkOutOffice(l1);

        // 1. Make some changes to the workbook;
        String newContent = "new input data";
        powerpoint.editOffice(l1, newContent);
        // l1.verifySetText("pane8", "new input data");
        // Save the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // 2. Cick File->Info button;
        // 3. Click Check In button;
        // 4. Enter a comment for this version;
        // 5. Select Keep the document checked out after checking in this
        // version;
        // 6. Click OK button;
        String commentFromWord = "comment from file check in";
        powerpoint.checkInOffice(l1, commentFromWord, true);

        // 7. Log into Share;
        ShareUser.login(drone, testUser);

        DocumentDetailsPage detailsPage;

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, pptxFileName_9849 + fileType);

        assertEquals(fileInfo.getContentInfo(), "This document is locked by you for offline editing.", "File " + pptxFileName_9849 + " isn't locked");

        documentLibPage.getFileDirectoryInfo(pptxFileName_9849 + fileType).selectCancelEditing().render();
        detailsPage = documentLibPage.selectFile(pptxFileName_9849 + fileType).render();
        
        String documentContent = detailsPage.getDocumentBody();
        Assert.assertTrue(documentContent.contains(newContent), "Changes are present in the document.");
        
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        Assert.assertEquals(detailsPage.getDocumentVersion(), "2.0");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9850() throws Exception
    {

        // 1. MS Office word 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.navigateToOpenSharePointBrowse(l);

        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9850, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9850);
        Assert.assertTrue(winName.contains(pptxFileName_9850) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9850);

        // Checkout the document
        powerpoint.checkOutOffice(l1);

        // 1. Make some changes to the file;
        String newContent = "new input data";
        powerpoint.editOffice(l1, newContent);

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

        // 6. Navigate the document;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9850 + fileType);

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
    public void AONE_9851() throws Exception
    {

        // 1. MS Office PowerPoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.navigateToOpenSharePointBrowse(l);

        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9851, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9851);
        Assert.assertTrue(winName.contains(pptxFileName_9851) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9851);

        // Checkout and check In the document
        powerpoint.checkOutOffice(l1);

        // Make some changes to the workbook;
        powerpoint.editOffice(l1, "new input data");

        // Save the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        // Click Check In button;

        String comment = "comment file check for 9851";
        powerpoint.checkInOffice(l1, comment, false);

        // Document is opened in read-only mode;

        String actualName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9851);
        Assert.assertTrue(actualName.contains("[Read-Only]"), "Excel is NOT opened in read only mode");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9852() throws Exception
    {
        // 1. MS Office PowerPoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.navigateToOpenSharePointBrowse(l);

        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the file from site Document Library you would like to open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9852, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9852);
        Assert.assertTrue(winName.contains(pptxFileName_9852) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9852);

        // Checkout the document
        powerpoint.checkOutOffice(l1);

        // Make some changes to the file;
        powerpoint.editOffice(l1, "new input data");

        // Save the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        // 1.Click Check In button;
        // 2. Don't enter a comment for this version;
        // 3. Click OK button;
        String commentFromExcel = "";
        powerpoint.checkInOffice(l1, commentFromExcel, false);

        // 4. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 5. Navigate the document; Verify changes are applied; Version history
        // contains is increased to major; no comment is displayed for the
        // version;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9852 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        String version = detailsPage.getDocumentVersion();
        Assert.assertEquals(version, "2.0");

        String emptyComment = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(emptyComment, "(No Comment)");

    }

    @Test(groups = "alfresco-one")
    public void AONE_9853() throws Exception
    {
        // 1. MS Office PowerPoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.navigateToOpenSharePointBrowse(l);

        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the file from site Document Library you would like to open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9853, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9853);
        Assert.assertTrue(winName.contains(pptxFileName_9853) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9853);

        // Checkout the document
        powerpoint.checkOutOffice(l1);
        // Make some changes to the file;
        powerpoint.editOffice(l1, "new input data");

        // Save the document
        powerpoint.goToFile(l1);
        powerpoint.getAbstractUtil().clickOnObject(l1, "Info");
        powerpoint.getAbstractUtil().clickOnObject(l1, "mnuSave");

        // Cick File->Info button;
        // 1.Click Check In button;
        // 2. Don't enter a comment for this version;
        // 3. Click OK button;
        String commentFromExcel = "a(e.g. !@#$%^&*()_+|\\/?.,:;\"'`=-{}[]";
        powerpoint.checkInOffice(l1, commentFromExcel, false);

        // 4. Log into Share;
        ShareUser.login(drone, testUser);

        documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // 5. Navigate the document; Verify changes are applied; Version history
        // contains is increased to major; no comment is displayed for the
        // version;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9853 + fileType).render();
        Assert.assertFalse(detailsPage.isCheckedOut(), "The document is checkout");
        String version = detailsPage.getDocumentVersion();
        Assert.assertEquals(version, "2.0");

        String fileComment = detailsPage.getCommentsOfLastCommit();
        Assert.assertEquals(fileComment, commentFromExcel);

    }

    @Test(groups = "alfresco-one")
    public void AONE_9854() throws Exception
    {
        // 1. MS Office PowerPoint 2013 is opened;
        Ldtp l = powerpoint.openOfficeApplication(officePath);
        powerpoint.navigateToOpenSharePointBrowse(l);

        String path = getPathSharepoint(drone);

        // Type url into File name field (e.g. http://<host>:7070/alfresco);
        // Select the workbook from site Document Library you would like to
        // open;
        // Click Open button;

        powerpoint.operateOnOpen(l, path, siteName, pptxFileName_9854, testUser, DEFAULT_PASSWORD);

        String winName = powerpoint.getAbstractUtil().findWindowName(pptxFileName_9854);
        Assert.assertTrue(winName.contains(pptxFileName_9854) && winName.contains("PowerPoint"), "File not found");

        Ldtp l1 = powerpoint.getAbstractUtil().setOnWindow(pptxFileName_9854);

        // Checkout the document
        powerpoint.checkOutOffice(l1);

        // 1. Make some changes to the workbook;
        String newContent = "new input data";
        powerpoint.editOffice(l1, newContent);

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

        // 7. Navigate the document;
        DocumentLibraryPage documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

        // 7. Excel Document is still checked out; Changes are not applied;
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(pptxFileName_9854 + fileType);
        assertEquals(detailsPage.getContentInfo(), "This document is locked by you for offline editing.", "File " + pptxFileName_9854 + " isn't locked");

        String documentContent = detailsPage.getDocumentBody();
        Assert.assertFalse(documentContent.contains(newContent), "Changes are applied to the document.");
    }

}
