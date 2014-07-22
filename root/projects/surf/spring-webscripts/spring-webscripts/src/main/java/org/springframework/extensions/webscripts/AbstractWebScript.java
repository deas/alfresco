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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.HandlesExtensibility;
import org.springframework.extensions.surf.extensibility.impl.ModelWriter;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.json.JSONWriter;


/**
 * Abstract implementation of a Web Script
 *
 * @author davidc
 * @author kevinr
 */
public abstract class AbstractWebScript implements WebScript 
{
    // Logger
    private static final Log logger = LogFactory.getLog(AbstractWebScript.class);
    
    // Constants
    private static final String DOT_PROPS = ".properties";
    
    // Dependencies
    private Container container;
    private Description description;
    
    // Service resources
    final private Map<Locale, ResourceBundle> resources = new HashMap<Locale, ResourceBundle>(4);
    final private ReadWriteLock resourcesLock = new ReentrantReadWriteLock();
    final private Map<String, String> jsonResources = new HashMap<String, String>(4);
    final private ReadWriteLock jsonResourcesLock = new ReentrantReadWriteLock();
    
    // Status Template cache
    final private Map<String, StatusTemplate> statusTemplates = new HashMap<String, StatusTemplate>(4);    
    final private ReadWriteLock statusTemplateLock = new ReentrantReadWriteLock(); 
    
    // Script Context
    private String basePath;
    final private Map<String, ScriptDetails> scripts = new HashMap<String, ScriptDetails>(4);
    final private ReadWriteLock scriptLock = new ReentrantReadWriteLock(); 
    
    /**
     * <p>A lock to prevent concurrent modification of the module bundle cache.</p>
     */
    final private ReadWriteLock moduleBundleCacheLock = new ReentrantReadWriteLock();
    
    /**
     * <p>A cache of {@link ResourceBundle} instances mapped against the path where they were found.
     * {@link ModuleBundleSentinel} instances are cached against paths where no bundle could be 
     * found. This map is initialised with an initial size of 5 as it is not expected that there
     * will be typically more many than this many extensions to a WebScript.</p>
     */
    final private Map<String, ResourceBundle> moduleBundleCache = new HashMap<String, ResourceBundle>(5);
    
    // The entry we use to 'remember' nulls in the cache
    final private static ScriptDetails NULLSENTINEL = new ScriptDetails(null, null);
    
    // MessageMessage helper - thread safe single instance
    private MessageMethod messageMethod = null;
    private ScriptMessage scriptMessage = null;
    
    // Script config wrappers - thread safe single instance
    private ScriptConfigModel scriptConfigModel = null;
    private TemplateConfigModel templateConfigModel = null;
    
    private String xmlConfig = null;
    private boolean xmlConfigInitialised = false;
    //
    // Initialisation
    //
    
    /**
     * Initialise a WebScript
     * 
     * @param container     Parent container
     * @param description   Description of the WebScript provided by the registry
     */
    public void init(Container container, Description description)
    {
    	// sanity check to ensure a web script is only registered with a single web script container
    	if (this.container != null && (!this.container.equals(container)))
    	{
    		throw new WebScriptException("WebScript " + description.getId() + " already associated with the '" + this.container.getName() + "' container");
    	}
    	
        this.container = container;
        this.description = description;
        this.basePath = description.getId();
        
        this.statusTemplateLock.writeLock().lock();
        try
        {
            this.statusTemplates.clear();
        }
        finally
        {
            this.statusTemplateLock.writeLock().unlock();
        }
        
        // Clear the module bundle cache...
        this.moduleBundleCacheLock.writeLock().lock();
        try
        {
            this.moduleBundleCache.clear();
        }
        finally
        {
            this.moduleBundleCacheLock.writeLock().unlock();
        }
        
        
        // init the resources for the default locale
        getResources();
        
        // clear scripts to format map
        this.scriptLock.writeLock().lock();
        try
        {
            this.scripts.clear();
        }
        finally
        {
            this.scriptLock.writeLock().unlock();
        }
    }

    /**
     * @return  web script container
     */
    final protected Container getContainer()
    {
        return container;
    }
    
    /**
     * @return the service description
     */
    final public Description getDescription()
    {
        return this.description;
    }
    
    /**
     * <p>Attempts to retrieve a previously cached {@link ResourceBundle} for the supplied
     * path.</p>
     * @param path The path to retrieve a cached bundle for.
     * @return A previously cached bundle or <code>null</code> if it cannot be found.
     */
    protected ResourceBundle checkModuleBundleCache(String path)
    {
        ResourceBundle bundle = null;
        this.moduleBundleCacheLock.readLock().lock();
        try
        {
            bundle = this.moduleBundleCache.get(path);
        }
        finally
        {
            this.moduleBundleCacheLock.readLock().unlock();
        }
        
        return bundle;
    }

    /**
     * <p>Adds a new {@link ResourceBundle} into the cache mapped against the path
     * that the bundle was found.</p>
     * @param path The path to map the {@link ResourceBundle} against.
     * @param bundle The {@link ResourceBundle} to cache.
     */
    protected void addModuleBundleToCache(String path, ResourceBundle bundle)
    {
        this.moduleBundleCacheLock.writeLock().lock();
        try
        {
            this.moduleBundleCache.put(path, bundle);
        }
        finally
        {
            this.moduleBundleCacheLock.writeLock().unlock();
        }
    }
    
