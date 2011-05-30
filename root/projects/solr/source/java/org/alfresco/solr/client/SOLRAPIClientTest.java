package org.alfresco.solr.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.solr.AlfrescoSolrDataModel;

public class SOLRAPIClientTest extends TestCase
{
    private SOLRAPIClient client;
    
    @Override
    public void setUp() throws Exception
    {
        AlfrescoSolrDataModel model = AlfrescoSolrDataModel.getInstance("test");
        // dummy implementation - don't know how to hook into Alfresco namespace stuff
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
    
    private void outputTextContent(SOLRAPIClient.Response response) throws IOException
    {
        InputStream in = response.getContentAsStream();
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

    public void testGetTextContent()
    {
        try
        {
            SOLRAPIClient.Response response = client.getTextContent(Long.valueOf(35617l), null, null);
            System.out.println("Status = " + response.getStatus());
            System.out.println("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);

            // test cache
            Long modifiedSince = System.currentTimeMillis();
            response = client.getTextContent(Long.valueOf(35617l), null, modifiedSince);
            System.out.println("Status = " + response.getStatus());
            System.out.println("Request took " + response.getRequestDuration() + " ms");
            
            response = client.getTextContent(Long.valueOf(35618l), null, null);
            System.out.println("Status = " + response.getStatus());
            System.out.println("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);
            
            response = client.getTextContent(Long.valueOf(35619l), null, null);
            System.out.println("Status = " + response.getStatus());
            System.out.println("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);
            
            response = client.getTextContent(Long.valueOf(35620l), null, null);
            System.out.println("Status = " + response.getStatus());
            System.out.println("Request took " + response.getRequestDuration() + " ms");
            outputTextContent(response);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
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
