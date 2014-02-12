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

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Upload file page object, holds all element of the html page relating to
 * share's upload file page in site.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class UpdateFilePage extends SharePage
{
    private static Log logger = LogFactory.getLog(UpdateFilePage.class);
    
	private static final String PLANIN_HTML_IDENTIFIER = "html-upload";
	private static final String HTML5_IDENTIFIER = "dnd-upload";
    private static final String NON_HTML5_INPUT_FILE_FIELD = "input[id$='default-filedata-file']";
	private static final String INPUT_DND_FILE_SELECTION_BUTTON = "input.dnd-file-selection-button";
	private static final String UPDATE_PAGE_TITLE_SPAN_CSS = "div[id$='html-upload'] span[id$='default-title-span']";
	protected  String textAreaCssLocation;
	protected String minorVersionRadioButton;
	protected  String majorVersionRadioButton;
    protected  String submitButton;
    protected String cancelButton;
    
    protected final String documentVersion;
    @SuppressWarnings("unused")
    private final boolean isEditOffLine;

    /**
     * Constructor.
     */
    public UpdateFilePage(WebDrone drone, final String documentVersion, final boolean editOffline)
    {
        super(drone);
        this.documentVersion = documentVersion;
        this.isEditOffLine = editOffline;
        
        //Check if supports HTML5 form input as cloud supports and enterprise doesnt.
        String prefix = alfrescoVersion.isFileUploadHtml5() ? HTML5_IDENTIFIER : PLANIN_HTML_IDENTIFIER;
        textAreaCssLocation = String.format("div[id*='%s'] textarea[id$='-description-textarea']", prefix);
        minorVersionRadioButton = String.format("div[id*='%s'] input[id$='minorVersion-radioButton']", prefix);
        majorVersionRadioButton = String.format("div[id*='%s'] input[id$='majorVersion-radioButton']", prefix);
        submitButton =  String.format("span[id*='%s'] button[id$='default-upload-button-button']", prefix);
        
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public synchronized UpdateFilePage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try { this.wait(50L); } catch (InterruptedException e) {}
            // Look for comment box
            try
            {
                if (!drone.findAndWait(By.cssSelector(textAreaCssLocation)).isDisplayed())
                {
                    continue;
                }
                if (!drone.find(By.cssSelector(minorVersionRadioButton)).isDisplayed())
                {
                    continue;
                }
                if (!drone.find(By.cssSelector(majorVersionRadioButton)).isDisplayed())
                {
                    continue;
                }
            }
            catch (NoSuchElementException e)
            {
                // It's not there
                continue;
            }
            // Everything was found and is visible
            break;
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UpdateFilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public UpdateFilePage render(final long time)
    {
        return render(new RenderTime(time));
    }
    
    /**
     * Select the minor version tick box.
     */
    public void selectMinorVersionChange()
    {
        drone.findAndWait(By.cssSelector(minorVersionRadioButton)).click();
    }

    /**
     * Select the major version tick box.
     */
    public void selectMajorVersionChange()
    {
        drone.findAndWait(By.cssSelector(majorVersionRadioButton)).click();
    }

    /**
     * Clicks on the submit upload button.
     */
    public HtmlPage submit()
    {
        //Get the expected version number
        
        drone.findAndWait(By.cssSelector(submitButton)).click();
        int countCheck = 0;
        //Check upload file dialog had gone
        while(true && countCheck < 3)
        {
            try
            {
                drone.findAndWait(By.cssSelector(UPDATE_PAGE_TITLE_SPAN_CSS), 20, 10);
            }
            catch (Exception e)
            {
                //We are now ready to move off the page.
                break;
            }
            countCheck ++;
        }
        return new DocumentDetailsPage(drone, documentVersion);
    }

    /**
     * Uploads a file by entering the file location into the input field and
     * submitting the form.
     * 
     * @param filePath String file location to upload
     */
    public void uploadFile(final String filePath)
    {
        WebElement input;
        if(alfrescoVersion.isFileUploadHtml5())
        {
            input = drone.find(By.cssSelector(INPUT_DND_FILE_SELECTION_BUTTON));
        } 
        else 
        {
            input = drone.find(By.cssSelector(NON_HTML5_INPUT_FILE_FIELD));
        }
        input.sendKeys(filePath);
    }

    /**
     * Sets the comment in the comments field
     * 
     * @param comment String of user comment.
     */
    public void setComment(final String comment)
    {
        WebElement commentBox = drone.find(By.cssSelector(textAreaCssLocation));
        commentBox.click();
        commentBox.sendKeys(comment);
    }
    
    /**
     * Clicks on the submit upload button.
     */
    public void selectCancel()
    {
        try
        {
            drone.findAndWait(By.cssSelector(cancelButton)).click();
        }
        catch(TimeoutException e)
        {
            logger.error ("Exceeded time to find the cancel button." + e.getMessage());
            throw new PageException("Unable to find the cancel button css : "+ cancelButton);
        }
    }
}
