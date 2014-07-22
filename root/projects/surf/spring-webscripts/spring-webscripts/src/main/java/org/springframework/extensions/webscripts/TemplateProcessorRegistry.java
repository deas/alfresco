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
 * Registry of Template Processors.
 * <p>
 * If no processors are registered, the default script processor is
 * the freemarker processor.
 * 
 * @author muzquiano
 * @author Kevin Roast
 */
public class TemplateProcessorRegistry
{
    private static final Log logger = LogFactory.getLog(TemplateProcessorRegistry.class);
    
    /** The default template processor */
    private String defaultTemplateProcessorName = "freemarker";  
    
    /** Maps containing the template processors */
    private Map<String, TemplateProcessor> templateProcessors = new HashMap<String, TemplateProcessor>(8);
    private Map<String, String> templateProcessorNamesByExtension = new HashMap<String, String>(8);
    
    /** Lock to provide protection around the template processor lookup caches */
    private ReadWriteLock resourceLock = new ReentrantReadWriteLock();
    
    
    /**
     * Sets the name of the default template processor
     * 
     * @param defaultTemplateProcessor    the name of the default template processor
     */    
    public void setDefaultTemplateProcessor(String defaultTemplateProcessorName)
    {
        this.defaultTemplateProcessorName = defaultTemplateProcessorName;
    }
    
    /**
     * Registers a template processor
     * 
     * @param   templateProcessor     the template processor to register
     */
    public void registerTemplateProcessor(TemplateProcessor templateProcessor)
    {
        registerTemplateProcessor(templateProcessor, null, null);
    }
    
    /**
     * Registers a template processor
     * 
     * @param   templateProcessor     the template processor to register
     * @param   extension
     * @param   name
     */
    public void registerTemplateProcessor(TemplateProcessor templateProcessor, String extension, String name)
    {
        if (name == null && extension == null)
        {
            // try to determine name and extension from processor itself        
            if (templateProcessor instanceof BaseProcessor)
            {
                name = ((BaseProcessor)templateProcessor).getName();
                extension = ((BaseProcessor)templateProcessor).getExtension();
            }
        }
        
        // if we have a name and extension to use, register
        if (name != null && extension != null)
        {
            this.resourceLock.writeLock().lock();
            try
            {
                this.templateProcessors.put(name, templateProcessor);
                this.templateProcessorNamesByExtension.put(extension, name);
            }
            finally
            {
                this.resourceLock.writeLock().unlock();
            }
            if (logger.isInfoEnabled())
                logger.info("Registered template processor " + name + " for extension " + extension);
        }
    }
    
    /**
     * Gets the default template processor.
     * 
     * @return the default template processor
     */
    protected TemplateProcessor getDefaultTemplateProcessor()
    {
        this.resourceLock.readLock().lock();
        try
        {
            return (TemplateProcessor) this.templateProcessors.get(this.defaultTemplateProcessorName);
        }
        finally
        {
            this.resourceLock.readLock().unlock();
        }
    }
    
    /**
     * Returns the best fit template processor for the given path
     * 
     * If a template processor cannot be matched to the path, the default
     * template processor will be returned.
     * 
     * @param path the path
     * 
     * @return the template processor
     */
    public TemplateProcessor getTemplateProcessor(String path)
    {
        TemplateProcessor processor = null;
        
        int i = path.lastIndexOf('.');
        if (i != -1)
        {
            String extension = path.substring(i + 1);
            this.resourceLock.readLock().lock();
            try
            {
                String templateProcessorName = (String) templateProcessorNamesByExtension.get(extension);
                if (templateProcessorName != null)
                {
                    processor = (TemplateProcessor) this.templateProcessors.get(templateProcessorName);
                }
            }
            finally
            {
                this.resourceLock.readLock().unlock();
            }
        }
        
        if (processor == null)
        {
            processor = getDefaultTemplateProcessor();
        }
        
        return processor;
    }
    
    /**
     * Gets the template processor registered for the given extension
     * 
     * @param extension the extension
     * 
     * @return the template processor by extension
     */
    public TemplateProcessor getTemplateProcessorByExtension(String extension)
    {
        TemplateProcessor processor = null;
        
        this.resourceLock.readLock().lock();
        try
        {
            String templateProcessorName = (String) templateProcessorNamesByExtension.get(extension);
            if (templateProcessorName != null)
            {
                processor = (TemplateProcessor) this.templateProcessors.get(templateProcessorName);
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
     * First attempts to find a template processor that contains
     * the content located at the given path (using extension
     * information, if available).
     * <p>
     * If no match is found, iterates over the file extensions
     * and attempts to find a match.
     * <p>
     * Path can therefore be values like:
     * <pre>
     *   testfile.ftl
     *     - matches to file testfile.ftl using freemarker procesor
     * 
     *   testfile
     *     - matches for all extensions, potentially looking at 
     *       testfile.ftl, testfile.php, etc.
     * </pre>    
     * The extension annotated path is returned which will correctly
     * dispatch to the discovered processor.
     * 
     * @param path the path
     * 
     * @return a valid processor file path or null if no match
     */
    public String findValidTemplatePath(final String path)
    {
        String validTemplatePath = null;
        String extension = null;
        
        // look up by file extension
        int i = path.lastIndexOf('.');
        if (i != -1)
        {
            extension = path.substring(i + 1);
            TemplateProcessor processor = getTemplateProcessorByExtension(extension);
            if (processor != null && processor.hasTemplate(path))
            {
                validTemplatePath = path;
            }
        }
        
        if (validTemplatePath == null)
        {
            // look across all of the extensions
            final String[] extensions = this.getRegisteredExtensions();
            for (final String ext : extensions)
            {
                if (!ext.equals(extension))
                {
                    TemplateProcessor processor = getTemplateProcessorByExtension(ext);
                    final String template = path + '.' + ext;
                    if (processor.hasTemplate(template))
                    {
                        validTemplatePath = template;
                        break;
                    }
                }
            }
        }
        
        return validTemplatePath;
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
            return templateProcessorNamesByExtension.keySet().toArray(new String[templateProcessorNamesByExtension.keySet().size()]);
        }
        finally
        {
            this.resourceLock.readLock().unlock();
        }
    }
    
    /**
     * Gets the extension for given processor.
     * 
     * @param templateProcessor the template processor
     * 
     * @return the extension for processor
     */
    public String getExtensionForProcessor(TemplateProcessor templateProcessor)
    {
        String ext = null;
        
        final String[] extensions = this.getRegisteredExtensions();
        for (int n=0; n<extensions.length; n++)
        {
            final String extension = extensions[n];
            TemplateProcessor processor = getTemplateProcessorByExtension(extension);
            if (processor == templateProcessor)
            {
                ext = extension;
                break;
            }
        }
        
        return ext;        
    }
    
    /**
     * Resets all of the registered template processors
     */
    public void reset()
    {
        this.resourceLock.readLock().lock();
        try
        {
            for (TemplateProcessor p : this.templateProcessors.values())
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