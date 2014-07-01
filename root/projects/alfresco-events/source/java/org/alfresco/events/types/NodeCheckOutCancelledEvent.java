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
 * Node checked out event.
 * 
 * @author steveglover
 *
 */
public class NodeCheckOutCancelledEvent extends NodeEvent
{
	private static final long serialVersionUID = 1854376131938754638L;

	public static final String EVENT_TYPE = "NODECHECKOUTCANCELLED";

	public NodeCheckOutCancelledEvent()
	{
	}

	public NodeCheckOutCancelledEvent(long seqNumber, String name, String txnId, long time, String networkId, String siteId, 
			String nodeId, String nodeType, List<String> paths, List<List<String>> pathNodeIds, String userId,
			Long modificationTime, Client client, Set<String> aspects, Map<String, Serializable> properties)
	{
		super(seqNumber, name, EVENT_TYPE, txnId, time, networkId, siteId, nodeId, nodeType, paths, pathNodeIds, userId,
				modificationTime, client, aspects, properties);
	}

	@Override
	public String toString()
	{
		return "NodeCheckOutCancelledEvent [name=" + name
				+ ", nodeModificationTime=" + nodeModificationTime
				+ ", nodeId=" + nodeId + ", siteId=" + siteId + ", paths="
				+ paths + ", parentNodeIds=" + parentNodeIds + ", nodeType="
				+ nodeType + ", client=" + client + ", aspects=" + aspects
				+ ", properties=" + properties + ", txnId=" + txnId
				+ ", networkId=" + networkId + ", id=" + id + ", type=" + type
				+ ", username=" + username + ", timestamp=" + timestamp + "]";
	}
}
