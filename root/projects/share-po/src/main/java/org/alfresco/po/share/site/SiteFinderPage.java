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
package org.alfresco.po.share.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Site finder page object, holds all element of the html page relating to
 * share's site finder page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteFinderPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final String PROMPT_PANEL_ID = "prompt.panel.id";
    private static final By SEARCH_SUBMIT = By.cssSelector("button[id$='default-button-button']");

    /**
     * Constructor.
     */
    public SiteFinderPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized SiteFinderPage render(RenderTime timer)
    {
        // check search button has rendered and is not grayed out.
        while (true)
        {
            timer.start();
            try
            {
                if (drone.find(SEARCH_SUBMIT).isEnabled())
                {
                    synchronized (this)
                    {
                        try
                        {
                            this.wait(100L);
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                    if (isMessageScreenDisplayed())
                    {
                        break;
                    }
                    if (hasResults())
                    {
                        break;
                    }
                }
            }
            catch (NoSuchElementException nse)
            {
            }
            finally
            {
                timer.end();
            }
        }

        return this;
    }

    /**
     * TODO shan please add description.
     * 
     * @return
     */
    private boolean isMessageScreenDisplayed()
    {
        try
        {
            return drone.find(By.cssSelector("tbody.yui-dt-message")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteFinderPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteFinderPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Search for a site form action.
     * 
     * @param title String name of site
     * @return HtmlPage page object response
     */
    public SiteFinderPage searchForSite(final String title)
    {
        WebElement input = drone.findAndWait(By.cssSelector("input[id$='site-finder_x0023_default-term']"));
        input.clear();
        input.sendKeys(title);

        WebElement searchSiteButton = drone.findAndWait(SEARCH_SUBMIT);
        searchSiteButton.click();
        return new SiteFinderPage(drone);
    }

    /**
     * Verify if results are displayed on the page.
     * 
     * @return true if results are present
     */
    public boolean hasResults()
    {
        boolean hasResult = true;
        try
        {
            WebElement resultDiv = drone.find(By.cssSelector("div[id$='default-sites'].results.yui-dt table tbody.yui-dt-message"));
            if (logger.isTraceEnabled())
            {
                logger.trace("no results element shown " + resultDiv.isDisplayed());
            }
            hasResult = resultDiv.isDisplayed() ? false : true;
        }
        catch (NoSuchElementException nse)
        {
        }
        return hasResult;
    }

    /**
     * Returns the list of siteNames found
     */
    public List<String> getSiteList()
    {
        List<String> siteList = new ArrayList<String>();
        if (this.hasResults())
        {
            List<WebElement> siteRows = getSiteResultsSet();
            if (siteRows.size() > 0)
            {
                for (WebElement row : siteRows)
                {
                    String siteName = row.findElement(By.cssSelector("h3.sitename")).getText();
                    siteList.add(siteName);
                }
                return siteList;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Returns the list of {@link}WebElements for the Site Search Results. Empty list for zero results
     */
    private List<WebElement> getSiteResultsSet()
    {
        try
        {
            List<WebElement> siteRows = drone.findAll(By.cssSelector("tbody.yui-dt-data tr"));
            return siteRows;
        }
        catch (NoSuchElementException nse)
        {
        }
        return Collections.emptyList();
    }    

    /**
     * Deletes a site based on the result of the site finder search which should
     * only yield one result. The single result will have a collection of
     * buttons which one is delete. Once the delete is button is found the
     * action of clicking the button takes place.
     * 
     * @param siteName String site name
     * @return {@link HtmlPage} page response object
     */
    public HtmlPage deleteSite(final String siteName)
    {
        if (StringUtils.isEmpty(siteName))
        {
            throw new UnsupportedOperationException("Site Name is required");
        }

        try
        {
            WebElement deleteButton = findButtonForSite(siteName, "Delete");
            removeSite(deleteButton);
        }
        catch (PageException pe)
        {
            throw new PageException("Unable to find site to delete");
        }
        return new SiteFinderPage(drone);
    }

    /**
     * Selects the delete button of a site.
     * 
     * @return {@link SharePage} response
     */
    private HtmlPage removeSite(WebElement deleteButton)
    {
        deleteButton.click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Delete button has been found and selected");
        }
        return confirmDelete();
    }

    /**
     * Confirm delete dialog acceptance action.
     */
    private HtmlPage confirmDelete()
    {
        WebElement prompt = drone.findAndWaitById(PROMPT_PANEL_ID);
        List<WebElement> elements = prompt.findElements(By.tagName("button"));
        // Find the delete button in the prompt
        WebElement delete = findButton("Delete", elements);
        delete.click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Confirm delete button has been found and selected");
        }
        return finalConfimration();
    }

    /**
     * Final step to confirm delete dialog acceptance action.
     */
    private HtmlPage finalConfimration()
    {
        WebElement prompt = drone.findAndWaitById(PROMPT_PANEL_ID);
        List<WebElement> elements = prompt.findElements(By.tagName("button"));
        // Find the delete button in the prompt
        WebElement button = findButton("Yes", elements);
        button.click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Final confirm delete button has been found and selected");
        }

        if (canResume())
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Site message indicating site deleted has been displayed");
            }
        }
        return new SiteFinderPage(drone);
    }

    /**
     * Simulates the action of user clicking on Join/Request to Join button.
     * 
     * @param siteName String site name
     * @return {@link SiteFinderPage} page response object
     */
    public SiteFinderPage joinSite(final String siteName)
    {
        if (StringUtils.isEmpty(siteName))
        {
            throw new UnsupportedOperationException("Site Name can't be empty or null, It is required");
        }
        findButtonForSite(siteName, "Join").click();
        return new SiteFinderPage(drone);
    }

    /**
     * Simulates the action of user clicking on Leaving to Join button.
     * 
     * @param siteName String site name
     * @return {@link SiteFinderPage} page response object
     */
    public SiteFinderPage leaveSite(final String siteName)
    {
        if (StringUtils.isEmpty(siteName))
        {
            throw new UnsupportedOperationException("Site Name can't be empty or null, It is required");
        }
        findButtonForSite(siteName, "Leave").click();
        return new SiteFinderPage(drone);
    }

    /**
     * Finds the button for the particular site name.
     * 
     * @param siteName String identifier
     * @param buttonString button title
     * @return {@link WebElement}
     */
    private WebElement findButtonForSite(final String siteName, final String buttonString)
    {
        if (StringUtils.isEmpty(siteName))
        {
            throw new UnsupportedOperationException("Site Name can't be empty or null, It is required");
        }
        if (StringUtils.isEmpty(buttonString))
        {
            throw new UnsupportedOperationException("Button String can't be empty or null, It is required");
        }

        List<WebElement> siteRows = getSiteResultsSet();
        // Check if site appears else throw exception
        if (siteRows.size() > 0)
        {
            for (WebElement row : siteRows)
            {
                String rowSiteName = row.findElement(By.cssSelector("h3")).getText();
                if (siteName.equalsIgnoreCase(rowSiteName))
                {
                    List<WebElement> elements = row.findElements(By.tagName("button"));
                    // Find the Join/Request to Join button in the prompt
                    for (WebElement webElement : elements)
                    {
                        if (webElement.getText().contains(buttonString))
                        {
                            return webElement;
                        }
                    }
                }
            }
            logger.info("Iterated all site rows, but not able to, perform action - " + buttonString);
            // By this time you should have joined a site and returned.
            throw new PageException("Button to perform '" + buttonString + "' not found.");
        }
        else
        {
            logger.info("Search did not return any result for name - " + siteName);
            throw new PageException("Site '" + siteName + "' could not be found.");
        }
    }

    /**
     * Simulates the action of user clicking on Site.
     * 
     * @param siteName String site name
     * @return {@link SiteDashboardPage} page response object
     */
    public HtmlPage selectSite(final String siteName)
    {
        if (StringUtils.isEmpty(siteName))
        {
            throw new UnsupportedOperationException("Site Name can't be empty or null, It is required");
        }

        List<WebElement> siteRows = drone.findAll(By.cssSelector("h3>a"));

        // Check if site appears else throw exception
        if (siteRows.size() > 0)
        {
            for (WebElement site : siteRows)
            {
                if (siteName.equalsIgnoreCase(site.getText()))
                {
                    if (logger.isTraceEnabled())
                    {
                        logger.info("Site Name: " + site.getText());
                    }
                    site.click();
                    return FactorySharePage.resolvePage(drone);
                }
            }
            if (logger.isTraceEnabled())
            {
                logger.trace("Iterated all site rows, but not able to, find Site - " + siteName);
            }
            throw new PageException("Site '" + siteName + "' could not be found.");
        }
        else
        {
            if (logger.isTraceEnabled())
            {
                logger.info("Search did not return any result for name - " + siteName);
            }
            throw new PageException("Site '" + siteName + "' could not be found.");
        }

    }
}
