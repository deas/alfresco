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

import org.alfresco.deployment.DeploymentTarget;
import org.alfresco.util.PropertyCheck;

/**
 * Utility class to register a deployment target with the deployment engine.
 */
public class DeploymentTargetRegistrationBean 
{
	private String name;
	
	private DeploymentTargetRegistry registry;
	
	private DeploymentTarget target;
	
	/**
	 * Register the deployment target with the deployment target registry
	 */
	public void register()
	{
		PropertyCheck.mandatory(this, "name", name);
		PropertyCheck.mandatory(this, "registry", registry);
		PropertyCheck.mandatory(this, "target", target);
		
		/**
		 * Go ahead and do the registration
		 */
		registry.registerTarget(name, target);
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void setRegistry(DeploymentTargetRegistry registry) 
	{
		this.registry = registry;
	}
	
	public DeploymentTargetRegistry getRegistry() 
	{
		return registry;
	}
	public void setTarget(DeploymentTarget target) 
	{
		this.target = target;
	}
	public DeploymentTarget getTarget() 
	{
		return target;
	}
}
