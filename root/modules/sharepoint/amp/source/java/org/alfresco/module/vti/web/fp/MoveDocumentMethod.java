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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.dic.PutOption;
import org.alfresco.module.vti.metadata.dic.RenameOption;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.web.VtiEncodingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling MoveDocument Method
 * 
 * @author AndreyAk
 */
public class MoveDocumentMethod extends AbstractMethod
{
    
    private static Log logger = LogFactory.getLog(MoveDocumentMethod.class);
    
    private static final String METHOD_NAME = "move document";
    
    /** 
     * Changes the name of the selected document to the new name provided by the user
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
        String oldUrl = request.getParameter("oldUrl", "");
        String newUrl = request.getParameter("newUrl", "");
        List<String> urlList = request.getParameter("url_list", new ArrayList<String>());
        EnumSet<RenameOption> renameOptionSet = RenameOption.getOptions(request.getParameter("rename_option"));
        EnumSet<PutOption> putOptionSet = PutOption.getOptions(request.getParameter("put_option"));
        boolean docopy = request.getParameter("docopy", false);
        boolean validateWelcomeNames = request.getParameter("validateWelcomeNames", false);

        serviceName = VtiPathHelper.removeSlashes(serviceName.replaceFirst(request.getAlfrescoContextName(), ""));
        DocsMetaInfo docs;
        try
        {
            docs = vtiHandler.moveDocument(serviceName, oldUrl, newUrl, urlList, renameOptionSet, putOptionSet, docopy, validateWelcomeNames);
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMethodException(e);
        }

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);

        response.addParameter("message=successfully renamed URL '" + VtiEncodingUtils.encode(oldUrl) + "' as '" + VtiEncodingUtils.encode(newUrl) + "'");
        response.addParameter("oldUrl", VtiEncodingUtils.encode(oldUrl));
        response.addParameter("newUrl", VtiEncodingUtils.encode(newUrl));
        response.beginList("document_list");
        response.endList();

        response.beginList("moved_docs");
        for (DocMetaInfo document : docs.getFileMetaInfoList())
        {
            response.beginList();
            response.addParameter("document_name", VtiEncodingUtils.encode(document.getPath()));
            response.beginList("meta_info");
            processDocMetaInfo(document, request, response);
            response.endList();
            response.endList();
        }
        response.endList();

        response.beginList("moved_dirs");
        for (DocMetaInfo url : docs.getFolderMetaInfoList())
        {
            response.beginList();
            response.addParameter("url", VtiEncodingUtils.encode(url.getPath()));
            response.beginList("meta_info");
            processDocMetaInfo(url, request, response);
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

    /**
     * @see org.alfresco.module.vti.method.VtiMethod#getName()
     */
    public String getName()
    {
        return METHOD_NAME;
    }

}
