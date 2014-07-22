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

import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigService;
import org.xml.sax.InputSource;

import freemarker.ext.dom.NodeModel;

/**
 * Model representation of configuration for use in scripts.
 *
 * @author Gavin Cornwell
 */
public class TemplateConfigModel extends ConfigModel
{
   private Object model;
   
   private static Log logger = LogFactory.getLog(TemplateConfigModel.class);

   /**
    * Constructor
    * 
    * @param configService ConfigService instance
    * @param scriptConfig The script's config as XML string
    */
   public TemplateConfigModel(ConfigService configService, String scriptConfig)
   {
      super(configService, scriptConfig);
      
      if (logger.isDebugEnabled())
         logger.debug(this.toString() + " created:\nconfig service: " + 
                  this.configService + "\nglobal config: " + this.globalConfig +
                  "\nscript config: " + this.scriptConfig);
   }
   
   /**
    * Returns the script's config as a Freemarker NodeModel object
    * 
    * @return Script config as a Freemarker NodeModel object
    */
   @Override
   public Object getScript()
   {
      if (this.model == null)
      {
          // create the model, if we have something to create it with
          if (this.scriptConfig != null)
          {
             StringReader reader = new StringReader(this.scriptConfig);
             InputSource is = new InputSource(reader);
             try
             {
                this.model = NodeModel.parse(is);
             }
             catch (Exception e)
             {
                if (logger.isWarnEnabled())
                   logger.warn("Failed to create 'script' config model: " + e.getMessage());
             }
          }
          else
          {
              // if we make it here return empty model
              this.model = NodeModel.NOTHING;
          }
      }
      
      return this.model;
   }
}
