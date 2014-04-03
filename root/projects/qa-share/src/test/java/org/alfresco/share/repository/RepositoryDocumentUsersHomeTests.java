package org.alfresco.share.repository;

import static org.alfresco.po.share.site.document.Categories.LANGUAGES;
import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.site.document.Categories;
import org.alfresco.po.share.site.document.CategoryPage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.po.share.site.document.TagPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class RepositoryDocumentUsersHomeTests extends AbstractUtils
{

    private static final Logger logger = Logger.getLogger(RepositoryDocumentUsersHomeTests.class);

    private String testUser;
    // TODO: Define this generic string in language property files
    private static final String USER_HOMES_FOLDER = "User Homes";

    /**
     * A single user for the class is created and assigned admin rights
     */

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        // create a single user
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUser);
    }

    /**
     * User logs in before test is executed
     * 
     * @throws Exception
     */

    @BeforeMethod(groups = { "RepositoryDocumentUsersHome" })
    public void prepare() throws Exception
    {
        // login as created user
        try
        {
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            logger.info("Repository user logged in - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * User logs out before test is executed
     * 
     * @throws Exception
     */

    @AfterMethod(groups = { "RepositoryDocumentUsersHome" })
    public void quit() throws Exception
    {
        // login as created user
        try
        {
            ShareUser.logout(drone);
            logger.info("Repository user logged out - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }

    }

    /**
     * Manage aspects - Classifiable
     * 1) Upload file to test folder
     * 2) Click on Manage Aspects
     * 3) Select Classifiable
     * 4) Open document details page and verify category is present in document's metadata
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5461() throws Exception
    {
        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // click Manage Aspects
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();

        // Select Classifiable
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(CLASSIFIABLE);
        aspectsPage.add(aspects).render();
        detailsPage = aspectsPage.clickApplyChanges().render();

        // Open document details page and verify category is present in document's metadata
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertTrue("(None)".equalsIgnoreCase((String) properties.get("Categories")));
    }

    /**
     * Add Category
     * 1) Upload file to test folder
     * 2) Click on Manage Aspects
     * 3) Select Classifiable
     * 4) Add a category
     * 5) Open document details page and verify category is present in document's metadata
     * 
     * @throws Exception
     */

    @SuppressWarnings("unchecked")
    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5462() throws Exception
    {

        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // click on Manage Aspects
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();

        // Select Classifiable
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(CLASSIFIABLE);
        aspectsPage.add(aspects).render();
        detailsPage = aspectsPage.clickApplyChanges().render();

        // add a category
        List<Categories> categories = new ArrayList<Categories>();
        categories.add(LANGUAGES);
        EditDocumentPropertiesPage editDocumentProperties = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.add(categories).render();
        editDocumentProperties = categoryPage.clickOk().render();
        detailsPage = editDocumentProperties.selectSave().render();

        // Open document details page and verify category is present in document's metadata
        Map<String, Object> properties = detailsPage.getProperties();
        List<Categories> addedCategories = (List<Categories>) properties.get("Categories");
        Assert.assertTrue(addedCategories.contains(Categories.LANGUAGES));

    }

    /**
     * Add Category - Cancel
     * 1) Upload file to test folder
     * 2) Click on Manage Aspects
     * 3) Select Classifiable
     * 4) Cancel adding of category
     * 5) Check the category is not added
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5463() throws Exception
    {
        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // click on Manage Aspects
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();

        // select Classifiable
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(CLASSIFIABLE);
        aspectsPage.add(aspects).render();
        detailsPage = aspectsPage.clickApplyChanges().render();

        // cancel adding of category
        List<Categories> categories = new ArrayList<Categories>();
        categories.add(LANGUAGES);
        EditDocumentPropertiesPage editDocumentProperties = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.add(categories).render();
        editDocumentProperties = categoryPage.clickOk().render();
        editDocumentProperties.clickOnCancel();

        // check the category is not added
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertTrue("(None)".equalsIgnoreCase((String) properties.get("Categories")));
    }

    /**
     * Full Metadata Edit Page
     * 1) Upload file to test folder
     * 2) Edit file properties
     * 3) Add aspects and category
     * 4) Confirm properties are changed
     * 
     * @throws Exception
     */

    @SuppressWarnings("unchecked")
    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5464() throws Exception
    {
        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // edit properties
        String newFileName = testName + System.currentTimeMillis();
        String newTitle = testName + "new title" + System.currentTimeMillis();
        String newDescription = testName + "new description" + System.currentTimeMillis();
        String mimeType = "XHTML";
        String newAuthor = "new author";
        String newTag = "new tag";

        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        EditDocumentPropertiesPage editDocumentProperties = detailsPage.selectEditProperties().render();
        editDocumentProperties.setName(newFileName);
        editDocumentProperties.setDocumentTitle(newTitle);
        editDocumentProperties.setDescription(newDescription);
        editDocumentProperties.selectMimeType(mimeType);
        editDocumentProperties.setAuthor(newAuthor);
        TagPage tagPage = editDocumentProperties.getTag().render();
        tagPage = tagPage.enterTagValue(newTag).render();
        editDocumentProperties = tagPage.clickOkButton().render();
        detailsPage = editDocumentProperties.selectSave().render();

        // add aspects and category
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(CLASSIFIABLE);
        List<Categories> categories = new ArrayList<Categories>();
        categories.add(LANGUAGES);
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        aspectsPage.add(aspects).render();
        detailsPage = aspectsPage.clickApplyChanges().render();
        editDocumentProperties = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.add(categories).render();
        editDocumentProperties = categoryPage.clickOk().render();
        detailsPage = editDocumentProperties.selectSave().render();

        // confirm properties are changed
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertEquals(properties.get("Name"), newFileName);
        Assert.assertEquals(properties.get("Title"), newTitle);
        Assert.assertEquals(properties.get("Description"), newDescription);
        Assert.assertEquals(properties.get("Mimetype"), mimeType);
        Assert.assertEquals(properties.get("Author"), newAuthor);
        List<Categories> addedCategories = (List<Categories>) properties.get("Categories");
        Assert.assertTrue(addedCategories.contains(Categories.LANGUAGES));
        repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        String[] testFolderPath = { USER_HOMES_FOLDER, testUser, testName };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);
        List<String> tags = repositoryPage.getFileDirectoryInfo(newFileName).getTags();
        Assert.assertEquals(tags.get(0), newTag);
    }

    /**
     * Full Metadata Edit Page - Cancel
     * 1) Upload file to test folder
     * 2) Edit file properties - cancel
     * 3) Check properties have not changed
     * 3) Add aspects and category
     * 4) Check aspects and category have not changed
     * 
     * @throws Exception
     */

    @SuppressWarnings("unchecked")
    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5465() throws Exception
    {
        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // edit properties
        String newFileName = testName + System.currentTimeMillis();
        String newTitle = testName + "new title" + System.currentTimeMillis();
        String newDescription = testName + "new description" + System.currentTimeMillis();
        String mimeType = "XHTML";
        String newAuthor = "new author";
        String newTag = "new tag";

        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        EditDocumentPropertiesPage editDocumentProperties = detailsPage.selectEditProperties().render();

        editDocumentProperties.setName(newFileName);
        editDocumentProperties.setDocumentTitle(newTitle);
        editDocumentProperties.setDescription(newDescription);
        editDocumentProperties.selectMimeType(mimeType);
        editDocumentProperties.setAuthor(newAuthor);
        TagPage tagPage = editDocumentProperties.getTag().render();
        tagPage = tagPage.enterTagValue(newTag).render();
        editDocumentProperties = tagPage.clickOkButton().render();
        editDocumentProperties.clickOnCancel();

        // check properties are not changed
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertFalse(newFileName.equalsIgnoreCase((String) properties.get(newFileName)));
        Assert.assertFalse("Title".equalsIgnoreCase((String) properties.get(newTitle)));
        Assert.assertFalse("Description".equalsIgnoreCase((String) properties.get(newDescription)));
        Assert.assertFalse("Mimetype".equalsIgnoreCase((String) properties.get(mimeType)));
        Assert.assertFalse("Author".equalsIgnoreCase((String) properties.get(newAuthor)));
        List<Categories> addedCategories = (List<Categories>) properties.get("Categories");
        Assert.assertNull(addedCategories);
        repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        String[] testFolderPath = { USER_HOMES_FOLDER, testUser, testName };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);
        Assert.assertFalse(repositoryPage.getFileDirectoryInfo(sampleFile.getName()).hasTags());

        // add aspects and category
        detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(CLASSIFIABLE);
        List<Categories> categories = new ArrayList<Categories>();
        categories.add(LANGUAGES);
        aspectsPage.add(aspects).render();
        detailsPage = aspectsPage.clickApplyChanges().render();
        editDocumentProperties = detailsPage.selectEditProperties().render();
        CategoryPage categoryPage = editDocumentProperties.getCategory().render();
        categoryPage.add(categories).render();
        editDocumentProperties = categoryPage.clickOk().render();
        detailsPage = editDocumentProperties.selectCancel().render();

        // check categories are not changed
        properties = detailsPage.getProperties();
        Assert.assertTrue("(None)".equalsIgnoreCase((String) properties.get("Categories")));
    }

    /**
     * Add a comment
     * 1) Upload file to test folder
     * 2) Add a comment
     * 3) Check the comment is added
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5466() throws Exception
    {
        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        Assert.assertTrue(detailsPage.getCommentCount() == 0);

        // add a comment
        detailsPage.addComment("comment 1");
        detailsPage.isCommentCountPresent(5000);

        // check the comment is added
        Assert.assertTrue(detailsPage.getCommentCount() == 1);

    }

    /**
     * Copy to My User home
     * 1) Upload file to repository
     * 2) Copy the file from repository to users home folder
     * 3) Check the file is copied to users home
     * 4) Copy the file within users home
     * 5) Check the file is copied within users home
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5467() throws Exception
    {
        // upload file to repository
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // copy the file from repository to users home folder
        FileDirectoryInfo fileDirectoryInfo = repositoryPage.getFileDirectoryInfo(sampleFile.getName());
        fileDirectoryInfo.selectCheckbox();
        String[] destinationFolders = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, sampleFile.getName(), destinationFolders, true);

        // check the file is copied to users home
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, destinationFolders);
        fileDirectoryInfo = repositoryPage.getFileDirectoryInfo(sampleFile.getName());
        Assert.assertEquals(fileDirectoryInfo.getName(), sampleFile.getName());

        // copy file within users home
        fileDirectoryInfo.selectCheckbox();
        String[] destinationUsersHomeFolders = { testUser };
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, sampleFile.getName(), destinationUsersHomeFolders, true);
        String copiedFileName = "Copy of " + sampleFile.getName();

        // check the file is copied
        fileDirectoryInfo = repositoryPage.getFileDirectoryInfo(copiedFileName);
        Assert.assertEquals(fileDirectoryInfo.getName(), copiedFileName);

        // delete files from users home
        repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectCheckbox();
        repositoryPage.getFileDirectoryInfo(copiedFileName).selectCheckbox();
        ConfirmDeletePage deletePage = repositoryPage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();

        // delete files from repository - after asserts
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectCheckbox();
        deletePage = repositoryPage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();

    }

    /**
     * Copy to My User home - Cancel
     * 1) Upload file to repository
     * 2) Cancel copying of the file from repository to users home folder
     * 3) Check user has returned to repository browser
     * 4) Check the file is not copied to users home
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5468() throws Exception
    {

        // upload file to repository
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // copy file from repository to users home - cancel
        FileDirectoryInfo fileDirectoryInfo = repositoryPage.getFileDirectoryInfo(sampleFile.getName());
        fileDirectoryInfo.selectCheckbox();
        String[] destinationFolders = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryCancel(drone, sampleFile.getName(), destinationFolders, true);

        // check user has returned to repository browser
        Assert.assertTrue(drone.getCurrentPage().getTitle().contains("Repository Browser"));

        // check the file is not copied to users home
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, destinationFolders);
        Assert.assertFalse(repositoryPage.isFileVisible(sampleFile.getName()));

        // delete files from repository - after asserts
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectCheckbox();
        ConfirmDeletePage deletePage = repositoryPage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();

    }

    /**
     * Move to My User home
     * 1) Upload file to repository
     * 2) Move the file from repository to users home
     * 3) Check the file is present in users home after moving
     * 4) Move the file in users home again
     * 5) Check that copy of the file is not present in users home
     * 6) Check the file is not present in repository anymore
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5469() throws Exception
    {

        // upload file to repository
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // move file from repository to users home
        FileDirectoryInfo fileDirectoryInfo = repositoryPage.getFileDirectoryInfo(sampleFile.getName());
        fileDirectoryInfo = repositoryPage.getFileDirectoryInfo(sampleFile.getName());
        String[] destinationFolders = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, sampleFile.getName(), destinationFolders, false);

        // check the file is moved - the file is in users folder after moving
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, destinationFolders);
        fileDirectoryInfo = repositoryPage.getFileDirectoryInfo(sampleFile.getName());
        Assert.assertEquals(fileDirectoryInfo.getName(), sampleFile.getName());

        // move the file in users home again
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryOk(drone, sampleFile.getName(), destinationFolders, false);

        // check that copy of the file is not present in users home
        String copiedFileName = "Copy of " + sampleFile.getName();
        Assert.assertFalse(repositoryPage.isFileVisible(copiedFileName));

        // check the file is not present in repository anymore - navigate to repository first
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        Assert.assertFalse(repositoryPage.isFileVisible(sampleFile.getName()));

        // delete file from users home - after asserts
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectCheckbox();
        ConfirmDeletePage deletePage = repositoryPage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();

    }

    /**
     * Move to My User home - Cancel
     * 1) Upload file to repository
     * 2) Cancel moving of the file from repository to users home
     * 3) Check the file is not present in users home after moving
     * 4) Check the file is still present in repository
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5470() throws Exception
    {

        // upload file to repository
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // move file from repository to users home - cancel
        FileDirectoryInfo fileDirectoryInfo = repositoryPage.getFileDirectoryInfo(sampleFile.getName());
        fileDirectoryInfo.selectCheckbox();
        String[] destinationFolders = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.copyOrMoveToFolderInRepositoryCancel(drone, sampleFile.getName(), destinationFolders, false);

        // check the file is not moved to users home
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, destinationFolders);
        Assert.assertFalse(repositoryPage.isFileVisible(sampleFile.getName()));

        // check the file is in still in repository
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // delete file from repository - after asserts
        repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectCheckbox();
        ConfirmDeletePage deletePage = repositoryPage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();

    }

    /**
     * Verify links at the document details page
     * 1) Upload file to test folder
     * 2) Open document in new tab
     * 3) Check that the current page displayed in the new tab
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_5471() throws Exception
    {
        // Upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // open document in new tab
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        detailsPage.openCopyThisLinkInNewTab();

        // check that the current page displayed in the new tab
        Assert.assertEquals(detailsPage.getDocumentTitle(), sampleFile.getName());

    }

    /**
     * Editing inline of .js file
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentUsersHome" })
    public void enterprise40x_8471() throws Exception
    {
        // upload .js file to Data Dictionary/Web Scripts Extensions
        File sampleFile = SiteUtil.prepareFile();
        File jsFile = File.createTempFile(getTestName() + System.currentTimeMillis(), ".js");
        jsFile.delete();
        sampleFile.renameTo(jsFile);

        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        
        String folderPath = REPO + SLASH + "Data Dictionary" + SLASH + "Web Scripts Extensions";
        repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, folderPath);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, jsFile);
        Assert.assertTrue(repositoryPage.isFileVisible(jsFile.getName()));

        // check edit inline option is present
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(jsFile.getName()).render();
        Assert.assertTrue(detailsPage.isInlineEditLinkDisplayed());

        // Edit inline
        ContentDetails contentDetails = new ContentDetails();
        String newFileName = getTestName() + System.currentTimeMillis() + ".js";
        String newTitle = getTestName() + " title";
        String newDescription = getTestName() + " description";
        String newContent = getTestName() + " content";
        contentDetails.setName(newFileName);
        contentDetails.setTitle(newTitle);
        contentDetails.setDescription(newDescription);
        contentDetails.setContent(newContent);

        EditTextDocumentPage editTextDocumentPage = detailsPage.selectInlineEdit().render();
        editTextDocumentPage.save(contentDetails).render();

        repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, folderPath);

        // verify changes
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(newFileName).getName(), newFileName);
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(newFileName).getTitle(), "(" + newTitle + ")");
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(newFileName).getDescription(), newDescription);

        detailsPage = repositoryPage.selectFile(newFileName).render();
        editTextDocumentPage = detailsPage.selectInlineEdit().render();
        Assert.assertTrue(editTextDocumentPage.getDetails().getContent().contains(newContent));

    }
}
