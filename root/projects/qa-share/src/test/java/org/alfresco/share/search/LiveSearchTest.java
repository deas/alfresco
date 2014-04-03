package org.alfresco.share.search;

import java.util.List;

import org.alfresco.po.share.search.LiveSearchDocumentResult;
import org.alfresco.po.share.search.LiveSearchDropdown;
import org.alfresco.po.share.search.LiveSearchPeopleResult;
import org.alfresco.po.share.search.LiveSearchSiteResult;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserLiveSearch;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageException;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Live search tests
 * @author jcule
 *
 */
@Listeners(FailedTestListener.class)
public class LiveSearchTest extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(LiveSearchTest.class);    
    
    private static String testPassword = DEFAULT_PASSWORD;
    protected String testUser;
    protected String siteName = "";
    
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
     * DataPreparation method - ACE_1063_01
     *   
     * 1) Login
     * 2) Create User
     * 3) Create Site
     * 4) Create and upload file with content
     * 
     * 
     * @throws Exception
     */
    
    @Test(groups={"DataPrepLiveSearch"})
    public void dataPrep_LiveSearch_ACE_1063_01() throws Exception
    {
        String testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);
        
        try
        {
            //Create user
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
 
            //Login as created user 
            ShareUser.login(drone, testUser, testPassword);
          
            //Create site
            SiteUtil.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
 
            //Uploading files with testName as document title.
            
            String[] fileInfo = { testName };
            ShareUser.uploadFileInFolder(drone, fileInfo);
            
            //Created user logs  out
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
     * 1) User logs in
     * 2) Performs live search with testName as a search term
     * 3) Checks that the created document is displayed in document search results 
     * 4) Checks that the created site is displayed in site search results
     * 5) Checks that the created user name is displayed in people search results
     * 6) Checks that all the links in live search results work properly
     * 7) User logs out
     */
    @Test
    public void liveSearch_ACE_1063_01()
    {

        //live search term is document title 
        testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        String siteName = getSiteName(testName);        
         
        ShareUser.login(drone, testUser, testPassword);
        
        LiveSearchDropdown liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, testName);
        List<LiveSearchDocumentResult> liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);

        //Checks document titles
        Assert.assertTrue(liveSearchDocumentResults.size() > 0);
        List<String> documentTitles = ShareUserLiveSearch.getLiveSearchDocumentTitles(liveSearchDocumentResults);
        Assert.assertTrue(documentTitles.contains(testName));

        //Checks document sites name
        List<String> documentSiteNames = ShareUserLiveSearch.getLiveSearchDocumentSiteNames(liveSearchDocumentResults);
        Assert.assertTrue(documentSiteNames.contains(siteName));
        
        //Checks document user names
        List<String> documentUserNames = ShareUserLiveSearch.getLiveSearchDocumentUserNames(liveSearchDocumentResults);
        Assert.assertTrue(documentUserNames.contains(testUser.toLowerCase()));      
        
        //Checks site result
        List<LiveSearchSiteResult> liveSearchSitesResults = ShareUserLiveSearch.getLiveSearchSitesResults(liveSearchDropdown);
        List<String> sitesNames = ShareUserLiveSearch.getLiveSearchSitesTitles(liveSearchSitesResults);
        Assert.assertTrue(sitesNames.contains(siteName));      
                
        //Checks people result
        List<LiveSearchPeopleResult> liveSearchPeopleResults = ShareUserLiveSearch.getLiveSearchPeopleResults(liveSearchDropdown);
        List<String> peopleNames = ShareUserLiveSearch.getLiveSearchUserNames(liveSearchPeopleResults);
        for(String peopleName : peopleNames){
            Assert.assertTrue(peopleName.indexOf(testUser) != -1);
        }

        //Clicks on document title
        DocumentDetailsPage documentDetailsPage = ShareUserLiveSearch.clickOnDocumentSearchResultTitle(liveSearchDropdown, testName).render();
        Assert.assertEquals(testName, documentDetailsPage.getDocumentTitle());
        
        //Clicks on document site name
        liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, testName);
        
        DocumentLibraryPage documentLibraryPage = ShareUserLiveSearch.clickOnDocumentSiteName(liveSearchDropdown, siteName).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(testName));
        
        //Clicks on document user name
        liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, testName);
        MyProfilePage myProfilePage = ShareUserLiveSearch.clickOnDocumentUserName(liveSearchDropdown, testUser).render();
        Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
        
        //Clicks on site name in sites results
        liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, testName);
        SiteDashboardPage siteDashboardPage = ShareUserLiveSearch.clickOnSiteResultSiteName(liveSearchDropdown, siteName).render();
        Assert.assertTrue(siteDashboardPage.isSiteTitle(siteName));
        
        //clicks on user name in people results
        liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, testName);
        myProfilePage = ShareUserLiveSearch.clickOnPeopleResultUserName(liveSearchDropdown, testUser).render();
        Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
        
        ShareUser.logout(drone);
        
    }
    
    
    /**
     * 
     * DataPreparation method - ACE_1063_02
     * 
     * 1) Login
     * 2) Create User
     * 3) Create Site
     * 4) Create and upload different types of files
     *  
     * @throws Exception
     */
    
    @Test(groups={"DataPrepLiveSearch"})
    public void dataPrep_LiveSearch_ACE_1063_02() throws Exception
    {
        String testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        String[] testUserInfo = new String[] { testUser };
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

        try
        {
            //Create user
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
 
            //Login as created user
            ShareUser.login(drone, testUser, testPassword);
          
            //Create site
            SiteUtil.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
 
            // UpLoad Files
            for (int index = 0; index <= fileTypes; index++)
            {
                String[] fileInfo = { fileName[index] };
                ShareUser.uploadFileInFolder(drone, fileInfo);
            }
            
            //Created user logs out
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
     * 
     * 1) User logs in
     * 2) Performs live search with searchTerm1 = testName + ".xml" as a search term
     * 3) Checks that the created document is displayed in document search results 
     * 4) Checks that there are no sites results
     * 5) Checks that there are no people results
     * 6) Clicks on the document title in document search results and checks that document details 
     * page is displayed
     * 7) Clicks on site name in document search results and checks that the site document library 
     * page is displayed
     * 8) Clicks on user name in document search results and checks that user profile 
     * page is displayed 
     * 9) Performs live search with searchTerm2 = testName as a search term
     * 10)Clicks on the More results... icon and verifies that all created documents are
     * displayed in document search results
     * 11) User logs out
     * 
     */
    @Test
    public void liveSearch_ACE_1063_02()
    {

        testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        String siteName = getSiteName(testName);        
           
        
        String searchTerm1 = testName + ".xml";
        String searchTerm2 = testName;
        
        ShareUser.login(drone, testUser, testPassword);
        
        LiveSearchDropdown liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, searchTerm1);
        List<LiveSearchDocumentResult> liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);

        //Checks document titles
        Assert.assertTrue(liveSearchDocumentResults.size() > 0);
        List<String> documentTitles = ShareUserLiveSearch.getLiveSearchDocumentTitles(liveSearchDocumentResults);
        Assert.assertTrue(documentTitles.contains(getFileName(testName + "." + "xml")));

        //Checks document sites name
        List<String> documentSiteNames = ShareUserLiveSearch.getLiveSearchDocumentSiteNames(liveSearchDocumentResults);
        Assert.assertTrue(documentSiteNames.contains(siteName));
        
        //Checks document user names
        List<String> documentUserNames = ShareUserLiveSearch.getLiveSearchDocumentUserNames(liveSearchDocumentResults);
        Assert.assertTrue(documentUserNames.contains(testUser.toLowerCase()));      
        
        //Checks site result
        List<LiveSearchSiteResult> liveSearchSitesResults = ShareUserLiveSearch.getLiveSearchSitesResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchSitesResults.size() == 0);      
                
        //Checks people result
        List<LiveSearchPeopleResult> liveSearchPeopleResults = ShareUserLiveSearch.getLiveSearchPeopleResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchPeopleResults.size() == 0);
        
        //Clicks on document title
        DocumentDetailsPage documentDetailsPage = ShareUserLiveSearch.clickOnDocumentSearchResultTitle(liveSearchDropdown, getFileName(testName + "." + "xml")).render();
        Assert.assertEquals(getFileName(testName + "." + "xml"), documentDetailsPage.getDocumentTitle());
         
        //Clicks on document site name
        liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, searchTerm1);
        DocumentLibraryPage documentLibraryPage = ShareUserLiveSearch.clickOnDocumentSiteName(liveSearchDropdown, siteName).render();
        Assert.assertTrue(documentLibraryPage.isFileVisible(getFileName(testName + "." + "xml")));
        
        //Clicks on document user name
        liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, searchTerm1);
        MyProfilePage myProfilePage = ShareUserLiveSearch.clickOnDocumentUserName(liveSearchDropdown, testUser).render();
        Assert.assertEquals(myProfilePage.getPageTitle(), "User Profile Page");
        
        //search for searchTerm2
        liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, searchTerm2);
        liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);
        
        liveSearchDropdown.clickToSeeMoreDocumentResults();
        liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);
        
        liveSearchDropdown.clickToSeeMoreDocumentResults();
        liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);
        
        liveSearchDropdown.clickToSeeMoreDocumentResults();
        liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);
        
        liveSearchDropdown.clickToSeeMoreDocumentResults();
        liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchDocumentResults.size() == 21); 
        
        ShareUser.logout(drone);
        
    }
    
    /**
     * DataPreparation method - ACE_1063_03
     *
     * 1) Login
     * 2) Create User
     * 3) Create Site
     * 4) Create and upload file with fileName = "}{+_)(&^%$#@!"
     * 
     * @throws Exception
     */
    
    @Test(groups={"DataPrepLiveSearch"})
    public void dataPrep_LiveSearch_ACE_1063_03() throws Exception
    {
        String testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        String siteName = getSiteName(testName);
        String fileName = "}{+_)(&^%$#@!";

        try
        {
            // User is created
            String[] testUserInfo = new String[] { testUser };
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

            //Created user logs in
            ShareUser.login(drone, testUser, testPassword);

            //Created user creates a site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            //and uploads created file to site's document library
            String[] fileInfo = { fileName };
            ShareUser.uploadFileInFolder(drone, fileInfo);
            
            //Created user logs out
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
     * 1) User logs in
     * 2) Performs live search with searchTerm1 = "!@#$%^&*()_+:\"|<>?;" as a search term
     * 3) Checks that there are no document results 
     * 4) Checks that there are no sites results
     * 5) Checks that there are no people results     
     * 6) Performs live search with searchTerm1 = "}{+_)(&^%$#@!" as a search term
     * 7) Checks that there are no document results 
     * 8) Checks that there are no sites results
     * 9) Checks that there are no people results     
     * 10)User logs out
     * 
     */
    @Test
    public void liveSearch_ACE_1063_03()
    {

        testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        
        String searchTerm1 = "!@#$%^&*()_+:\"|<>?;";
        String searchTerm2 = "}{+_)(&^%$#@!";
        
        ShareUser.login(drone, testUser, testPassword);
        
        //Check document results
        LiveSearchDropdown liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, searchTerm1);
        List<LiveSearchDocumentResult> liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchDocumentResults.size() == 0);
        
        //Checks site result
        List<LiveSearchSiteResult> liveSearchSitesResults = ShareUserLiveSearch.getLiveSearchSitesResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchSitesResults.size() == 0);      
                
        //Checks people result
        List<LiveSearchPeopleResult> liveSearchPeopleResults = ShareUserLiveSearch.getLiveSearchPeopleResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchPeopleResults.size() == 0);
        
        liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, searchTerm2);
        liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchDocumentResults.size() == 0);
        
        //Checks site result
        liveSearchSitesResults = ShareUserLiveSearch.getLiveSearchSitesResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchSitesResults.size() == 0);      
                
        //Checks people result
        liveSearchPeopleResults = ShareUserLiveSearch.getLiveSearchPeopleResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchPeopleResults.size() == 0);
         
        ShareUser.logout(drone);   
    }    
    
    /**
     * DataPreparation method - ACE_1063_04
     *
     * 1) Login
     * 2) Create User
     * 3) Create Site
     * 4) Log out
     * 
     * @throws Exception
     */
   
    @Test(groups={"DataPrepLiveSearch"})
    public void dataPrep_LiveSearch_ACE_1063_04() throws Exception
    {
        String testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        try
        {
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testPassword);

            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
            
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
     * 1) User logs in
     * 2) Performs live search with too long searchTerm
     * 3) Checks that there are no document results 
     * 4) Checks that there are no sites results
     * 5) Checks that there are no people results     
     * 6) User logs out    
     *  
     */
    @Test
    public void liveSearch_ACE_1063_04()
    {
        testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        String searchTerm = ShareUser.getRandomStringWithNumders(1030);
        
        ShareUser.login(drone, testUser, testPassword);
        
        //Check document results
        LiveSearchDropdown liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, searchTerm);
        List<LiveSearchDocumentResult> liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchDocumentResults.size() == 0);
        
        //Checks site result
        List<LiveSearchSiteResult> liveSearchSitesResults = ShareUserLiveSearch.getLiveSearchSitesResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchSitesResults.size() == 0);      
                
        //Checks people result
        List<LiveSearchPeopleResult> liveSearchPeopleResults = ShareUserLiveSearch.getLiveSearchPeopleResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchPeopleResults.size() == 0);
        
        ShareUser.logout(drone);
        
    }
    
    /**
     * DataPreparation method - ACE__1063_05
     * 
     * 1) Login
     * 2) Create User
     * 3) Create Site
     * 4) Create folders and files
     * 4) Log out    
     * 
     * @throws Exception
     */
    
    
    @Test(groups={"DataPrepLiveSearch"})
    public void dataPrep_LiveSearch_ACE_1063_05() throws Exception
    {
        String testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);
        
        String[] folders = { "H0us8 1", "H0us8 2", "H0us8 3" };
        String[] folderTitles_Descriptions = { "H0us8", "T3chn0" };
        String[] filesWithTitle = { "H0us8 my 11", "H0us8 my 21", "H0us8 my 31", "T3chn0 my" };

        try
        {
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

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
     * 1) User logs in
     * 2) Performs live search with search term "house my 3"
     * 3) Checks that document search results return document with title "House my 3" 
     * 4) Checks that document search results don't return document with title "Techno my" 
     * 5) Checks that there are no sites returned     
     * 6) User logs out        
     * 
     */
    @Test
    public void liveSearch_ACE_1063_05()
    {
        testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        String searchTerm = "H0us8 my 31";
        
        ShareUser.login(drone, testUser, testPassword);
        
        //Check document results
        LiveSearchDropdown liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, searchTerm);
        List<LiveSearchDocumentResult> liveSearchDocumentResults = ShareUserLiveSearch.getLiveSearchDocumentResults(liveSearchDropdown);
        List<String> documentTitles = ShareUserLiveSearch.getLiveSearchDocumentTitles(liveSearchDocumentResults);
        Assert.assertTrue(documentTitles.contains("H0us8 my 31"));
        Assert.assertFalse(documentTitles.contains("T3chn0 my"));
        Assert.assertFalse(liveSearchDropdown.isMoreResultsVisible());
        
        //Checks site result
        List<LiveSearchSiteResult> liveSearchSitesResults = ShareUserLiveSearch.getLiveSearchSitesResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchSitesResults.size() == 0);      
                
        //Checks people result
        List<LiveSearchPeopleResult> liveSearchPeopleResults = ShareUserLiveSearch.getLiveSearchPeopleResults(liveSearchDropdown);
        Assert.assertTrue(liveSearchPeopleResults.size() == 0);
        
        ShareUser.logout(drone);
    }   
    
    
    /**
     * DataPreparation method - ACE_1063_06
     *   
     * 1) Login
     * 2) Create User that is not system tenant i.e. not alfresco.com user
     * 3) Create Site
     * 4) Create and upload file with content
     * 
     * 
     * @throws Exception
     */
    
    @Test(groups={"DataPrepLiveSearch", "Cloud-only" })
    public void dataPrep_LiveSearch_ACE_1063_06() throws Exception
    {
        String testName = getTestName();
        String testUser = testName + "@" + DOMAIN_FREE;
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        try
        {
            //Create user
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
 
            //Login as created user 
            ShareUser.login(drone, testUser, testPassword);
          
            //Create site
            SiteUtil.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
 
            // Uploading file with testName as document title.
            String[] fileInfo = { testName };
            ShareUser.uploadFileInFolder(drone, fileInfo);
            
            //Created user logs  out
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
     * 1) User that is not system tenant logs into cloud
     * 2) Performs live search with testName as a search term
     * 3) Checks that the live search is not enabled for the user 
     * (it should be enabled only for the system tenants)
     */
    
    @Test(groups = { "Cloud-only" })
    public void liveSearch_ACE_1063_06()
    {

        //live search term is document title 
        testName = getTestName();
        String testUser = testName + "@" + DOMAIN_FREE;
        ShareUser.login(drone, testUser, testPassword);
        
        try
        {
            ShareUserLiveSearch.liveSearch(drone, testName);
            Assert.assertTrue(false, "Above line should have thrown page exception");
        }
        catch (PageException e)
        {
            Assert.assertEquals(e.getMessage(), "Live search not displayed.");
            ShareUser.logout(drone);
            
        }
    }
   
    /**
     * DataPreparation method - ACE_1063_07
     *   
     * 1) Login
     * 2) Create User that is not system tenant i.e. not alfresco.com user
     * 3) Create site with site name = "n3w s1t3 creat3ed 88"
     * 4) Create site with site name = "n3w s1t3 creat3ed 99"
     * 
     * @throws Exception
     */
    
    @Test(groups={"DataPrepLiveSearch" })
    public void dataPrep_LiveSearch_ACE_1063_07() throws Exception
    {
        String testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        String[] testUserInfo = new String[] { testUser };
        String siteName1 = "n3w s1t3 creat3ed 88";
        String siteName2 = "n3w s1t3 creat3ed 99";
        
        try
        {
            //Create user
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
 
            //Login as created user 
            ShareUser.login(drone, testUser, testPassword);
          
            //Create sites
            SiteUtil.createSite(drone, siteName1, AbstractUtils.SITE_VISIBILITY_PUBLIC);
            SiteUtil.createSite(drone, siteName2, AbstractUtils.SITE_VISIBILITY_PUBLIC);
            
            //Created user logs  out
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
     * 1) User logs in
     * 2) Performs live search with searchterm1 = siteName1 = "n3w s1t3 88"
     * 4) Checks that the site with name "n3w s1t3 creat3ed 88" is returned in live search sites results
     * 5) Checks that the site with name "n3w s1t3 creat3ed 99" is not returned in live search sites results
     */
    
    @Test
    public void liveSearch_ACE_1063_07()
    {

        //live search term is document title 
        testName = getTestName();
        String testUser = testName + "@" + "alfresco.com";
        ShareUser.login(drone, testUser, testPassword);
        
        String searchTerm1 = "n3w s1t3 88";
        
        //Checks document sites name
        LiveSearchDropdown liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, searchTerm1);
        List<LiveSearchSiteResult> liveSearchSitesResults = ShareUserLiveSearch.getLiveSearchSitesResults(liveSearchDropdown);
        List<String> sitesNames = ShareUserLiveSearch.getLiveSearchSitesTitles(liveSearchSitesResults);
        Assert.assertFalse(sitesNames.contains("n3w s1t3 creat3ed 99"));
        
        //Query issue to fix? !!!!!!
        //Assert.assertTrue(sitesNames.contains("n3w s1t3 creat3ed 88"));  
               
    }
    
    /**
     * DataPreparation method - ACE_1063_08
     *   
     * 1) Login
     * 2) Create user that is not system tenant i.e. not alfresco.com user with user name = "n3w us3r creat3ed 77"
     * 3) Create user that is not system tenant i.e. not alfresco.com user with user name = "n3w us3r creat3ed 55"
     * 4) Create site with site name = "n3w s1t3 creat3ed 99"
     * 
     * @throws Exception
     */
    
    @Test(groups={"DataPrepLiveSearch" })
    public void dataPrep_LiveSearch_ACE_1063_08() throws Exception
    {
        String testName = getTestName();
        String testUser1 = "n3w us3r creat3ed 77" + "@" + "alfresco.com";
        String[] testUserInfo1 = new String[] { testUser1 };
        
        String testUser2 = "n3w us3r creat3ed 55" + "@" + "alfresco.com";
        String[] testUserInfo2 = new String[] { testUser2 };
 
        
        try
        {
            //Create users
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo1);
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo2);
            
            //user logs  out
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
     * 1) User logs in
     * 2) Performs live search with searchterm1 = siteName1 = "n3w s1t3 88"
     * 4) Checks that the site with name "n3w s1t3 creat3ed 88" is returned in live search sites results
     * 5) Checks that the site with name "n3w s1t3 creat3ed 99" is not returned in live search sites results
     */
    
    @Test
    public void liveSearch_ACE_1063_08()
    {
 
        testName = getTestName();
        String testUser1 = "n3w us3r creat3ed 77" + "@" + "alfresco.com";
        String testUser2 = "n3w us3r creat3ed 55" + "@" + "alfresco.com";
        ShareUser.login(drone, testUser1, testPassword);
        
        String searchTerm = "n3w us3r 77";
        
        LiveSearchDropdown liveSearchDropdown = ShareUserLiveSearch.liveSearch(drone, searchTerm);
        List<LiveSearchPeopleResult> liveSearchPeopleResults = ShareUserLiveSearch.getLiveSearchPeopleResults(liveSearchDropdown);
        List<String> peopleNames = ShareUserLiveSearch.getLiveSearchUserNames(liveSearchPeopleResults);
        for(String peopleName : peopleNames){
            Assert.assertTrue(peopleName.indexOf(testUser1) != -1);
            Assert.assertTrue(peopleName.indexOf(testUser2) == -1);
        }
 
    }
    
}
