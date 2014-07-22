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

package org.springframework.extensions.webscripts.servlet.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Spring controller for retrieving and serving resources.
 * <p>
 * This controller retrieves content by interrogating resource providers
 * in the following order:
 * <p>
 * 1) Web application context resources (Jar files, followed by classpath)
 * 2) Web application path<br>
 * 3) Delegation to a default url handler<br>
 * <p>
 * The order allows resource assets to be overriden in jar file or classpath based extensions.
 * <p>
 * The following URL format is supported:
 * <code>
 *    /res/<path>
 * </code>
 * 
 * @author kevinr
 * @author muzquiano
 */
public class ResourceController extends AbstractController
{
    private static Log logger = LogFactory.getLog(ResourceController.class);
    
    private static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";
    private static final String HTTP_HEADER_LAST_MODIFIED = "Last-Modified";
    private static final String HTTP_HEADER_ETAG = "ETag";
    private static final String HTTP_HEADER_CACHE_CONTROL = "Cache-Control";
    
    private static final Map<String, String> defaultMimeTypes = new HashMap<String, String>();
    {
        defaultMimeTypes.put(".css", "text/css");
        defaultMimeTypes.put(".gif", "image/gif");
        defaultMimeTypes.put(".ico", "image/vnd.microsoft.icon");
        defaultMimeTypes.put(".jpeg", "image/jpeg");
        defaultMimeTypes.put(".jpg", "image/jpeg");
        defaultMimeTypes.put(".js", "text/javascript");
        defaultMimeTypes.put(".png", "image/png");
        defaultMimeTypes.put(".bin", "application/octet-stream");
        defaultMimeTypes.put(".woff", "application/x-font-woff");
        defaultMimeTypes.put(".eot", "application/vnd.ms-fontobject");
        defaultMimeTypes.put(".ttf", "application/x-font-ttf");
    }
    
    /** allow for a default redirection url to be provided */
    private String defaultUrl;
    
    /** Thread local stream byte buffer */
    private ThreadLocal<byte[]> streamBuffer = new ThreadLocal<byte[]>()
    {
        @Override
        protected byte[] initialValue()
        {
            return new byte[16384];
        }
    };
    
    
    /**
     * Sets the default url.
     * 
     * @param defaultUrl the new default url
     */
    public void setDefaultUrl(String defaultUrl)
    {
        this.defaultUrl = defaultUrl;
    }
    
