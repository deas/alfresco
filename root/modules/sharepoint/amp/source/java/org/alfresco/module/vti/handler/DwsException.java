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

import org.alfresco.module.vti.metadata.dic.DwsError;
import org.alfresco.module.vti.web.ws.VtiSoapException;

/**
 * Exception for DWS Actions, based on the Enumeration
 *  of possible DWS Errors.
 *  
 * These map onto a different set of Error XML, neither
 *  SOAP Faults (which go via {@link VtiSoapException})
 *  nor via Error IDs (which go via {@link VtiHandlerException})
 * 
 * @author Nick Smith
 */
public class DwsException extends RuntimeException
{
    private static final long serialVersionUID = 1932184211L;
    
    /**
     * What Error this is, from the Enumeration List
     */
    private DwsError error = DwsError.FAILED;

    /**
     * Create exception with specified message
     * 
     * @param error the specified error
     */
    public DwsException(DwsError error)
    {
        super(error.toCode());
        this.error = error;
    }

    /**
     * Create exception with specified message
     * 
     * @param message the specified message
     */
    public DwsException(String message)
    {
        super(message);
    }

    /**
     * Create exception with specified message and throwable object
     * 
     * @param error the specified error
     * @param throwable
     */
    public DwsException(DwsError error, Throwable throwable)
    {
        super(error.toCode(), throwable);
        this.error = error;
    }

    /**
     * Return the underlying error, if known,
     *  or {@link DwsError#FAILED} if not
     */
    public DwsError getError()
    {
        return error;
    }
}
