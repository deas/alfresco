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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.evaluator.Evaluator;

/**
 * The default algorithm used to determine whether a section applies to the object being looked up
 * 
 * @author gavinc
 */
public class DefaultLookupAlgorithm implements ConfigLookupAlgorithm
{
   private static final Log logger = LogFactory.getLog(DefaultLookupAlgorithm.class);
   
   /**
    * @see org.springframework.extensions.config.ConfigLookupAlgorithm#process(org.springframework.extensions.config.ConfigSection, org.springframework.extensions.config.evaluator.Evaluator, java.lang.Object, org.springframework.extensions.config.Config)
    */
   public void process(ConfigSection section, Evaluator evaluator, Object object, Config results)
   {
      // if the config section applies to the given object extract all the
      // config elements inside and add them to the Config object
      if (evaluator.applies(object, section.getCondition()))
      {
         if (logger.isDebugEnabled())
            logger.debug(section + " matches");

         List<ConfigElement> sectionConfigElements = section.getConfigElements();
         for (ConfigElement newConfigElement : sectionConfigElements)
         {
            // if the config element being added already exists we need to combine it or replace it
            String name = newConfigElement.getName();
            ConfigElement existingConfigElement = results.getConfigElements().get(name);
            if (existingConfigElement != null)
            {
               if (section.isReplace())
               {
                  // if the section has been marked as 'replace' and a config element
                  // with this name has already been found, replace it
                  results.getConfigElements().put(name, newConfigElement);
                  
                  if (logger.isDebugEnabled())
                     logger.debug("Replaced " + existingConfigElement + " with " + newConfigElement);
               }
               else
               {
                  // combine this config element with the previous one found with the same name
                  ConfigElement combinedConfigElement = existingConfigElement.combine(newConfigElement);
                  results.getConfigElements().put(name, combinedConfigElement);
               
                  if (logger.isDebugEnabled())
                  {
                     logger.debug("Combined " + newConfigElement + " with " + existingConfigElement + 
                                  " to create " + combinedConfigElement);
                  }
               }
            }
            else
            {
               results.getConfigElements().put(name, newConfigElement);
            }
         }
      }
   }
}
