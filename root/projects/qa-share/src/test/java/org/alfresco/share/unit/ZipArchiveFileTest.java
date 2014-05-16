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
package org.alfresco.share.unit;

import static org.testng.AssertJUnit.assertTrue;

import org.alfresco.share.util.ArchiveFile;
import org.alfresco.share.util.ZipArchiveFile;
import org.junit.Before;
import org.junit.Test;
import org.testng.annotations.AfterClass;

/**
 * @author Abhijeet Bharade
 * 
 */
public class ZipArchiveFileTest
{

    ArchiveFile nonEmptyFolderArchive;
    ArchiveFile folderInAnEmptyFolderArchive;
    ArchiveFile emptyFolderArchive;
    ArchiveFile emptyArchive;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        String archiveFolder = "testdata/archives";
        nonEmptyFolderArchive = new ZipArchiveFile(archiveFolder + "/share.zip");
        folderInAnEmptyFolderArchive = new ZipArchiveFile(archiveFolder + "/FolderEmptyWithChild.zip");
        emptyFolderArchive = new ZipArchiveFile(archiveFolder + "/FolderEmpty.zip");
        emptyArchive = new ZipArchiveFile(archiveFolder + "/empty.zip");
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass(alwaysRun=true)
    public void tearDown() throws Exception
    {
        nonEmptyFolderArchive = null;
        emptyFolderArchive = null;
        folderInAnEmptyFolderArchive = null;
    }

    /**
     * Tests the method ZipArchiveFile.isArchiveEmpty()
     */
    @Test
    public void isArchiveEmptyTest()
    {

        try
        {
            //fail();
            
            assertTrue("Archive should not have empty folder.", !emptyArchive.hasEmptyFolder());
            assertTrue("Archive should not have empty folder.", !nonEmptyFolderArchive.hasEmptyFolder());
            assertTrue("Archive should not have empty folder.", !folderInAnEmptyFolderArchive.hasEmptyFolder());
            assertTrue("Archive should have empty folder.", emptyFolderArchive.hasEmptyFolder());
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Tests the method ZipArchiveFile.isArchiveEmpty()
     */
    @Test
    public void isArchiveEmptyTes()
    {
        assertTrue("Archive should be empty.", emptyArchive.isArchiveEmpty());
        assertTrue("Archive should not be empty.", !nonEmptyFolderArchive.isArchiveEmpty());
        assertTrue("Archive should not be empty.", !folderInAnEmptyFolderArchive.isArchiveEmpty());
        assertTrue("Archive should not be empty.", !emptyFolderArchive.isArchiveEmpty());
    }

}
