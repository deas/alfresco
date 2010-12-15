/*
 * Copyright (C) 2009-2010 Alfresco Software Limited.
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

package org.alfresco.module.org_alfresco_module_dod5015.notification;

import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Records Management Notification Service
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementNotificationService 
{
    /** TODO Temporary constants */
    public static final String NT_EMAIL = "email";
    public static final String NE_DUE_FOR_REVIEW = "dueForReview";
    public static final String NE_SUPERSEDED = "superseded";
        
    /**
     * Get notification events
     * 
     * @return
     */
    Set<String> getNotificationEvents();
    
    /**
     * Get notification types 
     * 
     * @return
     */
    Set<String> getNotificationTypes();
    
    /**
     * Send notification to user
     * 
     * @param notificationEvent
     * @param notificationType
     * @param userName
     * @param notificationData
     */
    void sendNotificationToUser(String notificationEvent, String notificationType, String userName, Map<String, Object> notificationData);
    
    /**
     * Send notification to group
     * 
     * @param notificationEvent
     * @param notificationType
     * @param groupName
     * @param notificationData
     */
    void sendNotificationToGroup(String notificationEvent, String notificationType, String groupName, Map<String, Object> notificationData);
    
    /**
     * Send notification to role
     * 
     * @param notificationEvent
     * @param notificationType
     * @param rmRootNode
     * @param roleName
     * @param notificationData
     */
    void sendNotificationToRole(String notificationEvent, String notificationType, NodeRef rmRootNode, String roleName, Map<String, Object> notificationData);
}
