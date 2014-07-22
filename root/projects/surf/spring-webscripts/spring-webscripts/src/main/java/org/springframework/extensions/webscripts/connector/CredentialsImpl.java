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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Credentials for a given user. This stores credentials that are to be passed
 * to a back-end service in order to authenticate. Once these credentials are
 * used to authenticate, they may no longer be necessary as the service may hand
 * back "endpoint credentials" which are to be used on subsequent calls.
 * 
 * An example of a user credential might be username/password.
 * 
 * An example of an endpoint credential might be an Alfresco ticket.
 * 
 * @author muzquiano
 */
public class CredentialsImpl implements Credentials, Serializable
{
    protected boolean persistent;
    protected String endpointId;
    protected HashMap<String, Object> properties = new HashMap<String, Object>();

    /**
     * Instantiates a new user credential.
     * 
     * @param endpointId the endpoint id
     */
    public CredentialsImpl(String endpointId)
    {
        this.endpointId = endpointId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#getEndpointId()
     */
    public String getEndpointId()
    {
        return this.endpointId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#getProperty(java.lang.String)
     */
    public Object getProperty(String key)
    {
        return this.properties.get(key);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String key, Object value)
    {
        this.properties.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#removeProperty(java.lang.String)
     */
    public void removeProperty(String key)
    {
        this.properties.remove(key);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#removeAllProperties(java.lang.String)
     */
    public void removeAllProperties(String key)
    {
        this.properties.clear();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#getPropertyKeys()
     */
    public String[] getPropertyKeys()
    {
        String[] keys = new String[this.properties.keySet().size()];
        
        int count = 0;
        Iterator it = this.properties.keySet().iterator();
        while(it.hasNext())
        {
            keys[count] = (String) it.next();
            count++;
        }
        
        return keys;
    }

    /* (non-Javadoc)
     * @see org.alfresco.connector.Credentials#isPersistent()
     */    
    public boolean isPersistent()
    {
        return this.persistent;       
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.properties.toString();
    }
}
