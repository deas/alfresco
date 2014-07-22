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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Describes a connector to a remote endpoint for a given user.
 * <p>
 * All connectors are scoped to a particular user and a particular
 * endpoint. Create new connectors for new users and new endpoints.
 * <p>
 * All calls using a connector will be stamped with a user's
 * connector credentials.  These connector credentials usually consist
 * of things like cookies, tokens, additional request parameters and
 * other HTTP request state.
 * <p>
 * The caller does not have to pass this data manually.  It is managed
 * for the developer by the underlying ConnectorService during the
 * factory construction of Connector objects.
 * <p>
 * If a connector is constructed without user information then no credential
 * information is passed through - the connections are anonymous.
 * 
 * @author muzquiano
 * @author kevinr
 */
public interface Connector
{
    /**
     * Invokes a URI on a remote service.
     * 
     * The response data is buffered into a data element on the returned
     * object of type Response.
     * 
     * @param uri the uri
     * 
     * @return the response
     */
    public Response call(String uri);

    /**
     * Invokes a URI on a remote service.
     * If the context is null, then it will not be utilized.
     * 
     * The response data is buffered into a data element on the returned
     * object of type Response.
     * 
     * @param uri the uri
     * @param context the context of the invoke
     * 
     * @return the response
     */
    public Response call(String uri, ConnectorContext context);

    /**
     * Invokes a URI on a remote service, passing the input as supplied via
     * a POST/PUT.
     * 
     * If the context is null, then it will not be utilized and POST will
     * be assumed.
     * 
     * The response data is buffered into a data element on the returned
     * object of type Response.
     * 
     * @param uri the uri
     * @param context the context of the invoke
     * @param in the input stream
     * 
     * @return the response
     */    
    public Response call(String uri, ConnectorContext context, InputStream in);
    
    /**
     * Invokes a URI on a remote service.  Data is streamed back from the
     * response into the provided output stream.  Headers and response state
     * is maintained on the Response object.
     * 
     * If the context is null, then it will not be utilized.
     * 
     * The response data is not buffered
     * 
     * @param uri the uri
     * @param context the context of the invoke
     * @param in the input stream
     * @param out the output stream
     * 
     * @return the response
     */    
    public Response call(String uri, ConnectorContext context, InputStream in, OutputStream out);

    /**
     * Invokes a URI on a remote service and streams back results to the
     * provided response object.  This method makes sure that the full
     * response is propagated into the servlet response, including headers,
     * exception states and more.
     * 
     * If the context is null, then it will not be utilized.
     * 
     * The response data is not buffered.
     * 
     * @param uri the uri
     * @param context the context of the invoke
     * @param req Request to proxy from
     * @param res Response to proxy onto
     * 
     * @return the response
     */
    public Response call(String uri, ConnectorContext context, HttpServletRequest req, HttpServletResponse res);

    /**
     * Binds Credentials to this connector.
     * 
     * @param credentials the new credentials
     */
    public void setCredentials(Credentials credentials);

    /**
     * Returns the credents for this connector.
     * 
     * @return the credentials
     */
    public Credentials getCredentials();

    /**
     * Sets the endpoint.
     * 
     * @param endpoint the new endpoint
     */
    public void setEndpoint(String endpoint);

    /**
     * Returns the endpoint to which this connector connects.
     * 
     * @return endpoint the endpoint
     */
    public String getEndpoint();
    
    /**
     * Sets the connector session
     * 
     * @param connectorSession
     */
    public void setConnectorSession(ConnectorSession connectorSession);
    
    /**
     * Returns the connector session
     * 
     * @return the connector session
     */
    public ConnectorSession getConnectorSession();    
}
