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
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
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
     * 
     * @throws Exception
     */
    @Test(groups = { "DataSiteContentBreakdownReport" })
    public void dataPrep_TopSiteContributor_ALF_1056() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);
               
        int numberOfTxtFiles = 5;
        int numberOfDocxFiles = 4;
        int numberOfHtmlFiles = 2;
        int numberOfJpgFiles = 3;
        int numberOfPdfFiles = 9;
        
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
        SiteContentBreakdownDashlet siteContentBreakdownDashlet = ShareUserDashboard.getSiteContentBreakdownDashlet(drone, siteName);
        List<String> mimeTypes = siteContentBreakdownDashlet.getMimeTypes();
        List<String> mimeTypeCounts = siteContentBreakdownDashlet.getMimeTypeCounts();

        Assert.assertEquals(mimeTypes.size(), 5);
        Assert.assertEquals(mimeTypeCounts.size(), 5);
 
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
