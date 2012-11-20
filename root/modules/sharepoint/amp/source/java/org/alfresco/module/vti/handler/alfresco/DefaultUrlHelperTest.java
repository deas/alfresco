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
    private DefaultUrlHelper helper;
    
    @Before
    public void setUp() throws Exception
    {
        helper = new DefaultUrlHelper();
        helper.setExternalProtocol("https");
        helper.setExternalHost("sp.example.com");
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
}
