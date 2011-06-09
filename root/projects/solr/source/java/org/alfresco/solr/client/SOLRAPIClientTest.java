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
package org.alfresco.solr.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

/**
 * Tests {@link SOLRAPIClient}
 * 
 * @since 4.0
 */
public class SOLRAPIClientTest extends TestCase
{
    private static Log logger = LogFactory.getLog(SOLRAPIClientTest.class);
    
    private static final String TEST_MODEL = "org/alfresco/repo/dictionary/dictionarydaotest_model.xml";

    private SOLRAPIClient client;
    
    private void loadModel(Map<String, M2Model> modelMap, HashSet<String> loadedModels, M2Model model)
    {
        String modelName = model.getName();
        if (loadedModels.contains(modelName) == false)
        {
            for (M2Namespace importNamespace : model.getImports())
            {
                M2Model importedModel = modelMap.get(importNamespace.getUri());
                if (importedModel != null)
                {

                    // Ensure that the imported model is loaded first
                    loadModel(modelMap, loadedModels, importedModel);
                }
            }

            AlfrescoSolrDataModel.getInstance("test").putModel(model);
            loadedModels.add(modelName);
        }
    }
    
    @Override
    public void setUp() throws Exception
    {
        boolean inRepoContext = true;
        List<String> bootstrapModels = new ArrayList<String>();
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");
        if (inRepoContext)
        {
            bootstrapModels.add("alfresco/model/applicationModel.xml");
            bootstrapModels.add("alfresco/model/blogIntegrationModel.xml");
            bootstrapModels.add("alfresco/model/calendarModel.xml");
            bootstrapModels.add("alfresco/model/contentModel.xml");
            bootstrapModels.add("alfresco/model/datalistModel.xml");
            bootstrapModels.add("alfresco/model/emailServerModel.xml");
            bootstrapModels.add("alfresco/model/forumModel.xml");
            bootstrapModels.add("alfresco/model/imapModel.xml");
            bootstrapModels.add("alfresco/model/linksModel.xml");
            bootstrapModels.add("alfresco/model/siteModel.xml");
            bootstrapModels.add("alfresco/model/systemModel.xml");
            bootstrapModels.add("alfresco/model/transferModel.xml");
            bootstrapModels.add("alfresco/model/wcmAppModel.xml");
            bootstrapModels.add("alfresco/model/wcmModel.xml");
            
            //bootstrapModels.add("org/alfresco/repo/security/authentication/userModel.xml");
            //bootstrapModels.add("org/alfresco/repo/action/actionModel.xml");
            //bootstrapModels.add("org/alfresco/repo/rule/ruleModel.xml");
            //bootstrapModels.add("org/alfresco/repo/version/version_model.xml");  
        }
        else
        {
            bootstrapModels.add(TEST_MODEL);
        }
        HashMap<String, M2Model> modelMap = new HashMap<String, M2Model>();
        for (String bootstrapModel : bootstrapModels)
        {
            logger.debug("Loading ..."+bootstrapModel);
            InputStream modelStream = getClass().getClassLoader().getResourceAsStream(bootstrapModel);
            M2Model model = M2Model.createModel(modelStream);
            for (M2Namespace namespace : model.getNamespaces())
            {
                modelMap.put(namespace.getUri(), model);
            }
        }

        // Load the models ensuring that they are loaded in the correct order
        HashSet<String> loadedModels = new HashSet<String>();
        for (M2Model model : modelMap.values())
        {
            loadModel(modelMap, loadedModels, model);
        }
        
        AlfrescoSolrDataModel model = AlfrescoSolrDataModel.getInstance("test");
        // dummy implementation - don't know how to hook into SOLR-side namespace stuff
        NamespaceDAO namespaceDAO = new TestNamespaceDAO();
        client = new SOLRAPIClient(model.getDictionaryService(), namespaceDAO,
                "http://127.0.0.1:8085/alfresco/service", "admin", "admin");
    }
    
