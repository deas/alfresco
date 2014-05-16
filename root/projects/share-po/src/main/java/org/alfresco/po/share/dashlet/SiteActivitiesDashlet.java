/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet.LinkType;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Site activities dashlet object, holds all element of the HTML relating to dashlet site activity.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteActivitiesDashlet extends AbstractDashlet implements Dashlet
{
    private static final String DASHLET_CONTAINER_PLACEHOLDER = "div.dashlet.activities";
    private static final By RSS_FEED_BUTTON = By.cssSelector(".titleBarActionIcon.rss");
    private List<ShareLink> userLinks;
    private List<ShareLink> documetLinks;
    private List<String> activityDescriptions;

    /**
     * Constructor.
     */
    protected SiteActivitiesDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER));
        setResizeHandle(By.cssSelector("div.dashlet.activities .yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    public SiteActivitiesDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteActivitiesDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Populates all the possible links that appear on the dashlet
     * data view, the links are of user, document or site.
     * 
     * @param selector css placeholder for the dashlet data
     */
    private synchronized void populateData()
    {
        userLinks = new ArrayList<ShareLink>();
        documetLinks = new ArrayList<ShareLink>();
        activityDescriptions = new ArrayList<String>();
        try
        {
            List<WebElement> links = drone.findAll(By.cssSelector("div[id$='default-activityList'] > div.activity"));
            for (WebElement div : links)
            {
                WebElement userLink = div.findElement(By.cssSelector("a:nth-of-type(1)"));
                userLinks.add(new ShareLink(userLink, drone));

                WebElement documentLink = div.findElement(By.cssSelector("a:nth-of-type(2)"));
                documetLinks.add(new ShareLink(documentLink, drone));

                WebElement desc = div.findElement(By.cssSelector("div.content>span.detail"));
                activityDescriptions.add(desc.getText());
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access dashlet data", nse);
        }
    }

    /**
     * Selects the document link on the activity that appears on my activities dashlet
     * by matching the name to the link.
     * 
     * @param name identifier
     * @return {@link ShareLink} target link
     */
    public ShareLink selectActivityDocument(final String name)
    {
        return selectLink(name, LinkType.Document);
    }

    /**
     * Selects the user link on an activity that appears on my activities dashlet
     * by matching the name to the link.
     * 
     * @param name identifier
     * @return {@link ShareLink} target link
     */
    public ShareLink selectActivityUser(final String name)
    {
        return selectLink(name, LinkType.User);
    }

    /**
     * Find the match and selects on the link.
     * 
     * @param name identifier to match against link title
     * @param enum that determines document, site or user type link
     */
    private synchronized ShareLink selectLink(final String name, LinkType type)
    {
        if (name == null)
        {
            throw new UnsupportedOperationException("Name value of link is required");
        }
        if (userLinks == null || documetLinks == null)
        {
            populateData();
        }
        switch (type)
        {
            case Document:
                return extractLink(name, documetLinks);
            case User:
                return extractLink(name, userLinks);
            default:
                throw new IllegalArgumentException("Invalid link type specified");
        }
    }

    /**
     * Extracts the link from the ShareLink List that matches
     * the title.
     * 
     * @param name Title identifier
     * @param list Collection of ShareList
     * @return ShareLink link match
     */
    private ShareLink extractLink(final String name, List<ShareLink> list)
    {
        if (StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("title of item is required");
        }
        if (!list.isEmpty())
        {
            for (ShareLink link : list)
            {
                if (name.equalsIgnoreCase(link.getDescription()))
                {
                    return link;
                }
            }
        }
        throw new PageException(String.format("Link searched: %s can not be found on the page", name));
    }

    @SuppressWarnings("unchecked")
    public synchronized SiteActivitiesDashlet render(RenderTime timer)
    {
        if (renderBasic(timer, DASHLET_CONTAINER_PLACEHOLDER))
        {
            return this;
        }
        throw new PageException(this.getClass().getName() + " failed to render in time");
    }

    /**
     * Get Activities based on the link type.
     * 
     * @param linktype Document, User or Site
     * @return {@link ShareLink} collection
     */
    public synchronized List<ShareLink> getSiteActivities(LinkType linktype)
    {
        if (linktype == null)
        {
            throw new UnsupportedOperationException("LinkType is required");
        }
        populateData();
        switch (linktype)
        {
            case Document:
                return documetLinks;
            case User:
                return userLinks;
            default:
                throw new IllegalArgumentException("Invalid link type specified");
        }
    }

    /**
     * Get Activities descriptions.
     * 
     * @return {@String} collection
     */
    public synchronized List<String> getSiteActivityDescriptions()
    {

        if (activityDescriptions == null)
        {
            populateData();
        }

        return activityDescriptions;
    }

    /**
     * Method to verify whether RSS Feed is available
     *
     * @return boolean
     */
    public boolean isRssBtnDisplayed()
    {
        try
        {
            return drone.isElementDisplayed(RSS_FEED_BUTTON);
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
}
