package org.alfresco.po.share.workflow;
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

import org.apache.commons.lang3.StringUtils;

/**
 * This enum hold the WorkFlow Description
 *
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum WorkFlowDescription
{

    CREATE_A_TASK_OR_START_A_REVIEW("Create a task or start a review on Alfresco Cloud"),
    ASSIGN_NEW_TASK_TO_YOUR_SELF_OR_COLLEAGUE("Assign a new task to yourself or a colleague"),
    HYBRID_ADHOC_TASK_PROCESS("Hybrid Adhoc Task Process"),
    HYBRID_REVIEW_AND_APPROVE_PROCESS("Hybrid Review And Approve Process"),
    ASSIGN_NEW_TASK_TO_SOMEONE_ON_THE_CLOUD("Assign a new task to someone on the Cloud"),
    REQUEST_DOCUMENT_APPROVAL("Request document approval from someone on the Cloud");

    private String description;

    WorkFlowDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    /**
     * Returns {@link org.alfresco.webdrone.share.workflow.WorkFlowDescription} based on given value.
     *
     * @param value
     * @return {@link org.alfresco.webdrone.share.workflow.WorkFlowDescription}
     */
    public static WorkFlowDescription getWorkFlowDescription(String value)
    {
        if(StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (WorkFlowDescription desc : WorkFlowDescription.values())
        {
            if (value.equals(desc.description))
            {
                return desc;
            }
        }
        throw new IllegalArgumentException("Invalid WorkFlowDescription Value : " + value);
    }
}


