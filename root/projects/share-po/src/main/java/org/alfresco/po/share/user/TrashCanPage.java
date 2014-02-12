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

package org.alfresco.po.share.user;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * As part of 42 new features the user can recover or completely delete from the
 * respository using my profile trashcan
 * 
 * @author Subashni Prasanna
 * @since 1.7.0
 */
public class TrashCanPage extends SharePage
{
    protected static final By TRASHCAN_SEARCH_INPUT = By.cssSelector("input[id$='trashcan_x0023_default-search-text']");
    protected static final By TRASHCAN_SEARCH_BUTTON = By.cssSelector("button[id$='default-search-button-button']");
    protected static final By TRASHCAN_CLEAR_BUTTON = By.cssSelector("button[id$='user-trashcan_x0023_default-clear-button-button']");
    protected static final By TRASHCAN_SELECTED_BUTTON = By.cssSelector("button[id$='user-trashcan_x0023_default-selected-button']");
    protected static final By TRASHCAN_RECOVER_LINK = By.cssSelector("a[class^='recover-item yuimenuitemlabel']");
    protected static final By TRASHCAN_DELETE_LINK = By.cssSelector("a[class^='delete-item yuimenuitemlabel']");
    protected static final By TRASHCAN_SELECT_BUTTON = By.cssSelector("button[id$='user-trashcan_x0023_default-select-button-button']");
    protected static final By TRASHCAN_SELECT_ALL_LINK = By.cssSelector("a[class^='select-all yuimenuitemlabel']");
    protected static final By TRASHCAN_SELECT_NONE_LINK = By.cssSelector("a[class^='select-none yuimenuitemlabel']");
    protected static final By TRASHCAN_SELECT_INVERT_LINK = By.cssSelector("a[class^='select-invert yuimenuitemlabel']");
    protected static final By TRASHCAN_EMPTY_BUTTON = By.cssSelector("button[id$='user-trashcan_x0023_default-empty-button-button']");
    protected static final By TRASHCAN_SELECT_ITEM_CHECKBOX = By.cssSelector("input[id^='checkbox']");
    protected static final By TRASHCAN_ITEM_LIST = By.cssSelector("div[id$='user-trashcan_x0023_default-datalist']");
    protected static final By TRASHCAN_BUTTON = By.cssSelector("td[class$='yui-dt-col-actions yui-dt-last'] button");
    protected static final By TRASHCAN_SELECTED_LIST = By.cssSelector("div.bd");
    protected static final By TRASHCAN_ITEM_NAME = By.cssSelector("div.name");

    public TrashCanPage(WebDrone drone)
    {
        super(drone);

    }

