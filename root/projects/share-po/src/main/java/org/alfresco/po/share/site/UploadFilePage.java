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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Upload file page object, holds all element of the HTML page relating to
 * share's upload file page in site.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class UploadFilePage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private final By UPLOAD_FORM = By.cssSelector("form[id$='_default-htmlupload-form']");

    /**
     * Constructor.
     */
    public UploadFilePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UploadFilePage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UploadFilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public UploadFilePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if the file upload dialog is displayed. A wait is introduce to
     * deal with javascript side effects not rendering the page in the same way
     * as html.
     * 
     * @return true if dialog is displayed.
     */
    public boolean isUploadFileDialogDisplayed()
    {
        boolean displayed = false;
        try
        {
            displayed = drone.find(UPLOAD_FORM).isDisplayed();
        }
        catch (TimeoutException e)
        {
            displayed = false;
        }

        return displayed;
    }

    /**
     * Action that selects the submit upload button.
     * @return boolean true if submited.
     */
    private void submitUpload()
    {
        By selector;
        if(alfrescoVersion.isCloud())
        {
            selector = By.id("template_x002e_dnd-upload_x002e_documentlibrary_x0023_default-cancelOk-button-button");
        }
        else
        {
            selector = By.cssSelector("button[id*='html-upload']");
        }
        try
        {
            HtmlElement okButton = new HtmlElement(drone.find(selector), drone);
            String ready = okButton.click();
            if(logger.isTraceEnabled())
            {
                logger.trace(String.format("operation completed in: %s",ready));
            }
            while(true)
            {
                try
                {
                    //Verify button has been actioned
                    if(!drone.find(selector).isDisplayed())
                    {
                        break;
                    }
                }
                catch (NoSuchElementException e)
                {
                    break;
                }
            }
            
        }
        //Check result has been updated
        catch (TimeoutException te){}
    }

    /**
     * Uploads a file by entering the file location into the input field and
     * submitting the form.
     * 
     * @param filePath String file location to upload
     * @return {@link SharePage} DocumentLibrary or a RepositoryPage response
     */
    public HtmlPage uploadFile(final String filePath)
    {
        WebElement input;
        if(alfrescoVersion.isFileUploadHtml5())
        {
            input = drone.find(By.cssSelector("input.dnd-file-selection-button"));
            input.sendKeys(filePath);
        } 
        else 
        {
            input = drone.find(By.cssSelector("input[id$='default-filedata-file']"));
            input.sendKeys(filePath);
            submitUpload(); 
        }
        
        if (logger.isTraceEnabled())
        {
            logger.trace("Upload button has been actioned");
        }

        DocumentLibraryPage lib = FactorySharePage.getPage(drone.getCurrentUrl(), drone).render();
        lib.setShouldHaveFiles(true);
        return lib;
    }
    /**
     * Clicks on the cancel link.
     */
    public void cancel()
    {
        drone.findAndWait(By.cssSelector("button[id$='default-cancel-button-button']")).click();
    }
}
