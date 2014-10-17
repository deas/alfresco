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

import java.util.Set;

import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Advanced TinyMce Editor page object, holds the extra features edit and format the text.
 * 
 * @author Chiran
 */
public class AdvancedTinyMceEditor extends TinyMceEditor
{
    private Log logger = LogFactory.getLog(AdvancedTinyMceEditor.class);

    private static final By LINK_CSS = By.cssSelector("i.mce-i-link");
    private static final By UNLINK_CSS = By.cssSelector("span.mceIcon.mce_unlink");
    private static final By ANCHOR_CSS = By.cssSelector("i.mce-i-anchor");
    private static final By IMAGE_LINK_CSS = By.cssSelector("i.mce-i-image");
    private static final By HTML_CODE_CSS = By.cssSelector("i.mce-i-code");
    @SuppressWarnings("unused")
    private String mainWindow = null;
    
    public AdvancedTinyMceEditor(WebDrone drone)
    {
        super(drone);
        mainWindow = drone.getWindowHandle();
    }

    /**
     * This method does the clicking on Insert/Edit Link present on Site Notice Configure Tiny mce editor.
     * 
     * @return InsertOrEditLinkPage
     */
    public InsertOrEditLinkPage clickInsertOrEditLink()
    {
        try
        {
            drone.findAndWait(LINK_CSS).click();
            return new InsertOrEditLinkPage(drone, drone.findFirstDisplayedElement(By.cssSelector("div.mce-reset")));
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the the Insert/Edit Link css on SiteNoticeTinyMce:", te);
        }
    }
    
    /**
     * This method does the clicking on UnLink present on Site Notice Configure Tiny mce editor.
     * 
     */
    public void clickUnLink()
    {
        try
        {
            drone.findAndWait(UNLINK_CSS).click();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the the Insert/Edit UnLink css on SiteNoticeTinyMce:", te);
        }
    }

    /**
     * This method does the clicking on Insert/Edit Anchor link present on Site Notice Configure Tiny mce editor.
     * 
     * @return InsertOrEditAnchorPage
     */
    public InsertOrEditAnchorPage selectInsertOrEditAnchor()
    {
        try
        {
            drone.findAndWait(ANCHOR_CSS).click();
            return new InsertOrEditAnchorPage(drone, drone.findFirstDisplayedElement(By.cssSelector("div.mce-reset")));
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the the Insert/Edit Anchor Link css on SiteNoticeTinyMce:", te);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Anchor dialog", nse);
        }
    }
    
    /**
     * This method does the clicking on Insert/Edit Image link present on Site Notice Configure Tiny mce editor.
     * 
     * @return InsertOrEditImagePage
     */
    public InsertOrEditImagePage selectInsertOrEditImage()
    {
        try
        {
            drone.findAndWait(IMAGE_LINK_CSS).click();
            return new InsertOrEditImagePage(drone, drone.findFirstDisplayedElement(By.cssSelector("div.mce-reset")));
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the the Insert/Edit Image Link css on SiteNoticeTinyMce:", te);
        }
    }
    
    /**
     * This method does the clicking on Html Editor link present on Site Notice Configure Tiny mce editor.
     * 
     * @return HtmlSourceEditorPage
     */
    public HtmlSourceEditorPage selectHtmlSourceEditor()
    {
        try
        {
            drone.findAndWait(HTML_CODE_CSS).click();
//            switchDroneToNewWindowOfListOfWindows(drone.getValue("page.html.source.editor.title"));
            return new HtmlSourceEditorPage(drone, drone.findFirstDisplayedElement(By.cssSelector("div.mce-reset")));
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the the HtmlEditor Link css on SiteNoticeTinyMce:", te);
        }
    }
    
    @SuppressWarnings("unused")
    private void switchDroneToNewWindowOfListOfWindows(String windowName)
    {
        Set<String> windowHandles = drone.getWindowHandles();

        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            logger.info(drone.getTitle());
            if (drone.getTitle().equals(windowName))
            {
                return;
            }
        }

        throw new PageOperationException("Unable to find the given window name.");
    }
}