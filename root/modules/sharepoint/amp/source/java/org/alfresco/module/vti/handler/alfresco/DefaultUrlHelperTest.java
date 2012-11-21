/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.vti.handler.alfresco;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link DefaultUrlHelper} class.
 * 
 * @author Matt Ward
 */
public class DefaultUrlHelperTest
{
    private static final String HOST = "sp.example.com";
    private DefaultUrlHelper helper;
    private LocalHostNameProvider hostNameProvider;
    
    @Before
    public void setUp() throws Exception
    {
        hostNameProvider = new HostNameProviderStub();
        helper = new DefaultUrlHelper();
        helper.setLocalHostNameProvider(hostNameProvider);
        helper.setExternalProtocol("https");
        helper.setExternalHost(HOST);
        helper.setExternalPort(1234);
        helper.setExternalContextPath("/theContextPath");
        helper.afterPropertiesSet();
        
    }
    
    @Test
    public void canGetExternalBaseURL()
    {
        assertEquals("https://sp.example.com:1234/theContextPath", helper.getExternalBaseURL());
    }

    @Test
    public void canGetExternalBaseURLWithRootContextPath() throws Exception
    {
        helper.setExternalContextPath("/");
        helper.afterPropertiesSet();
        assertEquals("https://sp.example.com:1234", helper.getExternalBaseURL());
    }

    @Test
    public void canGetExternalURLHostOnly()
    {
        assertEquals("https://sp.example.com:1234", helper.getExternalURLHostOnly());
    }

    @Test
    public void canGetExternalURL()
    {
        assertEquals("https://sp.example.com:1234/theContextPath", helper.getExternalURL(null));        
        assertEquals("https://sp.example.com:1234/theContextPath", helper.getExternalURL(""));        
        assertEquals("https://sp.example.com:1234/theContextPath/a", helper.getExternalURL("a"));        
        assertEquals("https://sp.example.com:1234/theContextPath/a/b", helper.getExternalURL("a/b"));        
    }
    
    @Test
    public void canGetExternalURLWithRootContextPath() throws Exception
    {
        helper.setExternalContextPath("/");
        helper.afterPropertiesSet();
        assertEquals("https://sp.example.com:1234", helper.getExternalURL(null));        
        assertEquals("https://sp.example.com:1234", helper.getExternalURL(""));        
        assertEquals("https://sp.example.com:1234/a", helper.getExternalURL("a"));        
        assertEquals("https://sp.example.com:1234/a/b", helper.getExternalURL("a/b"));
    }
    
    
    private static class HostNameProviderStub implements LocalHostNameProvider
    {
        @Override
        public String getLocalName()
        {
            return HOST;
        }

        @Override
        public String subsituteHost(String hostName)
        {
            // Return whatever was passed in as-is.
            return hostName;
        }
    }
}
