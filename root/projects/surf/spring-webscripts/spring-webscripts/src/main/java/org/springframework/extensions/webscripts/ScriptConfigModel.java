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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigService;

/**
 * Model representation of configuration for use in scripts.
 * 
 * @author Gavin Cornwell
 */
public class ScriptConfigModel extends ConfigModel
{
   private static Log logger = LogFactory.getLog(ScriptConfigModel.class);
   
   /**
    * Constructor
    * 
    * @param configService ConfigService instance
    * @param scriptConfig The script's config as XML string
    */
   public ScriptConfigModel(ConfigService configService, String scriptConfig)
   {
      super(configService, scriptConfig);
      
      if (logger.isDebugEnabled())
         logger.debug(this.toString() + " created:\nconfig service: " + 
                  this.configService + "\nglobal config: " + this.globalConfig +
                  "\nscript config: " + this.scriptConfig);
   }

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
}
