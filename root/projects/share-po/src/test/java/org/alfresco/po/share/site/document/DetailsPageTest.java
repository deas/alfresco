/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.enums.Encoder;
import org.alfresco.po.share.site.NewFolderPage;
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
 * This class Tests the common functionalities in Details page
 * 
 * @author Meenal Bhave
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class DetailsPageTest extends AbstractTest
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static String siteName;
    private static String fileName;
    private static String folderName;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private FolderDetailsPage folderDetails;
    private static String comment = "test comment";
    private static String xssComment = "";    

    /**
     * Pre test setup: Site creation, file upload, folder creation
     * 
     * @throws Exception
     */
    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("====prepare====");
        }
        
        siteName = "site" + System.currentTimeMillis();
        fileName = "File";
        folderName = "The first folder";
        folderDescription = folderName;        
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<IMG \"\"\">");
        stringBuilder.append("<SCRIPT>alert(\"test\")</SCRIPT>");
        stringBuilder.append("\">");
        xssComment = stringBuilder.toString();

        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");

        // Select DocLib
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        // Create Folder
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();

        // Upload File
        File file = SiteUtil.prepareFile(fileName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        fileName = file.getName();
    }

    @AfterClass(groups = { "alfresco-one" })
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(groups = { "alfresco-one" })
    public void addCommentsToFile() throws Exception
    {

        DocumentDetailsPage docDetails = documentLibPage.selectFile(fileName).render();

        // Add text comment
        docDetails.addComment(null);
        docDetails.addComment(comment);
        
        docDetails.addComment(null, null);
        docDetails.addComment(comment, null);
        docDetails.addComment(comment, Encoder.ENCODER_NOENCODER);
        docDetails.addComment(comment, Encoder.ENCODER_HTML);
        docDetails.addComment(comment, Encoder.ENCODER_JAVASCRIPT);

        // Add comment for xss related test
        docDetails.addComment(xssComment);
        docDetails.addComment(xssComment, null);
        docDetails.addComment(xssComment, Encoder.ENCODER_NOENCODER);
        docDetails.addComment(xssComment, Encoder.ENCODER_HTML);
        docDetails.addComment(xssComment, Encoder.ENCODER_JAVASCRIPT);
        
        Assert.assertTrue(docDetails.getComments().contains(xssComment), "Problem adding XSS Comment");
        
    }
    
    @Test(dependsOnMethods="addCommentsToFile", groups = { "alfresco-one" })
    public void addCommentsToFolder() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        folderDetails = documentLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        // Add text comment
        folderDetails.addComment(null);
        folderDetails.addComment(comment);
        
        folderDetails.addComment(null, null);
        folderDetails.addComment(comment, null);
        folderDetails.addComment(comment, Encoder.ENCODER_NOENCODER);
        folderDetails.addComment(comment, Encoder.ENCODER_HTML);
        folderDetails.addComment(comment, Encoder.ENCODER_JAVASCRIPT);

        // Add comment for xss related test
        folderDetails.addComment(xssComment);
        folderDetails.addComment(xssComment, null);
        folderDetails.addComment(xssComment, Encoder.ENCODER_NOENCODER);
        folderDetails.addComment(xssComment, Encoder.ENCODER_HTML);
        folderDetails.addComment(xssComment, Encoder.ENCODER_JAVASCRIPT);
        
        Assert.assertTrue(folderDetails.getComments().contains(xssComment), "Problem adding XSS Comment");
    }
}
