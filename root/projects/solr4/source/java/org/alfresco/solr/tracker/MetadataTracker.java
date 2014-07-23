package org.alfresco.solr.tracker;

import java.util.List;

import org.alfresco.solr.NodeReport;
import org.alfresco.solr.client.Node;

public class MetadataTracker implements Tracker
{

    @Override
    public void track()
    {
        // TODO Auto-generated method stub

    }

    public NodeReport checkNode(Long dbid)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public NodeReport checkNode(Node node)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Node> getFullNodesForDbTransaction(Long txid)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IndexHealthReport checkIndex(Long fromTx, Long toTx, Long fromAclTx, Long toAclTx, Long fromTime, Long toTime)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAlfrescoVersion()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
