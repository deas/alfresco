package org.alfresco.po.share.site.document;
///**
// * 
// */
//package org.alfresco.webdrone.share.site.document;
//
//import java.io.File;
//import java.util.LinkedList;
//import java.util.List;
//
//import org.alfresco.webdrone.share.AbstractTest;
//import org.alfresco.webdrone.share.site.SitePage;
//import org.alfresco.webdrone.share.site.UploadFilePage;
//import org.alfresco.webdrone.testng.listener.FailedTestListener;
//import org.alfresco.webdrone.util.SiteUtil;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Listeners;
//import org.testng.annotations.Test;
//
///**
// * Integration test to verify CopyContent page elements are in place.
// * 
// * @author Chiran
// * @since 1.7.0
// */
//@Listeners(FailedTestListener.class)
//@Test(groups={"alfresco-one"})
//public class CopyOrMoveContentPageTest extends AbstractTest
//{
//    private String siteName1;
//    private File file1;
//    private File file2;
//    private DocumentLibraryPage documentLibPage;
//    private CopyOrMoveContentPage copyOrMoveContentPage;
//    private String folder1 = "CopyFolder";
//    private String folder2 = "moveFolder";
//
//    List<String> destinations = new LinkedList<String>();
//    List<String> sites = new LinkedList<String>();
//    List<String> folders = new LinkedList<String>();
//
//    /**
//     * Test process of accessing CopyContent page.
//     * 
//     * @throws Exception
//     */
//    @SuppressWarnings("unused")
//    @BeforeClass
//    private void setUp() throws Exception
//    {
//        siteName1 = "Site-1" + System.currentTimeMillis();
//
//        file1 = SiteUtil.prepareFile("File-1" + System.currentTimeMillis());
//        file2 = SiteUtil.prepareFile("File-2" + System.currentTimeMillis());
//
//        loginAs(username, password);
//
//        SiteUtil.createSite(drone, siteName1, "Public");
//
//        SitePage sitePage = (SitePage) drone.getCurrentPage();
//        sitePage.render();
//
//        documentLibPage = sitePage.getSiteNav().selectSiteDocumentLibrary().render();
//
//        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
//        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
//        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
//        documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();
//
//        documentLibPage.getNavigation().selectCreateNewFolder().render().createNewFolder(folder1);
//        documentLibPage.render();
//        documentLibPage.getNavigation().selectCreateNewFolder().render().createNewFolder(folder2);
//        documentLibPage.render();
//
//        documentLibPage = (DocumentLibraryPage) documentLibPage.getSiteNav().selectSiteDocumentLibrary();
//        documentLibPage.render();
//        
//        copyOrMoveContentPage = documentLibPage.getFileDirectoryInfo(file1.getName()).selectCopyTo().render();
//    }
//
//    @AfterClass
//    public void tearDown()
//    {
//        SiteUtil.deleteSite(drone, siteName1);
//        logout(drone);
//    }
//
//    @Test
//    public void isCopyContentPage() throws Exception
//    {
//        Assert.assertNotEquals(copyOrMoveContentPage.getDialogTitle(), "");
//    }
//
//    @Test(dependsOnMethods = "isCopyContentPage")
//    public void testGetDestinations() throws Exception
//    {
//        destinations = copyOrMoveContentPage.getDestinations();
//        Assert.assertNotNull(destinations);
//        Assert.assertTrue(destinations.size() > 0);
//    }
//
//    @Test(dependsOnMethods = "testGetDestinations")
//    public void testGetSites() throws Exception
//    {
//        sites = copyOrMoveContentPage.getSites();
//        Assert.assertNotNull(sites);
//        Assert.assertTrue(sites.size() > 0);
//        Assert.assertTrue(sites.contains(siteName1));
//    }
//
//    @Test(dependsOnMethods = "testGetSites")
//    public void testGetFolders() throws Exception
//    {
//        folders = copyOrMoveContentPage.getFolders();
//        Assert.assertNotNull(folders);
//        Assert.assertTrue(folders.contains(folder1));
//        Assert.assertTrue(folders.contains(folder2));
//    }
//
//    @Test(dependsOnMethods = "testGetFolders")
//    public void testSelectDestination() throws Exception
//    {
//        copyOrMoveContentPage = copyOrMoveContentPage.selectDestination(destinations.get(0)).render();
//        Assert.assertNotNull(copyOrMoveContentPage);
//    }
//
//    @Test(dependsOnMethods = "testSelectDestination", expectedExceptions = { IllegalArgumentException.class })
//    public void testSelectDestinationWithNull() throws Exception
//    {
//        copyOrMoveContentPage = copyOrMoveContentPage.selectDestination(null);
//    }
//
//    @Test(dependsOnMethods = "testSelectDestinationWithNull")
//    public void testSelectSite() throws Exception
//    {
//        copyOrMoveContentPage = copyOrMoveContentPage.selectSite(siteName1).render();
//        Assert.assertNotNull(copyOrMoveContentPage);
//    }
//
//    @Test(dependsOnMethods = "testSelectSite", expectedExceptions = { IllegalArgumentException.class })
//    public void testSelectSiteWithNull() throws Exception
//    {
//        copyOrMoveContentPage = copyOrMoveContentPage.selectSite(null);
//    }
//    
//    @Test(dependsOnMethods = "testSelectSiteWithNull", expectedExceptions = { IllegalArgumentException.class })
//    public void testSelectFolderWithNull() throws Exception
//    {
//        copyOrMoveContentPage.selectFolder(null);
//    }
//    
//    @Test(dependsOnMethods = "testSelectFolderWithNull")
//    public void testCloseAndCancelDialog() throws Exception
//    {
//        copyOrMoveContentPage.selectCloseButton();
//        copyOrMoveContentPage = documentLibPage.getFileDirectoryInfo(file1.getName()).selectCopyTo().render();
//        Assert.assertNotNull(copyOrMoveContentPage);
//        copyOrMoveContentPage.selectCancelButton();
//        copyOrMoveContentPage = documentLibPage.getFileDirectoryInfo(file1.getName()).selectCopyTo().render();
//        Assert.assertNotNull(copyOrMoveContentPage);
//    }
//    
//    @Test(dependsOnMethods = "testCloseAndCancelDialog")
//    public void testCopyToFolder() throws Exception
//    {
//
//        copyOrMoveContentPage = selectDestination(copyOrMoveContentPage, destinations);
//        Assert.assertNotNull(copyOrMoveContentPage);
//        copyOrMoveContentPage = copyOrMoveContentPage.selectSite(siteName1).render();
//        Assert.assertNotNull(copyOrMoveContentPage);
//        copyOrMoveContentPage = copyOrMoveContentPage.selectFolder(folders.get(0),folder1).render();
//        Assert.assertNotNull(copyOrMoveContentPage);
//
//        documentLibPage = copyOrMoveContentPage.selectOkButton().render();
//        
//        Assert.assertNotNull(documentLibPage);
//        Assert.assertTrue(documentLibPage.getFiles().size() == 4);
//        
//        documentLibPage = documentLibPage.selectFolder(folder1).render();
//        
//        Assert.assertTrue(documentLibPage.isFileVisible(file1.getName()));
//    }
//    
//    @Test(dependsOnMethods = "testCopyToFolder")
//    public void testMoveToFolder() throws Exception
//    {
//        documentLibPage = (DocumentLibraryPage) documentLibPage.getSiteNav().selectSiteDocumentLibrary();
//        documentLibPage.render();
//        
//        Assert.assertNotNull(documentLibPage);
//        Assert.assertTrue(documentLibPage.getFiles().size() == 4);
//        
//        copyOrMoveContentPage = documentLibPage.getFileDirectoryInfo(file2.getName()).selectMoveTo().render();
//        copyOrMoveContentPage = selectDestination(copyOrMoveContentPage, destinations);
//        Assert.assertNotNull(copyOrMoveContentPage);
//        copyOrMoveContentPage = copyOrMoveContentPage.selectSite(siteName1).render();
//        Assert.assertNotNull(copyOrMoveContentPage);
//        copyOrMoveContentPage = copyOrMoveContentPage.selectFolder(folders.get(0),folder2).render();
//        Assert.assertNotNull(copyOrMoveContentPage);
//
//        documentLibPage = copyOrMoveContentPage.selectOkButton().render();
//        
//        Assert.assertNotNull(documentLibPage);
//        Assert.assertTrue(documentLibPage.getFiles().size() == 3);
//        Assert.assertFalse(documentLibPage.isFileVisible(file2.getName()));
//        
//        documentLibPage = documentLibPage.selectFolder(folder2).render();
//        
//        Assert.assertTrue(documentLibPage.isFileVisible(file2.getName()));
//    }
//    
//    private CopyOrMoveContentPage selectDestination(CopyOrMoveContentPage copyOrMoveContentPage, List<String> destinations)
//    {
//        for(String destination : destinations)
//        {
//            if(destination.equalsIgnoreCase("All Sites"))
//            {
//                return copyOrMoveContentPage.selectDestination(destination).render();
//            }
//        }
//        
//        return copyOrMoveContentPage.selectDestination(destinations.get(0)).render();
//    }
//}
