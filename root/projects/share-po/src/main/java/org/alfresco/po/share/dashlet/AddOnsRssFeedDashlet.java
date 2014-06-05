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

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Aliaksei Boole
 */
public class AddOnsRssFeedDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.xpath("//div[count(./div[@class='toolbar'])=1 and contains(@class,'rssfeed')]");
    private static final By titleBarActions = By.cssSelector(".titleBarActions");

    /**
     * Constructor.
     */
    protected AddOnsRssFeedDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.xpath(".//div[contains (@class, 'yui-resize-handle')]"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized AddOnsRssFeedDashlet render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(DASHLET_CONTAINER_PLACEHOLDER));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AddOnsRssFeedDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AddOnsRssFeedDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on Site RSS Feed Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(dashlet.findElement(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to click Configure icon from RSS Feed dashlet
     *
     * @return RssFeedUrlBoxPage
     */
    public RssFeedUrlBoxPage clickConfigure()
    {
        try
        {
            drone.mouseOverOnElement(drone.find(titleBarActions));
            dashlet.findElement(CONFIGURE_DASHLET_ICON).click();
            return new RssFeedUrlBoxPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find configure button");
        }
    }
}
