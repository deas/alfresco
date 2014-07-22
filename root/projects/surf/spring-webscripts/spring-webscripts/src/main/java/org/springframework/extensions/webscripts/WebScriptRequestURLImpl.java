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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Web Script Request implementation that acts upon a string representation
 * of a URL
 * 
 * @author davidc
 */
public abstract class WebScriptRequestURLImpl extends WebScriptRequestImpl
{
    /** Script Url components */
    protected String contextPath;
    protected String servletPath;
    protected String pathInfo;
    protected String queryString;
    protected Map<String, String> queryArgs;
    protected Map<String, List<String>> queryArgsMulti;
    
    /** Service bound to this request */
    protected Match serviceMatch;
    
    /** cached parameter names as an array */
    private String[] paramNames = null;


    /**
     * Splits a Web Script Url into its component parts
     * 
     * @param scriptUrl  url  e.g. /alfresco/service/mytasks?f=1 
     * @return  url parts  [0] = context (e.g. alfresco), [1] = servlet (e.g. service), [2] = script (e.g. mytasks), [3] = args (e.g. f=1)
     */
    public static String[] splitURL(String scriptUrl)
    {
        String[] urlParts = new String[4];
        String path;
        String queryString;
        
        int argsIndex = scriptUrl.indexOf("?");
        if (argsIndex != -1)
        {
            path = scriptUrl.substring(0, argsIndex);
            queryString = scriptUrl.substring(argsIndex + 1);
        }
        else
        {
            path = scriptUrl;
            queryString = null;
        }
        
        String[] pathSegments = path.split("/");
        String pathInfo = "";
        for (int i = 3; i < pathSegments.length; i++)
        {
            pathInfo += "/" + pathSegments[i];
        }
        
        urlParts[0] = "/" + pathSegments[1];            // context path
        urlParts[1] = "/" + pathSegments[2];            // servlet path
        urlParts[2] = pathInfo;                         // path info
        urlParts[3] = queryString;                      // query string
        
        return urlParts;
    }
    
    /**
     * Splits a Web Script Url into its component parts
     *
     * @param context       context path (can be empty string but never null)
     * @param scriptUrl     url  e.g. /alfresco/service/mytasks?f=1
     * 
     * @return  url parts  [0] = context (e.g. alfresco, or empty if no context), [1] = servlet (e.g. service), [2] = script (e.g. mytasks), [3] = args (e.g. f=1)
     */
    public static String[] splitURL(final String context, final String scriptUrl)
    {
        String[] urlParts = new String[4];
        String path;
        String queryString;
        
        int argsIndex = scriptUrl.indexOf("?");
        if (argsIndex != -1)
        {
            path = scriptUrl.substring(0, argsIndex);
            queryString = scriptUrl.substring(argsIndex + 1);
        }
        else
        {
            path = scriptUrl;
            queryString = null;
        }
        
        final String[] pathSegments = path.substring(context.length() + 1).split("/");
        StringBuilder pathInfo = new StringBuilder(64);
        for (int i = 1; i < pathSegments.length; i++)
        {
            pathInfo.append('/').append(pathSegments[i]);
        }
        
        urlParts[0] = context;                          // context path
        urlParts[1] = '/' + pathSegments[0];            // servlet path
        urlParts[2] = pathInfo.toString();              // path info
        urlParts[3] = queryString;                      // query string
        
        return urlParts;
    }

    /**
     * Construct
     * 
     * Note: It's assumed scriptUrl contains context path
     * 
     * @param scriptUrl
     * @param serviceMatch
     */
    public WebScriptRequestURLImpl(Runtime runtime, String scriptUrl, Match serviceMatch)
    {
        this(runtime, splitURL(scriptUrl), serviceMatch);
    }
    
    /**
     * Construct
     * 
     * @param scriptUrlParts
     * @param serviceMatch
     */
    public WebScriptRequestURLImpl(Runtime runtime, String[] scriptUrlParts, Match serviceMatch)
    {
        super(runtime);
        
        this.contextPath = scriptUrlParts[0];
        this.servletPath = scriptUrlParts[1];
        this.pathInfo = scriptUrlParts[2];
        this.queryString = scriptUrlParts[3];
        this.queryArgs = new HashMap<String, String>(8, 1.0f);
        this.queryArgsMulti = new HashMap<String, List<String>>(4, 1.0f);
        if (this.queryString != null)
        {
            String[] args = this.queryString.split("&");
            for (String arg : args)
            {
                String[] parts = arg.split("=");
                if (this.queryArgs.containsKey(parts[0]))
                {
                    List<String> values = this.queryArgsMulti.get(parts[0]);
                    if (values == null)
                    {
                        values = new ArrayList<String>(4);
                        this.queryArgsMulti.put(parts[0], values);
                    }
                    values.add(parts.length == 2 ? parts[1] : joinParts(parts));
                }
                else
                {
                    this.queryArgs.put(parts[0], parts.length == 2 ? parts[1] : joinParts(parts));
                }
            }
        }
        Set<String> keys = queryArgs.keySet();
        String[] names = new String[keys.size()];
        keys.toArray(names);
        this.paramNames = names;
        this.serviceMatch = serviceMatch;
    }
    
    @Override
    public String toString()
    {
        return "Request Service Match: " + serviceMatch +
               " URL: " + this.contextPath + this.servletPath + this.pathInfo + "?" + queryString;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServiceMatch()
     */
    public Match getServiceMatch()
    {
        return serviceMatch;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getContextPath()
     */
    public String getContextPath()
    {
        return contextPath;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServiceContextPath()
     */
    public String getServiceContextPath()
    {
        return getContextPath() + servletPath;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServicePath()
     */
    public String getServicePath()
    {
        return getServiceContextPath() + pathInfo;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getURL()
     */
    public String getURL()
    {
        return getServicePath() + (queryString != null ? "?" + queryString : "");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getPathInfo()
     */
    public String getPathInfo()
    {
       return pathInfo; 
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getQueryString()
     */
    public String getQueryString()
    {
        return queryString;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameterNames()
     */
    public String[] getParameterNames()
    {
        return paramNames;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name)
    {
        return queryArgs.get(name);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getArrayParameter(java.lang.String)
     */
    public String[] getParameterValues(String name)
    {
        List<String> values = queryArgsMulti.get(name);
        if (values != null)
        {
            String[] array = new String[values.size()];
            values.toArray(array);
            return array;
        }
        else
        {
            String value = queryArgs.get(name);
            if (value != null)
            {
                return new String[]{value};
            }
        }
        return null;
    }
    
    private static String joinParts(final String[] parts)
    {
        final StringBuilder buf = new StringBuilder(parts.length << 4);
        for (int i=1; i<parts.length; i++)
        {
            if (i != 1)
            {
                buf.append('=');
            }
            buf.append(parts[i] != null ? parts[i] : "");
        }
        return buf.toString();
    }
}
