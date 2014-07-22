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

package org.springframework.extensions.config.source;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigDeployment;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.ConfigSource;

/**
 * Base class for ConfigSource implementations, provides support for parsing
 * comma separated sources and iterating around them
 * 
 * @author gavinc
 */
public abstract class BaseConfigSource implements ConfigSource
{
    private static final Log logger = LogFactory.getLog(BaseConfigSource.class);

    private List<String> sourceStrings = new ArrayList<String>();

    /**
     * Default constructor. If this contstructor is used source files
     * must be added using the addSourceString method.
     */
    protected BaseConfigSource() {}
    
    /**
     * @param sourceStrings
     *            a list of implementation-specific sources. The meaning of the
     *            source is particular to the implementation, eg. for a file config
     *            source they would be file names.
     */
    protected BaseConfigSource(List<String> sourceStrings)
    {
        for (String sourceString : sourceStrings)
        {
            addSourceString(sourceString);
        }
    }
    
    /**
     * Conditionally adds the source to the set of source strings if its
     * trimmed length is greater than 0.
     */
    protected void addSourceString(String sourceString)
    {
        if (sourceString == null || sourceString.trim().length() == 0)
        {
           throw new ConfigException("Invalid source value: " + sourceString);
        }
        
        this.sourceStrings.add(sourceString);
    }
    
    /**
     * <p>Iterates through the list of source strings supplied when instantiating the class. Each String
     * is used to resolve an <code>InputStream</code> that is used to instantiate a <code>ConfigDeployment</code>.
     * The result is a <code>List</code> of <code>ConfigDeployment</code> instances which are then returned
     * to the caller</code></p>
     *
     * @see #getInputStream(String)
     */
    public final List<ConfigDeployment> getConfigDeployments()
    {
        // check that we have some kind of source
        int size = this.sourceStrings.size();
        if (size == 0)
        {
            throw new ConfigException("No sources provided: " + sourceStrings);
        }
        
        // build a list of input streams
        List<ConfigDeployment> configDeployments = new ArrayList<ConfigDeployment>(size);
        for (String sourceString : sourceStrings)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Retrieving input stream for source: " + sourceString);
            }
            
            InputStream is = getInputStream(sourceString);
            if (is != null)
            {
            	configDeployments.add(new ConfigDeployment(sourceString, is));
            }
        }
        // done
        return configDeployments;
    }

    /**
     * Retrieves an InputStream to the source represented by the given
     * source location.  The meaning of the source location will depend
     * on the implementation.
     * 
     * @param sourceString the source location
     * @return Returns an InputStream to the named source location
     */
    protected abstract InputStream getInputStream(String sourceString);
}
