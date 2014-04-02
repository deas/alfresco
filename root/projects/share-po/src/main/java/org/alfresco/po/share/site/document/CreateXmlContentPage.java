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

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Create content page object, Where user can create content.
 * 
 * @author Jamie Allison,Shan Nagarajan,Subashni Prasanna
 * @since  1.6.1
 */
public class CreateXmlContentPage extends InlineEditPage
{
    protected static final By NAME = By.cssSelector("input[id$='default_prop_cm_name']");
    protected static final By TITLE = By.cssSelector("input[id$='default_prop_cm_title']");
    protected static final By DESCRIPTION = By.cssSelector("textarea[id$='default_prop_cm_description']");
    protected static final By CONTENT = By.cssSelector("textarea[id$='default_prop_cm_content']");
    protected static final By SUBMIT_BUTTON = By.cssSelector("button[id$='form-submit-button']");
    protected static final By CANCEL_BUTTON = By.cssSelector("button[id$='form-cancel-button']");
    
    public CreateXmlContentPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateXmlContentPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NAME), getVisibleRenderElement(TITLE), getVisibleRenderElement(DESCRIPTION), getVisibleRenderElement(CONTENT), getVisibleRenderElement(SUBMIT_BUTTON), getVisibleRenderElement(CANCEL_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateXmlContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateXmlContentPage render(long time)
    {
        return render(new RenderTime(time));
    }
    
    /**
     * Create the content with name, title and description.
     * 
     * @param name - The Name of the Document
     * @param title - The Title of the Document
     * @param description - Description
     * @param details - Document Content
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage create(ContentDetails details) 
    {
    	createContent(details);
        WebElement createButton = drone.find(SUBMIT_BUTTON);
        createButton.click();
        canResume();
        return drone.getCurrentPage();
        
    }
    
   /**
    * Cancel button interaction on the form
    * @return {@link DocumentDetailsPage}
    */
    public DocumentLibraryPage cancel(ContentDetails details)
    {
    	createContent(details);
       	WebElement cancelButton = drone.findAndWait(CANCEL_BUTTON);
   		cancelButton.click();
   		return new DocumentLibraryPage(drone);
    }
    
    private void createContent(ContentDetails details) 
    {
    	if(details == null || details.getName() == null || details.getName().trim().isEmpty())
        {
            throw new UnsupportedOperationException("Name can't be null or empty");
        }
        WebElement nameElement = drone.find(NAME);
        nameElement.clear();
        nameElement.sendKeys(details.getName());
        
        if(details.getTitle() != null)
        {
            WebElement titleElement = drone.find(TITLE);
            titleElement.clear();
            titleElement.sendKeys(details.getTitle());
        }
        
        if(details.getDescription() != null)
        {
            WebElement descriptionElement = drone.find(DESCRIPTION);
            descriptionElement.clear();
            descriptionElement.sendKeys(details.getDescription());
        }
        
        if(details.getContent() != null)
        {
            WebElement contentElement = drone.find(CONTENT);
            contentElement.clear();
            contentElement.sendKeys(details.getContent());
        }
        
    }
}
