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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.alfresco.deployment.FSDeploymentRunnable;
import org.alfresco.deployment.FileDescriptor;
import org.alfresco.deployment.FileType;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.util.Path;
import org.alfresco.util.Deleter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class manages the metadata for a Target in the FSR.    
 * 
 * @author britt
 * @author mrogers
 */
public class Target implements Serializable
{
    private static final long serialVersionUID = 7759718377782991626L;
    private static final String MD_NAME = ".md.";
    private static final String CLONE = "clone";
    private static final String OLD = "old";
    
    /**
     * Is this target busy ?
     */
    private boolean busy = false;
    
    private static Log logger = LogFactory.getLog(Target.class);

    /**
     * The name of the target.
     */
    private String fTargetName;

    /**
     * Where metadata is kept for this target.
     */
    private String fMetaDataDirectory;
    
    private MetadataCache metadataCache = new MetadataCache();

    /**
     * Make one up.
     * @param name
     * @param root
     * @param metadata
     */
    public Target(String name,
                  String metadataDirectory)
    {
        fTargetName = name;
        fMetaDataDirectory = metadataDirectory;
    
   
        File meta = new File(fMetaDataDirectory);
        if (!meta.exists())
        {
        	logger.info("Initialised empty metadata for target:" + fTargetName);
            if (meta.mkdir() == false)
                throw new DeploymentException("Could not create meta data directory: " + fMetaDataDirectory);

        } 
        File metaRoot = new File(fMetaDataDirectory + File.separatorChar + MD_NAME);
        if(!metaRoot.exists())
        {
            DirectoryMetaData md = new DirectoryMetaData();
            putDirectory(fMetaDataDirectory + File.separatorChar + MD_NAME, md);
        }        
        
    }
    
    /**
     * Validate the metadata
     *
     * 1) Checks whether files and directories have been deleted from the destination filesystem. 
     * 2) Checks whether metadata can be read from disk (metadata may be corrupt or otherwise unreadable).
     * 3) Checks whether files and directories are of correct type (a file may have replaced a dir 
     * and vice versa)
     * 
     * @param fixit if true then the validator will attempt to fix the problem
     * 
     * @return true meta data has had an error (the problem may have been fixed if fixit==true)
     */
    public boolean validateMetaData(String rootDir, boolean autoFix) 
    {
		File dir = new File(rootDir);
        return validateMetaData(fMetaDataDirectory , dir, autoFix);
    }
    
