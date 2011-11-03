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
package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.metadata.dic.VtiError;

/**
 * Exception used for SOAP problems where a specific SOAP FAULT error 
 *  code should be returned, rather than using one of the Errors from
 *  the {@link VtiError} enumeration.  
 * 
 * @author EugeneZh
 */
public class VtiSoapException extends RuntimeException
{
    /**
     * What SharePoint error code to return
     */
    private long errorCode = -1;
    
    /**
     * Create exception with specified message
     * 
     * @param message the specified message
     */
    public VtiSoapException(String message, long errorCode)
    {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Create exception with specified message and throwable object
     * 
     * @param message the specified message
     * @param throwable
     */
    public VtiSoapException(String message, long code, Throwable throwable)
    {
        super(message, throwable);
        this.errorCode = code;
    }
    
    /**
     * Return the error code, or -1 if not known
     */
    public long getErrorCode()
    {
        return errorCode;
    }
}
