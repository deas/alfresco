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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

/**
 * <p>Extends <code>PropertyResourceBundle</code> in order to provide two new capabilities. The first is to 
 * store the path where the properties file used to create the <code>InputStream</code> is located and the second
 * is to allow additional <code>ResourceBundle</code> properties to be merged into an instance.</p>
 * <p>The rational of these capabilities is to allow a <code>WebScript</code> to locate and merge extension module
 * properties files.</p>
 * 
 * @author David Draper
 */
public class WebScriptPropertyResourceBundle extends ResourceBundle
{
    /**
     * <p>The location of the properties file that was used to instantiate the <code>WebScriptPropertyResourceBundle</code>
     * instance. This field is set by the constructor.</p>
     */
    private String resourcePath = null;

    /**
     * @return The location of the properties file that was used to instantiate the <code>WebScriptPropertyResourceBundle</code>
     * instance.
     */
    public String getResourcePath()
    {
        return resourcePath;
    }

    /**
     * <p>A {@link Map} containing all the properties that have been merged from multiple {@link ResourceBundle} instances.</p>
     */
    private Map<String, Object> resources = new HashMap<String, Object>();
    
    /**
     * <p>A {@link StringBuilder} instance containing all the paths of the {@link ResourceBundle} instances that have been merged
     * into this instance. This value is intended to be use to help generate a key for caching JSON formatted resource output 
     * in the {@link AbstractWebScript} class.</p>
     */
    private StringBuilder mergedBundlePaths = new StringBuilder();
    
    /**
     * @return Returns the {@link StringBuilder} instance containing the paths of all the {@link ResourceBundle} instances that have
     * been merged into this instance.
     */
    public StringBuilder getMergedBundlePaths()
    {
        return mergedBundlePaths;
    }

    /**
     * <p>Instantiates a new <code>WebScriptPropertyResourceBundle</code>.</p>
     * 
     * @param stream The <code>InputStream</code> passed on to the super class constructor.
     * @param resourcePath The location of the properties file used to create the <code>InputStream</code>
     * @throws IOException
     */
    public WebScriptPropertyResourceBundle(InputStream stream, String resourcePath) throws IOException
    {
        PropertyResourceBundle p = new PropertyResourceBundle(stream);
        this.resourcePath = resourcePath;
        merge(resourcePath, p);
    }
    
    /**
     * <p>Constructor for instantiating from an existing {@link ResourceBundle}. This calls the <code>merge</code>
     * method to copy the properties from the bundle into the <code>resources</code> map.
     * 
     * @param baseBundle
     * @param resourcePath
     */
    public WebScriptPropertyResourceBundle(ResourceBundle baseBundle, String resourcePath)
    {
        super();
        this.resourcePath = resourcePath;
        merge(resourcePath, baseBundle);
    }

    /**
     * <p>Merges the properties of a <code>ResourceBundle</code> into the current <code>WebScriptPropertyResourceBundle</code>
     * instance. This will override any values mapped to duplicate keys in the current merged properties.</p>
     * 
     * @param resourceBundle The <code>ResourceBundle</code> to merge the properties of.
     * @return <code>true</code> if the bundle was successfully merged and <code>false</code> otherwise. 
     */
    public void merge(String bundlePath, ResourceBundle resourceBundle)
    {
        if (resourceBundle != null)
        {
            Enumeration<String> keys = resourceBundle.getKeys();
            while (keys.hasMoreElements())
            {
                String key = keys.nextElement();
                this.resources.put(key, resourceBundle.getObject(key));
            }
        }
        
        // Update the paths merged in this bundle...
        mergedBundlePaths.append(bundlePath);
        mergedBundlePaths.append(":");
    }

    /**
     * <p>Overrides the super class implementation to return an object located in the merged bundles</p>
     * 
     * @return An <code>Object</code> from the merged bundles 
     */
    @Override
    public Object handleGetObject(String key)
    {
        if (key == null) {
            throw new NullPointerException();
        }
        return this.resources.get(key);
    }
    
    /**
     * <p>Overrides the super class implementation to return an enumeration of keys from all the merged bundles</p>
     * 
     * @return An <code>Enumeration</code> of the keys across all the merged bundles. 
     */
    @Override
    public Enumeration<String> getKeys()
    {
        Vector<String> keys = new Vector<String>(this.resources.keySet());
        return keys.elements();
    }
    
    /**
     * <p>Overrides the super class implementation to return the <code>Set</code> of keys from all merged
     * bundles</p>
     * 
     * @return A <code>Set</code> of keys obtained from all merged bundles 
     */
    @Override
    protected Set<String> handleKeySet()
    {
        return this.resources.keySet();
    }

    /**
     * <p>Overrides the super class implementation to check the existence of a key across all merged
     * bundles</p>
     * 
     * @return <code>true</code> if the key is present and <code>false</code> otherwise.
     */
    @Override
    public boolean containsKey(String key)
    {
        return this.resources.containsKey(key);
    }

    /**
     * <p>Overrides the super class implementation to return the <code>Set</code> of keys from all merged
     * bundles</p>
     * 
     * @return A <code>Set</code> of keys obtained from all merged bundles 
     */
    @Override
    public Set<String> keySet()
    {
        return this.resources.keySet();
    }
}