    /**
     * recursive validate method
     * 
     * @param metaDir
     * @param destDir
     * @return error this metaData is invalid (problem may have been fixed if fixit=true)
     * 
     */
    private boolean validateMetaData(String metaDir, File destDir, boolean fixit)
    {
    	boolean error = false; // success unless changed
    	
    	String metaFileName = metaDir + File.separatorChar + MD_NAME;
    	
    	try 
    	{
    		DirectoryMetaData meta = getDirectory(metaFileName);

    		File[] srcList = destDir.listFiles();
    		Map<String, File> srcMap = new HashMap<String, File>(srcList.length);
    		for(int i = 0; i < srcList.length; i++)
    		{
    			srcMap.put(srcList[i].getName(), srcList[i]);
    		}
    		
    		Set<FileDescriptor> metaList = meta.getListing();
    		
    		Set<FileDescriptor>  toRemove = new HashSet<FileDescriptor>();
    		
    		boolean modified = false;

    		for(FileDescriptor descriptor : metaList)
    		{
    			// if fd in srcList
    			File child = srcMap.get(descriptor.getName());
    			if (child != null) // we have a child
    			{
    				if(child.isDirectory() != (descriptor.getType() == FileType.DIR)) 
    				{
    					error = true;
    					logger.warn("mismatch on file file or directory for path:" + descriptor.getName());
    					// Child is a directory - should be a file or vice versa.
    					if(fixit) 
    					{
    						toRemove.add(descriptor);
    						modified = true;
    					}
    				}
    				
    				if (child.isDirectory())
    				{
    					boolean val = validateMetaData(metaDir +  File.separatorChar + descriptor.getName(), child, fixit);	
    					error = val || error;
    					if(val)  // did child have an error ?
    					{
    						if(fixit)
    						{
    							// Need to invalidate the GUID of child directory and its parents
        						descriptor.setGuid("None");
        						modified = true;
    						}
    					}
    				}    	
    			}	
    			else  // missing file or dir
    		    {
    				error = true;
    				logger.warn("missing file or directory for path:" + descriptor.getName());
    				if(fixit)
    				{
    					toRemove.add(descriptor);
    					modified = true;
    				}
    		    }
    		}
    		if(modified) 
    		{
    			for(FileDescriptor desc : toRemove)
    			{
    				meta.remove(desc);
    			}
    			logger.warn("autofix: replaced metadata for dir:" + metaDir);
    		    putDirectory(metaFileName, meta);	
    		}
    	}
    	catch (DeploymentException de)
    	{
    		// metadata is unreadable.

    		error = true; // failure
    		if(fixit)
    		{
    			logger.warn("metadata is unreadable.  Replaced metadata for target: " + fTargetName + ", metaDir:" + metaDir, de);
    			putDirectory(metaFileName, new DirectoryMetaData());
    			
    		}
    		else
    		{
        		logger.error("metadata is unreadable. metaDir:" + metaDir, de);
    		}
    	}
    	return error;
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
     * Get the meta data directory.
     * @return
     */
    public String getMetaDataDirectory()
    {
        return fMetaDataDirectory;
    }
    
    /**
     * Looks up the metadata for the specified file.
     * 
     * Returns the file descriptor from meta-data.
     * 
     * @param path
     * 
     * @return the file descriptor or null if the file does not exist in the metadata 
     */
    public FileDescriptor lookupMetadataFile(String path, String fileName)
    {
       try
       {
           Set<FileDescriptor> list = getListing(path);
           for(FileDescriptor file : list)
           {
               if(file.getName().equals(fileName))
               {
                   logger.debug("lookupMetadataFile : found file in metadata");
                   // file found in listing
                   return file;
               }
           }
           
           logger.debug("lookupMetadataFile : not found metadata : return null");
           return null;
       }
       catch (Exception e)
       {
           return null;
       }
    }
    
    /**
     * Get the metadata listing for a directory
     * @param path
     * @return the listng for the specified directory
     * @throws DeploymentException - the directory does not exist 
     */
    public SortedSet<FileDescriptor> getListing(String path)
    {
        // Have we got the metadata cached ?
        SortedSet<FileDescriptor> val = metadataCache.lookup(path);
        
        if(val != null)
        {
            return val;
        }
        else
        {
            Path cPath = new Path(path);
            StringBuilder builder = new StringBuilder();
            builder.append(fMetaDataDirectory);
            if (cPath.size() != 0)
            {
                for (int i = 0; i < cPath.size(); i++)
                {
                    builder.append(File.separatorChar);
                    builder.append(cPath.get(i));
                }
            }
            builder.append(File.separatorChar);
            builder.append(MD_NAME);
            String mdPath = builder.toString();
            val = getDirectory(mdPath).getListing();
            return metadataCache.put(path, val);
        }
    }

    /**
     * Clone and update all the metadata files for the commit phase of a deployment.
     * @param deployment the deployment
     */
    public void cloneMetaData(Deployment deployment)
    {
        metadataCache.clear();
    	String currentmd = null;
       	DirectoryMetaData md = null;
    	
        for (DeployedFile file : deployment)
        {
        	Path path = new Path(file.getPath());
        	Path parent = path.getParent();
        	
        	String parentPath = "";
        	if(parent != null)
        	{
        		parentPath = parent.toString() + File.separatorChar;
        	}
        	
        	String mdName = fMetaDataDirectory + File.separatorChar + parentPath + MD_NAME;        	
           		
        	
        	if(! mdName.equals(currentmd))
        	{
        		if(md != null)
        		{   
        			// save the previous directory before we replace it with a new one.
        			putDirectory(currentmd + CLONE, md);
        		} 
        		
        		File metaFile = new File(mdName);
        		File cloneFile = new File(mdName + CLONE);
        		File parentDir = new File(metaFile.getParent());
        		
        		if(cloneFile.exists())
        		{
        			 md = getDirectory(cloneFile.getPath());
        		}
        		else if (metaFile.exists())
                {
                    md = getDirectory(metaFile.getPath());
                }
                else
                {
                    parentDir.mkdirs();
                	md = new DirectoryMetaData();
                }      		
            	currentmd = mdName;
        	}
       	
        	switch (file.getType())
        	{
            	case FILE :
            	{
            		FileDescriptor fd =
            			new FileDescriptor(path.getBaseName(),
                                       FileType.FILE,
                                       file.getGuid());
            		md.remove(fd);
            		md.add(fd);
            		break;
            	}
            	case DIR :
            	{
            		FileDescriptor fd =
            			new FileDescriptor(path.getBaseName(),
                                       FileType.DIR,
                                       file.getGuid());
            		md.remove(fd);
            		md.add(fd);
            		String newDirPath = fMetaDataDirectory + File.separatorChar + path.toString();
            		File newDir = new File(newDirPath);
            		newDir.mkdir();
            		DirectoryMetaData newMD = new DirectoryMetaData();
            		putDirectory(newDirPath + File.separatorChar + MD_NAME + CLONE, newMD);
            		break;
            	}
            	case DELETED :
            	{
            		FileDescriptor toRemove = new FileDescriptor(path.getBaseName(),
                                                             FileType.DELETED,
                                                             null);
            		md.remove(toRemove);
            		break;
            	}
            	case SETGUID :
            	{
            		FileDescriptor toModify = new FileDescriptor(path.getBaseName(),
                                                             null,
                                                             null);
            		SortedSet<FileDescriptor> tail = md.getListing().tailSet(toModify);
            		if (tail.size() != 0)
            		{
            			toModify = tail.first();
            			if (toModify.getName().equals(path.getBaseName()))
            			{
            				toModify.setGuid(file.getGuid());
            				break;
            			}
            		}
            		throw new DeploymentException("Trying to set guid on non existent file " + path);
            	}
            	default :
            	{
            		throw new DeploymentException("Configuration Error: unknown FileType " + file.getType());
            	}
        	} // end of switch
        }
        
        // Now write the last directory
		if(md != null)
		{
			putDirectory(currentmd + CLONE, md);
		}
    }
    
    

    /**
     * Utility routine to get a metadata object.
     * @param path
     * @return the directory metadata or DeploymentException if it does not exist.
     */
    private DirectoryMetaData getDirectory(String path)
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            DirectoryMetaData md = (DirectoryMetaData)in.readObject();
            in.close();
            return md;
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

    /**
     * Utility for writing a metadata object to disk.
     * @param path
     * @param md
     */
    private void putDirectory(String path, DirectoryMetaData md)
    {
        try
        {
            FileOutputStream fout = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(md);
            out.flush();
            fout.getChannel().force(true);
            out.close();
        }
        catch (IOException ioe)
        {
            throw new DeploymentException("Could not write metadata path:" + path, ioe);
        }
    }

    /**
     * Roll back metadata changes.
     */
    public void rollbackMetaData()
    {
        recursiveRollbackMetaData(fMetaDataDirectory);
    }

    /**
     * Commit cloned metadata.
     */
    public void commitMetaData(Deployment deployment)
    {
        metadataCache.clear();
        Set<String> toCommit = new HashSet<String>();
        for (DeployedFile file : deployment)
        {
            Path path = new Path(file.getPath());
            if (file.getType() == FileType.DIR)
            {
                toCommit.add(fMetaDataDirectory + File.separatorChar + path.toString() + File.separatorChar + MD_NAME);
            }
            String parent = fMetaDataDirectory + File.separatorChar + path.getParent().toString() +
                            File.separatorChar + MD_NAME;
            toCommit.add(parent);
        }
        for (String path : toCommit)
        {
            File original = new File(path);
            File old = new File(path + OLD);
            if (original.exists() && !original.renameTo(old))
            {
                throw new DeploymentException("Could not rename meta data file " + path);
            }
            
            File clone = new File(path + CLONE);
            clone.renameTo(original);
            old.delete();
        }
    }

    private void recursiveRollbackMetaData(String dir)
    {
        metadataCache.clear();
        String mdName = dir + File.separatorChar + MD_NAME;
        String clone = mdName + CLONE;
        File dClone = new File(clone);
        dClone.delete();
        DirectoryMetaData md = getDirectory(mdName);
        SortedSet<FileDescriptor> mdListing = md.getListing();
        File dDir = new File(dir);
        File[] listing = dDir.listFiles();
        for (File entry : listing)
        {
            if (entry.isDirectory())
            {
                FileDescriptor dummy = new FileDescriptor(entry.getName(),
                                                          null,
                                                          null);
                if (!mdListing.contains(dummy))
                {
                    Deleter.Delete(entry);
                }
            }
        }
        listing = dDir.listFiles();
        for (File entry : listing)
        {
            if (entry.isDirectory())
            {
                recursiveRollbackMetaData(dir + File.separatorChar + entry.getName());
            }
        }
    }

    /**
     * set that this target is busy
     */
	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public boolean isBusy() {
		return busy;
	}


	/**
	 * In memory, thread safe, cache of directory listings
	 * <p>
	 * The cache limits the number of entries it contains and discards entries. 
	 *
	 * @author Mark Rogers
	 */
	private class MetadataCache
	{
	    // InternalMap is synchronized so should be safe for multiple threads.
	    Map<String, SortedSet<FileDescriptor>> internalMap = Collections.synchronizedMap(new HashMap<String, SortedSet<FileDescriptor>>(10));
	    
	    SortedSet<FileDescriptor> lookup(String path)
	    {
	        return internalMap.get(path);
	    }
	    
	    SortedSet<FileDescriptor> put(String path, SortedSet<FileDescriptor> listing)
	    {
	        // Very simple and stupid cache, if it contains more than 10 elements then 
	        // clear everything else out.
	        if(internalMap.size() > 10)
	        {
	            clear();
	        }
	        internalMap.put(path, listing);
	        return listing;
	    }
	    
	    void clear()
	    {
	        internalMap.clear();   
	    }
	}
}

