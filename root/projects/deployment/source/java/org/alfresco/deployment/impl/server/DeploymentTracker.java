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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

import org.alfresco.deployment.DeploymentTarget;

/**
 * Class to track a deployment.
 * 
 * Contains a target
 * 
 * And a set of outputTokens.
 * 
 * @author mrogers
 *
 */
public class DeploymentTracker
{
	private DeploymentTarget target;
	
    /**
     * Timestamp of last time this deployment was talked to.
     */
    private Date lastActivity;
    
    private Date startDeployment;
    
    private Date endDeployment;
    
    /**
     * The state of this deployment with regards to the transaction.
     */
    private DeploymentState fState;
    
	private Set<String> tokens = Collections.synchronizedSet(new HashSet<String>());
	
	public DeploymentTracker(DeploymentTarget target)
	{
		lastActivity = new Date();
		startDeployment = new Date();
		this.target = target;
	}
	
	DeploymentTarget getTarget()
	{
		return target;
	}
	
	public void addToken(String token)
	{
		lastActivity = new Date();
		tokens.add(token);
	}
	
	public void removeToken(String token)
	{
		lastActivity = new Date();
		tokens.remove(token);
	}
	
	public Set<String> getTokens()
	{
		return tokens;
	}
	
	public void updateLastAccess()
	{
		lastActivity = new Date();
	}
}
