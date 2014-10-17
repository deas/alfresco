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
package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Aliaksei Boole
 */
public class MyProfileDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.xpath("//div[count(./div[@class='toolbar'])=0 and @class='dashlet']");

    /**
     * Constructor.
     *
     * @param drone
     */
    protected MyProfileDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
    }

    @Override
    public MyProfileDashlet render()
    {
        return render(maxPageLoadingTime);
    }

    @Override
    public MyProfileDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @Override
    public MyProfileDashlet render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(DASHLET_CONTAINER_PLACEHOLDER));
        return this;
    }
}
