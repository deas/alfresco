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

/**
 * Interface for a Credential Vault
 * 
 * Credential vaults allow for the storage and retrieval of credentials by
 * credential id.
 * 
 * They can also be loaded and saved if they are backed by a persisted storage
 * location.
 * 
 * @author muzquiano
 */
public interface CredentialVault
{
    /**
     * Places the given credentials into the vault
     * 
     * @param credentials the credentials
     */
    public void store(Credentials credentials);

    /**
     * Retrieves credentials for a given endpoint id from the vault
     * 
     * @param endpointId the endpoint id
     * 
     * @return the credentials
     */
    public Credentials retrieve(String endpointId);
    
    /**
     * Removes credentials for a given endpoint id from the vault
     * @param endpointId
     */
    public void remove(String endpointId);
    
    /**
     * @return true if any credentials are stored for this endpoint id
     */
    public boolean hasCredentials(String endpointId);

    /**
     * Creates new credentials which are stored in this vault
     * 
     * @param endpointId
     * @return the credentials object
     */
    public Credentials newCredentials(String endpointId);
    
    /**
     * Returns the ids for stored credentials
     * 
     * @return
     */
    public String[] getStoredIds();
    
    /**
     * Tells the Credential Vault to load state from persisted store
     * 
     * @return whether the credential vault successfully loaded
     */
    public boolean load();

    /**
     * Tells the Credential Vault to write state to persisted store
     * 
     * @return whether the credential vault successfully saved
     */
    public boolean save();
}
