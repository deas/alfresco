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

import java.io.InputStream;
import java.io.Reader;


/**
 * Web Script Content
 * 
 * @author davidc
 */
public interface ScriptContent
{
    /**
     * Gets an input stream to the contents of the script
     * 
     * @return  the input stream
     */
    InputStream getInputStream();
    
    /**
     * Gets a reader to the contents of the script
     * 
     * @return  the reader
     */
    Reader getReader();

    /**
     * Gets the path to the content
     * 
     * @return  path
     */
    public String getPath();
    
    /**
     * Gets path description
     * 
     * @return  human readable version of path
     */
    public String getPathDescription();
    
    /**
     * Returns true if the script content is considered cachedable - i.e. classpath located or similar.
     * Else the content will be compiled/interpreted on every execution i.e. repo content.
     * 
     * @return true if the script content is considered cachedable, false otherwise
     */
    boolean isCachable();
    
    /**
     * Returns true if the script location is considered secure - i.e. on the app-server classpath.
     * Secure scripts may access java.* libraries and instantiate pure Java objects directly. Unsecure
     * scripts only have access to pre-configure host objects and cannot access java.* libs.
     * 
     * @return true if the script location is considered secure
     */
    boolean isSecure();
}
