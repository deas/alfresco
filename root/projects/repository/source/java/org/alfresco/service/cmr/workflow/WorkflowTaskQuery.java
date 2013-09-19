/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.service.cmr.workflow;

import java.util.Map;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.namespace.QName;


/**
 * Workflow Task Query
 * 
 * Provides support for setting predicates and order by.
 * 
 * @author davidc
 */
@AlfrescoPublicApi
public class WorkflowTaskQuery
{
    // Engine Id
    private String engineId = null;
    
    // task predicates
    private String taskId;
    private WorkflowTaskState taskState = WorkflowTaskState.IN_PROGRESS;
    private QName taskName;
    private String actorId;    
    private Map<QName, Object> taskCustomProps; 
    
    // process predicates
    private String processId;
    private QName processName;
    private String workflowDefinitionName;
    private Boolean active = Boolean.TRUE;
    private Map<QName, Object> processCustomProps;
    
    // order by
    private OrderBy[] orderBy;
    
    // result set size
    private int limit = -1;
    
    /**
     * Order By Columns
     */
    public enum OrderBy
    {
        TaskId_Asc,
        TaskId_Desc,
        TaskCreated_Asc,
        TaskCreated_Desc,
        TaskDue_Asc,
        TaskDue_Desc,
        TaskName_Asc,
        TaskName_Desc,
        TaskActor_Asc,
        TaskActor_Desc,
        TaskState_Asc,
        TaskState_Desc;
    }
    
    
    /**
     * @param orderBy
     */
    public void setOrderBy(OrderBy[] orderBy)
    {
        this.orderBy = orderBy; 
    }
    
    /**
     * @return
     */
    public OrderBy[] getOrderBy()
    {
        return orderBy;
    }
    
    /**
     * @return
     */
    public String getTaskId()
    {
        return taskId;
    }
    
    /** 
     * @param taskId
     */
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    
    /**
     * @return
     */
    public Map<QName, Object> getTaskCustomProps()
    {
        return taskCustomProps;
    }

    /**
     * @param taskCustomProps
     */
    public void setTaskCustomProps(Map<QName, Object> taskCustomProps)
    {
        this.taskCustomProps = taskCustomProps;
    }

    /**
     * @return
     */
    public WorkflowTaskState getTaskState()
    {
        return taskState;
    }
    
    /**
     * @param taskState
     */
    public void setTaskState(WorkflowTaskState taskState)
    {
        this.taskState = taskState;
    }
    
    /**
     * @return
     */
    public QName getTaskName()
    {
        return taskName;
    }
    
    /**
     * @param taskName
     */
    public void setTaskName(QName taskName)
    {
        this.taskName = taskName;
    }
    
    /**
     * @return
     */
    public String getActorId()
    {
        return actorId;
    }
    
    /**
     * @param actorId
     */
    public void setActorId(String actorId)
    {
        this.actorId = actorId;
    }
    
    /**
     * @return
     */
    public String getProcessId()
    {
        return processId;
    }

    /**
     * Filters ont he {@link WorkflowInstance} Id.
     * @param processId
     */
    public void setProcessId(String processId)
    {
        this.processId = processId;
    }
    
    /**
     * @return
     */
    public QName getProcessName()
    {
        return processName;
    }

    /**
     * Use {@link WorkflowTaskQuery#setWorkflowDefinitionName(String)} instead.
     * Filters on the {@link WorkflowDefinition} name. When using Activiti,
     * the method {@link #setWorkflowDefinitionName(String)} should be used
     * instead of this method.
     * 
     * @param processName
     */
    @Deprecated
    public void setProcessName(QName processName)
    {
        this.processName = processName;
    }
    
    /**
     * @return 
     */
    public String getWorkflowDefinitionName()
    {
        return workflowDefinitionName;
    }
    
    /**
     * Filters on the {@link WorkflowDefinition} name.
     * @param workflowDefinitionName
     */
    public void setWorkflowDefinitionName(String workflowDefinitionName)
    {
        this.workflowDefinitionName = workflowDefinitionName;
    }
    
    /**
     * @return
     */
    public Boolean isActive()
    {
        return active;
    }
    
    /**
     * @param active
     */
    public void setActive(Boolean active)
    {
        this.active = active;
    }

    /**
     * @return
     */
    public Map<QName, Object> getProcessCustomProps()
    {
        return processCustomProps;
    }

    /**
     * @param processCustomProps
     */
    public void setProcessCustomProps(Map<QName, Object> processCustomProps)
    {
        this.processCustomProps = processCustomProps;
    }

    public int getLimit()
    {
        return this.limit;
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    /**
     * @param engineId the engineId to set
     */
    public void setEngineId(String engineId)
    {
        this.engineId = engineId;
    }
    
    /**
     * @return the engineId
     */
    public String getEngineId()
    {
        return engineId;
    }
}
