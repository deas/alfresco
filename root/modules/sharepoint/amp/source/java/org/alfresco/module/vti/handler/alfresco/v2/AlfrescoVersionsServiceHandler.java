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
package org.alfresco.module.vti.handler.alfresco.v2;

import java.util.Arrays;
import java.util.List;

import org.alfresco.module.vti.handler.alfresco.AbstractAlfrescoVersionsServiceHandler;
import org.alfresco.module.vti.metadata.model.DocumentVersionBean;
import org.alfresco.service.cmr.model.FileInfo;

/**
 * Alfresco implementation of VersionsServiceHandler and AbstractAlfrescoVersionsServiceHandler
 * 
 * @author Dmitry Lazurkin
 */
public class AlfrescoVersionsServiceHandler extends AbstractAlfrescoVersionsServiceHandler
{
    /**
     * @see org.alfresco.module.vti.handler.VersionsServiceHandler#deleteAllVersions(java.lang.String)
     */
    public List<DocumentVersionBean> deleteAllVersions(String fileName)
    {
        FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);

        assertDocument(documentFileInfo);

        versionService.deleteVersionHistory(documentFileInfo.getNodeRef());

        return Arrays.asList(new DocumentVersionBean[] {
              getDocumentVersionInfo(documentFileInfo) });
    }
}
