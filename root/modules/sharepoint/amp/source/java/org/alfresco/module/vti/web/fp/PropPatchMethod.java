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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.dom4j.io.XMLWriter;
import org.springframework.extensions.surf.util.URLDecoder;

/**
 * Implements the WebDAV PROPPATCH method with VTI specific
 * 
 * @author DmitryVas
 */
public class PropPatchMethod extends org.alfresco.repo.webdav.PropPatchMethod
{
    private String alfrescoContext;

    public PropPatchMethod(String alfrescoContext)
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
     * @see org.alfresco.repo.webdav.WebDAVMethod#getURLForPath(javax.servlet.http.HttpServletRequest, java.lang.String, boolean)
     */
    @Override
    protected String getURLForPath(HttpServletRequest request, String path, boolean isFolder)
    {
        return WebDAV.getURLForPath(new HttpServletRequestWrapper(m_request)
        {
            public String getServletPath()
            {
                return alfrescoContext.equals("") ? "/" : alfrescoContext;
            }

        }, path, isFolder);
    }    
    
    /**
     * @see org.alfresco.repo.webdav.WebDAVMethod#flushXML(org.dom4j.io.XMLWriter)
     */
    @Override
    protected void flushXML(XMLWriter xml) throws IOException
    {
        // Do nothing, related to specific Office behaviour
    }
    
}
