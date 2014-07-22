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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigImpl;
import org.springframework.extensions.config.ConfigLookupContext;
import org.springframework.extensions.config.ConfigSection;
import org.springframework.extensions.config.ConfigService;

/**
 * Model representation of configuration for use in scripts.
 * 
 * @author Gavin Cornwell
 */
public class ExtendedScriptConfigModel extends ScriptConfigModel
{
   private static Log logger = LogFactory.getLog(ExtendedScriptConfigModel.class);
   
   /**
    * Constructor
    * 
    * @param configService ConfigService instance
    * @param scriptConfig The script's config as XML string
    */
   public ExtendedScriptConfigModel(ConfigService configService,
                                               String xmlConfig,
                                               ConfigImpl globalConfig,
                                               Map<String, List<ConfigSection>> sectionsByArea,
                                               List<ConfigSection> sections)
   {
      super(configService, xmlConfig);

      // Set the PRIVATE instance variable, then use it to set the global config elements to be used...
      this.globalConfig = globalConfig;
      if (globalConfig != null)
      {
          this.globalConfigElements = globalConfig.getConfigElements();
      }
      else
      {
          this.globalConfigElements = Collections.emptyMap();
      }
      
      
      this.sectionsByArea = sectionsByArea;
      this.sections = sections;
      
      if (logger.isDebugEnabled())
         logger.debug(this.toString() + " created:\nconfig service: " + 
                  this.configService + "\nglobal config: " + this.globalConfig +
                  "\nscript config: " + this.scriptConfig);
   }

    private ConfigImpl globalConfig;
    private Map<String, List<ConfigSection>> sectionsByArea;
    private List<ConfigSection> sections;
    private Map<String, ConfigElement> globalConfigElements;
   
    /**
     * Returns the script's config as a String
     * 
     * @return Script config as a String
     */
    @Override
    public Object getScript()
    {
       return this.scriptConfig;
    }

    @Override
    public Map<String, ConfigElement> getGlobal()
    {
        return this.globalConfigElements;
    }
    
    /**
     * Retrieves scoped configuration as a Map.
     * 
     * @return Map of the scoped config
     */
    @SuppressWarnings("unchecked")
    public Map<String, ConfigElement> getScoped()
    {
       return new ExtendedScopedConfigMap();
    }
    
    /**
     */
    @SuppressWarnings({ "serial", "rawtypes" })
    public class ExtendedScopedConfigMap extends HashMap
    {
       @Override
       public Object get(Object identifier)
       {
          if (logger.isDebugEnabled())
             logger.debug("Getting scoped config for '" + identifier + "'");
          
          Map<String, ConfigElement> map = null;
          if (configService != null)
          {
             Config result = configService.getConfig(identifier, new ConfigLookupContext(), globalConfig, sectionsByArea, sections);
             map = result.getConfigElements();
          }
          else
          {
             map = Collections.emptyMap();
          }
          
          if (logger.isDebugEnabled())
             logger.debug("Returning config for '" + identifier + "': " + map);
          
          return map;
       }
    }
}
