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

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;


/**
 * Definition of an object responsible for converting the XML representation of a config
 * element into an in-memory object representation
 * 
 * @author gavinc
 */
public interface ConfigElementReader
{
   /**
    * Parses the given XML element into a ConfigElement object
    * 
    * @param element The XML element to parse
    * @return The object representation of the XML element
    */
   public ConfigElement parse(Element element);
}
