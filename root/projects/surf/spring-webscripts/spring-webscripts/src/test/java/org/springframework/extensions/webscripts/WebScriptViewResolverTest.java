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

import junit.framework.TestCase;

import org.springframework.extensions.webscripts.servlet.mvc.WebScriptViewResolver;
import org.springframework.web.servlet.View;

/**
 * Unit tests for Web Script view resolvers
 * 
 * @author uzquiano
 */
public class WebScriptViewResolverTest extends TestCase
{
    /**
     * Test that empty uri is handled correctly
     * 
     * @throws Exception
     */
    public void testEmptyViewURI() throws Exception
    {
    	View view = getViewResolver().resolveViewName("", null);
    	
    	// view should be null
    	assertTrue(view == null);
    }

    /**
     * Test that single slash uri is handled correctly
     * 
     * @throws Exception
     */
    public void testSlashViewURI() throws Exception
    {
    	View view = getViewResolver().resolveViewName("/", null);
    	
    	// view should be null
    	assertTrue(view == null);
    }

    /**
     * Test that bogus uri is handled correctly
     * 
     * @throws Exception
     */
    public void testBogusViewURI() throws Exception
    {    	
    	View view = getViewResolver().resolveViewName("/test/bogus", null);
    	
    	// view should not be null
    	assertTrue(view != null);
    }
    
    /**
     * Creates a new Web Script View Resolver using test server elements
     * 
     * @return WebScriptViewResolver
     */
    private WebScriptViewResolver getViewResolver()
    {
    	TestWebScriptServer server = TestWebScriptServer.getTestServer();
    	
    	WebScriptViewResolver resolver = new WebScriptViewResolver();
    	resolver.setApplicationContext(server.applicationContext);
    	resolver.setContainer(server.container);
    	resolver.onBootstrap();
    	
    	return resolver;
    }

}
