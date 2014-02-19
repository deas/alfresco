package org.alfresco.share;
/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LoginTest extends AbstractTests
{
    private static Log logger = LogFactory.getLog(LoginTest.class);
    
    //If default user is not yet created: Set to ADMIN_USERNAME 
    protected String testUser;
    protected String errorNotification = "Your authentication details have not been recognized";
    
    /**
     * Class includes: Tests from TestLink in Area: Login Tests
     * <ul>
     *   <li>Valid Login Succeeds</li>
     *   <li>Invalid Login Fails</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

 // LoginTests
    @Test(groups={"DataPrepLogin"})
    public void dataPrep_Login_1159() throws Exception
    {
        try
        {
            String testName = getTestName();
            String testUser = getUserNameFreeDomain(testName);
            String[] testUserInfo = new String[] { testUser };

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    //@Test(groups={"DataPrepLogin"})
    public void dataPrep_Login_1160() throws Exception
    {
        // N/A
    }
    
    //@Test(groups={"DataPrepLogin"}) 
    public void dataPrep_Login_1161() throws Exception
    {
        // N/A
    }
    //@Test(groups={"DataPrepLogin"})
    public void dataPrep_Login_1162() throws Exception
    {
        // N/A
    }

    //    @Test(groups={"DataPrepLogin"}) 
    public void dataPrep_Login_1172() throws Exception
    {
        // N/A
    }
    
    /**
     * Valid Login
     */
    @Test
    public void cloud_1159()
    {
        try
        {
            /**Start Test*/
            testName = getTestName();
            testUser=getUserNameFreeDomain(testName);
            
            /**Test Data Setup*/
            //N/A
           
            /**Test Steps*/
            
            //Login Succeeds: When appropriate credentials
            SharePage resultPage = login(drone, testUser, DEFAULT_PASSWORD);
            resultPage.render();
            Assert.assertTrue(resultPage.isLoggedIn());
            
            //Check Page titles
            Assert.assertTrue(resultPage.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));
        }
        catch (Throwable e)
        {
        	reportError(drone,testName,e);
        }
        finally 
        {
        	testCleanup(drone, testName);
        }
    }
    
    /**
     * Invalid Login
     */
    @Test
    public void cloud_1160()
    {
    	try
    	{
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String unregUsername = String.format("test%s@test.com", System.currentTimeMillis());

            /** Test Steps */
            // Login Fails: With inappropriate credentials
            SharePage resultPage = login(drone, unregUsername, DEFAULT_PASSWORD).render();
            Assert.assertFalse(resultPage.isLoggedIn());

            // Check Page titles
            SharePage page = drone.getCurrentPage().render();
            Assert.assertTrue(page.isBrowserTitle(PAGE_TITLE_LOGIN));
            Assert.assertFalse(page.isBrowserTitle(PAGE_TITLE_MY_DASHBOARD));

            // Check Error Message
            LoginPage loginPage = (LoginPage)page.render();
            Assert.assertTrue(loginPage.hasErrorMessage());
            logger.info(loginPage.getErrorMessage());
            Assert.assertTrue(loginPage.getErrorMessage().contains(errorNotification));
            
    	}
    	catch (Throwable e)
    	{
    		reportError(drone,testName,e);
    	}
    	finally
    	{
    		testCleanup(drone, testName);
    	}
    }
    
    /**
     * Incorrect Password
     */
    @Test
    public void cloud_1161()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String invalidUserpass = DEFAULT_PASSWORD + System.currentTimeMillis();

            /** Test Steps */
            // Login Fails: With inappropriate credentials
            SharePage resultPage = login(drone, testUser, invalidUserpass);
            Assert.assertFalse(resultPage.isLoggedIn());

            // Check Error Message
            LoginPage loginPage = (LoginPage)resultPage.render();
            Assert.assertTrue(loginPage.hasErrorMessage());
            logger.info(loginPage.getErrorMessage());
            Assert.assertTrue(loginPage.getErrorMessage().contains(errorNotification));

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }
    
    /**
     * Incorrect Username 
     */
    @Test
    public void cloud_1162()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String invalidUsername = System.currentTimeMillis() + testUser;

            /** Test Steps */
            // Login Fails: With inappropriate credentials
            SharePage resultPage = login(drone, invalidUsername, DEFAULT_PASSWORD);
            Assert.assertFalse(resultPage.isLoggedIn());

            // Check Error Message
            LoginPage loginPage = (LoginPage)resultPage.render();
            Assert.assertTrue(loginPage.hasErrorMessage());
            logger.info(loginPage.getErrorMessage());
            Assert.assertTrue(loginPage.getErrorMessage().contains(errorNotification));

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }
    
    /**
     * Guest Login Fails when user not registered
     */
    @Test
    public void cloud_1272()
    {
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String guestUsername = "guest" + "@" + DOMAIN_FREE;
            String guestUserpass = "guest";

            /** Test Steps */
            // Login Fails: With inappropriate credentials
            SharePage resultPage = login(drone, guestUsername, guestUserpass);
            Assert.assertFalse(resultPage.isLoggedIn());
            
            // Check Error Message
            LoginPage loginPage = (LoginPage)resultPage.render();
            Assert.assertTrue(loginPage.hasErrorMessage());
            logger.info(loginPage.getErrorMessage());
            Assert.assertTrue(loginPage.getErrorMessage().contains(errorNotification));
        }
        catch (Exception e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }
    
    private SharePage login(WebDrone drone, String userName, String password)
    {
        SharePage resultPage = null;
        try
        {
            resultPage = ShareUser.login(drone, userName, password);
        }
        catch(SkipException se)
        {
            resultPage = ShareUser.getSharePage(drone);
        }
        return resultPage;
    }
  }
