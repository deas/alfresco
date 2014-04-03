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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * In Line Edit Page Object, Where user edit the content.
 * 
 * @author Shan Nagarajan
 * @since  1.6.1
 */
public class EditTextDocumentPage extends CreatePlainTextContentPage
{
    private static final Log logger = LogFactory.getLog(EditTextDocumentPage.class);
    public EditTextDocumentPage(WebDrone drone)
    {
        super(drone);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public EditTextDocumentPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditTextDocumentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditTextDocumentPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Save the {@link ContentDetails}.
     * 
     * @param details - The {@link ContentDetails} to be saved.
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage save(ContentDetails details)
    {
        return create(details);
    }
    
    /**
     * Save the {@link ContentDetails}.
     * 
     * @param details - The {@link ContentDetails} to be saved.
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage saveWithValidation(ContentDetails details)
    {
        createWithValidation(details);
        return FactorySharePage.resolvePage(drone);
    }
    
    /**
     * Get the {@link ContentDetails} when try to do in line edit.
     * 
     * @return {@link ContentDetails}
     */
    public ContentDetails getDetails()
    {
        ContentDetails details = null;
        WebElement element = drone.find(NAME);
        if(element != null)
        {
            details =  new ContentDetails();
            details.setName(element.getAttribute("value"));
            
            element = drone.findAndWait(TITLE);
            if(element != null)
            {
                details.setTitle(element.getAttribute("value"));
            }
            
            element = drone.find(DESCRIPTION);
            if(element != null)
            {
                details.setDescription(element.getText());
            }
            
            element = drone.findAndWait(CONTENT);
            
            if(element != null)
            {
                details.setContent(element.getAttribute("value"));
            }
        }
        return details;
    }

    @SuppressWarnings("unchecked")
    /**
     * Method to select Cancel button
     * @return {@Link DocumentDetailsPage or @Link DocumentLibraryPage}
     */
    public <T extends SharePage> T selectCancel()
    {
        try
        {
            drone.findAndWait(By.cssSelector("button[id$='_default-form-cancel-button']")).click();
            return (T) FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException te)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Cancel button doesn't exist");
            }
        }
        throw new PageException();
    }
}
