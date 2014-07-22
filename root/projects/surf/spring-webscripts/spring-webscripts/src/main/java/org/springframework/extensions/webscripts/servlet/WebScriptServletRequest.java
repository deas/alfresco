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

package org.springframework.extensions.webscripts.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.surf.util.InputStreamContent;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequestImpl;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;


/**
 * HTTP Servlet Web Script Request
 * 
 * @author davidc
 */
public class WebScriptServletRequest extends WebScriptRequestImpl
{
    // Logger
    private static final Log logger = LogFactory.getLog(WebScriptServletRequest.class);
    
    private static final String HEADER_ALF_FORCE_SUCCESS_RESPONSE = "alf-force-success-response";
    private static final String HEADER_USER_AGENT = "user-agent";

    /** HTTP Request */
    private ServerProperties serverProperties;
    private HttpServletRequest req;
    
    /** Service bound to this request */
    private Match serviceMatch;
    
    /** Multi-part form data, if provided */
    private FormData formData;
    
    /** Content read from the inputstream */
    private Content content = null;

    
    /**
     * Construction
     *
     * @param container  request generator
     * @param req
     * @param serviceMatch
     */
    public WebScriptServletRequest(Runtime container, HttpServletRequest req, Match serviceMatch, ServerProperties serverProperties)
    {
        super(container);
        
        this.serverProperties = serverProperties;
        this.req = req;
        this.serviceMatch = serviceMatch;
        
        String contentType = getContentType();
        
        if (logger.isDebugEnabled())
            logger.debug("Content Type: " + contentType);
        
        // Multipart formdata is a special case as it must be made available as arguments,
        // therefore the object is prepared early so getParameter() has the formdata.
        if (contentType != null && contentType.equals(MULTIPART_FORM_DATA))
        {
            // Specialised webscripts can disable automatic formdata processing,
            // this may be required for Java backed webscripts that perform advanced
            // processing - such as manually streaming the inputstream direct to a store.
            if (serviceMatch != null && serviceMatch.getWebScript().getDescription().getMultipartProcessing())
            {
                this.formData = (FormData)parseContent();
            }
        }
    }

