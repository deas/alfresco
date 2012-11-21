/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.vti.handler.alfresco;

import java.net.InetAddress;

/**
 * Default implementation of {@link LocalHostNameProvider}.
 * 
 * @author Matt Ward
 */
public class DefaultLocalHostNameProvider implements LocalHostNameProvider
{
    /** Token name to substitute current servers DNS name or TCP/IP address into a host name **/
    private static final String TOKEN_LOCAL_NAME = "${localname}";
    private String localName;
    
    public DefaultLocalHostNameProvider()
    {
        String srvName = "localhost";
        try
        {
            srvName = InetAddress.getLocalHost().getHostName();
        }
        catch (Exception ex)
        {
            srvName = "localhost";
        }
        localName = srvName;
    }
    
    @Override
    public String getLocalName()
    {
        return localName;
    }
    
    /**
     * Expands the special ${localname} token within a host name using the resolved DNS name for the local host.
     * 
     * @param hostName
     *            the host name
     * @return the string
     */
    @Override
    public String subsituteHost(String hostName)
    {
        return hostName.replace(TOKEN_LOCAL_NAME, localName);
    }
}