    /**
     * Full testing of ChangeSets, ACLs and Readers
     */
    public void testGetAcls() throws Exception
    {
        List<AclChangeSet> aclChangeSets = null;
        
        aclChangeSets = client.getAclChangeSets(null, null, 50);
        assertTrue("Too many results", aclChangeSets.size() <= 50);
        if (aclChangeSets.size() < 2)
        {
            return;             // Not enough data
        }
        AclChangeSet aclChangeSetCheck = null;
        AclChangeSet aclChangeSet0 = aclChangeSets.get(0);
        AclChangeSet aclChangeSet1 = aclChangeSets.get(1);
        long id0 = aclChangeSet0.getId();
        long commitTimeMs0 = aclChangeSet0.getCommitTimeMs();
        // Now query for the next ID
        Long nextId = id0 + 1;
        aclChangeSets = client.getAclChangeSets(commitTimeMs0, nextId, 1);
        assertEquals(1, aclChangeSets.size());
        aclChangeSetCheck = aclChangeSets.get(0);
        assertEquals(aclChangeSet1, aclChangeSetCheck);
        
        Map<Long, AclChangeSet> aclChangeSetsById = new HashMap<Long, AclChangeSet>();
        for (AclChangeSet aclChangeSet : aclChangeSets)
        {
            aclChangeSetsById.put(aclChangeSet.getId(), aclChangeSet);
        }
        
        Set<Long> aclIdUniqueCheck = new HashSet<Long>(1000);
        // Now do a large walk-through of the ACLs
        Long minAclChangeSetId = null;
        Long fromCommitTimeMs = null;
        for (int i = 0; i < 100; i++)
        {
            aclChangeSets = client.getAclChangeSets(fromCommitTimeMs, minAclChangeSetId, 10);
            if (aclChangeSets.size() == 0)
            {
                break;
            }
            // Now repeat for the ACLs, keeping track of the last ChangeSet
            Long nextAclId = null;
            while (true)
            {
                List<Acl> acls = client.getAcls(aclChangeSets, nextAclId, 1000);
                if (acls.size() == 0)
                {
                    break;                  // Run out of ACLs
                }
                Set<Long> aclIds = new HashSet<Long>(1000);
                for (Acl acl : acls)
                {
                    long aclId = acl.getId();
                    aclIds.add(aclId);
                    if (!aclIdUniqueCheck.add(aclId))
                    {
                        fail("ACL already processed: " + aclId);
                    }
                    // Check that we are ascending
                    if (nextAclId != null)
                    {
                        assertTrue("ACL IDs must be ascending: " + aclId, nextAclId.longValue() <= aclId);
                    }
                    nextAclId = aclId + 1;
                }
                // Now get the readers for these ACLs
                List<AclReaders> aclsReaders = client.getAclReaders(acls);
                // Check that the ACL ids are all covered
                for (AclReaders aclReaders : aclsReaders)
                {
                    Long aclId = aclReaders.getId();
                    aclIds.remove(aclId);
                }
                assertTrue("Some ACL IDs were not covered: " + aclIds, aclIds.size() == 0);
            }
            // March in time
            AclChangeSet lastAclChangeSet = aclChangeSets.get(aclChangeSets.size() - 1);
            fromCommitTimeMs = lastAclChangeSet.getCommitTimeMs();
            minAclChangeSetId = lastAclChangeSet.getId() + 1;
        }
    }
    
    public void testGetTransactions() throws Exception
    {
        // get transactions starting from txn id 1298288417234l
        List<Transaction> transactions = client.getTransactions(1298288417234l, null, 5);

        // get transactions starting from transaction 426
        transactions = client.getTransactions(null, Long.valueOf(1), 5);
        List<Long> transactionIds = new ArrayList<Long>(transactions.size());
        for(Transaction info : transactions)
        {
            logger.debug(info);
            transactionIds.add(Long.valueOf(info.getId()));
        }

        // get the first 3 nodes in those transactions
        GetNodesParameters params = new GetNodesParameters();
        params.setTransactionIds(transactionIds);
        List<Node> nodes = client.getNodes(params, 5);
        for(Node info : nodes)
        {
            logger.debug(info);
        }

        // get the next 3 nodes in those transactions i.e. starting from the last node id (inclusive)
        params = new GetNodesParameters();
        params.setTransactionIds(transactionIds);
        params.setFromNodeId(nodes.get(nodes.size() - 1).getId());
        nodes = client.getNodes(params, 3);
        List<Long> nodeIds = new ArrayList<Long>(nodes.size());
        for(Node info : nodes)
        {
            logger.debug(info);
            nodeIds.add(info.getId());
        }
        
        NodeMetaDataParameters metaParams = new NodeMetaDataParameters();
        metaParams.setNodeIds(nodeIds);
        List<NodeMetaData> metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            logger.debug(info);
        }
    }
    
    public void testMetaData() throws IOException, JSONException
    {
        NodeMetaDataParameters metaParams = new NodeMetaDataParameters();
        List<Long> nodeIds = new ArrayList<Long>(1);
        nodeIds.add(1l);
        metaParams.setNodeIds(nodeIds);
        List<NodeMetaData> metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            logger.debug(info);
        }
        
        metaParams = new NodeMetaDataParameters();
        nodeIds = new ArrayList<Long>(1);
        nodeIds.add(9l);
        metaParams.setNodeIds(nodeIds);
        metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            logger.debug(info);
        }
        
        metaParams = new NodeMetaDataParameters();
        nodeIds = new ArrayList<Long>(1);
        nodeIds.add(19l);
        metaParams.setNodeIds(nodeIds);
        metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            logger.debug(info);
        }

        // individual tag/category
        // TODO check why the category path has a null QName
        metaParams = new NodeMetaDataParameters();
        nodeIds = new ArrayList<Long>(1);
        nodeIds.add(49437l);
        metaParams.setNodeIds(nodeIds);
        metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            logger.debug(info);
        }
        
        // content with tags
        metaParams = new NodeMetaDataParameters();
        nodeIds = new ArrayList<Long>(1);
        nodeIds.add(49431l);
        metaParams.setNodeIds(nodeIds);
        metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            logger.debug(info);
            MultiPropertyValue multi = (MultiPropertyValue)info.getProperties().get(QName.createQName("{http://www.alfresco.org/model/content/1.0}taggable"));
            for(PropertyValue propValue : multi.getValues())
            {
                logger.debug("multi property values = " + propValue);
            }
        }

        // content with null property values for author
        metaParams = new NodeMetaDataParameters();
        nodeIds = new ArrayList<Long>(1);
        nodeIds.add(117630l);
        metaParams.setNodeIds(nodeIds);
        metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            logger.debug(info);
        }
        
        // content with accented characters in title properties
        metaParams = new NodeMetaDataParameters();
        nodeIds = new ArrayList<Long>(1);
        nodeIds.add(117678l);
        metaParams.setNodeIds(nodeIds);
        metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            logger.debug(info);
        }
    }

    private void outputTextContent(SOLRAPIClient.GetTextContentResponse response) throws IOException
    {
        InputStream in = response.getContent();
        if(in != null)
        {
            logger.debug("Text content:");

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = null;
            while((line = reader.readLine()) != null)
            {
                logger.debug(line);
            }
        }
    }

