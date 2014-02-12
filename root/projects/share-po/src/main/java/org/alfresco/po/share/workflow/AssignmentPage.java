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
package org.alfresco.po.share.workflow;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Assignment page is to select the cloud reviewer in the workflow start form.
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public class AssignmentPage extends SharePage
{
    private static Log logger = LogFactory.getLog(AssignmentPage.class);
    private static final By SEARCH_TEXT = By.cssSelector("div[style^='visibility: visible'] input[id$='searchText']");
    private static final By SEARCH_BUTTON = By.cssSelector("div[style^='visibility: visible'] button[id$='searchButton-button']");
    private static final By OK_BUTTON = By.cssSelector("div[style^='visibility: visible'] button[id$='cntrl-ok-button']");
    private static final By SELECT_CLOUD_REVIEWER = By.cssSelector("a[title='Add']>span");
    private static final By WAIT_CLOUD_REVIEWER = (By.cssSelector("div[style^='visibility: visible'] div[id$='cntrl-picker-results']>table>tbody>tr>td>div>h3, div[id$='-cntrl-picker-left']>div[id$='_assignment-cntrl-picker-results']>table>tbody.yui-dt-message>tr>td.yui-dt-empty>div.yui-dt-liner"));
    private static final By LIST_CLOUD_REVIEWER = (By.cssSelector("div[style^='visibility: visible'] div[id$='picker-results']"));
    /**
     * Constructor.
     * 
     * @param drone
     *            WebDriver to access page
     */
    public AssignmentPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AssignmentPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AssignmentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AssignmentPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to select the cloud reviewer and submit.
     * 
     * @return Assignment Page return type clod sync cloud user part will be
     *         modified after completing other page objects.
     */
    public void selectAssignment(List<String> userNames)
    {
        
        for (String userName : userNames)
        {
            List<WebElement> elements = retrieveCloudUsers(userName);
            for (WebElement webElement : elements) {
                if(webElement.findElement(By.cssSelector(".item-name")).getText().contains("("+userName+")"))
                {
                    Dimension dimension =((WebDroneImpl) drone).getDriver().manage().window().getSize();
                    ((WebDroneImpl)drone).getDriver().manage().window().maximize();
                    drone.mouseOverOnElement(webElement.findElement(SELECT_CLOUD_REVIEWER));
                    webElement.findElement(SELECT_CLOUD_REVIEWER).click();
                    ((WebDroneImpl)drone).getDriver().manage().window().setSize(dimension);
                    break;
                }
            }
        }
        drone.findAndWait(OK_BUTTON).click();
    }
    
    /**
     * Method to get the cloud reviewers
     * 
     * @return List of users
     */
    public List<WebElement> retrieveCloudUsers(String userName)
    {
        try
        {
            searchForUser(userName);
            return drone.findAndWaitForElements(LIST_CLOUD_REVIEWER);
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding element" + toe.getMessage());
        }
        throw new PageException();
    }

    /**
     * Method to search for given user
     * @param userName
     */
    public void searchForUser(String userName)
    {
        if(StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("UserName cannot be null");
        }
        try
        {
            WebElement searchText = drone.findAndWait(SEARCH_TEXT);
            searchText.clear();
            searchText.sendKeys(userName);
            drone.findAndWait(SEARCH_BUTTON).click();
            drone.waitForElement(WAIT_CLOUD_REVIEWER, 2);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Element Not found" + nse.getMessage());
        }
        catch (TimeoutException toe)
        {
            logger.error("Search button not found" + toe.getMessage());
        }
    }

    /**
     * Method to check if "No items found" message is displayed
     * @param userName
     * @return True if "No items found" message is displayed
     */
    public boolean isNoItemsFoundMessageDisplayed(String userName)
    {
        if(StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("UserName cannot be null");
        }
        List<WebElement> users = retrieveCloudUsers(userName);
        return (users.size() == 1 && users.get(0).getText().equals("No items found"));
    }

    /**
     * Method to check if a given user found after search.
     * @param userName
     * @return True if given user found
     */
    public boolean isUserFound(String userName)
    {
        if(StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("UserName cannot be null");
        }
        List<WebElement> users = retrieveCloudUsers(userName);
        for (WebElement user : users)
        {
            if(users.size() == 1 && users.get(0).getText().equals("No items found"))
            {
                return false;
            }
            else if(user.findElement(By.cssSelector(".item-name")).getText().contains("("+userName+")"))
            {
                return true;
            }
        }
        return false;
    }
}