    /**
     * <p>Returns a {@link ResourceBundle} containing all the properties defined in the extension modules evaluated
     * for a request. This method will cope with a base bundle having been found or not but the parameters must
     * be supplied accordingly.</p>
     * 
     * @param container This should be an object that implements the {@link HandlesExtensibility} interface.
     * @param result This should be the base provided {@link ResourceBundle} but can be <code>null</code> if 
     * a base {@link ResourceBundle} does not exist.
     * @param bundlePath If the <code>result</code> parameter is <code>null</code> (i.e. if no base {@link ResourceBundle}
     * was found then this needs to be a valid path to attempt to look for in the extensions.</p>
     * @return A {@link ResourceBundle} containing properties merged from all evaluated extension modules.
     */
    private WebScriptPropertyResourceBundle getExtensionBundle(HandlesExtensibility container,
                                                               ResourceBundle result,
                                                               String bundlePath)
    {
        WebScriptPropertyResourceBundle extensionBundle = new WebScriptPropertyResourceBundle(result, bundlePath);
        if (container != null)
        {
            if (result instanceof WebScriptPropertyResourceBundle)
            {
                // If a base ResourceBundle was supplied then use it's resource path (assuming
                // that it is a WebScriptPropertyResourceBundle, which it will always be unless
                // the getBundleFromPath method has been overridden in the class hierarchy.
                bundlePath = ((WebScriptPropertyResourceBundle) result).getResourcePath();
            }
            
            if (bundlePath != null)
            {
                // Get the current WebScript id and generate a new id from the prefix (i.e. the package) and
                // the suffix (i.e. the bit after the package). Build a list of locale paths for this new path
                // to allow for degrading of locale (e.g. from en_GB -> en -> default). 
                String webScriptId = getDescription().getId();
                String suffix = webScriptId.substring(lastSlashIndex(webScriptId));
                String prefix = bundlePath.substring(0, lastSlashIndex(bundlePath));
                LinkedHashSet<String> paths = buildLocalePathList(prefix + suffix, I18NUtil.getLocale());
                
                // Iterate over the different locale paths in REVERSE order so that the most specific locale
                // file is merged into the bundle last. This means that all extension properties files will
                // be applied... this *could* mean a mixture of English with other languages but should ensure
                // that no keys are displayed (unless the messages have genuinely not been provided)...
                Object[] arrayOfPaths = paths.toArray();
                for (int i=arrayOfPaths.length-1; i>=0; i--)
                {
                    String currPath = arrayOfPaths[i].toString();
                    // If the bundle path is not null then iterate over the list of possible files 
                    // that the evaluated modules suggest could provide extensions to the current WebScript...
                    for (String moduleBundlePath: container.getExtendingModuleFiles(currPath))
                    {
                        try
                        {
                            // Check the cache to see if we've previously loaded a bundle for this path...
                            ResourceBundle moduleBundle = checkModuleBundleCache(moduleBundlePath);
                            if (moduleBundle == null)
                            {
                                // If the cache does not contain a bundle mapped against the path then
                                // we know that it hasn't previously been requested (if it had been requested
                                // and couldn't be found then we'd have been returned the sentinel).
                                moduleBundle = getBundleFromPath(moduleBundlePath);
                                if (moduleBundle == null)
                                {
                                    // If a bundle truly doesn't exist for this path (which is a perfectly
                                    // valid situation as modules do not have to provide additional i18n
                                    // properties) then we should cache the sentinel to ensure that we 
                                    // don't needlessly look up the bundle again...
                                    this.addModuleBundleToCache(moduleBundlePath, ModuleBundleSentinel.getInstance());
                                }
                                else
                                {
                                    // If the bundle does exist then add it to the cache...
                                    this.addModuleBundleToCache(moduleBundlePath, moduleBundle);
                                }
                            }
                            
                            // If we've found a module bundle and its NOT the sentinel then we can process it...
                            if (moduleBundle != null && moduleBundle != ModuleBundleSentinel.getInstance())
                            {
                                // If we have a bundle (regardless of whether or not it has been retrieved from the 
                                // cache or whether we have just loaded it) we need to add a record of its use for the
                                // current thread of execution.
                                extensionBundle.merge(moduleBundlePath, moduleBundle);
                            }
                        }
                        catch (IOException e)
                        {
                            if (logger.isDebugEnabled())
                            {
                                logger.error("It was not possible to merge properties from: " + moduleBundlePath, e);
                            }
                        }
                    }
                }
            }
        }
        return extensionBundle;
    }
    
    /**
     * <p>Find the last index of the "/" and return 0 if it does not occur (rather than -1). The purpose of this
     * method is exclusively for use with the String.substring method for finding the unqualified name of a WebScript.</p>
     * @param str
     * @return
     */
    private static int lastSlashIndex(final String str)
    {
        int lastSlashIndex = str.lastIndexOf('/');
        if (lastSlashIndex == -1)
        {
            lastSlashIndex = 0;
        }
        return lastSlashIndex;
    }
    
