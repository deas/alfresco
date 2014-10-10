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

package org.alfresco.share.reports;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.Navigation;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.AdhocAnalyzerDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.reports.AdhocAnalyzerPage;
import org.alfresco.po.share.reports.CreateEditAdhocReportPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.po.thirdparty.pentaho.PentahoUserConsolePage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserReports;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Adhoc Analyzer tests
 * 
 * @author jcule
 */

@Listeners(FailedTestListener.class)
public class AdhocAnalyzerTest extends AbstractUtils
{

    private static final Logger logger = Logger.getLogger(AdhocAnalyzerTest.class);

    private static String testPassword = DEFAULT_PASSWORD;
    protected String testUser;
    protected String siteName = "";
    private static final String CUSTOM_REPORTS = "Custom Reports";
    private static final String CUSTOM_SITE_REPORTS = "Custom Site Reports";
    private static final String UNSAVED_REPORT = "Unsaved Report";
    private static final String PIE_CHART_TYPE = "pie";
    private static final String AREA_CHART_TYPE = "area";
    
    private static final String PENTAHO_BUSINESS_ANALYST_USERNAME = "pentahoBusinessAnalyst";
    private static final String PENTAHO_BUSINESS_ANALYST_PASSWORD = "pentahoBusinessAnalyst";
    private static final String PENTAHO_BUSINESS_ANALYSTS_GROUP = "ANALYTICS_BUSINESS_ANALYSTS";
    
