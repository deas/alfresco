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

/**
 * <p>Represent the list of the Sharepoint site.</p>
 * 
 * @author PavelYur
 */
public class ListBean implements Serializable
{

    private static final long serialVersionUID = -5107974012043435445L;

    private String id;
    private String title;
    private String name;
    private String description;
    
    public ListBean()
    {
    }
    
    public ListBean(String id, String title, String name, String description)
    {
        this.id = id;
        this.title = title;
        this.name = name;
        this.description = description;
    }
    
    /**  
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    
    /**
     * 
     * @return the id of the list
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * 
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    /**
     * 
     * @return the title of the list in the site
     */
    public String getTitle()
    {
        return title;
    }
    
    /**
     * 
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * 
     * @return the name of the list in the site
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * 
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * 
     * @return the description of the list in the site
     */
    public String getDescription()
    {
        return description;
    }
}
