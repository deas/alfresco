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
 * <p>Enum of the standard permissions that user may have in dws.</p> 
 * 
 * @author PavelYur
 */
public enum Permission
{
    
    /**
     * Add items to lists, add documents to document libraries.
     */
    INSERT_LIST_ITEMS ("InsertListItems"),    
    
    /**
     * Edit items in lists, edit documents in document libraries.
     */
    EDIT_LIST_ITEMS ("EditListItems"),        
    
    /**
     * Delete items from a list, documents from a document library.
     */
    DELETE_LIST_ITEMS ("DeleteListItems"),    
    
    /**
     * Manage a site, including the ability to perform all administration tasks for the site and manage contents and permissions
     */
    MANAGE_WEB ("ManageWeb"),                 
    
    /**
     * Create, change, and delete site groups, including adding users to the site groups and specifying which rights are assigned to a site group.
     */
    MANAGE_ROLES ("ManageRoles"),             
    
    /**
     * Manage or create subsites.
     */
    MANAGE_SUBWEBS ("ManageSubwebs"),         
    
    /**
     * Approve content in lists, add or remove columns in a list, and add or remove public views of a list.
     */
    MANAGE_LISTS  ("ManageLists");               
    
    private final String value;
    
    Permission(String value) 
     {
         this.value = value;
     }
     
     public String toString()
     {
         return value;
     }
}
