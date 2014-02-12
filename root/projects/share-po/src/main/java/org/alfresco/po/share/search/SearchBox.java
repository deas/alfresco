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
package org.alfresco.po.share.search;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the html page relating to the search
 * functionality.
 * 
 * @author Michael Suzuki
 * @since 1.1
 */
public class SearchBox extends SharePage
{
    /**
     * Constructor.
     */
    public SearchBox(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchBox render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchBox render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SearchBox render(final long time)
    {
        return render(new RenderTime(time));
    }
    
    /**
     * Performs the search by entering the term into search field
     * and submitting the search.
     * @param term String term to search
     * @return true when actioned
     */
    public HtmlPage search(final String term)
    {
        if(term == null || term.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        try
        {
            String selector = dojoSupport ? "input.alf-search-box-text" : "input[id$='searchText']";
            WebElement input = drone.find(By.cssSelector(selector));
            input.clear();
            input.sendKeys(term + "\n");
        }
        catch (NoSuchElementException nse)
        {
        }
        return FactorySharePage.resolvePage(drone);
    }
}
