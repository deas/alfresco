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
 * <p>Represents the Sharepoint member.</p>
 * 
 * @author AndreyAk
 *
 */
public class MemberBean implements Serializable
{

    private static final long serialVersionUID = -7459386981434580654L;
    
    private String id;
    private String name;
    private String loginName;
    private String email;
    private boolean isDomainGroup;
    
    /**
     * Default constructor
     */
    public MemberBean()
    {
    }

    /**
     * @param id
     * @param name
     * @param loginName
     * @param email
     * @param isDomainGroup
     */
    public MemberBean(String id, String name, String loginName, String email, boolean isDomainGroup)
    {
        super();
        this.id = id;
        this.name = name;
        this.loginName = loginName;
        this.email = email;
        this.isDomainGroup = isDomainGroup;
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
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the loginName
     */
    public String getLoginName()
    {
        return loginName;
    }

    /**
     * @param loginName the loginName to set
     */
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * @return the isDomainGroup
     */
    public boolean isDomainGroup()
    {
        return isDomainGroup;
    }

    /**
     * @param isDomainGroup the isDomainGroup to set
     */
    public void setDomainGroup(boolean isDomainGroup)
    {
        this.isDomainGroup = isDomainGroup;
    }
        
}
