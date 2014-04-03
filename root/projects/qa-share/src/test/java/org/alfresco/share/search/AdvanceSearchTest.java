package org.alfresco.share.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.search.SearchResultItem;
import org.alfresco.po.share.search.SearchResultsPage;
import org.alfresco.po.share.search.SortType;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditTextDocumentPage;
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
public class AdvanceSearchTest extends AbstractTests
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
     * DataPreparation method - ALF-4991
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
    public void dataPrep_AdvSearch_ALF_4991() throws Exception
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
          
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

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
     * Test - ALF-4991: Name & Title Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>Search with name and title</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4991()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText[] = { "house", "techno" };

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Name string with Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText[0]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[0], "House my 1", true));

            // Searching for valid Name string with Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText[1]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText[1], "House my 2", true));
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
     * DataPreparation method - ALF-4992
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
    public void dataPrep_AdvSearch_ALF_4992() throws Exception
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

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

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
     * Test - ALF-4992: Name Title Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the title and Name, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4992()
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String searchText[] = { "house", "techno" };

        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Name string with Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText[0]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[0], "House 1", true));

            // Searching for valid Name string with Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText[0]);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText[1]);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText[1], "House 2", true));
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
     * DataPreparation method - ALF-4993
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
    public void dataPrep_AdvSearch_ALF_4993() throws Exception
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

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

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
     * Test - ALF-4993: Name & Description Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>Search with name and description</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4993()
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

            // Searching for valid Name string with Description
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "House my 1", true));
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
     * DataPreparation method - ALF-4994
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
    public void dataPrep_AdvSearch_ALF_4994() throws Exception
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

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

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
     * Test - ALF-4994: Name & Description Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the Description and Name, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4994()
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

            // Searching for valid Name string with Description
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText, "House 1", true));
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
     * DataPreparation method - ALF-4995
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
    public void dataPrep_AdvSearch_ALF_4995() throws Exception
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

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

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
     * Test - ALF-4995: Name & Title & Description Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>Search with name,title and description</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4995()
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

            // Searching for valid Name string with Description and Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "House my 1", true));
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
     * DataPreparation method - ALF-4996
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
    public void dataPrep_AdvSearch_ALF_4996() throws Exception
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

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
            
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
     * Test - ALF-4996: Name & Title & Description Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the Description,Name and Tiltle, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4996()
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

            // Searching for valid Name string with Description and Title
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText, "House 1", true));
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
     * DataPreparation method - ALF-4997
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
    public void dataPrep_AdvSearch_ALF_4997() throws Exception
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

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);            

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
     * Test - ALF-4997: Title & Description Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>Search with Title and description</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4997()
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

            // Searching for valid Title string with Description
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "House my 1", true));

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchText, "Techno my", true));
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
     * DataPreparation method - ALF-4998
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
    public void dataPrep_AdvSearch_ALF_4998() throws Exception
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

            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
            
            ShareUser.openDocumentLibrary(drone);

            ShareUserSitePage.selectView(drone,ViewType.SIMPLE_VIEW);
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
     * Test - ALF-4998: Title & Description Search (Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search Folder form</li>
     * <li>In the Description and Tiltle, enter "house"</li>
     * <li>click on search</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4998()
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

            // Searching for valid Title string with Description
            keyWordSearchText.put(SearchKeys.TITLE.getSearchKeys(), searchText);
            keyWordSearchText.put(SearchKeys.DESCRIPTION.getSearchKeys(), searchText);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText, "House 1", true));
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_FOLDER_SEARCH, searchText, "Techno", true));
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
     * DataPreparation method - ALF-4999
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
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_4999() throws Exception
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
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
            ShareUser.logout(drone);

            // User1 logged in and joined in site
            ShareUser.login(drone, testUser1, testPassword);
            ShareUserMembers.userRequestToJoinSite(drone, siteName);
            ShareUser.logout(drone);

            // User2 logged in and joined in site
            ShareUser.login(drone, testUser2, testPassword);
            ShareUserMembers.userRequestToJoinSite(drone, siteName);
            ShareUser.logout(drone);

            // Main user logs in to create the roles for the joined users on site.
            ShareUser.login(drone, mainUser, testPassword);
            ShareUserMembers.assignRoleToSiteMember(drone, testUser1, siteName, UserRole.MANAGER);
            ShareUserMembers.assignRoleToSiteMember(drone, testUser2, siteName, UserRole.MANAGER);
            ShareUser.logout(drone);

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

            ShareUser.logout(drone);

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

            ShareUser.logout(drone);
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
     * Test - ALF-4999: Name & Modified Search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with Name & Modified</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_4999()
    {
        /** Start Test */
        testName = getTestName();
        String mainUser = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + "1");

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, mainUser, testPassword);

            // Searching for valid Name and Modified.
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), testName + "test");
            keyWordSearchText.put(SearchKeys.MODIFIER.getSearchKeys(), testUser1);
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            String searchTerm = testName + "test";

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "1", true));
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
     * DataPreparation method - ALF-5003
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
    public void dataPrep_AdvSearch_ALF_5003() throws Exception
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
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

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
     * Test - ALF-5003: Name and Mime type search (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with Name & Mimetype</li>
     * <li>Validate the search results are returned as expected</li>
     * </ul>
     */
    @Test
    public void ALF_5003()
    {
        /** Start Test */
        testName = getTestName();

        String TEST1_HTML_FILE = getFileName(testName) + "_" + TEST_HTML_FILE;
        String testUser = getUserNameFreeDomain(testName);

        /** Test Data Setup */
        Map<String, String> keyWordSearchText = new HashMap<String, String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH, "searchAllSitesFromMyDashBoard");

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Searching for valid Name and Mime type.
            keyWordSearchText.put(SearchKeys.NAME.getSearchKeys(), testName + "_test");
            keyWordSearchText.put(SearchKeys.MIME.getSearchKeys(), "HTML");
            List<SearchResultItem> searchResults = ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, testName + "_test", TEST1_HTML_FILE, true));
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
     * DataPreparation method - ALF-5017
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create or upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5017() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
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
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

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

    /**
     * Test - ALF-5017:Sorting by Name on Search page (Content/Folder type)
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
    @Test
    public void ALF_5017()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Searching with keyword
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);
            contentSearchPage.inputKeyword(searchTerm);
            
            SearchResultsPage searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Name
            List<SearchResultItem> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.NAME);

            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "1");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "11");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(3).getTitle(), searchTerm + "22");
            Assert.assertEquals(resultsList.get(4).getTitle(), searchTerm + "3");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH);
            searchTerm = siteName + "_testing";

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Name
            resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.NAME);
          
            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "10");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "12");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(3).getTitle(), searchTerm + "44");
            Assert.assertEquals(resultsList.get(4).getTitle(), searchTerm + "9");
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
     * DataPreparation method - ALF-5018
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create and upload file with title and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5018() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
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
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

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
     * Test - ALF-5018: Sorting by Title on Search page (Content/Folder type)
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
    @Test
    public void ALF_5018()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        AdvanceSearchPage contentSearchPage;

        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            // Searching and sorting the content items
            ShareUser.login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            SearchResultsPage searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Title
            List<SearchResultItem> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.TITLE);

            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "5");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "4");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(3).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(4).getTitle(), searchTerm + "1");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH);
            searchTerm = siteName + "_testing";

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Title
            resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.TITLE);

            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "5");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "4");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(3).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(4).getTitle(), searchTerm + "1");
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
     * DataPreparation method - ALF-5019
     * <ul>
     * <li>Login</li>
     * <li>Create Users</li>
     * <li>Create Site</li>
     * <li>Create and upload file with description and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5019() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
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
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

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
     * Test - ALF-5019: Sorting by Description on Search page (Content/Folder type)
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
    @Test
    public void ALF_5019()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            SearchResultsPage searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Description
            List<SearchResultItem> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.DESCRIPTION);

            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "5");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "4");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(3).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(4).getTitle(), searchTerm + "1");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH);
            searchTerm = siteName + "_testing";

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Description
            resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.DESCRIPTION);

            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "5");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "4");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(3).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(4).getTitle(), searchTerm + "1");
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
     * DataPreparation method - ALF-5020
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
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5020() throws Exception
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
                String siteName = getSiteName(testName);

                // Main user logs in and creates the site.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);

                // User1 logs in and joins the site
                ShareUser.login(drone, testUser1, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUser.logout(drone);

                // User2 logs in and joins in site
                ShareUser.login(drone, testUser2, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUser.logout(drone);

                // Main user logs in and assign the roles to user1 and user2.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser1, siteName, UserRole.MANAGER);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser2, siteName, UserRole.MANAGER);
                ShareUser.logout(drone);

                // User1 logs in and adds the content
                ShareUser.login(drone, testUser1, testPassword);

                ShareUser.openSiteDashboard(drone, siteName);

                ContentDetails contentDetails = new ContentDetails();
                String searchTerm = siteName + "_test";

                contentDetails.setName(searchTerm + 2);
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUser.logout(drone);

                // User2 logs in and adds the content
                ShareUser.login(drone, testUser2, testPassword);

                ShareUser.openSiteDashboard(drone, siteName);

                contentDetails = new ContentDetails();

                contentDetails.setName(searchTerm + 1);
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUser.logout(drone);
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
     * Test - ALF-5020: Sorting by Creator on Search page (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Creator</li>
     * <li>Verify the search results are sorted by Creator</li>
     * </ul>
     */
    @Test
    public void ALF_5020()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            SearchResultsPage searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Creator
            List<SearchResultItem> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.CREATOR);

            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "1");
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
     * DataPreparation method - ALF-5021
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
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5021() throws Exception
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
                String siteName = getSiteName(testName);

                // Main user logs in and creates the site.
                ShareUser.login(drone, mainUser, testPassword);

                ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

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

                ShareUser.logout(drone);

                // User1 logs in and joins the site
                ShareUser.login(drone, testUser1, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUser.logout(drone);

                // User2 logs in and joins in site
                ShareUser.login(drone, testUser2, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUser.logout(drone);

                // Main user logs in and assign the roles to user1 and user2.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser1, siteName, UserRole.MANAGER);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser2, siteName, UserRole.MANAGER);
                ShareUser.logout(drone);
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
     * Test - ALF-5021: :Sorting by Author on Search page (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Author</li>
     * <li>Verify the search results are sorted by Author</li>
     * </ul>
     */
    @Test
    public void ALF_5021()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            SearchResultsPage searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Creator
            List<SearchResultItem> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.AUTHOR);

            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "1");
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
     * DataPreparation method - ALF-5022
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
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5022() throws Exception
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
                String siteName = getSiteName(testName);

                // Main user logs in and creates the site.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

                ContentDetails contentDetails = new ContentDetails();
                String testFile = siteName + "_test";

                contentDetails.setName(testFile + 1);
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                contentDetails.setName(testFile + 2);
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUser.logout(drone);

                // User1 logs in and joins the site
                ShareUser.login(drone, testUser1, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUser.logout(drone);

                // User2 logs in and joins the site
                ShareUser.login(drone, testUser2, testPassword);
                ShareUserMembers.userRequestToJoinSite(drone, siteName);
                ShareUser.logout(drone);

                // Main user logs in and assign the roles to user1 and user2.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser1, siteName, UserRole.MANAGER);
                ShareUserMembers.assignRoleToSiteMember(drone, testUser2, siteName, UserRole.MANAGER);
                ShareUser.logout(drone);

                // User1 logs in and edits the content
                ShareUser.login(drone, testUser1, testPassword);
                String fileName = testFile + 2;

                ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, fileName);
                ShareUser.logout(drone);

                // User2 logs in and edits the content
                ShareUser.login(drone, testUser2, testPassword);
                fileName = testFile + 1;

                ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, fileName);
                ShareUser.logout(drone);
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
     * Test - ALF-5022: :Sorting by Modifier on Search page (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Creator</li>
     * <li>Verify the search results are sorted by Modifier</li>
     * </ul>
     */
    @Test
    public void ALF_5022()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Open User DashBoard
            ShareUser.openSiteDashboard(drone, siteName).render();

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            SearchResultsPage searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Creator
            List<SearchResultItem> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.MODIFIER);

            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "1");
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
     * DataPreparation method - ALF-5023
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5023() throws Exception
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
                String siteName = getSiteName(testName);

                // Main user logs in and creates the site.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

                ContentDetails contentDetails = new ContentDetails();

                // Creating first content and folder
                contentDetails.setName(siteName + "_test1");
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUserSitePage.createFolder(drone, (siteName + "_testing1"), null, null);

                // The below wait method is used to create the files with time gap so that files will be considered as old files.
                webDriverWait(drone,70000);

                // Creating second content and folder
                contentDetails.setName(siteName + "_test2");
                ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

                ShareUserSitePage.createFolder(drone, (siteName + "_testing2"), null, null);
                webDriverWait(drone,70000);

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
     * Test - ALF-5023: :Sorting by Created on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Created</li>
     * <li>Verify the search results are sorted by Created Date</li>
     * </ul>
     */
    @Test
    public void ALF_5023()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            SearchResultsPage searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Created
            List<SearchResultItem> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.CREATED);
            
            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "1");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");
            searchTerm = siteName + "_testing";

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Created
            resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.CREATED);
            
            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "1");
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
     * DataPreparation method - ALF-5024
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5024() throws Exception
    {
        String testName = getTestName();

        String mainUser = getUserNameFreeDomain(testName);
        String[] mainUserInfo = new String[] { mainUser };
        String[] fileInfo = new String[2];

        try
        {
            // User
            Boolean userStatus = CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);

            if (userStatus)
            {
                String siteName = getSiteName(testName);

                // Main user logs in and creates the site.
                ShareUser.login(drone, mainUser, testPassword);
                ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

                ShareUserSitePage.createFolder(drone, (siteName + "_testing1"), null, null);

                // Uploading 1 kb file into first folder
                fileInfo[0] = TEST_FILE_WITH_1KB_SIZE;
                fileInfo[1] = siteName + "_testing1";

                ShareUser.uploadFileInFolder(drone, fileInfo).render();

                ShareUser.openDocumentLibrary(drone).render();

                ShareUserSitePage.createFolder(drone, (siteName + "_testing2"), null, null);

                // Uploading 1 MB file into first folder
                fileInfo[0] = TEST_FILE_WITH_1MB_SIZE;
                fileInfo[1] = siteName + "_testing2";

                ShareUser.uploadFileInFolder(drone, fileInfo).render();

                ShareUser.openDocumentLibrary(drone).render();

                ShareUserSitePage.createFolder(drone, (siteName + "_testing3"), null, null);

                // Uploading 2 MB file into first folder
                fileInfo[0] = TEST_FILE_WITH_2MB_SIZE;
                fileInfo[1] = siteName + "_testing3";

                ShareUser.uploadFileInFolder(drone, fileInfo);
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
     * Test - ALF-5024: :Sorting by Size on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by size</li>
     * <li>Verify the search results are sorted by file size</li>
     * </ul>
     */
    @Test
    public void ALF_5024()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = "ALF-5024";

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            SearchResultsPage searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Size
            List<SearchResultItem> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.SIZE);

            Assert.assertEquals(resultsList.get(0).getTitle(), TEST_FILE_WITH_1KB_SIZE);
            Assert.assertEquals(resultsList.get(1).getTitle(), TEST_FILE_WITH_1MB_SIZE);
            Assert.assertEquals(resultsList.get(2).getTitle(), TEST_FILE_WITH_2MB_SIZE);

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");
            searchTerm = siteName + "_testing";

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Size
            resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.SIZE);

            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "1");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "2");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "3");
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
     * DataPreparation method - ALF-5025
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files with different mime types</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5025() throws Exception
    {
        String testName = getTestName();

        String TEST1_HTML_FILE = testName + "_" + TEST_HTML_FILE;
        String TEST2_TXT_FILE = testName + "_" + TEST_TXT_FILE;
        String TEST3_DOC_FILE = testName + "_" + TEST_DOC_FILE;
        String TEST4_JPG_FILE = testName + "_" + TEST_JPG_FILE;
        String TEST5_PDF_FILE = testName + "_" + TEST_PDF_FILE;
        String TEST6_GIF_FILE = testName + "_" + TEST_GIF_FILE;

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
        finally
        {
            testCleanup(drone, testName);
        }
    }

    /**
     * Test - ALF-5025: :Sorting by Mimetype on Search page (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Mimetype</li>
     * <li>Verify the search results are sorted by Mimetype</li>
     * </ul>
     */
    @Test
    public void ALF_5025()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);

        String TEST1_HTML_FILE = testName + "_" + TEST_HTML_FILE;
        String TEST2_TXT_FILE = testName + "_" + TEST_TXT_FILE;
        String TEST3_DOC_FILE = testName + "_" + TEST_DOC_FILE;
        String TEST4_JPG_FILE = testName + "_" + TEST_JPG_FILE;
        String TEST5_PDF_FILE = testName + "_" + TEST_PDF_FILE;
        String TEST6_GIF_FILE = testName + "_" + TEST_GIF_FILE;

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = testName + "_test";

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            SearchResultsPage searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Mime Type.
            List<SearchResultItem> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.MIMETYPE);

            Assert.assertEquals(resultsList.get(0).getTitle(), TEST3_DOC_FILE);
            Assert.assertEquals(resultsList.get(1).getTitle(), TEST5_PDF_FILE);
            Assert.assertEquals(resultsList.get(2).getTitle(), TEST6_GIF_FILE);
            Assert.assertEquals(resultsList.get(3).getTitle(), TEST4_JPG_FILE);
            Assert.assertEquals(resultsList.get(4).getTitle(), TEST1_HTML_FILE);
            Assert.assertEquals(resultsList.get(5).getTitle(), TEST2_TXT_FILE);
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
     * DataPreparation method - ALF-5026
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create, modify and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5026() throws Exception
    {
        String testName = getTestName();

        String mainUser = getUserNameFreeDomain(testName + "1");
        String[] mainUserInfo = new String[] { mainUser };

        try
        {
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, mainUserInfo);

            String siteName = getSiteName(testName);

            // Main user logs in and creates the site.
            ShareUser.login(drone, mainUser, testPassword);
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

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
        finally
        {
            testCleanup(drone, testName);
        }
    }

    /**
     * Test - ALF-5026: :Sorting by Modified on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Modified</li>
     * <li>Verify the search results are sorted by Modified Date</li>
     * </ul>
     */
    @Test
    public void ALF_5026()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName + "1");
        String siteName = getSiteName(testName);

        AdvanceSearchPage contentSearchPage;

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";

        try
        {
            ShareUser.login(drone, testUser, testPassword);
            
            ShareUser.openSitesDocumentLibrary(drone, siteName);
            
            ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);
            
            // Modifying the 1st content and folder.
            ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, (siteName + "_test1"));
            ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, (siteName + "_testing1"));
            
            webDriverWait(drone,70000);
            
            // Modifying 3rd content and folder.
            ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, (siteName + "_test3"));
            ShareUserSitePage.editPropertiesFromDocLibPage(drone, siteName, (siteName + "_testing3"));

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            SearchResultsPage searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Modified
            List<SearchResultItem> resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.MODIFIED);
            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "1");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "2");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");
            searchTerm = siteName + "_testing";

            // Navigating back to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            searchResultsPage = ShareUserSearchPage.clickSearchOnAdvanceSearch(drone);
            
            // Sorting results by Modified
            resultsList = ShareUserSearchPage.sortSearchResults(drone, SortType.MODIFIED);
            Assert.assertEquals(resultsList.get(0).getTitle(), searchTerm + "3");
            Assert.assertEquals(resultsList.get(1).getTitle(), searchTerm + "1");
            Assert.assertEquals(resultsList.get(2).getTitle(), searchTerm + "2");
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
     * DataPreparation method - ALF-5027
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files with different mime types and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5027() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

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
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

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
        finally
        {
            testCleanup(drone, testName);
        }
    }

    /**
     * Test - ALF-5027: :Sorting by Modified on Search page (Content type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Type</li>
     * <li>Verify the search results are sorted by Type</li>
     * </ul>
     */
    @Test
    public void ALF_5027()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        AdvanceSearchPage contentSearchPage;

        List<String> resultContentItemNames = new ArrayList<String>();
        List<String> resultFolderNames = new ArrayList<String>();
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);

        // Searching and sorting the content items
        String searchTerm = siteName + "_Test";

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            // Navigating to Advance search page.
            contentSearchPage = ShareUserSearchPage.navigateToAdvanceSearch(drone, searchInfo);

            // Searching with keyword
            contentSearchPage.inputKeyword(searchTerm);
            contentSearchPage.clickSearch().render();

            // Clicking the search on basic search page.
            SearchResultsPage page = ShareUserSearchPage.repeatSearch(drone, BASIC_SEARCH, searchTerm);

            // Sorting results by Modified
            //TODO: Subs: Rename sortPage as sortResults?
            page = (SearchResultsPage) page.sortPage(SortType.TYPE);
            page.render();
            List<SearchResultItem> resultsList = page.getResults();

            // Splitting first 3 results into contents list and next 3 into folders list.
            for(int i=0; i<6; i++)
            {
                if(i<3)
                {
                    resultContentItemNames.add(resultsList.get(i).getTitle());  
                }
                else
                {
                    resultFolderNames.add(resultsList.get(i).getTitle());
                }
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
        finally
        {
            testCleanup(drone, testName);
        }
    }

    /**
     * DataPreparation method - ALF-5028
     * <ul>
     * <li>Login</li>
     * <li>Create User</li>
     * <li>Create Site</li>
     * <li>Create and upload files and folders</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test(groups={"DataPrepAdvanceSearch"})
    public void dataPrep_AdvSearch_ALF_5028() throws Exception
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
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

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
        finally
        {
            testCleanup(drone, testName);
        }
    }

    /**
     * Test - ALF-5028: Sorting by Relevance on Search page (Content/Folder type)
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search form</li>
     * <li>Search with keyword</li>
     * <li>Select sort by Relevance</li>
     * <li>Verify the search results are sorted by Relevance</li>
     * </ul>
     */
    @Test
    public void ALF_5028()
    {
        /** Start Test */
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        // Searching and sorting the content items
        List<String> searchInfo = Arrays.asList(ADV_CONTENT_SEARCH);
        String searchTerm = siteName + "_test";
        Map<String, String> keyWordSearchText = new HashMap<String, String>();

        try
        {
            ShareUser.login(drone, testUser, testPassword);

            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);

            // Searching with keyword
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            // Asserting the results as relevance sort order
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_a", true), " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_b", true), " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_c", true), " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_d", true), " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_e", true), " COUD-1916- Search returning inconsistent results in Cloud2 environment.");

            // Searching and sorting the folders
            searchInfo = Arrays.asList(ADV_FOLDER_SEARCH, "searchAllSitesFromMyDashBoard");
            searchTerm = siteName + "_testing";
            keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), searchTerm);

            // Searching with keyword
            ShareUserSearchPage.advanceSearch(drone, searchInfo, keyWordSearchText);

            // Asserting the results as relevance sort order
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_a", true), " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_b", true), " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_c", true), " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_d", true), " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
            Assert.assertTrue(ShareUserSearchPage.checkSearchResultsWithRetry(drone, ADV_CONTENT_SEARCH, searchTerm, searchTerm + "_e", true), " COUD-1916- Search returning inconsistent results in Cloud2 environment.");
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
     * This private method is used to edit the author name of document or folder.
     * 
     * @param testUser
     * @param testFile
     * @param docLibPage
     */
    private void editDocumentAuthorName(String testUser, String testFile, DocumentLibraryPage docLibPage)
    {
        DocumentDetailsPage docDetailsPage = docLibPage.selectFile(testFile).render();
        EditDocumentPropertiesPage editPropertiesPage = docDetailsPage.selectEditProperties().render();
        editPropertiesPage.setAuthor(testUser);
        editPropertiesPage.selectSave().render();
    }
}