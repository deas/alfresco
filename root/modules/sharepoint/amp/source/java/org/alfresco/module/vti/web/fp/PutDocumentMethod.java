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
import org.alfresco.module.vti.metadata.dic.PutOption;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.Document;
import org.alfresco.module.vti.web.VtiEncodingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling "put document" method
 * 
 * @author PavelYur
 *
 */
public class PutDocumentMethod extends AbstractMethod
{
    
    private static Log logger = LogFactory.getLog(PutDocumentMethod.class);

    /**
     * Default constructor
     */
    public PutDocumentMethod()
    {        
    }

    /**
     * Writes a single file to a directory in an existing Web site
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
        Document document = request.getParameter("document", new Document());
        EnumSet<PutOption> putOptionSet = PutOption.getOptions(request.getParameter("put_option"));
        String comment = request.getParameter("comment", "");
        boolean keepCheckedOut = request.getParameter("keep_checked_out", false);
        boolean validateWelcomeNames = request.getParameter("validateWelcomeNames", false);

        serviceName = VtiPathHelper.removeSlashes(serviceName.replaceFirst(request.getAlfrescoContextName(), ""));
        DocMetaInfo docMetaInfo = null;
        try
        {
            docMetaInfo = vtiHandler.putDocument(serviceName, document, putOptionSet, comment, keepCheckedOut, validateWelcomeNames);
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMethodException(e);
        }

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.addParameter("message", "successfully put document '" + VtiEncodingUtils.encode(docMetaInfo.getPath()) + "' as '" + VtiEncodingUtils.encode(docMetaInfo.getPath()) + "'");
        response.beginList("document");
        response.addParameter("document_name", VtiEncodingUtils.encode(docMetaInfo.getPath()));
        response.beginList("meta_info");
        processDocMetaInfo(docMetaInfo, request, response);
        response.endList();
        response.endList();
        response.endVtiAnswer();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

    /**
     * @return methods name
     */
    public String getName()
    {
        return "put document";
    }

}