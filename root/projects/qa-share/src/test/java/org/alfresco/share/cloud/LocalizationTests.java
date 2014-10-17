package org.alfresco.share.cloud;

import static org.testng.Assert.assertFalse;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.InviteToAlfrescoPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.UserPage;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesUserFilter;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.CloudForgotPasswordPage;
import org.alfresco.po.share.user.Language;
import org.alfresco.po.share.workflow.AssignmentPage;
import org.alfresco.po.share.workflow.CloudTaskOrReviewPage;
import org.alfresco.po.share.workflow.KeepContentStrategy;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.Priority;
import org.alfresco.po.share.workflow.SelectContentPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.TaskType;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.share.cloud.documentlibrary.QuickShareTests;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.MailUtil;
import org.alfresco.share.util.PropertiesUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.ShareUserWorkFlow;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
@Test(groups = { "CloudOnly" })
public class LocalizationTests extends AbstractUtils {
	private static Log logger = LogFactory.getLog(QuickShareTests.class);

	private String userCloudEmail;
	private String userCoundPassword;

	private String testDomain;
	private String alfrescoglobalpath = "";

	private static final By DOCUMENT_LIBRARY = By
			.cssSelector("a[href$='documentlibrary']");
	private static final By TASK_DROP_DOWN = By
			.cssSelector("button[id$='default-workflow-definition-button-button']");
	private static final By ADD_BUTTON = By
			.cssSelector("div[id$='packageItems-cntrl-itemGroupActions'] span:nth-child(1) span button");
	private static final By SELECT_BUTTON = By
			.cssSelector("div[id$='assoc_bpm_assignee-cntrl-itemGroupActions'] button");

