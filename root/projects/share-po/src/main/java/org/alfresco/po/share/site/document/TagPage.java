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

import java.util.List;

import org.alfresco.webdrone.HtmlPage;
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

/**
 * Tag Page allows the user to manage tags relating to document in view.
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */
public class TagPage extends AbstractEditProperties
{
    private Log logger = LogFactory.getLog(this.getClass());

    protected static final By SELECT_HEADER = By.cssSelector("div[id$='cntrl-picker-head']");
    protected static final By ENTER_TAG_VALUE = By.cssSelector("input.create-new-input");
    protected static final By CREATE_TAG = By.cssSelector("span.createNewIcon");
    protected static final By REMOVE_TAG = By.cssSelector("span.removeIcon");
    protected static final By OK_BUTTON = By.cssSelector("button[id$='cntrl-ok-button']");
    protected static final By CANCEL_BUTTON = By.cssSelector("button[id$='cntrl-cancel-button']");
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
     * 
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
     * 
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
     * 
     * @param tagName
     * @return 
     */
    public HtmlPage enterTagValue(String tagName)
    {
        try
        {
            WebElement input = drone.find(ENTER_TAG_VALUE);
            input.clear();
            input.sendKeys(tagName);

            WebElement createButton = drone.find(CREATE_TAG);
            createButton.click();
            canResume();
            
            //TODO: change return when jira WEBDRONE-563 is implemented.
            return this.render();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find the EnterTagName or CreateTag css." + nse.getMessage());
        }

        throw new PageOperationException("Error in finding the Enter tag value css.");
    }
    
    /**
     * Click on OK button in tag page
     * 
     * @return EditDocumentPropertiesPage 
     */
    public EditDocumentPropertiesPage clickOkButton()
    {
        try
        {
            WebElement okButton = drone.findAndWait(OK_BUTTON);
            okButton.click();
            return new EditDocumentPropertiesPage(drone);
        } 
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find 'ok' button." + nse.getMessage());
        }
        throw new PageOperationException("Error in finding 'ok' button");
    }
    
    /**
     * Click on cancel button in tag page
     * 
     * @return EditDocumentPropertiesPage 
     */
    public EditDocumentPropertiesPage clickCancelButton()
    {
        try
        {
            WebElement cancelButton = drone.find(CANCEL_BUTTON);
            cancelButton.click();
            return new EditDocumentPropertiesPage(drone);
        } 
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find 'cancel' button." + nse.getMessage());
        }
        throw new PageOperationException("Error in finding 'cancel' button");
    }
    
    /**
     * Enter the tag name and click to Remove tag.
     * 
     * @param tagName
     * @return EditDocumentPropertiesPage
     */
    public EditDocumentPropertiesPage removeTagValue(String tagName)
    {
        if (StringUtils.isEmpty(tagName))
        {
            throw new IllegalArgumentException("TagName should not be null.");
        }

        try
        {
            WebElement tagElement = getSelectedTagElement(tagName);
            tagElement.findElement(REMOVE_TAG).click();
            drone.find(OK_BUTTON).click();
            return new EditDocumentPropertiesPage(drone);
        } 
        catch (NoSuchElementException nse)
        {
            logger.error("RemoveLink on Tag is not present." + nse.getMessage());
        } 
        catch (PageOperationException pe)
        {
            logger.error(pe.getMessage());
        }

        throw new PageOperationException("Error in removing tag on TagPage.");
    }

    /**
     * This private method is used to get the selected tag element from the list of tags on tag page.
     * 
     * @param tagName
     * @return WebElement
     */
    private WebElement getSelectedTagElement(String tagName)
    {
        List<WebElement> tags = null;
        String name = null;

        try
        {
            tags = drone.findAndWaitForElements(By
                    .cssSelector("div[id$='prop_cm_taggable-cntrl-picker-selectedItems'] tbody.yui-dt-data tr"));
        } 
        catch (TimeoutException te)
        {
            logger.error("Exceeded time to find the tags list." + te.getMessage());
        }

        if(tags != null)
        {
            for (WebElement tag : tags)
            {
                name = tag.findElement(By.cssSelector("td[class$='yui-dt-col-name'] h3.name")).getText();
                
                if (name != null && name.equalsIgnoreCase(tagName))
                {
                    return tag;
                }
                
            }
        }

        throw new PageOperationException("Tag is not present.");
    }
    
 }