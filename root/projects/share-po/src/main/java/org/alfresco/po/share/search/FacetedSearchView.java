package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchView
 * This is still not completed since the development is in progress
 * @author Charu
 */
public class FacetedSearchView
{
    /** Constants. */
    private static final By FACETED_SEARCH_RESULTS_MENU_BAR = By.cssSelector("div#FCTSRCH_RESULTS_MENU_BAR");
    private static final By RESULTS_STRING = By.cssSelector("span.alfresco-html-Label");
    private static final By SORT_ORDER_BUTTON = By.cssSelector("div#FCTSRCH_SORT_ORDER_TOGGLE > img");
    private static final By CONFIGURE_VIEW_BUTTON = By.cssSelector("div[id^=alfresco_menus_AlfMenuBarPopup]>span[class=alf-menu-arrow]");
    
    private static final By CONFIGURE_VIEW_ITEMS = By.cssSelector("div[class$=group-items]>table>tbody>tr[id^=uniqName_24]");
    // Need to get the java script to get the default selection
    private static final By SELECTED_VIEW_TYPE = By.cssSelector("div[class$=group-items]>table>tbody>tr[id^=uniqName_24]>td[class$=alf-selected-icon]");

    private WebDrone drone;
    private WebElement resultsElement;
    private String results;
    private WebElement sortOrderButton;
    private WebElement configureViewButton;
    private String currentSelection;
    private List<WebElement> menuElements = new ArrayList<WebElement>();

    /**
     * Instantiates a new faceted search sort.
     */
    public FacetedSearchView(WebDrone drone)
    {
        this.drone = drone;
        WebElement facetedSearchResultsMenuBar = drone.find(FACETED_SEARCH_RESULTS_MENU_BAR);
        this.resultsElement = facetedSearchResultsMenuBar.findElement(RESULTS_STRING);
        this.results = resultsElement.getText();

        // The sort order button may be missing due to configuration so search for many and grab the first if found
        List<WebElement> sortButtons = facetedSearchResultsMenuBar.findElements(SORT_ORDER_BUTTON);
        if(sortButtons.size() > 0)
        {
            this.sortOrderButton = sortButtons.get(0);
        }

        this.configureViewButton = facetedSearchResultsMenuBar.findElement(CONFIGURE_VIEW_BUTTON); 
        //this.currentSelection = facetedSearchResultsMenuBar.findElement(SELECTED_VIEW_TYPE).;
    }

    /**
     * Gets the results.
     *
     * @return the results
     */
    public String getResults()
    {
        return results;
    }

    /**
     * Gets the sort order button.
     *
     * @return the sort order button
     */
    public WebElement getSortOrderButton()
    {
        return sortOrderButton;
    }

    /**
     * Gets the menu button.
     *
     * @return the menu button
     */
    public WebElement getConfigureViewButton()
    {
        return configureViewButton;
    }

    /**
     * This current selection method has to modified and replaced with get default selection using java script
     * Gets the current selection.
     *
     * @return the current selection
     */
	public String getCurrentSelection() {
		int i = 2;
		openMenu();
		if (i >= 0 && i < this.menuElements.size()) {
			this.menuElements.get(i).getText();			
			if (!(SELECTED_VIEW_TYPE == null))
				cancelMenu();
			 this.currentSelection = this.menuElements.get(i).getText();
		} else {
			i++;
		}
		return this.currentSelection;
	}
    
    /**
     * select view by index by the indexed item in the view menu
     *
     * @param i the index number of the item upon which to select
     * @return the html page
     */
    public HtmlPage selectViewByIndex(int i)
    {
        openMenu();
        boolean found = false;
        if(i >= 0 && i < this.menuElements.size())
        {
            this.currentSelection = this.menuElements.get(i).getText();
            this.menuElements.get(i).click();
            found = true;
        }
        if(!found)
        {
            cancelMenu();
        }
        return FactorySharePage.resolvePage(this.drone);
    }

    /**
     * Select by label.
     *
     * @param label the label to be sorted on
     * @return the html page
     */
    public HtmlPage selectViewByLabel(String label)
    {
        openMenu();
        boolean found = false;
        for(WebElement option : this.menuElements)
        {
            if(StringUtils.trim(option.getText()).equalsIgnoreCase(label))
            {
                this.currentSelection = StringUtils.trim(option.getText());
                option.click();
                found = true;
                break;
            }
        }
        if(!found)
        {
            cancelMenu();
        }
        return FactorySharePage.resolvePage(this.drone);
    }

    /**
     * Open the sort menu.
     */
    private void openMenu()
    {
        this.configureViewButton.click();
        this.menuElements = this.drone.findAll( CONFIGURE_VIEW_ITEMS);
    }

    /**
     * Cancel an open menu.
     */
    private void cancelMenu()
    {
        this.resultsElement.click();
        this.menuElements.clear();
    }
}
