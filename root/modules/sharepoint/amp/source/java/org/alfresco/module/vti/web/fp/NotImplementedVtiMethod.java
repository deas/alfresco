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

import java.io.IOException;

import org.alfresco.module.vti.metadata.dic.VtiError;

public class NotImplementedVtiMethod extends AbstractMethod
{
    private String vtiMethodName;
    
    /** 
     * Method that will be called if client sends request to undefined method 
     * 
     * @param request Vti Frontpage request ({@link VtiFpRequest})
     * @param response Vti Frontpage response ({@link VtiFpResponse})
     */
    protected void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMethodException, IOException
    {
        throw new VtiMethodException(VtiError.V_OWSSVR_ERRORSERVERINCAPABLE);
    }
    
    public NotImplementedVtiMethod(String vtiMethodName)
    {
        this.vtiMethodName = vtiMethodName;
    }

    public String getName()
    {
        return vtiMethodName;
    }
    
}
