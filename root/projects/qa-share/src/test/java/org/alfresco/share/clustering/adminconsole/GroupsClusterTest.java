package org.alfresco.share.clustering.adminconsole;

import org.alfresco.po.share.*;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by maryia.zaichanka on 6/16/14.
 */
public class GroupsClusterTest extends AbstractUtils

{
    private static Log logger = LogFactory.getLog(GroupsClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    private String testUser;
    private String groupName;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception {
        super.setup();
        testName = this.getClass().getSimpleName();

        logger.info("Starting Tests: " + testName);

        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2) {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
        } else {
            throw new PageOperationException("Number of cluster members is less than two");
        }
    }

    @Test(groups = {"EnterpriseOnly"})
    public void AONE_9191() throws Exception {
        testUser = getUserNameFreeDomain(testName);
        groupName = "testgroup";

        // Login as admin
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create a group at server B
        ShareUser.createEnterpriseGroup(drone, groupName);

        // Login at server A, open groups page
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        SharePage sharePage = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        GroupsPage groupPage = sharePage.getNav().getGroupsPage().render();
        groupPage.clickBrowse().render(maxWaitTime);
        drone.getCurrentPage().render(maxWaitTime);
        Assert.assertTrue(groupPage.isGroupPresent(groupName), "Created group isn't present");

        // Click "Edit Group" icon
        EditGroupPage editGroup = groupPage.selectEditGroup(groupName);

        // Fill in "Display Name" field with a correct data
        String newGroupName = groupName + "new";
        editGroup.setDisplayName(newGroupName);

        // Click "Save Changes" button
        editGroup.clickButton(EditGroupPage.ActionButton.SAVE);
        ShareUser.logout(drone);

        // Login at server B and verify group's name
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        sharePage = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        groupPage = sharePage.getNav().getGroupsPage().render();
        groupPage = groupPage.clickBrowse();
        drone.getCurrentPage().render(maxWaitTime);
        Assert.assertTrue(groupPage.isGroupPresent(groupName), "Edited group isn't present");

        ShareUser.logout(drone);

    }

    @Test(groups = {"EnterpriseOnly"})
    public void AONE_9192() throws Exception {
        testUser = getUserNameFreeDomain(testName);
        groupName = "testgroup2";

        // Login as admin
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create a group at server B
        ShareUser.createEnterpriseGroup(drone, groupName);

        // Login at server A, open groups page
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        SharePage sharePage = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        GroupsPage groupPage = sharePage.getNav().getGroupsPage().render();
        groupPage.clickBrowse().render(maxWaitTime);
        drone.getCurrentPage().render(maxWaitTime);
        Assert.assertTrue(groupPage.isGroupPresent(groupName), "Created group isn't present");

        // Click "Delete Group" icon
        DeleteGroupFromGroupPage deleteGroup = groupPage.deleteGroup(groupName);

        // Click "OK" button;
        deleteGroup.clickButton(DeleteGroupFromGroupPage.Action.Yes).render();

        // Login at server B and verify group's name
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        sharePage = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        groupPage = sharePage.getNav().getGroupsPage().render();
        groupPage = groupPage.clickBrowse();
        drone.getCurrentPage().render(maxWaitTime);
        Assert.assertFalse(groupPage.isGroupPresent(groupName), "Group isn't deleted");

        ShareUser.logout(drone);

    }

    @Test(groups = {"EnterpriseOnly"})
    public void AONE_9193() throws Exception {
        testUser = getUserNameFreeDomain(testName);
        groupName = getGroupName(testName);

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create user at server B
        String[] testUserInfo = new String[]{testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Create a group at server B
        ShareUser.createEnterpriseGroup(drone, groupName);

        // Add user form is displayed at server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        SharePage sharePage = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        GroupsPage groupPage = sharePage.getNav().getGroupsPage().render();
        groupPage.clickBrowse();
        groupPage.selectGroup(groupName).render(maxWaitTime);

        AddUserGroupPage addUser = groupPage.selectAddUser().render();

        // Search for created user;
        addUser.searchUser(testUser);

        // Click "Add" button for displayed user
        addUser.clickAddUserButton();
        Assert.assertFalse(addUser.isTitlePresent("Add User"));
        ShareUser.logout(drone);

        // Login to Share on Server B and verify Group page
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        sharePage = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        groupPage = sharePage.getNav().getGroupsPage().render();
        groupPage = groupPage.clickBrowse();
        groupPage = groupPage.selectGroup(groupName).render(maxWaitTime);
        drone.getCurrentPage().render();
        List<String> users = groupPage.getUserList();
        Assert.assertTrue(users.contains(testUser + " " + DEFAULT_LASTNAME + " (" + testUser + ")"), "Added user isn't displayed in a group");

        ShareUser.logout(drone);

    }

    @Test(groups = {"EnterpriseOnly"})
    public void AONE_9194() throws Exception {
        testUser = getUserNameFreeDomain(testName);
        groupName = "testgroup" + 1;

        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create user at server B
        String[] testUserInfo = new String[]{testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Create a group at server B
        ShareUser.createEnterpriseGroup(drone, groupName);

        // Add user form is displayed at server B
        SharePage sharePage = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        GroupsPage groupPage = sharePage.getNav().getGroupsPage().render();
        groupPage.clickBrowse();
        groupPage.selectGroup(groupName).render(maxWaitTime);

        AddUserGroupPage addUser = groupPage.selectAddUser().render();

        // Search for created user;
        addUser.searchUser(testUser);

        // Click "Add" button for displayed user
        addUser.clickAddUserButton();
        Assert.assertFalse(addUser.isTitlePresent("Add User"));
        ShareUser.logout(drone);

        // Login to Share on Server A and verify Group page
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        sharePage = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        groupPage = sharePage.getNav().getGroupsPage().render();
        groupPage = groupPage.clickBrowse();
        groupPage = groupPage.selectGroup(groupName).render(maxWaitTime);
        drone.getCurrentPage().render();
        List<String> users = groupPage.getUserList();
        Assert.assertTrue(users.contains(testUser + " " + DEFAULT_LASTNAME + " (" + testUser + ")"), "Added user isn't displayed in a group");

        // Set cursor on added user and click "Remove user" icon
        RemoveUserFromGroupPage removeUser = groupPage.selectRemoveUser(testUser).render();
        removeUser.selectAction(RemoveUserFromGroupPage.Action.Yes).render();
        ShareUser.logout(drone);

        // Login to Share on Server B and verify Group page
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        sharePage = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        groupPage = sharePage.getNav().getGroupsPage().render();
        groupPage = groupPage.clickBrowse();
        groupPage = groupPage.selectGroup(groupName).render(maxWaitTime);
        drone.getCurrentPage().render();
        Assert.assertEquals(groupPage.hasMembers(), false, "Deleted user is displayed in a group");

        ShareUser.logout(drone);

    }
}
