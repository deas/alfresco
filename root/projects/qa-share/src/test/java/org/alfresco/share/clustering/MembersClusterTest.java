package org.alfresco.share.clustering;

import org.alfresco.po.share.*;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.*;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.alfresco.po.share.enums.UserRole.COLLABORATOR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Created by maryia.zaichanka on 5/29/14.
 */
public class MembersClusterTest extends AbstractUtils

{
    private static Log logger = LogFactory.getLog(MembersClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    private String testUser;
    private String siteName;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    protected String user = "AONE_15865";
    protected String group = "Group" + getRandomString(3);

    private static final String USER_NAME_IN_EMAIL = "//DIV/P[4]/B[1]";
    private static final String PASSWORD_IN_EMAIL = "//DIV/P[4]/B[2]";
    private static final String INVITATION_URL_IN_EMAIL = "//DIV/P[3]/A";
    private String userNameInEmail;
    private String passwordInEmail;
    private String invitationUrlInEmail;

    private void parseExternalInvitationMail(String email) throws IOException, SAXException {
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(email)));
        DOMReader reader = new DOMReader();
        Document document = reader.read(parser.getDocument());
        try {
            userNameInEmail = document.selectSingleNode(USER_NAME_IN_EMAIL).getText();
            passwordInEmail = document.selectSingleNode(PASSWORD_IN_EMAIL).getText();
            invitationUrlInEmail = document.selectSingleNode(INVITATION_URL_IN_EMAIL).getText();
        } catch (NullPointerException e) {
            System.out.println("Bad email: ");
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(System.out, format);
            writer.write(document);
            fail("Can.t parse email about external inviting.");
        }
    }


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
    public void AONE_9145() throws Exception {

        testUser = getUserNameFreeDomain(user);
        siteName = getSiteName(user);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create site at server A
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Create user at server A
        String[] testUserInfo = new String[]{testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);

        // login at server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName).render();

        // Search for User1 and click Add button for him;
        // For added users press Select role drop-down menu;
        // Choose Site Manager role;
        // Click Invite button;
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser, siteName, UserRole.MANAGER);
        ShareUser.logout(drone);

        // Login at server B as invited user
        ShareUser.login(drone, testUserInfo).render();
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);

        // Verify a user is present at Site Members dashlet with a chosen role
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();

        SiteMember siteMember = dashlet.selectMember(testUser);
        Assert.assertEquals(siteMember.getRole(), UserRole.MANAGER, "User isn't present at a dashlet with correct role");
        ShareUser.logout(drone);

    }

    @Test(groups = {"EnterpriseOnly"}, alwaysRun = true, dependsOnMethods = {"AONE_9145"})
    public void AONE_9146() throws Exception {

        testUser = getUserNameFreeDomain(user);
        siteName = getSiteName(user);

        // Log in at server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Site Members page is opened
        // Choose User1 and change his role to Site Collaborator
        SiteDashboardPage siteDashboard = ShareUser.openSiteDashboard(drone, siteName).render();
        SiteMembersDashlet dashlet = siteDashboard.getDashlet("site-members").render();
        SiteMembersPage membersPage = dashlet.clickAllMembers().render();
        membersPage.assignRole(testUser, UserRole.COLLABORATOR);
        ShareUser.logout(drone);

        // Created user is logged in to share on Server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Verify a user is present at Site Members dashlet with a chosen role
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);
        dashlet = siteDashBoard.getDashlet("site-members").render();

        SiteMember siteMember = dashlet.selectMember(testUser);
        Assert.assertEquals(siteMember.getRole(), UserRole.COLLABORATOR, "User isn't present at a dashlet with correct role");
        ShareUser.logout(drone);

    }

    @Test(groups = {"EnterpriseOnly"}, alwaysRun = true, dependsOnMethods = {"AONE_9146"})
    public void AONE_9147() throws Exception {
        testUser = getUserNameFreeDomain(user);
        siteName = getSiteName(user);

        // Log in at server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Site Members page is opened
        // Choose User1 and change his role to Site Manager
        SiteDashboardPage siteDashboard = ShareUser.openSiteDashboard(drone, siteName).render();
        SiteMembersDashlet dashlet = siteDashboard.getDashlet("site-members").render();
        SiteMembersPage membersPage = dashlet.clickAllMembers().render();
        membersPage.assignRole(testUser, UserRole.MANAGER);

        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);
        dashlet = siteDashBoard.getDashlet("site-members").render();

        SiteMember siteMember = dashlet.selectMember(testUser);
        Assert.assertEquals(siteMember.getRole(), UserRole.MANAGER, "User isn't present at a dashlet with correct role");

        // Open Site Members page, remove User
        siteDashboard = ShareUser.openSiteDashboard(drone, siteName).render();
        dashlet = siteDashboard.getDashlet("site-members").render();
        membersPage = dashlet.clickAllMembers().render();
        membersPage.removeUser(testUser).render();
        ShareUser.logout(drone);

        // Created user is logged in to share on Server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        SharePage sharePage = ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Verify a user isn't present at Site Members dashlet with a chosen role
        SiteFinderPage siteFinderPage = sharePage.getNav().selectSearchForSites().render();
        siteFinderPage = siteFinderPage.searchForSite(siteName).render();

        siteDashBoard = siteFinderPage.selectSite(siteName).render();
        dashlet = siteDashBoard.getDashlet("site-members").render();
        Assert.assertFalse(dashlet.getMembers().contains(testUser), "User isn't removed from site");

        ShareUser.logout(drone);
    }

    @Test(groups = {"EnterpriseOnly"})
    public void AONE_9148() throws Exception {

        String extFirstName = "ExternalUser";
        String extLastName = DEFAULT_LASTNAME;
        String externalEmail = "alfrescoautoqa@gmail.com";

        siteName = getSiteName(getTestName());

        // Log in at server A
        dronePropertiesMap.get(drone).setShareUrl(node1Url);
        MailUtil.configOutBoundEmail();
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Open Invite Page
        SiteDashboardPage siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        InviteMembersPage inviteMembersPage = siteDashboardPage.getSiteNav().selectInvite();

        // Fill in all the mandatory fields for external user, chose role, invite
        inviteMembersPage.invExternalUser(extFirstName, extLastName, externalEmail, COLLABORATOR);

        // Check received message in mail inbox
        String email = MailUtil.getMailAsString(externalEmail, String.format("Alfresco Share: You have been invited to join the %s site", siteName));
        parseExternalInvitationMail(email);
        drone.navigateTo(invitationUrlInEmail);
        SiteMembersPage siteMembersPage = siteDashboardPage.getSiteNav().selectMembers();
        assertEquals(siteMembersPage.searchUser(userNameInEmail).size(), 1, String.format("User %s invited to site", userNameInEmail));
        assertTrue(siteMembersPage.isUserHasRole("ExternalUser " + DEFAULT_LASTNAME, COLLABORATOR), "External user has wrong role on site.");
        ShareUser.logout(drone);

        // Log in at server B with credentials from invite email
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, userNameInEmail, passwordInEmail);
        ShareUser.logout(drone);


    }

    @Test(groups = {"EnterpriseOnly"})
    public void AONE_9149() throws Exception {
        testName = getTestName();
        testUser = getUserNameFreeDomain(user + 1);
        siteName = getSiteName(testName);
        String groupName = "Group" + getRandomString(3);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create 4 groups with at least 1 member in each at server A
        for (int i = 0; i < 4; i++) {
            ShareUser.createEnterpriseGroup(drone, groupName + i);
            ShareUser.createEnterpriseUserWithGroup(drone, ADMIN_USERNAME, testUser + i, testUser + i, DEFAULT_LASTNAME, DEFAULT_PASSWORD, groupName + i);
        }
        ShareUser.logout(drone);

        // Log in at server B as User1
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, testUser + 0, DEFAULT_PASSWORD);

        // Create site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        InviteMembersPage membersPage = dashboard.getSiteNav().selectInvite();
        SiteGroupsPage siteGroups = membersPage.navigateToSiteGroupsPage().render();
        AddGroupsPage addGroupsToSite = siteGroups.navigateToAddGroupsPage().render();
        String[] userRoles = new String[]{UserRole.MANAGER.toString(), UserRole.COLLABORATOR.toString(), UserRole.CONTRIBUTOR.toString(), UserRole.CONSUMER.toString()};

        // Select Manager role for the first group (e.g. for Group1);
        // Select Collaborator role for the second group (e.g. for Group2);
        // Select Contributor role for the third group (e.g. for Group3);
        // Select Consumer role for the fourth group (e.g. for Group4);
        // Click "Add Groups" button

        for (int i = 0; i < 4; i++) {
            addGroupsToSite.addGroup(groupName + i, UserRole.valueOf(userRoles[i]));
        }
        ShareUser.logout(drone);

        // Log in as a user added for each group and verify his rights for site on server A;
        for (int i = 0; i < 4; i++) {
            dronePropertiesMap.get(drone).setShareUrl(node1Url);
            ShareUser.login(drone, testUser + i, DEFAULT_PASSWORD);

            // Verify a user is present at Site Members dashlet with a chosen role
            dashboard = ShareUser.openSiteDashboard(drone, siteName).render();
            SiteMembersDashlet dashlet = dashboard.getDashlet("site-members").render();

            SiteMember siteMember = dashlet.selectMember(testUser + i);
            Assert.assertEquals(siteMember.getRole(), UserRole.valueOf(userRoles[i]), "User isn't present at a dashlet with correct role");
            ShareUser.logout(drone);
        }

    }

    @Test(groups = {"EnterpriseOnly"})
    public void AONE_9150() throws Exception {
        testName = getTestName();
        testUser = getUserNameFreeDomain(user + 1);
        siteName = getSiteName(user + 1);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create any group with
        ShareUser.createEnterpriseGroup(drone, group);

        // Create a user with a membership ia the created group
        ShareUser.createEnterpriseUserWithGroup(drone, ADMIN_USERNAME, testUser, testUser, DEFAULT_LASTNAME, DEFAULT_PASSWORD, group);

        // Create site, add group to the created site with Manager role
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        SiteDashboardPage dashboard = drone.getCurrentPage().render();
        InviteMembersPage membersPage = dashboard.getSiteNav().selectInvite();
        SiteGroupsPage siteGroups = membersPage.navigateToSiteGroupsPage().render();
        AddGroupsPage addGroupsToSite = siteGroups.navigateToAddGroupsPage().render();

        addGroupsToSite.addGroup(group, UserRole.MANAGER);

        // Search for the created group
        dashboard = ShareUser.openSiteDashboard(drone, siteName);
        SiteMembersDashlet dashlet = dashboard.getDashlet("site-members").render();
        SiteMembersPage siteMembersPage = dashlet.clickAllMembers().render();
        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups().render();
        siteGroupsPage.searchGroup(group);
        siteGroupsPage.assignRole(group, UserRole.COLLABORATOR).render();
        ShareUser.logout(drone);

        // Log in as user added to the group and verify his rights for the site on server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        dashboard = ShareUser.openSiteDashboard(drone, siteName);
        dashlet = dashboard.getDashlet("site-members").render();

        SiteMember siteMember = dashlet.selectMember(testUser);
        Assert.assertEquals(siteMember.getRole(), UserRole.COLLABORATOR, "User isn't present at a dashlet with correct role");
        ShareUser.logout(drone);

    }

    @Test(groups = {"EnterpriseOnly"}, alwaysRun = true, dependsOnMethods = "AONE_9150")
    public void AONE_9151() throws Exception {
        testName = getTestName();
        testUser = getUserNameFreeDomain(user + 1);
        siteName = getSiteName(user + 1);

        // Login as Admin at server A
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Search for created group at Site Members page, remove group
        SiteMembersDashlet dashlet = dashboard.getDashlet("site-members").render();
        SiteMembersPage siteMembersPage = dashlet.clickAllMembers().render();
        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups().render();
        siteGroupsPage.searchGroup(group);
        siteGroupsPage.removeGroup(group);
        ShareUser.logout(drone);

        // Log in as Member of the deleted group at server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Verify a user isn't present at Site Members dashlet with a chosen role
        DashBoardPage mainDashboard = drone.getCurrentPage().render();
        MySitesDashlet mySitesDashlet = mainDashboard.getDashlet("my-sites").render();
        Assert.assertFalse(mySitesDashlet.getSites().contains(siteName), "Site is displayed on My Sites Dashlet");

        ShareUser.logout(drone);

    }


}
