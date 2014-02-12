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
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Task Details Page.
 * 
 * @author Shan Nagarajan
 * @since  1.7.1
 */
public class TaskDetailPage extends SharePage
{
    private final By MENU_TITLE = By.cssSelector(".alf-menu-title-text");
    private final By VIEW_LABEL = By.cssSelector(".viewmode-label");
    private final By VIEW_VALUE = By.cssSelector(".viewmode-value");
    private final By WORKFLOW_DETAILS = By.cssSelector(".links>a");
    private final By MY_TASK_DETAILS = By.cssSelector(".backLink>a");
    private RenderElement menuTitle = getVisibleRenderElement(MENU_TITLE);
    private RenderElement myTaskDetails = getVisibleRenderElement(MY_TASK_DETAILS);
    private RenderElement workflowDetails = getVisibleRenderElement(WORKFLOW_DETAILS);
    private final String COMMENT_LABEL = "Comment:";
    
    public TaskDetailPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TaskDetailPage render(RenderTime timer)
    {
       elementRender(timer, menuTitle, myTaskDetails, workflowDetails);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TaskDetailPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TaskDetailPage render()
    {
        return render(maxPageLoadingTime);
    }
    
    /**
     * 
     * @return
     */
    public String getComment()
    {
        int commentIndex = -1;
        String comment = "";
        List<WebElement> labels = drone.findAndWaitForElements(VIEW_LABEL);
        int index = 0;
        for (WebElement label : labels)
        {
            if(COMMENT_LABEL.equalsIgnoreCase(label.getText()))
            {
                commentIndex = index;
                break;
            }
            index++;
        }
        List<WebElement> values = drone.findAndWaitForElements(VIEW_VALUE);
        if(values != null && values.size() >= commentIndex && values.get(commentIndex).getText() != null)
        {
            comment = values.get(commentIndex).getText();
        }
        return comment;
    }
    
    /**
     * Mimics the action clicking My Tasks List hyper link. 
     * @return {@link MyTasksPage}
     */
    public MyTasksPage clickMyTasksLsit()
    {
        drone.find(MY_TASK_DETAILS).click();
        return new MyTasksPage(drone);
    }

}