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

/**
 * @author PavelYur
 *
 */
public class MwsTemplate
{

    private String name;
    
    private String title;
    
    private int id;
    
    private String description;
    
    private String imageUrl;
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }
    
    public String getImageUrl()
    {
        return imageUrl;
    }
    
    public static MwsTemplate getDefault()    
    {
        MwsTemplate result = new MwsTemplate();
        
        result.setName("MPS#0");
        result.setTitle("Alfresco Meeting Workspace");
        result.setId(2);
        result.setDescription("Standard Alfresco Meeting Workspace. This Meeting Workspace contains the following:  Calendar, Members.");
        result.setImageUrl("");
        
        return result;
    }
}