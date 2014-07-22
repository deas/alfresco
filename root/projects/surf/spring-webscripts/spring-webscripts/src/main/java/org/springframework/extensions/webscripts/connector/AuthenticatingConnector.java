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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.exception.AuthenticationException;

/**
 * An implementation of an Authenticating Connector.
 * <p>
 * The AuthenticatingConnector is a wrapper around a Connector object
 * and an Authenticator object.  It appears as a Connector to the
 * outside world but provides additional functionality.
 * <p>
 * When a call is made, the underlying connector is used to call over
 * to the resource.  The underlying connector retrieves cookie state
 * from the connector session (if available) and attempts to access the
 * remote resource.
 * <p>
 * If this succeeds, then the AuthenticatingConnector returns this response.
 * <p>
 * On the other hand, if this fails (i.e. it receives a 401 unauthorized
 * response), the AuthenticatingConnector calls into the underlying
 * Authenticator instance to perform an "authentication handshake".
 * <p>
 * This handshake retrieves the necessary cookies or tokens and places
 * them into the connector session.  The connector session is persisted
 * to the session (if it was originally bound to the session).
 * <p>
 * The AuthenticatingConnector then reattempts the connection using the
 * newly retrieved cookies or tokens.  If a 401 is received again, the
 * credentials are assumed to be invalid.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class AuthenticatingConnector implements Connector
{
    private static Log endpointLogger = LogFactory.getLog(EndpointManager.class);
    protected static Log logger = LogFactory.getLog(AuthenticatingConnector.class);
    protected Connector connector = null;
    protected Authenticator authenticator = null;
    
    /**
     * Instantiates a new authenticating connector.
     * 
     * @param connector the connector
     * @param authenticator the authenticator
     */
    public AuthenticatingConnector(Connector connector, Authenticator authenticator)
    {
        this.connector = connector;
        this.authenticator = authenticator;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String)
     */
    public Response call(String uri)
    {
        Response response = null;
        boolean handshake = false;
        boolean firstcall = true;
        
        if (isAuthenticated())
        {
            // try to call into the connector to see if we can successfully do this
            response = this.connector.call(uri);
            firstcall = false;
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on first call to: " + uri);
            
            // if there was an authentication challenge, handle here
            if (response.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
            {
                handshake = true;
            }
        }
        else
        {
            handshake = true;
        }
        
        if (handshake)
        {
            handshake(); // ignore result
            
            // now that we've authenticated, try again
            response = this.connector.call(uri);
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on " +
                        (firstcall ? "first" : "second") + " call to: " + uri);
        }
        
        return response;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext)
     */
    public Response call(String uri, ConnectorContext context)
    {
        Response response = null;
        boolean handshake = false;
        boolean firstcall = true;
        
        if (isAuthenticated())
        {
            // try to call into the connector to see if we can successfully do this
            response = this.connector.call(uri, context);
            firstcall = false;
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on first call to: " + uri);
            
            // if there was an authentication challenge, handle here
            if (response.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
            {
                handshake = true;
            }
        }
        else
        {
            handshake = true;
        }
        
        if (handshake)
        {
            handshake(); // ignore result
            
            // now that we've authenticated, try again
            response = this.connector.call(uri, context);
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on " +
                        (firstcall ? "first" : "second") + " call to: " + uri);
        }

        return response;        
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext, java.io.InputStream)
     */
    public Response call(String uri, ConnectorContext context, InputStream in)
    {
        Response response = null;
        boolean handshake = false;
        boolean firstcall = true;
        
        if (isAuthenticated())
        {
            // try to call into the connector to see if we can successfully do this
            response = this.connector.call(uri, context, in);
            firstcall = false;
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on first call to: " + uri);
            
            // if there was an authentication challenge, handle here
            if (response.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
            {
                handshake = true;
            }
        }
        else
        {
            handshake = true;
        }
        
        if (handshake)
        {
            handshake(); // ignore result

            // now that we've authenticated, try again
            if (in.markSupported())
            {
                try
                {
                    in.reset();
                }
                catch (IOException ioErr)
                {
                    // if we cannot reset the stream - there's nothing else we can do
                }
            }
            response = this.connector.call(uri, context, in);
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on " +
                        (firstcall ? "first" : "second") + " call to: " + uri);
        }
        
        return response;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext, java.io.InputStream, java.io.OutputStream)
     */
    public Response call(String uri, ConnectorContext context, InputStream in, OutputStream out)
    {
        Response response = null;
        boolean handshake = false;
        boolean firstcall = true;
        
        if (isAuthenticated())
        {
            // try to call into the connector to see if we can successfully do this
            context.setCommitResponseOnAuthenticationError(false);
            response = this.connector.call(uri, context, in, out);
            firstcall = false;
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on first call to: " + uri);
            
            // if there was an authentication challenge, handle here
            if (response.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
            {
                handshake = true;
            }
        }
        else
        {
            handshake = true;
        }
        
        if (handshake)
        {
            handshake(); // ignore result
            
            // now that we've authenticated, try again
            if (in.markSupported())
            {
                try
                {
                    in.reset();
                }
                catch (IOException ioErr)
                {
                    // if we cannot reset the stream - there's nothing else we can do
                }
            }
            context.setCommitResponseOnAuthenticationError(true);
            response = this.connector.call(uri, context, in, out);
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on " +
                        (firstcall ? "first" : "second") + " call to: " + uri);
        }
        
        return response;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String, org.alfresco.connector.ConnectorContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public Response call(String uri, ConnectorContext context, HttpServletRequest req, HttpServletResponse res)
    {
        Response response = null;
        boolean handshake = false;
        boolean firstcall = true;
        
        if (isAuthenticated())
        {
            // try to call into the connector to see if we can successfully do this
            context.setCommitResponseOnAuthenticationError(false);
            response = this.connector.call(uri, context, req, res);
            firstcall = false;
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on first call to: " + uri);
            
            // if there was an authentication challenge, handle here
            if (response.getStatus().getCode() == ResponseStatus.STATUS_UNAUTHORIZED)
            {
                handshake = true;
            }
        }
        else
        {
            handshake = true;
        }
        
        if (handshake)
        {
            handshake(); // ignore result
            
            // now that we've authenticated, try again
            context.setCommitResponseOnAuthenticationError(true);
            response = this.connector.call(uri, context, req, res);
            
            if (logger.isDebugEnabled())
                logger.debug("Received " + response.getStatus().getCode() + " on " +
                        (firstcall ? "first" : "second") + " call to: " + uri);
        }
        
        return response;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#setCredentials(org.alfresco.connector.Credentials)
     */
    public void setCredentials(Credentials credentials)
    {
        this.connector.setCredentials(credentials);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getCredentials()
     */
    public Credentials getCredentials()
    {
        return this.connector.getCredentials();
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#setEndpoint(java.lang.String)
     */
    public void setEndpoint(String endpoint)
    {
        this.connector.setEndpoint(endpoint);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getEndpoint()
     */
    public String getEndpoint()
    {
        return connector.getEndpoint();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractConnector#setConnectorSession(org.alfresco.connector.ConnectorSession)
     */
    public void setConnectorSession(ConnectorSession connectorSession)
    {
        this.connector.setConnectorSession(connectorSession);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.AbstractConnector#getConnectorSession()
     */
    public ConnectorSession getConnectorSession()
    {
        return this.connector.getConnectorSession();
    }
    
    /**
     * Returns whether the current session is authenticated already.
     * 
     * @return true, if checks if is authenticated
     */
    protected boolean isAuthenticated()
    {
        return this.authenticator.isAuthenticated(getEndpoint(), getConnectorSession());        
    }
    
    
    /**
     * Performs the authentication handshake.
     * 
     * @return true, if successful
     */
    final public boolean handshake()
    {
        boolean success = false;
        
        if (logger.isDebugEnabled())
            logger.debug("Performing authentication handshake");
        
        if (EndpointManager.allowConnect(getEndpoint()))
        {
            ConnectorSession cs = null;
            try
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Authentication handshake using credentials: " + getCredentials());
                    logger.debug("Authentication handshake using connectorSession: " + getConnectorSession());
                }
                
                cs = this.authenticator.authenticate(getEndpoint(), getCredentials(), getConnectorSession());
            }
            catch (AuthenticationException ae)
            {
                logger.error("An exception occurred while attempting authentication handshake for endpoint: " + getEndpoint(), ae);
            }
            if (cs != null)
            {
                this.setConnectorSession(cs);
                success = true;
            }
        }
        else
        {
            if (endpointLogger.isInfoEnabled())
                endpointLogger.info("Throttled authentication handshake, waiting for reconnect timeout on: " + getEndpoint());
        }
        
        return success;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.connector.toString();
    }
}
