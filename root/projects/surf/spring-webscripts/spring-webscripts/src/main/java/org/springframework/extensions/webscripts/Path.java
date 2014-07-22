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
 * Web Script Path
 * 
 * @author davidc
 */
public interface Path
{
    /**
     * Gets the full path
     * 
     * @return  path
     */
    public String getPath();

    /**
     * Gets the name of the path (last path segment)
     *  
     * @return  name
     */
    public String getName();
    
    /**
     * Gets the parent path
     * 
     * @return  path
     */
    public Path getParent();
    
    /**
     * Gets the child paths
     * 
     * @return  child paths
     */
    public Path[] getChildren();
    
    /**
     * Gets Web Scripts associated with this path
     * 
     * @return  web scripts
     */
    public WebScript[] getScripts();
    
}
