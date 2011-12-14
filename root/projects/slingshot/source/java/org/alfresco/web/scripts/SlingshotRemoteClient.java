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
package org.alfresco.web.scripts;

import java.net.URL;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.RemoteClient;

/**
 * Override the Spring WebScripts impl of RemoteClient to provide additional security
 * processing of HTML responses retrieved via content APIs. Prevents the execution of
 * inline JavaScript proxy driven API calls via XHR requests and similar.
 * 
 * @author Kevin Roast
 */
public class SlingshotRemoteClient extends RemoteClient
{
    private static final Pattern CONTENT_PATTERN = Pattern.compile(".*/api/(node|path)/content/workspace/SpacesStore/.*");
    
    @Override
    protected void processContentType(URL url, HttpServletResponse res, Header contentType)
    {
        if (res != null && getRequestMethod() == HttpMethod.GET)
        {
            // found a GET request that might be a security risk
            final String strContentType = contentType.getValue();
            if (strContentType.startsWith("text/html") || strContentType.startsWith("application/xhtml+xml"))
            {
                //  match appropriate content URIs 
                if (CONTENT_PATTERN.matcher(url.getPath()).matches())
                {
                    // rewrite content type header for security
                    res.setHeader(HEADER_CONTENT_TYPE, "text/plain; charset=utf-8");
                }
            }
        }
    }
}