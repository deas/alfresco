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
package org.alfresco.po.share;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailPage;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.ViewWorkflowPage;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * My tasks page object, holds all element of the html page relating to share's
 * my tasks page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class MyTasksPage extends SharePage
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private final By ACTIVE_LINK = By.cssSelector("a[rel='active']");
    private final By COMPLETED_LINK = By.cssSelector("a[rel='completed']");
    private final By TASKS_ROWS = By.cssSelector("div[id$='_default-tasks'] tbody.yui-dt-data tr");
    private final By TASK_VIEW_LINK = By.cssSelector("div.task-view-link");
    private final By WORKFLOW_VIEW_LINK = By.cssSelector("div.workflow-view-link>a");
    final By AVAILABLE_MY_TASKS_TABLE = By.cssSelector("div[id$='default-tasks']>table>tbody.yui-dt-data>tr");
    final By HEADER_TASKS_TABLE = By.cssSelector("td>div>h3");
    final By taskLink = By.cssSelector("a");
    final By INITIATOR = By.cssSelector("div.initiator span");
    private final By SUB_TITLE = By.cssSelector("h2[id$='_default-filterTitle']");
    private static final By START_WORKFLOW_BUTTON = By.cssSelector("button[id$='-startWorkflow-button-button']");

    RenderElement LOADING_ELEMENT = new RenderElement(By.cssSelector(".yui-dt-loading"), ElementState.INVISIBLE);
    RenderElement START_WORKFLOW_BUTTON_RENDER = RenderElement.getVisibleRenderElement(START_WORKFLOW_BUTTON);
    RenderElement CONTENT = new RenderElement(By.cssSelector("div[id$='_my-tasks']"), ElementState.PRESENT);

    /**
     * Constructor.
     * 
     * @param drone
     *            WebDriver to access page
     */
    public MyTasksPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyTasksPage render(RenderTime timer)
    {
        elementRender(timer, START_WORKFLOW_BUTTON_RENDER, LOADING_ELEMENT, CONTENT);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyTasksPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public MyTasksPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if people finder title is present on the page
     * 
     * @return true if exists
     */
    protected boolean isTitlePresent()
    {
        return isBrowserTitle("My Tasks");
    }

    /**
     * Method to select the task and click edit task button
     * 
     * @param searchParams, first item should be taskName, second is optional user first name
     */
    public EditTaskPage navigateToEditTaskPage(String... searchParams)
    {
        if (searchParams == null || searchParams.length < 1)
        {
            throw new UnsupportedOperationException("Task name can't be null or empty");
        }
        String taskName = searchParams[0];
        if(taskName == null)
        {
            throw new PageOperationException("Task name is required");
        }
        try
        {
            String xpathExpression = String.format("//h3[contains(.,'%s')]", taskName);
            WebElement row = drone.findAndWait(By.xpath(xpathExpression));
            clickEdit(row);
            return new EditTaskPage(drone);
        }
        catch (NoSuchElementException e)
        {
            logger.error("Not able to find the My Task Table.");
        }
        throw new PageException("Not able to find the site link element on this row.");
    }

    public void clickEdit(WebElement element, String ... attempt)
    {
        try
        {
            element.findElement(taskLink).click();
        }
        catch (StaleElementReferenceException e) 
        {
            if(attempt.length < 1)
            {
                clickEdit(element);
            }
            throw new PageOperationException("Unable to select edit task as its not clickable");
        }
    }
    /**
     * Clicks on Start workflow button.
     * 
     * @return {@link StartWorkFlowPage}
     */
    public StartWorkFlowPage selectStartWorkflowButton()
    {
        try
        {
            drone.findAndWait(By.cssSelector("button[id$='-startWorkflow-button-button']")).click();
            return new StartWorkFlowPage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Not able to find start work flow button" + e);
        }
        throw new PageException("Not able to find start work flow button");
    }

    /**
     * Method to get the page subtitle (Active Tasks, Completed Tasks etc)
     * @return
     */
    public String getSubTitle()
    {
        try
        {
            return drone.findAndWait(SUB_TITLE).getText();
        }
        catch (TimeoutException te)
        {
            throw new NoSuchElementException("Page Subtitle is not displayed");
        }
    }
    /**
     * Clicks on Active tasks link.
     * 
     * @return {@link MyTasksPage}
     */
    public MyTasksPage selectActiveTasks()
    {
        drone.findAndWait(ACTIVE_LINK).click();
        drone.waitUntilVisible(SUB_TITLE, "Active Tasks", TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return new MyTasksPage(drone);
    }

    /**
     * Clicks on Completed tasks link.
     * 
     * @return {@link MyTasksPage}
     */
    public MyTasksPage selectCompletedTasks()
    {
        drone.findAndWait(COMPLETED_LINK).click();
        drone.waitUntilVisible(SUB_TITLE, "Completed Tasks", TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return new MyTasksPage(drone);

    }

    /**
     * Clicks on view workflow for single task.
     * 
     * @return {@link MyTasksPage}
     */
    public TaskDetailPage selectViewTasks(String taskName)
    {
        performActionOnTask(taskName, TASK_VIEW_LINK);
        return new TaskDetailPage(drone);

    }

    /**
     * Clicks on view workflow for single task.
     * 
     * @return {@link MyTasksPage}
     */
    public ViewWorkflowPage selectViewWorkflow(String taskName)
    {
        performActionOnTask(taskName, WORKFLOW_VIEW_LINK);
        return new ViewWorkflowPage(drone);
    }

    /**
     * @param taskName
     */
    private void performActionOnTask(String taskName, By action)
    {
        if (taskName == null || action == null)
        {
            throw new UnsupportedOperationException("Both taskname and action should not be null");
        }
        WebElement taskRow = findTaskRow(taskName);
        if (taskRow != null)
        {
            taskRow.click();
            WebElement lastTD = taskRow.findElement(By.cssSelector("td:last-of-type"));
            getDrone().mouseOverOnElement(lastTD);
            lastTD.findElement(action).click();
        }
        else
        {
            throw new PageException("File not found");
        }
    }

    /**
     * Method to find given task row
     * @param taskName
     * @return
     */
    public WebElement findTaskRow(String taskName)
    {
        List<WebElement> taskRows = drone.findAll(TASKS_ROWS);
        if (null != taskRows && taskRows.size() > 0)
        {
            for (WebElement taskRow : taskRows)
            {
                if (taskName.equals(taskRow.findElement(By.cssSelector("h3 a")).getText()))
                    return taskRow;
            }
        }
        return null;
    }

    /**
     * Method to get the Task Details. If more than one task found, the first task details will be returned.
     * @param taskName
     * @return {@link TaskDetails}
     */
    public TaskDetails getTaskDetails(String taskName)
    {
        if(StringUtils.isEmpty(taskName))
        {
            throw new IllegalArgumentException("Workflow Name cannot be null");
        }

        WebElement taskRow = findTaskRow(taskName);

        TaskDetails taskDetails = new TaskDetails();
        try
        {

            if(taskRow != null)
            {
                taskDetails.setTaskName(taskRow.findElement(By.cssSelector("div.yui-dt-liner>h3>a")).getText());
                taskDetails.setDue(taskRow.findElement(By.cssSelector("div.due>span")).getText());
                taskDetails.setStartDate(taskRow.findElement(By.cssSelector("div[class^='started']>span")).getText());
                taskDetails.setStatus(taskRow.findElement(By.cssSelector("div.status>span")).getText());
                taskDetails.setType(taskRow.findElement(By.cssSelector("div.type>span")).getText());
                taskDetails.setDescription(taskRow.findElement(By.cssSelector("div.description>span")).getText());
                taskDetails.setStartedBy(taskRow.findElement(By.cssSelector("div.initiator>span")).getText());

                if(taskRow.findElement(By.cssSelector("div[class^='ended']>span")).isDisplayed())
                {
                    taskDetails.setEndDate(taskRow.findElement(By.cssSelector("div[class^='ended']>span")).getText());
                }
            }
            else
            {
                throw new PageOperationException("Unable to find task :" + taskName);
            }
        }
        catch (NoSuchElementException nse)
        {

        }
        return taskDetails;
    }

    /**
     * Method to check if a given task is displayed in MyTasksPage page
     * @param taskName
     * @return True if Task exists
     */
    public boolean isTaskPresent(String taskName)
    {
        return findTaskRow(taskName) != null;
    }
    
}
