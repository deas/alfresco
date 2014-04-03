/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document.download;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Meenal Bhave
 * @since 1.6.1
 */
@Listeners(FailedTestListener.class)
@Test(groups="download")
public class DownloadFileAndFolderTest extends AbstractDocumentTest
{
    private static String siteName;
    private static String folderName;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibraryPage;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folderDescription = String.format("Description of %s", folderName);
        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        SitePage page = drone.getCurrentPage().render();
        documentLibraryPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibraryPage.getNavigation().selectCreateNewFolder();
        documentLibraryPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
    }

    @AfterClass
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }
    
    @Test
    public void downloadTextFile() throws Exception
    {
        CreatePlainTextContentPage contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails details = new ContentDetails();
        details.setName("TextFile");
        DocumentDetailsPage detailsPage = contentPage.create(details).render();
        documentLibraryPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        FileDirectoryInfo row = documentLibraryPage.getFileDirectoryInfo("TextFile");
        row.selectDownload();
        documentLibraryPage.waitForFile(downloadDirectory + "TextFile");
        documentLibraryPage.render();
    }

    @Test(dependsOnMethods="downloadTextFile")
    public void uploadFile() throws Exception
    {
        documentLibraryPage.getFileDirectoryInfo(folderName).selectDownloadFolderAsZip();
        documentLibraryPage.waitForFile(downloadDirectory + folderName + ".zip");
        documentLibraryPage.render();
    }
    
}
