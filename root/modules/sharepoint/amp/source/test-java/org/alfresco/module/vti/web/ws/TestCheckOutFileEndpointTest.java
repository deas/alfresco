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
package org.alfresco.module.vti.web.ws;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for the {@link CheckOutFileEndpoint} class.
 * 
 * @author pavel.yurkevich
 */
@RunWith(MockitoJUnitRunner.class)
public class TestCheckOutFileEndpointTest
{
    private @Mock VtiSoapRequest office2007Request;
    private @Mock VtiSoapRequest office2010Request;
    private @Mock VtiSoapRequest office2011Request;
    
    @Before
    public void setUp()
    {
        when(office2007Request.getHeader(VtiUtils.HEADER_USER_AGENT)).thenReturn("Microsoft Office/12.0 (Windows NT 6.1; Microsoft Office Word 12.0.4518; Pro)");
        when(office2010Request.getHeader(VtiUtils.HEADER_USER_AGENT)).thenReturn("Microsoft Office/14.0 (Windows NT 6.1; Microsoft Word 14.0.6129; Pro)");
        when(office2011Request.getHeader(VtiUtils.HEADER_USER_AGENT)).thenReturn("Microsoft Office/14.3.6 (Macintosh 10.8.2, Microsoft Document Connection 14.3.6; Pro)");
    }
    
    @Test
    public void testUserAgentsAreCorrectlyIdentified()
    {
        // Office 2007 (version 12.x.xxxx should not match any)
        assertFalse(VtiUtils.isMacClientRequest(office2007Request) || VtiUtils.isOffice2010ClientRequest(office2007Request));
        
        // Office 2010 (version 14.x.xxxx should not be determined as request from Mac client)
        assertTrue(VtiUtils.isMacClientRequest(office2010Request) || VtiUtils.isOffice2010ClientRequest(office2010Request));
        
        // Office 2011 for Mac (version 14.x.xxxx could also be assumed as Office 2010 as it have same major version)
        assertTrue(VtiUtils.isMacClientRequest(office2011Request) || VtiUtils.isOffice2010ClientRequest(office2011Request));
    }
}
