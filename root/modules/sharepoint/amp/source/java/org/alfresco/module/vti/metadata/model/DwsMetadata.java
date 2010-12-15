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
import java.util.Set;

import org.alfresco.module.vti.metadata.dic.Permission;
import org.alfresco.module.vti.metadata.dic.WorkspaceType;

/**
 * Represents information about a Document Workspace site and the lists it contains
 * 
 * <p>DwsMetaData class contains more information about a Document Workspace site 
 * than the DwsData class. When Minimal is <i>False</i>, the DwsMetaData contains 
 * additional information about the schemas, lists, documents, links, and tasks 
 * lists of a Document Workspace site.</p>
 * 
 * @author AndreyAk
 *
 */
public class DwsMetadata implements Serializable
{
    private static final long serialVersionUID = 7567776221578867757L;
    
    private String subscribeUrl;
    //meeting instance, if applicable
    private String mtgInstance ;
    private String settingsUrl;
    private String permsUrl;
    private String userInfoUrl;
    private Set<String> roles;
    private List<SchemaBean> schemaItems;
    private List<ListInfoBean> listInfoItems;
    private List<Permission> permissions;
    private boolean hasUniquePerm;
    private WorkspaceType workspaceType;
    private boolean isADMode;
    private String docUrl;
    private boolean minimal;
    private DwsData dwsData ;
   
    /**
     * Default constructor
     */
    public DwsMetadata()
    {
    }
    
    /**
     * @param subscribeUrl
     * @param mtgInstance
     * @param settingsUrl
     * @param permsUrl
     * @param userInfoUrl
     * @param roles
     * @param schemaItems
     * @param listInfoItems
     * @param permissions
     * @param hasUniquePerm
     * @param workspaceType
     * @param isADMode
     * @param docUrl
     * @param minimal
     * @param dwsData
     */
    public DwsMetadata(String subscribeUrl, String mtgInstance, String settingsUrl, String permsUrl, String userInfoUrl, Set<String> roles, List<SchemaBean> schemaItems, List<ListInfoBean> listInfoItems,
            List<Permission> permissions, boolean hasUniquePerm, WorkspaceType workspaceType, boolean isADMode, String docUrl, boolean minimal, DwsData dwsData)
    {
        super();
        this.subscribeUrl = subscribeUrl;
        this.mtgInstance = mtgInstance;
        this.settingsUrl = settingsUrl;
        this.permsUrl = permsUrl;
        this.userInfoUrl = userInfoUrl;
        this.roles = roles;
        this.schemaItems = schemaItems;
        this.listInfoItems = listInfoItems;
        this.permissions = permissions;
        this.hasUniquePerm = hasUniquePerm;
        this.workspaceType = workspaceType;
        this.isADMode = isADMode;
        this.docUrl = docUrl;
        this.minimal = minimal;
        this.dwsData = dwsData;
    }

    /**
     * @return the subscribeUrl
     */
    public String getSubscribeUrl()
    {
        return subscribeUrl;
    }

    /**
     * @param subscribeUrl the subscribeUrl to set
     */
    public void setSubscribeUrl(String subscribeUrl)
    {
        this.subscribeUrl = subscribeUrl;
    }

    /**
     * @return the mtgInstance
     */
    public String getMtgInstance()
    {
        return mtgInstance;
    }

    /**
     * @param mtgInstance the mtgInstance to set
     */
    public void setMtgInstance(String mtgInstance)
    {
        this.mtgInstance = mtgInstance;
    }

    /**
     * @return the permsUrl
     */
    public String getPermsUrl()
    {
        return permsUrl;
    }

    /**
     * @param permsUrl the permsUrl to set
     */
    public void setPermsUrl(String permsUrl)
    {
        this.permsUrl = permsUrl;
    }

    /**
     * @return the userInfoUrl
     */
    public String getUserInfoUrl()
    {
        return userInfoUrl;
    }

    /**
     * @param userInfoUrl the userInfoUrl to set
     */
    public void setUserInfoUrl(String userInfoUrl)
    {
        this.userInfoUrl = userInfoUrl;
    }

    /**
     * @return the roles
     */
    public Set<String> getRoles()
    {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(Set<String> roles)
    {
        this.roles = roles;
    }

    /**
     * @return the settingsUrl
     */
    public String getSettingsUrl()
    {
        return settingsUrl;
    }

    /**
     * @param settingsUrl the settingsUrl to set
     */
    public void setSettingsUrl(String settingsUrl)
    {
        this.settingsUrl = settingsUrl;
    }

    /**
     * @return the schemaItems
     */
    public List<SchemaBean> getSchemaItems()
    {
        return schemaItems;
    }

    /**
     * @param schemaItems the schemaItems to set
     */
    public void setSchemaItems(List<SchemaBean> schemaItems)
    {
        this.schemaItems = schemaItems;
    }

    /**
     * @return the listInfoItems
     */
    public List<ListInfoBean> getListInfoItems()
    {
        return listInfoItems;
    }

    /**
     * @param listInfoItems the listInfoItems to set
     */
    public void setListInfoItems(List<ListInfoBean> listInfoItems)
    {
        this.listInfoItems = listInfoItems;
    }

    /**
     * @return the permissions
     */
    public List<Permission> getPermissions()
    {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(List<Permission> permissions)
    {
        this.permissions = permissions;
    }

    /**
     * @return the hasUniquePerm
     */
    public boolean isHasUniquePerm()
    {
        return hasUniquePerm;
    }

    /**
     * @param hasUniquePerm the hasUniquePerm to set
     */
    public void setHasUniquePerm(boolean hasUniquePerm)
    {
        this.hasUniquePerm = hasUniquePerm;
    }

    /**
     * @return the workspaceType
     */
    public WorkspaceType getWorkspaceType()
    {
        return workspaceType;
    }

    /**
     * @param workspaceType the workspaceType to set
     */
    public void setWorkspaceType(WorkspaceType workspaceType)
    {
        this.workspaceType = workspaceType;
    }

    /**
     * @return the isADMode
     */
    public boolean isADMode()
    {
        return isADMode;
    }

    /**
     * @param isADMode the isADMode to set
     */
    public void setADMode(boolean isADMode)
    {
        this.isADMode = isADMode;
    }

    /**
     * @return the docUrl
     */
    public String getDocUrl()
    {
        return docUrl;
    }

    /**
     * @param docUrl the docUrl to set
     */
    public void setDocUrl(String docUrl)
    {
        this.docUrl = docUrl;
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
     * @see org.alfresco.module.vti.metadata.model.DwsData
     * 
     * @return the dwsData
     */
    public DwsData getDwsData()
    {
        return dwsData;
    }

    /**
     * @see org.alfresco.module.vti.metadata.model.DwsData
     * 
     * @param dwsData the dwsData to set
     */
    public void setDwsData(DwsData dwsData)
    {
        this.dwsData = dwsData;
    }
        
}
