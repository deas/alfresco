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
 * Describes bound-context for a given user.
 * <p>
 * Bound context includes Credentials and Connector session information.
 * <p>
 * Credentials may or may not be bound to a CredentialVault.
 * ConnectorSessions may or may not be bound to an Http Session.
 * <p>
 * This class is immutable.
 * 
 * @author muzquiano
 * @author kevinr
 */
public final class UserContext
{
    private final String userId;
    private final Credentials credentials;
    private final ConnectorSession connectorSession;
    
    public UserContext(String userId, Credentials credentials, ConnectorSession connectorSession)
    {
        this.userId = userId;
        this.credentials = credentials;
        this.connectorSession = connectorSession;
    }
    
    public String getUserId()
    {
        return this.userId;
    }
    
    public Credentials getCredentials()
    {
        return this.credentials;
    }
    
    public ConnectorSession getConnectorSession()
    {
        return this.connectorSession;
    }
}
