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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * The class represents the Share Link page and holds the links to Public Link and share links with Facebook, Twitter, Gmail, g+.
 * 
 * @author cbairaajoni
 * 
 */
public class ShareLinkPage extends SharePage
{
    private static Log logger = LogFactory.getLog(ShareLinkPage.class);
    private static final By shareLinkDivLocator = By.cssSelector("div.visible div.bd");
    private static final By viewLinkLocator = By.cssSelector("div.visible span>a.quickshare-action-view");
    private static final By unShareLinkLocator = By.cssSelector("div.visible span>a.quickshare-action-unshare");
    private static final By emailLinkLocator = By.cssSelector("div.visible a.linkshare-action-email");
    private static final By facebookLinkLocator = By.cssSelector("div.visible a.linkshare-action-facebook");
    private static final By twitterLinkLocator = By.cssSelector("div.visible a.linkshare-action-twitter");
    private static final By googlePlusLinkLocator = By.cssSelector("div.visible a.linkshare-action-google-plus");
   
    /**
     * Constructor.
     */
    public ShareLinkPage(WebDrone drone)
    {
        super(drone);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ShareLinkPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(shareLinkDivLocator), getVisibleRenderElement(viewLinkLocator),
                getVisibleRenderElement(unShareLinkLocator), getVisibleRenderElement(emailLinkLocator), getVisibleRenderElement(facebookLinkLocator),
                getVisibleRenderElement(twitterLinkLocator), getVisibleRenderElement(googlePlusLinkLocator));
        
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareLinkPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareLinkPage render(final long time)
    {
        return render(new RenderTime(time));
    }
  
    /**
     * Verify that UnshareLink is displayed.
     * 
     * @return boolean
     */
    public boolean isViewLinkPresent()
    {
        try
        {
            return drone.find(viewLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Public View link is not present.");
        }

      return false;
    }
    
    /**
     * Click View Link present on Share Link page.
     * 
     * @return HtmlPage
     */
    public HtmlPage clickViewButton()
    {
        try
        {
            drone.findAndWait(viewLinkLocator).click();

            return new ViewPublicLinkPage(drone);
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the view link element", ex);
        }

        throw new PageException("Unable to find View link");
    }

    /**
     * Verify that UnshareLink is displayed.
     * 
     * @return boolean
     */
    public boolean isUnShareLinkPresent()
    {
        try
        {
            return drone.find(unShareLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Public unshare link is not present.");
        }

      return false;
    }

    /**
     * Click View Link present on Share Link page.
     * 
     * @return HtmlPage
     */
    public HtmlPage clickOnUnShareButton()
    {
        try
        {
            drone.findAndWait(unShareLinkLocator).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the unshare linkelement", ex);
        }

        throw new PageException("Unable to find Unshare link");
    }
    
    /**
     * Verify that Email link is displayed.
     * 
     * @return boolean
     */
    public boolean isEmailLinkPresent()
    {
        try
        {
            return drone.find(emailLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Email link is not present.");
        }

      return false;
    }

    /**
     * Verify that Facebook link is displayed.
     * 
     * @return boolean
     */
    public boolean isFaceBookLinkPresent()
    {
        try
        {
            return drone.find(facebookLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Facebook link is not present.");
        }

      return false;
    }

    /**
     * Click Facebook Link present on Share Link page.
     */
    public void clickFaceBookLink()
    {
        try
        {
            drone.find(facebookLinkLocator).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Facebook link element", ex);
            throw new PageException("Unable to find Facebook link");
        }
    }

    /**
     * Verify that Twitter link is displayed.
     * 
     * @return boolean
     */
    public boolean isTwitterLinkPresent()
    {
        try
        {
            return drone.find(twitterLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Twitter link is not present.");
        }

      return false;
    }

    /**
     * Click Twitter Link present on Share Link page.
     */
    public void clickTwitterLink()
    {
        try
        {
            drone.find(twitterLinkLocator).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Twitter link element", ex);
            throw new PageException("Unable to find Twitter link");
        }
    }

    /**
     * Verify that Google+ link is displayed.
     * 
     * @return boolean
     */
    public boolean isGooglePlusLinkPresent()
    {
        try
        {
            return drone.find(googlePlusLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Google+ link is not present.");
        }

      return false;
    }

    /**
     * Click Google+ Link present on Share Link page.
     */
    public void clickGooglePlusLink()
    {
        try
        {
            drone.find(googlePlusLinkLocator).click();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the Google+ link element", ex);
            throw new PageException("Unable to find Google+ link");
        }
    }
}