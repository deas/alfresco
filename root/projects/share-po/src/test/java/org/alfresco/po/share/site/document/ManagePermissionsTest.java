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
package org.alfresco.po.share.site.document;

import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.IOException;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ManagePermissionsPage.UserSearchPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
/** Once it is fixed can we remove the group called BuildBrokenBug and comment. 
 * (JIRA Issue: WEBDRONE-284) 
 *  Add group names back. 
 **/
@Test(groups = {"BuildBrokenBug"})
public class ManagePermissionsTest extends AbstractTest
{

    private String siteName;
    private File sampleFile;
    private ManagePermissionsPage pageUnderTest;
    private UserSearchPage pageReturned;

    @BeforeClass
    public void beforeClass() throws IOException
    {
        siteName = "site" + System.currentTimeMillis();

        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        SitePage page = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        sampleFile = SiteUtil.prepareFile();
        UploadFilePage upLoadPage = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = upLoadPage.uploadFile(sampleFile.getCanonicalPath()).render();
        DocumentDetailsPage docDetailPage = documentLibPage.selectFile(sampleFile.getName()).render();
        pageUnderTest = docDetailPage.selectManagePermissions().render();

    }

    @AfterClass
    public void afterClass()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void toggleInheritPermissionTest()
    {
        pageUnderTest = pageUnderTest.toggleInheritPermission(false).render();
        assertTrue("The Inherit permissio table should not be displayed.", !pageUnderTest.isInheritPermissionEnabled());

        pageUnderTest = pageUnderTest.toggleInheritPermission(true).render();
        assertTrue("The Inherit permissio table should be displayed.", pageUnderTest.isInheritPermissionEnabled());
        
    }

    @Test(dependsOnMethods = "toggleInheritPermissionTest")
    public void selectAddUserTest()
    {
        pageReturned = pageUnderTest.selectAddUser().render();
        assertTrue(pageReturned instanceof UserSearchPage);
    }
    
    @Test(dependsOnMethods = "selectAddUserTest")
    public void searchUserTest()
    {
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);
        UserSearchPage searchPage = pageReturned;
        pageUnderTest = searchPage.searchAndSelectUser(userProfile);
        pageUnderTest.render();
        assertTrue("User did not get added to 'Locally Set Permissions' table as user", pageUnderTest.isDirectPermissionForUser(userProfile));
    }

    @Test(dependsOnMethods = "searchUserTest")
    public void setAccessTypeTest()
    {
        UserRole userRole = UserRole.COLLABORATOR;
        pageUnderTest.setAccessType(userRole);
        DocumentDetailsPage pageReturned = (DocumentDetailsPage) pageUnderTest.selectSave();
        pageReturned.render();
        assertTrue(pageReturned instanceof DocumentDetailsPage);
        pageUnderTest = pageReturned.selectManagePermissions().render();
        UserRole role = pageUnderTest.getAccessType();
        assertTrue("Access type should have been '" + userRole + "' but was - " + pageUnderTest.getAccessType(),
                UserRole.COLLABORATOR.equals(role));
        pageReturned = (DocumentDetailsPage) pageUnderTest.selectCancel();
        pageReturned.render();
        assertTrue(pageReturned instanceof DocumentDetailsPage);
    }
    
    @Test(dependsOnMethods = "setAccessTypeTest")
    public void updateRoleTest()
    {
        pageUnderTest = ((DocumentDetailsPage)drone.getCurrentPage()).selectManagePermissions().render();
        Assert.assertFalse(pageUnderTest.isUserExistForPermission(username));       
        assertTrue(pageUnderTest.isUserExistForPermission("Administrator"));
        assertTrue(pageUnderTest.updateUserRole("Administrator", UserRole.CONSUMER));        
        ((DocumentDetailsPage) pageUnderTest.selectCancel()).render();        
    }
    
}
