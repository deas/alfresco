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
package org.alfresco.po.share.site;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
/**
 * @author nshah
 * To add Groups with a Site.
 */
public class SiteGroupsPage extends SharePage
{
    public static final String ADD_GROUPS = "a[id$='-addGroups-button']";

    public SiteGroupsPage(WebDrone drone)
    {
        super(drone);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SiteGroupsPage render(RenderTime timer)
    {
     
        try
        {
            elementRender(timer, getVisibleRenderElement(By.cssSelector(ADD_GROUPS)));
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
    public  SiteGroupsPage render(long time)
    {
    
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteGroupsPage  render()
    {  
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    /**
     * Navigate to Add Groups Page.
     * @return
     */
    public HtmlPage navigateToAddGroupsPage()
    {
        try
        {
            drone.find(By.cssSelector(ADD_GROUPS)).click();
            return new AddGroupsPage(drone);
        }
        catch(NoSuchElementException nse)
        {
            throw new PageException("Element:"+ADD_GROUPS+" not found", nse);
        }
    }
    
    /**
     * @return
     */
    public boolean isSiteGroupsPage()
    {
        try
        {
           if(drone.find(By.cssSelector(ADD_GROUPS)).isDisplayed())
           {
            return true;
           }
        }
        catch(NoSuchElementException nse)
        {
            throw new PageException("Element:"+ADD_GROUPS+" not found", nse);
        }
        throw new PageOperationException("Not a SiteGroups Page");
    }

}
