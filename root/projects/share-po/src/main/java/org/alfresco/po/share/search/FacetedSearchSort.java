package org.alfresco.po.share.search;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class FacetedSearchSort.
 */
public class FacetedSearchSort
{
    /** Constants. */
    private static final By FACETED_SEARCH_RESULTS_MENU_BAR = By.cssSelector("div#FCTSRCH_RESULTS_MENU_BAR");
    private static final By RESULTS_STRING = By.cssSelector("span.alfresco-html-Label");
    private static final By SORT_ORDER_BUTTON = By.cssSelector("div#FCTSRCH_SORT_ORDER_TOGGLE > img");
    private static final By MENU_BUTTON = By.cssSelector("div#FCTSRCH_SORT_MENU");
    private static final By MENU_BUTTON_TEXT = By.cssSelector("span#FCTSRCH_SORT_MENU_text");
    private static final By MENU_ITEMS = By.cssSelector("div#FCTSRCH_SORT_MENU_dropdown tr.dijitMenuItem");
    private static final Log logger = LogFactory.getLog(FacetedSearchSort.class);

    private WebDrone drone;
    private WebElement resultsElement;
    private String results;
    private WebElement sortOrderButton;
    private WebElement menuButton;
    private String currentSelection;
    private List<WebElement> menuElements = new ArrayList<WebElement>();

    /**
     * Instantiates a new faceted search sort.
     */
    public FacetedSearchSort(WebDrone drone)
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

        this.menuButton = facetedSearchResultsMenuBar.findElement(MENU_BUTTON);
        this.currentSelection = facetedSearchResultsMenuBar.findElement(MENU_BUTTON_TEXT).getText();
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
    public WebElement getMenuButton()
    {
        return menuButton;
    }

    /**
     * Gets the current selection.
     *
     * @return the current selection
     */
    public String getCurrentSelection()
    {
        return currentSelection;
    }

    /**
     * Toggle the sort order.
     *
     * @return the html page
     */
    public HtmlPage toggleSortOrder()
    {
        if(this.sortOrderButton != null)
        {
            this.sortOrderButton.click();
        }
        return FactorySharePage.resolvePage(this.drone);
    }

    /**
     * Sort by the indexed item in the sort menu.
     *
     * @param i the index number of the item upon which to sort
     * @return the html page
     */
    public HtmlPage sortByIndex(int i)
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
     * Sort by label.
     *
     * @param label the label to be sorted on
     * @return the html page
     */
    public HtmlPage sortByLabel(String label)
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
        this.menuButton.click();
        this.menuElements = this.drone.findAll(MENU_ITEMS);
    }

    /**
     * Cancel an open menu.
     */
    private void cancelMenu()
    {
        this.resultsElement.click();
        this.menuElements.clear();
    }

    /**
     * Verify sort list
     */
    public boolean isSortCorrect()
    {
        openMenu();
        SortType sortTypesValues[] = SortType.values();
        if (sortTypesValues.length == menuElements.size())
        {
            for (int i = 0; i < menuElements.size(); i++)
            {
                if (!menuElements.get(i).getText().trim().equals(sortTypesValues[i].getSortName()))
                {
                    logger.info("Sort list is not correct: " + menuElements.get(i).getText().trim() + " != " + sortTypesValues[i].getSortName());
                    return false;
                }
            }

            return true;
        }
        else
        {
            throw new ShareException("Sort by list is of incorrect size");
        }
    }
}
