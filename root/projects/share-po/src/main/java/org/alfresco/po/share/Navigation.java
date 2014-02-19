/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.search.AdvanceSearchContentPage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the HTML page relating to the main navigation bar
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class Navigation extends SharePage
{
    private static final String SITE_FINDER_LINK = "div[id$='app_sites-sites-menu']>div>ul[class^='site-finder-menuitem']>li>a";
    private static final String DEFAULT_NETWORK_MENU_BUTTON = "default.network.dropdown";
    private static final String NETWORK_NAMES = "network.names";
    private final String userNameDropDown ;

    /**
     * Constructor
     * 
     * @param drone WebDriver browser client
     */
    public Navigation(WebDrone drone)
    {
        super(drone);
        userNameDropDown = drone.getElement("user.dropdown");
      
    }

    @SuppressWarnings("unchecked")
    @Override
    public Navigation render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Navigation render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Navigation render(final long time)
    {
        return render(new RenderTime(time));
    }
    

    /**
     * Mimics the action of selecting the dashboard link.
     * 
     * @return HtmlPage dashboard page object
     */
    public DashBoardPage selectMyDashBoard()
    {
        String selector = isDojoSupport() ? "div#HEADER_HOME" : "a[id$='-dashboard-button']";
    	drone.find(By.cssSelector(selector)).click();
        return new DashBoardPage(drone);
    }

    /**
     * Mimics the action of selecting people finder link.
     * 
     * @return HtmlPage people finder page object
     */
    public PeopleFinderPage selectPeople()
    {
        String selector = isDojoSupport() ? "div#HEADER_PEOPLE" : "a[id$='people-button']";
    	drone.find(By.cssSelector(selector)).click();
        return new PeopleFinderPage(drone);
    }

    /**
     * Mimics the action of selecting site finder link.
     * 
     * @return HtmlPage people finder page object
     */
    public SiteFinderPage selectSearchForSites()
    {
        selectSitesDropdown();
        try
        {
            if(isDojoSupport())
            {
              drone.findAndWait(By.cssSelector(drone.getElement("site.finder"))).click();
            }
            else
            {
                drone.findAndWait(By.cssSelector(SITE_FINDER_LINK)).click();
            }
        }
        catch (NoSuchElementException nse)
        {
            //Try again
            selectSearchForSites();
        }
        return new SiteFinderPage(drone);
    }

    /**
     * Mimics the action of selecting create site link.
     * 
     * @return HtmlPage people finder page object
     */
    public CreateSitePage selectCreateSite()
    {
        selectSitesDropdown();
        String selector = isDojoSupport() ? "td#HEADER_SITES_MENU_CREATE_SITE_text" : "ul.create-site-menuitem>li>a";
        drone.findAndWait(By.cssSelector(selector)).click();
        return new CreateSitePage(drone);
    }

    /**
     * Selects the "Sites" icon on main navigation. As this link is created by
     * Java script, a wait is implemented to ensure the link is rendered.
     */
    protected void selectSitesDropdown()
    {
        // Wait is applied as the link is within a java script.
        String selector = isDojoSupport() ? "div#HEADER_SITES_MENU" : "button[id$='app_sites-button']";
        WebElement siteButton = drone.find(By.cssSelector(selector));
        siteButton.click();
    }

    /**
     * Selects the user link on main navigation. As this link is created by Java
     * script, a wait is implemented to ensure the link is rendered.
     */
    private void selectUserDropdown()
    {
    	WebElement userButton = drone.find(By.cssSelector(userNameDropDown));
        userButton.click();
    }

    /**
     * Mimics the action of selecting my profile link.
     * 
     * @return HtmlPage people finder page object
     */
    public MyProfilePage selectMyProfile()
    {
        selectUserDropdown();
        String selector = isDojoSupport() ? "td#HEADER_USER_MENU_PROFILE_text" : "div[id$='usermenu_user'] ul li:nth-of-type(2) a";
        drone.findAndWait(By.cssSelector(selector)).click();
        return new MyProfilePage(drone);
    }

    /**
     * Mimics the action of selecting my profile link.
     * 
     * @return HtmlPage change password page object
     */
    public ChangePasswordPage selectChangePassword()
    {
        String selector = isDojoSupport() ? "td#HEADER_USER_MENU_CHANGE_PASSWORD_text" : "div[id$='usermenu_user'] ul li:nth-of-type(3) a";
        selectUserDropdown();
        drone.findAndWait(By.cssSelector(selector)).click();
        return new ChangePasswordPage(drone); 
    }

    /**
     * Mimics the action of selecting logout link.
     * The page returned from a logout is a LoginPage.
     * @return {@link LoginPage} page response
     */
    public LoginPage logout()
    {
        selectUserDropdown();
        String selector = isDojoSupport() ? "td#HEADER_USER_MENU_LOGOUT_text" : "div[id$='usermenu_user'] ul li:nth-of-type(5) a";
        drone.findAndWait(By.cssSelector(selector)).click();
        return new LoginPage(drone);
    }

    /**
     * Mimics the action of selecting repository link.
     * 
     * @return HtmlPage repository page object
     */
    public RepositoryPage selectRepository()
    {
        String selector = isDojoSupport() ? "div#HEADER_REPOSITORY" : "a[id$='app_repository-button']"; 
        drone.find(By.cssSelector(selector)).click();
        return new RepositoryPage(drone);
    }

    /**
     * Select the advance search button from dropdown.
     * @return HtmlPage advance search page.
     */
    public AdvanceSearchContentPage selectAdvanceSearch()
    {
        try
        {
          if(isDojoSupport())
          {
              // TODO ALF-19185 - Bug Advance Search
              String usersPageURL = "/page/advsearch";
              String currentUrl = drone.getCurrentUrl();
              if (currentUrl != null)
              {
                  String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
                  drone.navigateTo(url);
              }
          }
          else
          {
              drone.findAndWait(By.cssSelector("button[id$='default-search_more-button']")).click();
              drone.findAndWait(By.cssSelector("div[id$='searchmenu_more']>div>ul>li>a")).click();
          }
          
          return new AdvanceSearchContentPage(drone);
        }
        catch(TimeoutException ne)
        {
            throw new PageException("Advance Search is not visible");
        }
    }    
    
    /**
     * Navigates to the users page on Admin Console - Enterprise Only option.
     * @return {@link UserSearchPage} Instance of UserSearchPage
     */
    public HtmlPage getUsersPage()
    {       
        if(alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This option is Enterprise only, not available for cloud");
        }
        //TODO To be implemented by using UI once JIRA: https://issues.alfresco.com/jira/browse/ALF-18909 is resolved 
        String usersPageURL = "/page/console/admin-console/users";
        String currentUrl = drone.getCurrentUrl();
        if(currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            drone.navigateTo(url);
        }
        return new UserSearchPage(drone);
    }
    
    /**
     * Selects the Network dropdown button present on UserDashBoard.
     * 
     * @return {@link DashBoardPage}
     */
    public DashBoardPage selectNetworkDropdown()
    {
        if (!alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This is an unsupported operation for Enterprise.");
        }

        try
        {
           String dropDownElementId = drone.getElement(DEFAULT_NETWORK_MENU_BUTTON);
           drone.findAndWait(By.cssSelector(dropDownElementId)).click();
           return new DashBoardPage(drone);
        }
        catch (TimeoutException e)
        {
            throw new PageException(this.getClass().getName() + " : selectNetworkDropdown() : failed to render in time. ", e);
        }
    }
    
    /**
     * Selects the given User Network link present in list of networks.
     * 
     * @param networkName String name
     * @return {@link DashBoardPage}
     */
    public DashBoardPage selectNetwork(final String networkName)
    {
        if (!alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This is an unsupported operation for Enterprise.");
        }
        if (StringUtils.isEmpty(networkName))
        {
            throw new IllegalArgumentException("Network name is required.");
        }
        try
        {
            String networkNamesid = drone.getElement(NETWORK_NAMES);
            List<WebElement> networks = drone.findAndWaitForElements(By.cssSelector(networkNamesid));
            {
                for (WebElement network : networks)
                {
                    if (network.getText() != null && network.getText().equalsIgnoreCase(networkName.trim()))
                    {
                        network.click();
                        return new DashBoardPage(drone);
                    }
                }
            }
            throw new PageException("Unable to find the User Network : " + networkName);
        }
        catch (TimeoutException e)
        {
            throw new PageException(this.getClass().getName() + " : selectNetwork() : failed to render in time. ", e);
        }
    }

    /**
     * Gets the list of user networks.
     * 
     * @return List<String>
     */
    public List<String> getUserNetworks()
    {
        List<WebElement> networks = null;
        try
        {
            String defaultNetworkButton = drone.getElement(DEFAULT_NETWORK_MENU_BUTTON);
            drone.findAndWait(By.cssSelector(defaultNetworkButton)).click();
            String networkNamesid = drone.getElement(NETWORK_NAMES);
            networks = drone.findAndWaitForElements(By.cssSelector(networkNamesid));
            if(networks != null)
            {
                List<String> networkList = new ArrayList<String>();
                for (WebElement network : networks)
                {
                    networkList.add(network.getText());
                }
                return networkList;
            }
            throw new PageException("Not able to find the any networks");
        }
        catch (TimeoutException e)
        {
            throw new PageException(this.getClass().getName() + " : selectUserNetwork() : failed to render in time. ", e);
        }
    }

    /**
     * Selects the user link on main navigation. As this link is created by Java
     * script, a wait is implemented to ensure the link is rendered.
     * 
     * @return {@link MyTasksPage}
     */
    public MyTasksPage selectMyTasks()
    {
        if (isDojoSupport())
        {
            if(!alfrescoVersion.isCloud())
            {
                drone.find(By.cssSelector("#HEADER_TASKS")).click();
            }
            drone.find(By.cssSelector("#HEADER_MY_TASKS")).click();
        }
        else
        {
            drone.find(By.cssSelector("button[id$='default-app_more-button']")).click();
            drone.find(By.cssSelector("div[id$='-appmenu_more']>div>ul:first-of-type>li:first-of-type>a")).click();
        }
        return new MyTasksPage(drone);
    }

    /**
     * Method to select "Workflows I've Started" under Tasks navigation menu item
     * @return {@link MyWorkFlowsPage}
     */
    public MyWorkFlowsPage selectWorkFlowsIHaveStarted()
    {
        try
        {
            drone.find(By.cssSelector("#HEADER_TASKS")).click();
            drone.find(By.cssSelector("td#HEADER_MY_WORKFLOWS_text")).click();
            return new MyWorkFlowsPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to find Workflows I've started link", nse);
        }
    }
    
    /**
     * Navigates to the groups page on Admin Console - Enterprise Only option.
     * @return {@link GroupsPage} Instance of UserSearchPage
     */
    public GroupsPage getGroupsPage()
    {       
        if(alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This option is Enterprise only, not available for cloud");
        }
        //TODO To be implemented by using UI once JIRA: https://issues.alfresco.com/jira/browse/ALF-18909 is resolved 
        String usersPageURL = "/page/console/admin-console/groups";
        String currentUrl = drone.getCurrentUrl();
        if(currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            drone.navigateTo(url);
        }
        return new GroupsPage(drone);
    }
}