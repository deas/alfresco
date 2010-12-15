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
package org.alfresco.module.mediawikiintegration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.phpIntegration.PHPProcessorException;
import org.alfresco.module.phpIntegration.lib.Folder;
import org.alfresco.module.phpIntegration.lib.Repository;
import org.alfresco.module.phpIntegration.lib.Session;
import org.alfresco.module.phpIntegration.lib.Store;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * MediaWiki space node type.  Provides helper methods to get the mediawiki 
 * space details.
 * 
 * @author Roy Wetherall
 */
public class MediaWikiSpace extends Folder
{
    /** The script object name */
    private static final String SCRIPT_OBJECT_NAME = "MediaWikiSpace";
    
    /**
     * Constructor
     * 
     * @param session   the session
     * @param nodeRef   the node reference
     */
    public MediaWikiSpace(Session session, NodeRef nodeRef)
    {
        super(session, nodeRef);
    }

    /**
     * Constructor
     * 
     * @param session   the session
     * @param store     the store
     * @param id        the node id
     */
    public MediaWikiSpace(Session session, Store store, String id)
    {
        super(session, store, id);
    }
    
    /**
     * Constructor
     * 
     * @param session   the session
     * @param store     the store
     * @param id        the id
     * @param type      the node type
     */
    public MediaWikiSpace(Session session, Store store, String id, String type)
    {
        super(session, store, id);
    }
    
    
    
    @Override
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    public static String validate(Repository repository, String nodeRefWikiSpace, String userName, String password)
    {
        String result = null;
        
        // Get the ticket
        String ticket = null;
        if (password.startsWith("TICKET_") == true)
        {
            ticket = password;
        }
        else
        {
            ticket = repository.authenticate(userName, password);
        }
        
        // Get the authentication service
        AuthenticationService authenticationService = repository.getServiceRegistry().getAuthenticationService();
        
        try
        {
            AuthenticationUtil.pushAuthentication();
            try
            {
                // Try and validate the ticket
                authenticationService.validate(ticket);
                
                // Check whether these credentials have the required permissions on the wikispace
                NodeRef nodeRef = new NodeRef(nodeRefWikiSpace);
                if (repository.getServiceRegistry().getPermissionService().hasPermission(nodeRef, "Collaborator") == AccessStatus.ALLOWED)
                {
                    result = ticket;
                }
            }
            finally
            {
                // Re-establish the previous authentication context
                AuthenticationUtil.popAuthentication();
            }
        }
        catch (Exception excpetion)
        {
            // Ensure result is empty
            result = null;
        }
        
        return result;
    }
    
    public static String getEvaluationString(Repository repository, String nodeRefWikiSpace)
    {
        StringBuffer buffer = new StringBuffer(1024);
        for (Map.Entry<String, String> entry : getConfigurationProperties(repository, nodeRefWikiSpace).entrySet())
        {
            int index = entry.getKey().indexOf("}");
            String name = entry.getKey().substring(index+1);
            
            buffer
                .append("$")
                .append(name)
                .append(" = \"")
                .append(entry.getValue())
                .append("\";\n");
        }
        
        return buffer.toString();
    }
    
    public static Map<String, String> getConfigurationProperties(Repository repository, final String nodeRefWikiSpace)
    {
        final NodeService nodeService = repository.getServiceRegistry().getNodeService();
        final NodeRef nodeRef = new NodeRef(nodeRefWikiSpace);
        
        final Map<String, String> configProperties = new HashMap<String, String>(20);
        
        AuthenticationUtil.runAs(new RunAsWork<Object>()
        {
            public Object doWork() throws Exception
            {
                List<ChildAssociationRef> assocs = nodeService.getChildAssocs(nodeRef, Constants.ASSOC_CONFIG, RegexQNamePattern.MATCH_ALL);
                if (assocs.size() != 1)
                {
                    throw new PHPProcessorException("MediaWiki configuration for " + nodeRefWikiSpace.toString() + " is not presnet.");
                }
                
                NodeRef configNodeRef = assocs.get(0).getChildRef();
                Map<QName, Serializable> properties = nodeService.getProperties(configNodeRef);
                
                for (Map.Entry<QName, Serializable> entry : properties.entrySet())                
                {
                    if (entry.getKey().getNamespaceURI().equals(Constants.CONFIG_NAMESPACE) == true)
                    {
                        String value = entry.getValue().toString();
                        if (value != null && value.trim().length() != 0)
                        {
                            configProperties.put(entry.getKey().toString(), value);
                        }
                    }
                }
                
                return null;
            }
        }, AuthenticationUtil.getSystemUserName());
                    
        return configProperties;
    }
}
