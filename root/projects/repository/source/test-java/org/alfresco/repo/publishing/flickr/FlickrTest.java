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

package org.alfresco.repo.publishing.flickr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.node.archive.NodeArchiveService;
import org.alfresco.repo.publishing.Environment;
import org.alfresco.repo.publishing.PublishingModel;
import org.alfresco.repo.publishing.PublishingQueueImpl;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.publishing.channels.Channel;
import org.alfresco.service.cmr.publishing.channels.ChannelService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.test_category.BaseSpringTestsCategory;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.GUID;
import org.junit.Assert;
import org.junit.experimental.categories.Category;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Brian
 * @since 4.0
 */
@Category(BaseSpringTestsCategory.class)
public class FlickrTest extends BaseSpringTest
{
    protected ServiceRegistry serviceRegistry;
    protected SiteService siteService;
    protected FileFolderService fileFolderService;
    protected NodeService nodeService;
    protected String siteId;
    protected PublishingQueueImpl queue;
    protected Environment environment;
    protected NodeRef docLib;
    protected NodeArchiveService nodeArchiveService;

    private ChannelService channelService;
    
    private RetryingTransactionHelper transactionHelper;

    public void onSetUp() throws Exception
    {
        serviceRegistry = (ServiceRegistry) getApplicationContext().getBean("ServiceRegistry");
        channelService = (ChannelService) getApplicationContext().getBean("channelService"); 
        nodeArchiveService = (NodeArchiveService) getApplicationContext().getBean("nodeArchiveService");
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        siteService = serviceRegistry.getSiteService();
        fileFolderService = serviceRegistry.getFileFolderService();
        nodeService = serviceRegistry.getNodeService();
        transactionHelper = serviceRegistry.getTransactionService().getRetryingTransactionHelper();

        siteId = GUID.generate();
        siteService.createSite("test", siteId, "Site created by publishing test", "Site created by publishing test",
                SiteVisibility.PUBLIC);
        docLib = siteService.createContainer(siteId, SiteService.DOCUMENT_LIBRARY, ContentModel.TYPE_FOLDER, null);
    }

    public void onTearDown()
    {
        SiteInfo siteInfo = siteService.getSite(siteId);
        if (siteInfo != null)
        {
            siteService.deleteSite(siteId);
            nodeArchiveService.purgeArchivedNode(nodeArchiveService.getArchivedNode(siteInfo.getNodeRef()));
        }
    }
    
    public void testBlank()
    {
    }
    
    //Note that this test isn't normally run, as it requires a valid Flickr OAuth token.
    //To run it, remove the initial 'x' from the method name and set the appropriate values where the
    //text "YOUR_OAUTH_TOKEN_VALUE" and "YOUR_OAUTH_TOKEN_SECRET" appear. Note that these can be quite tricky to obtain...
    public void xtestFlickrPublishAndUnpublishActions() throws Exception
    {
        final String channelName = "FlickrTestChannel_" + GUID.generate();
        final FlickrChannelType channelType = (FlickrChannelType) channelService.getChannelType(FlickrChannelType.ID);
        final NodeRef contentNode = transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                props.put(PublishingModel.PROP_OAUTH1_TOKEN_VALUE, "YOUR_OAUTH_TOKEN_VALUE");
                props.put(PublishingModel.PROP_OAUTH1_TOKEN_SECRET, "YOUR_OAUTH_TOKEN_SECRET");
                props.put(PublishingModel.PROP_AUTHORISATION_COMPLETE, Boolean.TRUE);
                
                Channel channel = channelService.createChannel(FlickrChannelType.ID, channelName, props);
                //This looks a little odd, but a new channel always has its "authorisation complete" flag
                //forced off initially. This will force it on for this channel...
                channelService.updateChannel(channel, props);
                
                NodeRef channelNode = channel.getNodeRef();
                Resource file = new ClassPathResource("test/alfresco/TestImageFile.png");
                Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>();
                contentProps.put(ContentModel.PROP_NAME, "Test Image");
                NodeRef contentNode = nodeService.createNode(channelNode, ContentModel.ASSOC_CONTAINS,
                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "testImage"),
                        ContentModel.TYPE_CONTENT, contentProps).getChildRef();
                ContentService contentService = serviceRegistry.getContentService();
                ContentWriter writer = contentService.getWriter(contentNode, ContentModel.PROP_CONTENT, true);
                writer.setMimetype(MimetypeMap.MIMETYPE_IMAGE_PNG);
                writer.putContent(file.getFile());
                return contentNode;
            }
        });
        transactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                Channel channel = channelService.getChannelByName(channelName);
                channelType.publish(contentNode, channel.getProperties());
                
                Map<QName, Serializable> props = nodeService.getProperties(contentNode);
                Assert.assertTrue(nodeService.hasAspect(contentNode, FlickrPublishingModel.ASPECT_ASSET));
                Assert.assertNotNull(props.get(PublishingModel.PROP_ASSET_ID));
                Assert.assertNotNull(props.get(PublishingModel.PROP_ASSET_URL));

                System.out.println("Asset id: " + props.get(PublishingModel.PROP_ASSET_ID));
                System.out.println("Asset URL: " + props.get(PublishingModel.PROP_ASSET_URL));
                
                channelType.unpublish(contentNode, channel.getProperties());

                props = nodeService.getProperties(contentNode);
                Assert.assertFalse(nodeService.hasAspect(contentNode, FlickrPublishingModel.ASPECT_ASSET));
                Assert.assertFalse(nodeService.hasAspect(contentNode, PublishingModel.ASPECT_ASSET));
                Assert.assertNull(props.get(PublishingModel.PROP_ASSET_ID));
                Assert.assertNull(props.get(PublishingModel.PROP_ASSET_URL));
                return null;
            }
        });

    }

}
