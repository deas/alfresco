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
public class NodeRenamedEvent extends NodeEvent
{
	private static final long serialVersionUID = -1790415247801638709L;

	public static final String EVENT_TYPE = "NODERENAMED";

	private List<String> toPaths;
	private String newName;

	public NodeRenamedEvent()
	{
	}
	
	public NodeRenamedEvent(long seqNumber, String oldName, String newName, String txnId, long time, String networkId, String siteId,
			String nodeId, String nodeType, List<String> paths, List<List<String>> parentNodeIds, String userId,
			Long modificationTime, List<String> toPaths, Client client, Set<String> aspects, Map<String, Serializable> properties)
	{
		super(seqNumber, oldName, EVENT_TYPE, txnId, time, networkId, siteId, nodeId, nodeType, paths,
				parentNodeIds, userId, modificationTime, client, aspects, properties);
		this.toPaths = toPaths;
		this.newName = newName;
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

	@Override
	public String toString()
	{
		return "NodeRenamedEvent [toPaths=" + toPaths + ", newName=" + newName
				+ ", name=" + name + ", nodeModificationTime="
				+ nodeModificationTime + ", nodeId=" + nodeId + ", siteId="
				+ siteId + ", paths=" + paths + ", parentNodeIds="
				+ parentNodeIds + ", nodeType=" + nodeType + ", client="
				+ client + ", txnId=" + txnId + ", networkId=" + networkId
				+ ", id=" + id + ", type=" + type + ", username=" + username
				+ ", timestamp=" + timestamp + "]";
	}
}
