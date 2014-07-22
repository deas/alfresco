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

package org.springframework.extensions.config;

import java.util.List;
import java.util.Map;

/**
 * Definition of a Configuration Service
 * 
 * @author gavinc
 * @author David Draper
 */
public interface ConfigService
{  
   /**
    * Retrieves the configuration for the given object
    * 
    * @param object The object to use as the basis of the lookup
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object);

   /**
    * Retrieves the configuration for the given object using the given context
    * 
    * @param object The object to use as the basis of the lookup
    * @param context The context to use for the lookup
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object, ConfigLookupContext context);
   
   /**
    * Retrieves the configuration for given object using the supplied context but retrieves the 
    * configuration from the supplied containers.
    * @param object The object to use as the basis of the lookup
    * @param context The context to use for the lookup
    * @param globalConfig The global configuration to search
    * @param sectionsByArea A map of sections by area to search
    * @param sections A list of sections to search
    * @return The Config object containing all the matching configuration data
    */
   public Config getConfig(Object object,
                           ConfigLookupContext context, 
                           ConfigImpl globalConfig,
                           Map<String, List<ConfigSection>> sectionsByArea,
                           List<ConfigSection> sections);
   /**
    * Returns just the global configuration, this allows the config service to be 
    * used independently of objects if desired (all config is placed in a global section).
    * 
    * @return The global config section or null if there isn't one
    */
   public Config getGlobalConfig();
   
   /**
    * Resets the Config Service
    */
   public void reset();
   
   public List<ConfigDeployment> appendConfig(ConfigSource configSource);
   
   public void addDeployer(ConfigDeployer configDeployer);
}
