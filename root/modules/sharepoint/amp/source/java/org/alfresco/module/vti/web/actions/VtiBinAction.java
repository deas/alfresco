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

package org.alfresco.module.vti.web.actions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.alfresco.module.vti.web.fp.NotImplementedVtiMethod;
import org.alfresco.module.vti.web.fp.VtiFpRequest;
import org.alfresco.module.vti.web.fp.VtiFpResponse;
import org.alfresco.module.vti.web.fp.VtiMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiBinAction is processor of Forntpage extension protocol. It provides 
* the back-end controller for dispatching among set of VtiMethods (Frontpage 
* extension protocol methods). It selects and invokes a realization of {@link VtiMethod}
* to perform the requested method of Frontpage extension protocol.</p>
*
* @author Michael Shavnev
*   
*/
public class VtiBinAction extends VtiBaseAction  {

    private static final long serialVersionUID = -4566432341846075170L;

    private static final String METHOD_DELIMETR = ":";
    
    private Map<String, VtiMethod> nameToVtiMethod = new HashMap<String, VtiMethod>();  
    
    private static Log logger = LogFactory.getLog(VtiBinAction.class);  

    
    /**
     * <p>Process Forntpage extension protocol GET request, dispatch among set of 
     * VtiMethods (Frontpage extension protocol methods), selects and invokes a
     * realization of {@link VtiMethod} to perform the requested method of 
     * Frontpage extension protocol.</p> 
     *
     * @param request Vti Frontpage request
     * @param response Vti Frontpage response
     */
    protected void doGet(VtiFpRequest request, VtiFpResponse response) throws ServletException, IOException
    {
        String vtiMethodName = request.getParameter("dialogview");
        if (vtiMethodName != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Request to VTI method '" + vtiMethodName + "'");

            String[] dialogViewName = request.getParameterValues("dialogview");
            if (dialogViewName != null && dialogViewName.length > 0)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Process '" + dialogViewName[0] + "'");

                processVtiMethod(dialogViewName[0] + "_dialog", request, response);
            }
        }
        else
        {
            vtiMethodName = request.getParameter("Cmd");
            if (vtiMethodName == null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Request to VTI method '" + vtiMethodName + "'");
                    logger.debug("Process '" + vtiMethodName + "'");
                }

                vtiMethodName = vtiMethodName + "_command";
                processVtiMethod(vtiMethodName, request, response);
            }
        }
    }

    /**
     * <p>Process Forntpage extension protocol POST request, dispatch among set of 
     * VtiMethods (Frontpage extension protocol methods), selects and invokes a
     * realization of {@link VtiMethod} to perform the requested method of 
     * Frontpage extension protocol.</p> 
     *
     * @param request Vti Frontpage request
     * @param response Vti Frontpage response
     */
    protected void doPost(VtiFpRequest request, VtiFpResponse response) throws ServletException, IOException
    {
        String vtiMethodAndVersion = request.getParameter("method");
        if (vtiMethodAndVersion != null)
        {
            String vtiMethodName = vtiMethodAndVersion.split(METHOD_DELIMETR)[0].replaceAll(" ", "_") + "_method";
            processVtiMethod(vtiMethodName, request, response);
        }
    }

    private void processVtiMethod(String vtiMethodName, VtiFpRequest request, VtiFpResponse response) throws IOException
    {
        VtiMethod vtiMethod = nameToVtiMethod.get(vtiMethodName);
        if (vtiMethod == null)
        {
            vtiMethod = new NotImplementedVtiMethod(vtiMethodName);
        }

        if (logger.isDebugEnabled())
            logger.debug("Executing vtiMethod: " + vtiMethod);

        vtiMethod.execute(request, response);
    }
    
    /**
     * <p>Vti methods mapping setter setter.</p>
     *
     * @param nameToVtiMethod describe mapping from name of Frontpage method to
     * its realization.    
     */
    public void setNameToVtiMethod(Map<String, VtiMethod> nameToVtiMethod)
    {
        this.nameToVtiMethod = nameToVtiMethod;
    }

}