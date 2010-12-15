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
 * <p>Enum of the standard constraints for the parameters.</p>
 * 
 * @author Michael Shavnev
 */
public enum VtiConstraint
{
    /**
     * Read-only
     */
    R ("R"),  
    
    /**
     * Write
     */
    W ("W"),  
    
    /**
     * Ignore
     */
    X ("X");  
    
    private final String value;
    
    VtiConstraint(String value) 
    {
        this.value = value;
    }
    
    public String toString()
    {
        return value;
    }
}