    /**
     * @return the services resources or null if none present
     */
    final public ResourceBundle getResources()
    {
        ResourceBundle result = null;
        final Locale locale = I18NUtil.getLocale();

        final boolean containsLocaleKey;
        this.resourcesLock.readLock().lock();
        try
        {
            result = this.resources.get(locale);
            containsLocaleKey = this.resources.containsKey(locale);
        }
        finally
        {
            this.resourcesLock.readLock().unlock();
        }

        // The bundle was not found in the cache for the requested locale so we will attempt to 
        // load it now and store it to the cache.
        if (result == null && !containsLocaleKey)
        {
            // If the bundle isn't already in the cache then attempt to load it...
            try
            {
                // Create a list of all the paths to check/merge based on the current locale and WebScript description.
                // We're then going to iterate BACKWARDS through the list so that the most specific locales entries overwrite
                // the least specific.
                Set<String> paths = buildLocalePathList(getDescription(), locale);
                Object[] arrayOfPaths = paths.toArray();
                for (int i=arrayOfPaths.length-1; i>=0; i--)
                {
                    String currPath = arrayOfPaths[i].toString();
                    ResourceBundle currPathResult = getBundleFromPath(currPath);
                    if (currPathResult != null)
                    {
                        if (result == null)
                        {
                            result = currPathResult;
                        }
                        else
                        {
                            // Unless there have been significant overrides to this class then we know that the
                            // ResourceBundle will be an instance of WebScriptPropertyResourceBundle. We are relying
                            // on this being the case so that we can merge multiple properties files.
                            if (result instanceof WebScriptPropertyResourceBundle)
                            {
                                ((WebScriptPropertyResourceBundle) result).merge(currPath, currPathResult); 
                            }
                        }
                    }
                }
                
                // Process imports any imports...
                // This code-block is intentionally performed before any extension modules are processed.
                // In order to achieve optimum caching it has to be accepted that an extension module cannot
                // change any import statements (although it can still override imported properties)...
                if (result != null && result instanceof WebScriptPropertyResourceBundle)
                {
                    try
                    {
                        WebScriptPropertyResourceBundle importedBundles = null;
                        Object includeBundles = result.getString("surf.include.resources");
                        if (includeBundles != null) 
                        {
                            for (String includeBundle: includeBundles.toString().split("\\s*,\\s*")) 
                            {
                                Set<String> importPaths = buildLocalePathList(includeBundle, locale);
                                Object[] arrayOfImportPaths = importPaths.toArray();
                                for (int i=arrayOfImportPaths.length-1; i>=0; i--)
                                {
                                    String currPath = arrayOfImportPaths[i].toString();
                                    WebScriptPropertyResourceBundle currPathResult = (WebScriptPropertyResourceBundle) getBundleFromPath(currPath);
                                    if (currPathResult != null)
                                    {
                                        if (importedBundles == null)
                                        {
                                            importedBundles = currPathResult;
                                        }
                                        // Unless there have been significant overrides to this class then we know that the
                                        // ResourceBundle will be an instance of WebScriptPropertyResourceBundle. We are relying
                                        // on this being the case so that we can merge multiple properties files.
                                        if (importedBundles instanceof WebScriptPropertyResourceBundle)
                                        {
                                            ((WebScriptPropertyResourceBundle) importedBundles).merge(currPath, currPathResult); 
                                        }
                                    }
                                }
                            }
                        }
                        if (importedBundles != null)
                        {
                            ((WebScriptPropertyResourceBundle) result).merge(getDescription().toString(), importedBundles);
//                            importedBundles.merge(getDescription().toString(), result);
//                            result = importedBundles;
                        }
                    }
                    catch (MissingResourceException e)
                    {
                        // No action required
                    }
                    catch (IOException e)
                    {
                        // no resources available if this occurs
                        logger.error(e);
                    }
                }
                
                // Write the results for the requested locale into the cache (even if its null)...
                this.resourcesLock.writeLock().lock();
                try
                {
                    // push the resources into the cache - null value is acceptable if none found
                    this.resources.put(locale, result);
                }
                finally
                {
                    this.resourcesLock.writeLock().unlock();
                }

            }
            catch (IOException resErr)
            {
                // no resources available if this occurs
                logger.error(resErr);
            }
        }
        
        // Apply extension modules if applicable...
        if (container instanceof HandlesExtensibility)
        {
            // If the container handles extensibility then we need to apply matching module extensions to
            // the bundle...
            ResourceBundle extendedBundle = ((HandlesExtensibility) container).getCachedExtendedBundle(this.getDescription().getId());
            if (extendedBundle != null)
            {
                // The container has a previously cached bundle with extension modules applied so we should
                // return that as the result.
                result = extendedBundle;
            }
            else
            {
                // The extension bundles have not previously been applied for this WebScript in the current container so 
                // we need to apply them now and then ask the container to cache them. In reality the container should only
                // be caching for each request, i.e. resource bundle extensions will be needed to be generated per request
                // but ONLY once...
                //
                // PLEASE NOTE: There is no thread locking used here as it is perfectly acceptable for multiple threads to
                //              apply extension modules at the same time. This is because each request can apply different
                //              modules. What is important is that this happens ONLY once.
                WebScriptPropertyResourceBundle extensionBundle = getExtensionBundle((HandlesExtensibility)container, result, this.getDescription().getId());
                ((HandlesExtensibility) container).addExtensionBundleToCache(this.getDescription().getId(), extensionBundle);
                result = extensionBundle;
            }
        }
        
        
        
        return result;
    }
    
    /**
     * <p>A locale based lookup sequence is build using the supplied {@link Locale} and (if it is 
     * different) the default {@link Locale}.
     * <ol><li>Lookup <{@code}descid><{@code}language_country_variant>.properties</li>
     * <li>Lookup <{@code}descid><{@code}language_country>.properties</li>
     * <li>Lookup <{@code}descid><{@code}language>.properties</li>
     * </ol>
     * The repeat but with the default {@link Locale}. Finally lookup <descid>.properties
     * </p>
     * @param description The current {@link WebScript} {@link Description}.
     * @param locale The requested {@link Locale}.
     * @return
     */
    @SuppressWarnings("static-access")
    private LinkedHashSet<String> buildLocalePathList(final String path, final Locale locale)
    {
        final LinkedHashSet<String> pathSet = new LinkedHashSet<String>();
        
        // Add the paths for the current locale...
        pathSet.add(path + '_' + locale.toString() + DOT_PROPS);
        if (locale.getCountry().length() != 0)
        {
            pathSet.add(path + '_' + locale.getLanguage() + '_' + locale.getCountry() + DOT_PROPS);
        }
        pathSet.add(path + '_' + locale.getLanguage() + DOT_PROPS);
        
        if (locale.equals(Locale.getDefault()))
        {
            // We're already using the default Locale, so don't add it's paths again.
        }
        else
        {
            // Use the default locale to add some more possible paths...
            final Locale defLocale = locale.getDefault();
            pathSet.add(path + '_' + defLocale.toString() + DOT_PROPS);
            if (defLocale.getCountry().length() != 0)
            {
                pathSet.add(path + '_' + defLocale.getLanguage() + '_' + defLocale.getCountry() + DOT_PROPS);
            }
            pathSet.add(path + '_' + defLocale.getLanguage() + DOT_PROPS);
        }
        
        // Finally add a path with no locale information...
        pathSet.add(path + DOT_PROPS);
        return pathSet;
    }
    
