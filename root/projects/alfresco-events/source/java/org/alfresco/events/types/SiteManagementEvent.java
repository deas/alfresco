/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

/**
 * An event that occurs when managing an Alfresco Site, e.g Site creation
 * 
 * @author Gethin James
 */
public class SiteManagementEvent extends RepositoryEventImpl implements SiteEvent
{
    private static final long serialVersionUID = -7387933680171703729L;
    
    private String siteId;
    private String title;

    private String description;
    private String visibility;
    private String sitePreset;
    
    public SiteManagementEvent()
    {
        super();
    }

    public SiteManagementEvent(String type, String txnId, String networkId, long timestamp,
                String username, String siteId, String title, String description,
                String visibility, String sitePreset)
    {
        super(type, txnId, networkId, timestamp, username);
        this.siteId = siteId;
        this.title = title;
        this.description = description;
        this.visibility = visibility;
        this.sitePreset = sitePreset;
    }

    public String getTitle()
    {
        return this.title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getDescription()
    {
        return this.description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getVisibility()
    {
        return this.visibility;
    }
    public void setVisibility(String visibility)
    {
        this.visibility = visibility;
    }
    public String getSitePreset()
    {
        return this.sitePreset;
    }
    public void setSitePreset(String sitePreset)
    {
        this.sitePreset = sitePreset;
    }
    public String getSiteId()
    {
        return this.siteId;
    }
    public void setSiteId(String siteId)
    {
        this.siteId = siteId;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SiteManagementEvent [id=").append(this.id).append(", txnId=")
                    .append(this.txnId).append(", networkId=").append(this.networkId)
                    .append(", type=").append(this.type).append(", username=")
                    .append(this.username).append(", timestamp=").append(this.timestamp)
                    .append(", siteId=").append(this.siteId).append(", title=").append(this.title)
                    .append(", description=").append(this.description).append(", visibility=")
                    .append(this.visibility).append(", sitePreset=").append(this.sitePreset)
                    .append("]");
        return builder.toString();
    }
}
