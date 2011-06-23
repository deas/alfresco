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

import org.alfresco.module.vti.metadata.dic.VtiError;

/**
 * Exception for SOAP services and VTI methods
 * 
 * @author EugeneZh
 */
public class VtiHandlerException extends RuntimeException
{
    public static final VtiError NOT_FOUND = VtiError.NOT_FOUND;
    public static final VtiError ITEM_NOT_FOUND = VtiError.ITEM_NOT_FOUND;
    public static final VtiError LIST_NOT_FOUND = VtiError.LIST_NOT_FOUND;
    public static final VtiError NO_PERMISSIONS = VtiError.NO_PERMISSIONS;
    public static final VtiError ALREADY_EXISTS = VtiError.ALREADY_EXISTS;
    public static final VtiError DOES_NOT_EXIST = VtiError.DOES_NOT_EXIST;
    public static final VtiError URL_NOT_FOUND = VtiError.V_URL_NOT_FOUND;
    public static final VtiError OWSSVR_ERRORACCESSDENIED = VtiError.V_OWSSVR_ERRORACCESSDENIED;
    public static final VtiError BAD_URL = VtiError.V_BAD_URL;
    public static final VtiError UNDEFINED = VtiError.V_UNDEFINED;
    public static final VtiError DOC_NOT_CHECKED_OUT = VtiError.V_DOC_NOT_CHECKED_OUT;
    public static final VtiError DOC_CHECKED_OUT = VtiError.V_DOC_CHECKED_OUT;
    public static final VtiError PRIMARY_PARENT_NOT_EXIST = VtiError.PRIMARY_PARENT_NOT_EXIST;
    public static final VtiError FOLDER_ALREADY_EXISTS = VtiError.FOLDER_ALREADY_EXISTS;
    public static final VtiError FILE_ALREADY_EXISTS = VtiError.FILE_ALREADY_EXISTS;
    public static final VtiError REMOVE_DIRECTORY = VtiError.V_REMOVE_DIRECTORY;
    public static final VtiError FILE_OPEN_FOR_WRITE = VtiError.V_FILE_OPEN_FOR_WRITE;
    public static final VtiError REMOVE_FILE = VtiError.V_REMOVE_FILE;
    public static final VtiError URL_DIR_NOT_FOUND = VtiError.V_URL_DIR_NOT_FOUND;
    public static final VtiError HAS_ILLEGAL_CHARACTERS = VtiError.V_HAS_ILLEGAL_CHARACTERS;

    private static final long serialVersionUID = 1L;
    
    /**
     * What SharePoint error code to return
     */
    private int errorCode = -1;
    
    /**
     * What Error this is
     */
    private VtiError error = VtiError.V_UNDEFINED;

    /**
     * Create exception with specified message
     * 
     * @param error the specified error
     */
    public VtiHandlerException(VtiError error)
    {
        super(error.getMessage());
        this.errorCode = error.getErrorCode();
        this.error = error;
    }

    /**
     * Create exception with specified message
     * 
     * @param message the specified message
     */
    public VtiHandlerException(String message)
    {
        super(message);
    }

    /**
     * Create exception with specified message ant throwable object
     * 
     * @param error the specified error
     * @param throwable
     */
    public VtiHandlerException(VtiError error, Throwable throwable)
    {
        super(error.getMessage(), throwable);
        this.errorCode = error.getErrorCode();
        this.error = error;
    }

    /**
     * Create exception with specified message ant throwable object
     * 
     * @param message the specified message
     * @param throwable
     */
    public VtiHandlerException(String message, Throwable throwable)
    {
        super(message, throwable);
    }

    /**
     * Create exception with specified throwable object
     * 
     * @param throwable
     */
    public VtiHandlerException(Throwable throwable)
    {
        super(throwable);
    }
    
    /**
     * Return the error code, or -1 if not known
     */
    public int getErrorCode()
    {
        return errorCode;
    }
    
    /**
     * Return the underlying error, if known,
     *  or {@link VtiError#V_UNDEFINED} if not
     */
    public VtiError getError()
    {
        return error;
    }
}
