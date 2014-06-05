/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;
import java.io.IOException;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Basic integration test to verify that document previews are using the PdfJs viewer.
 * 
 * <p>Tests PDF, DOC, DOCX and TXT file types</p>
 * 
 * @author Will Abson
 */
@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one"})
public class DocumentDetailsPagePreviewTest extends AbstractDocumentTest
{
    private String siteName;
    private SitePage site;
    private static Log logger = LogFactory.getLog(AbstractTest.class);

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "Site-1" + System.currentTimeMillis();
        loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "Public");
        site = drone.getCurrentPage().render();
    }

    @AfterClass
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    /**
     * Test that a PDF document is correctly displayed using the PdfJs previewer
     * 
     * @throws IOException
     */
    @Test
    public void testPdfPreview() throws Exception
    {
        File file1 = SiteUtil.prepareFile("File-1"+System.currentTimeMillis(), "This is a sample file", ".pdf");
        DocumentLibraryPage documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        documentLibPage.render();
        DocumentDetailsPage docDetailsPage = selectDocument(file1).render();

        if(logger.isDebugEnabled())
        {
            logger.debug("Previewer class name: " + docDetailsPage.getPreviewerClassName());
        }

        Assert.assertTrue(docDetailsPage.isDocumentDetailsPage());
        Assert.assertTrue(docDetailsPage.getPreviewerClassName().endsWith("PdfJs"));

        try
        {
            docDetailsPage.getPdfJsPreview();
        }
        catch(PageRenderTimeException e)
        {
            Assert.assertTrue(false, "PdfJs preview could not be rendered");
        }
    }

}
