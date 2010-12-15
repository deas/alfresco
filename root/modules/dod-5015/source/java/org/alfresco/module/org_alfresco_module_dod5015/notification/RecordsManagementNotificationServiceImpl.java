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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService;
import org.alfresco.module.org_alfresco_module_dod5015.security.Role;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Records management notification service implementation
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementNotificationServiceImpl implements RecordsManagementNotificationService
{
    /** Log */
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(RecordsManagementNotificationServiceImpl.class);
    
    /** Authority service */
    private AuthorityService authorityService;
    
    /** Action service */
    private ActionService actionService;
    
    /** Search service */
    private SearchService searchService;
    
    /** Template service */
    private TemplateService templateService;
    
    /** Person service */
    private PersonService personService;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Records management security service */
    private RecordsManagementSecurityService recordsManagementSecurityService;
    
    /** Notification events */
    private Map<String, NodeRef> notificationEvents;
    
    /** From email */
    private String emailFrom;
    
    /**
     * Set the authority service
     * 
     * @param authorityService
     */
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }
    
    /**
     * Sets the action service 
     * 
     * @param actionService
     */
    public void setActionService(ActionService actionService)
    {
        this.actionService = actionService;
    }
    
    /**
     * Sets the search service
     * 
     * @param searchService     the search service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
    /**
     * Set template service
     * 
     * @param templateService   the template service
     */
    public void setTemplateService(TemplateService templateService)
    {
        this.templateService = templateService;
    }
    
    /**
     * Set the records management security service
     * 
     * @param recordsManagementSecurityService  the records management security service
     */
    public void setRecordsManagementSecurityService(RecordsManagementSecurityService recordsManagementSecurityService)
    {
        this.recordsManagementSecurityService = recordsManagementSecurityService;
    }
    
    /**
     * Set the person service
     * 
     * @param personService     the person service
     */
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }
    
    /**
     * Set the node service
     * 
     * @param nodeService   the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.notification.RecordsManagementNotificationService#getNotificationEvents()
     */
    public Set<String> getNotificationEvents()
    {
        return getNotificationEventMap().keySet();
    }
    
    /**
     * Set the from email
     * 
     * @param emailFrom     email from address
     */
    public void setEmailFrom(String emailFrom)
    {
        this.emailFrom = emailFrom;
    }
    
    /**
     * Gets the notification event map
     * 
     * TODO this is currently hard coded, eventually will be 'Sprung' in
     * 
     * @return
     */
    private Map<String, NodeRef> getNotificationEventMap()
    {
        if (notificationEvents == null)
        {
            // TODO temp code to find template
            final String template = "/app:company_home/app:dictionary/cm:records_management/cm:records_management_email_templates/cm:notify-records-due-for-review-email.ftl";            
            ResultSet templateResults = searchService.query(
                    StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
                    SearchService.LANGUAGE_XPATH, template);
        
            final List<NodeRef> templateNodes = templateResults.getNodeRefs();
            templateResults.close();
            if (templateNodes.size() == 0)
            {
                throw new AlfrescoRuntimeException("Can not find records due for review email template");
            }
            
            // TODO for now manually initialise the notification events linking them to their templates
            notificationEvents = new HashMap<String, NodeRef>(2);
            notificationEvents.put(NE_DUE_FOR_REVIEW, templateNodes.get(0));
            notificationEvents.put(NE_SUPERSEDED, new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "record_superseded_template"));
        }
        
        return notificationEvents;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.notification.RecordsManagementNotificationService#getNotificationTypes()
     */
    public Set<String> getNotificationTypes()
    {
        // TODO for now this returns a fixed list
        Set<String> notificationTypes = new HashSet<String>(1);
        notificationTypes.add(NT_EMAIL);
        return null;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.notification.RecordsManagementNotificationService#sendNotificationToGroup(java.lang.String, java.lang.String, java.util.Map)
     */
    public void sendNotificationToGroup(final String notificationEvent, final String notificationType, final String groupName, final Map<String, Object> notificationData)
    {
        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                if (authorityService.authorityExists(groupName) == true)
                {
                    Set<String> users = authorityService.getContainedAuthorities(AuthorityType.USER, groupName, false);
                    for (String user : users)
                    {
                        sendNotificationToUser(notificationEvent, notificationType, user, notificationData);
                    }
                }
                
                return null;
            }
    
        }, AuthenticationUtil.getAdminUserName());      
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.notification.RecordsManagementNotificationService#sendNotificationToRole(java.lang.String, org.alfresco.service.cmr.repository.NodeRef, java.lang.String, java.util.Map)
     */
    public void sendNotificationToRole(final String notificationEvent, final String notificationType, final NodeRef rmRootNode, final String roleName, final Map<String, Object> notificationData)
    {
        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                Role role = recordsManagementSecurityService.getRole(rmRootNode, roleName);
                if (role != null)
                {
                    sendNotificationToGroup(notificationEvent, notificationType, role.getRoleGroupName(), notificationData);
                }
                
                return null;
            }
    
        }, AuthenticationUtil.getAdminUserName());                     
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.notification.RecordsManagementNotificationService#sendNotificationToUser(java.lang.String, java.lang.String, java.util.Map)
     */
    public void sendNotificationToUser(final String notificationEvent, final String notificationType, final String userName, final Map<String, Object> notificationData)
    {
        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                // TODO for now the notification type is hard coded
                //      eventually the notification type can be separated into a well known interface and implementations registered 
                //      via Spring into the service
                if (notificationType.equals(NT_EMAIL) == true)
                {
                    // TODO this implementation should be encapsulated else where (as described above) and
                    //      the presence of the bits of notification data checked
                    
                    String to = getUserEMail(userName);
                    if (to != null)
                    {
                        String subject = (String)notificationData.get("subject");  
                        String body = generateNotificationEventContent(notificationEvent, notificationData);
                        
                        if (logger.isDebugEnabled() == true)
                        {
                            logger.debug("Sending notificaiton email to " + to);
                        }
                        
                        // Send email
                        Action emailAction = actionService.createAction("mail");
                        emailAction.setParameterValue(MailActionExecuter.PARAM_TO, to);
                        emailAction.setParameterValue(MailActionExecuter.PARAM_FROM, emailFrom);
                        emailAction.setParameterValue(MailActionExecuter.PARAM_SUBJECT, subject);
                        emailAction.setParameterValue(MailActionExecuter.PARAM_TEXT, body);
                        emailAction.setExecuteAsynchronously(true);
                        actionService.executeAction(emailAction, null);
                    }
                    // If no email address for user is found, don't send notification!
                }
                else
                {
                    throw new AlfrescoRuntimeException("The notification type " + notificationType + " is currently unsupported.");
                }                
                
                return null;
            }
        }, AuthenticationUtil.getAdminUserName());
    }
    
    /**
     * 
     * @param notificationEvent
     * @param notificationData
     * @return
     */
    private String generateNotificationEventContent(String notificationEvent, Map<String, Object> notificationData)
    {
        NodeRef templateNodeRef = getNotificationEventMap().get(notificationEvent);
        if (templateNodeRef == null)
        {
            throw new AlfrescoRuntimeException("The notification event " + notificationEvent + "is invalid");
        }
        return templateService.processTemplate(templateNodeRef.toString(), notificationData);
    }
    
    /**
     * Gets a users email address
     * 
     * @param userName  user name
     * @return String   the email address, null if non found
     */
    private String getUserEMail(String userName)
    {
        String result = null;
        if (personService.personExists(userName) == true)
        {
            NodeRef personNodeRef = personService.getPerson(userName);
            result = (String)nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL);            
        }
        return result;
    }
}
