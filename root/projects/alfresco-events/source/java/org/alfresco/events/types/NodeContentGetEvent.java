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
 * Node content get event.
 * 
 * @author steveglover
 *
 */
public class NodeContentGetEvent extends NodeEvent
{
	private static final long serialVersionUID = 8034413766818328844L;

	public static final String EVENT_TYPE = "CONTENTGET";

	public NodeContentGetEvent()
	{
	}
	
	public NodeContentGetEvent(long seqNumber, String name, String txnId, long time, String networkId, String siteId,
			String nodeId, String nodeType, List<String> paths, List<List<String>> pathNodeIds, String userId,
			Long modificationTime, Client client, Set<String> aspects, Map<String, Serializable> properties)
	{
		super(seqNumber, name, EVENT_TYPE, txnId, time, networkId, siteId, nodeId, nodeType, paths, pathNodeIds,
		        userId, modificationTime, client, aspects, properties);
	}

	@Override
	public String toString()
	{
		return "NodeContentGetEvent [nodeModificationTime="
				+ nodeModificationTime + ", nodeId=" + nodeId + ", siteId="
				+ siteId + ", username=" + username + ", networkId=" + networkId
				+ ", paths=" + paths + ", nodeType=" + nodeType + ", id=" + id
				+ ", type=" + type + ", txnId=" + txnId + ", timestamp="
				+ timestamp + "]";
	}
}
