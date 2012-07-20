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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.cache.MemoryCache;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.model.filefolder.FileInfoImpl;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import static org.mockito.Matchers.anyString;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for the VtiPathHelper class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class VtiPathHelperTest
{
    private final static String ALFRESCO_CONTEXT = "/alfresco";
    private final static NodeRef ROOT_NODE_REF = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "dummy");
    private @Mock DictionaryService dict;
    private @Mock FileFolderService fileFolderService;
    private @Mock NodeService nodeService;
    private VtiPathHelper pathHelper;
    private SimpleCache<String, NodeRef> rootNodeCache;
    
    @Before
    public void setUp()
    {
        pathHelper = new VtiPathHelper();
        pathHelper.setDictionaryService(dict);
        pathHelper.setFileFolderService(fileFolderService);
        pathHelper.setNodeService(nodeService);
        rootNodeCache = new MemoryCache<String, NodeRef>();
        rootNodeCache.put("key.vtiRoot.noderef", ROOT_NODE_REF);
        pathHelper.setSingletonCache(rootNodeCache);
    }
    
    @Test
    public void canDoDecomposeURLWork() throws FileNotFoundException
    {
        NodeRef siteNodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "mysite-id");
        FileInfo siteFileInfo = makeFileInfo(siteNodeRef, SiteModel.TYPE_SITE);
        when(fileFolderService.resolveNamePath(ROOT_NODE_REF, Arrays.asList("mysite"))).thenReturn(siteFileInfo);
        when(nodeService.getType(siteNodeRef)).thenReturn(SiteModel.TYPE_SITE);
        when(dict.isSubClass(SiteModel.TYPE_SITE, SiteModel.TYPE_SITE)).thenReturn(true);
        
        // Decompose a complete URI path, including context, site name and document path.
        String[] parts = pathHelper.doDecomposeURLWork(
                    ALFRESCO_CONTEXT,
                    "/alfresco/mysite/documentLibrary/folder1/file1.txt",
                    SiteModel.TYPE_SITE);
        assertDecomposedURL("/alfresco/mysite", "documentLibrary/folder1/file1.txt", parts);

        // Decompose a URI path containing the context path only.
        parts = pathHelper.doDecomposeURLWork(ALFRESCO_CONTEXT, "/alfresco", SiteModel.TYPE_SITE);
        assertDecomposedURL("/alfresco", "", parts);

        // Decompose an empty URI path.
        parts = pathHelper.doDecomposeURLWork(ALFRESCO_CONTEXT, "", SiteModel.TYPE_SITE);
        assertDecomposedURL("", "", parts);
    }

    protected void assertDecomposedURL(String site, String doc, String[] parts)
    {
        assertEquals(2, parts.length);
        assertEquals(site, parts[0]);
        assertEquals(doc, parts[1]);
    }
    
    /**
     * Create a stub FileInfo object containing only the required information.
     */
    private FileInfo makeFileInfo(final NodeRef nodeRef, final QName type)
    {
        return new FileInfo()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public NodeRef getNodeRef()
            {
                return nodeRef;
            }

            @Override
            public boolean isFolder()
            {
                return false;
            }

            @Override
            public boolean isLink()
            {
                return false;
            }

            @Override
            public boolean isHidden()
            {
                return false;
            }

            @Override
            public NodeRef getLinkNodeRef()
            {
                return null;
            }

            @Override
            public String getName()
            {
                return null;
            }

            @Override
            public Date getCreatedDate()
            {
                return null;
            }

            @Override
            public Date getModifiedDate()
            {
                return null;
            }

            @Override
            public ContentData getContentData()
            {
                return null;
            }

            @Override
            public Map<QName, Serializable> getProperties()
            {
                return null;
            }

            @Override
            public QName getType()
            {
                return type;
            }
            
        };
    }

}
