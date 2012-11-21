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

/**
 * Provides Sharepoint with URL help, e.g. retrieve base URL of Sharepoint server as
 * exposed in the outside world.
 * <p>
 * Trailing slashes are never present so as to give consistency between different context paths,
 * e.g. "/" and "/alfresco". Otherwise it would be necessary to check for trailing slashes before appending
 * to the returned paths.
 * 
 * @author Matt Ward
 */
public interface UrlHelper
{
    /**
     * The base URL exposed to clients, regardless of where Sharepoint is deployed. For example
     * Sharepoint may have been deployed via a reverse proxy and the external URL will reflect the
     * endpoint mapped by the proxy, rather than the internal IP address of the Sharepoint server.
     * 
     * @return Base URL, e.g. https://sp.example.com:7070/sharepointContext
     */
    public String getExternalBaseURL();

    /**
     * Creates a URL relative to the base URL as returned by {@link #getExternalBaseURL()}. For example
     * if the base URL is
     * <pre>
     *    https://sp.example.com:7070/sharepointContext
     * </pre>
     * and pathWithinContext is
     * <pre>
     *    myfolder/mydocument.txt
     * </pre>
     * then the returned URL will be
     * <pre>
     *    https://sp.example.com:7070/sharepointContext/myfolder/mydocument.txt
     * </pre>
     * @param pathWithinContext
     * 
     * @return URL
     */
    String getExternalURL(String pathWithinContext);
    
    
    /**
     * The base URL including the protocol scheme, host and port only without any path information.
     * For example
     * <pre>
     *    https://sp.example.com:7070
     * </pre>
     * 
     * @return URL
     */
    String getExternalURLHostOnly();
}
