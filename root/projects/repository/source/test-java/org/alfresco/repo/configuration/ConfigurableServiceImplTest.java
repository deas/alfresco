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
package org.alfresco.repo.configuration;

import java.util.List;

import org.alfresco.model.ApplicationModel;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.test_category.BaseSpringTestsCategory;
import org.alfresco.util.BaseSpringTest;
import org.junit.experimental.categories.Category;

/**
 * Configurable service implementation test
 * 
 * @author Roy Wetherall
 */
@SuppressWarnings("unused")
@Category(BaseSpringTestsCategory.class)
public class ConfigurableServiceImplTest extends BaseSpringTest
{
	public NodeService nodeService;
    private ServiceRegistry serviceRegistry;
	private ConfigurableService configurableService;
	private StoreRef testStoreRef;
	private NodeRef rootNodeRef;
	private NodeRef nodeRef;
	
	/**
	 * onSetUpInTransaction
	 */
	@Override
	protected void onSetUpInTransaction() throws Exception
	{
		this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
		this.serviceRegistry = (ServiceRegistry)this.applicationContext.getBean(ServiceRegistry.SERVICE_REGISTRY);
		this.configurableService = (ConfigurableService)this.applicationContext.getBean("configurableService");
		
		this.testStoreRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        this.rootNodeRef = this.nodeService.getRootNode(this.testStoreRef);
        
        // Create the node used for tests
        this.nodeRef = this.nodeService.createNode(
                this.rootNodeRef,
				ContentModel.ASSOC_CHILDREN,
                ContentModel.ASSOC_CHILDREN,
                ContentModel.TYPE_CONTAINER).getChildRef();
	}

	/**
	 * Test isConfigurable
	 */
	public void testIsConfigurable()
	{
		assertFalse(this.configurableService.isConfigurable(this.nodeRef));
		this.configurableService.makeConfigurable(this.nodeRef);
		assertTrue(this.configurableService.isConfigurable(this.nodeRef));
	}
	
	/**
	 * Test make configurable
	 */
	public void testMakeConfigurable()
	{
		this.configurableService.makeConfigurable(this.nodeRef);
		assertTrue(this.nodeService.hasAspect(this.nodeRef, ApplicationModel.ASPECT_CONFIGURABLE));
		List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(
                this.nodeRef,
                RegexQNamePattern.MATCH_ALL,
                ApplicationModel.ASSOC_CONFIGURATIONS);
		assertNotNull(assocs);
		assertEquals(1, assocs.size());
	}
	
	/**
	 * Test getConfigurationFolder
	 */
	public void testGetConfigurationFolder()
	{
		assertNull(this.configurableService.getConfigurationFolder(this.nodeRef));
		this.configurableService.makeConfigurable(nodeRef);
		NodeRef configFolder = this.configurableService.getConfigurationFolder(this.nodeRef);
		assertNotNull(configFolder);
		NodeRef parentNodeRef = this.nodeService.getPrimaryParent(configFolder).getParentRef();
		assertNotNull(parentNodeRef);
		assertEquals(nodeRef, parentNodeRef);
	}
	
}
