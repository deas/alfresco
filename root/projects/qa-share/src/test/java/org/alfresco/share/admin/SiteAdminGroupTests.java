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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.admin;

/**
*
* @author Charu
* 
*/
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.RemoveUserFromGroupPage;
import org.alfresco.po.share.RemoveUserFromGroupPage.Action;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserAdmin;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


@Listeners(FailedTestListener.class)
public class SiteAdminGroupTests extends AbstractTests
{
    private static final Logger logger = Logger.getLogger(SiteAdminGroupTests.class);
    public String siteAdmin = "SITE_ADMINISTRATORS";        
    public DashBoardPage dashBoard;
    public String title = "Remove User from Group";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {

        super.setup();
        testName = this.getClass().getSimpleName();

    }

    /**
     * User logs in before test is executed
     * 
     * @throws Exception
     */
    @BeforeMethod
    public void prepare() throws Exception
    {
        // no before method
        try
        {
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
            logger.info("Create user logged in - drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * User logs out before test is executed
     * 
     * @throws Exception
     */
    @AfterMethod
    public void quit() throws Exception
    {
        // logout as created user
        try
        {
            ShareUser.logout(drone);
            logger.info("Created User logged out- drone.");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    /**
     * Test:
     * <ul>
     * <li>Get the list of groups displayed in Groups page</li>
     * <li>Verify Site_Administrator group is present in the group name list</li>
     * </ul>
     */    
    
    @Test (groups = "EnterpriseOnly")
    public void ACE_564_02() throws Exception
    {
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        
        // Open User dash board page
        //DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone);
        
        //Navigate to Groups page 
        //GroupsPage page = dashBoardPage.getNav().getGroupsPage();
        
        //Navigate to Add and Edit groups in Groups page
        //page = page.clickBrowse().render();
        ShareUserAdmin.navigateToGroup(drone);
        GroupsPage page = ShareUserAdmin.browseGroups(drone);
        //Verify Site_admin group name is present in the list of Groups
        Assert.assertTrue(page.getGroupList().contains(siteAdmin), "Site Admin Group is present!!");        
    
    }
    
    /**
     * Test:
     * <ul>
     * <li>Select the Site_Admin group from the list of Groups in Groups page</li>
     * <li>Verify Site_Admin is present in the Group Members list</li>
     * </ul>
     */  
    //cover this test in share po as page object
    @Test(groups = "EnterpriseOnly")
    public void ACE_564_04() throws Exception
    {
        String admin ="Administrator";
        String lname ="(admin)";
        
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        
        // Open User dash board page
        DashBoardPage dashBoard = ShareUser.openUserDashboard(drone);
        
        //Navigate to Groups page
        GroupsPage page = dashBoard.getNav().getGroupsPage().render();
        
        //Navigate to Add and Edit groups in Groups page
        page = page.clickBrowse().render();
        
        //Verify Site_admin group name is present in the list of Groups
        Assert.assertTrue(page.getGroupList().contains(siteAdmin), "Site Admin Group is present!!");
        
        ShareUserAdmin.isUserGroupMember(drone,lname, siteAdmin);
        /*//Select site_Admin group from list of groups           
        GroupsPage groupspage = page.selectGroup(siteAdmin).render();     
        
        //Get the user profile for list of users in Groups page
        List<UserProfile> userProfiles = groupspage.getMembersList();
        
        for (UserProfile userProfile : userProfiles)
        {
            if(admin.equals(userProfile.getfName()))
            {                
                // Verify user is present in Groups page
                Assert.assertTrue(userProfile.getUsername().contains(lname), "User is present!!");
               
            } 
        }*/
      
    }    
    
    /**
     * Test:
     * <ul>
     * <li>Create new user and add to site admin group</li>
     * <li>Select the Site_Admin group from the list of Groups in Groups page</li>
     * <li>Verify new user created and added to site admin group is present in the Members list</li>
     * <li></li>
     * </ul>
     */    
    @Test(groups = "EnterpriseOnly")
    public void ACE_564_06() throws Exception
    {
        
        String testname = getTestName();
        String UserName = getUserNameFreeDomain(testname+ System.currentTimeMillis());
        
        //Create User and add to Site Admin group
        ShareUser.createEnterpriseUserWithGroup(drone, ADMIN_USERNAME, UserName , UserName, UserName, DEFAULT_PASSWORD, siteAdmin);

        //Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);        
        
        //Navigate to Dash Board Page
        //DashBoardPage dashBoard = ShareUser.openUserDashboard(drone);
        
        //Navigate to Groups Page
        //GroupsPage page = dashBoard.getNav().getGroupsPage();
        
        //Navigate to Add and edit Groups page
        //GroupsPage groupsPage = page.clickBrowse().render(); 
              
        Assert.assertTrue(ShareUserAdmin.isUserGroupMember(drone,UserName, siteAdmin));
        
        /*//Select Site_admin group from the list of groups
        GroupsPage groupspage = groupsPage.selectGroup(siteAdmin).render();        
                
        //To do call this from Utils
        //Get the user profile for list of users
        List<UserProfile> userProfiles = groupspage.getMembersList();
        
        for (UserProfile userProfile : userProfiles)
        {
            if(testname.equals(userProfile.getfName()))
            {
                //Verify created user is present in the members list in groups page
                Assert.assertTrue(userProfile.getUsername().contains(testname), "User is present!!");
            }
        }*/

    }
    
    /**
     * Test:
     * <ul>
     * <li>Create new user and add to site_admin group</li>
     * <li>Select the Site_Admin group from the list of Groups in Groups page</li>
     * <li>Remove created and added user from Site_admin group</li>
     * <li>Verify Confirm remove user pop up window is displayed</li>
     * <li>Confirm 'No'to remove user from pop up window </li>
     * <li>Verify Created user is present in the members list in Groups page</li>
     * <li></li>
     * </ul>
     */    
    
    @Test(groups = "EnterpriseOnly")
    public void ACE_564_08() throws Exception
    {
        String testname = getTestName() + System.currentTimeMillis();        
        
        //Create User and add to Site Admin group
        ShareUser.createEnterpriseUserWithGroup(drone, ADMIN_USERNAME, testname, testname, testname, DEFAULT_PASSWORD, siteAdmin);
        
        //Login as RepoAdmin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);        
        
        //Navigate to Dash Board Page
        DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone);
        
        //Navigate to Groups Page
        GroupsPage page = dashBoardPage.getNav().getGroupsPage();
        
        //Navigate to Add and edit Groups page
        GroupsPage groupsPage = page.clickBrowse().render();
        
        //Verify Site_admin is present in groups list
        Assert.assertTrue(groupsPage.getGroupList().contains(siteAdmin), "Site Admin Group is present!!");
        
        //Select Site_admin group from the list of groups
        GroupsPage groupspage = groupsPage.selectGroup(siteAdmin).render();
        
        //Remove user from members list in Groups page
        RemoveUserFromGroupPage removeUserFromGroupPagegroupspage = groupspage.removeUser(testname);
        
        //Verify Confirm Remove pop up window is displayed
        Assert.assertTrue(removeUserFromGroupPagegroupspage.getTitle().equalsIgnoreCase(title), "Title is present");
        
        //Confirm Remove user from Group page
        removeUserFromGroupPagegroupspage.selectAction(Action.No).render();
        
        List<UserProfile> userProfiles = groupspage.getMembersList();
        
        for (UserProfile userProfile : userProfiles)
        {
            if(testname.equals(userProfile.getfName()))
            {
                //Verify user is present  in the members list  
                Assert.assertTrue(userProfile.getUsername().contains(testname));
                break;
            }
        }    
                     
    }       

    /**
     * Test:
     * <ul>
     * <li>Create new user and add to site_admin group</li>
     * <li>Select the Site_Admin group from the list of Groups in Groups page</li>
     * <li>Remove created and added user from Site_admin group</li>
     * <li>Verify Confirm remove user pop up window is displayed</li>
     * <li>Confirm 'Yes'to remove user from pop up window </li>
     * <li>Verify created user is removed from members list in Groups page</li>
     * <li></li>
     * </ul>
     */    
    @Test(groups = "EnterpriseOnly")
    public void ACE_564_10() throws Exception
    {
        String testname = getTestName() + System.currentTimeMillis();
        
        //Create User and add to Site Admin group
        ShareUser.createEnterpriseUserWithGroup(drone, ADMIN_USERNAME, testname, testname, testname, DEFAULT_PASSWORD, siteAdmin);
        
        //Login as RepoAdmin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);        
        
        //Navigate to Dash Board Page
        DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone); 
        
        //Navigate to Groups Page
        GroupsPage page = dashBoardPage.getNav().getGroupsPage();
        
        //Navigate to Add and edit Groups page
        GroupsPage groupsPage = page.clickBrowse().render();
        
        //Verify Site_admin is present in groups list
        Assert.assertTrue(groupsPage.getGroupList().contains(siteAdmin), "Site Admin Group is present!!");
        
        //Select Site_admin group from the list of groups
        GroupsPage groupspage = groupsPage.selectGroup(siteAdmin).render();
        
        //Remove user from members list in Groups page
        RemoveUserFromGroupPage removeUserFromGroupPagegroupspage = groupspage.removeUser(testname);
        
        //Verify Confirm Remove pop up window is displayed
        Assert.assertTrue(removeUserFromGroupPagegroupspage.getTitle().equalsIgnoreCase(title), "Title is present");
        
        //Confirm Remove user from Group page
        removeUserFromGroupPagegroupspage.selectAction(Action.Yes).render();
        
        List<UserProfile> userProfiles = groupspage.getMembersList();
        
        for (UserProfile userProfile : userProfiles)
        {
            if(testname.equals(userProfile.getfName()))
            {
                //Verify user is not displayed in the members list  
                Assert.assertFalse(userProfile.getUsername().contains(testname));
                break;
            }
        }    
                     
    }   
    
       
}

    