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
package org.alfresco.util.cache;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.transaction.TransactionListener;
import org.alfresco.util.transaction.TransactionSupportUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

/**
 * The base implementation for an asynchronously refreshed cache. 
 * 
 * Currently supports one value or a cache per key (such as tenant.)  Implementors just need to provide buildCache(String key/tennnantId)
 * 
 * @author Andy
 * @since 4.1.3
 *
 * @author mrogers
 * MER 17/04/2014 Refactored to core and generalised tennancy
 */
public abstract class AbstractAsynchronouslyRefreshedCache<T> 
	implements AsynchronouslyRefreshedCache<T>, 
	RefreshableCacheListener, 
	Callable<Void>, 
	BeanNameAware,
	InitializingBean, 
	TransactionListener
{
	   private static final String RESOURCE_KEY_TXN_DATA = "AbstractAsynchronouslyRefreshedCache.TxnData";
	    
	    private static Log logger = LogFactory.getLog(AbstractAsynchronouslyRefreshedCache.class);

	    private enum ActionState
	    {
	        IDLE, WAITING, RUNNING, DONE
	    };

            private enum CacheAction
            {
                REFRESH, REMOVE;
            };
	    private ThreadPoolExecutor threadPoolExecutor;
	    private AsynchronouslyRefreshedCacheRegistry registry;

	    // State

	    private List<RefreshableCacheListener> listeners = new LinkedList<RefreshableCacheListener>();
	    private final ReentrantReadWriteLock liveLock = new ReentrantReadWriteLock();
	    private final ReentrantReadWriteLock actionLock = new ReentrantReadWriteLock();
	    private final ReentrantReadWriteLock runLock = new ReentrantReadWriteLock();
	    private HashMap<String, T> live = new HashMap<String, T>();
	    private LinkedHashSet<Action> actionQueue = new LinkedHashSet<Action>();
	    private String cacheId;
	    private ActionState actionState = ActionState.IDLE;
	    private String resourceKeyTxnData;

	    @Override
	    public void register(RefreshableCacheListener listener)
	    {
	        listeners.add(listener);
	    }

	    /**
	     * @param threadPool
	     *            the threadPool to set
	     */
	    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor)
	    {
	        this.threadPoolExecutor = threadPoolExecutor;
	    }

	    /**
	     * @param registry
	     *            the registry to set
	     */
	    public void setRegistry(AsynchronouslyRefreshedCacheRegistry registry)
	    {
	        this.registry = registry;
	    }


	    public void init()
	    {
	        registry.register(this);
	    }

	    @Override
	    public T get(String key)
	    {
	        liveLock.readLock().lock();
	        try
	        {
	            if (live.get(key) != null)
	            {
	                if (logger.isDebugEnabled())
	                {
	                    logger.debug("get() from cache");
	                }
	                return live.get(key);
	            }
	        }
	        finally
	        {
	            liveLock.readLock().unlock();
	        }

	        if (logger.isDebugEnabled())
	        {
	            logger.debug("get() miss, sechudling and waiting ...");
	        }

	        // There was nothing to return so we build and return
	        Action action = null;
	        actionLock.writeLock().lock();
	        try
	        {
	            // Is there anything we can wait for
	            for (Action existing : actionQueue)
	            {
	                if (existing.getKey().equals(key))
	                {
	                    if (logger.isDebugEnabled())
	                    {
	                        logger.debug("get() found existing build to wait for  ...");
	                    }
	                    action = existing;
	                }
	            }

	            if (action == null)
	            {
	                if (logger.isDebugEnabled())
	                {
	                    logger.debug("get() building from scratch");
	                }
	                action = new Action(key, CacheAction.REFRESH);
	                actionQueue.add(action);
	            }

	        }
	        finally
	        {
	            actionLock.writeLock().unlock();
	        }
	        submit();
	        waitForBuild(action);

	        return get(key);
	    }

	    public void forceInChangesForThisUncommittedTransaction(String key)
	    {
	        if (logger.isDebugEnabled())
	        {
	            logger.debug("Building cache for tenant " + key + " ......");
	        }
	        T cache = buildCache(key);
	        if (logger.isDebugEnabled())
	        {
	            logger.debug(".... cache built for tenant " + key);
	        }

	        liveLock.writeLock().lock();
	        try
	        {
	            live.put(key, cache);
	        }
	        finally
	        {
	            liveLock.writeLock().unlock();
	        }
	    }

	    protected void waitForBuild(Action action)
	    {
	        while (action.getState() != ActionState.DONE)
	        {
	            synchronized (action)
	            {
	                try
	                {
	                    action.wait(100);
	                }
	                catch (InterruptedException e)
	                {
	                }
	            }
	        }
	    }

	    @Override
	    public void refresh(String key)
	    {
	        // String tenantId = tenantService.getCurrentUserDomain();
	        if (logger.isDebugEnabled())
	        {
	            logger.debug("Async cache refresh request: " + cacheId + " for tenant " + key);
	        }
	        registry.broadcastEvent(new RefreshableCacheRefreshEvent(cacheId, key), true);
	    }

	    @Override
            public void remove(String key)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Async cache remove request: " + cacheId + " for tenant " + key);
                }
                registry.broadcastEvent(new RefreshableCacheRemoveEvent(cacheId, key), true);
            }

	    @Override
	    public void onRefreshableCacheEvent(RefreshableCacheEvent refreshableCacheEvent)
	    {
	        if (logger.isDebugEnabled())
	        {
	            logger.debug("Async cache onRefreshableCacheEvent " + refreshableCacheEvent);
	        }
	        if (false == refreshableCacheEvent.getCacheId().equals(cacheId))
	        {
	            return;
	        }

                CacheAction action = CacheAction.REFRESH;
                if (refreshableCacheEvent instanceof RefreshableCacheRemoveEvent)
                {
                    action = CacheAction.REMOVE;
                }

	        // If in a transaction delay the refresh until after it commits
	        if (TransactionSupportUtil.getTransactionId() != null)
	        {
	            if (logger.isDebugEnabled())
	            {
	                logger.debug("Async cache adding" + refreshableCacheEvent.getKey() + " to post commit list");
	            }
	            TransactionData txData = getTransactionData();
	            txData.actions.put(refreshableCacheEvent.getKey(), action);
	        }
	        else
	        {
	            LinkedHashMap<String, CacheAction> actions = new LinkedHashMap<String, CacheAction>();
	            actions.put(refreshableCacheEvent.getKey(), action);
	            queueActionsAndSubmit(actions);
	        }
	    }
	    
	    /**
	     * To be used in a transaction only.
	     */
	    private TransactionData getTransactionData()
	    {
	        TransactionData data = (TransactionData) TransactionSupportUtil.getResource(resourceKeyTxnData);
	        if (data == null)
	        {
	            data = new TransactionData();
	            // create and initialize caches
	            data.actions = new LinkedHashMap<String, CacheAction>();

	            // ensure that we get the transaction callbacks as we have bound the unique
	            // transactional caches to a common manager
	            TransactionSupportUtil.bindListener(this, 0);
	            TransactionSupportUtil.bindResource(resourceKeyTxnData, data);
	        }
	        return data;
	    }

	    private void queueActionsAndSubmit(LinkedHashMap<String, CacheAction> actions)
	    {
	        if ((actions == null) || (actions.size() == 0))
	        {
	            return;
	        }
	        actionLock.writeLock().lock();
	        try
	        {
	            for (Entry<String, CacheAction> entry : actions.entrySet())
	            {
	                if (logger.isDebugEnabled())
	                {
	                    logger.debug("Async cache adding " + entry.getValue().toString().toLowerCase() + " to queue for " + entry.getKey());
	                }
	                actionQueue.add(new Action(entry.getKey(), entry.getValue()));
	            }
	        }
	        finally
	        {
	            actionLock.writeLock().unlock();
	        }
	        submit();
	    }

	    @Override
	    public boolean isUpToDate(String key)
	    {
	       actionLock.readLock().lock();
	       try
	       {
	           for(Action action : actionQueue)
	           {
	               if(action.getKey().equals(key))
	               {
	                   return false;
	               }
	           }
	           if (TransactionSupportUtil.getTransactionId() != null)
	           {
	               return (!getTransactionData().actions.containsKey(key));
	           }
	           else
	           {
	               return true;
	           }
	       }
	       finally
	       {
	           actionLock.readLock().unlock();
	       }
	    }
	    
	    /**
	     * Must be run with runLock.writeLock
	     */
	    private Action getNextAction()
	    {
	        if (runLock.writeLock().isHeldByCurrentThread())
	        {
	            for (Action action : actionQueue)
	            {
	                if (action.state == ActionState.WAITING)
	                {
	                    return action;
	                }
	            }
	            return null;
	        }
	        else
	        {
	            throw new IllegalStateException("Method should not be called without holding the write lock");
	        }

	    }

	    /**
	     * Must be run with runLock.writeLock
	     */
	    private int countWaiting()
	    {
	        int count = 0;
	        if (runLock.writeLock().isHeldByCurrentThread())
	        {
	            actionLock.readLock().lock();
	            try
	            {
	                for (Action action : actionQueue)
	                {
	                    if (action.state == ActionState.WAITING)
	                    {
	                        count++;
	                    }
	                }
	                return count;
	            }
	            finally
	            {
	                actionLock.readLock().unlock();
	            }
	        }
	        else
	        {
	            throw new IllegalStateException("Method should not be called without holding the write lock");
	        }

	    }

	    private void submit()
	    {
	        runLock.writeLock().lock();
	        try
	        {
	            if (actionState == ActionState.IDLE)
	            {
	                if (logger.isDebugEnabled())
	                {
	                    logger.debug("submit() scheduling job");
	                }
	                threadPoolExecutor.submit(this);
	                actionState = ActionState.WAITING;
	            }
	        }
	        finally
	        {
	            runLock.writeLock().unlock();
	        }
	    }

	    @Override
	    public Void call()
	    {
	        try
	        {
	            doCall();
	            return null;
	        }
	        catch (Exception e)
	        {
	            logger.error("Cache update failed (" + this.getCacheId() + ").", e);
	            runLock.writeLock().lock();
	            try
	            {
	                threadPoolExecutor.submit(this);
	                actionState = ActionState.WAITING;
	            }
	            finally
	            {
	                runLock.writeLock().unlock();
	            }
	            return null;
	        }
	    }

	    private void doCall() throws Exception
	    {
	        Action action = setUpAction();
	        if (action == null)
	        {
	            return;
	        }

	        if (logger.isDebugEnabled())
	        {
	            logger.debug("Building cache for key" + action.getKey()); 
	        }

	        try
	        {
	            doAction(action);
	        }
	        catch (Exception e)
	        {
	            action.setState(ActionState.WAITING);
	            throw e;
	        }
	    }

	    private void doAction(Action action)
	    {
                T cache = null;
                if (action.action == CacheAction.REFRESH)
	        {
	            if (logger.isDebugEnabled())
	            {
	                logger.debug("Building cache for tenant" + action.getKey()+ " ......");
	            }
	            cache = buildCache(action.getKey());
   	            if (logger.isDebugEnabled())
	            {
	                logger.debug(".... cache built for tenant" + action.getKey());
	            }
	        }

	        liveLock.writeLock().lock();
	        try
	        {
                    if (action.action == CacheAction.REFRESH)
                    {
                        live.put(action.getKey(), cache);
                    }
                    else if (action.action == CacheAction.REMOVE)
                    {
                        live.remove(action.getKey());
                    }
	        }
	        finally
	        {
	            liveLock.writeLock().unlock();
	        }

	        if (logger.isDebugEnabled())
	        {
	            logger.debug("Cache entry updated for tenant" + action.getKey());
	        }

                if (action.action == CacheAction.REFRESH)
                {
                    broadcastEvent(new RefreshableCacheRefreshedEvent(cacheId, action.key));
                }
                else if (action.action == CacheAction.REMOVE)
                {
                    broadcastEvent(new RefreshableCacheRemovedEvent(cacheId, action.key));
                }
	        
	        runLock.writeLock().lock();
	        try
	        {
	            actionLock.writeLock().lock();
	            try
	            {
	                if (countWaiting() > 0)
	                {
	                    if (logger.isDebugEnabled())
	                    {
	                        logger.debug("Rescheduling ... more work");
	                    }
	                    threadPoolExecutor.submit(this);
	                    actionState = ActionState.WAITING;
	                }
	                else
	                {
	                    if (logger.isDebugEnabled())
	                    {
	                        logger.debug("Nothing to do .... going idle");
	                    }
	                    actionState = ActionState.IDLE;
	                }
	                action.setState(ActionState.DONE);
	                actionQueue.remove(action);
	            }
	            finally
	            {
	                actionLock.writeLock().unlock();
	            }
	        }
	        finally
	        {
	            runLock.writeLock().unlock();
	        }
	    }

	    private Action setUpAction() throws Exception
	    {
	        Action action = null;
	        runLock.writeLock().lock();
	        try
	        {
	            if (actionState == ActionState.WAITING)
	            {
	                actionLock.writeLock().lock();
	                try
	                {
	                    action = getNextAction();
	                    if (action != null)
	                    {
	                        actionState = ActionState.RUNNING;
	                        action.setState(ActionState.RUNNING);
	                        return action;
	                    }
	                    else
	                    {
	                        actionState = ActionState.IDLE;
	                        return null;
	                    }
	                }
	                finally
	                {
	                    actionLock.writeLock().unlock();
	                }
	            }
	            else
	            {
	                return null;
	            }
	        }
	        catch (Exception e)
	        {
	            if (action != null)
	            {
	                action.setState(ActionState.WAITING);
	            }
	            throw e;
	        }
	        finally
	        {
	            runLock.writeLock().unlock();
	        }

	    }

	    @Override
	    public void setBeanName(String name)
	    {
	        cacheId = name;

	    }

	    @Override
	    public String getCacheId()
	    {
	        return cacheId;
	    }

	    /**
	     * Build the cache entry for the specific key.
	     * This method is called in a thread-safe manner i.e. it is only ever called by a single
	     * thread.
	     * 
	     * @param key
	     * @return new Cache instance
	     */
	    protected abstract T buildCache(String key);

	    private static class Action
	    {
	        private String key;
                private CacheAction action;

	        private volatile ActionState state = ActionState.WAITING;

	        Action(String key, CacheAction action)
	        {
	            this.key = key;
                    this.action = action;
	        }

	        /**
	         * @return the tenantId
	         */
	        public String getKey()
	        {
	            return key;
	        }

	        /**
	         * @return the state
	         */
	        public ActionState getState()
	        {
	            return state;
	        }

	        /**
	         * @param state
	         *            the state to set
	         */
	        public void setState(ActionState state)
	        {
	            this.state = state;
	        }

	        @Override
	        public int hashCode()
	        {
	            // The bucked is determined by the tenantId alone - we are going to change the state
	            final int prime = 31;
	            int result = 1;
	            result = prime * result + ((key == null) ? 0 : key.hashCode());
	            return result;
	        }

	        @Override
	        public boolean equals(Object obj)
	        {
	            if (this == obj)
	                return true;
	            if (obj == null)
	                return false;
	            if (getClass() != obj.getClass())
	                return false;
	            Action other = (Action) obj;
	            if (state != other.state)
	                return false;
	            if (key == null)
	            {
	                if (other.key != null)
	                    return false;
	            }
	            else if (!key.equals(other.key))
	                return false;
                    if (action == null)
                    {
                        if (other.action != null)
                            return false;
                    }
                    else if (!action.equals(other.action))
                        return false;
            
	            return true;
	        }

	        @Override
	        public String toString()
	        {
	            return "Refresh [key=" + key + ", state=" + state + ", hashCode()=" + hashCode() + "]";
	        }

	    }

	    @Override
	    public void afterPropertiesSet() throws Exception
	    {
	        PropertyCheck.mandatory(this, "threadPoolExecutor", threadPoolExecutor);
	        PropertyCheck.mandatory(this, "registry", registry);
	        registry.register(this);
	        
	        
	        resourceKeyTxnData = RESOURCE_KEY_TXN_DATA + "." + cacheId;

	    }

	    public void broadcastEvent(RefreshableCacheEvent event)
	    {
	        if (logger.isDebugEnabled())
	        {
	            logger.debug("Notifying cache listeners for " + getCacheId() + " " + event);
	        }
	        // If the system is up and running, broadcast the event immediately
	        for (RefreshableCacheListener listener : this.listeners)
	        {
	            listener.onRefreshableCacheEvent(event);
	        }

	    }

	    @Override
	    public void beforeCommit(boolean readOnly)
	    {
	        // Nothing
	    }

	    @Override
	    public void beforeCompletion()
	    {
	        // Nothing
	    }

	    @Override
	    public void afterCommit()
	    {
	        TransactionData txnData = getTransactionData();
	        queueActionsAndSubmit(txnData.actions);
	    }

	    @Override
	    public void afterRollback()
	    {
	        // Nothing
	    }

	    private static class TransactionData
	    {
	        LinkedHashMap<String, CacheAction> actions;
	    }
}
