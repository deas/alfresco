/*
* Copyright (C) 2005-2013 Alfresco Software Limited.
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

import java.io.Serializable;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.QName;
import org.dom4j.io.OutputFormat;

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
     * @see org.alfresco.repo.webdav.WebDAVMethod#getNodeForPath(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
     */    
    @Override
    protected FileInfo getNodeForPath(NodeRef rootNodeRef, String path) throws FileNotFoundException
    {
        FileInfo nodeInfo = super.getNodeForPath(rootNodeRef, path);
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
    public String getPath()
    {
       return AbstractMethod.getPathWithoutContext(alfrescoContext, m_request);
    }

    /**
     * @see org.alfresco.repo.webdav.WebDAVMethod#shouldFlushXMLWriter()
     */
    @Override
    protected boolean shouldFlushXMLWriter()
    {
        // Do not flush, related to specific Office behaviour
    	return false;
    }

    /**
     * @see org.alfresco.repo.webdav.WebDAVMethod#createXMLWriter()
     */
    @Override
    protected OutputFormat getXMLOutputFormat()
    {
        OutputFormat outputFormat = new OutputFormat();
        outputFormat.setNewLineAfterDeclaration(false);
        outputFormat.setNewlines(false);
        outputFormat.setIndent(false);
        return outputFormat;
    }

    @Override
    protected void attemptLock() throws WebDAVServerException, Exception
    {
        try
        {
            FileInfo lockNodeInfo = null;
            try
            {
                // Check if the path exists
                lockNodeInfo = getNodeForPath(getRootNodeRef(), getPath());
            }
            catch (FileNotFoundException e)
            {
                // That will be handled in the super.executeImpl();
            }
            if (lockNodeInfo != null)
            {
                if (getNodeService().hasAspect(lockNodeInfo.getNodeRef(), ContentModel.ASPECT_VERSIONABLE) == false)
                {
                    getNodeService().addAspect(
                                lockNodeInfo.getNodeRef(),
                                ContentModel.ASPECT_VERSIONABLE,
                                Collections.<QName,Serializable>singletonMap(ContentModel.PROP_VERSION_TYPE, VersionType.MAJOR));
                }
            }
            
            super.attemptLock();
        }
        catch (AccessDeniedException e) 
        {
            // Office 2008/2011 for Mac special error handling
            // returning 403 status will cause client to show user friendly message
            m_response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}
