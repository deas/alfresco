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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;

/**
 * The EndpointManager is responsible for maintaining connection timeout and connection
 * retry information for endpoints. It may be used by multiple Connector objects to
 * ensure that failing endpoints are not repeatedly connected to or waited on.
 * 
 * @author Kevin Roast
 */
public final class EndpointManager
{
    /** timeout value in milliseconds before a reconnection
        to a particular endpoint should be attempted */
    private static final long DEFAULT_RECONNECT_TIMEOUT = 0L;
    
    /** conncurrent map of current endpoint->timeout values */
    private static ConcurrentMap<String, Long> endpointTimeouts = new ConcurrentHashMap<String, Long>();
    /** conncurrent map of endpoint->reconnect values */
    private static ConcurrentMap<String, Long> endpointReconnectValues = new ConcurrentHashMap<String, Long>();
    
    
    /**
     * Private constructor
     */
    private EndpointManager()
    {
    }
    
    
    /**
     * Register an endpoint with the manager - the same endpoint can be registered
     * any number of times by multiple threads without side effects.
     * 
     * @param endpoint      The endpoint to register
     */
    public static void registerEndpoint(String endpoint)
    {
        registerEndpoint(endpoint, null);
    }
    
    /**
     * Register an endpoint with the manager - the same endpoint can be registered
     * any number of times by multiple threads without side effects.
     * 
     * @param endpoint      The endpoint to register
     * @param descriptor    The ConnectorDescriptor used to connect to this endpoint
     *                      Used to retrieve reconnection timeout config.
     */
    public static void registerEndpoint(String endpoint, ConnectorDescriptor descriptor)
    {
        endpointTimeouts.putIfAbsent(endpoint, 0L);
        if (!endpointReconnectValues.containsKey(endpoint))
        {
            long connTimeout = DEFAULT_RECONNECT_TIMEOUT;
            if (descriptor != null)
            {
                String strConnTimeout = descriptor.getReconnectTimeout();
                if (strConnTimeout != null && strConnTimeout.length() != 0)
                {
                    connTimeout = Long.parseLong(strConnTimeout);
                }
            }
            endpointReconnectValues.put(endpoint, connTimeout);
        }
    }
    
    /**
     * Returns true if the connector should make a connection attempt to the specified
     * endpoint, false if the endpoint is still in the "wait" period between retries.
     * 
     * @param endpoint      The endpoint to test
     * 
     * @return true to allow connect, false otherwise
     */
    public static boolean allowConnect(String endpoint)
    {
        return (endpointTimeouts.get(endpoint) + endpointReconnectValues.get(endpoint) <= System.currentTimeMillis());
    }
    
    /**
     * Process the given response code for an endpoint - recording if that remote
     * connection is unavailable for a time. Returns true if further response
     * processing should continue, false otherwise.
     * 
     * @param endpoint      The endpoint to record code against
     * @param code          Response code
     * 
     * @return true if further processing should continue, false otherwise
     */
    public static boolean processResponseCode(String endpoint, int code)
    {
        boolean allowContinue = true;
        
        if (HttpServletResponse.SC_SERVICE_UNAVAILABLE == code ||
            HttpServletResponse.SC_REQUEST_TIMEOUT == code)
        {
            // If special error codes were returned, don't check the remote connection
            // again for a short time. This is to ensure that if an endpoint is not
            // currently available, we don't continually connect+timeout potentially
            // 100's of times in a row therefore slowing the server startup etc. 
            endpointTimeouts.put(endpoint, System.currentTimeMillis());
            allowContinue = false;
        }
        
        return allowContinue;
    }
}