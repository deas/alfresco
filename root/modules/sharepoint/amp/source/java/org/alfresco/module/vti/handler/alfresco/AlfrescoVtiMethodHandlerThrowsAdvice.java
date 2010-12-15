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

package org.alfresco.module.vti.handler.alfresco;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.springframework.aop.ThrowsAdvice;

/**
 * Adviser that should wrap all realizations of VtiHandlers. It transform 
 * all obtained exceptions to VtiHandlerException.    
 * 
 * @author Dmitry Lazurkin
 */
public class AlfrescoVtiMethodHandlerThrowsAdvice implements ThrowsAdvice
{
    
    /**
     * Method that called after exception occurs
     * 
     * @param throwable source exception 
     */
    public void afterThrowing(Throwable throwable)
    {
        if (throwable instanceof VtiHandlerException)
        {
            throw (VtiHandlerException) throwable;
        }

        if (throwable instanceof AccessDeniedException)
        {
            throw new VtiHandlerException(VtiHandlerException.OWSSVR_ERRORACCESSDENIED, throwable);
        }

        throw new VtiHandlerException(VtiHandlerException.UNDEFINED, throwable);
    }

}
