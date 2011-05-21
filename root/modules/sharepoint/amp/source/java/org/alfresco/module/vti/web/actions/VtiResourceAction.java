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
package org.alfresco.module.vti.web.actions;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.metadata.dialog.DialogUtils;
import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.VtiRequestDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
* <p>VtiResourceAction is used for retrieving specific resource 
* for web-view (like images, css).</p>
*
*/
public class VtiResourceAction extends HttpServlet implements VtiAction, ApplicationContextAware
{

    private static final long serialVersionUID = 9073113240345164795L;

    private static Map<String, byte[]> resourcesMap = new HashMap<String, byte[]>();

    private static final ReadWriteLock resourceMapLock = new ReentrantReadWriteLock();

    private final static Log logger = LogFactory.getLog(VtiResourceAction.class);

    private ApplicationContext applicationContext;

    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public VtiResourceAction()
    {
        super();
    }

    /**
     * <p>Retrieve specific resource for web-view.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String alfrescoContext = (String) request.getAttribute(VtiRequestDispatcher.VTI_ALFRESCO_CONTEXT);
        String uri = request.getRequestURI().replaceAll(alfrescoContext + "/resources", "");
        uri = uri.replaceAll("/resources", "");
        writeResponse(uri, response, alfrescoContext);

    }

    private void writeResponse(String resourceLocation, HttpServletResponse response, String alfrescoContext) throws IOException
    {
        byte[] resource = null;

        try
        {
            resourceMapLock.readLock().lock();
            resource = resourcesMap.get(resourceLocation);
        }
        finally
        {
            resourceMapLock.readLock().unlock();
        }

        if (resource == null)
        {
            resource = cacheResource(resourceLocation, alfrescoContext);
        }

        response.getOutputStream().write(resource);
    }

    private byte[] cacheResource(String resourceLocation, String alfrescoContext) throws IOException
    {
        Resource resource = applicationContext.getResource(resourceLocation);

        if (!resource.exists())
        {
            if (resourceLocation.endsWith(DialogUtils.IMAGE_POSTFIX))
            {
                resource = applicationContext.getResource(DialogUtils.DEFAULT_IMAGE);
            }
            else
            {
                return null;
            }
        }

        InputStream input = resource.getInputStream();
      
        byte[] result = new byte[input.available()];
        input.read(result);

        try
        {
            resourceMapLock.writeLock().lock();
            resourcesMap.put(resourceLocation, result);
        }
        finally
        {
            resourceMapLock.writeLock().unlock();
        }

        return result;
    }
    
    /**
     * <p>Retrieve specific resource for web-view.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            service(request, response);
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action IO exception", e);
            }
        }
        catch (ServletException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action execution exception", e);
            }
        }
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}