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

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.dic.VtiError;

/**
 * Exception that describe specific error code of Frontpage extension protocol.
 * It is created using {@link VtiHandlerException} and transform VtiHandlerException message
 * to appropriate error code of Frontpage extension protocol     
 * 
 * @author Dmitry Lazurkin
 *
 */
public class VtiMethodException extends RuntimeException
{
    private static final long serialVersionUID = 6560087866101304630L;

    private VtiError error;
    private int errorCode;

    public VtiMethodException(VtiError errorCode, Throwable e)
    {
        super(errorCode.getMessage(), e);
        this.errorCode = errorCode.getErrorCode();
        this.error = errorCode;
    }

    public VtiMethodException(VtiError errorCode)
    {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getErrorCode();
        this.error = errorCode;
    }

    public VtiMethodException(String message, Throwable e)
    {
        super(message, e);
        this.errorCode = -1;
        this.error = VtiError.V_UNDEFINED;
    }

    public VtiMethodException(String message, int code)
    {
        super(message);
        this.errorCode = code;
        this.error = VtiError.V_UNDEFINED;
    }
    
    public VtiMethodException(VtiHandlerException e)
    {
        super(e.getMessage(), e.getCause());
        this.errorCode = e.getErrorCode();
        this.error = e.getError();
    }

    public VtiError getError()
    {
        return error;
    }
    
    public int getErrorCode()
    {
        return errorCode;
    }
}
