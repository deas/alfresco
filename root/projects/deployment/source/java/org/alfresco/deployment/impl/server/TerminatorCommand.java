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
package org.alfresco.deployment.impl.server;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alfresco.deployment.DeploymentReceiverTransport;

/**
 * The 
 * @author mrogers
 */
public class TerminatorCommand implements Runnable 
{
	DeploymentReceiverTransport transport;
	String ticket;
	String reason;
	Date queueTime = new Date();
	
	/**
	 * Wait at least 5 seconds 
	 */
	long delay = 5000;
	
	public TerminatorCommand(DeploymentReceiverTransport transport, String ticket, String reason)
	{
		this.ticket = ticket;
		this.reason = reason;
		this.transport = transport;		
		
	}
	
	public void run() 
	{
		// Make sure at least 5 seconds are up
		
		if(queueTime.getTime() + delay > new Date().getTime())
		{
			// Abort this deployment.
			try
			{
				transport.abort(ticket);
			} 
			catch (Exception e)
			{
				// do nothing
			}
		}
		else
		{
			//what to do here
		}
	}
}
