/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.web.site.servlet;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;
import org.springframework.extensions.webscripts.connector.AlfrescoConnector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorSession;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.RemoteClient;

/**
 * Extends the {@link AlfrescoConnector} to allow the connection from Share
 * to the Alfresco Repository to use a configurable HTTP header for the
 * authenticated user name. Allows both Share and Repository to use the same
 * HTTP header, when an external SSO is unable to provide the user name in the
 * default Alfresco Repository header {@code "X-Alfresco-Remote-User"}.
 * <p>
 * The user name from the header is also used by {@link SSOAuthenticationFilter}
 * for incoming request to Share to return the user name from
 * {@link HttpServletRequest#getRemoteUser}.
 * <p>
 * The name of the header to be used is defined in the userHeader element of the
 * Alfresco Connector definition (see share-config-custom.xml.sample). Also note
 * the class element specifies this class. For example:
 * <pre>
 * &lt;connector&gt;
 *   &lt;id&gt;alfrescoCookie&lt;/id&gt;
 *   &lt;name&gt;Alfresco Connector&lt;/name&gt;
 *   &lt;description&gt;Connects to an Alfresco instance using cookie-based authentication&lt;/description&gt;
 *   &lt;class&gt;org.alfresco.web.site.servlet.SlingshotAlfrescoConnector&lt;/class&gt;
 *   &lt;userHeader&gt;SsoUserHeader&lt;/userHeader&gt;
 * &lt;/connector&gt;
 * </pre>
 * This class does not suppress sending the user name in the default Alfresco Repository header
 * {@code "X-Alfresco-Remote-User"} but will also send the user name in the configurable
 * header when it has been configured.
 * <p>
 * The Alfresco global property {@code external.authentication.proxyHeader} still needs to
 * be configured on the Repository side to define which header will be used. For example:
 * <pre>
 * authentication.chain=MySso:external,alfrescoNtlm1:alfrescoNtlm
 * external.authentication.proxyUserName=
 * external.authentication.proxyHeader=SsoUserHeader
 * </pre>
 * 
 * When using the default Alfresco Repository header (X-Alfresco-Remote-User") Share and the
 * Alfresco Repository must be protected against direct access from other clients. The same is
 * true when using a configurable header. The reason is that Share and Alfresco just accept the
 * header value as valid. Without this protection, it would be possible to log in as any user
 * simply by setting the header.
 * 
 * @author adavis
 */
public class SlingshotAlfrescoConnector extends AlfrescoConnector
{
    /**
     * The name of the element in the {@link ConnectorDescriptor} 
     * ({@code <connector>...<userHeader>...</userHeader></connector>}) that
     * contains the name of the HTTP header used by an external SSO
     * to provide the authenticated user name. 
     */
    private static final String CD_USER_HEADER = "userHeader";
    
    /**
     * The name of the property in the {@link ConnectorSession} that
     * contains the name of the HTTP header used by an external SSO
     * to provide the authenticated user name. 
     */
    public static final String CS_PARAM_USER_HEADER = "userHeader";
    
    public SlingshotAlfrescoConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }
    
    private String getUserHeader()
    {
        String userHeader = descriptor.getStringProperty(CD_USER_HEADER);
        if (userHeader != null && userHeader.trim().length() == 0)
        {
            userHeader = null;
        }
        return userHeader;
    }
    
    /**
     * Overrides super method to set the CS_PARAM_USER_HEADER. This method is
     * always called at the end of {@link ConnectorService#getConnector} when
     * it constructs a {@link Connector}.
     */
    @Override
    public void setConnectorSession(ConnectorSession connectorSession)
    {
        super.setConnectorSession(connectorSession);
        connectorSession.setParameter(CS_PARAM_USER_HEADER, getUserHeader());
    }

    /**
     * Overrides the super method to add the HTTP header used by an external SSO
     * to provide the authenticated user name when calling alfresco from share.
     */
    protected void applyRequestHeaders(RemoteClient remoteClient, ConnectorContext context)
    {
        // Need to override the headers set on the remoteClient to include the 'userHeader'
        // The following duplicates much of the code in the super method. Creating a new
        // context with the userGeader is even more complex.
        super.applyRequestHeaders(remoteClient, context);
        
        Map<String, String> headers = new HashMap<String, String>(8);
        if (context != null)
        {
            headers.putAll(context.getHeaders());
        }
        
        // Proxy the authenticated user name if we have password-less credentials (indicates SSO auth over a secure
        // connection)
        if (getCredentials() != null)
        {
            String user = (String) getCredentials().getProperty(Credentials.CREDENTIAL_USERNAME);
            String pass = (String) getCredentials().getProperty(Credentials.CREDENTIAL_PASSWORD);
            if (pass == null)
            {
                headers.put("X-Alfresco-Remote-User", user);
            }
            String userHeader = getUserHeader();
            if (userHeader != null)
            {
                headers.put(userHeader, user);
            }
        }
        
        // stamp all headers onto the remote client
        if (headers.size() != 0)
        {
            remoteClient.setRequestProperties(headers);
        }
    }
}
