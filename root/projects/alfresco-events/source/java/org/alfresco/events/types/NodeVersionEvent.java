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

import org.alfresco.events.NodeVersion;
import org.alfresco.util.FileFilterMode.Client;

/**
 * Node versioned events.
 * 
 * @author steveglover
 *
 */
public class NodeVersionEvent extends NodeEvent
{
	private static final long serialVersionUID = -2382018139356315699L;

	public static final String EVENT_TYPE = "NODEVERSIONED";

	private NodeVersion version;
	
	public NodeVersionEvent(long seqNumber, String name, String txnId, long time, String networkId, String siteId, String nodeId, String nodeType,
			List<String> paths, List<List<String>> pathNodeIds, String userId, Long modificationTime, NodeVersion version,
			Client client, Set<String> aspects, Map<String, Serializable> properties)
	{
		super(seqNumber, name, EVENT_TYPE, txnId, time, networkId, siteId, nodeId, nodeType, paths, pathNodeIds, userId,
				modificationTime, client, aspects, properties);
		this.version = version;
	}

	public NodeVersion getVersion()
	{
		return version;
	}

	@Override
	public String toString()
	{
		return "NodeVersionEvent [version=" + version + ", nodeModificationTime="
				+ nodeModificationTime + ", nodeId=" + nodeId + ", siteId="
				+ siteId + ", username=" + username + ", networkId=" + networkId
				+ ", paths=" + paths + ", type=" + type + ", txnId=" + txnId
				+ ", timestamp=" + timestamp + "]";
	}
}
