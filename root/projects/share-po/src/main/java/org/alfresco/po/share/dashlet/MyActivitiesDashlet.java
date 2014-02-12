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
package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * My activities dashlet object, holds all element of the HTML page relating to
 * share's my activities dashlet on dashboard page.
 * 
 * @author Michael Suzuki
 * @since 1.3
 */
public class MyActivitiesDashlet extends AbstractDashlet implements Dashlet
{
	private static final String DIV_CLASS_DASHLET_PLACEHOLDER = "div[id$='default-activityList']";
    private static final String DASHLET_DIV_PLACEHOLDER = "div.dashlet.activities";
    public enum LinkType{ User, Document, Site; }
    private List<ActivityShareLink> activity;
    /**
     * Constructor.
     */
    protected MyActivitiesDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(DASHLET_DIV_PLACEHOLDER));
    }

    @SuppressWarnings("unchecked")
    public MyActivitiesDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public MyActivitiesDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }
    
    /**
     * Populates all the possible links that appear on the dashlet
     * data view, the links are of user, document or site.
     * @param selector css placeholder for the dashlet data
     */
    private synchronized void populateData()
    {
    	activity = new ArrayList<ActivityShareLink>();
    	try
    	{
    		List<WebElement> links = drone.findAll(By.cssSelector(DIV_CLASS_DASHLET_PLACEHOLDER + " > div.activity"));
    		
    		for(WebElement div:links)
    		{
    			WebElement userLink = div.findElement(By.cssSelector("div.activity>div.content>span.detail>a[class^='theme-color']"));
    			ShareLink user = new ShareLink(userLink, drone);
    			
                WebElement siteLink = div.findElement(By.cssSelector("div.activity>div.content>span.detail>a[class^='site-link']"));
                ShareLink site = new ShareLink(siteLink, drone);
                
                String description = div.findElement(By.cssSelector("div.content>span.detail")).getText();

    			if( div.findElements(By.cssSelector("div.activity>div.content>span.detail>a")).size() > 2 )
    			{

    	             WebElement documentLink = div.findElement(By.cssSelector("div.activity>div.content>span.detail>a[class*='item-link']"));
                     ShareLink document = new ShareLink(documentLink, drone);
                     activity.add(new ActivityShareLink(user, document, site, description));
    			}
    			else
    			{
    			    activity.add(new ActivityShareLink(user, site, description));
    			}
    		}
    	}
    	catch (NoSuchElementException nse) 
    	{
			throw new PageException("Unable to access dashlet data" , nse);
		}
    }

    @SuppressWarnings("unchecked")
    public MyActivitiesDashlet render(RenderTime timer)
    {
		try
		{
			while (true)
			{
			    timer.start();
			    try
			    {
			        synchronized (this)
			        {
			            try{ this.wait(100L); } catch (InterruptedException e) {}
			        }
			        if (isEmpty(DASHLET_DIV_PLACEHOLDER))
			        {
			            // There are no results
			            break;
			        }
			        else if (isVisibleResults())
			        {
			            // Results are visible
			            break;
			        }
			    }
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
    
    /**
     * Select a link from activity list by a given name
     * with a default of document type, as there are additional 
     * links such as user or site in the same web element.
     * @param name identifier to match against link title
     */
    public ActivityShareLink selectLink(final String name)
    {
    	return selectLink(name, LinkType.Document);
    }
    
    /**
     * Find the match and selects on the link.
     * @param name identifier to match against link title
     * @param enum that determines document, site or user type link
     */
    private synchronized ActivityShareLink selectLink(final String name, LinkType type)
    {
    	if(name == null) { throw new UnsupportedOperationException("Name value of link is required");}
		if(activity == null)
		{
			populateData();
		}
        for(ActivityShareLink link: activity)
        {
            ShareLink theLink = null;
            switch (type)
            {
                case Document: 
                    theLink = (link.getDocument() != null) ?  link.getDocument() : null ; 
                    break;
                case Site: theLink = link.getSite(); break;
                case User: theLink = link.getUser(); break;
            }
            if(theLink != null && name.equalsIgnoreCase(theLink.getDescription()))
            {
                return link;
            }

        }
        throw new PageException("Link searched for can not be found on the page");
	}
    
    /**
     * Selects the document link on the activity that appears on my activities dashlet 
     * by matching the name to the link.
     * @param name identifier
     */
    public HtmlPage selectActivityDocument(final String name)
    {
		selectLink(name, LinkType.Document);
    	throw new PageException("no documents found matching the given title: " + name);
    }
    /**
     * Selects the user link on an activity that appears on my activities dashlet 
     * by matching the name to the link.
     * @param name identifier
     */
    public HtmlPage selectActivityUser(final String name)
    {
		selectLink(name, LinkType.User);
    	throw new PageException("no documents found matching the given title: " + name);
    }
    /**
     * Selects a the site link on an activity that appears on my activities dashlet 
     * by matching the name to the link.
     * @param name identifier
     */
    public HtmlPage selectActivitySite(final String name)
    {
		selectLink(name, LinkType.User);
    	throw new PageException("no documents found matching the given title: " + name);
    }

    /**
     * Get Activities based on the link type.
     * @param linktype Document, User or Site 
     * @return {@link ShareLink} collection
     */
	public synchronized List<ActivityShareLink> getActivities() {
		if(activity == null)
		{
			populateData();
		}
		return activity;
	}
}