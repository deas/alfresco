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
 * An event raised by using the {@link ActivityService}
 *
 * @author Gethin James
 * @since 5.0
 */
public class ActivityEvent extends ContentEventImpl
{
    public ActivityEvent()
    {
        super();
        activityData = null;
    }

    public ActivityEvent(String type, String username, String networkId, long timestamp,
                String txnId, String nodeId, String siteId, String nodeType, Client client,
                String name, String mimeType, long size, String encoding)
    {
        super(type, username, networkId, timestamp, txnId, nodeId, siteId, nodeType, client, name,
                    mimeType, size, encoding);
        activityData = null;
    }

    public ActivityEvent(String type, String username, String networkId, String txnId,
                String nodeId, String siteId, String nodeType, Client client, String name,
                String mimeType, long size, String encoding)
    {
        super(type, username, networkId, txnId, nodeId, siteId, nodeType, client, name, mimeType, size,
                    encoding);
        activityData = null;
    }

    public static final String ACTIVITY_TYPE = "actvity.";
    private static final long serialVersionUID = -8101613202921138060L;
    private final transient String activityData;
    
    public ActivityEvent(String type, String txnId, String networkId, String username, 
                String nodeId, String siteId, String nodeType, Client client,
                String activityData, String name, String mimeType, long size, String encoding)
    {
        super(ACTIVITY_TYPE+type, username, networkId, txnId, nodeId, siteId, nodeType, client, name, mimeType, size, encoding);
        this.activityData = activityData;
    }
    
    public ActivityEvent(String type, String txnId, String networkId, long timestamp, String username, 
                String nodeId, String siteId, String nodeType, Client client,
                String activityData, String name, String mimeType, long size, String encoding)
    {
        super(ACTIVITY_TYPE+type, username, networkId, timestamp, txnId, nodeId, siteId, nodeType, client, name, mimeType, size, encoding);
        this.activityData = activityData;
    }

    public String getActivityData()
    {
        return this.activityData;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ActivityEvent [id=").append(this.id).append(", type=").append(this.type)
                    .append(", username=").append(this.username).append(", timestamp=")
                    .append(this.timestamp).append(", networkId=").append(this.networkId)
                    .append(", nodeId=").append(this.nodeId).append(", txnId=").append(this.txnId)
                    .append(", siteId=").append(this.siteId).append(", nodeType=")
                    .append(this.nodeType).append(", client=").append(this.client)
                    .append(", name=").append(this.name)
                    .append(", mimeType=").append(this.mimeType).append(", size=")
                    .append(this.size).append(", encoding=").append(this.encoding)
                    .append(", activityData=").append(this.activityData).append("]");
        return builder.toString();
    }

}
