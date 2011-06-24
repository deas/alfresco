package org.alfresco.web.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;
import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;
import org.springframework.extensions.webscripts.connector.AlfrescoConnector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.Response;

public class ActivitiAdminConnector extends AlfrescoConnector
{
    public static final String SET_COOKIES_HEADER = "Set-Cookie";
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
        
        // TODO: Return dummy response, cannot create/subclass Response due to constructor visibility
        return null;
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
