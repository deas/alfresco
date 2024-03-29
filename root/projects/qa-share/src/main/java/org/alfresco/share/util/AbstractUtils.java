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
package org.alfresco.share.util;

import org.alfresco.po.share.*;
import org.alfresco.po.share.console.CloudConsolePage;
import org.alfresco.share.search.SearchKeys;
import org.alfresco.share.util.api.tokenKey.Layer7AuthorizationOnCloud;
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
import org.testng.annotations.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Class includes: Abstract test holds all common methods, These will be used
 * from within the ShareUser utils or tests.
 *
 * @author Meenal Bhave
 */
public abstract class AbstractUtils
{

    public enum PerformOperation
    {
        OK, CANCEL;
    }

    // Test Run Options
    protected static final Boolean deleteSiteFlag = true; // Indicates if Site
    // is to be deleted at
    // the end of the test
    // -> using
    // ShareUser.deleteSiteAPI

    private static Log logger = LogFactory.getLog(AbstractUtils.class);
    protected static ApplicationContext ctx;
    protected static String shareUrl;

    protected static String pathSharepoint;

    protected static String cloudUrlForHybrid;
    protected static String pentahoUserConsoleUrl;
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
    public static Long maxWaitTimeCloudSync;
    public static long refreshDuration = 25000;
    public static Integer retrySearchCount = 3;
    public static long maxDownloadWaitTime = 10000;
    // Test Data Related options
    protected static final String DATAPREP_OPTION = "SKIP";
    // if data is expected to be pre-loaded

    // Constants
    protected static final String SITE_VISIBILITY_PUBLIC = "public";
    protected static final String SITE_VISIBILITY_PRIVATE = "private";
    protected static final String SITE_VISIBILITY_MODERATED = "moderated";
    protected static final String DOCLIB = "DocumentLibrary";
    protected static final String REPO = "Repository";
    protected static final String SITES = "Sites";
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
    protected static final String DASHLET_MEMBERS = "site-members";
    protected static final String SITE_CONTENT_DASHLET = "site-contents";
    protected static final String USER_TASKS = "tasks";
    protected static final String SITE_ACTIVITIES = "site-activities";
    protected static final String SITE_NOTICE = "site-notice";
    protected static final String MY_DISCUSSIONS = "my-discussions";
    protected static final String MY_CALENDARE = "my-calendar";
    protected static final String TOP_SITE_CONTRIBUTOR_REPORT = "top-site-contributor-report";
    protected static final String SITE_CONTENT_BREAKDOWN_REPORT = "site-content-report";
    protected static final String IMAGE_PREVIEW = "image-preview";
    protected static final String WIKI = "wiki";
    protected static final String WEB_VIEW = "web-view";
    protected static final String RSS_FEED = "rss-feed";
    protected static final String SITE_LINKS = "site-links";
    protected static final String DATA_LISTS = "data-lists";
    protected static final String SITE_CALENDAR = "site-calendar";
    protected static final String SITE_PROFILE = "site-profile";
    protected static final String SITE_SEARCH = "site-search";

    // Activity Feeds
    protected static final String FEED_CONTENT_ADDED = " added";
    protected static final String FEED_CONTENT_CREATED = " created";
    protected static final String FEED_CONTENT_DELETED = " deleted";
    protected static final String FEED_CONTENT_EDITED = " edited";
    protected static final String FEED_CONTENT_UPDATED = " updated";
    protected static final String FEED_CONTENT_RENAMED = " renamed";
    protected static final String FEED_CONTENT_LIKED = " liked";
    protected static final String FEED_COMMENTED_ON = " commented on";
    protected static final String FEED_UPDATED_COMMENT_ON = " updated comment on";
    protected static final String FEED_COMMENT_DELETED = " deleted a comment";
    protected static final String FEED_ROLE_CHANGED = " role changed";
    protected static final String FEED_COMMENTED_FROM = " from ";
    protected static final String FEED_COMMENTED_TO = " to ";
    protected static final String FEED_LOCATION = " in ";
    protected static final String FEED_FOR_FOLDER = " folder ";
    protected static final String FEED_FOR_FILE = " document ";
    protected static final String FEED_FOR_CALENDAR_EVENT = " calendar event ";
    protected static final String FEED_FOR_WIKI_PAGE = " wiki page ";
    protected static final String FEED_FOR_DATA_LIST = " data list ";
    protected static final String FEED_FOR_BLOG_POST = " blog post ";
    protected static final String DOCUMENT_LOCKED_BY_YOU_MESSAGE = "This document is locked by you.";
    protected static final String LAST_SYNC_FAILED_MESSAGE = "Last sync failed.";

