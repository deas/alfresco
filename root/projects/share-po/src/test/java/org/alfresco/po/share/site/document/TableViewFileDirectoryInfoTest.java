/*
 * Copyright (C) 2005-2014 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page in Table View is operating correctly.
 * 
 * @author Jamie Allison
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one"})
public class TableViewFileDirectoryInfoTest extends AbstractDocumentTest
{
    private static final String FILE_TITLE = "File";
    private static final String FILE_DESCRIPTION = "This is file";
    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    private File file1;
    private File file2;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    private void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();

        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        
        
        SiteUtil.createSite(drone, siteName, "description", "Public");
        file1 = SiteUtil.prepareFile();
        file2 = SiteUtil.prepareFile();
        
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();
        
    }

    @AfterClass(groups="alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(groups="alfresco-one")
    public void selectTableView() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        int noOfFiles = documentLibPage.getFiles().size();
        documentLibPage = ((DocumentLibraryPage) documentLibPage.getNavigation().selectTableView()).render();
        Assert.assertNotNull(documentLibPage);
        Assert.assertEquals(documentLibPage.getFiles().size(), noOfFiles);
    }

    @Test(dependsOnMethods = "selectTableView", groups="alfresco-one")
    public void selectEditProperties() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        TableViewFileDirectoryInfo fileInfo = (TableViewFileDirectoryInfo) documentLibPage.getFiles().get(0);
        EditDocumentPropertiesPopup editPage = fileInfo.selectEditProperties().render();
        editPage.setDescription(FILE_DESCRIPTION);
        editPage.setDocumentTitle(FILE_TITLE);
        documentLibPage = editPage.selectSave().render();

        Assert.assertEquals(fileInfo.getTitle(), FILE_TITLE);
    }

    @Test(dependsOnMethods = "selectEditProperties", groups="alfresco-one")
    public void getCreator() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        TableViewFileDirectoryInfo fileInfo = (TableViewFileDirectoryInfo) documentLibPage.getFiles().get(0);

        Assert.assertEquals(fileInfo.getCreator(), "Administrator");
    }

    @Test(dependsOnMethods = "getCreator", groups="alfresco-one")
    public void selectCreator() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        TableViewFileDirectoryInfo fileInfo = (TableViewFileDirectoryInfo) documentLibPage.getFiles().get(0);
        UserProfilePage profile = fileInfo.selectCreator();
        Assert.assertNotNull(profile);
        navigateDocumentLib(profile);
    }

    private void navigateDocumentLib(UserProfilePage profile)
    {
        SiteFinderPage finderPage = profile.getNav().selectSearchForSites().render();
        finderPage = finderPage.searchForSite(siteName).render();
        SiteDashboardPage dashboardPage  = finderPage.selectSite(siteName).render();
        documentLibPage = dashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
    }

    @Test(dependsOnMethods = "selectCreator", groups="alfresco-one")
    public void getCreated() throws Exception
    {
        // TODO: This test works but it needs to end on the site doclib page
        /*SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        TableViewFileDirectoryInfo fileInfo = (TableViewFileDirectoryInfo) documentLibPage.getFiles().get(0);
        Assert.assertNotNull(fileInfo.getCreated());*/
    }

    @Test(dependsOnMethods = "getCreated", groups="alfresco-one")
    public void getModifier() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        TableViewFileDirectoryInfo fileInfo = (TableViewFileDirectoryInfo) documentLibPage.getFiles().get(0);

        Assert.assertEquals(fileInfo.getModifier(), "Administrator");
    }

    @Test(dependsOnMethods = "getModifier", groups="alfresco-one")
    public void selectModifier() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        TableViewFileDirectoryInfo fileInfo = (TableViewFileDirectoryInfo) documentLibPage.getFiles().get(0);
        UserProfilePage profile = fileInfo.selectModifier();
        Assert.assertNotNull(profile);
        navigateDocumentLib(profile);
    }

    @Test(dependsOnMethods = "selectModifier", groups="alfresco-one")
    public void getModified() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        TableViewFileDirectoryInfo fileInfo = (TableViewFileDirectoryInfo) documentLibPage.getFiles().get(0);
        Assert.assertNotNull(fileInfo.getModified());
    }

    @Test(dependsOnMethods = "getModified", enabled = true, groups = "alfresco-one")
    public void renameContentTest()
    {
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file1.getName());

        thisRow.renameContent(file1.getName() + " updated");
        Assert.assertEquals(documentLibPage.getFileDirectoryInfo(file1.getName() + " updated").getName(), file1.getName() + " updated");
    }

    @Test(enabled = true, groups = "alfresco-one", dependsOnMethods = "renameContentTest")
    public void cancelRenameContentTest()
    {
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file2.getName());
        thisRow.contentNameEnableEdit();
        thisRow.contentNameEnter(file2.getName() + " not updated");
        thisRow.contentNameClickCancel();
        drone.refresh();
        documentLibPage = drone.getCurrentPage().render();
        Assert.assertEquals(documentLibPage.getFileDirectoryInfo(file2.getName()).getName(), file2.getName());
    }

}
