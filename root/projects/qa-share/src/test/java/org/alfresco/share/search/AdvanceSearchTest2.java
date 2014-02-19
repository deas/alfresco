package org.alfresco.share.search;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.search.SearchResultItem;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SuppressWarnings("unused")
public class AdvanceSearchTest2 extends AbstractTests
{
    private static Log logger = LogFactory.getLog(AdvanceSearchTest2.class);

    Date todayDate = new Date();

    private static String testPassword = DEFAULT_PASSWORD;

    protected String testUser;

    protected String siteName = "";

    private static final String TEST_HTML_FILE = "Test1.html";
    private static final String TEST_TXT_FILE = "Test2.txt";
    private static final String TEST_DOC_FILE = "Test3.doc";
    private static final String TEST_JPG_FILE = "Test4.jpg";
    private static final String TEST_PDF_FILE = "TestPDFImap.pdf";
    private static final String TEST_GIF_FILE = "Test6.gif";
    private static final String TEST_FILE_WITH_1KB_SIZE = "ALF-5024-1KB-FILE.txt";
    private static final String TEST_FILE_WITH_1MB_SIZE = "ALF-5024-1MB-FILE.txt";
    private static final String TEST_FILE_WITH_2MB_SIZE = "ALF-5024-2MB-FILE.txt";

    /**
     * Class includes: Tests from TestLink in Area: Advanced Search Tests
     * <ul>
     * <li>Test searches using various Properties, content, Folder</li>
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

    /**
     * DataPreparation method - ALF-4978
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create a content with a name }{+_)(&^%$#@! in the document library
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4978() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = "}{+_)(&^%$#@!";

        try
        {
            // User
            String[] testUserInfo = new String[] { testUser };
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            String[] fileInfo = { fileName };
            ShareUser.uploadFileInFolder(drone, fileInfo);
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
     * Test - ALF-4978:Wildcard search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Content form</li>
     * <li>In the keyword Search text of the content search form enter !@#$%^&*()_+:"|<>?;</li>
     * <li>Validate the search results are returned as zero</li>
     * <li>Go Back to Advance Search from</li>
     * <li>In the Keyword search field of the content search form enter }{+_)(&^%$#@!</li>
     * <li>Validate the search results are returned as zero</li>
     * </ul>
     */
    @Test
    public void ALF_4978()
    {
        /** Start Test */
        String testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText_1 = "!@#$%^&*()_+:\"|<>?;";
        String searchText_2 = "}{+_)(&^%$#@!";
        Map<String, String> keyWordSearchText = new HashMap<String, String>();

        // Initialise search data
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        boolean searchOk;

        try
        {
            // Login
            ShareUser.login(drone, testUser, testPassword);

            // Doing the first invalid Search
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText_1);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, SERACH_ZERO_CONTENT);

            Assert.assertTrue(searchOk);

