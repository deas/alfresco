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
 * Resource description
 * 
 * @author drq
 */
public class ResourceDescription extends AbstractBaseDescription 
{
    // required root element name
    public static final String ROOT_ELEMENT_NAME = "resource";

    // element name for webscripts 
    public static final String WEBSCRIPTS_ELEMENT_NAME = "webscripts";

    // related webscript ids 
    private String[] scriptIds;

    /**
     * @return the scriptIds
     */
    public String getScriptIdsAsString() 
    {
        StringBuffer sb = new StringBuffer();
        for (String scriptId : scriptIds)
        {
            sb.append(scriptId);
            sb.append(",");
        }
        String str = sb.toString();
        if (str.endsWith(","))
        {
            return str.substring(0, str.length()-1);
        }
        else
        {
            return str;
        }
    }

    /**
     * @return the scriptIds
     */
    public String[] getScriptIds() 
    {
        return scriptIds;
    }

    /**
     * @param scriptIds the scriptIds to set
     */
    public void setScriptIds(String[] scriptIds) 
    {
        this.scriptIds = scriptIds;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.document.AbstractDescription#parse(org.dom4j.Element)
     */
    public void parse(Element elem) 
    {
        if (this.validateRootElement(elem, ROOT_ELEMENT_NAME)) 
        {
            super.parse(elem);
            Element webscriptElement = elem.element(WEBSCRIPTS_ELEMENT_NAME);
            if (webscriptElement!=null) 
            {
                String [] webscriptIds = webscriptElement.getTextTrim().split(",");
                this.setScriptIds(webscriptIds);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<").append(ResourceDescription.ROOT_ELEMENT_NAME).append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<shortname>").append(this.getShortName()).append("</shortname>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<description>").append(this.getDescription()).append("</description>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("<").append(WEBSCRIPTS_ELEMENT_NAME).append(">").append(this.getScriptIdsAsString()).append("</").append(WEBSCRIPTS_ELEMENT_NAME).append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append(AbstractBaseDescriptionDocument.TAB).append("</").append(ResourceDescription.ROOT_ELEMENT_NAME).append(">");
        return sb.toString();    
    }
    
    /**
     * Return a new instance of ResourceDescription
     * 
     * @return a new instance of ResourceDescription
     */
    public static ResourceDescription newInstance()
    {
        ResourceDescription newResourceDescription = new ResourceDescription ("shortname","description",new String[]{});
        return newResourceDescription;
    }

    /**
     * Utility for comparint this instance with another ResourceDsecription
     * 
     * @param newResourceDescription instance to be compared to
     * @return true if they are different
     */
    public boolean compare(ResourceDescription newResourceDescription)
    {
        if (compareField(newResourceDescription.getShortName(),this.getShortName())
                &&compareField(newResourceDescription.getDescription(),this.getDescription())
                &&compareField(newResourceDescription.getScriptIdsAsString(),this.getScriptIdsAsString()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
        
    /**
     * Constructor with shortname, description and scriptIds
     * 
     * @param shortName
     * @param description
     * @param scriptIds
     */
    public ResourceDescription(String shortName, String description, String[] scriptIds)
    {
        super("",shortName,description);
        this.scriptIds = scriptIds;
    }

    /**
     * Default constructor
     */
    public ResourceDescription()
    {
        super();
    }
}
