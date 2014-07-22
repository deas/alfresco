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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implementation of an Authenticator base class.
 * <p>
 * This abstract implementation provides helper methods for post-processing
 * response elements such as headers.  
 * 
 * @see AbstractConnector
 * 
 * @author muzquiano
 * @author Kevin Roast
 */
public abstract class AbstractAuthenticator implements Authenticator, ApplicationContextAware
{
    private static Log logger = LogFactory.getLog(Authenticator.class);
    
    private ApplicationContext applicationContext;
    
    /** RemoteClient base bean used to clone beans for use in Authenticators */
    private static ThreadLocal<RemoteClient> remoteClientBase = new ThreadLocal<RemoteClient>();
    
    
    /**
     * Sets the Spring application context
     * 
     * @param applicationContext    the Spring application context
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }
    
    /**
     * Build a Remote Client instance by retrieving and configuring the "connector.remoteclient" bean.
     * 
     * @param endpoint  Configured Endpoint ID for the remote client instance
     */
    protected RemoteClient buildRemoteClient(String endpoint)
    {
        RemoteClient client = this.remoteClientBase.get();
        if (client == null)
        {
            // get the Remote Client prototype bean from Spring
            if (this.applicationContext == null)
            {
                throw new IllegalStateException("Application Context must be set programatically for Authenticator.");
            }
            client = (RemoteClient)this.applicationContext.getBean("connector.remoteclient");
            if (client == null)
            {
                throw new IllegalStateException("The 'connector.remoteclient' bean is required by the WebScript framework.");
            }
            // set the object used to clone further bean instances
            this.remoteClientBase.set(client);
        }
        try
        {
            // perform the bean clone from the base instance
            client = (RemoteClient)client.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new IllegalStateException("RemoteClient must support clone() method.");
        }
        
        // set the appropriate endpoint ID state for this RemoteClient instance
        client.setEndpoint(endpoint);
        
        return client;
    }
    
    /**
     * Retrieves headers from response and stores onto the Connector Session
     * 
     * @param response
     */
    protected void processResponse(Response response, ConnectorSession connectorSession)
    {
        // look for Set-Cookie header and store values back onto Connector Session
        Map<String, String> headers = response.getStatus().getHeaders();
        Iterator it = headers.keySet().iterator();
        while(it.hasNext())
        {
            String headerName = (String)it.next();
            if (headerName.equalsIgnoreCase("Set-Cookie"))
            {
                String headerValue = (String) headers.get(headerName);
                
                int z = headerValue.indexOf('=');
                if (z > -1)
                {
                    String cookieName = (String) headerValue.substring(0,z);
                    String cookieValue = (String) headerValue.substring(z+1, headerValue.length());
                    int y = cookieValue.indexOf(';');
                    if (y > -1)
                    {
                        cookieValue = cookieValue.substring(0,y);
                    }                    
                    
                    if (logger.isDebugEnabled())
                        logger.debug("Authenticator found Set-Cookie: " + cookieName + " = " + cookieValue);
                    
                    // store cookie back                    
                    if (connectorSession != null)
                    {
                        connectorSession.setCookie(cookieName, cookieValue);
                    }
                }
            }
        }
    }    
}
