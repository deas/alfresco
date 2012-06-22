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

import org.alfresco.service.namespace.QName;


/**
 * <p>Bean class that store all meta-information about
 *  a SharePoint List Type (definition of a kind of List)</p>
 * 
 * @author Nick Burch
 */
public class ListTypeBean
{
   private final int id;
   private final int baseType;
   private final boolean isDataList;
   private final QName entryType;
   private final String name;
   private final String title;
   private final String description;
   
   public ListTypeBean(int id, int baseType, boolean isDataList, QName entryType, 
                       String name, String title, String description) 
   {
      this.id = id;
      this.baseType = baseType;
      this.isDataList = isDataList;
      this.entryType = entryType;
      this.name = name;
      this.title = title;
      this.description = description;
   }

   /**
    * Get the ID of the Type
    */
   public int getId() 
   {
      return id;
   }

   /**
    * Get the List Base Type 
    */
   public int getBaseType() 
   {
      return baseType;
   }
   
   /**
    * Is this a Data List (can have many) or
    *  a Site Component (limited to one)?
    */
   public boolean isDataList()
   {
      return isDataList;
   }

   /**
    * The Type of entries within the list
    */
   public QName getEntryType()
   {
       return entryType;
   }

   /**
    * Get the (short form) name
    */
   public String getName() 
   {
      return name;
   }

   /**
    * Get the Title (Display Name)
    */
   public String getTitle() 
   {
      return title;
   }

   /**
    * Get the Description
    */
   public String getDescription() 
   {
      return description;
   }
}
