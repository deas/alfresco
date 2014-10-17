/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.alfresco;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Created by olga.lokhach on 6/17/2014.
 */
public class AbstractAdminConsole extends SharePage
{
    private final By INPUT_FIELD = By.xpath("//input[@id='searchForm:command']");
    private final By SUBMIT_BUTTON = By.xpath("//input[@id='searchForm:submitCommand']");
    private final By CLOSE_BUTTON = By.cssSelector("input[id$='Admin-console-title:_idJsp1']");

    public AbstractAdminConsole(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractAdminConsole render(RenderTime renderTime)
    {
        elementRender(renderTime,
            getVisibleRenderElement(INPUT_FIELD),
            getVisibleRenderElement(SUBMIT_BUTTON),
            getVisibleRenderElement(CLOSE_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractAdminConsole render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractAdminConsole render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for click Close Button
     *
     * @param
     * @return
     */
    public void clickClose()
    {
        drone.findAndWait(CLOSE_BUTTON).click();

    }

    /**
     * Method for send commands
     *
     * @param request
     * @return
     */
    public void sendCommands(String request)
    {
        drone.findAndWait(INPUT_FIELD).clear();
        drone.findAndWait(INPUT_FIELD).sendKeys(String.format("%s", request));
        drone.findAndWait(SUBMIT_BUTTON).click();
    }

    public String findText()
    {
        return drone.findAndWait(By.xpath("//*[@id='result']")).getText();

    }

}
