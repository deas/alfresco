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

package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This class holds the elements of Insert/Edit Link page and which is invoked from Site Notice Tiny MCE editor dialog.
 * @author cbairaajoni
 *
 */
public class InsertOrEditLinkPage extends BaseAdvancedTinyMceOptionsPage
{
    private static Log logger = LogFactory.getLog(InsertOrEditLinkPage.class);
    
    @RenderWebElement
    private static By INSERT_OR_EDIT_LINK_PANEL = By.cssSelector("#general_panel td");
    private static By TITLE_BOX_CSS = By.cssSelector("#linktitle");
    private static By LINK_URL = By.cssSelector("#href");
    private static By TARGET_LIST = By.cssSelector("#target_list");
   
    /**
     * Constructor.
     * @param mainWindow 
     */
    public InsertOrEditLinkPage(WebDrone drone, String mainWindow)
    {
        super(drone, mainWindow);
    }

    /**
     * This enum is used to describe the target items present on Target dropdown.
     */
    public enum InsertLinkPageTargetItems
    {
        NOT_SET ("-- Not Set --"),
        OPEN_LINK_IN_THE_SAME_WINDOW ("Open Link in the Same Window"),
        OPEN_LINK_IN_NEW_WINDOW ("Open Link in a New Window");
        
        private String itemName;
        
        private InsertLinkPageTargetItems(String name)
        {
            itemName = name;
        }
        
        public String getItemName() {
            return itemName;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public InsertOrEditLinkPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InsertOrEditLinkPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public InsertOrEditLinkPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    /**
     * This method sets the given text into Link Url.
     * 
     * @param text
     */
    public void setLinkUrl(String text)
    {
        if(text == null)
        {
            throw new IllegalArgumentException("Link url value is required");
        }
        
        try
        {
            drone.findAndWait(LINK_URL).sendKeys(text);
        }
        catch(TimeoutException te)
        {
            logger.info("Unable to find the Link Url field.", te);
            throw new PageOperationException("Unable to find Link Url field.", te);
        }
    }
    
    /**
     * This method sets the given text into title.
     * 
     * @param text
     */
    public void setTitle(String text)
    {
        if(text == null)
        {
            throw new IllegalArgumentException("Title is required");
        }
        
        try
        {
            WebElement title = drone.findAndWait(TITLE_BOX_CSS);
            title.clear();
            title.sendKeys(text);
        }
        catch(TimeoutException te)
        {
            logger.info("Unable to find the Title field.", te);
            throw new PageOperationException("Unable to find Title field.", te);
        }
    }
    
    /**
     * This method sets the given Target item from the Target dropdown values.
     * 
     * @param target
     */
    public void setTarget(InsertLinkPageTargetItems target)
    {
        if(target == null)
        {
            throw new IllegalArgumentException("Target value is required");
        }
        
        try
        {
            selectOption(TARGET_LIST, target.getItemName());
        }
        catch(TimeoutException te)
        {
            logger.info("Unable to find the Target Item field.", te);
            throw new PageOperationException("Unable to find Target Item field.", te);
        }
    }
}