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

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.extensions.surf.util.URLDecoder;

/**
 * Implements the WebDAV LOCK method with VTI specific
 * 
 * @author DmitryVas
 */
public class LockMethod extends org.alfresco.repo.webdav.LockMethod
{
    private String alfrescoContext;

    public LockMethod(String alfrescoContext)
    {
        this.alfrescoContext = alfrescoContext;
    }

    /** 
     * @see org.alfresco.repo.webdav.WebDAVMethod#getNodeForPath(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, java.lang.String)
     */    
    @Override
    protected FileInfo getNodeForPath(NodeRef rootNodeRef, String path, String servletPath) throws FileNotFoundException
    {
        FileInfo nodeInfo = super.getNodeForPath(rootNodeRef, URLDecoder.decode(path), alfrescoContext);
        FileInfo workingCopy = getWorkingCopy(nodeInfo.getNodeRef());
        return workingCopy != null ? workingCopy : nodeInfo;
    }

    /**
     * @see org.alfresco.repo.webdav.LockMethod#createNode(org.alfresco.service.cmr.repository.NodeRef, java.lang.String, org.alfresco.service.namespace.QName)
     */
    @Override
    protected FileInfo createNode(NodeRef parentNodeRef, String name, QName typeQName)
    {
        FileInfo lockNodeInfo = super.createNode(parentNodeRef, name, ContentModel.TYPE_CONTENT);

        ContentWriter writer = getFileFolderService().getWriter(lockNodeInfo.getNodeRef());
        writer.putContent("");

        if (getNodeService().hasAspect(lockNodeInfo.getNodeRef(), ContentModel.ASPECT_VERSIONABLE) == false)
        {
            getNodeService().addAspect(lockNodeInfo.getNodeRef(), ContentModel.ASPECT_VERSIONABLE, null);
        }

        if (getNodeService().hasAspect(lockNodeInfo.getNodeRef(), ContentModel.ASPECT_AUTHOR) == false)
        {
            getNodeService().addAspect(lockNodeInfo.getNodeRef(), ContentModel.ASPECT_AUTHOR, null);
        }
        getNodeService().setProperty(lockNodeInfo.getNodeRef(), ContentModel.PROP_AUTHOR, getAuthenticationService().getCurrentUserName());

        return lockNodeInfo;
    }
    
    /**
     * Returns the path, excluding the Servlet Context (if present)
     * @see org.alfresco.repo.webdav.WebDAVMethod#getPath()
     */
    @Override
    protected String getPath()
    {
        String path = URLDecoder.decode(m_request.getRequestURI());

        // remove the servlet path from the path
        if (alfrescoContext != null && alfrescoContext.length() > 0 && path.startsWith(alfrescoContext))
        {
            // Strip the servlet path from the relative path
            path = path.substring(alfrescoContext.length());
        }

        return path;
    }

    /**
     * @see org.alfresco.repo.webdav.WebDAVMethod#flushXML(org.dom4j.io.XMLWriter)
     */
    @Override
    protected void flushXML(XMLWriter xml) throws IOException
    {
        // Do nothing, related to specific Office behaviour
    }

    /**
     * @see org.alfresco.repo.webdav.WebDAVMethod#createXMLWriter()
     */
    @Override
    protected XMLWriter createXMLWriter() throws IOException
    {
        OutputFormat outputFormat = new OutputFormat();
        outputFormat.setNewLineAfterDeclaration(false);
        outputFormat.setNewlines(false);
        outputFormat.setIndent(false);
        return new XMLWriter(m_response.getWriter(), outputFormat);
    }

    @Override
    protected void executeImpl() throws WebDAVServerException, Exception
    {
        try
        {
            super.executeImpl();
        }
        catch (AccessDeniedException e) 
        {
            // Office 2008/2011 for Mac special error handling
            // returning 403 status will cause client to show user friendly message
            m_response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}
