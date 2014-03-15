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
package org.alfresco.po.share;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


/**
 * Pagination object for Document List.
 * 
 * @author Richard Smith
 */
public class DocListPaginator
{

    /** Constants */
    private static final By PAGINATION_GROUP = By.cssSelector("div[id^=\"alfresco_documentlibrary_AlfDocumentListPaginator___\"].alfresco-menus-AlfMenuBar");
    private static final By PAGINATION_NEXT_BUTTON = By.cssSelector("div[id$=\"_PAGE_BACK\"]");
    private static final By PAGINATION_PREVIOUS_BUTTON = By.cssSelector("div[id$=\"_PAGE_FORWARD\"]");

    private WebDrone drone;
    private WebElement prevPage;
    private WebElement nextPage;

    /**
     * Instantiates a new doc list paginator.
     *
     * @param drone the WebDrone
     */
    public DocListPaginator(WebDrone drone)
    {
        this.drone = drone;
        WebElement element = drone.find(PAGINATION_GROUP);
        nextPage = element.findElement(PAGINATION_NEXT_BUTTON);
        prevPage = element.findElement(PAGINATION_PREVIOUS_BUTTON);
    }

    /**
     * Gets the previous page.
     *
     * @return the previous page
     */
    public WebElement getPrevPage()
    {
        return prevPage;
    }

    /**
     * Gets the next page.
     *
     * @return the next page
     */
    public WebElement getNextPage()
    {
        return nextPage;
    }

    /**
     * Click next button.
     *
     * @return the html page
     */
    public HtmlPage clickNextButton()
    {
        if (nextPage.isDisplayed() && nextPage.isEnabled())
        {
            try
            {
                nextPage.click();
            }
            catch (NoSuchElementException nse)
            {}
            catch (TimeoutException te)
            {}
            return FactorySharePage.resolvePage(drone);
        }
        return null;
    }

    /**
     * Click previous button.
     *
     * @return the html page
     */
    public HtmlPage clickPrevButton()
    {
        if (prevPage.isDisplayed() && prevPage.isEnabled())
        {
            try
            {
                prevPage.click();
            }
            catch (NoSuchElementException nse)
            {}
            catch (TimeoutException te)
            {}
            return FactorySharePage.resolvePage(drone);
        }
        return null;
    }
}