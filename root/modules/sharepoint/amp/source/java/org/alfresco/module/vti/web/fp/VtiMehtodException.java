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

package org.alfresco.module.vti.web.fp;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.dic.VtiError;

/**
 * Exception that describe specific error code of Forntpage extension protocol.
 * It is created using {@link VtiHandlerException} and transform VtiHandlerException message
 * to appropriate error code of Forntpage extension protocol     
 * 
 * @author Dmitry Lazurkin
 *
 */
public class VtiMehtodException extends RuntimeException
{
    private static final long serialVersionUID = 6560087866101304630L;

    private static final Map<String, VtiError> exceptionMapping = new HashMap<String, VtiError>();

    private int errorCode;

    static
    {
        exceptionMapping.put(VtiHandlerException.URL_NOT_FOUND, VtiError.V_URL_NOT_FOUND);
        exceptionMapping.put(VtiHandlerException.OWSSVR_ERRORACCESSDENIED, VtiError.V_OWSSVR_ERRORACCESSDENIED);
        exceptionMapping.put(VtiHandlerException.BAD_URL, VtiError.V_BAD_URL);
        exceptionMapping.put(VtiHandlerException.UNDEFINED, VtiError.V_UNDIFUNED);
        exceptionMapping.put(VtiHandlerException.DOC_NOT_CHECKED_OUT, VtiError.V_DOC_NOT_CHECKED_OUT);
        exceptionMapping.put(VtiHandlerException.DOC_CHECKED_OUT, VtiError.V_DOC_CHECKED_OUT);
        exceptionMapping.put(VtiHandlerException.PRIMARY_PARENT_NOT_EXIST, VtiError.PRIMARY_PARENT_NOT_EXIST);
        exceptionMapping.put(VtiHandlerException.FOLDER_ALREADY_EXISTS, VtiError.FOLDER_ALREADY_EXISTS);
        exceptionMapping.put(VtiHandlerException.FILE_ALREADY_EXISTS, VtiError.FILE_ALREADY_EXISTS);
        exceptionMapping.put(VtiHandlerException.REMOVE_DIRECTORY, VtiError.V_REMOVE_DIRECTORY);
        exceptionMapping.put(VtiHandlerException.FILE_OPEN_FOR_WRITE, VtiError.V_FILE_OPEN_FOR_WRITE);
        exceptionMapping.put(VtiHandlerException.REMOVE_FILE, VtiError.V_REMOVE_FILE);
        exceptionMapping.put(VtiHandlerException.URL_DIR_NOT_FOUND, VtiError.V_URL_DIR_NOT_FOUND);
        exceptionMapping.put(VtiHandlerException.HAS_ILLEGAL_CHARACTERS, VtiError.V_HAS_ILLEGAL_CHARACTERS);
    }

    public VtiMehtodException(VtiError errorCode, Throwable e)
    {
        super(e);
        this.errorCode = errorCode.getErrorCode();
    }

    public VtiMehtodException(VtiError errorCode)
    {
        super(errorCode.getMessagePattern());
        this.errorCode = errorCode.getErrorCode();
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public VtiMehtodException(VtiHandlerException e)
    {
        this(exceptionMapping.get(e.getMessage()));
    }
}
