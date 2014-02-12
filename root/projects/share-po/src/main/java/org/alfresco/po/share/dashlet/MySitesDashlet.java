/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * My site dashlet object, holds all element of the HTML page relating to share's my site dashlet on dashboard page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class MySitesDashlet extends AbstractDashlet implements Dashlet
{
    private static final String DATA_LIST_CSS_LOCATION = "h3.site-title > a";
    private static final String DASHLET_CONTAINER_PLACEHOLDER = "div.dashlet.my-sites";
    private static final String DASHLET_CONTENT_DIV_ID_PLACEHOLDER = "div[id$='default-sites']";
    private static final String DASHLET_EMPTY_PLACEHOLDER = "table>tbody>tr>td.yui-dt-empty>div";

    /**
     * Constructor.
     */
    protected MySitesDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER));
    }

    @SuppressWarnings("unchecked")
    public MySitesDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MySitesDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Implemented a new render method with optionally waiting for 'Loading...' message to go away
     */
    @SuppressWarnings("unchecked")
    public MySitesDashlet render(RenderTime timer)
    {
        return render(timer, true);
    }

    /**
     * The active sites displayed on my site dashlet.
     * 
     * @return List<ShareLink> site links
     */
    public synchronized List<ShareLink> getSites()
    {
        return getList(DATA_LIST_CSS_LOCATION);
    }

    /**
     * Retrieves the link that match the site name.
     * 
     * @param name identifier
     * @return {@link ShareLink} that matches siteName
     */
    public synchronized ShareLink selectSite(final String name)
    {
        if (name == null)
        {
            throw new UnsupportedOperationException("Name value of link is required");
        }
        List<ShareLink> shareLinks = getList(DATA_LIST_CSS_LOCATION);
        for (ShareLink link : shareLinks)
        {
            if (name.equalsIgnoreCase(link.getDescription()))
            {
                return link;
            }
        }
        throw new PageException(String.format("Link %s can not be found on the page, dashlet exists: %s link size: %d", name, dashlet, shareLinks.size()));
    }

    /**
     * Render logic to determine if loaded and ready for use.
     * @param timer - {@link RenderTime}
     * @param waitForLoading boolean to whether check for waiting for Loading text to disappear.
     * @return {@link MySitesDashlet}
     */
    public synchronized MySitesDashlet render(RenderTime timer, boolean waitForLoading)
    {
        try
        {
            while (true)
            {
                try
                {
                    timer.start();
                    WebElement dashlet = drone.find(By.cssSelector(DASHLET_CONTENT_DIV_ID_PLACEHOLDER));
                    if (dashlet.isDisplayed())
                    {
                        this.dashlet = drone.find(By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER));
                        if (waitForLoading)
                        {
                            if (!isLoading(dashlet))
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }

                }
                catch (NoSuchElementException e){ }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    private boolean isLoading(WebElement dashletPlaceholder)
    {
        try
        {
            WebElement sitesDash = dashletPlaceholder.findElement(By.cssSelector(DASHLET_EMPTY_PLACEHOLDER));
            if (sitesDash.isDisplayed() && sitesDash.getText().startsWith("Loading..."))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        
    }

    /**
     * Checks the site is favourite.
     * 
     * @return
     */
    public boolean isSiteFavourite(String siteName)
    {
        try
        {
            WebElement siteRow = getSiteRow(siteName);

            // If site is favourite, anchor does not contain any text. Checking
            // length of text rather than string 'Favourite' to support i18n.
            return !(siteRow.findElement(By.cssSelector("div > span > a")).getText().length() > 1);
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Returns the div that hold the site info.
     * 
     * @return
     */
    private WebElement getSiteRow(String siteName)
    {
        if (siteName == null)
        {
            throw new UnsupportedOperationException("Name of the site is required");
        }
        return this.dashlet.findElement(By.xpath("//h3[a='" + siteName + "']/.."));
    }

}
