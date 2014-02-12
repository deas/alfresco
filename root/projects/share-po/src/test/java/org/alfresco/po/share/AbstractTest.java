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
package org.alfresco.po.share;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.ShareTestProperty;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
/**
 * Abstract test holds all common methods and functionality to test against
 * Benchmark Grid tests.
 * 
 * @author Michael Suzuki
 */
public abstract class AbstractTest
{
    private static Log logger = LogFactory.getLog(AbstractTest.class);
    private static ApplicationContext ctx;
    protected static String shareUrl;
    protected static String password;
    protected static String username;
    protected static String googleusername;
    protected static String googlepassword;
    protected static String cloudUserName;
    protected static String cloudUserPassword;
    protected static UserProfile anotherUser;
    protected static AlfrescoVersion alfrescoVersion;
    public static Integer retrySearchCount = 3;
    public static String downloadDirectory;
    public boolean hybridEnabled;
    protected WebDrone drone;
    protected String testName;
    
    public WebDrone getDrone()
    {
        return drone;
    }

    @BeforeSuite(alwaysRun = true)
    public void setupContext() throws Exception
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("Starting test context");
        }
        ctx = new ClassPathXmlApplicationContext( "share-po-test-context.xml","webdrone-context.xml");

        ShareTestProperty t = ctx.getBean(ShareTestProperty.class);
        shareUrl = t.getShareUrl();
        username = t.getUsername();
        password = t.getPassword();
        googleusername = t.getGoogleUserName();
        googlepassword = t.getGooglePassword();
        alfrescoVersion = t.getAlfrescoVersion();
        downloadDirectory = t.getDownloadDirectory();
        hybridEnabled = t.isHybridEnabled();
        cloudUserName = t.getCloudUserName();
        cloudUserPassword = t.getCloudUserPassword();
        if(logger.isTraceEnabled())
        {
            logger.trace("Alfresco version is" + alfrescoVersion);
            logger.trace("Alfresco shareUrl is" + shareUrl);
        }

        anotherUser = (UserProfile) ctx.getBean("anotherUser");
        if(logger.isTraceEnabled())
        {
            logger.trace("Loaded another user for test purposes - " + anotherUser);
        }
    }

    @BeforeClass(alwaysRun = true)
    public void getWebDrone() throws Exception
    {
        drone = (WebDrone) ctx.getBean("webDrone");
        drone.maximize();
    }
    
    @AfterClass(alwaysRun = true)
    public void closeWebDrone()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Closing web drone");
        }
        // Close the browser
        if (drone != null)
        {
            drone.quit();
            drone = null;
        }
    }

    /**
     * Helper to log admin user into dashboard.
     * 
     * @return DashBoardPage page object.
     * @throws Exception if error
     */
    public DashBoardPage loginAs(final String... userInfo) throws Exception
    {
        if(shareUrl == null)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("null shareUrl");
            }
        }
        return ShareUtil.loginAs(drone, shareUrl, userInfo).render();
    }
    
    public void saveScreenShot(String methodName) throws IOException
    {
        if(StringUtils.isEmpty(methodName))
        {
            throw new IllegalArgumentException("Method Name can't be empty or null.");
        }
        File file = drone.getScreenShot();
        File tmp = new File("target/webdrone-" + methodName + ".png");
        FileUtils.copyFile(file, tmp);
    }
    
    public void savePageSource(String methodName) throws IOException
    {
        String htmlSource = ((WebDroneImpl) drone).getDriver().getPageSource();
        File file = new File("target/webdrone-" + methodName + ".html");
        FileUtils.writeStringToFile(file, htmlSource);
    }

    @BeforeMethod(alwaysRun = true) 
    protected void startSession(Method method) throws Exception
    { 
        testName = method.getName(); 
        if(logger.isTraceEnabled())
        {
            logger.trace(String.format("Test run:%s.%s", 
                                        method.getDeclaringClass().getCanonicalName(),
                                        testName));
        }
    }
    /**
     * Helper method to get site dashboard page.
     * @param siteName String name of the site to enter
     * @return {@link SiteDashboardPage} page
     */
    protected SiteDashboardPage getSiteDashboard(final String siteName)
    {
        DashBoardPage dashBoard = drone.getCurrentPage().render();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        return dashlet.selectSite(siteName).click().render();
    }
    
    /**
     * Function to create user on Enterprise using UI
     * 
     * @param uname - This should always be unique. So the user of this method needs to verify it is unique. 
     *                eg. - "testUser" + System.currentTimeMillis();
     * @return
     * @throws Exception
     */
    public boolean createEnterpriseUser(String uname) throws Exception
    {
        if (alfrescoVersion.isCloud() || StringUtils.isEmpty(uname))
        {
            throw new UnsupportedOperationException("This method is not applicable for cloud");
        }
        try
        {
            DashBoardPage dashBoard = loginAs(username, password);
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            String userinfo = uname + "@test.com";
            newPage.inputFirstName(userinfo);
            newPage.inputLastName(userinfo);
            newPage.inputEmail(userinfo);
            newPage.inputUsername(uname);
            newPage.inputPassword("password");
            newPage.inputVerifyPassword("password");
            UserSearchPage userCreated = newPage.selectCreateUser().render();
            userCreated.searchFor(userinfo).render();
            return userCreated.hasResults();
        }
        catch (Throwable t)
        {
            saveScreenShot("createUser");
            throw new Exception(t);
        }
        finally
        {
            ShareUtil.logout(drone);
        }
    }

    public boolean isHybridEnabled()
    {
        return hybridEnabled;
    }

    /**
     * Method to setup cloud sync from My Profile page.
     * @param drone
     * @param cloudUserName
     * @param password
     */
    protected void signInToCloud(WebDrone drone, String cloudUserName, String password)
    {
        // go to profile
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();

        // Click cloud sync
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        // Sign in

        if (!cloudSyncPage.isDisconnectButtonDisplayed())
        {
            CloudSignInPage cloudSignPage = cloudSyncPage.selectCloudSign().render();
            cloudSyncPage = cloudSignPage.loginAs(cloudUserName, password).render();
        }
        logger.info("is signed in: " + cloudSyncPage.isDisconnectButtonDisplayed());
        cloudSyncPage.render();
    }

    /**
     * Utility method to open site document library from search
     * @param drone
     * @param siteName
     * @return
     */
    protected DocumentLibraryPage openSiteDocumentLibraryFromSearch(WebDrone drone, String siteName)
    {
        SharePage sharePage = drone.getCurrentPage().render();
        SiteFinderPage siteFinderPage = sharePage.getNav().selectSearchForSites().render();
        siteFinderPage.searchForSite(siteName).render();
        SiteDashboardPage siteDashboardPage = siteFinderPage.selectSite(siteName).render();
        DocumentLibraryPage documentLibPage = siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
        return documentLibPage;
    }

    /**
     * Utility method to disconnect Cloud Sync.
     * @param drone
     */
    protected void disconnectCloudSync(WebDrone drone)
    {
        if(hybridEnabled)
        {
            MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
            CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
            if(cloudSyncPage.isDisconnectButtonDisplayed())
            {
                cloudSyncPage.disconnectCloudAccount().render();
            }
        }
    }
}
