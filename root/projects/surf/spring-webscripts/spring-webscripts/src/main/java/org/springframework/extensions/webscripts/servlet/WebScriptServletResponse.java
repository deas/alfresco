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
import java.io.OutputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptResponseImpl;
import org.springframework.extensions.webscripts.ui.common.StringUtils;

/**
 * HTTP Servlet Web Script Response
 * 
 * @author davidc
 */
public class WebScriptServletResponse extends WebScriptResponseImpl
{
    private static final String NO_CACHE = "no-cache";

    // Logger
    private static final Log logger = LogFactory.getLog(WebScriptServletResponse.class);

    // Servlet Response
    private HttpServletResponse res;

    
    /**
     * Construct
     * 
     * @param res
     */
    public WebScriptServletResponse(Runtime container, HttpServletResponse res)
    {
        super(container);
        this.res = res;
    }

    /**
     * Gets the HTTP Servlet Response
     * 
     * @return  HTTP Servlet Response
     */
    public HttpServletResponse getHttpServletResponse()
    {
        return res;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#setStatus(int)
     */
    public void setStatus(int status)
    {
        res.setStatus(status);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#setHeader(java.lang.String, java.lang.String)
     */
    public void setHeader(String name, String value)
    {
        res.setHeader(name, value);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#addHeader(java.lang.String, java.lang.String)
     */
    public void addHeader(String name, String value)
    {
        res.addHeader(name, value);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#setContentType(java.lang.String)
     */
    public void setContentType(String contentType)
    {
        res.setContentType(contentType);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#setContentEncoding(java.lang.String)
     */
    public void setContentEncoding(String contentEncoding)
    {
        res.setCharacterEncoding(contentEncoding);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#setCache(org.alfresco.web.scripts.WebScriptCache)
     */
    public void setCache(Cache cache)
    {
        // set Cache-Control
        String cacheControl = "";
        String pragma = "";
        if (cache.getIsPublic())
        {
            cacheControl += "public";
        }
        if (cache.getNeverCache())
        {
            cacheControl += (cacheControl.length() > 0 ? ", " : "") + NO_CACHE;
            pragma += (pragma.length() > 0) ? ", " : "" + NO_CACHE;
        }
        if (cache.getMaxAge() != null && cache.getNeverCache() == false)
        {
            cacheControl += (cacheControl.length() > 0 ? ", " : "") + " max-age=" + cache.getMaxAge();
        }
        if (cache.getMustRevalidate() && cache.getNeverCache() == false)
        {
            cacheControl += (cacheControl.length() > 0 ? ", " : "") + " must-revalidate";
        }
        if (cacheControl.length() > 0)
        {
            res.setHeader("Cache-Control", cacheControl);
            if (logger.isDebugEnabled())
                logger.debug("Cache - set response header Cache-Control: " + cacheControl);
            // special case for IE Ajax request handling
            if (NO_CACHE.equals(cacheControl))
            {
               res.setHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT");
            }
        }
        if (pragma.length() > 0)
        {
            res.setHeader("Pragma", pragma);
            if (logger.isDebugEnabled())
                logger.debug("Cache - set response header Pragma: " + pragma);
        }
        
        // set ETag
        if (cache.getETag() != null)
        {
            String eTag = "\"" + cache.getETag() + "\"";
            res.setHeader("ETag", eTag);
            if (logger.isDebugEnabled())
                logger.debug("Cache - set response header ETag: " + eTag);
        }
        
        // set Last Modified
        if (cache.getLastModified() != null)
        {
            res.setDateHeader("Last-Modified", cache.getLastModified().getTime());
            if (logger.isDebugEnabled())
            {
                SimpleDateFormat formatter = getHTTPDateFormat();
                String lastModified = formatter.format(cache.getLastModified());
                logger.debug("Cache - set response header Last-Modified: " + lastModified);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#reset()
     */
    public void reset()
    {
        try
        {
            res.reset();
        }
        catch(IllegalStateException e)
        {
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#getWriter()
     */
    public Writer getWriter() throws IOException
    {
        return res.getWriter();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException
    {
        return res.getOutputStream();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#encodeScriptUrl(java.lang.String)
     */
    public String encodeScriptUrl(String url)
    {
        return url;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#getEncodeScriptUrlFunction(java.lang.String)
     */
    public String getEncodeScriptUrlFunction(String name)
    {
        // TODO: See if it's worth reusing Utils.encodeJavascript
        String s = "{ $name$: function(url) { return url; } }".replace("$name$", name);
        return StringUtils.encodeJavascript(s);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.WebScriptResponseImpl#encodeResourceUrl(java.lang.String)
     */
    public String encodeResourceUrl(String url)
    {
        return url;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.WebScriptResponseImpl#getEncodeResourceUrlFunction(java.lang.String)
     */
    public String getEncodeResourceUrlFunction(String name)
    {
        // TODO: See if it's worth reusing Utils.encodeJavascript
        String s = "{ $name$: function(url) { return url; } }".replace("$name$", name);
        return StringUtils.encodeJavascript(s);
    }
    
    /**
     * Helper to return a HTTP Date Formatter
     * 
     * @return  HTTP Date Formatter
     */
    private static SimpleDateFormat getHTTPDateFormat()
    {
        if (s_dateFormat.get() != null)
        {
            return s_dateFormat.get();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
        formatter.setLenient(false);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        s_dateFormat.set(formatter);
        return s_dateFormat.get();
    }
    
    private static ThreadLocal<SimpleDateFormat> s_dateFormat = new ThreadLocal<SimpleDateFormat>();

}
