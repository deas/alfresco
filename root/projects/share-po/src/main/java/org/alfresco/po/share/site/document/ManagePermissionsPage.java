/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
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

package org.alfresco.po.share.site.document;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


/**
 * Page object for Manage Permissions at granular level.
 * 
 * @author Abhijeet Bharade
 * @since 1.7.0
 */
public class ManagePermissionsPage extends SharePage
{
    private final By LAST_ROW_OF_PERMISSIONS = By.cssSelector("div[id$='_default-inheritedPermissions'] tr[class^='yui-dt-rec yui-dt-last']");
    private final By ADD_USER_BUTTON = By.cssSelector("div.add-user-group button");
    private final By SAVE_BUTTON = By.cssSelector("button[id$='-okButton-button']");
    private final By CANCEL_BUTTON = By.cssSelector("button[id$='-cancelButton-button']");
    private final By INHERIT_PERMISSION_BUTTON = By.cssSelector("div[id$='_default-inheritedButtonContainer']");
    private final By INHERIT_PERMISSION_TABLE = By.cssSelector("div[id$='_default-inheritedPermissions']");
    private final By ACCESS_TYPE_BUTTON = By.cssSelector("span[id^='roles-'] button");
    private final By  USER_LIST = By.cssSelector("div[id$='default-directPermissions'] tr[class^='yui-dt-rec']");
    private final By USER_NAME= By.cssSelector("td[class$='displayName']");
    private final By USRE_ROLE =  By.cssSelector("td[class$='role']");
    private final By LIST_USER_ROLE = By.cssSelector("span[id^='roles-']>div>div>ul>li");
    
    /**
     * Default constructor is not provided as the client should pass the
     * {@link FromClass} while creating ManagePermissionsPage.
     * @param drone
     * @param fromClass
     */
    public ManagePermissionsPage(WebDrone drone)
    {
        super(drone);
    }

    private final Log logger = LogFactory.getLog(ManagePermissionsPage.class);
    
