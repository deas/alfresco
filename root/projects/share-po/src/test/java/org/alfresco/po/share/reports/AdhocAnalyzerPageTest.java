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
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.AdhocAnalyzerDashlet;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
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

    private static final String CUSTOM_REPORTS = "Custom Reports";
    private static final String UNSAVED_REPORT = "Unsaved Report";
    private static final String PENTAHO_BUSINESS_ANALYST_USERNAME = "pentahoBusinessAnalyst";
    private static final String PENTAHO_BUSINESS_ANALYST_PASSWORD = "pentahoBusinessAnalyst";
    
    private static String reportName = null;

    private SharePage page = null;
    private AdhocAnalyzerPage adhocAnalyzePage = null;
    private CreateEditAdhocReportPage createEditAdhocReportPage = null;
    
    private String userName = "User_" + System.currentTimeMillis();
    private String siteName = "Site_" + System.currentTimeMillis();
    private String folderName = "Folder_" + System.currentTimeMillis();
    private String fileName = "File_" + System.currentTimeMillis();
    

    @Test
    public void loadFiles() throws Exception
    {
 
        //drop and recreate schemas
        schemasSetup();
        
        //create some users, sites and content first
        userShareInteractions();
        
        //run fact generation
        factTableGeneration();
        
        page = loginAs(PENTAHO_BUSINESS_ANALYST_USERNAME, PENTAHO_BUSINESS_ANALYST_PASSWORD); 
        adhocAnalyzePage = page.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_REPORTS);

    }
    
    private void userShareInteractions() throws Exception
    {
 
        //create user
        createEnterpriseUser(userName);

        //login as created user
        page = loginAs(userName, UNAME_PASSWORD);
        
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
                proc = Runtime.getRuntime().exec("C:/Users/jcule/Desktop/SchemasSetup.bat");
            } else if (SystemUtils.IS_OS_LINUX) {
                proc = Runtime.getRuntime().exec("C:/Users/jcule/Desktop/SchemasSetup.sh");
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
                proc = Runtime.getRuntime().exec("C:/Users/jcule/Desktop/FactTableGeneration.bat");
            } else if (SystemUtils.IS_OS_LINUX) {
                proc = Runtime.getRuntime().exec("C:/Users/jcule/Desktop/FactTableGeneration.sh");
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
     * Check if Adhoc Analyze page title is displayed correctly and test for Create Content, Users, Activities button
     */
    @Test(dependsOnMethods = "loadFiles")
    public void testAnalyzeAndContentUsersActivitiesButton() throws Exception
    {
        adhocAnalyzePage.clickOnAnalyzeButton();
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
    }

    /**
     * Test for Create, Save and Open saved report
     */
    
    @Test(dependsOnMethods = "testAnalyzeAndContentUsersActivitiesButton")
    public void testCreateSaveOpenEditReport()
    {
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

    }
    

    
    /**
     * Verifies that existing adhoc report can be opened in Adhoc Analyzer dashlet 
     */
    
    @Test(dependsOnMethods = "testCreateSaveOpenEditReport")
    public void testOpenReportInDashlet()
    {
        DashBoardPage dashboardPage = (DashBoardPage)createEditAdhocReportPage.getNav().selectMyDashBoard().render();
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
    
   }
