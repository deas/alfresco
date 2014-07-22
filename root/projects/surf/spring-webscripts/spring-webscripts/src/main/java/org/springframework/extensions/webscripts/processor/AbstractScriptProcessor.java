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

import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.surf.core.processor.ProcessorExtension;
import org.springframework.extensions.webscripts.MultiScriptLoader;
import org.springframework.extensions.webscripts.ScriptLoader;
import org.springframework.extensions.webscripts.ScriptProcessor;
import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.Store;
import org.springframework.extensions.webscripts.WebScriptException;

/**
 * Abstract class for use in helping developers to build script
 * processors that take advantage of the web script framework's
 * inherent support for search paths.
 * 
 * @author muzquiano
 */
public abstract class AbstractScriptProcessor extends BaseRegisterableScriptProcessor implements ApplicationContextAware, ScriptProcessor
{	
    /** Script loading SearchPath */
    private SearchPath searchPath;
    
    /** Object that gets the script location for the script at the specified path */
    private ScriptLoader scriptLoader;    
    
    /**
     * @param searchPath
     */
    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
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
     * Gets the script loader.
     * 
     * @return the script loader
     */
    protected ScriptLoader getScriptLoader()
    {
        return this.scriptLoader;
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
    
    /**
     * Initializes the script loaders
     */
    protected void initLoaders() 
    {
        List<ScriptLoader> loaders = new ArrayList<ScriptLoader>(searchPath.getStores().size());
        for (Store apiStore : searchPath.getStores())
        {
            ScriptLoader loader = apiStore.getScriptLoader();
            if (loader == null)
            {
                throw new WebScriptException("Unable to retrieve script loader for Web Script store " + apiStore.getBasePath());
            }
            loaders.add(loader);
        }
        this.scriptLoader = new MultiScriptLoader(loaders.toArray(new ScriptLoader[loaders.size()]));
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.processor.BaseRegisterableScriptProcessor#init()
     */
    public void init()
    {
        this.initLoaders();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.processor.BaseRegisterableScriptProcessor#register()
     */
    public void register()
    {
        this.getScriptProcessorRegistry().registerScriptProcessor(this);
    }
}
