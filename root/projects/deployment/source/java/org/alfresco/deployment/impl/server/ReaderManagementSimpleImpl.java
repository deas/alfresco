/*
 * Copyright (C) 2007-2010 Alfresco Software Limited.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.deployment.impl.DeploymentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReaderManagementSimpleImpl implements ReaderManagement
{
    /**
     * The logger for this target
     */
    private static Log logger = LogFactory.getLog(ReaderManagementSimpleImpl.class);
    
    /**
     * Need to kick of a reader thread to process input 
     * This class manages those threads
     */
    // Map token, thread
    Map<String, ReaderThread> fThreads = new HashMap<String, ReaderThread>();
    
    /**
     * addReader
     * @param is the input stream
     * @param os the output stream
     */
    public void addCopyThread(InputStream is, 
    		OutputStream os,
    		String token) 
    {
    	ReaderThread worker = new ReaderThread(is, os);
        worker.start();
        fThreads.put(token, worker);
    }
    
    /**
     * 
     * @param os the output stream
     */
    public void closeCopyThread(String token) throws IOException {
    	
    	ReaderThread worker = (ReaderThread)fThreads.get(token);
    	
    	if(worker == null)
    	{
            throw new DeploymentException("Closed unknown file.");
    	}
    	fThreads.remove(token);
    	
    	try {
			worker.join();
			if(worker.getException() != null) {
			    throw(worker.getException());	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    } // end of ReaderManagement
    

    private class ReaderThread extends Thread {	
		InputStream input;
		OutputStream output;
		IOException exception = null;
	
		public ReaderThread(InputStream input, OutputStream output) 
		{
			this.input = input;
			this.output = output;
			super.setName("Reader Thread ");
		}
	
		@Override
		public void run() 
		{
			byte b[] = new byte[1000];
			int len = 0;
			try 
			{
				while ( len >= 0 ) 
				{
					len = input.read(b, 0, b.length);
					if(len > 0 && exception == null) 
					{
						try 
						{
							output.write(b, 0, len);
						} 
						catch (IOException e) 
						{
							// If we get a write error we still need to drain 
							// input to avoid a broken pipe exception
							this.exception = e;
						}
					}
				}				
			}
			catch (IOException e) 
			{
				this.exception = e;
			}
			finally
			{
				try {
					output.close();
				} catch (IOException e) {
					// We can do nothing here
					logger.error("Unable to close content stream", e);
				}
			}
		}
		
		/**
		 * @return the exception or null if all is well
		 */
		public IOException getException()
		{
			return this.exception;
		}
	}
}