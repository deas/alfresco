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
import java.util.TreeMap;


/**
 * Basic implementation of a Web Script Path
 * 
 * Used for package & url trees.
 * 
 * @author davidc
 */
public class PathImpl implements Path
{
    private String path;
    private PathImpl parent = null;
    private Map<String, PathImpl> children = new TreeMap<String, PathImpl>();
    private Map<String, WebScript> scripts = new TreeMap<String, WebScript>();
    

    /**
     * Helper to concatenate paths
     * 
     * @param path1
     * @param path2
     * @return  concatenated path
     */
    public static String concatPath(String path1, String path2)
    {
        return path1.equals("/") ? path1 + path2 : path1 + "/" + path2;
    }

    
    /**
     * Construct
     * 
     * @param path
     */
    public PathImpl(String path)
    {
        this.path = path;
    }

    /**
     * Create a Child Path
     * 
     * @param path  child path name
     * @return  child path
     */
    public PathImpl createChildPath(String path)
    {
        PathImpl child = new PathImpl(concatPath(this.path, path));
        child.parent = this;
        children.put(child.path, child);
        return child;
    }

    /**
     * Associate Web Script with Path
     * 
     * @param script
     */
    public void addScript(WebScript script)
    {
        scripts.put(script.getDescription().getId(), script);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptPath#getChildren()
     */
    public Path[] getChildren()
    {
        Path[] childrenArray = new Path[children.size()];
        return children.values().toArray(childrenArray);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptPath#getScripts()
     */
    public WebScript[] getScripts()
    {
        WebScript[] scriptsArray = new WebScript[scripts.size()];
        return scripts.values().toArray(scriptsArray);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptPath#getName()
     */
    public String getName()
    {
        String name = "";
        int i = path.lastIndexOf("/");
        if (i != -1 && i != (path.length() -1))
        {
            name = path.substring(i + 1);
        }
        return name;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptPath#getParent()
     */
    public Path getParent()
    {
        return parent;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptPath#getPath()
     */
    public String getPath()
    {
        return path;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return path;
    }

}
