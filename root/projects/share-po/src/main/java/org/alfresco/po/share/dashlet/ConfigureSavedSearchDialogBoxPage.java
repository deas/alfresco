/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.dashlet;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * This page object holds all element of the HTML relating to Configure Saved
 * Search Dialogue box.
 * 
 * @author Ranjith Manyam
 */
public class ConfigureSavedSearchDialogBoxPage extends SharePage
{
    private static final Log LOGGER = LogFactory.getLog(ConfigureSavedSearchDialogBoxPage.class);
    private static final By CONFIGURE_SEARCH_DIALOG_BOX = By
            .cssSelector("div[id$='default-configDialog-configDialog_c'][style*='visibility: visible']>div[id$='_default-configDialog-configDialog']");
    private static final By CONFIGURE_SEARCH_DIALOG_HEADER = By
            .cssSelector("div[id$='default-configDialog-configDialog_c'][style*='visibility: visible'] div[id$='_default-configDialog-configDialog_h']");
    private static final By SEARCH_TERM_BOX = By.cssSelector("input[name='searchTerm']");
    private static final By TITLE_BOX = By.cssSelector("input[name='title']");
    private static final By LIMIT_SELECT_BOX = By.cssSelector("select[name='limit']");
    private static final By OK_BUTTON = By.cssSelector("button[id$='default-configDialog-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='default-configDialog-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector("a.container-close");
    private static final By HELP_BALLOON = By.cssSelector("div[style*='visible']>div.bd>div.balloon");
    private static final By HELP_BALLOON_TEXT = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.text");

    /**
     * Constructor.
     */
    protected ConfigureSavedSearchDialogBoxPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized ConfigureSavedSearchDialogBoxPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(CONFIGURE_SEARCH_DIALOG_BOX), getVisibleRenderElement(CONFIGURE_SEARCH_DIALOG_HEADER),
                getVisibleRenderElement(SEARCH_TERM_BOX), getVisibleRenderElement(TITLE_BOX), getVisibleRenderElement(LIMIT_SELECT_BOX),
                getVisibleRenderElement(OK_BUTTON), getVisibleRenderElement(CANCEL_BUTTON), getVisibleRenderElement(CLOSE_BUTTON));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfigureSavedSearchDialogBoxPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfigureSavedSearchDialogBoxPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    private boolean isConfigureSavedSearchDialogDisplayed()
    {
        try
        {
            drone.waitUntilElementDisappears(CONFIGURE_SEARCH_DIALOG_BOX, 2);
        }
        catch (TimeoutException te)
        {
            return true;
        }
        return false;
    }

    /**
     * This method is used to Finds OK button and clicks on it.
     * 
     * @return {@link org.alfresco.po.share.site.SiteDashboardPage}
     */
    public SharePage clickOnOKButton()
    {
        try
        {
            drone.find(OK_BUTTON).submit();
            if (isConfigureSavedSearchDialogDisplayed())
            {
                return this;
            }
            else
            {
                return new SiteDashboardPage(drone);
            }
        }
        catch (NoSuchElementException te)
        {
            LOGGER.error("Unable to find the OK button." + te.getMessage());
            throw new PageOperationException("Unable to click the OK Button.", te);
        }
    }

    /**
     * This method is used to Finds Cancel button and clicks on it.
     */
    public SiteDashboardPage clickOnCancelButton()
    {
        try
        {
            drone.find(CANCEL_BUTTON).click();
            return new SiteDashboardPage(drone);
        }
        catch (NoSuchElementException te)
        {
            LOGGER.error("Unable to find the CANCEL button." + te.getMessage());
            throw new PageOperationException("Unable to click the CANCEL Button.", te);
        }
    }

    /**
     * This method is used to Finds Close button and clicks on it.
     */
    public SiteDashboardPage clickOnCloseButton()
    {
        try
        {
            drone.find(CLOSE_BUTTON).click();
            return new SiteDashboardPage(drone);
        }
        catch (NoSuchElementException te)
        {
            LOGGER.error("Unable to find the CLOSE button." + te.getMessage());
            throw new PageOperationException("Unable to click the CLOSE Button.", te);
        }
    }

    /**
     * This method sets the given Search Term into Search Content Configure
     * Search Term box.
     * 
     * @param searchTerm
     */
    public void setSearchTerm(String searchTerm)
    {
        if (searchTerm == null)
        {
            throw new IllegalArgumentException("Search Term is required");
        }

        try
        {
            WebElement searchTermBox = drone.find(SEARCH_TERM_BOX);
            searchTermBox.clear();
            searchTermBox.sendKeys(searchTerm);
        }
        catch (NoSuchElementException te)
        {
            LOGGER.error("Unable to find the Search Term box." + te.getMessage());
            throw new PageOperationException("Unable to find the Search Term box.", te);
        }
    }

    /**
     * This method sets the given title into Site Content Configure title box.
     * 
     * @param title
     */
    public void setTitle(String title)
    {
        if (StringUtils.isEmpty(title))
        {
            throw new IllegalArgumentException("Title is required");
        }

        try
        {
            WebElement titleBox = drone.find(TITLE_BOX);
            titleBox.clear();
            titleBox.sendKeys(title);
        }
        catch (NoSuchElementException te)
        {
            LOGGER.error("Unable to find the Title box." + te.getMessage());
            throw new PageOperationException("Unable to find the Title box.", te);
        }
    }

    /**
     * Method to select Search searchLimit from search searchLimit drop down
     * 
     * @param searchLimit
     */
    public void setSearchLimit(SearchLimit searchLimit)
    {
        try
        {
            Select limitSelectBox = new Select(drone.find(LIMIT_SELECT_BOX));
            limitSelectBox.selectByValue(String.valueOf(searchLimit.getValue()));
        }
        catch (NoSuchElementException te)
        {
            LOGGER.error("Unable to find the Search SearchLimit drop down." + te.getMessage());
            throw new PageOperationException("Unable to find the Search SearchLimit drop down.", te);
        }
    }

    /**
     * Method to get available search limit values
     * 
     * @return {@link List} of {@link java.lang.Integer}
     */
    public List<Integer> getAvailableListOfSearchLimitValues()
    {
        List<Integer> searchLimitList = new ArrayList<Integer>();
        Select limitSelectBox = new Select(drone.find(LIMIT_SELECT_BOX));

        for (WebElement element : limitSelectBox.getOptions())
        {
            searchLimitList.add(Integer.parseInt(element.getText()));
        }
        return searchLimitList;
    }

    /**
     * Finds whether help balloon is displayed on this page.
     * 
     * @return True if the balloon displayed else false.
     */
    public boolean isHelpBalloonDisplayed()
    {
        try
        {
            return drone.findAndWait(HELP_BALLOON).isDisplayed();
        }
        catch (TimeoutException elementException){ }
        return false;
    }

    /**
     * This method gets the Help balloon messages and merge the message into
     * string.
     * 
     * @return String
     */
    public String getHelpBalloonMessage()
    {
        try
        {
            return drone.findAndWait(HELP_BALLOON_TEXT).getText();
        }
        catch (TimeoutException elementException)
        {
            LOGGER.error("Exceeded time to find the help ballon text");
        }
        throw new UnsupportedOperationException("Not able to find the help text");
    }

}
