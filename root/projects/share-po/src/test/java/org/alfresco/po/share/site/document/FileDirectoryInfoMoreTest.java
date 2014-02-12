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
//import static org.testng.AssertJUnit.assertTrue;
//
//import java.io.File;
//
//import org.alfresco.webdrone.WebDroneUtil;
//import org.alfresco.webdrone.share.SharePage;
//import org.alfresco.webdrone.share.site.UploadFilePage;
//import org.alfresco.webdrone.share.workflow.DestinationAndAssigneePage;
//import org.alfresco.webdrone.testng.listener.FailedTestListener;
//import org.alfresco.webdrone.util.SiteUtil;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Listeners;
//import org.testng.annotations.Test;
//
///**
// * Integration test to verify File Directory info methods are operating correctly.
// * 
// * @author Ranjith Manyam
// * @since 1.7
// */
//@Listeners(FailedTestListener.class)
//public class FileDirectoryInfoMoreTest extends AbstractDocumentTest
//{
//    private final Log logger = LogFactory.getLog(this.getClass());
//
//    private static String siteName;
//    private static String folderName;
//    @SuppressWarnings("unused")
//    private static String folderDescription;
//    private static DocumentLibraryPage documentLibPage;
//    private File testSyncFailedFile;
//    private File googleTestFile;
//
//    /**
//     * Pre test setup of a dummy file to upload.
//     * 
//     * @throws Exception
//     */
//    @SuppressWarnings("unused")
//    @BeforeClass(groups="alfresco-one")
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
//        drone.navigateTo(shareUrl);
//        SiteUtil.createSite(drone, siteName, "description", "Public");
//        testSyncFailedFile = SiteUtil.prepareFile("SyncFailFile");
//        googleTestFile = SiteUtil.prepareFile("googleTestFile");
//    }
//
//
//
//    @AfterClass(groups="alfresco-one")
//    public void teardown()
//    {
//        SiteUtil.deleteSite(drone, siteName);
//        disconnectCloudSync(drone);
//    }
//    /**
//     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
//     *
//     * @throws Exception
//     */
//    @Test(groups="alfresco-one")
//    public void createData() throws Exception
//    {
//        documentLibPage = openSiteDocumentLibraryFromSearch(drone, siteName);
//        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
//        documentLibPage = uploadForm.uploadFile(testSyncFailedFile.getCanonicalPath()).render();
//        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
//        documentLibPage = uploadForm.uploadFile(googleTestFile.getCanonicalPath()).render();
//    }
//    
//    @Test(dependsOnMethods = "createData", groups = { "Enterprise4.2" })
//    public void testSelectEditInGoogleDocsCloud() throws Exception
//    {
//        // Get File row
//        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(googleTestFile.getName());
//
//        GoogleDocsAuthorisation returnPage = thisRow.selectEditInGoogleDocs().render();
//        logger.info(returnPage.getClass() + "");
//        Assert.assertTrue(returnPage.isAuthorisationDisplayed());
//
//        GoogleSignUpPage signUpPage = returnPage.submitAuth().render();
//        Assert.assertTrue(signUpPage.isSignupWindowDisplayed());
//
//        EditInGoogleDocsPage googleDocsPage = signUpPage.signUp(googleusername, googlepassword).render();
//        assertTrue(googleDocsPage.isBrowserTitle("Google Docs Editor"));
//
//        googleDocsPage.selectDiscard().render().clickOkButton();
//        thisRow = documentLibPage.getFileDirectoryInfo(googleTestFile.getName());
//        SharePage returnedPage = thisRow.selectEditInGoogleDocs().render();
//        assertTrue("Returned page should be EditInGoogleDocsPage page.", (returnedPage instanceof EditInGoogleDocsPage));
//
//        ((EditInGoogleDocsPage) returnedPage).selectDiscard().render().clickOkButton().render();
//        assertTrue("Returned page should be EditInGoogleDocsPage page.", (drone.getCurrentPage() instanceof DocumentLibraryPage));
//    }
//
//    @Test(dependsOnMethods = "testSelectEditInGoogleDocsCloud", groups = {"Hybrid"})
//    public void isSyncToCloudLinkPresent()
//    {
//        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isSyncToCloudLinkPresent(), "Verifying \"Sync to Cloud\" link is present");
//    }
//
//
//    @Test(dependsOnMethods = "isSyncToCloudLinkPresent", groups = { "Hybrid" })
//    public void isSyncFailedIconPresent()
//    {
//        DestinationAndAssigneePage destinationAndAssigneePage = documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).selectSyncToCloud().render();
//        destinationAndAssigneePage.selectNetwork("premiernet.test");
//        destinationAndAssigneePage.render();
//        documentLibPage = (DocumentLibraryPage)destinationAndAssigneePage.selectSubmitButtonToSync();
//        documentLibPage.render();
//        // Verify the Sync Failed icon is not displayed
//        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isCloudSynced());
//        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isSyncFailedIconPresent(5000));
//        // Disconnect CloudSync
//        disconnectCloudSync(drone);
//        documentLibPage = openSiteDocumentLibraryFromSearch(drone, siteName);
//        DocumentDetailsPage detailsPage = documentLibPage.selectFile(testSyncFailedFile.getName()).render();
//        EditTextDocumentPage inlineEditPage = detailsPage.selectInlineEdit().render();
//        ContentDetails contentDetails = new ContentDetails();
//        contentDetails.setName(testSyncFailedFile.getName());
//        contentDetails.setDescription("isSyncFailedIconPresent test");
//        detailsPage = inlineEditPage.save(contentDetails).render();
//        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
//
//        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isSyncToCloudLinkPresent(), "Verifying \"Sync to Cloud\" link is NOT present");
//        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isRequestToSyncLinkPresent());
//        // Select Request to sync option from more options
//        documentLibPage = documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).selectRequestSync().render();
//        // Verify the Sync Failed icon is displayed
//        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testSyncFailedFile.getName()).isSyncFailedIconPresent(70000));
//    }
//
//}
