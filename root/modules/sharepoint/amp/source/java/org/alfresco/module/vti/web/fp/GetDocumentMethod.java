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
import java.util.EnumSet;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.dic.GetOption;
import org.alfresco.module.vti.metadata.model.Document;
import org.alfresco.module.vti.web.VtiEncodingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

/**
 * Class for handling GetDocument Method
 * 
 * @author andreyak
 *
 */
public class GetDocumentMethod extends AbstractMethod
{
    private static Log logger = LogFactory.getLog(GetDocumentMethod.class);
            
    private static final String METHOD_NAME = "get document"; 
    
    /**
     * Method for document retreiving
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
        boolean force = request.getParameter("force", true);
        String docVersion = request.getParameter("doc_version", "");
        EnumSet<GetOption> getOptionSet = GetOption.getOptions(request.getParameter("get_option"));
        int timeout = request.getParameter("timeout", 10);

        serviceName = VtiPathHelper.removeSlashes(serviceName.replaceFirst(request.getAlfrescoContextName(), ""));
        Document document;
        try
        {
            document = vtiHandler.getDocument(serviceName, documentName, force, docVersion, getOptionSet, timeout);
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMethodException(e);
        }

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);

        response.addParameter("message=successfully retrieved document '" + VtiEncodingUtils.encode(documentName) + "' from '" + VtiEncodingUtils.encode(documentName) + "'");
        response.beginList("document");

        response.addParameter("document_name", VtiEncodingUtils.encode(document.getPath()));
        response.beginList("meta_info");
        processDocMetaInfo(document, request, response);
        response.endList();

        response.endList();

        response.endVtiAnswer();

        FileCopyUtils.copy(document.getInputStream(), response.getOutputStream());

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }


    /**
     * @see org.alfresco.module.vti.method.VtiMethod#getName()
     */
    public String getName()
    {
        return METHOD_NAME;
    }

}
