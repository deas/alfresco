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
package org.alfresco.module.vti.metadata.dic;

/**
 * <p>Enum of the standard sorting fields.</p>
 * 
 * @author PavelYur
 */
public enum VtiSortField
{
    /**
     * sort by name
     */
    NAME ("BaseName"),                     

    /**
     * sort by type
     */
    TYPE ("DocIcon"),                      

    /**
     * sort by modifier
     */
    MODIFIEDBY ("Editor"),                 

    /**
     * sort by last modified date
     */
    MODIFIED ("Last_x0020_Modified"),      

    /**
     * sort by checked out username
     */
    CHECKEDOUTTO ("CheckedOutTitle");      

    private String value;
    
    VtiSortField(String value)
    {
        this.value = value;
        
    }    
    
    public String toString()
    {        
        return value;
    }
    
    public static VtiSortField value(String stringValue)
    {
        VtiSortField[] values = values();
        for (VtiSortField value:values)
        {
            if (stringValue.equals(value.value))
                return value;
        }
        return null;
    }
    
}
