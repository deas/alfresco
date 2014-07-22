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

package org.springframework.extensions.webscripts;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Description.FormatStyle;


/**
 * Basic Implementation of a Web Script Request
 * 
 * @author davidc
 */
public abstract class WebScriptRequestImpl implements WebScriptRequest
{
    private static final String ARG_GUEST = "guest";
    private static final String ARG_FORMAT = "format";
    private static final String ARG_ALF_CALLBACK = "alf_callback";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_ACCEPT = "Accept";
    
    /** "multipart/form-data" content type */
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    
    protected static final Log logger = LogFactory.getLog(WebScriptRequestImpl.class);
    
    /** WebScript Runtime for this request */
    private Runtime runtime;

    /** parsed request content */
    private Object requestContent = this;
    
    /** format for the request, after processing format arguments, extensions and format negotiation */
    private String format = null;


    /**
     * Construction
     * 
     * @param runtime
     */
    public WebScriptRequestImpl(Runtime runtime)
    {
        this.runtime = runtime;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getRuntime()
     */
    public Runtime getRuntime()
    {
        return this.runtime;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getExtensionPath()
     */
    public String getExtensionPath()
    {
        String extensionPath = "";
        Match match = getServiceMatch();
        if (match != null)
        {
            String matchPath = getServiceMatch().getPath();
            String pathInfo = getPathInfo();
            if (pathInfo != null)
            {
                extensionPath = pathInfo.substring(matchPath.length());
            }
        }
        return extensionPath;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#isGuest()
     */
    public boolean isGuest()
    {
        return Boolean.valueOf(getParameter(ARG_GUEST));
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getFormat()
     */
    public String getFormat()
    {
        if (this.format == null)
        {
            String format = null;
            Match match = getServiceMatch();
            if (match != null && match.getKind() != Match.Kind.URI)
            {
                Description desc = match.getWebScript().getDescription();
                FormatStyle style = desc.getFormatStyle();
                
                // extract format from extension
                if (style == FormatStyle.extension || style == FormatStyle.any)
                {
                    String pathInfo = getPathInfo();
                    if (pathInfo != null)
                    {
                        int extIdx = pathInfo.lastIndexOf('.');
                        if (extIdx != -1)
                        {
                            // format extension is only valid as the last URL element 
                            int pathIdx = pathInfo.lastIndexOf('/');
                            if (pathIdx < extIdx)
                            {
                                format = pathInfo.substring(extIdx +1);
                            }
                        }
                    }
                }
                
                // extract format from argument
                if (style == FormatStyle.argument || style == FormatStyle.any)
                {
                    String argFormat = getParameter(ARG_FORMAT);
                    if (argFormat != null)
                    {
                        if (format != null && format.length() > 0)
                        {
                            throw new WebScriptException("Format specified both in extension and format argument");
                        }
                        format = argFormat;
                    }
                }
                
                // negotiate format, if necessary
                if (format == null || format.length() == 0)
                {
                    String accept = getHeader(HEADER_ACCEPT);
                    NegotiatedFormat[] negotiatedFormats = desc.getNegotiatedFormats();
                    if (accept != null && negotiatedFormats != null)
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Negotiating format for " + accept);
                        
                        format = NegotiatedFormat.negotiateFormat(accept, negotiatedFormats);
                        if (format == null)
                        {
                            throw new WebScriptException(HttpServletResponse.SC_NOT_ACCEPTABLE, "Cannot negotiate appropriate response format for Accept: " + accept);
                        }
                    }
                }
                
                // fallback to default
                if (format == null || format.length() == 0)
                {
                    format = desc.getDefaultFormat();
                }
            }
            this.format = (format == null || format.length() == 0) ? "" : format;
        }
        return this.format;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getFormatStyle()
     */
    public FormatStyle getFormatStyle()
    {
        Match match = getServiceMatch();
        if (match == null)
        {
            return FormatStyle.any;
        }
        FormatStyle style = match.getWebScript().getDescription().getFormatStyle();
        if (style != FormatStyle.any)
        {
            return style;
        }
        else
        {
            String argFormat = getParameter(ARG_FORMAT);
            if (argFormat != null && argFormat.length() > 0)
            {
                return FormatStyle.argument;
            }
            else
            {
                return FormatStyle.extension;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getJSONCallback()
     */
    public String getJSONCallback()
    {
        return getParameter(ARG_ALF_CALLBACK);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#forceSuccessStatus()
     */
    public boolean forceSuccessStatus()
    {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getContentType()
     */
    public String getContentType()
    {
        String contentType = getHeader(HEADER_CONTENT_TYPE);
        if (contentType != null && contentType.startsWith(MULTIPART_FORM_DATA))
        {
            contentType = MULTIPART_FORM_DATA;
        }
        return contentType;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#parseContent()
     */
    public Object parseContent()
    {
        if (requestContent == this)
        {
            requestContent = null;
            String contentType = getContentType();
            if (contentType != null && contentType.length() > 0)
            {
                FormatRegistry formatRegistry = getRuntime().getContainer().getFormatRegistry();
                FormatReader<Object> reader = formatRegistry.getReader(contentType);
                if (reader != null)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Converting request (mimetype: " + contentType + ") to " + reader.getDestinationClass().getName());
                    
                    requestContent = reader.read(this);
                }
            }
        }
        return requestContent;
    }
    
}
