/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * SiteContentBreakdownDashlet page object for site content breakdown report dashlet
 * 
 * @author jcule
 */
public class SiteContentBreakdownDashlet extends AbstractDashlet implements Dashlet
{

    private static Log logger = LogFactory.getLog(SiteContentBreakdownDashlet.class);

    private static final String SITE_CONTENT_REPORT_DASHLET = "div[id*='SiteContentReportDashlet']";
    private static final String MIME_TYPES = "div[id*='SiteContentReportDashlet'] svg g text:nth-of-type(1)";
    private static final String MIME_TYPES_COUNTS = "div[id*='SiteContentReportDashlet'] svg g text:nth-of-type(2)";

    /**
     * Constructor
     * 
     * @param drone
     */
    protected SiteContentBreakdownDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(SITE_CONTENT_REPORT_DASHLET));
    }

    @SuppressWarnings("unchecked")
    public SiteContentBreakdownDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteContentBreakdownDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    public SiteContentBreakdownDashlet render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector(SITE_CONTENT_REPORT_DASHLET)));
        return this;
    }

    /**
     * Gets the list of mime type counts
     * 
     * @return
     */
    public List<String> getMimeTypeCounts()
    {
        List<String> mimeTypeCounts = new ArrayList<String>();
        try
        {

            List<WebElement> counts = drone.findAll(By.cssSelector(MIME_TYPES_COUNTS));
            if (counts.size() > 0)
            {
                for (WebElement count : counts)
                {
                    mimeTypeCounts.add(count.getText());
                }
            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("No mime type counts " + nse);
        }
        return mimeTypeCounts;
    }

    /**
     * Gets the list of mime types
     * 
     * @return
     */
    public List<String> getMimeTypes()
    {
        List<String> mimeTypes = new ArrayList<String>();
        try
        {

            List<WebElement> types = drone.findAll(By.cssSelector(MIME_TYPES));
            if (types.size() > 0)
            {
                for (WebElement type : types)
                {
                    mimeTypes.add(type.getText());
                }
            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("No mime types " + nse);
        }
        return mimeTypes;
    }

}
