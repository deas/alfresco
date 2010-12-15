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
/**
 * Information about a new deployment.
 * @author mrogers
 *
 */
public class DeploymentTokenImpl implements Serializable, DeploymentToken
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6013271259272921382L;

	private String ticket;
	
	private TargetStatus targetStatus;
	
	public void setTicket(String ticket) 
	{
		this.ticket = ticket;
	}

	public String getTicket() 
	{
		return ticket;
	}

	public TargetStatus getTargetStatus()
	{
		return targetStatus;
		
	}
	
	public void setTargetStatus(TargetStatus info)
	{
		this.targetStatus = info;
	}
}
