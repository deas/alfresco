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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.extensions.surf.exception.WebScriptsPlatformException;
import org.springframework.util.ResourceUtils;

import freemarker.cache.TemplateLoader;


/**
 * ClassPath based Web Script Store
 *
 * @author davidc
 * @author kevinr
 * @author muzquiano
 */
public class ClassPathStore extends AbstractStore implements ApplicationContextAware, Store
{
	// Logger
    private static final Log logger = LogFactory.getLog(ClassPathStore.class);
    
    protected static final String VFSFILE_URL_PREFIX = "vfsfile:";
    protected static final String VFSZIP_URL_PREFIX  = "vfszip:";
    protected static final String VFSJAR_URL_SEPARATOR = ".jar/";
    protected static final String VFSWAR_URL_SEPARATOR = ".war/WEB-INF/";
    protected static final String VFSWAR_CLASSES_URL_SEPARATOR = ".war/WEB-INF/classes/";
    
    protected ClassPathStoreResourceResolver resolver = null;
    protected String[] storeDirs = null;    

    protected ApplicationContext applicationContext = null;
    protected String classPath;
    protected boolean mustExist = false;
    protected boolean exists = false;

    /**
     * Sets whether the class path must exist
     *
     * If it must exist, but it doesn't exist, an exception is thrown
     * on initialisation of the store
     *
     * @param mustExist
     */
    public void setMustExist(boolean mustExist)
    {
        this.mustExist = mustExist;
    }
    
