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

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.tree.DefaultDocument;
import org.dom4j.tree.DefaultElement;
import org.jaxen.SimpleNamespaceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests for the {@link AbstractListItemsEndpoint} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAbstractListItemsEndpointTest
{
    // Class under test
    private AbstractListItemsEndpoint listItemsEndpoint;
    private @Mock ListServiceHandler listHandler;
    private @Mock VtiPathHelper pathHelper;
    
    @Before
    public void setUp()
    {
        listItemsEndpoint = new AbstractListItemsEndpoint(listHandler, null)
        {
            @Override
            protected DocsMetaInfo getListInfo(String siteName, ListInfoBean list, String initialUrl,
                        Date since)
            {
                return null;
            }
        };
        listItemsEndpoint.setPathHelper(pathHelper);
        listItemsEndpoint.setNamespace("http://schemas.microsoft.com/sharepoint/soap/");
    }
    
    /**
     * ALF-19833: MacOS: Could not save to SharePoint
     */
    @Test
    public void usesCorrectXMLNamespacePrefix()
    {
        assertEquals("lists", listItemsEndpoint.prefix);
    }

    @Test
    public void correctErrorCodeReturnedWhenSiteNotFound() throws Exception
    {
        VtiSoapRequest soapRequest = Mockito.mock(VtiSoapRequest.class);
        VtiSoapResponse soapResponse = new VtiSoapResponse(new MockHttpServletResponse());//Mockito.mock(VtiSoapResponse.class);
        Element requestElement = Mockito.mock(Element.class);
        SimpleNamespaceContext nc = Mockito.mock(SimpleNamespaceContext.class);
        
        when(nc.translateNamespacePrefixToUri(anyString())).thenReturn("some://uri");
        
        Element rootElement = new DefaultElement("root");
        when(soapRequest.getDocument()).thenReturn(new DefaultDocument(rootElement));
        
        // ALF-19833: listName was being passed to getList for both arguments.
        when(listHandler.getList("documentLibrary", "my-site")).thenThrow(new SiteDoesNotExistException(""));
        
        // Invoke the method under test.
        try
        {
            listItemsEndpoint.executeListActionDetails(soapRequest, soapResponse,
                        "my-site", "documentLibrary", requestElement, nc);
            
            fail("Expected exception was not thrown.");
        }
        catch(VtiSoapException e)
        {
            assertEquals(VtiError.V_LIST_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }
    }
    
    /**
     * ALF-19833: MacOS: Could not save to SharePoint
     */
    @Test
    public void canGetListUsingCorrectListAndSiteName() throws Exception
    {
        VtiSoapRequest soapRequest = Mockito.mock(VtiSoapRequest.class);
        VtiSoapResponse soapResponse = new VtiSoapResponse(new MockHttpServletResponse());//Mockito.mock(VtiSoapResponse.class);
        Element requestElement = Mockito.mock(Element.class);
        SimpleNamespaceContext nc = Mockito.mock(SimpleNamespaceContext.class);

        Element rootElement = new DefaultElement(QName.get("root", "lists", "some://uri"));
        when(soapRequest.getDocument()).thenReturn(new DefaultDocument(rootElement));
        
        // Invoke the method under test.
        listItemsEndpoint.executeListActionDetails(soapRequest, soapResponse,
                    "my-site", "documentLibrary", requestElement, nc);
        
        // Check the condition this test is for.
        verify(listHandler).getList("documentLibrary", "my-site");
    }
}
