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

package org.springframework.extensions.webscripts.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.extensions.surf.core.processor.ProcessorExtension;
import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.Store;
import org.springframework.extensions.webscripts.TemplateProcessor;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * Abstract class for use in helping developers to build template
 * processors that take advantage of the web script framework's
 * inherent support for search paths.
 * 
 * @author muzquiano
 */
public abstract class AbstractTemplateProcessor extends BaseRegisterableTemplateProcessor implements ApplicationContextAware, TemplateProcessor
{	
    private static Log logger = LogFactory.getLog(AbstractTemplateProcessor.class);
    
    private ApplicationContext applicationContext;
    
    /** Template loading search path */
    private SearchPath searchPath;

    /** List of loaders found when processing the SearchPath */
    private List<TemplateLoader> loaders = new ArrayList<TemplateLoader>();
    
    /** Object that gets the script location for the script at the specified path */
    private TemplateLoader templateLoader;

    /**
     * Sets the application context.
     * 
     * @param applicationContext the new application context
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }    
                
    /**
     * @param searchPath
     */
    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
    }
    
    /**
     * Gets the loader.
     * 
     * @return the loader
     */
    protected TemplateLoader getTemplateLoader()
    {
        return this.templateLoader;
    }
    
    /**
     * Gets the search path.
     * 
     * @return the search path
     */
    protected SearchPath getSearchPath()
    {
        return this.searchPath;
    }
    
    /**
     * Initializes the template loaders
     */
    protected void initLoaders() 
    {
        if (searchPath != null)
        {
           for (Store apiStore : searchPath.getStores())
           {
               TemplateLoader loader = apiStore.getTemplateLoader();
               if (loader == null)
               {
            	   logger.error("Unable to retrieve template loader for Web Script store " + apiStore.getBasePath());
               }
               loaders.add(loader);
           }
        }
        
        this.templateLoader = new MultiTemplateLoader(loaders.toArray(new TemplateLoader[loaders.size()]));
    } 
    
    /**
     * Add any configured processor model extensions to the model.
     * 
     * @param model
     */
    protected void addProcessorModelExtensions(Object model)
    {
        // there's always a model, if only to hold the extension objects
        if (model == null)
        {
            model = new HashMap<String, Object>();
        }
        if (model instanceof Map)
        {
            // add any processor extensions
            for (ProcessorExtension ex : this.processorExtensions.values()) 
            {
                ((Map<String, Object>)model).put(ex.getExtensionName(), ex);
            }
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
                // call init method
                init();
                
                // register with root processor
                register();
            }
        }
    }   
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.processor.BaseRegisterableTemplateProcessor#init()
     */
    public void init()
    {
        this.initLoaders();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.processor.BaseRegisterableTemplateProcessor#register()
     */
    public void register()
    {
        this.getTemplateProcessorRegistry().registerTemplateProcessor(this);
    }
}
