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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

/**
 * @author cbairaajoni
 *
 */
public abstract class BaseAdvancedTinyMceOptionsPage extends SharePage
{
    private static Log logger = LogFactory.getLog(BaseAdvancedTinyMceOptionsPage.class);

    @RenderWebElement
    protected static By CANCEL_BUTTON = By.cssSelector("#cancel");
    
    @RenderWebElement
    protected static By INSERT_BUTTON = By.cssSelector("#insert");
    
    private String mainWindow = "";
    
    /**
     * Constructor.
     * @param mainWindow 
     */
    public BaseAdvancedTinyMceOptionsPage(WebDrone drone, String mainWindow)
    {
        super(drone);
        this.mainWindow = mainWindow;
    }

    /**
     * This method is used to Finds Insert button and clicks on it.
     * 
     * @return {@link ConfigureSiteNoticeDialogBoxPage}
     */
    public HtmlPage clickInsertOrUpdateButton()
    {
        try
        {
            drone.findAndWait(INSERT_BUTTON).click();
            drone.switchToWindow(mainWindow);
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException te)
        {
            logger.info("Unable to find the Insert button.", te);
            throw new PageOperationException("Unable to click the Insert Button.", te);
        }
    }

    /**
     * This method is used to Finds Cancel button and clicks on it.
     */
    public HtmlPage clickOnCancelButton()
    {
        try
        {
            drone.findAndWait(CANCEL_BUTTON).click();
            drone.switchToWindow(mainWindow);
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException te)
        {
            logger.info("Unable to find the CANCEL button.", te);
            throw new PageOperationException("Unable to click the CANCEL Button.", te);
        }
    }
}