    /**
     * <p>A locale based lookup sequence is build using the supplied {@link Locale} and (if it is 
     * different) the default {@link Locale}.
     * <ol><li>Lookup <{@code}descid><{@code}language_country_variant>.properties</li>
     * <li>Lookup <{@code}descid><{@code}language_country>.properties</li>
     * <li>Lookup <{@code}descid><{@code}language>.properties</li>
     * </ol>
     * The repeat but with the default {@link Locale}. Finally lookup <descid>.properties
     * </p>
     * @param description The current {@link WebScript} {@link Description}.
     * @param locale The requested {@link Locale}.
     * @return
     */
    private LinkedHashSet<String> buildLocalePathList(Description description, Locale locale)
    {
        LinkedHashSet<String> paths = buildLocalePathList(description.getId(), locale);
        return paths;
    }
    
    /**
     * Helper to retrieve a ResourceBundle wrapper from a store path.
     * 
     * @param path
     * @return ResourceBundle
     * @throws IOException
     */
    private ResourceBundle getBundleFromPath(String path) throws IOException
    {
        ResourceBundle result = null;
        if (container.getSearchPath().hasDocument(path))
        {
            InputStream is = container.getSearchPath().getDocument(path);
            try
            {
                result = new WebScriptPropertyResourceBundle(is, path);
            }
            finally
            {
                is.close();
            }
        }
        return result;
    }

    
    //
    // Scripting Support
    //

    /**
	 * Find execute script for given request format
	 * 
	 * Note: This method caches the script to request format mapping
	 * 
	 * @param mimetype
	 * @return  execute script
	 */
	protected ScriptDetails getExecuteScript(String mimetype)
    {
		ScriptDetails script = null;
		String key = (mimetype == null) ? "<UNKNOWN>" : mimetype;
	    
		this.scriptLock.readLock().lock();
	    try
	    {
	        script = this.scripts.get(key);
	    }
	    finally
	    {
	        this.scriptLock.readLock().unlock();
	    }
        if (script == null)
        {
            FormatRegistry formatRegistry = getContainer().getFormatRegistry();
        	
            // Locate script in web script store
            ScriptContent scriptContent = null;
        	String generalizedMimetype = mimetype;
        	while (generalizedMimetype != null)
        	{
                String format = formatRegistry.getFormat(null, generalizedMimetype);
                if (format != null)
                {
                    String validScriptPath = getContainer().getScriptProcessorRegistry().findValidScriptPath(basePath + "." + format);
                    if (validScriptPath != null)
                    {
                        ScriptProcessor scriptProcessor = getContainer().getScriptProcessorRegistry().getScriptProcessor(validScriptPath);
                        scriptContent = scriptProcessor.findScript(validScriptPath);
                        break;
                    }
                }
                generalizedMimetype = formatRegistry.generalizeMimetype(generalizedMimetype);
        	}
            
            // fall-back to default
			if (scriptContent == null)
			{
			    String validScriptPath = getContainer().getScriptProcessorRegistry().findValidScriptPath(basePath);
			    if (validScriptPath != null)
			    {
                    ScriptProcessor scriptProcessor = getContainer().getScriptProcessorRegistry().getScriptProcessor(validScriptPath);
                    scriptContent = scriptProcessor.findScript(validScriptPath);						        
			    }

				// TODO: Special case. Because multipart form data
                // is parsed for free, we still allow non type
                // specific scripts to see the parsed form data
				generalizedMimetype = Format.FORMDATA.mimetype().equals(mimetype) ? mimetype : null;
			}
            
            if (scriptContent != null)
			{
            	// Validate that there is actually a reader registered to handle this format
            	if (formatRegistry.getReader(generalizedMimetype) == null)
            	{
            		throw new WebScriptException("No reader registered for \"" + generalizedMimetype + "\"");
            	}
				script = new ScriptDetails(scriptContent, generalizedMimetype);
			}
            
            if (logger.isDebugEnabled())
                logger.debug("Caching script " + ((script == null) ? "null" : script.getContent().getPathDescription()) + " for web script " + basePath + " and request mimetype " + ((mimetype == null) ? "null" : mimetype));
            
            this.scriptLock.writeLock().lock();
            try
            {
                this.scripts.put(key, script != null ? script : NULLSENTINEL);
            }
            finally
            {
                this.scriptLock.writeLock().unlock();
            }
        }
        return script != NULLSENTINEL ? script : null;
	}

	/**
     * Create a model for script usage
     *  
     * @param req  web script request
     * @param res  web script response
     * @param script    script details
     * @param customModel  custom model entries
     * 
     * @return  script model
     */
    protected Map<String, Object> createScriptParameters(WebScriptRequest req, WebScriptResponse res, ScriptDetails script, Map<String, Object> customParams)
    {
        Map<String, Object> params = new HashMap<String, Object>(32, 1.0f);
        
        // add web script parameters
        params.put("webscript", req.getServiceMatch().getWebScript().getDescription());
        params.put("format", new FormatModel(container.getFormatRegistry(), req.getFormat()));
        params.put("args", createArgs(req));
        params.put("argsM", createArgsM(req));
        params.put("headers", createHeaders(req));
        params.put("headersM", createHeadersM(req));
        params.put("guest", req.isGuest());
        params.put("url", createURLModel(req));
        params.put("msg", getScriptMessage());
        
        // If there is a request type specific script (e.g. *.json.js), parse
		// the request according to its MIME type and add request specific
		// parameters. Use the FormatReader for the generalised mime type
		// corresponding to the script - not necessarily the request mime type
        final String contentType = req.getContentType();
		if (script != null)
		{
			FormatReader<Object> reader = container.getFormatRegistry().getReader(script.getRequestType());
            if (!(WebScriptRequestImpl.MULTIPART_FORM_DATA.equals(contentType) && getDescription().getMultipartProcessing() == false))
            {
                params.putAll(reader.createScriptParameters(req, res));
            }
		}
        
        // add context & runtime parameters
        params.putAll(req.getRuntime().getScriptParameters());
        params.putAll(container.getScriptParameters());
        
        // add configuration
        setupScriptConfig();
        params.put("config", this.scriptConfigModel);
        
        // add custom parameters
        if (customParams != null)
        {
            params.putAll(customParams);
        }
        return params;
    }
    