    /**
     * Sets the class path
     *
     * @param classPath  classpath
     */
    public void setClassPath(String classPath)
    {
        String cleanClassPath = (classPath.endsWith("/")) ? classPath.substring(0, classPath.length() -1) : classPath;
        this.classPath = cleanClassPath;
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#init()
     */
    public void init()
    {
        // wrap the application context resource resolver with our own
        this.resolver = new ClassPathStoreResourceResolver(applicationContext);
        
        // check if there are any resources that live under this path
        // this is valid for read-only classpaths (class files + JAR file contents)
        try
        {
            Resource[] resources = resolver.getResources("classpath*:" + classPath + "/*");
            if (resources.length != 0)
            {
                exists = true;
            }
            else
            {
                resources = resolver.getResources("classpath*:" + classPath + "/**/*");
                exists = (resources.length != 0);
            }
            
            if (exists)
            {
                // NOTE: Locate root of web script store
                // NOTE: Following awkward approach is used to mirror lookup of web scripts within store.  This
                //       ensures root paths match.
                try
                {
                    List<String> storeDirList = null;
                    // Process each root resource - there may be several as the classpath* could match
                    // multiple location that each contain the configured path.
                    Resource rootResource = null;
                    resources = resolver.getResources("classpath*:" + classPath + "*");
                    if (resources.length == 0)
                    {
                        // the resolver may not be able to visit root folders using the pattern
                        // so get contents and manually walk up path to resolve a root
                        // this only appears to occur for a JBoss VFS directory outside of the war file location
                        resources = resolver.getResources("classpath*:" + classPath + "/*");
                        if (resources.length != 0)
                        {
                            String externalForm = resources[0].getURL().toExternalForm();
                            externalForm = externalForm.substring(0, externalForm.lastIndexOf('/'));
                            if (externalForm.endsWith(classPath))
                            {
                                storeDirList = new ArrayList<String>(1);
                                storeDirList.add(externalForm);
                            }
                        }
                    }
                    else
                    {
                        storeDirList = new ArrayList<String>(resources.length);
                        for (Resource resource : resources)
                        {
                            String externalForm = resource.getURL().toExternalForm();
                            if (externalForm.endsWith(classPath) || externalForm.endsWith(classPath + "/"))
                            {
                                // we've found the right resource, let's now bind using string constructor
                                // so that Spring 3 will correctly create relative paths
                                String directoryPath = resource.getFile().getAbsolutePath();
                                if (resource.getFile().isDirectory() && !directoryPath.endsWith("/"))
                                {
                                    directoryPath += "/";
                                }
                                if (new FileSystemResource(directoryPath).exists())
                                {
                                    // retrieve file system directory
                                    storeDirList.add(resource.getFile().toURI().toURL().toExternalForm());
                                }
                            }
                        }
                    }
                    if (storeDirList != null && storeDirList.size() != 0)
                    {
                        this.storeDirs = storeDirList.toArray(new String[storeDirList.size()]);
                    }                        
                }
                catch (IOException ioErr)
                {
                    // unable to resolve a storeDir - this is expected for certain protocols such as "vfszip"
                    // it is not critical and those protocols don't require it during path resolution later
                    if (logger.isDebugEnabled())
                        logger.debug("Unable to resolve storeDir for base path " + classPath);
                }
            }
        }
        catch (IOException ioe)
        {
            throw new WebScriptException("Failed to initialise Web Script Store classpath: " + classPath, ioe);
        }
        
        if (!exists && mustExist)
        {
            throw new WebScriptException("Web Script Store classpath:" + classPath + " must exist; it was not found");
        }        
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#exists()
     */
    public boolean exists()
    {
        return exists;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getBasePath()
     */
    public String getBasePath()
    {
        return "classpath:" + classPath;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#isSecure()
     */
    public boolean isSecure()
    {
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.AbstractStore#isReadOnly()
     */
    public boolean isReadOnly()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getAllDocumentPaths()
     */
    public String[] getAllDocumentPaths()
    {
        String[] paths;

        try
        {
            List<String> documentPaths = matchDocumentPaths("/**/*");
            paths = documentPaths.toArray(new String[documentPaths.size()]);
        }
        catch (IOException e)
        {
            // Note: Ignore: no documents found
            paths = new String[0];
        }

        return paths;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDocumentPaths(java.lang.String, boolean, java.lang.String)
     */
    public String[] getDocumentPaths(String path, boolean includeSubPaths, String documentPattern)
        throws IOException
    {
        if ((path == null) || (path.length() == 0))
        {
            path = "/";
        }

        if (!path.startsWith("/"))
        {
            path = "/" + path;
        }

        if (!path.endsWith("/"))
        {
            path = path + "/";
        }

        if ((documentPattern == null) || (documentPattern.length() == 0))
        {
            documentPattern = "*";
        }

        // classpath*:
        final StringBuilder pattern = new StringBuilder(128);
        pattern.append(path)
               .append((includeSubPaths ? "**/" : ""))
               .append(documentPattern);

        List<String> documentPaths = matchDocumentPaths(pattern.toString());
        return documentPaths.toArray(new String[documentPaths.size()]);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDescriptionDocumentPaths()
     */
    public String[] getDescriptionDocumentPaths() throws IOException
    {
        return getDocumentPaths("/", true, DESC_PATH_PATTERN);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getScriptDocumentPaths(org.alfresco.web.scripts.WebScript)
     */
    public String[] getScriptDocumentPaths(WebScript script) throws IOException
    {
        String scriptPaths = script.getDescription().getId() + ".*";
        return getDocumentPaths("/", false, scriptPaths);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#lastModified(java.lang.String)
     */
    public long lastModified(String documentPath)
        throws IOException
    {
        long mod = -1L;
        
        Resource document = this.getDocumentResource(documentPath);
        if (document != null)
        {
            mod = document.getURL().openConnection().getLastModified();
        }
        
        return mod;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#hasDocument(java.lang.String)
     */
    public boolean hasDocument(String documentPath)
    {
        boolean exists = false;

        Resource document = this.getDocumentResource(documentPath);
        if (document != null)
        {
            exists = document.exists();
        }
        
        return exists;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDescriptionDocument(java.lang.String)
     */
    public InputStream getDocument(String documentPath)
        throws IOException
    {
        Resource document = this.getDocumentResource(documentPath);
        
        if (document == null || !document.exists())
        {
            throw new IOException("Document " + documentPath + " does not exist within store " + getBasePath());
        }
        
        if (logger.isTraceEnabled())
            logger.trace("getDocument: documentPath: " + documentPath + " , storePath: " + document.getURL().toExternalForm());
        
        return document.getInputStream();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#createDocument(java.lang.String, java.lang.String)
     */
    public void createDocument(String documentPath, String content) throws IOException
    {
        throw new IOException("ClassPathStore is read-only - cannot create Document");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#updateDocument(java.lang.String, java.lang.String)
     */
    public void updateDocument(String documentPath, String content) throws IOException
    {
        throw new IOException("ClassPathStore is read-only - cannot update Document");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#removeDocument(java.lang.String)
     */
    public boolean removeDocument(String documentPath) throws IOException
    {
        throw new IOException("ClassPathStore is read-only - cannot remove Document");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getTemplateLoader()
     */
    public TemplateLoader getTemplateLoader()
    {
        return new ClassPathTemplateLoader();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getScriptLoader()
     */
    public ScriptLoader getScriptLoader()
    {
        return new ClassPathScriptLoader();
    }
    
    /**
     * Gets a resource for a document path.  The resource will either live in the class path as a class
     * file or as an entry in one of the JAR files on the class path.
     * 
     * @param documentPath
     * 
     * @return Resource or null if not found
     */
    private Resource getDocumentResource(String documentPath)
    {
        String resourcePath = toResourcePath(documentPath);

        // return the resource from the wrapped resolver
        // this does not require the classpath*: prefix
        
        return resolver.getResource(resourcePath);
    }
    
    /**
     * Gets resources that match a given location pattern.  A resource in the returned array can live
     * in the class path as either a class file or as an entry within one of the JAR files in the
     * class path.
     * 
     * @param locationPattern
     * 
     * @return Resource[] of resource that match location pattern - can be empty but never null
     * 
     * @throws IOException
     */
    private Resource[] getDocumentResources(String locationPattern)
        throws IOException
    {
        String resourcePath = toResourcePath(locationPattern);
        
        Resource[] resources = resolver.getResources("classpath*:" + resourcePath);
        ArrayList<Resource> list = new ArrayList<Resource>(resources.length);
        for (Resource resource : resources)
        {
            // only keep documents, not directories
            if (!resource.getURL().toExternalForm().endsWith("/"))
            {
                list.add(resource);
            }
        }
        
        return list.toArray(new Resource[list.size()]);
    }

    /**
     * Converts a document path to a resource path.  
     * 
     * A document path is relative to the base path of the store.  It is what users of the store pass in when
     * they call the methods of the store.
     * 
     * A resource path includes the base path and is descriptive of the resource relative to the root of the
     * resource tree.
     * 
     * @param documentPath
     * 
     * @return resource path
     */
    private String toResourcePath(String documentPath)
    {
        return createPath(classPath, documentPath);
    }
    
    /**
     * Converts a resource path back to a document path.
     * 
     * A document path is relative to the base path of the store.  It is what users of the store pass in when
     * they call the methods of the store.
     * 
     * A resource path includes the base path and is descriptive of the resource relative to the root of the
     * resource tree.
     * 
     * @param resourcePath
     * 
     * @return document path
     */
    private String toDocumentPath(final String resourcePath)
    {
        String documentPath = null;
        
        // check if this is a valid url (either a java URL or a Spring classpath prefix URL)
        try
        {
            final URL url = ResourceUtils.getURL(resourcePath);
            
            String urlString = resourcePath;
            
            // if the URL is a JAR url, trim off the reference to the JAR
            if (isJarURL(url))
            {
                // find the URL to the jar file and split off the prefix portion that references the jar file
                String jarUrlString = extractJarFileURL(url).toExternalForm();
                
                final int x = urlString.indexOf(jarUrlString);
                if (x != -1)
                {
                    urlString = urlString.substring(x + jarUrlString.length());
                    
                    // remove a prefix ! if it is found
                    if (urlString.charAt(0) == '!')
                    {
                        urlString = urlString.substring(1);
                    }
                    
                    // remove a prefix / if it is found
                    if (urlString.charAt(0) == '/')
                    {
                        urlString = urlString.substring(1);
                    }
                }
            }
            
            // if the url string starts with the classpath: prefix, remove it
            if (urlString.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX))
            {
                urlString = urlString.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length());
            }
            
            // if the url string starts with the file: prefix, remove the storeDir path
            // this also remove the base path
            if (urlString.startsWith(ResourceUtils.FILE_URL_PREFIX))
            {
                if (storeDirs == null)
                {
                    throw new WebScriptException("Unable to resolve a file: resource without a storeDir.");
                }
                for (int i=0; i<this.storeDirs.length; i++)
                {
                    if (urlString.startsWith(this.storeDirs[i]))
                    {
                        urlString = urlString.substring(this.storeDirs[i].length());
                        break;
                    }
                }
            }
            // handle the JBoss app-server virtual filesystem prefix
            else if (urlString.startsWith(VFSFILE_URL_PREFIX))
            {
                if (storeDirs == null)
                {
                    throw new WebScriptException("Unable to resolve a vfsfile: resource without a storeDir.");
                }
                for (int i=0; i<this.storeDirs.length; i++)
                {
                    // handle VFS files in expanded WARs
                    if (urlString.startsWith(this.storeDirs[i], 3))
                    {
                        urlString = urlString.substring(this.storeDirs[i].length() + 3); // to account for "vfs" prefix
                        break;
                    }
                    // handle VFS files in other classpath dirs
                    else if (urlString.startsWith(this.storeDirs[i]))
                    {
                        urlString = urlString.substring(this.storeDirs[i].length());
                        break;
                    }
                }
            }
            else
            {
                // now remove the class path store base path
                if (classPath != null && classPath.length() != 0)
                {
                    // the url string should always start with the class path
                    if (urlString.startsWith(classPath))
                    {
                        urlString = urlString.substring(classPath.length());
                    }
                }
            }
            
            // remove extra / at the front if found
            if (urlString.charAt(0) == '/')
            {
                urlString = urlString.substring(1);
            }
            
            // what remains is the document path
            documentPath = urlString;
        }
        catch (FileNotFoundException fnfe)
        {
            if (logger.isWarnEnabled())
                logger.warn("Unable to determine document path for resource: " + resourcePath + " with base path " + classPath, fnfe);
        } 
        catch (MalformedURLException mue) 
        {
            if (logger.isWarnEnabled())
                logger.warn("Unable to determine document path for resource: " + resourcePath + " with base path " + classPath, mue);
        }
        
        return documentPath;
    }

    /**
     * Helper method for creating fully qualified paths
     * 
     * @param path
     * @param relativePath
     * 
     * @return full qualified path
     */
    public static String createPath(String path, String relativePath) 
    {
        if (path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        
        if (relativePath.startsWith("/"))
        {
            relativePath = relativePath.substring(1);
        }
        
        return path + "/" + relativePath;
    }
    
    /**
     * Matches the given path to the full class path that is comprised of class files and resources located
     * inside of JAR files that are on the class path.
     * 
     * @param pattern The pattern to match
     * 
     * @return List<String> of matching paths
     */
    private List<String> matchDocumentPaths(String pattern)
        throws IOException
    {
        Resource[] resources = getDocumentResources(pattern);
        List<String> documentPaths = new ArrayList<String>(resources.length);
        for (Resource resource : resources)
        {
            String documentPath = toDocumentPath(resource.getURL().toExternalForm());
            documentPaths.add(documentPath);
        }
        return documentPaths;
    }    

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "classpath*:" + this.classPath;
    }
    
    /**
     * Determine whether the given URL points to a resource in a jar file,
     * that is, has protocol "jar", "zip", "vfszip", "wsjar" or "code-source".
     * <p>"zip" and "wsjar" and "vfszip" are used by BEA WebLogic Server and IBM WebSphere
     * and JBoss, respectively, but can be treated like jar files. The same applies to
     * "code-source" URLs on Oracle OC4J, provided that the path contains a jar separator.
     * 
     * @param url the URL to check
     * 
     * @return whether the URL has been identified as a JAR URL
     */
    protected static boolean isJarURL(final URL url)
    {
        final String protocol = url.getProtocol();
        return (ResourceUtils.URL_PROTOCOL_JAR.equals(protocol) ||
                ResourceUtils.URL_PROTOCOL_VFSZIP.equals(protocol) ||
                ResourceUtils.URL_PROTOCOL_ZIP.equals(protocol) ||
                ResourceUtils.URL_PROTOCOL_VFS.equals(protocol) ||
                ResourceUtils.URL_PROTOCOL_WSJAR.equals(protocol) ||
                (ResourceUtils.URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().contains(ResourceUtils.JAR_URL_SEPARATOR)));
    }

    /**
     * Extract the URL for the actual jar file from the given URL
     * (which may point to a resource in a jar file or to a jar file itself).
     * 
     * @param jarUrl the original URL
     * 
     * @return the URL for the actual jar file
     * 
     * @throws MalformedURLException if no valid jar file URL could be extracted
     */
    protected static URL extractJarFileURL(final URL jarUrl) throws MalformedURLException
    {
        String urlFile = jarUrl.getFile();
        int separatorIndex = urlFile.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
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
        if (separatorIndex == -1)
        {
        	// support for JBoss VFS filesystem WAR file references to /WEB-INF/classes
        	// in this case, the URL points to a WAR file concatenated with the internal path
        	// i.e. vfszip:/path_to_jboss_server/deploy/test.war/WEB-INF/classes/surf/webscripts/test/alfwebtest01.get.desc.xml
        	urlFile = jarUrl.toString();
        	separatorIndex = urlFile.indexOf(VFSWAR_CLASSES_URL_SEPARATOR);
        	if (separatorIndex != -1)
        	{
        		// offset index to account for .war/WEB-INF/classes suffix
        		separatorIndex += VFSWAR_CLASSES_URL_SEPARATOR.length() - 1;
        	}
        	else
            {
                separatorIndex = urlFile.indexOf(VFSWAR_URL_SEPARATOR);
                if (separatorIndex != -1)
                {
                    // offset index to account for .war/WEB-INF suffix
                    separatorIndex += VFSWAR_URL_SEPARATOR.length() - 1;
                }
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
                // Probably no protocol in original jar URL, like "jar:C:/mypath/myjar.jar".
                // This usually indicates that the jar file resides in the file system.
                // Make the best guess based on the original supplied protocol and jar file path
                return new URL(jarUrl.getProtocol() + ':' + jarFile);
            }
        }
        else
        {
            return jarUrl;
        }
    }


    /**
     * Class path based script loader
     *
     * @author davidc
     */
    private class ClassPathScriptLoader implements ScriptLoader
    {
        /** Cache of path to ScriptContent locations.
            Used to ensure that document resources are not looked up more than once.
            This cache will be destroyed when the ClassPathScriptLoader is recreated
            on a WebScripts refresh call. */
        private Map<String, ScriptContent> locationCache = new ConcurrentHashMap<String, ScriptContent>(256);
        private ClassPathScriptLocation CACHE_NULL_SENTINEL = new ClassPathScriptLocation(null, null, null);
        
        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.ScriptLoader#getScriptLocation(java.lang.String)
         */
        public ScriptContent getScript(String documentPath)
        {
            ScriptContent location = locationCache.get(documentPath);
            
            if (location == null)
            {
                Resource script = getDocumentResource(documentPath);
                if (script != null && script.exists())
                {
                    location = new ClassPathScriptLocation(classPath, documentPath, script);
                }
                else
                {
                    location = CACHE_NULL_SENTINEL;
                }
                
                // Update the cache.
                // We save Sentinel "null" values in the cache as the loader is often called
                // and returns a null result for most classpath locations.
                locationCache.put(documentPath, location);
            }
            
            return location != CACHE_NULL_SENTINEL ? location : null;
        }
    }

    /**
     * Class path script location
     *
     * @author davidc
     */
    private static class ClassPathScriptLocation implements ScriptContent
    {
        private String basePath;
        private String path;
        private Resource location;

        /**
         * Construct
         *
         * @param basePath
         * @param path
         * @param location
         */
        public ClassPathScriptLocation(String basePath, String path, Resource location)
        {
            this.basePath = basePath;
            this.path = path;
            this.location = location;
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.repository.ScriptLocation#getInputStream()
         */
        public InputStream getInputStream()
        {
            try
            {
                return this.location.getInputStream();
            }
            catch (IOException e)
            {
                throw new WebScriptException("Unable to retrieve input stream for script " + getPathDescription());
            }
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.repository.ScriptLocation#getReader()
         */
        public Reader getReader()
        {
            try
            {
                return new InputStreamReader(getInputStream(), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                throw new WebScriptsPlatformException("Unsupported Encoding", e);
            }
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.ScriptContent#getPath()
         */
        public String getPath()
        {
            String path = "<unknown path>";
            try
            {
                path = this.location.getURL().toExternalForm();
            }
            catch(IOException ioe)
            {
            }
            return path;
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.ScriptContent#getPathDescription()
         */
        public String getPathDescription()
        {
            return "classpath*:" + ClassPathStore.createPath(basePath, path);
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.ScriptContent#isCachable()
         */
        public boolean isCachable()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.ScriptContent#isSecure()
         */
        public boolean isSecure()
        {
            return true;
        }

        @Override
        public String toString()
        {
            return getPathDescription();
        }
    }
    
    /**
     * Class Path Store implementation of a Template Loader
     * 
     * Retrieves templates either from classes in the class path or classes inside of JAR files
     * within the class path
     * 
     * @author muzquiano
     */
    protected class ClassPathTemplateLoader implements TemplateLoader
    {
        /**
         * @see freemarker.cache.TemplateLoader#closeTemplateSource(java.lang.Object)
         */
        public void closeTemplateSource(Object templateSource)
                throws IOException
        {
        }

        /**
         * @see freemarker.cache.TemplateLoader#findTemplateSource(java.lang.String)
         */
        public Object findTemplateSource(String name) throws IOException
        {
            ClassPathTemplateSource source = null;
            if (hasDocument(name))
            {
                source = new ClassPathTemplateSource(name);
            }
            return source;
        }

        /**
         * @see freemarker.cache.TemplateLoader#getLastModified(java.lang.Object)
         */
        public long getLastModified(Object templateSource)
        {
            return ((ClassPathTemplateSource) templateSource).lastModified();
        }

        /**
         * @see freemarker.cache.TemplateLoader#getReader(java.lang.Object,
         *      java.lang.String)
         */
        public Reader getReader(Object templateSource, String encoding)
                throws IOException
        {
            return ((ClassPathTemplateSource) templateSource).getReader(encoding);
        }
    }

    /**
     * Template Source - loads from a Class Path Store
     * 
     * @author muzquiano
     * @author kevinr
     */
    protected class ClassPathTemplateSource
    {
        private String templatePath;

        protected ClassPathTemplateSource(String path)
        {
            this.templatePath = path;
        }

        protected long lastModified()
        {
            try
            {
                return ClassPathStore.this.lastModified(templatePath);
            }
            catch (IOException e)
            {
                return -1;
            }
        }

        protected Reader getReader(String encoding) throws IOException
        {
            Resource resource = getDocumentResource(templatePath);
            // respect the supplied character encoding - essential to build the correct Reader impl
            return encoding != null ? new InputStreamReader(resource.getInputStream(), encoding) : new InputStreamReader(resource.getInputStream());
        }
    }
}
