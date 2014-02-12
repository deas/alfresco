/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage.Optype;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit tests to verify methods of Sync Info functions are working correctly.
 * @author nshah
 *
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Hybrid")
public class SyncInfoPageTest extends AbstractDocumentTest
{

    private static String siteName;   
    private static DocumentLibraryPage documentLibPage;
    private File file;
    private final String cloudUserName = "user1@premiernet.test";
    private final String cloudUserPassword = "password";
    private DestinationAndAssigneePage desAndAsgPage ;
    private String folder;
    private String folder2;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unused")
    @BeforeClass
    private void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folder = "TempFolder"+System.currentTimeMillis();
        folder2 = "TempFolder-2"+System.currentTimeMillis();
        ShareUtil.loginAs(drone, shareUrl, username, password).render();        
        file = SiteUtil.prepareFile();
    }

    @Test(groups = "Hybrid")
    public void prepareCloudSyncData() throws Exception
    {
        
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();

        CloudSignInPage cloudSignInPage = cloudSyncPage.selectCloudSign().render();
        cloudSyncPage = cloudSignInPage.loginAs(cloudUserName, cloudUserPassword).render();
                
        SiteUtil.createSite(drone, siteName, "Public");
        SitePage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        drone.refresh();        
        desAndAsgPage = (DestinationAndAssigneePage)documentLibPage.getFileDirectoryInfo(file.getName()).selectSyncToCloud().render();
        documentLibPage = ((DocumentLibraryPage)desAndAsgPage.selectSubmitButtonToSync()).render();  
        Assert.assertTrue(documentLibPage.isMessagePresent(Optype.SYNC));
        documentLibPage.render().getNavigation().selectCreateNewFolder().render().createNewFolder(folder);
        drone.refresh();        
        documentLibPage.render().getNavigation().selectCreateNewFolder().render().createNewFolder(folder2);
        drone.refresh();
        desAndAsgPage = (DestinationAndAssigneePage)documentLibPage.getFileDirectoryInfo(folder).selectSyncToCloud().render();
        documentLibPage = ((DocumentLibraryPage)desAndAsgPage.selectSubmitButtonToSync()).render();   
        desAndAsgPage = (DestinationAndAssigneePage)documentLibPage.getFileDirectoryInfo(folder2).selectSyncToCloud().render();
        documentLibPage = ((DocumentLibraryPage)desAndAsgPage.selectSubmitButtonToSync()).render(); 
    }
    
    @Test(groups = "Hybrid", dependsOnMethods="prepareCloudSyncData")
    public void testSyncInfoIcon() throws Exception
    {
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(folder).isCloudSynced());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file.getName()).isCloudSynced());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file.getName()).isViewCloudSyncInfoLinkPresent());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(folder).isViewCloudSyncInfoLinkPresent());        
        Assert.assertEquals("Click to view sync info", documentLibPage.getFileDirectoryInfo(folder).getCloudSyncType());        
    }
    @Test(groups = "Hybrid", dependsOnMethods="prepareCloudSyncData")
    public void testSyncInfoPopupMethods() throws Exception
    {
        drone.refresh();
        FileDirectoryInfo fileDirInfo = documentLibPage.getFileDirectoryInfo(folder);
        SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo();  
        syncInfoPage.render(5000);
        Assert.assertTrue(syncInfoPage.getCloudSyncStatus().contains("Sync")?true:false);
        Assert.assertEquals("premiernet.test>Auto Account's Home>Documents>"+folder, syncInfoPage.getCloudSyncLocation());
        Assert.assertEquals(folder, syncInfoPage.getCloudSyncDocumentName());
        
        Assert.assertTrue(syncInfoPage.isLogoPresent());
        Assert.assertTrue(syncInfoPage.isRequestSyncButtonPresent());
        Assert.assertTrue(syncInfoPage.isSyncStatusPresent());
        Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());        
        Assert.assertNotNull(syncInfoPage.getSyncPeriodDetails());
        syncInfoPage.clickOnCloseButton();
        documentLibPage = (DocumentLibraryPage)drone.getCurrentPage().render();
        Assert.assertTrue(documentLibPage instanceof DocumentLibraryPage);
    }
    
    @Test(groups = "Hybrid", dependsOnMethods="testSyncInfoPopupMethods")
    public void testSyncInfoPopup() throws Exception
    {
        drone.refresh();
        FileDirectoryInfo fileDirInfo = documentLibPage.getFileDirectoryInfo(folder);
        SyncInfoPage syncInfoPage = fileDirInfo.clickOnViewCloudSyncInfo();  
        syncInfoPage.render(5000);
        Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());
        syncInfoPage.selectUnsyncRemoveContentFromCloud(true);
        
        documentLibPage = (DocumentLibraryPage)drone.getCurrentPage().render();     
        fileDirInfo = documentLibPage.getFileDirectoryInfo(folder);        
        Assert.assertFalse(fileDirInfo.isViewCloudSyncInfoLinkPresent());
        
        syncInfoPage =  documentLibPage.getFileDirectoryInfo(folder2).clickOnViewCloudSyncInfo();
        syncInfoPage.render(1000);
        Assert.assertTrue(syncInfoPage.isUnsyncButtonPresent());
        syncInfoPage.selectUnsyncRemoveContentFromCloud(false);
    }
    @AfterClass
    public void teardown()
    {
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        cloudSyncPage.disconnectCloudAccount();
        SiteUtil.deleteSite(drone, siteName);
    }
}
