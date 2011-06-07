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

package org.alfresco.module.org_alfresco_module_dod5015.test.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.model.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.notification.RecordsManagementNotificationService;
import org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService;
import org.alfresco.module.org_alfresco_module_dod5015.security.Role;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyMap;
import org.springframework.context.ApplicationContext;

/**
 * Notification service implementation (system) test
 * 
 * @author Roy Wetherall
 */
public class NotificationServiceImplSystemTest extends TestCase
{
    /** Application context */
    private ApplicationContext applicationContext;    
    private static boolean dataCreated = false;
    
    private static final String EMAIL_1 = "test@alfresco.com";  // Update test email here
    private static final String EMAIL_2 = "test@alfresco.com";    
    private static final String NT_EMAIL = "email";
    private static final String NE_DUE_FOR_REVIEW = "dueForReview";
    private static final String NE_REFERENCE_CREATED = "referenceCreated";
    
    private NodeRef filePlan;
    private String userOne;
    private String userTwo;
    private String group;
    private Role role;
    
    /** Services */
    private RecordsManagementNotificationService rmNotificationService;
    private RetryingTransactionHelper transactionHelper;
    private NodeService nodeService;
    private MutableAuthenticationService authenticationService;
    private PersonService personService;
    private AuthorityService authorityService;
    private RecordsManagementSecurityService rmSecurityService;
    
    @Override
    protected void setUp() throws Exception
    {
        applicationContext = ApplicationContextHelper.getApplicationContext();
        
        super.setUp();
        
        // Get services
        rmNotificationService = (RecordsManagementNotificationService)applicationContext.getBean("RecordsManagementNotificationService");
        transactionHelper = (RetryingTransactionHelper)applicationContext.getBean("retryingTransactionHelper");
        nodeService = (NodeService)applicationContext.getBean("NodeService");
        authenticationService = (MutableAuthenticationService)applicationContext.getBean("authenticationService");
        personService = (PersonService)applicationContext.getBean("PersonService");
        authorityService = (AuthorityService)applicationContext.getBean("AuthorityService");
        rmSecurityService = (RecordsManagementSecurityService)applicationContext.getBean("RecordsManagementSecurityService");
        
        // Set admin as the authenticated user
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        //if (dataCreated == false)
        //{
            filePlan =  transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
            {
                public NodeRef execute() throws Throwable
                {
                    NodeRef root = nodeService.getRootNode(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore"));
                    return nodeService.createNode(root, ContentModel.ASSOC_CHILDREN, ContentModel.ASSOC_CHILDREN, DOD5015Model.TYPE_FILE_PLAN).getChildRef();
                }        
            });
            
            transactionHelper.doInTransaction(new RetryingTransactionCallback<Object>()
            {
                public Object execute() throws Throwable
                {
                    createTestData();
                    return null;
                }        
            });
         //   dataCreated = true;
        //}
    }
    
    private void createTestData()
    {
        userOne = createUser(EMAIL_1);
        userTwo = createUser(EMAIL_2);
        
        group = authorityService.createAuthority(AuthorityType.GROUP, "myTestGroup" + GUID.generate());
        authorityService.addAuthority(group, userOne);
        authorityService.addAuthority(group, userTwo);
        
        role = rmSecurityService.createRole(filePlan, "myRole" + GUID.generate(), "My Role", null);
        rmSecurityService.assignRoleToAuthority(filePlan, role.getName(), userOne);
        rmSecurityService.assignRoleToAuthority(filePlan, role.getName(), group);
    }
    
    private String createUser(String email)
    {
        // Create an authentication
        String userName = GUID.generate();
        authenticationService.createAuthentication(userName, "PWD".toCharArray());
                
        // Create a person
        PropertyMap ppOne = new PropertyMap(4);
        ppOne.put(ContentModel.PROP_USERNAME, userName);
        ppOne.put(ContentModel.PROP_FIRSTNAME, "firstName");
        ppOne.put(ContentModel.PROP_LASTNAME, "lastName");
        ppOne.put(ContentModel.PROP_EMAIL, email);
        ppOne.put(ContentModel.PROP_JOBTITLE, "jobTitle");        
        personService.createPerson(ppOne);
           
        return userName;
    }
    
    public void testEMailUser()
    {
        Map<String, Object> record = new HashMap<String, Object>();
        record.put("name", "recordOne");
        record.put("identifier", "id123");
        
        List<Map<String,Object>> arrayList = new ArrayList<Map<String,Object>>();
        arrayList.add(record);
        
        Map<String, Object> notificationData = new HashMap<String, Object>();
        notificationData.put("records", arrayList);        
        notificationData.put("subject", "This is an email to user");
        
        
        rmNotificationService.sendNotificationToUser(
                NE_DUE_FOR_REVIEW, 
                NT_EMAIL, 
                userOne, 
                notificationData);
    }
    
    public void testEMailGroup()
    {
        Map<String, Object> record = new HashMap<String, Object>();
        record.put("name", "recordOne");
        record.put("identifier", "id123");
        
        List<Map<String,Object>> arrayList = new ArrayList<Map<String,Object>>();
        arrayList.add(record);
        
        Map<String, Object> notificationData = new HashMap<String, Object>();
        notificationData.put("records", arrayList);        
        notificationData.put("subject", "This is an email to group");
        
        
        rmNotificationService.sendNotificationToGroup(
                NE_DUE_FOR_REVIEW, 
                NT_EMAIL, 
                group, 
                notificationData);
    }
    
    public void testEMailRole()
    {
        Map<String, Object> record = new HashMap<String, Object>();
        record.put("name", "recordOne");
        record.put("identifier", "id123");
        
        List<Map<String,Object>> arrayList = new ArrayList<Map<String,Object>>();
        arrayList.add(record);
        
        Map<String, Object> notificationData = new HashMap<String, Object>();
        notificationData.put("records", arrayList);        
        notificationData.put("subject", "This is an email to role");
        
        
        rmNotificationService.sendNotificationToRole(
                NE_DUE_FOR_REVIEW, 
                NT_EMAIL,
                filePlan,
                role.getName(), 
                notificationData);
    }
}
