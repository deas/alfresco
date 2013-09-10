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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.alfresco.module.vti.handler.alfresco.AlfrescoListServiceHandler;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.util.GUID;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for the {@link AbstractListEndpoint} class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAbstractListEndpointTest
{
    private AbstractListEndpoint listEndpoint;
    private @Mock VtiSoapRequest soapRequest;
    
    @Before
    public void setUp()
    {
        listEndpoint = new AbstractListEndpoint(null)
        {
            @Override
            protected ListInfoBean executeListAction(VtiSoapRequest soapRequest, String dws,
                        String listName, String description, int templateID) throws Exception
            {
                return null;
            }
        };
        
        when(soapRequest.getAlfrescoContextName()).thenReturn("/alfresco");
        when(soapRequest.getRequestURI()).thenReturn("/alfresco/testSite/_vti_bin");
    }
    
    @Test
    public void usesCorrectXMLNamespacePrefix()
    {
        assertEquals("listsws", listEndpoint.prefix);
    }

    @Test
    public void testListResponseStructure()
    {
        Element xml = DocumentHelper.createElement("List");
        listEndpoint.renderListDefinition(createTestList(), "test", "/alfresco/test", xml);

        assertEquals(0, xml.elements().size());

        assertTrue(xml.attributes().size() > 0);
    }
    
    @Test
    public void testDwsFromUri()
    {
        String siteName = AbstractEndpoint.getDwsFromUri(soapRequest);
        
        assertFalse(siteName.startsWith("/"));
    }
    
    private ListInfoBean createTestList()
    {
        NodeRef listNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, GUID.generate());
        long suffix = System.currentTimeMillis();
        ListInfoBean result = new ListInfoBean(listNodeRef, "test" + suffix, AlfrescoListServiceHandler.TYPE_DOCUMENT_LIBRARY, false, null);
        return result;
    }
}
