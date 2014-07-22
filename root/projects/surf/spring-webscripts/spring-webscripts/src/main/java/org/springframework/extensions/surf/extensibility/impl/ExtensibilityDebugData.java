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
package org.springframework.extensions.surf.extensibility.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.extensions.surf.extensibility.ExtensibilityDirective;

/**
 * <p>Used to maintain the debug data about the {@link ExtensibilityDirective} instances used in a single request.</p>
 * 
 * @author David Draper
 */
public class ExtensibilityDebugData
{
    /**
     * <p>A map of directive to maps of id to extensibility data.</p>
     */
    private Map<String, Map<String, Data>> dataMap = new HashMap<String, Map<String, Data>>();
    
    /**
     * <p>Add new extensibility data for a directive</p>
     * @param id The id of the directive
     * @param directive The directive type
     * @param path The path of the file that provides the directive.
     */
    public void addData(String id, String directive, String path)
    {
        // Get the list of data items for the current directive, and create one if necessary...
        Map<String, Data> currDirectiveData = this.dataMap.get(directive);
        if (currDirectiveData == null)
        {
            currDirectiveData = new HashMap<String, Data>();
            this.dataMap.put(directive, currDirectiveData);
        }
        
        Data currData = currDirectiveData.get(id);
        if (currData == null)
        {
            currData = new Data(id, directive, path);
            currDirectiveData.put(id, currData);
        }
        else
        {
            currData.addPath(path);
        }
    }
    
    /**
     * @return The {@link Set} of {@link ExtensibilityDirective} types used.
     */
    public Set<String> getDirectives()
    {
        return this.dataMap.keySet();
    }
    
    /**
     * <p>Gets the debug data for a specific directive type</p>
     * @param directive The directive type to retrieve data for.
     * @return A list of {@link Data} objects.
     */
    public List<Data> getDirectiveData(String directive)
    {
        List<Data> data = new ArrayList<Data>();
        Map<String, Data> directiveData = this.dataMap.get(directive);
        if (directiveData != null)
        {
            data.addAll(directiveData.values());
        }
        return data;
    }
    
    /**
     * <p>Represents extensibility directive debug data.</p>
     */
    public class Data
    {
        private Data(String id, String directive, String path)
        {
            this.id = id;
            this.directive = directive;
            this.paths = new ArrayList<String>();
            this.paths.add(path);
        }
        
        private String id = null;
        private String directive = null;
        private List<String> paths;
        public void addPath(String path)
        {
            this.paths.add(path);
        }
        public String getId()
        {
            return id;
        }
        public String getDirective()
        {
            return directive;
        }
        public List<String> getPaths()
        {
            return paths;
        }
    }
}