/*    public void testGetTextContent()
    {
        try
        {
            SOLRAPIClient.GetTextContentResponse response = client.getTextContent(Long.valueOf(35617l), null, null);
            logger.debug("Status = " + response.getStatus());
            logger.debug("Transform Status = " + response.getTransformStatus());
            logger.debug("Transform Exception = " + response.getTransformException());
            logger.debug("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);

            // test cache
            Long modifiedSince = System.currentTimeMillis();
            response = client.getTextContent(Long.valueOf(35617l), null, modifiedSince);
            logger.debug("Status = " + response.getStatus());
            logger.debug("Transform Status = " + response.getTransformStatus());
            logger.debug("Transform Exception = " + response.getTransformException());
            logger.debug("Request took " + response.getRequestDuration() + " ms");
            
            response = client.getTextContent(Long.valueOf(35618l), null, null);
            logger.debug("Status = " + response.getStatus());
            logger.debug("Transform Status = " + response.getTransformStatus());
            logger.debug("Transform Exception = " + response.getTransformException());
            logger.debug("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);
            
            response = client.getTextContent(Long.valueOf(35619l), null, null);
            logger.debug("Status = " + response.getStatus());
            logger.debug("Transform Status = " + response.getTransformStatus());
            logger.debug("Transform Exception = " + response.getTransformException());
            logger.debug("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);
            
            response = client.getTextContent(Long.valueOf(35620l), null, null);
            logger.debug("Status = " + response.getStatus());
            logger.debug("Transform Status = " + response.getTransformStatus());
            logger.debug("Transform Exception = " + response.getTransformException());
            logger.debug("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }*/
    
    private class TestNamespaceDAO implements NamespaceDAO
    {
        private Map<String, String> prefixMappings = new HashMap<String, String>(10);
        
        TestNamespaceDAO()
        {
            prefixMappings.put(NamespaceService.CONTENT_MODEL_PREFIX, NamespaceService.CONTENT_MODEL_1_0_URI);
            prefixMappings.put(NamespaceService.SYSTEM_MODEL_PREFIX, NamespaceService.SYSTEM_MODEL_1_0_URI);
            prefixMappings.put(NamespaceService.DEFAULT_PREFIX, NamespaceService.DEFAULT_URI);
            prefixMappings.put(NamespaceService.DICTIONARY_MODEL_PREFIX, NamespaceService.DICTIONARY_MODEL_1_0_URI);
            prefixMappings.put(NamespaceService.APP_MODEL_PREFIX, NamespaceService.APP_MODEL_1_0_URI);            
            prefixMappings.put("ver", "http://www.alfresco.org/model/versionstore/1.0");            
            prefixMappings.put("ver2", "http://www.alfresco.org/model/versionstore/2.0");            
        }

        @Override
        public String getNamespaceURI(String prefix) throws NamespaceException
        {
            return prefixMappings.get(prefix);
        }

        @Override
        public Collection<String> getPrefixes(String namespaceURI) throws NamespaceException
        {
            return null;
        }

        @Override
        public Collection<String> getPrefixes()
        {
            return null;
        }

        @Override
        public Collection<String> getURIs()
        {
            return null;
        }

        @Override
        public void addURI(String uri)
        {
        }

        @Override
        public void removeURI(String uri)
        {
        }

        @Override
        public void addPrefix(String prefix, String uri)
        {
        }

        @Override
        public void removePrefix(String prefix)
        {
        }

        @Override
        public void init()
        {
        }

        @Override
        public void afterDictionaryInit()
        {
        }

        @Override
        public void destroy()
        {
        }

        @Override
        public void registerDictionary(DictionaryDAO dictionaryDAO)
        {
        }
    };
}
