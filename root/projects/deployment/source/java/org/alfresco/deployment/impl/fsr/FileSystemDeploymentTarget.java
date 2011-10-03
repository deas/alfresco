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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.channels.FileChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


import org.alfresco.deployment.DeploymentTarget;
import org.alfresco.deployment.FSDeploymentRunnable;
import org.alfresco.deployment.FileDescriptor;
import org.alfresco.deployment.FileType;
import org.alfresco.deployment.PathUtil;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.impl.server.DeployedFile;
import org.alfresco.deployment.impl.server.Deployment;
import org.alfresco.deployment.impl.server.DeploymentReceiverAuthenticator;
import org.alfresco.deployment.impl.server.DeploymentState;
import org.alfresco.deployment.impl.server.Target;
import org.alfresco.util.Deleter;
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyCheck;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This represents a target for deployment to a filesystem
 * 
 * A file system deployment can only process a single deployment  
 * 
 */
public class FileSystemDeploymentTarget implements Serializable, DeploymentTarget
{   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1257869549338878302L;

	/**
     * Is this target busy ?
     */
    private boolean isBusy = false;
   
    /**
     * Deployments in progress
     */
    private Map<String, Deployment> fDeployments = Collections.synchronizedMap(new HashMap<String, Deployment>());
       
    /**
     * The currentTarget  
     */
    Target metaDataTarget = null;
    
    /**
     * The logger for this target
     */
    private static Log logger = LogFactory.getLog(FileSystemDeploymentTarget.class);

    /**
     * The name of this target.
     */
    private String fTargetName;

    /**
     * The root directory of the target deployment.
     */
    private String fRootDirectory;
    
    /**
	 * Get the directory in which metadata is stored
	 * @return the metaData directory
	 */   
	private String fMetaDataDirectory;
	
	/**
	 * Get the directory in which the temp files are stored
	 */
	private String tempDirectory;

    
    /**
     * The authenticator for this target
     */
    private DeploymentReceiverAuthenticator authenticator;
    
    /**
     * autoFix - does meta validation auto fix data ?
     */
    private boolean autoFix = true;
   
    /**
     * Runnables that will be invoked after commit.
     */
    private List<FSDeploymentRunnable> postCommit;
    
    /**
     * Runnables that will be invoked during prepare phase.
     */
    private List<FSDeploymentRunnable> prepare;
     
    /**
     * The common bits of file system deployment
     */
    private FileSystemReceiverService fileSystemReceiverService;
    
