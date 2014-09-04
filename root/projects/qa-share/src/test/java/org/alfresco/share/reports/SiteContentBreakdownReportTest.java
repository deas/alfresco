/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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


package org.alfresco.share.reports;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.alfresco.po.share.dashlet.SiteContentBreakdownDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * 
 * Site content report dashlet tests
 * 
 * @author jcule
 *
 */
        
@Listeners(FailedTestListener.class)
public class SiteContentBreakdownReportTest extends AbstractUtils
{

 private static final Logger logger = Logger.getLogger(SiteContentBreakdownReportTest.class);
    
    private static String testPassword = DEFAULT_PASSWORD;
    protected String testUser;
    protected String siteName = "";
    
    private static final String JPEG_TYPE = "JPEG Image";
    private static final String TXT_TYPE =  "Plain Text"; 
    private static final String DOCX_TYPE = "Microsoft Word";
    private static final String HTML_TYPE = "HTML";
    private static final String PDF_TYPE =  "Adobe PDF Document";

    private static int numberOfTxtFiles = 5;
    private static int numberOfDocxFiles = 4;
    private static int numberOfHtmlFiles = 2;
    private static int numberOfJpgFiles = 3;
    private static int numberOfPdfFiles = 9;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }
    
    /**
     * 1) Test user is created
     * 2) Test user creates a private site
     * 3) Test user uploads 5 txt files, 4 docx files, 2 html files, 3 jpg files and 9 pdf files
     * 4) Test user logs out
     */
    @Test(groups = { "DataPrepSiteContentBreakdownReport" })
    public void dataPrep_SiteContentBreakdownReport_ALF_1056() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);
        
        // Create test user
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        // Login as created user
        ShareUser.login(drone, testUser, testPassword);

        // Create site
        SiteDashboardPage siteDashboard = ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PRIVATE);

        //upload files
        DocumentLibraryPage docPage = siteDashboard.getSiteNav().selectSiteDocumentLibrary().render();
        uploadFiles(docPage, numberOfTxtFiles, ".txt");
        uploadFiles(docPage, numberOfDocxFiles, ".docx");
        uploadFiles(docPage, numberOfHtmlFiles, ".html");
        uploadFiles(docPage, numberOfJpgFiles, ".jpg");
        uploadFiles(docPage, numberOfPdfFiles, ".pdf");
        
        ShareUser.logout(drone);
        
             
    }

    
    /**
     * 1) Test user (site creator) logs in
     * 2) Test user (site creator) adds Site Content Report Dashlet to site's dashboard
     * 3) Checks the mime types and mime type's counts
     */
    @Test(groups = { "SiteContentBreakdownReport" })
    public void ALF_1056()
    {
        //test user (site creator) logs in
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String siteName = getSiteName(testName);
        ShareUser.login(drone, testUser, testPassword);

        // test user (site creator) adds Site Content Report Dashlet to site's dashboard
        ShareUserDashboard.addDashlet(drone, siteName, Dashlets.SITE_CONTENT_REPORT);
        SiteContentBreakdownDashlet siteContentBreakdownDashlet = ShareUserDashboard.getSiteContentBreakdownDashlet(drone, siteName);
        
        List<String> mimeTypes = siteContentBreakdownDashlet.getTooltipFileTypes();
        Assert.assertTrue(mimeTypes.contains(TXT_TYPE));
        Assert.assertTrue(mimeTypes.contains(JPEG_TYPE));
        //Assert.assertTrue(mimeTypes.contains(DOCX_TYPE));
        Assert.assertTrue(mimeTypes.contains(PDF_TYPE));
        Assert.assertTrue(mimeTypes.contains(HTML_TYPE));   
        
        
        List<String> mimeTypesData = siteContentBreakdownDashlet.getTooltipFileData();
        Assert.assertEquals(mimeTypesData.size(), 5);
        
        for(String mimeType : mimeTypesData)
        {
           String [] counts = mimeType.split("-");
           String fileCount = counts[1];
            
           if (mimeType.trim().startsWith(TXT_TYPE))
           {
                //System.out.println("TYPE-COUNT ++++ " + mimeType);
                //System.out.println("TXT COUNT **** " + fileCount); 
                Assert.assertEquals(fileCount, numberOfTxtFiles);
           }
           if (mimeType.trim().startsWith(JPEG_TYPE))
           {
                //System.out.println("TYPE-COUNT ++++ " + mimeType);
                //System.out.println("JPEG COUNT **** " + fileCount);
                Assert.assertEquals(fileCount, numberOfJpgFiles);
           }
           if (mimeType.trim().startsWith(DOCX_TYPE))
           {
                //System.out.println("TYPE-COUNT ++++ " + mimeType);
                //System.out.println("DOCX COUNT **** " + fileCount);
                Assert.assertEquals(fileCount, numberOfDocxFiles);
           }
           if (mimeType.trim().startsWith(PDF_TYPE))
           {
                //System.out.println("TYPE-COUNT ++++ " + mimeType);
                //System.out.println("PDF COUNT **** " + fileCount);
                Assert.assertEquals(fileCount, numberOfPdfFiles);
           }
           if (mimeType.trim().startsWith(HTML_TYPE))
           {
                //System.out.println("TYPE-COUNT ++++ " + mimeType);
                //System.out.println("HTML COUNT **** " + fileCount);
                Assert.assertEquals(fileCount, numberOfHtmlFiles);
           }
            
        }
        
    }
    
    /**
     * 1) Create test user
     * 2) Login as test user
     * 3) Create site
     * 4) Create user1
     * 5) Add user1 with write permissions to write to the site
     * 6) User1 uploads txt files to site's document library
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepSiteContentBreakdownReport" })
    public void dataPrep_SiteContentBreakdownReport_ALF_1057() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        int numberOfTxtFiles = 2;

        // Create test user
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        // Login as created user
        ShareUser.login(drone, testUser, testPassword);

        // Create site
        SiteDashboardPage siteDashboard = ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PRIVATE);

        // first user
        String testUser1 = getUserNameForDomain(testName + "-1", DOMAIN_FREE);
        String[] testUserInfo1 = new String[] { testUser1 };

        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo1);
        
        // Login as created user
        ShareUser.login(drone, testUser, testPassword);

        // add user with write permissions to write to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser1, siteName, UserRole.COLLABORATOR);

        // Inviting user logs out
        ShareUser.logout(drone);

        // Invited User logs in
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

     
        // first user uploads files
        ShareUser.openSiteDashboard(drone, siteName);
        DocumentLibraryPage docPage = siteDashboard.getSiteNav().selectSiteDocumentLibrary().render();
     
        //DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(driver, siteName)openDocumentLibrary(drone);
        uploadFiles(docPage, numberOfTxtFiles, ".txt");

        // first user logs out
        ShareUser.logout(drone);

   
    }   
    
    
    /**
     * 1) Collaborator logs in
     * 2) Collaborator adds Site Content Report Dashlet to site's dashboard
     * 3) Verify user can't customize the site dasboard 
     */
    @Test(groups = { "SiteContentBreakdownReport" })
    public void ALF_1057()
    {
        //created logs in
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser1 = getUserNameForDomain(testName + "-1", DOMAIN_FREE);
        
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);
       
        //verify user can't customize the site dasboard
        try
        {
            siteDashBoard.getSiteNav().selectCustomizeSite();
            Assert.assertTrue(false, "Above line should have thrown page exception");
        }
        catch (NoSuchElementException e)
        {
            Assert.assertTrue(e.getMessage().startsWith( "Unable to locate element:"));
            ShareUser.logout(drone);

        }
        
    }
    
    
    /**
     * 
     * Uploads files to site's dodocument library
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
    
    
    
}
