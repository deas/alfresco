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

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.extensions.config.ConfigException;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * ConfigSource that looks for a prefix to determine where to look for the config.</br>
 * Valid prefixes are:
 * <ul>
 *   <li><b>file:</b> the location provided is a path to a physical file</li>
 *   <li><b>classpath:</b> the location provided is a resource on the classpath</li>
 *   <li><b>http:</b> the location provided is a HTTP address</li>
 *   <li><b>jar:</b> the location provided is within a JAR file. The location can either
 *   be a single JAR file location or a wildcard in which case all JAR files on the classpath
 *   will be searched for the given entry. NOTE: Currently only searching in the META-INF
 *   folder is supported.</li>
 * </ul>
 * The default, if none of the above is detected, is <b>classpath</b>.  An example of
 * a URL is <code>file:/home/root/settings/config.xml</code>.
 * 
 * @author Derek Hulley
 * @author gavinc
 */
public class UrlConfigSource extends BaseConfigSource implements ServletContextAware, ApplicationContextAware, InitializingBean
{
    public static final String PREFIX_JAR = "jar:";
    public static final String PREFIX_FILE = "file:";
    public static final String PREFIX_HTTP = "http:";
    public static final String PREFIX_CLASSPATH = "classpath:";
    public static final String PREFIX_CLASSPATH_ALL = "classpath*:";
    public static final String PREFIX_WEBAPP = "webapp:";
    
    private static final String WILDCARD = "*";
    private static final String META_INF = "META-INF";
    private static final String VFSJAR_URL_SEPARATOR = ".jar/";
    
    private static final Log logger = LogFactory.getLog(UrlConfigSource.class);
    
    private ServletContext servletContext;
    private ApplicationContext applicationContext;
    private List<String> sourceLocations = new ArrayList<String>();
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
     */
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    public void afterPropertiesSet() throws Exception
    {
        for (String location : sourceLocations)
        {
            processSourceString(location);
        }
    }

    /**
     * Constructs a config location that figures out where to look for the config
     * 
     * @param sourceLocation The location from which to get config
     * 
     * @see ClassPathConfigSource#ClassPathConfigSource(java.util.List)
     */
    public UrlConfigSource(String sourceLocation)
    {
        this(sourceLocation, false);
    }

    /**
     * Constructs a config location that figures out where to look for the
     * config
     * 
     * @param sourceLocation
     *            The location from which to get config
     * @param init
     *            indicates whether sourceLocation should be processed directly
     *            in constructor
     * 
     * @see ClassPathConfigSource#ClassPathConfigSource(java.util.List)
     */
    public UrlConfigSource(String sourceLocation, boolean init)
    {
        if (init)
        {
            processSourceString(sourceLocation);
        }
        else
        {
            this.sourceLocations.add(sourceLocation);
        }
    }

    /**
     * Constructs a config location that figures out where to look for the config
     * 
     * @param sourceLocations List of locations from which to get the config
     */
    public UrlConfigSource(List<String> sourceLocations)
    {
        this(sourceLocations, false);
    }

    /**
     * Constructs a config location that figures out where to look for the
     * config
     * 
     * @param sourceLocations
     *            List of locations from which to get the config
     * @param init
     *            indicates whether sourceLocations should be processed directly
     *            in constructor
     */
    public UrlConfigSource(List<String> sourceLocations, boolean init)
    {
        if (init)
        {
            for (String location : sourceLocations)
            {
                processSourceString(location);
            }
        }
        else
        {
            this.sourceLocations.addAll(sourceLocations);
        }
    }

