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

package org.springframework.extensions.config;

import java.util.Map;

/**
 * Definition of a object that represents the results of a lookup.
 * 
 * @author gavinc
 */
public interface Config
{
    /**
     * Returns the config element with the given name, if there is more
     * than one with the given name the first one added is returned.
     * 
     * @param name   Name of the config element to retrieve
     * 
     * @return The ConfigElement object or null if it doesn't exist
     */
    public ConfigElement getConfigElement(String name);
    
    /**
     * Shortcut method to get the config element with the given name and
     * return its value. If the config element does not exist, null is
     * returned. If there is more than one with the given name the first
     * one added is returned.
     * 
     * @param name   Name of the config element value to retrieve
     * 
     * @return The ConfigElement value or null if it doesn't exist
     */
    public String getConfigElementValue(String name);
    
    /**
     * Returns all the config elements
     * 
     * @return All the config elements
     */
    public Map<String, ConfigElement> getConfigElements();

    /**
     * Determines whether the given config element exists
     *  
     * @param name The name of the config element to look for
     * @return true if the config element exists
     */
    public boolean hasConfigElement(String name);
}
