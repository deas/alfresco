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
 * Functions to assist with integration of stores to IDE environments
 * 
 * @author uzi
 */
public class PathUtil 
{
    /**
     * Indicates whether the given path points to a development time resource.
     * 
     * This is useful for Stores that are mount on hot-deploy targets (as within
     * integrated development environments).  Stores operating in these environments
     * can safeguard to avoid penalities associated with loading development time
     * information that might be present in search paths.
     * 
     * @param path
     * @return
     */
    public static boolean isDevelopmentEnvironmentPath(String path)
    {
        boolean dev = false;
        
        if (path != null)
        {
            // subversion
            if (path.startsWith(".svn/") || path.contains("/.svn/")) 
            {
                dev = true;
            }
            
            // git
            if (path.startsWith(".git/") || path.contains("/.git/")) 
            {
                dev = true;
            }
        }
        
        return dev;
    }
}
