/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.site;

import javax.servlet.http.HttpServletRequest;

import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Credentials;

/**
 * A {@link SlingshotUserFactory} that adds credentials to the {@link CredentialVault}
 * for using the activiti-admin endpoint.
 *
 * @author Frederik Heremans
 */
public class EnterpriseSlingshotUserFactory extends SlingshotUserFactory
{

    public static final String ACTIVITI_ADMIN_ENDPOINT_ID = "activiti-admin";

    @Override
    public boolean authenticate(HttpServletRequest request, String username, String password)
    {
        boolean authenticated = super.authenticate(request, username, password);
        
        if(authenticated)
        {
            // Add activiti-admin credentials to the vault as well.
            CredentialVault vault = frameworkUtils.getCredentialVault(request.getSession(), username);
            Credentials credentials = vault.newCredentials(ACTIVITI_ADMIN_ENDPOINT_ID);
            credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
            credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, password);
        }
        return authenticated;
    }
}
