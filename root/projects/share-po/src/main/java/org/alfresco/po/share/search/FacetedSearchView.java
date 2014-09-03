package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
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
    private static final By CONFIGURE_VIEW_BUTTON = By.cssSelector("img[class='alf-configure-icon']");
    
    private static final By CONFIGURE_VIEW_ITEMS = By.cssSelector("div#DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP td[class='dijitReset dijitMenuItemLabel']");    
    private static final By SIMPLE_VIEW_RESULTS = By.cssSelector("tbody[id=FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS] td.thumbnailCell");
    private static final By GALLERY_VIEW_RESULTS = By.cssSelector("div[class='displayName']");

    
    private WebDrone drone;
    private WebElement resultsElement;
    private String results;
    private WebElement sortOrderButton;
    private WebElement configureViewButton;
    private List<WebElement> menuElements = new ArrayList<WebElement>();
    private WebElement simpleViewResults;
    private WebElement galleryViewResults;

    /**
     * Instantiates a new faceted search View.
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
            this.menuElements.get(i).getText();
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
                StringUtils.trim(option.getText());
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
     * Verify is results displayed in simple view
     */
    public boolean isSimpleViewResultsDisplayed()
    {
        try
        {
        	simpleViewResults = drone.findAndWait(SIMPLE_VIEW_RESULTS);
        	if(simpleViewResults.isDisplayed())
        	{        		
        		return true;        		       		 
        	}
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
		return false;        
    }
    
    /**
     * Verify is results displayed in Gallery view
     */
    public boolean isGalleryViewResultsDisplayed()
    {
        try
        {
        	galleryViewResults = drone.findAndWait(GALLERY_VIEW_RESULTS);
        	if(galleryViewResults.isDisplayed())
        	{        		
        		return true;        		       		 
        	}
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
		return false;        
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
