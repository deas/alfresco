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
package org.alfresco.po.share.search;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Repository search results page object, holds all element of the html page relating to
 * search results. This is the same as the other search result page but has then specific
 * render logic.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class RepositoryResultsPage extends SearchResultsPage
{
    private static final By Go_TO_ADV_SEARCH = By.cssSelector("#HEADER_ADVANCED_SEARCH_text");
    /**
     * Constructor.
     */
    public RepositoryResultsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryResultsPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryResultsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryResultsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Performs the search by entering the term into search field
     * and submitting the search.
     * 
     * @param term String term to search
     * @return {@link RepositoryResultsPage} page response
     */
    public RepositoryResultsPage search(final String term)
    {
        searchFor(term);
        return new RepositoryResultsPage(drone);
    }

    /**
     * Gets numeric string value indicating the number of search results found
     * from the search query.
     * 
     * @return numeric value of String displaying total result count
     */
    public int getResultCount()
    {
        try
        {
            String val = drone.find(By.cssSelector("div[id$='_default-search-info']>b")).getText();
            return Integer.parseInt(val);
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public AdvanceSearchPage goToAdvancedSearch()
    {
        try
        {
            WebElement backBtn = drone.findAndWait(Go_TO_ADV_SEARCH);
            backBtn.click();
            return drone.getCurrentPage().render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + Go_TO_ADV_SEARCH);
        }
    }

}
