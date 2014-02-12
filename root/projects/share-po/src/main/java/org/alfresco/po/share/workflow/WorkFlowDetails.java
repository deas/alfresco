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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Representation of Workflow details that can be used to verify Workflow details
 *
 * @author Ranjith Manyam
 * @since 1.7.1
 */
public class WorkFlowDetails
{

    private String workFlowName;
    private DateTime due;
    private DateTime startDate;
    private DateTime endDate;
    private WorkFlowType type;
    private String description;

    public String getWorkFlowName()
    {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName)
    {
        this.workFlowName = workFlowName;
    }

    public DateTime getDue()
    {
        return due;
    }

    public void setDue(String due)
    {
        try
        {
            this.due = DateTimeFormat.forPattern("dd MMM, yyyy").parseDateTime(due);
        }
        catch (IllegalArgumentException e)
        {
            this.due = null;
        }
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

    public WorkFlowType getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = WorkFlowType.getWorkflowTypeByTitle(type);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
