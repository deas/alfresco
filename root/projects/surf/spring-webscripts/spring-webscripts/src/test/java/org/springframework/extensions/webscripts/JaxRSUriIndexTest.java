/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.springframework.extensions.webscripts.JaxRSUriIndex;
import org.springframework.extensions.webscripts.UriTemplate;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.JaxRSUriIndex.IndexEntry;


/**
 * Test Jax-RS Uri Template
 * 
 * @author davidc
 */
public class JaxRSUriIndexTest extends TestCase
{

    public void testInvalidTemplate()
    {
        try
        {
            new UriTemplate(null);
            fail("Failed to catch null template");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("");
            fail("Failed to catch empty template");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("//");
            fail("Failed to catch double /");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("/a//");
            fail("Failed to catch double /");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("/a//b");
            fail("Failed to catch double /");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("/{1a}");
            fail("Failed to catch var name beginning with number");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("/;a");
            fail("Failed to catch semi-colon without prefix");
        }
        catch(WebScriptException e) {};
        try
        {
            new UriTemplate("/{1;a}");
            fail("Failed to catch semi-colon in template var name");
        }
        catch(WebScriptException e) {};
    }

    
    public void testValidTemplate()
    {
        try
        {
            new UriTemplate("/");
        }
        catch(WebScriptException e)
        {
            fail("Root path is valid");
        };
        try
        {
            new UriTemplate("/a;");
        }
        catch(WebScriptException e)
        {
            fail("Semi-colon in path is valid");
        };
        try
        {
            new UriTemplate("/a;aaaa");
        }
        catch(WebScriptException e)
        {
            fail("Semi-colon in path is valid");
        };
        try
        {
            new UriTemplate("/a;a;aaaa");
        }
        catch(WebScriptException e)
        {
            fail("Semi-colon in path is valid");
        };
        try
        {
            new UriTemplate("/{a_b}");
        }
        catch(WebScriptException e)
        {
            fail("Underscore in token name is valid");
        };
        try
        {
            new UriTemplate("/ads/test-2/{storeid}/{path}");   
        }
        catch(WebScriptException e)
        {
            fail("Hypthen in path is valid");
        };
        try
        {
            new UriTemplate("/ads/test_2/{storeid}/{path}");   
        }
        catch(WebScriptException e)
        {
            fail("Hypthen in path is valid");
        };
    }

    
    public void testParseTemplate()
    {
        UriTemplate i1 = new UriTemplate("/");
        assertEquals("/", i1.getTemplate());
        assertEquals("/", i1.getRegex().pattern());
        assertEquals(1, i1.getStaticCharCount());
        assertEquals(0, i1.getVariableNames().length);

        UriTemplate i2 = new UriTemplate("/a/{a1}/b{b1}b/{c_c}");
        assertEquals("/a/{a1}/b{b1}b/{c_c}", i2.getTemplate());
        assertEquals("/a/(.*?)/b(.*?)b/(.*?)", i2.getRegex().pattern());
        assertEquals(7, i2.getStaticCharCount());
        assertEquals(3, i2.getVariableNames().length);
        assertEquals("a1", i2.getVariableNames()[0]);
        assertEquals("b1", i2.getVariableNames()[1]);
        assertEquals("c_c", i2.getVariableNames()[2]);
    }
 

    public void testTemplateMatch()
    {
        UriTemplate i1 = new UriTemplate("/a/{a1}/b/b{b1}b");
        assertNull(i1.match("/"));
        assertNull(i1.match("/a"));
        assertNull(i1.match("/a/1/b"));
        assertNull(i1.match("/a/1/b/2"));
        assertNull(i1.match("/a/1/b/b2"));
        assertNull(i1.match("/a/1/b/b2b/"));
        
        Map<String, String> values1 = i1.match("/a/1/b/b2b");
        assertNotNull(values1);
        assertEquals(2, values1.size());
        assertEquals("1", values1.get("a1"));
        assertEquals("2", values1.get("b1"));

        UriTemplate i2 = new UriTemplate("/a/{a1}/b/{b1}");
        Map<String, String> values2 = i2.match("/a/1/b/2/3");
        assertNotNull(values2);
        assertEquals(2, values2.size());
        assertEquals("1", values2.get("a1"));
        assertEquals("2/3", values2.get("b1"));

        UriTemplate i3 = new UriTemplate("/a/{a1}/b/{a1}");
        Map<String, String> values3 = i3.match("/a/1/b/2");
        assertNull(values3);
        
        UriTemplate i4 = new UriTemplate("/a/b{b}/{c}");
        Map<String, String> values4 = i4.match("/a/b/c");
        assertEquals(2, values4.size());
        assertEquals("", values4.get("b"));
        assertEquals("c", values4.get("c"));

        UriTemplate i5 = new UriTemplate("/a/b{b}/{c}");
        Map<String, String> values5 = i5.match("/a/bb/c");
        assertEquals(2, values5.size());
        assertEquals("b", values5.get("b"));
        assertEquals("c", values5.get("c"));        
    }
    
    
    public void testTemplateDotMatch()
    {
        UriTemplate i1 = new UriTemplate("/a/b/{b}/c");
        Map<String, String> values1 = i1.match("/a/b/x/y/z.ext/c");
        assertEquals(1, values1.size());
        assertEquals("x/y/z.ext", values1.get("b"));
    }
    
    
    public void testIndexSort()
    {
        IndexEntry i1 = new IndexEntry("GET", new UriTemplate("/"), false, null);
        IndexEntry i2 = new IndexEntry("POST", new UriTemplate("/a/{a}"), false, null);
        IndexEntry i3 = new IndexEntry("get", new UriTemplate("/a/{a}/b"), false, null);
        IndexEntry i4 = new IndexEntry("get", new UriTemplate("/a"), false, null);
        IndexEntry i5 = new IndexEntry("get", new UriTemplate("/c/d"), false, null);
        IndexEntry i6 = new IndexEntry("get", new UriTemplate("/c/d/{e}"), true, null);
        IndexEntry i7 = new IndexEntry("get", new UriTemplate("/a/b"), false, null);
        IndexEntry i8 = new IndexEntry("get", new UriTemplate("/c/d/{e}/{e}"), false, null);
        IndexEntry i9 = new IndexEntry("get", new UriTemplate("/e"), false, null);
        IndexEntry i10 = new IndexEntry("GET", new UriTemplate("/a/{a}"), false, null);

        Set<IndexEntry> index = new TreeSet<IndexEntry>(JaxRSUriIndex.COMPARATOR);
        index.add(i1);
        index.add(i2);
        index.add(i3);
        index.add(i4);
        index.add(i5);
        index.add(i6);
        index.add(i7);
        index.add(i8);
        index.add(i9);
        index.add(i10);

        IndexEntry[] sorted = new IndexEntry[index.size()];
        index.toArray(sorted);
        assertEquals(i1, sorted[9]);
        assertEquals(i4, sorted[8]);
        assertEquals(i9, sorted[7]);
        assertEquals(i10, sorted[6]);
        assertEquals(i2, sorted[5]);
        assertEquals(i7, sorted[4]);
        assertEquals(i5, sorted[3]);
        assertEquals(i3, sorted[2]);
        assertEquals(i6, sorted[1]);
        assertEquals(i8, sorted[0]);
    }
    
}