    /**
     * Gets the default url.
     * 
     * @return the default url
     */
    public String getDefaultUrl()
    {
        return this.defaultUrl;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.mvc.AbstractController#createModelAndView(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response)
        throws Exception
    {
        // get the path to the resource (resolved by Spring for us)
        final String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        
        dispatchResource(path, request, response);
        
        return null;
    }
    
    /**
     * Dispatches to the resource with the given path
     * 
     * @param path the path
     * @param request the request
     * @param response the response
     */
    public boolean dispatchResource(final String path, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        final boolean debug = logger.isDebugEnabled();
        if (debug) logger.debug("Attemping to dispatch resource: " + path);
        
        boolean resolved = false;
        {
            // check JAR files
            URL resourceUrl = ClassUtils.getDefaultClassLoader().getResource("META-INF/" + path);
            if (resourceUrl != null)
            {
                if (debug) logger.debug("...dispatching resource from JAR location: " + resourceUrl);
                
                // attempt to stream back
                try
                {
                    commitResponse(path, resourceUrl, response);
                    resolved = true;
                }
                catch (IOException ioe)
                {
                    logger.info(ioe.getMessage());
                    if (debug) ioe.printStackTrace();
                }
            }
        }
        
        if (!resolved)
        {
            // look up the resource in the resource provider
            // (application context resources / classpath)
            
            // check the classpath
            Resource r = getApplicationContext().getResource("classpath*:" + path);
            if (r != null && r.exists())
            {
                URL resourceUrl = r.getURL();
                
                if (debug) logger.debug("...dispatching resource from classpath location: " + resourceUrl);
                
                // stream back resource
                try
                {
                    commitResponse(path, resourceUrl, response);
                    resolved = true;
                }
                catch (IOException ioe)
                {
                    logger.info(ioe.getMessage());
                    if (debug) ioe.printStackTrace();
                }
            }
        }
        
        if (!resolved)
        {
            // serve back the resource from the web application (if it exists)
            ServletContextResource resource = new ServletContextResource(getServletContext(), "/" + path);
            if (resource.exists())
            {
                // dispatch to resource
                if (debug) logger.debug("...dispatching resource from web application ServletContext.");
                commitResponse(path, resource, request, response);
                resolved = true;
            }
        }
        
        if (!resolved && defaultUrl != null)
        {
            if (debug) logger.debug("...handing off to Spring resource servlet: " + defaultUrl + "/" + path);
            
            // try to hand off to a default servlet context (compatibility with Spring JS resource servlet)
            // use a forward here because the new servlet context may be different than the current servlet context
            RequestDispatcher rd = this.getServletContext().getRequestDispatcher(defaultUrl + "/" + path);
            rd.forward(request, response);
            resolved = true;
        }
        
        return resolved;
    }
    
    /**
     * Commit the resource to the response stream.
     * Sets appropriate date, length and content type headers.
     * 
     * @throws IOException
     */
    public void commitResponse(final String path, final URL resourceUrl, final HttpServletResponse response)
        throws IOException
    {
        // determine properties of the resource being served back
        final URLConnection resourceConn = resourceUrl.openConnection();
        applyHeaders(path, response, resourceConn.getContentLength(), resourceConn.getLastModified());
        
        // stream back to response
        copyStream(resourceUrl.openStream(), response.getOutputStream());
    }

    public void commitResponse(
            final String path, final Resource resource, final HttpServletRequest request, final HttpServletResponse response)
        throws IOException, ServletException
    {
        // determine properties of the resource being served back
        final URLConnection resourceConn = resource.getURL().openConnection();
        applyHeaders(path, response, resourceConn.getContentLength(), resourceConn.getLastModified());
        
        // stream back to response
        RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/" + path);
        rd.include(request, response);
    }
    
    protected void applyHeaders(
            final String path, final HttpServletResponse response, final long contentLength, final long lastModified)
    {
        // determine mimetype
        String mimetype = getServletContext().getMimeType(path);
        if (mimetype == null) 
        {
            String extension = ".bin";
            final int extIndex = path.lastIndexOf('.');
            if (extIndex != -1)
            {
                extension = path.substring(extIndex);
                mimetype = (String)defaultMimeTypes.get(extension.toLowerCase());
            }
        }
        
        // set response headers
        if (mimetype != null && mimetype.startsWith("text/"))
        {
            // ensure text encoding is applied
            mimetype += ";charset=UTF-8";
        }
        response.setContentType(mimetype);
        response.setHeader(HTTP_HEADER_CONTENT_LENGTH, Long.toString(contentLength));
        if (lastModified != 0)
        {
            response.setHeader(HTTP_HEADER_ETAG, '"' + Long.toString(lastModified) + '"');
            response.setDateHeader(HTTP_HEADER_LAST_MODIFIED, lastModified);
        }
        if (!response.containsHeader(HTTP_HEADER_CACHE_CONTROL))
        {
            response.setHeader(HTTP_HEADER_CACHE_CONTROL, "max-age=86400");
        }
    }
    
    /**
     * Fast stream copy method - uses ThreadLocal byte buffer to avoid reallocating byte arrays
     */
    protected void copyStream(final InputStream in, final OutputStream out) throws IOException
    {
        try
        {
            int byteCount;
            final byte[] buffer = streamBuffer.get();
            while ((byteCount = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, byteCount);
            }
            out.flush();
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException ex) {}
            try
            {
                out.close();
            }
            catch (IOException ex) {}
        }
    }
}
