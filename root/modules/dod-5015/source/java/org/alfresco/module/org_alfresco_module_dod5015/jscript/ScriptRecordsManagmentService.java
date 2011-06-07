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
package org.alfresco.module.org_alfresco_module_dod5015.jscript;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceRegistry;
import org.alfresco.module.org_alfresco_module_dod5015.model.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.notification.RecordsManagementNotificationService;
import org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.scripts.ScriptException;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Records management service
 * 
 * @author Roy Wetherall
 */
public class ScriptRecordsManagmentService extends BaseScopableProcessorExtension
                                           implements RecordsManagementModel 
{
    /** Notification values */
    private String notificationRole;
    private String notificationSubject;
    
    /** Records management service registry */
    private RecordsManagementServiceRegistry rmServices;
    
    /**
     * Set records management service registry 
     * 
     * @param rmServices    records management service registry
     */
    public void setRecordsManagementServiceRegistry(RecordsManagementServiceRegistry rmServices)
    {
        this.rmServices = rmServices;
    }
    
    /**
     * Sets the notification role
     * 
     * @param notificationRole  notification role
     */
    public void setNotificationRole(String notificationRole)
    {
        this.notificationRole = notificationRole;
    }
    
    /**
     * Sets the notification subject
     * 
     * @param notificationSubject   notification subject
     */
    public void setNotificationSubject(String notificationSubject)
    {
        this.notificationSubject = notificationSubject;
    }
    
    /**
     * Get records management node
     * 
     * @param node                          script node
     * @return ScriptRecordsManagementNode  records management script node
     */
    public ScriptRecordsManagmentNode getRecordsManagementNode(ScriptNode node)
    {
        ScriptRecordsManagmentNode result = null;
        
        if (rmServices.getNodeService().hasAspect(node.getNodeRef(), ASPECT_FILE_PLAN_COMPONENT) == true)
        {
            // TODO .. at this point determine what type of records management node is it and 
            //         create the appropriate sub-type
            result = new ScriptRecordsManagmentNode(node.getNodeRef(), rmServices);
        }
        else
        {
            throw new ScriptException("Node is not a records management node type.");
        }
        
        return result;
    }
    
    /**
     * Set the RM permission
     * 
     * @param node
     * @param permission
     * @param authority
     */
    public void setPermission(ScriptNode node, String permission, String authority)
    {
        RecordsManagementSecurityService securityService = rmServices.getRecordsManagementSecurityService();
        securityService.setPermission(node.getNodeRef(), authority, permission);
    }
    
    /**
     * Delete the RM permission
     * 
     * @param node
     * @param permission
     * @param authority
     */
    public void deletePermission(ScriptNode node, String permission, String authority)
    {
        RecordsManagementSecurityService securityService = rmServices.getRecordsManagementSecurityService();
        securityService.deletePermission(node.getNodeRef(), authority, permission);
    }
    
    /**
     * Sends an email notification to everyone in the notification role
     */
    public void sendNotification(String notificationEvent, String notificationType, ScriptNode node)
    {
        // Create notification data
        Map<String, Object> notificationData = new HashMap<String, Object>();
        notificationData.put("record", node.getNodeRef());
        notificationData.put("subject", notificationSubject);
        
        // Get records management root node
        RecordsManagementService rmService = rmServices.getRecordsManagementService();
        NodeRef rmRootNode = rmService.getRecordsManagementRoot(node.getNodeRef());
        
        // Send the notification
        RecordsManagementNotificationService rmNotification = rmServices.getRecordsManagementNotificationService();
        rmNotification.sendNotificationToRole(
                notificationEvent, 
                notificationType, 
                rmRootNode, 
                notificationRole, 
                notificationData);
    }
}
