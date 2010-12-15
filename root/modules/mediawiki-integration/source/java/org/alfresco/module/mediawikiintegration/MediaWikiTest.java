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
package org.alfresco.module.mediawikiintegration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.mediawikiintegration.action.MediaWikiActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * @author Roy Wetherall
 */
@SuppressWarnings("unused")
public class MediaWikiTest extends BaseSpringTest
{    
    private NodeService nodeService;    
    private ContentService contentService;
    private MediaWikiService mediaWikiService;
    private ActionService actionService;
    
    private StoreRef storeRef;
    private NodeRef rootNode;

    /**
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
        // Get references to the required beans
        this.nodeService = (NodeService)this.applicationContext.getBean("NodeService");
        this.contentService = (ContentService)this.applicationContext.getBean("ContentService");
        this.mediaWikiService = (MediaWikiService)this.applicationContext.getBean("mediaWikiService");
        this.actionService = (ActionService)this.applicationContext.getBean("ActionService");
        
        // Create nodes used in the tests
        this.storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "mediaWikiTest_" + System.currentTimeMillis());
        this.rootNode = this.nodeService.getRootNode(this.storeRef);
             
    }
    
    public void testCreateMediaWiki()
    {
        // Create a new mediawiki space
        NodeRef folder = this.nodeService.createNode(this.rootNode, ContentModel.ASSOC_CHILDREN, ContentModel.ASSOC_CHILDREN, ContentModel.TYPE_FOLDER).getChildRef();
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
        properties.put(ContentModel.PROP_NAME, "mywiki");
        NodeRef mediaWikiNodeRef = this.nodeService.createNode(
                folder, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "mywiki"), 
                Constants.TYPE_MEDIAWIKI,
                properties).getChildRef();
        assertNotNull(mediaWikiNodeRef);    
        
        setComplete();
    }
    
    public void testCreateSitePage()
    {
        NodeRef folder = this.nodeService.createNode(this.rootNode, ContentModel.ASSOC_CHILDREN, ContentModel.ASSOC_CHILDREN, ContentModel.TYPE_FOLDER).getChildRef();
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>(1);
        properties.put(ContentModel.PROP_NAME, "mywiki");
        NodeRef mediaWikiNodeRef = this.nodeService.createNode(
                folder, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "mywiki"), 
                Constants.TYPE_MEDIAWIKI,
                properties).getChildRef();
        
        Action action = actionService.createAction(MediaWikiActionExecuter.NAME);
        action.setParameterValue(MediaWikiActionExecuter.PARAM_MEDIAWIKI_ACTION, "createSitePage");
        action.setParameterValue(MediaWikiActionExecuter.PARAM_PAGE_TITLE, "myTestSite");
        //action.setParameterValue(MediaWikiActionExecuter.PARAM_BODY, "This is a test .. .this is a test .. if you can see this jump up and down!!!");
        actionService.executeAction(action, mediaWikiNodeRef, false, false);
    }

}
