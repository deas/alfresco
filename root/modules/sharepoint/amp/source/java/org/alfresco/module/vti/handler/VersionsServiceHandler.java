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
package org.alfresco.module.vti.handler;

import java.util.List;

import org.alfresco.module.vti.metadata.model.DocumentVersionBean;
import org.alfresco.service.cmr.model.FileNotFoundException;

/**
 * Interface for versions web service handler
 * 
 * @author Dmitry Lazurkin
 */
public interface VersionsServiceHandler
{
    /**
     * Returns information about the versions of the specified file
     * 
     * @param fileName A string that contains the site-relative URL of the file in the form Folder_Name/File_Name
     * @return List<DocumentVersionBean> information about the versions of the specified file
     */
    public List<DocumentVersionBean> getVersions(String fileName) throws FileNotFoundException;

    /**
     * Restores the specified file version
     * 
     * @param fileName site relative url to the file
     * @param fileVersion file version to restore
     * @return List<DocumentVersionBean> list of DocumentVersion beans
     */
    public List<DocumentVersionBean> restoreVersion(String fileName, String fileVersion);

    /**
     * Deletes the specified file version
     * 
     * @param fileName site relative url to the file
     * @param fileVersion file version to restore
     * @return List<DocumentVersionBean> list of DocumentVersion beans
     */
    public List<DocumentVersionBean> deleteVersion(String fileName, String fileVersion) throws FileNotFoundException;

    /**
     * Deletes all versions of the specified file
     * 
     * @param fileName
     * @return DocumentVersionBean current document
     */
    public DocumentVersionBean deleteAllVersions(String fileName) throws FileNotFoundException;
}
