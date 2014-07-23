/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.solr.content;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests {@link SolrContentStoreTest}
 * 
 * @author Derek Hulley
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SolrContentStoreTest
{
    private String rootStr;
    
    @Before
    public void setUp() throws IOException
    {
        File tempFile = File.createTempFile("SolrContentStoreTest-", ".bin");
        File tempFolder = tempFile.getParentFile();
        rootStr = tempFolder.getAbsolutePath() + "/" + System.currentTimeMillis();
        rootStr = new File(rootStr).getAbsolutePath();          // Ensure we handle separator char for this test
    }
    
    @After
    public void tearDown() throws IOException
    {
        if (rootStr != null)
        {
            File rootDir = new File(rootStr);
            FileUtils.deleteDirectory(rootDir);
        }
    }
    
    @Test
    public void rootLocation()
    {
        SolrContentStore store = new SolrContentStore(rootStr);
        File rootDir = new File(rootStr);
        Assert.assertTrue(rootDir.exists());
        Assert.assertTrue(rootDir.isDirectory());
        
        Assert.assertEquals(rootStr, store.getRootLocation());
    }
    
    @Test
    public void failedRootLocation() throws IOException
    {
        File rootFile = new File(rootStr);
        rootFile.createNewFile();
        try
        {
            new SolrContentStore(rootStr);
            Assert.fail("Failed to handle file in root location.");
        }
        catch (RuntimeException e)
        {
            // Expected
        }
        rootFile.delete();
    }
    
    @Test
    public void reconstruct()
    {
        new SolrContentStore(rootStr);
        new SolrContentStore(rootStr);
    }
}
