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
package org.alfresco.module.vti.handler.alfresco;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.UserGroupServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.model.UserBean;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.security.PersonService.PersonInfo;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ObjectUtils;

/**
 * Alfresco implementation of UserGroupServiceHandler
 * 
 * @author PavelYur
 */
public class AlfrescoUserGroupServiceHandler implements UserGroupServiceHandler
{
    private static Log logger = LogFactory.getLog(AlfrescoUserGroupServiceHandler.class);

    private NodeService nodeService;
    private SiteService siteService;
    private PersonService personService;
    private AuthorityService authorityService;
    private TransactionService transactionService;

    /**
     * Set the node service
     * 
     * @param nodeService the node service to set ({@link NodeService})
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set the person service
     * 
     * @param personService the person service to set ({@link PersonService})
     */
    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    /**
     * Set the authority service
     * 
     * @param authorityService the authority service to set ({@link AuthorityService})
     */
    public void setAuthorityService(AuthorityService authorityService)
    {
        this.authorityService = authorityService;
    }

    /**
     * Set transaction service
     * 
     * @param transactionService the transaction service to set ({@link TransactionService})
     */
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Set site service
     * 
     * @param siteService the site service to set ({@link SiteService})
     */
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }
    

    /**
     * @see org.alfresco.module.vti.handler.UserGroupServiceHandler#addUserCollectionToRole(java.lang.String, java.lang.String, java.util.List)
     */
    public void addUserCollectionToRole(String dws, String roleName, List<UserBean> usersList)
    {
        dws = VtiPathHelper.removeSlashes(dws);
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'addUserCollectionToRole' is started.");

        if (logger.isDebugEnabled())
            logger.debug("Getting siteInfo for '" + dws + "'.");
        SiteInfo siteInfo = siteService.getSite(dws);

        if (siteInfo == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error: Site info not found.");
            throw new VtiHandlerException(VtiHandlerException.NOT_FOUND);
        }

        for (UserBean userBean : usersList)
        {
            NodeRef person = personService.getPerson(userBean.getLoginName().substring("ALFRESCO\\".length()));
            if (person != null)
            {
                String userName = (String) nodeService.getProperty(person, ContentModel.PROP_USERNAME);

                if (AuthorityType.getAuthorityType(userName) == AuthorityType.GUEST)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Error: Not allowed operation: guest can not be added.");
                    throw new RuntimeException("Not allowed operation: guest can not be added.");
                }
                UserTransaction tx = transactionService.getUserTransaction(false);
                try
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Setting membership [" + dws + ", " + userName + "].");

                    tx.begin();

                    siteService.setMembership(dws, userName, roleName);

                    tx.commit();
                }
                catch (Exception e)
                {
                    try
                    {
                        tx.rollback();
                    }
                    catch (Exception tex)
                    {
                    }
                    if (logger.isDebugEnabled())
                        logger.debug("Error: The user does not have sufficient rights.", e);
                    throw new VtiHandlerException(VtiHandlerException.NO_PERMISSIONS);
                }

            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.debug("Error: The user does not have sufficient rights.");
                throw new VtiHandlerException(VtiHandlerException.NO_PERMISSIONS);
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'addUserCollectionToRole' is finished.");
    }

    /**
     * @see org.alfresco.module.vti.handler.UserGroupServiceHandler#isUserMember(java.lang.String, java.lang.String)
     */
    public boolean isUserMember(String dwsUrl, String username)
    {
        // Normalize the user ID taking into account case sensitivity settings
        String normalized = personService.getUserIdentifier(username);
        if (normalized == null)
        {
            return false;
        }
        
        boolean isMember = siteService.isMember(dwsUrl, normalized);
        
        if (!isMember && authorityService.isAdminAuthority(username)) 
        {
           // Admin is allowed to do things even on sites they're not
           //  a member of. So, pretend they are a member so they're allowed
           isMember = true;
        }
        
        return isMember;
    }

    /**
     * @see org.alfresco.module.vti.handler.UserGroupServiceHandler#getUserLoginFromEmail(java.lang.String, java.util.List)
     */
    public List<UserBean> getUserLoginFromEmail(String dwsUrl, List<String> emailList)
    {
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'getUserLoginFromEmail' is started.");

        List<UserBean> result = new LinkedList<UserBean>();

        for (String loginOrEmail : emailList)
        {
            if (logger.isDebugEnabled())
                logger.debug("Checking existence of login or email '" + loginOrEmail + "'.");

            if (personService.personExists(loginOrEmail))
            {
                if (logger.isDebugEnabled())
                    logger.debug("Login '" + loginOrEmail + "' is exist, adding to result.");

                result.add(getUserBean(personService.getPerson(loginOrEmail)));
            }
            else
            {
                // Search for the person by email
                List<Pair<QName,String>> filter = new ArrayList<Pair<QName,String>>();
                filter.add(new Pair<QName,String>(
                        ContentModel.PROP_EMAIL, loginOrEmail
                ));
                PagingRequest paging = new PagingRequest(10);
                
                // Do the search, case insensitively without sorting
                PagingResults<PersonInfo> people = personService.getPeople(filter, true, null, paging);
                
                // Did we find them?
                if (people.getPage().size() == 0)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("No person details found for " + loginOrEmail);
                    
                    // Fake their details
                    UserBean userBean = new UserBean();
                    userBean.setEmail(loginOrEmail);
                    result.add(userBean);
                }
                else
                {
                    if (people.getPage().size() > 1)
                    {
                        logger.info("Found " + people.getPage().size() + " person details for " +
                                    "email " + loginOrEmail + ", using only the first");
                    }
                    
                    // Build up the details for them
                    PersonInfo person = people.getPage().get(0);
                    result.add(getUserBean(person.getNodeRef()));
                    
                    if (logger.isDebugEnabled())
                        logger.debug("Found user details for " + loginOrEmail + " as " + person.getFirstName() + 
                                     " " + person.getLastName() + " at " + person.getNodeRef());
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'getUserLoginFromEmail' is finished.");

        return result;
    }

    /**
     * Returns user bean for person node reference
     * 
     * @param personNodeRef the person node reference ({@link NodeRef})
     * @return UserBean
     */
    protected UserBean getUserBean(NodeRef personNodeRef)
    {
        UserBean userBean = new UserBean();

        String userName = (String)nodeService.getProperty(personNodeRef, ContentModel.PROP_USERNAME);

        String firstName = ObjectUtils.getDisplayString(nodeService.getProperty(personNodeRef, ContentModel.PROP_FIRSTNAME));
        String lastName = ObjectUtils.getDisplayString(nodeService.getProperty(personNodeRef, ContentModel.PROP_LASTNAME));
        String email = ObjectUtils.getDisplayString(nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL));

        userBean.setDisplayName(firstName + " " + lastName);
        userBean.setEmail(email);
        userBean.setLoginName("ALFRESCO\\" + userName);

        return userBean;
    }
}