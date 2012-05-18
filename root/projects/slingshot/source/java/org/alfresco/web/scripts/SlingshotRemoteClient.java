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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.RemoteClient;
import org.springframework.extensions.webscripts.ui.common.StringUtils;

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
    protected void copyResponseStreamOutput(URL url, HttpServletResponse res, OutputStream out,
            org.apache.commons.httpclient.HttpMethod method, String contentType, int bufferSize) throws IOException
    {
        boolean process = false;
        if (res != null && getRequestMethod() == HttpMethod.GET)
        {
            // found a GET request that might be a security risk
            if (contentType != null && (contentType.startsWith("text/html") || contentType.startsWith("application/xhtml+xml")))
            {
                //  match appropriate content URIs 
                if (CONTENT_PATTERN.matcher(url.getPath()).matches())
                {
                    // found content we need to process in-memory and perform HTML stripping on
                    process = true;
                }
            }
        }
        if (!process)
        {
            super.copyResponseStreamOutput(url, res, out, method, contentType, bufferSize);
        }
        else
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(bufferSize);
            final InputStream input = method.getResponseBodyAsStream();
            if (input != null)
            {
                // get data into our byte buffer for processing
                try
                {
                    final byte[] buffer = new byte[bufferSize];
                    int read = input.read(buffer);
                    while (read != -1)
                    {
                        bos.write(buffer, 0, read);
                        read = input.read(buffer);
                    }
                }
                finally
                {
                    input.close();
                }
                
                // convert to appropriate string format
                String encoding = null;
                int csi = contentType.indexOf(CHARSETEQUALS);
                if (csi != -1)
                {
                    encoding = contentType.substring(csi + CHARSETEQUALS.length());
                }
                String content = encoding != null ? new String(bos.toByteArray(), encoding) : new String(bos.toByteArray());
                
                // process with HTML stripper
                content = StringUtils.stripUnsafeHTMLTags(content, false);
                
                // push the modified response to the real outputstream
                try
                {
                    byte[] bytes = encoding != null ? content.getBytes(encoding) : content.getBytes();
                    // rewrite size header as it wil have changed
                    res.setContentLength(bytes.length);
                    // output the bytes
                    out.write(bytes);
                }
                finally
                {
                    out.close();
                }
            }
        }
    }
}