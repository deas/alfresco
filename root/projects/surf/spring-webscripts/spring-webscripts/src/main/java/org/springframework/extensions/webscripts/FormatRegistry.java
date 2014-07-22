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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Maintains a registry of mimetypes (indexed by format and user agent)
 * 
 * @author davidc
 */
public class FormatRegistry
{
    // Logger
    private static final Log logger = LogFactory.getLog(FormatRegistry.class);

    private Map<String, String> formats;
    private Map<String, String> mimetypes;
    private Map<String, Map<String, String>> agentFormats;
    private Map<String, Map<String, String>> agentMimetypes;
    private Map<String, FormatReader<Object>> readers;
    private FormatReader<Object> defaultReader;
    private Map<String, FormatWriter<Object>> writers;


    /**
     * Construct
     */
    public FormatRegistry()
    {
        formats = new HashMap<String, String>();
        mimetypes = new HashMap<String, String>();
        agentFormats = new HashMap<String, Map<String, String>>();
        agentMimetypes = new HashMap<String, Map<String, String>>();
        readers = new HashMap<String, FormatReader<Object>>();
        writers = new HashMap<String, FormatWriter<Object>>();
    }

    /**
     * Set default Reader
     * 
     * @param defaultReader
     */
    public void setDefaultReader(FormatReader<Object> defaultReader)
    {
    	this.defaultReader = defaultReader;
    }
    
    /**
     * Add a reader
     * 
     * @param mimetype
     * @param reader
     */
    public void addReader(FormatReader<Object> reader)
    {
        String mimetype = reader.getSourceMimetype();
        readers.put(reader.getSourceMimetype(), reader);
        if (logger.isDebugEnabled())
            logger.debug("Registered format reader: converts mimetype '" + mimetype + "' to class '" + reader.getDestinationClass().getSimpleName() + "'");
    }

    /**
     * Add a writer
     * 
     * @param mimetype
     * @param writer
     */
    public void addWriter(FormatWriter<Object> writer)
    {
        String mimetype = writer.getDestinationMimetype();
        Class<? extends Object> clazz = writer.getSourceClass();
        writers.put(clazz.getName() + "||" + mimetype, writer);
        if (logger.isDebugEnabled())
            logger.debug("Registered format writer: converts class '" + clazz.getSimpleName() + "' to mimetype '" + mimetype + "'");
    }
    
    /**
     * Add formats
     * 
     * @param agent
     * @param formatsToAdd
     */
    public void addFormats(String agent, Map<String, String> formatsToAdd)
    {
        Map<String, String> formatsForAgent = formats; 
        if (agent != null)
        {
            formatsForAgent = agentFormats.get(agent);
            if (formatsForAgent == null)
            {
                formatsForAgent = new HashMap<String, String>();
                agentFormats.put(agent, formatsForAgent);
            }
        }
        
        for (Map.Entry<String, String> entry : formatsToAdd.entrySet())
        {
            if (logger.isWarnEnabled())
            {
                String val = formatsForAgent.get(entry.getKey());
                if (val != null)
                {
                    logger.warn("Replacing mimetype '" + val + "' with '" + entry.getValue() + "' for format '" + entry.getKey() + "' (agent: " + agent + ")");
                }
            }
            
            formatsForAgent.put(entry.getKey(), entry.getValue());
            
            if (logger.isDebugEnabled())
                logger.debug("Registered format '" + entry.getKey() + "' for mimetype '" + entry.getValue() + "' (agent: " + agent + ")");
        }
    }

    /**
     * Add mimetypes
     * 
     * @param agent
     * @param mimetypesToAdd
     */
    public void addMimetypes(String agent, Map<String, String> mimetypesToAdd)
    {
        Map<String, String> mimetypesForAgent = mimetypes; 
        if (agent != null)
        {
            mimetypesForAgent = agentMimetypes.get(agent);
            if (mimetypesForAgent == null)
            {
                mimetypesForAgent = new HashMap<String, String>();
                agentMimetypes.put(agent, mimetypesForAgent);
            }
        }
        
        for (Map.Entry<String, String> entry : mimetypesToAdd.entrySet())
        {
            if (logger.isWarnEnabled())
            {
                String val = mimetypesForAgent.get(entry.getKey());
                if (val != null)
                {
                    logger.warn("Replacing format '" + val + "' with '" + entry.getValue() + "' for mimetype '" + entry.getKey() + "' (agent: " + agent + ")");
                }
            }
            
            mimetypesForAgent.put(entry.getKey(), entry.getValue());
            
            if (logger.isDebugEnabled())
                logger.debug("Registered mimetype '" + entry.getKey() + "' for format '" + entry.getValue() + "' (agent: " + agent + ")");
        }
    }

    /**
     * Gets the mimetype for the specified user agent and format
     * 
     * @param agent
     * @param format
     * @return  mimetype (or null, if one is not registered)
     */
    public String getMimeType(String agent, String format)
    {
        String mimetype = null;
        
        if (agent != null)
        {
            Map<String, String> formatsForAgent = agentFormats.get(agent);
            if (formatsForAgent != null)
            {
                mimetype = formatsForAgent.get(format);
            }
        }
        
        if (mimetype == null)
        {
            mimetype = formats.get(format);
        }

        return mimetype;
    }
    
    /**
     * Gets the format for the specified user agent and mimetype
     * 
     * @param agent
     * @param mimetype
     * @return  format (or null, if one is not registered)
     */
    public String getFormat(String agent, String mimetype)
    {
        String format = null;
        
        if (agent != null)
        {
            Map<String, String> mimetypesForAgent = agentMimetypes.get(agent);
            if (mimetypesForAgent != null)
            {
                format = mimetypesForAgent.get(mimetype);
            }
        }
        
        if (format == null)
        {
            format = mimetypes.get(mimetype);
        }

        return format;
    }

    /**
     * Gets a Format Reader
     * 
     * @param mimetype
     * @return  reader
     */
    public FormatReader<Object> getReader(String mimetype)
    {
    	if (mimetype == null)
    	{
    		return defaultReader;
    	}
    	
        // TODO: lookup by sorted mimetype list (most specific -> least specific)
    	String generalizedMimetype = mimetype;
    	while (generalizedMimetype != null)
    	{
            FormatReader<Object> reader = readers.get(generalizedMimetype); 
            if (reader != null)
            {
                return reader;
            }
            generalizedMimetype = generalizeMimetype(generalizedMimetype);
    	}
    	return null;
    }

    /**
     * Gets a Format Writer
     * 
     * @param object
     * @param mimetype
     * 
     * @return  writer
     */
    public FormatWriter<Object> getWriter(Object object, String mimetype)
    {
        // TODO: lookup by sorted mimetype list (most specific -> least specific)
        String generalizedMimetype = mimetype; 
        while (generalizedMimetype != null)
        {
            FormatWriter<Object> writer = writers.get(object.getClass().getName() + "||" + generalizedMimetype);
            if (writer != null)
            {
                return writer;
            }
            generalizedMimetype = generalizeMimetype(generalizedMimetype);
        }
        return null;
    }

    /**
     * Generalize Mimetype
     * 
     * @param mimetype
     * @return  generalized mimetype (null, if no generalization can be made)
     */
    public String generalizeMimetype(String mimetype)
    {
        String generalizedMimetype = null;
        if (mimetype != null)
        {
            int params = mimetype.lastIndexOf(";");
            if (params != -1)
            {
                generalizedMimetype = mimetype.substring(0, params);
            }
        }
        return generalizedMimetype;
    }
    
}