    // Test Run and Users Info: This is now a part of test.properties
    protected static String username;
    protected static String password;
    protected static String downloadDirectory;
    protected static String mimeTypes;
    protected static String googleUserName;
    protected static String googlePassword;
    protected static String serviceAccountEmail;
    protected static String serviceAccountPKCS12FileName;
    protected static String googleDriveUserName;
    protected static String googleDrivePassword;
    protected static String serviceDriveAccountEmail;
    protected static String serviceDriveAccountPKCS12FileName;
    protected static boolean hybridEnabled;
    protected static String UNIQUE_TESTRUN_NAME;
    protected static String DOMAIN_FREE;
    protected static String DOMAIN_PREMIUM;
    protected static String DOMAIN_HYBRID;
    protected static String DOMAIN_LIVE_SEARCH;
    protected static String SUPERADMIN_USERNAME;
    protected static String SUPERADMIN_PASSWORD;
    protected static String ADMIN_USERNAME;
    protected static String ADMIN_PASSWORD;
    protected static String HEADER_KEY;
    protected static String DEFAULT_USER;
    protected static String UNIQUE_TESTDATA_STRING;
    protected static String nodePort = "8080";
    protected static String jmxrmiPort;
    protected static String jmxrmiUser;
    protected static String jmxrmiPassword;
    protected static String blogUrl;
    protected static String blogUsername;
    protected static String blogPassword;
    protected static String ftpPort;
    protected static String sshHost;
    protected static int serverShhPort;
    protected static String serverUser;
    protected static String serverPass;
    protected static boolean isSecureSession;
    protected static String pathToKeys;
    protected static String replicationEndPointHost;
    protected static String serviceAccountPKCS12FilePath;
    protected static String serviceDriveAccountPKCS12FilePath;
    protected static String layer7Disabled;
    protected static String apiKey;
    protected static String apiSecretKey;

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
    public final String CONTENT_FAVOURITE_TOOLTIP = "content.favourite.tooltip";
    public final String CONTENT_UNFAVOURITE_TOOLTIP = "content.unfavourite.tooltip";
    public final String FOLDER_FAVOURITE_TOOLTIP = "folder.favourite.tooltip";
    public final String FOLDER_UNFAVOURITE_TOOLTIP = "folder.unfavourite.tooltip";
    public static String licenseShare;

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
        pathSharepoint = testProperties.getPathSharepoint();
        cloudUrlForHybrid = testProperties.getCloudUrlForHybrid();
        pentahoUserConsoleUrl = testProperties.getPentahoUserConsoleUrl();
        username = testProperties.getUsername();
        password = testProperties.getPassword();
        alfrescoVersion = testProperties.getAlfrescoVersion();
        downloadDirectory = testProperties.getDownloadDirectory();
        googleUserName = testProperties.getGoogleUserName();
        googlePassword = testProperties.getGooglePassword();
        serviceAccountEmail = testProperties.getServiceAccountEmail();
        serviceAccountPKCS12FileName = testProperties.getServiceAccountPKCS12FileName();
        googleDriveUserName = testProperties.getGoogleDriveUserName();
        googleDrivePassword = testProperties.getGoogleDrivePassword();
        serviceDriveAccountEmail = testProperties.getServiceDriveAccountEmail();
        serviceDriveAccountPKCS12FileName = testProperties.getServiceDriveAccountPKCS12FileName();
        hybridEnabled = testProperties.isHybridEnabled();
        UNIQUE_TESTRUN_NAME = testProperties.getuniqueTestRunName();
        DOMAIN_FREE = testProperties.getdomainFree();
        DOMAIN_PREMIUM = testProperties.getdomainPremium();
        DOMAIN_HYBRID = testProperties.getdomainHybrid();
        DOMAIN_LIVE_SEARCH = testProperties.getdomainLiveSearch();
        DEFAULT_USER = testProperties.getdefaultUser();
        UNIQUE_TESTDATA_STRING = testProperties.getuniqueTestDataString();
        SUPERADMIN_USERNAME = testProperties.getSuperadminUsername();
        SUPERADMIN_PASSWORD = testProperties.getSuperadminPassword();
        ADMIN_USERNAME = testProperties.getadminUsername();
        ADMIN_PASSWORD = testProperties.getadminPassword();
        HEADER_KEY = testProperties.getHeaderKey();
        mimeTypes = testProperties.getMimeTypes();
        licenseShare = testProperties.getLicenseShare();
        nodePort = testProperties.getNodePort();
        ftpPort = testProperties.getFtpPort();
        jmxrmiPort = testProperties.getJmxPort();
        jmxrmiUser = testProperties.getJmxUser();
        jmxrmiPassword = testProperties.getJmxPassword();
        blogUrl = testProperties.getBlogUrl();
        blogUsername = testProperties.getBlogUsername();
        blogPassword = testProperties.getBlogPassword();
        serverShhPort = testProperties.getSshPort();
        serverUser = testProperties.getSshLogin();
        serverPass = testProperties.getSshPassword();
        isSecureSession = testProperties.isSecureSession();
        replicationEndPointHost = testProperties.getreplicationEndPointHost();
        maxWaitTimeCloudSync = Long.parseLong(testProperties.getMaxWaitTimeCloudSync());
        RESULTS_FOLDER = SRC_ROOT + "test-output" + SLASH + UNIQUE_TESTRUN_NAME + SLASH;
        pathToKeys = AbstractUtils.DATA_FOLDER + "ssh_keys" + File.separator + "build.ppk";
        serviceAccountPKCS12FilePath = AbstractUtils.DATA_FOLDER + "p12_keys" + File.separator + serviceAccountPKCS12FileName;
        serviceDriveAccountPKCS12FilePath = AbstractUtils.DATA_FOLDER + "p12_keys" + File.separator + serviceDriveAccountPKCS12FileName;
        layer7Disabled = testProperties.getLayer7Disabled();
        apiKey = testProperties.getApiKey();
        apiSecretKey = testProperties.getApiSecretKey();

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
                    try
                    {
                        ShareUtil.logout(entry.getValue());
                    }
                    catch (Exception e)
                    {
                        logger.error("If it's tests associated with admin-console-summary-page. it's normal. If not - we have a problem.");
                    }
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
     * @param userInfo
     * @return DashBoardPage
     * @throws Exception if error
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
     * @param methodName String This is the Test Name / ID
     * @return void
     * @throws Exception if error
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
     * @param methodName String This is the Test Name / ID
     * @return void
     * @throws Exception if error
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
        // try
        // {
        // saveOsScreenShot(methodName);
        // }
        // catch (AWTException e)
        // {
        // logger.error("Not able to take the OS screen shot: " + e.getMessage());
        // }
    }

    /**
     * Take OS ScreenShot
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
     */
    public static String getTestName()
    {
        String testID = Thread.currentThread().getStackTrace()[2].getMethodName();
        return getTestName(testID);
    }

    /**
     * Helper returns the test / methodname. This needs to be called as the 1st
     * step of the test. Common Test code can later be introduced here.
     *
     * @return String testcaseName
     */
    public static String getTestName(String testID)
    {
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
     * @param testName String test case ID
     * @return N/A
     */
    protected void testCleanup(WebDrone driver, String testName)
    {
        logger.trace("Test Done: " + testName);

        // Perform Log out @after method, to allow for the activities to happen
        // in the same user session via after method
        if (driver != null)
        {
            ShareUser.logout(driver);
        }
    }

    /**
     * Helper to report error details for a test.
     *
     * @param driver   WebDrone Instance
     * @param testName String test case ID
     * @param t        Throwable Error & Exception to include testng assert
     *                 failures being reported as Errors
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
        String apiUrl = dronePropertiesMap.get(drone).getApiUrl();
        if (apiUrl == null || "".equals(apiUrl))
        {
            String shareURL = dronePropertiesMap.get(drone).getShareUrl();
            logger.info("getAPIURL: -- Using URL - " + shareURL);
            apiUrl = shareURL.replace("my.alfresco.me/share", "api.alfresco.me/");
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
        }
        else
        {
            logger.info("getAPIURL: -- configured apiUrl - " + apiUrl);
        }

        return apiUrl;
    }

    /**
     * Helper to return the stack trace as a string for reporting purposes.
     *
     * @param ex exception / error
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
     * @param filename String Complete path of the file to be created
     * @param contents String Contents for text file
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
     * @param cssClassName            : css Selector such as [class='filename']
     * @param linkTextOfElementToFind String
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
     * @param testID String Name of the test for uniquely identifying / mapping
     *               test data with the test
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
     * @param testID String Name of the test for uniquely identifying / mapping
     *               test data with the test
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
     * @param testID String Name of the test for uniquely identifying / mapping
     *               test data with the test
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
     * @param domainID String to be prefixed to DOMAIN_FREE
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
     * @param testID String Name of the test for uniquely identifying / mapping
     *               test data with the test
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
     * @param siteName String Name of the test for uniquely identifying / mapping
     *                 test data with the test
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
     * @param partFileName String Part Name of the file for uniquely identifying /
     *                     mapping test data with the test
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
     * @param partFolderName String Part Name of the folder for uniquely identifying /
     *                       mapping test data with the test
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
     * @param driver WebDrone Instance
     * @throws UnsupportedOperationException if driver is null
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
     * @param driver      WebDrone Instance
     * @param waitMiliSec Wait duration in milliseconds
     */
    @SuppressWarnings("deprecation")
    protected static HtmlPage webDriverWait(WebDrone driver, long waitMiliSec)
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
        return getSharePage(driver);
    }

    /**
     * Common method to get the Authentication details based on the username
     * specified.
     *
     * @param authUsername String Username, User email
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
     * @param partFolderName String Part Name of the folder for uniquely identifying /
     *                       mapping test data with the test
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
     * @param actualResult   HttpResponse
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
        droneMap.put("second_drone", secondDrone);
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
     * @param driver WebDrone instance
     * @param domain String
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
     * @param driver WebDrone Instance
     * @return SharePage
     * @throws PageException if the current page is not a share page
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
     * @param testID String Name of the test for uniquely identifying / mapping
     *               test data with the test
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
     * @param driver WebDrone Instance
     * @return ShareErrorPopup
     * @throws PageException if the current page is not a share error popup page
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
     * @param length int
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

    /**
     * This util method returns a random string of letters for the given length.
     *
     * @param length int
     * @return String
     */
    public static String getRandomString(int length)
    {
        StringBuilder rv = new StringBuilder();
        Random rnd = new Random();
        char from[] = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        for (int i = 0; i < length; i++)
        {
            rv.append(from[rnd.nextInt((from.length - 1))]);
        }
        return rv.toString();
    }

    /**
     * This util method returns a random string of letters and spaces matching the
     * lengths and proportion of English words for the given string length.
     *
     * @param length int
     * @return String
     */
    public static String getNaturalString(int length)
    {
        StringBuilder rv = new StringBuilder();
        Random rnd = new Random();
        int[] wordLengths = { 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9,
                9, 9, 10, 10, 10, 11, 11, 12, 13 };

        char from[] = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        int wordLength = wordLengths[rnd.nextInt((wordLengths.length - 1))];
        for (int i = 0; i < length; i++)
        {
            if (wordLength <= 0 && i < length - 5)
            {
                rv.append(" ");
                wordLength = wordLengths[rnd.nextInt((wordLengths.length - 1))];
                continue;
            }
            rv.append(from[rnd.nextInt((from.length - 1))]);
            wordLength--;
        }
        if (rv.charAt(rv.length() - 1) == ' ')
        {
            rv.setCharAt(rv.length() - 1, from[rnd.nextInt((from.length - 1))]);
        }
        return rv.toString();
    }

    /**
     * This util method resizes a given string to a given length.
     * If the string is shorter the end of the string will be cropped.
     * If the string is longer the extra length will be populated with random characters.
     *
     * @param string The string to be resized.
     * @param length The length of the new string.
     * @return String The new string.
     */
    public static String getResizedString(String string, int length)
    {
        if (string == null)
        {
            string = "";
        }

        StringBuilder rv;

        if (string.length() == length)
        {
            return string;
        }
        else if (string.length() < length)
        {
            rv = new StringBuilder(getNaturalString(length));
            rv.replace(0, string.length(), string);
        }
        else
        {
            rv = new StringBuilder(string);
            rv.setLength(length);
        }
        return rv.toString();
    }

    /**
     * This util method returns a file.
     *
     * @param fileName String
     * @param sizeMB   int
     * @return File
     */
    public static File getFileWithSize(String fileName, int sizeMB)
    {
        byte[] buffer = getRandomString(1024 * 1024).getBytes();
        int number_of_lines = sizeMB;

        try
        {
            FileChannel rwChannel = new RandomAccessFile(fileName, "rw").getChannel();
            ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, buffer.length * number_of_lines);
            for (int i = 0; i < number_of_lines; i++)
            {
                wrBuf.put(buffer);
            }
            rwChannel.close();
        }
        catch (IOException ex)
        {
            logger.error("Unable to create file method getFileWithSize", ex);
        }

        File file1 = new File(fileName);
        return file1;
    }

    /**
     * Checks if a browser window is open with a title matching the given string.
     *
     * @param windowName
     * @param driver     driverObj
     * @return boolean
     */
    public boolean isWindowOpened(WebDrone driver, String windowName)
    {
        Set<String> windowHandles = driver.getWindowHandles();

        for (String windowHandle : windowHandles)
        {
            driver.switchToWindow(windowHandle);
            logger.info(driver.getTitle());
            if (driver.getTitle().equals(windowName))
            {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("hiding")
    public static class CustomStringList<String> extends ArrayList<String>
    {
        private static final long serialVersionUID = 1L;

        public CustomStringList(Collection<String> c)
        {
            super(c);
        }

        public CustomStringList()
        {
            super();
        }

        @Override
        public boolean contains(Object o)
        {
            java.lang.String paramStr = (java.lang.String) o;
            for (String s : this)
            {
                if (paramStr.equalsIgnoreCase((java.lang.String) s))
                    return true;
            }
            return false;
        }

    }

    /**
     * Helper to consistently get the tagName.
     *
     * @param partTagName String Part Name of the tag for uniquely identifying /
     *                    mapping test data with the test
     * @return String tagName
     */
    protected static String getTagName(String partTagName)
    {
        String tagName = "";

        // Tag names are displayed in lower case to convert to lower case to help matching in tests.
        tagName = String.format("tag%s-%s", UNIQUE_TESTDATA_STRING, partTagName).toLowerCase();

        return tagName;
    }

    /**
     * Method to get the DependsOnMethod name
     *
     * @param cls
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected String getDependsOnMethodName(Class cls) throws Exception
    {
        Test test = cls.getMethod(Thread.currentThread().getStackTrace()[2].getMethodName()).getAnnotation(Test.class);
        return test.dependsOnMethods()[0];
    }

    /**
     * Util to switch drone to window with the specified name
     *
     * @param driver
     * @param windowName
     * @return boolean <tt>true</tt> if specified window is found
     */
    public boolean switchToWindowName(WebDrone driver, String windowName)
    {
        for (String windowHandle : driver.getWindowHandles())
        {
            driver.switchToWindow(windowHandle);
            logger.info("Found Open Window: " + driver.getTitle());
            if (driver.getTitle().equalsIgnoreCase(windowName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Refreshes and returns the current page: throws PageException if not a share page.
     *
     * @param driver WebDrone Instance
     * @return HtmlPage
     * @throws PageException if the current page is not a share page
     */
    public static HtmlPage refreshSharePage(WebDrone driver)
    {
        checkIfDriverNull(driver);
        driver.refresh();
        return getCurrentPage(driver);
    }

    /**
     * Returns the current page: throws PageException if not a share page.
     *
     * @param driver WebDrone Instance
     * @return HtmlPage
     * @throws PageException if the current page is not a share page
     */
    public static HtmlPage getCurrentPage(WebDrone driver)
    {
        checkIfDriverNull(driver);
        try
        {
            HtmlPage generalPage = driver.getCurrentPage().render(refreshDuration);
            return generalPage;
        }
        catch (PageException pe)
        {
            throw new PageException("Unable to return Share Page: Current URL: " + driver.getCurrentUrl());
        }
    }

    /**
     * Returns the text for file from Download Directory
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getTextFromDownloadDirectoryFile(String fileName) throws IOException
    {
        String filePath = new File(downloadDirectory + fileName).getAbsolutePath();

        String everything = null;
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null)
            {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
        }
        catch (FileNotFoundException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Expected file not found " + fileName);
            }
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Failed to read file " + fileName);
            }
        }
        finally
        {
            if (br != null)
            {
                br.close();
            }
        }
        return everything;
    }

    /**
     * Returns the text for file from Download Directory
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static void changeTextForDownloadDirectoryFile(String fileName, String content) throws IOException
    {
        File file = new File(downloadDirectory + fileName);
        FileOutputStream fop = new FileOutputStream(file);
        try
        {

            // get the content in bytes
            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
        }
        catch (FileNotFoundException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Expected file not found " + fileName);
            }
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Failed to write text into file " + fileName);
            }
        }
        finally
        {
            fop.flush();
            fop.close();

        }
    }

    /**
     * Method to get the path for SharePoint
     */
    public static String getPathSharepoint(WebDrone drone)
    {
        String pathSharepoint = dronePropertiesMap.get(drone).getPathSharepoint();
        return pathSharepoint;
    }

    /**
     * Method to get token Key for the user
     */
    public static String getTokenKey(WebDrone drone, String userName, String password)
    {
        String tokenKey = "";

        if (!Boolean.parseBoolean(dronePropertiesMap.get(drone).getLayer7Disabled()))
        {
            String currentUrl = drone.getCurrentUrl();
            if (dronePropertiesMap.get(drone).getAlfrescoVersion().equals(AlfrescoVersion.MyAlfresco))
            {
                Layer7AuthorizationOnCloud jetty = new Layer7AuthorizationOnCloud(drone);
                tokenKey = jetty.getUserTokenKey(userName, password);
                drone.navigateTo(currentUrl);
            }
        }
        else
        {
            System.out.println("Layer7 is not enabled, when Layer7Disabled = true");
        }

        return tokenKey;
    }

    public static boolean isLayer7Enabled()
    {
        return !Boolean.parseBoolean(layer7Disabled);
    }
}
