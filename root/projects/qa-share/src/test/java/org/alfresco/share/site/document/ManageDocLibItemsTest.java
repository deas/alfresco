package org.alfresco.share.site.document;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.CategoryManagerPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.alfresco.po.share.enums.UserRole.*;
import static org.alfresco.po.share.site.document.Categories.*;
import static org.alfresco.po.share.site.document.TreeMenuNavigation.DocumentsMenu.*;
import static org.alfresco.share.util.RandomUtil.getInt;
import static org.testng.Assert.*;
import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class ManageDocLibItemsTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(ManageDocLibItemsTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Start Tests in: " + testName);

    }

    @Test(groups = "DataPrepDocumentLibrary")
    public void dataPrep_Enterprise40x_8485() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        // User
        String testUser = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);
    }

    /**
     * Upload items. With special characters.
     *
     * @throws Exception
     */
    // TODO: Use appropriate groups: Enterprise42, refer to qa site wiki on ts, is the test valid for Ent 43, Cloud?
    @Test(groups = "Enterprise4.2")
    public void Enterprise40x_8485() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String fileWithSpecialCharacter = "désirBedürfnisèil あなたの名前は何ですか¿Cuál.txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Uploading file with special character.
        String[] fileInfo = { fileWithSpecialCharacter };
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        assertTrue(documentLibraryPage.isItemVisble(fileWithSpecialCharacter), "File with special characters didn't uploaded");
    }


    @Test(groups = "DataPrepDocumentLibrary")
    public void dataPrep_Enterprise40x_8731() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        // User
        String testUser = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);
    }

    /**
     * Navigation to folders with '%<number>' in the name
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2")
    public void Enterprise40x_8731() throws Exception
    {
        //Array with folderNames.
        final String[] folderNames = { "my%folderha", "my%folder", "my%folder%25156722", "my%folder%156722again", "xyz%123" };

        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Create set of folders with %.
        for (String folderName : folderNames)
        {
            ShareUserSitePage.createFolder(drone, folderName, folderName);
            documentLibraryPage.getFileDirectoryInfo(folderName);
        }

        //Upload testFile
        String fileName = getFileName(siteName + "_testFile");
        String[] fileInfo = { fileName, folderNames[0] };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        //Copy testFile to folders
        for (int i = 1; i < folderNames.length; i++)
        {
            ShareUserSitePage.copyOrMoveToFolder(drone, siteName, fileName, new String[] { folderNames[i] }, true);
        }
        documentLibraryPage.getNavigation().clickFolderUp();

        //Check all another folders
        for (int i = 1; i < folderNames.length; i++)
        {
            documentLibraryPage.selectFolder(folderNames[i]);
            assertTrue(documentLibraryPage.isFileVisible(fileName), String.format("File didn't copy to folder or folder broken.(%s) ACE-932!!!", folderNames[i]));
            documentLibraryPage.getNavigation().clickFolderUp();
        }
    }

    @Test(groups = "DataPrepDocumentLibrary")
    public void dataPrep_Enterprise40x_8561() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        // User
        String testUser = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);
    }

    /**
     * Extracting metadata for .eml messages
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2")
    public void Enterprise40x_8561() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String testFile = "test-eml.eml";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Uploading .csv file.
        String[] fileInfo = { testFile };
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        assertTrue(documentLibraryPage.isFileVisible(testFile), String.format("File %s didn't uploaded", testFile));

        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(testFile);

        Map<String, Object> properties = documentDetailsPage.getProperties();
        assertEquals(properties.get("Addressee"), "Alle Mitglieder von GMX <members@gmx.net>", "Metadata didn't extract.");
        assertEquals(properties.get("Mimetype"), "EMail", "Metadata didn't extract.");
        assertEquals(properties.get("Subject"), "75 Fotoabzüge auf Premium-Fotopapier gratis! Drucken Sie jetzt Ihre Lieblingsfotos einfach und schnell bei Snapfish!", "Metadata didn't extract.");
        assertEquals(properties.get("SentDate"), "Mon 9 Apr 2012 12:05:21", "Metadata didn't extract.");
    }


    @Test(groups = "DataPrepDocumentLibrary")
    public void dataPrep_Enterprise40x_4048() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        // User
        String testUser = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);
    }

    /**
     * Thumbnailing of *.eps files
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2")
    public void Enterprise40x_4048() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String testFile = "test-eps.eps";
        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        // Uploading .eps file.
        String[] fileInfo = { testFile };
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        assertTrue(documentLibraryPage.isFileVisible(testFile), String.format("File %s didn't upload", testFile));

        FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(testFile);
        String urlThumbnailAfterCreating = fileDirectoryInfo.getPreViewUrl();

        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(testFile);
        Map<String, Object> properties = documentDetailsPage.getProperties();
        assertEquals(properties.get("Mimetype"), "EPS Type PostScript", "Wrong MimeType.");
        assertTrue(documentDetailsPage.isFlashPreviewDisplayed(), "Preview for eps file didn't display.");

        documentLibraryPage = documentDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(testFile);
        String urlThumbnail = fileDirectoryInfo.getPreViewUrl();
        assertNotEquals(urlThumbnail, urlThumbnailAfterCreating, "Thumbnail image didn't generate");

    }

    @Test(groups = "DataPrepDocumentLibrary")
    public void dataPrep_Enterprise40x_9125() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        // User
        String testUser = getUserNameFreeDomain(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        for (int i = 0; i < 60; i++)
        {
            String[] fileInfo = { testName + i };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
        ShareUser.logout(drone);
    }

    /**
     * Pagination after editing
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2")
    public void Enterprise40x_9125() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String testFile = testName + 0;
        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        PaginationForm paginationForm = documentLibraryPage.getBottomPaginationForm();
        assertFalse(paginationForm.isPreviousButtonEnable(), "Can move to previous page.");
        assertTrue(paginationForm.isNextButtonEnable(), "Can't move next page.");
        assertEquals(paginationForm.getPaginationInfo(), "1 - 50 of 60", "Wrong info about pagination items.");
        assertEquals(paginationForm.getPaginationLinks().size(), 2, "Wrong pages links count.");
        assertEquals(documentLibraryPage.getFiles().size(), 50, "50 items didn't displayed.");

        FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(testFile);
        documentLibraryPage = fileDirectoryInfo.selectEditOffline();

        documentLibraryPage = documentLibraryPage.clickOnRecentlyAdded();
        paginationForm = documentLibraryPage.getBottomPaginationForm();
        assertFalse(paginationForm.isPreviousButtonEnable(), "Can move to previous page.");
        assertFalse(paginationForm.isNextButtonEnable(), "Can move next page.");
        assertEquals(paginationForm.getPaginationLinks().size(), 1, "Not only 1 page.");
        assertEquals(documentLibraryPage.getFiles().size(), 50, "50 items didn't displayed.");

        documentLibraryPage = documentLibraryPage.clickOnRecentlyModified();
        paginationForm = documentLibraryPage.getBottomPaginationForm();
        assertFalse(paginationForm.isPreviousButtonEnable(), "Can move to previous page.");
        assertFalse(paginationForm.isNextButtonEnable(), "Can't move next page.");
        assertEquals(paginationForm.getPaginationLinks().size(), 1, "Not only 1 page.");
        assertEquals(documentLibraryPage.getFiles().size(), 50, "50 items didn't displayed.");

        documentLibraryPage = documentLibraryPage.clickOnAllDocuments();
        paginationForm = documentLibraryPage.getBottomPaginationForm();
        assertFalse(paginationForm.isPreviousButtonEnable(), "Can move to previous page.");
        assertTrue(paginationForm.isNextButtonEnable(), "Can't move next page.");
        assertEquals(paginationForm.getPaginationInfo(), "1 - 50 of 60", "Wrong info about pagination items.");
        assertEquals(paginationForm.getPaginationLinks().size(), 2, "Wrong pages links count.");
        assertEquals(documentLibraryPage.getFiles().size(), 50, "50 items didn't displayed.");

        paginationForm.clickNext();
        assertTrue(paginationForm.isPreviousButtonEnable(), "Can't move to previous page.");
        assertFalse(paginationForm.isNextButtonEnable(), "Can move next page.");
        assertEquals(paginationForm.getPaginationInfo(), "51 - 60 of 60", "Wrong info about pagination items.");
        assertEquals(paginationForm.getPaginationLinks().size(), 2, "Wrong pages links count.");
        assertEquals(documentLibraryPage.getFiles().size(), 10, "10 items didn't displayed.");

        paginationForm.clickPrevious();
        assertFalse(paginationForm.isPreviousButtonEnable(), "Can move to previous page.");
        assertTrue(paginationForm.isNextButtonEnable(), "Can't move next page.");
        assertEquals(paginationForm.getPaginationInfo(), "1 - 50 of 60", "Wrong info about pagination items.");
        assertEquals(paginationForm.getPaginationLinks().size(), 2, "Wrong pages links count.");
        assertEquals(documentLibraryPage.getFiles().size(), 50, "50 items didn't displayed.");
    }

    @Test(groups = "DataPrepDocumentLibrary")
    public void dataPrep_Enterprise40x_13857() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);

        // User
        String testUser1 = getUserNameFreeDomain(testName);
        String testUser2 = testUser1 + 2;

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        // Login
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, COLLABORATOR);
        ShareUser.logout(drone);
    }

    /**
     * Modifier of folder was deleted
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2")
    public void Enterprise40x_13857() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser1 = getUserNameFreeDomain(testName);
        String testUser2 = testUser1 + 2;
        String folderName1 = getFolderName(testName);
        String folderName2 = folderName1 + 2;
        /** Login */
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName2, folderName2);
        FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName1);
        EditDocumentPropertiesPage editDocumentPropertiesPage = fileDirectoryInfo.selectEditProperties();
        editDocumentPropertiesPage.setDescription(testUser2);
        editDocumentPropertiesPage.clickSave();
        ShareUser.logout(drone);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.deleteUser(drone, testUser2);
        ShareUser.logout(drone);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName1);
        assertEquals(fileDirectoryInfo.getModifier(), String.format("'%s' (Deleted User)", testUser2), String.format("Wrong information about modifier.(%s)", folderName1));

        fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName2);
        assertEquals(fileDirectoryInfo.getCreator(), String.format("'%s' (Deleted User)", testUser2), String.format("Wrong information about creator.(%s)", folderName2));
    }

    @Test(groups = "DataPrepDocumentLibrary")
    public void dataPrep_Enterprise40x_8495() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);

        // User
        String testUser = getUserNameFreeDomain(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.navigateToFolder(drone, folderName);
        for (String subFolder : SUB_FOLDERS)
        {
            ShareUserSitePage.createFolder(drone, subFolder, subFolder);
        }
        for (String uploadFile : UPLOAD_FILES)
        {
            String[] fileInfo = { uploadFile, folderName };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
        ShareUser.logout(drone);
    }

    private final String[] SUB_FOLDERS = { "a", "Z", "1potatoe", "Apotatoe" };
    private final String[] UPLOAD_FILES = { "1-10", "a-z", "A-Z" };
    private final String[] EXPECTED_ORDER_OF_ITEMS = { "1potatoe", "a", "Apotatoe", "Z", "1-10", "a-z", "A-Z-1" };

    /**
     * Sort order among doclib items
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2")
    public void Enterprise40x_8495() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        /** Login */
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUserSitePage.navigateToFolder(drone, folderName);
        List<FileDirectoryInfo> fileDirectoryInfoList = documentLibraryPage.getFiles();
        for (int i = 0; i < EXPECTED_ORDER_OF_ITEMS.length; i++)
        {
            assertEquals(fileDirectoryInfoList.get(i).getName(), EXPECTED_ORDER_OF_ITEMS[i], "Wrong items order.");
        }
    }

    @Test(groups = { "DataPrepDocumentLibrary", "EnterpriseOnly" })
    public void dataPrep_Enterprise40x_8658() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(CLASSIFIABLE);

        // User
        String testUser = getUserNameFreeDomain(testName);
        SharePage page = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        CategoryManagerPage categoryManagerPage = page.getNav().getCategoryManagerPage();
        categoryManagerPage.addNewCategory(CATEGORY_ROOT.getValue(), CATEGORY_TEST_1.getValue());
        categoryManagerPage.addNewCategory(CATEGORY_ROOT.getValue(), CATEGORY_TEST_2.getValue());
        Thread.sleep(10000); //wait solr
        drone.refresh();
        categoryManagerPage.addNewCategory(CATEGORY_TEST_1.getValue(), SUB_CATEGORY_TEST.getValue());
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        for (int i = 0; i < 3; i++)
        {
            String tempFolderName = folderName + i;
            String tempFileName = fileName + i;
            ShareUserSitePage.createFolder(drone, tempFolderName, tempFolderName);
            FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, tempFolderName);
            SelectAspectsPage selectAspectsPage = folderDetailsPage.selectManageAspects();
            selectAspectsPage.add(aspects);
            folderDetailsPage = selectAspectsPage.clickApplyChanges().render();
            folderDetailsPage.getSiteNav().selectSiteDocumentLibrary();
            String[] fileInfo = { tempFileName };
            DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
            DocumentDetailsPage documentDetailPage = documentLibraryPage.selectFile(tempFileName);
            documentDetailPage.selectManageAspects();
            selectAspectsPage.add(aspects);
            documentDetailPage = selectAspectsPage.clickApplyChanges().render();
            documentDetailPage.getSiteNav().selectSiteDocumentLibrary();
        }
    }

    /**
     * Browse by Categories
     *
     * @throws Exception
     */
    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void Enterprise40x_8658() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        List<Categories> category1 = new ArrayList<Categories>();
        category1.add(CATEGORY_TEST_1);
        List<Categories> category2andCategory1 = new ArrayList<Categories>();
        category2andCategory1.add(CATEGORY_TEST_2);
        category2andCategory1.add(CATEGORY_TEST_1);
        List<Categories> subCategory = new ArrayList<Categories>();
        subCategory.add(SUB_CATEGORY_TEST);

        /** Login */
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        /** CATEGORY_TEST_1 */
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName + 0);
        EditDocumentPropertiesPage editDocumentProperties = documentDetailsPage.selectEditProperties().render();
        CategoryPage categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.add(category1).render();
        categoryPage.clickOk();
        editDocumentProperties.clickSave();

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName + 0);
        editDocumentProperties = folderDetailsPage.selectEditProperties().render();
        categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.add(category1).render();
        categoryPage.clickOk();
        editDocumentProperties.clickSave();

        /** SUB_CATEGORY_TEST */
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName + 1);
        editDocumentProperties = documentDetailsPage.selectEditProperties().render();
        categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.openSubCategories(CATEGORY_TEST_1);
        categoryPage.add(subCategory).render();
        categoryPage.clickOk();
        editDocumentProperties.clickSave();

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName + 1);
        editDocumentProperties = folderDetailsPage.selectEditProperties().render();
        categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.openSubCategories(CATEGORY_TEST_1);
        categoryPage.add(subCategory).render();
        categoryPage.clickOk();
        editDocumentProperties.clickSave();

        /** CATEGORY_TEST_2 and CATEGORY_TEST_1 */
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName + 2);
        editDocumentProperties = documentDetailsPage.selectEditProperties().render();
        categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.add(category2andCategory1).render();
        categoryPage.clickOk();
        editDocumentProperties.clickSave();

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName + 2);
        editDocumentProperties = folderDetailsPage.selectEditProperties().render();
        categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.add(category2andCategory1).render();
        categoryPage.clickOk();
        editDocumentProperties.clickSave();

        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        documentLibraryPage.clickOnCategory(CATEGORY_ROOT);
        documentLibraryPage.clickOnCategory(CATEGORY_TEST_1);
        assertEquals(documentLibraryPage.getFiles().size(), 4, String.format("Wrong items count in category [%s]", CATEGORY_TEST_1.getValue()));
        assertTrue(documentLibraryPage.isItemVisble(fileName + 0), String.format("Item [%s] didn't displayed for category [%s]", fileName + 0, CATEGORY_TEST_1.getValue()));
        assertTrue(documentLibraryPage.isItemVisble(folderName + 0), String.format("Item [%s] didn't displayed for category [%s]", folderName + 0, CATEGORY_TEST_1.getValue()));
        documentLibraryPage.clickOnCategory(SUB_CATEGORY_TEST);
        assertEquals(documentLibraryPage.getFiles().size(), 2, String.format("Wrong items count in category [%s]", SUB_CATEGORY_TEST.getValue()));
        assertTrue(documentLibraryPage.isItemVisble(fileName + 1), String.format("Item [%s] didn't displayed for category [%s]", fileName + 1, SUB_CATEGORY_TEST.getValue()));
        assertTrue(documentLibraryPage.isItemVisble(folderName + 1), String.format("Item [%s] didn't displayed for category [%s]", folderName + 1, SUB_CATEGORY_TEST.getValue()));
        documentLibraryPage = documentLibraryPage.getNavigation().clickFolderUp().render();
        assertEquals(documentLibraryPage.getFiles().size(), 4, String.format("Wrong items count in category [%s]", CATEGORY_TEST_1.getValue()));
        assertTrue(documentLibraryPage.isItemVisble(fileName + 0), String.format("Item [%s] didn't displayed for category [%s]", fileName + 0, CATEGORY_TEST_1.getValue()));
        assertTrue(documentLibraryPage.isItemVisble(folderName + 0), String.format("Item [%s] didn't displayed for category [%s]", folderName + 0, CATEGORY_TEST_1.getValue()));
        documentLibraryPage.clickOnCategory(CATEGORY_TEST_2);
        assertEquals(documentLibraryPage.getFiles().size(), 2, String.format("Wrong items count in category [%s]", CATEGORY_TEST_2.getValue()));
        assertTrue(documentLibraryPage.isItemVisble(fileName + 2), String.format("Item [%s] didn't displayed for category [%s]", fileName + 0, CATEGORY_TEST_2.getValue()));
        assertTrue(documentLibraryPage.isItemVisble(folderName + 2), String.format("Item [%s] didn't displayed for category [%s]", folderName + 0, CATEGORY_TEST_2.getValue()));
    }

    /**
     * Share SOLR connections
     *
     * @throws Exception
     */
    @Test(groups = { "Enterprise4.2", "EnterpriseOnly" })
    public void Enterprise40x_8578() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        ShareUserRepositoryPage.createFolderInRepository(drone, folderName, folderName);
        for (int i = 0; i < 15; i++)
        {
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName + i);
            contentDetails.setContent(fileName + i);
            contentDetails.setDescription(fileName + i);
            ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, folderName);
            ShareUserRepositoryPage.openRepository(drone);
        }

        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderName);
        FileDirectoryInfo fileDirectoryInfo = null;
        for (int i = 0; i < 5; i++)
        {
            fileDirectoryInfo = repositoryPage.getFileDirectoryInfo(fileName + getInt(14));
            fileDirectoryInfo.selectFavourite();
            fileDirectoryInfo.selectFavourite();
        }
        fileDirectoryInfo.selectFavourite();
        TreeMenuNavigation treeMenuNavigation = repositoryPage.getLeftMenus();
        treeMenuNavigation.selectDocumentNode(MY_FAVORITES);
        assertEquals(repositoryPage.getFiles().size(), 1, "Wrong my favourites file count displayed.");
        treeMenuNavigation.selectDocumentNode(IM_EDITING);
        assertEquals(repositoryPage.getFiles().size(), 0, "Wrong files count displayed in I'm editing view.");
    }


    @AfterMethod(alwaysRun = true)
    public void logout()
    {
        ShareUser.logout(drone);
        logger.info("User logged out - drone.");
    }

}
