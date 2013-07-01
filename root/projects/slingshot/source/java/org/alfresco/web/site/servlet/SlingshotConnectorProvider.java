/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site.servlet;

import org.alfresco.web.site.SlingshotUserFactory;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.surf.WebFrameworkConnectorProvider;
import org.springframework.extensions.surf.exception.ConnectorProviderException;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;

/**
 * @author Kevin Roast
 */
public class SlingshotConnectorProvider extends WebFrameworkConnectorProvider
{
    private ConnectorService connectorService;
    
    /**
     * Sets the connector service.
     * 
     * @param connectorService
     */
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
        super.setConnectorService(connectorService);
    }
    
    /**
     * @see org.springframework.extensions.surf.WebFrameworkConnectorProvider#provide(java.lang.String)
     */
    @Override
    public Connector provide(String endpoint) throws ConnectorProviderException
    {
        EndpointDescriptor descriptor = this.connectorService.getRemoteConfig().getEndpointDescriptor(endpoint);
        return super.provide(descriptor.getExternalAuth() ? SlingshotUserFactory.ALFRESCO_ENDPOINT_ID : endpoint);
    }
}
