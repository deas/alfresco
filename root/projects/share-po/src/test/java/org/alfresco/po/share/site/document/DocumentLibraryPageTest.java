/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.webdrone.exception.PageException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups={"Firefox17Ent"})
public class DocumentLibraryPageTest extends AbstractDocumentTest
{
    private static final String NEW_TEST_FILENAME = "test.txt";
    private static String siteName;
    private static String folderName, folderName2, folderName3, folderNameDelete;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private File file1;
    private File file2;
    private String uname = "dlpt1user" + System.currentTimeMillis();
    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folderName2 = folderName + "-1";
        folderName3 = folderName + "-2";
        folderNameDelete = folderName + "delete";
        folderDescription = String.format("Description of %s", folderName);
        createEnterpriseUser(uname);
        loginAs(uname, UNAME_PASSWORD).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        file1 = SiteUtil.prepareFile();
        file2 = SiteUtil.prepareFile();
    }

    @AfterClass(groups="alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, groups="alfresco-one")
    public void getShareContentWithNull()
    {
        DocumentLibraryPage lib = new DocumentLibraryPage(drone);
        String t = null;
        lib.getFileDirectoryInfo(t);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class, dependsOnMethods = "getShareContentWithNull", groups="alfresco-one")
    public void getShareContentWithEmptyName()
    {
        DocumentLibraryPage lib = new DocumentLibraryPage(drone);
        lib.getFileDirectoryInfo("");
    }

    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "getShareContentWithEmptyName", groups="alfresco-one")
    public void createNewFolder() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        // documentLibPage = getDocumentLibraryPage(siteName).render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        Assert.assertEquals(files.size(), 0);
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
        documentLibPage = (documentLibPage.getNavigation().selectDetailedView()).render();
        
        files = documentLibPage.getFiles();
        FileDirectoryInfo folder = files.get(0);
        
        Assert.assertTrue(documentLibPage.paginatorRendered());
        Assert.assertEquals(files.size(), 1);
        Assert.assertEquals(folder.isTypeFolder(), true);
        Assert.assertEquals(folder.getName(), folderName);
        Assert.assertEquals(folder.getDescription(), folderDescription);
        Assert.assertNotNull(documentLibPage.getFileDirectoryInfo(folderName));
    }

    @Test(dependsOnMethods = "createNewFolder", groups="alfresco-one")
    public void uploadFile() throws Exception
    {
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        List<FileDirectoryInfo> results = documentLibPage.getFiles();
        Assert.assertEquals(results.size(), 2);
        Assert.assertNotNull(documentLibPage.getFileDirectoryInfo(file1.getName()));
    }

    @Test(dependsOnMethods = "uploadFile", groups="alfresco-one")
    public void editProperites()
    {
        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(1);
        Assert.assertEquals(fileInfo.getName(), file1.getName());
        Assert.assertTrue(fileInfo.isEditPropertiesLinkPresent());
        EditDocumentPropertiesPopup editPage = fileInfo.selectEditProperties().render();
        Assert.assertNotNull(editPage);
        editPage.setDescription("the description");
        editPage.setName(NEW_TEST_FILENAME);
        documentLibPage = editPage.selectSave().render();
    }

    @Test(dependsOnMethods = "editProperites", groups="alfresco-one")
    public void cancelEditProperties()
    {
        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(1);
        EditDocumentPropertiesPopup editPage = fileInfo.selectEditProperties().render();
        editPage.setDocumentTitle("hello world");
        editPage.setName("helloworld");
        documentLibPage = editPage.selectCancel().render();
        fileInfo = documentLibPage.getFiles().get(1);
        Assert.assertEquals(fileInfo.getName(), NEW_TEST_FILENAME);

    }

    @Test(dependsOnMethods = "cancelEditProperties", groups="alfresco-one")
    public void deleteFile()
    {
        documentLibPage = documentLibPage.deleteItem(NEW_TEST_FILENAME).render();
        Assert.assertEquals(documentLibPage.getFiles().size(), 1);
    }

    @Test(dependsOnMethods = "deleteFile", groups="alfresco-one")
    public void navigateToFolder() throws IOException
    {
        documentLibPage = documentLibPage.selectFolder(folderName).render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        Assert.assertEquals(files.size(), 0);
    }

    @Test(dependsOnMethods = "navigateToFolder", groups="alfresco-one")
    public void goToSubFolders() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.selectFolder(folderName).render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName2, folderDescription).render();
        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName3, folderDescription).render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        Assert.assertEquals(files.size(), 2);

        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.selectFolder(folderName).render();
        files = documentLibPage.getFiles();
        if (files.isEmpty())
            saveScreenShot("DocumentLibraryPageTest.goToSubFolders.empty");
        Assert.assertEquals(files.size(), 2);
        documentLibPage = documentLibPage.selectFolder(folderName2).render();
        files = documentLibPage.getFiles();
        Assert.assertEquals(files.size(), 0);
    }

    @Test(dependsOnMethods = "goToSubFolders", groups="alfresco-one")
    public void deleteFileOrFolder()
    {
        SitePage page = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = (DocumentLibraryPage) page.getSiteNav().selectSiteDocumentLibrary();
                    documentLibPage.render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        Assert.assertEquals(files.size(), 1);
        documentLibPage = (DocumentLibraryPage) documentLibPage.deleteItem(folderName);
        documentLibPage.render();
        files = documentLibPage.getFiles();
        Assert.assertEquals(files.size(), 0);
    }
    
    @Test(dependsOnMethods = "deleteFileOrFolder", groups="alfresco-one")
    public void createNewFolderWithTitle() throws Exception
    {
        String title = "TestingFolderTitle";
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        Assert.assertEquals(files.size(), 0);
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.createNewFolder(folderName, title, folderDescription).render();
        files = documentLibPage.getFiles();
        FileDirectoryInfo folder = files.get(0);

        Assert.assertEquals(files.size(), 1);
        Assert.assertTrue(folder.getTitle().length() > 0);
        Assert.assertTrue(folder.getTitle().contains(title));
    }
    
    @Test(dependsOnMethods = "createNewFolderWithTitle", groups="alfresco-one")
    public void selectDetailedView() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        int noOfFiles = documentLibPage.getFiles().size();
        documentLibPage = ((DocumentLibraryPage) documentLibPage.getNavigation().selectDetailedView()).render();
        Assert.assertNotNull(documentLibPage);
        Assert.assertEquals(documentLibPage.getFiles().size(), noOfFiles);
    }
    
    @Test(dependsOnMethods="selectDetailedView", groups="alfresco-one")
    public void createFolderSelectCancel() throws Exception
    {
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.selectCancel().render();
    }
    
    @Test
    public void testBadCaseOfPaginationRendered()
    {
        DocumentLibraryPage l = new DocumentLibraryPage(drone);
        Assert.assertFalse(l.paginatorRendered());
    }

    /**
     * Test to check the uploaded file is created succesful
     * @param - String 
     * @author sprasanna 
     * @throws Exception 
     */
    @Test(dependsOnMethods = "createFolderSelectCancel", groups="alfresco-one")
    public void isContentUploadedSucessfulTest() throws Exception
    {
        File file1 = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        int count = 0;
        while(true)
        {
            if(documentLibPage.isItemVisble(file1.getName())||count > 5)
            {
                break;
            }
            count++;
        }
        Assert.assertTrue(documentLibPage.isItemVisble(file1.getName()), "File is uploaded successfully");
    }
    
    @Test(dependsOnMethods="isContentUploadedSucessfulTest", groups="Enterprise4.2")
    public void isSelectedItemMenuVisible()
    {
        Assert.assertFalse(documentLibPage.getNavigation().isSelectedItemMenuVisible());
    }
    
    @Test(dependsOnMethods="isSelectedItemMenuVisible", groups="Enterprise4.2", expectedExceptions=PageException.class, expectedExceptionsMessageRegExp="Selected Items Button found, but is not enabled please select one or more item")
    public void clickSelectedItemsWithException()
    {
        drone.refresh();//refresh to unselect rows
        documentLibPage = drone.getCurrentPage().render();
        documentLibPage = documentLibPage.getNavigation().clickSelectedItems().render();
    }
    
    @Test(dependsOnMethods="clickSelectedItemsWithException", groups="Enterprise4.2")
    public void clickSelectedItems()
    {
        documentLibPage.getFileDirectoryInfo(folderName).selectCheckbox();
        documentLibPage = documentLibPage.getNavigation().clickSelectedItems().render();
        Assert.assertTrue(documentLibPage.getNavigation().isSelectedItemMenuVisible());
    }
    
    @Test(dependsOnMethods="clickSelectedItems", groups="Enterprise4.2")
    public void selectDownloadAsZip()
    {
        documentLibPage.getNavigation().selectDownloadAsZip();
    }

    @Test(dependsOnMethods="selectDownloadAsZip", groups="Enterprise4.2")
    public void selectAll() throws Exception
    {
        documentLibPage.render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().render().selectFileUpload().render();
        documentLibPage = (DocumentLibraryPage) uploadForm.uploadFile(file2.getCanonicalPath());
        documentLibPage.render();
        documentLibPage = documentLibPage.getNavigation().selectAll().render();
        List<FileDirectoryInfo> fileList = documentLibPage.getFiles();
        for(FileDirectoryInfo file: fileList)
        {
            Assert.assertTrue(file.isCheckboxSelected());
        }
    }
    @Test(dependsOnMethods="selectAll", groups="Enterprise4.2")
    public void deleteFolderFromNavigation() throws Exception
    {
        documentLibPage.render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderNameDelete, folderDescription).render();
        documentLibPage.getFileDirectoryInfo(folderNameDelete).selectCheckbox();
        ConfirmDeletePage deletePage= documentLibPage.getNavigation().render().selectDelete().render();
        deletePage.selectAction(Action.Delete).render();
    }
    
    @Test(dependsOnMethods="deleteFolderFromNavigation", groups="Enterprise4.2")
    public void selectCopyTo() throws Exception
    {
    	String copyToFolder = "copyFolder";
    	NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
    	documentLibPage = newFolderPage.createNewFolder(copyToFolder, copyToFolder).render();
    	documentLibPage.getFileDirectoryInfo(copyToFolder).selectCheckbox();    	
    	CopyOrMoveContentPage copyTo = documentLibPage.getNavigation().render().selectCopyTo().render();
    	documentLibPage = copyTo.selectOkButton().render();
  
    	for (FileDirectoryInfo dirInfo: documentLibPage.getFiles()  ) {
    		boolean isCopied = dirInfo.getName().contains("Copy of") ? true : false;
			if(isCopied)
			{
				Assert.assertTrue(dirInfo.getName().contains(copyToFolder));
			}
		}
        
    }
    
    
    @Test(dependsOnMethods="selectCopyTo", groups="Enterprise4.2")
    public void selectMoveTo() throws Exception
    {
        String moveToFolder = "moveFolder";
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(moveToFolder, moveToFolder).render();
        documentLibPage.getFileDirectoryInfo(moveToFolder).selectCheckbox();        
        CopyOrMoveContentPage copyTo = documentLibPage.getNavigation().render().selectMoveTo().render();
        documentLibPage = copyTo.selectOkButton().render();
  
        for (FileDirectoryInfo dirInfo: documentLibPage.getFiles()  ) {
            boolean isMoved = dirInfo.getName().contains("Move of") ? true : false;
            if(isMoved)
            {
                Assert.assertTrue(dirInfo.getName().contains(moveToFolder));
            }
        }
        
    }
    
    /**
     * test to upload new version 
     * @author sprasanna
     * @throws Exception
     */ 
    @Test(dependsOnMethods="selectMoveTo", groups="Enterprise4.2")
    public void selectUploadNewVersion() throws Exception
    {
        File tempFile = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        UpdateFilePage updateFilePage =  documentLibPage.getFileDirectoryInfo(tempFile.getName()).selectUploadNewVersion().render();
        updateFilePage.selectMajorVersionChange();
        updateFilePage.selectCancel();
        documentLibPage = drone.getCurrentPage().render();
        Assert.assertTrue(documentLibPage.getTitle().contains("Document Library"));
    } 
    
    /**
     * Select Delete and perform cancel and OK action 
     * @author sprasanna 
     * @throws Exception 
     * 
     */
    
    @Test(dependsOnMethods="selectUploadNewVersion", groups="Enterprise4.2")
    public void selectDeleteforContent() throws Exception
    {
        documentLibPage.render();
        FileDirectoryInfo file;
        ConfirmDeletePage confirmdialog;
        int fileSize = 0;
        File tempFile = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        file = documentLibPage.getFileDirectoryInfo(tempFile.getName());
        confirmdialog = file.selectDelete().render();
        documentLibPage = ((DocumentLibraryPage) confirmdialog.selectAction(Action.Cancel)).render();
        fileSize = documentLibPage.getFiles().size();
        Assert.assertTrue(documentLibPage.getTitle().contains("Document Library"));
        file = documentLibPage.getFileDirectoryInfo(tempFile.getName());
        confirmdialog = file.selectDelete().render();
        documentLibPage = ((DocumentLibraryPage) confirmdialog.selectAction(Action.Delete)).render();
        Assert.assertEquals(documentLibPage.getFiles().size(), fileSize-1);
    } 
    
    @Test( dependsOnMethods="selectDeleteforContent" , groups="Enterprise4.2")
    public void testTagsCount() throws IOException
    {
        String tagName = "tagcount";
        File tempFile = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        DocumentDetailsPage detailsPage = documentLibPage.selectFile(tempFile.getName()).render();
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        TagPage tagPage = propertiesPage.getTag().render();
        tagPage = tagPage.enterTagValue(tagName).render();
        propertiesPage = tagPage.clickOkButton().render();
        detailsPage = propertiesPage.selectSave().render();
        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(tagName).render();
        
        Assert.assertNotNull(documentLibPage);
        Assert.assertTrue(documentLibPage.getTagsCountUnderTagsTreeMenu(tagName) == 1);
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();
    }
    
    @Test( dependsOnMethods="testTagsCount" , groups="Enterprise4.2")
    public void testDocumentsTree() throws IOException
    {
        Assert.assertTrue(documentLibPage.isDocumentsTreeExpanded());
        documentLibPage.clickDocumentsTreeExpanded();
        Assert.assertFalse(documentLibPage.isDocumentsTreeExpanded());
        documentLibPage.clickDocumentsTreeExpanded();
    }
    
    @Test(dependsOnMethods="testDocumentsTree", groups="Enterprise4.2")
    public void selectStartWorkFlow() throws Exception
    {
        documentLibPage.render();
        File tempFile = SiteUtil.prepareFile();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(tempFile.getCanonicalPath()).render();
        
        if(documentLibPage.getFileDirectoryInfo("copyFolder").isCheckboxSelected())
        {
            documentLibPage.getFileDirectoryInfo("copyFolder").selectCheckbox();
        }
        
        if(!documentLibPage.getFileDirectoryInfo(tempFile.getName()).isCheckboxSelected())
        {
            documentLibPage.getFileDirectoryInfo(tempFile.getName()).selectCheckbox();
        }
        
        StartWorkFlowPage workFlowPage= documentLibPage.getNavigation().render().selectStartWorkFlow().render();
        
        Assert.assertNotNull(workFlowPage);
        Assert.assertTrue(workFlowPage.getTitle().contains("Start Workflow"));
    }
 }
    