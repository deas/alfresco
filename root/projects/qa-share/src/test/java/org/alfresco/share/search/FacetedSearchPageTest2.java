package org.alfresco.share.search;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
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
 * This tests the Faceted Search results copy and move actions
 * These tests are non -executable since still in development
 *
 * @author charu
 */

public class FacetedSearchPageTest2 extends AbstractUtils
{

    /** The logger. */
    private static Log logger = LogFactory.getLog(FacetedSearchPageTest.class);

    /** Constants */
    
    private static final String fileDir = "faceted-search-files\\";
    private static final String fileStem = "-fs-test2.docx";
    
    private OpCloudTestContext testContext;
    private DashBoardPage dashBoardPage;
    private FacetedSearchPage facetedSearchPage;    
    
    private String siteName;
    private String siteName1;
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
        this.siteName1 = getSiteName(testName+"1");
        
        String folderName1 = "folder1";
        String folderDesc1 = "folder desc1";
        String folderName2 = "folder2";
        String folderDesc2 = "folder desc2";
        String folderName3 = "folder3";       
        String folderTitle3 = "folder Title3";
        String folderDesc3 = "folder desc3";

        // Create user
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);
        
        // Login as user
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Create site
        ShareUser.createSite(drone, this.siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, this.siteName1, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Upload 5 Files
        for (int i=0; i < 5; i++)
        {
            String[] fileInfo = { fileDir + (char)(i+97) + fileStem };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
        
        ShareUser.openSitesDocumentLibrary(drone, this.siteName);
        ShareUserSitePage.createFolder(drone, folderName1, folderDesc1);
        
        ShareUser.openSitesDocumentLibrary(drone, this.siteName1);
        ShareUserSitePage.createFolder(drone, folderName2, folderDesc2);

        // Navigate to the faceted search page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);
        facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage().render();
        
        ShareUserRepositoryPage.navigateFoldersInRepositoryPage(drone, "Shared");
        ShareUserRepositoryPage.createFolderInFolderInRepository(drone, folderName3, folderDesc3,"Shared");        
        
        // Logout        
        ShareUtil.logout(drone);

        trace("Setup complete");
    }
    
    /*selectCopyFoldertoFolderTest
    *This test is to verify 'Copy to' action is displayed under actions for uploaded file for same user
    *Click on 'Copy to' link under actions by same user
    *Verify url is not changed after clicking on Copy to link in search results page
    *Verify copied folder is displayed successfully in the destination folder
    *Logout and user1 and login as user2
    *Verify 'copy to' action link is not visible for user2  
    */
    
    @Test(groups = "alfresco-one")
    public void ALF_1() throws Exception
    {
        trace("Starting selectCopyFoldertoFolderTest");        
        
        String actionName1 = "Copy to";
        String folderName1 = "folder1";
        String folderName2 = "folder2";      
       
        // Login as user1
        userLogin1();

        // Do a search for the letter 'a'
        doSearch(folderName1);

        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");        
        
        // Get the current url
        String url = drone.getCurrentUrl();
        
        //Check Actions are displayed on Facet results page for folder
        Assert.assertTrue(facetedSearchPage.getResultByName(folderName1).getActions().hasActionByName(actionName1));        
        
        // Click the first action        
        facetedSearchPage.getResultByName(folderName1).getActions().clickActionByName(actionName1);

        // Get the url again
        String newUrl = drone.getCurrentUrl();

        //Verify the url is not changed
        Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");
        
        //Verify Copy to dialog is opened successfully
        CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage().render();        
        Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains("Copy"));
        //copyOrMoveContentPage.selectDestination(destinationName).render();
        //copyOrMoveContentPage.selectSite(this.siteName).render();
        
        //Select the destination folder
        copyOrMoveContentPage.selectPath("Recent Sites",this.siteName1,folderName2).render();
        copyOrMoveContentPage.selectOkButton();
        
        //Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.navigateToFolder(drone, folderName1);
        
        //Verify folder2 is copied successfully into folder1
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName1), "folder1 is visible");
        
        //Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUserSitePage.navigateToFolder(drone, folderName2);
        
        //Verify folder2 is copied successfully into folder1
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName1), "folder1 is visible");     
        
        // Logout
        ShareUtil.logout(drone);
        
        //login as user2
        userLogin2();
        
        //Do a search for the letter 'a'
        doSearch(folderName1);     
        
        // Check the results
        Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");
        
        Assert.assertFalse(facetedSearchPage.getResultByName(folderName1).getActions().hasActionByName(actionName1));        

        trace("selectCopyFoldertoFolderTest complete");
    }
    
     /*selectCopyFiletoFolderCancelTest
     *This test is to verify 'Copy to' action is displayed under actions for uploaded file for same user
     *Click on 'Copy to' link under actions by same user
     *Verify url is not changed after clicking on Copy to link in search results page
     *select destination folder and select 'cancel' button in 'copy to' dialog
     *Verify 'copy to' folder in site document library is cancelled
     *Logout and user1 and login as user2
     *Verify 'copy to' action link is not visible for user2  
     */
     
     @Test(groups = "alfresco-one")
     public void ALF_2() throws Exception
     {
         trace("Starting selectCopyFiletoFolderCancelTest");        
         
         String actionName1 = "Copy to";
         String name = ("a-fs-test1.docx");
         String folderName2 = "folder2";
         
         //String destinationName = "Recent Sites";
         
         // Login as user1
         userLogin1();

         // Do a search for the letter 'a'
         doSearch(name);

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

         //Verify the url is not changed
         Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");
         
         //Verify Copy to dialog is opened successfully
         CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage().render();        
         Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains("Copy"));         
         
         //Select the destination folder
         copyOrMoveContentPage.selectPath("Recent Sites",this.siteName1,folderName2).render();
         copyOrMoveContentPage.selectCancelButton();
         
         //Navigate to destination folder in site document library
         ShareUser.openSitesDocumentLibrary(drone, siteName);        
         
         //Verify folder2 is copied successfully into folder1
         Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, name), "file is visible");  
         
         //Navigate to destination folder in site document library
         ShareUser.openSitesDocumentLibrary(drone, siteName1);
         ShareUserSitePage.navigateToFolder(drone, folderName2);
         
         //Verify folder2 is copied successfully into folder1
         Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, name), "file is visible");     
         
         // Logout
         ShareUtil.logout(drone);
         
         //login as user2
         userLogin2();
         
         //Do a search for the letter 'a'
         doSearch(name);     
         
         // Check the results
         Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");
         
         Assert.assertFalse(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName1));        

         trace("selectCopyFiletoFolderCancelTest complete");
     }
     
     /*selectCopyFiletoSiteFolderTest
      *This test is to verify 'Copy to' action is displayed under actions for uploaded file for same user
      *Click on 'Copy to' link under actions by same user
      *Verify url is not changed after clicking on Copy to link in search results page
      *select destination folder and select 'ok' button in 'copy to' dialog
      *Verify 'copy' file to folder in site document library is  done successfully
      *Logout and user1 and login as user2
      *Verify 'copy to' action link is not visible for user2 
      */
      
      @Test(groups = "alfresco-one")
      public void ALF_3() throws Exception
      {
          trace("Starting selectCopyFiletoSiteFolderTest");        
          
          String actionName1 = "Copy to";
          String name = ("a-fs-test1.docx");
          String folderName2 = "folder2";
          
          //String destinationName = "Recent Sites";
          
          // Login as user1
          userLogin1();

          // Do a search for the letter 'a'
          doSearch(name);

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

          //Verify the url is not changed
          Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");
          
          //Verify Copy to dialog is opened successfully
          CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage().render();        
          Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains("Copy"));          
          
          //Select the destination folder
          copyOrMoveContentPage.selectPath("Recent Sites",this.siteName1,folderName2).render();
          copyOrMoveContentPage.selectOkButton();
          
          //Navigate to destination folder in site document library
          ShareUser.openSitesDocumentLibrary(drone, siteName);        
          
          //Verify folder2 is copied successfully into folder1
          Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, name), "file is visible");
          
          //Navigate to destination folder in site document library
          ShareUser.openSitesDocumentLibrary(drone, siteName1);
          ShareUserSitePage.navigateToFolder(drone, folderName2);
          
          //Verify folder2 is copied successfully into folder1
          Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, name), "file is visible");     
          
          // Logout
          ShareUtil.logout(drone);
          
          //login as user2
          userLogin2();
          
          //Do a search for the letter 'a'
          doSearch(name);     
          
          // Check the results
          Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");
          
          Assert.assertFalse(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName1));        

          trace("selectCopyFiletoSiteFolderTest complete");
      }
      
     
      /*selectCopyFiletoRepoTest
      *This test is to verify 'Copy to' action is displayed under actions for uploaded file for same user
      *Click on 'Copy to' link under actions by same user
      *Verify url is not changed after clicking on Copy to link in search results page
      *Verify copied folder is displayed successfully in the destination folder
      *Logout and user1 and login as user2
      *Verify 'copy to' action link is not visible for user2 
      */
      
      @Test(groups = "EnterpriseOnly")
      public void ALF_4() throws Exception
      {
          trace("Starting selectCopyFiletoRepoTest");        
          
          String actionName1 = "Copy to";
          String name = ("a-fs-test1.docx");   
                  
          // Login as user1
          userLogin1();

          // Do a search for the letter 'a'
          doSearch(name);

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

          //Verify the url is not changed
          Assert.assertEquals(url, newUrl, "After clicking on action, the url should not have changed");
          
          //Verify Copy to dialog is opened successfully
          CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage().render();        
          Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains("Copy"));          
          
          //Select the destination folder
          copyOrMoveContentPage.selectPath("Repository","Shared").render();
          copyOrMoveContentPage.selectOkButton();
          
          //Navigate to destination folder in site document library
          ShareUser.openSitesDocumentLibrary(drone, siteName);        
          
          //Verify folder2 is copied successfully into folder1
          Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, name), "file is visible"); 
                   
          //Navigate to Repository
          RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone,"Shared");          
          
          //Verify file is copied successfully into folder1
          Assert.assertTrue(repositoryPage.isFileVisible(name),"file dispalyed successfully");             
          
          // Logout
          ShareUtil.logout(drone);
          
          //login as user2
          userLogin2();
          
          //Do a search for the letter 'a'
          doSearch(name);     
          
          // Check the results
          Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");
          
          Assert.assertFalse(facetedSearchPage.getResultByName(name).getActions().hasActionByName(actionName1));        

          trace("selectCopyFiletoRepoTest complete");
      }
      
      /*selectCopyFoldertoRepoTest
       *This test is to verify 'Copy to' action is displayed under actions for uploaded file for same user
       *Click on 'Copy to' link under actions by same user
       *Verify url is not changed after clicking on Copy to link in search results page
       *Verify copied folder is displayed successfully in the destination folder
       *Logout and user1 and login as user2
       *Verify 'copy to' action link is not visible for user2 
       */
       
	@Test(groups = "EnterpriseOnly")
	public void ALF_5() throws Exception {

		trace("Starting selectCopyFoldertoRepoTest");

		String actionName1 = "Copy to";
		String folderName1 = "folder1";
      		
		// Login as user1
		userLogin1();

		// Do a search for the letter 'a'
		doSearch(folderName1);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0, "After searching for text there should be some search results");

		// Get the current url
		String url = drone.getCurrentUrl();

		// Check Actions are displayed on Facet results page for folder
		Assert.assertTrue(facetedSearchPage.getResultByName(folderName1).getActions().hasActionByName(actionName1));

		// Click the first action
		facetedSearchPage.getResultByName(folderName1).getActions().clickActionByName(actionName1);

		// Get the url again
		String newUrl = drone.getCurrentUrl();

		// Verify the url is not changed
		Assert.assertEquals(url, newUrl,"After clicking on action, the url should not have changed");

		// Verify Copy to dialog is opened successfully
		CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage().render();
		Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains("Copy"));
		// copyOrMoveContentPage.selectDestination(destinationName).render();
		// copyOrMoveContentPage.selectSite(this.siteName).render();

		// Select the destination folder
		copyOrMoveContentPage.selectPath("Repository","Shared").render();
		copyOrMoveContentPage.selectOkButton();
		
		//Navigate to destination folder in site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);        
        
        //Verify folder2 is copied successfully into folder1
        Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName1), "file is visible");  

		//Navigate to Repository
        RepositoryPage repositoryPage = ShareUserRepositoryPage.navigateToFolderInRepository(drone,"Shared");          
        
        //Verify folder1 is copied successfully into repository
        Assert.assertTrue(repositoryPage.isFileVisible(folderName1));           
	
		// Logout
		ShareUtil.logout(drone);

		// login as user2
		userLogin2();

		// Do a search for foldername1
		doSearch(folderName1);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,"After searching for text there should be some search results");

		Assert.assertFalse(facetedSearchPage.getResultByName(folderName1).getActions().hasActionByName(actionName1));

		trace("selectCopyFoldertoRepoTest complete");
	}
	
	/*selectMoveFoldertoFolderTest
	*This test is to verify 'Move to' action is displayed under actions for uploaded file for same user
	*Click on 'Move to' link under actions by same user
	*Verify url is not changed after clicking on Move to link in search results page
	*Verify moved folder is displayed successfully in the destination folder and not displayed in existing folder
	*Logout and user1 and login as user2
	*Verify 'Move to' action link is not visible for user2  
	*/
	    
	@Test(groups = "alfresco-one")
	public void ALF_6() throws Exception {
		trace("Starting selectMoveFoldertoFolderTest");

		String actionName1 = "Move to";
		String folderName1 = "folder1";
		String folderName2 = "folder2";

		// String destinationName = "Recent Sites";

		// Login as user1
		userLogin1();

		// Do a search for the letter 'a'
		doSearch(folderName1);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		// Get the current url
		String url = drone.getCurrentUrl();

		// Check Actions are displayed on Facet results page for folder
		Assert.assertTrue(facetedSearchPage.getResultByName(folderName1)
				.getActions().hasActionByName(actionName1));

		// Click the first action
		facetedSearchPage.getResultByName(folderName1).getActions()
				.clickActionByName(actionName1);

		// Get the url again
		String newUrl = drone.getCurrentUrl();

		// Verify the url is not changed
		Assert.assertEquals(url, newUrl,
				"After clicking on action, the url should not have changed");

		// Verify Copy to dialog is opened successfully
		CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage()
				.render();
		Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains(
				"Copy"));

		// Select the destination folder
		copyOrMoveContentPage.selectPath("Recent Sites", this.siteName1,
				folderName2).render();
		copyOrMoveContentPage.selectOkButton();

		// Navigate to existing folder in site document library
		ShareUser.openSitesDocumentLibrary(drone, siteName);

		// Verify folder1 is not displayed in site document library successfully
		Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, folderName1),
				"folder1 is visible");

		// Navigate to destination folder in site document library
		ShareUser.openSitesDocumentLibrary(drone, siteName1);
		ShareUserSitePage.navigateToFolder(drone, folderName2);

		// Verify folder1 is moved successfully into folder2
		Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName1),
				"folder1 is visible");

		// Logout
		ShareUtil.logout(drone);

		// login as user2
		userLogin2();

		// Do a search for folderName1
		doSearch(folderName1);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		Assert.assertFalse(facetedSearchPage.getResultByName(folderName1)
				.getActions().hasActionByName(actionName1));

		trace("selectMoveFoldertoFolderTest complete");
	}

	/*
	 * selectMoveFiletoFolderCancelTestThis test is to verify 'Move to' action
	 * is displayed under actions for uploaded file for same userClick on 'Move
	 * to' link under actions by same userVerify url is not changed after
	 * clicking on Move to link in search results pageselect destination folder
	 * and select 'cancel' button in 'Move to' dialogVerify 'Move to' folder in
	 * site document library is cancelledLogout and user1 and login as user2
	 * Verify 'Move to' action link is not visible for user2
	 */

	@Test(groups = "alfresco-one")
	public void ALF_7() throws Exception {
		trace("Starting selectMoveFiletoFolderCancelTest");

		String actionName1 = "Move to";
		String name = ("a-fs-test1.docx");
		String folderName2 = "folder2";

		// String destinationName = "Recent Sites";

		// Login as user1
		userLogin1();

		// Do a search for the letter 'a'
		doSearch(name);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		// Get the current url
		String url = drone.getCurrentUrl();

		// Check Actions are displayed on Facet results page for folder
		Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions()
				.hasActionByName(actionName1));

		// Click the first action
		facetedSearchPage.getResultByName(name).getActions()
				.clickActionByName(actionName1);

		// Get the url again
		String newUrl = drone.getCurrentUrl();

		// Verify the url is not changed
		Assert.assertEquals(url, newUrl,
				"After clicking on action, the url should not have changed");

		// Verify Move to dialog is opened successfully
		CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage()
				.render();
		Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains(
				"Move"));

		// Select the destination folder
		copyOrMoveContentPage.selectPath("Recent Sites", this.siteName1,
				folderName2).render();
		copyOrMoveContentPage.selectCancelButton();

		// Navigate to destination folder in site document library
		ShareUser.openSitesDocumentLibrary(drone, siteName);

		// Verify folder1 is not displayed in site document library successfully
		Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, name),
				"folder1 is visible");

		// Navigate to destination folder in site document library
		ShareUser.openSitesDocumentLibrary(drone, siteName1);
		ShareUserSitePage.navigateToFolder(drone, folderName2);

		// Verify file is not moved successfully into folder2
		Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, name),
				"file is visible");

		// Logout
		ShareUtil.logout(drone);

		// login as user2
		userLogin2();

		// Do a search for the letter 'a'
		doSearch(name);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		Assert.assertFalse(facetedSearchPage.getResultByName(name).getActions()
				.hasActionByName(actionName1));

		trace("selectMoveFiletoFolderCancelTest complete");
	}

	/*
	 * selectMoveFiletoSiteFolderTestThis test is to verify 'Move to' action is
	 * displayed under actions for uploaded file for same userClick on 'Move to'
	 * link under actions by same userVerify url is not changed after clicking
	 * on Move to link in search results pageselect destination folder and
	 * select 'ok' button in 'Move to' dialogVerify 'Move' file to folder in
	 * site document library is done successfullyLogout and user1 and login as
	 * user2Verify 'Move to' action link is not visible for user2
	 */

	@Test(groups = "alfresco-one")
	public void ALF_8() throws Exception {
		trace("Starting selectMoveFiletoSiteFolderTest");

		String actionName1 = "Move to";
		String name = ("a-fs-test1.docx");
		String folderName2 = "folder2";

		// String destinationName = "Recent Sites";

		// Login as user1
		userLogin1();

		// Do a search for the letter 'a'
		doSearch(name);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		// Get the current url
		String url = drone.getCurrentUrl();

		// Check Actions are displayed on Facet results page for folder
		Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions()
				.hasActionByName(actionName1));

		// Click the first action
		facetedSearchPage.getResultByName(name).getActions()
				.clickActionByName(actionName1);

		// Get the url again
		String newUrl = drone.getCurrentUrl();

		// Verify the url is not changed
		Assert.assertEquals(url, newUrl,
				"After clicking on action, the url should not have changed");

		// Verify Move to dialog is opened successfully
		CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage()
				.render();
		Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains(
				"Move"));

		// Select the destination folder
		copyOrMoveContentPage.selectPath("Recent Sites", this.siteName1,
				folderName2).render();
		copyOrMoveContentPage.selectOkButton();

		// Navigate to existing folder in site document library
		ShareUser.openSitesDocumentLibrary(drone, siteName);

		// Verify folder2 is copied successfully into folder1
		Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, name),
				"file is visible");

		// Navigate to destination folder in site document library
		ShareUser.openSitesDocumentLibrary(drone, siteName1);
		ShareUserSitePage.navigateToFolder(drone, folderName2);

		// Verify folder2 is copied successfully into folder1
		Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, name),
				"file is visible");

		// Logout
		ShareUtil.logout(drone);

		// login as user2
		userLogin2();

		// Do a search for name
		doSearch(name);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		Assert.assertFalse(facetedSearchPage.getResultByName(name).getActions()
				.hasActionByName(actionName1));

		trace("selectMoveFiletoSiteFolderTest complete");
	}

	/*
	 * selectMoveFiletoRepoTestThis test is to verify 'Move to' action is
	 * displayed under actions for uploaded file for same userClick on 'Move to'
	 * link under actions by same userVerify url is not changed after clicking
	 * on Move to link in search results pageVerify copied folder is displayed
	 * successfully in the destination folderLogout and user1 and login as user2
	 * Verify 'Move to' action link is not visible for user2
	 */

	@Test(groups = "EnterpriseOnly")
	public void ALF_9() throws Exception {
		trace("Starting selectMoveFiletoRepoTest");

		String actionName1 = "Move to";
		String name = ("a-fs-test1.docx");

		// Login as user1
		userLogin1();

		// Do a search for the letter 'a'
		doSearch(name);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		// Get the current url
		String url = drone.getCurrentUrl();

		// Check Actions are displayed on Facet results page for folder
		Assert.assertTrue(facetedSearchPage.getResultByName(name).getActions()
				.hasActionByName(actionName1));

		// Click the first action
		facetedSearchPage.getResultByName(name).getActions()
				.clickActionByName(actionName1);

		// Get the url again
		String newUrl = drone.getCurrentUrl();

		// Verify the url is not changed
		Assert.assertEquals(url, newUrl,
				"After clicking on action, the url should not have changed");

		// Verify Move to dialog is opened successfully
		CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage()
				.render();
		Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains(
				"Move"));

		// Select the destination folder
		copyOrMoveContentPage.selectPath("Repository", "Shared").render();
		copyOrMoveContentPage.selectOkButton();

		// Navigate to destination folder in site document library
		ShareUser.openSitesDocumentLibrary(drone, siteName);

		// Verify file is not displayed in existing location
		Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, name),
				"file is visible");

		// Navigate to Repository
		RepositoryPage repositoryPage = ShareUserRepositoryPage
				.navigateToFolderInRepository(drone, "Shared");

		// Verify file is copied successfully into folder1
		Assert.assertTrue(repositoryPage.isFileVisible(name),
				"file dispalyed successfully");

		// Logout
		ShareUtil.logout(drone);

		// login as user2
		userLogin2();

		// Do a search for name
		doSearch(name);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		Assert.assertFalse(facetedSearchPage.getResultByName(name).getActions()
				.hasActionByName(actionName1));

		trace("selectMoveFiletoRepoTest complete");
	}

	/*
	 * selectMoveFoldertoRepoTestThis test is to verify 'Move to' action is
	 * displayed under actions for uploaded file for same userClick on 'Move to'
	 * link under actions by same userVerify url is not changed after clicking
	 * on Move to link in search results pageVerify copied folder is displayed
	 * successfully in the destination folderLogout and user1 and login as user2
	 * Verify 'Move to' action link is not visible for user2
	 */

	@Test(groups = "EnterpriseOnly")
	public void ALF_10() throws Exception {

		trace("Starting selectMoveFoldertoRepoTest");

		String actionName1 = "Move to";
		String folderName1 = "folder1";

		// Login as user1
		userLogin1();

		// Do a search for the letter 'a'
		doSearch(folderName1);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		// Get the current url
		String url = drone.getCurrentUrl();

		// Check Actions are displayed on Facet results page for folder
		Assert.assertTrue(facetedSearchPage.getResultByName(folderName1)
				.getActions().hasActionByName(actionName1));

		// Click the first action
		facetedSearchPage.getResultByName(folderName1).getActions()
				.clickActionByName(actionName1);

		// Get the url again
		String newUrl = drone.getCurrentUrl();

		// Verify the url is not changed
		Assert.assertEquals(url, newUrl,
				"After clicking on action, the url should not have changed");

		// Verify Move to dialog is opened successfully
		CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage()
				.render();
		Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains(
				"Move"));
		// MoveOrMoveContentPage.selectDestination(destinationName).render();
		// MoveOrMoveContentPage.selectSite(this.siteName).render();

		// Select the destination folder
		copyOrMoveContentPage.selectPath("Repository", "Shared").render();
		copyOrMoveContentPage.selectOkButton();

		// Navigate to site document library
		ShareUser.openSitesDocumentLibrary(drone, siteName);

		// Verify folder1 is not displayed in site document library
		Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, folderName1),
				"file is visible");

		// Navigate to Repository
		RepositoryPage repositoryPage = ShareUserRepositoryPage
				.navigateToFolderInRepository(drone, "Shared");

		// Verify folder1 is copied successfully into repository
		Assert.assertTrue(repositoryPage.isFileVisible(folderName1));

		// Logout
		ShareUtil.logout(drone);

		// login as user2
		userLogin2();

		// Do a search for folderName1
		doSearch(folderName1);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		// Verify action is not displayed for user2
		Assert.assertFalse(facetedSearchPage.getResultByName(folderName1)
				.getActions().hasActionByName(actionName1));

		trace("selectMoveFoldertoRepoTest complete");
	}

	/*
	 * selectCopyRepoFoldertoSiteFolderTestThis test is to verify 'Copy to'
	 * action is displayed under actions for uploaded file for same userClick on
	 * 'Copy to' link under actions by same userVerify url is not changed after
	 * clicking on Copy to link in search results pageVerify copied folder is
	 * displayed successfully in the destination folderLogout and user1 and
	 * login as user2Verify 'copy to' action link is not visible for user2
	 */

	@Test(groups = "EnterpriseOnly")
	public void ALF_11() throws Exception {
		trace("Starting selectCopyFoldertoFolderTest");

		String actionName1 = "Copy to";
		String folderName3 = "folder3";
		String folderName2 = "folder2";

		// Login as user1
		userLogin1();

		// Do a search for the letter 'a'
		doSearch(folderName3);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		// Get the current url
		String url = drone.getCurrentUrl();

		// Check Actions are displayed on Facet results page for folder
		Assert.assertTrue(facetedSearchPage.getResultByName(folderName3)
				.getActions().hasActionByName(actionName1));

		// Click the first action
		facetedSearchPage.getResultByName(folderName3).getActions()
				.clickActionByName(actionName1);

		// Get the url again
		String newUrl = drone.getCurrentUrl();

		// Verify the url is not changed
		Assert.assertEquals(url, newUrl,
				"After clicking on action, the url should not have changed");

		// Verify Copy to dialog is opened successfully
		CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage()
				.render();
		Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains(
				"Copy"));
		// copyOrMoveContentPage.selectDestination(destinationName).render();
		// copyOrMoveContentPage.selectSite(this.siteName).render();

		// Select the destination folder
		copyOrMoveContentPage.selectPath("Recent Sites", this.siteName1,
				folderName2).render();
		copyOrMoveContentPage.selectOkButton();

		// Navigate to existing folder in Repository
		ShareUserSitePage.navigateToFolder(drone, "Shared");

		// Verify folder2 is copied successfully into folder1
		Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName3),
				"folder1 is visible");

		// Navigate to destination folder in site document library
		ShareUser.openSitesDocumentLibrary(drone, siteName1);
		ShareUserSitePage.navigateToFolder(drone, folderName2);

		// Verify folder2 is copied successfully into folder1
		Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName3),
				"folder1 is visible");

		// Logout
		ShareUtil.logout(drone);

		// login as user2
		userLogin2();

		// Do a search for the letter 'a'
		doSearch(folderName3);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		Assert.assertFalse(facetedSearchPage.getResultByName(folderName3)
				.getActions().hasActionByName(actionName1));

		trace("selectCopyFoldertoFolderTest complete");
	}

	/*
	 * selectMoveRepoFoldertoSiteFolderTestThis test is to verify 'Move to'
	 * action is displayed under actions for uploaded file for same userClick on
	 * 'Move to' link under actions by same userVerify url is not changed after
	 * clicking on Move to link in search results pageVerify Moved folder is
	 * displayed successfully in the destination folderLogout and user1 and
	 * login as user2Verify 'Move to' action link is not visible for user2
	 */

	@Test(groups = "EnterpriseOnly")
	public void ALF_12() throws Exception {
		trace("Starting selectMoveFoldertoFolderTest");

		String actionName1 = "Move to";
		String folderName3 = "folder3";
		String folderName2 = "folder2";

		// Login as user1
		userLogin1();

		// Do a search for the letter 'a'
		doSearch(folderName3);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		// Get the current url
		String url = drone.getCurrentUrl();

		// Check Actions are displayed on Facet results page for folder
		Assert.assertTrue(facetedSearchPage.getResultByName(folderName3)
				.getActions().hasActionByName(actionName1));

		// Click the first action
		facetedSearchPage.getResultByName(folderName3).getActions()
				.clickActionByName(actionName1);

		// Get the url again
		String newUrl = drone.getCurrentUrl();

		// Verify the url is not changed
		Assert.assertEquals(url, newUrl,
				"After clicking on action, the url should not have changed");

		// Verify Copy to dialog is opened successfully
		CopyOrMoveContentPage copyOrMoveContentPage = drone.getCurrentPage()
				.render();
		Assert.assertTrue(copyOrMoveContentPage.getDialogTitle().contains(
				"Move"));
		// copyOrMoveContentPage.selectDestination(destinationName).render();
		// copyOrMoveContentPage.selectSite(this.siteName).render();

		// Select the destination folder
		copyOrMoveContentPage.selectPath("Recent Sites", this.siteName1,
				folderName2).render();
		copyOrMoveContentPage.selectOkButton();

		// Navigate to existing folder in Repository
		ShareUserSitePage.navigateToFolder(drone, "Shared");

		// Verify folder2 is copied successfully into folder1
		Assert.assertFalse(ShareUserSitePage.isFileVisible(drone, folderName3),
				"folder1 is visible");

		// Navigate to destination folder in site document library
		ShareUser.openSitesDocumentLibrary(drone, siteName1);
		ShareUserSitePage.navigateToFolder(drone, folderName2);

		// Verify folder2 is copied successfully into folder1
		Assert.assertTrue(ShareUserSitePage.isFileVisible(drone, folderName3),
				"folder1 is visible");

		// Logout
		ShareUtil.logout(drone);

		// login as user2
		userLogin2();

		// Do a search for folderName3
		doSearch(folderName3);

		// Check the results
		Assert.assertTrue(facetedSearchPage.getResults().size() > 0,
				"After searching for text there should be some search results");

		Assert.assertFalse(facetedSearchPage.getResultByName(folderName3)
				.getActions().hasActionByName(actionName1));

		trace("selectCopyFoldertoFolderTest complete");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.alfresco.share.util.AbstractUtils#tearDown()
	 * 
	 * Should not be cloud only.
	 */
	@AfterClass(alwaysRun = true, groups = "alfresco-one")
	public void tearDown() {
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
	 * @param searchTerm
	 *            the search term
	 */
	private void doSearch(String searchTerm) {
		// Do a search for the searchTerm
		facetedSearchPage.getSearchForm().search(searchTerm);

		// Reload the page objects
		facetedSearchPage.render();
	}

	/**
	 * Trace.
	 * 
	 * @param msg
	 *            the msg
	 */
	private void trace(String msg) {
		if (logger.isTraceEnabled()) {
			logger.trace(msg);
		}
	}

	/**
	 * Login as user1.
	 */
	private void userLogin1() {
		// Login as test user
		ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

		// Navigate to the faceted search page
		facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage()
				.render();
	}

	/**
	 * Login as user2.
	 */
	private void userLogin2() {
		// Login as test user
		ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

		// Navigate to the faceted search page
		facetedSearchPage = dashBoardPage.getNav().getFacetedSearchPage()
				.render();
	}
	
}