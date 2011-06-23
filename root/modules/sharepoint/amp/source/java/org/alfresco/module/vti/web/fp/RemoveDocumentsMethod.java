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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.web.VtiEncodingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling "remove documents" method
 * 
 * @author PavelYur
 *
 */
public class RemoveDocumentsMethod extends AbstractMethod
{

    private static Log logger = LogFactory.getLog(RemoveDocumentsMethod.class);
            
    /**
     * Default constructor 
     */
    public RemoveDocumentsMethod()
    {        
    }

    /**
     *  Deletes the specified documents or folders from the Web site specified by the
     *  service_name parameter
     *  
     * @param request Vti Frontpage request ({@link VtiFpRequest})
     * @param response Vti Frontpage response ({@link VtiFpResponse})   
     */
    @Override
    protected void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMethodException, IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Start method execution. Method name: " + getName());
        }
        String serviceName = request.getParameter("service_name", "");
        List<String> urlList = request.getParameter("url_list", new LinkedList<String>());
        boolean validateWelcomeNames = request.getParameter("validateWelcomeNames", false);

        serviceName = VtiPathHelper.removeSlashes(serviceName.replaceFirst(request.getAlfrescoContextName(), ""));

        DocsMetaInfo list = null;
        try
        {
            list = vtiHandler.removeDocuments(serviceName, urlList, new LinkedList<Date>(), validateWelcomeNames);
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMethodException(e);
        }

        List<DocMetaInfo> files = list.getFileMetaInfoList();
        List<DocMetaInfo> folders = list.getFolderMetaInfoList();

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.addParameter("message", "successfully removed documents");

        response.beginList("removed_docs");
        for (DocMetaInfo docMetaInfo : files)
        {
            response.beginList();
            response.addParameter("document_name", VtiEncodingUtils.encode(docMetaInfo.getPath()));
            response.beginList("meta_info");
            processDocMetaInfo(docMetaInfo, request, response);
            response.endList();
            response.endList();
        }
        response.endList();

        response.beginList("removed_dirs");
        for (DocMetaInfo docMetaInfo : folders)
        {
            response.beginList();
            response.addParameter("url", VtiEncodingUtils.encode(docMetaInfo.getPath()));
            response.beginList("meta_info");
            processDocMetaInfo(docMetaInfo, request, response);
            response.endList();
            response.endList();
        }
        response.endList();
        response.beginList("failed_docs");
        response.endList();
        response.beginList("failed_dirs");
        response.endList();
        response.endVtiAnswer();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

    /**
     * returns methods name
     */
    public String getName()
    {
        return "remove documents";
    }

}
