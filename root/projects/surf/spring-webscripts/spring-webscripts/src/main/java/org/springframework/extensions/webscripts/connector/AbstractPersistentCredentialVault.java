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

import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;

/**
 * An abstract implementation of a persistent credential vault
 * where crednetials can be stored from a persistent location.
 *  
 * @author muzquiano
 */
public abstract class AbstractPersistentCredentialVault extends SimpleCredentialVault
{
    protected ConfigService configService;
    
    /**
     * Instantiates a new persistentcredential vault.
     * 
     * @param id the id
     */    
    public AbstractPersistentCredentialVault(String id)
    {
        super(id);
    }
    
    /**
     * Sets the config service.
     * 
     * @param configService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVault#store(java.lang.String, java.lang.String, org.alfresco.connector.Credentials)
     */
    public void store(Credentials credentials)
    {
        // check whether the given credentials should be flagged
        // as persistent
        String endpointId = credentials.getEndpointId();
        EndpointDescriptor descriptor = getRemoteConfig().getEndpointDescriptor(endpointId);
        if(descriptor != null)
        {
            // mark the persistence attribute onto the credentials
            ((CredentialsImpl)credentials).persistent = descriptor.getPersistent();
        }
        
        super.store(credentials);
    }
    
    /**
     * @return RemoteConfigElement
     */
    protected RemoteConfigElement getRemoteConfig()
    {
        RemoteConfigElement remoteConfig = (RemoteConfigElement)configService.getConfig(
                "Remote").getConfigElement("remote");
        return remoteConfig;
    }    

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "PersistentCredentialVault - " + this.id;
    }    
}
