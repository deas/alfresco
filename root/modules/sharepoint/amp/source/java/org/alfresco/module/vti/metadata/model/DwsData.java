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
import java.util.List;

/**
 * <p>The GetDwsData class is used to store general information about the Document 
 * Workspace site as well as its members, documents, links, and tasks.</p>
 * 
 * @author AndreyAk
 *
 */
public class DwsData implements Serializable
{

    private static final long serialVersionUID = 7388705900532472455L;   

    private String title;
    
    private String description;
    
    private String lastUpdate;
    
    private UserBean user;
    
    private List<MemberBean> members;
    
    private List<AssigneeBean> assignees;
    
    private List<TaskBean> tasksList;
    
    private List<DocumentBean> documentsList;
    
    private List<LinkBean> linksList;
    
    private boolean minimal;
    
    private String docLibUrl;



    /**
     * @param title
     * @param lastUpdate
     * @param user
     * @param members
     * @param assignees
     * @param tasksList
     * @param documentsList
     * @param linksList
     */
    public DwsData(String title, String lastUpdate, UserBean user, List<MemberBean> members, List<AssigneeBean> assignees, List<TaskBean> tasksList,
            List<DocumentBean> documentsList, List<LinkBean> linksList, boolean minimal)
    {
        super();
        this.title = title;
        this.lastUpdate = lastUpdate;
        this.user = user;
        this.members = members;
        this.assignees = assignees;
        this.tasksList = tasksList;
        this.documentsList = documentsList;
        this.linksList = linksList;
        this.minimal = minimal;
    }

    /**
     * Default constructor
     */
    public DwsData()
    {
        
    }
    
    /**
     * 
     * @return the assignees 
     */
    public List<AssigneeBean> getAssignees()
    {
        return assignees;
    }

    /**
     * 
     * @param assignees the assignees to set
     */
    public void setAssignees(List<AssigneeBean> assignees)
    {
        this.assignees = assignees;
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * <p>Sets the dws title.</p>
     * 
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * <p>Sets the dws description.</p>
     * 
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * 
     * @return the lastUpdate 
     */
    public String getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     * <p>Sets the last update date.</p>
     * 
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdate(String lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    /**
     * 
     * @return the user
     */
    public UserBean getUser()
    {
        return user;
    }

    /**
     * <p>Sets the current user.</p>
     * 
     * @param user the user to set
     */
    public void setUser(UserBean user)
    {
        this.user = user;
    }

    /**
     * 
     * @return the members 
     */
    public List<MemberBean> getMembers()
    {
        return members;
    }

    /**
     * <p>Sets the list of the dws members.</p>
     * 
     * @param members the members to set
     */
    public void setMembers(List<MemberBean> members)
    {
        this.members = members;
    }

        
    /**
     * @return the tasksList
     */
    public List<TaskBean> getTasksList()
    {
        return tasksList;
    }

    /**
     * <p>Sets the list of dws tasks.</p>
     * 
     * @param tasksList the tasksList to set
     */
    public void setTasksList(List<TaskBean> tasksList)
    {
        this.tasksList = tasksList;
    }

    /**
     * @return the documentsList
     */
    public List<DocumentBean> getDocumentsList()
    {
        return documentsList;
    }

    /**
     * <p>Sets the list of dws documents.</p>
     * 
     * @param documentsList the documentsList to set
     */
    public void setDocumentsList(List<DocumentBean> documentsList)
    {
        this.documentsList = documentsList;
    }

    /**
     * @return the linksList
     */
    public List<LinkBean> getLinksList()
    {
        return linksList;
    }

    /**
     * <p>Sets the list of dws links.</p>
     * 
     * @param linksList the linksList to set
     */
    public void setLinksList(List<LinkBean> linksList)
    {
        this.linksList = linksList;
    }
   
    /**
     * @return the minimal
     */
    public boolean isMinimal()
    {
        return minimal;
    }

    /**
     * @param minimal the minimal to set
     */
    public void setMinimal(boolean minimal)
    {
        this.minimal = minimal;
    }

    /**
     * <p>Sets the url of the document library for the dws.</p>
     * 
     * @param docLibUrl the docLibUrl to set 
     */
    public void setDocLibUrl(String docLibUrl)
    {
        this.docLibUrl = docLibUrl;
    }
    
    /**
     * 
     * @return the docLibUrl
     */
    public String getDocLibUrl()
    {
        return docLibUrl;
    }
}
