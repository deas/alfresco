/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

import org.alfresco.util.FileFilterMode.Client;

/**
 * Occurs on syncing of data between two Alfresco nodes.
 * eg. to and from Alfresco cloud.
 *
 * @author Gethin James
 */
public class SyncEvent extends ContentEventImpl implements ContentEvent
{
    private static final long serialVersionUID = 4975456813594912890L;

    protected String remoteNodeId; // remote node id (guid)
    protected String remoteNetwork; //cloudNetwork
    protected String syncSetGUID;
    
    public SyncEvent()
    {
        super();
    }
    
    public SyncEvent(String type, String username, String networkId, String txnId, String nodeId,
                String siteId, String nodeType, Client client, String name, String mimeType, long size,
                String encoding, String remoteNodeId, String remoteNetwork, String syncSetGUID)
    {
        super(type, username, networkId, txnId, nodeId, siteId, nodeType, client, name, mimeType, size, encoding);
        this.remoteNodeId = remoteNodeId;
        this.remoteNetwork = remoteNetwork;
        this.syncSetGUID = syncSetGUID;
    }
    
    public String getRemoteNodeId()
    {
        return this.remoteNodeId;
    }
    public String getRemoteNetwork()
    {
        return this.remoteNetwork;
    }
    public String getSyncSetGUID()
    {
        return this.syncSetGUID;
    }

    public void setRemoteNodeId(String remoteNodeId)
    {
        this.remoteNodeId = remoteNodeId;
    }

    public void setRemoteNetwork(String remoteNetwork)
    {
        this.remoteNetwork = remoteNetwork;
    }

    public void setSyncSetGUID(String syncSetGUID)
    {
        this.syncSetGUID = syncSetGUID;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SyncEvent [id=").append(this.id).append(", type=").append(this.type)
                    .append(", username=").append(this.username).append(", timestamp=")
                    .append(this.timestamp).append(", networkId=").append(this.networkId)
                    .append(", txnId=").append(this.txnId).append(", client=").append(this.client)
                    .append(", siteId=").append(this.siteId).append(", nodeId=")
                    .append(this.nodeId).append(", nodeType=").append(this.nodeType)
                    .append(", encoding=").append(this.encoding).append(", size=")
                    .append(this.size).append(", mimeType=").append(this.mimeType)
                    .append(", remoteNodeId=").append(this.remoteNodeId).append(", remoteNetwork=")
                    .append(this.remoteNetwork).append(", syncSetGUID=").append(this.syncSetGUID)
                    .append("]");
        return builder.toString();
    }
}
