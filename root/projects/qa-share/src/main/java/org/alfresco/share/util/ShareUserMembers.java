/**
 * 
 */
package org.alfresco.share.util;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.EditUserPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.AddGroupsPage;
import org.alfresco.po.share.site.InviteMembersPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SiteGroupsPage;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage.ButtonType;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.TimeoutException;
import org.testng.SkipException;

/**
 * @author cbairaajoni
 */
public class ShareUserMembers extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(ShareUserMembers.class);

    /**
     * Default Constructor
     */
    public ShareUserMembers()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }
    }

    /**
     * This method allows the Site Manager to invite user to join site as given
     * role
     * 
     * @param driver
     * @param invitingUser
     * @param userJoiningSite
     * @param siteName
     * @param role
     * @return Boolean
     */
    public static Boolean inviteUserToSiteWithRoleENT(WebDrone driver, String invitingUser, String userJoiningSite, String siteName, UserRole role)
    {
        List<String> searchUsers = null;
        Boolean retVal = false;

        if (driver == null || userJoiningSite == null || role == null)
        {
            throw new UnsupportedOperationException("Joining User on Site is null");
        }

        if (isAlfrescoVersionCloud(driver))
        {
            throw new UnsupportedOperationException("This method is not supported for Cloud environment.");
        }

        try
        {
            // Search for User to be invited
            SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(driver, siteName).render(maxWaitTime);

            InviteMembersPage membersPage = siteDashBoard.getSiteNav().selectInvite();

            searchUsers = retrySearchInviteMembers(driver, userJoiningSite);

            // Select Role for the User
            if (searchUsers.size() > 0)
            {
                membersPage.selectRole(searchUsers.get(0), role);
                membersPage.clickInviteButton();
                retVal = true;
            }
        }
        catch (Exception e)
        {
            logger.error("Inviting enterprise user to site is not successful:" + e.getMessage());
            throw new ShareException(String.format("Error in inviting User %s To Site %s as %s: ", userJoiningSite, siteName, role) + e);
        }

        return retVal;
    }

    /**
     * This method is used to retry the search to get user details in
     * Invitemembers page
     * 
     * @param driver
     * @param userJoiningSite
     * @return List<String>
     */
    private static List<String> retrySearchInviteMembers(WebDrone driver, String userJoiningSite)
    {

        InviteMembersPage members = null;
        List<String> searchUsers = new ArrayList<String>();

        SharePage sharePage = ShareUser.getSharePage(driver);
        if (sharePage instanceof InviteMembersPage)
        {
            members = (InviteMembersPage) sharePage;
        }
        else
        {
            throw new PageException("Can not Search members, InviteMembersPage not available");
        }

        try
        {
            searchUsers = members.searchUser(userJoiningSite);

            // Retry until User is found in the list on the invite members page.
            for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
            {
                if (searchCount > 1)
                {
                    webDriverWait(driver, refreshDuration);
                    searchUsers = members.searchUser(userJoiningSite);
                }

                if (searchUsers != null && searchUsers.size() > 0)
                {
                    break;
                }

            }
        }
        catch (Exception e)
        {

        }
        return searchUsers;
    }

    /**
     * This method allows the Site Manager to invite the user to join site as
     * given role.
     * 
     * @param driver
     * @param invitingUser
     * @param userJoiningSite
     * @param siteName
     * @param role
     * @return Boolean
     */
    public static Boolean inviteUserToSiteWithRole(WebDrone driver, String invitingUser, String userJoiningSite, String siteName, UserRole role)
    {
        Boolean retVal = false;

        if (driver == null || userJoiningSite == null || role == null)
        {
            throw new UnsupportedOperationException("Joining User on Site is null");
        }

        try
        {
            if (isAlfrescoVersionCloud(driver))
            {
                retVal = CreateUserAPI.inviteUserToSiteWithRoleAndAccept(driver, invitingUser, userJoiningSite, getSiteShortname(siteName), role.getRoleName(),
                        "");
            }
            else
            {
                inviteUserToSiteWithRoleENT(driver, invitingUser, userJoiningSite, siteName, role);

                // invitingUser logs out.
                ShareUser.logout(driver);

                // Login As userJoining
                ShareUser.login(driver, userJoiningSite, DEFAULT_PASSWORD);

                // Accepting the invitation to join on given site.
                ShareUserMembers.acceptSiteInvitationTask(driver, siteName);

                // Log out
                ShareUser.logout(driver);

                // Login as inviting user
                ShareUser.login(driver, invitingUser);

                retVal = true;
            }
        }
        catch (Exception e)
        {
            logger.info("Error in inviting User To Site : " + e.getMessage());
            throw new SkipException(String.format("Error in inviting User %s To Site %s as %s: ", userJoiningSite, siteName, role) + e);
        }

        return retVal;
    }

    /**
     * This method is used to accept the site invitation task in mytasks
     * dashlet. And it is specific to Enterprise only.
     * 
     * @param drone
     * @param siteName
     * @return {@link MyTasksDashlet}
     */
    public static MyTasksDashlet acceptSiteInvitationTask(WebDrone drone, String siteName)
    {
        if (isAlfrescoVersionCloud(drone))
        {
            throw new UnsupportedOperationException("This functionality is applicapable to Enterprise only.");
        }

        MyTasksDashlet myTasks = ShareUser.selectTaskByTaskName(drone, siteName);

        myTasks = myTasks.acceptInvitaton().render(maxWaitTime);

        return myTasks;
    }

    /**
     * This method allows the user to join himself on another user's site as
     * consumer. Assumes user is logged in
     * 
     * @param driver
     * @param siteName
     * @return {@link SiteFinderPage}
     */
    public static SiteFinderPage userRequestToJoinSite(WebDrone driver, String siteName)
    {
        logger.info(" User request to joins the Site " + siteName);

        SharePage page = ShareUser.getSharePage(driver);
        SiteFinderPage siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = SiteUtil.searchSiteWithRetry(driver, siteName, true);
        return siteFinder.joinSite(siteName).render();
    }

    /**
     * This method allows the Site Manager to assign the role to site member for
     * the public sites. For private and moderate sites Site Manager approves
     * the sitemembers request as a additional task before assigning the role.
     * 
     * @param drone
     * @param siteMember
     * @param siteName
     * @param role
     * @return {@link SiteMembersPage}
     */
    public static SiteMembersPage assignRoleToSiteMember(WebDrone drone, String siteMember, String siteName, UserRole role)
    {
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);

        SiteMembersPage siteMemberspage = siteDashBoard.getSiteNav().selectMembers().render();

        if (ShareUserMembers.searchUserWithRetryFromMembersPage(drone, siteMember))
        {
            siteMemberspage = siteMemberspage.assignRole(siteMember, role).render();

            logger.info(role + " role assigned successfully to " + siteMember);
        }
        return siteMemberspage;
    }

    /**
     * This method is used to retry the search to get user details in members
     * page and returns true if user found otherwise false.
     * 
     * @param driver
     * @param userName
     * @return boolean
     */
    public static boolean searchUserWithRetryFromMembersPage(WebDrone driver, String userName)
    {
        SiteMembersPage siteMembersPage = (SiteMembersPage) ShareUser.getSharePage(driver);

        List<String> searchUsers = null;
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            try
            {
                searchUsers = siteMembersPage.searchUser(userName);
                siteMembersPage.renderWithUserSearchResults(refreshDuration);
            }
            catch (PageException e)
            {
            }
            catch (PageRenderTimeException exception)
            {
            }

            if (searchUsers != null && searchUsers.size() > 0)
            {
                for (String user : searchUsers)
                {
                    if (user.contains(userName))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * This method allows the Site Manager to assign the role to site group for the public sites
     *
     * @param drone
     * @param siteGroup
     * @param siteName
     * @param role
     * @return {@link SiteGroupsPage}
     */
    public static SiteGroupsPage assignRoleToSiteGroup(WebDrone drone, String siteGroup, String siteName, UserRole role)
    {
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);

        SiteMembersPage siteMemberspage = siteDashBoard.getSiteNav().selectMembersPage().render();
        SiteGroupsPage siteGroupsPage = siteMemberspage.navigateToSiteGroups().render();

        if (ShareUserMembers.searchGroupWithRetryFromGroupsPage(drone, siteGroup))
        {
            siteGroupsPage = siteGroupsPage.assignRole(siteGroup, role).render();

            logger.info(role + " role assigned successfully to " + siteGroup);
        }
        return siteGroupsPage;
    }

    /**
     * This method is used to retry the search to get group details in members
     * page and returns true if group is found otherwise false.
     *
     * @param driver
     * @param groupName
     * @return boolean
     */
    public static boolean searchGroupWithRetryFromGroupsPage(WebDrone driver, String groupName)
    {
        SiteGroupsPage siteGroupsPage = ShareUser.getSharePage(driver).render();

        List<String> searchGroups = null;
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            try
            {
                searchGroups = siteGroupsPage.searchGroup(groupName);
                siteGroupsPage.renderWithGroupSearchResults(refreshDuration);
            }
            catch (PageException e)
            {
            }
            catch (PageRenderTimeException exception)
            {
            }

            if (searchGroups != null && searchGroups.size() > 0)
            {
                for (String group : searchGroups)
                {
                    if (group.contains(groupName))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * This method allows the Site Manager to remove the site member for the
     * public sites.
     * 
     * @param drone
     * @param siteMember
     * @param siteName
     * @return {@link SiteMembersPage}
     */
    public static SiteMembersPage removeSiteMember(WebDrone driver, String siteMember, String siteName)
    {
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(driver, siteName);

        SiteMembersPage siteMemberspage = siteDashBoard.getSiteNav().selectMembers().render();

        if (ShareUserMembers.searchUserWithRetryFromMembersPage(driver, siteMember))
        {
            // TODO: Chiran: Need a webdrone unit test for removeUser
            siteMemberspage = siteMemberspage.removeUser(siteMember).render();
            logger.info(siteMember + " has been removed from sitemembers");
            return siteMemberspage;
        }

        throw new PageException("Error in removing member from Site Members.");
    }

    /**
     * Utility to promote the user as network admin (for the given domain) Only
     * suitable for Enterprise: As Admin Console is not available for Cloud
     * 
     * @param drone
     *            WebDrone Instance
     * @param authUser
     *            String authenticating user
     * @param userNametoBePromoted
     *            String userName to be promoted as network admin
     * @throws Exception
     */
    public static Boolean promoteUserAsAdminEnterprise(WebDrone driver, String authUser, String userNametoBePromoted) throws Exception
    {
        Boolean result = false;

        if (isAlfrescoVersionCloud(driver))
        {
            throw new UnsupportedOperationException("Method not suitable for use for Cloud");
        }

        try
        {
            // ShareUser.login(driver, authUser, getAuthDetails(authUser)[1]);
            EditUserPage editUser = navigateToEditUser(driver, userNametoBePromoted).render();
            editUser.searchGroup(adminGroup).render();
            editUser.addGroup(adminGroup).render();
            editUser.saveChanges().render();
            // ShareUser.logout(driver);
            result = true;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not promote user as admin" + e.getMessage());
        }
        return result;
    }

    /**
     * Utility to navigate to EditUserPage on admin console, Assumes user is
     * logged in
     * 
     * @param driver
     * @param String
     *            userinfo such as username
     * @return {@link}EditUserPage
     */
    private static EditUserPage navigateToEditUser(WebDrone driver, String userinfo)
    {
        UserSearchPage userPage;
        try
        {
            SharePage sharePage = ShareUser.getSharePage(driver);
            userPage = sharePage.getNav().getUsersPage().render();

            userPage = userPage.searchFor(userinfo).render();
            UserProfilePage userProfile = userPage.clickOnUser(userinfo).render();
            EditUserPage editUser = userProfile.selectEditUser().render();
            return editUser;
        }
        catch (UnsupportedOperationException uso)
        {
            throw new UnsupportedOperationException("Can not navigate to Edit User Page");
        }
    }

    /**
     * Util to invite a group to the specified site Assumes user is logged in
     * 
     * @param driver
     * @param invitingUser
     * @param groupDisplayName
     * @param siteName
     * @param role
     * @return
     */
    public static Boolean inviteGroupToSiteWithRole(WebDrone driver, String invitingUser, String groupDisplayName, String siteName, UserRole role)
    {
        Boolean retVal = false;

        if (groupDisplayName == null || role == null)
        {
            throw new UnsupportedOperationException("Joining User on Site is null");
        }

        try
        {
            // Search for User to be invited
            SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(driver, siteName);

            InviteMembersPage membersPage = siteDashBoard.getSiteNav().selectInvite();
            SiteGroupsPage siteGroups = membersPage.navigateToSiteGroupsPage().render();
            AddGroupsPage addGroupsToSite = siteGroups.navigateToAddGroupsPage().render();
            addGroupsToSite.addGroup(groupDisplayName, role);
            retVal = true;

        }
        catch (TimeoutException e)
        {
            logger.info("Error in inviting User To Site : " + e.getMessage());
            throw new SkipException(String.format("Error in inviting group %s To Site %s as %s: ", groupDisplayName, siteName, role) + e);
        }

        return retVal;
    }

    /**
     * @param drone
     * @param user
     * @param contentName
     * @param userRole
     * @param toggleInheritPermission
     * @return HtmlPage
     */
    public static HtmlPage managePermissionsOnContent(WebDrone drone, String user, String contentName, UserRole userRole, boolean toggleInheritPermission)
    {
        ManagePermissionsPage mangPermPage = ShareUser.returnManagePermissionPage(drone, contentName);

        if (!(user == null || user.isEmpty()))
        {
            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(user);
            userProfile.setlName(DEFAULT_LASTNAME);

            if (mangPermPage.isUserExistForPermission(user))
            {
                mangPermPage.updateUserRole(user, userRole);
            }
            else
            {
                mangPermPage = mangPermPage.selectAddUser().searchAndSelectUser(userProfile);
                // mangPermPage.setAccessType(userProfile, userRole);
                mangPermPage.updateUserRole(user, userRole);
            }
        }
        return toggleInheritPermission(drone, toggleInheritPermission);
    }

    /**
     * This method updates the role to the content selected at document library
     * page. Assumes user is logged in
     * 
     * @param drone
     * @param user
     * @param contentName
     * @param userRole
     * @param toggleInheritPermission
     * @return
     */
    // TODO: Naved: Use managePermissionOnContent if it serves the same purpose
    public static HtmlPage updateRoleOnContent(WebDrone drone, String user, String contentName, UserRole userRole, boolean toggleInheritPermission)
    {
        ManagePermissionsPage mangPermPage = ShareUser.returnManagePermissionPage(drone, contentName);

        if (mangPermPage.isUserExistForPermission(user))
        {
            mangPermPage.updateUserRole(user, userRole);
        }
        else
        {
            logger.info("Failed to update Permissions for the user. No existing Permissions found:" + user);
        }

        return toggleInheritPermission(drone, toggleInheritPermission);
    }

    /**
     * This method switches the toggleInheritPemissions icon to desired status
     * specified as boolean value <toggleInheritPermission>
     * 
     * @IMP Note: This is to be called when user is on the ManagePermissions
     *      page.
     * @param drone
     * @param toggleInheritPermission
     * @return
     */
    public static HtmlPage toggleInheritPermission(WebDrone drone, boolean toggleInheritPermission)
    {
        ManagePermissionsPage mangPermPage = (ManagePermissionsPage) getSharePage(drone);
        ButtonType btnType = ButtonType.Yes;
        if (toggleInheritPermission)
        {
            btnType = ButtonType.No;
        }
        mangPermPage = mangPermPage.toggleInheritPermission(toggleInheritPermission, btnType);

        return mangPermPage.selectSave().render();
    }

    /**
     * Add Inherited permission to User and Group. Assumes user is logged in and
     * on ManagePermissionsPage
     * 
     * @param driver
     * @param candidate
     * @param role
     * @param isUser
     *            <tt>true</tt> if username is specified, <tt>false</tt> if
     *            group
     * @param toggleInheritPermission
     * @return
     */
    public static HtmlPage addUserOrGroupIntoInheritedPermissions(WebDrone driver, String candidate, boolean isUser, UserRole role, boolean toggleInheritPermission)
    {

        ManagePermissionsPage managePermissionPage = getSharePage(driver).render();
        ManagePermissionsPage.UserSearchPage userSearchPage = managePermissionPage.selectAddUser().render();

        if (isUser)
        {
            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(candidate);
            managePermissionPage = userSearchPage.searchAndSelectUser(userProfile).render();
        }
        else
        {
            managePermissionPage = userSearchPage.searchAndSelectGroup(candidate).render();
        }

        if (!managePermissionPage.isUserExistForPermission(candidate))
        {
            managePermissionPage.setAccessType(role);
        }
        else
        {
            managePermissionPage.updateUserRole(candidate, role);
        }

        return toggleInheritPermission(driver, toggleInheritPermission);
    }

    /**
     * Add Inherited permission to User and Group with the default role. Assumes user is logged in and
     * on ManagePermissionsPage
     * 
     * @param driver
     * @param candidate
     * @param isUser
     *            <tt>true</tt> if username is specified, <tt>false</tt> if
     *            group
     * @param toggleInheritPermission
     * @return
     */
    public static HtmlPage addUserOrGroupIntoInheritedPermissions(WebDrone driver, String candidate, boolean isUser, boolean toggleInheritPermission)
    {

        ManagePermissionsPage managePermissionPage = getSharePage(driver).render();
        ManagePermissionsPage.UserSearchPage userSearchPage = managePermissionPage.selectAddUser().render();

        if (isUser)
        {
            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(candidate);
            userSearchPage.searchAndSelectUser(userProfile).render();
        }
        else
        {
            userSearchPage.searchAndSelectGroup(candidate).render();
        }

        return toggleInheritPermission(driver, toggleInheritPermission);
    }

    /**
     * Util to assign the specified <UserRole> to the specified <User> ofr the
     * Site. Assumes the SiteManager role is logged in.
     * 
     * @param drone
     * @param user
     *            String username. Util assumes that this user is already a
     *            member of the site
     * @param userRole
     * @param siteName
     * @return
     */
    public static SiteMembersPage setUserRoleWithSite(WebDrone drone, String user, UserRole userRole, String siteName)
    {
        SiteDashboardPage dashBoardPage = ShareUser.openSiteDashboard(drone, siteName);
        SiteMembersPage siteMembersPage = dashBoardPage.getSiteNav().selectMembers().render();
        return siteMembersPage.assignRole(user, userRole);
    }

    /**
     * Get existing Permission assigned to User or Group.
     * 
     * @param drone
     * @param contentName
     * @param candidate
     * @return {@link} UserRole
     */
    public static UserRole getContentPermission(WebDrone drone, String contentName, String candidate)
    {
        return ShareUser.returnManagePermissionPage(drone, contentName).getExistingPermission(candidate);
    }
    
    
    
    /**
     * Search and Add user and group in the Permission list, it will not save the operation.
     * @param driver
     * @param candidate
     * @param isUser
     * @param role
     * @param toggleInheritPermission
     * @return
     */
    public static ManagePermissionsPage searchAndAddUserOrGroupWithoutSave(WebDrone driver, String candidate, boolean isUser)
    {

        ManagePermissionsPage managePermissionPage = getSharePage(driver).render();
        ManagePermissionsPage.UserSearchPage userSearchPage = managePermissionPage.selectAddUser().render();

        if (isUser)
        {
            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(candidate);
            managePermissionPage = userSearchPage.searchAndSelectUser(userProfile).render();
        }
        else
        {
            managePermissionPage = userSearchPage.searchAndSelectGroup(candidate).render();
        }
        
        return managePermissionPage;
    }

    /**
     * This method allows the user to leave a site.  Assumes user is logged in
     * 
     * @param driver
     * @param siteName
     * @return {@link SiteFinderPage}
     */
    public static SiteFinderPage userRequestToLeaveSite(WebDrone driver, String siteName)
    {
        logger.info(" User request to leave the Site " + siteName);

        SharePage page = ShareUser.getSharePage(driver);
        SiteFinderPage siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = SiteUtil.searchSiteWithRetry(driver, siteName, true);
        return siteFinder.leaveSite(siteName).render();
    }
}