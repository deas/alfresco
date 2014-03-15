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
package org.alfresco.share.util;

import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;

public class ShareUserAdmin extends AbstractTests
{
    private static Log logger = LogFactory.getLog(ShareUser.class);

    public ShareUserAdmin()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }
    }

    /**     
     * @param driver WebDriver Instance      
     * Navigate to Groups page
     * @return Groups page
     */
    
    public static GroupsPage navigateToGroup(WebDrone driver)
    {         
        DashBoardPage dashBoard = ShareUser.openUserDashboard(driver);     
        GroupsPage page = dashBoard.getNav().getGroupsPage();
        return page;
    }
    
    /**     
     * @param driver WebDriver Instance      
     * Click on browse button in Groups page
     * @return Groups page
     */
    public static GroupsPage browseGroups(WebDrone driver)
    {                 
        GroupsPage page = navigateToGroup(driver);  
        GroupsPage groupsPage = page.clickBrowse().render(); 
        return groupsPage;
    } 
    
    /**     
     * @param driver WebDriver Instance
     * @param userName- check whether this user is in group 
     * @param groupName - Check whether user in this specific group Name  
     * Verify user is a member of group
     * @return Boolean
     */
    
    public static Boolean isUserGroupMember(WebDrone driver, String userName, String groupName)
    {             
        GroupsPage page = browseGroups(driver);
        GroupsPage groupspage = page.selectGroup(groupName).render();
        List<UserProfile> userProfiles = groupspage.getMembersList();
        
        for (UserProfile userProfile : userProfiles)
        {
            if(userName.equals(userProfile.getfName()))
            {
                //Verify user is present  in the members list  
                Assert.assertTrue(userProfile.getUsername().contains(userName));                
                return true;               
            }
        }
        return false;
    }
    
    /**     
     * @param driver WebDriver Instance
     * @param fName- check whether this fName matches with users first name in group 
     * @param lName - check whether this lName matches with users first name in group 
     * Verify user is in ALFRESCO_ADMINISTRATORS  group
     * @return Boolean
     */
    
    
    public static Boolean isUserAdmin(WebDrone driver, String fName, String lName)
    {             
        String groupName = "ALFRESCO_ADMINISTRATORS";
        GroupsPage page = browseGroups(driver);
        GroupsPage groupspage = page.selectGroup(groupName).render();
        List<UserProfile> userProfiles = groupspage.getMembersList();
        
        for (UserProfile userProfile : userProfiles)
        {
            if(fName.equals(userProfile.getfName()))
            {
                //Verify user is present  in the members list  
                Assert.assertTrue(userProfile.getUsername().contains(lName));                
                return true;               
            }
        }
        return false;
    }


}

