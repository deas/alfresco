package org.alfresco.share.mydashboard.myactivities;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage.ButtonType;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.share.calendar.timezone.CalendarTabsTests;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class includes: Two tests from TestLink in Area:
 * Alfresco Share/My dashboard/My Activities/My Activities dashlet/
 * 
 * @author Cristina Axinte
 */
@Listeners(FailedTestListener.class)
public class MyActivitiesDashletTests extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(CalendarTabsTests.class);
    private String testName;
    private String testUser1;
    private String testUser2;
    private String testUser_2830;
    private String siteName;
    private String siteName_2830;
    String user1_fileName1;
    String user2_fileName1;
    String user1_fileName2;
    String user2_fileName2;
    String user1_fileName3;
    String user2_fileName3;
    String user1_fileName4;
    String user2_fileName4;
    String fileName_2830;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        testName = this.getClass().getSimpleName();
        testUser1 = getUserNameFreeDomain(testName) + 1;
        testUser2 = getUserNameFreeDomain(testName) + 2;
        testUser_2830 = getUserNameFreeDomain(testName) + 2830;
        siteName = getSiteName(testName) + 2829;
        siteName_2830 = getSiteName(testName) + 2830;
        user1_fileName1 = getFileName(testName) + "1-u1" + ".txt";
        user2_fileName1 = getFileName(testName) + "1-u2" + ".txt";
        user1_fileName2 = getFileName(testName) + "2-u1" + ".txt";
        user2_fileName2 = getFileName(testName) + "2-u2" + ".txt";
        user1_fileName3 = getFileName(testName) + "3-u1" + ".txt";
        user2_fileName3 = getFileName(testName) + "3-u2" + ".txt";
        user1_fileName4 = getFileName(testName) + "4-u1" + ".txt";
        user2_fileName4 = getFileName(testName) + "4-u2" + ".txt";
        fileName_2830 = getFileName(testName) + "2830" + ".txt";

        logger.info("Start Tests in: " + testName);

    }

    @Test(groups = { "DataPrepMyActivities" })
    public void dataPrep_AONE_2829() throws Exception
    {

        // Create 2 normal Users
        String[] testUserNew1 = new String[] { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserNew1);
        String[] testUserNew2 = new String[] { testUser2 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserNew2);

        // login with admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create public site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUser.openSiteDashboard(drone, siteName);

        // Users join the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser1, siteName, UserRole.MANAGER);
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser2, siteName, UserRole.MANAGER);

        // login with user1
        ShareUser.login(drone, testUser1);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        // create a file with user1
        ContentDetails contentDetails1 = new ContentDetails();
        contentDetails1.setName(user1_fileName1);
        contentDetails1.setContent("file1 content of user1");
        ShareUser.createContent(drone, contentDetails1, ContentType.PLAINTEXT);

        // logout user1
        ShareUser.logout(drone);

        // login with user2
        ShareUser.login(drone, testUser2);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        // create a file with user2
        ContentDetails contentDetails2 = new ContentDetails();
        contentDetails2.setName(user2_fileName1);
        contentDetails2.setContent("file1 content of user2");
        ShareUser.createContent(drone, contentDetails2, ContentType.PLAINTEXT);

        ShareUser.logout(drone);

    }

    /**
     * AONE-2829:Subscribe to My Activities
     */
    @Test(groups = { "MyActivities", "EnterpriseOnly" })
    public void AONE_2829() throws Exception
    {
        RssFeedPage rssFeedPage;
        SharePage someSharePage;
        MyActivitiesDashlet activitiesDashlet;
        DashBoardPage dashBoard;

        List<String> contentsDisplayedList = new ArrayList<String>();
        List<String> contentsNotDisplayedList = new ArrayList<String>();

        ShareUser.login(drone, testUser1);

        // Step 1 Select My Activities value from drop-down
        dashBoard = ShareUser.openUserDashboard(drone).render(maxWaitTime);
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromUserActivities("My activities").render();

        // Step 2 Subscribe User1 it to the feed
        // Verify User1 activities are displayed in the RSS list
        // Verify User2 activities are not displayed in the RSS list
        contentsDisplayedList.add(String.format("%s %s created document %s in %s", testUser1, DEFAULT_LASTNAME, user1_fileName1, siteName));
        contentsNotDisplayedList.add(user2_fileName1);
        contentsNotDisplayedList.add(user1_fileName2);
        contentsNotDisplayedList.add(user2_fileName2);
        rssFeedPage = openRSSFeedPageAndVerifyActivityInformation(activitiesDashlet, testUser1, contentsDisplayedList, contentsNotDisplayedList);

        // Step 3 Make some activities by the users (e.g. create new content11 by User1 and created new content22 by User2).
        someSharePage = rssFeedPage.clickOnFeedContent(testUser1);
        assertTrue(someSharePage.isLoggedIn(), "Page from rss don't open correct");
        ShareUser.openUserDashboard(drone).render(maxWaitTime);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
        // create a file with user1
        ContentDetails contentDetails12 = new ContentDetails();
        contentDetails12.setName(user1_fileName2);
        contentDetails12.setContent("file2 content of user1");
        ShareUser.createContent(drone, contentDetails12, ContentType.PLAINTEXT);
        ShareUser.logout(drone);

        // login with user2
        ShareUser.login(drone, testUser2);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        // create a file with user2
        ContentDetails contentDetails22 = new ContentDetails();
        contentDetails22.setName(user2_fileName2);
        contentDetails22.setContent("file2 content of user2");
        ShareUser.createContent(drone, contentDetails22, ContentType.PLAINTEXT);
        ShareUser.logout(drone);

        // Step 4 Login with user1
        ShareUser.login(drone, testUser1);
        dashBoard = ShareUser.openUserDashboard(drone).render(maxWaitTime);
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        Assert.assertTrue(activitiesDashlet.isOptionSelected("My activities"), "My activities option is not selected");

        // Enter current user1's credentials and subscribe it to the feed
        // Verify User1 activities are displayed and User2 activities are not displayed in the RSS list
        contentsDisplayedList.add(String.format("%s %s created document %s in %s", testUser1, DEFAULT_LASTNAME, user1_fileName2, siteName));
        contentsNotDisplayedList.remove(user1_fileName2);
        rssFeedPage = openRSSFeedPageAndVerifyActivityInformation(activitiesDashlet, testUser1, contentsDisplayedList, contentsNotDisplayedList);

        // Step 5 Select Everyone else's activities value from drop-down menu and open RSS Feed page
        someSharePage = rssFeedPage.clickOnFeedContent(testUser1);
        dashBoard = dashBoard.getNav().selectMyDashBoard().render();
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromUserActivities("Everyone else\'s activities").render();
        // Step 6 Verify the only others activities (of User2) are displayed in the RSS list
        rssFeedPage = openRSSFeedPageAndVerifyActivityInformation(activitiesDashlet, testUser1, contentsNotDisplayedList, contentsDisplayedList);

        // Step 7 Make some activities by the users (e.g. create new content111 by User1 and created new content222 by User2).
        someSharePage = rssFeedPage.clickOnFeedContent(testUser2);
        ShareUser.openUserDashboard(drone).render(maxWaitTime);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
        // create a file with user1
        ContentDetails contentDetails13 = new ContentDetails();
        contentDetails13.setName(user1_fileName3);
        contentDetails13.setContent("file3 content of user1");
        ShareUser.createContent(drone, contentDetails13, ContentType.PLAINTEXT);
        ShareUser.logout(drone);

        // login with user2
        ShareUser.login(drone, testUser2);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        // create a file with user2
        ContentDetails contentDetails23 = new ContentDetails();
        contentDetails23.setName(user2_fileName3);
        contentDetails23.setContent("file3 content of user2");
        ShareUser.createContent(drone, contentDetails23, ContentType.PLAINTEXT);
        ShareUser.logout(drone);

        // Step 8 login with user1
        ShareUser.login(drone, testUser1);
        dashBoard = ShareUser.openUserDashboard(drone).render(maxWaitTime);
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        Assert.assertTrue(activitiesDashlet.isOptionSelected("Everyone else\'s activities"), "Everyone else\'s activities option is not selected");
        // Verify User1 activities are not displayed and User2 activities are displayed in the RSS list
        contentsDisplayedList.add(String.format("%s %s created document %s in %s", testUser1, DEFAULT_LASTNAME, user1_fileName3, siteName));
        contentsNotDisplayedList.add(user2_fileName3);
        rssFeedPage = openRSSFeedPageAndVerifyActivityInformation(activitiesDashlet, testUser1, contentsNotDisplayedList, contentsDisplayedList);

        // Step 9 Select Everyone's activities value from drop-down menu
        someSharePage = rssFeedPage.clickOnFeedContent(testUser2);
        ShareUser.openUserDashboard(drone).render(maxWaitTime);
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromUserActivities("Everyone\'s activities").render();

        // Step 10 Open RSS Feed Page and verify for User1
        // verify all activities are displayed in the RSS list (created content1,content11,content111 by User1, and content2,content22,content222 by User1);
        contentsDisplayedList.addAll(contentsNotDisplayedList);
        contentsNotDisplayedList.removeAll(contentsNotDisplayedList);
        rssFeedPage = openRSSFeedPageAndVerifyActivityInformation(activitiesDashlet, testUser1, contentsDisplayedList, contentsNotDisplayedList);

        // Step 11 Make some activities by the users (e.g. create new content1111 by User1 and created new content2222 by User2)
        someSharePage = rssFeedPage.clickOnFeedContent(testUser1);
        ShareUser.openUserDashboard(drone).render(maxWaitTime);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);
        // create a file with user1
        ContentDetails contentDetails14 = new ContentDetails();
        contentDetails14.setName(user1_fileName4);
        contentDetails14.setContent("file4 content of user1");
        ShareUser.createContent(drone, contentDetails14, ContentType.PLAINTEXT);
        ShareUser.logout(drone);

        // login with user2
        ShareUser.login(drone, testUser2);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        // create a file with user2
        ContentDetails contentDetails24 = new ContentDetails();
        contentDetails24.setName(user2_fileName4);
        contentDetails24.setContent("file4 content of user2");
        ShareUser.createContent(drone, contentDetails24, ContentType.PLAINTEXT);
        ShareUser.logout(drone);

        // Step 12 login with user1
        ShareUser.login(drone, testUser1);
        dashBoard = ShareUser.openUserDashboard(drone).render(maxWaitTime);
        activitiesDashlet = dashBoard.getDashlet("activities").render();
        Assert.assertTrue(activitiesDashlet.isOptionSelected("Everyone\'s activities"), "Everyone\'s activities option is not selected");

        // Step 12 Open RSS Feed Page and verify for User1
        // verify all activities are displayed in the RSS list (created content1,content11,content111 by User1, and content2,content22,content222 by User1);
        contentsDisplayedList.add(String.format("%s %s created document %s in %s", testUser1, DEFAULT_LASTNAME, user1_fileName4, siteName));
        contentsDisplayedList.add(user2_fileName4);
        rssFeedPage = openRSSFeedPageAndVerifyActivityInformation(activitiesDashlet, testUser1, contentsDisplayedList, contentsNotDisplayedList);

    }

    @Test(groups = { "DataPrepMyActivities" })
    public void dataPrep_AONE_2830() throws Exception
    {
        // Create 2 normal Users
        String[] testUserNew1 = new String[] { testUser_2830 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserNew1);

        // login with admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create public site
        ShareUser.createSite(drone, siteName_2830, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Users join the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser_2830, siteName_2830, UserRole.CONSUMER);

        // login with admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName_2830);

        // create a file with admin
        ContentDetails contentDetails1 = new ContentDetails();
        contentDetails1.setName(fileName_2830);
        contentDetails1.setContent("file1 content of user_2830");
        ShareUser.createContent(drone, contentDetails1, ContentType.PLAINTEXT);

        // logout admin
        ShareUser.logout(drone);
    }

    /**
     * AONE-2830:Notification of comment update (no permissions)
     */
    @Test(groups = { "MyActivities", "EnterpriseOnly" })
    public void AONE_2830() throws Exception
    {

        // Step 1 Admin is logged in
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Step 2 Remove the Inherit Permissions
        DocumentLibraryPage documentLibraryPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName_2830).render();
        DocumentDetailsPage docDetailPage = documentLibraryPage.selectFile(fileName_2830).render();
        ManagePermissionsPage managePermissionsPage = docDetailPage.selectManagePermissions().render();
        managePermissionsPage = managePermissionsPage.toggleInheritPermission(false, ButtonType.Yes).render();
        // Permissions are successfully applied - Inherit Permissions button is disabled
        Assert.assertFalse(managePermissionsPage.isInheritPermissionEnabled(), "The Inherit permission table is displayed.");
        docDetailPage = managePermissionsPage.selectSave().render();

        // Step 3 Create a new comment on the document
        docDetailPage.addComment("new comment1");
        // New comment is created
        documentLibraryPage = docDetailPage.getSiteNav().selectSiteDocumentLibrary().render();
        int commentCount = documentLibraryPage.getCommentCount();
        Assert.assertEquals(commentCount, 1);

        // Step 4 Update the comment
        docDetailPage = documentLibraryPage.selectFile(fileName_2830).render();
        docDetailPage.editComment("new comment1", "new comment1-edited");
        // Comment is updated, only one comment is displayed
        documentLibraryPage = docDetailPage.getSiteNav().selectSiteDocumentLibrary().render();
        commentCount = documentLibraryPage.getCommentCount();
        Assert.assertEquals(commentCount, 1);

        // Step 5 Log in as user created in preconditions
        ShareUser.login(drone, testUser_2830);

        // Step 6 Browse his dashboard and verify 'My Activities' dashlet
        DashBoardPage dashBoard = ShareUser.openUserDashboard(drone).render(maxWaitTime);
        MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromUserActivities("Everyone\'s activities").render();
        Assert.assertTrue(activitiesDashlet.isOptionSelected("Everyone\'s activities"), "Everyone\'s activities option is not selected");
        // No notifications about added and updated comment is presented
        String activityEntry1 = String.format("%s commented on %s in %s", "Administrator", fileName_2830, siteName_2830);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry1, false), "Activity: " + activityEntry1 + "is present");
        String activityEntry2 = String.format("%s updated comment on %s in %s", "Administrator", fileName_2830, siteName_2830);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry2, false), "Activity: " + activityEntry2 + "is present");

    }

    private RssFeedPage openRSSFeedPageAndVerifyActivityInformation(MyActivitiesDashlet dashlet, String userNameToLogin, List<String> contentsDisplayedList, List<String> contentsNotDisplayedList)
    {

        // Enter current user1's credentials and subscribe it to the feed
        RssFeedPage rssFeedPage = dashlet.selectRssFeedPage(userNameToLogin, DEFAULT_PASSWORD).render(maxWaitTime);
        assertEquals(rssFeedPage.getTitle(), String.format("Alfresco Activities User Feed for %s %s", userNameToLogin, DEFAULT_LASTNAME),
                "Rss from activity dashlet didn't opened.");
        assertTrue(rssFeedPage.isSubscribePanelDisplay(), "Subscribe panel in feed don't display.");

        if (!contentsDisplayedList.isEmpty())
        {
            for (String content : contentsDisplayedList)
            {
                // Verify Activities displayed in the RSS list
                rssFeedPage = contentRefreshRetry(drone, rssFeedPage, content);
                assertTrue(rssFeedPage.isDisplayedInFeed(content), String.format("Content %s is not displayed.", content));
            }
        }
        if (!contentsNotDisplayedList.isEmpty())
        {
            for (String content : contentsNotDisplayedList)
            {
                // Verify Activities not displayed in the RSS list
                Assert.assertFalse(rssFeedPage.isDisplayedInFeed(content), String.format("Content %s is displayed.", content));
            }
        }

        return rssFeedPage;
    }

    public static RssFeedPage contentRefreshRetry(WebDrone drone, RssFeedPage rssFeedPage, String contentName)
    {
        int counter = 0;
        int waitInMilliSeconds = 2000;
        int retryRefreshCount = 5;
        while (counter < retryRefreshCount)
        {
            if (rssFeedPage.isDisplayedInFeed(contentName))
            {
                return rssFeedPage;
            }
            counter++;
            drone.refresh();
            rssFeedPage = rssFeedPage.render(maxWaitTime);
            // double wait time to not over do slow search
            waitInMilliSeconds = (waitInMilliSeconds * 2);
            synchronized (SiteUtil.class)
            {
                try
                {
                    SiteUtil.class.wait(waitInMilliSeconds);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        throw new PageException("Content search failed");
    }

}
