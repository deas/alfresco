package org.alfresco.share.search;

import static org.alfresco.po.share.site.document.ContentType.PLAINTEXT;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.OpCloudTestContext;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class FacetedSearchPageTest1.
 *
 * This tests the Faceted Search page behaves as one would expect.
 *
 * @author charu
 */

public class FacetedSearchPageTest1 extends AbstractUtils
{

    /** The logger. */
    private static Log logger = LogFactory.getLog(FacetedSearchPageTest.class);

    /** Constants */
    
    private static final String fileStem = "-fs-test1.txt";
    
    private OpCloudTestContext testContext;
    private DashBoardPage dashBoardPage;
    private FacetedSearchPage facetedSearchPage;    
    
    private String siteName;
    private String testUser1;
    private String testUser2;
    private String testUser3;
    private static final String USER_HOMES_FOLDER = "User Homes";
    /* (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#setup()
     * 
     * Should not be cloud only.
     * 
     */
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        trace("Starting setup");

        super.setup();
        this.testContext = new OpCloudTestContext(this);

        // Compose user and site names
        String testName = "FacetedSearch" + testContext.getRunId();
        this.testUser1 = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] {this.testUser1};
        
        this.testUser2 = getUserNameFreeDomain(testName)+"1";
        String[] testUserInfo1 = new String[] {this.testUser2};
        
        this.testUser3 = getUserNameForDomain(testName, "cloud");
        String[] testUserInfo2 = new String[] {this.testUser3};
        
        this.siteName = getSiteName(testName);

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);
        
        // Login as user
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Create site
        ShareUser.createSite(drone, this.siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload 5 Files
        for (int i=0; i < 5; i++)
        {
            String fileInfo =  (char)(i+97) + fileStem;
            ContentDetails contentDetails = new ContentDetails(fileInfo, fileInfo, fileInfo, fileInfo);
            ShareUser.createContent(drone, contentDetails, PLAINTEXT);
        }

        // Navigate to the faceted search page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
        
        // Logout        
        ShareUtil.logout(drone);

        trace("Setup complete");
    }
    
    /*searchAndClickDownloadActionTest
    This test is to verify 'Download' action is displayed under actions for uploaded file for same user
    Click on 'Down load' link under actions by same user
    Verify url is not changed after clicking on down load link in search results page
    Verify only 'Down load' link and 'View in Browser' links are displayed for a file under action for different user 
    */
    
    @Test(groups = "Enterprise-Only")
    public void ALF_3126() throws Exception
    {
        trace("Starting searchAndClickDownloadActionTest");        
        
        String actionName1 = "Download";
        String actionName2 = "View In Browser";
        String actionName3 = "Edit Offline";
        String actionName4 = "Delete Document";
        String actionName5 = "Manage Permissions";
        
        String name = ("b-fs-test1.txt");
        String name1 = ("c-fs-test1.txt");
        String name2 = ("d-fs-test1.txt");
        String name3 = ("e-fs-test1.txt");
        String name4 = ("a-fs-test1.txt");
        
        // Login as user1
        userLogin1();

        // Do a search 
        doretrySearch(name);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");        
        
        // Get the current url
        String url = drone.getCurrentUrl();
        
        //Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName1));        
        
        // Click the first action        
        facetedSearchPage.getResultByName(name).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // We should be on the faceted search page
        Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");       
                
        // Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Logout
        ShareUtil.logout(drone);
        
        //login as user2
        userLogin2();
        
        //Do a search for the letter 'a'
        doretrySearch("fs-test1.txt");     
        
        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");
        
        Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName1));
        Assert.assertTrue(facetedSearchPage.getResultByName(name1).getActions().hasActionByName(actionName2));        
        Assert.assertFalse(facetedSearchPage.getResultByName(name2).getActions().hasActionByName(actionName3));        
        Assert.assertFalse(facetedSearchPage.getResultByName(name3).getActions().hasActionByName(actionName4));        
        Assert.assertFalse(facetedSearchPage.getResultByName(name4).getActions().hasActionByName(actionName5));

        trace("searchAndClickDownloadActionTest complete" );
    }
    
    /*searchAndClickViewInBrowserActionTest
    This test is to verify 'View In Browser' action is displayed under actions for uploaded file for another user
    Click on 'View In Browser' link under actions by another user
    Verify url is changed after clicking on 'View In Browser' link in search results page    
    */
    
    @Test(groups = "Enterprise-Only")
    public void ALF_3127() throws Exception
    {
        trace("Starting searchAndClickViewInBrowserActionTest");      
                
        String actionName2 = "View In Browser";        
        
        String name = ("b-fs-test1.txt");
        String name1 = ("c-fs-test1.txt");
        
        // Login as user2
        userLogin2();

        // Do a search 
        doretrySearch("test1.txt");

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");        
        
        // Get the current url
        String url = drone.getCurrentUrl();
        
        //Check Actions are displayed on Facet results page for file
        Assert.assertTrue(facetedSearchPage.getResultByName(name1).getActions().hasActionByName(actionName2));        
        
        // Click the second action        
        facetedSearchPage.getResultByName(name).getActions().clickActionByName(actionName2);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // We should be on view in browser page
        Assert.assertNotSame(url, newUrl, "After clicking on action the url should have changed");                 
      
        // Logout
        ShareUtil.logout(drone);       
       
        trace("searchAndClickViewInBrowserActionTest complete");
    }
    
    /*searchAndClickEdit OfflineActionTest
    This test is to verify 'Edit Offline' action is displayed under actions for uploaded file for same user
    Click on 'Down load' link under actions by same user
    Verify url is not changed after clicking on 'Edit Offline' link in search results page   
    */
    
    @Test(groups = "alfresco-one")
    public void ALF_3128() throws Exception
    {
        trace("Starting searchAndClickEditOfflineActionTest");      
                
        String actionName3 = "Edit Offline";      
               
        String name = ("c-fs-test1.txt");
        
        // Login as user1
        userLogin1();

        // Do a search 
        doretrySearch(name);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for name there should be some search results");        
        
        // Get the current url
        String url = drone.getCurrentUrl();
        
        //Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName3));        
        
        // Click the third action        
        facetedSearchPage.getResultByName(name).getActions().clickActionByName(actionName3);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // We should be on the faceted search page
        Assert.assertEquals(url, newUrl, "After clicking on action the url should not have changed");    
                    
        //Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Logout
        ShareUtil.logout(drone);       
       
        trace("searchAndClickEditOfflineActionTest complete");
    }
    
    /*searchAndClickDeleteDocumentActionTest
    This test is to verify 'Delete Document' action is displayed under actions for uploaded file for same user
    Click on 'Delete Document' link under actions by same user
    Verify the doc is present in results page when cancel delete
    Verify the doc is not present in results page when confirm delete 
    Verify url is not changed after confirm 'Delete Document' link in search results page   
    */
    
    @Test(groups = "alfresco-one")
    public void ALF_3129() throws Exception
    {
        trace("Starting searchAndClickDeleteDocumentActionTest");      
                
        String actionName4 = "Delete Document";        
        
        String name = "e-fs-test1.txt";
        
        // Login as user1
        userLogin1();

        // Do a search
        doretrySearch(name);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");        
        
        // Get the current url
        String url = drone.getCurrentUrl();
        
        //Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName4));        
        
        // Click the first action        
        facetedSearchPage.getResultByName(name).getActions().clickActionByNameAndDialogByButtonName(actionName4,"No");
        
        //Verify name1 is present in search page
        //Assert.assertNotNull(facetedSearchPage.getResultByName(name));
        Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, name));
        
        //Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
        
        // Do a search for text
        doretrySearch(name);

        // Click the first action        
        facetedSearchPage.getResultByName(name).getActions().clickActionByNameAndDialogByButtonName(actionName4,"Yes");
        
        //Verify name is not present in search page
        Assert.assertFalse(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, name));

        // Get the url again
        String newUrl = drone.getCurrentUrl();
        
        // We should be on the faceted search page
        Assert.assertEquals(url, newUrl, "After searching for text and clicking result 1, the url should not have changed");              
                  
        //Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Logout
        ShareUtil.logout(drone);       
       
        trace("searchAndClickDeleteDocumentActionTest complete");
    }
    
    /*searchAndClickManagePermissionsActionTest
    This test is to verify 'Manage Permissions' action is displayed under actions for uploaded file for same user
    Click on 'Manage Permissions' link under actions by same user
    Verify manage permissions page is displayed successfully
    Verify url is not changed after confirm 'Delete Document' link in search results page   
    */
    
    @Test(groups = "alfresco-one")
    public void ALF_3130() throws Exception
    {
        trace("Starting searchAndClickManagePermissionsActionTest");      
                
        String actionName5 = "Manage Permissions";      
               
        String name1 = "c-fs-test1.txt";
        
        // Login as user1
        userLogin1();

        // Do a search
        doretrySearch(name1);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for name1 there should be some search results"); 
         
        //Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(name1).getActions().hasActionByName(actionName5));        
        
        // Click the first action        
        facetedSearchPage.getResultByName(name1).getActions().clickActionByName(actionName5);
        
        //We should be on the faceted search page
        ManagePermissionsPage managePermissionsPage = (ManagePermissionsPage) drone.getCurrentPage();
        Assert.assertTrue(managePermissionsPage.getTitle().contains("Manage Permissions"));
        
        //Open user dash board
        ShareUser.openUserDashboard(drone);                  
        
        // Logout
        ShareUtil.logout(drone);       
       
        trace("searchAndClickManagePermissionsActionTest complete");
    }
    
    /*searchAndVerifyFolderActionsTest
    This test is to verify  actions are displayed under actions for folder to same user      
    */
    
    @Test(groups = "alfresco-one")
    public void ALF_3131() throws Exception
    {
        trace("Starting searchAndVerifyFolderActionsTest");     
            
        String folderName = "Folder1";              
        String actionName1 = "View Details";
        String actionName2 = "Manage Rules";
        String actionName3 = "Delete Folder";
        String actionName4 = "Manage Permissions";        
        
        // Login as user1
        userLogin1();
        
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);        
        ShareUserSitePage.createFolder(drone, folderName, null);
        
        //Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
        
        //Do retry search        
        doretrySearch(folderName);       
                 
        //Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(folderName).getActions().hasActionByName(actionName1));        
        Assert.assertTrue(facetedSearchPage.getResultByName(folderName).getActions().hasActionByName(actionName2));
        Assert.assertTrue(facetedSearchPage.getResultByName(folderName).getActions().hasActionByName(actionName3));
        Assert.assertTrue(facetedSearchPage.getResultByName(folderName).getActions().hasActionByName(actionName4));        
        
        //Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Logout
        ShareUtil.logout(drone);       
       
        trace("searchAndVerifyFolderActionsTest complete");
    }
    
    /*This test is to verify content created under sub folder in user Home is displayed in search results page only to respective user
    Verify actions are displayed successfully for content created in sub folder under userHome*/
    // This test fails since Jira id ACE-1656 has been raised 
    
    @Test(groups = "Enterprise-only")
    public void ALF_3132() throws Exception
    {
        trace("Starting searchForContentInUserHomeFolderTest");        
        
        String actionName1 = "Download";
        String actionName2 = "View In Browser";
        String actionName3 = "Edit Offline";
        String actionName4 = "Delete Document";
        String actionName5 = "Manage Permissions";      
                
        // Login as user1
        userLogin1();

        String testName = getTestName();
        String fileName = testName+System.currentTimeMillis();

        //Open repository simple view
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        
        String[] folderPath = { USER_HOMES_FOLDER, testUser1 };
        
        //Navigate to UserHome folder in repository
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, folderPath);
        
        if (!repositoryPage.isFileVisible(testName))
        {
            //Create folder in user home folder
            ShareUserRepositoryPage.createFolderInRepository(drone, testName, testName, testName);
        }       

        // create a plain text file in test folder
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setTitle(testName + " title");
        contentDetails.setDescription(testName + " description");
        contentDetails.setContent(testName + " content");
        
        //Open Repository detailed view
        ShareUserRepositoryPage.openRepositoryDetailedView(drone);

        String[] contentFolderPath = { USER_HOMES_FOLDER, testUser1, testName };
        
        //Create content in folder path in User Home
        ShareUserRepositoryPage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, contentFolderPath);  
               
        //Navigate to faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
        
        // Do a search for filename
        doretrySearch(fileName);              
               
        //Check Actions are displayed on Facet results page for filename
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));        
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName2));    
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName3));
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName4));
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName5));
        
        // Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Logout
        ShareUser.logout(drone);
        
        //login as user2
        userLogin2();
        
        //Do a search for fileName
        doSearch(fileName);     
        
        // Check the results
        Assert.assertFalse(facetedSearchPage.getResults().size() > 0, "After searching for fileName there should not be search results");      
       
        trace("searchForContentInUserHomeFolderTest complete");
    }
    
    //This test is the verify only the same tenant user can view the file created by respective tenant
    
    @Test(groups = "CloudOnly")
    public void ALF_3133() throws Exception
    {
        trace("Starting searchAndVerifyMultiTenantTest");        
        
        String actionName1 = "Download";
        String actionName2 = "View In Browser";
        String actionName3 = "Edit Offline";
        String actionName4 = "Delete Document";
        String actionName5 = "Manage Permissions";
        
        String name = "b-fs-test1.txt";
        
        // Login as user1        
        userLogin1();

        // Do a search for name
        doretrySearch(name);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for name there should be some search results");      
               
        //Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName1));
        Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName2));
        Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName3));
        Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName4));
        Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName5));
        
        // Navigate back to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Logout
        ShareUtil.logout(drone);
        
        //login as user3
        userLogin3();
        
        //Do a search for the letter 'a'
        doretrySearch(name);     
        
        // Check the results
        Assert.assertFalse(facetedSearchPage.getResults().size() > 0, "After searching for name there should be no search results");    
           
        // Logout
        ShareUtil.logout(drone);
        
        trace("searchAndVerifyMultiTenantTest complete");
    }
   
    /* (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#tearDown()
     * 
     * Should not be cloud only.
     * 
     */
    @AfterClass(alwaysRun = true, groups = "alfresco-one")
    public void tearDown()
    {
        trace("Starting tearDown");

        // Login as test user        
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to the document library page and delete all content
        SiteUtil.openSiteURL(drone, getSiteShortname(this.siteName));
        ShareUser.openDocumentLibrary(drone);
        ShareUser.deleteAllContentFromDocumentLibrary(drone);

        // Logout
        ShareUser.logout(drone);

        super.tearDown();

        trace("TearDown complete");
    }

    /**
     * Do search.
     *
     * @param searchTerm the search term
     */
    private void doSearch(String searchTerm)
    {
        // Do a search for the searchTerm
        facetedSearchPage.getSearchForm().search(searchTerm); 
        facetedSearchPage.render();
       
    }
    
	private void doretrySearch(String searchTerm)
	{
		facetedSearchPage.getSearchForm().search(searchTerm);
		facetedSearchPage.render();
		if (!(facetedSearchPage.getResults().size() > 0)) 
		{
			webDriverWait(drone, refreshDuration);
			facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
			facetedSearchPage.getSearchForm().search(searchTerm);
			facetedSearchPage.render();
		}
	}       
    
    /**
     * Trace.
     *
     * @param msg the msg
     */
    private void trace(String msg)
    {
        if(logger.isTraceEnabled())
        {
            logger.trace(msg);
        }
    }

    /**
     * Login as user1.
     */
    private void userLogin1()
    {
        // Login as test user        
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
    }
    
    /**
     * Login as user2.
     */
    private void userLogin2()
    {
        // Login as test user        
        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        // Navigate to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
    }
    
    /**
     * Login as user3.
     */
    private void userLogin3()
    {
        // Login as test user        
        ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);

        // Navigate to the faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
    }
}