	@Override
	@BeforeClass(alwaysRun = true)
	public void setup() throws Exception {
		super.setup();
		testName = this.getClass().getSimpleName() + 3;
		userCloudEmail = googleUserName; // "alfresco.cloud@gmail.com";
		userCoundPassword = googlePassword; // "eiWei6vieiWei6vi";

		testDomain = DOMAIN_HYBRID;
		logger.info("Starting Tests: " + testName);
	}

	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
	public void dataPrep_AONE_12744() throws Exception {

		// --- Precondition ---
		// 1. At least two users are created and activated.

		String testUser1 = getUserNameFreeDomain(testName + "12744" + 1);
		String testUser2 = getUserNameFreeDomain(testName + "12744" + 2);

		String[] testUserInfo1 = new String[] { testUser1 };
		String[] testUserInfo2 = new String[] { testUser2 };

		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);

	}

	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
	public void dataPrep_AONE_12745() throws Exception {
		// --- Precondition ---
		// Preconditions
		// 1. Atleast two users are created;
		// 2. user1@cloud.test has set his language preference as English;
		// 3. user2@cloud.test has set his language preference as French;

		String testUser12745E = getUserNameFreeDomain(testName + "12745E");
		String testUser12745F = getUserNameFreeDomain(testName + "12745F");
		String testUser12745D = getUserNameFreeDomain(testName + "12745D");
		String testUser12745S = getUserNameFreeDomain(testName + "12745S");
		String testUser12745I = getUserNameFreeDomain(testName + "12745I");
//		String testUser12745J = getUserNameFreeDomain(testName + "12745J");

		String[] testUserInfo12745E = new String[] { testUser12745E };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12745E);

		String[] testUserInfo12745F = new String[] { testUser12745F };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12745F);

		String[] testUserInfo12745D = new String[] { testUser12745D };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12745D);

		String[] testUserInfo12745S = new String[] { testUser12745S };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12745S);

		String[] testUserInfo12745I = new String[] { testUser12745I };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12745I);

		// String[] testUserInfo12745J = new String[] { testUser12745J };
		// CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
		// testUserInfo12745J);
	}

	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
	public void dataPrep_AONE_12746() throws Exception {
		// --- Precondition ---
		// Preconditions
		// 1. Atleast two users are created;
		// 2. user1@cloud.test has set his language preference as English;
		// 3. user2@cloud.test has set his language preference as French;

		String testUser12746E = getUserNameFreeDomain(testName + "12746E");

		String[] testUserInfo12746E = new String[] { testUser12746E };
		CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME,
				testUserInfo12746E);

	}

	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
	public void dataPrep_AONE_12747() throws Exception {
		// --- Precondition ---
		// Preconditions
		// 1. Atleast two users are created;
		// 2. user1@cloud.test has set his language preference as English;
		// 3. user2@cloud.test has set his language preference as French;

		String testUser12747E = getUserNameFreeDomain(testName + "12747E");
		String testUser12747F = getUserNameFreeDomain(testName + "12747F");
		String testUser12747D = getUserNameFreeDomain(testName + "12747D");
		String testUser12747S = getUserNameFreeDomain(testName + "12747S");
		String testUser12747I = getUserNameFreeDomain(testName + "12747I");
		String testUser12747J = getUserNameFreeDomain(testName + "12747J");

		String[] testUserInfo12747E = new String[] { testUser12747E };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12747E);

		String[] testUserInfo12747F = new String[] { testUser12747F };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12747F);

		String[] testUserInfo12747D = new String[] { testUser12747D };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12747D);

		String[] testUserInfo12747S = new String[] { testUser12747S };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12747S);

		String[] testUserInfo12747I = new String[] { testUser12747I };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12747I);

		String[] testUserInfo12747J = new String[] { testUser12747J };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME,
				testUserInfo12747J);

		// ShareUser.loginAs(drone, testUser12747F).render();
		// ShareUser.changeLanguage(drone, Language.FRENCH).render();
		// ShareUser.logout(drone);
		//
		// // drone.navigateTo(shareUrl);
		// LoginPage lp = new LoginPage(drone).render();
		// lp.loginAs(testUser12747D, DEFAULT_PASSWORD, Language.ENGLISH_US);
		// ShareUser.changeLanguage(drone, Language.DEUTSCHE).render();
		// ShareUser.logout(drone);
		//
		// LoginPage lp1 = new LoginPage(drone).render();
		// lp1.loginAs(testUser12747S, DEFAULT_PASSWORD, Language.ENGLISH_US);
		// ShareUser.changeLanguage(drone, Language.SPANISH).render();
		// ShareUser.logout(drone);
		//
		// LoginPage lp2 = new LoginPage(drone).render();
		// lp2.loginAs(testUser12747I, DEFAULT_PASSWORD, Language.ENGLISH_US);
		// ShareUser.changeLanguage(drone, Language.ITALIAN).render();
		// ShareUser.logout(drone);

		drone.maximize();
		drone.navigateTo(shareUrl);
		LoginPage lp = new LoginPage(drone).render();
		lp.loginAs(testUser12747F, DEFAULT_PASSWORD, Language.FRENCH);
		ShareUser.logout(drone);

		LoginPage lp1 = new LoginPage(drone).render();
		lp1.loginAs(testUser12747D, DEFAULT_PASSWORD, Language.DEUTSCHE);
		ShareUser.logout(drone);

		LoginPage lp2 = new LoginPage(drone).render();
		lp2.loginAs(testUser12747S, DEFAULT_PASSWORD, Language.SPANISH);
		ShareUser.logout(drone);

		LoginPage lp3 = new LoginPage(drone).render();
		lp3.loginAs(testUser12747I, DEFAULT_PASSWORD, Language.ITALIAN);
		ShareUser.logout(drone);

	}

	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
	public void dataPrep_AONE_12748() throws Exception {

		String testUser12748E = getUserNameFreeDomain(testName + "12748E");
		String testUser12748F = getUserNameFreeDomain(testName + "12748F");
		String testUser12748D = getUserNameFreeDomain(testName + "12748D");
		String testUser12748S = getUserNameFreeDomain(testName + "12748S");
		String testUser12748I = getUserNameFreeDomain(testName + "12748I");
		// String testUser12748J = getUserNameFreeDomain(testName + "12748J");

		// --- Step 1 ---
		// --- Step action ---
		// At least two users are created;
		String[] testUser1 = new String[] { testUser12748E };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

		String[] newTestUser2 = new String[] { testUser12748F };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUser2);

		String[] newTestUserD = new String[] { testUser12748D };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserD);

		String[] newTestUserS = new String[] { testUser12748S };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserS);

		String[] newTestUserI = new String[] { testUser12748I };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserI);

		// --- Step 2 ---
		// --- Step action ---
		// user1@cloud.test has set his language preference as English;
		ShareUser.loginAs(drone, testUser12748E).render();
		ShareUser.changeLanguage(drone, Language.ENGLISH_US);
		ShareUser.logout(drone);

		// --- Step 3 ---
		// --- Step action ---
		// user2@cloud.test has set his language preference as French;
		LoginPage lp1 = new LoginPage(drone).render();
		lp1.loginAs(testUser12748F, DEFAULT_PASSWORD, Language.ENGLISH_US);
		ShareUser.changeLanguage(drone, Language.FRENCH).render();
		ShareUser.logout(drone);

		LoginPage lp2 = new LoginPage(drone).render();
		lp2.loginAs(testUser12748D, DEFAULT_PASSWORD, Language.ENGLISH_US);
		ShareUser.changeLanguage(drone, Language.DEUTSCHE).render();
		ShareUser.logout(drone);

		LoginPage lp3 = new LoginPage(drone).render();
		lp3.loginAs(testUser12748S, DEFAULT_PASSWORD, Language.ENGLISH_US);
		ShareUser.changeLanguage(drone, Language.SPANISH).render();
		ShareUser.logout(drone);

		LoginPage lp4 = new LoginPage(drone).render();
		lp4.loginAs(testUser12748I, DEFAULT_PASSWORD, Language.ENGLISH_US);
		ShareUser.changeLanguage(drone, Language.ITALIAN).render();
		ShareUser.logout(drone);

	}

	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
	public void dataPrep_AONE_12750() throws Exception {
		// --- Precondition--
		// Signup email has been sent to user but he hasn't activated yet (e.g
		// user3@cloud.test);

		String userE = getUserNameForDomain(testName + "12750E", testDomain);
		String userF = getUserNameForDomain(testName + "12750F", testDomain);
		String userD = getUserNameForDomain(testName + "12750D", testDomain);
		String userI = getUserNameForDomain(testName + "12750I", testDomain);
		String userJ = getUserNameForDomain(testName + "12750J", testDomain);
		String userS = getUserNameForDomain(testName + "12750S", testDomain);

		CreateUserAPI.signUp(drone, ADMIN_USERNAME, userE);

		CreateUserAPI.signUp(drone, ADMIN_USERNAME, userF);

		CreateUserAPI.signUp(drone, ADMIN_USERNAME, userD);

		CreateUserAPI.signUp(drone, ADMIN_USERNAME, userI);

		CreateUserAPI.signUp(drone, ADMIN_USERNAME, userJ);

		CreateUserAPI.signUp(drone, ADMIN_USERNAME, userS);

	}

	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
	public void dataPrep_AONE_12751() throws Exception {

		// --- Precondition ---
		// 1. Atleast two users are created;
		// 2. user1@cloud.test has set his language preference as English;
		// 3. user2@cloud.test has set his language preference as French;
		// 4. Log in as Admin user;

		String testUser12751E = getUserNameFreeDomain(testName + "12751E");
		String testUser12751F = getUserNameFreeDomain(testName + "12751F");
		String testUser12751D = getUserNameFreeDomain(testName + "12751D");
		String testUser12751S = getUserNameFreeDomain(testName + "12751S");
		String testUser12751I = getUserNameFreeDomain(testName + "12751I");
		// String testUser12751J = getUserNameFreeDomain(testName + "12748J");

		String[] testUser1 = new String[] { testUser12751E };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

		String[] newTestUser2 = new String[] { testUser12751F };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUser2);

		String[] newTestUserD = new String[] { testUser12751D };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserD);

		String[] newTestUserS = new String[] { testUser12751S };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserS);

		String[] newTestUserI = new String[] { testUser12751I };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserI);

		ShareUser.loginAs(drone, testUser12751E).render();
		ShareUser.changeLanguage(drone, Language.ENGLISH_US);
		ShareUser.logout(drone);

		ShareUser.loginAs(drone, testUser12751F).render();
		ShareUser.changeLanguage(drone, Language.FRENCH).render();
		ShareUser.logout(drone);

		ShareUser.loginAs(drone, testUser12751D).render();
		ShareUser.changeLanguage(drone, Language.DEUTSCHE).render();
		ShareUser.logout(drone);

		ShareUser.loginAs(drone, testUser12751S).render();
		ShareUser.changeLanguage(drone, Language.SPANISH).render();
		ShareUser.logout(drone);

		ShareUser.loginAs(drone, testUser12751I).render();
		ShareUser.changeLanguage(drone, Language.ITALIAN).render();
		ShareUser.logout(drone);

	}

	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
	public void dataPrep_AONE_12752() throws Exception {

		// --- Preconditions ---
		// --- Step 1 ---
		// --- Step action ---
		// 1. Atleast two users are created;
		// 2. user1@cloud.test has set his language preference as English;
		// 3. user2@cloud.test has set his language preference as French;
		// 4. As both users perform some activities (i.e. create sites, upload
		// content)

		String testUser = getUserNameFreeDomain(testName + "127521");
		String siteName = getSiteName(testName + 1);
		String fileName = "AONE_9831.xlsx";

		String[] testUser1 = new String[] { testUser };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

		ShareUser.login(drone, testUser);

		ShareUser.createSite(drone, siteName,
				AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

		ShareUser.openSitesDocumentLibrary(drone, siteName).render();
		File file = new File(DATA_FOLDER + SLASH + fileName);
		ShareUserSitePage.uploadFile(drone, file).render();

	}

	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
	public void dataPrep_AONE_12754() throws Exception {

		// --- Precondition ---
		// 1. Atleast two users are created;
		// 2. user1@cloud.test has set his language preference as English;
		// 3. user2@cloud.test has set his language preference as French;
		// 4. Log in as Admin user;

		String testUser12754E = getUserNameFreeDomain(testName + "12754E");
		String testUser12754F = getUserNameFreeDomain(testName + "12754F");
		String testUser12754D = getUserNameFreeDomain(testName + "12754D");
		String testUser12754S = getUserNameFreeDomain(testName + "12754S");
		String testUser12754I = getUserNameFreeDomain(testName + "12754I");
		// String testUser12751J = getUserNameFreeDomain(testName + "12748J");

		String[] testUser1 = new String[] { testUser12754E };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

		String[] newTestUser2 = new String[] { testUser12754F };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUser2);

		String[] newTestUserD = new String[] { testUser12754D };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserD);

		String[] newTestUserS = new String[] { testUser12754S };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserS);

		String[] newTestUserI = new String[] { testUser12754I };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserI);

		ShareUser.loginAs(drone, testUser12754E).render();
		ShareUser.changeLanguage(drone, Language.ENGLISH_US);
		ShareUser.logout(drone);

		ShareUser.loginAs(drone, testUser12754F).render();
		ShareUser.changeLanguage(drone, Language.FRENCH).render();
		ShareUser.logout(drone);

		ShareUser.loginAs(drone, testUser12754D).render();
		ShareUser.changeLanguage(drone, Language.DEUTSCHE).render();
		ShareUser.logout(drone);

		ShareUser.loginAs(drone, testUser12754S).render();
		ShareUser.changeLanguage(drone, Language.SPANISH).render();
		ShareUser.logout(drone);

		ShareUser.loginAs(drone, testUser12754I).render();
		ShareUser.changeLanguage(drone, Language.ITALIAN).render();
		ShareUser.logout(drone);

	}

