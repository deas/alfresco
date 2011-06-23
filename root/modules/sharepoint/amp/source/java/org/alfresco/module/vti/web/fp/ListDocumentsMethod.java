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
import java.util.List;
import java.util.Map;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.web.VtiEncodingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling ListDocuments Method
 *
 * @author andreyak
 */
public class ListDocumentsMethod extends AbstractMethod
{
    private static Log logger = LogFactory.getLog(ListDocumentsMethod.class);
            
    private static final String METHOD_NAME = "list documents";

    public ListDocumentsMethod()
    {
    }

    /**
     * Provides a list of the files, folders, and subsites complete with meta-information
     * for each file contained in the initialUrl parameter of the specified Web site.
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
        boolean listHiddenDocs = request.getParameter("listHiddenDocs", false);
        boolean listExplorerDocs = request.getParameter("listExplorerDocs", false);
        String platform = request.getParameter("platform", "");
        String initialURL = request.getParameter("initialUrl", "");
        boolean listRecurse = request.getParameter("listRecurse", false);
        boolean listLinkInfo = request.getParameter("listLinkInfo", false);
        boolean listFolders = request.getParameter("listFolders", true);
        boolean listFiles = request.getParameter("listFiles", true);
        boolean listIncludeParent = request.getParameter("listIncludeParent", true);
        boolean listDerived = request.getParameter("listDerived", false);
        boolean listBorders = request.getParameter("listBorders", false);
        boolean validateWelcomeNames = request.getParameter("validateWelcomeNames", false);
        Map<String, Object> folderList = request.getMetaDictionary("folderList");
        boolean listChildWebs = request.getParameter("listChildWebs", false);

        serviceName = VtiPathHelper.removeSlashes(serviceName.replaceFirst(request.getAlfrescoContextName(), ""));
        DocsMetaInfo documents;
        try
        {
            documents = vtiHandler.getListDocuments(serviceName, listHiddenDocs, listExplorerDocs, platform, initialURL, listRecurse, listLinkInfo, listFolders, listFiles,
                    listIncludeParent, listDerived, listBorders, validateWelcomeNames, folderList, listChildWebs);
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMethodException(e);
        }

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);

        response.beginList("document_list");
        List<DocMetaInfo> fileMetaInfoList = documents.getFileMetaInfoList();
        for (DocMetaInfo docMetaInfo : fileMetaInfoList)
        {
            response.beginList();
            response.addParameter("document_name", VtiEncodingUtils.encode(docMetaInfo.getPath().substring(serviceName.length() + 1)));
            response.beginList("meta_info");
            processDocMetaInfo(docMetaInfo, request, response);
            response.endList();
            response.endList();
        }
        response.endList();

        response.beginList("urldirs");
        List<DocMetaInfo> folderMetaInfoList = documents.getFolderMetaInfoList();
        for (DocMetaInfo docMetaInfo : folderMetaInfoList)
        {
            response.beginList();
            if (docMetaInfo.getPath().equalsIgnoreCase(serviceName))
            {
                response.addParameter("url", "");
            }
            else
            {
                if (serviceName.equals(""))
                {
                    response.addParameter("url", VtiEncodingUtils.encode(docMetaInfo.getPath()));
                }
                else
                {
                    response.addParameter("url", VtiEncodingUtils.encode(docMetaInfo.getPath().substring(serviceName.length() + 1)));
                }
            }
            response.beginList("meta_info");
            processDocMetaInfo(docMetaInfo, request, response);
            response.endList();
            response.endList();
        }
        response.endList();

        response.endVtiAnswer();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

    public String getName()
    {
        return METHOD_NAME;
    }

}
