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
import java.util.ArrayList;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.extensions.webscripts.AbstractWebScriptServerTest;
import org.springframework.extensions.webscripts.ClassPathStore;

/**
 * Unit tests for classpath store
 * 
 * @author muzquiano
 */
public class ClassPathStoreTestReadOnly extends AbstractWebScriptServerTest
{
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
		return (ClassPathStore) getTestServer().getApplicationContext().getBean("webscripts.store.test.readonly");
	}
	
	@Test
    public void testCreateDocument() throws Exception
    {
    	boolean caught = false;
    	try
    	{
    	    getClassPathStore().createDocument("file3.txt", "file3");
    	}
    	catch (IOException ioe)
    	{
    	    caught = true;
    	}
    	
    	assertTrue(caught);
    }
    
	@Test
    public void testUpdateDocument() throws Exception
    {
        boolean caught = false;
        try
        {
            getClassPathStore().createDocument("file1.txt", "file1update");
        }
        catch (IOException ioe)
        {
            caught = true;
        }
        
        assertTrue(caught);
    }

	@Test
    public void testRemoveDocument() throws Exception
    {
        boolean caught = false;
        try
        {
            getClassPathStore().removeDocument("file1.txt");
        }
        catch (IOException ioe)
        {
            caught = true;
        }
        
        assertTrue(caught);
    }
}
