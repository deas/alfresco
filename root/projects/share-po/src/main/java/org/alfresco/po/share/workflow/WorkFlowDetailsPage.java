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
package org.alfresco.po.share.workflow;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * WorkFlow Details Page.
 * 
 * @author Ranjith Manyam
 * @since  1.7.1
 */
public class WorkFlowDetailsPage extends SharePage
{
    private static final Logger logger = Logger.getLogger(WorkFlowDetailsPage.class);

    private final By MENU_TITLE = By.cssSelector(".alf-menu-title-text");
    private final By WORKFLOW_DETAILS_HEADER = By.cssSelector("div.workflow-details-header");
    private final By CANCEL_BUTTON = By.cssSelector("button[id$='_default-cancel-button']");
    private final By ASSIGNEE = By.cssSelector("div[id$='_hwf_assignment-cntrl']>span[id$='_assignment-cntrl-currentValueDisplay']>div");

    private RenderElement menuTitle = getVisibleRenderElement(MENU_TITLE);
    private RenderElement workflowDetailsHeader = getVisibleRenderElement(WORKFLOW_DETAILS_HEADER);

    public WorkFlowDetailsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WorkFlowDetailsPage render(RenderTime timer)
    {
        elementRender(timer, menuTitle, workflowDetailsHeader);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WorkFlowDetailsPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WorkFlowDetailsPage render()
    {
        return render(maxPageLoadingTime);
    }

    /**
     * Verify if WorkFlow Details title is present on the page
     *
     * @return true if exists
     */
    public boolean isTitlePresent()
    {
        return isBrowserTitle("Workflow Details");
    }

    /**
     * Method to select Edit Task link
     * @return
     */
    public EditTaskPage selectEditTask()
    {
        try
        {
            drone.findAndWait(By.cssSelector("a.task-edit")).click();
            return new EditTaskPage(drone);
        }
        catch (TimeoutException exception)
        {
        	logger.error("Not able find the Task Edit" + exception.getMessage());
            throw new PageException("Edit Task link doesn't exist on Workflow Details page");
        }
    }

    /**
     * Method to get workflow status
     * @return
     */
    public String getWorkFlowStatus()
    {
        try
        {
            return drone.findAndWait(By.cssSelector("span[id$='_default-status']")).getText();
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Unable to find WorkFlow Status Element");
        }
    }

    /**
     * Method to click Cancel button from Workflow Details page
     * // TODO - Should return to MyWorkFlowsPage rather than MyTasksPage
     * @return
     */
    public MyTasksPage selectCancelWorkFlow()
    {
        try
        {
            drone.find(CANCEL_BUTTON).click();
            drone.waitForElement(By.cssSelector("#prompt"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            List<WebElement> buttons = drone.findAll(By.cssSelector("div#prompt>div.ft>span.button-group>span.yui-button>span.first-child>button"));
            for(WebElement button: buttons)
            {
                if(button.getText().equals("Yes"))
                {
                    button.click();
                    break;
                }
            }
            return new MyTasksPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Unable to find Cancel button ");
            }
            throw new PageException("Unable to find Cancel button");
        }
    }

    /**
     * Method to get Assignee
     * @return Assignee Full Name and e-mail
     */
    public String getAssignee()
    {
        try
        {
            return drone.findAndWait(ASSIGNEE).getText();
        }
        catch (TimeoutException toe)
        {
            logger.error("Unable to find Assignee with exception" + toe.getMessage());
        }
        return "";
    }
}