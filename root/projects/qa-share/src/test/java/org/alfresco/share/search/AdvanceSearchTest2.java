package org.alfresco.share.search;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.search.SearchResult;
import org.alfresco.po.share.search.SiteResultsPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.*;

@Listeners(FailedTestListener.class)
@SuppressWarnings("unused")
public class AdvanceSearchTest2 extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AdvanceSearchTest2.class);

    Date todayDate = new Date();

    private static String testPassword = DEFAULT_PASSWORD;

    protected String testUser;

    protected String siteName = "";

    protected void advancedSearch(WebDrone drone, List<String> searchInfo, Map<String, String> keyWordSearchText, String entryToBeFound) throws Exception
    {

        ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        boolean searchOk = ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, entryToBeFound);

        if (!searchOk)
        {
            drone.refresh();
            drone.getCurrentPage().render();
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        }
    }

    protected void advancedSearchWithRetry(WebDrone drone, List<String> searchInfo, Map<String, String> keyWordSearchText, String searchType, String searchTerm, String entryToBeFound, Boolean isEntryVisible)
            throws Exception
    {

        ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        boolean searchOk = ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, searchType, searchTerm, entryToBeFound, isEntryVisible);

        if (!searchOk)
        {
            drone.refresh();
            drone.getCurrentPage().render();
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        }
    }

    /**
     * Class includes: Tests from TestLink in Area: Advanced Search Tests
     * <ul>
     * <li>Test searches using various Properties, content, Folder</li>
     * </ul>
     */
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
     * DataPreparation method - AONE-13906
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create a content with a name }{+_)(&^%$#@! in the document library
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13906() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = "}{+_)(&^%$#@!";

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        String[] fileInfo = { fileName };
        ShareUser.uploadFileInFolder(drone, fileInfo);
    }

    /**
     * Test - AONE-13906:Wildcard search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Content form</li>
     * <li>In the keyword Search text of the content search form enter !@#$%^&*()_+:"|<>?;</li>
     * <li>Validate the search results are returned as zero</li>
     * <li>Go Back to Advance Search from</li>
     * <li>In the Keyword search field of the content search form enter }{+_)(&^%$#@!</li>
     * <li>Validate the search results are returned as zero</li>
     * </ul>
     * 
     * @throws Exception
     * @throws PageException
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13906() throws Exception
    {
        /** Start Test */
        String testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText_1 = "!@#$%^&*()_+:\"|<>?;";
        String searchText_2 = "}{+_)(&^%$#@!";
        Map<String, String> keyWordSearchText = new HashMap<>();

        // Initialise search data
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        boolean searchOk;

        // Login
        ShareUser.login(drone, testUser, testPassword);

        // Doing the first invalid Search
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText_1);
        advancedSearch(drone, searchInfo, keyWordSearchText, SERACH_ZERO_CONTENT);

        searchOk = ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, SERACH_ZERO_CONTENT);
        Assert.assertTrue(searchOk);

        // Searching for valid string with content
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText_2);
        advancedSearch(drone, searchInfo, keyWordSearchText, SERACH_ZERO_CONTENT);

        searchOk = ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, SERACH_ZERO_CONTENT);
        Assert.assertTrue(searchOk);

    }

    /**
     * DataPreparation method - AONE-13907
     * <ul>
     * <li>Create User</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13907() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

    }

    /**
     * Test - AONE-13906:Empty search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>Click on Search button without any Folder Search content</li>
     * <li>Validate the search results are returned as zero</li>
     * </ul>
     */

    @Test(groups = { "AlfrescoOne" })
    public void AONE_13907() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);

        boolean searchOk;
        Map<String, String> keyWordSearchText = new HashMap<>();

        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        advancedSearch(drone, searchInfo, keyWordSearchText, SERACH_ZERO_CONTENT);

        searchOk = ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, SERACH_ZERO_CONTENT);

        Assert.assertTrue(searchOk);

    }

    /**
     * DataPreparation method - AONE-13908
     * <ul>
     * <li>Create User</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13908() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

    }

    /**
     * Test - AONE-13908:Empty search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Content form</li>
     * <li>Click on Search button without any Content Search content</li>
     * <li>Validate the search results are returned as zero</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13908() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);

        boolean searchOk;
        Map<String, String> keyWordSearchText = new HashMap<>();

        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);
        advancedSearch(drone, searchInfo, keyWordSearchText, SERACH_ZERO_CONTENT);

        searchOk = ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, SERACH_ZERO_CONTENT);
        Assert.assertTrue(searchOk);

    }

    /**
     * DataPreparation method - AONE-13909
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create a Folder with a name }{+_)(&^%$#@! in the document library
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13909() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);
        String folderName = "}{+_)(&^%$#@!";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUserSitePage.createFolder(drone, folderName, folderName);

    }

    /**
     * Test - AONE-13909:Wildcard search (Folder type)
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
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13909() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */

        String testUser = getUserNameFreeDomain(testName);

        String searchText_1 = "!@#$%^&*()_+:\"|<>?;";
        String searchText_2 = "}{+_)(&^%$#@!";

        Map<String, String> keyWordSearchText = new HashMap<>();
        boolean searchOk;

        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText_1);

        ShareUser.login(drone, testUser, testPassword);

        // Doing the first invalid Search
        advancedSearch(drone, searchInfo, keyWordSearchText, SERACH_ZERO_CONTENT);

        searchOk = ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, SERACH_ZERO_CONTENT);
        Assert.assertTrue(searchOk);

        // Searching for valid string with content
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText_2);
        advancedSearch(drone, searchInfo, keyWordSearchText, SERACH_ZERO_CONTENT);

        searchOk = ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, SERACH_ZERO_CONTENT);
        Assert.assertTrue(searchOk);

    }

    /**
     * DataPreparation method - AONE-13910
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13910() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

    }

    /**
     * Test - AONE-13910:Too Long Data Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the keyword / Name / Tiltle / Desc Search text of the Content search form enter more than 1024 symbols</li>
     * <li>Verify the symbols entered successfully and entered data is cut to 1024 symbols</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13910() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */

        String testUser = getUserNameFreeDomain(testName);
        String searchText = ShareUser.getRandomStringWithNumders(1030);
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);

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

    /**
     * DataPreparation method - AONE-13911
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13911() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName).replace("-", "");

        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] filesWithNoContent = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };
        String[] fileContents = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] fileInfo = new String[1];

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

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

    /**
     * Test - AONE-13911: Keyword Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the keyword enter "house","*echno","tran*"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * <li>Go Back to Advance Search from</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13911() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String siteName = getSiteName(testName).replace("-", "");
        /** Test Data Setup */

        String testUser = getUserNameFreeDomain(testName);

        String[] searchText = { siteName + "_house", "*echno", "tran*" };

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid keyword string
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[0]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText[0], siteName + "_My 4", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[0], siteName + "_My 4", true));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_House my"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_House Techno my"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_House Techno Trance my"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_My 1"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_My 3"));

        // Searching for valid keyword string
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[1]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText[1], siteName + "_My 4", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[1], siteName + "_My 4", true));

        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_Techno my"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_House Techno my"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_House Techno Trance my"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_My 2"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_My 3"));

        // Searching for valid keyword string
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[2]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText[2], siteName + "_My 4", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[2], siteName + "_My 4", true));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_House Techno Trance my"));

    }

    /**
     * DataPreparation method - AONE-13912
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13912() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName).replace("-", "");

        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] filesWithNoContent = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };
        String[] fileContents = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] fileInfo = new String[1];

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

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

    /**
     * Test - AONE-13912: Keyword Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the keyword enter "house","*echno","tran*"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13912() throws Exception
    {
        /** Start Test */
        testName = getTestName();
        String siteName = getSiteName(testName).replace("-", "");
        /** Test Data Setup */

        String testUser = getUserNameFreeDomain(testName);
        String[] searchText = { siteName + "_house", "*echno", "tran*" };

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid keyword string
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[0]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText[0], siteName + "_House", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[0], siteName + "_House", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[0], siteName + "_House Techno", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[0], siteName + "_House Techno Trance",
                true));

        // Searching for valid keyword string
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[1]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText[1], siteName + "_Techno", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[1], siteName + "_Techno", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[1], siteName + "_House Techno", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[1], siteName + "_House Techno Trance",
                true));

        // Searching for valid keyword string
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchText[2]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText[2], siteName + "_House Techno Trance", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[2], siteName + "_House Techno Trance",
                true));

    }

    /**
     * DataPreparation method - AONE-13913
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13913() throws Exception
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

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

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

    /**
     * Test - AONE-13913: Name Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the name, enter "house","*echno","tran*"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13913() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String[] searchText = { "house", "*echno", "tran*" };

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid Name string
        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText[0], "House Techno Trance my", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[0], "House Techno Trance my", true));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, "House my"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, "House Techno my"));

        // Searching for valid Name string
        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[1]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText[1], "House Techno Trance my", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[1], "House Techno Trance my", true));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, "Techno my"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, "House Techno my"));

        // Searching for valid Name string
        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[2]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText[2], "House Techno Trance my", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[2], "House Techno Trance my", true));

    }

    /**
     * DataPreparation method - AONE-13914
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13914() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName).replace("-", "");

        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] filesWithNoContent = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };
        String[] fileContents = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] fileInfo = new String[1];

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

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

    /**
     * Test - AONE-13914: Name Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the name, enter "house","*echno","tran*"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13914() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String siteName = getSiteName(testName).replace("-", "");
        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String[] searchText = { siteName + "_house", "*echno", "tran*" };

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid Name string
        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText[0], siteName + "_House Techno Trance", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[0], siteName + "_House Techno Trance",
                true));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_House"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_House Techno"));

        // Searching for valid Name string
        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[1]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText[1], siteName + "_House Techno Trance", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[1], siteName + "_House Techno Trance",
                true));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_Techno"));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, siteName + "_House Techno"));

        // Searching for valid Name string
        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[2]);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText[2], siteName + "_House Techno Trance", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[2], siteName + "_House Techno Trance",
                true));

    }

    /**
     * DataPreparation method - AONE-13915
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13915() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);
        String[] folders = { "House 1", "House 2", "Techno" };
        String[] folderTitles = { "House", "Techno" };
        String[] filesWithTitle = { "House my 1", "House my 2", "Techno my" };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

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

    /**
     * Test - AONE-13915: Title Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the title, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13915() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid Title string
        keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText, "House my 1", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "House my 1", true));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, "Techno my"));

    }

    /**
     * DataPreparation method - AONE-13916
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13916() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "Techno", "House Techno Trance" };
        String[] folderTitles = { "House", "Techno" };
        String[] filesWithTitle = { "House my 1", "House my 2", "Techno my" };
        String[] filesWithContentAndTitle = { "My 1", "My 2", "My 3" };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

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

    /**
     * Test - AONE-13916: Title Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the title, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13916() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid Title string
        keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
        advancedSearch(drone, searchInfo, keyWordSearchText, "House 1");

        SearchResult searchResultItem = ShareUserSearchPage.findInFacetSearchResults(drone, "House 1");

        Assert.assertNotNull(searchResultItem);
        Assert.assertTrue(searchResultItem.isFolder());

        searchResultItem = ShareUserSearchPage.findInFacetSearchResults(drone, "Techno");

        Assert.assertNotNull(searchResultItem);
        Assert.assertTrue(searchResultItem.isFolder());

    }

    /**
     * DataPreparation method - AONE-13917
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13917() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "Techno" };
        String[] descriptions = { "House", "Techno" };
        String[] files = { "House my 1", "House my 2", "Techno my" };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

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

    /**
     * Test - AONE-13917: Description Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the description, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13917() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid Description string
        keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText, "Techno my", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "Techno my", true));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, "House my 1"));

    }

    /**
     * DataPreparation method - AONE-13918
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload folders,files,file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13918() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "Techno" };
        String[] descriptions = { "House", "Techno" };
        String[] files = { "House my 1", "House my 2", "Techno my" };
        String[] filesWithContentAndDescription = { "My 1", "My 2", "My 3" };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, testPassword);

        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

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

    /**
     * Test - AONE-13918: Description Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the description, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13918() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid Description string
        keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
        advancedSearch(drone, searchInfo, keyWordSearchText, "House 1");

        SearchResult searchResult = ShareUserSearchPage.findInFacetSearchResults(drone, "House 1");
        Assert.assertNotNull(searchResult);
        Assert.assertTrue(searchResult.isFolder());

        searchResult = ShareUserSearchPage.findInFacetSearchResults(drone, "Techno");

        Assert.assertNotNull(searchResult);
        Assert.assertTrue(searchResult.isFolder());

    }

    /**
     * DataPreparation method - AONE-13901
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create and upload file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13901() throws Exception
    {
        String testName = getTestName();

        String siteName = getSiteName(testName).replace("-", "");

        String mainUser = getUserNameFreeDomain(testName);
        String testUser = getUserNameFreeDomain(testName + "1");
        String[] mainUserInfo = new String[] { mainUser };
        String[] testUserInfo = new String[] { testUser };

        // Adding siteName in folders/files/filecontents to get the exact test case specific results.
        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] files = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, mainUser, testPassword);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PRIVATE).render(maxWaitTime);

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

    /**
     * Test - AONE-13901: Searching for item in private site (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13901() throws Exception
    {
        /** Start Test */
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");
        String siteName = getSiteName(testName).replace("-", "");

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid keyword.
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), siteName + "_house");
        List<SearchResult> list = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        if(list.size() != 0)
        {
            drone.deleteCookies();
            drone.refresh();
            drone.getCurrentPage().render();
            ShareUser.login(drone, testUser, testPassword);
            list = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);
        }

        Assert.assertTrue(list.size() == 0);

    }

    /**
     * DataPreparation method - AONE-13902
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create and upload file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13902() throws Exception
    {
        String testName = getTestName();

        String siteName = getSiteName(testName).replace("-", "");

        String mainUser = getUserNameFreeDomain(testName);
        String testUser = getUserNameFreeDomain(testName + "1");
        String[] mainUserInfo = new String[] { mainUser };
        String[] testUserInfo = new String[] { testUser };

        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] files = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, mainUser, testPassword);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_MODERATED).render(maxWaitTime);

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

    /**
     * Test - AONE-13902: Searching for item in moderated site (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13902() throws Exception
    {
        /** Start Test */
        testName = AbstractUtils.getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");
        String siteName = getSiteName(testName).replace("-", "");

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid Keyword.
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), siteName + "_house");
        List<SearchResult> list = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        if(list.size() != 0)
        {
            drone.deleteCookies();
            drone.refresh();
            drone.getCurrentPage().render();
            ShareUser.login(drone, testUser, testPassword);
            list = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);
        }

        Assert.assertTrue(list.size() == 0);

    }

    /**
     * DataPreparation method - AONE-13903
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

    /*
     * Note : This requires and Site Manager approval process, once it is automated this test will be uncommented.
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13903() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).replace("-", "");
        String mainUser = getUserNameFreeDomain(testName);
        String[] mainUserInfo = new String[] { mainUser };
        String testUser = getUserNameFreeDomain(testName + "1");
        String[] testUserInfo = new String[] { testUser };
        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] files = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };

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

    /**
     * Test - AONE-13903: Searching for item in moderated site where user is invited (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */

    @Test(groups = { "AlfrescoOne" })
    public void AONE_13903() throws Exception
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");
        String siteName = getSiteName(testName).replace("-", "");

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");
        String searchTerm = siteName + "_house";

        ShareUser.login(drone, testUser, testPassword);
        // Searching for valid Keyword.
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House my", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House my", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House Techno my", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House Techno Trance my",
                true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 1", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 3", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 4", true));

    }

    /**
     * DataPreparation method - AONE-13904
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
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13904() throws Exception
    {
        String testName = getTestName();

        String siteName = getSiteName(testName).replace("-", "");

        String mainUser = getUserNameFreeDomain(testName);
        String[] mainUserInfo = new String[] { mainUser };
        String testUser = getUserNameFreeDomain(testName + "1");
        String[] testUserInfo = new String[] { testUser };

        String[] folders = { siteName + "_House", siteName + "_Techno", siteName + "_House Techno", siteName + "_House Techno Trance" };
        String[] files = { siteName + "_House my", siteName + "_Techno my", siteName + "_House Techno my", siteName + "_House Techno Trance my" };
        String[] filesWithContent = { siteName + "_My 1", siteName + "_My 2", siteName + "_My 3", siteName + "_My 4" };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, mainUser, testPassword);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

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

    /**
     * Test - AONE-13904: Searching for item in moderated site where user is not invited (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = "alfrescoBug")
    public void AONE_13904() throws Exception
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");
        String siteName = getSiteName(testName).replace("-", "");

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");
        String searchTerm = siteName + "_house";

        ShareUser.login(drone, testUser, testPassword);

        // Searching for valid Keyword.
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House my", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House my", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House Techno my", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_House Techno Trance my",
                true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 1", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 3", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, siteName + "_My 4", true));
    }

    /**
     * DataPreparation method - AONE-13905
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create and upload file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13905() throws Exception
    {
        String testName = getTestName();

        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, testPassword);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("test 1");
        contentDetails.setContent("test");
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

    }

    /**
     * Test - AONE-13905: Modified date search (zeros handling)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Enter modified from date</li>
     * <li>Validate the date</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13905() throws Exception
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

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

    /**
     * Test - AONE-13933:Verify results for advanced searches with quotes
     * <ul>
     * <li>Login</li>
     * <li>Crate and upload two files</li>
     * <li>Search with quoted string</li>
     * <li>Validate the results</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13933() throws Exception
    {
        /** Start Test */
        testName = getTestName();
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        // Login
        ShareUser.login(drone, testUser, testPassword);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        // Creating documents with quoted
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("string M.txt");
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails.setName("string alt M.txt");
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Searching for valid Name.
        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), "string M");
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, "string M", "string M.txt", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, "string M", "string M.txt", true));
        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, "string M", "string alt M.txt", true),
                "MNT-8476 - Search returning inconsistent results.");

        // Searching for valid Name.
        keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), "string M");
        advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, "\"string M\"", "string M.txt", true);

        Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, "\"string M\"", "string M.txt", true),
                "MNT-8476 - Search returning inconsistent results.");

    }

    /**
     * DataPreparation method - AONE-13900
     * <ul>
     * <li>Create User</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13900() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

    }

    /**
     * Test - AONE-13900: Verify Advanced search page and link
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13900() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        ShareUser.login(drone, testUserInfo);

        // Click Search Options menu for Search field - Menu is expanded, Advanced search link is displayed;
        SharePage sharePage = ShareUser.getSharePage(drone);
        Assert.assertTrue(sharePage.getNav().isAdvSearchLinkPresent(), "Advanced search link isn't available");

        // Click Advanced search link
        AdvanceSearchPage advanceSearchPage = sharePage.getNav().selectAdvanceSearch().render();
        Assert.assertTrue(advanceSearchPage != null);

        // Verify items are displayed
        Assert.assertTrue(advanceSearchPage.isAdvSearchPageCorrectlyDisplayed(), "Some elements are missing");
    }

    /**
     * DataPreparation method - AONE-13932: Back to.. link
     * <ul>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create Content</li>
     * </ul>
     * 
     * @throws Exception
     */
    // @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13932() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.login(drone, testUserInfo);

        // Any site "Test" is created in Alfresco Share;
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Any content item is created
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(testName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
    }

    // todo not relevant for 5.0
    // @Test(groups = { "AlfrescoOne" })
    public void AONE_13932() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);

        ShareUser.login(drone, testUser);
        ShareUser.openSiteDashboard(drone, siteName);

        SharePage sharePage = ShareUser.getSharePage(drone);
        AdvanceSearchPage advanceSearchPage;
        Map<String, String> keyWordSearchText = new HashMap<>();

        // Initialise search data
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), testName);
        ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        // Click Go to Advanced Search link
        //
        SiteResultsPage siteResultsPage = new SiteResultsPage(drone);
        siteResultsPage.goBackToAdvanceSearch().render();

        Assert.assertTrue(ShareUser.getSharePage(drone) instanceof AdvanceSearchPage, "The user isn't redirected to Advanced Search page");

        // Click "Back to Results" link;
        advanceSearchPage = new AdvanceSearchPage(drone).render();
        // todo ACE-2877
        advanceSearchPage.clickBackToResults().render();
        Assert.assertTrue(ShareUser.getSharePage(drone) instanceof SiteResultsPage, "The user isn't redirected to Advanced Search page");

        // Click site name for 'testing" item;
        siteResultsPage.getResults().get(0).clickSiteName();

        Assert.assertTrue(ShareUser.getSharePage(drone) instanceof SiteDashboardPage, "The user isn't redirected to SiteDashboard page");

        // Click "Advanced search" link in "search options" drop-down list;
        advanceSearchPage = sharePage.getNav().selectAdvanceSearch().render();

        // Click "Back to Test Site" link;
        advanceSearchPage.clickBackToSite();
        Assert.assertTrue(ShareUser.getSharePage(drone) instanceof SiteDashboardPage, "The user isn't redirected to SiteDashboard page");
    }
}