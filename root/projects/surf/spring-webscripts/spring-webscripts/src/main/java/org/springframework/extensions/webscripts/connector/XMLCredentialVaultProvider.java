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
import org.springframework.extensions.surf.exception.CredentialVaultProviderException;

/**
 * Provides instances of credential vaults
 * 
 * @author muzquiano
 */
public class XMLCredentialVaultProvider implements CredentialVaultProvider
{
    protected ConfigService configService;
    protected String location;
    
    /**
     * Instantiates a new XML credential vault provider.
     */
    public XMLCredentialVaultProvider()
    {
        this.location = "/xml";
    }

    /**
     * Sets the config service.
     * 
     * @param configService the new config service
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    public void setLocation(String location)
    {
        this.location = location;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVaultProvider#provide(java.lang.String)
     */
    public CredentialVault provide(String id) throws CredentialVaultProviderException
    {
        XMLCredentialVault vault = new XMLCredentialVault(id);
        vault.setConfigService(configService);
        vault.setLocation(location);

        return vault;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.CredentialVaultProvider#generateKey(java.lang.String, java.lang.String)
     */
    public String generateKey(String id, String userId)
    {
        return id + userId;
    }
}
