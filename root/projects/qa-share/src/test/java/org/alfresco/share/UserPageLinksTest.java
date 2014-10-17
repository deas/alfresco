package org.alfresco.share;

import org.alfresco.po.share.ChangePasswordPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
//import org.alfresco.share.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

/**
 * Class includes: Tests from TestLink in Area: 
 * <ul>
 * <li>Perform an activity on UserMenu</li>
 * <li>Verifies the links in the UserMenu</li>
 * <li>verifies the relevant pages from the UserMenu</li>
 * </ul>
 */
public class UserPageLinksTest extends AbstractUtils {
          private static Log logger = LogFactory.getLog(UserPageLinksTest.class);
	  private String siteDomain = "siteNotice.test";
	  @Override
	  @BeforeClass(alwaysRun = true)
	  public void setup() throws Exception{
	      super.setup();
	      testName = this.getClass().getSimpleName();
	      logger.info("Start Tests in: " + testName);
	  }
	
	  @Test(groups = { "DataPrepSiteNoticeDashlet" })
	  public void dataPrep_AONE_2661() throws Exception{		  
	      String testName = getTestName();
	      String testUser = getUserNameForDomain(testName, siteDomain);
	      logger.info("testusername = " + testUser );	      
	      // User
	      String[] testUserInfo = new String[] { testUser };
	      //CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
	      CreateUserAPI.CreateActivateUser(drone, "admin", testUserInfo);
	  }
	  
	  /**
	   * Test includes: Tests for alf_one only: 
	   * <ul>
	   * <li>Verifies ChangePassWordLink</li>
	   * <li>Verifies HelpLink</li>
	   * <li>verifies My Profile Link</li>
	   * </ul>
	   */
	  @Test
	  public void AONE_2661() throws Exception
	  {
	      String testName = getTestName();
	      String testUser = getUserNameForDomain(testName, siteDomain);
	      SharePage nav= ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
	      UserPage userpage = nav.getNav().selectUserDropdown().render();
	      assertTrue(userpage.isChangePassWordLinkPresent());
	      assertTrue(userpage.isHelpLinkPresent());
	      assertTrue(userpage.isMyProfileLinkPresent());
	      assertTrue(userpage.isSetStausLinkPresent());
	      assertTrue(userpage.isLogoutLinkPresent());
	      ChangePasswordPage changePasswordPage = userpage.selectChangePassword().render(); 
	      assertEquals(changePasswordPage.getTitle(), "Alfresco » Change User Password");
	   }

}
