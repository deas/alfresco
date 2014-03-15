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
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.RemoveUserFromGroupPage;
import org.alfresco.po.share.RemoveUserFromGroupPage.Action;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserAdmin;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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
     * User logs out after test is executed
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
                
        //Navigate to Groups pgae
        ShareUserAdmin.navigateToGroup(drone);
        
        //Click on browse button in Groups Page
        GroupsPage page = ShareUserAdmin.browseGroups(drone);
        
        //Verify Site_admin group name is present in the list of Groups
        Assert.assertTrue(page.getGroupList().contains(siteAdmin), "Site Admin Group is present!!");        
    
    }
    
    /**
     * Test:
     * <ul>
     * <li>Select the Site_Admin group from the list of Groups in Groups page</li>
     * <li>Verify Repo_Admin is present in Site_Admin Group Members list</li>
     * </ul>
     */  
    //cover this test in share po as page object
    @Test(groups = "EnterpriseOnly")
    public void ACE_564_04() throws Exception
    {
        String admin ="Administrator";
        String uname ="admin";
        
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);        
        
        //Navigate to Groups page
        ShareUserAdmin.navigateToGroup(drone);
        
        //Click on browse button in Groups Page
        ShareUserAdmin.browseGroups(drone);       
        
        //Verify RepoAdmin is the member of siteAdmin group
        Assert.assertTrue(ShareUserAdmin.isUserGroupMember(drone,admin,uname, siteAdmin));    
       
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
        String fName = getUserNameFreeDomain(testname+ System.currentTimeMillis());
        String lName = getUserNameFreeDomain(testname+ System.currentTimeMillis());
        String uName = getUserNameFreeDomain(testname+ System.currentTimeMillis());           
        
        //Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        
        //Create User and add to SiteAdmin group
        ShareUser.createEnterpriseUserWithGroup(drone, ADMIN_USERNAME, fName,lName,uName, DEFAULT_PASSWORD, siteAdmin);

        //Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);        
        
        //Navigate to Groups page
        ShareUserAdmin.navigateToGroup(drone);
        
        //Click on browse button in Groups Page
        ShareUserAdmin.browseGroups(drone);
        
        //Verify created user is present in siteAdmin group
        Assert.assertTrue(ShareUserAdmin.isUserGroupMember(drone,fName,uName, siteAdmin));       
        
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
        String testname = getTestName();
        String fName = getUserNameFreeDomain(testname+ System.currentTimeMillis());
        String lName = getUserNameFreeDomain(testname+ System.currentTimeMillis());
        String uName = getUserNameFreeDomain(testname+ System.currentTimeMillis());         
        
        //Login as RepoAdmin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);  
        
        //Create User and add to Site Admin group
        ShareUser.createEnterpriseUserWithGroup(drone, ADMIN_USERNAME, fName, lName, uName, DEFAULT_PASSWORD, siteAdmin);
        
        //Login as RepoAdmin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);                      
        
        //Navigate to Groups page
        ShareUserAdmin.navigateToGroup(drone);
        
        //Click on browse button in Groups Page
        GroupsPage groupsPage = ShareUserAdmin.browseGroups(drone);        
        
        //Select Site_admin group from the list of groups
        GroupsPage groupspage = groupsPage.selectGroup(siteAdmin).render();
        
        //Remove user from members list in Groups page
        RemoveUserFromGroupPage removeUserFromGroupPagegroupspage = groupspage.removeUser(testname);
        
        //Verify Confirm Remove pop up window is displayed
        Assert.assertTrue(removeUserFromGroupPagegroupspage.getTitle().equalsIgnoreCase(title), "Title is present");
        
        //Confirm Remove user from Group page
        removeUserFromGroupPagegroupspage.selectAction(Action.No).render();
        
        //Verify created user is present in siteAdmin group
        Assert.assertTrue(ShareUserAdmin.isUserGroupMember(drone,fName,uName, siteAdmin));  
                           
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
        String testname = getTestName();
        String fName = getUserNameFreeDomain(testname+ System.currentTimeMillis());
        String lName = getUserNameFreeDomain(testname+ System.currentTimeMillis());
        String uName = getUserNameFreeDomain(testname+ System.currentTimeMillis());         
        
        //Login as RepoAdmin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);  
        
        //Create User and add to Site Admin group
        ShareUser.createEnterpriseUserWithGroup(drone, ADMIN_USERNAME, fName, lName, uName, DEFAULT_PASSWORD, siteAdmin);
        
        //Login as RepoAdmin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);                      
        
        //Navigate to Groups page
        ShareUserAdmin.navigateToGroup(drone);
        
        //Click on browse button in Groups Page
        GroupsPage groupsPage = ShareUserAdmin.browseGroups(drone);        
        
        //Select Site_admin group from the list of groups
        GroupsPage groupspage = groupsPage.selectGroup(siteAdmin).render();
        
        //Remove user from members list in Groups page
        RemoveUserFromGroupPage removeUserFromGroupPagegroupspage = groupspage.removeUser(testname);
        
        //Verify Confirm Remove pop up window is displayed
        Assert.assertTrue(removeUserFromGroupPagegroupspage.getTitle().equalsIgnoreCase(title), "Title is present");
        
        //Confirm Remove user from Group page
        removeUserFromGroupPagegroupspage.selectAction(Action.Yes).render();
        
        //Verify created user is present in siteAdmin group
        Assert.assertFalse(ShareUserAdmin.isUserGroupMember(drone,fName,uName, siteAdmin));        
                     
    }   
    
       
}

    