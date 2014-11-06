package org.alfresco.po.share.search;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object holds all elements of HTML page objects relating to
 * CreateNewFilterPopUpPage
 * 
 * @author Charu
 * @since 5.0
 */
public class CreateNewFilterPopUpPage extends SharePage
{

    private static final int MAXNIUM_FILTER_SIZE = 20;
    protected static final By FILTER_ID = By.cssSelector("div[class$='dijitInputField dijitInputContainer']>input[name='filterID']");
    private static final By FILTER_NAME = By.cssSelector("div[class$='dijitInputField dijitInputContainer']>input[name='displayName']");
    private static final By SORT_BY_DD_CTRL = By.cssSelector("table[id='FORM_SORTBY_CONTROL']>tbody>tr>td[class$='dijitArrowButtonContainer']>input");
    private static final By SORT_POP_UP = By.cssSelector("table[id='FORM_SORTBY_CONTROL_menu']");
    private static final By SORT_BY_DD_POPUPMENU_ITEM = By
            .cssSelector("table[id='FORM_SORTBY_CONTROL_menu']>tbody>tr[class='dijitReset dijitMenuItem']>td[class='dijitReset dijitMenuItemLabel']");
    private static final By SORT_BY_SELECTED = By.cssSelector("table[id='FORM_SORTBY_CONTROL']>tbody>tr>td>div>span");
    private static final By SITE_DD_CTRL = By
            .cssSelector("table[id^='alfresco_forms_controls_DojoSelect']>tbody>tr>td[class$='dijitArrowButtonContainer']>input");
    private static final By FILTER_AVAI_DD_CTRL = By.cssSelector("table[id='FORM_SCOPE_CONTROL']>tbody>tr>td[class$='dijitArrowButtonContainer']>input");
    private static final By FILTER_AVAI_POP_UP = By.cssSelector("table[id='FORM_SCOPE_CONTROL_menu']");
    private static final By FILTER_AVAI_DD_POPUPMENU_ITEM = By
            .cssSelector("table[id='FORM_SCOPE_CONTROL_menu']>tbody>tr[class='dijitReset dijitMenuItem']>td[class='dijitReset dijitMenuItemLabel']");
    private static final By FILTER_AVAI_SELECTED = By.cssSelector("table[id='FORM_SCOPE_CONTROL']>tbody>tr>td>div>span");
    private static final By SELECTED_SITE_DD = By.cssSelector("table[id^='alfresco_forms_controls_DojoSelect']>tbody>tr>td>div>span");
    private static final By SITE_POP_UP = By.cssSelector("div[id$='CONTROL_dropdown']+[id^='alfresco_forms_controls_DojoSelect']");
    private static final By POP_UP_SITES_DD = By
            .cssSelector("div[id$='CONTROL_dropdown']>table[id^='alfresco_forms_controls_DojoSelect']>tbody>tr[class='dijitReset dijitMenuItem']");
    private static final By SAVED_SITE_DISPLAY = By.cssSelector("div[class='read-display']");
    private static final By SAVE_SITE = By.cssSelector("div[class='button doneEditing']>img");
    private static final By FILTER_PROPERTY__DD_CTRL = By.cssSelector("div[id='widget_FORM_FACET_QNAME_CONTROL']>div>input[class$='dijitArrowButtonInner']");
    private static final By SELECTED_FILTER_PROPERTY = By.cssSelector("div[class$='dijitInputField dijitInputContainer']>input[id='FORM_FACET_QNAME_CONTROL']");
    private static final By POP_UP_MENU = By.cssSelector("div[id='widget_FORM_FACET_QNAME_CONTROL_dropdown']");
    private static final By FILTER_PROPERTY_DD_POPUPMENU_ITEM = By
            .cssSelector("div[id='FORM_FACET_QNAME_CONTROL_popup']>div[id^='FORM_FACET_QNAME_CONTROL_popup']");
    private static final By CREATE_FILTER_POPUP_TITLE_BAR = By.cssSelector("div[class='dijitDialogTitleBar']");
    private static final By NEW_FILTER_SAVE_OR_CANCEL_BUTTON = By.cssSelector("div[class='footer']>span[class$='alfresco-buttons-AlfButton dijitButton']>span");
    private static Log logger = LogFactory.getLog(CreateNewFilterPopUpPage.class);
    private static final By MIN_FILTER_LENGTH_UP_ARROW = By
            .cssSelector("div[id$='FORM_MIN_FILTER_VALUE_LENGTH']>div>div>div>div>div[class$='dijitUpArrowButton']>div");
    private static final By MIN_FITER_LENGTH = By
            .cssSelector("div[id$='FORM_MIN_FILTER_VALUE_LENGTH']>div>div>div>div>input[class='dijitReset dijitInputInner']");
    private static final By MAX_NO_OF_FILTERS_UP_ARROW = By.cssSelector("div[id$='FORM_MAX_FILTERS']>div>div>div>div>div[class$='dijitUpArrowButton']>div");
    private static final By MAX_NO_OF_FITERS = By.cssSelector("div[id$='FORM_MAX_FILTERS']>div>div>div>div>input[class='dijitReset dijitInputInner']");
    private static final By MIN_REQ_RESULTS_UP_ARROW = By.cssSelector("div[id$='FORM_HIT_THRESHOLD']>div>div>div>div>div[class$='dijitUpArrowButton']>div");
    private static final By MIN_REQ_RESULT = By.cssSelector("div[id$='FORM_HIT_THRESHOLD']>div>div>div>div>input[class='dijitReset dijitInputInner']");
    private static final By ADD_NEW_ENTRY = By.cssSelector("div[id='FORM_SCOPED_SITES']>div>div>div>div[class='button add']");

    public CreateNewFilterPopUpPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    public CreateNewFilterPopUpPage render(RenderTime timer)
    {
        RenderElement actionMessage = getActionMessageElement(ElementState.INVISIBLE);
        elementRender(timer, getVisibleRenderElement(CREATE_FILTER_POPUP_TITLE_BAR), actionMessage);
        return this;
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.alfresco.webdrone.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFilterPopUpPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFilterPopUpPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Gets the value of the input field
     * 
     * @param by input field descriptor
     * @return String input value
     */
    protected String getValue(By by)
    {
        return drone.find(by).getAttribute("value");
    }

    /**
     * Get the String value of name input value.
     */
    public String getFilterID()
    {
        return getValue(FILTER_ID);
    }

    /**
     * Send Filter ID in CreateNewFilterPopUpPage
     * 
     * @param filterID
     * @return CreateNewFilterPopUpPage
     */

    public CreateNewFilterPopUpPage sendFilterID(String filterID)
    {
        WebDroneUtil.checkMandotaryParam("filterID", filterID);
        try
        {
            drone.findAndWait(FILTER_ID).sendKeys(filterID);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: FilterID" + toe.getMessage());
        }

    }

    /**
     * Send displayName in CreateNewFilterPopUpPage
     * 
     * @param displayName
     * @return CreateNewFilterPopUpPage
     **/

    public CreateNewFilterPopUpPage sendFilterName(String filterName)
    {
        WebDroneUtil.checkMandotaryParam("filterName", filterName);
        try
        {
            drone.findAndWait(FILTER_NAME).sendKeys(filterName);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: filterName" + toe.getMessage());
        }

    }

    /**
     * Get the String value of name input value.
     */
    public String getFilterName()
    {
        return getValue(FILTER_NAME);
    }

    /**
     * Select Property from drop down in CreateNewFilterPopUpPage
     * 
     * @param property the propertyName
     * @return CreateNewFilterPopUpPage
     **/

    public CreateNewFilterPopUpPage selectFilterProperty(String property)
    {
        WebDroneUtil.checkMandotaryParam("property", property);
        try
        {
            // Find the select control
            WebElement selectControl = drone.findAndWait(FILTER_PROPERTY__DD_CTRL);

            selectControl.click();

            // Find the pop up menu
            WebElement popupMenu = drone.findAndWait(POP_UP_MENU);

            // Get the pop up menu items
            List<WebElement> menuItems = popupMenu.findElements(FILTER_PROPERTY_DD_POPUPMENU_ITEM);

            // Iterate pop up menu items
            for (WebElement menuItem : menuItems)
            {
                if (menuItem.getText().equalsIgnoreCase(property))
                {
                    menuItem.click();
                    break;
                }
            }
            return this;

        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the property" + e);
            }
        }
        catch (TimeoutException exception)
        {
        }
        throw new PageOperationException("Unable to select the property : ");

    }

