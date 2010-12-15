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
 * <p>Represents the Sharepoint schema filed with its meta-information.</p>
 * 
 * @author AndreyAk
 *
 */
public class SchemaFieldBean implements Serializable
{

    private static final long serialVersionUID = 1387190605579465013L;
    
    private String name;
    private String type;
    private boolean required;
    private List<String> choices;
    
    /**
     * @param name
     * @param type
     * @param required
     * @param choices
     */
    public SchemaFieldBean(String name, String type, boolean required, List<String> choices)
    {
        super();
        this.name = name;
        this.type = type;
        this.required = required;
        this.choices = choices;
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
     * @return the type
     */
    public String getType()
    {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
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
    /**
     * @return the choices
     */
    public List<String> getChoices()
    {
        return choices;
    }
    /**
     * @param choices the choices to set
     */
    public void setChoices(List<String> choices)
    {
        this.choices = choices;
    }
        
}
