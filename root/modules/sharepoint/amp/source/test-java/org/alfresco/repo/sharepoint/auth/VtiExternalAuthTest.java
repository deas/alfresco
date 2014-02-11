/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.repo.sharepoint.auth;

import java.io.IOException;

import org.alfresco.repo.management.subsystems.AbstractChainedSubsystemTest;
import org.alfresco.repo.management.subsystems.ChildApplicationContextFactory;
import org.alfresco.repo.management.subsystems.DefaultChildApplicationContextManager;
import org.alfresco.util.ApplicationContextHelper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.context.ApplicationContext;

public class VtiExternalAuthTest extends AbstractChainedSubsystemTest
{
    private ApplicationContext ctx = ApplicationContextHelper.getApplicationContext(new String[] { "classpath:alfresco/application-context.xml", "classpath:alfresco/remote-api-context.xml", "classpath:alfresco/web-scripts-application-context.xml" });
    
    private ChildApplicationContextFactory childApplicationContextFactory; 
    private DefaultChildApplicationContextManager childApplicationContextManager;

    @Override
    protected void setUp() throws Exception
    {
        childApplicationContextManager = (DefaultChildApplicationContextManager) ctx.getBean("Authentication");
        childApplicationContextManager.stop();
        childApplicationContextManager.setProperty("chain", "external1:external");
        childApplicationContextFactory = getChildApplicationContextFactory(childApplicationContextManager, "external1");
    }

    public void testExternalAuth() throws HttpException, IOException
    {
        childApplicationContextFactory.stop();
        childApplicationContextFactory.setProperty("external.authentication.proxyUserName", "");
        
        String loginURL = "http://localhost:7070/alfresco/";
        HttpClient client = new HttpClient();
        HttpMethod getReq = new GetMethod(loginURL);
        getReq.addRequestHeader("X-Alfresco-Remote-User", "admin");
        int statusCode = client.executeMethod(getReq);
        assertEquals("sharepoint module doesn't respect external auth", 200, statusCode);
    }
    
    public void testExternalAuthNegative() throws HttpException, IOException
    {
        childApplicationContextManager = (DefaultChildApplicationContextManager) ctx.getBean("Authentication");
        childApplicationContextManager.stop();
        childApplicationContextManager.setProperty("chain", "alfrescoNtlm1:alfrescoNtlm");
        childApplicationContextFactory = getChildApplicationContextFactory(childApplicationContextManager, "alfrescoNtlm1");
        
        String loginURL = "http://localhost:7070/alfresco/";
        HttpClient client = new HttpClient();
        HttpMethod getReq = new GetMethod(loginURL);
        getReq.addRequestHeader("X-Alfresco-Remote-User", "admin");
        int statusCode = client.executeMethod(getReq);
        assertEquals("sharepoint module should reject external auth if there is no external authentication in chain", 401, statusCode);
    }    
}
