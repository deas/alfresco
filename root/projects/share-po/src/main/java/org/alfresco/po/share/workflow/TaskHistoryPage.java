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
package org.alfresco.po.share.workflow;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;

/**
 * This page represents the Task History details page.
 * 
 * @author Chiran
 * @since 1.7.1
 */
public class TaskHistoryPage extends AbstractWorkFlowTaskDetailsPage
{
    private static final By MY_TASKS_LIST_LINK = By.cssSelector("span>a[href*='workflows|active']");

    private RenderElement myTasksListLink = getVisibleRenderElement(MY_TASKS_LIST_LINK);

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public TaskHistoryPage(WebDrone drone)
    {
        super(drone);
    }

    @Override
    public TaskHistoryPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getMenuTitle(), getWorkflowDetailsHeader(), getFormFieldsElements(), myTasksListLink);
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    @Override
    public TaskHistoryPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public TaskHistoryPage render(final long time)
    {
        return render(new RenderTime(time));
    }
}