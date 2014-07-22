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
 * Schema description document
 * 
 * @author drq
 */
public class SchemaDescriptionDocument extends AbstractBaseDescriptionDocument 
{
    // required root element name
    public static final String ROOT_ELEMENT_NAME = "schema";

    // name pattern of schema description document
    public static final String DESC_NAME_POSTFIX ="schema-desc.xml";

    // path pattern of schema description document
    public static final String DESC_NAME_PATTERN ="*."+DESC_NAME_POSTFIX;

    // schema type descriptions 
    public ArrayList<TypeDescription> typeDescriptionList;

    /**
     * @return the typeDescriptionList
     */
    public ArrayList<TypeDescription> getTypeDescriptionList()
    {
        return typeDescriptionList;
    }

    /**
     * @param typeDescriptionList the typeDescriptionList to set
     */
    public void setTypeDescriptionList(
            ArrayList<TypeDescription> typeDescriptionList)
    {
        this.typeDescriptionList = typeDescriptionList;
    }

    /**
     * @return the schemaDescriptions
     */
    public TypeDescription[] getTypeDescriptions() 
    {
        return typeDescriptionList.toArray(new TypeDescription[typeDescriptionList.size()]);
    }

    /**
     * @param schemaDescriptions the schemaDescriptions to set
     */
    public void setTypeDescriptions(TypeDescription[] typeDescriptions) 
    {
        this.typeDescriptionList = new ArrayList<TypeDescription>();
        Collections.addAll(this.typeDescriptionList, typeDescriptions);
    }

    /**
     * @param typeDescription
     */
    public void addTypeDescription(TypeDescription typeDescription)
    {
        this.typeDescriptionList.add(typeDescription);
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
            TypeDescription[] typeDescriptions = null;
            Element typesElement = elem.element("types");
            if (typesElement != null) 
            {
                List<Element> typeElements = typesElement.elements("type");
                typeDescriptions = new TypeDescription[typeElements.size()];
                int iType = 0;
                Iterator<Element> iterTypeElements = typeElements.iterator();
                while (iterTypeElements.hasNext()) 
                {
                    Element typeElement = iterTypeElements.next();
                    TypeDescription typeDescription = new TypeDescription();
                    typeDescription.parse(typeElement);
                    String typeDescriptionId = typeDescription.getId();
                    if (typeDescriptionId != null && !typeDescriptionId.startsWith(this.getId())) 
                    {
                        typeDescription.setId(this.getId()+"."+typeDescription.getId());
                    }
                    typeDescriptions[iType++] = typeDescription;
                }
            }
            this.setTypeDescriptions(typeDescriptions);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(AbstractBaseDescriptionDocument.COMMON_XML_HEADER).append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append("<").append(SchemaDescriptionDocument.ROOT_ELEMENT_NAME).append(" ").append(AbstractBaseDescriptionDocument.COMMON_XML_NS).append(">").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append("<shortname>").append(this.getShortName()).append("</shortname>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append("<description>").append(this.getDescription()).append("</description>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append(AbstractBaseDescriptionDocument.TAB).append("<types>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        if (this.getTypeDescriptions() != null)
        {
            for (TypeDescription td : this.getTypeDescriptions())
            {
                sb.append(td.toString()).append(AbstractBaseDescriptionDocument.NEW_LINE);
            }
        }
        sb.append(AbstractBaseDescriptionDocument.TAB).append("</types>").append(AbstractBaseDescriptionDocument.NEW_LINE);
        sb.append("</").append(SchemaDescriptionDocument.ROOT_ELEMENT_NAME).append(">");
        return sb.toString();    
    }

    /**
     * Return a new instance of SchemaDescriptDocument
     * 
     * @return
     */
    public static SchemaDescriptionDocument newInstance()
    {
        SchemaDescriptionDocument newSchemaDescriptionDocument = new SchemaDescriptionDocument ("id","shortname","description");
        return newSchemaDescriptionDocument;
    }

    /**
     * Constructor with id, shortName and desription
     * 
     * @param id
     * @param shortName
     * @param description
     */
    public SchemaDescriptionDocument(String id, String shortName, String description)
    {
        super(id,shortName,description);
        this.typeDescriptionList = new ArrayList<TypeDescription>();
    }

    /**
     * Default constructor
     */
    public SchemaDescriptionDocument()
    {
        super();
        this.typeDescriptionList = new ArrayList<TypeDescription>();
    }
}