    public void setAuthenticator(DeploymentReceiverAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	public DeploymentReceiverAuthenticator getAuthenticator() {
		return authenticator;
	}
	
	private ValidateCommand validateMeCommand = new ValidateCommand(this);
	
    
    /**
     * initialise this target
     */
    public void init() 
    {
        PropertyCheck.mandatory(this, "authenticator", authenticator);
        PropertyCheck.mandatory(this, "rootDirectory", fRootDirectory);
		PropertyCheck.mandatory(this, "metaDataDirectory", fMetaDataDirectory);
        PropertyCheck.mandatory(this, "fileSystemReceiverService", fileSystemReceiverService);
        
        // Create the root directory if it does not already exist
        File rootFile = new File(fRootDirectory); 
        if(!rootFile.exists()){
        	logger.info("creating root data directory:" + rootFile.toString());
            if (rootFile.mkdirs() == false)
                throw new DeploymentException("Could not create root data directory while initializing: " + fRootDirectory);
        }
        
		// Create the various necessary directories if they don't already exits.
		File meta = new File(fMetaDataDirectory);
		if (!meta.exists())
		{
			logger.info("creating meta data directory:" + meta.toString());
            if (meta.mkdirs() == false)
                throw new DeploymentException("Could not create meta data directory while initializing: " + fMetaDataDirectory);
		}
		
	    tempDirectory = fileSystemReceiverService.getDataDirectory() + File.separator + fTargetName;
        
	    File temp = new File(tempDirectory);
	    if (!temp.exists())
	    {
	        logger.info("creating new temp location:" + temp.toString());
            if (temp.mkdirs() == false)
                throw new DeploymentException("Could not create temp directory while initializing: " + tempDirectory);
	    }
        
    	metaDataTarget = new Target(fTargetName, fMetaDataDirectory);
    	
    	// validate properties
    	fileSystemReceiverService.queueCommand(validateMeCommand);
    }
    
    /**
     * Get the target name.
     * @return
     */
    public String getName()
    {
        return fTargetName;
    }
    
    /**
     * Set the target name.
     * @return
     */
    public void setName(String name)
    {
        this.fTargetName = name;
    }

    /**
     * Get the root directory.
     * @return
     */
    public String getRootDirectory()
    {
        return fRootDirectory;
    }
    
    public void setRootDirectory(String root)
    {
    	this.fRootDirectory = root;
    }

	/**
	 * tell this target to autofix data during validation
	 * @param autoFix
	 */
	public void setAutoFix(boolean autoFix) 
	{
		this.autoFix = autoFix;
	}

	public boolean isAutoFix() 
	{
		return autoFix;
	}

	public void setFileSystemReceiverService(FileSystemReceiverService fileSystemReceiverService) 
	{
		this.fileSystemReceiverService = fileSystemReceiverService;
	}

	public FileSystemReceiverService getFileSystemReceiverService() 
	{
		return fileSystemReceiverService;
	}

	public synchronized String begin(String targetName, String storeName, int version, String user, char[] password) 
	{ 
		// Authenticate with the user and password
		if(!authenticator.logon(user, password))
		{
			logger.warn("Invalid user name or password");
			throw new DeploymentException("Invalid user name or password.");
		}
    
		// Check that the root directory exists
		File root = new File(this.getRootDirectory()); 
		if(!root.exists())
		{
			throw new DeploymentException("Root directory does not exist. rootDirectory:" + this.getRootDirectory()); 
		}
    
        String ticket = GUID.generate();
        logger.debug("begin deploy, target:" + targetName + ", ticket:" + ticket);
        
        try
        {
            Deployment deployment = new Deployment(ticket, targetName, storeName, version);
            fDeployments.put(ticket, deployment);
        }
        catch (IOException e)
        {
        	logger.error("Could not create logfile", e);
            throw new DeploymentException("Could not create logfile; Deployment cannot continue", e);
        }
        setBusy(true);
        return ticket;
	
	}
	

	public void prepare(String ticket) 
	{
	  	logger.info("Prepare ticket: " + ticket);
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
        	logger.debug("Could not prepare: invalid token ticket:" + ticket);
        	// We are most likely to get here because we are aborting an already aborted ticket
        	return;
        }
        if (deployment.getState() != DeploymentState.WORKING)
        {
            throw new DeploymentException("Deployment cannot be prepared: already aborting, or committing.");
        }
        try
        {
        	// prepare the meta data 
        	metaDataTarget.cloneMetaData(deployment);
            
            // Run any end user callbacks
            if (prepare != null && prepare.size() > 0)
            {
                for (FSDeploymentRunnable runnable : prepare)
                {
                    try
                    {
                        runnable.init(deployment);
                        runnable.run();
                    }
                    catch (Throwable t)
                    {
                        String msg = "Error thrown in prepare; rolled back";
                        
                        if(t.getCause() != null)
                        {
                           msg = msg + " :" + t.getCause().getMessage(); 
                        }
                        logger.error(msg, t);
               
                        throw new DeploymentException(msg, t);
                    }
                }
            }
        	
            // Mark the deployment as prepared
            deployment.prepare();
            
            logger.debug("prepared successfully ticket:" + ticket);
        }
        catch (IOException e)
        {
        	logger.error("Error while preparing ticket:" + ticket, e);
            throw new DeploymentException("Could not prepare.", e);
        }
	}


