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
package org.alfresco.service.cmr.repository;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Store-related exception that keeps a handle to the store reference
 * 
 * @author Derek Hulley
 */
@AlfrescoPublicApi
public abstract class AbstractStoreException extends RuntimeException
{
    private static final long serialVersionUID = 1315634811903555316L;

    private StoreRef storeRef;
    
    public AbstractStoreException(StoreRef storeRef)
    {
        this(null, storeRef, null);
    }

    public AbstractStoreException(String msg, StoreRef storeRef)
    {
        this(msg, storeRef, null);
    }

    public AbstractStoreException(StoreRef storeRef, Throwable e)
    {
        this(null, storeRef, e);
    }

    public AbstractStoreException(String msg, StoreRef storeRef, Throwable e)
    {
        super(msg, e);
        this.storeRef = storeRef;
    }

    /**
     * @return Returns the offending store reference
     */
    public StoreRef getStoreRef()
    {
        return storeRef;
    }
}
