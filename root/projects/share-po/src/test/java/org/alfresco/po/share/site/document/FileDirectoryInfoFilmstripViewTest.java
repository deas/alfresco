/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import static org.alfresco.po.share.site.document.DocumentAspect.CLASSIFIABLE;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Chiran
 */
@Listeners(FailedTestListener.class)
@Test(groups="AceBug")
public class FileDirectoryInfoFilmstripViewTest extends AbstractDocumentTest
{
    private final Log logger = LogFactory.getLog(this.getClass());
    private static String siteName;
    private static String folderName;
    private static String folderDescription;
    private String userName = "user" + System.currentTimeMillis() + "@test.com";
    private String firstName = userName;
    private String lastName = userName;
    private static DocumentLibraryPage documentLibPage;
    private File file;
    private File testLockedFile;
    private File tempFile;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(alwaysRun=true)
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folderDescription = String.format("Description of %s", folderName);
        createUser();
      //  WebDroneUtil.loginAs(drone, shareUrl, username, password).render();
        if(isHybridEnabled())
        {
            signInToCloud(drone, cloudUserName, cloudUserPassword);
        }
        SiteUtil.createSite(drone, siteName, "description", "Public");
        file = SiteUtil.prepareFile("alfresco123");
        testLockedFile = SiteUtil.prepareFile("Alfresco456");   
        tempFile = SiteUtil.prepareFile();
        createData();
    }



    @AfterClass(groups = { "alfresco-one" })
    public void teardown()
    {
        SiteFinderPage siteFinder = SiteUtil.searchSite(drone, siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        DocumentLibraryPage docPage = siteDash.getSiteNav().selectSiteDocumentLibrary().render();
        docPage.getNavigation().selectDetailedView();

        SiteUtil.deleteSite(drone, siteName);
        
        if (isHybridEnabled())
        {
            // go to profile
            MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();

            // Click cloud sync
            CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
            if (cloudSyncPage.isDisconnectButtonDisplayed())
            {
                cloudSyncPage.disconnectCloudAccount().render();
            }
        }
    }
    /**
     * Create User 
     * @throws Exception 
     */
    private void createUser() throws Exception
    {
        if (!alfrescoVersion.isCloud())
        {
            DashBoardPage dashBoard = loginAs(username, password);
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            newPage.inputFirstName(firstName);
            newPage.inputLastName(lastName);
            newPage.inputEmail(userName);
            newPage.inputUsername(userName);
            newPage.inputPassword(userName);
            newPage.inputVerifyPassword(userName);
            UserSearchPage userCreated = newPage.selectCreateUser().render();
            userCreated.searchFor(userName).render();
            Assert.assertTrue(userCreated.hasResults());
            logout(drone);
            loginAs(userName, userName);
        }
        else
        {
            loginAs(username, password);
            firstName = anotherUser.getfName();
            lastName = anotherUser.getlName();
            userName = firstName + " " + lastName;
        }
       
    }
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    //@Test(groups={"alfresco-one"})
    private void createData() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(testLockedFile.getCanonicalPath()).render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        documentLibPage = ((DocumentLibraryPage) documentLibPage.getNavigation().selectFilmstripView()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
    }
    

    /**
     * Method renders the documentlibrary page and returns the file as FileDirectoryInfo
     * @return FileDirectoryInfo element for file / row at index 1
     * @throws Exception
     */
    private FileDirectoryInfo getFile() throws Exception
    {
        documentLibPage = drone.getCurrentPage().render();
        List<FileDirectoryInfo> results = documentLibPage.getFiles();
        if(results.isEmpty())
        {
            throw new Exception("Error getting file");
        }
        else
        {
            // Get file
            return results.get(1);
        }
    }
    
    @Test(groups = { "alfresco-one" }, priority = 1)
    public void test101SelectManageRules()
    {
     // Get folder
        documentLibPage = documentLibPage.render();
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
        FolderRulesPage page = thisRow.selectManageRules().render();
        Assert.assertNotNull(page);
        SiteFinderPage siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        documentLibPage = siteDash.getSiteNav().selectSiteDocumentLibrary().render();
    }
    
    
    @Test(groups={"alfresco-one"}, priority=2)
    public void test102ContentCheckBoxForFolder() throws Exception
    {
        // Get folder row
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        String thisRowName = thisRow.getName();
        Assert.assertEquals(thisRowName, folderName);
        // Content CheckBox
        Assert.assertFalse(thisRow.isCheckboxSelected());
        thisRow.selectCheckbox();
        Assert.assertTrue(thisRow.isCheckboxSelected());

        // UnSelect
        thisRow.selectCheckbox();
        Assert.assertFalse(thisRow.isCheckboxSelected());
    }

    @Test(groups={"alfresco-one"}, priority=3)
    public void test103NodeRefForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);

        // NodeRef
        Assert.assertNotNull(thisRow.getContentNodeRef(), "Node Reference is null");
        logger.info("NodeRef:" + thisRow.getContentNodeRef());
    }

    @Test(groups={"alfresco-one"}, priority=4)
    public void test104ContentEditInfoForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);

        // Get ContentEditInfo
        Assert.assertNotNull(thisRow.getContentEditInfo());
        Assert.assertTrue(thisRow.getContentEditInfo().contains(userName), thisRow.getContentEditInfo() + " should contain: " + userName);
    }

    @Test(groups={"alfresco-one"}, priority=5)
    public void test105LikeMethodsForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        // Like
        thisRow.selectLike();
        documentLibPage = documentLibPage.render();
        thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        Assert.assertTrue(thisRow.getLikeCount().equals("1"));
    }

    @Test(groups={"alfresco-one"}, priority=6)
    public void test106FavouriteMethodsForFolder() throws Exception
    {
        documentLibPage = documentLibPage.render();
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        // Favourite
        thisRow.selectFavourite();
        Assert.assertTrue(thisRow.isFavourite());
    }

    @Test(groups={"Enterprise4.2"}, priority=7)
    public void test107TagsForFolder() throws Exception
    {
        String tagName = "foldertag";

        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        thisRow.addTag(tagName);
        documentLibPage = documentLibPage.render();
        thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        Assert.assertTrue(thisRow.getTags().size() == 1);
        Assert.assertTrue(thisRow.getTags().contains(tagName));
    }

    @Test(groups={"alfresco-one"}, priority=10)
    public void test110NodeRefForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        documentLibPage = thisRow.selectThumbnail().render();

        // NodeRef
        Assert.assertNotNull(thisRow.getContentNodeRef(), "Node Reference is null");
        logger.info("NodeRef:" + thisRow.getContentNodeRef());

        thisRow.selectThumbnail();
        Assert.assertFalse(thisRow.isVersionVisible());
        Assert.assertTrue(thisRow.isCheckBoxVisible());
        Assert.assertTrue(thisRow.getVersionInfo().equalsIgnoreCase("1.0"));
        Assert.assertTrue(thisRow.getContentNameFromInfoMenu().equalsIgnoreCase(file.getName()));
    }

    @Test(groups={"alfresco-one"}, priority=11)
    public void test111ContentEditInfoForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = getFile();

        // Get ContentEditInfo
        Assert.assertNotNull(thisRow.getContentEditInfo());
        Assert.assertTrue(thisRow.getContentEditInfo().contains(firstName), thisRow.getContentEditInfo());
    }

    @Test(groups={"alfresco-one"}, priority=12)
    public void test112LikeMethodsForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());

        // Like
        thisRow.selectLike();
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();
        thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        Assert.assertTrue(thisRow.isLiked());
    }

    @Test(groups={"alfresco-one"}, priority=13)
    public void test113FavouriteMethodsForFile() throws Exception
    {
        documentLibPage = documentLibPage.render();
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());

        // Favorite
        thisRow.selectFavourite();
        thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        Assert.assertTrue(thisRow.isFavourite());
    }

    @Test(groups={"alfresco-one"}, priority=15)
    public void test115SelectDownloadForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
        thisRow.selectDownload();
        Assert.assertNotNull(documentLibPage);
    }

    @Test(groups={"alfresco-one"}, priority=16)
    public void test116IsDeleteLinkPresent()
    {
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        assertTrue(thisRow.isDeletePresent());
    }

    @Test(groups={"alfresco-one"}, priority=17)
    public void test117SelectThumbnailForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        SitePage sitePage = thisRow.selectThumbnail().render();
        Assert.assertTrue(sitePage instanceof DocumentLibraryPage);
    }

    @Test(groups={"alfresco-one"}, priority=18)
    public void test118SelectThumbnailForFolder() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        try
        {
            assertNotNull(page);
            documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

            // Get File
            FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
            SitePage sitePage = thisRow.selectThumbnail().render();
            Assert.assertTrue(sitePage instanceof DocumentLibraryPage);
        }
        catch (Throwable e)
        {
            saveScreenShot("ShareContentRowTest.testSelectThumbnailForFile");
            throw new Exception(e);
        }
        finally
        {
            documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        }
    }

    @Test(groups = {  "Enterprise4.2" , "Cloud2"}, priority=19)
    public void test119managePermissionTest()
    {
        documentLibPage.render();
        documentLibPage = documentLibPage.getNavigation().selectFilmstripView().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        Assert.assertTrue(thisRow.isManagePermissionLinkPresent());
        ManagePermissionsPage mangPermPage = thisRow.selectManagePermission().render();       
        Assert.assertTrue(mangPermPage.isInheritPermissionEnabled());
        documentLibPage = ((DocumentLibraryPage)mangPermPage.selectSave()).render();
    }
    @Test(groups = { "Enterprise4.2" }, priority=20)
    public void test120IsEditInGoogleDocsPresent()
    {
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        assertTrue(thisRow.isEditInGoogleDocsPresent());
    }
    
    @Test(expectedExceptions = UnsupportedOperationException.class, groups = { "Enterprise4.2" }, priority=21)
    public void test121SelectDownloadFolderAsZipForFile() throws Exception
    {
        // Get File
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
        thisRow.selectDownloadFolderAsZip();
    }

    @Test(groups = "Enterprise4.2", priority=23)
    public void test123SelectDownloadFolderAsZipForFolder() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
        thisRow.selectDownloadFolderAsZip();
        drone.waitUntilElementDisappears(By.cssSelector("div[id*='archive-and-download'] a"), 2000);
    }

    @Test(groups={"alfresco-one"}, priority=24)
    public void testGetDescription() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        // Like
        String desc = thisRow.getDescription();
        Assert.assertNotNull(desc);
        assertTrue(desc.contains("Created") || desc.contains("Modified"));
    }

    @Test(groups={"alfresco-one"}, priority=25)
    public void testGetCategories() throws Exception
    {
        // Get folder
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        // Like
        List<Categories> categories = thisRow.getCategories();
        Assert.assertNotNull(categories);
    }
    
    @Test(groups={"Enterprise4.2","TestBug"}, priority=27)
    public void testClickOnRemoveAddTag() throws Exception
    {
        String tagName = "foldertag2";
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        thisRow.addTag(tagName);
        documentLibPage = documentLibPage.render();
        thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        thisRow.clickOnAddTag();
        thisRow.clickOnTagRemoveButton(tagName);
        thisRow.clickOnTagSaveButton();
        documentLibPage = documentLibPage.render();
        thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        Assert.assertFalse(thisRow.hasTags());
    }
    
   /* 
    * MNT-10630, once this issue is fixed we can enable the below test.
    * @Test(enabled = true, groups = { "Enterprise4.2" }, priority = 28)
    public void test124SelectStartWorkFlow() throws Exception
    {
        // Select SyncToCloud
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        thisRow.selectThumbnail();
        StartWorkFlowPage startWorkFlowPage = thisRow.selectStartWorkFlow().render();
        Assert.assertTrue(startWorkFlowPage.getTitle().contains("Start Workflow"));
        
        SiteFinderPage siteFinder = startWorkFlowPage.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        documentLibPage = siteDash.getSiteNav().selectSiteDocumentLibrary().render();
        Assert.assertNotNull(documentLibPage);
    }*/
    

    @Test(enabled = false, groups = { "Hybrid" }, priority = 29)
    public void test125SelectSyncToCloud() throws Exception
    {
        // Select SyncToCloud
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        DestinationAndAssigneePage destinationAndAssigneePage = (DestinationAndAssigneePage) thisRow.selectSyncToCloud().render();
        Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), "Sync " + file.getName() + " to The Cloud");
        destinationAndAssigneePage.selectSubmitButtonToSync();
        assertTrue("File should be synced", thisRow.isCloudSynced());
    }


    @Test(groups = { "Hybrid" }, priority=30)
    public void test126selectInlineEdit()
    {
        documentLibPage = drone.getCurrentPage().render();        
        InlineEditPage inlineEditPage = documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).selectInlineEdit().render();
        EditTextDocumentPage editTextDocumentPage = (EditTextDocumentPage)inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT);
        ContentDetails contentDetails = editTextDocumentPage.getDetails();
        Assert.assertEquals(contentDetails.getName(), testLockedFile.getName());
        documentLibPage = editTextDocumentPage.selectCancel().render();
        documentLibPage = ((DocumentLibraryPage) documentLibPage.getNavigation().selectFilmstripView()).render();
    }

    @Test(enabled = false, groups = { "Hybrid" }, priority = 31)
    public void test127isLockedTest()
    {
        documentLibPage = drone.getCurrentPage().render();
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isLocked(), "Verify the file is not locked");
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isInlineEditLinkPresent(), "Verify the Inline Edit option is displayed");
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isEditOfflineLinkPresent(), "Verify the Edit Offline option is displayed");
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork("premiernet.test");
        Assert.assertFalse(destinationAndAssigneePage.isFolderDisplayed(String.valueOf(Math.random())));
        Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed("Documents"));
        destinationAndAssigneePage.selectFolder("Documents");
        destinationAndAssigneePage.selectLockOnPremCopy();
        DocumentLibraryPage documentLibraryPage = destinationAndAssigneePage.selectSubmitButtonToSync().render();
        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(testLockedFile.getName()).isLocked());
        Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(testLockedFile.getName()).getContentInfo(), "This document is locked by you.");
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isInlineEditLinkPresent(), "Verify the Inline Edit option is NOT displayed");
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isEditOfflineLinkPresent(), "Verify the Edit Offline option is NOT displayed");
    }
    
    @Test(groups="Enterprise4.2", priority=32)
    public void selectDeleteforContent() throws Exception
    {
       documentLibPage.render();
        ConfirmDeletePage confirmdialog;
        int fileSize = 0;
        FileDirectoryInfo file = documentLibPage.getFileDirectoryInfo(tempFile.getName());
        confirmdialog = file.selectDelete().render();
        documentLibPage = ((DocumentLibraryPage) confirmdialog.selectAction(Action.Cancel)).render();
        fileSize = documentLibPage.getFiles().size();
        Assert.assertTrue(documentLibPage.getTitle().contains("Document Library"));
        file = documentLibPage.getFileDirectoryInfo(tempFile.getName());
        confirmdialog = file.selectDelete().render();
        documentLibPage = confirmdialog.selectAction(Action.Delete).render();
        Assert.assertEquals(documentLibPage.getFiles().size(), fileSize-1);
    } 
    
    @Test(groups="alfresco-one", priority=33)
    public void testEditProperites()
    {
        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(1);
        Assert.assertEquals(fileInfo.getName(), file.getName());
        Assert.assertTrue(fileInfo.isEditPropertiesLinkPresent());
        EditDocumentPropertiesPage editPage = fileInfo.selectEditProperties().render();
        Assert.assertNotNull(editPage);
        editPage.setDescription("the description");
        documentLibPage = editPage.selectSave().render();
    }
    
    @Test(groups="Enterprise4.2", priority=34)
    public void testIsPartOfWorkFlow() throws InterruptedException
    {
        DocumentDetailsPage documentDetailsPage = documentLibPage.selectFile(file.getName()).render();
        Assert.assertFalse(documentDetailsPage.isPartOfWorkflow());
        StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, siteName, reviewers);
        newWorkflowPage.cancelCreateWorkflow(formDetails).render();
        openSiteDocumentLibraryFromSearch(drone, siteName);
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        assertFalse("Document should not be part of workflow.", thisRow.isPartOfWorkflow());
    }
    
    @Test(groups = { "alfresco-one" }, priority=35)
    public void testSelectViewFolderDetails() throws Exception
    {
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        FolderDetailsPage folderDetailsPage = thisRow.selectViewFolderDetails().render();
        Assert.assertEquals(folderName, folderDetailsPage.getContentTitle());
        documentLibPage = folderDetailsPage.getSiteNav().selectSiteDocumentLibrary().render();
    }
    
    @Test(enabled = false, groups = { "GoogleDocs" }, priority = 36)
    public void testSelectEditInGoogleDocs() throws Exception
    {
        File tempFile = SiteUtil.prepareFile("test" + System.currentTimeMillis());
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        // Get File row
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(tempFile.getName());

        Assert.assertTrue(thisRow.isEditInGoogleDocsPresent());
        GoogleDocsAuthorisation returnPage = thisRow.selectEditInGoogleDocs().render();
        Assert.assertTrue(returnPage.isAuthorisationDisplayed());

        GoogleSignUpPage signUpPage = returnPage.submitAuth().render();
        Assert.assertTrue(signUpPage.isSignupWindowDisplayed());

        EditInGoogleDocsPage googleDocsPage = signUpPage.signUp(googleusername, googlepassword).render();
        assertTrue(googleDocsPage.isBrowserTitle("Google Docs Editor"));

        googleDocsPage.selectDiscard().render().clickOkButton();
        documentLibPage = documentLibPage.render();
    }
    
    /*  Note: MNT-10630, once this issue is fixed we can enable the below test.
     * 
     * @Test(groups = { "Hybrid" }, priority = 37)
     public void tesSelectUnSyncAndRemoveContentFromCloud() throws Exception
     {
         // Select SyncToCloud
         Assert.assertEquals("Click to view sync info", documentLibPage.getFileDirectoryInfo(file.getName()).getCloudSyncType());
         Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file.getName()).isUnSyncFromCloudLinkPresent(),
                 "Verifying \"UnSync from Cloud\" link is not present");
         FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
         SyncInfoPage syncInfoPage = thisRow.clickOnViewCloudSyncInfo();
         syncInfoPage.render(5000);
         Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());
         documentLibPage = thisRow.selectUnSyncAndRemoveContentFromCloud(true).render();
         thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
         // Verify CloudSync icon is not displayed
         Assert.assertFalse(thisRow.isCloudSynced(), "Verifying Cloud Sync icon is not be displayed");
         Assert.assertFalse(thisRow.isViewCloudSyncInfoLinkPresent());
     }
    
    @Test(enabled = true, groups = { "Hybrid" }, priority = 38)
    public void isSyncFailedIconPresent() throws IOException
    {
        File testSyncFailedFile = SiteUtil.prepareFile("SyncFailFile");
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(testSyncFailedFile.getCanonicalPath()).render();
        
        DestinationAndAssigneePage destinationAndAssigneePage = documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).selectSyncToCloud().render();
        destinationAndAssigneePage.selectNetwork("premiernet.test");
        destinationAndAssigneePage.render();
        documentLibPage = (DocumentLibraryPage)destinationAndAssigneePage.selectSubmitButtonToSync();
        documentLibPage.render();
        // Verify the Sync Failed icon is not displayed
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isCloudSynced());
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isSyncFailedIconPresent(5000));
        // Disconnect CloudSync
        disconnectCloudSync(drone);
        documentLibPage = openSiteDocumentLibraryFromSearch(drone, siteName);
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(testSyncFailedFile.getName()).render();
        EditTextDocumentPage inlineEditPage = detailsPage.selectInlineEdit().render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(testSyncFailedFile.getName());
        contentDetails.setDescription("isSyncFailedIconPresent test");
        detailsPage = inlineEditPage.save(contentDetails).render();
        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();

        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isSyncToCloudLinkPresent(), "Verifying \"Sync to Cloud\" link is NOT present");
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isRequestToSyncLinkPresent());
        // Select Request to sync option from more options
        documentLibPage = documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).selectRequestSync().render();
        // Verify the Sync Failed icon is displayed
        Assert.assertNotNull(documentLibPage);
    }*/
    
    @Test(groups="Enterprise4.2", priority=39)
    public void testCopyToFolder() throws Exception
    {
       
        CopyOrMoveContentPage copyOrMoveContentPage = documentLibPage.getFileDirectoryInfo(file.getName()).selectCopyTo().render();
        Assert.assertNotNull(copyOrMoveContentPage);
        copyOrMoveContentPage.selectCloseButton();
        
        copyOrMoveContentPage = documentLibPage.getFileDirectoryInfo(file.getName()).selectMoveTo().render();
        Assert.assertNotNull(copyOrMoveContentPage);
        copyOrMoveContentPage.selectCloseButton();
        
    }
    /**
     * test to upload new version 
     * @author sprasanna
     * @throws Exception
     */ 
    @Test(groups="Enterprise4.2", priority=40)
    public void selectUploadNewVersion() throws Exception
    {
        documentLibPage.render();
        File temp1File = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(temp1File.getCanonicalPath()).render();
        UpdateFilePage updateFilePage =  documentLibPage.getFileDirectoryInfo(temp1File.getName()).selectUploadNewVersion().render();
        updateFilePage.selectMajorVersionChange();
        updateFilePage.selectCancel();
        documentLibPage = drone.getCurrentPage().render();
        Assert.assertTrue(documentLibPage.getTitle().contains("Document Library"));
    }
    
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    @Test(groups="Enterprise4.2", priority=41)
    public void testSelectEditOffline() throws Exception
    {
        assertFalse(documentLibPage.getFileDirectoryInfo(file.getName()).isEdited());
        documentLibPage = documentLibPage.getFileDirectoryInfo(file.getName()).selectEditOffline().render();
    }
    
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    @Test(groups="Enterprise4.2", priority=42)
    public void selectCancelEditing() throws Exception
    {
        documentLibPage = documentLibPage.getFileDirectoryInfo(file.getName()).selectCancelEditing().render();
        assertFalse(documentLibPage.getFileDirectoryInfo(file.getName()).isEdited());
    }
    
    /**
     * Select the action of manage Aspects
     */
    
    @Test(groups="Enterprise4.2", priority=43)
    public void selectMangeAspectTest()
    {
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        SelectAspectsPage selectAspectPage = thisRow.selectManageAspects().render();
        Assert.assertNotNull(selectAspectPage);
    }

    @Test(enabled = true, groups = "Enterprise4.2", priority = 44)
    public void renameContentTest()
    {
        drone.refresh();
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        folderName = folderName + "updated";
        thisRow.renameContent(folderName);
        Assert.assertEquals(documentLibPage.getFileDirectoryInfo(folderName).getName(), folderName);
    }

    @Test(enabled = true, groups = "Enterprise4.2", priority = 45)
    public void cancelRenameContentTest()
    {
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        assertFalse(thisRow.isSaveLinkVisible());
        assertFalse(thisRow.isCancelLinkVisible());
        thisRow.contentNameEnableEdit();
        assertTrue(thisRow.isSaveLinkVisible());
        assertTrue(thisRow.isCancelLinkVisible());
        thisRow.contentNameEnter(folderName + " not updated");
        thisRow.contentNameClickCancel();
        drone.refresh();
        documentLibPage = drone.getCurrentPage().render();
        Assert.assertEquals(documentLibPage.getFileDirectoryInfo(folderName).getName(), folderName);
    }
    
    @Test(enabled = true, groups = "Enterprise4.2", priority = 46)
    public void testSelectViewInBrowser()
    {
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
        String mainWinHandle = drone.getWindowHandle();
        thisRow.selectViewInBrowser();
        assertTrue(drone.getCurrentUrl().toLowerCase().contains(file.getName().toLowerCase()));
        drone.closeWindow();
        drone.switchToWindow(mainWinHandle);
    }

    @Test(enabled = true, groups = "Enterprise4.2", priority = 47)
    public void clickOnCategoryLink()
    {
        documentLibPage = drone.getCurrentPage().render();
        SelectAspectsPage selectAspectsPage = documentLibPage.getFileDirectoryInfo(folderName).selectManageAspects().render();

        // Get several aspects in left hand side
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(CLASSIFIABLE);

        // Add several aspects to right hand side
        selectAspectsPage = selectAspectsPage.add(aspects).render();

         // Click on Apply changes on select aspects page
        selectAspectsPage.clickApplyChanges().render();
        
        EditDocumentPropertiesPage editDocumentPropertiesPopup = documentLibPage.getFileDirectoryInfo(folderName).selectEditProperties().render();
        // Add category and click ok
        CategoryPage categoryPage = editDocumentPropertiesPopup.getCategory().render();
        // Verify added category is displayed beneath categories part
        
        // Select add category
        categoryPage.add(Arrays.asList(Categories.LANGUAGES));

        // Click on save button in edit document properties pop up page
        categoryPage.clickOk().render();

        documentLibPage =  editDocumentPropertiesPopup.selectSave().render();
        
        FileDirectoryInfo fileDirInfo = documentLibPage.getFileDirectoryInfo(folderName);
        
        documentLibPage = fileDirInfo.clickOnCategoryNameLink(Categories.LANGUAGES.getValue()).render();
        int i = 0;
        do
        {
            i++;
            drone.refresh();
            documentLibPage = drone.getCurrentPage().render();
        }while(!documentLibPage.isFileVisible(folderName)  && i < 5);
        
        Assert.assertTrue(documentLibPage.isFileVisible(folderName));
    }


}