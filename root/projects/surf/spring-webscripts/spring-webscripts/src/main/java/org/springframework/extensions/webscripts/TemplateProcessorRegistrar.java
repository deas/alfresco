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

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.processor.AbstractTemplateProcessor;

/**
 * Registers a template processor with the web script framework
 * 
 * @author muzquiano
 */
public class TemplateProcessorRegistrar implements ApplicationContextAware
{
    private static final String WEBSCRIPTS_TEMPLATE_REGISTRY_ID = "webscripts.web.templateregistry";
    private static final String WEBSCRIPTS_SEARCHPATH_ID = "webscripts.searchpath";
    
    private ApplicationContext applicationContext;  
    private TemplateProcessorRegistry registry;
    private TemplateProcessorFactory factory;
    private SearchPath searchPath;
    private String name;
    private String extension;
    
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    public void setRegistry(TemplateProcessorRegistry registry)
    {
        this.registry = registry;
    }
    
    public void setFactory(TemplateProcessorFactory factory)
    {
        this.factory = factory;
    }
    
    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setExtension(String extension)
    {
        this.extension = extension;
    }
    
    public void init()
    {
        // if a registry override hasn't been provided, we'll use the default registry
        if (this.registry == null)
        {
            registry = (TemplateProcessorRegistry) applicationContext.getBean(WEBSCRIPTS_TEMPLATE_REGISTRY_ID);
        }
        
        if (this.factory != null)
        {
            TemplateProcessor templateProcessor = factory.newInstance();
            if (templateProcessor instanceof AbstractTemplateProcessor)
            {
                if (searchPath == null)
                {
                    searchPath = (SearchPath) applicationContext.getBean(WEBSCRIPTS_SEARCHPATH_ID);
                }
                ((AbstractTemplateProcessor)templateProcessor).setSearchPath(searchPath);
            }
            registry.registerTemplateProcessor(templateProcessor, extension, name);
        }
    }
}