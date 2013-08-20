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

import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Implements the WebDAV UNLOCK method with VTI specific
 * 
 * @author DmitryVas
 */
public class UnlockMethod extends org.alfresco.repo.webdav.UnlockMethod
{
    private String alfrescoContext;

    public UnlockMethod(String alfrescoContext)
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
}
