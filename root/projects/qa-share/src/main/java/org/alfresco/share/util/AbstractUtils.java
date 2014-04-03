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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.util;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.BrowserPreference;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.ShareProperties;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.console.CloudConsolePage;
import org.alfresco.share.search.SearchKeys;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Class includes: Abstract test holds all common methods, These will be used
 * from within the ShareUser utils or tests.
 * 
 * @author Meenal Bhave
 */
public abstract class AbstractUtils
{
    // Test Run Options
    protected static final Boolean deleteSiteFlag = true; // Indicates if Site
                                                          // is to be deleted at
                                                          // the end of the test
                                                          // -> using
                                                          // ShareUser.deleteSiteAPI

    private static Log logger = LogFactory.getLog(AbstractUtils.class);
    protected static ApplicationContext ctx;
    protected static String shareUrl;
    protected static String cloudUrlForHybrid;
    // Test Suite Admins
    public static final String advSearchAdmin = "advsearchtest";
    public static final String loginAdmin = "logintest";
    public static final String myDashAdmin = "mydashtest";
    public static final String siteDashAdmin = "sitedashtest";
    protected static String DEFAULT_FREENET_USER;
    protected static String DEFAULT_PREMIUMNET_USER;
    protected static final String DEFAULT_PASSWORD = "password";
    protected static final String DEFAULT_LASTNAME = "LName";

    // Test Related Folders
    public static final String SLASH = File.separator;
    private static final String SRC_ROOT = System.getProperty("user.dir") + SLASH;
    protected static final String DATA_FOLDER = SRC_ROOT + "testdata" + SLASH;
    private static String RESULTS_FOLDER;
    protected String testName;
    protected WebDrone drone;
    protected WebDrone customDrone;
    protected WebDrone hybridDrone;
    public static long maxWaitTime = 30000;
    public static long maxWaitTime_CloudSync = 60000;
    public static long refreshDuration = 25000;
    public static Integer retrySearchCount = 3;
    public static long maxDownloadWaitTime = 3000;
    // Test Data Related options
    protected static final String DATAPREP_OPTION = "SKIP";
    // if data is expected to be pre-loaded

    // Constants
    protected static final String SITE_VISIBILITY_PUBLIC = "public";
    protected static final String SITE_VISIBILITY_PRIVATE = "private";
    protected static final String SITE_VISIBILITY_MODERATED = "moderated";
    protected static final String DOCLIB = "DocumentLibrary";
    protected static final String REPO = "Repository";
    protected static final String DOCLIB_CONTAINER = "documentLibrary";
    protected static final String MY_DASHBOARD = " Dashboard";
    protected final static String SERACH_ZERO_CONTENT = "ZERO_RESULTS";
    protected final static String NONE = "(None)";
    protected final static String adminGroup = "ALFRESCO_ADMINISTRATORS";
    protected static final String BASIC_SEARCH = SearchKeys.BASIC_SEARCH.getSearchKeys();
    protected static final String ADV_FOLDER_SEARCH = SearchKeys.FOLDERS.getSearchKeys();
    protected static final String ADV_CONTENT_SEARCH = SearchKeys.CONTENT.getSearchKeys();
    protected static final String ADV_CRM_SEARCH = SearchKeys.CRM_SEARCH.getSearchKeys();

    // UI Elements
    public static final String uiWelcomePanel = "[class='welcome-info']";
    public static final String uiMySitesDashlet = "div.dashlet.my-sites.resizable.yui-resize";
    public static final String uiMyDocuments = "div.dashlet.my-documents";
    public static final String uiMyTasks = "[class='dashlet my-tasks resizable yui-resize']";
    public static final String uiMyActivities = "[class='dashlet activities resizable yui-resize']";

    // Page Titles
    protected static final String PAGE_TITLE_LOGIN = "login";
    protected static final String PAGE_TITLE_MY_DASHBOARD = "dashboard";
    protected static final String PAGE_TITLE_SITE_DASHBOARD = "Site Dashboard";
    protected static final String PAGE_TITLE_DOCLIB = "Document Library";
    protected static final String PAGE_TITLE_MYTASKS = "My Tasks";
    protected static final String PAGE_TITLE_EDITTASK = "Edit Task";
    protected static final String PAGE_TITLE_USERHOMES = "User Homes";

