/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.TreeMenuNavigation.DocumentsMenu;
import org.alfresco.po.share.site.document.TreeMenuNavigation.TreeMenu;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library left hand menu trees are
 * operating correctly.
 * 
 * @author Jamie Allison
 * @since 4.3.0
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })
public class TreeMenuNavigationTest extends AbstractDocumentTest
{
    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    
    private File file1;
    private File file2;
    private String folderName1 = "folder1";
    private String folderName2 = "folder2";
    private String userName;
    private String tagName;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups = "alfresco-one")
    private void prepare() throws Exception
    {
        siteName = "TreeMenuNavigation" + System.currentTimeMillis();
        userName = siteName;
        tagName = "tag" + System.currentTimeMillis();

        AlfrescoVersion version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
        }
        else
        {
            createEnterpriseUser(userName);

            ShareUtil.loginAs(drone, shareUrl, userName, UNAME_PASSWORD).render();
        }

        SiteUtil.createSite(drone, siteName, "description", "Public");
        file1 = SiteUtil.prepareFile();
        file2 = SiteUtil.prepareFile();

        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName1).render();

        documentLibPage = documentLibPage.selectFolder(folderName1).render();

        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName2).render();

        documentLibPage = documentLibPage.selectFolder(folderName2).render();

        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();

        documentLibPage.getFileDirectoryInfo(file1.getName()).selectFavourite();
        documentLibPage.getFileDirectoryInfo(file2.getName()).addTag(tagName);
    }

    @AfterClass(groups = "alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(groups = "alfresco-one")
    public void isMenuTreeVisible() throws Exception
    {
        TreeMenuNavigation treeMenuNav = documentLibPage.getLeftMenus();

        assertTrue(treeMenuNav.isMenuTreeVisible(TreeMenu.DOCUMENTS));
        assertTrue(treeMenuNav.isMenuTreeVisible(TreeMenu.LIBRARY));
        assertTrue(treeMenuNav.isMenuTreeVisible(TreeMenu.CATEGORIES));
        assertTrue(treeMenuNav.isMenuTreeVisible(TreeMenu.TAGS));
    }

    @Test(dependsOnMethods = "isMenuTreeVisible", groups = "alfresco-one")
    public void isDocumentNodeVisible() throws Exception
    {
        TreeMenuNavigation treeMenuNav = documentLibPage.getLeftMenus();

        assertTrue(treeMenuNav.isDocumentNodeVisible(DocumentsMenu.ALL_DOCUMENTS));
        assertTrue(treeMenuNav.isDocumentNodeVisible(DocumentsMenu.IM_EDITING));
        assertTrue(treeMenuNav.isDocumentNodeVisible(DocumentsMenu.OTHERS_EDITING));
        assertTrue(treeMenuNav.isDocumentNodeVisible(DocumentsMenu.RECENTLY_MODIFIED));
        assertTrue(treeMenuNav.isDocumentNodeVisible(DocumentsMenu.RECENTLY_ADDED));
    }

    @Test(dependsOnMethods = "isDocumentNodeVisible", groups = "alfresco-one")
    public void selectDocumentNode() throws Exception
    {
        TreeMenuNavigation treeMenuNav = documentLibPage.getLeftMenus();

        int retry = 5;
        do
        {
            documentLibPage = treeMenuNav.selectDocumentNode(DocumentsMenu.MY_FAVORITES).render();
            try
            {
                documentLibPage.renderItem(3000, file1.getName());
                break;
            }
            catch (PageRenderTimeException error)
            {
            }
            
            retry--;
        } while(retry > 0);
        assertTrue(documentLibPage.getFiles().size() == 1);
        assertEquals(documentLibPage.getFiles().get(0).getName(), file1.getName());
    }

    @Test(dependsOnMethods = "selectDocumentNode", groups = "alfresco-one")
    public void selectNode() throws Exception
    {
        TreeMenuNavigation treeMenuNav = documentLibPage.getLeftMenus();

        documentLibPage = treeMenuNav.selectNode(TreeMenu.LIBRARY, folderName1, folderName2).render();
        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        assertTrue(files.size() == 2);
    }

    @Test(dependsOnMethods = "selectNode", groups = "alfresco-one")
    public void getNodeChildren() throws Exception
    {
        TreeMenuNavigation treeMenuNav = documentLibPage.getLeftMenus();

        List<String> children = treeMenuNav.getNodeChildren(
                    TreeMenu.CATEGORIES, "Category Root", "Languages", "English");

        assertTrue(children.size() == 5);
        assertTrue(children.contains("American English"));
        assertTrue(children.contains("Australian English"));
        assertTrue(children.contains("British English"));
        assertTrue(children.contains("Canadian English"));
        assertTrue(children.contains("Indian English"));
    }

    @Test(dependsOnMethods = "getNodeChildren", groups = "alfresco-one")
    public void selectTagNode() throws Exception
    {
        TreeMenuNavigation treeMenuNav = documentLibPage.getLeftMenus();

        documentLibPage = treeMenuNav.selectTagNode(tagName).render();
        
        List<FileDirectoryInfo> files = documentLibPage.getFiles();

        assertTrue(files.size() == 1);
        assertEquals(files.get(0).getName(), file2.getName());
    }

    @Test(dependsOnMethods = "selectTagNode", groups = "alfresco-one")
    public void getTagCount() throws Exception
    {
        TreeMenuNavigation treeMenuNav = documentLibPage.getLeftMenus();

        int tagCount = treeMenuNav.getTagCount(tagName);

        assertEquals(tagCount, 1);
    }
}
