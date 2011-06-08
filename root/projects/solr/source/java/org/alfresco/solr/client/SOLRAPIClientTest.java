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

import junit.framework.TestCase;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.solr.AlfrescoSolrDataModel;
import org.json.JSONException;

/**
 * Tests {@link SOLRAPIClient}
 * 
 * @since 4.0
 */
public class SOLRAPIClientTest extends TestCase
{
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
            System.out.println("Loading ..."+bootstrapModel);
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
                "http://localhost:8080/alfresco/service", "admin", "admin");
    }
    
    public void testGetTransactions()
    {
        try
        {
            // get transactions starting from txn id 1298288417234l
            List<Transaction> transactions = client.getTransactions(Long.valueOf(1298288417234l), null, 5);

            // get transactions starting from transaction 426
            transactions = client.getTransactions(null, Long.valueOf(1), 5);
            List<Long> transactionIds = new ArrayList<Long>(transactions.size());
            for(Transaction info : transactions)
            {
                System.out.println(info);
                transactionIds.add(Long.valueOf(info.getId()));
            }

            // get the first 3 nodes in those transactions
            GetNodesParameters params = new GetNodesParameters();
            params.setTransactionIds(transactionIds);
            List<Node> nodes = client.getNodes(params, 5);
            for(Node info : nodes)
            {
                System.out.println(info);
            }

            // get the next 3 nodes in those transactions i.e. starting from the last node id (inclusive)
            params = new GetNodesParameters();
            params.setTransactionIds(transactionIds);
            params.setFromNodeId(nodes.get(nodes.size() - 1).getId());
            nodes = client.getNodes(params, 3);
            List<Long> nodeIds = new ArrayList<Long>(nodes.size());
            for(Node info : nodes)
            {
                System.out.println(info);
                nodeIds.add(info.getId());
            }
            
            NodeMetaDataParameters metaParams = new NodeMetaDataParameters();
            metaParams.setNodeIds(nodeIds);
            List<NodeMetaData> metadata = client.getNodesMetaData(metaParams, 3);
            for(NodeMetaData info : metadata)
            {
                System.out.println(info);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
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
            System.out.println(info);
        }
        
        metaParams = new NodeMetaDataParameters();
        nodeIds = new ArrayList<Long>(1);
        nodeIds.add(9l);
        metaParams.setNodeIds(nodeIds);
        metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            System.out.println(info);
        }
        
        metaParams = new NodeMetaDataParameters();
        nodeIds = new ArrayList<Long>(1);
        nodeIds.add(19l);
        metaParams.setNodeIds(nodeIds);
        metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            System.out.println(info);
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
            System.out.println(info);
        }
        
        // content with tags
        metaParams = new NodeMetaDataParameters();
        nodeIds = new ArrayList<Long>(1);
        nodeIds.add(49431l);
        metaParams.setNodeIds(nodeIds);
        metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            System.out.println(info);
            MultiPropertyValue multi = (MultiPropertyValue)info.getProperties().get(QName.createQName("{http://www.alfresco.org/model/content/1.0}taggable"));
            for(PropertyValue propValue : multi.getValues())
            {
                System.out.println("multi property values = " + propValue);
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
            System.out.println(info);
        }
        
        // content with accented characters in title properties
        metaParams = new NodeMetaDataParameters();
        nodeIds = new ArrayList<Long>(1);
        nodeIds.add(117678l);
        metaParams.setNodeIds(nodeIds);
        metadata = client.getNodesMetaData(metaParams, 3);
        for(NodeMetaData info : metadata)
        {
            System.out.println(info);
        }
    }

    private void outputTextContent(SOLRAPIClient.GetTextContentResponse response) throws IOException
    {
        InputStream in = response.getContent();
        if(in != null)
        {
            System.out.println("Text content:");

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = null;
            while((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }
        }
    }

/*    public void testGetTextContent()
    {
        try
        {
            SOLRAPIClient.GetTextContentResponse response = client.getTextContent(Long.valueOf(35617l), null, null);
            System.out.println("Status = " + response.getStatus());
            System.out.println("Transform Status = " + response.getTransformStatus());
            System.out.println("Transform Exception = " + response.getTransformException());
            System.out.println("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);

            // test cache
            Long modifiedSince = System.currentTimeMillis();
            response = client.getTextContent(Long.valueOf(35617l), null, modifiedSince);
            System.out.println("Status = " + response.getStatus());
            System.out.println("Transform Status = " + response.getTransformStatus());
            System.out.println("Transform Exception = " + response.getTransformException());
            System.out.println("Request took " + response.getRequestDuration() + " ms");
            
            response = client.getTextContent(Long.valueOf(35618l), null, null);
            System.out.println("Status = " + response.getStatus());
            System.out.println("Transform Status = " + response.getTransformStatus());
            System.out.println("Transform Exception = " + response.getTransformException());
            System.out.println("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);
            
            response = client.getTextContent(Long.valueOf(35619l), null, null);
            System.out.println("Status = " + response.getStatus());
            System.out.println("Transform Status = " + response.getTransformStatus());
            System.out.println("Transform Exception = " + response.getTransformException());
            System.out.println("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);
            
            response = client.getTextContent(Long.valueOf(35620l), null, null);
            System.out.println("Status = " + response.getStatus());
            System.out.println("Transform Status = " + response.getTransformStatus());
            System.out.println("Transform Exception = " + response.getTransformException());
            System.out.println("Request took " + response.getRequestDuration() + " ms");
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
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Collection<String> getPrefixes()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Collection<String> getURIs()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void addURI(String uri)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void removeURI(String uri)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void addPrefix(String prefix, String uri)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void removePrefix(String prefix)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void init()
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void afterDictionaryInit()
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void destroy()
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void registerDictionary(DictionaryDAO dictionaryDAO)
        {
            // TODO Auto-generated method stub
            
        }
        
    };
}