//	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
//	public void dataPrep_AONE_12755() throws Exception {
//		// --- Preconditions ---
//		// --- Step 1 ---
//		// --- Step action ---
//		// 1. Atleast two users are created;
//		// 2. user1@cloud.test has set his language preference as English;
//		// 3. user2@cloud.test has set his language preference as French;
//
//		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
//				"cloud.signup.remindTimer3", "PT15S");
//		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
//				"cloud.signup.remindTimer7", "R3/PT30S");
//		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
//				"cloud.signup.endTimer", "PT120S");
//		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
//				"cloud.invite.remindTimer3", "PT15S");
//		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
//				"cloud.invite.remindTimer7", "R3/PT30S");
//		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
//				"cloud.invite.endTimer", "PT120S");
//		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
//				"cloud.resetpassword.endTimer", "PT120S");
//
//		String testUser12755E = getUserNameFreeDomain(testName + "12755E");
//		String testUser12755F = getUserNameFreeDomain(testName + "12755F");
//		String testUser12755D = getUserNameFreeDomain(testName + "12755D");
//		String testUser12755S = getUserNameFreeDomain(testName + "12755S");
//		String testUser12755I = getUserNameFreeDomain(testName + "12755I");
//		// String testUser12751J = getUserNameFreeDomain(testName + "12748J");
//
//		String[] testUser1 = new String[] { testUser12755E };
//		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
//
//		String[] newTestUser2 = new String[] { testUser12755F };
//		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUser2);
//
//		String[] newTestUserD = new String[] { testUser12755D };
//		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserD);
//
//		String[] newTestUserS = new String[] { testUser12755S };
//		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserS);
//
//		String[] newTestUserI = new String[] { testUser12755I };
//		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserI);
//
//		ShareUser.loginAs(drone, testUser12755E);
//		ShareUser.changeLanguage(drone, Language.ENGLISH_US);
//		ShareUser.logout(drone);
//
//		ShareUser.loginAs(drone, testUser12755F).render();
//		ShareUser.changeLanguage(drone, Language.FRENCH).render();
//		ShareUser.logout(drone);
//
//		ShareUser.loginAs(drone, testUser12755D).render();
//		ShareUser.changeLanguage(drone, Language.DEUTSCHE).render();
//		ShareUser.logout(drone);
//
//		ShareUser.loginAs(drone, testUser12755S).render();
//		ShareUser.changeLanguage(drone, Language.SPANISH).render();
//		ShareUser.logout(drone);
//
//		ShareUser.loginAs(drone, testUser12755I).render();
//		ShareUser.changeLanguage(drone, Language.ITALIAN).render();
//		ShareUser.logout(drone);
//
//	}

	@Test(groups = { "DataPrepLocalization", "CloudOnly" })
	public void dataPrep_AONE_12757() throws Exception {

		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
				"cloud.signup.remindTimer3", "PT15S");
		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
				"cloud.signup.remindTimer7", "R3/PT30S");
		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
				"cloud.signup.endTimer", "PT120S");
		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
				"cloud.invite.remindTimer3", "PT15S");
		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
				"cloud.invite.remindTimer7", "R3/PT30S");
		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
				"cloud.invite.endTimer", "PT120S");
		PropertiesUtil.setPropertyValue(alfrescoglobalpath,
				"cloud.resetpassword.endTimer", "PT120S");

		String testUser = getUserNameFreeDomain(testName + "12757E");
		CreateUserAPI.signUp(drone, ADMIN_USERNAME, testUser);
	}

	@Test(groups = { "Localization", "CloudOnly" }, timeOut = 400000)
	public void AONE_12743() throws Exception {

		// --- Step 1 ---
		// --- Step action ---
		// Do a sign up in Alfresco cloud
		// --- Expected results --
		// The sign up email should be recieved and irrespective of the language
		// choose the sign up email should be english and it is in the new
		// template with
		// the Alfresco banner on the top

		String testUser = getUserNameFreeDomain(testName + "12743");

		String[] testUserInfo = new String[] { testUser };
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

		String emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ENGLISH_US), "signupemail");
		String emailSubjectWithUser = String.format(emailSubject, testUser);
		logger.info(emailSubjectWithUser);

		String emailContent = MailUtil.checkGmail(userCloudEmail,
				userCoundPassword, testUser, emailSubjectWithUser);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");
		logger.info(emailContent);

		String expectedEmailContent = String.format(PropertiesUtil
				.getPropertyValue(getLocalizationFile(Language.ENGLISH_US),
						"activatereminder"));
		logger.info(expectedEmailContent);

		String pattern1 = expectedEmailContent;
		Pattern p = Pattern.compile(pattern1, Pattern.MULTILINE);

		Assert.assertTrue(p.matcher(emailContent).find(),
				"Email doesn't contain " + expectedEmailContent);

	}

	/** AONE-12744:Email template - Invite to Site */

	@Test(groups = { "Localization", "CloudOnly" })
	public void AONE_12744() throws Exception {

		String testUser1 = getUserNameFreeDomain(testName + "12744" + 1);
		String testUser2 = getUserNameFreeDomain(testName + "12744" + 2);

		verifyEmailForLoginWithLangAndInviteSite(Language.ENGLISH_US,
				testUser1, testUser2, false);
		verifyEmailForLoginWithLangAndInviteSite(Language.FRENCH, testUser1,
				testUser2, false);
		verifyEmailForLoginWithLangAndInviteSite(Language.DEUTSCHE, testUser1,
				testUser2, false);
		verifyEmailForLoginWithLangAndInviteSite(Language.ITALIAN, testUser1,
				testUser2, false);
		verifyEmailForLoginWithLangAndInviteSite(Language.SPANISH, testUser1,
				testUser2, false);

	}

	/** AONE-12745:Email template - create task */

	@Test(groups = { "Localization", "CloudOnly" })
	public void AONE_12745() throws Exception {

		String testUser12745E = getUserNameFreeDomain(testName + "12745E");
		String testUser12745F = getUserNameFreeDomain(testName + "12745F");
		String testUser12745D = getUserNameFreeDomain(testName + "12745D");
		String testUser12745S = getUserNameFreeDomain(testName + "12745S");
		String testUser12745I = getUserNameFreeDomain(testName + "12745I");

		verifyEmailForCreateTask(testUser12745E, Language.ENGLISH_US);
		verifyEmailForCreateTask(testUser12745F, Language.FRENCH);
		verifyEmailForCreateTask(testUser12745D, Language.DEUTSCHE);
		verifyEmailForCreateTask(testUser12745S, Language.SPANISH);
		verifyEmailForCreateTask(testUser12745I, Language.ITALIAN);

	}

	/** AONE-12746:Email template - Invite People */

	@Test(groups = { "Localization", "CloudOnly" })
	public void AONE_12746() throws Exception {

		String testUser12746E = getUserNameFreeDomain(testName + "12746E");
		String testUser12746E2 = getUserNameFreeDomain(testName + "ToInviteE");
		String testUser12745F = getUserNameFreeDomain(testName + "ToInviteF");
		String testUser12745D = getUserNameFreeDomain(testName + "ToInviteD");
		String testUser12745S = getUserNameFreeDomain(testName + "ToInviteS");
		String testUser12745I = getUserNameFreeDomain(testName + "ToInviteI");

		verifyEmailForInvitePeople(testUser12746E, Language.ENGLISH_US,
				testUser12746E2);
		verifyEmailForInvitePeople(testUser12746E, Language.FRENCH,
				testUser12745F);
		verifyEmailForInvitePeople(testUser12746E, Language.DEUTSCHE,
				testUser12745D);
		verifyEmailForInvitePeople(testUser12746E, Language.SPANISH,
				testUser12745S);
		verifyEmailForInvitePeople(testUser12746E, Language.ITALIAN,
				testUser12745I);

	}

	private void verifyEmailForCreateTask(String loginUser,
			Language loginLanguage) throws Exception {
		// --- Step 1 ---
		// --- Step action ---
		// As user1@cloud.test select English language on Login page and login;
		// --- Expected results --
		// The user should be logged in successfully;
		drone.maximize();
		drone.navigateTo(shareUrl);
		LoginPage loginPage = new LoginPage(drone).render();
		loginPage.loginAs(loginUser, DEFAULT_PASSWORD, loginLanguage);

		// --- Step 2 ---
		// --- Step action ---
		// Create a task and assign it to yourself;
		// --- Expected results --
		// The task email should be recieved in the english as the user logged
		// is english user;
		String fileName_12745 = getFileName(testName) + ".txt";
		File file1 = newFile(fileName_12745, fileName_12745);
		String siteName = testName
				+ loginLanguage.getLanguageValue().replace("_", "")
				+ "12745011";
		String taskName = "Task "
				+ loginLanguage.getLanguageValue().replace("_", "");

		ShareUser.createSite(drone, siteName,
				AbstractUtils.SITE_VISIBILITY_PUBLIC).render(5000);
		selectDocumentLibrary(drone).render();

		ShareUserSitePage.uploadFile(drone, file1).render();

		startTaskWorkflow(drone, taskName, loginUser, fileName_12745, siteName)
				.render();

		// verify the email format
		String emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(loginLanguage), "createtasksubject");
		String formattedEmailSubject = String.format(emailSubject, loginUser);
		String emailContent = MailUtil.checkGmail(userCloudEmail,
				userCoundPassword, loginUser, formattedEmailSubject);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		String expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(loginLanguage), "createtaskbody");
		Assert.assertTrue(emailContent.contains(expectedContent1));

		String expectedContent2 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(loginLanguage), "createtaskname");
		String expectedEmailContent2 = String
				.format(expectedContent2, taskName);
		Assert.assertTrue(emailContent.contains(expectedEmailContent2));

		ShareUser.logout(drone);

	}

	private HtmlPage startTaskWorkflow(WebDrone drone, String taskName,
			String assigneeUser, String fileName, String siteName) {
		HtmlPage htmlPage = null;
		ShareUserWorkFlow.selectStartWorkFlowFromMyTasksPage(drone);
		drone.findAndWait(TASK_DROP_DOWN).click();
		By dropDown = By
				.cssSelector("div[id$='default-workflow-definition-menu'] li span.title");
		List<WebElement> liElements = drone.findAndWaitForElements(dropDown);
		liElements.get(0).click();
		NewWorkflowPage newTaskPage = new NewWorkflowPage(drone);
		newTaskPage.render();

		newTaskPage.enterMessageText(taskName);

		drone.findAndWait(SELECT_BUTTON).click();
		AssignmentPage assignmentPage = new AssignmentPage(drone);
		assignmentPage.render();
		List<String> reviewersList = new ArrayList<String>();
		reviewersList.add(assigneeUser);
		assignmentPage.selectReviewers(reviewersList).render();

		drone.findAndWait(ADD_BUTTON).click();
		SelectContentPage selectContentPage = new SelectContentPage(drone);
		selectContentPage.render();
		selectContentPage.addItemFromSite(fileName, siteName);
		selectContentPage.selectOKButton().render();

		htmlPage = newTaskPage.submitWorkflow();

		return htmlPage;
	}

	/**
	 * AONE-12747:Email template - create follow
	 */
	@Test(groups = { "Localization", "CloudOnly" }, timeOut = 400000)
	public void AONE_12747() throws Exception {

		String testUser12747E = getUserNameFreeDomain(testName + "12747E");
		String testUser12747F = getUserNameFreeDomain(testName + "12747F");
		String testUser12747D = getUserNameFreeDomain(testName + "12747D");
		String testUser12747S = getUserNameFreeDomain(testName + "12747S");
		String testUser12747I = getUserNameFreeDomain(testName + "12747I");

		// --- Step 1 ---
		// --- Step action ---
		// As user1@cloud.test set English on the Login page and log in;
		// --- Expected results --
		// The user should be logged in successfully;

		// --- Step 2 ---
		// --- Step action ---
		// Go to People Finder page and search for the user2@cloud.test. Click
		// on the follow button next to user's email;
		// --- Expected results --
		// The follow email should be recieved in the locale of user2 (in French
		// locale);
		verifyEmailForPeopleFinderAndFollow(testUser12747E,
				Language.ENGLISH_US, testUser12747F, Language.FRENCH);

		// --- Step 3 ---
		// --- Step action ---
		// As user2@cloud.test set French on the Login page and log in;
		// --- Expected results --
		// The user should be logged in successful and the page should be
		// displayed in the users locale;

		// --- Step 4 ---
		// --- Step action ---
		// Go to People Finder page and search for the user1@cloud.test. Click
		// on the follow button next to user's email;
		// --- Expected results --
		// The user following email should be recieved in English;
		verifyEmailForPeopleFinderAndFollow(testUser12747F, Language.FRENCH,
				testUser12747E, Language.ENGLISH_US);

		// --- Step 5 ---
		// --- Step action ---
		// Repeat same steps for all the other languages;
		// --- Expected results --
		// All the steps are passed;
		// For DEUTSCHE
		verifyEmailForPeopleFinderAndFollow(testUser12747E,
				Language.ENGLISH_US, testUser12747D, Language.DEUTSCHE);
		verifyEmailForPeopleFinderAndFollow(testUser12747D, Language.DEUTSCHE,
				testUser12747E, Language.ENGLISH_US);

		verifyEmailForPeopleFinderAndFollow(testUser12747E,
				Language.ENGLISH_US, testUser12747S, Language.SPANISH);
		verifyEmailForPeopleFinderAndFollow(testUser12747S, Language.SPANISH,
				testUser12747E, Language.ENGLISH_US);

		verifyEmailForPeopleFinderAndFollow(testUser12747E,
				Language.ENGLISH_US, testUser12747I, Language.ITALIAN);
		verifyEmailForPeopleFinderAndFollow(testUser12747I, Language.ITALIAN,
				testUser12747E, Language.ENGLISH_US);

	}

	/**
	 * AONE-12748:Email template - Password reset
	 */
	@Test(groups = { "Localization", "CloudOnly" })
	public void AONE_12748() throws Exception {
		String emailSubject;
		String emailContent;
		String expectedContent1;
		String expectedContent2;
		CloudForgotPasswordPage forgotPassPage;

		String testUser12748E = getUserNameFreeDomain(testName + "12748E");
		String testUser12748F = getUserNameFreeDomain(testName + "12748F");
		String testUser12748D = getUserNameFreeDomain(testName + "12748D");
		String testUser12748S = getUserNameFreeDomain(testName + "12748S");
		String testUser12748I = getUserNameFreeDomain(testName + "12748I");

		// --- Step 1 ---
		// --- Step action ---
		// Navigate to Forgot password link;
		// --- Expected results --
		// 'Forgot your password?' form is open;

		drone.maximize();
		drone.navigateTo(shareUrl);
		LoginPage login = new LoginPage(drone);
		login.changeLanguage(Language.ENGLISH_US);
		forgotPassPage = login.selectFogotPassordLink();

		// --- Step 2 ---
		// --- Step action ---
		// Type user1@cloud.test email address for English User and click Send
		// Instructions;
		// --- Expected results --
		// The reset password email is received in English;
		forgotPassPage.clickSendInstructions(testUser12748E);

		emailSubject = String.format(PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ENGLISH_US),
				"resetpasswordsubject"), testUser12748E);
		String emailSubjectEncoded = decodingString(emailSubject);
		emailContent = MailUtil.checkGmail(userCloudEmail, userCoundPassword,
				testUser12748E, emailSubjectEncoded);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ENGLISH_US), "resetpasswordbody1");
		String expectedContent1Encoded = decodingString(expectedContent1);
		expectedContent2 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ENGLISH_US), "resetpasswordbody2");
		String expectedContent2Encoded = decodingString(expectedContent2);
		Assert.assertTrue(emailContent.contains(expectedContent1Encoded),
				"Actual email: " + emailContent + " Expected: "
						+ expectedContent1);
		Assert.assertTrue(emailContent.contains(expectedContent2Encoded),
				"Actual email: " + emailContent + " Expected: "
						+ expectedContent2);

		// --- Step 3 ---
		// --- Step action ---
		// Navigate to Forgot password link;
		// --- Expected results --
		// 'Forgot your password?' form is open;

		drone.navigateTo(shareUrl);
		login = new LoginPage(drone);
		login.changeLanguage(Language.FRENCH);
		forgotPassPage = login.selectFogotPassordLink();

		// drone.navigateTo(forgotUrl);
		// forgotPassPage = new CloudForgotPasswordPage(drone);

		// --- Step 4 ---
		// --- Step action ---
		// Type user2@cloud.test email address for French User and click Send
		// Instructions;;
		// --- Expected results --
		// The reset password email is received in French;
		forgotPassPage.clickSendInstructions(testUser12748F);

		emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.FRENCH), "resetpasswordsubject");
		String formattedEmailSubject = String.format(emailSubject,
				testUser12748F);
		emailContent = MailUtil.checkGmail(userCloudEmail, userCoundPassword,
				testUser12748F, formattedEmailSubject);

		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.FRENCH), "resetpasswordbody1");
		expectedContent2 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.FRENCH), "resetpasswordbody2");
		Assert.assertTrue(emailContent.contains(expectedContent1));
		Assert.assertTrue(emailContent.contains(expectedContent2));

		// --- Step 5 ---
		// --- Step action ---
		// Navigate to Forgot password link;
		// --- Expected results --
		// 'Forgot your password?' form is open;

		drone.navigateTo(shareUrl);
		login = new LoginPage(drone);
		login.changeLanguage(Language.DEUTSCHE);
		forgotPassPage = login.selectFogotPassordLink();

		// --- Step 6 ---
		// --- Step action ---
		// Type user3@cloud.test email address for Deutsche User and click Send
		// Instructions;
		// --- Expected results --
		// The reset password email is received in Deutsche;

		forgotPassPage.clickSendInstructions(testUser12748D);

		emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.DEUTSCHE), "resetpasswordsubject");
		formattedEmailSubject = String.format(emailSubject, testUser12748D);
		emailContent = MailUtil.checkGmail(userCloudEmail, userCoundPassword,
				testUser12748D, formattedEmailSubject);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.DEUTSCHE), "resetpasswordbody1");
		expectedContent2 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.DEUTSCHE), "resetpasswordbody2");
		Assert.assertTrue(emailContent.contains(expectedContent1));
		Assert.assertTrue(emailContent.contains(expectedContent2));

		// --- Step 7 ---
		// --- Step action ---
		// Navigate to Forgot password link;
		// --- Expected results --
		// 'Forgot your password?' form is open;

		drone.navigateTo(shareUrl);
		login = new LoginPage(drone);
		login.changeLanguage(Language.ITALIAN);
		forgotPassPage = login.selectFogotPassordLink();

		// --- Step 8 ---
		// --- Step action ---
		// Type user4@cloud.test email address for Italian User and click Send
		// Instructions;
		// --- Expected results --
		// The reset password email is received in Italian;
		forgotPassPage.clickSendInstructions(testUser12748I);

		emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ITALIAN), "resetpasswordsubject");
		formattedEmailSubject = String.format(emailSubject, testUser12748I);
		emailContent = MailUtil.checkGmail(userCloudEmail, userCoundPassword,
				testUser12748I, formattedEmailSubject);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ITALIAN), "resetpasswordbody1");
		expectedContent2 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ITALIAN), "resetpasswordbody2");
		Assert.assertTrue(emailContent.contains(expectedContent1));
		Assert.assertTrue(emailContent.contains(expectedContent2));

		// --- Step 9 ---
		// --- Step action ---
		// Navigate to Forgot password link;
		// --- Expected results --
		// 'Forgot your password?' form is open;

		drone.navigateTo(shareUrl);
		login = new LoginPage(drone);
		login.changeLanguage(Language.SPANISH);
		forgotPassPage = login.selectFogotPassordLink();

		// --- Step 10 ---
		// --- Step action ---
		// Type user5@cloud.test email address for Spanish User and click Send
		// Instructions;
		// --- Expected results --
		// The reset password email is received in Spanish;

		forgotPassPage.clickSendInstructions(testUser12748S);

		emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.SPANISH), "resetpasswordsubject");
		formattedEmailSubject = String.format(emailSubject, testUser12748S);
		emailContent = MailUtil.checkGmail(userCloudEmail, userCoundPassword,
				testUser12748S, formattedEmailSubject);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.SPANISH), "resetpasswordbody1");
		expectedContent2 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.SPANISH), "resetpasswordbody2");
		Assert.assertTrue(emailContent.contains(expectedContent1));
		Assert.assertTrue(emailContent.contains(expectedContent2));

	}

	@Test(groups = { "Localization", "CloudOnly" }, timeOut = 400000)
	public void AONE_12749() throws Exception {
		String userE = getUserNameForDomain(testName + "12749E", testDomain);
		String userF = getUserNameForDomain(testName + "12749F", testDomain);
		String userD = getUserNameForDomain(testName + "12749D", testDomain);
		String userI = getUserNameForDomain(testName + "12749I", testDomain);
		String userJ = getUserNameForDomain(testName + "12749J", testDomain);
		String userS = getUserNameForDomain(testName + "12749S", testDomain);

		// --- Step 1 ---
		// --- Step action ---
		// In the login page choose English. Click on 'Forgot Password' button
		// and enter a user who is not registered;
		// --- Expected results --
		// Validate the email recieved is in English;

		// --- Step 2 ---
		// --- Step action ---
		// In the login page choose French. Click on 'Forgot Password' button
		// and enter a user who is not registered;
		// --- Expected results --
		// Validate the email recieved is in English;

		drone.maximize();
		drone.navigateTo(dronePropertiesMap.get(drone).getShareUrl());
		LoginPage login = new LoginPage(drone);
		String forgotUrl = login.getForgotPasswordURL();
		login.changeLanguage(Language.ENGLISH_US);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userE,
				false);

		// --- Step 3 ---
		// --- Step action ---
		// Repeat same steps for all the other languages;
		// --- Expected results --
		// All the steps are passed;

		login.changeLanguage(Language.FRENCH);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userF,
				false);

		// --- Step 3 ---
		// --- Step action ---
		// Repeat same steps for all the other languages;
		// --- Expected results --
		// All the steps are passed;

		login.changeLanguage(Language.DEUTSCHE);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userD,
				false);

		login.changeLanguage(Language.ITALIAN);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userI,
				false);

		login.changeLanguage(Language.JAPANESE);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userJ,
				false);

		login.changeLanguage(Language.SPANISH);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userS,
				false);
	}

	@Test(groups = { "Localization", "CloudOnly" }, timeOut = 400000)
	public void AONE_12750() throws Exception {
		String userE = getUserNameForDomain(testName + "12750E", testDomain);
		String userF = getUserNameForDomain(testName + "12750F", testDomain);
		String userD = getUserNameForDomain(testName + "12750D", testDomain);
		String userI = getUserNameForDomain(testName + "12750I", testDomain);
		String userJ = getUserNameForDomain(testName + "12750J", testDomain);
		String userS = getUserNameForDomain(testName + "12750S", testDomain);

		// --- Step 1 ---
		// --- Step action ---
		// In the login page choose English. Click on 'Forgot Password' button
		// and enter a user email for whom the invite is sent
		// (user3@cloud.test);
		// --- Expected results --
		// Validate the email recieved is in English;

		// --- Step 2 ---
		// --- Step action ---
		// In the login page choose French. Click on 'Forgot Password' button
		// and enter a user email for whom the invite is sent
		// (user3@cloud.test);
		// --- Expected results --
		// Validate the email recieved is in English;

		drone.maximize();
		drone.navigateTo(dronePropertiesMap.get(drone).getShareUrl());
		LoginPage login = new LoginPage(drone);
		String forgotUrl = login.getForgotPasswordURL();
		login.changeLanguage(Language.ENGLISH_US);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userE, true);

		login.changeLanguage(Language.FRENCH);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userF, true);

		login.changeLanguage(Language.DEUTSCHE);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userD, true);

		login.changeLanguage(Language.ITALIAN);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userI, true);

		login.changeLanguage(Language.JAPANESE);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userJ, true);

		login.changeLanguage(Language.SPANISH);
		operateOnForgotPasswordPage(forgotUrl, Language.ENGLISH_US, userS, true);
	}

	@Test(groups = { "Localization", "CloudOnly" })
	public void AONE_12751() throws Exception {

		String emailSubject;
		String emailSubjectWithUser;
		String emailContent;
		String expectedContent1;

		String testUser12751E = getUserNameFreeDomain(testName + "12751E");
		String testUser12751F = getUserNameFreeDomain(testName + "12751F");
		String testUser12751D = getUserNameFreeDomain(testName + "12751D");
		String testUser12751S = getUserNameFreeDomain(testName + "12751S");
		String testUser12751I = getUserNameFreeDomain(testName + "12751I");

		// --- Step 1 ---
		// --- Step action ---
		// In the Sign Up page enter the activated user email id and request
		// sign up for user1@cloud.test
		// --- Expected results --
		// Validate the email recieved is in English

		String[] testUser1 = new String[] { testUser12751E };

		drone.maximize();
		drone.navigateTo(shareUrl);
		LoginPage loginPage = new LoginPage(drone).render();
		loginPage
				.loginAs(testUser12751E, DEFAULT_PASSWORD, Language.ENGLISH_US);
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);

		emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ENGLISH_US), "registersubject");
		emailSubjectWithUser = String.format(emailSubject, testUser12751E);
		emailContent = MailUtil.checkGmail(userCloudEmail, userCoundPassword,
				testUser12751E, emailSubjectWithUser);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ENGLISH_US), "registerbody1");
		Assert.assertTrue(emailContent.contains(expectedContent1),
				"Email doesn't contain" + expectedContent1);

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ENGLISH_US), "labelcapabilities");
		Assert.assertTrue(emailContent.contains(expectedContent1),
				"Email doesn't contain" + expectedContent1);

		ShareUser.logout(drone);

		// --- Step 2 ---
		// --- Step action ---
		// In the Sign Up page enter user2@cloud.test and request for sign up;
		// --- Expected results --
		// Validate the email recieved is the French language;

		String[] newTestUser2 = new String[] { testUser12751F };

		drone.maximize();
		drone.navigateTo(shareUrl);
		loginPage = new LoginPage(drone).render();
		loginPage.loginAs(testUser12751F, DEFAULT_PASSWORD, Language.FRENCH);
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUser2);

		emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.FRENCH), "registersubject");
		emailSubjectWithUser = String.format(emailSubject, testUser12751F);
		emailContent = MailUtil.checkGmail(userCloudEmail, userCoundPassword,
				testUser12751F, emailSubjectWithUser);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.FRENCH), "registerbody1");
		Assert.assertTrue(emailContent.contains(expectedContent1),
				"Email doesn't contain" + expectedContent1);

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.FRENCH), "registerbody2");
		Assert.assertTrue(emailContent.contains(expectedContent1),
				"Email doesn't contain" + expectedContent1);

		ShareUser.logout(drone);

		// --- Step 3 ---
		// --- Step action ---
		// Repeat same steps for all the other languages;
		// --- Expected results --
		// All the steps are passed;

		String[] newTestUserD = new String[] { testUser12751D };

		drone.maximize();
		drone.navigateTo(shareUrl);
		loginPage = new LoginPage(drone).render();
		loginPage.loginAs(testUser12751D, DEFAULT_PASSWORD, Language.DEUTSCHE);
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserD);

		emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.DEUTSCHE), "registersubject");
		emailSubjectWithUser = String.format(emailSubject, testUser12751D);
		emailContent = MailUtil.checkGmail(userCloudEmail, userCoundPassword,
				testUser12751D, emailSubjectWithUser);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.DEUTSCHE), "registerbody1");
		Assert.assertTrue(emailContent.contains(expectedContent1),
				"Email doesn't contain " + expectedContent1);

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.DEUTSCHE), "registerbody2");
		Assert.assertTrue(emailContent.contains(expectedContent1),
				"Email doesn't contain " + expectedContent1);

		ShareUser.logout(drone);

		// Spanish
		String[] newTestUserS = new String[] { testUser12751S };

		drone.maximize();
		drone.navigateTo(shareUrl);
		loginPage = new LoginPage(drone).render();
		loginPage.loginAs(testUser12751S, DEFAULT_PASSWORD, Language.SPANISH);
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserS);

		emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.SPANISH), "registersubject");
		emailSubjectWithUser = String.format(emailSubject, testUser12751S);
		emailContent = MailUtil.checkGmail(userCloudEmail, userCoundPassword,
				testUser12751S, emailSubjectWithUser);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.SPANISH), "registerbody1");
		Assert.assertTrue(emailContent.contains(expectedContent1),
				"Email doesn't contain" + expectedContent1);

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.SPANISH), "registerbody2");
		Assert.assertTrue(emailContent.contains(expectedContent1),
				"Email doesn't contain" + expectedContent1);

		ShareUser.logout(drone);

		// Italian
		String[] newTestUserI = new String[] { testUser12751I };

		drone.maximize();
		drone.navigateTo(shareUrl);
		loginPage = new LoginPage(drone).render();
		loginPage.loginAs(testUser12751I, DEFAULT_PASSWORD, Language.ITALIAN);
		CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, newTestUserI);

		emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ITALIAN), "registersubject");
		emailSubjectWithUser = String.format(emailSubject, testUser12751I);
		emailContent = MailUtil.checkGmail(userCloudEmail, userCoundPassword,
				testUser12751I, emailSubjectWithUser);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ITALIAN), "registerbody1");
		Assert.assertTrue(emailContent.contains(expectedContent1),
				"Email doesn't contain" + expectedContent1);

		expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ITALIAN), "registerbody2");
		Assert.assertTrue(emailContent.contains(expectedContent1),
				"Email doesn't contain" + expectedContent1);

		ShareUser.logout(drone);

	}

	@Test(groups = { "Localization", "CloudOnly" })
	public void AONE_12752() throws Exception {

		String testUser = getUserNameFreeDomain(testName + "127521");
		String siteName = getSiteName(testName + 1);
		String fileName = "AONE_9831.xlsx";

		// --- Step 1 ---
		// --- Step action ---
		// Validate the feed for english user
		// --- Expected results --
		// Check the activites feed is in english

		ShareUser.login(drone, testUser);
		verifyActivitiesFeed(testUser, siteName, fileName, Language.ENGLISH_US);

		// --- Step 2 ---
		// --- Step action ---
		// Validate the feed for other language user
		// --- Expected results --
		// The feed should be localized in French.

		verifyActivitiesFeed(testUser, siteName, fileName, Language.FRENCH);
		verifyActivitiesFeed(testUser, siteName, fileName, Language.DEUTSCHE);
		verifyActivitiesFeed(testUser, siteName, fileName, Language.ITALIAN);
		verifyActivitiesFeed(testUser, siteName, fileName, Language.SPANISH);

	}

	@Test(groups = { "Localization", "CloudOnly" })
	public void AONE_12754() throws Exception {

		String testUser12754E = getUserNameFreeDomain(testName + "12754E");
		String testUser12754F = getUserNameFreeDomain(testName + "12754F");
		String testUser12754D = getUserNameFreeDomain(testName + "12754D");
		String testUser12754S = getUserNameFreeDomain(testName + "12754S");
		String testUser12754I = getUserNameFreeDomain(testName + "12754I");

		String testUserToInvite1 = getUserNameFreeDomain(testName + "ToInv1");
		String testUserToInvite2 = getUserNameFreeDomain(testName + "ToInv2");
		String testUserToInvite3 = getUserNameFreeDomain(testName + "ToInv3");
		String testUserToInvite4 = getUserNameFreeDomain(testName + "ToInv4");
		String testUserToInvite5 = getUserNameFreeDomain(testName + "ToInv5");

		// --- Step 1 ---
		// --- Step action ---
		// Log in as user1@cloud.test, create a site and invite a new user to
		// the site;
		// Check the email received;
		// --- Expected results --

		verifyEmailForInviteToSite(testUser12754E, testUserToInvite1,
				Language.ENGLISH_US, true);
		verifyEmailForInviteToSite(testUser12754F, testUserToInvite2,
				Language.FRENCH, true);
		verifyEmailForInviteToSite(testUser12754I, testUserToInvite3,
				Language.ITALIAN, true);
		verifyEmailForInviteToSite(testUser12754S, testUserToInvite4,
				Language.SPANISH, true);
		verifyEmailForInviteToSite(testUser12754D, testUserToInvite5,
				Language.DEUTSCHE, true);

	}

