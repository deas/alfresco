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
    private static final String TOP_SITE_CONTRIBUTOR_USERS = "div[id*='TopSiteContributorReport'] svg g text:nth-of-type(1)";
    private static final String TOP_SITE_CONTRIBUTOR_COUNTS = "div[id*='TopSiteContributorReport'] svg g text:nth-of-type(2)";
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
     * Returns the list of top site contributor counts
     * 
     * @return
     */
    public List<String> getTopSiteContributorCounts()
    {
        List<String> topSiteContributorCounts = new ArrayList<String>();
        try
        {

            List<WebElement> counts = drone.findAll(By.cssSelector(TOP_SITE_CONTRIBUTOR_COUNTS));
            if (counts.size() > 0)
            {
                for (WebElement count : counts)
                {
                    String [] tokens = count.getText().trim().split(" ");
                    for (String token : tokens)
                    {
                        if (token.trim().matches("[0-9]+"))
                        {
                            topSiteContributorCounts.add(token.trim());
                        }    
                    }
                }
            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("No top site contributor counts " + nse);
        }
        return topSiteContributorCounts;
    }

    /**
     * Returns the list of top site contributor usernames
     * 
     * @return
     */
    public List<String> getTopSiteContributorUsers()
    {
        List<String> topSiteContributorUsers = new ArrayList<String>();
        try
        {

            List<WebElement> usernames = drone.findAll(By.cssSelector(TOP_SITE_CONTRIBUTOR_USERS));
            if (usernames.size() > 0)
            {
                for (WebElement username : usernames)
                {
                    topSiteContributorUsers.add(username.getText());
                }
            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("No top site contributor usernames " + nse);
        }
        return topSiteContributorUsers;
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
