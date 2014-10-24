package org.alfresco.share.search;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.admin.ActionsSet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.search.*;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

import static org.testng.Assert.assertTrue;

@Listeners(FailedTestListener.class)
@SuppressWarnings("unused")
public class AdvanceSearchTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AdvanceSearchTest.class);

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
    private static final String TEST_FILE_WITH_1KB_SIZE = "AONE13942-1KB-FILE.txt";
    private static final String TEST_FILE_WITH_1MB_SIZE = "AONE13942-1MB-FILE.txt";
    private static final String TEST_FILE_WITH_2MB_SIZE = "AONE13942-2MB-FILE.txt";

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

    protected void login(WebDrone drone, String username, String password)
    {
        ShareUser.login(drone, username, password);

        SharePage sharePage = drone.getCurrentPage().render();
        if (!sharePage.isLoggedIn())
        {
            drone.deleteCookies();
            drone.refresh();
            drone.getCurrentPage().render();
            ShareUser.login(drone, username, password);
        }
    }

    /**
     * DataPreparation method - AONE-13923
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
    public void dataPrep_AdvSearch_AONE_13923() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "House 3" };
        String[] folderTitles = { "House", "Techno", "Trance" };
        String[] filesWithTitle = { "House my 1", "House my 2", "House my 3" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], folderTitles[0], null);
            ShareUserSitePage.createFolder(drone, folders[1], folderTitles[1], null);
            ShareUserSitePage.createFolder(drone, folders[2], folderTitles[2], null);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(filesWithTitle[0]);
            contentDetails.setTitle(folderTitles[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[1]);
            contentDetails.setTitle(folderTitles[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[2]);
            contentDetails.setTitle(folderTitles[2]);
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
     * Test - AONE-13923: Name & Title Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>Search with name and title</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13923()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText[] = { "house", "techno" };

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            login(drone, testUser, testPassword);

            // Searching for valid Name string with Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText[0]);
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText[0], "House my 1", true);

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[0], "House my 1", true));

            // Searching for valid Name string with Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText[1]);
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText[1], "House my 2", true);

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[1], "House my 2", true));
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
     * DataPreparation method - AONE-13919
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
    public void dataPrep_AdvSearch_AONE_13919() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "House 3" };
        String[] folderTitles = { "House", "Techno", "Trance" };
        String[] filesWithTitle = { "House my 1", "House my 2", "House my 3" };
        String[] filesWithContentAndTitle = { "My 1", "My 2", "My 3" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(filesWithTitle[0]);
            contentDetails.setTitle(folderTitles[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[1]);
            contentDetails.setTitle(folderTitles[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[2]);
            contentDetails.setTitle(folderTitles[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating files with given Title and description.
            contentDetails.setName(filesWithContentAndTitle[0]);
            contentDetails.setTitle(folderTitles[0]);
            contentDetails.setContent(folderTitles[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndTitle[1]);
            contentDetails.setTitle(folderTitles[1]);
            contentDetails.setContent(folderTitles[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndTitle[2]);
            contentDetails.setTitle(folderTitles[2]);
            contentDetails.setContent(folderTitles[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], folderTitles[0], null);
            ShareUserSitePage.createFolder(drone, folders[1], folderTitles[1], null);
            ShareUserSitePage.createFolder(drone, folders[2], folderTitles[2], null);
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
     * Test - AONE-13919: Name Title Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the title and Name, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13919()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText[] = { "house", "techno" };

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            login(drone, testUser, testPassword);

            // Searching for valid Name string with Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText[0]);
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText[0], "House 1", true);

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[0], "House 1", true));

            // Searching for valid Name string with Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText[1]);
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText[1], "House 2", true);

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[1], "House 2", true));

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
     * DataPreparation method - AONE-13920
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
    public void dataPrep_AdvSearch_AONE_13920() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "House 3" };
        String[] folderTitles = { "House", "Techno", "Trance" };
        String[] filesWithTitle = { "House my 1", "House my 2", "House my 3" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], folderTitles[0]);
            ShareUserSitePage.createFolder(drone, folders[1], folderTitles[1]);
            ShareUserSitePage.createFolder(drone, folders[2], folderTitles[2]);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(filesWithTitle[0]);
            contentDetails.setDescription(folderTitles[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[1]);
            contentDetails.setDescription(folderTitles[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[2]);
            contentDetails.setDescription(folderTitles[2]);
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
     * Test - AONE-13920: Name & Description Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>Search with name and description</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13920()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            login(drone, testUser, testPassword);

            // Searching for valid Name string with Description
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText, "House my 1", true);

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "House my 1", true));

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
     * DataPreparation method - AONE-13921
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
    public void dataPrep_AdvSearch_AONE_13921() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "House 3" };
        String[] descriptions = { "House", "Techno", "Trance" };
        String[] files = { "House my 1", "House my 2", "House my 3" };
        String[] filesWithContentAndDescription = { "My 1", "My 2", "My 3" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(files[0]);
            contentDetails.setDescription(descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[1]);
            contentDetails.setDescription(descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[2]);
            contentDetails.setDescription(descriptions[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating files with given Title and description.
            contentDetails.setName(filesWithContentAndDescription[0]);
            contentDetails.setDescription(descriptions[0]);
            contentDetails.setContent(descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndDescription[1]);
            contentDetails.setDescription(descriptions[1]);
            contentDetails.setContent(descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndDescription[2]);
            contentDetails.setDescription(descriptions[2]);
            contentDetails.setContent(descriptions[2]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[1], descriptions[1]);
            ShareUserSitePage.createFolder(drone, folders[2], descriptions[2]);
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
     * Test - AONE-13921: Name & Description Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the Description and Name, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13921()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            login(drone, testUser, testPassword);

            // Searching for valid Name string with Description
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText, "House 1", true);

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText, "House 1", true));
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
     * DataPreparation method - AONE-13922
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
    public void dataPrep_AdvSearch_AONE_13922() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "House 3" };
        String[] folderTitles_Descriptions = { "House", "Techno" };
        String[] filesWithTitle = { "House my 1", "House my 2", "House my 3", "Techno my" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(filesWithTitle[0]);
            contentDetails.setTitle(folderTitles_Descriptions[0]);
            contentDetails.setDescription(folderTitles_Descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[1]);
            contentDetails.setTitle(folderTitles_Descriptions[1]);
            contentDetails.setDescription(folderTitles_Descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[2]);
            contentDetails.setTitle(folderTitles_Descriptions[0]);
            contentDetails.setDescription(folderTitles_Descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[3]);
            contentDetails.setTitle(folderTitles_Descriptions[0]);
            contentDetails.setDescription(folderTitles_Descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], folderTitles_Descriptions[0], folderTitles_Descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[1], folderTitles_Descriptions[1], folderTitles_Descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[2], folderTitles_Descriptions[0], folderTitles_Descriptions[1]);

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
     * Test - AONE-13922: Name & Title & Description Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>Search with name,title and description</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13922()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            login(drone, testUser, testPassword);

            // Searching for valid Name string with Description and Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText, "House my 1", true);
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "House my 1", true));
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
     * DataPreparation method - AONE-13924
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
    public void dataPrep_AdvSearch_AONE_13924() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "House 3", "Techno" };
        String[] titles_descriptions = { "House", "Techno" };
        String[] files = { "House my 1", "House my 2", "House my 3", "Techno my" };
        String[] filesWithContentAndDescription = { "My 1", "My 2", "My 3", "My 4" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.openDocumentLibrary(drone);

            ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(files[0]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[1]);
            contentDetails.setTitle(titles_descriptions[1]);
            contentDetails.setDescription(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[2]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[3]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating files with given Title and description.
            contentDetails.setName(filesWithContentAndDescription[0]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[0]);
            contentDetails.setContent(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndDescription[1]);
            contentDetails.setTitle(titles_descriptions[1]);
            contentDetails.setDescription(titles_descriptions[0]);
            contentDetails.setContent(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndDescription[2]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[1]);
            contentDetails.setContent(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndDescription[3]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[0]);
            contentDetails.setContent(titles_descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], titles_descriptions[0], titles_descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[1], titles_descriptions[1], titles_descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[2], titles_descriptions[0], titles_descriptions[1]);
            ShareUserSitePage.createFolder(drone, folders[3], titles_descriptions[0], titles_descriptions[0]);
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
     * Test - AONE-13924: Name & Title & Description Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the Description,Name and Tiltle, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13924()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            login(drone, testUser, testPassword);

            // Searching for valid Name string with Description and Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText, "House 1", true);

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText, "House 1", true));
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
     * DataPreparation method - AONE-13925
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
    public void dataPrep_AdvSearch_AONE_13925() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "House 3", "Techno" };
        String[] folderTitles_Descriptions = { "House", "Techno" };
        String[] filesWithTitle = { "House my 1", "House my 2", "House my 3", "Techno my" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.openDocumentLibrary(drone);

            // Selecting Simple view to avoid issues due to content actions not being visible, if more items in doclib
            ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);

            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(filesWithTitle[0]);
            contentDetails.setTitle(folderTitles_Descriptions[0]);
            contentDetails.setDescription(folderTitles_Descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[1]);
            contentDetails.setTitle(folderTitles_Descriptions[1]);
            contentDetails.setDescription(folderTitles_Descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[2]);
            contentDetails.setTitle(folderTitles_Descriptions[0]);
            contentDetails.setDescription(folderTitles_Descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithTitle[3]);
            contentDetails.setTitle(folderTitles_Descriptions[0]);
            contentDetails.setDescription(folderTitles_Descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], folderTitles_Descriptions[0], folderTitles_Descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[1], folderTitles_Descriptions[1], folderTitles_Descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[2], folderTitles_Descriptions[0], folderTitles_Descriptions[1]);
            ShareUserSitePage.createFolder(drone, folders[3], folderTitles_Descriptions[0], folderTitles_Descriptions[0]);
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
     * Test - AONE-13925: Title & Description Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>Search with Title and description</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13925()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            login(drone, testUser, testPassword);

            // Searching for valid Title string with Description
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchText, "House my 1", true);

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "House my 1", true));
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "Techno my", true));
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
     * DataPreparation method - AONE-13926
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
    public void dataPrep_AdvSearch_AONE_13926() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        String[] folders = { "House 1", "House 2", "House 3", "Techno" };
        String[] titles_descriptions = { "House", "Techno" };
        String[] files = { "House my 1", "House my 2", "House my 3", "Techno my" };
        String[] filesWithContentAndDescription = { "My 1", "My 2", "My 3", "My 4" };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ShareUser.openDocumentLibrary(drone);

            ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);
            // Creating files with given Title.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(files[0]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[1]);
            contentDetails.setTitle(titles_descriptions[1]);
            contentDetails.setDescription(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[2]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(files[3]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating files with given Title and description.
            contentDetails.setName(filesWithContentAndDescription[0]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[0]);
            contentDetails.setContent(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndDescription[1]);
            contentDetails.setTitle(titles_descriptions[1]);
            contentDetails.setDescription(titles_descriptions[0]);
            contentDetails.setContent(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndDescription[2]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[1]);
            contentDetails.setContent(titles_descriptions[0]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(filesWithContentAndDescription[3]);
            contentDetails.setTitle(titles_descriptions[0]);
            contentDetails.setDescription(titles_descriptions[0]);
            contentDetails.setContent(titles_descriptions[1]);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating folders
            ShareUserSitePage.createFolder(drone, folders[0], titles_descriptions[0], titles_descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[1], titles_descriptions[1], titles_descriptions[0]);
            ShareUserSitePage.createFolder(drone, folders[2], titles_descriptions[0], titles_descriptions[1]);
            ShareUserSitePage.createFolder(drone, folders[3], titles_descriptions[0], titles_descriptions[0]);
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
     * Test - AONE-13926: Title & Description Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the Description and Tiltle, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13926()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText = "house";

        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            login(drone, testUser, testPassword);

            // Searching for valid Title string with Description
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_FOLDER_SEARCH, searchText, "House 1", true);

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText, "House 1", true));
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText, "Techno", true));
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
     * DataPreparation method - AONE-13927
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Invite users with Content Manger roles</li>
     * <li>Create and upload folders and file with contents</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13927() throws Exception
    {
        String testName = getTestName();

        String mainUser = getUserNameFreeDomain(testName);
        String[] mainUserInfo = new String[] { mainUser };

        String testUser1 = getUserNameFreeDomain(testName + "1");
        String[] testUserInfo1 = new String[] { testUser1 };

        String testUser2 = getUserNameFreeDomain(testName + "2");
        String[] testUserInfo2 = new String[] { testUser2 };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

            // Inviting another user to join on site.
            String siteName = getSiteName(testName);
            String[] folders = { "testing1", "testing2" };
            String[] filesWithContent = { testName + "test1.txt", testName + "test2.txt" };

            // Main user login and creates the site.
            ShareUser.login(drone, mainUser, testPassword);
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
            ShareUtil.logout(drone);

            // User1 logged in and joined in site
            ShareUser.login(drone, testUser1, testPassword);
            ShareUserMembers.userRequestToJoinSite(drone, siteName);
            ShareUtil.logout(drone);

            // User2 logged in and joined in site
            ShareUser.login(drone, testUser2, testPassword);
            ShareUserMembers.userRequestToJoinSite(drone, siteName);
            ShareUtil.logout(drone);

            // Main user logs in to create the roles for the joined users on site.
            ShareUser.login(drone, mainUser, testPassword);
            ShareUserMembers.assignRoleToSiteMember(drone, testUser1, siteName, UserRole.MANAGER);
            ShareUserMembers.assignRoleToSiteMember(drone, testUser2, siteName, UserRole.MANAGER);
            ShareUtil.logout(drone);

            // User1 logged in and creates the folder and content in site
            ShareUser.login(drone, testUser1, testPassword);

            // Open site dashboard.
            ShareUser.openSiteDashboard(drone, siteName);

            // Creating folder
            ShareUserSitePage.createFolder(drone, folders[0], null, null);

            // Creating file with given content.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(filesWithContent[0]);
            contentDetails.setContent(filesWithContent[0]);
            DocumentLibraryPage docLibPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT).render();

            // Modifying the content
            DocumentDetailsPage docDetailsPage = docLibPage.selectFile(filesWithContent[0]).render();
            EditTextDocumentPage editPage = docDetailsPage.selectInlineEdit();
            editPage.render();
            editPage.save(contentDetails).render();

            ShareUtil.logout(drone);

            // User2 logged in and creates the folder and content in site
            ShareUser.login(drone, testUser2, testPassword);
            ShareUser.openSitesDocumentLibrary(drone, siteName);

            // Creating folder
            ShareUserSitePage.createFolder(drone, folders[1], null, null);

            // Creating file with given content.
            contentDetails = new ContentDetails();
            contentDetails.setName(filesWithContent[1]);
            contentDetails.setContent(filesWithContent[1]);
            docLibPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT).render();

            // Modifying the content
            docDetailsPage = docLibPage.selectFile(filesWithContent[1]).render();
            editPage = docDetailsPage.selectInlineEdit();
            editPage.render();
            editPage.save(contentDetails).render();

            ShareUtil.logout(drone);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test - AONE-13927: Name & Modified Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with Name & Modified</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13927()
    {
        /** Start Test */
        testName = getTestName();
        String mainUser = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + "1");

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            login(drone, mainUser, testPassword);

            // Searching for valid Name and Modified.
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), testName + "test");
            keyWordSearchText.put(SearchKeys.MODIFIER.getSearchKeys(), testUser1);
            String searchTerm = testName + "test";
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "1", true);

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "1", true));
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
     * DataPreparation method - AONE-13931
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
    public void dataPrep_AdvSearch_AONE_13931() throws Exception
    {
        String testName = getTestName();

        String TEST1_HTML_FILE = getFileName(testName) + "_" + TEST_HTML_FILE;
        String TEST2_TXT_FILE = getFileName(testName) + "_" + TEST_TXT_FILE;
        String TEST3_DOC_FILE = getFileName(testName) + "_" + TEST_DOC_FILE;
        String TEST4_JPG_FILE = getFileName(testName) + "_" + TEST_JPG_FILE;
        String TEST5_PDF_FILE = getFileName(testName) + "_" + TEST_PDF_FILE;
        String TEST6_GIF_FILE = getFileName(testName) + "_" + TEST_GIF_FILE;

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
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            ShareUser.openDocumentLibrary(drone);

            String[] fileInfo = new String[3];
            fileInfo[1] = DOCLIB;
            fileInfo[2] = "This is sample test file.";

            // Uploading html file
            fileInfo[0] = TEST1_HTML_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading txt file
            fileInfo[0] = TEST2_TXT_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);
            // Uploading doc file
            fileInfo[0] = TEST3_DOC_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading jpg file
            fileInfo[0] = TEST4_JPG_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading pdf file
            fileInfo[0] = TEST5_PDF_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading gif file
            fileInfo[0] = TEST6_GIF_FILE;
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
     * Test - AONE-13931: Name and Mime type search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with Name & Mimetype</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13931()
    {
        /** Start Test */
        testName = getTestName();

        String TEST1_HTML_FILE = getFileName(testName) + "_" + TEST_HTML_FILE;
        String testUser = getUserNameFreeDomain(testName);

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            login(drone, testUser, testPassword);

            // Searching for valid Name and Mime type.
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), testName + "_test");
            keyWordSearchText.put(SearchKeys.MIME.getSearchKeys(), "HTML");
            List<SearchResult> searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            if (!ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, testName + "_test", TEST1_HTML_FILE, true))
            {
                drone.refresh();
                drone.getCurrentPage().render();

                searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);
            }

            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, testName + "_test", TEST1_HTML_FILE, true));
            Assert.assertTrue(searchResults.size() == 1);
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
     * DataPreparation method - AONE-13935
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create or upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13935() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).replace("-", "");
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String folderName;

        try
        {
            ContentDetails contentDetails = new ContentDetails();

            // Creating and activating User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, testPassword);

            // Crating site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            // Creating 5 content Items.
            String searchTerm = siteName + "_test";

            contentDetails.setName(searchTerm + 1);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 11);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 2);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 3);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 22);
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating 5 folders
            searchTerm = siteName + "_testing";

            folderName = (searchTerm + 3);
            ShareUserSitePage.createFolder(drone, folderName, null, null);

            folderName = (searchTerm + 9);
            ShareUserSitePage.createFolder(drone, folderName, null, null);

            folderName = (searchTerm + 12);
            ShareUserSitePage.createFolder(drone, folderName, null, null);

            folderName = (searchTerm + 44);
            ShareUserSitePage.createFolder(drone, folderName, null, null);

            folderName = (searchTerm + 10);
            ShareUserSitePage.createFolder(drone, folderName, null, null);
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

    protected List<SearchResult> searchAndSort(WebDrone drone, String siteName, List<String> searchInfo, String searchTerm, String sortBy) throws Exception
    {

        drone.refresh();
        drone.getCurrentPage().render();
        ShareUser.openSiteDashboard(drone, siteName).render();

        AdvanceSearchPage contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);
        contentSearchPage.inputKeyword(searchTerm);

        FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

        // Sorting results by Name
        Assert.assertTrue(facetedSearchPage.hasResults());
        facetedSearchPage.getSort().sortByLabel(sortBy);
        facetedSearchPage.render();

        return  facetedSearchPage.getResults();

    }

    /**
     * Test - AONE-13935:Sorting by Name on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Name</li>
     * <li>Verify the search results are sorted by name</li>
     * <li>Go back to advance Search for folder type</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Name</li>
     * <li>Verify the search results are sorted by name in alphabetical order.</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13935()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName).replace("-", "");

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Searching with keyword
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);
            contentSearchPage.inputKeyword(searchTerm);

            FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Name
            // List<SearchResult> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.NAME);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("NAME");

            List<SearchResult> results1 = facetedSearchPage.getResults();
            Assert.assertNotNull(results1);

            if(results1.size() != 5)
                results1 = searchAndSort(drone, siteName, searchInfo, searchTerm, "NAME");

            Assert.assertEquals(results1.size(), 5);

            Assert.assertEquals(results1.get(0).getName(), searchTerm + "1");
            Assert.assertEquals(results1.get(1).getName(), searchTerm + "11");
            Assert.assertEquals(results1.get(2).getName(), searchTerm + "2");
            Assert.assertEquals(results1.get(3).getName(), searchTerm + "22");
            Assert.assertEquals(results1.get(4).getName(), searchTerm + "3");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH);
            searchTerm = siteName + "_testing";

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Name
            // resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.NAME);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("NAME");

            List<SearchResult> results = facetedSearchPage.getResults();

            if(results1.size() != 5)
                results = searchAndSort(drone, siteName, searchInfo, searchTerm, "NAME");

            Assert.assertNotNull(results);
            Assert.assertEquals(results.size(), 5);

            Assert.assertEquals(results.get(0).getName(), searchTerm + "10");
            Assert.assertEquals(results.get(1).getName(), searchTerm + "12");
            Assert.assertEquals(results.get(2).getName(), searchTerm + "3");
            Assert.assertEquals(results.get(3).getName(), searchTerm + "44");
            Assert.assertEquals(results.get(4).getName(), searchTerm + "9");

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
     * DataPreparation method - AONE-13936
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create and upload file with title and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13936() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).replace("-", "");
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String folderName;

        try
        {
            ContentDetails contentDetails = new ContentDetails();

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, testPassword);

            // Site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            // Creating 5 content Items.
            String searchTerm = siteName + "_test";

            contentDetails.setName(searchTerm + 1);
            contentDetails.setTitle("Eeee");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 2);
            contentDetails.setTitle("Dddd");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 3);
            contentDetails.setTitle("Cccc");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 4);
            contentDetails.setTitle("Bbbb");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 5);
            contentDetails.setTitle("Aaaa");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating 5 folders
            searchTerm = siteName + "_testing";

            folderName = (searchTerm + 1);
            ShareUserSitePage.createFolder(drone, folderName, "Eeee", null);

            folderName = (searchTerm + 2);
            ShareUserSitePage.createFolder(drone, folderName, "Dddd", null);

            folderName = (searchTerm + 3);
            ShareUserSitePage.createFolder(drone, folderName, "Cccc", null);

            folderName = (searchTerm + 4);
            ShareUserSitePage.createFolder(drone, folderName, "Bbbb", null);

            folderName = (searchTerm + 5);
            ShareUserSitePage.createFolder(drone, folderName, "Aaaa", null);
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
     * Test - AONE-13936: Sorting by Title on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Title</li>
     * <li>Verify the search results are sorted by title</li>
     * <li>Go back to advance Search for folder type</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Title</li>
     * <li>Verify the search results are sorted by title in alphabetical order.</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13936()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName).replace("-", "");

        AdvanceSearchPage contentSearchPage;

        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            // Searching and sorting the content items
            login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Title
            // List<SearchResult> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.TITLE);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("TITLE");

            List<SearchResult> resultsList = facetedSearchPage.getResults();

            if(resultsList.get(0).getName().equals(searchTerm + "5"))
                resultsList = searchAndSort(drone, siteName, searchInfo, searchTerm, "TITLE");

            Assert.assertEquals(resultsList.get(0).getName(), searchTerm + "5");
            Assert.assertEquals(resultsList.get(1).getName(), searchTerm + "4");
            Assert.assertEquals(resultsList.get(2).getName(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(3).getName(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(4).getName(), searchTerm + "1");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH);
            searchTerm = siteName + "_testing";

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Title
            // resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.TITLE);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("TITLE");

            List<SearchResult> resultsList1 = facetedSearchPage.getResults();
            Assert.assertNotNull(resultsList1);

            if(resultsList1.size() != 5)
                resultsList1 = searchAndSort(drone, siteName, searchInfo, searchTerm, "TITLE");

            Assert.assertEquals(resultsList1.size(), 5);

            Assert.assertEquals(resultsList1.get(0).getName(), searchTerm + "5");
            Assert.assertEquals(resultsList1.get(1).getName(), searchTerm + "4");
            Assert.assertEquals(resultsList1.get(2).getName(), searchTerm + "3");
            Assert.assertEquals(resultsList1.get(3).getName(), searchTerm + "2");
            Assert.assertEquals(resultsList1.get(4).getName(), searchTerm + "1");

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
     * DataPreparation method - AONE-13937
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create and upload file with description and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13937() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).replace("-", "");
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String folderName;

        try
        {
            ContentDetails contentDetails = new ContentDetails();

            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, testPassword);

            // Site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            // Creating 5 content Items.
            String searchTerm = siteName + "_test";

            contentDetails.setName(searchTerm + 1);
            contentDetails.setDescription("Eeee");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 2);
            contentDetails.setDescription("Dddd");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 3);
            contentDetails.setDescription("Cccc");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 4);
            contentDetails.setDescription("Bbbb");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(searchTerm + 5);
            contentDetails.setDescription("Aaaa");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating 5 folders
            searchTerm = siteName + "_testing";

            folderName = (searchTerm + 1);
            ShareUserSitePage.createFolder(drone, folderName, null, "Eeee");

            folderName = (searchTerm + 2);
            ShareUserSitePage.createFolder(drone, folderName, null, "Dddd");

            folderName = (searchTerm + 3);
            ShareUserSitePage.createFolder(drone, folderName, null, "Cccc");

            folderName = (searchTerm + 4);
            ShareUserSitePage.createFolder(drone, folderName, null, "Bbbb");

            folderName = (searchTerm + 5);
            ShareUserSitePage.createFolder(drone, folderName, null, "Aaaa");
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
     * Test - AONE-13937: Sorting by Description on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Description</li>
     * <li>Verify the search results are sorted by Description</li>
     * <li>Go back to advance Search for folder type</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Description</li>
     * <li>Verify the search results are sorted by title in alphabetical order.</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13937()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName).replace("-", "");

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Description
            // List<SearchResult> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.DESCRIPTION);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("DESCRIPTION");

            List<SearchResult> resultsList1 = facetedSearchPage.getResults();

            if(resultsList1.size() != 5)
                resultsList1 = searchAndSort(drone, siteName, searchInfo, searchTerm, "DESCRIPTION");

            Assert.assertNotNull(resultsList1);
            Assert.assertEquals(resultsList1.size(), 5);

            Assert.assertEquals(resultsList1.get(0).getName(), searchTerm + "5");
            Assert.assertEquals(resultsList1.get(1).getName(), searchTerm + "4");
            Assert.assertEquals(resultsList1.get(2).getName(), searchTerm + "3");
            Assert.assertEquals(resultsList1.get(3).getName(), searchTerm + "2");
            Assert.assertEquals(resultsList1.get(4).getName(), searchTerm + "1");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH);
            searchTerm = siteName + "_testing";

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Description
            // resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.DESCRIPTION);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("DESCRIPTION");

            List<SearchResult> resultsList2 = facetedSearchPage.getResults();

            if(resultsList2.size() != 5)
                resultsList2 = searchAndSort(drone, siteName, searchInfo, searchTerm, "DESCRIPTION");

            Assert.assertNotNull(resultsList2);
            Assert.assertEquals(resultsList2.size(), 5);

            Assert.assertEquals(resultsList2.get(0).getName(), searchTerm + "5");
            Assert.assertEquals(resultsList2.get(1).getName(), searchTerm + "4");
            Assert.assertEquals(resultsList2.get(2).getName(), searchTerm + "3");
            Assert.assertEquals(resultsList2.get(3).getName(), searchTerm + "2");
            Assert.assertEquals(resultsList2.get(4).getName(), searchTerm + "1");

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
     * DataPreparation method - AONE-13938
     * <ul>
     * <li>Login</li>
     * <li>Create two Users</li>
     * <li>Create Site</li>
     * <li>Invite the users on site</li>
     * <li>Create and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13938() throws Exception
    {
        String testName = getTestName();

        String mainUser = getUserNameFreeDomain(testName);
        String[] mainUserInfo = new String[] { mainUser };

        String testUser1 = getUserNameFreeDomain(testName + "1");
        String[] testUserInfo1 = new String[] { testUser1 };

        String testUser2 = getUserNameFreeDomain(testName + "2");
        String[] testUserInfo2 = new String[] { testUser2 };

        try
        {
            // User
            Boolean userStatus = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
            Boolean user1Status = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
            Boolean user2Status = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

            // Inviting user1 and user2 to join on site.
            if (userStatus && user1Status && user2Status)
            {
                String siteName = getSiteName(testName).replace("-", "");

                // Main user logs in and creates the site.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
                ShareUtil.logout(drone);

                // User1 logs in and joins the site
                ShareUser.login(drone, testUser1, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUser.logout(drone);

                // User2 logs in and joins in site
                ShareUser.login(drone, testUser2, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUtil.logout(drone);

                // Main user logs in and assign the roles to user1 and user2.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser1, siteName, UserRole.MANAGER);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser2, siteName, UserRole.MANAGER);
                ShareUtil.logout(drone);

                // User1 logs in and adds the content
                ShareUser.login(drone, testUser1, testPassword);

                ShareUser.openSiteDashboard(drone, siteName);

                ContentDetails contentDetails = new ContentDetails();
                String searchTerm = siteName + "_test";

                contentDetails.setName(searchTerm + 2);
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUtil.logout(drone);

                // User2 logs in and adds the content
                ShareUser.login(drone, testUser2, testPassword);

                ShareUser.openSiteDashboard(drone, siteName);

                contentDetails = new ContentDetails();

                contentDetails.setName(searchTerm + 1);
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUtil.logout(drone);
            }
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test - AONE-13938: Sorting by Creator on Search page (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Creator</li>
     * <li>Verify the search results are sorted by Creator</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13938()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName).replace("-", "");

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Creator
            // List<SearchResult> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.CREATOR);
            Assert.assertTrue(facetedSearchPage.getResults().size() >= 2, "Expecting 2 results, retrieved:");
            facetedSearchPage.getSort().sortByLabel("CREATOR");

            List<SearchResult> results = facetedSearchPage.getResults();

            if(results.size() != 2)
                results = searchAndSort(drone, siteName, searchInfo, searchTerm, "CREATOR");

            Assert.assertNotNull(results);
            Assert.assertEquals(results.size(), 2);

            Assert.assertEquals(results.get(0).getName(), searchTerm + "2");
            Assert.assertEquals(results.get(1).getName(), searchTerm + "1");

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * DataPreparation method - AONE-13939
     * <ul>
     * <li>Login</li>
     * <li>Create two Users</li>
     * <li>Create Site</li>
     * <li>Invite the users on site</li>
     * <li>Create and upload files and folders</li>
     * <li>Edit the contents meta data</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13939() throws Exception
    {
        String testName = getTestName();

        String mainUser = getUserNameFreeDomain(testName);
        String[] mainUserInfo = new String[] { mainUser };

        String testUser1 = getUserNameFreeDomain(testName + "1");
        String[] testUserInfo1 = new String[] { testUser1 };

        String testUser2 = getUserNameFreeDomain(testName + "2");
        String[] testUserInfo2 = new String[] { testUser2 };

        try
        {
            // Users Creation and Activation
            Boolean userStatus = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
            Boolean user1Status = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
            Boolean user2Status = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

            // User1 and User2 to joins the site.
            if (userStatus && user1Status && user2Status)
            {
                String siteName = getSiteName(testName).replace("-", "");

                // Main user logs in and creates the site.
                ShareUser.login(drone, mainUser, testPassword);

                ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

                ContentDetails contentDetails = new ContentDetails();
                String testFile = siteName + "_test";

                // Creating the content testFile1
                contentDetails.setName(testFile + 1);
                DocumentLibraryPage docLibPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT).render();

                // Editing author as testUser2 from document details page.
                editDocumentAuthorName(testUser2, (testFile + 1), docLibPage);

                // Creating the content testFile2
                contentDetails.setName(testFile + 2);
                docLibPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT).render();

                // Editing author as testUser1 from document details page.
                editDocumentAuthorName(testUser1, (testFile + 2), docLibPage);

                ShareUtil.logout(drone);

                // User1 logs in and joins the site
                ShareUser.login(drone, testUser1, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUtil.logout(drone);

                // User2 logs in and joins in site
                ShareUser.login(drone, testUser2, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUtil.logout(drone);

                // Main user logs in and assign the roles to user1 and user2.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser1, siteName, UserRole.MANAGER);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser2, siteName, UserRole.MANAGER);
                ShareUtil.logout(drone);
            }
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test - AONE-13939: :Sorting by Author on Search page (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Author</li>
     * <li>Verify the search results are sorted by Author</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13939()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName).replace("-", "");

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            login(drone, testUser, testPassword);

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone).render();

            // Sorting results by Creator
            // List<SearchResult> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.AUTHOR);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("AUTHOR");

            List<SearchResult> results = facetedSearchPage.getResults();

            if(results.size() != 2)
                results = searchAndSort(drone, siteName, searchInfo, searchTerm, "AUTHOR");

            Assert.assertNotNull(results);
            Assert.assertEquals(results.size(), 2);

            Assert.assertEquals(results.get(0).getName(), searchTerm + "2");
            Assert.assertEquals(results.get(1).getName(), searchTerm + "1");

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * DataPreparation method - AONE-13940
     * <ul>
     * <li>Login</li>
     * <li>Create two Users</li>
     * <li>Create Site</li>
     * <li>Invite the users on site</li>
     * <li>Create and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13940() throws Exception
    {
        String testName = getTestName();

        String mainUser = getUserNameFreeDomain(testName);
        String[] mainUserInfo = new String[] { mainUser };

        String testUser1 = getUserNameFreeDomain(testName + "1");
        String[] testUserInfo1 = new String[] { testUser1 };

        String testUser2 = getUserNameFreeDomain(testName + "2");
        String[] testUserInfo2 = new String[] { testUser2 };

        try
        {
            // User
            Boolean userStatus = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);
            Boolean user1Status = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
            Boolean user2Status = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

            // Inviting user1 and user2 to join on site.
            if (userStatus && user1Status && user2Status)
            {
                String siteName = getSiteName(testName).replace("-", "");

                // Main user logs in and creates the site.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

                ContentDetails contentDetails = new ContentDetails();
                String testFile = siteName + "_test";

                contentDetails.setName(testFile + 1);
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                contentDetails.setName(testFile + 2);
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUtil.logout(drone);

                // User1 logs in and joins the site
                ShareUser.login(drone, testUser1, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUtil.logout(drone);

                // User2 logs in and joins the site
                ShareUser.login(drone, testUser2, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUtil.logout(drone);

                // Main user logs in and assign the roles to user1 and user2.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser1, siteName, UserRole.MANAGER);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser2, siteName, UserRole.MANAGER);
                ShareUtil.logout(drone);

                // User1 logs in and edits the content
                ShareUser.login(drone, testUser1, testPassword);
                String fileName = testFile + 2;

                ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, fileName);
                webDriverWait(drone, 70000);
                ShareUtil.logout(drone);

                // User2 logs in and edits the content
                ShareUser.login(drone, testUser2, testPassword);
                fileName = testFile + 1;

                ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, fileName);
                ShareUtil.logout(drone);
            }
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test - AONE-13940: :Sorting by Modifier on Search page (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Creator</li>
     * <li>Verify the search results are sorted by Modifier</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13940()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName).replace("-", "");

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Creator
            // List<SearchResult> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.MODIFIER);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("MODIFIER");

            List<SearchResult> resultsList1 = facetedSearchPage.getResults();

            if(resultsList1.size() != 2 || !resultsList1.get(0).getName().equals(searchTerm + "2"))
                resultsList1 = searchAndSort(drone, siteName, searchInfo, searchTerm, "MODIFIER");

            Assert.assertNotNull(resultsList1);
            Assert.assertEquals(resultsList1.size(), 2);

            Assert.assertEquals(resultsList1.get(0).getName(), searchTerm + "2");
            Assert.assertEquals(resultsList1.get(1).getName(), searchTerm + "1");

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * DataPreparation method - AONE-13941
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13941() throws Exception
    {
        String testName = getTestName();

        String mainUser = getUserNameFreeDomain(testName);
        String[] mainUserInfo = new String[] { mainUser };

        try
        {
            // User
            Boolean userStatus = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);

            if (userStatus)
            {
                String siteName = getSiteName(testName).replace("-", "");

                // Main user logs in and creates the site.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

                ContentDetails contentDetails = new ContentDetails();

                // Creating first content and folder
                contentDetails.setName(siteName + "_test1");
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUserSitePage.createFolder(drone, (siteName + "_testing1"), null, null);

                // The below wait method is used to create the files with time gap so that files will be considered as old files.
                webDriverWait(drone, 70000);

                // Creating second content and folder
                contentDetails.setName(siteName + "_test2");
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUserSitePage.createFolder(drone, (siteName + "_testing2"), null, null);
                webDriverWait(drone, 70000);

                // Creating third content and folder
                contentDetails.setName(siteName + "_test3");
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUserSitePage.createFolder(drone, (siteName + "_testing3"), null, null);
            }
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
     * Test - AONE-13941: :Sorting by Created on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Created</li>
     * <li>Verify the search results are sorted by Created Date</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13941()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName).replace("-", "");

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            login(drone, testUser, testPassword);

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Created
            // List<SearchResult> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.CREATED);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("CREATED DATE");

            List<SearchResult> resultsList = facetedSearchPage.getResults();

            if(resultsList.size() != 3)
                resultsList = searchAndSort(drone, siteName, searchInfo, searchTerm, "CREATED DATE");

            Assert.assertNotNull(resultsList);
            Assert.assertEquals(resultsList.size(), 3);

            Assert.assertEquals(resultsList.get(0).getName(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(1).getName(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(2).getName(), searchTerm + "1");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");
            searchTerm = siteName + "_testing";

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Created
            // resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.CREATED);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("CREATED DATE");

            List<SearchResult> resultsList1 = facetedSearchPage.getResults();

            if(resultsList1.size() != 3)
                resultsList1 = searchAndSort(drone, siteName, searchInfo, searchTerm, "CREATED DATE");

            Assert.assertNotNull(resultsList1);
            Assert.assertEquals(resultsList1.size(), 3);

            Assert.assertEquals(resultsList1.get(0).getName(), searchTerm + "3");
            Assert.assertEquals(resultsList1.get(1).getName(), searchTerm + "2");
            Assert.assertEquals(resultsList1.get(2).getName(), searchTerm + "1");

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * DataPreparation method - AONE-13942
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13942() throws Exception
    {
        String testName = getTestName();

        String mainUser = getUserNameFreeDomain(testName);
        String[] mainUserInfo = new String[] { mainUser };
        String[] fileInfo = new String[3];

        try
        {
            // User
            Boolean userStatus = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);

            if (userStatus)
            {
                String siteName = getSiteName(testName).replace("-", "");

                // Main user logs in and creates the site.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

                ShareUserSitePage.createFolder(drone, (siteName + "_testing1"), null, null);

                // Uploading 1 kb file into first folder
                fileInfo[0] = TEST_FILE_WITH_1KB_SIZE;
                fileInfo[1] = siteName + "_testing1";
                fileInfo[2] = getRandomString(10);
                ShareUser.uploadFileInFolder(drone, fileInfo).render();

                ShareUser.openDocumentLibrary(drone).render();

                ShareUserSitePage.createFolder(drone, (siteName + "_testing2"), null, null);

                // Uploading 1 MB file into first folder
                fileInfo[0] = TEST_FILE_WITH_1MB_SIZE;
                fileInfo[1] = siteName + "_testing2";
                fileInfo[2] = getRandomString(100);

                ShareUser.uploadFileInFolder(drone, fileInfo).render();

                ShareUser.openDocumentLibrary(drone).render();

                ShareUserSitePage.createFolder(drone, (siteName + "_testing3"), null, null);

                // Uploading 2 MB file into first folder
                fileInfo[0] = TEST_FILE_WITH_2MB_SIZE;
                fileInfo[1] = siteName + "_testing3";
                fileInfo[2] = getRandomString(500);

                ShareUser.uploadFileInFolder(drone, fileInfo);
            }
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test - AONE-13942: :Sorting by Size on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by size</li>
     * <li>Verify the search results are sorted by file size</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13942()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName).replace("-", "");

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = "AONE13942";

        try
        {
            login(drone, testUser, testPassword);

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Size
            // List<SearchResult> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.SIZE);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("SIZE").render();

            List<SearchResult> resultsList = facetedSearchPage.getResults();

            if(resultsList.size() != 3 || !resultsList.get(0).getName().equals(TEST_FILE_WITH_1KB_SIZE))
                resultsList = searchAndSort(drone, siteName, searchInfo, searchTerm, "SIZE");

            Assert.assertNotNull(resultsList);
            Assert.assertEquals(resultsList.size(), 3);

            Assert.assertEquals(resultsList.get(0).getName(), TEST_FILE_WITH_1KB_SIZE);
            Assert.assertEquals(resultsList.get(1).getName(), TEST_FILE_WITH_1MB_SIZE);
            Assert.assertEquals(resultsList.get(2).getName(), TEST_FILE_WITH_2MB_SIZE);

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");
            searchTerm = siteName + "_testing";

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Size
            // resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.SIZE);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("SIZE").render();

            List<SearchResult> results = facetedSearchPage.getResults();

            if(results.size() != 3 || !results.get(0).getName().equals(searchTerm + "1"))
                results = searchAndSort(drone, siteName, searchInfo, searchTerm, "SIZE");

            Assert.assertNotNull(results);
            Assert.assertEquals(results.size(), 3);

            Assert.assertEquals(results.get(0).getName(), searchTerm + "1");
            Assert.assertEquals(results.get(1).getName(), searchTerm + "3");
            Assert.assertEquals(results.get(2).getName(), searchTerm + "2");

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * DataPreparation method - AONE-13943
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files with different mime types</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13943() throws Exception
    {
        String testName = getTestName();

        String TEST1_HTML_FILE = testName.replace("-", "") + "_" + TEST_HTML_FILE;
        String TEST2_TXT_FILE = testName.replace("-", "") + "_" + TEST_TXT_FILE;
        String TEST3_DOC_FILE = testName.replace("-", "") + "_" + TEST_DOC_FILE;
        String TEST4_JPG_FILE = testName.replace("-", "") + "_" + TEST_JPG_FILE;
        String TEST5_PDF_FILE = testName.replace("-", "") + "_" + TEST_PDF_FILE;
        String TEST6_GIF_FILE = testName.replace("-", "") + "_" + TEST_GIF_FILE;

        String siteName = getSiteName(testName).replace("-", "");
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, testPassword);

            // Site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            String[] fileInfo = new String[3];
            fileInfo[1] = DOCLIB;
            fileInfo[2] = "This is sample test file.";

            // Uploading html file
            fileInfo[0] = TEST1_HTML_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading txt file
            fileInfo[0] = TEST2_TXT_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading doc file
            fileInfo[0] = TEST3_DOC_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading jpg file
            fileInfo[0] = TEST4_JPG_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading pdf file
            fileInfo[0] = TEST5_PDF_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading gif file
            fileInfo[0] = TEST6_GIF_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test - AONE-13943: :Sorting by Mimetype on Search page (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Mimetype</li>
     * <li>Verify the search results are sorted by Mimetype</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13943()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        String TEST1_HTML_FILE = "_" + TEST_HTML_FILE;
        String TEST2_TXT_FILE = "_" + TEST_TXT_FILE;
        String TEST3_DOC_FILE = "_" + TEST_DOC_FILE;
        String TEST4_JPG_FILE = "_" + TEST_JPG_FILE;
        String TEST5_PDF_FILE = "_" + TEST_PDF_FILE;
        String TEST6_GIF_FILE = "_" + TEST_GIF_FILE;

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = testName.replace("-", "");

        try
        {
            login(drone, testUser, testPassword);

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Mime Type
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("MIME TYPE");

            List<SearchResult> results = facetedSearchPage.getResults();

            Assert.assertNotNull(results);
            Assert.assertEquals(results.size(), 6);

            Assert.assertEquals(results.get(0).getName(), searchTerm + TEST3_DOC_FILE);
            Assert.assertEquals(results.get(1).getName(), searchTerm + TEST5_PDF_FILE);
            Assert.assertEquals(results.get(2).getName(), searchTerm + TEST6_GIF_FILE);
            Assert.assertEquals(results.get(3).getName(), searchTerm + TEST4_JPG_FILE);
            Assert.assertEquals(results.get(4).getName(), searchTerm + TEST1_HTML_FILE);
            Assert.assertEquals(results.get(5).getName(), searchTerm + TEST2_TXT_FILE);

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * DataPreparation method - AONE-13944
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create, modify and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13944() throws Exception
    {
        String testName = getTestName();

        String mainUser = getUserNameFreeDomain(testName + "1");
        String[] mainUserInfo = new String[] { mainUser };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);

            String siteName = getSiteName(testName).replace("-", "");

            // Main user logs in and creates the site.
            ShareUser.login(drone, mainUser, testPassword);
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            ContentDetails contentDetails = new ContentDetails();

            // Creating first content and folder
            contentDetails.setName(siteName + "_test1");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
            ShareUserSitePage.createFolder(drone, (siteName + "_testing1"), null, null);

            // Creating second content and folder
            contentDetails.setName(siteName + "_test2");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
            ShareUserSitePage.createFolder(drone, (siteName + "_testing2"), null, null);

            // Creating third content and folder
            contentDetails.setName(siteName + "_test3");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
            ShareUserSitePage.createFolder(drone, (siteName + "_testing3"), null, null);

            // Modifying 2nd Content and Folder. Others will be modified in the test
            ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, (siteName + "_test2"));
            ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, (siteName + "_testing2"));
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test - AONE-13944: :Sorting by Modified on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Modified</li>
     * <li>Verify the search results are sorted by Modified Date</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13944()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");
        String siteName = getSiteName(testName).replace("-", "");

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            login(drone, testUser, testPassword);

            ShareUser.openSitesDocumentLibrary(drone, siteName);

            ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);

            // Modifying the 1st content and folder.
            ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, (siteName + "_test1"));
            ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, (siteName + "_testing1"));

            // This is required to keep the edit time difference between 2 files
            // webDriverWait(drone,70000);

            // Modifying 3rd content and folder.
            ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, (siteName + "_test3"));
            ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, (siteName + "_testing3"));

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            FacetedSearchPage facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Modified
            // List<SearchResult> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.MODIFIED);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("MODIFIED DATE");

            List<SearchResult> results = facetedSearchPage.getResults();

            if(results.size() != 3)
                results = searchAndSort(drone, siteName, searchInfo, searchTerm, "MODIFIED DATE");

            Assert.assertNotNull(results);
            Assert.assertEquals(results.size(), 3);

            Assert.assertEquals(results.get(0).getName(), searchTerm + "3");
            Assert.assertEquals(results.get(1).getName(), searchTerm + "1");
            Assert.assertEquals(results.get(2).getName(), searchTerm + "2");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");
            searchTerm = siteName + "_testing";

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            facetedSearchPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);

            // Sorting results by Modified
            // resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.MODIFIED);
            Assert.assertTrue(facetedSearchPage.hasResults());
            facetedSearchPage.getSort().sortByLabel("MODIFIED DATE");

            List<SearchResult> results1 = facetedSearchPage.getResults();

                if(results1.size() != 3)
                results1 = searchAndSort(drone, siteName, searchInfo, searchTerm, "MODIFIED DATE");

            Assert.assertNotNull(results1);
            Assert.assertEquals(results1.size(), 3);

            Assert.assertEquals(results1.get(0).getName(), searchTerm + "3");
            Assert.assertEquals(results1.get(1).getName(), searchTerm + "1");
            Assert.assertEquals(results1.get(2).getName(), searchTerm + "2");

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * DataPreparation method - AONE-13945
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files with different mime types and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13945() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName).replace("-", "");

        String TEST1_HTML_FILE = siteName + "_" + TEST_HTML_FILE;
        String TEST2_TXT_FILE = siteName + "_" + TEST_TXT_FILE;
        String TEST3_DOC_FILE = siteName + "_" + TEST_DOC_FILE;

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, testPassword);

            // Site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            String[] fileInfo = new String[3];
            fileInfo[1] = DOCLIB;
            fileInfo[2] = "This is sample test file.";

            // Uploading html file
            fileInfo[0] = TEST1_HTML_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading txt file
            fileInfo[0] = TEST2_TXT_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Uploading doc file
            fileInfo[0] = TEST3_DOC_FILE;
            ShareUser.uploadFileInFolder(drone, fileInfo);

            // Creating folders
            ShareUserSitePage.createFolder(drone, siteName + "_Test4", null, null);
            ShareUserSitePage.createFolder(drone, siteName + "_Test5", null, null);
            ShareUserSitePage.createFolder(drone, siteName + "_Test6", null, null);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test - AONE-13945: :Sorting by Modified on Search page (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Type</li>
     * <li>Verify the search results are sorted by Type</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13945()
    {
        /** Start Test */

        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName).replace("-", "");

        AdvanceSearchPage contentSearchPage;

        List<String> resultContentItemNames = new ArrayList<>();
        List<String> resultFolderNames = new ArrayList<>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);

        // Searching and sorting the content items
        String searchTerm = siteName + "_Test";

        try
        {
            boolean found = false;
            int k = 0;
            while (!found && k < 3)
            {
                login(drone, testUser, testPassword);

                // Navigating to Advance search page.
                contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

                // Searching with keyword
                contentSearchPage.inputKeyword(searchTerm);
                contentSearchPage.clickSearch().render();

                // Clicking the search on basic search page.
                FacetedSearchPage page = ShareUserSearchPage.repeatBasicSearch(drone, BASIC_SEARCH, searchTerm);

                // Sorting results by Type
                page.getSort().sortByLabel("TYPE").render();

                List<SearchResult> resultsList = page.getResults();

                // Splitting first 3 results into contents list and next 3 into folders list.
                for (int i = 0; i < 6; i++)
                {
                    if (i < 3)
                    {
                        resultContentItemNames.add(resultsList.get(i).getName());
                    }
                    else
                    {
                        resultFolderNames.add(resultsList.get(i).getName());
                    }
                }

                if(resultContentItemNames.contains(siteName + "_" + TEST_TXT_FILE) &&
                       resultContentItemNames.contains(siteName + "_" + TEST_HTML_FILE) &&
                       resultContentItemNames.contains(siteName + "_" + TEST_DOC_FILE))
                   found = true;
                else
                {
                    drone.deleteCookies();
                    drone.refresh();
                    drone.getCurrentPage().render();
                }

                k++;
            }

            // Verifying the first 3 displayed items are content items
            Assert.assertTrue(resultContentItemNames.contains(siteName + "_" + TEST_HTML_FILE));
            Assert.assertTrue(resultContentItemNames.contains(siteName + "_" + TEST_TXT_FILE));
            Assert.assertTrue(resultContentItemNames.contains(siteName + "_" + TEST_DOC_FILE));

            // Verifying the first 3 displayed items are folder items
            Assert.assertTrue(resultFolderNames.contains(searchTerm + "4"));
            Assert.assertTrue(resultFolderNames.contains(searchTerm + "5"));
            Assert.assertTrue(resultFolderNames.contains(searchTerm + "6"));
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * DataPreparation method - AONE-13946
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13946() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test";

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, testPassword);

            // Site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            // Creating 5 content files.
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileOrFolder + "_a");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(fileOrFolder + "_b");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(fileOrFolder + "_c");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(fileOrFolder + "_d");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            contentDetails.setName(fileOrFolder + "_e");
            ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

            // Creating 5 folders
            fileOrFolder = siteName + "_testing";

            ShareUserSitePage.createFolder(drone, fileOrFolder + "_a", null, null);
            ShareUserSitePage.createFolder(drone, fileOrFolder + "_b", null, null);
            ShareUserSitePage.createFolder(drone, fileOrFolder + "_c", null, null);
            ShareUserSitePage.createFolder(drone, fileOrFolder + "_d", null, null);
            ShareUserSitePage.createFolder(drone, fileOrFolder + "_e", null, null);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test - AONE-13946: Sorting by Relevance on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Relevance</li>
     * <li>Verify the search results are sorted by Relevance</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13946()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";
        Map<String, String> keyWordSearchText = new HashMap<>();

        try
        {
            login(drone, testUser, testPassword);

            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);

            // Searching with keyword
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_a", true);

            // Asserting the results as relevance sort order
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_a", true),
                    " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_b", true),
                    " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_c", true),
                    " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_d", true),
                    " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_e", true),
                    " COUD-1916- Search returning inconsistent results in Cloud2 environment.");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");
            searchTerm = siteName + "_testing";
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);

            // Searching with keyword
            advancedSearchWithRetry(drone, searchInfo, keyWordSearchText, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_a", true);

            // Asserting the results as relevance sort order
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_a", true),
                    " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_b", true),
                    " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_c", true),
                    " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_d", true),
                    " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_e", true),
                    " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * This private method is used to edit the author name of document or folder.
     * 
     * @param testUser - user
     * @param testFile - name of file
     * @param docLibPage - document library page
     */
    private void editDocumentAuthorName(String testUser, String testFile, DocumentLibraryPage docLibPage)
    {
        DocumentDetailsPage docDetailsPage = docLibPage.selectFile(testFile).render();
        EditDocumentPropertiesPage editPropertiesPage = docDetailsPage.selectEditProperties().render();
        editPropertiesPage.setAuthor(testUser);
        editPropertiesPage.selectSave().render();
    }

    /**
     * DataPreparation method - AONE-13946
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13934() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String userName = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test";

        // Creating a user and a site
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUserInfo);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Several content items and folders are created
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileOrFolder + "_1");
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails.setName(fileOrFolder + "_2");
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserSitePage.createFolder(drone, fileOrFolder + "_fol1", null, null);
        ShareUserSitePage.createFolder(drone, fileOrFolder + "_fol2", null, null);
    }

    /**
     * Test - AONE-13934: Search Page
     * <ul>
     * <li>Login</li>
     * <li>From Site Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Verify all elements on Search results page</li>
     * <li>Go back to Advanced Search</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne" })
    public void AONE_13934() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String userName = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test";

        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";
        Map<String, String> keyWordSearchText = new HashMap<>();
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);

        ShareUser.login(drone, testUserInfo);
        ShareUser.openSiteDashboard(drone, siteName).render();

        // Search for content
        ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        // Verify Search page;
        FacetedSearchPage facetedSearchPage = ShareUser.getSharePage(drone).render();
        facetedSearchPage.render();
        Assert.assertTrue(facetedSearchPage.isPageCorrect(), "Some elements are not displayed on Search page");
        // Assert.assertTrue(facetedSearchPage.isGoToAdvancedSearchPresent(), "'Go to Advanced Search' link is absent: ACE-2877");
        // Click Go to Advanced Search link; TODO after fix bug

        // facetedSearchPage.goBackToAdvanceSearch();
        // Assert.assertTrue(ShareUser.getSharePage(drone) instanceof AdvanceSearchPage, "The user isn't redirected to Adv Search page");

        ShareUser.logout(drone);
    }

    /**
     * DataPreparation method - AONE-13947
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload a content item and a folder</li>
     * </ul>
     * 
     * @throws Exception
     */
    // @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13947() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName) + 1;
        String userName = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test1";

        // Creating a user and a site
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUserInfo);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // A content item and a folder are created
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileOrFolder + "_1");
        contentDetails.setTitle(fileOrFolder + "_1");
        contentDetails.setContent(fileOrFolder + "_1");
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserSitePage.createFolder(drone, fileOrFolder + "_fol1", null, null);
    }

    // todo not relevant for 5.0
    /**
     * Test - AONE-13947: View in Browser action
     * <ul>
     * <li>Login</li>
     * <li>From Site Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Click "View in Browser" button upon thumbnail</li>
     * <li>Content item is opened in new tab for preview</li>
     * </ul>
     */
    // @Test(groups = { "AlfrescoOne" })
    public void AONE_13947() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName) + 1;
        String userName = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test1";

        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";
        Map<String, String> keyWordSearchText = new HashMap<>();
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);

        ShareUser.login(drone, testUserInfo);
        ShareUser.openSiteDashboard(drone, siteName).render();

        // Search for content
        ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        // Search page is opened, items with given keywords are displayed
        FacetedSearchPage facetedSearchPage = ShareUser.getSharePage(drone).render();
        facetedSearchPage.render();
        Assert.assertTrue(facetedSearchPage.getResults().get(0).getName().equals(fileOrFolder + "_1"), "The result wasn't found");

        // Click "View in Browser";
        // Get the current url
        String url = drone.getCurrentUrl();

        ActionsSet set = facetedSearchPage.getResultByName(fileOrFolder + "_1").getActions();
        set.clickActionByName("View In Browser");
        Assert.assertTrue(facetedSearchPage.getResultByName(fileOrFolder + "_1").getActions().hasActionByName("View In Browser"));
        facetedSearchPage.getResultByName(fileOrFolder + "_1").getActions().clickActionByName("View In Browser");

        // Get the url again
        String newUrl = drone.getCurrentUrl();
        Assert.assertNotSame(url, newUrl, "After clicking on action the url should have changed");
        drone.navigateTo(url);

        ShareUser.logout(drone);
    }

    /**
     * DataPreparation method - AONE-13948
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload a content item and a folder</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepAlfrescoOne", "NonGrid" })
    public void dataPrep_AdvSearch_AONE_13948() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String userName = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test";

        // Creating a site
        ShareUser.login(drone, testUserInfo);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // A content item and a folder are created
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileOrFolder);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
    }

    // todo not relevant for 5.0
    /**
     * Test - AONE-13948: Download action
     * <ul>
     * <li>Login</li>
     * <li>From Site Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Click "Download" button upon thumbnail</li>
     * <li>Content item is downloaded</li>
     * </ul>
     */
    @Test(groups = { "AlfrescoOne", "NonGrid" })
    public void AONE_13948() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String userName = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test";

        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";
        Map<String, String> keyWordSearchText = new HashMap<>();
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);
        try
        {
            setupCustomDrone(WebDroneType.DownLoadDrone);
            ShareUser.login(customDrone, testUserInfo);
            ShareUser.openSiteDashboard(customDrone, siteName).render();

            // Search for content
            ShareUserSearchPage.advanceSearch(customDrone, searchInfo, keyWordSearchText);

            // Search page is opened, items with given keywords are displayed
            SiteResultsPage siteResultsPage = (SiteResultsPage) ShareUser.getSharePage(customDrone);
            assertTrue(siteResultsPage.getResults().get(0).getTitle().equals(fileOrFolder), "The result wasn't found");

            // Click "View in Browser" button upon thumbnail;
            siteResultsPage.getResults().get(0).clickOnDownloadIcon();
            getSharePage(customDrone).waitForFile(downloadDirectory + fileOrFolder);
            List<String> extractedChildFilesOrFolders = ShareUser.getContentsOfDownloadedArchieve(customDrone, downloadDirectory);
            assertTrue(extractedChildFilesOrFolders.contains(fileOrFolder), "The file wasn't downloaded");

            deleteFile(fileOrFolder);
        }
        catch (Exception e)
        {
            ShareUser.logout(customDrone);
            customDrone.quit();
        }
    }

    /**
     * DataPreparation method - AONE-13949
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload a content item and a folder</li>
     * </ul>
     * 
     * @throws Exception
     */
    // @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13949() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test";

        // Creating a site
        ShareUser.login(drone, ADMIN_USERNAME);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.login(drone, testUserInfo);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();
        ShareUser.openDocumentLibrary(drone);

        String[] fileTypes = { "html", "txt", "xml", "doc", "docx", "xls", "ppt", "jpg", "bmp", "pdf", "gif", "odt", "ods" };

        // Start Test
        for (String fileType : fileTypes)
        {
            String fileName = fileOrFolder + "-" + fileType + "." + fileType;
            File file = newFile(DATA_FOLDER + SLASH + fileName, testName);

            ShareUserSitePage.uploadFile(drone, file);
        }
    }

    // todo not relevant for 5.0
    /**
     * Test - AONE-13949: Thumbnail display
     * <ul>
     * <li>Login</li>
     * <li>From Site Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Verify thumbnails for found items</li>
     * </ul>
     */
    // @Test(groups = { "AlfrescoOne" })
    public void AONE_13949() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String userName = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test";
        String[] fileTypes = { "html", "txt", "xml", "doc", "docx", "xls", "ppt", "jpg", "bmp", "pdf", "gif", "odt", "ods" };
        Arrays.sort(fileTypes);
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        Map<String, String> keyWordSearchText = new HashMap<>();
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), fileOrFolder);

        ShareUser.login(drone, testUserInfo);
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        // Search page is opened, items with given keywords are displayed
        SiteResultsPage siteResultsPage = (SiteResultsPage) ShareUser.getSharePage(drone);
        String currentUrl = drone.getCurrentUrl();
        siteResultsPage.sortPage(SortType.NAME);
        List<SearchResult> resultItems = siteResultsPage.getResults();
        for (String fileType : fileTypes)
        {
            // String imgFileName = String.format("thumbnail_placeholder_256_%s.png", fileType);
            //
            // File imgFile = new File(DATA_FOLDER + SLASH + "placeholder-thumbnails","imgpreview.jpg");
            // Target target = new ImageTarget(imgFile);
            //
            // String thumbnailUrl = resultItems.get(Arrays.asList(fileTypes).indexOf(fileType)).getThumbnailUrl();
            // drone.navigateTo(thumbnailUrl);
            // drone.executeJavaScript("window.location.reload()");
            // drone.waitForPageLoad(3000);
            // Assert.assertTrue(drone.isImageVisible(target), "The preview isn't generated for " + fileType);
            // drone.navigateTo(currentUrl);
            // resultItems = siteResultsPage.getResults();

            // Verify preview is generated
            String previewUrl = resultItems.get(Arrays.asList(fileTypes).indexOf(fileType)).getPreViewUrl();
            Assert.assertTrue(previewUrl != null && previewUrl.contains("thumbnail"));
        }
    }

    // todo not relevant for 5.0
    /**
     * DataPreparation method - AONE-13950
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Upload more than 250 items</li>
     * </ul>
     * 
     * @throws Exception
     */
    // @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AdvSearch_AONE_13950() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test";

        ShareUser.login(drone, ADMIN_USERNAME);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.login(drone, testUserInfo);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openDocumentLibrary(drone);

        // Creating more than 250 items
        for (int i = 1; i <= 251; i++)
        {
            String[] fileInfo = { fileOrFolder + i };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
    }

    /**
     * Test - AONE-13950: Pagination on Search page
     * <ul>
     * <li>Login</li>
     * <li>From Site Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Click Next >> link till the 5 page</li>
     * <li>Check items are displayed correctly</li>
     * <li>Click Prev >> link till the 1 page</li>
     * <li>Click on page number</li>
     * </ul>
     */
    // @Test(groups = { "AlfrescoOne" })
    public void AONE_13950() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String userName = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        String fileOrFolder = siteName + "_test";
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        Map<String, String> keyWordSearchText = new HashMap<>();
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), fileOrFolder);

        // Log in and perform a search
        ShareUser.login(drone, testUserInfo);
        ShareUser.openSiteDashboard(drone, siteName);
        ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

        // Verify pagination count
        SiteResultsPage siteResultsPage = (SiteResultsPage) ShareUser.getSharePage(drone);
        List<SearchResult> resultItems = siteResultsPage.getResults();
        Assert.assertTrue(siteResultsPage.paginationCount() >= 5, "Pagination count is incorrect");

        // Click till 5th page
        siteResultsPage.clickNextPage().render();
        siteResultsPage.clickNextPage().render();
        siteResultsPage.clickNextPage().render();
        siteResultsPage.clickNextPage().render();
        Assert.assertTrue(siteResultsPage.getPaginationPosition() == 5 && siteResultsPage.getResults() != null, "The items are not available");

        // Click till 1st page
        siteResultsPage.clickPrevPage().render();
        siteResultsPage.clickPrevPage().render();
        siteResultsPage.clickPrevPage().render();
        siteResultsPage.clickPrevPage().render();
        Assert.assertTrue(siteResultsPage.getPaginationPosition() == 1 && siteResultsPage.getResults() != null, "The items are not available");

        // Click on any page number - verify items are displayed
        siteResultsPage.paginationSelect(3).render();
        Assert.assertTrue(siteResultsPage.getPaginationPosition() == 3 && siteResultsPage.getResults() != null, "The items are not available");
        siteResultsPage.paginationSelect(2).render();
        Assert.assertTrue(siteResultsPage.getPaginationPosition() == 2 && siteResultsPage.getResults() != null, "The items are not available");
    }

    private void deleteFile(String fileName)
    {
        File fileToDelete = new File(downloadDirectory + fileName);
        if (fileToDelete.delete())
            logger.info("File was deleted");
        else
            logger.info("Delete operation has failed");
    }
}