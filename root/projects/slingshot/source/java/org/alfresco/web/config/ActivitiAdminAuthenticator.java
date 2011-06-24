/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.config;

import org.springframework.extensions.surf.exception.AuthenticationException;
import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;
import org.springframework.extensions.webscripts.connector.ConnectorSession;
import org.springframework.extensions.webscripts.connector.Credentials;

public class ActivitiAdminAuthenticator extends AlfrescoAuthenticator
{
    private static final CharSequence ACTIVITI_ADMIN_UI_PATH = "alfresco/activiti-admin";
    private static final CharSequence ALFRESCO_SERVICE_BASE_PATH = "alfresco/s";
    
    @Override
    public ConnectorSession authenticate(String endpoint, Credentials credentials,
                ConnectorSession connectorSession) throws AuthenticationException
    {
        String endPointToUse = getAlfrescoEndpoint(endpoint);
        ConnectorSession session = super.authenticate(endPointToUse, credentials, connectorSession);
        return session;
    }

    /**
     * Create alfresco api base endpoint, based on activiti-admin endpoint
     * url.
     */
    private String getAlfrescoEndpoint(String endpoint)
    {
        return endpoint.replace(ACTIVITI_ADMIN_UI_PATH, ALFRESCO_SERVICE_BASE_PATH);
    }
    
}
