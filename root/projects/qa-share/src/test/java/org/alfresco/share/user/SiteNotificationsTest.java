/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.user;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter;
import org.alfresco.po.share.dashlet.SiteActivitiesUserFilter;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.user.Language;
import org.alfresco.po.share.user.LanguageSettingsPage;
import org.alfresco.po.share.user.NotificationPage;
import org.alfresco.po.share.user.UserSiteItem;
import org.alfresco.po.share.user.UserSitesPage;
import org.alfresco.share.site.document.TableViewDocLibTest;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ActivityType;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserProfile;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.WebDroneType;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Jamie Allison
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class SiteNotificationsTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(TableViewDocLibTest.class);

    private static final String JOIN_SITE_ACTIVITY_FORMAT = "%s %s joined site %s with role %s";
    private static final String LEAVE_SITE_ACTIVITY_FORMAT = "%s %s left site %s";
    private static final String ADD_DOCUMENT_ACTIVITY_FORMAT = "%s %s added document %s in %s";
    private static final String LIKE_DOCUMENT_ACTIVITY_FORMAT = "%s %s liked document %s in %s";

    private static final String SITE_ADD_DOCUMENT_ACTIVITY_FORMAT = "%s %s added document %s";
    private static final String SITE_CREATE_DOCUMENT_ACTIVITY_FORMAT = "%s %s created document %s";
    private static final String SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT = "%s %s liked document %s";
    private static final String SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT = "%s %s commented on %s";
    private static final String SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT = "%s %s deleted a comment from %s";

    private String gTestAdmin;
    private String gSiteName;
    private String gTestUser1;
    private String gTestUser2;

    private boolean isCloud = false;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        // Setup usernames and site name for tests ALF-15252 and ALF-15253.
        gTestAdmin = getUserNameFreeDomain(testName);
        gTestUser1 = getUserNameFreeDomain(testName + "1");
        gTestUser2 = getUserNameFreeDomain(testName + "2");
        gSiteName = getSiteName(testName) + System.currentTimeMillis();

        AlfrescoVersion version = drone.getProperties().getVersion();
        isCloud = version.isCloud();

        logger.info("Starting Tests: " + testName);
    }

    @AfterClass(alwaysRun = true)
    public void pauseForEmails()
    {
        // TODO: Shan: Add a wait in Webdrone to allow emails to be sent.
    }

    @Test(groups = { "DataPrepSiteNotification", "EnterpriseOnly" })
    public void dataPrep_ALF_10684() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String testUser2 = getUserNameFreeDomain(testName + "2");
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };

        // Create Users
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);
    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_10684() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String testUser2 = getUserNameFreeDomain(testName + "2");
        String siteName1 = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String siteName2 = getSiteName(testName + "-2-") + System.currentTimeMillis();
        String siteName3 = getSiteName(testName + "-3-") + System.currentTimeMillis();
        String siteName4 = getSiteName(testName + "-4-") + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-1";
        String fileName2 = getFileName(testName) + "-2";
        String fileName3 = getFileName(testName) + "-3";
        String fileName4 = getFileName(testName) + "-4";
        File file1 = newFile(fileName1, "New file 1");
        File file2 = newFile(fileName2, "New file 2");
        File file3 = newFile(fileName3, "New file 3");
        File file4 = newFile(fileName4, "New file 4");

        // User login
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName1, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName2, UserRole.COLLABORATOR);

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName3, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName4, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Start Test
        ShareUserProfile.navigateToNotifications(drone);
        ShareUserProfile.setNotificationStatus(drone, false, false);
        NotificationPage notificationPage = ShareUserProfile.navigateToNotifications(drone);

        assertTrue(notificationPage.isNotificationFeedChecked(), "Notification Feed emails should be enabled.");

        // Site 1
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.likeContent(drone, fileName1);

        // Site 2
        ShareUser.openSitesDocumentLibrary(drone, siteName2);
        ShareUserSitePage.uploadFile(drone, file2);
        ShareUserSitePage.likeContent(drone, fileName2);

        // Site 3
        ShareUser.openSitesDocumentLibrary(drone, siteName3);
        ShareUserSitePage.uploadFile(drone, file3);
        ShareUserSitePage.likeContent(drone, fileName3);

        // Site 4
        ShareUser.openSitesDocumentLibrary(drone, siteName4);
        ShareUserSitePage.uploadFile(drone, file4);
        ShareUserSitePage.likeContent(drone, fileName4);

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUserSitePage.likeContent(drone, fileName1);

        ShareUser.openSitesDocumentLibrary(drone, siteName2);
        ShareUserSitePage.likeContent(drone, fileName2);

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        String activity1 = String.format(JOIN_SITE_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, siteName1, UserRole.COLLABORATOR.getRoleName());
        String activity2 = String.format(JOIN_SITE_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, siteName2, UserRole.COLLABORATOR.getRoleName());
        String activity3 = String.format(ADD_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName1, siteName1);
        String activity4 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName1, siteName1);
        String activity5 = String.format(ADD_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName2, siteName2);
        String activity6 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName2, siteName2);
        String activity7 = String.format(ADD_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName3, siteName3);
        String activity8 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName3, siteName3);
        String activity9 = String.format(ADD_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName4, siteName4);
        String activity10 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName4, siteName4);
        String activity11 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser1, DEFAULT_LASTNAME, fileName1, siteName1);
        String activity12 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser1, DEFAULT_LASTNAME, fileName2, siteName2);

        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity12, true), "Could not find activity: " + activity12);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity11, true), "Could not find activity: " + activity11);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity10, true), "Could not find activity: " + activity10);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity9, true), "Could not find activity: " + activity9);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity8, true), "Could not find activity: " + activity8);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity7, true), "Could not find activity: " + activity7);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity6, true), "Could not find activity: " + activity6);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity5, true), "Could not find activity: " + activity5);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity4, true), "Could not find activity: " + activity4);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity3, true), "Could not find activity: " + activity3);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity2, true), "Could not find activity: " + activity2);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity1, true), "Could not find activity: " + activity1);

        // TODO: Set emails to be sent to specific account and enable for tests.
        // TODO: TESTLINK : Emails have to be checked manually.

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteNotification", "EnterpriseOnly" })
    public void dataPrep_ALF_10686() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String testUser2 = getUserNameFreeDomain(testName + "2");
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };

        // Create Users
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);
    }

    @Test(groups = "EnterpriseOnly")
    public void ALF_10686() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String testUser2 = getUserNameFreeDomain(testName + "2");
        String siteName1 = getSiteName(testName + "-1-") + System.currentTimeMillis();
        String siteName2 = getSiteName(testName + "-2-") + System.currentTimeMillis();
        String siteName3 = getSiteName(testName + "-3-") + System.currentTimeMillis();
        String siteName4 = getSiteName(testName + "-4-") + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-1";
        String fileName2 = getFileName(testName) + "-2";
        String fileName3 = getFileName(testName) + "-3";
        String fileName4 = getFileName(testName) + "-4";
        File file1 = newFile(fileName1, "New file 1");
        File file2 = newFile(fileName2, "New file 2");
        File file3 = newFile(fileName3, "New file 3");
        File file4 = newFile(fileName4, "New file 4");

        // User login
        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName1, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName2, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName1, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName2, UserRole.COLLABORATOR);

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName3, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.createSite(drone, siteName4, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Start Test
        ShareUserProfile.navigateToNotifications(drone);
        ShareUserProfile.setNotificationStatus(drone, false, true);
        NotificationPage notificationPage = ShareUserProfile.navigateToNotifications(drone);

        assertFalse(notificationPage.isNotificationFeedChecked(), "Notification Feed emails should be disabled.");

        // Site 1
        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.likeContent(drone, fileName1);

        // Site 2
        ShareUser.openSitesDocumentLibrary(drone, siteName2);
        ShareUserSitePage.uploadFile(drone, file2);
        ShareUserSitePage.likeContent(drone, fileName2);

        // Site 3
        ShareUser.openSitesDocumentLibrary(drone, siteName3);
        ShareUserSitePage.uploadFile(drone, file3);
        ShareUserSitePage.likeContent(drone, fileName3);

        // Site 4
        ShareUser.openSitesDocumentLibrary(drone, siteName4);
        ShareUserSitePage.uploadFile(drone, file4);
        ShareUserSitePage.likeContent(drone, fileName4);

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName1);
        ShareUserSitePage.likeContent(drone, fileName1);

        ShareUser.openSitesDocumentLibrary(drone, siteName2);
        ShareUserSitePage.likeContent(drone, fileName2);

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);

        String activity1 = String.format(JOIN_SITE_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, siteName1, UserRole.COLLABORATOR.getRoleName());
        String activity2 = String.format(JOIN_SITE_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, siteName2, UserRole.COLLABORATOR.getRoleName());
        String activity3 = String.format(ADD_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName1, siteName1);
        String activity4 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName1, siteName1);
        String activity5 = String.format(ADD_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName2, siteName2);
        String activity6 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName2, siteName2);
        String activity7 = String.format(ADD_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName3, siteName3);
        String activity8 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName3, siteName3);
        String activity9 = String.format(ADD_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName4, siteName4);
        String activity10 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, fileName4, siteName4);
        String activity11 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser1, DEFAULT_LASTNAME, fileName1, siteName1);
        String activity12 = String.format(LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser1, DEFAULT_LASTNAME, fileName2, siteName2);

        // Check activities have been logged. Emails should not be sent in this
        // test but The activities will still appear in the activities dashlet.
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity12, true), "Could not find activity: " + activity12);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity11, true), "Could not find activity: " + activity11);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity10, true), "Could not find activity: " + activity10);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity9, true), "Could not find activity: " + activity9);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity8, true), "Could not find activity: " + activity8);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity7, true), "Could not find activity: " + activity7);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity6, true), "Could not find activity: " + activity6);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity5, true), "Could not find activity: " + activity5);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity4, true), "Could not find activity: " + activity4);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity3, true), "Could not find activity: " + activity3);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity2, true), "Could not find activity: " + activity2);
        assertTrue(ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activity1, true), "Could not find activity: " + activity1);

        // TODO: Set emails to be sent to specific account and enable for tests.
        // TODO: TESTLINK : Emails have to be checked manually.

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteNotification", "AlfrescoOne" })
    public void dataPrep_ALF_15249() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String testUser2 = getUserNameFreeDomain(testName + "2");
        String testUser3 = getUserNameFreeDomain(testName + "3");
        String[] testAdminInfo = new String[] { testAdmin };
        String[] testUserInfo1 = new String[] { testUser1 };
        String[] testUserInfo2 = new String[] { testUser2 };
        String[] testUserInfo3 = new String[] { testUser3 };

        // Create Users
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testAdminInfo);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo3);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_15249() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String testUser2 = getUserNameFreeDomain(testName + "2");
        String testUser3 = getUserNameFreeDomain(testName + "3");
        String siteName = getSiteName(testName) + System.currentTimeMillis();

        // User login
        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUserProfile.navigateToNotifications(drone);
        ShareUserProfile.setNotificationStatus(drone, true, true);

        ShareUserProfile.navigateToUserSites(drone);
        ShareUserProfile.setSiteFeedStatus(drone, siteName, true);

        // Start Test
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testAdmin, testUser1, siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testAdmin, testUser2, siteName, UserRole.CONTRIBUTOR);

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToLeaveSite(drone, siteName);

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, siteName);

        ShareUser.logout(drone);

        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);
        ShareUserMembers.removeSiteMember(drone, testUser3, siteName);

        ShareUser.openSiteDashboard(drone, siteName);

        String activity1 = String.format(JOIN_SITE_ACTIVITY_FORMAT, testUser1, DEFAULT_LASTNAME, siteName, UserRole.COLLABORATOR.getRoleName());
        String activity2 = String.format(JOIN_SITE_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, siteName, UserRole.CONTRIBUTOR.getRoleName());
        String activity3 = String.format(LEAVE_SITE_ACTIVITY_FORMAT, testUser2, DEFAULT_LASTNAME, siteName);
        String activity4 = String.format(JOIN_SITE_ACTIVITY_FORMAT, testUser3, DEFAULT_LASTNAME, siteName, UserRole.CONSUMER.getRoleName());
        String activity5 = String.format(LEAVE_SITE_ACTIVITY_FORMAT, testUser3, DEFAULT_LASTNAME, siteName);

        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity5, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity5);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity4, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity4);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity3, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity3);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity2);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity1);

        // TODO: Set emails to be sent to specific account and enable for tests.
        // TODO: TESTLINK : Emails have to be checked manually.

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteNotification", "AlfrescoOne" })
    public void dataPrep_ALF_15250() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + "1");
        String[] testAdminInfo = new String[] { testAdmin };
        String[] testUserInfo1 = new String[] { testUser1 };

        // Create Users
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testAdminInfo);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_15250() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + "1");

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-1";
        String fileName2 = getFileName(testName) + "-2";
        String fileName3 = getFileName(testName) + "-3";
        File file1 = newFile(fileName1, "New file 1");
        File file2 = newFile(fileName2, "New file 2");

        // User login
        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUserProfile.navigateToNotifications(drone);
        ShareUserProfile.setNotificationStatus(drone, true, true);

        ShareUserProfile.navigateToUserSites(drone);
        ShareUserProfile.setSiteFeedStatus(drone, siteName, true);

        // Start Test
        ShareUser.openSiteDashboard(drone, siteName);

        ShareUserMembers.inviteUserToSiteWithRole(drone, testAdmin, testUser1, siteName, UserRole.COLLABORATOR);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.uploadFile(drone, file1);
        ShareUserSitePage.uploadFile(drone, file2);

        DetailsPage detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName1);

        detailsPage.selectLike();
        detailsPage.addComment("Comment 1");
        detailsPage.editComment("Comment 1", "Updated comment 1");
        detailsPage.saveEditComments();
        detailsPage.deleteComment("Updated comment 1");

        ShareUser.logout(drone);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        if (isCloud)
        {
            File file3 = newFile(fileName3, "New file 3");
            ShareUserSitePage.uploadFile(drone, file3);

            detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName3);
        }
        else
        {
            ContentDetails contentDetails = new ContentDetails();
            contentDetails.setName(fileName3);
            contentDetails.setTitle(fileName3);
            contentDetails.setDescription(fileName3);

            detailsPage = ShareUserSitePage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, siteName, "");
        }

        detailsPage.selectLike();
        detailsPage.addComment("Comment 2");
        detailsPage.editComment("Comment 2", "Updated comment 2");
        detailsPage.saveEditComments();
        detailsPage.deleteComment("Updated comment 2");

        ShareUser.logout(drone);

        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        String activity1, activity2, activity3, activity4, activity5, activity6 = "";
        if (isCloud)
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity2 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName2);
            activity3 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity4 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity5 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity6 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testUser1, DEFAULT_LASTNAME, fileName3);
        }
        else
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity2 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName2);
            activity3 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity4 = String.format(SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity5 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity6 = String.format(SITE_CREATE_DOCUMENT_ACTIVITY_FORMAT, testUser1, DEFAULT_LASTNAME, fileName3);
        }
        String activity7 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testUser1, DEFAULT_LASTNAME, fileName3);
        String activity8 = String.format(SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT, testUser1, DEFAULT_LASTNAME, fileName3);
        String activity9 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testUser1, DEFAULT_LASTNAME, fileName3);

        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity9, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity9);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity8, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity8);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity7, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity7);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity6, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity6);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity5, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity5);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity4, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity4);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity3, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity3);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity2);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity1);

        // TODO: Set emails to be sent to specific account and enable for tests.
        // TODO: TESTLINK : Emails have to be checked manually.

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteNotification", "AlfrescoOne", "NonGrid" })
    public void dataPrep_ALF_15251() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String[] testAdminInfo = new String[] { testAdmin };
        String siteName = getSiteName(testName);

        // Create User
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testAdminInfo);

        // Site creation
        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.logout(drone);
    }

    @Test(groups = {"AlfrescoOne", "NonGrid"})
    public void ALF_15251() throws Exception
    {
        WebDrone thisDrone;
        
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);

        setupCustomDrone(WebDroneType.FrenchDrone);
        if(isCloud)
        {
            thisDrone = drone;
            
            ShareUser.login(thisDrone, testAdmin, DEFAULT_PASSWORD);
            LanguageSettingsPage languagePage = ShareUserProfile.navigateToLanguage(thisDrone);
            languagePage.changeLanguage(Language.FRENCH);
        }
        else
        {
            thisDrone = customDrone;
            
            ShareUser.login(thisDrone, testAdmin, DEFAULT_PASSWORD);
        }
        
        ShareUserProfile.navigateToUserSites(thisDrone);
        UserSitesPage userSitesPage = ShareUserProfile.setSiteFeedStatus(thisDrone, siteName, true);

        UserSiteItem userSiteItem = userSitesPage.getSite(siteName);

        assertEquals(userSiteItem.getActivityFeedButtonLabel(), customDrone.getValue("user.profile.sites.disable.activity.feeds"));
        
        ShareUser.logout(thisDrone);
    }

    @Test(groups = { "DataPrepSiteNotification", "AlfrescoOne" })
    public void dataPrep_ALF_15252() throws Exception
    {
        String[] testAdminInfo = new String[] { gTestAdmin };
        String[] testUserInfo1 = new String[] { gTestUser1 };
        String[] testUserInfo2 = new String[] { gTestUser2 };

        // Create Users
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testAdminInfo);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo2);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_15252() throws Exception
    {
        String testName = getTestName();

        String fileName1 = getFileName(testName) + "-1";
        File file1 = newFile(fileName1, "New file 1");

        // User login
        ShareUser.login(drone, gTestAdmin, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, gSiteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUserProfile.navigateToNotifications(drone);
        ShareUserProfile.setNotificationStatus(drone, true, true);

        ShareUserProfile.navigateToUserSites(drone);
        ShareUserProfile.setSiteFeedStatus(drone, gSiteName, false);

        // Start Test
        ShareUser.openSitesDocumentLibrary(drone, gSiteName);
        ShareUserSitePage.uploadFile(drone, file1);

        DetailsPage detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName1);

        detailsPage.selectLike();
        detailsPage.addComment("Comment 1");

        ShareUser.login(drone, gTestUser1, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, gSiteName);

        ShareUser.logout(drone);

        ShareUser.login(drone, gTestAdmin, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, gSiteName);

        String activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, gTestAdmin, gTestAdmin, fileName1);
        String activity2 = String.format(JOIN_SITE_ACTIVITY_FORMAT, gTestUser1, DEFAULT_LASTNAME, gSiteName, UserRole.CONSUMER.getRoleName());

        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, gSiteName, ActivityType.DESCRIPTION),
                    "Activity should be disabled for site. Found: " + activity1);
        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, gSiteName, ActivityType.DESCRIPTION),
                    "Activity should be disabled for site. Found: " + activity2);
    }

    @Test(dependsOnMethods = "ALF_15252", groups = "AlfrescoOne")
    public void ALF_15253() throws Exception
    {
        String testName = getTestName();

        String fileName2 = getFileName(testName) + "-2";
        File file2 = newFile(fileName2, "New file 2");

        // User login
        ShareUser.login(drone, gTestAdmin, DEFAULT_PASSWORD);

        ShareUserProfile.navigateToNotifications(drone);
        ShareUserProfile.setNotificationStatus(drone, true, true);

        ShareUserProfile.navigateToUserSites(drone);
        ShareUserProfile.setSiteFeedStatus(drone, gSiteName, true);

        ShareUser.logout(drone);
        ShareUser.login(drone, gTestAdmin, DEFAULT_PASSWORD);

        // Start Test
        ShareUser.openSitesDocumentLibrary(drone, gSiteName);
        ShareUserSitePage.uploadFile(drone, file2);

        DetailsPage detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName2);

        detailsPage.selectLike();
        detailsPage.addComment("Comment 2");

        ShareUser.logout(drone);

        ShareUser.login(drone, gTestUser2, DEFAULT_PASSWORD);
        ShareUserMembers.userRequestToJoinSite(drone, gSiteName);

        ShareUser.logout(drone);

        ShareUser.login(drone, gTestAdmin, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, gSiteName);

        String activity1, activity2, activity3 = "";
        if (isCloud)
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, gTestAdmin, DEFAULT_LASTNAME, fileName2);
            activity2 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, gTestAdmin, DEFAULT_LASTNAME, fileName2);
            activity3 = String.format(SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT, gTestAdmin, DEFAULT_LASTNAME, fileName2);
        }
        else
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, gTestAdmin, gTestAdmin, fileName2);
            activity2 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, gTestAdmin, gTestAdmin, fileName2);
            activity3 = String.format(SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT, gTestAdmin, gTestAdmin, fileName2);
        }
        String activity4 = String.format(JOIN_SITE_ACTIVITY_FORMAT, gTestUser2, DEFAULT_LASTNAME, gSiteName, UserRole.CONSUMER.getRoleName());

        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity4, true, gSiteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity4);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity3, true, gSiteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity3);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, gSiteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity2);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, gSiteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity1);
    }

    @Test(groups = { "DataPrepSiteNotification", "AlfrescoOne" })
    public void dataPrep_ALF_15255() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String[] testAdminInfo = new String[] { testAdmin };

        // Create User
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testAdminInfo);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_15255() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-1";
        String fileName2 = getFileName(testName) + "-2";
        File file1 = newFile(fileName1, "New file 1");
        File file2 = newFile(fileName2, "New file 2");

        // User login
        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Ensure that Notifications are enabled
        ShareUserProfile.navigateToNotifications(drone);
        ShareUserProfile.setNotificationStatus(drone, true, true);

        ShareUserProfile.navigateToUserSites(drone);
        ShareUserProfile.setSiteFeedStatus(drone, siteName, true);

        // Start Test
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.uploadFile(drone, file1);

        DetailsPage detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName1);

        detailsPage.selectLike();
        detailsPage.addComment("Comment 1");
        detailsPage.deleteComment("Comment 1");

        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);

        String activity1, activity2, activity3, activity4 = "";
        if (isCloud)
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity2 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity3 = String.format(SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity4 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
        }
        else
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity2 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity3 = String.format(SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity4 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
        }

        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity4, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity4);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity3, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity3);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity2);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity1);

        SiteActivitiesDashlet dashlet = siteDashboardPage.getDashlet(SITE_ACTIVITIES).render();
        siteDashboardPage = dashlet.selectUserFilter(SiteActivitiesUserFilter.IM_FOLLOWING).render();

        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName, ActivityType.DESCRIPTION),
                    "Activity should not be displayed. Found: " + activity1);

        dashlet = siteDashboardPage.getDashlet(SITE_ACTIVITIES).render();
        siteDashboardPage = dashlet.selectUserFilter(SiteActivitiesUserFilter.MY_ACTIVITIES).render();
        dashlet = siteDashboardPage.getDashlet(SITE_ACTIVITIES).render();
        siteDashboardPage = dashlet.selectTypeFilter(SiteActivitiesTypeFilter.COMMENTS).render();

        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName, ActivityType.DESCRIPTION),
                    "Activity should not be displayed. Found: " + activity1);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity3, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity3);

        dashlet = siteDashboardPage.getDashlet(SITE_ACTIVITIES).render();
        siteDashboardPage = dashlet.selectTypeFilter(SiteActivitiesTypeFilter.ALL_ITEMS).render();

        ShareUserProfile.navigateToUserSites(drone);
        ShareUserProfile.setSiteFeedStatus(drone, siteName, false);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.uploadFile(drone, file2);

        detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName2);

        detailsPage.selectLike();
        detailsPage.addComment("Comment 2");
        detailsPage.deleteComment("Comment 2");

        ShareUser.openSiteDashboard(drone, siteName);

        activity1 = activity2 = activity3 = activity4 = "";
        if (isCloud)
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName2);
            activity2 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName2);
            activity3 = String.format(SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName2);
            activity4 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName2);
        }
        else
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName2);
            activity2 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName2);
            activity3 = String.format(SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName2);
            activity4 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName2);
        }

        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName, ActivityType.DESCRIPTION),
                    "Activity should be disabled for site. Found: " + activity1);
        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, siteName, ActivityType.DESCRIPTION),
                    "Activity should be disabled for site. Found: " + activity2);
        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity3, true, siteName, ActivityType.DESCRIPTION),
                    "Activity should be disabled for site. Found: " + activity3);
        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity4, true, siteName, ActivityType.DESCRIPTION),
                    "Activity should be disabled for site. Found: " + activity4);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteNotification", "AlfrescoOne" })
    public void dataPrep_ALF_15258() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String[] testAdminInfo = new String[] { testAdmin };

        // Create User
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testAdminInfo);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_15258() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-1";
        File file1 = newFile(fileName1, "New file 1");

        // User login
        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUserProfile.navigateToNotifications(drone);
        ShareUserProfile.setNotificationStatus(drone, true, true);

        // Ensure that Notifications are disabled for site
        ShareUserProfile.navigateToUserSites(drone);
        ShareUserProfile.setSiteFeedStatus(drone, siteName, false);

        SiteDashboardPage siteDashboard = ShareUser.openSiteDashboard(drone, siteName);

        CustomiseSiteDashboardPage customiseSiteDashboardPage = siteDashboard.getSiteNav().selectCustomizeDashboard().render();

        customiseSiteDashboardPage.remove(Dashlets.SITE_ACTIVITIES);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.uploadFile(drone, file1);

        DetailsPage detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName1);

        detailsPage.selectLike();
        detailsPage.addComment("Comment 1");
        detailsPage.deleteComment("Comment 1");

        siteDashboard = ShareUser.openSiteDashboard(drone, siteName);

        customiseSiteDashboardPage = siteDashboard.getSiteNav().selectCustomizeDashboard().render();

        siteDashboard = customiseSiteDashboardPage.addDashlet(Dashlets.SITE_ACTIVITIES, 2);

        ShareUser.openSiteDashboard(drone, siteName);

        String activity1, activity2, activity3, activity4 = "";
        if (isCloud)
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity2 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity3 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity4 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
        }
        else
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity2 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity3 = String.format(SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity4 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
        }

        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName, ActivityType.DESCRIPTION),
                    "Activity should be disabled for site. Found: " + activity1);
        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, siteName, ActivityType.DESCRIPTION),
                    "Activity should be disabled for site. Found: " + activity2);
        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity3, true, siteName, ActivityType.DESCRIPTION),
                    "Activity should be disabled for site. Found: " + activity3);
        assertFalse(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity4, true, siteName, ActivityType.DESCRIPTION),
                    "Activity should be disabled for site. Found: " + activity4);

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteNotification", "AlfrescoOne" })
    public void dataPrep_ALF_15259() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);
        String[] testAdminInfo = new String[] { testAdmin };

        // Create User
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testAdminInfo);
    }

    @Test(groups = "AlfrescoOne")
    public void ALF_15259() throws Exception
    {
        String testName = getTestName();
        String testAdmin = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName) + System.currentTimeMillis();

        String fileName1 = getFileName(testName) + "-1";
        File file1 = newFile(fileName1, "New file 1");

        // User login
        ShareUser.login(drone, testAdmin, DEFAULT_PASSWORD);

        // Site creation
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        ShareUserProfile.navigateToNotifications(drone);
        ShareUserProfile.setNotificationStatus(drone, true, true);

        // Ensure that Notifications are disabled for site
        ShareUserProfile.navigateToUserSites(drone);
        ShareUserProfile.setSiteFeedStatus(drone, siteName, true);

        SiteDashboardPage siteDashboard = ShareUser.openSiteDashboard(drone, siteName);

        CustomiseSiteDashboardPage customiseSiteDashboardPage = siteDashboard.getSiteNav().selectCustomizeDashboard().render();

        customiseSiteDashboardPage.remove(Dashlets.SITE_ACTIVITIES);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.uploadFile(drone, file1);

        DetailsPage detailsPage = ShareUserSitePage.getContentDetailsPage(drone, fileName1);

        detailsPage.selectLike();
        detailsPage.addComment("Comment 1");
        detailsPage.deleteComment("Comment 1");

        siteDashboard = ShareUser.openSiteDashboard(drone, siteName);

        customiseSiteDashboardPage = siteDashboard.getSiteNav().selectCustomizeDashboard().render();

        siteDashboard = customiseSiteDashboardPage.addDashlet(Dashlets.SITE_ACTIVITIES, 2);

        ShareUser.openSiteDashboard(drone, siteName);

        String activity1, activity2, activity3, activity4 = "";
        if (isCloud)
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity2 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity3 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
            activity4 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, DEFAULT_LASTNAME, fileName1);
        }
        else
        {
            activity1 = String.format(SITE_ADD_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity2 = String.format(SITE_LIKE_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity3 = String.format(SITE_COMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
            activity4 = String.format(SITE_UNCOMMENT_DOCUMENT_ACTIVITY_FORMAT, testAdmin, testAdmin, fileName1);
        }

        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity4, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity4);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity3, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity3);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity2, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity2);
        assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activity1, true, siteName, ActivityType.DESCRIPTION),
                    "Could not find activity: " + activity1);

        ShareUser.logout(drone);
    }
}