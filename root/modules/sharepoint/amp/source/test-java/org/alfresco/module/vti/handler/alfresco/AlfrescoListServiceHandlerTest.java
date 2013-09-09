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
package org.alfresco.module.vti.handler.alfresco;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for the {@link AlfrescoListServiceHandler}.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class AlfrescoListServiceHandlerTest
{
    // The class under test.
    private AlfrescoListServiceHandler handler;
    private @Mock SiteService siteService;
    private @Mock NodeService nodeService;
    private @Mock SiteInfo siteInfo;
    private @Mock NamespaceService namespaceService;
    private @Mock DictionaryService dictionaryService;
    private @Mock FileFolderService fileFolderService;
    private NodeRef listNodeRef;
    
    @Before
    public void setUp() throws Exception
    {
        handler = new AlfrescoListServiceHandler();
        handler.setSiteService(siteService);
        handler.setNodeService(nodeService);
        handler.setNamespaceService(namespaceService);
        handler.setDictionaryService(dictionaryService);
        handler.setFileFolderService(fileFolderService);
        
        when(namespaceService.getNamespaceURI(anyString())).thenReturn("some://uri");
        Map<Integer, String> dataListTypes = new HashMap<Integer, String>();
        dataListTypes.put(105, "dl:contact");
        dataListTypes.put(107, "dl:task");
        dataListTypes.put(1100, "dl:issue");
        dataListTypes.put(5001, "dl:todoList");
        dataListTypes.put(5002, "dl:simpletask");
        dataListTypes.put(5003, "dl:event");
        dataListTypes.put(5004, "dl:location");
        dataListTypes.put(5005, "dl:meetingAgenda");
        dataListTypes.put(5006, "dl:eventAgenda");
        handler.setDataListTypes(dataListTypes);
        
        handler.afterPropertiesSet();
        
        // Note UUID is lower-case version of that passed to AlfrescoListServiceHandler.getList(...)
        listNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
                    "12451c35-bbe3-4589-91d6-e704b00aca66");
        
        primeMocks();
    }
    
    private void primeMocks()
    {
        when(siteService.getSite("marketing-site")).thenReturn(siteInfo);
        NodeRef siteNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "node-id");
        when(siteInfo.getNodeRef()).thenReturn(siteNodeRef);
        when(siteInfo.getShortName()).thenReturn("marketing");
        when(siteInfo.getTitle()).thenReturn("marketing-site");
        
        when(nodeService.exists(listNodeRef)).thenReturn(true);
        
        ChildAssociationRef parentAssoc = new ChildAssociationRef(null, siteNodeRef, null, listNodeRef);
        when(nodeService.getPrimaryParent(listNodeRef)).thenReturn(parentAssoc);

        when(nodeService.getProperty(listNodeRef, ContentModel.PROP_NAME)).thenReturn("documentLibrary");
        
        @SuppressWarnings("unchecked")
        PagingResults<FileInfo> paging = (PagingResults<FileInfo>) mock(PagingResults.class);
        when(paging.getTotalResultCount()).thenReturn(new Pair<Integer, Integer>(1, 1));
        when(fileFolderService.list(eq(listNodeRef), eq(true), eq(false),
                                    eq((Set<QName>)null), eq((List<Pair<QName, Boolean>>)null),
                                    any(PagingRequest.class))).thenReturn(paging);
    }
    
    @Test
    public void canGetListWithListNameAsUUID() throws SiteDoesNotExistException, FileNotFoundException
    {
        // Invoke the method under test
        handler.getList("12451C35-BBE3-4589-91D6-E704B00ACA66", "marketing-site");
    }
    
    @Test
    public void canGetListWithListNameAsBracedUUID() throws SiteDoesNotExistException, FileNotFoundException
    {        
        // Invoke the method under test
        handler.getList("{12451C35-BBE3-4589-91D6-E704B00ACA66}", "marketing-site");
    }
    
    @Test
    public void canGetListWithListNameAsProperName() throws SiteDoesNotExistException, FileNotFoundException
    {
        when(siteService.getContainer("marketing", "documentLibrary")).thenReturn(listNodeRef);
        
        // Invoke the method under test
        handler.getList("documentLibrary", "marketing-site");
    }
}
