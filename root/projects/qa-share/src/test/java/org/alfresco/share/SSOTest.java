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
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.thirdparty.pentaho.PentahoUserConsolePage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.webdrone.RenderTime;
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
    private static final String ADMINISTRATOR_DASHBOARD = "Administrator Dashboard";
    
    protected String testUser;
    

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
        
        //verify user is actually logged out of pentaho - currently user dashboard page ????
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

}
