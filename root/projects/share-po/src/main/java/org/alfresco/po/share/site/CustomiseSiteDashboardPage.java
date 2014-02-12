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

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Customise site dashboard page object, holds all element of the html page relating to
 * customise site dashboard page.
 * 
 * @author Shan Nagarajan
 * @since  1.6.1
 */
public class CustomiseSiteDashboardPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By CHANGE_LAYOUT_BUTTON = By.cssSelector("button[id*='change-button']");
    private static final By ADD_DASHLET_BUTTON = By.cssSelector("button[id*='addDashlets-button']");
    private static final By TRASHCAN = By.cssSelector(".trashcan");
    private static final By SAVE_BUTTON = By.cssSelector("button[id$=save-button-button]");
    private static final By AVAILABLE_DASHLETS = By.cssSelector(".availableList>li>div.dnd-draggable");
    private static final String DRAGABLE_COLUMN_FORMAT = "ul[id$='column-ul-%d']>li>div.dnd-draggable";
    private static final String COLUMN_FORMAT = "ul[id$='column-ul-%d']";
    private static final int NUMBER_OF_COLUMNS = 4;
    private static final int MAX_DASHLETS_IN_COLUMN = 5;

    /**
     * Constructor.
     */
    public CustomiseSiteDashboardPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized CustomiseSiteDashboardPage render(RenderTime timer)
    {
        while(true)
        {
        	timer.start();
        	try
        	{
        		if(drone.find(CHANGE_LAYOUT_BUTTON).isEnabled())
        		{
        			break;
        		}
        	}
        	catch (NoSuchElementException nse) { }
        	finally
        	{
        		timer.end();
        	}
        }
        
        return this;
    }

	@SuppressWarnings("unchecked")
    @Override
    public CustomiseSiteDashboardPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomiseSiteDashboardPage render(final long time)
    {
        return render(new RenderTime(time));
    }
    
    /**
     * Mimics the action of selection change layout button.
     * 
     * @return {@link CustomiseSiteDashboardPage}
     */
    public CustomiseSiteDashboardPage selectChangeLayou()
    {
        drone.find(CHANGE_LAYOUT_BUTTON).click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Change Layout button has been found and selected");
        }
        
        return new CustomiseSiteDashboardPage(drone);
    }
    
    /**
     * Mimics the action of the Add Dashlets button click.
     */
    public void selectAddDashlets()
    {
        drone.find(ADD_DASHLET_BUTTON).click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Add Dashlet button has been found and selected");
        }
    }
    
    /**
     * Mimics the action of removing the all dashlets from Columns.
     * 
     * @return {@link SiteDashboardPage}
     */
    public SiteDashboardPage removeAllDashlets()
    {
        for (int column = 1; column <= NUMBER_OF_COLUMNS; column++)
        {
            List<WebElement> elements = drone.findAll(By
                        .cssSelector(String.format(DRAGABLE_COLUMN_FORMAT, column)));
            if(elements != null)
            {
                for (WebElement source : elements)
                {
                    drone.dragAndDrop(source, drone.find(TRASHCAN));
                }
            }
        }
        drone.find(SAVE_BUTTON).click();
        return new SiteDashboardPage(drone);
    }
    
    /**
     * Select Layout from given {@link SiteLayout}.
     * 
     * @return {@link SiteDashboardPage}
     */
    public SiteDashboardPage selectDashboard(SiteLayout layout)
    {
        drone.find(layout.getLocator()).click();
        drone.find(SAVE_BUTTON).click();
        return new SiteDashboardPage(drone);
        
    }
    
    /**
     * Add all the dashlets into different columns available.
     * 
     * @return {@link SiteDashboardPage}
     */
    public SiteDashboardPage addAllDashlets()
    {
        this.selectAddDashlets();
        List<WebElement> dashlets = drone.findAll(AVAILABLE_DASHLETS);
        if (logger.isTraceEnabled())
        {
            logger.trace("There are " + dashlets.size() + " dashlets found.");
        }
        int currentColumn = 1;
        int dashletCounter = 1;
        WebElement target = null;

        for (WebElement source : dashlets)
        {
            target = drone.find(By.cssSelector(String.format(COLUMN_FORMAT, currentColumn)));
            drone.dragAndDrop(source, target);
            if (dashletCounter % MAX_DASHLETS_IN_COLUMN == 0)
            {
                currentColumn++;
            }
            dashletCounter++;
        }
        drone.find(SAVE_BUTTON).click();
        return new SiteDashboardPage(drone);
    }

}