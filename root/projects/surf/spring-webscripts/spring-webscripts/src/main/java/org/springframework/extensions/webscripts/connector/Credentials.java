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
 * Interface that describes the credentials for a given service 
 * or user.
 * 
 * @author muzquiano
 */
public interface Credentials
{
    public final static String CREDENTIAL_USERNAME = "cleartextUsername";
    public final static String CREDENTIAL_PASSWORD = "cleartextPassword";

    /**
     * Gets the endpoint id.
     * 
     * @return the endpoint id
     */
    public String getEndpointId();

    /**
     * Gets a given property
     * 
     * @param key the key
     * 
     * @return the property
     */
    public Object getProperty(String key);

    /**
     * Sets a given property
     * 
     * @param key the key
     * @param value the value
     */
    public void setProperty(String key, Object value);

    /**
     * Removes a given property
     * 
     * @param key
     */
    public void removeProperty(String key);
    
    /**
     * Removes all properties
     * 
     * @param key
     */
    public void removeAllProperties(String key);
    
    /**
     * Returns the property keys
     * 
     * @return array of property keys
     */
    public String[] getPropertyKeys();
    
    /**
     * Returns whether this credential is persistent
     * 
     * A persistent credential is written to a persistent vault.
     * A non-persistent credential is loaded into the vault but never stored
     * 
     * @return
     */
    public boolean isPersistent();    
}
