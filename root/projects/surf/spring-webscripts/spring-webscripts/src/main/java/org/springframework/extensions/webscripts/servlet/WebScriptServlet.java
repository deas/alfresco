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
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.ServerConfigElement;
import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.RuntimeContainer;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Entry point for Web Scripts
 * 
 * @author davidc
 */
public class WebScriptServlet extends HttpServlet
{
    private static final long serialVersionUID = 4209892938069597860L;

    // Logger
    private static final Log logger = LogFactory.getLog(WebScriptServlet.class);

    // Component Dependencies
    protected RuntimeContainer container;
    protected ServletAuthenticatorFactory authenticatorFactory;
    protected ConfigService configService;

    /** Host Server Configuration */
    protected static ServerProperties serverProperties;

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException
    {
        super.init();
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        configService = (ConfigService)context.getBean("web.config");
        String containerName = getServletConfig().getInitParameter("container");
        if (containerName == null)
        {
            containerName = "webscripts.container";
        }
        container = (RuntimeContainer)context.getBean(containerName);
        
        // retrieve authenticator factory
        String authenticatorId = getInitParameter("authenticator");
        if (authenticatorId != null && authenticatorId.length() > 0)
        {
            Object bean = context.getBean(authenticatorId);
            if (bean == null || !(bean instanceof ServletAuthenticatorFactory))
            {
                throw new ServletException("Initialisation parameter 'authenticator' does not refer to a servlet authenticator factory (" + authenticatorId + ")");
            }
            authenticatorFactory = (ServletAuthenticatorFactory)bean;
        }
        
        // retrieve host server configuration 
        Config config = configService.getConfig("Server");
        serverProperties = (ServerConfigElement)config.getConfigElement(ServerConfigElement.CONFIG_ELEMENT_ID);
        
        // servlet specific initialisation
        initServlet(context);
        
        if (logger.isDebugEnabled())
            logger.debug("Initialised Web Script Servlet (authenticator='" + authenticatorId + "')");
    }

    /* (non-Javadoc) 
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        if (logger.isDebugEnabled())
            logger.debug("Processing request ("  + req.getMethod() + ") " + req.getRequestURL() + (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
        
        if (req.getCharacterEncoding() == null)
        {
            req.setCharacterEncoding("UTF-8");
        }
        
        setLanguageFromRequestHeader(req);
        
        try
        {
            WebScriptServletRuntime runtime = new WebScriptServletRuntime(container, authenticatorFactory, req, res, serverProperties);
            if (req.getMethod().equals(HttpMethod.OPTIONS.name()))
            {
                // respond to OPTIONS request with list of support methods for the WebScript
                String allow = HttpMethod.OPTIONS.name();
                for (HttpMethod supportedMethod : runtime.getSupportedMethods())
                {
                    allow += ", " + supportedMethod.name();
                }
                res.setHeader("Allow", allow);
            }
            else
            {
                // anything else, execute the script method
                runtime.executeScript();
            }
        }
        finally
        {
            // clear threadlocal
            I18NUtil.setLocale(null);
        }
    }
    
    /**
     * Servlet specific initialisation
     * 
     * @param context
     */
    protected void initServlet(ApplicationContext context)
    {
        // NOOP
    }
    
    /**
     * Apply Client and Repository language locale based on the 'Accept-Language' request header
     */
    public static void setLanguageFromRequestHeader(HttpServletRequest req)
    {
        // set language locale from browser header
        String acceptLang = req.getHeader("Accept-Language");
        if (acceptLang != null && acceptLang.length() != 0)
        {
            StringTokenizer t = new StringTokenizer(acceptLang, ",; ");
            // get language and convert to java locale format
            String language = t.nextToken().replace('-', '_');
            I18NUtil.setLocale(I18NUtil.parseLocale(language));
        }
    }
}
