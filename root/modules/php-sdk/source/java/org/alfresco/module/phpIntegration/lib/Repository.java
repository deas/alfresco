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
package org.alfresco.module.phpIntegration.lib;

import javax.servlet.ServletContext;

import org.alfresco.module.phpIntegration.PHPProcessor;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.caucho.quercus.annotation.Optional;
import com.caucho.quercus.env.Env;

/**
 * @author Roy Wetherall
 */
public class Repository implements ScriptObject
{
	/** The name of the script object */
    private static final String SCRIPT_OBJECT_NAME = "Repository";
    
    /** The service registry */
    private ServiceRegistry serviceRegistry;
    
    /** The node factory */
    private NodeFactory nodeFactory;
    
    /**
     *  The connection URL (this really doesn't have a lot of meaning for the local PHP 
     * processor but is kept for API consistency)
     */
    @SuppressWarnings("unused")
	private String connectionURL;    
    
    /**
     * Constructor
     * 
     * @param env	PHP env object
     */
    public Repository(Env env, @Optional("") String connectionURL)
    {
        if (env.getRequest() != null)
        {
            ServletContext servletContext = env.getRequest().getSession().getServletContext();
            ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            this.serviceRegistry = (ServiceRegistry)applicationContext.getBean("ServiceRegistry");
            this.nodeFactory = (NodeFactory)applicationContext.getBean("phpNodeFactory");
        }
        else
        {
            this.serviceRegistry = (ServiceRegistry)env.getQuercus().getSpecial(PHPProcessor.KEY_SERVICE_REGISTRY);
            this.nodeFactory = (NodeFactory)env.getQuercus().getSpecial(PHPProcessor.KEY_NODE_FACTORY);
        }
        
        // Set the connectionURL
        this.connectionURL = connectionURL;
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    public ServiceRegistry getServiceRegistry()
    {
        return this.serviceRegistry;
    }
    
    /*package*/ NodeFactory getNodeFactory()
    {
        return this.nodeFactory;
    }
    
    /**
     * Create session object
     * 
     * @return	the session object
     */
    public Session createSession(@Optional("") String ticket)
    {
    	if (ticket.length() == 0)
    	{
    		// Use the currently authenticated ticket for the session
    		ticket = this.serviceRegistry.getAuthenticationService().getCurrentTicket();
    	}
    	
        return new Session(this, ticket);
    }
    
    /**
     * Authenticates the passed credentials
     * 
     * @param user		user name
     * @param password	password
     * @return String	ticket, null if unauthenticated
     */
    public String authenticate(String user, String password)
    {
    	String ticket = null;
    	
    	// Get the authentication service
    	AuthenticationService authenticationService = this.serviceRegistry.getAuthenticationService();
    	
    	AuthenticationUtil.pushAuthentication();
        try
        {
        	// Try and authenticate with the provided user details
    		authenticationService.authenticate(user, password.toCharArray());
    		
    		// Retrieve the ticket
    		ticket = authenticationService.getCurrentTicket();
        }
        finally
        {
        	// Re-establish the previous authentication context
            AuthenticationUtil.popAuthentication();
        }
    	
    	return ticket;
    }
}
