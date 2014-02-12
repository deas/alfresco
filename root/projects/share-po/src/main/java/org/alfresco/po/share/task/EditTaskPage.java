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
package org.alfresco.po.share.task;

import java.util.concurrent.TimeUnit;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * This class represents the Edit task page which can be navigated from My Tasks
 * page > Edit Task.
 * 
 * @author Abhijeet Bharade
 * @since v1.6.2
 */
public class EditTaskPage extends SharePage
{

    private final String SAVE_BUTTON = "button[id$='default-form-submit-button']";
    private final String REASSIGN_BUTTON = "button[id$='default-reassign-button']";
    private final String TASK_DONE_BUTTON = "button[id$='default_prop_transitions-Next-button']";
    private final String APPROVE_BUTTON = "button[id$='reviewOutcome-approve-button']";
    private final String REJECT_BUTTON = "button[id$='reviewOutcome-reject-button']";
    private final String TASK_STATUS = "select[id$='default_prop_bpm_status']";
    private final String COMMENT_TEXTAREA = "textarea[id$='_comment']";

    /**
     * @param drone
     */
    public EditTaskPage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.share.HtmlPage#render(org.alfresco.po.share.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public EditTaskPage render(RenderTime timer) throws PageException
    {
        try
        {
            while (true)
            {
                try
                {
                    timer.start();
                    drone.find(By.cssSelector(SAVE_BUTTON));
                    break;

                }
                catch (NoSuchElementException e)
                {
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditTaskPage render() throws PageException
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditTaskPage render(final long time) throws PageException
    {
        return render(new RenderTime(time));
    }

    /**
     * Selects the Reassign button.
     * 
     * @return an instance of {@link SelectAssigneePage}
     */
    public SelectAssigneePage selectReassignButton()
    {
        if (alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("Operation valid only for enterprise versions.");
        }
        drone.find(By.cssSelector(REASSIGN_BUTTON)).click();
        return new SelectAssigneePage(drone);
    }

    /**
     * Selects the Status drop down list.
     */
    public HtmlPage selectStatusDropDown(TaskStatus status)
    {

        WebElement statusSelectDropDown = drone.find(By.cssSelector(TASK_STATUS));
        statusSelectDropDown.click();
        Select select = new Select(statusSelectDropDown);
        select.selectByValue(status.getTaskName());
        return new EditTaskPage(drone);
    }

    /**
     * Selects the Status drop down list.
     * 
     * @return {@link TaskStatus} - status selected from dropdown.
     */
    public TaskStatus getSelectedStatusFromDropDown()
    {
        Select comboBox = new Select(drone.find(By.cssSelector(TASK_STATUS)));
        String selectedTask = comboBox.getFirstSelectedOption().getText();
        return TaskStatus.getTaskFromString(selectedTask);
    }

    /**
     * Selects the Task done button.
     * 
     * @return {@link MyTasksPage} - instance of my task page.
     */
    public HtmlPage selectTaskDoneButton()
    {
        WebElement taskDoneButton = drone.find(By.cssSelector(TASK_DONE_BUTTON));
        String id = taskDoneButton.getAttribute("id");
        taskDoneButton.click();
        drone.waitUntilElementDeletedFromDom(By.id(id), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Selects the Reject button.
     * 
     * @return {@link MyTasksPage} - instance of my task page.
     */
    public MyTasksPage selectRejectButton()
    {
        drone.findAndWait(By.cssSelector(REJECT_BUTTON)).click();
        return new MyTasksPage(drone);
    }

    /**
     * Selects the Status drop down list.
     * 
     * @return {@link MyTasksPage} - instance of my task page.
     */
    public MyTasksPage selectApproveButton()
    {
        drone.findAndWait(By.cssSelector(APPROVE_BUTTON)).click();
        return new MyTasksPage(drone);
    }

    /**
     * Selects the Save button
     * 
     * @return {@link MyTasksPage} - instance of my task page.
     */
    public MyTasksPage selectSaveButton()
    {
        drone.findAndWait(By.cssSelector(SAVE_BUTTON)).click();
        return new MyTasksPage(drone);
    }

    /**
     * Enter comment
     * 
     * @param comment
     */
    public void enterComment(String comment)
    {
        try
        {
            WebElement commentBox = drone.find(By.cssSelector(COMMENT_TEXTAREA));
            commentBox.clear();
            commentBox.sendKeys(comment);
        }
        catch (NoSuchElementException e)
        {
            throw new UnsupportedOperationException("Comment cannot be added for this task");
        }
    }

    /**
     * TODO - Dont know whether its absence from 4.2 is expected behaviour.
     * Selects comment box and enters comment into it.
     * 
     * public void enterComment(String comment) { if (dojoSupport) { throw new
     * UnsupportedOperationException
     * ("Operation invalid for enterprise versions 4.2."); } WebElement
     * commentBox = drone.find(By.cssSelector(COMMENT_BOX));
     * commentBox.sendKeys(comment); }
     * 
     * public String readCommentFromCommentBox() { if (dojoSupport) { throw new
     * UnsupportedOperationException
     * ("Operation invalid for enterprise versions 4.2."); } WebElement
     * commentBox = drone.find(By.cssSelector(COMMENT_BOX)); return
     * commentBox.getText(); }
     */

}
