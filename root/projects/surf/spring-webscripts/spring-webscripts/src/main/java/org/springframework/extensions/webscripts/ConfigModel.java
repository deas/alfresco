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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;

/**
 * Abstract base class used for objects that represent configuration 
 * as a root object in a script or template model.
 * 
 * @author gavinc
 */
public abstract class ConfigModel
{
   protected ConfigService configService;
   protected Map<String, ConfigElement> globalConfig;
   protected String scriptConfig;
   
   private static Log logger = LogFactory.getLog(ConfigModel.class);
   
   public ConfigModel(ConfigService configService, String scriptConfig)
   {
      this.configService = configService;
      this.scriptConfig = scriptConfig;
   }

   /**
    * Retrieves the global configuration as a Map.
    * 
    * @return Map of the global config
    */
   public Map<String, ConfigElement> getGlobal()
   {
      if (this.globalConfig == null)
      {
         if (this.configService != null)
         {
            // get the global config
            this.globalConfig = this.configService.getGlobalConfig().getConfigElements();
         }
   
         // if no global config was found create an empty map
         if (this.globalConfig == null)
         {
            this.globalConfig = Collections.emptyMap();
         }
      }
      
      return this.globalConfig;
   }
   
   /**
    * Retrieves scoped configuration as a Map.
    * 
    * @return Map of the scoped config
    */
   @SuppressWarnings("unchecked")
   public Map<String, ConfigElement> getScoped()
   {
      return new ScopedConfigMap();
   }
   
   /**
    * Retrieves the script configuration.<br/>
    * It's up to the subclass what is returned to represent script config.
    * 
    * @return script configuration
    */
   public abstract Object getScript();
   
   /**
    * Map to allow access to scoped config in a unified way 
    * for scripts and templates.
    * 
    * @author gavinc
    */
   @SuppressWarnings({ "serial", "rawtypes" })
   public class ScopedConfigMap extends HashMap
   {
      @Override
      public Object get(Object identifier)
      {
         if (logger.isDebugEnabled())
            logger.debug("Getting scoped config for '" + identifier + "'");
         
         Map<String, ConfigElement> map = null;
         
         if (configService != null)
         {
            Config result = configService.getConfig(identifier);
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




