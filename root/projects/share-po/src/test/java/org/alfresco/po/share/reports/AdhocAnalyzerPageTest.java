/*
sd * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.share.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.dashlet.AdhocAnalyzerDashlet;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.InviteMembersPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.MimeType;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * AdhocAnalyzerPageTest test class for adhoc analyzer page object
 * 
 * @author jcule
 */

@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class AdhocAnalyzerPageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(AdhocAnalyzerPageTest.class);

    private static final String CUSTOM_REPORTS = "My Reports";
    private static final String CUSTOM_SITE_REPORTS = "Site Reports";
    private static final String UNSAVED_REPORT = "Unsaved Report";
    //private static final String PENTAHO_BUSINESS_ANALYST_USERNAME = "pentahoBusinessAnalyst";
    //private static final String PENTAHO_BUSINESS_ANALYST_PASSWORD = "pentahoBusinessAnalyst";
    private static final String pentahoBusinessAnalystGroup = "ANALYTICS_BUSINESS_ANALYSTS";
    
    private static String reportName = null;
    private static String siteReportName = null;
    
    private String userName = "User_" + System.currentTimeMillis();
    private String siteName = "Site_" + System.currentTimeMillis();
    private String folderName = "Folder_" + System.currentTimeMillis();
    private String fileName = "File_" + System.currentTimeMillis();
    private String siteName1 = "Site1_" + System.currentTimeMillis();
    private String businessAnalystsUserName = "BusinessAnalystUser_" + System.currentTimeMillis();

    
    @Test
    public void createBusinessAnalystUser() throws Exception
    {
        DashBoardPage dashBoard = loginAs(username, password);
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();
        newPage.createEnterpriseUserWithGroup(businessAnalystsUserName, businessAnalystsUserName, businessAnalystsUserName, businessAnalystsUserName + "@test.com", UNAME_PASSWORD, pentahoBusinessAnalystGroup);
        
        logout(drone);
    
    }
    
    
    @Test(dependsOnMethods = "createBusinessAnalystUser")
    public void loadFiles() throws Exception
    {
 
        //drop and recreate schemas
        schemasSetup();
        
        //create some users, sites and content first
        userShareInteractions();
        
        //run fact generation
        factTableGeneration();
         
    }
    
    private void userShareInteractions() throws Exception
    {
 
        //create user
        createEnterpriseUser(userName);

        //login as created user
        loginAs(userName, UNAME_PASSWORD);
        
        //user creates site
        SiteUtil.createSite(drone, siteName, "description", "public");
        
        //and a folder in sites document library
        SitePage site = drone.getCurrentPage().render();
        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        
        NewFolderPage newFolderPage = docPage.getNavigation().selectCreateNewFolder().render();
        docPage = newFolderPage.createNewFolder(folderName, folderName, folderName).render();
        
        //add comment to folder
        FolderDetailsPage folderDetailsPage = docPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
        folderDetailsPage.addComment("folderComment").render();
        
        site = drone.getCurrentPage().render();
        docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        
        //create text file in the folder - file-previewed 
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        CreatePlainTextContentPage contentPage = docPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        contentPage.create(contentDetails).render();
        
        //uploads a file in the folder
        site = drone.getCurrentPage().render();
        docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        
        File file = SiteUtil.prepareFile(fileName, fileName, ".txt");
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
        
        //add comment to file - file-previewed
        docPage.selectFile(fileName).addComment("fileComment").render();
        
        //like file
        site = drone.getCurrentPage().render();
        docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        FileDirectoryInfo fileDirectoryInfo = docPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectLike();
        
        //share file
        docPage = drone.getCurrentPage().render();
        docPage.getFileDirectoryInfo(fileName).clickShareLink().render();
        
        ContentDetails newContentDetails = new ContentDetails();
        newContentDetails.setContent(testName);
        
        //edit document inline
        InlineEditPage inlineEditPage = docPage.getFileDirectoryInfo(fileName).selectInlineEdit().render();
        EditTextDocumentPage editTextDocumentPage = inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT).render();
        editTextDocumentPage.saveWithValidation(newContentDetails).render();
      
        //select all
        docPage.getNavigation().selectAll().render();

        //delete all
        ConfirmDeletePage confirmDeletePage = docPage.getNavigation().selectDelete().render();
        confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();
       
        // add user with write permissions to write to the site
        InviteMembersPage membersPage = docPage.getSiteNav().selectInvite().render();
        List<String> users = membersPage.searchUser(username);
        for (String user : users)
        {    
            if(user.equalsIgnoreCase("(" + username + ")"))
            {
                membersPage.clickAddUser(user);
            }
        }
        membersPage.selectInviteeAndAssignRole("(" + username + ")", UserRole.COLLABORATOR);
        membersPage.clickInviteButton();
        
        logout(drone);
        
        DashBoardPage dashBoard = loginAs(username, password).render();
        MyTasksDashlet task = dashBoard.getDashlet("tasks").render();
        EditTaskPage editTaskPage = task.clickOnTask(siteName).render();
        dashBoard = editTaskPage.selectAcceptButton().render();
        
        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder =  SiteUtil.siteSearchRetry(drone, siteFinder, siteName);
 
        siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder =  SiteUtil.siteSearchRetry(drone, siteFinder, siteName);
        siteFinder.leaveSite(siteName).render();
              
        logout(drone);
    }
    
    /**
     * Sets up schemas and populates stagedmsg table
     * 
     * @throws Exception
     */
    private void schemasSetup() throws Exception
    {
        logger.info("Setting up schemas");
        try 
        {
            Process proc = null;
            if (SystemUtils.IS_OS_WINDOWS)
            {
                proc = Runtime.getRuntime().exec(getClass().getResource("/SchemasSetup.bat").getFile());
            } else if (SystemUtils.IS_OS_LINUX) {
                proc = Runtime.getRuntime().exec(getClass().getResource("/SchemasSetup.sh").getFile());
            }
            BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = read.readLine()) != null) {  
                logger.info(line);
            } 

        } catch (Exception e) {
            logger.error("Error during recreating schemas " + e);
        }       
    }
    
    /**
     * Populates cube
     * 
     * @throws Exception
     */
    private void factTableGeneration() throws Exception
    {
        try 
        {
            //transfers data from stagedmsg into fact and dim tables
            logger.info("Populating the cube");
            Process proc = null;
            if (SystemUtils.IS_OS_WINDOWS)
            {
                proc = Runtime.getRuntime().exec(getClass().getResource("/FactTableGeneration.bat").getFile());
            } else if (SystemUtils.IS_OS_LINUX) {
                proc = Runtime.getRuntime().exec(getClass().getResource("/FactTableGeneration.sh").getFile());
            }
                
            BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = read.readLine()) != null) {  
                logger.info(line);
            } 

        } catch (Exception e) {
            logger.error("Error during fact table generation " + e);
        }       
    }
  
    /**
     * Opens report in user dashboard dashlet
     */  
    @Test(dependsOnMethods = "createBusinessAnalystUser")
    public void testOpenReportInUserboardDashlet() throws Exception
    {
        
        //DashBoardPage dashboardPage = loginAs(PENTAHO_BUSINESS_ANALYST_USERNAME, PENTAHO_BUSINESS_ANALYST_PASSWORD);
        DashBoardPage dashboardPage = loginAs(businessAnalystsUserName, UNAME_PASSWORD);
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_REPORTS);
        
        adhocAnalyzePage.clickOnAnalyzeButton();
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);        
        
        
        // create new report
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
         
        // click on Save button
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        reportName = "NewReport-" + System.currentTimeMillis();
        createEditAdhocReportPage.enterAnalisysName(reportName);

        logger.info("Saving "+ reportName);
        
        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();

       
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());
        
        logger.info("Report "+ reportName + " successfully saved.");
        
        // Click on open button to open saved report
        createEditAdhocReportPage = createEditAdhocReportPage.clickOnOpenReportButton();
        createEditAdhocReportPage.clickOnExistingReport(reportName);
                
        //edit existing report, save it and check that report is updated
        createEditAdhocReportPage.doubleClickOnDayField();
        createEditAdhocReportPage.clickOnSaveReportButton();

        String [] tableStatusBarElements = createEditAdhocReportPage.getTableStatusBar();
               
             
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isDayDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());
        
        
        Assert.assertEquals(tableStatusBarElements[0].trim(), "Rows:");
        Assert.assertEquals(tableStatusBarElements[3].trim(), "Cols:");
        Assert.assertTrue(Integer.parseInt(tableStatusBarElements[1].trim()) > 0);
        Assert.assertTrue(Integer.parseInt(tableStatusBarElements[4].trim()) > 0);
        
        // check that the name of the report is saved correctly
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), reportName);
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_REPORTS);

        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        
        logger.info("Report "+ reportName + " successfully edited.");
        
        //change chart type
        createEditAdhocReportPage.clickOnChangeChartType();
        
        Assert.assertTrue(createEditAdhocReportPage.isPieChartEventsDisplayed());
        
        dashboardPage = createEditAdhocReportPage.getNav().selectMyDashBoard().render();
        CustomiseUserDashboardPage customiseUserDashboardPage = dashboardPage.getNav().selectCustomizeUserDashboard().render();
        
        dashboardPage = customiseUserDashboardPage.addDashlet(Dashlets.ADHOC_ANALYZER, 2).render();
        AdhocAnalyzerDashlet adhocAnalyzerDashlet = dashboardPage.getDashlet("adhoc-analyzer").render();
        Assert.assertTrue(adhocAnalyzerDashlet.isTitleDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isOpenDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isDashletMessageDisplayed());
        adhocAnalyzerDashlet.clickOnOpenDropdown();
        adhocAnalyzerDashlet.clickOnExistingReport(reportName);
        Assert.assertEquals(adhocAnalyzerDashlet.getDashletTitle(), reportName);
 
        int counter = 0;
        int waitInMilliSeconds = 8000;
        while (counter < 3)
        {
            synchronized (this)
            {
                try
                {
                    this.wait(waitInMilliSeconds);
                }
                catch (InterruptedException e)
                {
                }
            }
            if (createEditAdhocReportPage.isSiteNameDisplayed())
            {
                break;
            }
            else
            {
                counter++;
                drone.refresh();
            }
            // double wait time  
            waitInMilliSeconds = (waitInMilliSeconds * 2);

        }
 
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());

        logout(drone);

    }
    
    /**
     * Opens site report in site dashboard dashlet
     * 
     * @throws Exception
     */
    
    @Test(dependsOnMethods = "testOpenReportInUserboardDashlet")
    public void testOpenReportInSiteboardDashlet() throws Exception
    {
        //DashBoardPage dashboardPage = loginAs(PENTAHO_BUSINESS_ANALYST_USERNAME, PENTAHO_BUSINESS_ANALYST_PASSWORD);
        DashBoardPage dashboardPage = loginAs(businessAnalystsUserName, UNAME_PASSWORD);
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyzeSite().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_SITE_REPORTS); 
        
        adhocAnalyzePage.clickOnAnalyzeButton();
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_SITE_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);

        // create new report
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
         
        // click on Save button
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        siteReportName = "NewReport1-" + System.currentTimeMillis();
        createEditAdhocReportPage.enterAnalisysName(siteReportName);

        logger.info("Saving "+ siteReportName);
        
        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();

        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());
        
        logger.info("Report "+ reportName + " successfully saved.");
           
        //user creates site
        SiteUtil.createSite(drone, siteName1, "description", "public");
        
        SitePage site = drone.getCurrentPage().render();
        CustomiseSiteDashboardPage customiseSiteDashBoard = site.getSiteNav().selectCustomizeDashboard().render();
        SiteDashboardPage siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.CUSTOM_SITE_REPORTS, 1).render();
        
        //DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        
        AdhocAnalyzerDashlet adhocAnalyzerDashlet = siteDashBoard.getDashlet("adhoc-analyzer").render();
        Assert.assertTrue(adhocAnalyzerDashlet.isTitleDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isOpenDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isSiteDashletMessageDisplayed());
        adhocAnalyzerDashlet.clickOnOpenDropdown();
        adhocAnalyzerDashlet.clickOnExistingReport(siteReportName);
        Assert.assertEquals(adhocAnalyzerDashlet.getDashletTitle(), siteReportName);
 
        int counter = 0;
        int waitInMilliSeconds = 8000;
        while (counter < 3)
        {
            synchronized (this)
            {
                try
                {
                    this.wait(waitInMilliSeconds);
                }
                catch (InterruptedException e)
                {
                }
            }
            if (createEditAdhocReportPage.isSiteNameDisplayed())
            {
                break;
            }
            else
            {
                counter++;
                drone.refresh();
            }
            // double wait time  
            waitInMilliSeconds = (waitInMilliSeconds * 2);

        }
 
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());

        logout(drone);
        
    }
     
    /**
     * Deletes report
     * 
     * @throws Exception
     */
    
    @Test(dependsOnMethods = "testOpenReportInSiteboardDashlet")
    public void testDeleteReport() throws Exception
    {
        //DashBoardPage dashboardPage = loginAs(PENTAHO_BUSINESS_ANALYST_USERNAME, PENTAHO_BUSINESS_ANALYST_PASSWORD);
        DashBoardPage dashboardPage = loginAs(businessAnalystsUserName, UNAME_PASSWORD);
        AdhocAnalyzerPage adhocAnalyzerPage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzerPage.getPageTitle(), CUSTOM_REPORTS); 
        
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzerPage.clickOnOpenReportButton();
        createEditAdhocReportPage.clickOnExistingReport(reportName);
        createEditAdhocReportPage.clickOnDeleteReportButton();
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();
        createEditAdhocReportPage = adhocAnalyzerPage.clickOnOpenReportButton();
        String existingReportName = createEditAdhocReportPage.getExistingReportName(reportName);       
        Assert.assertTrue("".equals(existingReportName));

        logout(drone);
    }
    
    /**
     * Deletes site report
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "testDeleteReport")
    public void testDeleteSiteReport() throws Exception
    {
        //DashBoardPage dashboardPage = loginAs(PENTAHO_BUSINESS_ANALYST_USERNAME, PENTAHO_BUSINESS_ANALYST_PASSWORD);
        DashBoardPage dashboardPage = loginAs(businessAnalystsUserName, UNAME_PASSWORD);
        AdhocAnalyzerPage adhocAnalyzerPage = dashboardPage.getNav().selectAnalyzeSite().render();
        Assert.assertEquals(adhocAnalyzerPage.getPageTitle(), CUSTOM_SITE_REPORTS); 
        
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzerPage.clickOnOpenReportButton();
        createEditAdhocReportPage.clickOnExistingReport(siteReportName);
        createEditAdhocReportPage.clickOnDeleteReportButton();
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();
        
        createEditAdhocReportPage = adhocAnalyzerPage.clickOnOpenReportButton();
        createEditAdhocReportPage.getExistingReportName(siteReportName);
        String existingReportName = createEditAdhocReportPage.getExistingReportName(siteReportName);
        
        Assert.assertTrue("".equals(existingReportName));
              
        logout(drone);        
        
        
    }
    
   }
