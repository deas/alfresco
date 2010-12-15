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
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.alfresco.deployment.impl.DeploymentException;

public class NonblockingReaderManagement {

	ReaderThread worker;
	
	public Selector selector = null;
	
    public NonblockingReaderManagement()
    {
    	try 
    	{
    		selector = Selector.open();
    	}
    	catch (IOException ie)
    	{
    		
    	}
    	ReaderThread worker = new ReaderThread();
        worker.start();
    }
    
    /**
     * addReader
     * @param is the input stream
     * @param os the output stream
     */
    void addCopyThread(Pipe.SourceChannel sc,
    		InputStream is, 
    		OutputStream os,
    		String token) 
    {
    	ReadableByteChannel ic = Channels.newChannel(is);
   
    	try {
    		sc.configureBlocking(false);
    		Tracker newKey = new Tracker();
    		newKey.is = is;
    		newKey.os = os;
    		newKey.token = token;
			SelectionKey acceptKey = sc.register(selector, SelectionKey.OP_ACCEPT);
			acceptKey.attach(newKey);
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //fThreads.put(token, worker);
    }
    
    /**
     * 
     * @param os the output stream
     */
    void closeCopyThread(String token) throws IOException {
    	
//    	ReaderThread worker = (ReaderThread)fThreads.get(token);
//    	
//    	if(worker == null)
//    	{
//            throw new DeploymentException("Closed unknown file.");
//    	}
//    	fThreads.remove(token);
//    	
//    	try {
//			worker.join();
//			if(worker.getException() != null) {
//			    throw(worker.getException());	
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
    	
    } // end of ReaderManagement
    

    private class ReaderThread extends Thread {	
		
		public ReaderThread() 
		{
			super.setName("Reader Thread ");
		}
	
		@Override
		public void run() 
		{
			
			try {
				
				while (selector.select() > 0)
				{
					Set<SelectionKey> readyKeys = selector.selectedKeys();
					for(SelectionKey key : readyKeys)
					{
						if(key.isReadable())
						{
							Tracker t = (Tracker)key.attachment();
						}
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
    private class Tracker 
    {
    	public InputStream is; 
    	public OutputStream os;
    	public String token;
    }
}