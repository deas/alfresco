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

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.extensions.webscripts.ScriptProcessorRegistry;

/**
 * Base class for a registerable processor
 * 
 * @author muzquiano
 */
public abstract class BaseRegisterableScriptProcessor extends BaseProcessor implements ApplicationContextAware, ApplicationListener
{
    private ApplicationContext applicationContext;
    
    private ScriptProcessorRegistry scriptProcessorRegistry;
    
    /**
     * Sets the script processor registry to which this processor
     * will be registered
     * 
     * @param scriptProcessorRegistry the script processor registry
     */
    public void setScriptProcessorRegistry(ScriptProcessorRegistry scriptProcessorRegistry)
    {
        this.scriptProcessorRegistry = scriptProcessorRegistry;
    }
    
    /**
     * Gets the script processor registry
     * 
     * @return script processor registry
     */
    public ScriptProcessorRegistry getScriptProcessorRegistry()
    {
        return this.scriptProcessorRegistry;
    }
    
    /**
     * Sets the application context.
     * 
     * @param applicationContext the new application context
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
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
    
    /**
     * Inits the processor
     */
    public abstract void init();

    /**
     * Registers this processor with the parent processor
     */
    public abstract void register();
}