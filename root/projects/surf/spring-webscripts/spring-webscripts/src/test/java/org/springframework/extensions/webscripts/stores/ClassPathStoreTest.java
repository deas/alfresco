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

package org.springframework.extensions.webscripts.stores;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.extensions.webscripts.AbstractWebScriptServerTest;
import org.springframework.extensions.webscripts.ClassPathStore;
import org.springframework.extensions.webscripts.ScriptContent;
import org.springframework.extensions.webscripts.ScriptLoader;
import org.springframework.util.FileCopyUtils;

import freemarker.cache.TemplateLoader;

/**
 * Unit tests for classpath store
 * 
 * @author muzquiano
 */
public class ClassPathStoreTest extends AbstractWebScriptServerTest
{
    public static int TOTAL_DOCUMENT_COUNT = 10;
    public static int DESC_XML_DOCUMENT_COUNT = 2;
    
	public void setUp() throws ServletException
	{
		super.setUp();
		
		// manually init our classpath store
		getClassPathStore().init();
	}
	
	public ArrayList<String> getConfigLocations()
	{
		ArrayList<String> list = super.getConfigLocations();
		
		list.add("classpath:org/springframework/extensions/webscripts/stores/spring-webscripts-stores-context.xml");
		
		return list;
	}

	public ClassPathStore getClassPathStore()
	{
		return (ClassPathStore) getTestServer().getApplicationContext().getBean("webscripts.store.test");
	}
	
	@Test
    public void testAllDocumentPathsCount() throws Exception
    {
    	String[] allPaths = getClassPathStore().getAllDocumentPaths();
    	assertEquals(TOTAL_DOCUMENT_COUNT, allPaths.length);
    }

	@Test
    public void testDescriptionDocumentPathsCount() throws Exception
    {
    	String[] descPaths = getClassPathStore().getDescriptionDocumentPaths();
    	assertEquals(DESC_XML_DOCUMENT_COUNT, descPaths.length);
    }
    
	@Test
    public void testGetDocument() throws Exception
    {
    	InputStream is = getClassPathStore().getDocument("file1.txt");
    	byte[] array = FileCopyUtils.copyToByteArray(is);
    	assertEquals(5, array.length);
    }

	@Test
    public void testGetDocumentPaths() throws Exception
    {
    	String[] paths = getClassPathStore().getDocumentPaths("/", false, "*.txt");
    	assertEquals(2, paths.length);
    }

	@Test
    public void testGetDocumentSubPaths() throws Exception
    {
    	String[] paths = getClassPathStore().getDocumentPaths("/", true, "*.txt");
    	assertEquals(6, paths.length);
    }

	@Test
    public void testCreateDocument() throws Exception
    {
    	String[] allPaths1 = getClassPathStore().getAllDocumentPaths();

    	boolean threw = false;
    	try
    	{
    	    getClassPathStore().createDocument("file3.txt", "file3");
    	}
    	catch(IOException ioe)
    	{
    	    threw = true;
    	}
    	
    	assertTrue(threw);
    	
        String[] allPath2 = getClassPathStore().getAllDocumentPaths();
        assertEquals(allPaths1.length, allPath2.length);
    }

	@Test
    public void testRemoveDocument() throws Exception
    {
    	String[] allPaths1 = getClassPathStore().getAllDocumentPaths();
    	
        boolean threw = false;
        try
        {
            getClassPathStore().removeDocument("file1.txt");
        }
        catch(IOException ioe)
        {
            threw = true;
        }
        
        assertTrue(threw);
        
        String[] allPath2 = getClassPathStore().getAllDocumentPaths();
        assertEquals(allPaths1.length, allPath2.length);
    }

    @Test
    public void testUpdateDocument() throws Exception
    {
        boolean threw = false;
        try
        {
            getClassPathStore().updateDocument("file1.txt", "an update");
        }
        catch(IOException ioe)
        {
            threw = true;
        }
        
        assertTrue(threw);        
    }

	@Test
    public void testHasDocument() throws Exception
    {
    	boolean b1 = getClassPathStore().hasDocument("file7.txt");
    	assertEquals(false, b1);
    	
        boolean b2 = getClassPathStore().hasDocument("file1.txt");
        assertEquals(true, b2);
    }
    	
    @Test
    public void testTemplateLoader() throws Exception
    {
        TemplateLoader loader = getClassPathStore().getTemplateLoader();
        
        Object templateSource = loader.findTemplateSource("folder2/template.ftl");
        assertNotNull(templateSource);
        
        Reader reader = loader.getReader(templateSource, null);
        String text = FileCopyUtils.copyToString(reader);
        
        assertEquals("<html></html>", text);
    }

    @Test
    public void testScriptLoader() throws Exception
    {
        ScriptLoader loader = getClassPathStore().getScriptLoader();
        
        ScriptContent script = loader.getScript("folder2/script.js");
        assertNotNull(script);
        
        Reader reader = script.getReader();
        String text = FileCopyUtils.copyToString(reader);
        
        assertEquals("var test = 1;", text);
    }
    
}
