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

import java.io.Serializable;

import org.alfresco.deployment.FileType;

/**
 * This is a record of a deployed file. It holds the pre-commit location
 * of a file, the final location of the file, and the GUID of the file.
 * @author britt
 */
public class DeployedFile implements Serializable
{
    private static final long serialVersionUID = -8500167211804636309L;

    private FileType fType;
    
    private String fPreLocation;
    
    private String fPath;
    
    private String fGUID;
    
    private boolean fCreate;
    
    private boolean fFile;
    
    /**
     * 
     * @param type
     * @param preLocation where the temporary file is stored
     * @param path the final destination of the file.
     * @param guid
     * @param create true if this is a new file.
     */
    public DeployedFile(FileType type,
                        String preLocation,
                        String path,
                        String guid,
                        boolean create,
                        boolean file)
    {
        fType = type;
        fPreLocation = preLocation;
        fPath = path;
        fGUID = guid;
        fCreate = create;
        fFile = file;
    }

    /**
     * Get the path
     * 
     * @return the path 
     */
    public String getPath()
    {
        return fPath;
    }

    /**
     * Get the GUID which uniquely identifies this file
     * 
     * @return the GUID
     */
    public String getGuid()
    {
        return fGUID;
    }
    
    /**
     * Was this a new file or directory create 
     * 
     * @return true this is a new file or directory
     */
    public boolean isCreate()
    {
        return fCreate;
    }
    
    /**
     * Is this a file or directory ?
     * 
     * @return true this is a file, false this is a directory
     */
    public boolean isFile()
    {
        return fFile;
    }

    
    /**
     * The pre-location is where the file is stored temporarily prior to commit.
     * 
     * @return the PreLocation
     */
    public String getPreLocation()
    {
        return fPreLocation;
    }
    
    /**
     * Get the type
     * 
     * @return the Type
     */
    public FileType getType()
    {
        return fType;
    }
    
    @Override
    public boolean equals(Object o)
    {
    	if(! (o instanceof DeployedFile))
    	{
    		return false;
    	}
    	DeployedFile other = (DeployedFile)o;
    	
        return this.getGuid().equals(other.getGuid());
    		
    }
   
    @Override 
    public int hashCode() 
    {
    	return fGUID.hashCode();
    }
}
