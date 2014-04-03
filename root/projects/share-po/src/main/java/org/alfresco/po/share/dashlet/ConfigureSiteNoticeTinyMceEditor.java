/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.dashlet;

import java.util.NoSuchElementException;

import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;

/**
 * Site Notice Configure TinyMce Editor page object, it is used to apply the styles to text using fore&back color,font and formatting.
 * 
 * @author Chiran
 */
public class ConfigureSiteNoticeTinyMceEditor extends TinyMceEditor
{
    private Log logger = LogFactory.getLog(ConfigureSiteNoticeTinyMceEditor.class);
    public static final String frame1 = "page_x002e_component-1-2_x002e_site_x007e_";
    public static final String frame2 = "_x007e_dashboard_x0023_default-configDialog-text_ifr";

    public ConfigureSiteNoticeTinyMceEditor(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Get text from TinyMCE editor.
     * 
     * @param siteName
     * @return String
     */
    public String getTextFromConfigureTextEditor(String siteName)
    {
        if (siteName == null)
        {
            throw new IllegalArgumentException("SiteName is required");
        }

        try
        {
            setTinyMce(frame1 + siteName.toLowerCase() + frame2);
            drone.switchToFrame(getFrameId());
            String text = drone.find(By.cssSelector(TINYMCE_CONTENT)).getText();
            drone.switchToDefaultContent();
            return text;
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element : does not exist", noSuchElementExp);
            throw new PageException("Unable to find text in tinyMCE editor.");
        }
    }

    /**
     * Sets the tinymce editor frame id by updating with siteName in it.
     * 
     * @param siteName
     */
    public void setTinyMceOfConfigureDialogBox(String siteName)
    {
        setTinyMce(frame1 + siteName.toLowerCase() + frame2);
    }

    /**
     * Click to select color code on text.
     */
    public void clickColorCode()
    {
        selectTextFromEditor();
        clickElementOnRichTextFormatter("a[id$='default-configDialog-text_forecolor_open']");
        setFormatType(FormatType.COLOR_CODE);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
    }

    /**
     * Click to select color code on text.
     */
    public void clickBackgroundColorCode()
    {
        selectTextFromEditor();
        clickElementOnRichTextFormatter("a[id$='default-configDialog-text_backcolor_open']");
        setFormatType(FormatType.COLOR_CODE);
        clickElementOnRichTextFormatter("#_mce_item_70");
    }

    /**
     * Click on TinyMCE editor's format option.
     * 
     * @param formatType
     */
    public void clickTextFormatterFromConfigureDialog(FormatType formatType)
    {
        setFormatType(formatType);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
    }
}