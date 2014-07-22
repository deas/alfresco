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

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * <p>This class has been created for use the {@link AbstractWebScript} when retrieving {@link ResourceBundle} 
 * instances provided by extension modules. Because it is not mandatory for a module to provide a bundle it
 * is quite possible that a search will return no value. In order to prevent repeatedly searching unsuccessfully
 * for the same file this singleton object should be cached instead. 
 * in caches to indicate 
 * @author David Draper
 */
public class ModuleBundleSentinel extends ResourceBundle
{
    /**
     * <p>Private constructor to ensure that static <code>getInstance()</code> method must be used</p>
     */
    private ModuleBundleSentinel()
    {
    }
    
    /**
     * <p>Static reference to the singleton instance.<p>
     */
    private static ModuleBundleSentinel me;
    
    /**
     * <p>Retrieves the singleton instance and creates it if it does not exist.</p>
     * @return The singleton instance.
     */
    public static ModuleBundleSentinel getInstance()
    {
        if (me == null)
        {
            me = new ModuleBundleSentinel();
        }
        return me;
    }
    
    /**
     * <p>Implemented to fulfil the abstract superclass contract.</p>
     * @return <code>null</code>
     */
    @Override
    protected Object handleGetObject(String key)
    {
        return null;
    }

    /**
     * <p>Implemented to fulfil the abstract superclass contract.</p>
     * @return <code>null</code>
     */
    @Override
    public Enumeration<String> getKeys()
    {
        return null;
    }
}
