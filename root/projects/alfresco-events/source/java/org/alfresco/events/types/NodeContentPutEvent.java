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
 * Node content put/change event.
 * 
 * @author steveglover
 *
 */
public class NodeContentPutEvent extends NodeEvent implements ContentEvent
{
	private static final long serialVersionUID = -6657727955748547081L;
	
	public static final String EVENT_TYPE = "CONTENTPUT";

	public static final String FIELD_SIZE = "size";
	public static final String FIELD_MIME_TYPE = "mimeType";
	public static final String FIELD_ENCODING = "encoding";

	private long size;
	private String mimeType;
	private String encoding;

	public NodeContentPutEvent()
	{
	}
	
	public NodeContentPutEvent(long seqNumber, String name, String txnId, long time, String networkId, String siteId, String nodeId,
			String nodeType, List<String> paths, List<List<String>> pathNodeIds, String userId, Long modificationTime,
			long size, String mimeType, String encoding, Client client, Set<String> aspects, Map<String, Serializable> properties)
	{
		super(seqNumber, name, EVENT_TYPE, txnId, time, networkId, siteId, nodeId, nodeType,
				paths, pathNodeIds, userId, modificationTime, client, aspects, properties);
		this.size = size;
		this.mimeType = mimeType;
		this.encoding = encoding;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}

	public String getMimeType()
	{
		return mimeType;
	}

	public void setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	@Override
	public String toString()
	{
		return "NodeContentPutEvent [size=" + size + ", mimeType=" + mimeType
				+ ", encoding=" + encoding + ", nodeModificationTime="
				+ nodeModificationTime + ", nodeId=" + nodeId + ", siteId="
				+ siteId + ", username=" + username + ", networkId=" + networkId
				+ ", paths=" + paths + ", nodeType=" + nodeType + ", id=" + id
				+ ", type=" + type + ", txnId=" + txnId + ", timestamp="
				+ timestamp + "]";
	}
	
}
