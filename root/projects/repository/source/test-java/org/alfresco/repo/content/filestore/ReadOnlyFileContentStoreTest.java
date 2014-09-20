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
package org.alfresco.repo.content.filestore;

import java.io.File;

import org.alfresco.repo.content.AbstractReadOnlyContentStoreTest;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.alfresco.util.TempFileProvider;
import org.junit.Before;
import org.junit.experimental.categories.Category;

/**
 * Tests the file-based store when in read-only mode.
 * 
 * @see org.alfresco.repo.content.filestore.FileContentStore
 * 
 * @since 2.1
 * @author Derek Hulley
 */
@Category(OwnJVMTestsCategory.class)
public class ReadOnlyFileContentStoreTest extends AbstractReadOnlyContentStoreTest
{
    private FileContentStore store;
    
    @Before
    public void before() throws Exception
    {
        // create a store that uses a subdirectory of the temp directory
        File tempDir = TempFileProvider.getTempDir();
        store = new FileContentStore(ctx,
                tempDir.getAbsolutePath() +
                File.separatorChar +
                getName());
        // disallow random access
        store.setReadOnly(true);
    }
    
    @Override
    protected ContentStore getStore()
    {
        return store;
    }
}
