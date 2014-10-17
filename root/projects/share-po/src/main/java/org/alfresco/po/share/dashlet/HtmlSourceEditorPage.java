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

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This class holds the elements of HtmlSourceEditor Page and which is invoked from Site Notice Tiny MCE editor dialog.
 * @author cbairaajoni
 * 
 */
public class HtmlSourceEditorPage extends BaseAdvancedTinyMceOptionsPage
{
    private static Log logger = LogFactory.getLog(HtmlSourceEditorPage.class);

    @RenderWebElement
    private static By HTML_CONTENT_SOURCE = By.xpath("//div[starts-with(@class, 'mce-container-body')]/textarea");
    @RenderWebElement
    private static By HTML_SOURCE_LABEL = By.cssSelector("div.mce-reset>div.mce-window-head>div.mce-title");

    /**
     * Constructor.
     * 
     * @param element
     */
    public HtmlSourceEditorPage(WebDrone drone, WebElement element)
    {
        super(drone, element);
    }

    @SuppressWarnings("unchecked")
    public HtmlSourceEditorPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public HtmlSourceEditorPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    public HtmlSourceEditorPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method sets the given html code into html source element.
     * 
     * @param htmlSource
     */
    public void setHTMLSource(String htmlSource)
    {
        if (htmlSource == null)
        {
            throw new IllegalArgumentException("htmlSource should not be null");
        }

        try
        {
            WebElement source = drone.findAndWait(HTML_CONTENT_SOURCE);
            source.clear();
            source.sendKeys(htmlSource);
        }
        catch (TimeoutException te)
        {
            logger.info("Unable to find the HtmlSource field.", te);
            throw new PageOperationException("Unable to find HtmlSource field.", te);
        }
    }
}