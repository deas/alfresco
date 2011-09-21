/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015.capability;

import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;

/**
 * @author andyh
 */
public interface Capability
{
    /**
     * Does this capability apply to this nodeRef?
     * @param nodeRef
     * @return
     */
    public AccessStatus hasPermission(NodeRef nodeRef);

    /**
     * Indicates whether this is a group capability or not
     * 
     * @return
     */
    public boolean isGroupCapability();
    
    /**
     * Get the name of the capability
     * @return
     */
    public String getName();

    /**
     * Get the name of optional actions tied to this capability
     * @return
     */
    public List<String> getActionNames();
    
    public int hasPermissionRaw(NodeRef nodeRef);
    
    public List<RecordsManagementAction> getActions();
        
}
