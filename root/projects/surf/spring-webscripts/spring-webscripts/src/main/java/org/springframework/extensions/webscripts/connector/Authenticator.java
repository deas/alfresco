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

import org.springframework.extensions.surf.exception.AuthenticationException;

/**
 * Interface that defines an Authenticator.  Authenticators are used to
 * retrieve cookies and tokens from a remote service based on credentials
 * which are locally managed and passed to the remote service.
 * 
 * Authenticator objects are used when a "token" must be passed to the endpoint
 * and the current token is either invalid or non-existent. The Connectors must
 * then handshake with the endpoint to acquire a token.
 * 
 * Tokens are not always necessary. An example is HTTP Basic Authentication
 * where user names and passwords are sent on every request. Alternatively, of
 * course, you may wish only to authenticate on the first request and then pass
 * the Authenticate hash on every subsequent request.
 * 
 * In that case, the role of the authenticate() method would be to handshake
 * with the endpoint to acquire this hash.
 * 
 * @author muzquiano
 */
public interface Authenticator
{
    /**
     * Authenticate against the given Endpoint URL with the supplied Credentials
     * 
     * @return The connector session instance
     * 
     * @throws AuthenticationException on error
     */
    public ConnectorSession authenticate(String endpoint, Credentials credentials, ConnectorSession connectorSession)
            throws AuthenticationException;

    /**
     * Returns whether the current connector session has been authenticated or not
     * 
     * @param endpoint
     * @param connectorSession
     * @return
     */
    public boolean isAuthenticated(String endpoint, ConnectorSession connectorSession);
}
