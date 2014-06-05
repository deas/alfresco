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
package org.alfresco.po.share.site.datalist;


import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;
import java.util.NoSuchElementException;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Site data list page object, holds all element of the HTML page
 * relating to share's site data list page.
 *
 * @author Michael Suzuki
 * @since 1.2
 */
public class DataListPage extends AbstractDataList
{
    private Log logger = LogFactory.getLog(this.getClass());

    protected static final By NEW_LIST_LINK = By.cssSelector("button[id$='default-newListButton-button']");
    private static final By LISTS_TYPES_CONTAINER = By.cssSelector("div[id$='itemTypesContainer']>div");
    private static final By DEFAULT_LISTS_CONTAINER = By.cssSelector("div[id*='default-lists']>div");
    private static final By LISTS_CONTAINER = By.cssSelector("div[id*='default-lists']>ul>li");
    private static final By NEW_LIST_FORM = By.cssSelector("div[id*='default-newList-form-fields']");


    public DataListPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataListPage render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(NEW_LIST_LINK));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataListPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataListPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to click New List button
     *
     * @return New List form object
     */
    private NewListForm clickNewList()
    {
        try
        {
            drone.findAndWait(NEW_LIST_LINK, 40000).click();
        }
        catch (TimeoutException te)
        {
            logger.debug("Timed out finding " + NEW_LIST_LINK);
        }
        return new NewListForm(drone);
    }

    /**
     * Method to creata a Data List
     *
     * @param listType requires ListType
     * @param title    title of the List
     * @param desc     Description
     * @return DataList page object
     */
    public DataListPage createDataList(NewListForm.TypeOptions listType, String title, String desc)
    {
        logger.info("Creating a Data List of given type");
        if(!drone.isElementDisplayed(NEW_LIST_FORM))
        {
            clickNewList();
            waitUntilAlert();
        }
        List<WebElement> typeOptions = drone.findAndWaitForElements(LISTS_TYPES_CONTAINER);
        typeOptions.get(listType.ordinal()).click();
        NewListForm newListForm = new NewListForm(drone);
        newListForm.inputTitleField(title);
        newListForm.inputDescriptionField(desc);
        newListForm.clickSave();
        waitUntilAlert();
        return new DataListPage(drone);
    }

    /**
     * Method to select a data list  according to specified name
     *
     * @param name
     */
    public void selectDataList(String name)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//div[contains(@id,'default-lists')]//a[text()='%s']", name))).click();
            waitUntilAlert();
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
    }

    /**
     * Method to verify whether New List button is displayed
     *
     * @return true if displayed
     */
    public boolean isNewListEnabled()
    {
        try
        {
            return drone.findAndWait(NEW_LIST_LINK).isEnabled();
        }
        catch (TimeoutException nse)
        {
            return false;
        }
    }

    private DataListDirectoryInfo getDataListDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = drone.findAndWait(By.xpath(String.format("//a[text()='%s']/..", title)), WAIT_TIME_3000);
            drone.mouseOverOnElement(row);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        catch (TimeoutException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        return new DataListDirectoryInfo(drone, row);
    }

    /**
     * Method for editing a data list
     * @param oldTitle
     * @param newTitle
     * @param newDescription
     * @return Data List page
     */
    public DataListPage editDataList (String oldTitle, String newTitle, String newDescription)
    {
        logger.info("Editing the data list " + oldTitle);
        NewListForm newListForm = getDataListDirectoryInfo(oldTitle).clickEdit();
        newListForm.inputTitleField(newTitle);
        newListForm.inputDescriptionField(newDescription);
        newListForm.clickSave();
        return new DataListPage(drone).render();
    }

    /**
     * Method for deleting a list with confirmation
     * @param title
     * @return Data List Page
     */
    public DataListPage deleteDataListWithConfirm (String title)
    {
        logger.info("Deleting " + title + "data list");
        try
        {
            getDataListDirectoryInfo(title).clickDelete();
            drone.findAndWait(SharePage.CONFIRM_DELETE).click();
            waitUntilAlert();
            return new DataListPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to delete a list");
        }
    }

    /**
     * Method to get a number of lists
     * @return number of lists
     */
    public int getListsCount ()
    {
        try
        {
            if(drone.isElementDisplayed(NEW_LIST_FORM))
            {
                return 0;
            }
            if(drone.isElementDisplayed(DEFAULT_LISTS_CONTAINER))
            {
                return 0;
            }
            return drone.findAndWaitForElements(LISTS_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find lists container");
        }
    }

    public boolean isEditDataListDisplayed(String list)
    {
        return getDataListDirectoryInfo(list).isEditDisplayed();
    }

    public boolean isDeleteDataListDisplayed(String list)
    {
        return getDataListDirectoryInfo(list).isDeleteDisplayed();
    }
}
