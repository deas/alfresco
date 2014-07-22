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
 * Encapsulates the mapping of URIs to Web Scripts
 */
public interface UriIndex
{
    /**
     * Clear the index
     */
    public void clear();
    
    /**
     * Gets size of index (i.e. number of Uris indexed)
     * 
     * @return  index size
     */
    public int getSize();
    
    /**
     * Register a URI with a Web Script
     * 
     * @param script
     * @param uri
     */
    public void registerUri(WebScript script, String uri);
    
    /**
     * Gets a Web Script given an HTTP Method and URI
     * 
     * @param method  http method
     * @param uri  uri
     * @return  script match (pair of script and uri that matched)
     */
    public Match findWebScript(String method, String uri);
}
