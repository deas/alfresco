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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;


/**
 * Encapsulates a Container within which the Web Script Runtime executes.
 * 
 * Container examples - presentation (web tier), repository (server tier)
 * 
 * @author dcaruana
 */
public abstract class AbstractRuntimeContainer
    implements RuntimeContainer, ApplicationListener, ApplicationContextAware
{
    // Logger
    private static final Log logger = LogFactory.getLog(AbstractRuntimeContainer.class);
    
    protected ApplicationContext applicationContext = null;
    private boolean allowCallbacks = false;
    private String name = "<undefined>";
    private Registry registry;
    private FormatRegistry formatRegistry;
    private ScriptProcessorRegistry scriptProcessorRegistry;
    private TemplateProcessorRegistry templateProcessorRegistry;
    private ScriptParameterFactoryRegistry scriptParameterFactoryRegistry;
    private SearchPath searchPath; 
    private ConfigService configService;
    private Map<String, Object> scriptObjects;
    private Map<String, Object> templateObjects;

    /**
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @param formatRegistry
     */
    public void setFormatRegistry(FormatRegistry formatRegistry)
    {
        this.formatRegistry = formatRegistry;
    }

    /**
     * @param registry
     */
    public void setRegistry(Registry registry)
    {
        this.registry = registry;
    }
    
    /**
     * @param scriptProcessorRegistry
     */
    public void setScriptProcessorRegistry(ScriptProcessorRegistry scriptProcessorRegistry)
    {
        this.scriptProcessorRegistry = scriptProcessorRegistry;
    }
    
    /**
     * @param templateProcessorRegistry
     */
    public void setTemplateProcessorRegistry(TemplateProcessorRegistry templateProcessorRegistry)
    {
        this.templateProcessorRegistry = templateProcessorRegistry;
    }
    
    /**
     * @param scriptParameterFactoryRegistry
     */
    public void setScriptParameterFactoryRegistry(ScriptParameterFactoryRegistry scriptParameterFactoryRegistry)
    {
        this.scriptParameterFactoryRegistry = scriptParameterFactoryRegistry;
    }
    
    /**
     * @param searchPath
     */
    public void setSearchPath(SearchPath searchPath)
    {
       this.searchPath = searchPath;
    }
    
    /**
     * @param configService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * @param scriptObjects
     */
    public void setScriptObjects(Map<String, Object> scriptObjects)
    {
        this.scriptObjects = scriptObjects;
    }
    
    /**
     * @param templateObjects
     */
    public void setTemplateObjects(Map<String, Object> templateObjects)
    {
        this.templateObjects = templateObjects;
    }
    
    /**
     * @param allowCallbacks    try to enable callback methods, such as json_callback
     */
    public void setAllowCallbacks(boolean allowCallbacks)
    {
        this.allowCallbacks = allowCallbacks;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Container#allowCallbacks()
     */
    public boolean allowCallbacks()
    {
        return this.allowCallbacks;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.RuntimeContainer#getName()
     */
    public String getName()
    {
        return this.name;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getScriptParameters()
     */
    public Map<String, Object> getScriptParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        params.put("server", getDescription());
        params.putAll(scriptObjects);
        
        return Collections.unmodifiableMap(params);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getTemplateParameters()
     */
    public Map<String, Object> getTemplateParameters()
    {
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        params.put("server", getDescription());
        params.put("date", new Date());
        params.putAll(templateObjects);
        
        return Collections.unmodifiableMap(params);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getFormatRegistry()
     */
    public FormatRegistry getFormatRegistry()
    {
        return formatRegistry;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getRegistry()
     */
    public Registry getRegistry()
    {
        return registry;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getConfigService()
     */
    public ConfigService getConfigService()
    {
        return configService;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getScriptProcessorRegistry()
     */
    public ScriptProcessorRegistry getScriptProcessorRegistry()
    {
        return scriptProcessorRegistry;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getTemplateProcessorRegistry()
     */
    public TemplateProcessorRegistry getTemplateProcessorRegistry()
    {
        return templateProcessorRegistry;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Container#getScriptParameterFactoryRegistry()
     */
    public ScriptParameterFactoryRegistry getScriptParameterFactoryRegistry()
    {
        return scriptParameterFactoryRegistry;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#getSearchPath()
     */
    public SearchPath getSearchPath()
    {
        return searchPath;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Container#reset()
     */
    public void reset() 
    {
        long startTime = System.nanoTime();
        try
        {
            scriptProcessorRegistry.reset();
            templateProcessorRegistry.reset();
            getRegistry().reset();
            configService.reset();
        }
        finally
        {
            if (logger.isInfoEnabled())
                logger.info("Initialised " + getName() + " Web Script Container (in " + (System.nanoTime() - startTime)/1000000f + "ms)");
        }        
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
            if (refreshContext != null && refreshContext.equals(applicationContext))
            {
                reset();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * Gets the Application Context
     * 
     * @return  application context
     */
    protected ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.RuntimeContainer#getRequiredAuthentication()
     */
    public RequiredAuthentication getRequiredAuthentication()
    {
        return RequiredAuthentication.none;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.RuntimeContainer#authenticate(org.alfresco.web.scripts.Authenticator, org.alfresco.web.scripts.Description.RequiredAuthentication)
     */
    public boolean authenticate(Authenticator auth, RequiredAuthentication required)
    {
        if (! ((required == null) || (required.equals(RequiredAuthentication.none))))
        {
            logger.error("Unexpected - required authentication = "+required);
            return false;
        }
        
        return true;
    }
}
