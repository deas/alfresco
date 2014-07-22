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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ClassUtils;

/**
 * Class path resource resolver that traverses the entire class path.  This includes
 * both class files as well as the contents of JAR files.
 * 
 * @author muzquiano
 */
public class ClassPathStoreResourceResolver extends PathMatchingResourcePatternResolver
{
    private static Log logger = LogFactory.getLog(ClassPathStoreResourceResolver.class);
    
    
    /**
     * Constructor
     * 
     * @param applicationContext        ApplicationContext
     */
    public ClassPathStoreResourceResolver(ApplicationContext applicationContext)
    {
        super(applicationContext);
    }
    
    /**
     * Retrieves a resource for a given location
     * 
     * This method performs a robust lookup, first checking the servlet context for the resource.  This
     * will resolve simple class files and other simple class path elements.  These are physical file
     * elements accessible by the resource loader.  If nothing is found, the JAR files in the
     * servlet context are then consulted.  This lookup may result in a URL to an element inside of a
     * JAR file.
     * 
     * If a resource cannot ultimately be found, null is returned.
     * 
     * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResource(java.lang.String)
     */
    public Resource getResource(String location) 
    {
        Resource resource = null;
        
        // look for classes
        Resource r = super.getResource(location);
        if (r != null && r.exists())
        {
            resource = r;
        }
        else
        {
            // see if this resource is in a JAR file
            URL resourceUrl = ClassUtils.getDefaultClassLoader().getResource(location);
            if (resourceUrl != null)
            {
                r = new UrlInputStreamResource(resourceUrl);
                if (r.exists())
                {
                    resource = r;
                }
            }
        }
        
        return resource;
    }
    
    /**
     * Looks up resources by matching against a given location pattern
     * 
     * Performs a robust match of the given location pattern against both servlet context class path resources
     * as well as resources contained within JAR files.
     * 
     * The incoming location pattern is expected to be prefixed with classpath*:
     * 
     * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResources(java.lang.String)
     */
    public Resource[] getResources(String locationPattern) throws IOException 
    {
        // look up any resources that may be on the class path directly
        return super.getResources(locationPattern);
    }
    
    
    /**
     * @author Dmitry Velichkevich
     */
    public static class UrlInputStreamResource extends UrlResource
    {
        public UrlInputStreamResource(String path) throws MalformedURLException
        {
            super(path);
        }

        public UrlInputStreamResource(URI uri) throws MalformedURLException
        {
            super(uri);
        }

        public UrlInputStreamResource(URL url)
        {
            super(url);
        }

        @Override
        public boolean exists()
        {
            if (!super.exists())
            {
                // Check whether InputStream of the resource really cannot be retrieved - as the UrlResource
                // can return "false" from exists() if the resource is inside a JAR or JBoss VFS system file.
                InputStream stream = null;
                try
                {
                    stream = super.getInputStream();
                    return stream != null;
                }
                catch (IOException e)
                {
                    // We can't get the InputStream of current resource due to incorrect URL or other issue
                    return false;
                }
                finally
                {
                    if (stream != null)
                    {
                        try
                        {
                            stream.close();
                        }
                        catch (IOException e)
                        {
                            // Ignoring this exception
                        }
                    }
                }
            }
            else
            {
                return true;
            }
        }
    }
}
