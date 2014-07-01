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
 * Node add comment event.
 * 
 * @author steveglover
 *
 */
public class NodeCommentedEvent extends NodeEvent
{
	private static final long serialVersionUID = 8925467617685945150L;

	public static final String EVENT_TYPE = "NODECOMMENTED";

	private String comment;

	public NodeCommentedEvent()
	{
	}

	public NodeCommentedEvent(long seqNumber, String name, String txnId, long time, String networkId, String siteId,
			String nodeId, String nodeType, List<String> paths, List<List<String>> pathNodeIds, String userId,
			Long modificationTime, String comment, Client client, Set<String> aspects, Map<String, Serializable> properties)
	{
		super(seqNumber, name, EVENT_TYPE, txnId, time, networkId, siteId, nodeId, nodeType, paths, pathNodeIds,
				userId, modificationTime, client, aspects, properties);
		this.comment = comment;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}
}
