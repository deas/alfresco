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

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.exception.AuthenticationException;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.json.JSONWriter;

/**
 * An implementation of an Alfresco ticket or cookie-based Authenticator.
 * <p>
 * This Authenticator can be plugged into a connector to allow the connector
 * to handshake with an Alfresco Repository. This handshake involves POSTing
 * the username and password to the /api/login WebScript.
 * <p>
 * A ticket or cookie is returned that is then stored in a connector session.
 * 
 * @author muzquiano
 * @author Kevin Roast
 */
public class AlfrescoAuthenticator extends AbstractAuthenticator
{
    private static Log logger = LogFactory.getLog(AlfrescoAuthenticator.class);
    
    private static final String JSON_lOGIN = "'{'\"username\": \"{0}\", \"password\": \"{1}\"'}'";
    private static final String API_LOGIN = "/api/login";
    private static final String MIMETYPE_APPLICATION_JSON = "application/json";
    
    public final static String CS_PARAM_ALF_TICKET = "alfTicket";
    
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractAuthenticator#authenticate(java.lang.String, org.alfresco.connector.Credentials, org.alfresco.connector.ConnectorSession)
     */
    public ConnectorSession authenticate(String endpoint, Credentials credentials, ConnectorSession connectorSession)
            throws AuthenticationException
    {
        ConnectorSession cs = null;
        
        String user, pass;
        if (credentials != null && (user = (String)credentials.getProperty(Credentials.CREDENTIAL_USERNAME)) != null &&
            (pass = (String)credentials.getProperty(Credentials.CREDENTIAL_PASSWORD)) != null)
        {
            // build a new remote client
            RemoteClient remoteClient = buildRemoteClient(endpoint);
            
            if (logger.isDebugEnabled())
                logger.debug("Authenticating user: " + user);
            
            // POST to the Alfresco login WebScript
            remoteClient.setRequestContentType(MIMETYPE_APPLICATION_JSON);
            String body = MessageFormat.format(JSON_lOGIN, JSONWriter.encodeJSONString(user), JSONWriter.encodeJSONString(pass));
            Response response = remoteClient.call(getLoginURL(), body);
            
            // read back the ticket
            if (response.getStatus().getCode() == 200)
            {
                String ticket;
                try
                {
                    JSONObject json = new JSONObject(response.getResponse());
                    ticket = json.getJSONObject("data").getString("ticket");
                } 
                catch (JSONException jErr)
                {
                    // the ticket that came back could not be parsed
                    // this will cause the entire handshake to fail
                    throw new AuthenticationException(
                            "Unable to retrieve login ticket from Alfresco", jErr);
                }
                
                if (logger.isDebugEnabled())
                    logger.debug("Parsed ticket: " + ticket);
                
                // place the ticket back into the connector session
                if (connectorSession != null)
                {
                    connectorSession.setParameter(CS_PARAM_ALF_TICKET, ticket);
                    
                    // signal that this succeeded
                    cs = connectorSession;
                }
            }
            else if (response.getStatus().getCode() == Status.STATUS_NO_CONTENT)
            {
                if (logger.isDebugEnabled())
                    logger.debug("SC_NO_CONTENT(204) status received - retreiving auth cookies...");
                
                // The login created an empty response, probably with cookies in the connectorSession. We succeeded.
                processResponse(response, connectorSession);
                cs = connectorSession;
            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.debug("Authentication failed, received response code: " + response.getStatus().getCode());            
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("No user credentials available - cannot authenticate.");
        }
        
        return cs;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractAuthenticator#isAuthenticated(java.lang.String, org.alfresco.connector.ConnectorSession)
     */
    public boolean isAuthenticated(String endpoint, ConnectorSession connectorSession)
    {
        return (connectorSession.getParameter(CS_PARAM_ALF_TICKET) != null) ||
               (connectorSession.getCookieNames().length != 0);
    }
    
    /**
     * @return the REST URL to be used for login requests
     */
    protected String getLoginURL()
    {
        return API_LOGIN;
    }
}