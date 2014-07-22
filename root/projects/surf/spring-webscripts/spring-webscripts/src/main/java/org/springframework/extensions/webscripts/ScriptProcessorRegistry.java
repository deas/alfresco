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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.processor.BaseProcessor;


/**
 * Registry of Script Processors.
 * <p>
 * If no processors are registered, the default script processor is
 * the javascript processor.
 * 
 * @author muzquiano
 * @author Kevin Roast
 */
public class ScriptProcessorRegistry
{
    private static final Log logger = LogFactory.getLog(ScriptProcessorRegistry.class);
    
    /** The name of the default script processor */
    private String defaultScriptProcessorName = "javascript";
    
    /** Maps containing the script processors */
    private Map<String, ScriptProcessor> scriptProcessors = new HashMap<String, ScriptProcessor>(8);
    private Map<String, String> scriptProcessorNamesByExtension = new HashMap<String, String>(8);
    
    /** Lock to provide protection around the script processor lookup caches */
    private ReadWriteLock resourceLock = new ReentrantReadWriteLock();
    
    
    /**
     * Sets the name of the default script processor
     * 
     * @param defaultScriptProcessor    the name of the default script processor
     */
    public void setDefaultScriptProcessor(String defaultScriptProcessorName)
    {
        this.defaultScriptProcessorName = defaultScriptProcessorName;
    }

    /**
     * Registers a script processor
     * 
     * @param   scriptProcessor     the script processor to register
     */
    public void registerScriptProcessor(ScriptProcessor scriptProcessor)
    {
        registerScriptProcessor(scriptProcessor, null, null);
    }
    
    /**
     * Registers a script processor
     * 
     * @param   scriptProcessor     the script processor to register
     * @param   extension
     * @param   name
     */
    public void registerScriptProcessor(ScriptProcessor scriptProcessor, String extension, String name)
    {
        if (name == null && extension == null)
        {
            // try to determine name and extension from processor itself
            if (scriptProcessor instanceof BaseProcessor)
            {
                name = ((BaseProcessor)scriptProcessor).getName();
                extension = ((BaseProcessor)scriptProcessor).getExtension();
            }
        }
        
        // if we have a name and extension to use, register
        if (name != null && extension != null)
        {            
            this.resourceLock.writeLock().lock();
            try
            {
                this.scriptProcessors.put(name, scriptProcessor);
                this.scriptProcessorNamesByExtension.put(extension, name);
            }
            finally
            {
                this.resourceLock.writeLock().unlock();
            }
            if (logger.isInfoEnabled())
                logger.info("Registered script processor " + name + " for extension " + extension);
        }
    }
    
    /**
     * Gets the default script processor.
     * 
     * @return the default script processor
     */
    protected ScriptProcessor getDefaultScriptProcessor()
    {
        this.resourceLock.readLock().lock();
        try
        {
            return (ScriptProcessor) this.scriptProcessors.get(this.defaultScriptProcessorName);
        }
        finally
        {
            this.resourceLock.readLock().unlock();
        }
    }
    
    /**
     * Returns the script processor that matches the file
     * extension for the given path.
     * 
     * If a script processor cannot be matched to the path, the default
     * script processor will be returned.
     * 
     * @param path the path
     * 
     * @return the script processor
     */
    public ScriptProcessor getScriptProcessor(String path)
    {
        ScriptProcessor processor = null;
        
        int i = path.lastIndexOf('.');
        if (i != -1)
        {
            String extension = path.substring(i + 1);
            processor = getScriptProcessorByExtension(extension);
        }
        
        if (processor == null)
        {
            processor = getDefaultScriptProcessor();
        }
        
        return processor;
    }
    
    /**
     * Returns the best fit script processor for the given script
     * content object.
     * 
     * If a script processor cannot be matched, the default
     * script processor will be returned.
     * 
     * @param path the path
     * 
     * @return the script processor
     */
    public ScriptProcessor getScriptProcessor(ScriptContent scriptContent)
    {
        return getScriptProcessor(scriptContent.getPath());
    }
    
    /**
     * Gets the script processor registered for the given extension
     * 
     * @param extension the extension
     * 
     * @return the script processor by extension or null if no match
     */
    public ScriptProcessor getScriptProcessorByExtension(String extension)
    {
        ScriptProcessor processor = null;
        
        this.resourceLock.readLock().lock();
        try
        {
            String scriptProcessorName = (String) scriptProcessorNamesByExtension.get(extension);
            if (scriptProcessorName != null)
            {
                processor = (ScriptProcessor) this.scriptProcessors.get(scriptProcessorName);
            }
        }
        finally
        {
            this.resourceLock.readLock().unlock();
        }
        
        return processor;
    }
    
    /**
     * Returns a variation on the provided path that exists and
     * is processable by one of the processors in this registry.
     * <p>
     * First attempts to find a script processor that contains
     * the content located at the given path (using extension
     * information, if available).
     * <p>
     * If no match is found, iterates over the file extensions
     * and attempts to find a match.
     * <p>
     * Path can therefore be values like:
     * <pre>
     *   testfile.js
     *     - matches to file testfile.js using javascript procesor
     *     
     *   testfile
     *     - matches for all extensions, potentially looking at 
     *       testfile.js, testfile.groovy, etc.
     * </pre>
     * The extension annotated path is returned which will correctly
     * dispatch to the discovered processor.
     * 
     * @param path the path
     * 
     * @return a valid processor file path or null if no match
     */
    public String findValidScriptPath(String path)
    {
        String validScriptPath = null;
        
        // look up by file extension
        int i = path.lastIndexOf('.');
        if (i != -1)
        {
            String extension = path.substring(i + 1);
            ScriptProcessor processor = getScriptProcessorByExtension(extension);
            if (processor != null && processor.findScript(path) != null)
            {
                validScriptPath = path;
            }
        }
        
        if (validScriptPath == null)
        {
            // look across all of the extensions
            final String[] extensions = this.getRegisteredExtensions();
            for (int n=0; n<extensions.length; n++)
            {
                final String extension = extensions[n];
                ScriptProcessor processor = getScriptProcessorByExtension(extension);
                final String script = path + '.' + extension;
                if (processor.findScript(script) != null)
                {
                    validScriptPath = script;
                    break;
                }
            }
        }
        
        return validScriptPath;
    }
    
    /**
     * Returns the extensions with registered processors
     * 
     * @return the registered extensions
     */
    public String[] getRegisteredExtensions()
    {
        this.resourceLock.readLock().lock();
        try
        {
            return scriptProcessorNamesByExtension.keySet().toArray(new String[scriptProcessorNamesByExtension.keySet().size()]);
        }
        finally
        {
            this.resourceLock.readLock().unlock();
        }
    }
    
    /**
     * Gets the extension for given processor.
     * 
     * @param scriptProcessor the script processor
     * 
     * @return the extension for processor
     */
    public String getExtensionForProcessor(ScriptProcessor scriptProcessor)
    {
        String ext = null;
        
        final String[] extensions = this.getRegisteredExtensions();
        for (int n=0; n<extensions.length; n++)
        {
            final String extension = extensions[n];
            ScriptProcessor processor = getScriptProcessorByExtension(extension);
            if (processor == scriptProcessor)
            {
                ext = extension;
                break;
            }
        }
        
        return ext;        
    }
    
    /**
     * Resets all of the registered script processors
     */
    public void reset()
    {
        this.resourceLock.readLock().lock();
        try
        {
            for (ScriptProcessor p : this.scriptProcessors.values())
            {
                p.reset();
            }
        }
        finally
        {
            this.resourceLock.readLock().unlock();
        }
    }    
}