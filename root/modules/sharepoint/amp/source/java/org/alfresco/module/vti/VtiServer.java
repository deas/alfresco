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

package org.alfresco.module.vti;

import java.net.BindException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.webdav.WebDAVSessionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.mortbay.jetty.servlet.HashSessionManager;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;


/**
 * Embedded HTTP server that process all VTI requests.   
 * 
 * @author Mike Shavnev
 */
public class VtiServer extends AbstractLifecycleBean
{
    private static Log logger = LogFactory.getLog(VtiServer.class);
   
    private Server server;
    private Connector connector;
    private HttpServlet servlet;
    private List<String> servletMappings;
    private Filter filter;
    private String sessionCookieName;
    private HashSessionIdManager hashSessionIdManager;
    private HashSessionManager hashSessionManager;
    private WebDAVSessionListener webDAVSessionListener;
    
    /**
     * Set the HTTP connector
     * 
     * @param connector HTTP Connector 
     */
    public void setConnector(Connector connector)
    {
		this.connector = connector;
	}
    
    /**
     * Set the main VtiServlet. All the requests will be processed by it.
     * 
     * @param servlet HTTP Servlet 
     */
    public void setServlet(HttpServlet servlet)
    {
		this.servlet = servlet;
	}
    
    /**
     * Set the servlet mapping patterns for the Sharepoint dispatcher servlet.
     * 
     * @param servletMappings
     */
    public void setServletMappings(List<String> servletMappings)
    {
        this.servletMappings = new ArrayList<String>();
        for (String mapping : servletMappings)
        {
            // Double-slash possible when vti.server.url.path.prefix is "/"
            // and mapping produced in vti-context.xml after placeholder interpolation is "//*"
            mapping = mapping.replaceAll("//", "/");
            
            if (!this.servletMappings.contains(mapping))
            {
                this.servletMappings.add(mapping);
            }
        }
    }

    /**
     * Set the main VtiFilter. All the requests will be filtered by it.
     * 
     * @param filter HTTP Filter 
     */
    public void setFilter(Filter filter) 
    {
		this.filter = filter;
	}
   
    /**
     * Set the HashSessionIdManager
     *
     * @param hashSessionIdManager
     */
    public void setHashSessionIdManager(HashSessionIdManager hashSessionIdManager)
    {
		this.hashSessionIdManager = hashSessionIdManager;
	}
    
    /**
     * Sets the name of the cookie to be used for tracking sessions,
     *  typically used just on Debug / Information pages.
     * 
     * This should be different to the session cookie name used
     *  in the main Alfresco application.
     */
    public void setSessionCookieName(String sessionCookieName)
    {
         this.sessionCookieName = sessionCookieName;
    }
    
    /**
     * Set the HashSessionManager
     *
     * @param hashSessionManager
     */
    public void setHashSessionManager(HashSessionManager hashSessionManager)
    {
        this.hashSessionManager = hashSessionManager;
    }

    /**
     * Set the WebDAVSessionListener
     * 
     * @param webDAVSessionListener
     */
    public void setWebDAVSessionListener(WebDAVSessionListener webDAVSessionListener)
    {
        this.webDAVSessionListener = webDAVSessionListener;
    }

    /**
     * Method checks that all mandatory fiedls are set.
     * 
     * @throws RuntimeException Exception is thrown if at least one mandatory field isn't set.
     */
    private void check()
    {
    	if (servlet == null)
        {
            throw new AlfrescoRuntimeException("Error start VtiServer, cause: Property 'servlet' not set");
        }
        if (filter == null)
        {
            throw new AlfrescoRuntimeException("Error start VtiServer, cause: Property 'filter' not set");
        }

        if (connector == null)
        {
            throw new AlfrescoRuntimeException("Error start VtiServer, cause: Property 'connector' not set");
        }
    }

   /**
    * Method starts the server.  
    * @see org.springframework.extensions.surf.util.AbstractLifecycleBean#onBootstrap(org.springframework.context.ApplicationEvent)
    */
   @Override
   protected void onBootstrap(ApplicationEvent event) 
   {
      check();
      
      server = new Server();
      server.setStopAtShutdown(true);
      server.setConnectors(new Connector[] { connector });
      server.setSessionIdManager(hashSessionIdManager);

      Context context = new Context(server, "/", Context.SESSIONS);
      
      // Map the dispatcher servlet to URL patterns.
      ServletHolder servletHolder = new ServletHolder(servlet);
      for (String pattern : servletMappings)
      {
          context.addServlet(servletHolder, pattern);
      }
      
      // All requests will be filtered
      context.addFilter(new FilterHolder(filter), "/*", Handler.REQUEST | Handler.FORWARD);

      hashSessionManager.addEventListener(webDAVSessionListener);
      hashSessionManager.setSessionCookie(sessionCookieName);
      context.getSessionHandler().setSessionManager(hashSessionManager);
      try 
      {
         server.start();
         
         if (logger.isInfoEnabled())
            logger.info("Vti server started successfully on port: " + this.connector.getPort());
            logger.info("Vti server SessionIdManagerWorkerName: " + this.hashSessionIdManager.getWorkerName());
      } 
      catch (BindException be)
      {
          throw new AlfrescoRuntimeException("Unable to start the VtiServer, as the port " + 
                  this.connector.getPort() + " is already in use", be);
      }
      catch (Exception e) 
      {
         throw new AlfrescoRuntimeException("Error starting VtiServer, cause: ", e);
      }
   }

   /**
     * Method stops the server.  
     * @see org.springframework.extensions.surf.util.AbstractLifecycleBean#onBootstrap(org.springframework.context.ApplicationEvent)
     */
	@Override
	protected void onShutdown(ApplicationEvent event) 
	{
		try 
		{
			server.stop();
		} 
		catch (Exception e)
		{
			throw new AlfrescoRuntimeException("Error stop VtiServer, cause: ", e);
		}
	}
   
}
