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

import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Site members dashlet object, holds all element of the HTML relating to
 * share's site members dashlet.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteMembersDashlet extends AbstractDashlet implements Dashlet
{
    private static final String DATA_LIST_CSS_LOCATION = "div.detail-list-item > div.person > h3 > a";
	private static final String DASHLET_CONTAINER_PLACEHOLDER = "div.dashlet.colleagues";
    protected WebElement dashlet;
    /**
     * Constructor.
     */
    protected SiteMembersDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER));
    }

    @SuppressWarnings("unchecked")
    public SiteMembersDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SiteMembersDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }
    
    /**
     * The member of the site that is displayed on 
     * site members dashlet.
     * 
     * @return List<ShareLink> site links
     */
    public synchronized List<ShareLink> getMembers()
    {
    		return getList(DATA_LIST_CSS_LOCATION);
    }
    
    /**
     * Retrieves the link that match the site members name.
     * @param name identifier
     * @return {@link ShareLink} that matches members name
     */
    public synchronized ShareLink selectMember(final String name)
    {
    	if(name == null) { throw new UnsupportedOperationException("Name value of link is required");}
    	List<ShareLink> shareLinks = getList(DATA_LIST_CSS_LOCATION);
        for(ShareLink link : shareLinks)
        {
        	if(name.equalsIgnoreCase(link.getDescription()))
			{
        		return link;
			}
        }
        throw new PageException(String.format("Link %s can not be found on the page, dashlet exists: %s link size: %d", name, dashlet, shareLinks.size()));
    }
    
    @SuppressWarnings("unchecked")
    public synchronized SiteMembersDashlet render(RenderTime timer)
    {
		try
		{
			while (true)
			{
			    try
			    {
	               timer.start();
	               this.dashlet = drone.findAndWait(By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER), 100L, 10L);
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
		return this;
    }
}
