/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts.connector;

import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;

/**
 * An implementation of an Alfresco Connector that can be used to conncet
 * to an Alfresco Repository and issue URL invokes against it.
 * <p>
 * The Alfresco Connector extends the HttpConnector and provides the
 * additional functionality of apply authentication (usually a ticket or a
 * cookie) onto the outgoing request.
 * <p>
 * The authentication information is retrieved from the connector session.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class AlfrescoConnector extends HttpConnector
{
    private static final String UNAUTHENTICATED_MODE_GUEST = "guest";
    private static final String PARAM_TICKETNAME_ALF_TICKET = "alf_ticket";

    /**
     * Instantiates a new Alfresco Connector.
     * 
     * @param descriptor the descriptor
     * @param endpoint the endpoint
     */
    public AlfrescoConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.HttpConnector#stampCredentials(org.alfresco.connector.RemoteClient, org.alfresco.connector.ConnectorContext)
     */
    @Override
    protected void applyRequestAuthentication(RemoteClient remoteClient, ConnectorContext context)
    {
        // support for Alfresco ticket-based authentication - retrieving the ticket
        // from the connector context is a special case for Flash based apps that do
        // not share the same session and get at user connector session information
        String alfTicket = null;
        
        if (context != null)
        {
            alfTicket = context.getParameters().get(PARAM_TICKETNAME_ALF_TICKET);
        }
        
        if (getCredentials() != null)
        {
            // if this connector is managing session info
            if (getConnectorSession() != null)
            {
                // apply alfresco ticket from connector session - i.e. previous login attempt
                alfTicket = (String)getConnectorSession().getParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET);
            }
        }
        
        if (alfTicket != null)
        {
            remoteClient.setTicket(alfTicket);
            remoteClient.setTicketName(PARAM_TICKETNAME_ALF_TICKET);
        }
        else
        {
            // otherwise, if we don't have an alfresco ticket we can enter "guest mode"
            String unauthenticatedMode = this.descriptor.getUnauthenticatedMode();
            if (UNAUTHENTICATED_MODE_GUEST.equalsIgnoreCase(unauthenticatedMode))
            {
                remoteClient.setTicketName("guest");
                remoteClient.setTicket("true");
            }
        }
    }
}
