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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.source.BaseConfigSource;
import org.springframework.extensions.config.source.FileConfigSource;
import org.springframework.web.context.ServletContextAware;

/**
 * ConfigSource implementation that gets its data via files in a web
 * application.
 * 
 * TODO: Also deal with the source being specified as an init param i.e.
 * param:config.files
 * 
 * @author gavinc
 */
public class WebAppConfigSource extends BaseConfigSource implements ServletContextAware
{
    private static Log logger = LogFactory.getLog(FileConfigSource.class);
    private ServletContext servletCtx;

    /**
     * Constructs a webapp configuration source that uses a single file
     * 
     * @param filename the name of the file from which to get config
     * 
     * @see WebAppConfigSource#WebAppConfigSource(java.util.List)
     */
    public WebAppConfigSource(String filename)
    {
        this(Collections.singletonList(filename));
    }
    
    /**
     * @param sourceStrings List of paths to files in a web application
     */
    public WebAppConfigSource(List<String> sourceStrings)
    {
        super(sourceStrings);
    }

    /**
     * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
     */
    public void setServletContext(ServletContext servletContext)
    {
        this.servletCtx = servletContext;
    }

    /**
     * @see org.springframework.extensions.config.source.BaseConfigSource#getInputStream(java.lang.String)
     */
    public InputStream getInputStream(String sourceString)
    {
        InputStream is = null;

        try
        {
            String fullPath = this.servletCtx.getRealPath(sourceString);
            is = new BufferedInputStream(new FileInputStream(fullPath));
        } 
        catch (IOException ioe)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Failed to obtain input stream to file: " + sourceString, ioe);
            }
        }

        return is;
    }
}
