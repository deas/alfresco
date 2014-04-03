/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 *//*
    * Copyright (C) 2005-2013 Alfresco Software Limited.
    * This file is part of Alfresco
    * Alfresco is free software: you can redistribute it and/or modify
    * it under the terms of the GNU Lesser General Public License as published by
    * the Free Software Foundation, either version 3 of the License, or
    * (at your option) any later version.
    * Alfresco is distributed in the hope that it will be useful,
    * but WITHOUT ANY WARRANTY; without even the implied warranty of
    * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    * GNU Lesser General Public License for more details.
    * You should have received a copy of the GNU Lesser General Public License
    * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
    */
package org.alfresco.po.share.site;

import java.util.List;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author nshah
 *         To Add new Groups this page is used.
 */
public class AddGroupsPage extends SharePage
{

    public static final String SEARCH_INPUT_TEXT = "input[id$='-search-text']";
    public static final String SEARCH_RESULT_ROW = "tr[class^='yui-dt-rec']";
    public static final String SEARCH_BUTTON = "button[id$='group-search-button-button']";
    public static final String GROUP_DISPLAY_NAME = "td[class$='yui-dt-col-description']  div >h3.itemname";
    public static final String ADD_BUTTON = "td[class*='yui-dt-col-actions'] button";
    public static final String ADDED_GROUP = "td[class*='yui-dt-col-item'] div h3.itemname";
    public static final String ROW_ROLE_BUTTON = "td[class*='yui-dt-col-role'] button";
    public static final String ROW_ROLE_OPTION = "div[id$='inviteelist'] div > ul > li > a";
    public static final String ADD_GROUP = "button[id$='add-button-button']";

    public AddGroupsPage(WebDrone drone)
    {
        super(drone);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AddGroupsPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(By.cssSelector(ADD_GROUP)), getVisibleRenderElement(By.cssSelector(SEARCH_INPUT_TEXT)));
        }
        catch (NoSuchElementException e)
        {
        }
        catch (TimeoutException e)
        {
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AddGroupsPage render(long time)
    {

        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AddGroupsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @param groupDisplayName
     * @return
     */
    private AddGroupsPage addGroupToSite(String groupDisplayName)
    {
        if (StringUtils.isEmpty(groupDisplayName))
        {
            throw new IllegalArgumentException("Enter value of group Display Name");
        }
        try
        {
            List<WebElement> searchResultRows = drone.findAndWaitForElements(By.cssSelector(SEARCH_RESULT_ROW));
            for (WebElement row : searchResultRows)
            {
                String resultGroupName = row.findElement(By.cssSelector(GROUP_DISPLAY_NAME)).getText();
                if (groupDisplayName.equals(resultGroupName))
                {
                    WebElement addButton = drone.find(By.cssSelector(ADD_BUTTON));
                    if (addButton.isEnabled())
                    {
                        addButton.click();
                        return new AddGroupsPage(drone);
                    }
                    else
                    {
                        throw new PageException("Group is already added");
                    }
                }
            }
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element:" + SEARCH_RESULT_ROW, toe);
        }
        throw new PageException("Group does not exist!!");
    }

    /**
     * @param groupDisplayName
     * @return
     */
    private AddGroupsPage searchGroup(String groupDisplayName)
    {
        if (StringUtils.isEmpty(groupDisplayName))
        {
            throw new IllegalArgumentException("Enter value of group Display Name");
        }
        try
        {
            WebElement input = drone.find(By.cssSelector(SEARCH_INPUT_TEXT));
            input.clear();
            input.sendKeys(groupDisplayName);
            drone.findAndWait(By.cssSelector(SEARCH_BUTTON)).click();
            return new AddGroupsPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Not visible Element:" + SEARCH_INPUT_TEXT, nse);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element:" + SEARCH_BUTTON, toe);
        }

    }

    /**
     * Assign role to group.
     * 
     * @param groupDisplayName
     * @param roleToAssign
     * @return
     */
    private AddGroupsPage assignRoleToGroup(String groupDisplayName, UserRole roleToAssign)
    {
        try
        {
            if (drone.find(By.cssSelector(ADDED_GROUP)).isDisplayed())
            {
                drone.find(By.cssSelector(ROW_ROLE_BUTTON)).click();

                List<WebElement> listOfRoles = drone.findAll(By.cssSelector("div[id$='inviteelist'] div > ul > li > a"));

                for (WebElement role : listOfRoles)
                {
                    if (roleToAssign.toString().equalsIgnoreCase(role.getText()))
                    {
                        role.click();
                        return new AddGroupsPage(drone);
                    }
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Group does not exist", nse);
        }
        throw new PageException("Group does not exist!!");
    }

    /**
     * @param groupDisplayName
     * @param roleToAssign
     * @return
     */
    public AddGroupsPage addGroup(String groupDisplayName, UserRole roleToAssign)
    {
        searchGroup(groupDisplayName).render();
        addGroupToSite(groupDisplayName).render();
        assignRoleToGroup(groupDisplayName, roleToAssign).render();
        clickAddGroupsButton();
        return new AddGroupsPage(drone);
    }

    private void clickAddGroupsButton()
    {
        try
        {

            WebElement addGroupsButton = drone.find(By.cssSelector(ADD_GROUP));
            if (addGroupsButton.isEnabled())
            {
                addGroupsButton.click();
                canResume();
            }
            else
            {
                throw new PageOperationException("Add Group is disabled!!");
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Not found element is : " + ADD_GROUP, nse);
        }
    }

    /**
     * @param groupDisplayName
     * @param roleToAssign
     * @return
     */
    public boolean isGroupAdded(String groupDisplayName)
    {
        try
        {
            searchGroup(groupDisplayName);
            List<WebElement> searchResultRows = drone.findAndWaitForElements(By.cssSelector(SEARCH_RESULT_ROW));
            for (WebElement row : searchResultRows)
            {
                String resultGroupName = row.findElement(By.cssSelector(GROUP_DISPLAY_NAME)).getText();
                if (groupDisplayName.equals(resultGroupName))
                {
                    WebElement addButton = drone.find(By.cssSelector(ADD_BUTTON));
                    if (!addButton.isEnabled())
                    {
                        return true;
                    }
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Group not found", nse);
        }
        return false;
    }
}
