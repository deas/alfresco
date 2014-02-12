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
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.util.FailedTestListener;
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
@Test(groups={"Repository"})
@Listeners(FailedTestListener.class)
public class RepositoryPageTest extends AbstractDocumentTest
{
    private static final String MY_FOLDER = "my folder";
    private File sampleFile;

    @BeforeClass
    public void createSite()throws Exception
    {
        getWebDrone();
        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        sampleFile = SiteUtil.prepareFile();
    }

    @AfterClass(alwaysRun=true)
    public void deleteSite()
    {
        closeWebDrone();
    }

    @Test
    public void navigateToRepository() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        RepositoryPage repositoryPage = page.getNav().selectRepository().render();
        List<FileDirectoryInfo> files = repositoryPage.getFiles();
        Assert.assertTrue(repositoryPage.isSubFolderDocLib("Repository"));
        Assert.assertTrue(files.size() > 0);
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
        Assert.assertEquals(folder.getDescription(),"my test folder");
    }
    
    @Test(dependsOnMethods="createFolder")
    public void uploadFile() throws Exception
    {
        RepositoryPage repositoryPage = drone.getCurrentPage().render();
        UploadFilePage uploadForm = repositoryPage.getNavigation().selectFileUpload();
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

    @Test(dependsOnMethods="selectFolderByName")
    public void createInSubFolder()
    {
        RepositoryPage repoPage = drone.getCurrentPage().render();
        NewFolderPage form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder("test").render();
        FileDirectoryInfo folder = getItem(repoPage.getFiles(), "test");
        Assert.assertNotNull(folder);
        Assert.assertEquals(folder.getName(),"test");
    }
    
    @Test(dependsOnMethods="createInSubFolder")
    public void uploadInSubFolder() throws Exception
    {
        RepositoryPage repositoryPage = drone.getCurrentPage().render();
        UploadFilePage uploadForm = repositoryPage.getNavigation().selectFileUpload();
        repositoryPage = uploadForm.uploadFile(sampleFile.getCanonicalPath()).render();
        FileDirectoryInfo file = getItem(repositoryPage.getFiles(), sampleFile.getName());
        Assert.assertNotNull(file);
        Assert.assertEquals(file.getName(), sampleFile.getName());
    }
    
    @Test(dependsOnMethods="uploadInSubFolder")
    public void delete()
    {
        SharePage page = drone.getCurrentPage().render();
        RepositoryPage repositoryPage = page.getNav().selectRepository().render();
        repositoryPage = repositoryPage.deleteItem(MY_FOLDER).render();
        FileDirectoryInfo folder = getItem(repositoryPage.getFiles(), MY_FOLDER);
        Assert.assertNull(folder);
        
        repositoryPage = repositoryPage.deleteItem(sampleFile.getName()).render();
        FileDirectoryInfo file = getItem(repositoryPage.getFiles(), sampleFile.getName());
        Assert.assertNull(file);
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
}