    // Dashlets
    protected static final String DASHLET_ACTIVITIES = "activities";
    protected static final String DASHLET_CONTENT = "";
    protected static final String DASHLET_DOCUMENTS = "my-documents";
    protected static final String DASHLET_TASKS = "my-tasks";
    protected static final String DASHLET_SITES = "my-sites";
    protected static final String DASHLET_MEMBERS = "";
    protected static final String DASHLET_IMAGE_PREVIEW = "";
    protected static final String SITE_CONTENT_DASHLET = "site-contents";
    protected static final String USER_TASKS = "tasks";
    protected static final String SITE_ACTIVITIES = "site-activities";
    protected static final String SITE_NOTICE = "site-notice";
    protected static final String MY_DISCUSSIONS = "my-discussions";

    // Activity Feeds
    protected static final String FEED_CONTENT_ADDED = " added";
    protected static final String FEED_CONTENT_DELETED = " deleted";
    protected static final String FEED_CONTENT_EDITED = " edited";
    protected static final String FEED_CONTENT_UPDATED = " updated";
    protected static final String FEED_CONTENT_LIKED = " liked";
    protected static final String FEED_COMMENTED_ON = " commented on";
    protected static final String FEED_UPDATED_COMMENT_ON = " updated comment on";
    protected static final String FEED_COMMENT_DELETED = " deleted a comment";
    protected static final String FEED_ROLE_CHANGED = " role changed";
    protected static final String FEED_COMMENTED_FROM = " from ";
    protected static final String FEED_LOCATION = " in ";
    protected static final String FEED_FOR_FOLDER = " folder ";
    protected static final String FEED_FOR_FILE = " document ";
    protected static final String DOCUMENT_LOCKED_BY_YOU_MESSAGE = "This document is locked by you.";
    protected static final String LAST_SYNC_FAILED_MESSAGE = "Last sync failed.";

    // Test Run and Users Info: This is now a part of test.properties
    protected static String username;
    protected static String password;
    protected static String downloadDirectory;
    protected static String mimeTypes;
    protected static String googleUserName;
    protected static String googlePassword;
    protected static boolean hybridEnabled;
    protected static String UNIQUE_TESTRUN_NAME;
    protected static String DOMAIN_FREE;
    protected static String DOMAIN_PREMIUM;
    protected static String DOMAIN_HYBRID;
    protected static String SUPERADMIN_USERNAME;
    protected static String SUPERADMIN_PASSWORD;
    protected static String ADMIN_USERNAME;
    protected static String ADMIN_PASSWORD;
    protected static String HEADER_KEY;
    protected static String DEFAULT_USER;
    protected static String UNIQUE_TESTDATA_STRING;
    protected static AlfrescoVersion alfrescoVersion;
    protected static Map<WebDrone, ShareTestProperty> dronePropertiesMap = new HashMap<WebDrone, ShareTestProperty>();
    protected ShareTestProperty hybridShareTestProperties;
    private static ShareTestProperty testProperties;
    public static String apiContextCloudInternal = "alfresco/service/internal/cloud/";
    public static String apiContextPublicAPI = "/public/alfresco/versions/1/";
    public static String apicontextCloud = "alfresco/service/internal/cloud/";
    public static String apiContextEnt = "alfresco/api/";
    public static String apiPath = "";
    public final static String STAGURL = "https://stagmy.alfresco.com/share";

    Map<String, WebDrone> droneMap = new HashMap<String, WebDrone>();