    /**
     * <p>A <code>BaseConfigSource</code> maintains a list of source strings which for a <code>UrlConfigSource</code> define
     * locations from which a configuration file can be retrieved. Each location should be prefixed by an identifier indicating
     * where to find the configuration file (e.g. on the classpath, as a physical file, etc). If a prefix is not provided
     * the the location is assumed to be on the classpath.</p>
     * <p>This method determines where to find the specified configuration file by matching the prefix to an implementation
     * of the <code>ConfigSource</code> interface and creating a new instance of that class using the location. The <code>ConfigSource</code>
     * interface requires that a <code>getInputStream</code> method is implemented and this is then invoked and the <code>InputStream</code>
     * returned by the <code>ConfigSource</code> is then returned by this method</p>
     *
     * @param sourceUrl A location from which to obtain an <code>InputStream</code> to a configuration file.
     * @return An <code>InputStream</code> to a configuration file.
     */
    public InputStream getInputStream(String sourceUrl)
    {
        // input stream
        InputStream inputStream = null;
        
        // determine the config source
        BaseConfigSource configSource = null;
        String sourceString = null;
        if (sourceUrl.startsWith(PREFIX_FILE))
        {
            sourceString = sourceUrl.substring(5);
            configSource = new FileConfigSource(sourceString);
        }
        else if (sourceUrl.startsWith(PREFIX_HTTP))
        {
            sourceString = sourceUrl;
            configSource = new HTTPConfigSource(sourceString);
        }
        else if (sourceUrl.startsWith(PREFIX_CLASSPATH))
        {
            sourceString = sourceUrl.substring(10);
            configSource = new ClassPathConfigSource(sourceString);
        }
        else if (sourceUrl.startsWith(PREFIX_JAR))
        {
            sourceString = sourceUrl;
            configSource = new JarConfigSource(sourceString);
        }
        else if (sourceUrl.startsWith(ResourceUtils.URL_PROTOCOL_VFS))
        {
            sourceString = sourceUrl;

            InputStream is = null;
            try
            {
                URL resourceUrl = new URL(sourceString);
                URLConnection con = (URLConnection) resourceUrl.openConnection();
                is = con.getInputStream();
            }
            catch (MalformedURLException e)
            {
                if (logger.isDebugEnabled())
                    logger.debug("The malformed URL has occurred: " + sourceString, e);
                e.printStackTrace();
            }
            catch (IOException ioe)
            {
                if (logger.isDebugEnabled())
                    logger.debug("Failed to get input stream from open connection: " + sourceString, ioe);
            }
            
            // NOTE! special case for JBOSS VFS protocol! @see MNT-10050
            return is;
        }
        else if (sourceUrl.startsWith(PREFIX_WEBAPP))
        {
            if (servletContext != null)
            {
                sourceString = sourceUrl.substring(PREFIX_WEBAPP.length());
                if (!sourceString.startsWith("/"))
                {
                    sourceString = "/" + sourceString;
                }
                
                try
                {
                    if (servletContext.getResource(sourceString) != null)
                    {
                        sourceString = servletContext.getRealPath(sourceString);
                        if (sourceString != null)
                        {
                            configSource = new FileConfigSource(sourceString);
                        }
                    }
                }
                catch (Exception ex)
                {
                    if (logger.isInfoEnabled())
                        logger.info("Unable to locate web application resource: " + sourceString);
                }
            }
        }
        else if (sourceUrl.indexOf(':') > -1)
        {
            throw new ConfigException("Config source cannot be determined: " + sourceUrl);
        }
        else
        {
            sourceString = sourceUrl;
            configSource = new ClassPathConfigSource(sourceString);
        }
        
        // retrieve input stream if we've identified a source
        if (sourceString != null && configSource != null)
        {
            inputStream = configSource.getInputStream(sourceString);
        }
        
        return inputStream;
    }
    
    /**
     * Processes the given source string and adds the resulting config
     * source files to the list to be parsed.
     * <p>
     * If the sourceString contains a wildcard the appropriate resolution
     * processing is performed to obtain a list of physical locations.
     * </p> 
     * 
     * @param sourceString
     */
    protected void processSourceString(String sourceString)
    {
        // support for wildcards on JAR file sources
        if (sourceString != null && sourceString.startsWith(PREFIX_JAR) && 
            sourceString.indexOf(WILDCARD + JarConfigSource.JAR_PATH_SEPARATOR) != -1)
        {
            processWildcardJarSource(sourceString);
        }
        else
        {
            super.addSourceString(sourceString);
        }
    }
    
