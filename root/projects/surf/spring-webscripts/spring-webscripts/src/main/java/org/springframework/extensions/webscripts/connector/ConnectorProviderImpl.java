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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.exception.ConnectorProviderException;
import org.springframework.extensions.surf.exception.ConnectorServiceException;

/**
 * A very simple implementation of a connector provider that provisions
 * web script connectors.  These are inherently stateless connectors - no
 * reuse of credentials or connector session data is applied to the 
 * provisioned connectors.
 * 
 * The connector provider pattern is utilized by the remote store as well
 * as the script remote object.  Both delegate to connector providers so as
 * to acquire connectors.
 * 
 * @author muzquiano
 */
public class ConnectorProviderImpl implements ConnectorProvider
{    
    private static final Log logger = LogFactory.getLog(ConnectorProviderImpl.class);

    private ConnectorService connectorService;
    
    /**
     * Sets the connector service.
     * 
     * @param connectorService
     */
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }
 
    /**
     * Implementation of the contract to provide a Connector for our
     * the web script framework.
     * 
     * Allows lazy providing of the Connector object only if the remote store actually needs
     * it. Otherwise acquiring the Connector when rarely used is an expensive overhead as most
     * objects are cached by the persister in which case the remote store isn't actually called.
     */
    public Connector provide(String endpoint)
        throws ConnectorProviderException
    {
        Connector conn = null;

        try
        {
            conn = connectorService.getConnector(endpoint);
        }
        catch(ConnectorServiceException cse)
        {
            throw new ConnectorProviderException("Unable to provision connector for endpoint: " + endpoint, cse);
        }
        
        return conn;
    }
}
