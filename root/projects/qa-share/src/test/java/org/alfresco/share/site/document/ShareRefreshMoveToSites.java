package org.alfresco.share.site.document;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.ShareUser.TypeOfPage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class ShareRefreshMoveToSites extends AbstractUtils {
	private static final Logger logger = Logger.getLogger(ShareRefreshMoveToSites.class);		

	private String testUser;

	@Override
	@BeforeClass(alwaysRun=true)
	public void setup() throws Exception
	{
		super.setup();
	    //create a single user
	    testName = this.getClass().getSimpleName();
	    testUser = testName + "@" + DOMAIN_FREE;
	    String[] testUserInfo = new String[] {testUser};
	    try
	    {
	    	CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
	    	logger.info("ShareRefreshMoveToSites users created.");
		} catch (Throwable e)
		{
			reportError(drone, testName, e);
		} 
	    	
	}
	
	@BeforeMethod(groups={"ShareRefreshMoveToSites"})
	public void prepare() throws Exception
	{
		// login as created user
		try
		{
			ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
			logger.info("ShareRefreshMoveToSites user logged in - drone.");
		} catch (Throwable e)
		{
			reportError(drone, testName, e);
		} 
			
	     
	}
	
	@AfterMethod(groups={"ShareRefreshMoveToSites"})
	public void quit() throws Exception
	{
		// login as created user
		try
		{
			ShareUser.logout(drone);
			logger.info("ShareRefreshMoveToSites user logged out - drone.");
		} catch (Throwable e)
		{
			reportError(drone, testName, e);
		} 		
	     
	}	
	
	
	/**
	 * Move to dialog
	 * 
	 * enterprise
	 * cloud 
	 * 
	 * @throws Exception
	 */
	 
	@Test(groups={"ShareRefreshMoveToSites"})
	public void ALF_10469() throws Exception
	{  
		//create content    
		String testName = getTestName();
		String siteName = testName + System.currentTimeMillis();
		ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
			  
		//upload file    
		File sampleFile = SiteUtil.prepareFile();
		String fileName = sampleFile.getName(); 		  		  
		String[] fileInfo = { fileName, DOCLIB };
		DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
		CopyOrMoveContentPage moveToPage = documentLibraryPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
		List<String> destinations = moveToPage.getDestinations();
		List<String> sites = moveToPage.getSites();
			  
			  
		if (isAlfrescoVersionCloud(drone)) {
			//cloud
			Assert.assertTrue(destinations.contains("Recent Sites"));
			Assert.assertTrue(destinations.contains("Favorite Sites"));
			Assert.assertTrue(destinations.contains("All Sites"));
		} else {
			//enterprise
			Assert.assertTrue(destinations.contains("Recent Sites"));
			Assert.assertTrue(destinations.contains("Favorite Sites"));
			Assert.assertTrue(destinations.contains("All Sites"));
			Assert.assertTrue(destinations.contains("Repository"));
			Assert.assertTrue(destinations.contains("Shared Files"));
			Assert.assertTrue(destinations.contains("My Files"));
			Assert.assertTrue(sites.contains(siteName));				  
			  
		}
			  
	}	  
	  
	/**
	 * Move to Recent Sites
	 * 
	 * enterprise
	 * cloud 
	 * 
	 * @throws Exception
	 */
	 
	@Test(groups={"ShareRefreshMoveToSites"})
	public void ALF_10470() throws Exception
	{  
		//create site    
		String testName = getTestName();
		String siteName = testName + System.currentTimeMillis();
		ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
			  
		//upload file
		DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
		File sampleFile = SiteUtil.prepareFile();
		ShareUserSitePage.uploadFile(drone, sampleFile);
			  	  
        //create and visit recent site
		ShareUser.openUserDashboard(drone);
		String recentSiteName = testName + "RecentSite"+ System.currentTimeMillis();
		ShareUser.createSite(drone, recentSiteName, SITE_VISIBILITY_PUBLIC);
			  
		//move file to recent site
		docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

		ShareUser.copyOrMoveArtifact(drone, "Recent Sites", recentSiteName, sampleFile.getName(), PerformOperation.OK, TypeOfPage.MoveTo);
			  
		//check the file is not present anymore in the original site
		Assert.assertFalse(docPage.isFileVisible(sampleFile.getName()));
			  
		//check the file is present in recent site
		docPage = ShareUser.openSitesDocumentLibrary(drone, recentSiteName);
		Assert.assertTrue(docPage.isFileVisible(sampleFile.getName()));

	}	  
	  
	/**
	 * Move to Recent Sites - Cancel
	 * 
	 * enterprise
	 * cloud 
	 * 
	 * @throws Exception
	 */
	 
	@Test(groups={"ShareRefreshMoveToSites"})
	public void ALF_10471() throws Exception
	{  
		//create site    
		String testName = getTestName();
		String siteName = testName + System.currentTimeMillis();
		ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
			  
		//upload file
		DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
		File sampleFile = SiteUtil.prepareFile();
		ShareUserSitePage.uploadFile(drone, sampleFile);
			  	  
        //create and visit recent site
		ShareUser.openUserDashboard(drone);
		String recentSiteName = testName + "RecentSite"+ System.currentTimeMillis();
		ShareUser.createSite(drone, recentSiteName, SITE_VISIBILITY_PUBLIC);
			  
		//move file to recent site - cancel
		docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
		
		ShareUser.copyOrMoveArtifact(drone, "Recent Sites", recentSiteName, sampleFile.getName(), PerformOperation.CANCEL, TypeOfPage.MoveTo);
			  
		//check the file is still present in the original site
		Assert.assertTrue(docPage.isFileVisible(sampleFile.getName()));
			  
		//check the file is not present in the recent site
		docPage = ShareUser.openSitesDocumentLibrary(drone, recentSiteName);
		Assert.assertFalse(docPage.isFileVisible(sampleFile.getName()));

	}	 
	  
	/**
	 * Move to Favourite Sites
	 * 
	 * enterprise
	 * cloud 
	 * 
	 * @throws Exception
	 */
	 
	@Test(groups={"ShareRefreshMoveToSites"})
	public void ALF_10472() throws Exception
	{  
		//create site    
		String testName = getTestName();
		String siteName = testName + System.currentTimeMillis();
		ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
			  
		//upload file
		DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
		File sampleFile = SiteUtil.prepareFile();
		ShareUserSitePage.uploadFile(drone, sampleFile);
			  	  
        //create and visit favourite site
		ShareUser.openUserDashboard(drone);
		String favouriteSiteName = testName + "FavouriteSite"+ System.currentTimeMillis();
		ShareUser.createSite(drone, favouriteSiteName, SITE_VISIBILITY_PUBLIC);
			  
		//move file to recent site
		docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

		ShareUser.copyOrMoveArtifact(drone, "Favorite Sites", favouriteSiteName, sampleFile.getName(), PerformOperation.OK, TypeOfPage.MoveTo);
			  
		//check the file is not present anymore in the original site
		Assert.assertFalse(docPage.isFileVisible(sampleFile.getName()));
			  
		//check the file is present in the favourite site
		docPage = ShareUser.openSitesDocumentLibrary(drone, favouriteSiteName);
		Assert.assertTrue(docPage.isFileVisible(sampleFile.getName()));
	}	  
	  
	/**
	 * Move to Favourite Sites - Cancel
	 * 
	 * enterprise
	 * cloud 
	 * 
	 * @throws Exception
	 */
	 
	@Test(groups={"ShareRefreshMoveToSites"})
	public void ALF_10473() throws Exception
	{  
			  
		//create site    
		String testName = getTestName();
		String siteName = testName + System.currentTimeMillis();
		ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
			  
		//upload file
		DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
		File sampleFile = SiteUtil.prepareFile();
		ShareUserSitePage.uploadFile(drone, sampleFile);
			  	  
        //create and visit recent site
		ShareUser.openUserDashboard(drone);
		String favouriteSiteName = testName + "FavouriteSite"+ System.currentTimeMillis();
		ShareUser.createSite(drone, favouriteSiteName, SITE_VISIBILITY_PUBLIC);
			  
		//move file to recent site - cancel
		docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

		ShareUser.copyOrMoveArtifact(drone, "Favorite Sites", favouriteSiteName, sampleFile.getName(), PerformOperation.CANCEL, TypeOfPage.MoveTo);
			  
		//check the file is still present in the original site
		Assert.assertTrue(docPage.isFileVisible(sampleFile.getName()));
			  
		//check the file is not present in the favourite site
		docPage = ShareUser.openSitesDocumentLibrary(drone, favouriteSiteName);
		Assert.assertFalse(docPage.isFileVisible(sampleFile.getName()));

	}	 
	  
	/**
	 * Move to Sites
	 * 
	 * enterprise
	 * cloud 
	 * 
	 * @throws Exception
	 */
	 
	@Test(groups={"ShareRefreshMoveToSites"})
	public void ALF_10474() throws Exception
	{  
			  
		//create site    
		String testName = getTestName();
		String siteName = testName + System.currentTimeMillis();
		ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
			  
		//upload file
		DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
		File sampleFile = SiteUtil.prepareFile();
		ShareUserSitePage.uploadFile(drone, sampleFile);
			  	  
        //create and visit recent site
		ShareUser.openUserDashboard(drone);
		String anySiteName = testName + "AnySite"+ System.currentTimeMillis();
		ShareUser.createSite(drone, anySiteName, SITE_VISIBILITY_PUBLIC);
			  
		//move file to recent site
		docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

		ShareUser.copyOrMoveArtifact(drone, "All Sites", anySiteName, sampleFile.getName(), PerformOperation.OK, TypeOfPage.MoveTo);
			  
		//check the file is not present anymore in the original site
		Assert.assertFalse(docPage.isFileVisible(sampleFile.getName()));
			  
		//check the file is present in public site
		docPage = ShareUser.openSitesDocumentLibrary(drone, anySiteName);
		Assert.assertTrue(docPage.isFileVisible(sampleFile.getName()));

	}	  
	  
	/**
	 * Move to Sites - Cancel
	 * 
	 * enterprise
	 * cloud 
	 * 
	 * @throws Exception
	 */
	 
	@Test(groups={"ShareRefreshMoveToSites"})
	public void ALF_10475() throws Exception
	{  
			  
		//create site    
		String testName = getTestName();
		String siteName = testName + System.currentTimeMillis();
		ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
			  
		//upload file
		DocumentLibraryPage docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
		File sampleFile = SiteUtil.prepareFile();
		ShareUserSitePage.uploadFile(drone, sampleFile);
			  	  
        //create and visit recent site
		ShareUser.openUserDashboard(drone);
		String anySiteName = testName + "AnySite"+ System.currentTimeMillis();
		ShareUser.createSite(drone, anySiteName, SITE_VISIBILITY_PUBLIC);
			  
		//move file to recent site - cancel
		docPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

		ShareUser.copyOrMoveArtifact(drone, "All Sites", anySiteName, sampleFile.getName(), PerformOperation.CANCEL, TypeOfPage.MoveTo);
                
			  
		//check the file is still present in the original site
		Assert.assertTrue(docPage.isFileVisible(sampleFile.getName()));
			  
		//check the file is not present in the public site
		docPage = ShareUser.openSitesDocumentLibrary(drone, anySiteName);
		Assert.assertFalse(docPage.isFileVisible(sampleFile.getName()));

	}	 
	  
	  
}