	public void abort(String ticket) 
	{
	  	logger.info("Abort ticket: " + ticket);
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
        	logger.debug("Could not abort: invalid token ticket:" + ticket);
        	// We are most likely to get here because we are aborting an already aborted ticket
        	return;
        }
        if (deployment.getState() != DeploymentState.WORKING && deployment.getState() != DeploymentState.PREPARED)
        {
            throw new DeploymentException("Deployment cannot be aborted: already aborting, or committing.");
        }
        try
        {
        	// Mark the deployment
            deployment.abort();
            
            // Delete the files which have been transferred.
            for (DeployedFile file : deployment)
            {
                if (file.getType() == FileType.FILE)
                {
                    File toDelete = new File(file.getPreLocation());
                    toDelete.delete();
                }
            }
            
        	// roll back the meta data 
            metaDataTarget.rollbackMetaData();

        }
        catch (IOException e)
        {
        	logger.error("Error while aborting ticket:" + ticket, e);
            throw new DeploymentException("Could not abort.", e);
        }
        finally 
        {
            setBusy(false);
            fDeployments.remove(ticket);
           
            if(deployment.isMetaError())
            {
            	// Queue the validateMeCommand to async validate
            	fileSystemReceiverService.queueCommand(validateMeCommand);
            }
        }		
	}


	public void commit(String ticket) 
	{	
	       Deployment deployment = fDeployments.get(ticket);
	        if (deployment == null)
	        {
	        	String msg = "Could not commit because invalid ticket:" + ticket;
	        	logger.error(msg);
	            throw new DeploymentException(msg);
	        }
	        if (deployment.getState() != DeploymentState.PREPARED)
	        {
	            throw new DeploymentException("Deployment cannot be committed: not prepared.");
	        }
	        
	        logger.debug("commit ticket:" + ticket);
	        try
	        {
	        	// Used to communicate between this thread and the writer threads.
	        	LinkedBlockingQueue<DeployedFile> commitQueue = new LinkedBlockingQueue<DeployedFile>();
	            
	        	// Parallel processing here to reduce the overall time taken for commit. 
	        	CommitThread commitThreads[] = 
	        	{
	                    new CommitMetadataThread(metaDataTarget , deployment),
	                    new CommitWriterThread(metaDataTarget , deployment, commitQueue),
	                    new CommitWriterThread(metaDataTarget , deployment, commitQueue),
	                    new CommitWriterThread(metaDataTarget , deployment, commitQueue)
	        	};
	        			
	            logger.debug("starting deployment.");

	            try 
	            {
	            	for(int i = 0; i < commitThreads.length; i++)
	            	{
	            		commitThreads[i].start();	
	            	}
	            
	                // Rename any existing soon to be overwritten files and directories with *.alf.
	                // Copy in new files and directories into their final locations.
	                for (DeployedFile file : deployment)
	                {

	                    String path = file.getPath();
	                    switch (file.getType())
	                    {
	                        case DIR :
	                        {
	                            File f = PathUtil.getFileForPath(fRootDirectory, path);
	                            if(f.exists())
	                            {
	                            	if(f.isFile())
	                            	{
	                            		File dest = new File(f.getAbsolutePath() + ".alf");
	                            		f.renameTo(dest);
	                            		f = PathUtil.getFileForPath(fRootDirectory, path);
	                            		f.mkdir();
	                            	}
	                            }
	                            else 
	                            {
	                            	// create a new dir
	                            	f.mkdir();
	                            }
	                           
	                            break;
	                        }
	                        case FILE :
	                        {
	                    	    commitQueue.add(file);
	                            break;
	                        }
	                        case DELETED :
	                        {
	                    	    commitQueue.add(file);
	                            break;
	                        }
	                        case SETGUID :
	                        {
	                            // Do nothing.
	                            break;
	                        }
	                        default :
	                        {
	                    	    logger.error("Internal error: unknown file type: " + file.getType());
	                            throw new DeploymentException("Internal error: unknown file type: " + file.getType());
	                        }
	                    }
	                }
	            } 
	            finally 
	            {
	            	for(int i = 0; i < commitThreads.length; i++)
	            	{
	            		commitThreads[i].setFinish();	
	            	}
	            }
	            
	            for(int i = 0; i < commitThreads.length; i++)
	            {
	                commitThreads[i].join();
	                if(commitThreads[i].getException() != null){
	                	throw commitThreads[i].getException();
	                }
	            }
	     
	            // Now we are past the point of no return and must go forward.
	            logger.debug("committed - clean up");
	            
	            // Phase 2 : Go through the log again and remove all .alf entries
	            for (DeployedFile file : deployment)
	            {
	                if (file.getType() == FileType.FILE)
	                {
	                    File intermediate = new File(file.getPreLocation());
	                    intermediate.delete();

	                }
	                File old = new File(PathUtil.getFileForPath(fRootDirectory, file.getPath()).getAbsolutePath() + ".alf");
	                Deleter.Delete(old);
	            }
	            File preLocation = new File(fileSystemReceiverService.getDataDirectory() + File.separatorChar + ticket);
	            preLocation.delete();
	            
	            // Mark the deployment as committed
	            
	            deployment.commit();
	            
	            /**
	             * Now run the post commit runnables.
	             */
	            if (postCommit != null && postCommit.size() > 0)
	            {
	                for (FSDeploymentRunnable runnable : postCommit)
	                {
	                    try
	                    {
	                        runnable.init(deployment);
	                        runnable.run();
	                    }
	                    catch (Throwable t)
	                    {
	                    	logger.error("Error from postCommit event t:" + t.toString(), t);
	                    }
	                }
	            }

	            logger.debug("commited successfully ticket:" + ticket);
	        }
	        catch (Exception e)
	        {
	            //if (!recover(ticket, deployment))
	            //{
	            //	logger.error("Failure during commit phase; rolled back.", e);
	            //    throw new DeploymentException("Failure during commit phase; rolled back.", e);
	            //}
	        }
	        finally
	        {
	            setBusy(false);
	            
	            if(deployment.isMetaError())
	            {
	            	fileSystemReceiverService.queueCommand(validateMeCommand);
	            }
	            fDeployments.remove(ticket);
	        }
	}


	public void delete(String ticket, String path) 
	{
		   Deployment deployment = fDeployments.get(ticket);
	        if (deployment == null)
	        {
	           	String msg = "Could not delete because invalid ticket:" + ticket;
	            throw new DeploymentException(msg);
	        }
	        try
	        {
	        	File f = PathUtil.getFileForPath(fRootDirectory, path);
	        	boolean exists = f.exists();
	        	
	            if (!exists) {
	            	deployment.setMetaError(true);
	        		logger.warn("unable to delete, does not exist, path:" + f.getAbsolutePath());
	            	if(fileSystemReceiverService.isErrorOnOverwrite())
	            	{
	            		throw new DeploymentException("unable to delete, does not exist, path:" + f.getAbsolutePath());
	            	}
	            }
	            
	            boolean isFile = f.isFile();
	            
	            DeployedFile file = new DeployedFile(FileType.DELETED, 
	                                                 null,
	                                                 path,
	                                                 null,
	                                                 false,
	                                                 isFile);
	            deployment.add(file);
	        }
	        catch (IOException e)
	        {
	        	throw new DeploymentException("Could not update log.", e);
	        }		
	}


	public List<FileDescriptor> getListing(String ticket, String path) 
	{
	      Deployment deployment = fDeployments.get(ticket);
	        if (deployment == null)
	        {
	            throw new DeploymentException("getListing invalid ticket. ticket:" + ticket);
	        }
	        try
	        {
	        	/**
	        	 * get the listing
	        	 * could have meta and a dir exists - good this is what we expect
	        	 * could have meta data but no dir - external person has deleted the dir - what to do here ?
	        	 * could have a dir but no meta data - exception will get thrown - create empty metadata ?
	        	 */
	        	File f = PathUtil.getFileForPath(fRootDirectory, path);
	        	boolean exists = f.exists();
	        	Set<FileDescriptor> list = metaDataTarget.getListing(path);
	        	
	        	if(!exists)
	        	{
	        		// here got some meta data, but directory is missing, parent metadata is corrupt
	            	// create dir, return create empty meta data ?
	            	throw new DeploymentException("Directory is missing, path:" + f.getAbsolutePath());
	            }
	        	
	            return new ArrayList<FileDescriptor>(list);
	            
	        }
	        catch (Exception e)
	        {
	            throw new DeploymentException("Could not get listing for path:" + path, e);
	        }
	}


	public void createDirectory(String ticket, String path, String guid, Set<String>aspects, Map<String, Serializable> props) 
	{
	     Deployment deployment = fDeployments.get(ticket);
	        if (deployment == null)
	        {
	            throw new DeploymentException("mkdir invalid ticket. ticket:" + ticket);
	        }
	        
	    	File f = PathUtil.getFileForPath(fRootDirectory, path);
	    	boolean exists = f.exists();
	    	
	        if (exists) 
	        {
	        	deployment.setMetaError(true);
	    		logger.warn("writing to pre-existing directory, path:" + f.getAbsolutePath());
	        	if(fileSystemReceiverService.isErrorOnOverwrite())
	        	{
	        		throw new DeploymentException("directory already exists, path:" + f.getAbsolutePath());
	        	}
	        }
	        // create a new directory
	        DeployedFile file = new DeployedFile(FileType.DIR,
	                                             null,
	                                             path,
	                                             guid,
	                                             !exists,
	                                             false);
	        try
	        {
	            deployment.add(file);
	        }
	        catch (IOException e)
	        {
	    	   throw new DeploymentException("Could not log mkdir of " + path + " error: " + e.toString(), e);
	        }
		
	}


	public OutputStream send(String ticket, boolean create, String path, String guid, String encoding, String mimeType, Set<String>aspects, Map<String, Serializable> props)
	{
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment timed out or invalid ticket.");
        }
        
        try
        {
        	String preLocation = tempDirectory + File.separator + guid;
        
        	// Open the destination file
        	OutputStream out = new FileOutputStream(preLocation);
        
        	File f = PathUtil.getFileForPath(fRootDirectory, path);

        	boolean exists = f.exists();
        	
        	// Check whether we are overwriting something that is outside our 
        	// control
            if(fileSystemReceiverService.isErrorOnOverwrite() )
            {
                if(exists)
                {
                    // file does exist
                    if (metaDataTarget.lookupMetadataFile(f.getParent(), f.getName()) == null)
                    {
                        /**
                         * File exists but is not in metadata
                         */
                        throw new DeploymentException("file already exists, path:" + f.getAbsolutePath());
                    }
                }
                else
                {
                    // file does not exist
                    if(!create)
                    {
                        // And this should be an update
                        throw new DeploymentException("file to update does not exist, path:" + f.getAbsolutePath()); 
                    }
                }
            }
    	       
        	DeployedFile file = new DeployedFile(FileType.FILE,
                                             preLocation,
                                             path,
                                             guid,
                                             !exists,
                                             true);
        	deployment.add(file);
        	return out;
        }
        catch (IOException e)
	    {
	            throw new DeploymentException("Could not send for path:" + path, e);
	    }
	}


	public void updateDirectory(String ticket, String path, String guid, Set<String>aspects, Map<String, Serializable> props) 
	{
	     Deployment deployment = fDeployments.get(ticket);
	     if (deployment == null)
	     {
	            throw new DeploymentException("Deployment invalid ticket.");
	     }
	     try
	     {
	         DeployedFile file = new DeployedFile(FileType.SETGUID,
                     null,
                     path,
                     guid,
                     false,
                     false);
	         deployment.add(file);
	     }
	     catch (Exception e)
	     {
	          throw new DeploymentException("Could not set guid on " + path, e);
	     }		
	}
    
	/**
	 * Part of the commit process.
	 */
	private class CommitWriterThread extends CommitThread {
		
		private LinkedBlockingQueue<DeployedFile> queue;
		private Deployment deployment; 
		private Target target;
		
		CommitWriterThread(Target target, Deployment deployment, LinkedBlockingQueue<DeployedFile> queue) 
		{
			this.target = target;
			this.deployment = deployment;
			this.queue = queue;
		}
		
		/**
		 * 
		 */
		public void run() 
		{           
			while(getException() == null) 
			{
	            DeployedFile file = null;
				try {
					file = queue.poll(3, TimeUnit.SECONDS);
				} 
				catch (InterruptedException e1) 
				{
					logger.debug("interrupted");
				}
				
	            if(file == null) 
	            {
	            	if(isFinish()) 
	            	{
	            			logger.debug("committer thread finished normally");
	            			break;
	            	}
	        	}
	            else
	            {
	            	try 
	            	{
	            		String path = file.getPath();
	            		switch (file.getType())
	            		{
	                        case FILE :
	                        {
	                        	logger.debug("add file:" + path);
	                        	// If file already exists then rename it
	                        	File f = PathUtil.getFileForPath(fRootDirectory, path);
	                        	if (f.exists())
	                        	{
	                        		File dest = new File(f.getAbsolutePath() + ".alf");
	                        		f.renameTo(dest);
	                        		f = PathUtil.getFileForPath(fRootDirectory, path);
	                        	}
	                        	// copy the file from the preLocation to its final target location
	                           	FileOutputStream out = new FileOutputStream(f);
	                        	FileInputStream in = new FileInputStream(file.getPreLocation());
	                       
	                        	FileChannel outChannel = out.getChannel(); 
	                        	FileChannel inChannel = in.getChannel();
	                        		
	                        	// Chunk size is required to use NIO on large files
	                            int chunkSize = 1 * (1024 * 1024);
	                            long size = inChannel.size();
	                            long position = 0;
	                            while (position < size) {
	                               position += inChannel.transferTo(position, chunkSize, outChannel);
	                            }
	                        	in.close();
	                        	out.flush();
	                        	out.close();
	                        		
	                        	break;
	                        }
	                        case DELETED :
	                        {
	                        	logger.debug("delete file:" + path);
	                        	// prepare the file for deletion by renaming it
	                        	File f = PathUtil.getFileForPath(fRootDirectory, path);
	                        	if (f.exists())
	                        	{
	                        		File dest = new File(f.getAbsolutePath() + ".alf");
	                        		f.renameTo(dest);
	                        	}
	                        	break;
	                        }
	                    }
	            	} 
	            	catch (Exception e) 
	            	{
	            		logger.error("exception in committer thread", e);
	            		setException(e);
	            	}
	            }
			}
		}		
	}
    
	/**
	 * Part of the commit process.
	 */
	private class CommitMetadataThread extends CommitThread {
		
		private Deployment deployment; 
		private Target target;
		
		CommitMetadataThread(Target target, Deployment deployment) 
		{
			this.target = target;
			this.deployment = deployment;
		}
		
		/**
		 * run the metadata cloner
		 */
		@Override
		public void run() 
		{
			try 
			{
				logger.debug("commit meta data");
		        target.commitMetaData(deployment);
	            logger.debug("metadata cloned and prepared");
			} 
		    catch (Exception e)
			{
		    	setException(e);
			}
		}
	}
		
	/**
	 *  Validate this target 
	 */
	public void validate()
	{
		metaDataTarget.validateMetaData(fRootDirectory, autoFix);
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setPostCommit(List<FSDeploymentRunnable> postCommit) {
		this.postCommit = postCommit;
	}

	public List<FSDeploymentRunnable> getPostCommit() {
		return postCommit;
	}

	public void setPrepare(List<FSDeploymentRunnable> prepare) {
		this.prepare = prepare;
	}

	public List<FSDeploymentRunnable> getPrepare() {
		return prepare;
	}

	public int getCurrentVersion(String target, String storeName) {
		// Not implemented -- yet 
		return -1;
	}	
	
	public void setMetaDataDirectory(String dir)
	{
		fMetaDataDirectory = dir;
	}
	
	/**
	 * Get the directory in which metadata 
	 * @return
	 */
	public String getMetaDataDirectory()
	{
		return fMetaDataDirectory;
	}
}
