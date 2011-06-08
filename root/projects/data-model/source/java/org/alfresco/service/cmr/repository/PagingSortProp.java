/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.service.cmr.repository;

import org.alfresco.service.namespace.QName;

/**
 * TEMP
 * 
 * @deprecated for review (API is subject to change)
 */
public class PagingSortProp
{
    private QName sortProp;
    private boolean ascending;
    
    public PagingSortProp(QName sortProp, boolean ascending)
    {
        this.sortProp = sortProp;
        this.ascending = ascending;
    }
    
    public QName getSortProp()
    {
        return sortProp;
    }
    
    public boolean isAscending()
    {
        return ascending;
    }
}
