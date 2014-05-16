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
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.admin.ManagedSiteRow;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.po.share.adminconsole.NodeBrowserPage.QueryType;
import org.alfresco.po.share.adminconsole.NodeBrowserSearchResult;
import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.service.cmr.repository.MalformedNodeRefException;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ShareUserAdmin extends AbstractUtils
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
     *            Navigate to Groups page
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
     *            This method is called when the user is on groups page
     *            Click on browse button in Groups page
     * @return Groups page
     */
    public static GroupsPage browseGroups(WebDrone driver)
    {
        GroupsPage page = driver.getCurrentPage().render();
        // GroupsPage page = navigateToGroup(driver);
        GroupsPage groupsPage = page.clickBrowse().render();
        return groupsPage;
    }

    /**
     * @param driver WebDriver Instance
     * @param userName- check whether this user is in group
     * @param groupName - Check whether user in this specific group Name
     *            Verify user is a member of group
     * @return Boolean
     */

    public static Boolean isUserGroupMember(WebDrone driver, String fName, String uName, String groupName)
    {
        GroupsPage page = browseGroups(driver);
        GroupsPage groupspage = page.selectGroup(groupName).render();
        List<UserProfile> userProfiles = groupspage.getMembersList();

        for (UserProfile userProfile : userProfiles)
        {
            if (fName.equals(userProfile.getfName()))
            {
                // Verify user is present in the members list
                if (userProfile.getUsername().contains(uName))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param driver WebDriver Instance
     * @param fName- check whether this fName matches with users first name in group
     * @param lName - check whether this lName matches with users first name in group
     *            Verify user is in ALFRESCO_ADMINISTRATORS group
     * @return Boolean
     */

    public static Boolean isUserAdmin(WebDrone driver, String fName, String uName)
    {
        String groupName = "ALFRESCO_ADMINISTRATORS";
        GroupsPage page = browseGroups(driver);
        GroupsPage groupspage = page.selectGroup(groupName).render();
        List<UserProfile> userProfiles = groupspage.getMembersList();

        for (UserProfile userProfile : userProfiles)
        {
            if (fName.equals(userProfile.getfName()))
            {
                // Verify user is present in the members list
                if (userProfile.getUsername().contains(uName))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Navigate to ManageSitesPage
     * Assumes user is logged in
     * 
     * @param drone
     * @return
     */
    public static ManageSitesPage navigateToManageSites(WebDrone drone)
    {
        ManageSitesPage manageSitesPage;

        SharePage sharePage = getSharePage(drone).render();

        if (sharePage instanceof ManageSitesPage)
        {
            manageSitesPage = getSharePage(drone).render();
        }
        else
        {
            manageSitesPage = sharePage.getNav().selectManageSitesPage().render();
        }
        return manageSitesPage;
    }

    /**
     * Changes Site visibility to the specified value
     * Assumes Site Admin / Manager is logged in
     * 
     * @param drone
     * @param siteName
     * @param siteVisibility
     * @return ManageSitesPage
     */
    public static ManageSitesPage changeSiteVisibility(WebDrone drone, String siteName, SiteVisibility siteVisibility)
    {
        ManageSitesPage manageSitesPage = navigateToManageSites(drone);

        ManagedSiteRow managedSiteRow = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(siteName);
        if (managedSiteRow != null)
        {

            managedSiteRow.getVisibility().selectValue(siteVisibility);
        }
        else
        {
            logger.error("Unable to change Site Visibility to: " + siteVisibility.getDisplayValue());
        }

        return manageSitesPage;
    }
    
    /**
     * Util to navigate to NodeBrowserPage from AdminTools
     * Assumes Admin user is logged in
     * @param drone
     * @return NodeBrowserPage
     */
    public static NodeBrowserPage getNodeBrowser(WebDrone drone)
    {
        SharePage sharePage = getSharePage(drone).render();
        NodeBrowserPage nodeBrowserPage = sharePage.getNav().getNodeBrowserPage().render();
        return nodeBrowserPage;
    }

    /**
     * Util to perform the search on the Node Browser Page and return the Node Browser Page with results
     * Assumes Admin user is logged in
     * @param drone
     * @param queryType
     * @param queryString
     * @return NodeBrowserPage
     */
    public static NodeBrowserPage nodeSearch(WebDrone drone, QueryType queryType, String queryString)
    {

        NodeBrowserPage nodeBrowserPage = getNodeBrowser(drone);
        nodeBrowserPage.selectQueryType(queryType);
        nodeBrowserPage.fillQueryField(queryString);
        nodeBrowserPage = nodeBrowserPage.clickSearchButton().render();

        return nodeBrowserPage;
    }

    public static String getTagNodeRef(WebDrone drone, String tagName)
    {
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
    
        NodeBrowserPage nodeBrowserPage = nodeSearch(drone, QueryType.LUCENE, "PATH:\"/cm:categoryRoot/cm:taggable/cm:" + tagName + "\"");
    
        NodeBrowserSearchResult searchResult = nodeBrowserPage.getSearchResults("cm:"+tagName);
        String tagNodeRef = searchResult.getReference().getDescription();

        ShareUser.logout(drone);
        
        return tagNodeRef;
    }

    public static String getCategoryNodeRef(WebDrone drone, String propertyDefinitionId, String propertyName)
    {
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        
        NodeBrowserPage nodeBrowserPage = getNodeBrowser(drone);
        
        nodeBrowserPage = nodeSearch(drone, QueryType.LUCENE, "TYPE:\"" + propertyDefinitionId + "\"");       
    
        NodeBrowserSearchResult result = nodeBrowserPage.getSearchResults(propertyName);
        String categoryNodeRef = result.getReference().getDescription();
        logger.info(propertyName + " Node Ref: " + categoryNodeRef);
        ShareUser.logout(drone);
        
        // logger.info("Category NodeRef: " + categoryNodeRef);
        if(categoryNodeRef.isEmpty() || !categoryNodeRef.startsWith("workspace://SpacesStore/"))
        {
            throw new MalformedNodeRefException("Invalid node ref:" + categoryNodeRef);
        }
        return categoryNodeRef;
    }
}
