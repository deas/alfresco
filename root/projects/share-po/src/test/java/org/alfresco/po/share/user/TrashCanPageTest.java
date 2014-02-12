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

package org.alfresco.po.share.user;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * TrashCanPage Test
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */
@Listeners(FailedTestListener.class)
public class TrashCanPageTest extends AbstractTest
{
    private String siteName;
    private String fileName1;
    private String fileName2;
    private String fileName3;
    DocumentLibraryPage docPage;
    DashBoardPage dashBoard;
    SiteDashboardPage site;
    MyProfilePage myprofile;
    TrashCanPage trashCan;

    /**
     * Pre test to create a content with properties set.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unused")
    @BeforeClass(groups = { "Enterprise4.2" })
    private void prepare() throws Exception
    {
        siteName = "TrashCanTest" + System.currentTimeMillis();
        File file0 = SiteUtil.prepareFile("file1.txt");
        fileName1 = file0.getName();
        File file1 = SiteUtil.prepareFile("file2.txt");
        fileName2 = file1.getName();
        File file2 = SiteUtil.prepareFile("file3.txt");
        fileName3 = file2.getName();
        loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "Public");
        SitePage site = drone.getCurrentPage().render();
        docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload();
        upLoadPage.uploadFile(file0.getCanonicalPath()).render();
        upLoadPage = docPage.getNavigation().selectFileUpload();
        upLoadPage.uploadFile(file1.getCanonicalPath()).render();
        upLoadPage = docPage.getNavigation().selectFileUpload();
        upLoadPage.uploadFile(file2.getCanonicalPath()).render();
        docPage = drone.getCurrentPage().render();
        docPage = docPage.deleteItem(fileName1).render();
        docPage = docPage.deleteItem(fileName2).render(); 
        docPage = docPage.deleteItem(fileName3).render(); 
   }

    @AfterClass(groups = { "Enterprise4.2" })
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    /**
     * Test to Check from My profile Page trashCan Page can be accessed
     */

    @Test(groups = { "Enterprise4.2" })
    public void testTrashCanDisplayed()
    {
        dashBoard = docPage.getNav().selectMyDashBoard().render();
        myprofile = dashBoard.getNav().selectMyProfile().render();
        trashCan = myprofile.getProfileNav().selectTrashCan().render();
        Assert.assertTrue(trashCan.getPageTitle().equalsIgnoreCase("User Trashcan"));
    }
    
    /**
    * Test to Check searching in trashcan
    */
    
    @Test(dependsOnMethods = "testTrashCanDisplayed",groups = { "Enterprise4.2" })
    public void testTrashCanSearch()
    {
        boolean results = false;
        trashCan = (TrashCanPage) trashCan.itemSearch("file").render();
        Assert.assertTrue(trashCan.hasTrashCanItems());
        List<String> trashCanItem = new ArrayList<String>();
        trashCanItem = trashCan.getSearchResults();
        for (String searchTerm : trashCanItem)
        {
            if (searchTerm.contains("file")) results = true;
        }
        Assert.assertTrue(results);
    }
    
    /**
     * Test to Check searching in trashcan
     */
    
    @Test(dependsOnMethods = "testTrashCanSearch",groups = { "Enterprise4.2" })
    public void testTrashCanSelectCheckBox()
    {
        trashCan = (TrashCanPage) trashCan.selectTrashCanItem(fileName1).render();
        Assert.assertTrue(trashCan.isCheckBoxSelected(fileName1));
    }
    
    /**
     * Test to Recover an item trashcan
     */

    @Test(dependsOnMethods = "testTrashCanSelectCheckBox",groups = { "Enterprise4.2" })
    public void testTrashCanRecover()
    {
        trashCan = (TrashCanPage) trashCan.selectTrashCanAction(fileName1, "Recover").render();
        Assert.assertTrue(trashCan.getPageTitle().equalsIgnoreCase("User Trashcan"));

    }
    
    /**
     * Test to Delete an item trashcan
     */
    @Test(dependsOnMethods = "testTrashCanRecover",groups = { "Enterprise4.2" })
    public void testTrashCanDelete()
    {
        TrashCanDeleteConfirmationDialogPage trashCanDeleteDialog = trashCan.selectTrashCanAction(fileName2, "Delete").render();
        Assert.assertTrue(trashCanDeleteDialog.isConfirmationDialogDisplayed());
        trashCan = trashCanDeleteDialog.clickOkButton().render();
        Assert.assertTrue(trashCan.getPageTitle().equalsIgnoreCase("User Trashcan"));
    }
    
    /**
     * Test to Clear the search entry trashcan
     */
    @Test(dependsOnMethods = "testTrashCanDelete",groups = { "Enterprise4.2" })
    public void testTrashClear()
    {
        trashCan = trashCan.clearSearch().render();
        Assert.assertTrue(trashCan.getPageTitle().equalsIgnoreCase("User Trashcan"));
    }
    
    /**
     * Test to Empty trashcan
     */
    @Test(dependsOnMethods = "testTrashClear",groups = { "Enterprise4.2" })
    public void testTrashCanEmpty()
    {
        TrashCanEmptyConfirmationPage trashCanEmptyDialogPage = (TrashCanEmptyConfirmationPage) trashCan.selectEmpty().render();
        Assert.assertTrue(trashCanEmptyDialogPage.isConfirmationDialogDisplayed());
        trashCan = trashCanEmptyDialogPage.clickOkButton().render();
        Assert.assertTrue(trashCan.getPageTitle().equalsIgnoreCase("User Trashcan"));
        Assert.assertFalse(trashCan.hasTrashCanItems());
    }
}
