/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.share;

import java.util.List;

import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Charu -To confirm remove user/ not to remove user from any group this page is used
 */

@SuppressWarnings("unused")
public class RemoveUserFromGroupPage extends SharePage
{
    private static final String CONFIRM_MESSAGE = "div[class='yui-module yui-overlay yui-panel' ]>div[class='bd']";
    private static Log logger = LogFactory.getLog(CopyOrMoveContentPage.class);

    public enum Action
    {
        Yes, No
    }

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public RemoveUserFromGroupPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RemoveUserFromGroupPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RemoveUserFromGroupPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RemoveUserFromGroupPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Action of selecting "Yes" or "No" on Remove User from group page.
     * 
     * @param - Enum action - Yes/no
     * @return - HtmlPage
     */
    public HtmlPage selectAction(Action action)
    {
        try
        {
            List<WebElement> buttons = drone.findAll(By.cssSelector(".button-group span span button"));
            for (WebElement button : buttons)
            {
                if (action.name().equals(button.getText()))
                {
                    button.click();
                    canResume();
                    return FactorySharePage.resolvePage(drone);
                }

            }

        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("not present in this page", nse);
        }
        throw new PageOperationException("not present in this page");
    }

    /**
     * Get the Title in Remove user pop up window
     * 
     * @return - String
     */

    public String getTitle()
    {
        try
        {
            return drone.findAndWait(By.cssSelector("div[class='yui-module yui-overlay yui-panel']>div[id='prompt_h']")).getText();
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: Remove user from Goup", toe);
        }

    }

}