    private URLModelFactory urlModelFactory = null;
    
    public void setURLModelFactory(URLModelFactory urlModelFactory)
    {
        this.urlModelFactory = urlModelFactory;
    }
    
    private URLModel createURLModel(WebScriptRequest request)
    {
        URLModel urlModel = null;
        if (this.urlModelFactory == null)
        {
            urlModel = new DefaultURLModel(request);
        }
        else
        {
            urlModel = this.urlModelFactory.createURLModel(request);
        }
        return urlModel;
    }
    
    /**
     * Create a model for template usage
     * 
     * @param req  web script request
     * @param res  web script response
     * @param customModel  custom model entries
     *
     * @return  template model
     */
    protected Map<String, Object> createTemplateParameters(WebScriptRequest req, WebScriptResponse res, Map<String, Object> customParams)
    {
        Map<String, Object> params = new HashMap<String, Object>(64, 1.0f);
        
        // add context & runtime parameters
        params.putAll(req.getRuntime().getTemplateParameters());
        params.putAll(container.getTemplateParameters());
        
        // add web script parameters
        params.put("webscript", req.getServiceMatch().getWebScript().getDescription());
        params.put("format", new FormatModel(container.getFormatRegistry(), req.getFormat()));
        params.put("args", createArgs(req));
        params.put("argsM", createArgsM(req));
        params.put("headers", createHeaders(req));
        params.put("headersM", createHeadersM(req));
        params.put("guest", req.isGuest());
        params.put("url", createURLModel(req));
        
        // populate model with template methods
        params.put("absurl", new AbsoluteUrlMethod(req.getServerPath()));
        
        // urls that point back to this script
        if (params.get("scripturl") == null)
        {
            params.put("scripturl", new ScriptUrlMethod(req, res));
        }
        if (params.get("clienturlfunction") == null)
        {
            params.put("clienturlfunction", new ClientUrlFunctionMethod(res));
        }
        
        // urls that point to resources for this script
        if (params.get("resourceurl") == null)
        {
            params.put("resourceurl", new ResourceUrlMethod(req, res));
        }
        if (params.get("clientresourceurlfunction") == null)
        {
            params.put("clientresourceurlfunction", new ClientResourceUrlFunctionMethod(res));
        }
        
        params.put("formatwrite", new FormatWriterMethod(container.getFormatRegistry(), req.getFormat()));
        params.put("message", getMessageMethod());     // for compatibility with repo templates
        params.put("msg", getMessageMethod());         // short form for presentation webscripts
        params.put("messages", renderJSONResources(getResources()));
        
        // add configuration
        setupScriptConfig();
        params.put("config", this.templateConfigModel);
        
        // add custom parameters
        if (customParams != null)
        {
            params.putAll(customParams);
        }
        return params;
    }
    
    /**
     * Create a map of arguments from Web Script Request (for scripting)
     * 
     * @param req  Web Script Request
     * @return  argument map
     */
    final protected Map<String, String> createArgs(WebScriptRequest req)
    {
        String[] names = req.getParameterNames();
        Map<String, String> args = new HashMap<String, String>(names.length, 1.0f);
        for (String name : names)
        {
            args.put(name, req.getParameter(name));
        }
        return args;
    }

    /**
     * Create a map of (array) arguments from Web Script Request (for scripting)
     * 
     * @param req  Web Script Request
     * @return  argument map
     */
    final protected Map<String, String[]> createArgsM(WebScriptRequest req)
    {
        String[] names = req.getParameterNames();
        Map<String, String[]> args = new HashMap<String, String[]>(names.length, 1.0f);
        for (String name : names)
        {
            args.put(name, req.getParameterValues(name));
        }
        return args;
    }

    /**
     * Create a map of headers from Web Script Request (for scripting)
     * 
     * @param req  Web Script Request
     * @return  header map
     */
    final protected Map<String, String> createHeaders(WebScriptRequest req)
    {
        // NOTE: headers names are case-insensitive according to HTTP Spec
        Map<String, String> headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        String[] names = req.getHeaderNames();
        for (String name : names)
        {
            headers.put(name, req.getHeader(name));
        }
        return headers;
    }

    /**
     * Create a map of (array) headers from Web Script Request (for scripting)
     * 
     * @param req  Web Script Request
     * @return  argument map
     */
    final protected Map<String, String[]> createHeadersM(WebScriptRequest req)
    {
        // NOTE: headers names are case-insensitive according to HTTP Spec
        Map<String, String[]> headers = new TreeMap<String, String[]>(String.CASE_INSENSITIVE_ORDER);
        String[] names = req.getHeaderNames();
        for (String name : names)
        {
            headers.put(name, req.getHeaderValues(name));
        }
        return headers;
    }

