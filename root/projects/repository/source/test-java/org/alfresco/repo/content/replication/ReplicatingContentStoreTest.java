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
package org.alfresco.repo.content.replication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.alfresco.repo.content.AbstractWritableContentStoreTest;
import org.alfresco.repo.content.ContentContext;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.ContentStore.ContentUrlHandler;
import org.alfresco.repo.content.filestore.FileContentStore;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.alfresco.util.GUID;
import org.alfresco.util.TempFileProvider;
import org.junit.Before;
import org.junit.experimental.categories.Category;

/**
 * Tests read and write functionality for the replicating store.
 * <p>
 * By default, replication is off for both the inbound and outbound
 * replication.  Specific tests change this.
 * 
 * @see org.alfresco.repo.content.replication.ReplicatingContentStore
 * 
 * @author Derek Hulley
 */
@Category(OwnJVMTestsCategory.class)
public class ReplicatingContentStoreTest extends AbstractWritableContentStoreTest
{
    private static final String SOME_CONTENT = "The No. 1 Ladies' Detective Agency";
    
    private ReplicatingContentStore replicatingStore;
    private ContentStore primaryStore;
    private List<ContentStore> secondaryStores;
    
    @Before
    public void before() throws Exception
    {
        File tempDir = TempFileProvider.getTempDir();
        // create a primary file store
        String storeDir = tempDir.getAbsolutePath() + File.separatorChar + GUID.generate();
        primaryStore = new FileContentStore(ctx, storeDir);
        // create some secondary file stores
        secondaryStores = new ArrayList<ContentStore>(3);
        for (int i = 0; i < 4; i++)
        {
            storeDir = tempDir.getAbsolutePath() + File.separatorChar + GUID.generate();
            FileContentStore store = new FileContentStore(ctx, storeDir);
            secondaryStores.add(store);
            // Only the first 3 are writable
            if (i >= 3)
            {
                store.setReadOnly(true);
            }
        }
        // Create the replicating store
        replicatingStore = new ReplicatingContentStore();
        replicatingStore.setPrimaryStore(primaryStore);
        replicatingStore.setSecondaryStores(secondaryStores);
        replicatingStore.setOutbound(false);
        replicatingStore.setInbound(false);
    }

    @Override
    public ContentStore getStore()
    {
        return replicatingStore;
    }
    
    /**
     * Performs checks necessary to ensure the proper replication of content for the given
     * URL
     */
    private void checkForReplication(boolean inbound, boolean outbound, String contentUrl, String content)
    {
        if (inbound)
        {
            ContentReader reader = primaryStore.getReader(contentUrl);
            assertTrue("Content was not replicated into the primary store", reader.exists());
            assertEquals("The replicated content was incorrect", content, reader.getContentString());
        }
        if (outbound)
        {
            for (ContentStore store : secondaryStores)
            {
                // This is only required for writable stores
                if (!store.isWriteSupported())
                {
                    continue;
                }
                ContentReader reader = store.getReader(contentUrl);
                assertTrue("Content was not replicated out to the secondary stores within a second", reader.exists());
                assertEquals("The replicated content was incorrect", content, reader.getContentString());
            }
        }
    }
    
    /**
     * Checks that the url is present in each of the stores
     * 
     * @param contentUrl
     * @param mustExist true if the content must exist, false if it must <b>not</b> exist
     */
    private void checkForUrl(String contentUrl, boolean mustExist)
    {
        // check that the URL is present for each of the stores
        for (ContentStore store : secondaryStores)
        {
            final Set<String> urls = new HashSet<String>(1027);
            ContentUrlHandler handler = new ContentUrlHandler()
            {
                public void handle(String contentUrl)
                {
                    urls.add(contentUrl);
                }
            };
            store.getUrls(handler);
            assertTrue("URL of new content not present in store", urls.contains(contentUrl) == mustExist);
        }
    }
    
    public void testNoReplication() throws Exception
    {
        ContentWriter writer = getWriter();
        writer.putContent(SOME_CONTENT);
        
        checkForReplication(false, false, writer.getContentUrl(), SOME_CONTENT);
    }
    
    public void testOutboundReplication() throws Exception
    {
        replicatingStore.setOutbound(true);
        
        // write some content
        ContentWriter writer = getWriter();
        writer.putContent(SOME_CONTENT);
        String contentUrl = writer.getContentUrl();
        
        checkForReplication(false, true, contentUrl, SOME_CONTENT);
        
        // check for outbound deletes
        replicatingStore.delete(contentUrl);
        checkForUrl(contentUrl, false);
    }
    
    public void testAsyncOutboundReplication() throws Exception
    {
        ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        
        replicatingStore.setOutbound(true);
        replicatingStore.setOutboundThreadPoolExecutor(tpe);
        
        // write some content
        ContentWriter writer = getWriter();
        writer.putContent(SOME_CONTENT);
        String contentUrl = writer.getContentUrl();
        
        // wait for a second
        synchronized(this)
        {
            this.wait(1000L);
        }
        
        checkForReplication(false, true, contentUrl, SOME_CONTENT);
        
        // check for outbound deletes
        replicatingStore.delete(contentUrl);
        checkForUrl(contentUrl, false);
    }
    
    public void testInboundReplication() throws Exception
    {
        replicatingStore.setInbound(false);
        
        // pick a secondary store and write some content to it
        ContentStore secondaryStore = secondaryStores.get(2);
        ContentWriter writer = secondaryStore.getWriter(ContentContext.NULL_CONTEXT);
        writer.putContent(SOME_CONTENT);
        String contentUrl = writer.getContentUrl();
        
        // get a reader from the replicating store
        ContentReader reader = replicatingStore.getReader(contentUrl);
        assertTrue("Reader must have been found in secondary store", reader.exists());
        
        // set inbound replication on and repeat
        replicatingStore.setInbound(true);
        reader = replicatingStore.getReader(contentUrl);
        
        // this time, it must have been replicated to the primary store
        checkForReplication(true, false, contentUrl, SOME_CONTENT);
    }
    
    public void testTargetContentUrlExists()
    {
        replicatingStore.setOutbound(true);
        replicatingStore.setInbound(false);
        // pick a secondary store and write some content to it
        ContentStore secondaryStore = secondaryStores.get(2);
        ContentWriter secondaryWriter = secondaryStore.getWriter(ContentContext.NULL_CONTEXT);
        secondaryWriter.putContent("Content for secondary");
        String secondaryContentUrl = secondaryWriter.getContentUrl();
        
        // Now write to the primary store
        ContentWriter replicatingWriter = replicatingStore.getWriter(new ContentContext(null, secondaryContentUrl));
        String replicatingContent = "Content for primary";
        try
        {
            replicatingWriter.putContent(replicatingContent);
            fail("Replication should fail when the secondary store already has the content");
        }
        catch (ContentIOException e)
        {
            // Expected
        }
    }
}
