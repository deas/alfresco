package org.alfresco.po.share.dashlet;

import java.io.File;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.SiteUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

/**
 * Abstract class with common method used in dashlet
 * based test cases.
 * @author Michael Suzuki
 *
 */
public class AbstractDashletTest extends AbstractDocumentTest
{
    private static Log logger = LogFactory.getLog(AbstractDashletTest.class);
    protected String siteName;
    protected String fileName;
    protected DashBoardPage dashBoard;
    String userName = "user" + System.currentTimeMillis() + "@test.com";
    String firstName = userName;
    String lastName = userName;
    
    protected void uploadDocument()throws Exception
    {
    	try
    	{
	        File file = SiteUtil.prepareFile();
	        fileName = file.getName();
	        
	        if ( !alfrescoVersion.isCloud() )
            {
	        	dashBoard = loginAs(username, password);
	        	//Creating new user.
	       
	            UserSearchPage page = dashBoard.getNav().getUsersPage();
	            NewUserPage newPage = page.selectNewUser().render();
	            newPage.inputFirstName(firstName);
	            newPage.inputLastName(lastName);
	            newPage.inputEmail(userName);
	            newPage.inputUsername(userName);
	            newPage.inputPassword(userName);
	            newPage.inputVerifyPassword(userName);
	            UserSearchPage userCreated = newPage.selectCreateUser().render();
	            userCreated.searchFor(userName).render();
	            Assert.assertTrue(userCreated.hasResults());
	            ShareUtil.logout(drone);
	            loginAs(userName, userName);
            }
	        else
	            loginAs(username,password);
	        
	        SiteUtil.createSite(drone,siteName, 
	                            "description",
	                            "Public");
	        SitePage site = drone.getCurrentPage().render();
	        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
	        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload();
	        docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
	        DocumentDetailsPage detailsPage = docPage.selectFile(fileName).render();
	        dashBoard = detailsPage.getNav().selectMyDashBoard().render();
	        //DocumentDetailsPage dd = docPage.selectFile(fileName).render();
	        //dd.selectLike();
    	}
        catch (Throwable pe)
        {
            saveScreenShot("uploadDodDashlet");
            logger.error("Problem deleting site", pe);
        }
    }
    
    @BeforeMethod
    public void startAtDashboard()
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard().render();
    }
    
    public void deleteSite()
    {
        try
        {
            SiteUtil.deleteSite(drone, siteName);
        }
        catch (Exception e)
        {
            logger.error("Problem deleting site", e);
        }
    }
}
