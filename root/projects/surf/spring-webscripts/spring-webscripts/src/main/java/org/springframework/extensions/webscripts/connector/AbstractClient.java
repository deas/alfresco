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

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.extensions.surf.exception.WebScriptsPlatformException;

/**
 * Abstract base class for client implementations.
 * <p>
 * A general purpose but very useful RemoteClient implementation
 * is provided that should handle most HTTP(S) connection needs.
 * <p>
 * Client objects manage state between the web script layer and the
 * remote endpoint.
 * <p>
 * Connector objects tell the Client objects what to do and when.
 * They orchestrate the sequence of handshakes and so forth so that
 * the end user or developer doesn't need to worry about the
 * underlying mechanics remoting to the endpoint.
 * 
 * @author muzquiano
 * @author Kevin Roast
 */
public abstract class AbstractClient implements Client
{
    // remote endpoint URL stem
    protected String endpoint;  
    
    /**
     * Set the endpoint for the Client instance
     * 
     * @param endpoint      Endpoint URL stem
     */
    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Client#getEndpoint()
     */
    public String getEndpoint()
    {
        return this.endpoint;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Client#getURL()
     */
    public URL getURL()
    {
        try
        {
            return new URL(this.endpoint);
        }
        catch (MalformedURLException me)
        {
            throw new WebScriptsPlatformException("Unable to parse endpoint as URL: " + this.endpoint);
        }
    }
}
