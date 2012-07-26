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

import org.alfresco.repo.cache.MemoryCache;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Base class for VtiPathHelper tests. The VtiPathHelper class is expected to be
 * extended, therefore the tests will be too.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractVtiPathHelperTestBase<T extends VtiPathHelper>
{
    protected final static String ALFRESCO_CONTEXT = "/alfresco";
    protected final static NodeRef ROOT_NODE_REF = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "dummy");
    protected @Mock DictionaryService dict;
    protected @Mock FileFolderService fileFolderService;
    protected @Mock NodeService nodeService;
    protected SimpleCache<String, NodeRef> rootNodeCache;
    protected T pathHelper;
    
    /**
     * Create the class under test.
     */
    protected abstract T createVtiHelper();
    
    @Before
    public void setUp()
    {
        pathHelper = createVtiHelper();
        pathHelper.setDictionaryService(dict);
        pathHelper.setFileFolderService(fileFolderService);
        pathHelper.setNodeService(nodeService);
        rootNodeCache = new MemoryCache<String, NodeRef>();
        rootNodeCache.put("key.vtiRoot.noderef", ROOT_NODE_REF);
        pathHelper.setSingletonCache(rootNodeCache);
        
        pathHelper.setUrlPathPrefix("/alfresco");
    }
}
