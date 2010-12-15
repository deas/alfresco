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

import org.alfresco.deployment.impl.DeploymentException;

/**
 * The deployment target is the interface that is provided by 
 * deployment targets.
 * 
 * @see org.alfresco.deployment.impl.fsr.FileSystemDeploymentTarget
 * @see org.alfresco.deployment.impl.asr.AVMDeploymentTarget
 *  
 * @author mrogers
 */
public interface DeploymentTarget 
{
    /**
     * Start a new deployment. 
     * 
     * <br />
     * 
     * @param target The name of the target which is being deployed to. 
     * @param storeName the name of the store being deployed.  (authoring instance)
     * @param version the version of the store being deployed. (authoring instance)
     * @param user The user name for authentication.
     * @param password The password for authentication.
     * @return A deployment ticket which uniquely identifies the deployment.
     * @throws DeploymentException - unable to start a new deployment
     */
    public String begin(String target, String storeName, int version, String user, char[] password);
    
    /**
     * Signals that the deployment should be prepared.
     * @param ticket The deployment ticket.
     */
    public void prepare(String ticket) throws DeploymentException;
    
    /**
     * Signals that the deployment is finished and should
     * commit. 
     * @param ticket The deployment ticket, returned by an earlier call to 'begin'.
     */
    public void commit(String ticket);
    
    /**
     * Signals that the deployment should be aborted and
     * rolled back.
     * @param ticket The deployment ticket, returned by an earlier call to 'begin'.
     */
    public void abort(String ticket);
    
    /**
     * Send a file to a path.
     * 
     * <br />
     * The file may either be new or may be an update.
     * <br />
     * Implementations should also deal with the case where a directory becomes a file.   
     * If a file is created via this method then it should overwrite any existing directory on the same path.
     * 
     * @param ticket The deployment ticket, returned by an earlier call to 'begin'
     * @param create - true - create a new file, false update an existing file.
     * @param path path of the new file 
     * @param guid unique identifier for this particular version of the file
     * @param encoding the encoding of the file
     * @param mimeType the mime type of the file
     * @param aspects aspects to apply to the file
     * @param props properties.
     * @return an open output stream to receive content.
     * 
     * @throws DeploymentException - unable to send, deployment should be aborted
     */
    public OutputStream send(String ticket, boolean create, String path, String guid, String encoding, String mimeType, Set<String>aspects, Map<String, Serializable> props) throws DeploymentException;
    
    /**
     * Create a directory.
     * 
     * <br />
     * Implementations should also deal with the case where a file becomes a directory.   
     * If a directory is created via this method then it should overwrite any existing file on the same path.

     * @param ticket the deployment ticket, returned by an earlier call to 'begin'
     * @param path path of the new file
     * @param guid The GUID (version) of the directory to be created.
     * @param aspects aspects to apply to the new directory
     * @param props properties for the new directory
     * @throws DeploymentException - unable to get the listing, deployment should be aborted
     */
    public void createDirectory(String ticket, String path, String guid, Set<String>aspects, Map<String, Serializable> properties) throws DeploymentException;
    
    /**
     * Update a directory .
     * @param ticket the deployment ticket, returned by an earlier call to 'begin'
     * @param path path of the new file
     * @param guid The GUID (version) of the directory to be created.
     * @param aspects aspects to apply to the new directory
     * @param props properties for the new directory
     * @throws DeploymentException - unable to set the GUID, deployment should be aborted
     */
    public void updateDirectory(String ticket, String path, String guid, Set<String>aspects, Map<String, Serializable> properties) throws DeploymentException;
    
    /**
     * Delete a file or directory.
     * @param ticket the deployment ticket, returned by an earlier call to 'begin'
     * @param path
     * @throws DeploymentException - unable to delete, deployment should be aborted
     */
    public void delete(String ticket, String path) throws DeploymentException;
    
    /**
     * Get a listing of a directory.
     * @param ticket the deployment ticket, returned by an earlier call to 'begin'
     * @param path
     * @return The listing in name sorted order.
     * @throws DeploymentException - unable to get the listing, deployment should be aborted
     */
    public List<FileDescriptor> getListing(String ticket, String path) throws DeploymentException;

    /**
     * Get the current version for this target and source Store
     * 
     * @param target
     * @param storeName
     * 
     * Returns the current version (authoring version), 0 means no version has been deployed, -1 means the version is unknown or not 
     * implemented by this target.
     */
    public int getCurrentVersion(String target, String storeName);
}
