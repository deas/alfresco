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
 * <p>Enum of standard errors that may be returned while working with DwsService methods.</p>
 * 
 * @author PavelYur
 */
public enum DwsError
{
    /**
     * The user does not have sufficient rights
     */
    NO_ACCESS (3),               
    
    /**
     * Another user modified the specified item
     */
    CONFLICT (4),                
    
    /**
     * Could not find the specified item
     */
    ITEM_NOT_FOUND (5),          
    
    /**
     *  The specified list does not exist
     */
    LIST_NOT_FOUND (7),          
    
    /**
     * The specified list contains more than 99 items
     */
    TOO_MANY_ITEMS (8),          
    
    /**
     *  The parent folder does not exist
     */
    FOLDER_NOT_FOUND (10),       
    
    /**
     * The document workspace contains subsites
     */
    WEB_CONTAINS_SUBWEB (11),    
    
    /**
     * The specified URL already exists
     */
    ALREADY_EXISTS (13),         
    
    /**
     * This operation exceeds the user's quota
     */
    QUOTA_EXCEEDED (14);             
    
    private final int value;
    
    DwsError(int value) 
     {
         this.value = value;
     }
     
     public int toInt()
     {
         return value;
     }
}
