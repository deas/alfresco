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

package org.alfresco.po.share;

import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.thirdparty.pentaho.PentahoUserConsolePage;
import org.alfresco.webdrone.RenderTime;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for SSO functionality for alfresco and pentaho user console
 * 
 * @author jcule
 *
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class SSOTest extends AbstractTest
{
    private static final Logger logger = Logger.getLogger(SSOTest.class);
    
    private static final String ADMINISTRATOR_DASHBOARD = "Administrator Dashboard";
    private static final long PENTAHO_PAGE_LOADING_TIME = 200000;
    
    /**
     * Checks that admin user logged into share is logged into pentaho user console 
     * 
     * @throws Exception
     */

    @Test
    public void logInShare() throws Exception 
    {
        drone.navigateTo(shareUrl);
        LoginPage page = drone.getCurrentPage().render();
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertFalse(page.hasErrorMessage());

        DashBoardPage dashboardPage = (DashBoardPage) ShareUtil.loginAs(drone, shareUrl, username, password);
        dashboardPage.render();
        Assert.assertFalse(page.isBrowserTitle("login"));
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        
        //open new tab and and log in pentaho user console as admin 
        drone.createNewTab();
        drone.navigateTo(pentahoUserConsoleUrl);

        //verify admin is logged into pentaho user console
        PentahoUserConsolePage pentahoUserConsolePage = drone.getCurrentPage().render();
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(PENTAHO_PAGE_LOADING_TIME));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), "admin");
        
        //log out of share 
        drone.navigateTo(shareUrl);
        ShareUtil.logout(drone);
     
    }
    
    /**
     * Checks that admin user logged into pentaho user console is logged into share 
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "logInShare")
    public void logInPentahoUserConsole() throws Exception 
    {
        //log into pentaho user console
        drone.navigateTo(pentahoUserConsoleUrl);
        LoginPage page = drone.getCurrentPage().render();
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertFalse(page.hasErrorMessage());
        PentahoUserConsolePage pentahoUserConsolePage = (PentahoUserConsolePage) ShareUtil.logInAs(drone, username, password).render();
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(PENTAHO_PAGE_LOADING_TIME));
        //pentahoUserConsolePage.render();
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), "admin");
        
        //open new tab and go to share
        drone.createNewTab();
        drone.navigateTo(shareUrl);

        //verify user is logged into share
        DashBoardPage dashboardPage = drone.getCurrentPage().render();
        dashboardPage.render();
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertEquals(ADMINISTRATOR_DASHBOARD, dashboardPage.getPageTitle());
        
        ShareUtil.logout(drone);
        
    }
    
    /**
     * Checks that admin user that logged out of share is logged out of pentaho user console 
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "logInPentahoUserConsole")
    public void logOutOfShare() throws Exception 
    {
        //Log into share as admin
        drone.navigateTo(shareUrl);
        LoginPage page = drone.getCurrentPage().render();
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertFalse(page.hasErrorMessage());

        DashBoardPage dashboardPage = (DashBoardPage) ShareUtil.loginAs(drone, shareUrl, username, password);
        dashboardPage.render();
        Assert.assertFalse(page.isBrowserTitle("login"));
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        
        //verify admin is logged into pentaho user console
        drone.navigateTo(pentahoUserConsoleUrl);
        PentahoUserConsolePage pentahoUserConsolePage = drone.getCurrentPage().render();
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(PENTAHO_PAGE_LOADING_TIME));
        //pentahoUserConsolePage.render();
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), "admin");
        
        //log out of share 
        drone.navigateTo(shareUrl);
        ShareUtil.logout(drone);
        
        //verify admin is logged out of pentaho user console
        drone.navigateTo(pentahoUserConsoleUrl);
        page = drone.getCurrentPage().render();
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertFalse(page.hasErrorMessage());        
     
    }    
   
    /**
     * Checks that admin user that logged out of pentaho user console is still logged into share 
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "logOutOfShare")
    public void logOutOfPentahoUserConsole() throws Exception 
    {
        //log into pentaho user console
        drone.navigateTo(pentahoUserConsoleUrl);
        LoginPage page = drone.getCurrentPage().render();
        Assert.assertTrue(page.isBrowserTitle("login"));
        Assert.assertFalse(page.hasErrorMessage());
        PentahoUserConsolePage pentahoUserConsolePage = (PentahoUserConsolePage) ShareUtil.logInAs(drone, username, password).render();
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(PENTAHO_PAGE_LOADING_TIME));
        //pentahoUserConsolePage.render();
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), "admin");
               
        //verify user is logged into share
        drone.navigateTo(shareUrl);
        DashBoardPage dashboardPage = drone.getCurrentPage().render();
        dashboardPage.render();
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertEquals(ADMINISTRATOR_DASHBOARD, dashboardPage.getPageTitle());
        
        //log out of pentaho user console
        drone.navigateTo(pentahoUserConsoleUrl);
        pentahoUserConsolePage = drone.getCurrentPage().render();
        pentahoUserConsolePage.renderHomeTitle(new RenderTime(PENTAHO_PAGE_LOADING_TIME));
        Assert.assertTrue(pentahoUserConsolePage.isHomeTitleVisible());
        Assert.assertEquals(pentahoUserConsolePage.getLoggedInUser(), "admin");

        pentahoUserConsolePage.clickOnLoggedInUser();
        pentahoUserConsolePage.clickOnLogoutLink();
        
        //verify user is actually logged out of pentaho - currently user dashboard page ????
        drone.navigateTo(shareUrl);
        dashboardPage = drone.getCurrentPage().render();
        dashboardPage.render();
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertEquals(ADMINISTRATOR_DASHBOARD, dashboardPage.getPageTitle());
        
        //verify user is still logged into share
        drone.navigateTo(shareUrl);
        dashboardPage = drone.getCurrentPage().render();
        dashboardPage.render();
        Assert.assertTrue(dashboardPage.isBrowserTitle("dashboard"));
        Assert.assertTrue(dashboardPage.isLoggedIn());
        Assert.assertEquals(ADMINISTRATOR_DASHBOARD, dashboardPage.getPageTitle());        
        
        ShareUtil.logout(drone);
        
    }
    
}
