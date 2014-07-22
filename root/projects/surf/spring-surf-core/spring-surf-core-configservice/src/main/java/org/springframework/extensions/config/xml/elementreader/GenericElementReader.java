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

package org.springframework.extensions.config.xml.elementreader;

import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.GenericConfigElement;
import org.springframework.extensions.config.xml.XMLConfigService.PropertyConfigurer;

/**
 * Implementation of a generic element reader. This class can be used to 
 * convert any config element into a GenericConfigElement.
 * 
 * @author gavinc
 */
public class GenericElementReader implements ConfigElementReader
{
   private PropertyConfigurer propertyConfigurer;
	
   /**
    * Construct
    * 
    * @param propertyConfigurer
    */
   public GenericElementReader(PropertyConfigurer propertyConfigurer)
   {
      this.propertyConfigurer = propertyConfigurer;
   }
   
   /**
    * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   public ConfigElement parse(Element element)
   {
      GenericConfigElement configElement = null;
      
      if (element != null)
      {
         configElement = createConfigElement(element);
         
         // process any children there may be
         processChildren(element, configElement);
      }
      
      return configElement;
   }

   /**
    * Recursively processes the children creating the required config element
    * objects as it goes
    * 
    * @param element
    * @param parentConfig
    */
   @SuppressWarnings("unchecked")
   protected void processChildren(Element element, GenericConfigElement parentConfig)
   {
      // get the list of children for the given element
      Iterator<Element> children = element.elementIterator();
      while (children.hasNext())
      {
         Element child = children.next();
         GenericConfigElement childConfigElement = createConfigElement(child);
         parentConfig.addChild(childConfigElement);
         
         // recurse down the children
         processChildren(child, childConfigElement);
      }
   }
   
   /**
    * Creates a ConfigElementImpl object from the given element.
    * 
    * @param element The element to parse
    * @return The GenericConfigElement representation of the given element
    */
   @SuppressWarnings("unchecked")
   protected GenericConfigElement createConfigElement(Element element)
   {
      // get the name and value of the given element
      String name = element.getName();
      
      // create the config element object and populate with value
      // and attributes
      GenericConfigElement configElement = new GenericConfigElement(name);
      if ((element.hasContent()) && (element.hasMixedContent() == false))
      {
         String value = element.getTextTrim();
         if (value != null && value.length() > 0)
         {
            if (propertyConfigurer != null)
            {
               value = propertyConfigurer.resolveValue(value);
            }
            configElement.setValue(value);
         }
      }
      
      Iterator<Attribute> attrs = element.attributeIterator();
      while (attrs.hasNext())
      {
         Attribute attr = attrs.next();
         String attrName = attr.getName();
         String attrValue = attr.getValue();
         
         if (propertyConfigurer != null)
         {
             attrValue = propertyConfigurer.resolveValue(attrValue);
         }

         configElement.addAttribute(attrName, attrValue);
      }
      
      return configElement;
   }
}
