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
 * <p>Represent the Sharepoint Site user.</p>
 * 
 * @author PavelYur
 *
 */
public class UserBean implements Serializable
{

    private static final long serialVersionUID = 1404078639183811405L;
    
    private String id;
    private String name;
    private String displayName;
    private String loginName;
    private String email;    
    private String notes;
    private boolean isDomainGroup;
    private boolean isSiteAdmin;
 
    public UserBean()
    {
    }

    /**
     * @param id
     * @param name
     * @param loginName
     * @param email
     * @param isDomainGroup
     * @param isSiteAdmin
     */
    public UserBean(String id, String name, String loginName, String email, boolean isDomainGroup, boolean isSiteAdmin)
    {        
        this.id = id;
        this.name = name;
        this.loginName = loginName;
        this.email = email;
        this.notes = "";
        this.displayName = ""; 
        this.isDomainGroup = isDomainGroup;
        this.isSiteAdmin = isSiteAdmin;
    }
    
    public UserBean(String name, String loginName, String email, String notes)
    {
        this.id = "";
        this.name = name;
        this.loginName = loginName;
        this.email = email;
        this.notes = notes;
        this.displayName = ""; 
        this.isDomainGroup = false;
        this.isSiteAdmin = false;        
    }
    
    /**
     * 
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * 
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * 
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * 
     * @return the loginName
     */
    public String getLoginName()
    {
        return loginName;
    }

    /**
     * 
     * @param loginName the loginName to set
     */
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    /**
     * 
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * 
     * @param email the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * 
     * @return the isDomainGroup
     */
    public boolean isDomainGroup()
    {
        return isDomainGroup;
    }

    /**
     * 
     * @param isDomainGroup the isDomainGroup to set
     */
    public void setDomainGroup(boolean isDomainGroup)
    {
        this.isDomainGroup = isDomainGroup;
    }

    /**
     * 
     * @return the isSiteAdmin 
     */
    public boolean isSiteAdmin()
    {
        return isSiteAdmin;
    }

    /**
     * 
     * @param isSiteAdmin the isSiteAdmin to set
     */
    public void setSiteAdmin(boolean isSiteAdmin)
    {
        this.isSiteAdmin = isSiteAdmin;
    }
    
    /**
     * 
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * 
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * 
     * @return the notes
     */
    public String getNotes()
    {
        return notes;
    }

    /**
     * 
     * @param notes the notes to set
     */
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

}
