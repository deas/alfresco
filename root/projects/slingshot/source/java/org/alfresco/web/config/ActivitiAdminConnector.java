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
package org.alfresco.web.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;
import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;
import org.springframework.extensions.webscripts.connector.AlfrescoConnector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.ResponseStatus;

/**
 * Connector implementation to call the embedded Activiti Admin UI
 *
 * @author Frederik Heremans
 * @since 4.0
 */
public class ActivitiAdminConnector extends AlfrescoConnector
{
    public static final String PARAM_TICKETNAME_ALF_TICKET = "alf_ticket";
    
    public ActivitiAdminConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }
    
    @Override
    public Response call(String uri)
    {
        return super.call(uri);
    }
    
    @Override
    public Response call(String uri, ConnectorContext context, HttpServletRequest req,
                HttpServletResponse res)
    {
        try
        {
            // Send redirect to activiti-admin ui, containing ticket
            res.sendRedirect(getEndpoint() + (uri.lastIndexOf('?') == -1 ? ("?"+ PARAM_TICKETNAME_ALF_TICKET +"="+ getTicket(context)) : 
                ("&"+ PARAM_TICKETNAME_ALF_TICKET +"="+getTicket(context))));
        }
        catch (IOException error)
        {
            throw new RuntimeException("Error while redirecting: " + error.getMessage(), error);
        }
        
        // create response object
        ResponseStatus status = new ResponseStatus();
        status.setCode(ResponseStatus.STATUS_MOVED_PERMANENTLY);
        return new Response(status);
    }

    private String getTicket(ConnectorContext context)
    {
        // if this connector is managing session info
        if (getConnectorSession() != null)
        {
            // apply alfresco ticket from connector session - i.e. previous login attempt
            return (String)getConnectorSession().getParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET);
        }
        
        return null;
    }
}
