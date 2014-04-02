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
package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Abstract of an Alfresco Share dashlet web element.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
abstract class AbstractDashlet extends SharePage
{
    protected WebElement dashlet;
    private By resizeHandle;

    /**
     * Constructor.
     */
    protected AbstractDashlet(WebDrone drone, By by)
    {
        super(drone);
        try
        {
            this.dashlet = drone.findAndWait(by, 100L);
        }
        catch (Exception e) 
        {
            // We treat this as an empty dashlet (it might not be present)
        }
    }

    /**
     * Gets the title on the dashlet panel.
     * @return String dashlet title
     */
    public synchronized String getDashletTitle()
    {
        if (dashlet == null)
        {
            throw new IllegalStateException("Dashlet is not visible.");
        }
        return dashlet.findElement(By.cssSelector("div.title")).getText();
    }
    
    /**
     * Checks if dashlet is empty by verifying that dashlet div class empty
     * is not displayed.
     * @param dashlet css locator
     * @return true if empty
     */
    protected synchronized boolean isEmpty(final String css)
    {
        if (dashlet == null)
        {
            return true;
        }
    	try
    	{
    		String selector = css + " div.empty";
    		boolean empty = drone.find(By.cssSelector(selector)).isDisplayed();
    		return empty;
    	}
    	catch (NoSuchElementException te) 
    	{
    	    return false;
    	}
    }
    
    /**
     * Check if results table is populated.
     * @return true when results are displayed
     */
    protected synchronized boolean isVisibleResults()
    {
        if (dashlet == null)
        {
            return true;
        }
    	try
    	{
    	    return drone.find(By.cssSelector("tbody.yui-dt-data tr")).isDisplayed();
    	}
    	catch (NoSuchElementException nse)
    	{
    	    return false;
    	}
    }
    
    /**
     * Populates the data seen in dashlet.
     */
    protected synchronized List<ShareLink> getList(final String csslocator)
    {
        if(csslocator == null || csslocator.isEmpty()) 
    	{
    		throw new UnsupportedOperationException("Selector By value is required");
    	}
    	//Populate ShareLinks with content in dashlet
        List<WebElement> links = dashlet.findElements(By.cssSelector(csslocator));
        if(links == null)
        {
            return Collections.emptyList();
        }

        List<ShareLink> shareLinks = new ArrayList<ShareLink>();
    	for (WebElement site : links)
    	{
    		shareLinks.add(new ShareLink(site, drone));
    	}
        return shareLinks;
    }
    
    protected synchronized boolean renderBasic(RenderTime timer,final String css)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try{ this.wait(50L); } catch (InterruptedException e) {}
                }
                try
                {
                   this.dashlet = drone.find(By.cssSelector(css));
                   break;
                }
                catch (Exception e) { }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te) 
        {
            throw new PageException(this.getClass().getName() + " failed to render in time",te);
        }
        return true;
    }
    /**
     * Retries the {@link ShareLink} object that matches the title.
     * @param cssLocation String css selector description
     * @param title String identifier to match
     * @return {@link ShareLink} link that matches the title
     */
    protected synchronized ShareLink getLink(final String cssLocation, final String title)
    {
        if(null == cssLocation || cssLocation.isEmpty())
        {
            throw new UnsupportedOperationException("css location value is required");
        }
        if(null == title || title.isEmpty())
        {
            throw new UnsupportedOperationException("title value is required");
        }
        List<ShareLink> shareLinks = getList(cssLocation);
        for(ShareLink link : shareLinks)
        {
            if(title.equalsIgnoreCase(link.getDescription()))
            {
                return link;
            }
        }
        throw new PageException("no documents found matching the given title: " + title);
    }
    
    /**
     * This method is used to scroll down the current window.
     */
    protected void scrollDownToDashlet()
    {
        drone.findAndWait(resizeHandle).click();
    }

    protected void setResizeHandle(By resizeHandle)
    {
        this.resizeHandle = resizeHandle;
    }

    public By getResizeHandle()
    {
        return resizeHandle;
    }

}
