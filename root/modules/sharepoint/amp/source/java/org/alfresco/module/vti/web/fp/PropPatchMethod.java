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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.repo.webdav.WebDAVProperty;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.dom4j.io.XMLWriter;
import org.springframework.extensions.surf.util.URLDecoder;
import org.w3c.dom.Node;

/**
 * Implements the WebDAV PROPPATCH method with VTI specific
 * 
 * @author DmitryVas
 */
public class PropPatchMethod extends org.alfresco.repo.webdav.PropPatchMethod
{
    private String alfrescoContext;
    private Map<String,List<String>> ignoredProperties;

    public PropPatchMethod(String alfrescoContext, Map<String,List<String>> ignoredProperties)
    {
        this.alfrescoContext = alfrescoContext;
        this.ignoredProperties = ignoredProperties;
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
     * Creates the WebDAVProperty if appropriate, or returns null if
     *  the requested property is one to be ignored (eg the legacy
     *  extension properties such as Z:Win32FileAttributes)
     */
    @Override
    protected WebDAVProperty createProperty(Node node) {
        // Have the property created
        WebDAVProperty prop = super.createProperty(node);
        
        // Is it one to ignore?
        if (prop.getNamespaceUri() != null)
        {
           List<String> namespaceIgnoreProps = ignoredProperties.get(prop.getNamespaceUri());
           if (namespaceIgnoreProps != null && namespaceIgnoreProps.contains(prop.getName()))
           {
              // We are to ignore a property in this namespace with this name
              if (logger.isDebugEnabled())
              {
                 logger.debug("Ignoring Property " + prop.getName() + " from " + prop.getNamespaceUri() + "(" + prop.getNamespaceName() + ")");
              }
              return null;
           }
        }
        
        // Valid property, return it
        return prop;
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