    /**
     * Render a template (identified by path)
     * 
     * @param templatePath  template path
     * @param model  model
     * @param writer  output writer
     */
    final protected void renderTemplate(String templatePath, Map<String, Object> model, Writer writer)
    {
        final boolean debug = logger.isDebugEnabled();
        long start = 0L;
        if (debug) start = System.nanoTime();
        
        String validTemplatePath = getContainer().getTemplateProcessorRegistry().findValidTemplatePath(templatePath);
        if (validTemplatePath != null)
        {
            Container container = getContainer();
            if (container instanceof HandlesExtensibility && !((HandlesExtensibility) container).isExtensibilitySuppressed())
            {
                HandlesExtensibility extHandler = (HandlesExtensibility) container;
                // Get the extensibility model from the container, add some unboundContent to handle any output
                // prior to the first extensibility FreeMarker directive and retrieve the model writer...
                ExtensibilityModel extModel = extHandler.getCurrentExtensibilityModel();
                extModel.addUnboundContent();
                ModelWriter extModelWriter = extModel.getWriter();
                
                // Add any custom directives provided by the container...
                extHandler.addExtensibilityDirectives(model, extModel);
                
                // Process the template as normal, but sent the output to the model writer which stores the content 
                // in a buffer so that we can apply the any extending modules to it before flushing the final content
                // to the output stream...
                TemplateProcessor templateProcessor = container.getTemplateProcessorRegistry().getTemplateProcessor(validTemplatePath);
                extHandler.setFileBeingProcessed(validTemplatePath);
                templateProcessor.process(validTemplatePath, model, extModelWriter);
                
                // Switch into extension processing (this will prevent modules writing directly into the model,
                // they will need to use actions to update the model)...
                extModel.switchToExtensionProcessing();
                
                // Get the module handler from the container and retrieve any templates that provide extensions to the
                // the current WebScript...
                for (String moduleTemplatePath: extHandler.getExtendingModuleFiles(templatePath))
                {
                    String modulePath = container.getTemplateProcessorRegistry().findValidTemplatePath(moduleTemplatePath);
                    if (modulePath != null)
                    {
                        extHandler.setFileBeingProcessed(modulePath);
                        templateProcessor.process(modulePath, model, extModelWriter);
                    }
                }
            }
            else
            {
                TemplateProcessor templateProcessor = getContainer().getTemplateProcessorRegistry().getTemplateProcessor(validTemplatePath);
                templateProcessor.process(validTemplatePath, model, writer);
            }
            
            if (debug)
                logger.debug("Rendered template " + templatePath + " in " + (System.nanoTime() - start)/1000000f + "ms");            
        }
        else
        {
            throw new WebScriptException("Cannot locate template processor for template " + templatePath);
        }
    }

    /**
     * Render a template (contents as string)
     *  
     * @param template  the template
     * @param model  model
     * @param writer  output writer
     */
    final protected void renderString(String template, Map<String, Object> model, Writer writer)
    {
        renderString(template, model, writer, "ftl");
    }
    
    /**
     * Render a template (contents as string)
     *  
     * @param template  the template
     * @param model  model
     * @param writer  output writer
     * @param extension optional template extension type (i.e. ftl, php) 
     */
    final protected void renderString(String template, Map<String, Object> model, Writer writer, String extension)
    {        
        TemplateProcessor processor = container.getTemplateProcessorRegistry().getTemplateProcessorByExtension(extension);
        
        if (processor != null)
        {
            processor.processString(template, model, writer);
        }
        else
        {
            throw new WebScriptException("No processor found for extension " + extension);
        }
    }
     
    /**
     * Render an explicit response status template
     * 
     * @param req  web script request
     * @param res  web script response
     * @param status  web script status
     * @param format  format
     * @param model  model
     * @throws IOException
     */
    final protected void sendStatus(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache, String format, Map<String, Object> model)
        throws IOException
    {
        // locate status template
        // NOTE: search order...
        // NOTE: package path is recursed to root package
        //   1) script located <scriptid>.<format>.<status>.ftl
        //   2) script located <scriptid>.<format>.status.ftl
        //   3) package located <scriptpath>/<format>.<status>.ftl
        //   4) package located <scriptpath>/<format>.status.ftl
        //   5) default <status>.ftl
        //   6) default status.ftl

        int statusCode = status.getCode();
        String statusFormat = (format == null) ? "" : format;
        String scriptId = getDescription().getId();
        StatusTemplate template = getStatusTemplate(scriptId, statusCode, statusFormat);

        // render output
        String mimetype = container.getFormatRegistry().getMimeType(req.getAgent(), template.getFormat());
        if (mimetype == null)
        {
            throw new WebScriptException("Web Script format '" + template.getFormat() + "' is not registered");
        }
    
        if (logger.isDebugEnabled())
        {
            logger.debug("Force success status header in response: " + req.forceSuccessStatus());
            logger.debug("Sending status " + statusCode + " (Template: " + template.getPath() + ")");
            logger.debug("Rendering response: content type=" + mimetype);
        }
    
        res.reset();
        res.setCache(cache);
        res.setStatus(req.forceSuccessStatus() ? HttpServletResponse.SC_OK : statusCode);
        String location = status.getLocation();
        if (location != null && location.length() > 0)
        {
            if (logger.isDebugEnabled())
                logger.debug("Setting location to " + location);
            res.setHeader(WebScriptResponse.HEADER_LOCATION, location);
        }
        res.setContentType(mimetype + ";charset=UTF-8");
        renderTemplate(template.getPath(), model, res.getWriter());
    }

    /**
     * Create an exception whose associated message is driven from a status template and model
     * 
     * @param e  exception
     * @param req  web script request
     * @param res  web script response
     * @return  web script exception with associated template message and model
     */
    @SuppressWarnings("unchecked")
    final protected WebScriptException createStatusException(Throwable e, final WebScriptRequest req, final WebScriptResponse res)
    {
        // Unwrap the exception in case there is an underlying WebScriptException
        // Look for JavaScriptException specifically thrown by a JS implementation
        Throwable current = e;
        do
        {
            if (current instanceof WebScriptException)
            {
                break;
            }
            else if (current instanceof WrappedException)
            {
                current = ((WrappedException)current).getWrappedException();
            }
            else if (current instanceof JavaScriptException)
            {
                // extract "code" and "message" properties from JavaScript error object
                int code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                String msg = "Wrapped Exception (with status template): " + current.getMessage();
                Object val = ((JavaScriptException)current).getValue();
                if (val instanceof ScriptableObject)
                {
                    // found an error object - look for code and message properties
                    Object unwrapped = ScriptValueConverter.unwrapValue(val);
                    if (unwrapped instanceof Map)
                    {
                        Number numCode = (Number)((Map<String, Object>)unwrapped).get("code");
                        if (numCode != null)
                        {
                            code = numCode.intValue();
                        }
                        String strMsg = (String)((Map<String, Object>)unwrapped).get("message");
                        if (strMsg != null)
                        {
                            msg = strMsg;
                        }
                        // See http://www.mozilla.org/rhino/ScriptingJava.html#Exceptions
                        // A caught / rethrown error object has the Java exception in a javaException property
                        Object cause = ((Map<String, Object>)unwrapped).get("javaException");
                        if (cause instanceof Throwable)
                        {
                            current = (Throwable)cause;
                        }
                    }
                }
                current = new WebScriptException(code, msg, current);
                break;
            }
            else
            {
                current = current.getCause();
            }
        }
        while (current != null);
        final WebScriptException we;
        
        // decorate exception with template message
        if (current == null)
        {
            we = new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Wrapped Exception (with status template): " + e.getMessage(), e);
        }
        else
        {
            we = (WebScriptException)current;
        }       
        
        // find status template and construct model for it
        we.setStatusTemplateFactory(new StatusTemplateFactory()
        {
            public Map<String, Object> getStatusModel()
            {
                return createTemplateParameters(req, res, null);
            }

            public StatusTemplate getStatusTemplate()
            {
                int statusCode = we.getStatus();
                String format = req.getFormat();
                String scriptId = getDescription().getId();
                return AbstractWebScript.this.getStatusTemplate(scriptId, statusCode, (format == null) ? "" : format);
            }
        });
        
        return we;
    }
    
