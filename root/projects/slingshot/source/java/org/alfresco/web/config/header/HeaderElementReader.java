/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.config.header;

import org.alfresco.web.config.forms.DependenciesConfigElement;
import org.alfresco.web.config.forms.DependenciesElementReader;
import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * This class is a custom element reader to parse the config file for
 * &lt;header&gt; elements.
 * 
 * @author Mike Hatfield.
 * @see org.alfresco.web.config.forms.DependenciesElementReader
 */
public class HeaderElementReader implements ConfigElementReader
{
    public static final String ELEMENT_HEADER = "header";

    /**
     * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
     */
    public ConfigElement parse(Element headerElement)
    {
        HeaderConfigElement result = null;
        if (headerElement == null)
        {
            return null;
        }

        String name = headerElement.getName();
        if (!name.equals(ELEMENT_HEADER))
        {
            throw new ConfigException(this.getClass().getName()
                    + " can only parse " + ELEMENT_HEADER
                    + " elements, the element passed was '" + name + "'");
        }

        result = new HeaderConfigElement();

        // Go through each of the <app-items> tags under <header>
        for (Object obj : headerElement.selectNodes("./app-items"))
        {
            Element appItemsElement = (Element)obj;
            
            HeaderItemsElementReader appsReader = new HeaderItemsElementReader();
            HeaderItemsConfigElement appsCE = (HeaderItemsConfigElement)appsReader.parse(appItemsElement);

            result.setAppItems(appsCE);
        }

        // Go through each of the <user-items> tags under <header>
        for (Object obj : headerElement.selectNodes("./user-items")) {
            Element userItemsElement = (Element)obj;
            
            HeaderItemsElementReader userReader = new HeaderItemsElementReader();
            HeaderItemsConfigElement userCE = (HeaderItemsConfigElement)userReader.parse(userItemsElement);

            result.setUserItems(userCE);
        }
        
        // Go through each of the <dependencies> tags under <header>
        for (Object obj : headerElement.selectNodes("./dependencies")) {
            Element depsElement = (Element)obj;
            
            DependenciesElementReader depsReader = new DependenciesElementReader();
            DependenciesConfigElement depsCE = (DependenciesConfigElement)depsReader.parse(depsElement);

            result.setDependencies(depsCE);
        }
        
        return result;
    }
}
