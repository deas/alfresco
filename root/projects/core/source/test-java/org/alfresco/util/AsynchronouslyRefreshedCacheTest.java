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
package org.alfresco.util;

import java.util.concurrent.ThreadPoolExecutor;

import junit.framework.TestCase;

import org.alfresco.util.cache.AbstractAsynchronouslyRefreshedCache;
import org.alfresco.util.cache.DefaultAsynchronouslyRefreshedCacheRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

public class AsynchronouslyRefreshedCacheTest extends TestCase
{
    private final static long CACHE_BUILD_TIME = 5000L;

    private AbstractAsynchronouslyRefreshedCache<String> cache;

    @Override
    protected void setUp() throws Exception
    {
        cache = new AbstractAsynchronouslyRefreshedCache<String>()
        {

            @Override
            protected String buildCache(String key)
            {
                try
                {
                    Thread.sleep(CACHE_BUILD_TIME);
                }
                catch (InterruptedException e)
                {
                }
                return key + "-" + System.currentTimeMillis();
            }
        };

        cache.setRegistry(new DefaultAsynchronouslyRefreshedCacheRegistry());
        ThreadPoolExecutorFactoryBean threadPoolfactory = new ThreadPoolExecutorFactoryBean();
        threadPoolfactory.afterPropertiesSet();
        cache.setThreadPoolExecutor((ThreadPoolExecutor) threadPoolfactory.getObject());
        cache.setBeanName("AsynchronouslyRefreshedCacheTest");
        cache.init();
    }

    /**
     * Test that initial GET will fire cache build 
     * Test that second call to get will return cached value
     */
    public void testGet()
    {
        long start = System.currentTimeMillis();
        cache.get("testGet");
        long end = System.currentTimeMillis();
        assertTrue("First get must fire cache building that should take 5sec", end - start >= CACHE_BUILD_TIME);

        start = System.currentTimeMillis();
        cache.get("testGet");
        end = System.currentTimeMillis();
        assertTrue("Second get must not fire cache building", end - start < CACHE_BUILD_TIME);
    }

    /**
     * Test that while cache is refreshing, it will return value that was cached before 
     * Test that after refresh completes, new value will be cached and returned
     */
    public void testRefresh() throws InterruptedException
    {
        String value = cache.get("testRefresh");

        long start = System.currentTimeMillis();
        cache.refresh("testRefresh");
        Thread.sleep(500);
        String valueWhileUpdate = cache.get("testRefresh");
        long end = System.currentTimeMillis();
        assertEquals("While refresh is running, cache must return value that was cached before", value, valueWhileUpdate);
        assertTrue("Get operation from cache that is refreshing must be served immediately", end - start < CACHE_BUILD_TIME);

        Thread.sleep(CACHE_BUILD_TIME);
        String valueAfterUpdate = cache.get("testRefresh");
        assertNotSame("Cached value must be changed after refresh", value, valueAfterUpdate);
    }

    /**
     * Test that GET called after REMOVE will fire cache building
     */
    public void testRemove() throws InterruptedException
    {
        cache.get("testRemove");

        long start = System.currentTimeMillis();
        cache.remove("testRemove");
        Thread.sleep(500);
        cache.get("testRemove");
        long end = System.currentTimeMillis();
        assertTrue("Get, called after remove, must fire cache building that should take 5sec", end - start >= CACHE_BUILD_TIME);
    }

}