    @SuppressWarnings("unchecked")
    public TrashCanPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(TRASHCAN_SEARCH_INPUT), getVisibleRenderElement(TRASHCAN_SEARCH_BUTTON),
                        getVisibleRenderElement(TRASHCAN_CLEAR_BUTTON), getVisibleRenderElement(TRASHCAN_SELECT_BUTTON),
                        getVisibleRenderElement(TRASHCAN_EMPTY_BUTTON));
        }
        catch (NoSuchElementException e)
        {
        }
        catch (TimeoutException e)
        {
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TrashCanPage render(long time)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TrashCanPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public ProfileNavigation getProfileNav()
    {
        return ProfileNavigation.getInstance(drone);
    }
    
    /**
     * Input serach text and perform search with in the items which are
     * displayed in the trashcan page.
     * 
     * @param - String
     * @return - TrashCanPage as response
     */
    public HtmlPage itemSearch(String searchText)
    {
        WebElement inputField = drone.find(TRASHCAN_SEARCH_INPUT);
        inputField.clear();
        inputField.sendKeys(searchText);
        drone.find(TRASHCAN_SEARCH_BUTTON).click();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Method to clear the entered serach field.
     * 
     * @return - TrashCanPage as response
     */
    public HtmlPage clearSearch()
    {
        drone.find(TRASHCAN_CLEAR_BUTTON).click();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Method to Get the list of trashCan item
     * 
     * @return - list of WebElement
     */
    private List<WebElement> getTrashCanItem() throws NoSuchElementException
    {
        List<WebElement> results = new ArrayList<WebElement>();
        if (hasTrashCanItems())
        {
            try
            {
                results = drone.findAll(By.cssSelector("tbody.yui-dt-data tr"));
            }
            catch (NoSuchElementException nse)
            {
                throw new NoSuchElementException("The trashcan item list table is not visible");
            }
        }
        return results;
    }

    /**
     * Method to find out whether we have items in trashcan page
     * 
     * @return boolean
     */

    public boolean hasTrashCanItems()
    {
        try
        {
            WebElement info = drone.find(TRASHCAN_ITEM_LIST);
            String value = info.getText();
            if (value.contentEquals("No items exist"))
            {
                return false;
            }
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
        return true;
    }
    
    /**
     * TrashCan Search Results
     * 
     */
    public List<String> getSearchResults() throws PageOperationException
    {
       List<String> searchResults = new ArrayList<String>();
       try
        {
            List<WebElement> items = getTrashCanItem();
            for (WebElement element : items)
            {
                String fileName = element.findElement(TRASHCAN_ITEM_NAME).getText();
                searchResults.add(fileName);
            }
        }   
       catch(NoSuchElementException nse)
       {
           throw new PageOperationException("Cannot get the list of search items", nse);
       }
        return searchResults;
    }
    /**
     * select check box to delete an item
     * 
     * @param - itemToBeChecked
     * @return - TrashCanPage
     * @throws PageOperationException
     */
    public HtmlPage selectTrashCanItem(String itemToBeChecked) throws PageOperationException
    {
        try
        {
            List<WebElement> items = getTrashCanItem();
            for (WebElement element : items)
            {
                String fileName = element.findElement(TRASHCAN_ITEM_NAME).getText();
                if (fileName.equalsIgnoreCase(itemToBeChecked))
                {
                    element.findElement(TRASHCAN_SELECT_ITEM_CHECKBOX).click();
                }
            }
        }
       catch (NoSuchElementException nse)
        {
            throw new PageOperationException("The trashcan list is empty so cannot select an item");
        }
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Is check box selected
     * 
     * @return - Boolean
     */
    public boolean isCheckBoxSelected(String itemToBeChecked)
    {
        try
        {
            List<WebElement> items = getTrashCanItem();
            for (WebElement element : items)
            {
                String fileName = element.findElement(TRASHCAN_ITEM_NAME).getText();
                if (fileName.equalsIgnoreCase(itemToBeChecked))
                {
                   return (element.findElement(TRASHCAN_SELECT_ITEM_CHECKBOX).isSelected());
                }
            }
        }  
            catch (NoSuchElementException nse)
            {
                throw new PageOperationException("The trashcan list is empty so cannot select an item");
            }
        
        return false;
      }
    
    /**
     * This method will click on the recover button and that item will be
     * recovered from TrashCan
     * 
     * @param - itemToBeRecovered, actionType
     * @return - TrashCanPage
     * @throws - PageOperationException
     */
    public HtmlPage selectTrashCanAction(String itemToBeRecovered, String actionType) throws PageOperationException
    {
        try
        {
            List<WebElement> items = getTrashCanItem();
            for (WebElement element : items)
            {
                String fileName = element.findElement(By.cssSelector("div.name")).getText();
                if (fileName.equalsIgnoreCase(itemToBeRecovered))
                {
                    List<WebElement> buttonList = element.findElements(TRASHCAN_BUTTON);
                    for (WebElement button : buttonList)
                    {
                      
                        if (button.getText().equalsIgnoreCase(actionType))
                        {
                            button.click();
                            if(actionType.equalsIgnoreCase("Delete"))
                            {
                                return new TrashCanDeleteConfirmationDialogPage(drone);
                            }
                            
                        }
                    }
                }
            }
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Cannot select any action for the item specified");
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("The trashcan list is empty so cannot select an item");
        }
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Empty the trashcan
     */
    public TrashCanEmptyConfirmationPage selectEmpty()
    {
        drone.find(TRASHCAN_EMPTY_BUTTON).click();
        return new TrashCanEmptyConfirmationPage(drone);
    }
}
