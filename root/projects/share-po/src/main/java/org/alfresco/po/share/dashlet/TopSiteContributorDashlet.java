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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


/**
 * TopSiteContributorDashlet page object for top site contributor report dashlet
 * 
 * @author jcule
 */
public class TopSiteContributorDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(TopSiteContributorDashlet.class);

    private static final String TOP_SITE_CONTRIBUTOR_REPORT_DASHLET = "div[id*='TopSiteContributorReport']";
    private static final String PIE_CHART_SLICES = "path[transform]";
    private static final String TOOLTIP_DATA = "div[id^='tipsyPvBehavior']";
    private static final String ORIGINAL_TITLE_ATTRIBUTE = "original-title";
    private static final String FROM_DATE_INPUT_FIELD = "";
    private static final String TO_DATE_INPUT_FIELD = "";
    private static final String OK_BUTTON = "";
    private static final String CANCEL_BUTTON = "";

    /**
     * Constructor
     * 
     * @param drone
     */
    protected TopSiteContributorDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(TOP_SITE_CONTRIBUTOR_REPORT_DASHLET));
    }

    @SuppressWarnings("unchecked")
    public TopSiteContributorDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopSiteContributorDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    public TopSiteContributorDashlet render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector(TOP_SITE_CONTRIBUTOR_REPORT_DASHLET)));
        return this;
    }

    
    /**
     * Gets the list of user data appearing in tooltips (file type-count) 
     * @return
     */
    public List<String> getTooltipUserData()
    {
        List<WebElement> pieChartSlices = getPieChartSlices();
        List<String> toolTipData = new ArrayList<String>();
        for (WebElement pieChartSlice : pieChartSlices)
        {
            drone.mouseOver(pieChartSlice);
            WebElement tooltipElement = drone.findAndWait(By.cssSelector(TOOLTIP_DATA));
            String user = getElement(tooltipElement.getAttribute(ORIGINAL_TITLE_ATTRIBUTE), "/div/strong");
            String items = getElement(tooltipElement.getAttribute(ORIGINAL_TITLE_ATTRIBUTE), "/div/text()[preceding-sibling::br]");
            String [] counts = items.split(" ");
            String fileCount = counts[0];
            StringBuilder builder = new StringBuilder();
            builder.append(user).append("-").append(fileCount);
            toolTipData.add(builder.toString());
        }   
        return toolTipData;
    }
    
    
    /**
     * Gets the list of usernames appearing in tooltips 
     * @return
     */
    public List<String> getTooltipUsers()
    {
        List<WebElement> pieChartSlices = getPieChartSlices();
        List<String> users = new ArrayList<String>();
        for (WebElement pieChartSlice : pieChartSlices)
        {
            drone.mouseOver(pieChartSlice);
            WebElement tooltipElement = drone.findAndWait(By.cssSelector(TOOLTIP_DATA));
            String user = getElement(tooltipElement.getAttribute(ORIGINAL_TITLE_ATTRIBUTE), "/div/strong");
            users.add(user);
        }   
        return users;
    }
    
    
    
    
    
    
    /**
     * Gets the list of pie chart slices elements
     * 
     * @return
     */
    private List<WebElement> getPieChartSlices()
    {
        List<WebElement> pieChartSlices = new ArrayList<WebElement>();
        try
        {
            pieChartSlices = drone.findAll(By.cssSelector(PIE_CHART_SLICES));

        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Top Contributor Report pie chart slices " + nse);
        }
        return pieChartSlices;
    }
    

    
    
    /**
     * Enters from date into calendar
     * 
     * @param fromDate
     * @return
     */
    public HtmlPage enterFromDate(final String fromDate)
    {
        if (fromDate == null || fromDate.isEmpty())
        {
            throw new UnsupportedOperationException("From date is required");
        }
        try
        {

            WebElement input = drone.findAndWait(By.cssSelector(FROM_DATE_INPUT_FIELD));
            input.clear();
            input.sendKeys(fromDate);
            if (logger.isTraceEnabled())
            {
                logger.trace("From date entered: " + fromDate);
            }
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Calendar from date drop down not displayed.");
        }
    }

    /**
     * Enters to date into calendar
     * 
     * @param fromDate
     * @return
     */
    public HtmlPage enterToDate(final String toDate)
    {
        if (toDate == null || toDate.isEmpty())
        {
            throw new UnsupportedOperationException("To date is required");
        }
        try
        {

            WebElement input = drone.findAndWait(By.cssSelector(TO_DATE_INPUT_FIELD));
            input.clear();
            input.sendKeys(toDate);
            if (logger.isTraceEnabled())
            {
                logger.trace("To date entered: " + toDate);
            }
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Calendar to date drop down not displayed.");
        }
    }

    /**
     * Clicks on calendar Ok button
     */
    public void clickCalendarOkButtton()
    {
        try
        {
            scrollDownToDashlet();
            drone.findAndWait(By.cssSelector(OK_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the Ok Button.", e);
            }
        }
    }

    /**
     * Clicks on calendar Cancel button
     */
    public void clickCalendarCancelButtton()
    {
        try
        {
            scrollDownToDashlet();
            drone.findAndWait(By.cssSelector(CANCEL_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the Cancel Button.", e);
            }
        }
    }
}
