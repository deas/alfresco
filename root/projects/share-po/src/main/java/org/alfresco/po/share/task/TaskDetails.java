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

import org.alfresco.po.share.workflow.TaskDetailsType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Representation of Task details that can be used to verify Task details
 *
 * @author Ranjith Manyam
 * @since 1.7.1
 */
public class TaskDetails
{

    private String taskName;
    private DateTime due;
    private String dueDateString;
    private DateTime startDate;
    private DateTime endDate;
    private String status;
    private TaskDetailsType type;
    private String description;
    private String startedBy;

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public DateTime getDue()
    {
        return due;
    }

    public void setDue(String due)
    {
        try
        {
            this.due = DateTimeFormat.forPattern("dd MMMMM, yyyy").parseDateTime(due);
        }
        catch (IllegalArgumentException ie)
        {
            this.due = null;
        }
    }

    public String getDueDateString()
    {
        return dueDateString;
    }

    public void setDueDateString(String due)
    {
        this.dueDateString = due;
    }

    public DateTime getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = DateTimeFormat.forPattern("dd MMMMM, yyyy").parseDateTime(startDate);
    }

    public DateTime getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = DateTimeFormat.forPattern("dd MMMMM, yyyy").parseDateTime(endDate);
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public TaskDetailsType getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = TaskDetailsType.getTaskDetailsType(type);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getStartedBy()
    {
        return startedBy;
    }

    public void setStartedBy(String startedBy)
    {
        this.startedBy = startedBy;
    }
}
