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

import java.util.LinkedList;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.handler.UserGroupServiceHandler;
import org.alfresco.module.vti.metadata.model.UserBean;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PersonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Alfresco implementation of UserGroupServiceHandler interface
 * 
 * @author PavelYur
 */
public abstract class AbstractAlfrescoUserGroupServiceHandler implements UserGroupServiceHandler
{
    private static Log logger = LogFactory.getLog(AbstractAlfrescoUserGroupServiceHandler.class);

    protected NodeService nodeService;
    protected PersonService personService;
    protected AuthorityService authorityService;

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
     * @see org.alfresco.module.vti.handler.UserGroupServiceHandler#getUserLoginFromEmail(java.lang.String, java.util.List)
     */
    @SuppressWarnings("deprecation")
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
                boolean personFounded = false;

                for (NodeRef personNodeRef : personService.getAllPeople())
                {
                    if (nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL).equals(loginOrEmail))
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Email '" + loginOrEmail + "' is exist, adding to result.");
                        result.add(getUserBean(personNodeRef));
                        personFounded = true;
                    }
                }

                if (personFounded == false)
                {
                    UserBean userBean = new UserBean();
                    userBean.setEmail(loginOrEmail);
                    result.add(userBean);
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

        String userName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_USERNAME));

        String firstName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_FIRSTNAME));
        String lastName = DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_LASTNAME));

        userBean.setDisplayName(firstName + " " + lastName);
        userBean.setEmail(DefaultTypeConverter.INSTANCE.convert(String.class, nodeService.getProperty(personNodeRef, ContentModel.PROP_EMAIL)));
        userBean.setLoginName("ALFRESCO\\" + userName);

        return userBean;
    }

}
