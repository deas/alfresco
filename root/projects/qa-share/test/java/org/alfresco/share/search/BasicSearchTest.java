package org.alfresco.share.search;

import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class BasicSearchTest extends AbstractTests
{
    private static Log logger = LogFactory.getLog(BasicSearchTest.class);    

    protected String testUser;
    
    protected String siteName = "";

	/**
     * Class includes: Tests from TestLink in Area: Advanced Search Tests
     * <ul>
     *   <li>Test searches using various Properties, content, Proximity, Range Queries</li>
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
    
    // AdvancedSearchTest
    @Test(groups={"DataPrepSearch"})
    public void dataPrep_AdvSearch_cloud_421() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] {testUser};
        
        String siteName = getSiteName(testName);
        
        // Files
        String[] fileName = new String[21];
        fileName[0] = getFileName(testName + "." + "xlsx");
        fileName[1] = getFileName(testName + "." + "txt");
        fileName[2] = getFileName(testName + "." + "msg");
        fileName[3] = getFileName(testName + "." + "pdf");
        fileName[4] = getFileName(testName + "." + "xml");
        fileName[5] = getFileName(testName + "." + "html");
        fileName[6] = getFileName(testName + "." + "eml");
        fileName[7] = getFileName(testName + "." + "opd");
        fileName[8] = getFileName(testName + "." + "ods");
        fileName[9] = getFileName(testName + "." + "odt");
        fileName[10] = getFileName(testName + "." + "xls");
        fileName[11] = getFileName(testName + "." + "xsl");
        fileName[12] = getFileName(testName + "." + "doc");
        fileName[13] = getFileName(testName + "." + "docx");
        fileName[14] = getFileName(testName + "." + "pptx");
        fileName[15] = getFileName(testName + "." + "pot");
        fileName[16] = getFileName(testName + "." + "xsd");
        fileName[17] = getFileName(testName + "." + "js");
        fileName[18] = getFileName(testName + "." + "java");
        fileName[19] = getFileName(testName + "." + "css");
        fileName[20] = getFileName(testName + "." + "rtf");

        Integer fileTypes = fileName.length - 1;

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

        // UpLoad Files
        for (int index = 0; index <= fileTypes; index++)
        {
            String[] fileInfo = { fileName[index] };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
    }

    
    /**
     * Test:
     * <ul>
     *   <li>Login</li>
     *   <li>Check Search Results for diff types of files: Search based on Content</li>
     * </ul>
     */
    @Test
    public void cloud_421()
    {

    		/**Start Test*/
    		String testName = getTestName();

    		/**Test Data Setup*/
    		String testUser = getUserNameFreeDomain(testName);
    		String siteName = getSiteName(testName);
    		String[] fileName = new String[21];

    		fileName[0] = getFileName(testName + "." + "xlsx");
    		fileName[1] = getFileName(testName + "." + "xml");
    		fileName[2] = getFileName(testName + "." + "msg");
    		fileName[3] = getFileName(testName + "." + "pdf");
    		fileName[4] = getFileName(testName + "." + "xml");
    		fileName[5] = getFileName(testName + "." + "html");
    		fileName[6] = getFileName(testName + "." + "eml");
    		fileName[7] = getFileName(testName + "." + "opd");
    		fileName[8] = getFileName(testName + "." + "ods");
    		fileName[9] = getFileName(testName + "." + "odt");
    		fileName[10] = getFileName(testName + "." + "xls");
    		fileName[11] = getFileName(testName + "." + "xsl");
    		fileName[12] = getFileName(testName + "." + "doc");
    		fileName[13] = getFileName(testName + "." + "docx");
    		fileName[14] = getFileName(testName + "." + "pptx");
    		fileName[15] = getFileName(testName + "." + "pot");
    		fileName[16] = getFileName(testName + "." + "xsd");
    		fileName[17] = getFileName(testName + "." + "js");
    		fileName[18] = getFileName(testName + "." + "java");
    		fileName[19] = getFileName(testName + "." + "css");
    		fileName[20] = getFileName(testName + "." + "rtf");

    		String searchTerm = testName;

    		Integer fileTypes = fileName.length-1;

    		/**Test Steps*/
    		//Login
    		ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

    		//Search Specific Site
    		//Open Site DashBoard

    		ShareUser.openSiteDashboard(drone, siteName);

    		//Search
    		ShareUserSearchPage.basicSearch(drone, searchTerm, false);

    		//Check the Search Results
    		Boolean searchOk = ShareUserSearchPage.checkSearchResultsWithRetry(drone, BASIC_SEARCH, searchTerm, fileName[fileTypes], true);
    		Assert.assertTrue(searchOk, "Search Results don't include the last file: " + fileName[fileTypes]);

    		//Check each result contains the search term: apart from xlsx
    		//Assert.assertFalse(ShareUser.isSearchItemAvailable(drone, fileName[0]),"FTS for xlsx not supported: Found; " + fileName);
                
    		for (int index=1; index <= fileTypes; index++)
    		{                    
    			if (fileName[index].endsWith("ods") || fileName[index].endsWith("odt"))
    			{
    				//Skip Check until these files created in proper format are kept in testData folder
    			}
    			else
    			{
    			Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, fileName[index]),"Not Found " + fileName[index]);
    			}
    		}

    		//Search all sites
    		ShareUserSearchPage.basicSearch(drone, searchTerm, true);

    		//Check the Search Results
    		searchOk = ShareUserSearchPage.checkSearchResultsWithRetry(drone, BASIC_SEARCH, searchTerm, fileName[fileTypes], true);
    		Assert.assertTrue((searchOk), "Search Results don't include the last file: " + fileName[fileTypes]);

            
            //Check each result contains the search term: apart from xlsx
            //Assert.assertFalse(ShareUser.isSearchItemAvailable(drone,fileName[0]),"FTS for xlsx not supported: Found; " + fileName);
            
            for (int index=1; index <= fileTypes; index++)
            {                
                if (fileName[index].endsWith("ods") || fileName[index].endsWith("odt"))
                {
                    // Skip Check until these files created in proper format are kept in testData folder
                }
                else
                {
                    Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, fileName[index]), "Not Found " + fileName[index]);
                }
            }            
    }   
    
    @Test(groups={"DataPrepSearch"})
    public void dataPrep_AdvSearch_cloud_440() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] {testUser};        

        String fileName = getFileName(testName + ".txt");
        String folderName = getFolderName(testName);
        
        try{
            // User

            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC);

            // UpLoad Files
            String[] fileInfo = { fileName };
            ShareUser.uploadFileInFolder(drone, fileInfo);
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
        }
        catch(Exception e)
        {
            reportError(drone, testName, e);
        }

    }
    
    /**
     * Class includes: Tests from TestLink in Area: Dash-board Tests
     * <ul>
     *   <li>Login</li>
     *   <li>Create Site: Public</li>
     *   <li>Open User Dash-board</li>
     *   <li>Check that the User Dash-board > My Sites Dashlet shows the new Site</li>
     * </ul>
     */
    @Test
    public void cloud_440()
    {
        /** Start Test */
        String testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName + ".txt");
        String folderName = getFolderName(testName);

        String[] searchTerm = new String[4];
        searchTerm[0] = "ISUNSET:'cm:" + "creator'";
        searchTerm[1] = "ISUNSET:'cm:" + "title'";
        searchTerm[2] = "ISUNSET:'cm:" + "description'";
        searchTerm[3] = "ISUNSET:'cm:" + "author'";

        Integer searchCount = searchTerm.length - 1;
        Boolean searchOk = false;

        /** Test Steps */
        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open SiteDashBoard
        ShareUser.openSiteDashboard(drone, siteName);

        // Perform Basic Search
        ShareUserSearchPage.basicSearch(drone, searchTerm[0], false);

        // Check the Results
        searchOk = ShareUserSearchPage.checkSearchResultsWithRetry(drone, BASIC_SEARCH, searchTerm[0], fileName, false);
        Assert.assertTrue(searchOk, "Incorrect Result for search term: " + searchTerm[0]);

        for (int index = 1; index <= searchCount; index++)
        {
            // Search
            ShareUserSearchPage.basicSearch(drone, searchTerm[index], false);

            searchOk = ShareUserSearchPage.checkSearchResultsWithRetry(drone, BASIC_SEARCH, searchTerm[index], fileName, true);
            Assert.assertTrue(searchOk, "Incorrect Result for search term: " + searchTerm[index]);
        }
    }   
}
