/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify repository page is operating correctly.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */

@Test(groups={"Repository", "Enterprise4.2"})
@Listeners(FailedTestListener.class)
public class RepositoryPageTest extends AbstractDocumentTest
{
    private static Log logger = LogFactory.getLog(RepositoryPageTest.class);
    private static final String MY_FOLDER = "aa--" + System.currentTimeMillis();
    private File sampleFile;
    private String contentName = "Test" + System.currentTimeMillis();
    private String userName = "RepositoryPageTest" + System.currentTimeMillis() + "@test.com";
    private String firstName = userName;
    private String lastName = userName;
    
    @BeforeClass(groups={"Repository", "Enterprise4.2"})
    public void createSite()throws Exception
    {
        if (!alfrescoVersion.isCloud())
        {
            DashBoardPage dashBoard = loginAs(username, password);
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            newPage.createEnterpriseUserWithGroup(userName, firstName, lastName, userName, userName, "ALFRESCO_ADMINISTRATORS");
            UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
            userPage.searchFor(userName).render();
            Assert.assertTrue(userPage.hasResults());
            logout(drone);
            loginAs(userName, userName);
        }
        else
        {
            loginAs(cloudUserName, cloudUserPassword);
        }
        sampleFile = SiteUtil.prepareFile("ab--" + System.currentTimeMillis());
        logger.info("===completed create site");
    }

    @AfterClass(groups={"Repository", "Enterprise4.2"})
    public void deleteSite()
    {
        closeWebDrone();
    }

