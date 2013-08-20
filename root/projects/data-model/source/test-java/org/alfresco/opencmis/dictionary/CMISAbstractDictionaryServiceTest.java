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
package org.alfresco.opencmis.dictionary;

import static org.junit.Assert.*;

import org.alfresco.opencmis.dictionary.CMISAbstractDictionaryService.DictionaryRegistry;
import org.alfresco.repo.cache.MemoryCache;
import org.alfresco.repo.cache.SimpleCache;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link CMISAbstractDictionaryServiceTest} class.
 * 
 * @author Matt Ward
 */
public class CMISAbstractDictionaryServiceTest
{
    private static final String CACHE_KEY = "cache_key";
    // Class under test
    private CMISAbstractDictionaryService dictService;
    private SimpleCache<String, DictionaryRegistry> cache;
    private boolean initCalled;
    private DictionaryRegistry dictRegistry;
    
    @Before
    public void setUp() throws Exception
    {
        dictService = new CMISAbstractDictionaryService()
        {
            @Override
            protected void init()
            {
                initCalled = true;
                key_opencmis_dictionary_registry = CACHE_KEY;
                cache.put("cache_key", dictRegistry);
            }

            @Override
            protected void createDefinitions(DictionaryRegistry registry)
            {
            }
        };
        
        dictRegistry = dictService.new DictionaryRegistry();
        cache = new MemoryCache<String, CMISAbstractDictionaryService.DictionaryRegistry>();
        dictService.setSingletonCache(cache);
        initCalled = false;
    }

    @Test
    public void canGetRegistryWhenInitNotYetCalled()
    {
        // Pre-conditions of test
        dictService.key_opencmis_dictionary_registry = null;
        assertNull(dictService.key_opencmis_dictionary_registry);
        assertFalse(initCalled);
        
        DictionaryRegistry registry = dictService.getRegistry();
        
        assertTrue("init() should have been called.", initCalled);
        assertSame(dictRegistry, registry);
    }
    
    @Test
    public void canGetRegistryWhenInitAlreadyCalled()
    {
        // Pre-conditions of test
        dictService.init();
        assertNotNull(dictService.key_opencmis_dictionary_registry);
        assertTrue(initCalled);
        
        // Perform test
        DictionaryRegistry registry = dictService.getRegistry();
        
        assertTrue("init() should have been called.", initCalled);
        assertSame(dictRegistry, registry);
    }
}
