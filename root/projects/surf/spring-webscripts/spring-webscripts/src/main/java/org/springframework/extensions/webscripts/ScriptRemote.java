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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.surf.exception.ConnectorProviderException;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptParameter;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorProvider;
import org.springframework.extensions.webscripts.connector.ConnectorProviderImpl;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Root-scope class that provides useful functions for working with endpoints,
 * connectors and credentials.
 * <p>
 * This class also implements methods from the Connector interface so as to
 * allow application developers to use it straight away against the configured
 * default endpoint.
 * 
 * @author muzquiano
 * @author Kevin Roast
 */
@ScriptClass 
(
        help="Provides useful functions for working with endpoints, connectors and credentials.",
        code="// get a connector to the Alfresco repository endpoint\nvar connector = remote.connect(\"alfresco\");\n// retrieve the web script index page \nvar indexHtml = connector.get(\"/index\");",
        types=
        {
                ScriptClassType.JavaScriptRootObject
        }
)
public class ScriptRemote
{
    private static final Log logger = LogFactory.getLog(ScriptRemote.class);

    private ConnectorService connectorService;
    private ConnectorProvider connectorProvider;

    /**
     * Sets the Connector Service.
     * 
     * @param connectorService
     */
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }

    /**
     * Sets the connector provider.
     * 
     * @param connectorProvider
     */
    public void setConnectorProvider(ConnectorProvider connectorProvider)
    {
        this.connectorProvider = connectorProvider;
    }

    /**
     * Constructs a remote connector to a default endpoint (if configured).
     * If a default endpoint is not configured, null will be returned.
     * 
     * @return the remote client
     */
    @ScriptMethod
    (
            help="Constructs a remote connector to a default endpoint (if configured).\nIf a default endpoint is not configured, null will be returned.",
            output="The remote client"
    )
    public ScriptRemoteConnector connect()
    {
        ScriptRemoteConnector remoteConnector = null;

        // Check whether a remote configuration has been provided
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if (remoteConfig != null)
        {
            // See if we have a default endpoint id
            String defaultEndpointId = remoteConfig.getDefaultEndpointId();
            if (defaultEndpointId != null)
            {
                // Construct for this endpoint id
                remoteConnector = connect(defaultEndpointId);
            }
        }

        return remoteConnector;
    }

    /**
     * Constructs a RemoteClient to a specific endpoint. If the endpoint does
     * not exist, null is returned.
     * 
     * @param endpointId the endpoint id
     * 
     * @return the remote client
     */
    @ScriptMethod
    (
            help="Constructs a remote connector to a specific endpoint (if configured).\nIf a default endpoint is not configured, null will be returned.",
            output="The remote client"
    )
    public ScriptRemoteConnector connect(@ScriptParameter(help="Endpoint Url") String endpointId)
    {
        ScriptRemoteConnector remoteConnector = null;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if (remoteConfig != null)
        {
            // check whether we have a descriptor for this endpoint
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(endpointId);
            if (descriptor == null)
            {
                logger.error("No EndPoint descriptor configuration found for ID: " + endpointId);
            }
            else
            {
                // if a connector provider has not been assigned, we can use a
                // default provider which provides simple stateless access
                if (connectorProvider == null)
                {
                    connectorProvider = new ConnectorProviderImpl();                    
                }
                
                try
                {
                    // construct a connector to this endpoint
                    Connector connector = connectorProvider.provide(endpointId);
                    remoteConnector = new ScriptRemoteConnector(connector, descriptor);
                }
                catch (ConnectorProviderException cpe)
                {
                    logger.error("Unable to provision connector for endpoint: " + endpointId);
                }
            }
        }

        return remoteConnector;
    }

    /**
     * Invoke a specific URI on the default endpoint
     * 
     * @param uri the uri
     * 
     * @return the response
     */
    @ScriptMethod
    (
            help="Invoke a specific URI on the default endpoint",
            output="The reponse"
    )
    public Response call(@ScriptParameter(help="Endpoint Url") String uri)
    {
        return this.connect().call(uri);
    }

    /**
     * Returns a list of the application endpoint ids
     * 
     * @return
     */
    @ScriptMethod
    (
            help="Returns a list of the application endpoint ids",
            output="List of the application endpoint ids"
    )
    public String[] getEndpointIds()
    {
        String[] endpointIds = null;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if(remoteConfig != null)
        {
            endpointIds = remoteConfig.getEndpointIds();
        }
        
        return endpointIds;
    }

    /**
     * Returns the name of an endpoint
     * 
     * @param endpointId
     * @return
     */
    @ScriptMethod
    (
            help="Returns the name of an endpoint",
            output="Endpoint name"
    )
    public String getEndpointName(@ScriptParameter(help="Endpoint id") String endpointId)
    {
        String name = null;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if(remoteConfig != null)
        {
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(endpointId);
            if(descriptor != null)
            {
                name = descriptor.getName();
            }
        }
        
        return name;
    }

    /**
     * Returns the description of an endpoint
     * 
     * @param endpointId
     * @return
     */
    @ScriptMethod
    (
            help="Returns the description of an endpoint",
            output="Endpoint description"
    )
    public String getEndpointDescription(@ScriptParameter(help="Endpoint id") String endpointId)
    {
        String description = null;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if(remoteConfig != null)
        {
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(endpointId);
            if(descriptor != null)
            {
                description = descriptor.getDescription();
            }
        }
        
        return description;
    }    

    @ScriptMethod
    (
            help="Checks if an endpoint is persistent or not",
            output="True if the endpoint is persistent"
    )
    public boolean isEndpointPersistent(@ScriptParameter(help="Endpoint id") String id)
    {
        boolean persistent = false;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if(remoteConfig != null)
        {
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(id);
            if(descriptor != null)
            {
                persistent = descriptor.getPersistent();
            }
        }
        
        return persistent;
    }    

    /**
     * Returns the configured URL for the given endpoint
     * 
     * @param endpointId
     * 
     * @return the endpoint url
     */
    @ScriptMethod
    (
            help="Returns the configured URL for the given endpoint",
            output="The endpoint url"
    )
    public String getEndpointURL(@ScriptParameter(help="Endpoint id") String endpointId)
    {
        String url = null;
        
        RemoteConfigElement remoteConfig = getRemoteConfig();
        if (remoteConfig != null)
        {
            EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(endpointId);
            if (descriptor != null)
            {
                url = descriptor.getEndpointUrl();
            }
        }
        
        return url;
    }

    /**
     * @return RemoteConfigElement
     */
    private RemoteConfigElement getRemoteConfig()
    {
        return this.connectorService.getRemoteConfig();
    }
}
