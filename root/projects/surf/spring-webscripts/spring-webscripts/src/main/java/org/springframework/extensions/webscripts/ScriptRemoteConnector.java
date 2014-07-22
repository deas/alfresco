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

package org.springframework.extensions.webscripts;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.surf.exception.WebScriptsPlatformException;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptParameter;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Describes a connector to a remote endpoint.
 * 
 * This is a wrapper around the true connector object and it provides
 * Script-style interfaces for working with buffered response strings
 * and the like.
 * 
 * @author muzquiano
 * @author kevinr
 */
@ScriptClass 
(
        help="Describes a connector to a remote endpoint.",
        code="// get a connector to the Alfresco repository endpoint\nvar connector = remote.connect(\"alfresco\"); \n// retrieve the web script index page \nvar indexHtml = connector.get(\"/index\");",
        types=
        {
                ScriptClassType.JavaScriptAPI
        }
)
public final class ScriptRemoteConnector
{
    private static final Log logger = LogFactory.getLog(ScriptRemote.class);
    
    final private Connector connector;
    final private EndpointDescriptor descriptor; 
    
    
    /**
     * Constructor
     * 
     * @param connector     The Connector to wrap
     * @param descriptor    The description of the endpoint this connector is managing
     */
    public ScriptRemoteConnector(Connector connector, EndpointDescriptor descriptor)
    {
        this.connector = connector;
        this.descriptor = descriptor;
    }
    
    
    /**
     * Invokes a URI on the endpoint via a GET request.
     * 
     * @param uri the uri
     * 
     * @return Response object from the call {@link Response}
     */
    @ScriptMethod
    (
            help="Invokes a URI on the endpoint via a GET request.",
            output="Response object from the call"
    )
    public Response call(@ScriptParameter(help="Endpoint Url") String uri)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        return this.connector.call(uri, context);
    }
    
    /**
     * Invokes a GET request URI on the endpoint.
     * 
     * @param uri the uri
     * 
     * @return Response object from the call {@link Response}
     */
    @ScriptMethod
    (
            help="Invokes a GET request URI on the endpoint.",
            output="Response object from the call"
    )
    public Response get(@ScriptParameter(help="Endpoint Url") String uri)
    {
        return call(uri);
    }
    
    /**
     * Invokes a URI on a remote service, passing the supplied body as a POST request.
     * 
     * @param uri    Uri to call on the endpoint
     * @param body   Body of the POST request.
     * 
     * @return Response object from the call {@link Response}
     */
    @ScriptMethod
    (
            help="Invokes a URI on a remote service, passing the supplied body as a POST request.",
            output="Response object from the call"
    )
    public Response post(@ScriptParameter(help="Endpoint Url") String uri, @ScriptParameter(help="Body of the POST request.") String body)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        context.setMethod(HttpMethod.POST);
        try
        {
            return this.connector.call(uri, context, new ByteArrayInputStream(body.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException err)
        {
            throw new WebScriptsPlatformException("Unsupported encoding.", err);
        }
    }
    
    /**
     * Invokes a URI on a remote service, passing the supplied body as a POST request.
     * 
     * @param uri    Uri to call on the endpoint
     * @param body   Body of the POST request.
     * @param contentType   Content mimetype of the request body
     * 
     * @return Response object from the call {@link Response}
     */
    @ScriptMethod
    (
            help="Invokes a URI on a remote service, passing the supplied body as a POST request.",
            output="Response object from the call"
    )
    public Response post(@ScriptParameter(help="Endpoint Url") String uri, @ScriptParameter(help="Body of the POST request.") String body, @ScriptParameter(help="Content mimetype of the request body") String contentType)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        context.setMethod(HttpMethod.POST);
        context.setContentType(contentType);
        try
        {
            return this.connector.call(uri, context, new ByteArrayInputStream(body.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException err)
        {
            throw new WebScriptsPlatformException("Unsupported encoding.", err);
        }
    }
    
    /**
     * Invokes a URI on a remote service, passing the supplied body as a PUT request.
     * 
     * @param uri    Uri to call on the endpoint
     * @param body   Body of the PUT request.
     * 
     * @return Response object from the call {@link Response}
     */
    @ScriptMethod
    (
            help="Invokes a URI on a remote service, passing the supplied body as a PUT request.",
            output="Response object from the call"
    )
    public Response put(@ScriptParameter(help="Endpoint Url") String uri, @ScriptParameter(help="Body of the PUT request.") String body)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        context.setMethod(HttpMethod.PUT);
        try
        {
            return this.connector.call(uri, context, new ByteArrayInputStream(body.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException err)
        {
            throw new WebScriptsPlatformException("Unsupported encoding.", err);
        }
    }
    
    /**
     * Invokes a URI on a remote service, passing the supplied body as a PUT request.
     * 
     * @param uri    Uri to call on the endpoint
     * @param body   Body of the PUT request.
     * @param contentType   Content mimetype of the request
     * 
     * @return Response object from the call {@link Response}
     */
    @ScriptMethod
    (
            help="Invokes a URI on a remote service, passing the supplied body as a PUT request.",
            output="Response object from the call"
    )
    public Response put(@ScriptParameter(help="Endpoint Url") String uri, @ScriptParameter(help="Body of the PUT request.") String body, @ScriptParameter(help="Content mimetype of the request.") String contentType)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        context.setMethod(HttpMethod.PUT);
        context.setContentType(contentType);
        try
        {
            return this.connector.call(uri, context, new ByteArrayInputStream(body.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException err)
        {
            throw new WebScriptsPlatformException("Unsupported encoding.", err);
        }
    }
    
    /**
     * Invokes a URI on a remote service as DELETE request.
     * 
     * NOTE: the name of the method is 'del' not 'delete' so as to not
     * interfere with JavaScript Object.delete() method.
     * 
     * @param uri    Uri to call on the endpoint
     * 
     * @return Response object from the call {@link Response}
     */
    @ScriptMethod
    (
            help="Invokes a URI on a remote service as DELETE request.\nNOTE: the name of the method is 'del' not 'delete' so as to not\ninterfere with JavaScript Object.delete() method.",
            output="Response object from the call"
    )
    public Response del(@ScriptParameter(help="Endpoint Url") String uri)
    {
        ConnectorContext context = new ConnectorContext(null, buildDefaultHeaders());
        context.setMethod(HttpMethod.DELETE);
        return this.connector.call(uri, context);
    }
    
    /**
     * Returns the endpoint string
     * 
     * @return endpoint
     */
    @ScriptMethod
    (
            help="Returns the endpoint string",
            output="Endpoint"
    )
    public String getEndpoint()
    {
        return this.connector.getEndpoint();
    }
    
    /**
     * @return the endpoint descriptor object
     */
    @ScriptMethod
    (
            help="Returns the endpoint descriptor",
            output="Endpoint descriptor"
    )
    public EndpointDescriptor getDescriptor()
    {
        return this.descriptor;
    }
    
    
    /**
     * Helper to build a map of the default headers for script requests - we send over
     * the current users locale so it can be respected by any appropriate REST APIs.
     *  
     * @return map of headers
     */
    private static Map<String, String> buildDefaultHeaders()
    {
        Map<String, String> headers = new HashMap<String, String>(1, 1.0f);
        headers.put("Accept-Language", I18NUtil.getLocale().toString().replace('_', '-'));
        return headers;
    }
}
