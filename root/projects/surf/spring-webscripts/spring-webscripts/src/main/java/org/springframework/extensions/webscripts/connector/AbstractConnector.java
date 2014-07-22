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

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;

/**
 * Abstract class providing the base implementation for a Connector.
 * <p>
 * Provides implementations for the basic setter and helper functions.
 * <p>
 * The delegate functions is the call() method - this should be overriden
 * to provide the appropriate connector functionality.
 * 
 * @see AbstractAuthenticator
 * 
 * @author Kevin Roast
 */
public abstract class AbstractConnector implements Connector, ApplicationContextAware
{
    private Credentials credentials;
    private ApplicationContext applicationContext;
    
    protected String endpoint;
    protected ConnectorDescriptor descriptor;
    protected ConnectorSession connectorSession;
    
    /** RemoteClient base bean used to clone beans for use in Connectors */
    private static ThreadLocal<RemoteClient> remoteClientBase = new ThreadLocal<RemoteClient>();
    
    
    /**
     * Constructor.
     * 
     * @param descriptor the descriptor
     * @param endpoint the endpoint
     */
    protected AbstractConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        this.descriptor = descriptor;
        this.endpoint = endpoint;
    }
    
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
                throw new IllegalStateException("Application Context must be set programatically for Connector.");
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
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#call(java.lang.String)
     */
    public Response call(String uri)
    {
        return call(uri, null);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#setCredentials(org.alfresco.connector.Credentials)
     */
    public void setCredentials(Credentials credentials)
    {
        this.credentials = credentials;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getCredentials()
     */
    public Credentials getCredentials()
    {
        return credentials;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#setEndpoint(java.lang.String)
     */
    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getEndpoint()
     */
    public String getEndpoint()
    {
        return this.endpoint;
    }    

    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#setConnectorSession(org.alfresco.connector.ConnectorSession)
     */
    public void setConnectorSession(ConnectorSession connectorSession)
    {
        this.connectorSession = connectorSession;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Connector#getConnectorSession()
     */
    public ConnectorSession getConnectorSession()
    {
        return this.connectorSession;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.endpoint + (credentials != null ? (" - " + credentials.toString()) : "");
    }
}
