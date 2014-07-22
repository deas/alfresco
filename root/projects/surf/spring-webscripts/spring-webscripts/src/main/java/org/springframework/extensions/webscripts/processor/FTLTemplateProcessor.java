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

package org.springframework.extensions.webscripts.processor;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.element.GenericConfigElement;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.TemplateConfigModel;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.json.JSONUtils;

import freemarker.cache.MruCacheStorage;
import freemarker.cache.StrongCacheStorage;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * FTL Template Processor for Alfresco Web Framework
 *  
 * @author davidc
 * @author kevinr
 */
public class FTLTemplateProcessor extends AbstractTemplateProcessor
{
    private static final Log logger = LogFactory.getLog(FTLTemplateProcessor.class);

    /** Default template input encoding */
    private String defaultEncoding;
    
    /** FreeMarker config for templates */
    private Configuration templateConfig;
    
    /** FreeMarker config for string based generated template */
    private Configuration stringConfig;
        
    /** Time in seconds between FreeMarker checking for new template instances */
    private int updateDelay = 0;
    
    /** Size of the FreeMarker in-memory template cache */
    private int cacheSize = 256;

    /**
     * @param defaultEncoding
     */
    public void setDefaultEncoding(String defaultEncoding)
    {
        this.defaultEncoding = defaultEncoding;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.TemplateProcessor#getDefaultEncoding()
     */
    public String getDefaultEncoding()
    {
        return this.defaultEncoding;
    }
    
    /**
     * @param updateDelay the time in seconds between checks on the modified date for cached templates
     */
    public void setUpdateDelay(int updateDelay)
    {
        this.updateDelay = updateDelay;
    }
    
    /**
     * @param cacheSize the size of the MRU template cache, default is 256
     */
    public void setCacheSize(int cacheSize)
    {
        if (cacheSize >= 0)
        {
            this.cacheSize = cacheSize;
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.processor.AbstractTemplateProcessor#init()
     */
    public void init()
    {
        super.init();
        
        this.initConfig();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.processor.Processor#getExtension()
     */
    public String getExtension()
    {
        return "ftl";
    }

    /* (non-Javadoc)
     * @see org.alfresco.processor.Processor#getName()
     */
    public String getName()
    {
        return "freemarker";
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.TemplateProcessor#process(java.lang.String, java.lang.Object, java.io.Writer)
     */
    public void process(String template, Object model, Writer out)
    {
        if (template == null || template.length() == 0)
        {
            throw new IllegalArgumentException("Template name is mandatory.");
        }
        if (model == null)
        {
            throw new IllegalArgumentException("Model is mandatory.");
        }
        if (out == null)
        {
            throw new IllegalArgumentException("Output Writer is mandatory.");
        }
        
        try
        {
            long startTime = 0;
            if (logger.isDebugEnabled())
            {
                logger.debug("Executing template: " + template);
                startTime = System.nanoTime();
            }
            
            addProcessorModelExtensions(model);
            
            Template t = templateConfig.getTemplate(template);
            if (t != null)
            {
                try
                {
                    // perform the template processing against supplied data model
                    Environment env = t.createProcessingEnvironment(model, out);
                    // set the locale to ensure dates etc. are appropriate localised
                    env.setLocale(I18NUtil.getLocale());
                    env.process();
                }
                catch (Throwable err)
                {
                    throw new WebScriptException("Failed to process template " + template, err);
                }
            }
            else
            {
                throw new WebScriptException("Cannot find template " + template);
            }
            
            if (logger.isDebugEnabled())
            {
                long endTime = System.nanoTime();
                logger.debug("Time to execute template: " + (endTime - startTime)/1000000f + "ms");
            }
        }
        catch (IOException ioerr)
        {
            throw new WebScriptException("Failed to process template " + template, ioerr);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.TemplateProcessor#processString(java.lang.String, java.lang.Object, java.io.Writer)
     */
    public void processString(String template, Object model, Writer out)
    {
        if (template == null || template.length() == 0)
        {
            throw new IllegalArgumentException("Template is mandatory.");
        }
        if (model == null)
        {
            throw new IllegalArgumentException("Model is mandatory.");
        }
        if (out == null)
        {
            throw new IllegalArgumentException("Output Writer is mandatory.");
        }
        
        long startTime = 0;
        if (logger.isDebugEnabled())
        {
            logger.debug("Executing template: " + template);
            startTime = System.nanoTime();
        }
        
        addProcessorModelExtensions(model);
        
        try
        {
            Template t = new Template("name", new StringReader(template), stringConfig);
            t.process(model, out);
            
            if (logger.isDebugEnabled())
            {
                long endTime = System.nanoTime();
                logger.debug("Time to execute template: " + (endTime - startTime)/1000000f + "ms");
            }
        }
        catch (Throwable err)
        {
            throw new WebScriptException("Failed to process template " + template, err);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.TemplateProcessor#reset()
     */
    public void reset()
    {
        this.init();
        
        if (templateConfig != null)
        {
            templateConfig.clearTemplateCache();
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.TemplateProcessor#hasTemplate(java.lang.String)
     */
    public boolean hasTemplate(String templatePath)
    {
        boolean hasTemplate = false;
        try
        {
            Template template = templateConfig.getTemplate(templatePath);
            hasTemplate = (template != null);
        }
        catch(FileNotFoundException e)
        {
            // NOTE: return false as template is not found
        }
        catch(IOException e)
        {
            throw new WebScriptException("Failed to retrieve template " + templatePath, e);
        }
        return hasTemplate;
    }
    
    /**
     * Initialise FreeMarker Configuration
     */
    protected void initConfig()
    {
        // construct template config
        Configuration config = new Configuration();
        ObjectWrapper objectWrapper = new NonBlockingObjectWrapper();
        config.setObjectWrapper(objectWrapper);
        config.setCacheStorage(new StrongCacheStorage());
        config.setTemplateUpdateDelay(updateDelay);
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLocalizedLookup(false);
        config.setOutputEncoding("UTF-8");
        if (defaultEncoding != null)
        {
            config.setDefaultEncoding(defaultEncoding);
        }
        
        if (getTemplateLoader() != null)
        {
            config.setTemplateLoader(getTemplateLoader());
        }
                
        templateConfig = config;
        
        // construct string config
        stringConfig = new Configuration();
        stringConfig.setObjectWrapper(objectWrapper);
        stringConfig.setCacheStorage(new MruCacheStorage(2, 0));
        stringConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        stringConfig.setOutputEncoding("UTF-8");
        if (defaultEncoding != null)
        {
            stringConfig.setDefaultEncoding(defaultEncoding);
        }
    }
    
    
    private static final Class<?>[] INTROSPECT_CLASSES = {
        TemplateConfigModel.class, GenericConfigElement.class, JSONUtils.class };
    
    /**
     * Implements the FreeMarker ObjectWrapper cache as a ThreadLocal to avoid blocking
     * when interogating or populating the bean cache.
     * 
     * @author dward
     */
    public static class NonBlockingObjectWrapper implements ObjectWrapper
    {
        private final ThreadLocal<ObjectWrapper> threadWrappers = new ThreadLocal<ObjectWrapper>()
        {
            @Override
            protected ObjectWrapper initialValue()
            {
                return new DefaultObjectWrapper();
            }
        };
        
        public NonBlockingObjectWrapper()
        {
            // Force introspection of core classes in advance
            threadWrappers.get();
            for (Class<?> type : INTROSPECT_CLASSES)
            {
                try
                {
                    Introspector.getBeanInfo(type);
                }
                catch (IntrospectionException e)
                {
                    throw new RuntimeException(e);
                }
            }            
        }
        
        /* (non-Javadoc)
         * @see freemarker.template.ObjectWrapper#wrap(java.lang.Object)
         */
        public TemplateModel wrap(Object obj) throws TemplateModelException
        {
            return threadWrappers.get().wrap(obj);
        }  
    }
}