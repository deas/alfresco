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
 * type description
 * 
 * @author drq
 */
public class TypeDescription extends AbstractBaseDescription 
{

    // required root element name
    public static final String ROOT_ELEMENT_NAME = "type";

    // element name for format 
    public static final String FORMAT_ELEMENT_NAME = "format";

    // element name for definition 
    public static final String DEFINITION_ELEMENT_NAME = "definition";

    // element name for url 
    public static final String DEFINITION_ELEMENT_URL = "url";

    // format 
    public String format;

    // type definition (sample or schema definition)
    public String definition;
    
    // public url for schema definition
    public String url;

    /**
     * @return the format
     */
    public String getFormat() 
    {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) 
    {
        this.format = format;
    }

    /**
     * @return the definition
     */
    public String getDefinition() 
    {
        return definition;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(String definition) 
    {
        this.definition = definition;
    }

    /**
     * @return the url
     */
    public String getUrl() 
    {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) 
    {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.document.AbstractDescription#parse(org.dom4j.Element)
     */
    public void parse (Element elem) 
    {
        if (this.validateRootElement(elem, ROOT_ELEMENT_NAME)) 
        {
            super.parse(elem);

            Element formatElement = elem.element(FORMAT_ELEMENT_NAME);
            if (formatElement!=null) 
            {
                this.setFormat(formatElement.getTextTrim());
            }

            Element definitionElement = elem.element(DEFINITION_ELEMENT_NAME);
            if (definitionElement != null) 
            {
                this.setDefinition(definitionElement.getText());
            }
            
            Element urlElement = elem.element(DEFINITION_ELEMENT_URL);
            if (urlElement != null) 
            {
                this.setUrl(urlElement.getText());
            }
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<").append(TypeDescription.ROOT_ELEMENT_NAME).append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);
        if (this.getId()!=null && !this.getId().equals(""))
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<id>").append(this.getId()).append("</id>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        }
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<shortname>").append(this.getShortName()).append("</shortname>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<description>").append(this.getDescription()).append("</description>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<").append(FORMAT_ELEMENT_NAME).append(">").append(this.getFormat()).append("</").append(FORMAT_ELEMENT_NAME).append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);
        if (this.getDefinition() != null)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<").append(DEFINITION_ELEMENT_NAME).append("><![CDATA[").append(this.getDefinition()).append("]]></").append(DEFINITION_ELEMENT_NAME).append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);
          
        }
        if (this.getUrl() != null)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<").append(DEFINITION_ELEMENT_URL).append(">").append(this.getUrl()).append("</").append(DEFINITION_ELEMENT_URL).append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);            
        }
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("</").append(TypeDescription.ROOT_ELEMENT_NAME).append(">");
        return sb.toString();    
    }
    
    /**
     * Return a new instance of TypeDescription
     * 
     * @return
     */
    public static TypeDescription newInstance()
    {
        TypeDescription newTypeDescription = new TypeDescription ("id","shortname","description","","");
        return newTypeDescription;
    }

    /**
     * Utility function for comparing this instance to another instance of TypeDescription
     * 
     * @param newTypeDescription instance to be compared to
     * @return true if they are different
     */
    public boolean compare(TypeDescription newTypeDescription)
    {
        if (compareField(newTypeDescription.getShortName(),this.getShortName())
                &&compareField(newTypeDescription.getId(),this.getId())
                &&compareField(newTypeDescription.getDescription(),this.getDescription())
                &&compareField(newTypeDescription.getFormat(),this.getFormat())
                &&compareField(newTypeDescription.getDefinition(),this.getDefinition()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * Constructor with id, shortName, description, format and definition
     * 
     * @param id
     * @param shortName
     * @param description
     * @param format
     * @param definition
     */
    public TypeDescription(String id, String shortName, String description, String format, String definition)
    {
        super(id,shortName,description);
        this.format = format;
        this.definition = definition;
    }

    /**
     * Default constructor
     */
    public TypeDescription()
    {
        super();
    }
}
