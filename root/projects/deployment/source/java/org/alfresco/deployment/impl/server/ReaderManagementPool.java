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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.alfresco.deployment.impl.DeploymentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReaderManagementPool implements ReaderManagement
{
    /**
     * The logger for this target
     */
    private static Log logger = LogFactory.getLog(ReaderManagementPool.class);
    
    private ExecutorService pool = Executors.newCachedThreadPool();
    
    private boolean finished = false;
    
    /**
     * Need to kick of a reader thread to process input 
     * This class manages those threads
     */
    // Map token, thread
    Map<String, ReaderJob> fJobs = new HashMap<String, ReaderJob>();
    
    /**
     * addReader
     * @param is the input stream
     * @param os the output stream
     */
    public synchronized void addCopyThread(InputStream is, 
    		OutputStream os,
    		String token) 
    {
    	ReaderJob worker = new ReaderJob(is, os);
    	pool.execute(worker);
        fJobs.put(token, worker);
    }
    
    /**
     * 
     * @param os the output stream
     */
    public synchronized void closeCopyThread(String token) throws IOException {
    	
    	ReaderJob worker = (ReaderJob)fJobs.get(token);
    	
    	if(worker == null)
    	{
            throw new DeploymentException("Closed unknown file.");
    	}
    	
    	fJobs.remove(token);
    		
    	try {
			
    		worker.join();
			
			if(worker.getException() != null) {
			    throw(worker.getException());	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    } // end of ReaderManagement
    
    private class ReaderJob implements Runnable
    {
    	InputStream input;
		OutputStream output;
		IOException exception = null;
	
		public ReaderJob(InputStream input, OutputStream output) 
		{
			this.input = input;
			this.output = output;
		}
		
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
				
				synchronized(this)
				{
					finished = true;
					logger.debug("notifying");
					notifyAll();
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
		
		public void join() throws InterruptedException
		{
			synchronized(this)
			{
				if (finished)
				{
					return;
				}
				logger.debug("waiting for the reader to finish");
				wait();
			}
		
		}
	}
}