    @SuppressWarnings("unchecked")
    @Override
    public ManagePermissionsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ManagePermissionsPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ManagePermissionsPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, RenderElement.getVisibleRenderElement(ADD_USER_BUTTON));
            if (drone.find(INHERIT_PERMISSION_BUTTON).getAttribute("class").contains("on"))
            {
                elementRender(timer, RenderElement.getVisibleRenderElement(LAST_ROW_OF_PERMISSIONS));
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return this;
    }
    
    /**
     * Mimics the action of clicking on Add user button.
     * @return
     */
    public UserSearchPage selectAddUser()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace(" - Trying to click Add User button - ");
        }
        drone.find(ADD_USER_BUTTON).click();
        return this.new UserSearchPage(drone);
    }

    /**
     * Mimics the action of clicking on Inherit permission button.
     * 
     * @param turnOn
     *            - Inherit permission to be turned on/off. Send true if you
     *            want to turn on.
     * @return
     */
    public ManagePermissionsPage toggleInheritPermission(boolean turnOn)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace(" - Trying to click Inherit permissions button - ");
        }
        boolean buttonStatusOn = getInheritButtonStatus();
        WebElement inheritButton = drone.find(INHERIT_PERMISSION_BUTTON);
        // requested turn on and the inherited premissions is off then only
        // click the button.
        if (turnOn && !buttonStatusOn)
        {
            inheritButton.findElement(By.cssSelector("button")).click();
        }
        // requested turn off and the inherited permissions is on then only
        // click the button.
        if (!turnOn && buttonStatusOn)
        {
            inheritButton.findElement(By.cssSelector("button")).click();
            try{
                //if the confirm Yes/No box appears select yes. This happens only first time.
                drone.findAndWait(By.cssSelector("div.ft  button:first-of-type"), 1000).click();
            }
            catch (Exception e)
            {
                // ignore. this confirm box does not appear everytime.
            }
        }
        return new ManagePermissionsPage(drone);
    }

    /**
     * Resolves the inherit permission button to 'on' or 'off'
     * @return
     */
    private boolean getInheritButtonStatus()
    {
        try
        {
            drone.findAndWait(By.cssSelector("div[class$='inherited-on']"));
            return true;
        }
        catch (TimeoutException e) {}
        return false;
    }

    /**
     * Clicks on save and returns the page from where user arrived to this page.
     * 
     * @return
     */
    public boolean isInheritPermissionEnabled()
    {
        try
        {
            return drone.findAndWait(INHERIT_PERMISSION_TABLE, 500).isDisplayed();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Clicks on save and returns the page from where user arrived to this page.
     * 
     * @return
     */
    public HtmlPage selectSave()
    {
        WebElement saveButton = drone.findAndWait(SAVE_BUTTON);
        String saveButtonId = saveButton.getAttribute("id");
        saveButton.click();
        drone.waitUntilElementDeletedFromDom(By.id(saveButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Clicks on cancel and returns the page from where user arrived to this
     * page.
     * 
     * @return
     */
    public HtmlPage selectCancel()
    {
        drone.findAndWait(CANCEL_BUTTON).click();
        drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * From the drop down, the access level is selected.
     * 
     * @param userRole
     */
    public void setAccessType(UserRole userRole)
    {
        if (null == userRole)
        {
            logger.info("Access type was null. Should be set to some level.");
            throw new UnsupportedOperationException("Access type cannot be null");
        }
        drone.findAndWait(ACCESS_TYPE_BUTTON).click();
        drone.find(userRole.getAccessType()).click();
    }
    
    /**
     * Check if user is already added for permission.
     * @param userName
     * @return
     */
    public boolean isUserExistForPermission(String userName)
    {
        boolean isExist = false;
        try
        {
            List<WebElement> userList = drone.findAndWaitForElements(USER_LIST);
            for (WebElement webElement : userList)
                if (webElement.findElement(USER_NAME).getText().contains(userName))
                    isExist = true;

            return isExist;
        }
        catch (TimeoutException toe)
        {
            logger.error("User name elementis not found!!");
        }
        throw new PageOperationException("User name is not found!!");
    }
    
    /**
     * Update role of existing Users in permission table.
     * @param userName
     * @param userRole
     * @return
     */
    public boolean updateUserRole(String userName, UserRole userRole)
    {        
        try{            
            List <WebElement> elements = drone.findAndWaitForElements(USER_LIST);
            for (WebElement webElement : elements)
            {                
                if(webElement.findElement(USER_NAME).getText().contains(userName))
                {
                    WebElement roleElement = webElement.findElement(USRE_ROLE);
                    roleElement.findElement(ACCESS_TYPE_BUTTON).click();
                    
                    selectRole(userRole);
                    return true;
                }
            }
        }
        catch(TimeoutException toe)
        {
            logger.error("User name is not found!!");
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Role element is not found");
        }
        throw new PageOperationException("User or Role doesnt exist!!");
    }
    /**
     * @param userRole
     */
    private void selectRole(UserRole userRole)
    {
      try{
            List <WebElement> elements = drone.findAll(LIST_USER_ROLE);
            for (WebElement webElement : elements)
            {
                if(userRole.getRoleName().equals(webElement.getText())){
                    webElement.click();
                    return;
                }
            }
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Roles element is not found");
        }
      throw new PageOperationException("Role doesnt exist!!");
    }

    /**
     * From the drop down, get the access level selected.
     * 
     * @param accessType
     */
    public UserRole getAccessType()
    {
        String role = drone.find(ACCESS_TYPE_BUTTON).getText().toUpperCase();
        return UserRole.valueOf(role);
    }

    /**
     * Checks whether a user has direct permissions present.
     * 
     * @param userProfile
     * @return
     */
    public boolean isDirectPermissionForUser(UserProfile userProfile)
    {
        List<WebElement> userPermissionRows = null;
        try
        {
            userPermissionRows = drone.findAll(By.cssSelector("div[id$='default-directPermissions'] tbody.yui-dt-data tr"));
        }
        catch (Exception e)
        {
            return false;
        }
        
        for (WebElement userPermissionRow : userPermissionRows)
        {
            String name = userPermissionRow.findElement(By.cssSelector("td[class$='-displayName']")).getText();
            if (StringUtils.equalsIgnoreCase(name, (userProfile.getfName() + " " + userProfile.getlName()).trim()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Page object for searching user and selecting user. Ideally should not live w/o
     * ManagePersmissions instance. Hence its an inner class with private constructor.
     * 
     * @author Abhijeet Bharade
     * @since 1.7.0
     */
    public class UserSearchPage extends SharePage
    {
        private final By SEARCH_USER_INPUT = By.cssSelector("div.search-text input");
        private final By SEARCH_USER_BUTTON = By.cssSelector("div.authority-search-button button");

        private final WebElement searchContainerDiv;

        private UserSearchPage(WebDrone drone)
        {
            super(drone);
            searchContainerDiv = drone.findAndWait(By.cssSelector("div.finder-wrapper"));
        }

        @SuppressWarnings("unchecked")
        @Override
        public UserSearchPage render()
        {
            return render(new RenderTime(maxPageLoadingTime));
        }

        @SuppressWarnings("unchecked")
        @Override
        public UserSearchPage render(long time)
        {
            return render(new RenderTime(time));
        }

        @SuppressWarnings("unchecked")
        @Override
        public UserSearchPage render(RenderTime timer)
        {
            while (true)
            {
                timer.start();
                try
                {
                    WebElement message = searchContainerDiv.findElement(By.cssSelector("tbody.yui-dt-message div"));
                    if (message.isDisplayed() && message.getText().contains("Searching..."))
                    {
                        continue;
                    } 
                    else 
                    {
                        break;
                    }
                }
                catch (Exception e)
                {
                }
                finally
                {
                    timer.end();
                }
            }
            elementRender(timer, RenderElement.getVisibleRenderElement(SEARCH_USER_BUTTON), RenderElement.getVisibleRenderElement(SEARCH_USER_INPUT));
            return this;
        }

        /**
         * Enters user arg in search box and clicks the search button. Goes
         * through the result, finds the appropriate user and clicks Add button.
         * @param userProfile
         */
        public ManagePermissionsPage searchAndSelectUser(UserProfile userProfile)
        {
            String userName = userProfile.getUsername();
            if (StringUtils.isEmpty(userName) || userName.length() < 3)
            {
                throw new UnsupportedOperationException("User search text cannot be blank - min three characters required");
            }

            drone.find(SEARCH_USER_INPUT).clear();
            drone.find(SEARCH_USER_INPUT).sendKeys(userName);

            drone.find(SEARCH_USER_BUTTON).click();
            this.render();
            By DATA_ROWS = By.cssSelector("div.finder-wrapper tbody.yui-dt-data tr");
            By USERNAME_SPAN = By.cssSelector("h3>span");
            By NAME_ANCHOR = By.cssSelector("h3>a");

            try
            {
                List<WebElement> searchRows = drone.findAndWaitForElements(DATA_ROWS, maxPageLoadingTime);
                for (WebElement searchRow : searchRows)
                {
                    if (searchRow.findElement(USERNAME_SPAN).getText().contains(userName))
                    {
                        String fullName = searchRow.findElement(NAME_ANCHOR).getText();
                        String[] name = fullName.split(" ");
                        userProfile.setfName(name[0]);

                        if (name.length > 1)
                            userProfile.setlName(name[1]);
                        else
                            userProfile.setlName("");

                        searchRow.findElement(By.cssSelector("button")).click();
                        return new ManagePermissionsPage(drone);
                    }
                }
                throw new PageException("User with username containing - '" + userName + "' not found");
            }
            catch (Exception e)
            {
                throw new PageException("No users found for - " + userName);
            }
        }
    }
}
