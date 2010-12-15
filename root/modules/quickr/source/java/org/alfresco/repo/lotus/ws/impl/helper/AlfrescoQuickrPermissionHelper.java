/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.repo.lotus.ws.impl.helper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;

/**
 * @author Dmitry Vaserin
 */
public class AlfrescoQuickrPermissionHelper
{
    private final static Map<String, String> quickrToAlfPerms;

    static
    {
        quickrToAlfPerms = new HashMap<String, String>();
        quickrToAlfPerms.put("AddChild", PermissionService.ADD_CHILDREN);
        quickrToAlfPerms.put("Delete", PermissionService.DELETE);
        quickrToAlfPerms.put("Edit", PermissionService.WRITE);
        quickrToAlfPerms.put("View", PermissionService.READ);
        quickrToAlfPerms.put("GrantAccess", PermissionService.CHANGE_PERMISSIONS);
        // quickrToAlfPerms.put("Delegate",PermissionService.);
        // quickrToAlfPerms.put("LockOverride",PermissionService.);
        quickrToAlfPerms.put("AddFolder", PermissionService.ADD_CHILDREN);
        quickrToAlfPerms.put("EditFolder", PermissionService.WRITE);
    }

    private PermissionService permissionService;

    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    /**
     * Get all the Quickr permissions that are granted to the current authentication for the given node
     * 
     * @param nodeRef - the reference to the node
     * @return the set of allowed permissions
     */
    public List<String> getPermissions(NodeRef nodeRef)
    {
        List<String> allowedPerms = new LinkedList<String>();
        for (String quickrPerm : quickrToAlfPerms.keySet())
        {
            if (permissionService.hasPermission(nodeRef, quickrToAlfPerms.get(quickrPerm)).equals(AccessStatus.ALLOWED))
            {
                allowedPerms.add(quickrPerm);
            }
        }

        return allowedPerms;
    }
}
