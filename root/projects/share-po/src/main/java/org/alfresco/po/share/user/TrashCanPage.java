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

package org.alfresco.po.share.user;

import org.alfresco.po.share.NewPagination;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * As part of 42 new features the user can recover or completely delete from the respository using my profile trashcan
 *
 * @author Subashni Prasanna
 * @since 1.7.0
 */
public class TrashCanPage extends SharePage
{
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(TrashCanPage.class);
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
    protected static final By TRASHCAN_ITEM_LIST = By.cssSelector("div[id$='user-trashcan_x0023_default-datalist']");
    protected static final By TRASHCAN_SELECTED_LIST = By.cssSelector("div.bd");
    protected static final By TRASHCAN_SELECTED_RECOVER = By.cssSelector("div.bd a.recover-item");
    protected static final By TRASHCAN_SELECTED_DELETE = By.cssSelector("div.bd a.delete-item");
    protected static final String TRASHCAN_PAGINATION_MORE_BUTTON = "button[id$='paginator-more-button-button']";
    protected static final String TRASHCAN_PAGINATION_LESS_BUTTON = "button[id$='paginator-less-button-button']";
    protected static final By TRASHCAN_EMPTY = By.cssSelector("td.yui-dt-empty");
    protected static final By PAGE_LOADING = By.cssSelector("td.yui-dt-loading");
    private static final By HEADER_BAR = By.cssSelector(".header-bar");
    private boolean deleteInitiator = false;

    /*
     * Constructor
     */

    public TrashCanPage(WebDrone drone)
    {
        super(drone);
    }

