package org.alfresco.repo.cache;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public abstract class SimpleCacheTestBase<T extends SimpleCache<Integer, String>>
{
    protected T cache;
    
    @Before
    public void setUp() throws Exception
    {
        cache = createCache();
    }

    protected abstract T createCache();

    @Test
    public void canStoreNullValues()
    {
        cache.put(2, null);
        assertEquals(null, cache.get(2));
        // Check that the key has an entry against it.
        assertTrue(cache.contains(2));
        
        // Ensure that a key that has not been assigned is discernable
        // from a key that has been assigned a null value.
        assertEquals(null, cache.get(4));
        assertFalse(cache.contains(4));
    }
    
    @Test
    public void canRemoveItems()
    {
        cache.put(1, "hello");
        cache.put(2, "world");
        assertEquals("hello", cache.get(1));
        assertEquals("world", cache.get(2));
        
        cache.remove(2);
        assertEquals("hello", cache.get(1));
        assertEquals(null, cache.get(2));        
        assertEquals(false, cache.contains(2));        
    }
    
    @Test
    public void canClearItems()
    {
        cache.put(1, "hello");
        cache.put(2, "world");
        assertEquals("hello", cache.get(1));
        assertEquals("world", cache.get(2));
        
        cache.clear();

        assertEquals(null, cache.get(1));        
        assertEquals(false, cache.contains(1));
        assertEquals(null, cache.get(2));        
        assertEquals(false, cache.contains(2));
    }
    
    @Test
    public void canGetKeys()
    {
        cache.put(3, "blue");
        cache.put(12, "red");
        cache.put(43, "olive");
        
        List<Integer> keys = new ArrayList<Integer>(cache.getKeys());
        Collections.sort(keys);
        
        Iterator<Integer> it = keys.iterator();
        assertEquals(3, it.next().intValue());
        assertEquals(12, it.next().intValue());
        assertEquals(43, it.next().intValue());
        assertFalse("There should be no more keys.", it.hasNext());
    }
    
    @Test
    public void noConcurrentModificationException()
    {
        cache.put(1, "1");
        cache.put(2, "2");
        cache.put(3, "3");
        cache.put(4, "4");

        Iterator<Integer> i = cache.getKeys().iterator();
        i.next();
        i.next();
        
        cache.put(5, "5");
        
        // Causes a ConcurrentModificationException with a java.util.LinkedHashMap
        i.next();
    }

}
