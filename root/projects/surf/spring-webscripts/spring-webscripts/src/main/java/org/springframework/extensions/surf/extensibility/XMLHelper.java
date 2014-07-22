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
package org.springframework.extensions.surf.extensibility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

public class XMLHelper
{
    private static final Log logger = LogFactory.getLog(XMLHelper.class);
    
    public static String getStringData(String elementName, Element element, boolean required)
    {
        String str =  null;
        Element el = element.element(elementName);
        if (el != null)
        {
            str = el.getTextTrim();
        }
        else if (required)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("The required element <" + elementName + "> was not found in element <" + element.getName() + ">");
            }
            // TODO: Throw exception here?
        }
        return str;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, String> getProperties(String elementName, Element sourceElement)
    {
        Map<String, String> props = new HashMap<String, String>();
        Element el = sourceElement.element(elementName);
        if (el != null)
        {
            List<Element> elementList = el.elements();
            for (Element element: elementList)
            {
                props.put(element.getName(), element.getTextTrim());
            }
        }        
        return props;
    }
    
    public static boolean getBooleanAttribute(String attributeName, Element element, boolean defaultValue)
    {
        boolean value = defaultValue;
        if (element != null)
        {
            String booleanStr = element.attributeValue(attributeName);
            if (booleanStr != null)
            {
                // Although the parseBoolean method copes with null we need to ensure we return the
                // default value if the attribute is NOT defined...
                value = Boolean.parseBoolean(booleanStr);
            }
        }
        
        return value;
    }
}
