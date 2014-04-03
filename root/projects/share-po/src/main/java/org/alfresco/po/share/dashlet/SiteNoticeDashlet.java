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
package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

/**
 * Site Notice dashlet object, holds all element of the HTML relating to site Notice dashlet.
 * 
 * @author Chiran
 */
public class SiteNoticeDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(SiteNoticeDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.notice-dashlet");
    private static final By HELP_ICON = By.cssSelector("div.dashlet.notice-dashlet div.titleBarActionIcon.help");
    private static final By CONFIGURE_ICON = By.cssSelector("div.dashlet.notice-dashlet div.titleBarActionIcon.edit");
    private static final By DASHLET_HELP_BALLOON = By.cssSelector("div[style*='visible']>div.bd>div.balloon");
    private static final By DASHLET_HELP_BALLOON_TEXT = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.text");
    private static final By DASHLET_HELP_BALLOON_CLOSE_BUTTON = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.closeButton");
    private static final By DASHLET_CONTENT = By.cssSelector("div[id$='default-text']");
    private static final By DASHLET_TITLE = By.cssSelector("div[id$='default-title']");

    /**
     * Constructor.
     */
    protected SiteNoticeDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector("div.dashlet.notice-dashlet .yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized SiteNoticeDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(50L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    scrollDownToDashlet();
                    getFocus();
                    drone.find(DASHLET_CONTAINER_PLACEHOLDER);
                    drone.find(HELP_ICON);
                    drone.find(CONFIGURE_ICON);
                    drone.find(DASHLET_CONTENT);
                    drone.find(DASHLET_TITLE);
                    break;
                }
                catch (NoSuchElementException e)
                {

                }
                catch (StaleElementReferenceException ste)
                {
                    // DOM has changed therefore page should render once change is completed
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find site notice dashlet", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteNoticeDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteNoticeDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Finds whether help icon is displayed or not.
     * 
     * @return True if the help icon displayed else false.
     */
    public boolean isHelpIconDisplayed()
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            return drone.findAndWait(HELP_ICON).isDisplayed();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the help icon.", te);
            }
        }

        return false;
    }

    /**
     * Finds whether configure icon is displayed or not.
     * 
     * @return True if the configure icon displayed else false.
     */
    public boolean isConfigureIconDisplayed()
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            return drone.findAndWait(CONFIGURE_ICON).isDisplayed();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the configure icon.");
            }
        }

        return false;
    }

    /**
     * This method is used to Finds Help icon and clicks on it.
     */
    public void clickOnHelpIcon()
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            drone.findAndWait(HELP_ICON).click();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.info("Unable to find the help icon.");
            }
            throw new PageOperationException("Unable to click the Help icon", te);
        }
    }

    /**
     * This method is used to Finds Configure icon and clicks on it.
     * 
     * @return {@link ConfigureSiteNoticeDialogBoxPage}
     */
    public ConfigureSiteNoticeDialogBoxPage clickOnConfigureIcon()
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            drone.findAndWait(CONFIGURE_ICON).click();
            return new ConfigureSiteNoticeDialogBoxPage(drone);
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the help icon.", te);
            }
        }

        throw new PageOperationException("Unable to click the Configure icon");
    }

    /**
     * Finds whether help balloon is displayed on this page.
     * 
     * @return True if the balloon displayed else false.
     */
    public boolean isBalloonDisplayed()
    {
        try
        {
            return drone.findAndWait(DASHLET_HELP_BALLOON).isDisplayed();
        }
        catch (TimeoutException elementException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find the ballon", elementException);
            }
        }
        return false;
    }

    /**
     * This method gets the Help balloon messages and merge the message into string.
     * 
     * @return String
     */
    public String getHelpBalloonMessage()
    {
        try
        {
            return drone.findAndWait(DASHLET_HELP_BALLOON_TEXT).getText();
        }
        catch (TimeoutException elementException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find the help ballon text", elementException);
            }
        }

        throw new UnsupportedOperationException("Not able to find the help text");
    }

    /**
     * This method closes the Help balloon message.
     */
    public void closeHelpBallon()
    {
        try
        {
            drone.findAndWait(DASHLET_HELP_BALLOON_CLOSE_BUTTON).click();
        }
        catch (TimeoutException elementException)
        {
            throw new UnsupportedOperationException("Exceeded time to find the help ballon close button.", elementException);
        }
    }

    /**
     * This method gets the SiteNotice Dashlet title.
     * 
     * @return String
     */
    public String getTitle()
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            return drone.findAndWait(DASHLET_TITLE).getText();
        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("Exceeded time to find the title.", te);
        }
    }

    /**
     * This method gets the SiteNotice Dashlet content.
     * 
     * @return String
     */
    public String getContent()
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            return drone.findAndWait(DASHLET_CONTENT).getText();
        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("Exceeded time to find the title.", te);
        }
    }

    /**
     * This method gets the focus by placing mouse over on Site Content Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

}