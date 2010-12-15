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
package org.alfresco.module.vti.metadata.model;

import java.io.Serializable;

/**
 * <p>Represents the Sharepoint task.</p>
 * @author AndreyAk
 *
 */
public class TaskBean implements Serializable
{

    private static final long serialVersionUID = -7162094818151530500L;
    
    private String title;
    private String assignedTo;
    private String status;
    private String priority;
    private String dueDate;
    private String body;
    private String created;
    private String author;
    private String modified;
    private String editor;
    private String owshiddenversion;
    private String id;
    

    /**
     * @param title
     * @param assignedTo
     * @param status
     * @param priority
     * @param dueDate
     * @param body
     * @param created
     * @param author
     * @param modified
     * @param editor
     * @param owshiddenversion
     * @param id
     */
    public TaskBean(String title, String assignedTo, String status, String priority, String dueDate, String body, String created, String author, String modified, String editor,
            String owshiddenversion, String id)
    {
        super();
        this.title = title;
        this.assignedTo = assignedTo;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.body = body;
        this.created = created;
        this.author = author;
        this.modified = modified;
        this.editor = editor;
        this.owshiddenversion = owshiddenversion;
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the assignedTo
     */
    public String getAssignedTo()
    {
        return assignedTo;
    }

    /**
     * @param assignedTo the assignedTo to set
     */
    public void setAssignedTo(String assignedTo)
    {
        this.assignedTo = assignedTo;
    }

    /**
     * @return the status
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * @return the priority
     */
    public String getPriority()
    {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    /**
     * @return the dueDate
     */
    public String getDueDate()
    {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(String dueDate)
    {
        this.dueDate = dueDate;
    }

    /**
     * @return the modified
     */
    public String getModified()
    {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(String modified)
    {
        this.modified = modified;
    }

    /**
     * @return the editor
     */
    public String getEditor()
    {
        return editor;
    }

    /**
     * @param editor the editor to set
     */
    public void setEditor(String editor)
    {
        this.editor = editor;
    }

    /**
     * @return the owshiddenversion
     */
    public String getOwshiddenversion()
    {
        return owshiddenversion;
    }

    /**
     * @param owshiddenversion the owshiddenversion to set
     */
    public void setOwshiddenversion(String owshiddenversion)
    {
        this.owshiddenversion = owshiddenversion;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    

    /**
     * @return the body
     */
    public String getBody()
    {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * @return the created
     */
    public String getCreated()
    {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(String created)
    {
        this.created = created;
    }

    /**
     * @return the author
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author)
    {
        this.author = author;
    }
        
}
