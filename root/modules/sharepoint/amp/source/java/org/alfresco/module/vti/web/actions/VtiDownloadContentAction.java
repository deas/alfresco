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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiDownloadContentAction is used for content downloading for
*  documents that are located in version history.</p>
*
* @author PavelYur
*
*/
public class VtiDownloadContentAction implements VtiAction
{
    private static final long serialVersionUID = -4317029858934814804L;

    private static Log logger = LogFactory.getLog(VtiDownloadContentAction.class);

    private FileFolderService fileFolderService;
    
    private NodeService nodeService;

    /**
     * <p>FileFolderService setter.</p>
     *
     * @param fileFolderService {@link FileFolderService}.    
     */
    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    /**
     * <p>NodeService setter.</p>
     *
     * @param nodeService {@link NodeService}.    
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * <p>Write content to response for documents that are located in version history.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        String uri = request.getRequestURI();

        if (logger.isDebugEnabled())
        {
            logger.debug("Retriving document version for uri: " + uri);
        }

        NodeRef nodeRef = getNodeRefFromUri(uri);

        if (nodeRef == null)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = ((ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT)).getMimetype();
        
        ContentReader reader = fileFolderService.getReader(nodeRef);

        if (reader == null)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try
        {
            response.setContentType(contentType);
            reader.getContent(response.getOutputStream());
        }
        catch (ContentIOException e)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        catch (IOException e)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

    }

    private NodeRef getNodeRefFromUri(String uri)
    {
        NodeRef result = null;
        StoreRef storeRef = null;
        String uuid = null;

        uri = uri.substring(uri.indexOf("/_vti_history/") + "/_vti_history/".length());

        String[] parts = uri.split("/");
        try
        {
            storeRef = new StoreRef(parts[0].substring(0, parts[0].length() - 1), parts[1]);
        }
        catch (Exception e)
        {
            return result;
        }

        uuid = parts[2];

        try
        {
            result = new NodeRef(storeRef, uuid);
        }
        catch (Exception e)
        {
            return result;
        }
        return result;
    }
}
