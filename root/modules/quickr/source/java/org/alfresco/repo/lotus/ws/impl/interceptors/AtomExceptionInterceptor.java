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

import javax.ws.rs.core.Response;

import org.alfresco.repo.lotus.rs.error.QuickrError;
import org.alfresco.repo.node.integrity.IntegrityException;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.error.Error;

public class AtomExceptionInterceptor implements MethodInterceptor
{
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        Response result = Response.ok().build();
        try
        {
            result = (Response) invocation.proceed();
        }
        catch (FileNotFoundException e)
        {
            result = createFaultResponse(QuickrError.ITEM_NOT_FOUND, e.getMessage());
        }
        catch (InvalidNodeRefException e)
        {
            result = createFaultResponse(QuickrError.ITEM_NOT_FOUND, e.getMessage());
        }
        catch (FileExistsException e)
        {

            result = createFaultResponse(QuickrError.ITEM_EXISTS, e.getMessage());
        }
        catch (IntegrityException e)
        {
            result = createFaultResponse(QuickrError.COSNTRAIN_TVIOLATION, e.getMessage());
        }
        catch (AccessDeniedException e)
        {
            result = createFaultResponse(QuickrError.ACCESS_DENIED, e.getMessage());
        }        
        catch (Exception e)
        {
            result = createFaultResponse(QuickrError.UNKNOWN, e.getMessage());
        }
        return result;
    }

    private Response createFaultResponse(QuickrError error, String message)
    {
        return Response.status(error.getResponseStatus()).entity(Error.create(new Abdera(), error.getErrorCode(), message)).build();
    }

}
