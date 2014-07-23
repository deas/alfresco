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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.solr.client.AclReaders;
import org.alfresco.solr.client.SOLRAPIClient;
import org.alfresco.solr.content.SolrContentStore;
import org.alfresco.util.NumericEncoder;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.UpdateHandler;
import org.apache.solr.update.processor.DistributedUpdateProcessorFactory;
import org.apache.solr.update.processor.LogUpdateProcessorFactory;
import org.apache.solr.update.processor.RunUpdateProcessorFactory;
import org.apache.solr.update.processor.UpdateRequestProcessorChain;
import org.apache.solr.update.processor.UpdateRequestProcessorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for the {@link SolrInformationServer} class.
 * 
 * @author Matt Ward
 */
//@RunWith(MockitoJUnitRunner.class)
public class SolrInformationServerTest
{
    private SolrInformationServer infoServer;
    private @Mock AlfrescoCoreAdminHandler adminHandler;
    private @Mock UpdateHandler updateHandler;
    private @Mock SolrResourceLoader resourceLoader;
    private @Mock SOLRAPIClient solrAPIClient;
    private @Mock SolrContentStore solrContentStore;
    private @Mock RunUpdateProcessorFactory runUpdateProcessorFactory;
    private SolrCore core;
    private CoreDescriptor coreDescriptor;
    private CoreContainer coreContainer;
    
    @Before
    public void setUp() throws Exception
    {
        coreContainer = new CoreContainer();
        coreDescriptor = new CoreDescriptor(coreContainer, "name", "instanceDir");
        when(resourceLoader.getCoreProperties()).thenReturn(new Properties());
        // SolrCore is final, we can't mock with mockito
        core = new SolrCore("name", coreDescriptor);
        FieldUtils.writeField(core, "updateHandler", updateHandler, true);
        FieldUtils.writeField(core, "resourceLoader", resourceLoader, true);
        
        Map<String, UpdateRequestProcessorChain> map = new HashMap<>();
        UpdateRequestProcessorFactory[] factories = new UpdateRequestProcessorFactory[]{
               runUpdateProcessorFactory
        };
        UpdateRequestProcessorChain def = new UpdateRequestProcessorChain(factories, core);
        map.put(null, def);
        map.put("", def);
        FieldUtils.writeField(core, "updateProcessorChains", map, true);
        
        
        infoServer = new SolrInformationServer(adminHandler, core, solrAPIClient, solrContentStore);
    }

    //@Test
    // TODO: fix update chain ...
    public void testIndexAcl() throws IOException
    {
        assert(core.getUpdateProcessingChain(null) != null);
        
        // Source/expected data
        List<AclReaders> aclReadersList = new ArrayList<AclReaders>();
        aclReadersList.add(new AclReaders(101, Arrays.asList("r1", "r2", "r3"), Arrays.asList("d1", "d2"), 999, "example.com"));
        aclReadersList.add(new AclReaders(102, Arrays.asList("r4", "r5", "r6"), Arrays.asList("d3", "d4"), 999, "another.test"));
        aclReadersList.add(new AclReaders(103,
                    Arrays.asList("GROUP_marketing",
                                  "simpleuser",
                                  "GROUP_EVERYONE",
                                  "ROLE_GUEST",
                                  "ROLE_ADMINISTRATOR",
                                  "ROLE_OWNER",
                                  "ROLE_RANDOM"),
                      Arrays.asList("GROUP_engineering",
                                  "justauser",
                                  "GROUP_EVERYONE",
                                  "ROLE_GUEST",
                                  "ROLE_ADMINISTRATOR",
                                  "ROLE_OWNER",
                                  "ROLE_RANDOM"),                    
                    999, "tenant.test"));
        aclReadersList.add(new AclReaders(104,
                    Arrays.asList("GROUP_marketing",
                                  "simpleuser",
                                  "GROUP_EVERYONE",
                                  "ROLE_GUEST",
                                  "ROLE_ADMINISTRATOR",
                                  "ROLE_OWNER",
                                  "ROLE_RANDOM"),
                      Arrays.asList("GROUP_engineering",
                                  "justauser",
                                  "GROUP_EVERYONE",
                                  "ROLE_GUEST",
                                  "ROLE_ADMINISTRATOR",
                                  "ROLE_OWNER",
                                  "ROLE_RANDOM"),                    
                    999, "" /*Zero-length tenant == no mangling*/));

        final boolean willOverwrite = true;
        
        // Invoke the method under test
        infoServer.indexAcl(aclReadersList, willOverwrite);
        
        // Capture the AddUpdateCommand instances for further analysis.
        ArgumentCaptor<AddUpdateCommand> cmdArg = ArgumentCaptor.forClass(AddUpdateCommand.class);
        // UpdateHandler will receive as many addDoc(...) calls as items in the aclReaderList.
        verify(updateHandler, times(aclReadersList.size())).addDoc(cmdArg.capture());
        
        // Verify that the AddUpdateCommand is as expected.
        List<AddUpdateCommand> updates = cmdArg.getAllValues();
        assertEquals("Wrong number of updates", aclReadersList.size(), updates.size());
        
        for (int docIndex = 0; docIndex < updates.size(); docIndex++)
        {
            AddUpdateCommand update = updates.get(docIndex);
            assertEquals("Overwrite flag was not correct value.", willOverwrite, update.overwrite);
            SolrInputDocument inputDoc = update.getSolrInputDocument();
            // Retrieve the original AclReaders object and compare with data in submitted SolrInputDocument
            final AclReaders sourceAclReaders = aclReadersList.get(docIndex);
            assertEquals(AlfrescoSolrDataModel.getTenantId(sourceAclReaders.getTenantDomain())+"!"+NumericEncoder.encode(sourceAclReaders.getId())+"!ACL", inputDoc.getFieldValue("id").toString());            
            assertEquals("0", inputDoc.getFieldValue("_version_").toString());
            assertEquals(sourceAclReaders.getId(), inputDoc.getFieldValue(QueryConstants.FIELD_ACLID));
            assertEquals(sourceAclReaders.getAclChangeSetId(), inputDoc.getFieldValue(QueryConstants.FIELD_INACLTXID));
            
            if (sourceAclReaders.getId() == 103)
            {
                // Authorities *may* (e.g. GROUP, EVERYONE, GUEST) be mangled to include tenant information
                final Collection<Object> docReaders = inputDoc.getFieldValues(QueryConstants.FIELD_READER);
                assertEquals(Arrays.asList("GROUP_marketing@tenant.test",
                                           "simpleuser",
                                           "GROUP_EVERYONE@tenant.test",
                                           "ROLE_GUEST@tenant.test",
                                           "ROLE_ADMINISTRATOR",
                                           "ROLE_OWNER",
                                           "ROLE_RANDOM"), docReaders);
                final Collection<Object> docDenied = inputDoc.getFieldValues(QueryConstants.FIELD_DENIED);
                assertEquals(Arrays.asList("GROUP_engineering@tenant.test",
                            "justauser",
                            "GROUP_EVERYONE@tenant.test",
                            "ROLE_GUEST@tenant.test",
                            "ROLE_ADMINISTRATOR",
                            "ROLE_OWNER",
                            "ROLE_RANDOM"), docDenied);
            }
            else
            {
                // Simple case, no authority/tenant mangling.
                assertEquals(sourceAclReaders.getReaders(), inputDoc.getFieldValues(QueryConstants.FIELD_READER));            
                assertEquals(sourceAclReaders.getDenied(), inputDoc.getFieldValues(QueryConstants.FIELD_DENIED));            
            }
        }
    }

}
