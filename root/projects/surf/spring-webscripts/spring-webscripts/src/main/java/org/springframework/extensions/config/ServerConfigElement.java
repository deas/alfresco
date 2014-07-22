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

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * @author David Caruana
 */
public class ServerConfigElement extends ConfigElementAdapter implements ServerProperties
{
   public static final String CONFIG_ELEMENT_ID = "server";
   
   private String scheme = null;
   private String hostname = null;
   private Integer port = null;
      
   /**
    * Default constructor
    */
   public ServerConfigElement()
   {
      super(CONFIG_ELEMENT_ID);
   }

   /**
    * Constructor
    * 
    * @param name Name of the element this config element represents
    */
   public ServerConfigElement(String name)
   {
      super(name);
   }
   
   /**
    * @see org.springframework.extensions.config.element.ConfigElementAdapter#getChildren()
    */
   public List<ConfigElement> getChildren()
   {
      throw new ConfigException("Reading the Server config via the generic interfaces is not supported");
   }
   
   /**
    * @see org.springframework.extensions.config.element.ConfigElementAdapter#combine(org.springframework.extensions.config.ConfigElement)
    */
   public ConfigElement combine(ConfigElement configElement)
   {
      ServerConfigElement newElement = (ServerConfigElement)configElement;
      ServerConfigElement combinedElement = new ServerConfigElement();
      
      combinedElement.setScheme(newElement.getScheme());
      combinedElement.setHostName(newElement.getHostName());
      combinedElement.setPort(newElement.getPort());
      
      return combinedElement;
   }
   
   /**
    * @return  server scheme
    */
   public String getScheme()
   {
      return scheme;
   }

   /**
    * @param scheme
    */
   public void setScheme(String scheme)
   {
      this.scheme = scheme;
   }
      
   /**
    * @return  server hostname
    */
   public String getHostName()
   {
      return hostname;
   }

   /**
    * @param hostname
    */
   public void setHostName(String hostname)
   {
      this.hostname = hostname;
   }

   /**
    * @return  server port
    */
   public Integer getPort()
   {
      return port;
   }

   /**
    * @param port
    */
   public void setPort(Integer port)
   {
      this.port = port;
   }

}
