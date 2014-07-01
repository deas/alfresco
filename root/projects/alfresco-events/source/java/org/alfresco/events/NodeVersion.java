/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events;

import java.util.Date;

/**
 * Node version bean.
 * 
 * @author steveglover
 *
 */
public class NodeVersion
{
	private String versionedId;
	private String versionId;
	private Date modifiedAt;
	private String modifiedBy;
	private String versionLabel;
	private VersionType versionType;
	private String description;
	private String siteId;
	
    public NodeVersion(String siteId, String versionedId, String versionId,
			Date modifiedAt, String modifiedBy, String versionLabel,
			VersionType versionType, String description)
	{
		super();
		this.versionedId = versionedId;
		this.versionId = versionId;
		this.modifiedAt = modifiedAt;
		this.modifiedBy = modifiedBy;
		this.versionLabel = versionLabel;
		this.versionType = versionType;
		this.description = description;
	}

	public String getSiteId()
	{
		return siteId;
	}

	public Date getModifiedAt()
    {
    	return modifiedAt;
    }
    
    public String getModifiedBy()
    {
    	return modifiedBy;
    }
    
    public String getVersionLabel()
    {
        return versionLabel;
    }    
    
    public VersionType getVersionType()
    {
        return versionType;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getVersionedId()
    {
        return versionedId;
    }
    
    public String getVersionId()
    {
        return versionId;
    }
}
