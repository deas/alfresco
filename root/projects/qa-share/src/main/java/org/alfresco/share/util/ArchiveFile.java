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
package org.alfresco.share.util;

import java.util.List;

/**
 * The purpose of this interface is to provide utility methods for various
 * archive file operations.
 * 
 * @author Abhijeet Bharade
 * 
 */
public interface ArchiveFile
{

    /**
     * Gives the path where is the archive is store.
     * 
     * @return the archivePath
     */
    public abstract String getArchivePath();

    /**
     * Returns the list of file and folders in the archive.
     * 
     * @return the fileList
     */
    public abstract List<String> getFileList();

    /**
     * Returns boolean depending on whether the the archive has an empty folder
     * or not.
     * 
     * @return the hasFiles
     */
    public abstract boolean hasEmptyFolder();

    /**
     * Reads an archive file eg: zip and returns the list of files in it.
     * 
     * @return
     * @throws Exception
     */
    public abstract List<String> getFileNamesInArchive() throws Exception;

    /**
     * This method returns a bool depending on whether the archive is empty or
     * not.
     * 
     * @return
     */
    public abstract boolean isArchiveEmpty();

}