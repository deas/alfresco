/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.util.FileFilterMode.Client;

/**
 * Node moved/renamed event.
 * 
 * @author steveglover
 *
 */
public class NodeMovedEvent extends NodeEvent
{
	private static final long serialVersionUID = -1850974872159620997L;
	
	public static final String FIELD_OLD_PARENT_NODE_ID = "oldParentNodeId";
	public static final String FIELD_NEW_PARENT_NODE_ID = "newParentNodeId";
	public static final String FIELD_OLD_PATHS = "oldPaths";

	public static final String EVENT_TYPE = "NODEMOVED";

	private String newName;
	private List<String> toPaths;
    private List<List<String>> toParentNodeIds;

	public NodeMovedEvent()
	{
	}
	
	public NodeMovedEvent(long seqNumber, String oldName, String newName, String txnId, long time, String networkId, String siteId,
			String nodeId, String nodeType, List<String> paths, List<List<String>> parentNodeIds, String userId,
			Long modificationTime, List<String> toPaths, List<List<String>> toParentNodeIds, Client client,
			Set<String> aspects, Map<String, Serializable> properties)
	{
		super(seqNumber, oldName, EVENT_TYPE, txnId, time, networkId, siteId, nodeId, nodeType, paths, parentNodeIds,
				userId, modificationTime, client, aspects, properties);
		this.newName = newName;
		this.toPaths = toPaths;
		this.toParentNodeIds = toParentNodeIds;
	}

	public String getNewName()
	{
        return newName;
    }

    public void setNewName(String newName)
    {
        this.newName = newName;
    }

	public List<String> getToPaths()
	{
		return toPaths;
	}

	public void setToPaths(List<String> toPaths)
	{
		this.toPaths = toPaths;
	}
	
	public List<List<String>> getToParentNodeIds()
	{
        return toParentNodeIds;
    }

    public void setToParentNodeIds(List<List<String>> toParentNodeIds)
    {
        this.toParentNodeIds = toParentNodeIds;
    }

    @Override
	public String toString()
	{
		return "NodeMovedEvent [toPaths="
				+ toPaths + ", nodeModificationTime=" + nodeModificationTime
				+ ", parentNodeIds=" + parentNodeIds
				+ ", toParentNodeIds=" + toParentNodeIds
				+ ", nodeId=" + nodeId + ", siteId=" + siteId + ", username="
				+ username + ", networkId=" + networkId + ", paths=" + paths
				+ ", nodeType=" + nodeType + ", id=" + id + ", type=" + type
				+ ", txnId=" + txnId + ", timestamp=" + timestamp + "]";
	}

}
