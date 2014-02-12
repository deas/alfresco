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
package org.alfresco.po.share.site.document;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Tag Page allows the user to manage tags relating to document in view.
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */
public class TagPage extends AbstractEditProperties
{
    protected static final By SELECT_HEADER = By.cssSelector("div[id$='cntrl-picker-head']");
    protected static final By ENTER_TAG_VALUE = By.cssSelector("input.create-new-input");
    protected static final By CREATE_TAG = By.cssSelector("span.createNewIcon");
    protected static final By OK_BUTTON = By.cssSelector("button[id$='cntrl-ok-button']");
    
    /**
     * Constructor.
     * @param drone {@link WebDrone}
     */
    public TagPage(WebDrone drone)
    {
        super(drone);
    }


    @SuppressWarnings("unchecked")
    @Override
    public TagPage render(RenderTime timer)
    {
        while(true)
        {
            timer.start();
            try
            {
                if(isTagPageVisible() && isTagInputVisible())
                {
                    break;
                }
            }
            catch (Exception e) {}
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TagPage render(long time) 
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TagPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    /**
     * Verify if tagPage is displayed.
     * @return true if displayed
     */
    public boolean isTagPageVisible()
    {
        try
        {
            return(drone.find(SELECT_HEADER).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
    
    /**
     * Checks if tag input field is visible.
     * @return true if visible
     */
    public boolean isTagInputVisible()
    {
        try
        {
            return drone.find(ENTER_TAG_VALUE).isDisplayed();
        }
        catch (NoSuchElementException nse) {}
        return false;
    }
    
    /**
     * Enter the tag name and click to Add tag.
     * @return EditDocumentPropertiesPage 
     */
    public EditDocumentPropertiesPage enterTagValue(String tagName)
    {
        WebElement input = drone.find(ENTER_TAG_VALUE);
        input.clear();
        input.sendKeys(tagName);
        
        WebElement createButton = drone.find(CREATE_TAG);
        createButton.click();
        canResume();
        
        WebElement okButton = drone.find(OK_BUTTON);
        okButton.click();
        return new EditDocumentPropertiesPage(drone, tagName);
    }
 }