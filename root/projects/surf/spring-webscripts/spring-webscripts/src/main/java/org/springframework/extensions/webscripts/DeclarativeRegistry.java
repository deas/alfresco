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
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.Description.Lifecycle;


/**
 * Registry of declarative (scripted/template driven) Web Scripts
 * 
 * @author davidc
 */
public class DeclarativeRegistry
    implements Registry, ApplicationContextAware, InitializingBean
{
    /** Filename extension for webscript descriptor paths */
    public static final String WEBSCRIPT_DESC_XML = ".desc.xml";

    // Logger
    private static final Log logger = LogFactory.getLog(DeclarativeRegistry.class);

    // application context
    private ApplicationContext applicationContext;
    
    // default web script implementation bean name
    private String defaultWebScript;

    // web script search path
    private SearchPath searchPath;
    
    // web script container
    private Container container;
    
    // map of web scripts by id
    // NOTE: The map is sorted by id (ascending order)
    private Map<String, WebScript> webscriptsById = new TreeMap<String, WebScript>();
    
    // map of web script packages by path
    private Map<String, PathImpl> packageByPath = new TreeMap<String, PathImpl>();

    // map of web script uris by path
    private Map<String, PathImpl> uriByPath = new TreeMap<String, PathImpl>();

    // map of web script families by path
    private Map<String, PathImpl> familyByPath = new TreeMap<String, PathImpl>();
    
    // map of web script families by lifecycle
    private Map<String, PathImpl> lifecycleByPath = new TreeMap<String, PathImpl>();
    
    // uri index for mapping a URI to a Web Script
    private UriIndex uriIndex;
    
    // map of invalid web script definitions (error by path)
    private Map<String, String> failedWebScriptsByPath = new TreeMap<String, String>();

    // map of package description documents by path
    private Map<String, PackageDescriptionDocument> packageDocumentByPath = new TreeMap<String, PackageDescriptionDocument>();

    // map of schema description documents by id
    private Map<String, SchemaDescriptionDocument> schemaDocumentById = new TreeMap<String, SchemaDescriptionDocument>();

    // map of invalid package description documents (error by path)
    private Map<String, String> failedPackageDescriptionsByPath = new TreeMap<String, String>();

    // map of invalid schema description documents (error by path)
    private Map<String, String> failedSchemaDescriptionsByPath = new TreeMap<String, String>();
    
    // cache of URIs to WebScript match objects
    private Map<String, Match> uriIndexCache = new ConcurrentHashMap<String, Match>(1024);
    private static final Match SENTINEL_MATCH = new Match(null, Collections.<String, String>emptyMap(), null);
    
    // lock around the WebScripts index during a reset() operation
    private final ReadWriteLock indexResetLock = new ReentrantReadWriteLock();
    
    //
    // Initialisation
    // 
    
    /**
     * @param defaultWebScript
     */
    public void setDefaultWebScript(String defaultWebScript)
    {
        this.defaultWebScript = defaultWebScript;
    }

    /**
     * @param uriIndex
     */
    public void setUriIndex(UriIndex uriIndex)
    {
        this.uriIndex = uriIndex;
    }
    
    /**
     * @param searchPath
     */
    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
    }

    /**
     * @param container
     */
    public void setContainer(Container container)
    {
        this.container = container;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * Gets the Application Context
     * Changed from protected to public is for Dev Tools FreeMarker Editor plugin
     * 
     * @return  application context
     */
    public ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
        throws Exception
    {
        if (defaultWebScript == null || defaultWebScript.length() == 0 || !applicationContext.containsBean(defaultWebScript))
        {
            throw new WebScriptException("Default Web Script implementation '" + (defaultWebScript == null ? "<undefined>" : defaultWebScript) + "' does not exist.");
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Registry#reset()
     */
    public void reset()
    {
        initWebScripts();
        if (logger.isInfoEnabled()) {
            logger.info("Registered " + webscriptsById.size() + " Web Scripts (+" + failedWebScriptsByPath.size() + " failed), " + uriIndex.getSize() + " URLs");
            logger.info("Registered " + packageDocumentByPath.size() + " Package Description Documents (+" + failedPackageDescriptionsByPath.size() + " failed) ");
            logger.info("Registered " + schemaDocumentById.size() + " Schema Description Documents (+" + failedSchemaDescriptionsByPath.size() + " failed) ");
        }    
    }

    /**
     * Initialise Web Scripts
     *
     * Note: Each invocation of this method resets the list of the services
     */
    private void initWebScripts()
    {
        if (logger.isDebugEnabled())
            logger.debug("Initialising Web Scripts (Container: " + container.getName() + ", URI index: " + uriIndex.getClass().getName() + ")");
        
        this.indexResetLock.writeLock().lock();
        try
        {
            // clear currently registered services
            uriIndex.clear();
            uriIndexCache.clear();
            webscriptsById.clear();
            failedWebScriptsByPath.clear();
            packageByPath.clear();
            packageByPath.put("/", new PathImpl("/"));
            uriByPath.clear();
            uriByPath.put("/", new PathImpl("/"));
            familyByPath.clear();
            familyByPath.put("/", new PathImpl("/"));
            lifecycleByPath.clear();
            lifecycleByPath.put("/", new PathImpl("/"));
            failedPackageDescriptionsByPath.clear();
            failedSchemaDescriptionsByPath.clear();
            packageDocumentByPath.clear();
            schemaDocumentById.clear();
    
            for (Store apiStore : searchPath.getStores())
            {
                // Process package description documents.
                if (logger.isDebugEnabled())
                    logger.debug("Locating package descriptions within " + apiStore.getBasePath());
    
                String[] packageDescPaths;
                try
                {
                    packageDescPaths = apiStore.getDocumentPaths("/", true, PackageDescriptionDocument.DESC_NAME_PATTERN);
                }
                catch (IOException e)
                {
                    throw new WebScriptException("Failed to search for package descriptions in store " + apiStore, e);
                }
                for (String packageDescPath : packageDescPaths)
                {
                    try
                    {
                        // build package description
                        PackageDescriptionDocument packageDesc = null;
                        InputStream packageDescIS = null;
                        try
                        {
                            packageDescIS = apiStore.getDocument(packageDescPath);
                            packageDesc = createPackageDescription(apiStore, packageDescPath, packageDescIS);
    
                            String packageDescId = packageDesc.getId();
                            // register the package description document
                            if ( !packageDocumentByPath.containsKey(packageDescId) ) 
                            {
                                packageDocumentByPath.put(packageDescId, packageDesc);
                            }
                        }
                        catch(IOException e)
                        {
                            throw new WebScriptException("Failed to read package description document " + apiStore.getBasePath() + packageDescPath, e);
                        }
                        finally
                        {
                            try
                            {
                                if (packageDescIS != null) packageDescIS.close();
                            }
                            catch(IOException e)
                            {
                                // NOTE: ignore close exception
                            }
                        }
                    }                
                    catch(WebScriptException e)
                    {
                        // record package description document failure
                        String path = apiStore.getBasePath() + "/" + packageDescPath;
                        Throwable c = e;
                        String cause = c.getMessage();
                        while (c.getCause() != null && !c.getCause().equals(c))                    
                        {
                            c = c.getCause();
                            cause += " ; " + c.getMessage(); 
                        }
                        failedPackageDescriptionsByPath.put(path, cause);
                    }
    
                }
    
                // Process schema description documents.
    
                if (logger.isDebugEnabled())
                    logger.debug("Locating schema descriptions within " + apiStore.getBasePath());
    
                String[] schemaDescPaths;
                try
                {
                    schemaDescPaths = apiStore.getDocumentPaths("/", true, SchemaDescriptionDocument.DESC_NAME_PATTERN);
                }
                catch (IOException e)
                {
                    throw new WebScriptException("Failed to search for schema descriptions in store " + apiStore, e);
                }
                for (String schemaDescPath : schemaDescPaths)
                {
                    try
                    {
                        // build schema description
                        SchemaDescriptionDocument schemaDesc = null;
                        InputStream schemaDescIS = null;
                        try
                        {
                            schemaDescIS = apiStore.getDocument(schemaDescPath);
                            schemaDesc = createSchemaDescription(apiStore, schemaDescPath, schemaDescIS);
    
                            String schemaDescId = schemaDesc.getId();
                            // register the schema description document
                            if ( !schemaDocumentById.containsKey(schemaDescId) ) 
                            {
                                schemaDocumentById.put(schemaDescId, schemaDesc);
                            }
                        }
                        catch(IOException e)
                        {
                            throw new WebScriptException("Failed to read Web Script description document " + apiStore.getBasePath() + schemaDescPath, e);
                        }
                        finally
                        {
                            try
                            {
                                if (schemaDescIS != null) schemaDescIS.close();
                            }
                            catch(IOException e)
                            {
                                // NOTE: ignore close exception
                            }
                        }
                    }                
                    catch(WebScriptException e)
                    {
                        // record web script definition failure
                        String path = apiStore.getBasePath() + "/" + schemaDescPath;
                        Throwable c = e;
                        String cause = c.getMessage();
                        while (c.getCause() != null && !c.getCause().equals(c))                    
                        {
                            c = c.getCause();
                            cause += " ; " + c.getMessage(); 
                        }
                        failedSchemaDescriptionsByPath.put(path, cause);
                    }
    
                }
     
                // register services
                if (logger.isDebugEnabled())
                    logger.debug("Locating Web Scripts within " + apiStore.getBasePath());
                
                String[] serviceDescPaths;
                try
                {
                    serviceDescPaths = apiStore.getDescriptionDocumentPaths();
                }
                catch (IOException e)
                {
                    throw new WebScriptException("Failed to search for web scripts in store " + apiStore, e);
                }
                for (String serviceDescPath : serviceDescPaths)
                {
                    try
                    {
                        // build service description
                        DescriptionImpl serviceDesc = null;
                        InputStream serviceDescIS = null;
                        try
                        {
                            serviceDescIS = apiStore.getDocument(serviceDescPath);
                            serviceDesc = createDescription(apiStore, serviceDescPath, serviceDescIS);
                        }
                        catch(IOException e)
                        {
                            throw new WebScriptException("Failed to read Web Script description document " + apiStore.getBasePath() + serviceDescPath, e);
                        }
                        finally
                        {
                            try
                            {
                                if (serviceDescIS != null) serviceDescIS.close();
                            }
                            catch(IOException e)
                            {
                                // NOTE: ignore close exception
                            }
                        }
                        
                        // determine if service description has been registered
                        String id = serviceDesc.getId();
                        if (webscriptsById.containsKey(id))
                        {
                            // move to next service
                            if (logger.isDebugEnabled())
                            {
                                WebScript existingService = webscriptsById.get(id);
                                Description existingDesc = existingService.getDescription();
                                String msg = "Web Script description document " + serviceDesc.getStorePath() + "/" + serviceDesc.getDescPath();
                                msg += " overridden by " + existingDesc.getStorePath() + "/" + existingDesc.getDescPath();
                                logger.debug(msg);
                            }
                            continue;
                        }
                        
                        //
                        // construct service implementation
                        //
                        
                        // establish kind of service implementation
                        ApplicationContext applicationContext = getApplicationContext();
                        String kind = serviceDesc.getKind();
                        String serviceImplName = null;
                        String descImplName = null;
                        if (kind == null)
                        {
                            // rely on default mapping of webscript id to service implementation
                            // NOTE: always fallback to vanilla Declarative Web Script
                            String beanName = "webscript." + id.replace('/', '.');
                            serviceImplName = (applicationContext.containsBean(beanName) ? beanName : defaultWebScript);
                            descImplName = "webscriptdesc." + id.replace('/', '.');
                        }
                        else
                        {
                            // rely on explicitly defined web script kind
                            if (!applicationContext.containsBean("webscript." + kind))
                            {
                                throw new WebScriptException("Web Script kind '" + kind + "' is unknown");
                            }
                            serviceImplName = "webscript." + kind;
                            descImplName = "webscriptdesc." + kind;
                        }
                        
                        // extract service specific description extensions
                        if (applicationContext.containsBean(descImplName) && applicationContext.isTypeMatch(descImplName, DescriptionExtension.class))
                        {
                            DescriptionExtension descriptionExtensions = (DescriptionExtension)applicationContext.getBean(descImplName);
                            serviceDescIS = null;
                            try
                            {
                                serviceDescIS = apiStore.getDocument(serviceDescPath);
                                Map<String, Serializable> extensions = descriptionExtensions.parseExtensions(serviceDescPath, serviceDescIS);
                                serviceDesc.setExtensions(extensions);
                                
                                if (logger.isDebugEnabled())
                                    logger.debug("Extracted " + (extensions == null ? "0" : extensions.size()) + " description extension(s) for Web Script " + id + " (" + extensions + ")");
                            }
                            catch(IOException e)
                            {
                                throw new WebScriptException("Failed to parse extensions from Web Script description document " + apiStore.getBasePath() + serviceDescPath, e);
                            }
                            finally
                            {
                                try
                                {
                                    if (serviceDescIS != null) serviceDescIS.close();
                                }
                                catch(IOException e)
                                {
                                    // NOTE: ignore close exception
                                }
                            }
                        }
                        
                        // retrieve service implementation
                        WebScript serviceImpl = (WebScript)applicationContext.getBean(serviceImplName);
                        serviceImpl.init(container, serviceDesc);
                        
                        if (logger.isDebugEnabled())
                            logger.debug("Found Web Script " + id +  " (desc: " + serviceDescPath + ", impl: " + serviceImplName + ", auth: " + 
                                         serviceDesc.getRequiredAuthentication() + ", trx: " + serviceDesc.getRequiredTransaction() + ", format style: " + 
                                         serviceDesc.getFormatStyle() + ", default format: " + serviceDesc.getDefaultFormat() + ")");
                        
                        // register service and its urls
                        webscriptsById.put(id, serviceImpl);
                        for (String uriTemplate : serviceDesc.getURIs())
                        {
                            uriIndex.registerUri(serviceImpl, uriTemplate);
                            if (logger.isDebugEnabled())
                                logger.debug("Registered Web Script URL '" + serviceImpl.getDescription().getMethod() + ":" + uriTemplate + "'");
                        }
        
                        // build path indexes to web script
                        Path scriptPath = registerPackage(serviceImpl);
                        serviceDesc.setPackage(scriptPath);
                        registerURIs(serviceImpl);
                        registerFamily(serviceImpl);
                        registerLifecycle(serviceImpl);
                    }
                    catch(WebScriptException e)
                    {
                        // record web script definition failure
                        String path = apiStore.getBasePath() + "/" + serviceDescPath;
                        Throwable c = e;
                        String cause = c.getMessage();
                        while (c.getCause() != null && !c.getCause().equals(c))                    
                        {
                            c = c.getCause();
                            cause += " ; " + c.getMessage(); 
                        }
                        failedWebScriptsByPath.put(path, cause);
                    }
                }
            }
        }
        finally
        {
            this.indexResetLock.writeLock().unlock();
        }
        if (logger.isWarnEnabled())
        {
            for (Map.Entry<String, String> failedWebScript : failedWebScriptsByPath.entrySet())
            {
                String msg = "Unable to register script " + failedWebScript.getKey() + " due to error: " + failedWebScript.getValue();
                logger.warn(msg);
            }
            for (Map.Entry<String, String> failedPackageDesription : failedPackageDescriptionsByPath.entrySet())
            {
                String msg = "Unable to register package description document " + failedPackageDesription.getKey() + " due to error: " + failedPackageDesription.getValue();
                logger.warn(msg);
            }
            for (Map.Entry<String, String> failedSchemaDescription : failedSchemaDescriptionsByPath.entrySet())
            {
                String msg = "Unable to register schema description document " + failedSchemaDescription.getKey() + " due to error: " + failedSchemaDescription.getValue();
                logger.warn(msg);
            }
        }
    }

    /**
     * Register a Web Script Package
     * 
     * @param script
     */
    private Path registerPackage(WebScript script)
    {
        Description desc = script.getDescription();
        PathImpl path = packageByPath.get("/");
        String[] parts = desc.getScriptPath().split("/");
        for (String part : parts)
        {
            PathImpl subpath = packageByPath.get(PathImpl.concatPath(path.getPath(), part));
            if (subpath == null)
            {
                subpath = path.createChildPath(part);
                packageByPath.put(subpath.getPath(), subpath);
            }      
            path = subpath;
        }
        path.addScript(script);
        return path;
    }

    /**
     * Register a Web Script Family
     * 
     * @param script
     */
    private void registerFamily(WebScript script)
    {
        Description desc = script.getDescription();
        Set<String> familys = desc.getFamilys();
        for(String family : familys)
        {
        	if (family != null && family.length() > 0)
        	{
        		PathImpl path = familyByPath.get("/");
            	String[] parts = family.split("/");
            	for (String part : parts)
            	{
                	PathImpl subpath = familyByPath.get(PathImpl.concatPath(path.getPath(), part));
                	if (subpath == null)
                	{
                    	subpath = path.createChildPath(part);
                    	familyByPath.put(subpath.getPath(), subpath);
                	}      
                	path = subpath;
            	}
            	path.addScript(script);
        	}
        }
    }
    
    /**
     * Register a lifecycle
     * 
     * @param script
     */
    private void registerLifecycle(WebScript script)
    {
        Description desc = script.getDescription();
        Lifecycle lifecycle = desc.getLifecycle();
       	if (lifecycle != Lifecycle.none)
      	{
            PathImpl path = lifecycleByPath.get("/");
        	PathImpl subpath = lifecycleByPath.get(PathImpl.concatPath(path.getPath(), lifecycle.toString()));
            if (subpath == null)
            {
                 subpath = path.createChildPath(lifecycle.toString());
                 lifecycleByPath.put(subpath.getPath(), subpath);
            }      	
            subpath.addScript(script);
        }
    }

    /**
     * Register a Web Script URI
     * 
     * @param script
     */
    private void registerURIs(WebScript script)
    {
        Description desc = script.getDescription();
        for (String uri : desc.getURIs())
        {
            PathImpl path = uriByPath.get("/");
            String[] parts = uri.split("/");
            for (String part : parts)
            {
                if (part.indexOf("?") != -1)
                {
                    part = part.substring(0, part.indexOf("?"));
                }
                PathImpl subpath = uriByPath.get(PathImpl.concatPath(path.getPath(), part));
                if (subpath == null)
                {
                    subpath = path.createChildPath(part);
                    uriByPath.put(subpath.getPath(), subpath);
                }
                path = subpath;
            }
            path.addScript(script);
        }
    }

    /**
     * Creates a package description document
     * 
     * @param store web script store 
     * @param packageDescPath package description document path
     * @param packageDoc package description document input stream
     * @return
     */
    private PackageDescriptionDocument createPackageDescription(Store store, String packageDescPath, InputStream packageDoc)
    {
        try
        {
            PackageDescriptionDocument packageDesc = new PackageDescriptionDocument();

            // retrieve script path
            int iPathIdx = packageDescPath.lastIndexOf('/');
            String packagePath = packageDescPath.substring(0, iPathIdx == -1 ? 0 : iPathIdx);

            packageDesc.setId("/"+packagePath);
            packageDesc.setDescPath(packageDescPath);
            packageDesc.setPackage(new PathImpl(packagePath));
            packageDesc.setStore(store);

            // parse package description document
            packageDesc.parseDocument(packageDoc);
            
            // make sure the id is setup correctly.
            packageDesc.setId("/"+packagePath);

            return packageDesc;
        }
        catch (DocumentException e)
        {
            throw new WebScriptException("Failed to parse package description document " + packageDescPath, e);
        }
        catch (WebScriptException e)
        {
            throw new WebScriptException("Failed to parse package description document " + packageDescPath, e);
        }
    } 

    /**
     * Creates a schema description document
     * 
     * @param store web script store
     * @param schemaDescPath schema description document path
     * @param schemaDoc schema description document input stream
     * @return
     */
    private SchemaDescriptionDocument createSchemaDescription(Store store, String schemaDescPath, InputStream schemaDoc)
    {
        try
        {
            SchemaDescriptionDocument schemaDesc = new SchemaDescriptionDocument();

            // retrieve script path
            int iPathIdx = schemaDescPath.lastIndexOf('/');
            String schemaName = schemaDescPath.substring(iPathIdx == -1 ? 0 : iPathIdx+1);
            // retrieve schema id
            String id = schemaName.substring(0, schemaName.lastIndexOf(SchemaDescriptionDocument.DESC_NAME_POSTFIX)-1);

            schemaDesc.setId(id);
            schemaDesc.setDescPath(schemaDescPath);
            schemaDesc.setStore(store);

            // parse schema description document
            schemaDesc.parseDocument(schemaDoc);
            
            schemaDesc.setId(id);

            return schemaDesc;
        }
        catch (DocumentException e)
        {
            throw new WebScriptException("Failed to parse schema description document " + schemaDescPath, e);
        }
        catch (WebScriptException e)
        {
            throw new WebScriptException("Failed to parse schema description document " + schemaDescPath, e);
        }
    } 


    /**
     * Creates an Web Script Description
     * 
     * @param store
     * @param serviceDescPath
     * @param serviceDoc
     * 
     * @return  web script service description
     */
	private DescriptionImpl createDescription(Store store, String serviceDescPath, InputStream serviceDoc)
    {
        try
        {
            // retrieve script path
            int iPathIdx = serviceDescPath.lastIndexOf('/');
            String scriptPath = serviceDescPath.substring(0, iPathIdx == -1 ? 0 : iPathIdx);
            
            // retrieve script id
            String id = serviceDescPath.substring(0, serviceDescPath.lastIndexOf(WEBSCRIPT_DESC_XML));
            
            // retrieve http method
            int methodIdx = id.lastIndexOf('.');
            if (methodIdx == -1 || (methodIdx == id.length() - 1))
            {
                throw new WebScriptException("Unable to establish HTTP Method from web script description: naming convention must be <name>.<method>.desc.xml");
            }
            String method = id.substring(methodIdx + 1).toUpperCase();

            // construct service description
            DescriptionImpl serviceDesc = new DescriptionImpl();
            serviceDesc.setStore(store);
            serviceDesc.setScriptPath(scriptPath);
            serviceDesc.setDescPath(serviceDescPath);
            serviceDesc.setId(id);
            serviceDesc.setMethod(method);

            // parse service document
            serviceDesc.parseDocument(serviceDoc);

            // Validate default format
            String defaultFormat = serviceDesc.getDefaultFormat();
            String defaultFormatMimetype = null; 
            if (defaultFormat != null)
            {
                defaultFormatMimetype = container.getFormatRegistry().getMimeType(null, defaultFormat);
                if (defaultFormatMimetype == null)
                {
                    throw new WebScriptException("Default format '" + defaultFormat + "' is unknown");
                }
            }

            // Validate request type reference
            if (serviceDesc.getRequestTypes() != null) 
            { 
                for (TypeDescription requestType:serviceDesc.getRequestTypes()) 
                {
                    if (requestType.getId() != null) 
                    {
                        TypeDescription registedType  = getSchemaTypeDescriptionById(requestType.getId());
                        if (registedType != null) 
                        {
                            // TODO: DC - this should just be a reference rather than a copy
                            requestType.setShortName(registedType.getShortName());
                            requestType.setDescription(registedType.getDescription());
                            requestType.setFormat(registedType.getFormat());
                            requestType.setDefinition(registedType.getDefinition());
                            requestType.setUrl(registedType.getUrl());
                        } 
                        else 
                        {
                            throw new WebScriptException("Invalid schema type reference " + requestType.getId());                        
                        }
                    } 
                }
            }

            //Validate response type reference
            if (serviceDesc.getResponseTypes() != null) 
            {
                for (TypeDescription responseType:serviceDesc.getResponseTypes()) 
                {
                    if (responseType.getId() != null) 
                    {
                        TypeDescription registedType  = getSchemaTypeDescriptionById(responseType.getId());
                        if (registedType != null) 
                        {
                            // TODO: DC - this should just be a reference rather than a copy
                            responseType.setShortName(registedType.getShortName());
                            responseType.setDescription(registedType.getDescription());
                            responseType.setFormat(registedType.getFormat());
                            responseType.setDefinition(registedType.getDefinition());
                            responseType.setUrl(registedType.getUrl());
                        } 
                        else 
                        {
                            throw new WebScriptException("Invalid schema type reference " + responseType.getId());                        
                        }
                    } 
                }
            }
            return serviceDesc;

        }
        catch (DocumentException e)
        {
            throw new WebScriptException("Failed to parse web script description document " + serviceDescPath, e);
        }
        catch (WebScriptException e)
        {
            throw new WebScriptException("Failed to parse web script description document " + serviceDescPath, e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Registry#getPackage(java.lang.String)
     */
    public Path getPackage(String scriptPackage)
    {
        return packageByPath.get(scriptPackage);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Registry#getUri(java.lang.String)
     */
    public Path getUri(String scriptUri)
    {
        return uriByPath.get(scriptUri);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Registry#getFamily(java.lang.String)
     */
    public Path getFamily(String scriptUri)
    {
        return familyByPath.get(scriptUri);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Registry#getWebScripts()
     */
    public Collection<WebScript> getWebScripts()
    {
        return webscriptsById.values();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRegistry#getFailures()
     */
    public Map<String, String> getFailures()
    {
        return Collections.unmodifiableMap(failedWebScriptsByPath);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Registry#getWebScript(java.lang.String)
     */
    public WebScript getWebScript(String id)
    {
        return webscriptsById.get(id);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Registry#findWebScript(java.lang.String, java.lang.String)
     */
    public Match findWebScript(String method, String uri)
    {
        // this is the entry point used to retrieve a webscript description before execution
        // we need to protect the lookup against an index reset with a lock operation
        this.indexResetLock.readLock().lock();
        try
        {
            final String key = new StringBuilder(uri.length() + 5).append(method).append('|').append(uri).toString();
            Match match = uriIndexCache.get(key);
            if (match == null)
            {
                final boolean debug = logger.isDebugEnabled();
                
                long startTime = 0L;
                if (debug) startTime = System.nanoTime();
                
                match = uriIndex.findWebScript(method, uri);
                
                if (debug)
                    logger.debug("Web Script index lookup for uri " + uri + " took " + (System.nanoTime() - startTime)/1000000f + "ms");
                
                if (uriIndexCache.size() < 16384)
                {
                    if (match == null)
                    {
                        match = SENTINEL_MATCH;
                    }
                    uriIndexCache.put(key, match);
                }
                
                if (debug)
                    logger.debug("WebScript uriIndexCache size: " + uriIndexCache.size());
            }
            
            return match != SENTINEL_MATCH ? match : null;
        }
        finally
        {
            this.indexResetLock.readLock().unlock();
        }
    }
    
	public Path getLifecycle(String lifecyclePath) 
	{
		return lifecycleByPath.get(lifecyclePath);
	}

    /**
     * Returns package description document with the given webscript package
     * 
     * @param scriptPackage webscript package
     * @return list of package description document
     */
    public PackageDescriptionDocument getPackageDescriptionDocument(String scriptPackage) 
    {
        return packageDocumentByPath.get(scriptPackage);
    }

    /**
     * Returns schema description document with the given id.
     * 
     * @param schemaId schema id
     * @return schema description document
     */
    public SchemaDescriptionDocument getSchemaDescriptionDocument(String schemaId) 
    {
        return schemaDocumentById.get(schemaId);
    }

    /**
     * Returns list of package description documents
     * 
     * @return list of package description documents
     */
    public Collection<PackageDescriptionDocument> getPackageDescriptionDocuments()
    {
        return packageDocumentByPath.values();
    }

    /**
     * Returns list of schema description documents
     * 
     * @return list of schema description documents
     */
    public Collection<SchemaDescriptionDocument> getSchemaDescriptionDocuments() 
    {
        return schemaDocumentById.values();
    }

    /**
     * Returns schema type description document with given id
     * 
     * @param typeId id for schema description document
     * @return schema type description document
     */
    public TypeDescription getSchemaTypeDescriptionById(String typeId) 
    {
        for (SchemaDescriptionDocument doc:this.getSchemaDescriptionDocuments()) 
        {
            if (typeId.startsWith(doc.getId())) 
            {
                for (TypeDescription type:doc.getTypeDescriptions()) 
                {
                    if (typeId.equals(type.getId())) 
                    {
                        return type;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns list of package description documents that fail to register
     * 
     * @return the failedPackageDescriptionsByPath
     */
    public Map<String, String> getFailedPackageDescriptionsByPath()
    {
        return Collections.unmodifiableMap(failedPackageDescriptionsByPath);
    }

    /**
     * Returns list of schema description documents that fail to register
     * 
     * @return the failedSchemaDescriptionsByPath
     */
    public Map<String, String> getFailedSchemaDescriptionsByPath()
    {
        return Collections.unmodifiableMap(failedSchemaDescriptionsByPath);
    }
    
}