    @Test
    public void navigateToRepository() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        RepositoryPage repositoryPage = page.getNav().selectRepository().render();
        repositoryPage = repositoryPage.getNavigation().selectDetailedView().render();
        List<FileDirectoryInfo> files = repositoryPage.getFiles();
        Assert.assertTrue(files.size() > 0);
        Assert.assertTrue(repositoryPage.getTitle().contains("Repository Browser"));
    }
    
    @Test(dependsOnMethods="navigateToRepository")
    public void createFolder()
    {
        RepositoryPage repositoryPage = drone.getCurrentPage().render();
        NewFolderPage form = repositoryPage.getNavigation().selectCreateNewFolder();
        repositoryPage = form.createNewFolder(MY_FOLDER, "my test folder").render();
        Assert.assertNotNull(repositoryPage);
        FileDirectoryInfo folder = getItem(repositoryPage.getFiles(), MY_FOLDER);
        Assert.assertNotNull(folder);
        Assert.assertEquals(folder.getName(),MY_FOLDER);
//        Assert.assertEquals(folder.getDescription(),"my test folder");
    }
    
    @Test(dependsOnMethods="createFolder")
    public void navigateToParentFolderTest()
    {
        RepositoryPage repositoryPage = drone.getCurrentPage().render();
        FolderDetailsPage detailsPage = repositoryPage.getFileDirectoryInfo(MY_FOLDER).selectViewFolderDetails().render();
        repositoryPage = detailsPage.navigateToParentFolder().render();
        Assert.assertTrue(repositoryPage.isFileVisible(MY_FOLDER));
    }
    
    @Test(dependsOnMethods="navigateToParentFolderTest")
    public void uploadFile() throws Exception
    {
        RepositoryPage repositoryPage = drone.getCurrentPage().render();
        UploadFilePage uploadForm = repositoryPage.getNavigation().selectFileUpload().render();
        repositoryPage = uploadForm.uploadFile(sampleFile.getCanonicalPath()).render();
        FileDirectoryInfo file = getItem(repositoryPage.getFiles(), sampleFile.getName());
        Assert.assertNotNull(file);
        Assert.assertEquals(file.getName(), sampleFile.getName());
    }
    

    @Test(dependsOnMethods="uploadFile")
    public void selectFolderByName()
    {
        RepositoryPage repositoryPage = drone.getCurrentPage().render();
        DocumentLibraryPage libPage = repositoryPage.selectFolder(MY_FOLDER).render();
        Assert.assertFalse(libPage.hasFiles());
    }
    
  //TODO Disbaled due to defect in prodct JIRA issue: ALF-20814
    @Test(dependsOnMethods="selectFolderByName", enabled=false)
    public void copyFolderTest()
    {
        FileDirectoryInfo info = null;
        boolean Results = false;
        String copyFolder = "Copy Folder" + System.currentTimeMillis();
        String toFolderCopied = "Folder to be Copied" + System.currentTimeMillis();
        RepositoryPage repoPage = drone.getCurrentPage().render();
        NewFolderPage form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder(copyFolder).render();
        form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder(toFolderCopied).render();
        info = repoPage.getFileDirectoryInfo(toFolderCopied);
        CopyOrMoveContentPage copyOrMoveContentPage = info.selectCopyTo().render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath(MY_FOLDER, copyFolder).render();
        repoPage = copyOrMoveContentPage.selectOkButton().render();
        repoPage.selectFolder(copyFolder);
        List<FileDirectoryInfo> files = repoPage.getFiles();
        for(FileDirectoryInfo fileList : files)
        {
            if(fileList.getName().equalsIgnoreCase(toFolderCopied))
            {
                 Results = true;
            }
        }
        Assert.assertTrue(Results);
    }

  //TODO Disbaled due to defect in prodct JIRA issue: ALF-20814
    @Test(dependsOnMethods="selectFolderByName", enabled=false)
    public void copyFolderTestNegativeCase()
    {
        CopyOrMoveContentPage copyOrMoveContentPage = null;
      try
      {
        FileDirectoryInfo info = null;
        String copyFolder = "Copy Folder" + System.currentTimeMillis();
        String toFolderCopied = "Folder to be Copied" + System.currentTimeMillis();
        RepositoryPage repoPage = drone.getCurrentPage().render();
        NewFolderPage form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder(copyFolder).render();
        form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder(toFolderCopied).render();
        info = repoPage.getFileDirectoryInfo(toFolderCopied);
        copyOrMoveContentPage = info.selectCopyTo().render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath(copyFolder, copyFolder + "1").render();
      }
      catch (PageOperationException pe)
      {
          copyOrMoveContentPage.selectCancelButton().render();
      }
    }
    
    @Test(dependsOnMethods="selectFolderByName", enabled = false)
    public void createInSubFolder()
    {
        RepositoryPage repoPage = drone.getCurrentPage().render();
        NewFolderPage form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder("test").render();
        FileDirectoryInfo folder = getItem(repoPage.getFiles(), "test");
        Assert.assertNotNull(folder);
        Assert.assertEquals(folder.getName(),"test");
    }
    
    @Test(dependsOnMethods="createInSubFolder", enabled = false)
    public void uploadInSubFolder() throws Exception
    {
        RepositoryPage repositoryPage = drone.getCurrentPage().render();
        UploadFilePage uploadForm = repositoryPage.getNavigation().selectFileUpload().render();
        repositoryPage = uploadForm.uploadFile(sampleFile.getCanonicalPath()).render();
        FileDirectoryInfo file = getItem(repositoryPage.getFiles(), sampleFile.getName());
        Assert.assertNotNull(file);
        Assert.assertEquals(file.getName(), sampleFile.getName());
    }
    
    @Test(dependsOnMethods="uploadInSubFolder", enabled = false)
    public void delete()
    {
        SharePage page = drone.getCurrentPage().render();
        RepositoryPage repositoryPage = page.getNav().selectRepository().render();
        repositoryPage = repositoryPage.getFileDirectoryInfo(MY_FOLDER).delete().render();
        FileDirectoryInfo folder = getItem(repositoryPage.getFiles(), MY_FOLDER);
        Assert.assertNull(folder);
    }
    private  FileDirectoryInfo getItem(List<FileDirectoryInfo> items, final String name)
    {
        for(FileDirectoryInfo item : items)
        {
            if(name.equalsIgnoreCase(item.getName()))
            {
                return item;
            }
        }
        return null;
    }
    /**
     * create content in repository 
     * @author sprasanna 
     */
    
    @Test(dependsOnMethods="delete", enabled = false)
    public void createContent()
    {
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(contentName);
        contentDetails.setContent("test");
        SharePage sharePage = drone.getCurrentPage().render();
        RepositoryPage page = sharePage.getNav().selectRepository().render();
        CreatePlainTextContentPage contentPage = page.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        DocumentDetailsPage detailsPage = contentPage.create(contentDetails).render();
        RepositoryPage repositoryPage = detailsPage.navigateToFolderInRepositoryPage().render();
        FileDirectoryInfo file = getItem(repositoryPage.getFiles(), contentName);
        Assert.assertTrue(file.getName().equalsIgnoreCase(contentName));            
    }
 
    /**
     * Select the action of manage Aspects
     * @author sprasanna
     */
    
    @Test(
    dependsOnMethods="createContent", enabled = false)
    public void selectMangeAspectTest()
    {
        SharePage page = drone.getCurrentPage().render();
        RepositoryPage repositoryPage = page.getNav().selectRepository().render();
        FileDirectoryInfo file = getItem(repositoryPage.getFiles(), "Data Dictionary");
        SelectAspectsPage selectAspectPage = file.selectManageAspects().render();
        Assert.assertNotNull(selectAspectPage);
    }
}