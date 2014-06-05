/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.sanity;

import org.alfresco.po.share.*;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.po.share.site.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.alfresco.po.share.NewGroupPage.ActionButton.CREATE_GROUP;
import static org.alfresco.po.share.enums.UserRole.*;
import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class MembersSiteTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(MembersSiteTest.class);
    private static final String USER_NAME_IN_EMAIL = "//TBODY/TR/TD/DIV/P[4]/B[1]";
    private static final String PASSWORD_IN_EMAIL = "//TBODY/TR/TD/DIV/P[4]/B[2]";
    private static final String INVITATION_URL_IN_EMAIL = "//TBODY/TR/TD/DIV/P[3]/A";
    private static final String REJECT_INV_URL_IN_EMAIL = "//TBODY/TR/TD/DIV/P[6]/A";

    private String userNameInEmail;
    private String passwordInEmail;
    private String invitationUrlInEmail;
    private String rejectInvUrlInEmail;
    private String currentUrl;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_ALF_3085() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String testUser1 = testUser + "1";
        String testUser2 = testUser + "2";
        String testUser3 = testUser + "3";
        String siteName = getSiteName(testName);

        String group1 = getGroupName(testName) + "1";
        String group2 = getGroupName(testName) + "2";

        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserAdmin.navigateToGroup(drone);
        GroupsPage groupsPage = ShareUserAdmin.browseGroups(drone);
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage();
        groupsPage = (GroupsPage) newGroupPage.createGroup(group1, group1, CREATE_GROUP);
        newGroupPage = groupsPage.navigateToNewGroupPage();
        newGroupPage.createGroup(group2, group2, CREATE_GROUP);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, group1, testUser2);
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, group2, testUser3);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
    }

    @Test(groups = "Sanity")
    public void ALF_3085()
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String testUser1 = testUser + "1";
        String testUser2 = testUser + "2";
        String testUser3 = testUser + "3";
        String siteName = getSiteName(testName);

        String groupName = getGroupName(testName);
        String group1 = groupName + "1";
        String group2 = groupName + "2";

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        SiteDashboardPage siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        InviteMembersPage inviteMembersPage = siteDashboardPage.getSiteNav().selectInvite();
        SiteGroupsPage siteGroupsPage = inviteMembersPage.navigateToSiteGroupsPage();
        AddGroupsPage addGroupsPage = siteGroupsPage.navigateToAddGroupsPage();
        addGroupsPage.searchGroup(group1);
        assertTrue(addGroupsPage.isAddButtonEnabledFor(group1), String.format("Add button for group[%s] is disabled.", group1));
        addGroupsPage.addGroupToSite(group1);
        assertFalse(addGroupsPage.isAddButtonEnabledFor(group1), String.format("Add button for group[%s] is enabled.", group1));
        addGroupsPage.searchGroup(groupName + "*");
        assertTrue(addGroupsPage.isAddButtonEnabledFor(group2), String.format("Add button for group[%s] is disabled.", group2));
        assertFalse(addGroupsPage.isAddButtonEnabledFor(group1), String.format("Add button for group[%s] is enabled.", group1));
        addGroupsPage.addGroupToSite(group2);
        assertTrue(addGroupsPage.isRemoveIconEnabledFor(group1), String.format("RemoveIcon for group[%s] incorrect.", group1));
        assertTrue(addGroupsPage.isRemoveIconEnabledFor(group2), String.format("RemoveIcon for group[%s] incorrect.", group2));
        assertTrue(addGroupsPage.isSelectRoleEnabledFor(group1), String.format("SelectRole for group[%s] incorrect.", group1));
        assertTrue(addGroupsPage.isSelectRoleEnabledFor(group2), String.format("SelectRole for group[%s] incorrect.", group2));
        addGroupsPage.assignRoleToGroup(group2, MANAGER);
        assertFalse(addGroupsPage.isAddGroupsButtonEnabled(), "Add Group button enabled. Can add wrong groups.");
        addGroupsPage.selectRoleForAll(COLLABORATOR);
        assertTrue(addGroupsPage.isAddGroupsButtonEnabled(), "Add Group disabled after select role for all groups");
        addGroupsPage.removeGroupFromAdd(group2);
        assertTrue(addGroupsPage.isAddButtonEnabledFor(group2), String.format("Add button disabled. Can't add group[%s] to site", group2));
        addGroupsPage.clickAddGroupsButton();
        siteGroupsPage = inviteMembersPage.navigateToSiteGroupsPage();
        List<String> searchGroupResults = siteGroupsPage.searchGroup(group1);
        assertEquals(searchGroupResults.size(), 1, String.format("Found more than one group. Search:%s", group1));
        assertEquals(searchGroupResults.get(0), group1, String.format("Wrong group found!"));
        SiteMembersPage siteMembersPage = siteDashboardPage.getSiteNav().selectMembers();
        List<String> members = siteMembersPage.searchUser(testUser2);
        assertEquals(members.size(), 1, String.format("Found more than one user. Search:%s", testUser2));
        assertEquals(members.get(0), testUser2 + " " + testUser2, String.format("Wrong user found!"));
        assertTrue(siteMembersPage.isUserHasRole(testUser2, COLLABORATOR), String.format("Wrong user[%s] Role. Must be %s", testUser2, COLLABORATOR.getRoleName()));
        siteDashboardPage = siteDashboardPage.getSiteNav().selectSiteDashBoard().render();
        SiteMembersDashlet siteMembersDashlet = siteDashboardPage.getDashlet("site-members").render();
        SiteMember siteMember = siteMembersDashlet.selectMember(testUser2);
        assertEquals(siteMember.getRole(), COLLABORATOR, "Wrong added user Role");
        try
        {
            siteMembersDashlet.selectMember(testUser3);
            fail(String.format("Found user[%s] in siteMembersDashlet.", testUser3));
        }
        catch (Exception e)
        {
            logger.info(String.format("Test user %s not member for this site. It's Ok!", testUser3));
        }
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_ALF_3111() throws Exception
    {
        String testName = getTestName();
        String testUser1 = MailUtil.BOT_MAIL_1;
        String testUser2 = MailUtil.BOT_MAIL_2;
        String testUser3 = MailUtil.BOT_MAIL_3;
        String siteName = getSiteName(testName);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser3);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        if (!isAlfrescoVersionCloud(drone))
        {
            MailUtil.configOutBoundEmail();
        }
    }

    @Test(groups = "Sanity")
    public void ALF_3111() throws Exception
    {
        String testName = getTestName();
        String testUser1 = MailUtil.BOT_MAIL_1;
        String testUser2 = MailUtil.BOT_MAIL_2;
        String testUser3 = MailUtil.BOT_MAIL_3;
        String siteName = getSiteName(testName);

        String extFirstName = "Gogi";
        String extLastName = "Gruzinidze";
        String externalEmail = MailUtil.BASE_BOT_MAIL;

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        SiteDashboardPage siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        InviteMembersPage inviteMembersPage = siteDashboardPage.getSiteNav().selectInvite();

        List<String> foundUsers = inviteMembersPage.searchUser(testUser2);
        assertEquals(foundUsers.size(), 1, String.format("Found wrong users user[%s]", testUser2));
        assertEquals(foundUsers.get(0), "(" + testUser2 + ")", String.format("Bad user[%s] found.", foundUsers.get(0)));
        assertTrue(inviteMembersPage.isAddButtonEnabledFor(testUser2), "Add button disabled. Can't add user to site");

        inviteMembersPage.clickAddUser(testUser2);
        assertFalse(inviteMembersPage.isAddButtonEnabledFor(testUser2), String.format("Add button enabled for user[%s] after clicked add.", testUser2));

        foundUsers = inviteMembersPage.searchUser(testUser1);
        assertEquals(foundUsers.size(), 1, String.format("Found more than one user[%s]", testUser1));
        assertEquals(foundUsers.get(0), "(" + testUser1 + ")", String.format("Bad user[%s] found.", foundUsers.get(0)));
        assertFalse(inviteMembersPage.isAddButtonEnabledFor(testUser1), String.format("Add button enabled for invited user[%s].", testUser1));

        foundUsers = inviteMembersPage.searchUser(MailUtil.MAIL_BOT_BASE_NAME + "*");
        assertEquals(foundUsers.size(), 3, "3 user not found!");
        assertFalse(inviteMembersPage.isAddButtonEnabledFor(testUser1), String.format("Add button enabled for invited user[%s].", testUser1));
        assertFalse(inviteMembersPage.isAddButtonEnabledFor(testUser2), String.format("Add button disabled. Can't add user[%s] to site", testUser2));
        assertTrue(inviteMembersPage.isAddButtonEnabledFor(testUser3), String.format("Add button disabled. Can't add user[%s] to site", testUser3));

        inviteMembersPage.clickAddUser(testUser3);
        assertTrue(inviteMembersPage.isSelectRoleEnabledFor(testUser3), String.format("Select role button disabled.", testUser3));
        assertTrue(inviteMembersPage.isRemoveIconEnabledFor(testUser3), String.format("Remove Icon button disabled.", testUser3));
        assertFalse(inviteMembersPage.isAddButtonEnabledFor(testUser1), String.format("Add button enabled for invited user[%s].", testUser1));
        assertFalse(inviteMembersPage.isAddButtonEnabledFor(testUser2), String.format("Add button disabled. Can't add user[%s] to site", testUser2));
        assertFalse(inviteMembersPage.isInviteButtonEnabled(), "Invite button enabled. Can add wrong users to sites.");

        inviteMembersPage.selectRole(testUser2, COORDINATOR);
        assertFalse(inviteMembersPage.isInviteButtonEnabled(), "Invite button enabled. Can add wrong users to sites.");

        inviteMembersPage.selectRoleForAll(COLLABORATOR);
        assertTrue(inviteMembersPage.isInviteButtonEnabled(), "Invite button disabled. Can add users to sites.");

        inviteMembersPage.removeUserFromInvite(testUser3);
        assertTrue(inviteMembersPage.isAddButtonEnabledFor(testUser3), String.format("Add button disabled. Can't add user[%s] to site", testUser3));
        inviteMembersPage.clickInviteButton();
        assertTrue(MailUtil.isMailPresent(testUser2, String.format("You have been invited to join the %s site", siteName)), "Email about invite don't send.");

        DashBoardPage dashBoardPage = ShareUser.login(drone, testUser2, DEFAULT_PASSWORD).render();
        MyTasksDashlet myTasksDashlet = dashBoardPage.getDashlet("my-tasks").render();
        List<ShareLink> tasks = myTasksDashlet.getTasks();
        assertEquals(tasks.get(0).getDescription(),
                String.format("Invitation to join %s site", siteName),
                String.format("Information about invitation task don't display for user[%s]", testUser2));

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        inviteMembersPage = siteDashboardPage.getSiteNav().selectInvite();
        inviteMembersPage.invExternalUser(extFirstName, extLastName, externalEmail, COLLABORATOR);
        String email = MailUtil.getMailAsString(externalEmail, String.format("Alfresco Share: You have been invited to join the %s site", siteName));
        parseMail(email);

        SiteMembersPage siteMembersPage = inviteMembersPage.navigateToMembersSitePage();
        assertEquals(siteMembersPage.searchUser(userNameInEmail).size(), 0, "Can found don't inviting user.");
        PendingInvitesPage pendingInvitesPage = siteMembersPage.navigateToPendingInvites();
        pendingInvitesPage.search(userNameInEmail);
        List<WebElement> invitees = pendingInvitesPage.getInvitees();
        assertEquals(invitees.size(), 1, "Wrong invites count");
        assertTrue(invitees.get(0).getText().contains(userNameInEmail), "Information about external invite missing or wrong.");
        pendingInvitesPage.cancelInvitation(userNameInEmail);
        pendingInvitesPage.search(userNameInEmail);
        invitees = pendingInvitesPage.getInvitees();
        assertEquals(invitees.size(), 0, "Gogi invite found after cancel.");
        checkInvitationUrl(invitationUrlInEmail);

        drone.navigateTo(currentUrl);
        inviteMembersPage = siteDashboardPage.getSiteNav().selectInvite();
        inviteMembersPage.invExternalUser(extFirstName, extLastName, externalEmail, COLLABORATOR);

        email = MailUtil.getMailAsString(externalEmail, String.format("Alfresco Share: You have been invited to join the %s site", siteName));
        parseMail(email);

        rejectInvitation();
        drone.navigateTo(currentUrl);
        siteMembersPage = siteDashboardPage.getSiteNav().selectMembers();
        assertEquals(siteMembersPage.searchUser(userNameInEmail).size(), 0, "User find in members after invitation rejected.");

        inviteMembersPage = siteDashboardPage.getSiteNav().selectInvite();
        inviteMembersPage.invExternalUser(extFirstName, extLastName, externalEmail, COLLABORATOR);
        email = MailUtil.getMailAsString(externalEmail, String.format("Alfresco Share: You have been invited to join the %s site", siteName));
        assertNotNull(email, "Email for external user don't getting.");
        parseMail(email);

        drone.navigateTo(invitationUrlInEmail);
        siteMembersPage = siteDashboardPage.getSiteNav().selectMembers();
        assertEquals(siteMembersPage.searchUser(userNameInEmail).size(), 1, String.format("User %s invited to site", userNameInEmail));
        assertTrue(siteMembersPage.isUserHasRole("Gogi Gruzinidze", COLLABORATOR), "External user has wrong role on site.");
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_ALF_3126() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String testUser1 = testUser + "1";
        String testUser2 = testUser + "2";
        String testUser3 = testUser + "3";
        String siteName = getSiteName(testName);

        String group = getGroupName(testName);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUserAdmin.navigateToGroup(drone);
        GroupsPage groupsPage = ShareUserAdmin.browseGroups(drone);
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage();
        newGroupPage.createGroup(group, group, CREATE_GROUP);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);
        CreateUserAPI.createActivateUserWithGroup(drone, ADMIN_USERNAME, group, testUser3);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, CONTRIBUTOR);
        ShareUserMembers.inviteGroupToSiteWithRole(drone, testUser1, group, siteName, CONSUMER);
    }

    @Test(groups = "Sanity")
    public void ALF_3126()
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String testUser1 = testUser + "1";
        String testUser2 = testUser + "2";
        String testUser3 = testUser + "3";
        String siteName = getSiteName(testName);
        String group = getGroupName(testName);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        SiteDashboardPage siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        SiteMembersPage siteMembersPage = siteDashboardPage.getSiteNav().selectMembers();
        assertTrue(siteMembersPage.isUserHasRole(testUser1, MANAGER), "Wrong role for site administrator.");
        assertTrue(siteMembersPage.isUserHasRole(testUser2, CONTRIBUTOR), String.format("Wrong role fore user[%s]", testUser2));
        assertTrue(siteMembersPage.isUserHasRole(testUser3, CONSUMER), String.format("Wrong role fore user[%s]", testUser3));
        try
        {
            siteMembersPage.assignRole(testUser1, CONSUMER);
            fail("Can change role for manager");
        }
        catch (Exception e)
        {
            logger.info("Can't change role for site manager! It's Ok.");
        }
        siteMembersPage.assignRole(testUser2, COLLABORATOR);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName);
        SiteMembersDashlet siteMembersDashlet = siteDashboardPage.getDashlet("site-members").render();
        SiteMember siteMember = siteMembersDashlet.selectMember(testUser2);
        assertEquals(siteMember.getRole(), COLLABORATOR, String.format("User[%s] role didn't change.", testUser2));

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        siteMembersPage = siteDashboardPage.getSiteNav().selectMembers();
        siteMembersPage.removeUser(testUser2);

        ShareUser.login(drone, testUser2, DEFAULT_PASSWORD);
        try
        {
            ShareUser.openSiteDashboard(drone, siteName);
            fail("Possible navigate to site. User don't removes from members.");
        }
        catch (Exception e)
        {
            logger.info("Can't navigate to site manager! It's Ok.");
        }

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        siteMembersPage = siteDashboardPage.getSiteNav().selectMembersPage();
        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups();
        List<String> siteGroups = siteGroupsPage.searchGroup("");
        assertEquals(siteGroups.size(), 1, "Wrong groups count displayed.");
        assertEquals(siteGroups.get(0), group, "Information about site group don't correct.");
        siteGroupsPage.assignRole(group, CONTRIBUTOR);
        assertTrue(siteGroupsPage.isAssignRolePresent(group), "Group role don't changed.");
        siteMembersPage = siteDashboardPage.getSiteNav().selectMembersPage();
        assertTrue(siteMembersPage.isUserHasRole(testUser3, CONTRIBUTOR), String.format("User[%s](who added to group) role don't changed.", testUser3));

        ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        siteMembersDashlet = siteDashboardPage.getDashlet("site-members").render();
        siteMember = siteMembersDashlet.selectMember(testUser3);
        assertEquals(siteMember.getRole(), CONTRIBUTOR, String.format("User[%s] role didn't change.", testUser3));

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        siteMembersPage = siteDashboardPage.getSiteNav().selectMembersPage();
        siteGroupsPage = siteMembersPage.navigateToSiteGroups();
        siteGroupsPage.removeGroup(group);
        siteGroups = siteGroupsPage.searchGroup(group);
        assertEquals(siteGroups.size(), 0, "Group Found after deleting.");
        siteMembersPage = siteDashboardPage.getSiteNav().selectMembersPage();
        List<String> siteMembers = siteMembersPage.searchUser(testUser3);
        assertEquals(siteMembers.size(), 0, "User found in members after deleting.");

        ShareUser.login(drone, testUser3, DEFAULT_PASSWORD);
        try
        {
            ShareUser.openSiteDashboard(drone, siteName);
            fail("Possible navigate to site. User don't removes from members.");
        }
        catch (Exception e)
        {
            logger.info("Can't navigate to site manager! It's Ok.");
        }

    }

    private void checkInvitationUrl(String url)
    {
        currentUrl = drone.getCurrentUrl();
        drone.navigateTo(url);
        assertTrue(drone.findAndWait(By.xpath("//h1[text()='Processing invite acceptance failed']")).isDisplayed(), "Invitation link still work.");
    }

    private void rejectInvitation()
    {
        currentUrl = drone.getCurrentUrl();
        drone.navigateTo(rejectInvUrlInEmail);
        drone.findAndWait(By.xpath("//button[text()='Yes']")).click();
        WebElement declineElem = drone.findAndWait(By.xpath("//div[contains(@id,'declined')]/p[1]"));
        assertTrue(declineElem.getText().contains("site has been rejected."), "Don't reject invitation");
    }

    private void parseMail(String email) throws IOException, SAXException
    {
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(email)));
        DOMReader reader = new DOMReader();
        Document document = reader.read(parser.getDocument());
        try
        {
            userNameInEmail = document.selectSingleNode(USER_NAME_IN_EMAIL).getText();
            passwordInEmail = document.selectSingleNode(PASSWORD_IN_EMAIL).getText();
            invitationUrlInEmail = document.selectSingleNode(INVITATION_URL_IN_EMAIL).getText();
            rejectInvUrlInEmail = document.selectSingleNode(REJECT_INV_URL_IN_EMAIL).getText();
        }
        catch (NullPointerException e)
        {
            System.out.println("Bad email: ");
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(System.out, format);
            writer.write(document);
            fail("Can.t parse email about external inviting.");
        }
    }

}
