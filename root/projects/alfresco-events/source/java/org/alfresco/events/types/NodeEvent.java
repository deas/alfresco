/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.util.FileFilterMode.Client;

/**
 * A node event.
 * 
 * @author steveglover
 *
 */
public class NodeEvent extends BasicNodeEventImpl implements Serializable, NodeInfoEvent, TransactionOrderingAware
{
	private static final long serialVersionUID = 1632258418479600707L;

	protected Long nodeModificationTime;
	protected List<String> paths; // all paths, first one is the primary
	protected List<List<String>> parentNodeIds; // all paths, first one is the primary
	protected Set<String> aspects;
	protected Map<String, Serializable> properties = new HashMap<String, Serializable>();

	// Seq number relative to the transaction in which the event occurs
	protected Long seqNumber;
	
    // TODO checksum?
	// TODO changeId?

	public NodeEvent()
	{
	}

	@SuppressWarnings("unchecked")
	public NodeEvent(long seqNumber, String name, String type, String txnId, long timestamp, String networkId, String siteId, 
                   String nodeId, String nodeType, List<String> paths, List<List<String>> parentNodeIds, String username,
                   Long nodeModificationTime, Client client, Set<String> aspects, Map<String, Serializable> properties)
    {
         super(type, txnId, networkId,timestamp,username, nodeId,  siteId, nodeType, name, client);
         this.seqNumber = seqNumber;
         this.paths = (List<String>) (paths==null?Collections.emptyList():Collections.unmodifiableList(paths));
         this.parentNodeIds = (List<List<String>>) (parentNodeIds==null?Collections.emptyList():Collections.unmodifiableList(parentNodeIds));
         this.nodeModificationTime = nodeModificationTime;
         this.aspects = (Set<String>)(aspects==null?Collections.emptySet():Collections.unmodifiableSet(aspects));
         this.properties =  (Map<String, Serializable>)(properties==null?Collections.emptyMap():Collections.unmodifiableMap(properties));
    }

	public Set<String> getAspects()
	{
		return aspects;
	}

	public void setAspects(Set<String> aspects)
	{
		this.aspects = aspects;
	}

	public Map<String, Serializable> getProperties()
	{
		return properties;
	}

	public void setProperties(Map<String, Serializable> properties)
	{
		this.properties = properties;
	}

    public Long getNodeModificationTime()
	{
		return nodeModificationTime;
	}

	public void setNodeModificationTime(Long nodeModificationTime)
	{
		this.nodeModificationTime = nodeModificationTime;
	}

	/*
     * @see org.alfresco.events.types.NodeInfoEvent#getPaths()
     */
	@Override
    public List<String> getPaths()
	{
		return paths;
	}

	public void setPaths(List<String> paths)
	{
		this.paths = paths;
	}

	public void setNetworkId(String networkId)
	{
		this.networkId = networkId;
	}

	public String getNetworkId()
	{
		return networkId;
	}
	
	/*
     * @see org.alfresco.events.types.NodeInfoEvent#getParentNodeIds()
     */
	@Override
    public List<List<String>> getParentNodeIds()
	{
        return parentNodeIds;
    }

    public void setParentNodeIds(List<List<String>> parentNodeIds)
    {
        this.parentNodeIds = parentNodeIds;
    }

    /*
     * @see org.alfresco.events.types.TransactionOrderingAware#getSeqNumber()
     */
    @Override
    public Long getSeqNumber()
    {
        return this.seqNumber;
    }

    public void setSeqNumber(Long seqNumber)
    {
        this.seqNumber = seqNumber;
    }

    @Override
    public String toString()
    {
        return "NodeEvent [name=" + name + ", nodeModificationTime=" + nodeModificationTime + ", nodeId="
                + getNodeId() + ", siteId=" + getSiteId() + ", username=" + username
                + ", networkId=" + networkId + ", paths=" + paths + ", nodeType="
                + ", seqNumber=" + seqNumber + ", parentNodeIds=" + parentNodeIds 
                + getNodeType() + ", type=" + type + ", txnId=" + txnId + ", timestamp="
                + timestamp + "]";
    }

}
