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

import java.io.Serializable;
import java.util.Iterator;

import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.User;

/**
 * Represents the credential vault to the script engine
 * This exposes credentials from the vault which are "user" managed
 * 
 * @author muzquiano
 */
public final class ScriptCredentialVault
{    
    final private CredentialVault vault;
    final private User user;
    
    protected ScriptableMap<String, Serializable> properties;
    
    /**
     * Constructs a new ScriptCredentialVault object.
     * 
     * @param vault   The credential vault instance
     * @param user    The user to whom the credential vault belongs
     */
    public ScriptCredentialVault(CredentialVault vault, User user)
    {
        this.vault = vault;
        this.user = user;
    }
    
    // --------------------------------------------------------------
    // JavaScript Properties

    /**
     * Returns the properties of the credential vault
     */
    public ScriptableMap<String, Serializable> getProperties()
    {
        if (this.properties == null)
        {
            this.properties = new ScriptableLinkedHashMap<String, Serializable>();
            
            // put credentials onto the map
            String[] ids = this.vault.getStoredIds();
            for(int i = 0; i < ids.length; i++)
            {
                Credentials credentials = this.vault.retrieve(ids[i]);
                ScriptCredentials scriptCredentials = new ScriptCredentials(this.vault, credentials, true);
                this.properties.put(ids[i], scriptCredentials);
            }
        }
        
        return this.properties;
    }
    
    /**
     * Returns the user to whom this credential vault belongs
     */
    public User getUser()
    {
        return this.user;
    }
    
    /**
     * Returns whether the given endpoint credentials are stored on this vault
     * 
     * @param endpointId
     * @return
     */
    public boolean hasCredentials(String endpointId)
    {
        return getProperties().containsKey(endpointId);
    }
    
    /**
     * Creates new credentials and binds them into this vault.
     * If the credentials already exist, the old ones will be returned
     * 
     * @param endpointId
     * @return
     */
    public ScriptCredentials newCredentials(String endpointId)
    {
        ScriptCredentials scriptCredentials = (ScriptCredentials) getProperties().get(endpointId);
        if (scriptCredentials == null)
        {
            Credentials creds = this.vault.newCredentials(endpointId);
            this.vault.save();
            
            // update our properties map
            scriptCredentials = new ScriptCredentials(this.vault, creds);
            getProperties().put(endpointId, scriptCredentials);
        }
        
        return scriptCredentials;
    }
    
    /**
     * Removes credentials from the vault
     * 
     * @param endpointId
     */
    public void removeCredentials(String endpointId)
    {
        // remove from the actual vault
        this.vault.remove(endpointId);
        this.vault.save();
        
        // remove from our map
        getProperties().remove(endpointId);
    }
    
    /**
     * Saves the credential vault
     */
    public void save()
    {
        // get the actual vault and clear it
        String[] storedIds = this.vault.getStoredIds();
        for(int i = 0; i < storedIds.length; i++)
        {
            this.vault.remove(storedIds[i]);
        }
        
        // now walk through our properties and place them back into the vault
        Iterator it = getProperties().keySet().iterator();
        while (it.hasNext())
        {
            String endpointId = (String) it.next();

            // get the script credentials
            ScriptCredentials scriptCredentials = (ScriptCredentials) getProperties().get(endpointId);
            
            // create a new actual credentials onto which we will map
            Credentials creds = this.vault.newCredentials(endpointId);
            
            // now do the mapping
            Iterator it2 = scriptCredentials.getProperties().keySet().iterator();
            while (it2.hasNext())
            {
                String propertyKey = (String) it2.next();
                Object propertyValue = scriptCredentials.getProperties().get(propertyKey);
                
                if (propertyValue != null)
                {
                    creds.setProperty(propertyKey, propertyValue);
                }
            }
            
            // store the creds back onto the actual vault
            this.vault.store(creds);
        }
        
        // persist the vault (if needed)
        this.vault.save();
        
        // null our properties map so it reloads on next access
        this.properties = null;
    }
}
