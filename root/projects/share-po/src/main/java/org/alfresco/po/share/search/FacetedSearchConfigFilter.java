package org.alfresco.po.share.search;

import java.util.List;

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

    private static final By I_EDIT_CTRL = By.cssSelector("img.editIcon");
    private static final By I_EDIT_INPUT = By.cssSelector("input.dijitInputInner");
    private static final By I_EDIT_DD_CTRL = By.cssSelector("div.alfresco-forms-controls-DojoSelect table.dijitSelect");
    private static final String I_EDIT_DD_CTRL_MENU_MOD = "_dropdown";
    private static final By I_EDIT_DD_POPUPMENU_ITEM = By.cssSelector("tr.dijitMenuItem");
    private static final By I_EDIT_SAVE = By.cssSelector("span.action.save");
//    private static final By I_EDIT_CANCEL = By.cssSelector("span.action.cancel");

    private WebDrone drone;

    private WebElement filterId;
    private String filterId_text;

    private WebElement filterName;
    private String filterName_text;

    private WebElement filterProperty;
    private String filterProperty_text;

    private WebElement filterType;
    private String filterType_text;

    private WebElement filterShow;
    private String filterShow_text;
    
    private String filterDefault_text;
    
    private String filterAvailability_text;

    /**
     * Instantiates a new faceted search result - some items may be null.
     *
     * @param drone the drone
     * @param filter the filter
     */
    public FacetedSearchConfigFilter(WebDrone drone, WebElement filter)
    {
        
        this.drone = drone;
        
        if(filter.findElements(FILTER_ID).size() > 0)
        {
            filterId = filter.findElement(FILTER_ID);
            filterId_text = filterId.getText();
        }
        if(filter.findElements(FILTER_NAME).size() > 0)
        {
            filterName = filter.findElement(FILTER_NAME);
            filterName_text = filterName.getText();
        }
        if(filter.findElements(FILTER_PROPERTY).size() > 0)
        {
            filterProperty = filter.findElement(FILTER_PROPERTY);
            filterProperty_text = filterProperty.getText();
        }
        if(filter.findElements(FILTER_TYPE).size() > 0)
        {
            filterType = filter.findElement(FILTER_TYPE);
            filterType_text = filterType.getText();
        }
        if(filter.findElements(FILTER_SHOW).size() > 0)
        {
            filterShow = filter.findElement(FILTER_SHOW);
            filterShow_text = filterShow.getText();
        }
        if(filter.findElements(FILTER_DEFAULT).size() > 0)
        {
            filterDefault_text = filter.findElement(FILTER_DEFAULT).getText();
        }
        if(filter.findElements(FILTER_AVAILABILITY).size() > 0)
        {
            filterAvailability_text = filter.findElement(FILTER_AVAILABILITY).getText();
        }
    }

    /**
     * Gets the filter id.
     *
     * @return the filter id
     */
    public WebElement getFilterId()
    {
        return filterId;
    }

    /**
     * Gets the filter id_text.
     *
     * @return the filter id_text
     */
    public String getFilterId_text()
    {
        return filterId_text;
    }

    /**
     * Gets the filter name_text.
     *
     * @return the filter name_text
     */
    public String getFilterName_text()
    {
        return filterName_text;
    }

    /**
     * Edits the filter name.
     *
     * @param name the name
     */
    public void editFilterName(String name)
    {
        iEdit(this.filterName, name);
    }

    /**
     * Gets the filter property_text.
     *
     * @return the filter property_text
     */
    public String getFilterProperty_text()
    {
        return filterProperty_text;
    }
    
    /**
     * Edits the filter property.
     *
     * @param property the property
     */
    public void editFilterProperty(String property)
    {
        iEditDD(this.filterProperty, property);
    }

    /**
     * Gets the filter type_text.
     *
     * @return the filter type_text
     */
    public String getFilterType_text()
    {
        return filterType_text;
    }

    /**
     * Edits the filter type.
     *
     * @param type the type
     */
    public void editFilterType(String type)
    {
        iEditDD(this.filterType, type);
    }

    /**
     * Gets the filter show_text.
     *
     * @return the filter show_text
     */
    public String getFilterShow_text()
    {
        return filterShow_text;
    }

    /**
     * Gets the filter show_i edit.
     *
     * @return the filter show_i edit
     */
    public void editFilterShow(String show)
    {
        iEditDD(this.filterShow, show);
    }

    /**
     * Gets the filter default_text.
     *
     * @return the filter default_text
     */
    public String getFilterDefault_text()
    {
        return filterDefault_text;
    }

    /**
     * Gets the filter availability_text.
     *
     * @return the filter availability_text
     */
    public String getFilterAvailability_text()
    {
        return filterAvailability_text;
    }

    /**
     * In line edit.
     *
     * @param control the control
     * @param value the value
     */
    private void iEdit(WebElement control, String value)
    {
        // Click the in line edit control
        control.findElement(I_EDIT_CTRL).click();

        // Type the value in the input field
        control.findElement(I_EDIT_INPUT).sendKeys(value);

        // Click the save button
        control.findElement(I_EDIT_SAVE).click();
    }

    /**
     * In line edit drop down.
     *
     * @param control the control
     * @param value the value
     */
    private void iEditDD(WebElement control, String value)
    {
        // Click the in line edit control
        control.findElement(I_EDIT_CTRL).click();

        // Find the select control
        WebElement selectControl = control.findElement(I_EDIT_DD_CTRL);

        // Compose the id of the pop up menu
        String popupMenuId = selectControl.getAttribute("id") + I_EDIT_DD_CTRL_MENU_MOD;

        // Click the select control
        selectControl.click();

        // Find the pop up menu
        WebElement popupMenu = drone.find(By.id(popupMenuId));

        // Get the pop up menu items
        List<WebElement> menuItems = popupMenu.findElements(I_EDIT_DD_POPUPMENU_ITEM);

        // Iterate pop up menu items
        boolean found = false;
        for(WebElement menuItem : menuItems)
        {
            if(menuItem.getText().equalsIgnoreCase(value))
            {
                menuItem.click();
                found = true;
                break;
            }
        }

        // No item match - re-click the select control
        if(!found)
        {
            selectControl.click();
        }
        
        // Click the save button
        control.findElement(I_EDIT_SAVE).click();
    }
}