    /**
     * Gets the HTTP Servlet Request
     * 
     * @return  HTTP Servlet Request
     */
    public HttpServletRequest getHttpServletRequest()
    {
        return req;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServiceMatch()
     */
    public Match getServiceMatch()
    {
        return serviceMatch;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServerPath()
     */
    public String getServerPath()
    {
        return getServerScheme() + "://" + getServerName() + ":" + getServerPort();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getContextPath()
     */
    public String getContextPath()
    {
        return req.getContextPath();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServiceContextPath()
     */
    public String getServiceContextPath()
    {
        return req.getContextPath() + req.getServletPath();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServicePath()
     */
    public String getServicePath()
    {
        String pathInfo = getPathInfo();
        return getServiceContextPath() + ((pathInfo == null) ? "" : pathInfo);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getURL()
     */
    public String getURL()
    {
        String queryString = getQueryString();
        if (queryString != null)
        {
            return getServicePath() + "?" + queryString;
        }
        return getServicePath();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getPathInfo()
     */
    public String getPathInfo()
    {
        // NOTE: Don't use req.getPathInfo() - it truncates the path at first semi-colon in Tomcat
        final String requestURI = req.getRequestURI();
        final String serviceContextPath = getServiceContextPath();
        String pathInfo;
        
        if (serviceContextPath.length() > requestURI.length())
        {
            // NOTE: assume a redirect has taken place e.g. tomcat welcome-page
            // NOTE: this is unlikely, and we'll take the hit if the path contains a semi-colon
            pathInfo = req.getPathInfo();
        }
        else
        {
            pathInfo = URLDecoder.decode(requestURI.substring(serviceContextPath.length()));
        }
        
        return pathInfo;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getQueryString()
     */
    public String getQueryString()
    {
        String queryString = req.getQueryString();
        if (queryString != null)
        {
            queryString = URLDecoder.decode(queryString);
        }
        return queryString;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameterNames()
     */
    public String[] getParameterNames()
    {
        Set<String> keys = req.getParameterMap().keySet();
        if (formData != null)
        {
            Set<String> formkeys = formData.getParameters().keySet();
            keys = new HashSet<String>(keys);
            keys.addAll(formkeys);
        }
        String[] names = new String[keys.size()];
        keys.toArray(names);
        return names;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name)
    {
        String val = null;
        if (formData != null)
        {
            String[] vals = formData.getParameters().get(name);
            val = (vals == null) ? null : vals[0];
        }
        if (val == null)
        {
            val = req.getParameter(name);
        }
        return val;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getArrayParameter(java.lang.String)
     */
    public String[] getParameterValues(String name)
    {
        String[] vals = null;
        if (formData != null)
        {           
            vals = formData.getParameters().get(name);
        }
        if (vals == null)
        {
            vals = req.getParameterValues(name);
        }
        return vals;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderNames()
     */
    @SuppressWarnings("unchecked")
    public String[] getHeaderNames()
    {
        List<String> headersList = new ArrayList<String>();
        Enumeration<String> enumNames = req.getHeaderNames();
        while(enumNames.hasMoreElements())
        {
            headersList.add(enumNames.nextElement());
        }
        String[] headers = new String[headersList.size()];
        headersList.toArray(headers);
        return headers;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeader(java.lang.String)
     */
    public String getHeader(String name)
    {
        return req.getHeader(name);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderValues(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public String[] getHeaderValues(String name)
    {
        String[] values = null;
        Enumeration<String> enumValues = req.getHeaders(name);
        if (enumValues.hasMoreElements())
        {
            List<String> valuesList = new ArrayList<String>(2);
            do
            {
                valuesList.add(enumValues.nextElement());
            } 
            while (enumValues.hasMoreElements());
            values = new String[valuesList.size()];
            valuesList.toArray(values);
        }
        return values;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getAgent()
     */
    public String getAgent()
    {
        return resolveUserAgent(req.getHeader(HEADER_USER_AGENT));
    }
    
    /**
     * Helper to resolve common user agent strings from Http request header
     */
    public static String resolveUserAgent(String userAgent)
    {
        if (userAgent != null)
        {
            if (userAgent.indexOf("Firefox/") != -1)
            {
                return "Firefox";
            }
            else if (userAgent.indexOf("MSIE") != -1)
            {
                return "MSIE";
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getContent()
     */
    public Content getContent()
    {
        // ensure we only try to read the content once - as this method may be called several times
        // but the underlying inputstream itself can only be processed a single time
        if (content == null)
        {
            try
            {
                content = new InputStreamContent(req.getInputStream(), getContentType(), req.getCharacterEncoding());
            }
            catch(IOException e)
            {
                throw new WebScriptException("Failed to retrieve request content", e);
            }
        }
        return content;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getContentType()
     */
    public String getContentType()
    {
        String contentType = req.getContentType();
        if (contentType == null || contentType.length() == 0)
        {
            contentType = super.getContentType();
        }
        if (contentType != null && contentType.startsWith(MULTIPART_FORM_DATA))
        {
            contentType = MULTIPART_FORM_DATA;
        }
        return contentType;
    }
    
    /**
     * Get Server Scheme
     * 
     * @return  server scheme
     */
    private String getServerScheme()
    {
        String scheme = null;
        if (serverProperties != null)
        {
            scheme = serverProperties.getScheme();
        }
        if (scheme == null)
        {
            scheme = req.getScheme();
        }
        return scheme;
    }

    /**
     * Get Server Name
     * 
     * @return  server name
     */
    private String getServerName()
    {
        String name = null;
        if (serverProperties != null)
        {
            name = serverProperties.getHostName();
        }
        if (name == null)
        {
            name = req.getServerName();
        }
        return name;
    }

    /**
     * Get Server Port
     * 
     * @return  server name
     */
    private int getServerPort()
    {
        Integer port = null;
        if (serverProperties != null)
        {
            port = serverProperties.getPort();
        }
        if (port == null)
        {
            port = req.getServerPort();
        }
        return port;
    }
    
    /**
     * Returns the FormField bject representing a file uploaded via a multipart form.
     * 
     * @param name The name of the field containing the content
     * @return FormField bject representing a file uploaded via a multipart form or null
     *         if the field does not exist or is not a file field.
     */
    public FormField getFileField(String name)
    {
        FormField field = null;
        
        // attempt to find the requested field
        FormField[] fields = this.formData.getFields();
        for (FormField f : fields)
        {
            if (f.getName().equals(name))
            {
                // check the field is a file field
                if (f.getIsFile())
                {
                    field = f;
                }
                
                break;
            }
        }
            
        return field;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequestImpl#forceSuccessStatus()
     */
    @Override
    public boolean forceSuccessStatus()
    {
        String forceSuccess = req.getHeader(HEADER_ALF_FORCE_SUCCESS_RESPONSE);
        return Boolean.valueOf(forceSuccess);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getURL();
    }
}
