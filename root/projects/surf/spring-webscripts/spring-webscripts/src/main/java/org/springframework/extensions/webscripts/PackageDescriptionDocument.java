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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

/**
 * Package description document
 * 
 * @author drq
 */
public class PackageDescriptionDocument extends AbstractBaseDescriptionDocument 
{
    // required root element name
    public static final String ROOT_ELEMENT_NAME = "package";

    // element name for resources 
    public static final String RESOURCES_ELEMENT_NAME = "resources";

    // name post-fix for package description document 
    public static final String DESC_NAME_POSTFIX ="package-desc.xml";

    // name pattern for package description document 
    public static final String DESC_NAME_PATTERN =DESC_NAME_POSTFIX;

    // script package 
    private Path scriptPackage;

    // resource descriptions 
    private ArrayList<ResourceDescription> resourceDescriptionList;

    /**
     * Sets the Package  (path version of getScriptPath)
     * 
     * @param package
     */
    public void setPackage(Path scriptPackage)
    {
        this.scriptPackage = scriptPackage;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.PackageDescription#getPackage()
     */
    public Path getPackage()
    {
        return scriptPackage;
    }

    /**
     * @return the resourceDescriptionList
     */
    public ArrayList<ResourceDescription> getResourceDescriptionList()
    {
        return resourceDescriptionList;
    }

    /**
     * @param resourceDescriptionList the resourceDescriptionList to set
     */
    public void setResourceDescriptionList(
            ArrayList<ResourceDescription> resourceDescriptionList)
    {
        this.resourceDescriptionList = resourceDescriptionList;
    }

    /**
     * @return the resourceDescriptions
     */
    public ResourceDescription[] getResourceDescriptions() 
    {
        return resourceDescriptionList.toArray(new ResourceDescription[resourceDescriptionList.size()]);
    }

    /**
     * @param resourceDescriptions the resourceDescriptions to set
     */
    public void setResourceDescriptions(ResourceDescription[] resourceDescriptions) 
    {
        this.resourceDescriptionList = new ArrayList<ResourceDescription>();
        Collections.addAll(this.resourceDescriptionList, resourceDescriptions);
    }

    /**
     * @param resouceDescription
     */
    public void addResourceDescription(ResourceDescription resouceDescription)
    {
        this.resourceDescriptionList.add(resouceDescription);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.document.AbstractDescription#parse(org.dom4j.Element)
     */
    @SuppressWarnings("unchecked")
    public void parse(Element elem) 
    {        
        if (this.validateRootElement(elem, ROOT_ELEMENT_NAME)) 
        {
            super.parse(elem);
            resourceDescriptionList = new ArrayList<ResourceDescription>();
            Element resourcesElement = elem.element(RESOURCES_ELEMENT_NAME);
            if (resourcesElement != null) 
            {
                List<Element> resourceElements = resourcesElement.elements(ResourceDescription.ROOT_ELEMENT_NAME);
                Iterator<Element> iterResourceElements = resourceElements.iterator();
                while (iterResourceElements.hasNext()) 
                {
                    Element resourceElement = iterResourceElements.next();
                    ResourceDescription resourceDescription = new ResourceDescription();
                    resourceDescription.parse(resourceElement);
                    // Check if package path is missing in script id reference
                    String [] scriptIds = resourceDescription.getScriptIds();
                    String scriptPackagePath = this.getPackage().getPath();
                    if (scriptIds != null) 
                    {
                        for (int i = 0 ; i < scriptIds.length ; i++) 
                        {
                            String webscriptId = scriptIds[i];
                            if (!webscriptId.startsWith(scriptPackagePath)) 
                            {
                                //TODO: Still doesn't manage the method part.
                                scriptIds[i] = scriptPackagePath+"/"+webscriptId;
                            } 
                        }
                    }
                    resourceDescriptionList.add(resourceDescription);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(AbstractBaseDescriptionDocument.COMMON_XML_HEADER).append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append("<").append(PackageDescriptionDocument.ROOT_ELEMENT_NAME).append(" ").append(AbstractBaseDescriptionDocument.COMMON_XML_NS).append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append("<shortname>").append(this.getShortName()).append("</shortname>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append("<description>").append(this.getDescription()).append("</description>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append("<resources>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        if (this.getResourceDescriptions() != null)
        {
            for (ResourceDescription rd : this.getResourceDescriptions())
            {
                sb.append(rd.toString()).append(AbstractBaseDescriptionDocument.NEW_LINE);
            }
        }
        sb.append(AbstractBaseDescriptionDocument.TAB).append("</resources>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append("</").append(PackageDescriptionDocument.ROOT_ELEMENT_NAME).append(">");
        return sb.toString();    
    }

    /**
     * Return a new instance of PackageDecriptionDocument
     * 
     * @return a new instance of PackageDecriptionDocument
     */
    public static PackageDescriptionDocument newInstance()
    {
        PackageDescriptionDocument newPackageDescriptionDocument = new PackageDescriptionDocument ("","shortname","description");
        return newPackageDescriptionDocument;
    }

    /**
     * Constructor with id, shortName, description and definition
     * 
     * @param id
     * @param shortName
     * @param description
     * @param definition
     */
    public PackageDescriptionDocument(String id, String shortName, String description)
    {
        super(id,shortName,description);
        this.resourceDescriptionList = new  ArrayList<ResourceDescription>();
    }

    /**
     * Default constructor
     */
    public PackageDescriptionDocument()
    {
        super();
        this.resourceDescriptionList = new  ArrayList<ResourceDescription>();
    }
}
