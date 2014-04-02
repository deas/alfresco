/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;
import java.util.Set;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Share Link page is operating correctly.
 * 
 * @author Chiran
 */
@Listeners(FailedTestListener.class)
public class ShareLinkTest extends AbstractDocumentTest
{
    private static Log logger = LogFactory.getLog(ShareLinkTest.class);
    private static String siteName;
    private String userName = "user" + System.currentTimeMillis() + "@test.com";
    private String firstName = userName;
    private String lastName = userName;
    private static DocumentLibraryPage documentLibPage;
    private File file;
    private final String cloudUserName = "user1@premiernet.test";
    private final String cloudUserPassword = "spr!nkles";

    @BeforeClass(alwaysRun=true)
    private void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        createUser();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        file = SiteUtil.prepareFile("alfresco123");
        createData();
    }

    @AfterClass(alwaysRun=true)
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }
    
    /**
     * Create User 
     * @throws Exception 
     */
    public void createUser() throws Exception
    {
        if (!alfrescoVersion.isCloud())
        {
            DashBoardPage dashBoard = loginAs(username, password);
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
        {
            loginAs(cloudUserName, cloudUserPassword);
        }
    }
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    public void createData() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        documentLibPage = ((DocumentLibraryPage) documentLibPage.getNavigation().selectGalleryView()).render();
    }
    
    @Test(groups={"alfresco-one"}, priority=1)
    public void testViewLink()
    {
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
        Assert.assertTrue(thisRow.isShareLinkVisible());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();
        Assert.assertNotNull(shareLinkPage);
        Assert.assertTrue(shareLinkPage.isViewLinkPresent());
        ViewPublicLinkPage viewPage = shareLinkPage.clickViewButton().render();
        Assert.assertTrue(viewPage.isDocumentViewDisplayed());
        DocumentDetailsPage detailsPage = viewPage.clickOnDocumentDetailsButton().render();
        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
    }
    
    @Test(groups={"alfresco-one"}, priority=2)
    public void testVerifyUnShareLink()
    {
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();
        Assert.assertTrue(shareLinkPage.isUnShareLinkPresent());
        documentLibPage = shareLinkPage.clickOnUnShareButton().render();
    }
    
    @Test(groups={"alfresco-one"}, priority=3)
    public void testVerifyEmailLink()
    {
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();
        Assert.assertTrue(shareLinkPage.isEmailLinkPresent());
        documentLibPage = shareLinkPage.clickOnUnShareButton().render();
    }
    
    @Test(groups={"alfresco-one"}, priority=4)
    public void testOtherShareLinks()
    {
        FileDirectoryInfo thisRow =  documentLibPage.getFileDirectoryInfo(file.getName());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();

        Assert.assertTrue(shareLinkPage.isFaceBookLinkPresent());
        shareLinkPage.clickFaceBookLink();
        
        String mainWindow = drone.getWindowHandle();
        Assert.assertTrue(isWindowOpened("Facebook"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);
        
        Assert.assertTrue(shareLinkPage.isTwitterLinkPresent());
        shareLinkPage.clickTwitterLink();
        mainWindow = drone.getWindowHandle();
        Assert.assertTrue(isWindowOpened("Share a link on Twitter"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);
        
        Assert.assertTrue(shareLinkPage.isGooglePlusLinkPresent());
        shareLinkPage.clickGooglePlusLink();
        mainWindow = drone.getWindowHandle();
        Assert.assertTrue(isWindowOpened("Google+"));
        drone.closeWindow();
        drone.switchToWindow(mainWindow);
    }

    private boolean isWindowOpened(String windowName)
    {
        Set<String> windowHandles = drone.getWindowHandles();

        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            logger.info(drone.getTitle());
            if (drone.getTitle().equals(windowName))
            {
                return true;
            }
        }
        return false;
    }
}