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

import org.dom4j.Element;

/**
 * Abstract class for implementation of BaseDescription interface
 * 
 * @author drq
 */
public abstract class AbstractBaseDescription implements BaseDescription 
{
    private String id;
    private String shortName;
    private String description;

    /**
     * Sets the service id
     * 
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.DocumentDescription#getId()
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Sets the service short name
     * 
     * @param shortName
     */
    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    /**
     * Sets the service description
     * 
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.document.BaseDescription#getDescription()
     */
    public String getDescription() 
    {
        return this.description;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.document.BaseDescription#getShortName()
     */
    public String getShortName() 
    {
        return this.shortName;
    }

    /**
     * Validate XML root element which will be used for parsing
     * 
     * @param rootElement root element
     * @param name required root element name
     * @return true if the root element is valid and the name matches
     */
    public boolean validateRootElement (Element rootElement, String name) 
    {
        if (rootElement == null) 
        {
            throw new WebScriptException("Invalid XML root element.");
        }

        if (!rootElement.getName().equals(name))
        {
            throw new WebScriptException("Expected <"+name+"> root element - found <" + rootElement.getName() + ">");
        } 
        else 
        {
            return true;
        }
    }    
    
    /**
     * Populates fields from given XML element without validation
     * 
     * @param rootElement root element for parsing
     */
    public void parse(Element rootElement) 
    {
        Element idElement = rootElement.element("id");
        if (idElement != null) 
        {
            this.setId(idElement.getText());
        }

        Element nameElement = rootElement.element("shortname");
        if (nameElement != null) 
        {
            this.setShortName(nameElement.getText());
        }

        Element descElement = rootElement.element("description");
        if (descElement != null) 
        {
            this.setDescription(descElement.getText());
        }		
    }

    /**
     * Utility function for comparing two strings
     * 
     * @param text1
     * @param text2
     * @return true if text2 is same as text1
     */
    protected boolean compareField(String text1, String text2)
    {
        if (text1 == null)
        {
            if (text2 == null)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if (text2 == null)
            {
                return false;
            }
            else
            {
                return text2.equals(text1);
            }

        }
    }

    /**
     * Constructor with id, shortName and description
     * 
     * @param id
     * @param shortName
     * @param description
     */
    public AbstractBaseDescription(String id, String shortName,
            String description)
    {
        super();
        this.id = id;
        this.shortName = shortName;
        this.description = description;
    }

    /**
     * Default constructor
     */
    public AbstractBaseDescription()
    {
        super();
    }
    
}
