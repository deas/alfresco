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
 * An event that occurs when using a web browser.
 * This usually takes place within a Share site (but Site information isn't mandatory).
 *
 * @author Gethin James
 */
public class BrowserEvent extends EventImpl implements ClientEvent, SiteEvent
{
    private static final long serialVersionUID = 382560004647243948L;
    public static final String BROWSER_EVENT_TYPE = "browser";
    
    private final String siteId;
    private final String txnId;
    private final String networkId; // network/tenant
    
    private final String component; //aka Page
    private final String action;
    private final String agent;
    private final String attributes; //json map of additional attributes

    /**
     * Constructor for BrowserEvent
     * @param username - the user
     * @param networkId - network/tenant
     * @param siteId
     * @param txnId
     * @param component - page eg. "documentdetails"
     * @param action - eg. "view"
     * @param agent - browser user agent
     * @param attributes - optional additional attributes as a json map eg. {"liked":"true"}
     */
    public BrowserEvent(String username, String networkId, String txnId, String siteId,
                String component, String action, String agent, String attributes)
    {
        super(BROWSER_EVENT_TYPE, new Date().getTime(), username);
        this.siteId = siteId;
        this.txnId = txnId;
        this.networkId = networkId;
        this.component = component;
        this.action = action;
        this.agent = agent;
        this.attributes = attributes;
    }
    
    @Override
    public Client getClient()
    {
        return Client.webclient;
    }

    @Override
    public String getSiteId()
    {
        return this.siteId;
    }

    @Override
    public String getTxnId()
    {
        return this.txnId;
    }

    @Override
    public String getNetworkId()
    {
        return this.networkId;
    }

    public String getComponent()
    {
        return this.component;
    }

    public String getAction()
    {
        return this.action;
    }

    public String getAgent()
    {
        return this.agent;
    }

    public String getAttributes()
    {
        return this.attributes;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BrowserEvent [id=").append(this.id).append(", type=").append(this.type)
                    .append(", username=").append(this.username).append(", timestamp=")
                    .append(this.timestamp).append(", siteId=").append(this.siteId)
                    .append(", txnId=").append(this.txnId).append(", networkId=")
                    .append(this.networkId).append(", component=").append(this.component).append(", action=")
                    .append(this.action).append(", agent=").append(this.agent)
                    .append(", attributes=").append(this.attributes).append("]");
        return builder.toString();
    }
}
