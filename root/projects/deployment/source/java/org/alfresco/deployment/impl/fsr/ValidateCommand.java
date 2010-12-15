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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ValidateCommand implements Runnable
{
	public FileSystemDeploymentTarget target;
	
    /**
     * The logger for this target
     */
    private static Log logger = LogFactory.getLog(ValidateCommand.class);
	
	public ValidateCommand(FileSystemDeploymentTarget target)
	{
		this.target = target;
	}

	public void run() 
	{
      	try 
    	{
    		synchronized (target)
    		{
    			// Now we hold the lock for the target - which will prevent new deployments beginning.
    			if(target.isBusy())
    			{
    				// do no validation there is a deployment in progress
    				logger.warn("target is busy. Not validating target:" + target.getName());
    			}
    			else
    			{
    				logger.info("Validation starting for target:" + target.getName() );
    				target.validate();
    				logger.info("Validation finished");
    			}
    		}
    	}
    	catch (Exception e)
    	{
    		logger.error("Unable to validate", e);
    	}
	}
}
