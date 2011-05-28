package org.alfresco.solr.client;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.alfresco.solr.AlfrescoSolrDataModel;

public class SOLRAPIClientTest extends TestCase
{
    private SOLRAPIClient client;
    
    @Override
    public void setUp() throws Exception
    {
        client = new SOLRAPIClient(AlfrescoSolrDataModel.getInstance("test").getDictionaryService(),
                "http://localhost:8080/alfresco/service", "admin", "admin");
    }
    
    public void testGetTransactions()
    {
        try
        {
            // get transactions starting from txn id 1298288417234l
            List<Transaction> transactions = client.getTransactions(Long.valueOf(1298288417234l), null, 5);

            // get transactions starting from transaction 1
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
            List<Node> nodes = client.getNodes(params, 3);
            for(Node info : nodes)
            {
                System.out.println(info);
            }

            // get the next 3 nodes in those transactions i.e. starting from the last node id (inclusive)
            params = new GetNodesParameters();
            params.setTransactionIds(transactionIds);
            params.setFromNodeId(nodes.get(nodes.size() - 1).getId());
            nodes = client.getNodes(params, 3);
            for(Node info : nodes)
            {
                System.out.println(info);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
