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

import java.util.Map;

import org.alfresco.deployment.DeploymentTarget;

public interface DeploymentTargetRegistry 
{
	/**
	 * Register a new deployment target.  If an entry with the old name 
	 * already exists then the new value replaces the old value. 
	 * @param name the name of the target
	 * @param target the implementation of the target
	 */
	public void registerTarget(String name, DeploymentTarget target);

	
	/**
	 * Unregister a deployment target
	 * @param name the name of the target
	 */
	public void unregisterTarget(String name);

	
	/**
	 * Get the targets for this deployment engine.
	 * @return the targets for this deployment engine
	 */
	public Map<String, DeploymentTarget> getTargets(); 



}
