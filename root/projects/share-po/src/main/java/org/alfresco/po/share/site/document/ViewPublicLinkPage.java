/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * The class represents the Public view Link page and displays the view details of document.
 * 
 * @author cbairaajoni
 */
public class ViewPublicLinkPage extends SharePage
{
    private static Log logger = LogFactory.getLog(ViewPublicLinkPage.class);
    @RenderWebElement
    private static final By alfrescoImageLocator = By.cssSelector(".quickshare-header-left>img");
    @RenderWebElement
    private static final By documentDetailsLinkLocator = By.cssSelector("div.quickshare-header-right>a.brand-button");
    @RenderWebElement
    private static final By documentNameLocator = By.cssSelector(".quickshare-node-header h1");
    @RenderWebElement
    private static final By documentPreviewLocator = By.cssSelector("div[id$='web-preview-previewer-div']");
    
    private static final String THIN_DARK_TITLE_ELEMENT = "h1.quickshare-node-header-info-title.thin.dark";

    /**
     * Constructor.
     */
    public ViewPublicLinkPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ViewPublicLinkPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ViewPublicLinkPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ViewPublicLinkPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify that public view page is displayed with document view.
     * 
     * @return boolean
     */
    public boolean isDocumentViewDisplayed()
    {
        try
        {
            return drone.find(documentPreviewLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
        }

        return false;
    }

    /**
     * Click View Link present on Share Link page.
     * 
     * @return HtmlPage
     */
    public HtmlPage clickOnDocumentDetailsButton()
    {
        try
        {
            drone.find(documentDetailsLinkLocator).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Unable to find documentDetailsLinkLocator css", ex);
        }

        throw new PageException("Unable to find documentDetailsLinkLocator css");
    }
    
    /**
     * Gets the page detail title.
     * 
     * @return String page detail page title
     */
    public String getContentTitle()
    {
        WebElement element = drone.findAndWait(By.cssSelector(THIN_DARK_TITLE_ELEMENT));
        return element.getText();
    }

}