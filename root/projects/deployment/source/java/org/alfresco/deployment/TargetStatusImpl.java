/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.deployment;

import java.io.Serializable;

public class TargetStatusImpl implements TargetStatus, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -451429708095640372L;
	private String targetName;
	private String storeName;
	
	private int currentVersion;
	
	public void setCurrentVersion(int currentVersion) 
	{
		this.currentVersion = currentVersion;
	}

	public int getCurrentVersion() 
	{
		return currentVersion;
	}

	public String getTargetName() 
	{
		return targetName;
	}
	
	public void setTargetName(String name)
	{
		this.targetName = name;	
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreName() {
		return storeName;
	}

}