    @BeforeSuite(alwaysRun = true)
    @Parameters({ "contextFileName" })
    public static void setupContext(@Optional("qashare-test-context.xml") String contextFileName)
    {
        List<String> contextXMLList = new ArrayList<String>();
        contextXMLList.add(contextFileName);
        ctx = new ClassPathXmlApplicationContext(contextXMLList.toArray(new String[contextXMLList.size()]));
        testProperties = (ShareTestProperty) ctx.getBean("shareTestProperties");
        shareUrl = testProperties.getShareUrl();
        cloudUrlForHybrid = testProperties.getCloudUrlForHybrid();
        username = testProperties.getUsername();
        password = testProperties.getPassword();
        alfrescoVersion = testProperties.getAlfrescoVersion();
        downloadDirectory = testProperties.getDownloadDirectory();
        googleUserName = testProperties.getGoogleUserName();
        googlePassword = testProperties.getGooglePassword();
        hybridEnabled = testProperties.isHybridEnabled();
        UNIQUE_TESTRUN_NAME = testProperties.getuniqueTestRunName();
        DOMAIN_FREE = testProperties.getdomainFree();
        DOMAIN_PREMIUM = testProperties.getdomainPremium();
        DOMAIN_HYBRID = testProperties.getdomainHybrid();
        DEFAULT_USER = testProperties.getdefaultUser();
        UNIQUE_TESTDATA_STRING = testProperties.getuniqueTestDataString();
        SUPERADMIN_USERNAME = testProperties.getSuperadminUsername();
        SUPERADMIN_PASSWORD = testProperties.getSuperadminPassword();
        ADMIN_USERNAME = testProperties.getadminUsername();
        ADMIN_PASSWORD = testProperties.getadminPassword();
        HEADER_KEY = testProperties.getHeaderKey();
        mimeTypes = testProperties.getMimeTypes();

        RESULTS_FOLDER = SRC_ROOT + "test-output" + SLASH + UNIQUE_TESTRUN_NAME + SLASH;

        DEFAULT_FREENET_USER = DEFAULT_USER + "@" + DOMAIN_FREE;
        DEFAULT_PREMIUMNET_USER = DEFAULT_USER + "@" + DOMAIN_PREMIUM;

        logger.info("Target URL: " + shareUrl);
        logger.info("Alfresco Version: " + alfrescoVersion);
    }

    public static boolean isHybridEnabled()
    {
        return hybridEnabled;
    }

    public void setup() throws Exception
    {
        drone = (WebDrone) ctx.getBean("webDrone");
        droneMap.put("std_drone", drone);
        dronePropertiesMap.put(drone, testProperties);
        maxWaitTime = ((WebDroneImpl) drone).getMaxPageRenderWaitTime();
    }

    public void setupHybridDrone() throws Exception
    {
        hybridDrone = (WebDrone) ctx.getBean("hybridDrone");
        droneMap.put("hybrid_drone", hybridDrone);
        hybridShareTestProperties = (ShareTestProperty) ctx.getBean("hybridShareTestProperties");
        dronePropertiesMap.put(hybridDrone, hybridShareTestProperties);
    }

    public void setupCustomDrone(WebDroneType droneType) throws Exception
    {
        customDrone = ((WebDroneImpl) ctx.getBean(droneType.getName()));
        droneMap.put("custom_drone", customDrone);
        dronePropertiesMap.put(customDrone, testProperties);
        maxWaitTime = ((WebDroneImpl) customDrone).getMaxPageRenderWaitTime();
    }

