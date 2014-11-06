package org.alfresco.share.search;

import static org.alfresco.po.share.site.document.ContentType.PLAINTEXT;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.search.CopyAndMoveContentFromSearchPage;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.OpCloudTestContext;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
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
 * The Class FacetedSearchPageTest2.
 *
 * This tests the Faceted Search page behaves as one would expect.
 *
 * @author charu
 */

public class FacetedSearchPageTest2 extends AbstractUtils
{

    /** The logger. */
    private static Log logger = LogFactory.getLog(FacetedSearchPageTest.class);

    /** Constants */  
       
    private OpCloudTestContext testContext;
    private DashBoardPage dashBoardPage;
    private FacetedSearchPage facetedSearchPage;    
    
    private String siteName;
    private String siteName1;
    private String testUser1;
    private String testUser2;
    private String testUser3;    
    protected String parentFolderPath;    
    
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
        
        this.siteName = "asite1"+System.currentTimeMillis();
        this.siteName1 = "asite2"+System.currentTimeMillis();   
               
        parentFolderPath = REPO + SLASH + "Shared";

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);
        
        // Login as user
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Create site
        ShareUser.createSite(drone, this.siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, this.siteName1, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUser.logout(drone);

        trace("Setup complete");
    }
    
    /*
     * selectCopyFoldertoFolderTest
     * This test is to verify 'Copy to' action is displayed under actions for uploaded file for same user
     * Click on 'Copy to' link under actions by same user
     * Verify url is not changed after clicking on Copy to link in search results page
     * Verify copied folder is displayed successfully in the destination folder
     * Logout and user1 and login as user2
     * Verify 'copy to' action link is visible for user2
     */

    @Test(groups = "alfresco-one")
    public void ALF_1() throws Exception
    {
        trace("Starting selectCopyFoldertoFolderTest");

        String actionName1 = "Copy to...";
        String folderName1 = "folder1" + System.currentTimeMillis();
        String folderDesc1 = "folder desc1";
        String folderName2 = "folder2" + System.currentTimeMillis();
        String folderDesc2 = "folder desc2";

        // Login as user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        // Create folder1 under siteName
        ShareUserSitePage.createFolder(drone, folderName1, folderDesc1);

        // open siteName1 document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);

        // Create folder2 under siteName1
        ShareUserSitePage.createFolder(drone, folderName2, folderDesc2);

        // Open my dashboard page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for folderName1
        doretrySearch(folderName1);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Get the current url
        String url = drone.getCurrentUrl();

        // Check Actions are displayed on Facet results page for folderName1
        Assert.assertTrue(facetedSearchPage.getResultByName(folderName1).getActions().hasActionByName(actionName1));

        // Click the 'Copy to' action
        facetedSearchPage.getResultByName(folderName1).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // Verify the url is not changed
        Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");

        // Verify Copy to dialog is opened successfully
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Copy"));

        // Select the destination folderName2
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();
        copyAndMoveContentFromSearchPage.selectFolderInRepo("Sites");
        copyAndMoveContentFromSearchPage.selectFolder(this.siteName1, "documentLibrary", folderName2);

        // Select Copy button
        copyAndMoveContentFromSearchPage.selectCopyButton().render();

        // Navigate to dashboard page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        // Verify folder1 is still present in siteName
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName1), "folder1 is visible");

        // Navigate to destination folderName2 in siteName1 document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);
        ShareUserSitePage.navigateToFolder(drone, folderName2);

        // Verify folder1 is copied successfully into folder2
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName1), "folder1 is visible");

        // Logout
        ShareUtil.logout(drone);

        // login as user2
        userLogin2();

        // Do a search for folderName1
        doSearch(folderName1);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        facetedSearchPage.render();
        // Verify copy to action is displayed for different user
        Assert.assertTrue(facetedSearchPage.getResultByName(folderName1).getActions().hasActionByName(actionName1));

        trace("selectCopyFoldertoFolderTest complete");
    }

    /*
     * selectCopyFiletoFolderCancelTest
     * This test is to verify 'Copy to' action is displayed under actions for uploaded file for same user
     * Click on 'Copy to' link under actions by same user
     * Verify url is not changed after clicking on Copy to link in search results page
     * select destination folder and select 'cancel' button in 'copy to' dialog
     * Verify 'copy to' folder in site document library is cancelled
     * Logout and user1 and login as user2
     * Verify 'copy to' action link is visible for user2
     */

    @Test(groups = "alfresco-one")
    public void ALF_2() throws Exception
    {
        trace("Starting selectCopyFiletoFolderCancelTest");

        String actionName1 = "Copy to...";
        String folderName1 = "folder1" + System.currentTimeMillis();
        String folderDesc1 = "folder desc1";
        String fileName = "file1" + System.currentTimeMillis();

        // Login as user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        ContentDetails contentDetails1 = new ContentDetails(fileName, fileName, fileName, fileName);
        ShareUser.createContent(drone, contentDetails1, PLAINTEXT);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);

        // Create folder1 under siteName
        ShareUserSitePage.createFolder(drone, folderName1, folderDesc1);

        // Open my dash board page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for fileName
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Get the current url
        String url = drone.getCurrentUrl();

        // Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        // Click the 'Copy to' action
        facetedSearchPage.getResultByName(fileName).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // Verify the url is not changed
        Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");

        // Verify Copy to dialog is opened successfully
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Copy"));

        // Select the destination folderName1
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();
        copyAndMoveContentFromSearchPage.selectFolderInRepo("Sites");
        copyAndMoveContentFromSearchPage.selectFolder(this.siteName1, "documentLibrary", folderName1);

        // Select 'Cancel' button
        copyAndMoveContentFromSearchPage.selectCancelButton().render();
        facetedSearchPage.render();

        // Navigate to dash board page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify file is displayed in siteName
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, fileName), "file is visible");

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUserSitePage.navigateToFolder(drone, folderName1);

        // Verify file is not copied into folder1
        Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, fileName), "file is visible");

        // Logout
        ShareUtil.logout(drone);

        // login as user2
        userLogin2();

        // Do a search for fileName
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        facetedSearchPage.render();

        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        trace("selectCopyFiletoFolderCancelTest complete");
    }

    /*
     * selectCopyFiletoSiteFolderTest
     * This test is to verify 'Copy to' action is displayed under actions for uploaded file for same user
     * Click on 'Copy to' link under actions by same user
     * Verify url is not changed after clicking on Copy to link in search results page
     * select destination folder and select 'ok' button in 'copy to' dialog
     * Verify 'copy' file to folder in site document library is done successfully
     * Logout and user1 and login as user2
     * Verify 'copy to' action link is not visible for user2
     */

    @Test(groups = "alfresco-one")
    public void ALF_3() throws Exception
    {
        trace("Starting selectCopyFiletoSiteFolderTest");

        String actionName1 = "Copy to...";
        String folderName1 = "folder1" + System.currentTimeMillis();
        String folderDesc1 = "folder desc1";
        String fileName = "file1" + System.currentTimeMillis();

        // Login as user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        ContentDetails contentDetails1 = new ContentDetails(fileName, fileName, fileName, fileName);
        ShareUser.createContent(drone, contentDetails1, PLAINTEXT);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);

        // Create folder1 under siteName
        ShareUserSitePage.createFolder(drone, folderName1, folderDesc1);

        // Open my dash board page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for fileName
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Get the current url
        String url = drone.getCurrentUrl();

        // Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        // Click the 'Copy to' action
        facetedSearchPage.getResultByName(fileName).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // Verify the url is not changed
        Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");

        // Verify Copy to dialog is opened successfully
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Copy"));

        // Select the destination folderName1
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();
        copyAndMoveContentFromSearchPage.selectFolderInRepo("Sites");
        copyAndMoveContentFromSearchPage.selectFolder(this.siteName1, "documentLibrary", folderName1);

        // Select 'Cancel' button
        copyAndMoveContentFromSearchPage.selectCopyButton().render();

        // Navigate to dash board page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify file is displayed in siteName
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, fileName), "file is visible");

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUserSitePage.navigateToFolder(drone, folderName1);

        // Verify file is not copied into folder1
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, fileName), "file is visible");

        // Logout
        ShareUtil.logout(drone);

        // login as user2
        userLogin2();

        // Do a search for fileName
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        facetedSearchPage.render();

        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        trace("selectCopyFiletoSiteFolderTest complete");
    }
     
    /*
     * selectCopyFiletoRepoTest
     * This test is to verify 'Copy to' action is displayed under actions for uploaded file for same user
     * Click on 'Copy to' link under actions by same user
     * Verify url is not changed after clicking on Copy to link in search results page
     * Verify copied folder is displayed successfully in the destination folder
     * Logout and user1 and login as user2
     * Verify 'copy to' action link is not visible for user2
     */

    @Test(groups = "EnterpriseOnly")
    public void ALF_4() throws Exception
    {
        trace("Starting selectCopyFiletoRepoTest");

        String actionName1 = "Copy to...";
        String fileName = "file1" + System.currentTimeMillis();
        String folderName1 = "folder1" + System.currentTimeMillis();
        String folderDesc1 = "folder desc1";
        String SHARED_FOLDER_PATH = REPO + SLASH + "Shared";

        // Login as user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        ContentDetails contentDetails1 = new ContentDetails(fileName, fileName, fileName, fileName);
        ShareUser.createContent(drone, contentDetails1, PLAINTEXT);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);

        // Create folder1 under siteName
        ShareUserSitePage.createFolder(drone, folderName1, folderDesc1);

        // Open my dash board page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for name
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Get the current url
        String url = drone.getCurrentUrl();

        // Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        // Click the first action
        facetedSearchPage.getResultByName(fileName).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // Verify the url is not changed
        Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");

        // Verify Copy to dialog is opened successfully
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Copy"));

        // Select the destination folder
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();
        copyAndMoveContentFromSearchPage.selectFolder("Shared");
        copyAndMoveContentFromSearchPage.selectCopyButton().render();

        // Do a search for name
        doretrySearch(folderName1);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Get the current url
        String url1 = drone.getCurrentUrl();

        // Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(folderName1).getActions().hasActionByName(actionName1));

        // Click the first action
        facetedSearchPage.getResultByName(folderName1).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl1 = drone.getCurrentUrl();

        // Verify the url is not changed
        Assert.assertEquals(url1, newUrl1, "After clicking on action, the url should not have changed");

        // Verify Copy to dialog is opened successfully
        CopyAndMoveContentFromSearchPage copyandMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyandMoveContentFromSearchPage.getDialogTitle().contains("Copy"));

        // Select the destination folder
        copyandMoveContentFromSearchPage.selectDestination("Repository").render();

        copyAndMoveContentFromSearchPage.selectFolder("Shared");

        copyandMoveContentFromSearchPage.selectCopyButton().render();

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify folder2 is copied successfully into folder1
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, fileName), "file is visible");

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        // Verify folder2 is copied successfully into folder1
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName1), "file is visible");

        // Navigate to Repository
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, SHARED_FOLDER_PATH);

        // Verify file is copied successfully into folder1
        Assert.assertTrue(repositoryPage.isFileVisible(fileName), "file dispalyed successfully");

        // Verify file is copied successfully into folder1
        Assert.assertTrue(repositoryPage.isFileVisible(folderName1), "file dispalyed successfully");

        // Logout
        ShareUtil.logout(drone);

        // login as user2
        userLogin2();

        // Do a search for the letter 'a'
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        trace("selectCopyFiletoRepoTest complete");
    }

    /*
     * selectMoveFoldertoFolderTest
     * This test is to verify 'Move to' action is displayed under actions for uploaded file for same user
     * Click on 'Move to' link under actions by same user
     * Verify url is not changed after clicking on Move to link in search results page
     * Verify Moved folder is displayed successfully in the destination folder
     * Logout and user1 and login as user2
     * Verify 'Move to' action link is not visible for user2
     */

    @Test(groups = "alfresco-one")
    public void ALF_5() throws Exception
    {
        trace("Starting selectMoveFoldertoFolderTest");

        String actionName1 = "Move to...";
        String folderName1 = "folder1" + System.currentTimeMillis();
        String folderDesc1 = "folder desc1";
        String folderName2 = "folder2" + System.currentTimeMillis();
        String folderDesc2 = "folder desc2";

        // Login as user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        // Create folder1 under siteName
        ShareUserSitePage.createFolder(drone, folderName1, folderDesc1);

        // open siteName1 document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);

        // Create folder2 under siteName1
        ShareUserSitePage.createFolder(drone, folderName2, folderDesc2);

        // Open my dashboard page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for folderName1
        doretrySearch(folderName1);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Get the current url
        String url = drone.getCurrentUrl();

        // Check Actions are displayed on Facet results page for folderName1
        Assert.assertTrue(facetedSearchPage.getResultByName(folderName1).getActions().hasActionByName(actionName1));

        // Click the ' to' action
        facetedSearchPage.getResultByName(folderName1).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // Verify the url is not changed
        Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");

        // Verify Move to dialog is opened successfully
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Move"));

        // Select the destination folderName2
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();
        copyAndMoveContentFromSearchPage.selectFolderInRepo("Sites");
        copyAndMoveContentFromSearchPage.selectFolder(this.siteName1, "documentLibrary", folderName2);

        // Select Copy button
        copyAndMoveContentFromSearchPage.selectMoveButton().render();

        // Navigate to dashboard page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        // Verify folder1 is not present in siteName
        Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, folderName1), "folder1 is not visible");

        // Navigate to destination folderName2 in siteName1 document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);
        ShareUserSitePage.navigateToFolder(drone, folderName2);

        // Verify folder1 is copied successfully into folder2
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName1), "folder1 is visible");

        // Logout
        ShareUtil.logout(drone);

        // login as user2
        userLogin2();

        // Do a search for folderName1
        doSearch(folderName1);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Verify copy to action is displayed for different user
        Assert.assertFalse(facetedSearchPage.getResultByName(folderName1).getActions().hasActionByName(actionName1));

        trace("selectMoveFoldertoFolderTest complete");
    }

    /*
     * selectMoveFiletoFolderCancelTest
     * This test is to verify 'Move to' action is displayed under actions for uploaded file for same user
     * Click on 'Move to' link under actions by same user
     * Verify url is not changed after clicking on Move to link in search results page
     * select destination folder and select 'cancel' button in 'Move to' dialog
     * Verify 'Move to' folder in site document library is cancelled
     * Logout and user1 and login as user2
     * Verify 'Move to' action link is visible for user2
     */

    @Test(groups = "alfresco-one")
    public void ALF_6() throws Exception
    {
        trace("Starting selectMoveFiletoFolderCancelTest");

        String actionName1 = "Move to...";
        String folderName1 = "folder1" + System.currentTimeMillis();
        String folderDesc1 = "folder desc1";
        String fileName = "file1" + System.currentTimeMillis();

        // Login as user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        ContentDetails contentDetails1 = new ContentDetails(fileName, fileName, fileName, fileName);
        ShareUser.createContent(drone, contentDetails1, PLAINTEXT);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);

        // Create folder1 under siteName
        ShareUserSitePage.createFolder(drone, folderName1, folderDesc1);

        // Open my dash board page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for fileName
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Get the current url
        String url = drone.getCurrentUrl();

        // Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        // Click the 'Move to' action
        facetedSearchPage.getResultByName(fileName).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // Verify the url is not changed
        Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");

        // Verify Move to dialog is opened successfully
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Move"));

        // Select the destination folderName1
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();
        copyAndMoveContentFromSearchPage.selectFolderInRepo("Sites");
        copyAndMoveContentFromSearchPage.selectFolder(this.siteName1, "documentLibrary", folderName1);

        // Select 'Cancel' button
        copyAndMoveContentFromSearchPage.selectCancelButton().render();

        // Navigate to dash board page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify file is displayed in siteName
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, fileName), "file is visible");

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUserSitePage.navigateToFolder(drone, folderName1);

        // Verify file is not copied into folder1
        Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, fileName), "file is visible");

        // Logout
        ShareUtil.logout(drone);

        // login as user2
        userLogin2();

        // Do a search for fileName
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        facetedSearchPage.render();

        Assert.assertFalse(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        trace("selectMoveFiletoFolderCancelTest complete");
    }

    /*
     * selectMoveFiletoSiteFolderTest
     * This test is to verify 'Move to' action is displayed under actions for uploaded file for same user
     * Click on 'Move to' link under actions by same user
     * Verify url is not changed after clicking on Move to link in search results page
     * select destination folder and select 'ok' button in 'Move to' dialog
     * Verify 'Move' file to folder in site document library is done successfully
     * Logout and user1 and login as user2
     * Verify 'Move to' action link is not visible for user2
     */

    @Test(groups = "alfresco-one")
    public void ALF_7() throws Exception
    {
        trace("Starting selectMoveFiletoSiteFolderTest");

        String actionName1 = "Move to...";
        String folderName1 = "folder1" + System.currentTimeMillis();
        String folderDesc1 = "folder desc1";
        String fileName = "file1" + System.currentTimeMillis();

        // Login as user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        ContentDetails contentDetails1 = new ContentDetails(fileName, fileName, fileName, fileName);
        ShareUser.createContent(drone, contentDetails1, PLAINTEXT);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);

        // Create folder1 under siteName
        ShareUserSitePage.createFolder(drone, folderName1, folderDesc1);

        // Open my dash board page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for fileName
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Get the current url
        String url = drone.getCurrentUrl();

        // Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        // Click the 'Move to' action
        facetedSearchPage.getResultByName(fileName).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // Verify the url is not changed
        Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");

        // Verify Move to dialog is opened successfully
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Move"));

        // Select the destination folderName1
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();
        copyAndMoveContentFromSearchPage.selectFolderInRepo("Sites");
        copyAndMoveContentFromSearchPage.selectFolder(this.siteName1, "documentLibrary", folderName1);

        // Select 'Cancel' button
        copyAndMoveContentFromSearchPage.selectMoveButton().render();

        // Navigate to dash board page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify file is not displayed in siteName
        Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, fileName), "file is visible");

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUserSitePage.navigateToFolder(drone, folderName1);

        // Verify file is not copied into folder1
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, fileName), "file is visible");

        // Logout
        ShareUtil.logout(drone);

        // login as user2
        userLogin2();

        // Do a search for fileName
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        facetedSearchPage.render();

        Assert.assertFalse(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        trace("selectMoveFiletoSiteFolderTest complete");
    }

    /*
     * selectMoveFiletoRepoTest
     * This test is to verify 'Move to' action is displayed under actions for uploaded file for same user
     * Click on 'Move to' link under actions by same user
     * Verify url is not changed after clicking on Move to link in search results page
     * Verify copied folder is displayed successfully in the destination folder
     * Logout and user1 and login as user2
     * Verify 'Move to' action link is not visible for user2
     */

    @Test(groups = "EnterpriseOnly")
    public void ALF_8() throws Exception
    {
        trace("Starting selectMoveFileAndFoldertoRepoTest");

        String actionName1 = "Move to...";
        String fileName = "file1" + System.currentTimeMillis();
        String folderName1 = "folder1" + System.currentTimeMillis();
        String folderDesc1 = "folder desc1";
        String SHARED_FOLDER_PATH = REPO + SLASH + "Shared";

        // Login as user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        ContentDetails contentDetails1 = new ContentDetails(fileName, fileName, fileName, fileName);
        ShareUser.createContent(drone, contentDetails1, PLAINTEXT);

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);

        // Create folder1 under siteName
        ShareUserSitePage.createFolder(drone, folderName1, folderDesc1);

        // Open my dash board page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for name
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Get the current url
        String url = drone.getCurrentUrl();

        // Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        // Click the first action
        facetedSearchPage.getResultByName(fileName).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        // Verify the url is not changed
        Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");

        // Verify Move to dialog is opened successfully
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Move"));

        // Select the destination folder
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();
        copyAndMoveContentFromSearchPage.selectFolder("Shared");

        copyAndMoveContentFromSearchPage.selectMoveButton().render();

        // Do a search for name
        doretrySearch(folderName1);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Get the current url
        String url1 = drone.getCurrentUrl();

        // Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(folderName1).getActions().hasActionByName(actionName1));

        // Click the first action
        facetedSearchPage.getResultByName(folderName1).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl1 = drone.getCurrentUrl();

        // Verify the url is not changed
        Assert.assertEquals(url1, newUrl1, "After clicking on action, the url should not have changed");

        // Verify Move to dialog is opened successfully
        CopyAndMoveContentFromSearchPage copyandMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyandMoveContentFromSearchPage.getDialogTitle().contains("Move"));

        // Select the destination folder
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();
        copyAndMoveContentFromSearchPage.selectFolder("Shared");

        copyandMoveContentFromSearchPage.selectMoveButton().render();

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify fileName is is not displayed in siteName
        Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, fileName), "file is visible");

        // Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName1);

        // Verify folder2 is not displayed in sitename1
        Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, folderName1), "file is visible");

        // Navigate to Repository
        ShareUserRepositoryPage.openRepositorySimpleView(drone);
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone, SHARED_FOLDER_PATH);

        // Verify file is moved successfully into Shared folder
        Assert.assertTrue(repositoryPage.isFileVisible(fileName), "file dispalyed successfully");

        // Verify file is moved successfully into Shared folder
        Assert.assertTrue(repositoryPage.isFileVisible(folderName1), "file dispalyed successfully");

        // Logout
        ShareUtil.logout(drone);

        // login as user2
        userLogin2();

        // Do a search for fileName
        doretrySearch(fileName);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        facetedSearchPage.render();

        Assert.assertFalse(facetedSearchPage.getResultByName(fileName).getActions().hasActionByName(actionName1));

        trace("selectMoveFileAndFoldertoRepoTest complete");
    }

    /*
     * validateNextAndBackButtonTest
     * This test is to Validate Next and Back button in copy and move dialog page *
     */

    @Test(groups = "alfresco-one")
    public void ALF_9() throws Exception
    {
        trace("Starting validateNextAndBackButtonTest");

        String actionName1 = "Copy to...";
        String folderName1 = "folder1" + System.currentTimeMillis();
        String folderDesc1 = "folder desc1";

        // Login as user1
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Create site
        for (int site = 1; site <= 30; site++)
        {
            ShareUser.createSite(drone, this.siteName + site, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        }

        // open siteName document library
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);

        // Create folder1 under siteName
        ShareUserSitePage.createFolder(drone, folderName1, folderDesc1);

        // Open my dashboard page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);

        // Navigate to faceted search page
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();

        // Do a search for folderName1
        doretrySearch(folderName1);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

        // Click the 'Copy to' action
        facetedSearchPage.getResultByName(folderName1).getActions().clickActionByName(actionName1);

        // Select Copy button
        CopyAndMoveContentFromSearchPage copyAndMoveContentFromSearchPage = drone.getCurrentPage().render();
        Assert.assertTrue(copyAndMoveContentFromSearchPage.getDialogTitle().contains("Copy"));

        // Select the destination folder
        copyAndMoveContentFromSearchPage.selectDestination("Repository").render();
        copyAndMoveContentFromSearchPage.selectFolderInRepo("Sites").render();
        Assert.assertTrue(copyAndMoveContentFromSearchPage.isNextButtonEnabled(), "Next Button is enabled");

        copyAndMoveContentFromSearchPage.selectNextButton();

        Assert.assertFalse(copyAndMoveContentFromSearchPage.isNextButtonEnabled(), "Next Button is disabled");
        Assert.assertTrue(copyAndMoveContentFromSearchPage.isBackButtonEnabled(), "Back Button is disabled");
        copyAndMoveContentFromSearchPage.selectBackButton();
        Assert.assertFalse(copyAndMoveContentFromSearchPage.isBackButtonEnabled(), "Back Button is disabled");
        Assert.assertTrue(copyAndMoveContentFromSearchPage.isNextButtonEnabled(), "Next Button is disabled");

        copyAndMoveContentFromSearchPage.clickClose().render();

        trace("validateNextAndBackButtonTest complete");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#tearDown()
     * Should not be cloud only.
     */
    @AfterClass(alwaysRun = true, groups = "alfresco-one")
    public void tearDown()
    {
        trace("Starting tearDown");

        // Login as test user
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Navigate to the document library page and delete all content
        SiteUtil.openSiteURL(drone, getSiteShortname(this.siteName));
        // ShareUser.openUserDashboard(drone);
        SiteUtil.deleteSite(drone, siteName);
        // SiteUtil.deleteSite(drone, siteName1);
        /*
         * ShareUser.openDocumentLibrary(drone);
         * ShareUser.deleteAllContentFromDocumentLibrary(drone);
         * SiteUtil.openSiteURL(drone, getSiteShortname(this.siteName1));
         * ShareUser.openDocumentLibrary(drone);
         * ShareUser.deleteAllContentFromDocumentLibrary(drone);
         */
        // Logout
        // Create site
        ShareUser.openUserDashboard(drone);
        for (int site = 1; site <= 30; site++)
        {
            SiteUtil.deleteSite(drone, this.siteName + site);
        }
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

        // Reload the page objects
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
            facetedSearchPage.getSearchForm().search(searchTerm).render();
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
        if (logger.isTraceEnabled())
        {
            logger.trace(msg);
        }
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

}