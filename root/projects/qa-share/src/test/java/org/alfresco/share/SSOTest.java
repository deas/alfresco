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

package org.alfresco.share;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.Navigation;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.thirdparty.pentaho.PentahoUserConsolePage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.exception.PageException;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * 
 * Tests for SSO functionality for alfresco and pentaho user console
 * 
 * @author jcule
 *
 */
@Listeners(FailedTestListener.class)
public class SSOTest extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(SSOTest.class);
    
    private static String testPassword = DEFAULT_PASSWORD;
    protected String testUser;
    protected String siteName = "";
    private static final String ADMINISTRATOR_DASHBOARD = "Administrator Dashboard";
    

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
     * 1) Admin user logs into share
     * 2) Verify test user is logged into share
     * 3) Pentaho user console page opened
     * 4) Verify test user is logged into pentaho user console
     */
    @Test(groups = { "SSO" })
    public void AONE_16010() throws Exception
    {
        //admin logs in
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        
        //verify admin user is logged into share
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
              
        //admin user creates new new test user
        //String testName = getTestName();
        //String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        //String[] testUserInfo = new String[] { testUser };
        
        // Create test user 
        //CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
        //it looks like pentaho does not like special characters
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, "user16010");
        
        // Login as created user
        //dashboardPage = ShareUser.login(drone, testUser, testPassword).render();
        dashboardPage = ShareUser.login(drone, "user16010", testPassword).render();
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
        //Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), testUser);
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), "user16010");
        
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
        
        //log out of share 
        drone.navigateTo(shareUrl);
        ShareUser.logout(drone);
        
    }
    
    /**
     * 1) Admin user logs into pentaho user console
     * 2) Verify admin user is logged into pentaho user console
     * 3) Share page opened
     * 4) Verify test user is logged into share
     */
    @Test(groups = { "SSO" })
    public void AONE_16011() throws Exception
    {
        //log into pentaho user console
        LoginPage page = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertFalse(page.hasErrorMessage());
        PentahoUserConsolePage pentahoUserConsolePage = (PentahoUserConsolePage) ShareUtil.logInAs(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), "admin");
        
        //open new tab and go to share
        drone.createNewTab();
 
        //verify user is logged into share
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.navigateToPage(drone, shareUrl).render();
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertEquals(ADMINISTRATOR_DASHBOARD, dashboardPage.getPageTitle());
        
        ShareUser.logout(drone);
               
    }
    
    /**
     * 1) Admin user logs into share
     * 2) Verify test user is logged into share
     * 3) Pentaho user console page opened
     * 4) Verify test user is logged into pentaho user console
     * 5) Admin user logs out of share
     * 6) Go to pentaho user console and verify admin is logged out
     */
    @Test(groups = { "SSO" })
    public void AONE_16012() throws Exception
    {
        //admin logs in
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        
        //verify test user is logged into share
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertTrue(dashboardPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

        //open pentaho console url
        PentahoUserConsolePage pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();
        
        //verify admin is logged into pentaho user console
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), "admin");
        
        //log out of share 
        dashboardPage = (DashBoardPage) ShareUser.navigateToPage(drone, shareUrl).render();
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertEquals(ADMINISTRATOR_DASHBOARD, dashboardPage.getPageTitle());
        
        ShareUser.logout(drone);
        
        //verify admin is logged out of pentaho user console
        SharePage page = (SharePage) ShareUser.navigateToPage(drone, pentahoUserConsoleUrl);
        page = drone.getCurrentPage().render();
        Assert.assertTrue(page.isBrowserTitle("login"));       
        
    }
    
    /**
     * 1) Admin user logs into pentaho user console
     * 2) Verify admin user is logged into pentaho user console
     * 3) Share page opened
     * 4) Verify test user is logged into share
     * 5) Admin user is logged out of pentaho user console
     * 6) Go to share page and verify admin is still logged in
     */
    @Test(groups = { "SSO" })
    public void AONE_16013() throws Exception
    {
        //log into pentaho user console
        LoginPage page = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertFalse(page.hasErrorMessage());
        PentahoUserConsolePage pentahoUserConsolePage = (PentahoUserConsolePage) ShareUtil.logInAs(drone, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), "admin");
        
        //open new tab and go to share
        drone.createNewTab();
 
        //verify user is logged into share
        DashBoardPage dashboardPage = (DashBoardPage) ShareUser.navigateToPage(drone, shareUrl).render();
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertEquals(ADMINISTRATOR_DASHBOARD, dashboardPage.getPageTitle());
        
        //go to pentaho user console and log out
        pentahoUserConsolePage = ShareUser.navigateToPage(drone, pentahoUserConsoleUrl).render();
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(maxWaitTime));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), "admin");

        pentahoUserConsolePage.clickOnLoggedInUser();
        pentahoUserConsolePage.clickOnLogoutLink();
        
        //verify user is actually logged out of pentaho -  - currently user dashboard page - this will be fixed, change when fixed ????
        dashboardPage = (DashBoardPage) ShareUser.navigateToPage(drone, shareUrl).render();
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertEquals(ADMINISTRATOR_DASHBOARD, dashboardPage.getPageTitle());
        
        //verify user is still logged into share
        dashboardPage = (DashBoardPage) ShareUser.navigateToPage(drone, shareUrl).render();
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertEquals(ADMINISTRATOR_DASHBOARD, dashboardPage.getPageTitle());      
        
        ShareUser.logout(drone);       
    }

    /**
     * 1) Nonexisting user logs into share
     * 2) Verify nonexisting user is not logged into share and login page with error is displayed
     * 3) Pentaho user console page opened
     * 4) Verify login page displayed
     * 5) Nonexisting user logs in
     * 6) Verify login page with error is displayed
     */
    @Test(groups = { "SSO" })
    public void AONE_16140() throws Exception
    {
        //nonexisting user logs into share
        LoginPage page = (LoginPage) ShareUtil.loginAs(drone, shareUrl, "user16XXX", DEFAULT_PASSWORD).render();
        
        //verify admin user is logged into share
        Assert.assertFalse(page.isLoggedIn());
        Assert.assertFalse(page.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertFalse(page.isBrowserTitle("dashboard"));
        Assert.assertTrue(page.hasErrorMessage());
              
        //open pentaho console url
        page = ShareUtil.loginAs(drone, pentahoUserConsoleUrl, "user16XXX", DEFAULT_PASSWORD).render();
        
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertFalse(page.isBrowserTitle("dashboard"));
        Assert.assertFalse(page.isLoggedIn());
        Assert.assertTrue(page.hasErrorMessage());
        
        //log out of share 
        drone.navigateTo(shareUrl);
        ShareUser.logout(drone);
        
    }
    
}
