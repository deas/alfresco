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
package org.alfresco.module.vti.metadata.model;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Represents the Sharepoint schema with its meta-inforamtion.</p>
 * 
 * @author AndreyAk
 *
 */
public class SchemaBean implements Serializable
{

    private static final long serialVersionUID = -2075342655994340968L;
    
    private String name;
    private String url;
    private List<SchemaFieldBean> fields;
    
    
    /**
     * @param name
     * @param url
     * @param fields
     */
    public SchemaBean(String name, String url, List<SchemaFieldBean> fields)
    {
        super();
        this.name = name;
        this.url = url;
        this.fields = fields;
    }
    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
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
    /**
     * @return the fields
     */
    public List<SchemaFieldBean> getFields()
    {
        return fields;
    }
    /**
     * <p>Sets the fields that schema contains.</p>
     * 
     * @param fields the fields to set
     */
    public void setFields(List<SchemaFieldBean> fields)
    {
        this.fields = fields;
    }
        
}
