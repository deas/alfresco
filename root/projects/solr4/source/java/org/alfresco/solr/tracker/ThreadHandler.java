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
package org.alfresco.solr.tracker;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.solr.tracker.pool.DefaultTrackerPoolFactory;
import org.alfresco.solr.tracker.pool.TrackerPoolFactory;

/**
 * This class handles threads for trackers.
 * 
 * @author Ahmed Owian
 */
public class ThreadHandler implements QueueHandler
{
    /** the instance that will be given out by the factory */
    private ThreadPoolExecutor threadPool;
    private LinkedBlockingQueue<AbstractWorkerRunnable> reindexThreadQueue = new LinkedBlockingQueue<>();
    private ReentrantReadWriteLock reindexThreadLock = new ReentrantReadWriteLock(true);

    public ThreadHandler(Properties p, String coreName)
    {
        // construct the instance
        TrackerPoolFactory trackerPoolFactory = new DefaultTrackerPoolFactory(p, coreName);
        threadPool = trackerPoolFactory.create();
    }
    

    public void scheduleTask(AbstractWorkerRunnable awr)
    {
        try
        {
            reindexThreadLock.writeLock().lock();
            // Add the runnable to the queue to ensure ordering
            reindexThreadQueue.add(awr);
        }
        finally
        {
            reindexThreadLock.writeLock().unlock();
        }
        threadPool.execute(awr);
    }
    
    /**
     * Removes the job from the queue and notifies the HEAD
     */
    public void removeFromQueueAndProdHead(AbstractWorkerRunnable job)
    {
        try
        {
            reindexThreadLock.writeLock().lock();
            // Remove self from head of queue
            reindexThreadQueue.remove(job);
        }
        finally
        {
            reindexThreadLock.writeLock().unlock();
        }
    }
    
    /**
     * Read-safe method to peek at the head of the queue
     */
    public AbstractWorkerRunnable peekHeadReindexWorker()
    {
        try
        {
            reindexThreadLock.readLock().lock();
            return reindexThreadQueue.peek();
        }
        finally
        {
            reindexThreadLock.readLock().unlock();
        }
    }
    
    public void shutDownThreadPool()
    { 
        if (threadPool != null)
        {
            threadPool.shutdownNow();
        }
    }
}
