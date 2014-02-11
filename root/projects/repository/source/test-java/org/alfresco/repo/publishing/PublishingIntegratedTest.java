/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

package org.alfresco.repo.publishing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.publishing.PublishingDetails;
import org.alfresco.service.cmr.publishing.PublishingEvent;
import org.alfresco.service.cmr.publishing.PublishingPackageEntry;
import org.alfresco.service.cmr.publishing.PublishingService;
import org.alfresco.service.cmr.publishing.channels.Channel;
import org.alfresco.service.cmr.publishing.channels.ChannelService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.GUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Brian
 * @since 4.0
 */
@Category(OwnJVMTestsCategory.class)
public class PublishingIntegratedTest extends BaseSpringTest
{
    protected ServiceRegistry serviceRegistry;
    protected RetryingTransactionHelper retryingTransactionHelper;
    protected NodeService nodeService;
    protected WorkflowService workflowService;
    protected FileFolderService fileFolderService;
    protected SiteService siteService;
    private ChannelService channelService;
    private PublishingService publishingService;

    protected AuthenticationComponent authenticationComponent;
    private String siteId;
    private AbstractChannelType mockedChannelType = mock(AbstractChannelType.class);
    private String channelTypeName;

    @Test
    public void testScheduleNewEvent() throws Exception
    {
        Channel channel = channelService.createChannel(channelTypeName, GUID.generate(), null);

        Set<NodeRef> nodes = new HashSet<NodeRef>();
        for (int i = 0; i < 4; ++i)
        {
            nodes.add(nodeService.createNode(channel.getNodeRef(), ContentModel.ASSOC_CONTAINS, QName.createQName(
                    NamespaceService.CONTENT_MODEL_1_0_URI, Integer.toString(i)), ContentModel.TYPE_CONTENT).getChildRef());
        }

        Calendar schedule = Calendar.getInstance();
        schedule.add(Calendar.HOUR, 1);
        
        PublishingDetails details = publishingService.createPublishingDetails()
            .addNodesToPublish(nodes)
            .setPublishChannelId(channel.getId())
            .setSchedule(schedule);

        String eventId = publishingService.scheduleNewEvent(details);
        
        PublishingEvent event = publishingService.getPublishingEvent(eventId);
        
        Assert.assertEquals(schedule, event.getScheduledTime());
        Assert.assertEquals(eventId, event.getId());
        Collection<PublishingPackageEntry> entries = event.getPackage().getEntries();
        Assert.assertEquals(4, entries.size());
        for (PublishingPackageEntry entry : entries)
        {
            Assert.assertTrue(entry.isPublish());
            Assert.assertTrue(nodes.remove(entry.getNodeRef()));
        }
        Assert.assertTrue(nodes.isEmpty());
    }
    
    @Test
    public void testCancelScheduledEvent()
    {
        Channel channel = channelService.createChannel(channelTypeName, GUID.generate(), null);

        Set<NodeRef> nodes = new HashSet<NodeRef>();
        for (int i = 0; i < 4; ++i)
        {
            nodes.add(nodeService.createNode(channel.getNodeRef(), ContentModel.ASSOC_CONTAINS, QName.createQName(
                    NamespaceService.CONTENT_MODEL_1_0_URI, Integer.toString(i)), ContentModel.TYPE_CONTENT).getChildRef());
        }
        Calendar schedule = Calendar.getInstance();
        schedule.add(Calendar.HOUR, 1);

        PublishingDetails details = publishingService.createPublishingDetails()
            .addNodesToPublish(nodes)
            .setPublishChannelId(channel.getId())
            .setSchedule(schedule);
        String eventId = publishingService.scheduleNewEvent(details);
        PublishingEvent event = publishingService.getPublishingEvent(eventId);
        Assert.assertNotNull(event);
        publishingService.cancelPublishingEvent(eventId);
        event = publishingService.getPublishingEvent(eventId);
        Assert.assertNull(event);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void onSetUp() throws Exception
    {
        serviceRegistry = (ServiceRegistry) applicationContext.getBean(ServiceRegistry.SERVICE_REGISTRY);
        serviceRegistry.getAuthenticationService().authenticate("admin", "admin".toCharArray());

        retryingTransactionHelper = serviceRegistry.getTransactionService().getRetryingTransactionHelper();
        fileFolderService = serviceRegistry.getFileFolderService();
        workflowService = serviceRegistry.getWorkflowService();
        nodeService = serviceRegistry.getNodeService();
        siteService = serviceRegistry.getSiteService();

        channelService = (ChannelService) applicationContext.getBean("ChannelService");
        publishingService = (PublishingService) applicationContext.getBean("PublishingService");
        siteId = GUID.generate();
        siteService.createSite("test", siteId, "Test site created by ChannelServiceImplIntegratedTest",
                "Test site created by PublishingIntegratedTest", SiteVisibility.PUBLIC);

        channelTypeName = "MockedChannelType";
        when(mockedChannelType.getId()).thenReturn(channelTypeName);
        when(mockedChannelType.getChannelNodeType()).thenReturn(PublishingModel.TYPE_DELIVERY_CHANNEL);

        if (channelService.getChannelType(channelTypeName)== null)
        {
            channelService.register(mockedChannelType);
        }
    }
}