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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;

/**
 * Basic Connector implementation that can be used to perform simple HTTP and
 * HTTP communication with a remote endpoint. This connector supports basic HTTP
 * authentication via the RemoteClient.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class HttpConnector extends AbstractConnector
{
    private static Log endpointLogger = LogFactory.getLog(EndpointManager.class);
    private static Log logger = LogFactory.getLog(HttpConnector.class);
    
    
    /**
     * Instantiates a new http connector.
     * 
     * @param descriptor the descriptor
     * @param endpoint the endpoint
     */
    public HttpConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
        EndpointManager.registerEndpoint(endpoint, descriptor);
    }


    public Response call(String uri, ConnectorContext context)
    {
        if (logger.isDebugEnabled())
            logger.debug("Requested Method: " + (context != null ? context.getMethod() : "GET") + " URI: " + uri);
        Response response;
        if (EndpointManager.allowConnect(this.endpoint))
        {
            RemoteClient remoteClient = initRemoteClient(context);
            
            // call client and process response
            response = remoteClient.call(uri);
            processResponse(remoteClient, response);
        }
        else
        {
            if (endpointLogger.isInfoEnabled())
                endpointLogger.info("Throttled call to: " + uri + " waiting for reconnect timeout on: " + this.endpoint);
            ResponseStatus status = new ResponseStatus();
            status.setCode(ResponseStatus.STATUS_INTERNAL_SERVER_ERROR);
            response = new Response(status);
        }
        return response;
    }

    public Response call(String uri, ConnectorContext context, InputStream in)
    {
        if (logger.isDebugEnabled())
            logger.debug("Requested Method: " + (context != null ? context.getMethod() : "GET") + " URI: " + uri);
        Response response;
        if (EndpointManager.allowConnect(this.endpoint))
        {
            RemoteClient remoteClient = initRemoteClient(context);
            
            // call client and process response
            response = remoteClient.call(uri, in);
            processResponse(remoteClient, response);
        }
        else
        {
            if (endpointLogger.isInfoEnabled())
                endpointLogger.info("Throttled call to: " + uri + " waiting for reconnect timeout on: " + this.endpoint);
            ResponseStatus status = new ResponseStatus();
            status.setCode(ResponseStatus.STATUS_INTERNAL_SERVER_ERROR);
            response = new Response(status);
        }
        return response;
    }

    public Response call(String uri, ConnectorContext context, InputStream in, OutputStream out)
    {
        if (logger.isDebugEnabled())
            logger.debug("Requested Method: " + (context != null ? context.getMethod() : "GET") + " URI: " + uri);
        Response response;
        if (EndpointManager.allowConnect(this.endpoint))
        {
            RemoteClient remoteClient = initRemoteClient(context);
            
            // call client and process response
            response = remoteClient.call(uri, in, out);
            processResponse(remoteClient, response);
        }
        else
        {
            if (endpointLogger.isInfoEnabled())
                endpointLogger.info("Throttled call to: " + uri + " waiting for reconnect timeout on: " + this.endpoint);
            ResponseStatus status = new ResponseStatus();
            status.setCode(ResponseStatus.STATUS_INTERNAL_SERVER_ERROR);
            response = new Response(status);
        }
        
        return response;
    }
    
    public Response call(String uri, ConnectorContext context, HttpServletRequest req, HttpServletResponse res)
    {
        if (logger.isDebugEnabled())
            logger.debug("Requested Method: " + (context != null ? context.getMethod() : "GET") + " URI: " + uri);
        Response response;
        if (EndpointManager.allowConnect(this.endpoint))
        {
            RemoteClient remoteClient = initRemoteClient(context);
            
            // call client and process response
            response = remoteClient.call(uri, req, res);
            processResponse(remoteClient, response);
        }
        else
        {
            if (endpointLogger.isInfoEnabled())
                endpointLogger.info("Throttled call to: " + uri + " waiting for reconnect timeout on: " + this.endpoint);
            ResponseStatus status = new ResponseStatus();
            status.setCode(ResponseStatus.STATUS_INTERNAL_SERVER_ERROR);
            response = new Response(status);
        }
        
        return response;
    }
    
    /**
     * Stamps headers onto the remote client
     * 
     * @param remoteClient
     * @param context
     */
    protected void applyRequestHeaders(RemoteClient remoteClient, ConnectorContext context)
    {
        // copy in cookies that have been stored back as part of the connector session
        ConnectorSession connectorSession = getConnectorSession();
        if (connectorSession != null)
        {
            Map<String, String> cookies = new HashMap<String, String>(8);
            for (String cookieName : connectorSession.getCookieNames())
            {
                cookies.put(cookieName, connectorSession.getCookie(cookieName));
            }
            remoteClient.setCookies(cookies);
        }
        
        // get the headers
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
        }
        
        // stamp all headers onto the remote client
        if (headers.size() != 0)
        {
            remoteClient.setRequestProperties(headers);
        }
    }
    
    /**
     * Stamps Credentials values onto the remote client
     * 
     * @param remoteClient
     */
    protected void applyRequestAuthentication(RemoteClient remoteClient, ConnectorContext context)
    {
        // support for basic authentication (HTTP basic auth is performed by the RemoteClient)
        if (getCredentials() != null)
        {
            String user = (String) getCredentials().getProperty(Credentials.CREDENTIAL_USERNAME);
            String pass = (String) getCredentials().getProperty(Credentials.CREDENTIAL_PASSWORD);
            if (pass != null)
            {
                remoteClient.setUsernamePassword(user, pass);
            }
        }
    }
    
    /**
     * Retrieves cookie headers from response and stores back onto the Connector Session
     * 
     * @param response
     */
    protected void processResponse(RemoteClient remoteClient, Response response)
    {
        ConnectorSession connectorSession = getConnectorSession();
        if (EndpointManager.processResponseCode(this.endpoint, response.getStatus().getCode()) &&
            connectorSession != null)
        {
            Map<String, String> cookies = remoteClient.getCookies();
            for (Map.Entry<String, String> cookie : cookies.entrySet())
            {
                // store cookie back
                if (logger.isDebugEnabled())
                    logger.debug("Connector found Set-Cookie: " + cookie.getKey() + " = " + cookie.getValue());
                
                connectorSession.setCookie(cookie.getKey(), cookie.getValue());
            }
        }
    }
    
    /**
     * Init the RemoteClient object based on the Connector Context.
     * Applies Request headers and authentication as required.
     * 
     * @return RemoteClient
     */
    protected RemoteClient initRemoteClient(ConnectorContext context)
    {
        // create a remote client
        RemoteClient remoteClient = buildRemoteClient(getEndpoint());
        
        // configure the client
        if (context != null)
        {
            remoteClient.setRequestContentType(context.getContentType());
            remoteClient.setRequestMethod(context.getMethod());
            remoteClient.setCommitResponseOnAuthenticationError(context.getCommitResponse());
            remoteClient.setExceptionOnError(context.getExceptionOnError());
        }
        
        // stamp headers onto the remote client
        applyRequestHeaders(remoteClient, context);
        
        // stamp credentials onto the remote client
        applyRequestAuthentication(remoteClient, context);
        
        return remoteClient;
    }
}