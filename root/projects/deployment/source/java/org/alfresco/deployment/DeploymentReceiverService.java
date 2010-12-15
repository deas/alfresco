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

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Public Interface for File System Deployment Receiver (FSR)
 * 
 * @author britt
 */
public interface DeploymentReceiverService
{
    /**
     * Start a deployment. 
     * @param storeName - the name of the store being deployed.
     * @param target The target to deploy to. A target is simply a key
     * to a receiver side deployment configuration.  
     * @param version - the version being deployed.
     * @param user The user name for authentication.
     * @param password The password for the user.
     * @return information on the new deployment.
     */
    public DeploymentToken begin(String target, String storeName, int version, String user, char[] password);
    
    /**
     * Signals that the deployment should prepare
     * @param ticket The transaction ticket.
     */
    public void prepare(String ticket);
    
    /**
     * Signals that the deployment should commit.  
     * @param ticket The transaction ticket.
     */
    public void commit(String ticket);
    
    /**
     * Signals that the deployment should be aborted and
     * rolled back.
     * @param ticket
     */
    public void abort(String ticket);
    
    /**
     * Send a file to a path.
     * @param ticket
     * @param create - true new file
     * @param path
     * @param guid
     * @param encoding
     * @param mimeType
     * @param aspects - full qualified names of the aspects that this file is associated with.
     * @param props - map of full qualified names and property values.
     * 
     * @return an open output steam for writing content
     */
    public OutputStream send(String ticket, boolean create, String path, String guid, String encoding, String mimeType, Set<String> aspects, Map<String, Serializable> properties);
        
    /**
     * Create a new directory.
     * @param ticket
     * @param path
     * @param guid The GUID (Version) of the directory to be created.
     */
    public void createDirectory(String ticket, String path, String guid, Set<String> aspects, Map<String, Serializable> properties);
    
    /**
     * Set the Guid (Version) on a directory.
     * @param ticket
     * @param path
     * @param guid
     */
    public void updateDirectory(String ticket, String path, String guid, Set<String> aspects, Map<String, Serializable> properties);
    
    /**
     * Delete a file or directory.
     * @param ticket
     * @param path
     */
    public void delete(String ticket, String path);
    
    /**
     * Get a listing of a directory.
     * @param ticket
     * @param path
     * @return The listing in name sorted order.
     */
    public List<FileDescriptor> getListing(String ticket, String path);
    
          
}