    /**
     * Processes the given JAR file pattern source. The classpath
     * will be searched for JAR files that contain files that match
     * the given pattern.
     * 
     * NOTE: Currently only files within the META-INF folder are supported
     * i.e. patterns that look like "jar:*!/META-INF/[filename]"
     * 
     * @param sourcePattern The wildcard pattern for files to find within JARs
     */
    protected void processWildcardJarSource(String sourcePattern)
    {
        String file = sourcePattern.substring(7);
       
        if (file.startsWith(META_INF) == false)
        {
            throw new UnsupportedOperationException(
                "Only JAR file wildcard searches within the META-INF folder are currently supported");
        }
        
        try
        {
            if (applicationContext == null)
            {
                // get a list of all the JAR files that have the META-INF folder
                Enumeration<URL> urls = this.getClass().getClassLoader().getResources(META_INF);
                while (urls.hasMoreElements())
                {
                    URL url = urls.nextElement();
                    // only add the item if is a reference to a JAR file
                    if (url.getProtocol().equals(JarConfigSource.JAR_PROTOCOL))
                    {
                        URLConnection conn = url.openConnection();
                        if (conn instanceof JarURLConnection)
                        {
                            // open the jar file and see if it contains what we're looking for
                            JarURLConnection jarConn = (JarURLConnection)conn;
                            JarFile jar = ((JarURLConnection)conn).getJarFile();
                            ZipEntry entry = jar.getEntry(file);
                            if (entry != null)
                            {
                                if (logger.isInfoEnabled())
                                    logger.info("Found " + file + " in " + jarConn.getJarFileURL());
                           
                                String sourceString = JarConfigSource.JAR_PROTOCOL + ":" +
                                        jarConn.getJarFileURL().toExternalForm() + 
                                        JarConfigSource.JAR_PATH_SEPARATOR + file;
                           
                                super.addSourceString(sourceString);
                            }
                            else if (logger.isDebugEnabled())
                            {
                                logger.debug("Did not find " + file + " in " + jarConn.getJarFileURL());
                            }
                        }
                    }
                }
            }
            else
            {
                Resource[] resources = applicationContext.getResources(PREFIX_CLASSPATH_ALL + file);
                
                for (Resource resource : resources)
                {
                    URL resourceUrl = resource.getURL();
                    if (ResourceUtils.isJarURL(resourceUrl) || ResourceUtils.URL_PROTOCOL_VFSZIP.equals(resourceUrl.getProtocol()))
                    {
                        URL jarURL = extractJarFileURL(resourceUrl);
                        
                        String sourceString = JarConfigSource.JAR_PROTOCOL + ":" +
                                jarURL.toString() + JarConfigSource.JAR_PATH_SEPARATOR + file;
                        
                        super.addSourceString(sourceString);
                    }
                    else if (ResourceUtils.URL_PROTOCOL_VFS.equals(resourceUrl.getProtocol()))
                    {   
                        super.addSourceString(resourceUrl.toString());
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            if (logger.isDebugEnabled())
                logger.debug("Failed to process JAR file wildcard: " + sourcePattern, ioe);
        }
    }

    private URL extractJarFileURL(final URL jarUrl)
    {
        String urlFile = jarUrl.getFile();
        int separatorIndex = urlFile.indexOf(JarConfigSource.JAR_PATH_SEPARATOR);
        if (separatorIndex == -1)
        {
            // support for JBoss VFS filesystem JAR files URLs
            urlFile = jarUrl.toString();
            separatorIndex = urlFile.indexOf(VFSJAR_URL_SEPARATOR);
            if (separatorIndex != -1)
            {
                // offset index to account for .jar suffix
                separatorIndex += 4;
            }
        }
        if (separatorIndex != -1)
        {
            String jarFile = urlFile.substring(0, separatorIndex);
            try
            {
                return new URL(jarFile);
            }
            catch (MalformedURLException ex)
            {
                return jarUrl;
            }
        }
        else
        {
            return jarUrl;
        }
    }
}
