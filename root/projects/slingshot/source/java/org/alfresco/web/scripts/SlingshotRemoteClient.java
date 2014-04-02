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
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.htmlparser.*;
import org.htmlparser.tags.DoctypeTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;
import org.springframework.extensions.surf.util.I18NUtil;
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
    
    private boolean swfEnabled = false;
    
    public void setSwfEnabled(boolean swfEnabled)
    {
        this.swfEnabled = swfEnabled;
    }
    
    @Override
    protected void copyResponseStreamOutput(URL url, HttpServletResponse res, OutputStream out,
            org.apache.commons.httpclient.HttpMethod method, String contentType, int bufferSize) throws IOException
    {
        boolean processed = false;
        if (res != null && getRequestMethod() == HttpMethod.GET)
        {
            // only match if content is not an attachment - don't interfere with downloading of file content 
            Header cd = method.getResponseHeader("Content-Disposition");
            if (cd == null || !cd.getValue().startsWith("attachment"))
            {
                // only match appropriate content REST URIs 
                if (contentType != null && CONTENT_PATTERN.matcher(url.getPath()).matches())
                {
                    // found a GET request that might be a security risk
                    String mimetype = contentType;
                    String encoding = null;
                    int csi = contentType.indexOf(CHARSETEQUALS);
                    if (csi != -1)
                    {
                        mimetype = contentType.substring(0, csi - 1).toLowerCase();
                        encoding = contentType.substring(csi + CHARSETEQUALS.length());
                    }
                    
                    // examine the mimetype to see if additional processing is required
                    if (mimetype.equals("text/html") || mimetype.equals("application/xhtml+xml") || mimetype.equals("text/xml"))
                    {
                        // found HTML content we need to process in-memory and perform stripping on
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
                            String content = encoding != null ? new String(bos.toByteArray(), encoding) : new String(bos.toByteArray());

                            if (mimetype.equals("text/html") || mimetype.equals("application/xhtml+xml"))
                            {
                                // process with HTML stripper
                                content = StringUtils.stripUnsafeHTMLDocument(content, false);
                            }
                            else if (mimetype.equals("text/xml"))
                            {
                                // If docType is set to xml browsers (at least IE & Chrome) will treat it like it
                                // does for a svg+xml document
                                if (hasDocType(content, "svg", false))
                                {
                                    res.setContentType("text/plain");
                                }
                                else if (hasDocType(content, "html", false))
                                {
                                    content = StringUtils.stripUnsafeHTMLDocument(content, false);
                                }
                            }
                            else if (mimetype.equals("text/x-component"))
                            {
                                // IE supports "behaviour" which means that css can load a .htc file that could
                                // contain XSS code in the form of jscript, vbscript etc, to stop it form being
                                // evaluated we set the contient type to text/plain
                                res.setContentType("text/plain");
                            }

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
                        processed = true;
                    }
                    else if ((mimetype.equals("application/x-shockwave-flash") || mimetype.equals("image/svg+xml")) && !swfEnabled)
                    {
                        String msg = I18NUtil.getMessage("security.insecuremimetype");
                        try
                        {
                            byte[] bytes = encoding != null ? msg.getBytes(encoding) : msg.getBytes();
                            
                            // rewrite headers
                            res.setContentType("text/plain");
                            res.setContentLength(bytes.length);
                            // output the bytes
                            out.write(bytes);
                        }
                        finally
                        {
                            out.close();
                        }
                        processed = true;
                    }
                }
            }
        }
        if (!processed)
        {
            super.copyResponseStreamOutput(url, res, out, method, contentType, bufferSize);
        }
    }

    protected boolean hasDocType(String content, String docType, boolean encode)
    {
        try
        {
            Parser parser = Parser.createParser(content, "UTF-8");
            PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
            parser.setNodeFactory(factory);
            NodeIterator itr = parser.elements();
            while (itr.hasMoreNodes())
            {
                Node node = itr.nextNode();
                if (node instanceof DoctypeTag)
                {
                    // Found the doctype tag, now lets see if can find the searched for doctype attribute.
                    DoctypeTag docTypeTag = (DoctypeTag)node;
                    Vector<Attribute> attrs = docTypeTag.getAttributesEx();
                    if (attrs != null && attrs.size() > 1)
                    {
                        for (Attribute attr : attrs)
                        {
                            String name = attr.getName();
                            if (name != null && name.equalsIgnoreCase(docType))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        catch (ParserException e)
        {
            // Not a valid xml document, return false below
        }
        return false;
    }
}
