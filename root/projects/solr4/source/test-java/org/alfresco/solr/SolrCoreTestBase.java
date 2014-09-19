/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.solr;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.content.SolrContentStore;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.RequestHandlers;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrInfoMBean;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.UpdateHandler;
import org.apache.solr.update.processor.RunUpdateProcessorFactory;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.solr.update.processor.UpdateRequestProcessorChain;
import org.apache.solr.update.processor.UpdateRequestProcessorFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * This base class sets up Solr Core and related objects for unit tests.
 * @author Ahmed Owian
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class SolrCoreTestBase
{
    protected @Mock
    AlfrescoCoreAdminHandler adminHandler;
    protected @Mock
    UpdateHandler updateHandler;
    protected @Mock
    SolrResourceLoader resourceLoader;
    protected @Mock
    SOLRAPIClient solrAPIClient;
    protected @Mock
    SolrContentStore solrContentStore;
    protected @Mock
    RunUpdateProcessorFactory runUpdateProcessorFactory;
    protected @Mock
    UpdateRequestProcessor processor;
    protected SolrCore core;
    protected CoreDescriptor coreDescriptor;
    protected CoreContainer coreContainer;
    protected RequestHandlers reqHandlers;
    protected @Mock SolrRequestHandler selectRequestHandler;
    protected Map<String, SolrInfoMBean> infoRegistry;
    
    @Before
    public void setUpBase() throws Exception
    {
        coreContainer = new CoreContainer();
        coreDescriptor = new CoreDescriptor(coreContainer, "name", "instanceDir");
        when(resourceLoader.getCoreProperties()).thenReturn(new Properties());
        
        // SolrCore is final, we can't mock with mockito
        core = new SolrCore("name", coreDescriptor);
        FieldUtils.writeField(core, "updateHandler", updateHandler, true);
        FieldUtils.writeField(core, "resourceLoader", resourceLoader, true);
        infoRegistry = new HashMap<String, SolrInfoMBean>();
        FieldUtils.writeField(core, "infoRegistry", infoRegistry, true);
        reqHandlers = new RequestHandlers(core);
        reqHandlers.register("/select", selectRequestHandler);
        FieldUtils.writeField(core, "reqHandlers", reqHandlers, true);

        Map<String, UpdateRequestProcessorChain> map = new HashMap<>();
        UpdateRequestProcessorFactory[] factories = new UpdateRequestProcessorFactory[] { runUpdateProcessorFactory };
        when(runUpdateProcessorFactory.getInstance(any(SolrQueryRequest.class), any(SolrQueryResponse.class),
                                any(UpdateRequestProcessor.class))).thenReturn(processor);
        UpdateRequestProcessorChain def = new UpdateRequestProcessorChain(factories, core);
        map.put(null, def);
        map.put("", def);
        FieldUtils.writeField(core, "updateProcessorChains", map, true);
    }
}
