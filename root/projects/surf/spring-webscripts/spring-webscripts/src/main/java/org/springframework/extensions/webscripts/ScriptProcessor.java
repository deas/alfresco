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

import java.util.Map;


/**
 * Web Script Processor
 * 
 * @author davidc
 */
public interface ScriptProcessor
{

    /**
     * Find a script at the specified path (within registered Web Script stores)
     * 
     * @param path   script path
     * @return  script location (or null, if not found)
     */
    public ScriptContent findScript(String path);

    /**
     * Execute script
     * 
     * @param path  script path
     * @param model  model
     * @return  script result
     * @throws ScriptException
     */
    public Object executeScript(String path, Map<String, Object> model);

    /**
     * Execute script
     *  
     * @param location  script location
     * @param model  model
     * @return  script result
     */
    public Object executeScript(ScriptContent location, Map<String, Object> model);

    /**
     * Unwrap value returned by script
     * 
     * TODO: Remove this method when value conversion is truly hidden within script engine
     * 
     * @param path   the path to the file
     * @param value  value to unwrap
     * @return  unwrapped value
     */
    public Object unwrapValue(Object value);
    
    /**
     * Reset script cache
     */
    public void reset();

}
