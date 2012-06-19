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

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * Exception thrown when a Vti call tries to find a given Object
 *  or Node, but it could not be found.
 * This is typically thrown from a Handler, and caught in an
 *  Endpoint. The Endpoint will then return the appropriate error code.
 * 
 * @author Nick Burch
 */
public class ObjectNotFoundException extends AlfrescoRuntimeException
{
    private static final long serialVersionUID = -2639705474062599344L;

    /**
     * Create exception with no message
     */
    public ObjectNotFoundException()
    {
        super("The specified object could not be found");
    }

    /**
     * Create exception with specified message
     * 
     * @param message the specified message
     */
    public ObjectNotFoundException(String message)
    {
        super(message);
    }
}
