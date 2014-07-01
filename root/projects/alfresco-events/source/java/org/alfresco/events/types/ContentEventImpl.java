/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

import java.util.Date;

import org.alfresco.util.FileFilterMode.Client;

/**
 * Occurs when content is read.
 *
 * @author Gethin James
 * @since 5.0
 */
public class ContentEventImpl extends BasicNodeEventImpl implements ContentEvent
{
    private static final long serialVersionUID = 6471232122343040380L;

    String mimeType;
    long size;
    String encoding;

    public ContentEventImpl()
    {
        super();
    }
    
    public ContentEventImpl(String type, String username, String networkId, String txnId,  String nodeId,
                            String siteId, String nodeType, Client client, String name, String mimeType, long size, String encoding)
    {
        super(type, txnId, networkId, new Date().getTime(), username, nodeId, siteId, nodeType, name, client);
        this.mimeType = mimeType;
        this.size = size;
        this.encoding = encoding;
    }

    public ContentEventImpl(String type, String username, String networkId, long timestamp, String txnId,  String nodeId,
                String siteId, String nodeType, Client client, String name, String mimeType, long size, String encoding)
    {
        super(type, txnId, networkId, timestamp, username, nodeId, siteId, nodeType, name, client);
        this.mimeType = mimeType;
        this.size = size;
        this.encoding = encoding;
    }
    @Override
    public String getMimeType()
    {
        return this.mimeType;
    }
    @Override
    public long getSize()
    {
        return this.size;
    }
    @Override
    public String getEncoding()
    {
        return this.encoding;
    }

    @Override
    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    @Override
    public void setSize(long size)
    {
        this.size = size;
    }

    @Override
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ContentReadEvent [id=").append(this.id).append(", type=").append(this.type)
               .append(", timestamp=").append(this.timestamp).append(", username=")
               .append(this.username).append(", client=").append(this.client)
               .append(", networkId=").append(this.networkId).append(", siteId=")
               .append(this.siteId).append(", txnId=").append(this.txnId).append(", nodeId=")
               .append(this.nodeId).append(", nodeType=").append(this.nodeType)
               .append(", name=").append(this.name)
               .append(", mimeType=").append(this.mimeType).append(", size=")
               .append(this.size).append(", encoding=").append(this.encoding).append("]");
        return builder.toString();
    }
}