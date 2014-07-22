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

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.extensions.webscripts.TestWebScriptServer.Request;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * @author muzquiano
 */
@Ignore
public class AbstractWebScriptServerTest extends TestCase
{
	protected TestWebScriptServer server = null;
	
	public void setUp() throws ServletException
	{
		ArrayList<String> configLocations = getConfigLocations();
		String[] configLocationsArray = configLocations.toArray(new String[configLocations.size()]);
		
        ApplicationContext context = new ClassPathXmlApplicationContext(configLocationsArray);
        server = (TestWebScriptServer) context.getBean("webscripts.test");
	}
	
	public void tearDown() throws ServletException
	{
	}
	
	public ArrayList<String> getConfigLocations()
	{
		ArrayList<String> list = new ArrayList<String>();
		
		list.add("classpath:org/springframework/extensions/webscripts/spring-webscripts-application-context.xml");
		list.add("classpath:org/springframework/extensions/webscripts/test/spring-webscripts-server-test-context.xml");
		
	    return list;
	}
	
    public TestWebScriptServer getTestServer()
    {
    	return server;
    }
    
    /**
     * @param req
     * @param expectedStatus
     * @param expectedResponse
     * @return
     * @throws IOException
     */
    protected Response sendRequest(Request req, int expectedStatus, String expectedResponse) throws IOException
    {
        System.out.println();
        System.out.println("* Request: " + req.getMethod() + " " + req.getFullUri() + (req.getBody() == null ? "" : "\n" + req.getBody()));

        Response res = getTestServer().submitRequest(req);

        System.out.println();
        System.out.println("* Response: " + res.getStatus() + " " + req.getMethod() + " " + req.getFullUri() + "\n" + res.getContentAsString());
        if (expectedStatus > 0)
        {
            assertEquals("Unexpected status code", expectedStatus, res.getStatus());
        }
        if (expectedResponse != null)
        {
            assertEquals("Unexpected response", expectedResponse, res.getContentAsString());
        }
        return res;
    }
}
