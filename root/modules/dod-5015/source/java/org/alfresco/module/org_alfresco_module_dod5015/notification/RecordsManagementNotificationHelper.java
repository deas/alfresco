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
package org.alfresco.module.org_alfresco_module_dod5015.notification;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService;
import org.alfresco.module.org_alfresco_module_dod5015.security.Role;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.notification.EMailNotificationProvider;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.notification.NotificationContext;
import org.alfresco.service.cmr.notification.NotificationService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.util.ParameterCheck;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Helper bean containing methods useful when sending records
 * management notifications via the {@link NotificationService}
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementNotificationHelper
{
    /** I18n */
    private static final String MSG_SUBJECT_RECORDS_DUE_FOR_REVIEW = "notification.dueforreview.subject";
    private static final String MSG_SUBJECT_RECORD_SUPERCEDED = "notification.superseded.subject";

    /** Services */
    private NotificationService notificationService;
    private RecordsManagementService recordsManagementService;
    private RecordsManagementSecurityService securityService;
    private Repository repositoryHelper;
    private SearchService searchService;
    private NamespaceService namespaceService;
    
    /** Notification role */
    private String notificationRole;
    
    /** EMail notification templates */
    private NodeRef supersededTemplate = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "record_superseded_template");
    private NodeRef dueForReviewTemplate;
    
    /**
     * @param notificationService   notification service
     */
    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }
    
    /**
     * @param recordsManagementService  rm service
     */
    public void setRecordsManagementService(RecordsManagementService recordsManagementService)
    {
        this.recordsManagementService = recordsManagementService;
    }
    
    /**
     * @param securityService   rm security service
     */
    public void setSecurityService(RecordsManagementSecurityService securityService)
    {
        this.securityService = securityService;
    }
    
    /**
     * @param notificationRole  rm notification role
     */
    public void setNotificationRole(String notificationRole)
    {
        this.notificationRole = notificationRole;
    }
    
    /**
     * 
     * @param repositoryHelper  repository helper
     */
    public void setRepositoryHelper(Repository repositoryHelper)
    {
        this.repositoryHelper = repositoryHelper;
    }
    
    /**
     * @param searchService search service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    /**
     * @param namespaceService  namespace service
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    /**
     * @return  superseded email template
     */
    public NodeRef getSupersededTemplate()
    {
        return supersededTemplate;
    }
    
    /**
     * @return  due for review email template
     */
    public NodeRef getDueForReviewTemplate()
    {
        if (dueForReviewTemplate == null)
        {
            List<NodeRef> nodeRefs = 
                searchService.selectNodes(
                        repositoryHelper.getRootHome(), 
                        "app:company_home/app:dictionary/cm:records_management/cm:records_management_email_templates/cm:notify-records-due-for-review-email.ftl", null, 
                        namespaceService, 
                        false);
            if (nodeRefs.size() == 1)
            {
                dueForReviewTemplate = nodeRefs.get(0);
            }
        }
        return dueForReviewTemplate;
    }
    
    /**
     * 
     * @param records
     * @param roleName
     */
    public void recordsDueForReviewEmailNotification(final List<NodeRef> records)
    {
        ParameterCheck.mandatory("records", records);
        if (records.isEmpty() == false)
        {
            NotificationContext notificationContext = new NotificationContext();
            notificationContext.setSubject(I18NUtil.getMessage(MSG_SUBJECT_RECORDS_DUE_FOR_REVIEW));
            notificationContext.setAsyncNotification(false);
            notificationContext.setIgnoreNotificationFailure(true);
            
            notificationContext.setBodyTemplate(getDueForReviewTemplate());
            Map<String, Serializable> args = new HashMap<String, Serializable>(1, 1.0f);
            args.put("records", (Serializable)records); 
            notificationContext.setTemplateArgs(args);
            
            String groupName = getGroupName(records.get(0));
            notificationContext.addTo(groupName);        
            
            notificationService.sendNotification(EMailNotificationProvider.NAME, notificationContext);
        }
    }    
    
    /**
     * 
     * @param record
     */
    public void recordSupersededEmailNotification(final NodeRef record)
    {
        ParameterCheck.mandatory("record", record);   
        
        NotificationContext notificationContext = new NotificationContext();
        notificationContext.setSubject(I18NUtil.getMessage(MSG_SUBJECT_RECORD_SUPERCEDED));
        notificationContext.setAsyncNotification(false);
        notificationContext.setIgnoreNotificationFailure(true);
        
        notificationContext.setBodyTemplate(supersededTemplate);
        Map<String, Serializable> args = new HashMap<String, Serializable>(1, 1.0f);
        args.put("record", record); 
        notificationContext.setTemplateArgs(args);
        
        String groupName = getGroupName(record);
        notificationContext.addTo(groupName);        
        
        notificationService.sendNotification(EMailNotificationProvider.NAME, notificationContext);
    }
    
    /**
     * 
     * @param context
     * @return
     */
    private String getGroupName(final NodeRef context)
    {
        return AuthenticationUtil.runAs(new RunAsWork<String>()
        {
            @Override
            public String doWork() throws Exception
            {
                // Find the authority for the given role
                NodeRef root = recordsManagementService.getRecordsManagementRoot(context);
                Role role = securityService.getRole(root, notificationRole);
                return role.getRoleGroupName();
            }
        }, AuthenticationUtil.getSystemUserName());
    }
}
