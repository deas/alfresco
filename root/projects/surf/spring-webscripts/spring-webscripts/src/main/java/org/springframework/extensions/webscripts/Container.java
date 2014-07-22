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

import java.util.Map;

import org.springframework.extensions.config.ConfigService;


/**
 * Web Script Container
 * 
 * @author davidc
 */
public interface Container
{
	/**
	 * Gets the name of the Container
	 * 
	 * @return  name
	 */
    public String getName();
    	
	/**
	 * Gets the Container Description
	 * 
	 * @return  description
	 */
    public ServerModel getDescription();
    
    /**
     * Gets the Script Processor Registry
     * 
     * @return  script processor registry
     */
    public ScriptProcessorRegistry getScriptProcessorRegistry();
    
    /**
     * Get the Script Parameter Factory Registry
     * 
     * @return script parameter factory registry
     */
    public ScriptParameterFactoryRegistry getScriptParameterFactoryRegistry();
    
    /**
     * Gets parameters for the Script Processor
     *  
     * @return  parameters
     */
    public Map<String, Object> getScriptParameters();

    /**
     * Gets the Template Processor Registry
     *  
     * @return  template processor registry
     */
    public TemplateProcessorRegistry getTemplateProcessorRegistry();
    
    /**
     * Gets parameters for the Template Processor
     * 
     * @return  parameters
     */
    public Map<String, Object> getTemplateParameters();    
    
    /**
     * Gets the response format registry
     * 
     * @return  response format registry
     */
    public FormatRegistry getFormatRegistry();
    
    /**
     * Gets the registry of Web Scripts
     * 
     * @return  registry of web scripts
     */
    public Registry getRegistry();
    
    /**
     * Gets the Search Path
     * 
     * @return search path
     */
    public SearchPath getSearchPath();
    
    /**
     * Gets the Config Service
     * 
     * @return config service
     */
    public ConfigService getConfigService();
    
    /**
     * Returns if this container should allow callback methods such as json_callback
     * 
     * @return true if this container should allow callback methods such as json_callback, false otherwise
     */
    public boolean allowCallbacks();

    /**
     * Re-initialise the Web Script Container
     */
    public void reset();    

}