    /**
     * Find status template
     * 
     * Note: This method caches template search results
     * 
     * @param scriptId
     * @param statusCode
     * @param format
     * @return  status template (or null if not found)
     */
    protected StatusTemplate getStatusTemplate(String scriptId, int statusCode, String format)
    {
        StatusTemplate statusTemplate = null;
        final String key = statusCode + "." + format;
        
        this.statusTemplateLock.readLock().lock();
        try
        {
            statusTemplate = this.statusTemplates.get(key);
        }
        finally
        {
            this.statusTemplateLock.readLock().unlock();
        }
        if (statusTemplate == null)
        {
            // Locate template in web script store
            statusTemplate = getScriptStatusTemplate(scriptId, statusCode, format);
            if (statusTemplate == null)
            {
                Path path = this.container.getRegistry().getPackage(PathImpl.concatPath("/", getDescription().getScriptPath()));
                statusTemplate = getPackageStatusTemplate(path, statusCode, format);
                if (statusTemplate == null)
                {
                    statusTemplate = getDefaultStatusTemplate(statusCode);
                }
            }
            
            if (logger.isDebugEnabled())
                logger.debug("Caching template " + statusTemplate.getPath() + " for web script " + scriptId +
                             " and status " +statusCode + " (format: " + format + ")");
            
            this.statusTemplateLock.writeLock().lock();
            try
            {
                this.statusTemplates.put(key, statusTemplate);
            }
            finally
            {
                this.statusTemplateLock.writeLock().unlock();
            }
        }
        return statusTemplate;
    }
    
    /**
     * Find a script specific status template
     * 
     * @param scriptId
     * @param statusCode
     * @param format
     * @return  status template (or null, if not found)
     */
    private StatusTemplate getScriptStatusTemplate(String scriptId, int statusCode, String format)
    {
        // look up status code specific status template
        String validTemplatePath = getContainer().getTemplateProcessorRegistry().findValidTemplatePath(scriptId + "." + format + "." + statusCode);
        if (validTemplatePath != null)
        {
            return new StatusTemplate(scriptId + "." + format + "." + statusCode, format);
        }
        
        // look up general case status template
        validTemplatePath = getContainer().getTemplateProcessorRegistry().findValidTemplatePath(scriptId + "." + format + ".status");
        if (validTemplatePath != null)
        {
            return new StatusTemplate(scriptId + "." + format + ".status", format);
        }
        return null;
    }

    /**
     * Find a package specific status template
     * 
     * @param scriptPath
     * @param statusCode
     * @param format
     * @return  status template (or null, if not found)
     */
    private StatusTemplate getPackageStatusTemplate(Path scriptPath, int statusCode, String format)
    {
        while(scriptPath != null)
        {
            String path = PathImpl.concatPath(scriptPath.getPath(), format + "." + statusCode + ".ftl");
            String validTemplatePath = container.getTemplateProcessorRegistry().findValidTemplatePath(path);
            if (validTemplatePath != null)
            {
                return new StatusTemplate(path, format);
            }
            
            path = PathImpl.concatPath(scriptPath.getPath(), format + ".status.ftl");
            validTemplatePath = container.getTemplateProcessorRegistry().findValidTemplatePath(path);
            if (validTemplatePath != null)
            {
                return new StatusTemplate(path, format);
            }
            scriptPath = scriptPath.getParent();
        }
        return null;
    }
    
    /**
     * Find default status template
     * 
     * @param statusCode
     * @return  status template
     */
    private StatusTemplate getDefaultStatusTemplate(int statusCode)
    {
        String path = statusCode + ".ftl";
        String validTemplatePath = container.getTemplateProcessorRegistry().findValidTemplatePath(path);
        if (validTemplatePath != null)
        {
            return new StatusTemplate(path, WebScriptResponse.HTML_FORMAT);
        }
        path = "status.ftl";
        validTemplatePath = container.getTemplateProcessorRegistry().findValidTemplatePath(path);
        if (validTemplatePath != null)
        {
            return new StatusTemplate(path, WebScriptResponse.HTML_FORMAT);
        }
        throw new WebScriptException("Default status template /status.ftl could not be found");
    }
    