    private static final String COMMENT_CREATED = "activity.org.alfresco.comments.comment-created";
    private static final String FILE_ADDED = "activity.org.alfresco.documentlibrary.file-added";
    private static final String FILE_CREATED = "activity.org.alfresco.documentlibrary.file-created";
    private static final String FILE_DELETED = "activity.org.alfresco.documentlibrary.file-deleted";
    private static final String FILE_LIKED = "activity.org.alfresco.documentlibrary.file-liked";
    private static final String FILE_PREVIEWED = "activity.org.alfresco.documentlibrary.file-previewed";
    private static final String FOLDER_ADDED = "activity.org.alfresco.documentlibrary.folder-added";
    private static final String FOLDER_DELETED = "activity.org.alfresco.documentlibrary.folder-deleted";
    private static final String INLINE_EDIT = "activity.org.alfresco.documentlibrary.inline-edit";
    private static final String USER_JOINED = "activity.org.alfresco.site.user-joined";
    private static final String USER_LEFT = "activity.org.alfresco.site.user-left";
    private static final String USER_ROLE_CHANGED = "activity.org.alfresco.site.user-role-changed";
    private static final String ACTIVITY_QUICKSHARE = "activity.quickshare";
    private static final String SITE_CREATE = "site.create";
    private static final String USER_LOGIN = "login";
    private static final String USER_CREATED = "user.create";

  
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }
    
    
    public void schemasSetup() throws Exception
    {
        try 
        {
            //drop stagedmsg, dim and fact tables and recreating them
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
    
    
    public void factTableGeneration() throws Exception
    {
        try 
        {
            //transfers data from stagedmsg into fact and dim tables
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
     * Creates new test user 
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16006() throws Exception
    {
        String testUser = "user16006";

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Created user logs out
        ShareUser.logout(drone);
    }

    /**
     * 1) Test user logs into share
     * 2) Verify test user is logged into share and cannot see Reporting in the header bar
     * 3) Pentaho user console page opened
     * 4) Verify test user is logged into pentaho user console and cannot create reports
     * 5) Pentaho business analyst logs into share
     * 6) Verify pentaho business analyst can see reporting, Custom Reports page,
     * click on Analyze button and Content, Users and Activities
     * 7) Verify Adhoc Analyzer iframe is displayed
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16006() throws Exception
    {

        // Login as created user
        String testUser = "user16006";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // verify test user cannot see Reporting menu - verify they cannot create reports in share
        Navigation navigation = dashboardPage.getNav();

        try
        {
            navigation.isReportingVisible();
            Assert.assertTrue(false, "Above line should have thrown page exception");
        }
        catch (PageException e)
        {
            Assert.assertTrue(e.getMessage().startsWith("Unable to find Reporting menu in the header."));
        }

        // open pentaho console url
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), testUser);

        // verify test user cannot create reports
        pentahoUserConsolePage.clickOnFileMenu();
        try
        {
            pentahoUserConsolePage.isNewDisplayed();
            Assert.assertTrue(false, "Above line should have thrown page exception");
        }
        catch (PageException e)
        {
            Assert.assertTrue(e.getMessage().startsWith("Unable to find New Menu."));
        }

        // tests user logs out of share
        drone.navigateTo(shareUrl);
        ShareUser.logout(drone);

        // pentaho business analyst logs into share
        dashboardPage = (DashBoardPage) ShareUser.login(drone, PENTAHO_BUSINESS_ANALYST_USERNAME, PENTAHO_BUSINESS_ANALYST_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // penatho business analyst can see reporting menu
        navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());

        // penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_REPORTS);

        // penatho business analyst can see Adhoc Analyze page and click on Analyze button and Content, Users and Activities button
        adhocAnalyzePage.clickOnAnalyzeButton();
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
        
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);


    }
    
    
    /**
     * Creates new test user member of pentaho business analyst group
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16007() throws Exception
    {
        String testUser = "user16007";

        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();
        
        dashboardPage = (DashBoardPage) ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
 
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
       
    }   
    
    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify Pentaho business analyst is logged into share and can see Reporting in the header bar
     * 3) Click on the Analytics menu in the header bar
     * 4) Select Analyze from the dropdown
     * 5) Create new  report and save it
     * 6) Click on the Open button on Custom Reports page and verify the saved report name is displayed in the dropdown
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16007() throws Exception
    {

        // Login as created user
        String testUser = "user16007";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        
        // penatho business analyst can see reporting menu
        Navigation navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());

        // penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_REPORTS);
        
        adhocAnalyzePage.clickOnAnalyzeButton();
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        Assert.assertTrue(adhocAnalyzePage.isOpenButtonDisplayed());

        //create new report
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();  
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
        
        // click on Save button to save created report
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        String testName = getTestName();
        String reportName = "Report-" + testName;
        createEditAdhocReportPage.enterAnalisysName(reportName);

        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();
        
        //click on open button to open saved report
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        Assert.assertEquals(reportName, createEditAdhocReportPage.getExistingReportName(reportName));
        
    }    
    
    /**
     * Creates new test user member of pentaho business analyst group
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16008() throws Exception
    {
        String testUser = "user16008";

        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();
        
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
        
    }   
        

    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify Pentaho business analyst is logged into share and can see Reporting in the header bar
     * 3) Click on the Analytics menu in the header bar
     * 4) Select Analyze from the dropdown
     * 5) Verify Custom Reports page is displayed and click on Open button
     * 6) Verify pentaho business analyst can see reporting, Custom Reports page,
     * click on Open button
     * 7) Verify (There are no analyses) message is displayed
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16008() throws Exception
    {
        // Login as created user
        String testUser = "user16008";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // penatho business analyst can see reporting menu
        Navigation navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());

        // penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_REPORTS);

        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();  
        Assert.assertTrue(createEditAdhocReportPage.isThereAreNoAnalysesDisplayed());
    
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);

    }
    
    
    /**
     * Creates new test user member of pentaho business analyst group
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16009() throws Exception
    {
        String testUser = "user16009";

        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();

        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
        
    }   
    
    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify Pentaho business analyst is logged into share and can see Reporting in the header bar
     * 3) Click on the Analytics menu in the header bar
     * 4) Select Analyze from the dropdown
     * 5) Create new  report and save it
     * 6) Click on the Open button on Custom Reports page and verify the saved report name is displayed in the dropdown
     * 7) Click on the saved report name in the dropdown and verify report is displayed correctly with correct data
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16009() throws Exception
    {
        // Login as created user
        String testUser = "user16009";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        
        //drop schemas
        schemasSetup();
        
        //some share activity here
        ShareUserReports.userShareInteractions(drone, testUser);
        
        factTableGeneration();
               
        ShareUser.logout(drone);
        dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
 
        // penatho business analyst can see reporting menu
        Navigation navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());

        // penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_REPORTS);
          
        adhocAnalyzePage.clickOnAnalyzeButton();
        
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        Assert.assertTrue(adhocAnalyzePage.isOpenButtonDisplayed());
              
        //create new report
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();  
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
        
        // click on Save button to save created report
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        String testName = getTestName();
        String reportName = "Report-" + testName;
        createEditAdhocReportPage.enterAnalisysName(reportName);

        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();

        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());

        //click on open button to open saved report
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        Assert.assertEquals(reportName, createEditAdhocReportPage.getExistingReportName(reportName));
        
        createEditAdhocReportPage.clickOnExistingReport(reportName);
       
        createEditAdhocReportPage.getReportTitle();
        
        String [] tableStatusBarElements = createEditAdhocReportPage.getTableStatusBar();
        
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
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
                
        
        //change chart type
        createEditAdhocReportPage.clickOnChangeChartType();

        //select pie chart
        createEditAdhocReportPage.clickOnPieChartType();

        Assert.assertTrue(createEditAdhocReportPage.isPieChartEventsDisplayed());
              
        //get tooltip data
        List<String> tooltipData = createEditAdhocReportPage.getTooltipData(false, PIE_CHART_TYPE);
        Assert.assertEquals(tooltipData.size(), 16);
         
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
       
    }        
    
    
    
    /**
     * Creates new test user member of pentaho business analyst group
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16046() throws Exception
    {
        String testUser = "user16046";

        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();
        
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
        
    }   
    
    
    /**
     * Creates new test user member of pentaho business analyst group
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16045() throws Exception
    {
        String testUser = "user16045";

        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();

        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
        
    }   
    
    
    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify Pentaho business analyst is logged into share and can see Reporting in the header bar
     * 3) Click on the Analytics menu in the header bar
     * 4) Select Analyze from the dropdown
     * 5) Create new  report and save it
     * 6) Verify report is displayed correctly
     * 7) Change the chart type from table to pie chart and save it
     * 8) Add the Custom Reports dashlet to the user dashboard
     * 9) Verify that the chart in the dashlet is displayed correctly (right chart type and correct data)
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16045() throws Exception
    {
        // Login as created user
        String testUser = "user16045";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        
        //drop schemas
        schemasSetup();
        
        //some share activity here
        ShareUserReports.userShareInteractions(drone, testUser);
        
        factTableGeneration();
               
        ShareUser.logout(drone);
        dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
 
        // penatho business analyst can see reporting menu
        Navigation navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());

        // penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_REPORTS);
          
        adhocAnalyzePage.clickOnAnalyzeButton();
        
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        Assert.assertTrue(adhocAnalyzePage.isOpenButtonDisplayed());
              
        //create new report
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();  
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
        
        // click on Save button to save created report
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        String testName = getTestName();
        String reportName = "Report-" + testName;
        createEditAdhocReportPage.enterAnalisysName(reportName);

        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();

        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());

        //click on open button to open saved report
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        Assert.assertEquals(reportName, createEditAdhocReportPage.getExistingReportName(reportName));
        
        //boolean isReportDisplayed = reportName.equals(createEditAdhocReportPage.getReportTitle());
        createEditAdhocReportPage.clickOnExistingReport(reportName);
        
        createEditAdhocReportPage.getReportTitle();
        
        String [] tableStatusBarElements = createEditAdhocReportPage.getTableStatusBar();
        
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
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
                      
        //change chart type
        createEditAdhocReportPage.clickOnChangeChartType();

        //select pie chart
        //createEditAdhocReportPage.clickOnPieChartType();
        createEditAdhocReportPage.clickOnAreaChartType();
        
        //save report again
        createEditAdhocReportPage.clickOnSaveReportButton();
                
        //customise the user dashoard
        dashboardPage = (DashBoardPage)createEditAdhocReportPage.getNav().selectMyDashBoard().render();
        CustomiseUserDashboardPage customiseUserDashboardPage = dashboardPage.getNav().selectCustomizeUserDashboard().render();
        
        dashboardPage = customiseUserDashboardPage.addDashlet(Dashlets.ADHOC_ANALYZER, 2).render();
        AdhocAnalyzerDashlet adhocAnalyzerDashlet = dashboardPage.getDashlet("adhoc-analyzer").render();
        
        Assert.assertTrue(adhocAnalyzerDashlet.isTitleDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isOpenDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isDashletMessageDisplayed());
        
        adhocAnalyzerDashlet.clickOnOpenDropdown();
        adhocAnalyzerDashlet.clickOnExistingReport(reportName);
        Assert.assertEquals(adhocAnalyzerDashlet.getDashletTitle(), reportName);
 
        createEditAdhocReportPage.isPieChartEventsDisplayed();
        
        //get tooltip data
        //List<String> tooltipData = createEditAdhocReportPage.getTooltipData(false, PIE_CHART_TYPE );
        List<String> tooltipData = createEditAdhocReportPage.getTooltipData(false, AREA_CHART_TYPE );
        

        //expected data
        String commentCreated = COMMENT_CREATED + ":2";
        String fileAdded = FILE_ADDED + ":1";
        String fileCreated = FILE_CREATED + ":1";
        String fileDeleted = FILE_DELETED + ":1";
        //String fileDeleted = FILE_DELETED + ":2";
        String fileLiked = FILE_LIKED + ":1";
        String filePreviewed = FILE_PREVIEWED + ":2";
        String folderAdded = FOLDER_ADDED + ":1";
        String folderDeleted = FOLDER_DELETED + ":1";
        String inlineEdit = INLINE_EDIT + ":1";
        String userJoined = USER_JOINED + ":1";
        String userLeft = USER_LEFT + ":1";
        String userRoleChanged = USER_ROLE_CHANGED + ":1";
        String activityQuickshare = ACTIVITY_QUICKSHARE + ":1";
        String siteCreate = SITE_CREATE + ":1";
        String userLogin = USER_LOGIN + ":5";
        String userCreated = USER_CREATED + ":1";

       //verify chart type and data here
        
        // 2 x activity.org.alfresco.comments.comment-created
        // 1 x activity.org.alfresco.documentlibrary.file-added
        // 1 x activity.org.alfresco.documentlibrary.file-created
        // 1 x activity.org.alfresco.documentlibrary.file-deleted
        // 1 x activity.org.alfresco.documentlibrary.file-liked
        // 2 x activity.org.alfresco.documentlibrary.file-previewed
        // 1 x activity.org.alfresco.documentlibrary.folder-added
        // 1 x activity.org.alfresco.documentlibrary.folder-deleted
        // 1 x activity.org.alfresco.documentlibrary.inline-edit
        // 1 x activity.quickshare
        // 1 x site.create
        // 1 x activity.org.alfresco.site.user-created --- ?????? missing currently
        // 1 x activity.org.alfresco.site.user-joined
        // 1 x activity.org.alfresco.site.user-left
        // 1 x activity.org.alfresco.site.user-role-changed
        // 5 x login --- ?????? missing currently
        
        //Assert.assertEquals(tooltipData.size(), 15);
        Assert.assertEquals(tooltipData.size(), 16);
        
        Assert.assertTrue(tooltipData.contains(commentCreated));
        Assert.assertTrue(tooltipData.contains(fileAdded));
        Assert.assertTrue(tooltipData.contains(fileCreated));
        Assert.assertTrue(tooltipData.contains(fileDeleted));
        Assert.assertTrue(tooltipData.contains(fileLiked));
        Assert.assertTrue(tooltipData.contains(filePreviewed));
        Assert.assertTrue(tooltipData.contains(folderAdded));
        Assert.assertTrue(tooltipData.contains(folderDeleted));
        Assert.assertTrue(tooltipData.contains(inlineEdit));
        Assert.assertTrue(tooltipData.contains(userJoined));
        Assert.assertTrue(tooltipData.contains(userLeft));
        Assert.assertTrue(tooltipData.contains(userRoleChanged));
        Assert.assertTrue(tooltipData.contains(userLogin));
        Assert.assertTrue(tooltipData.contains(userCreated));
        Assert.assertTrue(tooltipData.contains(activityQuickshare));
        Assert.assertTrue(tooltipData.contains(siteCreate));
  
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
       
    }        
    

    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify pentaho business analyst dashboard is displayed and add Adhoc Analyzer dashlet to the user dashboard
     * 3) Click on the Open drop down menu on the dashlet
     * 4) Verify appropriate message is displayed showing there are no existing reports:“(There are no analyses)”
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16046() throws Exception
    {
        // Login as created user
        String testUser = "user16046";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        // pentaho business analyst adds Analyzer Report dashlet to the user dashboard
        CustomiseUserDashboardPage customiseUserDashboardPage = dashboardPage.getNav().selectCustomizeUserDashboard().render();
        dashboardPage = customiseUserDashboardPage.addDashlet(Dashlets.ADHOC_ANALYZER, 2).render();
        AdhocAnalyzerDashlet adhocAnalyzerDashlet = dashboardPage.getDashlet("adhoc-analyzer").render();
        Assert.assertTrue(adhocAnalyzerDashlet.isTitleDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isOpenDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isDashletMessageDisplayed());

        // pentaho business analyst clicks on the Open drop down menu on the dashlet
        adhocAnalyzerDashlet.clickOnOpenDropdown();

        // verify appropriate message is displayed showing there are no existing reports:“(There are no analyses)”
        Assert.assertTrue(adhocAnalyzerDashlet.isThereAreNoAnalysesDisplayed());
        
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);

    }
    
    
    /**
     * Creates new test user member of pentaho business analyst group
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16142() throws Exception
    {
        String testUser = "user16142";

        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();

        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
        
    }   
    
    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify Pentaho business analyst is logged into share and can see Reporting in the header bar
     * 3) Click on the Analytics menu in the header bar
     * 4) Select Analyze from the dropdown
     * 5) Create new  report and save it
     * 6) Verify report is displayed correctly
     * 7) Add an additional field - day and verify the chart is updated
     * 7) Change the chart type from table to pie chart and save it
     * 8) Add the Custom Reports dashlet to the user dashboard
     * 9) Verify that the chart in the dashlet is displayed correctly (additional field, right chart type and correct data)
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16142() throws Exception
    {
        // Login as created user
        String testUser = "user16142";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //drop schemas
        schemasSetup();
        
        //some share activity here
        ShareUserReports.userShareInteractions(drone, testUser);
        
        factTableGeneration();
               
        ShareUser.logout(drone);
        dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
 
        // penatho business analyst can see reporting menu
        Navigation navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());

        // penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_REPORTS);
          
        adhocAnalyzePage.clickOnAnalyzeButton();
        
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        Assert.assertTrue(adhocAnalyzePage.isOpenButtonDisplayed());
              
        //create new report
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();  
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
        
        // click on Save button to save created report
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        String testName = getTestName();
        String reportName = "Report-" + testName;
        createEditAdhocReportPage.enterAnalisysName(reportName);

        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();

        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());

        //click on open button to open saved report
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        Assert.assertEquals(reportName, createEditAdhocReportPage.getExistingReportName(reportName));
        
        createEditAdhocReportPage.clickOnExistingReport(reportName);
        createEditAdhocReportPage.getReportTitle();
        
        String [] tableStatusBarElements = createEditAdhocReportPage.getTableStatusBar();
        
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
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
                       
        //add additional field to the report and save it 
        createEditAdhocReportPage.doubleClickOnDayField();
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        tableStatusBarElements = createEditAdhocReportPage.getTableStatusBar();
        
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
        
        
        //change chart type
        createEditAdhocReportPage.clickOnChangeChartType();

        //select pie chart
        createEditAdhocReportPage.clickOnPieChartType();
        
        //save report again
        createEditAdhocReportPage.clickOnSaveReportButton();
                
        //customise the user dashoard
        dashboardPage = (DashBoardPage)createEditAdhocReportPage.getNav().selectMyDashBoard().render();
        CustomiseUserDashboardPage customiseUserDashboardPage = dashboardPage.getNav().selectCustomizeUserDashboard().render();
        
        dashboardPage = customiseUserDashboardPage.addDashlet(Dashlets.ADHOC_ANALYZER, 2).render();
        AdhocAnalyzerDashlet adhocAnalyzerDashlet = dashboardPage.getDashlet("adhoc-analyzer").render();
        Assert.assertTrue(adhocAnalyzerDashlet.isTitleDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isOpenDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isDashletMessageDisplayed());
        adhocAnalyzerDashlet.clickOnOpenDropdown();
        adhocAnalyzerDashlet.clickOnExistingReport(reportName);
        Assert.assertEquals(adhocAnalyzerDashlet.getDashletTitle(), reportName);
         
        createEditAdhocReportPage.isPieChartEventsDisplayed();
        
        //get tooltip data
        List<String> tooltipData = createEditAdhocReportPage.getTooltipData(true, PIE_CHART_TYPE);

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
       

        Assert.assertEquals(tooltipData.size(), 16);
        
        for (String tooltip : tooltipData)
        {
            Assert.assertTrue(tooltip.contains(date));
        }
 
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
       
    }        
    
    
    /**
     * Creates new test user member of pentaho business analyst group
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16143() throws Exception
    {
        String testUser = "user16143";

        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();

        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
        
    }      


    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify Pentaho business analyst is logged into share and can see Reporting in the header bar
     * 3) Click on the Analytics menu in the header bar
     * 4) Select Analyze from the dropdown
     * 5) Create new  report and save it
     * 6) Verify report is displayed correctly
     * 7) Change the chart type from table to area chart and save it
     * 8) Add the Custom Reports dashlet to the user dashboard
     * 9) Verify that the chart in the dashlet is displayed correctly (right chart type and correct data)
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16143() throws Exception
    {
        // Login as created user
        String testUser = "user16143";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        
        //drop schemas
        schemasSetup();
        
        //some share activity here
        ShareUserReports.userShareInteractions(drone, testUser);
        
        factTableGeneration();
               
        ShareUser.logout(drone);
        dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
 
        // penatho business analyst can see reporting menu
        Navigation navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());

        // penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_REPORTS);
          
        adhocAnalyzePage.clickOnAnalyzeButton();
        
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        Assert.assertTrue(adhocAnalyzePage.isOpenButtonDisplayed());
              
        //create new report
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();  
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
        
        // click on Save button to save created report
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        String testName = getTestName();
        String reportName = "Report-" + testName;
        createEditAdhocReportPage.enterAnalisysName(reportName);

        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();

        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());

        //click on open button to open saved report
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        Assert.assertEquals(reportName, createEditAdhocReportPage.getExistingReportName(reportName));
        
        createEditAdhocReportPage.clickOnExistingReport(reportName);
        createEditAdhocReportPage.getReportTitle();
        
        String [] tableStatusBarElements = createEditAdhocReportPage.getTableStatusBar();
        
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
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
                      
        //change chart type
        createEditAdhocReportPage.clickOnChangeChartType();

        //select pie chart
        createEditAdhocReportPage.clickOnAreaChartType();
        
        //save report again
        createEditAdhocReportPage.clickOnSaveReportButton();
                
        //customise the user dashoard
        dashboardPage = (DashBoardPage)createEditAdhocReportPage.getNav().selectMyDashBoard().render();
        CustomiseUserDashboardPage customiseUserDashboardPage = dashboardPage.getNav().selectCustomizeUserDashboard().render();
        
        dashboardPage = customiseUserDashboardPage.addDashlet(Dashlets.ADHOC_ANALYZER, 2).render();
        AdhocAnalyzerDashlet adhocAnalyzerDashlet = dashboardPage.getDashlet("adhoc-analyzer").render();
        Assert.assertTrue(adhocAnalyzerDashlet.isTitleDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isOpenDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isDashletMessageDisplayed());
        adhocAnalyzerDashlet.clickOnOpenDropdown();
        adhocAnalyzerDashlet.clickOnExistingReport(reportName);
        Assert.assertEquals(adhocAnalyzerDashlet.getDashletTitle(), reportName);
        
        createEditAdhocReportPage.isAreaChartTextDisplayed();
 
        //get tooltip data
        List<String> tooltipData = createEditAdhocReportPage.getTooltipData(false, AREA_CHART_TYPE);

        //expected data
        String commentCreated = COMMENT_CREATED + ":2";
        String fileAdded = FILE_ADDED + ":1";
        String fileCreated = FILE_CREATED + ":1";
        String fileDeleted = FILE_DELETED + ":1";
        String fileLiked = FILE_LIKED + ":1";
        String filePreviewed = FILE_PREVIEWED + ":2";
        String folderAdded = FOLDER_ADDED + ":1";
        //String folderDeleted = FOLDER_DELETED + ":2";
        String folderDeleted = FOLDER_DELETED + ":1";
        String inlineEdit = INLINE_EDIT + ":1";
        String userJoined = USER_JOINED + ":1";
        String userLeft = USER_LEFT + ":1";
        String userRoleChanged = USER_ROLE_CHANGED + ":1";
        String activityQuickshare = ACTIVITY_QUICKSHARE + ":1";
        String siteCreate = SITE_CREATE + ":1";
        String userLogin = USER_LOGIN + ":5";
        String userCreated = USER_CREATED + ":1";

        //verify chart type and data here
        
        // 2 x activity.org.alfresco.comments.comment-created
        // 1 x activity.org.alfresco.documentlibrary.file-added
        // 1 x activity.org.alfresco.documentlibrary.file-created
        // 1 x activity.org.alfresco.documentlibrary.file-deleted
        // 1 x activity.org.alfresco.documentlibrary.file-liked
        // 2 x activity.org.alfresco.documentlibrary.file-previewed
        // 1 x activity.org.alfresco.documentlibrary.folder-added
        // 1 x activity.org.alfresco.documentlibrary.folder-deleted
        // 1 x activity.org.alfresco.documentlibrary.inline-edit
        // 1 x activity.quickshare
        // 1 x site.create
        // 1 x activity.org.alfresco.site.user-created
        // 1 x activity.org.alfresco.site.user-joined
        // 1 x activity.org.alfresco.site.user-left
        // 1 x activity.org.alfresco.site.user-role-changed
        // 5 x login --- ?????? missing currently
        
        Assert.assertEquals(tooltipData.size(), 16);
        
        Assert.assertTrue(tooltipData.contains(commentCreated));
        Assert.assertTrue(tooltipData.contains(fileAdded));
        Assert.assertTrue(tooltipData.contains(fileCreated));
        Assert.assertTrue(tooltipData.contains(fileDeleted));
        Assert.assertTrue(tooltipData.contains(fileLiked));
        Assert.assertTrue(tooltipData.contains(filePreviewed));
        Assert.assertTrue(tooltipData.contains(folderAdded));
        Assert.assertTrue(tooltipData.contains(folderDeleted));
        Assert.assertTrue(tooltipData.contains(inlineEdit));
        Assert.assertTrue(tooltipData.contains(userJoined));
        Assert.assertTrue(tooltipData.contains(userLeft));
        Assert.assertTrue(tooltipData.contains(userRoleChanged));
        Assert.assertTrue(tooltipData.contains(userLogin));
        Assert.assertTrue(tooltipData.contains(userCreated));
        Assert.assertTrue(tooltipData.contains(activityQuickshare));
        Assert.assertTrue(tooltipData.contains(siteCreate));
 
         
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
       
    }        
    
    
    /**
     * Creates new test user
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16144() throws Exception
    {
        String testUser = "user16144";

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        // Created user logs out
        ShareUser.logout(drone);
    }

    /**
     * 1) Test user logs into share
     * 2) Verify test user is logged into share and cannot add Custom Reports dashlet to the user dashboard
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16144() throws Exception
    {

        // Login as created user
        String testUser = "user16144";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        CustomiseUserDashboardPage customiseUserDashboardPage = dashboardPage.getNav().selectCustomizeUserDashboard().render();

        // verify user can't customize the site dasboard
        try
        {
            customiseUserDashboardPage.addDashlet(Dashlets.ADHOC_ANALYZER, 2).render();
            Assert.assertTrue(false, "Above line should have thrown page exception");
        }
        catch (PageOperationException e)
        {
            Assert.assertTrue(e.getMessage().startsWith("Error in adding dashlet using drag and drop"));
            ShareUser.logout(drone);

        }
        
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);

    }
  
    /**
     * 1) Alfresco user created
     * 2) Created user creates site
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16508() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        // Create test user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        
        // Login as created test user
        ShareUser.login(drone, testUser, testPassword);

        // test user creates site
        SiteUtil.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PRIVATE);
        
        //test user logs out
        ShareUser.logout(drone);
    }    
    
    /**
     * 1) Test user (that is not business analyst) logs in
     * 2) Verify test user cannot have Hot Content Report dashlet on the site dashboard
     * 3) Verify test user cannot have User Activity Report dashlet on the site dashboard
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16508() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        
        try
        {        
            ShareUserDashboard.addDashlet(drone, siteName, Dashlets.HOT_CONTENT_REPORT);
            Assert.assertTrue(false, "Above line should have thrown page exception");
        } catch (PageOperationException e)
        {
            Assert.assertTrue(e.getMessage().startsWith( "Error in adding dashlet using drag and drop"));
            ShareUser.logout(drone);

        } 
        
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        
        try
        {        
            ShareUserDashboard.addDashlet(drone, siteName, Dashlets.USER_ACTIVITY_REPORT);
            Assert.assertTrue(false, "Above line should have thrown page exception");
        } catch (PageOperationException e)
        {
            Assert.assertTrue(e.getMessage().startsWith( "Error in adding dashlet using drag and drop"));
            ShareUser.logout(drone);

        } 
 
    }
    
    /**
     * 1) Pentaho business analust created
     * 2) Alfresco user (site creator) created
     * 3) Alfresco user creates a site
     *     
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16509() throws Exception
    {
        String testUser = "user16509";

        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();

        ShareUser.navigateToPage(drone, shareUrl).render();
        

        // Create new user - site manager
        String testName = getTestName();
        String testUser1 = getUserNameForDomain(testName, DOMAIN_FREE);
        String[] testUserInfo = new String[] { testUser1 };
        String siteName = getSiteName(testName);
        
        ShareUser.logout(drone);
        
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        
        // Login as created site manager user
        ShareUser.login(drone, testUser1, testPassword);

        //site manager user creates site
        SiteUtil.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //site user logs out
        ShareUser.logout(drone);
        
    }     
    
    /**
     * 1) Business analyst logs in
     * 2) Verifies business analyst cannot customise the site dashboard for the site he is not site manager of
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16509() throws Exception
    {
        //Login as business analyst but not site manager
        String testUser = "user16509";
        ShareUser.login(drone, testUser, testPassword).render();        
        String testName = getTestName();
        String siteName = getSiteName(testName);

        SharePage page = ShareUser.getSharePage(drone);
        SiteFinderPage siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = SiteUtil.searchSiteWithRetry(drone, siteName, true);
        SiteDashboardPage siteDashoardPage = siteFinder.selectSite(siteName).render();
        
        //verify user can't customize the site dasboard
        try
        {
            siteDashoardPage.getSiteNav().selectCustomizeSite();
            Assert.assertTrue(false, "Above line should have thrown page exception");
        }
        catch (NoSuchElementException e)
        {
            Assert.assertTrue(e.getMessage().startsWith( "Unable to locate element:"));
            ShareUser.logout(drone);

        }
        
    }
    
    /**
     * Creates business analyst test user
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16510() throws Exception
    {
        String testUser = "user16510";
        
        String testName = getTestName();
        String siteName = getSiteName(testName);

        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();
      
        //pentaho business analyst creates a site      
        ShareUser.navigateToPage(drone, shareUrl).render();
        
        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, testPassword).render();
        
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //site user logs out
        ShareUser.logout(drone);
        
    }     
    
    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify pentaho business analyst dashboard is displayed and add Adhoc Analyzer dashlet to the site dashboard
     * 3) Click on the Open drop down menu on the dashlet
     * 4) Verify appropriate message is displayed showing there are no existing reports:“(There are no analyses)”    
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16510() throws Exception
    {
        //Login as business analyst 
        String testUser = "user16510";
        ShareUser.login(drone, testUser, testPassword).render();        
        String testName = getTestName();
        String siteName = getSiteName(testName);
                
        // Login as created user
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        
        SharePage page = ShareUser.getSharePage(drone);
        SiteFinderPage siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = SiteUtil.searchSiteWithRetry(drone, siteName, true);
        SiteDashboardPage siteDashboardPage = siteFinder.selectSite(siteName).render();
       
        //customise the site dashboard
        siteDashboardPage = ShareUserDashboard.addDashlet(drone, siteName, Dashlets.CUSTOM_SITE_REPORTS);
        AdhocAnalyzerDashlet adhocAnalyzerDashlet = siteDashboardPage.getDashlet("adhoc-analyzer").render();

        Assert.assertTrue(adhocAnalyzerDashlet.isTitleDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isOpenDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isSiteDashletMessageDisplayed());

        // pentaho business analyst clicks on the Open drop down menu on the dashlet
        adhocAnalyzerDashlet.clickOnOpenDropdown();

        // verify appropriate message is displayed showing there are no existing reports:“(There are no analyses)”
        Assert.assertTrue(adhocAnalyzerDashlet.isThereAreNoAnalysesDisplayed());
        
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);  
        
    }
    
    /**
     * 1) Creates new test user member of pentaho business analyst group
     * 2) Created user creates the site
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16511() throws Exception
    {
        String testUser = "user16511";

        String testName = getTestName();
        String siteName = getSiteName(testName);
        
        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();

        //pentaho business analyst creates a site      
        ShareUser.navigateToPage(drone, shareUrl).render();

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, testPassword).render();
        
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //site user logs out
        ShareUser.logout(drone);
        
    }   
    
    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify Pentaho business analyst is logged into share and can see Reporting in the header bar
     * 3) Click on the Analytics menu in the header bar
     * 4) Select Analyze Site from the dropdown
     * 5) Create new report and save it
     * 6) Verify report is displayed correctly
     * 7) Change the chart type from table to area chart and save it
     * 8) Add the Custom Site Report dashlet to the user dashboard
     * 9) Verify that the chart in the dashlet is displayed correctly (right chart type and correct data)
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16511() throws Exception
    {
        // Login as created user
        String testUser = "user16511";
         
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        
        //drop schemas
        schemasSetup();
        
        //some share activity here
        ShareUserReports.userShareInteractions(drone, testUser);
        
        factTableGeneration();
               
        ShareUser.logout(drone);
        dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
 
        // penatho business analyst can see reporting menu
        Navigation navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());

        // penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyzeSite().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_SITE_REPORTS);
          
        adhocAnalyzePage.clickOnAnalyzeButton();
        
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        Assert.assertTrue(adhocAnalyzePage.isOpenButtonDisplayed());
              
        //create new report
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();  
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_SITE_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
        
        // click on Save button to save created report
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String reportName = "Report-" + testName;
        createEditAdhocReportPage.enterAnalisysName(reportName);

        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();

        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());

        //click on open button to open saved report
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        Assert.assertEquals(reportName, createEditAdhocReportPage.getExistingReportName(reportName));
        
        createEditAdhocReportPage.clickOnExistingReport(reportName);
        createEditAdhocReportPage.getReportTitle();
        
        String [] tableStatusBarElements = createEditAdhocReportPage.getTableStatusBar();
        
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());
        
        
        Assert.assertEquals(tableStatusBarElements[0].trim(), "Rows:");
        Assert.assertEquals(tableStatusBarElements[3].trim(), "Cols:");
        Assert.assertTrue(Integer.parseInt(tableStatusBarElements[1].trim()) > 0);
        Assert.assertTrue(Integer.parseInt(tableStatusBarElements[4].trim()) > 0);
        
        // check that the name of the report is saved correctly
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), reportName);
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_SITE_REPORTS);
        
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
                      
        //change chart type
        createEditAdhocReportPage.clickOnChangeChartType();

        //select pie chart
        createEditAdhocReportPage.clickOnAreaChartType();
        
        //save report again
        createEditAdhocReportPage.clickOnSaveReportButton();
                
        //customise the site dashoard
        SharePage page = ShareUser.getSharePage(drone);
        SiteFinderPage siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = SiteUtil.searchSiteWithRetry(drone, siteName, true);
        SiteDashboardPage siteDashboardPage = siteFinder.selectSite(siteName).render();
        
        siteDashboardPage = ShareUserDashboard.addDashlet(drone, siteName, Dashlets.CUSTOM_SITE_REPORTS);
        AdhocAnalyzerDashlet adhocAnalyzerDashlet = siteDashboardPage.getDashlet("adhoc-analyzer").render();

        Assert.assertTrue(adhocAnalyzerDashlet.isTitleDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isOpenDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isSiteDashletMessageDisplayed());
        adhocAnalyzerDashlet.clickOnOpenDropdown();
        adhocAnalyzerDashlet.clickOnExistingReport(reportName);
        Assert.assertEquals(adhocAnalyzerDashlet.getDashletTitle(), reportName);
        
        createEditAdhocReportPage.isAreaChartTextDisplayed();
 
        //get tooltip data
        List<String> tooltipData = createEditAdhocReportPage.getTooltipData(false, AREA_CHART_TYPE);

        //expected data
        String commentCreated = COMMENT_CREATED + ":2";
        String fileAdded = FILE_ADDED + ":1";
        String fileCreated = FILE_CREATED + ":1";
        String fileDeleted = FILE_DELETED + ":1";
        String fileLiked = FILE_LIKED + ":1";
        String filePreviewed = FILE_PREVIEWED + ":2";
        String folderAdded = FOLDER_ADDED + ":1";
        //String folderDeleted = FOLDER_DELETED + ":2";
        String folderDeleted = FOLDER_DELETED + ":1";
        String inlineEdit = INLINE_EDIT + ":1";
        String userJoined = USER_JOINED + ":1";
        String userLeft = USER_LEFT + ":1";
        String userRoleChanged = USER_ROLE_CHANGED + ":1";
        String activityQuickshare = ACTIVITY_QUICKSHARE + ":1";
        String siteCreate = SITE_CREATE + ":1";
        String userLogin = USER_LOGIN + ":5";
        String userCreated = USER_CREATED + ":1";

        //verify chart type and data here
        
        // 2 x activity.org.alfresco.comments.comment-created
        // 1 x activity.org.alfresco.documentlibrary.file-added
        // 1 x activity.org.alfresco.documentlibrary.file-created
        // 1 x activity.org.alfresco.documentlibrary.file-deleted
        // 1 x activity.org.alfresco.documentlibrary.file-liked
        // 2 x activity.org.alfresco.documentlibrary.file-previewed
        // 1 x activity.org.alfresco.documentlibrary.folder-added
        // 1 x activity.org.alfresco.documentlibrary.folder-deleted
        // 1 x activity.org.alfresco.documentlibrary.inline-edit
        // 1 x activity.quickshare
        // 1 x site.create
        // 1 x activity.org.alfresco.site.user-created
        // 1 x activity.org.alfresco.site.user-joined
        // 1 x activity.org.alfresco.site.user-left
        // 1 x activity.org.alfresco.site.user-role-changed
        // 5 x login --- ?????? missing currently
        
        Assert.assertEquals(tooltipData.size(), 16);
        
        Assert.assertTrue(tooltipData.contains(commentCreated));
        Assert.assertTrue(tooltipData.contains(fileAdded));
        Assert.assertTrue(tooltipData.contains(fileCreated));
        Assert.assertTrue(tooltipData.contains(fileDeleted));
        Assert.assertTrue(tooltipData.contains(fileLiked));
        Assert.assertTrue(tooltipData.contains(filePreviewed));
        Assert.assertTrue(tooltipData.contains(folderAdded));
        Assert.assertTrue(tooltipData.contains(folderDeleted));
        Assert.assertTrue(tooltipData.contains(inlineEdit));
        Assert.assertTrue(tooltipData.contains(userJoined));
        Assert.assertTrue(tooltipData.contains(userLeft));
        Assert.assertTrue(tooltipData.contains(userRoleChanged));
        Assert.assertTrue(tooltipData.contains(userLogin));
        Assert.assertTrue(tooltipData.contains(userCreated));
        Assert.assertTrue(tooltipData.contains(activityQuickshare));
        Assert.assertTrue(tooltipData.contains(siteCreate));
          
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
        
    }
    
    
    /**
     * 1) Alfresco user created
     * 2) Created user creates site
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16512() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        // Create test user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        
        // Login as created test user
        ShareUser.login(drone, testUser, testPassword);

        // test user creates site
        SiteUtil.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PRIVATE);
        
        //test user logs out
        ShareUser.logout(drone);
    } 
    
    /**
     * 1) Test user (that is not business analyst) logs in
     * 2) Verify test user cannot have Custom Site Report dashlet on the site dashboard
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16512() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        
        try
        {        
            ShareUserDashboard.addDashlet(drone, siteName, Dashlets.CUSTOM_SITE_REPORTS);
            Assert.assertTrue(false, "Above line should have thrown page exception");
        } catch (PageOperationException e)
        {
            Assert.assertTrue(e.getMessage().startsWith( "Error in adding dashlet using drag and drop"));
            ShareUser.logout(drone);

        } 
        
    }
    
    /**
     * Creates new test user member of pentaho business analyst group
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16514() throws Exception
    {
        String testUser = "user16514";

        String testName = getTestName();
        String siteName = getSiteName(testName);
        
        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();

        //pentaho business analyst creates a site      
        ShareUser.navigateToPage(drone, shareUrl).render();

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, testPassword).render();
        
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //site user logs out
        ShareUser.logout(drone);
        
    }   
  
    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify Pentaho business analyst is logged into share and can see Reporting in the header bar
     * 3) Click on the Analytics menu in the header bar
     * 4) Select Analyze from the dropdown
     * 5) Create new  report and save it
     * 6) Click on the Open button on Custom reports page and verify the saved report name is displayed in the dropdown
     * 7) Click on the saved report name in the dropdown and verify report is displayed correctly with correct data
     * 8) Click on delete button to delete report
     * 9) Verify report is deleted successfully
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16514() throws Exception
    {
        // Login as created user
        String testUser = "user16514";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        
        //drop schemas
        schemasSetup();
        
        //some share activity here
        ShareUserReports.userShareInteractions(drone, testUser);
        
        factTableGeneration();
               
        ShareUser.logout(drone);
        dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
 
        // penatho business analyst can see reporting menu
        Navigation navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());

        // penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_REPORTS);
          
        adhocAnalyzePage.clickOnAnalyzeButton();
        
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        Assert.assertTrue(adhocAnalyzePage.isOpenButtonDisplayed());
              
        //create new report
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();  
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
        
        // click on Save button to save created report
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        String testName = getTestName();
        String reportName = "Report-" + testName;
        createEditAdhocReportPage.enterAnalisysName(reportName);

        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();

        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());

        //click on open button to open saved report
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        Assert.assertEquals(reportName, createEditAdhocReportPage.getExistingReportName(reportName));
        
        createEditAdhocReportPage.clickOnExistingReport(reportName);
       
        createEditAdhocReportPage.getReportTitle();
        
        String [] tableStatusBarElements = createEditAdhocReportPage.getTableStatusBar();
        
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
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
                
        createEditAdhocReportPage.clickOnDeleteReportButton();
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        String existingReportName = createEditAdhocReportPage.getExistingReportName(reportName);       
        Assert.assertTrue("".equals(existingReportName));
        
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
       
    }        
    
    /**
     * 1) Creates new test user member of pentaho business analyst group
     * 2) Created user creates the site
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAdhocAnalyzer" })
    public void dataPrep_AdhocAnalyzer_AONE_16610() throws Exception
    {
        String testUser = "user16610";

        String testName = getTestName();
        String siteName = getSiteName(testName);
        
        //Create test user as pentaho business analyst
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, PENTAHO_BUSINESS_ANALYSTS_GROUP, testUser);

        //Created user logs into share and automatically into pentaho
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //go to pentaho user console and assign Read, Publish and Create Content permissions to created user
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();

        // verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
 
        pentahoUserConsolePage.clickOnHome();
        pentahoUserConsolePage.clickOnAdministration();
        pentahoUserConsolePage.clickOnManageRoles();
        pentahoUserConsolePage.clickOnBusinessAnalyst();
        pentahoUserConsolePage.clickOnReadContent();
        pentahoUserConsolePage.clickOnPublishContent();
        pentahoUserConsolePage.clickOnCreateContent();

        //pentaho business analyst creates a site      
        ShareUser.navigateToPage(drone, shareUrl).render();

        ShareUser.logout(drone);
        ShareUser.login(drone, testUser, testPassword).render();
        
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //site user logs out
        ShareUser.logout(drone);
        
    }   
    
    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify Pentaho business analyst is logged into share and can see Reporting in the header bar
     * 3) Click on the Analytics menu in the header bar
     * 4) Select Analyze Site from the dropdown
     * 5) Create new report and save it
     * 6) Click on the Open button on Custom Site Reports page and verify the saved report name is displayed in the dropdown
     * 7) Click on the saved report name in the dropdown and verify report is displayed correctly with correct data
     * 8) Click on delete button to delete report
     * 9) Verify report is deleted successfully
     * 
     * @throws Exception
     */
    @Test(groups = { "AdhocAnalyzerTests" })
    public void AONE_16610() throws Exception
    {
        // Login as created user
        String testUser = "user16610";
         
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        
        //drop schemas
        schemasSetup();
        
        //some share activity here
        ShareUserReports.userShareInteractions(drone, testUser);
        
        factTableGeneration();
               
        ShareUser.logout(drone);
        dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
 
        // penatho business analyst can see reporting menu
        Navigation navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());

        // penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyzeSite().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), CUSTOM_SITE_REPORTS);
          
        adhocAnalyzePage.clickOnAnalyzeButton();
        
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        Assert.assertTrue(adhocAnalyzePage.isOpenButtonDisplayed());
              
        //create new report
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();  
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_SITE_REPORTS);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
        createEditAdhocReportPage.doubleClickOnSiteNameField();
        createEditAdhocReportPage.doubleClickOnEventTypeField();
        createEditAdhocReportPage.doubleClickOnNumberOfEventsField();
        
        // click on Save button to save created report
        createEditAdhocReportPage.clickOnSaveReportButton();
        
        //check popup is displayed
        Assert.assertTrue(createEditAdhocReportPage.isSaveAnalysisDispalayed());

        // Enter report name
        String testName = getTestName();
        String reportName = "Report-" + testName;
        createEditAdhocReportPage.enterAnalisysName(reportName);

        // Click on Ok button to save report
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();

        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());

        //click on open button to open saved report
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        Assert.assertEquals(reportName, createEditAdhocReportPage.getExistingReportName(reportName));
        
        createEditAdhocReportPage.clickOnExistingReport(reportName);
        createEditAdhocReportPage.getReportTitle();
        
        String [] tableStatusBarElements = createEditAdhocReportPage.getTableStatusBar();
        
        Assert.assertTrue(createEditAdhocReportPage.isSiteNameDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventTypeDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isEventsNumberDisplayed());
        
        
        Assert.assertEquals(tableStatusBarElements[0].trim(), "Rows:");
        Assert.assertEquals(tableStatusBarElements[3].trim(), "Cols:");
        Assert.assertTrue(Integer.parseInt(tableStatusBarElements[1].trim()) > 0);
        Assert.assertTrue(Integer.parseInt(tableStatusBarElements[4].trim()) > 0);
        
        // check that the name of the report is saved correctly
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), reportName);
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), CUSTOM_SITE_REPORTS);
        
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
                      

        createEditAdhocReportPage.clickOnDeleteReportButton();
        createEditAdhocReportPage.clickOnSaveAnalisysOkButton();
        createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        String existingReportName = createEditAdhocReportPage.getExistingReportName(reportName);       
        Assert.assertTrue("".equals(existingReportName));
          
        ShareUser.navigateToPage(drone, shareUrl).render();
        ShareUser.logout(drone);
        
    }
    
}
