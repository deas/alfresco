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

import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Credentials;

/*
 * @author muzquiano
 */
public final class ScriptCredentials implements Serializable
{
    final private CredentialVault vault;
    final private Credentials credentials;
    final private boolean hideNonPersistent;
    
    protected ScriptableMap<String, Serializable> properties;

    public ScriptCredentials(CredentialVault vault, Credentials credentials)
    {
        this(vault, credentials, false);
    }
    
    public ScriptCredentials(CredentialVault vault, Credentials credentials, boolean hideNonPersistent)
    {
        this.vault = vault;
        this.credentials = credentials;
        this.hideNonPersistent = hideNonPersistent;
    }

    /**
     * Returns the properties of the credentials
     */
    public ScriptableMap<String, Serializable> getProperties()
    {
        if (this.properties == null)
        {
            this.properties = new ScriptableLinkedHashMap<String, Serializable>();
            
            // show either persistent credentials
            // or non-persistent credentials (when persistentOnly = false)
            if (!isHidden())
            {            
                // put credentials properties onto the map
                String[] keys = this.credentials.getPropertyKeys();
                for(int i = 0; i < keys.length; i++)
                {
                    Object propertyValue = this.credentials.getProperty(keys[i]);
                    this.properties.put(keys[i], (Serializable)propertyValue);
                }
            }
        }
        
        return this.properties;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Properties
    
    public boolean isHidden()
    {
        return !isPersistent() && hideNonPersistent;
    }
    
    public boolean isPersistent()
    {
        return credentials.isPersistent();
    }
}
