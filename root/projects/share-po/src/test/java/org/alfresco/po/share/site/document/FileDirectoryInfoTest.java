package org.alfresco.po.share.site.document;
///*
// * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
// * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
// * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
// * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
// */
//package org.alfresco.webdrone.share.site.document;
//
//import static org.testng.AssertJUnit.assertNotNull;
//import static org.testng.AssertJUnit.assertTrue;
//
//import java.io.File;
//import java.util.List;
//
//import org.alfresco.webdrone.WebDroneUtil;
//import org.alfresco.webdrone.exception.PageOperationException;
//import org.alfresco.webdrone.share.SharePage;
//import org.alfresco.webdrone.share.site.ManageRulesPage;
//import org.alfresco.webdrone.share.site.NewFolderPage;
//import org.alfresco.webdrone.share.site.SitePage;
//import org.alfresco.webdrone.share.site.UploadFilePage;
//import org.alfresco.webdrone.share.user.CloudSyncPage;
//import org.alfresco.webdrone.share.user.MyProfilePage;
//import org.alfresco.webdrone.share.workflow.DestinationAndAssigneePage;
//import org.alfresco.webdrone.testng.listener.FailedTestListener;
//import org.alfresco.webdrone.util.SiteUtil;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.openqa.selenium.By;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Listeners;
//import org.testng.annotations.Test;
//
///**
// * Integration test to verify document library page is operating correctly.
// * 
// * @author Meenal Bhave
// * @since 1.6.1
// */
//@Listeners(FailedTestListener.class)
//public class FileDirectoryInfoTest extends AbstractDocumentTest
//{
//    private final Log logger = LogFactory.getLog(this.getClass());
//
//    private static String siteName;
//    private static String folderName;
//    private static String folderDescription;
//    private static DocumentLibraryPage documentLibPage;
//    private File file;
//    private final String cloudUserName = "user1@premiernet.test";
//    private final String cloudUserPassword = "password";
//    private File testLockedFile;
//
//    /**
//     * Pre test setup of a dummy file to upload.
//     * 
//     * @throws Exception
//     */
//    @SuppressWarnings("unused")
//    @BeforeClass(groups={"alfresco-one"})
//    private void prepare() throws Exception
//    {
//        siteName = "site" + System.currentTimeMillis();
//        folderName = "The first folder";
//        folderDescription = String.format("Description of %s", folderName);
//        WebDroneUtil.loginAs(drone, shareUrl, username, password).render();
//        if(isHybridEnabled())
//        {
//            signInToCloud(drone, cloudUserName, cloudUserPassword);
//        }
//        SiteUtil.createSite(drone, siteName, "description", "Public");
//        file = SiteUtil.prepareFile("alfresco123");
//        testLockedFile = SiteUtil.prepareFile("Alfresco456");
//    }
//
//
//
//    @AfterClass(groups={"alfresco-one"})
//    public void teardown()
//    {
//        SiteUtil.deleteSite(drone, siteName);
//        
//        if (isHybridEnabled())
//        {
//            // go to profile
//            MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
//
//            // Click cloud sync
//            CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
//            if (cloudSyncPage.isDisconnectButtonDisplayed())
//            {
//                cloudSyncPage.disconnectCloudAccount().render();
//            }
//        }
//    }
//    /**
//     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
//     *
//     * @throws Exception
//     */
//    @Test(groups={"alfresco-one"})
//    public void createData() throws Exception
//    {
//        SitePage page = drone.getCurrentPage().render();
//        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
//        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
//        documentLibPage = uploadForm.uploadFile(testLockedFile.getCanonicalPath()).render();
//        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
//        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
//        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
//        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
//    }
//    
//    @Test(dependsOnMethods = "createData", groups={"alfresco-one"})
//    public void selectManageRules()
//    {
//     // Get folder
//        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
//        ManageRulesPage page = thisRow.selectManageRules().render();
//        Assert.assertNotNull(page);
//        drone.navigateTo(String.format(shareUrl + "/page/site/%s/documentlibrary",siteName));
//        documentLibPage = (DocumentLibraryPage) drone.getCurrentPage();
//        documentLibPage.render();
//    }
//    
//    
//    @Test(dependsOnMethods = "selectManageRules", groups={"alfresco-one"})
//    public void testContentCheckBoxForFolder() throws Exception
//    {
//        // Get folder row
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
//        String thisRowName = thisRow.getName();
//        Assert.assertEquals(thisRowName, folderName);
//        // Content CheckBox
//        Assert.assertFalse(thisRow.isCheckboxSelected());
//        thisRow.selectCheckbox();
//        Assert.assertTrue(thisRow.isCheckboxSelected());
//
//        // UnSelect
//        thisRow.selectCheckbox();
//        Assert.assertFalse(thisRow.isCheckboxSelected());
//    }
//
//    @Test(dependsOnMethods = "testContentCheckBoxForFolder", groups={"alfresco-one"})
//    public void testNodeRefForFolder() throws Exception
//    {
//        // Get folder
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
//
//        // NodeRef
//        Assert.assertNotNull(thisRow.getContentNodeRef(), "Node Reference is null");
//        logger.info("NodeRef:" + thisRow.getContentNodeRef());
//    }
//
//    @Test(dependsOnMethods = "testNodeRefForFolder", groups={"alfresco-one"})
//    public void testContentEditInfoForFolder() throws Exception
//    {
//        // Get folder
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
//
//        // Get ContentEditInfo
//        Assert.assertNotNull(thisRow.getContentEditInfo());
//        Assert.assertTrue(thisRow.getContentEditInfo().contains("Created"));
//    }
//
//    @Test(dependsOnMethods = "testContentEditInfoForFolder", groups={"alfresco-one"})
//    public void testLikeMethodsForFolder() throws Exception
//    {
//        // Get folder
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
//        // Like
//        Assert.assertFalse(thisRow.isLiked());
//        Assert.assertEquals(thisRow.getLikeCount(), "0");
//        thisRow.selectLike();
//    }
//
//    @Test(dependsOnMethods = "testLikeMethodsForFolder", groups={"alfresco-one"})
//    public void testFavouriteMethodsForFolder() throws Exception
//    {
//        documentLibPage = documentLibPage.render();
//        // Get folder
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
//
//        // Favourite
//        Assert.assertFalse(thisRow.isFavourite());
//        thisRow.selectFavourite();
//        Assert.assertTrue(thisRow.isFavourite());
//    }
//
//    @Test(dependsOnMethods = "testFavouriteMethodsForFolder", groups={"alfresco-one"})
//    public void testTagsForFolder() throws Exception
//    {
//        String tagName = "Folder Tag";
//        String tagName2 = "Folder Tag 2";
//
//        // Get folder
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
//        Assert.assertFalse(thisRow.hasTags());
//
//        thisRow.addTag(tagName);
//        thisRow.addTag(tagName2);
//
//        Assert.assertTrue(thisRow.getTags().contains(tagName.toLowerCase()));
//        Assert.assertTrue(thisRow.getTags().contains(tagName2.toLowerCase()));
//        Assert.assertEquals(thisRow.getTags().size(), 2);
//        Assert.assertFalse(thisRow.getTags().contains("No Tags"));
//    }
//
//    @Test(dependsOnMethods = "testTagsForFolder", expectedExceptions = UnsupportedOperationException.class, groups={"alfresco-one"})
//    public void testSelectDownloadForFolderWithExpection() throws Exception
//    {
//        // Get folder
//        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
//
//        if (thisRow.isFolder())
//        {
//            thisRow.selectDownload();
//        }
//    }
//
////    @Test(dependsOnMethods = "testSelectDownloadForFolderWithExpection")
////    public void testContentCheckBoxForFile() throws Exception
////    {
////        try
////        {
////            // Get File
//////            FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
////
////            // Content CheckBox
////// TODO Michael WD-26           Assert.assertTrue(thisRow.isCheckboxSelected());
////// TODO Michael WD-26          thisRow.selectCheckbox();
////// TODO Michael WD-26          Assert.assertFalse(thisRow.isCheckboxSelected());
////
////            //Select
////            thisRow.selectCheckbox();
//////            Assert.assertTrue(thisRow.isCheckboxSelected());
////        }
////        catch (Throwable e)
////        {
////            saveScreenShot("ShareContentRowTest.testContentCheckBoxForFile");
////            throw new Exception(e);
////        }
////    }
//
//    @Test(dependsOnMethods = "testSelectDownloadForFolderWithExpection", groups={"alfresco-one"})
//    public void testNodeRefForFile() throws Exception
//    {
//        // Get File
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//
//        // NodeRef
//        Assert.assertNotNull(thisRow.getContentNodeRef(), "Node Reference is null");
//        logger.info("NodeRef:" + thisRow.getContentNodeRef());
//    }
//
//    @Test(dependsOnMethods = "testNodeRefForFile", groups={"alfresco-one"})
//    public void testContentEditInfoForFile() throws Exception
//    {
//        // Get File
//        FileDirectoryInfo thisRow = getFile();
//
//        // Get ContentEditInfo
//        Assert.assertNotNull(thisRow.getContentEditInfo());
//        Assert.assertTrue(thisRow.getContentEditInfo().contains("Created"));
//    }
//
//    @Test(dependsOnMethods = "testContentEditInfoForFile", groups={"alfresco-one"})
//    public void testLikeMethodsForFile() throws Exception
//    {
//        // Get File
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//
//        // Like
//        Assert.assertFalse(thisRow.isLiked());
//        Assert.assertEquals(thisRow.getLikeCount(), "0");
//
//        thisRow.selectLike();
//
//        Assert.assertTrue(thisRow.isLiked());
//        Assert.assertEquals(thisRow.getLikeCount(), "1");
//    }
//
//    @Test(dependsOnMethods = "testLikeMethodsForFile", groups={"alfresco-one"})
//    public void testFavouriteMethodsForFile() throws Exception
//    {
//        documentLibPage = documentLibPage.render();
//        // Get File
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//
//        // Favorite
//        Assert.assertFalse(thisRow.isFavourite());
//        thisRow.selectFavourite();
//        Assert.assertTrue(thisRow.isFavourite());
//    }
//
//    @Test(dependsOnMethods = "testFavouriteMethodsForFile", groups={"alfresco-one"})
//    public void testTagsForFile() throws Exception
//    {
//        String tagName = "File Tag";
//        String tagName2 = "File Tag 2";
//        documentLibPage = documentLibPage.render();
//        // Get File
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//        Assert.assertFalse(thisRow.hasTags());
//
//        thisRow.addTag(tagName);
//        thisRow.addTag(tagName2);
//
//        Assert.assertTrue(thisRow.hasTags());
//        Assert.assertTrue(thisRow.getTags().contains(tagName.toLowerCase()));
//        Assert.assertTrue(thisRow.getTags().contains(tagName2.toLowerCase()));
//        Assert.assertEquals(thisRow.getTags().size(), 2);
//    }
//
//    @Test(dependsOnMethods = "testTagsForFile", groups={"alfresco-one"})
//    public void testSelectDownloadForFile() throws Exception
//    {
//        // Get File
//        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
//        thisRow.selectDownload();
//        Assert.assertNotNull(documentLibPage);
//    }
//
//    @Test(dependsOnMethods = "testSelectDownloadForFile", groups={"alfresco-one"})
//    public void testIsDeleteLinkPresent()
//    {
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//        assertTrue(thisRow.isDeletePresent());
//    }
//
//    @Test(dependsOnMethods = "testIsDeleteLinkPresent", groups={"alfresco-one"})
//    public void testSelectThumbnailForFile() throws Exception
//    {
//        // Get File
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//        SitePage sitePage = thisRow.selectThumbnail().render();
//        Assert.assertTrue(sitePage instanceof DocumentDetailsPage);
//    }
//
//    @Test(dependsOnMethods = "testSelectThumbnailForFile", groups={"alfresco-one"})
//    public void testSelectThumbnailForFolder() throws Exception
//    {
//        SitePage page = drone.getCurrentPage().render();
//        try
//        {
//            assertNotNull(page);
//            documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
//
//            // Get File
//            FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
//            SitePage sitePage = thisRow.selectThumbnail().render();
//            Assert.assertTrue(sitePage instanceof DocumentLibraryPage);
//        }
//        catch (Throwable e)
//        {
//            saveScreenShot("ShareContentRowTest.testSelectThumbnailForFile");
//            throw new Exception(e);
//        }
//        finally
//        {
//           page.getSiteNav().selectSiteDocumentLibrary();
//        }
//    }
//
//    @Test(dependsOnMethods = "testSelectThumbnailForFolder", groups = {  "Enterprise4.2" , "Cloud2"})
//    public void managePermissionTest()
//    {
//    	documentLibPage.render();
//        ManagePermissionsPage mangPermPage = (documentLibPage.getFileDirectoryInfo(folderName).selectManagePermission()).render();       
//        Assert.assertTrue(mangPermPage.isInheritPermissionEnabled());
//        documentLibPage = ((DocumentLibraryPage)mangPermPage.selectSave()).render();
//    }
//    @Test(dependsOnMethods = "managePermissionTest", groups = { "Enterprise4.2" })
//    public void testIsEditInGoogleDocsPresent()
//    {
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//        assertTrue(thisRow.isEditInGoogleDocsPresent());
//    }
//    
//    @Test(dependsOnMethods = "testIsEditInGoogleDocsPresent", expectedExceptions = PageOperationException.class, groups = { "Enterprise4.2" })
//    public void testSelectDownloadFolderAsZipForFile() throws Exception
//    {
//        // Get File
//        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
//        thisRow.selectDownloadFolderAsZip();
//    }
//
//
//    @Test(dependsOnMethods = "testSelectThumbnailForFolder", groups = "Enterprise4.2")
//    public void testSelectDownloadFolderAsZipForFolder() throws Exception
//    {
//        // Get folder
//        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(folderName);
//        thisRow.selectDownloadFolderAsZip();
//        drone.waitUntilElementDisappears(By.cssSelector("div[id*='archive-and-download'] a"), 2000);
//    }
//
//    /**
//     * Method renders the documentlibrary page and returns the file as FileDirectoryInfo
//     * @return FileDirectoryInfo element for file / row at index 1
//     * @throws Exception
//     */
//    private FileDirectoryInfo getFile() throws Exception
//    {
//        documentLibPage = drone.getCurrentPage().render();
//        List<FileDirectoryInfo> results = documentLibPage.getFiles();
//        if(results.isEmpty())
//        {
//            throw new Exception("Error getting file");
//        }
//        else
//        {
//            // Get file
//            return results.get(1);
//        }
//    }
//    
//   
//
//    @Test(dependsOnMethods = "testSelectDownloadFolderAsZipForFolder", groups = {"Hybrid" })
//    public void testSelectSyncToCloud() throws Exception
//    {
//        // Select SyncToCloud
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file.getName());
//        DestinationAndAssigneePage destinationAndAssigneePage = (DestinationAndAssigneePage) thisRow.selectSyncToCloud().render();
//        Assert.assertEquals(destinationAndAssigneePage.getSyncToCloudTitle(), "Sync " + file.getName() + " to The Cloud");
//        destinationAndAssigneePage.selectSubmitButtonToSync();
//        assertTrue("File should be synced", thisRow.isCloudSynced());
//    }
//
//
//    @Test(dependsOnMethods = "testSelectSyncToCloud", groups = { "Hybrid" })
//    public void selectInlineEdit()
//    {
//        InlineEditPage inlineEditPage = documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).selectInlineEdit().render();
//        EditTextDocumentPage editTextDocumentPage = (EditTextDocumentPage)inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT);
//        ContentDetails contentDetails = editTextDocumentPage.getDetails();
//        Assert.assertEquals(contentDetails.getName(), testLockedFile.getName());
//        documentLibPage = editTextDocumentPage.selectCancel().render();
//        documentLibPage.render();
//        /*inlineEditPage = documentLibPage.getFileDirectoryInfo(HTMLDocument).selectInlineEdit().render();
//        EditHtmlDocumentPage editHtmlDocumentPage = (EditHtmlDocumentPage)inlineEditPage.getInlineEditDocumentPage(MimeType.HTML);
//        Assert.assertTrue(editHtmlDocumentPage.isEditHtmlDocumentPage());
//        documentLibPage = ((DocumentLibraryPage)editHtmlDocumentPage.saveText()).render();*/
//    }
//
//    @Test(dependsOnMethods = "selectInlineEdit", groups = { "Hybrid" })
//    public void isLockedTest()
//    {
//        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isLocked(), "Verify the file is not locked");
//        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isInlineEditLinkPresent(), "Verify the Inline Edit option is displayed");
//        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isEditOfflineLinkPresent(), "Verify the Edit Offline option is displayed");
//        DestinationAndAssigneePage destinationAndAssigneePage = documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).selectSyncToCloud().render();
//        destinationAndAssigneePage.selectNetwork("premiernet.test");
//        Assert.assertFalse(destinationAndAssigneePage.isFolderDisplayed(String.valueOf(Math.random())));
//        Assert.assertTrue(destinationAndAssigneePage.isFolderDisplayed("Documents"));
//        destinationAndAssigneePage.selectFolder("Documents");
//        destinationAndAssigneePage.selectLockOnPremCopy();
//        DocumentLibraryPage documentLibraryPage = destinationAndAssigneePage.selectSubmitButtonToSync().render();
//        Assert.assertTrue(documentLibraryPage.getFileDirectoryInfo(testLockedFile.getName()).isLocked());
//        Assert.assertEquals(documentLibraryPage.getFileDirectoryInfo(testLockedFile.getName()).getContentInfo(), "This document is locked by you.");
//        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isInlineEditLinkPresent(), "Verify the Inline Edit option is NOT displayed");
//        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testLockedFile.getName()).isEditOfflineLinkPresent(), "Verify the Edit Offline option is NOT displayed");
//    }
//    
// 
//    
//}