            // Searching for valid string with content
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText_2);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, SERACH_ZERO_CONTENT);
            Assert.assertTrue(searchOk);
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
     * DataPreparation method - ALF-4979
     * <ul>
     * <li>Create User</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4979() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
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
     * Test - ALF-4978:Empty search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>Click on Search button without any Folder Search content</li>
     * <li>Validate the search results are returned as zero</li>
     * </ul>
     */

    @Test
    public void ALF_4979()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);

        boolean searchOk;
        Map<String, String> keyWordSearchText = new HashMap<String, String>();

        try
        {
            List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

            ShareUser.login(drone, testUser, testPassword);

            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, SERACH_ZERO_CONTENT);
            Assert.assertTrue(searchOk);
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
     * DataPreparation method - ALF-4980
     * <ul>
     * <li>Create User</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4980() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
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
     * Test - ALF-4980:Empty search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Content form</li>
     * <li>Click on Search button without any Content Search content</li>
     * <li>Validate the search results are returned as zero</li>
     * </ul>
     */
    @Test
    public void ALF_4980()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);

        boolean searchOk;
        Map<String, String> keyWordSearchText = new HashMap<String, String>();

        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);
            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, SERACH_ZERO_CONTENT);
            Assert.assertTrue(searchOk);
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
     * DataPreparation method - ALF-4981
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create a Folder with a name }{+_)(&^%$#@! in the document library
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4981() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);
        String folderName = "}{+_)(&^%$#@!";
        String folderDescription = folderName;

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            ShareUserSitePage.createFolder(drone, folderName, folderDescription);
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
     * Test - ALF-4981:Wildcard search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the keyword Search text of the Folder search form enter !@#$%^&*()_+:"|<>?;</li>
     * <li>Validate the search results are returned as zero</li>
     * <li>Go Back to Advance Search from</li>
     * <li>In the Keyword search field of the Folder search form enter }{+_)(&^%$#@!</li>
     * <li>Validate the search results are returned as zero</li>
     * </ul>
     */
    @Test
    public void ALF_4981()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */

        String testUser = getUserNameFreeDomain(testName);

        String searchText_1 = "!@#$%^&*()_+:\"|<>?;";
        String searchText_2 = "}{+_)(&^%$#@!";

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        boolean searchOk;

        try
        {
            List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText_1);

            ShareUser.login(drone, testUser, testPassword);

            // Doing the first invalid Search
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, SERACH_ZERO_CONTENT);
            Assert.assertTrue(searchOk);

            // Searching for valid string with content
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText_2);

            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            searchOk = ShareUserSearchPage.isSearchItemAvailable(drone, SERACH_ZERO_CONTENT);
            Assert.assertTrue(searchOk);
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
     * DataPreparation method - ALF-4982
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4982() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
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
     * Test - ALF-4982:Too Long Data Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the keyword / Name / Tiltle / Desc Search text of the Content search form enter more than 1024 symbols</li>
     * <li>Verify the symbols entered successfully and entered data is cut to 1024 symbols</li>
     * </ul>
     */
    @Test(groups="Enterprise4.2Bug")
    public void ALF_4982()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */

        String testUser = getUserNameFreeDomain(testName);
        String searchText = ShareUser.getRandomStringWithNumders(1030);
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openUserDashboard(drone);

            AdvanceSearchPage contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Search too long text in KEYWORD textbox
            contentSearchPage.inputKeyword(searchText);
            String valueEntered = contentSearchPage.getKeyword();
            Assert.assertEquals(valueEntered.length(), 1024);
            
            // Search too long text in NAME textbox
            contentSearchPage.inputName(searchText);
            valueEntered = contentSearchPage.getName();
            Assert.assertEquals(valueEntered.length(), 1024, "ALF-4839 - No restriction of length for Name field.");
            
            // Search too long text in TITLE textbox
            contentSearchPage.inputTitle(searchText);
            valueEntered = contentSearchPage.getTitle();
            Assert.assertEquals(valueEntered.length(), 1024, "ALF-4839 - No restriction of length for Title field.");
            
            // Search too long text in DESCRIPTION textbox
            contentSearchPage.inputDescription(searchText);
            valueEntered = contentSearchPage.getDescription();
            Assert.assertEquals(valueEntered.length(), 1024, "ALF-4839 - No restriction of length for Description field.");
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
     * DataPreparation method - ALF-4983
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4983() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName + "1");

        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] filesWithNoContent = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };
        String[] fileContents = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] fileInfo = new String[1];

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], null);
            ShareUserSitePage.createFolder(drone, folders[1], null);
            ShareUserSitePage.createFolder(drone, folders[2], null);
            ShareUserSitePage.createFolder(drone, folders[3], null);

            // Creating files
            fileInfo[0] = filesWithNoContent[0];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[1];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[2];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[3];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo = new String[3];
            // Creating files with given content.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(filesWithContent[0]);
            contentDetails.setContent(fileContents[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
            
            contentDetails.setName(filesWithContent[1]);
            contentDetails.setContent(fileContents[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[2]);
            contentDetails.setContent(fileContents[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[3]);
            contentDetails.setContent(fileContents[3]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
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
     * Test - ALF-4983: Keyword Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the keyword enter "house","*echno","tran*"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * <li>Go Back to Advance Search from</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2Bug")
    public void ALF_4983()
    {
        /** Start Test */
        String testName = getTestName();
        String siteName = getSiteName(testName + "1");
        /** Test Data Setup */
        
        String testUser = getUserNameFreeDomain(testName);

        String[] searchText = { siteName + "_house", "*echno", "tran*" };

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid keyword string
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[0]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[0], siteName + "_My 4", true));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_House my"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_House Techno my"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_House Techno Trance my"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_My 1"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_My 3"));

            // Searching for valid keyword string
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[1]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[1], siteName + "_My 4", true));

            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_Techno my"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_House Techno my"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_House Techno Trance my"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_My 2"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_My 3"));

            // Searching for valid keyword string
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[2]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[2], siteName + "_My 4", true));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_House Techno Trance my"));
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
     * DataPreparation method - ALF-4984
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4984() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] filesWithNoContent = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };
        String[] fileContents = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] fileInfo = new String[1];

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], null);
            ShareUserSitePage.createFolder(drone, folders[1], null);
            ShareUserSitePage.createFolder(drone, folders[2], null);
            ShareUserSitePage.createFolder(drone, folders[3], null);

            // Creating files
            fileInfo[0] = filesWithNoContent[0];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[1];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[2];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[3];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo = new String[3];
            // Creating files with given content.
            fileInfo[0] = filesWithContent[0];
            fileInfo[1] = DOCLIB;
            fileInfo[2] = fileContents[0];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);

            fileInfo[0] = filesWithContent[1];
            fileInfo[2] = fileContents[1];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);

            fileInfo[0] = filesWithContent[2];
            fileInfo[2] = fileContents[2];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);

            fileInfo[0] = filesWithContent[3];
            fileInfo[2] = fileContents[3];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);
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
     * Test - ALF-4984: Keyword Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the keyword enter "house","*echno","tran*"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4984()
    {
        /** Start Test */
        testName = getTestName();
        String siteName = getSiteName(testName);
        /** Test Data Setup */

        String testUser = getUserNameFreeDomain(testName);
        String[] searchText = { siteName + "_house", "*echno", "tran*" };

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid keyword string
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[0]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[0], siteName + "_House", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[0], siteName + "_House Techno", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[0], siteName + "_House Techno Trance", true));

            // Searching for valid keyword string
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[1]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[1], siteName + "_Techno", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[1], siteName + "_House Techno", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[1], siteName + "_House Techno Trance", true));

            // Searching for valid keyword string
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[2]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[2], siteName + "_House Techno Trance", true));
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
     * DataPreparation method - ALF-4985
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4985() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House", "Techno", "House Techno", "House Techno Trance" };
        String[] filesWithNoContent = { "House my", "Techno my", "House Techno my", "House Techno Trance my" };
        String[] filesWithContent = { "My 1", "My 2", "My 3", "My 4" };
        String[] fileContents = { "House", "Techno", "House Techno", "House Techno Trance" };
        String[] fileInfo = new String[1];

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], null);
            ShareUserSitePage.createFolder(drone, folders[1], null);
            ShareUserSitePage.createFolder(drone, folders[2], null);
            ShareUserSitePage.createFolder(drone, folders[3], null);

            // Creating files
            fileInfo[0] = filesWithNoContent[0];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[1];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[2];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[3];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo = new String[3];
            // Creating files with given content.
            fileInfo[0] = filesWithContent[0];
            fileInfo[1] = DOCLIB;
            fileInfo[2] = fileContents[0];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);

            fileInfo[0] = filesWithContent[1];
            fileInfo[2] = fileContents[1];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);

            fileInfo[0] = filesWithContent[2];
            fileInfo[2] = fileContents[2];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);

            fileInfo[0] = filesWithContent[3];
            fileInfo[2] = fileContents[3];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);
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
     * Test - ALF-4985: Name Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the name, enter "house","*echno","tran*"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4985()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String[] searchText = { "house", "*echno", "tran*" };

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Name string
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[0], "House Techno Trance my", true));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, "House my"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, "House Techno my"));

            // Searching for valid Name string
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[1]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[1], "House Techno Trance my", true));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, "Techno my"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, "House Techno my"));

            // Searching for valid Name string
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[2]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[2], "House Techno Trance my", true));
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
     * DataPreparation method - ALF-4986
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4986() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] filesWithNoContent = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };
        String[] fileContents = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] fileInfo = new String[1];

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], null);
            ShareUserSitePage.createFolder(drone, folders[1], null);
            ShareUserSitePage.createFolder(drone, folders[2], null);
            ShareUserSitePage.createFolder(drone, folders[3], null);

            // Creating files
            fileInfo[0] = filesWithNoContent[0];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[1];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[2];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo[0] = filesWithNoContent[3];
            ShareUser.uploadFileInFolder(drone, fileInfo);

            fileInfo = new String[3];
            // Creating files with given content.
            fileInfo[0] = filesWithContent[0];
            fileInfo[1] = DOCLIB;
            fileInfo[2] = fileContents[0];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);

            fileInfo[0] = filesWithContent[1];
            fileInfo[2] = fileContents[1];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);

            fileInfo[0] = filesWithContent[2];
            fileInfo[2] = fileContents[2];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);

            fileInfo[0] = filesWithContent[3];
            fileInfo[2] = fileContents[3];
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);
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
     * Test - ALF-4986: Name Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the name, enter "house","*echno","tran*"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4986()
    {
        /** Start Test */
        String testName = getTestName();
        String siteName = getSiteName(testName);
        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String[] searchText = { siteName + "_house", "*echno", "tran*" };

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Name string
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[0], siteName + "_House Techno Trance", true));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_House"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_House Techno"));

            // Searching for valid Name string
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[1]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[1], siteName + "_House Techno Trance", true));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_Techno"));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, siteName + "_House Techno"));

            // Searching for valid Name string
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[2]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[2], siteName + "_House Techno Trance", true));
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
     * DataPreparation method - ALF-4987
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4987() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);
        String[] folders = { "House 1", "House 2", "Techno" };
        String[] folderTitles = { "House", "Techno" };
        String[] filesWithTitle = { "House my 1", "House my 2", "Techno my" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], folderTitles[0], null);
            ShareUserSitePage.createFolder(drone, folders[1], folderTitles[1], null);
            ShareUserSitePage.createFolder(drone, folders[2], folderTitles[0], null);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(filesWithTitle[0]);
            contentDetails.setTitle(folderTitles[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[1]);
            contentDetails.setTitle(folderTitles[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[2]);
            contentDetails.setTitle(folderTitles[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
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
     * Test - ALF-4987: Title Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the title, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4987()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Title string
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "House my 1", true));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, "Techno my"));
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
     * DataPreparation method - ALF-4988
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4988() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "Techno", "House Techno Trance" };
        String[] folderTitles = { "House", "Techno" };
        String[] filesWithTitle = { "House my 1", "House my 2", "Techno my" };
        String[] filesWithContentAndTitle = { "My 1", "My 2", "My 3" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
            
            ShareUser.openDocumentLibrary(drone); 
            
            ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(filesWithTitle[0]);
            contentDetails.setTitle(folderTitles[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[1]);
            contentDetails.setTitle(folderTitles[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[2]);
            contentDetails.setTitle(folderTitles[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating files with given Title and description.
            contentDetails.setName(filesWithContentAndTitle[0]);
            contentDetails.setTitle(folderTitles[0]);
            contentDetails.setDescription(folderTitles[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndTitle[1]);
            contentDetails.setTitle(folderTitles[0]);
            contentDetails.setDescription(folderTitles[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndTitle[2]);
            contentDetails.setTitle(folderTitles[1]);
            contentDetails.setDescription(folderTitles[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
            
            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], folderTitles[0], null);
            ShareUserSitePage.createFolder(drone, folders[1], folderTitles[1], null);
            ShareUserSitePage.createFolder(drone, folders[2], folderTitles[0], null);
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
     * Test - ALF-4988: Title Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the title, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4988()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Title string
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);
            
            SearchResultItem searchResultItem = ShareUserSearchPage.findInSearchResults(drone,"House 1");
            
            Assert.assertNotNull(searchResultItem);
            Assert.assertTrue(searchResultItem.isFolder());
            
            searchResultItem = ShareUserSearchPage.findInSearchResults(drone,"Techno");
            
            Assert.assertNotNull(searchResultItem);
            Assert.assertTrue(searchResultItem.isFolder());
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
     * DataPreparation method - ALF-4989
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4989() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "Techno" };
        String[] descriptions = { "House", "Techno" };
        String[] files = { "House my 1", "House my 2", "Techno my" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[1], descriptions[1]);
            ShareUserSitePage.createFolder(drone, folders[2], descriptions[0]);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(files[0]);
            contentDetails.setDescription(descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[1]);
            contentDetails.setDescription(descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[2]);
            contentDetails.setDescription(descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
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
     * Test - ALF-4989: Description Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the description, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4989()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Description string
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "Techno my", true));
            Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, "House my 1"));
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
     * DataPreparation method - ALF-4990
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4990() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "Techno" };
        String[] descriptions = { "House", "Techno" };
        String[] files = { "House my 1", "House my 2", "Techno my" };
        String[] filesWithContentAndDescription = { "My 1", "My 2", "My 3" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
            
            ShareUser.openDocumentLibrary(drone);
            
            ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(files[0]);
            contentDetails.setDescription(descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[1]);
            contentDetails.setDescription(descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[2]);
            contentDetails.setDescription(descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating files with given content and description.
            contentDetails.setName(filesWithContentAndDescription[0]);
            contentDetails.setContent(descriptions[0]);
            contentDetails.setDescription(descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndDescription[1]);
            contentDetails.setContent(descriptions[0]);
            contentDetails.setDescription(descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndDescription[2]);
            contentDetails.setContent(descriptions[1]);
            contentDetails.setDescription(descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);            

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[1], descriptions[1]);
            ShareUserSitePage.createFolder(drone, folders[2], descriptions[0]);
            
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
     * Test - ALF-4990: Description Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the description, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4990()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Description string
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            SearchResultItem searchResultItem = ShareUserSearchPage.findInSearchResults(drone,"House 1");
            
            Assert.assertNotNull(searchResultItem);
            Assert.assertTrue(searchResultItem.isFolder());

            searchResultItem = ShareUserSearchPage.findInSearchResults(drone,"Techno");
            
            Assert.assertNotNull(searchResultItem);
            Assert.assertTrue(searchResultItem.isFolder());
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
     * DataPreparation method - ALF-4974
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create and upload file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4974() throws Exception
    {
        String testName = getTestName();

        String siteName = getSiteName(testName);

        String mainUser = getUserNameFreeDomain(testName);
        String testUser = getUserNameFreeDomain(testName + "1");
        String[] mainUserInfo = new String[] { mainUser };
        String[] testUserInfo = new String[] { testUser };

        // Adding siteName in folders/files/filecontents to get the exact test case specific results.
        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] files = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, mainUser, testPassword);

            // Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PRIVATE).render(maxWaitTime);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], null, null);
            ShareUserSitePage.createFolder(drone, folders[1], null, null);
            ShareUserSitePage.createFolder(drone, folders[2], null, null);
            ShareUserSitePage.createFolder(drone, folders[3], null, null);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(files[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[3]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating files with given Title and description.
            contentDetails.setName(filesWithContent[0]);
            contentDetails.setContent(folders[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[1]);
            contentDetails.setContent(folders[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[2]);
            contentDetails.setContent(folders[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[3]);
            contentDetails.setContent(folders[3]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
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
     * Test - ALF-4974: Searching for item in private site (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4974()
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");
        String siteName = getSiteName(testName);

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid keyword.
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), siteName + "_house");
            List<SearchResultItem> list = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(list.size() == 0);
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
     * DataPreparation method - ALF-4975
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create and upload file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4975() throws Exception
    {
        String testName = getTestName();

        String siteName = getSiteName(testName);

        String mainUser = getUserNameFreeDomain(testName);
        String testUser = getUserNameFreeDomain(testName + "1");
        String[] mainUserInfo = new String[] { mainUser };
        String[] testUserInfo = new String[] { testUser };

        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] files = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, mainUser, testPassword);

            // Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_MODERATED).render(maxWaitTime);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], null, null);
            ShareUserSitePage.createFolder(drone, folders[1], null, null);
            ShareUserSitePage.createFolder(drone, folders[2], null, null);
            ShareUserSitePage.createFolder(drone, folders[3], null, null);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(files[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[3]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating files with given Title and description.
            contentDetails.setName(filesWithContent[0]);
            contentDetails.setContent(folders[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[1]);
            contentDetails.setContent(folders[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[2]);
            contentDetails.setContent(folders[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[3]);
            contentDetails.setContent(folders[3]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
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
     * Test - ALF-4975: Searching for item in moderated site (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4975()
    {
        /** Start Test */
        testName = AbstractTests.getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");
        String siteName = getSiteName(testName);

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Keyword.
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), siteName + "_house");
            List<SearchResultItem> list = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(list.size() == 0);
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
     * DataPreparation method - ALF-4976
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Invite the user on site</li>
     * <li>Create and upload file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    
     /* Note : This requires and Site Manager approval process, once it is automated this test will be uncommented.
      * 
      */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4976() throws Exception
   {
       String testName = getTestName();
       String siteName = getSiteName(testName);
       String mainUser = getUserNameFreeDomain(testName);
       String[] mainUserInfo = new String[] { mainUser };
       String testUser = getUserNameFreeDomain(testName + "1");
       String[] testUserInfo = new String[] { testUser };
       String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
       String[] files = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
       String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };
       try
       {
           // User
           CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
           CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

           // Login
           ShareUser.login(drone, mainUser, testPassword);

           // Site
           ShareUser.createSite(drone, siteName, ShareUser.SITE_VISIBILITY_MODERATED);

           ShareUser.openDocumentLibrary(drone);
           ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);

           // Creating files with given Title.
           ContentDetails contentDetails = new ContentDetails();

           contentDetails.setName(files[0]);
           ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

           contentDetails.setName(files[1]);
           ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

           contentDetails.setName(files[2]);
           ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

           contentDetails.setName(files[3]);
           ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

           // Creating files with given Title and description.
           contentDetails.setName(filesWithContent[0]);
           contentDetails.setContent(folders[0]);
           ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

           contentDetails.setName(filesWithContent[1]);
           contentDetails.setContent(folders[1]);
           ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

           contentDetails.setName(filesWithContent[2]);
           contentDetails.setContent(folders[2]);
           ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

           contentDetails.setName(filesWithContent[3]);
           contentDetails.setContent(folders[3]);
           ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
                      
           // Creating folders
           ShareUserSitePage.createFolder(drone, folders[0], null, null);
           ShareUserSitePage.createFolder(drone, folders[1], null, null);
           ShareUserSitePage.createFolder(drone, folders[2], null, null);
           ShareUserSitePage.createFolder(drone, folders[3], null, null);

           ShareUserMembers.inviteUserToSiteWithRole(drone, mainUser, testUser, siteName, UserRole.COLLABORATOR);

       }
       catch (Throwable e)
       {
           reportError(drone, testName, e);
       }
   }
     

    /**
     * Test - ALF-4976: Searching for item in moderated site where user is invited (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4976()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");
        String siteName = getSiteName(testName);

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");
        String searchTerm = siteName + "_house";

        try
        {
            ShareUser.login(drone, testUser, testPassword);
            // Searching for valid Keyword.
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House my", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House Techno my", true));
            Assert.assertTrue(ShareUserSearchPage
                    .checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House Techno Trance my", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 1", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 3", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 4", true));
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
     * DataPreparation method - ALF-4977
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Invite the user on site</li>
     * <li>Create and upload file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4977() throws Exception
    {
        String testName = getTestName();

        String siteName = getSiteName(testName);

        String mainUser = getUserNameFreeDomain(testName);
        String[] mainUserInfo = new String[] { mainUser };
        String testUser = getUserNameFreeDomain(testName + "1");
        String[] testUserInfo = new String[] { testUser };

        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] files = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, mainUser, testPassword);

            // Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], null, null);
            ShareUserSitePage.createFolder(drone, folders[1], null, null);
            ShareUserSitePage.createFolder(drone, folders[2], null, null);
            ShareUserSitePage.createFolder(drone, folders[3], null, null);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(files[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[3]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating files with given Title and description.
            contentDetails.setName(filesWithContent[0]);
            contentDetails.setContent(folders[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[1]);
            contentDetails.setContent(folders[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[2]);
            contentDetails.setContent(folders[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContent[3]);
            contentDetails.setContent(folders[3]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
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
     * Test - ALF-4977: Searching for item in moderated site where user is not invited (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = "Enterprise4.2Bug")
    public void ALF_4977()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");
        String siteName = getSiteName(testName);

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");
        String searchTerm = siteName + "_house";

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Keyword.
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House my", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House Techno my", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House Techno Trance my", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 1", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 3", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 4", true));
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
     * DataPreparation method - ALF-10769
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create and upload file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_10769() throws Exception
    {
        String testName = getTestName();

        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, testPassword);

            // Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName("test 1");
            contentDetails.setContent("test");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
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
     * Test - ALF-10769: Modified date search (zeros handling)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Enter modified from date</li>
     * <li>Validate the date</li>
     * </ul>
     */
    @Test
    public void ALF_10769()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            AdvanceSearchPage contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Enter the valid modified from date
            contentSearchPage.inputFromDate(dateFormat.format(todayDate));
            String valueEntered = contentSearchPage.getFromDate();

            // Enter the invalid modified from date with day as 0
            valueEntered = valueEntered.replaceFirst(valueEntered.split("/")[0], "0");
            contentSearchPage.inputFromDate(valueEntered);

            Assert.assertFalse(contentSearchPage.isValidFromDate());

            // Enter the valid modified from date with day as 06
            valueEntered = valueEntered.replaceFirst(valueEntered.split("/")[0], "06");
            contentSearchPage.inputFromDate(valueEntered);

            Assert.assertTrue(contentSearchPage.isValidFromDate());

            String actualValueEntered = contentSearchPage.getFromDate();

            Assert.assertEquals(actualValueEntered, valueEntered.replaceFirst(valueEntered.split("/")[0], "6"));
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
     * Test - ALF-10819:Verify results for advanced searches with quotes
     * <ul>
     * <li>Login</li>
     * <li>Crate and upload two files</li>
     * <li>Search with quoted string</li>
     * <li>Validate the results</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups="Enterprise4.2Bug")
    public void ALF_10819() throws Exception
    {
        /** Start Test */
        testName = getTestName();
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            // Login
            ShareUser.login(drone, testUser, testPassword);

            // Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            // Creating documents with quoted
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName("string M.txt");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName("string alt M.txt");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Searching for valid Name.
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), "string M");
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, "string M", "string M.txt", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, "string M", "string alt M.txt", true),
                    "MNT-8476 - Search returning inconsistent results.");

            // Searching for valid Name.
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), "string M");
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, "\"string M\"", "string M.txt", true),
                    "MNT-8476 - Search returning inconsistent results.");
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
}