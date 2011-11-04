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

import org.alfresco.module.vti.metadata.dic.Permission;

/**
 * <p>Represents the Sharepoint List with its meta-information.</p>
 * 
 * @author AndreyAk
 *
 */
public class ListInfoBean implements Serializable
{
    private static final long serialVersionUID = 216886247863517038L;
    
    private String name;
    private boolean moderated;
    private List<Permission> permissionList;
    
    /**
     * @param name
     * @param moderated
     * @param permissionList
     */
    public ListInfoBean(String name, boolean moderated, List<Permission> permissionList)
    {
        super();
        this.name = name;
        this.moderated = moderated;
        this.permissionList = permissionList;
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
     * @return the moderated
     */
    public boolean isModerated()
    {
        return moderated;
    }
    /**
     * @param moderated the moderated to set
     */
    public void setModerated(boolean moderated)
    {
        this.moderated = moderated;
    }
    /**
     * @return the permissionList
     */
    public List<Permission> getPermissionList()
    {
        return permissionList;
    }
    /**
     * @param permissionList the permissionList to set
     */
    public void setPermissionList(List<Permission> permissionList)
    {
        this.permissionList = permissionList;
    }
        
}
