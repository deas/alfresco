package org.alfresco.share.search;

/**
 
 * 
 * @author Charu
 * 
 */

import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.search.PreViewPopUpPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class PreviewPopUpPageTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(BasicSearchTest.class);    

    protected String testUser;
    
    protected String siteName;

	/**
     * Class includes: Tests from TestLink in Area: Image preview Tests
     * <ul>
     *   <li>Test includes preview of different file types </li>
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
    
    // Data prep
    @Test(groups={"DataPrepSearch"})
    public void dataPrep_ALF_3260() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] {testUser};
        
        String siteName = getSiteName(testName);
        
        // Files
        String[] fileName = new String[14];
        fileName[0] = getFileName(testName + "." + "xlsx");
		fileName[1] = getFileName(testName + "." + "xml");    		
		fileName[2] = getFileName(testName + "." + "xml");
		fileName[3] = getFileName(testName + "." + "html");    		
		fileName[4] = getFileName(testName + "." + "ods");
		fileName[5] = getFileName(testName + "." + "odt");
		fileName[6] = getFileName(testName + "." + "xls");
		fileName[7] = getFileName(testName + "." + "xsl");
		fileName[8] = getFileName(testName + "." + "doc");
		fileName[9] = getFileName(testName + "." + "docx");
		fileName[10] = getFileName(testName + "." + "pptx");
		fileName[11] = getFileName(testName + "." + "pot");
		fileName[12] = getFileName(testName + "." + "xsd");    		
		fileName[13] = getFileName(testName + "." + "rtf");
        Integer fileTypes = fileName.length - 1;

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // UpLoad Files
        for (int index = 0; index <= fileTypes; index++)
        {
            String[] fileInfo = { fileName[index] };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
    }
    
    // Data prep to create image files in a site
    @Test(groups={"DataPrepSearch"})
    public void dataPrep_ALF_3259() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] {testUser};
        
        String siteName = getSiteName(testName);
        
        // Files
        String[] fileName = new String[2];
        fileName[0] = getFileName(testName + "." + "jpg");
        fileName[1] = getFileName(testName + "." + "png");

        Integer fileTypes = fileName.length - 1;

        // User
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // UpLoad Files
        for (int index = 0; index <= fileTypes; index++)
        {
            String[] fileInfo = { fileName[index] };
            ShareUser.uploadFileInFolder(drone, fileInfo);
        }
    }

    
    /**
     * PreviewPopUpPageTestImageFiles:
     * <ul>         
     *   <li>Login</li>
     *   <li>Check preview is displayed when clicking on the for different image types of files: Search based on Content</li>
     * </ul>
     */
    @Test
    public void ALF_3259()
    {

    		/**Start Test*/
    		String testName = getTestName();

    		/**Test Data Setup*/
    		String testUser = getUserNameFreeDomain(testName);
    		String siteName = getSiteName(testName);
    		String[] fileName = new String[2];

    		fileName[0] = getFileName(testName + "." + "jpg");
            fileName[1] = getFileName(testName + "." + "png");


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
    		
            FacetedSearchPage facetedSearchPage = drone.getCurrentPage().render();
    		
    		for (int index=0; index <= fileTypes; index++)
    		{          
    	        PreViewPopUpPage preViewPopUpPage = facetedSearchPage.getResultByName(fileName[index]).clickImageLink().render();	
    			Assert.assertTrue(preViewPopUpPage.isTitlePresent(fileName[index]),"Title is displayed");
    			Assert.assertTrue(preViewPopUpPage.isPreViewDisplayed(),"Preview image is displayed successfully");
    			facetedSearchPage = preViewPopUpPage.selectClose().render();    			
    			Assert.assertTrue(facetedSearchPage.isTitlePresent("Search"));    			
    			
    		}
    		
    }
    
    /**
     * PreviewPopUpPageTestDiffFileTypes:
     * <ul>
     *   <li>Login</li>
     *   <li>Check preview is displayed when clicking on the for diff types of files: Search based on Content</li>
     * </ul>
     */
    @Test
    public void ALF_3260()
    {

    		/**Start Test*/
    		String testName = getTestName();

    		/**Test Data Setup*/
    		String testUser = getUserNameFreeDomain(testName);
    		String siteName = getSiteName(testName);
    		String[] fileName = new String[14];

    		fileName[0] = getFileName(testName + "." + "xlsx");
    		fileName[1] = getFileName(testName + "." + "xml");    		
    		fileName[2] = getFileName(testName + "." + "xml");
    		fileName[3] = getFileName(testName + "." + "html");    		
    		fileName[4] = getFileName(testName + "." + "ods");
    		fileName[5] = getFileName(testName + "." + "odt");
    		fileName[6] = getFileName(testName + "." + "xls");
    		fileName[7] = getFileName(testName + "." + "xsl");
    		fileName[8] = getFileName(testName + "." + "doc");
    		fileName[9] = getFileName(testName + "." + "docx");
    		fileName[10] = getFileName(testName + "." + "pptx");
    		fileName[11] = getFileName(testName + "." + "pot");
    		fileName[12] = getFileName(testName + "." + "xsd");    		
    		fileName[13] = getFileName(testName + "." + "rtf");

    		String searchTerm = testName;

    		Integer fileTypes = fileName.length-1;

    		/**Test Steps*/
    		//Login
    		ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
    		
    		//Open Site DashBoard
    		ShareUser.openSiteDashboard(drone, siteName);

    		//Search
    		ShareUserSearchPage.basicSearch(drone, searchTerm, false);

    		FacetedSearchPage facetedSearchPage = drone.getCurrentPage().render();
    		
    		for (int index=1; index <= fileTypes; index++)
    		{         
    			   			
    			PreViewPopUpPage preViewPopUpPage = facetedSearchPage.getResultByName(fileName[index]).clickImageLink().render();	
    			Assert.assertTrue(preViewPopUpPage.isTitlePresent(fileName[index]),"Title is displaed");
    			Assert.assertTrue(preViewPopUpPage.isPreViewTextDisplayed(),"Preview text is displayed successfully");
    			facetedSearchPage = preViewPopUpPage.selectClose().render();
    			Assert.assertTrue(facetedSearchPage.isTitlePresent("Search"));
    			
    		}
    		
    }   
    
    
}