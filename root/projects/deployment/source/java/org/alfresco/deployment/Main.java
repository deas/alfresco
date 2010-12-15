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

import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Main class for Deployment Receiver.
 * @author britt
 */
public class Main
{
	 private static Log logger = LogFactory.getLog(Main.class);
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.err.println("Usage: org.alfresco.deployment.Main application-context.xml");
            System.exit(1);
        }

        try 
        {
        	logger.info("Alfresco Deployment Receiver Starting");
        	@SuppressWarnings("unused")
        	FileSystemXmlApplicationContext context =
        		new FileSystemXmlApplicationContext(args[0]);
        }
        catch (Exception e)
        {
        	logger.error("Unable to start deployment receiver", e);
        	System.err.println("Unable to start deployment receiver");
        	e.printStackTrace();
        }
    }
}
