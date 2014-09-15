package org.alfresco.share.reports;

import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.Navigation;
import org.alfresco.po.share.dashlet.AdhocAnalyzerDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.reports.AdhocAnalyzerPage;
import org.alfresco.po.share.reports.CreateEditAdhocReportPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.thirdparty.pentaho.PentahoUserConsolePage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.exception.PageException;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AdhocAnalyzerTest extends AbstractUtils
{

    private static final Logger logger = Logger.getLogger(AdhocAnalyzerTest.class);
    
    private static String testPassword = DEFAULT_PASSWORD;
    protected String testUser;
    protected String siteName = "";
    private static final String ADHOC_ANALYZE = "Adhoc Analyze";
    private static final String UNSAVED_REPORT = "Unsaved Report";
    private static final String PENTAHO_BUSINESS_ANALYST_USERNAME = "pentahoBusinessAnalyst";
    private static final String PENTAHO_BUSINESS_ANALYST_PASSWORD = "pentahoBusinessAnalyst";

    
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }
    
    @Test(groups = { "AdhocAnalyzer" })
    public void dataPrep_SSO_16006() throws Exception
    {
        String testUser = "user16006";
        String[] testUserInfo = new String[] { testUser };

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Created user logs out
        ShareUser.logout(drone);
    }
   
    /**
     * 1) Test user logs into share
     * 2) Verify test user is logged into share and cannot see Reporting in the header bar
     * 3) Pentaho user console page opened
     * 4) Verify test user is logged into pentaho user console and cannot create reports
     * 5) Pentaho business analyst logs into share
     * 6) Verify pentaho business analyst can see reporting, Adhoc Analyze page, 
     * click on Analyze button and Content, Users and Activities
     * 7) Verify Adhoc Analyzer iframe is displayed 
     */
    @Test(groups = { "AdhocAnalyzer" })
    public void AONE_16006() throws Exception
    {
 
        // Login as created user
        String testUser = "user16006";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        
        //verify test user cannot see Reporting menu - verify they cannot create reports in share
        Navigation navigation = dashboardPage.getNav();

        try
        {
            navigation.isReportingVisible();
            Assert.assertTrue(false, "Above line should have thrown page exception");
        }
        catch (PageException e)
        {
            Assert.assertTrue(e.getMessage().startsWith( "Unable to find Reporting menu in the header."));
        }
        
        //open pentaho console url
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();
        
        //verify test user is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), testUser);
        
        //verify test user cannot create reports
        pentahoUserConsolePage.clickOnFileMenu();        
        try
        {
            pentahoUserConsolePage.isNewDisplayed();
            Assert.assertTrue(false, "Above line should have thrown page exception");
        }
        catch (PageException e)
        {
            Assert.assertTrue(e.getMessage().startsWith( "Unable to find New Menu."));
        }
       
        //tests user logs out of share 
        drone.navigateTo(shareUrl);
        ShareUser.logout(drone);
        
        //pentaho business analyst logs into share
        dashboardPage = (DashBoardPage) ShareUser.login(drone, PENTAHO_BUSINESS_ANALYST_USERNAME, PENTAHO_BUSINESS_ANALYST_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        
        //penatho business analyst can see reporting menu
        navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());
        
        //penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), ADHOC_ANALYZE);
        
        //penatho business analyst can see Adhoc Analyze page and click on Analyze button and Content, Users and Activities button
        adhocAnalyzePage.clickOnAnalyzeButton();
        Assert.assertTrue(adhocAnalyzePage.isCreateContentUsersActivitiesDisplayed());
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnCreateReportButton();
        Assert.assertEquals(createEditAdhocReportPage.getPageTitle(), ADHOC_ANALYZE);
        Assert.assertTrue(createEditAdhocReportPage.isOpenButtonDisplayed());
        Assert.assertTrue(createEditAdhocReportPage.isSaveButtonDisplayed());
        Assert.assertEquals(createEditAdhocReportPage.getReportTitle(), UNSAVED_REPORT);
                
    }
    
    
    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify Pentaho business analyst is logged into share and can see Reporting in the header bar
     * 3) Click on the Reporting menu in the header bar
     * 4) Select Analyze from the dropdown
     * 5) Verify Adhoc Analyze page is displayed and click on Open button 
     * 6) Verify pentaho business analyst can see reporting, Adhoc Analyze page, 
     * click on Open button
     * 7) Verify (There are no analyses) message is displayed 
     */
    @Test(groups = { "AdhocAnalyzer" })
    public void AONE_16008() throws Exception
    {
        // Login as pentaho business analyst into share and verify user dashboard is displayed
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, PENTAHO_BUSINESS_ANALYST_USERNAME, PENTAHO_BUSINESS_ANALYST_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //penatho business analyst can see reporting menu
        Navigation navigation = dashboardPage.getNav();
        Assert.assertTrue(navigation.isReportingVisible());
        
        //penatho business analyst can see Adhoc Analyze page
        AdhocAnalyzerPage adhocAnalyzePage = dashboardPage.getNav().selectAnalyze().render();
        Assert.assertEquals(adhocAnalyzePage.getPageTitle(), ADHOC_ANALYZE);        
 
        CreateEditAdhocReportPage createEditAdhocReportPage = adhocAnalyzePage.clickOnOpenReportButton();
        Assert.assertTrue(createEditAdhocReportPage.isThereAreNoAnalysesDisplayed());
    }


    /**
     * 1) Pentaho business analyst logs into share
     * 2) Verify pentaho business analyst dashboard is displayed and add Adhoc Analyzer dashlet to the user dashboard
     * 3) Click on the Open drop down menu on the dashlet
     * 4) Verify appropriate message is displayed showing there are no existing reports:“(There are no analyses)” 
     */
    @Test(groups = { "AdhocAnalyzer" })
    public void AONE_16046() throws Exception
    {
        // Login as pentaho business analyst into share and verify user dashboard is displayed
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, PENTAHO_BUSINESS_ANALYST_USERNAME, PENTAHO_BUSINESS_ANALYST_PASSWORD).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //pentaho business analyst adds Analyzer Report dashlet to the user dashboard
        CustomiseUserDashboardPage customiseUserDashboardPage = dashboardPage.getNav().selectCustomizeUserDashboard().render();
        dashboardPage = customiseUserDashboardPage.addDashlet(Dashlets.ADHOC_ANALYZER, 2).render();
        AdhocAnalyzerDashlet adhocAnalyzerDashlet = dashboardPage.getDashlet("adhoc-analyzer").render();
        Assert.assertTrue(adhocAnalyzerDashlet.isTitleDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isOpenDisplayed());
        Assert.assertTrue(adhocAnalyzerDashlet.isDashletMessageDisplayed());
        
        //pentaho business analyst clicks on the Open drop down menu on the dashlet
        adhocAnalyzerDashlet.clickOnOpenDropdown();
    
        //verify appropriate message is displayed showing there are no existing reports:“(There are no analyses)”
        Assert.assertTrue(adhocAnalyzerDashlet.isThereAreNoAnalysesDisplayed());
    }

    @Test(groups = { "AdhocAnalyzer" })
    public void dataPrep_SSO_16144() throws Exception
    {
        String testUser = "user16144";
        String[] testUserInfo = new String[] { testUser };

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Created user logs out
        ShareUser.logout(drone);
    }
    
    /**
     * 1) Test user logs into share
     * 2) Verify test user is logged into share and cannot add Analyzer Report dashlet to the user dashboard
     */
    @Test(groups = { "AdhocAnalyzer" })
    public void AONE_16144() throws Exception
    {
 
        // Login as created user
        String testUser = "user16144";
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, testUser, testPassword).render();
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        CustomiseUserDashboardPage customiseUserDashboardPage = dashboardPage.getNav().selectCustomizeUserDashboard().render();  
        
        //verify user can't customize the site dasboard
        try
        {
            customiseUserDashboardPage.addDashlet(Dashlets.ADHOC_ANALYZER, 2).render();
            Assert.assertTrue(false, "Above line should have thrown page exception");
        }
        catch (NoSuchElementException e)
        {
            Assert.assertTrue(e.getMessage().startsWith( "Error in adding dashlet using drag and drop"));
            ShareUser.logout(drone);

        }
                   
    }

}
