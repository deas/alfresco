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
 * Webscript argument description
 * 
 * @author drq
 */
public class ArgumentTypeDescription extends AbstractBaseDescription 
{
    // argument default value
    private String defaultValue;

    // required or not
    private boolean required = true;

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() 
    {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) 
    {
        this.defaultValue = defaultValue;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.document.AbstractDescription#parse(org.dom4j.Element)
     */
    public void parse(Element elem) 
    {
        super.parse(elem);
        Element defaultElement = elem.element("default");
        if (defaultElement != null) 
        {
            this.setDefaultValue(defaultElement.getText());
        }		
    }

    /**
     * @return the required
     */
    public boolean isRequired() 
    {
        return required;
    }

    /**
     * @param required the required to set
     */
    public void setRequired(boolean required) 
    {
        this.required = required;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<").append("arg").append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<shortname>").append(this.getShortName()).append("</shortname>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<description>").append(this.getDescription()).append("</description>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        if (this.getDefaultValue()!= null)
        {
            sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<").append("default").append(">").append(this.getDefaultValue()).append("</").append("default").append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);            
        }
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("</").append("arg").append(">");
        return sb.toString();    
    }
    
    /**
     * Returns a new instance of ArgumentTypeDescription
     * 
     * @return a new instance of ArgumentTypeDescription
     */
    public static ArgumentTypeDescription newInstance()
    {
        ArgumentTypeDescription newArgumentTypeDescription = new ArgumentTypeDescription ("shortname","description","default");
        return newArgumentTypeDescription;
    }

    /**
     * Utility function for comparing this ArgumentTypeDescription with another ArgumentTypeDescription instance.
     * 
     * @param newArgumentTypeDescription ArgumentTypeDescription instance to be compared to
     * @return true if they are different
     */
    public boolean compare(ArgumentTypeDescription newArgumentTypeDescription)
    {
        if (compareField(newArgumentTypeDescription.getShortName(),this.getShortName())
                &&compareField(newArgumentTypeDescription.getDescription(),this.getDescription())
                &&compareField(newArgumentTypeDescription.getDefaultValue(),this.getDefaultValue()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * Constructor with shortName, description and defaultValue
     * 
     * @param shortName
     * @param description
     * @param defaultValue
     */
    public ArgumentTypeDescription(String shortName, String description, String defaultValue)
    {
        super("",shortName,description);
        this.defaultValue = defaultValue;
    }

    /**
     * Default constructor
     */
    public ArgumentTypeDescription()
    {
        super();
    }
}
