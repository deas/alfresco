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
package org.alfresco.po.share;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;
import java.util.List;

import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * User Profile page object
 * 
 * @author Chiran
 * @since 1.7.1
 */
public class UserProfilePage extends SharePage
{
    private static final String USER_DETAILS = ".view-main.separator";
    private static final String GO_BACK_BUTTON = "button[id$='default-goback-button-button']";
    private static final String EDIT_USER = "button[id$='default-edituser-button-button']";
    private static final String DELETE_USER = "button[id$='default-deleteuser-button-button']";
    private static final String BUTTON_GROUP = "span.button-group>span>span>button";

    private final RenderElement USER_DETAILS_ELEMENT = getVisibleRenderElement(By.cssSelector(USER_DETAILS));
    private final RenderElement GO_BACK_BUTTON_ELEMENT = getVisibleRenderElement(By.cssSelector(GO_BACK_BUTTON));
    private final RenderElement EDIT_USER_ELEMENT = getVisibleRenderElement(By.cssSelector(EDIT_USER));
    private final RenderElement DELETE_USER_ELEMENT = getVisibleRenderElement(By.cssSelector(DELETE_USER));

    /**
     * Constructor.
     * @param drone 
     */
    public UserProfilePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserProfilePage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, USER_DETAILS_ELEMENT, GO_BACK_BUTTON_ELEMENT, EDIT_USER_ELEMENT, DELETE_USER_ELEMENT);
            while(true)
            {
                timer.start();
                synchronized (this)
                {
                    try{ this.wait(100L); } catch (InterruptedException e) {}
                }
                try
                {
                    if(!isJSMessageDisplayed())
                    {
                        break;
                    }
                }
                catch (NoSuchElementException nse) { }
                    timer.end();
            }
        }
        catch (NoSuchElementException e) {}
        catch (TimeoutException e) {}

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserProfilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserProfilePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Gets the buttons present on Delete confirmation window.
     * @return List<WebElement>
     */
    private List<WebElement> getButtons()
    {
        try
        {
            return drone.findAndWaitForElements(By.cssSelector(BUTTON_GROUP));
        }
        catch (TimeoutException te) {}
        throw new PageException("Not able to find the Button group.");
    }

    /**
     * This method deletes the User through clicking on deleteUser button.
     * 
     * @return UserSearchPage
     */
    public UserSearchPage deleteUser()
    {
        try
        {
            drone.findAndWait(By.cssSelector(DELETE_USER)).click();
            
            List<WebElement> buttons = getButtons();

            for (WebElement button : buttons)
            {
                if (button.getText().equalsIgnoreCase("Delete"))
                {
                    button.click();
                    return new UserSearchPage(drone);
                }
            }
        }
        catch (TimeoutException te) {}
        throw new PageException("Not able to find the Delete User Button.");
    }
    
    /**
     * This method clicks on Edit User.
     * 
     * @return NewUserPage
     */
    public EditUserPage selectEditUser()
    {
        try
        {
            drone.findAndWait(By.cssSelector(EDIT_USER)).click();
            return new EditUserPage(drone);
        }
        catch (TimeoutException te) {}
        throw new PageException("Not able to find the Edit User Button.");
    }
    
    /**
     * This method clicks on GoBack.
     * 
     * @return UserSearchPage
     */
    public UserSearchPage selectGoBack()
    {
        try
        {
            drone.findAndWait(By.cssSelector(GO_BACK_BUTTON)).click();
            return new UserSearchPage(drone);
        }
        catch (TimeoutException te) {}
        throw new PageException("Not able to find the GoBack Button.");
    }
}