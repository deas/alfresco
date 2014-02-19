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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The purpose of this class is to provide utility methods for general
 * operations on a zip archive etc.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class ZipArchiveFile implements ArchiveFile
{

    private final String archivePath;

    private List<String> fileList;

    /**
     * @throws Exception
     * @throws IOException
     */
    public ZipArchiveFile(String archivePath) throws IOException, Exception
    {
        this.archivePath = archivePath;
        init();
    }
    /* (non-Javadoc)
     * @see org.alfresco.share.util.ArchiveFile1#getArchivePath()
     */
    public String getArchivePath()
    {
        return archivePath;
    }

    /* (non-Javadoc)
     * @see org.alfresco.share.util.ArchiveFile1#getFileList()
     */
    public List<String> getFileList()
    {
        return fileList;
    }

    /* (non-Javadoc)
     * @see org.alfresco.share.util.ArchiveFile1#hasEmptyFolder()
     */
    public boolean hasEmptyFolder()
    {
        // Entry name contains "/" means its a folder.
        return fileList.size() == 1 && fileList.get(0).contains("/");
    }

    /* (non-Javadoc)
     * @see org.alfresco.share.util.ArchiveFile1#getFileNamesInArchive()
     */
    public List<String> getFileNamesInArchive() throws Exception
    {
        return fileList;
    }

    private void init() throws Exception, IOException
    {
        fileList = new ArrayList<String>();
        FileInputStream fis = null;
        ZipInputStream zis = null;

        try
        {
            fis = new FileInputStream(archivePath);
            zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;

            // going through the archive file and adding each entry to fileList
            while ((entry = zis.getNextEntry()) != null)
            {
                fileList.add(entry.getName());
            }
        }
        catch (FileNotFoundException e)
        {
            throw new Exception("FileNotFoundException: File does not exist.");
        }
        catch (IOException e)
        {
            throw new Exception("Archive file is not decompressable");
        }
        finally
        {
            // Finally close all the streams
            if (zis != null)
            {
                zis.close();
            }
            if (fis != null)
            {
                fis.close();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.share.util.ArchiveFile1#isArchiveEmpty()
     */
    public boolean isArchiveEmpty()
    {
        return fileList.size() < 1;
    }
}
