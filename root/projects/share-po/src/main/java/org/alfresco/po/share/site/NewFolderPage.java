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
package org.alfresco.po.share.site;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Create folder page object, holds all element of the HTML page relating to
 * share's create folder page.
 * 
 * @author Michael Suzuki
 * @since  1.0
 */
public class NewFolderPage extends SharePage
{
    private final By folderTitleCss = By.cssSelector("input[id$='default-createFolder_prop_cm_title']");
    private final By name = By.cssSelector("input[id$='default-createFolder_prop_cm_name']");
    private final By descriptionLocator = By.cssSelector("textarea[id$='default-createFolder_prop_cm_description']");
    private final By submitButton = By.cssSelector("button[id$='default-createFolder-form-submit-button']");
    private final By cancelButton = By.cssSelector("button[id$='createFolder-form-cancel-button']");
    
    private final RenderElement folderTitleElement = getVisibleRenderElement(folderTitleCss);
    private final RenderElement nameElement = getVisibleRenderElement(name);
    private final RenderElement descriptionElement = getVisibleRenderElement(descriptionLocator);
    private final RenderElement submitButtonElement = getVisibleRenderElement(submitButton);
    private final RenderElement cancelButtonElement = getVisibleRenderElement(cancelButton);
    
    public enum Fields
    {
        NAME(By.cssSelector("input[id$='default-createFolder_prop_cm_name']")),
        TITLE(By.cssSelector("input[id$='default-createFolder_prop_cm_title']")),
        DESCRIPTION(By.cssSelector("textarea[id$='default-createFolder_prop_cm_description']"));
        
        private By locator;
        
        private Fields(By locator)
        {
            this.locator = locator;
        }
    }

    /**
     * Constructor.
     */
    public NewFolderPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewFolderPage render(RenderTime timer)
    {
        elementRender(timer, folderTitleElement, nameElement, descriptionElement, submitButtonElement, cancelButtonElement);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewFolderPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewFolderPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * @see #createNewFolder(String, String)
     */
    public HtmlPage createNewFolder(final String folderName)
    {
        return createNewFolder(folderName, null);
    }

    /**
     * @see #createNewFolderWithValidation(String, String)
     */
    public HtmlPage createNewFolderWithValidation(final String folderName)
    {
        return createNewFolderWithValidation(folderName, null);
    }

    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName    mandatory folder name
     * @param description   optional folder description
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewFolder(final String folderName, final String description)
    {
        typeName(folderName);
        typeDescription(description);
        WebElement okButton = drone.find(submitButton);
        okButton.click();
        
        //Wait till the pop up disappears
        waitUntilMessageAppearAndDisappear("Folder");
        DocumentLibraryPage page = FactorySharePage.getPage(drone.getCurrentUrl(), drone).render();
        page.setShouldHaveFiles(true);
        return page;
    }
    
    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName    mandatory folder name
     * @param description   optional folder description
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewFolderWithValidation(final String folderName, final String description)
    {
        boolean validationPresent = false;
        
        typeNameWithValidation(folderName);
        typeDescription(description);
        
        WebElement okButton = drone.find(submitButton);
        okButton.click();
        
        HtmlPage htmlPage = FactorySharePage.resolvePage(drone);
        if(htmlPage instanceof ShareDialogue)
        {
            htmlPage = ((ShareDialogue) htmlPage).resolveShareDialoguePage().render();
        
            validationPresent = isMessagePresent(name);
            validationPresent = validationPresent || isMessagePresent(folderTitleCss);
            validationPresent = validationPresent || isMessagePresent(descriptionLocator);
        }
        
        if(!validationPresent)
        {
            //Wait till the pop up disappears
            waitUntilMessageAppearAndDisappear("Folder");
            DocumentLibraryPage page = FactorySharePage.getPage(drone.getCurrentUrl(), drone).render();
            page.setShouldHaveFiles(true);
            htmlPage = page;
        }

        return htmlPage.render();
    }
    
    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName    mandatory folder name
     * @param description   optional folder description
     * @param folderTitle  options folder Title
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewFolder(final String folderName, final String folderTitle, final String description)
    {
        if (folderName == null || folderName.isEmpty())
        {
            throw new UnsupportedOperationException("Folder Name input required.");
        }
        typeTitle(folderTitle);
        return createNewFolder(folderName, description);
    }
    
    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName    mandatory folder name
     * @param description   optional folder description
     * @param folderTitle   optional folder Title
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewFolderWithValidation(final String folderName, final String folderTitle, final String description)
    {
        typeTitle(folderTitle);
        return createNewFolderWithValidation(folderName, description);
    }
    
    /**
     * Clear & Type Folder Name on the Text box.
     * @param folderName
     */
    public void typeName(final String folderName)
    {
        if (StringUtils.isEmpty(folderName))
        {
            throw new IllegalArgumentException("Folder Name input required.");
        }
        clearAndType(name, folderName);
    }
    
    private void typeNameWithValidation(final String folderName)
    {
        clearAndType(name, folderName);
    }
    
    /**
     * Clear & Type the Folder Title for box.
     * @param folderTitle
     */
    public void typeTitle(final String folderTitle)
    {
        if(folderTitle != null && !folderTitle.isEmpty())
        {
            clearAndType(folderTitleCss, folderTitle);
        } 
    }
    
    /**
     * Clear & Type the Description for box.
     * @param description
     */
    public void typeDescription(final String description)
    {
        if (description != null && !description.isEmpty())
        {
            clearAndType(descriptionLocator, description);
        }
    }
    
    private void clearAndType(By by, String text)
    {
        WebElement element = drone.find(by);
        element.clear();
        element.sendKeys(text);
    }
    
    /**
     * Mimics the action of clicking the cancel button.
     * 
     * @return {@link HtmlPage} Page Response.
     */
    public HtmlPage selectCancel()
    {
        WebElement cancelElement = drone.find(cancelButton);
        String id = cancelElement.getAttribute("id");
        cancelElement.click();
        drone.waitUntilElementDeletedFromDom(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return drone.getCurrentPage();
    }
    /**
     * Wait until the black message box appear with text then wait until same black message disappear with text.
     * 
     * @param text - Text to be checked in the black message.
     */
    protected void waitUntilMessageAppearAndDisappear(String text)
    {
        waitUntilMessageAppearAndDisappear(text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
    }
    
    /**
     * Wait until the black message box appear with text then wait until same black message disappear with text.
     * 
     * @param text - Text to be checked in the black message.
     * @param timeInSeconds - Time to wait in seconds.
     */
    protected void waitUntilMessageAppearAndDisappear(String text, long timeInSeconds)
    {
        //drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), text, timeInSeconds);
        drone.waitUntilElementDisappears(By.cssSelector("div.bd>span.message"), timeInSeconds);
    }
    
    /**
     * Returns the validation message, if any, for the given Field.
     * @param field The reqired field
     * @return The validation message or an empty string if there is no message.
     */
    public String getMessage(Fields field)
    {
        String message = "";
        try
        {
            message = getValidationMessage(field.locator);
        }
        catch(NoSuchElementException e)
        {
        }
        return message;
    }

    private boolean isMessagePresent(By locator)
    {
        try
        {
            String message = getValidationMessage(locator);
            if(message.length() > 0)
            {
                return true;
            }
        }
        catch(NoSuchElementException e)
        {
        }
        return false;
    }
}
