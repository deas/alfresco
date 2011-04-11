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
package org.alfresco.repo.lotus.ws.impl.interceptors;

import org.alfresco.repo.lotus.ws.ClbError;
import org.alfresco.repo.lotus.ws.ClbErrorType;
import org.alfresco.repo.lotus.ws.ClbResponse;
import org.alfresco.repo.node.integrity.IntegrityException;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class SOAPExceptionInterceptor implements MethodInterceptor
{

    public Object invoke(MethodInvocation invocation) throws Throwable
    {

        ClbResponse result = (ClbResponse) invocation.getMethod().getReturnType().getDeclaredConstructor().newInstance();
        try
        {
            result = (ClbResponse) invocation.proceed();
        }
        catch (FileNotFoundException e)
        {
            result.setError(createError(ClbErrorType.ITEM_NOT_FOUND, e.getMessage()));
        }
        catch (InvalidNodeRefException e)
        {
            result.setError(createError(ClbErrorType.ITEM_NOT_FOUND, e.getMessage()));
        }
        catch (FileExistsException e)
        {
            result.setError(createError(ClbErrorType.ITEM_EXISTS, e.getMessage()));
        }
        catch (IntegrityException e)
        {
            result.setError(createError(ClbErrorType.CONSTRAINT_VIOLATION, e.getMessage()));
        }
        catch (AccessDeniedException e)
        {
            result.setError(createError(ClbErrorType.ACCESS_DENIED, e.getMessage()));
        }
        catch (Exception e)
        {
            result.setError(createError(ClbErrorType.GENERAL_INFORMATION, e.getMessage()));
        }
        return result;
    }

    private ClbError createError(ClbErrorType errorType, String message)
    {
        ClbError error = new ClbError();
        error.setMessage(message);
        error.setType(errorType);

        return error;
    }
}
