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

import java.util.Locale;

import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.RuntimeContainer;
import org.springframework.extensions.webscripts.servlet.ServletAuthenticatorFactory;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Resolves views to Web Scripts
 * 
 * @author muzquiano
 */
public class WebScriptViewResolver extends AbstractWebScriptViewResolver implements ApplicationListener 
{
    protected RuntimeContainer container;
    
    protected ConfigService configService;
    protected ServletAuthenticatorFactory authenticatorFactory;
    
    
    /**
     * WebScriptViewResolver Constructor
     */
    public WebScriptViewResolver() 
    {
        Class viewClass = requiredViewClass();
        setViewClass(viewClass);
    }

    /**
     * Sets the container.
     * 
     * @param container the new container
     */
    public void setContainer(RuntimeContainer container)
    {
        this.container = container;
    }

    /**
     * Sets the authenticator factory.
     * 
     * @param authenticatorFactory the new authenticator factory
     */
    public void setAuthenticatorFactory(ServletAuthenticatorFactory authenticatorFactory)
    {
        this.authenticatorFactory = authenticatorFactory;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof ContextRefreshedEvent)
        {
            ContextRefreshedEvent refreshEvent = (ContextRefreshedEvent)event;            
            ApplicationContext refreshContext = refreshEvent.getApplicationContext();
            if (refreshContext != null && refreshContext.equals(getApplicationContext()))
            {
                onBootstrap();
            }
        }
    }
    
    /**
     * Initializes the view resolver
     * 
     * @throws ServletException
     */
    public void onBootstrap()
    {
        ApplicationContext context = this.getApplicationContext();
        configService = (ConfigService)context.getBean("web.config");
        
        // ensure that we have a container
        if (container == null)
        {
            container = (RuntimeContainer)context.getBean("webscripts.container");
        }        
        
        String authenticatorId = null;
        if (authenticatorFactory != null)
        {
            authenticatorId = authenticatorFactory.getClass().getName();
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Initialised Web Script View Resolver (authenticator='" + authenticatorId + "')");
    }
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#canHandle(java.lang.String, java.util.Locale)
     */
    protected boolean canHandle(String viewName, Locale locale) 
    {
        String uri = viewName;
        
        // path corrections
        if (uri != null)
        {
            if (uri.length() != 0 && uri.charAt(0) != '/')
            {
                uri = '/' + uri;
            }
        }
        
        // check the web script registry to see if a web script with this URI exists
        Match match = container.getRegistry().findWebScript("get", uri);
        return (match != null);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#buildView(java.lang.String)
     */
    protected AbstractUrlBasedView buildView(String viewName) throws Exception 
    {
        AbstractUrlBasedView view = null;
        
        String uri = viewName;
        
        // path corrections
        if (uri != null)
        {
        	if (uri.length() != 0 && uri.charAt(0) != '/')
            {
                uri = '/' + uri;
            }
        }
        
        // check the web script registry to see if a web script with this URI exists
        Match match = container.getRegistry().findWebScript("get", uri);
        if (match != null)
        {
            view = new WebScriptView(container, authenticatorFactory, configService);     
            view.setUrl(uri);
        }
        
        return view;
    }
}
