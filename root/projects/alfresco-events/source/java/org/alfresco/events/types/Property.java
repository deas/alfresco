/*
 * Copyright 2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.events.types;

import java.io.Serializable;

/**
 * Representation of a node property.
 * 
 * @author sglover
 *
 */
public class Property implements Serializable
{
	private static final long serialVersionUID = 5525793471683984872L;

	private String name;
	private Serializable value;
	private DataType dataType;

	public Property()
	{
	}

	public Property(String name, Serializable value)
	{
		super();
		this.name = name;
		this.value = value;
	}

	public Property(String name, Serializable value, DataType dataType)
	{
		super();
		this.name = name;
		this.dataType = dataType;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Serializable getValue()
	{
		return value;
	}

	public void setValue(Serializable value)
	{
		this.value = value;
	}

	public DataType getDataType()
	{
		return dataType;
	}

	public void setDataType(DataType dataType)
	{
		this.dataType = dataType;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Property other = (Property) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Property [name=" + name + ", value=" + value + ", dataType=" + dataType
				+ "]";
	}

}
