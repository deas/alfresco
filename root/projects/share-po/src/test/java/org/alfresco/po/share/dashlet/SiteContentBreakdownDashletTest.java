/*
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

package org.alfresco.po.share.dashlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * SiteContentBreakdownDashlet test class for site content breakdown report dashlet page object
 * 
 * @author jcule
 */
@Test(groups = { "alfresco-one" })
@Listeners(FailedTestListener.class)
public class SiteContentBreakdownDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_CONTENT_REPORT = "site-content-report";
    private SiteContentBreakdownDashlet siteContentBreakdownDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    
    private static final String JPEG_TYPE = "JPEG Image";
    private static final String TXT_TYPE =  "Plain Text"; 
    private static final String DOCX_TYPE = "Microsoft Word 2007";
    private static final String HTML_TYPE = "HTML";
    private static final String PDF_TYPE = "Adobe PDF Document";

    private static int numberOfTxtFiles = 5;
    private static int numberOfDocxFiles = 4;
    private static int numberOfHtmlFiles = 2;
    private static int numberOfJpgFiles = 3;
    private static int numberOfPdfFiles = 9;

    DashBoardPage dashBoard;

    @BeforeTest
    public void prepare()
    {
        siteName = "sitecontentreportdashlettest" + System.currentTimeMillis();

    }

    @BeforeClass
    public void loadFiles() throws Exception
    {
        dashBoard = loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "description", "Public");
        SitePage site = drone.getCurrentPage().render();
        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        uploadFiles(docPage, numberOfTxtFiles, ".txt");
        uploadFiles(docPage, numberOfDocxFiles, ".docx");
        uploadFiles(docPage, numberOfJpgFiles, ".jpg");
        uploadFiles(docPage, numberOfHtmlFiles, ".html");
        uploadFiles(docPage, numberOfPdfFiles, ".pdf");

        navigateToSiteDashboard();

    }

    
    @AfterClass
    public void deleteSite()
    {
      SiteUtil.deleteSite(drone, siteName);
    }
    

    /**
     * 
     * Uploads files to site's document library
     * 
     * @param docPage
     * @param numberofFiles
     * @param extension
     * @throws IOException
     */
    private void uploadFiles(DocumentLibraryPage docPage, int numberofFiles, String extension) throws IOException
    {
        for (int i = 0; i < numberofFiles; i++)
        {
            String random = UUID.randomUUID().toString();
            File file = SiteUtil.prepareFile(random, random, extension);
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
            docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();

        }
    }

    /**
     * Drags and drops Site content report dashlet to site's dashboard
     * 
     */
    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.SITE_CONTENT_REPORT, 2).render();
        siteContentBreakdownDashlet = siteDashBoard.getDashlet(SITE_CONTENT_REPORT).render();
        Assert.assertNotNull(siteContentBreakdownDashlet);
    }
    
    /**
     * Checks files mime types and counts on the pie chart
     */
    @Test(dependsOnMethods = "instantiateDashlet")
    public void testMimeTypesAndCounts() throws Exception
    {
        List<String> mimeTypes = siteContentBreakdownDashlet.getTooltipFileTypes();      
        Assert.assertTrue(mimeTypes.contains(TXT_TYPE));
        Assert.assertTrue(mimeTypes.contains(JPEG_TYPE));
        Assert.assertTrue(mimeTypes.contains(DOCX_TYPE));
        Assert.assertTrue(mimeTypes.contains(PDF_TYPE));
        Assert.assertTrue(mimeTypes.contains(HTML_TYPE));   
                
        
        List<String> mimeTypesData = siteContentBreakdownDashlet.getTooltipFileData();
        
        for(String mimeType : mimeTypesData)
        {
           String [] counts = mimeType.split("-");
           String fileCount = counts[1];

           Assert.assertEquals(mimeTypesData.size(), 5);
            
           if (mimeType.trim().startsWith(TXT_TYPE))
           {
                //System.out.println("TYPE-COUNT ++++ " + mimeType);
                //System.out.println("TXT COUNT **** " + fileCount); 
                Assert.assertEquals(Integer.parseInt(fileCount), numberOfTxtFiles);
           }
           if (mimeType.trim().startsWith(JPEG_TYPE))
           {
                //System.out.println("TYPE-COUNT ++++ " + mimeType);
                //System.out.println("JPEG COUNT **** " + fileCount);
                Assert.assertEquals(Integer.parseInt(fileCount), numberOfJpgFiles);
           }
           if (mimeType.trim().startsWith(DOCX_TYPE))
           {
                //System.out.println("TYPE-COUNT ++++ " + mimeType);
                //System.out.println("DOCX COUNT **** " + fileCount);
                Assert.assertEquals(Integer.parseInt(fileCount), numberOfDocxFiles);
           }
           if (mimeType.trim().startsWith(PDF_TYPE))
           {
                System.out.println("TYPE-COUNT ++++ " + mimeType);
                System.out.println("PDF COUNT **** " + fileCount);
                Assert.assertEquals(Integer.parseInt(fileCount), numberOfPdfFiles);
           }
           if (mimeType.trim().startsWith(HTML_TYPE))
           {
                //System.out.println("TYPE-COUNT ++++ " + mimeType);
                //System.out.println("HTML COUNT **** " + fileCount);
                Assert.assertEquals(Integer.parseInt(fileCount), numberOfHtmlFiles);
           }
            
        }
    }
     
}
