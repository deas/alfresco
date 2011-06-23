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

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling CheckoutDocument Method
 * 
 * @author Dmitry Lazurkin
 *
 */
public class CheckoutDocumentMethod extends AbstractMethod
{

    private static Log logger = LogFactory.getLog(CheckoutDocumentMethod.class);
    
    public String getName()
    {
        return "checkout document";
    }

    /**
     * Enables the currently authenticated user to make changes to a document under source control
     * 
     * @param request Vti Frontpage request ({@link VtiFpRequest})
     * @param response Vti Frontpage response ({@link VtiFpResponse})     
     */
    protected void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMethodException, IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Start method execution. Method name: " + getName());
        }

        String serviceName = request.getParameter("service_name", "");
        String documentName = request.getParameter("document_name", "");
        int force = request.getParameter("force", 0);
        int timeout = request.getParameter("timeout", 10);
        boolean validateWelcomeNames = request.getParameter("validateWelcomeNames", false);

        serviceName = VtiPathHelper.removeSlashes(serviceName.replaceFirst(request.getAlfrescoContextName(), ""));

        DocMetaInfo docMetaInfo = null;

        try
        {
            docMetaInfo = vtiHandler.checkOutDocument(serviceName, documentName, force, timeout, validateWelcomeNames);
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMethodException(e);
        }

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.beginList("meta_info");

        processDocMetaInfo(docMetaInfo, request, response);

        response.endList();
        response.endVtiAnswer();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

}