    /*
     * Render logic
     */

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
        return new ProfileNavigation(drone);
    }

    /**
     * Input serach text and perform search with in the items which are displayed in the trashcan page.
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
        drone.waitUntilElementDisappears(PAGE_LOADING, 1);
        return drone.getCurrentPage();
    }

    /**
     * Method to clear the entered serach field.
     *
     * @return - TrashCanPage as response
     */
    public HtmlPage clearSearch()
    {
        drone.find(TRASHCAN_CLEAR_BUTTON).click();
        return drone.getCurrentPage();
    }

    /**
     * Method to Get the list of trashCan item
     *
     * @return - list of WebElement
     */
    private List<WebElement> getTrashCanItemElements() throws NoSuchElementException
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
                throw new PageOperationException("The trashcan item list table is not visible", nse);
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
     */
    public List<TrashCanItem> getTrashCanItems() throws PageOperationException
    {
        List<TrashCanItem> results = Collections.emptyList();
        if (hasTrashCanItems())
        {
            results = new ArrayList<TrashCanItem>();
            List<WebElement> items = getTrashCanItemElements();
            for (WebElement element : items)
            {
                results.add(new TrashCanItem(element, drone));
            }
        }
        return results;
    }

    /**
     * This method will get TrashCanItem for the File
     *
     * @return - TrashCanItem
     * @throws - PageOperationException
     */
    public List<TrashCanItem> getTrashCanItemForContent(TrashCanValues trashCanFileType, String contentName, String contentPath) throws PageOperationException
    {
        List<TrashCanItem> trashCanItemList = getTrashCanItems();
        List<TrashCanItem> item = Collections.emptyList();
        if (!trashCanItemList.isEmpty())
        {
            item = new ArrayList<TrashCanItem>();
            for (TrashCanItem itemElement : trashCanItemList)
            {
                if (itemElement.getFileName().equalsIgnoreCase(contentName) && itemElement.getFolderPath().contains(contentPath))
                {
                    switch (trashCanFileType)
                    {
                        case FILE:
                            if (itemElement.isTrashCanItemFile())
                            {
                                item.add(itemElement);
                                break;
                            }
                        case FOLDER:
                            if (itemElement.isTrashCanItemFolder())
                            {
                                item.add(itemElement);
                                break;
                            }
                        case SITE:
                            if (itemElement.isTrashCanItemSite())
                            {
                                item.add(itemElement);
                                break;
                            }

                        default:
                            break;
                    }
                }
            }
        }
        return item;
    }

    /**
     * Empty the trashcan
     */
    public TrashCanEmptyConfirmationPage selectEmpty()
    {
        drone.find(TRASHCAN_EMPTY_BUTTON).click();
        return new TrashCanEmptyConfirmationPage(drone);
    }

    /**
     * Click on Selected Recover of trashcan
     *
     * @return - TrashCanRecoverConfrimationDialog
     */
    public TrashCanRecoverConfirmDialog selectedRecover()
    {
        try
        {
            drone.find(TRASHCAN_SELECTED_BUTTON).click();
            drone.findAndWait(TRASHCAN_SELECTED_RECOVER).click();
            return new TrashCanRecoverConfirmDialog(drone);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Selected Recover is not possible", te);
        }
    }

    /**
     * Click on Selected Recover of trashcan
     *
     * @return - TrashCanDeleteConfirmationDialogPage
     */
    public TrashCanDeleteConfirmationPage selectedDelete()
    {
        try
        {
            deleteInitiator = true;
            drone.find(TRASHCAN_SELECTED_BUTTON).click();
            drone.findAndWait(TRASHCAN_SELECTED_DELETE).click();
            return new TrashCanDeleteConfirmationPage(drone, deleteInitiator);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Selected Recover is not possible", te);
        }
    }

    /**
     * Click on select in the trashcan Page
     *
     * @param - String Action Type
     * @return - TrashCanPage
     */
    public HtmlPage selectAction(SelectActions selectAction)
    {
        try
        {
            drone.find(TRASHCAN_SELECT_BUTTON).click();
            switch (selectAction)
            {
                case ALL:
                    drone.findAndWait(TRASHCAN_SELECT_ALL_LINK).click();
                    return new TrashCanPage(drone);
                case INVERT:
                    drone.findAndWait(TRASHCAN_SELECT_INVERT_LINK).click();
                    return new TrashCanPage(drone);
                case NONE:
                    drone.findAndWait(TRASHCAN_SELECT_NONE_LINK).click();
                    return new TrashCanPage(drone);
                default:
                    throw new PageException("Selection does not exist");
            }
        }
        catch (TimeoutException te)
        {
            throw new PageException("Cannot perfom any selection", te);
        }

    }

    /**
     * Checks if pagination next button is active.
     *
     * @return true if next page exists
     */
    public boolean hasNextPage()
    {
        return NewPagination.hasPaginationButton(drone, TRASHCAN_PAGINATION_MORE_BUTTON);
    }

    /**
     * Checks if pagination previous button is active.
     *
     * @return true if next page exists
     */
    public boolean hasPreviousPage()
    {
        return NewPagination.hasPaginationButton(drone, TRASHCAN_PAGINATION_LESS_BUTTON);
    }

    /**
     * Selects the button next on the pagination bar.
     */
    public HtmlPage selectNextPage()
    {
        return NewPagination.selectPaginationButton(drone, TRASHCAN_PAGINATION_MORE_BUTTON);
    }

    /**
     * Selects the button previous on the pagination bar.
     */
    public HtmlPage selectPreviousPage()
    {
        return NewPagination.selectPaginationButton(drone, TRASHCAN_PAGINATION_LESS_BUTTON);
    }

    /**
     * Returns true if No items message displayed
     */
    public boolean checkNoItemsMessage()
    {
        try
        {
            WebElement emptyTrashCanMessage = drone.find(TRASHCAN_EMPTY);
            return (emptyTrashCanMessage.isDisplayed() && emptyTrashCanMessage.getText().equals("No items exist"));
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Return <code>true</code> if the Deleted Documents and Folders title is displayed on screen.
     *
     * @return boolean present
     */
    private boolean isHeaderTitlePresent()
    {
        boolean present = false;
        try
        {
            present = drone.findAndWait(HEADER_BAR).getText().equals("Deleted Documents and Folders");
            return present;
        }
        catch (NoSuchElementException e)
        {
        }
        return present;
    }

    /**
     * Method to verify all controls are displayed on TrashCanPage page
     *
     * @return true if page is correct
     */
    public boolean isPageCorrect()
    {
        boolean isCorrect = false;
        try
        {
            isCorrect = drone.isElementDisplayed(TRASHCAN_SEARCH_INPUT) && drone.isElementDisplayed(TRASHCAN_SEARCH_BUTTON)
                && drone.isElementDisplayed(TRASHCAN_CLEAR_BUTTON) && drone.isElementDisplayed(TRASHCAN_SELECT_BUTTON) && drone
                .isElementDisplayed(TRASHCAN_EMPTY_BUTTON) && drone.isElementDisplayed(TRASHCAN_SELECTED_BUTTON) && drone
                .isElementDisplayed(By.cssSelector(TRASHCAN_PAGINATION_MORE_BUTTON)) && drone
                .isElementDisplayed(By.cssSelector(TRASHCAN_PAGINATION_LESS_BUTTON)) && isHeaderTitlePresent()
                && checkNoItemsMessage();
            return isCorrect;

        }
        catch (NoSuchElementException e)
        {
            logger.error(e.getMessage());
        }
        return isCorrect;
    }
}