    /**
     * Looks for the script's config file and reads it's contents
     * if present. The result is the XML config stored in the
     * <code>xmlConfig</code> member variable.
     */
    private synchronized void setupScriptConfig()
    {
        if (!this.xmlConfigInitialised)
        {
            InputStream input = null;
            try
            {
                // Look for script's config file
                String configPath = getDescription().getId() + ".config.xml";
                input = this.container.getSearchPath().getDocument(configPath);
                if (input != null)
                {
                    // if config file found, read contents into buffer
                    StringBuilder fileContents = new StringBuilder(1024);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"), 1024);
                    char[] buf = new char[1024];
                    int read;
                    while ((read=reader.read(buf)) != -1)
                    {
                        fileContents.append(buf, 0, read);
                    }
                    
                    this.xmlConfig = fileContents.toString();
                }
            }
            catch (IOException ioe)
            {
                throw new WebScriptException("Failed to read script configuration file", ioe);
            }
            finally
            {
                // Indicate that we at least attempted to retrieve the XML configuration (even if an exception
                // was thrown in the process) so that we don't try again...
                this.xmlConfigInitialised = true;
            }
        }
        
        // Check to see if the container handles extensibility and if it does then attempt to create the Script and Template
        // configuration models using it. These versions will contain the default configuration plus additional configuration 
        // that is dynamically provided by modules evaluated to be applied to the current invocation of this WebScript
        if (this.container instanceof HandlesExtensibility)
        {
            this.scriptConfigModel = ((HandlesExtensibility) this.container).getExtendedScriptConfigModel(this.xmlConfig);
            this.templateConfigModel = ((HandlesExtensibility) this.container).getExtendedTemplateConfigModel(this.xmlConfig);
        }
        
        // Check to see if the Script and Template configuration models were set up and if not, create them from the default
        // configuration (i.e. without any modules applied)...
        if (this.scriptConfigModel == null)
        {
            this.scriptConfigModel = new ScriptConfigModel(this.container.getConfigService(), this.xmlConfig);
        }
        if (this.templateConfigModel == null)
        {
            this.templateConfigModel = new TemplateConfigModel(this.container.getConfigService(), this.xmlConfig);
        }
    }
        
    /**
     * Execute a script
     * 
     * @param location  script location
     * @param model  model
     */
    protected void executeScript(ScriptContent location, Map<String, Object> model)
    {
        final boolean debug = logger.isDebugEnabled();
        long start = 0L;
        if (debug) start = System.nanoTime();
        
        ScriptProcessor scriptProcessor = container.getScriptProcessorRegistry().getScriptProcessor(location);
        scriptProcessor.executeScript(location, model);
        
        Container container = getContainer();
        if (container instanceof HandlesExtensibility)
        {
            // Get the module handler from the container and retrieve any scripts that provide extensions to the
            // the current WebScript...
            for (String moduleScriptPath: ((HandlesExtensibility) container).getExtendingModuleFiles(this.basePath))
            {
                String validScriptPath = getContainer().getScriptProcessorRegistry().findValidScriptPath(moduleScriptPath);
                if (validScriptPath != null)
                {
                    scriptProcessor = getContainer().getScriptProcessorRegistry().getScriptProcessor(validScriptPath);
                    ScriptContent scriptContent = scriptProcessor.findScript(validScriptPath);
                    scriptProcessor.executeScript(scriptContent, model);
                }
            }
        }
        
        if (debug)
            logger.debug("Executed script " + location.getPathDescription() + " in " + (System.nanoTime() - start)/1000000f + "ms");
    }
    
    /**
     * Helper to render a bundle of webscript I18N resources as a JSON object
     * 
     * @param resources     To render - can be null if no resources present,
     *                      in which case an empty JSON object will be output.
     * 
     * @return JSON object string
     */
    private String renderJSONResources(ResourceBundle resources)
    {
        String result = "{}";
        if (resources != null)
        {
            final Locale locale = I18NUtil.getLocale();

            String cacheKey = locale.toString();
            if (resources instanceof WebScriptPropertyResourceBundle)
            {
                // Add a String of all the additional paths merged into the bundle to ensure that we do not 
                // retrieve stale cached data. It is important that we always return bundle data that is 
                // specific to the request as each request might result in different modules being applied
                // and therefore different bundles being merged together...
                cacheKey = cacheKey + "_" + ((WebScriptPropertyResourceBundle) resources).getMergedBundlePaths();
            }

            this.jsonResourcesLock.readLock().lock();
            try
            {
                result = jsonResources.get(cacheKey);
            }
            finally
            {
                this.jsonResourcesLock.readLock().unlock();
            }
            if (result == null)
            {
                StringBuilderWriter buf = new StringBuilderWriter(256);
                JSONWriter out = new JSONWriter(buf);
                try
                {
                    out.startObject();
                    Enumeration<String> keys = resources.getKeys();
                    while (keys.hasMoreElements())
                    {
                        String key = keys.nextElement();
                        out.writeValue(key, resources.getString(key));
                    }
                    out.endObject();
                }
                catch (IOException jsonErr)
                {
                    throw new WebScriptException("Error rendering I18N resources.", jsonErr);
                }
                result = buf.toString();
                
                this.jsonResourcesLock.writeLock().lock();
                try
                {
                    this.jsonResources.put(cacheKey, result);
                }
                finally
                {
                    this.jsonResourcesLock.writeLock().unlock();
                }
            }
        }
        
        return result;
    }
    
    /**
     * @return the MessageMethod instance for this WebScript
     */
    private MessageMethod getMessageMethod()
    {
        if (this.messageMethod == null)
        {
            this.messageMethod = new MessageMethod(this);
        }
        return this.messageMethod;
    }
    
    /**
     * @return the ScriptMessage instance for this WebScript
     */
    private ScriptMessage getScriptMessage()
    {
        if (this.scriptMessage == null)
        {
            this.scriptMessage = new ScriptMessage(this);
        }
        return this.scriptMessage;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.basePath;
    }


    /**
     * The combination of a ScriptContent and a request MIME type. Records the
     * most specific request MIME type expected by a script (according to its
     * naming convention, e.g. *.json.js or *.js). Used to determine what kind
     * of parsing should be done on the request (i.e. what kind of FormatReader
     * should be invoked to get extra script parameters).
     */    
    protected static class ScriptDetails
    {
        private final ScriptContent content;
        private final String requestType;
        
        private ScriptDetails(ScriptContent content, String requestType)
        {
            this.content = content;
            this.requestType = requestType;
        }
        
        /**
         * @return the content
         */
        public ScriptContent getContent()
        {
            return content;
        }
        
        /**
         * @return the requestType
         */
        public String getRequestType()
        {
            return requestType;
        }
    }
}
