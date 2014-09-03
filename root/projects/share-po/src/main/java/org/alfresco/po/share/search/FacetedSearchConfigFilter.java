package org.alfresco.po.share.search;

import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchConfigFilter.
 * 
 * @author Richard Smith
 */
public class FacetedSearchConfigFilter
{
    /** Constants. */
    private static final By FILTER_ID = By.cssSelector("td:nth-of-type(2)");
    private static final By FILTER_NAME = By.cssSelector("td:nth-of-type(3)");
    private static final By FILTER_PROPERTY = By.cssSelector("td:nth-of-type(4)");
    private static final By FILTER_TYPE = By.cssSelector("td:nth-of-type(5)");
    private static final By FILTER_SHOW = By.cssSelector("td:nth-of-type(6)");
    private static final By FILTER_DEFAULT = By.cssSelector("td:nth-of-type(7)");
    private static final By FILTER_AVAILABILITY = By.cssSelector("td:nth-of-type(8)");

    private String filterId;
    private String filterName;
    private String filterProperty;
    private String filterType;
    private String filterShow;
    private String filterDefault;
    private String filterAvailability;

    /**
     * Instantiates a new faceted search result - some items may be null.
     *
     * @param drone the drone
     * @param filter the filter
     */
    public FacetedSearchConfigFilter(WebDrone drone, WebElement filter)
    {
        if(filter.findElements(FILTER_ID).size() > 0)
        {
            filterId = filter.findElement(FILTER_ID).getText();
        }
        if(filter.findElements(FILTER_NAME).size() > 0)
        {
            filterName = filter.findElement(FILTER_NAME).getText();
        }
        if(filter.findElements(FILTER_PROPERTY).size() > 0)
        {
            filterProperty = filter.findElement(FILTER_PROPERTY).getText();
        }
        if(filter.findElements(FILTER_TYPE).size() > 0)
        {
            filterType = filter.findElement(FILTER_TYPE).getText();
        }
        if(filter.findElements(FILTER_SHOW).size() > 0)
        {
            filterShow = filter.findElement(FILTER_SHOW).getText();
        }
        if(filter.findElements(FILTER_DEFAULT).size() > 0)
        {
            filterDefault = filter.findElement(FILTER_DEFAULT).getText();
        }
        if(filter.findElements(FILTER_AVAILABILITY).size() > 0)
        {
            filterAvailability = filter.findElement(FILTER_AVAILABILITY).getText();
        }
    }

    /**
     * Gets the filter id.
     *
     * @return the filter id
     */
    public String getFilterId()
    {
        return filterId;
    }

    /**
     * Gets the filter name.
     *
     * @return the filter name
     */
    public String getFilterName()
    {
        return filterName;
    }

    /**
     * Gets the filter property.
     *
     * @return the filter property
     */
    public String getFilterProperty()
    {
        return filterProperty;
    }

    /**
     * Gets the filter type.
     *
     * @return the filter type
     */
    public String getFilterType()
    {
        return filterType;
    }

    /**
     * Gets the filter show.
     *
     * @return the filter show
     */
    public String getFilterShow()
    {
        return filterShow;
    }

    /**
     * Gets the filter default.
     *
     * @return the filter default
     */
    public String getFilterDefault()
    {
        return filterDefault;
    }

    /**
     * Gets the filter availability.
     *
     * @return the filter availability
     */
    public String getFilterAvailability()
    {
        return filterAvailability;
    }
}