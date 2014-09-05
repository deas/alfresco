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

package org.alfresco.po.thirdparty.pentaho;


import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Page object for pentaho user console page 
 * 
 * @author jcule
 *
 */
public class PentahoUserConsolePage extends SharePage
{
    
    private static Log logger = LogFactory.getLog(PentahoUserConsolePage.class);
    
    private final static String HOME = "div[id='mantle-perspective-switcher'] table tbody tr td div[class='custom-dropdown-label']";
    private final static String USERNAME = "div[id='pucUserDropDown'] table tbody tr td div[class='custom-dropdown-label']";
    private final static String LOGOUT = "div[id='customDropdownPopupMinor'] td[id='gwt-uid-4']";
    
    public PentahoUserConsolePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PentahoUserConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public PentahoUserConsolePage render(RenderTime timer)
    {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PentahoUserConsolePage render(final long time)
    {
        return render(new RenderTime(time));
    }
    
    public PentahoUserConsolePage renderHomeTitle(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector(HOME)));
        return this;
    }
 
    /**
     * Checks if Home title is displayed
     * 
     * @return
     */
    public boolean isHomeTitleVisible()
    {
        try
        {
            WebElement documentTitle = drone.find(By.cssSelector(HOME));
            return documentTitle.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Home title " + nse);
            throw new PageException("Unable to find Home title.", nse);
        }
    }
    
    /**
     * Gets logged in username 
     * 
     * @return
     */
    public String getLoggedInUser()
    {
        try
        {
            String username = drone.find(By.cssSelector(USERNAME)).getText();
            return username;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No username " + nse);
            throw new PageException("Unable to find user name.", nse);
        }
    }
    
    /**
     * Clicks on Logout link
     */
    public void clickOnLoggedInUser()
    {
        try
        {
            WebElement username = drone.findAndWait(By.cssSelector(USERNAME));
            username.click();
         
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find user link. " + te);
        }
    }
    
    /**
     * Clicks on Logout link
     */
    public void clickOnLogoutLink()
    {
        try
        {
            WebElement logout = drone.findAndWait(By.cssSelector(LOGOUT));
            logout.click();
         
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find logout link. " + te);
        }
    }
}
