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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.alfresco.deployment.DeploymentReceiverTransport;
import org.alfresco.deployment.DeploymentTarget;
import org.alfresco.deployment.DeploymentToken;
import org.alfresco.deployment.DeploymentTokenImpl;
import org.alfresco.deployment.DeploymentTransportInputFilter;
import org.alfresco.deployment.FileDescriptor;
import org.alfresco.deployment.TargetStatus;
import org.alfresco.deployment.TargetStatusImpl;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * This is the implementation of the Alfresco Deployment Receiver Engine
 * 
 * The Deployment Receiver Engine manages the communications with a host 
 * instance of alfresco and then delegates the deployment to one of the deployment targets.
 * It also manages some server based functionality to do with start up / shut down and housekeeping.
 */
public class DeploymentReceiverEngineImpl implements 
	DeploymentReceiverTransport, 
	DeploymentTargetRegistry,
	Runnable, 
    ApplicationContextAware
{
    /**
     * The transformers to apply to the incoming messages
     */
	List<DeploymentTransportInputFilter> transformers;
	
	/**
	 * The housekeepers which are periodically polled to do housekeeping on the deployment tasks
	 */
	private Set<Housekeeper> housekeepers;
	
    private ConfigurableApplicationContext fContext;
    
    private DeploymentCommandQueue commandQueue;
    
    /**
     * The authenticator which is used to control access to this engine 
     */
    private DeploymentReceiverAuthenticator authenticator;
     
    // Deployment engine control thread
    private Thread fThread;
    
    private boolean fDone;
    
    private static Log logger = LogFactory.getLog(DeploymentReceiverEngineImpl.class);
    
    /**
     * How long to wait before polling for shutdown or housekeeping
     * Default 5 seconds.
     */
    private long pollDelay = 5000;
    
    /**
     * Is the keep alive thead a demon thread, or does it need to stop shutdown?
     */
    private boolean isDaemonThread = false;
    
    /**
     * Map of targetName, DeploymentTarget
     */
    private Map<String, DeploymentTarget> targetByName = new HashMap<String, DeploymentTarget>();
    
    /**
     * Map of ticketName, DeploymentTracker
     */
    private Map<String, DeploymentTracker> trackerByTicket = Collections.synchronizedMap(new HashMap<String, DeploymentTracker>());
    
    private ReaderManagement readerManagement ;
        
    public DeploymentReceiverEngineImpl()
    {
        fDone = false;
    }

    public void init()
    { 
        PropertyCheck.mandatory(this, "readerManagement", readerManagement);
        
        fThread = new Thread(this);
        fThread.setName("Deployment Receiver Engine Keep Alive");
        fThread.setDaemon(isDaemonThread);
        fThread.start();   
    }
    
    public void shutDown()
    {
    	logger.info("Shutting down Implementation");
    	
        fDone = true;
        synchronized (this)
        {
            this.notifyAll();
        }
        try
        {
            fThread.join();
        }
        catch (InterruptedException e)
        {
        	logger.error("Unable to join implementation thread while shutting down", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#shutDown(java.lang.String, java.lang.String)
     */
    public synchronized void shutDown(String user, char[] password)
    {
    	if(authenticator == null)
    	{
    		return;
    	}
    	
    	if(authenticator.logon(user, password))
    	{
        	shutDown();
        	fContext.close();
    	}
    }

    /** 
     * This is the keep-alive thread of the FSR.
     * When fDone = true this thread exits and the JVM will terminate.
     * 
     * And since we have to have a thread - may as well use it to process our event queues.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
    	logger.info("Alfresco Deployment Receiver Engine Started");
        while (!fDone)
        {                 
            try
            {
                Thread.sleep(pollDelay);
                
                if(housekeepers != null)
                {
                	/**
                	 * Run any deployment housekeepers that have been registered
                	 */
                	for (Housekeeper housekeeper: housekeepers)
                	{
                		housekeeper.poll();
                	}
                }
            }
            catch (InterruptedException e)
            {
                // Finished Sleeping - fDone may have been set if we are shutting down.
            }
    
        }
        logger.info("Alfresco Deployment Receiver Engine Stopped");
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        fContext = (ConfigurableApplicationContext)applicationContext;
    }
    
	/**
	 * Get the content transformers for this transport - if the transport does not support
	 * content transformation then simply return null;
	 * @return the content transformers or null if there are no transformers.
	 */
	public List<DeploymentTransportInputFilter>getTransformers() 
	{
		return transformers;
	}
	
	public void setTransformers( List<DeploymentTransportInputFilter> transformers) 
	{
	    this.transformers = transformers;
	}
	
	/**
	 * Register a new deployment target.  If an entry with the old name 
	 * already exists then the new value replaces the old value. 
	 * @param name the name of the target
	 * @param target the implementation of the target
	 */
	public void registerTarget(String name, DeploymentTarget target)
	{
		logger.info("deployment target registered, name=" + name);
		targetByName.put(name, target);
	}
	
	/**
	 * Unregister a deployment target
	 */
	public void unregisterTarget(String name)
	{
		targetByName.remove(name);
	}
	
	/**
	 * Get the targets for this deployment engine.
	 * @return the targets for this deployment engine
	 */
	public Map<String, DeploymentTarget> getTargets() 
	{
		return targetByName;
	}
	
    /**
     * Get the deployment tracker
     * @return the deploymentTracker
     * @throws DeploymentException
     */
    private DeploymentTracker getDeploymentTracker(String ticket)
    {
    	DeploymentTracker ret = trackerByTicket.get(ticket);
    	
        if (ret == null)
        {
           	String msg = "invalid ticket:" + ticket;
            throw new DeploymentException(msg);
        }
    	
    	return ret;
    }
    
    
    /**
     * Get the Target with the given name.
     * @param targetName
     * @return
     */
    private DeploymentTarget getTarget(String targetName)
    {
        return targetByName.get(targetName);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#begin(java.lang.String, java.lang.String, java.lang.String)
     */
    public DeploymentToken begin(String targetName, String storeName, int version, String user, char[] password)
    {
    	try 
    	{
    		logger.debug("begin of target:" + targetName);
            DeploymentTarget target = getTarget(targetName);
            if (target == null)
            {
            	logger.warn("No such target:" + targetName);
                throw new DeploymentException("No such target: " + targetName);
            }
            
            // Delegate to Deployment Target
            String ticket = target.begin(targetName, storeName, version, user, password);
            
            // track the ticket
            trackerByTicket.put(ticket, new DeploymentTracker(target));
            
            DeploymentTokenImpl token = new DeploymentTokenImpl();
            token.setTicket(ticket);
            TargetStatusImpl ts = new TargetStatusImpl();
            ts.setCurrentVersion(target.getCurrentVersion(targetName, storeName));
            ts.setTargetName(targetName);
            ts.setStoreName(storeName);
            token.setTargetStatus(ts);
            
    		return token;
    	}
       	catch (RuntimeException e) 
    	{
    		MessageFormat f = new MessageFormat("error in begin user:{0}, password:{1}");
    		Object[] objs = { user, "****" };
    	    logger.error(f.format(objs), e);
    		throw e;
        }
    }
		
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#abort(java.lang.String)
     */
    public void abort(String ticket)
    {
    	DeploymentTracker tracker = getDeploymentTracker(ticket);
    	try 
    	{
    		tracker.getTarget().abort(ticket);
    		tearDown(tracker);
    		trackerByTicket.remove(ticket);	
    	}
       	catch (RuntimeException e) 
    	{
    		MessageFormat f = new MessageFormat("error in abort ticket:{1}");
    		Object[] objs = { ticket };
    	    logger.error(f.format(objs), e);
    		throw e;
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#abort(java.lang.String)
     */
    public void prepare(String ticket)
    {
    	DeploymentTracker tracker = getDeploymentTracker(ticket);
    	
    	try 
    	{
    		tracker.getTarget().prepare(ticket);
    	}
       	catch (RuntimeException e) 
    	{
       		commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
    		MessageFormat f = new MessageFormat("error in prepare ticket:{1}");
    		Object[] objs = { ticket };
    	    logger.error(f.format(objs), e);
    		throw e;
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#commit(java.lang.String)
     */
    public void commit(String ticket)
    {
		DeploymentTracker tracker = getDeploymentTracker(ticket);
    	try 
    	{
    		tracker.getTarget().commit(ticket);
    		tearDown(tracker);
    		trackerByTicket.remove(ticket);
    	}
       	catch (RuntimeException e) 
    	{
       		commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
    		MessageFormat f = new MessageFormat("error in commit ticket:{0}");
    		Object[] objs = { ticket };
    	    logger.error(f.format(objs), e);
    		throw e;
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#delete(java.lang.String, java.lang.String)
     */
    public void delete(String ticket, String path)
    {
		DeploymentTracker tracker = getDeploymentTracker(ticket);
    	try 
    	{
    		tracker.getTarget().delete(ticket, path);
    	}
        catch (RuntimeException e) 
    	{
        	commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
    		MessageFormat f = new MessageFormat("error in delete ticket:{0}, path:{1}");
    		Object[] objs = { ticket, path };
    	    logger.error(f.format(objs), e);
    		throw e;
        }

    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#getListing(java.lang.String, java.lang.String)
     */
    public List<FileDescriptor> getListing(String ticket, String path)
    {
    	DeploymentTracker tracker = getDeploymentTracker(ticket);
    	try
    	{
    	    List<FileDescriptor> list = tracker.getTarget().getListing(ticket, path);
    		if(list == null)
    		{
    		    return new ArrayList<FileDescriptor>();
    		}
    		else
    		{
    		    return list;
    		}
    	}
    
       	catch (RuntimeException e) 
    	{
       		commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
    		MessageFormat f = new MessageFormat("error in getListing ticket:{0}, path:{1}");
    		Object[] objs = { ticket, path};
    	    logger.error(f.format(objs), e);
    		throw e;
        }

    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#mkdir(java.lang.String, java.lang.String, java.lang.String)
     */
    public void createDirectory(String ticket, String path, String guid, Set<String>aspects, Map<String, Serializable> props)
    {
    	DeploymentTracker tracker = getDeploymentTracker(ticket);
    	try {
    		tracker.getTarget().createDirectory(ticket, path, guid, aspects, props);
    	}
        catch (RuntimeException e) 
    	{
        	commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
    		MessageFormat f = new MessageFormat("error in mkdir ticket:{0}, path:{1}, guid:{2}");
    		Object[] objs = { ticket, path, guid };
    	    logger.error(f.format(objs), e);
    		throw e;
        }
    }

    public void updateDirectory(String ticket, String path, String guid, Set<String>aspects, Map<String, Serializable> properties)
    {
    	DeploymentTracker tracker = getDeploymentTracker(ticket);
    	try 
    	{
    		tracker.getTarget().updateDirectory(ticket, path, guid, aspects, properties);
    	}
    
    	catch (RuntimeException e) 
    	{
    		commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
    		MessageFormat f = new MessageFormat("error in setGuid ticket:{0}, path:{1}, guid:{2}");
    		Object[] objs = { ticket, path, guid };
    	    logger.error(f.format(objs), e);
    		throw e;
        }
    }
    
    private String getNextHandle(String ticket)
    {
    	int handle = handleGenerator.incrementAndGet();    	
    	return String.valueOf(handle) + ":" + ticket;
    }
    

    /**
     * Closes all Output Stream Handlers
     * @param tracker
     */
    private void tearDown(DeploymentTracker tracker)
    {
    	// Need to clean up any open Output Streams here
    	Set<String> tokens = tracker.getTokens(); 
    	for(String outputToken : tokens)
    	{
    		OutputStream out = fOutputs.get(outputToken);
    		try 
    		{
    			out.flush();
    			out.close();
    			readerManagement.closeCopyThread(outputToken);
    		} 
    		catch (DeploymentException de)
    		{
    			// Do nothing
    		}
    		catch (IOException ie)
    		{
    			// Do nothing
    		}
    	}
    } 
     
    /**
     * Output stream Handlers - implementation of DeploymentReceiverTransport interface
     */
    
    //The table of OutputStreams.   Token to Output Stream
    private Map<String, OutputStream> fOutputs = Collections.synchronizedMap(new HashMap<String, OutputStream>());
    
    private AtomicInteger handleGenerator = new AtomicInteger(1);
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#send(java.lang.String, java.lang.String, java.lang.String)
     */
    public OutputStream send(String token, boolean create, String path, String guid, String encoding, String mimeType, Set<String>aspects, Map<String, Serializable> props)
    {
        throw new DeploymentException("Forbidden call.");
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverTransport#getSendToken(java.lang.String, java.lang.String, java.lang.String)
     */
    public String getSendToken(String ticket, boolean create, String path, String guid, String encoding, String mimeType, Set<String>aspects, Map<String, Serializable> props)
    {
    	DeploymentTracker tracker = getDeploymentTracker(ticket);
    	try 
    	{	
    		// Piped output stream allows us to write to an input stream so it can 
    		// be filtered.
            PipedOutputStream pos = new PipedOutputStream();
            PipedInputStream pis = new PipedInputStream();
            pis.connect(pos);
             
            // Decorate input with filters here
            InputStream is = pis;
            if(transformers != null && transformers.size() > 0) 
            {
            	for (DeploymentTransportInputFilter transformer : transformers)
            	{
            		is = transformer.addFilter(is, path, encoding, mimeType);
            	}
            }
            
    		OutputStream out = tracker.getTarget().send(ticket, create, path, guid, encoding, mimeType, aspects, props);
    		
    		/**
    		 * out should not be null, but cater for incorrect empty implementations.
    		 */
    		if(out == null)
    		{
    		    logger.warn("send to target returned null");
    		    out = new NullOutputStream(); 
    		}
    		String token = getNextHandle(ticket);
    		logger.debug("Open token " + token);
            // Need to kick off a reader thread to process input
    		// and drive transformation.
            readerManagement.addCopyThread(is, out, token);
    		fOutputs.put(token, pos);
    		tracker.addToken(token);
    		
     		return token;
    	}
       	catch (RuntimeException e) 
    	{
       		commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
    		MessageFormat f = new MessageFormat("error in getSendToken ticket:{0} path:{1}, guid:{2}");
    		Object[] objs = { ticket, path, guid };
    	    logger.error(f.format(objs), e);
    		throw e;
        }
        catch (IOException e)
        {
        	commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
    		MessageFormat f = new MessageFormat("error in getSendToken ticket:{0} path:{1}, guid:{2}");
    		Object[] objs = { ticket, path, guid };
    	    logger.error(f.format(objs), e);
            throw new DeploymentException("Unable to open " + path + " for write.", e);
        }
 
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverTransport#write(java.lang.String, java.lang.String, byte[], int, int)
     */
    public void write(String ticket, String outputToken, byte[] data,
                      int offset, int count)
    {
        OutputStream out = fOutputs.get(outputToken);
        if (out == null)
        {
            throw new DeploymentException("Invalid output stream token.");
        }
        try
        {
            out.write(data, offset, count);
        }
        catch (IOException e)
        {
        	commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
           	MessageFormat f = new MessageFormat("unable to write ticket:{0}, outputToken:{1}, data:{2}, offset:{3}, len:{4}");
        	Object[] objs = { ticket, outputToken, data, offset, count };
        	logger.error(f.format(objs), e);
            throw new DeploymentException("Failed write. ", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverTransport#finishSend(java.lang.String, java.lang.String)
     */
    public void finishSend(String ticket, String outputToken)
    {
    	DeploymentTracker tracker = getDeploymentTracker(ticket);
    	try 
    	{
    		logger.debug("finish token" + outputToken);
    		OutputStream out = fOutputs.get(outputToken);
    		if (out == null)
    		{
    			throw new DeploymentException("Invalid output token.");
    		}
    		
    		try
    		{
    			out.flush();
    			out.close();
    		} 
    		finally
    		{
    			readerManagement.closeCopyThread(outputToken);
    			fOutputs.remove(outputToken);
    			tracker.removeToken(outputToken);
    		}
    	}   
    	catch (RuntimeException e) 
    	{
    		commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
    		MessageFormat f = new MessageFormat("error in finishSend ticket:{0}, outputToken:{1}");
    		Object[] objs = { ticket, outputToken };
    	    logger.error(f.format(objs), e);
    		throw e;
        }
        catch (IOException e)
        {
        	commandQueue.queueCommand(new TerminatorCommand(this, ticket, "earlier exception"));
    		MessageFormat f = new MessageFormat("error in finishSend ticket:{0}, outputToken:{1}");
    		Object[] objs = { ticket, outputToken };
    	    logger.error(f.format(objs), e);
            throw new DeploymentException("FinishSend I/O error.", e);
        }
    }
    
    public void setAuthenticator(DeploymentReceiverAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	public DeploymentReceiverAuthenticator getAuthenticator() {
		return authenticator;
	}

	public void setHousekeepers(Set<Housekeeper> housekeepers) {
		this.housekeepers = housekeepers;
	}

	public Set<Housekeeper> getHousekeepers() {
		return housekeepers;
	}

	public void setPollDelay(long pollDelay) {
		this.pollDelay = pollDelay;
	}

	public long getPollDelay() {
		return pollDelay;
	}

	public void setCommandQueue(DeploymentCommandQueue commandQueue) 
	{
		this.commandQueue = commandQueue;
	}

	public DeploymentCommandQueue getCommandQueue() 
	{
		return commandQueue;
	}

	public void setReaderManagement(ReaderManagement readerManagement) 
	{
		this.readerManagement = readerManagement;
	}

	public ReaderManagement getReaderManagement() 
	{
		return readerManagement;
	}

    public void setDaemonThread(boolean isDemonThread)
    {
        this.isDaemonThread = isDemonThread;
    }

    public boolean isDaemonThread()
    {
        return isDaemonThread;
    }
	   
//	@Override
//	public TargetStatus[] getTargetStatus(String user, String password) 
//	{
//    	if(authenticator.logon(user, password))
//    	{
//    		targetByName.get
//    		return null;
//    	}
//    	else
//    	{
//    		throw new DeploymentException("Unable to authenticate");
//    	}
//    	 	
//	}
//
//	@Override
//	public TargetStatus getTargetStatus(String user, String password, String target) 
//	{
//    	if(authenticator.logon(user, password))
//    	{
//    		DeploymentTarget t = targetByName.get(target);
//    		if(t == null)
//    		{
//    			
//    		}
//    		else
//    		{
//    			TargetStatusImpl status = new TargetStatusImpl();
//    			status.setCurrentVersion(t.getCurrentVersion());
//    			
//    			return status;
//    		}
//    	}
//    	else
//    	{
//    		throw new DeploymentException("Unable to authenticate");
//    	} 	
//	}
    
    private class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException 
        {
            // Do nothing
        }
        @Override
        public void write(byte[] b, int off, int len) throws IOException 
        {
            // Do nothing
        }
        @Override
        public void flush() throws IOException
        {
            // Do nothing
        }
        @Override
        public void close() throws IOException
        {
            // Do nothing
        }
    }
}



