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
package org.alfresco.deployment.impl.fsr;

import org.alfresco.deployment.FSDeploymentRunnable;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.impl.server.Deployment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test class for testing FSRunnables.
 * 
 *  Sets a boolean flag to say that its been called.
 *  
 *  Can throw an exception when told to do so.
 */
public class FSRunnableTester implements FSDeploymentRunnable
{
	private static Log logger = LogFactory.getLog(FSRunnableTester.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5780190885270319744L;
	
	Deployment deployment;
	private boolean runCalled = false;
	private boolean throwException;
	
	
	public void init(Deployment deployment) 
	{
		this.deployment = deployment;	
	}

	public void run() 
	{
		logger.debug("called run");
		setRunCalled(true);
		
		if(isThrowException())
		{
			logger.debug("throwing exception");
			throw new DeploymentException("test exception");
		}
	}
	
	public Deployment getDeployment()
	{
		return deployment;
	}
	
	public boolean isRunCalled()
	{
		return runCalled;
	}

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

	public boolean isThrowException() {
		return throwException;
	}

	public void setRunCalled(boolean runCalled) {
		this.runCalled = runCalled;
	}
}
