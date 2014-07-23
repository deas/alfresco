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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.httpclient.AuthenticationException;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.solr.BoundedDeque;
import org.alfresco.solr.InformationServer;
import org.alfresco.solr.TrackerState;
import org.alfresco.solr.client.Acl;
import org.alfresco.solr.client.AclChangeSet;
import org.alfresco.solr.client.AclChangeSets;
import org.alfresco.solr.client.AclReaders;
import org.alfresco.solr.client.Node;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.util.DynamicallySizedThreadPoolExecutor;
import org.alfresco.util.TraceableThreadFactory;
import org.json.JSONException;
import org.quartz.Scheduler;

/**
 * Multithreaded ACL {@link Tracker} implementation.
 * 
 * @author Matt Ward
 */
public class AclTracker extends SingleThreadedAclTracker
{
    private static final int DEFAULT_CORE_POOL_SIZE = 4;

    private static final int DEFAULT_MAXIMUM_POOL_SIZE = -1; // -1 is a sign that it must match the core pool size

    private static final int DEFAULT_KEEP_ALIVE_TIME = 120; // seconds

    private static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY;

    private static final boolean DEFAULT_THREAD_DAEMON = Boolean.TRUE;

    private static final int DEFAULT_WORK_QUEUE_SIZE = -1;

    private static final RejectedExecutionHandler DEFAULT_REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

//    private static final int DEFAULT_TRANSACTION_DOCS_BATCH_SIZE = 100;

    private static final int DEFAULT_CHANGE_SET_ACLS_BATCH_SIZE = 100;

    private static final int DEFAULT_ACL_BATCH_SIZE = 10;

    private String poolName = "";

    private boolean enableMultiThreadedTracking = false;

    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;

    private int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE;

    private int keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;

    private int threadPriority = DEFAULT_THREAD_PRIORITY;

    private boolean threadDaemon = DEFAULT_THREAD_DAEMON;

    private int workQueueSize = DEFAULT_WORK_QUEUE_SIZE;

//    private int transactionDocsBatchSize = DEFAULT_TRANSACTION_DOCS_BATCH_SIZE;

    private int changeSetAclsBatchSize = DEFAULT_CHANGE_SET_ACLS_BATCH_SIZE;

    private int aclBatchSize = DEFAULT_ACL_BATCH_SIZE;

    private RejectedExecutionHandler rejectedExecutionHandler = DEFAULT_REJECTED_EXECUTION_HANDLER;

    /** the instance that will be given out by the factory */
    private DynamicallySizedThreadPoolExecutor threadPool;

    private LinkedBlockingQueue<AbstractWorkerRunnable> reindexThreadQueue = new LinkedBlockingQueue<AbstractWorkerRunnable>();

    private ReentrantReadWriteLock reindexThreadLock = new ReentrantReadWriteLock(true);

    
    /**
     * Default constructor, for testing.
     */
    public AclTracker()
    {
        super();
    }
    
