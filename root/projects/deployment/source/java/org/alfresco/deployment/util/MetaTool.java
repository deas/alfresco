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
package org.alfresco.deployment.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.Set;

import org.alfresco.deployment.FileDescriptor;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.impl.server.DirectoryMetaData;

/**
 * Class for viewing meta data
 *
 * Usage MetaTool [filename]
 */
public class MetaTool {
	
		public static void main(String args[]) {
			
			try {
				
			if(args.length != 1)
			{
				System.out.println("Usage: MetaTool filename");
			}
				
			DirectoryMetaData meta = getDirectory(args[0]);
			
			Set<FileDescriptor> metaList = meta.getListing();
			
			for(FileDescriptor file : metaList)
			{	
				System.out.println("fileDescriptor : " + file);
			}
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
		
		}
		
	    /**
	     * Utility routine to get a metadata object.
	     * @param path
	     * @return
	     */
	    private static DirectoryMetaData getDirectory(String path)
	    {
	        try
	        {
	            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
	            try {
	            	DirectoryMetaData md = (DirectoryMetaData)in.readObject();
	            	return md;
	            }
	            finally
	            {
	            	in.close();
	            }
	        }
	        catch (IOException ioe)
	        {
	            throw new DeploymentException("Could not read metadata file " + path, ioe);
	        }
	        catch (ClassNotFoundException nfe)
	        {
	            throw new DeploymentException("Configuration error: could not instantiate DirectoryMetaData.");
	        }
	    }
}
