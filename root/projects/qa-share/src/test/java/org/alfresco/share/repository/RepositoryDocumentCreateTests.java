/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.repository;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author jcule
 *
 */
@Listeners(FailedTestListener.class)
public class RepositoryDocumentCreateTests extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(RepositoryDocumentCreateTests.class);

    private String testUser;
    // TODO: Define this generic string in language property files
    private static final String USER_HOMES_FOLDER = "User Homes";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {

        super.setup();
        // create a single user and promote it as admin
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * User logs in before test is executed
     * 
     * @throws Exception
     */
    @BeforeMethod(groups = { "RepositoryDocumentCreate" })
    public void prepare() throws Exception
    {
        // login as created user
        try
        {
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            logger.info("RepositoryDocumentCreate user logged in - drone.");
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
    @AfterMethod(groups = { "RepositoryDocumentCreate" })
    public void quit() throws Exception
    {
        // logout as created user
        try
        {
            ShareUser.logout(drone);
            logger.info("RepositoryDocumentCreate user logged out - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Create Any Content - Create plain text document
     * 1) Create plain text file in test folder
     * 2) Check name, description and content are correct
     * 
     * @throws Exception
     */

    @Test(groups = "RepositoryDocumentCreate")
    public void enterprise40x_5432() throws Exception
    {

        // create a plain text file in test folder
        String testName = getTestName();
        String fileName = testName + System.currentTimeMillis();

        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }

        String description = testName + " description";
        String content = testName + " content";

        // create a plain text file in test folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(testName + " title");
        contentDetails.setDescription(testName + " description");
        contentDetails.setContent(testName + " content");
        
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);

        String[] contentFolderPath = { USER_HOMES_FOLDER, testUser, testName };
        repositoryPage = ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, contentFolderPath);
        
        Assert.assertTrue(repositoryPage.isFileVisible(fileName));

        // Check name, description and content are correct
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(fileName).getName(), fileName);
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(fileName).getDescription(), description);

        // TODO: Consider implementing this as a Util: using View in Browser?
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(fileName).render();
        EditTextDocumentPage editTextDocumentPage = detailsPage.selectInlineEdit().render();
        Assert.assertTrue(editTextDocumentPage.getDetails().getContent().contains(content));

    }

    /**
     * Create plain text document - Cancel
     * 1) Navigate to users home directory
     * 2) Create test folder in users home directory
     * 3) Cancel creation of plain text file
     * 4) Check the file is not present in test folder
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentCreate" })
    public void enterprise40x_5433() throws Exception
    {
        String testName = getTestName();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        String fileName = getTestName() + System.currentTimeMillis();
        String title = testName + " title";
        String description = testName + " description";
        String content = testName + " content";
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(title);
        contentDetails.setDescription(description);
        contentDetails.setContent(content);

        // Cancel creation of plain text file
        CreatePlainTextContentPage contentPage = repositoryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        contentPage.cancel(contentDetails).render();

        // Check the file is not present in test folder
        Assert.assertFalse(repositoryPage.isFileVisible(fileName));
    }

    /**
     * Upload file
     * 1) Upload file to the test folder in repository
     * 2) Check the file is uploaded successfully
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentCreate" })
    public void enterprise40x_5434() throws Exception
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

        // Check the file is uploaded successfully
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

    }

    /**
     * Cancel file upload
     * 1) Cancel file upload to the test folder in repository
     * 2) Check the file is not uploaded to the test folder in repository
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentCreate" })
    public void enterprise40x_5435() throws Exception
    {
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();

        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositorySimpleView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }

        // Cancel file upload to the test folder in repository
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        UploadFilePage uploadForm = repositoryPage.getNavigation().selectFileUpload().render();
        uploadForm.cancel();

        // Check the file is not uploaded to the test folder in repository
        Assert.assertFalse(repositoryPage.isContentUploadedSucessful(sampleFile.getName()));

    }

    /**
     * Edit file metadata
     * 1) Upload file to the test folder in repository
     * 2) Edit file properties
     * 3) Check file properties are correct
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentCreate" })
    public void enterprise40x_5436() throws Exception
    {
        // Upload file to the test folder in repository
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));

        // edit file properties
        String titleNew = testName + " enterprise40x_5436 title new";
        String descriptionNew = testName + " enterprise40x_5436 description new";
        String tagNew = testName + " enterprise40x_5436 tag new";

        EditDocumentPropertiesPage editDocumentPropertiesPopup = repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectEditProperties().render();

        String fileName = getTestName() + System.currentTimeMillis();

        editDocumentPropertiesPopup.setName(fileName);
        editDocumentPropertiesPopup.setDocumentTitle(titleNew);
        editDocumentPropertiesPopup.setDescription(descriptionNew);
        TagPage tagPage = editDocumentPropertiesPopup.getTag().render();
        tagPage = tagPage.enterTagValue(tagNew).render();
        tagPage.clickOkButton();
        editDocumentPropertiesPopup.selectSave().render();

        // Check file properties are correct
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(fileName).getTitle(), "(" + titleNew + ")");
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(fileName).getDescription(), descriptionNew);
        Assert.assertTrue(repositoryPage.getFileDirectoryInfo(fileName).getTags().contains(tagNew.toLowerCase()));

    }

    /**
     * Edit file metadata - cancel
     * 1) Upload file to the test folder in repository
     * 2) Cancel editing of file properties
     * 3) Check file properties didn't change
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentCreate" })
    public void enterprise40x_5437() throws Exception
    {
        // upload file to test folder
        String testName = getTestName();
        File sampleFile = SiteUtil.prepareFile();
        RepositoryPage repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        String[] folderPath = { USER_HOMES_FOLDER, testUser };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        if (!repositoryPage.isFileVisible(testName))
        {
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }
        repositoryPage = (RepositoryPage) repositoryPage.selectFolder(testName);
        repositoryPage = ShareUserRepositoryPage.uploadFileInRepository(drone, sampleFile);
        Assert.assertTrue(repositoryPage.isFileVisible(sampleFile.getName()));
        // edit file properties
        EditDocumentPropertiesPage editDocumentPropertiesPopup = repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectEditProperties().render();

        String titleNew = testName + " enterprise40x_5437 title new";
        String descriptionNew = testName + " enterprise40x_5437 description new";
        String tagNew = testName + " enterprise40x_5437 tag new";
        String fileName = getTestName() + System.currentTimeMillis();

        editDocumentPropertiesPopup.setName(fileName);
        editDocumentPropertiesPopup.setDocumentTitle(titleNew);
        editDocumentPropertiesPopup.setDescription(descriptionNew);
        TagPage tagPage = editDocumentPropertiesPopup.getTag().render();
        tagPage = tagPage.enterTagValue(tagNew).render();
        tagPage.clickOkButton();
        editDocumentPropertiesPopup.selectCancel().render();

        List<FileDirectoryInfo> files = repositoryPage.getFiles();
        Assert.assertFalse(files.contains(fileName));
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(sampleFile.getName()).getDescription(), "No Description");

        // Check file properties didn't change
        Assert.assertFalse(repositoryPage.getFileDirectoryInfo(sampleFile.getName()).hasTags());
    }

    /**
     * View in browser
     * 1) Upload file to the test folder in repository
     * 2) Select to view document in browser
     * 3) Check the document is correctly displayed in browser
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentCreate" })
    public void enterprise40x_5438() throws Exception
    {
        // upload file to test folder
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
        // select to view it browser
        repositoryPage.getFileDirectoryInfo(sampleFile.getName()).selectViewInBrowser();

        // check the document is correctly displayed
        String content = "this is a sample test upload file";
        String htmlSource = ((WebDroneImpl) drone).getDriver().getPageSource();
        Assert.assertTrue(htmlSource.contains(content));

    }

    /**
     * Upload new version (minor changes)
     * 1) Upload file to the test folder in repository
     * 2) Upload new version of the file with minor changes
     * 3) Check that the version has increased
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentCreate" })
    public void enterprise40x_5439() throws Exception
    {
        // upload file to test folder
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
        // Upload new version of document with minor changes
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        detailsPage = ShareUserSitePage.uploadNewVersionFromDocDetail(drone, false, sampleFile.getName(), "New version uploaded.");

        // Check the version is increased
        Assert.assertEquals(detailsPage.getDocumentVersion(), "1.1");
    }

    /**
     * Upload new version (major changes)
     * 1) Upload file to the test folder in repository
     * 2) Upload new version of the file with major changes
     * 3) Check that the version has increased
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentCreate" })
    public void enterprise40x_5440() throws Exception
    {
        // upload file to test folder
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
        // Upload new version of document with minor changes
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        detailsPage = ShareUserSitePage.uploadNewVersionFromDocDetail(drone, true, sampleFile.getName(), "New version uploaded.");

        // Check the version has increased
        Assert.assertEquals(detailsPage.getDocumentVersion(), "2.0");
    }

    /**
     * Upload new version cancel (major or minor changes)
     * 1) Upload file to the test folder in repository
     * 2) Cancel upload new version of the file with major changes
     * 3) Check that the version has not increased
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentCreate" })
    public void enterprise40x_5441() throws Exception
    {
        // upload file to the test folder in repository
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

        // cancel uploading of the new file with major changes
        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        String fileContents = "New File being created on repository page:" + sampleFile.getName();
        File newFile = newFile(sampleFile.getName(), fileContents);

        UpdateFilePage updatePage = detailsPage.selectUploadNewVersion().render();
        updatePage.selectMajorVersionChange();
        updatePage.uploadFile(newFile.getCanonicalPath());
        updatePage.setComment("New version uploaded.");
        updatePage.selectCancel();

        // check that the version has not increased
        Assert.assertEquals(detailsPage.getDocumentVersion(), "1.0");
    }

    /**
     * Edit document inline
     * 1) upload file to the test folder in repository
     * 2) edit inline file properties
     * 3) check file properties are correct
     * 
     * @throws Exception
     */

    @Test(groups = { "RepositoryDocumentCreate" })
    public void enterprise40x_5442() throws Exception
    {
        // upload file to the test folder in repository
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

        String fileName = testName + System.currentTimeMillis();
        String newTitle = testName + " title";
        String newDescription = testName + " description";
        String newContent = testName + " content";

        // edit inline file properties
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(newTitle);
        contentDetails.setDescription(newDescription);
        contentDetails.setContent(newContent);

        DocumentDetailsPage detailsPage = repositoryPage.selectFile(sampleFile.getName()).render();
        EditTextDocumentPage editTextDocumentPage = detailsPage.selectInlineEdit().render();
        editTextDocumentPage.save(contentDetails).render();

        repositoryPage = ShareUserRepositoryPage.openRepositoryDetailedView(drone);
        String[] testFolderPath = { USER_HOMES_FOLDER, testUser, testName };
        repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, testFolderPath);

        // check file properties are correct
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(fileName).getName(), fileName);
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(fileName).getTitle(), "(" + newTitle + ")");
        Assert.assertEquals(repositoryPage.getFileDirectoryInfo(fileName).getDescription(), newDescription);

        detailsPage = repositoryPage.selectFile(fileName).render();
        editTextDocumentPage = detailsPage.selectInlineEdit().render();
        Assert.assertTrue(editTextDocumentPage.getDetails().getContent().contains(newContent));

    }

}