    public AclTracker(Scheduler scheduler, String id, Properties p, SOLRAPIClient client, 
                String coreName, InformationServer informationServer)
    {
        super(scheduler, id, p, client, coreName, informationServer);

        enableMultiThreadedTracking = Boolean.parseBoolean(p.getProperty("alfresco.enableMultiThreadedTracking", "true"));
        corePoolSize = Integer.parseInt(p.getProperty("alfresco.corePoolSize", "3"));
        maximumPoolSize = Integer.parseInt(p.getProperty("alfresco.maximumPoolSize", "-1"));
        keepAliveTime = Integer.parseInt(p.getProperty("alfresco.keepAliveTime", "120"));
        threadPriority = Integer.parseInt(p.getProperty("alfresco.threadPriority", "5"));
        threadDaemon = Boolean.parseBoolean(p.getProperty("alfresco.threadDaemon", "true"));
        workQueueSize = Integer.parseInt(p.getProperty("alfresco.workQueueSize", "-1"));
//        transactionDocsBatchSize = Integer.parseInt(p.getProperty("alfresco.transactionDocsBatchSize", "100"));
        changeSetAclsBatchSize = Integer.parseInt(p.getProperty("alfresco.changeSetAclsBatchSize", "100"));
        aclBatchSize = Integer.parseInt(p.getProperty("alfresco.aclBatchSize", "10"));
        
        
        if (enableMultiThreadedTracking)
        {

            poolName = "SolrTrackingPool-" + coreName;

            // if the maximum pool size has not been set, change it to match the core pool size
            if (maximumPoolSize == DEFAULT_MAXIMUM_POOL_SIZE)
            {
                maximumPoolSize = corePoolSize;
            }

            // We need a thread factory
            TraceableThreadFactory threadFactory = new TraceableThreadFactory();
            threadFactory.setThreadDaemon(threadDaemon);
            threadFactory.setThreadPriority(threadPriority);

            if (poolName.length() > 0)
            {
                threadFactory.setNamePrefix(poolName);
            }

            BlockingQueue<Runnable> workQueue;
            if (workQueueSize < 0)
            {
                // We can have an unlimited queue, as we have a sensible thread pool!
                workQueue = new LinkedBlockingQueue<Runnable>();
            }
            else
            {
                // Use an array one for consistent performance on a small queue size
                workQueue = new ArrayBlockingQueue<Runnable>(workQueueSize);
            }

            // construct the instance
            threadPool = new DynamicallySizedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory, rejectedExecutionHandler);
        }
    }


    /**
     * @param reader
     * @throws AuthenticationException
     * @throws IOException
     * @throws JSONException
     */
    @Override
    protected void trackAclChangeSets() throws AuthenticationException, IOException, JSONException
    {
        if (!enableMultiThreadedTracking)
        {
            super.trackAclChangeSets();
            return;
        }

        boolean indexed = false;
        boolean upToDate = false;
        AclChangeSets aclChangeSets;
        BoundedDeque<AclChangeSet> changeSetsFound = new BoundedDeque<AclChangeSet>(100);
        HashSet<AclChangeSet> changeSetsIndexed = new HashSet<AclChangeSet>();
        TrackerState state = super.infoSrv.getTrackerState();
        
        do
        {
            int aclCount = 0;

            Long fromCommitTime = getChangeSetFromCommitTime(changeSetsFound, state.getLastGoodChangeSetCommitTimeInIndex());
            aclChangeSets = getSomeAclChangeSets(changeSetsFound, fromCommitTime, 60 * 60 * 1000L, 2000, state.getTimeToStopIndexing());

            Long maxChangeSetCommitTime = aclChangeSets.getMaxChangeSetCommitTime();
            if(maxChangeSetCommitTime != null)
            {
                state.setLastChangeSetCommitTimeOnServer(aclChangeSets.getMaxChangeSetCommitTime());
            }

            Long maxChangeSetId = aclChangeSets.getMaxChangeSetId();
            if(maxChangeSetId != null)
            {
                state.setLastChangeSetIdOnServer(aclChangeSets.getMaxChangeSetId());
            }
            
            log.info("Scanning Acl change sets ...");
            if (aclChangeSets.getAclChangeSets().size() > 0)
            {
                log.info(".... from " + aclChangeSets.getAclChangeSets().get(0));
                log.info(".... to " + aclChangeSets.getAclChangeSets().get(aclChangeSets.getAclChangeSets().size() - 1));
            }
            else
            {
                log.info(".... non found after lastTxCommitTime " + fromCommitTime);
            }

            ArrayList<AclChangeSet> changeSetBatch = new ArrayList<AclChangeSet>();
            for (AclChangeSet changeSet : aclChangeSets.getAclChangeSets())
            {
                boolean isInIndex = this.infoSrv.isInIndex(QueryConstants.FIELD_ACLTXID, changeSet.getId());
                if (isInIndex) 
                {
                    changeSetsFound.add(changeSet);
                }
                else 
                {
                    // Make sure we do not go ahead of where we started - we will check the holes here
                    // correctly next time
                    if (changeSet.getCommitTimeMs() > state.getTimeToStopIndexing())
                    {
                        upToDate = true;
                        break;
                    }
                    changeSetBatch.add(changeSet);
                    if (getAclCount(changeSetBatch) > changeSetAclsBatchSize)
                    {
                        indexed = true;
                        aclCount += indexBatchOfChangeSets(changeSetBatch);
                       
                        for (AclChangeSet scheduled : changeSetBatch)
                        {
                            changeSetsFound.add(scheduled);
                            changeSetsIndexed.add(scheduled);
                        }
                        changeSetBatch.clear();
                    }
                }

                if (aclCount > batchCount)
                {
                    if (super.infoSrv.getRegisteredSearcherCount() < getMaxLiveSearchers())
                    {
                        waitForAsynchronousReindexing();
                        for (AclChangeSet set : changeSetsIndexed)
                        {
                            super.infoSrv.indexAclTransaction(set, true);
                            if (set.getCommitTimeMs() > state.getLastIndexedChangeSetCommitTime())
                            {
                                state.setLastIndexedChangeSetCommitTime(set.getCommitTimeMs());
                                state.setLastIndexedChangeSetId(set.getId());
                            }
                            trackerStats.addChangeSetAcls((int) (set.getAclCount()));

                        }
                        changeSetsIndexed.clear();
                        super.infoSrv.commit();
                        aclCount = 0;
                    }
                }
                checkShutdown();
            }
            if (!changeSetBatch.isEmpty())
            {
                indexed = true;
                if (getAclCount(changeSetBatch) > 0)
                {
                    aclCount += indexBatchOfChangeSets(changeSetBatch);
                }
                for (AclChangeSet scheduled : changeSetBatch)
                {
                    changeSetsFound.add(scheduled);
                    changeSetsIndexed.add(scheduled);
                }
                changeSetBatch.clear();
            }
        }
        while ((aclChangeSets.getAclChangeSets().size() > 0) && (upToDate == false));

        if (indexed)
        {
            waitForAsynchronousReindexing();
            for (AclChangeSet set : changeSetsIndexed)
            {
                super.infoSrv.indexAclTransaction(set, true);
                if (set.getCommitTimeMs() > state.getLastIndexedChangeSetCommitTime())
                {
                    state.setLastIndexedChangeSetCommitTime(set.getCommitTimeMs());
                    state.setLastIndexedChangeSetId(set.getId());
                }
                trackerStats.addChangeSetAcls((int) (set.getAclCount()));
            }
            changeSetsIndexed.clear();
            super.infoSrv.commit();
        }
    }

    private int getAclCount(List<AclChangeSet> changeSetBatch)
    {
        int count = 0;
        for (AclChangeSet set : changeSetBatch)
        {
            count += set.getAclCount();
        }
        return count;
    }

    private int indexBatchOfChangeSets(List<AclChangeSet> changeSetBatch) throws AuthenticationException, IOException, JSONException
    {
        int aclCount = 0;
        ArrayList<AclChangeSet> nonEmptyChangeSets = new ArrayList<AclChangeSet>(changeSetBatch.size());
        for (AclChangeSet set : changeSetBatch)
        {
            if (set.getAclCount() > 0)
            {
                nonEmptyChangeSets.add(set);
            }
        }

        ArrayList<Acl> aclBatch = new ArrayList<Acl>();
        List<Acl> acls = client.getAcls(nonEmptyChangeSets, null, Integer.MAX_VALUE);
        for (Acl acl : acls)
        {
            if (log.isDebugEnabled())
            {
                log.debug(acl.toString());
            }
            aclBatch.add(acl);
            if (aclBatch.size() > aclBatchSize)
            {
                aclCount += aclBatch.size();
                AclIndexWorkerRunnable aiwr = new AclIndexWorkerRunnable(aclBatch);
                try
                {
                    reindexThreadLock.writeLock().lock();
                    // Add the runnable to the queue to ensure ordering
                    reindexThreadQueue.add(aiwr);
                }
                finally
                {
                    reindexThreadLock.writeLock().unlock();
                }
                threadPool.execute(aiwr);
                aclBatch = new ArrayList<Acl>();
            }
        }
        if (aclBatch.size() > 0)
        {
            aclCount += aclBatch.size();
            AclIndexWorkerRunnable aiwr = new AclIndexWorkerRunnable(aclBatch);
            try
            {
                reindexThreadLock.writeLock().lock();
                // Add the runnable to the queue to ensure ordering
                reindexThreadQueue.add(aiwr);
            }
            finally
            {
                reindexThreadLock.writeLock().unlock();
            }
            threadPool.execute(aiwr);
            aclBatch = new ArrayList<Acl>();
        }
        return aclCount;
    }

    protected synchronized void waitForAsynchronousReindexing()
    {
        AbstractWorkerRunnable currentRunnable = peekHeadReindexWorker();
        while (currentRunnable != null)
        {
            checkShutdown();
            synchronized (this)
            {
                try
                {
                    wait(100);
                }
                catch (InterruptedException e)
                {
                }
            }
            currentRunnable = peekHeadReindexWorker();
        }
    }

    /**
     * Read-safe method to peek at the head of the queue
     */
    private AbstractWorkerRunnable peekHeadReindexWorker()
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

    abstract class AbstractWorkerRunnable implements Runnable
    {
        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try
            {
                doWork();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (AuthenticationException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                // Triple check that we get the queue state right
                removeFromQueueAndProdHead();
            }

        }

        abstract protected void doWork() throws IOException, AuthenticationException, JSONException;

        /**
         * Removes this instance from the queue and notifies the HEAD
         */
        private void removeFromQueueAndProdHead()
        {
            try
            {
                reindexThreadLock.writeLock().lock();
                // Remove self from head of queue
                reindexThreadQueue.remove(this);
            }
            finally
            {
                reindexThreadLock.writeLock().unlock();
            }
        }
    }

    class NodeIndexWorkerRunnable extends AbstractWorkerRunnable
    {
        InformationServer infoServer;
        Node node;

        NodeIndexWorkerRunnable(Node node, InformationServer infoServer)
        {
            this.infoServer = infoServer;
            this.node = node;
        }

        protected void doWork() throws IOException, AuthenticationException, JSONException
        {
            this.infoServer.indexNode(node, true);
        }

    }

    class AclIndexWorkerRunnable extends AbstractWorkerRunnable
    {
        List<Acl> acls;

        AclIndexWorkerRunnable(List<Acl> acls)
        {
            this.acls = acls;
        }

        protected void doWork() throws IOException, AuthenticationException, JSONException
        {
            List<AclReaders> readers = client.getAclReaders(acls);
            indexAcl(readers, true);
        }
    }

    @Override
    public void close()
    {
        try
        {
            super.close();
           
        }
        finally
        {
            if (threadPool != null)
            {
                threadPool.shutdownNow();
            }
        }
        synchronized (this)
        {
            try
            {
                wait(1000);
            }
            catch (InterruptedException e)
            {
            }
        }
    }
}
