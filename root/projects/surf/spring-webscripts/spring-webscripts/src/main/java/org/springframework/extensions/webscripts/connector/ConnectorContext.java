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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes invocation context that the connector should use
 * when creating the connection to a remote service.
 * <p>
 * Invocation context consists of HTTP request state such as
 * fixed parameters and headers and the HTTP request method.
 * 
 * @author Kevin Roast
 */
public final class ConnectorContext
{
    /** The HTTP request parameters */
    private Map<String, String> parameters = Collections.<String, String>emptyMap();
    
    /** The HTTP request headers */
    private Map<String, String> headers = Collections.<String, String>emptyMap();
    
    /** The request content type */
    private String contentType;
    
    /** The HTTP method */
    private HttpMethod method = HttpMethod.GET;
    
    /** Commit Response on 401 error flag */
    private boolean commitResponse = true;
    
    /** Exception on error else return 500 response code */
    private boolean exceptionOnError = false;
    
    
    /**
     * Instantiates a new connector context.
     */
    public ConnectorContext()
    {
        this(null, null, null);
    }
    
    /**
     * Instantiates a new connector context.
     * 
     * @param parameters the parameters
     * @param headers the headers
     */
    public ConnectorContext(Map<String, String> parameters, Map<String, String> headers)
    {
        this(null, parameters, headers);
    }
    
    /**
     * Instantiates a new connector context.
     * 
     * @param method the HTTP method
     */
    public ConnectorContext(HttpMethod method)
    {
        if (method != null)
        {
            this.method = method;
        }
    }

    /**
     * Instantiates a new connector context.
     * 
     * @param method the HTTP method
     * @param parameters the parameters
     * @param headers the headers
     */
    public ConnectorContext(HttpMethod method, Map<String, String> parameters, Map<String, String> headers)
    {
        if (method != null)
        {
            this.method = method;
        }
        if (parameters != null)
        {
            this.parameters = new HashMap<String, String>(parameters.size());
            this.parameters.putAll(parameters);
        }
        if (headers != null)
        {
            this.headers = new HashMap<String, String>(headers.size());
            this.headers.putAll(headers);
        }
    }
    
    /**
     * Gets the parameters.
     * 
     * @return the parameters
     */
    public Map<String, String> getParameters()
    {
        return this.parameters;
    }
    
    /**
     * Gets the headers.
     * 
     * @return the headers
     */
    public Map<String, String> getHeaders()
    {
        return this.headers;
    }
    
    /**
     * Gets the content type.
     * 
     * @return the content type
     */
    public String getContentType()
    {
        return this.contentType;
    }
    
    /**
     * Sets the content type.
     * 
     * @param contentType the new content type
     */
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    
    /**
     * Gets the method.
     * 
     * @return the method
     */
    public HttpMethod getMethod()
    {
        return this.method;
    }
    
    /**
     * Sets the method.
     * 
     * @param method the new method
     */
    public void setMethod(HttpMethod method)
    {
        if (method != null)
        {
            this.method = method;
        }
    }

    /**
     * @return true to commit the response if a 401 error is returned, false otherwise.
     * Allows for retry of connections that stream to a remote connection after an authentication pass.
     */
    public boolean getCommitResponse()
    {
        return this.commitResponse;
    }

    /**
     * @param commitResponse true to commit the response if a 401 error is returned, false otherwise.
     * Allows for retry of connections that stream to a remote connection after an authentication pass.
     */
    public void setCommitResponseOnAuthenticationError(boolean commitResponse)
    {
        this.commitResponse = commitResponse;
    }
    
    /**
     * @param exceptionOnError  True to throw an exception on a server 500 response - else return 500 code
     * in the usual Response object. This is useful when internal services want to make a remote call but
     * are not interested in dealing with and returing exceptions directly to the user. 
     */
    public void setExceptionOnError(boolean exceptionOnError)
    {
        this.exceptionOnError = exceptionOnError;
    }
    
    public boolean getExceptionOnError()
    {
        return this.exceptionOnError;
    }
}
