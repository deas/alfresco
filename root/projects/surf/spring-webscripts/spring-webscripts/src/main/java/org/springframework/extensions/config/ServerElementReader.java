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

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * @author David Caruana
 */
public class ServerElementReader implements ConfigElementReader
{
   public static final String ELEMENT_SCHEME = "scheme";
   public static final String ELEMENT_HOSTNAME = "hostname";
   public static final String ELEMENT_PORT = "port";
   
   /**
    * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   @SuppressWarnings("unchecked")
   public ConfigElement parse(Element element)
   {
      ServerConfigElement configElement = new ServerConfigElement();
      
      if (element != null)
      {
         if (ServerConfigElement.CONFIG_ELEMENT_ID.equals(element.getName()) == false)
         {
            throw new ConfigException("ServerElementReader can only parse config elements of type '" + ServerConfigElement.CONFIG_ELEMENT_ID + "'");
         }
         
         Element schemeElem = element.element(ELEMENT_SCHEME);
         if (schemeElem != null)
         {
             configElement.setScheme(schemeElem.getTextTrim());
         }
         Element hostnameElem = element.element(ELEMENT_HOSTNAME);
         if (hostnameElem != null)
         {
             configElement.setHostName(hostnameElem.getTextTrim());
         }
         Element portElem = element.element(ELEMENT_PORT);
         if (portElem != null)
         {
             try
             {
                 Integer port = new Integer(portElem.getTextTrim());
                 configElement.setPort(port);
             }
             catch(NumberFormatException e)
             {
                 throw new ConfigException("Server port is not a number", e);
             }
         }
      }
      
      return configElement;
   }
}
