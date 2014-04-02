/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of
 * Alfresco Alfresco is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. Alfresco is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Alfresco. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This class holds the Gallery View specific File Directory Info implementations.
 * 
 * @author cbairaajoni
 * 
 */
public class GalleryViewFileDirectoryInfo extends FilmStripOrGalleryView
{

    private static Log logger = LogFactory.getLog(GalleryViewFileDirectoryInfo.class);
    
    private WebElement fileDirectoryInfo;
    
    public GalleryViewFileDirectoryInfo(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(nodeRef, webElement, drone);

        FILENAME_IDENTIFIER = "div.alf-label";
        THUMBNAIL_TYPE = "div.alf-gallery-item-thumbnail>span";
        rowElementXPath = "../../../..";
        FILE_DESC_IDENTIFIER = "h3.filename+div.detail+div.detail>span";
        TAG_LINK_LOCATOR = By.cssSelector("div>div>span>span>a.tag-link");
        fileDirectoryInfo = webElement;
        resolveStaleness();
        
        if(isFolder())
        {
            THUMBNAIL = "div.alf-gallery-item-thumbnail>div+div+span a>img";  
        }
        else
        {
            THUMBNAIL = "div.alf-gallery-item-thumbnail>div+div+a>img";
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectThumbnail()
     */
    @Override
    public HtmlPage selectThumbnail()
    {
        drone.mouseOverOnElement(drone.findAndWait(By.xpath(String.format(".//div[@class='alf-label']/a[text()='%s']",getName()))));
        return super.selectThumbnail();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#getFileOrFolderHeight()
     */
    public double getFileOrFolderHeight()
    {
        try
        {
            String style = fileDirectoryInfo.getAttribute("style");
            return Double.valueOf(style.substring(style.indexOf(" ") + 1, style.indexOf("p")));
        }
        catch (NumberFormatException e)
        {
            logger.error("Unable to convert String into int:" + e.getMessage());
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the css :" + e.getMessage());
        }

        throw new PageOperationException("Error in finding the file size.");
    }
}