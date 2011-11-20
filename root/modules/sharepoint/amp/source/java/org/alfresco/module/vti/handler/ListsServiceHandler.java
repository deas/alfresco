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

package org.alfresco.module.vti.handler;

import java.util.List;

import org.alfresco.module.vti.metadata.model.ListBean;

/**
 * Interface for lists web service handler
 * 
 * TODO Merge with {@link ListServiceHandler}
 * 
 * @author PavelYur
 */
public interface ListsServiceHandler
{

    /**
     * Returns the names and GUIDs for all the lists in the site.
     * 
     * @param siteName the name of site 
     * @return the list of site' lists
     */
    public List<ListBean> getListCollection(String siteName);

    /**
     * Returns a schema for the specified list.
     * 
     * @param listName the list name 
     * @return the list information
     */
    public ListBean getList(String listName);
}
