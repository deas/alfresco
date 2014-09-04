/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

import java.io.Serializable;
import java.util.UUID;

/**
 * Abstract event bean.
 * 
 * @author steveglover
 *
 */
public abstract class EventImpl implements Event, Serializable
{
	private static final long serialVersionUID = 5491215044908832482L;

	protected String id; // event id
	protected String type;
    protected String username;

	// Event (creation) timestamp. Note separate timestamp field (even though Mongo stores timestamp in the object id, it is not a very
	// accurate timestamp)
	protected Long timestamp;

	public EventImpl()
	{
	}

	public EventImpl(String type, long timestamp, String username)
	{
		this.type = type;
		this.timestamp = timestamp;
		this.id = UUID.randomUUID().toString();
		this.username = username;
	}
	
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
	{
        return username;
    }

    public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setTimestamp(Long timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getType()
	{
		return type;
	}

	public Long getTimestamp()
	{
		return timestamp;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (id == null) {
			if (other.getId() != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;
		return true;
	}
	
}