//	@Test(groups = { "Localization", "CloudOnly" })
//	public void AONE_12755() throws Exception {
//
//		String testUser12755E = getUserNameFreeDomain(testName + "12755E");
//		String testUser12755F = getUserNameFreeDomain(testName + "12755F");
//		String testUser12755D = getUserNameFreeDomain(testName + "12755D");
//		String testUser12755S = getUserNameFreeDomain(testName + "12755S");
//		String testUser12755I = getUserNameFreeDomain(testName + "12755I");
//
//		verifyEmailForInviteToSite(testUser12755E, testUser12755F,
//				Language.ENGLISH_US, false);
//		verifyEmailForInviteToSite(testUser12754F, testUser12754E, Language.ENGLISH_US, false);
//		verifyEmailForInviteToSite(testUser12754E, testUser12754F, Language.FRENCH, false);
//
//	}

	@Test(groups = { "Localization", "CloudOnly" })
	public void AONE_12757() throws Exception {

		String testUser = getUserNameFreeDomain(testName + "12757E");

		String emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(Language.ENGLISH_US), "signupemail");
		String emailSubjectWithUser = String.format(emailSubject, testUser);
		logger.info(emailSubjectWithUser);

		String emailContent = MailUtil.checkGmail(userCloudEmail,
				userCoundPassword, testUser, emailSubjectWithUser);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");
		logger.info(emailContent);

		String expectedEmailContent = String.format(PropertiesUtil
				.getPropertyValue(getLocalizationFile(Language.ENGLISH_US),
						"activatereminder"));
		logger.info(expectedEmailContent);

		String pattern1 = expectedEmailContent;
		Pattern p = Pattern.compile(pattern1, Pattern.MULTILINE);

		Assert.assertTrue(p.matcher(emailContent).find(),
				"Email doesn't contain " + expectedEmailContent);

	}

	private void verifyActivitiesFeed(String testUser, String siteName,
			String fileName, Language selectedLang) {

		SiteDashboardPage siteDashBoard;
		SiteActivitiesDashlet dashlet;
		String SITE_ACTIVITY = "site-activities";

		ShareUser.changeLanguage(drone, selectedLang);
		siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);

		dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
		siteDashBoard = dashlet.selectUserFilter(
				SiteActivitiesUserFilter.MY_ACTIVITIES).render();

		List<String> activities = dashlet.getSiteActivityDescriptions();

		String addText = " "
				+ PropertiesUtil.getPropertyValue(
						getLocalizationFile(selectedLang),
						"activityadddocument") + " ";
		String addedFeed;
		if (selectedLang != Language.DEUTSCHE)
			addedFeed = testUser + " " + DEFAULT_LASTNAME + addText + fileName;
		else {
			String lastAdded = PropertiesUtil.getPropertyValue(
					getLocalizationFile(selectedLang),
					"activityadddocumentlast");
			addedFeed = testUser + " " + DEFAULT_LASTNAME + addText + fileName
					+ " " + lastAdded;
		}
		Assert.assertTrue(activities.contains(addedFeed),
				"Activities feed does not contain " + addedFeed);
	}

	private void verifyEmailForInviteToSite(String testUser1, String testUser2,
			Language language, boolean verifyAcceptLink) throws Exception {

		// --- Step 1 ---
		// --- Step action ---
		// As user1@cloud.test choose English on the login page and log in;
		// --- Expected results --
		// The user should be logged in successful

		String siteName = testName
				+ language.getLanguageValue().replace("_", "") + "12754124";

		drone.navigateTo(shareUrl);
		LoginPage loginPage = new LoginPage(drone).render();
		loginPage.loginAs(testUser1, DEFAULT_PASSWORD, language);

		// --- Step 2 ---
		// --- Step action ---
		// Create a site and invite a new user to the site;
		// --- Expected results --
		// Invite to site email should be recieved in english and it should be
		// based on the new template with Alfresco banner on top

		ShareUser.createSite(drone, siteName,
				AbstractUtils.SITE_VISIBILITY_PUBLIC).render(5000);

		CreateUserAPI.inviteUserToSite(drone, testUser1, testUser2,
				getSiteShortname(siteName), "SiteCollaborator", "");

		// read from property file the value for the give key
		String formattedEmailSubject;
		String emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(language), "invitationsubject");
		if (language == Language.ENGLISH_US) {
			formattedEmailSubject = String.format(emailSubject, testUser2,
					getUserFullName(testUser1), siteName);
		} else {
			formattedEmailSubject = String.format(emailSubject, testUser2,
					siteName);
		}

		String emailContent = MailUtil.checkGmail(userCloudEmail,
				userCoundPassword, testUser1, formattedEmailSubject);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		String expectedContent = PropertiesUtil.getPropertyValue(
				getLocalizationFile(language), "invitationbody");
		String expectedEmailContent = String.format(expectedContent,
				getUserFullName(testUser1), siteName);
		Assert.assertTrue(emailContent.contains(expectedEmailContent));

		ShareUser.logout(drone);

		if (verifyAcceptLink) {
			// --- Step 3 ---
			// --- Step action ---
			// Copy the activation link and paste in the other browser;
			// --- Expected results --
			// The activation page should be in locale of the email;

			String acceptLink = PropertiesUtil.getPropertyValue(
					getLocalizationFile(language), "invitationacceptlink");
			Pattern p = Pattern.compile(
					"<a href=\".*\">" + acceptLink + "</a>", Pattern.MULTILINE);
			Matcher m = p.matcher(emailContent);
			if (m.find()) {
				String linkAcceptInvitation = m.group()
						.replace("<a href=\"", "")
						.replace("\">Accept Invitation</a>", "");
				verifyAcceptInvitationLink(linkAcceptInvitation,
						Language.ENGLISH_US, testUser1, siteName);
			} else
				Assert.fail("Accept link was not found in email");

		}

	}

	private void verifyAcceptInvitationLink(String linkAcceptInvitation,
			Language language, String user, String site) {
		drone.createNewTab();
		drone.navigateTo(linkAcceptInvitation);

		WebElement elem = drone.findAndWait(By.cssSelector("div.read-flow"));
		String actualInvitation = elem.getText();

		String expectedWelcome = PropertiesUtil.getPropertyValue(
				getLocalizationFile(language), "inviationacceptwelcome");
		String expectedBody = PropertiesUtil.getPropertyValue(
				getLocalizationFile(language), "inviationaccept1");
		String expectedAcceptInvitation = String.format(expectedBody,
				getUserFullName(user), site);

		Assert.assertTrue(actualInvitation.contains(expectedWelcome),
				"Activation page doesn't contain " + expectedWelcome);
		Assert.assertTrue(actualInvitation.contains(expectedAcceptInvitation),
				"Activation page doesn't contain " + expectedAcceptInvitation);

		drone.closeTab();
	}

	private void verifyEmailForLoginWithLangAndInviteSite(Language language,
			String testUser1, String testUser2, boolean verifyAcceptLink)
			throws Exception {

		// --- Step 1 ---
		// --- Step action ---
		// As user1@cloud.test choose English on the login page and log in;
		// --- Expected results --
		// The user should be logged in successful

		String siteName = testName
				+ language.getLanguageValue().replace("_", "") + "127449";

		ShareUser.loginWithLanguage(drone, language, testUser1,
				DEFAULT_PASSWORD).render();

		// --- Step 2 ---
		// --- Step action ---
		// Create a site and invite a new user to the site;
		// --- Expected results --
		// Invite to site email should be recieved in english and it should be
		// based on the new template with Alfresco banner on top

		ShareUser.createSite(drone, siteName,
				AbstractUtils.SITE_VISIBILITY_PUBLIC).render(5000);

		CreateUserAPI.inviteUserToSite(drone, testUser1, testUser2,
				getSiteShortname(siteName), "SiteCollaborator", "");

		// read from property file the value for the give key
		String emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(language), "invitationsubject");
		String formattedEmailSubject = "";

		if (language == Language.ENGLISH_US)
			formattedEmailSubject = String.format(emailSubject, testUser2,
					getUserFullName(testUser1), siteName);
		else
			formattedEmailSubject = decodingString(String.format(emailSubject,
					testUser2, siteName));

		String emailContent = MailUtil.checkGmail(userCloudEmail,
				userCoundPassword, testUser1, formattedEmailSubject);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		String expectedEmailContent = String
				.format(PropertiesUtil.getPropertyValue(
						getLocalizationFile(language), "invitationbody"),
						getUserFullName(testUser1), siteName);
		Assert.assertTrue(emailContent.contains(expectedEmailContent));

		ShareUser.logout(drone);

		if (verifyAcceptLink) {
			// --- Step 3 ---
			// --- Step action ---
			// Copy the activation link and paste in the other browser;
			// --- Expected results --
			// The activation page should be in locale of the email;

			String acceptLink = PropertiesUtil.getPropertyValue(
					getLocalizationFile(language), "invitationacceptlink");
			Pattern p = Pattern.compile(
					"<a href=\".*\">" + acceptLink + "</a>", Pattern.MULTILINE);
			Matcher m = p.matcher(emailContent);
			if (m.find()) {
				String linkAcceptInvitation = m.group()
						.replace("<a href=\"", "")
						.replace("\">Accept Invitation</a>", "");
				verifyAcceptInvitationLink(linkAcceptInvitation, language,
						testUser1, siteName);

			} else
				Assert.fail("Accept link was not found in email");
		}

	}

	private void verifyEmailForPeopleFinderAndFollow(String userLogin,
			Language loginLanguage, String followingUser,
			Language selectedLanguage) {

		// login
		drone.navigateTo(shareUrl);
		LoginPage loginPage = new LoginPage(drone).render();
		// LoginPage loginPage = (LoginPage) drone.getCurrentPage();
		loginPage.loginAs(userLogin, DEFAULT_PASSWORD, loginLanguage);

		// Go to People Finder page and search for the user. Click on the follow
		// button next to user's email;

		DashBoardPage dashBoard = ShareUser.openUserDashboard(drone).render();
		PeopleFinderPage peopleFinderPage = dashBoard.getNav().selectPeople()
				.render();
		PeopleFinderPage resultPage = peopleFinderPage.searchFor(followingUser)
				.render();
		List<ShareLink> searchLinks = resultPage.getResults();
		if (!searchLinks.isEmpty()) {
			resultPage.selectFollowForUser(followingUser);
		} else {
			Assert.fail("User " + followingUser + "  was not found.");
		}

		// verify the email format
		String emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(selectedLanguage), "createfollowsubject");
		String formattedEmailSubject = String.format(emailSubject,
				followingUser, getUserFullName(userLogin));
		// String emailSubjectEncoded = decodingString(selectedLanguage,
		// emailSubject);
		String emailContent = MailUtil.checkGmail(userCloudEmail,
				userCoundPassword, userLogin, formattedEmailSubject);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		String expectedContent = PropertiesUtil.getPropertyValue(
				getLocalizationFile(selectedLanguage), "createfollowbody");
		String expectedEmailContent = String.format(expectedContent,
				getUserDomain(userLogin));
		Assert.assertTrue(emailContent.contains(expectedEmailContent));

		ShareUser.logout(drone);

		drone.closeWindow();

		try {
			super.setup();
		} catch (Exception e) {
			logger.error("New window is not opened");
		}

	}

	// private void findOtherUserAndClickFollowAndCheckEmail(String
	// userToSearch, Language selectedLanguage)
	// {
	// DashBoardPage dashBoard = ShareUser.openUserDashboard(drone).render();
	// PeopleFinderPage peopleFinderPage =
	// dashBoard.getNav().selectPeople().render();
	// PeopleFinderPage resultPage =
	// peopleFinderPage.searchFor(userToSearch).render();
	// List<ShareLink> searchLinks = resultPage.getResults();
	// if (!searchLinks.isEmpty())
	// {
	// resultPage.selectFollowForUser(userToSearch);
	//
	// Assert.assertEquals(resultPage.getTextForFollowButton(userToSearch),
	// "Unfollow");
	// }
	// else
	// {
	// Assert.fail("User " + userToSearch + "  was not found.");
	// }
	//
	// String emailSubject =
	// PropertiesUtil.getPropertyValue(getLocalizationFile(selectedLanguage),
	// "createfollowsubject");
	// String emailSubjectEncoded = decodingString(selectedLanguage,
	// emailSubject);
	// String emailContent = MailUtil.checkGmail(userCloudEmail,
	// userCoundPassword, userToSearch, emailSubjectEncoded);
	//
	// String expectedContent1 =
	// PropertiesUtil.getPropertyValue(getLocalizationFile(selectedLanguage),
	// "createfollowbody");
	// String expectedContentEncoded = decodingString(selectedLanguage,
	// expectedContent1);
	// Assert.assertTrue(emailContent.contains(expectedContentEncoded));
	// }

	public String getLocalizationFile(Language lng) {

		String fileName = lng.getLanguagePropertyFileName();
		return DATA_FOLDER + SLASH + "localization" + SLASH + fileName;

	}

	public String decodingString(String textToDecode) {
		String stringDecoded = "";
		try {
			// ISO-2022-JP,
			// stringDecoded = URLDecoder.decode(new
			// String(textToDecode.getBytes("ISO-8859-1"), "UTF-8"), "UTF-8");
			stringDecoded = URLDecoder.decode(textToDecode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return stringDecoded;
	}

	private void operateOnForgotPasswordPage(String forgotUrl,
			Language selectedLanguage, String userEmail, boolean registred) {
		String emailSubject;
		String emailSubjectWithUser;
		String emailContent;
		String expectedContent1;

		drone.createNewTab();
		drone.navigateTo(forgotUrl);

		CloudForgotPasswordPage forgotPage = new CloudForgotPasswordPage(drone);
		forgotPage.clickSendInstructions(userEmail);

		if (registred == false) {
			emailSubject = PropertiesUtil.getPropertyValue(
					getLocalizationFile(selectedLanguage),
					"forgotpasswordsubject");
			emailSubjectWithUser = String.format(emailSubject, userEmail);
			emailContent = MailUtil.checkGmail(userCloudEmail,
					userCoundPassword, userEmail, emailSubjectWithUser);
			Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

			expectedContent1 = PropertiesUtil.getPropertyValue(
					getLocalizationFile(Language.ENGLISH_US),
					"forgotpasswordbody");
			Assert.assertTrue(emailContent.contains(expectedContent1));
		} else {
			emailSubject = PropertiesUtil.getPropertyValue(
					getLocalizationFile(selectedLanguage), "signupemail");
			emailSubjectWithUser = String.format(emailSubject, userEmail);
			emailContent = MailUtil.checkGmail(userCloudEmail,
					userCoundPassword, userEmail, emailSubjectWithUser);
			Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

			expectedContent1 = PropertiesUtil.getPropertyValue(
					getLocalizationFile(Language.ENGLISH_US),
					"labelcapabilities");
			Assert.assertTrue(emailContent.contains(expectedContent1),
					"Email doesn't contain" + expectedContent1);

			String expectedEmailContent = String.format(PropertiesUtil
					.getPropertyValue(getLocalizationFile(Language.ENGLISH_US),
							"activatereminder"));
			logger.info(expectedEmailContent);

			Pattern p = Pattern
					.compile(expectedEmailContent, Pattern.MULTILINE);
			Assert.assertTrue(p.matcher(emailContent).find(),
					"Email doesn't contain " + expectedEmailContent);
		}

		drone.closeTab();
	}

	private void verifyEmailForInvitePeople(String loginUser,
			Language loginLanguage, String userToInvite) {
		// --- Step 1 ---
		// --- Step action ---
		// As user1@cloud.test choose English language in the Login page and log
		// in;
		// --- Expected results --
		// The user should be logged in successfully;
		drone.maximize();
		drone.navigateTo(shareUrl);
		LoginPage loginPage = new LoginPage(drone).render();
		loginPage.loginAs(loginUser, DEFAULT_PASSWORD, loginLanguage);

		// --- Step 2 ---
		// --- Step action ---
		// Click on Invite People from Account Settings -> Manage users page and
		// type in a validate email id of new user;
		// --- Expected results --
		// Invite people email should be recieved in English and it should be
		// based on the new template with Alfresco banner on top;
		DashBoardPage dashBoard = ShareUser.openUserDashboard(drone);

		UserPage userPage = dashBoard.getNav().selectUserDropdown().render();

		AccountSettingsPage accSettingsPage = userPage
				.selectAccountSettingsPage().render();

		InviteToAlfrescoPage invitePage = accSettingsPage.selectManageUsers()
				.selectInvitePeople().render();
		invitePage.inputEmailsForInvitation(new String[] { userToInvite });
		String messageText = invitePage.getMessageText();

		accSettingsPage = invitePage.selectInvite().render();

		// verify the email format
		String emailSubject = PropertiesUtil.getPropertyValue(
				getLocalizationFile(loginLanguage), "invitepeoplesubject");
		String formattedEmailSubject = String.format(emailSubject,
				userToInvite, getUserFullName(loginUser));
		String emailContent = MailUtil.checkGmail(userCloudEmail,
				userCoundPassword, loginUser, formattedEmailSubject);
		Assert.assertTrue(!emailContent.isEmpty(), "Email was not found!");

		String expectedContent1 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(loginLanguage), "invitepeoplebody1");
		String formattedExpectedContent1 = String.format(expectedContent1,
				getUserFullName(loginUser));
		Assert.assertTrue(emailContent.contains(formattedExpectedContent1));

		String expectedContent2 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(loginLanguage), "invitepeoplebody2");
		String formattedExpectedContent2 = String.format(expectedContent2,
				getUserFullName(loginUser));
		Assert.assertTrue(emailContent.contains(formattedExpectedContent2));

		String expectedContent3 = PropertiesUtil.getPropertyValue(
				getLocalizationFile(loginLanguage), "invitepeoplemessage");
		String formattedExpectedContent3 = String.format(expectedContent3,
				messageText);
		Assert.assertTrue(emailContent.contains(formattedExpectedContent3));

		ShareUser.logout(drone);
	}

	private DocumentLibraryPage selectDocumentLibrary(WebDrone drone) {
		drone.findAndWait(DOCUMENT_LIBRARY).click();
		return new DocumentLibraryPage(drone);
	}

}
