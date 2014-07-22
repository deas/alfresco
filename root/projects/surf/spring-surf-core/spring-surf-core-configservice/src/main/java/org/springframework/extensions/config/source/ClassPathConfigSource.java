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
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ConfigSource implementation that gets its data via the class path.
 * 
 * @author gavinc
 */
public class ClassPathConfigSource extends BaseConfigSource
{
    private static Log logger = LogFactory.getLog(ClassPathConfigSource.class);
    
    /**
     * Constructs a class path configuration source that uses a single file
     * 
     * @param classpath the classpath from which to get config
     * 
     * @see ClassPathConfigSource#ClassPathConfigSource(java.util.List)
     */
    public ClassPathConfigSource(String classpath)
    {
        this(Collections.singletonList(classpath));
    }

    /**
     * Constructs an ClassPathConfigSource using the list of classpath elements
     * 
     * @param sourceStrings List of classpath resources to get config from
     */
    public ClassPathConfigSource(List<String> sourceStrings)
    {
        super(sourceStrings);
    }

    /**
     * Retrieves an input stream for the given class path source
     * 
     * @param sourceString The class path resource to search for
     * @return The input stream
     */
    public InputStream getInputStream(String sourceString)
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(sourceString);

        if (logger.isDebugEnabled())
        {
            if (is == null)
            {
                logger.debug("Failed to obtain input stream to classpath: " + sourceString);
            }
            else
            {
                URL url = this.getClass().getClassLoader().getResource(sourceString);
                String location = url.toExternalForm();
                logger.debug("Loaded '" + sourceString + "' from: " + location);
            }
        }

        return is;
    }
}
