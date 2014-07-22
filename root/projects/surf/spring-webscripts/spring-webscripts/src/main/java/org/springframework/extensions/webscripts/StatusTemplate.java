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
 * Status Template
 */
public class StatusTemplate
{
    /**
     * Construct
     * 
     * @param path
     * @param format
     */
    public StatusTemplate(String path, String format)
    {
        this.path = path;
        this.format = format;
    }

    /**
     * Gets template path
     * 
     * @return  path
     */
    public String getPath()
    {
        return path;
    }
    
    /**
     * Gets template format
     * 
     * @return  format
     */
    public String getFormat()
    {
        return format;
    }

    
    private String path;
    private String format;
}
