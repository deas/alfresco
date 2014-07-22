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

import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Abstract class for implementation of AbstractBaseDescriptionDocument interface
 * 
 * @author drq
 */
public abstract class AbstractBaseDescriptionDocument extends AbstractBaseDescription 
    implements BaseDescriptionDocument 
{
    public static final String COMMON_XML_HEADER ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static final String COMMON_XML_NS = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://www.springsurf.org/schemas/DescriptionXMLSchema.xsd\"";
    public static final String NEW_LINE = "\n";
    public static final String TAB = "    ";
    
    // web script store
    private Store store;

    // description document path
    private String descPath;

    /**
     * Sets the web description store
     * 
     * @param store  store
     */
    public void setStore(Store store)
    {
        this.store = store;
    }

    /**
     * Sets the desc path
     * 
     * @param descPath
     */
    public void setDescPath(String descPath)
    {
        this.descPath = descPath;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.DocumentDescription#getDescPath()
     */
    public String getDescPath()
    {
        return descPath;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.DocumentDescription#getDescDocument()
     */
    public InputStream getDescDocument()
        throws IOException
    {
        return store.getDocument(descPath);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.DocumentDescription#getStorePath()
     */
    public String getStorePath() 
    {
        return store.getBasePath();
    }

    /**
     * @return the store
     */
    public Store getStore()
    {
        return store;
    }
    
    /**
     * Parses input XML document
     * 
     * @param doc input XML document
     * @throws DocumentException
     */
    public void parseDocument(InputStream doc) throws DocumentException 
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read(doc);
        Element rootElement = document.getRootElement();
        parse(rootElement);
    }
    
    /**
     * Constructor with id, shortName and description
     * 
     * @param id
     * @param shortName
     * @param description
     */
    public AbstractBaseDescriptionDocument(String id, String shortName,
            String description)
    {
        super(id,shortName,description);
    }

    /**
     * Default constructor
     */
    public AbstractBaseDescriptionDocument()
    {
        super();
    }
}
