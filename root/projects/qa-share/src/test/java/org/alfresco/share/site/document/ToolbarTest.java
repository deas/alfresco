package org.alfresco.share.site.document;

import static org.alfresco.share.util.ShareUser.openSiteDashboard;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.enums.ZoomStyle;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditInGoogleDocsPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.GoogleDocsAuthorisation;
import org.alfresco.po.share.site.document.GoogleDocsDiscardChanges;
import org.alfresco.po.share.site.document.LibraryOption;
import org.alfresco.po.share.site.document.PaginationForm;
import org.alfresco.po.share.site.document.SelectedItemsOptions;
import org.alfresco.po.share.site.document.SortField;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserGoogleDocs;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Maryia Zaichanka
 */

@Listeners(FailedTestListener.class)
public class ToolbarTest extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(ToolbarTest.class);

    private String testUser1 = "User-14003";

    private void prepare()
    {
        // Deleting cookies
        ShareUser.deleteSiteCookies(drone, ShareUserGoogleDocs.googleURL);
        ShareUser.deleteSiteCookies(drone, ShareUserGoogleDocs.googlePlusURL);

        logger.info("Deleted google cookies successfully.");
    }

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {

        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }


    @AfterMethod(groups = { "AlfrescoOne" })
    public void quit() throws Exception
    {
        // Login as created user
        try
        {
            ShareUser.logout(drone);
            logger.info("Toolbar user logged out - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_13995() throws Exception
    {
        // Create site
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = testName + System.currentTimeMillis();
        String file = "test.txt";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload .txt file.
        String[] fileInfo = { file };
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        // Verify the list of components of the Document Library toolbar
        Assert.assertTrue(documentLibraryPage.isCheckBoxPresent(), "No check-box is present");

        // Create menu
        DocumentLibraryNavigation docLib = documentLibraryPage.getNavigation().render();
        docLib.selectCreateContentDropdown().render();
        if (!alfrescoVersion.isCloud())
        {
            Assert.assertTrue(docLib.isCreateContentPresent(ContentType.PLAINTEXT), "The Plaintext component isn't present");
            Assert.assertTrue(docLib.isCreateContentPresent(ContentType.HTML), "The HTML component isn't present");
            Assert.assertTrue(docLib.isCreateContentPresent(ContentType.XML), "The XML component isn't present");
            Assert.assertTrue(docLib.isCreateFromTemplatePresent(true), "The 'Create from template' component isn't present");
            Assert.assertTrue(docLib.isCreateFromTemplatePresent(false), "The 'Create from template' component isn't present");

        }
        Assert.assertTrue(docLib.isCreateContentPresent(ContentType.GOOGLEDOCS), "The GOOGLE DOCS component isn't present");
        Assert.assertTrue(docLib.isCreateContentPresent(ContentType.GOOGLESPREADSHEET), "The GOOGLE SPREADSHEET component isn't present");
        Assert.assertTrue(docLib.isCreateContentPresent(ContentType.GOOGLEPRESENTATION), "The GOOGLE SPREADSHEET component isn't present");

        Assert.assertTrue(docLib.isCreateNewFolderPresent(), "The FOLDER component isn't present");
        docLib.selectCreateContentDropdown().render();

        Assert.assertTrue(docLib.isFileUploadEnabled(), "The UPLOAD component isn't present");

        // Selected items menu
        Assert.assertFalse(docLib.isSelectedItemEnabled(), "The 'Selected items' button is enabled");
        documentLibraryPage.getNavigation().selectDocuments().render();
        Assert.assertTrue(docLib.isSelectedItemEnabled(), "The 'Selected items' button isn't enabled");

        // Number of displaying items (1-10 of 10, for example)
        PaginationForm paginationForm = documentLibraryPage.getBottomPaginationForm();
        Assert.assertEquals(paginationForm.getPaginationInfo(), "1 - 1 of 1", "Value isn't displayed");

        Assert.assertEquals(paginationForm.getCurrentPageNumber(), 1, "Page number isn't displayed");
        Assert.assertFalse(paginationForm.isPreviousButtonEnable(), "'>>' isn't present");
        Assert.assertFalse(paginationForm.isNextButtonEnable(), "'<<'  isn't present");

        Assert.assertEquals(documentLibraryPage.getNavigation().getCurrentSortField(), SortField.NAME, "Items aren't sorted by NAME");

        // Options menu
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.SHOW_FOLDERS)
                ^ documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.HIDE_FOLDERS), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.SHOW_BREADCRUMB)
                ^ documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.HIDE_BREADCRUMB), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.FULL_WINDOW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.FULL_SCREEN), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.SIMPLE_VIEW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.DETAILED_VIEW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.GALLERY_VIEW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.TABLE_VIEW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.AUDIO_VIEW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.MEDIA_VIEW), "The component isn't present");

        if (!alfrescoVersion.isCloud())
        {
            Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.RSS_FEED), "The component isn't present");
        }

        Assert.assertTrue(documentLibraryPage.getNavigation().isSortAscending(), "Items aren't sorted ascending");

    }

    @Test(groups = { "AlfrescoOne", "NonGrid" })
    public void AONE_13996() throws Exception
    {
        // Create site
        prepare();

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = testName + System.currentTimeMillis();

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Open Create menu on the DocLib toolbar
        DocumentLibraryNavigation docLib = documentLibraryPage.getNavigation().render();

        if (!alfrescoVersion.isCloud())
        {
            CreatePlainTextContentPage contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
            contentPage.cancel().render();
            documentLibraryPage = drone.getCurrentPage().render();
            Assert.assertTrue(documentLibraryPage.getTitle().contains("Document Library"));

            // Choose HTML document in the Create menu
            // Click on Cancel button
            contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.HTML).render();
            contentPage.cancel().render();
            documentLibraryPage = drone.getCurrentPage().render();
            Assert.assertTrue(documentLibraryPage.getTitle().contains("Document Library"));

            // Choose HTML document in the Create menu
            // Click on Cancel button
            contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.XML).render();
            contentPage.cancel().render();
            documentLibraryPage = drone.getCurrentPage().render();
            Assert.assertTrue(documentLibraryPage.getTitle().contains("Document Library"));

        }
        documentLibraryPage = ShareUser.getSharePage(drone).render(maxWaitTime);

        GoogleDocsAuthorisation googleAuthorisationPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.GOOGLEDOCS).render();
        googleAuthorisationPage.render();

        EditInGoogleDocsPage googleDocsPage = ShareUserGoogleDocs.signInGoogleDocs(googleAuthorisationPage);
        GoogleDocsDiscardChanges googleDocsDiscardChanges = googleDocsPage.selectDiscard().render(maxWaitTime);
        googleDocsDiscardChanges.clickOkButton().render();
        documentLibraryPage = drone.getCurrentPage().render();

        googleAuthorisationPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.GOOGLESPREADSHEET).render();
        googleAuthorisationPage.render(maxWaitTime);

        webDriverWait(drone, 7000);

        googleDocsPage = drone.getCurrentPage().render(maxWaitTime);
        googleDocsDiscardChanges = googleDocsPage.selectDiscard().render(maxWaitTime);
        googleDocsDiscardChanges.clickOkButton().render();
        documentLibraryPage = drone.getCurrentPage().render(maxWaitTime);

        googleAuthorisationPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.GOOGLEPRESENTATION).render();
        googleAuthorisationPage.render(maxWaitTime);

        webDriverWait(drone, 7000);

        googleDocsPage = drone.getCurrentPage().render();
        googleDocsDiscardChanges = googleDocsPage.selectDiscard().render(maxWaitTime);
        googleDocsDiscardChanges.clickOkButton().render();
        documentLibraryPage = drone.getCurrentPage().render(maxWaitTime);

        NewFolderPage folderPage = documentLibraryPage.getNavigation().selectCreateNewFolder().render();
        folderPage.clickCancel().render();
        documentLibraryPage = drone.getCurrentPage().render(maxWaitTime);
        Assert.assertTrue(documentLibraryPage.getTitle().contains("Document Library"));
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_13997() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = testName + System.currentTimeMillis();
        String fileName = "test.txt";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Create site
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Upload .txt file
        String fileLocation = DATA_FOLDER + fileName;

        File file = newFile(fileLocation, fileName);
        UploadFilePage upLoadPage = documentLibraryPage.getNavigation().selectFileUpload().render();
        documentLibraryPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
        documentLibraryPage.setContentName(file.getName());

        Assert.assertTrue(documentLibraryPage.isFileVisible(fileName), "The document isn't uploaded");

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13998() throws Exception
    {
        // Create site
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String file = "test.txt";
        String folderName = "testFolder";

//        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

        // Upload .txt files.
        for (int i = 1; i <= 2; i++)
        {
            String[] fileInfo = { i + file };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }

    }

    @Test(groups = { "AlfrescoOne"})
    public void AONE_13998() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = "testFolder";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.SHOW_FOLDERS)
                ^ documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.HIDE_FOLDERS), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.SHOW_BREADCRUMB)
                ^ documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.HIDE_BREADCRUMB), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.FULL_WINDOW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.FULL_SCREEN), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.SIMPLE_VIEW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.DETAILED_VIEW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.GALLERY_VIEW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.TABLE_VIEW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.AUDIO_VIEW), "The component isn't present");
        Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.MEDIA_VIEW), "The component isn't present");

        if (!alfrescoVersion.isCloud())
        {
            Assert.assertTrue(documentLibraryPage.getNavigation().isOptionPresent(LibraryOption.RSS_FEED), "The component isn't present");
        }

        // The view is changed to simple view;
        documentLibraryPage = documentLibraryPage.getNavigation().selectSimpleView().render();
        Assert.assertEquals(documentLibraryPage.getViewType(), ViewType.SIMPLE_VIEW, "The view isn't changed");

        // The view is changed to gallery view;
        documentLibraryPage = documentLibraryPage.getNavigation().selectGalleryView().render();
        Assert.assertEquals(documentLibraryPage.getViewType(), ViewType.GALLERY_VIEW, "The view isn't changed");

        // The view is changed to table view;
        documentLibraryPage = documentLibraryPage.getNavigation().selectFilmstripView().render();
        Assert.assertEquals(documentLibraryPage.getViewType(), ViewType.FILMSTRIP_VIEW, "The view isn't changed");

        // The view is changed to table view;
        documentLibraryPage = documentLibraryPage.getNavigation().selectTableView().render();
        Assert.assertEquals(documentLibraryPage.getViewType(), ViewType.TABLE_VIEW, "The view isn't changed");

        // The view is changed to audio view;
        documentLibraryPage = documentLibraryPage.getNavigation().selectAudioView().render();
        Assert.assertEquals(documentLibraryPage.getViewType(), ViewType.AUDIO_VIEW, "The view isn't changed");

        // The view is changed to media view;
        documentLibraryPage = documentLibraryPage.getNavigation().selectMediaView().render();
        Assert.assertEquals(documentLibraryPage.getViewType(), ViewType.MEDIA_VIEW, "The view isn't changed");

        // The view is changed to detailed view;
        documentLibraryPage = documentLibraryPage.getNavigation().selectDetailedView().render();
        Assert.assertEquals(documentLibraryPage.getViewType(), ViewType.DETAILED_VIEW, "The view isn't changed");

        // Choose Hide Folders option
        documentLibraryPage = documentLibraryPage.getNavigation().selectHideFolders().render();
        Assert.assertFalse(documentLibraryPage.isFileVisible(folderName), "The folder isn't hidden");

        // Choose Hide Breadcrumb option
        documentLibraryPage = documentLibraryPage.getNavigation().selectHideBreadcrump().render();
        Assert.assertFalse(documentLibraryPage.getNavigation().isCrumbTrailVisible(), "Breadcrumbs aren't hidden");

        // Click Show Folders, Show Breadcrumb
        documentLibraryPage = documentLibraryPage.getNavigation().selectShowFolders().render();
        documentLibraryPage = documentLibraryPage.getNavigation().selectShowBreadcrump().render(maxWaitTime);
        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName), "The folder isn't shown");
        Assert.assertTrue(documentLibraryPage.getNavigation().isCrumbTrailVisible(), "Breadcrumbs aren't shown");

        // Choose RSS Feed
        RssFeedPage rssFeedPage = documentLibraryPage.getNavigation().selectRssFeed(testUser, DEFAULT_PASSWORD, siteName);
        Assert.assertTrue(rssFeedPage.isSubscribePanelDisplay(), "The RSS subscription process isn't initiated");


    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_13999() throws Exception
    {
        // Create site
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = "testFolder";

//        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
        ShareUser.createFolderInFolder(drone, folderName + 1, folderName + 1, folderName);

    }

    @Test(groups = { "AlfrescoOne"})
    public void AONE_13999() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);
        String folderName = "testFolder";
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Choose Hide Breadcrumb option
        documentLibraryPage = documentLibraryPage.getNavigation().selectHideBreadcrump().render();
        Assert.assertFalse(documentLibraryPage.getNavigation().isCrumbTrailVisible(), "Breadcrumbs aren't hidden");

        // Click Show Breadcrumb
        documentLibraryPage = documentLibraryPage.getNavigation().selectShowBreadcrump().render();
        Assert.assertTrue(documentLibraryPage.getNavigation().isCrumbTrailVisible(), "Breadcrumbs aren't shown");
        Assert.assertEquals(documentLibraryPage.getNavigation().getBreadCrumbsPath(), "Documents", "Breadcrumbs aren't shown");

        // Click on folder name link
        documentLibraryPage = documentLibraryPage.selectFolder(folderName).render(maxWaitTime);
        String actualPath = documentLibraryPage.getNavigation().getBreadCrumbsPath();
        String path = "Documents" + "\n>\n " + folderName;
        Assert.assertEquals(path, actualPath, "The Folder Path isn't changed");

        // Click on child folder name link
        documentLibraryPage = documentLibraryPage.selectFolder(folderName + 1).render(maxWaitTime);
        actualPath = documentLibraryPage.getNavigation().getBreadCrumbsPath();
        path = "Documents" + "\n>\n " + folderName + "\n>\n " + folderName + 1;
        Assert.assertEquals(path, actualPath, "The Folder Path isn't changed");

        // Click on "Documents" link in the Folder Path
        documentLibraryPage = documentLibraryPage.getNavigation().clickCrumbsParentLinkName().render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName), "DocLib Page isn't opened");
        Assert.assertEquals(documentLibraryPage.getNavigation().getBreadCrumbsPath(), "Documents", "DocLib Page isn't opened");

        // Click on Documents link again
        FolderDetailsPage detailsPage = documentLibraryPage.getNavigation().clickCrumbsElementDetailsLinkName().render();
        Assert.assertTrue(detailsPage.isDetailsPage("folder"), "Details page of the documentLibrary folder isn't opened");

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14000() throws Exception
    {
        // Create site
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = "testFolder";
        String file = "test.txt";

//        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        for (int i = 1; i<= 3; i++)
        {
            ShareUser.createFolderInFolder(drone, folderName + i, folderName + i, DOCLIB);
            String[] fileInfo = { i + file };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
    }

    @Test(groups = { "AlfrescoOne"})
    public void AONE_14000() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = "testFolder";
        String file = "test.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Check the "Select ->All" check-box on the DocLib toolbar
        documentLibraryPage.getNavigation().selectAll().render(maxWaitTime);
        for (int i = 1; i<=3; i++)
        {
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(i + file).isCheckboxSelected(), "Document " + i + file + " isn't checked");
        assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " isn't checked");
        }

        // Check the "Select->None" check-box an the DocLib toolbar
        documentLibraryPage.getNavigation().selectNone().render();
        for (int i = 1; i<=3; i++)
        {
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(i + file).isCheckboxSelected(), "Document " + i + file + " is checked");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " is checked");
        }

        // Check several files and folders but not all
        for (int i = 1; i<= 2; i++){
            ShareUser.selectContentCheckBox(drone, folderName + i);
            ShareUser.selectContentCheckBox(drone, i + file);

            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(i + file).isCheckboxSelected(), "Document " + i + file + " isn't checked");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " isn't checked");
        }

        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(3 + file).isCheckboxSelected(), "Document " + 3 + file + " is checked");
        Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName + 3).isCheckboxSelected(), "Folder " + folderName + 3 + " is checked");

        // Click drop-down "Select" menu and set "Invert Selection" action
        documentLibraryPage.getNavigation().selectInvert().render();
        for (int i = 1; i<=2; i++)
        {
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(i + file).isCheckboxSelected(), "Document " + i + file + " is checked");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " is checked");
        }

        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(3 + file).isCheckboxSelected(), "Document " + 3 + file + " isn't checked");
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName + 3).isCheckboxSelected(), "Folder " + folderName + 3 + " isn't checked");

        // Check the "Select" -> All check-box an the DocLib toolbar
        documentLibraryPage.getNavigation().selectAll().render();
        for (int i = 1; i<=3; i++)
        {
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(i + file).isCheckboxSelected(), "Document " + i + file + " isn't checked");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " isn't checked");
        }

        // Check the "Select" -> "None" check-box at the DocLib toolbar again
        documentLibraryPage.getNavigation().selectNone().render();
        for (int i = 1; i<=3; i++)
        {
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(i + file).isCheckboxSelected(), "Document " + i + file + " is checked");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " is checked");
        }

        // Click drop-down "Select" menu and set "Folders" action
        documentLibraryPage.getNavigation().selectFolders().render();
        for (int i = 1; i<=3; i++)
        {
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(i + file).isCheckboxSelected(), "Document " + i + file + " is checked");
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " isn't checked");
        }

        // Click drop-down "Select" menu and set "None" action
        documentLibraryPage.getNavigation().selectNone().render();
        for (int i = 1; i<=3; i++)
        {
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(i + file).isCheckboxSelected(), "Document " + i + file + " is checked");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " is checked");
        }

        // Click drop-down "Select" menu and set "Documents" action
        documentLibraryPage.getNavigation().selectDocuments().render();
        for (int i = 1; i<=3; i++)
        {
            Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(i + file).isCheckboxSelected(), "Document " + i + file + " isn't checked");
            Assert.assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName + i).isCheckboxSelected(), "Folder " + folderName + i + " is checked");
        }

        // Verify the "Selected items" drop-down on the DocLib toolbar when some items are checked.
        DocumentLibraryNavigation docLib = documentLibraryPage.getNavigation().render();
        Assert.assertTrue(docLib.isSelectedItemEnabled(), "The 'Selected items' button isn't enabled");

        // Verify the "Selected items" drop-down an the DocLib toolbar when all items are checked
        documentLibraryPage.getNavigation().selectAll().render();
        Assert.assertTrue(docLib.isSelectedItemEnabled(), "The 'Selected items' button isn't enabled");

        // Verify the "Selected items" drop-down on the DocLib toolbar when no items are checked.
        documentLibraryPage.getNavigation().selectNone().render();
        Assert.assertFalse(docLib.isSelectedItemEnabled(), "The 'Selected items' button is enabled");

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14001() throws Exception
    {
        // Create site
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = "testFolder";
        String file = "test.txt";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        for (int i = 1; i<= 2; i++)
        {
            ShareUser.createFolderInFolder(drone, folderName + i, folderName + i, DOCLIB);
            String[] fileInfo = { i + file };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14001() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = "testFolder";
        String file = "test.txt";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage =ShareUser.openDocumentLibrary(drone).render();

        // Check a file and a folder
        ShareUser.selectContentCheckBox(drone, folderName + 1);
        ShareUser.selectContentCheckBox(drone, 1 + file);

        // Click drop-down "Selected Items..." menu and set "Copy to..." action
        CopyOrMoveContentPage copyPage = documentLibraryPage.getNavigation().selectCopyTo().render();
        Assert.assertTrue(copyPage.isShareDialogueDisplayed(), "Copy to... dialogue isn't opened");

        // Click "Cancel" button
        documentLibraryPage = copyPage.clickCancel().render();

        // Click drop-down "Selected Items..." menu and set "Move to..." action
        CopyOrMoveContentPage movePage = documentLibraryPage.getNavigation().selectCopyTo().render();
        Assert.assertTrue(copyPage.isShareDialogueDisplayed(), "Move to... dialogue isn't opened");

        // Click "Cancel" button
        documentLibraryPage = copyPage.clickCancel().render();

        DocumentLibraryNavigation docLib = documentLibraryPage.getNavigation().render();
        Assert.assertFalse(docLib.isSelectedItemsOptionPresent(SelectedItemsOptions.START_WORKFLOW), "Start Workflow is present");

        // Select only documents
        documentLibraryPage.getNavigation().selectDocuments().render();

        // Click drop-down "Selected Items..." menu and set "Start Workflow" (for Ent) / Create Task (for Cloud) action
        StartWorkFlowPage startWorkFlowPage = documentLibraryPage.getNavigation().selectStartWorkFlow().render();
        if (!isAlfrescoVersionCloud(drone))
        {
            Assert.assertTrue(startWorkFlowPage.getTitle().contains("Start Workflow"), "Start Workflow page isn't opened");
        }
        else
        {
            Assert.assertTrue(startWorkFlowPage.getTitle().contains("Create Task"), "Create Task page isn't opened");
        }

        NewWorkflowPage newWorkflowPage = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();
        documentLibraryPage = newWorkflowPage.cancelWorkflow().render(maxWaitTime);
        Assert.assertTrue(documentLibraryPage.getTitle().contains("Document Library"), "Document Library isn't opened");

        // Select a few item and click drop-down "Selected Items..." menu and set "Delete" action
        ShareUser.selectContentCheckBox(drone, folderName + 2);
        ShareUser.selectContentCheckBox(drone, 2 + file);
        ConfirmDeletePage confirmDeletePage = documentLibraryPage.getNavigation().selectDelete().render();

        // Click "Cancel" button
        documentLibraryPage = confirmDeletePage.selectAction(ConfirmDeletePage.Action.Cancel).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(folderName + 2), "The folder is deleted");
        Assert.assertTrue(documentLibraryPage.isFileVisible(2 + file), "The document is deleted");

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14002() throws Exception
    {
        // Create site
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String file = "test.txt";
        String folderName = "testFolder";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);

        // Upload .txt files.
        for (int i = 1; i <= 2; i++)
        {
            String[] fileInfo = { i + file };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }

    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14002() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String file = "test.txt";
        String folderName = "testFolder";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        openSiteDashboard(drone, siteName);
        DocumentLibraryPage docLibPage = ShareUser.openDocumentLibrary(drone).render();


        // The view is changed to simple view;
        docLibPage = docLibPage.getNavigation().selectSimpleView().render();
        Assert.assertEquals(docLibPage.getViewType(), ViewType.SIMPLE_VIEW, "The view isn't changed");

        // The view is changed to detailed view;
        docLibPage = docLibPage.getNavigation().selectDetailedView().render();
        Assert.assertEquals(docLibPage.getViewType(), ViewType.DETAILED_VIEW, "The view isn't changed");

        // The view is changed to gallery view;
        docLibPage = docLibPage.getNavigation().selectGalleryView().render();
        Assert.assertEquals(docLibPage.getViewType(), ViewType.GALLERY_VIEW, "The view isn't changed");

        DocumentLibraryNavigation navigation = docLibPage.getNavigation();
        docLibPage = ((DocumentLibraryPage) navigation.selectGalleryView()).render();

        Assert.assertTrue(navigation.isZoomControlVisible(), "The slider isn't present");

        docLibPage = navigation.selectZoom(ZoomStyle.SMALLER).render();

        ZoomStyle zoomStyles[] = { ZoomStyle.BIGGER, ZoomStyle.BIGGEST, ZoomStyle.SMALLER, ZoomStyle.SMALLEST };

        for (ZoomStyle zoomStyle : zoomStyles)
        {
            FileDirectoryInfo thisRow = docLibPage.getFileDirectoryInfo(1 + file);
            FileDirectoryInfo thisfolderRow = docLibPage.getFileDirectoryInfo(folderName);

            double fileHeightSize = thisRow.getFileOrFolderHeight();
            double folderHeightSize = thisfolderRow.getFileOrFolderHeight();

            navigation.selectZoom(zoomStyle).render();
            ZoomStyle actualZoomStyle = navigation.getZoomStyle();
            Assert.assertEquals(actualZoomStyle,zoomStyle, "The size isn't changed");

            double actualFileHeight = docLibPage.getFileDirectoryInfo(1 + file).getFileOrFolderHeight();
            double actualFolderHeight = docLibPage.getFileDirectoryInfo(folderName).getFileOrFolderHeight();

            if (zoomStyle.equals(zoomStyles[0]) || zoomStyle.equals(zoomStyles[1]))
            {
                Assert.assertTrue(actualFileHeight > fileHeightSize, "The size isn't changed");
                Assert.assertTrue(actualFolderHeight > folderHeightSize, "The size isn't changed");
            }
            else
            {
                Assert.assertTrue(actualFileHeight < fileHeightSize, "The size isn't changed");
                Assert.assertTrue(actualFolderHeight < folderHeightSize, "The size isn't changed");
            }

        }
        // The view is changed to table view;
        docLibPage = docLibPage.getNavigation().selectFilmstripView().render();
        Assert.assertEquals(docLibPage.getViewType(), ViewType.FILMSTRIP_VIEW, "The view isn't changed");

        // The view is changed to table view;
        docLibPage = docLibPage.getNavigation().selectTableView().render();
        Assert.assertEquals(docLibPage.getViewType(), ViewType.TABLE_VIEW, "The view isn't changed");

        // The view is changed to audio view;
        docLibPage = docLibPage.getNavigation().selectAudioView().render();
        Assert.assertEquals(docLibPage.getViewType(), ViewType.AUDIO_VIEW, "The view isn't changed");

        // The view is changed to media view;
        docLibPage = docLibPage.getNavigation().selectMediaView().render();
        Assert.assertEquals(docLibPage.getViewType(), ViewType.MEDIA_VIEW, "The view isn't changed");

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14003() throws Exception
    {
        // Create site
        String testUser = getUserNameFreeDomain(testUser1);
        String siteName = getSiteName(testUser1);
        String file = "test.txt";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload .txt files.
        for (int i = 1; i <= 200; i++)
        {
            String[] fileInfo = { file + i };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }

    }

    @Test(groups = { "AlfrescoOne"})
    public void AONE_14003() throws Exception
    {
        String testUser = getUserNameFreeDomain(testUser1);
        String siteName = getSiteName(testUser1);


        // Verify the Number of displayed items value
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        PaginationForm paginationForm = documentLibraryPage.getBottomPaginationForm();
        Assert.assertEquals(paginationForm.getPaginationInfo(), "1 - 50 of 200", "Value isn't displayed");

    }

    @Test(groups = { "AlfrescoOne"})
    public void AONE_14004() throws Exception
    {
        String testUser = getUserNameFreeDomain(testUser1);
        String siteName = getSiteName(testUser1);


        // Verify the Page number value
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        PaginationForm paginationForm = documentLibraryPage.getBottomPaginationForm();
        Assert.assertEquals(paginationForm.getCurrentPageNumber(), 1, "Current page number isn't displayed");
        List<WebElement> pagesLinks = paginationForm.getPaginationLinks();
        Assert.assertEquals(pagesLinks.size(), 4, "Pages' links aren't present");

        paginationForm.clickNext().render();
        Assert.assertEquals(paginationForm.getCurrentPageNumber(), 2, "Page number isn't displayed");
        Assert.assertEquals(paginationForm.getPaginationInfo(), "51 - 100 of 200", "Value isn't displayed");

        webDriverWait(drone,5000);
        paginationForm.clickPrevious().render(maxWaitTime);
        webDriverWait(drone,5000);
        Assert.assertEquals(paginationForm.getCurrentPageNumber(), 1, "Current page number isn't displayed");
        Assert.assertEquals(paginationForm.getPaginationInfo(), "1 - 50 of 200", "Value isn't displayed");

    }

    @Test(groups = { "AlfrescoOne" })
    public void AONE_14005() throws Exception
    {

        String testUser = getUserNameFreeDomain(testUser1);
        String siteName = getSiteName(testUser1);

        // Verify the Page number value
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        Assert.assertEquals(documentLibraryPage.getFiles().size(), 50, "Wrong number of items displayed by default");
    }

}