    /**
     * Get the String value of SelectedProperty in CreateNewFilterPopUpPage
     */
    public String getSelectedProperty()
    {
        return getValue(SELECTED_FILTER_PROPERTY);
    }

    /**
     * Select sort by from drop down in CreateNewFilterPopUpPage
     *
     * @param order the sort order
     */
    public CreateNewFilterPopUpPage selectSortBy(String order)
    {
        WebDroneUtil.checkMandotaryParam("order", order);
        try
        {
            // Find the select control
            WebElement selectControl = drone.findAndWait(SORT_BY_DD_CTRL);

            selectControl.click();

            // Find the pop up menu
            WebElement popupMenu = drone.findAndWait(SORT_POP_UP);

            // Get the pop up menu items
            List<WebElement> menuItems = popupMenu.findElements(SORT_BY_DD_POPUPMENU_ITEM);

            // Iterate pop up menu items
            for (WebElement menuItem : menuItems)
            {
                if (menuItem.getText().contains(order))
                {
                    menuItem.click();
                    return this;

                }
            }

        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the sortby order" + ne);
            }
        }
        catch (TimeoutException e)
        {
        }
        throw new PageOperationException("Unable to select the sort order : ");

    }

    /**
     * Helper method to return true if Sort by field is displayed with expected sort
     * 
     * @return boolean <tt>true</tt> is Selected 'SortBy' is displayed
     */
    public boolean isSortByDisplayed(String sortBy)
    {
        WebDroneUtil.checkMandotaryParam("sortBy", sortBy);
        try
        {
            if (drone.findAndWait(SORT_BY_SELECTED).isDisplayed() && drone.findAndWait(SORT_BY_SELECTED).getText().endsWith(sortBy))
            {
                return true;
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Select save button in Create New Filter Page
     * 
     * @return {@link FacetedSearchConfigPage Page} page response
     */
    public FacetedSearchConfigPage selectSaveOrCancel(String buttonName)
    {
        WebDroneUtil.checkMandotaryParam("buttonName", buttonName);
        try
        {
            // Get the list of buttons
            List<WebElement> buttonNames = drone.findAndWaitForElements(NEW_FILTER_SAVE_OR_CANCEL_BUTTON);

            // Iterate list of buttons
            for (WebElement button : buttonNames)
            {
                if (button.getText().equalsIgnoreCase(buttonName) && (button.isDisplayed()))
                {
                    button.click();
                    drone.waitUntilVisible(By.cssSelector("div.bd"), "Operation Completed Successfully", 10);
                    drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd"), "Operation Completed Successfully", 10);
                    return new FacetedSearchConfigPage(drone);
                }
            }

        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the" + buttonName + "button" + e.getMessage());
            }
        }

        throw new PageOperationException("Unable to select the" + buttonName + "button");
    }

    /**
     * IncrimentMinimumFilterLength field in Create New Filter Page
     *
     * @param int clickCount - number of clicks/ incremental count/ how many times the up arrow
     *        on min filter length field should be clicked
     * @return CreateNewFilterPopUpPage
     */
    public CreateNewFilterPopUpPage incrementMinimumFilterLength(int clickCount)
    {

        WebDroneUtil.checkMandotaryParam("clickCount", clickCount);
        try
        {

            // Get the initial text in the minFilterLength field
            String initialminFilLength = getValue(MIN_FITER_LENGTH);

            // Convert string to integer value in the minFilterLength field
            int initialMinFilterVal = Integer.valueOf(initialminFilLength).intValue();

            if (!(initialMinFilterVal > MAXNIUM_FILTER_SIZE - 1) && !(clickCount + initialMinFilterVal > MAXNIUM_FILTER_SIZE))
            {
                // Find the select control
                WebElement selectControl = drone.findAndWait(MIN_FILTER_LENGTH_UP_ARROW);

                for (int count = 1; count <= clickCount; count++)
                {
                    selectControl.click();
                    // Get the text in the minFilterLength field
                    String minFilLength = getValue(MIN_FITER_LENGTH);

                    // Convert string to integer value in the minFilterLength
                    // field
                    int MinFilterVal = Integer.valueOf(minFilLength).intValue();

                    if (MinFilterVal > MAXNIUM_FILTER_SIZE)
                    {
                        throw new PageOperationException("Unable to increment by" + clickCount + "since Minimum filter length exceeds maximum value"
                                + MAXNIUM_FILTER_SIZE);

                    }

                    // Get the final text in the minFilterLength field after
                    // incrementing
                    String finalMinFilterLength = getValue(MIN_FITER_LENGTH);

                    // Convert string to integer value in the minFilterLength
                    // field
                    int finalMinFilterVal = Integer.valueOf(finalMinFilterLength).intValue();

                    if (finalMinFilterVal == (clickCount + initialMinFilterVal))
                    {
                        return this;
                    }
                }
            }
            else
            {
                throw new PageOperationException("Unable to increment by" + clickCount + " since Minimum filter length exceeds maximum value"
                        + MAXNIUM_FILTER_SIZE);

            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to increment minimum filter length" + e.getMessage());
            }
        }
        throw new PageOperationException("Unable to increment minimum filter length");
    }

    /**
     * IncrimentMinimumFilterLength field in Create New Filter Page
     *
     * @param int clickCount - number of clicks/ incremental count/ how many times the up arrow
     *        on min filter length field should be clicked
     * @return CreateNewFilterPopUpPage
     */
    public CreateNewFilterPopUpPage incrementMaxNumberOfFilters(int clickCount)
    {

        WebDroneUtil.checkMandotaryParam("clickCount", clickCount);
        try
        {

            // Get the initial text in maxNumberOfFilters field
            String initialMaxNumberOfFilters = getValue(MAX_NO_OF_FITERS);

            // Convert string to integer value in themaxNumberOfFilters field
            int initialMaxNumberOfFiltersVal = Integer.valueOf(initialMaxNumberOfFilters).intValue();

            if (!(initialMaxNumberOfFiltersVal > MAXNIUM_FILTER_SIZE - 1) && !(clickCount + initialMaxNumberOfFiltersVal > MAXNIUM_FILTER_SIZE))
            {
                // Find the select control
                WebElement selectControl = drone.findAndWait(MAX_NO_OF_FILTERS_UP_ARROW);

                for (int count = 1; count <= clickCount; count++)
                {
                    selectControl.click();
                    // Get the text in the maxNumberOfFilters field
                    String maxNumberOfFilters = getValue(MAX_NO_OF_FITERS);

                    // Convert string to integer value in the maxNumberOfFilters
                    // field
                    int maxNumberOfFiltersVal = Integer.valueOf(maxNumberOfFilters).intValue();

                    if (maxNumberOfFiltersVal > MAXNIUM_FILTER_SIZE)
                    {
                        throw new PageOperationException("Unable to increment by" + clickCount + " since Max No of filters lengt exceeds maximum value"
                                + MAXNIUM_FILTER_SIZE);

                    }

                    // Get the final text in the maxNumberOfFilters field after
                    // incrementing
                    String finalMaxNumberOfFilters = getValue(MAX_NO_OF_FITERS);

                    // Convert string to integer value in the maxNumberOfFilters
                    // field
                    int finalMaxNumberOfFiltersVal = Integer.valueOf(finalMaxNumberOfFilters).intValue();

                    if (finalMaxNumberOfFiltersVal == (clickCount + initialMaxNumberOfFiltersVal))
                    {
                        return this;
                    }
                }
            }
            else
            {
                throw new PageOperationException("Unable to increment by" + clickCount + " since Max No of filters lengt exceeds maximum value"
                        + MAXNIUM_FILTER_SIZE);

            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to increment MaxNumberOffilter" + e.getMessage());
            }
        }
        throw new PageOperationException("Unable to increment MaxNumberOfFilters");
    }

    /**
     * incrementMinimumRequiredResults field in Create New Filter Page
     *
     * @param int clickCount - number of clicks/ incremental count/ how many times the up arrow
     *        on Number of Filters field should be clicked
     * @return CreateNewFilterPopUpPage
     */
    public CreateNewFilterPopUpPage incrementMinimumRequiredResults(int clickCount)
    {

        WebDroneUtil.checkMandotaryParam("clickCount", clickCount);
        try
        {

            // Get the initial text in the MinReqResult field
            String initialMinReqResult = getValue(MIN_REQ_RESULT);

            // Convert string to integer value in the MinReqResult field
            int initialMinReqResultVal = Integer.valueOf(initialMinReqResult).intValue();

            if (!(initialMinReqResultVal > MAXNIUM_FILTER_SIZE - 1) && !(clickCount + initialMinReqResultVal > MAXNIUM_FILTER_SIZE))
            {
                // Find the select control
                WebElement selectControl = drone.findAndWait(MIN_REQ_RESULTS_UP_ARROW);

                for (int count = 1; count <= clickCount; count++)
                {
                    selectControl.click();
                    // Get the text in the MinReqResult field
                    String minReqReult = getValue(MIN_REQ_RESULT);

                    // Convert string to integer value in the MinReqResult
                    // field
                    int minReqResultVal = Integer.valueOf(minReqReult).intValue();

                    if (minReqResultVal > MAXNIUM_FILTER_SIZE)
                    {
                        throw new PageOperationException("Unable to increment by" + clickCount + " since Min Req Result length exceeds maximum value"
                                + MAXNIUM_FILTER_SIZE);

                    }

                    // Get the final text in the MinReqResult field after
                    // incrementing
                    String finalMinReqResultLength = getValue(MIN_REQ_RESULT);

                    // Convert string to integer value in the MinReqResult
                    // field
                    int finalMinReqResultVal = Integer.valueOf(finalMinReqResultLength).intValue();

                    if (finalMinReqResultVal == (clickCount + initialMinReqResultVal))
                    {
                        return this;
                    }
                }
            }
            else
            {
                throw new PageOperationException("Unable to increment by" + clickCount + " since Min Req Result length exceeds maximum value"
                        + MAXNIUM_FILTER_SIZE);

            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to increment Minimum Required Result" + e.getMessage());
            }
        }
        throw new PageOperationException("Unable to increment by" + clickCount + " since Min Req Result length exceeds maximum value" + MAXNIUM_FILTER_SIZE);
    }

    /**
     * Select Filter Availability from drop down.
     *
     * @param availability - the Filter Availability
     * @return CreateNewFilterPopUpPage
     */
    public CreateNewFilterPopUpPage selectFilterAvailability(String availability)
    {
        WebDroneUtil.checkMandotaryParam("availability", availability);
        try
        {
            // Find the select control
            WebElement selectControl = drone.findAndWait(FILTER_AVAI_DD_CTRL);

            selectControl.click();

            // Find the pop up menu
            WebElement popupMenu = drone.findAndWait(FILTER_AVAI_POP_UP);

            // Get the pop up menu items
            List<WebElement> menuItems = popupMenu.findElements(FILTER_AVAI_DD_POPUPMENU_ITEM);

            // Iterate pop up menu items
            for (WebElement menuItem : menuItems)
            {
                if (menuItem.getText().contains(availability))
                {
                    menuItem.click();
                    return this;
                }
            }

        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the filter availability" + ne);
            }
        }
        catch (TimeoutException e)
        {
        }
        throw new PageOperationException("Unable to select the filter availability");

    }

    /**
     * Helper method to return true if selected Filter Availability is displayed
     * 
     * @return boolean <tt>true</tt> if expected Filter Availability is displayed
     */
    public boolean isFilterAvailabiltyDisplayed(String availability)
    {
        WebDroneUtil.checkMandotaryParam("availability", availability);
        try
        {
            if (drone.findAndWait(FILTER_AVAI_SELECTED).isDisplayed() && drone.findAndWait(FILTER_AVAI_SELECTED).getText().equalsIgnoreCase(availability))
            {
                return true;
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Click Add New Entry button.
     * 
     * @return {@link CreateNewFilterPopUpPage Page} page response
     */
    public CreateNewFilterPopUpPage clickAddNewEntry()
    {
        try
        {
            // Get the list of buttons
            WebElement addNewEntryButton = drone.findAndWait(ADD_NEW_ENTRY);

            // Iterate list of buttons
            if (addNewEntryButton.isDisplayed())
            {
                addNewEntryButton.click();
                return this;
            }

        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the new entry button" + e.getMessage());
            }
        }

        throw new PageOperationException("Unable to select the new entry button");
    }

    /**
     * Helper method to get the Minimum Filter Length
     * 
     * @return String
     */
    public int getMinFilterLength()
    {
        // Convert string to integer value in the minFilterLength field
        return Integer.valueOf(getValue(MIN_FITER_LENGTH)).intValue();
    }

    /**
     * Helper method to get the Minimum Filter Length
     * 
     * @return String
     */
    public int getMinReqResults()
    {
        // Convert string to integer value in the MinReqResults field
        return Integer.valueOf(getValue(MIN_REQ_RESULT)).intValue();
    }

    /**
     * Helper method to get the Max Number of Filters
     * 
     * @return String
     */
    public int getMaxNoOfFilters()
    {
        // Convert string to integer value in the MaxNoOfFilters field
        return Integer.valueOf(getValue(MAX_NO_OF_FITERS)).intValue();
    }

    /**
     * Helper method to return true if selected Site is displayed
     * 
     * @return boolean <tt>true</tt> if selected Site is displayed
     */
    public boolean isSelectedSiteDisplayed(String siteName)
    {
        WebDroneUtil.checkMandotaryParam("siteName", siteName);
        try
        {
            if (drone.findAndWait(SELECTED_SITE_DD).isDisplayed() && drone.findAndWait(SELECTED_SITE_DD).getText().contains(siteName))
            {
                return true;
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Select Site name from drop down.
     *
     * @param site
     * @return CreateNewFilterPopUpPage
     */
    public CreateNewFilterPopUpPage selectSiteNameAndSave(String siteName)
    {

        WebDroneUtil.checkMandotaryParam("siteName", siteName);
        try
        {
            // Find the select control
            WebElement selectControl = drone.findAndWait(SITE_DD_CTRL);
            // WebElement selectCancel = drone.findAndWait(CANCEL_SITE);

            selectControl.click();

            // Find the pop up menu
            WebElement popupMenu = drone.findAndWait(SITE_POP_UP);

            // Get the pop up menu items
            List<WebElement> menuItems = popupMenu.findElements(POP_UP_SITES_DD);

            // Iterate pop up menu items
            for (WebElement menuItem : menuItems)
            {
                if (menuItem.getText().equalsIgnoreCase(siteName))
                {
                    menuItem.click();
                    WebElement selectSave = drone.findAndWait(SAVE_SITE);
                    if (selectSave.isDisplayed() && selectSave.isEnabled())
                    {
                        selectSave.click();
                    }
                    return this;

                }
            }

        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the site from drop down" + ne);
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the site from drop down" + e.getMessage());
            }
        }

        throw new PageOperationException("Unable to select the sitefrom drop down");
    }

    /**
     * Helper method to return true if Selected Site is displayed
     * 
     * @return boolean <tt>true</tt> is site is displayed
     */
    public boolean isSavedSiteDisplayed(String siteName)
    {
        WebDroneUtil.checkMandotaryParam("siteName", siteName);
        try
        {
            if (drone.findAndWait(SAVED_SITE_DISPLAY).isDisplayed() && drone.findAndWait(SAVED_SITE_DISPLAY).getText().contains(siteName))
            {
                return true;
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

}
