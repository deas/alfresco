/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;


/**
 * Script / Template Model representing the server hosting the Web Script Framework
 * 
 * @author davidc
 */
public interface ServerModel
{
    /**
     * Gets the Container Name
     * 
     * @return  container name
     */
    public String getContainerName();

    /**
     * Gets the Id of the server instance
     * 
     * @return  id
     */
    public String getId();
    
    /**
     * Gets the Name of the server instance
     */
    public String getName();
    
    /**
     * Gets the major version number, e.g. <u>1</u>.2.3
     * 
     * @return  major version number
     */
    public String getVersionMajor();
    
    /**
     * Gets the minor version number, e.g. 1.<u>2</u>.3
     * 
     * @return  minor version number
     */
    public String getVersionMinor();
    
    /**
     * Gets the version revision number, e.g. 1.2.<u>3</u>
     * 
     * @return  revision number
     */
    public String getVersionRevision();

    /**
     * Gets the version label
     * 
     * @return  the version label
     */
    public String getVersionLabel();
    
    /**
     * Gets the build number 
     * 
     * @return  the build number i.e. build-1
     */
    public String getVersionBuild();

    /**
     * Gets the full version number
     * 
     * @return  full version number as major.minor.revision (label)
     */
    public String getVersion();
    
    /**
     * Gets the edition
     *  
     * @return  the edition
     */
    public String getEdition();
    
    /**
     * Gets the schema number
     * 
     * @return a positive integer
     */
    public int getSchema();

}
