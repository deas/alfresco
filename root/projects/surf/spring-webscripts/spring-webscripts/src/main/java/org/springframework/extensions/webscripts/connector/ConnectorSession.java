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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Container for Connector "session state".  Session state consists
 * of headers, cookies and parameters that need to be bound onto
 * the connector with subsequent connections.
 * 
 * This class essentially allows for the mimic of Browser-like
 * functionality in terms of subsequent Connectors reusing state
 * from previous Connector responses.
 * 
 * @author muzquiano
 */
public class ConnectorSession implements Serializable
{
    private Map<String, String> parameters = null;
    private Map<String, String> cookies = null;
    private String endpointId;
    
    
    /**
     * Instantiates a new connector session.
     * 
     * @param endpointId the endpoint id
     */
    public ConnectorSession(String endpointId)
    {
        this.endpointId = endpointId;
        this.parameters = new HashMap<String, String>(16, 1.0f);
        this.cookies = new HashMap<String, String>(16, 1.0f);
    }
    
    /**
     * Gets the endpoint id.
     * 
     * @return the endpoint id
     */
    public String getEndpointId()
    {
        return this.endpointId;
    }

    /**
     * Gets a parameter.
     * 
     * @param key the key
     * 
     * @return the parameter
     */
    public String getParameter(String key)
    {
        return this.parameters.get(key);
    }

    /**
     * Sets a given parameter.
     * 
     * @param key the key
     * @param value the value
     */
    public void setParameter(String key, String value)
    {
        this.parameters.put(key, value);
    }
    
    /**
     * Returns the parameter keys.
     * 
     * @return array of parameter keys
     */
    public String[] getParameterKeys()
    {
        return this.parameters.keySet().toArray(new String[this.parameters.size()]);
    }    

    /**
     * Gets a header.
     * 
     * @param name the name
     * 
     * @return the header
     */
    public String getCookie(String name)
    {
        return (String) this.cookies.get(name);
    }

    /**
     * Sets a given header.
     * 
     * @param name the name
     * @param value the header
     */
    public void setCookie(String name, String value)
    {
        this.cookies.put(name, value);
    }

    /**
     * Returns the cookie names.
     * 
     * @return array of cookie names
     */
    public String[] getCookieNames()
    {
        return this.cookies.keySet().toArray(new String[this.cookies.size()]);
    }    
}
