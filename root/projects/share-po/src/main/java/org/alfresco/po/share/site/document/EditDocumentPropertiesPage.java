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
package org.alfresco.po.share.site.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Edit document properties page object, holds all element of the HTML page
 * relating to share's edit document properties page.
 * 
 * @author Michael Suzuki
 * @since 1.3.1
 */
public class EditDocumentPropertiesPage extends AbstractEditProperties
{
    public enum Fields
    {
        NAME, TITLE, DESCRIPTION, AUTHOR;
    }

    private final String tagName;

    protected EditDocumentPropertiesPage(WebDrone drone, final String tagName)
    {
        super(drone);
        this.tagName = tagName;
    }

    public EditDocumentPropertiesPage(WebDrone drone)
    {
        super(drone);
        tagName = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditDocumentPropertiesPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try
            {
                if (isShareDialogueDisplayed())
                {

                    if (isEditPropertiesPopupVisible())
                    {
                        break;
                    }
                }
                else
                {
                    if (isEditPropertiesVisible() && isSaveButtonVisible())
                    {
                        if (tagName == null || tagName.isEmpty())
                        {
                            break;
                        }
                        else
                        {
                            if (isTagVisible(tagName))
                            {
                                break;
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;

    }

    /**
     * Check to see if tags are visible on the page
     * and match the given tag.
     * 
     * @param name identifier tag name
     * @return true if name matches tag
     */
    private boolean isTagVisible(String name)
    {
        if (name == null || name.isEmpty())
        {
            throw new UnsupportedOperationException("Tag name required");
        }
        try
        {
            List<WebElement> tags = drone.findAll(By.cssSelector("div.itemtype-tag"));
            for (WebElement tag : tags)
            {
                if (name.equalsIgnoreCase(tag.getText()))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditDocumentPropertiesPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditDocumentPropertiesPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if edit properties element,
     * that contains the form is visible.
     * 
     * @return true if displayed
     */
    public boolean isEditPropertiesVisible()
    {
        try
        {
            if (!isShareDialogueDisplayed())
            {
                return drone.find(By.cssSelector("div#bd div.share-form")).isDisplayed();
            }
            else
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Get value seen on the author input value.
     */
    public String getAuthor()
    {
        return getValue(INPUT_AUTHOR_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param author String name input
     */
    public void setAuthor(final String author)
    {
        setInput(drone.find(INPUT_AUTHOR_SELECTOR), author);
    }

    /**
     * Check if tags are attached to the particular document value.
     * 
     * @return true if tag elements are displayed
     */
    public boolean hasTags()
    {
        try
        {
            return drone.find(By.cssSelector("div.itemtype-tag")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Check if categories are attached to the particular document.
     * 
     * @return true if category elements are displayed
     */
    // public boolean hasCategories()
    // {
    // try
    // {
    // return drone.find(By.cssSelector("div[class*='itemtype-cm:category']")).isDisplayed();
    // }
    // catch (NoSuchElementException nse)
    // {
    // return false;
    // }
    // }

    /**
     * Get value seen on the resolution unit input value.
     */
    public String getResolutionUnit()
    {
        return getValue(INPUT_RESOLUTION_UNIT_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param unit String name input
     */
    public void setResolutionUnit(final String unit)
    {
        setInput(drone.find(INPUT_RESOLUTION_UNIT_SELECTOR), unit);
    }

    /**
     * Get value seen on the vertical resolution unit input value.
     */
    public String getVerticalResolution()
    {
        return getValue(INPUT_VERTICAL_RESOLUTION_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param verticalResolution String name input
     */
    public void setVerticalResolution(final String verticalResolution)
    {
        setInput(drone.find(INPUT_VERTICAL_RESOLUTION_SELECTOR), verticalResolution);
    }

    /**
     * Get value seen on the orientation input value.
     */
    public String getOrientation()
    {
        return getValue(INPUT_ORIENTATION_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param orientation String name input
     */
    public void setOrientation(final String orientation)
    {
        setInput(drone.find(INPUT_ORIENTATION_SELECTOR), orientation);
    }

    /**
     * Get the value of selected mime type
     * 
     * @return String value of select mime type
     */
    protected String getMimeType()
    {
        if (isShareDialogueDisplayed())
        {
            throw new UnsupportedOperationException("This operation is not supported");
        }
        WebElement selected = drone.find(By.cssSelector("select[id$='prop_mimetype'] option[selected='selected']"));
        return selected.getText();
    }

    /**
     * Selects a mime type from the dropdown by matching
     * the option displayed with the mimeType input.
     * 
     * @param mimeType String identifier as seen on the dropdown
     * @deprecated use selectMimeType(enum mimeType)
     */
    public void selectMimeType(final String mimeType)
    {
        WebElement dropDown = drone.find(By.cssSelector("select[id$='prop_mimetype']"));
        Select select = new Select(dropDown);
        select.selectByVisibleText(mimeType);
    }

    /**
     * Selects a mime type from the dropdown by matching
     * the option displayed with the mimeType input.
     * 
     * @param mimeType String identifier as seen on the dropdown
     */
    public void selectMimeType(final MimeType mimeType)
    {

        if (isShareDialogueDisplayed())
        {
            throw new UnsupportedOperationException("This operation is not supported");
        }
        WebElement dropDown = drone.find(By.cssSelector("select[id$='prop_mimetype']"));
        Select select = new Select(dropDown);
        String value = select.getFirstSelectedOption().getAttribute("value");
        select.selectByValue(mimeType.getMimeCode());
        String selected = select.getFirstSelectedOption().getAttribute("value");
        if (StringUtils.isEmpty(value) || value.equalsIgnoreCase(selected))
        {
            throw new PageOperationException(String.format("Select in dropdown failed, expected %s actual %s", mimeType.getMimeCode(), selected));
        }
    }

    /**
     * Verify the save is visible.
     * 
     * @return true if visible
     */
    public boolean isSaveButtonVisible()
    {
        try
        {
            return drone.find(By.cssSelector("span.yui-button.yui-submit-button")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Clicks on save button.
     * 
     * @return {@link DocumentDetailsPage} page response
     */
    public HtmlPage selectSave()
    {
        clickSave();
        // WEBDRONE-523: Amended to return HtmlPage rather than DocumentDetailsPage
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Clicks on save button.
     * 
     * @return {@link DocumentDetailsPage} page response or {@link EditDocumentPropertiesPage} if there is a validation message.
     */
    public HtmlPage selectSaveWithValidation()
    {
        boolean validationPresent = false;
        validationPresent = isMessagePresent(INPUT_NAME_SELECTOR);
        validationPresent = validationPresent || isMessagePresent(INPUT_TITLE_SELECTOR);
        validationPresent = validationPresent || isMessagePresent(INPUT_DESCRIPTION_SELECTOR);
        validationPresent = validationPresent || isMessagePresent(INPUT_AUTHOR_SELECTOR);

        if (!validationPresent)
        {
            clickSave();
        }
        // WEBDRONE-523: Amended to return HtmlPage rather than DocumentDetailsPage
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Select cancel button.
     * 
     * @return {@link DocumentDetailsPage} page response
     */
    public HtmlPage selectCancel()
    {
        clickOnCancel();
        // WEBDRONE-523: Amended to return HtmlPage rather than DocumentDetailsPage
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Returns a map of validation messages for all the fields in the form.
     * 
     * @param field The reqired field
     * @return The validation message or an empty string if there is no message.
     */
    public Map<Fields, String> getMessages()
    {
        Map<Fields, String> messages = new HashMap<>();

        String message = getMessage(INPUT_NAME_SELECTOR);
        if (message.length() > 0)
        {
            messages.put(Fields.NAME, message);
        }

        message = getMessage(INPUT_TITLE_SELECTOR);
        if (message.length() > 0)
        {
            messages.put(Fields.TITLE, message);
        }

        message = getMessage(INPUT_DESCRIPTION_SELECTOR);
        if (message.length() > 0)
        {
            messages.put(Fields.DESCRIPTION, message);
        }

        message = getMessage(INPUT_AUTHOR_SELECTOR);
        if (message.length() > 0)
        {
            messages.put(Fields.AUTHOR, message);
        }

        return messages;
    }

    private String getMessage(By locator)
    {
        String message = "";
        try
        {
            message = getValidationMessage(locator);
        }
        catch (NoSuchElementException e)
        {
        }
        return message;
    }

    private boolean isMessagePresent(By locator)
    {
        try
        {
            String message = getValidationMessage(locator);
            if (message.length() > 0)
            {
                return true;
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Verify if edit properties element,
     * that contains the form is visible.
     * 
     * @return true if displayed
     */
    public boolean isEditPropertiesPopupVisible()
    {

        if (!isShareDialogueDisplayed())
        {
            throw new UnsupportedOperationException("This operation is unsupported.");
        }
        try
        {
            return drone.find(By.cssSelector("form.bd")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
    }

    /**
     * click on All Properties button on Edit Properties pop-up
     *
     * @return {@link EditDocumentPropertiesPage} page response
     */
    public HtmlPage selectAllProperties(){
        clickAllProperties();
        return FactorySharePage.resolvePage(drone);
    }
}
