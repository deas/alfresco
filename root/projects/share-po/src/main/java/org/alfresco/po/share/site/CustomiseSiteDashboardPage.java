/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

package org.alfresco.po.share.site;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Customize site dashboard page object, holds all element of the html page
 * relating to customize site dashboard page.
 *
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class CustomiseSiteDashboardPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By CHANGE_LAYOUT_BUTTON = By.cssSelector("button[id*='change-button']");
    private static final By ADD_DASHLET_BUTTON = By.cssSelector("button[id*='addDashlets-button']");
    private static final By TRASHCAN = By.cssSelector(".trashcan");
    private static final By SAVE_BUTTON = By.cssSelector("button[id$=save-button-button]");
    private static final By AVAILABLE_DASHLETS = By.cssSelector(".availableList>li>div.dnd-draggable");
    private static final By AVAILABLE_DASHLETS_NAMES = By.cssSelector("ul.availableList>li.availableDashlet>span");
    private static final String DRAGABLE_COLUMN_FORMAT = "ul[id$='column-ul-%d']>li>div.dnd-draggable";
    private static final String COLUMN_FORMAT = "ul[id$='column-ul-%d']";
    private static final String DASHLET_IN_COLUMN_NAME_FORMAT = "//ul[contains(@id,'column-ul-%d')]/li/span";
    private static final int NUMBER_OF_COLUMNS = 4;
    private static final int MAX_DASHLETS_IN_COLUMN = 5;
    private static final By SELECT_ONE_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-1-column-button']");
    private static final By SELECT_TWO_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-2-columns-wide-left-button']");
    private static final By SELECT_THREE_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-3-columns-button']");
    private static final By SELECT_FOUR_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-4-columns-button']");
    private static final By TITLE_ON_PAGE = By.cssSelector(".sub-title");

    /**
     * Constructor.
     */
    public CustomiseSiteDashboardPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomiseSiteDashboardPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(TITLE_ON_PAGE),
                getVisibleRenderElement(LICENSE_TO),
                getVisibleRenderElement(USER_LOGGED_IN_LABEL)
        );
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomiseSiteDashboardPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomiseSiteDashboardPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Mimics the action of selection change layout button.
     *
     * @return {@link CustomiseSiteDashboardPage}
     */
    public CustomiseSiteDashboardPage selectChangeLayout()
    {
        drone.find(CHANGE_LAYOUT_BUTTON).click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Change Layout button has been found and selected");
        }

        return new CustomiseSiteDashboardPage(drone);
    }

    /**
     * Mimics the action of the Add Dashlets button click.
     */
    public void selectAddDashlets()
    {
        drone.find(ADD_DASHLET_BUTTON).click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Add Dashlet button has been found and selected");
        }
    }

    /**
     * Mimics the action of removing the all dashlets from Columns.
     *
     * @return {@link SiteDashboardPage}
     */
    public SiteDashboardPage removeAllDashlets()
    {
        removeAllDashletsWithOutConfirm();
        return selectOk();
    }

    /**
     * Remove dashlet by name.
     *
     * @param dashlet
     */
    public SiteDashboardPage remove(Dashlet dashlet)
    {
        String dashletXpath = String.format("//div[@class='column']//span[text()='%s']/../div", dashlet.getDashletName());
        WebElement element = drone.findAndWait(By.xpath(dashletXpath));
        drone.dragAndDrop(element, drone.find(TRASHCAN));
        waitUntilAlert();
        return selectOk();
    }

    private void removeAllDashletsWithOutConfirm()
    {
        List<WebElement> elements = getDragDashletElem();
        if (elements.size() != 0)
        {
            for (WebElement source : elements)
            {
                drone.dragAndDrop(source, drone.find(TRASHCAN));
                waitUntilAlert();
            }
        }
        else
        {
            logger.info("All Dashlets already removed");
        }
    }

    private List<WebElement> getDragDashletElem()
    {
        List<WebElement> allDashlets = new ArrayList<WebElement>();
        for (int column = 0; column <= NUMBER_OF_COLUMNS; column++)
        {
            allDashlets.addAll(drone.findAll(By.cssSelector(String.format(DRAGABLE_COLUMN_FORMAT, column))));
        }
        return allDashlets;
    }

    /**
     * Select Layout from given {@link SiteLayout}.
     *
     * @return {@link SiteDashboardPage}
     */
    public SiteDashboardPage selectDashboard(SiteLayout layout)
    {
        drone.findAndWait(layout.getLocator()).click();
        return selectOk();
    }

    /**
     * Add all the dashlets into different columns available.
     *
     * @return {@link SiteDashboardPage}
     */
    public SiteDashboardPage addAllDashlets()
    {
        this.selectAddDashlets();
        removeAllDashletsWithOutConfirm();
        waitUntilAlert();
        List<WebElement> dashlets = drone.findAll(AVAILABLE_DASHLETS);
        if (logger.isTraceEnabled())
        {
            logger.trace("There are " + dashlets.size() + " dashlets found.");
        }
        int currentColumn = 1;
        int dashletCounter = 1;
        WebElement target = null;

        for (WebElement source : dashlets)
        {
            target = drone.find(By.cssSelector(String.format(COLUMN_FORMAT, currentColumn)));
            drone.dragAndDrop(source, target);
            waitUntilAlert();

            if (dashletCounter % MAX_DASHLETS_IN_COLUMN == 0)
            {
                currentColumn++;
            }
            dashletCounter++;
        }

        return selectOk();
    }

    /**
     * Add given dashlet into given column.
     *
     * @param dashletName
     * @param columnNumber
     * @return {@link SiteDashboardPage}
     */
    public SiteDashboardPage addDashlet(Dashlet dashletName, int columnNumber)
    {
        if (dashletName == null)
        {
            throw new IllegalArgumentException("Dashlet Name is required");
        }

        if (columnNumber < 1 || columnNumber > NUMBER_OF_COLUMNS)
        {
            throw new IllegalArgumentException("Column number should be between 1 and 4");
        }

        WebElement newDashlet = null;
        int noOfColumns = 0;

        this.selectAddDashlets();

        try
        {
            String dashletXpath = String.format("//*[@class='availableDashlet dnd-draggable']/span[text()='%s']", dashletName.getDashletName());
            WebElement element = drone.findAndWait(By.xpath(dashletXpath));
            element.click();
            List<WebElement> dashlets = drone.findAndWaitForElements(AVAILABLE_DASHLETS_NAMES);
            for (WebElement source : dashlets)
            {
                if (source.getText().contains(dashletName.getDashletName()))
                {
                    newDashlet = source;
                    break;
                }
            }
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find the Available dashlet names " + te);
            }
        }

        if (newDashlet != null)
        {
            try
            {
                String columns = drone.find(By.cssSelector("div[id$='default-wrapper-div']")).getAttribute("class");
                if (!StringUtils.isEmpty(columns))
                {
                    String columnSize = columns.substring(columns.length() - 1);
                    noOfColumns = Integer.valueOf(columnSize);
                }
            }
            catch (NoSuchElementException te)
            {
                logger.info("Unable to find the Columns css " + te);
            }

            if (columnNumber <= noOfColumns)
            {
                try
                {
                    List<WebElement> existingDashletsInColumn = drone.findAndWaitForElements(By.cssSelector(String.format("ul[id$='column-ul-%d'] li",
                            columnNumber)));

                    if (existingDashletsInColumn.size() < MAX_DASHLETS_IN_COLUMN)
                    {
                        WebElement target = drone.findAndWait(By.cssSelector(String.format("ul[id$='column-ul-%d'] li", columnNumber)));
                        drone.executeJavaScript("window.scrollBy(0,250)", "");
                        drone.dragAndDrop(newDashlet, target);
                        return selectOk();
                    }
                    else
                    {
                        throw new PageOperationException("Exceeded the no. of dashlets in given column.");
                    }
                }
                catch (TimeoutException te)
                {
                    if (logger.isTraceEnabled())
                    {
                        logger.info("Exceeded time to find the Available dashlet names ", te);
                    }
                }
            }
            else
            {
                throw new PageOperationException("Expected column does not exist in available columns list.");
            }
        }

        throw new PageOperationException("Error in adding dashlet using drag and drop");
    }

    /**
     * This method used to select the ok button present on Customize site
     * dashboard page.
     *
     * @return SiteDashboardPage
     */
    public SiteDashboardPage selectOk()
    {
        try
        {
            drone.find(SAVE_BUTTON).click();
        }
        catch (NoSuchElementException te)
        {
            logger.info("Unable to find the Save button css " + te);
        }

        return new SiteDashboardPage(drone).render();
    }

    /**
     * Method to change layout on Customize Site Dashboard page
     *
     * @param numOfColumns
     */

    public void selectNewLayout(int numOfColumns)
    {
        if (numOfColumns > NUMBER_OF_COLUMNS || numOfColumns < 1)
        {
            throw new IllegalArgumentException("Select correct number of columns");
        }
        else
        {
            try
            {
                switch (numOfColumns)
                {
                    case 1:
                        drone.findAndWait(SELECT_ONE_COLUMN_LAYOUT_BTN).click();
                        break;
                    case 2:
                        drone.findAndWait(SELECT_TWO_COLUMN_LAYOUT_BTN).click();
                        break;
                    case 3:
                        drone.findAndWait(SELECT_THREE_COLUMN_LAYOUT_BTN).click();
                        break;
                    case 4:
                        drone.findAndWait(SELECT_FOUR_COLUMN_LAYOUT_BTN).click();
                        break;
                }
            }
            catch (NoSuchElementException nse)
            {
                logger.info("Unable to find the Select button css " + nse);
            }
        }
    }

    /**
     * Method return list of dashlet names from column with selected number.
     *
     * @param columnNumber
     * @return
     */
    public List<String> getDashletNamesFrom(int columnNumber)
    {
        List<String> dashletNames = new ArrayList<String>();
        List<WebElement> dashletsElem = getDashletsElemFrom(columnNumber);
        for (WebElement dashlet : dashletsElem)
        {
            dashletNames.add(dashlet.getText());
        }
        return dashletNames;
    }

    /**
     * Return dashlets count from column with selected number.
     *
     * @param columnNumber
     * @return
     */
    public int getDashletsCountIn(int columnNumber)
    {
        return getDashletsElemFrom(columnNumber).size();
    }

    /**
     * Check  is Dashlet with selected name in columnt with number columnNumber.
     *
     * @param dashlet
     * @param columnNumber
     * @return
     */
    public boolean isDashletInColumn(Dashlet dashlet, int columnNumber)
    {
        List<WebElement> dashletsElem = getDashletsElemFrom(columnNumber);
        for (WebElement dashletElem : dashletsElem)
        {
            if (dashletElem.getText().equals(dashlet.getDashletName()))
            {
                return true;
            }
        }
        return false;
    }


    private List<WebElement> getDashletsElemFrom(int columnNumber)
    {
        checkArgument(columnNumber > 0 && columnNumber <= NUMBER_OF_COLUMNS);
        By DashletsInColumn = By.xpath(String.format(DASHLET_IN_COLUMN_NAME_FORMAT, columnNumber));
        try
        {
            return drone.findAndWaitForElements(DashletsInColumn);
        }
        catch (TimeoutException e)
        {
            return Collections.emptyList();
        }
        catch (StaleElementReferenceException e)
        {
            return getDashletsElemFrom(columnNumber);
        }
    }
}