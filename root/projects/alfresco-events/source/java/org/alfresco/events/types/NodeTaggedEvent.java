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
 * Node tagged event.
 * 
 * @author steveglover
 *
 */
public class NodeTaggedEvent extends NodeEvent
{
	private static final long serialVersionUID = 2387114992479068002L;

	public static final String EVENT_TYPE = "NODETAGGED";

	private String tag;

	public NodeTaggedEvent()
	{
	}

	public NodeTaggedEvent(long seqNumber, String name, String txnId, long time, String networkId, String siteId,
			String nodeId, String nodeType, List<String> paths, List<List<String>> pathNodeIds,
			String userId, Long modificationTime, String tag, Client client, Set<String> aspects,
			Map<String, Serializable> properties)
	{
		super(seqNumber, name, EVENT_TYPE, txnId, time, networkId, siteId, nodeId, nodeType, paths, pathNodeIds, userId,
				modificationTime, client, aspects, properties);
		this.tag = tag;
	}

	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}
	
}