    public void setupCustomDrone(WebDriver webDriver)
    {
        customDrone = new WebDroneImpl(webDriver, 2000, 5000, new ShareProperties(alfrescoVersion.toString()), new FactorySharePage());
        droneMap.put("custom_drone", customDrone);
        dronePropertiesMap.put(customDrone, testProperties);
        maxWaitTime = ((WebDroneImpl) customDrone).getMaxPageRenderWaitTime();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("shutting web drone");
        }
        // Close the browser
        for (Map.Entry<String, WebDrone> entry : droneMap.entrySet())
        {
            try
            {
                if (entry.getValue() != null)
                {
                    ShareUtil.logout(entry.getValue());
                    entry.getValue().quit();
                    logger.info(entry.getKey() + " closed");
                    logger.info("[Suite ] : End of Tests in: " + this.getClass().getSimpleName());
                }
            }
            catch (Exception e)
            {
                logger.error("Failed to close previous instance of brower:" + entry.getKey(), e);
            }
        }
    }

    /**
     * Helper to log a user into alfresco.
     * 
     * @param drone
     *            TODO
     * @param userInfo
     * @return DashBoardPage
     * @throws Exception
     *             if error
     */
    public static DashBoardPage loginAs(WebDrone drone, String... userInfo)
    {
        if (userInfo.length < 2)
        {
            userInfo = getAuthDetails(userInfo[0]);
        }
        return ShareUtil.loginAs(drone, dronePropertiesMap.get(drone).getShareUrl(), userInfo).render();
    }

    /**
     * Helper to Take a ScreenShot. Saves a screenshot in target folder
     * <RESULTS_FOLDER>
     * 
     * @param methodName
     *            String This is the Test Name / ID
     * @return void
     * @throws Exception
     *             if error
     */
    public static void saveScreenShot(WebDrone drone, String methodName) throws IOException
    {
        if (drone != null)
        {
            File file = drone.getScreenShot();
            File tmp = new File(RESULTS_FOLDER + methodName + ".png");
            FileUtils.copyFile(file, tmp);
        }
    }

    public void savePageSource(String methodName) throws IOException
    {
        for (Map.Entry<String, WebDrone> entry : droneMap.entrySet())
        {
            if (entry.getValue() != null)
            {
                String htmlSource = ((WebDroneImpl) entry.getValue()).getDriver().getPageSource();
                File file = new File(RESULTS_FOLDER + methodName + "_" + entry.getKey() + "_Source.html");
                FileUtils.writeStringToFile(file, htmlSource);
            }
        }
    }

    /**
     * Helper to Take a ScreenShot. Saves a screenshot in target folder
     * <RESULTS_FOLDER>
     * 
     * @param methodName
     *            String This is the Test Name / ID
     * @return void
     * @throws Exception
     *             if error
     */
    public void saveScreenShot(String methodName) throws IOException
    {
        for (Map.Entry<String, WebDrone> entry : droneMap.entrySet())
        {
            if (entry.getValue() != null)
            {
                File file = entry.getValue().getScreenShot();
                File tmp = new File(RESULTS_FOLDER + methodName + "_" + entry.getKey() + ".png");
                FileUtils.copyFile(file, tmp);
            }
        }
        try
        {
            saveOsScreenShot(methodName);
        }
        catch (AWTException e)
        {
            logger.error("Not able to take the OS screen shot: " + e.getMessage());
        }
    }

    /**
     * Take OS Scren Shot
     * 
     * @param methodName - Method Name
     */
    public void saveOsScreenShot(String methodName) throws IOException, AWTException
    {
        Robot robot = new Robot();
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIO.write(screenShot, "png", new File("target/webdrone-" + methodName + "_OS" + ".png"));
    }

    @BeforeMethod
    protected String getMethodName(Method method)
    {
        String methodName = method.getName();
        logger.info("[Test: " + methodName + " ]: START");
        return methodName;
    }

    /**
     * Helper returns the test / methodname. This needs to be called as the 1st
     * step of the test. Common Test code can later be introduced here.
     * 
     * @return String testcaseName
     * @throws N
     *             /A
     */
    public static String getTestName()
    {
        String testID = Thread.currentThread().getStackTrace()[2].getMethodName();
        return (testID.substring(testID.lastIndexOf("_")).replace("_", alfrescoVersion + "-"));
    }

    @AfterMethod
    public void logTestResult(ITestResult result)

    {
        logger.info("[Test: " + result.getMethod().getMethodName() + " ]: " + result.toString().toUpperCase());
    }

    /**
     * Helper to perform the common cleanup actions after a test. This needs to
     * be called as the last step of the test. Common Test code to perform
     * cleanup can later be introduced here.
     * 
     * @param testName
     *            String test case ID
     * @return N/A
     */
    protected void testCleanup(WebDrone driver, String testName)
    {
        logger.trace("Test Done: " + testName);

        // Perform Log out @after method, to allow for the activities to happen
        // in the same user session via after method
        if (driver != null)
        {
            ShareUtil.logout(driver);
        }
    }

    /**
     * Helper to report error details for a test.
     * 
     * @param driver
     *            WebDrone Instance
     * @param testName
     *            String test case ID
     * @param t
     *            Throwable Error & Exception to include testng assert
     *            failures being reported as Errors
     */
    protected void reportError(WebDrone driver, String testName, Throwable t)
    {
        logger.error("Error in Test: " + testName, t);
        try
        {
            saveScreenShot(driver, testName);
            savePageSource(testName);

        }
        catch (IOException e)
        {
            Assert.fail("Unable to save screen shot of Test: " + testName + " : " + getCustomStackTrace(t));
        }
        Assert.fail("Error in Test: " + testName + " : " + getCustomStackTrace(t));
    }

    /**
     * This method returns appropriate API URL for given webDrone associated
     * with the call. URL is picked from the map created initially when the
     * drones are created
     * 
     * @param drone
     * @return
     */
    public static String getAPIURL(WebDrone drone)
    {
        String shareURL = dronePropertiesMap.get(drone).getShareUrl();
        logger.info("getAPIURL: -- Using URL - " + shareURL);
        String apiUrl = shareURL.replace("my.alfresco.me/share", "api.alfresco.me/");
        apiUrl = apiUrl.replace("my.alfresco.me:/share", "api.alfresco.me/");
        apiUrl = apiUrl.replace("my.alfresco.me:443/share", "api.alfresco.me/");

        if (!dronePropertiesMap.get(drone).getAlfrescoVersion().isCloud())
        {
            apiUrl = shareURL.replace("share", apiContextEnt);
        }
        else
        {
            if (shareURL.equalsIgnoreCase(STAGURL))
            {
                apiUrl = shareURL.replace("my.alfresco.com/share", "api.alfresco.com/");
            }
        }

        logger.info("getAPIURL: -- derived apiUrl - " + apiUrl);

        return apiUrl;
    }

    /**
     * Helper to return the stack trace as a string for reporting purposes.
     * 
     * @param ex
     *            exception / error
     * @return String: stack trace
     */
    protected static String getCustomStackTrace(Throwable ex)
    {

        final StringBuilder result = new StringBuilder();
        result.append(ex.toString());
        final String newline = System.getProperty("line.separator");
        result.append(newline);

        for (StackTraceElement element : ex.getStackTrace())
        {
            result.append(element);
            result.append(newline);
        }
        return result.toString();
    }

    /**
     * Helper to create a new file, empty or with specified contents if one does
     * not exist. Logs if File already exists
     * 
     * @param filename
     *            String Complete path of the file to be created
     * @param contents
     *            String Contents for text file
     * @return File
     */
    public static File newFile(String filename, String contents)
    {
        File file = new File(filename);

        try
        {
            if (!file.exists())
            {

                if (!contents.isEmpty())
                {
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8").newEncoder());
                    writer.write(contents);
                    writer.close();
                }
                else
                {
                    file.createNewFile();
                }
            }
            else
            {
                logger.debug("Filename already exists: " + filename);
            }
        }
        catch (IOException ex)
        {
            logger.error("Unable to create sample file", ex);
        }
        return file;
    }

    /**
     * Helper to search for an Element on the Share Page, with configurable
     * retry search option.
     * 
     * @param cssClassName
     *            : css Selector such as [class='filename']
     * @param linkTextOfElementToFind
     *            String
     * @return true if element is found
     */
    protected static boolean findElement(WebDrone driver, String cssClassName, String linkTextOfElementToFind)
    {
        boolean found = false;
        long maxWaitTime = 20000;

        // Wait for Page to Render
        driver.getCurrentPage().render(maxWaitTime);

        // Find Elements
        List<WebElement> webElement = driver.findAndWait(By.className(cssClassName), maxWaitTime).findElements(By.linkText(linkTextOfElementToFind));

        if (webElement.isEmpty())
        {
            found = false;
        }
        else
        {
            found = true;
        }

        return found;
    }

    /**
     * Helper to consistently get the username in the free domain, in the
     * desired format.
     * 
     * @param testID
     *            String Name of the test for uniquely identifying / mapping
     *            test data with the test
     * @return String username
     */
    public static String getUserNameFreeDomain(String testID)
    {
        String userName = "";

        userName = String.format("user%s@%s", testID, DOMAIN_FREE);

        return userName;
    }

    /**
     * Helper to consistently get the username in the premium domain, in the
     * desired format.
     * 
     * @param testID
     *            String Name of the test for uniquely identifying / mapping
     *            test data with the test
     * @return String username
     */
    protected static String getUserNamePremiumDomain(String testID)
    {
        String userName = "";

        userName = String.format("user%s@%s", testID, DOMAIN_PREMIUM);

        return userName;
    }

    /**
     * Helper to consistently get the userName in the specified domain, in the
     * desired format.
     * 
     * @param testID
     *            String Name of the test for uniquely identifying / mapping
     *            test data with the test
     * @return String userName
     */
    protected static String getUserNameForDomain(String testID, String domainName)
    {
        String userName = "";
        if (domainName.isEmpty())
        {
            domainName = DOMAIN_FREE;
        }
        // ALF: Workaround needs toLowerCase to be added. to be removed when
        // jira is fixed
        userName = String.format("user%s@%s", testID, domainName).toLowerCase();

        return userName;
    }

    /**
     * Helper to consistently get the DomainName based on the specified domain,
     * in the desired format.
     * 
     * @param domainID
     *            String to be prefixed to DOMAIN_FREE
     * @return String Domain
     */
    protected static String getDomainName(String domainID)
    {
        if ((domainID == null) || (domainID.isEmpty()))
        {
            return DOMAIN_FREE;
        }
        return domainID + DOMAIN_FREE;
    }

    /**
     * Helper to consistently get the Site Name.
     * 
     * @param testID
     *            String Name of the test for uniquely identifying / mapping
     *            test data with the test
     * @return String sitename
     */
    public static String getSiteName(String testID)
    {
        String siteName = "";

        siteName = String.format("Site%s%s", UNIQUE_TESTDATA_STRING, testID);

        return siteName;
    }

    /**
     * Helper to consistently get the Site Short Name.
     * 
     * @param siteName
     *            String Name of the test for uniquely identifying / mapping
     *            test data with the test
     * @return String site short name
     */
    public static String getSiteShortname(String siteName)
    {
        String siteShortname = "";
        String[] unallowedCharacters = { "_", "!" };

        for (String removeChar : unallowedCharacters)
        {
            siteShortname = siteName.replace(removeChar, "");
        }

        return siteShortname.toLowerCase();
    }

    /**
     * Helper to consistently get the filename.
     * 
     * @param partFileName
     *            String Part Name of the file for uniquely identifying /
     *            mapping test data with the test
     * @return String fileName
     */
    protected static String getFileName(String partFileName)
    {
        String fileName = "";

        fileName = String.format("File%s-%s", UNIQUE_TESTDATA_STRING, partFileName);

        return fileName;
    }

    /**
     * Helper to consistently get the folderName.
     * 
     * @param partFolderName
     *            String Part Name of the folder for uniquely identifying /
     *            mapping test data with the test
     * @return String folderName
     */
    protected static String getFolderName(String partFolderName)
    {
        String folderName = "";

        folderName = String.format("Folder%s-%s", UNIQUE_TESTDATA_STRING, partFolderName);

        return folderName;
    }

    /**
     * Checks if driver is null, throws UnsupportedOperationException if so.
     * 
     * @param driver
     *            WebDrone Instance
     * @throws UnsupportedOperationException
     *             if driver is null
     */
    protected static void checkIfDriverNull(WebDrone driver)
    {
        if (driver == null)
        {
            throw new UnsupportedOperationException("WebDrone is required");
        }
    }

    /**
     * Common method to wait for the next solr indexing cycle.
     * 
     * @param driver
     *            WebDrone Instance
     * @param waitMiliSec
     *            Wait duration in milliseconds
     */
    @SuppressWarnings("deprecation")
    protected static void webDriverWait(WebDrone driver, long waitMiliSec)
    {
        if (waitMiliSec <= 0)
        {
            waitMiliSec = maxWaitTime;
        }
        logger.info("Waiting For: " + waitMiliSec / 1000 + " seconds");
        /*
         * try { Thread.sleep(waitMiliSec); //driver.refresh(); }
         * catch(InterruptedException ie) { throw new
         * RuntimeException("Wait interrupted / timed out"); }
         */
        driver.waitFor(waitMiliSec);
    }

    /**
     * Common method to get the Authentication details based on the username
     * specified.
     * 
     * @param authUsername
     *            String Username, User email
     * @return String array of auth details, consisting of username and password
     */
    public static String[] getAuthDetails(String authUsername)
    {
        String[] authDetails = { ADMIN_USERNAME, ADMIN_PASSWORD };

        if (authUsername == null)
        {
            authUsername = "";
        }

        if (!authUsername.isEmpty())
        {
            authDetails[0] = authUsername;
        }

        if (!authUsername.equals(ADMIN_USERNAME))
        {
            authDetails[1] = DEFAULT_PASSWORD;
        }

        return authDetails;
    }

    /**
     * This method is used to get the userDomail from the username value.
     * 
     * @param invitedUser
     * @return String
     */
    public static String getUserDomain(String invitedUser)
    {
        // TODO: Chiran: null check for NPE
        return invitedUser.substring(invitedUser.lastIndexOf("@") + 1, invitedUser.length());
    }

    /**
     * Helper to consistently get the comment.
     * 
     * @param partFolderName
     *            String Part Name of the folder for uniquely identifying /
     *            mapping test data with the test
     * @return String folderName
     */
    protected static String getComment(String partFolderName)
    {
        String comment = "";

        comment = String.format("Folder%s-%s", UNIQUE_TESTDATA_STRING, partFolderName);

        return comment;
    }

    protected static String getCSRFToken(WebDrone drone)
    {
        String cookieCSRF = extractCSRFToken(drone);
        try
        {
            return URLDecoder.decode(cookieCSRF, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return "";
        }
    }

    /**
     * Helper method to extract cookie value of Alfresco-CSRFToken
     * 
     * @return String token value
     */
    private static String extractCSRFToken(WebDrone drone)
    {
        Cookie cookie = drone.getCookie("Alfresco-CSRFToken");
        if (cookie != null)
        {
            return cookie.getValue();
        }
        return "";
    }

    /**
     * Helper to check the actual Result Vs expected
     * 
     * @param actualResult HttpResponse
     * @param expectedResult int
     * @return void
     */
    protected void checkResult(HttpResponse actualResult, int expectedResult)
    {
        if ((null == actualResult))
        {
            throw new IllegalArgumentException("Illegal values for actual or expected result");
        }
        else

        {
            Assert.assertEquals(actualResult.getStatusLine().getStatusCode(), expectedResult);
        }
    }

    /**
     * Retrieves the another drone object.
     * 
     * @return WebDrone
     */
    public WebDrone getSecondDrone()
    {
        WebDrone secondDrone = (WebDrone) ctx.getBean("webDrone");
        ShareTestProperty secondDroneProperties = (ShareTestProperty) ctx.getBean("shareTestProperties");
        dronePropertiesMap.put(secondDrone, secondDroneProperties);
        return secondDrone;
    }

    /**
     * Return the {@link WebDrone} Configured starting of test.
     * 
     * @return {@link WebDrone}
     */
    public WebDrone getDrone()
    {
        return drone;
    }

    /**
     * Return the domain name to be used in public apis
     * 
     * @param driver
     *            WebDrone instance
     * @param domain
     *            String
     * @return {@String domainName}
     */
    public static String getDomainForAPI(WebDrone driver, String domain)
    {
        if (!isAlfrescoVersionCloud(driver))
        {
            return "-system-";
        }
        return domain;
    }

    /**
     * @return
     */
    public String getShareUrl()
    {
        return testProperties.getShareUrl();
    }

    /**
     * Method to return Full User name
     * 
     * @param firstName
     * @return
     */
    public String getUserFullName(String firstName)
    {
        return firstName + " " + DEFAULT_LASTNAME;
    }

    /**
     * Method to return Full User name with e-mail id
     * 
     * @param firstName
     * @return
     */
    public String getUserFullNameWithEmail(String firstName, String email)
    {
        return firstName + " " + DEFAULT_LASTNAME + " (" + email + ")";
    }

    /**
     * Method to get Local Date of Today's date
     * 
     * @return
     */
    public LocalDate getToDaysLocalDate()
    {
        return new DateTime().toLocalDate();
    }

    /**
     * Method to get LocalDate of given dateTime
     * 
     * @param dateTime
     * @return {@link = LocalDate}
     */
    public LocalDate getLocalDate(DateTime dateTime)
    {
        return dateTime.toLocalDate();
    }

    /**
     * Checks if the current page is share page, throws PageException if not.
     * 
     * @param driver
     *            WebDrone Instance
     * @return SharePage
     * @throws PageException
     *             if the current page is not a share page
     */
    public static SharePage getSharePage(WebDrone driver)
    {
        checkIfDriverNull(driver);
        try
        {
            HtmlPage generalPage = driver.getCurrentPage().render(refreshDuration);
            return (SharePage) generalPage;
        }
        catch (PageException pe)
        {
            throw new PageException("Can not cast to SharePage: Current URL: " + driver.getCurrentUrl());
        }
    }

    /**
     * Helper to consistently get the Group Name.
     * 
     * @param testID
     *            String Name of the test for uniquely identifying / mapping
     *            test data with the test
     * @return String groupName
     */
    public static String getGroupName(String testID)
    {
        String groupsName = "";

        groupsName = String.format("Group%s%s", UNIQUE_TESTDATA_STRING, testID);

        return groupsName;
    }

    /**
     * @param format
     * @return
     */
    public static String getDate(String format)
    {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    /**
     * Method to get the custom Drone
     * 
     * @param language
     * @return
     */
    protected Map<BrowserPreference, Object> getCustomDroneWithLanguage(Locale language)
    {
        Map<BrowserPreference, Object> customProfile = new HashMap<BrowserPreference, Object>();
        customProfile.put(BrowserPreference.Language, language);
        return customProfile;
    }

    /**
     * Method to check whether the Alfresco Version is cloud
     */
    public static boolean isAlfrescoVersionCloud(WebDrone drone)
    {
        AlfrescoVersion version = drone.getProperties().getVersion();
        return (version.isCloud());
    }

    /**
     * Checks if the current page is share page, throws PageException if not.
     * 
     * @param driver
     *            WebDrone Instance
     * @return ShareErrorPopup
     * @throws PageException
     *             if the current page is not a share error popup page
     */
    public static SharePopup getShareErrorPopupPage(WebDrone driver)
    {
        checkIfDriverNull(driver);
        RenderTime t = new RenderTime(maxWaitTime);
        try
        {
            t.start();
            while (true)
            {
                try
                {
                    HtmlPage generalPage = driver.getCurrentPage().render();

                    if (generalPage instanceof SharePopup)
                    {
                        return (SharePopup) generalPage;
                    }
                }
                finally
                {
                    t.end();
                }
            }
        }
        catch (PageRenderTimeException pe)
        {
            throw new PageException("Cannot cast to ShareErrorPopUpPage: Current URL: " + driver.getCurrentUrl());
        }
    }

    /**
     * Method to get Cloud Console URL for specific Cloud env.
     */
    public static String getCloudConsoleURL(WebDrone drone)
    {
        String shareURL = dronePropertiesMap.get(drone).getShareUrl();
        CloudConsolePage cloudConsolePage = new CloudConsolePage(drone);
        return cloudConsolePage.getCloudConsoleUrl(shareURL);
    }

    /**
     * Getter method to get Drone Map
     * 
     * @return droneMap
     */
    public Map<String, WebDrone> getDroneMap()
    {
        return droneMap;
    }

    /**
     * This util method gets the random number for the given length of return
     * string.
     * 
     * @param int length
     * @return String
     */
    public static String getRandomStringWithNumders(int length)
    {
        StringBuilder rv = new StringBuilder();
        Random rnd = new Random();
        char from[] = "0123456789".toCharArray();

        for (int i = 0; i < length; i++)
            rv.append(from[rnd.nextInt((from.length - 1))]);
        return rv.toString();
    }

    /**
     * Compact proxy for the logger.trace method.
     * 
     * @param string to log
     */
    public static void traceLog(String string)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace(string);
        }
    }

}