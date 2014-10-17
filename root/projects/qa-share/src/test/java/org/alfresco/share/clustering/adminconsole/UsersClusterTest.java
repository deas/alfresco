package org.alfresco.share.clustering.adminconsole;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.*;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import java.util.List;

/**
 * Created by maryia.zaichanka on 5/23/14.
 */
public class UsersClusterTest extends AbstractUtils

{
    private static Log logger = LogFactory.getLog(UsersClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    private String testUser;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        logger.info("Starting Tests: " + testName);

        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2)
        {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
        }
        else
        {
            throw new PageOperationException("Number of cluster members is less than two");
        }
    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_15185() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create user at server A
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);

        // Verify User is visible at server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        DashBoardPage dPage = drone.getCurrentPage().render();

        UserSearchPage searchPage = dPage.getNav().getUsersPage().render();
        UserSearchPage results = searchPage.searchFor(testUser).render();
        Assert.assertTrue(results.hasResults(), "User isn't successfully created");
        Assert.assertTrue(results.isUserPresent(testUser), "User isn't successfully created");

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_15186() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String groupName = "Test" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Group on server B
        ShareUser.createEnterpriseGroup(drone, groupName);
        ShareUser.logout(drone);

        // Log in at server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create user with membership in a group created at server B
        ShareUser.createEnterpriseUserWithGroup(drone, ADMIN_USERNAME, testUser, testUser, DEFAULT_LASTNAME, DEFAULT_PASSWORD, groupName);
        ShareUser.logout(drone);

        // Log in at server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        DashBoardPage dPage = drone.getCurrentPage().render();
        GroupsPage groupPage = dPage.getNav().getGroupsPage().render();
        groupPage.clickBrowse();
        groupPage.selectGroup(groupName).render(maxWaitTime);
        drone.getCurrentPage().render();
        Assert.assertTrue(groupPage.hasMembers(), "Group members aren't present");
        List<UserProfile> userProfiles = groupPage.getMembersList();
        for (UserProfile userProfile : userProfiles)
        {
            Assert.assertTrue(userProfile.getUsername().contains(testUser), "User isn't a member of group");
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_15187() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Fill all the mandatory fields on create new user page, disable user account
        DashBoardPage dPage = drone.getCurrentPage().render();
        UserSearchPage userSearchPage = dPage.getNav().getUsersPage().render();
        NewUserPage newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.selectDisableAccount();
        newUserPage.createEnterpriseUser(testUser, testUser, DEFAULT_LASTNAME, testUser, DEFAULT_PASSWORD);

        // Search for a created user
        UserSearchPage searchPage = drone.getCurrentPage().render();
        UserSearchPage results = searchPage.searchFor(testUser).render();
        Assert.assertTrue(results.hasResults(), "User isn't successfully created");
        Assert.assertTrue(results.isUserPresent(testUser), "User isn't successfully created");
        ShareUser.logout(drone);

        // Log in as recently created user on server B
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        LoginPage loginPage = drone.getCurrentPage().render();
        loginPage.loginAs(testUser, DEFAULT_PASSWORD);
        Assert.assertFalse(loginPage.isLoggedIn(), "User is able to log in");
        Assert.assertTrue(loginPage.getErrorMessage().length() > 1, "Friendly notification isn't displayed");

        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_15188() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create user at server B
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Log in at server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Search for created user
        DashBoardPage dPage = drone.getCurrentPage().render();

        UserSearchPage searchPage = dPage.getNav().getUsersPage().render();
        searchPage = searchPage.searchFor(testUser).render();
        UserProfilePage userProfilePage = searchPage.clickOnUser(testUser).render();
        EditUserPage editUserPage = userProfilePage.selectEditUser().render();

        // Fill "First Name" field with a correct data
        editUserPage.editFirstName(testUser + 1);

        // Fill "Last Name" field with a correct data
        editUserPage.editLastName(DEFAULT_LASTNAME + 1);

        // Fill "Email" field with a correct data
        editUserPage.editEmail(testUser + 1);

        // Click "Save Changes" button
        editUserPage.saveChanges().render();

        // User login to Share on server B and verify changes in user's profile
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        dPage = drone.getCurrentPage().render();

        MyProfilePage myProfilePage = dPage.getNav().selectMyProfile().render();
        Assert.assertEquals(myProfilePage.getUserName(), testUser + 1 + " " + DEFAULT_LASTNAME + 1, "User's name isn't changed");
        Assert.assertEquals(myProfilePage.getEmailName(), testUser + 1, "User's email isn't changed");

        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_15189() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create user at server B
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Log in at server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Search for created user
        DashBoardPage dPage = drone.getCurrentPage().render();

        UserSearchPage searchPage = dPage.getNav().getUsersPage().render();
        searchPage = searchPage.searchFor(testUser).render(maxWaitTime);
        UserProfilePage userProfilePage = searchPage.clickOnUser(testUser).render(maxWaitTime);
        EditUserPage editUserPage = userProfilePage.selectEditUser().render();

        // Enter into "New Password" field any valid string
        editUserPage.editPassword(DEFAULT_PASSWORD + 1);

        // Confirm the password in "Verify Password" field
        editUserPage.editVerifyPassword(DEFAULT_PASSWORD + 1);

        // Click "Save Changes" button
        editUserPage.saveChanges().render();
        ShareUser.logout(drone);

        // Try to log in as the user with new password on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        LoginPage loginPage = drone.getCurrentPage().render();
        loginPage.loginAs(testUser, DEFAULT_PASSWORD + 1);
        loginPage.isLoggedIn();
        ShareUser.logout(drone);

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_15190() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        // Login as admin at server B
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create user
        // Fill all the mandatory fields on create new user page, disable user account
        DashBoardPage dPage = drone.getCurrentPage().render();
        UserSearchPage userSearchPage = dPage.getNav().getUsersPage().render();
        NewUserPage newUserPage = userSearchPage.selectNewUser().render();
        newUserPage.selectDisableAccount();
        newUserPage.createEnterpriseUser(testUser, testUser, DEFAULT_LASTNAME, testUser, DEFAULT_PASSWORD);

        ShareUser.logout(drone);

        // Log in at server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Search for a created user
        dPage = drone.getCurrentPage().render();
        UserSearchPage searchPage = dPage.getNav().getUsersPage().render();
        searchPage = searchPage.searchFor(testUser).render();
        UserProfilePage userProfilePage = searchPage.clickOnUser(testUser).render();
        EditUserPage editUserPage = userProfilePage.selectEditUser().render();

        // Enable user
        editUserPage.deSelectDisableAccount();

        // Click "Save Changes" button
        editUserPage.saveChanges().render();
        ShareUser.logout(drone);

        // Log in as recently created user on server B
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        LoginPage loginPage = drone.getCurrentPage().render();
        loginPage.loginAs(testUser, DEFAULT_PASSWORD);
        Assert.assertTrue(loginPage.isLoggedIn(), "User isn't able to log in");

        ShareUser.logout(drone);
    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_15191() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create user at server B
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        ShareUser.logout(drone);

        // Log in at server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Search for created user
        DashBoardPage dPage = drone.getCurrentPage().render();

        UserSearchPage searchPage = dPage.getNav().getUsersPage().render();
        searchPage = searchPage.searchFor(testUser).render();
        Assert.assertTrue(searchPage.hasResults(), "User isn't successfully created");
        UserProfilePage userProfilePage = searchPage.clickOnUser(testUser).render();
        userProfilePage.deleteUser().render(maxDownloadWaitTime);
        UserSearchPage searchPageNew = drone.getCurrentPage().render(maxWaitTime);

        Assert.assertFalse(searchPageNew.hasResults(), "User isn't successfully deleted");
        ShareUser.logout(drone);

        // User is Logged in to Share on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        LoginPage loginPage = drone.getCurrentPage().render();
        loginPage.loginAs(testUser, DEFAULT_PASSWORD);
        Assert.assertFalse(loginPage.isLoggedIn(), "User is able to log in");
        Assert.assertTrue(loginPage.getErrorMessage().length() > 1, "Friendly notification isn't displayed");

        ShareUser.logout(drone);

    }

}
