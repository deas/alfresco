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

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * TrashCanPagination Test
 * 
 * @author Subashni Prasanna
 * @since 1.9
 */
@Listeners(FailedTestListener.class)
public class TrashCanPaginationTest extends AbstractTest
{
    protected DashBoardPage dashBoard;
    private String userName = "user" + System.currentTimeMillis() + "@test.com";
    private String firstName = userName;
    private String lastName = userName;
    TrashCanPage trashCan;
    private String siteName = "TrashCanTest" + System.currentTimeMillis();
    private String folderName;
    private String fileName;
    DocumentLibraryPage docPage;
    MyProfilePage myprofile;
    /**
     * Before method to create 60 files and delete them 
     * @throws Exception
     */
	@SuppressWarnings("unused")
    @BeforeClass(groups = { "Enterprise4.2" })
    private void setup() throws Exception
    {
        if (!alfrescoVersion.isCloud())
        {
            dashBoard = loginAs(username, password);
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
            loginAs(username, password);
       
    }
    
    private void prepare() throws Exception
    {
        SiteUtil.createSite(drone, siteName, "Public");
        SitePage site = drone.getCurrentPage().render();
        docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        for (int i = 0; i < 4; i++)
        {
            folderName = "folder" + i + System.currentTimeMillis();
            fileName = "file" + i + System.currentTimeMillis();
            File file = SiteUtil.prepareFile(fileName);
            docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
            NewFolderPage folder = docPage.getNavigation().selectCreateNewFolder().render();
            docPage = folder.createNewFolder(folderName).render();
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
            upLoadPage.uploadFile(file.getCanonicalPath()).render();
        }
        docPage = drone.getCurrentPage().render();
        for (int i = 0; i < 3; i++)
        {
            docPage = docPage.getNavigation().selectAll().render();
            CopyOrMoveContentPage copyContent = docPage.getNavigation().selectCopyTo().render();
            docPage = copyContent.selectOkButton().render();
            docPage = drone.getCurrentPage().render();
        }
        docPage = drone.getCurrentPage().render();
        do
        {
            docPage = docPage.getNavigation().selectAll().render();
            DocumentLibraryNavigation docLibNavOption = docPage.getNavigation().render();
            ConfirmDeletePage deletePage = docLibNavOption.selectDelete().render();
            deletePage.selectAction(Action.Delete);
            docPage = drone.getCurrentPage().render();
        } while (docPage.hasFiles());
    }
        
    @AfterClass(groups = { "Enterprise4.2" })
    public void deleteSite()
    {
      trashCan.selectEmpty().render();
      SiteUtil.deleteSite(drone, siteName);
    }
    
    private TrashCanPage getTrashCan()
    {
        dashBoard = docPage.getNav().selectMyDashBoard().render();
        myprofile = dashBoard.getNav().selectMyProfile().render();
        trashCan = myprofile.getProfileNav().selectTrashCan().render();
        return trashCan;
    }
    
    @Test (groups = { "Enterprise4.2" })
    public void trashCanEmptyPagination()
    {
     dashBoard = drone.getCurrentPage().render();
     myprofile = dashBoard.getNav().selectMyProfile().render();
     trashCan = myprofile.getProfileNav().selectTrashCan().render();
     Assert.assertFalse(trashCan.hasNextPage());
     Assert.assertFalse(trashCan.hasPreviousPage());
    }
    
    @Test (dependsOnMethods = "trashCanEmptyPagination", groups = { "Enterprise4.2" })
    public void trashCanHasPagination() throws Exception
    {
       prepare();
       trashCan  = getTrashCan();
        Assert.assertTrue(trashCan.hasNextPage());
        trashCan.selectNextPage();
        trashCan = trashCan.selectNextPage().render();
        Assert.assertTrue(trashCan.getTrashCanItems().size() > 0);
        Assert.assertTrue(trashCan.hasPreviousPage());
        trashCan = trashCan.selectPreviousPage().render();
        Assert.assertTrue(trashCan.getTrashCanItems().size() > 0);
    }
}
