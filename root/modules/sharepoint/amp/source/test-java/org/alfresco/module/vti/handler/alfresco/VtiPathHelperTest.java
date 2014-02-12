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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for the VtiPathHelper class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class VtiPathHelperTest extends AbstractVtiPathHelperTestBase<VtiPathHelper>
{
    protected VtiPathHelper createVtiHelper()
    {
        return new VtiPathHelper();
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

        // Decompose a URI path containing the context path with a slash in the end.
        // See MNT-10128
        parts = pathHelper.doDecomposeURLWork(ALFRESCO_CONTEXT, "/alfresco/", SiteModel.TYPE_SITE);
        assertDecomposedURL("/alfresco", "", parts);

        // Decompose an empty URI path.
        parts = pathHelper.doDecomposeURLWork(ALFRESCO_CONTEXT, "", SiteModel.TYPE_SITE);
        assertDecomposedURL("", "", parts);
        
        // Decompose the slash URI path.
        parts = pathHelper.doDecomposeURLWork(ALFRESCO_CONTEXT, "/", SiteModel.TYPE_SITE);
        assertDecomposedURL("", "", parts);
        
        // Detect bad URLs that have the incorrect prefix.
        try
        {
            parts = pathHelper.doDecomposeURLWork(ALFRESCO_CONTEXT, "/wrong-prefix/mysite", SiteModel.TYPE_SITE);
            throw new RuntimeException("Shouldn't have got here.");
        }
        catch (VtiHandlerException e)
        {
            // Got here, good.
            assertEquals(VtiHandlerException.BAD_URL, e.getError());
        }
        
    }

    @Test
    public void canStripPathPrefix()
    {
        assertEquals("path/to/file.txt", pathHelper.stripPathPrefix("/alfresco", "/alfresco/path/to/file.txt"));
        assertEquals("path/to/file.txt", pathHelper.stripPathPrefix("/", "/path/to/file.txt"));
        assertEquals("/path/to/file.txt", pathHelper.stripPathPrefix("", "/path/to/file.txt"));
        assertEquals("", pathHelper.stripPathPrefix("", ""));
                
        // If the path doesn't start with the prefix, it's left as-is
        assertEquals("/path/to/file.txt", pathHelper.stripPathPrefix("/prefix", "/path/to/file.txt"));
        assertEquals("", pathHelper.stripPathPrefix("/alfresco", ""));
    }

    @Test
    public void canGetPathForURL()
    {
        pathHelper.setUrlPathPrefix("/prefix");
        
        assertEquals("/mysite/documentLibrary/path/to/file.txt",
                    pathHelper.getPathForURL("/prefix/mysite/documentLibrary/path/to/file.txt"));
    }
}
